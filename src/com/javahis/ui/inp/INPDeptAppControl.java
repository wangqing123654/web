package com.javahis.ui.inp;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Map;

import jdo.emr.GetWordValue;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import jdo.util.Manager;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.tui.DMessageIO;
import com.dongyang.tui.text.ECapture;
import com.dongyang.tui.text.ECheckBoxChoose;
import com.dongyang.tui.text.EComponent;
import com.dongyang.tui.text.EFixed;
import com.dongyang.tui.text.EPage;
import com.dongyang.tui.text.EPanel;
import com.dongyang.tui.text.IBlock;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTree;
import com.dongyang.ui.TTreeNode;
import com.dongyang.ui.TWindow;
import com.dongyang.ui.TWord;
import com.dongyang.ui.event.TTreeEvent;
import com.dongyang.util.ImageTool;
import com.dongyang.util.StringTool;
import com.dongyang.util.TList;
import com.javahis.util.OdiUtil;
import com.javahis.util.StringUtil;

/**
 * 
 * ���Ҽ��������
 * 
 * @author lixiang
 * 
 */
public class INPDeptAppControl extends TControl implements DMessageIO {

	/**
	 * 
	 */
	public static final String TN_TEMPLET_PATH = "JHW\\סԺ���̼�¼\\�����¼";
	/**
	 * ҽԺȫ��
	 */
	private String hospAreaName = "";
	/**
	 * Ӣ��ȫ��
	 */
	private String hospEngAreaName = "";
	/**
	 * ����
	 */
	private TTreeNode treeRoot;
	/**
	 * 
	 */
	public static final String TN_EMT_FILENAME = "����ʹ�ü�����ҩ��������뵥";
	private String COM = getEktPort();// ̩�Ļ������Ʋ���
	private String COM1 = getEktPort1();// ̩�Ļ������Ʋ���
	private String COM2 = getEktPort2();// ̩�Ļ������Ʋ���
	/**
	 * ֪ʶ��ǰ׺
	 */
	private static final String KNOWLEDGE_STORE_PREFIX = "EMR9";
	/**
	 * WORD����
	 */
	private static final String TWORD = "WORD";
	/**
	 * ��������
	 */
	private static final String TREE_NAME = "TREE";

	private String caseNo;
	private String mrNo;
	/**
	 * ���û������뵥��ǣ���������Ϊfalse�����ﱨ��Ϊtrue
	 * yanjing��20131101
	 */
	private boolean flg = true;
	/**
	 * ����������
	 */
	private static final String actionName = "action.odi.ODIAction";
	
	private String FILE_SEQ;
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
	 * ��������
	 */
	private Timestamp admDate;

	/**
	 * סԺ��
	 */
	private String ipdNo;

	/**
	 * ����
	 */
	private String deptCode;

	private String str = "";
	/**
	 * ��������� add caoyong 20130929
	 */
	private TParm emrChildParm = new TParm();
	/**
	 * �����ļ�����
	 */
	private String subFileName;
	public String getSubFileName() {
		return subFileName;
	}

	public void setSubFileName(String subFileName) {
		this.subFileName = subFileName;
	}
	/**
	 * ��
	 */
	private String yearStr="";
	/**
	 * ��
	 */
	private String mouthStr="";
	/**
	 * WORD����
	 */
	private TWord word;

	public void onInit() {
		super.onInit();
		this.hospAreaName = Manager.getOrganization().getHospitalCHNFullName(
				Operator.getRegion());
		this.hospEngAreaName = Manager.getOrganization()
				.getHospitalENGFullName(Operator.getRegion());
		// ��ʼ��WORD
		initWord();
		// ��ʼ������
		initPage();
		loadTree();
		initEven();
	}

	public void initWord() {
		word = this.getTWord(TWORD);
		this.setWord(word);
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
	
	public void setFlg(boolean flg) {
		this.flg = flg;
	}

	public boolean getFlg() {
		return this.flg;
	}

	public void setAdmDate(Timestamp admDate) {
		this.admDate = admDate;
	}

	public Timestamp getAdmDate() {
		return admDate;
	}

	public String getIpdNo() {
		return ipdNo;
	}

	public void setIpdNo(String ipdNo) {
		this.ipdNo = ipdNo;
	}

	public String getDeptCode() {
		return deptCode;
	}

	public void setDeptCode(String deptCode) {
		this.deptCode = deptCode;
	}

	public TParm getEmrChildParm() {
		return emrChildParm;
	}

	public void setEmrChildParm(TParm emrChildParm) {
		this.emrChildParm = emrChildParm;
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

	/**
	 * ��ʼ������
	 */
	public void initPage() {
		//
		Object obj = this.getParameter();
		if (obj != null) {
			this.setMrNo(((TParm) obj).getValue("MR_NO"));
			this.setCaseNo(((TParm) obj).getValue("CASE_NO"));
			this.setAdmDate(((TParm) obj).getTimestamp("ADM_DATE"));
			this.setIpdNo(((TParm) obj).getValue("IPD_NO"));
			this.setFlg(((TParm) obj).getBoolean("FLG"));
		} else {
			// test
			this.setMrNo("000000159780");
			this.setCaseNo("130716000433");
		}
		yearStr = caseNo.substring(0, 2);
		// this.setAdmYear(yearStr);
		mouthStr = caseNo.substring(2, 4);
		// 1.ͨ�������ȡ REG_PATADM ���� DISEASE_HISTORY�� MED_HISTORY
		// ��ASSISTANT_EXAMINE��ASSSISTANT_STUFF
		// ���粻��
		// ȡ���ݣ� ����ṹ���ĵ�;
		// ���ò��ɱ༭
		setMicroField();
		this.getWord().setCanEdit(true);
		// this.messageBox("��Ҫ");
		// �༭״̬(������)
		this.getWord().onEditWord();

		// word.clearCapture("estimate");
		// word.pasteString("aaaa1111");
		// �Ƿ���ڼ�¼ �ٴ�ҽ�� �����Ŀ���ҩ��
		// ����
		// word.clearCapture("txt");
		// this.getWord().
		// word.pasteString(str.substring(0,str.lastIndexOf("\r")));
		//TParm parm = getPhaAntiInfo(this.getMrNo(), this.getCaseNo());
		// if (parm != null) {
		// // ���� ͬȡ��Ӧ��ֵ�� ����ץȡ��
		//
		// }
	}
	/**
	 * �Ƿ�ҩƷ�ѿ����ҩƷ��Ϣ
	 * 
	 * @param mrNo
	 * @param caseNo
	 * @return
	 */
	private TParm getPhaAntiInfo(String mrNo, String caseNo) {
		String sql = "select ORDER_DESC,SPECIFICATION,MEDI_QTY,FREQ_CODE from PHA_ANTI where case_no='" + caseNo
				+ "' and approve_flg='Y' GROUP BY ORDER_DESC,SPECIFICATION,MEDI_QTY,FREQ_CODE";
		TParm parm = new TParm(this.getDBTool().select(sql));
		str = "";
		if (parm.getCount() > 0) {
			for (int i = 0; i < parm.getCount(); i++) {
				str += parm.getValue("ORDER_DESC", i) + "("
						+ parm.getValue("SPECIFICATION", i) + ")";
				str += " " + parm.getValue("MEDI_QTY", i);
				str += "��";// parm.getValue("MEDI_QTY", i)
				str += " " + parm.getValue("FREQ_CODE", i);
				str += " ������� ";// +parm.getValue("FREQ_CODE", i);
				str += "\n";

			}
			word.clearCapture("confirm");
			word.pasteString(str.substring(0, str.lastIndexOf("\n")));
		}
		// word.focusInCaptue("estimate")

		return null;
	}
	/**
	 * ���ҩ��ʵ������
	 * @return
	 */
	private boolean getMedAntValue(String caseNo){
		String sql="SELECT A.ORDER_CODE, A.MED_APPLY_NO,C.ORDER_DESC, B.CULTURE_CHN_DESC,B.ANTI_CHN_DESC," +
				   " B.OPT_DATE,TO_CHAR(B.OPT_DATE,'YYYY/MM/DD') AS ANT_DATE "+
		           " FROM ODI_ORDER A, MED_LIS_ANTITEST B, SYS_FEE C "+
		           " WHERE A.MED_APPLY_NO = B.APPLICATION_NO AND A.ORDER_CODE = C.ORDER_CODE "+
		           " AND A.CASE_NO='"+caseNo+"' AND A.HIDE_FLG='N' AND A.ORDER_CODE=ORDERSET_CODE AND B.SENS_LEVEL <>'I' "+
		           " ORDER BY B.OPT_DATE,A.MED_APPLY_NO DESC ";
		TParm parm = new TParm(this.getDBTool().select(sql));
		String str = "";
		String antDate="";
		String medApplyNo="";
		if (parm.getCount() > 0) {
			antDate=parm.getValue("ANT_DATE",0);
			medApplyNo=parm.getValue("MED_APPLY_NO",0);
			str += parm.getValue("ANT_DATE", 0)+"\n"+parm.getValue("ORDER_DESC", 0) + " "
			+ parm.getValue("CULTURE_CHN_DESC", 0) ;
			str += " ����ҽ��:" + parm.getValue("ANTI_CHN_DESC", 0)+";";
			for (int i = 0; i < parm.getCount(); i++) {
				if (antDate.equals(parm.getValue("ANT_DATE",i))
						&&medApplyNo.equals(parm.getValue("MED_APPLY_NO",i))) {
					str += "\t"+ parm.getValue("ANTI_CHN_DESC", i)+";";
				}else{
					str += "\n" +parm.getValue("ANT_DATE", i)+"\n"+parm.getValue("ORDER_DESC", i) + " "
					+ parm.getValue("CULTURE_CHN_DESC", i) ;
					str += " ����ҽ��:" + parm.getValue("ANTI_CHN_DESC", i)+";";
					antDate=parm.getValue("ANT_DATE",i);
					medApplyNo=parm.getValue("MED_APPLY_NO",i);
				}
			}
			word.clearCapture("medAntValue");
			word.pasteString(str);
			return true;
		}
		return false;
	}
	/**
	 * ΢�����ͼ����ݽ��
	 * @param caseNo
	 * @return
	 */
	private boolean getMedCulValue(String caseNo){
		String sql="SELECT A.ORDER_CODE, A.MED_APPLY_NO,C.ORDER_DESC, B.CULTURE_CHN_DESC," +
		   " B.OPT_DATE,TO_CHAR(B.OPT_DATE,'YYYY/MM/DD') AS ANT_DATE "+
        " FROM ODI_ORDER A, MED_LIS_CULRPT B, SYS_FEE C "+
        " WHERE A.MED_APPLY_NO = B.APPLICATION_NO AND A.ORDER_CODE = C.ORDER_CODE "+
        " AND A.CASE_NO='"+caseNo+"' AND A.HIDE_FLG='N' AND A.ORDER_CODE=ORDERSET_CODE "+
        " ORDER BY B.OPT_DATE,A.MED_APPLY_NO DESC ";
		TParm parm = new TParm(this.getDBTool().select(sql));
		String str = "";
		String antDate = "";
		String medApplyNo = "";
		if (parm.getCount() > 0) {
			antDate = parm.getValue("ANT_DATE", 0);
			medApplyNo = parm.getValue("MED_APPLY_NO", 0);
			str += parm.getValue("ANT_DATE", 0) + "\n"
					+ parm.getValue("ORDER_DESC", 0) + " "
					+ parm.getValue("CULTURE_CHN_DESC", 0);
			for (int i = 0; i < parm.getCount(); i++) {
				if (antDate.equals(parm.getValue("ANT_DATE", i))
						&& medApplyNo.equals(parm.getValue("MED_APPLY_NO", i))) {
					str += "\t" + parm.getValue("CULTURE_CHN_DESC", i) + ";";
				} else {
					str += "\n" +parm.getValue("ANT_DATE", i) + "\n"
							+ parm.getValue("ORDER_DESC", i) + " "
							+ parm.getValue("CULTURE_CHN_DESC", i)+ ";";
					antDate = parm.getValue("ANT_DATE", i);
					medApplyNo = parm.getValue("MED_APPLY_NO", i);
				}
			}
			word.clearCapture("medCulValue");
			word.pasteString(str);
			return true;
		}else{
			return false;
		}
	}
	/**
	 * 
	 * @param isNull
	 *            �Ƿ������
	 */
	public void onInsertOrder(boolean isNull) {
		TParm inParm = new TParm();
		inParm.setData("CASE_NO", this.getCaseNo());
		inParm.setData("MR_NO", this.getMrNo());
		// inParm.addListener("onReturnContent", this, "onReturnContent");
		// ������
		Object rtn = this.openDialog("%ROOT%\\config\\inp\\INPOrder.x", inParm,
				true);
		onReturnContent(rtn.toString());
		/*
		 * window.setX(ImageTool.getScreenWidth() - window.getWidth());
		 * window.setY(0); window.setVisible(true);
		 */
	}

	public void onReturnContent(String value) {
		ECapture ecap = this.getWord().findCapture("confirm");
		if (ecap == null) {
			return;
		}
		ecap.setFocusLast();
		ecap.clear();
		this.getWord().pasteString(value);
		/*
		 * if (!this.getWord().pasteString(value)) { // ִ��ʧ��
		 * this.messageBox("E0005"); }
		 */
	}

	/**
	 * ���ú�
	 */
	private void setMicroField() {
		TParm allParm = new TParm();
		allParm.setData("FILE_HEAD_TITLE_IPD_NO", "TEXT", this.getIpdNo());
//		 allParm.setData("FILE_TITLE_TEXT", "TEXT", this.hospAreaName);
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
//	private void setMicroFieldOne(){
		private TParm  setMicroFieldOne(){
			TParm result = new TParm();
		Map map = this.getDBTool().select(
				"SELECT * FROM MACRO_PATINFO_VIEW WHERE 1=1 AND MR_NO='"
						+ this.getMrNo() + "'");
		TParm parm = new TParm(map);
		if (parm.getErrCode() < 0) {
			// ȡ�ò��˻�������ʧ��
			this.messageBox("E0110");
			return parm;
		}

		Timestamp tempBirth = parm.getValue("��������", 0).length() == 0 ? SystemTool
				.getInstance().getDate()
				: StringTool.getTimestamp(parm.getValue("��������", 0),
						"yyyy-MM-dd");
		// ��������
		String age = "0";
		if (this.getAdmDate() != null) {
			age = OdiUtil.getInstance().showAge(tempBirth, this.getAdmDate());
		} else {
			age = "";
		}

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
		parm.addData("סԺ��", this.getIpdNo());
		parm.addData("����", this.getDeptDesc(Operator.getDept()));
		parm.addData("������", Operator.getName());
		parm.addData("��������", dateStr);
		parm.addData("����", StringTool.getString(SystemTool.getInstance()
				.getDate(), "yyyy/MM/dd"));
		parm.addData("ʱ��", StringTool.getString(SystemTool.getInstance()
				.getDate(), "HH:mm:ss"));
		// parm.addData("EMR_DATE",StringTool.getString(SystemTool.getInstance().getDate(),"yyyy/MM/dd HH:mm:ssS"));
		parm.addData("����ʱ��", dateStr);
		parm.addData("��Ժʱ��", StringTool.getString(this.getAdmDate(),
				"yyyy/MM/dd HH:mm:ss"));

		parm.addData("��Ժʱ��", StringTool.getString(new java.sql.Timestamp(System
				.currentTimeMillis()), "yyyy/MM/dd"));

		parm.addData("���ÿ���", this.getDeptDesc(this.getDeptCode()));
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
		//����
		String patName = parm.getValue("����", 0).length() == 0 ? ""
				: parm.getValue("����", 0);
		String birthDate = tempBirth.toString();
		if(birthDate.length()>0){
			birthDate = tempBirth.toString().substring(0, 10);
		}
		result.addData("BIRTH_DATE", birthDate);
		result.addData("NAME", patName);
		result.addData("SEX", strSex);
       return result;
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
	 * �õ�����
	 * 
	 * @param deptCode
	 *            String
	 * @return String
	 */
	public String getDeptDesc(String deptCode) {
		TParm parm = new TParm(this.getDBTool().select(
				"SELECT DEPT_CHN_DESC FROM SYS_DEPT WHERE DEPT_CODE='"
						+ deptCode + "'"));
		return parm.getValue("DEPT_CHN_DESC", 0);
	}

	/**
	 * �ṹ����������˫���¼�
	 * 
	 * @param pageIndex
	 *            int
	 * @param x
	 *            int
	 * @param y
	 *            int
	 */
	public void onDoubleClicked(int pageIndex, int x, int y) {
		/*
		 * System.out.println("----------�����ٴ�ҽ��ҽ����������-------------");
		 * System.out.println("pageIndex"+pageIndex); System.out.println("x"+x);
		 * System.out.println("y"+y);
		 */
		String str = "estimate";
		if (word.focusInCaptue("estimate")) {
			// this.messageBox("�����ٴ�ҽ��ҽ����������");
			str = "estimate";
		} else if (word.focusInCaptue("confirm")) {
			// this.messageBox("������Ⱦ��ҽ��ҽ����������");
			str = "confirm";
		}
		if (StringUtil.isNullString(str)) {
			return;
		}
		if ("confirm".equalsIgnoreCase(str)) {
			if (flg) {
				onInsertOrder(true);
			} else {
				this.messageBox("�������벻�ɿ���ҽ����");
				return;
			}
		}
	}

	/**
	 * 
	 * @param pageIndex
	 * @param x
	 * @param y
	 */
	/*
	 * public void onMouseLeftPressed(){
	 * System.out.println("------onClicked--------");
	 * 
	 * }
	 */

	/**
	 * 
	 */
	public void onMouseRightPressed() {
		ECheckBoxChoose agree = (ECheckBoxChoose) word.findObject("agree",
				EComponent.CHECK_BOX_CHOOSE_TYPE);
		boolean ss = agree.isChecked();
		if (ss) {
			word.clearCapture("confirm");
			word.pasteString(str);
			// String
			// sql="select * from PHA_ANTI where case_no='"+caseNo+"' and mr_no='"+mrNo+"' and use_flg='N' and approve_flg='N'";
			String sql = "update PHA_ANTI set approve_flg='Y' where case_no='"
					+ this.getCaseNo() + "' and mr_no='" + this.getMrNo()
					+ "' and use_flg='N' and approve_flg='N'";
			this.getDBTool().update(sql);

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
		asSaveParm.setData("IPD_NO",ipdNo);
		asSaveParm.setData("CLASS_CODE",getEmrChildParm().getValue("CLASS_CODE"));
		asSaveParm.setData("SUBCLASS_CODE", getEmrChildParm().getData("SUBCLASS_CODE"));
		asSaveParm.setData("DISPOSAC_FLG","N");
		asSaveParm.setData("DESIGN_NAME", getEmrChildParm().getValue("EMT_FILENAME")+ "(" + dateStr + ")");
		
		if (saveEmrFile(asSaveParm)) {
			if (success) {
				this.messageBox("����ɹ�");
				loadTree();
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
	 * TREE����ʼ��
	 */
	public void loadTree() {
		treeRoot = this.getTTree(TREE_NAME).getRoot();
		treeRoot.setText("סԺ");
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
			node.setText(mainParm.getValue("CLASS_DESC"));
			node.setEnText(mainParm.getValue("ENG_DESC"));
			// ���ò˵���ʾ��ʽ
			node.setType("Root");
			node.setGroup(mainParm.getValue("CLASS_CODE"));
			node.setValue(mainParm.getValue("SYS_PROGRAM"));
			node.setData(mainParm);

			// ��������
			treeRoot.add(node);
			TTreeNode childrenNode = new TTreeNode(mainParm
					.getValue("CLASS_DESC"), mainParm.getValue("CLASS_STYLE"));
			childrenNode.setID(mainParm.getValue("CLASS_CODE"));
			childrenNode.setGroup(mainParm.getValue("CLASS_CODE"));
			// // mainParm.getValue("SYS_PROGRAM")?????��ʱû��
			treeRoot.findNodeForID(mainParm.getValue("CLASS_CODE")).add(
					childrenNode);
			//
			// // ��Ҷ�ڵ�ļ������ļ�
			// if (((String) allChildsParm.getValue("LEAF_FLG", j))
			// .equalsIgnoreCase("Y")) {
			// $$=====Add by lx 2012/10/10 start=======$$//
			TParm childParm = new TParm();
			// �ж��Ƿ����ٴ�֪ʶ�⣬
			// ��������,�����Ӧ�Ķ�Ӧ��֪ʶ���ļ�
			if (isKnowLedgeStore(mainParm.getValue("CLASS_CODE"))) {
				childParm = getChildNodeDataByKnw(mainParm
						.getValue("CLASS_CODE"));
				// $$=====Add by lx 2012/10/10 end=======$$//
			} else {
				// ��ѯ���ļ�
				childParm = this.getRootChildNodeData(mainParm
						.getValue("CLASS_CODE"), queryFlg);
			}
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

	/**
	 * �Ƿ���֪ʶ��
	 * 
	 * @param classcode
	 * @return
	 */
	private boolean isKnowLedgeStore(String classcode) {
		if (classcode.startsWith(KNOWLEDGE_STORE_PREFIX)) {
			return true;
		}
		return false;
	}

	public TParm getRootNodeData() {
		String strSQL = "SELECT CLASS_DESC, ENG_DESC, CLASS_STYLE, CLASS_CODE, '' SYS_PROGRAM, '' SEQ,PARENT_CLASS_CODE"
				+ " FROM EMR_CLASS"
				+ " WHERE PARENT_CLASS_CODE = '"+COM2+"' AND CLASS_CODE = '"
				+ COM + "' AND SEQ='"+COM1+"'" + " ORDER BY SEQ,CLASS_CODE";
		
		/*TParm result = new TParm(
				this.getDBTool().select(
								"SELECT B.CLASS_DESC,B.ENG_DESC,B.CLASS_STYLE,B.CLASS_CODE,A.SYS_PROGRAM,A.SEQ,B.PARENT_CLASS_CODE "
										+ "FROM EMR_TREE A,EMR_CLASS B "
										+ " WHERE  B.PARENT_CLASS_CODE = 'EMR10' AND B.CLASS_CODE='"
										+ COM
										+ "' AND SYSTEM_CODE='ODI' AND A.CLASS_CODE=B.PARENT_CLASS_CODE ORDER BY B.SEQ,A.CLASS_CODE"));*/
		
//		System.out.println("--strSQL--"+strSQL);
		TParm result = new TParm(
				this.getDBTool().select(strSQL));
		
	/*	System.out.println("-------111111sql1111���ڵ�---------"+"SELECT B.CLASS_DESC,B.ENG_DESC,B.CLASS_STYLE,B.CLASS_CODE,A.SYS_PROGRAM,A.SEQ,B.PARENT_CLASS_CODE "
				+ "FROM EMR_TREE A,EMR_CLASS B "
				+ " WHERE  B.PARENT_CLASS_CODE = 'EMR10' AND B.CLASS_CODE='"
				+ COM
				+ "' AND SYSTEM_CODE='ODI' AND A.CLASS_CODE=B.PARENT_CLASS_CODE ORDER BY B.SEQ,A.CLASS_CODE");*/
		//EMR1002    //EMR100202
		
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
		return result;
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
	 * �õ����ڵ�����
	 * 
	 * @return TParm
	 */
	public TParm getRootChildNodeData(String classCode, boolean queryFlg) {
		TParm result = new TParm();
		String myTemp =
		// ����ģ��
		" AND ((B.DEPT_CODE IS NULL AND B.USER_ID IS NULL) "
				// ����ģ��
				+ " OR (B.DEPT_CODE = '" + Operator.getDept()
				+ "' AND B.USER_ID IS NULL) "
				// ����ģ��
				+ " OR B.USER_ID = '" + Operator.getID() + "') ";
		String sql = "SELECT A.CASE_NO,A.FILE_SEQ,A.MR_NO,A.IPD_NO,A.FILE_PATH,A.FILE_NAME,A.DESIGN_NAME,A.CLASS_CODE,A.SUBCLASS_CODE,A.DISPOSAC_FLG,"
				+ " A.CREATOR_USER,A.CREATOR_DATE,A.CURRENT_USER,A.CANPRINT_FLG,A.MODIFY_FLG,"
				+ " A.CHK_USER1,A.CHK_DATE1,A.CHK_USER2,A.CHK_DATE2,A.CHK_USER3,A.CHK_DATE3,"
				+ " A.COMMIT_USER,A.COMMIT_DATE,A.IN_EXAMINE_USER,A.IN_EXAMINE_DATE,A.DS_EXAMINE_USER,A.DS_EXAMINE_DATE,A.PDF_CREATOR_USER,A.PDF_CREATOR_DATE,"
				+ " B.SUBCLASS_DESC,B.DEPT_CODE,B.RUN_PROGARM,B.SUBTEMPLET_CODE,B.CLASS_STYLE,B.OIDR_FLG,B.NSS_FLG,A.REPORT_FLG ";
		sql += " FROM EMR_FILE_INDEX A,EMR_TEMPLET B WHERE A.CASE_NO='"
				+ this.getCaseNo()
				+ "' AND A.CLASS_CODE='"
				+ classCode
				+ "' AND A.DISPOSAC_FLG<>'Y' AND IPD_FLG='Y' AND A.SUBCLASS_CODE=B.SUBCLASS_CODE "
				+ myTemp + " ORDER BY A.CLASS_CODE,A.SUBCLASS_CODE,A.FILE_SEQ";
		
//		System.out.println("-------111111sql1111�ӽڵ�---------"+sql);
		result = new TParm(this.getDBTool().select(sql));

		return result;
	}

	/**
	 * �õ�debug���
	 * 
	 * @return
	 */
	public String getEktPort() {
		String com = "";
		com = getProp().getString("", "inp.name");
		if (com == null || com.trim().length() <= 0) {
			// System.out.println("�����ļ�ҽ�ƿ�com��Ǵ���");
		}
		return com;
	}
	/**
	 * �õ�debug���
	 * 
	 * @return
	 */
	public String getEktPort1() {
		String com = "";
		com = getProp().getString("", "inp.nameseq");
		if (com == null || com.trim().length() <= 0) {
			// System.out.println("�����ļ�ҽ�ƿ�com��Ǵ���");
		}
		return com;
	}
	/**
	 * �õ�debug���
	 * 
	 * @return
	 */
	public String getEktPort2() {
		String com = "";
		com = getProp().getString("", "inp.nameparent");
		if (com == null || com.trim().length() <= 0) {
			// System.out.println("�����ļ�ҽ�ƿ�com��Ǵ���");
		}
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
			// System.out.println("TConfig.x �ļ�û���ҵ���");
		}
		return config;
	}
	/**
	 * �����
	 * 
	 * @param parm
	 *            Object
	 */
	public void onTreeDoubled(Object parm) {	
		//��ʼ����׷�ӱ��
		//this.setApplend(false);	
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
			allParm.setData("FILE_TITLE_TEXT", "TEXT", this.hospAreaName);
			allParm.setData("FILE_TITLEENG_TEXT", "TEXT", this.hospEngAreaName);
			allParm.setData("FILE_HEAD_TITLE_MR_NO", "TEXT", this.getMrNo());
			allParm.setData("FILE_HEAD_TITLE_IPD_NO", "TEXT", this.getIpdNo());
			allParm.setData("FILE_128CODE", "TEXT", this.getMrNo());
			allParm.addListener("onDoubleClicked", this, "onDoubleClicked");
			allParm.addListener("onMouseRightPressed", this,
					"onMouseRightPressed");
			this.getWord().setWordParameter(allParm);

		} else {
			//System.out.println("======˫����======"+emrParm);
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
			//=====add by lx 2012/10/10�������ٴ�֪ʶ�� ====start//
			if(this.isKnowLedgeStore(emrParm.getValue("SUBCLASS_CODE"))){
				//��֪ʶ��ģ��
			    if (!this.getWord().onOpen(
							emrParm.getValue("FILE_PATH"),
							emrParm.getValue("FILE_NAME"), 2, false))
				// ���ÿɱ༭
				this.getWord().setCanEdit(true);
			    this.getTMenuItem("save").setEnabled(false);
			    //setTMenuItem(false);
			    this.getWord().onPreviewWord();
				return;
			}
			//=====add by lx 2012/10/10�����ٴ�֪ʶ�� ====End//
			// �򿪲���
			if (!this.getWord().onOpen(emrParm.getValue("FILE_PATH"),
					emrParm.getValue("FILE_NAME"), 3, true)) {
				return;
			}
			FILE_SEQ=emrParm.getValue("FILE_SEQ");
			TParm allParm = new TParm();
			allParm.setData("FILE_TITLE_TEXT", "TEXT", this.hospAreaName);
			allParm.setData("FILE_TITLEENG_TEXT", "TEXT", this.hospEngAreaName);
			allParm.setData("FILE_HEAD_TITLE_MR_NO", "TEXT", this.getMrNo());
			allParm.setData("FILE_HEAD_TITLE_IPD_NO", "TEXT", this.getIpdNo());
			allParm.setData("FILE_128CODE", "TEXT", this.getMrNo());
			allParm.addListener("onDoubleClicked", this, "onDoubleClicked");
			allParm.addListener("onMouseRightPressed", this,
					"onMouseRightPressed");
			this.getWord().setWordParameter(allParm);
			getPhaAntiInfo(mrNo, caseNo);//����ҽ������
			// ���ú�
			//setMicroField(false);
			TParm sexP = new TParm(this.getDBTool().select(
					"SELECT SEX_CODE FROM SYS_PATINFO WHERE MR_NO='"
							+ this.getMrNo() + "'"));

			if (sexP.getInt("SEX_CODE", 0) == 9) {
				this.getWord().setSexControl(0);
			} else {
				this.getWord().setSexControl(sexP.getInt("SEX_CODE", 0));
			}
			// ���ñ༭״̬
			this.setOnlyEditType("ONLYONE");
			// ���õ�ǰ�༭����
			this.setEmrChildParm(emrParm);
			//�ɱ༭
			this.getWord().setCanEdit(true);
			//�༭״̬(������)
			this.getWord().onEditWord();
			//setTMenuItem(true);
			//setTMenuItem(false);
			//$$===========add by lx 2012-06-18 ���벡���Ѿ��ύ�����޸�start===============$$//
//			if(getSubmitFLG()){
//				this.getWord().onPreviewWord();
//				LogOpt(emrParm);
//				setPrintAndMfyFlg(emrParm);
//				//this.messageBox("���ύ�����������޸�");
//			}
			//$$===========add by lx 2012-06-18���벡���Ѿ��ύ�����޸� end===============$$//
			// ����༭
//			else if (ruleType.equals("2") || ruleType.equals("3")) {
//				onEdit();
//				setCanEdit(emrParm);
//				LogOpt(emrParm);
//				setPrintAndMfyFlg(emrParm);
//			} else {
//				this.getWord().onPreviewWord();
//				LogOpt(emrParm);
//				setPrintAndMfyFlg(emrParm);
//			}

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
	 * �õ��˵�
	 * 
	 * @param tag
	 *            String
	 * @return TMenuItem
	 */
	public TMenuItem getTMenuItem(String tag) {
		return (TMenuItem) this.getComponent(tag);
	}
	/**
	 * ��������(����׷��)
	 */
	public void onAddMenu() {
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
//		this.messageBox("===templetName==="+templetName);
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
	 * �õ���Ժʱ��
	 * 
	 * @return TParm
	 */
	public TParm getAdmInpDSData() {
		TParm admParm = new TParm(this.getDBTool().select(
				"SELECT DS_DATE FROM ADM_INP WHERE CASE_NO='"
						+ this.getCaseNo() + "'"));
		if (admParm.getCount() < 0) {
			return null;
		}
		return admParm;
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
		TParm parmNew = setMicroFieldOne();
		allParm.setData("filePatName", "TEXT", parmNew.getValue("NAME", 0));
		allParm.setData("fileSex", "TEXT", parmNew.getValue("SEX", 0));
		allParm.setData("fileBirthday", "TEXT", parmNew.getValue("BIRTH_DATE", 0));
		allParm.setData("FILE_HEAD_TITLE_IPD_NO", "TEXT", this.getIpdNo());
		allParm.setData("FILE_HEAD_TITLE_MR_NO", "TEXT", this.getMrNo());
		allParm.setData("FILE_128CODE", "TEXT", this.getMrNo());
		allParm.setData("FILE_TITLE_TEXT", "TEXT", this.hospAreaName);
		allParm.setData("FILE_TITLE_EN_TEXT", "TEXT", this.hospEngAreaName);
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
	 * 
	 */
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
		TParm childParm = this.getEmrChildParm();
		String templetName = childParm.getValue("EMT_FILENAME");
		//System.out.println("=============getFileServerEmrName templetName================"+templetName);
		
		TParm action = new TParm(
				this.getDBTool().select(
								"SELECT NVL(MAX(FILE_SEQ)+1,1) AS MAXFILENO" +
								" FROM EMR_FILE_INDEX" +
								" WHERE CASE_NO='"
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
		emrParm.setData("FILE_PATH", "JHW" + "\\" + yearStr + "\\"
				+ mouthStr + "\\" + this.getMrNo());
		emrParm.setData("DESIGN_NAME", templetName + "(" + dateStr + ")");
		emrParm.setData("DISPOSAC_FLG", "N");
		emrParm.setData("TYPEEMR", childParm.getValue("TYPEEMR"));
		emrParm.setData("EMT_FILENAME", childParm.getValue("EMT_FILENAME"));
		return emrParm;
	}
	/**
	 * ��ӡ
	 */
	public void onPrintXDDialog() {
		if (this.getWord().getFileOpenName() != null) {
			this.getWord().onPreviewWord();
			this.getWord().print();
		} else {
			// ��ѡ����
			this.messageBox("E0099");
		}
	}
	public void onMedApply(){
		if (caseNo==null || word==null) {
			this.messageBox("��ѡ��Ҫ����Ĳ���");
			return ;
		}
		boolean flg=getMedCulValue(caseNo);
		boolean flgOne=getMedAntValue(caseNo);
		if (!flgOne&&!flg) {
			this.messageBox("û����Ҫ���������");
			return;
		}
		this.messageBox("����ɹ�");
		
	}
}
