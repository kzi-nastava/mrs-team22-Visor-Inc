package inc.visor.voom_service.shared;

import inc.visor.voom_service.osrm.dto.LatLng;

import java.util.List;

public class PredefinedRoutes {

    public static final List<Route> PREDEFINED_ROUTES = List.of(

            // 1. Liman I → Centar
            new Route(
                    new LatLng(45.2458, 19.8529),
                    new LatLng(45.2556, 19.8449)
            ),

            // 2. Liman III → Spens
            new Route(
                    new LatLng(45.2429, 19.8434),
                    new LatLng(45.2472, 19.8372)
            ),

            // 3. Telep → Liman IV
            new Route(
                    new LatLng(45.2384, 19.8049),
                    new LatLng(45.2441, 19.8586)
            ),

            // 4. Novo Naselje → Detelinara
            new Route(
                    new LatLng(45.2678, 19.8006),
                    new LatLng(45.2627, 19.8149)
            ),

            // 5. Sajlovo → Novo Naselje
            new Route(
                    new LatLng(45.2709, 19.7753),
                    new LatLng(45.2661, 19.8062)
            ),

            // 6. Klisa → Podbara
            new Route(
                    new LatLng(45.2813, 19.8418),
                    new LatLng(45.2692, 19.8593)
            ),

            // 7. Podbara → Centar
            new Route(
                    new LatLng(45.2701, 19.8617),
                    new LatLng(45.2549, 19.8463)
            ),

            // 8. Centar → Spens
            new Route(
                    new LatLng(45.2562, 19.8468),
                    new LatLng(45.2476, 19.8354)
            ),

            // 9. Spens → Liman II
            new Route(
                    new LatLng(45.2483, 19.8336),
                    new LatLng(45.2451, 19.8481)
            ),

            // 10. Liman IV → Telep
            new Route(
                    new LatLng(45.2437, 19.8572),
                    new LatLng(45.2401, 19.8094)
            ),

            // 11. Detelinara → Sajlovo
            new Route(
                    new LatLng(45.2635, 19.8162),
                    new LatLng(45.2718, 19.7789)
            ),

            // 12. Sajlovo → Klisa
            new Route(
                    new LatLng(45.2726, 19.7771),
                    new LatLng(45.2796, 19.8449)
            ),

            // 13. Klisa → Petrovaradin
            new Route(
                    new LatLng(45.2807, 19.8462),
                    new LatLng(45.2471, 19.8733)
            ),

            // 14. Petrovaradin → Liman III
            new Route(
                    new LatLng(45.2478, 19.8749),
                    new LatLng(45.2431, 19.8426)
            ),

            // 15. Liman II → Podbara
            new Route(
                    new LatLng(45.2464, 19.8473),
                    new LatLng(45.2687, 19.8584)
            ),

            // 16. Podbara → Novo Naselje
            new Route(
                    new LatLng(45.2696, 19.8621),
                    new LatLng(45.2667, 19.8038)
            ),

            // 17. Novo Naselje → Centar
            new Route(
                    new LatLng(45.2659, 19.8051),
                    new LatLng(45.2551, 19.8438)
            ),

            // 18. Centar → Sajlovo
            new Route(
                    new LatLng(45.2546, 19.8479),
                    new LatLng(45.2712, 19.7796)
            ),

            // 19. Telep → Detelinara
            new Route(
                    new LatLng(45.2392, 19.8088),
                    new LatLng(45.2621, 19.8156)
            ),

            // 20. Detelinara → Petrovaradin
            new Route(
                    new LatLng(45.2639, 19.8131),
                    new LatLng(45.2469, 19.8721)
            )
    );

    public record Route(LatLng start, LatLng end) {
    }
}

