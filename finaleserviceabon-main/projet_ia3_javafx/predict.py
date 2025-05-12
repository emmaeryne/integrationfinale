import tensorflow as tf
from tensorflow.keras.applications import MobileNetV2
from tensorflow.keras.preprocessing import image
from tensorflow.keras.applications.mobilenet_v2 import preprocess_input, decode_predictions
import numpy as np
from sklearn.metrics.pairwise import cosine_similarity
from sentence_transformers import SentenceTransformer

# Charger le modèle MobileNetV2 pré-entraîné pour la classification d'images
model = MobileNetV2(weights='imagenet')

def predict_image_category(img_path):
    """Prédit la catégorie de l'image en utilisant MobileNetV2"""
    img = image.load_img(img_path, target_size=(224, 224))
    img_array = image.img_to_array(img)
    img_array = np.expand_dims(img_array, axis=0)
    img_array = preprocess_input(img_array)

    predictions = model.predict(img_array)
    decoded_predictions = decode_predictions(predictions, top=3)[0]

    return [pred[1] for pred in decoded_predictions]  # Retourne les 3 meilleures prédictions

# Charger le modèle NLP pour la correspondance sémantique
nlp_model = SentenceTransformer("C:/Users/moham/huggingface_models/all-MiniLM-L6-v2")

# 🏋️‍♂️ Liste de catégories liées à la musculation
CATEGORIES_SPORT = ["musculation", "fitness", "bodybuilding", "gym", "sport", "haltères", "dumbbell", "barbell"]

def is_text_matching_image(user_text, image_labels):
    """
    Vérifie si le texte de l'utilisateur correspond aux catégories prédites par MobileNetV2
    en utilisant une similarité sémantique NLP et des synonymes.
    """
    user_embedding = nlp_model.encode([user_text])  # Encode la catégorie saisie par l'utilisateur
    image_embeddings = nlp_model.encode(image_labels)  # Encode les prédictions de l’image

    # Calcul de la similarité entre le texte utilisateur et chaque label prédictif
    similarities = cosine_similarity(user_embedding, image_embeddings)[0]
    
    best_match_index = np.argmax(similarities)  # Trouver le meilleur match
    best_match_score = similarities[best_match_index]

    print(f"🔍 Meilleure correspondance : {image_labels[best_match_index]} (score={best_match_score:.2f})")

    # Vérification avec un seuil plus bas
    if best_match_score > 0.3:
        return True  

    # 🔍 Vérification avec liste de synonymes
    for label in image_labels:
        label_lower = label.lower()
        if label_lower in CATEGORIES_SPORT:
            print(f"✅ Correspondance trouvée avec une catégorie liée au sport : {label_lower}")
            return True  

    return False  # Aucune correspondance trouvée
