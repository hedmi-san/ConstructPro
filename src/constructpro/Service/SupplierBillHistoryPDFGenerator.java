package constructpro.Service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.lowagie.text.pdf.draw.LineSeparator;
import constructpro.DTO.Supplier;
import constructpro.DAO.BiLLItemDAO;
import constructpro.DTO.BiLLItem;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.util.List;
import java.awt.Color;

/**
 * PDF Generator for Supplier Bill History
 */
public class SupplierBillHistoryPDFGenerator {

    private static final Color HEADER_COLOR = new Color(70, 130, 180);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("#,##0.00");

    public static void generatePDF(
            Connection conn,
            Supplier supplier,
            ResultSet billsRs,
            String outputPath) throws Exception {

        Document document = new Document(PageSize.A4, 30, 30, 30, 30);
        PdfWriter.getInstance(document, new FileOutputStream(outputPath));

        document.open();

        // Add header
        addHeader(document, supplier);

        // Add spacing
        document.add(new Paragraph("\n"));

        // Add bills table
        addBillsTable(document, conn, billsRs);

        document.close();
    }

    private static void addHeader(Document document, Supplier supplier) throws DocumentException {
        Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD, HEADER_COLOR);
        Paragraph title = new Paragraph("HISTORIQUE DÉTAILLÉ DES FACTURES", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        document.add(new Paragraph("\n"));

        Font infoFont = new Font(Font.HELVETICA, 11, Font.NORMAL);
        Font boldFont = new Font(Font.HELVETICA, 11, Font.BOLD);

        Paragraph supplierInfo = new Paragraph();
        supplierInfo.add(new Chunk("Fournisseur: ", boldFont));
        supplierInfo.add(new Chunk(supplier.getName(), infoFont));
        document.add(supplierInfo);

        Paragraph dateInfo = new Paragraph();
        dateInfo.add(new Chunk("Date d'édition: ", boldFont));
        dateInfo.add(new Chunk(LocalDate.now().format(DATE_FORMATTER), infoFont));
        document.add(dateInfo);

        LineSeparator line = new LineSeparator();
        line.setLineColor(HEADER_COLOR);
        document.add(new Chunk(line));
    }

    private static void addBillsTable(Document document, Connection conn, ResultSet rs) throws Exception {
        BiLLItemDAO itemDAO = new BiLLItemDAO(conn);
        double totalSpent = 0;
        double totalPaid = 0;

        Font billHeaderFont = new Font(Font.HELVETICA, 10, Font.BOLD, Color.WHITE);
        Font itemHeaderFont = new Font(Font.HELVETICA, 9, Font.BOLD);
        Font cellFont = new Font(Font.HELVETICA, 9, Font.NORMAL);
        Font subtotalFont = new Font(Font.HELVETICA, 10, Font.BOLD);

        while (rs.next()) {
            int billId = rs.getInt("id");
            String factureNum = rs.getString("factureNumber");
            String siteName = rs.getString("site_name");
            LocalDate billDate = rs.getDate("billDate").toLocalDate();
            double cost = rs.getDouble("totalCost");
            double paid = rs.getDouble("paidAmount");

            totalSpent += cost;
            totalPaid += paid;

            // Bill Header Table
            PdfPTable billTable = new PdfPTable(3);
            billTable.setWidthPercentage(100);
            billTable.setSpacingBefore(10f);

            addHeaderCell(billTable, "Facture: " + factureNum, billHeaderFont);
            addHeaderCell(billTable, "Date: " + billDate.format(DATE_FORMATTER), billHeaderFont);
            addHeaderCell(billTable, "Chantier: " + siteName, billHeaderFont);

            document.add(billTable);

            // Items Table
            List<BiLLItem> items = itemDAO.getBillItems(billId);
            if (!items.isEmpty()) {
                PdfPTable itemsTable = new PdfPTable(4);
                itemsTable.setWidthPercentage(95);
                itemsTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
                itemsTable.setWidths(new float[] { 4f, 1f, 2f, 2f });

                addTableHeader(itemsTable, "Désignation", itemHeaderFont);
                addTableHeader(itemsTable, "Qté", itemHeaderFont);
                addTableHeader(itemsTable, "P.U", itemHeaderFont);
                addTableHeader(itemsTable, "Total", itemHeaderFont);

                for (BiLLItem item : items) {
                    addTableCell(itemsTable, item.getItemName(), cellFont, Color.WHITE);
                    addTableCell(itemsTable, String.valueOf(item.getQuantity()), cellFont, Color.WHITE);
                    addTableCell(itemsTable, CURRENCY_FORMAT.format(item.getUnitPrice()) + " DA", cellFont,
                            Color.WHITE);
                    addTableCell(itemsTable, CURRENCY_FORMAT.format(item.getQuantity() * item.getUnitPrice()) + " DA",
                            cellFont, Color.WHITE);
                }
                document.add(itemsTable);
            }

            // Bill Footer (Totals for this bill)
            PdfPTable footerTable = new PdfPTable(2);
            footerTable.setWidthPercentage(100);
            footerTable.setWidths(new float[] { 1f, 1f });

            PdfPCell costCell = new PdfPCell(
                    new Phrase("Coût Total Facture: " + CURRENCY_FORMAT.format(cost) + " DA", subtotalFont));
            costCell.setBorder(Rectangle.NO_BORDER);
            costCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            footerTable.addCell(costCell);

            PdfPCell paidCell = new PdfPCell(
                    new Phrase("Montant Payé: " + CURRENCY_FORMAT.format(paid) + " DA", subtotalFont));
            paidCell.setBorder(Rectangle.NO_BORDER);
            paidCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            footerTable.addCell(paidCell);

            document.add(footerTable);

            // Add a separator line
            LineSeparator sep = new LineSeparator();
            sep.setLineColor(Color.LIGHT_GRAY);
            sep.setLineWidth(0.5f);
            document.add(new Chunk(sep));
        }

        document.add(new Paragraph("\n"));

        Font totalFont = new Font(Font.HELVETICA, 12, Font.BOLD);
        Paragraph totalsInfo = new Paragraph();
        totalsInfo.add(new Chunk("RÉSUMÉ GÉNÉRAL\n", totalFont));
        totalsInfo.add(new Chunk("Total Dépensé: ", totalFont));
        totalsInfo.add(new Chunk(CURRENCY_FORMAT.format(totalSpent) + " DA\n", totalFont));
        totalsInfo.add(new Chunk("Total Payé: ", totalFont));
        totalsInfo.add(new Chunk(CURRENCY_FORMAT.format(totalPaid) + " DA\n", totalFont));

        Font debtFont = new Font(Font.HELVETICA, 12, Font.BOLD, new Color(200, 0, 0));
        totalsInfo.add(new Chunk("Dette Totale (Reste): ", debtFont));
        totalsInfo.add(new Chunk(CURRENCY_FORMAT.format(totalSpent - totalPaid) + " DA", debtFont));

        totalsInfo.setAlignment(Element.ALIGN_RIGHT);
        document.add(totalsInfo);
    }

    private static void addHeaderCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(HEADER_COLOR);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setPadding(5);
        table.addCell(cell);
    }

    private static void addTableHeader(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(Color.LIGHT_GRAY);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(3);
        table.addCell(cell);
    }

    private static void addTableCell(PdfPTable table, String text, Font font, Color bgColor) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(bgColor);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(3);
        table.addCell(cell);
    }
}
