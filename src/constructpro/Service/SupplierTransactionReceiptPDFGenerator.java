package constructpro.Service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.lowagie.text.pdf.draw.LineSeparator;
import constructpro.DTO.FinancialTransaction;
import constructpro.DTO.Supplier;
import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;
import java.awt.Color;

/**
 * PDF Generator for Supplier Transaction Receipts
 */
public class SupplierTransactionReceiptPDFGenerator {

    private static final Color HEADER_COLOR = new Color(70, 130, 180);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static void generatePDF(
            Supplier supplier,
            FinancialTransaction transaction,
            String outputPath) throws Exception {

        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        PdfWriter.getInstance(document, new FileOutputStream(outputPath));

        document.open();

        // Add header
        addHeader(document);

        // Add spacing
        document.add(new Paragraph("\n"));

        // Add supplier information
        addSupplierInfo(document, supplier);

        // Add spacing
        document.add(new Paragraph("\n"));

        // Add transaction details
        addTransactionDetails(document, transaction);

        // Add spacing
        document.add(new Paragraph("\n\n"));

        // Add signature section
        addSignatureSection(document);

        document.close();
    }

    private static void addHeader(Document document) throws DocumentException {
        Font titleFont = new Font(Font.HELVETICA, 20, Font.BOLD, HEADER_COLOR);
        Paragraph title = new Paragraph("REÇU DE PAIEMENT (FOURNISSEUR)", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        // Add horizontal line
        LineSeparator line = new LineSeparator();
        line.setLineColor(HEADER_COLOR);
        document.add(new Chunk(line));
    }

    private static void addSupplierInfo(Document document, Supplier supplier) throws DocumentException {
        Font sectionFont = new Font(Font.HELVETICA, 14, Font.BOLD);
        Font labelFont = new Font(Font.HELVETICA, 11, Font.BOLD);
        Font valueFont = new Font(Font.HELVETICA, 11, Font.NORMAL);

        Paragraph sectionTitle = new Paragraph("Informations du Fournisseur", sectionFont);
        document.add(sectionTitle);
        document.add(new Paragraph("\n"));

        // Supplier details
        Paragraph supplierName = new Paragraph();
        supplierName.add(new Chunk("Nom du Fournisseur: ", labelFont));
        supplierName.add(new Chunk(supplier.getName(), valueFont));
        document.add(supplierName);

        if (supplier.getType() != null) {
            Paragraph supplierType = new Paragraph();
            supplierType.add(new Chunk("Type: ", labelFont));
            supplierType.add(new Chunk(supplier.getType(), valueFont));
            document.add(supplierType);
        }
    }

    private static void addTransactionDetails(Document document, FinancialTransaction transaction)
            throws DocumentException {
        Font sectionFont = new Font(Font.HELVETICA, 14, Font.BOLD);
        Font labelFont = new Font(Font.HELVETICA, 11, Font.BOLD);
        Font valueFont = new Font(Font.HELVETICA, 11, Font.NORMAL);
        Font amountFont = new Font(Font.HELVETICA, 12, Font.BOLD, new Color(0, 100, 0));

        Paragraph sectionTitle = new Paragraph("Détails de la Transaction", sectionFont);
        document.add(sectionTitle);
        document.add(new Paragraph("\n"));

        // Transaction date
        Paragraph transDate = new Paragraph();
        transDate.add(new Chunk("Date de Paiement: ", labelFont));
        transDate.add(new Chunk(transaction.getPaymentDate().format(DATE_FORMATTER), valueFont));
        document.add(transDate);

        // Method
        Paragraph method = new Paragraph();
        method.add(new Chunk("Méthode de Paiement: ", labelFont));
        method.add(new Chunk(transaction.getMethod(), valueFont));
        document.add(method);

        // Paid amount
        Paragraph paidAmount = new Paragraph();
        paidAmount.add(new Chunk("Montant Payé: ", labelFont));
        paidAmount.add(new Chunk(String.format("%.2f DA", transaction.getAmount()), amountFont));
        document.add(paidAmount);
    }

    private static void addSignatureSection(Document document) throws DocumentException {
        Font labelFont = new Font(Font.HELVETICA, 11, Font.BOLD);
        Font valueFont = new Font(Font.HELVETICA, 10, Font.NORMAL);

        // Create a table for signatures
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[] { 1f, 1f });

        // Left cell - Date and signature of recipient (Fournisseur)
        PdfPCell leftCell = new PdfPCell();
        leftCell.setBorder(Rectangle.NO_BORDER);
        leftCell.setPadding(10);

        Paragraph leftContent = new Paragraph();
        leftContent.add(new Chunk("Date: _______________\n\n", valueFont));
        leftContent.add(new Chunk("Signature du Fournisseur:\n\n\n\n", labelFont));
        leftContent.add(new Chunk("_____________________", valueFont));
        leftCell.addElement(leftContent);
        table.addCell(leftCell);

        // Right cell - Signature of payer (ConstructPro)
        PdfPCell rightCell = new PdfPCell();
        rightCell.setBorder(Rectangle.NO_BORDER);
        rightCell.setPadding(10);

        Paragraph rightContent = new Paragraph();
        rightContent.add(new Chunk("\n\n", valueFont));
        rightContent.add(new Chunk("Signature du Responsable:\n\n\n\n", labelFont));
        rightContent.add(new Chunk("_____________________", valueFont));
        rightCell.addElement(rightContent);
        table.addCell(rightCell);

        document.add(table);
    }
}
