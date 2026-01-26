package inc.visor.voom_service.ride.helpers;

import inc.visor.voom_service.ride.dto.RideHistoryDto;
import inc.visor.voom_service.ride.model.Ride;

public class RideHistoryFormatter {

    public static RideHistoryDto getRideHistoryDto(Ride ride) {
        RideHistoryDto rideHistoryDto = new RideHistoryDto();
        rideHistoryDto.setId(ride.getId());
        rideHistoryDto.setRideRequest(ride.getRideRequest());
        rideHistoryDto.setRideRoute(ride.getRideRequest().getRideRoute());
        formatRideHistoryDtoAddresses(rideHistoryDto);
        rideHistoryDto.setCancelledBy(ride.getRideRequest().getCancelledBy());
        rideHistoryDto.setPassengers(ride.getPassengers());
        rideHistoryDto.setStatus(ride.getStatus());
        rideHistoryDto.setFinishedAt(ride.getFinishedAt());
        rideHistoryDto.setStartedAt(ride.getStartedAt());
        return rideHistoryDto;
    }

    public static String formatAddress(String address) {
        if (address == null) return null;

        int commaCount = 0;
        int index = -1;

        for (int i = 0; i < 3; i++) {
            index = address.indexOf(',', index + 1);
            if (index == -1) break;
            commaCount++;
        }

        return (commaCount == 3) ? address.substring(0, index).trim() : address;
    }

    public static void formatRideHistoryDtoAddresses(RideHistoryDto rideHistoryDto) {
        String ogPickupAddress = rideHistoryDto.getRideRoute().getPickupPoint().getAddress();
        String ogDropoffAddress = rideHistoryDto.getRideRoute().getDropoffPoint().getAddress();

        String formattedPickup = formatAddress(ogDropoffAddress);
        String formattedDropoff = formatAddress(ogPickupAddress);

        rideHistoryDto.getRideRoute().getPickupPoint().setAddress(formattedPickup);
        rideHistoryDto.getRideRoute().getDropoffPoint().setAddress(formattedDropoff);
    }
}
