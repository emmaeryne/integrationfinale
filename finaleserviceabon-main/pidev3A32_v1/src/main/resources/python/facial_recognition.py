import sys
import cv2
import numpy as np
import requests
import os

def load_image(image_path):
    try:
        if image_path.startswith("http"):
            response = requests.get(image_path, stream=True)
            if response.status_code == 200:
                image_array = np.asarray(bytearray(response.content), dtype=np.uint8)
                return cv2.imdecode(image_array, cv2.IMREAD_COLOR)
            else:
                print("Error: Failed to download image")
                sys.exit(1)
        elif os.path.exists(image_path):
            return cv2.imread(image_path)
        else:
            print("Error: Image path invalid")
            sys.exit(1)
    except Exception as e:
        print(f"Error: {str(e)}")
        sys.exit(1)

def detect_face(image_path):
    try:
        face_cascade = cv2.CascadeClassifier(cv2.data.haarcascades + "haarcascade_frontalface_default.xml")
        image = load_image(image_path)

        if image is None:
            print("Error: Could not load image")
            sys.exit(1)

        gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
        faces = face_cascade.detectMultiScale(gray, scaleFactor=1.1, minNeighbors=5, minSize=(30, 30))

        print(1 if len(faces) > 0 else 0)
        sys.exit(0)
    except Exception as e:
        print(f"Error: {str(e)}")
        sys.exit(1)

if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("Error: No image path provided")
        sys.exit(1)

    image_path = sys.argv[1]
    detect_face(image_path)
