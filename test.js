/* Local test: starts a fake Anthropic upstream and the helper, then exercises the API. */
const http = require('http');
process.env.ANTHROPIC_API_KEY = 'test-key';
process.env.ANTHROPIC_BASE_URL = 'http://127.0.0.1:9099';
process.env.APP_TOKEN = 'secret';
process.env.RATE_MAX = '8';
const { server, parseModelJson } = require('./server.js');

let mode = 'good', lastReq = null;
const upstream = http.createServer((req, res) => {
  let b = ''; req.on('data', c => b += c); req.on('end', () => {
    lastReq = { headers: req.headers, body: JSON.parse(b) };
    if (mode === 'down') { res.writeHead(500); return res.end('x'); }
    let text = '{"ingredients":["şeker","jelatin (sığır)","E471"],"language":"tr","confidence":"high","notes":""}';
    if (mode === 'fenced') text = '```json\n' + text + '\n```';
    if (mode === 'garbage') text = 'Sorry I cannot help';
    res.writeHead(200, { 'content-type': 'application/json' });
    res.end(JSON.stringify({ content: [{ type: 'text', text }] }));
  });
});

function call(path, method, body, headers) {
  return new Promise(resolve => {
    const r = http.request({ host: '127.0.0.1', port: 8788, path, method, headers: Object.assign({ 'content-type': 'application/json' }, headers || {}) }, res => {
      let d = ''; res.on('data', c => d += c); res.on('end', () => resolve({ code: res.statusCode, body: d ? JSON.parse(d) : null }));
    });
    if (body !== undefined) r.write(typeof body === 'string' ? body : JSON.stringify(body));
    r.end();
  });
}
let pass = 0, total = 0;
function ok(name, cond, extra) { total++; if (cond) pass++; console.log((cond ? 'OK   ' : 'FAIL ') + name + (cond ? '' : ' ' + JSON.stringify(extra))); }

(async () => {
  await new Promise(r => upstream.listen(9099, r));
  await new Promise(r => server.listen(8788, r));
  const T = { 'x-app-token': 'secret' };

  let r = await call('/health', 'GET');
  ok('health', r.code === 200 && r.body.ok === true, r);

  r = await call('/api/clean', 'POST', { text: 'sekcr, jela-\ntin (sigir), E47l' });
  ok('missing token -> 401', r.code === 401, r);

  mode = 'good';
  r = await call('/api/clean', 'POST', { text: 'sekcr, jela-\ntin (sigir), E47l' }, T);
  ok('valid request -> ingredients', r.code === 200 && r.body.ingredients.length === 3 && r.body.confidence === 'high', r);
  ok('upstream got key+version+model', lastReq.headers['x-api-key'] === 'test-key' && lastReq.headers['anthropic-version'] === '2023-06-01' && lastReq.body.model.startsWith('claude-'), lastReq && lastReq.headers);
  ok('untrusted text is wrapped in <ocr_text>', lastReq.body.messages[0].content.startsWith('<ocr_text>'), lastReq.body.messages);
  ok('system prompt forbids rulings', /Do NOT judge whether anything is halal/.test(lastReq.body.system), 1);

  mode = 'fenced';
  r = await call('/api/clean', 'POST', { text: 'abc' }, T);
  ok('code-fenced JSON tolerated', r.code === 200 && r.body.ingredients[0] === 'şeker', r);

  mode = 'garbage';
  r = await call('/api/clean', 'POST', { text: 'abc' }, T);
  ok('garbage model reply -> 502 not crash', r.code === 502, r);

  mode = 'down';
  r = await call('/api/clean', 'POST', { text: 'abc' }, T);
  ok('upstream 500 -> 502', r.code === 502, r);

  mode = 'good';
  r = await call('/api/clean', 'POST', { text: '' }, T);
  ok('empty text -> 400', r.code === 400, r);
  r = await call('/api/clean', 'POST', '{not json', T);
  ok('bad json -> 400', r.code === 400, r);
  r = await call('/api/clean', 'POST', { text: 'x'.repeat(5000) }, T);
  ok('long text -> 413', r.code === 413, r);
  r = await call('/nope', 'GET');
  ok('unknown path -> 404', r.code === 404, r);
  r = await call('/api/clean', 'OPTIONS');
  ok('CORS preflight -> 204', r.code === 204, r);

  // rate limit: RATE_MAX=8 so further authorised calls must eventually 429
  let got429 = false;
  for (let i = 0; i < 12; i++) { const x = await call('/api/clean', 'POST', { text: 'abc' }, T); if (x.code === 429) got429 = true; }
  ok('rate limit triggers 429', got429, 0);

  const p = parseModelJson('{"ingredients":[" a  b ",5,"' + 'z'.repeat(300) + '"],"confidence":"weird"}');
  ok('sanitises items / clamps length / default confidence', p.ingredients.length === 2 && p.ingredients[0] === 'a b' && p.ingredients[1].length === 120 && p.confidence === 'medium', p);

  console.log('---\n' + pass + '/' + total + ' passed');
  server.close(); upstream.close();
  process.exit(pass === total ? 0 : 1);
})();
