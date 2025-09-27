# Documentation : Lancer les tests (Unitaires, Intégration, E2E)

Ce projet utilise **Jest** pour les tests unitaires et d'intégration, et **Cypress** pour les tests End-to-End (E2E).

## 1. Tests Unitaires & d'Intégration (Jest)

Les tests unitaires et d'intégration sont situés dans le dossier `src/components/.../__tests__` ou `src/services/__tests__`.

### Lancer tous les tests Jest

```bash
npm test
```

Ou pour un rapport détaillé :

```bash
npm run test -- --coverage
```

### Lancer un test spécifique

```bash
npm test -- src/components/SearchFilter/__tests__/SearchFilter.test.tsx
```

## 2. Tests End-to-End (Cypress)

Les tests E2E sont dans le dossier `cypress/e2e/`.

### Lancer l'interface Cypress (mode interactif)

```bash
npx cypress open
```

Cela ouvre l'interface graphique de Cypress pour exécuter les tests manuellement.

### Lancer Cypress en mode headless (CI/CD ou terminal)

```bash
npx cypress run
```

### Lancer un test E2E spécifique

```bash
npx cypress run --spec cypress/e2e/searchFilter.e2e.cy.ts
```

## 3. Prérequis

- Node.js et npm installés
- Dépendances installées :

```bash
npm install
```

## 4. Structure des dossiers de test

- **Unitaires/Intégration** : `src/components/.../__tests__`, `src/services/__tests__`
- **E2E** : `cypress/e2e/`

## 5. Conseils

- Vérifiez la configuration dans `jest.config.cjs` et `cypress.config.ts` si besoin d'adapter.
- Pour CI/CD, privilégiez `npx cypress run` (mode headless).

---

Pour toute question, consultez la documentation officielle :
- [Jest](https://jestjs.io/docs/getting-started)
- [Cypress](https://docs.cypress.io/guides/overview/why-cypress)
