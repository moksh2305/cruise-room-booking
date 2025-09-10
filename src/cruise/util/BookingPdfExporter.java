package cruise.util;

import cruise.model.*;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.FileOutputStream;
import java.util.List;

public class BookingPdfExporter {

    public static void exportToPdf(List<Room> rooms, String filePath) throws Exception {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();

        // Add title
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Paragraph title = new Paragraph("Cruise Bookings", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(Chunk.NEWLINE);

        // Table headers
        PdfPTable table = new PdfPTable(7); // 7 columns
        table.setWidthPercentage(100);
        table.setWidths(new int[]{2, 2, 3, 4, 3, 3, 3});

        addTableHeader(table, "Room Number");
        addTableHeader(table, "Floor");
        addTableHeader(table, "User Name");
        addTableHeader(table, "Email");
        addTableHeader(table, "Phone");
        addTableHeader(table, "Check-In");
        addTableHeader(table, "Check-Out");

        // Data rows
        for (Room room : rooms) {
            for (Booking booking : room.getBookings()) {
                table.addCell(String.valueOf(room.getNumber()));
                table.addCell(String.valueOf(room.getFloor()));
                table.addCell(booking.getUser().getName());
                table.addCell(booking.getUser().getEmail());
                table.addCell(booking.getUser().getMobile());
                table.addCell(booking.getCheckIn().toString());
                table.addCell(booking.getCheckOut().toString());
            }
        }

        document.add(table);
        document.close();
    }

    private static void addTableHeader(PdfPTable table, String headerTitle) {
        PdfPCell header = new PdfPCell();
        header.setBackgroundColor(BaseColor.LIGHT_GRAY);
        header.setBorderWidth(1);
        header.setPhrase(new Phrase(headerTitle));
        table.addCell(header);
    }
}
