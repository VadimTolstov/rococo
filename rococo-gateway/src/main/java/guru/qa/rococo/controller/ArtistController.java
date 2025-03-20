package guru.qa.rococo.controller;

import guru.qa.rococo.config.RococoGatewayServiceConfig;
import guru.qa.rococo.model.ArtistJson;
import guru.qa.rococo.service.api.RestArtistClient;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/artist")
@Validated
public class ArtistController {

    private final RestArtistClient restArtistClient;

    @Autowired
    public ArtistController(RestArtistClient restArtistClient) {
        this.restArtistClient = restArtistClient;
    }

    @GetMapping("/{id}")
    public ArtistJson getArtistById(@PathVariable("id") UUID id) {
        return restArtistClient.getArtistById(id);
    }

    @GetMapping
    public Page<ArtistJson> getAllArtists(@PageableDefault Pageable pageable,
                                          @RequestParam(name = "name", required = false) String name) {
        return restArtistClient.getAllArtists(pageable, name);
    }

    @PostMapping
    @SecurityRequirement(name = RococoGatewayServiceConfig.OPEN_API_AUTH_SCHEME)
    public ArtistJson addArtist(@Valid @RequestBody ArtistJson artist) {
        return restArtistClient.addArtist(artist);
    }

    @PatchMapping
    @SecurityRequirement(name = RococoGatewayServiceConfig.OPEN_API_AUTH_SCHEME)
    public ArtistJson updateArtist(@Valid @RequestBody ArtistJson artist) {
        return restArtistClient.updateArtist(artist);
    }
}
