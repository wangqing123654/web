package com.javahis.ui.opb;

import com.dongyang.control.*;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.data.TParm;
import jdo.sys.DictionaryTool;
import com.dongyang.util.StringTool;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import jdo.sys.SystemTool;
import com.dongyang.ui.TTable;
import java.sql.Timestamp;
import jdo.opb.OPBBillingDeptListTool;
import java.util.HashSet;
import java.util.Iterator;
import java.text.DecimalFormat;
import jdo.sys.Operator;
import com.javahis.util.ExportExcelUtil;
import com.dongyang.ui.TComboBox;
import jdo.sys.SYSRegionTool;

/**
 *
 * <p>Title: 开单科室统计表</p>
 *
 * <p>Description: 开单科室统计表</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2010-4-9
 * @version 1.0
 */
public class OPBBillingDeptListControl
    extends TControl {
    TParm data = new TParm();
    private String header = ""; //保存TABLE的HEADER
    private String ParmMap = ""; //保存TABLE的ParmMap
    private String chageList = ""; //记录费用类型
    private TTable TABLE;
    private String startDate = "";//查询起日
    private String endDate = "";//查询止日
    private String s_date = ""; //报表打印时结算的开始日期
    private String e_date = ""; //报表打印时结算的开始日期
    private  DecimalFormat df = new DecimalFormat("0.00");//pangben modify 20110425
    /**
     * 初始化界面
     */
    public void onInit() {
        super.onInit();
        String title= (String) this.getParameter();
        this.setTitle(title);
        //table1的双击侦听事件
        callFunction("UI|TABLE|addEventListener",
                     "TABLE->" + TTableEvent.DOUBLE_CLICKED, this,
                     "onDoubleClick");
        TABLE = (TTable)this.getComponent("TABLE");
        //给TABLE初始化
        initTable();
        onClear();
        //================pangben modify 20110405 start 区域锁定
        setValue("REGION_CODE", Operator.getRegion());
        //================pangben modify 20110405 stop
        //========pangben modify 20110421 start 权限添加
        TComboBox cboRegion = (TComboBox)this.getComponent("REGION_CODE");
        cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(this.
                getValueString("REGION_CODE")));
        //===========pangben modify 20110421 stop
//        pageInit();
    }
    /**
     * 页面初始化
     */
    private void pageInit(){
        //组长权限
        if(this.getPopedem("LEADER")){
           callFunction("UI|DR_CODE|setEnabled",true);
        }
        //全院
        if(this.getPopedem("ALL")){
           callFunction("UI|DEPT_CODE|setEnabled",true);
           callFunction("UI|DR_CODE|setEnabled",true);
        }

        this.setValue("DEPT_CODE",Operator.getDept());
        this.setValue("DR_CODE",Operator.getID());
    }
    /**
     * 清空事件
     */
    public void onClear(){
        //获取系统时间
        Timestamp now = SystemTool.getInstance().getDate();
        this.setValue("DATE_S",now);
        this.setValue("TIME_S", StringTool.getTimestamp("00:00:00","HH:mm:ss"));
        this.setValue("DATE_E",now);
        this.setValue("TIME_E",StringTool.getTimestamp("23:59:59","HH:mm:ss"));
        //StringTool.getTimestamp(StringTool.getString(now,"HH:mm:ss"),"HH:mm:ss")
//        TABLE.removeRowAll();
        TParm resultParm = new TParm();
        TABLE.setParmValue(resultParm);
        this.clearValue("DEPT_CODE;DR_CODE;ADM_TYPE");
        //============pangben modify 20110422
        this.setValue("REGION_CODE",Operator.getRegion());
    }
    /**
     * 表格双击事件
     * @param row int
     */
    public void onDoubleClick(int row){
        TParm parm = new TParm();
        parm.setData("DATE_S",startDate);
        parm.setData("DATE_E",endDate);
        parm.setData("DEPT_CODE",data.getValue("DEPT_CODE",row));
        parm.setData("DR_CODE",data.getValue("DR_CODE",row));
        this.openDialog("%ROOT%\\config\\opb\\OPBBillDetial.x",parm);
    }

    /**
     * 导出Excel表格
     */
    public void onExport() {
        TTable table = (TTable) callFunction("UI|TABLE|getThis");
        if (table.getRowCount() <= 0) {
            messageBox("无导出资料");
            return;
        }
        ExportExcelUtil.getInstance().exportExcel(table, "开单科室统计表");
    }

    /**
     * 给TABLE赋HEADER
     */
    public void initTable() {
        TParm parm = new TParm();
        parm.setData("ADM_TYPE", getValueString("ADM_TYPE"));
        //=====pangben modify 20110414 start 添加区域列显示
        header += "科别,100;总金额,80;西药费,80;";
        ParmMap += "DEPT_DESC;AR_AMT;CHANGE1AND2;";
         //=====pangben modify 20110414 start
        String alignment = "0,left;1,right;2,right;";//列对齐方式

        //查询收据费用名称及编码

        TParm charge = DictionaryTool.getInstance().getGroupList(
            "SYS_CHARGE");
      //===zhangp 20120313 start
        String chargeAll = "";
        for (int i = 1; i < 31; i++) {
        	if(i<10){
        		chargeAll+=",CHARGE0"+i;
        	}else{
        		chargeAll+=",CHARGE"+i;
        	}
    	}
        chargeAll = chargeAll.substring(1, chargeAll.length());
        String sql = 
        	"SELECT "+chargeAll+" FROM BIL_RECPPARM WHERE ADM_TYPE = 'O'";
        TParm chargeParm = new TParm(TJDODBTool.getInstance().select(sql));
        TParm p = new TParm();
        String chargeo = "";
        for (int j = 1; j < 31; j++) {
        	for (int i = 0; i < charge.getCount(); i++) {
        		if(j<10){
        			chargeo = "CHARGE0"+j;
        		}else{
        			chargeo = "CHARGE"+j;
        		}
        		if(charge.getData("ID", i).equals(chargeParm.getData(chargeo, 0))){
        			p.addData("CHARGE", charge.getValue("NAME", i));
//        			p.addData("ID", charge.getValue("ID", i));
        			p.addData("ID", chargeo);  //modify by huangtt 20141219
        		}
    		}
    	}
        for (int i = 0; i < p.getCount("CHARGE"); i++) {
        	if("挂号费".equals(p.getValue("CHARGE", i))){
        		continue;
        	}
            header += p.getValue("CHARGE", i) + ",70;";
            chageList += p.getValue("ID", i) + ";";
            alignment += i + 3 + ",right;";
        }
        //===zhangp 20120313 end
//        System.out.println(ParmMap);
        ParmMap += chageList;
        TABLE.setHeader(header.substring(0,header.length()-1));//绑定表格时去掉最后一个分号
        TABLE.setParmMap(ParmMap.substring(0,ParmMap.length()-1));//绑定表格时去掉最后一个分号
        TABLE.setColumnHorizontalAlignmentData(alignment.substring(0,alignment.length()-1));//设置列对齐
//        System.out.println(ParmMap);
//        System.out.println(TABLE.getHeader());
    }

    /**
     * 查询配参
     * @return TParm
     */
    public TParm finishData() {
        TParm parm = new TParm();
        //区域
        if(getValueString("REGION_CODE").length()>0)
            parm.setData("REGION_CODE", getValueString("REGION_CODE"));
        //门急住别
        if(getValueString("ADM_TYPE").length()>0)
            parm.setData("ADM_TYPE", getValueString("ADM_TYPE"));
        //开单科室
        if(getValueString("DEPT_CODE").length()>0)
            parm.setData("DEPT_CODE", getValueString("DEPT_CODE"));
        //开单医师
        if(getValueString("DR_CODE").length()>0)
            parm.setData("DR_CODE", getValueString("DR_CODE"));
        //起日
        if(getValueString("DATE_S").length()>0)
            parm.setData("DATE_S",
                         StringTool.getString(TCM_Transform.getTimestamp(
                                              getValue("DATE_S")), "yyyyMMdd") +
                         StringTool.getString(TCM_Transform.getTimestamp(getValue(
                "TIME_S")), "HHmmss"));
        //迄日
        if(getValueString("DATE_E").length()>0)
            parm.setData("DATE_E",
                         StringTool.getString(TCM_Transform.getTimestamp(
                                              getValue("DATE_E")), "yyyyMMdd") +
                         StringTool.getString(TCM_Transform.getTimestamp(getValue(
                "TIME_E")), "HHmmss"));
        return parm;
    }

    /**
     * 条件查询
     */
    public void onQuery() {
        
        if(this.getValueString("DATE_S").length()<=0||this.getValueString("DATE_E").length()<=0){
            this.messageBox_("请选择查询起讫日期");
            return;
        }
        TParm parm = finishData();
        TParm result = selectData(parm);
        if (result.getErrCode() < 0) {
            this.messageBox("E0005");
            return;
        }
        if (result.getCount() == 0) {
            this.messageBox("E0008");
            TABLE.removeRowAll();
            return;
        }
//        //整理数据
        data = getTableData(result);
        int countAll = data.getCount("DEPT_CODE");
        //===============pangben modify 20110415 start
        for (int i = countAll - 1; i >= 0; i--) {
        	if(data.getDouble("AR_AMT", i) <= 0){ //预开检查可以直接作，不用报道，所有REG_patadm这张表中的科室字段没有值
        		 if ("".equals(data.getValue("DEPT_CODE", i)) ||
        	                data.getData("DEPT_CODE", i) == null) {
        	                data.removeRow(i);
        	            }
        	}
           
        }
        countAll=data.getCount("DEPT_CODE");
//        double []columnValue=new double[19];
//        double sumTotAMT=0.00;//合计行
//        for(int i=0;i<countAll;i++){
//            for(int j=0;j<columnValue.length;j++){
//                columnValue[j] +=
//                        StringTool.round(data.getDouble((101 + j) + "", i),
//                                         2);
//            }
//            sumTotAMT+= StringTool.round(data.getDouble("TOT_AMT", i),
//                                         2);
//        }
//        for(int i=0;i<columnValue.length;i++){
//            data.setData((101+i)+"",countAll,df.format(columnValue[i]));
//        }
        double sumArAmt = 0.00; //实收
        double sumTotAmt = 0.00; //应收
        double sumReduceAmt = 0.00; //减免
        double sumChange1and2 = 0.00; //西药费
        double []changeValue = new double[31];
        for(int i=0;i<countAll;i++){
        	for(int j = 1;j<changeValue.length;j++){
        		changeValue[j] += StringTool.round(data.getDouble("CHARGE"+ (j<10? "0"+j : j),i), 2);
        	}
        	sumArAmt += StringTool.round(data.getDouble("AR_AMT", i), 2);
        	sumTotAmt += StringTool.round(data.getDouble("TOT_AMT", i), 2);
        	sumReduceAmt += StringTool.round(data.getDouble("REDUCE_AMT", i), 2);
        	sumChange1and2 += StringTool.round(data.getDouble("CHANGE1AND2", i), 2);
        }
        for(int j = 1;j<changeValue.length;j++){
    		data.setData("CHARGE"+ (j<10? "0"+j : j), countAll ,df.format(changeValue[j]));
    	}
        data.setData("DEPT_CODE",countAll,"");
        data.setData("REGION_CHN_DESC",countAll,"");
        data.setData("DR_CODE",countAll,"");
        data.setData("DEPT_DESC",countAll,"合计:");
        data.setData("DR_NAME",countAll,"");
//        data.setData("TOT_AMT",countAll,df.format(sumTotAMT));
        data.setData("TOT_AMT",countAll,df.format(sumTotAmt));
        data.setData("AR_AMT",countAll,df.format(sumArAmt));
        data.setData("REDUCE_AMT",countAll,df.format(sumReduceAmt));
        data.setData("CHANGE1AND2",countAll,df.format(sumChange1and2));

        //===============pangben modify 20110415 stop
//        System.out.println(data);
        TABLE.setParmValue(data);
        startDate = StringTool.getString(TCM_Transform.getTimestamp(
                             getValue("DATE_S")), "yyyyMMdd");
        endDate = StringTool.getString(TCM_Transform.getTimestamp(
                             getValue("DATE_E")), "yyyyMMdd");
        
        
        
        //=====报表结算时间
        String sql="SELECT MAX (D.BILL_DATE) E_DATE,MIN(D.BILL_DATE) S_DATE" +
        		" FROM REG_PATADM A,SYS_DEPT B, BIL_OPB_RECP D,SYS_REGION E " +
        		" WHERE A.DEPT_CODE = B.DEPT_CODE(+) " +
        		" AND A.REGION_CODE = E.REGION_CODE " +
        		" AND A.REGCAN_USER IS NULL " +
        		" AND A.CASE_NO = D.CASE_NO(+) " +
        		" AND D.BILL_DATE BETWEEN TO_DATE('"+parm.getValue("DATE_S")+"','YYYYMMDDHH24MISS') " +
        				" AND TO_DATE('"+parm.getValue("DATE_E")+"','YYYYMMDDHH24MISS') " +
        		" AND D.TOT_AMT >= 0 AND D.RESET_RECEIPT_NO IS NULL ";
        
        if(parm.getValue("REGION_CODE").length() > 0){
        	sql += " AND A.REGION_CODE='"+parm.getValue("REGION_CODE")+"'";
        }
        if(parm.getValue("ADM_TYPE").length() > 0){
        	sql += " AND A.ADM_TYPE='"+parm.getValue("ADM_TYPE")+"'";
        }
        if(parm.getValue("DEPT_CODE").length() > 0){
        	sql += " AND A.DEPT_CODE='"+parm.getValue("DEPT_CODE")+"'";
        }
        if(parm.getValue("DR_CODE").length() > 0){
        	sql += " AND A.DR_CODE='"+parm.getValue("DR_CODE")+"'";
        }
//        System.out.println(sql);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        s_date = result.getValue("S_DATE", 0);
        e_date = result.getValue("E_DATE", 0);
        
    }

    private TParm getTableData(TParm re){
//        HashSet dept_list = new HashSet();//记录所有开单科室CODE（去除重复）
//        for(int i=0;i<re.getCount();i++){
//            //===========pangben modify  20110414 start  对比区域和DEPT_CODE的条件判断
//            String []temp=new String[2];
//            temp[0]=re.getValue("DEPT_CODE",i);
//            temp[1]=re.getValue("REGION_CHN_DESC",i);
//            dept_list.add(temp);//填入开单科室CODE 重复的会被过滤
//            //===========pangben modify  20110414 stop
//        }
//        TParm result = new TParm();
//        //循环每一位开单部门，汇总该部门的开单信息
//        Iterator list = dept_list.iterator();
//        while(list.hasNext()){
//            String[] dept_code = (String[])list.next();//科室CODE
//       //     System.out.println("dept_code:"+dept_code[0]);
//            int rowCount = re.getCount();
//            TParm deptData = new TParm();//记录某一科室的所有数据
//            //循环数据 将同一部门的所有数据汇总起来用以计算
//            for(int i=rowCount-1;i>=0;i--){
//                //=======pangben modify 20110414 start
//                if(dept_code[0].equals(re.getValue("DEPT_CODE",i))&&dept_code[1].equals(re.getValue("REGION_CHN_DESC",i))){
//                    deptData.addRowData(re,i);
//                    re.removeRow(i);//已经汇总的数据删除掉，减少以后的循环次数
//                }
//                //=======pangben modify 20110414 stop
//            }
//            //再根据每个医生将数据整理为表格可以绑定的形式
//            TParm tableData = this.getDrData(deptData);
//            // System.out.println("tableData:::"+tableData);
//            //将各个科室的数据汇总
//            for (int i = 0; i < tableData.getCount("DEPT_CODE"); i++) {
//                result.addRowData(tableData, i);
//            }
//
//       }
    	String[] chage = ParmMap.split(";");
    	for (int i = 0; i < re.getCount("DEPT_CODE"); i++) {
    		re.setData("CHANGE1AND2", i, re.getDouble("CHARGE01", i)+re.getDouble("CHARGE02", i));
			for (int j = 1; j < chage.length; j++) {
				re.setData(chage[j],i ,df.format(re.getDouble(chage[j], i)));
			}
			
		}
        return re;
    }
    /**
     * 将数据整理为表格可以绑定的形式
     * @param re TParm
     * @return TParm
     */
    private TParm getDrData(TParm re){
        DecimalFormat df = new DecimalFormat("0.00");
        TParm result = new TParm();
        HashSet dr_list = new HashSet();//记录所有开单医师ID（去除重复）
        for(int i=0;i<re.getCount();i++){
            //=====pangben modify 20110414 start 对比区域和DR_CODE的条件判断
            String [] temp=new String[2];
            temp[0]=re.getValue("DR_CODE",i);
            temp[1]=re.getValue("REGION_CHN_DESC",i);
            dr_list.add(temp);//填入开单医师ID 重复的会被过滤
            //=====pangben modify 20110414 stop
        }
        String[] charge_code = chageList.split(";");//每种费用的code
        //循环每一位开单医师，汇总该医师的开单信息
        Iterator list = dr_list.iterator();
        while(list.hasNext()){
            //====pangben modify 20110414
            String [] dr_code = (String[])list.next();//开单医师code
            String dr_name = "";//记录医生姓名
            String dept_desc = "";//记录科室名称
            String dept_code = "";//记录科室CODE
            String region_desc="";//===========pangben modify 20110414 记录区域
            double TOT_AMT = 0;//计算各种费用的合计
            //循环每种费用的code
            for(int h=0;h<charge_code.length;h++){
                int rowCount = re.getCount("DR_CODE");
                double ar_amt = 0;//记录费用(每种费用的值)
                for (int i = rowCount - 1; i >= 0; i--) {
                    //判断此条数据是否是该开单医师
                    if (dr_code[0].equals(re.getValue("DR_CODE", i))&&dr_code[1].equals(re.getValue("REGION_CHN_DESC", i))) {
                        dr_name = re.getValue("USER_NAME",i);
                        dept_desc = re.getValue("DEPT_CHN_DESC",i);
                        dept_code = re.getValue("DEPT_CODE",i);
                        region_desc=re.getValue("REGION_CHN_DESC",i);//=======pangben modify 20110414
                        //判断费用类别，根据费用类别插入结果集中
                        if(charge_code[h].equals(re.getValue("REXP_CODE",i))){
                            ar_amt += re.getDouble("AR_AMT",i);
                            re.removeRow(i);
                        }
                    }
                }
                TOT_AMT += ar_amt;
                result.addData(charge_code[h],df.format(ar_amt));
            }
            result.addData("REGION_CHN_DESC",region_desc);//============pangben modify 20110414
            result.addData("DEPT_CODE",dept_code);
            result.addData("DEPT_DESC",dept_desc);
            result.addData("DR_CODE",dr_code);
            result.addData("DR_NAME",dr_name);
            result.addData("TOT_AMT",df.format(TOT_AMT));
        }
        return result;
    }
    /**
     * 明细
     */
    public void onDetial() {
        int row = TABLE.getSelectedRow();
        if(row<0){
            return;
        }
        TParm parm = new TParm();
        parm.setData("DATE_S",startDate);
        parm.setData("DATE_E",endDate);
        parm.setData("DEPT_CODE",data.getValue("DEPT_CODE",row));
        parm.setData("DR_CODE",data.getValue("DR_CODE",row));
        this.openDialog("%ROOT%\\config\\opb\\OPBBillDetial.x",parm);
    }
    /**
     * 科室选择事件
     */
    public void onDeptSelect(){
        this.clearValue("DR_CODE");
        callFunction("UI|DR_CODE|onQuery");
    }
    /**
     * 打印
     */
    public void onPrint(){
        int rowCount = data.getCount("DEPT_DESC");
        if(rowCount<=0){
            return;
        }
        TParm T1 = new TParm();//表格数据
        for(int i=0;i<rowCount;i++){
            T1.addRowData(data,i);
        }
        T1.setCount(rowCount);
        String[] chage = ParmMap.split(";");
        for(int i=0;i<chage.length;i++){
            T1.addData("SYSTEM", "COLUMNS", chage[i]);
        }
        TParm printData = new TParm();
        printData.setData("T1",T1.getData());
        
//        String DATE = startDate.substring(0,4)+"/"+startDate.substring(4,6)+"/"+startDate.substring(6,8) +
//            "～" + endDate.substring(0,4) + "/" + endDate.substring(4,6) + "/" + endDate.substring(6,8);
       
        String DATE=s_date.replace("-", "/").substring(0, s_date.length()-2)+"～"+e_date.replace("-", "/").substring(0, e_date.length()-2); 
        printData.setData("DATE","TEXT","结算时间："+DATE);
        printData.setData("DEPT","TEXT",this.getText("DEPT_CODE"));
        printData.setData("printUser","TEXT",Operator.getName());
        //========pangben modify 20110419 start
        String region = ((TTable)this.getComponent("TABLE")).getParmValue().
                        getRow(0).getValue("REGION_CHN_DESC");
        printData.setData("TITLE", "TEXT",
                          (this.getValue("REGION_CODE") != null &&
                           !this.getValue("REGION_CODE").equals("") ? region :
                           "所有医院") + "开单科室收入统计表");
        //========pangben modify 20110419 stop

        printData.setData("printDate","TEXT",StringTool.getString(SystemTool.getInstance().getDate(),"yyyy/MM/dd"));
        this.openPrintDialog("%ROOT%\\config\\prt\\opb\\OPBBillingDeptList.jhw",printData);
    }
    
    public TParm selectData(TParm parm){
    	String sql="SELECT E.REGION_CHN_ABN REGION_CHN_DESC, A.DEPT_CODE, " +
    			"B.DEPT_CHN_DESC AS DEPT_DESC, SUM (D.TOT_AMT) AS TOT_AMT, " +
    			"SUM (D.CHARGE01+D.CHARGE02+D.CHARGE03+D.CHARGE04+D.CHARGE05+D.CHARGE06+D.CHARGE07+D.CHARGE08+" +
    			"D.CHARGE09+D.CHARGE10+D.CHARGE11+D.CHARGE12+D.CHARGE13+D.CHARGE14+D.CHARGE15+" +
    			"D.CHARGE16+D.CHARGE17+D.CHARGE18+D.CHARGE19+D.CHARGE20+D.CHARGE21+D.CHARGE22+" +
    			"D.CHARGE23+D.CHARGE24+D.CHARGE25+D.CHARGE26+D.CHARGE27+D.CHARGE28+D.CHARGE29+D.CHARGE30) AS AR_AMT, " +
    			"SUM (D.REDUCE_AMT) AS REDUCE_AMT, SUM (D.CHARGE01) AS CHARGE01, SUM (D.CHARGE02) AS CHARGE02, " +
    			"SUM (D.CHARGE03) AS CHARGE03, SUM (D.CHARGE04) AS CHARGE04, SUM (D.CHARGE05) AS CHARGE05, " +
    			"SUM (D.CHARGE06) AS CHARGE06, SUM (D.CHARGE07) AS CHARGE07, SUM (D.CHARGE08) AS CHARGE08, " +
    			"SUM (D.CHARGE09) AS CHARGE09, SUM (D.CHARGE10) AS CHARGE10, SUM (D.CHARGE11) AS CHARGE11, " +
    			"SUM (D.CHARGE12) AS CHARGE12, SUM (D.CHARGE13) AS CHARGE13, SUM (D.CHARGE14) AS CHARGE14, " +
    			"SUM (D.CHARGE15) AS CHARGE15, SUM (D.CHARGE16) AS CHARGE16, SUM (D.CHARGE17) AS CHARGE17, " +
    			"SUM (D.CHARGE18) AS CHARGE18, SUM (D.CHARGE19) AS CHARGE19, SUM (D.CHARGE20) AS CHARGE20, " +
    			"SUM (D.CHARGE21) AS CHARGE21, SUM (D.CHARGE22) AS CHARGE22, SUM (D.CHARGE23) AS CHARGE23, " +
    			"SUM (D.CHARGE24) AS CHARGE24, SUM (D.CHARGE25) AS CHARGE25, SUM (D.CHARGE26) AS CHARGE26, " +
    			"SUM (D.CHARGE27) AS CHARGE27, SUM (D.CHARGE28) AS CHARGE28, SUM (D.CHARGE29) AS CHARGE29, " +
    			"SUM (D.CHARGE30) AS CHARGE30, 0 AS CHANGE1AND2 " +
    			"FROM REG_PATADM A,SYS_DEPT B, BIL_OPB_RECP D,SYS_REGION E " +
    			"WHERE A.DEPT_CODE = B.DEPT_CODE(+) " +
    			"AND A.REGION_CODE = E.REGION_CODE " +
    			"AND A.REGCAN_USER IS NULL " +
    			"AND A.CASE_NO = D.CASE_NO(+) " +
    			"AND D.BILL_DATE BETWEEN TO_DATE('"+parm.getValue("DATE_S")+"','YYYYMMDDHH24MISS') AND TO_DATE('"+parm.getValue("DATE_E")+"','YYYYMMDDHH24MISS') ";
//    			"AND D.TOT_AMT >= 0 AND D.RESET_RECEIPT_NO IS NULL ";
    			

    	if(parm.getValue("REGION_CODE") != null && parm.getValue("REGION_CODE").length() > 0){
    		sql += " AND A.REGION_CODE='"+parm.getValue("REGION_CODE")+"'";
    	}
    	if(parm.getValue("DEPT_CODE") != null && parm.getValue("DEPT_CODE").length() > 0){
    		sql += " AND A.DEPT_CODE='"+parm.getValue("DEPT_CODE")+"'";
    	}
    	if(parm.getValue("DR_CODE") != null && parm.getValue("DR_CODE").length() > 0){
    		sql += " AND A.DR_CODE='"+parm.getValue("DR_CODE")+"'";
    	}
    	if(parm.getValue("ADM_TYPE") != null && parm.getValue("ADM_TYPE").length() > 0){
    		sql += " AND A.ADM_TYPE='"+parm.getValue("ADM_TYPE")+"'";
    	}
    	sql += " GROUP BY E.REGION_CHN_ABN, A.DEPT_CODE,B.DEPT_CHN_DESC ORDER BY E.REGION_CHN_ABN, A.DEPT_CODE ";
//    	System.out.println(sql);
    	TParm result = new TParm(TJDODBTool.getInstance().select(sql));
    	return result;
    }
}
