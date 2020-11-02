package jdo.bms.ws;
import java.sql.Timestamp;
import jdo.odi.OdiMainTool;
import jdo.opd.OrderTool;
import jdo.bil.BIL;
import jdo.ibs.IBSTool;
import jdo.sys.DeptTool;
import jdo.sys.SYSChargeHospCodeTool;
import jdo.sys.SYSFeeTool;
import jdo.sys.SystemTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.util.TypeTool;

/**
 * 
 * @author shibl
 * 
 */
public class BmsFeeTool extends TJDOTool {
	/**
	 * 实例
	 */
	public static BmsFeeTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return JDO
	 */
	public static BmsFeeTool getInstance() {
		if (instanceObject == null)
			instanceObject = new BmsFeeTool();
		return instanceObject;
	}
	/**
	 * 构造器
	 */
	public BmsFeeTool() {
        setModuleName("bms\\BMSApplyMModule.x");
        onInit();
		
	}
	/**
	 * 取得处方号
	 * 
	 * @return String
	 */
	public String getRxNo() {
		return SystemTool.getInstance().getNo("ALL", "ODO", "RX_NO", "RX_NO");
	}
	/**
	 * 得到最大医嘱组号
	 * @return
	 */
    public int getMaxOrderSetGroupNo(String  caseNo)throws Exception{
    	String sql="SELECT MAX(ORDERSET_GROUP_NO) NO FROM OPD_ORDER WHERE CASE_NO='"+caseNo+"'";
    	TParm Reparm=new TParm(TJDODBTool.getInstance().select(sql));
    	int max=1;
    	if(Reparm.getCount()<=0){
    		return max;
    	}
    	max=Reparm.getInt("NO", 0)+1;
    	return max;
    }

	/**
	 * 住院血库
	 * 
	 * @param parm
	 * @return
	 */
	public TParm getBmsIBSOrder(TParm parm)throws Exception{
		// 调用IBS接口返回费用数据
		TParm forIBSParm = new TParm();
		TParm result = new TParm();
		String sql = "SELECT A.CASE_NO,A.MR_NO,A.IPD_NO,B.BED_NO,B.DEPT_CODE,"
				+ " A.DR_CODE,B.CTZ1_CODE,B.CTZ2_CODE,B.CTZ3_CODE,B.STATION_CODE"
				+ " FROM BMS_APPLYM A,ADM_INP B WHERE A.CASE_NO=B.CASE_NO AND APPLY_NO='"
				+ parm.getValue("ApplyNo") + "'";
		TParm bmsApply = new TParm(TJDODBTool.getInstance().select(sql));
		if (bmsApply.getCount() <= 0) {
			result.setErr(-1, "未查询到申请单");
			return result;
		}
		forIBSParm.setData("CTZ1_CODE", bmsApply.getData("CTZ1_CODE", 0));
		forIBSParm.setData("CTZ2_CODE", bmsApply.getData("CTZ2_CODE", 0));
		forIBSParm.setData("CTZ3_CODE", bmsApply.getData("CTZ3_CODE", 0));
		forIBSParm.setData("FLG", "ADD");
		TParm inParm = new TParm();
		TParm BilFee = parm.getParm("orderParm");
		for (int i = 0; i < BilFee.getCount(); i++) {
			inParm.addData("CASE_NO", bmsApply.getData("CASE_NO", 0));// 申请单CASE_NO
			inParm.addData("MR_NO", bmsApply.getData("MR_NO", 0));// 申请单MR_NO
			inParm.addData("IPD_NO", bmsApply.getData("IPD_NO", 0));// 申请单IPD_NO
			inParm.addData("BED_NO", bmsApply.getData("BED_NO", 0));// adm所在床位
			inParm.addData("DEPT_CODE", bmsApply.getData("DEPT_CODE", 0));// adm所在科室
			inParm.addData("STATION_CODE", bmsApply.getData("STATION_CODE", 0));// adm所在病区
			inParm.addData("ORDER_DR_CODE", bmsApply.getData("DR_CODE", 0));// 申请单DR_NO
			inParm.addData("ORDER_DEPT_CODE", bmsApply.getData("DEPT_CODE", 0));// adm所在科室
			inParm.addData("TAKE_DAYS", "1");// 天数默认为1
			inParm.addData("OPT_TERM", "127.0.0.1");// IP默认为127.0.0.1
			inParm.addData("OPT_USER", parm.getValue("UserId"));// xml
			String orderCode = BilFee.getValue("OrderCode", i);// xml
			TParm feeParm = SYSFeeTool.getInstance().getFeeAllData(orderCode);
			if (feeParm.getErrCode() < 0) {
				result.setErr(-1, "查询计费医嘱有误");
				return result;
			}
			if (feeParm.getCount() <= 0) {
				result.setErr(-1, "未查询到计费医嘱");
				return result;
			}
			inParm.addData("EXEC_DEPT_CODE", feeParm.getValue("EXEC_DEPT_CODE",0));// sys_fee执行科室
			inParm.addData("CAT1_TYPE", feeParm.getValue("CAT1_TYPE", 0));// sys_fee
			inParm.addData("ORDER_CAT1_CODE", feeParm.getValue("ORDER_CAT1_CODE", 0));// sys_fee
			inParm.addData("DOSAGE_UNIT", feeParm.getValue("UNIT_CODE", 0));// sys_fee
			inParm.addData("MEDI_UNIT", feeParm.getValue("UNIT_CODE", 0));// sys_fee
			inParm.addData("ORDER_CODE", orderCode);// xml
			inParm.addData("DOSAGE_QTY", BilFee.getData("OrderQty", i));// xml
			inParm.addData("MEDI_QTY", BilFee.getData("OrderQty", i));// xml
		}
		forIBSParm.setData("M", inParm.getData());
		return forIBSParm;
	}

	/**
	 * 血库计费
	 * 
	 * @return
	 */
	public TParm onBmsIFee(TParm parm)throws Exception{
		TParm result = new TParm();
		TParm inparm=getBmsIBSOrder(parm);
		if(inparm.getErrCode()<0){
			result.setErr(-1, inparm.getErrText());
			return result;
		}
		TParm resultFromIBS = IBSTool.getInstance().getIBSOrderData(inparm);
		if (resultFromIBS.getErrCode() < 0) {
			System.out.println(resultFromIBS.getErrText());
			result.setErr(-1, "计费医嘱组成失败");
			return resultFromIBS;
		}
		if (resultFromIBS.getCount("CASE_NO") <= 0) {
			result.setErr(-1, "计费医嘱组成失败");
			return result;
		}
		TConnection conn = this.getConnection();
		// 保存后台使用的数据
		TParm forIBSParm2 = new TParm();
		forIBSParm2.setData("DATA_TYPE", "6"); // 新增webservice医技调用标记
		forIBSParm2.setData("M", resultFromIBS.getData());
		forIBSParm2.setData("FLG", inparm.getValue("FLG"));
		// 调用IBS提供的Tool继续执行
		result = IBSTool.getInstance().insertIBSOrder(forIBSParm2, conn);
		if (result.getErrCode() < 0) {
			result.setErr(-1, "住院计费医嘱插入计费档失败");
			conn.rollback();
			conn.close();
			return result;
		}
		conn.commit();
		conn.close();
		return result;
	}

	/**
	 * 门诊血库计费
	 * @return
	 */
	public TParm onBmsOEFee(TParm inparm)throws Exception{
		TParm Reparm = new TParm();
		TParm resultFromIBS =this.getOpdOrderData(inparm);
		if (resultFromIBS.getCount() <= 0) {
			Reparm.setErr(-1, "计费医嘱组成失败");
			return Reparm;
		}
		TConnection conn = this.getConnection();
		TParm result = OrderTool.getInstance().onInsertForOPB(resultFromIBS, conn);
		if (result.getErrCode() < 0) {
			result.setErr(-1, "门诊计费医嘱插入计费档失败");
			conn.rollback();
			conn.close();
			return result;
		}
		conn.commit();
		conn.close();
		return Reparm;
	}
	/**
	 * 组成计费医嘱
	 * @param inparm
	 * @return
	 */
	public TParm getOpdOrderData(TParm parm)throws Exception{
		TParm result = new TParm();
		String sql = "SELECT A.CASE_NO,A.MR_NO,B.REGION_CODE,A.DEPT_CODE,"
				+ " A.DR_CODE,B.CTZ1_CODE,B.CTZ2_CODE,B.CTZ3_CODE,B.ADM_TYPE,B.SERVICE_LEVEL,"
				+ " B.CONTRACT_CODE "
				+ " FROM BMS_APPLYM A,REG_PATADM B WHERE A.CASE_NO=B.CASE_NO AND APPLY_NO='"
				+ parm.getValue("ApplyNo") + "'";
		TParm bmsApply = new TParm(TJDODBTool.getInstance().select(sql));
		if (bmsApply.getCount() <= 0) {
			result.setErr(-1, "未查询到申请单");
			return result;
		}
		Timestamp  now=SystemTool.getInstance().getDate();
		String RxNo = this.getRxNo();
		int seq = 1;
		TParm inParm = new TParm();
		String ctz1Code = bmsApply.getValue("CTZ1_CODE", 0) == null ? ""
				: bmsApply.getValue("CTZ1_CODE", 0);
		String ctz2Code = bmsApply.getValue("CTZ2_CODE", 0) == null ? ""
				: bmsApply.getValue("CTZ2_CODE", 0);
		String ctz3Code = bmsApply.getValue("CTZ3_CODE", 0) == null ? ""
				: bmsApply.getValue("CTZ3_CODE", 0);
		String level = bmsApply.getValue("SERVICE_LEVEL", 0) == null ? ""
				: bmsApply.getValue("SERVICE_LEVEL", 0);
		TParm BilFee = parm.getParm("orderParm");
		for (int i = 0; i < BilFee.getCount(); i++) {
			inParm.addData("CASE_NO", bmsApply.getData("CASE_NO", 0));// 申请单CASE_NO
			inParm.addData("RX_NO", RxNo);
			inParm.addData("SEQ_NO", seq);
			inParm.addData("PRESRT_NO", "1");
			inParm.addData("REGION_CODE", bmsApply.getData("REGION_CODE", 0));
			inParm.addData("MR_NO", bmsApply.getData("MR_NO", 0));
			inParm.addData("ADM_TYPE", bmsApply.getData("ADM_TYPE", 0));
			inParm.addData("RX_TYPE", "7");
			inParm.addData("TEMPORARY_FLG", "N");
			inParm.addData("RELEASE_FLG", "N");
			inParm.addData("LINKMAIN_FLG", "N");
			inParm.addData("LINK_NO", "");
			inParm.addData("DR_NOTE", "");
			inParm.addData("NS_NOTE", "");	
			inParm.addData("DC_DR_CODE", "");
			inParm.addData("DC_DEPT_CODE", "");
			inParm.addData("DC_ORDER_DATE", "");
			inParm.addData("DCTAGENT_CODE", "");
			inParm.addData("DCTEXCEP_CODE", "");
			inParm.addData("DCT_TAKE_QTY", "");
			inParm.addData("PACKAGE_TOT", "");
			inParm.addData("AGENCY_ORG_CODE", "");
			inParm.addData("DCTAGENT_FLG", "");
			inParm.addData("DECOCT_CODE", "");
			inParm.addData("FREQ_CODE", "");
			inParm.addData("ROUTE_CODE", "");
			String orderCode = BilFee.getValue("OrderCode", i);// xml
			TParm feeParm = SYSFeeTool.getInstance().getFeeAllData(orderCode);
			if (feeParm.getErrCode() < 0) {
				result.setErr(-1, "查询计费医嘱有误");
				return result;
			}
			if (feeParm.getCount() <= 0) {
				result.setErr(-1, "未查询到计费医嘱");
				return result;
			}
			inParm.addData("ORDER_CODE", orderCode);// xml
			inParm.addData("ORDER_DESC", feeParm.getValue("ORDER_DESC", 0));// sys_fee
			inParm.addData("GOODS_DESC", feeParm.getValue("GOODS_DESC", 0));// sys_fee
			inParm.addData("SPECIFICATION", feeParm.getValue("SPECIFICATION", 0));// sys_fee
			inParm.addData("GIVEBOX_FLG", "N");// sys_fee
			inParm.addData("CAT1_TYPE", feeParm.getValue("CAT1_TYPE", 0));// sys_fee
			inParm.addData("EXEC_DEPT_CODE", feeParm.getValue("EXEC_DEPT_CODE",0));// sys_fee执行科室
			inParm.addData("EXEC_DR_CODE", parm.getValue("UserId"));
			inParm.addData("COST_CENTER_CODE", this.getCostCenter(feeParm.getValue("EXEC_DEPT_CODE",0)));
			inParm.addData("ORDER_CAT1_CODE", feeParm.getValue("ORDER_CAT1_CODE", 0));// sys_fee
			inParm.addData("DOSAGE_UNIT", feeParm.getValue("UNIT_CODE", 0));// sys_fee
			inParm.addData("MEDI_UNIT", feeParm.getValue("UNIT_CODE", 0));// sys_fee
			inParm.addData("DISPENSE_UNIT", feeParm.getValue("UNIT_CODE", 0));// sys_fee
			inParm.addData("DOSAGE_QTY", BilFee.getData("OrderQty", i));// xml
			inParm.addData("MEDI_QTY", BilFee.getData("OrderQty", i));// xml
			inParm.addData("DISPENSE_QTY", BilFee.getData("OrderQty", i));// xml
			double ownPrice = 0.0;
			double nhiPrice = 0.0;
			String chargeHospCode = "";
			String chargeCode;
			if ("2".equals(level)) {
				ownPrice = feeParm.getDouble("OWN_PRICE2", 0);
			} else if ("3".equals(level)) {
				ownPrice = feeParm.getDouble("OWN_PRICE3", 0);
			} else
				ownPrice = feeParm.getDouble("OWN_PRICE", 0);
			nhiPrice = feeParm.getDouble("NHI_PRICE", 0);
			chargeHospCode = feeParm.getValue("CHARGE_HOSP_CODE", 0);
			TParm inChargeParm = new TParm();
			inChargeParm.setData("CHARGE_HOSP_CODE", chargeHospCode);
			TParm chargeParm = SYSChargeHospCodeTool.getInstance()
					.selectChargeCode(inChargeParm);
			chargeCode = chargeParm.getValue("OPD_CHARGE_CODE", 0);
			double dosageQty = TypeTool.getDouble(BilFee.getData("OrderQty", i));
			double ownRate = BIL.getRate(ctz1Code, ctz2Code, ctz3Code,orderCode, level);
			if (ownRate < 0) {
				 inParm.setErr(-1, "自付比例错误");
				 return inParm;
			}
			double ownAmt = ownPrice * dosageQty;
			double totAmt = ownAmt * ownRate;
			inParm.addData("CTZ1_CODE", ctz1Code);
			inParm.addData("CTZ2_CODE", ctz2Code);
			inParm.addData("CTZ3_CODE", ctz3Code);
			inParm.addData("HEXP_CODE", chargeHospCode);
			inParm.addData("BILL_FLG", "N");
			inParm.addData("BILL_TYPE", "C");//默认为C现金
			inParm.addData("MED_APPLY_NO", "");
			inParm.addData("EXEC_FLG", "Y");
			inParm.addData("REXP_CODE", chargeCode);
			inParm.addData("OWN_PRICE", ownPrice);
			inParm.addData("NHI_PRICE", nhiPrice);
			inParm.addData("OWN_AMT", ownAmt);
			inParm.addData("AR_AMT",  totAmt);
			inParm.addData("DISCOUNT_RATE", ownRate);
			inParm.addData("DR_CODE", bmsApply.getData("DR_CODE", 0));// 申请单DR_NO
			inParm.addData("DEPT_CODE", bmsApply.getData("DEPT_CODE", 0));// adm所在科室
			inParm.addData("ORDER_DATE", now);
			inParm.addData("SETMAIN_FLG", feeParm.getValue("ORDERSET_FLG", 0));
			inParm.addData("INDV_FLG", feeParm.getValue("INDV_FLG", 0));
			inParm.addData("HIDE_FLG", feeParm.getData("HIDE_FLG", 0)==null?"N":feeParm.getData("HIDE_FLG", 0));
			if(feeParm.getValue("ORDERSET_FLG", 0).equals("Y")){
				inParm.addData("ORDERSET_GROUP_NO",this.getMaxOrderSetGroupNo(bmsApply.getValue("CASE_NO", 0)));
				inParm.addData("ORDERSET_CODE", orderCode);
		
			}else{
				inParm.addData("ORDERSET_GROUP_NO", "");
				inParm.addData("ORDERSET_CODE", "");
			}
			inParm.addData("RPTTYPE_CODE", feeParm.getValue("RPTTYPE_CODE", 0));
			inParm.addData("OPTITEM_CODE", feeParm.getValue("OPTITEM_CODE", 0));
			inParm.addData("DEV_CODE", feeParm.getValue("DEV_CODE", 0));
			inParm.addData("MR_CODE", feeParm.getValue("MR_CODE", 0));
			inParm.addData("FILE_NO", "");
			inParm.addData("DEGREE_CODE", feeParm.getValue("DEGREE_CODE", 0));
			inParm.addData("URGENT_FLG", "N");
			inParm.addData("INSPAY_TYPE",feeParm.getValue("INSPAY_TYPE", 0));
			TParm Phaparm=new TParm();
			if(feeParm.getValue("CAT1_TYPE", 0).equals("PHA")){
				TParm action = new TParm();
				action.setData("ORDER_CODE", orderCode);
				Phaparm = OdiMainTool.getInstance().queryPhaBase(action);
				if(Phaparm.getCount()<=0){
					 inParm.setErr(-1, "未查找药品信息");
					 return inParm;
				}
			}
			// 药品类型
			if (feeParm.getValue("CAT1_TYPE", 0).equals("PHA")) {
				inParm.addData("PHA_TYPE", Phaparm.getData("PHA_TYPE",0));
			} else {
				inParm.addData("PHA_TYPE", "");
			}
			// 剂型类别
			if (feeParm.getValue("CAT1_TYPE", 0).equals("PHA")) {
				inParm.addData("DOSE_TYPE", Phaparm.getData("DOSE_CODE",0));
			} else {
				inParm.addData("DOSE_TYPE", "");
			}
			if (feeParm.getValue("CAT1_TYPE", 0).equals("PHA")) {
				inParm.addData("CTRLDRUGCLASS_CODE", Phaparm.getData("CTRLDRUGCLASS_CODE",0));
			} else {
				inParm.addData("CTRLDRUGCLASS_CODE", "");
			}
			inParm.addData("PRESCRIPT_NO", "0");
			inParm.addData("EXPENSIVE_FLG", "N");
			inParm.addData("PRINTTYPEFLG_INFANT", "N");
			inParm.addData("ATC_FLG", "N");
			inParm.addData("SENDATC_DATE","");
			inParm.addData("RECEIPT_NO","");
			inParm.addData("BILL_DATE","");
			inParm.addData("BILL_USER","");
			inParm.addData("PRINT_FLG","");
			inParm.addData("CONTRACT_CODE",bmsApply.getValue("CONTRACT_CODE", 0));
			inParm.addData("TAKE_DAYS", "1");// 天数默认为1
			inParm.addData("OPT_TERM", "127.0.0.1");// IP默认为127.0.0.1
			inParm.addData("OPT_USER", parm.getValue("UserId"));// xml
			seq++;
		}
		inParm.setCount(inParm.getCount("CASE_NO"));
		return inParm;
	}
	/**
	 * 取得成本中心
	 * 
	 * @param dept_code
	 *            String
	 * @return String
	 */
	public String getCostCenter(String dept_code){
		return DeptTool.getInstance().getCostCenter(dept_code, "");
	}
}
