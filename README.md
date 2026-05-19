# BunOS backend overview
Dette repoet inneholder kodebasen som bygger opp backenden for lærerverkøyet "BunOS", skapt av Kristoffer Folkvord, Sofie Emmelin Weber, Edwina Ann Sopha Larsen og Sandre To som en bacheloroppgave.


## Teknologistack
* Java 21
* Spring boot 4.0.1
* PostgreSQL 18.1


## Features
Utgivelsen *Bachelor-release* har følgende features:
- Hosting av frontenden
- Oppgavetildeling
- Brukerhåndtering


## Installasjon og oppstart
Backenden er satt opp til å kunne kjøre i Docker, men kan også kjøres as is på de fleste maskiner, da monolitten er skrevet i Java. 


### Oppstart via Docker
Med Docker, trengs følgende programvare:
- Docker

si noe om db-brukeren

Ettersom Docker er installert, lager man en `.env`-fil med miljøvariablene under denne delen. Etter dette er gjort, brukes denne kommandoen:

`docker compose up --build`

Backenden er nå oppe, ezpz.


### Oppstart uten Docker
Uten Docker, trengs følgende programvare:
- Java 21 eller nyere
- PostgreSQL 18.1 eller nyere

Først må man lage en database for applikasjonen, og da er det lurt å lage sin egen postgres-bruker med minimale rettigheter utenfor databasen for å minke risikoen ved eksponering. Antagende at databasen heter `bunos_db` og brukeren `admin`, gjøres dette med følgende kommandoer:

```sql
CREATE DATABASE bunos_db;
CREATE USER admin WITH PASSWORD 'ditt passord';
GRANT CONNECT ON bunos_db TO admin;
GRANT CREATE ON bunos_db TO admin;
```

Videre i databasen:
```sql
GRANT USAGE, CREATE ON SCHEMA public TO admin;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO admin;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO admin;
```

Når dette er gjort, konfigurerer man miljøvariablene som vises under, i sitt miljø. 

Dermed skriver man følgende kommando for å kjøre det uten å kompilere:
`./mvnw spring-boot:run`

Alternativt, kan man kompilere det og kjøre `.jar`-filen:
```bash
./mvnw clean package
java -jar target/application.jar
```

## Miljøvariabler
De følgende miljøvariablene brukes og burde settes:
- DB_NAME: Databasenavnet
- DB_DRIVER_URL: URL-en til databasen
- DB_USERNAME: Brukernavnet til DB-brukeren
- DB_PASSWORD: Passordet til DB-brukeren

Eksempel .env-fil:
```env
DB_NAME=bunos_db
DB_DRIVER_URL=jdbc:postgresql://postgres:5432/bunos_db
DB_USERNAME=admin
DB_PASSWORD=mega-hemmelig
JWT_SECRET=*hemmelig BASE64 streng*
```

DB_DRIVER_URL følger dette formatet: `jdbc:postgresql://localhost:*postgres-port*/*db-navn*`, hvor *postgres-port* er grunninnstilt til *5432*, og *db-navn* er hva enn databasen heter. 

**NB:** Dersom man bruker Docker, blir URL-en alltid `jdbc:postgresql://postgres:5432/*db-navn*`. Noter bruken av domenenavnet "postges".

JWT_SECRET er nøkkelen backenden som benytter for å generere og verifisere JWT-tokens. Dette skal være en BASE64 enkodet streng på minst 48 bytes / 384-bits, og kan genereres som ønsket.
