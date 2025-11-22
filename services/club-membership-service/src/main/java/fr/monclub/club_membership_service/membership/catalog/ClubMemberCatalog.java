package fr.monclub.club_membership_service.membership.catalog;

import fr.monclub.club_membership_service.RabbitMQ.ClubDisabledEvent;
import fr.monclub.club_membership_service.RabbitMQ.ClubReactivedEvent;
import fr.monclub.club_membership_service.RabbitMQ.UserUpdatedEvent;
import fr.monclub.club_membership_service.membership.model.ClubMember;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Component
public interface ClubMemberCatalog {

    ClubMember findByClubIdAndUserId(UUID clubId, UUID userId);

    List<ClubMember> findAllByClubId(UUID clubId);
    List<ClubMember> findAllByClubIdAndIsActifTrue(UUID clubId);
    List<ClubMember> findAllByClubIdAndIsActifFalse(UUID clubId);

    boolean deleteClubMemberById(UUID uuid);

    ClubMember save(ClubMember clubMember);

    List<ClubMember> findClubMembersByUserId(UUID userId);

    public void updateClubInfo(UUID clubId, String name, UUID typeId, String typeName,
                               String city, String address, String country, String postalCode,
                               String phoneNumber, String email, Double latitude, Double longitude,
                             OffsetDateTime updatedAt);

    public boolean disableClub(ClubDisabledEvent clubDisabledEvent);

    public boolean reactiveClub(ClubReactivedEvent clubReactivedEvent);
    int deactivateClubMemberById(UUID memberId);
    boolean updateUser(UserUpdatedEvent userUpdatedEvent);
}
