package com.javahis.ui.database;

import com.dongyang.control.TControl;
import com.dongyang.tui.text.div.MVList;
import com.dongyang.tui.text.div.MV;
import com.dongyang.ui.TWord;
import com.dongyang.data.TParm;
import java.util.Vector;
import com.dongyang.ui.TTable;
import com.dongyang.tui.text.div.DIV;
import com.dongyang.tui.text.div.VText;
import java.awt.Color;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TComboBox;

/**
 * <p>Title: ��������Ԫ��</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2011.06.22</p>
 *
 * <p>Company: javahis</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class InsertHideElementDialogControl
    extends TControl {

    /**
     * ����ͼ������
     */
    public static final String UNVISIBLE_MV = "UNVISITABLE_ATTR";

    //��ǰͼ���б�
    private MVList mvList;

    /**
     * WORD����
     */
    private TWord word;
    /**
     * ����ͼ��
     */
    private MV hiddenMv;
    /**
     * ����Ԫ�ر�
     */
    private TTable hiddenElmTable;

    /**
     * �Ƿ��Ǻ�
     */
    private TCheckBox chkMacroFlg;

    /**
     * �����ı�
     */
    private TTextField txtWord;
    /**
     * Ԫ�ط�Ϊ�ؼ�;
     */
    private TTextFormat tfElementCate;
    /**
     * Ԫ�����ݿؼ�;
     */
    private TTextFormat tfElementData;

    private TComboBox  cmbMacro;

    public InsertHideElementDialogControl() {
    }

    /**
     * ��ʼ��
     */
    public void onInit() {
        super.onInit();
        TParm parm = (TParm) getParameter();
        word = (TWord) parm.getData("theWord", 0);
        //.getPageManager().getMVList().get(i).getName()
        mvList = (MVList) word.getPageManager().getMVList();
        //�Ƿ�����������Ե�ͼ��
        // MV unvisibleMV=mvList.get("UNVISIBLE_MV");
        //System.out.println("mvList size++" + mvList.size());
        chkMacroFlg = (TCheckBox) getComponent("chkMacroFlg");

        hiddenElmTable = (TTable) getComponent("Table");

        txtWord = (TTextField) getComponent("txtWord");

        tfElementCate = (TTextFormat) getComponent("ELEMENT_CATE");
        tfElementData = (TTextFormat) getComponent("ELEMENT_DATA");
        cmbMacro=(TComboBox)getComponent("cmb_macro");

        //ע���񵥻��¼�;
        /**callFunction("UI|Table|addEventListener",
                     "Table->" + TTableEvent.CLICKED, this, "onTableClicked");**/

        boolean isExist = false;
        for (int i = 0; i < mvList.size(); i++) {
            //System.out.println("mv name:" + mvList.get(i).getName());
            if (mvList.get(i).getName().equals(UNVISIBLE_MV)) {
                //����
                hiddenMv = mvList.get(i);
                isExist = true;
                break;
            }
        }
        //�У����г���Ӧ����������
        if (isExist) {
            onClickedVL();

            //DIV div = hiddenMv.get(1);
            //div.openProperty();

        }
        else {
            //�ޣ��򴴽�ͼ�㣻
            hiddenMv = new MV();
            hiddenMv.setName(UNVISIBLE_MV);
            hiddenMv.setVisible(false);
            mvList.add(hiddenMv);

        }


        /**addEventListener("TABLE->" + TTableEvent.CLICKED,
                         "onTableClicked");**/
    }
    /**
     * ���
     */
    public void onTableClicked(){
        //this.messageBox("come in");
         int row = hiddenElmTable.getSelectedRow();
         //this.messageBox("select elem"+hiddenElmTable.getParmValue().getData( "ELEMENT_CATE",row));
         tfElementCate.setValue(hiddenElmTable.getParmValue().getData( "ELEMENT_CATE",row));
         tfElementData.setValue(hiddenElmTable.getParmValue().getData( "ELEMENT_DATA",row));
         macroQuery();

         //ͼ��Ԫ��
         VText text = (VText)hiddenMv.get(row);
         //�Ǻ�
         if(text.isTextT()){
             chkMacroFlg.setSelected(true);
             txtWord.setText("");
             txtWord.setEnabled(false);
             cmbMacro.setValue(text.getMicroName());

         }else{
             chkMacroFlg.setSelected(false);
             txtWord.setEnabled(true);
             txtWord.setText(text.getText());
         }

         //chkMacroFlg

    }

    /**
     *ѡ���Ƿ��Ǻ�
     */
    public void onClickedChkMacro() {
        if (chkMacroFlg.isSelected()) {
            txtWord.setText("");
            txtWord.setEnabled(false);
        }
        else {
            txtWord.setEnabled(true);
        }

    }

    /**
     * ����Ԫ��
     */
    public void onSave() {
        //У��
        if (tfElementCate.getValue() == null ||
            tfElementCate.getValue().equals("")) {
            this.messageBox("��ѡ��Ԫ�ط���!");
            return;
        }

        if (tfElementData.getValue() == null ||
            tfElementData.getValue().equals("")) {
            this.messageBox("��ѡ��Ԫ������!");
            return;
        }
        if (!chkMacroFlg.isSelected()) {
            if (txtWord.getText().equals("")) {
                this.messageBox("���Ǻ꣬�������ı�����!");
                return;
            }
        //�Ǻ꣬��ѡ���
        }else{
            if(cmbMacro.isEnabled()&&cmbMacro.getValue().equals("")){
                this.messageBox("��ѡ���!");
                return;
            }

        }

        int row = hiddenElmTable.getSelectedRow();
        //this.messageBox("row++" + row);
        //����
        if (row == -1) {
            //����������Ԫ��
           //this.messageBox("����Ԫ��."+(String)tfElementData.getValue());
            VText text = hiddenMv.addText(100, 100, new Color(0, 0, 0));
            //����
            text.setName( (String) tfElementData.getValue());
            text.setGroupName((String)tfElementCate.getValue());

            //this.messageBox("����:"+(String)tfElementCate.getValue());
            //this.messageBox("������:"+cmbMacro.getValue());
            //�ı�
            if (chkMacroFlg.isSelected()) {
                //����
                text.setTextT(true);
                //���ú���;

                text.setMicroName(cmbMacro.getValue());

            }
            else {
                text.setTextT(false);
                text.setText(txtWord.getValue());
            }
            text.setVisible(false);
        //�޸�;
        }
        else {
            VText text = (VText)hiddenMv.get(row);
            //����
            text.setName( (String) tfElementData.getValue());
            text.setGroupName((String)tfElementCate.getValue());
            //�ı�
            if (chkMacroFlg.isSelected()) {
                //this.messageBox("chkMacroFlg===="+chkMacroFlg);
                //����
                text.setTextT(true);
                //���ú���;
                text.setMicroName(cmbMacro.getValue());
            }
            else {
                text.setTextT(false);
                text.setText(txtWord.getValue());
            }
            text.setVisible(false);


        }
        //ˢ�±��
        onClickedVL();
        this.onClear();
        this.messageBox("����ɹ�!");
        //����word����������;
    }

    /**
     * ɾ��Ԫ��
     */
    public void onDelete() {

        int row = hiddenElmTable.getSelectedRow();
        if (row == -1) {
            return;
        }
        DIV div = hiddenMv.get(row);
        if (messageBox("��ʾ��Ϣ", "�Ƿ�ɾ������Ԫ��" + div.getName() + "?", YES_NO_OPTION) ==
            1) {
            return;
        }
        hiddenMv.remove(div);
        hiddenElmTable.removeRow(row);
        if (hiddenElmTable.getRowCount() > 0) {
            hiddenElmTable.setSelectedRow(row >= hiddenElmTable.getRowCount() ?
                                          hiddenElmTable.getRowCount() - 1 :
                                          row);
        }
        mvList.update();
        div.DC();

    }

    /**
     * ���
     */
    public void onClear() {
        hiddenElmTable.clearSelection();
        tfElementCate.setValue("");
        tfElementData.setValue("");
        chkMacroFlg.setSelected(false);
        txtWord.setEnabled(true);
        txtWord.setText("");
        cmbMacro.setValue("");

    }

    /**
     * ����ͼ�㣬Ԫ������
     */
    public void onClickedVL() {

        TParm parm = new TParm();
        TParm elementParm = new TParm();
        if (hiddenMv.size() == 0) {
            parm.setData("ID", new Vector());
            parm.setData("SHOW", new Vector());
            parm.setData("NAME", new Vector());
            parm.setData("TYPE", new Vector());
            parm.setCount(0);
        }
        else {
            for (int i = 0; i < hiddenMv.size(); i++) {
                DIV div = hiddenMv.get(i);
                parm.addData("ID", i);
                parm.addData("SHOW", div.isVisible());
                //this.messageBox("ͨ������ȡ��" + div.getName());
                parm.addData("NAME", div.getName());
                parm.addData("TYPE", div.getType());
                //�ӿ���ȡ��Ӧ������Ԫ�أ�
                TParm theElem=this.dataElementByName(div.getName());
                //ID;ELEMENT_CATE;ELEMENT_DATA;DEFINE_DESC
                elementParm.addData("ID",i);
                elementParm.addData("ELEMENT_CATE",theElem.getData("GROUP_CODE",0));
                elementParm.addData("ELEMENT_DATA",theElem.getData("DATA_CODE",0));
                elementParm.addData("DEFINE_DESC",theElem.getData("DEFINE_DESC",0));

            }
            parm.setCount(hiddenMv.size());
        }
        hiddenElmTable.setParmValue(elementParm);
    }

    /**
     *���ѯ
     */
    public void macroQuery() {
        String elemCate = (String) tfElementCate.getValue();
        String elemData = (String) tfElementData.getValue();
        //ͨ��EMR_MICRO_CONVERT��ȡ�����ƣ�
       cmbMacro.setSQL("SELECT MICRO_NAME FROM EMR_MICRO_CONVERT WHERE DATA_ELEMENT_CODE='" + elemData + "'" );
       cmbMacro.retrieve();
        //�������0
        //this.messageBox("==count=="+cmbMacro.getDataStore().rowCount());
        //��ֵ�����chk,�̶�ֵ������;
        //���Ǻ�,
        if(cmbMacro.getDataStore().rowCount()==0){
            chkMacroFlg.setSelected(false);
            cmbMacro.setValue("");
            cmbMacro.setEnabled(false);
            txtWord.setEnabled(true);
        //�ж��������
        }else if(cmbMacro.getDataStore().rowCount()>1){
            chkMacroFlg.setSelected(true);
            cmbMacro.setEnabled(true);
            cmbMacro.setValue("");
            txtWord.setEnabled(false);
        //ֻ��һ��ֵ���
        }else{
            chkMacroFlg.setSelected(true);
            cmbMacro.setEnabled(true);
            cmbMacro.setValue("");
            cmbMacro.setSelectedIndex(1);
            txtWord.setEnabled(false);

        }



    }


    /**
     * ͨ��name�õ�����Ԫ��
     * @param name String
     * @return TParm
     */
    private TParm dataElementByName(String name){
         StringBuffer sb = new StringBuffer("SELECT GROUP_CODE,DATA_CODE,DEFINE_DESC FROM EMR_CLINICAL_DATAGROUP WHERE 1=1");
         sb.append(" AND DATA_CODE='"+name+"'");
         //System.out.println("======sql======"+sb.toString());
        TParm parm = new TParm(this.getDBTool().select(sb.toString()));
        return parm;
    }

    /**
     * �������ݿ��������
     * @return TJDODBTool
     */
    private TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }


}
