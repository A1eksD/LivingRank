# LivingRank - Projektdokumentation

Anonyme Bewertungsplattform fuer Strassen und Wohnungen in Deutschland, Oesterreich, Schweiz und weiteren europaeischen Laendern.

---

## Inhaltsverzeichnis

1. [Technologie-Stack](#1-technologie-stack)
2. [Projektstruktur](#2-projektstruktur)
3. [Backend-Architektur](#3-backend-architektur)
4. [Datenbank](#4-datenbank)
5. [API-Endpunkte](#5-api-endpunkte)
6. [Sicherheitsarchitektur](#6-sicherheitsarchitektur)
7. [Admin-System](#7-admin-system)
8. [Frontend-Architektur](#8-frontend-architektur)
9. [Konfiguration](#9-konfiguration)
10. [Abhaengigkeiten](#10-abhaengigkeiten)

---

## 1. Technologie-Stack

| Bereich | Technologie | Version |
|---------|-------------|---------|
| Backend | Java | 21 |
| Backend | Spring Boot | 3.4.1 |
| Backend | Spring Security | 6.x |
| Backend | Spring Data JPA | 3.x |
| Datenbank | PostgreSQL | 16+ |
| Migrationen | Flyway | 10.x |
| Auth | JWT (jjwt) | 0.12.6 |
| Auth | OAuth2 (Google) | Spring OAuth2 Client |
| Rate Limiting | Bucket4j | 8.10.1 |
| Frontend | Angular | 21.1 |
| Frontend | TypeScript | 5.9.2 |
| UI-Framework | PrimeNG | 21.1.1 |
| Icons | PrimeIcons | 7.0.0 |
| Build (Backend) | Maven | 3.9+ |
| Build (Frontend) | Angular CLI | 21.1.3 |
| Tests (Backend) | JUnit 5, Testcontainers | - |
| Tests (Frontend) | Vitest | 4.0.8 |
| Code Coverage | JaCoCo | 0.8.12 |

---

## 2. Projektstruktur

```
LivingRank/
├── docker-compose.yml
│
├── backend/
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/com/livingrank/
│       │   │   ├── LivingRankApplication.java
│       │   │   ├── config/
│       │   │   │   ├── SecurityConfig.java
│       │   │   │   └── RateLimitConfig.java
│       │   │   ├── controller/
│       │   │   │   ├── AuthController.java
│       │   │   │   ├── ReviewController.java
│       │   │   │   ├── StreetController.java
│       │   │   │   ├── UserController.java
│       │   │   │   └── admin/
│       │   │   │       ├── AdminAuditController.java
│       │   │   │       ├── AdminDashboardController.java
│       │   │   │       ├── AdminMailController.java
│       │   │   │       ├── AdminReviewController.java
│       │   │   │       ├── AdminScheduledActionController.java
│       │   │   │       └── AdminUserController.java
│       │   │   ├── dto/
│       │   │   │   ├── AuthResponse.java
│       │   │   │   ├── CriteriaAverages.java
│       │   │   │   ├── LoginRequest.java
│       │   │   │   ├── MessageResponse.java
│       │   │   │   ├── RegisterRequest.java
│       │   │   │   ├── ReviewRequest.java
│       │   │   │   ├── ReviewResponse.java
│       │   │   │   ├── StreetDetailResponse.java
│       │   │   │   ├── StreetResponse.java
│       │   │   │   ├── UpdateProfileRequest.java
│       │   │   │   ├── UserResponse.java
│       │   │   │   └── admin/
│       │   │   │       ├── AdminDashboardResponse.java
│       │   │   │       ├── AdminMailRequest.java
│       │   │   │       ├── AdminMailResponse.java
│       │   │   │       ├── AdminReviewResponse.java
│       │   │   │       ├── AdminUserResponse.java
│       │   │   │       ├── AdminUserUpdateRequest.java
│       │   │   │       ├── AuditLogResponse.java
│       │   │   │       ├── ChangeRoleRequest.java
│       │   │   │       ├── ExtendDeadlineRequest.java
│       │   │   │       ├── ScheduledActionResponse.java
│       │   │   │       └── SuspendUserRequest.java
│       │   │   ├── entity/
│       │   │   │   ├── AdminAuditLog.java
│       │   │   │   ├── AdminMail.java
│       │   │   │   ├── AdminScheduledAction.java
│       │   │   │   ├── AuthProvider.java
│       │   │   │   ├── EmailVerificationToken.java
│       │   │   │   ├── Review.java
│       │   │   │   ├── Role.java
│       │   │   │   ├── Street.java
│       │   │   │   ├── User.java
│       │   │   │   └── UserStatus.java
│       │   │   ├── exception/
│       │   │   │   ├── BadRequestException.java
│       │   │   │   ├── ConflictException.java
│       │   │   │   ├── GlobalExceptionHandler.java
│       │   │   │   ├── RateLimitException.java
│       │   │   │   └── ResourceNotFoundException.java
│       │   │   ├── repository/
│       │   │   │   ├── AdminAuditLogRepository.java
│       │   │   │   ├── AdminMailRepository.java
│       │   │   │   ├── AdminScheduledActionRepository.java
│       │   │   │   ├── EmailVerificationTokenRepository.java
│       │   │   │   ├── ReviewRepository.java
│       │   │   │   ├── StreetRepository.java
│       │   │   │   └── UserRepository.java
│       │   │   ├── scheduler/
│       │   │   │   └── AdminDeadlineScheduler.java
│       │   │   ├── security/
│       │   │   │   ├── AdminRateLimitFilter.java
│       │   │   │   ├── JwtAuthenticationFilter.java
│       │   │   │   ├── JwtTokenProvider.java
│       │   │   │   └── OAuth2SuccessHandler.java
│       │   │   └── service/
│       │   │       ├── AuthService.java
│       │   │       ├── EmailService.java
│       │   │       ├── NominatimService.java
│       │   │       ├── ReviewService.java
│       │   │       ├── StreetService.java
│       │   │       ├── UserService.java
│       │   │       └── admin/
│       │   │           ├── AdminAuditService.java
│       │   │           ├── AdminMailService.java
│       │   │           ├── AdminReviewService.java
│       │   │           ├── AdminScheduledActionService.java
│       │   │           └── AdminUserService.java
│       │   └── resources/
│       │       ├── application.yml
│       │       └── db/migration/
│       │           ├── V1__create_users_table.sql
│       │           ├── V2__create_streets_table.sql
│       │           ├── V3__create_reviews_table.sql
│       │           ├── V4__create_email_verification_tokens_table.sql
│       │           ├── V5__add_user_role_and_status.sql
│       │           ├── V6__create_admin_audit_log.sql
│       │           ├── V7__create_admin_scheduled_actions.sql
│       │           └── V8__create_admin_mails.sql
│       └── test/
│           └── java/com/livingrank/
│               ├── LivingRankApplicationTests.java
│               ├── controller/
│               │   ├── AuthControllerTest.java
│               │   └── StreetControllerTest.java
│               ├── dto/
│               │   └── CriteriaAveragesTest.java
│               ├── security/
│               │   └── JwtTokenProviderTest.java
│               └── service/
│                   ├── AuthServiceTest.java
│                   ├── ReviewServiceTest.java
│                   ├── StreetServiceTest.java
│                   └── UserServiceTest.java
│
└── frontend/
    ├── package.json
    ├── angular.json
    ├── tsconfig.json
    ├── tsconfig.app.json
    ├── tsconfig.spec.json
    └── src/
        ├── main.ts
        ├── environments/
        │   ├── environment.ts
        │   └── environment.prod.ts
        └── app/
            ├── app.ts
            ├── app.routes.ts
            ├── app.config.ts
            ├── components/
            │   └── header/
            │       └── header.component.ts
            ├── guards/
            │   ├── auth.guard.ts
            │   └── admin.guard.ts
            ├── interceptors/
            │   └── auth.interceptor.ts
            ├── models/
            │   ├── admin.model.ts
            │   ├── review.model.ts
            │   ├── street.model.ts
            │   └── user.model.ts
            ├── pages/
            │   ├── home/
            │   ├── login/
            │   ├── register/
            │   ├── verify-email/
            │   ├── oauth-callback/
            │   ├── profile/
            │   ├── search-results/
            │   ├── street-detail/
            │   ├── review-form/
            │   └── admin/
            │       ├── admin-dashboard/
            │       ├── admin-users/
            │       ├── admin-reviews/
            │       ├── admin-mails/
            │       ├── admin-audit/
            │       └── admin-scheduled-actions/
            └── services/
                ├── admin.service.ts
                ├── auth.service.ts
                ├── review.service.ts
                └── street.service.ts
```

---

## 3. Backend-Architektur

### 3.1 Schichtenmodell

```
HTTP Request
    │
    ▼
┌──────────────────┐
│   Controller     │   REST-Endpunkte, Request/Response Mapping
├──────────────────┤
│   Service        │   Geschaeftslogik, Validierung, Transaktionen
├──────────────────┤
│   Repository     │   Datenbankzugriff via Spring Data JPA
├──────────────────┤
│   Entity         │   JPA-Entities, Datenbankmodell
├──────────────────┤
│   PostgreSQL     │   Datenbank mit Flyway-Migrationen
└──────────────────┘
```

### 3.2 Entities

#### User
| Feld | Typ | Beschreibung |
|------|-----|-------------|
| id | UUID | Primaerschluessel |
| email | String | Eindeutig, Login-Identifier |
| displayName | String | Anzeigename (max. 100 Zeichen) |
| passwordHash | String | BCrypt-Hash (null bei OAuth) |
| authProvider | AuthProvider | LOCAL oder GOOGLE |
| role | Role | USER, ADMIN, SUPER_ADMIN |
| status | UserStatus | ACTIVE, SUSPENDED, DELETED |
| emailVerified | boolean | E-Mail bestaetigt? |
| profileImageUrl | String | Profilbild-URL |
| suspendedAt | LocalDateTime | Zeitpunkt der Sperrung |
| suspendedReason | String | Sperrgrund (max. 500 Zeichen) |
| lastLoginAt | LocalDateTime | Letzter Login |
| createdAt | LocalDateTime | Erstellungszeitpunkt |
| updatedAt | LocalDateTime | Letzte Aenderung |

#### Street
| Feld | Typ | Beschreibung |
|------|-----|-------------|
| id | Long | Primaerschluessel |
| streetName | String | Strassenname |
| postalCode | String | Postleitzahl |
| city | String | Stadt |
| stateRegion | String | Bundesland/Region |
| country | String | Land (Standard: DE) |
| lat | Double | Breitengrad |
| lon | Double | Laengengrad |
| createdAt | LocalDateTime | Erstellungszeitpunkt |
| updatedAt | LocalDateTime | Letzte Aenderung |

Eindeutigkeitsregel: (streetName + postalCode + city + country) muss eindeutig sein.

#### Review
| Feld | Typ | Beschreibung |
|------|-----|-------------|
| id | Long | Primaerschluessel |
| street | Street (FK) | Bewertete Strasse |
| user | User (FK) | Bewertender Nutzer |
| overallRating | Integer | Gesamtbewertung 1-5 |
| visible | boolean | Sichtbar fuer Nutzer? |
| comment | String | Freitext (max. 2000 Zeichen) |
| createdAt | LocalDateTime | Erstellungszeitpunkt |
| updatedAt | LocalDateTime | Letzte Aenderung |

18 Einzelkriterien (jeweils Integer, 1-5):

| Kriterium | DB-Spalte |
|-----------|-----------|
| Feuchtigkeit im Haus | damp_in_house |
| Freundliche Nachbarn | friendly_neighbors |
| Hauszustand | house_condition |
| Infrastrukturanbindung | infrastructure_connections |
| Nachbarn allgemein | neighbors_in_general |
| Nachbarschaftslaermpegel | neighbors_volume |
| Unangenehme Gerueche | smells_bad |
| Hellhoerige Waende | thin_walls |
| Strassenlaerm | noise_from_street |
| Oeffentl. Sicherheitsgefuehl | public_safety_feeling |
| Sauberkeit Gemeinschaftsflaechen | cleanliness_shared_areas |
| Parksituation | parking_situation |
| OEPNV-Anbindung | public_transport_access |
| Internetqualitaet | internet_quality |
| Schaedlingsprobleme | pest_issues |
| Heizungszuverlaessigkeit | heating_reliability |
| Wasserdruck/-qualitaet | water_pressure_or_quality |
| Preis-Leistungs-Verhaeltnis | value_for_money |

Eindeutigkeitsregel: Pro User maximal eine Bewertung pro Strasse.

#### AdminAuditLog
| Feld | Typ | Beschreibung |
|------|-----|-------------|
| id | Long | Primaerschluessel |
| admin | User (FK) | Ausfuehrender Admin |
| action | String | Aktionstyp (z.B. USER_SUSPENDED) |
| targetType | String | Zieltyp (USER, REVIEW, SCHEDULED_ACTION) |
| targetId | String | ID des Ziels |
| details | String | JSON-Detailinformationen |
| ipAddress | String | IP-Adresse des Admins |
| createdAt | LocalDateTime | Zeitstempel |

#### AdminMail
| Feld | Typ | Beschreibung |
|------|-----|-------------|
| id | Long | Primaerschluessel |
| admin | User (FK) | Absender (Admin) |
| recipient | User (FK) | Empfaenger |
| subject | String | Betreff |
| body | String | Nachrichtentext |
| hasDeadline | boolean | Hat Frist? |
| deadlineAction | String | Aktion nach Fristablauf |
| sentAt | LocalDateTime | Sendezeitpunkt |

#### AdminScheduledAction
| Feld | Typ | Beschreibung |
|------|-----|-------------|
| id | Long | Primaerschluessel |
| admin | User (FK) | Erstellender Admin |
| targetUser | User (FK) | Betroffener User |
| actionType | String | SUSPEND, DELETE, HIDE_REVIEWS |
| reason | String | Begruendung |
| deadline | LocalDateTime | Fristablauf |
| executed | boolean | Bereits ausgefuehrt? |
| executedAt | LocalDateTime | Ausfuehrungszeitpunkt |
| cancelled | boolean | Abgebrochen? |
| cancelledAt | LocalDateTime | Abbruchzeitpunkt |
| relatedMail | AdminMail (FK) | Zugehoerige Mail |
| createdAt | LocalDateTime | Erstellungszeitpunkt |

#### EmailVerificationToken
| Feld | Typ | Beschreibung |
|------|-----|-------------|
| id | Long | Primaerschluessel |
| user | User (FK) | Zugehoeriger User |
| token | String | Eindeutiger Token-String |
| expiresAt | LocalDateTime | Ablaufdatum (24h) |
| used | boolean | Bereits verwendet? |
| createdAt | LocalDateTime | Erstellungszeitpunkt |

### 3.3 Enums

```java
enum Role          { USER, ADMIN, SUPER_ADMIN }
enum UserStatus    { ACTIVE, SUSPENDED, DELETED }
enum AuthProvider  { LOCAL, GOOGLE }
```

### 3.4 DTOs

Alle DTOs sind Java Records.

**Authentifizierung:**
- `LoginRequest(email, password)`
- `RegisterRequest(email, password, displayName)` mit Bean Validation
- `AuthResponse(token, type, user)`

**User:**
- `UserResponse(id, email, displayName, authProvider, emailVerified, profileImageUrl, role, status)`
- `UpdateProfileRequest(displayName, profileImageUrl)`

**Review:**
- `ReviewRequest(overallRating, 18 Kriterien, comment)` mit Bean Validation
- `ReviewResponse(id, streetId, overallRating, 18 Kriterien, comment, createdAt, updatedAt)`

**Strasse:**
- `StreetResponse(id, streetName, postalCode, city, stateRegion, country, lat, lon, averageRating, reviewCount)`
- `StreetDetailResponse(street, criteriaAverages, userHasReviewed)`
- `CriteriaAverages(18 Durchschnittswerte)`

**Admin:**
- `AdminDashboardResponse(totalUsers, activeUsers, suspendedUsers, totalReviews, hiddenReviews, pendingScheduledActions, totalStreets)`
- `AdminUserResponse(id, email, displayName, ..., role, status, suspendedAt, suspendedReason, createdAt, updatedAt)`
- `AdminUserUpdateRequest(displayName, email)`
- `AdminReviewResponse(id, streetId, streetName, streetCity, userId, userEmail, overallRating, comment, visible, ...)`
- `AdminMailRequest(recipientId, subject, body, deadline, deadlineAction)`
- `AdminMailResponse(id, adminId, adminDisplayName, recipientId, recipientEmail, subject, body, hasDeadline, deadlineAction, sentAt)`
- `AuditLogResponse(id, adminId, adminDisplayName, action, targetType, targetId, details, ipAddress, createdAt)`
- `ScheduledActionResponse(id, adminId, adminDisplayName, targetUserId, targetUserEmail, actionType, reason, deadline, executed, cancelled, ...)`
- `SuspendUserRequest(reason)` mit Validierung
- `ChangeRoleRequest(role)` mit Validierung
- `ExtendDeadlineRequest(newDeadline)` mit @Future

**Allgemein:**
- `MessageResponse(message)`

### 3.5 Services

#### AuthService
- `register()` - Registrierung mit Rate-Limit-Pruefung, E-Mail-Verifikation
- `login()` - Login mit Credential-Check, Status-Pruefung, JWT-Generierung
- `verifyEmail()` - Token-Validierung und E-Mail-Bestaetigung

#### UserService
- `getProfile()` - Eigenes Profil abrufen
- `updateProfile()` - Profil aktualisieren (mit HTML-Sanitisierung)

#### StreetService
- `searchStreets()` - Suche in DB, bei Bedarf Nominatim-API-Abfrage mit Caching
- `getStreetDetail()` - Strassendetails mit Durchschnittsbewertungen

#### NominatimService
- `searchAndCache()` - Geocoding-Abfrage an OpenStreetMap Nominatim, Rate-Limited (1 req/s)
- Durchsucht DE, AT, CH, NL, BE, FR, PL, CZ

#### ReviewService
- `getReviewsForStreet()` - Reviews paginiert nach Strasse
- `createReview()` - Neue Bewertung (max. 1 pro User/Strasse)
- `updateReview()` - Eigene Bewertung bearbeiten
- `deleteReview()` - Eigene Bewertung loeschen

#### EmailService
- `sendVerificationEmail()` - Verifikationsmail (deutsch)
- `sendAdminMail()` - Admin-Mail an User

#### AdminUserService
- `getUsers()` - User-Liste mit Filter (Status, Suche) und Paginierung
- `getUser()` - Einzelnen User abrufen
- `updateUser()` - User bearbeiten (mit Audit-Log)
- `suspendUser()` - User sperren (Schutz: kein Self-Action, keine hoehere Rolle)
- `unsuspendUser()` - Sperre aufheben
- `softDeleteUser()` - User soft-loeschen
- `changeRole()` - Rolle aendern (nur SUPER_ADMIN)
- `hideAllReviewsOfUser()` - Alle Reviews eines Users ausblenden

#### AdminReviewService
- `getReviews()` - Reviews mit Filtern (Strasse, User, Sichtbarkeit)
- `hideReview()` / `showReview()` - Sichtbarkeit umschalten
- `deleteReview()` - Review permanent loeschen

#### AdminMailService
- `sendMail()` - Mail senden, optional mit Frist und automatischer Aktion
- `getMails()` - Mailhistorie abrufen

#### AdminScheduledActionService
- `getActions()` - Frist-Aktionen auflisten (optional nur offene)
- `cancelAction()` - Offene Aktion abbrechen
- `extendDeadline()` - Frist verlaengern
- `countPending()` - Anzahl offener Aktionen

#### AdminAuditService
- `log()` - Admin-Aktion protokollieren (eigene Transaktion mit REQUIRES_NEW)
- `getAuditLog()` - Gefilterte Audit-Logs abrufen
- `getAllAuditLogs()` - Alle Logs paginiert

### 3.6 Scheduler

#### AdminDeadlineScheduler
- Laeuft alle 60 Sekunden (`@Scheduled`)
- Findet ueberfaellige, nicht ausgefuehrte und nicht abgebrochene Aktionen
- Fuehrt je nach `actionType` aus:
  - **SUSPEND** → User wird gesperrt
  - **DELETE** → User wird soft-geloescht
  - **HIDE_REVIEWS** → Alle Reviews des Users werden ausgeblendet
- Markiert Aktion als ausgefuehrt und protokolliert im Audit-Log

### 3.7 Exception Handling

| Exception | HTTP-Status | Einsatz |
|-----------|-------------|---------|
| BadRequestException | 400 | Ungueltige Eingaben |
| ResourceNotFoundException | 404 | Entitaet nicht gefunden |
| ConflictException | 409 | Duplikate, Konflikte |
| RateLimitException | 429 | Rate-Limit ueberschritten |

`GlobalExceptionHandler` faengt zusaetzlich:
- `MethodArgumentNotValidException` → 400 mit Feld-Fehlern
- `DataIntegrityViolationException` → 409 (z.B. doppelte Bewertung)
- Generische Exceptions → 500

---

## 4. Datenbank

### 4.1 ER-Diagramm (vereinfacht)

```
┌───────────┐     ┌───────────┐     ┌───────────┐
│   users   │──┐  │  streets  │     │  reviews  │
│           │  │  │           │◄────│           │
│  id (UUID)│  │  │  id (LONG)│     │  id (LONG)│
│  email    │  │  │  name     │     │  street_id│
│  role     │  └──│           │     │  user_id  │──┐
│  status   │     └───────────┘     │  rating   │  │
└─────┬─────┘                       │  visible  │  │
      │                             └───────────┘  │
      │                                            │
      │     ┌────────────────────┐                 │
      ├────►│  admin_audit_log   │                 │
      │     │  admin_id (FK)     │                 │
      │     │  action, target    │                 │
      │     └────────────────────┘                 │
      │                                            │
      │     ┌────────────────────┐                 │
      ├────►│  admin_mails       │◄────────────────┘
      │     │  admin_id (FK)     │
      │     │  recipient_id (FK) │
      │     └────────┬───────────┘
      │              │
      │     ┌────────▼───────────┐
      └────►│ admin_scheduled_   │
            │ actions            │
            │  admin_id (FK)     │
            │  target_user_id(FK)│
            │  related_mail_id   │
            └────────────────────┘
```

### 4.2 Flyway-Migrationen

| Version | Datei | Beschreibung |
|---------|-------|-------------|
| V1 | create_users_table | Users-Tabelle mit UUID-PK, E-Mail, Auth-Felder |
| V2 | create_streets_table | Streets-Tabelle mit Geo-Daten, Unique-Constraint |
| V3 | create_reviews_table | Reviews mit 18 Kriterien, Unique (street+user) |
| V4 | create_email_verification_tokens | Token-Tabelle fuer E-Mail-Verifikation |
| V5 | add_user_role_and_status | role + status Spalten auf Users, visible auf Reviews |
| V6 | create_admin_audit_log | Audit-Log-Tabelle mit Indizes |
| V7 | create_admin_scheduled_actions | Frist-Aktionen mit Deadline-Index |
| V8 | create_admin_mails | Admin-Mails mit FK zu Scheduled Actions |

### 4.3 Indizes

**users:** role, status
**streets:** city, (street_name + city), UNIQUE(street_name + postal_code + city + country)
**reviews:** street_id, user_id, visible, UNIQUE(street_id + user_id)
**admin_audit_log:** admin_id, (target_type + target_id), action, created_at DESC
**admin_scheduled_actions:** deadline (WHERE executed=false AND cancelled=false), target_user_id, admin_id
**admin_mails:** recipient_id, admin_id

---

## 5. API-Endpunkte

### 5.1 Oeffentlich (kein Token noetig)

| Methode | Pfad | Beschreibung |
|---------|------|-------------|
| POST | /api/auth/register | Registrierung |
| POST | /api/auth/login | Login → JWT |
| GET | /api/auth/verify-email?token= | E-Mail bestaetigen |
| GET | /api/streets/search?q= | Strassensuche |
| GET | /api/streets/{id} | Strassendetails |

### 5.2 Authentifiziert (JWT noetig, Rolle: USER+)

| Methode | Pfad | Beschreibung |
|---------|------|-------------|
| GET | /api/me | Eigenes Profil |
| PUT | /api/me | Profil aktualisieren |
| GET | /api/me/reviews | Eigene Bewertungen |
| GET | /api/streets/{id}/reviews | Reviews einer Strasse |
| POST | /api/streets/{id}/reviews | Neue Bewertung |
| PUT | /api/streets/{id}/reviews/{rid} | Bewertung bearbeiten |
| DELETE | /api/streets/{id}/reviews/{rid} | Bewertung loeschen |

### 5.3 Admin (JWT noetig, Rolle: ADMIN oder SUPER_ADMIN)

| Methode | Pfad | Beschreibung |
|---------|------|-------------|
| GET | /api/admin/dashboard | Dashboard-Statistiken |
| GET | /api/admin/users | User-Liste (Filter: status, search) |
| GET | /api/admin/users/{id} | User-Details |
| PUT | /api/admin/users/{id} | User bearbeiten |
| POST | /api/admin/users/{id}/suspend | User sperren |
| POST | /api/admin/users/{id}/unsuspend | Sperre aufheben |
| DELETE | /api/admin/users/{id} | User loeschen (**nur SUPER_ADMIN**) |
| PUT | /api/admin/users/{id}/role | Rolle aendern (**nur SUPER_ADMIN**) |
| GET | /api/admin/reviews | Reviews (Filter: streetId, userId, visible) |
| GET | /api/admin/reviews/{id} | Review-Details |
| POST | /api/admin/reviews/{id}/hide | Review ausblenden |
| POST | /api/admin/reviews/{id}/show | Review einblenden |
| DELETE | /api/admin/reviews/{id} | Review loeschen |
| POST | /api/admin/mails | Mail an User senden |
| GET | /api/admin/mails | Mailhistorie |
| GET | /api/admin/mails/{id} | Mail-Details |
| GET | /api/admin/audit | Audit-Log (Filter: adminId, action, targetType, from, to) |
| GET | /api/admin/scheduled-actions | Frist-Aktionen (Filter: pendingOnly) |
| POST | /api/admin/scheduled-actions/{id}/cancel | Aktion abbrechen |
| PUT | /api/admin/scheduled-actions/{id}/extend | Frist verlaengern |

---

## 6. Sicherheitsarchitektur

### 6.1 Authentifizierung

```
┌─────────────┐     ┌──────────────────┐     ┌────────────────┐
│   Client     │────►│ JwtAuthFilter    │────►│ SecurityContext │
│  (Angular)   │     │                  │     │                │
│              │     │ 1. Token extrah. │     │ User + Role    │
│ Bearer Token │     │ 2. Token valid.  │     │                │
│ im Header    │     │ 3. User aus DB   │     └────────────────┘
└─────────────┘     │ 4. Role-Check    │
                    │ 5. Status-Check  │
                    └──────────────────┘
```

### 6.2 JWT-Token

- **Algorithmus:** HMAC-SHA256
- **Laufzeit User-Token:** 24 Stunden
- **Laufzeit Admin-Token:** 1 Stunde
- **Claims:** userId, email, role, iat, exp

### 6.3 Rollenhierarchie

```
SUPER_ADMIN (ordinal 2)
    │
    ▼
  ADMIN (ordinal 1)
    │
    ▼
  USER (ordinal 0)
```

Regeln:
- Admins koennen keine Aktionen auf User mit gleicher oder hoeherer Rolle ausfuehren
- Admins koennen keine Aktionen auf sich selbst ausfuehren
- Token-Rolle muss mit DB-Rolle uebereinstimmen (Role Escalation Protection)
- Gesperrte/geloeschte User werden komplett blockiert

### 6.4 Rate Limiting

| Bereich | Limit | Fenster |
|---------|-------|---------|
| Allgemein (Auth) | 10 Requests | 1 Minute |
| Admin-Endpunkte | 30 Requests | 1 Minute |

Tracking per IP-Adresse (X-Forwarded-For oder RemoteAddr).

### 6.5 Security Headers

- **X-XSS-Protection:** 1; mode=block
- **X-Content-Type-Options:** nosniff
- **X-Frame-Options:** DENY
- **CSRF:** Deaktiviert (Stateless JWT)
- **CORS:** Konfigurierbar (Standard: localhost:4200)

### 6.6 Input-Sanitisierung

- HTML-Tags werden aus User-Input entfernt (`<[^>]*>` Regex)
- Bean Validation auf allen Request-DTOs
- SQL Injection geschuetzt durch JPA/Prepared Statements

---

## 7. Admin-System

### 7.1 Uebersicht

```
┌─────────────────────────────────────────────┐
│              Admin Dashboard                 │
│  Benutzer | Reviews | Mails | Audit | Frist │
└─────────────────────────────────────────────┘
        │          │        │       │       │
        ▼          ▼        ▼       ▼       ▼
  ┌──────────┐ ┌────────┐ ┌─────┐ ┌─────┐ ┌───────────┐
  │ User-    │ │Review- │ │Mail-│ │Audit│ │Scheduled  │
  │ Mgmt     │ │Mgmt    │ │Ctr  │ │Log  │ │Actions    │
  │          │ │        │ │     │ │     │ │           │
  │ Anzeigen │ │Anzeigen│ │Send │ │View │ │Anzeigen   │
  │ Bearbeit.│ │Ausblen.│ │+Frist│ │Filt │ │Abbrechen  │
  │ Sperren  │ │Einblen.│ │     │ │     │ │Verlaengern│
  │ Loeschen │ │Loeschen│ │     │ │     │ │           │
  │ Rolle    │ │        │ │     │ │     │ │           │
  └──────────┘ └────────┘ └─────┘ └─────┘ └───────────┘
```

### 7.2 Frist- und Trigger-System

1. Admin sendet Mail an User mit optionaler Frist
2. Frist und gewuenschte Aktion werden in `admin_scheduled_actions` gespeichert
3. `AdminDeadlineScheduler` prueft alle 60 Sekunden auf ueberfaellige Aktionen
4. Bei Fristablauf wird die Aktion automatisch ausgefuehrt
5. Admins koennen offene Aktionen jederzeit abbrechen oder die Frist verlaengern

Unterstuetzte Deadline-Aktionen:
- **SUSPEND** - User automatisch sperren
- **DELETE** - User automatisch loeschen
- **HIDE_REVIEWS** - Alle Reviews des Users ausblenden

### 7.3 Audit-Log

Jede Admin-Aktion wird protokolliert:

| Aktion | Beschreibung |
|--------|-------------|
| USER_EDITED | User-Daten geaendert |
| USER_SUSPENDED | User gesperrt |
| USER_UNSUSPENDED | Sperre aufgehoben |
| USER_DELETED | User geloescht |
| USER_ROLE_CHANGED | Rolle geaendert |
| USER_REVIEWS_HIDDEN | Alle Reviews ausgeblendet |
| REVIEW_HIDDEN | Einzelnes Review ausgeblendet |
| REVIEW_SHOWN | Review eingeblendet |
| REVIEW_DELETED | Review geloescht |
| MAIL_SENT | Mail ohne Frist gesendet |
| MAIL_SENT_WITH_DEADLINE | Mail mit Frist gesendet |
| SCHEDULED_ACTION_CANCELLED | Frist-Aktion abgebrochen |
| SCHEDULED_ACTION_EXTENDED | Frist verlaengert |
| SCHEDULED_ACTION_EXECUTED | Automatische Ausfuehrung |

---

## 8. Frontend-Architektur

### 8.1 Ueberblick

- **Standalone Components** (keine NgModules)
- **Lazy Loading** fuer alle Seiten
- **Signals** fuer reaktives State Management
- **PrimeNG** als UI-Komponentenbibliothek
- **SCSS** fuer Styling

### 8.2 Routing

```
/                         → HomeComponent
/search                   → SearchResultsComponent
/streets/:id              → StreetDetailComponent
/streets/:id/review       → ReviewFormComponent         [authGuard]
/login                    → LoginComponent
/register                 → RegisterComponent
/verify-email             → VerifyEmailComponent
/oauth2/callback          → OAuthCallbackComponent
/profile                  → ProfileComponent            [authGuard]
/admin                    → AdminDashboardComponent      [adminGuard]
/admin/users              → AdminUsersComponent          [adminGuard]
/admin/reviews            → AdminReviewsComponent        [adminGuard]
/admin/mails              → AdminMailsComponent          [adminGuard]
/admin/audit              → AdminAuditComponent          [adminGuard]
/admin/scheduled-actions  → AdminScheduledActionsComponent [adminGuard]
```

### 8.3 Guards

| Guard | Pruefung | Redirect |
|-------|----------|----------|
| authGuard | User eingeloggt? | /login |
| adminGuard | Rolle = ADMIN oder SUPER_ADMIN? | / |

### 8.4 Services

#### AuthService
- State: `currentUser` (Signal), `isAuthenticated` (Signal)
- Login/Register/Logout/OAuth2-Handling
- Token-Speicherung in localStorage (`lr_token`, `lr_user`)

#### StreetService
- Strassensuche und Detailansicht

#### ReviewService
- CRUD-Operationen auf Bewertungen

#### AdminService
- Alle Admin-API-Aufrufe (Dashboard, Users, Reviews, Mails, Audit, Scheduled Actions)

### 8.5 Interceptor

#### authInterceptor (HttpInterceptorFn)
- Fuegt automatisch `Authorization: Bearer {token}` Header an alle Requests an

### 8.6 Models

| Datei | Interfaces |
|-------|-----------|
| user.model.ts | User, AuthResponse, RegisterRequest, LoginRequest, UpdateProfileRequest, MessageResponse |
| street.model.ts | Street, StreetDetail, CriteriaAverages |
| review.model.ts | Review, ReviewRequest, PageResponse, CRITERIA_LABELS |
| admin.model.ts | AdminUser, AdminReview, AdminMail, AuditLog, ScheduledAction, AdminDashboard, AdminMailRequest, AdminUserUpdateRequest, Page<T> |

### 8.7 Admin-Seiten

| Seite | Funktionen |
|-------|-----------|
| Dashboard | Statistiken (User, Reviews, Aktionen, Strassen), Navigation |
| Benutzerverwaltung | Tabelle mit Suche/Filter, Sperren, Entsperren, Mail senden, Rolle aendern, Loeschen |
| Review-Verwaltung | Tabelle mit Sichtbarkeitsfilter, Ausblenden/Einblenden, Loeschen |
| Mailcenter | Mailhistorie mit Detail-Ansicht |
| Audit-Log | Protokoll aller Admin-Aktionen mit Filtern (Aktion, Zieltyp) |
| Frist-Aktionen | Offene/alle Aktionen, Abbrechen, Frist verlaengern |

---

## 9. Konfiguration

### 9.1 application.yml (Struktur)

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/LiningRank
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate.ddl-auto: validate    # Flyway verwaltet Schema
    open-in-view: false             # Keine Lazy-Loading-Probleme
  flyway:
    enabled: true
  mail:
    host: localhost                 # MailHog fuer Dev
    port: 1025
  security.oauth2.client:
    registration.google:            # Google OAuth2
      client-id: ${GOOGLE_CLIENT_ID}
      client-secret: ${GOOGLE_CLIENT_SECRET}

app:
  jwt:
    secret: ${JWT_SECRET}           # Min. 256-Bit
    expiration-ms: 86400000         # 24h (User)
    admin-expiration-ms: 3600000    # 1h (Admin)
  cors:
    allowed-origins: http://localhost:4200
  mail.from: noreply@livingrank.de
  frontend-url: http://localhost:4200
  admin:
    rate-limit:
      capacity: 30                  # Requests pro Fenster
      refill-minutes: 1             # Fenstergroesse
    scheduler:
      check-interval-ms: 60000     # Scheduler-Intervall

server:
  port: 8080
```

### 9.2 Umgebungsvariablen

| Variable | Beschreibung |
|----------|-------------|
| JWT_SECRET | JWT-Signierungsschluessel (min. 256-Bit) |
| GOOGLE_CLIENT_ID | Google OAuth2 Client ID |
| GOOGLE_CLIENT_SECRET | Google OAuth2 Client Secret |
| CORS_ORIGINS | Erlaubte CORS-Origins |
| FRONTEND_URL | Frontend-URL fuer Links in E-Mails |
| MAIL_FROM | Absender-Adresse |

### 9.3 Frontend Environments

**Development** (`environment.ts`):
```typescript
apiUrl: 'http://localhost:8080/api'
googleOAuthUrl: 'http://localhost:8080/oauth2/authorization/google'
```

**Production** (`environment.prod.ts`):
```typescript
apiUrl: 'https://api.livingrank.de/api'
googleOAuthUrl: 'https://api.livingrank.de/oauth2/authorization/google'
```

---

## 10. Abhaengigkeiten

### 10.1 Backend (Maven)

| Abhaengigkeit | Zweck |
|---------------|-------|
| spring-boot-starter-web | REST-API, Tomcat |
| spring-boot-starter-data-jpa | JPA, Hibernate |
| spring-boot-starter-security | Spring Security |
| spring-boot-starter-oauth2-client | Google OAuth2 |
| spring-boot-starter-validation | Bean Validation |
| spring-boot-starter-mail | E-Mail-Versand |
| postgresql | PostgreSQL JDBC Driver |
| flyway-core + flyway-database-postgresql | DB-Migrationen |
| jjwt-api + jjwt-impl + jjwt-jackson | JWT-Token |
| bucket4j-core | Rate Limiting |
| spring-boot-starter-test | Testing |
| spring-security-test | Security Testing |
| testcontainers | Integrationstests mit echtem PostgreSQL |
| jacoco | Code Coverage |

### 10.2 Frontend (npm)

| Abhaengigkeit | Zweck |
|---------------|-------|
| @angular/core | Angular Framework |
| @angular/router | Routing |
| @angular/forms | Formulare |
| @angular/common/http | HTTP Client |
| primeng | UI-Komponenten |
| @primeng/themes | PrimeNG Themes |
| primeicons | Icon-Bibliothek |
| rxjs | Reaktive Programmierung |
| typescript | Typisierung |
| vitest | Unit Tests |
