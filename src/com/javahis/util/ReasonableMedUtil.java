package com.javahis.util;

import jdo.odo.ODO;
import jdo.odo.OpdOrder;
import jdo.sys.Operator;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TDS;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.util.StringTool;

public class ReasonableMedUtil {
	public static final String NULL="";
	public static final String GET_SEX_DESC=" SELECT SEX_DESC FROM SYS_SEX A,SYS_PATINFO B WHERE A.MR_NO=B.MR_NO AND B.MR_NO=";
	public static final String GET_FREQ="SELECT CYCLE,FREQ_TIMES FROM SYS_PHAFREQ ";
	public static final String GET_UNIT="SELECT UNIT_CODE,UNIT_CHN_DESC FROM SYS_UNIT ";
	public static final String GET_ROUTE="SELECT ROUTE_CODE,ROUTE_CHN_DESC FROM SYS_PHAROUTE";
	public  TDS tdsFreq,tdsUnit,tdsRoute;
	 /**
	   * ���벡�˻�����Ϣ
	   * ���ã����˵Ļ�����Ϣ�����仯֮�󣬵��øýӿڡ�
	   * @param PatientID String ���˲�����ţ����봫ֵ��
	   * @param VisitID String ��ǰ������������봫ֵ��
	   * @param Name String �������� �����봫ֵ��
	   * @param Sex String �����Ա� �����봫ֵ���磺�С�Ů������ֵ����δ֪��
	   * @param Birthday String �������� �����봫ֵ����ʽ��2005-09-20
	   * @param Weight String ���� �����Բ���ֵ����λ��KG
	   * @param Height String ��� �����Բ���ֵ����λ��CM
	   * @param DepartmentName String ҽ������ID/ҽ���������� �����Բ���ֵ��
	   * @param Doctor String ����ҽ��ID/����ҽ������ �����Բ���ֵ��
	   * @param LeaveHospitalDate String ��Ժ���� �����Բ���ֵ��
	   * @return int ��������
	   */
	public static String[] getPatInfo(ODO odo){
		if(odo==null)
			return null;
		String[] result=new String[10];
		result[0]=odo.getMrNo();
		result[1]="1";
		result[2]=odo.getPatInfo().getItemString(0, "PAT_NAME");
		TParm parmSex=new TParm(TJDODBTool.getInstance().select(GET_SEX_DESC+"'" +odo.getMrNo()+
				"'"));
		result[3]=parmSex.getValue("SEX_DESC");
		result[4]=TCM_Transform.getString(odo.getPatInfo().getItemData(0, "BIRTH_DATE"), "yyyy-MM-dd");
		result[5]=NULL;
		result[6]=NULL;
		result[7]=NULL;
		result[8]=NULL;
		result[9]=NULL;
		return result;
	}
	  /**
	   * ���벡����ҩ��Ϣ
	   * ���ã�����Ҫ������ҩҽ�����ʱ�����øýӿڡ�
	   * ע����������ǰ�����ж�����ҩҽ��ʱ��ѭ�����롣�����ҽ��Ϊ��ҩҽ����
	   * ���ڹ���վ����Ϊ10��ʱ�����ʱ(�磺סԺҽ��վ������ҽ��վ)��
	   * ��ҩ�������ڿ��Բ��ô�ֵ��Ĭ��Ϊ���죻�����ڹ���վ����Ϊ20�ع������ʱ(�磺�ٴ�ҩѧ�������ѯͳ��)��
	   * ��ҩ�������ڱ��봫ֵ��
	   * @param OrderUniqueCode String ҽ��Ψһ�루���봫ֵ�� 0
	   * @param DrugCode String ҩƷ���� �����봫ֵ�� 1
	   * @param DrugName String ҩƷ���� �����봫ֵ�� 2
	   * @param SingleDose String ÿ������ �����봫ֵ�� 3
	   * @param DoseUnit String ������λ �����봫ֵ�� 4
	   * @param Frequency String ��ҩƵ��(��/��)�����봫ֵ�� 5
	   * @param StartOrderDate String ��ҩ��ʼ���ڣ���ʽ��yyyy-mm-dd �����봫ֵ�� 6
	   * @param StopOrderDate String ��ҩ�������ڣ���ʽ��yyyy-mm-dd �����Բ���ֵ����Ĭ��ֵΪ���� 7
	   * @param RouteName String ��ҩ;���������� �����봫ֵ�� 8
	   * @param GroupTag String ����ҽ����־ �����봫ֵ�� 9
	   * @param OrderType String �Ƿ�Ϊ��ʱҽ�� 1-����ʱҽ�� 0��� ����ҽ�� �����봫ֵ�� 10
	   * @param OrderDoctor String ����ҽ��ID/����ҽ������ �����봫ֵ�� 11
	   * @return int ��������
	   */
	public static String[] getOrder(OpdOrder order,int row){
		String[] result=new String[12];
		if(order==null||order.rowCount()<1){
			return null;
		}
		result[0]=order.getItemString(row, "RX_NO")+order.getItemString(row, "ORDER_CODE");
		result[1]=order.getItemString(row, "ORDER_CODE");
		result[2]=order.getItemString(row, "ORDER_DESC");
		result[3]=order.getItemDouble(row, "DOSAGE_QTY")+"";
		result[4]=order.getItemString(row, "MEDI_UNIT");
//		result[5]=order.getItemString(row, "")
		return result;
	}
	/**
	 * ȡ��Ƶ������
	 * @return
	 */
	public  void getFreq_Unit_Route(){
		if(tdsFreq==null){
			tdsFreq=new TDS();
			tdsFreq.setSQL(GET_FREQ);
			tdsFreq.retrieve();
		}
		if(tdsUnit==null){
			tdsUnit=new TDS();
			tdsUnit.setSQL(GET_UNIT);
			tdsUnit.retrieve();
		}
		if(tdsRoute==null){
			tdsRoute=new TDS();
			tdsRoute.setSQL(GET_ROUTE);
			tdsRoute.retrieve();
		}
		return ;
	}
	/**
	 * 
	 * @param order
	 * @param row
	 * @return
	 */
	public  String[] getOrderInfo(OpdOrder order,int row){
		if(order==null||row<0)
			return null;
		if(tdsFreq==null||tdsUnit==null||tdsRoute==null)
			getFreq_Unit_Route();
		  /**
		   * ���벡����ҩ��Ϣ
		   * ���ã�����Ҫ������ҩҽ�����ʱ�����øýӿڡ�
		   * ע����������ǰ�����ж�����ҩҽ��ʱ��ѭ�����롣�����ҽ��Ϊ��ҩҽ����
		   * ���ڹ���վ����Ϊ10��ʱ�����ʱ(�磺סԺҽ��վ������ҽ��վ)��
		   * ��ҩ�������ڿ��Բ��ô�ֵ��Ĭ��Ϊ���죻�����ڹ���վ����Ϊ20�ع������ʱ(�磺�ٴ�ҩѧ�������ѯͳ��)��
		   * ��ҩ�������ڱ��봫ֵ��
		   * @param OrderUniqueCode String ҽ��Ψһ�루���봫ֵ�� 0
		   * @param DrugCode String ҩƷ���� �����봫ֵ�� 1
		   * @param DrugName String ҩƷ���� �����봫ֵ�� 2
		   * @param SingleDose String ÿ������ �����봫ֵ�� 3
		   * @param DoseUnit String ������λ �����봫ֵ�� 4
		   * @param Frequency String ��ҩƵ��(��/��)�����봫ֵ�� 5
		   * @param StartOrderDate String ��ҩ��ʼ���ڣ���ʽ��yyyy-mm-dd �����봫ֵ�� 6
		   * @param StopOrderDate String ��ҩ�������ڣ���ʽ��yyyy-mm-dd �����Բ���ֵ����Ĭ��ֵΪ���� 7
		   * @param RouteName String ��ҩ;���������� �����봫ֵ�� 8
		   * @param GroupTag String ����ҽ����־ �����봫ֵ�� 9
		   * @param OrderType String �Ƿ�Ϊ��ʱҽ�� 1-����ʱҽ�� 0��� ����ҽ�� �����봫ֵ�� 10
		   * @param OrderDoctor String ����ҽ��ID/����ҽ������ �����봫ֵ�� 11
		   */
		String[] result=new String[12];
		//ҩƷΨһ�룺RX_NO+FILL0(SEQ,3)+ORDER_CODE
		result[0]=order.getItemString(row, "RX_NO")+StringTool.fill0(order.getItemInt(row, "SEQ_NO")+"", 3)+order.getItemString(row, "ORDER_CODE");
		result[1]=order.getItemString(row, "ORDER_CODE");
		result[2]=order.getItemString(row, "ORDER_DESC");
		result[3]=order.getItemData(row, "MEDI_QTY")+"";
		tdsUnit.setFilter("UNIT_CODE='" +order.getItemString(row, "MEDI_UNIT")+
				"'");
		tdsUnit.filter();
		result[4]=tdsUnit.getItemString(0, "UNIT_CHN_DESC");
		tdsFreq.setFilter("FREQ_CODE='" +order.getItemString(row, "FREQ_CODE")+
				"'");
		tdsFreq.filter();
		result[5]=tdsFreq.getItemInt(0, "FREQ_TIMES")+"/"+tdsFreq.getItemInt(0, "CYCLE");
		String date=StringTool.getString(TJDODBTool.getInstance().getDBTime(), "yyyy-MM-dd");
		result[6]=date;
		result[7]=date;
		tdsRoute.setFilter("ROUTE_CODE='" +order.getItemString(row, "ROUTE_CODE")+
				"'");
		tdsRoute.filter();
		result[8]=tdsRoute.getItemString(0,"ROUTE_CHN_DESC");
		result[9]="1";
		result[10]="1";
		result[11]=Operator.getName();
		return result;
	}
}
