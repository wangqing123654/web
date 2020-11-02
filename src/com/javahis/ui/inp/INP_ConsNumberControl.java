package com.javahis.ui.inp;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.util.Vector;

import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.util.Compare;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

/**
*
* <p>Title: �������ͳ�Ʊ�</p>
*
* <p>Description: �������ͳ�Ʊ�</p>
*
* <p>Copyright: Copyright (c) caoyong 20139016</p>
*
* <p>Company: JavaHis</p>
*
* @author caoyong
* @version 1.0
*/
public class INP_ConsNumberControl extends TControl {
	private TTable table;
	private int sortColumn = -1;
	private boolean ascending = false;
	private Compare compare = new Compare();
    private TParm result = new TParm();
	
	public void setResult(TParm result){
		this.result=result;
		
	}

	public TParm getResult(){
		return result;
	}
	/**
	 * �õ�TABLE����
	 * @param tagName
	 * @return
	 */
	 private TTable getTable(String tagName) {
			return (TTable) getComponent(tagName);
		}
	 
	 /**
	  * ��ʼ������
	  */
	 public void onInit(){
		 callFunction("UI|TABLE|addEventListener","TABLE->"+TTableEvent.CLICKED,this,"onTABLEClicked");//�����¼�
		 this.setValue("REGION_CODE", Operator.getRegion());
		 
		 TComboBox cboRegion = (TComboBox)this.getComponent("REGION_CODE");
	        cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(this.
	                getValueString("REGION_CODE")));
//	     // Ϊ����Ӽ�������Ϊ������׼����
//	        table=this.getTable("TABLE");
//			addListener(table);
		 
		// String sql="SELECT REGION_CODE,DEPT_CODE,DR_CODE,CASE_NO,PATH_NAME,SUMNUMBER FROM INP_CONS " ;
		 //TParm parm=new TParm();
	    	//this.initPage();
	        
	    }
	/**
	 * ��ѯ����
	 */
	public void onQuery(){
//		table=this.getTable("TABLE");
		if(table.getRowCount()>0){
			table.removeRowAll();
		}
		/*if("".equals(this.getValueString("REGION_CODE"))){
			this.messageBox("�������ѯ����");
			this.grabFocus("REGION_CODE");
			return;
		}
		if("".equals(this.getValueString("DEPT_CODE"))){
			this.messageBox("�������ѯ����");
			this.grabFocus("DEPT_CODE");
			return;
		}
		if("".equals(this.getValueString("DR_CODE"))){
			this.messageBox("�������ѯҽ��");
			this.grabFocus("DR_CODE");
			return;
		}*/
	 String sql="SELECT A.REGION_CODE,A.ACCEPT_DEPT_CODE,A.ACCEPT_DR_CODE,"+
	            "A.CASE_NO,B.IPD_NO AS IND_NO,C.PAT_NAME AS PATH_NAME "+
	            "FROM INP_CONS A,ADM_INP B,SYS_PATINFO C " +
	            "WHERE "+
	            "A.REGION_CODE='"+this.getValueString("REGION_CODE")+"' ";
	 
	 if(!"".equals(this.getValueString("DEPT_CODE"))&&this.getValueString("DEPT_CODE")!=null){//���Ҳ�ѯ
			sql+=" AND A.ACCEPT_DEPT_CODE='"+this.getValueString("DEPT_CODE")+"' ";
		}
	
	  if(!"".equals(this.getValueString("DR_CODE"))&&this.getValueString("DR_CODE")!=null){//ҽ����ѯ
			sql+=" AND A.ACCEPT_DR_CODE='"+this.getValueString("DR_CODE")+"'";
		}
	        sql+=" AND A.CASE_NO=B.CASE_NO AND B.MR_NO=C.MR_NO ORDER BY A.CASE_NO ";
	      TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
	      if(parm.getErrCode()<0){
	    	  this.messageBox("��ѯ��������");
	    	  return ;
	      }
	      if(parm.getCount()<=0){
	    	  this.messageBox("û�в�ѯ����");
	    	  return;
	      }
	    	 int count=0;
	    	 String regionCode=parm.getValue("REGION_CODE",0);//����
	    	 String deptCode=parm.getValue("ACCEPT_DEPT_CODE",0);//����
	    	 String drCode=parm.getValue("ACCEPT_DR_CODE",0);//;ҽ��
	    	 String pathName=parm.getValue("PATH_NAME",0);//��������
	    	 String caseNO=parm.getValue("CASE_NO",0);//�����
	    	 String indNO=parm.getValue("IND_NO",0);//סԺ��
	    	 
	    	 for(int i=0;i<parm.getCount("REGION_CODE");i++){//�ۼƲ����������
	    		 if(caseNO.equals(parm.getValue("CASE_NO",i))){
	    			 count++;
	    		 }else{
	    			 this.addResutl(regionCode,deptCode,drCode,indNO,pathName,count);
	    			  count=0;
	    			  caseNO=parm.getValue("CASE_NO",i);
	    			  regionCode=parm.getValue("REGION_CODE",i);
	    	    	  deptCode=parm.getValue("ACCEPT_DEPT_CODE",i);
	    	    	  drCode=parm.getValue("ACCEPT_DR_CODE",i);
	    	    	  indNO=parm.getValue("IND_NO",i);
	    	    	  pathName=parm.getValue("PATH_NAME",i);
	    			  if(caseNO.equals(parm.getValue("CASE_NO",i))){
	    			     count ++;
	    			  }
	    		    }
	    		 if(i==parm.getCount("REGION_CODE")-1){
	    			 this.addResutl(regionCode,deptCode,drCode,indNO,pathName,count); 
		    	  }
	    	     }
	    	 //ͳ���ܼ�
	    	      result.addData("REGION_CODE", "ͳ���ܼ�");
	 		      result.addData("DEPT_CODE", "");
	 		      result.addData("DR_CODE", "");
	 		      result.addData("IND_NO", "");
	 		      result.addData("PATH_NAME", "");
 		          result.addData("SUMNUMBER", parm.getCount());
	              table=this.getTable("TABLE");
	              table.setParmValue(this.getResult());
	      
	}
	/**
	 * �����¼�
	 */
	public void onTABLEClicked(int row){
		 TParm tparm = table.getParmValue().getRow(row);
		 this.setValue("REGION_CODE",tparm.getValue("REGION_CODE"));
		 this.setValue("DEPT_CODE",tparm.getValue("DEPT_CODE"));
		 this.setValue("DR_CODE",tparm.getValue("DR_CODE"));
	}
	/**
	 * ͳ�ƻ������
	 * @param regionCode
	 * @param deptCode
	 * @param drCode
	 * @param caseNO
	 * @param pathName
	 * @param count
	 */
	public void addResutl(String regionCode,String deptCode, String drCode,String indNo,String pathName, int count ){
		result.addData("REGION_CODE", regionCode);
		result.addData("DEPT_CODE", deptCode);
		result.addData("DR_CODE", drCode);
		result.addData("IND_NO", indNo);
		result.addData("PATH_NAME", pathName);
		result.addData("SUMNUMBER", count);
	}
	/**
	 * ���
	 */
	public void onClear() {
		String clearString="REGION_CODE;DEPT_CODE;DR_CODE";
		clearValue(clearString);
		this.onInit();
		table.removeRowAll();
	   
		
	}
	/**
	 * ��ӡ
	 */
	public void onPrint() {
		
		if(table.getRowCount()<=0){
			this.messageBox("û�д�ӡ����");
			return ;
		}
		Timestamp today = SystemTool.getInstance().getDate();
			TParm parm = new TParm();
			//TParm tableParm = table.getParmValue();
			TParm  tableParm = table.getShowParmValue();
			// ��ӡ����
			TParm date = new TParm();
			date.setData("title", "TEXT", "�������ͳ�Ʊ���");
			date.setData("user","TEXT",Operator.getName());
            date.setData("Unit","TEXT",tableParm.getValue("DEPT_CODE",0));			
            date.setData("Doctor","TEXT",tableParm.getValue("DR_CODE",0));			
            date.setData("date","TEXT",today.toString().substring(0,today.toString().lastIndexOf(".") ));			
			
			for (int i = 0; i < table.getRowCount(); i++) {
				parm.addData("REGION_CODE", tableParm.getValue("REGION_CODE", i));
				parm.addData("DEPT_CODE", tableParm.getValue("DEPT_CODE", i));
				parm.addData("DR_CODE", tableParm.getValue("DR_CODE", i));
				parm.addData("IND_NO", tableParm.getValue("IND_NO", i));
				parm.addData("PATH_NAME", tableParm.getValue("PATH_NAME", i));
				parm.addData("SUMNUMBER", tableParm.getValue("SUMNUMBER",i));

			}
			
				parm.setCount(parm.getCount("DEPT_CODE"));
				parm.addData("SYSTEM", "COLUMNS", "REGION_CODE");//����
				parm.addData("SYSTEM", "COLUMNS", "DEPT_CODE");//����
				parm.addData("SYSTEM", "COLUMNS", "DR_CODE");//ҽ��
				parm.addData("SYSTEM", "COLUMNS", "IND_NO");//סԺ��
				parm.addData("SYSTEM", "COLUMNS", "PATH_NAME");//��������
				parm.addData("SYSTEM", "COLUMNS", "SUMNUMBER");//�ۼƴ���
				date.setData("TABLE", parm.getData());
				this.openPrintWindow("%ROOT%\\config\\prt\\INP\\INP_ConsNumber.jhw", date);
			
	}
	
	/**
     * ���Excel
     */
    public void onExport() {
    	if(table.getRowCount()<=0){
    		this.messageBox("û�л������");
    		return;
    	}
        ExportExcelUtil.getInstance().exportExcel(table, "�������ͳ�Ʊ�");
    }

    
    /**
	 * �õ� Vector ֵ
	 * @param parm TParm
	 * @param group String
	 * @param names String
	 * @param size int
	 * @return Vector
	 */
	private Vector getVector(TParm parm, String group, String names, int size) {
		Vector data = new Vector();
		String nameArray[] = StringTool.parseLine(names, ";");
		if (nameArray.length == 0) {
			return data;
		}
		int count = parm.getCount(group, nameArray[0]);
		if (size > 0 && count > size)
			count = size;
		for (int i = 0; i < count; i++) {
			Vector row = new Vector();
			for (int j = 0; j < nameArray.length; j++) {
				row.add(parm.getData(group, nameArray[j], i));
			}
			data.add(row);
		}
		return data;
	}
	/**
	 * ת��parm�е���
	 * @param columnName String[]
	 * @param tblColumnName String
	 * @return int
	 */
	private int tranParmColIndex(String columnName[], String tblColumnName) {
		int index = 0;
		for (String tmp : columnName) {
			if (tmp.equalsIgnoreCase(tblColumnName)) {
				// System.out.println("tmp���");
				return index;
			}
			index++;
		}
		return index;
	}
	/**
	 * vectoryת��param
	 * @param vectorTable Vector
	 * @param parmTable TParm
	 * @param columnNames String
	 */
	    //================start===============
//	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
//			String columnNames) {
	private TParm cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames) {
    	//================end=================
		// ������->��
		// System.out.println("========names==========="+columnNames);
		String nameArray[] = StringTool.parseLine(columnNames, ";");
		// ������;
		for (Object row : vectorTable) {
			int rowsCount = ((Vector) row).size();
			for (int i = 0; i < rowsCount; i++) {
				Object data = ((Vector) row).get(i);
				parmTable.addData(nameArray[i], data);
			}
		}
		parmTable.setCount(vectorTable.size());
		//================start===============
		//table.setParmValue(parmTable);
		return parmTable;
		//================end=================
		// System.out.println("�����===="+parmTable);
	}
	/**
	 * �����������������
	 * 
	 * @param table
	 */
	public void addListener(final TTable table) {
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				// �������򷽷�;
				// ת�����û���������к͵ײ����ݵ��У�Ȼ���ж� f
				if (j == sortColumn) {
					ascending = !ascending;
				} else {
					ascending = true;
					sortColumn = j;
				}
				// �����parmֵһ��,
				// 1.ȡparamwֵ;
//				TParm tableData = CLP_BSCINFO.getParmValue();
				TParm tableData = table.getShowParmValue();
				//=====������ �� "�ܼ�"��  �����봦����======
//				TParm titRowParm=new TParm();//��¼������
//				titRowParm.addRowData(table.getParmValue(), 0);
				TParm totRowParm=new TParm();//��¼���ܼơ���
				totRowParm.addRowData(table.getParmValue(), tableData.getCount()-1);
				int rowCount=tableData.getCount();//���ݵ�������������С���к��ܼ��У�
//				tableData.removeRow(0);//ȥ����һ�У������У�
				tableData.removeRow(tableData.getCount()-1);//ȥ�����һ��(�ܼ���)
				//=========================================
				// 2.ת�� vector����, ��vector ;
				String columnName[] = tableData.getNames("Data");
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				Vector vct = getVector(tableData, "Data", strNames, 0);

				// 3.���ݵ������,��vector����
				// ������������;
				String tblColumnName = table.getParmMap(sortColumn);
				// ת��parm�е���
				int col = tranParmColIndex(columnName, tblColumnName);
				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// ��������vectorת��parm;
				cloneVectoryParam(vct, new TParm(), strNames);
				TParm lastResultParm=new TParm();//��¼���ս��
				TParm tmpParm=cloneVectoryParam(vct, new TParm(), strNames);
//				lastResultParm.addRowData(titRowParm, 0);//���������
				for (int k = 0; k < tmpParm.getCount(); k++) {
					lastResultParm.addRowData(tmpParm, k);//�����м�����
				}
				lastResultParm.addRowData(totRowParm, 0);//�����ܼ���
				lastResultParm.setCount(rowCount);
//				System.out.println("lastResultParm:\r\n"+lastResultParm+"\r\n\r\n");////////////////////
				table.setParmValue(lastResultParm);
				//getTMenuItem("save").setEnabled(false);

				// getTMenuItem("save").setEnabled(false);
			}
		});
	}
}
