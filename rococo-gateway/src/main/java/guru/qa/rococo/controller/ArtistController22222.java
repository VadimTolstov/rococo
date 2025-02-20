//package guru.qa.rococo.controller;
//
//import jakarta.annotation.Nonnull;
//import jakarta.validation.Valid;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.web.PageableDefault;
//import org.springframework.http.HttpStatus;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.server.ResponseStatusException;
//import guru.qa.rococo.model.mm.ArtistJson;
//import guru.qa.rococo.service.ArtistService;
//import org.springframework.security.oauth2.jwt.Jwt;
//
//import java.util.Optional;
//import java.util.UUID;
//
//@RestController
//@RequestMapping("/api/artist")
//public class ArtistController22222 {
//    private final ArtistService dbArtistService;
//
//    @Autowired
//    public ArtistController22222(ArtistService dbArtistService) {
//        this.dbArtistService = dbArtistService;
//    }
//
//    @PostMapping
//    @Nonnull
//    @ResponseStatus(HttpStatus.CREATED)
//    public ArtistJson addArtist(@Valid @RequestBody @Nonnull ArtistJson artist,
//                                @RequestHeader(value = "Authorization", required = false) String authorization,
//                                @Nonnull @AuthenticationPrincipal Jwt principal) {
//        // Проверка наличия Bearer токена в заголовке Authorization
//        if (authorization == null || !authorization.startsWith("Bearer ")) {
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorization header is missing or invalid");
//        }
//
//        // Извлекаем имя пользователя из JWT токена
//        String username = principal.getClaim("sub");
//
//        // Логика добавления артиста
//        return dbArtistService.addArtist(artist);
//    }
//
//    @GetMapping
//    @Nonnull
//    public Page<ArtistJson> getArtists(@PageableDefault Pageable pageable) {
//        return dbArtistService.getAllArtists(pageable);
//    }
//
//    @GetMapping("/{id}")
//    @Nonnull
//    @ResponseStatus(HttpStatus.OK)
//    public ArtistJson getArtistById(@Nonnull @PathVariable UUID id) {
//        return dbArtistService.getArtistById(id)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Artist not found with id: " + id));
//    }
//
//    public Optional<ArtistJson> getArtistByName(@Nonnull String name) {
//        return Optional.empty();
//    }
//
//    @PatchMapping
//    @Nonnull
//    @ResponseStatus(HttpStatus.OK)
//    public ArtistJson updateArtist(@Valid @RequestBody @Nonnull ArtistJson artist,
//                                   @RequestHeader(value = "Authorization", required = false) String authorization,
//                                   @Nonnull @AuthenticationPrincipal Jwt principal) {
//        // Проверка наличия Bearer токена в заголовке Authorization
//        if (authorization == null || !authorization.startsWith("Bearer ")) {
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorization header is missing or invalid");
//        }
//
//        // Извлекаем имя пользователя из JWT токена (если нужно)
//        String username = principal.getClaim("sub");
//
//        // Проверяем, что ID артиста передан в теле запроса
//        if (artist.id() == null) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Artist ID is required");
//        }
//
//        // Логика обновления артиста
//        return dbArtistService.updateArtist(artist.id(), artist);
//    }
//}
