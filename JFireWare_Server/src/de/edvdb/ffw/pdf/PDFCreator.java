package de.edvdb.ffw.pdf;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

import de.edvdb.ffw.beans.Alarmfax;
import de.edvdb.ffw.beans.Field;
import de.edvdb.ffw.system.LayoutConfig;
import de.edvdb.ffw.system.ServerConfig;
import de.edvdb.ffw.util.Messages;
import de.edvdb.ffw.util.Utils;

public class PDFCreator {
	private static Logger log = Logger.getLogger(PDFCreator.class);
	private static Font headline1Font = new Font(Font.FontFamily.HELVETICA, 18,
			Font.BOLD);
	private static Font headline2Font = new Font(Font.FontFamily.HELVETICA, 14,
			Font.BOLD);
	private static Font warning = new Font(Font.FontFamily.HELVETICA, 9,
			Font.BOLD, BaseColor.RED);
	private static Font normalFont = new Font(Font.FontFamily.HELVETICA, 12,
			Font.NORMAL);
	private static Font italicFont = new Font(Font.FontFamily.HELVETICA, 12,
			Font.ITALIC);
	private static Font lightFont = new Font(Font.FontFamily.HELVETICA, 12,
			Font.ITALIC, BaseColor.GRAY);
	private static BaseColor bgColor = BaseColor.LIGHT_GRAY;

	private Alarmfax fax;
	private File bmpImage;

	public PDFCreator(Alarmfax fax, File bmpImage) {
		this.fax = fax;
		this.bmpImage = bmpImage;
	}

	public File createPDF() {
		try {
			File pdfFile = new File(
					ServerConfig.OUTPUTDIR
							+ Messages.getString("PDFCreator.Filename") + Utils.getCurrentDatetime() + ".pdf"); //$NON-NLS-1$ //$NON-NLS-2$
			log.debug("Preparing PDFFile.."); //$NON-NLS-1$
			Document document = new Document();
			PdfWriter writer = PdfWriter.getInstance(document,
					new FileOutputStream(pdfFile));
			writer.setPageEvent(new EventHandler());
			document.open();
			document.setMargins(10, 10, 10, 10);
			addMetaData(document);
			addContent(document);
			document.close();
			log.debug("Finished generating PDF.."); //$NON-NLS-1$
			return pdfFile;
		} catch (Exception e) {
			log.error("Failed to write PDF", e); //$NON-NLS-1$
			return null;
		}
	}

	// iText allows to add metadata to the PDF which can be viewed in your Adobe
	// Reader
	// under File -> Properties
	private void addMetaData(Document document) {
		document.addHeader("created by", "JFireWare / www.jfireware.de");
		document.addHeader("inspired by", "FF Gelting");
		document.addHeader("implemented by", "Michael van den Berg");
		document.addTitle(Messages.getString("Title")); //$NON-NLS-1$
		document.addAuthor(Messages.getString("Author")); //$NON-NLS-1$
		document.addCreator(Messages.getString("Author")); //$NON-NLS-1$
		document.addSubject(Messages.getString("Title") + "["
				+ fax.getAdresse().toString() + "]");
	}

	private void addContent(Document document) throws DocumentException,
			MalformedURLException, IOException {
		Paragraph preface = new Paragraph();
		preface.setAlignment(Paragraph.ALIGN_CENTER);
		Paragraph p = new Paragraph(Messages.getString("Title"), headline1Font);
		p.setAlignment(Paragraph.ALIGN_CENTER);
		preface.add(p); //$NON-NLS-1$
		p = new Paragraph("Zeitpunkt der Erstellung: "
				+ Utils.readableFormat.format(fax.getTimestamp()), italicFont);
		p.setAlignment(Paragraph.ALIGN_CENTER);
		preface.add(p);
		addEmptyLine(preface, 1);
		
		Paragraph warningBlock = new Paragraph(
				Messages.getString("BetaDisclaimer"), warning); //$NON-NLS-1$
		warningBlock.setAlignment(Paragraph.ALIGN_JUSTIFIED_ALL);
		preface.add(warningBlock);
		addEmptyLine(preface, 1);
		
		checkAndInsertElement(preface, Messages.getString("Adresse"), fax
				.getAdresse().toString());
		checkAndInsertList(preface, LayoutConfig.SCHLAGWORT,
				Messages.getString("Schlagwort"), fax.getSchlagwort());
		checkAndInsertList(preface, LayoutConfig.BEMERKUNG,
				Messages.getString("Bemerkung"), fax.getBemerkung());
		checkAndInsertList(preface, LayoutConfig.ZUSTAENDIGKEIT,
				Messages.getString("Zustaendigkeit"), fax.getZustaendigkeit());
		checkAndInsertList(preface, LayoutConfig.FAHRZEUG,
				Messages.getString("Fahrzeuge"), fax.getFahrzeuge());
		checkAndInsertList(preface, LayoutConfig.EINSATZMITTEL,
				Messages.getString("Einsatzmittel"), fax.getEinsatzmittel());
		document.add(preface);

		checkAndInsertImage(document, fax.getOverviewMap().getAbsolutePath(),
				false, true);
		checkAndInsertImage(document, fax.getDetailMap().getAbsolutePath(),
				false, true);
		checkAndInsertImage(document, bmpImage.getAbsolutePath(), true, true);
	}

	private void addEmptyLine(Paragraph paragraph, int number) {
		for (int i = 0; i < number; i++) {
			paragraph.add(new Paragraph(Messages.getString("BLANK"))); //$NON-NLS-1$
		}
	}

	private void checkAndInsertElement(Paragraph preface, String headline,
			String element) {
		java.util.List<String> elements = new ArrayList<String>();
		elements.add(element);
		checkAndInsertList(preface, headline, elements);
	}

	private void checkAndInsertList(Paragraph preface, Field field,
			String headline, Collection<String> elements) {
		if (field != null) {
			checkAndInsertList(preface, headline, elements);
		}
	}

	private void checkAndInsertList(Paragraph preface, String headline,
			Collection<String> elements) {
		PdfPTable table = new PdfPTable(1);
		table.setTotalWidth(90);

		PdfPCell header = new PdfPCell(new Phrase(headline, headline2Font));
		header.setHorizontalAlignment(Element.ALIGN_LEFT);
		header.setBackgroundColor(bgColor);
		table.addCell(header);
		table.setHeaderRows(1);

		for (String element : elements) {
			table.addCell(new Phrase(element, normalFont));
		}

		table.setSpacingAfter(10);
		preface.add(table);
	}

	private void checkAndInsertImage(Document document, String pathToFile,
			boolean newPage, boolean scaleToFit) {

		try {
			Image image = Image.getInstance(pathToFile);
			image.setBorder(Rectangle.BOX);
			image.setBorderWidth(2);
			image.setBorderColor(BaseColor.BLACK);
			image.setAlignment(Image.MIDDLE);
			if (scaleToFit) {
				image.scaleToFit(document.getPageSize().getWidth(), document
						.getPageSize().getHeight());
			}
			if (newPage) {
				document.newPage();
			}
			document.add(image);
		} catch (Exception e) {
			log.error("Could not insert image into pdf");
		}
	}

	class EventHandler extends PdfPageEventHelper {
		@Override
		public void onEndPage(PdfWriter writer, Document document) {
			Rectangle page = document.getPageSize();
			ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase(Messages.getString("PDFCreator.Footer"), lightFont), page.getWidth() / 2, 10, 0);
		}
	}
}
