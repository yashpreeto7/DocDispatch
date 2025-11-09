from flask import Flask, request, jsonify
from datetime import datetime
from regdb import PatientDB

app = Flask(__name__)

@app.route('/', methods=['POST'])
def receive_patient_data():
    try:
        data = request.get_json()
        if not data:
            return jsonify({"status": "error", "message": "No JSON received"}), 400
        
        print("📥 Received data:", data)

        number = data.get('phone', '').strip()
        name = data.get('name', '').strip()
        age = data.get('age', '').strip()
        gender = data.get('gender', '').strip()
        temperature = data.get('temperature', '').strip()
        days = data.get('days', '').strip()
        contagious = data.get('contagious', '').strip()

        if not all([number, name, age, gender, temperature, days, contagious]):
            return jsonify({"status": "error", "message": "Missing required fields"}), 400
        
        db = PatientDB()
        db.insert_patient(data)
        db.close()

        return jsonify({"status": "success", "message": "Patient data saved"}), 200

    except Exception as e:
        print("❌ Error:", e)
        return jsonify({"status": "error", "message": str(e)}), 500

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5050, debug=True)