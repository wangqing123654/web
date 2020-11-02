package com.javahis.ui.pes;

import java.text.SimpleDateFormat;

import jdo.pes.PESTool;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

/**
 * 
 * <p>
 * Title:���ﴦ������
 * </p>
 * 
 * <p>
 * Description:���ﴦ������
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2012
 * </p>
 * 
 * <p>
 * Company: Bluecore
 * </p>
 * 
 * @author zhangp 2012.8.2
 * @version 1.0
 */
public class PESMainControl extends TControl {
	private static TTable tableM;
	private static TTable tableD;
	private static int row = -1;

	/**
	 * ��ʼ������
	 */
	public void onInit() {
		super.onInit();
		setValue("EVAL_CODE", Operator.getID());
		setValue("REGION_CODE", Operator.getRegion());
		tableM = (TTable) getComponent("TABLE1");
		tableD = (TTable) getComponent("TABLE2");
	}

	/**
	 * ��ѯ
	 */
	public void onQuery() {
		TParm parm = new TParm();
		if (getValueString("PES_NO").equals("")) {
			messageBox("��ѡ������ڼ�");
			return;
		}

		if (getValueString("PES_TYPE").equals("")) {
			messageBox("��ѡ��������");
			return;
		}
		parm.setData("TYPE_CODE", getValueString("PES_TYPE"));
		parm.setData("PES_NO", getValueString("PES_NO"));
		TParm result = PESTool.getInstance().selectOPDM(parm);
		//add by huangtt 20131203 start 
		for(int i=0;i<result.getCount();i++){
			String zd = result.getValue("ICD_CHN_DESC", i);
			String zd1 [] = zd.split(";");
			String str = "";
			for(int j=0;j<zd1.length;j++){
				str =str+ "'"+zd1[j]+"',";
			}
			String sql = "SELECT ICD_CHN_DESC FROM SYS_DIAGNOSIS WHERE ICD_CODE IN("+str.substring(0,str.length()-1)+")";
			TParm zdParm = new TParm(TJDODBTool.getInstance().select(sql));
			String icdDesc="";
			for(int k=0;k<zdParm.getCount();k++){
				icdDesc= icdDesc + zdParm.getValue("ICD_CHN_DESC", k)+"��";
			}
			result.setData("ICD_CHN_DESC", i, icdDesc.subSequence(0, icdDesc.length()-1));
		}
		
		//add by huangtt 20131203 end
		tableM.setParmValue(result);
	}

	/**
	 * table1����¼�
	 */
	public void onClickTableM() {
		if (row != tableM.getSelectedRow()) {
			onSave("");
		}
		row = tableM.getSelectedRow();
		TParm parm = new TParm();
		parm.setData("PES_NO", getValueString("PES_NO"));
		parm.setData("CASE_NO", tableM.getParmValue().getValue("CASE_NO", row));
		parm.setData("PES_RX_NO", tableM.getParmValue().getValue("PES_RX_NO",
				row));
		TParm result = PESTool.getInstance().selectOPDD(parm);
		tableD.setParmValue(result);
	}

	/**
	 * ���水ť
	 */
	public void onSave() {
		onSave("����ɹ�");
	}

	/**
	 * ����
	 */
	private void onSave(String message) {
		if (row != -1) {
			tableM.acceptText();
			tableD.acceptText();
			TParm parmM = tableM.getParmValue().getRow(row);
			TParm parmD = tableD.getParmValue();
			// System.out.println(parmM);
			// System.out.println(parmD);
			TParm parm = new TParm();
			parm.setData("OPDM", parmM.getData());
			parm.setData("OPDD", parmD.getData());
			parm.setData("EVAL_CODE", getValue("EVAL_CODE"));
			parm.setData("OPT_USER", Operator.getID());
			parm.setData("OPT_TERM", Operator.getIP());
			TParm result = TIOM_AppServer.executeAction("action.pes.PESAction",
					"updatePESOPDMD", parm);
			if (result.getErrCode() < 0) {
				messageBox("����ʧ��");
				return;
			}
		}
		if (!message.equals("")) {
			messageBox(message);
		}
	}

	public void onSavePESResult() {
		TParm parm = new TParm();
		if (getValueString("PES_NO").equals("")) {
			messageBox("��ѡ������ڼ�");
			return;
		}

		if (getValueString("PES_TYPE").equals("")) {
			messageBox("��ѡ��������");
			return;
		}
		parm.setData("TYPE_CODE", getValueString("PES_TYPE"));
		parm.setData("PES_NO", getValueString("PES_NO"));
		TParm tableParm = PESTool.getInstance().selectOPDM(parm);
		parm = new TParm();
		double order_qty = 0;// A����ҩƷ������
		double antibiotic_qty = 0;// C��ʹ�ÿ���ҩ�Ĵ�����
		double inject_qty = 0;// E��ʹ��ע����Ĵ�����
		double base_qty = 0;// G�������л���ҩ��Ʒ������
		double goods_qty = 0;// I��������ʹ��ҩƷͨ��������
		double rx_total = 0;// K�������ܽ��
		double o = 0;// O������������
		for (int i = 0; i < tableParm.getCount("SEQ"); i++) {
			order_qty += tableParm.getInt("ORDER_QTY", i);
			antibiotic_qty += tableParm.getInt("ANTIBIOTIC_QTY", i);
			inject_qty += tableParm.getInt("INJECT_QTY", i);
			base_qty += tableParm.getInt("BASE_QTY", i);
			goods_qty += tableParm.getInt("GOODS_QTY", i);
			rx_total += tableParm.getDouble("RX_TOTAL", i);
			if (!tableParm.getValue("REASON_FLG", i).equals("Y")) {
				o++;
			}
		}
		double b = order_qty / tableParm.getCount("SEQ");// B��ƽ��ÿ�Ŵ�����ҩƷ���� =
		// A/��������
		double d = antibiotic_qty / tableParm.getCount("SEQ");// D������ҩʹ�ðٷ���=
		// C/��������
		double f = inject_qty / tableParm.getCount("SEQ");// F��ע���ʹ�ðٷ���= E/��������
		double h = base_qty / order_qty;// H�����һ���ҩ��ռ������ҩ�İٷ���= G/A
		double j = goods_qty / order_qty;// J��ҩƷͨ����ռ������ҩ�İٷ���=I/A
		double l = StringTool.round(rx_total, 2) / tableParm.getCount("SEQ");// L��ƽ��ÿ�Ŵ�����K/��������
		double p = o / tableParm.getCount("SEQ");// P���������ٷ��ʣ�O/��������
		TParm printParm = new TParm();
		printParm.setData("TYPE_CODE", getValueString("PES_TYPE"));
		printParm.setData("PES_NO", getValueString("PES_NO"));
		printParm.setData("PES_A", Double.valueOf(order_qty).intValue());
		printParm.setData("PES_B", StringTool.round(b, 1));
		printParm.setData("PES_C", Double.valueOf(antibiotic_qty).intValue());
		printParm.setData("PES_D", StringTool.round(d, 1));
		printParm.setData("PES_E", Double.valueOf(inject_qty).intValue());
		printParm.setData("PES_F", StringTool.round(f, 1));
		printParm.setData("PES_G", Double.valueOf(base_qty).intValue());
		printParm.setData("PES_H", StringTool.round(h, 1));
		printParm.setData("PES_I", Double.valueOf(goods_qty).intValue());
		printParm.setData("PES_J", StringTool.round(j, 1));
		printParm.setData("PES_K", StringTool.round(rx_total, 2));
		printParm.setData("PES_L", StringTool.round(l, 2));
		printParm.setData("PES_O", Double.valueOf(o).intValue());
		printParm.setData("PES_P", StringTool.round(p, 2));
		printParm.setData("OPT_USER", Operator.getID());
		printParm.setData("OPT_TERM", Operator.getIP());
		TParm result = TIOM_AppServer.executeAction("action.pes.PESAction",
				"updatePESResult", printParm);
		if (result.getErrCode() < 0) {
			messageBox("����ʧ��");
			return;
		}
		messageBox("����ɹ�");
	}

	/**
	 * ��������������
	 */
	public void onPrintPesOpdm() {
		TParm parm = new TParm();
		if (getValueString("PES_NO").equals("")) {
			messageBox("��ѡ������ڼ�");
			return;
		}

		if (getValueString("PES_TYPE").equals("")) {
			messageBox("��ѡ��������");
			return;
		}
		parm.setData("TYPE_CODE", getValueString("PES_TYPE"));
		parm.setData("PES_NO", getValueString("PES_NO"));
		TParm tableParm = PESTool.getInstance().selectOPDMforPrint(parm);
//		 System.out.println("tableParm=="+tableParm);
		SimpleDateFormat df=new SimpleDateFormat("yyyy/MM/dd"); 
		//add by huangtt 20131203 start 
		for(int i=0;i<tableParm.getCount();i++){
			String zd = tableParm.getValue("ICD_CHN_DESC", i);
			String zd1 [] = zd.split(";");
			String str = "";
			for(int j=0;j<zd1.length;j++){
				str =str+ "'"+zd1[j]+"',";
			}
			String sql = "SELECT ICD_CHN_DESC FROM SYS_DIAGNOSIS WHERE ICD_CODE IN("+str.substring(0,str.length()-1)+")";
			TParm zdParm = new TParm(TJDODBTool.getInstance().select(sql));
			String icdDesc="";
			for(int k=0;k<zdParm.getCount();k++){
				icdDesc= icdDesc + zdParm.getValue("ICD_CHN_DESC", k)+"��";
			}
			tableParm.setData("ICD_CHN_DESC", i, icdDesc.subSequence(0, icdDesc.length()-1));
			tableParm.setData("ORDER_DATE", i, df.format(tableParm.getData("ORDER_DATE", i)));
		}
		
		//add by huangtt 20131203 end
		
		TParm resultParm = PESTool.getInstance().selectPESResult(parm);
		// System.out.println("resultParm=="+resultParm);
		parm = new TParm();
		double order_qty = resultParm.getDouble("PES_A", 0);// A����ҩƷ������
		double antibiotic_qty = resultParm.getDouble("PES_C", 0);// C��ʹ�ÿ���ҩ�Ĵ�����
		double inject_qty = resultParm.getDouble("PES_E", 0);// E��ʹ��ע����Ĵ�����
		double base_qty = resultParm.getDouble("PES_G", 0);// G�������л���ҩ��Ʒ������
		double goods_qty = resultParm.getDouble("PES_I", 0);// I��������ʹ��ҩƷͨ��������
		double rx_total = resultParm.getDouble("PES_K", 0);// K�������ܽ��
		double o = resultParm.getDouble("PES_O", 0);// O������������
		double b = resultParm.getDouble("PES_B", 0);// B��ƽ��ÿ�Ŵ�����ҩƷ���� =
		// A/��������
		double d = resultParm.getDouble("PES_D", 0);// D������ҩʹ�ðٷ���=
		// C/��������
		double f = resultParm.getDouble("PES_F", 0);// F��ע���ʹ�ðٷ���= E/��������
		double h = resultParm.getDouble("PES_H", 0);// H�����һ���ҩ��ռ������ҩ�İٷ���= G/A
		double j = resultParm.getDouble("PES_J", 0);// J��ҩƷͨ����ռ������ҩ�İٷ���=I/A
		double l = resultParm.getDouble("PES_L", 0);// L��ƽ��ÿ�Ŵ�����K/��������
		double p = resultParm.getDouble("PES_P", 0);// P���������ٷ��ʣ�O/��������
		tableParm.addData("SYSTEM", "COLUMNS", "SEQ");
		tableParm.addData("SYSTEM", "COLUMNS", "ORDER_DATE");
		tableParm.addData("SYSTEM", "COLUMNS", "AGE");
		tableParm.addData("SYSTEM", "COLUMNS", "ICD_CHN_DESC");
		tableParm.addData("SYSTEM", "COLUMNS", "ORDER_QTY");
		tableParm.addData("SYSTEM", "COLUMNS", "ANTIBIOTIC_QTY");
		tableParm.addData("SYSTEM", "COLUMNS", "INJECT_QTY");
		tableParm.addData("SYSTEM", "COLUMNS", "BASE_QTY");
		tableParm.addData("SYSTEM", "COLUMNS", "GOODS_QTY");
		tableParm.addData("SYSTEM", "COLUMNS", "RX_TOTAL");
		tableParm.addData("SYSTEM", "COLUMNS", "DR_CODE");
		tableParm.addData("SYSTEM", "COLUMNS", "PHA_DOSAGE_CODE");
		tableParm.addData("SYSTEM", "COLUMNS", "PHA_DISPENSE_CODE");
		tableParm.addData("SYSTEM", "COLUMNS", "REASON_FLG");
		tableParm.addData("SYSTEM", "COLUMNS", "QUESTION_CODE");
		TParm printParm = new TParm();
		printParm.setData("TABLE", tableParm.getData());
		printParm.setData("A", Double.valueOf(order_qty).intValue());
		printParm.setData("B", StringTool.round(b, 1));
		printParm.setData("C", Double.valueOf(antibiotic_qty).intValue());
		printParm.setData("D", (StringTool.round(d, 1)*100+"").substring(0, (StringTool.round(d, 1)*100+"").length()-2));
		printParm.setData("E", Double.valueOf(inject_qty).intValue());
		printParm.setData("F", (StringTool.round(f, 1)*100+"").substring(0, (StringTool.round(f, 1)*100+"").length()-2));
		printParm.setData("G", Double.valueOf(base_qty).intValue());
		printParm.setData("H", (StringTool.round(h, 1)*100+"").substring(0, (StringTool.round(h, 1)*100+"").length()-2));
		printParm.setData("I", Double.valueOf(goods_qty).intValue());
		printParm.setData("J", (StringTool.round(j, 1)*100+"").substring(0, (StringTool.round(j, 1)*100+"").length()-2));
		printParm.setData("K", StringTool.round(rx_total, 2));
		printParm.setData("L", StringTool.round(l, 2));
		printParm.setData("O", Double.valueOf(o).intValue());
		printParm.setData("P", StringTool.round(p, 1));
		
		String date = resultParm.getValue("PES_DATE", 0).toString();
//		printParm.setData("PES_TIME", "TEXT", "�������: " );
//				+ date.substring(0, 4)
//				+ "/" + date.substring(5, 7) + "/" + date.substring(8, 10)
		printParm.setData("REGION", "TEXT", Operator.getHospitalCHNShortName());
		printParm.setData("EVAL_CODE", "TEXT", resultParm.getValue("EVAL_CODE",
				0));
		this.openPrintWindow("%ROOT%\\config\\prt\\PES\\PESWorkList.jhw",
				printParm);
	}

	/**
	 * ���Excel
	 */
	public void onExport() {
		tableM.acceptText();
		// �õ�UI��Ӧ�ؼ�����ķ���
		TParm parm = tableM.getParmValue();
		if (null == parm || parm.getCount() <= 0) {
			this.messageBox("û����Ҫ����������");
			return;
		}
		ExportExcelUtil.getInstance().exportExcel(tableM, "�������������б�");
	}

	/**
	 * ���
	 */
	public void onClear() {
		TParm parm = new TParm();
		tableM.setParmValue(parm);
		tableD.setParmValue(parm);
	}
}
