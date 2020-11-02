package action.inv;

import com.dongyang.patch.Patch;
import com.dongyang.util.StringTool;
import com.dongyang.data.TParm;

import java.util.Calendar;
import java.util.Date;


import com.dongyang.db.TConnection;
import com.dongyang.db.TDBPoolManager;

import jdo.inv.INVSQL;
import jdo.inv.INVTool;
import jdo.spc.INDTool;
import jdo.spc.INDSQL;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.jdo.TJDODBTool;

/**
 * <p>
 * Title: ���ʿ��Զ�������ҵ
 * </p>
 * 
 * <p>  
 * Description: ���ʿ��Զ�������ҵ
 * </p>
 * 
 * <p>
 * Copyright: bluecore (c) 2013
 * </p>
 * 
 * <p> 
 * Company:
 * </p>
 * 
 * @author fux 2013.05.24 
 * @version 1.0  
 */   
                                                                                                                                                                                                                                         
public class INVAutoReqBatchPatch extends Patch {
	
	private static final String Flag_Y = "Y";
	private static final String Flag_N = "N";
	private static final String Flag_ONE = "1";
	private static final String Flag_ZERO = "0";
	/**
	 * @param args         
	 */
	public static void main(String[] args){
		
	} 
 
	//��ҩ�Ⲧ���޸ĳ�������ʿⲦ��
  
	public boolean run() {
		System.out.println("=====out=====");
		TConnection connection = TDBPoolManager.getInstance().getConnection();		
		// �õ����ʿⲦ�����ڲ���			
		TParm assignParm = new TParm(TJDODBTool.getInstance().select(INVSQL.getAllAssignorg())); 
		// �õ����ʿⲦ������ 		
		TParm parm = new TParm(TJDODBTool.getInstance().select(INVSQL.getINVSysParm()));
		// �Զ�������ʽ��0:ÿ��(��)��������, 1:���������(���ڰ�ȫ���ʱ) 2:����С�����(���ڰ�ȫ���ʱ)
		String fixedAmountFlagMain = parm.getValue("FIXEDAMOUNT_FLG", 0);
		if (null == fixedAmountFlagMain || "".equals(fixedAmountFlagMain)) {
			fixedAmountFlagMain = Flag_ZERO; 
		}

		// �Զ������� :2�����������������1����������
		String autoFillTypeMain = parm.getValue("AUTO_FILL_TYPE", 0);
		if (null == autoFillTypeMain || "".equals(autoFillTypeMain)) {
			autoFillTypeMain = Flag_ONE;
		} 
		//�õ��Զ���������
		TParm orgParm = new TParm(TJDODBTool.getInstance().select(INVSQL.queryOrgCodeAuto()));
		// ���ݼ��
		if (orgParm == null || orgParm.getCount() < 1)
			return false;
		TParm result = new TParm();  
		for (int i = 0; i < orgParm.getCount("ORG_CODE"); i++) {
			String orgCode = orgParm.getValue("ORG_CODE", i);
			String fromOrgCode = orgParm.getValue("DISPENSE_ORG_CODE",i) ;
			String orgType = orgParm.getValue("DISPENSE_ORG_CODE", i);
			TParm orgAutoParm = new TParm(TJDODBTool.getInstance().select(INVSQL.getINVORG(orgCode, Operator.getRegion())));
			//�Զ�������ʽ(0:ÿ��(��)��������, 1:���������(���ڰ�ȫ���ʱ) 2:����С�����(���ڰ�ȫ���ʱ))   
			String fixedAmountFlag = fixedAmountFlagMain;
			//�Զ���������2�����������������1����������  
			String autoFillType = autoFillTypeMain;  
			if (null != orgAutoParm && orgAutoParm.getCount() > 0) { 
				String fixedAmountFlagNew = orgAutoParm.getValue("FIXEDAMOUNT_FLG", 0);
				if (null != fixedAmountFlagNew || !"".equals(fixedAmountFlagNew)) {//���ҩ�ⲿ��û���趨�Զ�������ʽ ��ο� ҩ�����ͳһ�涨���Զ�������ʽ
					fixedAmountFlag = fixedAmountFlagNew;
				}
				String autoFillTypeNew = orgAutoParm.getValue("AUTO_FILL_TYPE", 0);
				if (null != autoFillTypeNew || !"".equals(autoFillTypeNew)) {////���ҩ�ⲿ��û���趨�Զ��������� ��ο� ҩ�����ͳһ�涨���Զ���������
					autoFillType = autoFillTypeNew;
				}
			}
			// ��������
			String toOrgCode =orgParm.getValue("DISPENSE_ORG_CODE", i);
			//�ӿ�ĸ���Ĳ���
			//���ű��������ж��Ƿ��в���
			String orgCodeAssign = orgCode; 			
			boolean flag = isAssignAuto(assignParm, orgCodeAssign);
			if (!flag) {// ����Ƿ� ������ѭ�� 
				System.out.println(orgCode+"------orgCode-nononoono-----");
				continue;
			}  
			//========�����������
			TParm parmD = new TParm(TJDODBTool.getInstance().select(INVSQL.queryStockM(orgCode,fromOrgCode, fixedAmountFlag))); 
			if ((null == parmD || parmD.getCount() < 1)) {// ���Ϊ���� ������һ�������ж�
				continue;   
			}    
				if(null != parmD && parmD.getCount()>0){
						//=========================�Զ����ɶ���
						String purorderNo = SystemTool.getInstance().getNo("ALL", "INV", "INV_PURORDER", "No");
						//���� --- ���ڶ��ǰ������ߣ�����̩���ǵ�������������  
						TParm purOrderM = getPurOrderMParm(purorderNo,orgCode,"19", fixedAmountFlag, autoFillType); 
						// �����Զ�������/ϸ         
						result = INVTool.getInstance().onSavePurOrderMAuto(purOrderM, parmD, connection);
						if (result.getErrCode() < 0) {  
							System.out.println("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
							connection.close();
							return false;
						}
//					else  {
//						// �õ����쵥��
//						String requestNo = SystemTool.getInstance().getNo("ALL", "INV", 
//								"INV_REQUEST", "No");
//						System.out.println("requestNo"+requestNo);
//						// ��װ����������Ϣ   
//						TParm parmM = getRequestMParm(requestNo, orgCode, 
//								toOrgCode, fixedAmountFlag, autoFillType);
//						// �����Զ�������/ϸ(��������ϸ��)  
//						result = INVTool.getInstance().onSaveRequestMAuto(parmM, 
//								parmD, connection);  
//						if (result.getErrCode() < 0) {  
//							System.out.println("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
//							connection.close();
//							return false;
//						} 	
//					}
				
				}   
		} 

		connection.commit();
		connection.close();
		return true;
	}

	/**
	 * �жϲ��Ž����Ƿ��Զ�����
	 * 
	 * @param assignParm
	 * @param orgCode
	 * @return
	 */
	public boolean isAssignAuto(TParm assignParm, String orgCode) {
		// ���ر�־ 
		System.out.println("assignParm"+assignParm);
		System.out.println("orgCode"+orgCode);
		boolean flag = false;
		// �������� 1�����ڣ�0����
		String cycleType = Flag_ZERO;
		String assignDay = ""; 
		if (null == assignParm || assignParm.getCount() < 0) {
			// ���û�в������ڷ���false;
			return false; 
		}
		// ѭ�� �õ��Ĳ��� ������ ��ʽ�������ڻ����·�               
		for (int i = 0; i < assignParm.getCount(); i++) {
			String orgCode1 = assignParm.getValue("ORG_CODE", i);
			if (orgCode.equals(orgCode1)) {
				cycleType = assignParm.getValue("CYCLE_TYPE", i);
				assignDay = assignParm.getValue("ASSIGNED_DAY", i);
				// �жϵ��� �Ƿ񲦲�
				flag = isAtuoToday(cycleType, assignDay);
				break;
			}       
		}
		return flag;
	}

	/**
	 * �жϽ����Ƿ��Զ�����
	 * 
	 * @return
	 */
	public boolean isAtuoToday(String cycleType, String assignDay) {
		boolean flag = false;
		if (Flag_ONE.equals(cycleType)) {// �����ڲ���
			//(�����տ�ʼ���в���)
			// �õ����������ڼ����գ�һ�����������ģ��壬��
			int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
			// ���¸�ʽ�����ڰ��ţ��գ�һ�����������ģ� �壬��
			String assday = getFromatAssingDay(assignDay);
			// ��ȡ��ǰ����ֵ��Ӧ������
			String subString = assday.substring(day, day + 1);
			if (Flag_Y.equals(subString)) {
				flag = true;
			} else {
				flag = false;  
			}
		}
		if (Flag_ZERO.equals(cycleType)) {// ���·ݲ���
			// �õ������Ǽ���,�õ������������
			int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
			// ��ȡ������ֵ��Ӧ������
			String subString = assignDay.substring(day-1, day);
			if (Flag_Y.equals(subString)) {
				flag = true;
			} else {         
				flag = false;
			}
		}
		return flag;
	}

	/**
	 * ���������������
	 * 
	 * @des ԭ�ȸ�ʽ ����һ�����������ģ��壬�����գ���Ϊ�����գ�һ�����������ģ��壬��
	 * @param assignDay
	 * @return 
	 */
	public String getFromatAssingDay(String assignDay) {
		int len = assignDay.length();
		String newStr = assignDay.substring(0, len - 1);
		String lastStr = assignDay.substring(len - 1);
		return lastStr + newStr;
	}

	/**
	 * ����Զ��������������PARM
	 * @param parm
	 * @return 
	 */
	private TParm getRequestMParm(String requestNo, String orgCode, String toOrgCode, String fixedType, String autoFillType){
		TParm parm = new TParm();
		parm.setData("REQUEST_NO", requestNo);
		//�������ͣ�����
		parm.setData("REQUEST_TYPE", "AUT"); 
		//��Ӧ����     
		parm.setData("FROM_ORG_CODE", orgCode);    
		//�������
		parm.setData("TO_ORG_CODE", toOrgCode);
		// �Զ�������ʽ
		parm.setData("FIXEDAMOUNT_FLG", fixedType);
		// �Զ���������
		parm.setData("AUTO_FILL_TYPE", autoFillType);
		return parm;
	}

	/**
	 * ��ö���/�ƻ����������PARM
	 * @param purOrderNo
	 * @param orgCode
	 * @param SUP_CODE
	 * @return
	 */
	private TParm getPurOrderMParm(String purOrderNo,String orgCode, String SUP_CODE, String fixedType, String autoFillType) {
		TParm parm = new TParm();
		parm.setData("PURORDER_NO",purOrderNo); 
		parm.setData("PURORDER_DATE",StringTool.getTimestamp(new Date()));  
		if(orgCode.equals("011201")){
			parm.setData("CON_ORG", "");
			parm.setData("CON_FLG", "N");
		}
		else {
			parm.setData("CON_ORG", orgCode);
			parm.setData("CON_FLG", "Y");
		}
		parm.setData("ORG_CODE", "011201");
		parm.setData("SUP_CODE", SUP_CODE);	
		parm.setData("FIXEDAMOUNT_FLG", fixedType);//�Զ�������ʽ     	
		parm.setData("AUTO_FILL_TYPE", autoFillType);//�Զ���������
		return parm;
	}

}
