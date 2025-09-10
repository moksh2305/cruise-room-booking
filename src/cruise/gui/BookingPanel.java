package cruise.gui;

import cruise.model.*;
import cruise.util.*;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class BookingPanel extends JPanel {
    private Cruise cruise;
    private JFrame parentFrame;

    private JComboBox<Integer> floorBox;
    private JTextField checkInField, checkOutField, adultsField, childrenField;
    private JTextField nameField, mobileField, emailField;
    private JButton bookBtn;
    private JButton checkAvailabilityBtn;
    private JPanel roomTilesPanel;

    private int selectedRoomNumber = -1;

    public BookingPanel(Cruise cruise, JFrame parentFrame) {
        this.cruise = cruise;
        this.parentFrame = parentFrame;
        setLayout(new BorderLayout(10, 10));

        // Top panel for floor/date/people/user info
        JPanel topPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        floorBox = new JComboBox<>();
        for (int i = 1; i <= 12; i++) floorBox.addItem(i);

        checkInField = new JTextField("2025-06-01");
        checkOutField = new JTextField("2025-06-03");
        adultsField = new JTextField("2");
        childrenField = new JTextField("0");
        nameField = new JTextField();
        mobileField = new JTextField();
        emailField = new JTextField();

        checkAvailabilityBtn = new JButton("Check Availability");

        topPanel.add(new JLabel("Floor (1-12):")); topPanel.add(floorBox);
        topPanel.add(new JLabel("Check-in (YYYY-MM-DD):")); topPanel.add(checkInField);
        topPanel.add(new JLabel("Check-out (YYYY-MM-DD):")); topPanel.add(checkOutField);
        topPanel.add(new JLabel()); topPanel.add(checkAvailabilityBtn); // Button under date fields
        topPanel.add(new JLabel("Adults (pairs):")); topPanel.add(adultsField);
        topPanel.add(new JLabel("Children (<18):")); topPanel.add(childrenField);
        topPanel.add(new JLabel("Name:")); topPanel.add(nameField);
        topPanel.add(new JLabel("Mobile (+91...):")); topPanel.add(mobileField);
        topPanel.add(new JLabel("Email:")); topPanel.add(emailField);

        add(topPanel, BorderLayout.NORTH);

        // Center panel for room tiles
        roomTilesPanel = new JPanel(new GridLayout(3, 5, 10, 10));
        add(roomTilesPanel, BorderLayout.CENTER);

        // Book button
        bookBtn = new JButton("Book Selected Room");
        bookBtn.setEnabled(false);
        add(bookBtn, BorderLayout.SOUTH);

        // Only refresh tiles when floor changes or button is pressed
        floorBox.addActionListener(e -> updateRoomTiles());
        checkAvailabilityBtn.addActionListener(e -> updateRoomTiles());

        // Initial tiles
        updateRoomTiles();

        // Book button action
        bookBtn.addActionListener(e -> {
            try {
                int floor = (int) floorBox.getSelectedItem();
                int roomNum = selectedRoomNumber;
                if (roomNum == -1) {
                    JOptionPane.showMessageDialog(this, "Please select a room.");
                    return;
                }
                LocalDate in = LocalDate.parse(checkInField.getText().trim());
                LocalDate out = LocalDate.parse(checkOutField.getText().trim());
                int adults = Integer.parseInt(adultsField.getText().trim());
                int children = Integer.parseInt(childrenField.getText().trim());
                String name = nameField.getText().trim();
                String mobile = mobileField.getText().trim();
                String email = emailField.getText().trim();

                Room room = getRoom(floor, roomNum);
                if (!room.isAvailable(in, out)) {
                    JOptionPane.showMessageDialog(this, "Room is NOT available for those dates.");
                    updateRoomTiles();
                    return;
                }
                if (adults < 1) {
                    JOptionPane.showMessageDialog(this, "At least one adult required.");
                    return;
                }
                long nights = java.time.temporal.ChronoUnit.DAYS.between(in, out);
                if (nights < 1) {
                    JOptionPane.showMessageDialog(this, "Check-out must be after check-in.");
                    return;
                }
                double cost = nights * (adults * 15000 + children * 7500);

                User user = new User(name, mobile, email);
                Booking booking = new Booking(room, user, in, out, adults, children, cost);

                // Generate OTP and send via Gmail
                String otp = String.valueOf((int)(Math.random()*9000)+1000);
                parentFrame.getRootPane().putClientProperty("pendingBooking", booking);
                parentFrame.getRootPane().putClientProperty("pendingOtp", otp);

                // Send OTP email
                try {
                    EmailSender.sendBookingConfirmation(
                            email,
                            "Your Cruise Booking OTP",
                            "Dear " + name + ",\n\nYour OTP for cruise room booking is: " + otp + "\n\nPlease enter this OTP in the application to confirm your booking.",
                            null
                    );
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Failed to send OTP email: " + ex.getMessage());
                    return;
                }

                // Switch to OTP panel
                parentFrame.setContentPane(new OtpPanel(cruise, parentFrame));
                parentFrame.validate();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });
    }

    private void updateRoomTiles() {
        roomTilesPanel.removeAll();
        selectedRoomNumber = -1;
        bookBtn.setEnabled(false);

        int floor = (int) floorBox.getSelectedItem();
        LocalDate in, out;
        try {
            in = LocalDate.parse(checkInField.getText().trim());
            out = LocalDate.parse(checkOutField.getText().trim());
        } catch (Exception e) {
            in = null; out = null;
        }

        for (int roomNum = 1; roomNum <= 15; roomNum++) {
            final int thisRoomNum = roomNum; // Make it final for lambda
            Room room = getRoom(floor, thisRoomNum);
            boolean available = false;
            if (in != null && out != null) {
                available = room.isAvailable(in, out);
            }
            JButton tile = new JButton(String.valueOf(thisRoomNum));
            tile.setPreferredSize(new Dimension(60, 60));
            tile.setOpaque(true);
            tile.setBorderPainted(false);

            if (!available) {
                tile.setBackground(Color.LIGHT_GRAY);
                tile.setEnabled(false);
                tile.setToolTipText("Booked");
            } else {
                tile.setBackground(Color.GREEN);
                tile.setEnabled(true);
                tile.setToolTipText("Available");
                tile.addActionListener(e -> {
                    // Deselect all other tiles
                    for (Component comp : roomTilesPanel.getComponents()) {
                        if (comp instanceof JButton) {
                            comp.setBackground(Color.GREEN);
                        }
                    }
                    tile.setBackground(Color.BLUE);
                    selectedRoomNumber = thisRoomNum;
                    bookBtn.setEnabled(true);
                });
            }
            roomTilesPanel.add(tile);
        }
        roomTilesPanel.revalidate();
        roomTilesPanel.repaint();
    }

    private Room getRoom(int floor, int number) {
        for (Room r : cruise.getRooms()) {
            if (r.getFloor() == floor && r.getNumber() == number) return r;
        }
        throw new IllegalArgumentException("Room not found");
    }
}
