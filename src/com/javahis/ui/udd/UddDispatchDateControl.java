package com.javahis.ui.udd;

import java.sql.Timestamp;

import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import jdo.sys.SystemTool;
import com.dongyang.ui.event.TTableEvent;

/**
 * <p> Title: סԺҩ�����ڰ�ҩ�����趨 </p>
 *
 * <p> Description: סԺҩ�����ڰ�ҩ�����趨 </p>
 *
 * <p> Copyright: javahis 20090311 </p>
 *
 * <p> Company:javahis </p>
 *
 * @author ehui
 * @version 1.0
 */
public class UddDispatchDateControl
    extends TControl {
    //TABLE
    private TTable table;
    //ȫѡCHECK_BOX,��һ������CHECK_BOX
    private TCheckBox all, sunday, monday, tuesday, wednesday, thursday, friday,
        saturday;
    //���ݶ���
    private TDataStore date;
    private String SQL = "SELECT * FROM ODI_DISTDATE WHERE SCH_DATE>=TO_DATE('#','YYYYMMDD') AND SCH_DATE<=TO_DATE('#','YYYYMMDD') ORDER BY SCH_DATE";
    /**
     * ��ʼ��
     */
    public void onInit() {
        super.onInit();
        initComponent();

        // ��TABLEDEPT�е�CHECKBOX��������¼�
        callFunction("UI|TABLE|addEventListener",
                     TTableEvent.CHECK_BOX_CLICKED, this,
                     "onTableCheckBoxClicked");
    }

    /**
     * ���(TABLE)��ѡ��ı��¼�
     *
     * @param obj
     */
    public void onTableCheckBoxClicked(Object obj) {
        table.acceptText();
    }

    /**
     * ��ʼ���ؼ�
     */
    private void initComponent() {
        table = (TTable)this.getComponent("TABLE");
        all = (TCheckBox)this.getComponent("ALL");
        sunday = (TCheckBox)this.getComponent("0");
        monday = (TCheckBox)this.getComponent("1");
        tuesday = (TCheckBox)this.getComponent("2");
        wednesday = (TCheckBox)this.getComponent("3");
        thursday = (TCheckBox)this.getComponent("4");
        friday = (TCheckBox)this.getComponent("5");
        saturday = (TCheckBox)this.getComponent("6");
        // �õ���ǰʱ��
        Timestamp date = SystemTool.getInstance().getDate();
        //��ȡ�����º�Ľ���
       // String data3 = date.toString();
        String Y = date.toString().substring(0, 4);
        String M = date.toString().substring(5, 7);
        String time = date.toString().substring(8, 10);
        int d = Integer.parseInt(M);
        int y = Integer.parseInt(Y);
        String date4 = "";
        d += 3;
        String YY = "" + y;
        String DD = "" + d;
        if (d < 10) {
            DD = "0" + d;
        }
        if (d > 9) {
            DD = "" + d;
        }
        if (d > 12) {
            DD = "0" + (d - 12);
            YY = "" + (y + 1);
        }

        date4 = YY + "-" + DD + "-" + time;

        //��ǰ̨ҳ��Ԫ�ظ�ֵ����ǰʱ��
        this.setValue("START_YEAR",
                      date.toString().substring(0, 10).replace('-', '/'));
        //��ǰ̨ҳ��Ԫ�ظ�ֵ�������º�Ľ���
        this.setValue("END_YEAR",
                      date4.toString().substring(0, 10).replace('-', '/'));

    }

    /**
     * ����¼�
     */
    public void onClear() {
        table.removeRowAll();
        all.setSelected(false);
        sunday.setSelected(true);
        monday.setSelected(true);
        tuesday.setSelected(true);
        wednesday.setSelected(true);
        thursday.setSelected(true);
        friday.setSelected(true);
        saturday.setSelected(true);
        this.onInit();
    }

    /**
     * ��ѯ�¼�
     */
    public void onQuery() {
        Timestamp start = (Timestamp)this.getValue("START_YEAR");
        Timestamp end = (Timestamp)this.getValue("END_YEAR");
        String startDate = StringTool.getString(start, "yyyyMMdd");
        String endDate = StringTool.getString(end, "yyyyMMdd");
        String sql = SQL.replaceFirst("#", startDate).replaceFirst("#", endDate);
        date = new TDataStore();
        date.setSQL(sql);
        date.retrieve();
        fillDate();
        table.setDataStore(date);
        table.setDSValue();
    }

    /**
     * ��ȫ����
     */
    private void fillDate() {
        Timestamp start = (Timestamp)this.getValue("START_YEAR");
        Timestamp end = (Timestamp)this.getValue("END_YEAR");
        if (date.rowCount() <= 0) {
            int row = date.insertRow();
            date.setItem(row, "SCH_DATE", start);
            date.setItem(row, "DIST_FLG", "N");
            date.setActive(row, true);
            fillFromStartToEnd(start, end);
        }
        else {
            start = date.getItemTimestamp(date.rowCount() - 1, "SCH_DATE");
            fillFromStartToEnd(start, end);
        }
    }

    /**
     * ���ݸ������ڲ�ȫ���ݶ��󣬴ӿ�ʼ���ڣ������������ڡ���������ʼ����,������������
     * @param startDate
     * @param endDate
     */
    private void fillFromStartToEnd(Timestamp startDate, Timestamp endDate) {
        int dayDiffer = StringTool.getDateDiffer(endDate, startDate);
        for (int i = 0; i < dayDiffer; i++) {
            Timestamp newDate = StringTool.rollDate(date.getItemTimestamp(date.
                rowCount() - 1, "SCH_DATE"), 1);
            int row = date.insertRow();
            date.setItem(row, "SCH_DATE", newDate);
            date.setItem(row, "DIST_FLG", "Y");
            date.setActive(row, true);
        }
    }

    /**
     * ȫѡ�¼�
     */
    public void onClickAll() {
        boolean checked = TypeTool.getBoolean(this.getValue("ALL"));
        String checkedStr = checked ? "Y" : "N";
        if (date == null) {
            return;
        }
        if (checkedStr == "Y") {
         sunday.setSelected(true);
         monday.setSelected(true);
         tuesday.setSelected(true);
         wednesday.setSelected(true);
         thursday.setSelected(true);
         friday.setSelected(true);
         saturday.setSelected(true);
     }
     else {
         sunday.setSelected(false);
         monday.setSelected(false);
         tuesday.setSelected(false);
         wednesday.setSelected(false);
         thursday.setSelected(false);
         friday.setSelected(false);
         saturday.setSelected(false);

     }

        int count = date.rowCount();
        if (count <= 0) {
            return;
        }
        for (int i = 0; i < count; i++) {
            date.setItem(i, "DIST_FLG", checkedStr);
            date.setActive(i, true);
        }
        table.setDSValue();
    }

    /**
     * �����¼�
     */
    public void onSave() {
        if (date == null) {
            return;
        }
        int count = date.rowCount();
        if (count <= 0) {
            return;
        }
        Timestamp now = date.getDBTime();
        for (int i = 0; i < count; i++) {
            date.setItem(i, "OPT_USER", Operator.getID());
            date.setItem(i, "OPT_DATE", now);
            date.setItem(i, "OPT_TERM", Operator.getIP());
        }
        String[] sql = date.getUpdateSQL();
        if (sql.length <= 0) {
            return;
        }
        TParm result = new TParm(TJDODBTool.getInstance().update(sql));
        if (result.getErrCode() != 0) {
            this.messageBox("E0001");
        }
        else {
            this.messageBox("P0001");
            date.resetModify();
            onClear();
        }
    }

    /**
     * �趨��Ϣ��
     * @param tag
     */
    public void onDate(Object tag) {
        String dayOfWeek = tag + "";
        boolean isClicked = TypeTool.getBoolean(this.getValue(dayOfWeek));
        String distStr ="";
        distStr = (!isClicked) ? "N" : "Y";
        if (date == null) {
            return;
        }
        int count = date.rowCount();
        if (count <= 0) {
            return;
        }
        Timestamp now = date.getDBTime();

        for (int i = 0; i < count; i++) {
            Timestamp everyday = date.getItemTimestamp(i, "SCH_DATE");
            if (everyday.getDay() == TypeTool.getInt(dayOfWeek)) {
                date.setItem(i, "DIST_FLG", distStr);
            }
        }
        table.setDSValue();
    }

    /**
     * TABLE�����¼�����ѡ��Ĺ���Ϊ��ԭ����ֵ�෴
     */
    public void onTbleClick() {
        int row = table.getSelectedRow();
        if (date == null) {
            return;
        }
        int count = date.rowCount();
        if (count <= 0) {
            return;
        }
        date.setItem(row, "DIST_FLG",
                     (!TypeTool.getBoolean(date.getItemData(row, "DIST_FLG"))) ?
                     "Y" : "N");
        table.setDSValue();
    }


    //�������
    public void onClick0() {
        boolean checked = TypeTool.getBoolean(this.getValue("1"));
        //String checkedStr = checked ? "Y" : "N";
        onDate("0");
    }

    //�����һ
    public void onClick1() {
        boolean checked = TypeTool.getBoolean(this.getValue("1"));
       // String checkedStr = checked ? "Y" : "N";
        onDate("1");
    }

    //����ܶ�
    public void onClick2() {
        boolean checked = TypeTool.getBoolean(this.getValue("2"));
        //String checkedStr = checked ? "Y" : "N";
        onDate("2");
    }

    //�������
    public void onClick3() {
        boolean checked = TypeTool.getBoolean(this.getValue("3"));
       // String checkedStr = checked ? "Y" : "N";
        onDate("3");
    }

    //�������
    public void onClick4() {
        boolean checked = TypeTool.getBoolean(this.getValue("4"));
        //String checkedStr = checked ? "Y" : "N";
        onDate("4");
    }

    //�������
    public void onClick5() {
        boolean checked = TypeTool.getBoolean(this.getValue("5"));
       // String checkedStr = checked ? "Y" : "N";
        onDate("5");
    }

    //�������
    public void onClick6() {
        boolean checked = TypeTool.getBoolean(this.getValue("6"));
       // String checkedStr = checked ? "Y" : "N";
        onDate("6");
    }

}
