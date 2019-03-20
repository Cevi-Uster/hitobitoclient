package ch.cevi.db.client.business.export;

import java.io.FileOutputStream;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import ch.cevi.db.client.business.entities.YGroup;
import ch.cevi.db.client.communication.ServerFacade;

public class Excel97Exporter extends GeneralExporter {

	public Excel97Exporter(ExportDescription exportDescription, ServerFacade serverFacade, YGroup yGroup) {
		super(exportDescription, serverFacade, yGroup);
	}

	@Override
	protected void saveToFile(List<List<String>> data) throws Exception {
		if (data != null) {
			HSSFWorkbook workbook = new HSSFWorkbook();
			FileOutputStream out = null;
			try {
				out = new FileOutputStream(targetFile);
				HSSFSheet sheet = workbook.createSheet("export");
				int rowNumber = 0;
				for (List<String> row : data) {
					HSSFRow sheetRow = sheet.createRow(rowNumber);
					int columnNumber = 0;
					for (String cellContent : row){
						HSSFCell cell = sheetRow.createCell(columnNumber);
						cell.setCellValue(cellContent);
						columnNumber ++;
					}
					rowNumber ++;
				}
				workbook.write(out);
			} finally {
				if (out != null) {
					out.close();
				} if (workbook != null){
					workbook.close();
				}
			}
		}
	}

}
