package cruise.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

public class Room implements Serializable {
    private int floor;
    private int number;
    private List<Booking> bookings = new ArrayList<>();

    public Room(int floor, int number) {
        this.floor = floor;
        this.number = number;
    }

    public int getFloor() { return floor; }
    public int getNumber() { return number; }
    public List<Booking> getBookings() { return bookings; }

    // Correct overlap logic for room availability
    public boolean isAvailable(LocalDate checkIn, LocalDate checkOut) {
        for (Booking b : bookings) {
            // Overlap if: checkIn < b.getCheckOut() && checkOut > b.getCheckIn()
            if (checkIn.isBefore(b.getCheckOut()) && checkOut.isAfter(b.getCheckIn())) {
                return false;
            }
        }
        return true;
    }

    public void addBooking(Booking booking) {
        bookings.add(booking);
    }

    @Override
    public String toString() {
        return "Room " + number + " (Floor " + floor + ")";
    }
}
