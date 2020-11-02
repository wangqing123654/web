package com.javahis.ui.ins;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import jdo.adm.ADMInpTool;
import jdo.sys.Pat;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
/**
 * 
 * <p>
 * Title:סԺ�ʸ�ȷ�������غͿ���
 * </p>
 * 
 * <p>
 * Description:סԺ�ʸ�ȷ�������غͿ���:סԺδ�᰸
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
 * @author pangb 2011-11-25
 * @version 2.0
 */
public class INSAdmNCloseControl  extends TControl{
	private int selectrow = -1;//ѡ�����
	SimpleDateFormat df1 = new SimpleDateFormat("yyyyMMdd");
	String flg="";
	public void onInit() {
		super.onInit();
		//�õ�ǰ̨���������ݲ���ʾ�ڽ�����
		TParm recptype = (TParm) getParameter();
		setValueForParm("REGION_CODE;DEPT_CODE", recptype, -1);
		//================pangben 2012-6-18 start 
		if (null!=recptype.getValue("MR_NO") && recptype.getValue("MR_NO").length()>0) {
			this.setValue("MR_NO", recptype.getValue("MR_NO"));
		}
		flg=recptype.getValue("FLG");//��ʾ���� SQL �޸�����
		if (null!=flg && flg.equals("Y")) {
			this.setTitle("��Ժ������Ϣ��ѯ");
		}
		   //table1�ĵ��������¼�
        callFunction("UI|TABLE|addEventListener",
                     "TABLE->" + TTableEvent.CLICKED, this, "onTableClicked");
        //table1�ĵ��������¼�
        callFunction("UI|TABLE|addEventListener",
                     "TABLE->" + TTableEvent.DOUBLE_CLICKED, this,
                     "onTableDoubleClicked");
        //Ԥ�����ʱ���
        //DateFormat df = new SimpleDateFormat("yyyy");
       // String date=df.format(SystemTool.getInstance().getDate())+"-01-01";
        this.callFunction("UI|STARTTIME|setValue",
        		 SystemTool.getInstance().getDate());
        this.callFunction("UI|ENDTIME|setValue",
                          SystemTool.getInstance().getDate());
        onQuery();
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
	 * ��ѯ����
	 */
	public void onQuery(){
		TParm parm=new TParm();
		//����
		if(this.getValue("REGION_CODE").toString().length()>0){
			parm.setData("REGION_CODE",this.getValue("REGION_CODE"));	
		}
		//��������
		if(this.getValueString("MR_NO").length()>0){
			parm.setData("MR_NO",this.getValue("MR_NO"));	
		}
		//����
		if(this.getValueString("DEPT_CODE").length()>0){
			parm.setData("DEPT_CODE",this.getValue("DEPT_CODE"));	
		}
		//����
		if (this.getValueString("STATION_CODE").length()>0) {
			parm.setData("STATION_CODE",this.getValue("STATION_CODE"));	
		}
		//��ʼʱ��
		if(null!=this.getValue("STARTTIME")){
			parm.setData("START_DATE",df1.format(getValue("STARTTIME"))+"000000");	
		}
		//����ʱ��
		if(null!=this.getValue("ENDTIME")){
			parm.setData("END_DATE",df1.format(getValue("ENDTIME"))+"235959");	
		}
		TParm result=null;
		//=========pangben 2012-6-18 start ���÷ָ��ѯ������Ϣȷ��Ψһ����
		if (null!=flg && flg.equals("Y")) {
			result=ADMInpTool.getInstance().queryAdmNCloseInsBalance(parm);
		}else{
			result=ADMInpTool.getInstance().queryAdmNClose(parm);
		}
		//=========pangben 2012-6-18 stop
		if(result.getErrCode()<0 ){
			this.messageBox("E0005");
			err(result.getErrText()+":"+result.getErrName());
			return;
		}
		if(result.getCount()<=0){
			this.messageBox("E0008");
			TTable table = (TTable) this.callFunction("UI|TABLE|getThis");
			table.removeRowAll();
			return;
		}
		this.callFunction("UI|TABLE|setParmValue", result);
	}
	/**
     * �������ı���س��¼�
     */
    public void onMrNo() {
//		TParm parm = getTableSeleted();
//		if (null == parm) {
//			return;
//		}
        Pat pat = Pat.onQueryByMrNo(TypeTool.getString(getValue("MR_NO")));
        if (pat == null) {
            this.messageBox("�޴˲�����!");
            return;
        }
       // this.setValue("PAT_NAME", pat.getName());
        this.setValue("MR_NO", pat.getMrNo());
        //TParm result = INSIbsTool.getInstance().queryIbsSum(parm);// ��ѯ���ݸ����渳ֵ
        //setSumValue(result, parm);
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
