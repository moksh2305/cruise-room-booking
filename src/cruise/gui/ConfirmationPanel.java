package cruise.gui;

import cruise.model.*;
import cruise.util.*;
import javax.swing.*;
import java.awt.*;
import java.io.File;

public class ConfirmationPanel extends JPanel {
    public ConfirmationPanel(Cruise cruise, JFrame parentFrame, Booking booking) {
        setLayout(new BorderLayout(10, 10));
        String details = String.format(
                "<html>Booking Confirmed!<br>Room: %d<br>Floor: %d<br>Check-in: %s<br>Check-out: %s<br>Adults: %d<br>Children: %d<br>Total: ₹%.2f<br>Email: %s</html>",
                booking.getRoom().getNumber(), booking.getRoom().getFloor(), booking.getCheckIn(), booking.getCheckOut(),
                booking.getAdults(), booking.getChildren(), booking.getTotalCost(), booking.getUser().getEmail()
        );
        add(new JLabel(details), BorderLayout.CENTER);

        // Generate QR code
        String qrData = String.format("Room:%d,Floor:%d,CheckIn:%s,CheckOut:%s,Total:%.2f",
                booking.getRoom().getNumber(), booking.getRoom().getFloor(),
                booking.getCheckIn(), booking.getCheckOut(), booking.getTotalCost());
        String qrFile = "booking_qr.png";
        try {
            QrCodeGenerator.generate(qrData, qrFile);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "QR code generation failed.");
        }

        // Send confirmation email
        try {
            EmailSender.sendBookingConfirmation(
                    booking.getUser().getEmail(),
                    "Cruise Room Booking Confirmation",
                    "Dear " + booking.getUser().getName() + ",\n\nYour booking is confirmed!\n\n" +
                            "Room: " + booking.getRoom().getNumber() + "\n" +
                            "Floor: " + booking.getRoom().getFloor() + "\n" +
                            "Check-in: " + booking.getCheckIn() + "\n" +
                            "Check-out: " + booking.getCheckOut() + "\n" +
                            "Total: ₹" + booking.getTotalCost() + "\n\n" +
                            "Enjoy your cruise!\n",
                    new File(qrFile)
            );
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Email sending failed: " + ex.getMessage());
        }

        // Panel for action buttons at the bottom
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton newBookingBtn = new JButton("New Booking");
        JButton exportPdfBtn = new JButton("Export Bookings to PDF");
        buttonPanel.add(newBookingBtn);
        buttonPanel.add(exportPdfBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        // New Booking button action
        newBookingBtn.addActionListener(e -> {
            parentFrame.setContentPane(new BookingPanel(cruise, parentFrame));
            parentFrame.validate();
        });

        // Export to PDF button action
        exportPdfBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save PDF");
            fileChooser.setSelectedFile(new java.io.File("cruise_bookings.pdf"));
            int userSelection = fileChooser.showSaveDialog(this);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                java.io.File fileToSave = fileChooser.getSelectedFile();
                try {
                    // Use the correct reference for BookingPdfExporter
                    BookingPdfExporter.exportToPdf(cruise.getRooms(), fileToSave.getAbsolutePath());
                    JOptionPane.showMessageDialog(this, "Exported to " + fileToSave.getAbsolutePath());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage());
                }
            }
        });
    }
}
