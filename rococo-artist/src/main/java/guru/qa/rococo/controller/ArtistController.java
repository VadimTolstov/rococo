package guru.qa.rococo.controller;

import guru.qa.rococo.model.ArtistJson;
import guru.qa.rococo.service.ArtistService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/internal/artist")
@Validated
public class ArtistController {
    private final ArtistService artistService;

    @Autowired
    public ArtistController(ArtistService artistService) {
        this.artistService = artistService;
    }

    @GetMapping("/{id}")
    public ArtistJson getArtist(@PathVariable(name = "id") UUID id) {
        return artistService.getArtistById(id);
    }

    @GetMapping
    public Page<ArtistJson> getAllArtists(
            @PageableDefault Pageable pageable,
            @RequestParam(name = "name", required = false) String name) {
        return artistService.getAllArtists(pageable, name);
    }

    @PostMapping
    public ArtistJson addArtist(@Valid @RequestBody ArtistJson artist) {
        return artistService.addArtist(artist);
    }

    @PatchMapping
    public ArtistJson updateArtist(@Valid @RequestBody ArtistJson artist) {
        return artistService.updateArtist(artist);
    }
}
