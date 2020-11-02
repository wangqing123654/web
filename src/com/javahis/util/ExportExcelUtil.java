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
     * ����EXCEL�ķ����Ľӿ�
     * @param table TTable table����
     * @param defName String Ĭ���ļ�������
     */
    public void exportExcel(TTable mainTable, String defName) {
        table=mainTable;
        //�õ�talble��ͷ
        String header = mainTable.getHeader();
        //ֻ�õ�table����ʾ�����ݣ�PS���������ݵò�����
        TParm mainDate = mainTable.getShowParmValue();
        if (mainDate.getCount() <= 0) {
            this.messageBox("�޵���EXCEL���ݣ�");
            return;
        }
        //���õ���excel����������ͷ�������ݣ��ļ����֣�
        exeSaveExcel(header, mainDate, defName);

    }
    /**
     * ����EXCEL�ķ����Ľӿ�
     * @param table TTable table����
     * @param defName String Ĭ���ļ�������
     */
    public void exportExcel(String header,String parmMap,TParm parm, String defName) {
        //table=mainTable;
        this.parmMap = parmMap;
        //�õ�talble��ͷ
        /*String header = mainTable.getHeader();
        if (parm.getCount() <= 0) {
            this.messageBox("�޵���EXCEL���ݣ�");
            return;
        }*/
        //���õ���excel����������ͷ�������ݣ��ļ����֣�
        exeSaveExcel(header, parm, defName);

    }
    /**
     * ����excel�ļ�
     * @param headerDate String
     * @param mainDate TParm
     * @param fileDefName Ĭ���ļ�����
     */

    public void exeSaveExcel(String headerDate, TParm mainDate,String fileDefName) {
        //�����������ͷ���ݣ�ֻ���±��⣩
        Vector arrHeader = arrHeader(headerDate);
        //��׼SWING
        JFileChooser chooser = new JFileChooser();
        //���õ�ǰ��Ŀ¼��Ŀǰд����
        File dir= new File("C:\\JavaHis\\Excel");
        if(!dir.exists())
            dir.mkdirs();
        chooser.setCurrentDirectory(dir);
        //���õ�ǰʱ�丽�ӵ�Ĭ���ļ�����
        Timestamp optTime = SystemTool.getInstance().getDate();
        fileDefName=fileDefName+StringTool.getString(optTime,"yyyyMMddHHmmss");
        //Ĭ�ϵ��ļ���������
        chooser.setSelectedFile(new File(fileDefName));//�ṩĬ����
        //���öԻ���ı���
        chooser.setDialogTitle("����EXCEL����");
        //���ù��ˣ���չ����
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
     * ����EXCEL�ķ����Ľӿڣ��ļ����󲻴�ʱ�䣩
     * @param table TTable table����
     * @param defName String Ĭ���ļ�������
     */
    public void exportExcelWithoutTime(TTable mainTable, String defName) {// add by wanglong 20130419
        table = mainTable;
        // �õ�talble��ͷ
        String header = mainTable.getHeader();
        //ֻ�õ�table����ʾ�����ݣ�PS���������ݵò�����
        TParm mainDate = mainTable.getShowParmValue();
        if (mainDate.getCount() <= 0) {
            this.messageBox("�޵���EXCEL���ݣ�");
            return;
        }
        //���õ���excel����������ͷ�������ݣ��ļ����֣�
        exeSaveExcel(header, mainDate, defName, false);
    }

    /**
     * ����excel�ļ�(����ѡ���ļ����Ƿ��ʱ��)
     * @param headerDate String
     * @param mainDate TParm
     * @param fileDefName Ĭ���ļ�����
     */

    public void exeSaveExcel(String headerDate, TParm mainDate, String fileDefName, boolean withTime) {// add by wanglong 20130419
        // �����������ͷ���ݣ�ֻ���±��⣩
        Vector arrHeader = arrHeader(headerDate);
        // ��׼SWING
        JFileChooser chooser = new JFileChooser();
        //���õ�ǰ��Ŀ¼��Ŀǰд����
        File dir= new File("C:\\JavaHis\\Excel");
        if(!dir.exists())
            dir.mkdirs();
        chooser.setCurrentDirectory(dir);
        //���õ�ǰʱ�丽�ӵ�Ĭ���ļ�����
        Timestamp optTime = SystemTool.getInstance().getDate();
        if (withTime) {
            fileDefName = fileDefName + StringTool.getString(optTime, "yyyyMMddHHmmss");
        }
        //Ĭ�ϵ��ļ���������
        chooser.setSelectedFile(new File(fileDefName));//�ṩĬ����
        //���öԻ���ı���
        chooser.setDialogTitle("����EXCEL����");
        //���ù��ˣ���չ����
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
     * ����excel�ļ�
     * @param headerDate String
     * @param mainDate TParm
     * @param fileDefName Ĭ���ļ�����
     */

    public void exeSaveExcel(TParm[] mainDate,String fileDefName) {
        //�����������ͷ���ݣ�ֻ���±��⣩
        int mainDCount =  mainDate.length;
        for(int i=0;i<mainDCount;i++){
//            System.out.println("�����������ͷ����==="+mainDate[i].getValue("HEAD"));
//            System.out.println("����==="+mainDate[i]);
            mainDate[i].setData("HEAD",arrHeader(mainDate[i].getValue("HEAD")));
        }
        //��׼SWING
        JFileChooser chooser = new JFileChooser();
        //���õ�ǰ��Ŀ¼��Ŀǰд����
        File dir= new File("C:\\JavaHis\\Excel");
        if(!dir.exists())
            dir.mkdirs();
        chooser.setCurrentDirectory(dir);
        //���õ�ǰʱ�丽�ӵ�Ĭ���ļ�����
        Timestamp optTime = SystemTool.getInstance().getDate();
        fileDefName=fileDefName+StringTool.getString(optTime,"yyyyMMddHHmmss");
        //Ĭ�ϵ��ļ���������
        chooser.setSelectedFile(new File(fileDefName));//�ṩĬ����
        //���öԻ���ı���
        chooser.setDialogTitle("����EXCEL����");
        //���ù��ˣ���չ����
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
     * �򿪱���Ի���ѡ��·��
     * @param header Vector
     * @param date TParm
     * @param FileName String
     */
    public void writeFile(Vector header, TParm date, String FileName) {
        //����һ���ļ�����
        File file;
        try {
            //��·���а���.xls�Ͳ��Ӻ�׺
            if (!FileName.contains(".xls")) {
                file = new File(FileName + ".xls");
            }
            else
                file = new File(FileName);
            //�жϸ��ļ��Ƿ���ڣ�����Ѿ�������ʾ�Ƿ񸲸ǣ������Ǿ��˳�������
            if (file.exists() &&
                JOptionPane.showConfirmDialog(null, "save", "���ļ��Ѿ�����,�Ƿ񸲸ǣ�",
                                              JOptionPane.YES_NO_OPTION) == 1)
                return;
            //���ļ���������
            FileOutputStream fileOutStream = null;
            fileOutStream = new FileOutputStream(file);
            //ִ��������������
            exportToExcel(header, date, fileOutStream);
            fileOutStream.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "ת��ʧ��");
        }
    }
    /**
     * �򿪱���Ի���ѡ��·��
     * @param header Vector
     * @param date TParm
     * @param FileName String
     */
    public void writeFile(TParm[] date, String FileName) {
        //����һ���ļ�����
        File file;
        try {
            //��·���а���.xls�Ͳ��Ӻ�׺
            if (!FileName.contains(".xls")) {
                file = new File(FileName + ".xls");
            }
            else
                file = new File(FileName);
            //�жϸ��ļ��Ƿ���ڣ�����Ѿ�������ʾ�Ƿ񸲸ǣ������Ǿ��˳�������
            if (file.exists() &&
                JOptionPane.showConfirmDialog(null, "save", "���ļ��Ѿ�����,�Ƿ񸲸ǣ�",
                                              JOptionPane.YES_NO_OPTION) == 1)
                return;
            //���ļ���������
            FileOutputStream fileOutStream = null;
            fileOutStream = new FileOutputStream(file);
            //ִ��������������
            exportToExcel(date, fileOutStream);
            fileOutStream.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "ת��ʧ��");
        }
    }

    /**
     * ����excel�������ݺ����ø�ʽ����������
     * @param headerDate Vector
     * @param date TParm
     * @param os OutputStream
     */

    public void exportToExcel(TParm[] date, OutputStream os) {

        int headCount = date.length;
        int err=0;
        //���ļ�
        WritableWorkbook workbook = null;
        try {
            workbook = Workbook.createWorkbook(os);
        }
        catch (IOException ex1) {
            ex1.fillInStackTrace();
        }
        for (int w = 0; w < headCount; w++) {
            Vector temp = date[w].getVector("HEAD");
            //ȡ����Ҫ������
            Vector header = (Vector)((Vector) temp.get(0)).get(0);
            Vector colView = (Vector)((Vector) temp.get(1)).get(0);
            Vector dateType = (Vector)((Vector) temp.get(2)).get(0);
            //ȡ������
            int colNum = header.size();
            //���ø�ʽ���б���
            WritableFont fontHeader = new WritableFont(WritableFont.createFont(
                "���� _GB2312"), 12, WritableFont.BOLD);
            WritableCellFormat formatHeader = new WritableCellFormat(fontHeader);
            //���ø�ʽ��������
            WritableFont fontDate = new WritableFont(WritableFont.createFont(
                "���� _GB2312"), 10, WritableFont.NO_BOLD);
            //��ʽ��һ������ݣ�����
            WritableCellFormat formatDateLeft = new WritableCellFormat(fontDate);
            //��ʽ�����֣����ң�
            WritableCellFormat formatDateRight = new WritableCellFormat(
                fontDate);

            try {
                //�趨�б������
                formatHeader.setAlignment(jxl.format.Alignment.CENTRE);
                //�����Զ�����
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

                //������Ϊ���� ���Ĺ���������0��ʾ���ǵ�һҳ��sheet�����֣�
                WritableSheet sheet1 = workbook.createSheet(date[w].getValue("TITLE"), w);
//                System.out.println("www========="+w+"===="+date[w].getValue("TITLE"));
                //�����п�
                for (int n = 0; n < colNum; n++) {
                    int colViwe = Integer.parseInt( (String) colView.get(n));
                    //ѭ�������п���table���泤�ȵ�10��֮һ��
                    sheet1.setColumnView(n, colViwe / 10);
                }

                //��ӱ�ͷ
                for (int i = 0; i < colNum; i++) {
                    //��ӱ�ͷ��һ�У����⣩
                    Label row1 = new Label(i, 0, (String) header.get(i),
                                           formatHeader);
                    sheet1.addCell(row1);
                }

                //���������
                int dateRow = date[w].getCount();
                //�õ�Ĭ�����map���ձ�ͨ���������������ݣ���Ϊmap�������ǲ�׼�ģ�
//                String[] title = date[w].getNames();
//                date[w].getData("SYSTEM","COLUMNS");
                Vector columns = (Vector)date[w].getData("SYSTEM","COLUMNS");
                //ѭ�����������
                for (int j = 0; j < dateRow; j++) { //��
                    for (int n = 0; n < colNum; n++) { //��
                        //ÿһ���������
                        Label dateCell;
                        //�õ����е�����
                        String type = (String) dateType.get(n);
                        //������������;Ϳ���
                        if (type.equalsIgnoreCase("int") ||
                            type.equalsIgnoreCase("float") ||
                            type.equalsIgnoreCase("double") ||
                            type.equalsIgnoreCase("long")) {
                            //���ø����ݿ���
                            dateCell = new Label(n, j + 1,
                                                 date[w].getValue(columns.get(n).toString(), j),
                                                 formatDateRight);
                        }
                        else {
                            //���ø����ݿ���
//                            System.out.println("Excel��������"+date[w]);
//                            System.out.println("����=="+columns.get(n).toString());
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
            JOptionPane.showMessageDialog(null, "ת��ʧ��");
        else
            JOptionPane.showMessageDialog(null, "ת���ɹ�");
    }
    /**
     * дExcel
     * @param title String
     * @param head String[]
     * @param os OutputStream
     * @param parm TParm
     * @throws Exception
     */
    public  void writeExcel(String title,String[] head, OutputStream os,TParm parm) throws Exception {
    //������
    int columnNum = head.length;
    int rowCount = parm.getInt("ACTION","COUNT");
    if(columnNum==0||rowCount==0){
        JOptionPane.showMessageDialog(null, "�޵������ݣ�");
    }
    //��ӱ���
    WritableWorkbook titleObj = Workbook.createWorkbook(os);
    WritableSheet sheetName = titleObj.createSheet(title,0);
    //�����
    for(int i=0;i<columnNum;i++){
        WritableFont columnName = new WritableFont(WritableFont.createFont(
            "����"),10,WritableFont.BOLD,false,UnderlineStyle.NO_UNDERLINE, jxl.format.Colour.BLACK);
       WritableCellFormat columnFormat = new WritableCellFormat(columnName);
       jxl.write.Label column = new Label(i,0,head[i].split(":")[0],columnFormat);
       sheetName.addCell(column);
    }
    for(String temp:head){
//        System.out.println("����"+temp);
    }
    //�����
    for (int j = 0; j < rowCount; j++) {
//        System.out.println("һ������"+parm.getRow(j));
        for(int i=0;i<head.length;i++){
            jxl.write.Label rowData = new jxl.write.Label(i, j+1, parm.getData(head[i].split(":")[1],j).toString());
            sheetName.addCell(rowData);
        }
    }
    try{
        //д��Exel������
        titleObj.write();
        //�ر�Excel����������
        titleObj.close();
        os.flush();
        os.close();
        JOptionPane.showMessageDialog(null, "�浵�ɹ���");
    }catch(Exception ex){
        JOptionPane.showMessageDialog(null, "�浵ʧ�ܣ�");
    }
    }
    /**
     * ����Excel
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
//        System.out.println("·��"+path);
        if(!path.contains(".xls"))
            return false;
        if (dirSel.exists() &&JOptionPane.showConfirmDialog(null, "���ļ��Ѿ�����,�Ƿ񸲸ǣ�", "����",JOptionPane.YES_NO_OPTION) == 1)
            return false;
        dirSel.createNewFile();
        writeExcel(title,head,new FileOutputStream(dirSel),parm);
        return true;
    }
    /**
     * ѡ��·��
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
        chooser.setSelectedFile(new File("δ����"));
        chooser.setDialogTitle("����EXCEL����");
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
//            System.out.println("�ļ�·��"+path+"\\"+fileName);
        }
        return path+"\\"+fileName;
    }
    /**
     * ����excel�������ݺ����ø�ʽ����������
     * @param headerDate Vector
     * @param date TParm
     * @param os OutputStream
     */

    public void exportToExcel(Vector headerDate, TParm date, OutputStream os) {

        //ȡ����Ҫ������
        Vector header = (Vector) headerDate.get(0);
        Vector colView = (Vector) headerDate.get(1);
        Vector dateType = (Vector) headerDate.get(2);
        //ȡ������
        int colNum = header.size();

        //���ø�ʽ���б���
        WritableFont fontHeader = new WritableFont(WritableFont.createFont(
            "���� _GB2312"), 12, WritableFont.BOLD);
        WritableCellFormat formatHeader = new WritableCellFormat(fontHeader);
        //���ø�ʽ��������
        WritableFont fontDate = new WritableFont(WritableFont.createFont(
            "���� _GB2312"), 10, WritableFont.NO_BOLD);
        //��ʽ��һ������ݣ�����
        WritableCellFormat formatDateLeft = new WritableCellFormat(fontDate);
        //��ʽ�����֣����ң�
        WritableCellFormat formatDateRight = new WritableCellFormat(fontDate);

        try {
            //�趨�б������
            formatHeader.setAlignment(jxl.format.Alignment.CENTRE);
            //�����Զ�����
            formatHeader.setWrap(true);

            formatDateRight.setAlignment(jxl.format.Alignment.RIGHT);
            formatDateRight.setWrap(true);

            formatDateLeft.setAlignment(jxl.format.Alignment.LEFT);
            formatDateLeft.setWrap(true);
        }
        catch (WriteException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "ת��ʧ��");
        }
        try {
            //���ļ�
            WritableWorkbook workbook = Workbook.createWorkbook(os);
            //������Ϊ���� ���Ĺ���������0��ʾ���ǵ�һҳ��sheet�����֣�
            WritableSheet sheet1 = workbook.createSheet("��  ��", 0);
            //�����п�
            for (int n = 0; n < colNum; n++) {
                int colViwe = Integer.parseInt( (String) colView.get(n));
                //ѭ�������п���table���泤�ȵ�10��֮һ��
                sheet1.setColumnView(n, colViwe / 10);
            }

            //��ӱ�ͷ
            for (int i = 0; i < colNum; i++) {
                //��ӱ�ͷ��һ�У����⣩
                Label row1 = new Label(i, 0, (String) header.get(i),
                                       formatHeader);
                sheet1.addCell(row1);
            }

            //���������
            int dateRow = date.getCount();
            //�õ�Ĭ�����map���ձ�ͨ���������������ݣ���Ϊmap�������ǲ�׼�ģ�
            String map = "";
            if(table != null){
            	map = (String)table.getParmMap();
            }else{
            	map = this.parmMap;
            }
            String[] title = map.split(";");
            //ѭ�����������
            for (int j = 0; j < dateRow; j++) { //��
                for (int n = 0; n < colNum; n++) { //��
                    //ÿһ���������
                    Label dateCell;
                    //�õ����е�����
                    String type = (String) dateType.get(n);
                    //������������;Ϳ���
                    if (type.equalsIgnoreCase("int") || type.equalsIgnoreCase("float") ||
                        type.equalsIgnoreCase("double") || type.equalsIgnoreCase("long")) {
                        //���ø����ݿ���
                        dateCell = new Label(n, j + 1,
                                             date.getValue(title[n], j),
                                             formatDateRight);
                    }
                    else{
                        //���ø����ݿ���
                        dateCell = new Label(n, j + 1,
                                             date.getValue(title[n], j),
                                             formatDateLeft);
                    }
                    sheet1.addCell(dateCell);
                }
            }

            workbook.write();
            workbook.close();
            JOptionPane.showMessageDialog(null, "ת���ɹ�");
        }
        catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "ת��ʧ��");
        }
    }

    /**
     * ����table�ı���ͷ��ʹֻ֮ʡ�±���
     * @param date String ȱ��ʡ״̬�µ�ͷ
     * @return Vector
     */
    public Vector arrHeader(String date) {

        //�ѱ������ݰ�������Ϊ�ַ�����
        String[] indate = date.split(";");
        Vector colAndType = getColumnView(indate);

        Vector result = new Vector();
        Vector header = new Vector();
        //ѭ��ȥ��--> �����ȣ����͵�
        for (int j = 0; j < indate.length; j++) {
        	System.out.println(j+":"+indate[j]);
            //��ȡ����һ��������ǰ������--����
            String a = indate[j].substring(0, (int) (indate[j].indexOf(",")));
            header.add(a);
        }
        //��һ���ǵ�һ�е���Ŀ
        result.add(header);
        //�ڶ������п�
        result.add(colAndType.get(0));
        //����������������
        result.add(colAndType.get(1));

        return result;
    }

    public Vector getColumnView(String[] date) {

        Vector result = new Vector();
        //�洢�п����������
        Vector colView = new Vector();
        Vector dateType = new Vector();
        //����
        int col = date.length;
        for (int i = 0; i < col; i++) {
            //�ӵ�һ�����ź���һλ��ʼ
            int start = date[i].indexOf(",") + 1;
            int end;
            String type;
            //û�еڶ������ŵ�ʱ��˵��û����������
            if (date[i].indexOf(",", start) == -1) {
                //û�еڶ�������ȡ�����--����
                end = date[i].length();
                //û�����;�Ĭ��Ϊ�ַ���
                type = "String";
            }
            else {
                //�о�ȡ���ڶ���������ǰ--����
                end = date[i].indexOf(",", start);
                //ȡ��ʡ�µ��ַ�--����
                type = date[i].substring(end + 1);
            }
            //�洢���еĳ���
            String view = date[i].substring(start, end);
            colView.add(view);
            dateType.add(type);

        }
        result.add(colView);
        result.add(dateType);
        return result;
    }


//-----------------------------����EXCEL�ķ���---end-----------------------------

}
