package com.javahis.ui.mro;

import java.util.ArrayList;
import java.util.List;

import jdo.mro.MROBorrowTool;
import jdo.mro.MROQueueTool;
import jdo.sys.Operator;
import jdo.sys.Pat;

import org.apache.commons.lang.StringUtils;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TComponent;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>
 * Title: �����������
 * 
 * <p>
 * Description:
 * 
 * <p>
 * Copyright:
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author wangbin 2014.08.29
 * @version 4.0
 */
public class MROArchivesManageControl extends TControl {

	private TTable table;
	private String storeLocation; // ��ǰ���λ��
	private TParm parameterParm; // ҳ�洫�����

	/**
	 * ��ʼ��
	 */
	public void onInit() {
		super.onInit();
		
		Object obj = this.getParameter();
		if (null != obj) {
			if (obj instanceof TParm) {
				this.parameterParm = (TParm) obj;
			}
		}
		
		this.onInitPage();
	}

	/**
	 * ��ʼ��ҳ��
	 */
	public void onInitPage() {
		table = (TTable) this.getComponent("TABLE");
		
		// ������ݵ���¼�
		this.callFunction("UI|TABLE|addEventListener", "TABLE->"
				+ TTableEvent.CLICKED, this, "onTableClicked");
		
		// ���û��ҳ�洫�Σ�˵���ǵ���˵���������ҳ��
		if (null != parameterParm) {
			((TComponent) callFunction("UI|getThis")).setTag("MROArchivesManageUI");
			this.setValue("MR_NO", parameterParm.getValue("MR_NO"));
			getComboBox("ADM_TYPE").setSelectedID(parameterParm.getValue("ADM_TYPE"));
			this.onQuery();
		}
	}

	/**
	 * ��ѯ
	 */
	public void onQuery() {
		// ��ѯ������Ĳ�������
		TParm resultParm = MROBorrowTool.getInstance().queryMroMrv(this.getQueryParm());
		if (resultParm.getCount() <= 0) {
			table.setParmValue(new TParm());
			messageBox("��������");
			return;
		}
		
		table.setParmValue(resultParm);
	}

	/**
	 * ��ȡ��ѯ��������
	 * 
	 * @return
	 */
	private TParm getQueryParm() {
		TParm parm = new TParm();
		
		// ��������
		if (StringUtils.isNotEmpty(this.getValueString("ADM_TYPE"))) {
			parm.setData("ADM_TYPE", this.getValueString("ADM_TYPE"));
		}
		
		String status = ((TComboBox)this.getComponent("STATUS")).getSelectedID();
		// �ڿ�״̬
		if (StringUtils.isNotEmpty(status)) {
			parm.setData("IN_FLG", status);
		}
		
		// ������
		if (StringUtils.isNotEmpty(this.getValueString("MR_NO"))) {
			parm.setData("MR_NO", this.getValueString("MR_NO"));
		}
		
		String archivesNo = this.getValueString("ARCHIVES_NO");
		// ������
		if (StringUtils.isNotEmpty(archivesNo.trim())) {
			String[] array = archivesNo.split("-");
			
			if (array.length > 2) {
				// �����
				String boxCode = array[0];
				// ���
				String bookNo = array[2];
				
				parm.setData("BOX_CODE", boxCode);
				
				parm.setData("BOOK_NO", bookNo);
			}
		}
		
		return parm;
	}

	/**
	 * 
	 * ����
	 */
	public void onSave() {
		if (null == table.getParmValue() || table.getParmValue().getCount() <= 0) {
			this.messageBox("�ޱ�������");
			return;
		}
		
		int selRow = table.getSelectedRow();
		if (selRow < 0) {
			this.messageBox("��ѡ����Ҫ�޸ĵ�������");
			return;
		}
		
		// �޸ĺ���λ��
		String updateStoreLocation = this.getValueString("CURT_LOCATION").trim();
		if (StringUtils.equals(storeLocation, updateStoreLocation)) {
			this.messageBox("���޸ĵ�ǰ����Ĵ��λ�ú󱣴�");
			return;
		}
		
		// ������Ա
		String optUser = Operator.getID();
		// ������ĩ
		String optTerm = Operator.getIP();
		
		TParm parm = table.getParmValue().getRow(selRow);
		TParm updateParm = new TParm();
		updateParm.setData("MR_NO", parm.getValue("MR_NO"));
		updateParm.setData("ADM_TYPE", parm.getValue("ADM_TYPE"));
		updateParm.setData("BOX_CODE", parm.getValue("BOX_CODE"));
		updateParm.setData("BOOK_NO", parm.getValue("BOOK_NO"));
		// �޸ĺ���λ��
		updateParm.setData("CURT_LOCATION", updateStoreLocation);
		updateParm.setData("OPT_USER", optUser);
		updateParm.setData("OPT_TERM", optTerm);
		
		TParm result = MROBorrowTool.getInstance().updateMroMrvStoreLocation(updateParm);
		if (result.getErrCode() < 0) {
			this.messageBox("E0001");
			err(result.getErrCode() + " " + result.getErrText());
			return;
		}
		
		this.messageBox("P0001");
		table.setValueAt(updateStoreLocation, selRow, 5);
		table.getParmValue().setData("CURT_LOCATION", selRow, updateStoreLocation);
	}
	
	/**
	 * ���ݲ����Ų�ѯ
	 */
	public void onQueryByMrNo() {
		// ȡ�ò�����
		String mrNo = this.getValueString("MR_NO").trim();
		if (StringUtils.isEmpty(mrNo)) {
			return;
		} else {
			Pat pat = Pat.onQueryByMrNo(mrNo);
			mrNo = pat.getMrNo();
			this.setValue("MR_NO", mrNo);
			this.onQuery();
		}
	}
	
	/**
	 * ���ݰ����Ų�ѯ
	 */
	public void onQueryByArchivesNo() {
		String[] array = this.getValueString("ARCHIVES_NO").split("-");
		if (array.length != 3) {
			this.messageBox("��������");
			return;
		}
		
		TParm queryParm = new TParm();
		queryParm.setData("BOX_CODE", array[0]);
		queryParm.setData("ADM_TYPE", array[1]);
		queryParm.setData("BOOK_NO", array[2]);
		
		TParm resultParm = MROBorrowTool.getInstance().queryMroMrv(queryParm);
		if (resultParm.getCount() <= 0) {
			table.setParmValue(new TParm());
			messageBox("��������");
			return;
		}
		
		table.setParmValue(resultParm);
	}
	
	/**
	 * ���
	 */
	public void onClear() {
		clearValue("ADM_TYPE;STATUS;MR_NO;ARCHIVES_NO;CURT_LOCATION");
		table.setParmValue(new TParm());
	}
	
	/**
	 * ��Ӷ�table �ļ����¼�
	 * 
	 * @param row
	 */
	public void onTableClicked(int row) {
		if (row < 0) {
			return;
		}
		
		TParm data = table.getParmValue();
		
		setValueForParm("CURT_LOCATION", data, row);
		
		storeLocation = this.getValueString("CURT_LOCATION").trim();
	}
	
	/**
	 * ��ӡ�½��鵵�����İ����
	 */
	public void onPrint() {
		if (null == table.getParmValue() || table.getParmValue().getCount() <= 0) {
			this.messageBox("�޴�ӡ����");
			return;
		}
		
		int selRow = table.getSelectedRow();
		if (selRow < 0) {
			this.messageBox("��ѡ����Ҫ��ӡ�����ŵ�������");
			return;
		}
		
		TParm parm = table.getParmValue().getRow(selRow);
		// ��ӡ��Parm
		TParm printParm = new TParm();
		
		String boxCode = parm.getValue("BOX_CODE");
		String bookNo = parm.getValue("BOOK_NO");
		
		if (StringUtils.isEmpty(boxCode) || StringUtils.isEmpty(bookNo)) {
			this.messageBox("��ǰѡ�е������ް�����");
			return;
		}
		
		TParm queryParm = new TParm();
		queryParm.setData("MR_NO", parm.getValue("MR_NO"));
		
		queryParm = MROQueueTool.getInstance().selectMroPrintData(queryParm);
		
		if (queryParm.getErrCode() < 0) {
			this.messageBox("��ѯ������Ϣ����");
			err("ERR:" + queryParm.getErrCode() + queryParm.getErrText()
					+ queryParm.getErrName());
            return;
        }
		
		String barCode = boxCode + "-" + parm.getValue("ADM_TYPE") + "-" + bookNo;
		// ����
		printParm.setData("BAR_CODE", "TEXT", barCode);
		// ������
		printParm.setData("ARC_CODE", "TEXT", "�����:" + barCode);
		// ��������
		printParm.setData("PAT_NAME", "TEXT", "����:" + queryParm.getValue("PAT_NAME", 0));
		// �Ա�
		printParm.setData("SEX", "TEXT", "�Ա�:" + queryParm.getValue("SEX", 0));
		// ��������
		String birthday = queryParm.getValue("BIRTH_DATE", 0);
		if (StringUtils.isNotEmpty(birthday)) {
			birthday = birthday.substring(0, 10);
		}
		// ��������
		printParm.setData("BIRTHDAY", "TEXT", "��������:" + birthday);
		// ������
		printParm.setData("MR_NO", "TEXT", "������:" + queryParm.getValue("MR_NO", 0));
		String tel = "��ϵ�绰:";
		if (StringUtils.isNotEmpty(queryParm.getValue("CELL_PHONE", 0))) {
			tel = tel + queryParm.getValue("CELL_PHONE", 0) + "  " + queryParm.getValue("GUARDIAN1_PHONE", 0);
		} else {
			tel = tel + queryParm.getValue("GUARDIAN1_PHONE", 0);
		}
		// ��ϵ�绰
		printParm.setData("TEL", "TEXT", tel);
		
		// ��ӡʱ��������ӡ���öԻ���
		this.openPrintWindow("%ROOT%\\config\\prt\\MRO\\MROArchivesBarCodePrint.jhw", printParm, true);
	}
	
	/**
	 * ���򰸾��(Сlabel)
	 */
	public void onPrintSmall() {
		if (null == table.getParmValue() || table.getParmValue().getCount() <= 0) {
			this.messageBox("�޴�ӡ����");
			return;
		}
		
		int selRow = table.getSelectedRow();
		if (selRow < 0) {
			this.messageBox("��ѡ����Ҫ��ӡ�����ŵ�������");
			return;
		}
		
		TParm parm = table.getParmValue().getRow(selRow);
		// ��ӡ��Parm
		TParm printParm = new TParm();
		
		String boxCode = parm.getValue("BOX_CODE");
		String bookNo = parm.getValue("BOOK_NO");
		
		if (StringUtils.isEmpty(boxCode) || StringUtils.isEmpty(bookNo)) {
			this.messageBox("��ǰѡ�е������ް�����");
			return;
		}
		
		TParm queryParm = new TParm();
		queryParm.setData("MR_NO", parm.getValue("MR_NO"));
		
		queryParm = MROQueueTool.getInstance().selectMroPrintData(queryParm);
		
		if (queryParm.getErrCode() < 0) {
			this.messageBox("��ѯ������Ϣ����");
			err("ERR:" + queryParm.getErrCode() + queryParm.getErrText()
					+ queryParm.getErrName());
            return;
        }
		
		String barCode = boxCode + "-" + parm.getValue("ADM_TYPE") + "-" + bookNo;
		// ����
		printParm.setData("BAR_CODE", "TEXT", barCode);
		// ������
		printParm.setData("ARC_CODE", "TEXT", "�����:" + barCode);
		// ��������
		printParm.setData("PAT_NAME", "TEXT", "����:" + queryParm.getValue("PAT_NAME", 0));
		// �Ա�
		printParm.setData("SEX", "TEXT", "�Ա�:" + queryParm.getValue("SEX", 0));
		// ��������
		String birthday = queryParm.getValue("BIRTH_DATE", 0);
		if (StringUtils.isNotEmpty(birthday)) {
			birthday = birthday.substring(0, 10);
		}
		// ��������
		printParm.setData("BIRTHDAY", "TEXT", "��������:" + birthday);
		// ������
		printParm.setData("MR_NO", "TEXT", "������:" + queryParm.getValue("MR_NO", 0));
		// סԺ��
		printParm.setData("IPD_NO", "TEXT", "סԺ��:" + queryParm.getValue("IPD_NO", 0));
		// ȥ���̶���ӡ��������Ϊ����Ԥ���������û�ѡ���ӡ����
		this.openPrintWindow("%ROOT%\\config\\prt\\MRO\\MROArchivesBarCodeSmallPrint.jhw", printParm);
	}
	
	/**
	 * �ֲ�
	 */
	public void onSeparate() {
		TParm result = new TParm();
		TParm parm = table.getParmValue();
		int dataLen = 0;
		int selCount = 0;
		int selRow = 0;
		
		if (null == parm || parm.getCount() <= 0) {
			this.messageBox("�޷ֲ�����");
			return;
		}
		
		// ǿ��ʧȥ�༭����
		if (table.getTable().isEditing()) {
			table.getTable().getCellEditor().stopCellEditing();
		}
		
		dataLen = parm.getCount();
		for (int i = 0; i < dataLen; i++) {
			if (parm.getBoolean("FLG", i)) {
				selCount = selCount + 1;
				selRow = i;
			}
		}
		
		if (selCount == 0) {
			this.messageBox("�빴ѡ��Ҫ���зֲ����������");
			return;
		}else if (selCount > 1) {
			this.messageBox("�ֲ����ֻ����Ե�������");
			return;
		}
		
		TParm selRowParm = parm.getRow(selRow);
		TParm queryParm = new TParm();
		queryParm.setData("MR_NO", selRowParm.getValue("MR_NO"));
		queryParm.setData("ADM_TYPE", selRowParm.getValue("ADM_TYPE"));
		queryParm.setData("BOX_CODE", selRowParm.getValue("BOX_CODE"));
		queryParm.setData("BOOK_NO", selRowParm.getValue("BOOK_NO"));
		// ��ѯ���ݿ������µ��ڿ�״̬
		result = MROQueueTool.getInstance().selectMRO_MRV(queryParm);
		
		if (result.getErrCode() < 0) {
			this.messageBox_("ϵͳ����");
			err(result.getErrCode() + " " + result.getErrText());
			return;
		}
		
		if (!StringUtils.equals("1", result.getValue("IN_FLG", 0))) {
			this.messageBox("�ڿⰸ��ſɽ��зֲ����,��ˢ��ҳ���ѯ��ǰ���ݵ��ڿ�״̬");
			return;
		}
		
		// �����ݿ������µ��ڿ�״̬����
		selRowParm.setData("IN_FLG", result.getValue("IN_FLG", 0));
		
		queryParm.removeData("BOOK_NO");
		// ���ݲ����š��ż�ס��ʶ�Ͱ���Ų�ѯ���µ������
		result = MROBorrowTool.getInstance().queryMroMrv(queryParm);
		
		if (result.getErrCode() < 0) {
			this.messageBox_("E0001");
			err(result.getErrCode() + " " + result.getErrText());
			return;
		}
		
		dataLen = result.getCount();
		if (dataLen > 1) {
			// ȡ�����Ĳ��
			String maxBookNo = result.getValue("BOOK_NO", dataLen - 1);
			// ����ŵİ���ſɽ��зֲ�
			if (!StringUtils.equals(maxBookNo, selRowParm.getValue("BOOK_NO"))) {
				this.messageBox("��ͬ������£�����ŵİ���ſɽ��зֲ����");
				return;
			}
		}
		
		// ȡ�������ݵ���ˮ��
		int newSeq = result.getInt("SEQ", dataLen - 1) + 1;
		// ȡ�������ݵĲ��
		String newBookNo = String.format("%02d", Integer.parseInt(result
				.getValue("BOOK_NO", dataLen - 1)) + 1);
		
		// �ж���󳤶�(���Ϊ��λ)
		if (newBookNo.length() > 2) {
			this.messageBox("��ǰ������Ϊ���ֵ�������ٷֲ�");
			return;
		}
		
		selRowParm.setData("SEQ", newSeq);
		selRowParm.setData("BOOK_NO", newBookNo);
		selRowParm.setData("OPT_USER", Operator.getID());
		selRowParm.setData("OPT_TERM", Operator.getIP());
		
		result = MROQueueTool.getInstance().insertMRO_MRV(selRowParm);
		if (result.getErrCode() < 0) {
			this.messageBox_("�ֲ�ʧ��");
			err(result.getErrCode() + " " + result.getErrText());
			return;
		}
		
		this.messageBox("�ֲ�ɹ�");
		// ��������
		this.onQuery();
	}
	
	/**
	 * �ϲ�
	 */
	public void onMerge() {
		TParm result = new TParm();
		TParm parm = table.getParmValue();
		TParm selRowParm = new TParm();
		TParm queryParm = new TParm();
		int dataLen = 0;
		int count = 0;
		List<String> mrNoList = new ArrayList<String>();
		List<String> admTypeList = new ArrayList<String>();
		List<String> bookNoList = new ArrayList<String>();
		
		if (null == parm || parm.getCount() <= 0) {
			this.messageBox("�޺ϲ�����");
			return;
		}
		
		// ǿ��ʧȥ�༭����
		if (table.getTable().isEditing()) {
			table.getTable().getCellEditor().stopCellEditing();
		}
		
		dataLen = parm.getCount();
		for (int i = 0; i < dataLen; i++) {
			if (parm.getBoolean("FLG", i)) {
				queryParm = new TParm();
				queryParm.setData("MR_NO", parm.getValue("MR_NO", i));
				queryParm.setData("ADM_TYPE", parm.getValue("ADM_TYPE", i));
				queryParm.setData("BOX_CODE", parm.getValue("BOX_CODE", i));
				queryParm.setData("BOOK_NO", parm.getValue("BOOK_NO", i));
				// ��ѯ���ݿ������µ��ڿ�״̬
				result = MROQueueTool.getInstance().selectMRO_MRV(queryParm);
				
				if (result.getErrCode() < 0) {
					this.messageBox_("ϵͳ����");
					err(result.getErrCode() + " " + result.getErrText());
					return;
				}
				
				// ֻ���ڿ�״̬�İ���ſ��Խ��кϲ�����
				if (!StringUtils.equals("1", result.getValue("IN_FLG", 0))) {
					this.messageBox("�ڿⰸ��ſɽ��кϲ�����");
					return;
				}
				
				// ��¼��ѡ�Ĳ�����
				if (!mrNoList.contains(parm.getValue("MR_NO", i))) {
					mrNoList.add(parm.getValue("MR_NO", i));
				}
				
				// ��¼��ѡ������
				if (!admTypeList.contains(parm.getValue("ADM_TYPE", i))) {
					admTypeList.add(parm.getValue("ADM_TYPE", i));
				}
				
				// ��¼��ѡ�Ĳ��
				if (!bookNoList.contains(parm.getValue("BOOK_NO", i))) {
					bookNoList.add(parm.getValue("BOOK_NO", i));
				}
				
				count = count + 1;
				selRowParm.addData("MR_NO", parm.getValue("MR_NO", i));
				selRowParm.addData("ADM_TYPE", parm.getValue("ADM_TYPE", i));
				selRowParm.addData("BOX_CODE", parm.getValue("BOX_CODE", i));
				selRowParm.addData("BOOK_NO", parm.getValue("BOOK_NO", i));
			}
		}
		
		selRowParm.setCount(count);
		
		selRowParm.addData("SYSTEM", "COLUMNS", "MR_NO");
		selRowParm.addData("SYSTEM", "COLUMNS", "ADM_TYPE");
		selRowParm.addData("SYSTEM", "COLUMNS", "BOX_CODE");
		selRowParm.addData("SYSTEM", "COLUMNS", "BOOK_NO");
		
		if (selRowParm.getCount() < 2) {
			this.messageBox("�ϲ���������ѡ����������");
			return;
		}
		
		if (mrNoList.size() > 1) {
			this.messageBox("��ͬ�����ŵİ���ſ��Խ��кϲ�");
			return;
		}
		
		if (admTypeList.size() > 1) {
			this.messageBox("��ͬ���͵İ���ſ��Խ��кϲ�");
			return;
		}
		
		// �����²��ȷ�Ͽ�
		result = (TParm) this.openDialog(
				"%ROOT%\\config\\mro\\MROMergeBookNo.x", selRowParm.getValue("BOOK_NO", 0));
		
		// ���ȡ��
		if (null == result) {
			return;
		}
		
		// �ϲ�ǰ��֤
		// ���������Ĳ�����ڵ�ǰ��ѡ������һ����ţ����õ�����֤
		// ����ϲ�ǰ��֤������Ĳ���Ƿ��Ѿ�����
		if (!bookNoList.contains(result.getValue("NEW_BOOK_NO"))) {
			queryParm = new TParm();
			queryParm.setData("MR_NO", selRowParm.getValue("MR_NO", 0));
			queryParm.setData("ADM_TYPE", selRowParm.getValue("ADM_TYPE", 0));
			queryParm.setData("BOX_CODE", selRowParm.getValue("BOX_CODE", 0));
			queryParm.setData("BOOK_NO", result.getValue("NEW_BOOK_NO"));
			
			TParm checkResult = MROQueueTool.getInstance().selectMRO_MRV(queryParm);
			
			if (checkResult.getErrCode() < 0) {
				this.messageBox_("�ϲ�ʧ��");
				err(checkResult.getErrCode() + " " + checkResult.getErrText());
				return;
			}
			
			if (checkResult.getCount() > 0) {
				this.messageBox("������Ĳ���Ѿ����ڣ�����������");
				return;
			}
		}
		
		int rowNum = 0;
		boolean existFlg = false;
		for (int i = 0; i < bookNoList.size(); i++) {
			// ����²�ŵ��ڵ�ǰ�������еĲ�ţ��򽫴������ݹ��˵�
			if (StringUtils.equals(result.getValue("NEW_BOOK_NO"), bookNoList.get(i))) {
				rowNum = i;
				existFlg = true;
				break;
			}
		}
		
		TParm updateParm = new TParm();
		updateParm.setData("MR_NO", selRowParm.getValue("MR_NO", rowNum));
		updateParm.setData("ADM_TYPE", selRowParm.getValue("ADM_TYPE", rowNum));
		updateParm.setData("BOX_CODE", selRowParm.getValue("BOX_CODE", rowNum));
		updateParm.setData("NEW_BOOK_NO", result.getValue("NEW_BOOK_NO"));
		updateParm.setData("OLD_BOOK_NO", selRowParm.getValue("BOOK_NO", rowNum));
		
		// �ϲ������µĲ��
		result = MROBorrowTool.getInstance().updateMroMrvBookNo(updateParm);
		
		if (result.getErrCode() < 0) {
			this.messageBox_("�ϲ�ʧ��");
			err(result.getErrCode() + " " + result.getErrText());
			return;
		}
		
		if (existFlg) {
			// ��������
			selRowParm.removeRow(rowNum);
		}
		// �ϲ���ɾ�����������
		result = TIOM_AppServer.executeAction("action.mro.MROBorrowAction",
				"deleteMroMrv", selRowParm);
		
		if (result.getErrCode() < 0) {
			this.messageBox("�ϲ�ʧ��");
			err(result.getErrCode() + " " + result.getErrText());
			return;
		}

		this.messageBox("�ϲ��ɹ�");
		// ��������
		this.onQuery();
	}
	
	/**
	 * ���Excel
	 */
	public void onExport() {
		TParm parm = table.getShowParmValue();
		if (parm == null || parm.getCount() <= 0) {
			this.messageBox("û����Ҫ����������");
			return;
		}
		
		if (table.getRowCount() > 0) {
			TParm exportParm = table.getShowParmValue(); 
			exportParm.setData("TITLE", "�����������");
			exportParm.setData("HEAD", table.getHeader().replaceAll("ѡ,40,boolean;", ""));
	        String[] header = table.getParmMap().split(";");
	        for (int i = 1; i < header.length; i++) {
	        	exportParm.addData("SYSTEM", "COLUMNS", header[i]);
	        }
	        TParm[] execleTable = new TParm[]{exportParm};
	        ExportExcelUtil.getInstance().exeSaveExcel(execleTable, "�����������");
		}
	}
	
	/**
	 * �õ�ComboBox����
	 * 
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TComboBox getComboBox(String tagName) {
		return (TComboBox) getComponent(tagName);
	}
}
