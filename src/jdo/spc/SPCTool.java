package jdo.spc;


import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.javahis.ui.spc.util.ElectronicTagUtil;

/**
 * ������tool 
 * @author liyh
 *
 */
public class SPCTool extends TJDOTool {

	private String code;

	/**
	 * ʵ��
	 */
	public static SPCTool instanceObject;

	/**
	 * �õ�ʵ��
	 * 
	 * @return INDTool
	 */
	public static SPCTool getInstance() {
		if (instanceObject == null)
			instanceObject = new SPCTool();
		return instanceObject;
	}

	/**
	 * ������
	 */
	public SPCTool() {
		onInit();
	}
	
	/**
	 * ������ҩƷ����תΪHIS
	 */
	public String getHisOrderCodeBySpcOrderCode(String orderCode,String region){
		String hisOrderCode = "";
//		System.out.println("-������ҩƷ����תΪHIS------sql:"+SPCSQL.getSpcOrderCodeByHisOrderCode(orderCode,region,""));
		TParm parm = new TParm(TJDODBTool.getInstance().select(SPCSQL.getSpcOrderCodeByHisOrderCode(orderCode,region,"")));
		if(null != parm && parm.getCount() > 0){
			 hisOrderCode = parm.getValue("HIS_ORDER_CODE", 0);
		}
		return hisOrderCode;
	}
	
	/**
	 * �õ�ҩƷ��Ӧ�İ�ҩ�����ű���
	 * @param orderCode
	 * @return
	 */
	public TParm getOrgCodeOfAtc(String orgCode){
//		System.out.println("-----------SPCSQL.getOrgCodeOfAtc(orderCode):"+SPCSQL.getOrgCodeOfAtc(orgCode));
		TParm parm = new TParm(TJDODBTool.getInstance().select(SPCSQL.getOrgCodeOfAtc(orgCode)));
		return parm;
		
	}

	/**
	 * ��ѯ��ǰ�����µ��ӱ�ǩ
	 * @param orgCode
	 * @param orderCode
	 * @param lightNum
	 */
	public void sendEleTag(String orgCode,String orderCode,int lightNum){
		//��ѯ��ǰ���
//		System.out.println("----------��ѯ��ǰ��� sql:"+SPCSQL.getIndStockQtyAndEleTag(orgCode, orderCode));
		TParm parm = new TParm(TJDODBTool.getInstance().select(SPCSQL.getIndStockQtyAndEleTag(orgCode, orderCode)));
//		System.out.println("-------parm:"+parm);
		if(null != parm){
			//���µ��ӱ�ǩ
			ElectronicTagUtil.getInstance().sendEleTag(parm.getValue("ELETAG_CODE", 0), parm.getValue("ORDER_DESC", 0), 
					parm.getValue("SPECIFICATION", 0),  parm.getValue("STOCK_QTY", 0), lightNum);	
		}
	}

    /**
     * �ż�����ҩ-�޸�ִ�п���-OPD_ORDER
     * @param orderCode
     * @return
     */
    public TParm  updateExecDeptCode(String caseNo,String rxNo,String seqNo,String orgCode){
    	String sql = SPCSQL.updateExecDeptCode(caseNo,rxNo,seqNo,orgCode);
//		System.out.println("-----------updateExecDeptCode(orderCode):"+sql);
		TParm parm = new TParm(TJDODBTool.getInstance().update(sql));
		return parm;
		
	}
    
    /**
     * �õ��������-BATCH_SEQ
     * @param orderCode
     * @return
     */
    public String   getMaxBatchSeq(String orgCode,String orderCode,String validDate,String price,String batchNo,String supCode ){
    	String sql = SPCSQL.getMaxBatchSeq(orgCode,orderCode,validDate,price,batchNo,supCode);
//		System.out.println("-----------getMaxBatchSeq(orderCode):"+sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm.getValue("", 0);
	}    
	/**
	 * ��ѯ��ǰ���
	 * @param orgCode
	 * @param orderCode
	 * @return
	 */
	public static boolean checkStockQty(TParm parm){
		//Ĭ��ֵtrue �������
		boolean flg = true;
	    String orgCode = parm.getValue("ORG_CODE");
	    String orderCode = parm.getValue("ORDER_CODE");
	    double dosageQty = parm.getDouble("DOSAGE_QTY");
	    //��ѯ��ǰ���
//	    System.out.println("--------checkStockQty.sql:"+SPCSQL.getStockQtyByOrgCodeAndOrderCode(orgCode, orderCode));
	    TParm result = new TParm(TJDODBTool.getInstance().select(SPCSQL.getStockQtyByOrgCodeAndOrderCode(orgCode, orderCode)));
//	    System.out.println("----checkStockQty----dosageQty"+dosageQty+",��ѯ��ǰ���----result:"+result);
	    double stockQty = result.getDouble("STOCK_QTY", 0);
	    if(stockQty < dosageQty){//��ҩ�����ڿ����ʱ ����ֵ false  ���������
	    	flg = false;
	    }
	    return flg;
	}
	
	/**
	 * �����̵���
	 * @param orgCode
	 * @param orderCode
	 * @return
	 */
	public static void updateQtyCheck(double modiQty,double acturlCheckQty,String orgCode,String frozenDate,String orderCode,
			String batchSeq,String optUser,String optTerm){
		String sql = SPCSQL.updateQtyCheck(modiQty, acturlCheckQty, optUser, orgCode, frozenDate, orderCode, batchSeq, optUser, optTerm);
//		System.out.println("------sql: "+sql);
		TParm parm = new TParm(TJDODBTool.getInstance().update(sql));
	}	
    
}
