package com.javahis.ui.bil;

import java.util.HashSet;
import java.util.Map;
import java.util.ArrayList;
import java.util.Set;

import com.dongyang.jdo.TJDODBTool;
import java.util.HashMap;
import java.text.DecimalFormat;
import java.util.List;
import com.dongyang.data.TParm;
import java.util.Iterator;
import java.util.Vector;
import com.dongyang.util.StringTool;
import com.sun.mail.handlers.message_rfc822;

/**
 * <p>Title: 门诊应收费用明细表</p>
 *
 * <p>Description: 门诊应收费用明细表</p>
 *
 * <p>Copyright: Copyright (c)</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author pangben 2016-5-16
 * @version 1.0
 */
public class BILAccountForDetailPrint {
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
    	 "CHARGE01,CHARGE02,CHARGE03,CHARGE04,CHARGE05,CHARGE06,CHARGE07," +
         "CHARGE08,CHARGE09,CHARGE10,CHARGE11,CHARGE12,CHARGE13,CHARGE14," +
         //===zhangp 20120310 modify start
         "CHARGE15,CHARGE16,CHARGE17,CHARGE18,CHARGE19,CHARGE20";
    //====zhangp 20120310 modify end
    /**
     * 返回结果集
     */
    private TParm result = new TParm();
    /**
     * 构造器
     */
    public BILAccountForDetailPrint() {
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
        result.addData("SYSTEM", "COLUMNS", "CASE_NO");
        result.addData("SYSTEM", "COLUMNS", "PAT_NAME");
        result.addData("SYSTEM", "COLUMNS", "CTZ1_CODE");
        result.addData("SYSTEM", "COLUMNS", "REG_DATE");
        result.addData("SYSTEM", "COLUMNS", "DEPT_CODE");
        result.addData("SYSTEM", "COLUMNS", "DR_CODE");
        result.addData("SYSTEM", "COLUMNS", "REALDEPT_CODE");
        result.addData("SYSTEM", "COLUMNS", "REALDR_CODE");
        result.addData("SYSTEM", "COLUMNS", "RECEIPT_NO");
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
    public void addResult(String mrNo,String caseNo,String patName,String ctzCode,String deptCode,String drCode,
    		String realdeptCode,String realdrCode,String regDate,String receiptNo) {
    	result.addData("SYSTEM", "COLUMNS", "MR_NO");
        result.addData("SYSTEM", "COLUMNS", "CASE_NO");
        result.addData("SYSTEM", "COLUMNS", "CTZ1_CODE");
        result.addData("SYSTEM", "COLUMNS", "PAT_NAME");
        result.addData("SYSTEM", "COLUMNS", "REG_DATE");
        result.addData("SYSTEM", "COLUMNS", "DEPT_CODE");
        result.addData("SYSTEM", "COLUMNS", "DR_CODE");
        result.addData("SYSTEM", "COLUMNS", "REALDEPT_CODE");
        result.addData("SYSTEM", "COLUMNS", "REALDR_CODE");
        result.addData("SYSTEM", "COLUMNS", "RECEIPT_NO");
        result.addData("MR_NO", mrNo);
        result.addData("CASE_NO", caseNo);
        result.addData("PAT_NAME", patName);
        result.addData("CTZ1_CODE", ctzCode);
        result.addData("REG_DATE", regDate);
        result.addData("DEPT_CODE", deptCode);
        result.addData("DR_CODE", drCode);
        result.addData("REALDEPT_CODE", realdeptCode);
        result.addData("REALDR_CODE", realdrCode);
        result.addData("RECEIPT_NO", receiptNo);
        Iterator deptIterator = getChargeMap().keySet().iterator();
        while (deptIterator.hasNext()) {
            String name = (String) deptIterator.next();
            String value = (String) getChargeMap().get(name);
            result.addData(value, 0.00);
        }
        result.setCount(result.getCount("MR_NO"));

    }
    
    /**
     * 查找科室对应行号
     * @param ipdNo String
     * @return int
     */
    public int findDept(String caseNo,String receiptNo) {
        if (result.getCount() <= 0)
            return -1;
//        System.out.println("result" + result);
        return ((Vector) result.getData("RECEIPT_NO")).indexOf(receiptNo);
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
    public void setValue(String mrNo,String caseNo,String patName,String ctzCode,String deptCode,String drCode,
    		String realdeptCode,String realdrCode,String recpCode, double totAmt,String regDate,String receiptNo) {
        int row = findDept(caseNo,receiptNo);
        if (row == -1) {
            this.addResult(mrNo, caseNo, patName, ctzCode, deptCode, drCode,
            		realdeptCode, realdrCode,regDate,receiptNo);
            row = result.getCount() - 1;
        }
        String chargeCode = getCharge(recpCode);
        double value = result.getDouble(chargeCode, row) + totAmt;
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
                "  WHERE ADM_TYPE = 'O' ";
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
        result.setData("AR_AMT", row, df.format(totAmt));
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
        this.addResult("合计:", "", "", "", "", "","","","","");
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
        //result.setData("TOT_AMT", count, df.format(arAmt));
    }

    /**
     * 返回最终报表数据
     * @param parm TParm
     * @return TParm
     */
    public TParm getValue(TParm parm) {

        int count = parm.getCount();
        //Set<String> set  = new HashSet<String>() ;
        //StringBuilder sb = new StringBuilder() ;
        //TParm myParm = new TParm() ;
        //Map<String,TParm> map = new HashMap<String, TParm>() ;
        for (int i = 0; i < count; i++) {
            String mrNo = parm.getValue("MR_NO", i);
            String caseNo = parm.getValue("CASE_NO", i);
            String patName = parm.getValue("PAT_NAME", i);
            String ctzCode = parm.getValue("CTZ1_CODE", i);
            String regDate = parm.getValue("REG_DATE", i);
            String deptCode = parm.getValue("DEPT_CODE", i);
            double totAmt = parm.getDouble("TOT_AMT", i);
            String drCode = parm.getValue("DR_CODE", i);
            String realdeptCode = parm.getValue("REALDEPT_CODE", i);
            String realdrCode = parm.getValue("REALDR_CODE", i);
            String recpCode = parm.getValue("REXP_CODE", i);
            String receiptNo = parm.getValue("RECEIPT_NO", i);
            this.setValue(mrNo, caseNo, patName, ctzCode, deptCode, drCode,
            		realdeptCode, realdrCode,recpCode,totAmt,regDate,receiptNo);
            
            
          //modify by lim 2012/05/14 end
        }
        sumTot();
        allSumTot();
        return result;
    }
}
