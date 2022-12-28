package com.revature.sylvester.controllers;

import com.revature.sylvester.dtos.requests.NewPostRequest;
import com.revature.sylvester.dtos.responses.Principal;
import com.revature.sylvester.entities.Post;
import com.revature.sylvester.entities.UserProfile;
import com.revature.sylvester.services.LikeService;
import com.revature.sylvester.services.PostService;
import com.revature.sylvester.services.TokenService;
import com.revature.sylvester.services.UserProfileService;
import com.revature.sylvester.utils.custom_exceptions.InvalidAuthException;
import com.revature.sylvester.utils.custom_exceptions.InvalidPostException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;
    private final LikeService likeService;
    private final UserProfileService profileService;
    private final TokenService tokenService;

    public PostController(PostService postService, LikeService likeService, UserProfileService profileService,
                          TokenService tokenService) {
        this.postService = postService;
        this.likeService = likeService;
        this.profileService = profileService;
        this.tokenService = tokenService;
    }

    @PostMapping
    public void create(@RequestBody NewPostRequest req, HttpServletRequest servReq) {
        String token = servReq.getHeader("authorization");

        if(token == null || token.isEmpty())
            throw new InvalidAuthException("Invalid token");

        Principal principal = tokenService.extractRequesterDetails(token);

        if(principal == null)
            throw new InvalidAuthException("Please log in to create a post");

        if(!principal.isActive())
            throw new InvalidAuthException("Your account is not active");

        String userId = principal.getUserId();
        UserProfile profile = profileService.getProfileByUserId(userId);

        if(postService.isValidContent(req.getContent()))
            postService.savePostByUserId(req, userId, principal.getUsername(), profile.getDisplayName());
        else
            throw new InvalidPostException("Post content must be 128 characters or less");
    }

    @GetMapping
    public List<Post> getAll() {
        return postService.getAllPosts();
    }

    @GetMapping("/user")
    public List<Post> getByUserId(@RequestParam String id) {
        return postService.getAllPostsByUserId(id);
    }

    @GetMapping("/liked")
    public List<Post> filterByLiked(HttpServletRequest servReq) {
        String token = servReq.getHeader("authorization");

        if(token == null || token.isEmpty())
            throw new InvalidAuthException("Invalid token");

        Principal principal = tokenService.extractRequesterDetails(token);

        if(principal == null)
            throw new InvalidAuthException("Please log in to create a post");

        if(!principal.isActive())
            throw new InvalidAuthException("Your account is not active");

        List<String> userLikedPostIds = likeService.getAllLikedPostIdsByUserId(principal.getUserId());
        return postService.getLikedPostsByUserLikedPostIds(userLikedPostIds);
    }

    @GetMapping("/posted")
    public List<Post> filterByPosted(@RequestParam int limit) {
        return postService.filterPostsByPosted(limit);
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(InvalidPostException.class)
    public InvalidPostException handledPostException (InvalidPostException e) {
        return e;
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(InvalidAuthException.class)
    public InvalidAuthException handledAuthException (InvalidAuthException e) {
        return e;
    }
}
