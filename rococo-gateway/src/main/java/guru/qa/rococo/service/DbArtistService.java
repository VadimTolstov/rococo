//package guru.qa.rococo.service;
//
//import jakarta.annotation.Nonnull;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ResponseStatusException;
//import guru.qa.rococo.data.ArtistEntity;
//import guru.qa.rococo.data.repository.ArtistRepository;
//import guru.qa.rococo.model.mm.ArtistJson;
//
//import java.nio.charset.StandardCharsets;
//import java.util.Optional;
//import java.util.UUID;
//
//@Component
//public class DbArtistService implements ArtistService {
//
//    private final ArtistRepository artistRepository;
//
//    @Autowired
//    public DbArtistService(ArtistRepository artistRepository) {
//        this.artistRepository = artistRepository;
//    }
//
//    @Nonnull
//    @Override
//    public ArtistJson addArtist(@Nonnull ArtistJson artist) {
//        ArtistEntity entity = artist.toEntity();
//        ArtistEntity artistEntity = artistRepository.save(entity);
//        return ArtistJson.fromEntity(artistEntity);
//    }
//
//    @Nonnull
//    @Override
//    public Page<ArtistJson> getAllArtists(@Nonnull Pageable pageable) {
//        // Получаем страницу с артистами
//        Page<ArtistEntity> artistPage = artistRepository.findAll(pageable);
//
//        // Преобразуем Page<ArtistEntity> в Page<ArtistJson>
//        return artistPage.map(ArtistJson::fromEntity);
//    }
//
//    @Override
//    @Nonnull
//    public Optional<ArtistJson> getArtistById(@Nonnull UUID id) {
//        Optional<ArtistJson> artistJson = artistRepository.findById(id).map(ArtistJson::fromEntity);
//           return artistJson.isEmpty() ? Optional.empty() : artistJson;
//    }
//
//    @Override
//    public Optional<ArtistJson> getArtistByName(@Nonnull String name) {
//        return Optional.empty();
//    }
//
//    @Nonnull
//    @Override
//    public ArtistJson updateArtist(@Nonnull UUID id, ArtistJson artist) {
//        ArtistEntity entity = artistRepository.findById(id)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Artist not found with id: " + id));
//
//        // Обновляем данные артиста
//        entity.setName(artist.name());
//        entity.setBiography(artist.biography());
//        entity.setPhoto(artist.photo() != null ? artist.photo().getBytes(StandardCharsets.UTF_8) : null);
//
//        // Сохраняем обновленные данные
//        ArtistEntity updatedEntity = artistRepository.save(entity);
//
//        // Возвращаем обновленный объект ArtistJson
//        return ArtistJson.fromEntity(updatedEntity);
//    }
//}
