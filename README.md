# LivingRank - Wohnungsbewertung

Anonyme Straßen- und Wohnungsbewertungsplattform für Deutschland und Europa.

## Tech Stack

- **Frontend:** Angular 21, SCSS, PrimeNG (Aura Theme)
- **Backend:** Java 21, Spring Boot 3.4, REST API
- **Datenbank:** PostgreSQL 16 mit Flyway Migrationen
- **Auth:** E-Mail/Passwort (BCrypt) + Google OAuth2, JWT
- **Geocoding:** OpenStreetMap Nominatim (mit DB-Caching)

## Voraussetzungen

- **Java 21** - `brew install openjdk@21`
- **Maven** - `brew install maven`
- **Node.js 20+** - bereits installiert
- **Angular CLI** - bereits installiert
- **PostgreSQL** - bereits installiert
- **Docker** (optional, für MailHog) - `brew install --cask docker`

## Setup

### 1. PostgreSQL Datenbank

Die Datenbank `LiningRank` ist bereits angelegt. Flyway erstellt die Tabellen automatisch beim ersten Start.

Falls noch nicht vorhanden:
```bash
createdb LiningRank
```

### 2. Backend starten

```bash
cd backend
mvn spring-boot:run
```

Das Backend läuft auf `http://localhost:8080`.

Flyway führt automatisch alle Migrationen aus und erstellt die Tabellen:
- `users` - Benutzer (UUID, E-Mail, BCrypt-Hash, Auth-Provider)
- `streets` - Straßen (gecacht aus Nominatim)
- `reviews` - Bewertungen (20 Kriterien + Gesamtwertung, UNIQUE pro User/Straße)
- `email_verification_tokens` - E-Mail-Bestätigungstokens

### 3. Frontend starten

```bash
cd frontend
npm install
ng serve
```

Das Frontend läuft auf `http://localhost:4200`.

### 4. MailHog (optional, für E-Mail-Versand im Dev)

```bash
docker-compose up mailhog
```

Web-UI: `http://localhost:8025`

## Konfiguration

Umgebungsvariablen können in `application.yml` oder als System-Env-Vars gesetzt werden.
Siehe `.env.example` für alle Variablen.

### Google OAuth Setup

1. Google Cloud Console: Neues Projekt erstellen
2. OAuth 2.0 Client-ID erstellen
3. Autorisierte Redirect-URI: `http://localhost:8080/login/oauth2/code/google`
4. Client-ID und Secret in `application.yml` oder als Env-Vars setzen

## API Endpunkte

| Methode | Pfad | Auth | Beschreibung |
|---------|------|------|-------------|
| POST | `/api/auth/register` | - | Registrierung |
| POST | `/api/auth/login` | - | Login, JWT zurück |
| GET | `/api/auth/verify-email?token=` | - | E-Mail bestätigen |
| GET | `/api/streets/search?q=` | - | Straßensuche |
| GET | `/api/streets/{id}` | - | Straßendetails |
| GET | `/api/streets/{id}/reviews` | - | Reviews (paginiert) |
| POST | `/api/streets/{id}/reviews` | JWT | Review erstellen |
| PUT | `/api/streets/{id}/reviews/{rid}` | JWT | Review bearbeiten |
| DELETE | `/api/streets/{id}/reviews/{rid}` | JWT | Review löschen |
| GET | `/api/me` | JWT | Eigenes Profil |
| PUT | `/api/me` | JWT | Profil aktualisieren |
| GET | `/api/me/reviews` | JWT | Eigene Reviews |

## Bewertungskriterien (1-5 Sterne)

| Kriterium | Beschreibung |
|-----------|-------------|
| overall_rating | Gesamtbewertung (Pflichtfeld) |
| damp_in_house | Feuchtigkeit im Haus |
| friendly_neighbors | Freundliche Nachbarn |
| house_condition | Hauszustand |
| infrastructure_connections | Infrastruktur-Anbindung |
| neighbors_in_general | Nachbarn allgemein |
| neighbors_volume | Lautstärke der Nachbarn |
| smells_bad | Geruchsbelästigung |
| thin_walls | Dünne Wände |
| noise_from_street | Straßenlärm |
| public_safety_feeling | Sicherheitsgefühl |
| cleanliness_shared_areas | Sauberkeit Gemeinschaftsflächen |
| parking_situation | Parksituation |
| public_transport_access | ÖPNV-Anbindung |
| internet_quality | Internetqualität |
| pest_issues | Ungeziefer |
| heating_reliability | Heizung Zuverlässigkeit |
| water_pressure_or_quality | Wasser Druck/Qualität |
| value_for_money | Preis-Leistung |

## Sicherheit

- **Passwörter:** BCrypt-Hashing
- **JWT:** Stateless Auth, Token im Authorization Header
- **Rate Limiting:** Bucket4j (10 Anfragen/Minute pro IP für Auth-Endpunkte)
- **CORS:** Nur Frontend-Origin erlaubt
- **XSS:** HTML-Tags werden serverseitig gestripped, Angular escaped automatisch
- **SQL Injection:** JPA/Prepared Statements, kein String-Concat
- **E-Mail-Enumeration:** Generische Antworten bei Registrierung und Login
- **CSRF:** Nicht nötig (stateless JWT)
- **Security Headers:** X-XSS-Protection, X-Content-Type-Options, X-Frame-Options

## Design-Entscheidungen

- **overall_rating:** Vom Nutzer vergeben (nicht berechnet) - gibt dem Nutzer Kontrolle
- **PostgreSQL:** Robuste Volltextsuche, starke Constraints, JSON-Erweiterbarkeit
- **Nominatim:** Kostenfreies Geocoding, Rate-Limit (1 req/sec), Ergebnisse in DB gecacht
- **Profilbilder:** Als URL gespeichert (kein Upload) - einfacher Scope

## Tests

### Backend
```bash
cd backend
mvn test
mvn jacoco:report  # Coverage Report in target/site/jacoco/
```

### Frontend
```bash
cd frontend
ng test --code-coverage
# Coverage Report in coverage/
```

## Projektstruktur

```
LivingRank/
├── docker-compose.yml
├── .env.example
├── .gitignore
├── README.md
├── backend/
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/com/livingrank/
│       │   │   ├── LivingRankApplication.java
│       │   │   ├── config/         (SecurityConfig, RateLimitConfig)
│       │   │   ├── controller/     (Auth, Street, Review, User)
│       │   │   ├── dto/            (Request/Response Records)
│       │   │   ├── entity/         (User, Street, Review, Token)
│       │   │   ├── exception/      (Global Handler, Custom Exceptions)
│       │   │   ├── repository/     (JPA Repositories)
│       │   │   ├── security/       (JWT, OAuth2 Handler)
│       │   │   └── service/        (Auth, Street, Review, User, Email, Nominatim)
│       │   └── resources/
│       │       ├── application.yml
│       │       └── db/migration/   (V1-V4 Flyway SQL)
│       └── test/
└── frontend/
    ├── package.json
    └── src/
        └── app/
            ├── components/     (Header)
            ├── guards/         (Auth Guard)
            ├── interceptors/   (JWT Interceptor)
            ├── models/         (TypeScript Interfaces)
            ├── pages/          (Home, Search, Detail, Review, Auth, Profile)
            └── services/       (Auth, Street, Review)
```
# LivingRank
