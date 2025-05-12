from flask import Flask, request, jsonify
import base64
from io import BytesIO
from PIL import Image
import torch
from transformers import BlipProcessor, BlipForConditionalGeneration

app = Flask(__name__)

# Charge le modèle BLIP v1 (~400 Mo)
processor = BlipProcessor.from_pretrained("Salesforce/blip-image-captioning-base")
model = BlipForConditionalGeneration.from_pretrained("Salesforce/blip-image-captioning-base")
device = "cuda" if torch.cuda.is_available() else "cpu"
model.to(device)

@app.route("/generate_caption", methods=["POST"])
def generate_caption():
    data = request.get_json()
    if not data:
        return jsonify({"error": "No data"}), 400

    image_b64 = data.get("imageBase64", "")
    nom_categorie = data.get("nomCategorie", "")

    # Décoder l’image
    image_data = base64.b64decode(image_b64)
    pil_image = Image.open(BytesIO(image_data)).convert("RGB")

    inputs = processor(pil_image, return_tensors="pt").to(device)
    with torch.no_grad():
        output_ids = model.generate(**inputs, max_new_tokens=30)
    caption_brute = processor.decode(output_ids[0], skip_special_tokens=True)

    # Combiner avec la catégorie
    description = f"{nom_categorie} : {caption_brute}"
    return jsonify({"description": description}), 200

if __name__ == "__main__":
    app.run(port=5001, debug=True)
