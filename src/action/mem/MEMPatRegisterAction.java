package action.mem;

import jdo.mem.MEMPatRegisterTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>
 * Title: ������Աע��
 * </p>
 * 
 * <p>
 * Description: ������Աע��
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author duzhw 2014.01.14
 * @version 4.5
 */
public class MEMPatRegisterAction extends TAction {
	
	/**
	 * ִ�б�������
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onSave(TParm parm) {
		TConnection conn = getConnection();
		TParm result = new TParm();
		try {
			//1:�����Աע���sql��MEM_PATINFO��
			result = MEMPatRegisterTool.getInstance().insertMemPatinfoDate(parm,conn);
	        if (result.getErrCode() < 0) {
	            err("ERR:" + result.getErrCode() + result.getErrText() +
	                result.getErrName());
	            conn.rollback();
	            conn.close();
	            return result;
	        }
			
			//2:�����Ա�����ױ�sql��MEM_TRADE��-��STATUS=0�½���
	        result = MEMPatRegisterTool.getInstance().insertMemTradeData(parm,conn);
	        if (result.getErrCode() < 0) {
	            err("ERR:" + result.getErrCode() + result.getErrText() +
	                result.getErrName());
	            conn.rollback();
	            conn.close();
	            return result;
	        }
	        
	        //3:��ע��Ĳ�����Ϣͬ����sys_patinfo��
	        
		} catch (Exception e) {
			e.printStackTrace();
			err(e.getMessage());
		}
		conn.commit();
		conn.close();
		return result;
	}
	
	/**
	 * ���ݲ����Ų�ѯmem_patinfo������
	 */
	public TParm onQueryMem(TParm parm) {
		TParm result = new TParm();
		try {
			result = MEMPatRegisterTool.getInstance().onQueryMem(parm);
	        if (result.getErrCode() < 0) {
	            err("ERR:" + result.getErrCode() + result.getErrText() +
	                result.getErrName());
	            return result;
	        }
		} catch (Exception e) {
			e.printStackTrace();
			err(e.getMessage());
		}
		return result;
	}
	
	/**
	 * ��ѯ��Ա���ױ�����-Ԥ�쿨��Ϣ
	 */
	public TParm onQueryTrade(TParm parm) {
		TParm result = new TParm();
		try {
			result = MEMPatRegisterTool.getInstance().onQueryTrade(parm);
	        if (result.getErrCode() < 0) {
	            err("ERR:" + result.getErrCode() + result.getErrText() +
	                result.getErrName());
	            return result;
	        }
		} catch (Exception e) {
			e.printStackTrace();
			err(e.getMessage());
		}
		return result;
	}
	/**
	 * �޸Ļ�Ա��Ϣ
	 */
	public TParm updateMemData(TParm parm) {
		TConnection conn = getConnection();
		TParm result = new TParm();
		try {
			//��Ա��Ϣ�޸�
			result = MEMPatRegisterTool.getInstance().updateMemData(parm,conn);
			//System.out.println("updateMemData-result.getErrCode()="+result.getErrCode());
	        if (result.getErrCode() < 0) {
	            err("ERR:" + result.getErrCode() + result.getErrText() +
	                result.getErrName());
	            conn.rollback();
	            conn.close();
	            return result;
	        }
	        
	        result = MEMPatRegisterTool.getInstance().updatePatinfoData(parm,conn);
	        
	        if (result.getErrCode() < 0) {
	            err("ERR:" + result.getErrCode() + result.getErrText() +
	                result.getErrName());
	            conn.rollback();
	            conn.close();
	            return result;
	        }
	        //���ױ��޸�
	        result = MEMPatRegisterTool.getInstance().updateMemTrade(parm,conn);
	        if (result.getErrCode() < 0) {
	            err("ERR:" + result.getErrCode() + result.getErrText() +
	                result.getErrName());
	            conn.rollback();
	            conn.close();
	            return result;
	        }
			
		} catch (Exception e) {
			e.printStackTrace();
			err(e.getMessage());
		}
		conn.commit();
		conn.close();
		return result;
	}
	/**
	 * ������Ա��Ϣ
	 */
	public TParm insertMemData(TParm parm) {
		TConnection conn = getConnection();
		TParm result = new TParm();
		try {
			//��Ա��Ϣ����
			result = MEMPatRegisterTool.getInstance().insertMemPatinfoDate(parm,conn);
	        if (result.getErrCode() < 0) {
	            err("ERR:" + result.getErrCode() + result.getErrText() +
	                result.getErrName());
	            conn.rollback();
	            conn.close();
	            return result;
	        }
	        //�������ױ�
//	        result = MEMPatRegisterTool.getInstance().insertMemTrade(parm,conn);
//	        if (result.getErrCode() < 0) {
//	            err("ERR:" + result.getErrCode() + result.getErrText() +
//	                result.getErrName());
//	            conn.rollback();
//	            conn.close();
//	            return result;
//	        }
	        //������Ϣ��ͬ��SYS_PATINFO
	        result = MEMPatRegisterTool.getInstance().updatePatinfoData(parm,conn);
	        if (result.getErrCode() < 0) {
	            err("ERR:" + result.getErrCode() + result.getErrText() +
	                result.getErrName());
	            conn.rollback();
	            conn.close();
	            return result;
	        }
	        
			
		} catch (Exception e) {
			e.printStackTrace();
			err(e.getMessage());
		}
		conn.commit();
		conn.close();
		return result;
	}
	/**
	 * ��������
	 */
	public TParm newSysPatinfo(TParm parm) {
		TConnection conn = getConnection();
		TParm result = new TParm();
		try {
			//1���������ݲ��뵽sys_patinfo��
			result = MEMPatRegisterTool.getInstance().newSysPatinfo(parm,conn);
			//System.out.println("newSysPatinfo-result.getErrCode()="+result.getErrCode());
	        if (result.getErrCode() < 0) {
	            err("ERR:" + result.getErrCode() + result.getErrText() +
	                result.getErrName());
	            conn.rollback();
	            conn.close();
	            return result;
	        }
	        //2���������ݲ��뵽MEM_PATINFO��-�ǻ�Ա
	        result = MEMPatRegisterTool.getInstance().insertNoMemPatinfoDate(parm,conn);
	        if (result.getErrCode() < 0) {
	            err("ERR:" + result.getErrCode() + result.getErrText() +
	                result.getErrName());
	            conn.rollback();
	            conn.close();
	            return result;
	        }
//	        //3:�ж��Ƿ����Ԥ�쿨ҵ�� �ǣ����뽻�ױ�
//	        if(parm.getValue("MEM_CODE").toString().length()>0){
//	        	//�������ױ�
//		        result = MEMPatRegisterTool.getInstance().insertMemTrade(parm,conn);
//		        if (result.getErrCode() < 0) {
//		            err("ERR:" + result.getErrCode() + result.getErrText() +
//		                result.getErrName());
//		            conn.rollback();
//		            conn.close();
//		            return result;
//		        }
//	        }
			
		} catch (Exception e) {
			e.printStackTrace();
			err(e.getMessage());
		}
		conn.commit();
		conn.close();
		return result;
	}
	/**
	 * ���뽻�ױ�
	 */
	public TParm insertMemTradeData(TParm parm) {
		TConnection conn = getConnection();
		TParm result = new TParm();
		try {
	        //�������ױ�
	        result = MEMPatRegisterTool.getInstance().insertMemTrade(parm,conn);
	        if (result.getErrCode() < 0) {
	            err("ERR:" + result.getErrCode() + result.getErrText() +
	                result.getErrName());
	            conn.rollback();
	            conn.close();
	            return result;
	        }
			
		} catch (Exception e) {
			e.printStackTrace();
			err(e.getMessage());
		}
		conn.commit();
		conn.close();
		return result;
	}
	/**
	 * �޸Ľ��ױ�
	 */
	public TParm updateMemTrade(TParm parm) {
		TConnection conn = getConnection();
		TParm result = new TParm();
		try {
	        //���ױ��޸�
	        result = MEMPatRegisterTool.getInstance().updateMemTrade(parm,conn);
	        if (result.getErrCode() < 0) {
	            err("ERR:" + result.getErrCode() + result.getErrText() +
	                result.getErrName());
	            conn.rollback();
	            conn.close();
	            return result;
	        }
			
		} catch (Exception e) {
			e.printStackTrace();
			err(e.getMessage());
		}
		conn.commit();
		conn.close();
		return result;
	}
	/**
	 * ��ȡ�����Աע���sql��MEM_PATINFO��
	 */
//	public String getMemPatinfoSql(TParm parm){
//		String sql = "";
//		
//		System.out.println("��ӡ�����Աע���sql��"+sql);
//		return sql;
//	}
	
	/**
	 * ��ȡ�����Ա�����ױ�sql��MEM_TRADE��
	 */
//	public String getMemTradeSql(TParm parm){
//		String sql = "";
//		
//		System.out.println("��ӡ�����Ա�����ױ�sql��"+sql);
//		return sql;
//	}

}
