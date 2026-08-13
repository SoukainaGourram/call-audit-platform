# 📞 Plateforme Historique des Appels

Plateforme sécurisée de consultation de l'historique des appels téléphoniques, développée en **architecture microservices** dans le cadre d'un projet de fin d'année (PFA) chez Inwi.

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=flat-square&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.3.4-6DB33F?style=flat-square&logo=springboot&logoColor=white)
![Angular](https://img.shields.io/badge/Angular-DD0031?style=flat-square&logo=angular&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-4169E1?style=flat-square&logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=flat-square&logo=docker&logoColor=white)
![JWT](https://img.shields.io/badge/Auth-JWT-000000?style=flat-square&logo=jsonwebtokens&logoColor=white)

## 🎯 Contexte

Ce projet permet à des agents habilités de rechercher l'historique d'appels associé à un numéro de téléphone, tout en respectant des contraintes de confidentialité (masquage des numéros affichés) et de traçabilité (chaque recherche est journalisée). L'application est découpée en 4 microservices Spring Boot indépendants, chacun avec sa propre base de données PostgreSQL, orchestrés via Docker Compose et exposés à un frontend Angular.

## 🏗️ Architecture

```
                         ┌─────────────────────┐
                         │   Frontend Angular   │
                         │      (port 4200)     │
                         └──────────┬───────────┘
                                    │
        ┌───────────────┬──────────┼──────────┬────────────────┐
        │                │                     │                │
┌───────▼──────┐ ┌───────▼──────┐  ┌───────────▼────────┐ ┌─────▼──────────────┐
│ auth-service │ │ user-service │  │ call-history-service│ │search-history-service│
│  (port 8081) │ │  (port 8082) │  │     (port 8083)     │ │      (port 8084)    │
│  JWT / Login │ │ CRUD users   │  │ Recherche + masquage │ │ Traçabilité recherches│
└───────┬──────┘ └───────┬──────┘  └───────────┬─────────┘ └─────┬────────────────┘
        │                │                     │                 │
        └────────────────┴─────────┬───────────┴─────────────────┘
                                    │
                          ┌─────────▼─────────┐
                          │   PostgreSQL 15    │
                          │  (1 DB / service)  │
                          └────────────────────┘
```

| Service | Port | Rôle |
|---|---|---|
| `auth-service` | 8081 | Authentification, génération et validation des tokens JWT |
| `user-service` | 8082 | Gestion des comptes utilisateurs (CRUD, profil, rôles) |
| `call-history-service` | 8083 | Recherche d'appels par numéro, avec masquage partiel (`06*****67`) |
| `search-history-service` | 8084 | Journalisation de chaque recherche effectuée (audit) |
| `frontend` | 4200 | Interface Angular consommant les 4 APIs |

Chaque service backend possède sa **propre base de données PostgreSQL** (`authdb`, `userdb`, `callhistorydb`, `searchhistorydb`), respectant le principe *database-per-service* des architectures microservices.

## ✨ Fonctionnalités

- 🔐 **Authentification JWT** (login/logout), avec rôle embarqué dans le token (`ADMIN` / `USER`)
- 👥 **Gestion des utilisateurs** (réservée aux admins) : création, modification, suppression, activation/désactivation de comptes
- 🙋 **Profil utilisateur** : consultation/modification des informations, changement de mot de passe
- 🔍 **Recherche de numéro** avec masquage automatique des numéros affichés (protection de la confidentialité)
- 📝 **Historique de recherche** : chaque requête est tracée avec l'utilisateur, le numéro recherché et le nombre de résultats
- 🛡️ **Contrôle d'accès par rôle** côté interface (routes admin protégées par guard Angular)

## 🛠️ Stack technique

**Backend**
- Java 21 · Spring Boot 3.3.4 (Web, Data JPA, Security, Validation)
- JJWT 0.12.6 pour la génération/validation des tokens
- PostgreSQL 15 (une base par microservice)
- Lombok

**Frontend**
- Angular (composants standalone : login, recherche d'appels, gestion utilisateurs, profil, historique)
- Intercepteur HTTP pour l'injection automatique du token JWT
- Guards de routes (`authGuard`, `adminGuard`)

**Infrastructure**
- Docker & Docker Compose (5 conteneurs orchestrés + réseau bridge dédié)
- Nginx (reverse proxy pour le frontend en production)

## 🚀 Démarrage rapide (Docker)

```bash
git clone https://github.com/SoukainaGourram/plateforme-historique-appels.git
cd plateforme-historique-appels
docker compose up --build
```

Ouvrir **http://localhost:4200**

### Comptes de démonstration

| Utilisateur | Mot de passe | Rôle |
|---|---|---|
| `admin` | `admin123` | ADMIN |
| `user` | `user123` | USER |

### Arrêt

```bash
docker compose down          # arrêter les conteneurs
docker compose down -v       # arrêter + réinitialiser les bases de données
```

## 💻 Développement local (sans Docker)

1. Démarrer PostgreSQL et créer les 4 bases (`authdb`, `userdb`, `callhistorydb`, `searchhistorydb`)
2. Lancer chaque microservice Spring Boot individuellement (ports 8081 à 8084)
3. Démarrer le frontend :
   ```bash
   cd frontend
   npm install
   npm start
   ```
4. Ouvrir **http://localhost:4200** (proxy Angular configuré vers les APIs locales)

## 📡 Aperçu des endpoints principaux

| Méthode | Endpoint | Description |
|---|---|---|
| `POST` | `/api/auth/login` | Authentification, retourne un JWT |
| `GET` | `/api/auth/validate` | Validation d'un token |
| `GET` | `/api/users` | Liste des utilisateurs (admin) |
| `PUT` | `/api/users/profile/{username}` | Mise à jour du profil |
| `GET` | `/api/calls/search?number=...` | Recherche d'appels par numéro (masqué) |
| `GET` | `/api/searches/user/{username}` | Historique des recherches d'un utilisateur |

## 📌 Pistes d'amélioration

- Renforcement du contrôle d'accès par rôle côté backend (autorisation au niveau des endpoints, pas uniquement côté interface)
- Passerelle API (Spring Cloud Gateway) pour centraliser le routage et la sécurité
- Tests d'intégration automatisés entre services
