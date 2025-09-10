package cruise.gui;

import cruise.model.Cruise;
import javax.swing.*;
import java.io.File;

public class MainFrame extends JFrame {
    public MainFrame() {
        setTitle("Cruise Room Reservation");
        setSize(500, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        Cruise cruise;
        try {
            cruise = Cruise.loadFromFile("cruise_bookings.dat");
        } catch (Exception e) {
            cruise = new Cruise();
        }

        setContentPane(new BookingPanel(cruise, this));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
