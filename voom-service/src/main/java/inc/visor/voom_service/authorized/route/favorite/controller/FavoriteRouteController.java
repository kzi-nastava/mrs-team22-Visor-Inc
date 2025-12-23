package inc.visor.voom_service.authorized.route.favorite.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import inc.visor.voom_service.authorized.route.favorite.dto.CreateFavoriteRouteRequestDto;
import inc.visor.voom_service.authorized.route.favorite.dto.FavoriteRouteDto;
import inc.visor.voom_service.authorized.route.favorite.dto.FavoriteRouteResponseDto;

@RestController
@RequestMapping("/api/favorite-routes")
public class FavoriteRouteController {

    public FavoriteRouteController() {
    }

    @GetMapping
    public ResponseEntity<List<FavoriteRouteDto>> getFavoriteRoutes(
    ) {

        FavoriteRouteDto dummyRoute1 =
            new FavoriteRouteDto(
                1L,
                "Home → Faculty",
                null 
            );

        FavoriteRouteDto dummyRoute2 =
            new FavoriteRouteDto(
                2L,
                "Home → Job",
                null 
            );

        return ResponseEntity.ok(List.of(dummyRoute1, dummyRoute2));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FavoriteRouteDto> getFavoriteRoutes(
        @PathVariable Long id
    ) {

        FavoriteRouteDto dummyRoute =
            new FavoriteRouteDto(
                1L,
                "Home → Faculty",
                null 
            );
        return ResponseEntity.ok().body(dummyRoute);
    }

    @PostMapping
    public ResponseEntity<FavoriteRouteResponseDto> addFavoriteRoute(
        @RequestBody CreateFavoriteRouteRequestDto request
    ) {

        FavoriteRouteResponseDto response =
            new FavoriteRouteResponseDto(
                1L,
                request.getName()
            );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFavoriteRoute(
        @PathVariable Long id
    ) {

        return ResponseEntity.noContent().build();
    }
}
