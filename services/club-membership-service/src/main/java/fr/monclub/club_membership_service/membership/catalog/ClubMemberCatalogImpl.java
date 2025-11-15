
package fr.monclub.club_membership_service.membership.catalog;
import fr.monclub.club_membership_service.RabbitMQ.ClubDisabledEvent;
import fr.monclub.club_membership_service.RabbitMQ.ClubReactivedEvent;
import fr.monclub.club_membership_service.membership.catalog.ClubMemberCatalog;

import fr.monclub.club_membership_service.membership.model.ClubMember;
import fr.monclub.club_membership_service.membership.repository.ClubMemberRepository;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ClubMemberCatalogImpl implements ClubMemberCatalog {

    private final ClubMemberRepository clubMemberRepository;

    public ClubMemberCatalogImpl(ClubMemberRepository clubMemberRepository) {
        this.clubMemberRepository = clubMemberRepository;
    }

    public ClubMember findByClubIdAndUserId(UUID clubId, UUID userId)
    {
        return this.clubMemberRepository.findByClubIdAndUserId(clubId, userId);
    }

    public List<ClubMember> findAllByClubId(UUID clubId)
    {

        return this.clubMemberRepository.findAllByClubId(clubId);
    }

    @Override
    public List<ClubMember> findAllByClubIdAndIsActifTrue(UUID clubId) {
        return this.clubMemberRepository.findAllByClubIdAndIsActifTrue(clubId);
    }

    @Override
    public List<ClubMember> findAllByClubIdAndIsActifFalse(UUID clubId) {
        return this.clubMemberRepository.findAllByClubIdAndIsActifFalse(clubId);
    }

    public boolean deleteClubMemberById(UUID uuid) {
        if (this.clubMemberRepository.existsById(uuid))
        {
            this.clubMemberRepository.deleteById(uuid);
            return true;
        }
        return false;
    }

    public ClubMember save(ClubMember clubMember)
    {

        ClubMember m = this.clubMemberRepository.save(clubMember);
        System.out.println("mail du membre enregistré : " + m.getEmail());
        return m;
    }

    public List<ClubMember>  findClubMembersByUserId(UUID userId)
    {
        return this.clubMemberRepository.findClubMembersByUserId(userId);
    }

    public void updateClubInfo(UUID clubId, String name, UUID typeId, String typeName,
                               String city, String address, String country, String postalCode,
                               String phoneNumber, String email, Double latitude, Double longitude,
                              OffsetDateTime updatedAt) {

        // Récupération des membres du club
        List<ClubMember> members = clubMemberRepository.findAllByClubId(clubId);

        if (members.isEmpty()) {
            System.out.println("⚠ Aucun membre trouvé pour le club : " + clubId);
            return;
        }

        // Mise à jour des champs pour chaque membre
        for (ClubMember member : members) {
            member.setClubName(name);
            member.setClubTypeId(typeId);
            member.setClubTypeName(typeName);
            member.setClubCity(city);
            member.setClubAdresse(address);
            member.setClubCountry(country);
            member.setClubPostalCode(postalCode);
            member.setPhoneNumber(phoneNumber);
            member.setEmail(email);
            member.setClubLatitude(latitude);
            member.setClubLongitude(longitude);
            member.setClubUpdatedAt(updatedAt);
        }

        // Sauvegarde en lot via le repository
        clubMemberRepository.saveAll(members);

        System.out.println("Club mis à jour pour tous les membres du club : " + clubId);
    }

    @Override
    public boolean disableClub(ClubDisabledEvent clubDisabledEvent) {
        // Récupération des membres du club
        List<ClubMember> members = clubMemberRepository.findAllByClubId(clubDisabledEvent.getClubId());

        if (members.isEmpty()) {
            System.out.println("⚠ Aucun membre trouvé pour le club : ");
            return false;
        }

        // Désactivation des membres
        for (ClubMember member : members) {
            member.setClubActive(clubDisabledEvent.isActive());
            member.setClubDeletedAt(clubDisabledEvent.getDisabledAt());
        }

        clubMemberRepository.saveAll(members);

        System.out.println("Club désactivé pour tous les membres du club : " + clubDisabledEvent.getClubId());
        return true;
    }

    @Override
    public boolean reactiveClub(ClubReactivedEvent clubReactivedEvent) {
        List<ClubMember> members = clubMemberRepository.findAllByClubId(clubReactivedEvent.getClubId());
        if (members.isEmpty()) {
            System.out.println("⚠ Aucun membre trouvé pour le club : ");
            return false;
        }

        // Désactivation des membres
        for (ClubMember member : members) {
            member.setClubActive(clubReactivedEvent.isActive());
            member.setClubDeletedAt(null);
        }

        clubMemberRepository.saveAll(members);

        System.out.println("Club réactivé pour tous les membres du club : " + clubReactivedEvent.getClubId());
        return true;

    }

    @Override
    public int deactivateClubMemberById(UUID memberId) {
        return this.clubMemberRepository.deactivateClubMemberById(memberId);
    }


}
