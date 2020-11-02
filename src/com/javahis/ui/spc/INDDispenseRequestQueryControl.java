package com.javahis.ui.spc;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;

import jdo.spc.INDSQL;
import jdo.sys.SystemTool;

import java.sql.Timestamp;

import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.TypeTool;
import com.dongyang.data.TParm;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;

import com.javahis.util.ExportExcelUtil;

import jdo.sys.Operator;
import jdo.util.Manager;

/**
 * <p>Title: �������óɱ�ͳ�Ʊ���(�Կ���Ϊ��λ)</p>
 *
 * <p>Description: �������óɱ�ͳ�Ʊ���(�Կ���Ϊ��λ)</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *  
 * @author fux 2015.03.18
 * @version 1.0
 */  
public class INDDispenseRequestQueryControl
    extends TControl {

    public TTable table_1;  

    public INDDispenseRequestQueryControl() {   
    }

    /**
     * ��ʼ������
     */
    public void onInit() {
        table_1 = this.getTable("TABLE_1");
        // ��ʼ��ͳ������
        Timestamp date = TJDODBTool.getInstance().getDBTime();
        // ����ʱ��
        Timestamp dateTime = StringTool.getTimestamp(TypeTool.getString(date).
            substring(0, 4) + "/" + TypeTool.getString(date).
            substring(5, 7) + "/01 23:59:59", "yyyy/MM/dd HH:mm:ss");
        // (�ϸ������һ��)
        setValue("END_DATE", StringTool.rollDate(dateTime, -1));
        // ��ʼʱ��(�ϸ��µ�һ��)
        setValue("START_DATE",
                 StringTool.rollDate(dateTime, -1).toString().substring(0, 4) +
                 "/" +
                 StringTool.rollDate(dateTime, -1).toString().substring(5, 7) +
                 "/01 00:00:00");
        //20150512 wangjc add
//        getTextFormat("ORG_CODE").setPopupMenuSQL(
//				INDSQL.initTextFormatSysDeptWithRegion(Operator.getRegion()));
        this.setValue("ORG_CODE", Operator.getDept());
        this.clearValue("DEPT_CODE");
    }

    /**
     * ��ѯ����
     */  
    public void onQuery() {
        String start_date = getValueString("START_DATE");
        start_date = start_date.substring(0, 4) + start_date.substring(5, 7) +
            start_date.substring(8, 10) + start_date.substring(11, 13) +
            start_date.substring(14, 16) + start_date.substring(17, 19);
        String end_date = getValueString("END_DATE");
        end_date = end_date.substring(0, 4) + end_date.substring(5, 7) +         
            end_date.substring(8, 10) + end_date.substring(11, 13) + 
            end_date.substring(14, 16) + end_date.substring(17, 19);     

        String where = "";   
        String where_1 = "";
        String where_2 = "";
        if (!"".equals(this.getValueString("DEPT_CODE"))) {
            where += " AND M.APP_ORG_CODE = '" + getValueString("DEPT_CODE") +
                "' ";
        }
        if (!"".equals(this.getValueString("ORG_CODE"))) {
            where_1 += " AND M.TO_ORG_CODE = '" + getValueString("ORG_CODE") +
                "' ";
//            where_2 += " AND A.EXE_DEPT_CODE = '" + getValueString("ORG_CODE") +
//                "' ";
        }
        
        String sql_1 = "SELECT XX.DEPT_CHN_DESC, SUM (XX.OWN_AMT) AS OWN_AMT, SUM (XX.COST_AMT) AS COST_AMT, XX.TYPE_CODE, XX.DEPT_CODE FROM (" +
        " SELECT   A.DEPT_CHN_DESC, SUM (D.QTY*D.RETAIL_PRICE) "+
        //"  AS OWN_AMT, SUM (D.QTY*D.VERIFYIN_PRICE) AS COST_AMT, B.PHA_TYPE, A.DEPT_CODE "+ 
        "  AS OWN_AMT, SUM (D.QTY*D.VERIFYIN_PRICE) AS COST_AMT, B.TYPE_CODE, A.DEPT_CODE "+  
        "  FROM IND_DISPENSEM M, IND_DISPENSED D,SYS_DEPT A, PHA_BASE B   "+  
        "  WHERE M.APP_ORG_CODE = A.DEPT_CODE   "+
        " AND A.DEPT_CODE NOT IN (SELECT B.ORG_CODE FROM IND_ORG B WHERE B.ORG_TYPE IN ('A','B')) "+//20150521 wangjc add
        "  AND M.DISPENSE_NO = D.DISPENSE_NO "+
        "  AND D.ORDER_CODE = B.ORDER_CODE  "+        
        //"  AND M.REQTYPE_CODE = ' ' "+      
        "  AND M.UPDATE_FLG = '3'  " +
        //fux modify 20151020 ����������Ʒ�   ������Ʒ� ���� ��ҩ�ɱ�ͳ����
        //liuyl 20161121 ���ӿ�����   ��� union all
        //modify by wukai 20170208 ���������� �ĵ�union all���� �������Ϊ����
        //" AND (M.REQTYPE_CODE != 'EXM' OR M.REQTYPE_CODE != 'DRT') " + 
        " AND M.REQTYPE_CODE != 'EXM' AND M.REQTYPE_CODE != 'DRT' " +
//        "  AND M.WAREHOUSING_DATE BETWEEN TO_DATE('" + start_date + 
//            "', 'YYYYMMDDHH24MISS') AND TO_DATE('" + end_date +
//            "', 'YYYYMMDDHH24MISS') " + where + where_1 +  
        "  AND M.DISPENSE_DATE BETWEEN TO_DATE('" + start_date +     
        "', 'YYYYMMDDHH24MISS') AND TO_DATE('" + end_date +      
        "', 'YYYYMMDDHH24MISS') " + where + where_1 +       
        //fux modify ���첻���Ƿ����ٴ�����
        //" AND A.CLASSIFY = '0'     " +      
        " GROUP BY A.DEPT_CHN_DESC,B.TYPE_CODE,A.DEPT_CODE   "+   
        //wukai modify on 20170208 ====  start ���ӿ���������
        " UNION ALL " +
        "SELECT   " + 
        "A.DEPT_CHN_DESC, -SUM (D.QTY*D.RETAIL_PRICE)AS OWN_AMT, -SUM (D.QTY*D.VERIFYIN_PRICE) AS COST_AMT, B.TYPE_CODE, A.DEPT_CODE  " +
        "FROM IND_DISPENSEM M, IND_DISPENSED D,SYS_DEPT A, PHA_BASE B   WHERE M.APP_ORG_CODE = A.DEPT_CODE   " +
        "AND A.DEPT_CODE NOT IN (SELECT B.ORG_CODE FROM IND_ORG B WHERE B.ORG_TYPE IN ('A','B')) " +
        "AND M.DISPENSE_NO = D.DISPENSE_NO  " +
        "AND D.ORDER_CODE = B.ORDER_CODE  " +
        "AND M.UPDATE_FLG = '3' " +
        "AND M.REQTYPE_CODE = 'DRT'  " +
        "AND M.DISPENSE_DATE BETWEEN TO_DATE('" + start_date + "', 'YYYYMMDDHH24MISS') " +
        "AND TO_DATE('" + end_date + "', 'YYYYMMDDHH24MISS') " + 
        where + where_1 + 
        "GROUP BY A.DEPT_CHN_DESC,B.TYPE_CODE,A.DEPT_CODE " +
        " ) XX GROUP BY XX.DEPT_CHN_DESC, XX.TYPE_CODE, XX.DEPT_CODE"  ;
        //wukai modify on 20170208 ====  end ���ӿ���������
        
        String sql_2 = " SELECT   A.DEPT_CHN_DESC, SUM (D.QTY*D.RETAIL_PRICE) "+
        "  AS OWN_AMT, SUM (D.QTY*D.VERIFYIN_PRICE) AS COST_AMT, B.TYPE_CODE, A.DEPT_CODE "+
        //"  AS OWN_AMT, SUM (D.QTY*D.VERIFYIN_PRICE) AS COST_AMT, B.PHA_TYPE, A.DEPT_CODE "+  
        "  FROM IND_BASEMANAGEM M, IND_BASEMANAGED D,SYS_DEPT A, PHA_BASE B   "+
        "  WHERE M.APP_ORG_CODE = A.DEPT_CODE   "+
        " AND A.DEPT_CODE NOT IN (SELECT B.ORG_CODE FROM IND_ORG B WHERE B.ORG_TYPE IN ('A','B')) "+//20150521 wangjc add
        "  AND M.BASEMANAGE_NO = D.BASEMANAGE_NO "+  
        "  AND D.ORDER_CODE = B.ORDER_CODE  "+   
        //fux modify 20151020 ����������Ʒ�   ������Ʒ� ���� ��ҩ�ɱ�ͳ����
        //"  AND M.REQTYPE_CODE in ('COS','EXM') "+  
        "  AND M.REQTYPE_CODE = 'COS' "+  
        "  AND M.REQUEST_DATE BETWEEN TO_DATE('" + start_date +        
            "', 'YYYYMMDDHH24MISS') AND TO_DATE('" + end_date +  
            "', 'YYYYMMDDHH24MISS') " + where + where_1 +
        "  GROUP BY A.DEPT_CHN_DESC,B.TYPE_CODE,A.DEPT_CODE   "+     
        "  ORDER BY A.DEPT_CODE ";
        
          
//        String sql_1 = "SELECT   B.DEPT_CHN_DESC, SUM (A.AR_AMT) "
//            +
//            " AS OWN_AMT, SUM (A.COST_AMT) AS COST_AMT, D.PHA_TYPE, A.DEPT_CODE "
//            + " FROM IND_DISPENSEM A, SYS_DEPT B, PHA_BASE D "
//            + " WHERE A.DEPT_CODE = B.DEPT_CODE "
//            + " AND A.ORDER_CODE = D.ORDER_CODE "
//            + " AND A.PHA_DOSAGE_DATE IS NOT NULL "  
//            + " AND A.PHA_RETN_CODE IS NULL "  
//            + " AND B.FINAL_FLG = 'Y' "                                
//            + " AND B.CLASSIFY = '0' "
//            + " AND A.REGION_CODE = '" + Operator.getRegion() + "' "
//            + " AND A.PHA_DOSAGE_DATE BETWEEN TO_DATE('" + start_date +
//            "', 'YYYYMMDDHH24MISS') AND TO_DATE('" + end_date +
//            "', 'YYYYMMDDHH24MISS') " + where + where_1
//            + " GROUP BY B.DEPT_CHN_DESC, D.PHA_TYPE, "    
//            + " A.DEPT_CODE ORDER BY A.DEPT_CODE ";
        //System.out.println("sql_1----" + sql_1);

//        String sql_2 = "SELECT D.DEPT_CHN_DESC, SUM (A.OWN_AMT) "
//            +
//            " AS OWN_AMT, SUM (A.COST_AMT) AS COST_AMT, C.PHA_TYPE, A.DEPT_CODE "
//            + " FROM IBS_ORDD A, IBS_ORDM B, PHA_BASE C, SYS_DEPT D "
//            + " WHERE A.CASE_NO = B.CASE_NO "
//            + " AND A.CASE_NO_SEQ = B.CASE_NO_SEQ "
//            + " AND A.ORDER_CODE = C.ORDER_CODE "  
//            + " AND A.DEPT_CODE = D.DEPT_CODE "  
//            + " AND B.DATA_TYPE = '2' "
//            + " AND D.FINAL_FLG = 'Y' "
//            + " AND D.CLASSIFY = '0' "
//            + " AND B.REGION_CODE = '" + Operator.getRegion() + "' "
//            + " AND A.BILL_DATE BETWEEN TO_DATE('" + start_date +
//            "', 'YYYYMMDDHH24MISS') AND TO_DATE('" + end_date +
//            "', 'YYYYMMDDHH24MISS') " + where + where_2
//            + " GROUP BY D.DEPT_CHN_DESC, C.PHA_TYPE, "       
//            + " A.DEPT_CODE ORDER BY A.DEPT_CODE ";  
//        System.out.println("sql_1----" + sql_1);
//        System.out.println("sql_2----" + sql_2);
        TParm parm_1 = new TParm(TJDODBTool.getInstance().select(sql_1));
        TParm parm_2 = new TParm(TJDODBTool.getInstance().select(sql_2));

        TParm parm = new TParm();     
        if (parm_1 == null || parm_1.getCount("DEPT_CHN_DESC") <= 0) {
            if (parm_2 == null || parm_2.getCount("DEPT_CHN_DESC") <= 0) {
                this.messageBox("û�в�ѯ����");
                TParm parmC= new TParm();
                table_1.setParmValue(parmC);
                return;
            }
            parm = parm_2;
        }
        else if (parm_2 == null || parm_2.getCount("DEPT_CHN_DESC") <= 0) {
            if (parm_1 == null || parm_1.getCount("DEPT_CHN_DESC") <= 0) {
                this.messageBox("û�в�ѯ����");
                TParm parmC= new TParm();
                table_1.setParmValue(parmC);
                return;
            }
            parm = parm_1;
        }
        else {    
            //String dr_code = "";
            String org_code = "";
            String pha_type = "";
            for (int i = 0; i < parm_2.getCount("DEPT_CHN_DESC"); i++) {
                boolean flg = false;
                //dr_code = parm_2.getValue("DR_CODE", i);
                org_code = parm_2.getValue("DEPT_CODE", i);
                pha_type = parm_2.getValue("TYPE_CODE", i);
                for (int j = 0; j < parm_1.getCount("DEPT_CHN_DESC"); j++) {
                    if (org_code.equals(parm_1.getValue("DEPT_CODE", j)) &&
                        pha_type.equals(parm_1.getValue("TYPE_CODE", j))) {
                        parm_1.setData("OWN_AMT", j, StringTool.round(
                            parm_1.getDouble("OWN_AMT", j) +
                            parm_2.getDouble("OWN_AMT", i), 2));
                        parm_1.setData("COST_AMT", j, StringTool.round(
                            (parm_1.getDouble("COST_AMT", j) +
                             parm_2.getDouble("COST_AMT", i)), 2));
                        flg = true;
                        break;
                    }
                }
                if (!flg) {
                    parm_1.addData("DEPT_CHN_DESC",
                                   parm_2.getValue("DEPT_CHN_DESC", i));
                    parm_1.addData("OWN_AMT",
                                   StringTool.round(parm_2.getDouble("OWN_AMT",
                        i), 2));
                    parm_1.addData("COST_AMT", StringTool.round(parm_2.
                        getDouble("COST_AMT", i), 2));
                    parm_1.addData("TYPE_CODE", parm_2.getValue("TYPE_CODE", i));
                    parm_1.addData("DEPT_CODE", parm_2.getValue("DEPT_CODE", i));
                }
            }
            parm = parm_1;
            if (parm_1 == null || parm_1.getCount("DEPT_CHN_DESC") <= 0) {
                this.messageBox("û�в�ѯ����");
                TParm parmC= new TParm();
                table_1.setParmValue(parmC);
                return;
            }   
        }

        TParm data = new TParm();

        String dept_code = "";
        String dr_code = "";
        data.addData("DEPT_CHN_DESC", "");
        data.addData("OWN_AMT_W", 0);
        data.addData("COST_AMT_W", 0);
        data.addData("DIFF_AMT_W", 0);
        data.addData("OWN_AMT_C", 0);
        data.addData("COST_AMT_C", 0);
        data.addData("DIFF_AMT_C", 0);
        data.addData("OWN_AMT_G", 0);
        data.addData("COST_AMT_G", 0);
        data.addData("DIFF_AMT_G", 0);
        //fux modify 20150317
        data.addData("OWN_AMT_T", 0);  
        data.addData("COST_AMT_T", 0);
        data.addData("DIFF_AMT_T", 0);
        data.addData("DEPT_CODE", "");

        for (int i = 0; i < parm.getCount("DEPT_CHN_DESC"); i++) {
            boolean flg = false;
            dept_code = parm.getValue("DEPT_CODE", i);
            if (i == 0) {
                data.setData("DEPT_CHN_DESC", i,
                             parm.getValue("DEPT_CHN_DESC", i));
                //if ("W".equals(parm.getValue("PHA_TYPE", i))) {
                if ("1".equals(parm.getValue("TYPE_CODE", i))) {
                    data.setData("OWN_AMT_W", i, parm.getDouble("OWN_AMT", i));
                    data.setData("COST_AMT_W", i, parm.getDouble("COST_AMT", i));
                    data.setData("DIFF_AMT_W", i, StringTool.round(parm.getDouble("OWN_AMT", i) -
                                 parm.getDouble("COST_AMT", i), 2));
                }
                //else if ("C".equals(parm.getValue("PHA_TYPE", i))) {
                else if ("2".equals(parm.getValue("TYPE_CODE", i))) {
                    data.setData("OWN_AMT_C", i, parm.getDouble("OWN_AMT", i));
                    data.setData("COST_AMT_C", i, parm.getDouble("COST_AMT", i));
                    data.setData("DIFF_AMT_C", i, StringTool.round(parm.getDouble("OWN_AMT", i) -
                                 parm.getDouble("COST_AMT", i), 2));
                }                                                   
                //else {  
                else if ("4".equals(parm.getValue("TYPE_CODE", i))) {
                    data.setData("OWN_AMT_G", i, parm.getDouble("OWN_AMT", i));
                    data.setData("COST_AMT_G", i, parm.getDouble("COST_AMT", i));
                    data.setData("DIFF_AMT_G", i, StringTool.round(parm.getDouble("OWN_AMT", i) -
                                 parm.getDouble("COST_AMT", i), 2));
                }
                data.setData("DEPT_CODE", i, parm.getValue("DEPT_CODE", i));
            }
            else {
                for (int j = 0; j < data.getCount("DEPT_CHN_DESC"); j++) {
                    if (dept_code.equals(data.getValue("DEPT_CODE", j))) {
                    	if ("1".equals(parm.getValue("TYPE_CODE", i))) {
                       // if ("W".equals(parm.getValue("PHA_TYPE", i))) {
                            data.setData("OWN_AMT_W", j,  
                                         parm.getDouble("OWN_AMT", i));
                            data.setData("COST_AMT_W", j,  
                                         parm.getDouble("COST_AMT", i));   
                            data.setData("DIFF_AMT_W", j,
                                         StringTool.round(parm.getDouble("OWN_AMT", i) -
                                         parm.getDouble("COST_AMT", i),2));
                        }
                    	else if ("2".equals(parm.getValue("TYPE_CODE", i))) {
                       // else if ("C".equals(parm.getValue("PHA_TYPE", i))) {
                            data.setData("OWN_AMT_C", j,
                                         parm.getDouble("OWN_AMT", i));
                            data.setData("COST_AMT_C", j,
                                         parm.getDouble("COST_AMT", i));
                            data.setData("DIFF_AMT_C", j,
                                         StringTool.round(parm.getDouble("OWN_AMT", i) -
                                         parm.getDouble("COST_AMT", i),2));
                        }
                    	else if ("4".equals(parm.getValue("TYPE_CODE", i))) {
                        //else {  
                            data.setData("OWN_AMT_G", j,
                                         parm.getDouble("OWN_AMT", i));
                            data.setData("COST_AMT_G", j,
                                         parm.getDouble("COST_AMT", i));
                            data.setData("DIFF_AMT_G", j,
                                         StringTool.round(parm.getDouble("OWN_AMT", i) -
                                         parm.getDouble("COST_AMT", i),2));
                        }
                        flg = true;
                        break;
                    }
                }
                if (!flg) {
                    data.addData("DEPT_CHN_DESC",
                                 parm.getValue("DEPT_CHN_DESC", i));
                    data.addData("OWN_AMT_W", 0);
                    data.addData("COST_AMT_W", 0);
                    data.addData("DIFF_AMT_W", 0);
                    data.addData("OWN_AMT_C", 0);
                    data.addData("COST_AMT_C", 0);
                    data.addData("DIFF_AMT_C", 0);
                    data.addData("OWN_AMT_G", 0);
                    data.addData("COST_AMT_G", 0);
                    data.addData("DIFF_AMT_G", 0);
                    int row = data.getCount("DEPT_CHN_DESC") - 1;
                    //if ("W".equals(parm.getValue("PHA_TYPE", i))) {
                     if ("1".equals(parm.getValue("TYPE_CODE", i))) {
                        data.setData("OWN_AMT_W", row,  
                                     parm.getDouble("OWN_AMT", i));
                        data.setData("COST_AMT_W", row,
                                     parm.getDouble("COST_AMT", i));
                        data.setData("DIFF_AMT_W", row,
                                     StringTool.round(parm.getDouble("OWN_AMT", i) -
                                     parm.getDouble("COST_AMT", i),2));
                    }
                    //else if ("C".equals(parm.getValue("PHA_TYPE", i))) {
                     else if ("2".equals(parm.getValue("TYPE_CODE", i))) {
                        data.setData("OWN_AMT_C", row,
                                     parm.getDouble("OWN_AMT", i));
                        data.setData("COST_AMT_C", row,
                                     parm.getDouble("COST_AMT", i));
                        data.setData("DIFF_AMT_C", row,
                                     StringTool.round(parm.getDouble("OWN_AMT", i) -
                                     parm.getDouble("COST_AMT", i),2));
                    }
                   // else {
                     else if ("4".equals(parm.getValue("TYPE_CODE", i))) {
                        data.setData("OWN_AMT_G", row,
                                     parm.getDouble("OWN_AMT", i));
                        data.setData("COST_AMT_G", row,
                                     parm.getDouble("COST_AMT", i));
                        data.setData("DIFF_AMT_G", row,
                                     StringTool.round(parm.getDouble("OWN_AMT", i) -
                                     parm.getDouble("COST_AMT", i),2));
                    }
                    data.addData("DEPT_CODE", parm.getValue("DEPT_CODE", i));
                }
            }
        }

        Map map = new HashMap();  
        for (int i = 0; i < data.getCount("DEPT_CODE"); i++) {
            if (map.isEmpty()) {
                map.put(data.getValue("DEPT_CODE", i),
                        data.getValue("DEPT_CHN_DESC", i));
            }
            else {
                if (map.containsKey(data.getValue("DEPT_CODE", i))) {
                    continue;
                }
                else {
                    map.put(data.getValue("DEPT_CODE", i),
                            data.getValue("DEPT_CHN_DESC", i));
                }
            }
        }

        //�ϼ�
        double SUM_OWN_AMT_W = 0;
        double SUM_COST_AMT_W = 0;
        double SUM_DIFF_AMT_W = 0;
        double SUM_OWN_AMT_C = 0;
        double SUM_COST_AMT_C = 0;
        double SUM_DIFF_AMT_C = 0;
        double SUM_OWN_AMT_G = 0;
        double SUM_COST_AMT_G = 0;
        double SUM_DIFF_AMT_G = 0;

        Set set = map.keySet();
        Iterator iterator = set.iterator();
        TParm table_parm = new TParm();
        while (iterator.hasNext()) {
            //С��
            double OWN_AMT_W = 0;
            double COST_AMT_W = 0;
            double DIFF_AMT_W = 0;
            double OWN_AMT_C = 0;
            double COST_AMT_C = 0;
            double DIFF_AMT_C = 0;
            double OWN_AMT_G = 0;
            double COST_AMT_G = 0;
            double DIFF_AMT_G = 0;
            double OWN_AMT_T = 0;
            double COST_AMT_T = 0;
            double DIFF_AMT_T = 0;

            dept_code = TypeTool.getString(iterator.next());
            for (int i = 0; i < data.getCount("DEPT_CODE"); i++) {
                if (dept_code.equals(data.getValue("DEPT_CODE", i))) {
                    table_parm.addData("DEPT_CHN_DESC",
                                       data.getValue("DEPT_CHN_DESC", i));
                    OWN_AMT_W += data.getDouble("OWN_AMT_W", i);
                    table_parm.addData("OWN_AMT_W",
                    		StringTool.round(data.getDouble("OWN_AMT_W", i),2));
                    COST_AMT_W += data.getDouble("COST_AMT_W", i);
                    table_parm.addData("COST_AMT_W",
                    		StringTool.round(data.getDouble("COST_AMT_W", i),2));
                    DIFF_AMT_W += data.getDouble("DIFF_AMT_W", i);
                    table_parm.addData("DIFF_AMT_W",
                    		StringTool.round(data.getDouble("DIFF_AMT_W", i),2));
                    OWN_AMT_C += data.getDouble("OWN_AMT_C", i);
                    table_parm.addData("OWN_AMT_C",
                    		StringTool.round(data.getDouble("OWN_AMT_C", i),2));
                    COST_AMT_C += data.getDouble("COST_AMT_C", i);
                    table_parm.addData("COST_AMT_C",
                    		StringTool.round(data.getDouble("COST_AMT_C", i),2));
                    DIFF_AMT_C += data.getDouble("DIFF_AMT_C", i);
                    table_parm.addData("DIFF_AMT_C",
                    		StringTool.round(data.getDouble("DIFF_AMT_C", i),2));
                    OWN_AMT_G += data.getDouble("OWN_AMT_G", i);
                    table_parm.addData("OWN_AMT_G",
                    		StringTool.round(data.getDouble("OWN_AMT_G", i),2));
                    COST_AMT_G += data.getDouble("COST_AMT_G", i);
                    table_parm.addData("COST_AMT_G",
                    		StringTool.round(data.getDouble("COST_AMT_G", i),2));
                    DIFF_AMT_G += data.getDouble("DIFF_AMT_G", i);
                    table_parm.addData("DIFF_AMT_G",
                    		StringTool.round(data.getDouble("DIFF_AMT_G", i),2));
                    //fux modify 20150318
                    OWN_AMT_T += data.getDouble("OWN_AMT_G", i)+data.getDouble("OWN_AMT_C", i)+data.getDouble("OWN_AMT_W", i);
                    table_parm.addData("OWN_AMT_T",StringTool.round(data.getDouble("OWN_AMT_G", i)+data.getDouble("OWN_AMT_C", i)+data.getDouble("OWN_AMT_W", i)
                                       ,2));
                    COST_AMT_T += data.getDouble("COST_AMT_G", i)+data.getDouble("COST_AMT_C", i)+data.getDouble("COST_AMT_W", i);
                    table_parm.addData("COST_AMT_T",  
                    		StringTool.round(data.getDouble("COST_AMT_G", i)+data.getDouble("COST_AMT_C", i)+data.getDouble("COST_AMT_W", i),2));
                    DIFF_AMT_T += data.getDouble("DIFF_AMT_G", i)+data.getDouble("DIFF_AMT_C", i)+data.getDouble("DIFF_AMT_W", i);
                    table_parm.addData("DIFF_AMT_T",  
                    		StringTool.round(data.getDouble("DIFF_AMT_G", i)+data.getDouble("DIFF_AMT_C", i)+data.getDouble("DIFF_AMT_W", i),2));
                }
            }    
            //С��  
            
            SUM_OWN_AMT_W += OWN_AMT_W;
           
            SUM_COST_AMT_W += COST_AMT_W;
           
            SUM_DIFF_AMT_W += DIFF_AMT_W;
          
            SUM_OWN_AMT_C += OWN_AMT_C;
            
            SUM_COST_AMT_C += COST_AMT_C;
           
            SUM_DIFF_AMT_C += DIFF_AMT_C;
            
            SUM_OWN_AMT_G += OWN_AMT_G;
           
            SUM_COST_AMT_G += COST_AMT_G;
     
            SUM_DIFF_AMT_G += DIFF_AMT_G;
       
        }
    
        //�ܼ�
        table_parm.addData("DEPT_CHN_DESC", "�ܼ�:");
        table_parm.addData("USER_NAME", "");
        table_parm.addData("OWN_AMT_W", StringTool.round(SUM_OWN_AMT_W, 2));
        table_parm.addData("COST_AMT_W", StringTool.round(SUM_COST_AMT_W, 2));
        table_parm.addData("DIFF_AMT_W", StringTool.round(SUM_DIFF_AMT_W, 2));
        table_parm.addData("OWN_AMT_C", StringTool.round(SUM_OWN_AMT_C, 2));  
        table_parm.addData("COST_AMT_C", StringTool.round(SUM_COST_AMT_C, 2));
        table_parm.addData("DIFF_AMT_C", StringTool.round(SUM_DIFF_AMT_C, 2));  
        table_parm.addData("OWN_AMT_G", StringTool.round(SUM_OWN_AMT_G, 2));
        table_parm.addData("COST_AMT_G", StringTool.round(SUM_COST_AMT_G, 2));
        table_parm.addData("DIFF_AMT_G", StringTool.round(SUM_DIFF_AMT_G, 2));
        table_parm.addData("OWN_AMT_T", StringTool.round(SUM_OWN_AMT_W+SUM_OWN_AMT_C+SUM_OWN_AMT_G, 2));
        table_parm.addData("COST_AMT_T", StringTool.round(SUM_COST_AMT_W+SUM_COST_AMT_C+SUM_COST_AMT_G, 2));
        table_parm.addData("DIFF_AMT_T", StringTool.round(SUM_DIFF_AMT_W+SUM_DIFF_AMT_C+SUM_DIFF_AMT_G, 2)); 

        //System.out.println("table_parm----" + table_parm);

        table_1.setParmValue(table_parm);
    }

    /**
     * ���Excel
     */
    public void onExport() {
        if (table_1.getRowCount() <= 0) {
            this.messageBox("û�л������");
            return;
        }
        ExportExcelUtil.getInstance().exportExcel(table_1,
                                                  "����ҩƷ��ҩ�ɱ����ܱ�");
    }

    /**
     * ��ӡ����
     */
    public void onPrint() {
        if (table_1.getRowCount() <= 0) {
            this.messageBox("û�пɴ�ӡ����");
            return;
        }

        // ��ӡ����
        TParm date = new TParm();
        // ��ͷ����
        date.setData("TITLE", "TEXT",
                     Manager.getOrganization().
                     getHospitalCHNFullName(Operator.getRegion()) +
                     "����ҩƷ���óɱ�ͳ�Ʊ���");
        date.setData("ORG_CODE", "TEXT",
                     "ͳ�Ʋ���: " +
                     ("".equals(this.getText("ORG_CODE")) ? "ȫ��" :
                      this.getText("ORG_CODE")));
        String start_date = getValueString("START_DATE");
        String end_date = getValueString("END_DATE");
        date.setData("DATE_AREA", "TEXT",
                     "ͳ������: " +
                     start_date.substring(0, 4) + "/" +
                     start_date.substring(5, 7) + "/" +
                     start_date.substring(8, 10) + " " +
                     start_date.substring(11, 13) + ":" +
                     start_date.substring(14, 16) + ":" +
                     start_date.substring(17, 19) +
                     " ~ " +
                     end_date.substring(0, 4) + "/" +
                     end_date.substring(5, 7) + "/" +
                     end_date.substring(8, 10) + " " +
                     end_date.substring(11, 13) + ":" +
                     end_date.substring(14, 16) + ":" +
                     end_date.substring(17, 19));
        date.setData("DATE", "TEXT",
                     "�Ʊ�ʱ��: " +
                     SystemTool.getInstance().getDate().toString().
                     substring(0, 10).
                     replace('-', '/'));
        date.setData("USER", "TEXT", "�Ʊ���: " + Operator.getName());

        TParm tableParm = table_1.getParmValue();
        tableParm.setCount(tableParm.getCount("DEPT_CHN_DESC"));
        tableParm.addData("SYSTEM", "COLUMNS", "DEPT_CHN_DESC");
        tableParm.addData("SYSTEM", "COLUMNS", "OWN_AMT_W");
        tableParm.addData("SYSTEM", "COLUMNS", "COST_AMT_W");
        tableParm.addData("SYSTEM", "COLUMNS", "DIFF_AMT_W");
        tableParm.addData("SYSTEM", "COLUMNS", "OWN_AMT_C");
        tableParm.addData("SYSTEM", "COLUMNS", "COST_AMT_C");
        tableParm.addData("SYSTEM", "COLUMNS", "DIFF_AMT_C");
        tableParm.addData("SYSTEM", "COLUMNS", "OWN_AMT_G");
        tableParm.addData("SYSTEM", "COLUMNS", "COST_AMT_G");
        tableParm.addData("SYSTEM", "COLUMNS", "DIFF_AMT_G");
        tableParm.addData("SYSTEM", "COLUMNS", "OWN_AMT_T");
        tableParm.addData("SYSTEM", "COLUMNS", "COST_AMT_T");
        tableParm.addData("SYSTEM", "COLUMNS", "DIFF_AMT_T");
        date.setData("TABLE", tableParm.getData());

        //System.out.println("date----" + date);
        // ���ô�ӡ����
        this.openPrintWindow(
            "%ROOT%\\config\\prt\\spc\\INDDispenseRequestQuery.jhw", date);

    }

    /**
     * ��շ���
     */
    public void onClear() {
        String clearStr = "ORG_CODE;DEPT_CODE";
        this.clearValue(clearStr);

        // ��ʼ��ͳ������
        Timestamp date = TJDODBTool.getInstance().getDBTime();
        // ����ʱ��
        Timestamp dateTime = StringTool.getTimestamp(TypeTool.getString(date).
            substring(0, 4) + "/" + TypeTool.getString(date).
            substring(5, 7) + "/01 23:59:59", "yyyy/MM/dd HH:mm:ss");
        // (�ϸ������һ��)
        setValue("END_DATE", StringTool.rollDate(dateTime, -1));    
        // ��ʼʱ��(�ϸ��µ�һ��)
        setValue("START_DATE",
                 StringTool.rollDate(dateTime, -1).toString().substring(0, 4) +
                 "/" +
                 StringTool.rollDate(dateTime, -1).toString().substring(5, 7) +
                 "/01 00:00:00");
        TParm parm = new TParm();
        table_1.setParmValue(parm);  
        //table_1.removeRowAll();
    }   

    /**
     * �õ�Table����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }
    
    /**
	 * �õ�TextFormat����
	 * 
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TTextFormat getTextFormat(String tagName) {
		return (TTextFormat) getComponent(tagName);
	}

}
