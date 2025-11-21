package fr.monclub.findclub_service.RabbitMQ.listener;

import fr.monclub.findclub_service.RabbitMQ.events.ClubCreatedEvent;
import fr.monclub.findclub_service.club.model.Club;
import fr.monclub.findclub_service.club.repository.FindClubRepository;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ClubListener {

    private final FindClubRepository clubRepository;

    @RabbitListener(queues = "#{clubCreatedQueueFindClubService.name}")
    public void handleClubCreated(ClubCreatedEvent event) {
        System.out.println("Received ClubCreatedEvent for club: " + event.getId());

        Club club = new Club();
        club.setId(event.getId());
        club.setName(event.getName());
        club.setTypeIdSport(event.getTypeId());
        club.setTypeNameSport(event.getTypeName());
        club.setCity(event.getCity());
        club.setCountry(event.getCountry());
        club.setAddress(event.getAddress());
        club.setPostalCode(event.getPostalCode());
        club.setPhoneNumber(event.getPhoneNumber());
        club.setEmail(event.getEmail());
        club.setLatitude(event.getLatitude());
        club.setLongitude(event.getLongitude());
        club.setOwnerId(event.getOwnerId());
        club.setCreatedAt(event.getCreatedAt());
        club.setUpdatedAt(event.getUpdatedAt());
        club.setActive(event.isActive());
        
        // Default values for fields not in event but in entity
        club.setNotation(0.0);
        club.setMemberCount(0);

        clubRepository.save(club);
        System.out.println("Club saved in findClub-service: " + club.getId());
    }
}
