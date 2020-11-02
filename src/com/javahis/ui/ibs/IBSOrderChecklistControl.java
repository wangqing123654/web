package com.javahis.ui.ibs;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Vector;

import jdo.ibs.IBSOrderChecklistTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.util.Compare;
import com.dongyang.util.StringTool;
/**
*
* <p>Title:ҽ���������</p>
*
* <p>Description: ҽ���������</p>
*
* <p>Copyright: Copyright (c) caoyong 20131022</p>
*
* <p>Company:��������Ŀ</p>
*
* @author caoyong
* @version 1.0
*/
public class IBSOrderChecklistControl extends TControl {
	int selectRow = -1;
    private int sortColumn = -1;
    private boolean ascending = false;
    private Compare compare = new Compare();
	/**
	 * �����
	 */
	private String caseNo;
	/**
	 * ��ʱ����
	 */
	private String temportaryFee;
	/**
	 * ���ڷ���
	 */
	private String longFee;
	/**
	 * ��Ժ��ҩ
	 */
	private String outFee;
	/**
	 * �̶�����
	 */
	private String fixedFee;
	/**
	 * �������
	 */
	private String subsifyFee;
	/**
	 * ����
	 */
	private String deptCode;
	
	private TParm oresult;
	public String getDeptCode() {
		return deptCode;
	}
	public void setDeptCode(String deptCode) {
		this.deptCode = deptCode;
	}
	public String getFixedFee() {
		return fixedFee;
	}
	public void setFixedFee(String fixedFee) {
		this.fixedFee = fixedFee;
	}
	public String getSubsifyFee() {
		return subsifyFee;
	}
	public void setSubsifyFee(String subsifyFee) {
		this.subsifyFee = subsifyFee;
	}
	public String getTemportaryFee() {
		return temportaryFee;
	}
	public void setTemportaryFee(String temportaryFee) {
		this.temportaryFee = temportaryFee;
	}
	public String getLongFee() {
		return longFee;
	}
	public void setLongFee(String longFee) {
		this.longFee = longFee;
	}
	public String getOutFee() {
		return outFee;
	}
	public void setOutFee(String outFee) {
		this.outFee = outFee;
	}
	public void setCaseNo(String caseNo) {
		this.caseNo = caseNo;
	}
	public String getCaseNo() {
		return caseNo;
	}
	/**
	 * TABLE1ȫ��ҽ�����
	 */
	private TTable table1;
	
	/**
	 * TABLE2 	���ݻ��ܱ�
	 */
	private TTable table2;
	
	/**
	 * TABLE3 ��ϸ��
	 */
	private TTable table3;
	
    private final int num=2;
	/**
	 * �õ�TABLE����
	 * @param tagName
	 * @return
	 */
	 private TTable getTable(String tagName) {
		 
			return (TTable) getComponent(tagName);
		}
	 
	/**
	 * ��ʼ��
	 */
	public void onInit(){
		callFunction("UI|TABLE1|addEventListener","TABLE1->"+TTableEvent.CLICKED,this,"onTABLEClicked");//table1�����¼�
		callFunction("UI|TABLE2|addEventListener","TABLE2->"+TTableEvent.CLICKED,this,"onTABLEClickedS");//table2�����¼�
		
		table1=this.getTable("TABLE1");
		table2=this.getTable("TABLE2");
		table3=this.getTable("TABLE3");
		//addListener(table1);
		addListener(table3);
		// ֻ��text���������������sys_fee������
		callFunction("UI|ORDER_CODE|setPopupMenuParameter", "ORDER_CODELIST",
				"%ROOT%\\config\\sys\\SYSFeePopup.x");
		// ���ܻش�ֵ
		callFunction("UI|ORDER_CODE|addEventListener",
				TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
		inPage();
		//onQueryall();
	}
	/**
	 * ��ʼ����ѯ��ֹʱ��
	 */
	public void inPage(){
		 Timestamp date = StringTool.getTimestamp(new Date());
		// ʱ����Ϊ1��
		// ��ʼ����ѯ����
		this.setValue("E_DATE", date.toString().substring(0, 10).replace('-','/')+ " 23:59:59");
		this.setValue("S_DATE", StringTool.rollDate(date, -1).toString().substring(0, 10).replace('-', '/')+ " 00:00:00");
	    TParm outsideParm = (TParm) this.getParameter();//���ܴ���Ĳ���
		String caseno=outsideParm.getValue("INW","CASE_NO");
		if(caseno.length()>0){
		this.setCaseNo(caseno);
		}
		this.setFixedFee("Y");
		this.setSubsifyFee("Y");
		this.setOutFee("Y");
		this.setLongFee("Y");
		this.setTemportaryFee("Y");
		
	}
	/**
	 * ��ѯ
	 */
	  public void onQuery(){
			//��ʱ����
			if("Y".equals(getValue("TEMPORTARY_FEE"))){
				this.setTemportaryFee("Y");
			}else{
				this.setTemportaryFee("N");
			}
			//���ڷ���
			if("Y".equals(getValue("LONG_FEE"))){
			   this.setLongFee("Y");
			}else{
				this.setLongFee("N");
			}
			//��Ժ��ҩ
			if("Y".equals(getValue("OUT_MEDICINE"))){
				this.setOutFee("Y");
			}else{
				this.setOutFee("N");
			}
		  
		  if("Y".equals(getValue("FIXED_FEE"))){
				this.setFixedFee("Y");
			}else{
				this.setFixedFee("N");
			}
			if("Y".equals(getValue("SUBSIDY_FEE"))){
			    this.setSubsifyFee("Y");
			}else{
				this.setSubsifyFee("N");
			}
		      onQueryall();
	  }
	  
	  public void  onQueryall(){
		     TParm result=new TParm();
			 TParm cparm=new TParm();
			 TParm eparm=new TParm();
			 TParm parm=new TParm();
			// TParm result=new TParm();
			 //����
			 
			/* if(i%2==0){
				cparm.setData("ORDERBY","ORDER BY EFF_DATE");
			 }else{
				cparm.setData("ORDERBY","ORDER BY EFF_DATE DESC"); 
			 }*/
			       cparm.setData("CASE_NO",this.getCaseNo());//�����
			       
			 //ҽ�������ѯ
			if(!"".equals(getValue("ORDER_CODE"))&&getValue("ORDER_CODE")!=null){
					cparm.setData("ORDER_CODE",getValue("ORDER_CODE"));
				}
			 
			//��ʱ����
			if("Y".equals(getValue("TEMPORTARY_FEE"))){
				parm.addData("RX_KIND","ST");
			}
			//���ڷ���
			if("Y".equals(getValue("LONG_FEE"))){
				parm.addData("RX_KIND","UD");
			}
			//��Ժ��ҩ
			if("Y".equals(getValue("OUT_MEDICINE"))){
				parm.addData("RX_KIND","DS");
			}
			//�̶�����
			if("Y".equals(getValue("FIXED_FEE"))){
				eparm.addData("DATA_TYPE","0");
			}
			//�������
			if("Y".equals(getValue("SUBSIDY_FEE"))){
				  eparm.addData("DATA_TYPE","1");
		    }
			//����
			if(this.getValueString("DEPT_CODE").length()>0){
				cparm.setData("DEPT_CODE",this.getValueString("DEPT_CODE"));
			}
			   this.setDeptCode(this.getValueString("DEPT_CODE"));//���Ҹ�ֵ
			   
			 if("Y".equals(this.getTemportaryFee())||"Y".equals(this.getLongFee())||"Y".equals(this.getOutFee())){//||!"".equals(getValue("ORDER_CODE"))
				 result=IBSOrderChecklistTool.getInstance().selectdata(cparm,parm);//��ѯ��ʱ�ɷ��ã����ڷ��ã���Ժ��ҩ
				 
			 }else{
				 result=new TParm();
			 }
			 
			 if("Y".equals(this.getFixedFee())||"Y".equals(this.getSubsifyFee())){//||!"".equals(getValue("ORDER_CODE"))
			 TParm fresult=IBSOrderChecklistTool.getInstance().selectdatatype(cparm,eparm);//��ѯ�̶�����
			 //(��ʱ����,���ڷ���,��Ժ��ҩ)�͹̶�����/������úϲ�
			 if(fresult.getCount()>0){
				 for(int j=0;j<fresult.getCount();j++){
					 result.addRowData(fresult, j);
				 }
			 }
			 
			 }
			 
			if(result.getErrCode()<0){
				this.messageBox("��ѯ��������");
				return;
			}
			if(result.getCount()<=0){
				this.messageBox("û�в�ѯ����");
				table1.removeRowAll();
				return;
			}
			    table1.setParmValue(result);
			
		  
	  }
	  /**
	   * ����table1����table2
	   * @param row
	   */
	  public void onTABLEClicked(int row){
		  
		     DecimalFormat df= new DecimalFormat("0.00");
		     table3.removeRowAll();
		     table2.removeRowAll();
		     
		     TParm tparm = table1.getParmValue().getRow(row);
		    // System.out.println("==========="+getParm(table1,row));
			    TParm tresult=IBSOrderChecklistTool.getInstance().selectdatall(getParm(table1,row));
			    
			    oresult=getOrderNO(table1,tparm.getValue("ORDER_CODE"),row);//ȡ��orderNO��ͬ��ҽ��
			    double tAmt=0;//�Ĳ��ܽ��
			    double lAmt=0;//����ҽ���ܽ��
			    double totAmt=0;//����ҽ���ܽ��
			    for(int i=0;i<oresult.getCount();i++){
			    	tAmt+=oresult.getDouble("TOT_AMT",i);
			    }
			         //this.messageBox(""+tAmt);
			         totAmt=tresult.getDouble("TOT_AMT",0);
			    //����ҽ���ܷ���
			    if(tparm.getValue("ORDERS_CODE").length()>0){
			    	
			    	TParm aresult=IBSOrderChecklistTool.getInstance().selecttotamt(getParm(table1,row));
			    	lAmt=aresult.getDouble("TOT_AMT",0);
			    	tresult.setData("TOT_AMT",0,df.format(lAmt+tAmt));
			    	tresult.setData("OWN_PRICE",0,aresult.getDouble("TOT_AMT",0));//����
			    }else{
			    	tresult.setData("TOT_AMT",0,df.format(totAmt+tAmt));
			    }
			   
			    if(tresult.getErrCode()<0){
					this.messageBox("��ѯ��������");
					return;
				}
			    
			    //this.messageBox(""+tresult.getCount());
			    for(int i=0;i<tresult.getCount();i++){
			    	tresult.addData("T_AMT",df.format(tAmt));
			    	tresult.addData("ORDER_DESC",tparm.getValue("ORDER_DESC"));
			    	tresult.addData("SPECIFICATION",tparm.getValue("SPECIFICATION"));
			    	tresult.addData("DATA_TYPE",tparm.getValue("DATA_TYPE"));
			    	
			    }
			    
				    table2.setParmValue(tresult);
			 
	  }
	  /**
	   * table2����table3
	   */
	  public void onTABLEClickedS(int row){
		        //TParm oresult=new TParm();
		        TParm tresult=new TParm();
		        TParm sresult=table2.getParmValue().getRow(row);
		         if(!"".equals(sresult.getValue("DATA_TYPE"))&&sresult.getValue("DATA_TYPE")!=null){//����Ʒ�
		        	  tresult=IBSOrderChecklistTool.getInstance().selectdatac(getParm(table2,row));
		         }else{//���ǲ���Ʒ�
		              //oresult=getOrderNO(table1,sresult.getValue("ORDER_CODE"),row);//ȡ��orderNO��ͬ��ҽ��
		        	
			          tresult=IBSOrderChecklistTool.getInstance().selectdatac(getParm(table2,row));
			          tresult.addParm(oresult);
		         }
			    if(tresult.getErrCode()<0){
					this.messageBox("��ѯ��������");
					return;
				}
				table3.setParmValue(tresult);
			    
	  }
	  
	  /**
	   * ��ѯ
	   */
	  public TParm getParm(TTable table, int row){
		    TParm parm=new TParm();
		    TParm tparm = table.getParmValue().getRow(row);
		  //��ѯ������
		    String sDate = getValueString("S_DATE");
		    sDate=sDate.replaceAll("-","").replaceAll(":","").replace(" ", "");
		    sDate=sDate.substring(0,sDate.lastIndexOf("."));
		    
		    //��ѯ������
			String eDate = getValueString("E_DATE");
			eDate=eDate.replaceAll("-","").replaceAll(":","").replace(" ", "");
			eDate=eDate.substring(0,eDate.lastIndexOf("."));
			//����ҽ���ܽ��
			if(tparm.getValue("FREQ_CODE").length()>0){
				parm.setData("FREQ_CODE", tparm.getValue("FREQ_CODE"));
				
			}
			
			
			if(tparm.getValue("ORDERS_CODE").length()>0){
				parm.setData("ORDERSET_CODE",tparm.getValue("ORDERS_CODE"));
			}
			//���Ҳ�ѯ
			if(this.getDeptCode().length()>0){
				parm.setData("DEPT_CODE", this.getDeptCode());//
			}
			/*if(tparm.getValue("ORDER_NO").length()>0){
				parm.setData("ORDER_NO",tparm.getValue("ORDER_NO"));
			}*/
			
			if(tparm.getValue("ORDERSET_CODE").length()>0){//����ҽ��ϸ
				parm.setData("ORDERSET_CODE",tparm.getValue("ORDERSET_CODE"));//����ҽ����ϸ
			}else{//�Ǽ���ҽ��
				parm.setData("ORDER_CODE",tparm.getValue("ORDER_CODE"));//������ҽ����ϸ
			}
				parm.setData("S_DATE", sDate);//
				parm.setData("E_DATE", eDate);
			    parm.setData("CASE_NO",this.getCaseNo());
		        return parm;
	  }
	  
	  /**
		 * ���ô��������б�ѡ��
		 * 
		 * @param tag
		 *            String
		 * @param obj
		 *            Object
		 */
		public void popReturn(String tag, Object obj) {
			TParm parm = (TParm) obj;
			this.setValue("ORDER_CODE", parm.getValue("ORDER_CODE"));
			this.setValue("ORDER_DESC", parm.getValue("ORDER_DESC"));
			// this.grabFocus("ORDER_DESC");
		}
		/**
		 * ���
		 */
		public void onClear(){
			  oresult=new TParm();
			  String clearString="ORDER_CODE;ORDER_DESC";
			  this.clearValue(clearString);
			  table1.removeRowAll();
			  table2.removeRowAll();
			  table3.removeRowAll();
			  this.setValue("TEMPORTARY_FEE", "N");
			  this.setValue("OUT_MEDICINE", "N");
			  this.setValue("FIXED_FEE", "N");
			  this.setValue("LONG_FEE", "N");
			  this.setValue("SUBSIDY_FEE", "N");
			  inPage();
		 }
		/**
		 * ȡ��order_no��ͬ��ҽ��
		 */
		public TParm getOrderNO(TTable Table,String orderCode, int row){
			TParm parm=IBSOrderChecklistTool.getInstance().selectdataorderno(getParm(Table,row));
			TParm sresult=new TParm();
			TParm result=new TParm();
			
			if(parm.getCount("ORDER_NO")>0){
				for(int i=0;i<parm.getCount("ORDER_NO");i++){
					 sresult=IBSOrderChecklistTool.getInstance().selectdatao(getParm(Table,row),parm.getValue("ORDER_NO",i),parm.getValue("ORDER_SEQ", i));
					 for(int j=0;j<sresult.getCount();j++){
						 result.addRowData(sresult, j); 
					 }
					
				}
			}
			return result;
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
					// System.out.println("+i+"+i);
					// System.out.println("+i+"+j);
					// �������򷽷�;
					// ת�����û���������к͵ײ����ݵ��У�Ȼ���ж� f
					if (j == sortColumn) {
						ascending = !ascending;
					} else {
						ascending = true;
						sortColumn = j;
					}
					// table.getModel().sort(ascending, sortColumn);

					// �����parmֵһ��,
					// 1.ȡparamwֵ;
					TParm tableData = table.getParmValue();
					// 2.ת�� vector����, ��vector ;
					String columnName[] = tableData.getNames("Data");
					String strNames = "";
					for (String tmp : columnName) {
						strNames += tmp + ";";
					}
					strNames = strNames.substring(0, strNames.length() - 1);
					// System.out.println("==strNames=="+strNames);
					Vector vct = getVector(tableData, "Data", strNames, 0);
					// System.out.println("==vct=="+vct);

					// 3.���ݵ������,��vector����
					// System.out.println("sortColumn===="+sortColumn);
					// ������������;
					String tblColumnName = table.getParmMap(sortColumn);
					// ת��parm�е���
					int col = tranParmColIndex(columnName, tblColumnName);
					// System.out.println("==col=="+col);

					compare.setDes(ascending);
					compare.setCol(col);
					java.util.Collections.sort(vct, compare);
					// ��������vectorת��parm;
					cloneVectoryParam(vct, new TParm(), strNames);

					// getTMenuItem("save").setEnabled(false);
				}
			});
			
		}
		
		/**
		 * �õ� Vector ֵ
		 * 
		 * @param group
		 *            String ����
		 * @param names
		 *            String "ID;NAME"
		 * @param size
		 *            int �������
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
		 * 
		 * @param columnName
		 * @param tblColumnName
		 * @return
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
		 */
		private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
				String columnNames) {
			//
			// System.out.println("===vectorTable==="+vectorTable);
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
			table3.setParmValue(parmTable);
			// System.out.println("�����===="+parmTable);

		}
		
      public void onSubsidy(){
    	  this.setValue("SUBSIDY_FEE", "N");
    	  this.setValue("FIXED_FEE", "N");
      }
      
      public void onSetOther(){
    	  this.setValue("TEMPORTARY_FEE", "N");
    	  this.setValue("LONG_FEE", "N");
    	  this.setValue("OUT_MEDICINE", "N");
      }
}
