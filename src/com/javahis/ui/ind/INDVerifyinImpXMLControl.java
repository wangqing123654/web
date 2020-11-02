package com.javahis.ui.ind;

import java.io.File;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.javahis.util.StringUtil;
import com.dongyang.ui.TTextFormat;

/**
 * <p>
 * Title: ���ö���Control
 * </p>
 *
 * <p>
 * Description: ���ö���Control
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: blurcore
 * </p>
 *
 * @author liyh 
 * @version 1.0
 */

public class INDVerifyinImpXMLControl
    extends TControl {

    // �������ݼ���
    private TParm resultParm;

    // ��������
    private String org_code;

    TTable tblPat;
    public INDVerifyinImpXMLControl() {
        super();
    }

    /**
     * ��ʼ������
     */
    public void onInit() {
        // ��ʼ��������
        initPage();
    }

    /**
     * ��ѯ����
     */
    public void onQuery() {
      
    }

    /**
     * ��շ���
     */
    public void onClear() {
        String clearStr =
            "PURORDER_NO;ORDER_CODE;ORDER_DESC;SELECT_ALL";
        this.clearValue(clearStr);
        getTextFormat("SUP_CODE").setText("");
        getTable("TABLE").removeRowAll();
    }

    /**
     * ���ط���
     */
    public void onReturn() {
        TTable table = getTable("TABLE");
        table.acceptText();
        if (table.getRowCount() < 0) {
            return;
        }
        TParm result = resultParm;
        int count = 0 ;
        TParm resuTParm = new TParm();
        for (int i = 0 ; i< result.getCount(); i++) {
            if ("Y".equals(table.getItemString(i, "SELECT_FLG"))) {
            	count++;
                resuTParm.addData("PATH", result.getData("PATH",i));
            }
        }
        
        if(count > 1){
        	this.messageBox("ÿ�ε���ֻ��ѡ��һ���ļ�");
        	return;
        }
        
        if (result == null) {
            this.messageBox("û�д�������");
            return;
        }
        setReturnValue(resuTParm);
//        System.out.println("--��ѯ����: " + resuTParm);
        this.closeWindow();
    }

    /**
     * ȫѡ��ѡ��ѡ���¼�
     */
    public void onCheckSelectAll() {
        TTable table = getTable("TABLE");
        table.acceptText();
        if (table.getRowCount() < 0) {
            getCheckBox("SELECT_ALL").setSelected(false);
            return;
        }
        for (int i = 0; i < table.getRowCount(); i++) {
            table.setItem(i, "SELECT_FLG", getValueString("SELECT_ALL"));
        }
    }
    
    /**
     * ���(TABLE)��ѡ��ı��¼�
     *
     * @param obj
     */
    public void onTableCheckBoxClicked(Object obj) {
    	  TTable table = getTable("TABLE");
        // ��õ����table����
        TTable tableDown = (TTable) obj;
        // ֻ��ִ�и÷�����ſ����ڹ���ƶ�ǰ���ܶ���Ч���������Ҫ��
        tableDown.acceptText();
        // ���ѡ�е���
        int column = tableDown.getSelectedColumn();
        // ���ѡ�е���
        int row = tableDown.getSelectedRow();
        table.setItem(row, "SELECT_FLG", "Y");
       
    }

	/**
	 * ����TABLE����¼�������TABLEֻ����һ������ѡ
	 */
	public void onTblPatClick() {
		boolean value = TCM_Transform.getBoolean(tblPat.getValueAt(
				tblPat.getSelectedRow(), 0));
		int allRow = tblPat.getRowCount();

		for (int i = 0; i < allRow; i++) {
			tblPat.setValueAt(false, i, 0);
			tblPat.getParmValue().setData("SELECT_FLG", i, false);
		}
		tblPat.setValueAt(true, tblPat.getSelectedRow(), 0);
		tblPat.getParmValue().setData("SELECT_FLG", tblPat.getSelectedRow(), true);
		// System.out.println("click parm======"+tblPat.getParmValue());

	}

    /**
     * ���ܷ���ֵ����
     *
     * @param tag
     * @param obj
     */
    public void popReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        String order_code = parm.getValue("ORDER_CODE");
        if (!StringUtil.isNullString(order_code))
            getTextField("ORDER_CODE").setValue(order_code);
        String order_desc = parm.getValue("ORDER_DESC");
        if (!StringUtil.isNullString(order_desc))
            getTextField("ORDER_DESC").setValue(order_desc);
    }

    /**
     * ��ʼ��������
     */
    private void initPage() {
    	//�õ��ϴ��ļ�·��
    	 String path = getFilePath();
         File filePath = new File(path);
         TParm parm = new TParm();
         if(filePath.isDirectory()){
        	 String[] fileList = filePath.list();
        	 if(null != fileList && fileList.length > 0){
        		 int i = 0;
        		 for (String s : fileList) {
					String delFilePath = path + "\\/" + s;
//					System.out.println("--delFilePath: "+delFilePath);
					File delFile = new File(delFilePath);
					String absolutePath = delFile.getAbsolutePath();
					parm.addData("SELECT_FLG", "N");
					parm.addData("NAME",delFile.getName());
					parm.addData("PATH",absolutePath);
/*					System.out.println("path:"+ delFile.getPath());
					System.out.println("absolutepath:"+ delFile.getAbsolutePath());
					System.out.println("name:"+ delFile.getName());*/
					i++;
				}
        		parm.setCount(i); 
        	 }
         }
//         System.out.println("parm:"+parm);
         tblPat = getTable("TABLE");
         tblPat.setParmValue(parm);
         resultParm = parm;
    }
    
    /**
     * �õ��ϴ��ļ�·��
     * @return string
     * @author liyh
     * @date 20120905
     */
    private String getFilePath(){
    	//Ĭ��·��
    	String filePath = "c:/upload";
    	String sql = "SELECT GROUP_ID,CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='IND_PUR_PATH' ";
    	TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
    	if(null != parm && parm.getCount()>0){
    		filePath = parm.getValue("CHN_DESC", 0).toLowerCase();
    	}
    	return filePath;
    }

    /**
     * �õ�ComboBox����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TComboBox getComboBox(String tagName) {
        return (TComboBox) getComponent(tagName);
    }

    /**
     * �õ�CheckBox����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TCheckBox getCheckBox(String tagName) {
        return (TCheckBox) getComponent(tagName);
    }

    /**
     * �õ�TextField����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TTextField getTextField(String tagName) {
        return (TTextField) getComponent(tagName);
    }

    /**
     * �õ�Table����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }

    /**
     * �õ�TextFormat����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TTextFormat getTextFormat(String tagName) {
        return (TTextFormat) getComponent(tagName);
    }

}
