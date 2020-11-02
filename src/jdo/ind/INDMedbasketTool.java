package jdo.ind;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>
 * Title: ҩ���Tool
 * </p>
 *
 * <p>
 * Description: ҩ���Tool
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 *
 * <p>
 * Company: JavaHis
 * </p>
 *
 * @author Yuanxm 2012.08.30
 * @version 1.0
 */
public class INDMedbasketTool  extends TJDOTool {
    /**
     * ʵ��
     */
    public static INDMedbasketTool instanceObject;

    /**
     * �õ�ʵ��
     *
     * @return INDMedbasketTool
     */
    public static INDMedbasketTool getInstance() {
        if (instanceObject == null)
            instanceObject = new INDMedbasketTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public INDMedbasketTool() {
        setModuleName("ind\\INDMedbasketModule.x");
        onInit();
    }
    
    /**
     * ��ѯҩ���벡�����Ƿ��
     * @param parm
     * @return
     */
    public TParm onQuery(TParm parm){
    	String conditionSql = "";
    	if(parm.getCount() > 0 ){
    		//������
    		String mr_no = parm.getValue("MR_NO") ;
    		if(mr_no != null && !mr_no.equals("") ){
    			conditionSql += " MR_NO='"+mr_no+"' " ;
    		}
    		
    		//����ǩ��
    		String rx_no = parm.getValue("RX_NO");
    		if(!conditionSql.equals("")){
    			conditionSql += " AND RX_NO='"+rx_no+"' ";
    		}else{
    			conditionSql += "  RX_NO='"+rx_no+"' ";
    		}
    		
    		//���ӱ�ǩ
    		String basketId = parm.getValue("BASKET_ID");
    		if(null != basketId && !"".equals(basketId)){
	    		if(!conditionSql.equals("")){
	    			conditionSql += " AND BASKET_ID='"+basketId+"' ";
	    		}else{
	    			conditionSql += "  BASKET_ID='"+basketId+"' ";
	    		}
    		}
    	}else{
    		return new TParm();
    	}
    	String sql = " SELECT MR_NO,BASKET_ID,RX_NO,CASE_NO,PAT_NAME,SEX_TYPE,AGE "+
    				 " FROM IND_MEDBASKET ";
    	if(!conditionSql.equals("")){
    		sql += " WHERE "+conditionSql ;
    	}
    	
//    	System.out.println("---��ѯҩ���벡�����Ƿ��-------------sql: "+sql);
    	TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
}
