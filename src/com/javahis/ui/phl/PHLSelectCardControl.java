package com.javahis.ui.phl;
import jdo.phl.PhlBedTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;

/**
 * <p>Title: 床位索引</p>
 *
 * <p>Description:床位索引</p>
 *
 * <p>Copyright: Copyright (c)cao yong 2014</p>
 *
 * <p>Company: BlueCore</p>
 *
 * @author 2014.05.07
 * @version 1.0
 */
public class PHLSelectCardControl
    extends TControl {

    private TTable table;

    private String region_code;

    public PHLSelectCardControl() {
    }

    /**
     * 初始化方法
     */
    public void onInit() {
        // 初始画面数据
        Object obj = getParameter();
        if (obj != null) {
            region_code = ( (TParm) obj).getValue("REGION_CODE");
            this.setValue("REGION_CODE", region_code);
        }
          onQuery();
    }
    
    
    public void onQuery(){
    	
    	 TParm parm=new TParm();
         parm.setData("REGION_CODE", region_code);
         boolean onQflg=false;
         if ("Y".equals(this.getValue("BED"))&&"Y".equals(this.getValue("SEAT"))) {
 	   		parm.setData("TYPE2", "");
 	   		onQflg=true;
 	   	}
         if(!onQflg){
         if ("Y".equals(this.getValue("BED"))) {
             parm.setData("TYPE0", "0");
             
         }
 	   	 if ("Y".equals(this.getValue("SEAT"))) {
 	   		 parm.setData("TYPE1", "1");
 	   		 
 	   	 }
         }
         
         if ("Y".equals(this.getValue("STATUS"))) {
 	   		  parm.setData("BED_STATUS", "0");
 	   		 
 	   	 }
         TParm result = PhlBedTool.getInstance().onQuery(parm);
        
        table = this.getTable("TABLE");
        table.setParmValue(result);
    }

    /**
     * TABLE双击事件
     */
    public void onTableDoubleClicked() {
        int row = table.getSelectedRow();
        setReturnValue(table.getParmValue().getRow(row));
        this.closeWindow();
    }

    /**
     * 得到Table对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }
    
    /**
     * 复选框查询
     */
    public void onSelectBed(){
	   onQuery();
    }
}
