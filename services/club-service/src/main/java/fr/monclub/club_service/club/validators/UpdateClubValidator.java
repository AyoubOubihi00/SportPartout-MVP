package fr.monclub.club_service.club.validators;

import fr.monclub.club_service.core.validator.BaseValidator;
import fr.monclub.club_service.core.validator.VoidFunction;
import fr.monclub.club_service.club.model.SportsType;
import fr.monclub.club_service.club.model.UpdateClubInput;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdateClubValidator extends BaseValidator<UpdateClubInput> {


    @Override
    public VoidFunction validate(UpdateClubInput target) {

        if (target == null) {
            addError("Input is null");
            return null;
        }

        if (target.getName() == null) addErrorNull("name");
        if (target.getName() != null && target.getName().length() > 100)
        {
            addError("Name too long");
        }

        if (target.getEmail() == null) addErrorNull("email");
        if (target.getEmail() != null && target.getEmail().length() > 100){
            addError("Email too long");
        }

        if (target.getPhoneNumber() == null) addErrorNull("phoneNumber");
        if (target.getPhoneNumber() != null && target.getPhoneNumber().length() > 15){
            addError("Phone number too long");
        }

        if (target.getCountry() == null) addErrorNull("country");
        if (target.getCountry() != null && target.getCountry().length() > 100)
        {
            addError("Country too long");
        }

        if (target.getLatitude() == 0) addErrorNull("latitude");
        if (target.getLatitude() != 0 && (target.getLatitude() < -90 || target.getLatitude() > 90)) {
            addError("Latitude invalid");
        }

        if (target.getLongitude() == 0) addErrorNull("longitude");
        if (target.getLongitude() != 0 && (target.getLongitude() < -180 || target.getLongitude() > 180)) {
            addError("Longitude invalid");
        }

        if (target.getTypeId() == null) addErrorNull("typeId");
        if (target.getTypeName() == null) addErrorNull("typeName");


        if (target.getAddress() == null) addErrorNull("address");
        if (target.getAddress() != null && target.getAddress().length() > 200) {
            addError("Address too long");
        }

        if (target.getCity() == null) addErrorNull("city");
        if (target.getCity() != null && target.getCity().length() > 100) {
            addError("City too long");
        }



        return null;
    }
}