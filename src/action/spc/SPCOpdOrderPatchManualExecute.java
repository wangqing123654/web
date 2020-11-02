package action.spc;

import java.text.SimpleDateFormat;
import java.util.Date;

import jdo.spc.SPCOpdOrderPathTool;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

import action.spc.opdOrderClient.SPCOpdOrderSynchWsTool;

import com.dongyang.action.TAction;
import com.dongyang.config.TConfig;
import com.dongyang.data.TParm;

public class SPCOpdOrderPatchManualExecute extends TAction{

	 
	
	public TParm executeSync(TParm tparm) {
		
		TParm returnParm = new TParm();
		
		String dateStr = tparm.getValue("DATE_STR");//2012-04-01  
		/**ִ����־*/
		SimpleDateFormat logFormater = new SimpleDateFormat("yyMMdd HHmmss");
		
		SimpleDateFormat verifyFormate = new SimpleDateFormat("yyyy-MM-dd");
		
		TParm logParm = new TParm();
		
		String resultMsg = "";//������Ϣ
		String detialMsg = "";//��ϸ����������Ϣ
		int falseCount =0;//������
		int totalCount =0;//����
		
		try{
			
			if(null==dateStr || "".equals(dateStr.trim())){
				throw new Exception();
			}
			
			try{	//2012-04-01
				Date date = verifyFormate.parse(dateStr);
			}catch(Exception e){
				throw e;
			}
			
			
			logParm.setData("BATCH_CODE","OPD_ORDERͬ��");
			logParm.setData("START_TIME",logFormater.format(new Date()));
			
			SimpleDateFormat formater2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			TParm parm = new TParm();
			
			parm.setData("ORDER_DATE",dateStr);
			
			TParm result = SPCOpdOrderPathTool.getInstance().query(parm);
			
			int count = result.getCount();
			totalCount = count; 
						
			for(int i = 0; i < count; i++){
				
				String case_no = "";
				String rx_no = "";
				String seq_no = "";
				
				try{
					case_no = result.getValue("CASE_NO", i);
					rx_no = result.getValue("RX_NO", i);
					seq_no = result.getValue("SEQ_NO", i);
					
					String pha_check_code = result.getValue("PHA_CHECK_CODE", i);
					
					Object obj1 = result.getData("PHA_CHECK_DATE", i);
					Date pha_check_date = null;
					if(null != obj1) pha_check_date = (Date)obj1;
					
					String pha_check_date_str = null;
					if(null != pha_check_date) pha_check_date_str = formater2.format(pha_check_date);
					
					String pha_dosage_code = result.getValue("PHA_DOSAGE_CODE", i);
					
					Object obj2 = result.getData("PHA_DOSAGE_DATE", i);
					Date pha_dosage_date = null;
					if(null != obj2) pha_dosage_date = (Date)obj2;
					
					String pha_dosage_date_str = null;
					if(null != pha_dosage_date) pha_dosage_date_str = formater2.format(pha_dosage_date);
					
					String pha_dispense_code = result.getValue("PHA_DISPENSE_CODE", i);
					
					Object obj3 = result.getData("PHA_DISPENSE_DATE", i);
					Date pha_dispense_date = null;
					if(null != obj3) pha_dispense_date = (Date)obj3;
					
					String pha_dispense_date_str = null;
					if(null != pha_dispense_date) pha_dispense_date_str = formater2.format(pha_dispense_date);
					
					String pha_retn_code = result.getValue("PHA_RETN_CODE", i);
					
					Object obj4 = result.getData("PHA_RETN_DATE", i);
					Date pha_retn_date = null;
					if(null != obj4) pha_retn_date = (Date)obj4;
					
					String pha_retn_date_str = null;
					if(null != pha_retn_date) pha_retn_date_str = formater2.format(pha_retn_date);
								
					boolean rtn = synchHis(case_no,rx_no,seq_no,
							pha_check_code,pha_check_date_str,
							pha_dosage_code,pha_dosage_date_str,
							pha_dispense_code,pha_dispense_date_str,
							pha_retn_code,pha_retn_date_str);	
					
					if(!rtn) {
						falseCount = falseCount + 1;
						detialMsg = detialMsg + case_no + "-" + rx_no + "-" + seq_no + "(��������ݸ���δ���л����˸��²���ʱʧ��),\n";
					}
					
				}catch (Exception e){
					e.printStackTrace();
					falseCount = falseCount + 1;
					detialMsg = detialMsg + case_no + "-" + rx_no + "-" + seq_no + "(�ͻ�����������),\n";
				}
								
			}
		}catch (Exception e){
			e.printStackTrace();
			
			logParm.setData("END_TIME",logFormater.format(new Date()));
			logParm.setData("RESULT_DESC","ִ��ʧ��");
			
//			SPCOpdOrderPathTool.getInstance().save(logParm);
			
			returnParm.setData("RESULT_MEG", "ϵͳ����ִ��ʧ��");
			
			return returnParm;
		}
		
		
		logParm.setData("END_TIME",logFormater.format(new Date()));
		
//		if(null != detialMsg && detialMsg.length()>170){  ���������־����Ҫ�жϳ���
//			detialMsg = detialMsg.substring(0, 170);
//		}
		
		resultMsg="�ܼ�¼:"+totalCount+" ������:"+ String.valueOf(falseCount)+".\n"+detialMsg;
		logParm.setData("RESULT_DESC",resultMsg);
		
//		SPCOpdOrderPathTool.getInstance().save(logParm);
		
		returnParm.setData("RESULT_MEG", resultMsg);
		
		return returnParm;
		
	}
	
	/**ͬ������*/
	private boolean synchHis(String case_no,String rx_no,String seq_no,
			String pha_check_code,String pha_check_date_str,
			String pha_dosage_code,String pha_dosage_date_str,
			String pha_dispense_code,String pha_dispense_date_str,
			String pha_retn_code,String pha_retn_date_str) throws Exception{
		
		boolean _updateOpdOrder__return = false;
		
		try{
	      
			 _updateOpdOrder__return = updateOpdOrder(case_no,rx_no,seq_no,
															pha_check_code,pha_check_date_str,
															pha_dosage_code,pha_dosage_date_str,
															pha_dispense_code,pha_dispense_date_str,
															pha_retn_code,pha_retn_date_str);
		}catch(Exception e){
			throw new Exception();
		}
      
      return _updateOpdOrder__return;
	}
	
	
	private static  TConfig getProp() {
        TConfig config = TConfig.getConfig("WEB-INF\\config\\system\\TConfig.x");
        return config;
	}
 
	 public  static String getWebServicesIp(){
		 TConfig config = getProp() ;
		 String url = config.getString("", "WEB_SERVICES_58IP");
		 return url;
	 }

	 
	 public boolean updateOpdOrder(String case_no,String rx_no,String seq_no,
				String pha_check_code,String pha_check_date_str,
				String pha_dosage_code,String pha_dosage_date_str,
				String pha_dispense_code,String pha_dispense_date_str,
				String pha_retn_code,String pha_retn_date_str)throws Exception {

			 String ip = getWebServicesIp();
		     String url = "http://"+ip+"/services/SPCOpdOrderSynchWs" ;
		     //System.out.println("url--:"+url);
		     JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean(); 
		     factory.setAddress(url); 
		     factory.setServiceClass(SPCOpdOrderSynchWsTool.class); 
		     SPCOpdOrderSynchWsTool service = (SPCOpdOrderSynchWsTool) factory.create();
		     boolean  result = service.updateOpdOrder(case_no,rx_no,seq_no,
						pha_check_code,pha_check_date_str,
						pha_dosage_code,pha_dosage_date_str,
						pha_dispense_code,pha_dispense_date_str,
						pha_retn_code,pha_retn_date_str);;
		     return result ;
		}

}
