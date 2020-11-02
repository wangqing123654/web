package com.javahis.ui.opd;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TComboBox;
import jdo.sys.Operator;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.data.TParm;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTextField;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.util.StringTool;
import jdo.sys.SystemTool;
import com.dongyang.util.TMessage;
import com.dongyang.data.TSocket;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class OPDComTemplatePhraseControl
    extends TControl {
    private static final String PHRASE_FILE_PATH = "JHW/Ƭ��";
    //�����п���ҽʦ����
    private String deptOrDr = "";
    //�����п��ҡ�ҽʦ����
    private String deptOrDrCode = "";

    //TABLE
    private TTable table;

    //COMBOBOX ����
    private TComboBox combo;
    //Ƭ������(�Ժ�����Ƕ��)
    private TComboBox comboPhraseType;

    //Ƭ�����
    private TTextField phraseCode;

    //��ǰѡ�е�Ƭ��ģ��·��
    private String seletedPhraseFilePath;
   //��ǰѡ�е�Ƭ���ļ���;
    private String seletedPhraseFileName;
    //ԭƬ�����
    private String oldPhraseCode;
    //
    private int selectRow = -1;


    public OPDComTemplatePhraseControl() {
    }


    /**
     * ��ʼ������
     */
    public void onInit() {
        super.onInit();
        deptOrDr = this.getParameter() + "";
        //this.messageBox("===deptOrDr==="+deptOrDr);
        if ("1".equalsIgnoreCase(deptOrDr)) {
            combo = (TComboBox)this.getComponent("DEPT");
            //��Ѫ������ģ��������ҽ�����ڲ���??????
            deptOrDrCode = Operator.getDept();
            combo.setSelectedID(deptOrDrCode);
            combo.setVisible(true);
            combo.setEnabled(false);
        }
        comboPhraseType = (TComboBox)this.getComponent("PHRASE_TYPE");
        //��ʼ������
        comboPhraseType.setStringData(
            "[[id,text],['',''],[1,����],[2,�ֲ�ʷ],[3,����],[4,����ʷ],[5,����ʷ],[6,����ʷ],[7,����ʷ],[8,����ʷ]]");
        //
        table = (TTable)this.getComponent("TABLECOM");

        //Ƭ�����
        phraseCode = (TTextField)this.getComponent("PHRASE_CODE");

        callFunction("UI|TABLECOM|addEventListener",
                     "TABLECOM->" + TTableEvent.CLICKED, this, "onTableClicked");

        //����˫����Ƭ��༭
        table.addEventListener("TABLECOM->" + TTableEvent.DOUBLE_CLICKED, this,
                               "onTableDoubleClicked");
        //��ʼ�����
        initTable();

    }

    private void initTable() {
        TParm parm = new TParm(this.getDBTool().select(
            "SELECT * FROM OPD_COMTEMPLATEPHRASE WHERE DEPT_OR_DR='" + deptOrDr +
            "' AND DEPTORDR_CODE='" + deptOrDrCode +
            "' ORDER BY PHRASE_CODE"));
        table.setParmValue(parm);

    }

    /**
     * �����޸�
     * @param row int
     */
    public void onTableClicked(int row) {
        //// System.out.println("=========onTableClicked============" + row);
        if (row < 0) {
            return;
        }

        //Ƭ�����
        phraseCode.setValue(table.getParmValue().getValue("PHRASE_CODE", row));
        //Ƭ������
        comboPhraseType.setSelectedID(table.getParmValue().getValue(
            "PHRASE_TYPE", row));

        oldPhraseCode=phraseCode.getValue();

    }


    /**
     * ��EMRƬ��༭����
     * @param row int
     */
    public void onTableDoubleClicked(int row) {
        //// System.out.println("=========OpenEMRTemplateEdit============" + row);
        //�򿪽ṹ��Ƭ��༭����;
        if (row < 0) {
            return;
        }
        //�Ƿ�����ѱ���·��
        //NEW|EDIT
        seletedPhraseFilePath = table.getParmValue().getValue(
            "PHRASE_FILE_PATH", row);
        seletedPhraseFileName = table.getParmValue().getValue(
            "PHRASE_FILE_NAME", row);
       /** this.messageBox("===onTableDoubleClicked seletedPhraseFilePath===" +
                        seletedPhraseFilePath);**/
        TParm inParm = new TParm();
        //�޸�
        if (seletedPhraseFilePath != null && !seletedPhraseFilePath.equals("")) {
            inParm.setData("opType", "EDIT");
            inParm.setData("phraseFilePath", seletedPhraseFilePath);
            inParm.setData("phraseFileName", seletedPhraseFileName);
        }
        else {
            //����;
            inParm.setData("opType", "NEW");
            inParm.setData("phraseFilePath", PHRASE_FILE_PATH);
            inParm.setData("phraseFileName",
                           deptOrDr + "_" + deptOrDrCode + "_" +
                           table.getParmValue().getValue("PHRASE_CODE", row));
        }

        inParm.setData("deptOrDr", deptOrDr);
        inParm.setData("deptOrDrCode", deptOrDrCode);
        inParm.setData("phraseCode",
                       table.getParmValue().getValue("PHRASE_CODE", row));

        /** System.out.println("=====seletedPhraseFilePath======" +
                           seletedPhraseFilePath);
        // System.out.println("=====seletedPhraseFileName======" +
                           seletedPhraseFileName);**/
        this.openDialog("%ROOT%\\config\\emr\\EMREditComPhrase.x", inParm);
        initTable();

    }

    /**
     * ��ղ���
     */
    public void onClear() {
        //����Ƭ�����Ϊ��
        setValue("PHRASE_CODE", "");
        //Ƭ������Ϊ��
        setValue("PHRASE_TYPE", "");
        ( (TTable) getComponent("TABLECOM")).clearSelection();
        selectRow = -1;

    }

    /**
     * ɾ����¼;
     */
    public void onDelete() {
        //ɾ�����ݿ�
        if (this.messageBox( "ѯ��","�Ƿ�ɾ��", 2) == 0) {
            //this.messageBox("table row"+table.getSelectedRow());
            if (table.getSelectedRow() == -1)
                return;
            int selRow = table.getSelectedRow();
            TParm tableParm = table.getParmValue();
            String selPhraseCode = tableParm.getValue("PHRASE_CODE", selRow);
            String selPhraseFilePath = tableParm.getValue("PHRASE_FILE_PATH", selRow);
            String selPhraseFileName = tableParm.getValue("PHRASE_FILE_NAME", selRow);

            //deptOrDr  //
            String sql="DELETE FROM  OPD_COMTEMPLATEPHRASE  WHERE DEPT_OR_DR ='"+deptOrDr+"'";
                   sql+=" AND DEPTORDR_CODE='"+deptOrDrCode+"'";
                   sql+=" AND PHRASE_CODE='"+selPhraseCode+"'";

            TParm result =new TParm(this.getDBTool().update(sql));
            if (result.getErrCode() < 0) {
                //messageBox(result.getErrText());
                this.messageBox("ɾ��ʧ�ܣ�");
                return;
            }
            //�ɹ���ɾ����ӦƬ���ļ�;
            this.delFileTempletFile(selPhraseFilePath,selPhraseFileName);
            //ɾ��������ʾ
            int row = (Integer) callFunction("UI|TABLECOM|getSelectedRow");
            if (row < 0)
                return;
            this.callFunction("UI|TABLECOM|removeRow", row);
            this.callFunction("UI|TABLECOM|setSelectRow", row);

            this.messageBox("P0003");
            this.onClear();
        }
        else {
            return;
        }



    }

    /**
 * ɾ��ģ���ļ�
 * @param templetPath String
 * @param templetName String
 * @return boolean
 */
public boolean delFileTempletFile(String templetPath, String templetName) {
    //Ŀ¼���һ����Ŀ¼FILESERVER
    String rootName = TIOM_FileServer.getRoot();
    //ģ��·��������
    String templetPathSer = TIOM_FileServer.getPath("EmrTemplet");
    //�õ�SocketͨѶ����
    TSocket socket = TIOM_FileServer.getSocket();
    //ɾ���ļ�
    return TIOM_FileServer.deleteFile(socket,
                                      rootName + templetPathSer +
                                      templetPath +
                                      "\\" + templetName + ".jhw");
}


    /**
     * ����
     */
    public void onSave() {
        int selected = table.getSelectedRow();
        //this.messageBox("selected" + selected);
        //У��;
        if(phraseCode.getValue()==null||phraseCode.getValue().equals("")){
            this.messageBox("����дƬ����룡");
            return;
        }
        //this.messageBox("comboPhraseType.getSelectedID()"+comboPhraseType.getSelectedID());
        if(comboPhraseType.getSelectedID()==null ||comboPhraseType.getSelectedID().equals("") ){
            this.messageBox("��ѡ��Ƭ�����ͣ�");
            return;
        }


        String userID = Operator.getID();
        String userIP = Operator.getIP();
        String sysDate = StringTool.getString(SystemTool.getInstance().getDate(),
                                              "yyyy/MM/dd HH:mm:ss");

        String py = TMessage.getPy(phraseCode.getValue());

        //����
        if (selected == -1) {
            //// System.out.println("===========insert================");
            String sql="INSERT INTO OPD_COMTEMPLATEPHRASE(DEPT_OR_DR,DEPTORDR_CODE,PHRASE_CODE,PHRASE_TYPE,OPT_USER,OPT_DATE,OPT_TERM,PY1) VALUES('" +
                                    deptOrDr + "','" + deptOrDrCode + "','" +
                                    phraseCode.getValue() + "','" +
                                    comboPhraseType.getSelectedID() + "','" +
                                    userID + "',TO_DATE('" +sysDate+"','YYYY/MM/DD HH24:MI:SS') ,'" + userIP +
                                    "','" + py + "')";

            //// System.out.println("=====INSERT INTO sql======"+sql);
            TParm parm = new TParm(this.getDBTool().update(sql));
             if (parm.getErrCode() < 0) {
                 this.messageBox("����ʧ�ܣ�");
                 return;

             }else{
                 this.messageBox("����ɹ���");
                 //return;
             }



        }
        else {
            //���¿��¼��
            //// System.out.println("===========update================");
            String sql="UPDATE OPD_COMTEMPLATEPHRASE SET PHRASE_CODE='"+phraseCode.getValue() +"',";
                   sql+="PHRASE_TYPE='"+comboPhraseType.getSelectedID()+"',";
                   sql+="OPT_USER='"+userID+"',";
                   sql+="OPT_DATE=TO_DATE('" +sysDate+"','YYYY/MM/DD HH24:MI:SS') ,";
                   sql+="OPT_TERM='"+userIP+"',";
                   sql+="PY1='"+py+"'";
                   sql+=" WHERE DEPT_OR_DR='"+deptOrDr+"'";
                   sql+=" AND DEPTORDR_CODE='"+deptOrDrCode+"'";
                   sql+=" AND PHRASE_CODE='"+oldPhraseCode+"'";
            //// System.out.println("====update sql====="+sql);
            TParm parm = new TParm(this.getDBTool().update(sql));

             if (parm.getErrCode() < 0) {
                 this.messageBox("����ʧ�ܣ�");
                 return;

             }else{
                 //��¼���³ɹ����Ҽ���Ƭ�������ԭƬ����벻һ�£����ӦƬ���ļ�����;
                 if (!oldPhraseCode.equals(phraseCode.getValue())) {
                     //����һ��һ�����ļ�??????

                     //ɾ��ԭ�ļ�??????
                 }
                  this.messageBox("����ɹ���");

             }
        }
        this.onClear();
        //ˢ���б�;
        initTable();

    }

    /**
     *
     */
    public void onEdit() {
        int row = table.getSelectedRow();
        onTableDoubleClicked(row);
    }

    /**
     * ���Ƭ��ȫ·��
     * @return String
     */
    /** private String getPhraseFullPath(int row) {
         //Ŀ¼���һ����Ŀ¼FILESERVER
         String rootName = TIOM_FileServer.getRoot();
         //ģ��·��������
         String templetPathSer = TIOM_FileServer.getPath("EmrTemplet");
         //
         //// System.out.println("===rootName==="+rootName);
         //// System.out.println("===templetPathSer==="+templetPathSer);
         //1_01020101_����θ��-�ֲ�ʷ    ����_���ұ���_XXXX
         String fileName = deptOrDr + "_" + deptOrDrCode + "_" +
             table.getParmValue().getValue("PHRASE_CODE", row);
         String fullPath = rootName + templetPathSer + PHRASE_FILE_PATH +
             fileName + ".jhw";
         //��ʽ��fullPath
         fullPath = fullPath.replaceAll("\\\\", "/");
         // System.out.println("======fullPath=======" + fullPath);

         return fullPath;
     }**/


    /**
     * �������ݿ��������
     * @return TJDODBTool
     */
    private TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }

}
