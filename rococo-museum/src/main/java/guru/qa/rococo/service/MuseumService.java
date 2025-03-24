package guru.qa.rococo.service;

import guru.qa.rococo.data.CountryEntity;
import guru.qa.rococo.data.GeoEntity;
import guru.qa.rococo.data.MuseumEntity;
import guru.qa.rococo.data.repository.CountryRepository;
import guru.qa.rococo.data.repository.GeoRepository;
import guru.qa.rococo.data.repository.MuseumRepository;
import guru.qa.rococo.ex.BadRequestException;
import guru.qa.rococo.ex.NotFoundException;
import guru.qa.rococo.model.CountryJson;
import guru.qa.rococo.model.MuseumJson;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

@Service
public class MuseumService {

    private final MuseumRepository museumRepository;
    private final CountryRepository countryRepository;
    private final GeoRepository geoRepository;

    @Autowired
    public MuseumService(MuseumRepository museumRepository, CountryRepository countryRepository, GeoRepository geoRepository) {
        this.museumRepository = museumRepository;
        this.countryRepository = countryRepository;
        this.geoRepository = geoRepository;
    }

    @Transactional(readOnly = true)
    public @Nonnull Page<MuseumJson> getAllMuseum(@Nonnull Pageable pageable,
                                                  @Nullable String title) {
        if (title != null) {
            title = title.trim();
        }
        Page<MuseumEntity> entities = title == null
                ? museumRepository.findAll(pageable)
                : museumRepository.findAllByTitleContainsIgnoreCase(pageable, title);
        return entities.map(MuseumJson::fromEntity);
    }

    @Transactional(readOnly = true)
    public @Nonnull MuseumJson getMuseumById(@Nonnull UUID id) {
        return MuseumJson.fromEntity(
                museumRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Музей не найден по id: " + id))
        );
    }

    @Transactional
    public @Nonnull MuseumJson addMuseum(@Nonnull MuseumJson museum) {
        validateRequest(museum);
        checkPhoto(museum.photo());

        // Обработка страны
        CountryEntity countryEntity = resolveCountry(museum.geo().country());

        // Создание/получение геолокации
        GeoEntity geoEntity = getOrCreateGeo(museum.geo().city().trim(), countryEntity.getId());

        // Создание музея
        MuseumEntity entity = new MuseumEntity();
        entity.setTitle(museum.title().trim());
        entity.setDescription(museum.description().trim());
        entity.setPhoto(museum.photo().getBytes(StandardCharsets.UTF_8));
        entity.setGeo(geoEntity);

        return MuseumJson.fromEntity(museumRepository.save(entity));
    }

    @Transactional
    public @Nonnull MuseumJson updateMuseum(@Nonnull MuseumJson museum) {
        validateRequest(museum);
        checkPhoto(museum.photo());

        MuseumEntity entity = museumRepository.findById(museum.id())
                .orElseThrow(() -> new NotFoundException("Музей не найден по ID: " + museum.id()));

        // Обновление основных полей
        Optional.ofNullable(museum.title()).ifPresent(title -> entity.setTitle(title.trim()));
        Optional.ofNullable(museum.description()).ifPresent(desc -> entity.setDescription(desc.trim()));

        // Обновление фото
        entity.setPhoto(museum.photo().getBytes(StandardCharsets.UTF_8));

        // Обновление геолокации и страны
        CountryEntity countryEntity = resolveCountry(museum.geo().country());
        GeoEntity geoEntity = getOrCreateGeo(museum.geo().city().trim(), countryEntity.getId());
        entity.setGeo(geoEntity);

        return MuseumJson.fromEntity(museumRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public @Nonnull Page<CountryJson> getAllCountries(@Nonnull Pageable pageable) {
        return countryRepository.findAll(pageable)
                .map(CountryJson::fromEntity);
    }

    // Вспомогательные методы
    private void validateRequest(@Nonnull MuseumJson museum) {
        if (museum.geo() == null || museum.geo().city() == null || museum.geo().city().isBlank()) {
            throw new BadRequestException("Город обязателен");
        }
        if (museum.geo().country() == null || (museum.geo().country().id() == null && museum.geo().country().name() == null)) {
            throw new BadRequestException("Укажите ID или название страны");
        }
    }

    private @Nonnull CountryEntity resolveCountry(@Nonnull CountryJson countryJson) {
        if (countryJson.id() != null) {
            return countryRepository.findById(countryJson.id())
                    .orElseThrow(() -> new NotFoundException("Страна не найдена по ID: " + countryJson.id()));
        } else {
            String countryName = countryJson.name().trim();
            return countryRepository.findByName(countryName)
                    .orElseGet(() -> {
                        if (countryRepository.existsByName(countryName)) {
                            throw new BadRequestException("Страна '" + countryName + "' уже существует");
                        }
                        CountryEntity newCountry = new CountryEntity();
                        newCountry.setName(countryName);
                        return countryRepository.save(newCountry);
                    });
        }
    }

    private void checkPhoto(String photo) {
        if (photo == null || photo.isEmpty() || !photo.startsWith("data:image")) {
            throw new BadRequestException("Фото должно быть в формате data:image");
        }
    }

    private @Nonnull GeoEntity getOrCreateGeo(String city, UUID countryId) {
        return geoRepository.findByCityAndCountryId(city, countryId)
                .orElseGet(() -> {
                    GeoEntity newGeo = new GeoEntity();
                    newGeo.setCity(city);
                    newGeo.setCountry(countryRepository.getReferenceById(countryId));
                    return geoRepository.save(newGeo);
                });
    }
}