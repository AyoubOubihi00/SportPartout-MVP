# Sport Partout

## Description

**Sport Partout** est une application mobile permettant de mettre en relation les passionnés de sport. L’écosystème repose sur plusieurs services communiquant entre eux via RabbitMQ et utilisant PostgreSQL pour le stockage des données.

## Variables d'environnement

Chaque service nécessite son propre fichier `.env` pour définir les variables d'environnement spécifiques. Assurez-vous de créer ces fichiers avant de démarrer les services.

```
RABBITMQ_HOST=rabbitmq
RABBITMQ_PORT=5672
RABBITMQ_DEFAULT_USER=guest
RABBITMQ_DEFAULT_PASS=guest

POSTGRES_DB=admin_db
POSTGRES_USER=club_user
POSTGRES_PASSWORD=club_password
```


## Configuration spécifique

Pour le service auth-service, vous devez définir une clé JWT :
  
```
JWT_SECRET=your_jwt_secret_key
```

## Clés pour la génération des tokens
L’authentification repose sur une paire de clés privée et publique (RSA).
Ces fichiers doivent être présents dans le répertoire :

```
environment:
  - KEYS_PATH=/app/keys

volumes:
  - ./keys:/app/keys


```

Assurez-vous que les fichiers `private_key.pem` et `public_key.pem` sont placés dans le dossier `keys` à la racine du projet avant de démarrer les services.


## Démarrage des services
Utilisez Docker Compose pour démarrer tous les services :

```bash
docker-compose up --build
```

## Accès aux services
- **API Gateway** : http://localhost:8080
- **RabbitMQ Management** : http://localhost:15672 (utilisateur : guest, mot de passe : guest)
- **Frontend** : http://localhost:5173
