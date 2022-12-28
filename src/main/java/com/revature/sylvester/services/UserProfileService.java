package com.revature.sylvester.services;

import com.revature.sylvester.dtos.requests.NewUserRequest;
import com.revature.sylvester.dtos.requests.UpdateProfileRequest;
import com.revature.sylvester.entities.User;
import com.revature.sylvester.entities.UserProfile;
import com.revature.sylvester.repositories.UserProfileRepository;
import com.revature.sylvester.repositories.UserRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class UserProfileService {
    private final UserProfileRepository profileRepo;

    public UserProfileService(UserProfileRepository profileRepo) {
        this.profileRepo = profileRepo;
    }

    public UserProfile createProfile(NewUserRequest req, User user) {
        UserProfile createdProfile = new UserProfile(UUID.randomUUID().toString(), req.getDisplayName(), null,
                req.getBirthDate(), null, null, null, user, user.getUsername());

        profileRepo.save(createdProfile);
        return createdProfile;
    }

    public void updateProfile(UpdateProfileRequest req, String username, String profileId) {
        profileRepo.update(req.getDisplayName(), req.getLocation(), req.getBirthDate(), req.getOccupation(),
                req.getBio(), req.getProfilePicUrl(), username, profileId);
    }

    public UserProfile getProfileByUserId(String userId) {
        return profileRepo.findByUserId(userId);
    }

    public boolean isEmptyDisplayName(String displayName) {
        return displayName.isEmpty();
    }

    public boolean isValidBirthDate(LocalDate birthDate) {
        LocalDate currentDate = LocalDate.now();
        int age = Period.between(birthDate, currentDate).getYears();

        return age > 13;
    }
}
