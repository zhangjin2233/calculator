import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.math.BigDecimal;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.regex.Pattern;

public class NewStartLoad {

    private int INSERT_ODS_RIGHT_ROWNUMS;// 定义处理成功条数
    private int INSERT_ODS_WRONG_ROWNUMS;// 定义处理失败条数
    private int ods_all_rownums;// 传入表格文件所有的行数
    private String datetime;//ODS入库开始时间
    private String datetime2;// ODS入库完成的时间
    private int fileTrail;//表尾行数
    private int beginCell;//起始列下标
    private int num;//与起始列下表在预编译sql配合使用
    private Boolean ifLoad;//是否执行导入ODS表
    private Boolean add;//判断是否进行添加

    public Serializable execute(String file_list, String file_name, DBDataSource datasource,String Err_File_Path, String Err_Log,
                                int startRow, PDCDataSet cellNumSet, String tableName, String sql, Boolean trail,Boolean firstCell,PDCDataSet numberCellSet,PDCDataSet dateCellSet,PDCDataSet jasonCellSet){
        //如果文件检查有问题则不会执行插入的操作
        File errFile = new File(Err_File_Path+"/"+Err_Log);
        if(errFile.length()!=0){
            return null;
        }
        //初始化操作
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd :HH:mm:ss");
        initial(trail,firstCell,sdf);
        String fileFolder = "";//结束之后将文件move到的文件夹
        File file = new File(file_list + "/" + file_name);
        //获得JDBC连接
        GetConnection jdbc = new GetConnection();
        Statement stmt = null;
        Connection c = jdbc.connection(datasource);
        PreparedStatement psts = null;
        // 创建文件输入流
        FileInputStream excelFileInputStream = null;

        // 创建文件输出流
        FileOutputStream fileOut = null;

        // 文件写出
        OutputStreamWriter writer = null;
        try {
            c.setAutoCommit(false);
            psts = c.prepareStatement(sql);
            stmt = c.createStatement();
            writer = new OutputStreamWriter(new FileOutputStream(Err_File_Path + "/" + Err_Log, true),"unicode");

            //清除之前的上传数据
            clearTable(stmt,tableName,file_name);
            //获取文件输入流
            excelFileInputStream = new FileInputStream(file);
            String file_type = file_name.substring(15);
            HSSFWorkbook hssfWorkbook = null;
            XSSFWorkbook xssfWorkbook = null;
            //判断文件类型，根据不同类型选择不同的插入方法
            if (file_type.equals("xls")) { // 判断文件格式是否为xls,如果是则执行以下操作
                //执行xls文件的插入
                hssfWorkbook = new HSSFWorkbook(excelFileInputStream);
            }else {
                //执行xlsx文件的插入
                xssfWorkbook = new XSSFWorkbook(excelFileInputStream);
            }
            loadFile(xssfWorkbook,hssfWorkbook,startRow,fileFolder,sdf,file_name,jasonCellSet,dateCellSet,cellNumSet,numberCellSet,writer,psts);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }finally {
            try {
                insertProgressTable(startRow,stmt,tableName,file_name);
                if (c != null) {
                    c.commit();
                    jdbc.closeConnection(c);
                }
                if (writer != null) {
                    writer.flush();
                    writer.close();
                }
                if (fileOut != null) {
                    fileOut.close();
                }
                if (excelFileInputStream != null) {
                    excelFileInputStream.close();
                }
            }catch (SQLException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return null;
    }
    private void insertProgressTable(int startRow,Statement stmt,String tableName,String file_name)throws SQLException{
        //所有数据的行数
        int totalDataRows = ods_all_rownums - startRow - fileTrail +2;
        //查询成功插入ODS表中的数据条数
        ResultSet rs =  stmt.executeQuery("select count(*) from "+tableName+" where ODS_FILE_NAME = '"+file_name+"'");
        if(rs.next()){
            INSERT_ODS_RIGHT_ROWNUMS = rs.getInt(1);
            INSERT_ODS_WRONG_ROWNUMS = totalDataRows - INSERT_ODS_RIGHT_ROWNUMS;
        }
        //插入ODS的结果
        int result = 0;
        if(INSERT_ODS_RIGHT_ROWNUMS==totalDataRows){
            result = 1;                               //插入ODS成功
        }else if(INSERT_ODS_RIGHT_ROWNUMS==0){
            result = 9;                               //插入ODS全部失败
        }else{
            result = 2;                               //插入ODS部分成功
        }

        // 执行状态表的插入
        String sql2 = "insert into LJS_DATA_DEAL_PROCESS(ods_file_name, ODS_LOAD_DATETIME,ods_all_rownums, insert_ods_datetime, insert_ods_result,insert_ods_right_rownums, insert_ods_wrong_rownums)"
                + "values('" + file_name + "','"+datetime+"'," + totalDataRows + ",'" + datetime2
                + "',"+result+"," + INSERT_ODS_RIGHT_ROWNUMS + "," + INSERT_ODS_WRONG_ROWNUMS + ")";
        stmt.executeUpdate(sql2);
    }
    //插入文件内容
    private void loadFile(XSSFWorkbook xssfWorkbook,HSSFWorkbook hssfWorkbook,int startRow,String fileOut,SimpleDateFormat sdf,String file_name,PDCDataSet jasonCellSet,PDCDataSet dateCellSet,PDCDataSet cellNumSet,PDCDataSet numberCellSet,OutputStreamWriter writer,PreparedStatement psts)throws SQLException{
        Boolean isXSSF = true;
        if (xssfWorkbook==null){
            isXSSF=false;
        }
        try{
            HSSFSheet hssfSheet = null;
            XSSFSheet xssfSheet = null;
            int lastCellNum = 0;
            if (isXSSF){
                hssfSheet = hssfWorkbook.getSheetAt(0);
                ods_all_rownums = hssfSheet.getLastRowNum();
                lastCellNum = hssfSheet.getRow(startRow-2).getLastCellNum();
            }else {
                xssfSheet = xssfWorkbook.getSheetAt(0);
                ods_all_rownums = xssfSheet.getLastRowNum();
                lastCellNum = xssfSheet.getRow(startRow-2).getLastCellNum();
            }
            HSSFRow hssfRow = null;
            XSSFRow xssfRow = null;
            // 循环所有的行，如果数据有误则记录到日志，全部数据无误则插入到ODS表
            for (int rowIndex = (startRow-1); rowIndex <= (ods_all_rownums - fileTrail); rowIndex++) {
                // 当前的行数据，如果为空则退出循环
                if (isXSSF){
                    hssfRow = hssfSheet.getRow(rowIndex);
                }else {
                    xssfRow = xssfSheet.getRow(rowIndex);
                }

                if(hssfRow==null){
                    break;
                }
                //判断处理规则中是否有为空的值，判断的列下标通过参数传进来，找出所有有错误的行数并记录到错误日志，如果数据有误则不执行插入ODS表的操作
                checkNull(cellNumSet,xssfRow,hssfRow,INSERT_ODS_WRONG_ROWNUMS,rowIndex,writer);

                //判断应该为数字类型的表格是否有误，如果有误则不执行插入ODS操作
                checkNumberCell(numberCellSet,xssfRow,hssfRow,rowIndex,writer);

                // 如果ifLoad=true;执行数据插入ODS表的操作。如果ifLoad=false;检查下一行数据是否有误
                if (ifLoad) {
                    // 循环当前行的每一列
                    for (int i = beginCell; i < lastCellNum; i++) {
                        HSSFCell hssfCell = hssfRow.getCell(i);
                        XSSFCell xssfCell = xssfRow.getCell(i);
                        //判断当前单元格是否已经添加，add=true表示可以进行判断添加
                        add = true;
                        //判断当前单元格是否是数字类型、时间类型、字符串类型，然后对空的单元格赋不同的初始值
                        //1.判断数字类型的单元格
                        if(numberCellSet.getRowCount()!=0){
                            psts = addLong2Psts(numberCellSet,hssfCell,xssfCell,i,psts);
                        }
                        if (add){
                            psts = addDate2Psts(psts,dateCellSet,rowIndex,i,hssfCell,xssfCell);
                        }

                        if (add){
                            psts = addNormal2Psts(psts,hssfCell,xssfCell,i);
                        }
                    }
                    //设置部分字符串为jason格式
                    if(jasonCellSet.getRowCount()!=0){
                        for (int rowID = 0; rowID < jasonCellSet.getRowCount(); rowID++) {
                            Map<String, String> map = dateCellSet.getRow(rowID);
                            String cellNum = map.get("cellNum");
                            int x = Integer.parseInt(cellNum);
                            if (hssfRow!=null) {
                                psts.setString(x + 3, change2json(hssfRow.getCell(x - 1).getStringCellValue()));
                            }else {
                                psts.setString(x + 3, change2json(xssfRow.getCell(x - 1).getStringCellValue()));
                            }
                        }
                    }

                    psts.setString(1, file_name);
                    psts.setString(2, datetime);
                    psts.setString(3, (rowIndex+1)+"");
                    psts.addBatch();
                }
                else {
                    ifLoad = true;
                    continue;
                }
            }
            //执行批量插入
            psts.executeBatch();
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            try {
                if (xssfWorkbook != null) {
                    xssfWorkbook.close();
                }
                if (hssfWorkbook!=null){
                    hssfWorkbook.close();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    //判断Cell中的值，并向psts中插入String
    private PreparedStatement addNormal2Psts(PreparedStatement psts,HSSFCell hssfCell, XSSFCell xssfCell,int i)throws SQLException{
        if (hssfCell!=null){
            if (hssfCell == null||"".equals(hssfCell.toString())) {
                psts.setString((i+num), "");
            }else if(1==hssfCell.getCellType()){
                psts.setString((i+num), hssfCell.getStringCellValue());
            }else{
                psts.setString((i+num), String.format("%.0f", hssfCell.getNumericCellValue()));
            }
        }else {
            if (xssfCell == null || "".equals(xssfCell.toString())) {
                psts.setString((i + num), "");
            } else if (1 == xssfCell.getCellType()) {
                psts.setString((i + num), xssfCell.getStringCellValue());
            } else {
                psts.setString((i + num), String.format("%.0f", xssfCell.getNumericCellValue()));
            }
        }
        return psts;
    }

    //判断Cell中的值，并向psts中插入Date
    private PreparedStatement addDate2Psts(PreparedStatement psts,PDCDataSet dateCellSet,int rowIndex,int i,HSSFCell hssfCell, XSSFCell xssfCell)throws SQLException{
        if(dateCellSet.getRowCount()!=0){
            for (int rowID = 0; rowID < dateCellSet.getRowCount(); rowID++) {
                Map<String, String> map = dateCellSet.getRow(rowID);
                String cellNum = map.get("cellNum");
                int x = Integer.parseInt(cellNum);

                if ((i + 1) == x) {
                    add = false;
                    if (hssfCell != null) {
                        if (rowIndex == 1) {
                        }
                        if (hssfCell == null || "".equals(hssfCell.toString())) {
                            psts.setString((i + num), "0000-00-00 00:00:00");
                        } else if (0 == hssfCell.getCellType()) {
                            psts.setString((i + num), hssfCell.getNumericCellValue() + "");
                        } else {
                            String ss = hssfCell.getStringCellValue().replace("//", "-");
                            if (rowIndex == 1) {
                            }
                            psts.setString((i + num), ss);
                        }
                    }else {
                        if (rowIndex == 1) {
                        }
                        if (xssfCell == null || "".equals(xssfCell.toString())) {
                            psts.setString((i + num), "0000-00-00 00:00:00");
                        } else if (0 == xssfCell.getCellType()) {
                            //表格类型为数值型的时候
                            //                               LvUtil.trace("日期类型   "+nowCell.getNumericCellValue()+"");
                            psts.setString((i + num), xssfCell.getNumericCellValue() + "");
                        } else {
                            String ss = xssfCell.getStringCellValue().replace("//", "-");
                            if (rowIndex == 1) {
                            }
                            psts.setString((i + num), ss);
                        }
                    }
                }
            }
        }
        return psts;
    }

    //判断Cell中的值，并向psts中插入Long
    private PreparedStatement addLong2Psts(PDCDataSet numberCellSet, HSSFCell hssfCell, XSSFCell xssfCell, int i, PreparedStatement psts)throws SQLException{
        for (int rowID = 0; rowID < numberCellSet.getRowCount(); rowID++) {
            Map<String, String> map = numberCellSet.getRow(rowID);
            String cellNum = map.get("cellNum");
            int x = Integer.parseInt(cellNum);

            //如果列号吻合，开始往批量添加里面进行添加数据
            if((i+1)==x){
                add = false;
                //如果表格内容为空，则设置初始值为0
                if (xssfCell!=null){
                    if (("".equals(xssfCell.toString()) || xssfCell == null)) {
                        psts.setLong((i+num), 0);
                        //如果表格格式为文本
                    }else if(1==xssfCell.getCellType()){
                        //如果表格内容为空，则设置初始值为0
                        if("".equals(xssfCell.getStringCellValue())){
                            psts.setLong((i+num), 0);
                        }else{
                            if(xssfCell.getStringCellValue().contains(".")){
                                psts.setString((i+num), xssfCell.getStringCellValue());
                            }else{
                                psts.setLong((i+num), Long.parseLong(xssfCell.getStringCellValue()));
                            }
                        }
                    }else{
                        psts.setLong((i+num), (long) xssfCell.getNumericCellValue());
                    }
                }else {
                    if (("".equals(hssfCell.toString()) || hssfCell == null)) {
                        psts.setLong((i + num), 0);
                        //如果表格格式为文本
                    } else if (1 == hssfCell.getCellType()) {
                        //如果表格内容为空，则设置初始值为0
                        if ("".equals(hssfCell.getStringCellValue())) {
                            psts.setLong((i + num), 0);
                        } else {
                            if (hssfCell.getStringCellValue().contains(".")) {
                                psts.setString((i + num), hssfCell.getStringCellValue());
                            } else {
                                psts.setLong((i + num), Long.parseLong(hssfCell.getStringCellValue()));
                            }
                        }
                    } else {
                        psts.setLong((i + num), (long) hssfCell.getNumericCellValue());
                    }
                }
            }
        }
        return psts;
    }

    //检查数字类型的列是否正确
    public void checkNumberCell(PDCDataSet numberCellSet,XSSFRow xRow,HSSFRow hRow,int rowIndex,OutputStreamWriter writer)throws IOException{
        if (numberCellSet.getRowCount()!=0) {
            String err = "";
            int i = 0;
            String cellStr = "";
            try{
                for (int rowID = 0; rowID < numberCellSet.getRowCount(); rowID++) {
                    Map<String, String> map = numberCellSet.getRow(rowID);
                    i = Integer.parseInt(map.get("cellNum"));
                    if (xRow==null){
                        cellStr = hRow.getCell(i-1).toString();
                        if (hRow.getCell(i-1) == null || "".equals(cellStr)) {
                        }else{
                            if(!isNumeric(hRow.getCell(i-1).toString())){
                                BigDecimal bd1 = new BigDecimal(cellStr);
                                if(!isNumeric(bd1+"")){ err = err + "第"+(rowIndex + 1)+"行，"+i+"    "+bd1+"列数据应该为数字但是被误填为字符\n";
                                    writer.write(err);
                                    ifLoad = false;
                                    break;
                                }
                            }
                        }
                    }else {
                        cellStr = xRow.getCell(i-1).toString();
                        if (xRow.getCell(i-1) == null || "".equals(cellStr)) {
                        }else{
                            if(!isNumeric(xRow.getCell(i-1).toString())){
                                BigDecimal bd1 = new BigDecimal(cellStr);
                                if(!isNumeric(bd1+"")){ err = err + "第"+(rowIndex + 1)+"行，"+i+"    "+bd1+"列数据应该为数字但是被误填为字符\n";
                                    writer.write(err);
                                    ifLoad = false;
                                    break;
                                }
                            }
                        }
                    }
                }
            }catch(Exception e){
                err = err + "第"+(rowIndex + 1)+"行，"+i+"    "+cellStr+"列数据应该为数字但是被误填为字符\n";
                writer.write(err);
                ifLoad = false;
            }
        }
    }

    //判断字符串是否为数字
    public boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("-?[0-9]+.?[0-9]*");
        if(pattern.matcher(str).matches()){
            return true;
        }else{
            return isDouble(str);
        }
    }

    //判断是否为浮点型
    public boolean isDouble(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[.\\d]*$");
        return pattern.matcher(str).matches();
    }

    //检查空值，XSSFRow、HSSFRow两种类型的行都能进行处理
    public void checkNull(PDCDataSet cellNumSet,XSSFRow xRow,HSSFRow hRow,int INSERT_ODS_WRONG_ROWNUMS,int rowIndex,OutputStreamWriter writer){
        // 判断处理规则中是否有为空的值，判断的列下标通过参数传进来，找出所有有错误的行数，如果数据有误则不执行插入ODS表的操作
        if (cellNumSet.getRowCount()!=0) {
            try{
                for (int rowID = 0; rowID < cellNumSet.getRowCount(); rowID++) {
                    Map<String, String> map = cellNumSet.getRow(rowID);
                    String cellNum = map.get("cellNum");
                    int i = Integer.parseInt(cellNum);
                    String err = "";
                    if (xRow==null){
                        if (hRow.getCell(i-1) == null || "".equals(hRow.getCell(i-1).toString())) {
                            INSERT_ODS_WRONG_ROWNUMS++;
                            err = err + "第"+(rowIndex + 1)+"行，"+i+"列数据不符合要求\n";
                            writer.write(err);
                            ifLoad = false;
                            break;
                        }
                    }else {
                        if (xRow.getCell(i-1) == null || "".equals(xRow.getCell(i-1).toString())) {
                            INSERT_ODS_WRONG_ROWNUMS++;
                            err = err + "第"+(rowIndex + 1)+"行，"+i+"列数据不符合要求\n";
                            writer.write(err);
                            ifLoad = false;
                            break;
                        }
                    }
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    //清理之前插入的数据
    public void clearTable(Statement stmt,String tableName,String file_name){
        try {
            // 首先删除ODS表中文件名为上传文件的数据
            stmt.executeUpdate("delete from " + tableName + "  where ODS_FILE_NAME = '" + file_name + "'");
            //删除状态表中文件名为上传文件的数据
            stmt.executeUpdate("delete from LJS_DATA_DEAL_PROCESS where ODS_FILE_NAME = '"+file_name+"'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //初始化各个数据
    public void initial(Boolean trail,Boolean firstCell,SimpleDateFormat sdf){
        INSERT_ODS_RIGHT_ROWNUMS = 0;
        INSERT_ODS_WRONG_ROWNUMS = 0;
        ods_all_rownums = 0;
        datetime2 = "";

        //判断表尾
        fileTrail = 0;
        if (trail) {
            fileTrail = 1;
        }

        //起始读取的列下标
        beginCell = 1;
        num = 3;
        if (firstCell) {
            beginCell = 0;
            num = 4;
        }
        // ifLoad为true才会执行插入ODS表的操作，一旦有数据出现异常ifLoad=false，本行数据数据不会执行插入，后面的数据会再次进行判断是否插入
        ifLoad = true;
        //获取开始处理的时间
        Calendar calendar = Calendar.getInstance();
        datetime = sdf.format(calendar.getTime());
    }

    //将字符串转换为json格式
    public String change2json(String str){
        String jsonStr = "{";
        String[]arr1 =  str.split(";");
        for(int i = 0 ; i<arr1.length ;i++){
            String cell = "";
            String[]arr2 =  null;
            if(str.contains("，")){
                arr2 =  str.split("，");
            }else{
                arr2 =  str.split(",");
            }
            for(int j = 0 ; j<arr2.length ;j++){
                if(j==0){
                    cell = cell+"{\"name\":\""+arr2[j]+"\",";
                }else{
                    cell = cell+"\"phone\":\""+arr2[j]+"\"}";
                }
            }
            if(i==(arr1.length-1)){
                jsonStr = jsonStr + cell+"}";
            }else{
                jsonStr = jsonStr + cell+",";
            }
        }
        return jsonStr;
    }
}
