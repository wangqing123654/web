package com.javahis.ui.opd;

import java.sql.Timestamp;

import jdo.odo.OdoCaseQuatityStaticTool;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;
import jdo.sys.SYSRegionTool;
/**
 * <p>Title: ������ͳ�Ʊ�</p>
 *
 * <p>Description: ������ͳ�Ʊ�</p>
 *
 * <p>Copyright: Copyright JavaHis (c) 2009��1��</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author ehui 20091015
 * @version JavaHis 1.0
 */
public class OdoCaseQuatityStasticControl extends TControl {
	//�������͡��ż�ס�����򡢿��ҡ�ҽʦCOMBO
	private TComboBox staType,admType,regionCode;
	private TTextFormat startTime,endTime;
	private TTable table;
	/**
	 * ��ʼ��
	 */
	public void onInit() {
		// ��ʼ��
		super.onInit();
		//��ʼ���ؼ�
		initComponent();
		//���
		onClear();
                //================pangben modify 20110406 start ��������
                setValue("REGION_CODE", Operator.getRegion());
                //================pangben modify 20110406 stop
                //========pangben modify 20110421 start Ȩ�����
                TComboBox cboRegion = (TComboBox)this.getComponent("REGION_CODE");
                cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(this.
                        getValueString("REGION_CODE")));
                //===========pangben modify 20110421 stop

	}
	/**
	 * ��ʼ���ؼ�
	 */
	private void initComponent() {
		staType=(TComboBox)this.getComponent("STA_TYPE");
		admType=(TComboBox)this.getComponent("ADM_TYPE");
		regionCode=(TComboBox)this.getComponent("REGION_CODE");
		startTime=(TTextFormat)this.getComponent("START_TIME");
		endTime=(TTextFormat)this.getComponent("END_TIME");
		Timestamp today=TJDODBTool.getInstance().getDBTime();
                //=============pangben modify 20110412 start �޸ĳ���ʱ����ʾ��ʽ
		String todayStr=StringTool.getString(today,"yyyyMMdd");
		//today=StringTool.getTimestamp(todayStr,"yyyyMMddHHmmss");
		this.setValue("START_TIME", todayStr);
		this.setValue("END_TIME",todayStr);
                this.setValue("E_TIME", StringTool.getString(today,"HH:mm:ss"));
                 //=============pangben modify 20110412 stop
                if("en".equals(this.getLanguage())){
                    staType.setStringData("[[id,name],[MED,RX],[CTRL,Poison],[CHN,Herbal],[INFAINT,Paediatric RX],[OP,Treatment],[EXA,LIS/RIS]]");
                    admType.setStringData("[[id,name],[,],[O,O],[E,E]]");
                }else{
                    staType.setStringData("[[id,name,py1],[MED,����ǩ,CFQ],[CTRL,���ƴ���,GZCF],[CHN,��ҩ����,CYCF],[INFAINT,��ͯ����,ETCF],[OP,����,CZ],[EXA,����,JY]]");
                    admType.setStringData("[[id,name,py1],[,,],[O,����,MZ],[E,����,JZ]]");
                }
		table=(TTable)this.getComponent("TABLE");
                //=============pangben modify 20110418 ���������ʾ
                table.setHeader("����,120;�ż������,100;����,160,DEPT_CODE;��Ա,160,DR_CODE;����,100,double;���,100,double");
                table.setItem("DEPT_CODE;DR_CODE");
	}
	/**
	 * ����¼�
	 */
	public void onClear() {
		staType.setSelectedID("MED");
                //===================pangben modify 20110406 start ���ó�ʼ����
		regionCode.setSelectedID(Operator.getRegion());
                //===================pangben modify 20110406 stop
		admType.setSelectedID("");
		this.clearValue("DEPT_CODE;DR_CODE");
		Timestamp today=TJDODBTool.getInstance().getDBTime();
		String todayStr=StringTool.getString(today,"yyyyMMdd")+"0000";
		today=StringTool.getTimestamp(todayStr,"yyyyMMddHHmm");
		this.setValue("START_TIME", today);
		this.setValue("END_TIME", TJDODBTool.getInstance().getDBTime());
		this.setValue("TOT_AMT", "");
		table.removeRowAll();
	}
	/**
	 * ��ѯ
	 */
	public void onQuery(){
		TParm parm=getParam();
		if(parm==null){
			this.messageBox_("��ѯʧ��");
			table.removeRowAll();
			return;
		}
		TParm result=OdoCaseQuatityStaticTool.getInstance().onQuery(parm);
		if(result.getErrCode()<0){
			this.messageBox_("��ѯʧ�� ");
			table.removeRowAll();
			return;
		}
		int count=result.getCount();
		if(count<1){
			this.messageBox_("û������");
			table.removeRowAll();
			return;
		}
		double tot=0.0;
                String lan = this.getLanguage();
		for(int i=0;i<count;i++){
			double tempTot=TypeTool.getDouble( result.getData("AR_AMT",i));
			tot+=tempTot;
                        if("zh".equals(lan)){
                            if(result.getValue("ADM_TYPE",i).equals("O"))
                                result.setData("ADM_TYPE",i,"����");
                            else if(result.getValue("ADM_TYPE",i).equals("E"))
                                result.setData("ADM_TYPE",i,"����");
                        }
		}
		this.setValue("TOT_AMT", StringTool.round(tot, 2));
		table.setParmValue(result);
	}
	/**
	 * ���EXCEL
	 */
	public void onExcel(){

		if(table==null){
			this.messageBox_("û������");
			return;
		}
		TParm value=table.getParmValue();
		if(value==null||value.getCount()<1){
			this.messageBox_("û������");
			return;
		}
		ExportExcelUtil.getInstance().exportExcel(table, "");
	}
	/**
	 * ��ӡ�¼�
	 */
	public void onPrint(){
		TParm parm=getParam();
		if(parm==null){
			this.messageBox_("��ӡʧ��");
			return;
		}
		if(table.getParmValue().getCount()<=0){
			this.messageBox_("û������");
			return;
		}
		String staTypeStr=staType.getSelectedID();
		String title="";
		if("INFAINT".equalsIgnoreCase(staTypeStr)){
			title="�ż����ͯ������ͳ�Ʊ���";
		}else if("MED".equalsIgnoreCase(staTypeStr)){
			title="�ż��ﴦ����ͳ�Ʊ���";
		}
		else if("CTRL".equalsIgnoreCase(staTypeStr)){
			title="�ż������ҩƷ������ͳ�Ʊ���";
		}
		else if("CHN".equalsIgnoreCase(staTypeStr)){
			title="�ż����ҩ������ͳ�Ʊ���";
		}
		else if("OP".equalsIgnoreCase(staTypeStr)){
			title="�ż��ﴦ�ô�����ͳ�Ʊ���";
		}
		else if("EXA".equalsIgnoreCase(staTypeStr)){
			title="�ż�������鴦����ͳ�Ʊ���";
		}
		TParm inParm=new TParm();
		inParm.setData("TITLE","TEXT",title);
		String regionDesc=regionCode.getSelectedName();
                //=============pangben modify 20110418 strat
		inParm.setData("REGION","TEXT",regionDesc.equals("")||regionDesc==null?"����ҽԺ":regionDesc);
                //=============pangben modify 20110418 stop
		Timestamp startTimeT=(Timestamp)startTime.getValue();
                //=====pangben modify 20110412 start �޸Ĳ���ʱ����ʾ��ʽ�õ�ʱ������
		String startTimeStr=StringTool.getString(startTimeT,"yyyy/MM/dd ")+"00:00:00";
		Timestamp endTimeT=(Timestamp)endTime.getValue();
		String endTimeStr=StringTool.getString(endTimeT,"yyyy/MM/dd ")+this.getValueString("E_TIME");
                 //=====pangben modify 20110412 stop
		String time="ͳ�����䣺"+startTimeStr+" ~ "+endTimeStr;
		inParm.setData("TIME","TEXT",time);
		inParm.setData("OPT_USER","TEXT","�Ʊ���:"+StringUtil.getDesc("SYS_OPERATOR", "USER_NAME", "USER_ID='" +Operator.getID()+"'"));
		String sql=OdoCaseQuatityStaticTool.getInstance().getSql(parm);
		inParm.setData("TOT","�ϼƣ�"+this.getValueDouble("TOT_AMT"));
//		this.messageBox_(this.getValueDouble("TOT_AMT"));
//		this.messageBox_(this.getValueString("TOT_AMT"));
		inParm.setData("TABLE_NEW","SQL",sql);
//		// System.out.println("tot:"+inParm.getValue("TOT"));
		openPrintWindow("%ROOT%\\config\\prt\\OPD\\SheetQuatityStatistic.jhw",inParm,false);


	}
	/**
	 * ȡ�ò�ѯ����
	 * @return
	 */
	private TParm getParam(){
		TParm parm=new TParm();
		String staTypeStr=staType.getSelectedID();
		String admTypeStr=admType.getSelectedID();
		String regionStr=regionCode.getSelectedID();
		String deptCodeStr=this.getValueString("DEPT_CODE");

		String drCodeStr=this.getValueString("DR_CODE");
		Object o1=startTime.getValue();
		Object o2=endTime.getValue();
		if(o1==null){
			this.messageBox_("��ʼʱ�䲻��Ϊ��");
			return null;
		}
		if(o2==null){
			this.messageBox_("����ʱ�䲻��Ϊ��");
			return null;
		}
		Timestamp startTimeT=(Timestamp)startTime.getValue();
                //=====pangben modify 20110412 start �޸Ĳ���ʱ����ʾ��ʽ�õ�ʱ������
		String startTimeStr=StringTool.getString(startTimeT,"yyyyMMdd")+"000000";
		Timestamp endTimeT=(Timestamp)endTime.getValue();
		String endTimeStr=StringTool.getString(endTimeT,"yyyyMMdd")+this.getValueString("E_TIME").replace(":","");
                //=====pangben modify 20110412 stop
		if(!StringUtil.isNullString(regionStr)){
			parm.setData("REGION_CODE","REGION_CODE='"+regionStr+"'");
		}
		if(!StringUtil.isNullString(admTypeStr)){
			parm.setData("ADM_TYPE","ADM_TYPE='"+admTypeStr+"'");
		}
		if(!StringUtil.isNullString(deptCodeStr)){
			parm.setData("DEPT_CODE","DEPT_CODE='"+deptCodeStr+"'");
		}
		if(!StringUtil.isNullString(drCodeStr)){
			parm.setData("DR_CODE","DR_CODE='"+drCodeStr+"'");
		}
		if("INFAINT".equalsIgnoreCase(staTypeStr)){
			parm.setData("INFAINT","(PRINTTYPEFLG_INFANT='Y' AND (RX_TYPE='1' OR RX_TYPE='2'))");
		}else if("MED".equalsIgnoreCase(staTypeStr)){
			parm.setData("RX_TYPE","RX_TYPE='1'");
		}
		else if("CTRL".equalsIgnoreCase(staTypeStr)){
			parm.setData("RX_TYPE","RX_TYPE='2'");
		}
		else if("CHN".equalsIgnoreCase(staTypeStr)){
			parm.setData("RX_TYPE","RX_TYPE='3'");
		}
		else if("OP".equalsIgnoreCase(staTypeStr)){
			parm.setData("RX_TYPE","RX_TYPE='4'");
		}
		else if("EXA".equalsIgnoreCase(staTypeStr)){
			parm.setData("RX_TYPE","RX_TYPE='5'");
		}
		parm.setData("START_TIME",startTimeStr);
		parm.setData("END_TIME",endTimeStr);
		return parm;
	}
        /**
         * ����ѡ���¼�
         */
        public void onDEPT_CODE(){
            this.clearValue("DR_CODE");
        }
}
