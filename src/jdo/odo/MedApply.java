package jdo.odo;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jdo.sys.Operator;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.javahis.util.StringUtil;

/**
*
* <p>Title: ����ҽ��վ����</p>
*
* <p>Description: </p>
*
* <p>Copyright: Copyright (c) 2008</p>
*
* <p>Company: JavaHis</p>
*
* @author lzk 2009.2.11
* @version 1.0
*/
public class MedApply extends StoreBase {
	//��ʼ��
	private static final String INIT="SELECT * FROM MED_APPLY WHERE CASE_NO='#'";
	//����һ��ҽ����ѯ����
	private static final String INIT_BY_ORDER="SELECT * FROM MED_APPLY WHERE CASE_NO='#' AND ORDER_CODE='#' AND SEQ_NO=#";
	//����һ��ҽ����ѯ����
	private static final String GET_STATUS="SELECT * FROM MED_APPLY WHERE CAT1_TYPE='#' AND APPLICATION_NO='#' AND ORDER_NO='#' AND SEQ_NO=#";
	//����CASE_NO��ʼ��
	private static final String INIT_BY_CASE="SELECT * FROM MED_APPLY WHERE CASE_NO='#' ORDER BY SEQ_NO";
	//����ORDER_NO�����ڽ�����˵����CASE_NO����ѯLIS�ļ�����
	private static final String GET_LIS_PARM="SELECT A.ORDER_DESC,B.* FROM HRM_ORDER A, MED_LIS_RPT B WHERE A.CASE_NO='#' AND A.CAT1_TYPE='LIS' AND A.CAT1_TYPE=B.CAT1_TYPE AND A.MED_APPLY_NO=B.APPLICATION_NO AND A.SEQ_NO=B.SEQ_NO ORDER BY B.SEQ_NO, B.RPDTL_SEQ";
	//����CASE_NO��ѯRIS�����
	private static final String GET_RIS_PARM="SELECT B.*,A.ORDER_DESC FROM HRM_ORDER A,MED_RPTDTL B WHERE A.CASE_NO='#' AND A.SETMAIN_FLG='Y' AND A.CAT1_TYPE='RIS' AND A.CAT1_TYPE=B.CAT1_TYPE AND A.MED_APPLY_NO = B.APPLICATION_NO ORDER BY B.APPLICATION_NO, B.RPDTL_SEQ";
	/**
	 * ��ԃ
	 */
	public boolean onQuery(){
		this.setSQL(INIT);
		return true;
	}
	/**
	 * ����CASE_NO��ʼ��
	 * @param caseNo
	 * @return
	 */
	public boolean onQueryByCaseNo(String caseNo){
		if(StringUtil.isNullString(caseNo)){
			return false;
		}
		String sql=INIT_BY_CASE.replaceFirst("#", caseNo);
		this.setSQL(sql);
		this.retrieve();
		return true;
	}
	/**
	 * ��ʼ��
	 * @param odo
	 * @return
	 */
	public boolean onInit(ODO odo){
		if(odo==null){
			return false;
		}
		OpdOrder order=odo.getOpdOrder();
		if(order==null){
			return false;
		}
		String filter=order.getFilter();
		Map medMap=order.getLabMap();
		if(medMap==null){
			return false;
		}
		int size=medMap.size();
		if(size<1){
			return false;
		}
		this.setSQL(INIT);
		this.retrieve();
		for(int i=0;i<size;i++){
			int row=this.insertRow();
			List med=(ArrayList)medMap.get(i);

			//value.add(labNo);//0
			this.setItem(row, "APPLICATION_NO", med.get(0));
			//value.add(cat1Type);//1
			this.setItem(row, "CAT1_TYPE", med.get(1));
			//// ORDER_NO
			this.setItem(row, "ORDER_NO", med.get(2));
			//value.add(this.getItemString(row, "RX_NO"));//2
			////SEQ_NO
			this.setItem(row, "SEQ_NO", med.get(3));
			//value.add(this.getItemData(row, "SEQ_NO"));//3
			////ORDER_CODE
			this.setItem(row, "ORDER_CODE", med.get(4));
			//value.add(this.getItemData(row, "ORDER_CODE"));//4
			////ORDER_DESC
			this.setItem(row, "ORDER_DESC", med.get(5));
			//value.add(this.getItemData(row, "ORDER_DESC"));
			////ORDER_DR_CODE
			this.setItem(row, "ORDER_DR_CODE", med.get(6));
			//value.add(Operator.getID());
			//Timestamp now=this.getDBTime();
			////ORDER_DATE
			this.setItem(row, "ORDER_DATE", med.get(7));
			//value.add(now);
			////ORDER_DEPT_CODE
			this.setItem(row, "ORDER_DEPT_CODE", med.get(8));
			//value.add(Operator.getDept());
			////START_DTTM
			this.setItem(row, "START_DTTM", med.get(9));
			//value.add(now);
			////EXEC_DEPT_CODE
			this.setItem(row, "EXEC_DEPT_CODE", med.get(10));
			//value.add(this.getItemData(row, "EXEC_DEPT_CODE"));
			////EXEC_DR_CODE
			this.setItem(row, "EXEC_DR_CODE", med.get(11));
			//value.add("");
			////OPTITEM_CODE
			this.setItem(row, "OPTITEM_CODE", med.get(12));
			//value.add(this.getItemData(row, "OPTITEM_CODE"));
			//String optitemDesc=StringUtil.getDesc("SYS_OPTITEM", "OPTITEM_CHN_DESC", "OPTITEM_CODE='" +this.getItemData(row, "OPTITEM_CODE")+"'");
			////OPTITEM_CHN_DESC
			this.setItem(row, "OPTITEM_CHN_DESC", med.get(13));
			//value.add(optitemDesc);
			////ORDER_CAT1_CODE
			this.setItem(row, "ORDER_CAT1_CODE", med.get(14));
			//value.add(this.getItemData(row, "ORDER_CAT1_CODE"));
			//String dealSystem=StringUtil.getDesc("SYS_OPTITEM", "OPTITEM_CHN_DESC", "OPTITEM_CODE='" +this.getItemData(row, "OPTITEM_CODE")+"'");
			////DEAL_SYSTEM
			this.setItem(row, "DEAL_SYSTEM", med.get(15));
			//value.add(OrderUtil.getInstance().getDealSystem(this.getItemString(row, "ORDER_CAT1_CODE")));
			////RPTTYPE_CODE
			this.setItem(row, "RPTTYPE_CODE", med.get(16));
			//value.add(this.getItemData(row, "RPTTYPE_CODE"));
			////DEV_CODE
			this.setItem(row, "DEV_CODE", med.get(17));
			//value.add(this.getItemData(row, "DEV_CODE"));
			////REMARK
			this.setItem(row, "REMARK", med.get(18));
			//value.add(this.getItemData(row, "DR_NOTE"));
			//OWN_AMT
			this.setItem(row, "OWN_AMT", med.get(19));
			//AR_AMT
			this.setItem(row, "AR_AMT", med.get(20));


			//CASE_NO
			this.setItem(row, "CASE_NO", odo.getCaseNo());
			//MR_NO
			this.setItem(row, "MR_NO", odo.getMrNo());
			//ADM_TYPE
			this.setItem(row, "ADM_TYPE", "O");
			//PAT_NAME
			this.setItem(row, "PAT_NAME", odo.getPatInfo().getItemString(0, "PAT_NAME"));
			//PAT_NAME1
			this.setItem(row, "PAT_NAME1", odo.getPatInfo().getItemString(0, "PAT_NAME1"));
			//BIRTH_DATE
			this.setItem(row, "BIRTH_DATE", odo.getPatInfo().getItemData(0, "BIRTH_DATE"));
			//SEX_CODE
			this.setItem(row, "SEX_CODE", odo.getPatInfo().getItemData(0, "SEX_CODE"));
			//POST_CODE
			this.setItem(row, "POST_CODE", odo.getPatInfo().getItemData(0, "POST_CODE"));
			//ADDRESS
			this.setItem(row, "ADDRESS", odo.getPatInfo().getItemData(0, "ADDRESS"));
			//COMPANY
			this.setItem(row, "COMPANY", odo.getPatInfo().getItemData(0, "COMPANY"));
			//TEL
			this.setItem(row, "TEL", odo.getPatInfo().getItemData(0, "TEL"));
			//IDNO
			this.setItem(row, "IDNO", odo.getPatInfo().getItemData(0, "IDNO"));
			//DEPT_CODE
			Operator d;
			this.setItem(row, "DEPT_CODE", Operator.getDept());
			//REGION_CODE
			this.setItem(row, "REGION_CODE", Operator.getRegion());
			//CLINICROOM_NO
			this.setItem(row, "CLINICROOM_NO", odo.getPatInfo().getItemData(0, "CLINICROOM_NO"));
			//ICD_TYPE
			int main=odo.getDiagrec().getMainDiag();
			this.setItem(row, "ICD_TYPE", odo.getDiagrec().getItemData(main, "ICD_TYPE"));
			//ICD_CODE
			this.setItem(row, "ICD_CODE", odo.getDiagrec().getItemData(main, "ICD_CODE"));
			//STATUS
			this.setItem(row, "STATUS", "0");
			//BILL_FLG
			this.setItem(row, "BILL_FLG", "N");

			//OPT_USER
			this.setItem(row, "OPT_USER", Operator.getID());
			Timestamp now=this.getDBTime();
			//OPT_DATE
			this.setItem(row, "OPT_DATE", now);
			//OPT_TERM
			this.setItem(row, "OPT_TERM", Operator.getIP());
			//PRINT_FLG
			this.setItem(row, "PRINT_FLG", "N");
		}
		return true;
	}
	/**
	 * ���ݸ���������ѯMED_APPLY
	 * @param orderCode
	 * @param caseNo
	 * @param seqNo
	 * @return
	 */
	public boolean onQueryByOrder(String orderCode,String caseNo,int seqNo){
		if(StringUtil.isNullString(caseNo)||StringUtil.isNullString(orderCode)){
			return false;
		}
		if(seqNo<0){
			return false;
		}
		String sql=INIT_BY_ORDER.replaceFirst("#", caseNo).replaceFirst("#", orderCode).replaceFirst("#", seqNo+"");
//		System.out.println("onQureyBy order.sql="+sql);
		this.setSQL(sql);
		this.retrieve();
		return true;
	}
	/**
	 * ���ݼ�����������APPLICATION_NO
	 * @param orderCode
	 * @param caseNo
	 * @param seqNo
	 * @return String
	 */
	public String getApplicationNo(String orderCode,String caseNo,int seqNo){
		String appNo="";
		String filterString=this.getFilter();
		this.setFilter("CASE_NO='" +caseNo+
				"' AND ORDER_CODE='" +orderCode+
				"' AND SEQ_NO="+seqNo);
		this.filter();
		appNo=this.getItemString(0, "APPLICATION_NO");
		this.setFilter(filterString);
		this.filter();
		return appNo;
	}
	/**
	 * ��������ɾ��һ����¼
	 * @param orderCode
	 * @param caseNo
	 * @param seqNo
	 * @return
	 */
	public boolean onDeleteOrder(String orderCode,String caseNo,int seqNo){
		String filterString=this.getFilter();
		this.setFilter("CASE_NO='" +caseNo+
				"' AND ORDER_CODE='" +orderCode+
				"' AND SEQ_NO="+seqNo);
		this.filter();
		if(this.rowCount()<=0){
//			System.out.println("count<=0");
			return false;
		}
		if(!isRemovable(0)){
//			System.out.println("not removable");
			return false;
		}
		this.setItem(0, "BILL_FLG", "N");
		this.setItem(0, "OPT_USER", Operator.getID());
		this.setItem(0, "OPT_DATE", this.getDBTime());
		this.setItem(0, "OPT_TERM", Operator.getIP());
		this.setFilter(filterString);
		this.filter();
		return true;
	}
	/**
	 * ����APPLICATION_NOɾ��ҽ��
	 * @param appNo
	 * @return
	 */
	public boolean onDeleteOrder(String appNo){
		if(StringUtil.isNullString(appNo)){
			return false;
		}
		String filterString=this.getFilter();
		this.setFilter("APPLICATION_NO='" +appNo+"'");
		this.filter();
		this.setItem(0, "BILL_FLG", "N");
		this.setItem(0, "OPT_USER", Operator.getID());
		this.setItem(0, "OPT_DATE", this.getDBTime());
		this.setItem(0, "OPT_TERM", Operator.getIP());
		this.setFilter(filterString);
		this.filter();
		return true;
	}
	/**
	 * һ����¼�Ƿ����ɾ��
	 * @param row
	 * @return boolean true:����ɾ����false��������ɾ��
	 */
	public boolean isRemovable(int row){
		if(row<0){
//			System.out.println("isRemovable.row<0");
			return false;
		}
		int count=this.rowCount();
		if(count<=0){
//			System.out.println("isRemovable.count<=0");
			return false;
		}
		int status=this.getItemInt(row, "STATUS");
		if(status==4||status==6||status==7||status==8){
			return false;
		}
		return true;
	}
	/**
	 * һ����¼�Ƿ����ɾ��
	 * @param cat1Type
	 * @param applicationNo
	 * @param orderNo
	 * @param seqNo
	 * @return
	 */
	public boolean isRemovable(String cat1Type,String applicationNo,String orderNo,int seqNo){
		if(StringUtil.isNullString(cat1Type)||StringUtil.isNullString(applicationNo)||StringUtil.isNullString(orderNo)){
			return false;
		}
		if(seqNo<0){
			return false;
		}
		String sql=GET_STATUS.replaceFirst("#", cat1Type).replaceFirst("#", applicationNo).replaceFirst("#", orderNo).replaceFirst("#", seqNo+"");
          //      System.out.println("MED==="+sql);
		TParm result=new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getErrCode()!=0){
//			System.out.println("isRemovable.errText="+result.getErrText());
//			System.out.println("sql=="+sql);
			return false;
		}
//		System.out.println("result==="+result);
		if(result.getCount()<=0){
			return false;
		}
		int status=result.getInt("STATUS",0);
		if(status==4||status==6||status==7||status==8){
			return false;
		}
		return true;
	}
	/**
	 * ȡ��LIS�ش�����
	 * @param caseNo
	 * @return
	 */
	public TParm getLisParm(String caseNo){
		TParm result=new TParm();
		if(StringUtil.isNullString(caseNo)){
			return null;
		}
		String sql=GET_LIS_PARM.replaceFirst("#", caseNo);
//                System.out.println("sql=="+sql);
		result=new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getErrCode()!=0){
//			System.out.println("sql========="+sql);
//			System.out.println("getLisParm.getErr"+result.getErrText());
			return null;
		}
		return result;
	}
	/**
	 * ����CASE_NO��ѯRIS������
	 * @param caseNo
	 * @return
	 */
	public TParm getRisParm(String caseNo){
		TParm result=new TParm();
		String sql=GET_RIS_PARM.replaceFirst("#", caseNo);
//                System.out.println("RIS=="+sql);
		result=new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getErrCode()!=0){
//			System.out.println("sql========="+sql);
			System.out.println("getLisParm.getErr"+result.getErrText());
			return null;
		}
		return result;
	}
	/**
	 * ���ݸ�����Ϣɾ��һ����¼
	 * @param applicationNo
	 * @param orderNo
	 * @param seqNo
	 * @return
	 */
	public boolean deleteRowBy(String applicationNo,String orderNo,int seqNo,String cat1Type) throws Exception{
		if(StringUtil.isNullString(applicationNo)||StringUtil.isNullString(orderNo)){
			return false;
		}
		if(seqNo<0){
			return false;
		}
		String filterString=this.getFilter();
		this.setFilter("APPLICATION_NO='" +applicationNo+
				"' AND ORDER_NO='" +orderNo+
				"' AND SEQ_NO=" +seqNo+
				" AND CAT1_TYPE='" +cat1Type+"'");
		this.filter();
		this.deleteRow(0);
		this.setFilter(filterString);
		this.filter();
		return true;
	}
}
