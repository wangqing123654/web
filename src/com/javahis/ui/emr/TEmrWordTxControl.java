package com.javahis.ui.emr;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import jdo.emr.EMRCreateXMLTool;
import jdo.emr.GetWordValue;
import jdo.emr.OptLogTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.data.TSocket;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.tui.DMessageIO;
import com.dongyang.tui.text.ECapture;
import com.dongyang.tui.text.EComponent;
import com.dongyang.tui.text.EFixed;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TMovePane;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTree;
import com.dongyang.ui.TTreeNode;
import com.dongyang.ui.TWindow;
import com.dongyang.ui.TWord;
import com.dongyang.ui.event.TTreeEvent;
import com.dongyang.util.ImageTool;
import com.dongyang.util.StringTool;
import com.javahis.util.JavaHisDebug;
import com.javahis.util.OdiUtil;
import com.dongyang.tui.text.div.MV;
import com.dongyang.tui.text.div.DIV;
import com.dongyang.tui.text.div.VText;
import com.dongyang.util.TList;
import com.dongyang.tui.text.EPage;
import com.dongyang.tui.text.EPanel;
import com.dongyang.tui.text.IBlock;
import com.dongyang.tui.text.ECheckBoxChoose;
import jdo.clp.CLPEMRTool;
import com.dongyang.tui.text.MModifyNode;
import com.dongyang.tui.text.EModifyInf;
import com.dongyang.tui.text.EMacroroutine;
import painting.Image_Paint;
import com.dongyang.tui.text.div.VPic;
import java.io.File;
import com.dongyang.util.FileTool;
import java.util.Date;
import java.io.IOException;
import java.awt.Component;
import javax.swing.ImageIcon;
import com.dongyang.tui.text.ETable;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import com.dongyang.ui.TPanel;
import com.dongyang.util.TypeTool;
import javax.swing.Icon;
import com.javahis.util.ClipboardTool;
import com.dongyang.tui.text.CopyOperator;
import java.awt.Image;

/**
 * <p>Title: ���Ӳ�������ģ��(̩��ר��)</p>
 *
 * <p>Description: ���Ӳ�����д�������¼�����ﲡ����</p>
 *
 * <p>Copyright: Copyright (c) 2011</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author Sundx & Lix & Zhangjg
 * @version 1.0
 */
public class TEmrWordTxControl
    extends TControl implements DMessageIO {

    //��Ѫ��DB2���ݿ����
    public static final String DB2_POOL_NAME = "javahisdb2";
    //
    public static final String PARAM_REGION_CODE = "REGION_CODE";
    public static final String PARAM_OP_USERID = "OP_USERID";

    public static final String PARAM_OP_USERIP = "OP_USERIP";

    public static final String PARAM_SYSTEM_TYPE = "SYSTEM_TYPE";
    public static final String PARAM_ADM_TYPE = "ADM_TYPE";
    public static final String PARAM_CASE_NO = "CASE_NO";
    public static final String PARAM_PAT_NAME = "PAT_NAME";
    public static final String PARAM_MR_NO = "MR_NO";
    public static final String PARAM_IPD_NO = "IPD_NO";
    public static final String PARAM_ADM_DATE = "ADM_DATE";
    public static final String PARAM_DEPT_CODE = "DEPT_CODE";
    public static final String PARAM_STATION_CODE = "STATION_CODE";
    public static final String PARAM_RULETYPE = "RULETYPE";
    public static final String PARAM_WRITE_TYPE = "WRITE_TYPE";
    //���ò���;
    public TParm parmObj;

    //
    /**
     * ����ͼ������
     */
    public static final String UNVISIBLE_MV = "UNVISITABLE_ATTR";

    /**
     * �����ֵ���־
     */
    private static final String PATTERN = "P";

    /**
     * �����ֵ���־
     */
    private static final String DICTIONARY = "D";
    /**
     * WORD����
     */
    private static final String TWORD = "WORD";
    /**
     * ��������
     */
    private static final String TREE_NAME = "TREE";
    /**
     * WORD����
     */
    private TWord word;
    /**
     * �ż�ס��
     */
    private String admType;
    /**
     * ϵͳ���
     */
    private String systemType;
    /**
     * ������
     */
    private String mrNo;
    /**
     * סԺ��
     */
    private String ipdNo;
    /**
     * ����
     */
    private String patName;
    /**
     * Ӣ������
     */
    private String patEnName;
    /**
     * �õ���
     */
    private String admYear;
    /**
     * �õ���
     */
    private String admMouth;
    /**
     * �����
     */
    private String caseNo;
    /**
     * ���������
     */
    private TParm emrChildParm = new TParm();
    /**
     * ����
     */
    private TTreeNode treeRoot;
    /**
     * ����ҽʦ
     */
    private String vsDrCode;
    /**
     * ����ҽʦ
     */
    private String attendDrCode;
    /**
     * ������
     */
    private String directorDrCode;
    /**
     * ���ÿ���
     */
    private String deptCode;
    /**
     * ���ò���
     */
    private String stationCode;
    /**
     * ��ǰ�༭״̬
     */
    private String onlyEditType;
    /**
     * ��������
     */
    private Timestamp admDate;
    /**
     * Ȩ�ޣ�1,ֻ�� 2,��д 3,���ֶ�д
     */
    private String ruleType = "1";
    /**
     * ����ʽ
     */
    private String styleType = "2";
    /**
     * ��������
     */
    private String type = "";
    /**
     * ҽԺȫ��
     */
    private String hospAreaName = "";
    /**
     * Ӣ��ȫ��
     */
    private String hospEngAreaName = "";
    /**
     * д����
     */
    private String writeType;
    /**
     * ���Ӧ
     */
    private Map microMap = new HashMap();

    /**
     * ����Ԫ�ؼ���Ӧ��ֵ��
     */
    private Map hideElemMap = new HashMap();

    private String picPath = "C:/hispic/";

    //��ͼ��ʱ�ļ���
    private String picTempPath = "C:/hispic/temp/";

    //Ժ��
    String regionCode = "";
    //��ǰҽ���û�
    String operatorUserID = "";
    String operatorUserIP = "";

    /**
     * �Ƿ����ͼƬ
     */
    private boolean containPic = false;


    /**
     * ����������
     */
    private static final String actionName = "action.odi.ODIAction";
    public void onInit() {
        super.onInit();
        //��ʼ��WORD
        initWord();
        //��ʼ������
        initPage();
        //ע���¼�
        initEven();
        // ��������ҽʦ��ֵ��ҽʦ�����Ӳ���������־/EMR_OPTLOG/��½
        //|| this.isDutyDrList() ��Ѫ����ֵ��ҽ����????(�ʸ��Ʒ�)
        if (isDR() || this.isCheckUserDr()) {
            TParm emrParm = new TParm();
            emrParm.setData("FILE_SEQ", "0");
            emrParm.setData("FILE_NAME", "");
            TParm result = OptLogTool.getInstance().writeOptLog(this.getParmObj(),
                "L",
                emrParm);

        }
        // ����������ҽʦ��ֵ��ҽʦ�����Ӳ���������־/EMR_OPTLOG/����
        else {
            TParm emrParm = new TParm();
            emrParm.setData("FILE_SEQ", "0");
            emrParm.setData("FILE_NAME", "");
            TParm result = OptLogTool.getInstance().writeOptLog(this.getParmObj(),
                "R",
                emrParm);
        }
        // �Զ����涨ʱ����ʼ��(��ʱȥ���Զ����湦��)
        //initTimer();
        initMicroMap();
        //��Ļ����
        //callFunction("UI|showMaxWindow");
    }

    /**
     * ��ʼ����Ԫ��
     */
    private void initMicroMap() {
        TParm microParm = new TParm(getDBTool().select(
            "SELECT MICRO_NAME,DATA_ELEMENT_CODE FROM EMR_MICRO_CONVERT"));
        int count = microParm.getCount();
        for (int i = 0; i < count; i++) {
            microMap.put(microParm.getValue("MICRO_NAME", i),
                         microParm.getValue("DATA_ELEMENT_CODE", i));
        }
    }

    /**
     * ��ȡ��ʾ��Ϣ;
     * @param elementGroup String
     * @param elementCode String
     * @return String
     */
    private String getShowMessage(String elementGroup, String elementCode) {
        TParm nameParm = new TParm(getDBTool().select(
            "SELECT DATA_DESC FROM EMR_CLINICAL_DATAGROUP WHERE GROUP_CODE='" +
            elementGroup + "' AND DATA_CODE='" + elementCode + "'"));
        int count = nameParm.getCount();
        if (count > 0) {
            return nameParm.getValue("DATA_DESC", 0);
        }
        return elementCode;

    }

    /**
     * ��ʼ��WORD
     */
    public void initWord() {
        word = this.getTWord(TWORD);
        this.setWord(word);
        //����
        this.getWord().setFontComboTag("ModifyFontCombo");
        //����
        this.getWord().setFontSizeComboTag("ModifyFontSizeCombo");
        //���
        this.getWord().setFontBoldButtonTag("FontBMenu");
        //б��
        this.getWord().setFontItalicButtonTag("FontIMenu");

    }

    /**
     * �õ�WORD����
     * @param tag String
     * @return TWord
     */
    public TWord getTWord(String tag) {
        return (TWord)this.getComponent(tag);
    }

    /**
     * ע���¼�
     */
    public void initEven() {
        //����ѡ������Ŀ
        addEventListener(TREE_NAME + "->" + TTreeEvent.DOUBLE_CLICKED,
                         "onTreeDoubled");
    }

    /**
     * ��ʼ������
     */
    public void initPage() {
        this.hospAreaName = "";
        this.hospEngAreaName = "";
        //�õ�ϵͳ���
        //ͨ��Mettisϵͳȡ�ò���;
        TPanel p = (TPanel) getComponent();
        Object mettisObj = p.getParameter();
        //mettisObj=null;
        //===========test start===============//
        /**TFrame p = (TFrame) getComponent();
         Object mettisObj = p.getParameter();
         System.out.println("======in parm========" + mettisObj);
          System.out.println("======class========" + mettisObj.getClass());
         mettisObj=null;**/
        //===========test end================//
        //mettis���ò���;
        if (mettisObj != null && mettisObj instanceof Vector) {
            Vector VParm = (Vector) mettisObj;
            TParm obj = new TParm();
            //ת��Tparam
            vectorToTParm(VParm, obj);
            //
            this.setSystemType( ( (TParm) obj).getValue("SYSTEM_TYPE"));
            this.setMrNo( ( (TParm) obj).getValue("MR_NO"));
            this.patEnName = getPatEnName(this.getMrNo());
            this.setIpdNo( ( (TParm) obj).getValue("IPD_NO"));
            this.setPatName( ( (TParm) obj).getValue("PAT_NAME"));
            this.setCaseNo( ( (TParm) obj).getValue("CASE_NO"));

            String yearStr = ( (TParm) obj).getValue("CASE_NO").substring(0, 2);
            this.setAdmYear(yearStr);
            String mouthStr = ( (TParm) obj).getValue("CASE_NO").substring(2, 4);
            this.setAdmMouth(mouthStr);
            this.setAdmType( ( (TParm) obj).getValue("ADM_TYPE"));
            this.setDeptCode( ( (TParm) obj).getValue("DEPT_CODE"));
            this.setStationCode( ( (TParm) obj).getValue("STATION_CODE"));
            this.setAdmDate( ( (TParm) obj).getTimestamp("ADM_DATE"));

            this.ruleType = ( (TParm) obj).getValue("RULETYPE");
            this.styleType = ( (TParm) obj).getValue("STYLETYPE");
            String typeStr = ( (TParm) obj).getValue("TYPE");
            if (typeStr.length() == 0) {
                this.type = this.getAdmType();
            }
            else {
                this.type = typeStr;
            }
            this.writeType = ( (TParm) obj).getValue("WRITE_TYPE");
            this.menuEnable(false);

            //�õ���������(���������뵥)
            TParm emrFileDataParm = (TParm) ( (TParm) obj).getData(
                "EMR_FILE_DATA");

            //���������뵥����
            if (emrFileDataParm != null) {
                //System.out.println("���в���==============================��"+emrFileDataParm);
                if (emrFileDataParm.getBoolean("FLG")) {
                    //�򿪲���
                    if (!this.getWord().onOpen(emrFileDataParm.getValue(
                        "FILE_PATH"),
                                               emrFileDataParm.getValue(
                        "FILE_NAME"),
                                               3, false)) {
                        return;
                    }
                    TParm allParm = new TParm();
                    allParm.setData("FILE_TITLE_TEXT", "TEXT",
                                    this.hospAreaName);
                    allParm.setData("FILE_TITLEENG_TEXT", "TEXT",
                                    this.hospEngAreaName);
                    allParm.setData("FILE_HEAD_TITLE_MR_NO", "TEXT",
                                    this.getMrNo());
                    allParm.setData("FILE_HEAD_TITLE_IPD_NO", "TEXT",
                                    this.getIpdNo());
                    allParm.setData("FILE_128CODE", "TEXT", this.getMrNo());
                    //�������뵥��
                    allParm.setData("FLG", "TEXT",
                                    emrFileDataParm.getValue("URGENT_FLG").
                                    equalsIgnoreCase("Y") ? "��" : "");

                    allParm.addListener("onDoubleClicked", this,
                                        "onDoubleClicked");
                    allParm.addListener("onMouseRightPressed", this,
                                        "onMouseRightPressed");
                    this.getWord().setWordParameter(allParm);
                    //���ò��ɱ༭
                    this.getWord().setCanEdit(false);
                    //���ú�(�оɲ��������º�)
//                    setMicroField();
                    //���ñ༭״̬
                    this.setOnlyEditType("ONLYONE");
                    TParm emrParm = new TParm();
                    emrParm.setData("FILE_SEQ",
                                    emrFileDataParm.getValue("FILE_SEQ"));
                    emrParm.setData("FILE_NAME",
                                    emrFileDataParm.getValue("FILE_NAME"));
                    emrParm.setData("CLASS_CODE",
                                    emrFileDataParm.getValue("CLASS_CODE"));
                    emrParm.setData("SUBCLASS_CODE",
                                    emrFileDataParm.getValue("SUBCLASS_CODE"));
                    emrParm.setData("CASE_NO", this.getCaseNo());
                    emrParm.setData("MR_NO", this.getMrNo());
                    emrParm.setData("IPD_NO", this.getIpdNo());
                    emrParm.setData("FILE_PATH",
                                    emrFileDataParm.getValue("FILE_PATH"));
                    emrParm.setData("DESIGN_NAME",
                                    emrFileDataParm.getValue("DESIGN_NAME"));
                    emrParm.setData("DISPOSAC_FLG",
                                    emrFileDataParm.getValue("DISPOSAC_FLG"));

                    //���õ�ǰ�༭����
                    this.setEmrChildParm(emrParm);
                    menuEnable(false);

                    //����༭
                    if ( (ruleType.equals("2") || ruleType.equals("3")) &&
                        isCheckUserDr() || isDR()) {
                        onEdit();
                    }
                }
                else {
                    //�򿪲���
                    if (!this.getWord().onOpen(emrFileDataParm.getValue(
                        "TEMPLET_PATH"),
                                               emrFileDataParm.getValue(
                        "EMT_FILENAME"), 2, false)) {
                        return;
                    }

                    TParm allParm = new TParm();
                    allParm.setData("FILE_TITLE_TEXT", "TEXT",
                                    this.hospAreaName);
                    allParm.setData("FILE_TITLEENG_TEXT", "TEXT",
                                    this.hospEngAreaName);
                    allParm.setData("FILE_HEAD_TITLE_MR_NO", "TEXT",
                                    this.getMrNo());
                    allParm.setData("FILE_HEAD_TITLE_IPD_NO", "TEXT",
                                    this.getIpdNo());
                    allParm.setData("FILE_128CODE", "TEXT", this.getMrNo());
                    //�������뵥��
                    allParm.setData("FLG", "TEXT",
                                    emrFileDataParm.getValue("URGENT_FLG").
                                    equalsIgnoreCase("Y") ? "��" : "");

                    allParm.addListener("onDoubleClicked", this,
                                        "onDoubleClicked");
                    allParm.addListener("onMouseRightPressed", this,
                                        "onMouseRightPressed");

                    this.getWord().setWordParameter(allParm);
                    //���ò��ɱ༭
                    this.getWord().setCanEdit(false);
                    //���ú�
                    setMicroField(true);
                    //���ñ༭״̬
                    this.setOnlyEditType("NEW");
                    //���ýڵ�ֵ
                    this.setEmrChildParm(emrFileDataParm);
                    //TParm emrNameP = this.getFileServerEmrName();
                    this.setEmrChildParm(this.getFileServerEmrName());
                    menuEnable(false);
                    //����༭   || this.isDutyDrList()
                    if ( (ruleType.equals("2") || ruleType.equals("3")) &&
                        "O".equals(this.getAdmType()) ||
                        (ruleType.equals("2") || ruleType.equals("3")) &&
                        (isCheckUserDr()) || isDR()) {
                        onEdit();
                    }
                    //ˢ��ץ�ı�
                    this.getWord().fixedTryReset(this.getMrNo(), this.getCaseNo());
                }

            }
            else {
                this.getWord().setCanEdit(false);
            }
            //����������
            TParm ioParm = new TParm();
            //�����ļ��Ƿ������ļ�
            if (emrFileDataParm == null) {
                ioParm.setData("FLG", false);
            }
            else {
                ioParm.setData("FLG", emrFileDataParm.getBoolean("FLG"));
            }

            //���ú�
            ioParm.addListener("setMicroData", this, "setMicroData");
            //����ץȡ
            ioParm.addListener("getCaptureValue", this, "getCaptureValue");
            //����ץȡ��
            ioParm.addListener("getCaptureValueArray", this,
                               "getCaptureValueArray");
            //����ץȡ��ֵ
            ioParm.addListener("setCaptureValue", this, "setCaptureValueArray");
            ( (TParm) obj).runListener("EMR_LISTENER", ioParm);
            if ("1".equals(this.styleType)) {
                onCloseChickedCY();
            }
            if (emrFileDataParm != null) {
                if (!emrFileDataParm.getBoolean("FLG")) {
                    this.setOnlyEditType("NEW");
                }
            }
            else {
                this.setOnlyEditType("NEW");
            }
        }
        else {
            //this.messageBox("��������,ģ�⹹�����");
            //System.out.println("=========come in test.===================");
            //��������,ģ�⹹�����;
            Vector VParm = new Vector();
            VParm.add("HIS"); //0   PARAM_REGION_CODE
            VParm.add("000211"); //1 PARAM_OP_USERID
            VParm.add("31300"); //2  PARAM_DEPT_CODE
            VParm.add("050205000003"); //3 PARAM_CASE_NO
            VParm.add("ODI"); //4 SYSTEM_TYPE
            VParm.add(""); //5   PARAM_WRITE_TYPE
            VParm.add("2"); //6   PARAM_RULETYPE
            VParm.add("I"); //7   PARAM_ADM_TYPE
            VParm.add(""); //8   PARAM_STATION_CODE
            VParm.add("127.0.0.1"); //9  PARAM_OP_USERIP

            TParm obj = new TParm();
            //ת��Tparam
            vectorToTParm(VParm, obj);

            this.setSystemType( ( (TParm) obj).getValue("SYSTEM_TYPE"));
            this.setMrNo( ( (TParm) obj).getValue("MR_NO"));
            this.patEnName = getPatEnName(this.getMrNo());
            this.setIpdNo( ( (TParm) obj).getValue("IPD_NO"));
            this.setPatName( ( (TParm) obj).getValue("PAT_NAME"));
            this.setCaseNo( ( (TParm) obj).getValue("CASE_NO"));

            String yearStr = ( (TParm) obj).getValue("CASE_NO").substring(0, 2);
            this.setAdmYear(yearStr);
            String mouthStr = ( (TParm) obj).getValue("CASE_NO").substring(2, 4);
            this.setAdmMouth(mouthStr);
            this.setAdmType( ( (TParm) obj).getValue("ADM_TYPE"));
            this.setDeptCode( ( (TParm) obj).getValue("DEPT_CODE"));
            this.setStationCode( ( (TParm) obj).getValue("STATION_CODE"));
            this.setAdmDate( ( (TParm) obj).getTimestamp("ADM_DATE"));
            this.ruleType = ( (TParm) obj).getValue("RULETYPE");
            this.styleType = ( (TParm) obj).getValue("STYLETYPE");
            String typeStr = ( (TParm) obj).getValue("TYPE");
            if (typeStr.length() == 0) {
                this.type = this.getAdmType();
            }
            else {
                this.type = typeStr;
            }
            this.writeType = ( (TParm) obj).getValue("WRITE_TYPE");
            menuEnable(false);
        }
        //������
        loadTree();

    }


    /**
     * �õ�����Ӣ����
     * @param mrNo String
     * @return String
     */
    public String getPatEnName(String mrNo) {
        String patEnName = "";
        TParm parm = new TParm(this.getDBTool().select(DB2_POOL_NAME,
            "SELECT PAT_NAME2 FROM SYS_PATINFO WHERE MR_NO='" + mrNo + "'"));
        if (parm.getCount() > 0) {
            patEnName = parm.getValue("PAT_NAME2", 0);
        }
        return patEnName;
    }

    /**
     * ����ѡ��ر��¼�
     */
    public void onCloseChickedCY() {

        TMovePane mp = (TMovePane) callFunction("UI|MOVEPANE|getThis");
        mp.onDoubleClicked(true);
        ( (TMovePane)this.getComponent("MOVEPANE")).setEnabled(false);
    }

    /**
     * �õ�ץȡֵ
     * @param name String
     * @return String
     */
    public String getCaptureValue(String name) {
        return this.getWord().getCaptureValue(name);
    }

    /**
     * �����
     * @param names String[]
     * @return TParm
     */
    public Map getCaptureValueArray(List name) {
        Map result = new HashMap();
        int rowCount = name.size();
        for (int i = 0; i < rowCount; i++) {
            result.put(name.get(i), this.getCaptureValue("" + name.get(i)));
        }
        return result;
    }

    /**
     * ����ץȡ��
     * @param name String
     * @param value String
     */
    public void setCaptureValueArray(String name, String value) {
        //��������ԭ���������ظ���,������Ҫ��Tword���мӸ����� ͨ������ȡ�ؼ�������  ��ֵ��  ͬ���Ḳ����ǰ��ֵ��
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
     * ���ú�����
     * @param name String
     * @param value String
     */
    public void setMicroData(String name, String value) {
        this.getWord().setMicroField(name, value);
    }

    /**
     * �õ��˵�
     * @param tag String
     * @return TMenuItem
     */
    public TMenuItem getTMenuItem(String tag) {
        return (TMenuItem)this.getComponent(tag);
    }

    /**
     * ��������ҽ���༭Ȩ��()
     */
    private void setEditLevel() {
        String dateStr = StringTool.getString(SystemTool.getInstance().getDate(),
                                              "yyyy��MM��dd�� HHʱmm��ss��");
        int stuts = this.getWord().getNodeIndex();
        int stutsOnly = this.getWord().getNodeIndex();
        if ("ODI".equals(this.getSystemType())) {
            //����ҽʦOperator.getID()
            if (this.getVsDrCode().equals(this.getOperatorUserID()) &&
                this.getWord().getNodeIndex() == -1) {
                stuts = 0;
            }
            //����ҽʦ
            if (this.getAttendDrCode().equals(this.getOperatorUserID()) &&
                this.getWord().getNodeIndex() == 0) {
                stuts = 1;
            }
            //����ҽʦ
            if (this.getDirectorDrCode().equals(this.getOperatorUserID()) &&
                this.getWord().getNodeIndex() == 1) {
                stuts = 2;
            }
            //���Ǿ���ҽʦ��������ҽʦ
            if (this.getVsDrCode().equals(this.getOperatorUserID()) &&
                this.getAttendDrCode().equals(this.getOperatorUserID())) {
                stuts = 1;
            }
            //���Ǿ���ҽʦ��������ҽʦ��������ҽʦ
            if (this.getVsDrCode().equals(this.getOperatorUserID()) &&
                this.getAttendDrCode().equals(this.getOperatorUserID()) &&
                this.getDirectorDrCode().equals(this.getOperatorUserID())) {
                stuts = 2;
            }
        }
        //�޸ļ�¼
        MModifyNode modifyNode = this.getWord().getPM().getModifyNodeManager();
        int count = modifyNode.size();
        if (count > 0) {
            //�����޸�ʱ��
            modifyNode.get(stutsOnly).setModifyDate(dateStr);
        }
        else {
            //����ҽʦ
            EModifyInf infDr = new EModifyInf();
            //�޸���
            infDr.setUserName(getOperatorName(this.getVsDrCode()));
            //�޸�ʱ��
            infDr.setModifyDate(dateStr);
            //�����޸ģ������Է���ADD��
            modifyNode.add(infDr);
            //����ҽʦ
            EModifyInf infAtten = new EModifyInf();
            //�޸���
            infAtten.setUserName(getOperatorName(this.getAttendDrCode()));
            //�޸�ʱ��
            infAtten.setModifyDate(dateStr);
            //�����޸ģ������Է���ADD��
            modifyNode.add(infAtten);
            //����ҽʦ
            EModifyInf infDir = new EModifyInf();
            //�޸���
            infDir.setUserName(getOperatorName(this.getDirectorDrCode()));
            //�޸�ʱ��
            infDir.setModifyDate(dateStr);
            //�����޸ģ������Է���ADD��
            modifyNode.add(infDir);
        }
        if (stuts != this.getWord().getNodeIndex()) {
            this.getWord().setNodeIndex(stuts);
        }
        saveWord();
    }

    /**
     * ��������ҽ���༭Ȩ��
     */
    private void setEditLevelCancel() {
        String creatFileUser = this.getWord().getFileLastEditUser();
        //this.messageBox("creatFileUser" + creatFileUser);
        int stuts = this.getWord().getNodeIndex();
        if ("ODI".equals(this.getSystemType())) {
            if (this.getVsDrCode().equals(this.getOperatorUserID()) &&
                this.getWord().getNodeIndex() == 0 &&
                creatFileUser.equals(this.getOperatorUserID())) {
                stuts = -1;
            }
            if (this.getAttendDrCode().equals(this.getOperatorUserID()) &&
                this.getWord().getNodeIndex() == 1 &&
                creatFileUser.equals(this.getOperatorUserID())) {
                stuts = 0;
            }
            if (this.getDirectorDrCode().equals(this.getOperatorUserID()) &&
                this.getWord().getNodeIndex() == 2 &&
                creatFileUser.equals(this.getOperatorUserID())) {
                stuts = 1;
            }
            //���Ǿ���ҽʦ��������ҽʦ
            if (this.getVsDrCode().equals(this.getOperatorUserID()) &&
                this.getAttendDrCode().equals(this.getOperatorUserID())) {
                stuts = -1;
            }
            //���Ǿ���ҽʦ��������ҽʦ��������ҽʦ
            if (this.getVsDrCode().equals(this.getOperatorUserID()) &&
                this.getAttendDrCode().equals(this.getOperatorUserID()) &&
                this.getDirectorDrCode().equals(this.getOperatorUserID())) {
                stuts = -1;
            }
        }
        if (stuts == this.getWord().getNodeIndex()) {
            return;
        }
        this.getWord().setNodeIndex(stuts);
        saveWord();
    }

    private void saveWord() {
        boolean mSwitch = this.getWord().getMessageBoxSwitch();
        this.getWord().setMessageBoxSwitch(false);
        this.getWord().onSave();
        this.getWord().setMessageBoxSwitch(mSwitch);
    }

    /**
     * �ύ
     */
    public void onSubmit() {
        if (this.getWord().getFileOpenName() == null) {
            //û����Ҫ�༭���ļ�
            this.messageBox("û����Ҫ�༭���ļ�");
            //this.messageBox("E0100");
            return;
        }

        TParm emrParm = this.getEmrChildParm();
        //���ǵ�ǰҽʦ��������������
        String lockedUser = this.findLockedEmrFileUser(this.getEmrChildParm());
        if (!lockedUser.equals("")) {
            this.messageBox(lockedUser + "ҽʦ������д�������޷��ύ��");
            return;
        }
        //���뱣���ļ�;
        boolean flg = onsaveEmr(false);
        /**if (!flg) {
            this.messageBox("����ʧ�ܣ�");
            return;
                 }**/
        //����ɹ������ύ��������;
        if (flg) {
            this.setEmrChildParm(emrParm);
            //��ʾ���ͷ���
            final String caseNo = this.getCaseNo();
            final String fileSeq = String.valueOf(emrParm.getData("FILE_SEQ"));
            //�ύ����;
            this.updateLocked(caseNo, fileSeq, false);
            //
            this.messageBox("�ύ�ɹ���");
            this.getWord().setCanEdit(false);
            this.getWord().onPreviewWord();
            return;
        }
        else {
            this.messageBox("�ύʧ�ܣ�");
            return;
        }
    }

    /**
     * �Ƿ�����һ��ҽʦ
     * @return boolean
     */
    public boolean checkonSumbit() {
        //Operator.getID()
        if (this.getOperatorUserID().equals("")) {
            return false;
        }
        else if (this.getOperatorUserID().equals(this.getVsDrCode())) {
            if (this.getAttendDrCode().equals("")) {
                return false;
            }
        }
        else if (this.getOperatorUserID().equals(this.getAttendDrCode())) {
            if (this.getDirectorDrCode().equals("")) {
                return false;
            }
        }
        else if (this.getOperatorUserID().equals(this.getDirectorDrCode())) {
            return false;
        }
        return true;

    }

    /**
     * ȡ���ύ
     */
    public void onSubmitCancel() {
        if (this.getWord().getFileOpenName() == null) {
            //û����Ҫ�༭���ļ�
            this.messageBox("û����Ҫ�༭���ļ�");
            //this.messageBox("E0100");
            return;
        }
        //5������ȡ���ύȨ�޿��ƣ�
        if (!this.checkDrForSubmitCancel()) {
            this.messageBox("Ȩ�޲���");
            //this.messageBox("E0011"); //Ȩ�޲���
            return;
        }
        TParm emrParm = this.getEmrChildParm();
        //Operator.getID()
        if (this.checkUserSubmit(emrParm, this.getOperatorUserID())) {
            if (this.messageBox("ѯ��", "��ǰ�û����ύ���Ƿ�ȡ���ύ��", 2) != 0) {
                return;
            }
            this.setOptDataParm(emrParm, 3);
        }
        else {
            if (this.messageBox("ѯ��", "��ǰ�û�δ�ύ���Ƿ�ȡ���ύ��", 2) != 0) {
                return;
            }
            this.setOptDataParm(emrParm, 4);
        }
        setEditLevelCancel();
        if (this.saveEmrFile(emrParm)) {
            this.setEmrChildParm(emrParm);
            this.getWord().setCanEdit(true);
            this.getWord().onEditWord();
            this.messageBox("ȡ���ύ�ɹ���");
            return;
        }
        else {
            this.messageBox("ȡ���ύʧ�ܣ�");
            return;
        }
    }

    /**
     * ����༭
     */
    public void onEdit() {

        //this.messageBox("file Name"+word.getFileOpenName());
        if (this.getWord().getFileOpenName() == null) {
            this.messageBox("û����Ҫ�༭���ļ���");
            //û����Ҫ�༭���ļ�
            //this.messageBox("E0100");
            return;
        }
        //this.messageBox("ruleType"+ruleType.equals("1"));

        //��дȨ��
        if (ruleType.equals("1")) {
            //this.messageBox("��û�б༭Ȩ��");
            this.getWord().onPreviewWord();
            //��û�б༭Ȩ��
            //this.messageBox("E0102");
            return;
        }
        else {
            if (!ruleType.equals("3")) {

                //this.messageBox("ruleType======" + ruleType);
                //��ɫ�ж�   ???????   SYS_POSITION  db2��oracle��һ��
                //��ʱע��;  ������ģ���Ȩ������
                /** String userRule = this.word.getFileLastEditUser();
                                // this.messageBox("����޸���" + userRule);
                 String ruleSQL = "SELECT B.FLAG1,B.FLAG2,B.FLAG3,B.FLAG4,B.FLAG5,B.FLAG6,B.FLAG7,B.FLAG8,B.FLAG9,B.FLAG10,";
                 ruleSQL += "B.FLAG11,B.FLAG12,B.FLAG13,B.FLAG14,B.FLAG15,B.FLAG16,B.FLAG17,B.FLAG18,B.FLAG19,B.FLAG20";
                 ruleSQL += " FROM SYS_OPERATOR A,SYS_POSITION B";
                 ruleSQL += " WHERE A.POS_CODE=B.POS_CODE AND USER_ID='" +
                     userRule + "'";
                 TParm ruleParm = new TParm(this.getDBTool().select(
                     DB2_POOL_NAME, ruleSQL));
                 int flagNum = 0;
                 for (int i = 1; i <= 20; i++) {
                 if (ruleParm.getValue("FLAG" + i, 0).equalsIgnoreCase("Y")) {
                         flagNum = i;
                         break;
                     }
                 }
                 //this.messageBox("flagNum==="+flagNum);
                 //this.messageBox("current user id"+this.getOperatorUserID());

                 TParm ruleParmM = new TParm(this.getDBTool().select(
                     DB2_POOL_NAME,
                     "SELECT B.FLAG" + flagNum +
                     " FROM SYS_OPERATOR A,SYS_POSITION B WHERE A.POS_CODE=B.POS_CODE AND USER_ID='" +
                     this.getOperatorUserID() + "'"));
                 //this.messageBox("ruleParm����" + ruleParm.getCount());
                 //this.messageBox("ruleParmM����" + ruleParmM.getCount());
                 if (ruleParm.getCount() > 0 && ruleParmM.getCount() > 0) {

                     String posType = ruleParm.getValue("FLAG" + flagNum, 0);
                     String posTypeM = ruleParmM.getValue("FLAG" + flagNum, 0);

                     //this.messageBox("posType"+posType);
                     //this.messageBox("posTypeM"+posTypeM);

                     if (!posType.equals(posTypeM)) {
                         this.messageBox("E0102");
                         return;
                     }
                 }**/
            }
        }
        //�½�ģ��
        if (this.getOnlyEditType().equals("NEW")) {
            //�ɱ༭
            this.getWord().setCanEdit(true);
            //�༭״̬(������)
            this.getWord().onEditWord();
            menuEnable(true);
            return;
        }
        //��
        if (this.getOnlyEditType().equals("ONLYONE")) {
            //this.messageBox("==============bengin checkDrForEdit=============");
            if (!this.checkDrForEdit()) {
                //3�������༭����Ȩ�޿��ƣ�
                //this.messageBox("��û�б༭Ȩ��");
                this.getWord().onPreviewWord();
                //this.messageBox("E0102");
                //��û�б༭Ȩ��
                return;
            }
            if ("3".equals(this.ruleType)) {
                TParm emrChildParm = this.getEmrChildParm();
                //this.messageBox("writeType"+this.writeType);
                //this.messageBox("OIDR_FLG"+emrChildParm.getValue("OIDR_FLG"));
                if ("OIDR".equals(this.writeType)) {
                    if ("N".equals(emrChildParm.getValue("OIDR_FLG"))) {
                        this.callFunction("UI|save|setEnabled", false);
                        this.messageBox("��û�б༭Ȩ��");
                        this.getWord().onPreviewWord();
                        //this.messageBox("E0102");
                        return;
                    }
                }
                if ("NSS".equals(this.writeType)) {
                    if ("N".equals(emrChildParm.getValue("NSS_FLG"))) {
                        this.callFunction("UI|save|setEnabled", false);
                        this.messageBox("��û�б༭Ȩ��");
                        this.getWord().onPreviewWord();
                        //this.messageBox("E0102");
                        return;
                    }
                }
            }

            //this.messageBox("111111У����������.");
            //��ʾ��XXҽʦ������д���������Ժ����ԡ�
            String lockedUser = this.findLockedEmrFileUser(this.getEmrChildParm());
            if (!lockedUser.equals("")) {
                this.messageBox(lockedUser + "ҽʦ������д���������Ժ����ԣ�");
                this.getWord().onPreviewWord();
                return;
            }

            //�ɱ༭
            this.getWord().setCanEdit(true);
            //PIC
            this.setContainPic(false);

            //�༭״̬(������)
            this.getWord().onEditWord();
            menuEnable(true);
            if ("3".equals(this.ruleType)) {
                this.callFunction("UI|save|setEnabled", true);
            }
            //??????ֵ��ҽ����this.isDutyDrList() &&  ��Ѫ����ֵ��ҽ����
            /**if (
                !this.getOperatorUserID().equals(this.getVsDrCode()) &&
                !this.getOperatorUserID().equals(this.getAttendDrCode()) &&
             !this.getOperatorUserID().equals(this.getDirectorDrCode())) {**/
            //��ҽʦ
            if (this.isDR()) {

            }
        }
    }

    /**
     * ��Ѫ�ܴ˷������ã���û��ֵ��ҽ����
     * �Ƿ���ֵ��ҽ��(??????ֵ��ҽ����)
     * @return boolean
     */
    public boolean isDutyDrList() {
        boolean falg = false;
        TParm parm = new TParm(this.getDBTool().select(
            "SELECT * FROM ODI_DUTYDRLIST WHERE STATION_CODE='" +
            this.getStationCode() + "' AND DR_CODE='" + Operator.getID() + "'"));
        if (parm.getCount() > 0) {
            falg = true;
        }
        return falg;
    }

    /**
     * ??????ֵ��ҽ����,��Ѫ�ܴ˷������ã���û��ֵ��ҽ����
     * �Ƿ���ֵ��ҽ��
     */
    public boolean isDutyDrList(String user) {
        boolean falg = false;
        TParm parm = new TParm(this.getDBTool().select(
            "SELECT * FROM ODI_DUTYDRLIST WHERE STATION_CODE='" +
            this.getStationCode() + "' AND DR_CODE='" + user + "'"));
        if (parm.getCount() > 0) {
            falg = true;
        }
        return falg;
    }

    /**
     * ȡ���༭
     */
    public void onCancelEdit() {
        if (!this.getWord().canEdit()) {
            //�Ѿ���ȡ���༭״̬
            this.messageBox("�Ѿ���ȡ���༭״̬");
            //this.messageBox("E0104");
            return;
        }
        if (this.checkSave()) {
            //���ɱ༭
            this.getWord().setCanEdit(false);
            menuEnable(false);
        }
    }

    /**
     * �Ƿ�����������ҽʦ(db2)
     */
    public boolean isCheckUserDr() {
        String sql = " SELECT VS_CODE,BEDSN_DR_CODE,DIS_DR_CODE" +
            " FROM ADM_INP " +
            " WHERE CASE_NO = '" + this.getCaseNo() + "' AND HOSP_AREA='" +
            this.getRegionCode() + "'";
        TParm parm = new TParm(this.getDBTool().select(DB2_POOL_NAME, sql));
        //����ҽʦ
        //this.messageBox("==����ҽʦ==" + parm.getValue("VS_CODE", 0));
        this.setVsDrCode(parm.getValue("VS_CODE", 0));
        //����ҽʦ
        // this.messageBox("==����ҽʦ==" + parm.getValue("BEDSN_DR_CODE", 0));
        this.setAttendDrCode(parm.getValue("BEDSN_DR_CODE", 0));
        //������
        //this.messageBox("==������==" + parm.getValue("BEDSN_DR_CODE", 0));
        this.setDirectorDrCode(parm.getValue("DIS_DR_CODE", 0));
        //this.messageBox("��ǰҽ��" + this.getOperatorUserID());
        if (this.getOperatorUserID().equals(parm.getValue("VS_CODE", 0))) {
            return true;
        }
        if (this.getOperatorUserID().equals(parm.getValue("BEDSN_DR_CODE", 0))) {
            return true;
        }
        if (this.getOperatorUserID().equals(parm.getValue("DIS_DR_CODE", 0))) {
            return true;
        }
        return false;
    }

    /**
     * �ж��Ƿ���ҽʦ;
     * @return boolean
     */
    public boolean isDR() {
        //ȡ������
        String sql = "SELECT USER_ID FROM SYS_DR WHERE USER_ID='" +
            this.getOperatorUserID() + "'";
        TParm parm = new TParm(this.getDBTool().select(DB2_POOL_NAME, sql));
        if (parm.getValue("USER_ID", 0) != null &&
            !parm.getValue("USER_ID", 0).equals("")) {
            return true;
        }
        return false;
    }


    /**
     * ������
     */
    public void loadTree() {
        treeRoot = this.getTTree(TREE_NAME).getRoot();
        this.setValue("MR_NO", this.getMrNo());
        if ("en".equals(this.getLanguage()) && patEnName != null &&
            patEnName.length() > 0) {
            this.setValue("PAT_NAME", this.patEnName);
        }
        else {
            this.setValue("PAT_NAME", this.getPatName());
        }
        boolean queryFlg = false;
        //סԺ
        if (this.getSystemType().equals("ODI")) {
            this.setValue("IPD_NO", this.getIpdNo());
            //�õ�����
            treeRoot.setText("סԺ");
            treeRoot.setEnText("Hospital");
            treeRoot.setType("Root");
            treeRoot.removeAllChildren();
            //û��
            //isCheckUserDr();
        }
        //����
        if (this.getSystemType().equals("ODO")) {
            ( (TTextField)this.getComponent("IPD_NO")).setVisible(false);
            ( (TLabel)this.getComponent("IPD_LAB")).setVisible(false);
            //�õ�����
            treeRoot.setText("����");
            treeRoot.setEnText("Outpatient");
            treeRoot.setType("Root");
            treeRoot.removeAllChildren();
            if (!this.getType().equals("O")) {
                queryFlg = true;
            }
        }
        //����
        if (this.getSystemType().equals("EMG")) {
            ( (TTextField)this.getComponent("IPD_NO")).setVisible(false);
            ( (TLabel)this.getComponent("IPD_LAB")).setVisible(false);
            //�õ�����
            treeRoot.setText("����");
            treeRoot.setEnText("Emergency");
            treeRoot.setType("Root");
            treeRoot.removeAllChildren();
            if (!this.getType().equals("E")) {
                queryFlg = true;
            }
        }
        //�������
        if (this.getSystemType().equals("HRM")) {
            ( (TTextField)this.getComponent("IPD_NO")).setVisible(false);
            ( (TLabel)this.getComponent("IPD_LAB")).setVisible(false);
            //�õ�����
            treeRoot.setText("�������");
            treeRoot.setEnText("Health Check");
            treeRoot.setType("Root");
            treeRoot.removeAllChildren();
            if (!this.getType().equals("H")) {
                queryFlg = true;
            }
        }
        //���ﻤʿվ
        if (this.getSystemType().equals("ONW")) {
            ( (TTextField)this.getComponent("IPD_NO")).setVisible(false);
            ( (TLabel)this.getComponent("IPD_LAB")).setVisible(false);
            //�õ�����
            treeRoot.setText("���ﻤʿվ");
            treeRoot.setEnText("Out-patient nurse station");
            treeRoot.setType("Root");
            treeRoot.removeAllChildren();
            if (!this.getType().equals("O")) {
                queryFlg = true;
            }
        }
        //סԺ��ʿվ
        if (this.getSystemType().equals("INW")) {
            ( (TTextField)this.getComponent("IPD_NO")).setVisible(false);
            ( (TLabel)this.getComponent("IPD_LAB")).setVisible(false);
            //�õ�����
            treeRoot.setText("סԺ��ʿվ");
            treeRoot.setEnText("Hospital nurse station");
            treeRoot.setType("Root");
            treeRoot.removeAllChildren();
            if (!this.getType().equals("I")) {
                queryFlg = true;
            }
        }
        //��Ⱦ����
        if (this.getSystemType().equals("INF")) {
            ( (TTextField)this.getComponent("IPD_NO")).setVisible(false);
            ( (TLabel)this.getComponent("IPD_LAB")).setVisible(false);
            //�õ�����
            treeRoot.setText("��Ⱦ����");
            treeRoot.setEnText("Infection Control");
            treeRoot.setType("Root");
            treeRoot.removeAllChildren();
            if (!this.getType().equals("F")) {
                queryFlg = true;
            }
        }
        //�õ��ڵ�����
        TParm parm = this.getRootNodeData();
        if (parm.getInt("ACTION", "COUNT") == 0) {
            //û������EMRģ�壡�������ݿ�����
            this.messageBox("û������EMRģ�壡�������ݿ�����");
            //this.messageBox("E0105");
            return;
        }
        int rowCount = parm.getInt("ACTION", "COUNT");
        for (int i = 0; i < rowCount; i++) {
            TParm mainParm = parm.getRow(i);
            //�ӽڵ�   EMR_CLASS���е�STYLE�ֶ�
            TTreeNode node = new TTreeNode();
            node.setID(mainParm.getValue("CLASS_CODE"));
            node.setText(mainParm.getValue("CLASS_DESC"));
            node.setEnText(mainParm.getValue("ENG_DESC"));
            //���ò˵���ʾ��ʽ
            node.setType("" + mainParm.getInt("CLASS_STYLE"));
            node.setGroup(mainParm.getValue("CLASS_CODE"));
            node.setValue(mainParm.getValue("SYS_PROGRAM"));
            node.setData(mainParm);

            //��������
            treeRoot.add(node);

            //$$=============start add by lx ����ģ��������޸�; ================$$//
            TParm allChildsParm = new TParm();
            this.getAllChildsByParent(mainParm.getValue("CLASS_CODE"),
                                      allChildsParm);
            int childRowCount = allChildsParm.getCount();
            for (int j = 0; j < childRowCount; j++) {
                TParm childDirParm = allChildsParm.getRow(j);

                TTreeNode childrenNode = new TTreeNode(allChildsParm.getValue(
                    "CLASS_DESC", j), allChildsParm.getValue("CLASS_STYLE", j));
                childrenNode.setID(allChildsParm.getValue("CLASS_CODE", j));
                childrenNode.setGroup(allChildsParm.getValue("CLASS_CODE", j));
                //mainParm.getValue("SYS_PROGRAM")?????��ʱû��
                treeRoot.findNodeForID(allChildsParm.getValue(
                    "PARENT_CLASS_CODE", j)).add(childrenNode);

                //��Ҷ�ڵ�ļ������ļ�
                if ( ( (String) allChildsParm.getValue("LEAF_FLG", j)).
                    equalsIgnoreCase("Y")) {

                    //��ѯ���ļ�
                    TParm childParm = this.getRootChildNodeData(allChildsParm.
                        getValue("CLASS_CODE", j), queryFlg);
                    //System.out.println("======childParm======"+childParm);

                    int rowCldCount = childParm.getInt("ACTION", "COUNT");
                    for (int z = 0; z < rowCldCount; z++) {
                        TParm chlidParmTemp = childParm.getRow(z);
                        TTreeNode nodeChlid = new TTreeNode();
                        nodeChlid.setID(chlidParmTemp.getValue("FILE_NAME"));
                        nodeChlid.setText(chlidParmTemp.getValue("DESIGN_NAME"));
                        nodeChlid.setType("4");
                        nodeChlid.setGroup(chlidParmTemp.getValue("CLASS_CODE"));
                        nodeChlid.setValue(chlidParmTemp.getValue(
                            "SUBCLASS_CODE"));
                        nodeChlid.setData(chlidParmTemp);
                        childrenNode.add(nodeChlid);
                    }

                }

            }
            //$$=============end add by lx ����ģ��������޸�; ================$$//
        }
        this.getTTree(TREE_NAME).update();
    }

    /**
     * �õ����ڵ�����
     * @return TParm
     */
    public TParm getRootNodeData() {
        TParm result = new TParm(this.getDBTool().select(
            "SELECT B.CLASS_DESC,B.ENG_DESC,B.CLASS_STYLE,A.CLASS_CODE,A.SYS_PROGRAM,A.SEQ " +
            " FROM EMR_TREE A,EMR_CLASS B WHERE A.SYSTEM_CODE = '" +
            this.getSystemType() +
            "' AND A.CLASS_CODE=B.CLASS_CODE ORDER BY B.SEQ,A.CLASS_CODE"));
        return result;
    }

    /**
     * �õ����ڵ�����
     * @return TParm
     */
    public TParm getRootChildNodeData(String classCode, boolean queryFlg) {
        TParm result = new TParm();
        String myTemp =
            // ����ģ��
            " AND ((B.DEPT_CODE IS NULL AND B.USER_ID IS NULL) "
            // ����ģ��  Operator.getDept()
            + " OR (B.DEPT_CODE = '" + this.getDeptCode() +
            "' AND B.USER_ID IS NULL) "
            // ����ģ��
            + " OR B.USER_ID = '" + this.getOperatorUserID() + "') ";
        String sql = "SELECT A.CASE_NO,A.FILE_SEQ,A.MR_NO,A.IPD_NO,A.FILE_PATH,A.FILE_NAME,A.DESIGN_NAME,A.CLASS_CODE,A.SUBCLASS_CODE,A.DISPOSAC_FLG," +
            " A.CREATOR_USER,A.CREATOR_DATE,A.CURRENT_USER,A.CANPRINT_FLG,A.MODIFY_FLG," +
            " A.CHK_USER1,A.CHK_DATE1,A.CHK_USER2,A.CHK_DATE2,A.CHK_USER3,A.CHK_DATE3," +
            " A.COMMIT_USER,A.COMMIT_DATE,A.IN_EXAMINE_USER,A.IN_EXAMINE_DATE,A.DS_EXAMINE_USER,A.DS_EXAMINE_DATE,A.PDF_CREATOR_USER,A.PDF_CREATOR_DATE," +
            " B.SUBCLASS_DESC,B.DEPT_CODE,B.RUN_PROGARM,B.SUBTEMPLET_CODE,B.CLASS_STYLE,B.OIDR_FLG,B.NSS_FLG,A.REPORT_FLG ";
        if (queryFlg) {
            if ("O".equals(this.getAdmType())) {
                result = new TParm(this.getDBTool().select(sql +
                    " FROM EMR_FILE_INDEX A,EMR_TEMPLET B WHERE A.MR_NO='" +
                    this.getMrNo() + "' AND A.CLASS_CODE='" + classCode +
                    "' AND A.DISPOSAC_FLG<>'Y' AND OPD_FLG='Y' AND A.SUBCLASS_CODE=B.SUBCLASS_CODE " +
                    myTemp +
                    " ORDER BY A.CLASS_CODE,A.SUBCLASS_CODE,A.FILE_SEQ"));
            }
            if ("I".equals(this.getAdmType())) {
                result = new TParm(this.getDBTool().select(sql +
                    " FROM EMR_FILE_INDEX A,EMR_TEMPLET B WHERE A.MR_NO='" +
                    this.getMrNo() + "' AND A.CLASS_CODE='" + classCode +
                    "' AND A.DISPOSAC_FLG<>'Y' AND IPD_FLG='Y' AND A.SUBCLASS_CODE=B.SUBCLASS_CODE " +
                    myTemp +
                    " ORDER BY A.CLASS_CODE,A.SUBCLASS_CODE,A.FILE_SEQ"));
            }
            if ("E".equals(this.getAdmType())) {
                result = new TParm(this.getDBTool().select(sql +
                    " FROM EMR_FILE_INDEX A,EMR_TEMPLET B WHERE A.MR_NO='" +
                    this.getMrNo() + "' AND A.CLASS_CODE='" + classCode +
                    "' AND A.DISPOSAC_FLG<>'Y' AND EMG_FLG='Y' AND A.SUBCLASS_CODE=B.SUBCLASS_CODE " +
                    myTemp +
                    " ORDER BY A.CLASS_CODE,A.SUBCLASS_CODE,A.FILE_SEQ"));
            }
            if ("H".equals(this.getAdmType())) {
                result = new TParm(this.getDBTool().select(sql +
                    " FROM EMR_FILE_INDEX A,EMR_TEMPLET B WHERE A.MR_NO='" +
                    this.getMrNo() + "' AND A.CLASS_CODE='" + classCode +
                    "' AND A.DISPOSAC_FLG<>'Y' AND HRM_FLG='Y' AND A.SUBCLASS_CODE=B.SUBCLASS_CODE " +
                    myTemp +
                    " ORDER BY A.CLASS_CODE,A.SUBCLASS_CODE,A.FILE_SEQ"));
            }
        }
        else {
            if ("O".equals(this.getAdmType())) {

                result = new TParm(this.getDBTool().select(sql +
                    " FROM EMR_FILE_INDEX A,EMR_TEMPLET B WHERE A.CASE_NO='" +
                    this.getCaseNo() + "' AND A.CLASS_CODE='" + classCode +
                    "' AND A.DISPOSAC_FLG<>'Y' AND OPD_FLG='Y' AND A.SUBCLASS_CODE=B.SUBCLASS_CODE " +
                    myTemp +
                    " ORDER BY A.CLASS_CODE,A.SUBCLASS_CODE,A.FILE_SEQ"));

            }
            if ("I".equals(this.getAdmType())) {

                result = new TParm(this.getDBTool().select(sql +
                    " FROM EMR_FILE_INDEX A,EMR_TEMPLET B WHERE A.CASE_NO='" +
                    this.getCaseNo() + "' AND A.CLASS_CODE='" + classCode +
                    "' AND A.DISPOSAC_FLG<>'Y' AND IPD_FLG='Y' AND A.SUBCLASS_CODE=B.SUBCLASS_CODE " +
                    myTemp +
                    " ORDER BY A.CLASS_CODE,A.SUBCLASS_CODE,A.FILE_SEQ"));

            }
            if ("E".equals(this.getAdmType())) {
                result = new TParm(this.getDBTool().select(sql +
                    " FROM EMR_FILE_INDEX A,EMR_TEMPLET B WHERE A.CASE_NO='" +
                    this.getCaseNo() + "' AND A.CLASS_CODE='" + classCode +
                    "' AND A.DISPOSAC_FLG<>'Y' AND EMG_FLG='Y' AND A.SUBCLASS_CODE=B.SUBCLASS_CODE " +
                    myTemp +
                    " ORDER BY A.CLASS_CODE,A.SUBCLASS_CODE,A.FILE_SEQ"));
            }
            if ("H".equals(this.getAdmType())) {
                result = new TParm(this.getDBTool().select(sql +
                    " FROM EMR_FILE_INDEX A,EMR_TEMPLET B WHERE A.CASE_NO='" +
                    this.getCaseNo() + "' AND A.CLASS_CODE='" + classCode +
                    "' AND A.DISPOSAC_FLG<>'Y' AND HRM_FLG='Y' AND A.SUBCLASS_CODE=B.SUBCLASS_CODE " +
                    myTemp +
                    " ORDER BY A.CLASS_CODE,A.SUBCLASS_CODE,A.FILE_SEQ"));
            }
        }
        return result;
    }

    /**
     * �����
     * @param parm Object
     */
    public void onTreeDoubled(Object parm) {
        //this.messageBox("come in!");
        TTreeNode node = (TTreeNode) parm;
        TParm emrParm = (TParm) node.getData();
        //System.out.println("=======emrParm==========="+emrParm);
        //this.messageBox("REPORT_FLG====="+emrParm.getValue("REPORT_FLG"));
        //����EMR�ļ��û�
        if (emrParm != null && emrParm.getValue("REPORT_FLG").equals("Y")) {
            //�򿪲���
            if (!this.getWord().onOpen(emrParm.getValue("FILE_PATH"),
                                       emrParm.getValue("FILE_NAME"), 3, true)) {
                return;
            }
            //���ò��ɱ༭
            this.getWord().setCanEdit(false);
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

            //this.getWord().getFocusManager().onPreviewWord();
        }
        else {
            if (!this.checkInputObject(parm)) {
                return;
            }
            //1.�����鿴Ȩ�޿��ƣ�
            if (!checkDrForOpen()) {
                this.messageBox("Ȩ�޲���");
                //this.messageBox("E0011"); //Ȩ�޲���
                return;
            }

            if (!node.getType().equals("4")) {
                return;
            }

            //�˳��Զ����涨ʱ��(��ʱȥ����ʱ����)
            /**this.cancel();
                         //ɾ��ǰ����ʱ�ļ�
                         delFile(this.getEmrChildParm().getValue("FILE_PATH"),
                    "TMP_" + this.getEmrChildParm().getValue("FILE_NAME"));
                         //��֤�˴���ʱ�ļ�
                         if (checkTMP(emrParm.getValue("FILE_PATH"),
                         "TMP_" + emrParm.getValue("FILE_NAME"))
                &&
             this.messageBox("ѯ��", "�˲����ĵ����ϴα༭�������쳣�жϣ��Ƿ�ָ��ϴα༭��״̬��", 2) == 0) {
                delFile(emrParm.getValue("FILE_PATH"),
                        emrParm.getValue("FILE_NAME"));
                copyFile(emrParm.getValue("FILE_PATH"),
                         emrParm.getValue("FILE_NAME"));
                         }
                         //ɾ���˴���ʱ�ļ�
                         delFile(emrParm.getValue("FILE_PATH"),
                    "TMP_" + emrParm.getValue("FILE_NAME"));
                         //�����Զ����涨ʱ��
                         this.schedule();**/
            //�򿪲���
            if (!this.getWord().onOpen(emrParm.getValue("FILE_PATH"),
                                       emrParm.getValue("FILE_NAME"), 3, true)) {
                return;
            }
            //���ò��ɱ༭
            this.getWord().setCanEdit(false);

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
            //���ú�
            setMicroField(false);
            TParm sexP = new TParm(this.getDBTool().select(DB2_POOL_NAME,
                "SELECT SEX_CODE FROM SYS_PATINFO WHERE MR_NO='" + this.getMrNo() +
                "'"));
            if (sexP.getInt("SEX_CODE", 0) == 9) {
                this.getWord().setSexControl(0);
            }
            else {
                this.getWord().setSexControl(sexP.getInt("SEX_CODE", 0));
            }
            //���ñ༭״̬
            this.setOnlyEditType("ONLYONE");
            //���õ�ǰ�༭����
            this.setEmrChildParm(emrParm);
            menuEnable(false);
            //����༭
            if (ruleType.equals("2") || ruleType.equals("3")) {
                onEdit();
                setCanEdit(emrParm);
                LogOpt(emrParm);
                setPrintAndMfyFlg(emrParm);
            }
            else {
                this.getWord().onPreviewWord();
                LogOpt(emrParm);
                setPrintAndMfyFlg(emrParm);
            }

        }

    }

    /**
     * ��־��¼
     */
    private void LogOpt(TParm emrParm) {
        // ��������ҽʦ��ֵ��ҽʦ�����Ӳ���������־/EMR_OPTLOG/��  || this.isDutyDrList()
        if (this.isCheckUserDr() || isDR()) {
            TParm result = OptLogTool.getInstance().writeOptLog(this.
                getParmObj(), "O",
                emrParm);
        }
        // ����������ҽʦ��ֵ��ҽʦ�����Ӳ���������־/EMR_OPTLOG/����
        else {
            TParm result = OptLogTool.getInstance().writeOptLog(this.
                getParmObj(), "R",
                emrParm);
        }

    }

    /**
     *  ���������ӡ��ʶ�������޸ı�ʶ
     * @param emrParm TParm
     */
    private void setPrintAndMfyFlg(TParm emrParm) {
        // ���������ӡ��ʶ�������޸ı�ʶ
        String caseNo = this.getCaseNo();
        String fileSeq = emrParm.getValue("FILE_SEQ");
        if (!this.checkDrForDel()) {
            setCanPrintFlgAndModifyFlg("", "", false);
        }
        else {
            setCanPrintFlgAndModifyFlg(caseNo, fileSeq, true);
        }

    }


    private void setCanEdit(TParm emrParm) {
        int fileSeq = emrParm.getInt("FILE_SEQ");
        String subTempletCode = emrParm.getValue("SUBTEMPLET_CODE");
        String subClassCode = emrParm.getValue("SUBCLASS_CODE");
        String caseNo = emrParm.getValue("CASE_NO");
        if (subTempletCode.length() != 0) {
            TParm result = new TParm(getDBTool().select(
                " SELECT MAX(FILE_SEQ) FILE_SEQ" +
                " FROM EMR_FILE_INDEX " +
                " WHERE CASE_NO='" + caseNo + "' " +
                " AND   SUBCLASS_CODE='" + subClassCode + "'"));
            if (fileSeq != result.getInt("FILE_SEQ", 0)) {
                this.getWord().setCanEdit(false);
                menuEnable(false);
            }
        }
        else {
            TParm result = new TParm(getDBTool().select(
                " SELECT B.FILE_SEQ" +
                " FROM EMR_TEMPLET A,EMR_FILE_INDEX B " +
                " WHERE A.SUBTEMPLET_CODE='" + subClassCode + "'" +
                " AND   A.SUBCLASS_CODE = B.SUBCLASS_CODE" +
                " AND   B.CASE_NO = '" + caseNo + "'"));
            if (result.getCount() > 0) {
                this.getWord().setCanEdit(false);
                menuEnable(false);
            }
        }
    }

    /**
     *  �õ���
     * @param tag String
     * @return TTree
     */
    public TTree getTTree(String tag) {
        return (TTree)this.getComponent(tag);
    }

    /**
     * �½�����(���)
     */
    public void onCreatMenu() {
        //���ֶ�дȨ�޹ܿ�
        openChildDialog();
        this.setOnlyEditType("NEW");
        this.getWord().fixedTryReset(this.getMrNo(), this.getCaseNo());
    }

    /**
     * �򿪲���(Ψһ)
     */
    public void onOpenMenu() {
        //���ֶ�дȨ�޹ܿ�
        openChildDialog();
        this.setOnlyEditType("NEW");
    }

    /**
     * ��������(����׷��)
     */
    public void onAddMenu() {
        //���ֶ�дȨ�޹ܿ�
        openChildDialog();
        this.getWord().fixedTryReset(this.getMrNo(), this.getCaseNo());
    }

    /**
     * ���Ӵ���
     */
    public void openChildDialog() {
        //2�������½�����Ȩ�޿��ƣ�
        if (!this.checkDrForNew()) {
            this.messageBox("Ȩ�޲���");
            //this.messageBox("E0011"); //Ȩ�޲���
            return;
        }
        if (!checkSave()) {
            return;
        }
        //ģ������
        String emrClass = this.getTTree(TREE_NAME).getSelectNode().getGroup();
        String nodeName = this.getTTree(TREE_NAME).getSelectNode().getText();
        String emrType = this.getTTree(TREE_NAME).getSelectNode().getType();
        String programName = this.getTTree(TREE_NAME).getSelectNode().getValue();

        //����ǵ���HIS��������������
        if (programName.length() != 0) {
            TParm hisParm = new TParm();
            hisParm.setData("CASE_NO", this.getCaseNo());
            this.openDialog(programName, hisParm);
            return;
        }
        TParm parm = new TParm();
        String whereOtherStr = "";
        if (this.ruleType.equals("3") && "OIDR".equals(this.writeType)) {
            whereOtherStr += " AND OIDR_FLG='Y'";
        }
        if (this.ruleType.equals("3") && "NSS".equals(this.writeType)) {
            whereOtherStr += " AND NSS_FLG='Y'";
        }
        if (this.getSystemType() != null && this.getSystemType().length() != 0) {
            whereOtherStr += " AND (SYSTEM_CODE='" + this.getSystemType() +
                "' OR SYSTEM_CODE IS NULL)";
        }
        String myTemp =
            // ����ģ��
            " AND ((DEPT_CODE IS NULL AND USER_ID IS NULL) "
            // ����ģ�� Operator.getDept()
            + " OR (DEPT_CODE = '" + this.getDeptCode() +
            "' AND USER_ID IS NULL) "
            // ����ģ�� Operator.getID()
            + " OR USER_ID = '" + this.getOperatorUserIP() + "') ";
        //סԺ
        if (this.getAdmType().equals("I")) {
            parm = new TParm(this.getDBTool().select(
                "SELECT CLASS_CODE,SUBCLASS_CODE,SUBCLASS_DESC,TEMPLET_PATH,SEQ,DEPT_CODE,EMT_FILENAME,RUN_PROGARM,SUBTEMPLET_CODE,CLASS_STYLE FROM EMR_TEMPLET WHERE CLASS_CODE='" +
                emrClass +
                "' AND IPD_FLG='Y' " + whereOtherStr + myTemp + " ORDER BY SEQ"));

            parm.setData("SYSTEM_CODE", this.getSystemType());
            parm.setData("NODE_NAME", nodeName);
            parm.setData("TYPE", emrType);
            parm.setData("DEPT_CODE", this.getDeptCode());
        }
        //����
        if (this.getAdmType().equals("O")) {
            parm = new TParm(this.getDBTool().select(
                "SELECT CLASS_CODE,SUBCLASS_CODE,SUBCLASS_DESC,TEMPLET_PATH,SEQ,DEPT_CODE,EMT_FILENAME,RUN_PROGARM,SUBTEMPLET_CODE,CLASS_STYLE FROM EMR_TEMPLET WHERE CLASS_CODE='" +
                emrClass +
                "' AND OPD_FLG='Y' " + whereOtherStr + myTemp + " ORDER BY SEQ"));
            parm.setData("SYSTEM_CODE", this.getSystemType());
            parm.setData("NODE_NAME", nodeName);
            parm.setData("TYPE", emrType);
            parm.setData("DEPT_CODE", this.getDeptCode());
        }
        //����
        if (this.getAdmType().equals("E")) {
            parm = new TParm(this.getDBTool().select(
                "SELECT CLASS_CODE,SUBCLASS_CODE,SUBCLASS_DESC,TEMPLET_PATH,SEQ,DEPT_CODE,EMT_FILENAME,RUN_PROGARM,SUBTEMPLET_CODE,CLASS_STYLE FROM EMR_TEMPLET WHERE CLASS_CODE='" +
                emrClass +
                "' AND EMG_FLG='Y' " + whereOtherStr + myTemp + " ORDER BY SEQ"));
            parm.setData("SYSTEM_CODE", this.getSystemType());
            parm.setData("NODE_NAME", nodeName);
            parm.setData("TYPE", emrType);
            parm.setData("DEPT_CODE", this.getDeptCode());
        }
        if (this.getAdmType().equals("H")) {
            parm = new TParm(this.getDBTool().select(
                "SELECT CLASS_CODE,SUBCLASS_CODE,SUBCLASS_DESC,TEMPLET_PATH,SEQ,DEPT_CODE,EMT_FILENAME,RUN_PROGARM,SUBTEMPLET_CODE,CLASS_STYLE FROM EMR_TEMPLET WHERE CLASS_CODE='" +
                emrClass +
                "' AND HRM_FLG='Y' " + whereOtherStr + myTemp + " ORDER BY SEQ"));
            parm.setData("SYSTEM_CODE", this.getSystemType());
            parm.setData("NODE_NAME", nodeName);
            parm.setData("TYPE", emrType);
            parm.setData("DEPT_CODE", this.getDeptCode());
        }
        //�����½�UI
        Object obj = this.openDialog("%ROOT%\\config\\emr\\EMRChildUI.x", parm);
        if (obj == null || ! (obj instanceof TParm)) {
            return;
        }

        //ɾ��ǰ����ʱ�ļ�
        /**delFile(this.getEmrChildParm().getValue("FILE_PATH"),
                "TMP_" + this.getEmrChildParm().getValue("FILE_NAME"));
                 //�˳��Զ����涨ʱ��
                 this.cancel();**/
        TParm action = (TParm) obj;
        //Ԥ����������
        String runProgarmName = action.getValue("RUN_PROGARM");
        TParm runParm = new TParm();
        //�ж������Ƿ���Դ�
        String styleClass = action.getValue("CLASS_STYLE");
        //ֻ���½�
        if ("1".equals(styleClass)) {
            if (runProgarmName.length() != 0) {
                TParm ermParm = new TParm();
                ermParm.setData("TYPE", "1");
                ermParm.setData("FILE_NAME", action.getValue("SUBCLASS_DESC"));
                ermParm.setData("ADM_DATE", this.getAdmDate());
                //��Ժʱ��
                TParm tem = this.getAdmInpDSData();
                if (tem != null) {
                    //ermParm.setData("DS_DATE", tem.getTimestamp("DS_DATE", 0));
                    ermParm.setData("DS_DATE",
                                    StringTool.getTimestamp(parm.
                        getValue("DS_DATE", 0),
                        "yyyyMMddHHmmss"));

                }
                else {
                    ermParm.setData("DS_DATE", "");
                }
                Object objParm = this.openDialog(runProgarmName, ermParm);
                if (objParm != null) {
                    runParm = (TParm) objParm;
                }
                else {
                    return;
                }
            }
        }
        //Ψһֻ�ܽ���һ��
        if ("2".equals(styleClass)) {
            String subclassCode = action.getValue("SUBCLASS_CODE");
            TParm fileParm = new TParm(this.getDBTool().select("SELECT CASE_NO,FILE_SEQ,MR_NO,IPD_NO,FILE_PATH,FILE_NAME,DESIGN_NAME,CLASS_CODE,SUBCLASS_CODE,DISPOSAC_FLG FROM EMR_FILE_INDEX WHERE CASE_NO='" +
                this.getCaseNo() + "' AND SUBCLASS_CODE='" + subclassCode + "'"));
            if (fileParm.getCount() > 0) {
                //���ļ��Ѿ����ڲ��������½�
                this.messageBox("���ļ��Ѿ����ڲ��������½�");
                //this.messageBox("E0106");
                return;
            }
            if (runProgarmName.length() != 0) {
                TParm ermParm = new TParm();
                ermParm.setData("TYPE", "1");
                ermParm.setData("FILE_NAME", action.getValue("SUBCLASS_DESC"));
                ermParm.setData("ADM_DATE", this.getAdmDate());
                //��Ժʱ��
                TParm tem = this.getAdmInpDSData();
                if (tem != null) {
                    //ermParm.setData("DS_DATE", tem.getTimestamp("DS_DATE", 0));
                    ermParm.setData("DS_DATE",
                                    StringTool.getTimestamp(parm.
                        getValue("DS_DATE", 0),
                        "yyyyMMddHHmmss"));
                }
                else {
                    ermParm.setData("DS_DATE", "");
                }
                Object objParm = this.openDialog(runProgarmName, ermParm);
                if (objParm != null) {
                    runParm = (TParm) objParm;
                }
                else {
                    return;
                }
            }
        }
        //�ļ�����׷������ǰ����
        if ("3".equals(styleClass)) {
            //�õ�����ģ���ļ���
            String subtempletCode = action.getValue("SUBTEMPLET_CODE");
            //��ǰģ��
            String subclassCode = action.getValue("SUBCLASS_CODE");
            if (subtempletCode.length() != 0) {
                //�鿴�����ļ������Ƿ����
                TParm fileParm = new TParm(this.getDBTool().select("SELECT CASE_NO,FILE_SEQ,MR_NO,IPD_NO,FILE_PATH,FILE_NAME,DESIGN_NAME,CLASS_CODE,SUBCLASS_CODE,DISPOSAC_FLG FROM EMR_FILE_INDEX WHERE CASE_NO='" +
                    this.getCaseNo() + "' AND SUBCLASS_CODE='" + subtempletCode +
                    "'"));
                if (fileParm.getCount() > 0) {
                    //���ļ�
                    if (runProgarmName.length() != 0) {
                        TParm ermParm = new TParm();
                        ermParm.setData("TYPE", "2");
                        ermParm.setData("FILE_NAME",
                                        action.getValue("SUBCLASS_DESC"));
                        ermParm.setData("ADM_DATE", this.getAdmDate());
                        //��Ժʱ��
                        TParm tem = this.getAdmInpDSData();
                        if (tem != null) {
                            //tem.getTimestamp("DS_DATE", 0)
                            ermParm.setData("DS_DATE",
                                            StringTool.getTimestamp(parm.
                                getValue("DS_DATE", 0),
                                "yyyyMMddHHmmss"));
                        }
                        else {
                            ermParm.setData("DS_DATE", "");
                        }
                        Object objParm = this.openDialog(runProgarmName,
                            ermParm);
                        if (objParm != null) {
                            runParm = (TParm) objParm;
                            TParm addEmr = new TParm(this.getDBTool().select("SELECT CASE_NO,FILE_SEQ,MR_NO,IPD_NO,FILE_PATH,FILE_NAME,DESIGN_NAME,CLASS_CODE,SUBCLASS_CODE,DISPOSAC_FLG FROM EMR_FILE_INDEX WHERE CASE_NO='" +
                                this.getCaseNo() + "' AND SUBCLASS_CODE='" +
                                subclassCode +
                                "' ORDER BY FILE_SEQ DESC"));
                            //�Ѿ��м�¼
                            if (addEmr.getCount() > 0) {
                                //�õ����༭���ļ�׷��
                                TParm addParm = addEmr.getRow(0);
                                addParm.setData("FLG", true);
                                this.onOpenEmrFile(addParm);
                                //TEMP
                                EFixed fixed = this.getWord().findFixed(
                                    "APPLEND");
                                if (fixed == null) {
                                    this.messageBox("�޲���㣡");
                                    return;
                                }
                                fixed.setFocus();
                                if (!this.getWord().separatePanel()) {
                                    return;
                                }
                                EFixed fixedDate = this.getWord().findFixed(
                                    "DATETIME");
                                if (fixedDate != null) {
                                    fixed = this.getWord().insertFixed("DATEMR",
                                        "��¼ʱ��:" +
                                        runParm.getValue("EMRADD_DATE"));
                                }
                                this.getWord().onInsertFileFrontFixed("APPLEND",
                                    action.getValue("TEMPLET_PATH"),
                                    action.getValue("EMT_FILENAME"), 2, false);
                                this.setEmrChildParm(action);
                                this.setEmrChildParm(this.getFileServerEmrName());
                                this.getWord().update();
                                return;
                            }
                            else {
                                //�õ������ļ�׷��
                                TParm fParm = fileParm.getRow(0);
                                fParm.setData("FLG", true);
                                this.onOpenEmrFile(fParm);
                                //TEMP
                                EFixed fixed = this.getWord().findFixed(
                                    "APPLEND");
                                if (fixed == null) {
                                    this.messageBox("�޲���㣡");
                                    return;
                                }
                                fixed.setFocus();
                                if (!this.getWord().separatePanel()) {
                                    return;
                                }
                                EFixed fixedDate = this.getWord().findFixed(
                                    "DATETIME");
                                if (fixedDate != null) {
                                    fixed = this.getWord().insertFixed("DATEMR",
                                        "��¼ʱ��:" +
                                        runParm.getValue("EMRADD_DATE"));
                                }
                                this.getWord().onInsertFileFrontFixed("APPLEND",
                                    action.getValue("TEMPLET_PATH"),
                                    action.getValue("EMT_FILENAME"), 2, false);
                                this.setEmrChildParm(action);
                                this.setEmrChildParm(this.getFileServerEmrName());
                                this.getWord().update();
                                return;
                            }
                        }
                        else {
                            return;
                        }
                    }
                    return;
                }
                else {
                    //������δ��д�޷�����
                    this.messageBox("������δ��д�޷�������");
                    //this.messageBox("E0108��");
                    return;
                }
            }
            else {
                //��ģ������û�����ø���ģ�������ú���д��
                this.messageBox("��ģ������û�����ø���ģ�������ú���д��");
                //this.messageBox("E0109");
                return;
            }
        }

        String templetPath = action.getValue("TEMPLET_PATH");
        String templetName = action.getValue("EMT_FILENAME");
        openTempletNoEdit(templetPath, templetName, runParm);

        this.setEmrChildParm(action);
        this.setEmrChildParm(this.getFileServerEmrName());
        // ���������ӡ�������޸ı�ʶ�����أ�
        setCanPrintFlgAndModifyFlg("", "", false);
    }

    /**
     * �򿪲���
     * @param parm Object
     */
    public void onOpenEmrFile(TParm parm) {
        if (parm == null) {
            return;
        }
        //��������
        if ("ODI".equals(this.getSystemType())) {
            //�Ƿ��в鿴��Ȩ��  !this.isDutyDrList() &&
            if (!this.isDR() && !isCheckUserDr() &&
                !"2".equals(this.ruleType) && !"3".equals(this.ruleType)) {
                //Ȩ�޲���
                this.messageBox("Ȩ�޲���");
                //this.messageBox("E0011");
                return;
            }
        }
        //�򿪲���
        if (!this.getWord().onOpen(parm.getValue("FILE_PATH"),
                                   parm.getValue("FILE_NAME"), 3, false)) {
            return;
        }
        //���ò��ɱ༭
        this.getWord().setCanEdit(false);
        TParm allParm = new TParm();
        allParm.setData("FILE_TITLE_TEXT", "TEXT", this.hospAreaName);
        allParm.setData("FILE_TITLEENG_TEXT", "TEXT", this.hospEngAreaName);
        allParm.setData("FILE_HEAD_TITLE_MR_NO", "TEXT", this.getMrNo());
        allParm.setData("FILE_HEAD_TITLE_IPD_NO", "TEXT", this.getIpdNo());
        allParm.setData("FILE_128CODE", "TEXT", this.getMrNo());
        allParm.addListener("onDoubleClicked", this, "onDoubleClicked");
        allParm.addListener("onMouseRightPressed", this, "onMouseRightPressed");
        this.getWord().setWordParameter(allParm);
        //���ú�
        if (!parm.getBoolean("FLG")) {
            setMicroField(true);
        }
        //���ñ༭״̬
        this.setOnlyEditType("NEW");
        //���õ�ǰ�༭����
        this.setEmrChildParm(parm);
        menuEnable(false);
        //����༭
        if (ruleType.equals("2") || ruleType.equals("3")) {
            onEdit();
        }
    }

    /**
     * �ж��Ƿ�Ҫ�����ļ�
     * @param fileParm TParm
     * @param subclassCode String
     * @return boolean
     */
    public boolean isEmrCreatFile(TParm fileParm, String subclassCode) {
        boolean falg = true;
        int rowCount = fileParm.getCount();
        for (int i = 0; i < rowCount; i++) {
            TParm temp = fileParm.getRow(i);
            if (subclassCode.equals(temp.getValue("SUBCLASS_CODE"))) {
                falg = false;
                break;
            }
        }
        return falg;
    }

    /**
     * �õ���Ժʱ��
     * @return TParm
     */
    public TParm getAdmInpDSData() {
        TParm admParm = new TParm(this.getDBTool().select(DB2_POOL_NAME,
            "SELECT DS_DATE FROM ADM_INP WHERE CASE_NO='" + this.getCaseNo() +
            "'"));
        if (admParm.getCount() < 0) {
            return null;
        }
        return admParm;
    }

    /**
     * ɾ������
     */
    public void onDelFile() {
        //����ѯ��ɾ����
        if (this.messageBox("ѯ��", "�Ƿ�ɾ����", 2) == 0) {
            TTreeNode node = this.getTTree(TREE_NAME).getSelectionNode();
            TParm fileData = (TParm) node.getData();
            //System.out.println("fileData========================="+fileData);
            //6������ɾ��Ȩ�޿��ƣ�
            if (!this.checkDrForDel()) {
                this.messageBox("������ɾ����");
                //this.messageBox("E0107"); //������ɾ��
                return;
            }

            delFileTempletFile(fileData.getValue("FILE_PATH"),
                               fileData.getValue("FILE_NAME"));

            //ɾ���ļ�
            /** if (delFileTempletFile(fileData.getValue("FILE_PATH"),
                                    fileData.getValue("FILE_NAME"))) {**/
            //ɾ�����ݿ�����
            Map del = this.getDBTool().update(
                "DELETE EMR_FILE_INDEX WHERE CASE_NO='" +
                fileData.getValue("CASE_NO") + "' AND FILE_SEQ='" +
                fileData.getValue("FILE_SEQ") + "'");
            TParm delParm = new TParm(del);
            if (delParm.getErrCode() < 0) {
                //ɾ��ʧ��
                this.messageBox("ɾ��ʧ�ܣ�");
                //this.messageBox("E0003");
                this.setOnlyEditType("NEW");
                return;
            }
            //ɾ��ǰ����ʱ�ļ�
            /**delFile(fileData.getValue("FILE_PATH"),
                    "TMP_" + fileData.getValue("FILE_NAME"));
                         //�˳��Զ����涨ʱ��
                         this.cancel();**/
            //ɾ���ɹ�
            this.messageBox("ɾ���ɹ���");
            //this.messageBox("P0003");
            this.getWord().onNewFile();
            this.setOnlyEditType("NEW");
            this.getWord().setCanEdit(false);
            // ����������ҽʦ��ֵ��ҽʦ�����Ӳ���������־/EMR_OPTLOG/ɾ��
            TParm result = OptLogTool.getInstance().writeOptLog(this.getParmObj(),
                "D",
                fileData);
            /**}
                         else {
                //ɾ��ʧ��
                this.messageBox("E0003");
                this.setOnlyEditType("NEW");
                return;
                         }**/
            //������
            loadTree();
        }
        else {
            return;
        }
    }

    /**
     * Ƭ��
     */
    public void onInsertPY() {
        TParm inParm = new TParm();
        inParm.setData("TYPE", "2");
        inParm.setData("ROLE", "1");
        //Operator.getID()
        inParm.setData("DR_CODE", this.getOperatorUserID());
        inParm.setData("DEPT_CODE", this.getDeptCode());
        inParm.addListener("onReturnContent", this, "onReturnContent");
//        this.openWindow("%ROOT%\\config\\emr\\EMRComPhraseQuote.x",inParm);
        TWindow window = (TWindow)this.openWindow(
            "%ROOT%\\config\\emr\\EMRComPhraseQuote.x", inParm, true);
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
        //this.messageBox("this.getDeptCode()"+this.getDeptCode());
        inParm.setData("DEPT_CODE", this.getDeptCode());
        inParm.setData("TWORD", this.getWord());
        inParm.addListener("onReturnTemplateContent", this,
                           "onReturnTemplateContent");
        TWindow window = (TWindow)this.openWindow(
            "%ROOT%\\config\\emr\\EMRTemplateComPhraseQuote.x", inParm, true);
        window.setX(ImageTool.getScreenWidth() - window.getWidth());
        window.setY(0);
        window.setVisible(true);

    }

    public void onReturnTemplateContent(TWord templateWord) {
        //this.messageBox("ִ��Paste");
        this.getWord().onPaste();
    }


    /**
     * Ƭ���¼
     * @param value String
     */
    public void onReturnContent(String value) {
        if (!this.getWord().pasteString(value)) {
            //ִ��ʧ��
            this.messageBox("ִ��ʧ�ܣ�");
            //this.messageBox("E0005");
        }
    }

    /**
     * �ٴ�����
     */
    public void onInsertLCSJ() {
        TParm inParm = new TParm();
        inParm.setData("CASE_NO", this.getCaseNo());
        inParm.addListener("onReturnContent", this, "onReturnContent");
//        this.openWindow("%ROOT%\\config\\emr\\EMRMEDDataUI.x",inParm);
        TWindow window = (TWindow)this.openWindow(
            "%ROOT%\\config\\emr\\EMRMEDDataUI.x", inParm, true);
        window.setX(ImageTool.getScreenWidth() - window.getWidth());
        window.setY(0);
        window.setVisible(true);
    }

    /**
     * ɾ�������ļ�
     * @param templetPath String
     * @param templetName String
     * @return boolean
     */
    public boolean delFileTempletFile(String templetPath, String templetName) {
        //Ŀ¼���һ����Ŀ¼FILESERVER
        String rootName = TIOM_FileServer.getRoot();
        //ģ��·��������
        String templetPathSer = TIOM_FileServer.getPath("EmrData");
        //�õ�SocketͨѶ����
        TSocket socket = TIOM_FileServer.getSocket();

        //ɾ���ļ�
        boolean isDelFile = TIOM_FileServer.deleteFile(socket,
            rootName + templetPathSer +
            templetPath +
            "\\" + templetName + ".jhw");

        //ɾ��ͼƬ�ļ����е��ϴ�ͼƬ;
        String dir = rootName + templetPathSer + templetPath + "\\" +
            templetName + "\\";
        String s[] = TIOM_FileServer.listFile(socket, dir);
        if (s != null) {
            for (int i = 0; i < s.length; i++) {
                String path = dir + "\\" + s[i];
                TIOM_FileServer.deleteFile(socket, path);
            }
        }
        if (isDelFile) {
            return true;
        }

        return false;
    }

    /**
     * ��ģ��
     * @param templetPath String
     * @param templetName String
     */
    public void openTempletNoEdit(String templetPath, String templetName,
                                  TParm parm) {

        this.getWord().onOpen(templetPath, templetName, 2, false);
        TParm allParm = new TParm();
        allParm.setData("FILE_TITLE_TEXT", "TEXT", this.hospAreaName);
        allParm.setData("FILE_TITLE_EN_TEXT", "TEXT", this.hospEngAreaName);
        allParm.setData("FILE_HEAD_TITLE_MR_NO", "TEXT", this.getMrNo());
        allParm.setData("FILE_HEAD_TITLE_IPD_NO", "TEXT", this.getIpdNo());
        allParm.setData("FILE_128CODE", "TEXT", this.getMrNo());
        allParm.addListener("onDoubleClicked", this, "onDoubleClicked");
        allParm.addListener("onMouseRightPressed", this, "onMouseRightPressed");
        this.getWord().setWordParameter(allParm);

        //���ú�
        setMicroField(true);
        String comlunName[] = parm.getNames();
        for (String temp : comlunName) {
            this.getWord().setMicroField(temp, parm.getValue(temp));
            this.setCaptureValueArray(temp, parm.getValue(temp));
        }

        //���ñ༭״̬
        this.setOnlyEditType("NEW");
        //���ò����Ա༭
        this.getWord().setCanEdit(false);
        menuEnable(false);
        //����༭
        if (ruleType.equals("2") || ruleType.equals("3")) {
            onEdit();
        }

    }

    /**
     * ���ú�
     */
    public void setMicroField(boolean falg) {
        //���˻�������
        Map map = this.getDBTool().select(DB2_POOL_NAME,
                                          "SELECT * FROM EMR_MACRO_PATINFO WHERE 1=1 AND MR_NO='" +
                                          this.getMrNo() +
                                          "'");

        TParm parm = new TParm(map);
        if (parm.getErrCode() < 0) {
            //ȡ�ò��˻�������ʧ��
            this.messageBox("ȡ�ò��˻�������ʧ�ܣ�");
            //this.messageBox("E0110");
            return;
        }

        Timestamp tempBirth = parm.getValue("��������", 0).length() == 0 ?
            SystemTool.getInstance().getDate() :
            StringTool.getTimestamp(parm.getValue("��������", 0), "yyyy-MM-dd");
        //��������
        String age = "0";
        if (this.getAdmDate() != null) {
            age = OdiUtil.getInstance().showAge(tempBirth, this.getAdmDate());
        }
        else {
            age = "";
        }
        String dateStr = StringTool.getString(SystemTool.getInstance().getDate(),
                                              "yyyy/MM/dd HH:mm:ss");
        parm.addData("����", age);
        parm.addData("�����", this.getCaseNo());
        parm.addData("������", this.getMrNo());
        parm.addData("סԺ��", this.getIpdNo());
        //��ǰҽ����������  Operator.getDept()
        parm.addData("����", this.getDeptDesc(this.getDeptCode()));
        //????? ��Ҫ�޸ķ���   Operator.getName()
        parm.addData("������", this.getOperatorName(this.getOperatorUserID()));
        parm.addData("��������", dateStr);
        parm.addData("����",
                     StringTool.getString(SystemTool.getInstance().getDate(),
                                          "yyyy/MM/dd"));
        parm.addData("ʱ��",
                     StringTool.getString(SystemTool.getInstance().getDate(),
                                          "HH:mm:ss"));
//        parm.addData("EMR_DATE",StringTool.getString(SystemTool.getInstance().getDate(),"yyyy/MM/dd HH:mm:ssS"));
        parm.addData("����ʱ��", dateStr);
        //this.messageBox("��Ժʱ��" + this.getAdmDate());
        parm.addData("��Ժʱ��",
                     StringTool.getString(this.getAdmDate(),
                                          "yyyy/MM/dd HH:mm:ss"));

        parm.addData("��Ժʱ��",
                     StringTool.getString(new java.sql.Timestamp(System.
            currentTimeMillis()),
                                          "yyyy/MM/dd"));

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
//      parm.addData("SYSTEM","COLUMNS","EMR_DATE");
        parm.addData("SYSTEM", "COLUMNS", "����ʱ��");
        parm.addData("SYSTEM", "COLUMNS", "��Ժʱ��");
        parm.addData("SYSTEM", "COLUMNS", "���ÿ���");

        //��ѯסԺ������Ϣ(���ţ�סԺ���)
        TParm odiParm = new TParm(this.getDBTool().select(
            "SELECT * FROM EMR_MACRO_ADMINP WHERE CASE_NO='" + this.getCaseNo() +
            "'"));

        if (odiParm.getCount() > 0) {
            for (String parmName : odiParm.getNames()) {
                parm.addData(parmName, odiParm.getValue(parmName, 0));
            }

        }
        else {
            for (String parmName : odiParm.getNames()) {
                parm.addData(parmName, "");
            }
        }

        //סԺ���
        if ("ODI".equals(this.getSystemType()) ||
            "INW".equals(this.getSystemType())) {
            parm.addData("�ż�ס��", "סԺ");
            String sql = "SELECT A.ICD_CODE,B.ICD_CHN_DESC,A.IO_TYPE,A.MAINDIAG_FLG FROM ADM_INPDIAG A,SYS_DIAGNOSIS B WHERE A.ICD_CODE=B.ICD_CODE AND CASE_NO='" +
                this.getCaseNo() + "' ORDER BY MAINDIAG_FLG DESC";
            TParm odiDiagParm = new TParm(this.getDBTool().select(DB2_POOL_NAME,
                sql));
            //������Ժ���
            StringBuffer inDiagAll = new StringBuffer();
            int inDiags = 1;
            //���г�Ժ���
            StringBuffer diag = new StringBuffer();
            int diags = 1;
            if (odiDiagParm.getCount() > 0) {
                int rowCount = odiDiagParm.getCount();
                for (int i = 0; i < rowCount; i++) {
                    TParm temp = odiDiagParm.getRow(i);
                    //�ż������
                    if ("I".equals(temp.getValue("IO_TYPE")) &&
                        "Y".equals(temp.getValue("MAINDIAG_FLG"))) {
                        parm.addData("�ż������", temp.getValue("ICD_CHN_DESC"));
                        inDiagAll.append("�ż������:" +
                                         temp.getValue("ICD_CHN_DESC") + ",");

                    }
                    //��Ժ���
                    if ("M".equals(temp.getValue("IO_TYPE")) &&
                        "Y".equals(temp.getValue("MAINDIAG_FLG"))) {
                        parm.addData("��Ժ���", temp.getValue("ICD_CHN_DESC"));
                        inDiagAll.append("��Ժ�����:" +
                                         temp.getValue("ICD_CHN_DESC") + ",");
                    }
                    //add
                    if ("M".equals(temp.getValue("IO_TYPE")) &&
                        "N".equals(temp.getValue("MAINDIAG_FLG"))) {
                        inDiagAll.append("��Ժ�������" + inDiags + ":" +
                                         temp.getValue("ICD_CHN_DESC") + ",");
                        inDiags++;
                    }

                    //��Ժ���
                    if ("O".equals(temp.getValue("IO_TYPE")) &&
                        "Y".equals(temp.getValue("MAINDIAG_FLG"))) {
                        parm.addData("��Ժ���", temp.getValue("ICD_CHN_DESC"));

                        diag.append("��Ժ�����:" + temp.getValue("ICD_CHN_DESC") +
                                    ",");
                    }
                    if ("O".equals(temp.getValue("IO_TYPE")) &&
                        "N".equals(temp.getValue("MAINDIAG_FLG"))) {
                        parm.addData("��Ժ��ϸ���", temp.getValue("ICD_CHN_DESC"));
                        diag.append("��Ժ��ϸ���" + diags + ":" +
                                    temp.getValue("ICD_CHN_DESC") + ",");
                        diags++;
                    }
                }

                //this.messageBox("��Ժ�������"+inDiagAll.toString());
                parm.addData("��Ժ�������", diag.toString());
                parm.addData("��Ժ�������", inDiagAll.toString());
            }
            else {
                parm.addData("�ż������", "");
                //parm.addData("��Ժ���", "");
                parm.addData("��Ժ���", "");

                parm.addData("��Ժ�������", "");
                parm.addData("��Ժ�������", "");
            }

            //����סԺ����
            int inDays = StringTool.getDateDiffer(SystemTool.getInstance().
                                                  getDate(), this.getAdmDate());
            parm.setData("סԺ����", 0, inDays == 0 ? "1" : inDays + " ��");

            parm.addData("SYSTEM", "COLUMNS", "�ż�ס��");
            parm.addData("SYSTEM", "COLUMNS", "����");
            parm.addData("SYSTEM", "COLUMNS", "����");
            parm.addData("SYSTEM", "COLUMNS", "סԺ�������");
            parm.addData("SYSTEM", "COLUMNS", "�ż������");
            parm.addData("SYSTEM", "COLUMNS", "��Ժ���");
            parm.addData("SYSTEM", "COLUMNS", "��Ժ���");
            parm.addData("SYSTEM", "COLUMNS", "��Ժ��ϸ���");
            parm.addData("SYSTEM", "COLUMNS", "��Ժʱ��");
            parm.addData("SYSTEM", "COLUMNS", "����ҽʦ");
            parm.addData("SYSTEM", "COLUMNS", "����ҽʦ");
            parm.addData("SYSTEM", "COLUMNS", "����ҽʦ");
            parm.addData("SYSTEM", "COLUMNS", "סԺ����");
        }
        //�������(��ʱ���޸�;)
        if ("ODO".equals(this.getSystemType()) ||
            "EMG".equals(this.getSystemType())) {
            parm.addData("�ż�ס��", "����");
            //���������
            StringBuffer mainDiag = new StringBuffer();
            //�����������
            StringBuffer DiagAll = new StringBuffer();
            //������ϸ���
            int odoDiagAllCount = 1;
            //���ﱾ���������(CASE_NO����ҽ�����)
            //���ﱾ���������(CASE_NO)
            TParm odoDiagParm = new TParm(this.getDBTool().select("SELECT A.ICD_CODE,B.ICD_CHN_DESC,A.ICD_TYPE,A.MAIN_DIAG_FLG FROM OPD_DIAGREC A,SYS_DIAGNOSIS B WHERE A.ICD_CODE=B.ICD_CODE AND CASE_NO='" +
                this.getCaseNo() + "'"));
            if (odoDiagParm.getCount() > 0) {
                int rowCount = odoDiagParm.getCount();
                for (int i = 0; i < rowCount; i++) {
                    TParm temp = odoDiagParm.getRow(i);
                    if ("Y".equals(temp.getValue("MAIN_DIAG_FLG"))) {
                        if ("W".equals(temp.getValue("ICD_TYPE"))) {
                            mainDiag.append("��ҽ�����:" +
                                            temp.getValue("ICD_CHN_DESC") + "|");
                            //DiagAll.append("��ҽ�����:"+temp.getValue("ICD_CHN_DESC"));
                        }
                        if ("C".equals(temp.getValue("ICD_TYPE"))) {
                            mainDiag.append("��ҽ�����:" +
                                            temp.getValue("ICD_CHN_DESC") + "|");
                            //DiagAll.append("��ҽ�����:"+temp.getValue("ICD_CHN_DESC"));
                        }
                    }
                    else {
                        DiagAll.append("�������" + odoDiagAllCount + ":" +
                                       temp.getValue("ICD_CHN_DESC") + "|");
                        odoDiagAllCount++;
                    }
                }
                parm.addData("�������", mainDiag.toString());
                parm.addData("�����������", mainDiag.toString() + DiagAll.toString());
            }
            else {
                parm.addData("�������", "");
                parm.addData("�����������", "");
            }
            parm.addData("SYSTEM", "COLUMNS", "�ż�ס��");
            parm.addData("SYSTEM", "COLUMNS", "�������");
            parm.addData("SYSTEM", "COLUMNS", "�����������");
        }
        //���ﲿ��
        if ("EMG".equals(this.getSystemType())) {
            parm.addData("�ż�ס��", "����");
            parm.addData("SYSTEM", "COLUMNS", "�ż�ס��");
        }
        //�������
        if ("HRM".equals(this.getSystemType())) {
            parm.addData("�ż�ס��", "�������");
            parm.addData("SYSTEM", "COLUMNS", "�ż�ס��");
        }

        //������Ϣ   ��Ѫ��DB2��û��DRUG_TYPE�ֶΣ�����������������???
        //����ʷ(MR_NO)
        /**StringBuffer drugStr = new StringBuffer();
                 String drugSql =
            "SELECT A.DRUG_OR_INGRD,A.DRUGORINGRD_CODE,A.ADM_TYPE,";
                 drugSql += " CASE WHEN  A.DRUG_OR_INGRD='1' THEN C.CHI_DESC WHEN  A.DRUG_OR_INGRD='2' THEN B.ORDER_DESC ELSE D.ALLERGY_CHN_DESC END ORDER_DESC";
                 drugSql += " FROM ODO_DRUGALLERGY A";
         drugSql += " LEFT JOIN SYS_FEE B ON A.DRUGORINGRD_CODE=B.ORDER_CODE";
                 drugSql +=
            " LEFT JOIN PHA_INGREDIENT C ON A.DRUGORINGRD_CODE=C.INGRED_CODE";
                 drugSql +=
         " LEFT JOIN SYS_ALLERGYTYPE D ON A.DRUGORINGRD_CODE=D.ALLERGY_TYPE";
                 drugSql += " WHERE A.MR_NO='" + this.getMrNo() + "'";

         TParm drugParm = new TParm(this.getDBTool().select(DB2_POOL_NAME,
            drugSql));
                 if (drugParm.getCount() > 0) {
            int rowCount = drugParm.getCount();
            for (int i = 0; i < rowCount; i++) {
                TParm temp = drugParm.getRow(i);
                drugStr.append(temp.getValue("ORDER_DESC") + ",");
            }
            parm.addData("����ʷ", drugStr.toString());
                 }
                 else {
            parm.addData("����ʷ", "");
                 }
                 parm.addData("SYSTEM", "COLUMNS", "����ʷ");
                 //���ο������������ֲ�ʷ(CASE_NO)
         TParm subjParm = new TParm(this.getDBTool().select(DB2_POOL_NAME,
            "SELECT SUBJ_TEXT FROM ODO_SUBJREC WHERE CASE_NO='" +
            this.getCaseNo() + "'"));
                 if (subjParm.getCount() > 0) {
            parm.addData("�����ֲ�ʷ", subjParm.getValue("SUBJ_TEXT", 0));
                 }
                 else {
            parm.addData("�����ֲ�ʷ", "");
                 }
                 parm.addData("SYSTEM", "COLUMNS", "�����ֲ�ʷ");

                 //����ʷ(MR_NO)
                 StringBuffer medhisStr = new StringBuffer();
                 String medhisSQL = "SELECT B.ICD_CHN_DESC,A.COMMENTS";
                 medhisSQL += " FROM ODO_MEDHISTORY A,SYS_DIAGNOSIS B";
                 medhisSQL += " WHERE A.MR_NO='" + this.getMrNo() + "'";
                 medhisSQL += " AND A.ICD_CODE=B.ICD_CODE";
         TParm medhisParm = new TParm(this.getDBTool().select(DB2_POOL_NAME,
            medhisSQL));
                 if (medhisParm.getCount() > 0) {
            int rowCount = medhisParm.getCount();
            for (int i = 0; i < rowCount; i++) {
                TParm temp = medhisParm.getRow(i);
                medhisStr.append(temp.getValue("ICD_CHN_DESC").length() != 0 ?
                                 temp.getValue("ICD_CHN_DESC") :
                                 "" + temp.getValue("DESCRIPTION"));
            }
            parm.addData("����ʷ", medhisStr.toString());
                 }
                 else {
            parm.addData("����ʷ", "");
                 }
                 parm.addData("SYSTEM", "COLUMNS", "����ʷ");

                 //����
                 TParm sumParm = new TParm(this.getDBTool().select(DB2_POOL_NAME, "SELECT TEMPERATURE,PLUSE,RESPIRE,SYSTOLICPRESSURE,DIASTOLICPRESSURE FROM SUM_VTSNTPRDTL WHERE CASE_NO='" +
            this.getCaseNo() + "' ORDER BY EXAMINE_DATE,EXAMINESESSION DESC"));
                 if (sumParm.getCount() > 0) {
            parm.addData("����", sumParm.getValue("TEMPERATURE", 0));
            parm.addData("����", sumParm.getValue("PLUSE", 0));
            parm.addData("����", sumParm.getValue("RESPIRE", 0));
            parm.addData("����ѹ", sumParm.getValue("SYSTOLICPRESSURE", 0));
            parm.addData("����ѹ", sumParm.getValue("DIASTOLICPRESSURE", 0));
                 }
                 else {
            parm.addData("����", "");
            parm.addData("����", "");
            parm.addData("����", "");
            parm.addData("����ѹ", "");
            parm.addData("����ѹ", "");
                 }
                 parm.addData("SYSTEM", "COLUMNS", "����");
                 parm.addData("SYSTEM", "COLUMNS", "����");
                 parm.addData("SYSTEM", "COLUMNS", "����");
                 parm.addData("SYSTEM", "COLUMNS", "����ѹ");
                 parm.addData("SYSTEM", "COLUMNS", "����ѹ");**/

        String names[] = parm.getNames();
        TParm obj = (TParm)this.getWord().getFileManager().getParameter();
        TParm macroCodeParm = new TParm(this.getDBTool().select("SELECT MICRO_NAME,HIS_ATTR,HIS_TABLE_NAME FROM EMR_MICRO_CONVERT WHERE CODE_FLG='Y'"));
        for (String temp : names) {
            //��ֵ��־;
            boolean flag = false;
            //ѭ��������(�Դ��ŵĴ���)
            for (int j = 0; j < macroCodeParm.getCount(); j++) {
                //�ֵ����� P ���õ�,D�Զ���� �ֵ�;
                String dictionaryType = macroCodeParm.getValue("HIS_ATTR", j);
                //��Ӧ�ı���;
                String tableName = macroCodeParm.getValue("HIS_TABLE_NAME", j);
                if (macroCodeParm.getValue("MICRO_NAME", j).equals(temp)) {
                    if ("�Ա�".equals(temp)) {
                        if (parm.getInt(temp, 0) == 9) {
                            this.getWord().setSexControl(0);
                        }
                        else {
                            this.getWord().setSexControl(parm.getInt(temp, 0));
                        }
                    }
                    if (falg) {
                        //���ú��������ʾ��
                        this.getWord().setMicroFieldCode(temp,
                            getDictionary(tableName,
                                          parm.getValue(temp, 0)),
                            this.getEMRCode(dictionaryType,
                                            tableName,
                                            parm.getValue(temp, 0)));

                        //��������Ԫ�� CODE
                        hideElemMap.put(temp,
                                        this.getEMRCode(dictionaryType,
                            tableName,
                            parm.getValue(temp, 0)));

                        //����ץȡ��ֵ;
                        this.setCaptureValueArray(temp,
                                                  getDictionary(tableName,
                            parm.getValue(temp, 0)));

                        obj.setData(temp, "TEXT",
                                    getDictionary(tableName,
                                                  parm.getValue(temp, 0)));

                    }
                    else {
                        obj.setData(temp, "TEXT",
                                    getDictionary(tableName,
                                                  parm.getValue(temp, 0)));
                    }
                    //�Ѹ�ֵ;
                    flag = true;
                    break;

                }
            }
            //�Ѿ���ֵ,����ѭ����һ����
            if (flag) {
                continue;
            }

            String tempValue = parm.getValue(temp, 0);
            if (tempValue == null) {
                continue;
            }
            if (falg) {
                this.getWord().setMicroField(temp, tempValue);
                this.setCaptureValueArray(temp, tempValue);
                //��������Ԫ�� CODE
                hideElemMap.put(temp, tempValue);
                obj.setData(temp, "TEXT", tempValue);
            }
            else {
                obj.setData(temp, "TEXT", tempValue);
            }
        }
        this.setHiddenElements(obj);
        //��ʱ����Ҫ�������ٴ�·��ֵ;��
        //this.setCLPValue();
        this.getWord().setWordParameter(obj);

    }


    /**
     * ������
     */
    private EFixed fixed;

    private EMacroroutine macroroutine;
    /**
     * ץȡ����
     */
    //private ECapture capture;
    /**
     * �ṹ�������Ҽ�����
     */
    public void onMouseRightPressed() {
        EComponent e = this.getWord().getFocusManager().getFocus();
        if (e == null) {
            return;
        }
        //�Ƿ�ɱ༭
        this.onEdit();

        TTreeNode node = getTTree(TREE_NAME).getSelectionNode();
        if (node.getType().equals("4")) {
            TParm emrParm = (TParm) node.getData();
            setCanEdit(emrParm);
        }

        if (!this.getWord().canEdit()) {
            return;
        }
        //ץȡ��
        if (e instanceof ECapture) {
            //System.out.println("============is ECapture===========");
            return;
        }
        //��
        if (e instanceof EFixed) {
            fixed = (EFixed) e;
            this.getWord().popupMenu(fixed.getName() + "�޸�,onModify", this);
        }
        //ͼƬ
        if (e instanceof EMacroroutine) {
            macroroutine = (EMacroroutine) e;
            this.getWord().popupMenu(macroroutine.getName() +
                                     "�༭,onModifyMacroroutine", this);

        }

//        if(e instanceof ECapture){
//            capture = (ECapture)e;
//        }
//        if(capture!=null){
//            if(word.focusInCaptue(capture.getName())){
//                word.popupMenu(capture.getName()+"�޸�,onModifyCapture",this);
//            }
//        }
    }

    /**
     * ����ͼƬFileServer��Ӧȫ·��;
     * @return String
     */
    private String getImgFullPath() {
        //�ļ�������·��;
        //fileserver+/JHW/caseNo��λ/caseNo��/MR_NO/fileName/
        //Ŀ¼���һ����Ŀ¼FILESERVER
        String rootName = TIOM_FileServer.getRoot();
        //ģ��·��������
        String templetPathSer = TIOM_FileServer.getPath("EmrData");
        String filePath = this.getEmrChildParm().getValue("FILE_PATH");
        String foldName = this.getEmrChildParm().getValue("FILE_NAME");
        String fileFullPath = rootName + templetPathSer + filePath + "/" +
            foldName;
        //System.out.println("===file server fileFullPath==="+fileFullPath
        return fileFullPath;
    }

    /**
     * ���ģ��·��
     * @return String
     */
    private String getTemplatePath() {
        //ģ��·��������
        String templetPathSer = TIOM_FileServer.getPath("EmrData");
        String filePath = this.getEmrChildParm().getValue("FILE_PATH");
        String foldName = this.getEmrChildParm().getValue("FILE_NAME");
        String templatePath = templetPathSer + filePath + "/" +
            foldName;
        templatePath = templatePath.replaceAll("\\\\", "/");
        //System.out.println("templatePath==============" + templatePath);
        return templatePath;
    }

    /**
     * �ϴ�word�д�����������༭��ͼƬ�ļ�;
     * @return boolean
     */
    private boolean uploadPictureToFileServer() {
        boolean flag = true;
        //����word�ļ���picԪ��������Ǳ�����ʱ·���ģ����ϴ���fileServer.
        TList components = this.getWord().getPageManager().getComponentList();
        int size = components.size();
        for (int i = 0; i < size; i++) {
            EPage ePage = (EPage) components.get(i);
            //this.messageBox("EPanel size" + ePage.getComponentList().size());
            for (int j = 0; j < ePage.getComponentList().size(); j++) {
                EPanel ePanel = (EPanel) ePage.getComponentList().get(j);
                //
                for (int k = 0; k < ePanel.getBlockSize(); k++) {
                    IBlock block = (IBlock) ePanel.get(k);
                    if (block != null) {
                        if (block.getObjectType() == EComponent.TABLE_TYPE) {
                            //System.out.println("�������Ԫ��.....");
                            for (int row = 0;
                                 row <
                                 ( (ETable) block).getComponentList().size();
                                 row++) {
                                int columnCount = ( (ETable) block).get(row).
                                    getComponentList().size();
                                //��������;
                                for (int column = 0; column < columnCount;
                                     column++) {
                                    int tPanelCount = ( (ETable) block).get(row).
                                        get(column).getComponentList().size();
                                    //ȡ���������;
                                    for (int tpc = 0; tpc < tPanelCount; tpc++) {
                                        IBlock tBlock = ( (ETable) block).get(
                                            row).get(column).get(tpc).get(0);
                                        if (tBlock != null) {
                                            //����ͼƬ����,���ϴ�;
                                            flag = this.processPicElement(
                                                tBlock);
                                            if (!flag) {
                                                return false;
                                            }

                                        }
                                    }
                                }
                            }

                        }
                        else {
                            flag = this.processPicElement(block);
                            if (!flag) {
                                return false;
                            }

                        }

                    }

                }

            }

        }
        //�ϴ��ļ��ɹ����������ʱ�༭�ļ������ļ�;
        if (flag) {
            try {
                this.delDir(picTempPath);
            }
            catch (IOException ex) {
            }
        }

        return flag;
    }

    /**
     * ����PICԪ��,���ϴ���������;
     * @param block IBlock
     * @return boolean
     */
    private boolean processPicElement(IBlock block) {
        TSocket socket = TIOM_FileServer.getSocket();
        String filePath = this.getImgFullPath();
        //System.out.println("===filePath===" + filePath);
        if (block instanceof EMacroroutine &&
            ( (EMacroroutine) block).isPic()) {
            //this.messageBox("is pic");
            VPic pic = ( (VPic) ( (EMacroroutine) block).
                        getModel().getMVList().get(0).get(0));
            String fullPathName = pic.getPictureName();
            /**if (pic.getPictureName() == null) {
                this.messageBox("ͼƬ·������Ϊ�գ�");
                return false;
                         }**/
            if (fullPathName != null) {
                //�������������ʱ·����,˵����Ҫ�ϴ�
                if (pic.getPictureName().indexOf(picTempPath) != -1) {
                    /**System.out.println("��ʼ�ϴ��ļ�=============" +
                        pic.getPictureName());**/
                    //�����ļ�����ʼ�ϴ���
                    String finalPictureName = fullPathName.
                        substring(fullPathName.lastIndexOf("/") + 1,
                                  fullPathName.lastIndexOf("."));
                    /**System.out.println(
                        "=======finalPictureName=========" +
                        finalPictureName);**/
                    String fileName = finalPictureName.substring(0,
                        finalPictureName.lastIndexOf("_"));
                    //System.out.println("fileName" + fileName);
                    //���챳��ͼ������ǰ��ͼ������ϳ�ͼ��
                    String bgImage = fileName + "_BG.gif";
                    String ticImage = fileName + "_FG.TIC";
                    String finalImage = fileName + "_FINAL.jpg";

                    byte[] bgData = TIOM_FileServer.readFile(
                        picTempPath + bgImage);
                    /**System.out.println(
                        "=== picTempPath + bgImage===" +
                        picTempPath + bgImage);
                     System.out.println("====bgData====" +
                        bgData.length); ;
                     System.out.println("filePath  bgImage" +
                        filePath + "/" + bgImage);**/
                    //����ͼƬ�ļ�Ŀ¼;
                    boolean isMkDir = TIOM_FileServer.mkdir(socket,
                        filePath);
                    if (isMkDir) {
                        boolean isflag1 = TIOM_FileServer.writeFile(
                            socket, filePath + "/" + bgImage,
                            bgData);

                        byte[] fgData = TIOM_FileServer.readFile(
                            picTempPath + ticImage);
                        boolean isflag2 = TIOM_FileServer.writeFile(
                            socket, filePath + "/" + ticImage,
                            fgData);

                        byte[] finalData = TIOM_FileServer.readFile(
                            picTempPath + finalImage);
                        boolean isflag3 = TIOM_FileServer.writeFile(
                            socket, filePath + "/" + finalImage,
                            finalData);
                        /** System.out.println("is flag1====" + isflag1);
                         System.out.println("is flag2====" + isflag2);
                         System.out.println("is isflag3====" +
                             isflag3);**/

                        if (!isflag1 || !isflag2 || !isflag3) {
                            return false;
                        }
                        //pic path���Ϊ�������ļ�·����
                        /**System.out.println("=====���pic·��====" +
                            "%FILE.ROOT%/" + filePath + "/" +
                            finalImage);**/
                        pic.setPictureName("%FILE.ROOT%" +
                                           getTemplatePath() + "/" + finalImage);
                        this.getWord().update();

                    }

                } //ֻ�Ǳ���ֱ���ϴ��ı���ͼ
                else if (pic.getPictureName().indexOf(":") == 1) {
                    String finalPictureName = fullPathName.
                        substring(fullPathName.lastIndexOf("/") + 1,
                                  fullPathName.length());

                    //System.out.println("====fullPathName====="+fullPathName);
                    //System.out.println("====finalPictureName====="+finalPictureName);

                    boolean isMkDir = TIOM_FileServer.mkdir(socket,
                        filePath);
                    if (isMkDir) {
                        byte[] bgData = TIOM_FileServer.readFile(fullPathName);
                        boolean isflag1 = TIOM_FileServer.writeFile(
                            socket, filePath + "/" + finalPictureName,
                            bgData);
                        if (!isflag1) {
                            return false;
                        }
                        pic.setPictureName("%FILE.ROOT%" +
                                           getTemplatePath() + "/" +
                                           finalPictureName);
                        this.getWord().update();

                    }

                }
            }
        }

        return true;
    }

    /**
     * �༭ͼƬ����;
     */
    public void onModifyMacroroutine() {
        //this.messageBox("��ͼƬ�༭����.");
        //����һ����ʱ��ͼ�ļ��У� ����ʱ�����ʱ�ļ�;
        File f = new File(picTempPath);
        if (!f.exists()) {
            f.mkdirs();
        }
        //�ļ�������·��;
        //fileserver+/JHW/caseNo��λ/caseNo��/MR_NO/fileName/
        String fileFullPath = this.getImgFullPath();
        //System.out.println("===file server fileFullPath==="+fileFullPath);

        //ȡ��Ӧ��ͼƬ;
        MV mv = macroroutine.getModel().getMVList().get("ͼ��");
        if (mv == null) {
            return;
        }
        DIV div = mv.get("ͼƬ");
        if (! (div instanceof VPic)) {
            return;
        }
        VPic pic = (VPic) div;

        //�ϳ�ͼ;
        String finalImg = pic.getPictureName();
        //System.out.println("=========finalImg========" + finalImg);

        //�ϳ�ͼ�Ƿ�Ϊnull;  _FINAL.jp
        //�ϳ�ͼ��null���������ϴ�����ͼƬ��
        if (finalImg == null) {
            this.messageBox("�����ϴ�����ͼƬ!");
            return;
            //�ϳ�ͼ����null,
        }
        else {
            String fullFileName = finalImg.substring(finalImg.lastIndexOf("/") +
                1, finalImg.length());
            //System.out.println("fullFileName==============" + fullFileName);
            String fileName = fullFileName.substring(0,
                fullFileName.indexOf("."));
            String extendName = fullFileName.substring(fullFileName.indexOf(".") +
                1, fullFileName.length());
            //System.out.println("======fileName======" + fileName);
            //System.out.println("======extendName======" + extendName);

            //���������ļ����ļ���
            String bgImage = "";
            String ticImage = "";
            String finalImage = "";
            //����_FINALΪ��������ļ�;
            if (fileName.indexOf("_FINAL") != -1) {
                fileName = fileName.substring(0, fileName.lastIndexOf("_"));
            }
            else {
                if (!extendName.equalsIgnoreCase("gif")) {
                    this.messageBox("��gif����ͼ�����Ա༭!");
                    return;
                }
            }
            //extendName
            bgImage = fileName + "_BG.gif";
            ticImage = fileName + "_FG.TIC";
            finalImage = fileName + "_FINAL.jpg";

            //�ж�tic��ǰ��ͼ�Ƿ���ڣ� ������������
            String flg = "NEW";
            //���жϱ�����ʱ�ļ��������б༭���ļ���
            //����Ϊ flg=NEW;
            File ticFile = new File(picTempPath + ticImage);
            //����TIC�ļ�
            if (ticFile.exists()) {
                flg = "OPEN";
            }
            else {
                //û�У����ļ����������Ƿ��У�
                //�У������ص�������ʱ�ļ����¡� flg=OPEN
                //�õ�SocketͨѶ����
                TSocket socket = TIOM_FileServer.getSocket();
                byte ticByte[] = TIOM_FileServer.readFile(socket,
                    fileFullPath + "/" + ticImage);
                //Ӧ������������
                if (ticByte != null) {
                    //д��ʱ�ļ�;
                    TIOM_FileServer.writeFile(picTempPath + ticImage, ticByte);
                    //���ص�����;
                    byte bgByte[] = TIOM_FileServer.readFile(socket,
                        fileFullPath + "/" + bgImage);
                    //д��ʱ�ļ�;
                    TIOM_FileServer.writeFile(picTempPath + bgImage, bgByte);
                    flg = "OPEN";

                    //��������û�еģ�
                }
                else {
                    //System.out.println("=====����=====");
                    long timestamp = new Date().getTime();
                    //�����ļ���;
                    bgImage = fileName + "_" + timestamp + "_BG." + extendName;
                    ticImage = fileName + "_" + timestamp + "_FG.TIC";
                    finalImage = fileName + "_" + timestamp + "_FINAL.jpg";
                    //System.out.println("picName============" +pic.getPictureName());
                    //����
                    if (pic.getPictureName().indexOf(":") == 1) {
                        //System.out.println("�����ļ�");
                        byte bgByte[] = TIOM_AppServer.readFile(pic.
                            getPictureName());
                        TIOM_FileServer.writeFile(picTempPath + bgImage, bgByte);
                        //����Ϊ�ļ��������ϱ���
                    }
                    else {
                        //System.out.println("Զ���ļ�");
                        //Զ���ļ���ζ�ȡ�ģ������ĸ�ʽ��
                        byte bgByte[] = null;
                        String imageFilePath = "";
                        if (pic.getPictureName().indexOf("%ROOT%") != -1) {
                            bgByte = TIOM_AppServer.readFile(TIOM_AppServer.
                                SOCKET, pic.getPictureName());
                        }
                        else if (pic.getPictureName().indexOf("%FILE.ROOT%") !=
                                 -1) {
                            imageFilePath = pic.getPictureName().replace(
                                "%FILE.ROOT%", TIOM_FileServer.getRoot());
                            bgByte = TIOM_FileServer.readFile(TIOM_FileServer.
                                getSocket(), imageFilePath);
                        }

                        //System.out.println("�������غ��ļ�·��:"+picTempPath + fileName+"."+extendName);
                        TIOM_FileServer.writeFile(picTempPath + bgImage, bgByte);
                    }
                    //д��ʱ�ļ�;
                    flg = "NEW";
                }

            }

            /**System.out.println("========flg==========" + flg);
                         System.out.println("======bgImage======" + bgImage);
                         System.out.println("======ticImage======" + ticImage);
             System.out.println("======finalImage======" + finalImage);**/
            BufferedImage bfImage = this.readImage(picTempPath + bgImage);
            int imgWidth = bfImage.getWidth();
            int imgHeight = bfImage.getHeight();
            //System.out.println("======width======="+imgWidth);
            //System.out.println("======height======="+imgHeight);

            //OPEN  NEW
            //���粻����tic     ��Ϊ������
            if (flg.equals("NEW")) {
                //�����Ƿ����   �ļ��������� 800 /600
                Image_Paint image_Paint1 = new Image_Paint(this.getWord()
                    , picTempPath, ticImage, imgWidth, imgHeight
                    , picTempPath, bgImage
                    , picTempPath, finalImage
                    , "NEW", pic);
                //1024*768
                image_Paint1.setSize(ImageTool.getScreenWidth(),
                                     ImageTool.getScreenHeight());
                image_Paint1.setVisible(true);
                image_Paint1.setTitle("ͼƬ�༭");
                //���� Ϊ���޸�;
            }
            else {
                Image_Paint image_Paint1 = new Image_Paint(this.getWord()
                    , picTempPath, ticImage, imgWidth, imgHeight
                    , picTempPath, bgImage
                    , picTempPath, finalImage
                    , "OPEN", pic);
                //1024*768
                image_Paint1.setSize(ImageTool.getScreenWidth(),
                                     ImageTool.getScreenHeight());
                image_Paint1.setVisible(true);
                image_Paint1.setTitle("ͼƬ�༭");

            }

        }
        //����ͼƬ��Ӧ����(��������Ӧ��·��������)��

    }


    /**
     * ���޸�
     */
    public void onModify() {
        if (fixed == null) {
            return;
        }
        Object obj = this.openDialog("%ROOT%\\config\\emr\\ModifUI.x",
                                     fixed.getText());
        if (obj != null) {
            fixed.setText(obj.toString());
        }
    }

//    /**
//     * ץȡ�޸�
//     */
//    public void onModifyCapture(){
//        if(capture==null)
//            return;
//        Object obj = this.openDialog("%ROOT%\\config\\emr\\ModifUI.x",this.word.getCaptureValue(capture.getName()));
//        if(obj!=null){
//            capture.setFocusLast();
//            capture.clear();
//            this.word.pasteString(obj.toString());
//        }
//    }

//    public void onMan(){
//        this.word.setSexControl(1);
//    }
//
//    public void onWoman() {
//        this.word.setSexControl(2);
//    }
//
//    public void onAlls() {
//        this.word.setSexControl(0);
//    }


    /**
     * �õ��ֵ���Ϣ
     * @param groupId String
     * @param id String
     * @return String
     */
    public String getDictionary(String groupId, String id) {
        String result = "";
        TParm parm = new TParm(this.getDBTool().select(
            "SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='" + groupId +
            "' AND ID='" + id + "'"));
        result = parm.getValue("CHN_DESC", 0);
        return result;
    }

    /**
     * �õ�����  db2
     * @param deptCode String
     * @return String
     */
    public String getDeptDesc(String deptCode) {
        //this.messageBox("===deptCode==="+deptCode);
        TParm parm = new TParm(this.getDBTool().select(DB2_POOL_NAME,
            "SELECT DEPT_DESC FROM SYS_DEPT WHERE DEPT_CODE='" + deptCode +
            "'"));
        return parm.getValue("DEPT_DESC", 0);
    }

    /**
     * �õ��û���
     * @param userID String
     * @return String
     */
    public String getOperatorName(String userID) {
        TParm parm = new TParm(this.getDBTool().select(DB2_POOL_NAME,
            "SELECT USER_NAME FROM SYS_OPERATOR WHERE USER_ID='" + userID + "'"));
        return parm.getValue("USER_NAME", 0);
    }

    /**
     * ����
     */
    public boolean onSave() {
        return onsaveEmr(true);
    }

    /**
     * ����EMR�ļ�
     * @param parm TParm
     */
    public boolean saveEmrFile(TParm parm) {
        //System.out.println("saveEmrFile type+++++"+this.getOnlyEditType());
        boolean falg = true;
        //Operator.getID()
        parm.setData("OPT_USER", this.getOperatorUserID());
        parm.setData("OPT_DATE", this.getDBTool().getDBTime());
        //Operator.getIP()
        parm.setData("OPT_TERM", this.getOperatorUserIP());
        parm.setData("REPORT_FLG", "N");
        parm.setData("CURRENT_USER", this.getOperatorUserID());
        if (this.getOnlyEditType().equals("NEW")) {
            parm.setData("CREATOR_USER", this.getOperatorUserID());
            TParm result = TIOM_AppServer.executeAction(actionName,
                "saveNewEmrFile", parm);
            if (result.getErrCode() < 0) {
                falg = false;
            }
            return falg;
        }
        if (this.getOnlyEditType().equals("ONLYONE")) {
            TParm result = TIOM_AppServer.executeAction(actionName,
                "writeEmrFile", parm);
            if (result.getErrCode() < 0) {
                falg = false;
            }
        }
        return falg;
    }

    /**
     * �õ��ļ�������·��
     * @param rootPath String
     * @param fileServerPath String
     * @return String
     */
    public TParm getFileServerEmrName() {
        TParm emrParm = new TParm();
        String emrName = "";
        TParm childParm = this.getEmrChildParm();
        String templetName = childParm.getValue("EMT_FILENAME");
        TParm action = new TParm(this.getDBTool().select(
            "SELECT NVL(MAX(FILE_SEQ)+1,1) AS MAXFILENO FROM EMR_FILE_INDEX WHERE CASE_NO='" +
            this.getCaseNo() + "'"));
        int index = action.getInt("MAXFILENO", 0);
        emrName = this.getCaseNo() + "_" + templetName + "_" + index;
        String dateStr = StringTool.getString(SystemTool.getInstance().getDate(),
                                              "yyyy/MM/dd HH:mm:ss");
        emrParm.setData("FILE_SEQ", index);
        emrParm.setData("FILE_NAME", emrName);
        emrParm.setData("CLASS_CODE", childParm.getData("CLASS_CODE"));
        emrParm.setData("SUBCLASS_CODE", childParm.getData("SUBCLASS_CODE"));
        emrParm.setData("CASE_NO", this.getCaseNo());
        emrParm.setData("MR_NO", this.getMrNo());
        emrParm.setData("IPD_NO", this.getIpdNo());
        emrParm.setData("FILE_PATH",
                        "JHW" + "\\" + this.getAdmYear() + "\\" +
                        this.getAdmMouth() +
                        "\\" + this.getMrNo());
        emrParm.setData("DESIGN_NAME", templetName + "(" + dateStr + ")");
        emrParm.setData("DISPOSAC_FLG", "N");
        emrParm.setData("TYPEEMR", childParm.getValue("TYPEEMR"));
        return emrParm;
    }

    /**
     * ����Ƿ񱣴�
     */
    public boolean checkSave() {
        if (this.getWord().getFileOpenName() != null
            && (ruleType.equals("2") || ruleType.equals("3"))
            && this.getTMenuItem("save").isEnabled()) {
            // P0009��ʾ��Ϣ
            if (this
                .messageBox("��ʾ��Ϣ Tips", "�Ƿ񱣴�"
                            + this.getWord().getFileOpenName() +
                            "�ļ�?\n To save"
                            + this.getWord().getFileOpenName() + "File?",
                            this.YES_NO_OPTION) == 0) {
                // �������ݿ�����
                if (onSave()) {
                    this.loadTree();
                    return true;
                }
                else {
                    // ����ʧ��E0001
                    this.messageBox("����ʧ�ܣ�");
                    return false;
                }
            }
            else {

            }
        }
        return true;
    }

    public static void main(String args[]) {
        /*JFrame f = new JFrame();
                 f.getRootPane().add(new EMRPad30());
                 f.getRootPane().setLayout(new BorderLayout());
                 f.setVisible(true);*/
        JavaHisDebug.TBuilder();
    }

    /**
     * �������ݿ��������
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }

    /**
     * �õ�ϵͳ���
     * @return String
     */
    public String getSystemType() {
        return systemType;
    }

    public String getIpdNo() {
        return ipdNo;
    }

    public String getMrNo() {
        return mrNo;
    }

    public String getPatName() {
        return patName;
    }

    public String getAdmMouth() {
        return admMouth;
    }

    public String getAdmYear() {
        return admYear;
    }

    public String getCaseNo() {
        return caseNo;
    }

    public TParm getEmrChildParm() {
        return emrChildParm;
    }

    public String getDirectorDrCode() {
        return directorDrCode;
    }

    public String getAttendDrCode() {
        return attendDrCode;
    }

    public String getVsDrCode() {
        return vsDrCode;
    }


    public String getAdmType() {
        return admType;
    }

    public String getDeptCode() {
        return deptCode;
    }

    public String getOnlyEditType() {
        return onlyEditType;
    }

    public Timestamp getAdmDate() {
        return admDate;
    }

    public String getType() {
        return type;
    }

    public String getStationCode() {
        return stationCode;
    }

    /**
     * �õ�ϵͳ���
     * @param systemType String
     */
    public void setSystemType(String systemType) {
        this.systemType = systemType;
    }

    public void setIpdNo(String ipdNo) {
        this.ipdNo = ipdNo;
    }

    public void setMrNo(String mrNo) {
        this.mrNo = mrNo;
    }

    public void setPatName(String patName) {
        this.patName = patName;
    }

    public void setAdmMouth(String admMouth) {
        this.admMouth = admMouth;
    }

    public void setAdmYear(String admYear) {
        this.admYear = admYear;
    }

    public void setCaseNo(String caseNo) {
        this.caseNo = caseNo;
    }

    public void setEmrChildParm(TParm emrChildParm) {
        this.emrChildParm = emrChildParm;
    }

    public void setDirectorDrCode(String directorDrCode) {
        this.directorDrCode = directorDrCode;
    }

    public void setAttendDrCode(String attendDrCode) {
        this.attendDrCode = attendDrCode;
    }

    public void setVsDrCode(String vsDrCode) {
        this.vsDrCode = vsDrCode;
    }

    public void setAdmType(String admType) {
        this.admType = admType;
    }

    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }

    public void setOnlyEditType(String onlyEditType) {
        this.onlyEditType = onlyEditType;
    }

    public void setAdmDate(Timestamp admDate) {
        this.admDate = admDate;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setStationCode(String stationCode) {
        this.stationCode = stationCode;
    }

    /**
     *
     * @param regionCode String
     */
    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    public String getRegionCode() {
        return this.regionCode;
    }

    /**
     *
     * @param operatorUserID String
     */
    public void setOperatorUserID(String operatorUserID) {
        this.operatorUserID = operatorUserID;
    }

    public String getOperatorUserID() {
        return this.operatorUserID;
    }


    public void setOperatorUserIP(String operatorUserIP) {
        this.operatorUserIP = operatorUserIP;
    }

    public String getOperatorUserIP() {
        return this.operatorUserIP;
    }


    /**
     * ������
     */
    public void onInsertTable() {
        if (this.getWord().getFileOpenName() != null) {
            this.getWord().insertBaseTableDialog();
        }
        else {
            //��ѡ����
            this.messageBox("��ѡ����!");
        }
    }

    /**
     * ɾ�����
     */
    public void onDelTable() {
        if (this.getWord().getFileOpenName() != null) {
            this.getWord().deleteTable();
        }
        else {
            //��ѡ����E0099
            this.messageBox("��ѡ����!");
        }
    }

    /**
     * ��������
     */
    public void onInsertTableRow() {
        if (this.getWord().getFileOpenName() != null) {
            this.getWord().insertTR();
        }
        else {
            //��ѡ����E0099
            this.messageBox("��ѡ����!");
        }
    }

    /**
     * ׷�ӱ����
     */
    public void onAddTableRow() {
        if (this.getWord().getFileOpenName() != null) {
            this.getWord().appendTR();
        }
        else {
            //��ѡ����E0099E0099
            this.messageBox("��ѡ����!");
        }
    }

    /**
     * ɾ�������
     */
    public void onDelTableRow() {
        if (this.getWord().getFileOpenName() != null) {
            this.getWord().deleteTR();
        }
        else {
            //��ѡ����E0099
            this.messageBox("��ѡ����!");
        }
    }

    /**
     * ��ӡ����
     */
    public void onPrintSetup() {
        if (this.getWord().getFileOpenName() != null) {
            this.getWord().printSetup();
        }
        else {
            //��ѡ���� E0099
            this.messageBox("��ѡ����!");
        }
    }

    /**
     * ��ӡ
     */
    public void onPrint() {
        if (this.getWord().getFileOpenName() != null) {
            this.getWord().onPreviewWord();
            this.getWord().print();
        }
        else {
            //��ѡ���� E0099
            this.messageBox("��ѡ����!");
        }
    }

    /**
     * �����ӡ
     */
    public void onPrintClear() {
        if (this.getWord().getFileOpenName() != null) {
            this.getWord().onPreviewWord();
        }
        else {
            //��ѡ����E0099
            this.messageBox("��ѡ����!");
        }
    }

    /**
     * ����
     */
    public void onCutMenu() {
        if (this.getWord().getFileOpenName() != null) {
            this.getWord().onCut();
        }
        else {
            //��ѡ���� E0099E0099
            this.messageBox("��ѡ����!");
        }
    }

    /**
     * ����
     */
    public void onCopyMenu() {
        if (this.getWord().getFileOpenName() != null) {
            this.getWord().onCopy();
        }
        else {
            //��ѡ����E0099
            this.messageBox("��ѡ����!");
        }
    }

    /**
     * ճ��
     */
    public void onPasteMenu() {
        if (this.getWord().getFileOpenName() != null) {
            this.getWord().onPaste();
        }
        else {
            //��ѡ���� E0099
            this.messageBox("��ѡ����!");
        }
    }

    /**
     * ɾ��
     */
    public void onDeleteMenu() {
        if (this.getWord().getFileOpenName() != null) {
            this.getWord().onDelete();
        }
        else {
            //��ѡ���� E0099
            this.messageBox("��ѡ����!");
        }
    }

    /**
     * ��ҳ��ӡ
     */
    public void onPrintIndex() {
        if (this.getWord().getFileOpenName() != null) {
            this.getWord().onPreviewWord();
            this.getWord().getPM().getPageManager().printDialog();
        }
        else {
            //��ѡ����E0099
            this.messageBox("��ѡ����!");
        }
    }

    /**
     * ��ˢ��
     */
    public void onHongR() {
        if (this.getWord().getFileOpenName() != null) {
            this.setMicroField(true);
        }
        else {
            //��ѡ����
            //this.messageBox("E0099");
            this.messageBox("��ѡ����!");
        }
    }

    /**
     * ��ӡ
     */
    public void onPrintXDDialog() {
        if (this.getWord().getFileOpenName() != null) {
            this.getWord().onPreviewWord();
            this.getWord().printXDDialog();
        }
        else {
            //��ѡ����
            //this.messageBox("E0099");
            this.messageBox("��ѡ����!");
        }
    }

    /**
     * ��ʾ�кſ���
     */
    public void onShowRowIDSwitch() {
        if (this.getWord().getFileOpenName() != null) {
            this.getWord().setShowRowID(!word.isShowRowID());
            this.getWord().update();
        }
        else {
            //��ѡ����
            //this.messageBox("E0099");
            this.messageBox("��ѡ����!");
        }
    }

    /**
     * �������
     */
    public void onAddDLText() {
        if (this.getWord().getFileOpenName() != null) {
            this.getWord().onParagraphDialog();
        }
        else {
            this.messageBox("��ѡ��ģ�棡");
        }
    }

    public void onChangeLanguage(String language) {

    }

    /****************************** �Ž��� �ǿ���֤ ******************************/
    /**
     * ����ǿ���֤
     * @param str String
     * @return boolean
     */
    public boolean checkInputObject(Object obj) {
        if (obj == null) {
            return false;
        }
        String str = String.valueOf(obj);
        if (str == null) {
            return false;
        }
        else if ("".equals(str.trim())) {
            return false;
        }
        else {
            return true;
        }
    }

    /****************************** �Ž��� �Ƿ������޸ġ��Ƿ������ӡ���� ******************************/
    /**
     * ��ѯ���Ӳ����ļ��浵
     * @param caseNo String
     * @param fileSeq String
     * @return TParm
     */
    public TParm getEmrFileIndex(String caseNo, String fileSeq) {
        String sql =
            " SELECT EFI.* FROM EMR_FILE_INDEX EFI WHERE EFI.CASE_NO = '"
            + caseNo + "' AND FILE_SEQ = '" + fileSeq + "'";
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        return result;
    }

    /**
     * ���������ӡ��ʶ�������޸ı�ʶ
     * @param isVisible
     * @param canPrintFlg
     */
    public void setCanPrintFlgAndModifyFlg(String caseNo, String fileSeq,
                                           boolean isVisible) {
        TCheckBox CANPRINT_FLG = (TCheckBox)this.getComponent("CANPRINT_FLG");
        TCheckBox MODIFY_FLG = (TCheckBox)this.getComponent("MODIFY_FLG");
        TMenuItem PrintShow = (TMenuItem)this.getComponent("PrintShow");
        TMenuItem PrintSetup = (TMenuItem)this.getComponent("PrintSetup");
        TMenuItem PrintClear = (TMenuItem)this.getComponent("PrintClear");
        TMenuItem printIndex = (TMenuItem)this.getComponent("printIndex");
        TMenuItem LinkPrint = (TMenuItem)this.getComponent("LinkPrint");
        TMenuItem save = (TMenuItem)this.getComponent("save");
        // �Ƿ���ʾ
        if (isVisible) {
            CANPRINT_FLG.setVisible(true);
            MODIFY_FLG.setVisible(true);
            // ��ѯ���Ӳ����ļ��浵
            TParm result = getEmrFileIndex(caseNo, fileSeq);
            // ���������ӡ��ʶ
            CANPRINT_FLG.setValue(result.getValue("CANPRINT_FLG", 0));
            // ���������޸ı�ʶ
            MODIFY_FLG.setValue(result.getValue("MODIFY_FLG", 0));
            // ��ǰ�û��Ƿ�Ϊ���༭�� Operator.getID()
            if (this.getOperatorUserID().equals(result.getValue("CURRENT_USER",
                0))) {
                CANPRINT_FLG.setEnabled(true);
                MODIFY_FLG.setEnabled(true);
                // �����ӡ
                PrintShow.setEnabled(true);
                PrintSetup.setEnabled(true);
                PrintClear.setEnabled(true);
                printIndex.setEnabled(true);
                LinkPrint.setEnabled(true);
                // �����޸�
                save.setEnabled(true);
            }
            else {
                CANPRINT_FLG.setEnabled(false);
                MODIFY_FLG.setEnabled(false);
                // �����Ƿ������ӡ
                if ("Y".equals(result.getValue("CANPRINT_FLG", 0))) {
                    PrintShow.setEnabled(true);
                    PrintSetup.setEnabled(true);
                    PrintClear.setEnabled(true);
                    printIndex.setEnabled(true);
                    LinkPrint.setEnabled(true);
                }
                else {
                    PrintShow.setEnabled(false);
                    PrintSetup.setEnabled(false);
                    PrintClear.setEnabled(false);
                    printIndex.setEnabled(false);
                    LinkPrint.setEnabled(false);
                }
                // �����Ƿ������޸�
                if ("Y".equals(result.getValue("MODIFY_FLG", 0))) {
                    save.setEnabled(true);
                }
                else {
                    save.setEnabled(false);
                }
            }
        }
        else {
            CANPRINT_FLG.setVisible(false);
            MODIFY_FLG.setVisible(false);
            CANPRINT_FLG.setEnabled(false);
            MODIFY_FLG.setEnabled(false);
            PrintShow.setEnabled(true);
            PrintSetup.setEnabled(true);
            PrintClear.setEnabled(true);
            printIndex.setEnabled(true);
            LinkPrint.setEnabled(true);
            save.setEnabled(true);
        }
    }

    /****************************** �Ž��� ��ʱ�Զ����� ******************************/
    /** �Զ����涨ʱ�� */
    private Timer timer;
    /** �Զ��������� */
    private TimerTask task;
    /** ��ʱ��ʱ���� */
    private long period = 10 * 1000;
    /** ��ʱ���ӳ�ʱ�� */
    private long delay = 10 * 1000;
    /**
     * �Զ����涨ʱ����ʼ��
     */
   /** public void initTimer() {
        this.timer = new Timer();
        setPeriod();
        this.task = new TimerTask() {
            public void run() {
                autoSave();
            }
        };
    }**/

    /**
     * ��ȡ��ʱ��ʱ����
     */
    /**public void setPeriod() {
        String sql = " SELECT AUTOSAVE_TIMES FROM ODI_SYSPARM ";
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getCount("AUTOSAVE_TIMES") > 0 &&
            checkInputObject(result.getLong("AUTOSAVE_TIMES", 0))) {
            this.period = result.getLong("AUTOSAVE_TIMES", 0) * 1000;
            this.delay = this.period;
        }
    }**/
    
    /**
     * �Զ����涨ʱ����ʼ��
     */
    /**public void schedule() {
        this.task = new TimerTask() {
            public void run() {
                autoSave();
            }
        };
        this.timer.schedule(this.task, this.delay, this.period);
    }**/

    /**
     * �Զ���������ȡ��
     * @param tmpFilePath String
     * @param tmpFileName String
     */
    public void cancel() {
        this.task.cancel();
    }

    /**
     * ���ڹر�ʱȡ��Timer��ʱ��
     * @return boolean
     */
    public boolean onClosing() {
        //this.messageBox("come in!!!");
        //System.out.println("=======come in onClosing============");
        if (this.messageBox("ѯ��", "�Ƿ�رգ�", 2) != 0) {
            return false;
        }
        //ɾ��ǰ����ʱ�ļ�
        /**delFile(this.getEmrChildParm().getValue("FILE_PATH"),
                "TMP_" + this.getEmrChildParm().getValue("FILE_NAME"));
                 //�˳��Զ����涨ʱ��(����̩��EMR����ʱȥ���Զ����湦��)
                 this.cancel();**/
        return true;
    }

    /**
     * ɾ���ļ�
     * @param tmpFilePath
     * @param tmpFileName
     */
    public void delFile(String filePath, String fileName) {
        while (checkTMP(filePath, fileName)) {
            delFileTempletFile(filePath, fileName);
        }
    }

    /**
     * ��ʱ�ļ��Ƿ������֤
     * @param filePath
     * @param fileName
     * @return
     */
    public boolean checkTMP(String filePath, String fileName) {
        //Ŀ¼���һ����Ŀ¼FILESERVER
        String rootName = TIOM_FileServer.getRoot();
        //ģ��·��������
        String templetPathSer = TIOM_FileServer.getPath("EmrData");
        //����·��
        String realPath = rootName + templetPathSer + filePath + "\\" +
            fileName + ".jhw";
        //�õ�SocketͨѶ����
        TSocket socket = TIOM_FileServer.getSocket();
        byte b[] = TIOM_FileServer.readFile(socket, realPath);
        if (b == null || b.length == 0) {
            return false;
        }
        else {
            return true;
        }
    }

    /**
     * �ļ�����
     * @param filePath
     * @param fileName
     * @return
     */
    public boolean copyFile(String filePath, String fileName) {
        //Ŀ¼���һ����Ŀ¼FILESERVER
        String rootName = TIOM_FileServer.getRoot();
        //ģ��·��������
        String templetPathSer = TIOM_FileServer.getPath("EmrData");
        //����·��
        String realPath = rootName + templetPathSer + filePath + "\\TMP_" +
            fileName + ".jhw";
        String destPath = rootName + templetPathSer + filePath + "\\" +
            fileName + ".jhw";
        //�õ�SocketͨѶ����
        TSocket socket = TIOM_FileServer.getSocket();
        byte data[] = TIOM_FileServer.readFile(socket, realPath);
        return TIOM_FileServer.writeFile(socket, destPath, data);
    }

    /**
     * �Զ�����
     */
     /** public void autoSave() {
        if (ruleType.equals("1")) {
            //��û�б༭Ȩ��
            return;
        }
        if (this.getWord().getFileOpenName() == null) {
            //��ѡ��һ����Ҫ�༭�Ĳ���
            return;
        }
        if (!this.getWord().canEdit()) {
            return;
        }
        //��浽�û��ļ�
        TParm asSaveParm = new TParm(getEmrChildParm().getData());
        //����
        String dateStr = StringTool.getString(SystemTool.getInstance().
                                              getDate(),
                                              "yyyy��MM��dd�� HHʱmm��ss��");
        if ("ONLYONE".equals(getOnlyEditType())) {
            //������ʾ������
            this.getWord().setMessageBoxSwitch(false);
            //����޸���  Operator.getID()
            this.getWord().setFileLastEditUser(this.getOperatorUserID());
            //����޸�����
            this.getWord().setFileLastEditDate(dateStr);
            //����޸�IP  Operator.getIP()
            this.getWord().setFileLastEditIP(this.getOperatorUserIP());
            //����
            if (!this.getWord().onSaveAs(asSaveParm.getValue("FILE_PATH"),
                                         "TMP_" +
                                         asSaveParm.getValue("FILE_NAME"), 3)) {
                //�ļ��������쳣
                //������ʾ��Ϊ����ʾ
                this.getWord().setMessageBoxSwitch(true);
                //���ɱ༭
                this.getWord().setCanEdit(false);
                getTMenuItem("InsertTable").setEnabled(false);
                getTMenuItem("DelTable").setEnabled(false);
                getTMenuItem("InsertTableRow").setEnabled(false);
                getTMenuItem("AddTableRow").setEnabled(false);
                getTMenuItem("DelTableRow").setEnabled(false);
                getTMenuItem("InsertPY").setEnabled(false);
                getTMenuItem("InsertLCSJ").setEnabled(false);
                return;
            }
            //������ʾ��Ϊ����ʾ
            this.getWord().setMessageBoxSwitch(true);
            //���ɱ༭
            this.getWord().setCanEdit(false);
            getTMenuItem("InsertTable").setEnabled(false);
            getTMenuItem("DelTable").setEnabled(false);
            getTMenuItem("InsertTableRow").setEnabled(false);
            getTMenuItem("AddTableRow").setEnabled(false);
            getTMenuItem("DelTableRow").setEnabled(false);
            getTMenuItem("InsertPY").setEnabled(false);
            getTMenuItem("InsertLCSJ").setEnabled(false);
            //����ɹ�
            Object obj = getParameter();
            //( (TParm) obj).runListener("EMR_SAVE_LISTENER", new TParm());
            this.getWord().setCanEdit(true);
            return;
        }
    }**/

    /****************************** �Ž��� �������� ******************************/
    /**
     * ��������Ȩ�޿��Ƹ�Ҫ
     * 1��ruleType=1
     * ������������м�¼ֻ�ܲ鿴
     * 2��ruleType=2��systemType!=ODI
     * ������������Բ鿴���½����༭��ɾ�������¼���ǻ����¼ֻ�ܲ鿴
     * 3��ruleType=3��systemType=ODI��writeType=OIDR
     * ������������Բ鿴���½����༭��ɾ�����ﲡ�����ǻ��ﲡ��ֻ�ܲ鿴
     * 4��ruleType=2��systemType=ODI
     * ��������Ȩ�޿���
     */
    /**
     * ��֤�û��Ƿ��ύ�˲���
     */
    public boolean checkUserSubmit(TParm parm, String user) {
        String chkUser1 = parm.getValue("CHK_USER1");
        String chkUser2 = parm.getValue("CHK_USER2");
        String chkUser3 = parm.getValue("CHK_USER3");
        if (user.equals(chkUser1)) {
            return true;
        }
        if (user.equals(chkUser2)) {
            return true;
        }
        if (user.equals(chkUser3)) {
            return true;
        }
        return false;
    }

    /**
     * ��ȡ�û���ְ�����
     * 1���Ǿ���ҽʦ		������ҽʦ	������ҽʦ
     * 2������ҽʦ		������ҽʦ	������ҽʦ
     * 3���Ǿ���ҽʦ		����ҽʦ		������ҽʦ
     * 4���Ǿ���ҽʦ		������ҽʦ	����ҽʦ
     * 5������ҽʦ		����ҽʦ		������ҽʦ
     * 6������ҽʦ		������ҽʦ	����ҽʦ
     * 7���Ǿ���ҽʦ		����ҽʦ		����ҽʦ
     * 8������ҽʦ		����ҽʦ		����ҽʦ
     */
    public int getUserDuty(String user) {
        if (!user.equals(this.getVsDrCode())
            && !user.equals(this.getAttendDrCode())
            && !user.equals(this.getDirectorDrCode())) {
            return 1;
        }
        if (user.equals(this.getVsDrCode())
            && !user.equals(this.getAttendDrCode())
            && !user.equals(this.getDirectorDrCode())) {
            return 2;
        }
        if (!user.equals(this.getVsDrCode())
            && user.equals(this.getAttendDrCode())
            && !user.equals(this.getDirectorDrCode())) {
            return 3;
        }
        if (!user.equals(this.getVsDrCode())
            && !user.equals(this.getAttendDrCode())
            && user.equals(this.getDirectorDrCode())) {
            return 4;
        }
        if (user.equals(this.getVsDrCode())
            && user.equals(this.getAttendDrCode())
            && !user.equals(this.getDirectorDrCode())) {
            return 5;
        }
        if (user.equals(this.getVsDrCode())
            && !user.equals(this.getAttendDrCode())
            && user.equals(this.getDirectorDrCode())) {
            return 6;
        }
        if (!user.equals(this.getVsDrCode())
            && user.equals(this.getAttendDrCode())
            && user.equals(this.getDirectorDrCode())) {
            return 7;
        }
        if (user.equals(this.getVsDrCode())
            && user.equals(this.getAttendDrCode())
            && user.equals(this.getDirectorDrCode())) {
            return 8;
        }
        return 0;
    }

    /**
     * ��װ��������
     * @param parm ��������
     * @param optType �������� 1:�༭���� 2:�ύ 3:ȡ���ύ(��ǰ�û����ύ) 4:ȡ���ύ(��ǰ�û�δ�ύ)
     */
    public void setOptDataParm(TParm parm, int optType) {
        TParm data = this.getEmrChildParm();
        //Operator.getID()
        int operDuty = this.getUserDuty(this.getOperatorUserID());
        //����ʱ
        if (optType == 1) {
            //������CANPRINT_FLG��MODIFY_FLG
            TCheckBox CANPRINT_FLG = (TCheckBox)this.getComponent(
                "CANPRINT_FLG");
            TCheckBox MODIFY_FLG = (TCheckBox)this.getComponent("MODIFY_FLG");
            parm.setData("CANPRINT_FLG", CANPRINT_FLG.getValue());
            parm.setData("MODIFY_FLG", MODIFY_FLG.getValue());
        }
        //�ύʱ
        if (optType == 2) {
            //�����ӡ�������޸�
            parm.setData("CANPRINT_FLG", "Y");
            parm.setData("MODIFY_FLG", "Y");
            //CURRENT_USERΪ�ϼ�ҽʦ����ǰ�û�Ϊ����ҽʦʱΪ�㣩
            if (operDuty == 1) {
            }
            if (operDuty == 2) {
                parm.setData("CURRENT_USER", this.getAttendDrCode());
            }
            if (operDuty == 3) {
                parm.setData("CURRENT_USER", this.getDirectorDrCode());
            }
            if (operDuty == 4) {
                parm.setData("CURRENT_USER", "0");
            }
            if (operDuty == 5) {
                parm.setData("CURRENT_USER", this.getDirectorDrCode());
            }
            if (operDuty == 6) {
                parm.setData("CURRENT_USER", "0");
            }
            if (operDuty == 7) {
                parm.setData("CURRENT_USER", "0");
            }
            if (operDuty == 8) {
                parm.setData("CURRENT_USER", "0");
            }
            //��д���������˺�ʱ��  Operator.getID()
            if (this.getOperatorUserID().equals(this.getVsDrCode())) {
                parm.setData("CHK_USER1", this.getOperatorUserID());
                parm.setData("CHK_DATE1", this.getDBTool().getDBTime());
            }
            if (this.getOperatorUserID().equals(this.getAttendDrCode())) {
                parm.setData("CHK_USER2", this.getOperatorUserID());
                parm.setData("CHK_DATE2", this.getDBTool().getDBTime());
            }
            if (this.getOperatorUserID().equals(this.getDirectorDrCode())) {
                parm.setData("CHK_USER3", this.getOperatorUserID());
                parm.setData("CHK_DATE3", this.getDBTool().getDBTime());
            }
            //��д�ύ�˺�ʱ��
            parm.setData("COMMIT_USER", this.getOperatorUserID());
            parm.setData("COMMIT_DATE", this.getDBTool().getDBTime());
        }
        //ȡ���ύ(��ǰ�û����ύ)
        if (optType == 3) {
            //�����ӡ�������޸�
            parm.setData("CANPRINT_FLG", "Y");
            parm.setData("MODIFY_FLG", "Y");
            //CURRENT_USERΪ��ǰ�û�����ǰ�û�Ϊ����ҽʦʱ��գ����ύ�˺�ʱ��Ϊ�¼�ҽʦ����ǰ�û�Ϊ����ҽʦʱ��գ�
            if (operDuty == 1) {
            }
            if (operDuty == 2) {
                parm.setData("CURRENT_USER", "");
                parm.setData("COMMIT_USER", "");
                parm.setData("COMMIT_DATE", "");
            }
            if (operDuty == 3) {
                parm.setData("CURRENT_USER", this.getAttendDrCode());
                parm.setData("COMMIT_USER", this.getVsDrCode());
                parm.setData("COMMIT_DATE", this.getDBTool().getDBTime());
            }
            if (operDuty == 4) {
                parm.setData("CURRENT_USER", this.getDirectorDrCode());
                parm.setData("COMMIT_USER", this.getAttendDrCode());
                parm.setData("COMMIT_DATE", this.getDBTool().getDBTime());
            }
            if (operDuty == 5) {
                parm.setData("CURRENT_USER", "");
                parm.setData("COMMIT_USER", "");
                parm.setData("COMMIT_DATE", "");
            }
            if (operDuty == 6) {
                parm.setData("CURRENT_USER", "");
                parm.setData("COMMIT_USER", "");
                parm.setData("COMMIT_DATE", "");
            }
            if (operDuty == 7) {
                parm.setData("CURRENT_USER", this.getAttendDrCode());
                parm.setData("COMMIT_USER", this.getVsDrCode());
                parm.setData("COMMIT_DATE", this.getDBTool().getDBTime());
            }
            if (operDuty == 8) {
                parm.setData("CURRENT_USER", "");
                parm.setData("COMMIT_USER", "");
                parm.setData("COMMIT_DATE", "");
            }
            //���������˺�ʱ�����
            if (this.getOperatorUserID().equals(this.getVsDrCode())) {
                parm.setData("CHK_USER1", "");
                parm.setData("CHK_DATE1", "");
            }
            if (this.getOperatorUserID().equals(this.getAttendDrCode())) {
                parm.setData("CHK_USER2", "");
                parm.setData("CHK_DATE2", "");
            }
            if (this.getOperatorUserID().equals(this.getDirectorDrCode())) {
                parm.setData("CHK_USER3", "");
                parm.setData("CHK_DATE3", "");
            }
        }
        //ȡ���ύ(��ǰ�û�δ�ύ)
        if (optType == 4) {
            //�����ӡ�������޸�
            parm.setData("CANPRINT_FLG", "Y");
            parm.setData("MODIFY_FLG", "Y");
            //CURRENT_USERΪ�¼�ҽʦ����ǰ�û�Ϊ����ҽʦ������ҽʦʱ��գ����ύ�˺�ʱ��Ϊ�¼�ҽʦ���¼�ҽʦ����ǰ�û�Ϊ����ҽʦ������ҽʦʱ��գ����¼������˺�ʱ�����
            if (operDuty == 1) {
            }
            if (operDuty == 2) {
                parm.setData("CURRENT_USER", "");
                parm.setData("COMMIT_USER", "");
                parm.setData("COMMIT_DATE", "");
                parm.setData("CHK_USER1", "");
                parm.setData("CHK_DATE1", "");
            }
            if (operDuty == 3) {
                parm.setData("CURRENT_USER", "");
                parm.setData("COMMIT_USER", "");
                parm.setData("COMMIT_DATE", "");
                parm.setData("CHK_USER1", "");
                parm.setData("CHK_DATE1", "");
            }
            if (operDuty == 4) {
                parm.setData("CURRENT_USER", this.getAttendDrCode());
                parm.setData("COMMIT_USER", this.getVsDrCode());
                parm.setData("COMMIT_DATE", this.getDBTool().getDBTime());
                parm.setData("CHK_USER2", "");
                parm.setData("CHK_DATE2", "");
            }
            if (operDuty == 5) {
                parm.setData("CURRENT_USER", "");
                parm.setData("COMMIT_USER", "");
                parm.setData("COMMIT_DATE", "");
                parm.setData("CHK_USER1", "");
                parm.setData("CHK_DATE1", "");
            }
            if (operDuty == 6) {
                parm.setData("CURRENT_USER", "");
                parm.setData("COMMIT_USER", "");
                parm.setData("COMMIT_DATE", "");
                parm.setData("CHK_USER1", "");
                parm.setData("CHK_DATE1", "");
            }
            if (operDuty == 7) {
                parm.setData("CURRENT_USER", "");
                parm.setData("COMMIT_USER", "");
                parm.setData("COMMIT_DATE", "");
                parm.setData("CHK_USER1", "");
                parm.setData("CHK_DATE1", "");
            }
            if (operDuty == 8) {
                parm.setData("CURRENT_USER", "");
                parm.setData("COMMIT_USER", "");
                parm.setData("COMMIT_DATE", "");
                parm.setData("CHK_USER1", "");
                parm.setData("CHK_DATE1", "");
            }
        }

        //һ��������
        if (!parm.existData("CHK_USER1")) {
            parm.setData("CHK_USER1", data.getValue("CHK_USER1"));
        }
        //һ������ʱ��
        if (!parm.existData("CHK_DATE1")) {
            parm.setData("CHK_DATE1", data.getValue("CHK_DATE1"));
        }
        //����������
        if (!parm.existData("CHK_USER2")) {
            parm.setData("CHK_USER2", data.getValue("CHK_USER2"));
        }
        //��������ʱ��
        if (!parm.existData("CHK_DATE2")) {
            parm.setData("CHK_DATE2", data.getValue("CHK_DATE2"));
        }
        //����������
        if (!parm.existData("CHK_USER3")) {
            parm.setData("CHK_USER3", data.getValue("CHK_USER3"));
        }
        //��������ʱ��
        if (!parm.existData("CHK_DATE3")) {
            parm.setData("CHK_DATE3", data.getValue("CHK_DATE3"));
        }
        //�ύ��
        if (!parm.existData("COMMIT_USER")) {
            parm.setData("COMMIT_USER", data.getValue("COMMIT_USER"));
        }
        //�ύʱ��
        if (!parm.existData("COMMIT_DATE")) {
            parm.setData("COMMIT_DATE", data.getValue("COMMIT_DATE"));
        }
        //��Ժ�����
        if (!parm.existData("IN_EXAMINE_USER")) {
            parm.setData("IN_EXAMINE_USER", data.getValue("IN_EXAMINE_USER"));
        }
        //��Ժ���ʱ��
        if (!parm.existData("IN_EXAMINE_DATE")) {
            parm.setData("IN_EXAMINE_DATE", data.getValue("IN_EXAMINE_DATE"));
        }
        //��Ժ�����
        if (!parm.existData("DS_EXAMINE_USER")) {
            parm.setData("DS_EXAMINE_USER", data.getValue("DS_EXAMINE_USER"));
        }
        //��Ժ���ʱ��
        if (!parm.existData("DS_EXAMINE_DATE")) {
            parm.setData("DS_EXAMINE_DATE", data.getValue("DS_EXAMINE_DATE"));
        }
        //PDF������
        if (!parm.existData("PDF_CREATOR_USER")) {
            parm.setData("PDF_CREATOR_USER", data.getValue("PDF_CREATOR_USER"));
        }
        //PDF����ʱ��
        if (!parm.existData("PDF_CREATOR_DATE")) {
            parm.setData("PDF_CREATOR_DATE", data.getValue("PDF_CREATOR_DATE"));
        }
    }

    /**
     * 1.�����鿴Ȩ�޿��ƣ�
     */
    public boolean checkDrForOpen() {
        return true;
    }

    /**
     * 2.�����½�����Ȩ�޿��ƣ�
     * ruleType=1�����½�
     * ����ʱ����д�����˺�ʱ��
     */
    public boolean checkDrForNew() {
        if ("1".equals(this.ruleType)) {
            return false;
        }
        return true;
    }

    /**
     * 3.�����༭����Ȩ�޿��ƣ�
     * ruleType=1���ɱ༭
     * ruleType=2��systemType!=ODI
     * 		EMR_FILE_INDEX.SYSTEM_CODE!=INW���ɱ༭��������Ա༭
     * ruleType=3��systemType=ODI��writeType=OIDR
     * 		EmrFileIndex.OIDR_FLG!=Y���ɱ༭��������Ա༭
     * ruleType=2��systemType=ODI��
     * 		����������ҽʦ��ֵ��ҽʦ���ɱ༭
     * 		��CURRNET_USERΪ�գ�����ҽʦ���������������ҽʦ��ֵ��ҽʦ���ɱ༭
     * 		����
     * 			����ǰ�û�ΪCURRENT_USER���Ա༭
     * 			����
     * 				��MODIFY_FLG=N���ɱ༭
     * 				����
     * 					������ҽʦδ�ύ�������������ҽʦ��ֵ��ҽʦ���ɱ༭
     * 					����������ҽʦδ�ύ���������ҽʦ�ҷ�����ҽʦ���ɱ༭
     * 					����������ҽʦδ�ύ���������ҽʦ���ɱ༭
     * 					���򣬣�����ҽʦ���ύ��������������ҽʦ��ֵ��ҽʦ�����ɱ༭
     * ���򲻿ɱ༭
     * ����ʱ��������CANPRINT_FLG��MODIFY_FLG
     */
    public boolean checkDrForEdit() {
        TParm data = this.getEmrChildParm();
        if ("1".equals(this.ruleType)) {
            return false;
        }
        if ("2".equals(this.ruleType) && !"ODI".equals(this.getSystemType())) {
            if (!"INW".equals(data.getValue("SYSTEM_CODE"))) {
                return false;
            }
            else {
                return true;
            }
        }
        if ("3".equals(this.ruleType) && "ODI".equals(this.getSystemType())
            && "OIDR".equals(this.writeType)) {
            if (!"Y".equals(data.getValue("OIDR_FLG"))) {
                return false;
            }
            else {
                return true;
            }
        }
        if ("2".equals(this.ruleType) && "ODI".equals(this.getSystemType())) {
            // !this.isCheckUserDr() && !this.isDutyDrList()
            /**System.out.println("=======CURRENT_USER========" +
                               data.getValue("CURRENT_USER"));**/
            if (!this.isDR()) {
                return false;
            }
            if (!this.checkInputObject(data.getValue("CURRENT_USER"))) {
                return true;
            }
            else {
                //Operator.getID()
                if (this.getOperatorUserID().equals(data.getValue(
                    "CURRENT_USER"))) {
                    /**System.out.println(
                        "================ is current user=============");**/
                    return true;
                }
                else {
                    if ("N".equals(data.getValue("MODIFY_FLG"))) {
                        /**System.out.println(
                         "================ is current user1=============");**/
                        return false;
                    }
                    else {
                        if (isDR()) {
                            return true;
                        }
                        else {
                            return false;
                        }
                        //̩�������������ύ
                        /**if (!this.checkUserSubmit(data, this.getVsDrCode())) {
                            return false;
                                                 }
                         else if (!this.checkUserSubmit(data,
                            this.getAttendDrCode())) {
                            //Operator.getID()
                            if (!this.getOperatorUserID().equals(this.
                                getAttendDrCode())
                                &&
                                !this.getOperatorUserID().equals(this.
                                getDirectorDrCode())) {
                                return false;
                            }
                            else {
                                return true;
                            }
                                                 }
                         else if (!this.checkUserSubmit(data,
                            this.getDirectorDrCode())) {
                            if (!this.getOperatorUserID().equals(this.
                                getDirectorDrCode())) {
                                return false;
                            }
                            else {
                                return true;
                            }
                                                 }
                                                 else {
                            return false;
                                                 }**/
                    }
                }
            }
        }
        else {
            return false;
        }

    }

    /**
     * 4.�����ύȨ�޿��ƣ�
     * ruleType!=2��systemType!=ODI�����ύ
     * ����������ҽʦ�����ύ
     * ��CURRENT_USERΪ�գ�����ҽʦ������Ǿ���ҽʦ�����ύ
     * ����
     * 		����ǰ�û���CURRENT_USER�����ύ
     * 		��������ύ
     * �ύʱ��CURRENT_USERΪ�ϼ�ҽʦ����ǰ�û�Ϊ����ҽʦʱΪ�㣩�������ӡ�������޸ģ���д���������˺�ʱ�䣬��д�ύ�˺�ʱ��
     */
    public boolean checkDrForSubmit() {
        TParm data = this.getEmrChildParm();
        if (!"2".equals(this.ruleType) || !"ODI".equals(this.getSystemType())) {
            return false;
        }
        if (!this.isCheckUserDr()) {
            return false;
        }
        if (!this.checkInputObject(data.getValue("CURRENT_USER"))) {
            //Operator.getID()
            if (!this.getOperatorUserID().equals(this.getVsDrCode())) {
                return false;
            }
            else {
                return true;
            }
        }
        else {
            if (!this.getOperatorUserID().equals(data.getValue("CURRENT_USER"))) {
                return false;
            }
            else {
                return true;
            }
        }
    }

    /**
     * 5.����ȡ���ύȨ�޿��ƣ�
     * ruleType!=2��systemType!=ODI����ȡ���ύ
     * ����������ҽʦ����ȡ���ύ
     * ��CURRNET_USERΪ�գ�����ҽʦ�����򲻿�ȡ���ύ
     * ����
     * 		����ǰ�û�ΪCURRENT_USER�������ȡ���ύ
     * 		����
     * 			������ҽʦ���ύ���������ҽʦ����ȡ���ύ
     * 			����������ҽʦ���ύ���������ҽʦ�ҷ�����ҽʦ�����ύ
     * 			����������ҽʦ���ύ����Ǿ���ҽʦ�ҷ�����ҽʦ�����ύ
     * 			���򣬣�����ҽʦδ�ύ��������ȡ���ύ
     * 	��ǰ�û����ύ��CURRENT_USERΪ��ǰ�û�����ǰ�û�Ϊ����ҽʦʱ��գ��������ӡ�������޸ģ����������˺�ʱ����գ��ύ�˺�ʱ��Ϊ�¼�ҽʦ����ǰ�û�Ϊ����ҽʦʱ��գ�
     * 	��ǰ�û�δ�ύ��CURRENT_USERΪ�¼�ҽʦ����ǰ�û�Ϊ����ҽʦʱ��գ��������ӡ�������޸ģ��¼������˺�ʱ����գ��ύ�˺�ʱ��Ϊ�¼�ҽʦ���¼�ҽʦ����ǰ�û�Ϊ����ҽʦʱ��գ�
     */
    public boolean checkDrForSubmitCancel() {
        TParm data = this.getEmrChildParm();
        if (!"2".equals(this.ruleType) || !"ODI".equals(this.getSystemType())) {
            return false;
        }
        if (!this.isCheckUserDr()) {
            return false;
        }
        if (!this.checkInputObject(data.getValue("CURRENT_USER"))) {
            return false;
        }
        else {
            //Operator.getID()
            if (this.getOperatorUserID().equals(data.getValue("CURRENT_USER"))) {
                return true;
            }
            else {
                if (this.checkUserSubmit(data, this.getDirectorDrCode())) {
                    if (!this.getOperatorUserID().equals(this.getDirectorDrCode())) {
                        return false;
                    }
                    else {
                        return true;
                    }
                }
                else if (this.checkUserSubmit(data, this.getAttendDrCode())) {
                    if (!this.getOperatorUserID().equals(this.getAttendDrCode())
                        &&
                        !this.getOperatorUserID().equals(this.getDirectorDrCode())) {
                        return false;
                    }
                    else {
                        return true;
                    }
                }
                else if (this.checkUserSubmit(data, this.getVsDrCode())) {
                    if (!this.getOperatorUserID().equals(this.getVsDrCode())
                        &&
                        !this.getOperatorUserID().equals(this.getAttendDrCode())) {
                        return false;
                    }
                    else {
                        return true;
                    }
                }
                else {
                    return false;
                }
            }
        }
    }

    /**
     * 6.����ɾ�����Ƿ������ӡ���á��Ƿ������޸�����Ȩ�޿��ƣ�
     * ruleType=1����ɾ��
     * ��ǰ�û���CURRENT_USER����ɾ��
     */
    public boolean checkDrForDel() {
        TParm data = this.getEmrChildParm();
        //CURRENT_USER
        String currentUser = data.getValue("CURRENT_USER");
        //this.messageBox("ruleType==="+this.ruleType);
        if ("1".equals(this.ruleType)) {
            return false;
        }
        //this.messageBox("Operator.getID()"+Operator.getID());
        //this.messageBox("currentUser"+currentUser);
        //Operator.getID()
        if (!this.getOperatorUserID().equals(currentUser)) {
            return false;
        }
        return true;
    }

    /**
     * ��ø��ڵ��µ������ӽڵ�
     * @param parentClassCode String ���ڵ�
     * @param allChilds TParm  ���շ������нڵ�
     */

    private void getAllChildsByParent(String parentClassCode, TParm allChilds) {
        TParm childs = this.getChildsByParentNode(parentClassCode);
        if (childs != null && childs.getCount() > 0) {
            //��ʼֵ��
            if (allChilds.getCount() == -1) {
                allChilds.setCount(0);
            }
            allChilds.setCount(allChilds.getCount() + childs.getCount());

            for (int i = 0; i < childs.getCount(); i++) {
                allChilds.addData("CLASS_CODE", childs.getData("CLASS_CODE", i));
                allChilds.addData("CLASS_DESC", childs.getData("CLASS_DESC", i));
                allChilds.addData("CLASS_STYLE",
                                  childs.getData("CLASS_STYLE", i));
                allChilds.addData("LEAF_FLG", childs.getData("LEAF_FLG", i));
                allChilds.addData("PARENT_CLASS_CODE",
                                  childs.getData("PARENT_CLASS_CODE", i));
                allChilds.addData("SEQ", childs.getData("SEQ", i));
                this.getAllChildsByParent( ( (String) childs.getData(
                    "CLASS_CODE", i)), allChilds);
            }
        }

    }


    /**
     * add by lx
     * ͨ�����ڵ�õ��ӽڵ�;
     * @param parentClassCode String
     * @return TParm
     */
    private TParm getChildsByParentNode(String parentClassCode) {
        TParm result = new TParm(this.getDBTool().select("SELECT CLASS_CODE,CLASS_DESC,CLASS_STYLE,LEAF_FLG,PARENT_CLASS_CODE,SEQ FROM EMR_CLASS WHERE PARENT_CLASS_CODE='" +
            parentClassCode + "' ORDER BY SEQ,CLASS_CODE"));
        return result;
    }


    /**
     * ����ͼƬ�༭��
     */
    public void onInsertImageEdit() {

        if (this.getWord().getFileOpenName() == null) {
            //��ѡ��һ����Ҫ�༭�Ĳ���E0111
            this.messageBox("��ѡ��һ����Ҫ�༭�Ĳ���!");
            return;
        }
        this.getWord().insertImage();

    }


    /**
     * ɾ��ͼƬ�༭��
     */
    public void onDeleteImageEdit() {
        if (this.getWord().getFileOpenName() == null) {
            //��ѡ��һ����Ҫ�༭�Ĳ���E0111
            this.messageBox("��ѡ��һ����Ҫ�༭�Ĳ���");
            return;
        }
        this.getWord().deleteImage();

    }


    /**
     * �����
     */
    public void onInsertGBlock() {
        if (this.getWord().getFileOpenName() == null) {
            //��ѡ��һ����Ҫ�༭�Ĳ���E0111
            this.messageBox("��ѡ��һ����Ҫ�༭�Ĳ���");
            return;
        }

        this.getWord().insertGBlock();

    }

    /**
     * ������
     */
    public void onInsertGLine() {
        if (this.getWord().getFileOpenName() == null) {
            //��ѡ��һ����Ҫ�༭�Ĳ���E0111
            this.messageBox("��ѡ��һ����Ҫ�༭�Ĳ���");
            return;
        }
        this.getWord().insertGLine();

    }

    /**
     * ������ߴ�
     * @param index int
     */
    public void onSizeBlockMenu(String index) {
        if (this.getWord().getFileOpenName() == null) {
            //��ѡ��һ����Ҫ�༭�Ĳ���E0111
            this.messageBox("��ѡ��һ����Ҫ�༭�Ĳ���");
            return;
        }
        this.getWord().onSizeBlockMenu(StringTool.getInt(index));
    }

    /**
     * ��ȡ���ұ�׼��code���粻���ڷ���hisϵͳcode;
     * @param HisAttr String  P:pattern�����ֵ�|D��dictionary�����ֵ�
     * @param hisTableName String  hisϵͳ����
     * @param hisCode String    hisϵͳ����
     * @return String  ��Ӧ�Ĺ��ұ�׼��code
     */
    private String getEMRCode(String HisAttr, String hisTableName,
                              String hisCode) {

        String sql = "SELECT EMR_CODE FROM EMR_CODESYSTEM_D";
        sql += " WHERE HIS_ATTR='" + HisAttr + "'";
        sql += " AND HIS_TABLE_NAME='" + hisTableName + "'";
        sql += " AND HIS_CODE='" + hisCode + "'";
        TParm emrCodeParm = new TParm(getDBTool().select(sql));
        int count = emrCodeParm.getCount();
        //�ж�Ӧ
        if (count > 0) {
            return emrCodeParm.getValue("EMR_CODE", 0);
        }

        return hisCode;
    }

    /**
     * ��������Ԫ��ֵ��
     * @param objParameter TParm
     */
    private void setHiddenElements(TParm objParameter) {
        for (int i = 0; i < this.getWord().getPageManager().getMVList().size();
             i++) {
            MV mv = this.getWord().getPageManager().getMVList().get(i);
            if (mv.getName().equals("UNVISITABLE_ATTR")) {
                for (int j = 0; j < mv.size(); j++) {
                    DIV div = mv.get(j);
                    if (div instanceof VText) {
                        //this.messageBox("�Ǵ�������ֵ:"+((VText)div).isTextT());
                        if ( ( (VText) div).isTextT()) {
                            //��������ֵ�����ֵ��
                            //objParameter.setData(div.getName(), "TEXT", hideElemMap.get(div.getName()));
                            objParameter.setData( ( (VText) div).getMicroName(),
                                                 "TEXT",
                                                 hideElemMap.get( ( (VText) div).
                                getMicroName()));

                        }
                        else {
                            //��ֵ;
                            objParameter.setData(div.getName(), "TEXT",
                                                 ( (VText) div).getText());

                        }

                    }
                }

            }

        }

    }

    /**
     * ͨ����������ץȡ��ֵ��
     * @param macroName String
     * @param value String
     */
    private boolean setCaptureValue(String macroName, String value) {
        boolean isSetValue = false;
        TList components = this.getWord().getPageManager().getComponentList();
        int size = components.size();
        for (int i = 0; i < size; i++) {
            EPage ePage = (EPage) components.get(i);
            //EComponent
            //this.messageBox("EPanel size"+ePage.getComponentList().size());
            if (ePage != null) {
                for (int j = 0; j < ePage.getComponentList().size(); j++) {
                    EPanel ePanel = (EPanel) ePage.getComponentList().get(j);
                    for (int z = 0; z < ePanel.getBlockSize(); z++) {
                        IBlock block = (IBlock) ePanel.get(z);
                        //this.messageBox("type"+block.getObjectType());
                        //this.messageBox("value"+block.getBlockValue());
                        //9Ϊץȡ��;
                        if (block != null) {
                            if (block.getObjectType() ==
                                EComponent.CAPTURE_TYPE) {
                                EComponent com = block;
                                ECapture capture = (ECapture) com;

                                if (capture.getMicroName().equals(macroName)) {
                                    //this.messageBox("microName"+capture.getMicroName());
                                    //�ǿ�ʼ����ֵ;
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
     * �����ٴ�·��ֵ;
     */
    /**private void setCLPValue() {
        TParm parm = new TParm();
        parm.setData("CASE_NO", this.getCaseNo());
        parm.setData("REGION_CODE", Operator.getRegion());

        TParm result = CLPEMRTool.getInstance().getEMRManagedData(parm);
        //System.out.println("====�ٴ�·��result===="+result);
        String names[] = result.getNames();
        //this.messageBox("setCLPValue===="+names.length);

        for (int i = 0; i < names.length; i++) {
            //this.messageBox("name"+names[i]);
            //this.messageBox("value"+result.getValue(names[i]));
     EComponent com = this.getWord().getPageManager().findObject(names[i],
                EComponent.CHECK_BOX_CHOOSE_TYPE);
            ECheckBoxChoose checkbox = (ECheckBoxChoose) com;
            if (checkbox != null) {
                if (result.getValue(names[i]).equalsIgnoreCase("Y")) {
                    checkbox.setChecked(true);

                }
            }
        }
        this.getWord().update();
         }**/


    /**
     * ɾ����ʱ�ļ�Ŀ¼;
     * @param filepath String
     * @throws IOException
     */
    private void delDir(String filepath) throws IOException {
        File f = new File(filepath); // �����ļ�·��
        if (f.exists() && f.isDirectory()) { // �ж����ļ�����Ŀ¼
            // ��Ŀ¼��û���ļ���ֱ��ɾ��
            if (f.listFiles().length == 0) {
                f.delete();
            }
            else { // ��������ļ��Ž����飬���ж��Ƿ����¼�Ŀ¼
                File delFile[] = f.listFiles();
                int i = f.listFiles().length;
                for (int j = 0; j < i; j++) {
                    if (delFile[j].isDirectory()) {
                        // �ݹ����del������ȡ����Ŀ¼·��
                        delDir(delFile[j].getAbsolutePath());
                    }
                    // ɾ���ļ�dddd
                    delFile[j].delete();
                }
            }
        }
    }

    /**
     * ��ͼƬ�ļ�
     * @param url
     * @return
     */
    private BufferedImage readImage(String fileName) {
        File file = new File(fileName);
        BufferedImage image = null;
        if (file != null && file.isFile() && file.exists()) {
            try {
                image = ImageIO.read(file);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        return image;
    }

    /**
     * ɾ���̶��ı�
     */
    public void onDelFixText() {
        if (this.getWord().getFileOpenName() != null) {
            this.getWord().deleteFixed();
        }
        else {
            this.messageBox("��ѡ��ģ�棡");
        }
    }

    /**
     * ��Mettis����Vectorת��Tparm;
     * @param VParm Vector
     * @param obj TParm
     */
    public void vectorToTParm(Vector VParm, TParm obj) {
        obj.setData(PARAM_REGION_CODE, VParm.get(0));
        obj.setData(PARAM_OP_USERID, VParm.get(1));
        obj.setData(PARAM_DEPT_CODE, VParm.get(2));
        obj.setData(PARAM_CASE_NO, VParm.get(3));
        obj.setData(PARAM_SYSTEM_TYPE, VParm.get(4));
        obj.setData(PARAM_WRITE_TYPE, VParm.get(5));
        obj.setData(PARAM_RULETYPE, VParm.get(6));
        obj.setData(PARAM_ADM_TYPE, VParm.get(7));
        obj.setData(PARAM_STATION_CODE, VParm.get(8));
        obj.setData(PARAM_OP_USERIP, VParm.get(9));

        this.setRegionCode(obj.getValue(PARAM_REGION_CODE));
        this.setOperatorUserID(obj.getValue(PARAM_OP_USERID));
        this.setDeptCode(obj.getValue(PARAM_DEPT_CODE));
        this.setOperatorUserIP(PARAM_OP_USERIP);

        //SELECT a.MR_NO,REG_DATE,b.PAT_NAME,b.PAT_NAME2 FROM ADM_INP a LEFT JOIN SYS_PATINFO b ON a.MR_NO=b.MR_NO WHERE HOSP_AREA='HIS' AND CASE_NO='050205000003'
        String sql = "SELECT a.MR_NO,a.IPD_MR_NO,REG_DATE,b.PAT_NAME,b.PAT_NAME2,IN_DATE FROM ADM_INP a LEFT JOIN SYS_PATINFO b ON a.MR_NO=b.MR_NO";
        sql += " WHERE HOSP_AREA='" + obj.getValue(PARAM_REGION_CODE) +
            "' AND CASE_NO='" + obj.getValue(PARAM_CASE_NO) + "'";
        TParm parm = new TParm(this.getDBTool().select(DB2_POOL_NAME, sql));

        obj.setData(PARAM_MR_NO, parm.getValue("MR_NO", 0));

        System.out.println("======MR_NO====" + parm.getValue("MR_NO", 0));
        System.out.println("======CASE_NO====" + obj.getValue(PARAM_CASE_NO));
        /**System.out.println("========���IN_DATE============" +
                           parm.getValue("IN_DATE", 0));**/

        obj.setData(PARAM_ADM_DATE,
                    StringTool.getTimestamp(parm.getValue("IN_DATE", 0),
                                            "yyyy-MM-dd HH:mm:ss"));

        obj.setData(PARAM_IPD_NO, parm.getValue("IPD_MR_NO", 0));
        obj.setData(PARAM_PAT_NAME, parm.getValue("PAT_NAME", 0));

        //obj.addListener("EMR_LISTENER", this, "emrListener");
        //obj.addListener("EMR_SAVE_LISTENER", this, "emrSaveListener");
        this.setParmObj(obj);
        //m_obj = obj;
        //this.setParameter(obj);
    }


    /**
     * ����ͼƬ
     */
    public void onInsertPictureObject() {
        if (this.getWord().getFileOpenName() != null) {
            this.getWord().insertPicture();
            //word����ͼƬ
            this.setContainPic(true);
        }
        else {
            this.messageBox("��ѡ��ģ�棡");
        }
    }

    /**
     * ���뵱ǰʱ��
     */
    public void onInsertCurrentTime() {
        //��ȡʱ��
        Timestamp sysDate = StringTool.getTimestamp(new Date());
        String strSysDate = StringTool.getString(sysDate, "yyyy/MM/dd HH:mm:ss");
        //���㴦����ʱ��;
        //EComponent e = word.getFocusManager().getFocus();
        this.getWord().pasteString(strSysDate);
    }

    /**
     * ճ��ͼƬ
     */
    public void onPastePicture() {
        ClipboardTool tool = new ClipboardTool();
        try {
            Image img = tool.getImageFromClipboard();
            // �ж�ͼƬ��ʽ�Ƿ���ȷ
            if (img == null || img.getWidth(null) == -1) {
                this.messageBox("���а���û��ͼƬ����,����ץȡ!");
                return;

            }
            this.getWord().onPaste();

        }
        catch (Exception ex) {
            this.messageBox("ճ��ͼƬʧ��!");
            return;
        }

        //ճ��ͼ��
        /**try {
            File f = new File(picTempPath);
            if (!f.exists()) {
                f.mkdirs();
            }
            String tempFileName = "temp" + new Date().getTime() + ".gif";
            ClipboardTool.run(picPath + tempFileName);
            EMacroroutine mactroroutine = null;
            try {
                mactroroutine = this.getWord().insertPicture(new Object[] {
                    "004.gif", "temp", 150, 150});

            }
            catch (Exception ex) {

            }

            //�����ϴ�����;
            mactroroutine.setName(tempFileName);
            mactroroutine.getModel().setWidth(TypeTool.getInt("150"));
            mactroroutine.getModel().setHeight(TypeTool.getInt(getValue(
                "150")));

            MV mv = mactroroutine.getModel().getMVList().get("ͼ��");
            if (mv == null) {
                return;
            }
            DIV div = mv.get("ͼƬ");
            if (! (div instanceof VPic)) {
                return;
            }
            VPic pic = (VPic) div;
            //
            pic.setPictureName(picPath + tempFileName);
            Icon bgIcon = (Icon) getImage(picPath + tempFileName);
            pic.setIcon(bgIcon);
            //word����ͼƬ
            this.setContainPic(true);
            setReturnValue("OK");

                 }
                 catch (Exception ex) {
            //ex.printStackTrace();
            this.messageBox(ex.getMessage());
            return;
                 }**/

    }

    /**
     *
     * @param fileName String
     * @return ImageIcon
     */
    private static ImageIcon getImage(String fileName) {
        ImageIcon icon = null;
        byte[] data = null;
        try {
            data = FileTool.getByte(fileName);
        }
        catch (IOException ex) {
        }
        if (data == null) {
            System.out.println("ERR:û���ҵ�ͼƬ" + fileName);

            return icon;
        }
        icon = new ImageIcon(data);
        return icon;
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
        //��ϵͳ���а����Ƿ�������ݣ�
        if (CopyOperator.getComList() == null ||
            CopyOperator.getComList().size() == 0) {
            this.messageBox("����ѡ���ղصĲ�����Ϣ��");
            return;
        }
        TParm inParm = new TParm();
        inParm.setData("MR_NO", this.getMrNo());
        inParm.setData("PAT_NAME", this.getPatName());
        inParm.setData("OP_TYPE", "SavePatInfo");
        this.openDialog("%ROOT%\\config\\emr\\EMRPatPhrase.x", inParm, true);
        /**TWindow window = (TWindow)this.openWindow(
            "%ROOT%\\config\\emr\\EMRPatPhrase.x", inParm, true);
                 window.setX(ImageTool.getScreenWidth() - window.getWidth());
                 window.setY(0);
                 window.setVisible(true);**/
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
        //inParm.addListener("onReturnContent", this, "onReturnContent");

        TWindow window = (TWindow)this.openWindow(
            "%ROOT%\\config\\emr\\EMRPatPhrase.x", inParm, true);
        window.setX(ImageTool.getScreenWidth() - window.getWidth());
        window.setY(0);
        window.setVisible(true);
    }

    public void setParmObj(TParm parmObj) {
        this.parmObj = parmObj;
    }

    public TParm getParmObj() {
        return this.parmObj;
    }

    /**
     *
     * @param word TWord
     */
    public void setWord(TWord word) {
        this.word = word;
    }

    public TWord getWord() {
        return this.word;
    }

    /**
     *
     * @param flag boolean
     */
    private void menuEnable(boolean flag) {
        this.getTMenuItem("InsertTable").setEnabled(flag);
        this.getTMenuItem("DelTable").setEnabled(flag);
        this.getTMenuItem("InsertTableRow").setEnabled(flag);
        this.getTMenuItem("AddTableRow").setEnabled(flag);
        this.getTMenuItem("DelTableRow").setEnabled(flag);
        this.getTMenuItem("InsertPY").setEnabled(flag);
        this.getTMenuItem("InsertLCSJ").setEnabled(flag);

        this.getTMenuItem("InsertPictureObject").setEnabled(flag);
        this.getTMenuItem("PastePictureObject").setEnabled(flag);
        this.getTMenuItem("InsertTemplatePY").setEnabled(flag);
        this.getTMenuItem("InsertPatInfo").setEnabled(flag);
        this.getTMenuItem("InsertCurrentTime").setEnabled(flag);
    }

    /**
     * �������EMR�ļ�ҽʦ
     * @return String �����ļ�ҽʦ��
     */
    private String findLockedEmrFileUser(TParm emrParm) {
        String caseNo = this.getCaseNo();
        String fileSeq = emrParm.getValue("FILE_SEQ");
        TParm result = getEmrFileIndex(caseNo, fileSeq);
        String currentUser = result.getValue("CURRENT_USER", 0);
        boolean isLocked = result.getBoolean("LOCKED_FLG", 0);
        //���������
        if (isLocked) {
            //�����߲��ǵ�ǰ�û�
            if (!this.getOperatorUserID().equals(currentUser)) {
                //�������������ı༭��;
                return this.getOperatorName(currentUser);

            }
            else {
                //�ǵ�ǰ�û�
                return "";
            }

        }
        //����û������
        else {
            //���µ�ǰ�û��������ļ�
            updateLocked(caseNo, fileSeq, true);
            //����"";
            return "";
        }
    }

    /**
     * ������״̬;
     * @param caseNo String
     * @param fileSeq String
     * @param isLocked boolean
     * @return boolean
     */
    public boolean updateLocked(String caseNo, String fileSeq, boolean isLocked) {
        String lockedFlg = "N";
        if (isLocked) {
            lockedFlg = "Y";
        }
        String sql =
            " UPDATE EMR_FILE_INDEX SET LOCKED_FLG='" + lockedFlg +
            "',CURRENT_USER='" + this.getOperatorUserID() +
            "' WHERE CASE_NO = '"
            + caseNo + "' AND FILE_SEQ = '" + fileSeq + "'";
        /**System.out.println(
         "updateLocked SqL========================================" + sql);**/

        this.getDBTool().update(sql);
        return false;
    }

    /**
     * ���没��
     * @param isShow boolean  �Ƿ���ʾ����ɹ�
     * @return boolean
     */
    private boolean onsaveEmr(boolean isShow) {
        if (this.getWord().getFileOpenName() == null) {
            //��ѡ��һ����Ҫ�༭�Ĳ���
            this.messageBox("��ѡ��һ����Ҫ�༭�Ĳ�����");
            //this.messageBox("E0111");
            return false;
        }
        //2�������½�����Ȩ�޿��ƣ�
        if ("NEW".equals(this.getOnlyEditType())) {
            if (!this.checkDrForNew()) {
                this.messageBox("��û�б༭Ȩ�ޣ�");
                //this.messageBox("E0102");
                return false;
            }
        }
        //3�������༭����Ȩ�޿��ƣ�
        if ("ONLYONE".equals(this.getOnlyEditType())) {
            if (!this.checkDrForEdit()) {
                this.messageBox("��û�б༭Ȩ�ޣ�");
                //this.messageBox("E0102");
                return false;
            }
        }
        // add by lx ����У��  2011-06-12 start
        TParm nullParm = GetWordValue.getInstance().checkValue(this.getWord());
        //������ֵ��
        //���Ҷ�Ӧ�����֣�������
        /**Vector nullDataGroup = (Vector) nullParm.getData("NULL_GROUP_NAME");
                 Vector nullDataCode = (Vector) nullParm.getData("NULL_NAME");
                 String strNull = "";
                 if (nullDataGroup != null && nullDataGroup.size() > 0) {
            for (int i = 0; i < nullDataGroup.size(); i++) {
                strNull += this.getShowMessage( (String) nullDataGroup.get(i),(String) nullDataCode.get(i)) +
                    "\r\n";
            }
            this.messageBox(strNull + "������д��");
            return false;

                 }**/
        //���볬����Χ
        Vector numDataGroup = (Vector) nullParm.getData("RANG_GROUP_NAME");
        Vector numDataCode = (Vector) nullParm.getData("RANG_NAME");
        String strNum = "";
        if (numDataGroup != null && numDataGroup.size() > 0) {
            for (int i = 0; i < numDataGroup.size(); i++) {
                strNum +=
                    this.getShowMessage( (String) numDataGroup.get(i),
                                        (String) numDataCode.get(i)) +
                    "\r\n";
            }
            this.messageBox(strNum + "�ѳ�����Χ��");
            return false;
        }
        // add by lx ����У��  2011-06-12  end
        //�Ƿ���Ҫ�ϴ�ͼƬ
        if (this.isUloadPic()) {
            boolean isUploadSuccess = uploadPictureToFileServer();
            if (!isUploadSuccess) {
                //����ʧ��
                this.messageBox("ͼƬ�ϴ�ʧ��.");
                return false;
            }
        }
        //��浽�û��ļ�
        TParm asSaveParm = this.getEmrChildParm();
        //����
        String dateStr = StringTool.getString(SystemTool.getInstance().getDate(),
                                              "yyyy��MM��dd�� HHʱmm��ss��");
        //�½�����
        if ("NEW".equals(this.getOnlyEditType())) {
            this.getWord().setMessageBoxSwitch(false);
            //����(��һ�α���)
            this.getWord().setFileAuthor(this.getOperatorUserID());
            //��˾
            this.getWord().setFileCo("JAVAHIS");
            //����
            this.getWord().setFileTitle(asSaveParm.getValue("DESIGN_NAME"));
            //��ע
            this.getWord().setFileRemark(asSaveParm.getValue("CLASS_CODE") +
                                         "|" +
                                         asSaveParm.getValue("FILE_PATH") + "|" +
                                         asSaveParm.getValue("FILE_NAME"));
            //����ʱ��
            this.getWord().setFileCreateDate(dateStr);
            //����޸��� Operator.getID()
            this.getWord().setFileLastEditUser(this.getOperatorUserID());
            //����޸�����
            this.getWord().setFileLastEditDate(dateStr);
            //����޸�IP  Operator.getIP()
            this.getWord().setFileLastEditIP(this.getOperatorUserIP());
            //���Ϊ
            boolean success = this.getWord().onSaveAs(asSaveParm.getValue(
                "FILE_PATH"),
                asSaveParm.getValue("FILE_NAME"), 3);
            EMRCreateXMLTool.getInstance().createXML(asSaveParm.getValue(
                "FILE_PATH"), asSaveParm.getValue("FILE_NAME"), "EmrData",
                this.getWord());

            if (!success) {
                //�ļ��������쳣
                this.messageBox("�ļ��������쳣");
                //this.messageBox("E0103");
                this.getWord().setMessageBoxSwitch(true);
                //���ɱ༭
                this.getWord().setCanEdit(false);
                menuEnable(false);
                return false;
            }
            this.getWord().setMessageBoxSwitch(true);
            //���ɱ༭
            this.getWord().setCanEdit(false);
            menuEnable(false);
            //�������ݿ�����
            if (saveEmrFile(asSaveParm)) {
                //����ɹ�
                if (isShow) {
                    this.messageBox("����ɹ���");
                }
                //this.messageBox("P0001");
                this.loadTree();
                Object obj = this.getParameter();
                TParm emrFileDataParm = this.getEmrChildParm();
                //���뵥
                if (obj != null &&
                    "SQD".equals(emrFileDataParm.getValue("TYPEEMR"))) {
                    emrFileDataParm = (TParm) ( (TParm) obj).getData(
                        "EMR_FILE_DATA");
                    emrFileDataParm.setData("FILE_SEQ",
                                            asSaveParm.getValue("FILE_SEQ"));
                    emrFileDataParm.setData("CASE_NO", this.getCaseNo());
                    emrFileDataParm.setData("ADM_TYPE", this.getAdmType());
                }
                //���뵥����
                /** if ("SQD".equals(emrFileDataParm.getValue("TYPEEMR"))) {
                     ( (TParm) obj).runListener("EMR_SAVE_LISTENER",
                                                emrFileDataParm);
                 }
                 else {
                 ( (TParm) obj).runListener("EMR_SAVE_LISTENER", new TParm());
                 }**/

                //System.out.println("edit type================="+this.getOnlyEditType());
                this.setOnlyEditType("ONLYONE");
                // ����������ҽʦ��ֵ��ҽʦ�����Ӳ���������־/EMR_OPTLOG/�½�
                TParm result = OptLogTool.getInstance().writeOptLog(this.
                    getParmObj(), "C",
                    emrFileDataParm);
                // ���������ӡ��ʶ�������޸ı�ʶ
                String caseNo = ( (TParm)this.getParmObj()).getValue(
                    "CASE_NO");
                String fileSeq = emrFileDataParm.getValue("FILE_SEQ");
                if (!this.checkDrForDel()) {
                    setCanPrintFlgAndModifyFlg("", "", false);
                }
                else {
                    setCanPrintFlgAndModifyFlg(caseNo, fileSeq, true);
                }
                return true;
            }
            else {
                //����ʧ��
                //this.messageBox("E0001");
                if (isShow) {
                    this.messageBox("����ʧ��");
                }
                return false;
            }
        }
        //�༭����
        if ("ONLYONE".equals(this.getOnlyEditType())) {
            //���ǵ�ǰҽʦ��������������
            String lockedUser = this.findLockedEmrFileUser(this.getEmrChildParm());
            if (!lockedUser.equals("")) {
                this.messageBox(lockedUser + "ҽʦ������д�������޷����棡");
                return false;
            }

            //������ʾ������
            this.getWord().setMessageBoxSwitch(false);
            //����޸���  Operator.getID()
            this.getWord().setFileLastEditUser(this.getOperatorUserID());
            //����޸�����
            this.getWord().setFileLastEditDate(dateStr);
            //����޸�IP  Operator.getIP()
            this.getWord().setFileLastEditIP(this.getOperatorUserIP());

            System.out.println("===========file path=============" +
                               asSaveParm.getValue("FILE_PATH"));
            System.out.println("===========file name=============" +
                               asSaveParm.getValue("FILE_NAME"));

            //����
            boolean success = this.getWord().onSaveAs(asSaveParm.getValue(
                "FILE_PATH"),
                asSaveParm.getValue("FILE_NAME"), 3);

            EMRCreateXMLTool.getInstance().createXML(asSaveParm.getValue(
                "FILE_PATH"), asSaveParm.getValue("FILE_NAME"), "EmrData",
                this.getWord());
            if (!success) {
                //�ļ��������쳣
                this.messageBox("�ļ��������쳣");
                //������ʾ��Ϊ����ʾ
                this.getWord().setMessageBoxSwitch(true);
                //���ɱ༭
                this.getWord().setCanEdit(false);
                menuEnable(false);
                return false;
            }
            //������ʾ��Ϊ����ʾ
            this.getWord().setMessageBoxSwitch(true);
            //���ɱ༭
            this.getWord().setCanEdit(false);
            menuEnable(false);
            //��װ��������TParm���༭���棩
            this.setOptDataParm(asSaveParm, 1);
            //��������
            if (saveEmrFile(asSaveParm)) {
                //����ɹ�
                if (isShow) {
                    this.messageBox("����ɹ���");
                }
                Object obj = this.getParameter();
                //( (TParm) obj).runListener("EMR_SAVE_LISTENER", new TParm());
                //ɾ���˴���ʱ�ļ�
                /**delFile(this.getEmrChildParm().getValue("FILE_PATH"),
                 "TMP_" + this.getEmrChildParm().getValue("FILE_NAME"));**/
                this.loadTree();
                // ����������ҽʦ��ֵ��ҽʦ�����Ӳ���������־/EMR_OPTLOG/�޸�
                TParm result = OptLogTool.getInstance().writeOptLog(this.
                    getParmObj(), "M",
                    asSaveParm);
                return true;
            }
            else {
                //����ʧ��E0001
                if (isShow) {
                    this.messageBox("����ʧ��");
                }
                return false;
            }
        }
        return true;
    }

    public void setContainPic(boolean containPic) {
        this.containPic = containPic;
    }

    public boolean isContainPic() {
        return this.containPic;
    }

    /**
     * �ж��Ƿ���Ҫ�ϴ�
     * @return boolean
     */
    private boolean isUloadPic() {
        boolean isUpload = false;
        if ("NEW".equals(this.getOnlyEditType())) {
            //this.messageBox("��������ͼƬ:" + this.isContainPic());
            if (this.isContainPic()) {
                isUpload = true;
                this.getWord().setFileContainPic("Y");
            }
            else {
                isUpload = false;
                this.getWord().setFileContainPic("N");
            }
        }

        if ("ONLYONE".equals(this.getOnlyEditType())) {
            if (this.isContainPic() ||
                (this.getWord().getFileContainPic() != null &&
                 this.getWord().getFileContainPic().equals("Y"))) {
                isUpload = true;
                this.getWord().setFileContainPic("Y");
            }
            else {
                isUpload = false;
                this.getWord().setFileContainPic("N");
            }

        }

        return isUpload;

    }


}
