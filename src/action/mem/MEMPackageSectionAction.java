package action.mem;

import java.math.BigDecimal;
import java.sql.Timestamp;

import jdo.bil.BILPrintTool;
import jdo.bil.BILTool;
import jdo.mem.MEMReceiptTool;
import jdo.mem.MEMTool;
import jdo.opb.OPBReceiptTool;
import jdo.sys.SystemTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;

/**
 * <p>
 * Title: ʱ���ײ�
 * </p>
 * 
 * <p>
 * Description: ʱ���ײ�
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company: bluecore
 * </p>
 * 
 * @author duzhw 2013.12.26
 * @version 1.0
 */
public class MEMPackageSectionAction extends TAction {
	
	/**
	 * ִ�б�������
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onSave(TParm parm) {
//		System.out.println("ִ�е�ACTION��");
		TConnection conn = getConnection();
		TParm result = new TParm();
		// ���DataStore�е�SQL���
		try {
			//���������ݲ���--ҽ����MEM_PACKAGE_SECTION_D
			TParm updateParm = parm.getParm("UPDATEORDERDATA");
//			System.out.println("aaaaaaaaaaaaaaaaaaa");
			for (int i = 0; i < updateParm.getCount("ID"); i++) {
				TParm uparm=updateParm.getRow(i);
				String updateSql=this.updateOrderInfoSql(uparm);
				result = new TParm(TJDODBTool.getInstance().update(updateSql, conn));
				if (result.getErrCode() < 0) {
					conn.rollback();
					conn.close();
					return result;
				}
				//=======pangben 2015-9-2 ����޸ļ���ҽ��ϸ��
				if("Y".equals(updateParm.getValue("SETMAIN_FLG", i))){	
					String sql="UPDATE  MEM_PACKAGE_SECTION_D SET UN_NUM_FLG= '"
						+updateParm.getValue("UN_NUM_FLG", i)+"' WHERE PACKAGE_CODE='"
						+updateParm.getValue("PACKAGE_CODE", i)+"' AND SECTION_CODE='"
						+updateParm.getValue("SECTION_CODE", i)+"' AND ORDERSET_GROUP_NO='"
						+updateParm.getValue("ORDERSET_GROUP_NO", i)+"' AND ORDERSET_CODE='"
						+updateParm.getValue("ORDERSET_CODE", i)+"'";
					result = new TParm(TJDODBTool.getInstance().update(sql, conn));
					if (result.getErrCode() < 0) {
						conn.rollback();
						conn.close();
						return result;
					}
				}
			}

			//���������ݲ���--ҽ���۸��MEM_PACKAGE_SECTION_D_PRICE add by lich  20141022
//			System.out.println("MEM_PACKAGE_SECTION_D_PRICE uparm= "+parm.getParm("UPDATEORDERDATA"));
//			System.out.println("bbbbbbbbbbbbbbbbbbbbbb");
			for (int i = 0; i < updateParm.getCount("ID"); i++) {
				TParm uparm=updateParm.getRow(i);
//				System.out.println("uparm:::::::"+uparm);
//				String selectSql = this.getSelectDPriceSql(uparm);
//				System.out.println("getSelectDPriceSql="+selectSql);
//				result = new TParm(TJDODBTool.getInstance().select(selectSql));
//				System.out.println("selectSqlresult"+result);
				String updateSql = "";
//				if(result.getDouble("COUNT",0) == 0){
//					updateSql = this.getPriceSqlBY(uparm);
//				}else if(result.getDouble("COUNT", 0) > 0){
//					updateSql = this.updateOrderPriceInfoSql(uparm);
//				}
//				System.out.println("ҽ���۸��updateSecSql="+updateSql);
//				String updateSql;
//				System.out.println("uparm="+uparm.getValue("PRICE_TYPE"));
//				System.out.println("uparm.length==="+uparm.getValue("PRICE_TYPE").length());
				if(uparm.getValue("PRICE_TYPE").length()==0){
					updateSql = this.getPriceSqlBY(uparm);
				}else{
					updateSql=this.updateOrderPriceInfoSql(uparm);
				}
//				System.out.println("MEM_PACKAGE_SECTION_D_PRICE����"+updateSql);
//				System.out.println("���������ݲ���--ҽ���۸��sql="+updateSql);
				
				
				
				result = new TParm(TJDODBTool.getInstance().update(updateSql, conn));
				//System.out.println("resultD::"+result);
				if (result.getErrCode() < 0) {
					conn.rollback();
					conn.close();
					return result;
				}
			}
			
			
			//ɾ��ҽ����Ϣ--ҽ����
			TParm delparm = parm.getParm("DELORDERDATA");
//			System.out.println("��ʼɾ��ҽ����");
//			System.out.println("ccccccccccccccccccccccccc");
			for (int i = 0; i < delparm.getCount("SEQ"); i++){
				//����������ɾ��ϸ����ɾ������
				TParm dparm=delparm.getRow(i);
//				System.out.println("dparm1============"+dparm);
				String setmainFlg = dparm.getValue("SETMAIN_FLG");
				if("Y".equals(setmainFlg)){
					String delDetailSql = this.delDetailSql(dparm);
//					System.out.println("delDetailSql1="+delDetailSql);
					result = new TParm(TJDODBTool.getInstance().update(delDetailSql, conn));
				}
				String delSql=this.delSql(dparm);
//				System.out.println("delSql1="+delSql);
				result = new TParm(TJDODBTool.getInstance().update(delSql, conn));
				if (result.getErrCode() < 0) {
					conn.rollback();
					conn.close();
					return result;
				}
			}
			
			//ɾ��ҽ����Ϣ--ҽ���۸�� add by lich 
//			System.out.println("��ʼɾ��ҽ���۸��");
//			System.out.println("ddddddddddddddddddddddd");
			for (int i = 0; i < delparm.getCount("SEQ"); i++){
				//����������ɾ��ϸ����ɾ������
				TParm dparm=delparm.getRow(i);
//				String setmainFlg = dparm.getValue("SETMAIN_FLG");
//				if("Y".equals(setmainFlg)){
//					String delDetailSql = this.delDetailPriceSql(dparm);
////					System.out.println("delDetailSql2="+delDetailSql);
//					result = new TParm(TJDODBTool.getInstance().update(delDetailSql, conn));
//				}
//				System.out.println("dparm2============"+dparm);
				String delDetailSql = this.delDetailPriceSql(dparm);
//				System.out.println("delDetailSql2="+delDetailSql);
				result = new TParm(TJDODBTool.getInstance().update(delDetailSql, conn));
//				String delSql=this.delPriceSql(dparm);
//				System.out.println("delSql2="+delSql);
//				result = new TParm(TJDODBTool.getInstance().update(delSql, conn));
				if (result.getErrCode() < 0) {
					conn.rollback();
					conn.close();
					return result;
				}
			}
			
			
			// ����ҽ����Ϣ
//			System.out.println("����ҽ����Ϣҽ����");
			TParm insertParm = parm.getParm("INSERTORDERDATA");
//			System.out.println("action��insertParm="+insertParm);
//			System.out.println("����������"+insertParm.getCount("SEQ"));
//			System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeee");
			for (int i = 0; i < insertParm.getCount("SEQ"); i++) {
				TParm inparm=insertParm.getRow(i);
				String insertsql=this.getSql(inparm);
//				System.out.println("����ҽ����Ϣҽ����۸�insertsql==="+insertsql);
				result = new TParm(TJDODBTool.getInstance().update(insertsql, conn));
				if (result.getErrCode() < 0) {
					conn.rollback();
					conn.close();
					return result;
				}
			}
			// ����ҽ���۸���Ϣ  add by lich 20141022
//			System.out.println("����ҽ����Ϣҽ����۸�");
//			System.out.println("fffffffffffffffffffffffff");
			for (int i = 0; i < insertParm.getCount("SEQ"); i++) {
				TParm inparm=insertParm.getRow(i);
				String insertsql=this.getPriceSql(inparm);
//				System.out.println("����ҽ����Ϣҽ����۸�insertsql==="+insertsql);
				result = new TParm(TJDODBTool.getInstance().update(insertsql, conn));
				if (result.getErrCode() < 0) {
					conn.rollback();
					conn.close();
					return result;
				}
			}
			
			
			/** ���������һ������
			//ͳ���ײͼ۸����ʱ�̱��Ӧ��ϸ
			//1����ѯͳ���ײ��ܼ�
			TParm operSectionParm = parm.getParm("OPERSECTIONDATA");
			TParm oparm=operSectionParm.getRow(0);
			TParm allParm = new TParm(TJDODBTool.getInstance().select(this.getAllFeeD(oparm)));
			System.out.println("allParm="+allParm);
			//2������ͳ�����ݵ�ʱ�̱�
			result = new TParm(TJDODBTool.getInstance().update(
					this.updateAllPriceSql(oparm, allParm.getValue("ALL_UNIT_PRICE", 0), 
							allParm.getValue("ALL_RETAIL_PRICE", 0)), conn));
			if (result.getErrCode() < 0) {
				conn.rollback();
				conn.close();
				return result;
			} **/
			
			//���������ݲ���--ʱ�̱�
			TParm updateSectionParm = parm.getParm("UPDATESECTIONDATA");
//			System.out.println("����������-ʱ�̱�==="+updateSectionParm);
//			System.out.println("gggggggggggggggggggggggg");
			for (int i = 0; i < updateSectionParm.getCount("SEQ"); i++) {
				TParm usecparm=updateSectionParm.getRow(i);
				
				String updateSecSql=this.updateSectionInfoSql(usecparm);
				result = new TParm(TJDODBTool.getInstance().update(updateSecSql, conn));
				if (result.getErrCode() < 0) {
					conn.rollback();
					conn.close();
					return result;
				}
			}
			
			//���������ݲ���--ʱ�̼۸�� add by lich 20141022
//			System.out.println("����������-ʱ�̼۸��==="+updateSectionParm);
//			System.out.println("hhhhhhhhhhhhhhhhhhhhhhhh");
			for (int i = 0; i < updateSectionParm.getCount("SEQ"); i++) {
				TParm usecparm = updateSectionParm.getRow(i);
//				System.out.println("��ʱ�̼۸��usecparm==="+usecparm);
//				String selectSPriceSql = this.getSelectSPriceSql(usecparm);
//				System.out.println("selectSPriceSql="+selectSPriceSql);
//				result = new TParm(TJDODBTool.getInstance().select(selectSPriceSql));
//				System.out.println("selectSPriceSqlresult = "+ selectSPriceSql);
				String updateSecSql = "";
//				if(result.getDouble("COUNT",0) == 0){
//					updateSecSql = this.getSecPriceSqlBY(usecparm);
//				}else if(result.getDouble("COUNT", 0) > 0){
//					updateSecSql = this.updateSectionPriceInfoSql(usecparm);
//				}
				
//				System.out.println("ʱ�̼۸��updateSecSql="+updateSecSql);
				
				
//				String updateSecSql;
//				System.out.println("uparm="+usecparm.getValue("PRICE_TYPE"));
//				System.out.println("uparm.length==="+usecparm.getValue("PRICE_TYPE").length());
				if(usecparm.getValue("PRICE_TYPE").length()==0){
					updateSecSql = this.getSecPriceSqlBY(usecparm);
				}else{					
					updateSecSql = this.updateSectionPriceInfoSql(usecparm);
				}
//				System.out.println("MEM_PACKAGE_SECTION_PRICE����"+updateSecSql);
//				System.out.println("���������ݲ���--ʱ�̼۸��sql="+updateSecSql);
				result = new TParm(TJDODBTool.getInstance().update(updateSecSql, conn));
				//System.out.println("result::::"+result);
				if (result.getErrCode() < 0) {
					conn.rollback();
					conn.close();
					return result;
				}
			}
			
			
			 
			
			//����ʱ����Ϣ
//			System.out.println("����ʱ�̱�===");
			TParm insertSectionParm = parm.getParm("INSERTSECTIONDATA");
//			System.out.println("action��insertSectionParm="+insertSectionParm);
//			System.out.println("iiiiiiiiiiiiiiiiiiiiiiiii");
			for (int i = 0; i < insertSectionParm.getCount("SEQ"); i++) {
				TParm insecparm=insertSectionParm.getRow(i);
				String insecsertsql=this.getSecSql(insecparm);
//				System.out.println("����ʱ�̱�===insecsertsql"+insecsertsql);
				result = new TParm(TJDODBTool.getInstance().update(insecsertsql, conn));
				if (result.getErrCode() < 0) {
					conn.rollback();
					conn.close();
					return result;
				}
			}
			//����ʱ�̼۸���Ϣ
//			System.out.println("����ʱ�̼۸��===");
//			System.out.println("jjjjjjjjjjjjjjjjjjjjjjjj");
			for (int i = 0; i < insertSectionParm.getCount("SEQ"); i++) {
				TParm insecparm=insertSectionParm.getRow(i);
				String insecsertsql=this.getSecPriceSql(insecparm);
//				System.out.println("����ʱ�̼۸��===insecparm"+insecsertsql);
				result = new TParm(TJDODBTool.getInstance().update(insecsertsql, conn));
				if (result.getErrCode() < 0) {
					conn.rollback();
					conn.close();
					return result;
				}
			}
			//�޸��ײͱ���������Ϣ
//			System.out.println("�޸��ײͱ�����---");
			TParm packageParm = parm.getParm("PACKAGEDATA");
//			System.out.println("�޸��ײͱ�����packageParm==="+packageParm);
			String updataPacksql=this.getPackageSql(packageParm);
//			System.out.println("�޸��ײͱ�����updataPacksql="+updataPacksql);
			result = new TParm(TJDODBTool.getInstance().update(updataPacksql, conn));
			if (result.getErrCode() < 0) {
				conn.rollback();
				conn.close();
				return result;
			}
			
			//�޸��ײͼ۸����������Ϣ
			/**
			 * ע�⣺��Ҫ�����������������Ƚ��в�ѯ���ж��ײͼ۸���ж�Ӧ�����Ƿ���ڣ�
			 * ������ڣ���ִ�и��²���
			 * ��������ڣ���ִ�в������
			 */
//			System.out.println("���ײͼ۸�����---");
			TParm packagePriceParm = parm.getParm("PACKAGEDATA");
			String selectSql = this.getSelectSql(packagePriceParm);// ���ײͼ۸���в�ѯ���ݣ�ͨ���۸����ͺ��ײʹ���
//			System.out.println("��ѯ�ײ��Ƿ����sql="+selectSql);
			result = new TParm(TJDODBTool.getInstance().select(selectSql));
//			System.out.println("result="+result);
			/**
			 * ��������result ����Ϊ0 ˵����ѯ��������ڣ�ִ��������䣬����ִ���޸�
			 */
			if (result.getErrCode() < 0) {
				conn.rollback();
				conn.close();
				return result;
			}
			//��������result ����Ϊ0 ˵����ѯ��������ڣ�ִ���������
			if(result.getDouble("COUNT", 0) == 0){
				String insertPackPricesql = this.getInsertPackagePriceSql(packagePriceParm);
//				System.out.println("�ײͼ۸��������insertPackPricesql="+insertPackPricesql);
				result = new TParm(TJDODBTool.getInstance().update(insertPackPricesql,conn));
//				if (result.getErrCode() < 0) {
//					conn.rollback();
//					conn.close();
//					return result;
//				}
			//��������result ���ݴ���0˵�������ݴ��ڣ���ôִ���޸Ĳ���	
			}else if(result.getDouble("COUNT", 0) > 0){				
				String updataPackPricesql = this.getPackagePriceSql(packagePriceParm);
//				System.out.println("�ײͼ۸���޸Ĳ���updataPackPricesql="+updataPackPricesql);
				result = new TParm(TJDODBTool.getInstance().update(updataPackPricesql,conn));
				if (result.getErrCode() < 0) {
					conn.rollback();
					conn.close();
					return result;
				}
			}
			
			
			
			
			/**
			//����ͳ��ʱ���ܼ۸��ײ�������
			String packageCode = oparm.getValue("PACKAGE_CODE");
			if(!updateSectionPrice(packageCode)){
				conn.rollback();
				conn.close();
				result.setErrCode(-2);
				return result;
			} **/
			
			
//			// ���没��������¼��Ϣ��(MRO_RECORD)updateMRODiag
//			result = MROTool.getInstance().updateMRODiag(diagParm, conn);
//			if (result.getErrCode() < 0) {
//				conn.close();
//				return result;
//			}
//			// ��д������
//			result = ADMTool.getInstance().updateNewDaily(parm, conn);
//			if (result.getErrCode() < 0) {
//				conn.close();
//				return result;
//			}
		} catch (Exception e) {
			e.printStackTrace();
			result.setErr(-1, e.getMessage());
			err(e.getMessage());
		}   
		conn.commit();
		conn.close();
		return result; 
	}
	
//	/**
//	 * ͳ���ײͼ۸����ʱ�̱��Ӧ��ϸ
//	 */
//	public TParm onUpdatePrice(TParm parm){
//		TConnection conn = getConnection();
//		TParm result = new TParm();
//		//ͳ���ײͼ۸����ʱ�̱��Ӧ��ϸ
//		//1����ѯͳ���ײ��ܼ�
//		TParm operSectionParm = parm.getParm("OPERSECTIONDATA");
//		TParm oparm=operSectionParm.getRow(0);
//		TParm allParm = new TParm(TJDODBTool.getInstance().select(this.getAllFeeD(oparm)));
////		System.out.println("allParm="+allParm);
//		//2������ͳ�����ݵ�ʱ�̱�
//		result = new TParm(TJDODBTool.getInstance().update(
//				this.updateAllPriceSql(oparm, allParm.getValue("ALL_UNIT_PRICE", 0), 
//						allParm.getValue("ALL_RETAIL_PRICE", 0)), conn));
//		if (result.getErrCode() < 0) {
//			conn.rollback();
//			conn.close();
//			return result;
//		}
//		
//		//����ͳ��ʱ���ܼ۸��ײ�������
//		String packageCode = oparm.getValue("PACKAGE_CODE");
//		if(!updateSectionPrice(packageCode)){
//			conn.rollback();
//			conn.close();
//			result.setErrCode(-2);
//			return result;
//		}
//		
//		conn.commit();
//		conn.close();
//		return result;
//	}
	
	/**
	 * �ж��Ƿ����ɾ��ʱ�̱�����
	 */
	public TParm onIfDelSection(TParm parm) {
		TParm result = new TParm();
		try {
			result = new TParm(TJDODBTool.getInstance().select(delSectionflgSql(parm)));
			if(result.getErrCode()<0){
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.setErr(-1, e.getMessage());
			err(e.getMessage());
		}
		return result;
	}
	/**
	 * ɾ��ʱ�̱�����
	 * ��ʱ�̼۸��
	 */
	public TParm onDelSection(TParm parm){
		TConnection conn = getConnection();
		TParm result = new TParm();
		TParm result1 = new TParm();//ʱ�̼۸��
		TParm delParm = new TParm();
		boolean flag = false;
		try {
			String packageCode = parm.getValue("PACKAGE_CODE", 0);
			String sectionCode = parm.getValue("SECTION_CODE", 0);
			delParm.addData("PACKAGE_CODE", packageCode);
			delParm.addData("SECTION_CODE", sectionCode);
			//System.out.println("packageCode="+packageCode);
			//ɾ������
			result = new TParm(TJDODBTool.getInstance().update(delSectionSql(delParm), conn));
			result1 = new TParm(TJDODBTool.getInstance().update(delSectionPriceSql(delParm), conn));
			if(result.getErrCode()<0 || result1.getErrCode() < 0){
				result.addData("FLAG", "false");
				conn.rollback();
				conn.close();
				return result;
			}
			flag = updateSectionPrice(packageCode);
			//System.out.println("---onDelSection-flag="+flag);
			if(flag){
				result.addData("FLAG", "true");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		conn.commit();
		conn.close();
		return result;
		
	}
	
	/**
	 * ��ѯD_PRICE
	 * @param parm
	 * @return
	 */
	
	private String getSelectDPriceSql(TParm parm){
//		System.out.println("�����е�parm="+parm);
		String sql = "SELECT COUNT(*) AS COUNT " +
					" FROM MEM_PACKAGE_SECTION_D_PRICE " +
					" WHERE PRICE_TYPE = '"+parm.getValue("PRICE_TYPE")+"' " +
					" AND SECTION_CODE = '"+parm.getValue("SECTION_CODE")+"'" +
					" AND ID = '"+ parm.getValue("ID")+"'"+
					" AND PACKAGE_CODE = '"+parm.getValue("PACKAGE_CODE")+"'";
//		System.out.println("�����е�SQL"+sql);
		return sql;
	}
	/**
	 * ��ѯSection_PRICE
	 * @param parm
	 * @return
	 */
	private String getSelectSPriceSql(TParm parm){
//		System.out.println("�����е�parm:::::::::::"+parm);
		String sql = "SELECT COUNT(*) AS COUNT " +
				" FROM MEM_PACKAGE_SECTION_PRICE " +
				" WHERE PRICE_TYPE = '"+parm.getValue("PRICE_TYPE")+"' " +
				" AND SECTION_CODE = '"+parm.getValue("SECTION_CODE")+"'" +
				" AND PACKAGE_CODE = '"+parm.getValue("PACKAGE_CODE")+"'";
//		System.out.println("�����е� sql======"+sql);
		return sql;
	}
	
	
	
	
	/**
	 * ����ʱ���ײ��ܺͲ������ײ���������
	 */
	public boolean updateSectionPrice(String packageCode){
		boolean flag = true;
		TConnection conn = getConnection();
		TParm result = new TParm();
		TParm result1 = new TParm();
		String selectSql = "SELECT SUM(ORIGINAL_PRICE) AS ALL_ORIGINAL_PRICE,SUM(SECTION_PRICE) AS ALL_SECTION_PRICE " +
				" FROM MEM_PACKAGE_SECTION WHERE PACKAGE_CODE = '"+packageCode+"'";
		//System.out.println("selectSql="+selectSql);
		result = new TParm(TJDODBTool.getInstance().select(selectSql));
		if(result.getCount()>0){
			//���������ײ��ܼ۸�
			String allOriginalPrice = result.getValue("ALL_ORIGINAL_PRICE", 0);
			String allSectionPrice = result.getValue("ALL_SECTION_PRICE", 0);
			String updateSql = "UPDATE MEM_PACKAGE SET ORIGINAL_PRICE = '"+allOriginalPrice+"'," +
					" PACKAGE_PRICE = '"+allSectionPrice+"' WHERE PACKAGE_CODE = '"+packageCode+"' ";
			
			String updateSql1 = "UPDATE MEM_PACKAGE_PRICE SET " +//����MEM_PACKAGE_PRICE
			" PACKAGE_PRICE = '"+allSectionPrice+"' WHERE PACKAGE_CODE = '"+packageCode+"' ";
			
			//System.out.println("updateSql="+updateSql);
			
			result = new TParm(TJDODBTool.getInstance().update(updateSql, conn));
			result1 = new TParm(TJDODBTool.getInstance().update(updateSql1, conn));
			
			if (result.getErrCode() < 0 || result1.getErrCode() < 0) {
				conn.rollback();
				conn.close();
				return flag = false;
			}
			
		}
		try {
			
		} catch (Exception e) {
			e.printStackTrace();
			result.setErr(-1, e.getMessage());
			err(e.getMessage());
		}
		conn.commit();
		conn.close();
		return flag;
	}
	
	/**
	 * �ж��Ƿ��Ǽ���ҽ��
	 */
	public TParm checkIsDetailOrder(TParm parm){
		TParm result = new TParm();
		TParm selectParm = new TParm();
		try {
			String orderCode = parm.getValue("ORDER_CODE");
			selectParm.addData("ORDER_CODE", orderCode);
			result = new TParm(TJDODBTool.getInstance().select(checkIsDetailOrderSql(selectParm)));
			if(result.getErrCode()<0){
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	/**
	 * �����ֵ���м���ҽ��-ϸ���ܼ۸�
	 */
	public TParm onSumDetailPrice(TParm parm){
		TParm result = new TParm();
		TParm result2 = new TParm();
		TParm selectParm = new TParm();
		double price = 0.00;
		double sumPrice = 0.00;
		try {
			String orderCode = parm.getValue("ORDER_CODE");
			selectParm.addData("ORDER_CODE", orderCode);
			result = new TParm(TJDODBTool.getInstance().select(sumPriceDeatailOrderSql1(selectParm)));
			if(result.getErrCode()<0){
				return result;
			}
			if(result.getCount("OWN_AMT")>0){
				for (int i = 0; i < result.getCount("OWN_AMT"); i++) {
					price = result.getDouble("OWN_AMT", i);
					sumPrice += price;
				}
			}
			result2.addData("OWN_AMT", sumPrice);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result2;
	}
	
	/**
	 * ����ֵ���м���ҽ��ϸ������
	 */
	public TParm getDetailOrderData(TParm parm){
		TParm result = new TParm();
		
		try {
			result = new TParm(TJDODBTool.getInstance().select(getDetailOrderDataSql(parm)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	
	}
	
	
	
	
	
	/**
	 * ҽ��������� 
	 * sql
	 * MEM_PACKAGE_SECTION_D
	 * @return
	 */
	private String getSql(TParm parm) {
		String sql = "INSERT INTO MEM_PACKAGE_SECTION_D(ID,SEQ,SECTION_DESC,ORDER_CODE,ORDER_DESC," +
		"ORDER_NUM, UNIT_CODE,UNIT_PRICE,RETAIL_PRICE,DESCRIPTION,OPT_DATE,OPT_USER,OPT_TERM," +
		" SECTION_CODE,PACKAGE_CODE,SETMAIN_FLG,ORDERSET_CODE,ORDERSET_GROUP_NO,HIDE_FLG,MEDI_UNIT,ROUTE_CODE,FREQ_CODE,UN_NUM_FLG) " +
		" VALUES('"+parm.getValue("ID")+"'," +
				"'"+parm.getValue("SEQ")+"'," +
				"'"+parm.getValue("SECTION_DESC")+"'," +
				"'"+parm.getValue("ORDER_CODE")+"'," +
				"'"+parm.getValue("ORDER_DESC")+"'," +
				"'"+parm.getValue("ORDER_NUM")+"'," +
				"'"+parm.getValue("UNIT_CODE")+"'," +
				"'"+parm.getValue("UNIT_PRICE")+"'," +
				"'"+parm.getValue("RETAIL_PRICE")+"'," +
				"'"+parm.getValue("DESCRIPTION")+"'," +
				//"to_date('"+parm.getValue("OPT_DATE")+"','YYYY-mm-DD')," +
				"sysdate," +
				"'"+parm.getValue("OPT_USER")+"'," +
				"'"+parm.getValue("OPT_TERM")+"'," +
				"'"+parm.getValue("SECTION_CODE")+"'," +
				"'"+parm.getValue("PACKAGE_CODE")+"'," +
				"'"+parm.getValue("SETMAIN_FLG")+"'," +
				"'"+parm.getValue("ORDERSET_CODE")+"'," +
				"'"+parm.getValue("ORDERSET_GROUP_NO")+"'," +
				"'"+parm.getValue("HIDE_FLG")+"'," +
				"'"+parm.getValue("MEDI_UNIT")+"'," +
				"'"+parm.getValue("ROUTE_CODE")+"'," +
				"'"+parm.getValue("FREQ_CODE")+"','"+parm.getValue("UN_NUM_FLG")+"')";//====pangben 2015-9-2 ��Ӳ�����
//		System.out.println("��������MEM_PACKAGE_SECTION_D sql="+sql);
		return sql;
	}
	/**
	 * add by lich  20141022
	 * ҽ���۸�������� 
	 * sql
	 * MEM_PACKAGE_SECTION_D_PRICE
	 * @return
	 */
	private String getPriceSql(TParm parm) {
		String sql = "INSERT INTO MEM_PACKAGE_SECTION_D_PRICE(ID,PRICE_TYPE,SECTION_CODE,PACKAGE_CODE,RETAIL_PRICE," +
		"DISCOUNT_RATE, OPT_DATE,OPT_USER,OPT_TERM,ORIGINAL_PRICE) " +
		" VALUES('"+parm.getValue("ID")+"'," +
				"'"+parm.getValue("PRICE_TYPE")+"'," +
				"'"+parm.getValue("SECTION_CODE")+"'," +
				"'"+parm.getValue("PACKAGE_CODE")+"'," +
				"'"+parm.getValue("RETAIL_PRICE")+"'," +
				"'"+parm.getValue("DISCOUNT_RATE")+"'," +
				//"to_date('"+parm.getValue("OPT_DATE")+"','YYYY-mm-DD')," +
				"sysdate," +
				"'"+parm.getValue("OPT_USER")+"'," +
				"'"+parm.getValue("OPT_TERM")+"'," +
				"'"+StringTool.round(parm.getDouble("UNIT_PRICE")*parm.getDouble("ORDER_NUM"),2)+"')";
//		System.out.println("����MEM_PACKAGE_SECTION_D_PRICE����sql="+sql);
//		System.out.println("parm==="+parm);
		return sql;
	}
	
	/**
	 * add by lich  20141030
	 * ҽ���۸�������� (�������)
	 * ��PRICE_TYPE�ֶ�Ϊ��ʱ����в��������
	 * ��ʱPRICE_TPYE�ֶε�ֵ��PRICE_TYPE_BY�����ȡ
	 * sql
	 * MEM_PACKAGE_SECTION_D_PRICE
	 * @return
	 */
	private String getPriceSqlBY(TParm parm) {
		String sql = "INSERT INTO MEM_PACKAGE_SECTION_D_PRICE(ID,PRICE_TYPE,SECTION_CODE,PACKAGE_CODE,RETAIL_PRICE," +
		"DISCOUNT_RATE, OPT_DATE,OPT_USER,OPT_TERM,ORIGINAL_PRICE) " +
		" VALUES('"+parm.getValue("ID")+"'," +
				"'"+parm.getValue("PRICE_TYPE_BY")+"'," +
				"'"+parm.getValue("SECTION_CODE")+"'," +
				"'"+parm.getValue("PACKAGE_CODE")+"'," +
				"'"+parm.getValue("RETAIL_PRICE")+"'," +
				"'"+parm.getValue("DISCOUNT_RATE")+"'," +
				//"to_date('"+parm.getValue("OPT_DATE")+"','YYYY-mm-DD')," +
				"sysdate," +
				"'"+parm.getValue("OPT_USER")+"'," +
				"'"+parm.getValue("OPT_TERM")+"'," +
				"'"+StringTool.round(parm.getDouble("UNIT_PRICE")*parm.getDouble("ORDER_NUM"),2)+"')";
//		System.out.println("����MEM_PACKAGE_SECTION_D_PRICE����sql="+sql);
//		System.out.println("parm==="+parm);
		return sql;
	}
	
	
	
	
	/**
	 * ʱ�̲������
	 * sql
	 * @return
	 */
	private String getSecSql(TParm parm) {
		String sql = "INSERT INTO MEM_PACKAGE_SECTION(SECTION_CODE,PACKAGE_CODE,SECTION_DESC,SECTION_ENG_DESC,PY1," +
		"PY2, SEQ,DESCRIPTION,ORIGINAL_PRICE,SECTION_PRICE,OPT_DATE,OPT_USER,OPT_TERM," +
		" START_DATE,END_DATE) VALUES('"+parm.getValue("SECTION_CODE")+"'," +
				"'"+parm.getValue("PACKAGE_CODE")+"'," +
				"'"+parm.getValue("SECTION_DESC")+"'," +
				"'"+parm.getValue("SECTION_ENG_DESC")+"'," +
				"'"+parm.getValue("PY1")+"'," +
				"''," +
				"'"+parm.getValue("SEQ")+"'," +
				"'"+parm.getValue("DESCRIPTION")+"'," +
				"'"+parm.getValue("ORIGINAL_PRICE")+"'," +
				"'"+parm.getValue("SECTION_PRICE")+"'," +
				//"to_date('"+parm.getValue("OPT_DATE")+"','YYYY-mm-DD')," +
				"sysdate," +
				"'"+parm.getValue("OPT_USER")+"'," +
				"'"+parm.getValue("OPT_TERM")+"'," +
//				"to_date('"+parm.getValue("START_DATE")+"','YYYY-mm-DD')," +
//				"to_date('"+parm.getValue("END_DATE")+"','YYYY-mm-DD') )";
				"'"+parm.getValue("START_DATE")+"'," +
				"'"+parm.getValue("END_DATE")+"')";
//		System.out.println("MEM_PACKAGE_SECTION sql="+sql);
		return sql;
	}
	
	/**
	 * ʱ�̼۸�������
	 * sql
	 * MEM_PACKAGE_SECTION_PRICE
	 * add by lich 20141022
	 * @return
	 */
	private String getSecPriceSql(TParm parm) {
		String sql = "INSERT INTO MEM_PACKAGE_SECTION_PRICE (SECTION_CODE,PACKAGE_CODE,PRICE_TYPE,SECTION_PRICE,DISCOUNT_RATE," +
				"OPT_DATE,OPT_USER,OPT_TERM,ORIGINAL_PRICE) " +
				" VALUES('"+parm.getValue("SECTION_CODE")+"'," +
				"'"+parm.getValue("PACKAGE_CODE")+"'," +
				"'"+parm.getValue("PRICE_TYPE")+"'," +
				"'"+parm.getValue("SECTION_PRICE")+"'," +
				"'"+parm.getValue("DISCOUNT_RATE")+"'," +
				//"to_date('"+parm.getValue("OPT_DATE")+"','YYYY-mm-DD')," +
				"sysdate," +
				"'"+parm.getValue("OPT_USER")+"'," +	
				"'"+parm.getValue("OPT_TERM")+"'," +
				"'"+parm.getValue("ORIGINAL_PRICE")+"')";
//		System.out.println("����MEM_PACKAGE_SECTION_PRICE����2sql="+sql);
		return sql;
	}
	
	/**
	 * ʱ�̼۸�������(�������)
	 * ��PRICE_TYPE�ֶ�Ϊ��ʱ����в��������
	 * ��ʱPRICE_TPYE��ֵ��PRICE_TYPE_BY�����ȡ
	 * sql
	 * MEM_PACKAGE_SECTION_PRICE
	 * add by lich 20141030
	 * @return
	 */
	private String getSecPriceSqlBY(TParm parm) {
		String sql = "INSERT INTO MEM_PACKAGE_SECTION_PRICE (SECTION_CODE,PACKAGE_CODE,PRICE_TYPE,SECTION_PRICE,DISCOUNT_RATE," +
				"OPT_DATE,OPT_USER,OPT_TERM,ORIGINAL_PRICE) " +
				" VALUES('"+parm.getValue("SECTION_CODE")+"'," +
				"'"+parm.getValue("PACKAGE_CODE")+"'," +
				"'"+parm.getValue("PRICE_TYPE_BY")+"'," +
				"'"+parm.getValue("SECTION_PRICE")+"'," +
				"'"+parm.getValue("DISCOUNT_RATE")+"'," +
				//"to_date('"+parm.getValue("OPT_DATE")+"','YYYY-mm-DD')," +
				"sysdate," +
				"'"+parm.getValue("OPT_USER")+"'," +	
				"'"+parm.getValue("OPT_TERM")+"'," +
				"'"+parm.getValue("ORIGINAL_PRICE")+"')";
//		System.out.println("����MEM_PACKAGE_SECTION_PRICE����2sql="+sql);
		return sql;
	}
	
	
	
	
	/**
	 * ɾ��ҽ����ϸ��
	 * MEM_PACKAGE_SECTION_D
	 * @return
	 */
	private String delSql(TParm parm){
		String sectionCode = parm.getValue("SECTION_CODE");
		String packageCode = parm.getValue("PACKAGE_CODE");
		String seq = parm.getValue("SEQ");
		String orderCode = parm.getValue("ORDER_CODE");
		String id = parm.getValue("ID");
		String sql = " DELETE FROM MEM_PACKAGE_SECTION_D WHERE SECTION_CODE = '"+sectionCode+"' " +
		" AND PACKAGE_CODE = '"+packageCode+"' AND SEQ='"+seq+"' AND ORDER_CODE = '"+orderCode+"' AND ID = '"+id+"'";
//		System.out.println("ɾ��ҽ����ϸ��sql="+sql);
		return sql;	
	}
	
	/**
	 * ɾ��ҽ����ϸ��
	 * MEM_PACKAGE_SECTION_D_PRICE
	 * @return
	 */
	private String delPriceSql(TParm parm){
		String sectionCode = parm.getValue("SECTION_CODE");
		String packageCode = parm.getValue("PACKAGE_CODE");
//		String seq = parm.getValue("SEQ");
//		String priceType = parm.getValue("PRICE_TYPE");
		
//		String sql = " DELETE FROM MEM_PACKAGE_SECTION_D_PRICE WHERE SECTION_CODE = '"+sectionCode+"' " +
//		" AND PACKAGE_CODE = '"+packageCode+"' AND PRICE_TYPE = '"+priceType+"'";
		String sql = " DELETE FROM MEM_PACKAGE_SECTION_D_PRICE WHERE SECTION_CODE = '"+sectionCode+"' " +
		" AND PACKAGE_CODE = '"+packageCode+"' ";
		//System.out.println("ɾ��ҽ����ϸ��sql="+sql);
		return sql;	
	}
	
	
	
	
	/**
	 * ɾ��ϸ������-��ɾ��ϸ����ɾ������
	 *  MEM_PACKAGE_SECTION_D
	 */
	public String delDetailSql(TParm parm){
		String sectionCode = parm.getValue("SECTION_CODE");
		String packageCode = parm.getValue("PACKAGE_CODE");
		String orderCode = parm.getValue("ORDER_CODE");
		
		String sql = " DELETE FROM MEM_PACKAGE_SECTION_D WHERE SECTION_CODE = '"+sectionCode+"' " +
		" AND PACKAGE_CODE = '"+packageCode+"' AND ORDERSET_CODE ='"+orderCode+"' AND HIDE_FLG = 'Y' ";
//		System.out.println("ɾ��ϸ��D������sql="+sql);
		return sql;	
	}
	
	/**
	 * ɾ��ϸ������-��ɾ��ϸ����ɾ������
	 *  MEM_PACKAGE_SECTION_D_PRICE
	 *  add by lich 20141022
	 */
	public String delDetailPriceSql(TParm parm){
//		System.out.println("ɾ���۸��ϸ������parm="+parm);
		String sectionCode = parm.getValue("SECTION_CODE");
		String packageCode = parm.getValue("PACKAGE_CODE");
//		String priceType = parm.getValue("PRICE_TYPE");
		String id = parm.getValue("ID");
		String sql = " DELETE FROM MEM_PACKAGE_SECTION_D_PRICE WHERE SECTION_CODE = '"+sectionCode+"' " +
				" AND PACKAGE_CODE = '"+packageCode+"'" +
//				" AND PRICE_TYPE ='"+priceType+"'" +
				" AND ID = '"+id+"'";
//		System.out.println("ɾ���۸��ϸ������sql="+sql);
		return sql;	
	}
	
	
	
	/**
	 * ʱ�̱�ɾ���ж�
	 * MEM_PACKAGE_SECTION_D
	 */
	private String delSectionflgSql(TParm parm){
		String sectionCode = parm.getValue("SECTION_CODE", 0);
		String packageCode = parm.getValue("PACKAGE_CODE", 0);
		
		String sql = " SELECT ID FROM MEM_PACKAGE_SECTION_D WHERE PACKAGE_CODE='"+packageCode+"' " +
		" AND SECTION_CODE = '"+sectionCode+"' ";
		//System.out.println("ʱ�̱�ɾ���ж�sql="+sql);
		return sql;	
	}
	/**
	 * ͳ��ҽ��ϸ���ײ��ܼ� SYS_PACKAGE_SECTION_D
	 * SUM(UNIT_PRICE), SUM(RETAIL_PRICE)
	 */
	private String getAllFeeD(TParm parm){
		String sectionCode = parm.getValue("SECTION_CODE");
		String packageCode = parm.getValue("PACKAGE_CODE");
		String sql = "SELECT SUM(UNIT_PRICE*ORDER_NUM) AS ALL_UNIT_PRICE, SUM(RETAIL_PRICE*ORDER_NUM) AS ALL_RETAIL_PRICE " +
				" FROM MEM_PACKAGE_SECTION_D WHERE SECTION_CODE = '"+sectionCode+"' AND PACKAGE_CODE = '"+packageCode+"' " +
				" AND HIDE_FLG = 'N' ";
		//System.out.println("ͳ��ҽ��ϸ���ײ��ܼ�sql="+sql);
		return sql;
	}
	/**
	 * ����ͳ�����ݵ�ʱ�̱� SYS_PACKAGE_SECTION
	 */
	private String updateAllPriceSql(TParm parm, String originalPrice, String sectionPrice){
		String sectionCode = parm.getValue("SECTION_CODE");
		String packageCode = parm.getValue("PACKAGE_CODE");
		String sql = "UPDATE MEM_PACKAGE_SECTION SET ORIGINAL_PRICE = '"+originalPrice+"',SECTION_PRICE = '"+sectionPrice+"' " +
				" WHERE SECTION_CODE ='"+sectionCode+"' AND PACKAGE_CODE = '"+packageCode+"'";
		//System.out.println("����ͳ�����ݵ�ʱ�̱�sql="+sql);
		return sql;
		
	}
	/**
	 * �޸�ҽ���������ݲ���MEM_PACKAGE_SECTION_D
	 */
	private String updateOrderInfoSql(TParm parm){
		String sql = "UPDATE  MEM_PACKAGE_SECTION_D SET " +
				" SECTION_DESC='"+parm.getValue("SECTION_DESC")+"'," +
				" ORDER_CODE='"+parm.getValue("ORDER_CODE")+"'," +
				" TRADE_ENG_DESC='"+parm.getValue("TRADE_ENG_DESC")+"'," +
				" ORDER_DESC='"+parm.getValue("ORDER_DESC")+"'," +
				" ORDER_NUM='"+parm.getValue("ORDER_NUM")+"'," +
				" UNIT_CODE='"+parm.getValue("UNIT_CODE")+"'," +
				" UNIT_PRICE='"+parm.getValue("UNIT_PRICE")+"'," +
				" RETAIL_PRICE='"+parm.getValue("RETAIL_PRICE")+"'," +
				" DESCRIPTION='"+parm.getValue("DESCRIPTION")+"'," +
				" OPT_DATE=sysdate," +
				" OPT_USER='"+parm.getValue("OPT_USER")+"'," +
				" OPT_TERM='"+parm.getValue("OPT_TERM")+"'," +
				" MEDI_UNIT='"+parm.getValue("MEDI_UNIT")+ "'," +
				" ROUTE_CODE='"+parm.getValue("ROUTE_CODE")+ "'," +
				" FREQ_CODE='"+parm.getValue("FREQ_CODE")+ "'," +
				" UN_NUM_FLG='"+parm.getValue("UN_NUM_FLG")+ "'" +//====pangben 2015-9-2 ������
				" WHERE ID='"+parm.getValue("ID")+"' " +
				" AND SECTION_CODE='"+parm.getValue("SECTION_CODE")+ "' " +
				" AND PACKAGE_CODE='"+parm.getValue("PACKAGE_CODE")+ "' " ;
//		System.out.println("�޸�MEM_PACKAGE_SECTION_D����sql="+sql);
		return sql;
	}
	
	/**
	 * �޸�ҽ���۸�������ݲ���MEM_PACKAGE_SECTION_D_PRICE add by lich  20141022
	 */
	private String updateOrderPriceInfoSql(TParm parm){
		String sql = "UPDATE  MEM_PACKAGE_SECTION_D_PRICE SET " +
//				" ID='"+parm.getValue("ID")+"'," +
//				" SECTION_CODE='"+parm.getValue("SECTION_CODE")+"'," +
//				" PRICE_TYPE='"+parm.getValue("PRICE_TYPE")+"'," +
//				" PACKAGE_CODE='"+parm.getValue("PACKAGE_CODE")+"'," +
				" RETAIL_PRICE='"+parm.getValue("RETAIL_PRICE")+"'," +
				" DISCOUNT_RATE='"+parm.getValue("DISCOUNT_RATE")+"'," +
				" OPT_USER='"+parm.getValue("OPT_USER")+"'," +
				" OPT_DATE=sysdate," +
				" OPT_TERM='"+parm.getValue("OPT_TERM")+"' " +
				" WHERE ID='"+parm.getValue("ID")+"' " +
				" AND SECTION_CODE='"+parm.getValue("SECTION_CODE")+ "' " +
				" AND PRICE_TYPE='"+parm.getValue("PRICE_TYPE")+ "' " +
				" AND PACKAGE_CODE='"+parm.getValue("PACKAGE_CODE")+ "' " ;
//		System.out.println("�޸�MEM_PACKAGE_SECTION_D_PRICE����sql="+sql);
//		System.out.println("RETAIL_PRICE ="+parm.getValue("RETAIL_PRICE"));
		
		return sql;
	}
	
	
	/**
	 * �޸�ҽ��ʱ�̱������ݲ���MEM_PACKAGE_SECTION
	 */
	private String updateSectionInfoSql(TParm parm){
		String sql = "UPDATE  MEM_PACKAGE_SECTION SET " +
				" SECTION_DESC='"+parm.getValue("SECTION_DESC")+"'," +
				" SECTION_ENG_DESC='"+parm.getValue("SECTION_ENG_DESC")+"'," +
				" PY1='"+parm.getValue("PY1")+"'," +
				" DESCRIPTION='"+parm.getValue("DESCRIPTION")+"'," +
				" ORIGINAL_PRICE='"+parm.getValue("ORIGINAL_PRICE")+"'," +
				" SECTION_PRICE='"+parm.getValue("SECTION_PRICE")+"'," +
				" OPT_DATE=sysdate," +
				" OPT_USER='"+parm.getValue("OPT_USER")+"'," +
				" OPT_TERM='"+parm.getValue("OPT_TERM")+"', " +
				" START_DATE='"+parm.getValue("START_DATE")+"', " +
				" END_DATE='"+parm.getValue("END_DATE")+"' " +
				" WHERE SECTION_CODE='"+parm.getValue("SECTION_CODE")+"' " +
				" AND PACKAGE_CODE = '"+parm.getValue("PACKAGE_CODE")+"' "
				;
//		System.out.println("�޸�ҽ��ʱ�̱������ݲ���MEM_PACKAGE_SECTION sql="+sql);
		
		return sql;
	}
	/**
	 * �޸�ʱ�̼۸�������ݲ���MEM_PACKAGE_SECTION_PRICE
	 */
	private String updateSectionPriceInfoSql(TParm parm){
		String sql = "UPDATE  MEM_PACKAGE_SECTION_PRICE SET " +
//		" PRICE_TYPE='"+parm.getValue("PRICE_TYPE")+"'," +
		" SECTION_CODE='"+parm.getValue("SECTION_CODE")+"'," +
//		" PACKAGE_CODE='"+parm.getValue("PACKAGE_CODE")+"'," +
		" SECTION_PRICE='"+parm.getValue("SECTION_PRICE")+"'," +
		" DISCOUNT_RATE='"+parm.getValue("DISCOUNT_RATE")+"'," +
		" OPT_USER='"+parm.getValue("OPT_USER")+"'," +
		" OPT_DATE=sysdate," +
		" OPT_TERM='"+parm.getValue("OPT_TERM")+"' "+
		" WHERE SECTION_CODE='"+parm.getValue("SECTION_CODE")+"' " +
		" AND PRICE_TYPE = '"+parm.getValue("PRICE_TYPE")+"' " +
		" AND PACKAGE_CODE = '"+parm.getValue("PACKAGE_CODE")+"' ";
//		System.out.println("�޸�ʱ�̼۸�������ݲ���MEM_PACKAGE_SECTION_PRICE sql="+sql);
//		System.out.println("SECTION_PRICE ="+parm.getValue("SECTION_PRICE"));
		return sql;
	}
	
	/**
	 * �޸�ҽ�ײͱ������ݲ���MEM_PACKAGE
	 * add by lich 20141022
	 */
	private String getPackageSql(TParm parm){
		String sql = "UPDATE  MEM_PACKAGE SET " +
		" ORIGINAL_PRICE='"+parm.getValue("OWN_PRICE",0)+"' ,"+
		//��������ۿ۹������ż�ס���--xiongwg20150630
		" CTZ_CODE='"+parm.getValue("CTZ_CODE",0)+"' ,"+
		" ADM_TYPE='"+parm.getValue("ADM_TYPE",0)+"' ,"+
		
		" PACKAGE_PRICE='"+parm.getValue("PACKAGE_PRICE",0)+"' "+
		" WHERE PACKAGE_CODE='"+parm.getValue("PACKAGE_CODE",0)+"' ";
//		System.out.println("�޸�MEM_PACKAGE_SECTION_PRICE����sql="+sql);
		return sql;
	}
	
	/**
	 * �޸�ҽ�ײͼ۸�������ݲ���MEM_PACKAGE_PRICE
	 * add by lich 20141022
	 */
	private String getPackagePriceSql(TParm parm){
//		System.out.println("parm------="+parm);
		String sql = "UPDATE  MEM_PACKAGE_PRICE SET " +
		" PACKAGE_PRICE= "+parm.getValue("PACKAGE_PRICE",0)+" ,"+
		" DISCOUNT_RATE= "+parm.getValue("RATE",0)+" ,"+
		" OPT_USER ='"+parm.getValue("OPT_USER",0)+"' ,"+
		" OPT_TERM ='"+parm.getValue("OPT_TERM",0)+"' ,"+
		" OPT_DATE = sysdate "+
		" WHERE PACKAGE_CODE='"+parm.getValue("PACKAGE_CODE",0)+"' "+
		" AND PRICE_TYPE = '"+parm.getValue("PRICE_TYPE",0)+"' ";
//		System.out.println("�޸�MEM_PACKAGE_SECTION_PRICE����sql="+sql);
		return sql;
	}
	
	
	
	/**
	 * ɾ��ʱ��sql
	 * MEM_PACKAGE_SECTION
	 */
	public String delSectionSql(TParm parm){
		String sql = "DELETE FROM MEM_PACKAGE_SECTION " +
				" WHERE PACKAGE_CODE = '"+parm.getValue("PACKAGE_CODE", 0)+"' " +
				" AND SECTION_CODE = '"+parm.getValue("SECTION_CODE", 0)+"'";
		
		//System.out.println("ɾ������sql="+sql);
		return sql;
	}
	/**
	 * ɾ��ʱ�̼۸�sql
	 * MEM_PACKAGE_SECTION_PRICE
	 * add by lich 20141023
	 */
	public String delSectionPriceSql(TParm parm){
		String sql = "DELETE FROM MEM_PACKAGE_SECTION_PRICE " +
				" WHERE PACKAGE_CODE = '"+parm.getValue("PACKAGE_CODE", 0)+"' " +
				" AND SECTION_CODE = '"+parm.getValue("SECTION_CODE", 0)+"'";
		
		//System.out.println("ɾ������sql="+sql);
		return sql;
	}
	
	
	/**
	 * ����Ƿ��Ǽ���ҽ��sql
	 */
	public String checkIsDetailOrderSql(TParm parm){
		String sql = "SELECT ORDERSET_CODE, ORDER_CODE FROM SYS_ORDERSETDETAIL " +
				" WHERE ORDERSET_CODE = '"+parm.getValue("ORDER_CODE", 0)+"' AND HIDE_FLG = 'Y'";
		//System.out.println("�ж��Ƿ��Ǽ���ҽ��sql:"+sql);
		return sql;
	}
	
	/**
	 * �����ֵ���м���ҽ��ϸ���ܼ۸�sql
	 */
	public String sumPriceDeatailOrderSql1(TParm parm){
		String sql = "SELECT A.ORDER_CODE,B.ORDERSET_CODE,A.ORDER_DESC,  DOSAGE_QTY,OWN_PRICE, " +
				" OWN_PRICE * DOSAGE_QTY AS OWN_AMT FROM SYS_FEE A, SYS_ORDERSETDETAIL B " +
				" WHERE A.ORDER_CODE = B.ORDER_CODE and A.ACTIVE_FLG = 'Y' AND B.HIDE_FLG='Y' " +
				" AND B.ORDERSET_CODE = '"+parm.getValue("ORDER_CODE", 0)+"'";
		//System.out.println("�����ֵ���м���ҽ��ϸ���ܼ۸�sql:"+sql);
		return sql;
	}
	
	/**
	 * 
	 */
	public String getDetailOrderDataSql(TParm parm){
		String sql = "SELECT A.ORDER_CODE,B.ORDERSET_CODE,A.ORDER_DESC, A.SPECIFICATION, DOSAGE_QTY," +
				" UNIT_CODE AS MEDI_UNIT, OWN_PRICE, OWN_PRICE * DOSAGE_QTY" +
				" AS OWN_AMT, EXEC_DEPT_CODE, OPTITEM_CODE, INSPAY_TYPE " +
				" FROM SYS_FEE A, SYS_ORDERSETDETAIL B " +
				" WHERE A.ORDER_CODE = B.ORDER_CODE and A.ACTIVE_FLG = 'Y' AND B.HIDE_FLG='Y' " +
				" AND B.ORDERSET_CODE = '"+parm.getValue("ORDER_CODE",0)+"'";
		//System.out.println("--------->getDetailOrderDataSql:"+sql);
		return sql;
	}
	
	
	/**
	 * ���ײͼ۸���в�ѯ���ݣ�ͨ���۸����ͺ��ײʹ��룬
	 * MEM_PACKAGE_PRICE
	 * @param parm
	 * @return
	 */
	public String getSelectSql(TParm parm){
		String sql = "SELECT COUNT(*) AS COUNT " +
					" FROM MEM_PACKAGE_PRICE " +
					" WHERE PRICE_TYPE = '"+parm.getValue("PRICE_TYPE",0)+"' " +
					" AND PACKAGE_CODE = '"+parm.getValue("PACKAGE_CODE",0)+"'";
		return sql;
	}
	
	
	/**
	 * ���ײͼ۸���в�������
	 * MEM_PACKAGE_PRICE
	 * @param parm
	 * @return
	 */
	public String getInsertPackagePriceSql(TParm parm){
		String sql = "INSERT INTO MEM_PACKAGE_PRICE "
				+ "(PRICE_TYPE, PACKAGE_CODE, PACKAGE_PRICE, DISCOUNT_RATE,"
				+ "OPT_USER, OPT_DATE, OPT_TERM" + ")" + "VALUES ('"
				+ parm.getValue("PRICE_TYPE",0) + "', '"
				+ parm.getValue("PACKAGE_CODE",0) + "', "
				+ parm.getValue("PACKAGE_PRICE",0) + ", "
				+ parm.getValue("RATE",0) + ",' "
				+ parm.getValue("OPT_USER",0) + "',sysdate, '" + parm.getValue("OPT_TERM",0) + "'" + ")";
		return sql;
	}
	
	
	
	
	
	/**
	 * �ײ����ѿ���ҽ���ԱȺ󣬸������� ==huangtt 20141021
	 * @param parm
	 * @return
	 */
	public TParm updateOrderPack(TParm packOrderParm){
		TConnection connection = getConnection();
		TParm result = new TParm();
//		TParm orderMDParm = parm.getParm("orderMDParm");
//		TParm packDParm = parm.getParm("packDParm");
//		TParm packMParm = parm.getParm("packMParm"); 
		String mrNo = packOrderParm.getValue("mrNo");
		String caseNo = packOrderParm.getValue("caseNo");
		String packSecID = packOrderParm.getValue("packSecID");
		String tradeNo = packOrderParm.getValue("tradeNo");
		TParm execPack = packOrderParm.getParm("execPack");
		TParm orderMemParm = packOrderParm.getParm("orderMemParm");//�ײ���ת����
//		System.out.println("execPack=="+execPack);
		
		if(orderMemParm.getCount() > 0){
			for (int i = 0; i < orderMemParm.getCount(); i++) {
				String sqlMem = "SELECT * FROM MEM_PAT_PACKAGE_SECTION_D " +
						" WHERE ID='"+orderMemParm.getValue("MEM_PACKAGE_ID", i)+"' AND CASE_NO='"+caseNo+"'";
				result = new TParm(TJDODBTool.getInstance().select(sqlMem));
				if("Y".equals(result.getValue("SETMAIN_FLG", 0))){
					sqlMem = "UPDATE MEM_PAT_PACKAGE_SECTION_D SET CASE_NO='1',USED_FLG='0' " +
					" WHERE ORDERSET_ID='"+orderMemParm.getValue("MEM_PACKAGE_ID", i)+"' AND CASE_NO='"+caseNo+"'";
					result = new TParm(TJDODBTool.getInstance().update(sqlMem, connection));
					if (result.getErrCode() < 0) {
						connection.rollback();
						connection.close();
						return result;
					}
				}
					
			    sqlMem = "UPDATE MEM_PAT_PACKAGE_SECTION_D SET CASE_NO='1',USED_FLG='0' " +
						" WHERE ID='"+orderMemParm.getValue("MEM_PACKAGE_ID", i)+"' AND CASE_NO='"+caseNo+"'";
				result = new TParm(TJDODBTool.getInstance().update(sqlMem, connection));
    			if (result.getErrCode() < 0) {
    				connection.rollback();
    				connection.close();
    				return result;
    			}
				
				String sqlOrder = "UPDATE OPD_ORDER SET AR_AMT=OWN_AMT*DISCOUNT_RATE," +
						"PAYAMOUNT=OWN_AMT-OWN_AMT*DISCOUNT_RATE," +
						"MEM_PACKAGE_FLG='N',MEM_PACKAGE_ID=''" +
						" WHERE MEM_PACKAGE_ID='"+orderMemParm.getValue("MEM_PACKAGE_ID", i)+"' AND CASE_NO='"+caseNo+"'";
				result = new TParm(TJDODBTool.getInstance().update(sqlOrder, connection));
    			if (result.getErrCode() < 0) {
    				connection.rollback();
    				connection.close();
    				return result;
    			}
				
			}
			
		}

    	if(execPack.getCount() > 0 || packOrderParm.getCount("ORDER_CODE") > 0){
    		
    		TParm orderMDParm = new TParm(); //����opd_order���ײ͵�����
        	TParm packDParm = new TParm();  //����MEM_PAT_PACKAGE_SECTION_D������
        	TParm packMParm = new TParm();  //����MEM_PAT_PACKAGE_SECTION_M������
    		
    		for(int i=0;i<packOrderParm.getCount("ORDER_CODE");i++){
        		String sql = "SELECT ORDERSET_CODE, ORDERSET_GROUP_NO,SECTION_CODE,PACKAGE_CODE" +
        				" FROM MEM_PAT_PACKAGE_SECTION_D" +
        				" WHERE TRADE_NO = '"+packOrderParm.getValue("TRADE_NO", i)+"'" +
//        				" AND ORDER_CODE = '"+packOrderParm.getValue("ORDER_CODE", i)+"'" +
        				" AND ID = '"+packOrderParm.getValue("ID", i)+"' " ;
//        				" AND HIDE_FLG='N'";
        		TParm parm = new TParm(TJDODBTool.getInstance().select(sql)); //���ײ��еõ�����ҽ�������
        		sql = "SELECT ORDER_CODE, RETAIL_PRICE, ORDERSET_CODE,HIDE_FLG,ID,TRADE_NO,SETMAIN_FLG" +
        					" FROM MEM_PAT_PACKAGE_SECTION_D" +
        					" WHERE TRADE_NO = '"+packOrderParm.getValue("TRADE_NO", i)+"'" +
        					" AND ORDERSET_CODE = '"+parm.getValue("ORDERSET_CODE", 0)+"'" +
        					" AND PACKAGE_CODE = '"+parm.getValue("PACKAGE_CODE", 0)+"'" +
        					" AND SECTION_CODE = '"+parm.getValue("SECTION_CODE", 0)+"'" +
        					" AND ORDERSET_GROUP_NO = "+parm.getInt("ORDERSET_GROUP_NO", 0);   		
        		TParm parmD = new TParm(TJDODBTool.getInstance().select(sql)); //�ײ�����ϸ���orderCode �ͼ�Ǯ
        		if("9".equals(packOrderParm.getValue("RX_TYPE",i)) && "CLINIC_FEE".equals(packOrderParm.getValue("RX_NO",i))){

        			orderMDParm.addData("RETAIL_PRICE", parmD.getValue("RETAIL_PRICE", 0));
        			orderMDParm.addData("RX_NO", packOrderParm.getValue("RX_NO", i));
        			orderMDParm.addData("SEQ_NO", packOrderParm.getValue("SEQ_NO", i));
        			orderMDParm.addData("CASE_NO", caseNo);
        			orderMDParm.addData("MEM_PACKAGE_ID", packOrderParm.getValue("ID", i));
        			
        			packDParm.addData("ID", parmD.getValue("ID", 0));
            		packDParm.addData("TRADE_NO", parmD.getValue("TRADE_NO", 0));
            		packDParm.addData("ORDER_CODE", parmD.getValue("ORDER_CODE", 0));
            		packDParm.addData("MR_NO", mrNo);
            		packDParm.addData("CASE_NO", caseNo);
            		
        		}else{
        			sql = "SELECT * FROM OPD_ORDER WHERE CASE_NO = '"+caseNo+"' AND RX_NO='"+packOrderParm.getValue("RX_NO", i)+"' " +
        					" AND SEQ_NO = '"+packOrderParm.getValue("SEQ_NO", i)+"'";
        			TParm orderParm = new TParm(TJDODBTool.getInstance().select(sql));
        			if( !orderParm.getBoolean("SETMAIN_FLG", 0) && orderParm.getValue("ORDERSET_CODE", 0).length() == 0){ //�Ǽ���ҽ��
        				orderMDParm.addData("RETAIL_PRICE", parmD.getValue("RETAIL_PRICE", 0));
            			orderMDParm.addData("RX_NO", packOrderParm.getValue("RX_NO", i));
            			orderMDParm.addData("SEQ_NO", packOrderParm.getValue("SEQ_NO", i));
            			orderMDParm.addData("CASE_NO", caseNo);
            			orderMDParm.addData("MEM_PACKAGE_ID", packOrderParm.getValue("ID", i));
            			
            			packDParm.addData("ID", parmD.getValue("ID", 0));
                		packDParm.addData("TRADE_NO", parmD.getValue("TRADE_NO", 0));
                		packDParm.addData("ORDER_CODE", parmD.getValue("ORDER_CODE", 0));
                		packDParm.addData("MR_NO", mrNo);
                		packDParm.addData("CASE_NO", caseNo);
        			}else{
        				sql = "SELECT * FROM OPD_ORDER WHERE CASE_NO = '"+caseNo+"' AND RX_NO='"+packOrderParm.getValue("RX_NO", i)+"' " +
    					" AND ORDERSET_CODE = '"+packOrderParm.getValue("ORDER_CODE", i)+"' "+
        				" AND ORDERSET_GROUP_NO = '"+packOrderParm.getValue("ORDERSET_GROUP_NO", i)+"' ";
        				TParm orderParmMD = new TParm(TJDODBTool.getInstance().select(sql));
        				if(orderParmMD.getCount() == parmD.getCount()){  //����ҽ�����ײ͵�ϸ���Ƿ�һ��
        					for (int j = 0; j < orderParmMD.getCount(); j++) {
        						for (int j2 = 0; j2 < parmD.getCount(); j2++) {
    								if(orderParmMD.getValue("ORDER_CODE", j).equals(parmD.getValue("ORDER_CODE", j2))){
    									
    									if(orderParmMD.getBoolean("SETMAIN_FLG", j)){
    										orderMDParm.addData("RETAIL_PRICE", orderParmMD.getValue("AR_AMT", 0));
    									}else{
    										orderMDParm.addData("RETAIL_PRICE", parmD.getValue("RETAIL_PRICE", j2));
    									}
    				        			orderMDParm.addData("RX_NO", orderParmMD.getValue("RX_NO", j));
    				        			orderMDParm.addData("SEQ_NO", orderParmMD.getValue("SEQ_NO", j));
    				        			orderMDParm.addData("CASE_NO", caseNo);
    				        			orderMDParm.addData("MEM_PACKAGE_ID", packOrderParm.getValue("ID", i));
    				        			
    				        			packDParm.addData("ID", parmD.getValue("ID", j2));
    				            		packDParm.addData("TRADE_NO", parmD.getValue("TRADE_NO", j2));
    				            		packDParm.addData("ORDER_CODE", parmD.getValue("ORDER_CODE", j2));
    				            		packDParm.addData("MR_NO", mrNo);
    				            		packDParm.addData("CASE_NO", caseNo);
    									
    									
    									
    								}
    							}

    						}
        				}else{
        					result.setErrCode(-2);
        					result.setData("MESSAGE", packOrderParm.getValue("ORDER_CODE", i)+" ����ҽ�����ײ���ϸ����������");
        					return result; 
        				}
        			}
        			
        		}
        		

        	}
        	
        	//�����ײ��й�ѡִ��ʱ�������ײ�����
    		for (int i = 0; i < execPack.getCount(); i++) {
    			String sql = "SELECT ORDERSET_CODE, ORDERSET_GROUP_NO,SECTION_CODE,PACKAGE_CODE"
    					+ " FROM MEM_PAT_PACKAGE_SECTION_D"
    					+ " WHERE TRADE_NO = '"+ execPack.getValue("TRADE_NO", i)+ "'"
    					+ " AND ORDER_CODE = '"+ execPack.getValue("ORDER_CODE", i)+ "'"
    					+ " AND ID = '"+ execPack.getValue("ID", i) + "' " 
    					+ " AND HIDE_FLG='N'";
    			TParm parm = new TParm(TJDODBTool.getInstance().select(sql)); // ���ײ��еõ�����ҽ�������
    			sql = "SELECT ORDER_CODE, RETAIL_PRICE, ORDERSET_CODE,HIDE_FLG,ID,TRADE_NO"
    					+ " FROM MEM_PAT_PACKAGE_SECTION_D"
    					+ " WHERE TRADE_NO = '"+ execPack.getValue("TRADE_NO", i)+ "'"
    					+ " AND ORDERSET_CODE = '"+ parm.getValue("ORDERSET_CODE", 0)+ "'"
    					+ " AND PACKAGE_CODE = '"+ parm.getValue("PACKAGE_CODE", 0)+ "'"
    					+ " AND SECTION_CODE = '"+ parm.getValue("SECTION_CODE", 0)+ "'"
    					+ " AND ORDERSET_GROUP_NO = "+ parm.getInt("ORDERSET_GROUP_NO", 0);
    			TParm parmD = new TParm(TJDODBTool.getInstance().select(sql)); // �ײ�����ϸ��
    			for(int j=0;j<parmD.getCount();j++){
        			packDParm.addData("ID", parmD.getValue("ID", j));
            		packDParm.addData("TRADE_NO", parmD.getValue("TRADE_NO", j));
            		packDParm.addData("ORDER_CODE", parmD.getValue("ORDER_CODE", j));
            		packDParm.addData("MR_NO", mrNo);
            		packDParm.addData("CASE_NO", caseNo);
        		}
    			
    		}
        	
        	packDParm.setCount(packDParm.getCount("ID"));	
        	orderMDParm.setCount(orderMDParm.getCount("CASE_NO"));
        
        	packMParm.setData("TRADE_NO", tradeNo);
        	packMParm.setData("MR_NO", mrNo);
        	packMParm.setData("CASE_NO", caseNo);
        	packMParm.setData("ID", packSecID);
    				
    		for(int i=0;i<orderMDParm.getCount();i++){
    			
    			result = MEMTool.getInstance().updateOpdOrder(orderMDParm.getRow(i), connection);
    			if (result.getErrCode() < 0) {
    				connection.rollback();
    				connection.close();
    				return result;
    			}

    			
    		}
    		
    		for(int i=0;i<packDParm.getCount();i++){
    			result = MEMTool.getInstance().updatePaceageSectionD(packDParm.getRow(i), connection);
    			if (result.getErrCode() < 0) {
    				connection.rollback();
    				connection.close();
    				return result;
    			}
    		}
    		result = MEMTool.getInstance().updatePaceageSection(packMParm, connection);
    		if (result.getErrCode() < 0) {
    			connection.rollback();
    			connection.close();
    			return result;
    		}
    	}
    	
    	
		
		connection.commit();
		connection.close();
		return result;
	}
	
	/**
	 * ��Ŀ�������ײͽ����滻  add by huangtt 20150713
	 * @param parm
	 * @return
	 */
	public TParm onSaveComorderRelpace(TParm parm){
		TConnection connection = getConnection();
		TParm newOrderParm = parm.getParm("newOrderParm");
		TParm tableParm = parm.getParm("tableParm");
		TParm result = new TParm();
		
		TParm fineOrderParm = new TParm();
		TParm priceOrderParm= new TParm();
		TParm patFineOrderParm = new TParm(); //�ͻ��õ���ҽ��ϸ��
		TParm packSection = new TParm(); //ʱ���ܼ��޸�
//		for (int i = 0; i < tableParm.getCount("ID"); i++) {
//			if(tableParm.getBoolean("SETMAIN_FLG",i)){
//				
//				//ɾ��MEM_PACKAGE_SECTION_D_PRICE����ϸ��ļ�Ǯ
//				result = MEMTool.getInstance().delMemPackageSectionDPrice(tableParm.getRow(i), connection);
//				if (result.getErrCode() < 0) {
//					connection.rollback();
//					connection.close();
//					return result;
//				}
//				//��ɾ����ҽ����ϸ�� MEM_PACKAGE_SECTION_D
//				result = MEMTool.getInstance().delMemPackageSectionD(tableParm.getRow(i), connection);
//				if (result.getErrCode() < 0) {
//					connection.rollback();
//					connection.close();
//					return result;
//				}
//				
//				//�õ�ϸ���е�ԭ�ۣ������ۼۣ�����MEM_PACKAGE_SECTION_D_PRICE����
//				fineOrderParm = new TParm();
//				for (int j = 0; j < newOrderParm.getCount("ORDER_CODE"); j++) {
//					if(newOrderParm.getValue("ORDERSET_CODE", j).equals(tableParm.getValue("NEW_ORDER_CODE", i)) &&
//							newOrderParm.getValue("PACKAGE_CODE", j).equals(tableParm.getValue("PACKAGE_CODE", i)) &&
//							newOrderParm.getValue("SECTION_CODE", j).equals(tableParm.getValue("SECTION_CODE", i)) &&
//							newOrderParm.getValue("ORDERSET_GROUP_NO", j).equals(tableParm.getValue("ORDERSET_GROUP_NO", i)) &&
//							newOrderParm.getBoolean("HIDE_FLG", j)
//							){
//						fineOrderParm.addData("PACKAGE_CODE", newOrderParm.getValue("PACKAGE_CODE", j));
//						fineOrderParm.addData("SECTION_CODE", newOrderParm.getValue("SECTION_CODE", j));
//						fineOrderParm.addData("ID", newOrderParm.getValue("ID", j));
//						fineOrderParm.addData("OPT_USER", newOrderParm.getValue("OPT_USER", j));
//						fineOrderParm.addData("OPT_TERM", newOrderParm.getValue("OPT_TERM", j));
//						fineOrderParm.addData("VERSION_NUMBER", newOrderParm.getValue("VERSION_NUMBER", j));
//						fineOrderParm.addData("ORIGINAL_PRICE", newOrderParm.getValue("SUM_PRICE", j));
////						patFineOrderParm.addRowData(newOrderParm, j);
//					}
//				}
//				String sql = "SELECT PRICE_TYPE,RETAIL_PRICE,ORIGINAL_PRICE FROM MEM_PACKAGE_SECTION_D_PRICE " +
//						" WHERE ID='"+tableParm.getValue("ID", i)+"'" +
//								" AND SECTION_CODE='"+tableParm.getValue("SECTION_CODE", i)+"' " +
//								" AND PACKAGE_CODE='"+tableParm.getValue("PACKAGE_CODE", i)+"'";
//				TParm priceD = new TParm(TJDODBTool.getInstance().select(sql));
//				for (int j = 0; j < priceD.getCount(); j++) {				
//					priceD.setData("ORIGINAL_PRICE", j, tableParm.getValue("NEW_SUM_PRICE", i));
//					
//					BigDecimal sumPrice =new BigDecimal(0); //�ۼ��ܺ�
//					BigDecimal retailPrice = new BigDecimal(priceD.getDouble("RETAIL_PRICE", j));
//					double price =0;//���һ�μ�����ۼ�
//					for (int j2 = 0; j2 < fineOrderParm.getCount("ID"); j2++) {
//						priceOrderParm.addRowData(fineOrderParm, j2);
//						priceOrderParm.addData("PRICE_TYPE", priceD.getValue("PRICE_TYPE", j));
//						//�����ۼ�
//						BigDecimal a = new BigDecimal(priceD.getDouble("RETAIL_PRICE", j));
//						BigDecimal b = new BigDecimal(fineOrderParm.getDouble("ORIGINAL_PRICE", j2));
//						BigDecimal c = new BigDecimal(priceD.getDouble("ORIGINAL_PRICE",j));					
//						BigDecimal tmp = b.multiply(a);
//						BigDecimal r = tmp.divide(c,2,BigDecimal.ROUND_HALF_UP);
//						sumPrice = sumPrice.add(r);	
//						price = r.doubleValue();
////						System.out.println("price==="+price);
//						priceOrderParm.addData("RETAIL_PRICE", price);
//						
//					}
//					//ϸ���ۼ�֮����������ڲ����ϸ������һ����в�ƽ
//					int row = priceOrderParm.getCount("ID")-1;
//					BigDecimal pr = new BigDecimal(price);				
//					retailPrice = retailPrice.add(pr);
//					sumPrice = sumPrice.negate();
//					retailPrice = retailPrice.add(sumPrice);
////					System.out.println("retailPrice==="+retailPrice.doubleValue());
//					priceOrderParm.setData("RETAIL_PRICE", row, retailPrice.doubleValue());
//	
//				}
//				
//
//			}
//			
//			
//			packSection = new TParm(); //ʱ���ܼ��޸�
//			String priceSql = "SELECT ORIGINAL_PRICE FROM MEM_PACKAGE_SECTION " +
//					"WHERE PACKAGE_CODE = '"+tableParm.getValue("PACKAGE_CODE", i)+"' " +
//					" AND SECTION_CODE = '"+tableParm.getValue("SECTION_CODE", i)+"' ";
//			TParm priceParm = new TParm(TJDODBTool.getInstance().select(priceSql));
//			packSection.setData("PACKAGE_CODE", tableParm.getValue("PACKAGE_CODE", i));
//			packSection.setData("SECTION_CODE", tableParm.getValue("SECTION_CODE", i));
//			packSection.setData("VERSION_NUMBER", tableParm.getValue("VERSION_NUMBER", i));
//			packSection.setData("OPT_TERM", tableParm.getValue("OPT_TERM", i));
//			packSection.setData("OPT_USER", tableParm.getValue("OPT_USER", i));
//			double originalPrice = priceParm.getDouble("ORIGINAL_PRICE", 0)+tableParm.getDouble("NEW_SUM_PRICE", i)-tableParm.getDouble("SUM_PRICE", i);
//			packSection.setData("ORIGINAL_PRICE", originalPrice);
//			
//			//����MEM_PACKAGE_SECTION���е�ORIGINAL_PRICE�ֶ�
//			result =  MEMTool.getInstance().updateMemPackageSection(packSection, connection);
//			if (result.getErrCode() < 0) {
//				connection.rollback();
//				connection.close();
//				return result;
//			}
//			
//			//����MEM_PACKAGE_SECTION_PRICE����ORIGINAL_PRICE
//			result =  MEMTool.getInstance().updateMemPackageSectionPrice(packSection, connection);
//			if (result.getErrCode() < 0) {
//				connection.rollback();
//				connection.close();
//				return result;
//			}
//			
//			//������ʷ��
//			result =  MEMTool.getInstance().insertMemPacageSectionDHistory(tableParm.getRow(i), connection);
//			if (result.getErrCode() < 0) {
//				connection.rollback();
//				connection.close();
//				return result;
//			}
//			
//	
//		}
//		
//		
//		for (int k = 0; k < priceOrderParm.getCount("ID"); k++) {
//			//����MEM_PACKAGE_SECTION_D_PRICE��(ϸ��)
//			result =  MEMTool.getInstance().insertMemPackageSectionDPrice(priceOrderParm.getRow(k), connection);
//			if (result.getErrCode() < 0) {
//				connection.rollback();
//				connection.close();
//				return result;
//			}
//		}
//		
//		
//		for (int i = 0; i < newOrderParm.getCount("ORDER_CODE"); i++) {
//			if(newOrderParm.getBoolean("HIDE_FLG",i)){
//				//�����ҽ����ϸ��
//				result = MEMTool.getInstance().insertMemPackageSectionD(newOrderParm.getRow(i), connection);
//				if (result.getErrCode() < 0) {
//					connection.rollback();
//					connection.close();
//					return result;
//				}
//			}else{
//				
//				//����ҽ��������ҽ����
//				result = MEMTool.getInstance().updateMemPackageSectionD(newOrderParm.getRow(i), connection);
//				if (result.getErrCode() < 0) {
//					connection.rollback();
//					connection.close();
//					return result;
//				}
//				
//				
//				
//				//����MEM_PACKAGE_SECTION_D_PRICE���е��ۿ�����ԭ��
//				result = MEMTool.getInstance().updateMemPackageSectionDPrice(newOrderParm.getRow(i), connection);
//				if (result.getErrCode() < 0) {
//					connection.rollback();
//					connection.close();
//					return result;
//				}
//			}
//			
//			
//		}
		
		//����ʱ����δʹ�ù���ҽ�����и���
		for (int i = 0; i < tableParm.getCount("ID"); i++) {

			//���Ϊ����ҽ���Ļ�����ɾ����ҽ����ϸ��������ҽ����ϸ��
			if(tableParm.getBoolean("SETMAIN_FLG",i)){
				patFineOrderParm = new TParm();
				for (int j = 0; j < newOrderParm.getCount("ORDER_CODE"); j++) {
					if(newOrderParm.getValue("ORDERSET_CODE", j).equals(tableParm.getValue("NEW_ORDER_CODE", i)) &&
							newOrderParm.getValue("TRADE_NO", j).equals(tableParm.getValue("TRADE_NO", i)) &&
							newOrderParm.getValue("PACKAGE_CODE", j).equals(tableParm.getValue("PACKAGE_CODE", i)) &&
							newOrderParm.getValue("SECTION_CODE", j).equals(tableParm.getValue("SECTION_CODE", i)) &&
							newOrderParm.getValue("ORDERSET_GROUP_NO", j).equals(tableParm.getValue("ORDERSET_GROUP_NO", i)) &&
							newOrderParm.getBoolean("HIDE_FLG", j)
							){

						patFineOrderParm.addRowData(newOrderParm, j);
					}
				}
				
				
				//�Ȳ�ѯ�������ģ�δʹ�õ���������
				String sql = "SELECT ID,TRADE_NO,PACKAGE_CODE,SECTION_CODE,PACKAGE_DESC,SECTION_DESC," +
						" CASE_NO,MR_NO,SEQ,ORDER_CODE,ORDER_DESC,ORDER_NUM,UNIT_CODE," +
						" UNIT_PRICE,RETAIL_PRICE,DESCRIPTION, OPT_DATE,OPT_USER,OPT_TERM," +
						" USED_FLG,SETMAIN_FLG,ORDERSET_CODE,HIDE_FLG,ORDERSET_GROUP_NO," +
						" REST_TRADE_NO,TRADE_ENG_DESC, PACKAGE_ENG_DESC,ORDERSET_ID,VERSION_NUMBER " +
						" FROM MEM_PAT_PACKAGE_SECTION_D WHERE " +
						" PACKAGE_CODE = '"+tableParm.getValue("PACKAGE_CODE", i)+"' " +
						" AND SECTION_CODE = '"+tableParm.getValue("SECTION_CODE", i)+"' " +
						" AND ID = '"+tableParm.getValue("ID", i)+"' " +
						" AND ORDER_CODE = '"+tableParm.getValue("ORDER_CODE", i)+"' AND USED_FLG = '0'";

				TParm patSection = new TParm(TJDODBTool.getInstance().select(sql));
//				System.out.println("patSection=="+patSection);
				
				//ɾ����ҽ��ϸ��
				result = MEMTool.getInstance().delMemPatPackageSectionD(patSection.getRow(0), connection);
				if (result.getErrCode() < 0) {
					connection.rollback();
					connection.close();
					return result;
				}
				
				//���ϸ��
				TParm patParm = patSection.getRow(0);
				sql = "SELECT MAX(SEQ) SEQ FROM MEM_PAT_PACKAGE_SECTION_D " +
				" WHERE PACKAGE_CODE = '"+patSection.getValue("PACKAGE_CODE", 0)+"'" +
				" AND SECTION_CODE = '"+patSection.getValue("SECTION_CODE", 0)+"'" +
				" AND TRADE_NO = '"+patSection.getValue("TRADE_NO", 0)+"' ";
				TParm seqParm = new TParm(TJDODBTool.getInstance().select(sql));
				int seq = seqParm.getInt("SEQ", 0)+1;
				
				for (int j = 0; j < patFineOrderParm.getCount("ID"); j++) {
					
					patParm.setData("ID", patFineOrderParm.getValue("ID", j));						
					patParm.setData("SEQ", seq+j);
					patParm.setData("ORDERSET_CODE", patFineOrderParm.getValue("ORDERSET_CODE", j));
					patParm.setData("ORDER_CODE", patFineOrderParm.getValue("ORDER_CODE", j));
					patParm.setData("ORDER_DESC", patFineOrderParm.getValue("ORDER_DESC", j));
					patParm.setData("UNIT_CODE", patFineOrderParm.getValue("UNIT_CODE", j));
					patParm.setData("UNIT_PRICE", patFineOrderParm.getValue("UNIT_PRICE", j));
					patParm.setData("VERSION_NUMBER", patFineOrderParm.getValue("VERSION_NUMBER", j));
					patParm.setData("OPT_USER", patFineOrderParm.getValue("OPT_USER", j));
					patParm.setData("OPT_TERM", patFineOrderParm.getValue("OPT_TERM", j));
					patParm.setData("UN_NUM_FLG", patFineOrderParm.getValue("UN_NUM_FLG", j));
					patParm.setData("RETAIL_PRICE", patFineOrderParm.getValue("RETAIL_PRICE", j));
					patParm.setData("SETMAIN_FLG", "N");
					patParm.setData("HIDE_FLG", "Y");
					
					result = MEMTool.getInstance().insertMemPatPackageSectionD(patParm, connection);
					if (result.getErrCode() < 0) {
						connection.rollback();
						connection.close();
						return result;
					}
					
				}
				
				
				
				
//				for (int j = 0; j < patSection.getCount(); j++) {
//					//ɾ����ҽ��ϸ��
//					result = MEMTool.getInstance().delMemPatPackageSectionD(patSection.getRow(j), connection);
//					if (result.getErrCode() < 0) {
//						connection.rollback();
//						connection.close();
//						return result;
//					}
//					
//					//���ϸ��
//					TParm patParm = patSection.getRow(j);
//					sql = "SELECT MAX(SEQ) SEQ FROM MEM_PAT_PACKAGE_SECTION_D " +
//					" WHERE PACKAGE_CODE = '"+patSection.getValue("PACKAGE_CODE", j)+"'" +
//					" AND SECTION_CODE = '"+patSection.getValue("SECTION_CODE", j)+"'" +
//					" AND TRADE_NO = '"+patSection.getValue("TRADE_NO", j)+"' ";
//					
//					BigDecimal sumPrice =new BigDecimal(0); //�ۼ��ܺ�
//					BigDecimal retailPrice = new BigDecimal(patSection.getDouble("RETAIL_PRICE", j)); //������ۼ�
//					double price =0;//���һ�μ�����ۼ�
//					
//					for (int j2 = 0; j2 < patFineOrderParm.getCount("ID")-1; j2++) {
//						patParm.setData("ID", SystemTool.getInstance().getNo("ALL", "EKT", "MEM_NO","MEM_NO"));						
//						TParm seqParm = new TParm(TJDODBTool.getInstance().select(sql));
//						patParm.setData("SEQ", seqParm.getInt("SEQ", 0)+1);
//						patParm.setData("ORDERSET_CODE", patFineOrderParm.getValue("ORDERSET_CODE", j2));
//						patParm.setData("ORDER_CODE", patFineOrderParm.getValue("ORDER_CODE", j2));
//						patParm.setData("ORDER_DESC", patFineOrderParm.getValue("ORDER_DESC", j2));
//						patParm.setData("UNIT_CODE", patFineOrderParm.getValue("UNIT_CODE", j2));
//						patParm.setData("UNIT_PRICE", patFineOrderParm.getValue("UNIT_PRICE", j2));
//						patParm.setData("VERSION_NUMBER", patFineOrderParm.getValue("VERSION_NUMBER", j2));
//						patParm.setData("OPT_USER", patFineOrderParm.getValue("OPT_USER", j2));
//						patParm.setData("OPT_TERM", patFineOrderParm.getValue("OPT_TERM", j2));
//						patParm.setData("UN_NUM_FLG", patFineOrderParm.getValue("UN_NUM_FLG", j2));
//						
//						//�����ۼ�
//						BigDecimal a = new BigDecimal(patSection.getDouble("RETAIL_PRICE", j));
//						BigDecimal b = new BigDecimal(patFineOrderParm.getDouble("UNIT_PRICE", j2));
//						BigDecimal c = new BigDecimal(patSection.getDouble("UNIT_PRICE",j));					
//						BigDecimal tmp = b.multiply(a);
//						BigDecimal r = tmp.divide(c,2,BigDecimal.ROUND_HALF_UP);
//						sumPrice = sumPrice.add(r);	
//						price = r.doubleValue();
//						patParm.setData("RETAIL_PRICE", price);
//						patParm.setData("SETMAIN_FLG", "N");
//						patParm.setData("HIDE_FLG", "Y");
//						
//						result = MEMTool.getInstance().insertMemPatPackageSectionD(patParm, connection);
//						if (result.getErrCode() < 0) {
//							connection.rollback();
//							connection.close();
//							return result;
//						}
//
//					}
//					
//					//Ϊ�˱�֤ϸ������ۼ۸��ܸ�������������ۼ۸����һ��ֱ�������ǰ�漸��֮��
//					int j2= patFineOrderParm.getCount("ID")-1;
//					patParm.setData("ID", SystemTool.getInstance().getNo("ALL", "EKT", "MEM_NO","MEM_NO"));						
//					TParm seqParm = new TParm(TJDODBTool.getInstance().select(sql));
//					patParm.setData("SEQ", seqParm.getInt("SEQ", 0)+1);
//					patParm.setData("ORDER_CODE", patFineOrderParm.getValue("ORDER_CODE", j2));
//					patParm.setData("ORDER_DESC", patFineOrderParm.getValue("ORDER_DESC", j2));
//					patParm.setData("UNIT_CODE", patFineOrderParm.getValue("UNIT_CODE", j2));
//					patParm.setData("UNIT_PRICE", patFineOrderParm.getValue("UNIT_PRICE", j2));
//					patParm.setData("VERSION_NUMBER", patFineOrderParm.getValue("VERSION_NUMBER", j2));
//					patParm.setData("OPT_USER", patFineOrderParm.getValue("OPT_USER", j2));
//					patParm.setData("OPT_TERM", patFineOrderParm.getValue("OPT_TERM", j2));
//					patParm.setData("UN_NUM_FLG", patFineOrderParm.getValue("UN_NUM_FLG", j2));
//					sumPrice = sumPrice.negate();
//					retailPrice = retailPrice.add(sumPrice);
//					patParm.setData("RETAIL_PRICE", retailPrice.doubleValue());
//					patParm.setData("SETMAIN_FLG", "N");
//					patParm.setData("HIDE_FLG", "Y");
//
//					
//					result = MEMTool.getInstance().insertMemPatPackageSectionD(patParm, connection);
//					if (result.getErrCode() < 0) {
//						connection.rollback();
//						connection.close();
//						return result;
//					}
//					
//					
//				}
				
				
			}
			
			
			//���¼���ҽ������ ���Ǽ���ҽ����ҽ��
			result = MEMTool.getInstance().updateMemPatPackageSectionD(tableParm.getRow(i), connection);
			if (result.getErrCode() < 0) {
				connection.rollback();
				connection.close();
				return result;
			}
		}
		
		
		

		connection.commit();
		connection.close();
		return result;
	}
	
	/** 
	 * ��Ŀ�����Ǯ��ʱ������ײ��еļ�Ǯ  add by huangtt 20150713
	 *  
	 * @param parm
	 * @return
	 */
	public TParm onUpdatePackageOrderPrice(TParm parm){
		 TConnection connection = getConnection();
		 TParm result = new TParm();
		 
		 String sectionCode="";
		 String packageCode = "";
		 double sumDiffPrice = 0;
		 
		 for (int i = 0; i < parm.getCount(); i++) {
			 
			 //���� MEM_PACKAGE_SECTION_D�е�UNIT_PRICE��Ǯ
			 result = MEMTool.getInstance().updateMemPackageSectionDUnitPrice(parm.getRow(i), connection);
				if (result.getErrCode() < 0) {
					connection.rollback();
					connection.close();
					return result;
				}
			 
		    //���� MEM_PACKAGE_SECTION_D_PRICE�е�UNIT_PRICE��Ǯ
				result = MEMTool.getInstance().updateMemPackageSectionDPriceOriginal(parm.getRow(i), connection);
				if (result.getErrCode() < 0) {
					connection.rollback();
					connection.close();
					return result;
				}
				
			 
//			 //���û�ҩƷ SETMAIN_FLG= N  HIDE_FLG=N
//			if(!parm.getBoolean("SETMAIN_FLG", i) && !parm.getBoolean("HIDE_FLG", i)){
//				//
//				
//			}
			
			//����ҽ��ϸ�� SETMAIN_FLG= N  HIDE_FLG=Y
			if(!parm.getBoolean("SETMAIN_FLG", i) && parm.getBoolean("HIDE_FLG", i)){
				//���������ID��
				String sql = "SELECT ID,ORDER_CODE FROM MEM_PACKAGE_SECTION_D" +
						" WHERE  ORDERSET_CODE = '"+parm.getValue("ORDERSET_CODE", i)+"'" +
						" AND SECTION_CODE = '"+parm.getValue("SECTION_CODE", i)+"'" +
						" AND PACKAGE_CODE = '"+parm.getValue("PACKAGE_CODE", i)+"'" +
						" AND ORDERSET_GROUP_NO = '"+parm.getValue("ORDERSET_GROUP_NO", i)+"'" +
						" AND SETMAIN_FLG='Y'";
				TParm parmD = new TParm(TJDODBTool.getInstance().select(sql));
				TParm sectionParm = new TParm();
				sectionParm.setData("ID", parmD.getValue("ID", 0));
				sectionParm.setData("ORDER_CODE", parmD.getValue("ORDER_CODE", 0));
				sectionParm.setData("PACKAGE_CODE", parm.getValue("PACKAGE_CODE", i));
				sectionParm.setData("SECTION_CODE", parm.getValue("SECTION_CODE", i));
				sectionParm.setData("DIFF_PRICE", parm.getValue("DIFF_PRICE", i));
				sectionParm.setData("VERSION_NUMBER", parm.getValue("VERSION_NUMBER", i));
				sectionParm.setData("OPT_USER", parm.getValue("OPT_USER", i));
				sectionParm.setData("OPT_TERM", parm.getValue("OPT_TERM", i));
				sectionParm.setData("DIFF_PRICE_SUM", parm.getValue("DIFF_PRICE_SUM", i));
				
				//���������ԭ��
				//���� MEM_PACKAGE_SECTION_D�е�UNIT_PRICE��Ǯ
				 result = MEMTool.getInstance().updateMemPackageSectionDUnitPrice(sectionParm, connection);
					if (result.getErrCode() < 0) {
						connection.rollback();
						connection.close();
						return result;
					}
				 
			    //���� MEM_PACKAGE_SECTION_D_PRICE�е�UNIT_PRICE��Ǯ
					result = MEMTool.getInstance().updateMemPackageSectionDPriceOriginal(sectionParm, connection);
					if (result.getErrCode() < 0) {
						connection.rollback();
						connection.close();
						return result;
					}
					
			}
			
		    TParm packSection = new TParm(); //ʱ���ܼ��޸�
			String priceSql = "SELECT ORIGINAL_PRICE FROM MEM_PACKAGE_SECTION " +
					"WHERE PACKAGE_CODE = '"+parm.getValue("PACKAGE_CODE", i)+"' " +
					" AND SECTION_CODE = '"+parm.getValue("SECTION_CODE", i)+"' ";
			TParm priceParm = new TParm(TJDODBTool.getInstance().select(priceSql));
			packSection.setData("PACKAGE_CODE", parm.getValue("PACKAGE_CODE", i));
			packSection.setData("SECTION_CODE", parm.getValue("SECTION_CODE", i));
			packSection.setData("VERSION_NUMBER", parm.getValue("VERSION_NUMBER", i));
			packSection.setData("OPT_TERM", parm.getValue("OPT_TERM", i));
			packSection.setData("OPT_USER", parm.getValue("OPT_USER", i));
			
			//�����ǰʱ�����ϸ�һ��ʱ����ͬ�Ļ����Ͱ�֮ǰ�Ĳ�Ǯ�ӽ����������ͬ��ֻ���Լ���
			if(!(sectionCode.equals(parm.getValue("SECTION_CODE", i)) && packageCode.equals(parm.getValue("PACKAGE_CODE", i)))){
				sumDiffPrice = 0;
			}
			
			double originalPrice = priceParm.getDouble("ORIGINAL_PRICE", 0)+parm.getDouble("DIFF_PRICE_SUM", i)+sumDiffPrice;
			packSection.setData("ORIGINAL_PRICE", originalPrice);

			//��������еļ�Ǯ MEM_PACKAGE_SECTION
			result =  MEMTool.getInstance().updateMemPackageSection(packSection, connection);
			if (result.getErrCode() < 0) {
				connection.rollback();
				connection.close();
				return result;
			}
			
			//��������еļ�Ǯ MEM_PACKAGE_SECTION_PRICE
			result =  MEMTool.getInstance().updateMemPackageSectionPriceOriginal(packSection, connection);
			if (result.getErrCode() < 0) {
				connection.rollback();
				connection.close();
				return result;
			}
			
           //��¼���ε�ʱ�̴������Ǯ�ܺ�
			sumDiffPrice +=  parm.getDouble("DIFF_PRICE_SUM", i);
			sectionCode = parm.getValue("SECTION_CODE", i);
			packageCode = 	parm.getValue("PACKAGE_CODE", i);
		 }
		 
		//�����ײ��еı���MEM_PACKAGE_PRICE
		 double sumPrice = 0;
		 TParm priceParm  = new TParm();
		 for (int j = 0; j < parm.getCount()-1; j++) {
			 sumPrice += parm.getDouble("DIFF_PRICE_SUM", j);

			if(!parm.getValue("PACKAGE_CODE", j).equals(parm.getValue("PACKAGE_CODE", j+1))){
				priceParm.addData("PACKAGE_CODE", parm.getValue("PACKAGE_CODE", j));
				priceParm.addData("SUM_PRICE", sumPrice);
				priceParm.addData("OPT_TERM", parm.getValue("OPT_TERM", j));
				priceParm.addData("OPT_USER", parm.getValue("OPT_USER", j));
				priceParm.addData("VERSION_NUMBER", parm.getValue("VERSION_NUMBER", j));
				sumPrice = 0;
			}
		}
		    sumPrice += parm.getDouble("DIFF_PRICE_SUM", parm.getCount()-1);
			priceParm.addData("PACKAGE_CODE", parm.getValue("PACKAGE_CODE", parm.getCount()-1));
			priceParm.addData("SUM_PRICE", sumPrice);
			priceParm.addData("OPT_TERM", parm.getValue("OPT_TERM", parm.getCount()-1));
			priceParm.addData("OPT_USER", parm.getValue("OPT_USER", parm.getCount()-1));
			priceParm.addData("VERSION_NUMBER", parm.getValue("VERSION_NUMBER", parm.getCount()-1)); 
			
		for (int j = 0; j < priceParm.getCount("PACKAGE_CODE"); j++) {
			String sumSql = "SELECT SUM(ORIGINAL_PRICE) ORIGINAL_PRICE" +
			" FROM MEM_PACKAGE_SECTION WHERE PACKAGE_CODE = '"+priceParm.getValue("PACKAGE_CODE", j)+"'";
			TParm sumParm = new TParm(TJDODBTool.getInstance().select(sumSql));
//			System.out.println("package=="+priceParm.getValue("PACKAGE_CODE", j));
//			System.out.println("SUM_PRICE=="+priceParm.getValue("SUM_PRICE", j));
//			System.out.println("ORIGINAL_PRICE=="+sumParm.getDouble("ORIGINAL_PRICE", 0));
			
			priceParm.setData("SUM_PRICE", j, priceParm.getDouble("SUM_PRICE", j)+sumParm.getDouble("ORIGINAL_PRICE", 0));
			
			result =  MEMTool.getInstance().updateMemPackagePrice(priceParm.getRow(j), connection);
			if (result.getErrCode() < 0) {
				connection.rollback();
				connection.close();
				return result;
			}
		}	
		 
		 
		connection.commit();
		connection.close();
		return result; 
	}

	/**
	 * �ײ�����ת �����վ�  ����ʹ��״̬
	 * @param parm
	 * @return
	 */
	public TParm onPackBalanceTransfer(TParm parm){
		
		TConnection connection = getConnection();
		TParm result = new TParm();
		
		TParm bilRecpParm = parm.getParm("bilRecpParm");
		TParm selParm = parm.getParm("selParm");
		
		TParm bilInvricpt = new TParm();
		bilInvricpt.setData("RECP_TYPE", "MEM"); // ҽ�ƿ���Ʊ
		bilInvricpt.setData("CANCEL_USER", new TNull(String.class));
		bilInvricpt.setData("CASHIER_CODE", bilRecpParm.getData("OPT_USER"));
		bilInvricpt.setData("OPT_USER", bilRecpParm.getData("OPT_USER"));
		bilInvricpt.setData("INV_NO", bilRecpParm.getData("PRINT_NO"));
		bilInvricpt.setData("OPT_TERM", bilRecpParm.getData("OPT_TERM"));
		bilInvricpt.setData("CANCEL_DATE", new TNull(Timestamp.class));
		bilInvricpt.setData("TOT_AMT", bilRecpParm.getDouble("TOT_AMT"));

		bilInvricpt.setData("AR_AMT", bilRecpParm.getDouble("AR_AMT"));
		// bilInvricpt.setData("INS_AMT", insAmt);//ҽ���ۿ���
		TParm bilInvoice = new TParm();
		bilInvoice.setData("RECP_TYPE", "MEM");
		bilInvoice.setData("STATUS", "0");
		bilInvoice.setData("CASHIER_CODE", bilRecpParm.getData("OPT_USER"));
		bilInvoice.setData("START_INVNO", parm.getData("START_INVNO"));
		bilInvoice.setData("UPDATE_NO", StringTool.addString(bilRecpParm.getData("PRINT_NO").toString()));
		TParm actionParm = new TParm();
		actionParm.setData("BILINVRICPT", bilInvricpt.getData());
		actionParm.setData("BILINVOICE", bilInvoice.getData());
		actionParm.setData("ADM_TYPE", bilRecpParm.getValue("ADM_TYPE")); // ===pangben
																	// 2012-3-19
		actionParm.setData("RECEIPT_NO", bilRecpParm.getData("RECEIPT_NO"));
		 result = BILPrintTool.getInstance().saveEktOpb(actionParm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			connection.rollback();
			connection.close();
			return result;
		}
		
		result = MEMReceiptTool.getInstance().insertMemReceipt(bilRecpParm, connection);
		if (result.getErrCode() < 0) {
			connection.rollback();
			connection.close();
			return result;
		}
		
		for (int i = 0; i < selParm.getCount("MR_NO") ; i++) {
			result = MEMReceiptTool.getInstance().updateMemPackSessionD(selParm.getRow(i), connection);
			if (result.getErrCode() < 0) {
				connection.rollback();
				connection.close();
				return result;
			}
		}
		
		connection.commit();
		connection.close();
		return result;
		
		
	}
	
	 /**
     * ���������ս�
     * @param parm TParm
     * @return TParm
     */
    public TParm onSaveAcctionMEM(TParm parm) {
        TParm result=new TParm();
        if(parm==null)
            return result.newErrParm(-1,"����Ϊ��");
        TConnection connection = getConnection();
        result=MEMReceiptTool.getInstance().onSaveAccountMem(parm,connection);
        if (result==null||result.getErrCode() < 0) {
            connection.close();
             return result;
        }
        connection.commit();
        connection.close();
        return result;

    }

}
