package com.javahis.ui.opd;

import jdo.ins.INSTJTool;
import jdo.odo.ODO;
import jdo.reg.Reg;
import jdo.sys.Pat;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.tui.DMessageIO;

/**
 * 
 * <p>
 * Title: ҽ������վ����
 * </p>
 * 
 * <p>
 * Description:ҽ������վ����������
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) xueyf 2012
 * </p>
 * 
 * <p>
 * Company:JavaHis
 * </p>
 * 
 * @author xueyf 2012.03.13
 * @version 1.0
 */
public class OdoMainBaseControl extends TControl implements DMessageIO {
	protected INSPat insPat = null;
	// pat����
	protected Pat pat;
	// odo ����
	protected ODO odo;
	// reg����
	Reg reg;
	/**
	 * ��ְ����
	 */
	private static String INS_CHENGZHITUIXIU = "12";
	/**
	 * ��ְ����
	 */
	private static String INS_CHENGZHILIXIU = "13";
	/**
	 * �Ǿӳ������
	 */
	private static String INS_CHENGJUCHENGNIANJUMING = "23";
	/**
	 * �Է�
	 */
	private static String INS_ZIFEI = "99";
	/**
	 * ��ְ��ͨ
	 */
	private static String INS_CHENGZHIPUTONG = "11";
	/**
	 * �Ǿ�������
	 */
	private static String INS_CHENGZHIXINSHENGER = "21";
	/**
	 * �Ǿ�ѧ����ͯ
	 */
	private static String INS_CHENGJUXUESHENGERTONG = "22";

	/**
	 * ��ʼ��ҽ���ӿ�
	 * 
	 * @param ctzCode
	 */
	protected void initINS() {
		insPat = new INSPat(this, pat);
	}

	public void onInit() {

		super.onInit();
		

	}

	/**
	 * �ڲ�ҽ���ӿ���
	 * 
	 * @author xueyf
	 * 
	 */
	class INSPat {
		TControl control = null;
		/**
		 * �Ƿ�ҽ����� true ҽ����� false ��ҽ�����
		 */
		private boolean isINSPat = false;
		/**
		 * ҽ������
		 */
		private INSTJTool insTool = null;

		/**
		 * �������
		 */
		private String ctzCode = "";

		/**
		 * ��ʼ���趨��ǰ�����Ƿ�����ҽ������
		 * 
		 * @param ctzCode
		 */
		public INSPat(TControl control, Pat pat) {
			this.control=control;
			if (insTool == null) {
				insTool = new INSTJTool();
//				TParm parm = insTool.orderCheck("", pat.getCtz1Code(),"O",reg.getInsPatType());
//				if (parm.getErrCode() != -1) {
//					isINSPat = true;
//					ctzCode = pat.getCtz1Code();
//				}
			}
		}

		public boolean isINSPat() {

			return isINSPat;
		}

//		/**
//		 * ����ҽ����У��
//		 * 
//		 * @param parm
//		 * @return
//		 */
//		public boolean saveInsCheck1(ODO odo) {
//			// ��ҽ��ֱ�ӷ���
//			boolean isCanSave = true;
//			if (!isINSPat) {
//				return true;
//			}
//			int newRow[] = odo.getOpdOrder().getNewRows();
//			// �Է�ҩƷ�ж�
//			// int ZF = 0;
//			for (int i : newRow) {
//				TParm ordParm = odo.getOpdOrder().getRowParm(i);
//				// �Է���ʾ
//				if (parm.getErrCode() == -2) {
//					control.messageBox(orderDesc + ":" + parm.getErrText());
//					// ZF++;
//				}
//				if (ctzCode.equals(INS_CHENGJUXUESHENGERTONG)// ��ͯ���ߴ���
//						&& parm.getErrCode() != -5) {
//
//					// �Ƕ�ͯ��ҩ������
//					if (control.messageBox("��ʾ��Ϣ Tips", orderDesc+"���ڷǶ�ͯҽ�����Ƿ�����",
//							control.YES_NO_OPTION) != 0) {
//						isCanSave = false;
//						break;
//					}
//					
//				}else if (ctzCode.equals(INS_CHENGJUXUESHENGERTONG)// ���ػ��ߴ��� // �޸����ر���
//						&& parm.getErrCode() != -6) {
//
//					// ��������ҩ������
//					if (control.messageBox("��ʾ��Ϣ Tips", orderDesc+"���ڷ�������ҩ��Χ���Ƿ�����",
//							control.YES_NO_OPTION) != 0) {
//						isCanSave = false;
//						break;
//					}
//				}else	if (parm.getErrCode() == -4) {// סԺ�ж�
//					// סԺ�жϲ�����
//					if (control.messageBox("��ʾ��Ϣ Tips", orderDesc+"����סԺ��ҩ��Χ���Ƿ�����",
//							control.YES_NO_OPTION) != 0) {
//						isCanSave = false;
//						break;
//					}
//				}
//			}
//			return isCanSave;
//		}
	}
}
