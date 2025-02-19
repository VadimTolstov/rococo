package guru.qa.rococo.service;

import jakarta.annotation.Nonnull;
import org.springframework.data.domain.Pageable;
import guru.qa.rococo.model.mm.ArtistJson;
import org.springframework.data.domain.Page;


import java.util.Optional;
import java.util.UUID;

public interface ArtistService {
    @Nonnull
    ArtistJson addArtist(@Nonnull ArtistJson artist);

    //    {
//        "priority": 2,
//            "request": {
//        "method": "OPTIONS",
//                "url": "/api/artist",
//                "headers": {
//            "Access-Control-Request-Method": {
//                "contains": "POST"
//            }
//        }
//    },
//        "response": {
//        "status": 200,
//                "headers": {
//            "Access-Control-Allow-Credentials": "true",
//                    "Access-Control-Allow-Headers": "authorization, content-type",
//                    "Access-Control-Allow-Methods": "POST",
//                    "Access-Control-Allow-Origin": "http://127.0.0.1:3000"
//        }
    @Nonnull
    Page<ArtistJson> getAllArtists(@Nonnull Pageable pageable);

    /*
      "mappings": [
        {
          "priority": 1,
          "request": {
            "method": "OPTIONS",
            "url": "/api/artist?size=18&page=0",
            "headers": {
              "Access-Control-Request-Method": {
                "contains": "GET"
              }
            }
          }
     */
    Optional<ArtistJson> getArtistById(@Nonnull UUID id);

    /*{
      "mappings": [
        {
          "priority": 1,
          "request": {
            "method": "GET",
            "url": "/api/artist/19bbbbb8-b687-4eec-8ba0-c8917c0a58a3"
          },
          "response": {
            "status": 200,
            "jsonBody": {
              "id": "19bbbbb8-b687-4eec-8ba0-c8917c0a58a3",
              "name": "Ренуар",
              "biography": "Французский живописец, график и скульптор, один из основных представителей импрессионизма.",
              "photo": "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEAYABgAAD

     */
    Optional<ArtistJson> getArtistByName(@Nonnull String name);

    /*{
          "priority": 2,
          "request": {
            "method": "OPTIONS",
            "url": "/api/artist?name=%D1%88%D0%B8%D1%88%D0%BA%D0%B8%D0%BD",
            "headers": {
              "Access-Control-Request-Method": {
                "contains": "GET"
              }
            }
          }

     */
    @Nonnull
    ArtistJson updateArtist(@Nonnull UUID id, @Nonnull ArtistJson artist);
//    {
//        "mappings": [
//        {
//            "priority": 1,
//                "request": {
//            "method": "OPTIONS",
//                    "url": "/api/artist",
//                    "headers": {
//                "Access-Control-Request-Method": {
//                    "contains": "PATCH"
//                }
//            }
//        },
//            "response": {
//            "status": 200,
//                    "headers": {
//                "Access-Control-Allow-Credentials": "true",
//                        "Access-Control-Allow-Headers": "authorization, content-type",
//                        "Access-Control-Allow-Methods": "PATCH",
//                        "Access-Control-Allow-Origin": "http://127.0.0.1:3000"
//            }
//        }
//        }
}
