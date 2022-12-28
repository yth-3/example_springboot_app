package com.revature.sylvester.controllers;

import com.revature.sylvester.dtos.requests.NewReplyRequest;
import com.revature.sylvester.dtos.responses.Principal;
import com.revature.sylvester.entities.Reply;
import com.revature.sylvester.entities.UserProfile;
import com.revature.sylvester.services.PostService;
import com.revature.sylvester.services.ReplyService;
import com.revature.sylvester.services.TokenService;
import com.revature.sylvester.services.UserProfileService;
import com.revature.sylvester.utils.custom_exceptions.InvalidAuthException;
import com.revature.sylvester.utils.custom_exceptions.InvalidPostException;
import com.revature.sylvester.utils.custom_exceptions.InvalidReplyException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/replies")
public class ReplyController {
    private final ReplyService replyService;
    private final PostService postService;
    private final UserProfileService profileService;
    private final TokenService tokenService;

    public ReplyController(ReplyService replyService, PostService postService, UserProfileService profileService,
                           TokenService tokenService) {
        this.replyService = replyService;
        this.postService = postService;
        this.profileService = profileService;
        this.tokenService = tokenService;
    }

    @PostMapping
    public void create(@RequestBody NewReplyRequest req, HttpServletRequest servReq) {
        String token = servReq.getHeader("authorization");

        if(token == null || token.isEmpty())
            throw new InvalidAuthException("Invalid token");

        Principal principal = tokenService.extractRequesterDetails(token);

        if(principal == null)
            throw new InvalidAuthException("Please log in to create a reply");

        if(!principal.isActive())
            throw new InvalidAuthException("Your account is not active");

        String userId = principal.getUserId();
        UserProfile profile = profileService.getProfileByUserId(userId);

        if(replyService.isValidContent(req.getReply())) {
            if(postService.isValidPostId(req.getPostId()))
                replyService.saveReplyByUserId(req, userId, principal.getUsername(), profile.getDisplayName());
            else
                throw new InvalidPostException("Post does not exist");
        } else
            throw new InvalidReplyException("Reply must be 128 characters or less");
    }

    @GetMapping("/post")
    public List<Reply> getAllByPostId(@RequestParam String id) {
        return replyService.getAllRepliesByPostId(id);
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(InvalidReplyException.class)
    public InvalidReplyException handledReplyException (InvalidReplyException e) {
        return e;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
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
