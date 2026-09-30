/* HelalGuard rule dataset — informational only, not a fatwa.
   Extends the project's original rules.json (v2026.09.1) and combined.js (v2026.09.2)
   with additional commonly-documented items, kept deliberately conservative:
   every entry separates "clearly prohibited" (haram) from "source unclear, verify"
   (uncertain) and never claims a ruling the ingredient name alone doesn't support. */
const RULES_VERSION = '2026.09.3';
const RULE_SOURCES = [
 {id:'hanafi-general', label:'Hanefi fıkıh referans seti', labelEn:'Hanafi fiqh reference set'},
 {id:'qaradawi-general', label:'Yusuf el-Karadavi referans seti', labelEn:'Yusuf al-Qaradawi reference set'}
];
const CATS = {
 pork:{tr:'Domuz Kaynaklı',en:'Pork-derived'},
 alcohol:{tr:'Alkol',en:'Alcohol'},
 animal:{tr:'Hayvansal Kaynak Belirsiz',en:'Animal source unclear'},
 insect:{tr:'Böcek Kaynaklı Renklendirici',en:'Insect-derived colorant'},
 enzyme:{tr:'Enzim',en:'Enzyme'},
 emulsifier:{tr:'Emülgatör / E-kodu',en:'Emulsifier / E-number'},
 meat:{tr:'Et / Et Suyu',en:'Meat / broth'}
};
const RULES = [
 {id:'pork', status:'haram', cat:'pork', reason:{tr:'Domuz kaynaklı bileşen.', en:'Pork/swine-derived ingredient.'},
  patterns:['pork','porcine','pig meat','swine','schweinefleisch','porc','cerdo','carne de cerdo','domuz']},
 {id:'pork-gelatin', status:'haram', cat:'pork', reason:{tr:'Açıkça domuz jelatini olarak belirtilmiş.', en:'Explicitly identified as porcine gelatin.'},
  patterns:['porcine gelatin','pork gelatin','schweinegelatine','gelatine (schwein)','domuz jelatini']},
 {id:'blood', status:'haram', cat:'pork', reason:{tr:'Kan veya kan ürünü.', en:'Blood or blood-derived ingredient.'},
  patterns:['blood plasma','dried blood','blood powder','domuz kanı']},
 {id:'ethanol-beverage', status:'haram', cat:'alcohol', reason:{tr:'İçecek/alkollü içki olarak listelenmiş.', en:'Listed as an alcoholic beverage ingredient.'},
  patterns:['rum','brandy','whisky','whiskey','beer','wine (beverage)','sherry','liqueur']},
 {id:'alcohol', status:'uncertain', cat:'alcohol', reason:{tr:'Alkol ibaresi bağlam ve kaynak değerlendirmesi gerektirir; her iz miktarı otomatik olarak eşitlenmez.', en:'Alcohol wording requires context and source assessment; the engine does not equate every trace or processing use automatically.'},
  patterns:['alcohol','ethanol','ethyl alcohol','ethanolhaltig','spirits','wine vinegar','vanilla extract']},
 {id:'gelatin', status:'uncertain', cat:'animal', reason:{tr:'Jelatinin kaynağı metinde belirtilmemiş.', en:'Gelatin source is not identified in the ingredient text.'},
  patterns:['gelatin','gelatine','gélatine','gelatina','jelatin']},
 {id:'isinglass', status:'uncertain', cat:'animal', reason:{tr:'Balık kaynaklı jelatin türevi; işleme yöntemi metinde belirtilmemiş.', en:'Fish-derived gelatin (isinglass); processing method is not specified.'},
  patterns:['isinglass']},
 {id:'animal-fat', status:'uncertain', cat:'animal', reason:{tr:'Hayvansal kaynak metinden tespit edilemiyor.', en:'Animal source is not established from the text.'},
  patterns:['animal fat','tierisches fett','lard','tallow','shortening','hayvansal yağ']},
 {id:'carmine', status:'uncertain', cat:'insect', reason:{tr:'Karmin/koşnil böcek kaynaklıdır; profil bazlı değerlendirme için işaretlenir.', en:'Carmine/cochineal is animal-derived; the app flags it for profile-specific review.'},
  patterns:['carmine','cochineal','carminic acid','e120','e 120','cochenille','koşnil']},
 {id:'shellac', status:'uncertain', cat:'insect', reason:{tr:'Şellak, böcek salgısından elde edilen bir parlatıcı maddedir (E904).', en:'Shellac (E904) is a glazing agent derived from insect secretion.'},
  patterns:['shellac','e904','e 904','şellak']},
 {id:'rennet', status:'uncertain', cat:'enzyme', reason:{tr:'Peynir mayasının kaynağı ve üretim yöntemi doğrulama gerektirir.', en:'Rennet source and production method require verification.'},
  patterns:['rennet','rennin','animal rennet','kalbslab','hayvansal maya']},
 {id:'pepsin', status:'uncertain', cat:'enzyme', reason:{tr:'Hayvansal kaynaklı sindirim enzimi; kaynağı belirtilmemiş.', en:'Animal-derived digestive enzyme; source not specified.'},
  patterns:['pepsin']},
 {id:'lipase', status:'uncertain', cat:'enzyme', reason:{tr:'Hayvansal kaynaklı olabilecek enzim; kaynağı belirtilmemiş.', en:'Enzyme that may be animal-derived; source not specified.'},
  patterns:['animal lipase','lipase (animal)']},
 {id:'l-cysteine', status:'uncertain', cat:'enzyme', reason:{tr:'L-sistein (E920) tüy/kıl veya sentetik kaynaklı olabilir.', en:'L-cysteine (E920) may be sourced from feathers/hair or made synthetically.'},
  patterns:['l-cysteine','e920','e 920','l-sistein']},
 {id:'meat', status:'uncertain', cat:'meat', reason:{tr:'Et menşei ve kesim usulü isim bilgisinden anlaşılamaz.', en:'Meat origin and slaughter status are not established by the ingredient name alone.'},
  patterns:['beef','chicken','turkey','lamb','mutton','bovine','ovine','poultry','veal','sığır eti','tavuk eti','sığır','tavuk']},
 {id:'broth', status:'uncertain', cat:'meat', reason:{tr:'Et suyu/bulyon; hayvanın kesim usulü belirtilmemiş.', en:'Broth/stock/bouillon; the slaughter method of the animal is not specified.'},
  patterns:['chicken broth','beef broth','beef stock','chicken stock','bouillon','et suyu','bulyon','bulyonu']},
 {id:'whey', status:'uncertain', cat:'animal', reason:{tr:'Peynir altı suyu genelde mayalı peynirden elde edilir; mayanın kaynağı belirtilmemiş.', en:'Whey commonly comes from rennet-set cheese; the rennet source is not specified.'},
  patterns:['whey powder','whey protein','peynir altı suyu']},
 {id:'e471', status:'uncertain', cat:'emulsifier', reason:{tr:'Bitkisel veya hayvansal kaynaklı olabilir; üretici teyidi gerekir.', en:'Source can be plant or animal; verify manufacturer/source.'},
  patterns:['e471','e 471','mono- and diglycerides','mono and diglycerides','mono-und diglyceride','mono ve digliseritler']},
 {id:'e472', status:'uncertain', cat:'emulsifier', reason:{tr:'E471 ile aynı kaynak belirsizliğini taşır.', en:'Carries the same sourcing ambiguity as E471.'},
  patterns:['e472','e 472']},
 {id:'polysorbate', status:'uncertain', cat:'emulsifier', reason:{tr:'Bazı polisorbat türevleri hayvansal yağ asitleriyle üretilebilir; kaynak teyidi gerekir.', en:'Some polysorbate variants can be manufactured from animal-derived fatty acids; verify source.'},
  patterns:['polysorbate 60','polysorbate 65','polysorbate 80','e433','e 433','e435','e 435']},
 {id:'e422', status:'uncertain', cat:'emulsifier', reason:{tr:'Gliserin kaynağı değişebilir; gerektiğinde kaynağı doğrulayın.', en:'Glycerol source may vary; verify source where required.'},
  patterns:['e422','e 422','glycerol','glycerin','gliserin']}
];
/* A short "commonly presumed fine" reference list for the encyclopedia's positive section.
   These are NOT scanned/flagged — shown only as an educational browse list. */
const COMMONLY_OK = [
 {tr:'Şeker, tuz, su', en:'Sugar, salt, water'},
 {tr:'Sitrik asit (E330)', en:'Citric acid (E330)'},
 {tr:'Askorbik asit / C vitamini (E300)', en:'Ascorbic acid / vitamin C (E300)'},
 {tr:'Pektin (E440)', en:'Pectin (E440)'},
 {tr:'Ksantan sakızı (E415)', en:'Xanthan gum (E415)'},
 {tr:'Ayçiçek/soya lesitini (E322) — çoğunlukla bitkisel', en:'Sunflower/soy lecithin (E322) — usually plant-based'},
 {tr:'Bitkisel yağlar (ayçiçek, zeytin, kanola)', en:'Vegetable oils (sunflower, olive, canola)'},
 {tr:'Bikarbonat / kabartma tozu', en:'Baking soda / baking powder'}
];
