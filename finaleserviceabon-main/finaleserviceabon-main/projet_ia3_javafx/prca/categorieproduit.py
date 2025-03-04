from flask import Flask, request, jsonify
import joblib
import numpy as np

app = Flask(__name__)

# 📌 Charger le modèle et le vectorizer
try:
    model = joblib.load("produit_categorie_model.pkl")

    # Vérifier si le modèle est un pipeline contenant 'tfidf'
    if hasattr(model, "named_steps") and "tfidf" in model.named_steps:
        vectorizer = model.named_steps["tfidf"]
    else:
        raise ValueError("⚠️ Le modèle chargé n'est pas un pipeline valide ou ne contient pas 'tfidf'.")

    # Vérifier que le modèle a bien `predict_proba()`
    if not hasattr(model.named_steps["classifier"], "predict_proba"):
        raise ValueError("⚠️ Le classifieur du modèle ne supporte pas `predict_proba()`.")

    print("✅ Modèle et vectorizer chargés avec succès !")

    # 🔍 Tester `predict_proba()` après chargement
    try:
        test_vect = vectorizer.transform(["test"])
        test_proba = model.named_steps["classifier"].predict_proba(test_vect)
        print(f"✅ Test `predict_proba()` OK, sortie: {test_proba}")
    except Exception as e:
        raise ValueError(f"❌ ERREUR: `predict_proba()` ne fonctionne pas après chargement. Détails : {e}")

except Exception as e:
    print(f"❌ ERREUR: Problème lors du chargement du modèle : {e}")
    exit(1)  # Arrêter Flask si le modèle ne peut pas être chargé

@app.route("/", methods=["GET"])
def home():
    return "🚀 Serveur Flask en cours d'exécution !"

@app.route("/predict", methods=["POST"])
def predict():
    try:
        data = request.get_json()

        # Vérifier que 'nom_produit' est bien présent et valide
        if not data or "nom_produit" not in data:
            return jsonify({"error": "Le champ 'nom_produit' est requis."}), 400

        nom_produit = data.get("nom_produit")

        if not isinstance(nom_produit, str) or not nom_produit.strip():
            return jsonify({"error": "Le champ 'nom_produit' doit être une chaîne de caractères non vide."}), 400

        # Nettoyer et transformer en minuscules
        nom_produit = nom_produit.strip().lower()

        # 🔹 Transformer l'entrée avec le vectorizer
        try:
            nom_produit_vect = vectorizer.transform([nom_produit])
            print(f"✅ Transformation réussie, dimensions : {nom_produit_vect.shape}")
        except Exception as e:
            return jsonify({"error": "Erreur lors de la vectorisation du texte", "details": str(e)}), 500

        # 🔮 Faire la prédiction
        try:
            proba = model.named_steps["classifier"].predict_proba(nom_produit_vect)
            max_proba = np.max(proba)
            prediction = model.classes_[np.argmax(proba)]
            print(f"🔮 Prédiction : {prediction} (Confiance: {max_proba:.2f})")

            # Si la confiance est inférieure à 0.5, renvoyer "Inconnue"
            if max_proba < 0.5:
                prediction = "Inconnue"
                print("⚠️ Confiance insuffisante (< 0.5), catégorie fixée à 'Inconnue'")
                
        except Exception as e:
            return jsonify({"error": "Erreur interne du modèle", "details": str(e)}), 500

        return jsonify({"categorie_predite": prediction, "confiance": round(max_proba, 2)})

    except Exception as e:
        return jsonify({"error": "Erreur interne du serveur", "details": str(e)}), 500

if __name__ == "__main__":
    app.run(host="127.0.0.1", port=5004, debug=True)
