function escapeRe(s){return s.replace(/[.*+?^${}()|[\]\\]/g,'\\$&')}
function patternRegex(p){
  const esc = escapeRe(p.toLowerCase());
  const startsWord = /^[a-z0-9]/i.test(p[0]);
  const endsWord = /[a-z0-9]$/i.test(p[p.length-1]);
  return new RegExp((startsWord?'\\b':'')+esc+(endsWord?'\\b':''), 'i');
}
function analyzeIngredients(text){
  const t = (text||'').toLowerCase();
  const findings = [];
  const seen = new Set();
  for(const rule of RULES){
    for(const p of rule.patterns){
      if(patternRegex(p).test(t)){
        if(!seen.has(rule.id)){
          seen.add(rule.id);
          findings.push({id:rule.id, matched:p, status:rule.status, reason:rule.reason, cat:rule.cat});
        }
        break;
      }
    }
  }
  let verdict = 'halal';
  if(findings.some(f=>f.status==='haram')) verdict='haram';
  else if(findings.some(f=>f.status==='uncertain')) verdict='uncertain';
  else if(!text || !text.trim()) verdict='unknown';
  return {verdict, findings, rulesVersion: RULES_VERSION, checkedAt: Date.now()};
}
