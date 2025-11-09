package fr.monclub.club_membership_service.membership.service;


import fr.monclub.club_membership_service.config.JWT.UserSimple;
import fr.monclub.club_membership_service.shared.entity.GetEntity;

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