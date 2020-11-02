package com.javahis.ui.emr;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TButton;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TWord;
import jdo.sys.Operator;
import com.dongyang.tui.text.CopyOperator;
import com.dongyang.util.StringTool;
import jdo.sys.SystemTool;

import java.util.UUID;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTable;
import com.dongyang.data.TSocket;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.tui.text.EComponent;

/**
 * <p>Title: �ղر༭������Ϣ</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: BlueCore</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class EMRPatPhraseControl
    extends TControl {
    private static String TABLE = "TABLE_PATINFO";
    /**
     * WORD����
     */
    private static final String TWORD_NAME = "WORD";
    //������Ϣ�ղ�Ŀ¼;
    private static final String PAT_INF_ROOT = "JHW/������Ϣ�ղؼ�/";


    private TParm evenParm;
    private String opType;

    private TButton btnOk;
    private TButton btnClose;
    private TButton btnSave;
    private TButton btnDel;
    private TTextField txtPhraseID;
    private TTextField txtMrNo;
    private TTextField txtPATName;
    private TTextField txtPhraseCode;
    private String strMrNo;
    private TTable tablePatInfo;


    /**
     * WORD����
     */
    private TWord parmWord;
    private TWord word;


    /**
     * ��ǰ�༭״̬
     */
    private String onlyEditType;


    public EMRPatPhraseControl() {
    }

    public void onInit() {
        super.onInit();

        Object obj = this.getParameter();
        if (obj != null) {
            evenParm = (TParm) obj;
            opType = evenParm.getValue("OP_TYPE");
            parmWord = (TWord) evenParm.getData("TWORD");

            btnOk = (TButton)this.getComponent("BtnOk");
            btnClose = (TButton)this.getComponent("BtnClose");
            btnSave = (TButton)this.getComponent("BtnSave");
            btnDel = (TButton)this.getComponent("BtnDel");
            txtMrNo = (TTextField)this.getComponent("MRNO");
            txtPATName = (TTextField)this.getComponent("PATName");
            txtPhraseCode = (TTextField)this.getComponent("PhraseName");
            txtPhraseID = (TTextField)this.getComponent("PhraseID");

            word = (TWord)this.getComponent(TWORD_NAME);
            strMrNo = evenParm.getValue("MR_NO");
            txtMrNo.setText(strMrNo);
            txtPATName.setText(evenParm.getValue("PAT_NAME"));

            //�ղز�����Ϣ;
            if (opType.equals("SavePatInfo")) {
                txtPhraseCode.setEnabled(true);
                btnOk.setVisible(false);
                btnClose.setVisible(false);
                this.setOnlyEditType("NEW");

                //�ɱ༭
                this.word.setCanEdit(true);
                //�༭״̬(������)
                this.word.onEditWord();
                this.word.onPaste();
                CopyOperator.clearComList();
                //���벡����Ϣ;
            }
            else if (opType.equals("InsertPatInfo")) {
                btnSave.setVisible(false);
                btnDel.setVisible(false);

            }
            //��ʼ���б�
            tablePatInfo = (TTable)this.getComponent(TABLE);
            initTable();
            //�б�˫�����¼�;
            //ע��Table����¼�
            callFunction("UI|" + TABLE + "|addEventListener",
                         TABLE + "->" + TTableEvent.DOUBLE_CLICKED, this,
                         "onDoubleTableClicked");

        }

    }

    /**
     * ��ʼ���б�
     */
    public void initTable() {
        //���֣��ļ�·�����ļ���
        TParm parm = new TParm(this.getDBTool().select(
            "SELECT PHRASE_NAME,FILE_NAME,FILE_PATH FROM EMR_PATINFO_FOLDER WHERE MR_NO='" +
            strMrNo + "' ORDER BY OPT_DATE DESC"));
        tablePatInfo.setParmValue(parm);
    }

    /**
     * ����
     */
    public void onSave() {
        //this.messageBox("come in"+txtPhraseCode.getText());
        //�жϸ������ֵ10����ֹ
        if(tablePatInfo.getRowCount()==10){
            this.messageBox("ÿ���������ֻ�ܴ�10����Ϣ��");
            return;
        }

        if (txtPhraseCode.getText().equals("")) {
            this.messageBox("��������Ϣ���ƣ�");
            return;
        }
        //����
        String dateStr = StringTool.getString(SystemTool.getInstance().getDate(),
                                              "yyyy��MM��dd�� HHʱmm��ss��");
        String sysDate = StringTool.getString(SystemTool.getInstance().getDate(),
                                              "yyyy/MM/dd HH:mm:ss");

        //����
        if ("NEW".equals(this.getOnlyEditType())) {
            word.setMessageBoxSwitch(false);
            this.word.setFileAuthor(Operator.getID());
            //��˾
            this.word.setFileCo("JAVAHIS");
            //����
            this.word.setFileTitle(txtPhraseCode.getText());
            //����ʱ��
            this.word.setFileCreateDate(dateStr);
            //����޸���
            this.word.setFileLastEditUser(Operator.getID());
            //����޸�����
            this.word.setFileLastEditDate(dateStr);
            //����޸�IP
            this.word.setFileLastEditIP(Operator.getIP());
            //���Ϊ
            String id = this.getUUID();
            boolean success = word.onSaveAs(PAT_INF_ROOT + "/" + strMrNo,
                                            id, 3);
            if (success) {
                String sql = "INSERT INTO EMR_PATINFO_FOLDER (ID,MR_NO,PHRASE_NAME,FILE_PATH,FILE_NAME,OPT_USER,OPT_DATE,OPT_TERM)";
                sql += " VALUES('" + id + "',";
                sql += "'" + strMrNo + "',";
                sql += "'" + txtPhraseCode.getText() + "',";
                sql += "'" + PAT_INF_ROOT + strMrNo + "',";
                sql += "'" + id + "',";
                sql += "'" + Operator.getID() + "',";
                sql += "TO_DATE('" + sysDate +
                    "','YYYY/MM/DD HH24:MI:SS') ,";
                sql += "'" + Operator.getIP() + "')";
                // System.out.println("===sql====" + sql);
                //�ɹ��󣬲������ݱ�EMR_PATINFO_FOLDER;
                TParm parm = new TParm(this.getDBTool().update(sql));

                if (parm.getErrCode() < 0) {
                    this.messageBox("����ʧ�ܣ�");
                    return;
                }

                this.messageBox("����ɹ���");
                //��������
                txtPhraseID.setText(id);
                this.setOnlyEditType("EDIT");

            }
            //����
        }
        else {
            //this.messageBox("���±༭ text====" + txtPhraseID.getText());
            String id = txtPhraseID.getText();
            //������ʾ������
            this.word.setMessageBoxSwitch(false);
            //����޸���
            this.word.setFileLastEditUser(Operator.getID());
            //����޸�����
            this.word.setFileLastEditDate(dateStr);
            //����޸�IP
            this.word.setFileLastEditIP(Operator.getIP());
            //����
            boolean success = word.onSaveAs(PAT_INF_ROOT + "/" + strMrNo,
                                            txtPhraseID.getText(), 3);
            if (success) {
                String sql = "UPDATE EMR_PATINFO_FOLDER SET ";
                sql += "MR_NO='" + strMrNo + "',"; //,,FILE_PATH,FILE_NAME,OPT_USER,OPT_DATE,OPT_TERM)";
                sql += "PHRASE_NAME='" + txtPhraseCode.getText() + "',";
                sql += "FILE_PATH='" + PAT_INF_ROOT + strMrNo + "',";
                sql += "FILE_NAME='" + id + "',";
                sql += "OPT_USER='" + Operator.getID() + "',";
                sql += "OPT_DATE=TO_DATE('" + sysDate +
                    "','YYYY/MM/DD HH24:MI:SS') ,";
                sql += "OPT_TERM='" + Operator.getIP() + "'";
                sql += " WHERE ID='" + id + "'";

                //System.out.println("=====sql====="+sql);
                //�ɹ��󣬲������ݱ�EMR_PATINFO_FOLDER;
                TParm parm = new TParm(this.getDBTool().update(sql));

                if (parm.getErrCode() < 0) {
                    this.messageBox("����ʧ�ܣ�");
                    return;
                }

                this.messageBox("����ɹ���");
                this.setOnlyEditType("EDIT");

            }

        }
        initTable();
    }

    /**
     * ɾ��Ƭ��
     */
    public void onDelete() {
        final int row=tablePatInfo.getSelectedRow();
        //this.messageBox("==row="+row);
        if(row<0){
            this.messageBox("����ѡ��ɾ����Ϣ��");
            return;
        }

        //ɾ�����ݿ�
        if (this.messageBox("ѯ��", "�Ƿ�ɾ����", 2) == 0) {
            final String fileName=(String) tablePatInfo.getParmValue().getData("FILE_NAME",row);
            final String filePath=(String) tablePatInfo.getParmValue().getData("FILE_PATH",row);
            String sql="DELETE FROM EMR_PATINFO_FOLDER WHERE ID='"+fileName+"'";
            //ɾ�����ݿ��¼
              TParm parm = new TParm(this.getDBTool().update(sql));
              //������ӣ���ID��
              if (parm.getErrCode() < 0) {
                   this.messageBox("����ʧ�ܣ�");
                   return;
               }
              //ɾ����Ӧ���ļ�
              boolean flag= delFileTempletFile(filePath, fileName);
              this.messageBox("����ɹ���");


            this.setOnlyEditType("NEW");
            //ɾ���ɹ���ˢ�±��;
            initTable();

        }



    }

    /**
     * �ر�
     */
    public boolean onClosing() {
        this.setReturnValue(this.getValue(TWORD_NAME));
        return true;
    }

    /**
     * Ƭ�ﴫ��
     */
    public void onFetchBack() {
         final int row=tablePatInfo.getSelectedRow();
        if(row<0){
            this.messageBox("����ѡ����Ϣ��");
            return;
        }
        final String fileName=(String) tablePatInfo.getParmValue().getData("FILE_NAME",row);
        final String filePath=(String) tablePatInfo.getParmValue().getData("FILE_PATH",row);
        //this.messageBox("filePath"+filePath);
        //this.messageBox("fileName"+fileName);

         EComponent com=parmWord.getFocusManager().getFocus();
         parmWord.getFocusManager().onInsertFile(filePath, fileName,
                                             3, false);
         parmWord.update();
    }

    public void setOnlyEditType(String onlyEditType) {
        this.onlyEditType = onlyEditType;
    }

    public String getOnlyEditType() {
        return this.onlyEditType;
    }

    public void onDoubleTableClicked(int row) {
        //this.messageBox("row" + row);
        //���ò�����
        if (row < 0) {
            return;
        }
        TParm data = (TParm) callFunction("UI|TABLE_PATINFO|getParmValue");
        String strPhraseName = data.getValue("PHRASE_NAME", row);
        txtPhraseCode.setText(strPhraseName);
        String strFileName = data.getValue("FILE_NAME", row);
        txtPhraseID.setText(strFileName);
        String strFilePath = data.getValue("FILE_PATH", row);

        //�򿪲���
        if (!this.word.onOpen(strFilePath,
                              strFileName, 3, true)) {
            return;
        }
        this.word.setCanEdit(true);
        this.setOnlyEditType("EDIT");

        //selectRow = row;



    }

    private synchronized String getUUID() {
        String s = UUID.randomUUID().toString();
        //ȥ����-������
        return s.substring(0, 8) + s.substring(9, 13) + s.substring
            (14, 18) + s.substring(19, 23) + s.substring(24);
    }

    /**
     * �������ݿ��������
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
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

       if (isDelFile) {
           return true;
       }

       return false;
   }



}
