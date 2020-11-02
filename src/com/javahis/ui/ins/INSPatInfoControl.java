package com.javahis.ui.ins;

import jdo.sys.PatTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.event.TTableEvent;
/**
 * 
 * <p>
 * Title:������Ϣ��ѯ
 * </p>
 * 
 * <p>
 * Description:������Ϣ��ѯ
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) bluecore
 * </p>
 * 
 * <p>
 * Company:Javahis
 * </p>
 * 
 * @author pangb 2012-1-31
 * @version 2.0
 */
public class INSPatInfoControl extends TControl{
	private String idNo;//���֤��
	private int selectrow = -1;//ѡ�����
	public void onInit() {
		super.onInit();
		//�õ�ǰ̨���������ݲ���ʾ�ڽ�����
		TParm recptype = (TParm) getParameter();
		idNo=recptype.getValue("IDNO");
		TParm parm=new TParm();
		   //table1�ĵ��������¼�
        callFunction("UI|TABLE|addEventListener",
                     "TABLE->" + TTableEvent.CLICKED, this, "onTableClicked");
        //table1�ĵ��������¼�
        callFunction("UI|TABLE|addEventListener",
                     "TABLE->" + TTableEvent.DOUBLE_CLICKED, this,
                     "onTableDoubleClicked");
		parm.setData("IDNO", idNo);// ���֤����
		TParm result = PatTool.getInstance().getInfoForIdNo(parm);
		this.callFunction("UI|TABLE|setParmValue", result);
	}
	 /**
     *���Ӷ�Table�ļ���
     * @param row int
     */
    public void onTableClicked(int row) {
        //���������¼�
        this.callFunction("UI|TABLE|acceptText");
//   TParm data = (TParm) callFunction("UI|TABLE|getParmValue");
        selectrow = row;
    }

    public void onTableDoubleClicked(int row) {
        TParm data = (TParm) callFunction("UI|TABLE|getParmValue");
        this.setReturnValue(data.getRow(row));
        this.callFunction("UI|onClose");
    }
    /**
    *
    */
   public void onOK() {
       TParm data = (TParm) callFunction("UI|TABLE|getParmValue");
       this.setReturnValue(data.getRow(selectrow));
       this.callFunction("UI|onClose");
   }
}
