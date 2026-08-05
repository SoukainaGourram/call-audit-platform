# Plateforme Historique Appels — Inwi

Plateforme sécurisée de consultation de l'historique des appels téléphoniques (architecture microservices).

## Démarrage rapide (Docker)

```bash
docker compose up --build
```

Ouvrir **http://localhost:4200**

### Comptes de démonstration

| Utilisateur | Mot de passe | Rôle  |
|-------------|--------------|-------|
| admin       | admin123     | ADMIN |
| user        | user123      | USER  |

## Architecture

| Service               | Port | Description                          |
|-----------------------|------|--------------------------------------|
| auth-service          | 8081 | Authentification JWT                 |
| user-service          | 8082 | Gestion des utilisateurs             |
| call-history-service  | 8083 | Recherche d'appels + masquage        |
| search-history-service| 8084 | Traçabilité des recherches           |
| frontend (Angular)    | 4200 | Interface utilisateur                |

## Fonctionnalités

- Authentification JWT (login / logout)
- CRUD utilisateurs (admin) : créer, modifier, supprimer, activer/désactiver
- Profil utilisateur : consulter/modifier, changer le mot de passe
- Recherche de numéro avec affichage masqué (ex. `06*****67`)
- Historique automatique de chaque recherche

## Développement local (sans Docker)

1. Démarrer chaque microservice Spring Boot (ports 8081–8084)
2. Frontend :
   ```bash
   cd frontend
   npm install
   npm start
   ```
3. Ouvrir **http://localhost:4200** (proxy Angular vers les APIs locales)

## Arrêt

```bash
docker compose down
```

Pour réinitialiser les bases de données :
```bash
docker compose down -v
```
