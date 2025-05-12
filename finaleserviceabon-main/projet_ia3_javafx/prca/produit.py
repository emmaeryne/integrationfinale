import pandas as pd

categories = {
    "Énergétique": [
        "Red Bull", "Monster Energy", "Rockstar Energy", "Gatorade", "Powerade", "V8 Energy", 
        "Celsius", "NOS Energy", "5-Hour Energy", "Zipfizz", "Bang Energy", "XS Energy", 
        "Motive8", "Eboost", "GU Energy", "Stinger Energy", "Guarana", "Lypo-Spheric Energy", 
        "Runa", "Revive", "Tribal", "FXX", "Xtend Energy", "TRUE Energy", "FitAid", 
        "Hydrive Energy", "Amino Energy", "Bodyarmor", "Nutrabolt C4 Energy", "Super Coffee", 
        "MatchaBar Hustle", "Aspire", "Raze", "Mio Energy", "Bai Energy", "Skream", "Volcano Energy", 
        "Rockstar Pure Zero", "Vita Coco Energy", "Neuro Sonic", "Cellucor C4", "ThermoLife", 
        "Pure Boost", "Adrenaline Shoc", "Fireball Energy", "Elevate", "G Fuel", "Packaged Sun Energy", 
        "Power Performance Energy", "Arctic Energy", "ProSupps Mr. Hyde", "Bang Natural Energy", 
        "Reign Total Body Fuel", "Performance Energy", "Lazarus Naturals Energy", "CytoSport Monster", 
        "7-Eleven Energy", "Juiced Energy", "Taurine Boost", "C4 Ripped", "Amino Performance"
    ],
    "Musculation": [
        "Protéine Whey", "BCAA", "Créatine", "Glutamine", "HMB", "Protéine Isolat", "Protéine Végétale", 
        "Hydrolysat de Protéine", "Protéine de caséine", "Acides Aminés", "Pré-workout", "Post-workout", 
        "Beurre de cacahuète protéiné", "Barres protéinées", "Maltodextrine", "Gainer", "Creatine Monohydrate", 
        "Beta-Alanine", "DHEA", "L-Carnitine", "ZMA", "Arginine", "Citrulline", "L-Glutamine", "Collagène", 
        "Protéines végétales bio", "Peptides de collagène", "Gélules de créatine", "Acide alpha-lipoïque", 
        "Acides aminés essentiels", "Supplements pré-entraînement", "Entraînement de force", "Protéine de soja", 
        "Protéine de riz", "Caféine", "L-Tyrosine", "Glucosamine", "Kre-Alkalyn", "L-Ornithine", 
        "BCAA intra-workout", "Caféine anhydre", "Citrulline Malate", "Mélange d’électrolytes", "Protéines bio", 
        "Sodium", "Acides gras essentiels", "Lactase", "Acides aminés ramifiés", "Collagène hydrolysé", 
        "Protéine d’œuf", "Peptides de collagène marin", "Aminos", "Flavonoïdes", "Protéine de chanvre", 
        "Hydroxycitric acid (HCA)", "Protéine de lait", "Isolat de protéine de pois", "Mélange de glucides", 
        "Protéine végétalienne", "Dextrose", "Protéine de pois bio", "L-Glutamine micellaire", "Lactosérum", 
        "Protéine d’amande", "L-Carnitine-L-Tartrate", "Taurine", "Glutamine en poudre", "Protéine d’avoine", 
        "Gainer végétalien", "Iso-Whey", "Protéine d’algues", "Sodium", "Protéine de coco", 
        "Mélange de protéines végétales"
    ],
    "Soins Sportive": [
        "Crème de massage", "Baume musculaire", "Bandes de kinésiologie", "Crème de récupération musculaire", 
        "Gel anti-inflammatoire", "Spray de refroidissement", "Huile de massage", "Baume chauffant", 
        "Bande de compression", "Bandage de soutien", "Pommade musculaire", "Crème de soulagement", 
        "Roll-on anti-douleur", "Gel de sport", "Spray anti-douleur articulaire", "Baume relaxant", 
        "Soins des tendons", "Crème de circulation sanguine", "Gel de récupération rapide", "Patchs chauffants", 
        "Gel de froid", "Soin des articulations", "Bains de pieds relaxants", "Crème apaisante pour muscles", 
        "Compresses froides", "Spray à l’arnica", "Baume revitalisant", "Lotion après-effort", "Gants de massage", 
        "Crème de sport", "Pommade à base d’arnica", "Baume réparateur", "Bandeau chauffant", 
        "Spray apaisant", "Baume à l’eucalyptus", "Cire de massage", "Crème de soins musculaires", 
        "Bain de chaleur", "Gel d’hydratation", "Spray anti-frottement", "Baume réconfortant", "Huile essentielle", 
        "Compresses de récupération", "Pansements pour sportifs", "Huile chauffante", "Gel de soutient musculaire", 
        "Mousse de massage", "Baume calmant", "Gel relaxant", "Tapis de massage", "Gants de kinésiologie", 
        "Gel de récupération musculaire", "Crème chauffante pour muscles", "Bandeaux thérapeutiques", 
        "Bande élastique", "Baume pour les genoux", "Pansement cicatrisant", "Crème pour entorses", "Spray de refroidissement", 
        "Gels apaisants", "Soins pour le dos", "Baume antifatigues", "Huile de récupération", "Gel pour foulure", 
        "Crème de soulagement articulaire", "Baume pour les douleurs musculaires", "Gel apaisant musculaire", 
        "Patch de récupération", "Pommade pour crampes", "Bain de soleil réparateur", "Patch anti-douleur", 
        "Compresses de chaleur", "Gel décontractant", "Baume musculaire pour sport", "Crème anti-douleur articulaire", 
        "Spray de massage", "Gel de soutien articulaire", "Soin des muscles après entraînement", "Crème régénérante"
    ]
}


# 📌 Création du dataset
data = []
for categorie, produits in categories.items():
    for produit in produits:
        data.append({"Nom Produit": produit.strip().lower(), "Catégorie": categorie})  

# 📌 Création du DataFrame
df = pd.DataFrame(data)

# 📌 Sauvegarde du fichier CSV
df.to_csv("produits_500.csv", index=False, encoding="utf-8")

print("📂 Dataset enregistré avec succès !")
