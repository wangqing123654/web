package jdo.pha;


import com.dongyang.jdo.*;
import com.dongyang.data.TParm;
/**
 * 
 * <p>
 * Title:������ҩҩƷ�����ֵ�
 * </p>
 * 
 * <p>
 * Description:������ҩҩƷ�����ֵ�
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) caoyong 2014
 * </p>
 * 
 * <p>
 * Company: Bluecore
 * </p>
 * 
 * @author caoyong 201402119
 * @version 1.0
 */
public class PHA_AntiBioticTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static PHA_AntiBioticTool instanceObject;
    
    
    /**
	 * ������
	 */
	public PHA_AntiBioticTool() {
		setModuleName("pha\\PHA_ANTIBIOTICModule.x");
		onInit();
	}
    /**
     * �õ�ʵ��
     * @return BILInvrcptTool
     */
    public static PHA_AntiBioticTool getInstance() {
        if (instanceObject == null)
            instanceObject = new PHA_AntiBioticTool();
        return instanceObject;
    }

    
    /**
     * ��ѯȫ��������ϸ
     * @return TParm
     */
    public TParm selectAllData() {
        TParm result = query("selectall");
        if(result.getErrCode() < 0)
        {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }
    
    
    /**
     * 
     * @return TParm
     */
    public TParm selectM(TParm parm) {
    	String pha_desc="";
    	String ant_code="";
    	if(!"".equals(parm.getValue("PHA_ANTIBIOTIC_DESC"))&&parm.getValue("PHA_ANTIBIOTIC_DESC")!=null){
    		pha_desc=" AND PHA_ANTIBIOTIC_DESC LIKE '%"+parm.getValue("PHA_ANTIBIOTIC_DESC")+"%' ";
    	}
    	if(!"".equals(parm.getValue("ANTIBIOTIC_CODE"))&&parm.getValue("ANTIBIOTIC_CODE")!=null){
    		ant_code=" AND ANTIBIOTIC_CODE= '"+parm.getValue("ANTIBIOTIC_CODE")+"' ";
    	}
        String sql="SELECT  PHA_ANTIBIOTIC_NO,PHA_ANTIBIOTIC_DESC," +
        		" ANTIBIOTIC_CODE  " +
        		" FROM PHA_ANTIBIOTIC  WHERE 1=1 "+pha_desc +ant_code;
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if(result.getErrCode() < 0)
        {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }
    /**
     * ��ѯȫ��������ϸ
     * @return TParm
     */
    public TParm selectDataB(TParm parm) {
    	TParm result = query("selectaB",parm);
    	if(result.getErrCode() < 0)
    	{
    		err(result.getErrCode() + " " + result.getErrText());
    		return result;
    	}
    	return result;
    }
    /**
     * ��ѯȫ��������ϸ
     * @return TParm
     */
    public TParm selectDataC(TParm parm) {
    	TParm result = query("selectaC",parm);
    	if(result.getErrCode() < 0)
    	{
    		err(result.getErrCode() + " " + result.getErrText());
    		return result;
    	}
    	return result;
    }
    /**
     * ��ѯȫ��������ϸ
     * @return TParm
     */
    public TParm selectDataA(TParm parm) {
    	TParm result = query("selectaA",parm);
    	if(result.getErrCode() < 0)
    	{
    		err(result.getErrCode() + " " + result.getErrText());
    		return result;
    	}
    	return result;
    }
   /**
    * ��ӡƱ��
    * @return TParm
    */
   public TParm insertDataA(TParm parm) {
	   
       TParm result = update("insertdataA",parm);
       if(result.getErrCode() < 0)
       {
           err(result.getErrCode() + " " + result.getErrText());
           return result;
       }
       return result;
   }
   /**
    * ��ӡƱ��
    * @return TParm
    */
   public TParm insertDataB(TParm parm) {
	   
	   TParm result = update("insertdataB",parm);
	   if(result.getErrCode() < 0)
	   {
		   err(result.getErrCode() + " " + result.getErrText());
		   return result;
	   }
	   return result;
   }
   /**
    * ���£����ϵ�
    * @return TParm
    */
   public TParm updatedataA(TParm parm) {
       TParm result = update("updatedataA",parm);
       if(result.getErrCode() < 0)
       {
           err(result.getErrCode() + " " + result.getErrText());
           return result;
       }
       return result;
   }
       /**
        * ���£����ϵ�
        * @return TParm
        */
       public TParm updatedataB(TParm parm) {
    	   TParm result = update("updatedataB",parm);
    	   if(result.getErrCode() < 0)
    	   {
    		   err(result.getErrCode() + " " + result.getErrText());
    		   return result;
    	   }
    	   return result;
   }
       /**
        * ���£����ϵ�
        * @return TParm
        */
       public TParm deletedataA(TParm parm) {
    	   TParm result = update("deletedataA",parm);
    	   if(result.getErrCode() < 0)
    	   {
    		   err(result.getErrCode() + " " + result.getErrText());
    		   return result;
    	   }
    	   return result;
       }
       
       /**
        * ���£����ϵ�
        * @return TParm
        */
       public TParm deletedataB(TParm parm) {
    	   TParm result = update("deletedataB",parm);
    	   if(result.getErrCode() < 0)
    	   {
    		   err(result.getErrCode() + " " + result.getErrText());
    		   return result;
    	   }
    	   return result;
       }
       
       /**
   	 */
     
       

}
