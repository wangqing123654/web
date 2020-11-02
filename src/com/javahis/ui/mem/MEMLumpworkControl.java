package com.javahis.ui.mem;

import java.sql.Timestamp;
import jdo.mem.MEMLumpworkTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
/**
 * <p>
 * Title: 包干套餐维护
 * </p>
 * @author liling 20140508
 * 
 *
 */
public class MEMLumpworkControl  extends TControl {
	TParm data;
	 int selectRow = -1;
	public MEMLumpworkControl()
	{
		selectRow = -1;
	}
    public void onInit() {
        super.onInit();
        ( (TTable) getComponent("TABLE")).addEventListener("TABLE->"
	            + TTableEvent.CLICKED, this, "onTABLEClicked");
        Timestamp sysDate = SystemTool.getInstance().getDate();
        Timestamp	startDate = StringTool.getTimestamp(StringTool.getString(sysDate,
				"yyyy/MM/dd")
				+ " 00:00:00", "yyyy/MM/dd HH:mm:ss");
		// 迄日
        Timestamp	endDate = StringTool.getTimestamp(StringTool.getString(sysDate,
		"yyyy/MM/dd")
		+ " 23:59:59", "yyyy/MM/dd HH:mm:ss");
		// 起日YYYY/MM/DD
		this.setValue("START_DATE", startDate);
		// 迄日YYYY/MM/DD
		this.setValue("END_DATE", endDate);
		 TParm parm = new TParm(TJDODBTool.getInstance().select(
	        		"SELECT COUNT(*) AS COUNT FROM MEM_LUMPWORK"));
		 this.setValue("SEQ",Integer.parseInt(parm.getValue("COUNT", 0)));		 
        onQuery();
    }
    /**
     *增加对TABLE的监听
     * @param row int
     */
    public void onTABLEClicked(int row) {
    	 // 选中行
        if (row < 0)
            return;
        TParm data = (TParm) callFunction("UI|TABLE|getParmValue");
        setValueForParm(
            "LUMPWORK_CODE;LUMPWORK_DESC;PY1;ENG_DESC;SEQ;FEE;START_DATE;END_DATE;DESCRIPTION;CTZ_CODE",
            data, row);     
        selectRow = row;
        ((TTextField) getComponent("LUMPWORK_CODE")).setEnabled(false);
    }
    /**
     * 新增
     */
    public void onInsert() {
    	//LUMPWORK_CODE;LUMPWORK_DESC;PY1;ENG_DESC;SEQ;FEE;START_DATE;END_DATE;DESCRIPTION
        if (!this.emptyTextCheck("LUMPWORK_CODE"))
            return;
        //LUMPWORK_DESC
        if(this.getValueString("LUMPWORK_DESC").equals("")){
        	this.messageBox("请输入包干套餐名称！");
        	return;
        }
        if(this.getValueString("START_DATE").equals("")){
        	this.messageBox("请输入生效日期！");
        	return;
        }
        if(this.getValueString("END_DATE").equals("")){
        	this.messageBox("请输入失效日期！");
        	return;
        }
        //LUMPWORK_CODE;LUMPWORK_DESC;PY1;ENG_DESC;SEQ;FEE;START_DATE;END_DATE;DESCRIPTION;OPT_DATE;OPT_TERM;OPT_USER
        TParm parm = getParmForTag("LUMPWORK_CODE;LUMPWORK_DESC;PY1;ENG_DESC;SEQ;FEE;START_DATE;END_DATE;DESCRIPTION;CTZ_CODE");
        String startDate =parm.getValue("START_DATE").substring(0, 19);
        String endDate =parm.getValue("END_DATE").substring(0, 19);
        parm.setData("START_DATE", startDate);
        parm.setData("END_DATE", endDate);
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_DATE", SystemTool.getInstance().getDate().toString().substring(0, 19));
        parm.setData("OPT_TERM", Operator.getIP());
      //  System.out.println("parm===>"+parm);
        TParm result = MEMLumpworkTool.getInstance().insertdata(parm);
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }
        onClear();
        onQuery();
        //弹出新增成功提示框
        this.messageBox("P0002");
      
    }
    /**
     * 根据汉字输出拼音首字母
     * @return Object
     */
    public Object onCode() {
        if (TCM_Transform.getString(this.getValue("LUMPWORK_DESC")).length() <
            1) {
            return null;
        }
        SystemTool st = new SystemTool();
        String value = st.charToCode(String.valueOf(this.getValue(
            "LUMPWORK_DESC")));
        if (null == value || value.length() < 1) {
            return null;
        }
        this.setValue("PY1", value);
        //光标下移
        this.callFunction("UI|afterFocus","LUMPWORK_DESC");
        return null;
    }

    /**
     * 查询
     */
    public void onQuery() {
        TParm parm = getParmForTag("LUMPWORK_CODE;LUMPWORK_DESC", true);
        TParm data = MEMLumpworkTool.getInstance().selectdata(parm);
        if (data.getErrCode() < 0) {
            messageBox(data.getErrText());
            return;
        }
        ( (TTable) getComponent("TABLE")).setParmValue(data);
        ((TTextField) getComponent("LUMPWORK_CODE")).setEnabled(true);
    }

   

    /**
     * 更新
     */
    public void onUpdate() {
        TParm parm = getParmForTag("LUMPWORK_CODE;LUMPWORK_DESC;PY1;ENG_DESC;SEQ;FEE;START_DATE;END_DATE;DESCRIPTION;CTZ_CODE");
        String startDate =parm.getValue("START_DATE").substring(0, 19);
        String endDate =parm.getValue("END_DATE").substring(0, 19);
        parm.setData("START_DATE", startDate);
        parm.setData("END_DATE", endDate);
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_DATE", SystemTool.getInstance().getDate().toString().substring(0, 19));
        parm.setData("OPT_TERM", Operator.getIP());
        TParm result = MEMLumpworkTool.getInstance().updatedata(parm);
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }
        onClear();
        onQuery();
       this.messageBox("P0001");
    }

    /**
     * 保存
     */
    public void onSave() {
        if (selectRow == -1) {
            onInsert();
            return;
        }
        onUpdate();        
    }


    /**
     * 删除
     */
    public void onDelete() {
    	 if (selectRow == -1){
         	this.messageBox("请选择要删除的数据");
             return;
         }
        if (this.messageBox("询问", "是否删除", 2) == 0) {
            String LUMPWORK_CODE = getValue("LUMPWORK_CODE").toString();
      //     String queNo = getValue("QUE_NO").toString();
            TParm result = MEMLumpworkTool.getInstance().deletedata(LUMPWORK_CODE);
            if (result.getErrCode() < 0) {
                messageBox(result.getErrText());
                return;
            }
            onClear();
            onQuery();
            this.messageBox("P0003");
        }
        else {
            return;
        }
    }
    /**
     *清空
     */
    public void onClear() {
        clearValue("LUMPWORK_CODE;LUMPWORK_DESC;PY1;ENG_DESC;SEQ;FEE;START_DATE;END_DATE;DESCRIPTION");
        this.callFunction("UI|Table|clearSelection");
        selectRow = -1;
        onInit();
        ( (TTable) getComponent("TABLE")).removeRowAll();
        
    }

}
