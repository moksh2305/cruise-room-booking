package cruise.model;

import java.io.Serializable;
import java.time.LocalDate;

public class Booking implements Serializable {
    private Room room;
    private User user;
    private LocalDate checkIn, checkOut;
    private int adults, children;
    private double totalCost;

    public Booking(Room room, User user, LocalDate checkIn, LocalDate checkOut, int adults, int children, double totalCost) {
        this.room = room;
        this.user = user;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.adults = adults;
        this.children = children;
        this.totalCost = totalCost;
    }

    public boolean conflictsWith(LocalDate newCheckIn, LocalDate newCheckOut) {
        return newCheckIn.isBefore(checkOut) && newCheckOut.isAfter(checkIn);
    }

    public Room getRoom() { return room; }
    public User getUser() { return user; }
    public LocalDate getCheckIn() { return checkIn; }
    public LocalDate getCheckOut() { return checkOut; }
    public int getAdults() { return adults; }
    public int getChildren() { return children; }
    public double getTotalCost() { return totalCost; }
}
