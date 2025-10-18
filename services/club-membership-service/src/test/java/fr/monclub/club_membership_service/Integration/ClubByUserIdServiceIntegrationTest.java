package fr.monclub.club_membership_service.Integration;

import fr.monclub.club_membership_service.catalog.ClubMemberCatalog;
import fr.monclub.club_membership_service.model.ClubMember;
import fr.monclub.club_membership_service.service.AllClubMembersByUserId;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional
public class ClubByUserIdServiceIntegrationTest {

    @Autowired
    private AllClubMembersByUserId clubByUserIdService;

    @Autowired
    private ClubMemberCatalog clubMemberCatalog;

    private UUID userId;
    private UUID clubId1;
    private UUID clubId2;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        clubId1 = UUID.randomUUID();
        clubId2 = UUID.randomUUID();

        ClubMember member1 = new ClubMember();
        member1.setUserId(userId);
        member1.setClubId(clubId1);
        member1.setIsActif(true);

        ClubMember member2 = new ClubMember();
        member2.setUserId(userId);
        member2.setClubId(clubId2);
        member2.setIsActif(true);

        clubMemberCatalog.save(member1);
        clubMemberCatalog.save(member2);
    }

    @Test
    void findClubIdsByUserId_shouldReturnAllClubIds() {
        List<ClubMember> members = clubByUserIdService.findClubIdsByUserId(userId);

        List<UUID> clubIds = members.stream()
                .map(ClubMember::getClubId)
                .collect(Collectors.toList());

        assertThat(clubIds).containsExactlyInAnyOrder(clubId1, clubId2);
    }
}

