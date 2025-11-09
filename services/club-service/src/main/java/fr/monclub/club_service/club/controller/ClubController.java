package fr.monclub.club_service.club.controller;

import fr.monclub.club_service.club.catalog.ClubCatalog;
import fr.monclub.club_service.club.model.Club;
import fr.monclub.club_service.club.model.UpdateClubInput;
import fr.monclub.club_service.club.service.AddClubService;
import fr.monclub.club_service.club.service.DeleteClubService;
import fr.monclub.club_service.club.service.UpdateClubService;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

@Controller
@AllArgsConstructor
public class ClubController {

    private final ClubCatalog clubCatalog;
    private final UpdateClubService updateClub;
    private final AddClubService addClub;
    private final DeleteClubService deleteClubService;

    @QueryMapping
    public List<Club> clubs() {
        return clubCatalog.getAllClubs();
    }

    @QueryMapping
    public Club clubById(@Argument UUID id) {
        System.out.println("Fetching club with id: " + id);
        return clubCatalog.getClubById(id);
    }

    @QueryMapping
    public List<Club> clubsByOwnerId(@Argument UUID id) {
        return clubCatalog.getClubsByOwnerId(id);
    }

    @MutationMapping
    public Club updateClub(@Argument UUID id, @Argument UpdateClubInput data, @Argument UUID ownerId) {
        return updateClub.apply(id, data, ownerId);
    }

    @MutationMapping
    public Club createClub(@Argument UpdateClubInput data, @Argument UUID ownerId) {
        return addClub.apply(data, ownerId);
    }

    @MutationMapping
    public Boolean disableClub(@Argument UUID id, @Argument UUID ownerId) {
        return deleteClubService.apply(id, ownerId);
    }





}
