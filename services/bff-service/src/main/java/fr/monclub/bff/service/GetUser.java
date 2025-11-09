package fr.monclub.bff.service;


import fr.monclub.bff.config.JWT.UserSimple;
import fr.monclub.bff.shared.entity.GetEntity;
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