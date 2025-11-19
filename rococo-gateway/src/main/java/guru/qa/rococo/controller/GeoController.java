package guru.qa.rococo.controller;

import guru.qa.rococo.model.CountryJson;
import guru.qa.rococo.service.api.GrpcGeoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Validated
public class GeoController {
  private final GrpcGeoClient grpcGeoClient;

  @Autowired
  public GeoController(GrpcGeoClient grpcGeoClient) {
    this.grpcGeoClient = grpcGeoClient;
  }

  @GetMapping("/country")
  public Page<CountryJson> getAllCountries(@PageableDefault Pageable pageable) {
    return grpcGeoClient.getCountriesWithPagination(pageable.getPageNumber(), pageable.getPageSize());
  }

}
