# Vite Config (app/vite.config.ts)

Ce document explique comment est configuré Vite pour l’app située dans ce dossier.

## Vue d’ensemble

- `app/vite.config.ts` utilise:
  - `@vitejs/plugin-react` pour React + HMR.
  - `vite-plugin-pwa` pour générer le Service Worker et le manifest.
  - Un proxy de dev optionnel pour GraphQL (activé selon une variable d’environnement).
  - `optimizeDeps.include` pour forcer la pré‑optimisation de quelques libs UI (`@emotion/*`, `@mui/joy`).

## Variables d’environnement

- `VITE_CLUB_SERVICE_URL`
  - Rôle: cible du proxy dev pour l’API GraphQL.
  - Format: URL avec ou sans suffixe `/graphql` (il est normalisé côté config).
  - Emplacement: à définir dans `app/.env`.
  - Exemple:
    - `VITE_CLUB_SERVICE_URL=http://localhost:8081`
    - ou `VITE_CLUB_SERVICE_URL=http://localhost:8081/graphql`

## Détails du proxy dev

- Fichier: `app/vite.config.ts:11-12, 54-57`
- Si `VITE_CLUB_SERVICE_URL` est défini, Vite crée un proxy HTTP:
  - Requêtes du front vers `/graphql` → redirigées vers la cible (avec `changeOrigin: true`).
- Si la variable n’est pas définie, aucun proxy n’est créé et le front appellera `/graphql` en same‑origin (nécessite un serveur/reverse‑proxy côté back).

## PWA

- Fichier: `app/vite.config.ts`
- `vite-plugin-pwa` est configuré en mode `generateSW` avec:
  - Manifest minimal (nom/short_name/theme_color).
  - Workbox qui précache JS/CSS/HTML/SVG/PNG/ICO.
  - `devOptions.enabled` désactivé (pas de SW en dev).

## Endpoint GraphQL côté code

- Fichier: `app/src/api/graphqlClient.ts`
  - Si `VITE_CLUB_SERVICE_URL` est définie à la build, l’endpoint est construit à partir de cette variable.
  - Sinon, le code utilise `"/graphql"` (same‑origin) — alignez votre infra en conséquence.

## Commandes utiles

- Dev: `npm run dev`
  - Pour exposer sur le réseau: `npm run dev -- --host`
- Build: `npm run build`
- Preview: `npm run preview`

## Exemple de app/.env

```
VITE_CLUB_SERVICE_URL=http://localhost:8081
```

Ou si l’endpoint comporte déjà `/graphql`:

```
VITE_CLUB_SERVICE_URL=http://localhost:8081/graphql
```

Note: les variables `VITE_*` sont injectées au moment du build. En production, assurez‑vous que `VITE_CLUB_SERVICE_URL` est bien définie lors de la compilation si vous ne servez pas `/graphql` en same‑origin.
