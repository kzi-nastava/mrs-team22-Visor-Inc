package inc.visor.voom_service.route.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import inc.visor.voom_service.route.dto.CreateFavoriteRouteRequestDto;
import inc.visor.voom_service.route.dto.FavoriteRouteDto;
import inc.visor.voom_service.route.dto.FavoriteRouteResponseDto;

@RestController
@RequestMapping("/api/favorite")
public class FavoriteRouteController {

    public FavoriteRouteController() {
    }

    @GetMapping
    public ResponseEntity<List<FavoriteRouteDto>> getFavoriteRoutes() {

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
    public ResponseEntity<FavoriteRouteDto> getFavoriteRoute(@PathVariable Long id) {

        FavoriteRouteDto dummyRoute =
            new FavoriteRouteDto(
                1L,
                "Home → Faculty",
                null 
            );
        return ResponseEntity.ok().body(dummyRoute);
    }

    @PostMapping
    public ResponseEntity<FavoriteRouteResponseDto> addFavoriteRoute(@RequestBody CreateFavoriteRouteRequestDto request) {

        FavoriteRouteResponseDto response =
            new FavoriteRouteResponseDto(
                1L,
                request.getName()
            );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping
    public ResponseEntity<FavoriteRouteResponseDto> addFavoriteRoute(@RequestBody FavoriteRouteResponseDto request) {

        return ResponseEntity.ok().body(request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFavoriteRoute(@PathVariable Long id) {

        return ResponseEntity.noContent().build();
    }
}
