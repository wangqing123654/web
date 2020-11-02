package com.javahis.util;

import java.util.Vector;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import javax.swing.JOptionPane;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.*;
import jxl.Workbook;
import jxl.write.WritableWorkbook;
import jxl.write.WritableSheet;
import jxl.write.Label;
import jxl.write.WritableFont;
import jxl.write.WritableCellFormat;
import jxl.write.*;
import java.sql.Timestamp;
import com.dongyang.ui.TTable;
import com.dongyang.data.TParm;
import jdo.sys.SystemTool;
import com.dongyang.util.StringTool;
import com.dongyang.control.TControl;
import jxl.format.UnderlineStyle;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class ExportExcelUtil extends TControl{
    private static final ExportExcelUtil INSTANCE =new ExportExcelUtil();

    private ExportExcelUtil() {
    }

    public static ExportExcelUtil getInstance(){

        return INSTANCE;
    }

    private TTable table;
    private String parmMap = "";
    
    /**
     * 导出EXCEL的方法的接口
     * @param table TTable table对象
     * @param defName String 默认文件的名字
     */
    public void exportExcel(TTable mainTable, String defName) {
        table=mainTable;
        //得到talble的头
        String header = mainTable.getHeader();
        //只得到table上显示的数据（PS：隐藏数据得不到）
        TParm mainDate = mainTable.getShowParmValue();
        if (mainDate.getCount() <= 0) {
            this.messageBox("无导出EXCEL数据！");
            return;
        }
        //调用导出excel方法（标题头，主数据，文件名字）
        exeSaveExcel(header, mainDate, defName);

    }
    /**
     * 导出EXCEL的方法的接口
     * @param table TTable table对象
     * @param defName String 默认文件的名字
     */
    public void exportExcel(String header,String parmMap,TParm parm, String defName) {
        //table=mainTable;
        this.parmMap = parmMap;
        //得到talble的头
        /*String header = mainTable.getHeader();
        if (parm.getCount() <= 0) {
            this.messageBox("无导出EXCEL数据！");
            return;
        }*/
        //调用导出excel方法（标题头，主数据，文件名字）
        exeSaveExcel(header, parm, defName);

    }
    /**
     * 导出excel文件
     * @param headerDate String
     * @param mainDate TParm
     * @param fileDefName 默认文件名字
     */

    public void exeSaveExcel(String headerDate, TParm mainDate,String fileDefName) {
        //重新整理标题头数据（只留下标题）
        Vector arrHeader = arrHeader(headerDate);
        //标准SWING
        JFileChooser chooser = new JFileChooser();
        //设置当前的目录（目前写死）
        File dir= new File("C:\\JavaHis\\Excel");
        if(!dir.exists())
            dir.mkdirs();
        chooser.setCurrentDirectory(dir);
        //利用当前时间附加到默认文件名上
        Timestamp optTime = SystemTool.getInstance().getDate();
        fileDefName=fileDefName+StringTool.getString(optTime,"yyyyMMddHHmmss");
        //默认的文件保存名字
        chooser.setSelectedFile(new File(fileDefName));//提供默认名
        //设置对话框的标题
        chooser.setDialogTitle("导出EXCEL界面");
        //设置过滤（扩展名）
        chooser.setFileFilter(new FileFilter() {
            public boolean accept(File f) {
                return f.getName().toUpperCase().endsWith(".XLS");
            }

            public String getDescription() {
                return "Excel (*.xls)";
            }
        });

        int returnVal = chooser.showSaveDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String path = chooser.getSelectedFile().getAbsolutePath();
            this.writeFile(arrHeader, mainDate, path);
        }
    }
    
    /**
     * 导出EXCEL的方法的接口（文件名后不带时间）
     * @param table TTable table对象
     * @param defName String 默认文件的名字
     */
    public void exportExcelWithoutTime(TTable mainTable, String defName) {// add by wanglong 20130419
        table = mainTable;
        // 得到talble的头
        String header = mainTable.getHeader();
        //只得到table上显示的数据（PS：隐藏数据得不到）
        TParm mainDate = mainTable.getShowParmValue();
        if (mainDate.getCount() <= 0) {
            this.messageBox("无导出EXCEL数据！");
            return;
        }
        //调用导出excel方法（标题头，主数据，文件名字）
        exeSaveExcel(header, mainDate, defName, false);
    }

    /**
     * 导出excel文件(可以选择文件名是否带时间)
     * @param headerDate String
     * @param mainDate TParm
     * @param fileDefName 默认文件名字
     */

    public void exeSaveExcel(String headerDate, TParm mainDate, String fileDefName, boolean withTime) {// add by wanglong 20130419
        // 重新整理标题头数据（只留下标题）
        Vector arrHeader = arrHeader(headerDate);
        // 标准SWING
        JFileChooser chooser = new JFileChooser();
        //设置当前的目录（目前写死）
        File dir= new File("C:\\JavaHis\\Excel");
        if(!dir.exists())
            dir.mkdirs();
        chooser.setCurrentDirectory(dir);
        //利用当前时间附加到默认文件名上
        Timestamp optTime = SystemTool.getInstance().getDate();
        if (withTime) {
            fileDefName = fileDefName + StringTool.getString(optTime, "yyyyMMddHHmmss");
        }
        //默认的文件保存名字
        chooser.setSelectedFile(new File(fileDefName));//提供默认名
        //设置对话框的标题
        chooser.setDialogTitle("导出EXCEL界面");
        //设置过滤（扩展名）
        chooser.setFileFilter(new FileFilter() {
            public boolean accept(File f) {
                return f.getName().toUpperCase().endsWith(".XLS");
            }

            public String getDescription() {
                return "Excel (*.xls)";
            }
        });

        int returnVal = chooser.showSaveDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String path = chooser.getSelectedFile().getAbsolutePath();
            this.writeFile(arrHeader, mainDate, path);
        }
    }
    
    /**
     * 导出excel文件
     * @param headerDate String
     * @param mainDate TParm
     * @param fileDefName 默认文件名字
     */

    public void exeSaveExcel(TParm[] mainDate,String fileDefName) {
        //重新整理标题头数据（只留下标题）
        int mainDCount =  mainDate.length;
        for(int i=0;i<mainDCount;i++){
//            System.out.println("重新整理标题头数据==="+mainDate[i].getValue("HEAD"));
//            System.out.println("数据==="+mainDate[i]);
            mainDate[i].setData("HEAD",arrHeader(mainDate[i].getValue("HEAD")));
        }
        //标准SWING
        JFileChooser chooser = new JFileChooser();
        //设置当前的目录（目前写死）
        File dir= new File("C:\\JavaHis\\Excel");
        if(!dir.exists())
            dir.mkdirs();
        chooser.setCurrentDirectory(dir);
        //利用当前时间附加到默认文件名上
        Timestamp optTime = SystemTool.getInstance().getDate();
        fileDefName=fileDefName+StringTool.getString(optTime,"yyyyMMddHHmmss");
        //默认的文件保存名字
        chooser.setSelectedFile(new File(fileDefName));//提供默认名
        //设置对话框的标题
        chooser.setDialogTitle("导出EXCEL界面");
        //设置过滤（扩展名）
        chooser.setFileFilter(new FileFilter() {
            public boolean accept(File f) {
                return f.getName().toUpperCase().endsWith(".XLS");
            }

            public String getDescription() {
                return "Excel (*.xls)";
            }
        });

        int returnVal = chooser.showSaveDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String path = chooser.getSelectedFile().getAbsolutePath();
            this.writeFile(mainDate, path);
        }
    }
    /**
     * 打开保存对话框选择路径
     * @param header Vector
     * @param date TParm
     * @param FileName String
     */
    public void writeFile(Vector header, TParm date, String FileName) {
        //创建一个文件对象
        File file;
        try {
            //过路径中包含.xls就不加后缀
            if (!FileName.contains(".xls")) {
                file = new File(FileName + ".xls");
            }
            else
                file = new File(FileName);
            //判断该文件是否存在，如果已经存在提示是否覆盖（不覆盖就退出操作）
            if (file.exists() &&
                JOptionPane.showConfirmDialog(null, "save", "该文件已经存在,是否覆盖？",
                                              JOptionPane.YES_NO_OPTION) == 1)
                return;
            //把文件变成输出流
            FileOutputStream fileOutStream = null;
            fileOutStream = new FileOutputStream(file);
            //执行数据整理并导出
            exportToExcel(header, date, fileOutStream);
            fileOutStream.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "转档失败");
        }
    }
    /**
     * 打开保存对话框选择路径
     * @param header Vector
     * @param date TParm
     * @param FileName String
     */
    public void writeFile(TParm[] date, String FileName) {
        //创建一个文件对象
        File file;
        try {
            //过路径中包含.xls就不加后缀
            if (!FileName.contains(".xls")) {
                file = new File(FileName + ".xls");
            }
            else
                file = new File(FileName);
            //判断该文件是否存在，如果已经存在提示是否覆盖（不覆盖就退出操作）
            if (file.exists() &&
                JOptionPane.showConfirmDialog(null, "save", "该文件已经存在,是否覆盖？",
                                              JOptionPane.YES_NO_OPTION) == 1)
                return;
            //把文件变成输出流
            FileOutputStream fileOutStream = null;
            fileOutStream = new FileOutputStream(file);
            //执行数据整理并导出
            exportToExcel(date, fileOutStream);
            fileOutStream.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "转档失败");
        }
    }

    /**
     * 导出excel整理数据和设置格式（主方法）
     * @param headerDate Vector
     * @param date TParm
     * @param os OutputStream
     */

    public void exportToExcel(TParm[] date, OutputStream os) {

        int headCount = date.length;
        int err=0;
        //打开文件
        WritableWorkbook workbook = null;
        try {
            workbook = Workbook.createWorkbook(os);
        }
        catch (IOException ex1) {
            ex1.fillInStackTrace();
        }
        for (int w = 0; w < headCount; w++) {
            Vector temp = date[w].getVector("HEAD");
            //取得需要的数据
            Vector header = (Vector)((Vector) temp.get(0)).get(0);
            Vector colView = (Vector)((Vector) temp.get(1)).get(0);
            Vector dateType = (Vector)((Vector) temp.get(2)).get(0);
            //取得列数
            int colNum = header.size();
            //设置格式化列标题
            WritableFont fontHeader = new WritableFont(WritableFont.createFont(
                "楷体 _GB2312"), 12, WritableFont.BOLD);
            WritableCellFormat formatHeader = new WritableCellFormat(fontHeader);
            //设置格式化主数据
            WritableFont fontDate = new WritableFont(WritableFont.createFont(
                "楷体 _GB2312"), 10, WritableFont.NO_BOLD);
            //格式化一般的数据（靠左）
            WritableCellFormat formatDateLeft = new WritableCellFormat(fontDate);
            //格式化数字（靠右）
            WritableCellFormat formatDateRight = new WritableCellFormat(
                fontDate);

            try {
                //设定列标题居中
                formatHeader.setAlignment(jxl.format.Alignment.CENTRE);
                //设置自动换行
                formatHeader.setWrap(true);

                formatDateRight.setAlignment(jxl.format.Alignment.RIGHT);
                formatDateRight.setWrap(true);

                formatDateLeft.setAlignment(jxl.format.Alignment.LEFT);
                formatDateLeft.setWrap(true);
            }
            catch (WriteException ex) {
                ex.printStackTrace();
                err = -1;
            }
            try {

                //生成名为“报 表”的工作表，参数0表示这是第一页（sheet的名字）
                WritableSheet sheet1 = workbook.createSheet(date[w].getValue("TITLE"), w);
//                System.out.println("www========="+w+"===="+date[w].getValue("TITLE"));
                //设置列宽
                for (int n = 0; n < colNum; n++) {
                    int colViwe = Integer.parseInt( (String) colView.get(n));
                    //循环设置列宽（是table上面长度的10分之一）
                    sheet1.setColumnView(n, colViwe / 10);
                }

                //添加表头
                for (int i = 0; i < colNum; i++) {
                    //添加表头的一行（标题）
                    Label row1 = new Label(i, 0, (String) header.get(i),
                                           formatHeader);
                    sheet1.addCell(row1);
                }

                //添加主数据
                int dateRow = date[w].getCount();
                //得到默认组的map对照表，通过它来依次拿数据（因为map的排序是不准的）
//                String[] title = date[w].getNames();
//                date[w].getData("SYSTEM","COLUMNS");
                Vector columns = (Vector)date[w].getData("SYSTEM","COLUMNS");
                //循环添加主数据
                for (int j = 0; j < dateRow; j++) { //行
                    for (int n = 0; n < colNum; n++) { //列
                        //每一个格的数据
                        Label dateCell;
                        //得到该列的类型
                        String type = (String) dateType.get(n);
                        //如果是数字类型就靠右
                        if (type.equalsIgnoreCase("int") ||
                            type.equalsIgnoreCase("float") ||
                            type.equalsIgnoreCase("double") ||
                            type.equalsIgnoreCase("long")) {
                            //设置该数据靠右
                            dateCell = new Label(n, j + 1,
                                                 date[w].getValue(columns.get(n).toString(), j),
                                                 formatDateRight);
                        }
                        else {
                            //设置该数据靠左
//                            System.out.println("Excel导出参数"+date[w]);
//                            System.out.println("列名=="+columns.get(n).toString());
//                            System.out.println(""+(j + 1));
                            dateCell = new Label(n, j + 1,
                                                 date[w].getValue(columns.get(n).toString(), j),
                                                 formatDateLeft);
                        }
                        sheet1.addCell(dateCell);
                    }
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
                err = -1;
            }
        }
        try {
            workbook.write();
        }
        catch (IOException ex2) {
        }
        try {
            workbook.close();
        }
        catch (WriteException ex3) {
        }
        catch (IOException ex3) {
        }
        if(err!=0)
            JOptionPane.showMessageDialog(null, "转档失败");
        else
            JOptionPane.showMessageDialog(null, "转档成功");
    }
    /**
     * 写Excel
     * @param title String
     * @param head String[]
     * @param os OutputStream
     * @param parm TParm
     * @throws Exception
     */
    public  void writeExcel(String title,String[] head, OutputStream os,TParm parm) throws Exception {
    //列数量
    int columnNum = head.length;
    int rowCount = parm.getInt("ACTION","COUNT");
    if(columnNum==0||rowCount==0){
        JOptionPane.showMessageDialog(null, "无导出数据！");
    }
    //添加标题
    WritableWorkbook titleObj = Workbook.createWorkbook(os);
    WritableSheet sheetName = titleObj.createSheet(title,0);
    //列添加
    for(int i=0;i<columnNum;i++){
        WritableFont columnName = new WritableFont(WritableFont.createFont(
            "宋体"),10,WritableFont.BOLD,false,UnderlineStyle.NO_UNDERLINE, jxl.format.Colour.BLACK);
       WritableCellFormat columnFormat = new WritableCellFormat(columnName);
       jxl.write.Label column = new Label(i,0,head[i].split(":")[0],columnFormat);
       sheetName.addCell(column);
    }
    for(String temp:head){
//        System.out.println("列名"+temp);
    }
    //行添加
    for (int j = 0; j < rowCount; j++) {
//        System.out.println("一行数据"+parm.getRow(j));
        for(int i=0;i<head.length;i++){
            jxl.write.Label rowData = new jxl.write.Label(i, j+1, parm.getData(head[i].split(":")[1],j).toString());
            sheetName.addCell(rowData);
        }
    }
    try{
        //写入Exel工作表
        titleObj.write();
        //关闭Excel工作薄对象
        titleObj.close();
        os.flush();
        os.close();
        JOptionPane.showMessageDialog(null, "存档成功！");
    }catch(Exception ex){
        JOptionPane.showMessageDialog(null, "存档失败！");
    }
    }
    /**
     * 生成Excel
     * @param title String
     * @param head String[]
     * @param parm TParm
     * @return boolean
     * @throws IOException
     */
    public  boolean creatExcelFile(String title,String[] head,TParm parm)throws IOException,Exception{
        String path = getPath();
        if(path.length()==0)
            return false;
        File dirSel = new File(path);
//        System.out.println("路径"+path);
        if(!path.contains(".xls"))
            return false;
        if (dirSel.exists() &&JOptionPane.showConfirmDialog(null, "该文件已经存在,是否覆盖？", "保存",JOptionPane.YES_NO_OPTION) == 1)
            return false;
        dirSel.createNewFile();
        writeExcel(title,head,new FileOutputStream(dirSel),parm);
        return true;
    }
    /**
     * 选择路径
     * @return String
     */
    public  String getPath() throws IOException{
        String path = "";
        String fileName="";
//        com.javahis.util.JavaHisDebug.initClient();
        JFileChooser chooser = new JFileChooser();
        File dir= new File("C:\\JavaHis\\Excel");
        if(!dir.exists())
            dir.mkdirs();
        chooser.setCurrentDirectory(dir);
        chooser.setSelectedFile(new File("未命名"));
        chooser.setDialogTitle("导出EXCEL界面");
        chooser.setFileFilter(new FileFilter() {
            public boolean accept(File f) {
                return f.getName().toUpperCase().endsWith(".XLS");
            }

            public String getDescription() {
                return "Excel (*.xls)";
            }
        });

        int returnVal = chooser.showSaveDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            if(!chooser.getSelectedFile().getName().contains(".xls")){
                fileName = chooser.getSelectedFile().getName()+".xls";
            }else{
                fileName = chooser.getSelectedFile().getName();
            }
            path = chooser.getSelectedFile().getParent();
//            System.out.println("path:"+path);
//            System.out.println("name:"+fileName);
//            System.out.println("文件路径"+path+"\\"+fileName);
        }
        return path+"\\"+fileName;
    }
    /**
     * 导出excel整理数据和设置格式（主方法）
     * @param headerDate Vector
     * @param date TParm
     * @param os OutputStream
     */

    public void exportToExcel(Vector headerDate, TParm date, OutputStream os) {

        //取得需要的数据
        Vector header = (Vector) headerDate.get(0);
        Vector colView = (Vector) headerDate.get(1);
        Vector dateType = (Vector) headerDate.get(2);
        //取得列数
        int colNum = header.size();

        //设置格式化列标题
        WritableFont fontHeader = new WritableFont(WritableFont.createFont(
            "楷体 _GB2312"), 12, WritableFont.BOLD);
        WritableCellFormat formatHeader = new WritableCellFormat(fontHeader);
        //设置格式化主数据
        WritableFont fontDate = new WritableFont(WritableFont.createFont(
            "楷体 _GB2312"), 10, WritableFont.NO_BOLD);
        //格式化一般的数据（靠左）
        WritableCellFormat formatDateLeft = new WritableCellFormat(fontDate);
        //格式化数字（靠右）
        WritableCellFormat formatDateRight = new WritableCellFormat(fontDate);

        try {
            //设定列标题居中
            formatHeader.setAlignment(jxl.format.Alignment.CENTRE);
            //设置自动换行
            formatHeader.setWrap(true);

            formatDateRight.setAlignment(jxl.format.Alignment.RIGHT);
            formatDateRight.setWrap(true);

            formatDateLeft.setAlignment(jxl.format.Alignment.LEFT);
            formatDateLeft.setWrap(true);
        }
        catch (WriteException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "转档失败");
        }
        try {
            //打开文件
            WritableWorkbook workbook = Workbook.createWorkbook(os);
            //生成名为“报 表”的工作表，参数0表示这是第一页（sheet的名字）
            WritableSheet sheet1 = workbook.createSheet("报  表", 0);
            //设置列宽
            for (int n = 0; n < colNum; n++) {
                int colViwe = Integer.parseInt( (String) colView.get(n));
                //循环设置列宽（是table上面长度的10分之一）
                sheet1.setColumnView(n, colViwe / 10);
            }

            //添加表头
            for (int i = 0; i < colNum; i++) {
                //添加表头的一行（标题）
                Label row1 = new Label(i, 0, (String) header.get(i),
                                       formatHeader);
                sheet1.addCell(row1);
            }

            //添加主数据
            int dateRow = date.getCount();
            //得到默认组的map对照表，通过它来依次拿数据（因为map的排序是不准的）
            String map = "";
            if(table != null){
            	map = (String)table.getParmMap();
            }else{
            	map = this.parmMap;
            }
            String[] title = map.split(";");
            //循环添加主数据
            for (int j = 0; j < dateRow; j++) { //行
                for (int n = 0; n < colNum; n++) { //列
                    //每一个格的数据
                    Label dateCell;
                    //得到该列的类型
                    String type = (String) dateType.get(n);
                    //如果是数字类型就靠右
                    if (type.equalsIgnoreCase("int") || type.equalsIgnoreCase("float") ||
                        type.equalsIgnoreCase("double") || type.equalsIgnoreCase("long")) {
                        //设置该数据靠右
                        dateCell = new Label(n, j + 1,
                                             date.getValue(title[n], j),
                                             formatDateRight);
                    }
                    else{
                        //设置该数据靠左
                        dateCell = new Label(n, j + 1,
                                             date.getValue(title[n], j),
                                             formatDateLeft);
                    }
                    sheet1.addCell(dateCell);
                }
            }

            workbook.write();
            workbook.close();
            JOptionPane.showMessageDialog(null, "转档成功");
        }
        catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "转档失败");
        }
    }

    /**
     * 整理table的标题头，使之只省下标题
     * @param date String 缺身省状态下的头
     * @return Vector
     */
    public Vector arrHeader(String date) {

        //把标题数据按；区间为字符数组
        String[] indate = date.split(";");
        Vector colAndType = getColumnView(indate);

        Vector result = new Vector();
        Vector header = new Vector();
        //循环去掉--> ，长度，类型等
        for (int j = 0; j < indate.length; j++) {
        	System.out.println(j+":"+indate[j]);
            //截取‘第一个’逗号前的文字--标题
            String a = indate[j].substring(0, (int) (indate[j].indexOf(",")));
            header.add(a);
        }
        //第一组是第一行的题目
        result.add(header);
        //第二组是列宽
        result.add(colAndType.get(0));
        //第三行是数据类型
        result.add(colAndType.get(1));

        return result;
    }

    public Vector getColumnView(String[] date) {

        Vector result = new Vector();
        //存储列宽和数据类型
        Vector colView = new Vector();
        Vector dateType = new Vector();
        //行数
        int col = date.length;
        for (int i = 0; i < col; i++) {
            //从第一个逗号后面一位开始
            int start = date[i].indexOf(",") + 1;
            int end;
            String type;
            //没有第二个逗号的时候说明没有数据类型
            if (date[i].indexOf(",", start) == -1) {
                //没有第二个逗号取到最后--长度
                end = date[i].length();
                //没有类型就默认为字符串
                type = "String";
            }
            else {
                //有就取到第二个逗号以前--长度
                end = date[i].indexOf(",", start);
                //取得省下的字符--类型
                type = date[i].substring(end + 1);
            }
            //存储该列的长度
            String view = date[i].substring(start, end);
            colView.add(view);
            dateType.add(type);

        }
        result.add(colView);
        result.add(dateType);
        return result;
    }


//-----------------------------导出EXCEL的方法---end-----------------------------

}
