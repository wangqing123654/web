package com.javahis.ui.opb;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import jdo.ekt.EKTIO;
import jdo.hl7.Hl7Communications;
import jdo.ins.INSMZConfirmTool;
import jdo.ins.INSOpdTJTool;
import jdo.ins.INSRunTool;
import jdo.ins.INSTJReg;
import jdo.mem.MEMTool;
import jdo.opb.OPB;
import jdo.opb.OPBReceiptList;
import jdo.opb.OPBReceiptTool;
import jdo.opb.OPBTool;
import jdo.opb.OPBFeeListPrintTool;
import jdo.reg.REGCcbReTool;
import jdo.reg.Reg;
import jdo.sys.IReportTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;
import jdo.util.Manager;

import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTextFormat;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.StringUtil;
import com.tiis.util.TiMath;

/**
 * <p>
 * Title: 门诊费用明细查询
 * </p>
 * 
 * <p>
 * Description: 门诊费用明细查询
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: javahis
 * </p>
 * 
 * @author fudw 2009-07-08
 * @version 1.0
 */
public class OPBBackReceiptControl extends TControl {
	/**
	 * 票据档
	 */
	TTable tableM;
	/**
	 * 医嘱档
	 */
	TTable tableD;
	/**
	 * 计价对象
	 */
	OPB opb;
	/**
	 * table数据英文
	 */
	TParm tableMParmEng;
	/**
	 * table数据
	 */
	TParm tableMParm;
	/**
	 * 印刷号
	 */
	String printNoOnly;
	/**
	 * 退票权限
	 */
	boolean backBill = false;
	private String printNo;// 票号
	private Timestamp bill_date;// 收费日期
	// 批号
	private TParm batchNoParm;

	private TParm ektParm;// 医疗卡操作
	private TParm parmEKT;// 医疗卡读卡数据
	private String nhiRegionCode;// 医保区域号码
	// =====zhangp 20120229
	private int insType = 0;
	TParm sendHL7Parm = new TParm();
	// ====zhangp 20120229
	private boolean cashFLg = false;// 现金退票
	private TParm regionParm;
	private String type = "";// 操作类型 1：现金 2：医疗卡 3:医保卡
	private boolean insFlg = false;// 是否操作医保
	// private double totAmt = 0.00;// 总金额
	private String caseNo;
	private String receipt_No;
	TParm datas;
	/**
	 * 初始化
	 */
	public void onInit() {
		super.onInit();
		tableM = (TTable) this.getComponent("TABLEM");
		tableD = (TTable) this.getComponent("TABLED");
		// 处理数据
		dealData();
		batchNoParm = new TParm(TJDODBTool.getInstance().select(
				"SELECT ORDER_CODE, BATCH_NO FROM IND_STOCK"));
		regionParm = SYSRegionTool.getInstance().selectdata(
				Operator.getRegion()); // 获得医保区域代码
	}

	/**
	 * 处理数据
	 */
	public void dealData() {
		// 初始化权限
		if (!initPopedem())
			return;
		// //把数据放入界面
		// if (!getReceiptList())
		// return;
		// 得到收费列表
		getReceiptParm();
	}

	/**
	 * 接受前台传来数据和初始化权限
	 * 
	 * @return boolean
	 */
	public boolean initPopedem() {
		TParm parm;
		// 前台传来的计价对象
		if (this.getParameter() != null) {
			parm = (TParm) this.getParameter();
			// 加载opb
			if (!initOpb(parm))
				return false;
		}
		// 退票权限
		if (!getPopedem("BACKBILL")) {
			backBill = false;
		}
		return true;
	}

	/**
	 * 加载opb
	 * 
	 * @param parm
	 *            TParm
	 * @return boolean
	 */
	public boolean initOpb(TParm parm) {
		caseNo = parm.getValue("CASE_NO");
		String mrNo = parm.getValue("MR_NO");
		Pat pat = Pat.onQueryByMrNo(mrNo);
		if (pat == null) {
			this.messageBox("查无此病案号");
			return false;
		}
		// 界面赋值
		setValueForParm("MR_NO;PAT_NAME;IDNO;SEX_CODE;COMPANY_DESC", pat
				.getParm());
		Reg reg = Reg.onQueryByCaseNo(pat, caseNo);
		// 判断挂号信息
		if (reg == null)
			return false;
		// 三身份
		callFunction("UI|CTZ1_CODE|setValue", reg.getCtz1Code());
		callFunction("UI|CTZ2_CODE|setValue", reg.getCtz2Code());
		callFunction("UI|CTZ3_CODE|setValue", reg.getCtz3Code());
		// 通过reg和caseNo得到pat
		opb = OPB.onQueryByCaseNo(reg);
		// 给界面上部分地方赋值
		if (opb == null) {
			this.messageBox("此病人尚未就诊!");
			return false;
		}
		return true;
	}

	/**
	 * 初始化收费列表
	 * 
	 * @return boolean
	 */
	public boolean getReceiptList() {
		OPBReceiptList opbReceiptList = new OPBReceiptList()
				.initReceiptList(opb.getReg().caseNo());
		if (opbReceiptList == null)
			return false;
		opb.setReceiptList(opbReceiptList);
		return true;
	}

	/**
	 * 得到收费界面上收费列表
	 */
	public void getReceiptParm() {
//		TParm parm = OPBReceiptTool.getInstance().getReceipt(
//				opb.getReg().caseNo());
		String sql = MSQL.replace("#", opb.getReg().caseNo());
//		System.out.println(sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		// opb.getReceiptList().getParm(opb.getReceiptList().PRIMARY);
		for(int i = 0; i < parm.getCount("RECEIPT_NO"); i++){
			parm.setData("PAY_MEDICAL_CARD", i, parm.getDouble("PAY_MEDICAL_CARD", i)+parm.getDouble("PAY_TYPE11", i));
			String remarkSql = "SELECT REMARK02,REMARK01,REMARK03,REMARK04,REMARK05,REMARK06,REMARK07,REMARK08,REMARK09,REMARK10,REMARK11 FROM BIL_OPB_RECP " +
					" WHERE CASE_NO = '"+opb.getReg().caseNo()+"' AND RECEIPT_NO = '"+parm.getValue("RECEIPT_NO",i)+"'";
			TParm sqlParm = new TParm(TJDODBTool.getInstance().select(remarkSql));
			if(!"".equals(sqlParm.getValue("REMARK02",0))&&!"#".equals(sqlParm.getValue("REMARK02",0))){
				String [] str=sqlParm.getValue("REMARK02",0).split("#");
				//String [] str1=str[0].split(";");
				try {
					parm.setData("REMARK", i, str[1]);
				} catch (Exception e) {
					System.out.println(e);
					// TODO: handle exception
				}
				
			}
		}
	
		tableM.setParmValue(parm);
		// opb.getReceiptList().initOrder(opb.getPrescriptionList());
	}

	/**
	 * 主表点击事件
	 */
	public void onClickTableM() {
		tableMParm = new TParm();
		tableMParmEng = new TParm();
		cashFLg = false;
		type = "";
		insFlg = false;
		int row = tableM.getSelectedRow();
		TParm tableParm = tableM.getParmValue();
		printNo = tableParm.getValue("PRINT_NO", row);// 票号
		bill_date = tableParm.getTimestamp("BILL_DATE", row);// 收费日期
		// System.out.println("主表数据"+tableParm);
		// //拿到一张票据
		// OPBReceipt opbReceipt = (OPBReceipt) opb.getReceiptList().get(row);
		// //得到其中的parm
		// TParm parm = opbReceipt.getOrderList().getParm(OrderList.PRIMARY);
		TParm orderParm = new TParm();
		TParm parm = null;
		TParm parmEng = null;
		
		// String recpType=tableParm.getValue("RECP_TYPE",row);
		// 医疗卡医嘱显示
		TParm tempParm = new TParm();
		tempParm.setData("PRINT_NO", printNo);
		receipt_No = (String) tableParm.getData("RECEIPT_NO", row);
		ektParm = OPBReceiptTool.getInstance().seletEktState(tempParm);// 查询此次就诊是否是医疗卡操作
		// System.out.println("ektParm::"+ektParm);
		caseNo=(String) tableParm.getData("CASE_NO", row);
		orderParm.setData("RECEIPT_NO", tableParm.getData("RECEIPT_NO", row));
		orderParm.setData("CASE_NO", tableParm.getData("CASE_NO", row));
		//=====zhangp 20120925 start
		//=====zhangp 20121217 start
//		if(tableParm.getDouble("PAY_INS_CARD", row) == 0){
//			parm = OrderTool.getInstance().queryFill(orderParm);
//		}else{
			parm = getInsRuleParm(orderParm);
			parmEng = getInsRuleParmEng(orderParm);
//		}
		//=====zhangp 20121217 end
		//=====zhangp 20120925 end
		// System.out.println("明细表数据"+parm);
		tableMParm = parm;
		tableMParmEng = parmEng;
		tableD.setParmValue(parm);
		// // 校验是否已到检或已发药
		// int count = parm.getCount("EXEC_FLG");
		// String exeFlg = "";
		// for (int i = 0; i < count; i++) {
		// exeFlg = parm.getValue("EXEC_FLG", i);
		// if ("Y".equals(exeFlg)) {
		// returnFeeFlg = true;
		// return;
		// }
		// }
	}

	/**
	 * 退票入口
	 * 
	 * @return boolean
	 */
	public boolean onSave() {
		int row = tableM.getSelectedRow();
		if (row < 0) {
			messageBox("请选择要退票的票据!");
		}
		switch (this.messageBox("提示信息", "是否退票", this.YES_NO_OPTION)) {
		case 0:
			if (!backReceipt(row))
				return false;
			break;
		case 1:
			return true;
		}
		return true;
	}

	/**
	 * 退票方法
	 * 
	 * @param row
	 *            int
	 * @return boolean
	 */
	public boolean backReceipt(int row) {
		// if (returnFeeFlg) {
		// this.messageBox("已发药或已到检，不可退票!");
		// return false;
		// }
		// if (!opb.onSaveBackReceipt(row)) {
		getDatas();
		if (!onSaveBackReceipt(row)) {
			// 保存失败
			// messageBox("E0001");
			return false;
		}
		// 保存成功
		messageBox("P0001");
		if (cashFLg) {
			sendHL7Mes(sendHL7Parm);
		}
		updateBillHighFlg();
		// 保存后重新初始化票据
		afterSave();
		return true;
	}
	
	public void getDatas(){
		datas = new TParm(TJDODBTool.getInstance().select("SELECT CASE_NO,RX_NO,SEQ_NO,BIL_HIGH_FINANCE_FLG" +
				" FROM OPD_ORDER WHERE RECEIPT_NO = '"+this.receipt_No+"' "));
		System.out.println("SELECT CASE_NO,RX_NO,SEQ_NO,BIL_HIGH_FINANCE_FLG" +
				" FROM OPD_ORDER WHERE RECEIPT_NO = '"+this.receipt_No+"' ");
	}
	/**
	 *退费的时候将高值耗材标记置空
	 * @param row
	 */
	public void updateBillHighFlg(){
		//System.out.println(":::::"+tableM.getDataStore().getVectorRow(row, "RECEIPT_NO"));
		//TParm data = tableM.getParmValue().getRow(row);
		boolean isDebug = true;
		try {
			for (int i = 0; i < datas.getCount("SEQ_NO"); i++) {
				if (datas.getValue("BIL_HIGH_FINANCE_FLG", i).equals("Y")) {
					TParm result = new TParm(TJDODBTool.getInstance().update(
							"UPDATE OPD_ORDER SET BIL_HIGH_FINANCE_FLG='' "
									+ " WHERE CASE_NO='"
									+ datas.getData("CASE_NO", i)
									+ "' AND RX_NO='"
									+ datas.getData("RX_NO", i) + "' AND "
									+ " SEQ_NO='" + datas.getData("SEQ_NO", i)
									+ "' "));
				}
			}
		} catch (Exception e) {
			if(isDebug){
				System.out.println("come in class: OPBBackReceiptControl ，method ：updateBillHighFlg");
				e.printStackTrace();
			}
		}
	}
	/**
	 * 保存后重新初始化票据
	 */
	public void afterSave() {
		// //把数据放入界面
		// if (!getReceiptList())
		// return;
		// 得到收费列表
		dealData();
		getReceiptParm();
		tableD.removeRowAll();
		// ====zhangp 20120229
		insType = 0;
		// =====zhangp 20120229
	}

	// /**
	// * 打印费用清单
	// */
	// public void onFill() {
	// if (opb == null)
	// return;
	// System.out.println("tableMParm" + tableMParm);
	// int count = tableMParm.getCount("SETMAIN_FLG");
	// String setmainFlg = "";
	// for (int i = count - 1; i >= 0; i--) {
	// setmainFlg = tableMParm.getValue("SETMAIN_FLG", i);
	// if ("Y".equals(setmainFlg)) {
	// tableMParm.removeRow(i);
	// }
	// }
	// TParm parm = opb.getReceiptList().dealTParm(tableMParm);
	// if (parm == null)
	// return;
	// TParm pringListParm = new TParm();
	// pringListParm.setData("TABLEORDER", parm.getData());
	// //病案号
	// pringListParm.setData("MR_NO", opb.getPat().getMrNo());
	// //病患姓名
	// pringListParm.setData("PAT_NAME", opb.getPat().getName());
	// //就诊序号
	// pringListParm.setData("CASE_NO", opb.getReg().caseNo());
	// String sql =
	// " SELECT CHN_DESC FROM SYS_DICTIONARY " +
	// "  WHERE GROUP_ID ='SYS_SEX' " +
	// "    AND ID = '" + opb.getPat().getSexCode() + "'";
	// TParm sexParm = new TParm(TJDODBTool.getInstance().select(sql));
	// //性别
	// pringListParm.setData("SEX_CODE", sexParm.getValue("CHN_DESC", 0));
	// //就诊日期
	// pringListParm.setData("ADM_DATE", opb.getReg().getAdmDate());
	// //医院名称
	// pringListParm.setData("HOSP",Operator.getHospitalCHNFullName()+"门诊费用清单");
	// this.openPrintDialog("%ROOT%\\config\\prt\\opb\\OPBOrderList.jhw",
	// pringListParm);
	// }
	/**
	 * 打印费用清单 ============泰心医院费用清单打印
	 */
	public void onFill() {
		if(this.messageBox("提示信息", "是否打印费用清单", 2) != 0)//===pangben 2013-9-2 添加提示
			return;
		DecimalFormat df = new DecimalFormat("##########0.00");
		if (opb == null)
			return;
		
		
		
		boolean engFlag = this.getValueBoolean("ENG_FLAG");//ENG_FLAG为英文单选按钮
//		TCheckBox engFlag = (TCheckBox)getComponent("ENG_FLAG");
		 String sql4=" SELECT m.UNIT_ENG_DESC,m.CONTRACTOR_DESC,n.INSURANCE_NUMBER,n.INSURANCE_BILL_NUMBER " +
  		"FROM MEM_CONTRACTOR m,  MEM_INSURE_INFO n WHERE n.MR_NO='"+opb.getPat().getMrNo()+
  		"' AND m.CONTRACTOR_CODE=n.CONTRACTOR_CODE  AND n.VALID_FLG='Y'";
			//result结果只包含：保险公司，和保险号
		 TParm resultIns = new TParm(TJDODBTool.getInstance().select(sql4));
		/**
		 * modify by lich
		 * 如果选择英文打印则走if
		 * 否则走else
		 */
		if(engFlag){
			int count = tableMParmEng.getCount("SETMAIN_FLG");
			
			String setmainFlg = "";
			for (int i = count - 1; i >= 0; i--) {
				setmainFlg = tableMParmEng.getValue("SETMAIN_FLG", i);// 集合医嘱注记
				if ("Y".equals(setmainFlg)) {
					tableMParmEng.removeRow(i);
				}
			}
			TParm parm = opb.getReceiptList().dealTParmEng(tableMParmEng, batchNoParm);

			if (parm == null)
				return;
			double sumTotS = parm.getDouble("SUMTOTS");// 合计应收金额
			double sumTotR = parm.getDouble("SUMTOTR");// 合计实收金额
//			this.messageBox(""+sumTotR);
			TParm pringListParm = new TParm();
			pringListParm.setData("TABLE", parm.getData());
			pringListParm.setData("MR_NO", "TEXT", opb.getPat().getMrNo());//病案号
			
			pringListParm.setData("InsCom","TEXT", resultIns.getValue("CONTRACTOR_DESC",0));//保险公司名称
			pringListParm.setData("InsNum","TEXT", resultIns.getValue("INSURANCE_NUMBER",0));//保险号
			
			pringListParm.setData("PAT_NAME", "TEXT", opb.getPat().getName());// 患者姓名
			pringListParm.setData("HOSP", "TEXT", Operator.getHospitalCHNFullName());// 医院名称
//			pringListParm.setData("TITLE", "TEXT", Operator.getHospitalCHNFullName());// 医院名称
			pringListParm.setData("DATE", "TEXT", StringTool.getString(TypeTool.getTimestamp(bill_date), "yyyy/MM/dd HH:mm:ss"));// 收费日期
			pringListParm.setData("PRINT_NO", "TEXT", ""+printNo);// 票号
			String yMd = StringTool.getString(TypeTool.getTimestamp(TJDODBTool.getInstance().getDBTime()), "yyyy/MM/dd"); // 年月日
			pringListParm.setData("NOW", "TEXT", yMd);// 日期
			pringListParm.setData("TOT_AMT1","TEXT" ,new java.text.DecimalFormat("###,##0.00").format(sumTotR));//总金额
			pringListParm.setData("Birthday","TEXT" ,opb.getPat().getBirthday().toString().replaceAll("-", "/").substring(0, 10));
			pringListParm.setData("OP_NAME", "TEXT", Operator.getID());//制表人
			
			/**add by sunqy 20140630 门诊费用清单以及门诊套餐费用清单*/
//			TParm receptParm = (TParm)this.getParameter();
//			String memPackageFlg = receptParm.getValue("MEM_PACKAGE_FLG");//由OPBChargeM处套餐CHECK_BOX处事件得到
//			System.out.println("################memPackageFlg"+memPackageFlg);   
			
			String memPackageFlg = tableM.getParmValue().getValue("MEM_PACK_FLG", tableM.getSelectedRow());
			
					
					//add by lich 增加英文医嘱字段TRADE_ENG_DESC  PACKAGE_CODE 20141010
			String packageSql = "SELECT DISTINCT A.SEQ_NO, D.PACKAGE_DESC, D.PACKAGE_CODE, D.TRADE_ENG_DESC, A.AR_AMT AS RETAIL_PRICE," +
					" C.ORDER_DESC, D.ORDER_NUM, B.UNIT_CHN_DESC, B.UNIT_ENG_DESC, D.TRADE_ENG_DESC, A.SETMAIN_FLG " +
					" FROM OPD_ORDER A, SYS_UNIT B, MEM_PACKAGE_SECTION_D C,MEM_PAT_PACKAGE_SECTION_D D " +
					" WHERE A.CASE_NO = '"+ caseNo +"' AND A.RECEIPT_NO IS NOT NULL AND D.ID = A.MEM_PACKAGE_ID " +
					" AND A.MEDI_UNIT = B.UNIT_CODE AND " +
					//" A.ORDERSET_CODE = C.ORDERSET_CODE AND " +
					" A.ORDER_CODE = C.ORDER_CODE  ORDER BY D.PACKAGE_CODE, A.SEQ_NO";
			//System.out.println("SQL--------"+packageSql);
			TParm packResult = new TParm(TJDODBTool.getInstance().select(packageSql));
			packResult.setData("engFlag", engFlag);////是否是英文打印判断
//			System.out.println("RESULT------"+packResult);
			if("N".equals(memPackageFlg)||"".equals(memPackageFlg)||memPackageFlg==null){//无套餐情况
				pringListParm.setData("TABLEORDER", parm.getData());
				if (opb.getReg().getAdmType().equals("O")) {
					pringListParm.setData("TITLE", "TEXT", "门诊费用结算清单");
				}else{
					pringListParm.setData("TITLE", "TEXT", "急诊费用结算清单");
				}
				pringListParm.setData("SUMTOTS", df.format(StringTool.round(sumTotS,2)));// 合计应收金额
				pringListParm.setData("SUMTOTR", df.format(StringTool.round(sumTotR,2)));// 合计实收金额
				pringListParm.setData("TOT_AMT", df.format(StringTool.round(sumTotS,2)));// 合计应收金额
				pringListParm.setData("OWN_AMT", df.format(StringTool.round(sumTotR,2)));// 合计实收金额
				
				//20141223 wangjingchun add start 854
				String patinfo_sql = "SELECT VALID_FLG, DEPT_FLG FROM MEM_INSURE_INFO WHERE MR_NO = '"+opb.getPat().getMrNo()+"'";
				TParm patinfo_parm = new TParm(TJDODBTool.getInstance().select(patinfo_sql));
				int i = 1;
				if(patinfo_parm.getValue("DEPT_FLG",0).equals("N")&&patinfo_parm.getValue("VALID_FLG",0).equals("Y")){
					i=2;
				}
				for(int j=0;j<i;j++){
					this.openPrintDialog(IReportTool.getInstance().getReportPath("OPDFeeListEnV45.jhw"),
							IReportTool.getInstance().getReportParm("OPDFeeListV45.class", pringListParm));//报表合并  modify by sunqy 20140620
				}
				//20141223 wangjingchun add end 854
				
			}else if("Y".equals(memPackageFlg)){//有套餐情况
//				System.out.println("!!!--------enter package situation-------!!!");
				String mainFlg = "";// 集合医嘱注记
				double sumPackage = 0.00;//套餐价格
				for (int i = packResult.getCount() - 1; i >= 0; i--) {
					mainFlg = packResult.getValue("SETMAIN_FLG", i);
					if ("Y".equals(mainFlg)) {//如果是集合医嘱就去掉集合医嘱项，只显示细项
						packResult.removeRow(i);
					}
					sumPackage += packResult.getDouble("RETAIL_PRICE", i);
				}
				TParm packageParm = OPBPackageListControl.getInstance().listParm(packResult);
//				System.out.println("packageParm-+++++++"+packageParm);
				/*------------ 增加报表英文信息 add by lich----20141009----start*/
				packageParm.setData("engFlag", engFlag);//是否是英文打印判断
				packageParm.setData("InsCom", resultIns.getValue("CONTRACTOR_DESC",0));//保险公司名称
				packageParm.setData("InsNum", resultIns.getValue("INSURANCE_NUMBER",0));//保险号
				packageParm.setData("TOT_AMT1",new java.text.DecimalFormat("###,##0.00").format(sumTotR));//总金额
				packageParm.setData("Birthday" ,opb.getPat().getBirthday().toString().replaceAll("-", "/").substring(0, 10));
				packageParm.setData("TOT_AMT", df.format(StringTool.round(sumTotS,2)));// 合计应收金额
				packageParm.setData("OWN_AMT", df.format(StringTool.round(sumTotR,2)));// 合计实收金额
				
				/*------------ 增加报表英文信息 add by lich----20141009----end*/		
				packageParm.setData("MR_NO", opb.getPat().getMrNo());//病案号
				packageParm.setData("PAT_NAME", opb.getPat().getName());// 患者姓名
				
				packageParm.setData("PRINT_NO", printNo);// 票号
				packageParm.setData("BILL_DATE", StringTool.getString(TypeTool.getTimestamp(bill_date), "yyyy/MM/dd HH:mm:ss"));// 收费日期
				packageParm.setData("DATE", yMd);// 日期
				packageParm.setData("OP_NAME", Operator.getID());//制表人
				packageParm.setData("TOT_AMT1",new java.text.DecimalFormat("###,##0.00").format(sumTotR));//总金额
				packageParm.setData("Birthday" ,opb.getPat().getBirthday().toString().replaceAll("-", "/").substring(0, 10));
				packageParm.setData("RECEIVE_PARM", parm);
//				System.out.println("sumPackage===--#!@#$!%$!#"+sumPackage);
				packageParm.setData("SUM_PACKAGE", sumPackage);
				packageParm.setData("CLEAR_FLG", "Y");
				
//				packageParm.setData("TOT_AMT", df.format(StringTool.round(sumTotS,2)));// 合计应收金额
//				packageParm.setData("OWN_AMT", df.format(StringTool.round(sumTotR,2)));// 合计实收金额
//				this.openDialog("%ROOT%\\config\\opb\\OPBPackageChoose.x", packageParm);
				//add by huangtt 20150824 start
				TParm printParm = new TParm();
				TParm p = new TParm();//用于置空不勾选的表格
				p.setData("Visible", false);
				printParm.setData("TABLEPACKAGE", packageParm.getData());//套餐表格
				printParm.setData("TABLEORDER", p.getData());//门诊费用表格
				printParm.setData("OTHERPAY", "TEXT", "");//其他消费
				printParm.setData("TABLESUM", p.getData());//最终总计
				printParm.setData("HOSP","TEXT",packageParm.getData("HOSP"));//医院名称
				printParm.setData("MR_NO", "TEXT", packageParm.getData("MR_NO"));//病案号
				printParm.setData("PAT_NAME", "TEXT", packageParm.getData("PAT_NAME"));//姓名
				printParm.setData("InsCom","TEXT", packageParm.getData("InsCom"));//保险公司名称
				printParm.setData("InsNum","TEXT", packageParm.getData("InsNum"));//保险号
				printParm.setData("PRINT_NO", "TEXT", packageParm.getData("PRINT_NO"));//收据号
				printParm.setData("DATE", "TEXT", packageParm.getData("BILL_DATE"));//收费日期
				printParm.setData("NOW", "TEXT", packageParm.getData("DATE"));//打印日期
				printParm.setData("OP_NAME", "TEXT", packageParm.getData("OP_NAME"));//经办人
				printParm.setData("TOT_AMT1","TEXT" ,packageParm.getData("TOT_AMT1"));//总金额
				printParm.setData("Birthday","TEXT" ,packageParm.getData("Birthday"));//生日
				printParm.setData("TOT_AMT",df.format(StringTool.round(sumTotS,2)));//合计应收
				printParm.setData("OWN_AMT", df.format(StringTool.round(sumTotR,2)));//合计实收
				printParm.setData("SUM_", df.format(sumPackage));
				//20141223 wangjingchun add start 854
				String patinfo_sql = "SELECT VALID_FLG, DEPT_FLG FROM MEM_INSURE_INFO WHERE MR_NO = '"+packageParm.getData("MR_NO")+"'";
				TParm patinfo_parm = new TParm(TJDODBTool.getInstance().select(patinfo_sql));
				int i = 1;
				if(patinfo_parm.getValue("DEPT_FLG",0).equals("N")&&patinfo_parm.getValue("VALID_FLG",0).equals("Y")){
					i=2;
				}
				for(int j=0;j<i;j++){
					this.openPrintDialog(IReportTool.getInstance().getReportPath("OPDFeeListPackEnV45.jhw"),
							IReportTool.getInstance().getReportParm("OPDFeeListV45.class", printParm));//报表合并  modify by sunqy 20140630
				}
				//add by huangtt 20150824 end
			}
		/**
		 * 中文打印
		 */
		}else{
			// System.out.println("tableMParm" + tableMParm);
			// ORDER_DESC;MEDI_QTY;MEDI_UNIT;FREQ_CODE;TAKE_DAYS;
			// DOSAGE_QTY;DOSAGE_UNIT;OWN_PRICE;AR_AMT
			int count = tableMParm.getCount("SETMAIN_FLG");
			String setmainFlg = "";
//			for (int i = count - 1; i >= 0; i--) {
//				setmainFlg = tableMParm.getValue("SETMAIN_FLG", i);// 集合医嘱注记
//				if ("Y".equals(setmainFlg)) {
//					tableMParm.removeRow(i);
//				}
//			}
			//add by lich 显示集合医嘱主项，细项不显示 --------strat 20150302
//			for(int i = 0;i < count;i++){
//	            TParm temp = tableMParm.getRow(i);
//	            if("Y".equals(temp.getValue("SETMAIN_FLG"))){
//	                double price = 0;
//	                double ownPrice = 0;
//	                double onePrice = 0;
//	                String rxNo = temp.getValue("RX_NO");
//	                String orderSetGroupNo = temp.getValue("ORDERSET_GROUP_NO");
//	                for(int j = 0;j < count;j++){
//	                    TParm itemTemp = tableMParm.getRow(j);
//	                    if((rxNo + orderSetGroupNo).equals(itemTemp.getValue("RX_NO")+itemTemp.getValue("ORDERSET_GROUP_NO"))){
////	                        System.out.println(rxNo+orderSetGroupNo+"===="+(itemTemp.getValue("RX_NO")+itemTemp.getValue("ORDERSET_GROUP_NO")));
//	                        price += itemTemp.getDouble("AR_AMT");
//	                        ownPrice += itemTemp.getDouble("OWN_AMT");
//	                        onePrice += (itemTemp.getDouble("OWN_PRICE") * itemTemp.getDouble("DOSAGE_QTY"));
////	                        System.out.println("j = "+ j +"  price = = "+price);
//	                    }
//	                }
//	                tableMParm.setData("AR_AMT",i,price);
//	                tableMParm.setData("OWN_AMT",i,ownPrice);
//	                tableMParm.setData("OWN_PRICE", i,onePrice);
//	            }
//	        } 
//			for(int k = count - 1;k >= 0;k--){
//	            if(tableMParm.getBoolean("HIDE_FLG",k)){
//	            	tableMParm.removeRow(k);
//	            }
//	        }
			//add by lich 显示集合医嘱主项，细项不显示 --------end 20150302
			
			//modify by huangtt 20160118 显示集合医嘱细项－－－start
			for (int i = count-1; i >= 0; i--) {
				if(tableMParm.getBoolean("SETMAIN_FLG", i)){
					tableMParm.removeRow(i);
				}
			}
			
			//modify by huangtt 20160118 显示集合医嘱细项－－－end
			
			// System.out.println("batchNoParm:::"+batchNoParm);
			TParm parm = opb.getReceiptList().dealTParm(tableMParm, batchNoParm);
//			System.out.println("parmCN==========="+parm);
			if (parm == null)
				return;
			double sumTotS = parm.getDouble("SUMTOTS");// 合计应收金额
			double sumTotR = parm.getDouble("SUMTOTR");// 合计实收金额
			TParm pringListParm = new TParm();
			
		
			pringListParm.setData("TABLEORDER", parm.getData());
			pringListParm.setData("MR_NO", "TEXT", opb.getPat().getMrNo());//病案号
			pringListParm.setData("PAT_NAME", "TEXT", opb.getPat().getName());// 患者姓名
			pringListParm.setData("HOSP", "TEXT", Operator.getHospitalCHNFullName());// 医院名称
//			pringListParm.setData("TITLE", "TEXT", Operator.getHospitalCHNFullName());// 医院名称
			pringListParm.setData("BILL_DATE", "TEXT", StringTool.getString(TypeTool.getTimestamp(bill_date), "yyyy/MM/dd HH:mm:ss"));// 收费日期
			pringListParm.setData("PRINT_NO", "TEXT", "收据号："+printNo);// 票号
			String yMd = StringTool.getString(TypeTool.getTimestamp(TJDODBTool.getInstance().getDBTime()), "yyyy/MM/dd"); // 年月日
			pringListParm.setData("DATE", "TEXT", yMd);// 日期
			pringListParm.setData("OP_NAME", "TEXT", Operator.getID());//制表人
			// System.out.println("parm==============" + parm);
//			this.openPrintDialog("%ROOT%\\config\\prt\\opb\\OPBOrderPrint.jhw",
//					pringListParm);
//		      this.openPrintDialog(IReportTool.getInstance().getReportPath("OPBOrderPrint.jhw"),
//		                           IReportTool.getInstance().getReportParm("OPBOrderPrint.class", pringListParm));//报表合并modify by wanglong 20130730
			/**add by sunqy 20140630 门诊费用清单以及门诊套餐费用清单*/
//			TParm receptParm = (TParm)this.getParameter();
//			String memPackageFlg = receptParm.getValue("MEM_PACKAGE_FLG");//由OPBChargeM处套餐CHECK_BOX处事件得到
			
			String memPackageFlg = tableM.getParmValue().getValue("MEM_PACK_FLG", tableM.getSelectedRow());

			
			//add by lich 增加英文医嘱字段TRADE_ENG_DESC  PACKAGE_CODE 20141010
			String packageSql = "SELECT DISTINCT A.SEQ_NO,D.PACKAGE_DESC,D.PACKAGE_CODE, D.TRADE_ENG_DESC, A.AR_AMT AS RETAIL_PRICE," +
					" C.ORDER_DESC, D.ORDER_NUM, B.UNIT_CHN_DESC, B.UNIT_ENG_DESC, D.TRADE_ENG_DESC, A.SETMAIN_FLG  " +
					" FROM OPD_ORDER A, SYS_UNIT B, MEM_PACKAGE_SECTION_D C,MEM_PAT_PACKAGE_SECTION_D D " +
					" WHERE A.CASE_NO = '"+ caseNo +"' AND A.RECEIPT_NO IS NOT NULL  AND D.ID = A.MEM_PACKAGE_ID " +
					" AND A.MEDI_UNIT = B.UNIT_CODE AND " +
					//" A.ORDERSET_CODE = C.ORDERSET_CODE AND " +
					" A.ORDER_CODE = C.ORDER_CODE  ORDER BY D.PACKAGE_CODE, A.SEQ_NO";
			//System.out.println("SQL--------"+packageSql);
			TParm packResult = new TParm(TJDODBTool.getInstance().select(packageSql));
			packResult.setData("engFlag", engFlag);////是否是英文打印判断
			if("N".equals(memPackageFlg)||memPackageFlg==null){//无套餐情况
				pringListParm.setData("TABLEORDER", parm.getData());
				if (opb.getReg().getAdmType().equals("O")) {
					pringListParm.setData("TITLE", "TEXT", "门诊费用结算清单");
				}else{
					pringListParm.setData("TITLE", "TEXT", "急诊费用结算清单");
				}
				pringListParm.setData("SUMTOTS", df.format(StringTool.round(sumTotS,2)));// 合计应收金额
				pringListParm.setData("SUMTOTR", df.format(StringTool.round(sumTotR,2)));// 合计实收金额
				pringListParm.setData("TOT_AMT", df.format(StringTool.round(sumTotS,2)));// 合计应收金额
				pringListParm.setData("OWN_AMT", df.format(StringTool.round(sumTotR,2)));// 合计实收金额
				
				//20141223 wangjingchun add start 854
				String patinfo_sql = "SELECT VALID_FLG, DEPT_FLG FROM MEM_INSURE_INFO WHERE MR_NO = '"+opb.getPat().getMrNo()+"'";
				TParm patinfo_parm = new TParm(TJDODBTool.getInstance().select(patinfo_sql));
				int i1 = 1;
				if(patinfo_parm.getValue("DEPT_FLG",0).equals("N")&&patinfo_parm.getValue("VALID_FLG",0).equals("Y")){
					i1=2;
				}
				for(int j=0;j<i1;j++){
					this.openPrintDialog(IReportTool.getInstance().getReportPath("OPDFeeListV45.jhw"),
							IReportTool.getInstance().getReportParm("OPDFeeListV45.class", pringListParm));//报表合并  modify by sunqy 20140620
				}
				//20141223 wangjingchun add end 854
				
			}else if("Y".equals(memPackageFlg)){//有套餐情况
				String mainFlg = "";// 集合医嘱注记
				double sumPackage = 0.00;//套餐价格
				for (int i = packResult.getCount() - 1; i >= 0; i--) {
					mainFlg = packResult.getValue("SETMAIN_FLG", i);
					if ("Y".equals(mainFlg)) {//如果是集合医嘱就去掉集合医嘱项，只显示细项
						packResult.removeRow(i);
					}
					sumPackage += packResult.getDouble("RETAIL_PRICE", i);
				}
				TParm packageParm = OPBPackageListControl.getInstance().listParm(packResult);
				/*------------ 增加报表英文信息 add by lich----20141009----start*/
				packageParm.setData("engFlag", engFlag);//是否是英文打印判断
				packageParm.setData("InsCom", resultIns.getValue("CONTRACTOR_DESC",0));//保险公司名称
				packageParm.setData("InsNum", resultIns.getValue("INSURANCE_NUMBER",0));//保险号
				packageParm.setData("TOT_AMT1",new java.text.DecimalFormat("###,##0.00").format(sumTotR));//总金额
				packageParm.setData("Birthday" ,opb.getPat().getBirthday().toString().replaceAll("-", "/").substring(0, 10));
				packageParm.setData("TOT_AMT", df.format(StringTool.round(sumTotS,2)));// 合计应收金额
				packageParm.setData("OWN_AMT", df.format(StringTool.round(sumTotR,2)));// 合计实收金额
				
				/*------------ 增加报表英文信息 add by lich----20141009----end*/		
				packageParm.setData("HOSP", Operator.getHospitalCHNFullName());//医院名称
				packageParm.setData("MR_NO", opb.getPat().getMrNo());//病案号
				packageParm.setData("PAT_NAME", opb.getPat().getName());// 患者姓名
				packageParm.setData("PRINT_NO", "收据号："+printNo);// 票号
				packageParm.setData("BILL_DATE", StringTool.getString(TypeTool.getTimestamp(bill_date), "yyyy/MM/dd HH:mm:ss"));// 收费日期
				packageParm.setData("DATE", yMd);// 日期
				packageParm.setData("OP_NAME", Operator.getID());//制表人
				packageParm.setData("RECEIVE_PARM", parm);
//				System.out.println("sumPackage===--#!@#$!%$!#"+sumPackage);
				packageParm.setData("SUM_PACKAGE", sumPackage);
				packageParm.setData("CLEAR_FLG", "Y");
				
				
//				this.openDialog("%ROOT%\\config\\opb\\OPBPackageChoose.x", packageParm);
				
				//add by huangtt 20150824 start
				TParm printParm = new TParm();
				TParm p = new TParm();//用于置空不勾选的表格
				if("Y".equals(packageParm.getValue("CLEAR_FLG"))){
					printParm.setData("TITLE", "TEXT", "门诊套餐费用结算清单");
				}else{
					printParm.setData("TITLE", "TEXT", "门诊套餐费用明细清单");
				}
				p.setData("Visible", false);
				printParm.setData("TABLEPACKAGE", packageParm.getData());//套餐表格
				printParm.setData("TABLEORDER", p.getData());//门诊费用表格
				printParm.setData("OTHERPAY", "TEXT", "");//其他消费
				printParm.setData("TABLESUM", p.getData());//最终总计
				printParm.setData("MR_NO", "TEXT", packageParm.getData("MR_NO"));//病案号
				printParm.setData("PAT_NAME", "TEXT", packageParm.getData("PAT_NAME"));//姓名
				printParm.setData("PRINT_NO", "TEXT", packageParm.getData("PRINT_NO"));//收据号
				printParm.setData("BILL_DATE", "TEXT", packageParm.getData("BILL_DATE"));//收费日期
				printParm.setData("DATE", "TEXT", packageParm.getData("DATE"));//打印日期
				printParm.setData("OP_NAME", "TEXT", packageParm.getData("OP_NAME"));//经办人
				printParm.setData("SUM_", df.format(sumPackage));
				String patinfo_sql = "SELECT VALID_FLG, DEPT_FLG FROM MEM_INSURE_INFO WHERE MR_NO = '"+packageParm.getData("MR_NO")+"'";
				TParm patinfo_parm = new TParm(TJDODBTool.getInstance().select(patinfo_sql));
				int i1 = 1;
				if(patinfo_parm.getValue("DEPT_FLG",0).equals("N")&&patinfo_parm.getValue("VALID_FLG",0).equals("Y")){
					i1=2;
				}
				for(int j=0;j<i1;j++){
					this.openPrintDialog(IReportTool.getInstance().getReportPath("OPDFeeListPackV45.jhw"),
							IReportTool.getInstance().getReportParm("OPDFeeListV45.class", printParm));//报表合并  modify by sunqy 20140630
				}
				
				
				//add by huangtt 20150824 end
			}
		}
	}

	/**
	 * 补打票据
	 */
	public void onPrint() {
		int row = tableM.getSelectedRow();
		if (row < 0) {
			this.messageBox("请选择要打印的数据");
			return;
		}
		if (opb.getBilInvoice().getUpdateNo().compareTo(
				opb.getBilInvoice().getEndInvno()) > 0) {
			this.messageBox("票据已用完!");
			return;
		}
		if(this.messageBox("提示", "是否执行补印操作", 2) != 0){//===pangben 2013-9-2 添加提示
			return ;
		}
		if (!onSaveRePrint(row)) {
			messageBox("补印失败!");
			return;
		}
		messageBox("保存成功");
		// //拿到一张票据
		// OPBReceipt opbReceipt = (OPBReceipt) opb.getReceiptList().get(row);
		// if (opbReceipt == null)
		// return;
		TParm saveParm = tableM.getParmValue();
		TParm actionParm = saveParm.getRow(row);
//		String receiptNo = actionParm.getValue("RECEIPT_NO"); RECEIPT_NO 重号问题   shibl modify 20140527  
		TParm recpParm = null;
		// 门诊收据档数据:医疗卡收费打票\现金收费打票
		recpParm = OPBReceiptTool.getInstance().getOneReceipt(actionParm);
		if (ektParm.getCount("CASE_NO") > 0) {
			onPrint(recpParm, true);
		} else {
			onPrint(recpParm, false);
		}
		// 保存后重新初始化票据
		afterSave();
	}

	/**
	 * 打印票据封装 泰心门诊收费打票
	 * 
	 * @param recpParm
	 *            TParm ===================pangben 20111014
	 */
	// private void onPrint(TParm recpParm, boolean isEKT) {
	// DecimalFormat df = new DecimalFormat("0.00");
	// TParm oneReceiptParm = new TParm();
	// // 票据信息
	// // 姓名
	// oneReceiptParm.setData("PAT_NAME", "TEXT", opb.getPat().getName());
	// // 医疗机构名称
	// oneReceiptParm.setData("HOSP_DESC", "TEXT", Operator
	// .getHospitalCHNFullName());
	// // 费用合计
	// oneReceiptParm.setData("TOT_AMT", "TEXT", recpParm.getDouble("TOT_AMT",
	// 0));
	// // 费用显示大写金额
	// oneReceiptParm.setData("TOTAL_AW", "TEXT", StringUtil.getInstance()
	// .numberToWord(recpParm.getDouble("TOT_AMT", 0)));
	//
	// // 个人支付
	// oneReceiptParm.setData("Individual_pay", "TEXT", recpParm.getDouble(
	// "TOT_AMT", 0));
	// // 现金支付
	// oneReceiptParm.setData("Cash", "TEXT", null != recpParm.getValue(
	// "PAY_CASH", 0)
	// && recpParm.getDouble("PAY_CASH", 0) > 0 ? StringTool.round(
	// recpParm.getDouble("PAY_CASH", 0), 2) : StringTool.round(
	// recpParm.getDouble("PAY_MEDICAL_CARD", 0), 2));
	// // 打印日期
	// oneReceiptParm.setData("OPT_DATE", "TEXT", StringTool.getString(
	// SystemTool.getInstance().getDate(), "yyyy/MM/dd"));
	// // 医保金额
	// oneReceiptParm.setData("PAY_DEBIT", "TEXT", StringTool.round(recpParm
	// .getDouble("PAY_INS_CARD", 0), 2));
	// if (recpParm.getDouble("PAY_OTHER1", 0) > 0) {
	// // 绿色通道金额
	// oneReceiptParm.setData("GREEN_PATH", "TEXT", "绿色通道支付");
	// // 绿色通道金额
	// oneReceiptParm.setData("GREEN_AMT", "TEXT", StringTool.round(
	// recpParm.getDouble("PAY_OTHER1", 0), 2));
	//
	// }
	// // 医生名称
	// oneReceiptParm.setData("DR_NAME", "TEXT", recpParm.getValue(
	// "CASHIER_CODE", 0));
	//
	// // 打印人
	// oneReceiptParm.setData("OPT_USER", "TEXT", Operator.getName());
	// oneReceiptParm.setData("USER_NAME", "TEXT", Operator.getID());
	// // if (this.getValue("BILL_TYPE").equals("E")){
	// TParm EKTTemp = null;
	// if (isEKT) {
	// EKTTemp = EKTIO.getInstance().TXreadEKT();
	// if (null == EKTTemp || EKTTemp.getErrCode() < 0
	// || EKTTemp.getValue("MR_NO").length() <= 0) {
	//
	// this.messageBox("医疗卡没有使用不可以打票");
	// return;
	// } else {
	// if (!EKTTemp.getValue("MR_NO").equals(opb.getPat().getMrNo())) {
	// this.messageBox("医疗卡信息与此病患不符");
	// return;
	// }
	// }
	// }
	// oneReceiptParm.setData("START_AMT", "TEXT", "0.00"); // 起付金额
	// oneReceiptParm.setData("MAX_AMT", "TEXT", ""); // 最高限额余额
	// oneReceiptParm.setData("DA_AMT", "TEXT", "0.00"); // 账户支付
	// oneReceiptParm.setData("TEXT_TITLE1", "TEXT", "(详见费用清单)");
	// // =====zhangp 20120229 modify start
	// // oneReceiptParm.setData("TEXT_TITLE", "TEXT", "门诊联网已结算");
	// if ("1".equals(insType)) {
	// oneReceiptParm.setData("TEXT_TITLE", "TEXT", "门大联网已结算");
	// //oneReceiptParm.setData("Cost_class", "TEXT", "门统");
	// if (opb.getReg().getAdmType().equals("E")) {
	// oneReceiptParm.setData("TEXT_TITLE", "TEXT", "急诊联网已结算");
	// }
	// } else if ("2".equals(insType) || "3".equals(insType)) {
	// oneReceiptParm.setData("TEXT_TITLE", "TEXT", "门特联网已结算");
	// //oneReceiptParm.setData("Cost_class", "TEXT", "门特");
	// if (opb.getReg().getAdmType().equals("E")) {
	// oneReceiptParm.setData("TEXT_TITLE", "TEXT", "急诊联网已结算");
	// }
	// }
	// // =====zhangp 20120229 modify end
	// oneReceiptParm.setData("CARD_CODE", "TEXT", null != EKTTemp ? EKTTemp
	// .getValue("MR_NO")
	// + EKTTemp.getValue("SEQ") : "");
	// oneReceiptParm.setData("COPY", "TEXT", "(COPY)");
	// for (int i = 1; i <= 30; i++) {
	// if (i < 10)
	// oneReceiptParm.setData("CHARGE0" + i, "TEXT", recpParm
	// .getDouble("CHARGE0" + i, 0) == 0 ? "" : recpParm
	// .getData("CHARGE0" + i, 0));
	// else
	// oneReceiptParm.setData("CHARGE" + i, "TEXT", recpParm
	// .getDouble("CHARGE" + i, 0) == 0 ? "" : recpParm
	// .getData("CHARGE" + i, 0));
	// }
	// // =================20120219 zhangp modify start
	// oneReceiptParm.setData("CHARGE01", "TEXT", df.format(recpParm
	// .getDouble("CHARGE01", 0)
	// + recpParm.getDouble("CHARGE02", 0)));
	// String caseNo = opb.getReg().caseNo();
	//
	// TParm dparm = new TParm();
	// dparm.setData("CASE_NO", caseNo);
	// dparm.setData("ADM_TYPE",opb.getReg().getAdmType());
	// if (isEKT) {
	// onPrintEktParm(oneReceiptParm, recpParm, dparm);
	// } else {
	// onPrintCashParm(oneReceiptParm, recpParm, dparm);
	// }
	// // zhap 20120223 end
	//
	// this.openPrintDialog("%ROOT%\\config\\prt\\opb\\OPBRECTPrint.jhw",
	// oneReceiptParm);
	// return;
	// // }
	// // 南京门诊收费打票
	// // this.openPrintDialog("%ROOT%\\config\\prt\\opb\\OPB_Print1.jhw",
	// // oneReceiptParm);
	// // this.onClear();
	//
	// }
	/**
	 * 打印票据封装===================pangben 20111014
	 * 
	 * @param recpParm
	 *            TParm
	 * @param flg
	 *            boolean
	 */
	private void onPrint(TParm recpParm, boolean flg) {
		DecimalFormat df = new DecimalFormat("0.00");
		TParm oneReceiptParm = new TParm();
		TParm insOpdInParm = new TParm();
		String confirmNo = "";
		String cardNo = "";
		String insCrowdType = "";
		String insPatType = "";
		// 特殊人员类别代码
		String spPatType = "";
		// 特殊人员类别
		String spcPerson = "";
		double startStandard = 0.00; // 起付标准
		double accountPay = 0.00; // 个人实际帐户支付
		double gbNhiPay = 0.00; // 医保支付
		String reimType = ""; // 报销类别
		double gbCashPay = 0.00; // 现金支付
		double agentAmt = 0.00; // 补助金额
		double unreimAmt = 0.00;// 基金未报销金额
		double difference = 0.00;//优惠
		String reduceReason = "";
		TParm parm = new TParm();
		// System.out.println("---------------医保退费-------------:" + opbParm);
		parm.setData("CASE_NO", opb.getReg().caseNo());
		parm.setData("INV_NO", recpParm.getValue("PRINT_NO", 0));
		parm.setData("RECP_TYPE", "OPB");// 收费类型

		// 查询是否医保 退费
		TParm result = INSOpdTJTool.getInstance().selectInsInvNo(parm);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}
		// if (result.getCount("CASE_NO") <= 0) {// 不是医保退费
		// return ;
		// }
		// 医保打票操作
		if (null != result && null != result.getValue("CONFIRM_NO", 0)
				&& result.getValue("CONFIRM_NO", 0).length() > 0) {
			parm.setData("CONFIRM_NO", result.getValue("CONFIRM_NO", 0));
			TParm mzConfirmParm = INSMZConfirmTool.getInstance()
					.queryMZConfirm(parm);
			confirmNo = result.getValue("CONFIRM_NO", 0);
			cardNo = mzConfirmParm.getValue("INSCARD_NO", 0);// 医保卡号
			insOpdInParm.setData("CASE_NO", opb.getReg().caseNo());
			insOpdInParm.setData("CONFIRM_NO", confirmNo);
			TParm insOpdParm = INSOpdTJTool.getInstance().queryForPrint(
					insOpdInParm);
			unreimAmt = insOpdParm.getDouble("UNREIM_AMT", 0);// 基金未报销
			TParm insPatparm = INSOpdTJTool.getInstance().selPatDataForPrint(
					insOpdInParm);
			insCrowdType = insOpdParm.getValue("INS_CROWD_TYPE", 0); // 1.城职
			// 2.城居
			insPatType = insOpdParm.getValue("INS_PAT_TYPE", 0); // 1.普通
			// 特殊人员类别代码
			spPatType = insPatparm.getValue("SPECIAL_PAT", 0);
			// 特殊人员类别
			spcPerson = getSpPatDesc(spPatType);
			//====zhangp 20120712 start
			//报销类别
			reimType = insOpdInParm.getValue("REIM_TYPE", 0);
			//====zhangp 20120712 end
			// 城职普通
			if (insCrowdType.equals("1") && insPatType.equals("1")) {
				startStandard = insOpdParm.getDouble("INS_STD_AMT", 0);

				accountPay = insOpdParm.getDouble("ACCOUNT_PAY_AMT", 0);
				if (reimType.equals("1"))

					gbNhiPay = insOpdParm.getDouble("TOT_AMT", 0)
							- insOpdParm.getDouble("UNACCOUNT_PAY_AMT", 0)
							- insOpdParm.getDouble("ACCOUNT_PAY_AMT", 0)
							//====ZHANGP 20120712 START
							- insOpdParm.getDouble("UNREIM_AMT", 0)
							//====ZHANGP 20120712 END
							;
				else

					gbNhiPay = insOpdParm.getDouble("TOT_AMT", 0)
							- insOpdParm.getDouble("UNACCOUNT_PAY_AMT", 0)
							- insOpdParm.getDouble("ACCOUNT_PAY_AMT", 0)
							- insOpdParm.getDouble("ARMY_AI_AMT", 0)
							//===ZHANGP 20120712 START
							- insOpdParm.getDouble("UNREIM_AMT", 0)
							//===ZHANGP 20120712 END
							;
				gbNhiPay = TiMath.round(gbNhiPay, 2);

				gbCashPay = insOpdParm.getDouble("UNACCOUNT_PAY_AMT", 0)
						+ insOpdParm.getDouble("UNREIM_AMT", 0);

				agentAmt = insOpdParm.getDouble("ARMY_AI_AMT", 0);
			}
			// 城职门特
			if (insCrowdType.equals("1") && insPatType.equals("2")) {
				startStandard = insOpdParm.getDouble("INS_STD_AMT", 0);

				if (reimType.equals("1"))
					// 医保支付
					gbNhiPay = insOpdParm.getDouble("TOT_AMT", 0)
							- insOpdParm.getDouble("UNACCOUNT_PAY_AMT", 0)
							- insOpdParm.getDouble("ACCOUNT_PAY_AMT", 0)
							//===ZHANGP 20120712 START
							- insOpdParm.getDouble("UNREIM_AMT", 0)
							//===ZHANGP 20120712 END
							;
				else
					// 医保支付
					gbNhiPay = insOpdParm.getDouble("TOT_AMT", 0)
							- insOpdParm.getDouble("UNACCOUNT_PAY_AMT", 0)
							- insOpdParm.getDouble("ACCOUNT_PAY_AMT", 0)
							- insOpdParm.getDouble("ARMY_AI_AMT", 0)
							//===ZHANGP 20120712 START
							- insOpdParm.getDouble("UNREIM_AMT", 0)
							//===ZHANGP 20120712 END
							;
				gbNhiPay = TiMath.round(gbNhiPay, 2);
				// 个人实际帐户支付
				accountPay = insOpdParm.getDouble("ACCOUNT_PAY_AMT", 0);
				// 现金支付
				gbCashPay = insOpdParm.getDouble("UNACCOUNT_PAY_AMT", 0)
						+ insOpdParm.getDouble("UNREIM_AMT", 0);
				// 补助金额
				agentAmt = insOpdParm.getDouble("ARMY_AI_AMT", 0);
			}
			// 城居门特
			if (insCrowdType.equals("2") && insPatType.equals("2")) {
				startStandard = insOpdParm.getDouble("INS_STD_AMT", 0);

				// 个人实际帐户支付
				accountPay = insOpdParm.getDouble("ACCOUNT_PAY_AMT", 0);
				if (reimType.equals("1"))
					// 医保支付
					gbNhiPay = insOpdParm.getDouble("TOTAL_AGENT_AMT", 0)
							+ insOpdParm.getDouble("ARMY_AI_AMT", 0)
							+ insOpdParm.getDouble("FLG_AGENT_AMT", 0)
							//===ZHANGP 20120712 START
							- insOpdParm.getDouble("UNREIM_AMT", 0)
							//===ZHANGP 20120712 END
							;
				else
					// 医保支付
					gbNhiPay = insOpdParm.getDouble("TOTAL_AGENT_AMT", 0)
							+ insOpdParm.getDouble("FLG_AGENT_AMT", 0)
							//===ZHANGP 20120712 START
							- insOpdParm.getDouble("UNREIM_AMT", 0)
							//===ZHANGP 20120712 END
							;

				gbNhiPay = TiMath.round(gbNhiPay, 2);
				// 现金支付
				gbCashPay = insOpdParm.getDouble("TOT_AMT", 0)
						- insOpdParm.getDouble("TOTAL_AGENT_AMT", 0)
						- insOpdParm.getDouble("FLG_AGENT_AMT", 0)
						- insOpdParm.getDouble("ARMY_AI_AMT", 0)
						+ insOpdParm.getDouble("UNREIM_AMT", 0);
				gbCashPay = TiMath.round(gbCashPay, 2);
				// 补助金额
				agentAmt = insOpdParm.getDouble("ARMY_AI_AMT", 0);
			}
		}
		// INS_CROWD_TYPE, INS_PAT_TYPE
		// 票据信息
		// 姓名
//		oneReceiptParm.setData("PAT_NAME", "TEXT", opb.getPat().getName());
		oneReceiptParm.setData("PAT_NAME",  opb.getPat().getName());
		oneReceiptParm.setData("SEX_CODE",  opb.getPat().getSexString());
		// 特殊人员类别
		oneReceiptParm.setData("SPC_PERSON", "TEXT",
				spcPerson.length() == 0 ? "" : spcPerson);
		// 社会保障号
		oneReceiptParm.setData("Social_NO", "TEXT", cardNo);
		// 人员类别
		oneReceiptParm.setData("CTZ_DESC", "TEXT", "职工医保");
		oneReceiptParm.setData("COPY", "TEXT", "(COPY)");
		// 费用类别
		// ======zhangp 20120228 modify start
		if ("1".equals(insPatType)) {
			oneReceiptParm.setData("TEXT_TITLE", "TEXT", "门大联网已结算");
			if (opb.getReg().getAdmType().equals("E")) {
				oneReceiptParm.setData("TEXT_TITLE", "TEXT", "急诊联网已结算");
			}
			// oneReceiptParm.setData("Cost_class", "TEXT", "门统");
		} else if ("2".equals(insPatType) || "3".equals(insPatType)) {
			oneReceiptParm.setData("TEXT_TITLE", "TEXT", "门特联网已结算");
			if (opb.getReg().getAdmType().equals("E")) {
				oneReceiptParm.setData("TEXT_TITLE", "TEXT", "急诊联网已结算");
			}
			// oneReceiptParm.setData("Cost_class", "TEXT", "门特");
		}
		// =====zhangp 20120228 modify end
		// 医疗机构名称
		oneReceiptParm.setData("HOSP_DESC", "TEXT", Manager.getOrganization()
				.getHospitalCHNFullName(opb.getReg().getRegion()));
		// 起付金额
		oneReceiptParm.setData("START_AMT", "TEXT", df.format(startStandard));
		//===ZHANGP 20120712 START
		//基金未报销显示文字======pangben 2012-7-12
		oneReceiptParm.setData("MAX_DESC", "TEXT", unreimAmt == 0 ? "" : "基金未报销金额:");
		//===ZHANGP 20120712 END
		// 最高限额余额
		oneReceiptParm.setData("MAX_AMT", "TEXT", unreimAmt == 0 ? "--" : df
				.format(unreimAmt));
		//====zhangp 20120925 start
		//联网垫付，年终申报
		oneReceiptParm.setData("MAX_DESC2", "TEXT", unreimAmt == 0 ? "" : "联网垫付，年终申报");
		//====zhangp 20120925 end
		// 账户支付
		oneReceiptParm.setData("DA_AMT", "TEXT", df.format(accountPay));
		
/*----add by lich 20140928 添加报表信息----start*/
		
		String deptCode = opb.getReg().getDeptCode();
		String sqlDept = "SELECT DEPT_CHN_DESC FROM SYS_DEPT WHERE DEPT_CODE = '"+deptCode+"'";
		TParm deptParm = new TParm(TJDODBTool.getInstance().select(sqlDept));
		TComboBox ctzCode = (TComboBox) getComponent("CTZ1_CODE");
		System.out.println("ssssss"+ctzCode.getText());
		String s="SELECT CTZ_DESC FROM SYS_CTZ WHERE CTZ_CODE='"+ctzCode.getText()+"'";
		  TParm ctzparm=new TParm(TJDODBTool.getInstance().select(s));
		oneReceiptParm.setData("CTZ_DESC","TEXT",parm.getValue("CTZ_DESC"));
		
		oneReceiptParm.setData("REALDEPTCODE","TEXT",deptParm.getValue("DEPT_CHN_DESC",0));
		
		//add by huangtt 20140930  start
		String ownSql = "SELECT SUM(OWN_AMT) OWN_AMT ,SUM(AR_AMT) AR_AMT FROM OPD_ORDER WHERE CASE_NO='"
				+ recpParm.getValue("CASE_NO", 0)
				+ "' AND RECEIPT_NO='"
				+ recpParm.getValue("RECEIPT_NO", 0) + "' AND ( BILL_FLG='Y' OR MEM_PACKAGE_ID IS NOT NULL)";
//		System.out.println("ownSql=  ="+ownSql);
		TParm ownParm = new TParm(TJDODBTool.getInstance().select(ownSql));
		double ownAmt = ownParm.getDouble("OWN_AMT", 0); //折扣之前的价钱
		double totAmt = recpParm.getDouble("TOT_AMT", 0); //折扣之后的价钱
		double arAmt = recpParm.getDouble("AR_AMT", 0);  //实收
		double reduceAmt=recpParm.getDouble("REDUCE_AMT", 0); //减免
		//add by huangtt 20140930  start
		
		// 费用应收
		oneReceiptParm.setData("TOT_AMT", df.format(ownAmt));
//		System.out.println("recpParm++++++++++++++++++"+recpParm);
//		System.out.println("TOT_AMT-------------------"+recpParm.getDouble("TOT_AMT", 0));
//		System.out.println("REDUCE_REASON:::::::::::::::::"+recpParm.getValue("REDUCE_REASON", 0));
		
		//优惠金额
//		difference = recpParm.getDouble("TOT_AMT", 0)-recpParm.getDouble("AR_AMT", 0);
		difference = ownAmt-arAmt;
		oneReceiptParm.setData("DIFFERENCE",df.format(difference));
		
		//实收金额
		oneReceiptParm.setData("AR_AMT",df.format(recpParm.getDouble("AR_AMT",0)));
		
		//减免、折扣
		reduceReason = recpParm.getValue("REDUCE_NOTE", 0);
		
		if(reduceReason.trim().length() == 0){
			reduceReason = "折扣";
		}else if(reduceReason.trim().length() > 0){
			if (reduceReason.length()>55) {
				reduceReason=reduceReason.substring(0,55);
			}
			reduceReason = "减免原因："+reduceReason;
		}
		
		oneReceiptParm.setData("REDUCE_REASON",reduceReason);
		/*----add by lich 20140928 添加报表信息----end*/
		
		
		
		
		
		// 减免金额
		oneReceiptParm.setData("REDUCE_AMT", "TEXT", df.format(recpParm.getDouble(
				"REDUCE_AMT", 0)));
		// 费用合计
		oneReceiptParm.setData("TOT_AMT", "TEXT", df.format(recpParm.getDouble(
				"TOT_AMT", 0)));
		// 费用显示大写金额
//		oneReceiptParm.setData("TOTAL_AW", "TEXT", StringUtil.getInstance()
//				.numberToWord(recpParm.getDouble("AR_AMT", 0)));
		oneReceiptParm.setData("TOTAL_AW", StringUtil.getInstance().numberToWord(recpParm.getDouble("AR_AMT", 0)));//==liling

		// 统筹支付
		oneReceiptParm.setData("Overall_pay", "TEXT", StringTool.round(recpParm
				.getDouble("Overall_pay", 0), 2));
		// 个人支付
		oneReceiptParm.setData("Individual_pay", "TEXT", df.format(recpParm
				.getDouble("AR_AMT", 0)));
		// 现金支付= 医疗卡金额+现金+绿色通道
		double payCash = StringTool.round(recpParm.getDouble("PAY_CASH", 0), 2)
				+ StringTool
						.round(recpParm.getDouble("PAY_MEDICAL_CARD", 0), 2)
				+ StringTool.round(recpParm.getDouble("PAY_OTHER1", 0), 2);
		// 现金支付
		oneReceiptParm.setData("Cash", "TEXT", gbCashPay == 0 ? payCash : df
				.format(gbCashPay));

		// 账户支付---医疗卡支付
		oneReceiptParm.setData("Recharge", "TEXT", 0.00);
		// 医疗救助金额
		// oneReceiptParm.setData("AGENT_AMT", "TEXT", df.format(agentAmt));
		// =====zhangp 20120229 modify start
		if (agentAmt != 0) {
			oneReceiptParm.setData("AGENT_NAME", "TEXT", "医疗救助支付");
			// 医疗救助金额
			oneReceiptParm.setData("AGENT_AMT", "TEXT", df.format(agentAmt));
		}
//		oneReceiptParm.setData("MR_NO", "TEXT", opb.getPat().getMrNo());
		oneReceiptParm.setData("MR_NO", opb.getPat().getMrNo());
		// =====zhangp 20120229 modify end
		// 打印日期
		oneReceiptParm.setData("OPT_DATE", StringTool.getString(
				SystemTool.getInstance().getDate(), "yyyy/MM/dd HH:mm:ss"));
		// 医保金额
		//===zhangp 20120703 start
//		oneReceiptParm.setData("PAY_DEBIT", "TEXT", gbNhiPay == 0 ? StringTool
//				.round(recpParm.getDouble("PAY_INS_CARD", 0), 2) : df
//				.format(gbNhiPay));
		//===zhangp 20120703 end
		oneReceiptParm.setData("PAY_DEBIT", "TEXT", df
				.format(gbNhiPay));
		if (recpParm.getDouble("PAY_OTHER1", 0) > 0) {
			// 绿色通道金额
			oneReceiptParm.setData("GREEN_PATH", "TEXT", "绿色通道支付");
			// 绿色通道金额
			oneReceiptParm.setData("GREEN_AMT", "TEXT", StringTool.round(
					recpParm.getDouble("PAY_OTHER1", 0), 2));

		}
		// 医生名称
		oneReceiptParm.setData("DR_NAME", "TEXT", recpParm.getValue(
				"CASHIER_CODE", 0));

		// 打印人
		oneReceiptParm.setData("OPT_USER", "TEXT", Operator.getName());
		oneReceiptParm.setData("USER_NAME", Operator.getID());
		oneReceiptParm.setData("TEXT_TITLE1", "TEXT", "(详见费用清单)");
		// =====20120229 zhangp modify start
		if (cardNo.equals("")) {
			oneReceiptParm.setData("CARD_CODE", "TEXT", opb.getPat().getIdNo());// 如果不是医保
			// 显示身份证号
		} else {
			oneReceiptParm.setData("CARD_CODE", "TEXT", cardNo);// 否则 显示医保卡号
		}
		// =====20120229 zhangp modify end
		for (int i = 1; i <= 30; i++) {
			if (i < 10) {
//				oneReceiptParm.setData("CHARGE0" + i, "TEXT", recpParm
//						.getDouble("CHARGE0" + i, 0) == 0 ? "" : recpParm
//						.getData("CHARGE0" + i, 0));
				oneReceiptParm.setData("CHARGE0" + i,df.format( recpParm.getDouble("CHARGE0" + i, 0)).equals("0.00") ? "" : df.format( recpParm.getDouble("CHARGE0" + i, 0)));//==liling
			} else {
//				oneReceiptParm.setData("CHARGE" + i, "TEXT", recpParm
//						.getDouble("CHARGE" + i, 0) == 0 ? "" : recpParm
//						.getData("CHARGE" + i, 0));
				oneReceiptParm.setData("CHARGE" + i, df.format( recpParm.getDouble("CHARGE" + i, 0)).equals("0.00") ? "" : df.format( recpParm.getDouble("CHARGE" + i, 0)));//==liling
			}
		}
		//====================20140609 liling  modify start 查出各项收费对应的中文=====
		String sql="SELECT ID,CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_CHARGE' ORDER BY ID ";
		TParm parm1 = new TParm(TJDODBTool.getInstance().select(sql));
		String sql2= "SELECT ADM_TYPE,CHARGE01,CHARGE02,CHARGE03,CHARGE04, CHARGE05,CHARGE06,CHARGE07,CHARGE08,CHARGE09, "+
                     " CHARGE10,CHARGE11,CHARGE12,CHARGE13,CHARGE14, CHARGE15,CHARGE16,CHARGE17,CHARGE18,CHARGE19,CHARGE20 "+
                     " FROM BIL_RECPPARM   WHERE  ADM_TYPE='O'  " ;
		TParm parm2 = new TParm(TJDODBTool.getInstance().select(sql2));
		int row = tableM.getSelectedRow();
		TParm tableParm = tableM.getParmValue();
		String sql3 = "SELECT MEM_PACKAGE_ID FROM OPD_ORDER WHERE MR_NO = '"+
			opb.getPat().getMrNo()+"' AND CASE_NO = '"+opb.getReg().caseNo()+"' AND MEM_PACKAGE_ID IS NOT NULL " +
					"AND RECEIPT_NO = '"+tableParm.getData("RECEIPT_NO", row)+"'";//add by sunqy 20140827 是否医生站引用套餐
		TParm parm3 = new TParm(TJDODBTool.getInstance().select(sql3));
//		System.out.println("parm3= = = = =" + parm3);
//		System.out.println("sql3====="+sql3);
		String	id="";
		String	charge ="";
		for(int i=1;i<=20;i++){
			if (i < 10) {
			charge =parm2.getValue("CHARGE0"+i, 0);
			for(int j=0;j<parm1.getCount();j++){
				id=parm1.getValue("ID", j);	
				if(id.equals(charge)){
					oneReceiptParm.setData("CHARGE0" + i+"_D", parm1.getData("CHN_DESC", j));
//					System.out.println(i+"========="+parm1.getData("CHN_DESC", j));
					}
			}
			} else {
			charge =parm2.getValue("CHARGE"+i, 0);
			for(int j=0;j<parm1.getCount();j++){
				id=parm1.getValue("ID", j);	
				if(!id.equals(charge))continue;
				else{
					oneReceiptParm.setData("CHARGE" + i+"_D", parm1.getData("CHN_DESC", j));
//					System.out.println(i+"========="+parm1.getData("CHN_DESC", j));
					}
				}
			}
		}
		oneReceiptParm.setData("CHARGE001_D", "西药费");
//		oneReceiptParm.setData("BILL_DATE", "TEXT",StringTool.getString(
//				recpParm.getTimestamp("BILL_DATE", 0), "yyyy/MM/dd HH:mm:ss"));//结算日期
		oneReceiptParm.setData("BILL_DATE", StringTool.getString(
				recpParm.getTimestamp("BILL_DATE", 0), "yyyy/MM/dd HH:mm:ss"));//结算日期
		oneReceiptParm.setData("AR_AMT", df.format(recpParm.getDouble("AR_AMT", 0)));//合计
		//====================20140609 liling  modify end  ===========================
		// =================20120219 zhangp modify start
//		oneReceiptParm.setData("CHARGE01", "TEXT", df.format(recpParm
//				.getDouble("CHARGE01", 0)
//				+ recpParm.getDouble("CHARGE02", 0)));
		oneReceiptParm.setData("CHARGE01", df.format(recpParm.getDouble("CHARGE01", 0)+ recpParm.getDouble("CHARGE02", 0)).equals("0.00")?"":df.format(recpParm.getDouble("CHARGE01", 0)+ recpParm.getDouble("CHARGE02", 0)));//==liling
		String caseNo = opb.getReg().caseNo();

		TParm dparm = new TParm();
		dparm.setData("CASE_NO", caseNo);
		dparm.setData("ADM_TYPE", opb.getReg().getAdmType());
		onPrintCashParm(oneReceiptParm, recpParm, dparm);
//	    oneReceiptParm.setData("RECEIPT_NO", "TEXT", recpParm.getValue("RECEIPT_NO", 0));//add by wanglong 20121217  PRINT_NO
		oneReceiptParm.setData("RECEIPT_NO", "TEXT", recpParm.getValue("PRINT_NO", 0));//===modify by liling 20140703 
//		this.openPrintDialog("%ROOT%\\config\\prt\\opb\\OPBRECTPrint.jhw",
//				oneReceiptParm, true);
		
		//add by sunqy 20140827 为收据添加支付细项 ----start----
		String mapSql = "SELECT A.PAYTYPE, B.CHN_DESC FROM BIL_GATHERTYPE_PAYTYPE A,SYS_DICTIONARY B " +
				"WHERE B.GROUP_ID='GATHER_TYPE' AND A.GATHER_TYPE=B.ID";//储存PAY_TYPE极其对应的中文
		TParm mapParm = new TParm(TJDODBTool.getInstance().select(mapSql));
		String sql4 = "SELECT PAY_CASH,PAY_MEDICAL_CARD,PAY_TYPE01,PAY_TYPE02,PAY_TYPE03,PAY_TYPE04," +
				"PAY_TYPE05,PAY_TYPE06,PAY_TYPE07,PAY_TYPE08,PAY_TYPE09,PAY_TYPE10,PAY_OTHER3,PAY_OTHER4,REMARK02,REMARK09,REMARK10,PAY_TYPE11,REMARK11 " +
				"FROM BIL_OPB_RECP WHERE CASE_NO = '" + caseNo + "' " +
				"AND RECEIPT_NO = '" + recpParm.getData("RECEIPT_NO", 0).toString() + "'";
		TParm parm4 = new TParm(TJDODBTool.getInstance().select(sql4));
		String payDetail = "";
		String card_type = "";
		if(!"".equals(parm4.getValue("PAY_MEDICAL_CARD",0)) && parm4.getValue("PAY_MEDICAL_CARD",0)!=null && parm4.getDouble("PAY_MEDICAL_CARD",0)!=0){
			payDetail += ";医疗卡"+parm4.getValue("PAY_MEDICAL_CARD",0)+"元";
		}
		if(!"".equals(parm4.getValue("PAY_OTHER3",0)) && parm4.getValue("PAY_OTHER3",0)!=null && parm4.getDouble("PAY_OTHER3",0)!=0){
			payDetail += ";礼品卡"+parm4.getValue("PAY_OTHER3",0)+"元";
		}
		if(!"".equals(parm4.getValue("PAY_OTHER4",0)) && parm4.getValue("PAY_OTHER4",0)!=null && parm4.getDouble("PAY_OTHER4",0)!=0){
			payDetail += ";现金折扣券"+parm4.getValue("PAY_OTHER4",0)+"元";
		}
		if(!"".equals(parm4.getValue("PAY_CASH",0)) && parm4.getValue("PAY_CASH",0)!=null && parm4.getDouble("PAY_CASH",0)!=0){
			for (int i = 0; i < mapParm.getCount(); i++) {
				if("".equals(parm4.getValue(mapParm.getValue("PAYTYPE", i),0)) || parm4.getValue(mapParm.getValue("PAYTYPE", i),0)==null 
						|| parm4.getDouble(mapParm.getValue("PAYTYPE", i),0)==0){
					continue;
				}
				payDetail += ";"+mapParm.getValue("CHN_DESC", i)+parm4.getValue(mapParm.getValue("PAYTYPE", i),0)+"元";
				if(mapParm.getValue("PAYTYPE", i).equals("PAY_TYPE02")){
					card_type=parm4.getValue("REMARK02", 0);//收费为刷卡时，收据添加卡号和卡类型 add by huangjw 20141230 
				}
			}
		}
		if(!"".equals(payDetail)){
			payDetail = payDetail.substring(1, payDetail.length());
		}
		
		TParm receptParm = (TParm)this.getParameter();

		String memPackageFlg = receptParm.getValue("MEM_PACKAGE_FLG");
//		System.out.println("memPackageFlg = = = " + memPackageFlg);
		oneReceiptParm.setData("PAY_DETAIL", payDetail);
		//add by sunqy 20140827 为收据添加支付细项 ----end----
		//收费为刷卡时，收据添加卡号和卡类型 add by huangjw 20141230  start
		if(!"".equals(card_type)&&!"#".equals(card_type)){
			String [] str=card_type.split("#");//将卡类型和卡号以#分开
			String [] str1=str[0].split(";");//将卡类型以；分开
//			String [] str2=str[1].split(";");//将卡号以；分开
			String [] str2=null;
			if(str.length == 2){
				str2=str[1].split(";");
			}
			String cardtypeString="";
			for(int m=0;m<str1.length;m++){
				String cardsql= "select CHN_DESC from sys_dictionary where id='"+str1[m]+"' and group_id='SYS_CARDTYPE'";
				TParm cardParm=new TParm(TJDODBTool.getInstance().select(cardsql));
				cardtypeString=cardtypeString+cardParm.getValue("CHN_DESC",0)+" ";
				
				if(str2 != null){
					if(m < str2.length ){
						cardtypeString=cardtypeString+str2[m]+" ";
					}
				}
			}
			oneReceiptParm.setData("CARD_TYPE",cardtypeString);
		}
		String dept_package_session = "科室："+ deptParm.getValue("DEPT_CHN_DESC",0)+ "    ";
		if(parm3.getCount()<=0){//add by sunqy 20140826 引用套餐的情况标题改为"门诊结算收据（套餐）"
//		if("N".equals(memPackageFlg)){
			if(recpParm.getValue("ADM_TYPE", 0).equals("E")){
				oneReceiptParm.setData("TITLE", "TEXT", "急诊结算收据");
			}else {
				oneReceiptParm.setData("TITLE", "TEXT", "门诊结算收据");
			}
			oneReceiptParm.setData("CHN1","优 惠 金 额");
			oneReceiptParm.setData("DIFFERENCE",df.format(difference));
		}else{
			oneReceiptParm.setData("TITLE", "TEXT", "门诊结算收据【套餐结转】");
			oneReceiptParm.setData("CHN1","结 转 金 额");
			oneReceiptParm.setData("DIFFERENCE",ownParm.getDouble("AR_AMT", 0));
			//add by huangtt 20150714 添加套餐名称与时程名称 -----start
			String memSql = "SELECT B.PACKAGE_DESC, B.SECTION_DESC" +
					" FROM OPD_ORDER A, MEM_PAT_PACKAGE_SECTION_D B" +
					" WHERE  A.CASE_NO = '"+opb.getReg().caseNo()+"'" +
					" AND A.MEM_PACKAGE_FLG = 'Y'" +
					" AND A.RECEIPT_NO ='" +recpParm.getData("RECEIPT_NO", 0).toString()+"'"+
					" AND A.MEM_PACKAGE_ID = B.ID GROUP BY B.PACKAGE_DESC, B.SECTION_DESC" +
					" ORDER BY B.PACKAGE_DESC, B.SECTION_DESC";
			TParm memParm = new TParm(TJDODBTool.getInstance().select(memSql));;
			List<String> packageCodeList = new ArrayList<String>();
			List<String> sessionCodeList = new ArrayList<String>();
			for (int i = 0; i < memParm.getCount(); i++) {
				
				if(!packageCodeList.contains(memParm.getValue("PACKAGE_DESC", i))){
					packageCodeList.add(memParm.getValue("PACKAGE_DESC", i));
				}
				if(!sessionCodeList.contains(memParm.getValue("SECTION_DESC", i))){
					sessionCodeList.add(memParm.getValue("SECTION_DESC", i));
				}
			}

			dept_package_session += "套餐名称:";
			for (int i = 0; i < packageCodeList.size(); i++) {
				dept_package_session = dept_package_session+ packageCodeList.get(i)+"、";
			}
			dept_package_session = dept_package_session.substring(0, dept_package_session.length()-1);
			dept_package_session += "  时程名称:";
			for (int i = 0; i < sessionCodeList.size(); i++) {
				dept_package_session = dept_package_session + sessionCodeList.get(i)+"、";
			}
			dept_package_session = dept_package_session.substring(0, dept_package_session.length()-1);
			
			//add by huangtt 20150714 添加套餐名称与时程名称 -----end
		}
		dept_package_session=dept_package_session+"  就诊身份:"+ctzparm.getValue("CTZ_DESC");
		TParm T1 = new TParm();
		T1.addData("MEM_CODE", dept_package_session);
		T1.setCount(1);
		T1.addData("SYSTEM", "COLUMNS","MEM_CODE");
		oneReceiptParm.setData("MEMTABLE",T1.getData());
	    this.openPrintDialog(IReportTool.getInstance().getReportPath("OPBRECTPrint.jhw"),
                             IReportTool.getInstance().getReportParm("OPBRECTPrint.class", oneReceiptParm));//报表合并modify by wanglong 20130730
//	    this.openPrintDialog("%ROOT%\\config\\prt\\opb\\OPBRECTPrint_V45.jhw", oneReceiptParm);
	    	    return;

	}

	/**
	 * 现金打票明细入参
	 */
	private void onPrintCashParm(TParm oneReceiptParm, TParm recpParm,
			TParm dparm) {
		String receptNo = recpParm.getData("RECEIPT_NO", 0).toString();
		dparm.setData("NO", receptNo);
		TParm tableresultparm = OPBTool.getInstance().getReceiptDetail(dparm);
		if (oneReceiptParm.getCount() > 10) {
			tableresultparm.setData("DETAIL", "TEXT", "(详见费用明细表)");
		}
		oneReceiptParm.setData("TABLE", tableresultparm.getData());
	}

	/**
	 * 特殊人员类别
	 * 
	 * @param type
	 *            String
	 * @return String
	 */
	private String getSpPatDesc(String type) {
		if (type == null || type.length() == 0 || type.equals("null"))
			return "";
		if ("04".equals(type))
			return "伤残军人";
		if ("06".equals(type))
			return "公务员";
		if ("07".equals(type))
			return "民政救助人员";
		if ("08".equals(type))
			return "优抚对象";
		return "";
	}

	/**
	 * 医疗卡打票明细入参
	 * 
	 * @param oneReceiptParm
	 * @param recpParm
	 * @param dparm
	 */
	private void onPrintEktParm(TParm oneReceiptParm, TParm recpParm,
			TParm dparm) {
		String receptNo = recpParm.getData("RECEIPT_NO", 0).toString();
		dparm.setData("NO", receptNo);
		TParm tableresultparm = OPBTool.getInstance().getReceiptDetail(dparm);
		// if(orderParm.getCount()>10){
		// oneReceiptParm.setData("DETAIL", "TEXT", "(详见费用明细表)");
		// }
		oneReceiptParm.setData("TABLE", tableresultparm.getData());
	}
	/**
	 * 医疗卡退票公用
	 * @param actionParm
	 * @return
	 * =====pangben 2014-8-22
	 */
	private TParm ektCommRePrint(TParm actionParm){
		type = "医疗卡";
		// 医保操作
		if (!reSetInsSave(actionParm))
			return null;
		if (!insFlg) {
			if (actionParm.getDouble("REDUCE_AMT")>0) {
				this.messageBox(type + "退票操作,减免扣款："+actionParm.getDouble("REDUCE_AMT")+"元");
			}else{
				this.messageBox("印刷号:"+actionParm.getValue("PRINT_NO")+" "+type + "退票操作,不执行退费");
			}
		}
		TParm result = ektResetFee(actionParm);
		if (null == result) {
			return result;
		}
		if(result.getErrCode()<0){
			this.messageBox(result.getErrText());
			return null;
		}
		return result;
	}
	/**
	 * 现金退票公用
	 * @param actionParm
	 * @return
	 * =====pangben 2014-8-22
	 */
	private TParm cashCommRePrint(TParm actionParm){
		//add by huangtt 20140905 start
		String sql="SELECT A.GATHER_TYPE, A.PAYTYPE, B.CHN_DESC" +
				" FROM BIL_GATHERTYPE_PAYTYPE A, SYS_DICTIONARY B" +
				" WHERE A.GATHER_TYPE = B.ID AND GROUP_ID = 'GATHER_TYPE'";
		TParm payTypeParm = new TParm(TJDODBTool.getInstance().select(sql));
		TParm typeParm=new TParm();
		for(int i=0;i<payTypeParm.getCount();i++){
			if(actionParm.getDouble(payTypeParm.getValue("PAYTYPE", i))>0){
				//=====pangben 2016-6-21 添加微信和支付宝退票填写交易号功能
				if(payTypeParm.getValue("GATHER_TYPE", i).equals("WX")){
					if(actionParm.getDouble(payTypeParm.getValue("PAYTYPE", i))>0){
						typeParm.setData("WX_FLG","Y");
						typeParm.setData("FLG","Y");
						typeParm.setData("WX_AMT",actionParm.getDouble(payTypeParm.getValue("PAYTYPE", i)));
						
					}
				}
				if(payTypeParm.getValue("GATHER_TYPE", i).equals("ZFB")){
					if(actionParm.getDouble(payTypeParm.getValue("PAYTYPE", i))>0){
						typeParm.setData("ZFB_FLG","Y");
						typeParm.setData("FLG","Y");
						typeParm.setData("ZFB_AMT",actionParm.getDouble(payTypeParm.getValue("PAYTYPE", i)));
					}
				}
				type = type + " " +payTypeParm.getValue("CHN_DESC", i) +"退费金额:" + actionParm.getDouble(payTypeParm.getValue("PAYTYPE", i));
			}
		}
		if(null!=typeParm.getValue("FLG")&&typeParm.getValue("FLG").equals("Y")){
			Object result = this.openDialog(
    	            "%ROOT%\\config\\bil\\BILPayTypeTransactionNo.x", typeParm, false);
			if(result==null){
				return null;
			}
			actionParm.setData("PAY_TYPE_FLG","Y");//支付类型退票添加交易号
			actionParm.setData("PAY_TYPE_PARM",((TParm)result).getData());
		}
		//add by huangtt 20140905 end
		
//		type = "现金";
		cashFLg = true;
		isCheckBack(1);
		// 医保操作
		if (!reSetInsSave(actionParm))
			return null;
		if (!insFlg) {
//			this.messageBox("印刷号:"+actionParm.getValue("PRINT_NO")+" "+type + "退费金额:"
//					+ actionParm.getDouble("PAY_CASH"));
			this.messageBox("印刷号:"+actionParm.getValue("PRINT_NO")+" "+type );
		}
		// 现金退费
		TParm result = TIOM_AppServer.executeAction("action.opb.OPBAction",
				"backOPBRecp", actionParm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return null;
		}
		return result;
	}
	/**
	 * 医疗卡和现金退废票据
	 * 
	 * @param row
	 *            int
	 * @return boolean
	 */
	public boolean onSaveBackReceipt(int row) {
		TParm saveParm = tableM.getParmValue();
		TParm actionParm = saveParm.getRow(row);
		actionParm.setData("OPT_USER_T", Operator.getID());
		actionParm.setData("OPT_TERM_T", Operator.getIP());
		actionParm.setData("OPT_DATE_T", SystemTool.getInstance().getDate());
		actionParm.setData("PRINT_DATE", actionParm.getData("BILL_DATE"));
		actionParm.setData("ADM_TYPE", opb.getReg().getAdmType());		
		if (saveParm == null)				
			return false;
		// 调用opbaction
		TParm result = null;
		TParm parm = null;  
		
		//获得历史表数据 add by huangtt 20150604
	   TParm makeOrderInvalidParm = OPBMakeRecpAndOrderInvalid.getInstance().getMakeOrderInvalid(actionParm.getValue("RECEIPT_NO"));
		
	   actionParm.setData("PAY_MEDICAL_CARD", actionParm.getDouble("PAY_MEDICAL_CARD")-actionParm.getDouble("PAY_TYPE11"));
	   
		// 医疗卡操作
		if (actionParm.getDouble("PAY_MEDICAL_CARD")
				+ actionParm.getDouble("PAY_OTHER1") > 0||
//				actionParm.getDouble("PAY_OTHER3") > 0||//20141219 YANJING modify
//				actionParm.getDouble("PAY_OTHER4") > 0
				actionParm.getDouble("EKT_LPK") > 0||
				actionParm.getDouble("EKT_XJZKQ") > 0
				) {//======pangben 
			result=ektCommRePrint(actionParm);
			if (null==result) {
				return false;
			}
			// 现金操作
//		} else if (actionParm.getDouble("PAY_CASH") > 0) {
		} else if (actionParm.getDouble("PAY_CASH_TOT") > 0) {
			result=cashCommRePrint(actionParm);
			if (null==result) {
				return false;
			}
			// 医保全部报销操作,执行现金操作
		} else if (actionParm.getDouble("PAY_INS_CARD") > 0
				&& (actionParm.getDouble("PAY_MEDICAL_CARD")
						+ actionParm.getDouble("PAY_OTHER1")) == 0
				&& actionParm.getDouble("PAY_CASH") == 0 && actionParm.getDouble("PAY_OTHER2") == 0) {
			if (!onExeInsSum(actionParm, result)) {
				return false;							
			}
			//建行操作
		}else if(actionParm.getDouble("PAY_OTHER2") >0){
			if(!isCheckBack(2)){
				return false;
			}
			TParm opbParm=getCcbParm(actionParm);
//			this.messageBox("opbParm==="+opbParm);
			//建行接口操作						
			//TParm resultData=REGCcbReTool.getInstance().getCcbRe(opbParm);
			TParm resultData = TIOM_AppServer.executeAction("action.ccb.CCBServerAction",
					"getCcbRe", opbParm);																							
			if (resultData.getErrCode()<0) {
				this.messageBox("建行接口调用出现问题,请联系信息中心");
				return false;				
			}
			//医保操作								
			if(!REGCcbReTool.getInstance().reSetInsSave(opbParm,this)){
				return false;
			}
			resultData.setData("OPT_USER",Operator.getID()); 
			resultData.setData("OPT_TERM",Operator.getIP());
			resultData.setData("BUSINESS_TYPE","OPBT");
			result=REGCcbReTool.getInstance().saveEktCcbTrede(resultData);
			if (result.getErrCode() < 0) {
				this.messageBox("添加交易档失败");
				return false;
			}
			//作废票据
			result = TIOM_AppServer.executeAction("action.opb.OPBAction",
					"backOPBRecp", actionParm);
		}else if(actionParm.getDouble("REDUCE_AMT") > 0){//全部减免
			String sql="SELECT BILL_TYPE FROM OPD_ORDER WHERE CASE_NO='"+
			actionParm.getData("CASE_NO")+"' AND RECEIPT_NO='"+actionParm.getValue("RECEIPT_NO")+"' GROUP BY BILL_TYPE";
			result =new TParm(TJDODBTool.getInstance().select(sql));
			if (result.getValue("BILL_TYPE",0).equals("E")) {//医疗卡打票
				result=ektCommRePrint(actionParm);
				if (null==result) {
					return false;
				}
			}else{//现金打票
				result=cashCommRePrint(actionParm);
				if (null==result) {
					return false;
				}
			}
		}else if(actionParm.getBoolean("MEM_PACK_FLG")){ // 套餐退票  add by huangtt 20141216
			result=cashCommRePrint(actionParm);
			if (null==result) {
				return false;
			}
		}else if(actionParm.getDouble("TOT_AMT") == 0){ // 0元退票  add by huangtt 20141216
			result=cashCommRePrint(actionParm);
			if (null==result) {
				return false;
			}
		}
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return false;
		}
		if (!deleteInsRun(opb.getReg().caseNo())) {
			System.out.println("删除在途状态失败：" + opb.getReg().caseNo());
		}
		
		//modiby by huagtt 20150604 start
		if(!OPBMakeRecpAndOrderInvalid.getInstance().makeOrderInvalid(makeOrderInvalidParm)){
			this.messageBox("写历史表错误");
			return false;
		}
		//modiby by huagtt 20150604 end
		
		return true;
	}
	/**
	 * 医保全部报销逻辑
	 * @return
	 */
	private boolean onExeInsSum(TParm actionParm,TParm result){
		//添加建行
		TParm ektCcbParm=new TParm(TJDODBTool.getInstance().select("SELECT CASE_NO FROM EKT_CCB_TRADE WHERE CASE_NO='"+opb.getReg().caseNo()+
				"' AND RECEIPT_NO='"+actionParm.getValue("RECEIPT_NO")+"'"));
		if (ektCcbParm.getErrCode()<0) {
			return false;
		}
		if (ektCcbParm.getCount("CASE_NO")>0) {
			if(!isCheckBack(2)){
				return false;
			}
			//建行操作
			TParm opbParm=getCcbParm(actionParm);
			if(!REGCcbReTool.getInstance().reSetInsSave(opbParm,this)){
				return false;
			}
			actionParm.setData("CCB_FLG","Y");
		}else{
			type = "医疗卡";
			if (!reSetInsSave(actionParm))
				return false;
			// 医疗卡退费
			actionParm.setData("INS_UN_FLG", "Y");
		}
		result = TIOM_AppServer.executeAction("action.opb.OPBAction",
				"backOPBRecp", actionParm);
		return true;
	}
	/**
	 * 现金、建行操作 是否可以退费
	 * @return
	 * type：1 ：现金操作 2.建行操作
	 */
	private boolean isCheckBack(int type){
		boolean returnFeeFlg = false;
		// 校验是否已到检或已发药
		StringBuffer messagePha = new StringBuffer();
		StringBuffer message = new StringBuffer();
		TParm orderParm = tableD.getParmValue();
		int count = orderParm.getCount("EXEC_FLG");
		String exeFlg = "";
		int HL7Count = 0;
		for (int i = 0; i < count; i++) {
			exeFlg = orderParm.getValue("EXEC_FLG", i);
			if ("Y".equals(exeFlg)) {
				returnFeeFlg = true;
				if (orderParm.getValue("CAT1_TYPE", i).equals("PHA")) {// 药品
					messagePha.append(orderParm.getValue("ORDER_DESC", i)
							+ ",");
				} else {
					if (orderParm.getValue("CAT1_TYPE", i).equals("LIS")
							|| orderParm.getValue("CAT1_TYPE", 0).equals(
									"RIS")) {
						sendHL7Parm.setRowData(HL7Count, orderParm, i);
						HL7Count++;
						if (orderParm.getValue("SETMAIN_FLG", i)
								.equals("Y")) {
							message.append(orderParm.getValue("ORDER_DESC",
									i)
									+ ",");
						}
					}
				}
			}
		}

		if (returnFeeFlg) {
			String sumMessage = "";
			if (messagePha.length() > 0) {
				sumMessage = messagePha.toString().substring(0,
						messagePha.lastIndexOf(","))
						+ " 已经发药\n";
			}
			if (message.length() > 0) {
				sumMessage += message.toString().substring(0,
						message.lastIndexOf(","))
						+ " 已经到检\n";
			}
			switch (type){
			case 1:
				sumMessage += "现金退费操作请注意";
				break;
			case 2:
				sumMessage += "不可以进行建行退费操作";
				break;
			}
			
			this.messageBox(sumMessage);
			return false;
		}
		return true;
	}
	/**
	 * 建行医保操作入参
	 * @param actionParm
	 * @return
	 */
	private TParm getCcbParm(TParm actionParm){
		TParm opbParm=new TParm();
		opbParm.setData("CASE_NO" ,opb.getReg().caseNo());// 退挂使用
		opbParm.setData("PAT_NAME", opb.getPat().getName());
		opbParm.setData("MR_NO", opb.getPat().getMrNo());// 病患号
		opbParm.setData("NHI_NO",regionParm.getValue("NHI_NO", 0));
		opbParm.setData("PRINT_NO",actionParm.getValue("PRINT_NO"));
		opbParm.setData("OPT_USER", Operator.getID());// id
		opbParm.setData("OPT_TERM", Operator.getIP());// ip
		opbParm.setData("OPT_NAME", Operator.getName());// ip
		opbParm.setData("CTZ1_CODE", this.getValue("CTZ1_CODE"));
		opbParm.setData("RECEIPT_NO",actionParm.getValue("RECEIPT_NO"));					
		opbParm.setData("AMT", actionParm.getDouble("PAY_OTHER2"));
		return opbParm;
	}
	/**
	 * 医疗卡充值
	 */
	public void onTop() {
		TParm parm =new TParm();
		parm.setData("FLG","Y");
		parm = (TParm) openDialog("%ROOT%\\config\\ekt\\EKTTopUp.x",parm);
	}
	/**
	 * 调用HL7
	 */
	private void sendHL7Mes(TParm sendHL7Parm) {
		/**
		 * 发送HL7消息
		 * 
		 * @param admType
		 *            String 门急住别
		 * @param catType
		 *            医令分类
		 * @param patName
		 *            病患姓名
		 * @param caseNo
		 *            String 就诊号
		 * @param applictionNo
		 *            String 条码号
		 * @param flg
		 *            String 状态(0,发送1,取消)
		 */
		int count = 0;
		if (null != sendHL7Parm && null != sendHL7Parm.getData("ADM_TYPE"))
			count = ((Vector) sendHL7Parm.getData("ADM_TYPE")).size();
		List list = new ArrayList();
		String patName = opb.getPat().getName();
		for (int i = 0; i < count; i++) {
			TParm temp = sendHL7Parm.getRow(i);
			String admType = temp.getValue("ADM_TYPE");
			String sql = " SELECT CASE_NO,MED_APPLY_NO,CAT1_TYPE FROM OPD_ORDER "
					+ "  WHERE CASE_NO ='"
					+ opb.getReg().caseNo()
					+ "' "
					+ "    AND RX_NO='"
					+ temp.getValue("RX_NO")
					+ "' "
					+ "    AND ORDERSET_CODE IS NOT NULL "
					+ "    AND ORDERSET_CODE = ORDER_CODE "
					+ "    AND SEQ_NO='"
					+ temp.getValue("SEQ_NO")
					+ "' AND ADM_TYPE='" + admType + "'";
			// System.out.println("SQL:"+sql);
			TParm result = new TParm(TJDODBTool.getInstance().select(sql));
			// System.out.println("查询结果:"+result);
			TParm parm = new TParm();
			parm.setData("PAT_NAME", patName);
			parm.setData("ADM_TYPE", admType);
			parm.setData("FLG", 1);// 退费
			parm.setData("CASE_NO", result.getValue("CASE_NO", 0));
			parm.setData("LAB_NO", result.getValue("MED_APPLY_NO", 0));
			parm.setData("CAT1_TYPE", result.getValue("CAT1_TYPE", 0));

			list.add(parm);
		}
		// System.out.println("发送接口项目:"+list);
		// 调用接口
		TParm resultParm = Hl7Communications.getInstance().Hl7Message(list);
		// System.out.println("resultParm::::"+resultParm);
		if (resultParm.getErrCode() < 0) {
			this.messageBox(resultParm.getErrText());
		}
	}

	/**
	 * 医疗卡退费操作
	 * 
	 * @param parm
	 * @param actionParm
	 * @return
	 */
	private TParm ektResetFee(TParm actionParm) {
		TParm result = new TParm();
		//flg = true;// 判断退费方式
		actionParm.setData("REGION_CODE", Operator.getRegion());
		actionParm.setData("CASE_NO", opb.getReg().caseNo());
		actionParm.setData("MR_NO", opb.getPat().getMrNo());
		if (actionParm.getDouble("REDUCE_AMT")>0) {
			String sql="SELECT SUM(PAY_OTHER3) PAY_OTHER3,SUM(PAY_OTHER4) PAY_OTHER4 FROM EKT_TRADE A,OPD_ORDER B " +
					"WHERE A.TRADE_NO=B.BUSINESS_NO AND B.CASE_NO='"+opb.getReg().caseNo()+"' AND B.RECEIPT_NO='"
					+actionParm.getValue("RECEIPT_NO")+"' AND A.STATE='1'";
			TParm tradeParm=new TParm(TJDODBTool.getInstance().select(sql));
			double reduce=tradeParm.getDouble("PAY_OTHER3",0)-actionParm.getDouble("PAY_OTHER3")+
			tradeParm.getDouble("PAY_OTHER4",0)-actionParm.getDouble("PAY_OTHER4");
			actionParm.setData("REDUCELAST_AMT",reduce);//====pangben 减免金额剩余金额
			actionParm.setData("USED_LPK",tradeParm.getDouble("PAY_OTHER3",0));//使用的礼品卡金额
			actionParm.setData("USED_XJZKQ",tradeParm.getDouble("PAY_OTHER4",0));//使用的代金券金额
		}else{
			actionParm.setData("REDUCELAST_AMT",0);
		}
		// 医疗卡退费
		result = TIOM_AppServer.executeAction("action.opb.OPBAction",
				"backEKTOPBRecp", actionParm);
		return result;
	}

	/**
	 * 撤销操作
	 * 
	 * @param parm
	 */
	private void concelResetFee(TParm parm) {
		// 医疗卡退费操作失败回冲数据
		TParm writeParm = new TParm();
		writeParm.setData("CURRENT_BALANCE", parm.getValue("OLD_AMT"));
		writeParm.setData("MR_NO", parmEKT.getValue("MR_NO"));
		writeParm.setData("SEQ", parmEKT.getValue("SEQ"));
		try {
			writeParm = EKTIO.getInstance().TXwriteEKTATM(writeParm,
					parmEKT.getValue("MR_NO"));
			// 回写医疗卡金额
			if (writeParm.getErrCode() < 0)
				System.out.println("err:" + writeParm.getErrText());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("err:医疗卡写卡动作失败");
			e.printStackTrace();
		} 
		
		TParm concelParm = new TParm();
		concelParm.setData("TREDE_NO", parm.getValue("TREDE_NO"));// 医疗卡收据号
		concelParm.setData("BUSINESS_NO", parm.getValue("BUSINESS_NO"));// 医疗卡交易号
		writeParm = concelEKT(concelParm);
		if (writeParm.getErrCode() < 0) {
			System.out.println("医疗卡退费撤销医疗卡操作失败");
		} else {
			System.out.println("医疗卡退费撤销医疗卡操作成功");
		}
	}

	/**
	 * 撤销医疗卡操作
	 * 
	 * @return
	 */
	private TParm concelEKT(TParm parm) {
		TParm result = TIOM_AppServer.executeAction("action.ekt.EKTAction",
				"deleteRegOldData", parm);
		return result;
		// EKTTool.getInstance().deleteTrede(parm, connection);
	}

	/**
	 * 票据补打
	 * 
	 * @param row
	 *            int
	 * @return boolean
	 */
	public boolean onSaveRePrint(int row) {
		TParm saveRePrintParm = getRePrintData(row);
		if (saveRePrintParm == null)
			return false;
		// 调用opbaction
		TParm result = null;
		if (ektParm.getCount("CASE_NO") > 0) {
			// 医疗卡补打
			// COUNT
			saveRePrintParm.setData("COUNT", ektParm.getCount("CASE_NO"));
			result = TIOM_AppServer.executeAction("action.opb.OPBAction",
					"saveOPBEKTRePrint", saveRePrintParm);

		} else {
			// 现金补打
			saveRePrintParm.setData("COUNT", -1);
			result = TIOM_AppServer.executeAction("action.opb.OPBAction",
					"saveOPBRePrint", saveRePrintParm);
		}
		printNoOnly = result.getValue("PRINT_NO");
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return false;
		}

		return true;
	}

	/**
	 * 得到补印数据
	 * 
	 * @param row
	 *            int
	 * @return TParm
	 */
	public TParm getRePrintData(int row) {
		TParm saveParm = tableM.getParmValue();
		TParm actionParm = saveParm.getRow(row);
		actionParm.setData("OPT_USER", Operator.getID());
		actionParm.setData("OPT_TERM", Operator.getIP());
		actionParm.setData("OPT_DATE", SystemTool.getInstance().getDate());
		// System.out.println("actionParm"+actionParm);
		actionParm.setData("ADM_TYPE", opb.getReg().getAdmType());
		return actionParm;

	}

	/**
	 * 医保获取数据
	 * 
	 * @return
	 */
	private void getInsValue(TParm resultParm, TParm sumParm,String printNo) {
		TParm insFeeParm = new TParm();
		insFeeParm.setData("CASE_NO", opb.getReg().caseNo());// 退挂使用
		insFeeParm.setData("RECP_TYPE", "OPB");// 收费使用
		// insFeeParm.setData("CONFIRM_NO", resultParm.getValue("CONFIRM_NO",
		// 0));// 医保就诊号
		// ---写死了需要修改
		insFeeParm.setData("NAME", opb.getPat().getName());
		insFeeParm.setData("MR_NO", opb.getPat().getMrNo());// 病患号
		if (ektParm.getCount("CASE_NO") > 0) {
			insFeeParm.setData("PAY_TYPE", true);// 支付方式
		} else {
			insFeeParm.setData("PAY_TYPE", false);// 支付方式
		}
		TParm result = INSMZConfirmTool.getInstance().queryMZConfirmOne(
				insFeeParm);// 查询医保信息
		if (result.getErrCode() < 0 || result.getCount() <= 0) {
			return;
		}
		if (cashFLg) {
			this.messageBox("印刷号:"+printNo+" 医保退费金额:"
					+ StringTool.round(sumParm.getDouble("PAY_INS_CARD", 0), 2)
					+ ","
					+ type
					+ "扣款:"
					+ (StringTool.round(sumParm.getDouble("TOT_AMT", 0)
							- sumParm.getDouble("PAY_INS_CARD", 0), 2))
					+ ",请注意");
		} else {
			this.messageBox("印刷号:"+printNo+" 医保退费金额:"
					+ StringTool.round(sumParm.getDouble("PAY_INS_CARD", 0), 2)
					+ ","
					+ type
					+ "扣款:"
					+ (StringTool.round(sumParm.getDouble("TOT_AMT", 0)
							- sumParm.getDouble("PAY_INS_CARD", 0), 2)));

		}
		nhiRegionCode = result.getValue("REGION_CODE", 0);
		if (result.getInt("INS_CROWD_TYPE", 0) == 1
				&& result.getInt("INS_PAT_TYPE", 0) == 1) {
			resultParm.setData("INS_TYPE", "1");// 医保就医类别
		} else if (result.getInt("INS_CROWD_TYPE", 0) == 1
				&& result.getInt("INS_PAT_TYPE", 0) == 2) {
			resultParm.setData("INS_TYPE", "2");// 医保就医类别
		} else if (result.getInt("INS_CROWD_TYPE", 0) == 2
				&& result.getInt("INS_PAT_TYPE", 0) == 2) {
			resultParm.setData("INS_TYPE", "3");// 医保就医类别
		}
	}

	/**
	 * 医保退费操作
	 */
	private boolean reSetInsSave(TParm opbParm) {

		// 查询医保金额
		String sql = "SELECT PAY_INS_CARD,TOT_AMT,RECEIPT_NO FROM BIL_OPB_RECP WHERE PRINT_NO='"
				+ opbParm.getValue("PRINT_NO") + "'";
		TParm bilParm = new TParm(TJDODBTool.getInstance().select(sql));

		// 医疗卡操作
		if (opbParm.getDouble("PAY_MEDICAL_CARD")
				+ opbParm.getDouble("PAY_OTHER1") > 0 || opbParm.getDouble("PAY_INS_CARD") > 0
				&& (opbParm.getDouble("PAY_MEDICAL_CARD")
				+ opbParm.getDouble("PAY_OTHER1")) == 0&& opbParm.getDouble("PAY_CASH") == 0) {
			if (null == parmEKT || parmEKT.getErrCode() < 0) {
				parmEKT = EKTIO.getInstance().TXreadEKT();
				if (null == parmEKT || parmEKT.getErrCode() < 0
						|| parmEKT.getValue("MR_NO").length() <= 0) {
					this.messageBox("请读医疗卡");
					return false;
				}
			}
			if (!parmEKT.getValue("MR_NO").equals(opb.getPat().getMrNo())) {
				this.messageBox("医疗卡信息与此病患不符");
				return false;
			}
			if (parmEKT.getDouble("CURRENT_BALANCE") < bilParm.getDouble(
					"PAY_INS_CARD", 0)) {
				this.messageBox("医疗卡中金额小于医保退费金额,不能执行医疗卡退费操作");
				return false;
			}
		}
		TParm reSetInsParm = new TParm();
		TParm parm = new TParm();
		// System.out.println("---------------医保退费-------------:" + opbParm);
		parm.setData("CASE_NO", opb.getReg().caseNo());
		parm.setData("INV_NO", opbParm.getValue("PRINT_NO"));
		parm.setData("RECP_TYPE", "OPB");// 收费类型

		// 查询是否医保 退费
		TParm result = INSOpdTJTool.getInstance().selectInsInvNo(parm);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			return false;
		}
		if (result.getCount("CASE_NO") <= 0) {// 不是医保退费
			return true;
		}

		// // 查询医保退费金额
		// TParm sumParm = INSOpdTJTool.getInstance().selectInsSumAmt(parm);
		// if (sumParm.getErrCode() < 0) {
		// this.messageBox("E0005");
		// return false;
		// }
		// 医保卡退费 需要修改医疗卡参数
		if (null == opb.getReg().caseNo()
				&& opb.getReg().caseNo().length() <= 0) {
			this.messageBox("E0005");
			return false;
		}

		// int returnType = insExeFee(opbParm, result, false);
		// if (returnType == 0) {// 取消
		// System.out.println("医保操作失败");
		// return false;
		// }
		getInsValue(result, bilParm,opbParm.getValue("PRINT_NO"));
		// ====zhangp 20120229
		insType = result.getInt("INS_TYPE");
		// =====zhangp 20120229
		reSetInsParm.setData("CASE_NO", opb.getReg().caseNo());// 就诊号
		reSetInsParm.setData("CONFIRM_NO", result.getValue("CONFIRM_NO", 0));// 医保就诊号
		reSetInsParm.setData("INS_TYPE", result.getValue("INS_TYPE"));// 医保就医类型
		reSetInsParm.setData("RECP_TYPE", "OPB");// 收费类型
		reSetInsParm.setData("UNRECP_TYPE", "OPBT");// 退费类型
		reSetInsParm.setData("OPT_USER", Operator.getID());// id
		reSetInsParm.setData("OPT_TERM", Operator.getIP());// ip
		reSetInsParm.setData("REGION_CODE", nhiRegionCode);// 医保区域代码
		reSetInsParm.setData("INV_NO", opbParm.getValue("PRINT_NO"));// 印刷号
		reSetInsParm.setData("PAT_TYPE", this.getValue("CTZ1_CODE"));// 身份
		reSetInsParm.setData("REGION_CODE", regionParm.getValue("NHI_NO", 0));// 身份

		// System.out.println("reSetInsParm:::::::" + reSetInsParm);
		result = INSTJReg.getInstance().insResetCommFunction(
				reSetInsParm.getData());

		if (result.getErrCode() < 0) {
			this.messageBox(result.getErrText());
			return false;
		}
		insFlg = true;// 医保操作
		if (opbParm.getDouble("PAY_MEDICAL_CARD")
				+ opbParm.getDouble("PAY_OTHER1") > 0 || opbParm.getDouble("PAY_INS_CARD") > 0
				&& (opbParm.getDouble("PAY_MEDICAL_CARD")
				+ opbParm.getDouble("PAY_OTHER1")) == 0&& opbParm.getDouble("PAY_CASH") == 0) {

			TParm opdParm = new TParm();
			opdParm.setData("NAME", opb.getPat().getName());
			opdParm.setData("SEX", opb.getPat().getSexCode().equals("1") ? "男"
					: "女");
			opdParm.setData("AMT", bilParm.getDouble("PAY_INS_CARD", 0));
			opdParm.setData("INS_FLG", "Y"); // 医保使用
			// 需要修改的地方
			opdParm.setData("MR_NO", opb.getPat().getMrNo());
			result = insExeUpdate(-bilParm.getDouble("PAY_INS_CARD", 0),
					bilParm.getDouble("TOT_AMT", 0), opb.getReg().caseNo(), "OPB",bilParm.getValue("RECEIPT_NO", 0));

			// result = EKTIO.getInstance().insUnFee(opdParm, this);
			// System.out.println("修result::::::::"+result);
			if (result.getErrCode() < 0) {
				this.messageBox(result.getErrText());
				return false;
			}
		}
		return true;
	}

	/**
	 * 删除医保在途状态
	 * 
	 * @return
	 */
	public boolean deleteInsRun(String caseNo) {
		if (null == caseNo && caseNo.length() <= 0) {
			return false;
		}
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		parm.setData("EXE_USER", Operator.getID());
		parm.setData("EXE_TERM", Operator.getIP());
		parm.setData("EXE_TYPE", "OPBT");
		TParm result = INSRunTool.getInstance().deleteInsRun(parm);
		// System.out.println("在途状态数据返回：" + result);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			result.setErr(-1, "医保卡执行操作失败");
			return false;
		}
		return true;
	}

	/**
	 * 医疗卡操作保存此次医保卡扣款金额
	 * 
	 * @param returnParm
	 * @return
	 */
	private TParm insExeUpdate(double accountAmt, double totAmt,
			String caseNo, String business_type,String receiptNo) {
		// 入参:AMT:本次操作金额 BUSINESS_TYPE :本次操作类型 CASE_NO:就诊号码
		TParm orderParm = new TParm();
		orderParm.setData("AMT", -accountAmt);
		orderParm.setData("BUSINESS_TYPE", business_type);
		orderParm.setData("CASE_NO", caseNo);
		orderParm.setData("RECEIPT_NO", receiptNo);
		orderParm.setData("CURRENT_BALANCE",parmEKT.getDouble("CURRENT_BALANCE"));
		orderParm.setData("OPT_USER", Operator.getID());
		orderParm.setData("OPT_TERM", Operator.getIP());
		orderParm.setData("EXE_FLG", "Y");
		orderParm.setData("ID_NO", parmEKT.getValue("IDNO"));
		orderParm.setData("CARD_NO", parmEKT.getValue("PK_CARD_NO"));// 卡号
		orderParm.setData("MR_NO", parmEKT.getValue("MR_NO"));// 病案号
		orderParm.setData("PAT_NAME", parmEKT.getValue("PAT_NAME"));// 病患名称
		orderParm.setData("EKT_USE", totAmt);// 总金额
		orderParm.setData("EKT_OLD_AMT",parmEKT.getDouble("CURRENT_BALANCE"));// 原来金额
		TParm insExeParm = TIOM_AppServer.executeAction("action.ekt.EKTAction",
				"exeInsSave", orderParm);
		return insExeParm;

	}
	/**
	 * 取得带三目字典的清单数据
	 * ======zhangp 20120925
	 * @param orderParm
	 * @return
	 */
	private TParm getInsRuleParm(TParm orderParm){
		String sql =
			" SELECT   A.CASE_NO, A.RX_NO, A.SEQ_NO, A.OPT_USER, A.OPT_DATE, A.OPT_TERM," +
			" A.PRESRT_NO, A.REGION_CODE, A.MR_NO, A.ADM_TYPE, A.RX_TYPE," +
			" A.TEMPORARY_FLG, A.RELEASE_FLG, A.LINKMAIN_FLG, A.LINK_NO," +
			" A.ORDER_CODE," +
			" A.ORDER_DESC" +
			//delete by huangtt 20141124
//			" || CASE" +
//			" WHEN TRIM (D.MAN_CODE) IS NOT NULL" +
//			" OR TRIM (D.MAN_CODE) <> ''" +
//			" THEN '*' || D.MAN_CODE" +
//			" ELSE ''" +
//			" END" +
			" AS ORDER_DESC," +
			" A.SPECIFICATION, A.GOODS_DESC, A.ORDER_CAT1_CODE, A.MEDI_QTY," +
			" A.MEDI_UNIT, A.FREQ_CODE, A.ROUTE_CODE, A.TAKE_DAYS, A.DOSAGE_QTY," +
			" A.DOSAGE_UNIT, A.DISPENSE_QTY, A.DISPENSE_UNIT, A.GIVEBOX_FLG," +
			//modigy by huangtt 20141125 start
//			" A.OWN_PRICE, " +
			" CASE " +
			" WHEN A.DISPENSE_UNIT <> A.DOSAGE_UNIT" +
			" THEN A.DOSAGE_QTY/A.DISPENSE_QTY*A.OWN_PRICE" +
			" ELSE A.OWN_PRICE" +
			" END AS OWN_PRICE, " +
			//modigy by huangtt 20141125 end
			" A.NHI_PRICE, A.DISCOUNT_RATE, A.OWN_AMT, A.AR_AMT," +
			" A.DR_NOTE, A.NS_NOTE, A.DR_CODE, A.ORDER_DATE, A.DEPT_CODE," +
			" A.DC_DR_CODE, A.DC_ORDER_DATE, A.DC_DEPT_CODE, A.EXEC_DEPT_CODE," +
			" A.SETMAIN_FLG, A.ORDERSET_GROUP_NO, A.ORDERSET_CODE, A.HIDE_FLG," +
			" A.RPTTYPE_CODE, A.OPTITEM_CODE, A.DEV_CODE, A.MR_CODE, A.FILE_NO," +
			" A.DEGREE_CODE, A.URGENT_FLG, A.INSPAY_TYPE, A.PHA_TYPE, A.DOSE_TYPE," +
			" A.PRINTTYPEFLG_INFANT, A.EXPENSIVE_FLG, A.CTRLDRUGCLASS_CODE," +
			" A.PRESCRIPT_NO, A.ATC_FLG, A.SENDATC_DATE, A.RECEIPT_NO, A.BILL_FLG," +
			" A.BILL_DATE, A.BILL_USER, A.PRINT_FLG, A.REXP_CODE, A.HEXP_CODE," +
			" A.CONTRACT_CODE, A.CTZ1_CODE, A.CTZ2_CODE, CTZ3_CODE," +
			" A.PHA_CHECK_CODE, A.PHA_CHECK_DATE, A.PHA_DOSAGE_CODE," +
			" A.PHA_DOSAGE_DATE, A.PHA_DISPENSE_CODE, A.PHA_DISPENSE_DATE," +
			" A.NS_EXEC_CODE, A.NS_EXEC_DATE, A.NS_EXEC_DEPT, A.DCTAGENT_CODE," +
			" A.DCTEXCEP_CODE, A.DCT_TAKE_QTY, A.PACKAGE_TOT, A.AGENCY_ORG_CODE," +
			" A.DCTAGENT_FLG, A.DECOCT_CODE, A.EXEC_FLG, A.RECEIPT_FLG," +
			" A.CAT1_TYPE, A.MED_APPLY_NO, A.BILL_TYPE, A.PHA_RETN_CODE," +
			" F.DOSE_CHN_DESC, '0.0%' AS AL, A.BUSINESS_NO,A.MEM_PACKAGE_ID" +
			" FROM OPD_ORDER A," +
			" SYS_FEE D," +
			" PHA_BASE E," +
			" PHA_DOSE F" +
			" WHERE A.ORDER_CODE = D.ORDER_CODE(+)" +
			" AND A.ORDER_CODE = E.ORDER_CODE(+)" +
			" AND E.DOSE_CODE = F.DOSE_CODE(+)" +
			" AND CASE_NO = '" + orderParm.getValue("CASE_NO") + "'" +
			" AND A.RECEIPT_NO = '" + orderParm.getValue("RECEIPT_NO") + "'" +
			" AND ( A.BILL_FLG = 'Y' OR A.MEM_PACKAGE_ID IS NOT NULL ) " +  // modify by huangtt 20141216
			" ORDER BY REXP_CODE,RX_TYPE, RX_NO, SEQ_NO";//yanjing 添加rexp_code 20150226
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
//		System.out.println("ssssss = " + sql);
		if(result.getErrCode()<0){
			System.out.println(result.getErrText());
		}
		return result;
	}
	
	/**
	 * 取得带英文三目字典的清单数据
	 * by lich
	 */
	private TParm getInsRuleParmEng(TParm orderParm){
		String sql =
			" SELECT   A.CASE_NO, A.RX_NO, A.SEQ_NO, A.OPT_USER, A.OPT_DATE, A.OPT_TERM," +
			" A.PRESRT_NO, A.REGION_CODE, A.MR_NO, A.ADM_TYPE, A.RX_TYPE," +
			" A.TEMPORARY_FLG, A.RELEASE_FLG, A.LINKMAIN_FLG, A.LINK_NO," +
			" A.ORDER_CODE," +
			" A.ORDER_DESC" +
			//delete by huangtt 20141124
//			" || CASE" +
//			" WHEN TRIM (D.MAN_CODE) IS NOT NULL" +
//			" OR TRIM (D.MAN_CODE) <> ''" +
//			" THEN '*' || D.MAN_CODE" +
//			" ELSE ''" +
//			" END" +
			" AS ORDER_DESC," +
			" A.SPECIFICATION, A.GOODS_DESC, A.ORDER_CAT1_CODE, A.MEDI_QTY," +
			" A.MEDI_UNIT, A.FREQ_CODE, A.ROUTE_CODE, A.TAKE_DAYS, A.DOSAGE_QTY," +
			" A.DOSAGE_UNIT, A.DISPENSE_QTY, A.DISPENSE_UNIT, A.GIVEBOX_FLG," +
			//modigy by huangtt 20141125 start
//			" A.OWN_PRICE, " +
			" CASE " +
			" WHEN A.DISPENSE_UNIT <> A.DOSAGE_UNIT" +
			" THEN A.DOSAGE_QTY/A.DISPENSE_QTY*A.OWN_PRICE" +
			" ELSE A.OWN_PRICE" +
			" END AS OWN_PRICE, " +
			//modify by huangtt 20141125 end
			" A.NHI_PRICE, A.DISCOUNT_RATE, A.OWN_AMT, A.AR_AMT," +
			" A.DR_NOTE, A.NS_NOTE, A.DR_CODE, A.ORDER_DATE, A.DEPT_CODE," +
			" A.DC_DR_CODE, A.DC_ORDER_DATE, A.DC_DEPT_CODE, A.EXEC_DEPT_CODE," +
			" A.SETMAIN_FLG, A.ORDERSET_GROUP_NO, A.ORDERSET_CODE, A.HIDE_FLG," +
			" A.RPTTYPE_CODE, A.OPTITEM_CODE, A.DEV_CODE, A.MR_CODE, A.FILE_NO," +
			" A.DEGREE_CODE, A.URGENT_FLG, A.INSPAY_TYPE, A.PHA_TYPE, A.DOSE_TYPE," +
			" A.PRINTTYPEFLG_INFANT, A.EXPENSIVE_FLG, A.CTRLDRUGCLASS_CODE," +
			" A.PRESCRIPT_NO, A.ATC_FLG, A.SENDATC_DATE, A.RECEIPT_NO, A.BILL_FLG," +
			" A.BILL_DATE, A.BILL_USER, A.PRINT_FLG, A.REXP_CODE, A.HEXP_CODE," +
			" A.CONTRACT_CODE, A.CTZ1_CODE, A.CTZ2_CODE, CTZ3_CODE," +
			" A.PHA_CHECK_CODE, A.PHA_CHECK_DATE, A.PHA_DOSAGE_CODE," +
			" A.PHA_DOSAGE_DATE, A.PHA_DISPENSE_CODE, A.PHA_DISPENSE_DATE," +
			" A.NS_EXEC_CODE, A.NS_EXEC_DATE, A.NS_EXEC_DEPT, A.DCTAGENT_CODE," +
			" A.DCTEXCEP_CODE, A.DCT_TAKE_QTY, A.PACKAGE_TOT, A.AGENCY_ORG_CODE," +
			" A.DCTAGENT_FLG, A.DECOCT_CODE, A.EXEC_FLG, A.RECEIPT_FLG," +
			" A.CAT1_TYPE, A.MED_APPLY_NO, A.BILL_TYPE, A.PHA_RETN_CODE," +
			" F.DOSE_CHN_DESC, '0.0%' AS AL, A.BUSINESS_NO ,G.ENG_DESC ,D.TRADE_ENG_DESC,A.MEM_PACKAGE_ID" +
			" FROM OPD_ORDER A," +
			" SYS_FEE D," +
			" PHA_BASE E," +
			" PHA_DOSE F, " +
			" (select * from SYS_DICTIONARY where group_id='SYS_CHARGE') G"+
			" WHERE A.ORDER_CODE = D.ORDER_CODE(+)" +
			" AND A.ORDER_CODE = E.ORDER_CODE(+)" +
			" AND E.DOSE_CODE = F.DOSE_CODE(+)" +
			" AND CASE_NO = '" + orderParm.getValue("CASE_NO") + "'" +
			" AND A.RECEIPT_NO = '" + orderParm.getValue("RECEIPT_NO") + "'" +
			" AND ( A.BILL_FLG = 'Y' OR A.MEM_PACKAGE_ID IS NOT NULL ) " +
			" AND G.ID = A.REXP_CODE "+
			" ORDER BY REXP_CODE,RX_TYPE, RX_NO, SEQ_NO";//====yanjing 20150227
//		System.out.println("sql========"+sql);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
//		System.out.println("result:::::"+result);
		if(result.getErrCode()<0){
			System.out.println(result.getErrText());
		}
		return result;
	}
	
	
	/**
	 * 费用查询 add by sunqy 20140708
	 */
	public void onBackReceipt(){
		DecimalFormat df = new DecimalFormat("##########0.00");
		if (opb == null)
			return;
		
		boolean engFlag = this.getValueBoolean("ENG_FLAG");//ENG_FLAG为英文单选按钮
		String sql4=" SELECT m.UNIT_ENG_DESC,m.CONTRACTOR_DESC,n.INSURANCE_NUMBER,n.INSURANCE_BILL_NUMBER " +
 		"FROM MEM_CONTRACTOR m,  MEM_INSURE_INFO n WHERE n.MR_NO='"+opb.getPat().getMrNo()+
 		"' AND m.CONTRACTOR_CODE=n.CONTRACTOR_CODE  AND n.VALID_FLG='Y'";
		
		TParm resultIns = new TParm(TJDODBTool.getInstance().select(sql4));//得到保险相关信息
		
		//add by huangtt 20150824 start 判断opd_order中是否包含套餐
		String memSql = "SELECT MEM_PACKAGE_ID FROM OPD_ORDER WHERE " +
				" MEM_PACKAGE_ID IS NOT NULL AND MEM_PACKAGE_FLG='Y' " +
				" AND CASE_NO = '"+opb.getReg().caseNo()+"'";
		TParm memParm = new TParm(TJDODBTool.getInstance().select(memSql));
		String memPackageFlg="";
		if(memParm.getCount()>0){
			memPackageFlg="Y";
		}else{
			memPackageFlg="N";
		}
		//add by huangtt 20150824 end
		
		/**
		 * modified by lich 
		 * 添加英文打印
		 */
		if(engFlag){
			String sql =
				" SELECT   A.CASE_NO, A.RX_NO, A.SEQ_NO, A.OPT_USER, A.OPT_DATE, A.OPT_TERM," +
				" A.PRESRT_NO, A.REGION_CODE, A.MR_NO, A.ADM_TYPE, A.RX_TYPE," +
				" A.TEMPORARY_FLG, A.RELEASE_FLG, A.LINKMAIN_FLG, A.LINK_NO," +
				" A.ORDER_CODE," +
				" A.ORDER_DESC" +
				//delete by huangtt 20141124
//				" || CASE" +
//				" WHEN TRIM (D.MAN_CODE) IS NOT NULL" +
//				" OR TRIM (D.MAN_CODE) <> ''" +
//				" THEN '*' || D.MAN_CODE" +
//				" ELSE ''" +
//				" END" +
				" AS ORDER_DESC," +
				" A.SPECIFICATION, A.GOODS_DESC, A.ORDER_CAT1_CODE, A.MEDI_QTY," +
				" A.MEDI_UNIT, A.FREQ_CODE, A.ROUTE_CODE, A.TAKE_DAYS, A.DOSAGE_QTY," +
				" A.DOSAGE_UNIT, A.DISPENSE_QTY, A.DISPENSE_UNIT, A.GIVEBOX_FLG," +
				//modigy by huangtt 20141125 start
				" CASE " +
				" WHEN A.DISPENSE_UNIT <> A.DOSAGE_UNIT" +
				" THEN A.DOSAGE_QTY/A.DISPENSE_QTY*A.OWN_PRICE" +
				" ELSE A.OWN_PRICE" +
				" END AS OWN_PRICE, " +
				//modigy by huangtt 20141125 end
				" A.NHI_PRICE, A.DISCOUNT_RATE, A.OWN_AMT, A.AR_AMT," +
				" A.DR_NOTE, A.NS_NOTE, A.DR_CODE, A.ORDER_DATE, A.DEPT_CODE," +
				" A.DC_DR_CODE, A.DC_ORDER_DATE, A.DC_DEPT_CODE, A.EXEC_DEPT_CODE," +
				" A.SETMAIN_FLG, A.ORDERSET_GROUP_NO, A.ORDERSET_CODE, A.HIDE_FLG," +
				" A.RPTTYPE_CODE, A.OPTITEM_CODE, A.DEV_CODE, A.MR_CODE, A.FILE_NO," +
				" A.DEGREE_CODE, A.URGENT_FLG, A.INSPAY_TYPE, A.PHA_TYPE, A.DOSE_TYPE," +
				" A.PRINTTYPEFLG_INFANT, A.EXPENSIVE_FLG, A.CTRLDRUGCLASS_CODE," +
				" A.PRESCRIPT_NO, A.ATC_FLG, A.SENDATC_DATE, A.RECEIPT_NO, A.BILL_FLG," +
				" A.BILL_DATE, A.BILL_USER, A.PRINT_FLG, A.REXP_CODE, A.HEXP_CODE," +
				" A.CONTRACT_CODE, A.CTZ1_CODE, A.CTZ2_CODE, CTZ3_CODE," +
				" A.PHA_CHECK_CODE, A.PHA_CHECK_DATE, A.PHA_DOSAGE_CODE," +
				" A.PHA_DOSAGE_DATE, A.PHA_DISPENSE_CODE, A.PHA_DISPENSE_DATE," +
				" A.NS_EXEC_CODE, A.NS_EXEC_DATE, A.NS_EXEC_DEPT, A.DCTAGENT_CODE," +
				" A.DCTEXCEP_CODE, A.DCT_TAKE_QTY, A.PACKAGE_TOT, A.AGENCY_ORG_CODE," +
				" A.DCTAGENT_FLG, A.DECOCT_CODE, A.EXEC_FLG, A.RECEIPT_FLG," +
				" A.CAT1_TYPE, A.MED_APPLY_NO, A.BILL_TYPE, A.PHA_RETN_CODE," +
				" F.DOSE_CHN_DESC, '0.0%' AS AL, A.BUSINESS_NO ,G.ENG_DESC ,D.TRADE_ENG_DESC" +
				" FROM OPD_ORDER A," +
				" SYS_FEE D," +
				" PHA_BASE E," +
				" PHA_DOSE F, " +
				" (select * from SYS_DICTIONARY where group_id='SYS_CHARGE') G"+
				" WHERE A.ORDER_CODE = D.ORDER_CODE(+)" +
				" AND A.ORDER_CODE = E.ORDER_CODE(+)" +
				" AND E.DOSE_CODE = F.DOSE_CODE(+)" +
				" AND CASE_NO = '" + caseNo + "'" +
				" AND G.ID = A.REXP_CODE "+
				" AND A.MEM_PACKAGE_ID IS NULL" +
				" AND A.RELEASE_FLG <> 'Y'" + //自备药不显示 add by huangjw  20160530
				" ORDER BY REXP_CODE,RX_TYPE, RX_NO, SEQ_NO";//===yanjing 20150227
			
			//查询保险信息SQL
				
			TParm result = new TParm(TJDODBTool.getInstance().select(sql));
				
			int count = result.getCount("SETMAIN_FLG");
			String setmainFlg = "";
			for (int i = count - 1; i >= 0; i--) {
				setmainFlg = result.getValue("SETMAIN_FLG", i);// 集合医嘱注记
				if ("Y".equals(setmainFlg)) {
					result.removeRow(i);
				}
			}
			// System.out.println("batchNoParm:::"+batchNoParm);
			TParm parm = opb.getReceiptList().dealTParmEng(result, batchNoParm);
			if (parm == null)
				return;
			double sumTotS = parm.getDouble("SUMTOTS");// 合计应收金额
			double sumTotR = parm.getDouble("SUMTOTR");// 合计实收金额
			TParm pringListParm = new TParm();
			// 病患姓名
			pringListParm.setData("TABLE", parm.getData());
			pringListParm.setData("MR_NO", "TEXT", opb.getPat().getMrNo());//病案号
			pringListParm.setData("PAT_NAME", "TEXT", opb.getPat().getName());// 患者姓名
			pringListParm
			.setData("HOSP", "TEXT", Operator.getHospitalCHNFullName());// 医院名称
//			pringListParm.setData("TITLE", "TEXT", Operator
//					.getHospitalCHNFullName());// 医院名称
			pringListParm.setData("DATE", "TEXT", StringTool.getString(TypeTool.getTimestamp(bill_date), "yyyy/MM/dd HH:mm:ss"));// 收费日期
			pringListParm.setData("PRINT_NO", "TEXT", printNo);// 票号
			String yMd = StringTool.getString(TypeTool.getTimestamp(TJDODBTool
					.getInstance().getDBTime()), "yyyy/MM/dd"); // 年月日
			pringListParm.setData("NOW", "TEXT", yMd);// 日期
			pringListParm.setData("OP_NAME", "TEXT", Operator.getID());//制表人
			pringListParm.setData("TOT_AMT1","TEXT" ,new java.text.DecimalFormat("###,##0.00").format(sumTotR));
			pringListParm.setData("Birthday","TEXT" ,opb.getPat().getBirthday().toString().replaceAll("-", "/").substring(0, 10));
			pringListParm.setData("InsCom","TEXT", resultIns.getValue("CONTRACTOR_DESC",0));//保险公司名称
			pringListParm.setData("InsNum","TEXT", resultIns.getValue("INSURANCE_NUMBER",0));//保险号
			// System.out.println("parm==============" + parm);

			/**add by sunqy 20140630 门诊费用清单以及门诊套餐费用清单*/
//			TParm receptParm = (TParm)this.getParameter();
////			System.out.println("receptParm===="+receptParm);
//			String memPackageFlg = receptParm.getValue("MEM_PACKAGE_FLG");//由OPBChargeM处套餐CHECK_BOX处事件得到
//			System.out.println("################memPackageFlg"+memPackageFlg);
			
								//add by lich 增加英文医嘱字段TRADE_ENG_DESC  PACKAGE_CODE 20141010
			String packageSql = "SELECT DISTINCT A.SEQ_NO, D.PACKAGE_DESC, D.PACKAGE_CODE, A.AR_AMT AS RETAIL_PRICE, " +
					" C.ORDER_DESC, D.ORDER_NUM, B.UNIT_CHN_DESC, B.UNIT_ENG_DESC, D.TRADE_ENG_DESC, A.SETMAIN_FLG " +
					" FROM OPD_ORDER A, SYS_UNIT B, MEM_PACKAGE_SECTION_D C,MEM_PAT_PACKAGE_SECTION_D D " +
					" WHERE A.CASE_NO = '"+ caseNo +"' AND D.ID = A.MEM_PACKAGE_ID " +
					" AND A.MEDI_UNIT = B.UNIT_CODE AND A.ORDER_CODE = C.ORDER_CODE ORDER BY D.PACKAGE_CODE,A.SEQ_NO";
			//System.out.println("SQL--------"+packageSql);
			TParm packResult = new TParm(TJDODBTool.getInstance().select(packageSql));
			
			packResult.setData("engFlag", engFlag);////是否是英文打印判断
//			System.out.println("RESULT------"+packResult);
			if("N".equals(memPackageFlg)||"".equals(memPackageFlg)||memPackageFlg==null){//无套餐情况
				pringListParm.setData("TABLEORDER", parm.getData());
				if (opb.getReg().getAdmType().equals("O")) {
					pringListParm.setData("TITLE", "TEXT", "门诊费用明细清单");
				}else{
					pringListParm.setData("TITLE", "TEXT", "急诊费用明细清单");
				}
//				pringListParm.setData("SUMTOTS", df.format(StringTool.round(sumTotS,2)));// 合计应收金额
//				pringListParm.setData("SUMTOTR", df.format(StringTool.round(sumTotR,2)));// 合计实收金额
				pringListParm.setData("TOT_AMT", df.format(StringTool.round(sumTotS,2)));// 合计应收金额
				pringListParm.setData("OWN_AMT", df.format(StringTool.round(sumTotR,2)));// 合计实收金额
				pringListParm.setData("SUMTOTS", df.format(StringTool.round(sumTotS,2)));// 合计应收金额
				pringListParm.setData("SUMTOTR", df.format(StringTool.round(sumTotR,2)));// 合计实收金额
				this.openPrintDialog(IReportTool.getInstance().getReportPath("OPDFeeListEnV45.jhw"),
						IReportTool.getInstance().getReportParm("OPDFeeListV45.class", pringListParm));//报表合并  modify by sunqy 20140620
			}else if("Y".equals(memPackageFlg)){//有套餐情况
//				System.out.println("!!!--------enter package situation-------!!!");
				String mainFlg = "";// 集合医嘱注记
				double sumPackage = 0.00;//套餐价格
				for (int i = packResult.getCount() - 1; i >= 0; i--) {
					mainFlg = packResult.getValue("SETMAIN_FLG", i);
					if ("Y".equals(mainFlg)) {//如果是集合医嘱就去掉集合医嘱项，只显示细项
						packResult.removeRow(i);
					}
					sumPackage += packResult.getDouble("RETAIL_PRICE", i);
				}
				TParm packageParm = OPBPackageListControl.getInstance().listParm(packResult);
				
//				System.out.println("packageParmEng-+++++++"+packageParm);
				/*------------ 增加报表英文信息 add by lich----20141009----start*/
				packageParm.setData("engFlag", engFlag);//是否是英文打印判断
				packageParm.setData("InsCom", resultIns.getValue("CONTRACTOR_DESC",0));//保险公司名称
				packageParm.setData("InsNum", resultIns.getValue("INSURANCE_NUMBER",0));//保险号
				packageParm.setData("TOT_AMT1",new java.text.DecimalFormat("###,##0.00").format(sumTotR));//总金额
				packageParm.setData("Birthday" ,opb.getPat().getBirthday().toString().replaceAll("-", "/").substring(0, 10));
				packageParm.setData("TOT_AMT", df.format(StringTool.round(sumTotS,2)));// 合计应收金额
				packageParm.setData("OWN_AMT", df.format(StringTool.round(sumTotR,2)));// 合计实收金额
				
				/*------------ 增加报表英文信息 add by lich----20141009----end*/		
				packageParm.setData("MR_NO", opb.getPat().getMrNo());//病案号
				packageParm.setData("PAT_NAME", opb.getPat().getName());// 患者姓名
				packageParm.setData("PRINT_NO", printNo);// 票号
				packageParm.setData("BILL_DATE", StringTool.getString(TypeTool.getTimestamp(bill_date), "yyyy/MM/dd HH:mm:ss"));// 收费日期
				packageParm.setData("DATE", "");// 日期
				packageParm.setData("OP_NAME", Operator.getID());//制表人
				packageParm.setData("RECEIVE_PARM", parm);
//				System.out.println("sumPackage===--#!@#$!%$!#"+sumPackage);
				packageParm.setData("SUM_PACKAGE", sumPackage);
				packageParm.setData("CLEAR_FLG", "N");
				this.openDialog("%ROOT%\\config\\opb\\OPBPackageChoose.x", packageParm);
			}
			
			/**
			 * 中文打印
			 */
		}else{
			// System.out.println("tableMParm" + tableMParm);
			// ORDER_DESC;MEDI_QTY;MEDI_UNIT;FREQ_CODE;TAKE_DAYS;
			// DOSAGE_QTY;DOSAGE_UNIT;OWN_PRICE;AR_AMT
			String sql =
				" SELECT   A.CASE_NO, A.RX_NO, A.SEQ_NO, A.OPT_USER, A.OPT_DATE, A.OPT_TERM," +
				" A.PRESRT_NO, A.REGION_CODE, A.MR_NO, A.ADM_TYPE, A.RX_TYPE," +
				" A.TEMPORARY_FLG, A.RELEASE_FLG, A.LINKMAIN_FLG, A.LINK_NO," +
				" A.ORDER_CODE," +
				" A.ORDER_DESC" +
				//delete by huangtt 20141118
//				" || CASE" +
//				" WHEN TRIM (D.MAN_CODE) IS NOT NULL" +
//				" OR TRIM (D.MAN_CODE) <> ''" +
//				" THEN '*' || D.MAN_CODE" +
//				" ELSE ''" +
//				" END" +
				" AS ORDER_DESC," +
				" A.SPECIFICATION, A.GOODS_DESC, A.ORDER_CAT1_CODE, A.MEDI_QTY," +
				" A.MEDI_UNIT, A.FREQ_CODE, A.ROUTE_CODE, A.TAKE_DAYS, A.DOSAGE_QTY," +
				" A.DOSAGE_UNIT, A.DISPENSE_QTY, A.DISPENSE_UNIT, A.GIVEBOX_FLG," +
				//modigy by huangtt 20141125 start
//				" A.OWN_PRICE, " +
				" CASE " +
				" WHEN A.DISPENSE_UNIT <> A.DOSAGE_UNIT" +
				" THEN A.DOSAGE_QTY/A.DISPENSE_QTY*A.OWN_PRICE" +
				" ELSE A.OWN_PRICE" +
				" END AS OWN_PRICE, " +
				//modigy by huangtt 20141125 end
				" A.NHI_PRICE, A.DISCOUNT_RATE, A.OWN_AMT, A.AR_AMT," +
				" A.DR_NOTE, A.NS_NOTE, A.DR_CODE, A.ORDER_DATE, A.DEPT_CODE," +
				" A.DC_DR_CODE, A.DC_ORDER_DATE, A.DC_DEPT_CODE, A.EXEC_DEPT_CODE," +
				" A.SETMAIN_FLG, A.ORDERSET_GROUP_NO, A.ORDERSET_CODE, A.HIDE_FLG," +
				" A.RPTTYPE_CODE, A.OPTITEM_CODE, A.DEV_CODE, A.MR_CODE, A.FILE_NO," +
				" A.DEGREE_CODE, A.URGENT_FLG, A.INSPAY_TYPE, A.PHA_TYPE, A.DOSE_TYPE," +
				" A.PRINTTYPEFLG_INFANT, A.EXPENSIVE_FLG, A.CTRLDRUGCLASS_CODE," +
				" A.PRESCRIPT_NO, A.ATC_FLG, A.SENDATC_DATE, A.RECEIPT_NO, A.BILL_FLG," +
				" A.BILL_DATE, A.BILL_USER, A.PRINT_FLG, A.REXP_CODE, A.HEXP_CODE," +
				" A.CONTRACT_CODE, A.CTZ1_CODE, A.CTZ2_CODE, CTZ3_CODE," +
				" A.PHA_CHECK_CODE, A.PHA_CHECK_DATE, A.PHA_DOSAGE_CODE," +
				" A.PHA_DOSAGE_DATE, A.PHA_DISPENSE_CODE, A.PHA_DISPENSE_DATE," +
				" A.NS_EXEC_CODE, A.NS_EXEC_DATE, A.NS_EXEC_DEPT, A.DCTAGENT_CODE," +
				" A.DCTEXCEP_CODE, A.DCT_TAKE_QTY, A.PACKAGE_TOT, A.AGENCY_ORG_CODE," +
				" A.DCTAGENT_FLG, A.DECOCT_CODE, A.EXEC_FLG, A.RECEIPT_FLG," +
				" A.CAT1_TYPE, A.MED_APPLY_NO, A.BILL_TYPE, A.PHA_RETN_CODE," +
				" F.DOSE_CHN_DESC, '0.0%' AS AL, A.BUSINESS_NO" +
				" FROM OPD_ORDER A," +
				" SYS_FEE D," +
				" PHA_BASE E," +
				" PHA_DOSE F" +
				" WHERE A.ORDER_CODE = D.ORDER_CODE(+)" +
				" AND A.ORDER_CODE = E.ORDER_CODE(+)" +
				" AND E.DOSE_CODE = F.DOSE_CODE(+)" +
				" AND CASE_NO = '" + caseNo + "'" +
				" AND A.MEM_PACKAGE_ID IS NULL" +
				" AND A.RELEASE_FLG <> 'Y'" + //自备药不显示 add by huangjw  20160530
				" ORDER BY REXP_CODE,RX_TYPE, RX_NO, SEQ_NO";//===yanjing 20150227
//			System.out.println("sql费用清单 = = = = " + sql);
			TParm result = new TParm(TJDODBTool.getInstance().select(sql));
			int count = result.getCount("SETMAIN_FLG");
			String setmainFlg = "";
			for (int i = count - 1; i >= 0; i--) {
				setmainFlg = result.getValue("SETMAIN_FLG", i);// 集合医嘱注记
				if ("Y".equals(setmainFlg)) {
					result.removeRow(i);
				}
			}
//			//add by lich 显示集合医嘱主项，细项不显示 --------strat 20150302
//			for(int i = 0;i < count;i++){
//	            TParm temp = result.getRow(i);
//	            if("Y".equals(temp.getValue("SETMAIN_FLG"))){
//	                double price = 0;
//	                double ownPrice = 0;
//	                double onePrice = 0;
//	                String rxNo = temp.getValue("RX_NO");
//	                String orderSetGroupNo = temp.getValue("ORDERSET_GROUP_NO");
//	                for(int j = 0;j < count;j++){
//	                    TParm itemTemp = result.getRow(j);
//	                    if((rxNo + orderSetGroupNo).equals(itemTemp.getValue("RX_NO")+itemTemp.getValue("ORDERSET_GROUP_NO"))){
//	                        System.out.println(rxNo+orderSetGroupNo+"===="+(itemTemp.getValue("RX_NO")+itemTemp.getValue("ORDERSET_GROUP_NO")));
//	                        price += itemTemp.getDouble("AR_AMT");
//	                        ownPrice += itemTemp.getDouble("OWN_AMT");
//	                        onePrice += (itemTemp.getDouble("OWN_PRICE") * itemTemp.getDouble("DOSAGE_QTY"));
////	                        System.out.println("j = "+ j +"  price = = "+price);
//	                    }
//	                }
//	                result.setData("AR_AMT",i,price);
//	                result.setData("OWN_AMT",i,ownPrice);
//	                result.setData("OWN_PRICE",i,onePrice);
//	            }
//	        } 
//			for(int k = count - 1;k >= 0;k--){
//	            if(result.getBoolean("HIDE_FLG",k)){
//	            	result.removeRow(k);
//	            }
//	        }
//			//add by lich 显示集合医嘱主项，细项不显示 --------end 20150302
			TParm parm = opb.getReceiptList().dealTParm(result, batchNoParm);
			if (parm == null)
				return;
			double sumTotS = parm.getDouble("SUMTOTS");// 合计应收金额
			double sumTotR = parm.getDouble("SUMTOTR");// 合计实收金额
			TParm pringListParm = new TParm();
			
			
			
			
			// 病患姓名
			pringListParm.setData("MR_NO", "TEXT", opb.getPat().getMrNo());//病案号
			pringListParm.setData("PAT_NAME", "TEXT", opb.getPat().getName());// 患者姓名
			pringListParm
			.setData("HOSP", "TEXT", Operator.getHospitalCHNFullName());// 医院名称
//			pringListParm.setData("TITLE", "TEXT", Operator
//					.getHospitalCHNFullName());// 医院名称
			pringListParm.setData("BILL_DATE", "TEXT", StringTool.getString(
					TypeTool.getTimestamp(bill_date), "yyyyMMddHHmmss"));// 收费日期
			//pringListParm.setData("PRINT_NO", "TEXT", printNo);// 票号
			String yMd = StringTool.getString(TypeTool.getTimestamp(TJDODBTool
					.getInstance().getDBTime()), "yyyy/MM/dd"); // 年月日
			pringListParm.setData("DATE", "TEXT", yMd);// 日期
			pringListParm.setData("OP_NAME", "TEXT", Operator.getID());//制表人
			// System.out.println("parm==============" + parm);
//			this.openPrintDialog("%ROOT%\\config\\prt\\opb\\OPBOrderPrint.jhw",
//					pringListParm);
//		      this.openPrintDialog(IReportTool.getInstance().getReportPath("OPBOrderPrint.jhw"),
//		                           IReportTool.getInstance().getReportParm("OPBOrderPrint.class", pringListParm));//报表合并modify by wanglong 20130730
//			/**add by sunqy 20140630 门诊费用清单以及门诊套餐费用清单*/
//			String packageSql = "SELECT A.PACKAGE_CODE, A.SECTION_CODE, A.PACKAGE_DESC, A.ORDER_DESC, " +
//					"A.ORDER_NUM, A.UNIT_PRICE, A.RETAIL_PRICE, A.SETMAIN_FLG, B.ORIGINAL_PRICE, B.SECTION_PRICE " +
//					"FROM MEM_PAT_PACKAGE_SECTION_D A, MEM_PACKAGE_SECTION B " +
//					"WHERE A.MR_NO = '"+ opb.getPat().getMrNo() +"' AND A.SECTION_CODE = B.SECTION_CODE AND A.PACKAGE_CODE = B.PACKAGE_CODE";
//			TParm packResult = new TParm(TJDODBTool.getInstance().select(packageSql));
//			if(packResult.getCount()<=0){//无套餐情况
//				pringListParm.setData("TABLEORDER", parm.getData());
//				pringListParm.setData("SUMTOTS", df.format(StringTool.round(sumTotS,2)));// 合计应收金额
//				pringListParm.setData("SUMTOTR", df.format(StringTool.round(sumTotR,2)));// 合计实收金额
//				this.openPrintDialog(IReportTool.getInstance().getReportPath("OPDFeeListV45.jhw"),
//						IReportTool.getInstance().getReportParm("OPDFeeListV45.class", pringListParm));//报表合并  modify by sunqy 20140620
//			}else {//有套餐情况
//				String mainFlg = "";// 集合医嘱注记
//				double sumPackage = 0.00;//套餐价格
//				for (int i = packResult.getCount() - 1; i >= 0; i--) {
//					mainFlg = packResult.getValue("SETMAIN_FLG", i);
//					if ("Y".equals(mainFlg)) {//如果是集合医嘱就去掉集合医嘱项，只显示细项
//						sumPackage += packResult.getDouble("RETAIL_PRICE", i);
//						packResult.removeRow(i);
//					}
//				}
//				TParm packageParm = OPBPackageListControl.getInstance().listParm(packResult);
//				pringListParm.setData("TABLEPACKAGE", packageParm.getData());
//				pringListParm.setData("SUM_", df.format(sumPackage));
//				pringListParm.setData("SUMTOTS", df.format(StringTool.round(sumTotS,2))+df.format(sumPackage));// 合计应收金额
//				pringListParm.setData("SUMTOTR", df.format(StringTool.round(sumTotR,2))+df.format(sumPackage));// 合计实收金额
//				this.openPrintDialog(IReportTool.getInstance().getReportPath("OPDFeeListPackV45.jhw"),
//						IReportTool.getInstance().getReportParm("OPDFeeListV45.class", pringListParm));//报表合并  modify by sunqy 20140630
//			}
			/**add by sunqy 20140630 门诊费用清单以及门诊套餐费用清单*/
//			TParm receptParm = (TParm)this.getParameter();
////			System.out.println("receptParm===="+receptParm);
//			String memPackageFlg = receptParm.getValue("MEM_PACKAGE_FLG");//由OPBChargeM处套餐CHECK_BOX处事件得到
//			System.out.println("################memPackageFlg"+memPackageFlg);
										//add by lich 增加英文医嘱字段TRADE_ENG_DESC  PACKAGE_CODE 20141010
			String packageSql = "SELECT DISTINCT A.SEQ_NO, D.PACKAGE_DESC, D.PACKAGE_CODE, A.AR_AMT AS RETAIL_PRICE," +
					" C.ORDER_DESC, D.ORDER_NUM, B.UNIT_CHN_DESC, B.UNIT_ENG_DESC, D.TRADE_ENG_DESC, A.SETMAIN_FLG " +
					" FROM OPD_ORDER A, SYS_UNIT B, MEM_PACKAGE_SECTION_D C,MEM_PAT_PACKAGE_SECTION_D D " +
					" WHERE A.CASE_NO = '"+ caseNo +"' AND D.ID = A.MEM_PACKAGE_ID " +
					" AND A.MEDI_UNIT = B.UNIT_CODE AND A.ORDER_CODE = C.ORDER_CODE ORDER BY D.PACKAGE_CODE,A.SEQ_NO";
			//System.out.println("SQL--------"+packageSql);
			TParm packResult = new TParm(TJDODBTool.getInstance().select(packageSql));
			
			packResult.setData("engFlag", engFlag);////是否是英文打印判断
//			System.out.println("RESULT------"+packResult);
			if("N".equals(memPackageFlg)||"".equals(memPackageFlg)||memPackageFlg==null){//无套餐情况
				pringListParm.setData("TABLEORDER", parm.getData());
				if (opb.getReg().getAdmType().equals("O")) {
					pringListParm.setData("TITLE", "TEXT", "门诊费用明细清单");
				}else{
					pringListParm.setData("TITLE", "TEXT", "急诊费用明细清单");
				}
				pringListParm.setData("SUMTOTS", df.format(StringTool.round(sumTotS,2)));// 合计应收金额
				pringListParm.setData("SUMTOTR", df.format(StringTool.round(sumTotR,2)));// 合计实收金额
				String deptCode = "";
				boolean isDedug=true; //add by huangtt 20160505 日志输出
				try {
					deptCode = this.getDeptDesc(opb.getReg().getRealdeptCode());
				} catch (Exception e) {
					// TODO: handle exception
					if(isDedug){  
						System.out.println(" come in class: OPBBackReceiptControl.class ，method ：onBackReceipt");
						System.out.println("获取科室名称出现问题");
					}
				}
				pringListParm.setData("DEPT", "TEXT", "科室："+deptCode); //add by huangtt 20150925添加科室
				this.openPrintDialog(IReportTool.getInstance().getReportPath("OPDFeeListV45.jhw"),
						IReportTool.getInstance().getReportParm("OPDFeeListV45.class", pringListParm));//报表合并  modify by sunqy 20140620
			}else if("Y".equals(memPackageFlg)){//有套餐情况
//				System.out.println("!!!--------enter package situation-------!!!");
				String mainFlg = "";// 集合医嘱注记
				double sumPackage = 0.00;//套餐价格
				for (int i = packResult.getCount() - 1; i >= 0; i--) {
					mainFlg = packResult.getValue("SETMAIN_FLG", i);
					if ("Y".equals(mainFlg)) {//如果是集合医嘱就去掉集合医嘱项，只显示细项
						packResult.removeRow(i);
					}
					sumPackage += packResult.getDouble("RETAIL_PRICE", i);
				}
				TParm packageParm = OPBPackageListControl.getInstance().listParm(packResult);
//				System.out.println("packageParm-+++++++"+packageParm);
				
				/*------------ 增加报表英文信息 add by lich----20141009----start*/
				packageParm.setData("engFlag", engFlag);//是否是英文打印判断
				packageParm.setData("InsCom", resultIns.getValue("CONTRACTOR_DESC",0));//保险公司名称
				packageParm.setData("InsNum", resultIns.getValue("INSURANCE_NUMBER",0));//保险号
				packageParm.setData("TOT_AMT1",new java.text.DecimalFormat("###,##0.00").format(sumTotR));//总金额
				packageParm.setData("Birthday" ,opb.getPat().getBirthday().toString().replaceAll("-", "/").substring(0, 10));
				packageParm.setData("TOT_AMT", df.format(StringTool.round(sumTotS,2)));// 合计应收金额
				packageParm.setData("OWN_AMT", df.format(StringTool.round(sumTotR,2)));// 合计实收金额	
				/*------------ 增加报表英文信息 add by lich----20141009----end*/		
				
				packageParm.setData("MR_NO", opb.getPat().getMrNo());//病案号
				packageParm.setData("PAT_NAME", opb.getPat().getName());// 患者姓名
				//packageParm.setData("PRINT_NO", printNo);// 票号
				packageParm.setData("BILL_DATE", StringTool.getString(TypeTool.getTimestamp(bill_date), "yyyy/MM/dd HH:mm:ss"));// 收费日期
				packageParm.setData("DATE", yMd);// 日期
				packageParm.setData("OP_NAME", Operator.getID());//制表人
				packageParm.setData("RECEIVE_PARM", parm);
//				System.out.println("sumPackage===--#!@#$!%$!#"+sumPackage);
				packageParm.setData("SUM_PACKAGE", sumPackage);
				packageParm.setData("CLEAR_FLG", "N");
				packageParm.setData("DEPT", "科室："+this.getDeptDesc(opb.getReg().getRealdeptCode()));//add by huangtt 20150925添加科室
				this.openDialog("%ROOT%\\config\\opb\\OPBPackageChoose.x", packageParm);
			}
		}
	}
	/**
	 * 
	* @Title: onChnEnFill
	* @Description: TODO(中英文费用清单)
	* @author pangben 2015-7-23 
	* @throws
	 */
	public void onChnEnFill(){
		TParm print=OPBFeeListPrintTool.getInstance().getSumPrintData(caseNo, Operator.getID(), "O");
		boolean unPackFlg = this.getValueBoolean("CHN_EN_FLG");
		if (unPackFlg) {
			this.openPrintDialog(IReportTool.getInstance().getReportPath("OPDFeeListChn_EnV45_1.jhw"),
					IReportTool.getInstance().getReportParm("OPDFeeListChn_EnV45.class", print));
		}else{
			this.openPrintDialog(IReportTool.getInstance().getReportPath("OPDFeeListChn_EnV45.jhw"),
					IReportTool.getInstance().getReportParm("OPDFeeListChn_EnV45.class", print));
		}

	}
	
	public String getDeptDesc(String deptCode){
		String sql="SELECT DEPT_CHN_DESC DEPT_DESC FROM SYS_DEPT WHERE DEPT_CODE='"+deptCode+"'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm.getValue("DEPT_DESC", 0);
	}
	
	private final String MSQL = " SELECT B.RECP_TYPE,A.CASE_NO,B.RECEIPT_NO,A.ADM_TYPE,A.REGION_CODE,A.MR_NO,"
			+ "A.PRINT_NO,A.REDUCE_NO," 
			+ "A.BILL_DATE, "  //modify by huangtt 20170215  B.PRINT_DATE AS BILL_DATE, 改为a.bill_date
			+ "SUM(CHARGE01) AS CHARGE01,SUM(CHARGE02) AS CHARGE02,SUM(CHARGE03) AS CHARGE03,SUM(CHARGE04) AS CHARGE04,"
			+ "SUM(CHARGE05) AS CHARGE05,SUM(CHARGE06) AS CHARGE06,SUM(CHARGE07) AS CHARGE07,SUM(CHARGE08) AS CHARGE08,SUM(CHARGE09) AS CHARGE09,"
			+ "SUM(CHARGE10) AS CHARGE10,SUM(CHARGE11) AS CHARGE11,SUM(CHARGE12) AS CHARGE12,SUM(CHARGE13) AS CHARGE13,SUM(CHARGE14) AS CHARGE14,"
			+ "SUM(CHARGE15) AS CHARGE15,SUM(CHARGE16) AS CHARGE16,SUM(CHARGE17) AS CHARGE17,SUM(CHARGE18) AS CHARGE18,SUM(CHARGE19) AS CHARGE19,"
			+ "SUM(CHARGE20) AS CHARGE20,SUM(CHARGE21) AS CHARGE21,SUM(CHARGE22) AS CHARGE22,SUM(CHARGE23) AS CHARGE23,SUM(CHARGE24) AS CHARGE24,"
			+ "SUM(CHARGE25) AS CHARGE25,SUM(CHARGE26) AS CHARGE26,SUM(CHARGE27) AS CHARGE27,SUM(CHARGE28) AS CHARGE28,SUM(CHARGE29) AS CHARGE29,"
			+ "SUM(CHARGE30) AS CHARGE30,SUM(TOT_AMT) AS TOT_AMT,SUM(REDUCE_AMT) AS REDUCE_AMT,"
			+ "SUM(A.AR_AMT) AS AR_AMT,SUM(A.PAY_TYPE01) AS PAY_CASH , SUM (A.PAY_CASH) AS PAY_CASH_TOT,SUM(A.PAY_MEDICAL_CARD) AS PAY_MEDICAL_CARD,SUM(A.PAY_TYPE02) AS PAY_BANK_CARD,"
			+ "SUM(A.PAY_INS_CARD) AS PAY_INS_CARD,SUM(A.PAY_TYPE06) AS PAY_CHECK, SUM(A.PAY_DEBIT) AS PAY_DEBIT, SUM(A.PAY_BILPAY) AS PAY_BILPAY,"
			+ "SUM(A.PAY_INS) AS PAY_INS, SUM(A.PAY_OTHER1) AS PAY_OTHER1, SUM(A.PAY_OTHER2) AS PAY_OTHER2, SUM(A.PAY_REMARK) PAY_REMARK,"
			+ "A.CASHIER_CODE,A.OPT_USER,A.OPT_DATE,A.OPT_TERM,B.PRINT_USER,SUM(A.PAY_OTHER3+A.PAY_TYPE05) AS PAY_OTHER3,SUM(A.PAY_OTHER4+A.PAY_TYPE07) AS PAY_OTHER4 "
			+ ",SUM(A.PAY_TYPE01) AS PAY_TYPE01,SUM(A.PAY_TYPE02) AS PAY_TYPE02,SUM(A.PAY_TYPE03) AS PAY_TYPE03,SUM(A.PAY_TYPE04) AS PAY_TYPE04,SUM(A.PAY_TYPE05) AS PAY_TYPE05, "
			+ "SUM(A.PAY_TYPE06) AS PAY_TYPE06,SUM(A.PAY_TYPE07) AS PAY_TYPE07,SUM(A.PAY_TYPE08) AS PAY_TYPE08,SUM(A.PAY_TYPE09) AS PAY_TYPE09,SUM(A.PAY_TYPE10) AS PAY_TYPE10 " +
					",SUM(A.PAY_TYPE11) AS PAY_TYPE11,SUM(A.PAY_OTHER3) AS EKT_LPK,SUM(A.PAY_OTHER4) AS EKT_XJZKQ "
			+ ",A.MEM_PACK_FLG "
			+ "FROM BIL_OPB_RECP A,BIL_INVRCP B "
			+ "WHERE A.PRINT_NO=B.INV_NO AND A.CASE_NO='#' AND B.RECP_TYPE='OPB' "
			+ "AND A.RESET_RECEIPT_NO IS NULL AND A.PRINT_NO IS NOT NULL  AND A.TOT_AMT>=0 " 
			+ " AND A.RECEIPT_NO NOT IN (SELECT RESET_RECEIPT_NO FROM BIL_OPB_RECP WHERE CASE_NO ='#' AND RESET_RECEIPT_NO IS NOT NULL AND PRINT_NO IS NOT NULL )"
			+ "GROUP BY B.RECP_TYPE,A.CASE_NO,B.RECEIPT_NO,A.ADM_TYPE,A.REGION_CODE,A.MR_NO,A.REDUCE_NO,"
			+ "A.RESET_RECEIPT_NO,A.PRINT_NO,A.BILL_DATE , A.CASHIER_CODE,A.OPT_USER,A.OPT_DATE,A.OPT_TERM ,B.PRINT_USER,A.MEM_PACK_FLG "
			+ "ORDER BY B.RECEIPT_NO";
	
}
