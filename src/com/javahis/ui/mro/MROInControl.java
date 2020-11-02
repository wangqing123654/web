package com.javahis.ui.mro;

import java.util.HashMap;
import java.util.Map;

import jdo.mro.MROBorrowTool;
import jdo.mro.MROQueueTool;
import jdo.sys.IReportTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import org.apache.commons.lang.StringUtils;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TTableEvent;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>
 * Title: ����������
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
 * @author wangbin 2014.08.25
 * @version 4.0
 */
public class MROInControl extends TControl {
	
	private String pageParam; // ҳ�洫��
	private TTable tableUp; // ������Ϣ����
	private TTable tableDown; // ������ϸ����
	private TTable tableNew; // �½�����
	private TTable tableNewPrint; // �½�������ӡ
	private TParm lendAreaParm;// �������������ؼ�����
	private Map<String, String> lendAreaMap;// �������������ؼ�Map����
	private String bigLabelPrtSwitch;// ��ӡ���ǩ����
	private String smallLabelPrtSwitch;// ��ӡС��ǩ����
	
	/**
	 * ��ʼ��
	 */
	public void onInit() {
		super.onInit();
		this.pageParam = (String) this.getParameter();
		
		if (StringUtils.isEmpty(pageParam)) {
			this.messageBox("ҳ�洫�δ���");
			return;
		}
		
		// ��ʼ��
		this.onInitPage();
	}

	/**
	 * ��ʼ��ҳ��
	 */
	private void onInitPage() {
		tableUp = (TTable) this.getComponent("TABLE_UP");
		tableDown = (TTable) this.getComponent("TABLE_DOWN");
		tableNew = (TTable) this.getComponent("TABLE_NEW");
		tableNewPrint = (TTable) this.getComponent("TABLE_NEW_PRINT");
		
		// ���÷ֲᰴť
		callFunction("UI|separate|setEnabled", false);
		// �½�����������ݹ�ѡ�¼�
		tableNew.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this, "onCheckNewMroData");
		
		// ҳ�����
		String title = "";
		
		// ����Һ�
		if ("O".equals(pageParam)) {
			// ��һҳǩ���ⷽʽ
			getComboBox("IN_TYPE").setSelectedID("0");
			// �ڶ�ҳǩ���ⷽʽ
			getComboBox("NEW_IN_TYPE").setSelectedID("0");
			// ��һҳǩ���������ؼ���ʾ
			this.callFunction("UI|CLINIC_AREA|setVisible", true);
			this.callFunction("UI|ENDEMIC_AREA|setVisible", false);
			
			// �ڶ�ҳǩ���������ؼ���ʾ
			this.callFunction("UI|NEW_CLINIC_AREA|setVisible", true);
			this.callFunction("UI|NEW_ENDEMIC_AREA|setVisible", false);
			// ҳ�����
			title = "���ﲡ��������";
		} else if ("I".equals(pageParam)) {
			// ��һҳǩ���ⷽʽ
			getComboBox("IN_TYPE").setSelectedID("1");
			// �ڶ�ҳǩ���ⷽʽ
			getComboBox("NEW_IN_TYPE").setSelectedID("1");
			// ��һҳǩ���������ؼ���ʾ
			this.callFunction("UI|CLINIC_AREA|setVisible", false);
			this.callFunction("UI|ENDEMIC_AREA|setVisible", true);
			
			// �ڶ�ҳǩ���������ؼ���ʾ
			this.callFunction("UI|NEW_CLINIC_AREA|setVisible", false);
			this.callFunction("UI|NEW_ENDEMIC_AREA|setVisible", true);
			// ҳ�����
			title = "סԺ����������";
			
			// �趨סԺ����ͷ���ݽ���(TABLE_UP)
			String tableUpHeader = this.tableUp.getHeader().replace("CLINIC_AREA", "ENDEMIC_AREA");
			this.tableUp.setHeader(tableUpHeader);
			
			// �趨סԺ����ͷ���ݽ���(TABLE_DOWN)
			String tableDownHeader = this.tableDown.getHeader().replace("�������,60;", "");
			this.tableDown.setHeader(tableDownHeader);
			
			String tableDownParmMap = this.tableDown.getParmMap().replace("QUE_NO;", "");
			this.tableDown.setParmMap(tableDownParmMap);
			
			this.callFunction("UI|TABLE_DOWN|setColumnHorizontalAlignmentData",
					"1,center;2,left;3,center;4,center");
			
			// �ڶ�ҳǩ������ݽ���
			// �趨סԺ����ͷ���ݽ���(TABLE_UP)
			String tableNewHeader = this.tableNew.getHeader().replace("NEW_CLINIC_AREA", "NEW_ENDEMIC_AREA");
			this.tableNew.setHeader(tableNewHeader);
		} else {
			// ��һҳǩ���ⷽʽ
			getComboBox("IN_TYPE").setSelectedID("2");
			// ���ڽ��Ĳ����������½������黹�����Խ��ø�ҳǩ
			callFunction("UI|tTabbedPane_0|setEnabled", false);
			// �ڶ�ҳǩ���ⷽʽ
			getComboBox("NEW_IN_TYPE").setSelectedID("2");
			// ҳ�����
			title = "���Ĳ���������";
			
			// ����/����������
			this.callFunction("UI|CLINIC_AREA|setVisible", false);
			this.callFunction("UI|ENDEMIC_AREA|setVisible", false);
			this.callFunction("UI|LEND_AREA|setVisible", true);
			this.callFunction("UI|LEND_AREA|setEnabled", true);
			
			// �趨���ĳ����ͷ���ݽ���(TABLE_UP)
			String tableUpHeader = this.tableUp.getHeader().replace(",CLINIC_AREA", "");
			this.tableUp.setHeader(tableUpHeader);
			
			String tableUpParmMap = this.tableUp.getParmMap().replace("ADM_AREA_CODE", "LEND_AREA_DESC");
			this.tableUp.setParmMap(tableUpParmMap);
			
			this.callFunction("UI|TABLE_UP|setColumnHorizontalAlignmentData",
					"0,left;1,left;2,left;3,right;4,right");
			
			// �趨���ĳ����ͷ���ݽ���(TABLE_DOWN)
			String tableDownHeader = this.tableDown.getHeader().replace("�������,60;", "��������,80;");
			this.tableDown.setHeader(tableDownHeader);
			
			String tableDownParmMap = this.tableDown.getParmMap().replace("QUE_NO;", "ARCHIVES_TYPE;");
			this.tableDown.setParmMap(tableDownParmMap);
			
			this.callFunction("UI|TABLE_DOWN|setColumnHorizontalAlignmentData",
					"1,center;2,center;3,left;4,center;5,center");
			
			// ����/�����ؼ��趨
			TTextFormat lendArea = ((TTextFormat)this.getComponent("LEND_AREA"));
			lendAreaParm = MROQueueTool.getInstance().selectMroLendArea();
			lendArea.setPopupMenuData(lendAreaParm);
			lendArea.setComboSelectRow();
			lendArea.popupMenuShowData();
			
			// ��װ������������Map
			lendAreaMap = new HashMap<String, String>();
			for (int i = 0; i < lendAreaParm.getCount(); i++) {
				lendAreaMap.put(lendAreaParm.getValue("ID", i), lendAreaParm.getValue("NAME", i));
			}
		}
		
		this.setTitle(title);
		
		String now = SystemTool.getInstance().getDate().toString().substring(0,
				10).replace('-', '/');
		// ��һҳǩ
		this.setValue("S_DATE", now); // ��ʼʱ��
		this.setValue("E_DATE", now); // ����ʱ��
		// �ڶ�ҳǩ
		this.setValue("NEW_S_DATE", now); // ��ʼʱ��
		this.setValue("NEW_E_DATE", now); // ����ʱ��
		
        // add by wangbin 2015/1/4 ���ӽ������Ƿ��Զ���ӡ��ǩ���ص��ж� START
		// ȡ�ô�ӡ��ǩ����
        bigLabelPrtSwitch = IReportTool.getInstance().getPrintSwitch("MROInArchivingBigLabel.prtSwitch");
        smallLabelPrtSwitch = IReportTool.getInstance().getPrintSwitch("MROInArchivingSmallLabel.prtSwitch");
        // add by wangbin 2015/1/4 ���ӽ������Ƿ��Զ���ӡ��ǩ���ص��ж� END
	}
	
	/**
	 * ��ѯ
	 */
	public boolean onQuery() {
		boolean flg = false;
        //��ȡ��ǰѡ���ҳǩ ����
        int selectedPage = ((TTabbedPane)this.getComponent("tTabbedPane_0")).getSelectedIndex();
        
        // ��֤������
        this.validate(selectedPage);
        
        if (selectedPage == 0) {
        	flg = this.onQueryOne(selectedPage);
        } else {
        	flg = this.onQueryTwo(selectedPage);
        }
        
        
        
        return flg;
	}
	
	/**
	 * ��һҳǩ��ѯ�������
	 */
	private boolean onQueryOne(int selectedPage) {
		clearValue("BOX_CODE;MR_NO;PAT_NAME;DEPT_CODE;DR_CODE");

		TParm queryParm = this.getQueryParm(selectedPage);
		
		// ��ѯ�����Ĳ�������
		TParm resultParmUp = MROQueueTool.getInstance().selectMroOutIn(queryParm);
		if (resultParmUp.getCount() <= 0) {
			tableDown.setParmValue(new TParm());
			tableUp.setParmValue(new TParm());
			messageBox("��������");
			return false;
		}
		
		// �������
		if ("L".equals(pageParam)) {
			// ����������ʾ����(��������)
			for (int i = 0; i < resultParmUp.getCount(); i++) {
				resultParmUp.addData("LEND_AREA_DESC", lendAreaMap
						.get(resultParmUp.getValue("ADM_AREA_CODE", i)));
			}
		}
		
		tableUp.setParmValue(resultParmUp);
		
		// ��ѯ������������
		TParm resultParmDown = MROQueueTool.getInstance().selectMroOutInDetail(queryParm);
		
		if (resultParmDown.getErrCode() < 0) {
			this.messageBox("��ѯ�����������ݳ���");
			err("ERR:" + resultParmDown.getErrCode() + resultParmDown.getErrText()
					+ resultParmDown.getErrName());
			return false;
		}
		
		// ����ϸ�������Ӱ���������
		for (int i = 0; i < resultParmDown.getCount(); i++) {
			if (StringUtils.equals("I", resultParmDown.getValue("ADM_TYPE", i))) {
				resultParmDown.addData("ARCHIVES_TYPE", "סԺ����");
			} else {
				resultParmDown.addData("ARCHIVES_TYPE", "���ﲡ��");
			}
		}
		
		this.tableDown.setParmValue(resultParmDown);
		
		// �趨����
		grabFocus("BOX_CODE");
		
		return true;
	}
	
	/**
	 * �ڶ�ҳǩ��ѯ�������
	 */
	private boolean onQueryTwo(int selectedPage) {
		// ���������
		tableNew.setLockRows("");
		
		// ��ѯ�����Ĳ�������
		TParm resultParm = MROQueueTool.getInstance().selectMroNew(this.getQueryParm(selectedPage));
		if (resultParm.getCount() <= 0) {
			tableNew.setParmValue(new TParm());
			messageBox("��������");
			//------------add yanglu 20181116 begin ---------------
	        this.clearValue("NEW_MR_NO") ;
			//------------add yanglu 20181116 end ---------------
			return false;
		}
		
		// add by wangbin 2015/2/10 ������� #968 ���ӽ���״̬�� START
		int dataCount = resultParm.getCount();
		for (int i = 0; i < dataCount; i++) {
			resultParm.addData("ARCHIVING_STATUS", "δ����");
			if (!StringUtils.isEmpty(resultParm.getValue("BOX_CODE", i))) {
				resultParm.setData("ARCHIVING_STATUS", i, "�ѽ���");
			}
		}
		// add by wangbin 2015/2/10 ������� #968 ���ӽ���״̬�� END
		
		tableNew.setParmValue(resultParm);
		
		// �趨����
		grabFocus("NEW_MR_NO");
		//------------add yanglu 20181116 begin ---------------
        this.clearValue("NEW_MR_NO") ;
		//------------add yanglu 20181116 end ---------------
		
		return true;
	}
	
	/**
	 * ���ݲ����Ų�ѯ
	 */
	public void onQueryByMrNo() {
		// ȡ�ò�����
		String mrNo = this.getValueString("NEW_MR_NO").trim();
		if (StringUtils.isEmpty(mrNo)) {
			return;
		} else {
			Pat pat = Pat.onQueryByMrNo(mrNo);
			mrNo = pat.getMrNo();
			this.setValue("NEW_MR_NO", mrNo);
			// ��ѯ����
			if (this.onQuery()) {
				// ���ݲ����Ų�ѯ�����ݺ��Զ���ѡ���������Ƿ񽨵��Ի���
				tableNew.setItem(0, "FLG", "Y");
				tableNew.setSelectedRow(0);
				this.onCheckNewMroData(tableNew);
			}
		}
	}
	
	/**
	 * ��ȡ��ѯ��������
	 * 
	 * @return parm
	 */
	private TParm getQueryParm(int selectedPage) {
		TParm queryParm = new TParm();
		
        if (selectedPage == 0) {
        	// ���״̬(0_�Ǽ�δ����,1_�ѳ���,2_�ѹ黹)
    		queryParm.setData("ISSUE_CODE", "1");
    		queryParm.setData("S_DATE", this.getValueString("S_DATE").substring(0, 10));
    		queryParm.setData("E_DATE", this.getValueString("E_DATE").substring(0, 10));
    		// ��ⷽʽ
    		queryParm.setData("OUT_TYPE", getComboBox("IN_TYPE").getSelectedID());
    		if (!StringUtils.equals("L", this.pageParam)) {
    			// �ż�ס��ʶ
    			queryParm.setData("ADM_TYPE", this.pageParam);
    		}
    		// ������������ڿ�״̬(0_δ����,1_�ڿ�,2����)
    		queryParm.setData("IN_FLG", "2");
    		
    		// ����Һ�
    		if ("O".equals(pageParam)) {
    			if (StringUtils.isNotEmpty(this.getValueString("CLINIC_AREA"))) {
    				// ����
    				queryParm.setData("ADM_AREA_CODE", this.getValueString("CLINIC_AREA"));
    			}
    		} else if ("I".equals(pageParam)) {
    			if (StringUtils.isNotEmpty(this.getValueString("ENDEMIC_AREA"))) {
    				// ����
    				queryParm.setData("ADM_AREA_CODE", this.getValueString("ENDEMIC_AREA"));
    			}
    		} else {
    			if (StringUtils.isNotEmpty(this.getValueString("LEND_AREA"))) {
    				// ����/����
    				queryParm.setData("ADM_AREA_CODE", this.getValueString("LEND_AREA"));
    			}
    		}
        } else {
        	// ��ѯʱ�䷶Χ
    		queryParm.setData("NEW_S_DATE", this.getValueString("NEW_S_DATE").substring(0, 10));
    		queryParm.setData("NEW_E_DATE", this.getValueString("NEW_E_DATE").substring(0, 10));
    		// �ż�ס��ʶ
    		queryParm.setData("ADM_TYPE", this.pageParam);
    		// �½���
    		queryParm.setData("BOOK_BUILD", "Y");
    		// ԤԼȡ�����
    		queryParm.setData("CANCEL_FLG", "N");
    		// ����ȷ�ϱ��
    		queryParm.setData("CONFIRM_STATUS", "0");
    		// �ڿ���(0_δ����)
    		queryParm.setData("IN_FLG", "0");
    		
    		// ����Һ�
    		if ("O".equals(pageParam)) {
    			if (StringUtils.isNotEmpty(this.getValueString("NEW_CLINIC_AREA"))) {
    				// ����
    				queryParm.setData("ADM_AREA_CODE", this.getValueString("NEW_CLINIC_AREA"));
    			}
    		} else if ("I".equals(pageParam)) {
    			if (StringUtils.isNotEmpty(this.getValueString("NEW_ENDEMIC_AREA"))) {
    				// ����
    				queryParm.setData("ADM_AREA_CODE", this.getValueString("NEW_ENDEMIC_AREA"));
    			}
    		}
    		
    		if (StringUtils.isNotEmpty(this.getValueString("NEW_DEPT_CODE"))) {
    			// ����
				queryParm.setData("DEPT_CODE", this.getValueString("NEW_DEPT_CODE"));
    		}
    		
    		if (StringUtils.isNotEmpty(this.getValueString("NEW_MR_NO"))) {
    			// ҽ��
				queryParm.setData("MR_NO", this.getValueString("NEW_MR_NO"));
    		}
        }
		
		return queryParm;
	}
	
	/**
	 * ���
	 */
	public void onClear() {
        //��ȡ��ǰѡ���ҳǩ ����
        int selectedPage = ((TTabbedPane)this.getComponent("tTabbedPane_0")).getSelectedIndex();
        String now = SystemTool.getInstance().getDate().toString().substring(0,
				10).replace('-', '/');
        
        if (selectedPage == 0) {
    		// ���ð����
//    		this.callFunction("UI|BOX_CODE|setEnabled", false);
    		clearValue("CLINIC_AREA;ENDEMIC_AREA;BOX_CODE;MR_NO;PAT_NAME;DEPT_CODE;DR_CODE;LEND_AREA");
    		tableUp.setParmValue(new TParm());
    		tableDown.setParmValue(new TParm());
    		
    		// ��һҳǩ
    		this.setValue("S_DATE", now); // ��ʼʱ��
    		this.setValue("E_DATE", now); // ����ʱ��
        } else {
        	clearValue("NEW_CLINIC_AREA;NEW_ENDEMIC_AREA;NEW_DEPT_CODE;NEW_DR_CODE;NEW_MR_NO");
    		
    		// �ڶ�ҳǩ
    		this.setValue("NEW_S_DATE", now); // ��ʼʱ��
    		this.setValue("NEW_E_DATE", now); // ����ʱ��
        	
        	tableNew.setParmValue(new TParm());
        }
	}
	
	/**
	 * ���������Ϣ
	 */
	private boolean onSave(TParm selParm) {
		TParm executeParm = new TParm();
		TParm result = new TParm();
		// ������Ա
		String optUser = Operator.getID();
		// ������ĩ
		String optTerm = Operator.getIP();

		// ���·������
		selParm.setData("TYPE", "IN");
		selParm.setData("OPT_USER", optUser);
		selParm.setData("OPT_TERM", optTerm);
		// ����״̬(0_δ����,1_�ѳ���,2_�ѹ黹)
		selParm.setData("ISSUE_CODE", "2");
		// �����ڿ�״̬
		selParm.setData("IN_FLG", "1");
		// �黹�����Ա
		selParm.setData("IN_PERSON", selParm.getValue("DR_CODE"));
		// ��ǰ���Ŀ���
		selParm.setData("CURT_LEND_DEPT_CODE", "");
		// ��ǰ������Ա
		selParm.setData("CURT_LEND_DR_CODE", "");

		executeParm.setData("MRV", selParm.getData());
		executeParm.setData("Queue", selParm.getData());

		// ִ�в�������
		result = TIOM_AppServer.executeAction("action.mro.MROQueueAction",
				"updateMroOutIn", executeParm);

		if (result.getErrCode() < 0) {
			this.messageBox("��ǰ�������ʧ��");
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return false;
		}

		// ���ɨ�赽�İ����
		clearValue("BOX_CODE");
		// �趨����
		grabFocus("BOX_CODE");

		return true;
	}
	
	/**
	 * ����Żس��¼�(ɨ�谸�������)
	 */
	public void onSaveByBoxCode() {
		TParm parmDown = this.tableDown.getParmValue();
		TParm parmUp = this.tableUp.getParmValue();
		
		if (StringUtils.isEmpty(this.getValueString("BOX_CODE").trim())) {
			return;
		}
		
		if (null == parmUp || parmUp.getCount() <= 0) {
			this.messageBox("�޾�������");
			return;
		}
		
		if (null == parmDown || parmDown.getCount() <= 0) {
			this.messageBox("�޾�������");
			return;
		}
		
		int len = parmDown.getCount();
		boolean existFlg = false;
		
		// ������
		String archivesNo = "";
		for (int i = 0; i < len; i++) {
			archivesNo = parmDown.getValue("BOX_CODE", i) + "-" + parmDown.getValue("ADM_TYPE", i) + "-" + parmDown.getValue("BOOK_NO", i);
			// �ҵ���Ӧ���������
			if (StringUtils.equals(archivesNo, this.getValueString("BOX_CODE"))) {
				
				// �����ǰ�����Ѿ�����ѡ�������κδ���
				if (parmDown.getBoolean("FLG", i)) {
					this.messageBox("��ǰ�����Ѿ�������");
					return;
				}
				
				if (this.onSave(parmDown.getRow(i))) {
					// ��ѡ
					tableDown.setItem(i, "FLG", "Y");
					// �趨������
					this.setValue("MR_NO", parmDown.getValue("MR_NO", i));
					// �趨����
					this.setValue("PAT_NAME", parmDown.getValue("PAT_NAME", i));
					// �趨����
					this.setValue("DEPT_CODE", parmDown.getValue("DEPT_CODE", i));
					// �趨ҽ��
					this.setValue("DR_CODE", parmDown.getValue("DR_CODE", i));
					
					// ʵ����������
					this.onTableUpCount(parmDown.getRow(i));
				}
				
				existFlg = true;
				break;
			}
		}
		
		// û���ҵ���Ӧ�İ����
		if (!existFlg) {
			this.messageBox("�޸ð��������");
			// ���ɨ�赽�İ����
			clearValue("BOX_CODE");
			// �趨����
			grabFocus("BOX_CODE");
			return;
		}
	}
	
	/**
	 * ������������
	 */
	private void onTableUpCount(TParm selParmDown) {
		TParm parmUp = tableUp.getParmValue();
		// ���ڲ�ͬ���ⷽʽ��Ӧҳ��������в�ͬ��ȡ�ö�Ӧ�����һ��λ�ã�����������
		int outPosition = this.tableUp.getParmMap().split(";").length;
		
		for (int i = 0; i < parmUp.getCount(); i++) {
			if (StringUtils.equals(selParmDown.getValue("QUE_DATE"), parmUp
					.getValue("QUE_DATE", i))
					&& StringUtils.equals(selParmDown.getValue("ADM_TYPE"),
							parmUp.getValue("ADM_TYPE", i))
					&& StringUtils.equals(selParmDown.getValue("DEPT_CODE"),
							parmUp.getValue("DEPT_CODE", i))
					&& StringUtils.equals(selParmDown.getValue("DR_CODE"),
							parmUp.getValue("DR_CODE", i))) {
				
				// �ҵ���Ӧ�����ݣ�ѡ�У���������ʵ�ʳ�������
				tableUp.setSelectedRow(i);
				int outCount = Integer.parseInt(String.valueOf(tableUp
						.getValueAt(i, outPosition - 1)));

				if (0 == outCount) {
					tableUp.setValueAt("1", i, outPosition - 1);
				} else {
					outCount = outCount + 1;
					tableUp.setValueAt(String.valueOf(outCount), i, outPosition - 1);
				}
				
				// ��ǰ����ҽ���µ����д�����İ�������󣬶�Ӧ��TABLE_UP�е�������Ҫ���²�ѯ
				if (StringUtils.equals(
						String.valueOf(tableUp.getValueAt(i, outPosition - 2)), String
								.valueOf(tableUp.getValueAt(i, outPosition - 1)))) {
					tableUp.removeRow(i);
				}
				
				break;
			}
		}
	}
	
	/**
	 * ��ӡ�����ⲡ������
	 */
	public void onPrint() {
		int selectedPage = ((TTabbedPane)this.getComponent("tTabbedPane_0")).getSelectedIndex();
		if (selectedPage == 0) {
			// ��ӡ���ȷ�ϵ�
			this.onPrintMroIn();
		} else {
			this.onPrintNewMro(selectedPage);
		}
	}
	
	/**
	 * ��ӡ���ȷ�ϵ�
	 */
	private void onPrintMroIn () {
		TParm parm = tableUp.getShowParmValue();
		
		if (parm == null || parm.getCount() <= 0) {
			this.messageBox("�޴�ӡ����");
			return;
		}
		
		int dataLen = parm.getCount();
		
		// ��ӡ��Parm
		TParm printParm = new TParm();
		// ��ӡ����
		TParm printData = new TParm();
		
		for (int i = 0; i < dataLen; i++) {
			if (!"L".equals(pageParam)) {
				// ����/����
				printData.addData("ADM_AREA", parm.getValue("ADM_AREA_CODE", i));
			} else {
				// ��������/����
				printData.addData("ADM_AREA", parm.getValue("LEND_AREA_DESC", i));
			}

			// ����
			printData.addData("DEPT_CODE", parm.getValue("DEPT_CODE", i));
			// ҽ��
			printData.addData("DR_CODE", parm.getValue("DR_CODE", i));
			// Ӧ�������
			printData.addData("EXPECTED_IN_COUNT", parm.getValue("EXPECTED_OUT_COUNT", i));
		}
		
		printParm.setData("TITLE", "TEXT", "�������ȷ�ϵ�");
		
		printData.setCount(dataLen);
		
		printData.addData("SYSTEM", "COLUMNS", "ADM_AREA");
		printData.addData("SYSTEM", "COLUMNS", "DEPT_CODE");
		printData.addData("SYSTEM", "COLUMNS", "DR_CODE");
		printData.addData("SYSTEM", "COLUMNS", "EXPECTED_IN_COUNT");
		
		printParm.setData("PRINT_TABLE", printData.getData());
		
		this.openPrintWindow("%ROOT%\\config\\prt\\MRO\\MROInPrint.jhw", printParm);
	}
	
	/**
	 * ��ӡ�½������鵵
	 */
	private void onPrintNewMro (int selectedPage) {
		TParm parm = MROQueueTool.getInstance().selectMroNewToPrint(this.getQueryParm(selectedPage));
		tableNewPrint.setParmValue(parm);
		parm = tableNewPrint.getShowParmValue();
		
		if (parm == null || parm.getCount() <= 0) {
			this.messageBox("�޴�ӡ����");
			return;
		}
		
		int dataLen = parm.getCount();
		
		// ��ӡ��Parm
		TParm printParm = new TParm();
		// ��ӡ����
		TParm printData = new TParm();
		
		for (int i = 0; i < dataLen; i++) {
			// ����/����
			printData.addData("ADM_AREA", parm.getValue("ADM_AREA_CODE", i));
			// ����
			printData.addData("DEPT_CODE", parm.getValue("DEPT_CODE", i));
			// ҽ��
			printData.addData("DR_CODE", parm.getValue("DR_CODE", i));
			// Ӧ�������
			printData.addData("EXPECTED_IN_COUNT", parm.getValue("EXPECTED_IN_COUNT", i));
		}
		
		printParm.setData("TITLE", "TEXT", "�½������鵵���ȷ�ϵ�");
		
		printData.setCount(dataLen);
		
		printData.addData("SYSTEM", "COLUMNS", "ADM_AREA");
		printData.addData("SYSTEM", "COLUMNS", "DEPT_CODE");
		printData.addData("SYSTEM", "COLUMNS", "DR_CODE");
		printData.addData("SYSTEM", "COLUMNS", "EXPECTED_IN_COUNT");
		
		printParm.setData("PRINT_TABLE", printData.getData());
		
		this.openPrintWindow("%ROOT%\\config\\prt\\MRO\\MROInPrint.jhw", printParm);
	}
	
	/**
	 * ��ѡ�½������鵵�����¼�
	 */
	public void onCheckNewMroData(Object obj) {
		TParm result = new TParm();
		// ǿ��ʧȥ�༭����
		if (this.tableNew.getTable().isEditing()) {
			this.tableNew.getTable().getCellEditor().stopCellEditing();
		}
		// ��ǰѡ�е��к�
		int selRow = tableNew.getSelectedRow();
		// ��ǰѡ�е�������
		TParm parm = tableNew.getParmValue().getRow(selRow);
		// ������Ա
		String optUser = Operator.getID();
		// ������ĩ
		String optTerm = Operator.getIP();
		// ��ǰ��������
		String lockedRows = "";
		// ��ѯ����
		TParm queryParm = new TParm();
		
		if (parm.getBoolean("FLG")) {
			// modify by wangbin 2015/01/23 ����ǰ����֤�Ƿ��Ѿ����ڰ����
			queryParm.setData("ADM_TYPE", pageParam);
			queryParm.setData("MR_NO", parm.getValue("MR_NO"));
			
			// modify by wangbin 2015/01/26 ������֤�ò�����ǰ�Ľ���״̬
			result = PatTool.getInstance().getInfoForMrno(parm.getValue("MR_NO"));
			
			if (result.getErrCode() < 0) {
				this.messageBox("��ѯ������Ϣ����");
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
	            return;
			}
			
			if (!StringUtils.equalsIgnoreCase("Y", result.getValue("BOOK_BUILD", 0))) {
				this.messageBox("��ǰ�����Ѿ�ȡ������");
				// ��յ�ǰ��
				tableNew.removeRow(selRow);
				return;
			}
			
			// ��ѯ�ò����ŵİ�����Ϣ
			result = MROQueueTool.getInstance().selectMRO_MRV(queryParm);
			
			if (result.getErrCode() < 0) {
				this.messageBox("��ѯ����ų���");
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
	            return;
			}
			
			// �����ѯ���ò����ŵİ�������
			if (result.getCount() > 0
					&& StringUtils.isNotEmpty(result.getValue("BOOK_NO", 0))) {
				this.messageBox("�ò����Ѿ�����������");
				tableNew.setValueAt(result.getValue("BOX_CODE", 0), selRow, 6);
				tableNew.setValueAt(result.getValue("BOOK_NO", 0), selRow, 7);
				// add by wangbin 2015/2/10 ������� #968 ���ӽ���״̬�� START
				tableNew.setValueAt("�ѽ���", selRow, 8);
				// add by wangbin 2015/2/10 ������� #968 ���ӽ���״̬�� END
				
				// add by wangbin 2015/2/4 �ڲ����� #980 ���ﲡ�����鵵�����������ȷ�Ͻ�������״̬��ͬ�������� START
				TParm updateParm = new TParm();
				updateParm.setData("MRO_REGNO", parm.getValue("MRO_REGNO"));
				updateParm.setData("SEQ", parm.getValue("SEQ"));
				updateParm.setData("OPT_USER", optUser);
				updateParm.setData("OPT_TERM", optTerm);
				
				// ���´�����ȷ��״̬
				result = MROBorrowTool.getInstance().updateConfirmStatus(updateParm);
				
				if (result.getErrCode() < 0) {
					this.messageBox("���´�����״̬����");
					err("ERR:" + result.getErrCode() + result.getErrText()
							+ result.getErrName());
		            return;
		        }
				// add by wangbin 2015/2/4 �ڲ����� #980 ���ﲡ�����鵵�����������ȷ�Ͻ�������״̬��ͬ�������� END
				
				lockedRows = tableNew.getLockRows() + "," + String.valueOf(selRow);
				// ��ѡ����������
				tableNew.setLockRows(lockedRows);
				return;
			}
			
			int sel = this.messageBox("����", "�Ƿ񽨵���", 1);

			// ѡ�񽨵�
			if (sel == 0) {
				
				result = MROQueueTool.getInstance().selectMaxBoxCode(queryParm);
				
				if (result.getErrCode() < 0) {
					this.messageBox("��ѯ����ų���");
					err("ERR:" + result.getErrCode() + result.getErrText()
							+ result.getErrName());
		            return;
		        }
				
				int newBoxCode = 0;
				// ȡ�õ�ǰ���ݿ��е���󰸾��
				String maxBoxCode = result.getValue("MAX_BOX_CODE", 0);
				
				if (StringUtils.isEmpty(maxBoxCode)) {
					newBoxCode = 1;
				} else {
					newBoxCode = Integer.parseInt(maxBoxCode) + 1;
				}
				
				// ����λ,�����µİ����
				String boxCode = String.format("%06d", newBoxCode);
				// �µĲ��
				String bookNo = "01";
				
				// �����ڿ�״̬����Ϊ�ڿ�
				parm.setData("IN_FLG", "1");
				parm.setData("BOX_CODE", boxCode);
				parm.setData("BOOK_NO", bookNo);
				parm.setData("OPT_USER", optUser);
				parm.setData("OPT_TERM", optTerm);
				
				// ���䰸���
				result = MROQueueTool.getInstance().updateMroMrvBoxCode(parm);
				
				if (result.getErrCode() < 0) {
					this.messageBox("���䰸��ų���");
					err("ERR:" + result.getErrCode() + result.getErrText()
							+ result.getErrName());
		            return;
		        }
				
				TParm updateParm = new TParm();
				updateParm.setData("MRO_REGNO", parm.getValue("MRO_REGNO"));
				updateParm.setData("SEQ", parm.getValue("SEQ"));
				updateParm.setData("OPT_USER", optUser);
				updateParm.setData("OPT_TERM", optTerm);
				
				// �½������鵵�󣬸��´�����ȷ��״̬
				result = MROBorrowTool.getInstance().updateConfirmStatus(updateParm);
				
				if (result.getErrCode() < 0) {
					this.messageBox("���´�����״̬����");
					err("ERR:" + result.getErrCode() + result.getErrText()
							+ result.getErrName());
		            return;
		        }
				
				tableNew.setValueAt(boxCode, selRow, 6);
				tableNew.setValueAt(bookNo, selRow, 7);
				// add by wangbin 2015/2/10 ������� #968 ���ӽ���״̬�� START
				tableNew.setValueAt("�ѽ���", selRow, 8);
				// add by wangbin 2015/2/10 ������� #968 ���ӽ���״̬�� END
				
				lockedRows = tableNew.getLockRows() + "," + String.valueOf(selRow);
				// ��ѡ����������
				tableNew.setLockRows(lockedRows);
				
	            // add by wangbin 2015/1/4 ���ӽ������Ƿ��Զ���ӡ��ǩ���ص��ж� START
				if (StringUtils.equals(bigLabelPrtSwitch, IReportTool.ON)
						|| StringUtils.equals(smallLabelPrtSwitch,
								IReportTool.ON)) {
					TParm printParm = new TParm();
					// ������
					printParm.setData("BAR_CODE", boxCode + "-" + pageParam
							+ "-" + bookNo);
					// ������
					printParm.setData("MR_NO", parm.getValue("MR_NO"));
					// ��ӡ�½��鵵�����İ����
					this.onPrintArchivesBarCode(printParm);
				}
	            // add by wangbin 2015/1/4 ���ӽ������Ƿ��Զ���ӡ��ǩ���ص��ж� END
			} else if (sel == 1) {
				// ������Ϣ�������
				parm.setData("BOOK_BUILD", "N");
				// ��տͷ�����״̬���
				result = MROQueueTool.getInstance().updateSysPatInfoBookBuild(parm);
				
				if (result.getErrCode() < 0) {
					this.messageBox("ϵͳ����");
					err("ERR:" + result.getErrCode() + result.getErrText()
							+ result.getErrName());
		            return;
		        }
				
				// ��յ�ǰ��
				tableNew.removeRow(selRow);
			} else {
				tableNew.setItem(selRow, "FLG", "N");
				return;
			}
		}
	}
	
	/**
	 * ҳǩ��ѡ�¼�
	 */
	public void onTabChange() {
        //��ȡ��ǰѡ���ҳǩ ����
        int selectedPage = ((TTabbedPane)this.getComponent("tTabbedPane_0")).getSelectedIndex();
        
        if (selectedPage == 0) {
    		// �趨����
    		grabFocus("BOX_CODE");
    		
    		// ���÷ֲᰴť
    		callFunction("UI|separate|setEnabled", false);
        } else {
    		// �趨����
    		grabFocus("NEW_MR_NO");
    		
    		// ���÷ֲᰴť
    		callFunction("UI|separate|setEnabled", true);
        }
	}
	
	
	/**
	 * ���������
	 * 
	 * @param selectedPage
	 *            ѡ�е�ҳǩ
	 */
	private void validate(int selectedPage) {
        if (selectedPage == 0) {
    		if (StringUtils.isEmpty(this.getValueString("S_DATE"))
    				|| StringUtils.isEmpty(this.getValueString("E_DATE"))) {
    			this.messageBox("������黹���ڷ�Χ");
    			return;
    		}
        } else {
    		if (StringUtils.isEmpty(this.getValueString("NEW_S_DATE"))
    				|| StringUtils.isEmpty(this.getValueString("NEW_E_DATE"))) {
    			this.messageBox("�������ѯ���ڷ�Χ");
    			return;
    		}
        }
	}
	
	/**
	 * �ֲ�
	 */
	public void onSeparate() {
		if (null == tableNew.getShowParmValue()
				|| tableNew.getShowParmValue().getCount() <= 0) {
			this.messageBox("�޷ֲ�����");
			return;
		}
		
		// ǿ��ʧȥ�༭����
		if (this.tableNew.getTable().isEditing()) {
			this.tableNew.getTable().getCellEditor().stopCellEditing();
		}
		
		// ��ǰѡ�е��к�
		int selRow = tableNew.getSelectedRow();
		// ��ǰѡ�е�������
		TParm selParm = tableNew.getShowParmValue().getRow(selRow);
		
		if (selParm.getBoolean("FLG")) {
			if (StringUtils.isEmpty(selParm.getValue("BOX_CODE"))) {
				this.messageBox("���Ƚ��й鵵����");
				return;
			}
		} else {
			this.messageBox("�빴ѡ����");
			return;
		}
		
		// �����ż�ס���
		selParm.setData("ADM_TYPE", pageParam);
		
		// �����ֲ�ҳ��(ģ̬��)
		this.openDialog("%ROOT%\\config\\mro\\MROArchivesManage.x", selParm);
	}
	
	/**
	 * ��ӡ�½��鵵�����İ����
	 */
	private void onPrintArchivesBarCode(TParm parm) {
		// ��ӡ��Parm
		TParm printParm = new TParm();
		
		TParm queryParm = new TParm();
		queryParm.setData("MR_NO", parm.getValue("MR_NO"));
		
		queryParm = MROQueueTool.getInstance().selectMroPrintData(queryParm);
		
		if (queryParm.getErrCode() < 0) {
			this.messageBox("��ѯ������Ϣ����");
			err("ERR:" + queryParm.getErrCode() + queryParm.getErrText()
					+ queryParm.getErrName());
            return;
        }
		
		// ����
		printParm.setData("BAR_CODE", "TEXT", parm.getValue("BAR_CODE"));
		// ������
		printParm.setData("ARC_CODE", "TEXT", "�����:" + parm.getValue("BAR_CODE"));
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
		
		if (StringUtils.equals(bigLabelPrtSwitch, IReportTool.ON)) {
			// ��ӡ���ǩ
			this.openPrintWindow(
					"%ROOT%\\config\\prt\\MRO\\MROArchivesBarCodePrint.jhw",
					printParm, true);
		}
		
		if (StringUtils.equals(smallLabelPrtSwitch, IReportTool.ON)) {
			// С��ǩһ�δ�ӡ����
			for (int i = 0; i < 4; i++) {
				this.openPrintWindow("%ROOT%\\config\\prt\\MRO\\MROArchivesBarCodeSmallPrint.jhw",
						printParm, true);
			}
		}
	}
	
	/**
	 * ���Excel
	 */
	public void onExport() {
        //��ȡ��ǰѡ���ҳǩ ����
        int selectedPage = ((TTabbedPane)this.getComponent("tTabbedPane_0")).getSelectedIndex();
        TTable table = new TTable();
        String title = "";
        
        // ���ݵ�ǰѡ�е�ҳǩȡ��Ӧ�ı������
        if (selectedPage == 0) {
        	table = tableDown;
        	title = "���ⲡ�����鵵";
        } else {
        	table = tableNew;
        	title = "�½��������鵵";
        }
		
		TParm parm = table.getShowParmValue();
		if (null == parm || parm.getCount("MR_NO") <= 0) {
			this.messageBox("û����Ҫ����������");
			return;
		}
		
		if (table.getRowCount() > 0) {
			parm.setData("TITLE", title);
			// ������Excel������ȥ����ѡ����һ��
			parm.setData("HEAD", table.getHeader().replaceAll("ѡ,30,boolean;", ""));
	        String[] header = table.getParmMap().split(";");
	        for (int i = 1; i < header.length; i++) {
	        	parm.addData("SYSTEM", "COLUMNS", header[i]);
	        }
	        TParm[] execleTable = new TParm[]{parm};
	        ExportExcelUtil.getInstance().exeSaveExcel(execleTable, title);
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
