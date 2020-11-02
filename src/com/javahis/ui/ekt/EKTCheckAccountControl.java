package com.javahis.ui.ekt;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;
import jdo.sys.SystemTool;

public class EKTCheckAccountControl extends TControl
{
  private String startDate;
  private String endDate;
  private String deptCode;
  private String user;

  public void onInit()
  {
    Timestamp date = SystemTool.getInstance().getDate();

    setValue("END_DATE", date.toString().substring(0, 10).replace('-', '/'));
    setValue("START_DATE", StringTool.rollDate(date, -7L).toString().substring(0, 10)
      .replace('-', '/'));
    callFunction("UI|DEPT_CODE|setEnabled", new Object[] { Boolean.valueOf(false) });
  }

  public void onQuery()
  {
    this.startDate = 
      (getValue("START_DATE").toString().substring(0, 10).replaceAll("/", "-") + 
      " 00:00:00");
    this.endDate = 
      (getValue("END_DATE").toString().substring(0, 10).replaceAll("/", "-") + 
      " 23:59:59");
    this.deptCode = getValueString("DEPT_CODE");
    this.user = getValueString("USER");
    String sql = 
      "SELECT A.CARD_NO, CASE WHEN A.ACCNT_TYPE = 1 THEN '¹º¿¨' WHEN A.ACCNT_TYPE = 2 THEN '»»¿¨' WHEN A.ACCNT_TYPE = 3 THEN '²¹¿¨' WHEN A.ACCNT_TYPE = 4 THEN '³äÖµ'  WHEN A.ACCNT_TYPE = 5 THEN '¿Û¿î'  WHEN A.ACCNT_TYPE = 6 THEN 'ÍË·Ñ' END ACCNT_TYPE, A.MR_NO, A.ID_NO, A.NAME, A.AMT, C.USER_NAME CREAT_USER, D.USER_NAME OPT_DATE, A.BIL_BUSINESS_NO, A.STORE_DATE  FROM EKT_BIL_PAY A, EKT_BIL_PAY B, SYS_OPERATOR C, SYS_OPERATOR D  WHERE A.ACCNT_TYPE = B.ACCNT_TYPE  AND A.GATHER_TYPE = B.GATHER_TYPE  AND A.MR_NO = B.MR_NO  AND A.ID_NO = B.ID_NO  AND A.NAME = B.NAME  AND A.AMT = B.AMT  AND A.CREAT_USER = B.CREAT_USER  AND A.OPT_USER = B.OPT_USER  AND A.CARD_NO = B.CARD_NO  AND A.BIL_BUSINESS_NO <> B.BIL_BUSINESS_NO  AND A.STORE_DATE <> B.STORE_DATE  AND A.CREAT_USER = C.USER_ID  AND A.OPT_USER = D.USER_ID  @ #  AND ABS(FLOOR(TO_NUMBER(A.STORE_DATE - B.STORE_DATE) * 24 * 60)) <= 2  AND A.STORE_DATE BETWEEN TO_DATE( '" + 
      this.startDate + 
      "', 'YYYY-MM-DD HH24:MI:SS') " + 
      " AND TO_DATE( '" + 
      this.endDate + 
      "', 'YYYY-MM-DD HH24:MI:SS') " + "  ORDER BY A.STORE_DATE";
    if (!"".equals(this.deptCode))
    {
      sql = sql.replaceFirst("@", "");
    }
    else sql = sql.replaceFirst("@", "");

    if (!"".equals(this.user))
      sql = sql.replaceFirst("#", "AND A.CREAT_USER = '" + this.user + "'");
    else {
      sql = sql.replaceFirst("#", "");
    }

    TParm result = new TParm(TJDODBTool.getInstance().select(sql));
    getTTable("TABLE").setParmValue(result);
    if (getTTable("TABLE").getRowCount() < 1)
      messageBox("E0008");
  }

  public void onClear()
  {
    clearValue("DEPT_CODE;USER");
    getTTable("TABLE").removeRowAll();
    onInit();
  }

  public TTable getTTable(String tag) {
    return (TTable)getComponent(tag);
  }
}