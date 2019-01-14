import org.apache.poi.ss.usermodel.*;

import java.io.FileInputStream;

public class POITest {
    public static void main(String[] args){
        System.out.println("123");
        try {
            FileInputStream fileInputStream = new FileInputStream("D:/a.xlsx");
            Workbook workbook = WorkbookFactory.create(fileInputStream);
            Sheet sheet = workbook.getSheetAt(0);
            Row row = sheet.getRow(0);
            System.out.println("111");
            System.out.println(row.getCell(0).getCellTypeEnum());
            if(row.getCell(0).getCellTypeEnum().equals(CellType.NUMERIC)){
                System.out.println("22222222222");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
