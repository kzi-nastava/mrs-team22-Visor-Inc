package inc.visor.voom.app.driver.history;

import java.util.Date;
import java.util.List;

public class Ride {

    private final Date date;
    private final String startTime;
    private final String endTime;
    private final String source;
    private final String destination;
    private final String status;
    private final int price;
    private final List<Passenger> passengers;
    private final boolean panic;

    // UI state (important for expand/collapse)
    private boolean expanded = false;

    public Ride(Date date,
                String startTime,
                String endTime,
                String source,
                String destination,
                String status,
                int price,
                List<Passenger> passengers,
                boolean panic) {

        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.source = source;
        this.destination = destination;
        this.status = status;
        this.price = price;
        this.passengers = passengers;
        this.panic = panic;
    }

    // getters
    public Date getDate() { return date; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
    public String getSource() { return source; }
    public String getDestination() { return destination; }
    public String getStatus() { return status; }
    public int getPrice() { return price; }
    public List<Passenger> getPassengers() { return passengers; }
    public boolean isPanic() { return panic; }

    public boolean isExpanded() { return expanded; }
    public void setExpanded(boolean expanded) { this.expanded = expanded; }
}

