package com.javahis.ui.mem;



import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
/**
 * <p>Title: 身份设置医嘱细项显示</p>
 *
 * <p>Description: 身份设置医嘱细项显示</p>
 *
 * <p>Copyright: javahis 20140103</p>
 *
 * <p>Company:JavaHis</p>
 *
 * @author duzhw
 * @version 4.5
 */
public class MEMCTZOrderSetShowControl extends TControl {
		private TTable table;

	    public void onInit() {
	        super.onInit();
	        //初始化控件
			initComponent();
	        initParam();
	    }
	    
	    /**
		 * 初始化控件
		 */
	    private void initComponent() {
	    	table=(TTable)this.getComponent("TABLE");
			//table.addEventListener("TABLE->"+ TTableEvent.CHANGE_VALUE,this, "onValueChange");
	    	
	    }
	    
		/**
		 * 初始化参数
		 */
		public void initParam() {
			TParm parm=(TParm)this.getParameter();
			// System.out.println("parm="+parm);
			String orderSetCode = parm.getValue("ORDERSET_CODE");
			String sql = "SELECT A.ORDER_CODE,B.ORDERSET_CODE,A.ORDER_DESC, A.SPECIFICATION, B.DOSAGE_QTY," +
					" A.UNIT_CODE AS MEDI_UNIT, A.OWN_PRICE,A.OWN_PRICE AS PACKAGE_PRICE, A.OWN_PRICE * B.DOSAGE_QTY " +
					" AS OWN_AMT, A.EXEC_DEPT_CODE, A.OPTITEM_CODE, A.INSPAY_TYPE " +
					" FROM SYS_FEE A, SYS_ORDERSETDETAIL B " +
					" WHERE A.ORDER_CODE = B.ORDER_CODE and A.ACTIVE_FLG = 'Y' AND B.HIDE_FLG='Y' " +
					" AND B.ORDERSET_CODE = '"+orderSetCode+"'";
			System.out.println("sql===----"+sql);
			TParm result = new TParm(TJDODBTool.getInstance().select(sql));
			//System.out.println("result="+result);
			table.setParmValue(result);
		}

	    /**
	     * 返回数据库操作工具
	     * @return TJDODBTool
	     */
	    public TJDODBTool getDBTool() {
	        return TJDODBTool.getInstance();
	    }

		
	    
		/**
		 * 关闭
		 */
	    public void close() {
	    	TParm result = new TParm();
			this.setReturnValue(result);
	    	this.closeWindow();
	        
	    }
	 
}
