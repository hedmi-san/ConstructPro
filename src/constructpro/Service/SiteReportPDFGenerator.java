package constructpro.Service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import constructpro.DTO.ConstructionSite;
import java.io.FileOutputStream;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.awt.Color;

public class SiteReportPDFGenerator {

    private static final Color HEADER_COLOR = new Color(50, 50, 50);
    private static final Color TABLE_HEADER_COLOR = new Color(70, 130, 180);
    private static final Color ALTERNATE_ROW_COLOR = new Color(245, 245, 245);
    private static final Color TOTAL_ROW_COLOR = new Color(230, 230, 230);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static void generateReport(
            ConstructionSite site,
            LocalDate start,
            LocalDate end,
            String outputPath,
            ResultSet workersRS,
            ResultSet billsRS,
            ResultSet maintenanceRS,
            ResultSet rentalsRS) throws Exception {

        Document document = new Document(PageSize.A4, 36, 36, 54, 54);
        PdfWriter.getInstance(document, new FileOutputStream(outputPath));
        document.open();

        // 1. Header
        addReportHeader(document, site, start, end);

        // 2. Workers Table
        addSectionTitle(document, "1. Paiements des Travailleurs");
        addWorkersTable(document, workersRS);

        // 3. Bills Table
        addSectionTitle(document, "2. Factures (Fournisseurs)");
        addBillsTable(document, billsRS);

        // 4. Maintenance Table
        addSectionTitle(document, "3. Maintenance des Véhicules");
        addMaintenanceTable(document, maintenanceRS);

        // 5. Rentals Table
        addSectionTitle(document, "4. Location des Véhicules");
        addRentalsTable(document, rentalsRS);

        document.close();
    }

    private static void addReportHeader(Document document, ConstructionSite site, LocalDate start, LocalDate end)
            throws DocumentException {
        Font titleFont = new Font(Font.HELVETICA, 20, Font.BOLD, HEADER_COLOR);
        Paragraph title = new Paragraph("RAPPORT DÉTAILLÉ DU CHANTIER", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        document.add(new Paragraph("\n"));

        Font infoFont = new Font(Font.HELVETICA, 11, Font.NORMAL);
        Font boldFont = new Font(Font.HELVETICA, 11, Font.BOLD);

        Paragraph p1 = new Paragraph();
        p1.add(new Chunk("Chantier: ", boldFont));
        p1.add(new Chunk(site.getName(), infoFont));
        document.add(p1);

        if (site.getLocation() != null && !site.getLocation().isEmpty()) {
            Paragraph p2 = new Paragraph();
            p2.add(new Chunk("Localisation: ", boldFont));
            p2.add(new Chunk(site.getLocation(), infoFont));
            document.add(p2);
        }

        Paragraph p3 = new Paragraph();
        p3.add(new Chunk("Période: ", boldFont));
        p3.add(new Chunk(start.format(DATE_FORMATTER) + " au " + end.format(DATE_FORMATTER), infoFont));
        document.add(p3);

        document.add(new Paragraph("\n" + new String(new char[80]).replace("\0", "_") + "\n\n"));
    }

    private static void addSectionTitle(Document document, String title) throws DocumentException {
        Font sectionFont = new Font(Font.HELVETICA, 14, Font.BOLD, TABLE_HEADER_COLOR);
        Paragraph p = new Paragraph(title, sectionFont);
        p.setSpacingBefore(15);
        p.setSpacingAfter(10);
        document.add(p);
    }

    private static void addWorkersTable(Document document, ResultSet rs) throws Exception {
        PdfPTable table = createBaseTable(3, new float[] { 5f, 2f, 3f });
        addTableHeader(table, new String[] { "Nom du Travailleur", "Nombre de Chèques", "Total Payé" });

        double grandTotal = 0;
        int rows = 0;
        Font cellFont = new Font(Font.HELVETICA, 9);

        while (rs.next()) {
            Color bg = (rows % 2 == 0) ? Color.WHITE : ALTERNATE_ROW_COLOR;
            String name = rs.getString("workerName");
            int count = rs.getInt("checkCount");
            double total = rs.getDouble("totalPaid");
            grandTotal += total;

            addTableCell(table, name, cellFont, bg, Element.ALIGN_LEFT);
            addTableCell(table, String.valueOf(count), cellFont, bg, Element.ALIGN_CENTER);
            addTableCell(table, String.format("%.2f DA", total), cellFont, bg, Element.ALIGN_RIGHT);
            rows++;
        }

        if (rows == 0) {
            addNoDataMessage(table, 3);
        } else {
            addTotalRow(table, "TOTAL TRAVAILLEURS", String.format("%.2f DA", grandTotal), 2);
        }
        document.add(table);
    }

    private static void addBillsTable(Document document, ResultSet rs) throws Exception {
        PdfPTable table = createBaseTable(3, new float[] { 3f, 4f, 3f });
        addTableHeader(table, new String[] { "Date", "Fournisseur", "Montant Total" });

        double grandTotal = 0;
        int rows = 0;
        Font cellFont = new Font(Font.HELVETICA, 9);

        while (rs.next()) {
            Color bg = (rows % 2 == 0) ? Color.WHITE : ALTERNATE_ROW_COLOR;
            LocalDate date = rs.getDate("billDate").toLocalDate();
            String supplier = rs.getString("supplierName");
            double amount = rs.getDouble("totalCost");
            grandTotal += amount;

            addTableCell(table, date.format(DATE_FORMATTER), cellFont, bg, Element.ALIGN_CENTER);
            addTableCell(table, supplier, cellFont, bg, Element.ALIGN_LEFT);
            addTableCell(table, String.format("%.2f DA", amount), cellFont, bg, Element.ALIGN_RIGHT);
            rows++;
        }

        if (rows == 0) {
            addNoDataMessage(table, 3);
        } else {
            addTotalRow(table, "TOTAL FACTURES", String.format("%.2f DA", grandTotal), 2);
        }
        document.add(table);
    }

    private static void addMaintenanceTable(Document document, ResultSet rs) throws Exception {
        PdfPTable table = createBaseTable(3, new float[] { 3f, 4f, 3f });
        addTableHeader(table, new String[] { "Date", "Véhicule", "Coût" });

        double grandTotal = 0;
        int rows = 0;
        Font cellFont = new Font(Font.HELVETICA, 9);

        while (rs.next()) {
            Color bg = (rows % 2 == 0) ? Color.WHITE : ALTERNATE_ROW_COLOR;
            LocalDate date = rs.getDate("repaireDate").toLocalDate();
            String vehicle = rs.getString("vehicleName");
            double cost = rs.getDouble("cost");
            grandTotal += cost;

            addTableCell(table, date.format(DATE_FORMATTER), cellFont, bg, Element.ALIGN_CENTER);
            addTableCell(table, vehicle, cellFont, bg, Element.ALIGN_LEFT);
            addTableCell(table, String.format("%.2f DA", cost), cellFont, bg, Element.ALIGN_RIGHT);
            rows++;
        }

        if (rows == 0) {
            addNoDataMessage(table, 3);
        } else {
            addTotalRow(table, "TOTAL MAINTENANCE", String.format("%.2f DA", grandTotal), 2);
        }
        document.add(table);
    }

    private static void addRentalsTable(Document document, ResultSet rs) throws Exception {
        PdfPTable table = createBaseTable(6, new float[] { 2f, 2f, 3f, 3f, 2f, 2f });
        addTableHeader(table, new String[] { "Début", "Fin", "Véhicule", "Propriétaire", "Taux/J", "Payé" });

        double grandTotal = 0;
        int rows = 0;
        Font cellFont = new Font(Font.HELVETICA, 8);

        while (rs.next()) {
            Color bg = (rows % 2 == 0) ? Color.WHITE : ALTERNATE_ROW_COLOR;
            LocalDate start = rs.getDate("startDate").toLocalDate();
            java.sql.Date sqlEnd = rs.getDate("endDate");
            String endStr = (sqlEnd != null) ? sqlEnd.toLocalDate().format(DATE_FORMATTER) : "-";
            String vehicle = rs.getString("vehicleName");
            String owner = rs.getString("ownerCompany");
            double rate = rs.getDouble("dailyRate");
            double paid = rs.getDouble("paidAmount");
            grandTotal += paid;

            addTableCell(table, start.format(DATE_FORMATTER), cellFont, bg, Element.ALIGN_CENTER);
            addTableCell(table, endStr, cellFont, bg, Element.ALIGN_CENTER);
            addTableCell(table, vehicle, cellFont, bg, Element.ALIGN_LEFT);
            addTableCell(table, owner, cellFont, bg, Element.ALIGN_LEFT);
            addTableCell(table, String.format("%.0f", rate), cellFont, bg, Element.ALIGN_RIGHT);
            addTableCell(table, String.format("%.2f", paid), cellFont, bg, Element.ALIGN_RIGHT);
            rows++;
        }

        if (rows == 0) {
            addNoDataMessage(table, 6);
        } else {
            addTotalRow(table, "TOTAL LOCATIONS", String.format("%.2f DA", grandTotal), 5);
        }
        document.add(table);
    }

    private static PdfPTable createBaseTable(int cols, float[] widths) throws DocumentException {
        PdfPTable table = new PdfPTable(cols);
        table.setWidthPercentage(100);
        table.setWidths(widths);
        table.setSpacingBefore(5);
        table.setSpacingAfter(15);
        return table;
    }

    private static void addTableHeader(PdfPTable table, String[] headers) {
        Font headerFont = new Font(Font.HELVETICA, 10, Font.BOLD, Color.WHITE);
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
            cell.setBackgroundColor(TABLE_HEADER_COLOR);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(6);
            table.addCell(cell);
        }
    }

    private static void addTableCell(PdfPTable table, String text, Font font, Color bg, int align) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(bg);
        cell.setHorizontalAlignment(align);
        cell.setPadding(5);
        table.addCell(cell);
    }

    private static void addTotalRow(PdfPTable table, String label, String value, int labelColspan) {
        Font boldFont = new Font(Font.HELVETICA, 10, Font.BOLD);
        PdfPCell labelCell = new PdfPCell(new Phrase(label, boldFont));
        labelCell.setColspan(labelColspan);
        labelCell.setBackgroundColor(TOTAL_ROW_COLOR);
        labelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        labelCell.setPadding(6);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, boldFont));
        valueCell.setBackgroundColor(TOTAL_ROW_COLOR);
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        valueCell.setPadding(6);
        table.addCell(valueCell);
    }

    private static void addNoDataMessage(PdfPTable table, int colspan) {
        PdfPCell cell = new PdfPCell(
                new Phrase("Aucune donnée enregistrée pour cette période.", new Font(Font.HELVETICA, 9, Font.ITALIC)));
        cell.setColspan(colspan);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(10);
        table.addCell(cell);
    }
}
