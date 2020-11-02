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
* <p>Title:医嘱费用审核</p>
*
* <p>Description: 医嘱费用审核</p>
*
* <p>Copyright: Copyright (c) caoyong 20131022</p>
*
* <p>Company:爱育华项目</p>
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
	 * 就诊号
	 */
	private String caseNo;
	/**
	 * 临时费用
	 */
	private String temportaryFee;
	/**
	 * 长期费用
	 */
	private String longFee;
	/**
	 * 出院带药
	 */
	private String outFee;
	/**
	 * 固定费用
	 */
	private String fixedFee;
	/**
	 * 补充费用
	 */
	private String subsifyFee;
	/**
	 * 科室
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
	 * TABLE1全部医嘱表格
	 */
	private TTable table1;
	
	/**
	 * TABLE2 	数据汇总表
	 */
	private TTable table2;
	
	/**
	 * TABLE3 明细表
	 */
	private TTable table3;
	
    private final int num=2;
	/**
	 * 得到TABLE对象
	 * @param tagName
	 * @return
	 */
	 private TTable getTable(String tagName) {
		 
			return (TTable) getComponent(tagName);
		}
	 
	/**
	 * 初始化
	 */
	public void onInit(){
		callFunction("UI|TABLE1|addEventListener","TABLE1->"+TTableEvent.CLICKED,this,"onTABLEClicked");//table1单击事件
		callFunction("UI|TABLE2|addEventListener","TABLE2->"+TTableEvent.CLICKED,this,"onTABLEClickedS");//table2单击事件
		
		table1=this.getTable("TABLE1");
		table2=this.getTable("TABLE2");
		table3=this.getTable("TABLE3");
		//addListener(table1);
		addListener(table3);
		// 只有text有这个方法，调用sys_fee弹出框
		callFunction("UI|ORDER_CODE|setPopupMenuParameter", "ORDER_CODELIST",
				"%ROOT%\\config\\sys\\SYSFeePopup.x");
		// 接受回传值
		callFunction("UI|ORDER_CODE|addEventListener",
				TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
		inPage();
		//onQueryall();
	}
	/**
	 * 初始化查询起止时间
	 */
	public void inPage(){
		 Timestamp date = StringTool.getTimestamp(new Date());
		// 时间间隔为1天
		// 初始化查询区间
		this.setValue("E_DATE", date.toString().substring(0, 10).replace('-','/')+ " 23:59:59");
		this.setValue("S_DATE", StringTool.rollDate(date, -1).toString().substring(0, 10).replace('-', '/')+ " 00:00:00");
	    TParm outsideParm = (TParm) this.getParameter();//接受传入的参数
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
	 * 查询
	 */
	  public void onQuery(){
			//临时费用
			if("Y".equals(getValue("TEMPORTARY_FEE"))){
				this.setTemportaryFee("Y");
			}else{
				this.setTemportaryFee("N");
			}
			//长期费用
			if("Y".equals(getValue("LONG_FEE"))){
			   this.setLongFee("Y");
			}else{
				this.setLongFee("N");
			}
			//出院带药
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
			 //排列
			 
			/* if(i%2==0){
				cparm.setData("ORDERBY","ORDER BY EFF_DATE");
			 }else{
				cparm.setData("ORDERBY","ORDER BY EFF_DATE DESC"); 
			 }*/
			       cparm.setData("CASE_NO",this.getCaseNo());//就诊号
			       
			 //医嘱代码查询
			if(!"".equals(getValue("ORDER_CODE"))&&getValue("ORDER_CODE")!=null){
					cparm.setData("ORDER_CODE",getValue("ORDER_CODE"));
				}
			 
			//临时费用
			if("Y".equals(getValue("TEMPORTARY_FEE"))){
				parm.addData("RX_KIND","ST");
			}
			//长期费用
			if("Y".equals(getValue("LONG_FEE"))){
				parm.addData("RX_KIND","UD");
			}
			//出院带药
			if("Y".equals(getValue("OUT_MEDICINE"))){
				parm.addData("RX_KIND","DS");
			}
			//固定费用
			if("Y".equals(getValue("FIXED_FEE"))){
				eparm.addData("DATA_TYPE","0");
			}
			//补充费用
			if("Y".equals(getValue("SUBSIDY_FEE"))){
				  eparm.addData("DATA_TYPE","1");
		    }
			//科室
			if(this.getValueString("DEPT_CODE").length()>0){
				cparm.setData("DEPT_CODE",this.getValueString("DEPT_CODE"));
			}
			   this.setDeptCode(this.getValueString("DEPT_CODE"));//科室赋值
			   
			 if("Y".equals(this.getTemportaryFee())||"Y".equals(this.getLongFee())||"Y".equals(this.getOutFee())){//||!"".equals(getValue("ORDER_CODE"))
				 result=IBSOrderChecklistTool.getInstance().selectdata(cparm,parm);//查询临时飞费用，长期费用，出院带药
				 
			 }else{
				 result=new TParm();
			 }
			 
			 if("Y".equals(this.getFixedFee())||"Y".equals(this.getSubsifyFee())){//||!"".equals(getValue("ORDER_CODE"))
			 TParm fresult=IBSOrderChecklistTool.getInstance().selectdatatype(cparm,eparm);//查询固定费用
			 //(临时费用,长期费用,出院带药)和固定费用/补充费用合并
			 if(fresult.getCount()>0){
				 for(int j=0;j<fresult.getCount();j++){
					 result.addRowData(fresult, j);
				 }
			 }
			 
			 }
			 
			if(result.getErrCode()<0){
				this.messageBox("查询出现问题");
				return;
			}
			if(result.getCount()<=0){
				this.messageBox("没有查询数据");
				table1.removeRowAll();
				return;
			}
			    table1.setParmValue(result);
			
		  
	  }
	  /**
	   * 监听table1关联table2
	   * @param row
	   */
	  public void onTABLEClicked(int row){
		  
		     DecimalFormat df= new DecimalFormat("0.00");
		     table3.removeRowAll();
		     table2.removeRowAll();
		     
		     TParm tparm = table1.getParmValue().getRow(row);
		    // System.out.println("==========="+getParm(table1,row));
			    TParm tresult=IBSOrderChecklistTool.getInstance().selectdatall(getParm(table1,row));
			    
			    oresult=getOrderNO(table1,tparm.getValue("ORDER_CODE"),row);//取得orderNO相同的医嘱
			    double tAmt=0;//耗材总金额
			    double lAmt=0;//联合医嘱总金额
			    double totAmt=0;//联合医嘱总金额
			    for(int i=0;i<oresult.getCount();i++){
			    	tAmt+=oresult.getDouble("TOT_AMT",i);
			    }
			         //this.messageBox(""+tAmt);
			         totAmt=tresult.getDouble("TOT_AMT",0);
			    //联合医嘱总费用
			    if(tparm.getValue("ORDERS_CODE").length()>0){
			    	
			    	TParm aresult=IBSOrderChecklistTool.getInstance().selecttotamt(getParm(table1,row));
			    	lAmt=aresult.getDouble("TOT_AMT",0);
			    	tresult.setData("TOT_AMT",0,df.format(lAmt+tAmt));
			    	tresult.setData("OWN_PRICE",0,aresult.getDouble("TOT_AMT",0));//单价
			    }else{
			    	tresult.setData("TOT_AMT",0,df.format(totAmt+tAmt));
			    }
			   
			    if(tresult.getErrCode()<0){
					this.messageBox("查询出现问题");
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
	   * table2关联table3
	   */
	  public void onTABLEClickedS(int row){
		        //TParm oresult=new TParm();
		        TParm tresult=new TParm();
		        TParm sresult=table2.getParmValue().getRow(row);
		         if(!"".equals(sresult.getValue("DATA_TYPE"))&&sresult.getValue("DATA_TYPE")!=null){//补充计费
		        	  tresult=IBSOrderChecklistTool.getInstance().selectdatac(getParm(table2,row));
		         }else{//不是补充计费
		              //oresult=getOrderNO(table1,sresult.getValue("ORDER_CODE"),row);//取得orderNO相同的医嘱
		        	
			          tresult=IBSOrderChecklistTool.getInstance().selectdatac(getParm(table2,row));
			          tresult.addParm(oresult);
		         }
			    if(tresult.getErrCode()<0){
					this.messageBox("查询出现问题");
					return;
				}
				table3.setParmValue(tresult);
			    
	  }
	  
	  /**
	   * 查询
	   */
	  public TParm getParm(TTable table, int row){
		    TParm parm=new TParm();
		    TParm tparm = table.getParmValue().getRow(row);
		  //查询启日期
		    String sDate = getValueString("S_DATE");
		    sDate=sDate.replaceAll("-","").replaceAll(":","").replace(" ", "");
		    sDate=sDate.substring(0,sDate.lastIndexOf("."));
		    
		    //查询迄日期
			String eDate = getValueString("E_DATE");
			eDate=eDate.replaceAll("-","").replaceAll(":","").replace(" ", "");
			eDate=eDate.substring(0,eDate.lastIndexOf("."));
			//联合医嘱总金额
			if(tparm.getValue("FREQ_CODE").length()>0){
				parm.setData("FREQ_CODE", tparm.getValue("FREQ_CODE"));
				
			}
			
			
			if(tparm.getValue("ORDERS_CODE").length()>0){
				parm.setData("ORDERSET_CODE",tparm.getValue("ORDERS_CODE"));
			}
			//科室查询
			if(this.getDeptCode().length()>0){
				parm.setData("DEPT_CODE", this.getDeptCode());//
			}
			/*if(tparm.getValue("ORDER_NO").length()>0){
				parm.setData("ORDER_NO",tparm.getValue("ORDER_NO"));
			}*/
			
			if(tparm.getValue("ORDERSET_CODE").length()>0){//联合医嘱细
				parm.setData("ORDERSET_CODE",tparm.getValue("ORDERSET_CODE"));//联合医嘱明细
			}else{//非集合医嘱
				parm.setData("ORDER_CODE",tparm.getValue("ORDER_CODE"));//非联合医嘱明细
			}
				parm.setData("S_DATE", sDate);//
				parm.setData("E_DATE", eDate);
			    parm.setData("CASE_NO",this.getCaseNo());
		        return parm;
	  }
	  
	  /**
		 * 费用代码下拉列表选择
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
		 * 清空
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
		 * 取得order_no相同的医嘱
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
		 * 加入表格排序监听方法
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
					// 调用排序方法;
					// 转换出用户想排序的列和底层数据的列，然后判断 f
					if (j == sortColumn) {
						ascending = !ascending;
					} else {
						ascending = true;
						sortColumn = j;
					}
					// table.getModel().sort(ascending, sortColumn);

					// 表格中parm值一致,
					// 1.取paramw值;
					TParm tableData = table.getParmValue();
					// 2.转成 vector列名, 行vector ;
					String columnName[] = tableData.getNames("Data");
					String strNames = "";
					for (String tmp : columnName) {
						strNames += tmp + ";";
					}
					strNames = strNames.substring(0, strNames.length() - 1);
					// System.out.println("==strNames=="+strNames);
					Vector vct = getVector(tableData, "Data", strNames, 0);
					// System.out.println("==vct=="+vct);

					// 3.根据点击的列,对vector排序
					// System.out.println("sortColumn===="+sortColumn);
					// 表格排序的列名;
					String tblColumnName = table.getParmMap(sortColumn);
					// 转成parm中的列
					int col = tranParmColIndex(columnName, tblColumnName);
					// System.out.println("==col=="+col);

					compare.setDes(ascending);
					compare.setCol(col);
					java.util.Collections.sort(vct, compare);
					// 将排序后的vector转成parm;
					cloneVectoryParam(vct, new TParm(), strNames);

					// getTMenuItem("save").setEnabled(false);
				}
			});
			
		}
		
		/**
		 * 得到 Vector 值
		 * 
		 * @param group
		 *            String 组名
		 * @param names
		 *            String "ID;NAME"
		 * @param size
		 *            int 最大行数
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
					// System.out.println("tmp相等");
					return index;
				}
				index++;
			}

			return index;
		}
		
		/**
		 * vectory转成param
		 */
		private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
				String columnNames) {
			//
			// System.out.println("===vectorTable==="+vectorTable);
			// 行数据->列
			// System.out.println("========names==========="+columnNames);
			String nameArray[] = StringTool.parseLine(columnNames, ";");
			// 行数据;
			for (Object row : vectorTable) {
				int rowsCount = ((Vector) row).size();
				for (int i = 0; i < rowsCount; i++) {
					Object data = ((Vector) row).get(i);
					parmTable.addData(nameArray[i], data);
				}
			}
			parmTable.setCount(vectorTable.size());
			table3.setParmValue(parmTable);
			// System.out.println("排序后===="+parmTable);

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
