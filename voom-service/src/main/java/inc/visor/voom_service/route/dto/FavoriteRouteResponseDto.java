package inc.visor.voom_service.route.dto;

public class FavoriteRouteResponseDto {

    private Long id;
    private String name;

    public FavoriteRouteResponseDto() {}

    public FavoriteRouteResponseDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

