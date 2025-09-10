package cruise.model;

import java.io.*;
import java.util.*;
import java.time.LocalDate;

public class Cruise implements Serializable {
    private List<Room> rooms = new ArrayList<>();

    public Cruise() {
        for (int floor = 1; floor <= 12; floor++) {
            for (int num = 1; num <= 15; num++) {
                rooms.add(new Room(floor, num));
            }
        }
    }

    public List<Room> getAvailableRooms(LocalDate checkIn, LocalDate checkOut) {
        List<Room> available = new ArrayList<>();
        for (Room r : rooms) {
            if (r.isAvailable(checkIn, checkOut)) available.add(r);
        }
        return available;
    }

    public void saveToFile(String filename) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(this);
        }
    }

    public static Cruise loadFromFile(String filename) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            return (Cruise) ois.readObject();
        }
    }

    public List<Room> getRooms() { return rooms; }
}
