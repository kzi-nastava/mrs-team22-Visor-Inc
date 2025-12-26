package inc.visor.voom.app.driver.history;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import inc.visor.voom.app.driver.history.models.Passenger;
import inc.visor.voom.app.driver.history.models.Ride;

public class DriverRideHistoryViewModel extends ViewModel {

    private final MutableLiveData<List<Ride>> rides = new MutableLiveData<>();

    public static List<Date> getSeenDates() {
        return seenDates;
    }

    private static List<Date> seenDates = new ArrayList<>();


    public DriverRideHistoryViewModel() {
        rides.setValue(createMockRides());
    }

    public LiveData<List<Ride>> getRides() {
        return rides;
    }

    public List<Ride> sortByDate(boolean ascending, List<Ride> ridesToSort) {
        if (ridesToSort == null) return null;

        List<Ride> sorted = new ArrayList<>(ridesToSort);

        sorted.sort((r1, r2) -> {
            if (ascending) {
                return r1.getDate().compareTo(r2.getDate());
            } else {
                return r2.getDate().compareTo(r1.getDate());
            }
        });

        return sorted;
    }

    public void activateSort(boolean ascending) {
        if (rides.getValue() != null) {
            rides.setValue(sortByDate(ascending, rides.getValue()));
        }

    }

    public void filter(Date start, Date end, boolean asc) {
        List<Ride> filtered = new ArrayList<>();
        List<Ride> current = createMockRides();

        if (start != null && end != null) {
            for (Ride r : current) {
                if (r.getDate().before(start00(start)) || r.getDate().after(end23(end))) {
                    continue;
                }
                filtered.add(r);
                if (!seenDates.contains(r.getDate())) {
                    seenDates.add(r.getDate());
                }
            }
        }
        else {
            filtered = current;
        }

        filtered = sortByDate(asc, filtered);
        rides.setValue(filtered);
    }

    private List<Ride> createMockRides() {
        List<Ride> list = new ArrayList<>();

        list.add(new Ride(
                date(2024, 5, 7).getTime(),
                "08:15",
                "08:45",
                "Bulevar Oslobođenja 45",
                "Železnička stanica Novi Sad",
                "Completed",
                850,
                Arrays.asList(
                        new Passenger("Marko", "Petrović", true),
                        new Passenger("Ivan", "Jovanović", false)
                ),
                false
        ));

        list.add(new Ride(
                date(2025, 3, 1).getTime(),
                "10:30",
                "11:05",
                "Limanski park",
                "Futoška pijaca",
                "Completed",
                720,
                Arrays.asList(
                        new Passenger("Ana", "Nikolić", true)
                ),
                false
        ));

        list.add(new Ride(
                date(2025, 3, 2).getTime(),
                "09:00",
                "09:40",
                "Detelinara",
                "FTN",
                "Completed",
                900,
                Arrays.asList(
                        new Passenger("Stefan", "Ilić", true),
                        new Passenger("Maja", "Kovačević", false),
                        new Passenger("Luka", "Marić", false)
                ),
                false
        ));

        list.add(new Ride(
                date(2025, 3, 3).getTime(),
                "14:10",
                "",
                "Spens",
                "Centar",
                "Cancelled by you",
                0,
                Arrays.asList(
                        new Passenger("Jelena", "Popović", true)
                ),
                false
        ));

        list.add(new Ride(
                date(2025, 3, 3).getTime(),
                "16:20",
                "16:55",
                "Novo Naselje",
                "Promenada",
                "Completed",
                780,
                Arrays.asList(
                        new Passenger("Nemanja", "Stojanović", true),
                        new Passenger("Filip", "Đorđević", false)
                ),
                false
        ));

        list.add(new Ride(
                date(2025, 3, 4).getTime(),
                "07:40",
                "08:10",
                "Veternik",
                "Centar",
                "Completed",
                950,
                Arrays.asList(
                        new Passenger("Milica", "Ristić", true)
                ),
                false
        ));

        list.add(new Ride(
                date(2025, 3, 4).getTime(),
                "12:00",
                "12:35",
                "Telep",
                "Klinički centar",
                "Completed",
                820,
                Arrays.asList(
                        new Passenger("Ognjen", "Savić", true),
                        new Passenger("Petar", "Milošević", false)
                ),
                true
        ));

        list.add(new Ride(
                date(2025, 3, 5).getTime(),
                "18:30",
                "19:05",
                "Centar",
                "Petrovaradin",
                "Completed",
                880,
                Arrays.asList(
                        new Passenger("Sara", "Lazić", true)
                ),
                false
        ));

        list.add(new Ride(
                date(2025, 3, 6).getTime(),
                "20:10",
                "",
                "Promenada",
                "Limanski park",
                "Cancelled by Vuk",
                0,
                Arrays.asList(
                        new Passenger("Vuk", "Obradović", true)
                ),
                false
        ));

        list.add(new Ride(
                date(2025, 3, 6).getTime(),
                "22:15",
                "22:50",
                "Petrovaradin",
                "Novo Naselje",
                "Completed",
                920,
                Arrays.asList(
                        new Passenger("Teodora", "Pavlović", true),
                        new Passenger("Andrej", "Mitrović", false)
                ),
                false
        ));


        return sortByDate(false, list);
    }

    private Date start00(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    private Date end23(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 999);
        return c.getTime();
    }

    private Calendar date(int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.set(year, month - 1, day);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c;
    }

    public void clearFilters(boolean asc) {
        filter(null, null, asc);
    }
}
