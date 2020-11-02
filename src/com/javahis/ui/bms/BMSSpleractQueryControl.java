package com.javahis.ui.bms;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TMenuItem;
import com.dongyang.data.TParm;
import jdo.bms.BMSQueryPatTool;

/**
 * <p>
 * Title: ��Ѫ��Ӧ��ѯ
 * </p>
 *
 * <p>
 * Description: ��Ѫ��Ӧ��ѯ
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: JavaHis
 * </p>
 *
 * @author zhangy 2009.09.24
 * @version 1.0
 */
public class BMSSpleractQueryControl extends TControl {
    public BMSSpleractQueryControl() {
    }

    /**
     * ��ʼ������
     */
    public void onInit() {
        Object obj = this.getParameter();
        if (obj != null) {
            TParm parm = (TParm) obj;
            String mr_no = parm.getValue("MR_NO");
            TParm inparm = new TParm();
            inparm.setData("MR_NO", mr_no);
            // �ż�����Ϣ
            TParm resultOE = BMSQueryPatTool.getInstance().onQueryOE(inparm);
            // סԺ��Ϣ
            TParm resultI = BMSQueryPatTool.getInstance().onQueryI(inparm);

            TParm parmValue = new TParm();
            if (resultOE.getCount() > 0) {
                for (int i = 0; i < resultOE.getCount(); i++) {
                    parmValue.addData("DEPT_CODE",
                                      resultOE.getValue("DEPT_CODE", i));
                    parmValue.addData("STATION_CODE", "");
                    parmValue.addData("MR_NO", resultOE.getValue("MR_NO", i));
                    parmValue.addData("CASE_NO", resultOE.getValue("CASE_NO", i));
                    parmValue.addData("IPD_NO", "");
                    parmValue.addData("BED_NO", "");//liling 20140703 add
                }
            }

            if (resultI.getCount() > 0) {
                for (int i = 0; i < resultI.getCount(); i++) {
                    parmValue.addData("DEPT_CODE",
                                      resultI.getValue("DEPT_CODE", i));
                    parmValue.addData("STATION_CODE",
                                      resultI.getValue("STATION_CODE", i));
                    parmValue.addData("MR_NO", resultI.getValue("MR_NO", i));
                    parmValue.addData("CASE_NO", resultI.getValue("CASE_NO", i));
                    parmValue.addData("IPD_NO", resultI.getValue("IPD_NO", i));
                    parmValue.addData("BED_NO", resultI.getValue("BED_NO", i));//liling 20140703 add
                }
            }

            getTable("TABLE").setParmValue(parmValue);
        }
    }

    /**
     * ���ط���
     */
    public void onReturn() {
        TTable table = this.getTable("TABLE");
        if (table.getSelectedRow() < 0) {
            this.messageBox("û��ѡ����");
            return;
        }
        this.setReturnValue(table.getParmValue().getRow(table.getSelectedRow()));
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
