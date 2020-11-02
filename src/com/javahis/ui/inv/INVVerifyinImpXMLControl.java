package com.javahis.ui.inv;

import java.io.File;

import org.apache.xmlbeans.StringEnumAbstractBase.Table;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;


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
 * Copyright: Copyright (c) 2013.7.31      
 * </p>
 *
 * <p>
 * Company: blurcore
 * </p>
 *
 * @author chenx 
 * @version 4.0
 */

public class INVVerifyinImpXMLControl
    extends TControl {

    // �������ݼ���
    private TParm resultParm;

    TTable tblPat;
    public INVVerifyinImpXMLControl() {
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
        
        if (count == 0) {
            this.messageBox("û�д�������");
            return;
        }
        setReturnValue(resuTParm);
        this.closeWindow();
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
					File delFile = new File(delFilePath);
					String absolutePath = delFile.getAbsolutePath();
					parm.addData("SELECT_FLG", "N");
					parm.addData("NAME",delFile.getName());
					parm.addData("PATH",absolutePath);
					i++;
				}
        		parm.setCount(i); 
        	 }
         }
         tblPat = getTable("TABLE");
         tblPat.setParmValue(parm);
         resultParm = parm;
    }
    
    /**
     * �õ��ϴ��ļ�·��
     * @return string
     * @author 
     * @date 
     */
    private String getFilePath(){
    	//Ĭ��·��
    	String filePath = "c:/upload";
    	return filePath;    
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
     * ���(TABLE)��ѡ��ı��¼�
     *
     * @param obj
     */
    public void onTableCheckBoxClicked(int  row) {
    	if(row<0)   
    		return ;
    	row = tblPat.getSelectedRow() ;
    	for(int i=0;i<tblPat.getRowCount();i++){
    		tblPat.setItem(i, "SELECT_FLG", "N");	
    	}
      tblPat.setItem(row, "SELECT_FLG", "Y");
       
    }
}

