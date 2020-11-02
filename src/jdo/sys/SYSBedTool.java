package jdo.sys;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company:JavaHis
 * </p>
 * 
 * @author JiaoY
 * @version 1.0
 */
public class SYSBedTool extends TJDOTool {
	/**
	 * ʵ��
	 */
	private static SYSBedTool instanceObject;

	/**
	 * �õ�ʵ��
	 * 
	 * @return PatTool
	 */
	public static SYSBedTool getInstance() {
		if (instanceObject == null)
			instanceObject = new SYSBedTool();
		return instanceObject;
	}

	/**
	 * ������
	 */
	public SYSBedTool() {
		setModuleName("sys\\SYSBedModule.x");
		onInit();
	}

	/**
	 * ��ѯȫ�ֶ�
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm queryAll(TParm parm) {
		// System.out.println("��ѯȫ�ֶ�");
		TParm result = query("selectall", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;

	}

	/**
	 * ���´�λ
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm upDate(TParm parm, TConnection conn) {
		// System.out.println("<-------------���´�λ-------------------->");
		TParm result = update("updateForAdm", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * ������λ�������������     chenxi
	 * @param parm
	 * @param conn
	 * @return
	 */
   public TParm selectBed(TParm parm ,TConnection conn){
		TParm result = query("selectBed", parm, conn);
		if (result.getErrCode()<0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
   }
	/**
	 * ԤԼ�޸�ԤԼ���λ
	 * 
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm upDateForResv(TParm parm, TConnection conn) {
		TParm result = update("updateForResv", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;

	}

	/**
	 * ��������
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm alloSysbed(TParm parm) {
		TParm result = update("alloSysbed", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}

		return result;
	}

	/**
	 * ��ѯ��λ�����ڵĲ�����˵�в���
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm queryRoomBed(TParm parm) {
		// System.out.println("��ѯ��λ�����ڵĲ�����˵�в���");
		TParm result = query("queryRoomBed", parm);
		// System.out.println("��ѯ��λ�����ڵĲ�����˵�в���result="+result);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}

		return result;

	}

	/**
	 * ��סǰ�Ѵ˲���������λ��ԤԼ���ǰ���崲��סԺ�����Ŵ�λ��
	 * 
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm clearForadmin(TParm parm, TConnection conn) {
		// System.out.println("�崲");
		TParm result = update("clearForadmin", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}

		return result;

	}

	/**
	 * ��סǰ�Ѵ˲���������λ��ԤԼ���ǰ���崲��סԺ�����Ŵ�λ��
	 * 
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm updataOccu(TParm parm, TConnection conn) {
		// System.out.println("�崲");
		TParm result = update("upDataOccu", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}

		return result;

	}

	/**
	 * ��סǰ�Ѵ˲���������λ��ԤԼ���ǰ���崲��סԺ�����Ŵ�λ��
	 * 
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm clearAllForadmin(TParm parm, TConnection conn) {
		// System.out.println("�崲");
		TParm result = update("clearForAlladmin", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}

		return result;

	}
	//==========add  by  chenxi 20130222==============
	/**
	 * �ı���������ֶ�
	 */
	public TParm changeLockBed(TParm parm,TConnection conn){
		
		TParm result = this.selectLockBed(parm, conn) ;
		if (result.getCount()>=1) {
			err("ERR:�˴������˴����У���ѡ�������մ�");
			result.setData("Err", "Code", "-1") ;
			result.setData("Err", "Name", "�˴������˴����У���ѡ�������մ�") ;
			return result;
		}
		result = this.updateLockBed(parm, conn) ;
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result ;
	}
	/**
	 * ��ѯ�˴��Ƿ���
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm  selectLockBed(TParm parm,TConnection conn){
		TParm result = query("selectLockBed", parm, conn);
		if (result.getErrCode()<0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * ���������ֶ�
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm updateLockBed(TParm parm,TConnection conn){
		TParm result = update("updateLockBed", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}

		return result;
	}
	//==========add  by  chenxi 20130222==============

	/**
	 * ת��
	 * 
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm changBed(TParm parm, TConnection conn) {
		// �崲
		TParm clearBed = new TParm();
		clearBed.setRowData(parm);
		TParm result = this.clearAllForadmin(clearBed, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		//===========================   chenxi �����������
		//��λ����
		TParm selectBed =  new TParm()  ;
		selectBed.setRowData(parm) ;
		result =  this.selectBed(selectBed, conn) ;
		if (result.getCount()>=1) {
			err("ERR:�˴��ѱ�������ѡ�������մ�");
			result.setData("Err", "Code", "-1") ;
			result.setData("Err", "Name", "�˴��ѱ�������ѡ�������մ�") ;
			return result;
		}
		//==========================   chenxi  �����������
		// ת��

		TParm changeBed = new TParm();
		changeBed.setRowData(parm);

		result = this.upDate(changeBed, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ��ѯ��ɫ���� ��ɫ����
	 * 
	 * @return TParm
	 */
	public TParm queryBedYellowRed(TParm parm) {
		TParm result = query("queryYellowRed", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ��˲����Ƿ��ڴ�
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm checkInBed(TParm parm) {
		TParm result = new TParm();
		result = query("checkBedStatus", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}

		return result;

	}

	/**
	 * �����崲 ������CASE_NO��մ�λ��
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm clearForAdm(TParm parm, TConnection conn) {
		TParm result = this.update("clearForAdm", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ȡ������
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm clearOCCUBed(TParm parm) {
		TParm result = this.update("clearOCCUBed", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * �Ƿ���ICU
	 * @param parm
	 * @return
	 */
	public boolean checkIsICU(String caseNO) {
		TParm result = new TParm();
		TParm inparm=new TParm();
        boolean icuFlg=false;
		inparm.setData("CASE_NO", caseNO);
		result = query("IsICUBed", inparm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return false;
		}
		//System.out.println(result.getBoolean("ICU_FLG",0)+"------------flg---------"+result.getBoolean("ICU_FLG"));
        icuFlg=result.getBoolean("ICU_FLG",0);
		return icuFlg;
	}
	
	/**
	 * �Ƿ���CCU
	 * @param parm
	 * @return
	 */
	public boolean checkIsCCU(String caseNO) {
		TParm result = new TParm();
		TParm inparm=new TParm();
        boolean ccuFlg=false;
		inparm.setData("CASE_NO", caseNO);
		result = query("IsCCUBed", inparm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return false;
		}
		//System.out.println(result.getBoolean("ICU_FLG",0)+"------------flg---------"+result.getBoolean("ICU_FLG"));
        ccuFlg=result.getBoolean("CCU_FLG",0);
		return ccuFlg;
	}
}
