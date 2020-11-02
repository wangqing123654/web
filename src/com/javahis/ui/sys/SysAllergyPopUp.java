package com.javahis.ui.sys;

import java.awt.event.KeyEvent;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TKeyListener;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.event.TTextFieldEvent;

/**
*
* <p>Title: ����Դ�������ɷ� ����ѡ���</p>
*
* <p>Description: </p>
*
* <p>Copyright: Copyright (c) 2008</p>
*
* <p>Company: JavaHis</p>
*
* @author EHUI 2009.1.16
* @version 1.0
*/
public class SysAllergyPopUp extends TControl{

    private String oldText = "";
    private  String allergyType;/*A���ɷݹ���B��ҩƷ����C����������*/
    private TTable table;
    private static final String GET_A_SQL="SELECT * FROM SYS_DICTIONARY WHERE GROUP_ID='PHA_INGREDIENT' ORDER BY ID";
    private static final String GET_C_SQL="SELECT * FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_ALLERGYTYPE' ORDER BY ID";
    private TParm tds;
    private TDataStore dataStore;
    /**
     * ��ʼ��
     */
    public void onInit()
    {
        super.onInit();
        table = (TTable)this.getComponent("TABLE");
        tds=new TParm();
        callFunction("UI|EDIT|addEventListener",TTextFieldEvent.KEY_RELEASED,this,"onKeyReleased");
        callFunction("UI|EDIT|addEventListener","EDIT->" + TKeyListener.KEY_PRESSED,this,"onKeyPressed");
//        table.addEventListener("TABLE->" + TTableEvent.DOUBLE_CLICKED,this,"onDoubleClicked");
        table.addEventListener("TABLE->" + TTableEvent.DOUBLE_CLICKED, this,
        "onDoubleClicked");
        initParamenter();
    }
    /**
     * ��ʼ������
     */
    public void initParamenter()
    {
        Object obj = getParameter();
        if(obj == null){
        	return;
        }

        if(!(obj instanceof TParm)){
        	return;
        }

        TParm parm = (TParm)obj;
        allergyType=parm.getValue("ALLERGY_TYPE",0);
        dataStore=new TDataStore();
        table.setDataStore(dataStore);
        if("A".equalsIgnoreCase(allergyType)){
        	tds=new TParm(TJDODBTool.getInstance().select(GET_A_SQL));
        	dataStore.setSQL(GET_A_SQL);
        	dataStore.retrieve();
        }else if("C".equalsIgnoreCase(allergyType)){
        	tds=new TParm(TJDODBTool.getInstance().select(GET_C_SQL));
        	dataStore.setSQL(GET_C_SQL);
        	dataStore.retrieve();
        }
//        dataStore.showDebug();
//        table.setParmValue(tds);
        table.setDSValue();
        //ALLERGY_TYPE,ORDER_DESC
        String text = parm.getValue("TEXT");
        setEditText(text);
    }
    /**
     * ���¼���
     */
    public void onInitReset()
    {
        Object obj = getParameter();
        if(obj == null)
            return;
        if(!(obj instanceof TParm))
            return;
        TParm parm = (TParm)obj;
        String text = parm.getValue("TEXT");
        String oldText = this.getValueString("EDIT");
        if(oldText.equals(text))
            return;
        setEditText(text);
    }
    /**
     * ������������
     * @param s String
     */
    public void setEditText(String s)
    {
        callFunction("UI|EDIT|setText",s);
        int x = s.length();
        callFunction("UI|EDIT|select",x,x);
        onKeyReleased(s);
    }
    /**
     * �����¼�
     * @param s String
     */
    public void onKeyReleased(String s)
    {
        s = s.toUpperCase();
        if(oldText.equals(s))
            return;
        oldText = s.toUpperCase();
        //System.out.println("ssssssssssssssssss= " + s);
        table.filterObject(this,"filter");
        int count = table.getRowCount();
        if(count > 0)
            table.setSelectedRow(0);
    }
    /**
     * ���˷���
     * @param parm TParm
     * @param row int
     * @return boolean
     */
    public boolean filter(TParm parm,int row)
    {
    	//System.out.println("PARM=================="+parm);
    	boolean result=parm.getValue("ORDER_DESC",row).toUpperCase().startsWith(oldText) ||
        parm.getValue("PY1",row).toUpperCase().startsWith(oldText)||
        parm.getValue("PY2",row).toUpperCase().startsWith(oldText)||
        parm.getValue("DESCRIPTION",row).toUpperCase().startsWith(oldText);

        return result;

//    	return true;
    }
    /**
     * �����¼�
     * @param e KeyEvent
     */
    public void onKeyPressed(KeyEvent e)
    {
    	//System.out.println("e.getKeyCode"+e.getKeyCode());
    	//System.out.println("here");
        if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
        {
            callFunction("UI|setVisible",false);
            return;
        }
        int count = (Integer)callFunction("UI|TABLE|getRowCount");
        if(count <= 0)
            return;
        switch(e.getKeyCode()){
        case KeyEvent.VK_UP:
            int row = (Integer)callFunction("UI|TABLE|getSelectedRow") - 1;
            if(row < 0)
                row = 0;
//            table.getTable().grabFocus();
//            table.setSelectedRow(row);
            callFunction("UI|TABLE|setSelectedRow",row);
            break;
        case KeyEvent.VK_DOWN:
            row = (Integer)callFunction("UI|TABLE|getSelectedRow") + 1;
            if(row >= count)
                row = count - 1;
//            table.getTable().grabFocus();
//            table.setSelectedRow(row);
            callFunction("UI|TABLE|setSelectedRow",row);
            break;
        case KeyEvent.VK_ENTER:
            callFunction("UI|setVisible",false);
            onSelected();
            break;
        }
    }
    /**
     * ��˫���¼�
     * @param row int
     */
    public void onDoubleClicked(int row)
    {
        if(row < 0)
            return;
        callFunction("UI|setVisible",false);
        onSelected();
    }
    /**
     * ѡ��
     */
    public void onSelected()
    {
        int row = (Integer)callFunction("UI|TABLE|getSelectedRow");
        if(row < 0)
            return;
        TDataStore dataStore = (TDataStore)callFunction("UI|TABLE|getDataStore");
        TParm parm = dataStore.getRowParm(row);
        setReturnValue(parm);
    }

}
