package com.javahis.ui.emr;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

import jdo.bil.BILSysParmTool;
import jdo.sys.Operator;
import jdo.sys.SYSRuleTool;
import jdo.sys.SystemTool;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.tui.text.ECapture;
import com.dongyang.tui.text.ECheckBoxChoose;
import com.dongyang.tui.text.EComponent;
import com.dongyang.tui.text.EFixed;
import com.dongyang.tui.text.EMacroroutine;
import com.dongyang.tui.text.EPage;
import com.dongyang.tui.text.EPanel;
import com.dongyang.tui.text.IBlock;
import com.dongyang.ui.TTree;
import com.dongyang.ui.TTreeNode;
import com.dongyang.ui.TWord;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.event.TTreeEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TList;
import com.javahis.util.OdiUtil;
/**
*
* <p>Title: �����ɼ���ѯ</p>
*
* <p>Description: </p>
*
* <p>Copyright: Copyright (c) 2008</p>
*
* <p>Company: JavaHis</p>
*
* @author 20140320 yanjing
* @version 4.5
*/

public class EMRSignsCollectControl extends TControl {
	String mrNo = "";//ʱ�̱��
	String patName = "";//�ײͱ��
	String deptCode = "";//�ײͱ��
	private boolean flg = false;
	private String caseNo;
	private String FILE_SEQ;
	private String COM = getEktPort();// ̩�Ļ������Ʋ���
	private String str = "";
	public static final String TN_EMT_FILENAME = "����";
	/**
	 * ����������
	 */
	private static final String actionName = "action.odi.ODIAction";
	/**
	 * ��
	 */
	private String yearStr="";
	/**
	 * ��
	 */
	private String mouthStr="";
	/**
	 * ��������� add yanjing 20130326
	 */
	private TParm emrChildParm = new TParm();
	/**
     * ����
     */
    private TTreeNode treeRoot;
    
    /**
	 * ֪ʶ��ǰ׺
	 */
	private static final String KNOWLEDGE_STORE_PREFIX = "EMR";
	/**
	 * WORD����
	 */
	private static final String TWORD = "WORD";
    
    /**
	 * WORD����
	 */
	private TWord word;
	/**
	 * ��������
	 */
	private static final String TREE_NAME = "TREE";
    
    /**
     * ��Ź�����𹤾�
     */
    SYSRuleTool ruleTool;
    
    /**
     * �������ݷ���datastore���ڶ��������ݹ���
     */
    TDataStore treeDataStore = new TDataStore();
    /**
     * ��ʼ��
     */
    public void onInit() { // ��ʼ������
        super.onInit();
        initPage();
        // ��ʼ����
        onInitSelectTree();
        // ��tree��Ӽ����¼�
        initEven();
     // ��ʼ��WORD
		initWord();
		
    }
    public void initWord() {
		word = this.getTWord(TWORD);
		this.setWord(word);
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
	 * 
	 * @param word
	 *            TWord
	 */
	public void setWord(TWord word) {
		this.word = word;
	}

	public TWord getWord() {
		return this.word;
	}
	public String getCaseNo() {
		return caseNo;
	}

	public void setCaseNo(String caseNo) {
		this.caseNo = caseNo;
	}

	public String getMrNo() {
		return mrNo;
	}

	public void setMrNo(String mrNo) {
		this.mrNo = mrNo;
	}
	public TParm getEmrChildParm() {
		return emrChildParm;
	}
	public void setEmrChildParm(TParm emrChildParm) {
		this.emrChildParm = emrChildParm;
	}
	/**
	 * ��ǰ�༭״̬
	 */
	private String onlyEditType;
	public String getOnlyEditType() {
		return onlyEditType;
	}

	public void setOnlyEditType(String onlyEditType) {
		this.onlyEditType = onlyEditType;
	}

	/**
	 * ��ʼ������
	 */
	public void initPage() {
		//
		Object obj = this.getParameter();
		String patName = "";
		if (obj != null) {
			this.setMrNo(((TParm) obj).getValue("MR_NO"));
			this.setCaseNo(((TParm) obj).getValue("CASE_NO"));
			patName = ((TParm) obj).getValue("PAT_NAME");
			deptCode =  ((TParm) obj).getValue("DEPT_CODE");
		} else {
			// test
			this.setMrNo("000000159780");
			this.setCaseNo("130716000433");
		}
		yearStr = caseNo.substring(0, 2);
		// this.setAdmYear(yearStr);
		mouthStr = caseNo.substring(2, 4);
		this.setValue("MR_NO", mrNo);
		this.setValue("PAT_NAME", patName);
		//��ʼ��ʱ��ؼ�
		//��ʼ������
//		Timestamp date = getDateForInit(queryFirstDayOfLastMonth(StringTool.getString(
//				SystemTool.getInstance().getDate(),"yyyyMMdd")));
        Timestamp time = SystemTool.getInstance().getDate();
//        setValue("S_DATE", date);
        setValue("S_DATE", StringTool.rollDate(time, -365));  //add by huangtt 20150629 
        setValue("E_DATE", time);
	}
    /**
	 * �õ���
	 * 
	 * @param tag
	 *            String
	 * @return TTree
	 */
	public TTree getTTree(String tag) {
		return (TTree) this.getComponent(tag);
	}
    /**
     * ��ʼ����
     */
    public void onInitSelectTree() {
    	treeRoot = this.getTTree(TREE_NAME).getRoot();
		treeRoot.setText("�����ɼ�");
		treeRoot.setEnText("Hospital");
		treeRoot.setType("Root");
		treeRoot.removeAllChildren();
		boolean queryFlg = false;
		// �õ��ڵ�����
		TParm parm = this.getRootNodeData();
		if (parm.getInt("ACTION", "COUNT") == 0) {
			// û������EMRģ�壡�������ݿ�����
			this.messageBox("E0105");
			return;
		}
		int rowCount = parm.getInt("ACTION", "COUNT");
		for (int i = 0; i < rowCount; i++) {
			TParm mainParm = parm.getRow(i);
			// �ӽڵ� EMR_CLASS���е�STYLE�ֶ�
			TTreeNode node = new TTreeNode();
			node.setID(mainParm.getValue("CLASS_CODE"));
			node.setText(mainParm.getValue("SUBCLASS_DESC"));
//			node.setEnText(mainParm.getValue("ENG_DESC"));
			// ���ò˵���ʾ��ʽ
			node.setType("Root");
			node.setGroup(mainParm.getValue("CLASS_CODE"));
			node.setValue(mainParm.getValue("SUBCLASS_DESC"));
			node.setData(mainParm);

			// ��������
//			treeRoot.add(node);
			TTreeNode childrenNode = new TTreeNode(mainParm
					.getValue("SUBCLASS_DESC"), mainParm.getValue("CLASS_STYLE"));
			childrenNode.setID(mainParm.getValue("CLASS_CODE"));
			childrenNode.setGroup(mainParm.getValue("CLASS_CODE"));
			// // mainParm.getValue("SYS_PROGRAM")?????��ʱû��
//			treeRoot.findNodeForID(mainParm.getValue("CLASS_CODE")).add(
//					childrenNode);
			treeRoot.add(childrenNode);
			
			//
			// // ��Ҷ�ڵ�ļ������ļ�
			// if (((String) allChildsParm.getValue("LEAF_FLG", j))
			// .equalsIgnoreCase("Y")) {
			// $$=====Add by lx 2012/10/10 start=======$$//
			TParm childParm = new TParm();
			// �ж��Ƿ����ٴ�֪ʶ�⣬
			// ��������,�����Ӧ�Ķ�Ӧ��֪ʶ���ļ�
//			if (isKnowLedgeStore(mainParm.getValue("SUBCLASS_CODE"))) {
//				childParm = getChildNodeDataByKnw(mainParm
//						.getValue("SUBCLASS_CODE"));
//				// $$=====Add by lx 2012/10/10 end=======$$//
//			} else {
				// ��ѯ���ļ�
//			System.out.println("====mainParm mainParm is ::"+mainParm);
				childParm = this.getRootChildNodeData(mainParm.getValue("SUBCLASS_CODE"),
						mainParm.getValue("CLASS_CODE"),mainParm.getValue("SEQ"), queryFlg);
//			}
			int rowCldCount = childParm.getInt("ACTION", "COUNT");
			for (int z = 0; z < rowCldCount; z++) {
				TParm chlidParmTemp = childParm.getRow(z);
				TTreeNode nodeChlid = new TTreeNode();
				nodeChlid.setID(chlidParmTemp.getValue("FILE_NAME"));
				nodeChlid.setText(chlidParmTemp.getValue("DESIGN_NAME"));
				nodeChlid.setType("4");
				nodeChlid.setGroup(chlidParmTemp.getValue("CLASS_CODE"));
				nodeChlid.setValue(chlidParmTemp.getValue("SUBCLASS_CODE"));
				nodeChlid.setData(chlidParmTemp);
				childrenNode.add(nodeChlid);
			}
			// }

			// }
			// // $$=============end add by lx ����ģ��������޸�; ================$$//
		}
		this.getTTree(TREE_NAME).update();
    }
    public boolean checkSeq(){
    	String seqSql = "SELECT SEQ FROM EMR_TEMPLET WHERE SUBCLASS_CODE IN ("+COM+") ORDER BY SEQ";
    	TParm seqResult = new TParm (this.getDBTool().select(seqSql));
    	for(int i = 0;i<seqResult.getCount();i++){
    		if(seqResult.getInt("SEQ", i)==4){
    			flg = true;
    			return flg;
    		}
    	}
    	return flg;
    }
    
    public TParm getRootNodeData() {
    	TParm result = new TParm();
    	String emrSql = "";
    	if(checkSeq()){
    		 emrSql = "SELECT SUBCLASS_CODE,EMT_FILENAME,SUBCLASS_DESC,SEQ,CLASS_CODE,TEMPLET_PATH,CLASS_STYLE,DEPT_CODE "
				+ "FROM EMR_TEMPLET  "
				+ " WHERE  SUBCLASS_CODE IN ("+COM+") AND SEQ = '4'  "
				+ " ORDER BY SEQ,CLASS_CODE";
    	}else{
    		 emrSql = "SELECT SUBCLASS_CODE,EMT_FILENAME,SUBCLASS_DESC,SEQ,CLASS_CODE,TEMPLET_PATH,CLASS_STYLE,DEPT_CODE "
				+ "FROM EMR_TEMPLET  "
				+ " WHERE  SUBCLASS_CODE IN ("+COM+") "
				+ " ORDER BY SEQ,CLASS_CODE";
    		
    	}
//    	System.out.println("===++++ emrSql emrSql emrSql is ::"+emrSql);
    	result = new TParm(this.getDBTool().select(emrSql));
		return result;
	}
	
	/**
	 * ͨ��֪ʶ��Ҷ�ڵ�ȡ��Ӧ֪ʶ��ģ��
	 * 
	 * @param classCode
	 * @return
	 */
	public TParm getChildNodeDataByKnw(String classCode) {
		TParm result = new TParm();
		String sql = "SELECT   SUBCLASS_CODE, CLASS_CODE, EMT_FILENAME FILE_NAME,";
		sql += "TEMPLET_PATH FILE_PATH, SUBCLASS_DESC DESIGN_NAME ";
		sql += "FROM EMR_TEMPLET ";
		sql += "WHERE CLASS_CODE = '" + classCode + "' ";
		sql += "ORDER BY SEQ";
		result = new TParm(this.getDBTool().select(sql));
//		System.out.println("222222Ҷ�ڵ�������sql is����"+sql);
		return result;
	}
    
    /**
     * �õ�������
     *
     * @return TTree
     */
    public TTree getTree() {
        return (TTree) callFunction("UI|TREE|getThis");
    }
    /**
	 * �õ�debug���
	 * 
	 * @return
	 */
	public String getEktPort() {
		String com = "";
		com = getProp().getString("", "emr.name");
		if (com == null || com.trim().length() <= 0) {
//			 System.out.println("�����ļ�ҽ�ƿ�com��Ǵ���");
		}
//		System.out.println("com com com is ::"+com);
		return com;
	}
    /**
	 * ��ȡ TConfig.x
	 * 
	 * @return TConfig
	 */
	public static TConfig getProp() {
		TConfig config = TConfig
				.getConfig("WEB-INF\\config\\system\\TConfig.x");
		if (config == null) {
//			 System.out.println("TConfig.x �ļ�û���ҵ���");
		}
		return config;
	}
	
	/**
	 * �õ����ڵ�����
	 * 
	 * @return TParm
	 */
	public TParm getRootChildNodeData(String subClassCode,String classCode, String seq,boolean queryFlg) {
		TParm result = new TParm();
		String sDate = this.getValueString("S_DATE").substring(0, 19).replace("-", "").replace("-", "").replace(" ", "").replace(":", "");
		String eDate = this.getValueString("E_DATE").substring(0, 19).replace("-", "").replace("-", "").replace(" ", "").replace(":", "");
		String myTemp =
		// ����ģ��
//		" AND ((B.DEPT_CODE IS NULL AND B.USER_ID IS NULL) "
				// ����ģ��
//				+ " OR (B.DEPT_CODE = '" + Operator.getDept()
//				+ "' AND B.USER_ID IS NULL) "
				// ����ģ��
//				+ " OR B.USER_ID = '" + Operator.getID() + "') " 
				" AND  A.OPT_DATE BETWEEN TO_DATE('" + sDate
				+ "','YYYYMMDDHH24MISS') AND TO_DATE('"
				+ eDate + "','YYYYMMDDHH24MISS') ";
		if(!"".equals(this.getValue("MR_NO"))){
			myTemp+= " AND A.MR_NO = '"+this.getValue("MR_NO")+"'";
		}
				
		String sql = "SELECT A.CASE_NO,A.FILE_SEQ,A.MR_NO,A.IPD_NO,A.FILE_PATH,A.FILE_NAME,A.DESIGN_NAME,A.CLASS_CODE,A.SUBCLASS_CODE,A.DISPOSAC_FLG,"
				+ " A.CREATOR_USER,A.CREATOR_DATE,A.CURRENT_USER,A.CANPRINT_FLG,A.MODIFY_FLG,"
				+ " A.CHK_USER1,A.CHK_DATE1,A.CHK_USER2,A.CHK_DATE2,A.CHK_USER3,A.CHK_DATE3,"
				+ " A.COMMIT_USER,A.COMMIT_DATE,A.IN_EXAMINE_USER,A.IN_EXAMINE_DATE,A.DS_EXAMINE_USER,A.DS_EXAMINE_DATE,A.PDF_CREATOR_USER,A.PDF_CREATOR_DATE,"
				+ " B.SUBCLASS_DESC,B.DEPT_CODE,B.RUN_PROGARM,B.SUBTEMPLET_CODE,B.CLASS_STYLE,B.OIDR_FLG,B.NSS_FLG,A.REPORT_FLG ";
		String sqlDeptCode = "";
		if(!"".equals(deptCode)&&!deptCode.equals(null)){//��������ҵ�����
			sqlDeptCode = " AND C.DEPTORDR_CODE ='"+deptCode+"'  ";
		}
		if(this.checkSeq()){
			
			sql += " FROM EMR_FILE_INDEX A,EMR_TEMPLET B WHERE "
				+ " A.CLASS_CODE='"
				+ classCode
				+ "'  AND A.SUBCLASS_CODE = '"
				+ subClassCode
				+ "' AND B.SEQ = '"+seq+"' AND A.DISPOSAC_FLG<>'Y' AND B.SEQ = '"+onSelectSeqFromTem(subClassCode,deptCode)+"' " +
						"AND A.SUBCLASS_CODE = B.SUBCLASS_CODE AND A.CLASS_CODE = B.CLASS_CODE AND B.SEQ = '4' "
//				+ sqlDeptCode
				+ myTemp + " ORDER BY A.OPT_DATE DESC,A.CLASS_CODE,A.SUBCLASS_CODE,A.FILE_SEQ";
		}else{
			sql += " FROM EMR_FILE_INDEX A,EMR_TEMPLET B WHERE "
				+ " A.CLASS_CODE='"
				+ classCode
				+ "'  AND A.SUBCLASS_CODE = '"
				+ subClassCode
				+ "' AND B.SEQ = '"+seq+"' AND A.DISPOSAC_FLG<>'Y' AND B.SEQ = '"+onSelectSeqFromTem(subClassCode,deptCode)+"' " +
						"AND A.SUBCLASS_CODE = B.SUBCLASS_CODE AND A.CLASS_CODE = B.CLASS_CODE  "
//				+ sqlDeptCode
				+ myTemp + " ORDER BY A.OPT_DATE DESC,A.CLASS_CODE,A.SUBCLASS_CODE,A.FILE_SEQ";
		}
//		System.out.println("Ҷ�ڵ�������sql is����"+sql);
		result = new TParm(this.getDBTool().select(sql));

		return result;
	}
	/**
	 * ��ѯ����ģ���SEQ,���ֲ��ƺͶ��Ʋ���
	 * yanjing 20150109
	 */
	private String onSelectSeqFromTem(String subClassCode,String deptCode){
		String sql = "SELECT SEQ FROM OPD_COMTEMPLET WHERE DEPT_OR_DR IN ('3','4') " +
				"AND SUBCLASS_CODE = '"+subClassCode+"' AND DEPTORDR_CODE = '"+deptCode+"'";
		TParm result = new TParm(this.getDBTool().select(sql));
		String seq = result.getValue("SEQ",0);
		return seq;
	}
	/**
	 * �������ݿ��������
	 * 
	 * @return TJDODBTool
	 */
	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}
	/**
	 * ע���¼�
	 */
	public void initEven() {
		// ����ѡ������Ŀ
		addEventListener(TREE_NAME + "->" + TTreeEvent.DOUBLE_CLICKED,
				"onTreeDoubled");
	}
	
	/**
	 * �����
	 * 
	 * @param parm
	 *            Object
	 */
	public void onTreeDoubled(Object parm) {	
		//��ʼ����׷�ӱ��
		TTreeNode node = (TTreeNode) parm;
		TParm emrParm = (TParm) node.getData();
		if (emrParm != null && emrParm.getValue("REPORT_FLG").equals("Y")) {
			// �򿪲���
			if (!this.getWord().onOpen(emrParm.getValue("FILE_PATH"),
					emrParm.getValue("FILE_NAME"), 3, true)) {
				return;
			}
			FILE_SEQ=emrParm.getValue("FILE_SEQ");
			// ���ò��ɱ༭
			this.getWord().setCanEdit(true);
			TParm allParm = new TParm();
//			allParm.setData("FILE_TITLE_TEXT", "TEXT", this.hospAreaName);
//			allParm.setData("FILE_TITLEENG_TEXT", "TEXT", this.hospEngAreaName);
			allParm.setData("FILE_HEAD_TITLE_MR_NO", "TEXT", this.getMrNo());
//			allParm.setData("FILE_HEAD_TITLE_IPD_NO", "TEXT", this.getIpdNo());
//			allParm.setData("FILE_128CODE", "TEXT", this.getMrNo());
			allParm.addListener("onDoubleClicked", this, "onDoubleClicked");
			allParm.addListener("onMouseRightPressed", this,
					"onMouseRightPressed");
			this.getWord().setWordParameter(allParm);

		} else {
			if (!this.checkInputObject(parm)) {
				return;
			}
//			// 1.�����鿴Ȩ�޿��ƣ�
//			if (!checkDrForOpen()) {
//				this.messageBox("E0011"); // Ȩ�޲���
//				return;
//			}

			if (!node.getType().equals("4")) {
				return;
			}
			// �򿪲���
			if (!this.getWord().onOpen(emrParm.getValue("FILE_PATH"),
					emrParm.getValue("FILE_NAME"), 3, true)) {
				return;
			}
			FILE_SEQ=emrParm.getValue("FILE_SEQ");
			TParm allParm = new TParm();
//			allParm.setData("FILE_TITLE_TEXT", "TEXT", this.hospAreaName);
//			allParm.setData("FILE_TITLEENG_TEXT", "TEXT", this.hospEngAreaName);
			allParm.setData("FILE_HEAD_TITLE_MR_NO", "TEXT", this.getMrNo());
//			allParm.setData("FILE_HEAD_TITLE_IPD_NO", "TEXT", "00000000000000");
			allParm.setData("FILE_128CODE", "TEXT", this.getMrNo());
			allParm.addListener("onDoubleClicked", this, "onDoubleClicked");
			allParm.addListener("onMouseRightPressed", this,
					"onMouseRightPressed");
			this.getWord().setWordParameter(allParm);
			this.getWord().setCanEdit(false);

		}
	}
	
	/**
	 * ���ú�
	 */
	private void setMicroField() {
		TParm allParm = new TParm();
//		allParm.setData("FILE_HEAD_TITLE_IPD_NO", "TEXT", this.getIpdNo());
		allParm.setData("FILE_HEAD_TITLE_IPD_NO", "TEXT", "000000077777");
		// allParm.setData("FILE_TITLE_TEXT", "TEXT", this.hospAreaName);
		// allParm.setData("FILE_TITLE_EN_TEXT", "TEXT", this.hospEngAreaName);
		allParm.setData("FILE_HEAD_TITLE_MR_NO", "TEXT", this.getMrNo());
		// allParm.setData("FILE_HEAD_TITLE_IPD_NO", "TEXT", this.getIpdNo());
		allParm.setData("FILE_128CODE", "TEXT", this.getMrNo());
		allParm.addListener("onDoubleClicked", this, "onDoubleClicked");
		allParm.addListener("onMouseRightPressed", this, "onMouseRightPressed");
		// allParm.addListener("onMouseLeftPressed", this,
		// "onMouseLeftPressed");
		// CLICKED

		this.getWord().setWordParameter(allParm);
		setMicroFieldOne();
	}
	private void setMicroFieldOne(){

		Map map = this.getDBTool().select(
				"SELECT * FROM MACRO_PATINFO_VIEW WHERE 1=1 AND MR_NO='"
						+ this.getMrNo() + "'");
		TParm parm = new TParm(map);
		if (parm.getErrCode() < 0) {
			// ȡ�ò��˻�������ʧ��
			this.messageBox("E0110");
			return;
		}

		Timestamp tempBirth = parm.getValue("��������", 0).length() == 0 ? SystemTool
				.getInstance().getDate()
				: StringTool.getTimestamp(parm.getValue("��������", 0),
						"yyyy-MM-dd");
		// ��������
		String age = "0";
//		if (this.getAdmDate() != null) {
//			age = OdiUtil.getInstance().showAge(tempBirth, this.getAdmDate());
//		} else {
//			age = "";
//		}

		if (parm.getCount() > 0) {
			for (String parmName : parm.getNames()) {
				parm.addData(parmName, parm.getValue(parmName, 0));
			}

		} else {
			for (String parmName : parm.getNames()) {
				parm.addData(parmName, "");
			}

		}
		String dateStr = StringTool.getString(SystemTool.getInstance()
				.getDate(), "yyyy/MM/dd HH:mm:ss");
		parm.addData("����", age);
		parm.addData("�����", this.getCaseNo());
		parm.addData("������", this.getMrNo());
//		parm.addData("סԺ��", this.getIpdNo());
//		parm.addData("����", this.getDeptDesc(Operator.getDept()));
		parm.addData("������", Operator.getName());
		parm.addData("��������", dateStr);
		parm.addData("����", StringTool.getString(SystemTool.getInstance()
				.getDate(), "yyyy/MM/dd"));
		parm.addData("ʱ��", StringTool.getString(SystemTool.getInstance()
				.getDate(), "HH:mm:ss"));
		// parm.addData("EMR_DATE",StringTool.getString(SystemTool.getInstance().getDate(),"yyyy/MM/dd HH:mm:ssS"));
		parm.addData("����ʱ��", dateStr);
//		parm.addData("��Ժʱ��", StringTool.getString(this.getAdmDate(),
//				"yyyy/MM/dd HH:mm:ss"));

		parm.addData("��Ժʱ��", StringTool.getString(new java.sql.Timestamp(System
				.currentTimeMillis()), "yyyy/MM/dd"));

//		parm.addData("���ÿ���", this.getDeptDesc(this.getDeptCode()));
		parm.addData("SYSTEM", "COLUMNS", "����");
		parm.addData("SYSTEM", "COLUMNS", "�����");
		parm.addData("SYSTEM", "COLUMNS", "������");
		parm.addData("SYSTEM", "COLUMNS", "סԺ��");
		parm.addData("SYSTEM", "COLUMNS", "����");
		parm.addData("SYSTEM", "COLUMNS", "������");
		parm.addData("SYSTEM", "COLUMNS", "��������");
		parm.addData("SYSTEM", "COLUMNS", "����");
		parm.addData("SYSTEM", "COLUMNS", "ʱ��");
		// parm.addData("SYSTEM","COLUMNS","EMR_DATE");
		parm.addData("SYSTEM", "COLUMNS", "����ʱ��");
		parm.addData("SYSTEM", "COLUMNS", "��Ժʱ��");
		parm.addData("SYSTEM", "COLUMNS", "���ÿ���");

		// ��ѯסԺ������Ϣ(���ţ�סԺ���)
		TParm odiParm = new TParm(this.getDBTool().select(
				"SELECT * FROM MACRO_ADMINP_VIEW WHERE CASE_NO='"
						+ this.getCaseNo() + "'"));

		if (odiParm.getCount() > 0) {
			for (String parmName : odiParm.getNames()) {
				parm.addData(parmName, odiParm.getValue(parmName, 0));
			}

		} else {
			for (String parmName : odiParm.getNames()) {
				parm.addData(parmName, "");
			}

		}
		//
		String names[] = parm.getNames();
		TParm obj = (TParm) this.getWord().getFileManager().getParameter();
		String strSex = "";
		for (String temp : names) {
			if ("�Ա�".equals(temp)) {
				if (parm.getInt(temp, 0) == 0) {
					strSex = "δ˵��";
				} else if (parm.getInt(temp, 0) == 1) {
					strSex = "��";
					// 1.�� 2.Ů
					// this.getWord().setSexControl(parm.getInt(temp, 0));
				} else if (parm.getInt(temp, 0) == 2) {
					strSex = "Ů";

				} else if (parm.getInt(temp, 0) == 9) {
					strSex = "δ˵��";
				}

				this.getWord().setMicroField(temp, strSex);
			} else {
				obj.setData(temp, "TEXT", parm.getValue(temp, 0));
				// System.out.println("===temp==="+temp);

				this.getWord().setMicroField(temp, parm.getValue(temp, 0));
				this.getWord().setWordParameter(obj);

			}

		}

	}
	
	/**
	 * ����ǿ���֤
	 * 
	 * @param str
	 *            String
	 * @return boolean
	 */
	public boolean checkInputObject(Object obj) {
		if (obj == null) {
			return false;
		}
		String str = String.valueOf(obj);
		if (str == null) {
			return false;
		} else if ("".equals(str.trim())) {
			return false;
		} else {
			return true;
		}
	}
	/**
	 * ��ѯ����
	 */
	public void onQuery(){
		this.onInitSelectTree();
		
	}
	
	/**
	 * �õ��ϸ���
	 * 
	 * @param dateStr
	 *            String
	 * @return Timestamp
	 */
	public Timestamp queryFirstDayOfLastMonth(String dateStr) {
		DateFormat defaultFormatter = new SimpleDateFormat("yyyyMMdd");
		Date d = null;
//		System.out.println("9999999"+d);
		try {
			d = defaultFormatter.parse(dateStr);
//			System.out.println("9999999"+d);
		} catch (Exception e) {
			e.printStackTrace();
		}
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(d);
		cal.add(Calendar.MONTH, -1);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		return StringTool.getTimestamp(cal.getTime());
	}
	/**
	 * ��ʼ��ʱ������
	 * 
	 * @param date
	 *            Timestamp
	 * @return Timestamp
	 */
	public Timestamp getDateForInit(Timestamp date) {
		String dateStr = StringTool.getString(date, "yyyyMMdd");
		TParm sysParm = BILSysParmTool.getInstance().getDayCycle("I");
		int monthM = sysParm.getInt("MONTH_CYCLE", 0) + 1;
		String monThCycle = "" + monthM;
		dateStr = dateStr.substring(0, 6) + monThCycle;
		Timestamp result = StringTool.getTimestamp(dateStr, "yyyyMMdd");
		return result;
	}
	
	/**
	 * 
	 */
	public void onMouseRightPressed() {
//		this.messageBox("======+++====");
		ECheckBoxChoose agree = (ECheckBoxChoose) word.findObject("agree",
				EComponent.CHECK_BOX_CHOOSE_TYPE);
		boolean ss = agree.isChecked();
		if (ss) {
			word.clearCapture("confirm");
			word.pasteString(str);
			// String
			// sql="select * from PHA_ANTI where case_no='"+caseNo+"' and mr_no='"+mrNo+"' and use_flg='N' and approve_flg='N'";
//			String sql = "update PHA_ANTI set approve_flg='Y' where case_no='"
//					+ this.getCaseNo() + "' and mr_no='" + this.getMrNo()
//					+ "' and use_flg='N' and approve_flg='N'";
//			this.getDBTool().update(sql);

		} else {
			word.clearCapture("confirm");
		}

	}

	/**
	 * ����
	 */
	public boolean onSave() {
		//boolean flg = onsaveEmr(false);

		this.getWord().setMessageBoxSwitch(false);
		
		// this.setAdmMouth(mouthStr);
		// ����(��һ�α���)
		// if(this.word.getFileAuthor()==null||this.word.getFileAuthor().length()==0||"admin".equals(this.word.getFileAuthor()))
		this.getWord().setFileAuthor(Operator.getID());
		// ��˾
		// TParm asSaveParm = this.getEmrChildParm();
		// System.out.println("asSaveParm::::"+asSaveParm);
		String dateStr = StringTool.getString(SystemTool.getInstance()
				.getDate(), "yyyy/MM/dd HH:mm:ss");
		// this.getWord().setFileCo("BlueCore");
		// // ����
		// this.getWord().setFileTitle(asSaveParm.getValue("DESIGN_NAME"));
		// // ��ע
		// this.getWord().setFileRemark(
		// asSaveParm.getValue("CLASS_CODE") + "|"
		// + asSaveParm.getValue("FILE_PATH") + "|"
		// + asSaveParm.getValue("FILE_NAME"));
		// ����ʱ��
		this.getWord().setFileCreateDate(dateStr);
		// ����޸���
		this.getWord().setFileLastEditUser(Operator.getID());
		// ����޸�����`
		this.getWord().setFileLastEditDate(dateStr);
		// ����޸�IP
		this.getWord().setFileLastEditIP(Operator.getIP());
		// /*
		// * System.out.println("==save filePath==" +
		// * asSaveParm.getValue("FILE_PATH"));
		// * System.out.println("==save fileName==" +
		// * asSaveParm.getValue("FILE_NAME"));
		// */
		// // ���Ϊ
		// asSaveParm=new TParm();
		// asSaveParm.setData("FILE_PATH",TN_TEMPLET_PATH);
		// asSaveParm.setData("FILE_NAME",TN_EMT_FILENAME);
		String emrName ="";
		String fileName ="";
		TParm asSaveParm=new TParm();
		if (this.getOnlyEditType().equals("NEW")) {
			TParm action = new TParm(this.getDBTool().select(
					"SELECT NVL(MAX(FILE_SEQ)+1,1) AS MAXFILENO"
							+ " FROM EMR_FILE_INDEX" + " WHERE CASE_NO='"
							+ this.getCaseNo() + "'"));
			int index = action.getInt("MAXFILENO", 0);
			emrName = caseNo + "_" + TN_EMT_FILENAME + "_" + index;
			fileName = "JHW" + "\\" + yearStr + "\\" + mouthStr + "\\"
					+ this.getMrNo();
			asSaveParm.setData("FILE_SEQ",index);
		}else{
			emrName = caseNo + "_" + TN_EMT_FILENAME + "_" + FILE_SEQ;
			fileName = "JHW" + "\\" + yearStr + "\\" + mouthStr + "\\"
					+ this.getMrNo();
			asSaveParm.setData("FILE_SEQ",FILE_SEQ);
		}
		boolean success = this.getWord().onSaveAs(fileName, emrName, 3);
		asSaveParm.setData("FILE_PATH",fileName);
		asSaveParm.setData("FILE_NAME",emrName);
		asSaveParm.setData("CASE_NO",caseNo);
		asSaveParm.setData("MR_NO",mrNo);
//		asSaveParm.setData("IPD_NO",ipdNo);
		asSaveParm.setData("CLASS_CODE",getEmrChildParm().getValue("CLASS_CODE"));
		asSaveParm.setData("SUBCLASS_CODE", getEmrChildParm().getData("SUBCLASS_CODE"));
		asSaveParm.setData("DISPOSAC_FLG","N");
		asSaveParm.setData("DESIGN_NAME", getEmrChildParm().getValue("EMT_FILENAME")+ "(" + dateStr + ")");
		
		if (saveEmrFile(asSaveParm)) {
			if (success) {
				this.messageBox("����ɹ�");
				onInitSelectTree();
			}else{
				this.messageBox("����ʧ��");
			}
		}else{
			if (success) {
				this.messageBox("����ʧ��");
			}
		}
		return true;
	}
	/**
	 * ��������(����׷��)
	 */
	public void onAddMenu() {
//		this.messageBox("===1234");
		// ���ֶ�дȨ�޹ܿ�
		openChildDialog();
		this.getWord().fixedTryReset(this.getMrNo(), this.getCaseNo());
	}
	/**
	 * ���Ӵ���
	 */
	public void openChildDialog() {
		// ģ������
		String emrClass = this.getTTree(TREE_NAME).getSelectNode().getGroup();
		String nodeName = this.getTTree(TREE_NAME).getSelectNode().getText();
		String emrType = this.getTTree(TREE_NAME).getSelectNode().getType();
		String programName = this.getTTree(TREE_NAME).getSelectNode()
				.getValue();
		// ����ǵ���HIS��������������
		if (programName.length() != 0) {
			TParm hisParm = new TParm();
			hisParm.setData("CASE_NO", this.getCaseNo());
			this.openDialog(programName, hisParm);
			return;
		}
		String myTemp =
			// ����ģ��
			" AND ((DEPT_CODE IS NULL AND USER_ID IS NULL) "
					// ����ģ��
					+ " OR (DEPT_CODE = '" + Operator.getDept()
					+ "' AND USER_ID IS NULL) "
					// ����ģ��
					+ " OR USER_ID = '" + Operator.getID() + "') ";
		TParm parm = new TParm(
				this.getDBTool().select(
								"SELECT CLASS_CODE,SUBCLASS_CODE,SUBCLASS_DESC,TEMPLET_PATH,SEQ,DEPT_CODE,EMT_FILENAME,RUN_PROGARM,SUBTEMPLET_CODE,CLASS_STYLE" +
								" FROM EMR_TEMPLET" +
								" WHERE CLASS_CODE='"
										+ emrClass
										+ "' AND IPD_FLG='Y' AND (SYSTEM_CODE='ODI' OR SYSTEM_CODE IS NULL) "
										+ myTemp
										+ " ORDER BY SEQ"));

		parm.setData("SYSTEM_CODE", "ODI");
		parm.setData("NODE_NAME", nodeName);
		parm.setData("TYPE", emrType);
		parm.setData("DEPT_CODE", Operator.getDept());
		Object obj = this.openDialog("%ROOT%\\config\\emr\\EMRChildUI.x", parm);
		if (obj == null || !(obj instanceof TParm)) {
			return;
		}
		TParm action = (TParm) obj;
		// Ԥ����������
		//String runProgarmName = action.getValue("RUN_PROGARM");

		TParm runParm = new TParm();
		// �ж������Ƿ���Դ�
		//String styleClass = action.getValue("CLASS_STYLE");
		//System.out.println("styleClass:::sDF:::"+action);
		
		String templetPath = action.getValue("TEMPLET_PATH");
		String templetName = action.getValue("EMT_FILENAME");
		//this.messageBox("===templetName==="+templetName);
        try{
        	openTempletNoEdit(templetPath, templetName, runParm);
        }catch (Exception e) {

		}
		this.setEmrChildParm(action);
		// ���ܳ�����
		//this.setEmrChildParm(this.getFileServerEmrName());
		// ���������ӡ�������޸ı�ʶ�����أ�
		//setCanPrintFlgAndModifyFlg("", "", false);
	}
	
	/**
	 * ����EMR�ļ�
	 * 
	 * @param parm
	 *            TParm
	 */
	public boolean saveEmrFile(TParm parm) {
		boolean falg = true;
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_DATE", this.getDBTool().getDBTime());
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setData("REPORT_FLG", "N");
		parm.setData("CURRENT_USER", Operator.getID());
//		// $$=================������ֵ��ҽ��=======================$$//
//		if (isDutyDrList()) {
//			parm.setData("CURRENT_USER", Operator.getID());
//			parm.setData("CHK_USER1", "");
//			parm.setData("CHK_DATE1", "");
//			parm.setData("CHK_USER2", "");
//			parm.setData("CHK_DATE2", "");
//			parm.setData("CHK_USER3", "");
//			parm.setData("CHK_DATE3", "");
//			parm.setData("COMMIT_USER", "");
//			parm.setData("COMMIT_DATE", "");
//		}
//		// $$=================������ֵ��ҽ��=======================$$//
		if (this.getOnlyEditType().equals("NEW")) {
			parm.setData("CREATOR_USER", Operator.getID());
			TParm result = TIOM_AppServer.executeAction(actionName,
					"saveNewEmrFile", parm);
			if (result.getErrCode() < 0) {
				falg = false;
			}
			return falg;
		}
		// this.messageBox("type"+this.getOnlyEditType());

		if (this.getOnlyEditType().equals("ONLYONE")) {
			parm.setData("CANPRINT_FLG","N");
			parm.setData("MODIFY_FLG","Y");
			parm.setData("CURRENT_USER","");
			parm.setData("CHK_USER1","");
			parm.setData("CHK_DATE1",SystemTool.getInstance().getDate());
			parm.setData("CHK_USER1","");
			parm.setData("CHK_USER2","");
			parm.setData("CHK_DATE2",SystemTool.getInstance().getDate());
			parm.setData("CHK_USER3","");
			parm.setData("CHK_DATE3",SystemTool.getInstance().getDate());
			parm.setData("COMMIT_USER","");
			parm.setData("COMMIT_DATE",SystemTool.getInstance().getDate());
			parm.setData("IN_EXAMINE_USER","");
			parm.setData("IN_EXAMINE_DATE",SystemTool.getInstance().getDate());
			parm.setData("DS_EXAMINE_USER","");
			parm.setData("DS_EXAMINE_DATE",SystemTool.getInstance().getDate());
			parm.setData("PDF_CREATOR_USER","");
			parm.setData("PDF_CREATOR_DATE",SystemTool.getInstance().getDate());
			TParm result = TIOM_AppServer.executeAction(actionName,
					"writeEmrFile", parm);
			if (result.getErrCode() < 0) {
				falg = false;
			}
		}
		return falg;
	}
	
	/**
	 * ��ģ��
	 * 
	 * @param templetPath
	 *            String
	 * @param templetName
	 *            String
	 */
	public void openTempletNoEdit(String templetPath, String templetName,
			TParm parm) {
		// �½�����޸ı�־;
		
		this.getWord().onOpen(templetPath, templetName, 2, false);
		// ���ò��ɱ༭
		//setMicroField();
		TParm allParm = new TParm();
//		allParm.setData("FILE_HEAD_TITLE_IPD_NO", "TEXT", this.getIpdNo());
		allParm.setData("FILE_HEAD_TITLE_MR_NO", "TEXT", this.getMrNo());
		allParm.setData("FILE_128CODE", "TEXT", this.getMrNo());
//		allParm.setData("FILE_TITLE_TEXT", "TEXT", this.hospAreaName);
//		allParm.setData("FILE_TITLE_EN_TEXT", "TEXT", this.hospEngAreaName);
		allParm.addListener("onDoubleClicked", this, "onDoubleClicked");
		allParm.addListener("onMouseRightPressed", this, "onMouseRightPressed");
		
		// allParm.addListener("onMouseLeftPressed", this,
		// "onMouseLeftPressed");
		// CLICKED
		
		
		this.getWord().setWordParameter(allParm);
		setMicroFieldOne();
		// ���ú�
		//setMicroField(true);
		String comlunName[] = parm.getNames();
		for (String temp : comlunName) {
			this.getWord().setMicroField(temp, parm.getValue(temp));
			this.setCaptureValueArray(temp, parm.getValue(temp));
		}
		//setSingleDiseBasicData();//add by wanglong 20121115  ���õ����ֻ�����Ϣ
        //setOPEData();//add by wanglong 20130514 ��������������Ϣ
		// ���ñ༭״̬
		this.setOnlyEditType("NEW");
		// ���ò����Ա༭
		//this.getWord().setCanEdit(false);
		//setTMenuItem(false);
		onEdit();
		// ����༭
//		if (ruleType.equals("2") || ruleType.equals("3")) {
//			
//		}
	}
	private void onEdit(){
		// �ɱ༭
		this.getWord().setCanEdit(true);
	}
	/**
	 * ����ץȡ��
	 * 
	 * @param name
	 *            String
	 * @param value
	 *            String
	 */
	public void setCaptureValueArray(String name, String value) {
		//ԭ���������ظ���,������Ҫ��Tword���мӸ����� ͨ������ȡ�ؼ������� ��ֵ�� ͬ���Ḳ����ǰ��ֵ��
		boolean isSetCaptureValue = this.setCaptureValue(name, value);
		if (!isSetCaptureValue) {
			ECapture ecap = this.getWord().findCapture(name);
			if (ecap == null) {
				return;
			}
			ecap.setFocusLast();
			ecap.clear();
			this.getWord().pasteString(value);

		}

	}
	
	/**
	 * ͨ����������ץȡ��ֵ��
	 * 
	 * @param macroName
	 *            String
	 * @param value
	 *            String
	 */
	private boolean setCaptureValue(String macroName, String value) {
		boolean isSetValue = false;
		TList components = this.getWord().getPageManager().getComponentList();
		int size = components.size();
		for (int i = 0; i < size; i++) {
			EPage ePage = (EPage) components.get(i);
			// EComponent
			// this.messageBox("EPanel size"+ePage.getComponentList().size());
			for (int j = 0; j < ePage.getComponentList().size(); j++) {
				EPanel ePanel = (EPanel) ePage.getComponentList().get(j);
				if (ePanel != null) {
					for (int z = 0; z < ePanel.getBlockSize(); z++) {
						IBlock block = (IBlock) ePanel.get(z);
						// this.messageBox("type"+block.getObjectType());
						// this.messageBox("value"+block.getBlockValue());
						// 9Ϊץȡ��;
						if (block != null) {
							if (block.getObjectType() == EComponent.CAPTURE_TYPE) {
								EComponent com = block;
								ECapture capture = (ECapture) com;

								if (capture.getMicroName().equals(macroName)) {
									// this.messageBox("microName"+capture.getMicroName());
									// �ǿ�ʼ����ֵ;
									if (capture.getCaptureType() == 0) {
										capture.setFocusLast();
										capture.clear();
										this.getWord().pasteString(value);
										isSetValue = true;
										break;
									}
								}
							}
						}
						if (isSetValue) {
							break;
						}
					}
					if (isSetValue) {
						break;
					}

				}

			}

		}
		return isSetValue;
	}
}
