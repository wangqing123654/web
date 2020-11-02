package com.javahis.ui.emr;

import java.sql.Timestamp;
import java.util.Date;

import jdo.emr.EMRCreateXMLTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.tui.text.CopyOperator;
import com.dongyang.ui.TWindow;
import com.dongyang.ui.TWord;
import com.dongyang.util.ImageTool;
import com.dongyang.util.StringTool;

/**
 * ���̱༭��
 * 
 * @author lix@bluecore.com.cn
 *
 */
public class EMREditControl  extends TControl {
	/**
	 * ���Կ���
	 */
	private boolean isDebug=false;
	
	/**
	 * ����������
	 */
	private static final String actionName = "action.odi.ODIAction";
	/**
	 * WORD�ؼ�
	 */
	private static final String TWORD = "WORD";
	/**
	 * ������ͼģʽ
	 */
	private static final String NEW_MODE="NEW";
	/**
	 * �༭��ͼģʽ
	 */
	private static final String EDIT_MODE="EDIT";
	
	/**
	 * �ճ�������ʷ��¼ģ��
	 */
	private static final String SUBCLASS_CODE="EMR10001701";
	private static final String CLASS_CODE="EMR100017";
	/**
	 * ���
	 */
	TParm inParm;
	
	/**
	 * WORD����
	 */
	private TWord word;
	TParm emrParm = new TParm();
	
	//�������;
	private String captureName="";
	private String viewMode="";
	private String refFileName="";
	private String caseNo;
	private String mrNo;
	private String ipdNo;
	private String admYear;
	private String admMouth;
	private String patName;
	/**
	 * ���ÿ���
	 */
	private String deptCode;
	
	
	public void onInit() {
		super.onInit();		
		initWord();
		initPage();
	}
	
	
	/**
	 * ��ʼ������
	 */
	public void initPage() {
		//�õ������
		inParm= (TParm)this.getParameter();
		captureName=inParm.getValue("CAPTURE_NAME");
		this.setViewMode(inParm.getValue("MODE"));
		this.setCaseNo(inParm.getValue("CASENO"));
		this.setMrNo(inParm.getValue("MRNO"));
		this.setIpdNo(inParm.getValue("IPDNO"));
		this.setAdmMouth(inParm.getValue("ADM_MOUTH"));
		this.setAdmYear(inParm.getValue("ADM_YEAR"));
		this.setPatName(inParm.getValue("PAT_NAME"));
		this.setDeptCode(inParm.getValue("DEPT_CODE"));
		
		
		refFileName=inParm.getValue("REF_FILE_NAME");
		if(isDebug){
			System.out.println("==captureName=="+captureName);
			System.out.println("==viewMode=="+this.getViewMode());
			System.out.println("==refFileName=="+refFileName);
		}
		//TODO 
		//����      �ֹ���ֻ�ṩ      �༭�޸Ĺ���
		if(this.getViewMode().equals(NEW_MODE)){
			String sql = "SELECT CLASS_CODE,SUBCLASS_CODE,SUBCLASS_DESC,TEMPLET_PATH,SEQ,DEPT_CODE,EMT_FILENAME,RUN_PROGARM,SUBTEMPLET_CODE,CLASS_STYLE,REF_FLG FROM EMR_TEMPLET WHERE CLASS_CODE='"
					+ CLASS_CODE
					+ "' AND SEQ=1"
					+ " AND SUBCLASS_CODE='"
					+ SUBCLASS_CODE + "'";
			TParm currParm = new TParm();
			//System.out.println("=====sql11111111111111====="+sql);
			TParm parm = new TParm(this.getDBTool().select(sql));			
			currParm=parm.getRow(0);

			String templetPath = currParm.getValue("TEMPLET_PATH");
			//this.messageBox("templetPath" + templetPath);
			String templetName = currParm.getValue("EMT_FILENAME");
			try {
				this.getWord().onOpen(templetPath, templetName, 2, false);
			} catch (Exception e) {
				System.out.println("--------�½���ģ���ļ�����------------");
			}
			this.setEmrParm(currParm);
			
		//�򿪱༭	
		}else if(this.getViewMode().equals(EDIT_MODE)){
			if(isDebug){
				System.out.println("==========�������ļ�==============");
			}
			String sql = "SELECT A.CASE_NO,A.FILE_SEQ,A.MR_NO,A.IPD_NO,A.FILE_PATH,A.FILE_NAME,A.DESIGN_NAME,A.CLASS_CODE,A.SUBCLASS_CODE,A.DISPOSAC_FLG,"
				+ " A.CREATOR_USER,A.CREATOR_DATE,A.CURRENT_USER,A.CANPRINT_FLG,A.MODIFY_FLG,"
				+ " A.CHK_USER1,A.CHK_DATE1,A.CHK_USER2,A.CHK_DATE2,A.CHK_USER3,A.CHK_DATE3,"
				+ " A.COMMIT_USER,A.COMMIT_DATE,A.IN_EXAMINE_USER,A.IN_EXAMINE_DATE,A.DS_EXAMINE_USER,A.DS_EXAMINE_DATE,A.PDF_CREATOR_USER,A.PDF_CREATOR_DATE,"
				+ " B.SUBCLASS_DESC,B.DEPT_CODE,B.RUN_PROGARM,B.SUBTEMPLET_CODE,B.CLASS_STYLE,B.OIDR_FLG,B.NSS_FLG,A.REPORT_FLG,A.REF_FLG "
			+ " FROM EMR_FILE_INDEX A,EMR_TEMPLET B WHERE A.FILE_NAME='"
			+ refFileName
			+ "' AND A.DISPOSAC_FLG<>'Y' AND A.SUBCLASS_CODE=B.SUBCLASS_CODE(+) "
			+ " ORDER BY A.CLASS_CODE,A.SUBCLASS_CODE,A.FILE_SEQ";
				TParm currParm = new TParm();
				if(isDebug){
					System.out.println("=====sql2222222222222222222222222222222====="+sql);
				}
				
				TParm parm = new TParm(this.getDBTool().select(sql));			
				currParm=parm.getRow(0);
			
				this.setEmrParm(currParm);
			// �򿪲���
			if (!this.getWord().onOpen(emrParm.getValue("FILE_PATH"),
					emrParm.getValue("FILE_NAME"), 3, true)) {
				return;
			}
			
			
		}

		TParm allParm = new TParm();
		//allParm.setData("FILE_TITLE_TEXT", "TEXT", this.hospAreaName);
		//allParm.setData("FILE_TITLEENG_TEXT", "TEXT", this.hospEngAreaName);
		allParm.setData("FILE_HEAD_TITLE_MR_NO", "TEXT", this.getMrNo());
		allParm.setData("FILE_HEAD_TITLE_IPD_NO", "TEXT", this.getIpdNo());
		allParm.setData("FILE_128CODE", "TEXT", this.getMrNo());
//		allParm.addListener("onDoubleClicked", this, "onDoubleClicked");
//		allParm.addListener("onMouseRightPressed", this,
//				"onMouseRightPressed");
		this.getWord().setWordParameter(allParm);
		// �ɱ༭
		this.getWord().setCanEdit(true);
		// �༭״̬(������)
		this.getWord().onEditWord();
		
	}
	
	/**
	 * ��ʼ��WORD
	 */
	public void initWord() {
		//��ȡ�ؼ�
		word = this.getTWord(TWORD);
		//
		this.setWord(word);
		// ����
		this.getWord().setFontComboTag("ModifyFontCombo");
		// ����
		this.getWord().setFontSizeComboTag("ModifyFontSizeCombo");

		// ���
		this.getWord().setFontBoldButtonTag("FontBMenu");

		// б��
		this.getWord().setFontItalicButtonTag("FontIMenu");

	}


	public TWord getWord() {
		return word;
	}


	public void setWord(TWord word) {
		this.word = word;
	}
	
	public TWord getTWord(String tag) {
		return (TWord) this.getComponent(tag);
	}
	
	
	/**
	 * ����
	 */
	public void onSave() {
		if(isDebug){
			System.out.println("====EMREditControl onSave start=====");
		}
		onSaveEmr(true);
	}
	
	/**
	 * ���没��
	 * @param isShow  �Ƿ���ʾ��ʾ��
	 * @return
	 */
	private void onSaveEmr(boolean isShow) {
		
		TParm asSaveParm=null;
		// ����
		String dateStr = StringTool.getString(SystemTool.getInstance()
				.getDate(), "yyyy��MM��dd�� HHʱmm��ss��");
		if ("NEW".equals(this.getViewMode())) {
			this.setEmrParm(this.getFileServerEmrName());
			// ��浽�û��ļ�
			asSaveParm = this.getEmrParm();
		    System.out.println("==asSaveParm=="+asSaveParm);
			this.getWord().setMessageBoxSwitch(false);
			this.getWord().setFileAuthor(Operator.getID());
			// ��˾
			this.getWord().setFileCo("JAVAHIS");
			// ����
			this.getWord().setFileTitle(asSaveParm.getValue("DESIGN_NAME"));
			// ��ע
			this.getWord().setFileRemark(
					asSaveParm.getValue("CLASS_CODE") + "|"
							+ asSaveParm.getValue("FILE_PATH") + "|"
							+ asSaveParm.getValue("FILE_NAME"));
			// ����ʱ��
			this.getWord().setFileCreateDate(dateStr);
			// ����޸���
			this.getWord().setFileLastEditUser(Operator.getID());
			// ����޸�����
			this.getWord().setFileLastEditDate(dateStr);
			// ����޸�IP
			this.getWord().setFileLastEditIP(Operator.getIP());
			System.out.println("==save filePath=="
					+ asSaveParm.getValue("FILE_PATH"));
			System.out.println("==save fileName=="
					+ asSaveParm.getValue("FILE_NAME"));
			// ���Ϊ
			boolean success = this.getWord().onSaveAs(
					asSaveParm.getValue("FILE_PATH"),
					asSaveParm.getValue("FILE_NAME"), 3);

			EMRCreateXMLTool.getInstance()
					.createXML(asSaveParm.getValue("FILE_PATH"),
							asSaveParm.getValue("FILE_NAME"), "EmrData",
							this.getWord());
			
			if (!success) {
				// �ļ��������쳣
				this.messageBox("E0103");
				this.getWord().setMessageBoxSwitch(true);
				//return false;
				return;
		    
			//�ļ�����ɹ�.
			}
			this.getWord().setMessageBoxSwitch(true);
			
			
			// �������ݿ�����
			if (saveEmrFile(asSaveParm)) {
				if (isShow) {
					// ����ɹ�
					this.messageBox("P0001");
				}
			}else {
				if (isShow) {
					// ����ʧ��
					this.messageBox("E0001");
				}
				//return false;
				return;
			}
		
		}
		
		if ("EDIT".equals(this.getViewMode())) {
			asSaveParm = this.getEmrParm();
			
			//System.out.println("-------asSaveParm-----"+asSaveParm);
			// ������ʾ������
			this.getWord().setMessageBoxSwitch(false);
			// ����޸���
			this.getWord().setFileLastEditUser(Operator.getID());
			// ����޸�����
			this.getWord().setFileLastEditDate(dateStr);
			// ����޸�IP
			this.getWord().setFileLastEditIP(Operator.getIP());
			
			// ����
			boolean success = this.getWord().onSaveAs(
					asSaveParm.getValue("FILE_PATH"),
					asSaveParm.getValue("FILE_NAME"), 3);
			EMRCreateXMLTool.getInstance()
					.createXML(asSaveParm.getValue("FILE_PATH"),
							asSaveParm.getValue("FILE_NAME"), "EmrData",
							this.getWord());
			
			if (!success) {
				// �ļ��������쳣
				this.messageBox("E0103");
				// ������ʾ��Ϊ����ʾ
				this.getWord().setMessageBoxSwitch(true);
				return;
			}
			this.getWord().setMessageBoxSwitch(true);
			// ��������
			if (saveEmrFile(asSaveParm)) {
				if (isShow) {
					// ����ɹ�
					this.messageBox("P0001");
				}
			} else {
				if (isShow) {
					// ����ʧ��
					this.messageBox("E0001");
				}
				return;
			}
			
		}
		inParm.runListener("onReturnEMRContent",asSaveParm.getValue("FILE_NAME"),this.captureName);
		
	    this.closeWindow();
		//return true;		
	}
	
	/**
	 * �������ݿ��������
	 * 
	 * @return TJDODBTool
	 */
	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}


	public TParm getEmrParm() {
		return emrParm;
	}


	public void setEmrParm(TParm emrParm) {
		this.emrParm = emrParm;
	}
	
	
	/**
	 * �õ��ļ�������·��
	 * 
	 * @param rootPath
	 *            String
	 * @param fileServerPath
	 *            String
	 * @return String
	 */
	public TParm getFileServerEmrName() {
		TParm emrParm = new TParm();
		String emrName = "";
		TParm childParm = this.getEmrParm();
		String templetName = childParm.getValue("EMT_FILENAME");
		TParm action = new TParm(
				this
						.getDBTool()
						.select(
								"SELECT NVL(MAX(FILE_SEQ)+1,1) AS MAXFILENO FROM EMR_FILE_INDEX WHERE CASE_NO='"
										+ this.getCaseNo() + "'"));
		int index = action.getInt("MAXFILENO", 0);
		emrName = this.getCaseNo() + "_" + templetName + "_" + index;
		String dateStr = StringTool.getString(SystemTool.getInstance()
				.getDate(), "yyyy/MM/dd HH:mm:ss");
		emrParm.setData("FILE_SEQ", index);
		emrParm.setData("FILE_NAME", emrName);
		emrParm.setData("CLASS_CODE", childParm.getData("CLASS_CODE"));
		emrParm.setData("SUBCLASS_CODE", childParm.getData("SUBCLASS_CODE"));
		emrParm.setData("CASE_NO", this.getCaseNo());
		emrParm.setData("MR_NO", this.getMrNo());
		emrParm.setData("IPD_NO", this.getIpdNo());
		emrParm.setData("FILE_PATH", "JHW" + "\\" + this.getAdmYear() + "\\"
				+ this.getAdmMouth() + "\\" + this.getMrNo());
		emrParm.setData("DESIGN_NAME", templetName + "(" + dateStr + ")");
		emrParm.setData("DISPOSAC_FLG", "N");
		emrParm.setData("TYPEEMR", childParm.getValue("TYPEEMR"));
		emrParm.setData("REF_FLG", childParm.getData("REF_FLG"));
		return emrParm;
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


	public String getAdmYear() {
		return admYear;
	}


	public void setAdmYear(String admYear) {
		this.admYear = admYear;
	}


	public String getAdmMouth() {
		return admMouth;
	}


	public void setAdmMouth(String admMouth) {
		this.admMouth = admMouth;
	}


	public String getIpdNo() {
		return ipdNo;
	}


	public void setIpdNo(String ipdNo) {
		this.ipdNo = ipdNo;
	}


	public String getViewMode() {
		return viewMode;
	}


	public void setViewMode(String viewMode) {
		this.viewMode = viewMode;
	}
	
	/**
	 * ����EMR�ļ������ݿ�
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
		//
		
		if (this.getViewMode().equals("NEW")) {
			parm.setData("CREATOR_USER", Operator.getID());
			TParm result = TIOM_AppServer.executeAction(actionName,
					"saveNewEmrFile", parm);
			if (result.getErrCode() < 0) {
				falg = false;
			}
			return falg;
		}
		

		if (this.getViewMode().equals("EDIT")) {
			parm.setData("CHK_USER1", "");
			parm.setData("CHK_DATE1", "");
			parm.setData("CHK_USER2", "");
			parm.setData("CHK_DATE2", "");
			parm.setData("CHK_USER3", "");
			parm.setData("CHK_DATE3", "");
			parm.setData("COMMIT_USER", "");
			parm.setData("COMMIT_DATE", "");		
			parm.setData("PDF_CREATOR_USER", "");
			parm.setData("PDF_CREATOR_DATE", "");
			parm.setData("IN_EXAMINE_USER", "");
			parm.setData("IN_EXAMINE_DATE", "");
			parm.setData("DS_EXAMINE_USER", "");			
			parm.setData("DS_EXAMINE_DATE", "");

			TParm result = TIOM_AppServer.executeAction(actionName,
					"writeEmrFile", parm);
			if (result.getErrCode() < 0) {
				falg = false;
			}
		}
		return falg;
	}
	
	/**
	 * ɾ���̶��ı�
	 */
	public void onDelFixText() {
		if (this.getWord().getFileOpenName() != null) {
			this.getWord().deleteFixed();
		} else {
			this.messageBox("��ѡ��ģ�棡");
		}
	}
	
	/**
	 * ���ϵͳ������
	 */
	public void onClearMenu() {
		CopyOperator.clearComList();
	}
	
	/**
	 * ������Ϣ�ղع���
	 */
	public void onSavePatInfo() {
		this.getWord().onCopy();
		// ��ϵͳ���а����Ƿ�������ݣ�
		if (CopyOperator.getComList() == null
				|| CopyOperator.getComList().size() == 0) {
			this.messageBox("����ѡ��Ҫ����Ĳ�����Ϣ��");
			return;
		}
		TParm inParm = new TParm();
		inParm.setData("MR_NO", this.getMrNo());
		inParm.setData("PAT_NAME", this.getPatName());
		inParm.setData("OP_TYPE", "SavePatInfo");
		this.openDialog("%ROOT%\\config\\emr\\EMRPatPhrase.x", inParm, true);
		/**
		 * TWindow window = (TWindow)this.openWindow(
		 * "%ROOT%\\config\\emr\\EMRPatPhrase.x", inParm, true);
		 * window.setX(ImageTool.getScreenWidth() - window.getWidth());
		 * window.setY(0); window.setVisible(true);
		 **/

	}


	public String getPatName() {
		return patName;
	}


	public void setPatName(String patName) {
		this.patName = patName;
	}
	
	/**
	 * ���뵱ǰʱ��
	 */
	public void onInsertCurrentTime() {
		// ��ȡʱ��
		Timestamp sysDate = StringTool.getTimestamp(new Date());
		String strSysDate = StringTool
				.getString(sysDate, "yyyy/MM/dd HH:mm:ss");
		// ���㴦����ʱ��;
		// EComponent e = word.getFocusManager().getFocus();
		this.getWord().pasteString(strSysDate);
	}
	
	/**
	 * Ƭ��
	 */
	public void onInsertPY() {
		TParm inParm = new TParm();
		inParm.setData("TYPE", "2");
		inParm.setData("ROLE", "1");
		inParm.setData("DR_CODE", Operator.getID());
		inParm.setData("DEPT_CODE", this.getDeptCode());
		inParm.addListener("onReturnContent", this, "onReturnContent");
		// this.openWindow("%ROOT%\\config\\emr\\EMRComPhraseQuote.x",inParm);
		TWindow window = (TWindow) this.openWindow(
				"%ROOT%\\config\\emr\\EMRComPhraseQuote.x", inParm, true);
		window.setX(ImageTool.getScreenWidth() - window.getWidth());
		window.setY(0);
		window.setVisible(true);
	}


	public String getDeptCode() {
		return deptCode;
	}


	public void setDeptCode(String deptCode) {
		this.deptCode = deptCode;
	}
	
	/**
	 * �ٴ�����
	 */
	public void onInsertLCSJ() {
		TParm inParm = new TParm();
		inParm.setData("CASE_NO", this.getCaseNo());
		inParm.addListener("onReturnContent", this, "onReturnContent");
		// this.openWindow("%ROOT%\\config\\emr\\EMRMEDDataUI.x",inParm);
		TWindow window = (TWindow) this.openWindow(
				"%ROOT%\\config\\emr\\EMRMEDDataUI.x", inParm, true);
		window.setX(ImageTool.getScreenWidth() - window.getWidth());
		window.setY(0);
		window.setVisible(true);
	}
	
	/**
	 * ����ģ��Ƭ��
	 */
	public void onInsertTemplatePY() {
		TParm inParm = new TParm();
		inParm.setData("TYPE", "2");
		// this.messageBox("this.getDeptCode()"+this.getDeptCode());
		inParm.setData("DEPT_CODE", this.getDeptCode());
		inParm.setData("TWORD", this.getWord());
		inParm.addListener("onReturnTemplateContent", this,
				"onReturnTemplateContent");
		TWindow window = (TWindow) this.openWindow(
				"%ROOT%\\config\\emr\\EMRTemplateComPhraseQuote.x", inParm,
				true);
		window.setX(ImageTool.getScreenWidth() - window.getWidth());
		window.setY(0);
		window.setVisible(true);

	}
	
	/**
	 * ���벡����Ϣ;
	 */
	public void onInsertPatInfo() {
		TParm inParm = new TParm();
		inParm.setData("MR_NO", this.getMrNo());
		inParm.setData("PAT_NAME", this.getPatName());
		inParm.setData("OP_TYPE", "InsertPatInfo");
		inParm.setData("TWORD", this.getWord());
		// inParm.addListener("onReturnContent", this, "onReturnContent");

		TWindow window = (TWindow) this.openWindow(
				"%ROOT%\\config\\emr\\EMRPatPhrase.x", inParm, true);
		window.setX(ImageTool.getScreenWidth() - window.getWidth());
		window.setY(0);
		window.setVisible(true);
	}
	
	
	

}
