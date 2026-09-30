'use strict';
/* ---------------- i18n ---------------- */
const I18N = {
 tr:{
  home_title:'İçindekiler Analizi', home_lead:"Ürün etiketindeki içindekiler listesini yapıştır, olası uyarıları gör.",
  home_placeholder:'Örn: şeker, su, jelatin, E471, tuz...', home_analyze:'Analiz Et',
  disclaimer_short:"Bu sonuçlar bilgilendirme amaçlıdır, dini bir fetva değildir. Kesin karar için sertifikalı helal kurumlarına veya bir alime danışın.",
  barcode_title:'Barkod Sorgula', barcode_lead:'Ürün barkod numarasını gir, Open Food Facts veritabanından içindekiler otomatik getirilsin.',
  barcode_placeholder:'Örn: 8690504041202', barcode_lookup:'Sorgula',
  barcode_note:"Barkod sorgusu internet bağlantısı gerektirir. Ürün veritabanında yoksa içindekileri Ana Sayfa'dan elle girebilirsin.",
  encyc_title:'Bileşen Rehberi', encyc_lead:'Sık karşılaşılan bileşenler ve neden işaretlendikleri.',
  encyc_search:'Bileşen ara...', encyc_ok_title:'Genelde Sorunsuz Kabul Edilenler',
  history_title:'Geçmiş', history_lead:'Daha önce yaptığın kontroller burada listelenir.',
  settings_title:'Ayarlar', settings_lang:'Uygulama Dili', settings_lang_sub:'Türkçe / English',
  settings_profile:'Fıkıh Profili', settings_rulesver:'Kural Seti Sürümü',
  settings_clear:'Geçmişi Temizle', settings_clear_btn:'Temizle', settings_about:'Hakkında',
  about_p1:'HelalGuard, ürün içindekiler listesindeki yaygın olarak tartışılan bileşenleri saydam kurallarla işaretleyen bilgilendirme aracıdır.',
  about_p2:'Uygulama "haram" ve "belirsiz/şüpheli" ayrımını her zaman korur; metnin desteklemediği bir hükmü asla uydurmaz.',
  about_p3:'Bu uygulama dini bir otorite değildir. Kesin ve bağlayıcı hüküm için lütfen sertifikalı bir helal belgelendirme kurumuna veya güvendiğiniz bir alime danışın.',
  tab_home:'Ana Sayfa', tab_barcode:'Barkod', tab_encyc:'Rehber', tab_history:'Geçmiş', tab_settings:'Ayarlar',
  v_halal:'HELAL', v_halal_sub:'Sorunlu bileşen bulunamadı', v_haram:'HELAL DEĞİL', v_haram_sub:'Açıkça yasak bileşen tespit edildi',
  v_uncertain:'ŞÜPHELİ', v_uncertain_sub:'Kaynağı belirsiz bileşen(ler) var, kontrol gerekir', v_unknown:'VERİ YETERSİZ', v_unknown_sub:'Metin girilmedi',
  matched:'Eşleşen ifade', all_cat:'Tümü', empty_enc:'Sonuç bulunamadı', empty_hist:'Henüz geçmiş yok', empty_hist_sub:'Bir ürün analiz ettiğinde burada görünecek.',
  toast_saved:'Kaydedildi', toast_cleared:'Geçmiş temizlendi', toast_empty:'Lütfen içindekiler girin', toast_barcode_empty:'Lütfen bir barkod numarası girin',
  looking_up:'Ürün aranıyor…', not_found:'Ürün veritabanında bulunamadı. İçindekileri Ana Sayfa\u2019dan elle girebilirsin.',
  network_err:'Bağlantı kurulamadı. İnternet bağlantını kontrol et.', ingredients_label:'İçindekiler (kaynak: Open Food Facts)',
  rules_version_label:'Sürüm',
  home_ocr_btn:'📷 Fotoğrafla Oku (Yapay Zekâ)', barcode_scan_btn:'📷 Kamera ile Tara',
  settings_ai_title:'Yapay Zekâ Yardımı (Fotoğraf Okuma)',
  settings_ai_lead:'Fotoğrafla okuma sonrası bozuk/kırık kelimeleri düzeltmek için isteğe bağlı bir yardımcı sunucu adresi girebilirsin. Boş bırakılırsa yalnızca cihazdaki metin tanıma kullanılır.',
  settings_ai_save:'Kaydet', settings_ai_note:'Yapay zekâ yalnızca yazıyı okur/düzeltir; helal-haram kararını hiçbir zaman o vermez, karar her zaman kural motorundan gelir.',
  ai_saved:'Yapay zekâ ayarları kaydedildi', ai_cleaning:'Yapay zekâ metni düzenliyor…', ai_unreachable:'Yardımcı sunucuya ulaşılamadı, ham metin kullanılıyor.',
  scanning_barcode:'Barkod taranıyor…', scan_cancelled:'Tarama iptal edildi', reading_photo:'Fotoğraf okunuyor…'
 },
 en:{
  home_title:'Ingredient Analysis', home_lead:'Paste the ingredient list from a product label to see possible flags.',
  home_placeholder:'E.g.: sugar, water, gelatin, E471, salt...', home_analyze:'Analyze',
  disclaimer_short:'These results are informational only, not a religious ruling. For a definitive decision, consult a certified halal-certification body or a scholar you trust.',
  barcode_title:'Barcode Lookup', barcode_lead:'Enter a product barcode to fetch its ingredients automatically from Open Food Facts.',
  barcode_placeholder:'E.g.: 8690504041202', barcode_lookup:'Look up',
  barcode_note:"Barcode lookup needs an internet connection. If the product isn't in the database, you can enter the ingredients manually on the Home tab.",
  encyc_title:'Ingredient Guide', encyc_lead:'Commonly encountered ingredients and why they are flagged.',
  encyc_search:'Search ingredient...', encyc_ok_title:'Commonly Considered Fine',
  history_title:'History', history_lead:'Your previous checks are listed here.',
  settings_title:'Settings', settings_lang:'App Language', settings_lang_sub:'Türkçe / English',
  settings_profile:'Fiqh Profile', settings_rulesver:'Rule Set Version',
  settings_clear:'Clear History', settings_clear_btn:'Clear', settings_about:'About',
  about_p1:'HelalGuard is an informational tool that flags commonly-discussed ingredients in a product\u2019s ingredient list using transparent rules.',
  about_p2:'It always keeps the distinction between "haram" and "uncertain/needs review" — it never invents a ruling the text doesn\u2019t support.',
  about_p3:'This app is not a religious authority. For a definitive, binding ruling, please consult a certified halal-certification body or a scholar you trust.',
  tab_home:'Home', tab_barcode:'Barcode', tab_encyc:'Guide', tab_history:'History', tab_settings:'Settings',
  v_halal:'HALAL', v_halal_sub:'No flagged ingredient found', v_haram:'NOT HALAL', v_haram_sub:'A clearly prohibited ingredient was detected',
  v_uncertain:'UNCERTAIN', v_uncertain_sub:'Ingredient(s) with unclear source need review', v_unknown:'INSUFFICIENT DATA', v_unknown_sub:'No text entered',
  matched:'Matched text', all_cat:'All', empty_enc:'No results', empty_hist:'No history yet', empty_hist_sub:'Checks you run will show up here.',
  toast_saved:'Saved', toast_cleared:'History cleared', toast_empty:'Please enter an ingredient list', toast_barcode_empty:'Please enter a barcode number',
  looking_up:'Looking up product…', not_found:"Product not found in the database. You can enter the ingredients manually on the Home tab.",
  network_err:'Could not connect. Check your internet connection.', ingredients_label:'Ingredients (source: Open Food Facts)',
  rules_version_label:'Version',
  home_ocr_btn:'📷 Read with camera (AI)', barcode_scan_btn:'📷 Scan with camera',
  settings_ai_title:'AI Help (Photo Reading)',
  settings_ai_lead:'Optionally set a helper server address to clean up broken words after reading a photo. Leave blank to use only on-device text recognition.',
  settings_ai_save:'Save', settings_ai_note:'The AI only reads/cleans the text; it never decides halal/haram — that verdict always comes from the rule engine.',
  ai_saved:'AI settings saved', ai_cleaning:'AI is cleaning up the text…', ai_unreachable:'Could not reach the helper server, using raw text.',
  scanning_barcode:'Scanning barcode…', scan_cancelled:'Scan cancelled', reading_photo:'Reading photo…'
 }
};
let lang = localStorage.getItem('hg_lang') || 'tr';
function tr(key){return (I18N[lang]&&I18N[lang][key]) || key}

/* ---------------- state / persistence ---------------- */
const SAVEKEY='hg_state_v1';
let state = {history:[]};
try{const raw=localStorage.getItem(SAVEKEY); if(raw) state=Object.assign(state, JSON.parse(raw))}catch(e){}
function persist(){try{localStorage.setItem(SAVEKEY, JSON.stringify(state))}catch(e){}}

/* ---------------- toast ---------------- */
let toastT=null;
function toast(msg){
 const el=document.getElementById('toast'); el.textContent=msg; el.classList.add('show');
 clearTimeout(toastT); toastT=setTimeout(()=>el.classList.remove('show'), 2200);
}

/* ---------------- verdict rendering ---------------- */
function verdictMeta(v){
 return {
  halal:{cls:'v-halal', big:tr('v_halal'), sub:tr('v_halal_sub')},
  haram:{cls:'v-haram', big:tr('v_haram'), sub:tr('v_haram_sub')},
  uncertain:{cls:'v-uncertain', big:tr('v_uncertain'), sub:tr('v_uncertain_sub')},
  unknown:{cls:'v-unknown', big:tr('v_unknown'), sub:tr('v_unknown_sub')}
 }[v];
}
function findingsHTML(findings){
 if(!findings.length) return '';
 return findings.map(f=>{
  const cls = f.status==='haram' ? 'f-haram' : 'f-uncertain';
  const catName = (CATS[f.cat]&&CATS[f.cat][lang]) || '';
  return `<div class="finding ${cls}">
   <div class="fh"><span>${esc(catName||f.id)}</span><span class="tag">${f.status==='haram'?tr('v_haram'):tr('v_uncertain')}</span></div>
   <div class="fr">${esc(f.reason[lang]||f.reason.en)}</div>
   <div class="fm">${esc(tr('matched'))}: "${esc(f.matched)}"</div>
  </div>`;
 }).join('');
}
function esc(s){return String(s==null?'':s).replace(/[&<>"']/g, c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]))}

function renderResult(container, text, result, meta){
 const vm = verdictMeta(result.verdict);
 let html = `<div class="verdict ${vm.cls}"><div class="big">${vm.big}</div><div class="sub">${vm.sub}</div></div>`;
 if(meta && meta.name){
  html += `<div class="card"><div class="pinfo">${meta.image?`<img src="${esc(meta.image)}">`:''}<div><div class="pn">${esc(meta.name)}</div><div class="pb">${esc(meta.brand||'')}</div></div></div>`;
  html += `<div class="section-title" style="margin-top:0">${esc(tr('ingredients_label'))}</div><p class="lead" style="margin-bottom:0">${esc(text)}</p></div>`;
 }
 html += findingsHTML(result.findings);
 container.innerHTML = html;
}

/* ---------------- history ---------------- */
function addHistory(entry){
 state.history.unshift(entry);
 if(state.history.length>60) state.history.length=60;
 persist();
}
function renderHistory(){
 const el=document.getElementById('historyList');
 if(!state.history.length){
  el.innerHTML = `<div class="empty"><svg viewBox="0 0 24 24" fill="none"><path d="M3 12a9 9 0 109-9" stroke="currentColor" stroke-width="1.6" stroke-linecap="round"/><path d="M3 4v5h5" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round"/></svg><div>${esc(tr('empty_hist'))}</div><div style="font-size:12px;margin-top:4px">${esc(tr('empty_hist_sub'))}</div></div>`;
  return;
 }
 el.innerHTML = state.history.map((h,i)=>{
  const label = h.name || (h.text.length>60 ? h.text.slice(0,60)+'…' : h.text);
  const d = new Date(h.at);
  const dstr = d.toLocaleDateString(lang==='tr'?'tr-TR':'en-US',{day:'2-digit',month:'short',hour:'2-digit',minute:'2-digit'});
  return `<div class="hist-item" data-idx="${i}"><div class="hist-dot dot-${h.verdict}"></div><div class="hist-text"><div class="hist-title">${esc(label)}</div><div class="hist-sub">${dstr}</div></div></div>`;
 }).join('');
 el.querySelectorAll('.hist-item').forEach(node=>{
  node.addEventListener('click', ()=>{
   const h = state.history[+node.dataset.idx];
   showScreen('s-home');
   document.getElementById('ingInput').value = h.text;
   const r = analyzeIngredients(h.text);
   renderResult(document.getElementById('resultArea'), h.text, r, h.meta);
  });
 });
}

/* ---------------- encyclopedia ---------------- */
let encFilter = 'all', encQuery='';
function renderEncChips(){
 const el = document.getElementById('encChips');
 const cats = ['all', ...Object.keys(CATS)];
 el.innerHTML = cats.map(c=>{
  const label = c==='all' ? tr('all_cat') : (CATS[c][lang]||c);
  return `<div class="chip ${c===encFilter?'active':''}" data-cat="${c}">${esc(label)}</div>`;
 }).join('');
 el.querySelectorAll('.chip').forEach(ch=>{
  ch.addEventListener('click', ()=>{ encFilter=ch.dataset.cat; renderEncyc(); });
 });
}
function renderEncyc(){
 renderEncChips();
 const q = encQuery.trim().toLowerCase();
 const list = RULES.filter(r=>{
  if(encFilter!=='all' && r.cat!==encFilter) return false;
  if(!q) return true;
  const hay = (r.reason[lang]+' '+r.id+' '+r.patterns.join(' ')).toLowerCase();
  return hay.includes(q);
 });
 const el = document.getElementById('encList');
 if(!list.length){ el.innerHTML = `<div class="empty" style="padding:30px 20px">${esc(tr('empty_enc'))}</div>`; }
 else {
  el.innerHTML = list.map(r=>{
   const badge = r.status==='haram' ? 'b-haram' : 'b-uncertain';
   const badgeLabel = r.status==='haram' ? tr('v_haram') : tr('v_uncertain');
   const catName = (CATS[r.cat]&&CATS[r.cat][lang])||'';
   return `<div class="entry"><div class="eh"><span class="name">${esc(catName)}</span><span class="badge ${badge}">${esc(badgeLabel)}</span></div>
    <div class="desc">${esc(r.reason[lang]||r.reason.en)}</div>
    <div class="pats">${esc(r.patterns.slice(0,5).join(', '))}${r.patterns.length>5?'…':''}</div></div>`;
  }).join('');
 }
 const okEl = document.getElementById('okList');
 okEl.innerHTML = COMMONLY_OK.map(o=>`<div class="okentry">✓ ${esc(o[lang]||o.en)}</div>`).join('');
}

/* ---------------- screens / nav ---------------- */
function showScreen(id){
 document.querySelectorAll('.screen').forEach(s=>s.classList.toggle('active', s.id===id));
 document.querySelectorAll('nav.tabbar button').forEach(b=>b.classList.toggle('active', b.dataset.screen===id));
 document.getElementById('main').scrollTop = 0;
 if(id==='s-history') renderHistory();
 if(id==='s-encyc') renderEncyc();
}
document.querySelectorAll('nav.tabbar button').forEach(b=>{
 b.addEventListener('click', ()=>showScreen(b.dataset.screen));
});

/* ---------------- home: analyze ---------------- */
document.getElementById('analyzeBtn').addEventListener('click', ()=>{
 const text = document.getElementById('ingInput').value;
 if(!text.trim()){ toast(tr('toast_empty')); return; }
 const r = analyzeIngredients(text);
 renderResult(document.getElementById('resultArea'), text, r);
 addHistory({text, verdict:r.verdict, at:Date.now()});
 toast(tr('toast_saved'));
});

/* ---------------- barcode lookup ---------------- */
document.getElementById('barcodeBtn').addEventListener('click', async ()=>{
 const code = document.getElementById('barcodeInput').value.trim();
 const area = document.getElementById('barcodeArea');
 if(!code){ toast(tr('toast_barcode_empty')); return; }
 area.innerHTML = `<div class="card" style="text-align:center;color:var(--sub)"><span class="spin" style="border-color:rgba(8,116,67,.25);border-top-color:var(--g)"></span>${esc(tr('looking_up'))}</div>`;
 try{
  const resp = await fetch('https://world.openfoodfacts.org/api/v3/product/'+encodeURIComponent(code)+'.json', {method:'GET'});
  if(!resp.ok) throw new Error('http '+resp.status);
  const data = await resp.json();
  const p = data && data.product;
  const ingText = p && (p.ingredients_text_tr || p.ingredients_text_en || p.ingredients_text) || '';
  if(!p || (data.status===0) || !ingText){
   area.innerHTML = `<div class="card" style="color:var(--sub)">${esc(tr('not_found'))}</div>`;
   return;
  }
  const meta = {name: p.product_name || p.product_name_tr || p.product_name_en || code, brand: p.brands||'', image: p.image_front_small_url||p.image_url||''};
  const r = analyzeIngredients(ingText);
  renderResult(area, ingText, r, meta);
  addHistory({text: ingText, verdict:r.verdict, at:Date.now(), name: meta.name, meta});
  toast(tr('toast_saved'));
 }catch(err){
  area.innerHTML = `<div class="card" style="color:var(--sub)">${esc(tr('network_err'))}</div>`;
 }
});

/* ---------------- settings ---------------- */
document.getElementById('clearHistBtn').addEventListener('click', ()=>{
 state.history=[]; persist(); renderHistory(); toast(tr('toast_cleared'));
});
function setLang(l){
 lang=l; localStorage.setItem('hg_lang', l);
 document.getElementById('langBtn').textContent = l.toUpperCase();
 document.getElementById('settingsLangBtn').textContent = l==='tr'?'EN':'TR';
 document.querySelectorAll('[data-i]').forEach(el=>{ el.textContent = tr(el.dataset.i) });
 document.querySelectorAll('[data-i-ph]').forEach(el=>{ el.placeholder = tr(el.dataset.iPh) });
 document.getElementById('profileSub').textContent = lang==='tr' ? 'Hanefi (genel, muhafazakâr)' : 'Hanafi (general, conservative)';
 document.getElementById('rulesVerSub').textContent = tr('rules_version_label')+' '+RULES_VERSION;
 renderEncyc(); renderHistory();
}
document.getElementById('langBtn').addEventListener('click', ()=> setLang(lang==='tr'?'en':'tr'));
document.getElementById('settingsLangBtn').addEventListener('click', ()=> setLang(lang==='tr'?'en':'tr'));
document.getElementById('encSearch').addEventListener('input', (e)=>{ encQuery=e.target.value; renderEncyc(); });

/* ---------------- AI helper settings ---------------- */
function loadAiConfig(){
 try{ return JSON.parse(localStorage.getItem('hg_ai_cfg')||'{}') }catch(e){ return {} }
}
function saveAiConfig(cfg){ try{ localStorage.setItem('hg_ai_cfg', JSON.stringify(cfg)) }catch(e){} }
(function initAiSettings(){
 const cfg = loadAiConfig();
 document.getElementById('aiUrlInput').value = cfg.url || '';
 document.getElementById('aiTokenInput').value = cfg.token || '';
})();
document.getElementById('aiSaveBtn').addEventListener('click', ()=>{
 const url = document.getElementById('aiUrlInput').value.trim().replace(/\/$/,'');
 const token = document.getElementById('aiTokenInput').value.trim();
 saveAiConfig({url, token});
 toast(tr('ai_saved'));
});

/* Send raw OCR text to the optional helper server for cleanup.
   Falls back to the raw text (split on commas/newlines) if no server is
   configured or it cannot be reached — the app must keep working offline. */
async function aiCleanOcrText(raw){
 const cfg = loadAiConfig();
 if(!cfg.url) return raw;
 try{
  const headers = {'content-type':'application/json'};
  if(cfg.token) headers['x-app-token'] = cfg.token;
  const resp = await fetch(cfg.url + '/api/clean', {method:'POST', headers, body: JSON.stringify({text: raw.slice(0,4000)})});
  if(!resp.ok) return raw;
  const data = await resp.json();
  if(data && Array.isArray(data.ingredients) && data.ingredients.length) return data.ingredients.join(', ');
  return raw;
 }catch(e){
  toast(tr('ai_unreachable'));
  return raw;
 }
}

/* ---------------- native camera bridge ---------------- */
const hasNative = typeof window.Native !== 'undefined' && typeof window.Native.scan === 'function';
if(hasNative){
 document.getElementById('ocrBtn').style.display = '';
 document.getElementById('barcodeScanBtn').style.display = '';
}
let pendingScan = null; // 'ocr' | 'barcode'
document.getElementById('ocrBtn').addEventListener('click', ()=>{
 if(!hasNative) return;
 pendingScan = 'ocr';
 window.Native.scan('ocr');
});
document.getElementById('barcodeScanBtn').addEventListener('click', ()=>{
 if(!hasNative) return;
 pendingScan = 'barcode';
 window.Native.scan('barcode');
});
window.onNativeResult = async function(jsonStr){
 let payload;
 try{ payload = JSON.parse(jsonStr) }catch(e){ return }
 const mode = pendingScan; pendingScan = null;
 if(!payload.ok){
  if(payload.error !== 'cancelled') toast(tr('scan_cancelled'));
  return;
 }
 if(payload.kind === 'barcode'){
  document.getElementById('barcodeInput').value = payload.value;
  document.getElementById('barcodeBtn').click();
 }else if(payload.kind === 'ocr'){
  const area = document.getElementById('resultArea');
  area.innerHTML = `<div class="card" style="text-align:center;color:var(--sub)"><span class="spin" style="border-color:rgba(8,116,67,.25);border-top-color:var(--g)"></span>${esc(tr('ai_cleaning'))}</div>`;
  const cleaned = await aiCleanOcrText(payload.value);
  document.getElementById('ingInput').value = cleaned;
  const r = analyzeIngredients(cleaned);
  renderResult(area, cleaned, r);
  addHistory({text: cleaned, verdict:r.verdict, at:Date.now()});
 }
};

/* ---------------- init ---------------- */
setLang(lang);
showScreen('s-home');
