package jdo.spc.inf;

import java.util.List;

import org.apache.poi.hssf.record.PageBreakRecord.Break;
import org.omg.CORBA.OBJ_ADAPTER;

import jdo.spc.SPCSQL;
import jdo.pha.inf.client.SpcOpdDiagrecDto;
import jdo.pha.inf.client.SpcOpdDrugAllergyDto;
import jdo.pha.inf.client.SpcOpdOrderDto;
import jdo.pha.inf.client.SpcOpdOrderDtos;
import jdo.pha.inf.client.SpcRegPatadmDto;
import jdo.pha.inf.client.SpcSysPatinfoDto;
import jdo.spc.inf.dto.SpcOpdOrderReturnDto;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.javahis.ui.spc.util.StringUtils;


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
public class SpcDaoImpl extends TJDOTool {
	/**
	 * ʵ��
	 */
	public static SpcDaoImpl instanceObject;

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
	public static SpcDaoImpl getInstance() {
		if (instanceObject == null)
			instanceObject = new SpcDaoImpl();
		return instanceObject;
	}

	/**
	 * ������
	 */
	public SpcDaoImpl() {
		onInit();
	}

	/**
	 * �ż���ҩ����ˣ�HISִ���ż���ҩ����˺󡢽�������Ϣ��������������
	 * 
	 * @param list
	 * @return
	 */
	public String onSavePhaOrder(SpcOpdOrderDtos dtos) {
		String result = RESULT_FLAG_TRUE;
		if (null != dtos) {
			// �õ�OpdOrder�ż���List
		//�����ż���ҽ����ϸ��
		List<SpcOpdOrderDto> opdOrderDto = dtos.getSpcOpdOrderDtos();
		String isBillFlg = "0";
		if(null != opdOrderDto && opdOrderDto.size()>0){//���Ϊ�շ� ������
			for(SpcOpdOrderDto obj : opdOrderDto){
				String billFlg = obj.getBillFlg();
				String releaseFlg = obj.getReleaseFlg();
//				System.out.println("------billFlg:"+billFlg+",releaseFlg:"+releaseFlg);
				if("N".equals(billFlg) && "N".equals(releaseFlg)){//�����δ�շѣ��Ҳ����Ա�ҩ
					isBillFlg = "-1";
					break;
				}
			}
/*			String billFlg = opdOrderDto.get(0).getBillFlg();
//			System.out.println("------billFlg:"+billFlg);
			if(StringUtils.equals("N", billFlg)){
				return RESULT_FLAG_FALSE;
			}*/
		}
//		System.out.println("-----------isBillFlg: "+isBillFlg);
		if("-1".equals(isBillFlg)){
			return RESULT_FLAG_FALSE;
		}
		boolean flg = saveOpdOrder(dtos.getSpcOpdOrderDtos());
		//���没��������Ϣ
		 flg = saveSysPationInfo(dtos.getSpcSysPatinfoDtos());
		 //����-�ż�����ϵ�
		 flg = saveOpdDiagrec(dtos.getSpcOpdDiagrecDtos());
		 //����-����������¼
		 flg = saveOpdDrugallergy(dtos.getSpcOpdDrugAllergyDtos());
		 //����-�Һ�����
		 flg = saveRegPatadm(dtos.getSpcRegPatadmDtos());
		} else {
			result = RESULT_FLAG_FALSE;
		}
		return result;
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
//			System.out.println("SpcDaoImpl-125-------------sql:" + SPCSQL.deleteOPDOrder(spcOpdOrderDtosList.get(0)));
			parm =  new TParm(TJDODBTool.getInstance().update(SPCSQL.deleteOPDOrder(spcOpdOrderDtosList.get(0))));
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
//				System.out.println("SpcDaoImpl-92-------------sql:" + SPCSQL.saveOpdOrderFromHisBySpc(obj));
				parm = new TParm(TJDODBTool.getInstance().update(SPCSQL.saveOpdOrderFromHisBySpc(obj)));
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
				String sql = SPCSQL.deleteSysPationInfo(obj);
//				System.out.println("����������Ϣ-----deleteSysPationInfo.sql:"+sql);
				//��ɾ
				TParm delParm = new TParm(TJDODBTool.getInstance().update(sql));
				if(delParm.getErrCode()<0){
					return false;
				}
				sql = SPCSQL.saveSysPationInfo(obj);
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
				String sql = SPCSQL.deleteOpdDrugallergyById(obj);
//				System.out.println("����������¼-----deleteOpdDrugallergyById:"+sql);
				//��ɾ
				TParm delParm = new TParm(TJDODBTool.getInstance().update(sql));
				if(delParm.getErrCode()<0){
					return false;
				}
				sql = SPCSQL.saveOpdDrugallergy(obj);
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
				String sql = SPCSQL.deleteOpdDiagrec(obj);
//				System.out.println("�ż�����ϵ�------deleteOpdDiagrec:"+sql);
				//��ɾ
				TParm delParm = new TParm(TJDODBTool.getInstance().update(sql));
				if(delParm.getErrCode()<0){
					return false;
				}
				sql = SPCSQL.saveOpdDiagrec(obj);
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
				String sql = SPCSQL.deleteRegPatadm(obj);
//				System.out.println("�Һ�����-----deleteRegPatadm:"+sql);
				//��ɾ
				TParm delParm = new TParm(TJDODBTool.getInstance().update(sql));
				if(delParm.getErrCode()<0){
					return false;
				}
				sql = SPCSQL.saveRegPatadm(obj);
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
//		System.out.println("---------115--sql:"+SPCSQL.getSpcOrderCodeByHisOrderCode("", "", obj.getOrderCode()));
		TParm parm = new TParm(TJDODBTool.getInstance().select(SPCSQL.getSpcOrderCodeByHisOrderCode("", "", obj.getOrderCode())));
		if(null != parm && parm.getCount() > 0){
			vo.setOrderCode(parm.getValue("ORDER_CODE", 0));
		}
		return vo;
	}
/*	*//**
	 * �ż���ҩ����ҩ(��HIS�ż��﷢ҩ������д���ʱ����Ӧҩ������˸��ʾ)
	 * 
	 * @param SpcCommonDto
	 *            ���ݴ������
	 * @return
	 *//*
	public String phaSend(SpcCommonDtos dtos) {
		// 1 �ɹ���0ʧ��
		String result = RESULT_FLAG_TRUE;
		if (null != dtos) {
			// �õ�OpdOrder�ż���List
			List<SpcCommonDto> spcCommonDtoList = dtos.getSpcCommonDtos();
			if (null != spcCommonDtoList && spcCommonDtoList.size() > 0) {
				TParm parm = new TParm();
				// ���ӱ�ǩ��½
				// if (Constant.parameters == null || Constant.parameters.get(0)
				// != null || Constant.parameters.get(1) != null
				// || Constant.parameters.get(2) != null) {// ���Token��Ϊ�տ�
				// ���µ�½
				// ElectronicTagUtil.login();
				// }
				for (SpcCommonDto obj : spcCommonDtoList) {
					// ������
					String rxNo = obj.getRxNo();
					// ������
					String mrNo = obj.getMrNo();
					// ���ӱ�ǩ
					String basketId = "";
					// �����Ա�
					String sexStr = "";
					// ��������
					String ageStr = "";
					// ��������
					String patName = "";

					if (null != rxNo && rxNo.length() > 0) {
						// ��ѯ���ӱ�ǩ��ʾ
//						System.out.println("phaSend-- 111--------------sql: " + PhaSQL.getBasketInfo(rxNo));
						TParm basketParm = new TParm(TJDODBTool.getInstance().select(PhaSQL.getBasketInfo(rxNo)));
						if (null != basketParm) {
							basketId = basketParm.getValue("BASKET_ID", 0);
							ageStr = basketParm.getValue("AGE", 0);
							patName = basketParm.getValue("PAT_NAME", 0);
							sexStr = basketParm.getValue("SEX_TYPE", 0);
							// ���µ��ӱ�ǩ
							// ElectronicTagUtil.getInstance().sendEleTag(basketId,
							// patName + " " + ageStr, mrNo, sexStr, LIGHT_NUM);
						} else {
							result = RESULT_FLAG_FALSE;
						}
					}
				}
			}
		}
		return result;
	}
*/

	/**
	 * HIS��ҩ��ѯҽ��״̬
	 * 
	 * @param mrNo
	 *            ������
	 * @param rxNo
	 *            ������
	 * @param orderCode
	 *            ҩƷ����            
	 * @return
	 */
	public SpcOpdOrderReturnDto getPhaStateReturn(String rxNo,String caseNo, String seqNo) {
		SpcOpdOrderReturnDto result = null;
		// TODO Auto-generated method stub
		String sql =SPCSQL.getPhaStateReturn(rxNo,caseNo,seqNo);
//		System.out.println("---------getPhaStateReturn:"+sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (null != parm && parm.getCount() > 0) {
			result = new SpcOpdOrderReturnDto(parm.getValue("PHA_CHECK_DATE", 0), parm.getValue("PHA_CHECK_CODE", 0), parm.getValue("PHA_DOSAGE_DATE", 0),
					parm.getValue("PHA_DOSAGE_CODE", 0), parm.getValue("PHA_DISPENSE_DATE", 0), parm.getValue("PHA_DISPENSE_CODE", 0)
					, parm.getValue("PHA_RETN_DATE", 0), parm.getValue("PHA_RETN_CODE", 0));
		}
//		System.out.println("--------getPhaStateReturn: "+parm);
		return result;
	}

}
