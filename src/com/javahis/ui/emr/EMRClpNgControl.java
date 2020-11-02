package com.javahis.ui.emr;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTree;
import com.dongyang.ui.TTreeNode;
import com.dongyang.ui.TWord;
import com.dongyang.ui.event.TTreeEvent;

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
 * Copyright: Copyright (c) 2012
 * </p>
 * 
 * <p>
 * Company: bluecore
 * </p>
 * 
 * @author shibl
 * @version 1.0
 */
public class EMRClpNgControl extends TControl {
	private static final String TREE = "TREE";
	/**
	 * �õ�TWORD����
	 */
	private static final String WORD = "WORD";

	private TWord word;
	/**
	 * ����
	 */
	private TTreeNode root;

	public void onInit() {
		// JDO��һ�����ڲ������ݿ�,������װ�˶����ݿ�����Ĵ��������
		super.onInit();
		initWord();
		// ˫��ѡ������Ŀ
		addEventListener(TREE + "->" + TTreeEvent.DOUBLE_CLICKED,
				"onTreeDoubleClicked");
		loadTree();
		String subClassCode = (String) this.getParameter();
		if (subClassCode != null) {
			this.onOpenTemplet(subClassCode);
		}
	}

	/**
	 * ��ʼ��WORD
	 */
	public void initWord() {
		word = this.getTWord(WORD);
		this.word.setCanEdit(false);
	}

	/**
	 * �õ�WORD����
	 * 
	 * @param tag
	 *            String
	 * @return TWord
	 */
	public TWord getTWord(String tag) {
		return (TWord) this.getComponent(tag);
	}

	/**
	 * ��ʼ����
	 */
	public void loadTree() {
		// �õ�Tree�Ļ�������
		String sql = "SELECT * FROM EMR_CLASS WHERE CLASS_CODE>'EMR90' AND PARENT_CLASS_CODE IS NOT NULL";
		String rootsql = "SELECT * FROM EMR_CLASS WHERE CLASS_DESC='�ٴ�֪ʶ��'";
		TParm result = new TParm(this.getDBTool().select(sql));
		TParm rootParm = new TParm(this.getDBTool().select(rootsql));
		root = this.getTTree(TREE).getRoot();
		root.setText("�ٴ�֪ʶ��");
		root.setType("Root");
		root.setID(rootParm.getValue("CLASS_CODE", 0));
		// ��ʼ������ǰ���������Ҷ�ӽڵ�
		root.removeAllChildren();
		// ��ʼ��ʼ����������
		int count = result.getCount();
		for (int i = 0; i < count; i++) {
			TParm dataParm = result.getRow(i);
			// �ڵ�
			TTreeNode treeNode = new TTreeNode();
			treeNode.setText(dataParm.getValue("CLASS_DESC"));
			treeNode.setID(dataParm.getValue("CLASS_CODE"));
			treeNode.setType("2");
			putNodeInTree(dataParm, treeNode);
		}
		this.getTTree(TREE).update();

	}

	/**
	 * 
	 * @param dataParm
	 *            TParm
	 * @param root
	 *            TTreeNode
	 */
	private void putNodeInTree(TParm dataParm, TTreeNode treeNode) {
		// �Ƿ�Ҷ�ڵ�,��ģ��
		if (dataParm.getValue("LEAF_FLG").equals("Y")) {
			String leafsql = "SELECT  * FROM  EMR_TEMPLET WHERE CLASS_CODE='"
					+ treeNode.getID() + "'";
			TParm leafParm = new TParm(this.getDBTool().select(leafsql));
			if (leafParm.getCount() > 0) {
				for (int i = 0; i < leafParm.getCount(); i++) {
					TTreeNode LEAFNode = new TTreeNode();
					LEAFNode.setText(leafParm.getValue("SUBCLASS_DESC", i));
					LEAFNode.setID(leafParm.getValue("SUBCLASS_CODE", i));
					LEAFNode.setType("4");
					LEAFNode.setData(leafParm.getRow(i));
					treeNode.addSeq(LEAFNode);
				}
			}
		}
		String sql = "SELECT  * FROM  EMR_CLASS WHERE CLASS_CODE IN (SELECT PARENT_CLASS_CODE FROM EMR_CLASS WHERE CLASS_CODE='"
				+ treeNode.getID() + "')";
		TParm result = new TParm(this.getDBTool().select(sql));
		// ĸ�ڵ�
		TTreeNode ParentNode = new TTreeNode();
		ParentNode.setText(result.getValue("CLASS_DESC", 0));
		ParentNode.setID(result.getValue("CLASS_CODE", 0));
		ParentNode.setType("2");
		// �ڵ�Ϊ���ڵ�
		if (treeNode.getID().equals(root.getID())) {
			return;
		}
		// �˽ڵ����
		if (root.findNodeForID(treeNode.getID()) != null) {
			return;
		}
		// �˽ڵ��ĸ�ڵ����
		else if (root.findNodeForID(ParentNode.getID()) != null) {
			root.findNodeForID(ParentNode.getID()).addSeq(treeNode);
		}
		// �˽ڵ��ĸ�ڵ㲻����
		else {
			ParentNode.addSeq(treeNode);
			putNodeInTree(result.getRow(0), ParentNode);
		}
	}

	/**
	 * ����ѡ�����¼�
	 * 
	 * @param obj
	 *            Object
	 */
	public void onTreeDoubleClicked(Object obj) {
		if (obj == null) {
			return;
		}
		TTreeNode node = this.getTTree(TREE).getSelectNode();
		// �����ģ��ſɴ�;
		if (node.getType().equals("4")) {
			onOpenTemplet();
		}
	}

	/**
	 * ��ģ��
	 */
	public void onOpenTemplet() {
		TTreeNode node = this.getTTree(TREE).getSelectNode();
		String templetPath = ((TParm) node.getData()).getValue("TEMPLET_PATH");
		String templetName = ((TParm) node.getData()).getValue("EMT_FILENAME");
		// ��ģ��
		word.onOpen(templetPath, templetName, 2, false);
		// ���ɱ༭
		this.word.setCanEdit(false);
	}

	/**
	 * �򿪲���ģ��
	 * 
	 * @param subClassCode
	 *            String
	 */
	public void onOpenTemplet(String subClassCode) {
		String sql = "SELECT EMT_FILENAME,CLASS_CODE,TEMPLET_PATH,SYSTEM_CODE FROM EMR_TEMPLET WHERE SUBCLASS_CODE='"
				+ subClassCode + "'";
		TParm parm = new TParm(this.getDBTool().select(sql));
		String templetPath = parm.getValue("TEMPLET_PATH", 0);
		String templetName = parm.getValue("EMT_FILENAME", 0);
		// ��ģ��
		word.onOpen(templetPath, templetName, 2, false);
		// ���ɱ༭
		this.word.setCanEdit(false);
		word.onPreviewWord();
	}
	/**
	 * ��ӡ
	 */
	public void onPrint() {
		if (this.word.getFileOpenName() != null) {
			this.word.onPreviewWord();
			this.word.print();
		} else {
			// ��ѡ����
			this.messageBox("E0099");
		}
	}
	/**
	 * ʵ����
	 */
	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}

	/**
	 * 
	 * @param tag
	 *            String
	 * @return TTree
	 */
	public TTree getTTree(String tag) {
		return (TTree) this.getComponent(tag);
	}
}
