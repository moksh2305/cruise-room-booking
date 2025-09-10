package cruise.gui;

import cruise.model.*;
import javax.swing.*;
import java.awt.*;

public class OtpPanel extends JPanel {
    private Cruise cruise;
    private JFrame parentFrame;
    private JTextField otpField;
    private JButton verifyBtn;

    public OtpPanel(Cruise cruise, JFrame parentFrame) {
        this.cruise = cruise;
        this.parentFrame = parentFrame;
        setLayout(new GridLayout(3, 1, 10, 10));
        add(new JLabel("Enter OTP sent to your email:"));
        otpField = new JTextField();
        add(otpField);
        verifyBtn = new JButton("Verify OTP");
        add(verifyBtn);

        verifyBtn.addActionListener(e -> {
            String entered = otpField.getText().trim();
            String expected = (String) parentFrame.getRootPane().getClientProperty("pendingOtp");
            if (entered.equals(expected)) {
                Booking booking = (Booking) parentFrame.getRootPane().getClientProperty("pendingBooking");
                booking.getRoom().addBooking(booking);
                // Save booking
                try {
                    cruise.saveToFile("cruise_bookings.dat");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Warning: Could not save booking.");
                }
                parentFrame.setContentPane(new ConfirmationPanel(cruise, parentFrame, booking));
                parentFrame.validate();
            } else {
                JOptionPane.showMessageDialog(this, "Incorrect OTP. Try again.");
            }
        });
    }
}
