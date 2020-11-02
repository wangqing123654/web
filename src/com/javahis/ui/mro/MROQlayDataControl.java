package com.javahis.ui.mro;

import java.sql.Timestamp;
import jdo.sys.SYSRuleTool;
import jdo.sys.SystemTool;
import jdo.sys.Operator;
import jdo.mro.MROQlayControlMTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;
import com.dongyang.ui.TTreeNode;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.event.TTreeEvent;
import com.dongyang.ui.TFrame;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTree;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TCheckBox;
import com.dongyang.manager.TIOM_AppServer;
import com.javahis.util.ExportExcelUtil;
import jdo.mro.MROChrtvetstdTool;



/**
 * <p>Title: �ֶ��ʿ�</p>
 *
 * <p>Description: �ֶ��ʿ�</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author pangben 20110730
 * @version 1.0
 */
public class MROQlayDataControl extends TControl {
    private String MR_NO; //����������
    private String CASE_NO; //�����������
    private String type = "";//������ʾ���ͣ���Ժ����Ժ
    private String PAT_NAME; //��������
    private String OPEN_USER; //����������
    private String IPD_NO;//סԺ��
    private Timestamp ADM_DATE;//����ʱ��
    private String DEPT_CODE;//���� add by wanglong 20121127
    private String STATION_CODE;//���� add by wanglong 20121127
    private String OPT_USER; //������id
    private String TYPERESULT; //�ύ�ֶ�
    private String VS_DR_CODE; //����ҽ��  add by wanglong 20121105
    TParm data;//��ѯ���е�MROChrtvetstd������
    int selectRow = -1;//��ǰѡ�������
    private TParm parmValue; //��������з�֧���Ҳ����е�ȫ������
    private TParm result; //��ѯ�þ��ﲡ�������п۷���Ŀ
    private TTable table;
    /**
     * ����
     */
    private TTreeNode treeRoot;
    /**
     * ��Ź�����𹤾�
     */
    SYSRuleTool ruleTool;
    //�������֮����ʾ�����͵�����Ÿ��ڵ�
    private String id = null;
    TParm  selParm ;//�����ӽڵ��Ĭ��ֵ
    /**
     * �������ݷ���datastore���ڶ��������ݹ���
     */
    TDataStore treeDataStore = new TDataStore();
    TParm parmData = new TParm();//add by wanglong 20130819

    public void onInit() {
        super.onInit();
        TFrame tf=(TFrame)this.getComponent("UI");//add by wanglong 20121129
        tf.setSize(1104, 758);//��ʹ��openWindow()���ô˽���ʱ�����������������ô��ڴ�С��������ʾ��ȫ��
        Object obj = this.getParameter();
        if (obj != null) {
            parmData = (TParm) obj;
            MR_NO = parmData.getValue("MR_NO"); //��ȡ������
            CASE_NO = parmData.getValue("CASE_NO"); //��ȡ�������
            PAT_NAME = parmData.getValue("PAT_NAME"); //��ȡ�������
            type = parmData.getValue("TYPE"); //����Ȩ��
            OPEN_USER = parmData.getValue("OPEN_USER"); //����������
            OPT_USER = parmData.getValue("OPT_USER"); //������id
            IPD_NO = parmData.getValue("IPD_NO"); //סԺ��
            ADM_DATE = parmData.getTimestamp("ADM_DATE"); //����ʱ��
            DEPT_CODE = parmData.getValue("DEPT_CODE"); //����
            STATION_CODE = parmData.getValue("STATION_CODE"); //סԺ��
            TParm result = MROQlayControlMTool.getInstance().queryQlayControlSUM(parmData);
            if (result.getErrCode() < 0) {
                this.messageBox(result.getErrText());
                this.closeWindow();
                return;
            }
//            TYPERESULT = parmData.getValue("TYPERESULT"); //�ύ�ֶ�
            TYPERESULT = result.getValue("TYPERESULT", 0); //�ύ�ֶ�
            VS_DR_CODE = parmData.getValue("VS_DR_CODE"); //����ҽ��  add by wanglong 20121105
            data = MROChrtvetstdTool.getInstance().selectdata(""); //��ѯ���е�MROChrtvetstd������
            parmValue = new TParm(); //�ύ����ʱ����������
            showQlayM();//Ϊresult��ֵ
            getShowTParm();//���Ѿ��ʿص����ݷŵ�����У���ʾ���е��ʿ���Ŀ             
            if ("1".equals(TYPERESULT)) {// add by wanglong 20131111
                ((TCheckBox) getComponent("TYPERESULT")).setSelected(true);
                this.callFunction("UI|SELECTQUERYSTATUS|setEnabled", false);
                this.callFunction("UI|SELECTSTATUS|setEnabled", false);
                table = (TTable) callFunction("UI|Table|getThis");
                table.setLockColumns("all");
            }else{
                this.callFunction("UI|SELECTQUERYSTATUS|setEnabled", true);
                this.callFunction("UI|SELECTSTATUS|setEnabled", true);
                table = (TTable) callFunction("UI|Table|getThis");
                table.setLockColumns("0,1,3,4,5,6,7");
            }
            if (MR_NO.trim().length() > 0 && CASE_NO.trim().length() > 0) {
                ((TTable) getComponent("Table")).addEventListener("Table->" + TTableEvent.CLICKED, 
                        this, "onTableClicked");
                onInitSelectTree();// ��ʼ����
                addEventListener("TREE->" + TTreeEvent.CLICKED, "onTreeClicked");// ��tree��Ӽ����¼�
                table = (TTable) callFunction("UI|Table|getThis");
                //��ӱ��tableֵ�ı��¼�:�޸�״̬����˽����ֵ
                callFunction("UI|Table|addEventListener", TTableEvent.CHECK_BOX_CLICKED, this,
                             "onTableCheckBoxClicked"); // ��TABLEDEPT�е�CHECKBOX��������¼�
                this.addEventListener("Table->" + TTableEvent.CHANGE_VALUE,
                      "onTableChargeValue");//��ӵڶ������tableֵ�ı��¼�
                onCreatTree();// ����
                this.setValue("MR_NO", MR_NO);
                this.setValue("IPD_NO", IPD_NO);
                this.setValue("PAT_NAME", PAT_NAME);
                TParm parmValueM = MROQlayControlMTool.getInstance().queryQlayControlSUM(parmData);//���ܷ�
                this.setValue("RESULTPRICE", parmValueM.getDouble("SUMSCODE",0)+"");
            }
        }
    }
    
    /**
     * TABLE_VARIANCE�������¼���ֵ�ı��¼�
     * @param obj Object
     * @return boolean
     */
    public boolean onTableChargeValue(Object obj) {
        //�õ��ڵ�����,�洢��ǰ�ı���к�,�к�,����,��������Ϣ
        TTableNode node = (TTableNode) obj;
        if (node == null) return false;
        //����ı�Ľڵ����ݺ�ԭ����������ͬ�Ͳ����κ�����
        if (node.getValue().equals(node.getOldValue())) return false;
        //�õ�table�ϵ�parmmap������
        String columnName = node.getTable().getParmMap(node.getColumn());
        //�õ��ı����
        int row = node.getRow();
        //�õ���ǰ�ı�������
        String value = "" + node.getValue();
        //��������Ƹı���ƴ��1�Զ�����,����ԭ��˵������Ϊ��
        int rows = getRow("EXAMINE_CODE", table.getParmValue().getValue("EXAMINE_CODE", row));
       // TParm parm=table.getParmValue();
        //�˹��ʿ��ۼ���
        if ("DEDUCT_SCORECOUNT".equals(columnName)) {
            if ("Y".equals(table.getParmValue().getValue("FLG", row))) {
                this.messageBox("�Զ��ʿ�������޸�");
//                parm.setDataN(columnName,row,1);
//                table.setParmValue(parm);
//                table.acceptText();
            } else
                parmValue.setData("DEDUCT_SCORECOUNT", rows, value);
        }
      //  onRadionClick();
        return false;
    }
    
    /**
     * �������е��Զ��ʿ���Ϣ
     */
    public void showQlayM() {
        String sql = "SELECT DISTINCT A.CASE_NO, A.MR_NO, A.IPD_NO,"//modify by wanglong 20121120
                    + " A.EXAMINE_CODE,B.EXAMINE_DESC,A.EXAMINE_DATE,A.QUERYSTATUS,A.STATUS,B.SCORE,"
                    + " A.CHECK_USER,A.CHECK_DATE,A.CHECK_RANGE, C.USER_NAME,B.DESCRIPTION,B.URG_FLG,B.TYPE_CODE,B.CHECK_FLG, "
                    + " D.VS_CODE,D.DEDUCT_SCORECOUNT,D.REPLY_DTTM,D.REPLY_DR_CODE,D.REPLY_REMK "//modify by wanglong 20131111
                    + " FROM MRO_QLAYCONTROLM A, MRO_CHRTVETSTD B, SYS_OPERATOR C,MRO_CHRTVETREC D "
                    + " WHERE A.EXAMINE_CODE = B.EXAMINE_CODE "
                    + " AND A.CASE_NO = D.CASE_NO    "
                    + " AND A.EXAMINE_CODE = D.EXAMINE_CODE "
                    + " AND A.CHECK_USER = C.USER_ID "
                    + " AND A.CASE_NO = '" + CASE_NO + "' ORDER BY A.CHECK_DATE";
      //  System.out.println("====================�������е��Զ��ʿ���Ϣ=================="+sql);
        result = new TParm(TJDODBTool.getInstance().select(sql));
    }

    /**
     * ��ʼ����
     */
    public void onInitSelectTree() {
        treeRoot = (TTreeNode) callMessage("UI|TREE|getRoot");// �õ�����
        if (treeRoot == null)
            return;
        if (type.equals("TYPE_IN"))
            treeRoot.setText("��Ժ");// �����ڵ����������ʾ
        else
            treeRoot.setText("��Ժ");
        treeRoot.setType("Root");// �����ڵ㸳tag
        treeRoot.setID("");// ���ø��ڵ��id
        treeRoot.removeAllChildren();// ������нڵ������
        callMessage("UI|TREE|update");// ���������ʼ������
    }

    /**
     * ��ʼ�����ϵĽڵ�
     */
    public void onCreatTree() {
        StringBuffer sql = new StringBuffer();
        //��ʾ��˱�׼���ӽڵ�
        sql.append("SELECT RULE_TYPE, CATEGORY_CODE,"
                   + " CATEGORY_CHN_DESC, CATEGORY_ENG_DESC,"
                   + " PY1, PY2, SEQ, DESCRIPTION, DETAIL_FLG,"
                   + " OPT_USER, OPT_DATE, OPT_TERM"
                   + " FROM SYS_CATEGORY WHERE RULE_TYPE='MRO_CHRTVETSTD' AND LENGTH(CATEGORY_CODE)=3 ");
        if (type.equals("TYPE_IN"))
            sql.append(" AND CATEGORY_CODE LIKE '21%'");//��ʾ��Ժ���ӽڵ�����,���ձ��������ʾ������סԺΪ2��ͷ������
        else
            sql.append(" AND CATEGORY_CODE LIKE '22%'");//��ʾ��Ժ���ӽڵ�����
     //   System.out.println("=============��ʼ�����ϵĽڵ�==============="+sql);
        selParm = new TParm(TJDODBTool.getInstance().select(sql.toString()));
        treeDataStore.setSQL(sql.toString());// ��dataStore��ֵ
        if (treeDataStore.retrieve() <= 0)// �����dataStore���õ�������С��0
            return;
        ruleTool = new SYSRuleTool("MRO_CHRTVETSTD"); // ��������,������������׼����
        if (ruleTool.isLoad()) { // �����۽ڵ����:datastore���ڵ����,�ڵ���ʾ����,,�ڵ�����
            TTreeNode node[] = ruleTool.getTreeNode(treeDataStore, "CATEGORY_CODE", "CATEGORY_CHN_DESC", "Path", "SEQ");
            for (int i = 0; i < node.length; i++) { // ѭ����������ڵ�
                treeRoot.addSeq(node[i]);
            }
        }
        TTree tree = (TTree) callMessage("UI|TREE|getThis");// �õ������ϵ�������
        tree.update();// ������
        tree.setSelectNode(treeRoot);// ��������Ĭ��ѡ�нڵ�
    }

    /**
     * ������
     *
     * @param parm Object
     */
    public void onTreeClicked(Object parm) { // ������ť������
        TTreeNode node = (TTreeNode) parm;// �õ�������Ľڵ����
        ((TRadioButton)this.getComponent("RADIONSUM")).setSelected(true); //ȫ����ѡ��ťѡ��
        if (node == null)
            return;
        // �õ�table����
        if (node.getType().equals("Root")) {// �������������ĸ����
        } else { // �����Ĳ��Ǹ����
            id = node.getID();// �õ���ǰѡ�еĽڵ��idֵ
            String scode="";
            for(int i=0;i<selParm.getCount();i++ ){
                if(id.equals(selParm.getValue("CATEGORY_CODE",i))){
                    scode=selParm.getValue("DESCRIPTION",i);//��õ�ǰ�ڵ�ķ���ֵ
                    break;
                }
            }
            double score = 0;
            for (int i = 0; i < parmValue.getCount(); i++) {
                if (parmValue.getValue("EXAMINE_CODE", i).contains(id)
                        && parmValue.getValue("QUERYSTATUS", i).equals("1")
                        && parmValue.getValue("STATUS", i).equals("0")) {
                    score += parmValue.getDouble("SCORE", i);
                }
            }
            score = StringTool.round(score, 2);
            this.setValue("SUMPRICE", scode);//Ĭ�Ϸ�ֵ��ʾ
            this.setValue("SUBPRICE", "" + score);//��ǰ��Ŀ�۷���ʾ
            // ���ݹ�����������Tablet�ϵ�����
            TParm parmTemp= showTParm(id,"-1");//ɸѡ������ʾ
            table.setParmValue(parmTemp);
        }
    }

    /**
     * ��������
     */
    private void getShowTParm() {
        String date = StringTool.getString(SystemTool.getInstance().getDate(), "yyyy/MM/dd HH:mm:ss");
        for (int i = 0; i < data.getCount(); i++) {//data��¼���е��ʿ���Ŀ
            boolean isAuto = false;//�Ƿ�Ϊ�Զ��ʿ���
            for (int j = 0; j < result.getCount(); j++) {//resultΪ��ǰ���������п۷���Ŀ
                if (data.getValue("EXAMINE_CODE", i).equals(result.getValue("EXAMINE_CODE", j))) {
                    //�Զ��ʿص����ݺ��Ѿ���ӵ���Ŀ����
                    parmValue.addData("EXAMINE_DESC", result.getValue("EXAMINE_DESC", j)); //�����Ŀ����
                    parmValue.addData("EXAMINE_CODE", result.getValue("EXAMINE_CODE", j)); //��˱��   
                    parmValue.addData("EXAMINE_DATE", result.getValue("EXAMINE_DATE", j)); //���ʱ��
                    parmValue.addData("MR_NO", result.getValue("MR_NO", j)); //������
                    parmValue.addData("CASE_NO", result.getValue("CASE_NO", j)); //�����
                    parmValue.addData("IPD_NO", result.getValue("IPD_NO", j)); //סԺ��
                    parmValue.addData("USER_NAME", result.getValue("USER_NAME", j)); //������
                    parmValue.addData("CHECK_RANGE", result.getValue("CHECK_RANGE", j)); //��˷�Χ
                    parmValue.addData("CHECK_USER", result.getValue("CHECK_USER", j)); //������
                    parmValue.addData("CHECK_DATE", StringTool.getString(result.getTimestamp("CHECK_DATE", j), "yyyy/MM/dd HH:mm:ss"));
                    parmValue.addData("SCORE", result.getData("SCORE", j)); //����
                    parmValue.addData("DEDUCT_SCORECOUNT", result.getInt("DEDUCT_SCORECOUNT", j)); //����ͳ��
                    parmValue.addData("QUERYSTATUS", result.getValue("QUERYSTATUS", j)); //�Զ�ָ�ص�������ʾ�Ѳ�
                    parmValue.addData("STATUS", result.getValue("STATUS", j)); //��˽����ͨ����δͨ��
                    parmValue.addData("FLG", result.getValue("CHECK_FLG", j)); //ɸѡ��ʾ�������Զ�ָ�ء��˹�ָ��
                    parmValue.addData("VS_CODE", result.getValue("VS_CODE", j)); //����ҽ��
                    //add by wanglong 20131111
                    parmValue.addData("REPLY_DTTM", result.getData("REPLY_DTTM", j)); //�ظ�ʱ��
                    parmValue.addData("REPLY_DR_CODE", result.getValue("REPLY_DR_CODE", j)); //�ظ�ҽʦ
                    parmValue.addData("REPLY_REMK", result.getValue("REPLY_REMK", j)); //�ظ���ע
                    //add end
                    parmValue.addData("ISTRUE", "1"); //"�޸�"�������
                    if (result.getValue("QUERYSTATUS", j).equals("1"))
                        parmValue.addData("QUERYSTATUS_FLG", "1"); //��ѡ״̬��ʾ�Ѳ�
                    else
                        parmValue.addData("QUERYSTATUS_FLG", "0");
                    if (result.getValue("STATUS", j).equals("1"))
                        parmValue.addData("STATUS_FLG", "1"); //��ѡ��˽����ʾͨ��
                    else
                        parmValue.addData("STATUS_FLG", "0");
                    parmValue.addData("URG_FLG",null == result.getValue("URG_FLG", j)?"N":result.getValue("URG_FLG", j)); //������ע��
                    isAuto = true;
                    break;
                }
            }
            if (!isAuto) {
                //�˹��ʿص�����
                parmValue.addData("EXAMINE_DESC", data.getValue("EXAMINE_DESC", i)); //�����Ŀ����
                parmValue.addData("EXAMINE_CODE", data.getValue("EXAMINE_CODE", i)); //��˱��
                parmValue.addData("EXAMINE_DATE", StringTool.getString(SystemTool.getInstance().getDate(), "yyyyMMddHHmmss")); //���ʱ��
                parmValue.addData("MR_NO", MR_NO); //������
                parmValue.addData("CASE_NO", CASE_NO); //�����
                parmValue.addData("IPD_NO", IPD_NO); //סԺ��
                parmValue.addData("USER_NAME", OPEN_USER); //ҽ������
                parmValue.addData("CHECK_RANGE", data.getValue("CHECK_RANGE", i)); //��˷�Χ
                parmValue.addData("CHECK_USER", OPT_USER); //������
                parmValue.addData("CHECK_DATE", date);
                parmValue.addData("SCORE", data.getValue("SCORE", i)); //����
                parmValue.addData("DEDUCT_SCORECOUNT", "1"); //����ͳ��
                parmValue.addData("QUERYSTATUS", "0"); //��ʾ��δ�顢�Ѳ�
                parmValue.addData("STATUS", "0"); //��ʾ��ͨ����δͨ��
                parmValue.addData("FLG", data.getValue("CHECK_FLG", i)); //ɸѡ��ʾ�������Զ�ָ�ء��˹�ָ��
                parmValue.addData("VS_CODE", VS_DR_CODE); //����ҽ��
                //add by wanglong 20131111
                parmValue.addData("REPLY_DTTM", ""); //�ظ�ʱ��
                parmValue.addData("REPLY_DR_CODE", ""); //�ظ�ҽʦ
                parmValue.addData("REPLY_REMK", ""); //�ظ���ע
                //add end
                parmValue.addData("ISTRUE", "0"); //"���"�������
                parmValue.addData("QUERYSTATUS_FLG", "0"); //��ѯ��ѡ��
                parmValue.addData("STATUS_FLG", "0"); //��˸�ѡ��
                parmValue.addData("URG_FLG", null==data.getValue("URG_FLG", i)?"N":data.getValue("URG_FLG", i)); //������ע��
            }
            parmValue.addData("OPT_USER", Operator.getID()); //������
            parmValue.addData("OPT_TERM", Operator.getIP()); //����ʱ��
//            parmValue.addData("VS_DR_CODE", VS_DR_CODE); //����ҽ�� add by wanglong 20121105
        }
        parmValue.setCount(data.getCount());
//        System.out.println("============��������======================="+parmValue);
    }

    /**
     * ɸѡ��ʾ���Զ��ʿء��˹��ʿء�ȫ��ɸѡ
     */
    public void onRadionClick() {
        TParm parm = null;
        if (((TRadioButton)this.getComponent("RADIONQLAY")).isSelected()) {
            parm = showTParm(id,"Y");//�Զ��ʿ�
        } else if (((TRadioButton)this.getComponent("RADIONPOWER")).isSelected()) {
            parm = showTParm(id,"N");//�˹��ʿ�
        } else {
            parm = showTParm(id,"-1");//ȫ��
        }
        table.setParmValue(parm);
    }

    /**
     * ɸѡ����
     * @param id String
     * @return TParm
     */
    public TParm showTParm(String id,String flg) {
        TParm parm = new TParm();
        int count = 0;
        //��ʾ����Ŀ����������
        if("-1".equals(flg)){
            for(int i = 0; i < parmValue.getCount(); i++){
                if(parmValue.getValue("EXAMINE_CODE",i).contains(id)){
                    showParmTemp(parm, i);
                    count++;
                }
            }
        }
        //��ʾ�Զ��ʿػ��˹��ʿ�����
        else{
            for (int i = 0; i < parmValue.getCount(); i++) {
                //ɸѡ��ʾ�������Զ��ʿء��˹��ʿ�
                if (parmValue.getValue("FLG", i).equals(flg) && parmValue.getValue("EXAMINE_CODE",i).contains(id)) {
                    showParmTemp(parm, i);
                    count++;
                }
            }
        }
        parm.setCount(count);
        return parm;
    }
    
    /**
     * ɸѡ��������������ʾ�ڱ����
     */
    private void showParmTemp(TParm parm, int i) {
        parm.addData("EXAMINE_DESC", parmValue.getValue("EXAMINE_DESC", i)); //�����Ŀ����
        parm.addData("EXAMINE_CODE", parmValue.getValue("EXAMINE_CODE", i)); //��˱��
        parm.addData("EXAMINE_DATE", parmValue.getValue("EXAMINE_DATE", i)); //���ʱ��
        parm.addData("MR_NO", parmValue.getValue("MR_NO", i)); //������
        parm.addData("CASE_NO", parmValue.getValue("CASE_NO", i)); //�����
        parm.addData("IPD_NO", parmValue.getValue("IPD_NO", i)); //סԺ��
        parm.addData("SCORE", parmValue.getValue("SCORE", i)); //����
        parm.addData("DEDUCT_SCORECOUNT", parmValue.getInt("DEDUCT_SCORECOUNT", i)); //����ͳ��
        parm.addData("USER_NAME", parmValue.getValue("USER_NAME", i)); //ҽ������
        parm.addData("QUERYSTATUS", parmValue.getValue("QUERYSTATUS", i)); //��ʾ��δ�顢�Ѳ�
        parm.addData("STATUS", parmValue.getValue("STATUS", i)); //��ʾ��ͨ����δͨ��
        parm.addData("FLG", parmValue.getValue("FLG", i)); //ɸѡ��ʾ�������Զ�ָ�ء��˹�ָ��
        parm.addData("QUERYSTATUS_FLG", parmValue.getValue("QUERYSTATUS_FLG", i)); //��ѯ��ѡ��
        parm.addData("STATUS_FLG", parmValue.getValue("STATUS_FLG", i)); //��˸�ѡ��
        parm.addData("CHECK_RANGE", parmValue.getValue("CHECK_RANGE", i));//��˷�Χ
        parm.addData("CHECK_USER", parmValue.getValue("CHECK_USER", i));//�����Ա
        parm.addData("CHECK_DATE", parmValue.getValue("CHECK_DATE", i));
        parm.addData("OPT_USER", parmValue.getValue("OPT_USER", i));
        parm.addData("OPT_TERM", parmValue.getValue("OPT_TERM", i));
        parm.addData("ISTRUE", parmValue.getValue("ISTRUE", i)); //�������޸ġ����
        parm.addData("URG_FLG", parmValue.getValue("URG_FLG", i)); //������ע��
    }
    
    /**
     * ���ֵ�ı��¼����޸�״̬����˽����ֵ
     * @param obj Object
     * @return boolean
     */
    public void onTableCheckBoxClicked(Object obj) {
        //�õ��ڵ�����,�洢��ǰ�ı���к�,�к�,����,��������Ϣ
        table.acceptText();
        TTable node = (TTable) obj;
        if (node == null)
            return;
        //����ı�Ľڵ����ݺ�ԭ����������ͬ�Ͳ����κ�����
        int column = node.getSelectedColumn();
        int selectRow = node.getSelectedRow();
        int row = getRow("EXAMINE_CODE", node.getParmValue().getValue("EXAMINE_CODE", selectRow));
        //�õ���ǰ�ı�������
        String value = null;
        //�õ�table�ϵ�parmmap������
        String columnName = node.getParmMap(column);
        //����Ǽ��״̬��ѡ��ı�
        if ("QUERYSTATUS_FLG".equals(columnName)) {
            value = parmValue.getValue("STATUS_FLG", row);
            if("1".equals(value) || "Y".equals(value)){
                this.messageBox("��ͨ����˵���Ŀ������ȡ���Ѳ�״̬");
                parmValue.setData("QUERYSTATUS_FLG", row,"Y");
                onRadionClick();
                return ;
            }
            value = getStatusValue("QUERYSTATUS_FLG", node, selectRow);
            parmValue.setData("QUERYSTATUS", row, value);//�������м��״̬�ı�ֵ
            parmValue.setData("QUERYSTATUS_FLG", row, node.getParmValue().getValue(columnName, selectRow));
        }
        //����Ǽ��״̬��ѡ��ı�
        if ("STATUS_FLG".equals(columnName)) {
            value = getStatusValue("STATUS_FLG", node, selectRow);
            parmValue.setData("STATUS", row, value);//�������м��״̬�ı�ֵ
            parmValue.setData("STATUS_FLG", row, node.getParmValue().getValue(columnName, selectRow));
            parmValue.setData("QUERYSTATUS", row, value); //�������м��״̬�ı�ֵ
            parmValue.setData("QUERYSTATUS_FLG", row, node.getParmValue().getValue(columnName, selectRow));
        }
        onRadionClick();
        return;
    }

    private int getRow(String colName, String code) {
        int rowCount = parmValue.getCount();
        for (int i = 0; i < rowCount; i++) {
            if (!parmValue.getValue(colName, i).equals(code))
                continue;
            return i;
        }
        return rowCount;
    }

    /**
     * �޸���ֵ
     * @param columnName String
     * @param node TTable
     * @param row int
     * @return String
     */
    private String getStatusValue(String columnName, TTable node, int row) {
        String value = node.getParmValue().getValue(columnName, row);
        value = value.equals("Y") ? "1" : "0";
        return value;
    }

    /**
     * ���ͨ��
     */
    public void onSave() {
//        TParm parm = table.getParmValue();
//        if (parm.getCount() <= 0) {
//            this.messageBox("��ѡ��Ҫ�޸ĵ�����");
//            return;
//        }
        TParm parmValues = new TParm();
        // �ύѡ��
        TParm parmr = new TParm();
        parmr.setData("CASE_NO", CASE_NO);
        parmr.setData("MR_NO", MR_NO);
        parmr = MROQlayControlMTool.getInstance().queryQlayControlSUM(parmr);//��MRO_RECORD�в�ѯ�Ƿ���ɣ��Լ��ܷ�����Ϣ
        //�����Ƿ��Ѿ��ύ���
        if (TYPERESULT.equals("0") && !getCheckBox("TYPERESULT").isSelected()) {
            if ("1".equals(parmr.getValue("TYPERESULT", 0))) {
                this.messageBox("�ύ״̬�쳣�����˳������ؽ����ٽ��в���");
                this.closeWindow();
                return;
            }else{
                parmValues.setData("TYPE", "0"); // ���ύ
            }
        }
        if (TYPERESULT.equals("0") && getCheckBox("TYPERESULT").isSelected()) {// �ύ��ʾ
            if (!"1".equals(parmr.getValue("TYPERESULT", 0))) {
                if (this.messageBox("��ʾ", "�Ƿ�ȷ���Ѿ���ɴ˲����������ʿ���Ŀ,�ύ�󽫲������޸�", 2) == 0) {
                    parmValues.setData("TYPE", "1"); // �ύ�ֶ�
                } else return;
            } else {
                parmValues.setData("TYPE", "1"); // �ύ�ֶ�
            }
        }
        if (TYPERESULT.equals("1") && getCheckBox("TYPERESULT").isSelected()) {// �ύ��ʾ
            parmValues.setData("TYPE", "1"); // �ύ�ֶ�
        } else if (TYPERESULT.equals("1") && !getCheckBox("TYPERESULT").isSelected()) {// ֻ�������ݲ��ύ
            parmValues.setData("TYPE", "0"); // ���ύ
        }
        parmValues.setData("SCODEPARM", parmValue.getData());//��ǰ�����������ʿ���Ϣ
        parmValues.setData("selPrice", selParm.getData());//���ÿ����֧���ܷ���
        TParm result = TIOM_AppServer.executeAction("action.mro.MROQlayDataAction", "saveQlayControlm", parmValues);
        if (result == null || !result.getErrText().equals("")) {// �����ж�
            this.messageBox("����ʧ��" + " , " + result.getErrText());
            return;
        }
        this.setValue("RESULTPRICE", result.getValue("RESULTPRICE"));
        this.messageBox("P0001");//����ɹ�
        table.setParmValue(new TParm());
        this.clearValue("TYPERESULT;SELECTQUERYSTATUS;SELECTSTATUS");
        ((TRadioButton) this.getComponent("RADIONSUM")).setSelected(true); // ȫ����ѡ��ťѡ��
        this.setValue("SUMPRICE", "0");
        this.setValue("SUBPRICE", "0");
        onInit();//add by wanglong 20131201
        parmData.runListener("onReturnContent", CASE_NO + ";" + result.getValue("RESULTPRICE") + ";" + result.getValue("TYPERESULT"));//
//        parmValue = new TParm(); //�ύ�����ǵ���������
//        showQlayM();
//        //���Ѿ��ʿص����ݷŵ�����У���ʾ���е��ʿ���Ŀ
//        getShowTParm();
//        //========= add by wanglong 20130819
//        TParm caseParm = new TParm();
//        caseParm.addData("CASE_NO", CASE_NO);
//        result =
//                TIOM_AppServer.executeAction("action.mro.MROQlayControlAction", "updateScore", caseParm);//���µ÷�
//        if (result.getErrCode() != 0) {
//            this.messageBox(result.getErrText());
//            return;
//        }
//        result = MROQlayControlMTool.getInstance().queryQlayControlSUM(caseParm.getRow(0));//��ѯ�÷�
//        if(result.getErrCode() != 0){
//            this.messageBox(result.getErrText());
//            return;
//        }
//        this.setValue("RESULTPRICE", result.getValue("SUMSCODE", 0));//��ʾ�÷ֵ�����
//        parmData.runListener("onReturnContent", CASE_NO+";"+ result.getValue("SUMSCODE", 0) );
        //========= add end
    }

    /**
     * ������д
     */
    public void onEmrRead() {
        TParm parm = new TParm();
        parm.setData("SYSTEM_TYPE", "ODI");
        parm.setData("ADM_TYPE", "I");
        parm.setData("CASE_NO", CASE_NO);
        parm.setData("PAT_NAME", PAT_NAME);
        parm.setData("MR_NO", MR_NO);
        parm.setData("IPD_NO", IPD_NO);
        parm.setData("ADM_DATE", ADM_DATE);
        parm.setData("DEPT_CODE", DEPT_CODE);
        parm.setData("STATION_CODE", STATION_CODE);
        //parm.setData("STYLETYPE", "1");//�����Ƿ���������
        parm.setData("RULETYPE", "1");
        parm.setData("WRITE_TYPE", "ODIR");// д�����͡���� �� �൱�ڡ�ֻ����
        parm.setData("EMR_DATA_LIST", new TParm());
        parm.addListener("EMR_LISTENER", this, "emrListener");
        parm.addListener("EMR_SAVE_LISTENER", this, "emrSaveListener");
        //System.out.println("=====================�����鿴============="+parm);
        openWindow("%ROOT%\\config\\emr\\TEmrWordUI.x", parm);
    }

    
    /**
    * ״̬��ѡ��ȫѡ
    */
   public void selectQueryAll(){
       selectTemp("SELECTQUERYSTATUS","QUERYSTATUS_FLG","QUERYSTATUS");
   }
   
   /**
    * ��˽����ѡ��ȫѡ
    */
   public void selectStatusAll(){
       selectTemp("SELECTSTATUS","STATUS_FLG","STATUS");
       if(getCheckBox("SELECTSTATUS").isSelected())
           getCheckBox("SELECTQUERYSTATUS").setSelected(true);
       selectTemp("SELECTQUERYSTATUS","QUERYSTATUS_FLG","QUERYSTATUS");
   }
   
   private void selectTemp(String checkName,String flgName,String name){
       String flg = "N";
       String flgValue="0";
       if (getCheckBox(checkName).isSelected()) {
           flg = "Y";
           flgValue="1";
       } else {
           flg = "N";
           flgValue="0";
       }
       for (int i = 0; i < table.getRowCount(); i++) {
           int row = getRow("EXAMINE_CODE", table.getParmValue().getValue("EXAMINE_CODE", i));
           parmValue.setData(flgName, row, flg);
           parmValue.setData(name, row, flgValue);
       }
       onRadionClick();
   }
   
   /**
    * �õ�CheckBox����
    *
    * @param tagName Ԫ��TAG����
    * @return
    */
   private TCheckBox getCheckBox(String tagName) {
       return (TCheckBox) getComponent(tagName);
   }
   
   /**
    * ���Excel
    */
   public void onExport() {//add by wanglong 20130819
       if (table.getRowCount()<1) {
           return;
       }
       ExportExcelUtil.getInstance().exportExcel(table, "�ֶ��ʿ���Ŀ�嵥");
   }
   
    
}
