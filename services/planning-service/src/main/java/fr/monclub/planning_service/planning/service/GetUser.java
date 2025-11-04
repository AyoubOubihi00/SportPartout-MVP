package fr.monclub.planning_service.planning.service;



import fr.monclub.planning_service.config.JWT.UserSimple;
import fr.monclub.planning_service.planning.catalog.SeanceCatalog;
import fr.monclub.planning_service.shared.entity.GetEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class GetUser implements GetEntity<UserSimple> {

    @Override
    public UserSimple apply() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getPrincipal() instanceof UserSimple) {
            return (UserSimple) auth.getPrincipal();
        }
        return null;
    }
}