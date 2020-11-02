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
 * Title: 时程套餐
 * </p>
 * 
 * <p>
 * Description: 时程套餐
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
	 * 执行保存事物
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onSave(TParm parm) {
//		System.out.println("执行到ACTION了");
		TConnection conn = getConnection();
		TParm result = new TParm();
		// 获得DataStore中的SQL语句
		try {
			//更新老数据操作--医嘱表MEM_PACKAGE_SECTION_D
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
				//=======pangben 2015-9-2 添加修改集合医嘱细项
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

			//更新老数据操作--医嘱价格表MEM_PACKAGE_SECTION_D_PRICE add by lich  20141022
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
//				System.out.println("医嘱价格表updateSecSql="+updateSql);
//				String updateSql;
//				System.out.println("uparm="+uparm.getValue("PRICE_TYPE"));
//				System.out.println("uparm.length==="+uparm.getValue("PRICE_TYPE").length());
				if(uparm.getValue("PRICE_TYPE").length()==0){
					updateSql = this.getPriceSqlBY(uparm);
				}else{
					updateSql=this.updateOrderPriceInfoSql(uparm);
				}
//				System.out.println("MEM_PACKAGE_SECTION_D_PRICE操作"+updateSql);
//				System.out.println("更新老数据操作--医嘱价格表sql="+updateSql);
				
				
				
				result = new TParm(TJDODBTool.getInstance().update(updateSql, conn));
				//System.out.println("resultD::"+result);
				if (result.getErrCode() < 0) {
					conn.rollback();
					conn.close();
					return result;
				}
			}
			
			
			//删除医嘱信息--医嘱表
			TParm delparm = parm.getParm("DELORDERDATA");
//			System.out.println("开始删除医嘱表");
//			System.out.println("ccccccccccccccccccccccccc");
			for (int i = 0; i < delparm.getCount("SEQ"); i++){
				//如果是主项，先删除细项再删除主项
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
			
			//删除医嘱信息--医嘱价格表 add by lich 
//			System.out.println("开始删除医嘱价格表");
//			System.out.println("ddddddddddddddddddddddd");
			for (int i = 0; i < delparm.getCount("SEQ"); i++){
				//如果是主项，先删除细项再删除主项
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
			
			
			// 新增医嘱信息
//			System.out.println("新增医嘱信息医嘱表");
			TParm insertParm = parm.getParm("INSERTORDERDATA");
//			System.out.println("action中insertParm="+insertParm);
//			System.out.println("新增数量："+insertParm.getCount("SEQ"));
//			System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeee");
			for (int i = 0; i < insertParm.getCount("SEQ"); i++) {
				TParm inparm=insertParm.getRow(i);
				String insertsql=this.getSql(inparm);
//				System.out.println("新增医嘱信息医嘱表价格insertsql==="+insertsql);
				result = new TParm(TJDODBTool.getInstance().update(insertsql, conn));
				if (result.getErrCode() < 0) {
					conn.rollback();
					conn.close();
					return result;
				}
			}
			// 新增医嘱价格信息  add by lich 20141022
//			System.out.println("新增医嘱信息医嘱表价格");
//			System.out.println("fffffffffffffffffffffffff");
			for (int i = 0; i < insertParm.getCount("SEQ"); i++) {
				TParm inparm=insertParm.getRow(i);
				String insertsql=this.getPriceSql(inparm);
//				System.out.println("新增医嘱信息医嘱表价格insertsql==="+insertsql);
				result = new TParm(TJDODBTool.getInstance().update(insertsql, conn));
				if (result.getErrCode() < 0) {
					conn.rollback();
					conn.close();
					return result;
				}
			}
			
			
			/** 提出来单独一个方法
			//统计套餐价格更新时程表对应明细
			//1、查询统计套餐总价
			TParm operSectionParm = parm.getParm("OPERSECTIONDATA");
			TParm oparm=operSectionParm.getRow(0);
			TParm allParm = new TParm(TJDODBTool.getInstance().select(this.getAllFeeD(oparm)));
			System.out.println("allParm="+allParm);
			//2、更新统计数据到时程表
			result = new TParm(TJDODBTool.getInstance().update(
					this.updateAllPriceSql(oparm, allParm.getValue("ALL_UNIT_PRICE", 0), 
							allParm.getValue("ALL_RETAIL_PRICE", 0)), conn));
			if (result.getErrCode() < 0) {
				conn.rollback();
				conn.close();
				return result;
			} **/
			
			//更新老数据操作--时程表
			TParm updateSectionParm = parm.getParm("UPDATESECTIONDATA");
//			System.out.println("更新老数据-时程表==="+updateSectionParm);
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
			
			//更新老数据操作--时程价格表 add by lich 20141022
//			System.out.println("更新老数据-时程价格表==="+updateSectionParm);
//			System.out.println("hhhhhhhhhhhhhhhhhhhhhhhh");
			for (int i = 0; i < updateSectionParm.getCount("SEQ"); i++) {
				TParm usecparm = updateSectionParm.getRow(i);
//				System.out.println("、时程价格表usecparm==="+usecparm);
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
				
//				System.out.println("时程价格表updateSecSql="+updateSecSql);
				
				
//				String updateSecSql;
//				System.out.println("uparm="+usecparm.getValue("PRICE_TYPE"));
//				System.out.println("uparm.length==="+usecparm.getValue("PRICE_TYPE").length());
				if(usecparm.getValue("PRICE_TYPE").length()==0){
					updateSecSql = this.getSecPriceSqlBY(usecparm);
				}else{					
					updateSecSql = this.updateSectionPriceInfoSql(usecparm);
				}
//				System.out.println("MEM_PACKAGE_SECTION_PRICE操作"+updateSecSql);
//				System.out.println("更新老数据操作--时程价格表sql="+updateSecSql);
				result = new TParm(TJDODBTool.getInstance().update(updateSecSql, conn));
				//System.out.println("result::::"+result);
				if (result.getErrCode() < 0) {
					conn.rollback();
					conn.close();
					return result;
				}
			}
			
			
			 
			
			//新增时程信息
//			System.out.println("新增时程表===");
			TParm insertSectionParm = parm.getParm("INSERTSECTIONDATA");
//			System.out.println("action中insertSectionParm="+insertSectionParm);
//			System.out.println("iiiiiiiiiiiiiiiiiiiiiiiii");
			for (int i = 0; i < insertSectionParm.getCount("SEQ"); i++) {
				TParm insecparm=insertSectionParm.getRow(i);
				String insecsertsql=this.getSecSql(insecparm);
//				System.out.println("新增时程表===insecsertsql"+insecsertsql);
				result = new TParm(TJDODBTool.getInstance().update(insecsertsql, conn));
				if (result.getErrCode() < 0) {
					conn.rollback();
					conn.close();
					return result;
				}
			}
			//新增时程价格信息
//			System.out.println("新增时程价格表===");
//			System.out.println("jjjjjjjjjjjjjjjjjjjjjjjj");
			for (int i = 0; i < insertSectionParm.getCount("SEQ"); i++) {
				TParm insecparm=insertSectionParm.getRow(i);
				String insecsertsql=this.getSecPriceSql(insecparm);
//				System.out.println("新增时程价格表===insecparm"+insecsertsql);
				result = new TParm(TJDODBTool.getInstance().update(insecsertsql, conn));
				if (result.getErrCode() < 0) {
					conn.rollback();
					conn.close();
					return result;
				}
			}
			//修改套餐表老数据信息
//			System.out.println("修改套餐表数据---");
			TParm packageParm = parm.getParm("PACKAGEDATA");
//			System.out.println("修改套餐表数据packageParm==="+packageParm);
			String updataPacksql=this.getPackageSql(packageParm);
//			System.out.println("修改套餐表数据updataPacksql="+updataPacksql);
			result = new TParm(TJDODBTool.getInstance().update(updataPacksql, conn));
			if (result.getErrCode() < 0) {
				conn.rollback();
				conn.close();
				return result;
			}
			
			//修改套餐价格表老数据信息
			/**
			 * 注意：需要进行两步操作，首先进行查询，判断套餐价格表中对应数据是否存在，
			 * 如果存在，则执行更新操作
			 * 如果不存在，则执行插入操作
			 */
//			System.out.println("对套餐价格表操作---");
			TParm packagePriceParm = parm.getParm("PACKAGEDATA");
			String selectSql = this.getSelectSql(packagePriceParm);// 从套餐价格表中查询数据，通过价格类型和套餐代码
//			System.out.println("查询套餐是否存在sql="+selectSql);
			result = new TParm(TJDODBTool.getInstance().select(selectSql));
//			System.out.println("result="+result);
			/**
			 * 如果结果集result 数据为0 说明查询结果不存在，执行新增语句，否则执行修改
			 */
			if (result.getErrCode() < 0) {
				conn.rollback();
				conn.close();
				return result;
			}
			//如果结果集result 数据为0 说明查询结果不存在，执行新增语句
			if(result.getDouble("COUNT", 0) == 0){
				String insertPackPricesql = this.getInsertPackagePriceSql(packagePriceParm);
//				System.out.println("套餐价格表插入操作insertPackPricesql="+insertPackPricesql);
				result = new TParm(TJDODBTool.getInstance().update(insertPackPricesql,conn));
//				if (result.getErrCode() < 0) {
//					conn.rollback();
//					conn.close();
//					return result;
//				}
			//如果结果集result 数据大于0说明有数据存在，那么执行修改操作	
			}else if(result.getDouble("COUNT", 0) > 0){				
				String updataPackPricesql = this.getPackagePriceSql(packagePriceParm);
//				System.out.println("套餐价格表修改操作updataPackPricesql="+updataPackPricesql);
				result = new TParm(TJDODBTool.getInstance().update(updataPackPricesql,conn));
				if (result.getErrCode() < 0) {
					conn.rollback();
					conn.close();
					return result;
				}
			}
			
			
			
			
			/**
			//更新统计时程总价格到套餐主档表
			String packageCode = oparm.getValue("PACKAGE_CODE");
			if(!updateSectionPrice(packageCode)){
				conn.rollback();
				conn.close();
				result.setErrCode(-2);
				return result;
			} **/
			
			
//			// 保存病患病历记录信息表(MRO_RECORD)updateMRODiag
//			result = MROTool.getInstance().updateMRODiag(diagParm, conn);
//			if (result.getErrCode() < 0) {
//				conn.close();
//				return result;
//			}
//			// 回写最近诊断
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
//	 * 统计套餐价格更新时程表对应明细
//	 */
//	public TParm onUpdatePrice(TParm parm){
//		TConnection conn = getConnection();
//		TParm result = new TParm();
//		//统计套餐价格更新时程表对应明细
//		//1、查询统计套餐总价
//		TParm operSectionParm = parm.getParm("OPERSECTIONDATA");
//		TParm oparm=operSectionParm.getRow(0);
//		TParm allParm = new TParm(TJDODBTool.getInstance().select(this.getAllFeeD(oparm)));
////		System.out.println("allParm="+allParm);
//		//2、更新统计数据到时程表
//		result = new TParm(TJDODBTool.getInstance().update(
//				this.updateAllPriceSql(oparm, allParm.getValue("ALL_UNIT_PRICE", 0), 
//						allParm.getValue("ALL_RETAIL_PRICE", 0)), conn));
//		if (result.getErrCode() < 0) {
//			conn.rollback();
//			conn.close();
//			return result;
//		}
//		
//		//更新统计时程总价格到套餐主档表
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
	 * 判断是否可以删除时程表数据
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
	 * 删除时程表数据
	 * 和时程价格表
	 */
	public TParm onDelSection(TParm parm){
		TConnection conn = getConnection();
		TParm result = new TParm();
		TParm result1 = new TParm();//时程价格表
		TParm delParm = new TParm();
		boolean flag = false;
		try {
			String packageCode = parm.getValue("PACKAGE_CODE", 0);
			String sectionCode = parm.getValue("SECTION_CODE", 0);
			delParm.addData("PACKAGE_CODE", packageCode);
			delParm.addData("SECTION_CODE", sectionCode);
			//System.out.println("packageCode="+packageCode);
			//删除数据
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
	 * 查询D_PRICE
	 * @param parm
	 * @return
	 */
	
	private String getSelectDPriceSql(TParm parm){
//		System.out.println("方法中的parm="+parm);
		String sql = "SELECT COUNT(*) AS COUNT " +
					" FROM MEM_PACKAGE_SECTION_D_PRICE " +
					" WHERE PRICE_TYPE = '"+parm.getValue("PRICE_TYPE")+"' " +
					" AND SECTION_CODE = '"+parm.getValue("SECTION_CODE")+"'" +
					" AND ID = '"+ parm.getValue("ID")+"'"+
					" AND PACKAGE_CODE = '"+parm.getValue("PACKAGE_CODE")+"'";
//		System.out.println("方法中的SQL"+sql);
		return sql;
	}
	/**
	 * 查询Section_PRICE
	 * @param parm
	 * @return
	 */
	private String getSelectSPriceSql(TParm parm){
//		System.out.println("方法中的parm:::::::::::"+parm);
		String sql = "SELECT COUNT(*) AS COUNT " +
				" FROM MEM_PACKAGE_SECTION_PRICE " +
				" WHERE PRICE_TYPE = '"+parm.getValue("PRICE_TYPE")+"' " +
				" AND SECTION_CODE = '"+parm.getValue("SECTION_CODE")+"'" +
				" AND PACKAGE_CODE = '"+parm.getValue("PACKAGE_CODE")+"'";
//		System.out.println("方法中的 sql======"+sql);
		return sql;
	}
	
	
	
	
	/**
	 * 计算时程套餐总和并更新套餐主档数据
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
			//更新主档套餐总价格
			String allOriginalPrice = result.getValue("ALL_ORIGINAL_PRICE", 0);
			String allSectionPrice = result.getValue("ALL_SECTION_PRICE", 0);
			String updateSql = "UPDATE MEM_PACKAGE SET ORIGINAL_PRICE = '"+allOriginalPrice+"'," +
					" PACKAGE_PRICE = '"+allSectionPrice+"' WHERE PACKAGE_CODE = '"+packageCode+"' ";
			
			String updateSql1 = "UPDATE MEM_PACKAGE_PRICE SET " +//更新MEM_PACKAGE_PRICE
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
	 * 判断是否是集合医嘱
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
	 * 计算字典表中集合医嘱-细项总价格
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
	 * 获得字典表中集合医嘱细项数据
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
	 * 医嘱插入语句 
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
				"'"+parm.getValue("FREQ_CODE")+"','"+parm.getValue("UN_NUM_FLG")+"')";//====pangben 2015-9-2 添加不限量
//		System.out.println("新增数据MEM_PACKAGE_SECTION_D sql="+sql);
		return sql;
	}
	/**
	 * add by lich  20141022
	 * 医嘱价格表插入语句 
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
//		System.out.println("新增MEM_PACKAGE_SECTION_D_PRICE数据sql="+sql);
//		System.out.println("parm==="+parm);
		return sql;
	}
	
	/**
	 * add by lich  20141030
	 * 医嘱价格表插入语句 (备用语句)
	 * 当PRICE_TYPE字段为空时候进行插入操作，
	 * 此时PRICE_TPYE字段的值从PRICE_TYPE_BY里面获取
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
//		System.out.println("新增MEM_PACKAGE_SECTION_D_PRICE数据sql="+sql);
//		System.out.println("parm==="+parm);
		return sql;
	}
	
	
	
	
	/**
	 * 时程插入语句
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
	 * 时程价格插入语句
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
//		System.out.println("新增MEM_PACKAGE_SECTION_PRICE数据2sql="+sql);
		return sql;
	}
	
	/**
	 * 时程价格插入语句(备用语句)
	 * 当PRICE_TYPE字段为空时候进行插入操作，
	 * 此时PRICE_TPYE的值从PRICE_TYPE_BY里面获取
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
//		System.out.println("新增MEM_PACKAGE_SECTION_PRICE数据2sql="+sql);
		return sql;
	}
	
	
	
	
	/**
	 * 删除医嘱明细表
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
//		System.out.println("删除医嘱明细表sql="+sql);
		return sql;	
	}
	
	/**
	 * 删除医嘱明细表
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
		//System.out.println("删除医嘱明细表sql="+sql);
		return sql;	
	}
	
	
	
	
	/**
	 * 删除细项数据-先删除细项再删除主项
	 *  MEM_PACKAGE_SECTION_D
	 */
	public String delDetailSql(TParm parm){
		String sectionCode = parm.getValue("SECTION_CODE");
		String packageCode = parm.getValue("PACKAGE_CODE");
		String orderCode = parm.getValue("ORDER_CODE");
		
		String sql = " DELETE FROM MEM_PACKAGE_SECTION_D WHERE SECTION_CODE = '"+sectionCode+"' " +
		" AND PACKAGE_CODE = '"+packageCode+"' AND ORDERSET_CODE ='"+orderCode+"' AND HIDE_FLG = 'Y' ";
//		System.out.println("删除细表D项数据sql="+sql);
		return sql;	
	}
	
	/**
	 * 删除细项数据-先删除细项再删除主项
	 *  MEM_PACKAGE_SECTION_D_PRICE
	 *  add by lich 20141022
	 */
	public String delDetailPriceSql(TParm parm){
//		System.out.println("删除价格表细项数据parm="+parm);
		String sectionCode = parm.getValue("SECTION_CODE");
		String packageCode = parm.getValue("PACKAGE_CODE");
//		String priceType = parm.getValue("PRICE_TYPE");
		String id = parm.getValue("ID");
		String sql = " DELETE FROM MEM_PACKAGE_SECTION_D_PRICE WHERE SECTION_CODE = '"+sectionCode+"' " +
				" AND PACKAGE_CODE = '"+packageCode+"'" +
//				" AND PRICE_TYPE ='"+priceType+"'" +
				" AND ID = '"+id+"'";
//		System.out.println("删除价格表细项数据sql="+sql);
		return sql;	
	}
	
	
	
	/**
	 * 时程表删除判断
	 * MEM_PACKAGE_SECTION_D
	 */
	private String delSectionflgSql(TParm parm){
		String sectionCode = parm.getValue("SECTION_CODE", 0);
		String packageCode = parm.getValue("PACKAGE_CODE", 0);
		
		String sql = " SELECT ID FROM MEM_PACKAGE_SECTION_D WHERE PACKAGE_CODE='"+packageCode+"' " +
		" AND SECTION_CODE = '"+sectionCode+"' ";
		//System.out.println("时程表删除判断sql="+sql);
		return sql;	
	}
	/**
	 * 统计医嘱细表套餐总价 SYS_PACKAGE_SECTION_D
	 * SUM(UNIT_PRICE), SUM(RETAIL_PRICE)
	 */
	private String getAllFeeD(TParm parm){
		String sectionCode = parm.getValue("SECTION_CODE");
		String packageCode = parm.getValue("PACKAGE_CODE");
		String sql = "SELECT SUM(UNIT_PRICE*ORDER_NUM) AS ALL_UNIT_PRICE, SUM(RETAIL_PRICE*ORDER_NUM) AS ALL_RETAIL_PRICE " +
				" FROM MEM_PACKAGE_SECTION_D WHERE SECTION_CODE = '"+sectionCode+"' AND PACKAGE_CODE = '"+packageCode+"' " +
				" AND HIDE_FLG = 'N' ";
		//System.out.println("统计医嘱细表套餐总价sql="+sql);
		return sql;
	}
	/**
	 * 更新统计数据到时程表 SYS_PACKAGE_SECTION
	 */
	private String updateAllPriceSql(TParm parm, String originalPrice, String sectionPrice){
		String sectionCode = parm.getValue("SECTION_CODE");
		String packageCode = parm.getValue("PACKAGE_CODE");
		String sql = "UPDATE MEM_PACKAGE_SECTION SET ORIGINAL_PRICE = '"+originalPrice+"',SECTION_PRICE = '"+sectionPrice+"' " +
				" WHERE SECTION_CODE ='"+sectionCode+"' AND PACKAGE_CODE = '"+packageCode+"'";
		//System.out.println("更新统计数据到时程表sql="+sql);
		return sql;
		
	}
	/**
	 * 修改医嘱表老数据操作MEM_PACKAGE_SECTION_D
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
				" UN_NUM_FLG='"+parm.getValue("UN_NUM_FLG")+ "'" +//====pangben 2015-9-2 不限量
				" WHERE ID='"+parm.getValue("ID")+"' " +
				" AND SECTION_CODE='"+parm.getValue("SECTION_CODE")+ "' " +
				" AND PACKAGE_CODE='"+parm.getValue("PACKAGE_CODE")+ "' " ;
//		System.out.println("修改MEM_PACKAGE_SECTION_D操作sql="+sql);
		return sql;
	}
	
	/**
	 * 修改医嘱价格表老数据操作MEM_PACKAGE_SECTION_D_PRICE add by lich  20141022
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
//		System.out.println("修改MEM_PACKAGE_SECTION_D_PRICE操作sql="+sql);
//		System.out.println("RETAIL_PRICE ="+parm.getValue("RETAIL_PRICE"));
		
		return sql;
	}
	
	
	/**
	 * 修改医嘱时程表老数据操作MEM_PACKAGE_SECTION
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
//		System.out.println("修改医嘱时程表老数据操作MEM_PACKAGE_SECTION sql="+sql);
		
		return sql;
	}
	/**
	 * 修改时程价格表老数据操作MEM_PACKAGE_SECTION_PRICE
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
//		System.out.println("修改时程价格表老数据操作MEM_PACKAGE_SECTION_PRICE sql="+sql);
//		System.out.println("SECTION_PRICE ="+parm.getValue("SECTION_PRICE"));
		return sql;
	}
	
	/**
	 * 修改医套餐表老数据操作MEM_PACKAGE
	 * add by lich 20141022
	 */
	private String getPackageSql(TParm parm){
		String sql = "UPDATE  MEM_PACKAGE SET " +
		" ORIGINAL_PRICE='"+parm.getValue("OWN_PRICE",0)+"' ,"+
		//增加身份折扣关联和门急住类别--xiongwg20150630
		" CTZ_CODE='"+parm.getValue("CTZ_CODE",0)+"' ,"+
		" ADM_TYPE='"+parm.getValue("ADM_TYPE",0)+"' ,"+
		
		" PACKAGE_PRICE='"+parm.getValue("PACKAGE_PRICE",0)+"' "+
		" WHERE PACKAGE_CODE='"+parm.getValue("PACKAGE_CODE",0)+"' ";
//		System.out.println("修改MEM_PACKAGE_SECTION_PRICE操作sql="+sql);
		return sql;
	}
	
	/**
	 * 修改医套餐价格表老数据操作MEM_PACKAGE_PRICE
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
//		System.out.println("修改MEM_PACKAGE_SECTION_PRICE操作sql="+sql);
		return sql;
	}
	
	
	
	/**
	 * 删除时程sql
	 * MEM_PACKAGE_SECTION
	 */
	public String delSectionSql(TParm parm){
		String sql = "DELETE FROM MEM_PACKAGE_SECTION " +
				" WHERE PACKAGE_CODE = '"+parm.getValue("PACKAGE_CODE", 0)+"' " +
				" AND SECTION_CODE = '"+parm.getValue("SECTION_CODE", 0)+"'";
		
		//System.out.println("删除操作sql="+sql);
		return sql;
	}
	/**
	 * 删除时程价格sql
	 * MEM_PACKAGE_SECTION_PRICE
	 * add by lich 20141023
	 */
	public String delSectionPriceSql(TParm parm){
		String sql = "DELETE FROM MEM_PACKAGE_SECTION_PRICE " +
				" WHERE PACKAGE_CODE = '"+parm.getValue("PACKAGE_CODE", 0)+"' " +
				" AND SECTION_CODE = '"+parm.getValue("SECTION_CODE", 0)+"'";
		
		//System.out.println("删除操作sql="+sql);
		return sql;
	}
	
	
	/**
	 * 检查是否是集合医嘱sql
	 */
	public String checkIsDetailOrderSql(TParm parm){
		String sql = "SELECT ORDERSET_CODE, ORDER_CODE FROM SYS_ORDERSETDETAIL " +
				" WHERE ORDERSET_CODE = '"+parm.getValue("ORDER_CODE", 0)+"' AND HIDE_FLG = 'Y'";
		//System.out.println("判断是否是集合医嘱sql:"+sql);
		return sql;
	}
	
	/**
	 * 计算字典表中集合医嘱细项总价格sql
	 */
	public String sumPriceDeatailOrderSql1(TParm parm){
		String sql = "SELECT A.ORDER_CODE,B.ORDERSET_CODE,A.ORDER_DESC,  DOSAGE_QTY,OWN_PRICE, " +
				" OWN_PRICE * DOSAGE_QTY AS OWN_AMT FROM SYS_FEE A, SYS_ORDERSETDETAIL B " +
				" WHERE A.ORDER_CODE = B.ORDER_CODE and A.ACTIVE_FLG = 'Y' AND B.HIDE_FLG='Y' " +
				" AND B.ORDERSET_CODE = '"+parm.getValue("ORDER_CODE", 0)+"'";
		//System.out.println("计算字典表中集合医嘱细项总价格sql:"+sql);
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
	 * 从套餐价格表中查询数据，通过价格类型和套餐代码，
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
	 * 向套餐价格表中插入数据
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
	 * 套餐与已开立医嘱对比后，更新数据 ==huangtt 20141021
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
		TParm orderMemParm = packOrderParm.getParm("orderMemParm");//套餐内转套外
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
    		
    		TParm orderMDParm = new TParm(); //更新opd_order中套餐的数据
        	TParm packDParm = new TParm();  //更新MEM_PAT_PACKAGE_SECTION_D表数据
        	TParm packMParm = new TParm();  //更新MEM_PAT_PACKAGE_SECTION_M表数据
    		
    		for(int i=0;i<packOrderParm.getCount("ORDER_CODE");i++){
        		String sql = "SELECT ORDERSET_CODE, ORDERSET_GROUP_NO,SECTION_CODE,PACKAGE_CODE" +
        				" FROM MEM_PAT_PACKAGE_SECTION_D" +
        				" WHERE TRADE_NO = '"+packOrderParm.getValue("TRADE_NO", i)+"'" +
//        				" AND ORDER_CODE = '"+packOrderParm.getValue("ORDER_CODE", i)+"'" +
        				" AND ID = '"+packOrderParm.getValue("ID", i)+"' " ;
//        				" AND HIDE_FLG='N'";
        		TParm parm = new TParm(TJDODBTool.getInstance().select(sql)); //在套餐中得到集合医嘱的组号
        		sql = "SELECT ORDER_CODE, RETAIL_PRICE, ORDERSET_CODE,HIDE_FLG,ID,TRADE_NO,SETMAIN_FLG" +
        					" FROM MEM_PAT_PACKAGE_SECTION_D" +
        					" WHERE TRADE_NO = '"+packOrderParm.getValue("TRADE_NO", i)+"'" +
        					" AND ORDERSET_CODE = '"+parm.getValue("ORDERSET_CODE", 0)+"'" +
        					" AND PACKAGE_CODE = '"+parm.getValue("PACKAGE_CODE", 0)+"'" +
        					" AND SECTION_CODE = '"+parm.getValue("SECTION_CODE", 0)+"'" +
        					" AND ORDERSET_GROUP_NO = "+parm.getInt("ORDERSET_GROUP_NO", 0);   		
        		TParm parmD = new TParm(TJDODBTool.getInstance().select(sql)); //套餐中主细项的orderCode 和价钱
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
        			if( !orderParm.getBoolean("SETMAIN_FLG", 0) && orderParm.getValue("ORDERSET_CODE", 0).length() == 0){ //非集合医嘱
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
        				if(orderParmMD.getCount() == parmD.getCount()){  //开立医嘱和套餐的细项是否一样
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
        					result.setData("MESSAGE", packOrderParm.getValue("ORDER_CODE", i)+" 开立医嘱与套餐中细项数量不符");
        					return result; 
        				}
        			}
        			
        		}
        		

        	}
        	
        	//当在套餐中勾选执行时，更新套餐数据
    		for (int i = 0; i < execPack.getCount(); i++) {
    			String sql = "SELECT ORDERSET_CODE, ORDERSET_GROUP_NO,SECTION_CODE,PACKAGE_CODE"
    					+ " FROM MEM_PAT_PACKAGE_SECTION_D"
    					+ " WHERE TRADE_NO = '"+ execPack.getValue("TRADE_NO", i)+ "'"
    					+ " AND ORDER_CODE = '"+ execPack.getValue("ORDER_CODE", i)+ "'"
    					+ " AND ID = '"+ execPack.getValue("ID", i) + "' " 
    					+ " AND HIDE_FLG='N'";
    			TParm parm = new TParm(TJDODBTool.getInstance().select(sql)); // 在套餐中得到集合医嘱的组号
    			sql = "SELECT ORDER_CODE, RETAIL_PRICE, ORDERSET_CODE,HIDE_FLG,ID,TRADE_NO"
    					+ " FROM MEM_PAT_PACKAGE_SECTION_D"
    					+ " WHERE TRADE_NO = '"+ execPack.getValue("TRADE_NO", i)+ "'"
    					+ " AND ORDERSET_CODE = '"+ parm.getValue("ORDERSET_CODE", 0)+ "'"
    					+ " AND PACKAGE_CODE = '"+ parm.getValue("PACKAGE_CODE", 0)+ "'"
    					+ " AND SECTION_CODE = '"+ parm.getValue("SECTION_CODE", 0)+ "'"
    					+ " AND ORDERSET_GROUP_NO = "+ parm.getInt("ORDERSET_GROUP_NO", 0);
    			TParm parmD = new TParm(TJDODBTool.getInstance().select(sql)); // 套餐中主细项
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
	 * 项目主更对套餐进行替换  add by huangtt 20150713
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
		TParm patFineOrderParm = new TParm(); //客户用到的医嘱细项
		TParm packSection = new TParm(); //时程总价修改
//		for (int i = 0; i < tableParm.getCount("ID"); i++) {
//			if(tableParm.getBoolean("SETMAIN_FLG",i)){
//				
//				//删除MEM_PACKAGE_SECTION_D_PRICE表中细项的价钱
//				result = MEMTool.getInstance().delMemPackageSectionDPrice(tableParm.getRow(i), connection);
//				if (result.getErrCode() < 0) {
//					connection.rollback();
//					connection.close();
//					return result;
//				}
//				//先删除旧医嘱的细项 MEM_PACKAGE_SECTION_D
//				result = MEMTool.getInstance().delMemPackageSectionD(tableParm.getRow(i), connection);
//				if (result.getErrCode() < 0) {
//					connection.rollback();
//					connection.close();
//					return result;
//				}
//				
//				//得到细项中的原价，换出售价，插入MEM_PACKAGE_SECTION_D_PRICE表中
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
//					BigDecimal sumPrice =new BigDecimal(0); //售价总和
//					BigDecimal retailPrice = new BigDecimal(priceD.getDouble("RETAIL_PRICE", j));
//					double price =0;//最后一次计算的售价
//					for (int j2 = 0; j2 < fineOrderParm.getCount("ID"); j2++) {
//						priceOrderParm.addRowData(fineOrderParm, j2);
//						priceOrderParm.addData("PRICE_TYPE", priceD.getValue("PRICE_TYPE", j));
//						//计算售价
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
//					//细项售价之各与总项存在差导，在细项的最后一项进行补平
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
//			packSection = new TParm(); //时程总价修改
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
//			//更新MEM_PACKAGE_SECTION表中的ORIGINAL_PRICE字段
//			result =  MEMTool.getInstance().updateMemPackageSection(packSection, connection);
//			if (result.getErrCode() < 0) {
//				connection.rollback();
//				connection.close();
//				return result;
//			}
//			
//			//更新MEM_PACKAGE_SECTION_PRICE表中ORIGINAL_PRICE
//			result =  MEMTool.getInstance().updateMemPackageSectionPrice(packSection, connection);
//			if (result.getErrCode() < 0) {
//				connection.rollback();
//				connection.close();
//				return result;
//			}
//			
//			//插入历史表
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
//			//插入MEM_PACKAGE_SECTION_D_PRICE表(细项)
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
//				//添加新医嘱的细项
//				result = MEMTool.getInstance().insertMemPackageSectionD(newOrderParm.getRow(i), connection);
//				if (result.getErrCode() < 0) {
//					connection.rollback();
//					connection.close();
//					return result;
//				}
//			}else{
//				
//				//将新医嘱更到旧医嘱上
//				result = MEMTool.getInstance().updateMemPackageSectionD(newOrderParm.getRow(i), connection);
//				if (result.getErrCode() < 0) {
//					connection.rollback();
//					connection.close();
//					return result;
//				}
//				
//				
//				
//				//更新MEM_PACKAGE_SECTION_D_PRICE表中的折扣率与原价
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
		
		//已买时程中未使用过的医嘱进行更新
		for (int i = 0; i < tableParm.getCount("ID"); i++) {

			//如果为集合医嘱的话，先删除老医嘱的细项，在添加新医嘱的细项
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
				
				
				//先查询出卖出的，未使用的主项内容
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
				
				//删除老医嘱细项
				result = MEMTool.getInstance().delMemPatPackageSectionD(patSection.getRow(0), connection);
				if (result.getErrCode() < 0) {
					connection.rollback();
					connection.close();
					return result;
				}
				
				//添加细项
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
//					//删除老医嘱细项
//					result = MEMTool.getInstance().delMemPatPackageSectionD(patSection.getRow(j), connection);
//					if (result.getErrCode() < 0) {
//						connection.rollback();
//						connection.close();
//						return result;
//					}
//					
//					//添加细项
//					TParm patParm = patSection.getRow(j);
//					sql = "SELECT MAX(SEQ) SEQ FROM MEM_PAT_PACKAGE_SECTION_D " +
//					" WHERE PACKAGE_CODE = '"+patSection.getValue("PACKAGE_CODE", j)+"'" +
//					" AND SECTION_CODE = '"+patSection.getValue("SECTION_CODE", j)+"'" +
//					" AND TRADE_NO = '"+patSection.getValue("TRADE_NO", j)+"' ";
//					
//					BigDecimal sumPrice =new BigDecimal(0); //售价总和
//					BigDecimal retailPrice = new BigDecimal(patSection.getDouble("RETAIL_PRICE", j)); //主项的售价
//					double price =0;//最后一次计算的售价
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
//						//计算售价
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
//					//为了保证细项的销售价格总各等于主项的销售价格，最后一项直接用总项－前面几项之和
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
			
			
			//更新集合医嘱主项 或不是集合医嘱的医嘱
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
	 * 项目变更价钱的时候更新套餐中的价钱  add by huangtt 20150713
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
			 
			 //更新 MEM_PACKAGE_SECTION_D中的UNIT_PRICE价钱
			 result = MEMTool.getInstance().updateMemPackageSectionDUnitPrice(parm.getRow(i), connection);
				if (result.getErrCode() < 0) {
					connection.rollback();
					connection.close();
					return result;
				}
			 
		    //更新 MEM_PACKAGE_SECTION_D_PRICE中的UNIT_PRICE价钱
				result = MEMTool.getInstance().updateMemPackageSectionDPriceOriginal(parm.getRow(i), connection);
				if (result.getErrCode() < 0) {
					connection.rollback();
					connection.close();
					return result;
				}
				
			 
//			 //处置或药品 SETMAIN_FLG= N  HIDE_FLG=N
//			if(!parm.getBoolean("SETMAIN_FLG", i) && !parm.getBoolean("HIDE_FLG", i)){
//				//
//				
//			}
			
			//集合医嘱细项 SETMAIN_FLG= N  HIDE_FLG=Y
			if(!parm.getBoolean("SETMAIN_FLG", i) && parm.getBoolean("HIDE_FLG", i)){
				//查找主项的ID号
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
				
				//更改主项的原价
				//更新 MEM_PACKAGE_SECTION_D中的UNIT_PRICE价钱
				 result = MEMTool.getInstance().updateMemPackageSectionDUnitPrice(sectionParm, connection);
					if (result.getErrCode() < 0) {
						connection.rollback();
						connection.close();
						return result;
					}
				 
			    //更新 MEM_PACKAGE_SECTION_D_PRICE中的UNIT_PRICE价钱
					result = MEMTool.getInstance().updateMemPackageSectionDPriceOriginal(sectionParm, connection);
					if (result.getErrCode() < 0) {
						connection.rollback();
						connection.close();
						return result;
					}
					
			}
			
		    TParm packSection = new TParm(); //时程总价修改
			String priceSql = "SELECT ORIGINAL_PRICE FROM MEM_PACKAGE_SECTION " +
					"WHERE PACKAGE_CODE = '"+parm.getValue("PACKAGE_CODE", i)+"' " +
					" AND SECTION_CODE = '"+parm.getValue("SECTION_CODE", i)+"' ";
			TParm priceParm = new TParm(TJDODBTool.getInstance().select(priceSql));
			packSection.setData("PACKAGE_CODE", parm.getValue("PACKAGE_CODE", i));
			packSection.setData("SECTION_CODE", parm.getValue("SECTION_CODE", i));
			packSection.setData("VERSION_NUMBER", parm.getValue("VERSION_NUMBER", i));
			packSection.setData("OPT_TERM", parm.getValue("OPT_TERM", i));
			packSection.setData("OPT_USER", parm.getValue("OPT_USER", i));
			
			//如果当前时程与上个一个时程相同的话，就把之前的差钱加进来，如果不同就只加自己的
			if(!(sectionCode.equals(parm.getValue("SECTION_CODE", i)) && packageCode.equals(parm.getValue("PACKAGE_CODE", i)))){
				sumDiffPrice = 0;
			}
			
			double originalPrice = priceParm.getDouble("ORIGINAL_PRICE", 0)+parm.getDouble("DIFF_PRICE_SUM", i)+sumDiffPrice;
			packSection.setData("ORIGINAL_PRICE", originalPrice);

			//更新里程中的价钱 MEM_PACKAGE_SECTION
			result =  MEMTool.getInstance().updateMemPackageSection(packSection, connection);
			if (result.getErrCode() < 0) {
				connection.rollback();
				connection.close();
				return result;
			}
			
			//更新里程中的价钱 MEM_PACKAGE_SECTION_PRICE
			result =  MEMTool.getInstance().updateMemPackageSectionPriceOriginal(packSection, connection);
			if (result.getErrCode() < 0) {
				connection.rollback();
				connection.close();
				return result;
			}
			
           //记录本次的时程代码与差钱总和
			sumDiffPrice +=  parm.getDouble("DIFF_PRICE_SUM", i);
			sectionCode = parm.getValue("SECTION_CODE", i);
			packageCode = 	parm.getValue("PACKAGE_CODE", i);
		 }
		 
		//更新套餐中的比列MEM_PACKAGE_PRICE
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
	 * 套餐余额结转 插入收据  更新使用状态
	 * @param parm
	 * @return
	 */
	public TParm onPackBalanceTransfer(TParm parm){
		
		TConnection connection = getConnection();
		TParm result = new TParm();
		
		TParm bilRecpParm = parm.getParm("bilRecpParm");
		TParm selParm = parm.getParm("selParm");
		
		TParm bilInvricpt = new TParm();
		bilInvricpt.setData("RECP_TYPE", "MEM"); // 医疗卡打票
		bilInvricpt.setData("CANCEL_USER", new TNull(String.class));
		bilInvricpt.setData("CASHIER_CODE", bilRecpParm.getData("OPT_USER"));
		bilInvricpt.setData("OPT_USER", bilRecpParm.getData("OPT_USER"));
		bilInvricpt.setData("INV_NO", bilRecpParm.getData("PRINT_NO"));
		bilInvricpt.setData("OPT_TERM", bilRecpParm.getData("OPT_TERM"));
		bilInvricpt.setData("CANCEL_DATE", new TNull(Timestamp.class));
		bilInvricpt.setData("TOT_AMT", bilRecpParm.getDouble("TOT_AMT"));

		bilInvricpt.setData("AR_AMT", bilRecpParm.getDouble("AR_AMT"));
		// bilInvricpt.setData("INS_AMT", insAmt);//医保扣款金额
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
     * 保存门诊日结
     * @param parm TParm
     * @return TParm
     */
    public TParm onSaveAcctionMEM(TParm parm) {
        TParm result=new TParm();
        if(parm==null)
            return result.newErrParm(-1,"参数为空");
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
