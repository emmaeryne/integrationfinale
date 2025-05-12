from flask import Flask, request, jsonify
import os
from predict import predict_image_category, is_text_matching_image  # Importer depuis predict.py

app = Flask(__name__)

UPLOAD_FOLDER = "uploads"
os.makedirs(UPLOAD_FOLDER, exist_ok=True)  # Crée le dossier s'il n'existe pas
ALLOWED_EXTENSIONS = {"png", "jpg", "jpeg"}

def allowed_file(filename):
    return '.' in filename and filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS

@app.route('/predict', methods=['POST'])
def predict():
    # Vérifier si l'image et le texte sont présents
    if 'image' not in request.files or 'text' not in request.form:
        return jsonify({"error": "Image ou texte manquant"}), 400

    file = request.files['image']
    text = request.form['text']

    if file.filename == '' or not allowed_file(file.filename):
        return jsonify({"error": "Fichier invalide"}), 400

    file_path = os.path.join(UPLOAD_FOLDER, file.filename)
    file.save(file_path)

    # Prédire la catégorie de l'image
    predicted_labels = predict_image_category(file_path)

    # Vérifier la correspondance avec le texte utilisateur
    match = is_text_matching_image(text, predicted_labels)

    return jsonify({"match": bool(match), "predictions": predicted_labels})

if __name__ == '__main__':
    app.run(port=5000, debug=True)  # Debug activé pour voir les erreurs
