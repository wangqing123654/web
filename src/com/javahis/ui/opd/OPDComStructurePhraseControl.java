package com.javahis.ui.opd;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTree;
import com.dongyang.ui.TTreeNode;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;

import jdo.spc.StringUtils;
import jdo.sys.Operator;
import com.dongyang.ui.event.TTreeEvent;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TCheckBox;
import com.dongyang.util.StringTool;
import jdo.sys.SystemTool;
import com.dongyang.util.TMessage;
import com.dongyang.data.TSocket;
import com.dongyang.manager.TIOM_FileServer;
import com.javahis.util.StringUtil;
import com.dongyang.tui.text.CopyOperator;

/**
 * <p>
 * Title:
 * </p>
 *
 * <p>
 * Description:
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 *
 * <p>
 * Company:
 * </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class OPDComStructurePhraseControl extends TControl {
	private static final String PHRASE_FILE_PATH = "JHW/Ƭ��";
	/**
	 * ������
	 */
	private static final String TREE = "TREE";
	/**
	 * ���������б�
	 */
	private static final String DEPT_CODE = "DEPT_CODE";

	// TABLE_PHRASE
	private static final String TABLE_PHRASE = "TABLE_PHRASE";

	// �����п���ҽʦ����
	private String deptOrDr = "";
	// �����п��ҡ�ҽʦ����
	private String deptOrDrCode = "";

	/**
	 * Ƭ����루������
	 */
	private static final String CLASS_CODE = "CODE";

	/**
	 * Ƭ������;
	 */
	private static final String PHRASE_CODE = "PHRASE_NAME";
	/**
	 * ƴ����;
	 */
	private static final String PHRASE_PY1 = "PY1";

	/**
	 * ������
	 */
	private static final String PHRASE_PY2 = "PY2";

	/**
	 * ˳���
	 */
	private static final String PHRASE_SEQ = "SEQ";

	/**
	 * ��Ƭ����
	 */
	private static final String IS_PHRASE_FLG = "CHK_ISPhrase";
	/**
	 * ��Ƭ����
	 */
	private static final String IS_PRM_FLG = "CHK_ISPRIMARY";

	private TTree tree;
	// COMBOBOX ����
	private TComboBox deptCombo;
	//
	private TTable table;
	//
	private TTextField classCode;

	// Ƭ������Ƭ������
	private TTextField phraseCode;

	private TTextField py1;

	private TTextField py2;

	private TTextField seq;

	private TCheckBox chkIsPhrase;
	private TCheckBox chkIsPri;

	public OPDComStructurePhraseControl() {
	}

	public void onInit() {
		super.onInit();
		deptOrDr = this.getParameter() + "";
		// this.messageBox("===deptOrDr==="+deptOrDr);
		if ("1".equalsIgnoreCase(deptOrDr)) {
			deptOrDrCode = Operator.getDept();
		}

		// ��ʼ����
		onInitTree();
		// ��ʼ������;
		deptCombo = (TComboBox) this.getComponent(DEPT_CODE);
		deptCombo.setSelectedID(Operator.getDept());
		deptCombo.setVisible(true);
		deptCombo.setEnabled(false);

		//
		classCode = (TTextField) this.getComponent(CLASS_CODE);
		phraseCode = (TTextField) this.getComponent(PHRASE_CODE);
		py1 = (TTextField) this.getComponent(PHRASE_PY1);
		py2 = (TTextField) this.getComponent(PHRASE_PY2);
		seq = (TTextField) this.getComponent(PHRASE_SEQ);
		chkIsPhrase = (TCheckBox) this.getComponent(IS_PHRASE_FLG);
		chkIsPri = (TCheckBox) this.getComponent(IS_PRM_FLG);

		// ���б�
		table = (TTable) this.getComponent(TABLE_PHRASE);
		// �����¼�
		// ��TREE��Ӽ����¼�
		addEventListener("TREE->" + TTreeEvent.CLICKED, "onTreeClicked");

		callFunction("UI|TABLE_PHRASE|addEventListener", "TABLE_PHRASE->" + TTableEvent.CLICKED, this,
				"onTableClicked");

		// ����˫����Ƭ��༭
		table.addEventListener("TABLE_PHRASE->" + TTableEvent.DOUBLE_CLICKED, this, "onTableDoubleClicked");

	}

	/**
	 * ��ʼ����
	 */
	private void onInitTree() {
		// this.messageBox("==onInitTree==");
		tree = (TTree) callMessage("UI|TREE|getThis");
		tree.getRoot().removeAllChildren();
		TTreeNode treeRoot = (TTreeNode) callMessage("UI|TREE|getRoot");
		// ȡ���ڵ�;
		TParm rootParam = this.getTreeRoot();
		treeRoot.setText(rootParam.getValue("PHRASE_CODE", 0));
		treeRoot.setType("Root");
		treeRoot.setID(rootParam.getValue("CLASS_CODE", 0));
		//
		// �õ����ݼ���
		TParm nodesParam = this.getNodes();

		if (nodesParam == null) {
			return;
		}
		int nodesCount = nodesParam.getCount();
		// ȫ������Ȩ��
		boolean deptAll = this.getPopedem("deptAll");
		deptAll = false;// 5745 ������ģ��ά��δ�����ҷ���
		// ��ǰ��¼����
		String deptCode = Operator.getDept();

		for (int i = 0; i < nodesCount; i++) {
			TParm temp = nodesParam.getRow(i);
			// ���ݽڵ����������Ƿ�ΪĿ¼�ڵ�
			String noteType = "1";
			// Ҷ�ڵ�(�ǽṹ��Ƭ��)
			if (nodesParam.getValue("LEAF_FLG", i).equals("Y")) {
				noteType = "4";
			}
			// �����½ڵ�
			TTreeNode PhraseClass = new TTreeNode("PHRASECLASS" + i, noteType);
			// ��ERM������Ϣ���õ��ڵ㵱��
			PhraseClass.setText(nodesParam.getValue("PHRASE_CODE", i));
			PhraseClass.setID(nodesParam.getValue("CLASS_CODE", i));

			// ��һ���Ľڵ��������
			if (nodesParam.getValue("PARENT_CLASS_CODE", i).equals("ROOT")) {
				treeRoot.addSeq(PhraseClass);
			}
			// ��������Ľڵ������Ӧ�ĸ��ڵ�����
			else {
				if (nodesParam.getValue("PARENT_CLASS_CODE", i).length() != 0) {
					// ����Ҷ�ڵ㣨Ƭ��ڵ㣩,�ǵ�ǰ���ҵļ��룻
					// modify by wangb 2017/11/14 ���տ��ҹ�������
					if (deptAll) {
						treeRoot.findNodeForID(nodesParam.getValue("PARENT_CLASS_CODE", i)).add(PhraseClass);
					} else if (StringUtils.equals(nodesParam.getValue("DEPTORDR_CODE", i), deptCode)) {
						// modify by wangb 2017/12/18 ������ԭʼ���ݽṹ���ҵ��¿���
						if (treeRoot.findNodeForID(nodesParam.getValue("PARENT_CLASS_CODE", i)) != null) {
							treeRoot.findNodeForID(nodesParam.getValue("PARENT_CLASS_CODE", i)).add(PhraseClass);
						}
					}
				}
			}

		}
		tree.update();

	}

	private TParm getTreeRoot() {
		TParm result = new TParm(this.getDBTool().select(
				"SELECT CLASS_CODE,PHRASE_CODE,LEAF_FLG,PARENT_CLASS_CODE FROM OPD_COMTEMPLATE_PHRASE WHERE PARENT_CLASS_CODE IS NULL"));
		return result;

	}

	private TParm getNodes() {
		// TParm result = new TParm(this.getDBTool().select(
		// "SELECT CLASS_CODE,PHRASE_CODE,LEAF_FLG,PARENT_CLASS_CODE,DEPTORDR_CODE FROM
		// OPD_COMTEMPLATE_PHRASE WHERE DEPT_OR_DR='1' AND DEPTORDR_CODE = '020702'
		// ORDER BY CLASS_CODE,SEQ"));
		TParm result = new TParm(this.getDBTool().select(
				"SELECT CLASS_CODE,PHRASE_CODE,LEAF_FLG,PARENT_CLASS_CODE,DEPTORDR_CODE FROM OPD_COMTEMPLATE_PHRASE  ORDER BY CLASS_CODE,SEQ"));
		return result;
	}

	/**
	 * ���ĵ����¼�
	 */
	public void onTreeClicked() {
		// this.messageBox("==come in=="+getAutoClassCode());
		if (tree.getSelectNode() == null) {
			return;
		}
		// this.messageBox(" select id" + tree.getSelectNode().getID());
		this.clearValue("CODE;PHRASE_NAME;PY1;PY2;SEQ;CHK_ISPhrase;CHK_ISPRIMARY");
		table.setParmValue(new TParm());

		String sql = "SELECT * FROM OPD_COMTEMPLATE_PHRASE WHERE 1=1";
		sql += " AND PARENT_CLASS_CODE='" + tree.getSelectNode().getID() + "'";

		// add by wangb 2017/11/14 ���˵�ǰ����
		if (!this.getPopedem("deptAll")) {
			sql += " AND DEPTORDR_CODE = '" + Operator.getDept() + "' ";
		}

		sql += " ORDER BY SEQ";
		// ��ѯѡ�нڵ��µģ�Ƭ������Ƭ��;
		TParm result = new TParm(this.getDBTool().select(sql));
		// this.messageBox("result=="+result);

		// ���ֵ;
		if (result == null || result.getCount() <= 0) {
			// this.messageBox("û�в�ѯ����");
		} else {
			table.setParmValue(result);
		}

	}

	/**
	 * �����޸�
	 * 
	 * @param row
	 *            int
	 */
	public void onTableClicked(int row) {
		//// System.out.println("=========onTableClicked============" + row);
		if (row < 0) {
			return;
		}
		// ��ֵ��
		// Ƭ��ID
		classCode.setValue(table.getParmValue().getValue("CLASS_CODE", row));
		phraseCode.setValue(table.getParmValue().getValue("PHRASE_CODE", row));
		py1.setValue(table.getParmValue().getValue("PY1", row));
		py2.setValue(table.getParmValue().getValue("PY2", row));
		seq.setValue(table.getParmValue().getValue("SEQ", row));
		chkIsPhrase.setValue(table.getParmValue().getValue("LEAF_FLG", row));
		chkIsPri.setValue(table.getParmValue().getValue("MAIN_FLG", row));
	}

	/**
	 * public void onEntryGetPY(){ String py1 =
	 * TMessage.getPy(phraseCode.getValue()); py1.split(py1); }
	 **/
	/**
	 * ƴ���¼�
	 */
	public void onPY() {
		String py1 = StringUtil.onCode(this.getValueString("PHRASE_NAME"));
		this.setValue("PY1", py1);
	}

	/**
	 * �������;
	 */
	public void onSave() {
		int selected = table.getSelectedRow();
		// У��;
		if (phraseCode.getValue() == null || phraseCode.getValue().equals("")) {
			this.messageBox("����д���ƣ�");
			return;
		}
		String selectedId = tree.getSelectionNode().getID();
		if (selectedId == null || selectedId.equals("")) {
			this.messageBox("��ѡ��ģ����࣡");
			return;
		}
		// ��Ҷ�ڵ�(��Ƭ��)
		if (isPhrase(selectedId)) {
			this.messageBox("ģ��ڵ��²������ӣ�");
			return;
		}
		// this.messageBox("===selectedId==="+selectedId);

		String mainClass = getMainCode(selectedId);
		// this.messageBox("===mainClass==="+mainClass);

		String userID = Operator.getID();
		String userIP = Operator.getIP();
		String sysDate = StringTool.getString(SystemTool.getInstance().getDate(), "yyyy/MM/dd HH:mm:ss");

		// this.messageBox("==py=="+TMessage.getPy(phraseCode.getValue()));
		String py1 = TMessage.getPy(phraseCode.getValue());
		// ����
		if (selected == -1) {
			String id = this.getAutoClassCode();
			String sql = "INSERT INTO OPD_COMTEMPLATE_PHRASE(CLASS_CODE,PHRASE_CODE,DEPT_OR_DR,DEPTORDR_CODE,";
			sql += "PY1,PY2,SEQ,LEAF_FLG,MAIN_FLG,PARENT_CLASS_CODE,OPT_USER,OPT_DATE,OPT_TERM,MAIN_CLASS_CODE)";
			sql += " VALUES('" + id + "','" + phraseCode.getValue() + "','" + deptOrDr + "','" + deptOrDrCode + "','"
					+ py1 + "','" + py2.getValue() + "','" + seq.getValue() + "','" + chkIsPhrase.getValue() + "','"
					+ chkIsPri.getValue() + "','" + tree.getSelectionNode().getID() + "','" + userID + "',TO_DATE('"
					+ sysDate + "','YYYY/MM/DD HH24:MI:SS') ,'" + userIP + "','" + mainClass + "')";

			//// System.out.println("=====INSERT INTO sql======" + sql);
			TParm parm = new TParm(this.getDBTool().update(sql));
			if (parm.getErrCode() < 0) {
				this.messageBox("����ʧ�ܣ�");
				return;

			} else {
				this.messageBox("����ɹ���");
			}
			// �޸�;
		} else {
			// this.messageBox("chkIsPri.getValue()" + chkIsPri.getValue());
			String sql = "UPDATE OPD_COMTEMPLATE_PHRASE SET PHRASE_CODE='" + phraseCode.getValue() + "',";
			sql += "PY1='" + py1 + "',";
			sql += "PY2='" + py2.getValue() + "',";
			sql += "SEQ='" + seq.getValue() + "',";
			sql += "LEAF_FLG='" + chkIsPhrase.getValue() + "',";
			sql += "MAIN_FLG='" + chkIsPri.getValue() + "',";
			sql += "OPT_USER='" + userID + "',";
			sql += "OPT_DATE=TO_DATE('" + sysDate + "','YYYY/MM/DD HH24:MI:SS') ,";
			sql += "OPT_TERM='" + userIP + "',";
			sql += "MAIN_CLASS_CODE='" + mainClass + "'";
			sql += " WHERE CLASS_CODE='" + classCode.getValue() + "'";
			//// System.out.println("====update sql=====" + sql);
			TParm parm = new TParm(this.getDBTool().update(sql));
			if (parm.getErrCode() < 0) {
				this.messageBox("����ʧ�ܣ�");
				return;

			} else {
				this.messageBox("����ɹ���");
			}

		}
		// ˢ�±��
		onTreeClicked();
		// ˢ�����ڵ�;
		// tree.update();
		TTreeNode selectNode = tree.getSelectNode();
		onInitTree();
		tree.expandRow(selectNode);
	}

	/**
	 * �༭�¼�
	 */
	public void onEdit() {
		int row = table.getSelectedRow();
		onTableDoubleClicked(row);
	}

	/**
	 * ��ղ���;
	 */
	public void onClear() {
		this.clearValue("CODE;PHRASE_NAME;PY1;PY2;SEQ;CHK_ISPhrase;CHK_ISPRIMARY");
		table.clearSelection();
	}

	/**
	 * ɾ������
	 */
	public void onDelete() {
		// ɾ�����ݿ�
		if (this.messageBox("ѯ��", "�Ƿ�ɾ��", 2) == 0) {

			if (table.getSelectedRow() == -1) {
				return;
			}
			int selRow = table.getSelectedRow();
			TParm tableParm = table.getParmValue();
			String id = tableParm.getValue("CLASS_CODE", selRow);

			// �ж��Ƿ����ӽڵ㣬������ӽڵ㣬 ����ɾ��
			String selectSQL = "SELECT CLASS_CODE FROM OPD_COMTEMPLATE_PHRASE WHERE PARENT_CLASS_CODE='" + id + "'";
			TParm childs = new TParm(this.getDBTool().select(selectSQL));
			// ���ӽڵ�
			if (childs.getCount() > 0) {
				this.messageBox("������ģ�棬����ɾ����");
				return;
			}

			// FILE_PATH;FILE_NAME
			String filePath = tableParm.getValue("FILE_PATH", selRow);
			String fileName = tableParm.getValue("FILE_NAME", selRow);

			String sql = "DELETE FROM  OPD_COMTEMPLATE_PHRASE  WHERE CLASS_CODE ='" + id + "'";

			TParm result = new TParm(this.getDBTool().update(sql));
			if (result.getErrCode() < 0) {
				// messageBox(result.getErrText());
				this.messageBox("ɾ��ʧ�ܣ�");
				return;
			}
			// �ɹ���ɾ����ӦƬ���ļ�;
			this.delFileTempletFile(filePath, fileName);
			// ɾ��������ʾ
			int row = (Integer) callFunction("UI|TABLE_PHRASE|getSelectedRow");
			if (row < 0) {
				return;
			}
			this.callFunction("UI|TABLE_PHRASE|removeRow", row);
			this.callFunction("UI|TABLE_PHRASE|setSelectRow", row);

			this.messageBox("P0003");
			this.onClear();
			onInitTree();

		} else {
			return;
		}

	}

	/**
	 * ���˫���¼�����EMRƬ��༭����
	 * 
	 * @param row
	 *            int
	 */
	public void onTableDoubleClicked(int row) {
		// �򿪽ṹ��Ƭ��༭����;
		if (row < 0) {
			return;
		}
		// ����Ƭ��
		if (!table.getParmValue().getValue("LEAF_FLG", row).equals("Y")) {
			return;
		}
		// �Ƿ�����ѱ���·��
		// NEW|EDIT
		String seletedPhraseFilePath = table.getParmValue().getValue("FILE_PATH", row);
		String seletedPhraseFileName = table.getParmValue().getValue("FILE_NAME", row);
		/**
		 * this.messageBox("===onTableDoubleClicked seletedPhraseFilePath===" +
		 * seletedPhraseFilePath);
		 **/

		TParm inParm = new TParm();
		// �޸�
		if (seletedPhraseFilePath != null && !seletedPhraseFilePath.equals("")) {
			inParm.setData("opType", "EDIT");
			inParm.setData("phraseFilePath", seletedPhraseFilePath);
			inParm.setData("phraseFileName", seletedPhraseFileName);
		} else {
			// ����;
			inParm.setData("opType", "NEW");
			inParm.setData("phraseFilePath", PHRASE_FILE_PATH);
			inParm.setData("phraseFileName", table.getParmValue().getValue("CLASS_CODE", row));
		}

		inParm.setData("classCode", table.getParmValue().getValue("CLASS_CODE", row));
		/**
		 * inParm.setData("deptOrDrCode", deptOrDrCode); inParm.setData("phraseCode",
		 * table.getParmValue().getValue("PHRASE_CODE", row));
		 **/

		this.openDialog("%ROOT%\\config\\emr\\EMREditComPhrase.x", inParm);
		// ˢһ�±�
		onTreeClicked();

	}

	/**
	 * �Ƿ���Ƭ��ڵ�;
	 * 
	 * @param nodeID
	 *            String
	 * @return boolean
	 */
	private boolean isPhrase(String nodeID) {
		boolean flg = false;
		String sql = "SELECT LEAF_FLG FROM OPD_COMTEMPLATE_PHRASE WHERE CLASS_CODE='" + nodeID + "'";
		TParm parm = new TParm(this.getDBTool().update(sql));
		if (parm.getValue("LEAF_FLG", 0).equals("Y")) {
			return true;
		}
		return flg;
	}

	/**
	 * �Զ�ȡclassCode;
	 * 
	 * @return String
	 */
	private synchronized String getAutoClassCode() {
		/**
		 * String s = UUID.randomUUID().toString(); //ȥ����-������ return s.substring(0, 8) +
		 * s.substring(9, 13) + s.substring (14, 18) + s.substring(19, 23) +
		 * s.substring(24);
		 **/
		// �ĳ�ͨ���Զ���������
		// ����ȡ����ȡֵ��
		String id = SystemTool.getInstance().getNo("ALL", "ODO", "EMRTEMPLATE_NO", "EMRTEMPLATE_NO");
		return id;

	}

	/**
	 * ɾ��ģ���ļ�
	 * 
	 * @param templetPath
	 *            String
	 * @param templetName
	 *            String
	 * @return boolean
	 */
	public boolean delFileTempletFile(String templetPath, String templetName) {
		// Ŀ¼���һ����Ŀ¼FILESERVER
		String rootName = TIOM_FileServer.getRoot();
		// ģ��·��������
		String templetPathSer = TIOM_FileServer.getPath("EmrTemplet");
		// �õ�SocketͨѶ����
		TSocket socket = TIOM_FileServer.getSocket();
		// ɾ���ļ�
		return TIOM_FileServer.deleteFile(socket,
				rootName + templetPathSer + templetPath + "\\" + templetName + ".jhw");
	}

	/**
	 * �ݹ�ȡ������;
	 * 
	 * @param selectedId
	 *            String
	 * @return String
	 */
	private String getMainCode(String selectedId) {
		// ȡselectedId�ڵ��µĸ�ID
		//
		String sql = "SELECT CLASS_CODE,PARENT_CLASS_CODE FROM OPD_COMTEMPLATE_PHRASE WHERE 1=1";
		sql += " AND CLASS_CODE=+'" + selectedId + "'";
		TParm result = new TParm(this.getDBTool().select(sql));
		// this.messageBox("PARENT_CLASS_CODE"+result.getValue("PARENT_CLASS_CODE", 0));
		if (result.getValue("PARENT_CLASS_CODE", 0).equals("ROOT")
				|| result.getValue("PARENT_CLASS_CODE", 0).equals("")) {
			return result.getValue("CLASS_CODE", 0);
		} else {
			return this.getMainCode(result.getValue("PARENT_CLASS_CODE", 0));
		}
		// return "";
	}

	/**
	 * �������ݿ��������
	 * 
	 * @return TJDODBTool
	 */
	private TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}

	/**
	 * ���ϵͳ������
	 */
	public void onClearMenu() {
		CopyOperator.clearComList();
	}

}
