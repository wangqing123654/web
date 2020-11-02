package jdo.inv;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>
 * Title: ��������Ӧ�����ʱ���ȶ�   
 * </p>
 *
 * <p>
 * Description: ��������Ӧ�����ʱ���ȶ�
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
 * @author fux 2013.6.6
 * @version 1.0
 */
public class INVCodeMapTool extends TJDODBTool {
	
	 /** ʵ��*/
    public static INVCodeMapTool instanceObject;

    /**�õ�ʵ��*/
    public static INVCodeMapTool getInstance() {
        if (instanceObject == null)
            instanceObject = new INVCodeMapTool();
        return instanceObject;
    }
     
    
    /**��ѯ������������Ϣ*/
    public TParm query(TParm parm) {
    	String cond1 = "";
    	String invCode = parm.getValue("INV_CODE");
    	
    	if(null != invCode &&!"".equals(invCode)){
    		cond1 = " AND A.INV_CODE LIKE '%"+invCode+"%' ";
    	}
    	 
    	String cond2 = "";
    	String supCode = parm.getValue("SUP_CODE");
    	    	
    	if(null != supCode &&!"".equals(supCode)){
    		cond2 = " AND A.SUP_CODE = '"+supCode.trim()+"' ";
    	}
    	    

    	String sql = "SELECT " +
    					"B.INV_CODE, A.SUP_CODE, A.SUP_INV_CODE, " +
    					"B.INV_CHN_DESC, B.DESCRIPTION " +
    				"FROM INV_CODE_MAP A, INV_BASE B " +
    				"WHERE A.INV_CODE = B.INV_CODE " + 
    				cond1 +
    				cond2 + 
    				" ORDER BY A.SUP_CODE";
    	System.out.println("����sql"+sql);
    	TParm result = new TParm(this.select(sql));

		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
    }
    
    /**��ѯ������������Ϣ*/
    public TParm updateQuery(TParm parm) {

    	String invCode = parm.getValue("INV_CODE");    	
    	String cond1 = " AND A.INV_CODE = '"+invCode+"' ";
    	
    	
    	String supCode = parm.getValue("SUP_CODE");    	    	
    	String	cond2 = " AND A.SUP_CODE = '"+supCode+"' ";    	   

    	String sql = "SELECT " +
    					"A.INV_CODE, A.SUP_CODE, A.SUP_INV_CODE, " +
    					"B.INV_CHN_DESC, B.DESCRIPTION " +
    				"FROM INV_CODE_MAP A, INV_BASE B " +
    				"WHERE A.INV_CODE = B.INV_CODE " + 
    				cond1 + 
    				cond2;
    	
    	TParm result = new TParm(this.select(sql));

		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
    }
    
    /**��ѯINV_BASE���Ƿ��и�����*/
    public boolean queryBase(TParm parm){
    	
    	String sql = "SELECT INV_CODE FROM INV_BASE WHERE INV_CODE='" + parm.getData("INV_CODE") + "'";
    	TParm result = new TParm(this.select(sql));
    	
    	if (result.getErrCode() < 0 || result.getCount()<=0) return false;
    	
    	return true;
    }
     
	/**���淽��*/
	public boolean save(TParm parm) {
		
		String sql = "INSERT INTO INV_CODE_MAP " +
							"(INV_CODE,SUP_CODE,SUP_INV_CODE) " +
					"VALUES('"+parm.getData("INV_CODE")+"','"+
								parm.getData("SUP_CODE")+"','"+
								parm.getData("SUP_INV_CODE")+"')";

		TParm result = new TParm(this.update(sql));
		if (result.getErrCode() < 0) return false;
		
		return true;
	}
	 
	/**���뱣�淽��*/
	public boolean importSave(TParm parm) {
		
		// ����֮ǰ�Ȳ�ѯ,���ݿ����Ƿ��иü�¼
		TParm result = INVCodeMapTool.getInstance().updateQuery(parm);

		if (result.getCount("INV_CODE") > 0) {
			boolean flg = INVCodeMapTool.getInstance().update(parm);
			if (!flg) return false;
		} else {
			boolean flg = INVCodeMapTool.getInstance().save(parm);
			if (!flg) return false;
		}
			  
		return true; 
	}
	
    
	/**�޸ķ���*/
	public boolean update(TParm parm) {
			
		String sql = "UPDATE INV_CODE_MAP " +
						 "SET " +
				 			"SUP_INV_CODE='" + parm.getData("SUP_INV_CODE") + "' "+				
				 	"WHERE INV_CODE='"+ parm.getData("INV_CODE") + "' " +
						"AND SUP_CODE='"+parm.getData("SUP_CODE")+"'" ;
		
		TParm result = new TParm(this.update(sql));
		if (result.getErrCode() < 0) 
			return false;
		
		return true;
	}
	
    
	/**ɾ������*/   
	public boolean delete(TParm parm) {
		
		String sql = "DELETE INV_CODE_MAP " +
					"WHERE " +
						"INV_CODE='"+parm.getData("INV_CODE")+"' " +
					"AND SUP_CODE='"+parm.getData("SUP_CODE")+"'";
		
		TParm result = new TParm(this.update(sql));
		if (result.getErrCode() < 0) return false;
		
		return true;
	}
	
}
