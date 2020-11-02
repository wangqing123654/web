package com.javahis.ui.bil;

import jdo.bil.BILCounteTool;
import jdo.bil.BILInvoiceTool;
import jdo.bil.BILInvrcptTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;

/**
 * 
 * <p>
 * Title: ��������Ʊ��
 * </p>
 * 
 * <p>
 * <b>Description:</b> <br>
 * ��������Ʊ��
 * </p>
 * 
 * <p>
 * Copyright:
 * </p>
 * 
 * <p>
 * Company: bluecore
 * </p>
 * 
 * @author design: pangben 2014-7-9 <br>
 *         coding:
 * @version 4.0
 */
public class BILInvoiceCollarControl extends TControl {
	TTable tableM;
	/**
     * �õ�TTable
     *
     * @param tag
     *            String
     * @return TTable
     */
    public TTable getTTable(String tag) {
        return (TTable)this.getComponent(tag);
    }
	public void onInit() {
		super.onInit();
		tableM = (TTable) this.getComponent("TABLE1");
		// ����ʹ��ID
		this.callFunction("UI|CASHIER_CODE|setValue", Operator.getID());
		callFunction("UI|CASHIER_CODE|setEnabled", false);
		// ����ʹ����
		this.callFunction("UI|OPT_TERM|setValue", Operator.getIP());
		callFunction("UI|OPT_TERM|setEnabled", false);
		getTTable("TABLE1").addEventListener(TTableEvent.CHECK_BOX_CLICKED,
                 this, "onClicked");

        //table1�������¼�
//        callFunction("UI|TABLE1|addEventListener", "TABLE1->"
//                     + TTableEvent.CLICKED, this, "onTableClicked");
		this.onQuery();
	}
    /**
     * ���Ӷ�Table�ļ���
     */
	public void onClicked(Object obj) {
		 TTable bilTable = (TTable) obj;
		 bilTable.acceptText();
//	     TParm tableParm = bilTable.getParmValue();
//		// ��¼��ǰѡ�е���
//		// �õ���ǰѡ��������
//		TParm parm = tableM.getParmValue().getRow(row);
//		// �����һƱ��Ϊ��,��˵����Ʊ���Ѿ��ù�
//		if (tableParm.getValue("UPDATE_NO",) == null
//				|| parm.getValue("UPDATE_NO").length() == 0)
//			return;
//		for (int i = 0; i < tableM.getParmValue().getCount(); i++) {
//			
//		}
		// for (int i = 0; i < tableM.getRowCount(); i++) {
		// tableM.setValueAt("N", i, 0);
		// }
		// tableM.setValueAt("Y", row, 0);
//		tableM.setParmValue(parmValue)
//		tableM.acceptText();
	}
    /**
     * ��ѯ����
     */
    public void onQuery() {
        //�õ���ѯ����
        TParm parm = this.getdata();
        //���table
        this.callFunction("UI|TABLE1|removeRowAll");
        TParm data = new TParm();
        //��ѯ����
        data = BILInvoiceTool.getInstance().selectAllData(parm);
        //��table���
        tableM.setParmValue(data);
        ((TCheckBox)this.getComponent("CHK_SEL")).setSelected(false);
    }
    /**
     * ��ѯ���
     * @return TParm
     */
    public TParm getdata() {
        TParm parm = new TParm();
        String value = getValueString("RECP_TYPE");
        if (value.length() > 0)
            parm.setData("RECP_TYPE", value);
        value = Operator.getID();
        if (value.length() != 0)
            parm.setData("CASHIER_CODE", value);
        //0��ʾʹ���У�1��ʾ״̬��û�н��ص�
        parm.setData("STATUS", "1");
        return parm;
    }
    /**
     * ����
     */
    public void onSave() {
    	TParm tableParm=tableM.getParmValue();
    	boolean flg=false;
    	TParm tempParm=new TParm();
    	for (int j = 0; j < tableParm.getCount(); j++) {
    		if (tableParm.getValue("FLG",j).equals("Y")) {
				if (tableParm.getValue("UPDATE_NO", j) == null
						|| tableParm.getValue("UPDATE_NO", j).length() == 0) {
					messageBox_(tableParm.getValue("RECP_TYPE_NAME", j)+"����Ʊ��Ϊ:"+tableParm.getValue("END_INVNO", j)+"��Ʊ���Ѿ�����!");
					return;
				}
				 //01����ʹ����
		        if (checkout(tableParm.getRow(j))) {
		            //��������....�Ѿ����˲����ٿ�
		            this.messageBox(tableParm.getValue("RECP_TYPE_NAME", j)+"����Ʊ��Ϊ:"+tableParm.getValue("END_INVNO", j)+"�Ѿ�����");
		            return;
		        }
    			flg=true;
    			tempParm.addRowData(tableParm, j);
			}
    	}
    	if (!flg) {
			this.messageBox("��ѡ����Ҫ����������");
			return;
		}
       
        //datat.setData("START_INVNO", parm.getData("UPDATE_NO", selectrow));
        //״̬0����ʹ����
    	TParm parm=new TParm();
    	parm.setData("STATUS", "0");
    	parm.setData("OPEN_DATE", StringTool.getString(SystemTool
            .getInstance().getDate(), "yyyyMMdd HHmmss"));
    	parm.setData("OPT_TERM", Operator.getIP());
    	parm.setData("TERM_IP", Operator.getIP());
    	parm.setData("OPT_USER", Operator.getID());
    	parm.setData("BILPARM", tempParm.getData());
//        System.out.println("����Action����" + parm);
        //���ÿ���Action
        TParm result = TIOM_AppServer.executeAction(
            "action.bil.InvoicePersionAction", "sumOpencheck", parm);
        if (result.getErrCode() != 0) {
            this.messageBox("E0005"); //ִ��ʧ��
        }
        else {
            this.messageBox("P0005"); //ִ�гɹ�
        }
        onQuery();
    }
    /**
     * ����Ƿ��Ѿ�����״̬
     * @param parm TParm
     * @return boolean
     */
    public boolean checkout(TParm parm) {
        //�ý���Ʊ����Ϊ�������Ʊ�Ƿ�����ʹ����
        parm.setData("CASHIER_CODE", Operator.getID());
//		System.out.println("check parm="+parm);
        //����ʱ��
        TParm result = BILCounteTool.getInstance().CheckCounter(parm);
        if (result.getCount("CASHIER_CODE") > 0)
            return true;
        return false;
    }
    /**
     * ȫѡ
     */
    public void onSel(){
    	TParm tableParm=tableM.getParmValue();
    	if (tableParm.getCount()<=0) {
			this.messageBox("û�в���������");
		}
    	if (((TCheckBox)this.getComponent("CHK_SEL")).isSelected()) {
    		String []arrayName={"EKT","IBS","OPB","PAY","REG"};
    		boolean flg=true;
        	for (int i = 0; i < arrayName.length; i++) {
        		flg=true;
    			for (int j = 0; j < tableParm.getCount(); j++) {
    				if (arrayName[i].equals(tableParm.getValue("RECP_TYPE", j))) {
						if (flg) {
							tableParm.setData("FLG",j, "Y");
							flg=false;
						}
					}
    			}
    		}
        	tableM.setParmValue(tableParm);
		}else{
			for (int i = 0; i < tableParm.getCount(); i++) {
				tableParm.setData("FLG",i, "N");
			}
			tableM.setParmValue(tableParm);
		}
    	
    }
    /**
     * ��շ���
     */
    public void onClear() {
        clearValue("RECP_TYPE");
        callFunction("UI|TABLE1|clearSelection");
        this.onQuery();
    }
    /**
     * ���ʷ���
     */
    public void onClosedata() {
    	TParm tableParm=tableM.getParmValue();
    	boolean flg=false;
    	TParm tempParm=new TParm();
    	for (int j = 0; j < tableParm.getCount(); j++) {
    		if (tableParm.getValue("FLG",j).equals("Y")) {
				if (!tableParm.getValue("STATUS",j).equals("0")) {
		            this.messageBox(tableParm.getValue("RECP_TYPE_NAME", j)+"����Ʊ��Ϊ:"+tableParm.getValue("END_INVNO", j)+"��Ʊ�ݲ���ʹ����");
		            return;
		        }
				 //01����ʹ����
		        if (!checkout(tableParm.getRow(j))) {
		            //��������....�Ѿ����˲����ٿ�
		            this.messageBox(tableParm.getValue("RECP_TYPE_NAME", j)+"����Ʊ��Ϊ:"+tableParm.getValue("END_INVNO", j)+"��δ����");
		            return;
		        }
    			flg=true;
    			tempParm.addRowData(tableParm, j);
			}
    	}
    	if (!flg) {
			this.messageBox("��ѡ����Ҫ����������");
			return;
		}
    	for (int i = 0; i < tempParm.getCount("END_INVNO"); i++) {
    		tempParm.setData("STATUS",i, "1");
    		tempParm.setData("CASHIER_CODE",i, Operator.getID());
    		tempParm.setData("OPT_USER",i, Operator.getID());
    		tempParm.setData("OPT_TERM",i, Operator.getIP());
    		tempParm.setData("TERM_IP",i, Operator.getIP());
    		tempParm.setData("CLS_DATE",i,
                    StringTool.getString(SystemTool.getInstance()
                                         .getDate(), "yyyyMMdd HH:mm:ss"));
    		 //��Ҫ��1
            //�����һƱ��Ϊ��,�����Ʊ�ž��ǽ���Ʊ��
            if (tempParm.getValue("UPDATE_NO",i) == null ||
            		tempParm.getValue("UPDATE_NO",i).length() == 0) {
            }else {
                //��һƱ�ż�һ��Ϊ����Ʊ��
            	tempParm.setData("END_INVNO",i,
                             StringTool.subString(tempParm.getValue("UPDATE_NO",i)));
            }
            //�����һƱ�ŵ��ڳ�ʼƱ��,����Ʊ�ŵ�����ʼƱ��
            if (tempParm.getValue("START_INVNO",i).equals(tempParm.getValue("UPDATE_NO",i)))
            	tempParm.setData("END_INVNO",i, tempParm.getValue("UPDATE_NO",i));
		}
        //���ù���Action
        TParm result = TIOM_AppServer.executeAction(
            "action.bil.InvoicePersionAction", "sumCloseCheck", tempParm);
        if (result.getErrCode() != 0) {
            this.messageBox("E0005"); //ִ��ʧ��
        }
        else {
            this.messageBox("P0005"); //ִ�гɹ�
        }
        this.onQuery();
    }
    /**
     * ѡ������������table
     */
    public void onSelect() {
        this.onQuery();
    }
    /**
     *�ɻ�
     */
    public void returnback() {
    	int selectrow=tableM.getSelectedRow();
        if (selectrow < 0) {
            this.messageBox("E0012");
            return;
        }
        //ȡtable�����ݣ�ת��TParm
        TParm parm = (TParm)this.callFunction("UI|TABLE1|getParmValue");
        TParm datat = new TParm();
        datat.setRowData( -1, parm, selectrow);
        if (checkout(datat)) {
            this.messageBox("�˹�̨��δ����");
            return;
        }
        this.openDialog("%ROOT%\\config\\bil\\BILRecipientsReturn.x", datat);
        this.onQuery();
    }

    /**
     * ����Ʊ��
     */
    public void onAdjustment() {
    	int selectrow=tableM.getSelectedRow();
        if (selectrow < 0) {
            this.messageBox("E0012");
            return;
        }
        TParm parm = (TParm)this.callFunction("UI|TABLE1|getParmValue");
        TParm datat = new TParm();
        datat.setRowData( -1, parm, selectrow);
        this
            .openDialog("%ROOT%\\config\\bil\\BILAdjustmentRecipients.x",
                        datat);
        this.onQuery();
    }
}
