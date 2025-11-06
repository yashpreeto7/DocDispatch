from flask import Flask, request, jsonify

app = Flask(__name__)

@app.route('/', methods=['POST'])
def receive_summary():
    try:
        data = request.get_json()
        summary = data.get('summary', '')

        print("Received summary:")
        print(summary)


        return jsonify({"status": "success", "message": "Summary received"}), 200

    except Exception as e:
        return jsonify({"status": "error", "message": str(e)}), 400

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5050, debug=True)