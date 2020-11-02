package jdo.clp;


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
import com.dongyang.ui.TTable;
import com.dongyang.data.TParm;
import com.dongyang.control.TControl;

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

public class ClpExportExcleTool extends TControl{

    private static final ClpExportExcleTool INSTANCE =new ClpExportExcleTool();

    private ClpExportExcleTool() {
    }

    public static ClpExportExcleTool getInstance(){

        return INSTANCE;
    }

//    private TTable table;
    /**
     * ����EXCEL�ķ����Ľӿ�
     * @param table TTable table����
     * @param defName String Ĭ���ļ�������
     */
//    public void exportExcel(TTable mainTable, String defName) {
//        table=mainTable;
//        //�õ�talble��ͷ
//        String header = mainTable.getHeader();
//        //ֻ�õ�table����ʾ�����ݣ�PS���������ݵò�����
//        TParm mainDate = mainTable.getShowParmValue();
//        if (mainDate.getCount() <= 0) {
//            this.messageBox("�޵���EXCEL���ݣ�");
//            return;
//        }
//        //���õ���excel����������ͷ�������ݣ��ļ����֣�
//        exeSaveExcel(header, mainDate, defName);
//
//    }

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
        fileDefName=fileDefName;
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
//            String map = (String)table.getParmMap();
            String map = "A;B";
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

//                    this.messageBox_(dateCell);
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
