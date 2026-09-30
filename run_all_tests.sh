#!/usr/bin/env bash
set -e
cd "$(dirname "$0")/.."
echo "== rule engine tests (27 cases) =="
node tools/test_rules.js
echo "== backend tests =="
(cd backend && node test.js)
