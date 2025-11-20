package guru.qa.rococo.service;

import guru.qa.rococo.data.MuseumEntity;
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
import java.util.UUID;

@Service
public class MuseumService {

  private final MuseumRepository museumRepository;


  @Autowired
  public MuseumService(MuseumRepository museumRepository) {
    this.museumRepository = museumRepository;
  }

  @Transactional(readOnly = true)
  public @Nonnull Page<MuseumJson> getAllMuseum(@Nonnull Pageable pageable,
                                                @Nullable String title) {

    Page<MuseumEntity> entities = (title != null && !title.isBlank())
        ? museumRepository.findAllByTitleContainsIgnoreCase(pageable, title.trim())
        : museumRepository.findAll(pageable);
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

    // Создание музея
    MuseumEntity entity = new MuseumEntity();
    entity.setTitle(museum.title().trim());
    entity.setDescription(museum.description().trim());
    entity.setPhoto(museum.photo().getBytes(StandardCharsets.UTF_8));
    entity.setGeo(museum.geo().id());

    return MuseumJson.fromEntity(museumRepository.save(entity));
  }

//  @Transactional
//  public @Nonnull MuseumJson updateMuseum(@Nonnull MuseumJson museum) {
//    validateRequest(museum);
//    checkPhoto(museum.photo());
//
//    MuseumEntity entity = museumRepository.findById(museum.id())
//        .orElseThrow(() -> new NotFoundException("Музей не найден по ID: " + museum.id()));
//
//    // Обновление основных полей
//    Optional.ofNullable(museum.title()).ifPresent(title -> entity.setTitle(title.trim()));
//    Optional.ofNullable(museum.description()).ifPresent(desc -> entity.setDescription(desc.trim()));
//
//    // Обновление фото
//    entity.setPhoto(museum.photo().getBytes(StandardCharsets.UTF_8));
//
//    // Обновление геолокации и страны
//    CountryEntity countryEntity = resolveCountry(museum.geo().country());
//    GeoEntity geoEntity = getOrCreateGeo(museum.geo().city().trim(), countryEntity.getId());
//    entity.setGeo(geoEntity);
//
//    return MuseumJson.fromEntity(museumRepository.save(entity));
//  }


  // Вспомогательные методы
  private void validateRequest(@Nonnull MuseumJson museum) {
    if (museum.title() == null || museum.title().isBlank()) {
      throw new BadRequestException("Название музея не должно быть пустым или содержать одни пробелы");
    } else if (museum.description() == null || museum.description().isBlank()) {
      throw new BadRequestException("Описание музея не должно быть пустым или содержать одни пробелы");
    } else if (museum.geo() == null || museum.geo().city() == null || museum.geo().city().isBlank()) {
      throw new BadRequestException("Город обязателен");
    } else if (museum.geo().country() == null || (museum.geo().country().id() == null && museum.geo().country().name() == null)) {
      throw new BadRequestException("Укажите ID или название страны");
    }
  }
  private void checkPhoto(String photo) {
    if (photo == null || photo.isEmpty() || !photo.startsWith("data:image")) {
      throw new BadRequestException("Фото должно быть в формате data:image");
    }
  }
}