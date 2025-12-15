package constructpro.Service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import constructpro.DTO.ConstructionSite;
import java.awt.Color;
import java.io.FileOutputStream;
import java.util.List;
import java.util.stream.Collectors;

public class AttachmentPDFGenerator {

    private static final Color SECTION_HEADER_COLOR = new Color(230, 230, 230);

    public static void generatePDF(String titleStr, ConstructionSite site, List<String> roles, String outputPath)
            throws Exception {

        Document document = new Document(PageSize.A4.rotate(), 20, 20, 20, 20);
        PdfWriter.getInstance(document, new FileOutputStream(outputPath));

        document.open();

        // 1. Header
        addHeader(document, titleStr, site);

        // 2. Determine Roles (Columns)
        // Check if roles need sorting? Maybe default sort is fine.
        List<String> sortedRoles = roles.stream().distinct().sorted().collect(Collectors.toList());

        // 3. Build Table
        addAttachmentTable(document, sortedRoles);

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

    private static void addAttachmentTable(Document document, List<String> roles) throws DocumentException {
        // Columns: Date (1) + Roles * 3 (Nom, Acompt, Obs)
        int numColumns = 1 + (roles.size() * 3);
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

        // Role Headers (Colspan 3)
        for (String role : roles) {
            PdfPCell roleCell = new PdfPCell(new Phrase(role, new Font(Font.HELVETICA, 10, Font.BOLD)));
            roleCell.setColspan(3);
            roleCell.setBackgroundColor(SECTION_HEADER_COLOR);
            roleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            roleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            roleCell.setPadding(5);
            table.addCell(roleCell);
        }

        // --- ROW 2: Sub-headers ---
        Font subHeaderFont = new Font(Font.HELVETICA, 8, Font.BOLD);
        for (String role : roles) { // Repeat for each role
            addCell(table, "NOM", subHeaderFont, SECTION_HEADER_COLOR);
            addCell(table, "ACOMPT", subHeaderFont, SECTION_HEADER_COLOR);
            addCell(table, "OBS", subHeaderFont, SECTION_HEADER_COLOR);
        }

        // --- ROWS: Empty Rows for Manual Entry ---
        int numRows = 15; // Generate 25 empty rows
        Font cellFont = new Font(Font.HELVETICA, 8, Font.NORMAL);

        for (int i = 0; i < numRows; i++) {
            // Date Column
            PdfPCell dateCell = new PdfPCell(new Phrase(" ", cellFont));
            dateCell.setBackgroundColor(new Color(245, 245, 220));
            dateCell.setMinimumHeight(20f);
            table.addCell(dateCell);

            // Role Columns
            for (int j = 0; j < roles.size() * 3; j++) {
                PdfPCell cell = new PdfPCell(new Phrase(" ", cellFont));
                cell.setMinimumHeight(20f);
                table.addCell(cell);
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
