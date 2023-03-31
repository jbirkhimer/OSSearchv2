package edu.si.ossearch.reports.service.impl;

import edu.si.ossearch.reports.service.ExportService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

/**
 * @author jbirkhimer
 */
@Slf4j
@Service
public class ExportServiceImpl implements ExportService {

    @Override
    public ByteArrayInputStream toCSV(List<Map<String, Object>> list) {
        List<String> headers = list.stream().flatMap(map -> map.keySet().stream()).distinct().collect(toList());

        List<List<String>> rows = new ArrayList<>();
        list.forEach(row -> {
            List<String> values = new ArrayList<>();
            row.entrySet().forEach(entry -> values.add(String.valueOf(entry.getValue())));
            rows.add(values);
        });
        return toCSV(headers, rows);
    }

    @Override
    public ByteArrayInputStream toCSV(List<String> headers, List<List<String>> rows) {

        ByteArrayInputStream byteArrayOutputStream;

        try (
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                // defining the CSV printer
                CSVPrinter csvPrinter = new CSVPrinter(
                        new PrintWriter(out),
                        // withHeader is optional
                        CSVFormat.Builder.create().setTrim(true).setHeader(headers.toArray(new String[headers.size()])).build()
                        //CSVFormat.DEFAULT.withHeader(headers.toArray(new String[headers.size()]))
                );
        ) {
            // populating the CSV content
            csvPrinter.printRecords(rows);
            // writing the underlying stream
            csvPrinter.flush();

            byteArrayOutputStream = new ByteArrayInputStream(out.toByteArray());

        } catch (IOException e) {
            log.error("problem creating csv", e);
            throw new RuntimeException(e.getMessage());
        }

        return byteArrayOutputStream;
    }

    @Override
    public ByteArrayInputStream toExcel(List<Map<String, Object>> data, String name) throws IOException {
        List<String> headers = data.stream().flatMap(map -> map.keySet().stream()).distinct().collect(toList());

        ByteArrayInputStream byteArrayOutputStream;

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             Workbook workbook = new XSSFWorkbook();) {

            Sheet sheet = workbook.createSheet(name);

            CellStyle headerStyle = workbook.createCellStyle();

            Row header = sheet.createRow(0);

            for (int i = 0; i < headers.size(); i++) {
                String headerName = headers.get(i);
                Cell headerCell = header.createCell(i);
                headerCell.setCellValue(headerName);
                headerCell.setCellStyle(headerStyle);
            }

            for (int i = 0; i < data.size(); i++) {
                Row row = sheet.createRow(i + 1);

                Map<String, Object> rowMap = data.get(i);

                for (int j = 0; j < headers.size(); j++) {
                    String headerName = headers.get(j);
                    Cell cell = row.createCell(j);
                    if (rowMap.get(headerName) != null && !rowMap.get(headerName).toString().isEmpty()) {
                        cell.setCellValue(rowMap.get(headerName).toString());

                        if (headerName.equalsIgnoreCase("url")) {
                            Hyperlink link = workbook.getCreationHelper().createHyperlink(HyperlinkType.URL);
                            link.setAddress(rowMap.get(headerName).toString());
                            cell.setHyperlink(link);
                        }
                    } else {
                        cell.setBlank();
                    }
                }
            }

            workbook.write(out);
            byteArrayOutputStream = new ByteArrayInputStream(out.toByteArray());
        }
        return byteArrayOutputStream;
    }

    @Override
    public ByteArrayInputStream toPdf(List<Map<String, Object>> data) {
        return null;
    }
}
