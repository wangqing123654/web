package com.javahis.ui.erd;

import com.dongyang.control.TControl;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class ERDBedSelControl extends TControl{

    public ERDBedSelControl() {
    }

    public void onInit() {
        super.onInit();
        initTable();
    }

    /**
     * �������ݿ��������
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }
    /**
      * �õ�����
      * @param deptCode String
      * @return String
      */
     public TParm getERDBed(){
         String eRDRegionSQL = "";
         if(getValueString("ERD_REGION_CODE").length() != 0)
             eRDRegionSQL = " AND ERD_REGION_CODE = '"+getValueString("ERD_REGION_CODE")+"'";
         return new TParm(getDBTool().select(" SELECT * "+
                                             " FROM ERD_BED "+
                                             " WHERE MR_NO IS NULL "+
                                             eRDRegionSQL));
    }

    /**
     * ��ʼ������Table
     */
    public void initTable(){
        ((TTable)getComponent("TABLE")).setParmValue(getERDBed());
    }

    /**
     * ȷ��
     */
    public void onConfirm() {
        if(((TTable)getComponent("TABLE")).getSelectedRow() < 0)
            return;
        setReturnValue(((TTable) getComponent("TABLE")).getParmValue().getRow(((TTable)getComponent("TABLE")).getSelectedRow()));
        closeWindow();
    }
    /**
     * ȡ��
     */
    public void onCancel() {
        closeWindow();
    }
}
