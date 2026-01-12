# 🏥 Hospital Management System

A **JavaFX-based Hospital Management System** integrated with **PostgreSQL** for managing patients, doctors, departments, appointments, prescriptions, inventory, and reporting. Designed with **MVC + DAO + Service architecture**, normalized to **3NF**, and optimized with **indexes** for performance.

***

## 📌 Features

*   **Dashboard UI** with tabs:
    *   Patients
    *   Doctors
    *   Departments
    *   Appointments
    *   Reports (Charts & Analytics)
*   **CRUD Operations** for all entities:
    *   Add, Edit, Delete, Search (with date range & status filters for Appointments)
*   **Validation** using ValidatorFX
*   **Dynamic Charts** (Patients by Gender, Doctors per Department, Appointment Trends)
*   **Database Integration**:
    *   PostgreSQL schema normalized to 3NF
    *   Parameterized queries via JDBC
*   **Performance Optimization**:
    *   Indexing on high-frequency columns
    *   Search filters and date range queries
*   **Modern UI Styling** with BootstrapFX + custom CSS
*   **Modular Architecture**:
    *   `controller/` — JavaFX controllers
    *   `dao/` — Data Access Objects
    *   `service/` — Business logic layer
    *   `model/` — Entity classes

***

## 🛠 Tech Stack

*   **Java**: OpenJDK 21+
*   **JavaFX**: UI framework
*   **PostgreSQL**: Relational database
*   **HikariCP**: Connection pooling
*   **ControlsFX / ValidatorFX**: UI enhancements & validation
*   **TilesFX**: Dashboard charts
*   **Ikonli**: Icons
*   **BootstrapFX**: Modern UI styling
*   **Maven**: Build & dependency management

***

## 📂 Project Structure

    HospitalManagementSystem/
    ├── src/main/java/com/amalitech/hospitalmanagementsystem/
    │   ├── controller/        # JavaFX controllers
    │   ├── dao/               # DAO interfaces
    │   │   └── impl/          # DAO implementations
    │   ├── service/           # Service interfaces
    │   │   └── impl/          # Service implementations
    │   ├── model/             # Entity classes
    │   └── config/            # DataSourceProvider (HikariCP)
    ├── src/main/resources/view/   # FXML files
    ├── src/main/resources/css/    # Stylesheets
    ├── docs/                      # Documentation (ERD, design docs, schema)
    │   ├── Hospital_DB_Design_Document.md
    │   ├── hospital_schema.sql
    │   └── HMS_ERD.png
    ├── screenshots/               # README screenshots & demo
    │   ├── patients_tab.png
    │   ├── departments_tab.png
    │   ├── reports_tab.png
    │   └── demo.gif
    └── pom.xml                    # Maven configuration

***

## ⚙️ Setup Instructions

### 1) Clone the Repository

```bash
git clone https://github.com/your-username/HospitalManagementSystem.git
cd HospitalManagementSystem
```

### 2) Configure Database

*   Create the database:

```sql
CREATE DATABASE hospital_db;
```

*   Run the schema script:

```bash
psql -U postgres -d hospital_db -f docs/hospital_schema.sql
```

### 3) Update DB Credentials

Edit `src/main/java/com/amalitech/hospitalmanagementsystem/config/DataSourceProvider.java`:

```java
cfg.setJdbcUrl("jdbc:postgresql://localhost:5432/hospital_db");
cfg.setUsername("postgres");
cfg.setPassword("your_password");
```

### 4) Build & Run

```bash
mvn clean javafx:run
```

> If you use modules, ensure `module-info.java` includes:
>
> ```java
> requires org.kordamp.bootstrapfx.core;
> requires org.controlsfx.controls;
> requires com.dlsc.formsfx;
> requires net.synedra.validatorfx;
> requires org.kordamp.ikonli.javafx;
> requires eu.hansolo.tilesfx;
> requires com.almasb.fxgl.all; // if used
> ```

## 🖼 Screenshots
**Patients Tab**  
<img width="768" height="472" alt="ty1" src="https://github.com/user-attachments/assets/caf6955c-4ec9-4ec7-9f54-0bc289c52f7d" />

**Departments Tab**  
<img width="771" height="462" alt="2222" src="https://github.com/user-attachments/assets/3565caee-36eb-4a50-9db9-d16dfa6a7096" />

**Reports Tab**  
<img width="775" height="448" alt="1111" src="https://github.com/user-attachments/assets/598ffec7-7fd8-4a64-ac09-719f7f8af412" />


## 📄 Documentation

*   **Database Design (Markdown)**: `docs/Hospital_DB_Design_Document.md`
*   **PostgreSQL DDL**: `docs/hospital_schema.sql`
*   **ERD (PNG)**: `docs/HMS_ERD.png`

***

## ✅ Deliverables Check

*   **Database Design Document** — Conceptual, logical, physical models (with ERD & explanations)
*   **SQL Implementation Script** — Full PostgreSQL DDL
*   **JavaFX Application** — CRUD, search, reporting via JDBC
*   **Performance Report** — Indexing & search optimization *(in progress)*
*   **README.md** — Complete setup & documentation

***

## 🔮 Roadmap / Next Steps

*   Optional **NoSQL module** for patient notes
*   **Pagination** for large tables
*   **CSV/JSON export/import**
*   **Unit tests** with JUnit & Mockito (80%+ coverage target)
*   **CI/CD** workflow (Maven + GitHub Actions)


## 🤝 Contributing

1.  Fork the repo
2.  Create a feature branch (`git checkout -b feature/name`)
3.  Commit changes (`git commit -m "feat: add ... "`)
4.  Push (`git push origin feature/name`)
5.  Open a Pull Request

## 👨‍💻 Author

**Lennox Owusu Afriyie**  
National Service Personnel



