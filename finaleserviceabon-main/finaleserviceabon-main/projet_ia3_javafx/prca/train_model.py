import pandas as pd
import joblib
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.naive_bayes import MultinomialNB
from sklearn.model_selection import train_test_split
from sklearn.pipeline import Pipeline

# 📌 Charger les données
df = pd.read_csv("Large_Product_Dataset.csv")

# 📌 Vérifier si le dataset est bien chargé
if df.empty:
    raise ValueError("⚠️ ERREUR: Le fichier `produits_500.csv` est vide !")

# 📌 Nettoyage des données
df = df.dropna()  # Supprime les valeurs nulles
X = df["Nom Produit"].str.strip()
y = df["Catégorie"]

# 📌 Séparer en train/test
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)

# 📌 Construire le pipeline
model = Pipeline([
    ("tfidf", TfidfVectorizer(ngram_range=(1, 2), lowercase=True, strip_accents="unicode")),
    ("classifier", MultinomialNB())
])

# 📌 Entraîner le modèle
model.fit(X_train, y_train)

# 📌 Vérifier `predict_proba()` avant de sauvegarder
try:
    test_vect = model.named_steps["tfidf"].transform(["test"])
    test_proba = model.named_steps["classifier"].predict_proba(test_vect)
    print(f"✅ Test `predict_proba()` réussi, sortie: {test_proba}")
except Exception as e:
    raise ValueError(f"❌ ERREUR: `predict_proba()` ne fonctionne pas. Détails : {e}")

# 📌 Sauvegarde du modèle
joblib.dump(model, "produit_categorie_model.pkl")

print("🎯 Modèle entraîné et sauvegardé avec succès !")
