package com.javahis.ui.ins;

import java.sql.Timestamp;
import com.dongyang.data.TParm;
import com.dongyang.control.TControl;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.dongyang.ui.TTable;
import jdo.sys.Operator;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;
import jdo.ins.INSSingleTransferTool;

/**
 * <p>Title: 单病种转入控制类</p>
 *
 * <p>Description: 单病种转入控制类</p>
 *
 * <p>Copyright: Copyright (c) BlueCore 2011</p>
 *
 * <p>Company: BlueCore</p>
 *
 * @author wangl 2012.02.13
 * @version 1.0
 */
public class INSSingleTransferControl
    extends TControl {
   

    /**
     * 初始化
     */
    public void onInit() {
        super.onInit();
        initPage();
    }

    /**
     * 初始化界面
     */
    public void initPage() {
        Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance().
                                                  getDate(), -1);
        //入院开始时间
        setValue("S_DATE", yesterday);
        //入院结束时间
        setValue("E_DATE", SystemTool.getInstance().getDate());
        //默认值 城职
        setValue("CARD_TYPE", "1");
        //默认值 未转入
        setValue("TRANS_TYPE", "2");
        //table专用的监听
        getTTable("Table").addEventListener(TTableEvent.CLICKED, this,
                                                "onTable1Clicked");
    }

    /**
     * 保存
     */
    public void onSave() {
        int row = getTTable("Table").getSelectedRow();
        if (row < 0) {
            messageBox("请点选一条数据");
            return;
        }
        TParm parm = new TParm();
        parm = getTTable("Table").getParmValue().getRow(row);
//        parm.setData("TRANS_TYPE",getValueString("TRANS_TYPE"));
        //System.out.println("table"+parm);
        TParm result = new TParm();
        //更新资格确认书状态
        result = INSSingleTransferTool.getInstance().onUpINSIbsUpload(parm);
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;

        }else{
            messageBox("P0005");
            onQuery();
        }
    }

    /**
     * 查询
     */
    public void onQuery() {
        TParm result = new TParm();
        TParm parm = new TParm();
        parm.setData("CARD_TYPE", this.getValueString("CARD_TYPE"));
        parm.setData("REGION_CODE", Operator.getRegion());
        parm.setData("S_DATE",StringTool.getString(TypeTool.getTimestamp(getValue(
            "S_DATE")), "yyyyMMdd"));
        parm.setData("E_DATE",
                     StringTool.getString(TypeTool.getTimestamp(getValue("E_DATE")), "yyyyMMdd"));
        
        String mrNo = getValueString("MR_NO").length()==0?"":PatTool.getInstance().checkMrno(getValueString("MR_NO"));
        parm.setData("MR_NO", mrNo);
        if (this.getValueString("TRANS_TYPE").equals("1"))
            result = INSSingleTransferTool.getInstance().getSingleTransData(
                parm);
        else
            result = INSSingleTransferTool.getInstance().getSingleNoTransData(
                parm);
        if (result.getErrCode() < 0) {
            this.messageBox(result.getErrText());
            return;
        }
        if (result.getCount("CONFIRM_NO") < 1) {
            //查无数据
            this.messageBox("E0008");
            this.onClear();
            return;
        }
        this.callFunction("UI|Table|setParmValue", result);
        return;
    }
    /**
     * 清空
     */
    public void onClear() {
        initPage();
        TTable table = (TTable)this.getComponent("Table");
        table.removeRowAll();
        this.clearValue("MR_NO");
    }
    /**
     * table监听事件
     */
    public void onTable1Clicked() {
        callFunction("UI|Table1|getClickedRow");
    }
    /**
     * 得到TTable
     * @param tag String
     * @return TTable
     */
    public TTable getTTable(String tag) {
        return (TTable)this.getComponent(tag);
    }
    /**
     * 给单病种内容赋值
     */
    public void setDesc(){
      TTextFormat singleDisease = (TTextFormat) this.getComponent("SYS_SINGLEDISEASE");
      String text=singleDisease.getText();
      getTTable("Table").setValueAt(text,getTTable("Table").getSelectedRow(),10);
    }


}

