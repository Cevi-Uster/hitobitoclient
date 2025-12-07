package ch.cevi.db.client.business.export;

import java.io.FileWriter;
import java.util.List;

import com.opencsv.CSVWriter;

import ch.cevi.db.client.business.entities.YGroup;
import ch.cevi.db.client.communication.ServerFacade;

public class CSVExporter extends GeneralExporter {

	public CSVExporter(ExportDescription exportDescription, ServerFacade serverFacade, YGroup yGroup) {
		super(exportDescription, serverFacade, yGroup);
	}

	@Override
	protected void saveToFile(List<List<String>> data) throws Exception {
		if (data != null) {
			FileWriter fw = null;
			CSVWriter csvWriter = null;
			try {
				fw = new FileWriter(targetFile);
				csvWriter = new CSVWriter(fw, ';', CSVWriter.DEFAULT_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);
				for (List<String> row : data) {
					csvWriter.writeNext(row.toArray(new String[0]));
				}
			} finally {
				if (csvWriter != null) {
					csvWriter.close();
				}
			}
		}
	}

}
