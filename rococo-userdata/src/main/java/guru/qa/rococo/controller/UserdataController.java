package guru.qa.rococo.controller;

import guru.qa.rococo.model.UserJson;
import guru.qa.rococo.service.UserdataService;
import jakarta.annotation.Nonnull;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.awt.image.BufferedImage;

@RestController
@RequestMapping("/internal/user")
public class UserdataController {
    private static final Logger LOG = LoggerFactory.getLogger(UserdataController.class);

    private final UserdataService userdataService;

    @Autowired
    public UserdataController(UserdataService userdataService) {
        this.userdataService = userdataService;
    }


    @GetMapping
    public UserJson getUser(@RequestParam @Nonnull String username) {
        LOG.info("### GET /internal/user request for username: {}", username);
        return userdataService.getUser(username);
    }

    @PatchMapping
    public UserJson updateUser(@RequestBody UserJson user) {
        LOG.info("### PATCH /internal/user request for user: {}", user.username());
        return userdataService.update(user);
    }
}
