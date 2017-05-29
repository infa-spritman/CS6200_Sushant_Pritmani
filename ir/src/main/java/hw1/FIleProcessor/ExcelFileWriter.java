package hw1.FIleProcessor;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Sushant on 5/26/2017.
 */
public class ExcelFileWriter {



    public static void wrtiteToExcel(String reportPath, Integer k, Map<String, ArrayList<String>> significantTerms) {

        File file = new File(reportPath);
        XSSFWorkbook workbook =null;
        // if file doesnt exists, then create it
        if (!file.exists()) {
            workbook = new XSSFWorkbook();
        }else{
            FileInputStream is = null;
            try {
                is = new FileInputStream(file);
                workbook = new XSSFWorkbook(is);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }



        XSSFSheet sheet = workbook.createSheet("Query_No_" + k);

        int rowNum = 0;
        System.out.println("Creating excel");

        AtomicInteger atomicInteger = new AtomicInteger(0);

        CellStyle style = workbook.createCellStyle(); //Create new style
        style.setWrapText(true); //Set wordwrap

        significantTerms.forEach((t, l) -> {
            Row row = sheet.createRow(atomicInteger.getAndIncrement());
            int colNum =0;

            Cell cell_term = row.createCell(colNum++);
            cell_term.setCellStyle(style);
            cell_term.setCellValue(t);

            Cell cell_list  = row.createCell(colNum);
            cell_list.setCellStyle(style);
            cell_list.setCellValue(l.toString());

        });
        sheet.autoSizeColumn(1);
        try {
            FileOutputStream outputStream = new FileOutputStream(reportPath);
            workbook.write(outputStream);
            workbook.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Done");




    }
}
