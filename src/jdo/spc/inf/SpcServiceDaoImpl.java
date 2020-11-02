package jdo.spc.inf;

import java.util.List;


import jdo.pha.inf.dto.SpcOpdDiagrecDto;
import jdo.pha.inf.dto.SpcOpdDrugAllergyDto;
import jdo.pha.inf.dto.SpcOpdOrderDto;
import jdo.pha.inf.dto.SpcOpdOrderDtos;
import jdo.pha.inf.dto.SpcRegPatadmDto;
import jdo.pha.inf.dto.SpcSysPatinfoDto;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;


/**
 * <p>
 * Title: ������ werbservice�ӿ�-DAOʵ����
 * </p>
 * 
 * <p>
 * Description: ������ werbservice�ӿ�DAOʵ����
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * 
 * <p>
 * Company: Javahis
 * </p>
 * 
 * @author Liyh
 * @version 1.0
 */
public class SpcServiceDaoImpl extends TJDOTool {
	/**
	 * ʵ��
	 */
	public static SpcServiceDaoImpl instanceObject;

	// ��ǩ��˸����30
	private static final Integer LIGHT_NUM = 30;

	// ��ǩ��˸����0
	private static final Integer LIGHT_NUM_SEND = 0;

	// ���ؽ��-��ȷ
	private static final String RESULT_FLAG_TRUE = "1";

	// ���ؽ��-����
	private static final String RESULT_FLAG_FALSE = "0";

	/**
	 * �õ�ʵ��
	 * 
	 * @return INDTool
	 */
	public static SpcServiceDaoImpl getInstance() {
		if (instanceObject == null)
			instanceObject = new SpcServiceDaoImpl();
		return instanceObject;
	}

	/**
	 * ������
	 */
	public SpcServiceDaoImpl() {
		onInit();
	}

	
	/**
	 * ����-�ż���ҽ����ϸ��
	 * @param obj
	 * @return
	 */
	private static boolean saveOpdOrder(List<SpcOpdOrderDto> spcOpdOrderDtosList){
		if (null != spcOpdOrderDtosList && spcOpdOrderDtosList.size() > 0) {
			TParm parm = new TParm();
			//���� case_no,rx_no ɾ������ҽ����Ϣ
//			System.out.println("SpcDaoImpl-125-------------sql:" + SpcServiceSQL.deleteOPDOrder(spcOpdOrderDtosList.get(0)));
			parm =  new TParm(TJDODBTool.getInstance().update(SpcServiceSQL.deleteOPDOrder(spcOpdOrderDtosList.get(0))));
			for (SpcOpdOrderDto obj : spcOpdOrderDtosList) {
				obj = getSpcOrderCodeByHisOrderCode(obj);
				obj.setPhaCheckCode("");
				obj.setPhaCheckDate("");
				obj.setPhaDispenseCode("");
				obj.setPhaDispenseDate("");
				obj.setPhaDosageCode("");
				obj.setPhaDosageDate("");
				//obj.setExecDeptCode("040102");
//				System.out.println("-----rx_no:"+obj.getRxNo()+",atc_flg:"+obj.getAtcFlg()+",region_code:"+obj.getRegionCode());
//				System.out.println("SpcDaoImpl-92-------------sql:" + SpcServiceSQL.saveOpdOrderFromHisBySpc(obj));
				parm = new TParm(TJDODBTool.getInstance().update(SpcServiceSQL.saveOpdOrderFromHisBySpc(obj)));
//				System.out.println("--------parm: "+parm);
				if (parm.getErrCode() < 0) {
					return false;
				}
			}
		} 
		return true;
	}
	
	/**
	 * ���没��������Ϣ-SYS_PATIONINFO
	 * @return
	 */
	private static boolean saveSysPationInfo(List<SpcSysPatinfoDto> list){
		if (null != list && list.size() > 0) {
			for(SpcSysPatinfoDto obj : list){
				String sql = SpcServiceSQL.deleteSysPationInfo(obj);
//				System.out.println("����������Ϣ-----deleteSysPationInfo.sql:"+sql);
				//��ɾ
				TParm delParm = new TParm(TJDODBTool.getInstance().update(sql));
				if(delParm.getErrCode()<0){
					return false;
				}
				sql = SpcServiceSQL.saveSysPationInfo(obj);
//				System.out.println("����������Ϣ-----saveSysPationInfo:"+sql);
				//�󱣴�
				TParm saveParm = new TParm(TJDODBTool.getInstance().update(sql));
				if (saveParm.getErrCode()<0) {
					return false;
				}
			}
			return true;
		}
		return true;
	}
	
	/**
	 * ����-����������¼-OPD_DRUGALLERGY 
	 * @return
	 */
	private static boolean saveOpdDrugallergy(List<SpcOpdDrugAllergyDto> list){
		if (null != list && list.size() > 0) {
			for(SpcOpdDrugAllergyDto obj : list){
				String sql = SpcServiceSQL.deleteOpdDrugallergyById(obj);
//				System.out.println("����������¼-----deleteOpdDrugallergyById:"+sql);
				//��ɾ
				TParm delParm = new TParm(TJDODBTool.getInstance().update(sql));
				if(delParm.getErrCode()<0){
					return false;
				}
				sql = SpcServiceSQL.saveOpdDrugallergy(obj);
//				System.out.println("����������¼-----saveOpdDrugallergy:"+sql);
				//�󱣴�
				TParm saveParm = new TParm(TJDODBTool.getInstance().update(sql));
				if (saveParm.getErrCode()<0) {
					return false;
				}
			}
			return true;
		}		
		return true;
	}

	
	/**
	 * ����-�ż�����ϵ�-OPD_DIAGREC
	 * @return
	 */
	@SuppressWarnings("unused")
	private static boolean saveOpdDiagrec(List<SpcOpdDiagrecDto> list){
		if (null != list && list.size() > 0) {
			for(SpcOpdDiagrecDto obj : list){
				String sql = SpcServiceSQL.deleteOpdDiagrec(obj);
//				System.out.println("�ż�����ϵ�------deleteOpdDiagrec:"+sql);
				//��ɾ
				TParm delParm = new TParm(TJDODBTool.getInstance().update(sql));
				if(delParm.getErrCode()<0){
					return false;
				}
				sql = SpcServiceSQL.saveOpdDiagrec(obj);
//				System.out.println("�ż�����ϵ�------saveOpdDiagrec:"+sql);
				//�󱣴�
				TParm saveParm = new TParm(TJDODBTool.getInstance().update(sql));
				if (saveParm.getErrCode()<0) {
					return false;
				}
			}
			return true;
		}		
		return true;
	}

	
	/**
	 * ����-�Һ�����-REG_PATADM
	 * @return
	 */
	private static boolean saveRegPatadm(List<SpcRegPatadmDto> list){
		if (null != list && list.size() > 0) {
			for(SpcRegPatadmDto obj : list){
				String sql = SpcServiceSQL.deleteRegPatadm(obj);
//				System.out.println("�Һ�����-----deleteRegPatadm:"+sql);
				//��ɾ
				TParm delParm = new TParm(TJDODBTool.getInstance().update(sql));
				if(delParm.getErrCode()<0){
					return false;
				}
				sql = SpcServiceSQL.saveRegPatadm(obj);
//				System.out.println("�Һ�����-----saveRegPatadm:"+sql);
				//�󱣴�
				TParm saveParm = new TParm(TJDODBTool.getInstance().update(sql));
				if (saveParm.getErrCode()<0) {
					return false;
				}
			}
			return true;
		}		
		return true;
	}


	/**
	 * ת��ҩƷ����
	 * @param obj
	 * @return
	 */
	private static SpcOpdOrderDto getSpcOrderCodeByHisOrderCode(SpcOpdOrderDto obj){
		SpcOpdOrderDto vo = obj;
//		System.out.println(vo.getOrderCode()+"----116---orderCode:"+obj.getOrderCode());
//		System.out.println("---------115--sql:"+SpcServiceSQL.getSpcOrderCodeByHisOrderCode("", "", obj.getOrderCode()));
		TParm parm = new TParm(TJDODBTool.getInstance().select(SpcServiceSQL.getSpcOrderCodeByHisOrderCode("", "", obj.getOrderCode())));
		if(null != parm && parm.getCount() > 0){
			vo.setOrderCode(parm.getValue("ORDER_CODE", 0));
		}
		return vo;
	}


	/**
	 * webServices�����-����HIS���͹�����ҽ��ҽԺ
	 * @param list
	 * @return
	 */
	public String onSavePhaOrderFromHis(SpcOpdOrderDtos dtos) {
		String result = RESULT_FLAG_TRUE;
		boolean flg = saveOpdOrder(dtos.getSpcOpdOrderDtos());
		//���没��������Ϣ
		 flg = saveSysPationInfo(dtos.getSpcSysPatinfoDtos());
		 //����-�ż�����ϵ�
		 flg = saveOpdDiagrec(dtos.getSpcOpdDiagrecDtos());
		 //����-����������¼
		 flg = saveOpdDrugallergy(dtos.getSpcOpdDrugAllergyDtos());
		 //����-�Һ�����
		 flg = saveRegPatadm(dtos.getSpcRegPatadmDtos());
		return result;
	}	
}
