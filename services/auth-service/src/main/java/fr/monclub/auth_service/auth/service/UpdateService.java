package fr.monclub.auth_service.auth.service;

import fr.monclub.auth_service.auth.catalog.UserCatalog;
import fr.monclub.auth_service.auth.model.UpdateUserInput;
import fr.monclub.auth_service.auth.model.User;
import fr.monclub.auth_service.auth.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UpdateService {

    private  final UserCatalog userCatalog;

    public String update(User user, UpdateUserInput input) {
        user.setFirstName(input.getFirstName());
        user.setLastName(input.getLastName());
        user.setBirthDate(input.getBirthDate());
        user.setPhone(input.getPhone());
        userCatalog.save(user);

        return "User updated successfully";
    }
}
