package constructpro.Service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import constructpro.DAO.WorkerDAO;
import constructpro.DTO.ConstructionSite;
import java.io.FileOutputStream;
import java.sql.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.awt.Color;

/**
 * PDF Generator for Attendance Tracking using OpenPDF 1.3.3
 * Generates attendance sheets for workers at construction sites
 * Split by month periods (1-14 and 15-end)
 */
public class AttendancePDFGenerator {

    private static final Color HEADER_COLOR = new Color(70, 130, 180);
    private static final Color ALTERNATE_ROW_COLOR = new Color(240, 240, 240);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("MMMM yyyy");

    public static void generatePDF(
            Connection conn,
            ConstructionSite site,
            boolean isFirstHalf,
            String outputPath,
            WorkerDAO workerDAO) throws Exception {

        // Use landscape orientation for better date column visibility
        Document document = new Document(PageSize.A4.rotate(), 30, 30, 30, 30);
        PdfWriter.getInstance(document, new FileOutputStream(outputPath));

        document.open();

        // Determine date range
        LocalDate today = LocalDate.now();
        YearMonth currentMonth = YearMonth.from(today);

        int startDay = isFirstHalf ? 1 : 15;
        int endDay = isFirstHalf ? 14 : currentMonth.lengthOfMonth();

        LocalDate startDate = LocalDate.of(today.getYear(), today.getMonth(), startDay);
        LocalDate endDate = LocalDate.of(today.getYear(), today.getMonth(), endDay);

        // Add header
        addHeader(document, site, startDate, endDate, isFirstHalf);

        // Add spacing
        document.add(new Paragraph("\n"));

        // Add attendance table
        addAttendanceTable(document, workerDAO, site.getId(), startDate, endDate);

        document.close();
    }

    private static void addHeader(Document document, ConstructionSite site,
            LocalDate startDate, LocalDate endDate, boolean isFirstHalf)
            throws DocumentException {

        // Title
        Font titleFont = new Font(Font.HELVETICA, 16, Font.BOLD, HEADER_COLOR);
        Paragraph title = new Paragraph("FICHE DE POINTAGE", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        document.add(new Paragraph("\n"));

        // Site and period information
        Font infoFont = new Font(Font.HELVETICA, 11, Font.NORMAL);
        Font infoBoldFont = new Font(Font.HELVETICA, 11, Font.BOLD);

        // Create a table for header info (2 columns)
        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        headerTable.setWidths(new float[] { 1f, 1f });

        // Left column
        PdfPCell leftCell = new PdfPCell();
        leftCell.setBorder(Rectangle.NO_BORDER);
        Paragraph leftInfo = new Paragraph();
        leftInfo.add(new Chunk("Chantier: ", infoBoldFont));
        leftInfo.add(new Chunk(site.getName(), infoFont));
        leftInfo.add(Chunk.NEWLINE);
        if (site.getLocation() != null && !site.getLocation().isEmpty()) {
            leftInfo.add(new Chunk("Localisation: ", infoBoldFont));
            leftInfo.add(new Chunk(site.getLocation(), infoFont));
        }
        leftCell.addElement(leftInfo);
        headerTable.addCell(leftCell);

        // Right column
        PdfPCell rightCell = new PdfPCell();
        rightCell.setBorder(Rectangle.NO_BORDER);
        Paragraph rightInfo = new Paragraph();
        rightInfo.add(new Chunk("Mois: ", infoBoldFont));
        rightInfo.add(new Chunk(startDate.format(MONTH_FORMATTER), infoFont));
        rightInfo.add(Chunk.NEWLINE);
        rightInfo.add(new Chunk("Période: ", infoBoldFont));
        String period = isFirstHalf ? "1ère partie (1-14)" : "2ème partie (15-fin)";
        rightInfo.add(new Chunk(period, infoFont));
        rightCell.addElement(rightInfo);
        headerTable.addCell(rightCell);

        document.add(headerTable);
    }

    private static void addAttendanceTable(
            Document document,
            WorkerDAO workerDAO,
            int siteId,
            LocalDate startDate,
            LocalDate endDate) throws Exception {

        // Calculate number of days
        int numDays = (int) (endDate.toEpochDay() - startDate.toEpochDay()) + 1;

        // Create table: 1 column for name + numDays columns for dates
        int numColumns = 1 + numDays;
        PdfPTable table = new PdfPTable(numColumns);
        table.setWidthPercentage(100);

        // Set column widths (name column wider, date columns equal)
        float[] widths = new float[numColumns];
        widths[0] = 3f; // Name column
        for (int i = 1; i < numColumns; i++) {
            widths[i] = 1f; // Date columns
        }
        table.setWidths(widths);

        // Header fonts
        Font headerFont = new Font(Font.HELVETICA, 8, Font.BOLD, Color.WHITE);
        Font dateHeaderFont = new Font(Font.HELVETICA, 7, Font.BOLD, Color.WHITE);

        // Add "Nom et Prénom" header
        addTableHeader(table, "Nom et Prénom", headerFont, 30f);

        // Add date headers
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            String dateStr = String.format("%02d", currentDate.getDayOfMonth());
            addTableHeader(table, dateStr, dateHeaderFont, 30f);
            currentDate = currentDate.plusDays(1);
        }

        // Fetch workers for this site
        ResultSet rs = workerDAO.getWorkersBySiteId(siteId);

        Font cellFont = new Font(Font.HELVETICA, 8, Font.NORMAL);
        int rowCount = 0;

        while (rs.next()) {
            String firstName = rs.getString("first_name");
            String lastName = rs.getString("last_name");

            // Alternate row colors
            Color bgColor = (rowCount % 2 == 0) ? Color.WHITE : ALTERNATE_ROW_COLOR;

            // Add worker name
            addTableCell(table, lastName + " " + firstName, cellFont, bgColor, 40f);

            // Add empty cells for each day
            for (int i = 0; i < numDays; i++) {
                addTableCell(table, "", cellFont, bgColor, 40f);
            }

            rowCount++;
        }

        rs.close();

        if (rowCount == 0) {
            // No workers found
            PdfPCell noDataCell = new PdfPCell(new Phrase("Aucun travailleur trouvé pour ce chantier", cellFont));
            noDataCell.setColspan(numColumns);
            noDataCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            noDataCell.setPadding(10);
            table.addCell(noDataCell);
        }

        document.add(table);

        // Add footer note
        if (rowCount > 0) {
            document.add(new Paragraph("\n"));
            Font noteFont = new Font(Font.HELVETICA, 8, Font.ITALIC);
            Paragraph note = new Paragraph(
                    "Note: Marquer la présence de chaque travailleur quotidiennement",
                    noteFont);
            document.add(note);
        }
    }

    private static void addTableHeader(PdfPTable table, String text, Font font, float minHeight) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(HEADER_COLOR);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(5);
        cell.setMinimumHeight(minHeight);
        table.addCell(cell);
    }

    private static void addTableCell(PdfPTable table, String text, Font font, Color bgColor, float minHeight) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(bgColor);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(5);
        cell.setMinimumHeight(minHeight);
        table.addCell(cell);
    }
}
