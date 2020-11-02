package com.javahis.ui.udd;

import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.javahis.ui.sys.SYSDeptControl;
/**
 * <p>
 * Title: Ƥ�������޸�
 * </p>
 * 
 * <p>
 * Description: סԺ��ҩ����Ƥ��ҩƷ�����޸�Ƥ������
 * </p>
 * 
 * <p>
 * Copyright: JAVAHIS
 * </p>
 * 
 * @author pangben 2015-4-13
 * @version 1.0
 */
public class UDDSkiResultControl extends TControl{
	private TParm parmmeter;//���ܻ�ʿִ�н���Ĳ���
	private String caseNo = "";//�����
	private String orderCode = "";//ҩ������
	//private String phl="";//add caoyong 2014/03/27����ִ��
	private String batchNo;
	private String orderNo;
	private String orderSeq;
	private String orgCode; //add wukai on 20170412 ִ�п���
	/**
	 * ��ʼ������
	 */
	public void onInit() {
		this.parmmeter = new TParm();
		Object obj = this.getParameter();
		if (obj.toString().length() != 0 || obj != null) {
			this.parmmeter = (TParm) obj;
			this.setValue("BATCH_NO", parmmeter.getValue("BATCH_NO"));//��ʼ������
			//this.setValue("SKINTEST_NOTE", parmmeter.getValue("SKINTEST_FLG"));//��ʼ��Ƥ�Խ��
		}
		//phl=parmmeter.getValue("PHL");//add caoyong 2014/03/27����ִ��
		caseNo = parmmeter.getValue("CASE_NO");
		orderCode = parmmeter.getValue("ORDER_CODE");
		batchNo= parmmeter.getValue("BATCH_NO");
		orderNo=parmmeter.getValue("ORDER_NO");
		orderSeq=parmmeter.getValue("ORDER_SEQ");
		orgCode = parmmeter.getValue("ORG_CODE");
		//Ƥ�Խ�� add caoyong 2014/4/3 end 
	}
	/**
	 * Ƥ�������޸�
	 */
	public void onFecth() {
		TParm result = new TParm();
		String skiNo = this.getValueString("BATCH_NO");// ��ȡ�����Ƥ������
		if (skiNo.equals(null) || "".equals(skiNo)) {
			this.messageBox("Ƥ�����Ų���Ϊ�ա�");
			return;
		}
		
		//add by wukai on 20170412 ��� ����У�� start
		String sql = "SELECT BATCH_NO FROM IND_STOCK WHERE ORG_CODE = '" + orgCode + "' AND ORDER_CODE = '" + orderCode + "'";
		
		String sql1 = sql + " AND BATCH_NO = '" + skiNo + "'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql1));
		
		if(parm == null || parm.getErrCode() < 0 || parm.getCount("BATCH_NO") <= 0) {
			parm = new TParm(TJDODBTool.getInstance().select(sql));
			StringBuilder sb = new StringBuilder();
			for(int i = 0; i < parm.getCount("BATCH_NO"); i ++) {
				sb.append(parm.getValue("BATCH_NO", i) + "  ");
			}
			this.messageBox_("��ҩƷ��治�������ţ���" + skiNo + "��\n���õ������У�" + sb.toString());
			return;
		}
		//add by wukai on 20170412 ��� ����У�� end
		
		if (!checkPW()) {
			return;
		}
		result.addData("BATCH_NO", skiNo);
		this.setReturnValue(result);
		TParm inParm = new TParm();
		inParm.setData("ORDER_CODE", orderCode);
		inParm.setData("BATCH_NO", skiNo);
		inParm.setData("CASE_NO", caseNo);
		inParm.setData("ORDER_NO", orderNo);//
		inParm.setData("ORDER_SEQ", orderSeq);//
		inParm.setData("OPT_USER", Operator.getID());//
		inParm.setData("OPT_TERM", Operator.getIP());//
		// ����actionִ������
		TParm result1 = TIOM_AppServer.executeAction(
				"action.inw.InwOrderExecAction", "insterUddSkinNote", inParm);
		if (result1.getErrCode() < 0) {
			this.messageBox(result1.getErrText());
			return;
		}
		this.closeWindow();
	}
	/**
     * ����������֤
     * 
     * @return boolean
     */
    public boolean checkPW() {
        String inwExe = "inwExe";
        String value = (String) this.openDialog(
                "%ROOT%\\config\\inw\\passWordCheck.x", inwExe);
        if (value == null) {
            return false;
        }
        return value.equals("OK");
    }
}
