package com.javahis.ui.odi;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

import jdo.adm.ADMInpTool;
import jdo.hl7.Hl7Communications;
import jdo.odi.ODISingleExeTool;
import jdo.sys.Operator;
import jdo.sys.PatTool;
import jdo.sys.SYSBedTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.event.TTextFieldEvent;
import com.dongyang.util.ImageTool;
import com.dongyang.util.StringTool;
import com.javahis.ui.spc.util.ElectronicTagUtil;
import com.javahis.util.StringUtil;
import com.tiis.ui.TiLabel;
import com.tiis.ui.TiMultiPanel;
import com.tiis.ui.TiPanel;

/**
 * <p>
 * Title: 行动护理执行
 * </p>
 * 
 * <p>
 * Description: 行动护理执行
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
 * @author not attributable
 * @version 1.0
 */
public class ODISingleExeControl extends TControl {

	private static final String TPanel = null;
	// 病患基本信息
	TParm patInfo = new TParm();
	TPanel tiPanel4;
	TPanel tiPanel0;
	TiPanel tiPanel2 = new TiPanel();
	JScrollPane jScrollPane1 = new JScrollPane();
	GridLayout gridLayout1 = new GridLayout();
	TiPanel tiPanel3 = new TiPanel();
	String SQL = "";
    TParm ctrlParm = new TParm();// 保存麻精药嘱add by wanglong 20130603
	
	public void onInit() {
		super.onInit();
		// getTextField("MR_NO").grabFocus();
		// nowDate();
		onControl();
		// R1.setSelected(true);
		setExeDate();
		((TTextField) getComponent("MR_NO")).grabFocus();
		callFunction("UI|tableM|addEventListener",
				TTableEvent.CHECK_BOX_CLICKED, this,
				"onTableCheckBoxChangeValue");
		callFunction("UI|BAR_CODE|addEventListener",
				TTextFieldEvent.KEY_PRESSED, this, "onExeQuery");
		// callFunction("UI|BAR_CODE|addEventListener",
		// TTextFieldEvent.KEY_RELEASED, this, "onSaveExe");
		TTextField bar = ((TTextField) getComponent("BAR_CODE"));
		bar.grabFocus();
		panelInit();
	}

	/**
	 * 初始化面板
	 */
	public void panelInit() {
		tiPanel2.setBounds(new Rectangle(2, 2, 1330, 204));
		tiPanel2.setBorder(null);
		tiPanel2.setLayout(null);
		tiPanel4 = ((TPanel) getComponent("tPanel_1"));
		tiPanel4.add(tiPanel2, null);
		jScrollPane1.setBorder(null);
		jScrollPane1.setBounds(new Rectangle(2, 2, 1331, 200));
		tiPanel2.add(jScrollPane1, null);
		tiPanel3.setBorder(null);
		jScrollPane1.setViewportView(tiPanel3);
	}

	/**
	 * 医令接受返回值方法
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void popReturn1(String tag, Object obj) {
		TParm parm = (TParm) obj;
		String order_desc = parm.getValue("ORDER_DESC");
		if (!StringUtil.isNullString(order_desc)) {
			getTextField("ORDER_DESC1").setValue(order_desc);
		}
	}

	private void setExeDate() {
		Timestamp now = TJDODBTool.getInstance().getDBTime();
		Timestamp yes = StringTool.rollDate(now, -1);
		long timeStart = yes.getTime() - 60 * 60 * 1000;
		long timeEnd = now.getTime() + 60 * 60 * 1000;
		setValue("start_Date", new Timestamp(timeStart));
		setValue("end_Date", new Timestamp(timeEnd));
	}

	private TRadioButton R1, R2, R3, R4;
	private String action = "a";
	ODISingleExeTool tool = new ODISingleExeTool();

	/**
	 * 初始化时间
	 */
	public void onQuery() {
        onEnter();
        ctrlParm=new TParm();
        if (((TRadioButton) this.getComponent("R5")).isSelected()) { // add by wanglong 20130603
        	if(Operator.getSpcFlg().equals("N")){
        		this.messageBox("物联网开关没用启用，不能查询");
        		return;
        	}
            // 物联网
            String caseNo = patInfo.getValue("CASE_NO", 0);
            String barCode = getValueString("BAR_CODE");
            String startDate = getValueString("start_Date").replaceAll("[^0-9]", "").substring(0, 8);
            String endDate = getValueString("end_Date").replaceAll("[^0-9]", "").substring(0, 8);
            String startTime = getValueString("start_Date").replaceAll("[^0-9]", "").substring(8);
            String endTime = getValueString("end_Date").replaceAll("[^0-9]", "").substring(8);
            TParm inParm = new TParm();
            inParm.setData("CASE_NO", caseNo);
            inParm.setData("BAR_CODE", barCode);
            TParm spcParm =
                    TIOM_AppServer.executeAction("action.odi.ODIAction", "onQueryDspnDSpc", inParm);
            if (spcParm == null || spcParm.getErrCode() < 0) {
                this.messageBox(spcParm.getErrText());
                return;
            }
            String QUERY_DSPND_SQL =
                    " SELECT CASE WHEN A.NS_EXEC_DATE_REAL IS  NULL THEN 'Y' ELSE 'N' END SEL_FLG,CASE WHEN A.NS_EXEC_DATE_REAL IS NOT NULL THEN 'Y' ELSE 'N' END EXE_FLG,"
                            + "B.LINKMAIN_FLG,B.LINK_NO,TO_CHAR(TO_DATE (A.ORDER_DATE || A.ORDER_DATETIME,'YYYYMMDDHH24MISS'),'YYYY/MM/DD HH24:MI:SS')NS_EXEC_DATE,"
                            + "B.ORDER_DESC,A.MEDI_QTY,A.MEDI_UNIT,A.DOSAGE_QTY,A.DOSAGE_UNIT,B.FREQ_CODE,B.ROUTE_CODE,"
                            + "B.DR_NOTE,B.ORDER_DR_CODE,A.DC_DATE,B.DC_DR_CODE,A.CANCELRSN_CODE,A.INV_CODE,'#' AS BAR_CODE,"//modify by wanglong 20130609
                            + "A.CASE_NO,A.ORDER_NO,A.ORDER_SEQ,A.ORDER_DATE,A.ORDER_DATETIME,B.ORDERSET_GROUP_NO,B.CAT1_TYPE,"
//                            + "CASE WHEN B.CAT1_TYPE='PHA' THEN A.BAR_CODE ELSE C.MED_APPLY_NO END AS BAR_CODE,"
                            + "A.NS_EXEC_DATE_REAL,A.NS_EXEC_CODE_REAL,B.START_DTTM,B.END_DTTM,A.ORDER_CODE,A.BOX_ESL_ID,'#' BARCODE_1,'#' BARCODE_2,'#' BARCODE_3 "
                            + " FROM ODI_DSPND A,ODI_DSPNM B,ODI_ORDER C,SYS_PHAROUTE D "
                            + "WHERE B.CASE_NO = '#' "
                            + "  AND B.ORDER_NO = '#' "
                            + "  AND B.ORDER_SEQ = '#' "
//                            + "  AND A.ORDER_DATE = '#' "
//                            + "  AND A.ORDER_DATETIME = '#' "
                            + "  AND A.CASE_NO = B.CASE_NO "
                            + "  AND A.ORDER_NO = B.ORDER_NO "
                            + "  AND A.ORDER_SEQ = B.ORDER_SEQ "
//                            + "  AND A.ORDER_DATE || A.ORDER_DATETIME BETWEEN B.START_DTTM AND B.END_DTTM "
                            + "  AND A.ORDER_DATE || A.ORDER_DATETIME BETWEEN # AND # "
                            + "  AND A.ORDER_DATE BETWEEN '#' AND '#' "//add by wanglong 20130620
                            + "  AND A.ORDER_DATETIME BETWEEN '#' AND '#' "//add by wanglong 20130620
                            + "  AND (B.ORDERSET_CODE IS NULL OR B.ORDER_CODE = B.ORDERSET_CODE) "
                            + "  AND A.CASE_NO = C.CASE_NO "
                            + "  AND A.ORDER_NO = C.ORDER_NO "
                            + "  AND A.ORDER_SEQ = C.ORDER_SEQ "
                            + "  AND B.ROUTE_CODE=D.ROUTE_CODE(+) "
                            // cat1_type B.CAT1_TYPE
                            // 是否执行
                            + " ORDER BY A.ORDER_NO,A.ORDER_SEQ";
            for (int i = 0; i < spcParm.getCount(); i++) {
                if (!caseNo.equals(spcParm.getValue("CASE_NO", i))) {
                    this.messageBox("就诊号不一致");
                    return;
                }
                String orderNo = spcParm.getValue("ORDER_NO", i);
                String orderSeq = spcParm.getValue("ORDER_SEQ", i);
//                String orderDate = spcParm.getValue("ORDER_DATE", i);
//                String orderDateTime = spcParm.getValue("ORDER_DATETIME", i);
                String startDttm = spcParm.getValue("START_DTTM", i);
                String endDttm = spcParm.getValue("END_DTTM", i);
                String barCode1 = spcParm.getValue("BARCODE_1", i);
                String barCode2 = spcParm.getValue("BARCODE_2", i);
                String barCode3 = spcParm.getValue("BARCODE_3", i);
                String sql =
                        QUERY_DSPND_SQL.replaceFirst("#", barCode).replaceFirst("#", barCode1)
                                .replaceFirst("#", barCode2).replaceFirst("#", barCode3)
                                .replaceFirst("#", caseNo).replaceFirst("#", orderNo)
                                .replaceFirst("#", orderSeq).replaceFirst("#", startDttm)
                                .replaceFirst("#", endDttm).replaceFirst("#", startDate)
                                .replaceFirst("#", endDate).replaceFirst("#", startTime)
                                .replaceFirst("#", endTime);
                TParm result = new TParm(TJDODBTool.getInstance().select(sql));
                if (result.getErrCode() != 0 || result.getCount() < 1) {
                    this.messageBox("查无麻精发药数据 " + result.getErrText());
                    return;
                }
                ctrlParm.addRowData(result, 0);
            }
            onSetTableValue();
        } else {
            onBarCode();
        }
		TTextField bar = ((TTextField) getComponent("BAR_CODE"));
		bar.grabFocus();
		this.clearValue("BAR_CODE");
	}

	public void nowDate() {
		// 得到当前时间
		Timestamp date = SystemTool.getInstance().getDate();
		String Y = date.toString().substring(0, 4);
		String M = date.toString().substring(5, 7);
		String D = date.toString().substring(8, 10);
		String H = date.toString().substring(11, 13);
		int y = Integer.parseInt(Y);
		int m = Integer.parseInt(M);
		int d = Integer.parseInt(D);
		int h = Integer.parseInt(H);
		String YY = "" + y;
		String MM = "" + m;
		String DD = "" + d;
		String HH = "" + h;
		h += 1;
		if (h < 10) {
			HH = "0" + h;
		}
		if (h > 9 && h < 24) {
			HH = "" + h;
		}
		if (h > 24 || h == 24 || h == 00) {
			if (h == 24) {
				HH = "00";
				d += 1;
				DD = "" + d;
			}
			if (h > 24) {
				HH = "0" + (h - 24);
				d += 1;
				DD = "" + d;
			}
			if (d == 28 && y % 4 != 0) {
				DD = "01";
				MM = "03";
			}
			if (d == 30
					&& (m != 1 || m != 3 || m != 5 || m != 7 || m != 8
							|| m != 10 || m != 12)) {
				DD = "01";
				if (m < 10) {
					MM = "0" + (m + 1);
				}
				if (m > 9 && m <= 12) {
					MM = "" + m;
				}
				if (m > 12) {
					MM = "0" + (m - 12);
					y += 1;
					YY = "" + y;
				}
			}
			if (d == 30
					&& (m == 1 || m == 3 || m == 5 || m == 7 || m == 8
							|| m == 10 || m == 12)) {
				d += 1;
				DD = "" + d;
				if (m < 10) {
					MM = "0" + m;
				}
				if (m > 9 && m <= 12) {
					MM = "" + m;
				}
				if (m > 12) {
					MM = "0" + (m - 12);
					y += 1;
					YY = "" + y;
				}
			}

		}
		if (d == 32) {
			DD = "01";
			MM = "" + (m + 1);
		}
		if (m < 10) {
			MM = "0" + m;
		}
		if (d < 10) {
			DD = "0" + d;
		}

		if (m > 9 && m <= 12) {
			MM = "" + m;
		}
		if (m > 12) {
			MM = "0" + (m - 12);
			YY = "" + (y + 1);
		}

		String end_date = "" + YY + "-" + MM + "-" + DD + " " + HH + ":00:00";
		String start_Date = date.toString().substring(0, 14).replace('-', '/')
				+ "00:00";
		// String Start_Date=StringTool;
		// String End_Date=(Timestamp)end_Date;

		end_date = end_date.replace('-', '/');
		// this.messageBox(start_Date);
		// this.messageBox(end_date);
		this.setValue("start_Date", start_Date);
		this.setValue("end_Date", end_date);
	}

	/**
	 * 初始化控件
	 */
	public void onControl() {
		this.setValue("QY", Operator.getRegion());
		this.setValue("BQ", Operator.getStation());
		this.setValue("user", Operator.getID());
		callFunction("UI|BQ|onQuery");
		callFunction("UI|user|onQuery");
		// R1 = (TRadioButton)this.getComponent("R1");

	}

	// 查询病患信息
	public void onQueryPatInfo() {
		String mrNo = PatTool.getInstance().checkMrno(getValueString("MR_NO"));
		String stationCode = getValueString("BQ");
		String SQL = ODISingleExeTool.getInstance().queryPatInfo(mrNo, "",
				stationCode);
		patInfo = new TParm(TJDODBTool.getInstance().select(SQL));
		if (patInfo.getCount() <= 0) {
			this.messageBox("没有病患！");
			this.onClear();
			return;
		}
		
		String patName = patInfo.getValue("PAT_NAME", 0) ;
		String stationDesc = patInfo.getValue("STATION_DESC", 0);
		String bedNoDesc = this.patInfo.getValue("BED_NO_DESC", 0);
		
		setValue("MR_NO", mrNo);
		setValue("PAT_NAME", patName);
		setValue("SEX_CODE", patInfo.getValue("CHN_DESC", 0));
		setValue("bed_no", patInfo.getValue("BED_NO_DESC", 0));
		viewPhoto(mrNo);
		((TTextField) getComponent("BAR_CODE")).grabFocus();
        // onBarCode();//查询SQL
		String caseNo = patInfo.getValue("CASE_NO",0);
        if (Operator.getSpcFlg().equals("Y")) {// shibl add 国药开关
            TParm inparm = new TParm();
            inparm.setData("CASE_NO", caseNo);
            inparm.setData("MR_NO", mrNo);// add by wanglong 20130607
            inparm.setData("PAT_NAME", patName);
            inparm.setData("STATION_DESC", stationDesc);
            inparm.setData("OPT_TERM", Operator.getIP());// add by wanglong 20130607
            inparm.setData("BED_NO_DESC", bedNoDesc);
            TParm result =
                    TIOM_AppServer.executeAction("action.odi.ODIAction", "sendElectronicTag",
                                                 inparm);
            if (result.getErrCode() < 0) {
                this.messageBox(result.getErrText());
                return;
            }
            if (!result.getValue("RESULT").toLowerCase().equals("success")) {
                this.messageBox("查询病患物联网业务失败");
                return;
            }
        }
        // sendElectronicTag();//调用接口
    }

	/**
	 * 查询
	 */
	public void onBarCode() {
        String startDate =
                StringTool.getString((Timestamp) getValue("start_Date"), "yyyyMMddHHmmss");
        String endDate = StringTool.getString((Timestamp) getValue("end_Date"), "yyyyMMddHHmmss");
		String cat1Type = "";
		String doseType = "";
		if (getValueString("R2").equals("Y")) {
			cat1Type = "PHA";
		}
		if (getValueString("R3").equals("Y")) {
			cat1Type = "LIS','RIS";
		}
		if (getValueString("R4").equals("Y")) {
			cat1Type = "TRT','PLN','OTH";
			if (!this.getValueString("BAR_CODE").equals("")) {
				// panelInit();
				// getTTable("tableM").removeRowAll();
				this.messageBox("处置无条码！");
				return;
			}
		}
		String isEex = "";
		if (getValueString("ALL").equals("Y")) {
			isEex = "A";
		} else if (getValueString("YEXE").equals("Y")) {
			isEex = "Y";
		} else if (getValueString("NEXE").equals("Y")) {
			isEex = "N";
		}
		String barCode = getValueString("BAR_CODE");
		/**
		 * 查询药瞩口服数据 20120912 shibl modify
		 * 例000000378016@1205290567,12;1205290567,
		 * 13;1205290567,15;@20120910|090001
		 */
		if (barCode.contains("@")) {
			String[] str = barCode.split("@");
			if (str.length <= 2) {
				this.messageBox("扫描条码内容异常");
				return;
			}
			String mrNo = str[0];// 病案号
			String orderdataStr = str[1];// 医嘱处方信息
			String orderDateStr = str[2];// 医嘱餐次时间
			String orderNo = "";// 处方号
			String orderSeq = "";// 处方序号
			String orderDate = "";// 医嘱日期
			String orderDatetime = "";// 医嘱时间
			if (orderDateStr.contains("|")) {
				orderDate = orderDateStr
						.substring(0, orderDateStr.indexOf("|")).trim();
				if (orderDateStr.substring(orderDateStr.indexOf("|"),
						orderDateStr.length()).length() >= 4)
					orderDatetime = orderDateStr
							.substring(orderDateStr.indexOf("|") + 1,
									orderDateStr.length()).substring(0, 4)
							.trim();
			}
			if (orderdataStr.contains(";")) {
				String[] order = orderdataStr.split(";");
				int count = order.length;
				for (int i = 0; i < count; i++) {
					if (order[i].contains(",")) {
						boolean ISud=orderDatetime.startsWith("2355")?false:true;//是否为长期(暂定时间2355为临时)
						orderNo = order[i].substring(0, order[i].indexOf(","))
								.trim();
						orderSeq = order[i].substring(
								order[i].indexOf(",") + 1, order[i].length())
								.trim();
						SQL = ODISingleExeTool.getInstance().queryPatOrderPhaO(
								patInfo.getValue("CASE_NO", 0), startDate,
								endDate, cat1Type, orderNo, orderSeq,
								orderDate, orderDatetime, isEex,ISud);
						onSetTableValue();
					}
				}
			}
		} else {// 非口服药SQL语句查询
			SQL = ODISingleExeTool.getInstance().queryPatOrder(
					patInfo.getValue("CASE_NO", 0), barCode, startDate,
					endDate, cat1Type, isEex, doseType);
			onSetTableValue();
		}

	}

	/**
	 * 给表格赋值
	 * 
	 * @param sql
	 */
	public void onSetTableValue() {
		this.getTable("tableM").acceptText();
        TParm parmTable = new TParm();
        if (((TRadioButton) this.getComponent("R5")).isSelected()) {// modify by wanglong 20130603
            parmTable = ctrlParm;
        } else {
            parmTable = new TParm(TJDODBTool.getInstance().select(SQL));
            if (parmTable.getCount() <= 0) {
                // panelInit();
                // getTTable("tableM").removeRowAll();
                this.messageBox("没有对应数据！");
                return;
            }
        }
        TParm tableParm = this.getTable("tableM").getParmValue();
        boolean flg = true;
        int row = 0;
        if (tableParm != null) {
            for (int i = 0; i < tableParm.getCount("BAR_CODE"); i++) {
                if (tableParm.getValue("BAR_CODE", i).equals(parmTable.getValue("BAR_CODE", 0))) {
                    this.messageBox("已扫描此条码！");
                    row = i;
                    flg = false;
                    break;
                } else {
                    row = tableParm.getCount("BAR_CODE");
                }
            }
            if (flg) {
                tableParm.addParm(parmTable);
                this.getTable("tableM").setParmValue(tableParm);
                this.getTable("tableM").getTable().grabFocus();
            } else {
                this.getTable("tableM").getTable().grabFocus();
                return;
            }
        } else {
            this.getTable("tableM").setParmValue(parmTable);
            this.getTable("tableM").getTable().grabFocus();
            this.getTable("tableM").setSelectedRow(row);
        }
        int count = 0;
        TParm afParm=getTable("tableM").getParmValue();
        TParm picParm = new TParm();
        for (int i = 0; i < afParm.getCount("ORDER_DESC"); i++) {
            if (afParm.getValue("CAT1_TYPE", i).equals("PHA")) {
                picParm.setData("ORDER_CODE", count, afParm.getValue("ORDER_CODE", i));
                picParm.setData("ORDER_DESC", count, afParm.getValue("ORDER_DESC", i));
                count++;
            }
        }
        tiPanel3.setLayout(null);
		tiPanel3.removeAll();
		this.panelInit();
		gridLayout1 = new GridLayout(0, 5, 10, 10);
        tiPanel3.setLayout(gridLayout1);
        picParm.setCount(count);
        PHA_PIC phaPic[] = new PHA_PIC[count];
        for (int j = 0; j < count; j++) {
            phaPic[j] =
                    new PHA_PIC(picParm.getValue("ORDER_CODE", j),
                            picParm.getValue("ORDER_DESC", j));
            phaPic[j].setPreferredSize(new Dimension(200, 200));
            tiPanel3.add(phaPic[j], null);
        }
        jScrollPane1.setViewportView(tiPanel3);
    }

	/**
	 * 调用电子标签接口
	 * 
	 * @param map
	 */
	public void sendElectronicTag() {
		Map map = new HashMap();
		if (map == null)
			return;
		TParm parmTable = new TParm(TJDODBTool.getInstance().select(SQL));
		for (int i = 0; i < parmTable.getCount(); i++) {
			if (map.get(parmTable.getValue("BOX_ESL_ID", i)) == null
					&& parmTable.getValue("BOX_ESL_ID", i) != null)
				map.put(parmTable.getValue("BOX_ESL_ID", i),
						parmTable.getValue("BOX_ESL_ID", i));
		}
		Iterator it = map.values().iterator();
		try {
			if (it.hasNext()) {
				String  patName= this.patInfo.getValue("PAT_NAME", 0);
//				System.out.println("病患姓名" +patName );
				String stationDesc = this.patInfo.getValue("STATION_DESC", 0);
//				System.out.println("病区" +stationDesc );
				String bedNo = this.patInfo.getValue("BED_NO_DESC", 0);
//				System.out.println("床号" +bedNo );
				String boxID = (String) it.next();
				int lightNum = 3;
				ElectronicTagUtil.login();
				boolean Send = ElectronicTagUtil.getInstance().sendEleTag(
						boxID, patName, stationDesc, bedNo, lightNum);
//				if (Send) {
//					System.out.println("==========调用电子标签接口成功=================");
//				} else {
//					System.out.println("==========调用电子标签接口失败=================");
//				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		    e.printStackTrace();
//			System.out.println("==========调用电子标签接口失败=================");
		}
	}

	/**
	 * 
	 */
	public void onExeQuery() {
		ctrlParm=new TParm();
		if (this.getValueString("BAR_CODE").equals("")) {
			this.messageBox("请输入条码！");
			return;
		}
        if (this.getValueString("PAT_NAME").equals("")) {
            this.messageBox("请先录入病案号");
            return;
        }
		if (((TRadioButton) this.getComponent("R5")).isSelected()) { // add by wanglong 20130603
			if(Operator.getSpcFlg().equals("N")){
        		this.messageBox("物联网开关没用启用，不能查询");
        		return;
        	}
            // 物联网
            String caseNo = patInfo.getValue("CASE_NO", 0);
            String barCode = getValueString("BAR_CODE");
            String startDate = getValueString("start_Date").replaceAll("[^0-9]", "").substring(0, 8);
            String endDate = getValueString("end_Date").replaceAll("[^0-9]", "").substring(0, 8);
            String startTime = getValueString("start_Date").replaceAll("[^0-9]", "").substring(8);
            String endTime = getValueString("end_Date").replaceAll("[^0-9]", "").substring(8);
            TParm inParm = new TParm();
            inParm.setData("CASE_NO", caseNo);
            inParm.setData("BAR_CODE", barCode);
            TParm spcParm =
                    TIOM_AppServer.executeAction("action.odi.ODIAction", "onQueryDspnDSpc", inParm);
            if (spcParm == null || spcParm.getErrCode() < 0) {
                this.messageBox(spcParm.getErrText());
                return;
            }
            String QUERY_DSPND_SQL =
                    " SELECT CASE WHEN A.NS_EXEC_DATE_REAL IS  NULL THEN 'Y' ELSE 'N' END SEL_FLG,CASE WHEN A.NS_EXEC_DATE_REAL IS NOT NULL THEN 'Y' ELSE 'N' END EXE_FLG,"
                            + "B.LINKMAIN_FLG,B.LINK_NO,TO_CHAR(TO_DATE (A.ORDER_DATE || A.ORDER_DATETIME,'YYYYMMDDHH24MISS'),'YYYY/MM/DD HH24:MI:SS')NS_EXEC_DATE,"
                            + "B.ORDER_DESC,A.MEDI_QTY,A.MEDI_UNIT,A.DOSAGE_QTY,A.DOSAGE_UNIT,B.FREQ_CODE,B.ROUTE_CODE,"
                            + "B.DR_NOTE,B.ORDER_DR_CODE,A.DC_DATE,B.DC_DR_CODE,A.CANCELRSN_CODE,A.INV_CODE,'#' AS BAR_CODE,"//modify by wanglong 20130609
                            + "A.CASE_NO,A.ORDER_NO,A.ORDER_SEQ,A.ORDER_DATE,A.ORDER_DATETIME,B.ORDERSET_GROUP_NO,B.CAT1_TYPE,"
//                            + "CASE WHEN B.CAT1_TYPE='PHA' THEN A.BAR_CODE ELSE C.MED_APPLY_NO END AS BAR_CODE,"
                            + "A.NS_EXEC_DATE_REAL,A.NS_EXEC_CODE_REAL,B.START_DTTM,B.END_DTTM,A.ORDER_CODE,A.BOX_ESL_ID,'#' BARCODE_1,'#' BARCODE_2,'#' BARCODE_3 "
                            + " FROM ODI_DSPND A,ODI_DSPNM B,ODI_ORDER C,SYS_PHAROUTE D "
                            + "WHERE B.CASE_NO = '#' "
                            + "  AND B.ORDER_NO = '#' "
                            + "  AND B.ORDER_SEQ = '#' "
//                            + "  AND A.ORDER_DATE = '#' "
//                            + "  AND A.ORDER_DATETIME = '#' "
                            + "  AND A.CASE_NO = B.CASE_NO "
                            + "  AND A.ORDER_NO = B.ORDER_NO "
                            + "  AND A.ORDER_SEQ = B.ORDER_SEQ "
//                            + "  AND A.ORDER_DATE || A.ORDER_DATETIME BETWEEN B.START_DTTM AND B.END_DTTM "
                            + "  AND A.ORDER_DATE || A.ORDER_DATETIME BETWEEN # AND # "
                            + "  AND A.ORDER_DATE BETWEEN '#' AND '#' "//add by wanglong 20130620
                            + "  AND A.ORDER_DATETIME BETWEEN '#' AND '#' "//add by wanglong 20130620
                            + "  AND (B.ORDERSET_CODE IS NULL OR B.ORDER_CODE = B.ORDERSET_CODE) "
                            + "  AND A.CASE_NO = C.CASE_NO "
                            + "  AND A.ORDER_NO = C.ORDER_NO "
                            + "  AND A.ORDER_SEQ = C.ORDER_SEQ "
                            + "  AND B.ROUTE_CODE=D.ROUTE_CODE(+) "
                            // cat1_type B.CAT1_TYPE
                            // 是否执行
                            + " ORDER BY A.ORDER_NO,A.ORDER_SEQ";
            for (int i = 0; i < spcParm.getCount(); i++) {
                if (!caseNo.equals(spcParm.getValue("CASE_NO", i))) {
                    this.messageBox("就诊号不一致");
                    return;
                }
                String orderNo = spcParm.getValue("ORDER_NO", i);
                String orderSeq = spcParm.getValue("ORDER_SEQ", i);
//                String orderDate = spcParm.getValue("ORDER_DATE", i);
//                String orderDateTime = spcParm.getValue("ORDER_DATETIME", i);
                String startDttm = spcParm.getValue("START_DTTM", i);
                String endDttm = spcParm.getValue("END_DTTM", i);
                String barCode1 = spcParm.getValue("BARCODE_1", i);
                String barCode2 = spcParm.getValue("BARCODE_2", i);
                String barCode3 = spcParm.getValue("BARCODE_3", i);
                String sql =
                        QUERY_DSPND_SQL.replaceFirst("#", barCode).replaceFirst("#", barCode1)
                                .replaceFirst("#", barCode2).replaceFirst("#", barCode3)
                                .replaceFirst("#", caseNo).replaceFirst("#", orderNo)
                                .replaceFirst("#", orderSeq).replaceFirst("#", startDttm)
                                .replaceFirst("#", endDttm).replaceFirst("#", startDate)
                                .replaceFirst("#", endDate).replaceFirst("#", startTime)
                                .replaceFirst("#", endTime);
                TParm result = new TParm(TJDODBTool.getInstance().select(sql));
                if (result.getErrCode() != 0 || result.getCount() < 1) {
                    this.messageBox("查无麻精发药数据 " + result.getErrText());
                    return;
                }
                ctrlParm.addRowData(result, 0);
            }
            onSetTableValue();
        } else {
            onBarCode();
        }
		TTextField bar = ((TTextField) getComponent("BAR_CODE"));
		bar.grabFocus();
		this.setValue("BAR_CODE", "");
	}

	// 得到保存执行数据
	public TParm getExeSaveDate(TParm inparm) {
		getTTable("tableM").acceptText();
		int row = getTTable("tableM").getRowCount();
		TParm parmTable = getTTable("tableM").getParmValue();
		Timestamp now = TJDODBTool.getInstance().getDBTime();
		TParm parmM = new TParm();
		TParm parmD = new TParm();
		for (int i = 0; i < row; i++) {
			parmM.addData("CASE_NO", parmTable.getValue("CASE_NO", i));
			parmM.addData("ORDER_NO", parmTable.getValue("ORDER_NO", i));
			parmM.addData("ORDER_SEQ", parmTable.getValue("ORDER_SEQ", i));
			parmM.addData("ORDER_DATE", parmTable.getValue("ORDER_DATE", i));
			parmM.addData("ORDER_DATETIME",
					parmTable.getValue("ORDER_DATETIME", i));
			parmM.addData("CANCELRSN_CODE",
					parmTable.getValue("CANCELRSN_CODE", i));
			// 为CIS接口提供数据--------------------------------------begin
			parmM.addData("LINKMAIN_FLG", parmTable.getValue("LINKMAIN_FLG", i));
			parmM.addData("LINK_NO", parmTable.getValue("LINK_NO", i));
			parmM.addData("CAT1_TYPE", parmTable.getValue("CAT1_TYPE", i));
			parmM.addData("ORDER_DR_CODE",
					parmTable.getValue("ORDER_DR_CODE", i));
			// 为CIS接口提供数据--------------------------------------end
			parmD.addData("CASE_NO", parmTable.getValue("CASE_NO", i));
			parmD.addData("ORDER_NO", parmTable.getValue("ORDER_NO", i));
			parmD.addData("ORDER_SEQ", parmTable.getValue("ORDER_SEQ", i));
			parmD.addData("ORDER_DATE", parmTable.getValue("ORDER_DATE", i));
			parmD.addData("ORDER_DATETIME",
					parmTable.getValue("ORDER_DATETIME", i));
			parmD.addData("INV_CODE", parmTable.getValue("INV_CODE", i));
			parmD.addData("CANCELRSN_CODE",
					parmTable.getValue("CANCELRSN_CODE", i));
			if (getTTable("tableM").getValueAt(i, 0).equals("N")) {
				parmD.addData(
						"NS_EXEC_DATE_REAL",
						parmTable.getData("NS_EXEC_DATE_REAL", i) == null ? new TNull(
								Timestamp.class) : parmTable.getData(
								"NS_EXEC_DATE_REAL", i));
				parmD.addData(
						"NS_EXEC_CODE_REAL",
						parmTable.getData("NS_EXEC_CODE_REAL", i) == null ? new TNull(
								String.class) : parmTable.getData(
								"NS_EXEC_CODE_REAL", i));
	            parmD.addData("BARCODE_1", parmTable.getValue("BARCODE_1", i));// add by wanglong 20130604
	            parmD.addData("BARCODE_2", parmTable.getValue("BARCODE_2", i));
	            parmD.addData("BARCODE_3", parmTable.getValue("BARCODE_3", i));
				parmM.addData(
						"NS_EXEC_DATE_REAL",
						parmTable.getData("NS_EXEC_DATE_REAL", i) == null ? new TNull(
								Timestamp.class) : parmTable.getData(
								"NS_EXEC_DATE_REAL", i));
				parmM.addData(
						"NS_EXEC_CODE_REAL",
						parmTable.getData("NS_EXEC_CODE_REAL", i) == null ? new TNull(
								String.class) : parmTable.getData(
								"NS_EXEC_CODE_REAL", i));
				continue;
			}
			if (getTTable("tableM").getValueAt(i, 1).equals("N")) {
				parmD.addData("NS_EXEC_DATE_REAL", now);
				parmD.addData("NS_EXEC_CODE_REAL", inparm.getValue("USER_ID"));
				parmM.addData("NS_EXEC_DATE_REAL", now);
				parmM.addData("NS_EXEC_CODE_REAL", inparm.getValue("USER_ID"));
	            parmD.addData("BARCODE_1", parmTable.getValue("BARCODE_1", i));// add by wanglong 20130604
	            parmD.addData("BARCODE_2", parmTable.getValue("BARCODE_2", i));
	            parmD.addData("BARCODE_3", parmTable.getValue("BARCODE_3", i));
			} else {
				parmD.addData("NS_EXEC_DATE_REAL", new TNull(Timestamp.class));
				parmD.addData("NS_EXEC_CODE_REAL", new TNull(String.class));
				parmM.addData("NS_EXEC_DATE_REAL", new TNull(Timestamp.class));
				parmM.addData("NS_EXEC_CODE_REAL", new TNull(String.class));
	            parmD.addData("BARCODE_1",new TNull(String.class));// add by wanglong 20130604
	            parmD.addData("BARCODE_2", new TNull(String.class));
	            parmD.addData("BARCODE_3",new TNull(String.class));
			}
			if (parmTable.getValue("ORDERSET_GROUP_NO", i).length() == 0) {
				continue;
			}
			setDetailOrder(parmTable, i, parmD, now);
		}
		TParm parm = new TParm();
		parm.setData("DSPNM", parmM.getData());
		parm.setData("DSPND", parmD.getData());
		return parm;
	}

	/**
	 * 保存动作
	 */
	public void onSaveExe() {
		boolean flg = (Boolean) this.callFunction("UI|ALL|isSelected");
		if (flg) {
			this.messageBox("不能在全部状态下保存");
			return;
		}
		String type = "singleExe";
		TParm inParm = (TParm) this.openDialog(
				"%ROOT%\\config\\inw\\passWordCheck.x", type);
		String OK = inParm.getValue("RESULT");
		if (!OK.equals("OK")) {
			return;
		}
		getTTable("tableM").acceptText();
		TParm parm = getExeSaveDate(inParm);
//		System.out.println("=============="+parm);
		if (parm == null) {
			return;
		}
		TParm result = TIOM_AppServer.executeAction(
				"action.inw.INWOrderSingleExeAction", "onSaveExe", parm);
		if (result.getErrCode() < 0) {
			messageBox("保存失败");
			return;
		} else {
			String cisflg = "N";
			TParm parm1 = parm.getParm("DSPNM");
			for (int i = 0; i < parm1.getCount("CASE_NO"); i++) {
				if (parm1.getValue("CAT1_TYPE", i).equals("PHA"))
					cisflg = "Y";
			}
			// 药嘱
			if (cisflg.equals("Y") && getValueString("NEXE").equals("Y")) {
				// ICU、CCU注记
				String caseNO = parm1.getValue("CASE_NO", 0);
				boolean IsICU = SYSBedTool.getInstance().checkIsICU(caseNO);
				boolean IsCCU = SYSBedTool.getInstance().checkIsCCU(caseNO);
				if (IsICU || IsCCU) {
					String typeF = "NBW";
					List list = new ArrayList();
					parm.setData("ADM_TYPE", "I");
					list.add(parm1);
					// 调用接口
					TParm resultParm = Hl7Communications.getInstance()
							.Hl7MessageCIS(list, typeF);
					if (resultParm.getErrCode() < 0)
						messageBox(resultParm.getErrText());
				}
			}

			tiPanel3.setLayout(null);
			tiPanel3.removeAll();
			this.panelInit();
			getTTable("tableM").removeRowAll();
			messageBox("保存成功");
			this.onClear();
		}
		// /**********************************************************************************/
		// String caseNo = "", orderNo = "", orderSeq = "", startDttm = "",
		// endDttm = "";
		// // 通过CASE_NO，ORDER_NO，ORDER_SEQ在ODI_DSPND中定位多条细项
		// TParm tableParm = getTTable("tableM").getParmValue();
		// for (int i = 0; i < tableParm.getCount(); i++) {
		// caseNo = tableParm.getValue("CASE_NO", i);
		// orderNo = tableParm.getValue("ORDER_NO", i);
		// orderSeq = tableParm.getValue("ORDER_SEQ", i);
		// startDttm = tableParm.getValue("START_DTTM", i);
		// endDttm = tableParm.getValue("END_DTTM", i);
		// // 查询细项的SQL
		// String sql =
		// "SELECT CASE_NO,ORDER_NO,ORDER_SEQ,ORDER_DATE,ORDER_DATETIME,"
		// + "DC_DATE,EXEC_NOTE,EXEC_DEPT_CODE,NS_EXEC_CODE FROM ODI_DSPND "
		// + "WHERE CASE_NO='"
		// + caseNo
		// + "' AND ORDER_NO='"
		// + orderNo
		// + "' AND ORDER_SEQ='"
		// + orderSeq
		// + "' "
		// +
		// " AND TO_DATE (ORDER_DATE||ORDER_DATETIME, 'YYYYMMDDHH24MISS') >= TO_DATE ('"
		// + startDttm
		// + "','YYYYMMDDHH24MISS') "
		// +
		// " AND TO_DATE (ORDER_DATE||ORDER_DATETIME, 'YYYYMMDDHH24MISS') <= TO_DATE ('"
		// + endDttm
		// + "','YYYYMMDDHH24MISS')"
		// + " ORDER BY ORDER_DATE||ORDER_DATETIME";
		// // 更新细表的TDS,更改其数据
		// TParm inparm = new TParm(TJDODBTool.getInstance().select(sql));
		// }
		// /******************************************************************************************/
		// onBarCode();
	}

	private void setDetailOrder(TParm parmTable, int i, TParm parm,
			Timestamp now) {
		String SQL = ODISingleExeTool.getInstance().queryPatOrderSetDetail(
				patInfo.getValue("CASE_NO", 0),
				parmTable.getValue("ORDER_NO", i),
				parmTable.getValue("ORDER_DATE", i),
				parmTable.getValue("ORDER_DATETIME", i),
				parmTable.getValue("ORDERSET_GROUP_NO", i));
		TParm detailParm = new TParm(TJDODBTool.getInstance().select(SQL));
		for (int j = 0; j < detailParm.getCount(); j++) {
			parm.addData("CASE_NO", detailParm.getValue("CASE_NO", j));
			parm.addData("ORDER_NO", detailParm.getValue("ORDER_NO", j));
			parm.addData("ORDER_SEQ", detailParm.getValue("ORDER_SEQ", j));
			parm.addData("ORDER_DATE", detailParm.getValue("ORDER_DATE", j));
			parm.addData("ORDER_DATETIME",
					detailParm.getValue("ORDER_DATETIME", j));
			parm.addData("INV_CODE", new TNull(String.class));
			parm.addData("CANCELRSN_CODE", new TNull(String.class));
			parm.addData("BARCODE_1",new TNull(String.class));// add by wanglong 20130604
			parm.addData("BARCODE_2",new TNull(String.class));
			parm.addData("BARCODE_3",new TNull(String.class));
			if (getTTable("tableM").getValueAt(i, 1).equals("N")) {
				parm.addData("NS_EXEC_DATE_REAL", now);
				parm.addData("NS_EXEC_CODE_REAL", Operator.getID());
			} else {
				parm.addData("NS_EXEC_DATE_REAL", new TNull(Timestamp.class));
				parm.addData("NS_EXEC_CODE_REAL", new TNull(String.class));
			}
		}
	}

	public void onExe() {
		int row = getTTable("tableM").getRowCount();
		for (int i = 0; i < row; i++) {
			getTTable("tableM").setValueAt(getValue("EXE_ALL"), i, 0);
		}
	}

	public void onTableCheckBoxChangeValue(Object obj) {
		TTable table = (TTable) obj;
		table.acceptText();
		int selCol = table.getSelectedColumn();
		int selRow = table.getSelectedRow();
		String columnName = table.getParmMap(selCol);
		int row = table.getRowCount();
		TParm tblParm = table.getParmValue();
		if (!columnName.equals("SEL_FLG")) {
			return;
		}
		if (!tblParm.getValue("LINKMAIN_FLG", selRow).equals("Y")) {
			return;
		}
		for (int i = 0; i < row; i++) {
			if (i == selRow) {
				continue;
			}
			if (!tblParm.getValue("LINK_NO", i).equals(
					tblParm.getValue("LINK_NO", selRow))) {
				continue;
			}
			if (!tblParm.getValue("ORDER_NO", i).equals(
					tblParm.getValue("ORDER_NO", selRow))) {
				continue;
			}
			if (!tblParm.getValue("ORDER_DATE", i).equals(
					tblParm.getValue("ORDER_DATE", selRow))) {
				continue;
			}
			if (!tblParm.getValue("ORDER_DATETIME", i).equals(
					tblParm.getValue("ORDER_DATETIME", selRow))) {
				continue;
			}
			table.setValueAt(table.getValueAt(selRow, 0), i, 0);
		}
	}

	/**
	 * 病案号的回车事件
	 */
	public void onEnter() {
		onQueryPatInfo();
		/*
		 * String start_Date = this.getValueString("start_Date"); String
		 * end_date = this.getValueString("end_date"); String Star_time =
		 * start_Date.toString().substring(11, 13) +
		 * start_Date.toString().substring(14, 16); String End_date =
		 * end_date.toString().substring(9, 11) +
		 * end_date.toString().substring(12, 14); String order_date =
		 * start_Date.substring(0, 10).replace("/", "");
		 * 
		 * Pat pat = new Pat(); pat =
		 * pat.onQueryByMrNo(getValueString("MR_NO")); if (pat == null ||
		 * "".equals(pat.getMrNo())) { this.messageBox("查无此病患!"); return; }
		 * String mr_no = pat.getMrNo(); this.setValue("MR_NO", mr_no);
		 * QueryBed_no(mr_no); QueryPatInfo(mr_no);
		 * getTextField("MR_NO").grabFocus(); viewPhoto(mr_no);
		 * getTextField("tt").grabFocus(); onQueryM(mr_no, Operator.getRegion(),
		 * Operator.getStation(), Operator.getDept(), Star_time, End_date,
		 * order_date);
		 */
	}

	public void QueryBed_no(String mr_no) {
		TParm parm = new TParm();
		parm.setData("MR_NO", mr_no);
		String bed_no = ADMInpTool.getInstance().queryCaseNo(parm).getRow(0)
				.getValue("BED_NO");
		this.setValue("bed_no", bed_no);

	}

	public void QueryPatInfo(String mr_no) {
		String sql = tool.sql_Patinfo(mr_no);
		TParm selParm = new TParm();
		selParm = new TParm(TJDODBTool.getInstance().select(sql));
		this.setValue("PAT_NAME", selParm.getValue("PAT_NAME", 0));
		this.setValue("SEX_CODE", selParm.getValue("CHN_DESC", 0));
	}

	/**
	 * 给table放数据
	 * 
	 * @param MR_NO
	 *            String
	 * @param REGION_CODE
	 *            String
	 * @param STATION_CODE
	 *            String
	 * @param EXEC_DEPT_CODE
	 *            String
	 */
	public void onQueryM(String MR_NO, String REGION_CODE, String STATION_CODE,
			String EXEC_DEPT_CODE, String Star_time, String End_date,
			String order_date) {
		String sql1 = tool.sql_caseNo(MR_NO);
		// this.messageBox("sql" + sql1);
		TParm selParm1 = new TParm();
		selParm1 = new TParm(TJDODBTool.getInstance().select(sql1));
		String case_no = selParm1.getValue("CASE_NO", 0);
		// this.messageBox(case_no);

		if (null == case_no || "".equals(case_no)) {
			this.messageBox("该患者已出院或是未住院");
			return;
		}

		String sql = tool.sql_TableMessage(case_no, REGION_CODE, STATION_CODE,
				EXEC_DEPT_CODE, Star_time, End_date, order_date);
		TParm selParm = new TParm();
		selParm = new TParm(TJDODBTool.getInstance().select(sql));
		if (selParm.getCount() < 0) {
			this.messageBox("此患者在本时段没有要执行的医嘱或是已全部执行完毕");
		}
		this.getTTable("tableM").setParmValue(selParm);
	}

	/**
	 * 清空方法
	 * 
	 * @param tagName
	 *            String
	 * @return TTextField
	 */
	public void onClear() {
		setValue("R1", "Y");
		setValue("MR_NO", "");
		setValue("PAT_NAME", "");
		setValue("SEX_CODE", "");
		setValue("bed_no", "");
//		setExeDate();
		setValue("BAR_CODE", "");
		setValue("EXE_ALL", "N");
		getTTable("tableM").removeRowAll();
        ctrlParm = new TParm();// add by wanglong 20130603
		tiPanel3.setLayout(null);
		tiPanel3.removeAll();
		this.panelInit();
		TPanel photo = (TPanel) this.getComponent("PHOTO_PANEL");
		Image image = null;
		Pic pic = new Pic(image);
		pic.setSize(photo.getWidth(), photo.getHeight());
		pic.setLocation(0, 0);
		photo.removeAll();
		photo.add(pic);
		pic.repaint();

		// this.clearValue(
		// "MR_NO;PAT_NAME;SEX_CODE;bed_no;tt;PHOTO_PANEL");
		//
		// this.getTTable("tableM").setSelectionMode(0);
		// this.getTTable("tableM").removeRowAll();
		// this.action = "a";
		// TPanel photo = (TPanel)this.getCompnent("PHOTO_PANEL");
		// Image image = null;
		// Pic pic = new Pic(image);
		// pic.setSize(photo.getWidth(), photo.getHeight());
		// pic.setLocation(0, 0);
		// photo.removeAll();
		// photo.add(pic);
		// pic.repaint();
		//
		// this.onInit();

	}

	/**
	 * 得到TextField对象
	 * 
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */

	private TTextField getTextField(String tagName) {
		return (TTextField) getComponent(tagName);
	}

	public TTable getTTable(String tag) {
		return (TTable) this.getComponent(tag);
	}

	/*
	 * public void viewPhoto(String mrNo) { // String photoName = mNo + ".jpg";
	 * // String photoName = "" + mrNo + ".jpg"; // String fileName = photoName;
	 * // String mrNo1 = "" + mrNo.substring(0, 3) + "\\"; // String mrNo2 = ""
	 * + mrNo.substring(3, 6) + "\\"; // String mrNo3 = "" + mrNo.substring(6,
	 * 9); // try { // TPanel viewPanel = (TPanel) getComponent("PHOTO_PANEL");
	 * // String root = TIOM_FileServer.getRoot(); // String dir =
	 * TIOM_FileServer.getPath("PatInfPIC.ServerPath"); // dir = root + dir +
	 * mrNo1 + mrNo2 + mrNo3 + "\\"; // byte[] data =
	 * TIOM_FileServer.readFile(TIOM_FileServer.getSocket(), // dir + fileName);
	 * // if (data == null) // return; // double scale = 0.45; // boolean flag =
	 * true; // Image image = ImageTool.scale(data, scale, flag); // Pic pic =
	 * new Pic(image); // pic.setSize(viewPanel.getWidth(),
	 * viewPanel.getHeight()); // pic.setLocation(0, 0); //
	 * viewPanel.removeAll(); // viewPanel.add(pic); // pic.repaint(); // } //
	 * catch (Exception e) { // this.messageBox_(e); // }
	 * 
	 * }
	 */
	/**
	 * 保存事件
	 */
	public void onSave() {
		onSaveExe();
		/*
		 * String value = (String)this.openDialog(
		 * "%ROOT%\\config\\inw\\passWordCheck.x"); if (value == null) { return;
		 * } Timestamp date = SystemTool.getInstance().getDate();
		 * this.getTTable("tableM").setSelectionMode(0); TParm parm =
		 * getTTable("tableM").getParmValue(); int row = parm.getCount(); String
		 * a = parm.getValue("SELECT_FLG"); String b =
		 * parm.getValue("EXEC_FLG"); for (int i = 0; i < row; i++) { if
		 * ("Y".equals(parm.getValue("EXEC_FLG", i))) { TParm tparm = new
		 * TParm(); tparm.setData("OPT_USER", Operator.getID());
		 * tparm.setData("OPT_DATE", date); tparm.setData("OPT_TERM",
		 * Operator.getIP()); tparm.setData("CASE_NO", parm.getValue("CASE_NO",
		 * i)); tparm.setData("ORDER_NO", parm.getValue("ORDER_NO", i));
		 * tparm.setData("ORDER_SEQ", parm.getValue("ORDER_SEQ", i));
		 * tparm.setData("ORDER_DATE", parm.getValue("ORDER_DATE", i));
		 * tparm.setData("ORDER_DATETIME", parm.getValue("ORDER_DATETIME", i));
		 * 
		 * tparm.setData("NS_EXEC_CODE", Operator.getID());
		 * 
		 * TestNtool.getInstance().onUpdate(tparm); } else { continue; }
		 * 
		 * } this.messageBox("P0001");
		 */

	}

	/**
	 * 图片
	 * 
	 */
	public class PHA_PIC extends TiMultiPanel implements MouseListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = 6685653491843293933L;
		TiLabel tiL_orderDesc = new TiLabel();
		TiLabel tiL_Laber = new TiLabel();
		private String OrderDesc = "";
		TiPanel tiPanel1 = new TiPanel();
		TitledBorder titledBorder1;

		public PHA_PIC() {
			try {
				jbInit();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public PHA_PIC(String OrderDesc) {// 空图
			try {
				this.OrderDesc = OrderDesc;
				jbInit();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		/**
		 * 
		 * @param OrderCode
		 * @param OrderDesc
		 * @param Color
		 */
		public PHA_PIC(String OrderCode, String OrderDesc) {//
			try {
				// System.out.println("------2-------------"+OrderCode);
				this.OrderDesc = OrderDesc;
				jbInit();
				viewPhoto(OrderCode);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void jbInit() throws Exception {
			titledBorder1 = new TitledBorder("");
			tiL_orderDesc.setBackground(Color.black);
			tiL_orderDesc.setFont(new java.awt.Font("宋体", 1, 12));
			tiL_orderDesc.setForeground(Color.black);
			tiL_orderDesc.setText(this.OrderDesc);
			tiL_orderDesc.setBounds(new Rectangle(9, 1, 240, 15));
			tiL_Laber.setBackground(Color.black);
			tiL_Laber.setFont(new java.awt.Font("宋体", 1, 12));
			tiL_Laber.setForeground(Color.black);
			tiL_Laber.setText("没有图片");
			tiL_Laber.setBounds(new Rectangle(60, 70, 112, 15));
			this.setFont(new java.awt.Font("Dialog", 0, 11));
			this.setBorder(BorderFactory.createEtchedBorder());
			this.setLayout(null);
			this.addMouseListener(this);
			tiPanel1.setBounds(new Rectangle(30, 15, 190, 180));
			tiPanel1.setLayout(null);
			this.add(tiPanel1, null);
			this.add(tiL_orderDesc, null);
		}

		/**
		 * 图片显示方法
		 * 
		 * @param orderCode
		 */
		public void viewPhoto(String orderCode) {
			// System.out.println("------------------------3---------"+orderCode);
			String photoName = orderCode + ".jpg";
			String fileName = photoName;
			try {
				String root = TIOM_FileServer.getRoot();
				String dir = TIOM_FileServer.getPath("PHAInfoPic.ServerPath");
				dir = root + dir;
				byte[] data = TIOM_FileServer.readFile(
						TIOM_FileServer.getSocket(), dir + fileName);
				if (data == null) {
					tiPanel1.removeAll();
					tiPanel1.add(tiL_Laber, null);
					return;
				}
				double scale = 0.7;
				boolean flag = true;
				Image image = ImageTool.scale(data, scale, flag);
				Pic pic = new Pic(image);
				pic.setSize(tiPanel1.getWidth(), tiPanel1.getHeight());
				pic.setLocation(0, 0);
				tiPanel1.removeAll();
				pic.setHorizontalAlignment(0);
				tiPanel1.add(pic, null);
				pic.repaint();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void mouseClicked(MouseEvent e) {
			if (this.OrderDesc.equals("")) {
				JOptionPane.showMessageDialog(this, "没有图片");
				return;
			}
		}

		public void mousePressed(MouseEvent e) {
		}

		public void mouseReleased(MouseEvent e) {
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}

		public String getOrderDesc() {
			return OrderDesc;
		}

		public void setOrderDesc(String OrderDesc) {
			this.OrderDesc = OrderDesc;
		}
	}

	/**
	 * 相片显示方法
	 * 
	 * @param mrNo
	 */
	public void viewPhoto(String mrNo) {
		String photoName = mrNo + ".jpg";
		String fileName = photoName;
		try {
			TPanel viewPanel = (TPanel) getComponent("PHOTO_PANEL");
			String root = TIOM_FileServer.getRoot();
			String dir = TIOM_FileServer.getPath("PatInfPIC.ServerPath");
			//病案号大于等于10位处理
            if(mrNo.length()>=10){
            	dir = root + dir + mrNo.substring(0, 3) + "\\"
                + mrNo.substring(3, 6) + "\\" + mrNo.substring(6, 9) + "\\";
            //病案号小于10位处理
            }else{
            	dir = root + dir + mrNo.substring(0, 2) + "\\"
                + mrNo.substring(2, 4) + "\\" + mrNo.substring(4, 7) + "\\";
            }
            //

			byte[] data = TIOM_FileServer.readFile(TIOM_FileServer.getSocket(),
					dir + fileName);
			if (data == null) {
				viewPanel.removeAll();
				return;
			}
			double scale = 0.5;
			boolean flag = true;
			Image image = ImageTool.scale(data, scale, flag);
			Pic pic = new Pic(image);
			pic.setSize(viewPanel.getWidth(), viewPanel.getHeight());
			pic.setLocation(0, 0);
			viewPanel.removeAll();
			viewPanel.add(pic);
			pic.repaint();
		} catch (Exception e) {
		}
	}

	class Pic extends JLabel {
		Image image;

		public Pic(Image image) {
			this.image = image;
		}

		public void paint(Graphics g) {
			g.setColor(new Color(161, 220, 230));
			g.fillRect(4, 15, 100, 100);
			if (image != null) {
				g.drawImage(image, 4, 15, null);

			}
		}
	}

	/**
	 * 得到TTable对象
	 * 
	 * @param tagName
	 *            String
	 * @return TTable
	 */
	private TTable getTable(String tagName) {
		return (TTable) getComponent(tagName);
	}
}
