package inc.visor.voom.app.driver.history.models;

public class Passenger {
    private final String name;
    private final String lastName;
    private final boolean orderedRide;

    public Passenger(String name, String lastName, boolean orderedRide) {
        this.name = name;
        this.lastName = lastName;
        this.orderedRide = orderedRide;
    }

    public String getFullName() {
        return name + " " + lastName;
    }

    public boolean isOrderedRide() {
        return orderedRide;
    }
}

