/// <reference types="cypress" />


describe('SearchFilter E2E', () => {
  it('affiche et sélectionne un sport', () => {
    cy.visit('http://localhost:5173'); // adapte l’URL si besoin
    cy.get('input[placeholder="Rechercher un sport / lieu ..."]').focus().type('ten');
    cy.contains('Tennis').click();
    cy.contains('Tennis').should('exist');
  });

  it('affiche et sélectionne une ville', () => {
    cy.visit('http://localhost:5173');
    cy.get('input[placeholder="Rechercher un sport / lieu ..."]').focus().type('Paris');
    cy.contains('Paris').click();
    cy.contains('Paris').should('exist');
  });
});
