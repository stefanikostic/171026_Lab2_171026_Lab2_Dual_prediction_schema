import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CreateExcelFile {
    private String fileName;
    private int counter;
    private HSSFWorkbook hssfWorkbook;
    private HSSFSheet hssfSheet;

    public List<Double> readTurbidityFromExcel (String filePath, String fileName, String sheetName) throws IOException {
        List<Double> turbidityData = new ArrayList<>();

        //Create an object of File class to open xlsx file
        File file = new File(filePath + "\\" + fileName);

        //Create an object of FileInputStream class to read excel file
        FileInputStream inputStream = new FileInputStream(file);
        Workbook workbook = null;

        //Find the file extension by splitting file name in substring  and getting only extension name
        String fileExtensionName = fileName.substring(fileName.indexOf("."));

        //Check condition if the file is xls file
        if (fileExtensionName.equals(".xls")) {
            //If it is xls file then create object of HSSFWorkbook class
            workbook = new HSSFWorkbook(inputStream);
        }

        //Read sheet inside the workbook by its name

        Sheet sheet = workbook.getSheet(sheetName);

        //Find number of rows in excel file

        int rowCount = sheet.getLastRowNum() - sheet.getFirstRowNum();

        //Create a loop over all the rows of excel file to read it

        for (int i = 1; i < rowCount + 1; i++) {
            Row row = sheet.getRow(i);

            if (row.getCell(3) != null) {
                turbidityData.add(row.getCell(3).getNumericCellValue());
            }
        }
        return turbidityData;
    }

    public List<Double> readWaterTemperatureFromExcel (String filePath, String fileName,
                                                       String sheetName) throws IOException {
        List<Double> waterTemperatureData = new ArrayList<>();

        //Create an object of File class to open xlsx file
        File file = new File(filePath + "\\" + fileName);

        //Create an object of FileInputStream class to read excel file
        FileInputStream inputStream = new FileInputStream(file);
        Workbook workbook = null;

        //Find the file extension by splitting file name in substring  and getting only extension name
        String fileExtensionName = fileName.substring(fileName.indexOf("."));

        //Check condition if the file is xls file
        if (fileExtensionName.equals(".xls")) {
            //If it is xls file then create object of HSSFWorkbook class
            workbook = new HSSFWorkbook(inputStream);
        }

        //Read sheet inside the workbook by its name

        Sheet sheet = workbook.getSheet(sheetName);

        //Find number of rows in excel file

        int rowCount = sheet.getLastRowNum() - sheet.getFirstRowNum();

        //Create a loop over all the rows of excel file to read it

        for (int i = 1; i < rowCount + 1; i++) {
            Row row = sheet.getRow(i);

            if (row.getCell(2) != null) {
                waterTemperatureData.add(row.getCell(2).getNumericCellValue());
            }
        }
        return waterTemperatureData;
    }

    public void createInitialExcelFile (String fileName) {
        this.fileName = fileName;
        hssfWorkbook = new HSSFWorkbook();
        hssfSheet = hssfWorkbook.createSheet("Data");
        this.counter = 0;
        HSSFRow rowHead = hssfSheet.createRow((short) counter);
        rowHead.createCell(0).setCellValue("Dual Prediction Schema Algorithm");
        rowHead.createCell(1).setCellValue("Percentage of sent data");
        rowHead.createCell(2).setCellValue("e - Error");
        rowHead.createCell(3).setCellValue("RMSE - Root Mean Square Error");
        counter++;
    }

    public void createInitialExcelFile2D (String fileName) {
        this.fileName = fileName;
        hssfWorkbook = new HSSFWorkbook();
        hssfSheet = hssfWorkbook.createSheet("Data");
        this.counter = 0;
        HSSFRow rowHead = hssfSheet.createRow((short) counter);
        rowHead.createCell(0).setCellValue("Dual Prediction Schema Algorithm");
        rowHead.createCell(1).setCellValue("Percentage of sent data");
        rowHead.createCell(2).setCellValue("Threshold (error) of water turbidity");
        rowHead.createCell(3).setCellValue("Threshold (error) of water temperature");
        rowHead.createCell(4).setCellValue("RMSE - Root Mean Square Error");
        counter++;
    }

    public void addRowForDualPrediction (String algorithm, double sentDataPercentage, double error, double RMSE) {
        HSSFRow rowHead = hssfSheet.createRow((short) counter);
        rowHead.createCell(0).setCellValue(algorithm);
        rowHead.createCell(1).setCellValue(sentDataPercentage);
        rowHead.createCell(2).setCellValue(error);
        rowHead.createCell(3).setCellValue(RMSE);
        counter++;
    }

    public void addRowForDualPrediction2D (String algorithm, double sentDataPercentage, double errorTurbidity,
                                           double errorTemperature, double RMSE) {
        HSSFRow rowHead = hssfSheet.createRow((short) counter);
        rowHead.createCell(0).setCellValue(algorithm);
        rowHead.createCell(1).setCellValue(sentDataPercentage);
        rowHead.createCell(2).setCellValue(errorTurbidity);
        rowHead.createCell(3).setCellValue(errorTemperature);
        rowHead.createCell(4).setCellValue(RMSE);
        counter++;
    }


    public void closeFile () throws IOException {
        FileOutputStream fileOut = new FileOutputStream(fileName);
        hssfWorkbook.write(fileOut);
        //closing the Stream
        fileOut.close();
        //closing the workbook
        hssfWorkbook.close();
    }
}
