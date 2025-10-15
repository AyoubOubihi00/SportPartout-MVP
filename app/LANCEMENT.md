# Documentation : Lancer le projet

Ce guide explique comment installer et démarrer l'application localement.

## 1. Prérequis

- Node.js (version recommandée : >= 18)
- npm (installé avec Node.js)

## 2. Installation des dépendances

Dans le dossier racine du projet, exécutez :

```bash
npm install
```

## 3. Lancer le serveur de développement

```bash
npm run dev
```

L'application sera accessible à l'adresse indiquée dans le terminal (par défaut : http://localhost:5173).

## 4. Autres commandes utiles

- **Build de production** :

  ```bash
  npm run build
  ```

  Les fichiers optimisés seront générés dans le dossier `dist/`.

- **Prévisualiser le build** :
  ```bash
  npm run preview
  ```
  Permet de tester le build localement avant déploiement.

## 5. Structure du projet

- `src/` : Code source de l'application
- `public/` : Fichiers statiques
- `package.json` : Dépendances et scripts
- `vite.config.ts` : Configuration Vite

## 6. Dépannage

- Si une erreur apparaît, vérifiez la version de Node.js avec :
  ```bash
  node -v
  ```
- Assurez-vous que toutes les dépendances sont installées :
  ```bash
  npm install
  ```
- Consultez le fichier `README.md` pour plus d'informations.

---

Pour toute question, consultez la documentation officielle de Vite :

- [Vite](https://vitejs.dev/guide/)
