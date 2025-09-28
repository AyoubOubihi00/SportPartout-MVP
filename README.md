# Variables d’environnement

Ce dépôt contient un front (Vite/React dans `app/`) et des services lancés via Docker Compose. Voici les variables d’environnement à connaître et comment les renseigner.

## Front (`app/`)

- `VITE_CLUB_SERVICE_URL` (obligatoire en dev)
  - URL du service GraphQL « club ».
  - Accepte une URL avec ou sans suffixe `/graphql`.
  - Exemple: `http://localhost:8081/graphql` ou `http://localhost:8081`.
  - Effet:
    - Si défini: Vite crée un proxy dev sur `/graphql` vers cette URL.
    - Si non défini: aucun proxy n’est créé. Le code du front appellera `/graphql` sur la même origine. Cela marchera seulement si un reverse‑proxy/serveur répond sur le même domaine.

Créer le fichier `app/.env` et y ajouter:

```
VITE_CLUB_SERVICE_URL=http://localhost:8081
```

Ou, si votre endpoint inclut déjà le chemin GraphQL:

```
VITE_CLUB_SERVICE_URL=http://localhost:8081/graphql
```

## Docker Compose (racine)

Copiez `./.env.dist` en `./.env` puis complétez les valeurs:

- `RABBITMQ_DEFAULT_USER`, `RABBITMQ_DEFAULT_PASS`
- `RABBITMQ_PORT`, `RABBITMQ_MANAGEMENT_PORT`
- `POSTGRES_USER`, `POSTGRES_PASSWORD`, `POSTGRES_DB`

Ces variables sont lues par `docker-compose.yml` pour exposer les ports et configurer RabbitMQ et PostgreSQL.

## Récapitulatif fichiers

- Front: `app/.env` → variables `VITE_*` (utilisées par Vite et accessibles à l’application).
- Infrastructure: `./.env` → variables pour Docker Compose.

## Démarrage rapide

1) Backend/infra (optionnel si vous avez déjà un backend):
- Copier `.env.dist` → `.env` et remplir.
- `docker-compose up -d`

2) Front:
- Créer `app/.env` avec `VITE_CLUB_SERVICE_URL`.
- `cd app && npm install && npm run dev`

