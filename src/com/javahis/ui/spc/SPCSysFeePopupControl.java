package com.javahis.ui.spc;

import java.awt.event.KeyEvent;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_Database;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TKeyListener;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.event.TTextFieldEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.StringUtil;
import jdo.sys.Operator;

/**
 *
 * <p>Title: SYS Fee ����ѡ���</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author lzk 2009.1.16
 * @version 1.0
 */
public class SPCSysFeePopupControl
    extends TControl {
    private String oldText = "";
    private TTable table;
    /**
     * ����ҽ��վ������5��ҳǩ�Ĵ�������
     */
    private int rxType;
    /**
     * ����ҽ��վ���׷�Ϊ2��ҳǩ��������ҩ�����У��������������ҩ�����á�����ҩƷ��
     */
    private String W_group;

    /**
     * סԺ
     */
    private String orTypeOdi;
    /**
     * ָ��ҽ������
     */
    private String inCat1Type;
    /**
     * ��������
     */
    private String allergyType;
    //��������ҽ�����ͣ�ӦȫΪ����ҽ��������LIS/RIS/OTH�Ȳ���
    private String hrmType;
    private int page = 0;
    private int index = 0;
    
    private String activeFlg = "";
    /**
     * ��ʼ��
     */
    public void onInit() {
        super.onInit();
        table = (TTable) callFunction("UI|TABLE|getThis");
  
        table.setSort("SEQ DESC,ORDER_CODE ASC");
        table.sort();
        table.filter();
        
        /**String sql = table.getSQL().replace("ORDER BY SEQ DESC","ORDER BY ORDER_CODE ASC");
        table.setSQL(sql);
        table.filter();**/
        callFunction("UI|EDIT|addEventListener", TTextFieldEvent.KEY_RELEASED, this,
                     "onKeyReleased");
        callFunction("UI|EDIT|addEventListener",
                     "EDIT->" + TKeyListener.KEY_PRESSED, this, "onKeyPressed");
        table.addEventListener("TABLE->" + TTableEvent.DOUBLE_CLICKED, this,
                               "onDoubleClicked");
        initParamenter();
    }

    /**
     * ��ʼ������
     */
    public void initParamenter() {
        Object obj = getParameter();
        if (obj == null)
            return;
        if (! (obj instanceof TParm))
            return;
        TParm parm = (TParm) obj;
        String text = parm.getValue("TEXT");
        setEditText(text);
        rxType = parm.getInt("RX_TYPE");
        hrmType=parm.getValue("HRM_TYPE");
        activeFlg = parm.getValue("ACTIVE_FLG");
        
        //סԺ
        Object objOidOrder = parm.getData("ODI_ORDER_TYPE");
        if (objOidOrder != null) {
            orTypeOdi = "" + objOidOrder;
        }
        //����
        Object wGroup = parm.getData("W_GROUP");
        if (wGroup != null) {
            W_group = "" + wGroup;
        }
        // ָ��ҽ������
        inCat1Type = parm.getValue("CAT1_TYPE");
        // ����ʷ��ҽ������
        allergyType = parm.getValue("ALLERGY_TYPE");
    }

    /**
     * ���¼���
     */
    public void onInitReset() {
        Object obj = getParameter();
        if (obj == null)
            return;
        if (! (obj instanceof TParm))
            return;
        TParm parm = (TParm) obj;
        String text = parm.getValue("TEXT");
        String oldText = (String) callFunction("UI|EDIT|getText");
        if (oldText.equals(text)) {
            return;
        }

        setEditText(text);
    }

    /**
     * ������������
     * @param s String
     */
    public void setEditText(String s) {
        page = 0;
        index = 0;
        setValue("L_PAGE","" + (page + 1));
        callFunction("UI|EDIT|setText", s);
        int x = s.length();
        callFunction("UI|EDIT|select", x, x);
        onKeyReleased(s);
    }

    /**
     * �����¼�
     * @param s String
     */
    public void onKeyReleased(String s) {
        page = 0;
        index = 0;
        setValue("L_PAGE","" + (page + 1));
        s = s.toUpperCase();
        if (oldText.equals(s))
            return;
        oldText = s;
        table.filterObject(this, "filter");
        int count = table.getRowCount();
        if (count > 0)
            table.setSelectedRow(0);
    }

    /**
     * ���˷���
     * @param parm TParm
     * @param row int
     * @return boolean
     */
    public boolean filter(TParm parm, int row) {
    	//System.out.println("=====parm222222====="+parm);
        boolean result =
            (parm.getValue("ORDER_CODE", row).toUpperCase().indexOf(oldText) !=
             -1 ||
             parm.getValue("ORDER_DESC", row).toUpperCase().indexOf(oldText) !=
             -1 ||
             parm.getValue("TRADE_ENG_DESC", row).toUpperCase().indexOf(oldText) !=
             -1 ||
             parm.getValue("GOODS_DESC", row).toUpperCase().indexOf(oldText) !=
             -1 ||
             parm.getValue("GOODS_PYCODE", row).toUpperCase().indexOf(oldText) !=
             -1 ||
             parm.getValue("ALIAS_DESC", row).toUpperCase().indexOf(oldText) !=
             -1 ||
             parm.getValue("ALIAS_PYCODE", row).toUpperCase().indexOf(oldText) !=
             -1 ||
             parm.getValue("NHI_FEE_DESC", row).toUpperCase().indexOf(oldText) !=
             -1 ||
             parm.getValue("PY1", row).toUpperCase().indexOf(oldText) != -1 ||
             parm.getValue("PY2", row).toUpperCase().indexOf(oldText) != -1 );
        if(activeFlg != null && "Y".equals(activeFlg)){
        	result = result && ("Y".equals(parm.getValue("ACTIVE_FLG", row).toUpperCase())) ; 
        }
        String region_code = Operator.getRegion();
        if (!"".equals(region_code)) {
            result = result && (parm.getData("REGION_CODE", row) == null ||
                                "".equals(parm.getValue("REGION_CODE", row)) ||
                                region_code.equals(parm.getValue("REGION_CODE", row).
                                                   toUpperCase()));
        }

        String cat1Code = parm.getValue("ORDER_CAT1_CODE", row);
        String cat1Type = parm.getValue("CAT1_TYPE", row);
        if (!StringUtil.isNullString(orTypeOdi)) {
            //ȫ��
            if ("A".equals(orTypeOdi)) {
                result = result;
            }
            //������
            if ("B".equals(orTypeOdi)) {
                result = result &&
                    ("LIS".equalsIgnoreCase(cat1Type) ||
                     "RIS".equalsIgnoreCase(cat1Type));
            }
            //������Ŀ
            if ("C".equals(orTypeOdi)) {
                result = result &&
                    ("TRT".equalsIgnoreCase(cat1Type) ||
                     "PLN".equalsIgnoreCase(cat1Type));
            }
            //��ҩ
            if ("D".equals(orTypeOdi)) {
                result = result && "PHA_W".equalsIgnoreCase(cat1Code);
            }
            //�г�
            if ("E".equals(orTypeOdi)) {
                result = result && "PHA_C".equalsIgnoreCase(cat1Code);
            }
            //�в�
            if ("F".equals(orTypeOdi)) {
                result = result && "PHA_G".equalsIgnoreCase(cat1Code);
            }
            //����ҩƷ
            if ("G".equals(orTypeOdi)) {
                result = result &&
                    (parm.getValue("LCS_CLASS_CODE", row).length() != 0);
            }
        }
        else if (rxType > 0) {
            switch (rxType) {
                //����ҩ
                case 1:

                    result = result &&
                        (cat1Code.equalsIgnoreCase("PHA_W") ||
                         cat1Code.equalsIgnoreCase("PHA_C"));
                    break;
                    //����ҩ
                case 2:
                    result = result &&
                        (TypeTool.getBoolean(parm.getValue("CTRL_FLG", row)));
                    break;
                case 3:

                    //��ҩtodo
                    result = result && (cat1Code.equalsIgnoreCase("PHA_G"));
                    break;
                    //����
                case 4:
                    result = result && (cat1Type.equalsIgnoreCase("TRT") || cat1Type.equalsIgnoreCase("PLN")||cat1Type.equalsIgnoreCase("OTH"));
                    break;
                    //����������
                case 5:
                    String setmain = parm.getValue("ORDERSET_FLG", row);
                    result = result &&
                        ("Y".equalsIgnoreCase(setmain) &&
                         (cat1Type.equalsIgnoreCase("LIS") ||
                          cat1Type.equalsIgnoreCase("RIS")));
                    break;
                    //ϸ��
                case 6:
                    String setMain = parm.getValue("ORDERSET_FLG", row);
                    result = result &&
                        ("N".equalsIgnoreCase(setMain) &&
                         cat1Code.equalsIgnoreCase("EXA"));
            }
        }
        //����
        else if (!StringUtil.isNullString(W_group)) {

            result = result && ("PHA_W".equalsIgnoreCase(cat1Code) ||
                                "PHA_C".equalsIgnoreCase(cat1Code) ||
                                "TRT".equalsIgnoreCase(cat1Type) ||
                                "PLN".equalsIgnoreCase(cat1Type));
        }
        //ָ��ҽ������
        else if (!StringUtil.isNullString(inCat1Type)) {
            //System.out.println("parm---"+parm);
            result = result && (cat1Type.equalsIgnoreCase(inCat1Type));
        }
        //ָ������ҽ������
        else if (!StringUtil.isNullString(allergyType)) {
            result = result && ("PHA".equalsIgnoreCase(cat1Type));
        }
        else if(!StringUtil.isNullString(hrmType)){
            result=(result && (StringTool.getBoolean(parm.getValue("ORDERSET_FLG",row)))) ||
            	   (result && "PHA_W".equals(cat1Code)) || (result && "MAT".equals(cat1Code));
        }
        if(result)
        {
            index++;
            if(index < (page) * 19 || index > (page + 1) * 19)
                return false;
        }
        
        //this.messageBox("result"+result);
        //System.out.println("=========result============="+result);
        return result;
    }

    /**
     * �����¼�
     * @param e KeyEvent
     */
    public void onKeyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            callFunction("UI|setVisible", false);
            return;
        }
        int count = table.getRowCount();
        if (count <= 0)
            return;
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                int row = table.getSelectedRow() - 1;
                if (row < 0)
                    row = 0;
                table.getTable().grabFocus();
                table.setSelectedRow(row);
                break;
            case KeyEvent.VK_DOWN:
                row = table.getSelectedRow() + 1;
                if (row >= count)
                    row = count - 1;
                table.getTable().grabFocus();
                table.setSelectedRow(row);
                break;
            case KeyEvent.VK_ENTER:
                callFunction("UI|setVisible", false);
                onSelected();
                break;
        }
    }

    /**
     * ��˫���¼�
     * @param row int
     */
    public void onDoubleClicked(int row) {
        if (row < 0)
            return;
        callFunction("UI|setVisible", false);
        onSelected();
    }

    /**
     * ѡ��
     */
    public void onSelected() {
        int row = table.getSelectedRow();
        if (row < 0)
            return;
        TDataStore dataStore = table.getDataStore();
        String orderCode = dataStore.getItemString(row,"ORDER_CODE");
        String sql =  "SELECT * FROM SYS_FEE WHERE ORDER_CODE='" + orderCode +"'  ";
        if(activeFlg != null && "Y".equals(activeFlg)){
        	sql += " AND  ACTIVE_FLG = 'Y'  ";
        }
        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
        //System.out.println("====parm======"+parm);
        if (parm.getErrCode() < 0 || parm.getCount() <= 0)
            return;
        parm = parm.getRow(0);
        setReturnValue(parm);
    }

    /**
     * ���±��� ," ORDER BY SEQ DESC, ORDER_CODE"
     */
    public void onResetDW() {
        TIOM_Database.removeLocalTable("SYS_FEE", false);
        table.retrieve();
    }

    /**
     * ��������ȫ��(," ORDER BY SEQ DESC, ORDER_CODE")
     */
    public void onResetFile() {
        TIOM_Database.removeLocalTable("SYS_FEE", true);
        table.retrieve();
    }
    public void onUp()
    {
        page--;
        index = 0;
        if(page < 0)
        {
            page = 0;
            return;
        }
        setValue("L_PAGE","" + (page + 1));
        table.filterObject(this, "filter");
        int count = table.getRowCount();
        if (count > 0)
            table.setSelectedRow(0);
    }
    public void onDown()
    {
        page ++;
        index = 0;
        setValue("L_PAGE","" + (page + 1));
        table.filterObject(this, "filter");
        int count = table.getRowCount();
        if (count > 0)
            table.setSelectedRow(0);
    }

    /**
     * �һ�MENU�����¼�
     * @param tableName
     */
    public void onShowPopMenu() {
        TTable table = (TTable)this.getComponent("TABLE");
        table.setPopupMenuSyntax(
            "��ʾ����ҽ��ϸ��|Show LIS/RIS Detail Items,onOrderSetShow");
    }

    /**
     * �һ�MENU��ʾ����ҽ���¼�
     */
    public void onOrderSetShow(){
        TTable table = (TTable)this.getComponent("TABLE");
        int row = table.getSelectedRow();
        String order_code = table.getDataStore().getItemString(row, "ORDER_CODE");
        String sql = "SELECT A.ORDER_DESC, A.SPECIFICATION, DOSAGE_QTY, "
            + " UNIT_CODE AS MEDI_UNIT, OWN_PRICE, OWN_PRICE * DOSAGE_QTY "
            + " AS OWN_AMT, EXEC_DEPT_CODE, OPTITEM_CODE, INSPAY_TYPE "
            + " FROM SYS_FEE A, SYS_ORDERSETDETAIL B "
            + " WHERE A.ORDER_CODE = B.ORDERSET_CODE "
            + " AND B.ORDERSET_CODE = '" + order_code + "' "
            + " ORDER BY B.ORDERSET_CODE, B.ORDER_CODE";
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        this.openDialog("%ROOT%\\config\\opd\\OPDOrderSetShow.x", result);

//        TParm parm = new TParm();
//        TTextField textfield = (TTextField)this.getComponent("EDIT");
//        parm.setData("ORDER_CODE", textfield.getValue());
//        // ��table�ϵ���text����sys_fee��������
//        textfield.setPopupMenuParameter("ORDER", getConfigParm().newConfig(
//            "%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
    }

}
