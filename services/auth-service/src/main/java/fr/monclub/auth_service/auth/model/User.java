package fr.monclub.auth_service.auth.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Table(name = "app_user")
@EntityListeners(AuditingEntityListener.class)
public class User {
    @Id
    private UUID id;

    private String firstName;
    private String lastName;

    @Column(unique = true)
    private String email;

    private String password;

    private String role;

    @CreatedDate
    private Date createdAt;

    @LastModifiedDate
    private Date updatedAt;

    private boolean isActive = true;


    private String phone;



    private String profilePictureUrl;

    private String bio;

    private OffsetDateTime birthDate;

    @PrePersist
    public void ensureIdExists() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
    }


}
