package constructpro.Service;

import constructpro.DTO.ConstructionSite;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.awt.Color;
import java.io.FileOutputStream;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TabSpecificPDFGenerator {

    private static final Font TITLE_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.BLACK);
    private static final Font SUBTITLE_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.DARK_GRAY);
    private static final Font HEADER_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.WHITE);
    private static final Font DATA_FONT = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);
    private static final Color HEADER_BG_COLOR = new Color(45, 45, 45); // Dark Gray

    public static void generateWorkerReport(ConstructionSite site, LocalDate start, LocalDate end, String outputPath,
            ResultSet rs) throws Exception {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, new FileOutputStream(outputPath));
        document.open();

        addHeader(document, "Rapport des Travailleurs", site, start, end);

        PdfPTable table = new PdfPTable(3); // Worker Name, Date, Amount
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setWidths(new float[] { 3f, 1.5f, 1.5f });

        addTableHeader(table, new String[] { "Nom du Travailleur", "Date de Paiement", "Montant Payé" });

        String currentWorker = "";
        double workerTotal = 0;
        double grandTotal = 0;

        while (rs.next()) {
            String workerName = rs.getString("workerName");
            java.sql.Date pDate = rs.getDate("paymentDate");
            double amount = rs.getDouble("paidAmount");

            if (!workerName.equals(currentWorker)) {
                if (!currentWorker.isEmpty()) {
                    addWorkerSubtotal(table, currentWorker, workerTotal);
                }
                currentWorker = workerName;
                workerTotal = 0;
            }

            table.addCell(createCell(workerName, Element.ALIGN_LEFT));
            table.addCell(createCell(pDate.toString(), Element.ALIGN_CENTER));
            table.addCell(createCell(String.format("%.2f", amount), Element.ALIGN_RIGHT));

            workerTotal += amount;
            grandTotal += amount;
        }

        if (!currentWorker.isEmpty()) {
            addWorkerSubtotal(table, currentWorker, workerTotal);
        }

        addGrandTotal(table, "TOTAL PAYÉ", grandTotal);

        document.add(table);
        document.close();
    }

    public static void generateBillReport(ConstructionSite site, String reportType, LocalDate start, LocalDate end,
            String outputPath, ResultSet rs) throws Exception {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, new FileOutputStream(outputPath));
        document.open();

        addHeader(document, "Rapport " + reportType, site, start, end);

        PdfPTable table = new PdfPTable(4); // Item, Qty, Unit Price, Total
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setWidths(new float[] { 3f, 1f, 1.5f, 1.5f });

        addTableHeader(table, new String[] { "Désignation", "Qté", "Prix Unitaire", "Total" });

        String currentBill = "";
        double billTotal = 0;
        double grandTotal = 0;
        boolean hasItems = false;

        while (rs.next()) {
            String billRef = rs.getString("factureNumber") + " (" + rs.getDate("billDate") + ")";
            String itemName = rs.getString("itemName");
            double qty = rs.getDouble("quantity");
            double price = rs.getDouble("unitPrice");
            double itemTotal = rs.getDouble("itemTotal");

            // Check if this row represents a bill without items (outer join) or new bill
            if (!billRef.equals(currentBill)) {
                if (!currentBill.isEmpty() && hasItems) {
                    // Start new bill section (visual handling only, as we list raw items)
                }
                currentBill = billRef;
                // Add a merger row for Bill Reference
                PdfPCell billCell = new PdfPCell(new Phrase("Facture: " + billRef, SUBTITLE_FONT));
                billCell.setColspan(4);
                billCell.setBackgroundColor(new Color(230, 230, 230));
                billCell.setPadding(5f);
                table.addCell(billCell);
            }

            if (itemName != null) {
                table.addCell(createCell(itemName, Element.ALIGN_LEFT));
                table.addCell(createCell(String.format("%.0f", qty), Element.ALIGN_CENTER));
                table.addCell(createCell(String.format("%.2f", price), Element.ALIGN_RIGHT));
                table.addCell(createCell(String.format("%.2f", itemTotal), Element.ALIGN_RIGHT));

                billTotal = rs.getDouble("totalCost"); // Use bill total from DB for accuracy if possible
                // But for list of items, we sum itemTotal.
                // Let's rely on summing item items for the 'Grand Total' of the report
                grandTotal += itemTotal;
                hasItems = true;
            } else {
                // Bill exists but no items?
                table.addCell(createCell("Aucun détail", Element.ALIGN_LEFT));
                table.addCell(createCell("-", Element.ALIGN_CENTER));
                table.addCell(createCell("-", Element.ALIGN_CENTER));
                table.addCell(createCell(String.format("%.2f", rs.getDouble("totalCost")), Element.ALIGN_RIGHT));
                grandTotal += rs.getDouble("totalCost");
            }
        }

        addGrandTotal(table, "COÛT TOTAL", grandTotal);

        document.add(table);
        document.close();
    }

    public static void generateVehicleReport(ConstructionSite site, LocalDate start, LocalDate end, String outputPath,
            ResultSet maintenanceRS, ResultSet rentalRS) throws Exception {
        Document document = new Document(PageSize.A4.rotate()); // Landscape for vehicle
        PdfWriter.getInstance(document, new FileOutputStream(outputPath));
        document.open();

        addHeader(document, "Rapport Véhicules (Maintenance & Location)", site, start, end);

        // Maintenance Section
        Paragraph title1 = new Paragraph("Maintenance", SUBTITLE_FONT);
        title1.setSpacingBefore(20f);
        document.add(title1);

        PdfPTable maintTable = new PdfPTable(4);
        maintTable.setWidthPercentage(100);
        maintTable.setSpacingBefore(10f);
        maintTable.setWidths(new float[] { 2f, 2f, 3f, 1.5f });
        addTableHeader(maintTable, new String[] { "Date", "Véhicule", "Type", "Coût" });

        double maintTotal = 0;
        while (maintenanceRS.next()) {
            maintTable.addCell(createCell(maintenanceRS.getDate("repaireDate").toString(), Element.ALIGN_CENTER));
            maintTable.addCell(createCell(maintenanceRS.getString("vehicleName"), Element.ALIGN_LEFT));
            maintTable.addCell(createCell(
                    maintenanceRS.getString("maintenanceType") != null ? maintenanceRS.getString("maintenanceType")
                            : "-",
                    Element.ALIGN_LEFT));
            double cost = maintenanceRS.getDouble("cost");
            maintTable.addCell(createCell(String.format("%.2f", cost), Element.ALIGN_RIGHT));
            maintTotal += cost;
        }
        addGrandTotal(maintTable, "Total Maintenance", maintTotal);
        document.add(maintTable);

        // Rental Section
        Paragraph title2 = new Paragraph("Location", SUBTITLE_FONT);
        title2.setSpacingBefore(20f);
        document.add(title2);

        PdfPTable rentTable = new PdfPTable(6);
        rentTable.setWidthPercentage(100);
        rentTable.setSpacingBefore(10f);
        rentTable.setWidths(new float[] { 1.5f, 1.5f, 2f, 2f, 1f, 1.5f });
        addTableHeader(rentTable, new String[] { "Début", "Fin", "Véhicule", "Propriétaire", "Jours", "Total" });

        double rentTotal = 0;
        while (rentalRS.next()) {
            rentTable.addCell(createCell(rentalRS.getDate("startDate").toString(), Element.ALIGN_CENTER));
            java.sql.Date endDate = rentalRS.getDate("endDate");
            rentTable.addCell(createCell(endDate != null ? endDate.toString() : "En cours", Element.ALIGN_CENTER));
            rentTable.addCell(createCell(rentalRS.getString("vehicleName"), Element.ALIGN_LEFT));
            rentTable.addCell(createCell(rentalRS.getString("ownerCompany"), Element.ALIGN_LEFT));
            rentTable.addCell(createCell(String.valueOf(rentalRS.getInt("daysWorked")), Element.ALIGN_CENTER));

            double paid = rentalRS.getDouble("paidAmount"); // Using depositeAmount as paid amount per current mapping
            rentTable.addCell(createCell(String.format("%.2f", paid), Element.ALIGN_RIGHT));
            rentTotal += paid;
        }
        addGrandTotal(rentTable, "Total Location", rentTotal);
        document.add(rentTable);

        // Combined Total
        Paragraph finalTotal = new Paragraph("COÛT TOTAL VÉHICULES: " + String.format("%.2f", (maintTotal + rentTotal)),
                TITLE_FONT);
        finalTotal.setAlignment(Element.ALIGN_RIGHT);
        finalTotal.setSpacingBefore(20f);
        document.add(finalTotal);

        document.close();
    }

    // Helper methods
    private static void addHeader(Document doc, String title, ConstructionSite site, LocalDate start, LocalDate end)
            throws DocumentException {
        Paragraph p1 = new Paragraph(site.getName(), SUBTITLE_FONT);
        p1.setAlignment(Element.ALIGN_CENTER);
        doc.add(p1);

        Paragraph pTitle = new Paragraph(title, TITLE_FONT);
        pTitle.setAlignment(Element.ALIGN_CENTER);
        pTitle.setSpacingAfter(10f);
        doc.add(pTitle);

        Paragraph pDate = new Paragraph("Période: " + start.format(DateTimeFormatter.ISO_LOCAL_DATE) + " au "
                + end.format(DateTimeFormatter.ISO_LOCAL_DATE), DATA_FONT);
        pDate.setAlignment(Element.ALIGN_CENTER);
        pDate.setSpacingAfter(20f);
        doc.add(pDate);
    }

    private static void addTableHeader(PdfPTable table, String[] headers) {
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, HEADER_FONT));
            cell.setBackgroundColor(HEADER_BG_COLOR);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(8f);
            table.addCell(cell);
        }
    }

    private static PdfPCell createCell(String content, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(content, DATA_FONT));
        cell.setHorizontalAlignment(alignment);
        cell.setPadding(5f);
        return cell;
    }

    private static void addWorkerSubtotal(PdfPTable table, String workerName, double total) {
        PdfPCell cell = new PdfPCell(new Phrase("Total pour " + workerName, SUBTITLE_FONT));
        cell.setColspan(2);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(cell);

        PdfPCell totalCell = new PdfPCell(new Phrase(String.format("%.2f", total), SUBTITLE_FONT));
        totalCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(totalCell);
    }

    private static void addGrandTotal(PdfPTable table, String label, double total) {
        PdfPCell cell = new PdfPCell(new Phrase(label, TITLE_FONT));
        cell.setColspan(table.getNumberOfColumns() - 1);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setPadding(10f);
        table.addCell(cell);

        PdfPCell totalCell = new PdfPCell(new Phrase(String.format("%.2f", total), TITLE_FONT));
        totalCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalCell.setPadding(10f);
        table.addCell(totalCell);
    }
}
