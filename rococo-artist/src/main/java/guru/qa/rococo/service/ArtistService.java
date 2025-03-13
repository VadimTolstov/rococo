package guru.qa.rococo.service;

import guru.qa.rococo.data.ArtistEntity;
import guru.qa.rococo.data.repository.ArtistRepository;
import guru.qa.rococo.ex.BadRequestException;
import guru.qa.rococo.ex.NotFoundException;
import guru.qa.rococo.model.ArtistJson;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
public class ArtistService {

    private final ArtistRepository artistRepository;

    @Autowired
    public ArtistService(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    @Transactional(readOnly = true)
    public @Nonnull Page<ArtistJson> getAllArtists(@Nonnull Pageable pageable,
                                                   @Nullable String name) {
        Page<ArtistEntity> entities = name == null
                ? artistRepository.findAll(pageable)
                : artistRepository.findByNameContainingIgnoreCase(name.trim(), pageable);
        return entities.map(ArtistJson::fromEntity);
    }

    @Transactional
    public @Nonnull ArtistJson addArtist(@Nonnull ArtistJson artist) {
        ArtistEntity entity = artist.toEntity();
        entity.setId(null);
        return ArtistJson.fromEntity(artistRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public @Nonnull ArtistJson getArtistById(@Nonnull UUID id) {
        return ArtistJson.fromEntity(
                artistRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Artist not found id:" + id))
        );
    }

    @Transactional
    public @Nonnull ArtistJson updateArtist(@Nonnull ArtistJson artist) {
        if (artist.id() == null) {
            throw new BadRequestException("id: ID художника обязателен для заполнения");
        }
        ArtistEntity existing = artistRepository.findById(artist.id())
                .orElseThrow(() -> new NotFoundException("Artist not found id:" + artist.id()));

        if (artist.name() != null) {
            existing.setName(artist.name().trim());
        }

        if (artist.biography() != null) {
            existing.setBiography(artist.biography().trim());
        }

        if (artist.photo() != null && artist.photo().startsWith("data:image")) {
            existing.setPhoto(artist.photo().getBytes(StandardCharsets.UTF_8));
        }
        return ArtistJson.fromEntity(artistRepository.save(existing));
    }
}
