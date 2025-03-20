package guru.qa.rococo.service;

import guru.qa.rococo.data.PaintingEntity;
import guru.qa.rococo.data.repository.PaintingRepository;
import guru.qa.rococo.ex.BadRequestException;
import guru.qa.rococo.ex.NotFoundException;
import guru.qa.rococo.model.ArtistJson;
import guru.qa.rococo.model.MuseumJson;
import guru.qa.rococo.model.PaintingRequestJson;
import guru.qa.rococo.model.PaintingResponseJson;
import guru.qa.rococo.service.api.RestArtistClient;
import guru.qa.rococo.service.api.RestMuseumClient;
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
public class PaintingService {

    private final PaintingRepository paintingRepository;
    private final RestArtistClient restArtistClient;
    private final RestMuseumClient restMuseumClient;

    @Autowired
    public PaintingService(PaintingRepository paintingRepository,
                           RestArtistClient restArtistClient,
                           RestMuseumClient restMuseumClient) {
        this.paintingRepository = paintingRepository;
        this.restArtistClient = restArtistClient;
        this.restMuseumClient = restMuseumClient;
    }

    @Transactional(readOnly = true)
    public @Nonnull PaintingResponseJson getPaintingById(@Nonnull UUID id) {
        PaintingEntity entity = paintingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Картина не найдена: " + id));
        ArtistJson artist = restArtistClient.getArtistById(entity.getArtist());
        MuseumJson museum = restMuseumClient.getMuseumById(entity.getMuseum());
        return PaintingResponseJson.fromEntity(entity, artist, museum);
    }

    @Transactional(readOnly = true)
    public @Nonnull Page<PaintingResponseJson> getPaintingsByAuthor(@Nonnull Pageable pageable, @Nonnull UUID authorId) {
        ArtistJson artist = restArtistClient.getArtistById(authorId);
        Page<PaintingEntity> entities = paintingRepository.findAllByArtist(pageable, authorId);

        return entities.map(entity ->
                PaintingResponseJson.fromEntity(
                        entity,
                        artist,
                        restMuseumClient.getMuseumById(entity.getMuseum())
                ));
    }

    @Transactional(readOnly = true)
    public Page<PaintingResponseJson> getAllPaintings(@Nonnull Pageable pageable, @Nullable String title) {
        Page<PaintingEntity> entities = (title != null && !title.isBlank())
                ? paintingRepository.findAllByTitleContainingIgnoreCase(pageable, title)
                : paintingRepository.findAll(pageable);

        return entities.map(entity -> {
            ArtistJson artist = restArtistClient.getArtistById(entity.getArtist());
            MuseumJson museum = restMuseumClient.getMuseumById(entity.getMuseum());
            return PaintingResponseJson.fromEntity(entity, artist, museum);
        });
    }

    @Transactional
    public @Nonnull PaintingResponseJson addPainting(@Nonnull PaintingRequestJson request) {
        validateRequest(request);
        checkContent(request.content());

        ArtistJson artist = restArtistClient.getArtistById(request.artist().id());
        MuseumJson museum = restMuseumClient.getMuseumById(request.museum().id());

        PaintingEntity entity = new PaintingEntity();
        entity.setId(null);
        entity.setTitle(request.title().trim());
        entity.setDescription(request.description().trim());
        entity.setArtist(artist.id());
        entity.setMuseum(museum.id());
        entity.setContent(request.content().getBytes(StandardCharsets.UTF_8));

        return PaintingResponseJson.fromEntity(paintingRepository.save(entity), artist, museum);
    }

    @Transactional
    public @Nonnull PaintingResponseJson updatePainting(@Nonnull PaintingRequestJson request) {
        if (request.id() == null) {
            throw new BadRequestException("ID обязателен для обновления");
        }
        validateRequest(request);

        PaintingEntity entity = paintingRepository.findById(request.id())
                .orElseThrow(() -> new NotFoundException("Картина не найдена: " + request.id()));

        entity.setTitle(request.title().trim());
        entity.setDescription(request.description().trim());
        checkContent(request.content());
        entity.setContent(request.content().getBytes(StandardCharsets.UTF_8));

        updateArtistIfChanged(entity, request.artist().id());
        updateMuseumIfChanged(entity, request.museum().id());

        ArtistJson artist = restArtistClient.getArtistById(entity.getArtist());
        MuseumJson museum = restMuseumClient.getMuseumById(entity.getMuseum());
        // Сохраняем изменения явно
        entity = paintingRepository.save(entity);
        return PaintingResponseJson.fromEntity(entity, artist, museum);
    }

    private void validateRequest(@Nonnull PaintingRequestJson request) {
        if (request.artist() == null || request.artist().id() == null) {
            throw new BadRequestException("Художник или id художника не может быть null");
        }

        if (request.museum() == null || request.museum().id() == null) {
            throw new BadRequestException("Музей или id музея не может быть null");
        }
    }

    private void checkContent(String content) {
        if (content == null || !content.startsWith("data:image")) {
            throw new BadRequestException("Контент должен начинаться на data:image или не может быть пустым");
        }
    }

    private void updateArtistIfChanged(@Nonnull PaintingEntity entity, @Nonnull UUID newArtistId) {
        if (!newArtistId.equals(entity.getArtist())) {
            restArtistClient.getArtistById(newArtistId);
            entity.setArtist(newArtistId);
        }
    }

    private void updateMuseumIfChanged(@Nonnull PaintingEntity entity, @Nonnull UUID newMuseumId) {
        if (!newMuseumId.equals(entity.getMuseum())) {
            restMuseumClient.getMuseumById(newMuseumId);
            entity.setMuseum(newMuseumId);
        }
    }

}
