package com.javahis.ui.erd;


import jdo.sys.Pat;
import com.dongyang.control.TControl;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TWord;
import com.dongyang.data.TParm;
import jdo.adm.ADMTool;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.JavaHisDebug;
import jdo.erd.ERDForSUMTool;

/**
 * <p>Title: 医嘱单打印预览主窗口</p>
 *
 * <p>Description: 医嘱单处理特效：
 *                  1,order_cat1_type:PHA  数量+单位+频次+用法
 *                    非PHA （AW1,.,STAT 不显示凭此频次）其余显示 频次
 *                  2,连接医嘱
 *                  3,ZZZZ医嘱备注
 *                  4,转科换页</p>
 *
 * <p>Copyright: JAVAHIS </p>
 *
 * <p>Company: </p>
 *
 * @author ZangJH 2009-10-30
 * @version 1.0
 */
public class ERDOrderSheetPrtAndPreViewControl
    extends TControl {

    //医嘱嘱种类
    TRadioButton ST;
    TRadioButton UD;
    TRadioButton DS;

    TWord word;

    String caseNo = "";
    String name="";
    String ipdNo="";
    String mrN0="";
    String dept="";
    String station="";
    String bed="";

    //保存来自住院管理界面的参数，传给补充计价
    TParm outsideParm = new TParm();

    public ERDOrderSheetPrtAndPreViewControl() {
    }

    public void onInit() {
        super.onInit();
        myInitControler();
        outsideParm = (TParm)this.getParameter();
//        this.messageBox("===外部参数===="+outsideParm);
        if (outsideParm != null){
        	if(!initParmFromOutside()){
        		
        		return;
        	}else{
        		initWord();	
        	}
        	
        }else{
        	initWord();
        }
            
        
    }

    /**
     * 初始化时得到所有控件对象
     */
    public void myInitControler() {

        ST = (TRadioButton)this.getComponent("ST");
        UD = (TRadioButton)this.getComponent("UD");
        DS = (TRadioButton)this.getComponent("DS");
        word = (TWord)this.getComponent("WORD");
    }

    /**
     * 初始化界面参数caseNo/stationCode
     */
    public boolean initParmFromOutside() {
        //按就诊号查询的caseNo
        this.setCaseNo(outsideParm.getValue("INW", "CASE_NO"));
        TParm parm1 = new TParm();
        parm1.setData("CASE_NO", getCaseNo());
        TParm admInfo = ERDForSUMTool.getInstance().selERDPatInfo(parm1);//ADMTool.getInstance().getADM_INFO(parm1);
        if(admInfo.getCount()<=0){
        	this.messageBox("没有相关的留观医嘱。");
        	return false;
        }else{
        	this.setIpdNo( (String) admInfo.getData("IPD_NO", 0));
            this.setMrN0( (String) admInfo.getValue("MR_NO", 0));
            String inDeptCode = (String) admInfo.getValue("IN_DEPT_CODE", 0);
            TParm firstData = new TParm(TJDODBTool.getInstance().select(
                "SELECT DEPT_CHN_DESC from sys_dept where DEPT_CODE='" + inDeptCode +
                "'"));
            this.setDept( (String) firstData.getData("DEPT_CHN_DESC", 0));
            station = admInfo.getValue("ERD_REGION_DESC",0);
            bed = admInfo.getValue("BED_DESC",0);
            Pat pat = Pat.onQueryByMrNo( (String) admInfo.getValue("MR_NO", 0));
            this.setName(pat.getName());
           
        }
        return true;
    }


    /**
     * 打印程序
     */
    public void onPrint() {
        word.print();
    }

    /**
     * 初始化报表界面
     */
    public void initWord() {
        word.setWordParameter(null);
        word.getWordText().getPM().getFileManager().onNewFile();
        TParm prtParm = new TParm();
        //调用TWord
        word.setWordParameter(prtParm);
        word.setPreview(true);
        TParm orderParm = new TParm();
        //得到打印参数
        orderParm = getOrderParm();
        orderParm.addData("SYSTEM", "COLUMNS", "EFF_DATE_DAY");
        orderParm.addData("SYSTEM", "COLUMNS", "EFF_DATE_TIME");
        orderParm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
        orderParm.addData("SYSTEM", "COLUMNS", "ORDER_DR_CODE");
        orderParm.addData("SYSTEM", "COLUMNS", "NS_EXEC_DATE");
        orderParm.addData("SYSTEM", "COLUMNS", "NS_EXEC_CODE");
        TParm data = new TParm();
        data.setData("TABLE", orderParm.getData());
        data.setData("NAME", "TEXT", this.getName());
        data.setData("MR_NO", "TEXT", this.getMrN0());
        data.setData("IPD_NO", "TEXT", this.getIpdNo());
        data.setData("DEPT", "TEXT", this.getDept());
        data.setData("STATION", "TEXT", station);
        data.setData("BED", "TEXT", bed);
        word.setWordParameter(data);
        word.setFileName("%ROOT%\\config\\prt\\erd\\OrderSheet.jhw");
    }

    /**
      * 挑勾动作
      * @param flg Object
      */
     public void onCheck(Object flg) {

         word.setWordParameter(null);
         word.getWordText().getPM().getFileManager().onNewFile();
         TParm prtParm = new TParm();
         //调用TWord
         word.setWordParameter(prtParm);
         word.setPreview(true);
         TParm orderParm = new TParm();
         if ("ST".equals(flg + "") && ST.isSelected()) {
             //得到打印参数
             orderParm = getSTOrderParm();
             orderParm.addData("SYSTEM", "COLUMNS", "EFF_DATE_DAY");
             orderParm.addData("SYSTEM", "COLUMNS", "EFF_DATE_TIME");
             orderParm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
             orderParm.addData("SYSTEM", "COLUMNS", "ORDER_DR_CODE");
             orderParm.addData("SYSTEM", "COLUMNS", "NS_EXEC_DATE");
             orderParm.addData("SYSTEM", "COLUMNS", "NS_EXEC_CODE");
             TParm data = new TParm();
             data.setData("TABLE", orderParm.getData());
             data.setData("NAME", "TEXT", this.getName());
             data.setData("MR_NO", "TEXT", this.getMrN0());
             data.setData("IPD_NO", "TEXT", this.getIpdNo());
             data.setData("DEPT", "TEXT", this.getDept());
             word.setWordParameter(data);
             word.setFileName("%ROOT%\\config\\prt\\inw\\OrderSheet_ST.jhw");
             return;
         }
         if ("UD".equals(flg + "") && UD.isSelected()) {
             //得到打印参数
             orderParm = getUDOrderParm();
             orderParm.addData("SYSTEM", "COLUMNS", "EFF_DATE_DAY");
             orderParm.addData("SYSTEM", "COLUMNS", "EFF_DATE_TIME");
             orderParm.addData("SYSTEM", "COLUMNS", "ORDER_DR_CODE");
             orderParm.addData("SYSTEM", "COLUMNS", "NS_CHECK_CODE");
             orderParm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
             orderParm.addData("SYSTEM", "COLUMNS", "DC_DATE_DAY");
             orderParm.addData("SYSTEM", "COLUMNS", "DC_DATE_TIME");
             orderParm.addData("SYSTEM", "COLUMNS", "DC_DR_CODE");
             orderParm.addData("SYSTEM", "COLUMNS", "DC_NS_CHECK_CODE");
             TParm data = new TParm();
             data.setData("TABLE", orderParm.getData());
             data.setData("NAME", "TEXT", this.getName());
             data.setData("MR_NO", "TEXT", this.getMrN0());
             data.setData("IPD_NO", "TEXT", this.getIpdNo());
             data.setData("DEPT", "TEXT", this.getDept());
             word.setWordParameter(data);
             word.setFileName("%ROOT%\\config\\prt\\inw\\OrderSheet_UD.jhw");
             return;
         }
         if ("DS".equals(flg + "") && DS.isSelected()) {
             //得到打印参数
             orderParm = getDSOrderParm();//目前临时和出院带药一样
             orderParm.addData("SYSTEM", "COLUMNS", "EFF_DATE_DAY");
             orderParm.addData("SYSTEM", "COLUMNS", "EFF_DATE_TIME");
             orderParm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
             orderParm.addData("SYSTEM", "COLUMNS", "ORDER_DR_CODE");
             orderParm.addData("SYSTEM", "COLUMNS", "NS_EXEC_DATE");
             orderParm.addData("SYSTEM", "COLUMNS", "NS_EXEC_CODE");
             TParm data = new TParm();
             data.setData("TABLE", orderParm.getData());
             data.setData("NAME", "TEXT", this.getName());
             data.setData("MR_NO", "TEXT", this.getMrN0());
             data.setData("IPD_NO", "TEXT", this.getIpdNo());
             data.setData("DEPT", "TEXT", this.getDept());
             word.setWordParameter(data);
             word.setFileName("%ROOT%\\config\\prt\\inw\\OrderSheet_DS.jhw");
             return;
         }

    }

    /**
     * 得到门诊医嘱信息
     * @return TParm
     */
    private TParm getOrderParm() {
        TParm STparm = new TParm(TJDODBTool.getInstance().select(getSQL()));
        TParm printData = arrangeData(STparm);
        return printData;
    }

    /**
     * 取得门诊医嘱信息SQL
     * @return String
     */
    private String getSQL(){
        String SQL = " SELECT   TO_CHAR (A.ORDER_DATE, 'MM/DD') AS EFF_DATE_DAY, " +
                     "          TO_CHAR (A.ORDER_DATE, 'HH24:MI') AS EFF_DATE_TIME, " +
                     "          A.DR_CODE ORDER_DR_CODE,A.ORDER_DESC,A.MEDI_QTY,F.UNIT_CHN_DESC,A.FREQ_CODE, " +
                     "          A.DOSE_TYPE,A.LINKMAIN_FLG,A.LINK_NO,A.DR_NOTE,A.ORDER_CODE,A.CAT1_TYPE, " +
                     "          TO_CHAR (A.NS_EXEC_DATE,'MM/DD HH24:MI') AS NS_EXEC_DATE,A.NS_EXEC_CODE,A.ROUTE_CODE " +
                     " FROM   OPD_ORDER A,SYS_UNIT F" +
                     " WHERE  A.CASE_NO='" + getCaseNo() + "'" +
                     " AND    A.RX_TYPE != '0'"+
                     " AND    A.HIDE_FLG = 'N' " +
                     " AND    A.MEDI_UNIT = F.UNIT_CODE (+)" +
                     " ORDER BY A.ORDER_DATE ";
        return SQL;
    }


    /**
     * 整理数据
     * @param parm TParm
     * @return TParm
     */
    private TParm arrangeData(TParm parm) {
        TParm result = new TParm();
        int count = parm.getCount();
        for (int i = 0; i < count; i++) {
            TParm order = parm.getRow(i);
            //判断连接医嘱
            if (ifLinkOrder(order)) {
                //如果为连接医嘱细项则不予处理
                if (ifLinkOrderSubItem(order))
                    continue;
                String finalOrder = getLinkOrder(order, parm);
                result.addData("ORDER_DESC", finalOrder);
            }
            else { //普通医嘱
                String drNote = (String) order.getData("DR_NOTE");
                String desc = (String) order.getData("ORDER_DESC");
                String qty = order.getData("MEDI_QTY") + "";
                String unit = (String) order.getData("UNIT_CHN_DESC");
                String freq = (String) order.getData("FREQ_CODE");

                String dose = (String) order.getData("ROUTE_CODE");
                String cat1=(String) order.getData("CAT1_TYPE");
                //判断是否是医嘱备注
                if (ifZ00Order(order)) {
                    desc = drNote;
                    drNote = "";
                    qty="";
                    unit="";
                    freq="";
                    dose="";
                }
                //如果该医嘱是非PHA
                if((!checkOrderCat1(cat1))&&chackFreq(freq)){
                    qty="";
                    unit="";
                    freq="";
                    dose="";
                }
                String finalDesc = "";
                if(desc.startsWith("*"))
                    finalDesc = desc;
                else
                    finalDesc = "" + desc + "\r" + qty + " " + unit + " " +
                                freq +
                                " " + getRouteDesc(dose) +
                                ((drNote != null && drNote.length() != 0) ?
                                 "\r" + "(" + drNote + ")" : "");
                //主要参数--医嘱
                result.addData("ORDER_DESC", finalDesc);
            }
            result.addData("EFF_DATE_DAY", order.getData("EFF_DATE_DAY"));
            result.addData("EFF_DATE_TIME", order.getData("EFF_DATE_TIME"));
            result.addData("ORDER_DR_CODE", order.getData("ORDER_DR_CODE"));
            result.addData("NS_EXEC_DATE", order.getData("NS_EXEC_DATE"));
            result.addData("NS_EXEC_CODE", order.getData("NS_EXEC_CODE"));
        }
        result.setCount(result.getCount("EFF_DATE_DAY"));
        return result;
    }

    private String getRouteDesc(String dose) {
        TParm parm = new TParm(TJDODBTool.getInstance().select("SELECT ROUTE_CHN_DESC FROM SYS_PHAROUTE WHERE ROUTE_CODE = '"+dose+"'"));
        if(parm.getCount() <= 0)
            return "";
        return parm.getValue("ROUTE_CHN_DESC",0);
    }
    /**
     * 获得该病人长期医嘱
     * @return TParm
     */
    private TParm getUDOrderParm() {
        TParm UDparm = new TParm(TJDODBTool.getInstance().select(this.
            getSelectSQL("UD")));
        TParm printData = arrangeData(UDparm, "UD");
        return printData;
    }

    /**
     * 获得该病人临时医嘱
     * @return TParm
     */
    private TParm getSTOrderParm() {
        TParm STparm = new TParm(TJDODBTool.getInstance().select(this.
            getSelectSQL("ST")));
        TParm printData = arrangeData(STparm, "ST");
        return printData;
    }
    /**
     * 获得该病人出院带药医嘱
     * @return TParm
     */
    private TParm getDSOrderParm() {
        TParm STparm = new TParm(TJDODBTool.getInstance().select(this.
            getSelectSQL("DS")));
        TParm printData = arrangeData(STparm, "DS");
        return printData;
    }

    /**
     * 整理数据
     * @param parm TParm
     * @return TParm
     */
    private TParm arrangeData(TParm parm, String flg) {
        TParm result = new TParm();
        int count = parm.getCount();
        for (int i = 0; i < count; i++) {
            TParm order = parm.getRow(i);
            //判断连接医嘱
            if (ifLinkOrder(order)) {
                //如果为连接医嘱细项则不予处理
                if (ifLinkOrderSubItem(order))
                    continue;
                String finalOrder = getLinkOrder(order, parm);
                result.addData("ORDER_DESC", finalOrder);
            }
            else { //普通医嘱
                String drNote = (String) order.getData("DR_NOTE");
                String desc = (String) order.getData("ORDER_DESC");
                String qty = order.getData("MEDI_QTY") + "";
                String unit = (String) order.getData("UNIT_CHN_DESC");
                String freq = (String) order.getData("FREQ_CODE");
                String dose = (String) order.getData("ROUTE_CODE");
                String cat1=(String) order.getData("CAT1_TYPE");
                //判断是否是医嘱备注
                if (ifZ00Order(order)) {
                    desc = drNote;
                    drNote = "";
                    qty="";
                    unit="";
                    freq="";
                    dose="";
                }
                //如果该医嘱是非PHA
                if((!checkOrderCat1(cat1))&&chackFreq(freq)){
                    qty="";
                    unit="";
                    freq="";
                    dose="";
                }
                String finalDesc = "" + desc + "\r" + qty + " " + unit + " " +
                    freq +
                    " " + dose +
                    ((drNote != null && drNote.length() != 0) ?
                     "\r" + "(" + drNote + ")" : "");
                //主要参数--医嘱
                result.addData("ORDER_DESC", finalDesc);
            }
            //根据医嘱类型设置不同的数据列
            if ("UD".equals(flg)) {
                result.addData("EFF_DATE_DAY", order.getData("EFF_DATE_DAY"));
                result.addData("EFF_DATE_TIME", order.getData("EFF_DATE_TIME"));
                result.addData("ORDER_DR_CODE", order.getData("ORDER_DR_CODE"));
                result.addData("NS_CHECK_CODE", order.getData("NS_CHECK_CODE"));
                result.addData("DC_DATE_DAY", order.getData("DC_DATE_DAY"));
                result.addData("DC_DATE_TIME", order.getData("DC_DATE_TIME"));
                result.addData("DC_DR_CODE", order.getData("DC_DR_CODE"));
                result.addData("DC_NS_CHECK_CODE",
                               order.getData("DC_NS_CHECK_CODE"));
            }
            else if ("ST".equals(flg)) { //临时
                result.addData("EFF_DATE_DAY", order.getData("EFF_DATE_DAY"));
                result.addData("EFF_DATE_TIME", order.getData("EFF_DATE_TIME"));
                result.addData("ORDER_DR_CODE", order.getData("ORDER_DR_CODE"));
                result.addData("NS_EXEC_DATE", order.getData("NS_EXEC_DATE"));
                result.addData("NS_EXEC_CODE", order.getData("NS_EXEC_CODE"));
            }
            else { //出院带药
                result.addData("EFF_DATE_DAY", order.getData("EFF_DATE_DAY"));
                result.addData("EFF_DATE_TIME", order.getData("EFF_DATE_TIME"));
                result.addData("ORDER_DR_CODE", order.getData("ORDER_DR_CODE"));
                result.addData("NS_EXEC_DATE", order.getData("NS_EXEC_DATE"));
                result.addData("NS_EXEC_CODE", order.getData("NS_EXEC_CODE"));
            }
        }
        result.setCount(result.getCount("EFF_DATE_DAY"));
        return result;
    }

    /**
     * 判断该医嘱是否是PHA类型
     * @param code String
     * @return boolean
     */
    private boolean checkOrderCat1(String code){
        return "PHA".equals(code);
    }

    /**
     * 非PHA医嘱 （AW1,.,STAT 不显示凭此频次）
     * @param freq String
     * @return boolean
     */
    private boolean chackFreq(String freq){
        return "AW1".equals(freq)||".".equals(freq)||"STAT".equals(freq);
    }

    /**
     * 判断数否是医嘱备注
     * @param parm TParm
     * @return boolean
     */
    private boolean ifZ00Order(TParm parm) {
        String orderCode = (String) parm.getData("ORDER_CODE");
        return orderCode.startsWith("Z");
    }

    /**
     * 整理连接医嘱ORDER_DESC
     * @param order TParm
     * @param parm TParm
     * @return String
     */
    private String getLinkOrder(TParm order, TParm parm) {
        String resultDesc = "";
        String mainOrder = (String) order.getData("ORDER_DESC");
        String mainNote = (String) order.getData("DR_NOTE");
        String mainmediQty = order.getData("MEDI_QTY") + "";
        String mainUnit = (String) order.getData("UNIT_CHN_DESC");
        String mainFreq = (String) order.getData("FREQ_CODE");
        String mainDose = (String) order.getData("ROUTE_CODE");
        String mainLinkNo = (String) order.getData("LINK_NO");
        /*String mainRxKind=(String) order.getData("RX_KIND");*/
        resultDesc = mainOrder + " " + mainmediQty + "" + mainUnit +
            (mainNote != null && mainNote.length() != 0 ?
             "\r" + "(" + mainNote + ")" : "");
        int count = parm.getCount();
        for (int i = 0; i < count; i++) {
            String linkNo = (String) parm.getData("LINK_NO", i);
            /*String rxKind=(String) parm.getData("RX_KIND", i);*/
            if (/*rxKind.equals(mainRxKind) &&*/ mainLinkNo.equals(linkNo) &&
                !TypeTool.getBoolean(parm.getData("LINKMAIN_FLG", i))) {
                String subOrder = (String) parm.getData("ORDER_DESC", i);
                String submediQty = parm.getData("MEDI_QTY", i) + "";
                String subUnit = (String) parm.getData("UNIT_CHN_DESC", i);
                String subNote = (String) parm.getData("DR_NOTE", i);
                resultDesc += "\r" + subOrder + " " + submediQty + "" + subUnit +
                    (subNote != null && subNote.length() != 0 ?
                     "\r" + "(" + subNote + ")" : "");
            }
            else
                continue;
        }
        resultDesc += "\r     " + mainFreq + " " + getRouteDesc(mainDose) + " " + "第 " +
            mainLinkNo + " 组";
        return resultDesc;
    }


    private String getSelectSQL(String orderFlg) {
        String sql = "";
        //临时and出院带药
        if ("ST".equals(orderFlg)||"DS".equals(orderFlg)) {
            sql = " SELECT   TO_CHAR (A.EFF_DATE, 'MM/DD') AS EFF_DATE_DAY, " +
                " TO_CHAR (A.EFF_DATE, 'HH24:MI') AS EFF_DATE_TIME, " +
                " A.ORDER_DR_CODE,A.ORDER_DESC,A.MEDI_QTY,F.UNIT_CHN_DESC,A.FREQ_CODE, " +
                " A.DOSE_TYPE,A.LINKMAIN_FLG,A.LINK_NO,A.DR_NOTE,A.ORDER_CODE,A.CAT1_TYPE, " +
                " TO_CHAR (B.NS_EXEC_DATE,'MM/DD HH24:MI') AS NS_EXEC_DATE,B.NS_EXEC_CODE,A.RX_KIND,A.ROUTE_CODE " +
                " FROM   ODI_ORDER A, SYS_UNIT F,ODI_DSPNM B " +
                " WHERE  A.CASE_NO='" + this.getCaseNo() + "'" +
                " AND A.CASE_NO=B.CASE_NO (+)" +
                " AND A.ORDER_NO=B.ORDER_NO (+)" +
                " AND A.ORDER_SEQ=B.ORDER_SEQ (+)" +
                " AND A.RX_KIND='" + orderFlg + "' " +
                " AND A.HIDE_FLG = 'N' " +
                " AND A.MEDI_UNIT = F.UNIT_CODE (+)" +
                " ORDER BY   A.EFF_DATE ";
        }else{
            sql = " SELECT TO_CHAR(A.EFF_DATE,'MM/DD') AS EFF_DATE_DAY,TO_CHAR(A.EFF_DATE,'HH24:MI') AS EFF_DATE_TIME, " +
                " A.ORDER_DR_CODE,A.NS_CHECK_CODE,A.ORDER_DESC,A.MEDI_QTY, " +
                " F.UNIT_CHN_DESC,A.FREQ_CODE,A.DOSE_TYPE,A.LINKMAIN_FLG,A.LINK_NO, " +
                " A.DR_NOTE,A.ORDER_CODE,A.CAT1_TYPE, " +
                " TO_CHAR(A.DC_DATE,'MM/DD') AS DC_DATE_DAY,TO_CHAR(A.DC_DATE,'HH24:MI') AS DC_DATE_TIME, " +
                " A.DC_DR_CODE,A.DC_NS_CHECK_CODE,A.RX_KIND,A.ROUTE_CODE " +
                " FROM   ODI_ORDER A,SYS_UNIT F " +
                " WHERE  A.CASE_NO='" + this.getCaseNo() + "' " +
                " AND A.RX_KIND='" + orderFlg + "' " +
                " AND A.HIDE_FLG='N' " +
                " AND A.MEDI_UNIT=F.UNIT_CODE (+)" +
                " ORDER BY A.EFF_DATE ";
        }
//        System.out.println("医嘱===sql====》"+sql);
        return sql;
    }

    /**
     * 判断是否是连接医嘱
     * @return boolean
     */
    private boolean ifLinkOrder(TParm oneOrder) {
        String LinkNo = (String) oneOrder.getData("LINK_NO");
        if (LinkNo == null || LinkNo.length() == 0)
            return false;
        return true;
    }

    /**
     * 判断是否是链接医嘱子项
     * @return boolean
     */
    private boolean ifLinkOrderSubItem(TParm oneOrder) {
        return!TypeTool.getBoolean(oneOrder.getData("LINKMAIN_FLG"));
    }


    /**
     * 关闭事件
     * @return boolean
     */
    public boolean onClosing() {
        return true;
    }

    public String getCaseNo() {
        return caseNo;
    }

    public String getIpdNo() {
        return ipdNo;
    }

    public String getName() {
        return name;
    }

    public String getMrN0() {
        return mrN0;
    }

    public String getDept() {
        return dept;
    }

    public void setCaseNo(String caseNo) {
        this.caseNo = caseNo;
    }

    public void setIpdNo(String ipdNo) {
        this.ipdNo = ipdNo;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMrN0(String mrN0) {
        this.mrN0 = mrN0;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public static void main(String[] args) {

        JavaHisDebug.initClient();
        //JavaHisDebug.initServer();
//        JavaHisDebug.TBuilder();
        JavaHisDebug.runFrame("inw\\INWOrderSheetPrtAndPreView.x");
    }


}
