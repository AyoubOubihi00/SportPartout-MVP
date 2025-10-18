package fr.monclub.club_membership_service.exception;

import java.util.UUID;

public class MemberNotFoundException extends RuntimeException{
    public MemberNotFoundException(UUID userId, UUID clubId) {
        super("Membre non trouvé pour userId=" + userId + " et clubId=" + clubId);
    }
}
