
# Hospital Management System — Database Design Document

**Date:** Jan 12, 2026  
**Database:** PostgreSQL  
**Application:** JavaFX + JDBC (DAO/Service/Controller architecture)

---

## 1. Introduction
This document presents the **conceptual**, **logical**, and **physical** data models for the Hospital/Healthcare Management System. The schema is designed for **Third Normal Form (3NF)**, enforces **referential integrity**, and defines **indexes** to accelerate high‑frequency lookups and joins used by the JavaFX application.

**Entities in scope:** Patients, Doctors, Departments, Appointments, Prescriptions, PrescriptionItems, PatientFeedback, MedicalInventory.

---

## 2. Conceptual Data Model (ERD Summary)

**Key entities & relationships:**
- **Patients** — receive care, provide feedback, have prescriptions, schedule appointments.
- **Doctors** — belong to one **Department**, issue **Prescriptions**, attend **Appointments**.
- **Departments** — group doctors; referenced by appointments.
- **Appointments** — link a **Patient**, a **Doctor**, and optionally a **Department**, with date/time, status, and reason.
- **Prescriptions** — issued by a **Doctor** for a **Patient** (optionally tied to an **Appointment**).
- **PrescriptionItems** — medications/dosages within a **Prescription**.
- **PatientFeedback** — rating/comments provided by **Patients**.
- **MedicalInventory** — stock items used by the hospital.

**Cardinality notes:**
- Departments **1..*** Doctors  
- Patients **1..*** Appointments; Doctors **1..*** Appointments  
- Prescriptions **1..*** PrescriptionItems  
- Patients **1..*** Feedback


## 3. Logical Data Model (Data Dictionary)

### 3.1 departments
| Column | Type | Nullability | Constraints | Description |
|---|---|---|---|---|
| id | BIGSERIAL | NOT NULL | PRIMARY KEY | Unique identifier |
| name | VARCHAR(100) | NOT NULL | UNIQUE | Department name |
| description | TEXT | NULL | — | Description of the department |

### 3.2 patients
| Column | Type | Nullability | Constraints | Description |
|---|---|---|---|---|
| id | BIGSERIAL | NOT NULL | PRIMARY KEY | Unique identifier |
| first_name | VARCHAR(50) | NOT NULL | — | Patient first name |
| last_name | VARCHAR(50) | NOT NULL | — | Patient last name |
| gender | CHAR(1) | NOT NULL | CHECK (gender IN ('M','F')) | Gender M/F |
| date_of_birth | DATE | NOT NULL | — | Date of birth |
| phone | VARCHAR(20) | NULL | — | Phone number |
| email | VARCHAR(255) | NULL | UNIQUE | Email address |
| address | TEXT | NULL | — | Residential address |

### 3.3 doctors
| Column | Type | Nullability | Constraints | Description |
|---|---|---|---|---|
| id | BIGSERIAL | NOT NULL | PRIMARY KEY | Unique identifier |
| first_name | VARCHAR(50) | NOT NULL | — | Doctor first name |
| last_name | VARCHAR(50) | NOT NULL | — | Doctor last name |
| gender | CHAR(1) | NOT NULL | CHECK (gender IN ('M','F')) | Gender M/F |
| phone | VARCHAR(20) | NULL | — | Phone number |
| email | VARCHAR(255) | NULL | UNIQUE | Email address |
| department_id | BIGINT | NOT NULL | REFERENCES departments(id) ON DELETE RESTRICT | FK to departments |

### 3.4 appointments
| Column | Type | Nullability | Constraints | Description |
|---|---|---|---|---|
| id | BIGSERIAL | NOT NULL | PRIMARY KEY | Unique identifier |
| patient_id | BIGINT | NOT NULL | REFERENCES patients(id) ON DELETE CASCADE | FK to patients |
| doctor_id | BIGINT | NOT NULL | REFERENCES doctors(id) ON DELETE CASCADE | FK to doctors |
| department_id | BIGINT | NULL | REFERENCES departments(id) ON DELETE SET NULL | FK to departments |
| appointment_at | TIMESTAMP | NOT NULL | — | Appointment date & time |
| status | VARCHAR(20) | NOT NULL | CHECK (status IN ('SCHEDULED','COMPLETED','CANCELLED')) | Status |
| reason | TEXT | NULL | — | Reason/notes |
| created_at | TIMESTAMP | NOT NULL | DEFAULT NOW() | Record creation timestamp |
| updated_at | TIMESTAMP | NOT NULL | DEFAULT NOW() | Last update timestamp |

### 3.5 prescriptions
| Column | Type | Nullability | Constraints | Description |
|---|---|---|---|---|
| id | BIGSERIAL | NOT NULL | PRIMARY KEY | Unique identifier |
| patient_id | BIGINT | NOT NULL | REFERENCES patients(id) ON DELETE CASCADE | FK to patients |
| doctor_id | BIGINT | NOT NULL | REFERENCES doctors(id) ON DELETE CASCADE | FK to doctors |
| appointment_id | BIGINT | NULL | REFERENCES appointments(id) ON DELETE SET NULL | FK to appointments |
| prescribed_at | TIMESTAMP | NOT NULL | DEFAULT NOW() | Prescription timestamp |

### 3.6 prescription_items
| Column | Type | Nullability | Constraints | Description |
|---|---|---|---|---|
| id | BIGSERIAL | NOT NULL | PRIMARY KEY | Unique identifier |
| prescription_id | BIGINT | NOT NULL | REFERENCES prescriptions(id) ON DELETE CASCADE | FK to prescriptions |
| medication_name | VARCHAR(255) | NOT NULL | — | Medication name |
| dosage | VARCHAR(100) | NULL | — | Dosage instructions |
| quantity | INTEGER | NOT NULL | CHECK (quantity >= 0) | Quantity |
| notes | TEXT | NULL | — | Additional notes |

### 3.7 patient_feedback
| Column | Type | Nullability | Constraints | Description |
|---|---|---|---|---|
| id | BIGSERIAL | NOT NULL | PRIMARY KEY | Unique identifier |
| patient_id | BIGINT | NOT NULL | REFERENCES patients(id) ON DELETE CASCADE | FK to patients |
| rating | SMALLINT | NOT NULL | CHECK (rating BETWEEN 1 AND 5) | Rating 1–5 |
| feedback_text | TEXT | NOT NULL | — | Feedback text |
| created_at | TIMESTAMP | NOT NULL | DEFAULT NOW() | Creation timestamp |

### 3.8 medical_inventory
| Column | Type | Nullability | Constraints | Description |
|---|---|---|---|---|
| id | BIGSERIAL | NOT NULL | PRIMARY KEY | Unique identifier |
| item_name | VARCHAR(255) | NOT NULL | UNIQUE | Item name |
| category | VARCHAR(100) | NULL | — | Inventory category |
| quantity | INTEGER | NOT NULL | DEFAULT 0 | Current stock |
| reorder_level | INTEGER | NULL | DEFAULT 0 | Threshold for reordering |
| unit_price | NUMERIC(12,2) | NULL | DEFAULT 0 | Unit price |
| supplier | VARCHAR(255) | NULL | — | Supplier name |

---

## 4. Physical Data Model (PostgreSQL DDL — Excerpts)
Below is an excerpt for **appointments**. The full DDL for all entities is provided in the SQL block in section 9.

```sql
CREATE TABLE IF NOT EXISTS appointments (
    id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
    doctor_id  BIGINT NOT NULL REFERENCES doctors(id) ON DELETE CASCADE,
    department_id BIGINT REFERENCES departments(id) ON DELETE SET NULL,
    appointment_at TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('SCHEDULED','COMPLETED','CANCELLED')),
    reason TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_appointment_doctor_time
ON appointments (doctor_id, appointment_at);
```

---

## 5. Referential Integrity & Constraints
- **Foreign keys** ensure valid links among entities.  
- **CHECK constraints** enforce domain validity (e.g., gender, status, rating).  
- **ON DELETE** policies: 
  - **CASCADE** for patient‑related artifacts (appointments, prescriptions, feedback) to remove dependent rows when a patient is deleted.  
  - **SET NULL** for optional relationships (e.g., appointment’s department).  
  - **RESTRICT** for departments referenced by doctors, to prevent accidental deletion.

---

## 6. Normalization (3NF)
- All attributes are **atomic**.  
- Every non‑key attribute depends on the **whole primary key**.  
- **Transitive dependencies** removed by introducing separate tables (e.g., **Prescription** vs. **PrescriptionItems**).

---

## 7. Performance & Indexing
Indexes are defined on high‑frequency search and join columns:
- **appointments**: `(doctor_id, appointment_at)` **UNIQUE**, plus `patient_id`, `doctor_id`, `department_id`, `appointment_at`, `status`.
- **patients**: `LOWER(first_name)`, `LOWER(last_name)`, and **UNIQUE** on `LOWER(email)`.
- **doctors**: `department_id`, `LOWER(first_name)`, `LOWER(last_name)`, and **UNIQUE** on `LOWER(email)`.
- **prescriptions**/**items**/**feedback**/**inventory**: targeted indexes for frequent joins and filters.

These indexes improve lookup/join performance for the JavaFX UI.

---

## 8. Sample Queries
**Upcoming appointments for a doctor:**
```sql
SELECT a.*
FROM appointments a
WHERE a.doctor_id = :doctorId
  AND a.appointment_at >= NOW()
ORDER BY a.appointment_at;
```

**Doctors per department:**
```sql
SELECT d.name, COUNT(*) AS doctor_count
FROM doctors doc
JOIN departments d ON d.id = doc.department_id
GROUP BY d.name
ORDER BY doctor_count DESC;
```

---

## 9. Full DDL Script (All Tables & Indexes)
> Copy this into a file (e.g., `hospital_schema.sql`) and run in PostgreSQL.

```sql
-- departments
CREATE TABLE IF NOT EXISTS departments (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT
);
CREATE UNIQUE INDEX IF NOT EXISTS uq_departments_name ON departments (LOWER(name));

-- patients
CREATE TABLE IF NOT EXISTS patients (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name  VARCHAR(50) NOT NULL,
    gender CHAR(1) NOT NULL CHECK (gender IN ('M','F')),
    date_of_birth DATE NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(255) UNIQUE,
    address TEXT
);
CREATE INDEX IF NOT EXISTS idx_patients_name ON patients (LOWER(first_name), LOWER(last_name));
CREATE UNIQUE INDEX IF NOT EXISTS uq_patients_email ON patients (LOWER(email));

-- doctors
CREATE TABLE IF NOT EXISTS doctors (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name  VARCHAR(50) NOT NULL,
    gender CHAR(1) NOT NULL CHECK (gender IN ('M','F')),
    phone VARCHAR(20),
    email VARCHAR(255) UNIQUE,
    department_id BIGINT NOT NULL REFERENCES departments(id) ON DELETE RESTRICT
);
CREATE INDEX IF NOT EXISTS idx_doctors_dept ON doctors (department_id);
CREATE INDEX IF NOT EXISTS idx_doctors_name ON doctors (LOWER(first_name), LOWER(last_name));
CREATE UNIQUE INDEX IF NOT EXISTS uq_doctors_email ON doctors (LOWER(email));

-- appointments
CREATE TABLE IF NOT EXISTS appointments (
    id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
    doctor_id  BIGINT NOT NULL REFERENCES doctors(id) ON DELETE CASCADE,
    department_id BIGINT REFERENCES departments(id) ON DELETE SET NULL,
    appointment_at TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('SCHEDULED','COMPLETED','CANCELLED')),
    reason TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE UNIQUE INDEX IF NOT EXISTS uq_appointment_doctor_time ON appointments (doctor_id, appointment_at);
CREATE INDEX IF NOT EXISTS idx_appt_patient ON appointments (patient_id);
CREATE INDEX IF NOT EXISTS idx_appt_doctor ON appointments (doctor_id);
CREATE INDEX IF NOT EXISTS idx_appt_dept ON appointments (department_id);
CREATE INDEX IF NOT EXISTS idx_appt_at ON appointments (appointment_at);
CREATE INDEX IF NOT EXISTS idx_appt_status ON appointments (status);

-- prescriptions
CREATE TABLE IF NOT EXISTS prescriptions (
    id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
    doctor_id  BIGINT NOT NULL REFERENCES doctors(id) ON DELETE CASCADE,
    appointment_id BIGINT REFERENCES appointments(id) ON DELETE SET NULL,
    prescribed_at TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_prescriptions_patient ON prescriptions (patient_id);
CREATE INDEX IF NOT EXISTS idx_prescriptions_doctor ON prescriptions (doctor_id);
CREATE INDEX IF NOT EXISTS idx_prescriptions_appt ON prescriptions (appointment_id);
CREATE INDEX IF NOT EXISTS idx_prescriptions_time ON prescriptions (prescribed_at);

-- prescription_items
CREATE TABLE IF NOT EXISTS prescription_items (
    id BIGSERIAL PRIMARY KEY,
    prescription_id BIGINT NOT NULL REFERENCES prescriptions(id) ON DELETE CASCADE,
    medication_name VARCHAR(255) NOT NULL,
    dosage VARCHAR(100),
    quantity INTEGER NOT NULL CHECK (quantity >= 0),
    notes TEXT
);
CREATE INDEX IF NOT EXISTS idx_items_prescription ON prescription_items (prescription_id);

-- patient_feedback
CREATE TABLE IF NOT EXISTS patient_feedback (
    id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
    rating SMALLINT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    feedback_text TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_feedback_patient ON patient_feedback (patient_id);
CREATE INDEX IF NOT EXISTS idx_feedback_rating ON patient_feedback (rating);

-- medical_inventory
CREATE TABLE IF NOT EXISTS medical_inventory (
    id BIGSERIAL PRIMARY KEY,
    item_name VARCHAR(255) NOT NULL UNIQUE,
    category VARCHAR(100),
    quantity INTEGER NOT NULL DEFAULT 0,
    reorder_level INTEGER DEFAULT 0,
    unit_price NUMERIC(12,2) DEFAULT 0,
    supplier VARCHAR(255)
);
CREATE INDEX IF NOT EXISTS idx_inventory_category ON medical_inventory (LOWER(category));
```

---

## 10. Read‑me Notes
- Ensure `search_path` points to the correct schema (default `public`).
- Run scripts in the order shown to satisfy FK dependencies.
- Seed data can be inserted after creation for testing.

---

*Prepared for integration with your JavaFX Hospital Management System.*
