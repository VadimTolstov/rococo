package guru.qa.rococo.controller;

import guru.qa.rococo.ex.BadRequestException;
import guru.qa.rococo.model.UserJson;
import guru.qa.rococo.service.UserdataService;
import jakarta.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserdataController {
    private static final Logger LOG = LoggerFactory.getLogger(UserdataController.class);

    @Autowired
    private UserdataService userdataService;
    private final UserdataService userdataService;

    @Autowired
    public UserdataController(UserdataService userdataService) {
        this.userdataService = userdataService;
    }


    @GetMapping
    public UserJson getUser(@RequestParam @Nonnull String username) {
        LOG.info("### GET /user request received");

        if (username == null || !username.isEmpty()) {
            LOG.warn("### GET /api/user - Authorization header missing or invalid");
            throw new BadRequestException("Authorization header missing or invalid");
        }
    }

    @PatchMapping
    public UserJson updateUser(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody UserJson user
    ) {
        LOG.info("### PATCH /api/user request received");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new BadRequestException("Authorization header missing or invalid");
        }
    }
}
