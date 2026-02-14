package constructpro.Service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import constructpro.DTO.ConstructionSite;
import constructpro.DTO.Worker;
import java.awt.Color;
import java.io.FileOutputStream;
import java.util.List;

public class AttachmentPDFGenerator {

    private static final Color SECTION_HEADER_COLOR = new Color(230, 230, 230);

    public static void generatePDF(String titleStr, ConstructionSite site, List<Worker> workers, String outputPath)
            throws Exception {

        Document document = new Document(PageSize.A4.rotate(), 20, 20, 20, 20);
        PdfWriter.getInstance(document, new FileOutputStream(outputPath));

        document.open();

        // 1. Header
        addHeader(document, titleStr, site);

        // 2. Build Table
        addAttachmentTable(document, workers);

        // 4. Worker List removed as requested for Job selection flow

        document.close();
    }

    private static void addHeader(Document document, String titleStr, ConstructionSite site) throws DocumentException {
        // Logo / Enterprise Name
        Font logoFont = new Font(Font.HELVETICA, 12, Font.BOLD);
        Paragraph logo = new Paragraph("ETP.HANACHE HOUSSAM", logoFont);
        logo.setAlignment(Element.ALIGN_RIGHT);
        document.add(logo);

        // Main Header Box
        PdfPTable headerTable = new PdfPTable(1);
        headerTable.setWidthPercentage(40);
        headerTable.setHorizontalAlignment(Element.ALIGN_CENTER);

        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.BOX);
        cell.setPadding(10);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);

        Font titleFont = new Font(Font.HELVETICA, 14, Font.BOLD);
        Font subTitleFont = new Font(Font.HELVETICA, 12, Font.NORMAL);

        cell.addElement(new Paragraph(titleStr, titleFont));
        if (site != null) {
            cell.addElement(new Paragraph(site.getName(), subTitleFont));
            if (site.getLocation() != null) {
                cell.addElement(new Paragraph(site.getLocation(), subTitleFont));
            }
        }

        headerTable.addCell(cell);
        document.add(headerTable);

        // Bloc Title
        Paragraph blocTitle = new Paragraph("\n" + titleStr + "\n", new Font(Font.HELVETICA, 16, Font.BOLD));
        blocTitle.setAlignment(Element.ALIGN_CENTER);
        document.add(blocTitle);
        document.add(new Paragraph("\n"));
    }

    private static void addAttachmentTable(Document document, List<Worker> workers) throws DocumentException {
        // Columns: Date (1) + Workers * 3 (Nom, Acompt, Obs)
        int numColumns = 1 + (workers.size() * 3);
        PdfPTable table = new PdfPTable(numColumns);
        table.setWidthPercentage(100);

        // Widths: Date=small, Others=equal
        float[] widths = new float[numColumns];
        widths[0] = 1.5f; // Date column width
        for (int i = 1; i < numColumns; i++) {
            widths[i] = 3f;
        }
        table.setWidths(widths);

        // --- ROW 1: Headers (Merged) ---

        // Date Header (Rowspan 2)
        PdfPCell dateHeader = new PdfPCell(new Phrase("DATE", new Font(Font.HELVETICA, 9, Font.BOLD)));
        dateHeader.setRowspan(2);
        dateHeader.setBackgroundColor(new Color(245, 245, 220)); // Beige-ish
        dateHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
        dateHeader.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(dateHeader);

        // Worker Headers (Colspan 3)
        for (Worker worker : workers) {
            String role = (worker.getRole() != null ? worker.getRole() : "TRAVAILLEUR");
            PdfPCell roleCell = new PdfPCell(new Phrase(role.toUpperCase(), new Font(Font.HELVETICA, 10, Font.BOLD)));
            roleCell.setColspan(3);
            roleCell.setBackgroundColor(SECTION_HEADER_COLOR);
            roleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            roleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            roleCell.setPadding(5);
            table.addCell(roleCell);
        }

        // --- ROW 2: Sub-headers ---
        Font subHeaderFont = new Font(Font.HELVETICA, 8, Font.BOLD);
        for (int i = 0; i < workers.size(); i++) {
            addCell(table, "NOM", subHeaderFont, SECTION_HEADER_COLOR);
            addCell(table, "ACOMPT", subHeaderFont, SECTION_HEADER_COLOR);
            addCell(table, "OBS", subHeaderFont, SECTION_HEADER_COLOR);
        }

        // --- ROWS: Data Rows ---
        int numRows = 15; // Generate 25 rows
        Font cellFont = new Font(Font.HELVETICA, 8, Font.NORMAL);

        for (int i = 0; i < numRows; i++) {
            // Date Column
            PdfPCell dateCell = new PdfPCell(new Phrase(" ", cellFont));
            dateCell.setBackgroundColor(new Color(245, 245, 220));
            dateCell.setMinimumHeight(20f);
            table.addCell(dateCell);

            // Worker Columns
            for (Worker worker : workers) {
                // NOM Column (Pre-filled with worker name ONLY for first row)
                String nameText = " ";
                if (i == 0) {
                    nameText = (worker.getFirstName() + " " + worker.getLastName()).toUpperCase();
                }

                PdfPCell nameCell = new PdfPCell(new Phrase(nameText, cellFont));
                nameCell.setMinimumHeight(20f);
                nameCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                nameCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(nameCell);

                // ACOMPT Column (Empty)
                PdfPCell acomptCell = new PdfPCell(new Phrase(" ", cellFont));
                acomptCell.setMinimumHeight(20f);
                table.addCell(acomptCell);

                // OBS Column (Empty)
                PdfPCell obsCell = new PdfPCell(new Phrase(" ", cellFont));
                obsCell.setMinimumHeight(20f);
                table.addCell(obsCell);
            }
        }

        document.add(table);
    }

    private static void addCell(PdfPTable table, String text, Font font, Color bg) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(bg);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);
    }

}
