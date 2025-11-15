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
            System.out.println("Input is null");
            return null;
        }

        if (target.getName() == null)
        {
            System.out.println("Name is null");
            addErrorNull("name");
        }
        if (target.getName() != null && target.getName().length() > 100)
        {
            System.out.println("Name too long");
            addError("Name too long");
        }

        if (target.getEmail() == null)
        {
            System.out.println("Email is null");
            addErrorNull("email");
        }
        if (target.getEmail() != null && target.getEmail().length() > 100){
            System.out.println("Email too long");
            addError("Email too long");
        }

        if (target.getPhoneNumber() == null)
        {
            System.out.println("Phone number is null");
            addErrorNull("phoneNumber");
        }
        if (target.getPhoneNumber() != null && target.getPhoneNumber().length() > 15){
            System.out.println("Phone number too long");
            addError("Phone number too long");
        }

        if (target.getCountry() == null)
        {
            System.out.println("Country is null");
            addErrorNull("country");
        }
        if (target.getCountry() != null && target.getCountry().length() > 100)
        {
            System.out.println("Country too long");
            addError("Country too long");
        }

        if (target.getLatitude() == 0)
        {
            System.out.println("Latitude is null");
            addErrorNull("latitude");
        }
        if (target.getLatitude() != 0 && (target.getLatitude() < -90 || target.getLatitude() > 90)) {
            System.out.println("Latitude invalid");
            addError("Latitude invalid");
        }

        if (target.getLongitude() == 0)
        {
            System.out.println("Longitude is null");
            addErrorNull("longitude");
        }
        if (target.getLongitude() != 0 && (target.getLongitude() < -180 || target.getLongitude() > 180)) {
            System.out.println("Longitude invalid");
            addError("Longitude invalid");
        }

        if (target.getTypeId() == null)
        {
            System.out.println("TypeId is null");
            addErrorNull("typeId");
        }
        if (target.getTypeName() == null) addErrorNull("typeName");


        if (target.getAddress() == null)
        {
            System.out.println("Address is null");
            addErrorNull("address");
        }
        if (target.getAddress() != null && target.getAddress().length() > 200) {
            System.out.println("Address too long");
            addError("Address too long");
        }

        if (target.getCity() == null) addErrorNull("city");
        if (target.getCity() != null && target.getCity().length() > 100) {
            System.out.println("City too long");
            addError("City too long");
        }



        return null;
    }
}