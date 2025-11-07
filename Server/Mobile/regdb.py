import os
import mysql.connector
from datetime import datetime

class PatientDB:
    def __init__(self):
        self.host = os.getenv('MYSQL_HOST')
        self.port = int(os.getenv('MYSQL_PORT'))
        self.user = os.getenv('MYSQL_USER')
        self.password = os.getenv('MYSQL_PASSWORD')
        self.db_name = os.getenv('MYSQL_DATABASE')

        temp_conn = mysql.connector.connect(
            host=self.host,
            port=self.port,
            user=self.user,
            password=self.password
        )
        temp_cursor = temp_conn.cursor()
        
        temp_cursor.execute(f"CREATE DATABASE IF NOT EXISTS `{self.db_name}` DEFAULT CHARACTER SET = utf8mb4 DEFAULT COLLATE = utf8mb4_unicode_ci")
        temp_conn.commit()
        temp_cursor.close()
        temp_conn.close()

        self.conn = mysql.connector.connect(
            host=self.host,
            port=self.port,
            user=self.user,
            password=self.password,
            database=self.db_name
        )
        self.cursor = self.conn.cursor()

        self.cursor.execute("""
        CREATE TABLE IF NOT EXISTS `patients` (
            id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
            name VARCHAR(255) NOT NULL,
            age VARCHAR(20),
            gender VARCHAR(50),
            temperature VARCHAR(50),
            days VARCHAR(50),
            contagious VARCHAR(50),
            received_at DATETIME DEFAULT CURRENT_TIMESTAMP
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
        """)
        self.conn.commit()

    def insert_patient(self, data):
        self.cursor.execute("""
        INSERT INTO patients (name, age, gender, temperature, days, contagious)
        VALUES (%s, %s, %s, %s, %s, %s)
        """, (
            data.get('name'), data.get('age'), data.get('gender'),
            data.get('temperature'), data.get('days'), data.get('contagious')
        ))
        self.conn.commit()

    def close(self):
        if self.cursor:
            self.cursor.close()
        if self.conn:
            self.conn.close()