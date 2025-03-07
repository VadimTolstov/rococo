package guru.qa.rococo.controller;

import guru.qa.rococo.config.RococoGatewayServiceConfig;
import guru.qa.rococo.model.PaintingJson;
import guru.qa.rococo.service.api.RestPaintingClient;
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
@RequestMapping("/api/painting")
@Validated
public class PaintingController {
    private final RestPaintingClient restPaintingClient;

    @Autowired
    public PaintingController(RestPaintingClient restPaintingClient) {
        this.restPaintingClient = restPaintingClient;
    }

    @GetMapping("/{id}")
    public PaintingJson getPaintingById(@PathVariable("id") UUID id) {
        return restPaintingClient.getPaintingById(id);
    }

    @GetMapping
    public Page<PaintingJson> getAllPaintings(@PageableDefault Pageable pageable,
                                              @RequestParam(name = "title", required = false) String title) {
        return restPaintingClient.getAllPaintings(pageable, title);
    }

    @GetMapping("/author/{id}")
    public Page<PaintingJson> getPaintingsByAuthorId(@PathVariable("id") UUID id, @PageableDefault Pageable pageable) {
        return restPaintingClient.getPaintingsByAuthorId(id, pageable);
    }

    @PostMapping
    @SecurityRequirement(name = RococoGatewayServiceConfig.OPEN_API_AUTH_SCHEME)
    public PaintingJson addPainting(@Valid @RequestBody PaintingJson painting) {
        return restPaintingClient.addPainting(painting);
    }

    @PatchMapping
    @SecurityRequirement(name = RococoGatewayServiceConfig.OPEN_API_AUTH_SCHEME)
    public PaintingJson updatePainting(@Valid @RequestBody PaintingJson painting) {
        return restPaintingClient.updatePainting(painting);
    }
}
