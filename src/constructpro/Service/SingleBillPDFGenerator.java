package constructpro.Service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.lowagie.text.pdf.draw.LineSeparator;
import constructpro.DTO.Bill;
import constructpro.DTO.BiLLItem;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.awt.Color;

/**
 * PDF Generator for a Single Bill
 */
public class SingleBillPDFGenerator {

    private static final Color HEADER_COLOR = new Color(70, 130, 180);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("#,##0.00");

    public static void generatePDF(
            Bill bill,
            List<BiLLItem> items,
            String supplierName,
            String siteName,
            String outputPath) throws Exception {

        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        PdfWriter.getInstance(document, new FileOutputStream(outputPath));

        document.open();

        // Add header
        addHeader(document, bill, supplierName, siteName);

        // Add spacing
        document.add(new Paragraph("\n"));

        // Add items table
        addItemsTable(document, items);

        // Add totals
        addTotals(document, bill);

        document.close();
    }

    private static void addHeader(Document document, Bill bill, String supplierName, String siteName)
            throws DocumentException {
        Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD, HEADER_COLOR);
        Paragraph title = new Paragraph("FACTURE DÉTAILLÉE", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        document.add(new Paragraph("\n"));

        Font infoFont = new Font(Font.HELVETICA, 11, Font.NORMAL);
        Font boldFont = new Font(Font.HELVETICA, 11, Font.BOLD);

        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        headerTable.setWidths(new float[] { 1f, 1f });

        addHeaderInfo(headerTable, "N° Facture:", bill.getFactureNumber(), boldFont, infoFont);
        addHeaderInfo(headerTable, "Date:", bill.getBillDate().format(DATE_FORMATTER), boldFont, infoFont);
        addHeaderInfo(headerTable, "Fournisseur:", supplierName, boldFont, infoFont);
        addHeaderInfo(headerTable, "Chantier:", siteName, boldFont, infoFont);

        document.add(headerTable);

        LineSeparator line = new LineSeparator();
        line.setLineColor(HEADER_COLOR);
        document.add(new Chunk(line));
    }

    private static void addHeaderInfo(PdfPTable table, String label, String value, Font bold, Font normal) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        Paragraph p = new Paragraph();
        p.add(new Chunk(label + " ", bold));
        p.add(new Chunk(value, normal));
        cell.addElement(p);
        table.addCell(cell);
    }

    private static void addItemsTable(Document document, List<BiLLItem> items) throws DocumentException {
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[] { 4f, 1f, 2f, 2f });

        Font headerFont = new Font(Font.HELVETICA, 10, Font.BOLD, Color.WHITE);
        String[] headers = { "Désignation", "Qté", "P.U (DA)", "Total (DA)" };

        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setBackgroundColor(HEADER_COLOR);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(5);
            table.addCell(cell);
        }

        Font cellFont = new Font(Font.HELVETICA, 10, Font.NORMAL);
        for (BiLLItem item : items) {
            addTableCell(table, item.getItemName(), cellFont);
            addTableCell(table, String.valueOf(item.getQuantity()), cellFont);
            addTableCell(table, CURRENCY_FORMAT.format(item.getUnitPrice()), cellFont);
            addTableCell(table, CURRENCY_FORMAT.format(item.getQuantity() * item.getUnitPrice()), cellFont);
        }

        document.add(table);
    }

    private static void addTableCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(5);
        table.addCell(cell);
    }

    private static void addTotals(Document document, Bill bill) throws DocumentException {
        document.add(new Paragraph("\n"));

        Font totalFont = new Font(Font.HELVETICA, 11, Font.BOLD);
        Font grandTotalFont = new Font(Font.HELVETICA, 12, Font.BOLD, HEADER_COLOR);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(40);
        table.setHorizontalAlignment(Element.ALIGN_RIGHT);

        double itemsTotal = bill.getCost() - bill.getTransferFee();

        addTotalRow(table, "Total Articles:", CURRENCY_FORMAT.format(itemsTotal) + " DA", totalFont);
        addTotalRow(table, "Frais de transfert:", CURRENCY_FORMAT.format(bill.getTransferFee()) + " DA", totalFont);
        addTotalRow(table, "TOTAL GÉNÉRAL:", CURRENCY_FORMAT.format(bill.getCost()) + " DA", grandTotalFont);

        document.add(table);
    }

    private static void addTotalRow(PdfPTable table, String label, String value, Font font) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, font));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, font));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(valueCell);
    }
}
