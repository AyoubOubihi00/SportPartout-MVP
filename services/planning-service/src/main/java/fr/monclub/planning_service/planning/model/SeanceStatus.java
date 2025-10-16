package fr.monclub.planning_service.planning.model;

public enum SeanceStatus {
    OPEN,       // séance créée côté gérant
    FULL,       // capacite atteinte
    CANCELED,   // annulée
    PENDING     // création via user en attente de validation
}
