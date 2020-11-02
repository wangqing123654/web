package com.javahis.ui.hrm;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.util.Vector;

import javax.swing.JLabel;

import jdo.bil.BILComparator;
import jdo.hrm.HRMOrder;
import jdo.hrm.HRMSchdayDr;
import jdo.sys.Operator;
import jdo.sys.PatTool;
import jdo.util.Manager;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TWord;
import com.dongyang.util.ImageTool;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.EmrUtil;
import com.javahis.util.StringUtil;
/**
 * <p>Title: ����������������</p>
 *
 * <p>Description: ����������������</p>
 *
 * <p>Copyright: javahis 20090922</p>
 *
 * <p>Company:JavaHis</p>
 *
 * @author ehui
 * @version 1.0
 */
public class HRMViewControl extends TControl {

    // TABLE
    private TTable table;
    // TWORD
    private TWord word;
    // �Ʊ�����
    private String deptAtt, caseNo;
    // ҽ������
    private HRMOrder order;
    // �ṹ����������·��
    private String[] saveFile;
    // �ṹ������·��
    private TParm pathParm, patParm;
    private BILComparator compare = new BILComparator();// add by wanglong 20150515
    private boolean ascending = false;
    private int sortColumn = -1;
    
    /**
     * ��ʼ���¼�
     */
    public void onInit() {
        super.onInit();
        // ��ʼ���ؼ�
        initComponent();
        // ��ʼ������
        initData();
    }

    /**
     * ��ʼ���ؼ�
     */
    private void initComponent() {
        table = (TTable) this.getComponent("TABLE");
        addSortListener(table);// add by wanglong 20130515
        word = (TWord) this.getComponent("WORD");
        // caowl 20130326 start ���ӳ�ʼ��ʱ��
        Timestamp now = TJDODBTool.getInstance().getDBTime();
        String date = StringTool.getString(now, "yyyyMMdd");
        this.setValue("START_DATE", StringTool.getTimestamp(date + "000000", "yyyyMMddHHmmss"));
        this.setValue("END_DATE", StringTool.getTimestamp(date + "235959", "yyyyMMddHHmmss"));
        // caowl 20130326 end
    }

    /**
     * ��ʼ������
     */
    private void initData() {
        order = new HRMOrder();
        // ȡ�õ�ǰ���ҡ�ҽʦ�ĿƱ�����
        deptAtt = HRMSchdayDr.getDeptAttribute();
        if (StringUtil.isNullString(deptAtt)) {
            this.messageBox_("ȡ�ÿƱ����Դ���");
            return;
        }
        this.setValue("DEPT_ATT", deptAtt);
        onClear();
    }

    /**
     * ����¼�
     */
    public void onClear() {
        Timestamp now = TJDODBTool.getInstance().getDBTime();
        String date = StringTool.getString(now, "yyyyMMdd");
        this.setValue("START_DATE", StringTool.getTimestamp(date + "000000", "yyyyMMddHHmmss"));
        this.setValue("END_DATE", StringTool.getTimestamp(date + "235959", "yyyyMMddHHmmss"));
        this.setValue("UNDONE", "Y");
        this.setValue("MR_NO", "");
        this.setValue("PAT_NAME", "");
        this.setValue("SEX_CODE", "");
        this.setValue("AGE", "");
        clearValue("UNDONE_NUM;DONE_NUM");// add by wanglong 20130328
        TPanel panel = (TPanel) this.getComponent("PIC");
        panel.removeAll();
        panel.repaint();
        table.removeRowAll();
        word.onNewFile();
        word.update();
        saveFile = new String[]{};
    }

    /**
     * ������ںͿƱ�����ʱ��ִ�в�ѯ
     */
    public void onDoQuery() {
        onQuery();
    }

    /**
     * ��ѯ
     */
    public void onQuery() {
        Timestamp now = (Timestamp) this.getValue("START_DATE");
        Timestamp tomorrow = (Timestamp) this.getValue("END_DATE");
        String startDate = StringTool.getString(now, "yyyyMMdd") + "000000";
        String endDate = StringTool.getString(tomorrow, "yyyyMMdd") + "235959";
        // ȡ�õ�ǰҽʦӦ������Ĳ����б�
        TParm patParm = order.onQueryByDeptAttribute(this.getValueString("DEPT_ATT"), TypeTool.getBoolean(this.getValue("DONE")), startDate, endDate);
        TParm patParm1 = order.onQueryByDeptAttribute(this.getValueString("DEPT_ATT"), !TypeTool.getBoolean(this.getValue("DONE")), startDate, endDate);
        if (TypeTool.getBoolean(this.getValue("DONE"))) {
            int countUnDone = patParm1.getCount();
            int countDone = patParm.getCount();
            if (countUnDone <= 0) {
                countUnDone = 0;
            }
            if (countDone <= 0) {
                countDone = 0;
            }
            this.setValue("UNDONE_NUM", countUnDone + "��");
            this.setValue("DONE_NUM", countDone + "��");
        } else {
            int countUnDone = patParm.getCount();
            int countDone = patParm1.getCount();
            if (countUnDone <= 0) {
                countUnDone = 0;
            }
            if (countDone <= 0) {
                countDone = 0;
            }
            this.setValue("UNDONE_NUM", countUnDone + "��");
            this.setValue("DONE_NUM", countDone + "��");
        }
        table.setParmValue(patParm);
    }

    /**
     * TABLE˫���¼���ѡ�񲡻���ҽ��
     */
    public void onChoosePat() {
        TParm parm = table.getParmValue();
        int row = table.getSelectedRow();
        if (row < 0) {
            return;
        }
        if (parm == null) {
            return;
        }
        String mrNo = parm.getValue("MR_NO", row);
        caseNo = parm.getValue("CASE_NO", row);
        // System.out.println("onChoosePat.caseNo============"+caseNo);
        if (StringUtil.isNullString(mrNo)) {
            return;
        }
        patParm = parm.getRow(row);
        this.setValue("MR_NO", mrNo);
        this.setValue("PAT_NAME", parm.getValue("PAT_NAME", row));
        this.setValue("SEX_CODE", parm.getValue("SEX_CODE", row));
        if (!StringUtil.isNullString(parm.getValue("BIRTHDAY", row))) {
            this.setValue("AGE", StringUtil.showAge(parm.getTimestamp("BIRTHDAY", row), TJDODBTool.getInstance().getDBTime()));
        }
        viewPhoto(mrNo);
        // ���ô򿪽ṹ�������Ĳ������򿪽ṹ������������
        order.filt(caseNo);
        String tempName = parm.getValue("MR_CODE", row);
        TParm emrParm = new TParm();
        emrParm.setData("MR_CODE", tempName);
        emrParm.setData("CASE_NO", caseNo);
        emrParm = EmrUtil.getInstance().getEmrFilePath(emrParm);
        emrParm.setData("FILE_TITLE_TEXT", "TEXT", Manager.getOrganization().getHospitalCHNFullName(Operator.getRegion()));
        emrParm.setData("FILE_TITLEENG_TEXT", "TEXT", Manager.getOrganization().getHospitalENGFullName(Operator.getRegion()));
        emrParm.setData("FILE_HEAD_TITLE_MR_NO", "TEXT", this.getValueString("MR_NO"));
        emrParm.setData("FILE_HEAD_TITLE_IPD_NO", "TEXT", "");
        emrParm.setData("FILE_128CODE", "TEXT", this.getValueString("MR_NO"));
        // ============xueyf modify 20120223 start
        // emrParm.setData("TEMPLET_PATH","JHW\\"+emrParm.getValue("TEMPLET_PATH"));
        emrParm.setData("TEMPLET_PATH", emrParm.getValue("TEMPLET_PATH"));
        // ============xueyf modify 20120223 stop
        pathParm = emrParm;
        // System.out.println("emrParm="+emrParm);
        if (TypeTool.getBoolean(this.getValue("UNDONE"))) {
            word.onOpen(emrParm.getValue("TEMPLET_PATH"), emrParm.getValue("EMT_FILENAME"), 2, false);
            emrParm.addListener("EMR_SAVE_LISTENER", this, "emrSaveListener");
            word.setWordParameter(emrParm);
            word.setCanEdit(true);
            saveFile = new String[2];
        } else {
            saveFile = new String[2];
            saveFile[0] = emrParm.getValue("FILE_PATH").indexOf("JHW") < 0 ? "JHW\\" + emrParm.getValue("FILE_PATH") : emrParm.getValue("FILE_PATH");
            saveFile[1] = emrParm.getValue("FILE_NAME");
            word.onOpen(saveFile[0], saveFile[1], 3, false);
            emrParm.addListener("EMR_SAVE_LISTENER", this, "emrSaveListener");
            word.setWordParameter(emrParm);
            word.setCanEdit(true);
        }
    }

    /**
     * ��ʾphoto
     */
    public void viewPhoto(String mrNo) {
        String photoName = mrNo + ".jpg";
        String fileName = photoName;
        try {
            TPanel viewPanel = (TPanel) getComponent("PIC");
            String root = TIOM_FileServer.getRoot();
            String dir = TIOM_FileServer.getPath("PatInfPIC.ServerPath");
            //�����Ŵ��ڵ���10λ����
            if(mrNo.length()>=10){
            	dir = root + dir + mrNo.substring(0, 3) + "\\"
                + mrNo.substring(3, 6) + "\\" + mrNo.substring(6, 9) + "\\";
            //������С��10λ����
            }else{
            	dir = root + dir + mrNo.substring(0, 2) + "\\"
                + mrNo.substring(2, 4) + "\\" + mrNo.substring(4, 7) + "\\";
            }
            //
            byte[] data = TIOM_FileServer.readFile(TIOM_FileServer.getSocket(), dir + fileName);
            if (data == null) return;
            double scale = 0.2;
            boolean flag = true;
            Image image = ImageTool.scale(data, scale, flag);
            // Image image = ImageTool.getImage(data);
            Pic pic = new Pic(image);
            pic.setSize(viewPanel.getWidth() - 2, viewPanel.getHeight() - 2);
            pic.setLocation(0, 0);
            viewPanel.removeAll();
            viewPanel.add(pic);
            pic.repaint();
        }
        catch (Exception e) {}
    }

    /**
     * ����
     */
    public void onSave() {
        if (StringUtil.isNullString(saveFile[0])) {
            pathParm.setData("CASE_NO", patParm.getValue("CASE_NO"));
            pathParm.setData("MR_NO", patParm.getValue("MR_NO"));
            TParm parm = EmrUtil.getInstance().getFileServerEmrName(pathParm);
            parm.setData("OPT_USER", Operator.getID());
            parm.setData("OPT_DATE", TJDODBTool.getInstance().getDBTime());
            parm.setData("OPT_TERM", Operator.getIP());
            // =============xueyf modify 20120212 start
            parm.setData("REPORT_FLG", "N");
            // =============xueyf modify 20120212 stop
            parm.setData("CREATOR_USER", Operator.getID());
            parm.setData("CURRENT_USER", Operator.getID());
            // System.out.println("parm====="+parm);
            TParm result = TIOM_AppServer.executeAction("action.odi.ODIAction", "saveNewEmrFile", parm);
            if (result.getErrCode() != 0) {
                // System.out.println("errText="+result.getErrText());
                this.messageBox("E0002");
                return;
            }
			result=order.saveByCheck(Operator.getRegion(), Operator.getDept(), caseNo, this.getValueString("DEPT_ATT"), parm.getValue("FILE_SEQ"), true);
            if (result.getErrCode() != 0) {
                this.messageBox("E0002");
                return;
            }
            // ============xueyf modify 20120223 start
            parm.setData("FILE_PATH", parm.getValue("FILE_PATH").indexOf("JHW") < 0 ? "JHW\\"
                    + parm.getValue("FILE_PATH") : parm.getValue("FILE_PATH"));
            // ============xueyf modify 20120223 stop
            word.onSaveAs(parm.getValue("FILE_PATH"), parm.getValue("FILE_NAME"), 3);
        } else {
            pathParm.setData("CASE_NO", patParm.getValue("CASE_NO"));
            pathParm.setData("MR_NO", patParm.getValue("MR_NO"));
            TParm parm = EmrUtil.getInstance().getFileServerEmrName(pathParm);
            parm.setData("OPT_USER", Operator.getID());
            parm.setData("OPT_DATE", TJDODBTool.getInstance().getDBTime());
            parm.setData("OPT_TERM", Operator.getIP());
            pathParm.setData("CREATOR_USER", Operator.getID());
            pathParm.setData("CURRENT_USER", Operator.getID());
            TParm result = TIOM_AppServer.executeAction("action.odi.ODIAction", "updateEmrFile", parm);
            if (result.getErrCode() != 0) {
                // System.out.println("errText="+result.getErrText());
                this.messageBox("E0001");
                return;
            }
            result = order.saveByCheck(Operator.getRegion(), Operator.getDept(), caseNo, this.getValueString("DEPT_ATT"), "", false);
            if (result.getErrCode() != 0) {
                this.messageBox("E0001");
                return;
            }
            word.onSave();
        }
        TPanel panel = (TPanel) this.getComponent("PIC");
        panel.removeAll();
        panel.repaint();
        table.removeRowAll();
        word.onNewFile();
        word.update();
        saveFile = new String[]{};
        onQuery();
    }

    /**
     * ����ѡ��MR_NO��ѯ������ҽ��
     */
    public void onMrNo() {
        if (StringUtil.isNullString(this.getValueString("DEPT_ATT"))) {
            this.messageBox("��ѡ��Ʊ�����");
            return;
        }
        if ("N".equals(this.getValueString("DONE")) && "N".equals(this.getValueString("UNDONE"))) {
            this.messageBox("��ѡ��״̬");
            return;
        }
        String mrNo = this.getValueString("MR_NO");
        if (StringUtil.isNullString(mrNo)) {
            return;
        }
        mrNo = StringTool.fill0(mrNo, PatTool.getInstance().getMrNoLength()); // ========= chenxi
        this.setValue("MR_NO", mrNo);
        // ============================caowl 20130326 start �����Ų�ѯ��ʱ�򲻿�����
        // onQuery();
        String startDate = "";
        String endDate = "";
        // ȡ�õ�ǰҽʦӦ������Ĳ����б�
        TParm patParm =
                order.onQueryByDeptAttribute(this.getValueString("DEPT_ATT"), mrNo,
                                               TypeTool.getBoolean(this.getValue("DONE")),
                                               startDate, endDate);//modify by wanglong 20130515
        TParm patParm1 =
                order.onQueryByDeptAttribute(this.getValueString("DEPT_ATT"), mrNo,
                                               !TypeTool.getBoolean(this.getValue("DONE")),
                                               startDate, endDate);
        if (patParm.getErrCode() != 0 || patParm1.getErrCode() != 0) {
            this.messageBox("��ѯʧ��");
            this.setValue("UNDONE_NUM", "");
            this.setValue("DONE_NUM", "");
            table.setParmValue(new TParm());
            return;
        }
        if ((patParm == null || patParm.getCount() <= 0)
                && (patParm1 == null || patParm1.getCount() <= 0)) {
            this.messageBox("�����ݣ�");
            this.setValue("UNDONE_NUM", "");
            this.setValue("DONE_NUM", "");
            table.setParmValue(new TParm());
            return;
        }
        if (TypeTool.getBoolean(this.getValue("DONE"))) {// �����
            int countUnDone = patParm1.getCount();
            int countDone = patParm.getCount();
            if (countUnDone <= 0) {
                countUnDone = 0;
            }
            if (countDone <= 0) {
                countDone = 0;
            }
            this.setValue("UNDONE_NUM", countUnDone + "��");
            this.setValue("DONE_NUM", countDone + "��");
            if (countDone <= 0) {
                table.setParmValue(patParm1);
                this.setValue("UNDONE", "Y");
            } else {
                table.setParmValue(patParm);
            }
        } else {// δ���
            int countUnDone = patParm.getCount();
            int countDone = patParm1.getCount();
            if (countUnDone <= 0) {
                countUnDone = 0;
            }
            if (countDone <= 0) {
                countDone = 0;
            }
            this.setValue("UNDONE_NUM", countUnDone + "��");
            this.setValue("DONE_NUM", countDone + "��");
            if (countUnDone <= 0) {
                table.setParmValue(patParm1);
                this.setValue("DONE", "Y");
            } else {
                table.setParmValue(patParm);
            }
        }
        table.setSelectedRow(0);
        onChoosePat();
    }

    /**
     * ��ӡ�¼�
     */
    public void onPrint() {
        if (word == null) {
            return;
        }
        if (StringUtil.isNullString(word.getFileName())) {
            return;
        }
        word.onPreviewWord();
        word.print();
    }

    class Pic extends JLabel {

        Image image;

        public Pic(Image image) {
            this.image = image;
        }

        public void paint(Graphics g) {
            g.setColor(new Color(161, 220, 230));
            g.fillRect(4, 15, 100, 100);
            if (image != null) {
                g.drawImage(image, 4, 15, null);
            }
        }
    }
    
    // ====================������begin======================add by wanglong 20130515
    /**
     * �����������������
     * @param table
     */
    public void addSortListener(final TTable table) {
        table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent mouseevent) {
                int i = table.getTable().columnAtPoint(mouseevent.getPoint());
                int j = table.getTable().convertColumnIndexToModel(i);
                if (j == sortColumn) {
                    ascending = !ascending;// �����ͬ�У���ת����
                } else {
                    ascending = true;
                    sortColumn = j;
                }
                TParm tableData = table.getParmValue();// ȡ�ñ��е�����
                String columnName[] = tableData.getNames("Data");// �������
                String strNames = "";
                for (String tmp : columnName) {
                    strNames += tmp + ";";
                }
                strNames = strNames.substring(0, strNames.length() - 1);
                Vector vct = getVector(tableData, "Data", strNames, 0);
                String tblColumnName = table.getParmMap(sortColumn); // ������������;
                int col = tranParmColIndex(columnName, tblColumnName); // ����ת��parm�е�������
                compare.setDes(ascending);
                compare.setCol(col);
                java.util.Collections.sort(vct, compare);
                // ��������vectorת��parm;
                cloneVectoryParam(vct, new TParm(), strNames, table);
            }
        });
    }

    /**
     * �����������ݣ���TParmתΪVector
     * @param parm
     * @param group
     * @param names
     * @param size
     * @return
     */
    private Vector getVector(TParm parm, String group, String names, int size) {
        Vector data = new Vector();
        String nameArray[] = StringTool.parseLine(names, ";");
        if (nameArray.length == 0) {
            return data;
        }
        int count = parm.getCount(group, nameArray[0]);
        if (size > 0 && count > size)
            count = size;
        for (int i = 0; i < count; i++) {
            Vector row = new Vector();
            for (int j = 0; j < nameArray.length; j++) {
                row.add(parm.getData(group, nameArray[j], i));
            }
            data.add(row);
        }
        return data;
    }

    /**
     * ����ָ���������������е�index
     * @param columnName
     * @param tblColumnName
     * @return int
     */
    private int tranParmColIndex(String columnName[], String tblColumnName) {
        int index = 0;
        for (String tmp : columnName) {
            if (tmp.equalsIgnoreCase(tblColumnName)) {
                return index;
            }
            index++;
        }
        return index;
    }

    /**
     * �����������ݣ���Vectorת��Parm
     * @param vectorTable
     * @param parmTable
     * @param columnNames
     * @param table
     */
    private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
            String columnNames, final TTable table) {
        String nameArray[] = StringTool.parseLine(columnNames, ";");
        for (Object row : vectorTable) {
            int rowsCount = ((Vector) row).size();
            for (int i = 0; i < rowsCount; i++) {
                Object data = ((Vector) row).get(i);
                parmTable.addData(nameArray[i], data);
            }
        }
        parmTable.setCount(vectorTable.size());
        table.setParmValue(parmTable);
    }
    // ====================������end======================
}
