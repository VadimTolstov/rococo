package guru.qa.rococo.service;

import guru.qa.rococo.data.CountryEntity;
import guru.qa.rococo.data.GeoEntity;
import guru.qa.rococo.data.repository.CountryRepository;
import guru.qa.rococo.data.repository.GeoRepository;
import guru.qa.rococo.ex.BadRequestException;
import guru.qa.rococo.ex.NotFoundException;
import guru.qa.rococo.model.CountryJson;
import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class GeoService {

    private final CountryRepository countryRepository;
    private final GeoRepository geoRepository;

    @Autowired
    public GeoService(CountryRepository countryRepository, GeoRepository geoRepository) {
        this.countryRepository = countryRepository;
        this.geoRepository = geoRepository;
    }

    @Transactional(readOnly = true)
    public @Nonnull Page<CountryJson> getAllCountries(@Nonnull Pageable pageable) {
        return countryRepository.findAll(pageable)
                .map(CountryJson::fromEntity);
    }

//    // Вспомогательные методы
//    private void validateRequest(@Nonnull MuseumJson museum) {
//        if (museum.title() == null || museum.title().isBlank()) {
//            throw new BadRequestException("Название музея не должно быть пустым или содержать одни пробелы");
//        } else if (museum.description() == null || museum.description().isBlank()) {
//            throw new BadRequestException("Описание музея не должно быть пустым или содержать одни пробелы");
//        } else if (museum.geo() == null || museum.geo().city() == null || museum.geo().city().isBlank()) {
//            throw new BadRequestException("Город обязателен");
//        } else if (museum.geo().country() == null || (museum.geo().country().id() == null && museum.geo().country().name() == null)) {
//            throw new BadRequestException("Укажите ID или название страны");
//        }
//    }

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