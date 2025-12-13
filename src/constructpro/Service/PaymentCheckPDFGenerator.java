package constructpro.Service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import constructpro.DAO.PaymentCheckDAO;
import constructpro.DTO.ConstructionSite;
import java.io.FileOutputStream;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.awt.Color;

/**
 * PDF Generator for Payment Checks using OpenPDF 1.3.3
 * Generates a professional PDF document listing all payment checks for workers
 * at a specific construction site on a specific date.
 */
public class PaymentCheckPDFGenerator {

    private static final Color HEADER_COLOR = new Color(70, 130, 180);
    private static final Color ALTERNATE_ROW_COLOR = new Color(240, 240, 240);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static void generatePDF(
            Connection conn,
            ConstructionSite site,
            LocalDate paymentDate,
            String outputPath,
            PaymentCheckDAO checkDAO) throws Exception {

        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(outputPath));

        document.open();

        // Add header
        addHeader(document, site, paymentDate);

        // Add spacing
        document.add(new Paragraph("\n"));

        // Add payment checks table
        addPaymentChecksTable(document, checkDAO, site.getId(), paymentDate);

        document.close();
    }

    private static void addHeader(Document document, ConstructionSite site, LocalDate paymentDate)
            throws DocumentException {

        // Company/Title
        Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD, HEADER_COLOR);
        Paragraph title = new Paragraph("FICHE DE PAIEMENT", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        document.add(new Paragraph("\n"));

        // Site and Date information
        Font infoFont = new Font(Font.HELVETICA, 12, Font.NORMAL);
        Font infoBoldFont = new Font(Font.HELVETICA, 12, Font.BOLD);

        Paragraph siteInfo = new Paragraph();
        siteInfo.add(new Chunk("Chantier: ", infoBoldFont));
        siteInfo.add(new Chunk(site.getName(), infoFont));
        document.add(siteInfo);

        Paragraph dateInfo = new Paragraph();
        dateInfo.add(new Chunk("Date de paiement: ", infoBoldFont));
        dateInfo.add(new Chunk(paymentDate.format(DATE_FORMATTER), infoFont));
        document.add(dateInfo);

        if (site.getLocation() != null && !site.getLocation().isEmpty()) {
            Paragraph locationInfo = new Paragraph();
            locationInfo.add(new Chunk("Localisation: ", infoBoldFont));
            locationInfo.add(new Chunk(site.getLocation(), infoFont));
            document.add(locationInfo);
        }
    }

    private static void addPaymentChecksTable(
            Document document,
            PaymentCheckDAO checkDAO,
            int siteId,
            LocalDate paymentDate) throws Exception {

        // Create table with 3 columns
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.setWidths(new float[] { 4f, 3f, 3f });

        // Header font
        Font headerFont = new Font(Font.HELVETICA, 10, Font.BOLD, Color.WHITE);

        // Add table headers
        addTableHeader(table, "Nom et Prénom", headerFont);
        addTableHeader(table, "Fonction", headerFont);
        addTableHeader(table, "Montant Payé", headerFont);

        // Fetch data
        ResultSet rs = checkDAO.getPaymentChecksBySiteAndDate(siteId, paymentDate);

        Font cellFont = new Font(Font.HELVETICA, 9, Font.NORMAL);
        Font cellBoldFont = new Font(Font.HELVETICA, 9, Font.BOLD);

        double totalPaid = 0;
        int rowCount = 0;

        while (rs.next()) {
            String firstName = rs.getString("firstName");
            String lastName = rs.getString("lastName");
            String job = rs.getString("job");
            double paidAmount = rs.getDouble("paidAmount");

            totalPaid += paidAmount;

            // Alternate row colors
            Color bgColor = (rowCount % 2 == 0) ? Color.WHITE : ALTERNATE_ROW_COLOR;

            addTableCell(table, lastName + " " + firstName, cellFont, bgColor);
            addTableCell(table, job != null ? job : "N/A", cellFont, bgColor);
            addTableCell(table, String.format("%.2f DA", paidAmount), cellFont, bgColor);

            rowCount++;
        }

        rs.close();

        if (rowCount == 0) {
            // No data found
            PdfPCell noDataCell = new PdfPCell(new Phrase("Aucun chèque de paiement trouvé pour cette date", cellFont));
            noDataCell.setColspan(3);
            noDataCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            noDataCell.setPadding(10);
            table.addCell(noDataCell);
        } else {
            // Add totals row
            addTotalCell(table, "TOTAL", cellBoldFont);
            addTotalCell(table, "", cellBoldFont);
            addTotalCell(table, String.format("%.2f DA", totalPaid), cellBoldFont);
        }

        document.add(table);

        // Add summary information
        if (rowCount > 0) {
            document.add(new Paragraph("\n"));
            Font summaryFont = new Font(Font.HELVETICA, 10, Font.NORMAL);
            Paragraph summary = new Paragraph(
                    String.format("Nombre total de travailleurs: %d", rowCount),
                    summaryFont);
            document.add(summary);
        }
    }

    private static void addTableHeader(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(HEADER_COLOR);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(8);
        table.addCell(cell);
    }

    private static void addTableCell(PdfPTable table, String text, Font font, Color bgColor) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(bgColor);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(5);
        table.addCell(cell);
    }

    private static void addTotalCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(new Color(220, 220, 220));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(8);
        table.addCell(cell);
    }
}
