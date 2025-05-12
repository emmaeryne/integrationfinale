# Fichier : translate_server.py
from flask import Flask, request, jsonify
from googletrans import Translator

app = Flask(__name__)

@app.route("/translate", methods=["POST"])
def translate_text():
    data = request.get_json()
    texte = data.get("texte", "")
    source = data.get("source", "en")  # langue source, ex. "en"
    cible = data.get("cible", "fr")   # langue cible, ex. "it"

    # Créer un traducteur
    translator = Translator()

    # Faire la traduction
    result = translator.translate(texte, src=source, dest=cible)

    # Renvoyer un JSON avec le texte traduit
    return jsonify({"traduction": result.text}), 200

if __name__ == "__main__":
    app.run(port=5002, debug=True)
