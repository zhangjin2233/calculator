import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileNotFoundException;

public class Excel2Json {
    public String change2Json(File file){
        String json = "";
        try{
            Workbook workbook = WorkbookFactory.create(file);
            workbook2json(workbook);
        }catch (FileNotFoundException ex){
            ex.printStackTrace();
            return "文件不存在";
        }catch (Exception e){
            return "异常";
        }finally {

        }

        return json;
    }

    public String workbook2json(Workbook workbook){
        int nums = workbook.getNumberOfSheets();
        for (int i = 0; i < nums; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            sheet2json(sheet);
        }
        return "";
    }

    public String sheet2json(Sheet sheet){
        Row row = sheet.getRow(0);
        Cell cell0 = row.getCell(0);
        String str = cell0.getStringCellValue();
        if ("PDF名称".equals(str)){
            createSrcTabCollocateJson(sheet);
        }else if ("数据源".equals(str)){
            createOutputTabCollocateJson(sheet);
        }else if ("来源节点名称".equals(str)){
            createRelationJson(sheet);
        }else if ("节点名称".equals(str)){
            String str3 = row.getCell(3).getStringCellValue();
            if ("SQL扩展部件".equals(str3)){
                createSQLComponentJson(sheet);
            }else if("分支部件".equals(str3)){
                createBranchComponentJson(sheet);
            }
        }else if ("变量名".equals(str)){
            createVariableJson(sheet);
        }else {
            //有表不符合规范
            String err = sheet.getSheetName()+"含有名称错误";
        }
        return "";
    }

    //拼接来源表配置Json
    public String createSrcTabCollocateJson(Sheet sheet){
        StringBuffer sb = new StringBuffer("\"PDF名称\":");
        sb.append("\""+sheet.getRow(1).getCell(0).getStringCellValue()+"\",[");
        for (int i = 3; i < sheet.getLastRowNum(); i++) {

        }
        return "";
    }

    public String createOutputTabCollocateJson(Sheet sheet){
        return "";
    }

    public String createRelationJson(Sheet sheet){
        return "";
    }

    public String createSQLComponentJson(Sheet sheet){
        return "";
    }

    public String createBranchComponentJson(Sheet sheet){
        return "";
    }

    public String createVariableJson(Sheet sheet){
        return "";
    }

}
