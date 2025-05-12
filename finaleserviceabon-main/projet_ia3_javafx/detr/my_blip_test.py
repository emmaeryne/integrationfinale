from transformers import BlipProcessor, BlipForConditionalGeneration
from PIL import Image
import requests

def generate_caption_blip(image_path_or_url):
    """
    Génère une légende (description) à partir d'une image en utilisant BLIP v1.
    Modèle : Salesforce/blip-image-captioning-base
    Taille ~ 400-500 Mo, beaucoup plus léger qu'InstructBLIP (9.8 Go).
    """
    model_name = "Salesforce/blip-image-captioning-base"
    
    # 1) Charger le processor et le modèle
    processor = BlipProcessor.from_pretrained(model_name)
    model = BlipForConditionalGeneration.from_pretrained(model_name)

    # 2) Charger l'image (depuis une URL ou un chemin local)
    if image_path_or_url.startswith("http"):
        # Image depuis Internet
        image = Image.open(requests.get(image_path_or_url, stream=True).raw).convert("RGB")
    else:
        # Image depuis le disque
        image = Image.open(image_path_or_url).convert("RGB")

    # 3) Préparer l'entrée pour le modèle
    inputs = processor(image, return_tensors="pt")
    
    # 4) Génération d'une légende
    output_ids = model.generate(**inputs, max_new_tokens=30)
    caption_brute = processor.decode(output_ids[0], skip_special_tokens=True)
    
    return caption_brute

def construire_description(nom_categorie, caption):
    """
    Combine le nom de la catégorie et la légende brute en une description finale.
    """
    return f"{nom_categorie} : {caption}"

# ===========================
# Exemple d’utilisation
# ===========================
if __name__ == "__main__":
    # URL d'exemple (image open-source "Confusing-Pictures" de Salesforce)
    image_url = "https://raw.githubusercontent.com/salesforce/LAVIS/main/docs/_static/Confusing-Pictures.jpg"
    
    # Nom de catégorie
    nom_categorie = "Catégorie Exotique"
    
    # 1) Générer la légende brute
    caption = generate_caption_blip(image_url)
    
    # 2) Combiner avec le nom de catégorie
    description_finale = construire_description(nom_categorie, caption)
    
    print("Légende brute :", caption)
    print("Description finale :", description_finale)
