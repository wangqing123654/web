package com.javahis.ui.bil;

import java.util.Map;
import java.util.ArrayList;
import com.dongyang.jdo.TJDODBTool;
import java.util.HashMap;
import java.text.DecimalFormat;
import java.util.List;
import com.dongyang.data.TParm;
import java.util.Iterator;
import java.util.Vector;
import com.dongyang.util.StringTool;

/**
 * <p>Title: 票据对应(For账务明细表)</p>
 *
 * <p>Description: 票据对应(For账务明细表)</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl
 * @version 1.0
 */
public class BILRecpChargeForDetailPrint {
    /**
     * 票据
     */
    private Map chargeMap = new HashMap();

    /**
     * 设置票据
     * @param chargeMap Map
     */
    public void setChargeMap(Map chargeMap) {
        this.chargeMap = chargeMap;
    }

    /**
     * 得到票据
     * @return Map
     */
    public Map getChargeMap() {
        return chargeMap;
    }

    /**
     * 收据费用明细
     */
    private List chargeList = new ArrayList();
    public static final String CHARGE =
            "CHARGE01, CHARGE02, CHARGE03, CHARGE04, CHARGE05, CHARGE06, CHARGE07," +
            "CHARGE08, CHARGE09, CHARGE10, CHARGE11, CHARGE12, CHARGE13, CHARGE14," +
            //===zhangp 20120310 modify start
            "CHARGE15, CHARGE16, CHARGE17, CHARGE18, CHARGE19,CHARGE20,CHARGE21 ";
    //====zhangp 20120310 modify end
    /**
     * 返回结果集
     */
      private TParm result = new TParm();
    /**
     * 构造器
     */
    public BILRecpChargeForDetailPrint() {
        initCharge();
        initChargeList();
        initResult();

    }

    /**
     *初始化chargeMap
     */
    public void initCharge() {
        TParm recpParm = getChargeData();
//        System.out.println("票据" + recpParm);
        Vector columns = (Vector) recpParm.getData("SYSTEM", "COLUMNS");
        for (int i = 0; i < columns.size(); i++) {
            String value = (String) columns.get(i);
            String name = recpParm.getValue(value, 0);
            getChargeMap().put(name, value);
        }
    }

    /**
     * 初始化chargeList
     */
    public void initChargeList() {
        String[] a = StringTool.parseLine(CHARGE, ",");
        int count = a.length;
        for (int i = 0; i < count; i++) {
            chargeList.add(a[i]);
        }
    }

    /**
     * 初始化result
     */
    public void initResult() {
        result.addData("SYSTEM", "COLUMNS", "MR_NO");
        result.addData("SYSTEM", "COLUMNS", "IPD_NO");
        result.addData("SYSTEM", "COLUMNS", "PAT_NAME");
        result.addData("SYSTEM", "COLUMNS", "CTZ_DESC");
        result.addData("SYSTEM", "COLUMNS", "DEPT_ABS_DESC");
        result.addData("SYSTEM", "COLUMNS", "STATION_DESC");
        result.addData("SYSTEM", "COLUMNS", "TOT_AMT");
        int count = chargeList.size();

        for (int i = 0; i < count; i++) {
            result.addData("SYSTEM", "COLUMNS", chargeList.get(i));

        }

    }

    /**
     * 返回列头名
     * @param recpCode String
     * @return String
     */
    public String getCharge(String recpCode) {
        return (String) getChargeMap().get(recpCode);

    }

    public void setResult(TParm result) {
        this.result = result;
    }

    public TParm getResult() {
        return result;
    }

    /**
     * 增加
     * @param mrNo String
     * @param ipdNo String
     * @param patName String
     * @param ctzDesc String
     * @param deptDesc String
     * @param stationCode String
     */
    public void addResult(String mrNo, String ipdNo, String patName,
                          String ctzDesc, String deptDesc, String stationCode,String caseNo) {
        result.addData("MR_NO", mrNo);
        result.addData("IPD_NO", ipdNo);
        result.addData("PAT_NAME", patName);
        result.addData("CTZ_DESC", ctzDesc);
        result.addData("DEPT_ABS_DESC", deptDesc);
        result.addData("STATION_DESC", stationCode);
        result.addData("CASE_NO", caseNo);
        Iterator deptIterator = getChargeMap().keySet().iterator();
        while (deptIterator.hasNext()) {
            String name = (String) deptIterator.next();
            String value = (String) getChargeMap().get(name);
            result.addData(value, 0.00);
        }
        result.addData("TOT_AMT", 0.00);
        result.setCount(result.getCount("IPD_NO"));

    }

    /**
     * 查找科室对应行号
     * @param ipdNo String
     * @return int
     */
    public int findDept(String ipdNo,String deptDesc,String mrNo,String caseNo) {//======pangben 2014-6-5 添加病案号参数，新生儿数据显示，
    	                                                          //同样的住院号情况校验 	
        if (result.getCount() <= 0){
            return -1;
        }//修改判断科室 caoyong
        if (((Vector) result.getData("CASE_NO")).indexOf(caseNo)!=-1) {
			return ((Vector) result.getData("CASE_NO")).indexOf(caseNo);//=====pangben 2014-6-5 查询唯一键位置
		}else{
			return -1;
		}
    }

    /**
     * 给每个charge赋值
     * @param mrNo String
     * @param ipdNo String
     * @param patName String
     * @param ctzDesc String
     * @param deptDesc String
     * @param stationCode String
     * @param recpCode String
     * @param fee double
     */
    public void setValue(String mrNo, String ipdNo, String patName,
                         String ctzDesc,
                         String deptDesc, String stationCode, String recpCode,
                         double fee,String caseNo) {
        int row = findDept(ipdNo,deptDesc,mrNo,caseNo);
        if (row == -1) {
            this.addResult(mrNo, ipdNo, patName, ctzDesc, deptDesc, stationCode,caseNo);
            row = result.getCount() - 1;
        }
        String chargeCode = getCharge(recpCode);
        double value = result.getDouble(chargeCode, row) + fee;
        DecimalFormat df = new DecimalFormat("##########0.00");
        result.setData(chargeCode, row, df.format(value));
    }

    /**
     * 得到charge数据
     * @return TParm
     */
    public TParm getChargeData() {
        String recpSql =
                " SELECT " + CHARGE +
                "   FROM BIL_RECPPARM " +
                "  WHERE ADM_TYPE = 'I' ";
        return new TParm(TJDODBTool.getInstance().select(recpSql));

    }

    /**
     * 单行合计
     * @param row int
     */
    public void sumRowTot(int row) {
        double totAmt = 0.00;
        Iterator deptIterator = getChargeMap().keySet().iterator();
        while (deptIterator.hasNext()) {
            String name = (String) deptIterator.next();
            String value = (String) getChargeMap().get(name);
            totAmt += result.getDouble(value, row);
        }
        DecimalFormat df = new DecimalFormat("##########0.00");
        result.setData("TOT_AMT", row, df.format(totAmt));
    }

    /**
     * 单行合计
     */
    public void sumTot() {
        int count = result.getCount();
        
        for (int i = 0; i < count; i++) {
            sumRowTot(i);
        }
    }

    /**
     * 总计
     */
    public void allSumTot() {
        this.addResult("合计:", "", "", "", "", "","");
        int count = result.getCount() - 1;
        DecimalFormat df = new DecimalFormat("##########0.00");
        Iterator deptIterator = getChargeMap().keySet().iterator();
        double arAmt = 0.00;
        while (deptIterator.hasNext()) {
            double totAmt = 0.00;
            String name = (String) deptIterator.next();
            String value = (String) getChargeMap().get(name);
            for (int i = 0; i < count; i++)
                totAmt += result.getDouble(value, i);
            result.setData(value, count, df.format(totAmt));
            arAmt = arAmt + totAmt;
        }
        result.setData("TOT_AMT", count, df.format(arAmt));
    }

    /**
     * 返回最终报表数据
     * @param parm TParm
     * @return TParm
     */
    public TParm getValue(TParm parm) {
        int count = parm.getCount();
        for (int i = 0; i < count; i++) {
            String mrNo = parm.getValue("MR_NO", i);
            String ipdNo = parm.getValue("IPD_NO", i);
            String caseNo = parm.getValue("CASE_NO", i);
            String patName = parm.getValue("PAT_NAME", i);
            String ctzDesc = parm.getValue("CTZ_DESC", i);
            String deptCode = parm.getValue("DEPT_ABS_DESC", i);
            String recpCode = parm.getValue("REXP_CODE", i);
            String stationCode = parm.getValue("STATION_DESC", i);
            double totAmt = parm.getDouble("TOT_AMT", i);
            this.setValue(mrNo, ipdNo, patName, ctzDesc, deptCode, stationCode,
                          recpCode, totAmt,caseNo);
        }
        this.sumTot(); //单行合计
        this.allSumTot(); //总计
        return getResult();
    }
}
