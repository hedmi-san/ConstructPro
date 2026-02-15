package constructpro.Service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.lowagie.text.pdf.draw.LineSeparator;
import constructpro.DTO.PaymentCheck;
import constructpro.DTO.Worker;
import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;
import java.awt.Color;

/**
 * PDF Generator for Payment Receipts using OpenPDF 1.3.3
 * Generates a professional receipt for individual worker payments
 */
public class PaymentReceiptPDFGenerator {

    private static final Color HEADER_COLOR = new Color(70, 130, 180);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static void generatePDF(
            Worker worker,
            PaymentCheck paymentCheck,
            String nin,
            String destinorName,
            String outputPath) throws Exception {

        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        PdfWriter.getInstance(document, new FileOutputStream(outputPath));

        document.open();

        // Add header
        addHeader(document);

        // Add spacing
        document.add(new Paragraph("\n"));

        // Add worker information
        addWorkerInfo(document, worker);

        // Add spacing
        document.add(new Paragraph("\n"));

        // Add payment details
        addPaymentDetails(document, paymentCheck);

        // Add spacing
        document.add(new Paragraph("\n"));

        // Add recipient information
        addRecipientInfo(document, nin, destinorName);

        // Add spacing
        document.add(new Paragraph("\n\n"));

        // Add signature section
        addSignatureSection(document);

        document.close();
    }

    private static void addHeader(Document document) throws DocumentException {
        Font titleFont = new Font(Font.HELVETICA, 20, Font.BOLD, HEADER_COLOR);
        Paragraph title = new Paragraph("REÇU DE PAIEMENT", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        // Add horizontal line
        LineSeparator line = new LineSeparator();
        line.setLineColor(HEADER_COLOR);
        document.add(new Chunk(line));
    }

    private static void addWorkerInfo(Document document, Worker worker) throws DocumentException {
        Font sectionFont = new Font(Font.HELVETICA, 14, Font.BOLD);
        Font labelFont = new Font(Font.HELVETICA, 11, Font.BOLD);
        Font valueFont = new Font(Font.HELVETICA, 11, Font.NORMAL);

        Paragraph sectionTitle = new Paragraph("Informations du Travailleur", sectionFont);
        document.add(sectionTitle);
        document.add(new Paragraph("\n"));

        // Worker details
        Paragraph workerName = new Paragraph();
        workerName.add(new Chunk("Nom et Prénom: ", labelFont));
        workerName.add(new Chunk(worker.getLastName() + " " + worker.getFirstName(), valueFont));
        document.add(workerName);

        Paragraph workerJob = new Paragraph();
        workerJob.add(new Chunk("Fonction: ", labelFont));
        workerJob.add(new Chunk(worker.getRole() != null ? worker.getRole() : "N/A", valueFont));
        document.add(workerJob);
    }

    private static void addPaymentDetails(Document document, PaymentCheck paymentCheck) throws DocumentException {
        Font sectionFont = new Font(Font.HELVETICA, 14, Font.BOLD);
        Font labelFont = new Font(Font.HELVETICA, 11, Font.BOLD);
        Font valueFont = new Font(Font.HELVETICA, 11, Font.NORMAL);
        Font amountFont = new Font(Font.HELVETICA, 12, Font.BOLD, new Color(0, 100, 0));

        Paragraph sectionTitle = new Paragraph("Détails du Paiement", sectionFont);
        document.add(sectionTitle);
        document.add(new Paragraph("\n"));

        // Payment date
        Paragraph paymentDate = new Paragraph();
        paymentDate.add(new Chunk("Date de Paiement: ", labelFont));
        paymentDate.add(new Chunk(paymentCheck.getPaymentDay().format(DATE_FORMATTER), valueFont));
        document.add(paymentDate);

        // Paid amount
        Paragraph paidAmount = new Paragraph();
        paidAmount.add(new Chunk("Montant Payé: ", labelFont));
        paidAmount.add(new Chunk(String.format("%.2f DA", paymentCheck.getPaidAmount()), amountFont));
        document.add(paidAmount);
    }

    private static void addRecipientInfo(Document document, String nin, String destinorName) throws DocumentException {
        Font sectionFont = new Font(Font.HELVETICA, 14, Font.BOLD);
        Font labelFont = new Font(Font.HELVETICA, 11, Font.BOLD);
        Font valueFont = new Font(Font.HELVETICA, 11, Font.NORMAL);

        Paragraph sectionTitle = new Paragraph("Informations du Destinataire", sectionFont);
        document.add(sectionTitle);
        document.add(new Paragraph("\n"));

        // NIN
        Paragraph ninPara = new Paragraph();
        ninPara.add(new Chunk("Numéro d'Identification Nationale: ", labelFont));
        ninPara.add(new Chunk(nin, valueFont));
        document.add(ninPara);

        // Destinor name
        Paragraph destinorPara = new Paragraph();
        destinorPara.add(new Chunk("Nom du Destinataire: ", labelFont));
        destinorPara.add(new Chunk(destinorName, valueFont));
        document.add(destinorPara);
    }

    private static void addSignatureSection(Document document) throws DocumentException {
        Font labelFont = new Font(Font.HELVETICA, 11, Font.BOLD);
        Font valueFont = new Font(Font.HELVETICA, 10, Font.NORMAL);

        // Create a table for signatures
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[] { 1f, 1f });

        // Left cell - Date and signature of recipient
        PdfPCell leftCell = new PdfPCell();
        leftCell.setBorder(Rectangle.NO_BORDER);
        leftCell.setPadding(10);

        Paragraph leftContent = new Paragraph();
        leftContent.add(new Chunk("Date: _______________\n\n", valueFont));
        leftContent.add(new Chunk("Signature du Destinataire:\n\n\n\n", labelFont));
        leftContent.add(new Chunk("_____________________", valueFont));
        leftCell.addElement(leftContent);
        table.addCell(leftCell);

        // Right cell - Signature of payer
        PdfPCell rightCell = new PdfPCell();
        rightCell.setBorder(Rectangle.NO_BORDER);
        rightCell.setPadding(10);

        Paragraph rightContent = new Paragraph();
        rightContent.add(new Chunk("\n\n", valueFont));
        rightContent.add(new Chunk("Signature du Payeur:\n\n\n\n", labelFont));
        rightContent.add(new Chunk("_____________________", valueFont));
        rightCell.addElement(rightContent);
        table.addCell(rightCell);

        document.add(table);
    }
}
