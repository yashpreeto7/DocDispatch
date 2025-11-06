from flask import Flask, request, jsonify
from datetime import datetime

app = Flask(__name__)

FILE = "file.txt"

@app.route('/', methods=['POST'])
def receive_patient_data():
    try:
        data = request.get_json()
        if not data:
            return jsonify({"status": "error", "message": "No JSON received"}), 400

        name = data.get('name', '').strip()
        age = data.get('age', '').strip()
        gender = data.get('gender', '').strip()
        temperature = data.get('temperature', '').strip()
        days = data.get('days', '').strip()
        contagious = data.get('contagious', '').strip()

        if not all([name, age, gender, temperature, days, contagious]):
            return jsonify({"status": "error", "message": "Missing required fields"}), 400

        summary = (
            f"Name: {name}\n"
            f"Age: {age}\n"
            f"Gender: {gender}\n"
            f"Temperature: {temperature}°C\n"
            f"Days: {days}\n"
            f"Contagious: {contagious}"
        )

        with open(FILE, "a", encoding="utf-8") as f:
            f.write("=" * 50 + "\n")
            f.write(f"Received at: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}\n")
            f.write(summary + "\n")
            f.write("=" * 50 + "\n\n")

        return jsonify({"status": "success", "message": "Patient data saved"}), 200

    except Exception as e:
        print("❌ Error:", e)
        return jsonify({"status": "error", "message": str(e)}), 500

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5050, debug=True)