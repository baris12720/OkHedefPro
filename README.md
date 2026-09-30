# HelalGuard AI Helper (backend)

Tiny, dependency-free Node.js server. Its only job is to clean up text that the
phone's on-device OCR read from a product label (fix broken words, drop
unrelated text like nutrition tables) and return a clean ingredient list.

**It never decides halal/haram.** That verdict always comes from the app's
own rule engine (`assets/rules.js` + `assets/engine.js`), which ships inside
the APK and works fully offline. The AI helper is optional — if you never
set a server address in the app's Settings screen, the app simply uses the
raw on-device OCR text and everything still works.

## Run it

```bash
cd backend
ANTHROPIC_API_KEY=sk-ant-... \
APP_TOKEN=pick-a-long-random-string \
node server.js
```

Environment variables:

| Variable | Required | Default | Purpose |
|---|---|---|---|
| `ANTHROPIC_API_KEY` | yes | — | Your Claude API key. Never put this in the app — it stays on the server. |
| `APP_TOKEN` | recommended | (none) | A secret the app sends as `x-app-token`. Without it, anyone who finds your server URL can use your API key's quota. |
| `MODEL` | no | `claude-haiku-4-5-20251001` | Cheap and fast is enough for this task. |
| `ALLOWED_ORIGIN` | no | `*` | CORS origin. |
| `RATE_MAX` | no | `30` | Max requests per IP per 10-minute window. |
| `PORT` | no | `8787` | Listen port. |

## Deploy it

Any place that runs Node 18+ works (Render, Railway, Fly.io, a small VPS,
etc.). Put it behind HTTPS — the app calls it directly from a live phone.

**Important:** the app's Settings screen expects a plain **public** server
address (e.g. `https://helalguard-ai.onrender.com`), not `localhost` or a
private/LAN IP — modern mobile browsers block a public page from silently
reaching into a private network, so a local test server needs to send the
`Access-Control-Allow-Private-Network: true` header on its `OPTIONS`
response (this server already does).

## What the app sends and gets back

Request:
```
POST /api/clean
x-app-token: <your APP_TOKEN>
{"text": "<raw OCR text from the photo>"}
```

Response:
```
{"ingredients": ["şeker","jelatin (sığır)","E471"], "language":"tr", "confidence":"high", "notes":""}
```

If the server is unreachable, misconfigured, or returns an error, the app
falls back to the raw OCR text automatically — the person can still get a
result, just without the cleanup pass.

## Tests

```bash
cd backend
node test.js
```
