package com.javahis.ui.dev;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import jdo.dev.DevInStorageTool;
import jdo.dev.DevOutStorageTool;

import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import jdo.sys.SystemTool;
import java.sql.Timestamp;  
  
/** 
 * <p>Title: �������ȷ�ϵ�</p>  
 *
 * <p>Description:�������ȷ�ϵ� </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: javahis</p> 
 *
 * @author fux  
 * @version 1.0 
 */
public class StorageControl  extends TControl {
   /**
     * ��ʼ������
     */
    public void onInit() {
        super.onInit();
        initComponent();
        onQuery();
    }
    /**
     * ��ʼ������ռ�Ĭ��ֵ   
     */
    public void initComponent(){
        Timestamp timestamp = SystemTool.getInstance().getDate();
        setValue("QSTART_DATE",timestamp);
        setValue("QEND_DATE",timestamp);  
    }
    /**
     * ��ѯ����
     */
    public void onQuery(){ 
        ((TTable)getComponent("TABLE")).removeRowAll();
        ((TTable)getComponent("TABLE")).resetModify();
        TParm parm = new TParm();
        if(getValueString("EXWAREHOUSE_NO").length() != 0)
            parm.setData("EXWAREHOUSE_NO",getValueString("EXWAREHOUSE_NO"));
        if(getValueString("QSTART_DATE").length() != 0) 
            parm.setData("EXWAREHOUSE_DATE_BEGIN",StringTool.getTimestamp(getValueString("QSTART_DATE"),"yyyy-MM-dd"));
        if(getValueString("QEND_DATE").length() != 0) 
            parm.setData("EXWAREHOUSE_DATE_END",StringTool.getTimestamp(getValueString("QEND_DATE"),"yyyy-MM-dd"));
        if(getValueString("EXWAREHOUSE_DEPT").length() != 0) //������� 
            parm.setData("EXWAREHOUSE_DEPT",getValueString("EXWAREHOUSE_DEPT")); 
        if(getValueString("EXWAREHOUSE_USER").length() != 0) 
            parm.setData("EXWAREHOUSE_USER",getValueString("EXWAREHOUSE_USER"));
        //�½������TOOl�࣬��������ѯsql
        //ֻ����;�Ĳ���ʾ-���ȷ�� 
        parm.setData("DISCHECK_FLG","Y");
        parm = DevOutStorageTool.getInstance().selectDevOutStorageInf(parm);  
        System.out.println("���ⵥ��ѯ����parm"+parm);
        if(parm.getErrCode()<0)
            return;          
        if(parm.getCount() < 0)
            return;
        ((TTable)getComponent("TABLE")).setParmValue(parm);
    }

    /**
     * ��շ���
     */
    public void onCLear(){
        setValue("EXWAREHOUSE_NO","");
        setValue("QSTART_DATE","");
        setValue("QEND_DATE","");
        setValue("EXWAREHOUSE_DEPT",""); 
        setValue("EXWAREHOUSE_USER","");
        ((TTable)getComponent("TABLE")).removeRowAll();
        ((TTable)getComponent("TABLE")).resetModify();
    }

    /**
     * ������ⵥ
     */ 
    public void onGenerateReceipt(){
        TParm parm = new TParm(); 
        TTable table = (TTable)getComponent("TABLE");
        if(table.getSelectedRow() < 0){ 
            messageBox("��ѡ��һ��������ⵥ");
            return; 
        }  
        TParm parmTable = table.getParmValue();
        //���쵥��
        parm.setData("EXWAREHOUSE_NO",parmTable.getValue("EXWAREHOUSE_NO",table.getSelectedRow()));
        //�������
        parm.setData("INWAREHOUSE_DEPT",parmTable.getValue("EXWAREHOUSE_DEPT",table.getSelectedRow()));
        setReturnValue(parm);
        closeWindow();
    }
}
