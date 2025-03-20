package guru.qa.rococo.controller;

import guru.qa.rococo.model.PaintingRequestJson;
import guru.qa.rococo.model.PaintingResponseJson;
import guru.qa.rococo.service.PaintingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/internal/painting")
public class PaintingController {

    private final PaintingService paintingService;

    @Autowired
    public PaintingController(PaintingService paintingService) {
        this.paintingService = paintingService;
    }

    @GetMapping("/{paintingId}")
    public PaintingResponseJson getPainting(@PathVariable(name = "paintingId") UUID paintingId) {
        return paintingService.getPaintingById(paintingId);
    }

    @GetMapping
    public Page<PaintingResponseJson> getAllPaintings(@PageableDefault Pageable pageable,
                                                      @RequestParam(name = "title", required = false) String title) {
        return paintingService.getAllPaintings(pageable, title);
    }

    @GetMapping("/author/{authorId}")
    public Page<PaintingResponseJson> getPaintingsByAuthorId(@PageableDefault Pageable pageable,
                                                             @PathVariable(name = "authorId") UUID authorId) {
        return paintingService.getPaintingsByAuthor(pageable, authorId);
    }

    @PostMapping
    public PaintingResponseJson addPainting(@RequestBody PaintingRequestJson painting) {
        return paintingService.addPainting(painting);
    }

    @PatchMapping
    public PaintingResponseJson updatePainting(@RequestBody PaintingRequestJson painting) {
        return paintingService.updatePainting(painting);
    }
}
