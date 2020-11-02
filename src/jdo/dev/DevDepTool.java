package jdo.dev;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import jdo.sys.SystemTool;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.util.StringTool;

/**
 * <p>Title:�豸�۾ɼ���</p>
 *
 * <p>Description:�豸�۾ɼ��� </p>
 *
 * <p>Copyright: Copyright (c) 2013</p>
 *
 * <p>Company:javahis </p>
 *
 * @author  fux
 * @version 1.0
 */
public class DevDepTool extends TJDOTool{ 
	
	/**
	 * ʵ��
	 */ 
	private static DevDepTool instanceObject;
	 
	/**
	 * �õ�ʵ�� 
	 * @return
	 */
	public static DevDepTool getInstance() {
		if (null == instanceObject) {
			instanceObject = new DevDepTool();
		}
		return instanceObject;
	}
	
	/**
	 * �۾��㷨
	 * @param parm
	 * @return
	 */
	public TParm selectSeqDevInf(TParm parm,double unitPrice,String inWarehouseDate) {
		DecimalFormat formatObject = new DecimalFormat("###########0.00");	
		//�����۾����޺͹�������   
		TParm parmDep = new TParm();
		Double depYear  = parm.getDouble("DEP_DEADLINE",0);  
		Double depMonth  =  depYear*12;
		//���۾� 
		Double mdepPrice = unitPrice/depMonth ;
		formatObject.format(mdepPrice); 
		//(�������ʲ�)���۾�����
		Double MMonth1 = 0.00;  
		Double MMonth2 = 0.00;
		MMonth1 = (double) Month(inWarehouseDate); 
		//(�����ʲ�)���۾�����  
	    MMonth2 = (double) (Month(inWarehouseDate) + 1); 		
		//�ж������ʲ���� 
		if("Y".equals(parm.getData("INTANGIBLE_FLG",0))){
			//�۾��´ӵ�������
			//�ۼ��۾�   
			Double depPrice = mdepPrice * MMonth2; 
			Double currPrice =  unitPrice -  depPrice;
			parmDep.setData("DEP_PRICE",formatObject.format(depPrice));
			parmDep.setData("CURR_PRICE",formatObject.format(currPrice));
		}
		else{ 
			//�۾��´���һ��������
			Double depPrice = mdepPrice * MMonth1;
			Double currPrice =  unitPrice -  depPrice;
			parmDep.setData("DEP_PRICE",formatObject.format(depPrice));
			parmDep.setData("CURR_PRICE",formatObject.format(currPrice));
		}  
		parmDep.setData("MDEP_PRICE",formatObject.format(mdepPrice));
		//�õ��۾�����
		//�۾����޻�����۾����� 
		//ԭֵ/�۾����� = ���۾�ֵ
		//���۾�����* ���۾�ֵ = �ۼ��۾���
		//ԭֵ-�ۼ��۾��� = ��ֵ
		//�������豸���²��۾�(��ֵΪ0���۾�) 
		//�����豸���¼���(���¾���)
		//�����µ׹��ˣ������۾ɣ�����     
		return parmDep; 
	} 
	/** 
	 * �������۾���������
	 * @param parm
	 * @return 
	 */ 
	public int Month(String InwareDate){
		//depDate�������
		GregorianCalendar ApDate = new GregorianCalendar(); //ϵͳ����
	    String sYear = Integer.toString(ApDate.get(Calendar.YEAR));
	    String sMonth = Integer.toString(ApDate.get(Calendar.MONTH) + 1);
		int year1 = Integer.parseInt(sYear);
		int year2 = Integer.parseInt(InwareDate.substring(0,4));
		int month1 = Integer.parseInt(sMonth);
		int month2 = Integer.parseInt(InwareDate.substring(6,7));
	    int year = (year2-year1)*12;
	    int month = month2-month1;
	    int depMonth = year-month; 
		//�������� ---ȡ������,��ȡ������  
		//������� ---ȡ������,��ȡ������ 
		//��������õ���һ������12
		//������� 
		return depMonth; 
	} 
	
	/** 
	 * �½�����۾���ؽ��
	 * @param parm
	 * @return  
	 */   
	public TParm UpdateMonthDep(TParm parm){ 
		System.out.println("�½�����۾���ؽ��parm==="+parm);
           String sql = " UPDATE DEV_STOCKDD SET "+ 
				        " DEP_PRICE='"+parm.getValue("DEP_PRICE",0)+"' ,"+  
				        " CURR_PRICE='"+parm.getValue("CURR_PRICE",0)+"' "+  
			            " WHERE DEV_CODE='"+parm.getValue("DEV_CODE",0)+"' " + 
			            " AND DEVSEQ_NO ='"+parm.getValue("DEVSEQ_NO",0)+"' " +
			            " AND REGION_CODE = '"+parm.getValue("REGION_CODE",0)+"' "; 
           System.out.println("�½�����۾���ؽ��sql"+sql);
           TParm result = new TParm(TJDODBTool.getInstance().update(sql)); 
			if (result.getErrCode() < 0) {
				return result; 
			} 
           return result; 
              
	} 
	

}
