package edu.si.ossearch.reports.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author jbirkhimer
 */
public interface ExportService {

    ByteArrayInputStream toCSV(List<Map<String, Object>> rows);

    ByteArrayInputStream toCSV(List<String> headers, List<List<String>> rows);

    ByteArrayInputStream toExcel(List<Map<String, Object>> data, String name) throws IOException;

    ByteArrayInputStream toPdf(List<Map<String, Object>> data);
}
