const fs=require('fs');
const path=require('path');
const base=path.join(__dirname,'..','app','src','main','assets');
new Function(fs.readFileSync(path.join(base,'rules.js'),'utf8')+fs.readFileSync(path.join(base,'engine.js'),'utf8')+'\nglobalThis.RULES=RULES;globalThis.RULES_VERSION=RULES_VERSION;globalThis.analyzeIngredients=analyzeIngredients;')();
function check(name, text, expectVerdict, expectIds){
  const r = analyzeIngredients(text);
  const ids = r.findings.map(f=>f.id).sort();
  const ok = r.verdict===expectVerdict && JSON.stringify(ids)===JSON.stringify(expectIds.sort());
  console.log((ok?'OK  ':'FAIL')+' '+name+' -> verdict='+r.verdict+' ids='+JSON.stringify(ids));
  if(!ok) console.log('    expected verdict='+expectVerdict+' ids='+JSON.stringify(expectIds));
  return ok;
}
let pass=0,total=0;
function t(...a){total++; if(check(...a))pass++;}

// original 16 tests (must still pass after expansion)
t('plain sugar/water', 'sugar, water, citric acid', 'halal', []);
t('pork explicit', 'water, pork fat, salt', 'haram', ['pork']);
t('porcine gelatin', 'gelatin (porcine gelatin), sugar', 'haram', ['pork-gelatin','gelatin','pork']);
t('gelatin unspecified', 'sugar, gelatin, citric acid', 'uncertain', ['gelatin']);
t('animal-fat', 'water, animal fat, salt', 'uncertain', ['animal-fat']);
t('beer word-boundary', 'root beer extract, sugar', 'haram', ['ethanol-beverage']);
t('wineberry false positive avoided', 'wineberry flavoring, sugar', 'halal', []);
t('carrageenan not flagged', 'water, carrageenan, sugar', 'halal', []);
t('e471 code boundary', 'emulsifier E471, water', 'uncertain', ['e471']);
t('e4711 no false match', 'contains E4711X compound', 'halal', []);
t('empty text -> unknown', '', 'unknown', []);
t('carmine/cochineal', 'color: cochineal extract', 'uncertain', ['carmine']);
t('l-cysteine e920', 'flour treatment agent E920', 'uncertain', ['l-cysteine']);
t('multiple triggers -> haram wins over uncertain', 'gelatin, pork, e471', 'haram', ['pork','gelatin','e471']);
t('vinegar not flagged (only wine vinegar flagged)', 'malt vinegar, salt', 'halal', []);
t('wine vinegar flagged uncertain', 'wine vinegar, salt', 'uncertain', ['alcohol']);
t('turkish domuz', 'un, domuz yağı, tuz', 'haram', ['pork']);

// new tests for expanded entries
t('shellac glazing agent', 'glazing agent (shellac)', 'uncertain', ['shellac']);
t('e904 code', 'contains E904', 'uncertain', ['shellac']);
t('chicken broth', 'water, chicken broth, salt', 'uncertain', ['broth','meat']);
t('whey powder', 'milk, whey powder, sugar', 'uncertain', ['whey']);
t('polysorbate 80', 'polysorbate 80, water', 'uncertain', ['polysorbate']);
t('e433 code boundary', 'emulsifier E433', 'uncertain', ['polysorbate']);
t('turkish bulyon', 'su, tavuk bulyonu, tuz', 'uncertain', ['broth','meat']);
t('commonly ok plant list not flagged', 'sunflower oil, xanthan gum, pectin, citric acid', 'halal', []);
t('isinglass', 'clarified with isinglass', 'uncertain', ['isinglass']);
t('lipase animal', 'lipase (animal), milk', 'uncertain', ['lipase']);

console.log('---');
console.log(pass+'/'+total+' tests passed');
process.exit(pass===total?0:1);
