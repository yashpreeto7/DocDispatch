import mysql.connector

print("=== MySQL Database & Table Setup ===")
host = input("MySQL Host (default: localhost): ").strip() or "localhost"
port_str = input("MySQL Port (default: 3306): ").strip() or "3306"
user = input("MySQL User: ").strip()
password = input("MySQL Password: ")
database = input("Database Name (default: docdispatch): ").strip() or "docdispatch"

if not user:
    print("Error: MySQL user is required.")
    exit(1)

try:
    port = int(port_str)
except ValueError:
    print("Error: Port must be a number.")
    exit(1)

print(f"\nConnecting to MySQL at {host}:{port} as '{user}'...")

temp_conn = mysql.connector.connect(
    host=host,
    port=port,
    user=user,
    password=password
)
temp_cursor = temp_conn.cursor()

print(f"Creating database `{database}` (if not exists)...")
temp_cursor.execute(f"""
    CREATE DATABASE IF NOT EXISTS `{database}`
    DEFAULT CHARACTER SET = utf8mb4
    DEFAULT COLLATE = utf8mb4_unicode_ci
""")
temp_conn.commit()
temp_cursor.close()
temp_conn.close()

conn = mysql.connector.connect(
    host=host,
    port=port,
    user=user,
    password=password,
    database=database
)
cursor = conn.cursor()

print("Creating table `queries`...")
cursor.execute("""
CREATE TABLE IF NOT EXISTS `queries` (
    qid INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    contact VARCHAR(15) NOT NULL,
    name VARCHAR(255) NOT NULL,
    age VARCHAR(3),
    gender VARCHAR(6),
    temperature VARCHAR(2),
    days VARCHAR(1),
    contagious VARCHAR(3),
    treatment VARCHAR(100),
    disease VARCHAR(20),
    attended BOOLEAN DEFAULT FALSE,
    received_at DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
""")

print("Creating table `attended`...")
cursor.execute("""
CREATE TABLE IF NOT EXISTS `attended` (
    qid INT PRIMARY KEY,
    contact VARCHAR(15),
    doctor VARCHAR(255),
    treatment VARCHAR(255),
    remarks VARCHAR(255),
    attended_at DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
""")

conn.commit()
cursor.close()
conn.close()

print("\n✅ Success! Database and tables created.")
print(f"Database: `{database}`")
print("Tables: `queries`, `attended`")