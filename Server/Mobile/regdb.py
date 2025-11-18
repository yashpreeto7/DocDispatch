import os
import mysql.connector
from datetime import datetime
from dotenv import load_dotenv
load_dotenv()

class PatientDB:
    def __init__(self):
        self.host = os.getenv('MYSQL_HOST')
        self.port = int(os.getenv('MYSQL_PORT'))
        self.user = os.getenv('MYSQL_USER')
        self.password = os.getenv('MYSQL_PASSWORD')
        self.db_name = os.getenv('MYSQL_DATABASE')

        self.conn = mysql.connector.connect(
            host=self.host,
            port=self.port,
            user=self.user,
            password=self.password,
            database=self.db_name
        )
        self.cursor = self.conn.cursor()

    def fetch_queries(self, contact):
        self.cursor.execute("""
            SELECT q.qid, q.attended, q.name,
                a.doctor, a.treatment, a.remarks
            FROM queries q
            LEFT JOIN attended a ON q.qid = a.qid
            WHERE q.contact = %s
            ORDER BY q.received_at DESC
        """, (contact,))

        rows = self.cursor.fetchall()

        result = []
        for row in rows:
            result.append({
                "qid": row[0],
                "attended": bool(row[1]),
                "name": row[2],
                "doctor": row[3],
                "treatment": row[4],
                "remarks": row[5]
            })

        return result

    def insert_patient(self, data):
        treat = data.get('treatment')
        disea = data.get('disease')
        if treat == "":
            treat = 'None'
        if disea == "":
            disea = 'None'
            
        self.cursor.execute("""
        INSERT INTO queries (contact, name, age, gender, temperature, days, contagious, treatment, disease)
        VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s)
        """, (
            data.get('phone'), data.get('name'), data.get('age'), data.get('gender'), data.get('temperature'), 
            data.get('days'), data.get('contagious'), treat, disea
        ))
        self.conn.commit()

    def close(self):
        if self.cursor:
            self.cursor.close()
        if self.conn:
            self.conn.close()