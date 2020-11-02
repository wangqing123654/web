package com.javahis.ui.phl;

import java.sql.Timestamp;

import jdo.phl.PHLSQL;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.javahis.util.DateUtil;

/**
 * <p>
 * Title: �����Ҵ�λ��
 * </p>
 *
 * <p>
 * Description: �����Ҵ�λ��
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company:
 * </p>
 *
 * @author zhangy 2009.04.22
 * @version 1.0
 */
public class PHLCardControl
    extends TControl {

    private TTable table;

    private String region_code;

    public PHLCardControl() {
    }

    /**
     * ��ʼ������
     */
    public void onInit() {
        // ��ʼ��������
        Object obj = getParameter();
        if (obj != null) {
            region_code = ( (TParm) obj).getValue("REGION_CODE");
            this.setValue("REGION_CODE", region_code);
        }
        String sql = PHLSQL.getPHLBedCardInfor(region_code,Operator.getRegion());//=====pangben modify 20110622
        //System.out.println("sql" + sql);
        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
        if (parm == null || parm.getCount() <= 0) {
            this.messageBox("û�в�ѯ����");
        }

        Timestamp date = SystemTool.getInstance().getDate();
        for (int i = 0; i < parm.getCount(); i++) {
            if (!"".equals(parm.getValue("BIRTH_DATE", i))) {
               // parm.setData("AGE", i, StringUtil.getInstance().showAge(parm.
                   // getTimestamp("BIRTH_DATE", i), date));
                parm.setData("AGE", i, DateUtil.showAge(parm.
                		getTimestamp("BIRTH_DATE", i), date));//modify caoyong 20140401
                
            }
            else {
                parm.setData("AGE", i, "");
            }
        }
        table = this.getTable("TABLE");
        table.setParmValue(parm);
    }

    /**
     * TABLE˫���¼�
     */
    public void onTableDoubleClicked() {
        int row = table.getSelectedRow();
        setReturnValue(table.getParmValue().getRow(row));
        this.closeWindow();
    }

    /**
     * �õ�Table����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }

}
