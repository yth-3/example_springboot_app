package com.revature.sylvester.controllers;

import com.revature.sylvester.dtos.requests.NewUserRequest;
import com.revature.sylvester.dtos.requests.UpdateProfileRequest;
import com.revature.sylvester.dtos.responses.Principal;
import com.revature.sylvester.entities.User;
import com.revature.sylvester.entities.UserProfile;
import com.revature.sylvester.services.TokenService;
import com.revature.sylvester.services.UserProfileService;
import com.revature.sylvester.services.UserService;
import com.revature.sylvester.utils.custom_exceptions.InvalidAuthException;
import com.revature.sylvester.utils.custom_exceptions.InvalidProfileException;
import com.revature.sylvester.utils.custom_exceptions.InvalidUserException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@CrossOrigin
@RestController
@RequestMapping("/profiles")
public class UserProfileController {
    private final UserProfileService profileService;
    private final TokenService tokenService;

    public UserProfileController(UserProfileService profileService, TokenService tokenService) {
        this.profileService = profileService;
        this.tokenService = tokenService;
    }

    public void create(NewUserRequest req) {
        if(!profileService.isEmptyDisplayName(req.getDisplayName())) {
            if(profileService.isValidBirthDate(req.getBirthDate())) {
                User user = new User();
                UserProfile createdProfile = profileService.createProfile(req, user);
            } else
                throw new InvalidUserException("Must be 13 years or older to create a profile");
        } else
            throw new InvalidProfileException("Please enter a display name");
    }

    @PutMapping
    public void update(@RequestBody UpdateProfileRequest req, HttpServletRequest servReq) {
        String token = servReq.getHeader("authorization");

        if(token == null || token.isEmpty())
            throw new InvalidAuthException("Invalid token");

        Principal principal = tokenService.extractRequesterDetails(token);

        if(principal == null)
            throw new InvalidAuthException("Please log in to create a post");

        if(!principal.isActive())
            throw new InvalidAuthException("Your account is not active");

        UserProfile updatedProfile = profileService.getProfileByUserId(principal.getUserId());

        if(!profileService.isEmptyDisplayName(req.getDisplayName())) {
            if(profileService.isValidBirthDate(req.getBirthDate())) {
                profileService.updateProfile(req, principal.getUsername(), updatedProfile.getProfileId());
            } else
                throw new InvalidUserException("Must be 13 years or older");
        } else
            throw new InvalidUserException("Please enter a display name");
    }

    @GetMapping("/user")
    public UserProfile getByUserId(@RequestParam String id) {
        return profileService.getProfileByUserId(id);
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(InvalidProfileException.class)
    public InvalidProfileException handledProfileException (InvalidProfileException e) {
        return e;
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(InvalidAuthException.class)
    public InvalidAuthException handledAuthException (InvalidAuthException e) {
        return e;
    }
}
