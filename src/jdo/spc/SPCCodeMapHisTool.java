package jdo.spc;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;


/**
 * <p>
 * Title: ������ҽԺ��ҩƷ����ȶ�
 * </p>
 *
 * <p>
 * Description: ������ҽԺ��ҩƷ����ȶ�
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 *
 * <p>
 * Company: BLUECORE
 * </p>
 *
 * @author liuzhen 2013.1.17
 * @version 1.0
 */
public class SPCCodeMapHisTool extends TJDODBTool {
	
	 /** ʵ��*/
    public static SPCCodeMapHisTool instanceObject;

    /**�õ�ʵ��*/
    public static SPCCodeMapHisTool getInstance() {
        if (instanceObject == null)
            instanceObject = new SPCCodeMapHisTool();
        return instanceObject;
    }
    
    
    /**��ѯ������ҩƷ��Ϣ*/
    public TParm query(TParm parm) {

    	String cond1 = "";
    	String orderCode = parm.getValue("ORDER_CODE");
    	
    	if(null != orderCode &&!"".equals(orderCode)){
    		cond1 = " AND A.ORDER_CODE LIKE '%"+orderCode+"%' ";
    	}
    	
    	String cond2 = "";
    	String regionCode = parm.getValue("REGION_CODE");
    	    	
    	if(null != regionCode &&!"".equals(regionCode)){
    		cond2 = " AND A.REGION_CODE = '"+regionCode.trim()+"' ";
    	}
    	
    	String cond3 = "";
    	String hisOrderCode = parm.getValue("HIS_ORDER_CODE");
    	    	
    	if(null != hisOrderCode &&!"".equals(hisOrderCode)){
    		cond3 = " AND A.HIS_ORDER_CODE = '"+hisOrderCode.trim()+"' ";
    	}
    	   
    	String sql = "SELECT " +
    					"B.ORDER_CODE, A.REGION_CODE, A.HIS_ORDER_CODE, " +
    					"B.ORDER_DESC, B.SPECIFICATION, A.ACTIVE_FLG,A.OPT_DATE " +
    				"FROM SYS_FEE_SPC A, PHA_BASE B " +
    				"WHERE A.ORDER_CODE(+) = B.ORDER_CODE " + 
    				cond1 +
    				cond2 + 
    				cond3 +
    				" ORDER BY A.REGION_CODE";
    	
    	TParm result = new TParm(this.select(sql));

		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
    }
    
    /**��ѯ������ҩƷ��Ϣ*/
    public TParm updateQuery(TParm parm) {

    	String cond1 = "";
    	String orderCode = parm.getValue("ORDER_CODE");
    	
    	if(null != orderCode &&!"".equals(orderCode)){
    		cond1 = " AND A.ORDER_CODE LIKE '%"+orderCode+"%' ";
    	}
    	
    	String cond2 = "";
    	String regionCode = parm.getValue("REGION_CODE");
    	    	
    	if(null != regionCode &&!"".equals(regionCode)){
    		cond2 = " AND A.REGION_CODE = '"+regionCode.trim()+"' ";
    	}
    	   
    	String cond3 = "";
    	String active_flg = parm.getValue("ACTIVE_FLG");
    	    	
    	if(null != active_flg &&!"".equals(active_flg)){
    		cond3 = " AND ACTIVE_FLG = '"+active_flg.trim()+"' ";
    	}

    	String sql = "SELECT " +
    					"A.ORDER_CODE, A.REGION_CODE, A.HIS_ORDER_CODE, " +
    					"B.ORDER_DESC, B.SPECIFICATION " +
    				"FROM SYS_FEE_SPC A, PHA_BASE B " +
    				"WHERE A.ORDER_CODE = B.ORDER_CODE " + 
    				cond1 +
    				cond2 + 
    				cond3 +
    				" ORDER BY A.REGION_CODE";
    	
    	TParm result = new TParm(this.select(sql));

		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
    }
    
    /**��ѯPHA_BASE���Ƿ��и�ҩƷ*/
    public boolean queryBase(TParm parm){
    	
    	String sql = "SELECT ORDER_CODE FROM PHA_BASE WHERE ORDER_CODE='" + parm.getData("ORDER_CODE") + "'";
    	TParm result = new TParm(this.select(sql));
    	
    	if (result.getErrCode() < 0 || result.getCount()<=0) return false;
    	
    	return true;
    }
    
	/**���淽��*/
	public boolean save(TParm parm) {
		
//		if(!queryBase(parm)) return false;
		
		String sql = "INSERT INTO SYS_FEE_SPC " +
							"(ORDER_CODE,REGION_CODE,HIS_ORDER_CODE,ACTIVE_FLG,OPT_DATE) " +
					"VALUES('"+parm.getData("ORDER_CODE")+"','"+
								parm.getData("REGION_CODE")+"','"+
								parm.getData("HIS_ORDER_CODE")+"','"+
								parm.getData("ACTIVE_FLG")+"',SYSDATE)";

		TParm result = new TParm(this.update(sql));
		if (result.getErrCode() < 0) return false;
		
		return true;
	}
	
	/**���뱣�淽��*/
	public boolean importSave(TParm parm) {
		
		// ����֮ǰ�Ȳ�ѯ,���ݿ����Ƿ��иü�¼
		TParm result = SPCCodeMapHisTool.getInstance().updateQuery(parm);

		if (result.getCount("ORDER_CODE") > 0) {
			boolean flg = SPCCodeMapHisTool.getInstance().update(parm);
			if (!flg) return false;
		} else {
			boolean flg = SPCCodeMapHisTool.getInstance().save(parm);
			if (!flg) return false;
		}
			
		return true;
	}
	
    
	/**�޸ķ���*/
	public boolean update(TParm parm) {
			
		String sql = "UPDATE SYS_FEE_SPC " +
						 "SET " +
				 			"HIS_ORDER_CODE='" + parm.getData("HIS_ORDER_CODE") 
				 			+ "',ACTIVE_FLG='"+parm.getData("ACTIVE_FLG")+"',OPT_DATE=SYSDATE "+				
				 	"WHERE ORDER_CODE='"+ parm.getData("ORDER_CODE") + "' " +
						"AND REGION_CODE='"+parm.getData("REGION_CODE")+"'"+
						"AND ACTIVE_FLG='"+parm.getData("ACTIVE_FLG_UPDATE")+"'";
		TParm result = new TParm(this.update(sql));
		if (result.getErrCode() < 0) 
			return false;
		
		return true;
	}
	
    
	/**ɾ������*/
	public boolean delete(TParm parm) {
		
		String sql = "DELETE SYS_FEE_SPC " +
					"WHERE " +
						"ORDER_CODE='"+parm.getData("ORDER_CODE")+"' " +
					"AND REGION_CODE='"+parm.getData("REGION_CODE")+"'" +
					"AND ACTIVE_FLG='"+parm.getData("ACTIVE_FLG")+"'";
		TParm result = new TParm(this.update(sql));
		if (result.getErrCode() < 0) return false;
		
		return true;
	}
	
	/**
	 * �ж��Ƿ�һ��order_code��Ӧ���his_order_code
	 * @param parm
	 * @return
	 * @author shendr 2013.08.06
	 */
	public TParm queryCount(TParm parm){
		String sql="SELECT COUNT(HIS_ORDER_CODE) " +
				"FROM SYS_FEE_SPC " +
				"WHERE REGION_CODE='"
				+parm.getData("REGION_CODE")+"' " +
				"AND ORDER_CODE='"
				+parm.getData("ORDER_CODE")+"'";
		TParm result = new TParm(this.select(sql));
		return result;
	}
	
	/**
	 * �ж��Ƿ�����ͬ��REGION_CODE��HIS_ORDER_CODE��ӦACTIVE_FLGΪY
	 * @param parm
	 * @return
	 * @author shendr 2013.09.02
	 */
	public TParm queryCounts(TParm parm){
		String sql="SELECT  COUNT(ACTIVE_FLG) AS COUNTS FROM SYS_FEE_SPC "
					+"WHERE REGION_CODE='"+parm.getData("REGION_CODE")+"' "
					+"AND HIS_ORDER_CODE='"+parm.getData("HIS_ORDER_CODE")+"'";
		TParm result = new TParm(this.select(sql));
		return result;
	}
	
	/**
	 * ��ѯ��Ӧorder_code�Ŀ����
	 */
	public TParm queryQtyByOrderCode(TParm parm){
		String sql = "SELECT A.ORDER_CODE,B.STOCK_QTY,C.ORG_CHN_DESC "
					+"FROM SYS_FEE_SPC A,IND_STOCK B,IND_ORG C "
					+"WHERE A.ORDER_CODE='"+parm.getData("ORDER_CODE")+"'  "
					+"AND B.STOCK_QTY>0 "
					+"AND A.ORDER_CODE = B.ORDER_CODE "
					+"AND B.ORG_CODE = C.ORG_CODE ";
		TParm result = new TParm(this.select(sql));
		return result;
	}
	
	/**
	 * �ж���������������Ƿ��ж�Ӧ������
	 * @param parm
	 * @return
	 * @author shendr 2013.08.22
	 */
	public TParm queryLastOrder(TParm parm){
		String sql = "SELECT ORDER_CODE " +
				"FROM SYS_FEE_SPC WHERE ORDER_CODE='"
			+parm.getData("ORDER_CODE")+"'";
		TParm result = new TParm(this.select(sql));
		return result;
	}
	
	/**
	 * ����ORDER_CODE����ACTIVE_FLG�ֶ�Ϊδ����
	 * @return
	 * @author shendr 2013.08.22
	 */
	public TParm updateActiveFlg(TParm parm){
		String sql = "UPDATE SYS_FEE_SPC SET ACTIVE_FLG = 'N' " +
				"WHERE REGION_CODE='"+parm.getData("REGION_CODE")+"' " +
						"AND ORDER_CODE = '"+parm.getData("ORDER_CODE")+"'";
		TParm result = new TParm(this.update(sql));
		return result;
	}
	
	/**
	 * ����ORDER_CODE��ѯ��λ����λת����
	 * @param parm
	 * @return
	 */
	public TParm queryPha(TParm parm){
		String sql="SELECT A.PURCH_UNIT,A.STOCK_UNIT,A.DOSAGE_UNIT,A.MEDI_UNIT,B.PURCH_QTY, "
			        +"B.PURCH_QTY,B.STOCK_QTY,B.DOSAGE_QTY,B.MEDI_QTY,B.OPT_USER, "
			        +"B.OPT_DATE,B.OPT_TERM,B.REGION_CODE "
			        +"FROM PHA_BASE A,PHA_TRANSUNIT B "
			        +"WHERE A.ORDER_CODE = '"
			        +parm.getValue("ORDER_CODE")
			        +"' "
			        +"AND A.ORDER_CODE = B.ORDER_CODE";
		TParm result = new TParm(this.update(sql));
		return result;
	}
	
}
