package jdo.bil;


import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;

import jdo.mem.MEMTool;

public class BILLumpWorkTool {
	/**
	 * ʵ��
	 */
	public static BILLumpWorkTool instanceObject;
	/**
	 * �õ�ʵ��
	 * 
	 * @return
	 */
	public static BILLumpWorkTool getInstance() {
		if (instanceObject == null)
			instanceObject = new BILLumpWorkTool();
		return instanceObject;
	}
	/**
	 * �����ۿ�
	 * ͨ�������Ų�ѯ��ǰʹ�õ��ײͣ������ۿ�
	 * @param MrNo
	 * @param packCode
	 * @return
	 */
	public TParm onComputeRateByMrNo(String mrNo, String packCode) {
		TParm lumpWorkParm = MEMTool.getInstance().selectMemPackTradeM(mrNo,
				packCode);
		if (lumpWorkParm.getErrCode() < 0) {
			return lumpWorkParm;
		}
		if (lumpWorkParm.getCount() <= 0) {
			lumpWorkParm = new TParm();
			lumpWorkParm.setErr(-1, "δ��ѯ���˲���������ײ�");
		}
//		System.out.println("lumpWorkParmD:::::::::"+lumpWorkParm);
		TParm parm = new TParm();
		parm.setData("TRADE_NO", lumpWorkParm.getValue("TRADE_NO", 0));
		if (packCode.length() > 0) {
			parm.setData("PACKAGE_CODE", packCode);
		}
		TParm result = onComputeRateComm(parm, lumpWorkParm);
//		System.out.println("resultSDFSD:::::"+result);
		return result;
	}
	/**
	 * �����ۿ�
	 * ͨ������Ų�ѯ��ǰʹ�õ��ײͣ������ۿ�
	 * @return
	 */
	public TParm onComputeRateByCaseNo(String caseNo,String packCode){
		TParm lumpWorkParm=MEMTool.getInstance().selectMemPackTradeM(caseNo);
		if(lumpWorkParm.getErrCode()<0){
			return lumpWorkParm;
		}
		if(lumpWorkParm.getCount()<=0){
			lumpWorkParm= new TParm();
			lumpWorkParm.setErr(-1, "û�в�ѯ������");
			return lumpWorkParm;
		}
		TParm parm=new TParm();
		parm.setData("CASE_NO",caseNo);
		if(packCode.length()>0){
			parm.setData("PACKAGE_CODE",packCode);
		}
		TParm result=onComputeRateComm(parm, lumpWorkParm);
		return result;
	}
	/**
	 * �ײ��ۿۼ���
	 * @return
	 */
	public TParm onLumpWorkRateFee(String packCode,String packType,String schCode){
		String sqlSum="SELECT SUM(SECTION_PRICE) FEE " +
			"FROM MEM_PACKAGE_SECTION_PRICE WHERE PACKAGE_CODE='"+packCode+"' AND PRICE_TYPE='"+packType+"' " +
			"AND SECTION_CODE IN("+schCode+")";
		TParm sumParm= new TParm(TJDODBTool.getInstance().select(sqlSum));
		if(sumParm.getErrCode()<0){
			return sumParm;
		}
		String sql="SELECT SUM(D.ORDER_NUM*E.OWN_PRICE) SUM_FEE " +
				"FROM MEM_PACKAGE_PRICE B,MEM_PACKAGE_SECTION C,MEM_PACKAGE_SECTION_D D,SYS_FEE E " +
				"WHERE B.PACKAGE_CODE=C.PACKAGE_CODE " +
				"AND C.SECTION_CODE=D.SECTION_CODE AND C.PACKAGE_CODE=D.PACKAGE_CODE AND D.SETMAIN_FLG='N' " +
				"AND D.ORDER_CODE=E.ORDER_CODE(+) AND B.PACKAGE_CODE='"+packCode+"' AND B.PRICE_TYPE='"+packType+"' " +
			    "AND C.SECTION_CODE IN("+schCode+")";
		TParm memPackSctionParm= new TParm(TJDODBTool.getInstance().select(sql));
		if(memPackSctionParm.getErrCode()<0){
			return memPackSctionParm;
		}
		
		if(memPackSctionParm.getCount()<=0 || memPackSctionParm.getValue("SUM_FEE",0).length()<=0){
			memPackSctionParm= new TParm();
			memPackSctionParm.setErr(-1, "û�в�ѯ���ײ�����");
			return memPackSctionParm;
		}
		sql="SELECT SUM(D.ORDER_NUM*E.OWN_PRICE) SUM_FEE " +
				"FROM MEM_PACKAGE_PRICE B,MEM_PACKAGE_SECTION C,MEM_PACKAGE_SECTION_D D,SYS_FEE E " +
				"WHERE B.PACKAGE_CODE=C.PACKAGE_CODE " +
				"AND C.SECTION_CODE=D.SECTION_CODE AND C.PACKAGE_CODE=D.PACKAGE_CODE AND D.SETMAIN_FLG='N' " +
				"AND D.ORDER_CODE=E.ORDER_CODE(+) AND B.PACKAGE_CODE='"+packCode+"' AND B.PRICE_TYPE='"+packType+"' " +
			    "AND C.SECTION_CODE IN("+schCode+") AND E.CAT1_TYPE='PHA'";
		TParm memPackSctionPhaParm= new TParm(TJDODBTool.getInstance().select(sql));
		if(memPackSctionPhaParm.getErrCode()<0){
			return memPackSctionPhaParm;
		}
		
		if(memPackSctionPhaParm.getCount()<=0 || memPackSctionPhaParm.getValue("SUM_FEE",0).length()<=0){
			memPackSctionPhaParm= new TParm();
			memPackSctionPhaParm.setErr(-1, "û�в�ѯ���ײ�����");
			return memPackSctionPhaParm;
		}
		sql="SELECT SUM(D.ORDER_NUM*E.OWN_PRICE) SUM_FEE " +
				"FROM MEM_PACKAGE_PRICE B,MEM_PACKAGE_SECTION C,MEM_PACKAGE_SECTION_D D,SYS_FEE E " +
				"WHERE B.PACKAGE_CODE=C.PACKAGE_CODE " +
				"AND C.SECTION_CODE=D.SECTION_CODE AND C.PACKAGE_CODE=D.PACKAGE_CODE AND D.SETMAIN_FLG='N' " +
				"AND D.ORDER_CODE=E.ORDER_CODE(+) AND B.PACKAGE_CODE='"+packCode+"' AND B.PRICE_TYPE='"+packType+"' " +
			    "AND C.SECTION_CODE IN("+schCode+") AND E.CHARGE_HOSP_CODE='RA'";
		TParm memPackSctionRaParm= new TParm(TJDODBTool.getInstance().select(sql));
		if(memPackSctionRaParm.getErrCode()<0){
			return memPackSctionRaParm;
		}
		
		if(memPackSctionRaParm.getCount()<=0 || memPackSctionRaParm.getValue("SUM_FEE",0).length()<=0){
			memPackSctionRaParm= new TParm();
			memPackSctionRaParm.setErr(-1, "û�в�ѯ���ײ�����");
			return memPackSctionRaParm;
		}
		double fee=sumParm.getDouble("FEE",0);//���ײ�������
		//���ײ��ۿ��ʡ�=�����ײ������ۡ�-��ҩƷ��ѪƷ�Ȳ��������ܽ���/�����ײ�ԭ�ۡ�-��ҩƷ��ѪƷ�Ȳ��������ܽ���
		//���У�
		//��ҩƷ��ѪƷ�Ȳ��������ܽ�= ��ҩƷ��ѪƷ�Ȳ���������Ŀ��ǰ�ԷѼۡ����������� ֮��
		double feeAmt=memPackSctionParm.getDouble("SUM_FEE",0);//�ײ�ԭ��
		double phaAmt=memPackSctionPhaParm.getDouble("SUM_FEE",0);//ҩƷԭ��
		double reAmt=memPackSctionRaParm.getDouble("SUM_FEE",0);//Ѫ��ԭ��
		double rate=0.00;//�ײ��ۿ���
		//double phaMAmt=0.00;//ҩƷ���ۼ�Ǯ
		//double reMAmt=0.00;//Ѫ�����ۼ�Ǯ
		TParm result=new TParm();
//		for (int i = 0; i < memPackSctionParm.getCount(); i++) {
////			if(null==memPackSctionParm.getValue("ORDER_FEE_CODE",i)||memPackSctionParm.getValue("ORDER_FEE_CODE",i).length()<=0){
////				result.setErr(-1,
////						"ҽ������:"+memPackSctionParm.getValue("ORDER_CODE",i)+","+memPackSctionParm.getValue("ORDER_DESC",i)+"��ҽ���ֵ��в�����");
////				return result;
////			}
////			if(null==memPackSctionParm.getValue("ACTIVE_FLG",i)||memPackSctionParm.getValue("ACTIVE_FLG",i).length()<=0
////					||memPackSctionParm.getValue("ACTIVE_FLG",i).equals("N")){
////				result.setErr(-1,
////						"ҽ������:"+memPackSctionParm.getValue("ORDER_CODE",i)+","+memPackSctionParm.getValue("ORDER_DESC",i)+"��ҽ���ֵ����Ѿ�ͣ��");
////				return result;
////			}
//			
//			if(memPackSctionParm.getValue("CAT1_TYPE",i).equals("PHA")){
//				phaAmt+=StringTool.round(memPackSctionParm.getDouble("ORDER_NUM",i)*memPackSctionParm.getDouble("OWN_PRICE_FEE",i), 2);
//				//phaMAmt+=StringTool.round(memPackSctionParm.getDouble("ORDER_NUM",i)*memPackSctionParm.getDouble("RETAIL_PRICE",i), 2);
//				feeAmt+=StringTool.round(memPackSctionParm.getDouble("ORDER_NUM",i)*memPackSctionParm.getDouble("OWN_PRICE_FEE",i), 2);
//			}else{
//				feeAmt+=StringTool.round(memPackSctionParm.getDouble("ORDER_NUM",i)*memPackSctionParm.getDouble("OWN_PRICE",i), 2);
//			}
//			if(memPackSctionParm.getValue("CHARGE_HOSP_CODE",i).equals("RA")){
//				reAmt+=StringTool.round(memPackSctionParm.getDouble("ORDER_NUM",i)*memPackSctionParm.getDouble("OWN_PRICE_FEE",i), 2);
//				feeAmt+=StringTool.round(memPackSctionParm.getDouble("ORDER_NUM",i)*memPackSctionParm.getDouble("OWN_PRICE_FEE",i), 2);
//				//reMAmt+=StringTool.round(memPackSctionParm.getDouble("ORDER_NUM",i)*memPackSctionParm.getDouble("RETAIL_PRICE",i), 2);
//			}else{
//				feeAmt+=StringTool.round(memPackSctionParm.getDouble("ORDER_NUM",i)*memPackSctionParm.getDouble("OWN_PRICE",i), 2);
//			}
//		}
//		feeAmt=StringTool.round(feeAmt,2);//�ײ�ԭ��
//		phaAmt=StringTool.round(phaAmt,2);//ҩƷԭ��
//		reAmt=StringTool.round(reAmt,2);//Ѫ��ԭ��
		//reMAmt=StringTool.round(reMAmt,2);
		//phaMAmt=StringTool.round(phaMAmt,2);
		System.out.println("fee::::::"+fee);
		System.out.println("phaAmtS::::"+phaAmt);
		System.out.println("reAmt:::"+reAmt);
		System.out.println("feeAmt:::::"+feeAmt);
		rate=(fee-phaAmt-reAmt)/(feeAmt-phaAmt-reAmt);//�ۿ�
		System.out.println("rate::::::"+rate);
		result.setData("RATE",rate);
		return result;
	}
	/**
	 * ����ۿ۹���
	 * @param parm
	 * @param lumpWorkParm
	 * @return
	 */
	private TParm onComputeRateComm(TParm parm,TParm lumpWorkParm){
		String packageCode="";
		String caseNo= "";
		String tradeNo= "";
		if(null != parm.getValue("PACKAGE_CODE") && parm.getValue("PACKAGE_CODE").length()>0){
			packageCode = " AND A.PACKAGE_CODE='"+parm.getValue("PACKAGE_CODE")+"'";
		}
		if(null != parm.getValue("CASE_NO") && parm.getValue("CASE_NO").length()>0){
			caseNo = " AND A.CASE_NO='"+parm.getValue("CASE_NO")+"'";
		}
		if(null != parm.getValue("TRADE_NO") && parm.getValue("TRADE_NO").length()>0){
			tradeNo = " AND A.TRADE_NO='"+parm.getValue("TRADE_NO")+"'";
		}
		String sql ="SELECT  SUM(A.ORDER_NUM*A.UNIT_PRICE) SUM_REAL_FEE, SUM(A.ORDER_NUM*B.OWN_PRICE) SUM_FEE"
	      		+ " FROM MEM_PAT_PACKAGE_SECTION_D A,SYS_FEE B WHERE A.ORDER_CODE=B.ORDER_CODE(+) AND A.SETMAIN_FLG='N' "
	      		+ caseNo+tradeNo+packageCode;
	   
		//TParm memPackSctionParm=MEMTool.getInstance().selectMemPackageSectionDByCaseNo(parm); 
		
		TParm memPackSctionParm = new TParm(TJDODBTool.getInstance().select(sql));
		if(memPackSctionParm.getErrCode()<0){
			return memPackSctionParm;
		}
//		System.out.println("memPackSctionParm:::::"+memPackSctionParm);
		if(memPackSctionParm.getCount()<=0 || memPackSctionParm.getValue("SUM_FEE",0).length()<=0){
			memPackSctionParm= new TParm();
			memPackSctionParm.setErr(-1, "û�в�ѯ���ײ�����");
			return memPackSctionParm;
		}
		sql ="SELECT  SUM(A.ORDER_NUM*UNIT_PRICE) SUM_REAL_FEE, SUM(A.ORDER_NUM*OWN_PRICE) SUM_FEE"
	      		+ " FROM MEM_PAT_PACKAGE_SECTION_D A,SYS_FEE B WHERE A.ORDER_CODE=B.ORDER_CODE(+) AND A.SETMAIN_FLG='N' "
	      		+ caseNo+tradeNo+packageCode+" AND B.CAT1_TYPE='PHA'";
		TParm memPackSctionPhaParm = new TParm(TJDODBTool.getInstance().select(sql));
		if(memPackSctionPhaParm.getErrCode()<0){
			return memPackSctionPhaParm;
		}
		if(memPackSctionPhaParm.getCount()<=0 || memPackSctionPhaParm.getValue("SUM_FEE",0).length()<=0){
			memPackSctionPhaParm= new TParm();
			memPackSctionPhaParm.setErr(-1, "û�в�ѯ���ײ�����");
			return memPackSctionPhaParm;
		}
		sql ="SELECT  SUM(A.ORDER_NUM*UNIT_PRICE) SUM_REAL_FEE, SUM(A.ORDER_NUM*OWN_PRICE) SUM_FEE"
	      		+ " FROM MEM_PAT_PACKAGE_SECTION_D A,SYS_FEE B WHERE A.ORDER_CODE=B.ORDER_CODE(+) AND A.SETMAIN_FLG='N' "
	      		+ caseNo+tradeNo+packageCode+" AND B.CHARGE_HOSP_CODE='RA'";
		TParm memPackSctionRaParm = new TParm(TJDODBTool.getInstance().select(sql));
		if(memPackSctionRaParm.getErrCode()<0){
			return memPackSctionRaParm;
		}
		if(memPackSctionRaParm.getCount()<=0 || memPackSctionRaParm.getValue("SUM_FEE",0).length()<=0){
			memPackSctionRaParm= new TParm();
			memPackSctionRaParm.setErr(-1, "û�в�ѯ���ײ�����");
			return memPackSctionRaParm;
		}
		double fee=lumpWorkParm.getDouble("FEE",0);//���ײ�������
		//���ײ��ۿ��ʡ�=�����ײ������ۡ�-��ҩƷ��ѪƷ�Ȳ��������ܽ���/�����ײ�ԭ�ۡ�-��ҩƷ��ѪƷ�Ȳ��������ܽ���
		//���У�
		//��ҩƷ��ѪƷ�Ȳ��������ܽ�= ��ҩƷ��ѪƷ�Ȳ���������Ŀ��ǰ�ԷѼۡ����������� ֮��
		double feeAmt=memPackSctionParm.getDouble("SUM_FEE",0);//�ײ�ԭ��
		double phaAmt=memPackSctionPhaParm.getDouble("SUM_FEE",0);//ҩƷԭ��
		double reAmt=memPackSctionRaParm.getDouble("SUM_FEE",0);//Ѫ��ԭ��
		double rate=0.00;//�ײ��ۿ���
		//double phaMAmt=0.00;//ҩƷ���ۼ�Ǯ
		//double reMAmt=0.00;//Ѫ�����ۼ�Ǯ
		TParm result=new TParm();
//		for (int i = 0; i < memPackSctionParm.getCount(); i++) {
////			if(null==memPackSctionParm.getValue("ORDER_FEE_CODE",i)||memPackSctionParm.getValue("ORDER_FEE_CODE",i).length()<=0){
////				result.setErr(-1,
////						"ҽ������:"+memPackSctionParm.getValue("ORDER_CODE",i)+","+memPackSctionParm.getValue("ORDER_DESC",i)+"��ҽ���ֵ��в�����");
////				return result;
////			}
////			if(null==memPackSctionParm.getValue("ACTIVE_FLG",i)||memPackSctionParm.getValue("ACTIVE_FLG",i).length()<=0
////					||memPackSctionParm.getValue("ACTIVE_FLG",i).equals("N")){
////				result.setErr(-1,
////						"ҽ������:"+memPackSctionParm.getValue("ORDER_CODE",i)+","+memPackSctionParm.getValue("ORDER_DESC",i)+"��ҽ���ֵ����Ѿ�ͣ��");
////				return result;
////			}
//			if(memPackSctionParm.getValue("CAT1_TYPE",i).equals("PHA")){
//				phaAmt+=StringTool.round(memPackSctionParm.getDouble("ORDER_NUM",i)*memPackSctionParm.getDouble("OWN_PRICE_FEE",i), 2);
//				//phaMAmt+=StringTool.round(memPackSctionParm.getDouble("ORDER_NUM",i)*memPackSctionParm.getDouble("RETAIL_PRICE",i), 2);
//				feeAmt+=StringTool.round(memPackSctionParm.getDouble("ORDER_NUM",i)*memPackSctionParm.getDouble("OWN_PRICE_FEE",i), 2);
//			}else{
//				feeAmt+=StringTool.round(memPackSctionParm.getDouble("ORDER_NUM",i)*memPackSctionParm.getDouble("OWN_PRICE",i), 2);
//			}
//			if(memPackSctionParm.getValue("CHARGE_HOSP_CODE",i).equals("RA")){
//				reAmt+=StringTool.round(memPackSctionParm.getDouble("ORDER_NUM",i)*memPackSctionParm.getDouble("OWN_PRICE_FEE",i), 2);
//				feeAmt+=StringTool.round(memPackSctionParm.getDouble("ORDER_NUM",i)*memPackSctionParm.getDouble("OWN_PRICE_FEE",i), 2);
//				//reMAmt+=StringTool.round(memPackSctionParm.getDouble("ORDER_NUM",i)*memPackSctionParm.getDouble("RETAIL_PRICE",i), 2);
//			}else{
//				feeAmt+=StringTool.round(memPackSctionParm.getDouble("ORDER_NUM",i)*memPackSctionParm.getDouble("OWN_PRICE",i), 2);
//			}
//		}
//		feeAmt=StringTool.round(feeAmt,2);//�ײ�ԭ��
//		phaAmt=StringTool.round(phaAmt,2);//ҩƷԭ��
//		reAmt=StringTool.round(reAmt,2);//Ѫ��ԭ��
		//reMAmt=StringTool.round(reMAmt,2);
		//phaMAmt=StringTool.round(phaMAmt,2);
		System.out.println("fee::::::"+fee);
		System.out.println("phaAmtS::::"+phaAmt);
		System.out.println("reAmt:::"+reAmt);
		System.out.println("feeAmt:::::"+feeAmt);
		rate=(fee-phaAmt-reAmt)/(feeAmt-phaAmt-reAmt);//�ۿ�
		System.out.println("rateDSfsd::::::"+rate);
		result.setData("RATE",rate);
		return result;
	}
	/**
	 * סԺ�Ǽǲ���������ײ��ۿ�
	 * @param mrNo
	 * @param lumpCode
	 * @return
	 */
	public TParm getLumpRate(String caseNo,String mrNo,String lumpCode){
		TParm result=new TParm();
		if(caseNo.length()>0){//�Ѿ�����caseNoֱ�Ӳ���
			result=onComputeRateByCaseNo(caseNo,lumpCode);
		}else{//�����ڣ�ͨ�������Ż������
			result=onComputeRateByMrNo(mrNo, lumpCode);
		}
		return result;
	}
}
