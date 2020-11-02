package com.javahis.ui.emr;

import com.dongyang.control.TControl;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import com.javahis.device.EMRPad30;
import com.javahis.util.JavaHisDebug;
import com.dongyang.ui.TTree;
import com.dongyang.ui.event.TTreeEvent;
import com.dongyang.ui.TTreeNode;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.util.FileTool;
import java.io.File;
import java.io.IOException;
import javax.swing.SwingUtilities;
import com.dongyang.data.TSocket;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TTextField;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;
import jdo.sys.Operator;
import com.dongyang.manager.TIOM_AppServer;
import java.util.Map;
import java.util.StringTokenizer;

public class EMRControl extends TControl{
    /**
     * EMR�����
     */
    static EMRPad30 emr;
    /**
     * ��������
     */
    private static final String TREE_NAME = "TREE";
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
     * ��ǰ�༭�ļ���
     */
    private String fileNameOnly="";
    /**
     * �Ƿ�ֻ��
     */
    private boolean isReadOnly = true;
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
     * ģ������
     */
    private String emrTypeCode;
    /**
     * ����������
     */
    private static final String actionName = "action.odi.ODIAction";
    public void onInit(){
        super.onInit();
        //��ʼ������
        initPage();
        //ע���¼�
        initEven();
    }
    /**
     * ע���¼�
     */
    public void initEven(){
        //����ѡ������Ŀ
        addEventListener(TREE_NAME+"->" + TTreeEvent.DOUBLE_CLICKED, "onTreeDoubled");
    }
    /**
     * ��ʼ������
     */
    public void initPage(){
        //�õ�ϵͳ���
        Object obj = this.getParameter();
//        this.messageBox(""+obj);
        if(obj!=null){
            this.setSystemType(((TParm)obj).getValue("SYSTEM_TYPE"));
            this.setMrNo(((TParm)obj).getValue("MR_NO"));
            this.setIpdNo(((TParm)obj).getValue("IPD_NO"));
            this.setPatName(((TParm)obj).getValue("PAT_NAME"));
            this.setCaseNo(((TParm)obj).getValue("CASE_NO"));
            String yearStr = StringTool.getString((Timestamp)((TParm)obj).getData("ADM_DATE"),"yyyy");
            this.setAdmYear(yearStr);
            String mouthStr = StringTool.getString((Timestamp)((TParm)obj).getData("ADM_DATE"),"MM");
            this.setAdmMouth(mouthStr);
        }else{
            this.setSystemType("ODI");
            this.setMrNo("000000000151");
            this.setIpdNo("000000000047");
            this.setPatName("��һ����3");
            this.setCaseNo("090508000001");
            this.setAdmYear("2009");
            this.setAdmMouth("05");
        }
        //����EMR
        initEMR();
        //������
        loadTree();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                initOCX();
                //����EMR��ִ��
                emrOpen();
            }
        });
    }
    /**
     * ����EMR��ִ��
     */
    public void emrOpen(){
        //��ʼ������EMR
        emrInterface();
    }
    /**
     *
     */
    public void initOCX(){
        emr.initOCX();
    }
    /**
     * ����༭
     */
    public void onEdit(){
        //�Ƿ���Ҫ�༭���ļ�
        TParm emrparm = this.getEmrChildParm();
        if (emrparm.getData("EMR_FILE_DATA") == null) {
            this.messageBox("û��Ҫ�༭���ļ���");
            return;
        }
        if(!this.isReadOnly){
            this.messageBox("�ļ��Ѿ����ڱ༭״̬��");
            return;
        }
        TParm parm = (TParm)this.getEmrChildParm().getData("EMR_FILE_DATA");
        String creatFileUser = parm.getValue("CREATOR_USER");
        //��������
        if("ODI".equals(this.getSystemType())){
            //�Ƿ��б༭��Ȩ��
            //�Ǿ���ҽʦ
            if((this.getVsDrCode().equals(Operator.getID())&&creatFileUser.equals(this.getAttendDrCode()))||(this.getVsDrCode().equals(Operator.getID())&&creatFileUser.equals(this.getDirectorDrCode()))){
                this.messageBox("��û��Ȩ�ޱ༭���ļ���");
                return;
            }
            //����ҽʦ
            if(this.getAttendDrCode().equals(Operator.getID())&&creatFileUser.equals(this.getDirectorDrCode())){
                this.messageBox("��û��Ȩ�ޱ༭���ļ���");
                return;
            }
        }
        //�Ƿ��ڱ༭״̬
        if(isEdit()){
            this.messageBox("���ļ������ڱ༭״̬��");
            return;
        }
        emr.setDocumentMode(emr.DOCUMENT_MODE_EDIT);
        //System.out.println("�򿪲�������" + parm);
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        parm.setData("CURRENT_USER", Operator.getID());
        TParm result = TIOM_AppServer.executeAction(actionName,
            "updateEmrCurrentUser", parm);
        if (result.getErrCode() < 0) {
            this.messageBox("���ô���");
            return;
        }
        this.isReadOnly=false;
        this.fileNameOnly = this.getEmrChildParm().getValue("EMR_FILE_NAME");
    }
    /**
     * �Ƿ���Բ鿴
     * @return boolean
     */
    public boolean isCheckUserDr(){
        boolean falg = false;
        TParm parm = new TParm(this.getDBTool().select("SELECT VS_DR_CODE,ATTEND_DR_CODE,DIRECTOR_DR_CODE FROM ADM_INP WHERE CASE_NO='"+this.getCaseNo()+"' AND (VS_DR_CODE='"+Operator.getID()+"' OR ATTEND_DR_CODE='"+Operator.getID()+"' OR DIRECTOR_DR_CODE='"+Operator.getID()+"')"));
        if(parm.getInt("ACTION","COUNT")>0){
            //����ҽʦ
            this.setVsDrCode(parm.getValue("VS_DR_CODE",0));
            //����ҽʦ
            this.setAttendDrCode(parm.getValue("ATTEND_DR_CODE",0));
            //������
            this.setDirectorDrCode(parm.getValue("DIRECTOR_DR_CODE",0));
            falg = true;
        }
        return falg;
    }
    /**
     * ������
     */
    public void loadTree(){

        treeRoot = this.getTTree(TREE_NAME).getRoot();
        this.setValue("MR_NO", this.getMrNo());
        this.setValue("PAT_NAME", this.getPatName());
        //סԺ
        if(this.getSystemType().equals("ODI")){
           this.messageBox("ODI");
            this.setValue("IPD_NO", this.getIpdNo());
            //�õ�����
            treeRoot.setText("סԺ");
            treeRoot.setType("Root");
            treeRoot.removeAllChildren();
            //�õ��ڵ�����
            TParm parm = this.getRootNodeData();
            if(parm.getInt("ACTION","COUNT")==0){
                this.messageBox("û������EMRģ�壡�������ݿ�����");
                return;
            }
            int rowCount = parm.getInt("ACTION","COUNT");
            for(int i=0;i<rowCount;i++){
                //�ӽڵ�   EMR_CLASS���е�STYLE�ֶ�
                TTreeNode node = new TTreeNode(parm.getValue("CLASS_DESC",i),parm.getValue("CLASS_STYLE",i));
                //�洢EMR_TREE���CODE�ֶ�
                node.setID(parm.getValue("SEQ",i));
                //�����Ƿ��EMR,EMR_TREE���EMR_CLASS�ֶ�
                node.setGroup(parm.getValue("CLASS_CODE",i));
                //�洢EMR_TREE��� SYS_PROGRAM�ֶ�
                node.setData(parm.getValue("SYS_PROGRAM",i));
                //add by lx
                node.setValue(parm.getValue("CLASS_CODE",i));
                //��������
                treeRoot.add(node);
                //�����ڵ�����Ӧ�ڴ���
                /**TParm allChildsParm = this.getAllChildsNode(parm.getValue("CLASS_CODE",i));
                int childRowCount = allChildsParm.getCount();
                this.messageBox("===childRowCount===="+childRowCount);
                for (int j = 0; j < rowCount; i++) {
                    TTreeNode childrenNode = new TTreeNode(allChildsParm.getValue("CLASS_DESC",j),parm.getValue("CLASS_STYLE",j));
                    childrenNode.setID(allChildsParm.getValue("SEQ",j));
                    childrenNode.setID(allChildsParm.getValue("CLASS_CODE",j));
                    childrenNode.setValue(allChildsParm.getValue("CLASS_CODE",i));

                    treeRoot.findNodeForID(allChildsParm.getValue(
                        "PARENT_CLASS_CODE", i)).add(childrenNode);
                }**/




                //��ѯ���ļ�
                TParm childParm = this.getRootChildNodeData(parm.getValue("CLASS_CODE",i));
                int rowCldCount = childParm.getInt("ACTION","COUNT");
                for(int j=0;j<rowCldCount;j++){
                    TTreeNode nodeChlid = new TTreeNode(childParm.getValue("DESIGN_NAME",j),"4");
                    nodeChlid.setID(childParm.getValue("FILE_SEQ",j));
                    nodeChlid.setGroup(childParm.getValue("CLASS_CODE",j));
                    nodeChlid.setData(childParm.getRow(j));
                    node.add(nodeChlid);
                }
            }
            this.getTTree(TREE_NAME).update();
        }
        if(this.getSystemType().equals("ODO")){
            this.messageBox("ODO");
            ((TTextField)this.getComponent("IPD_NO")).setVisible(false);
            ((TLabel)this.getComponent("IPD_LAB")).setVisible(false);
            //�õ�����
            treeRoot.setText("����");
            treeRoot.setType("Root");
            treeRoot.removeAllChildren();
            //�õ��ڵ�����
            TParm parm = this.getRootNodeData();
            if(parm.getInt("ACTION","COUNT")==0){
                this.messageBox("û������EMRģ�壡�������ݿ�����");
                return;
            }
            int rowCount = parm.getInt("ACTION","COUNT");
            for(int i=0;i<rowCount;i++){
                //�ӽڵ�EMR_CLASS���е�STYLE�ֶ�
                TTreeNode node = new TTreeNode(parm.getValue("CLASS_DESC",i),parm.getValue("CLASS_STYLE",i));
                //�洢EMR_TREE���CODE�ֶ�
                node.setID(parm.getValue("SEQ",i));
                //�����Ƿ��EMR,EMR_TREE���EMR_CLASS�ֶ�
                node.setGroup(parm.getValue("CLASS_CODE",i));
                //�洢EMR_TREE��� SYS_PROGRAM�ֶ�
                node.setData(parm.getValue("SYS_PROGRAM",i));
                //��������
                treeRoot.add(node);
                //��ѯ���ļ�
                TParm childParm = this.getRootChildNodeData(parm.getValue("CLASS_CODE",i));
                int rowCldCount = childParm.getInt("ACTION","COUNT");
                for(int j=0;j<rowCldCount;j++){
                    TTreeNode nodeChlid = new TTreeNode(childParm.getValue("DESIGN_NAME",j),"4");
                    nodeChlid.setID(childParm.getValue("FILE_SEQ",j));
                    nodeChlid.setGroup(childParm.getValue("CLASS_CODE",j));
                    nodeChlid.setData(childParm.getRow(j));
                    node.add(nodeChlid);
                }
            }
            this.getTTree(TREE_NAME).update();
        }
    }

    /**
     *  �ṹ�������ӿ�
     */
    public void emrInterface(){
        Object objOther = ( (TParm)this.getParameter()).getData("EMR_FILE_DATA");
        TParm action = new TParm();
        if (objOther != null) {
            TParm parm = (TParm)objOther;
            if(parm.getBoolean("FLG")){
                action.setData("EMR_TEMPLET_NAME",parm.getValue("SUBCLASS_DESC"));
                action.setData("EMR_FILE_NAME", parm.getValue("FILE_NAME"));
                action.setData("EMR_FILE_PATH", parm.getValue("FILE_PATH"));
                action.setData("EMR_FILE_DATA", parm);
//                this.getEmrFileData(action);
                this.getEmrFileTemp(action);
            }else{
                action.setData("EMR_TEMPLET_NAME",parm.getValue("SUBCLASS_DESC"));
                action.setData("EMR_FILE_NAME", parm.getValue("EMT_FILENAME"));
                action.setData("EMR_FILE_PATH", parm.getValue("TEMPLET_PATH"));
                action.setData("EMR_FILE_DATA", parm);
                this.getEmrFileTemp(action);
            }
        }
    }
    /**
     * ����ģ����������
     * @param parm TParm
     */
    public void getEmrFileTemp(TParm parm){
        //�������������
        this.setEmrChildParm(parm);
        //Ŀ¼���һ����Ŀ¼FILESERVER
        String rootName = TIOM_FileServer.getRoot();
        //ģ��·��������
        String templetPath = TIOM_FileServer.getPath("EmrTemplet");
        //EMR�ļ���
        String emrFileName = parm.getValue("EMR_FILE_NAME");
        //Ҫ�õ�ģ���ļ�·��
        String olinPath = parm.getValue("EMR_FILE_PATH");
        //���ر�����ļ�����Ŀ¼
        String emrLocal = TIOM_FileServer.getPath("EmrLocalTempFileName");
//        this.messageBox(rootName+templetPath+olinPath+"\\"+emrFileName+".emt");
        TSocket socket = TIOM_FileServer.getSocket();
        byte bytEmrData[] = TIOM_FileServer.readFile(socket,rootName+templetPath+olinPath+"\\"+emrFileName);
        if(bytEmrData==null){
            this.messageBox("ģ���ļ������ڣ�");
            return;
        }
        File file = new File(emrLocal);
        file.mkdirs();
        try {
            FileTool.setByte(emrLocal+"EmrTemplet.emt", bytEmrData);
        }
        catch (IOException ex) {
            this.messageBox("������ʱ�ļ�ʧ�ܣ�");
            ex.printStackTrace();
            return;
        }
        //���ú�
        setMicroField();
        emr.fileOpen(emrLocal+"EmrTemplet.emt");
        file = new File(emrLocal+"EmrTemplet.emt");
        file.delete();
        this.isReadOnly=false;
        this.fileNameOnly = parm.getValue("EMR_FILE_NAME");
    }
    /**
     * �õ����ڵ�����
     * @return TParm
     */
    public TParm getRootNodeData() {
        TParm result = new TParm(this.getDBTool().select("SELECT B.CLASS_DESC,B.CLASS_STYLE,A.CLASS_CODE,A.SYS_PROGRAM,A.SEQ "+
            " FROM EMR_TREE A,EMR_CLASS B WHERE A.SYSTEM_CODE = '" +this.getSystemType() +
            "' AND A.CLASS_CODE=B.CLASS_CODE ORDER BY A.SEQ"));
        //System.out.println("��������" + result);
        return result;
    }
    /**
     * �õ����ڵ�����
     * @return TParm
     */
    public TParm getRootChildNodeData(String classCode) {
        TParm result = new TParm(this.getDBTool().select("SELECT A.CASE_NO,A.FILE_SEQ,A.MR_NO,A.IPD_NO,A.FILE_PATH,A.FILE_NAME,A.DESIGN_NAME,A.CLASS_CODE,A.SUBCLASS_CODE,A.DISPOSAC_FLG,B.SUBCLASS_DESC,A.CURRENT_USER,A.CREATOR_USER"+
            " FROM EMR_FILE_INDEX A,EMR_TEMPLET B WHERE A.CASE_NO='"+this.getCaseNo()+"' AND A.CLASS_CODE='"+classCode+"' AND A.DISPOSAC_FLG<>'Y' AND A.SUBCLASS_CODE=B.SUBCLASS_CODE"));
        /**System.out.println("==========sql test===========SELECT A.CASE_NO,A.FILE_SEQ,A.MR_NO,A.IPD_NO,A.FILE_PATH,A.FILE_NAME,A.DESIGN_NAME,A.CLASS_CODE,A.SUBCLASS_CODE,A.DISPOSAC_FLG,B.SUBCLASS_DESC,A.CURRENT_USER,A.CREATOR_USER"+
            " FROM EMR_FILE_INDEX A,EMR_TEMPLET B WHERE A.CASE_NO='"+this.getCaseNo()+"' AND A.CLASS_CODE='"+classCode+"' AND A.DISPOSAC_FLG<>'Y' AND A.SUBCLASS_CODE=B.SUBCLASS_CODE");

        System.out.println("��������" + result);**/
        return result;
    }

    /**
     * �õ����ڵ��������ӽڵ�
     * @param parentClassCode String
     * @return TParm
     */
    private TParm getAllChildsNode(String parentClassCode){
         TParm result = new TParm();
         TParm childs = this.getChildsByParentNode(parentClassCode);
         if(childs!=null&&childs.getCount()>0){
             for(int i=0;i<childs.getCount();i++){
                 //CLASS_CODE,CLASS_DESC,CLASS_STYLE,LEAF_FLG,PARENT_CLASS_CODE
                 result.addData("CLASS_CODE",childs.getData("CLASS_CODE",i));
                 result.addData("CLASS_DESC",childs.getData("CLASS_DESC",i));
                 result.addData("CLASS_STYLE",childs.getData("CLASS_STYLE",i));
                 result.addData("LEAF_FLG",childs.getData("LEAF_FLG",i));
                 result.addData("PARENT_CLASS_CODE",childs.getData("PARENT_CLASS_CODE",i));
                 result.addData("SEQ",childs.getData("SEQ",i));
             }
         }


         return result;
    }

    /**
     * add by lx
     * ͨ�����ڵ�õ��ӽڵ�;
     * @param parentClassCode String
     * @return TParm
     */
    private TParm getChildsByParentNode(String parentClassCode){
         TParm result = new TParm(this.getDBTool().select("SELECT CLASS_CODE,CLASS_DESC,CLASS_STYLE,LEAF_FLG,PARENT_CLASS_CODE,SEQ FROM EMR_CLASS WHERE PARENT_CLASS_CODE='"+parentClassCode+"' ORDER BY CLASS_CODE,SEQ"));

         //System.out.println("��������" + result);
         return result;
    }

    /**
     * ����EMR
     */
    public void initEMR(){
        if(emr == null)
            emr = new EMRPad30();
        JPanel panel = (JPanel)callFunction("UI|PANEL|getThis");
        panel.setLayout(new BorderLayout());
        panel.add(emr);
    }
    /**
     * �����
     * @param parm Object
     */
    public void onTreeDoubled(Object parm){
        //��������
        if("ODI".equals(this.getSystemType())){
            //�Ƿ��в鿴��Ȩ��
            if(!isCheckUserDr()){
                this.messageBox("��û��Ȩ�޲鿴��");
                return;
            }
        }
        //System.out.println("object"+parm);
        TTreeNode node = (TTreeNode)parm;
        if(!node.getType().equals("4"))
            return;
        TParm action = new TParm();
        TParm emrParm = (TParm)node.getData();
        action.setData("EMR_TEMPLET_NAME", emrParm.getValue("SUBCLASS_DESC"));
        action.setData("EMR_FILE_NAME",emrParm.getValue("FILE_NAME"));
        action.setData("EMR_FILE_PATH",emrParm.getValue("FILE_PATH"));
        action.setData("EMR_FILE_DATA",emrParm);
        getEmrFileData(action);
    }
    /**
     *  �õ���
     * @param tag String
     * @return TTree
     */
    public TTree getTTree(String tag){
        return (TTree)this.getComponent(tag);
    }
    /**
     * �½�����(���)
     */
    public void onCreatMenu(){
        openChildDialog();
    }
    /**
     * ���Ӵ���
     */
    public void openChildDialog(){
        if(!checkSave())
             return;
        //ģ������
        String emrClass = this.getTTree(TREE_NAME).getSelectNode().getGroup();
        String nodeName = this.getTTree(TREE_NAME).getSelectNode().getText();
        Object programObj = this.getTTree(TREE_NAME).getSelectNode().getData();
        String emrType = this.getTTree(TREE_NAME).getSelectNode().getType();
        //����ǵ���HIS��������������
        if(programObj.toString().length()!=0){
            return;
        }
        TParm parm = new TParm();
        if (this.getSystemType().equals("ODI")) {
            parm = new TParm(this.getDBTool().select(
                "SELECT CLASS_CODE,SUBCLASS_CODE,SUBCLASS_DESC,TEMPLET_PATH,SEQ,DEPT_CODE,EMT_FILENAME FROM EMR_TEMPLET WHERE CLASS_CODE='" + emrClass +
                "' AND IPD_FLG='Y' ORDER BY SEQ"));
            parm.setData("SYSTEM_CODE","ODI");
            parm.setData("NODE_NAME",nodeName);
            parm.setData("TYPE",emrType);
        }
        if (this.getSystemType().equals("ODO")) {
            parm = new TParm(this.getDBTool().select(
                "SELECT CLASS_CODE,SUBCLASS_CODE,SUBCLASS_DESC,TEMPLET_PATH,SEQ,DEPT_CODE,EMT_FILENAME FROM EMR_TEMPLET WHERE CLASS_CODE='" + emrClass +
                "' AND OPD_FLG='Y' ORDER BY SEQ"));
            parm.setData("SYSTEM_CODE","ODO");
            parm.setData("NODE_NAME",nodeName);
            parm.setData("TYPE",emrType);
        }
        Object obj = this.openDialog("%ROOT%\\config\\emr\\EMRChildUI.x",parm);
        if(obj==null|| !(obj instanceof TParm)){
            return;
        }
        TParm action = (TParm)obj;
        //�������������
//        this.messageBox_("�Ӵ��ڵ���"+action);
        this.setEmrChildParm(action);
        //Ŀ¼���һ����Ŀ¼FILESERVER
        String rootName = TIOM_FileServer.getRoot();
        //ģ��·��������
        String templetPath = TIOM_FileServer.getPath("EmrTemplet");
        //EMR�ļ���
        String emrFileName = action.getValue("EMR_FILE_NAME");
        //Ҫ�õ�ģ���ļ�·��
        String olinPath = action.getValue("EMR_FILE_PATH");
        //���ر�����ļ�����Ŀ¼
        String emrLocal = TIOM_FileServer.getPath("EmrLocalTempFileName");
//        this.messageBox(rootName+templetPath+olinPath+"\\"+emrFileName+".emt");
        TSocket socket = TIOM_FileServer.getSocket();
        byte bytEmrData[] = TIOM_FileServer.readFile(socket,rootName+templetPath+olinPath+"\\"+emrFileName);
        if(bytEmrData==null){
            this.messageBox("ģ���ļ������ڣ�");
            return;
        }
        File file = new File(emrLocal);
        file.mkdirs();
        try {
            FileTool.setByte(emrLocal+"EmrTemplet.emt", bytEmrData);
        }
        catch (IOException ex) {
            this.messageBox("������ʱ�ļ�ʧ�ܣ�");
            ex.printStackTrace();
            return;
        }
        //���ú�
        setMicroField();
        emr.fileOpen(emrLocal+"EmrTemplet.emt");
        file = new File(emrLocal+"EmrTemplet.emt");
        file.delete();
        this.isReadOnly=false;
        this.fileNameOnly = emrFileName;
        //ģ������
        this.setEmrTypeCode(emrType);
    }
    /**
     * ���ú�
     */
    public void setMicroField(){
        Map map = this.getDBTool().select("SELECT A.PAT_NAME AS ����,A.IDNO AS ���֤��,TO_CHAR(A.BIRTH_DATE,'YYYY-MM-DD') AS ��������,"+
                                          " D.CTZ_DESC AS ���ʽ,A.TEL_HOME AS ��ͥ�绰,A.SEX_CODE AS �Ա�,A.CONTACTS_NAME AS ��ϵ��,"+
                                          " TO_CHAR(A.FIRST_ADM_DATE,'YYYY-MM-DD') AS ��������,A.CELL_PHONE AS �ֻ�,"+
                                          " A.ADDRESS AS ��ͥסַ,A.OCC_CODE AS ְҵ,A.COMPANY_DESC AS ������λ,"+
                                          " A.RELATION_CODE AS �뻼�߹�ϵ,A.CONTACTS_TEL AS ��λ�绰,"+
                                          " A.SPECIES_CODE AS ����,A.HEIGHT AS ���,A.WEIGHT AS ����,A.RESID_POST_CODE AS �ʱ�,A.MARRIAGE_CODE AS ����״��"+
                                          " FROM SYS_PATINFO A,SYS_CTZ D"+
                                          " WHERE A.CTZ1_CODE=D.CTZ_CODE(+)"+
                                          " AND A.MR_NO='"+this.getMrNo()+"'");
        TParm parm = new TParm(map);
        if(parm.getErrCode()<0){
            this.messageBox("ȡ�ò��˻�������ʧ�ܣ�");
            return;
        }
        String sbithDay="";
        StringTokenizer stk = new StringTokenizer(parm.getValue(
        "��������",0), "-");
        while (stk.hasMoreTokens())
            sbithDay += stk.nextToken();
        parm.addData("�����",this.getCaseNo());
        parm.addData("������",this.getMrNo());
        parm.addData("������1",this.getMrNo());
        parm.addData("����",this.getDeptDesc(Operator.getDept()));
        parm.addData("������",Operator.getName());
        if("ODI".equals(this.getSystemType())){
            parm.addData("סԺ",this.getSystemType());
        }
        if("ODO".equals(this.getSystemType())){
            parm.addData("����",this.getSystemType());
        }
        String names[] = parm.getNames();
        for(int i=0;i<names.length;i++){
            if("��������".equals(names[i])){
                emr.setMicroField(names[i],sbithDay);
                continue;
            }
            if("�Ա�".equals(names[i])){
              emr.setMicroField(names[i],getDictionary("SYS_SEX",parm.getValue(names[i],0)));
              continue;
            }
            if("����״��".equals(names[i])){
              emr.setMicroField(names[i],getDictionary("SYS_MARRIAGE",parm.getValue(names[i],0)));
              continue;
            }
            if("ְҵ".equals(names[i])){
              emr.setMicroField(names[i],getDictionary("SYS_OCCUPATION",parm.getValue(names[i],0)));
              continue;
            }
            if("�뻼�߹�ϵ".equals(names[i])){
              emr.setMicroField(names[i],getDictionary("SYS_RELATIONSHIP",parm.getValue(names[i],0)));
              continue;
            }
            if("����".equals(names[i])){
              emr.setMicroField(names[i],getDictionary("SYS_SPECIES",parm.getValue(names[i],0)));
              continue;
            }
            emr.setMicroField(names[i],parm.getValue(names[i],0));
        }
        //���º�
        emr.updateMicroField(true);
    }
    /**
     * �õ��ֵ���Ϣ
     * @param groupId String
     * @param id String
     * @return String
     */
    public String getDictionary(String groupId,String id){
        String result="";
        TParm parm = new TParm(this.getDBTool().select("SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='"+groupId+"' AND ID='"+id+"'"));
        result = parm.getValue("CHN_DESC",0);
        return result;
    }
    /**
     * �õ�����
     * @param deptCode String
     * @return String
     */
    public String getDeptDesc(String deptCode){
        TParm parm = new TParm(this.getDBTool().select("SELECT DEPT_CHN_DESC FROM SYS_DEPT WHERE DEPT_CODE='"+deptCode+"'"));
        return parm.getValue("DEPT_CHN_DESC",0);
    }
    /**
     * �õ�EMR�ļ�
     * @param parm TParm
     */
    public void getEmrFileData(TParm parm){
        this.setEmrChildParm(parm);
        //Ҫ�õ�ģ���ļ�·��
        String olinPath = parm.getValue("EMR_FILE_PATH");
        //���ر�����ļ�����Ŀ¼
        String emrLocal = TIOM_FileServer.getPath("EmrLocalTempFileName");
        TSocket socket = TIOM_FileServer.getSocket();
        byte bytEmrData[] = TIOM_FileServer.readFile(socket,olinPath);
        if(bytEmrData==null){
            this.messageBox("ģ���ļ������ڣ�");
            return;
        }
        File file = new File(emrLocal);
        file.mkdirs();
        try {
            FileTool.setByte(emrLocal+"EmrTemplet.emr", bytEmrData);
        }
        catch (IOException ex) {
            this.messageBox("������ʱ�ļ�ʧ�ܣ�");
            ex.printStackTrace();
            return;
        }
        //���ú�
        setMicroField();
        emr.fileOpen(emrLocal+"EmrTemplet.emr");
        file = new File(emrLocal+"EmrTemplet.emr");
        file.delete();
        //����EMR�ļ�ֻ��
        emr.setDocumentMode(emr.DOCUMENT_MODE_READ_ONLY);
        this.isReadOnly = true;
        this.fileNameOnly = "";
    }
    /**
     * ����
     */
    public void onSave(){
        TParm parm = this.getEmrChildParm();
        if (parm.getData("EMR_FILE_DATA") == null) {
            this.messageBox("û��Ҫ������ļ���");
            return;
        }
        //Ŀ¼���һ����Ŀ¼FILESERVER
        String rootName = TIOM_FileServer.getRoot();
        //���ر�����ļ�����Ŀ¼
        String emrLocal = TIOM_FileServer.getPath("EmrLocalTempFileName");
        File file = new File(emrLocal);
        file.mkdirs();
        emr.fileSaveAs(emrLocal+"EmrTemplet.emt",0);
        //�ļ������������·��
        String emrFileServerPath = TIOM_FileServer.getPath("EmrData");
        byte[] fileData=null;
        TSocket socket = TIOM_FileServer.getSocket();
        try {
            fileData = FileTool.getByte(emrLocal+"EmrTemplet.emt");
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return;
        }
        if(fileData==null){
           return;
        }
        //�õ�EMR�ļ���Ϣ
        TParm emrParm = this.getFileServerEmrName();
//        this.messageBox_(emrParm);
        //�õ�EMR�ļ���
        String emrName = emrParm.getValue("FILE_NAME");
//        this.messageBox(emrName);
        //����EMR·��
        String serverPath = rootName+emrFileServerPath+this.getAdmYear()+"\\"+this.admMouth+"\\"+this.getMrNo()+"\\"+emrName;
        //�����ļ�������
        boolean flg = TIOM_FileServer.writeFile(socket,serverPath,fileData);
        if(flg){
            if(this.saveEmrFile(emrParm,serverPath)){
                this.messageBox("����ɹ���");
//                this.messageBox_(emrParm);
                TParm action = new TParm();
                action.setData("EMR_TEMPLET_NAME",emrParm.getValue("EMR_TEMPLET_NAME"));
                action.setData("EMR_FILE_NAME", emrParm.getValue("FILE_NAME"));
                action.setData("EMR_FILE_PATH", serverPath);
                action.setData("EMR_FILE_DATA", emrParm);
                //����EMR�ļ�
                this.getEmrFileData(action);
                //������
                this.loadTree();
            }
            else{
                this.messageBox("����ʧ�ܣ�");
                TIOM_FileServer.deleteFile(socket,serverPath);
            }
        }else{
            this.messageBox("����ʧ�ܣ�");
        }
        file = new File(emrLocal+"EmrTemplet.emt");
        file.delete();
        //����EMR�ļ�ֻ��
        emr.setDocumentMode(emr.DOCUMENT_MODE_READ_ONLY);
        this.isReadOnly = true;
        this.fileNameOnly = "";
    }
    /**
     * ����EMR�ļ�
     * @param parm TParm
     */
    public boolean saveEmrFile(TParm parm,String filePath){
        boolean falg = true;
        parm.setData("CASE_NO",this.getCaseNo());
        parm.setData("MR_NO",this.getMrNo());
        parm.setData("IPD_NO",this.getIpdNo());
        parm.setData("FILE_PATH",filePath);
        parm.setData("DESIGN_NAME",parm.getValue("FILE_NAME"));
        parm.setData("DISPOSAC_FLG","N");
        parm.setData("CURRENT_USER",Operator.getID());
        parm.setData("CREATOR_USER",Operator.getID());
        parm.setData("OPT_USER",Operator.getID());
        parm.setData("OPT_DATE",this.getDBTool().getDBTime());
        parm.setData("OPT_TERM",Operator.getIP());
        TParm result = TIOM_AppServer.executeAction(actionName,"saveEmrFile", parm);
        if(result.getErrCode()<0)
            falg = false;
        return falg;
    }
    /**
     * �õ��ļ�������·��
     * @param rootPath String
     * @param fileServerPath String
     * @return String
     */
    public TParm getFileServerEmrName(){
        TParm emrParm =new TParm();
        String emrName = "";
        String indexStr = "01";
        TParm childParm = this.getEmrChildParm();
//        this.messageBox_("����"+childParm);
        TParm emrFileParm = (TParm)childParm.getData("EMR_FILE_DATA");
        String templetName = childParm.getValue("EMR_TEMPLET_NAME");
        TParm action = new TParm(this.getDBTool().select("SELECT NVL(MAX(FILE_SEQ)+1,1) AS MAXFILENO FROM EMR_FILE_INDEX WHERE CASE_NO='"+this.getCaseNo()+"'"));
        int index = action.getInt("MAXFILENO",0);
        if(index<10){
           indexStr = "0"+index;
        }else{
            indexStr = ""+index;
        }
        emrName = this.getCaseNo()+"_"+templetName+"_"+indexStr+".emr";
        emrParm.setData("FILE_SEQ",indexStr);
        emrParm.setData("FILE_NAME",emrName);
        emrParm.setData("CLASS_CODE",emrFileParm.getData("CLASS_CODE"));
        emrParm.setData("SUBCLASS_CODE",emrFileParm.getData("SUBCLASS_CODE"));
        emrParm.setData("EMR_TEMPLET_NAME",templetName);
        return emrParm;
    }
    /**
     * ���ڹر��¼�
     * @return boolean
     */
    public boolean onClosing(){
         if(!checkSave())
             return false;
         emr.closeOCX();
         return true;
     }
     /**
      * ����Ƿ񱣴�
      * @return boolean
      */
     public boolean checkSave(){
         if(emr==null)
             return true;
         if(this.fileNameOnly.length()>0&&!this.isReadOnly){
             if (messageBox("��ʾ��Ϣ", "�Ƿ񱣴�"+this.fileNameOnly+"�ļ�?", this.YES_NO_OPTION) != 0){

                 TParm parm = (TParm)this.getEmrChildParm().getData("EMR_FILE_DATA");
                 if(parm.getValue("FILE_SEQ").length()==0)
                     return true;
                 parm.setData("OPT_USER",Operator.getID());
                 parm.setData("OPT_TERM",Operator.getIP());
                 parm.setData("CURRENT_USER","");
                 TParm result = TIOM_AppServer.executeAction(actionName,"updateEmrCurrentUser",parm);
                 if(result.getErrCode()<0)
                     return false;
                 return true;
             }
             else{
                 //ɾ��ǰһ���ļ�
                 TParm childParm = (TParm)this.getEmrChildParm().getData(
                     "EMR_FILE_DATA");
                 //System.out.println("FILE_SEQ" + childParm.getValue("FILE_SEQ"));
                 //System.out.println("CASE_NO" + childParm.getValue("CASE_NO"));
                 childParm.setData("OPT_USER", Operator.getID());
                 childParm.setData("OPT_TERM", Operator.getIP());
                 childParm.setData("CURRENT_USER", "");
                 TParm result = TIOM_AppServer.executeAction(actionName,"updateEmrFile", childParm);

                 //����
                 this.onSave();
                 //�����ı༭�����
                 TParm parm1 = (TParm)this.getEmrChildParm().getData(
                     "EMR_FILE_DATA");
                 parm1.setData("OPT_USER", Operator.getID());
                 parm1.setData("OPT_TERM", Operator.getIP());
                 parm1.setData("CURRENT_USER", "");
//                 this.messageBox_("��"+parm1);
                 TParm result1 = TIOM_AppServer.executeAction(actionName,
                     "updateEmrCurrentUser", parm1);
                 if (result1.getErrCode() < 0)
                     return false;
             }
         }
         return true;
     }
    /**
     * �򿪲���(Ψһ)
     */
    public void onOpenMenu(){
        openChildDialog();
    }
    /**
     * ��������(����׷��)
     */
    public void onAddMenu(){
        openChildDialog();
    }
    /**
     * ɾ��
     */
    public void onDelete(){
        TParm parm = this.getEmrChildParm();
        if(parm.getData("EMR_FILE_DATA")==null){
            this.messageBox("û��Ҫɾ�����ļ���");
            return;
        }
        TParm childParm = (TParm)parm.getData("EMR_FILE_DATA");
        String creatFileUser = childParm.getValue("CREATOR_USER");
        //Ȩ�޼��
        if(!creatFileUser.equals(Operator.getID())){
            this.messageBox("��û��Ȩ��ɾ�����ļ�");
            return;
        }
        //System.out.println("FILE_SEQ"+childParm.getValue("FILE_SEQ"));
        //System.out.println("CASE_NO"+childParm.getValue("CASE_NO"));
        childParm.setData("OPT_USER",Operator.getID());
        childParm.setData("OPT_TERM",Operator.getIP());
        childParm.setData("CURRENT_USER","");
        TParm result = TIOM_AppServer.executeAction(actionName,"updateEmrFile",childParm);
        if(result.getErrCode()<0){
            this.messageBox("ɾ��ʧ�ܣ�");
            return;
        }
        this.messageBox("ɾ���ɹ���");
        emr.fileNew();
        //���¼�����
        this.loadTree();
        this.isReadOnly=true;
        this.fileNameOnly = "";
    }
    public static void main(String args[])
    {
        /*JFrame f = new JFrame();
        f.getRootPane().add(new EMRPad30());
        f.getRootPane().setLayout(new BorderLayout());
        f.setVisible(true);*/
        JavaHisDebug.TBuilder();

    }
    /**
     * �ж��Ƿ������ڱ༭
     * @return boolean
     */
    public boolean isEdit(){
        boolean falg = true;
        TParm action = (TParm)this.getEmrChildParm().getData("EMR_FILE_DATA");
        String caseNo = action.getValue("CASE_NO");
        String fileSeq = action.getValue("FILE_SEQ");
        TParm parm = new TParm(this.getDBTool().select("SELECT CURRENT_USER FROM EMR_FILE_INDEX WHERE CASE_NO='"+caseNo+"' AND FILE_SEQ='"+fileSeq+"'"));
//        this.messageBox_(parm);
        String currentUser = parm.getValue("CURRENT_USER",0);
        if(Operator.getID().equals(currentUser)||currentUser.length()==0)
            falg = false;
        return falg;
    }
    /**
     * �������ݿ��������
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool(){
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

    public String getEmrTypeCode() {
        return emrTypeCode;
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

    public void setEmrTypeCode(String emrTypeCode) {

        this.emrTypeCode = emrTypeCode;
    }
}
