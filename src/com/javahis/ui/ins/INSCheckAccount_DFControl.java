package com.javahis.ui.ins;

import jdo.ins.InsManager;
import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
/**
 * <p>Title: ҽ���渶����</p>
 *
 * <p>Description: ҽ���渶����</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author zhangp 20120211
 * @version 1.0
 */
public class INSCheckAccount_DFControl extends TControl{
	/**
     * ��ʼ������
     */
    public void onInit() {
    	setValue("START_DATE", SystemTool.getInstance().getDate());
		setValue("END_DATE", SystemTool.getInstance().getDate());
    }
    /**
     * ��ѯ
     */
    public void onQuery(){
    	String startDate = "";
    	String endDate = "";
    	if (!"".equals(this.getValueString("START_DATE")) &&
	            !"".equals(this.getValueString("END_DATE"))) {
			startDate = getValueString("START_DATE").substring(0, 19);
			endDate = getValueString("END_DATE").substring(0, 19);
			startDate = startDate.substring(0, 4) + startDate.substring(5, 7) +
			startDate.substring(8, 10)+"000000";
			endDate = endDate.substring(0, 4) + endDate.substring(5, 7) +
			endDate.substring(8, 10)+"235959";
		}else{
			messageBox("�������ѯ����");
			return;
		}
    	String sql =
    		"SELECT I.ADM_SEQ, I.PAT_NAME,I.TOT_AMT, I.NHI_AMT, I.OWN_AMT, I.ADD_AMT, I.UPLOAD_DATE " +
    		" FROM INS_ADVANCE_PAYMENT I " +
    		" WHERE I.UPLOAD_DATE BETWEEN TO_DATE('"+startDate+"','YYYYMMDDhh24miss') " +
    		" AND TO_DATE('"+endDate+"','YYYYMMDDhh24miss') " +
    		" AND I.INS_STATUS = '3'";
    	TParm result = new TParm(TJDODBTool.getInstance().select(sql));
    	if(result.getErrCode()<0){
    		messageBox(result.getErrText());
    		return;
    	}
    	if(result.getCount()<0){
    		messageBox("��������");
    	}
    	this.callFunction("UI|TABLE1|setParmValue", result);
    }
    /**
     * ������
     */
    public void onCheckAll(){
    	String startDate = "";
    	String endDate = "";
    	if (!"".equals(this.getValueString("START_DATE")) &&
	            !"".equals(this.getValueString("END_DATE"))) {
			startDate = getValueString("START_DATE").substring(0, 19);
			endDate = getValueString("END_DATE").substring(0, 19);
			startDate = startDate.substring(0, 4) + startDate.substring(5, 7) +
			startDate.substring(8, 10)+"000000";
			endDate = endDate.substring(0, 4) + endDate.substring(5, 7) +
			endDate.substring(8, 10)+"235959";
		}else{
			messageBox("�������ѯ����");
			return;
		}
    	TParm result = queryCheckAll(startDate, endDate);
    	if(result.getErrCode()<0){
    		messageBox(result.getErrText());
    		return;
    	}
    	if(result.getCount()<0){
    		messageBox("������");
    		return;
    	}
    	TParm regionParm = SYSRegionTool.getInstance().selectdata(Operator.getRegion());
    	String hospital =  regionParm.getData("NHI_NO", 0).toString();//��ȡHOSP_NHI_NO
    	TParm parm = new TParm();
    	//����
    	parm.addData("HOSP_NHI_NO", hospital);//ҽԺ����
		parm.addData("START_DATE", startDate);//��ʼʱ��
		parm.addData("END_DATE", endDate);//����ʱ��
		parm.addData("CTZ_TYPE", "21");//��ҽ���
		parm.addData("TOTAL_AMT", result.getData("SUM(A.TOT_AMT)", 0));//�������
		parm.addData("TOTAL_NHI_AMT", result.getData("SUM(A.NHI_AMT)", 0));//�걨���
		parm.addData("OWN_AMT", result.getData("SUM(A.OWN_AMT)", 0));//�Էѽ��
		parm.addData("ADDPAY_AMT", result.getData("SUM(A.ADD_AMT)", 0));//�������
		parm.addData("ALL_TIME", result.getData("COUNT(A.TOT_AMT)", 0));//���˴�
			parm.setData("PIPELINE", "DataDown_czys");
			parm.setData("PLOT_TYPE", "S");
			parm.addData("PARM_COUNT", 9);
		result = InsManager.getInstance().safe(parm);//ҽ�����ӿڷ���
		messageBox(result.getValue("PROGRAM_MESSAGE"));
    }
    /**
     * �����˲�ѯ
     * @param startDate
     * @param endDate
     * @return
     */
    public TParm queryCheckAll(String startDate,String endDate){
    	String sql =
    		"SELECT SUM(A.TOT_AMT),SUM(A.NHI_AMT),SUM(A.OWN_AMT),SUM(A.ADD_AMT),COUNT(A.TOT_AMT) " +
    		" FROM INS_ADVANCE_PAYMENT A " +
    		" WHERE A.UPLOAD_DATE BETWEEN TO_DATE('"+startDate+"','YYYYMMDDhh24miss') " +
    		" AND TO_DATE('"+endDate+"','YYYYMMDDhh24miss') " +
    		" AND A.INS_STATUS = '3' ";
    	TParm result = new TParm(TJDODBTool.getInstance().select(sql));
    	return result;
    }
    /**
     * ����ϸ��
     */
    public void onCheckDetailAccnt(){
    	String startDate = "";
    	String endDate = "";
    	if (!"".equals(this.getValueString("START_DATE")) &&
	            !"".equals(this.getValueString("END_DATE"))) {
			startDate = getValueString("START_DATE").substring(0, 19);
			endDate = getValueString("END_DATE").substring(0, 19);
			startDate = startDate.substring(0, 4) + startDate.substring(5, 7) +
			startDate.substring(8, 10)+"000000";
			endDate = endDate.substring(0, 4) + endDate.substring(5, 7) +
			endDate.substring(8, 10)+"235959";
		}
    	onQuery();//��ѯtable1
    	TParm parm = new TParm();
    	TParm regionParm = SYSRegionTool.getInstance().selectdata(Operator.getRegion());
    	String hospital =  regionParm.getData("NHI_NO", 0).toString();//��ȡHOSP_NHI_NO
    	//����
    	parm.setData("PIPELINE", "DataDown_czyd");
		parm.setData("PLOT_TYPE", "N");
		parm.addData("HOSP_NHI_NO", hospital);//ҽԺ����
		parm.addData("START_DATE", startDate);//��ʼʱ��
		parm.addData("END_DATE", endDate);//����ʱ��
		parm.addData("CTZ_TYPE", "21");//��ҽ���
		parm.addData("ADVANCE_TYPE", "01"); //�渶���
		parm.addData("PARM_COUNT", 5);
		TParm result = InsManager.getInstance().safe(parm,"");//ҽ�����ӿڷ���(����)
		if(result.getErrCode()<0){
			messageBox(result.getErrText());
			return;
		}
		this.callFunction("UI|TABLE2|setParmValue", result);
		countDetail();
    }
    /**
     * ������ϸ�˲��
     */
    public void countDetail(){
    	TTable table1 = (TTable)this.getComponent("TABLE1");//TABLE1
    	TTable table2 = (TTable)this.getComponent("TABLE2");//TABLE2
    	if(table1.getParmValue()==null||table2.getParmValue()==null){
    		messageBox("�������ݲ���Ϊ��");
    		return;
    	}
//    	ADM_SEQ;PAT_NAME;TOT_AMT;NHI_AMT;OWN_AMT;ADD_AMT;UPLOAD_DATE
//    	CONFIRM_NO;NAME;TOTAL_AMT;TOTAL_NHI_AMT;OWN_AMT;ADDPAY_AMT
    	TParm tableParm1 = table1.getParmValue();
    	TParm tableParm2 = table2.getParmValue();
    	TParm parm = new TParm();
    	for (int i = 0; i < table1.getRowCount(); i++) {
    	      String admSeqLocal = tableParm1.getData("ADM_SEQ", i).toString();
    	      boolean canfind = false;
    	      for(int j = 0;j < table2.getRowCount();j++){
    	        String admSeqCenter = tableParm2.getData("CONFIRM_NO", j).toString();
    	        if(!admSeqLocal.equals(admSeqCenter))
    	          continue;
    	        canfind = true;
    	        //���ؽ��
    	        double totAmtLocal = tableParm1.getDouble("TOT_AMT", i);//�������
    	        double nhiAmtLocal = tableParm1.getDouble("NHI_AMT", i);//�걨���
    	        double ownAmtLocal = tableParm1.getDouble("OWN_AMT", i);//ȫ�Էѽ��
    	        double addAmtLocal = tableParm1.getDouble("ADD_AMT", i);//�������
    	        //���Ķ˽��
    	        double totAmtCenter = tableParm2.getDouble("TOTAL_AMT", j);//�������
    	        double nhiAmtCenter = tableParm2.getDouble("TOTAL_NHI_AMT", j);//�걨���
    	        double ownAmtCenter = tableParm2.getDouble("OWN_AMT", j);//ȫ�Էѽ��
    	        double addAmtCenter = tableParm2.getDouble("ADDPAY_AMT", j);//�������
    	        if(totAmtLocal != totAmtCenter ||
    	                nhiAmtLocal != nhiAmtCenter ||
    	                ownAmtLocal != ownAmtCenter ||
    	                addAmtLocal != addAmtCenter ){
    	               parm.addData("STATUS_ONE", "Y");
    	               parm.addData("STATUS_TWO", "N");
    	               parm.addData("STATUS_THREE", "N");
    	               parm.addData("ADM_SEQ",tableParm1.getData("ADM_SEQ", i));
    	               parm.addData("NAME",tableParm1.getData("PAT_NAME", i));
    	               parm.addData("TOT_AMT_LOCAL",tableParm1.getData("TOT_AMT", i));
    	               parm.addData("TOT_AMT_CENTER",tableParm2.getData("TOTAL_AMT", j));
    	               parm.addData("NHI_AMT_LOCAL",tableParm1.getData("NHI_AMT", i));
    	               parm.addData("NHI_AMT_CENTER",tableParm2.getData("TOTAL_NHI_AMT", j));
    	               parm.addData("OWN_AMT_LOCAL",tableParm1.getData("OWN_AMT", i));
    	               parm.addData("OWN_AMT_CENTER",tableParm2.getData("OWN_AMT", j));
    	               parm.addData("ADD_AMT_LOCAL",tableParm1.getData("ADD_AMT", i));
    	               parm.addData("ADD_AMT_CENTER",tableParm2.getData("ADDPAY_AMT", j));
    	             }
    	      }
    	      if(!canfind){
    	          parm.addData("STATUS_ONE", "N");
    	          parm.addData("STATUS_TWO", "Y");
    	          parm.addData("STATUS_THREE", "N");
	              parm.addData("ADM_SEQ",tableParm1.getData("ADM_SEQ", i));
	              parm.addData("NAME",tableParm1.getData("PAT_NAME", i));
    	          parm.addData("TOT_AMT_LOCAL",tableParm1.getData("TOT_AMT", i));
    	          parm.addData("TOT_AMT_CENTER",0);
    	          parm.addData("NHI_AMT_LOCAL",tableParm1.getData("NHI_AMT", i));
    	          parm.addData("NHI_AMT_CENTER",0);
    	          parm.addData("OWN_AMT_LOCAL",tableParm1.getData("OWN_AMT", i));
    	          parm.addData("OWN_AMT_CENTER",0);
    	          parm.addData("ADD_AMT_LOCAL",tableParm1.getData("ADD_AMT", i));
    	          parm.addData("ADD_AMT_CENTER",0);
    	        }
		}
//    	ADM_SEQ;PAT_NAME;TOT_AMT;NHI_AMT;OWN_AMT;ADD_AMT;UPLOAD_DATE
//    	CONFIRM_NO;NAME;TOTAL_AMT;TOTAL_NHI_AMT;OWN_AMT;ADDPAY_AMT
    	for(int i = 0;i < table2.getRowCount();i++){
    	      String confirmNoCenter = tableParm2.getData("CONFIRM_NO", i).toString();
    	      boolean canfind = false;
    	      for (int j = 0; j < table1.getRowCount(); j++) {
    	        String confirmNoLocal = tableParm1.getData("ADM_SEQ", i).toString();
    	        if (!confirmNoLocal.equals(confirmNoCenter))
    	          continue;
    	        canfind = true;
    	      }
    	      if(!canfind){
    	        parm.addData("STATUS_ONE", "N");
    	        parm.addData("STATUS_TWO", "N");
    	        parm.addData("STATUS_THREE", "Y");
    	        parm.addData("ADM_SEQ",tableParm2.getData("CONFIRM_NO", i));
    	        parm.addData("NAME",tableParm2.getData("NAME", i));
    	        parm.addData("TOT_AMT_LOCAL",0);
    	        parm.addData("TOT_AMT_CENTER",tableParm2.getData("TOTAL_AMT", i));
    	        parm.addData("NHI_AMT_LOCAL",0);
    	        parm.addData("NHI_AMT_CENTER",tableParm2.getData("TOTAL_NHI_AMT", i));
    	        parm.addData("OWN_AMT_LOCAL",0);
    	        parm.addData("OWN_AMT_CENTER",tableParm2.getData("OWN_AMT", i));
    	        parm.addData("ADD_AMT_LOCAL",0);
    	        parm.addData("ADD_AMT_CENTER",tableParm2.getData("ADDPAY_AMT", i));
    	      }
    	    }
    	    if(parm.getCount("ADM_SEQ") <= 0){
    	    	messageBox("����ϸ�ʳɹ�");
    	    	return;
    	    }
    	    TParm reParm = (TParm)this.openDialog(
    	            "%ROOT%\\config\\ins\\INSCheckAccount_DFDetail.x", parm);
    }
    /**
     * ���
     */
    public void onclear(){
    	clearValue("START_DATE;END_DATE");
    }

}
