package guru.qa.rococo.controller;

import guru.qa.rococo.config.RococoGatewayServiceConfig;
import guru.qa.rococo.model.CountryJson;
import guru.qa.rococo.model.MuseumJson;
import guru.qa.rococo.service.api.RestMuseumClient;
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
@RequestMapping("/api")
@Validated
public class MuseumController {
    private final RestMuseumClient restMuseumClient;

    @Autowired
    public MuseumController(RestMuseumClient restMuseumClient) {
        this.restMuseumClient = restMuseumClient;
    }

    @GetMapping("/museum/{id}")
    public MuseumJson getMuseumById(@PathVariable("id") UUID id) {
        return restMuseumClient.getMuseumById(id);
    }

    @GetMapping("/museum")
    public Page<MuseumJson> getAllMuseums(
            @PageableDefault Pageable pageable,
            @RequestParam(name = "title", required = false) String title) {
        return restMuseumClient.getAllMuseums(pageable, title);
    }

    @GetMapping("/country")
    public Page<CountryJson> getAllCountries(@PageableDefault Pageable pageable) {
        return restMuseumClient.getAllCountries(pageable);
    }

    @PostMapping("/museum")
    @SecurityRequirement(name = RococoGatewayServiceConfig.OPEN_API_AUTH_SCHEME)
    public MuseumJson addMuseum(@Valid @RequestBody MuseumJson museum) {
        return restMuseumClient.addMuseum(museum);
    }

    @PatchMapping("/museum")
    @SecurityRequirement(name = RococoGatewayServiceConfig.OPEN_API_AUTH_SCHEME)
    public MuseumJson updateMuseum(@Valid @RequestBody MuseumJson museum) {
        return restMuseumClient.updateMuseum(museum);
    }
}
