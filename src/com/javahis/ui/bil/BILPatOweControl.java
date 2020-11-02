package com.javahis.ui.bil;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.javahis.ui.testOpb.bean.OpdOrder;
import com.javahis.ui.testOpb.tools.OrderTool;
import com.javahis.ui.testOpb.tools.QueryTool;
import com.javahis.ui.testOpb.tools.TableTool;
import com.javahis.util.DateUtil;
import java.io.PrintStream;
import java.sql.Timestamp;
import java.util.List;
import jdo.reg.PatAdmTool;
import jdo.sys.Operator;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

public class BILPatOweControl extends TControl
{
  private TTabbedPane tabbedPane;
  private TTextField MR_NO;
  private String mrno = "";
  TTable table;
  TTable table2;
  TTable table3;
  TParm result;
  int row1 = -1;
  int row2 = -1;
  private String sql = "";
  private String caseNo = "";
  private TableTool tableTool;
  private String[] syncFieldsNames = new String[0];
  private String[] mutiplyFieldsNames = new String[0];

  public void onInit()
  {
    super.onInit();
    Object obj = getParameter();
    if ((obj instanceof String)) {
      this.mrno = ((String)obj);
      setValue("MR_NO", this.mrno);
    }
    onInitEvent();
  }

  public void onInitEvent()
  {
    Timestamp date = SystemTool.getInstance().getDate();
    setValue("END_DATE", date.toString().substring(0, 10).replace('-', 
      '/'));
    setValue("START_DATE", StringTool.rollDate(date, -90L).toString()
      .substring(0, 10).replace('-', '/'));
    this.tabbedPane = ((TTabbedPane)getComponent("TABBEDPANE"));
    initPage();
    this.tableTool = new TableTool(this.table3, this.syncFieldsNames, this.mutiplyFieldsNames);
  }

  public void initPage()
  {
    this.table3 = ((TTable)getComponent("TABLE3"));

    switch (this.tabbedPane.getSelectedIndex()) {
    case 0:
      this.mrno = getValueString("MR_NO");
      this.table = ((TTable)getComponent("TABLE1"));
      this.sql = getPatSql();

      String startDate = getValueString("START_DATE").substring(0, 10).replace("-", "/");
      String endDate = getValueString("END_DATE").substring(0, 10).replace("-", "/");
      this.sql = (this.sql + "AND B.ADM_DATE BETWEEN TO_DATE('" + startDate + "','yyyy/MM/dd') AND TO_DATE('" + endDate + "','yyyy/MM/dd')");
      System.out.println("sql++++++++" + this.sql);

      if ((!"".equals(this.mrno)) && (this.mrno != null)) {
        this.sql = (this.sql + " AND A.MR_NO='" + this.mrno + "'");
      }
      this.sql += " ORDER BY MR_NO";
      this.result = new TParm(TJDODBTool.getInstance().select(this.sql));

      if (this.result.getCount() > 0) {
        for (int i = 0; i < this.result.getCount(); i++) {
          this.result.setData("PAT_AGE", i, patAge(this.result
            .getTimestamp("BIRTH_DATE", i)));
        }
        this.table.removeRowAll();
        this.table.setParmValue(this.result);
      } else {
        messageBox("没有数据");
        this.table.removeRowAll();
      }

      this.sql = "";
      break;
    case 1:
      this.table2 = ((TTable)getComponent("TABLE2"));

      if (this.row1 < 0) {
        this.table2.setParmValue(new TParm());
        messageBox("请选择病患");
        this.tabbedPane.setSelectedIndex(0);
        return;
      }
      String regionCode = Operator.getRegion();
      TParm parm = PatAdmTool.getInstance().selDateByMrNo(this.mrno, 
        (Timestamp)getValue("START_DATE"), 
        (Timestamp)getValue("END_DATE"), regionCode);
      if (parm.getCount() < 0)
        return;
      this.sql = getAdmSql(this.mrno);
      this.result = new TParm(TJDODBTool.getInstance().select(this.sql));
      for (int k = 0; k < parm.getCount(); k++) {
        parm.setData("MR_NO", k, this.mrno);
        parm.setData("PAT_SEX", k, this.result.getValue("PAT_SEX", 0));
        parm.setData("PAT_NAME", k, this.result.getValue("PAT_NAME", 0));
        parm.setData("PAT_AGE", k, patAge(this.result.getTimestamp(
          "BIRTH_DATE", 0)));
        parm.setData("BIRTH_DATE", k, this.result.getTimestamp("BIRTH_DATE", 
          0));
      }
      opbArrearage(parm);
      TParm parmN = new TParm();
      TParm parmY = new TParm();
      System.out.println("222222222222--------------:" + parm);
      if (getTRadioButton("tRadioButton_2").isSelected()) {
        for (int m = 0; m < parm.getCount(); m++) {
          if (parm.getValue("OPB_ARREAGRAGE", m).equals("N")) {
            parmN.addRowData(parm, m);
          }
        }
        parm = parmN;
      }
      if (getTRadioButton("tRadioButton_1").isSelected()) {
        for (int m = 0; m < parm.getCount(); m++) {
          if (parm.getValue("OPB_ARREAGRAGE", m).equals("Y")) {
            parmY.addRowData(parm, m);
          }
        }
        parm = parmY;
      }

      if (parm.getCount() > 0) {
        getParm(parm);
        this.table2.setParmValue(new TParm());
        this.table2.setParmValue(parm);
      } else if (parmN.getCount() > 0) {
        getParm(parmN);
        this.table2.setParmValue(new TParm());
        this.table2.setParmValue(parmN);
      } else if (parmY.getCount() > 0) {
        getParm(parmY);
        this.table2.setParmValue(new TParm());
        this.table2.setParmValue(parmY);
      } else {
        this.table2.setParmValue(new TParm());
        messageBox("没有数据");
      }

      this.sql = "";
      break;
    case 2:
      if ("".equals(this.mrno)) {
        messageBox("请选择病患");
        this.tabbedPane.setSelectedIndex(0);
        return;
      }
      if ("".equals(this.caseNo)) {
        this.table3.removeRowAll();
        messageBox("请选择一条就诊记录");
        this.tabbedPane.setSelectedIndex(1);
        return;
      }
      try {
        this.table3.removeRowAll();
        String sql = "select * from opd_order where case_no = '" + 
          this.caseNo + "' order by rx_no, seq_no";
        OpdOrder opdOrder = new OpdOrder();
        List list = QueryTool.getInstance().queryBySql(sql, 
          opdOrder);
        list = OrderTool.getInstance().initOrder(list);

        this.tableTool.setList(list);
        this.tableTool.show();
      }
      catch (Exception localException)
      {
      }
    }
  }

  public void getParm(TParm parm)
  {
    double arAmt = 0.0D;
    for (int n = 0; n < parm.getCount(); n++) {
      arAmt += parm.getDouble("MONEY", n);
    }
    parm.setData("MR_NO", parm.getCount(), "欠费总额：");
    parm.setData("MONEY", parm.getCount(), Double.valueOf(arAmt));
  }

  public void onQuery()
  {
    initPage();
  }

  public void onMrNo()
  {
    String mrNo = getValueString("MR_NO");
    setValue("MR_NO", PatTool.getInstance().checkMrno(mrNo));
    onQuery();
  }

  public void onClear()
  {
    clearValue("MR_NO");
    this.tabbedPane.setSelectedIndex(0);
    this.table.removeRowAll();
    this.row1 = -1;
    onInitEvent();
    getTRadioButton("tRadioButton_1").setSelected(true);
    clearValue("MONEY");
  }

  public void changePaneEvent()
  {
    if (this.tabbedPane.getSelectedIndex() == 0) {
      this.mrno = "";
      clearValue("MR_NO");
      this.row1 = -1;
    }
    if (this.tabbedPane.getSelectedIndex() != 2) {
      this.caseNo = "";
    }

    initPage();
  }

  public void onTableClicked1()
  {
    this.row1 = this.table.getSelectedRow();
    setValue("MR_NO", this.table.getParmValue().getValue("MR_NO", this.row1));
    this.mrno = getValueString("MR_NO");
  }

  public void onTableClicked2()
  {
    this.row2 = this.table2.getSelectedRow();
    this.caseNo = this.table2.getParmValue().getValue("CASE_NO", this.row2);
    setValue("AMT", this.table2.getParmValue().getValue("MONEY", this.row2));
  }

  public TTextFormat getTextFormat(String tagName)
  {
    return (TTextFormat)getComponent(tagName);
  }

  private String patAge(Timestamp date)
  {
    Timestamp sysDate = SystemTool.getInstance().getDate();
    Timestamp temp = date == null ? sysDate : date;
    String age = "0";
    age = DateUtil.showAge(temp, sysDate);
    return age;
  }

  public String getPatSql()
  {
    String sql = "SELECT DISTINCT A.MR_NO,A.PAT_NAME,A.SEX_CODE PAT_SEX," +
    		" A.CTZ1_CODE,A.BIRTH_DATE,A.CELL_PHONE,A.ADDRESS " +
    		"  FROM SYS_PATINFO A,REG_PATADM B,OPD_ORDER C " +
    		" WHERE A.MR_NO = B.MR_NO AND B.CASE_NO = C.CASE_NO " +
    		" AND  C.EXEC_FLG = 'Y'  AND C.BILL_FLG = 'N' " +
    		" AND A.opb_Arreagrage='Y' AND C.AR_AMT>0 " +
    		" AND C.MEM_PACKAGE_ID IS NULL ";

    //System.out.println("sql--------------:" + sql);
    return sql;
  }

  public boolean getOpdOrderByCaseNo(String case_no)
  {
    String sql = "SELECT CASE_NO FROM OPD_ORDER WHERE CASE_NO='" + case_no + 
      "' AND BILL_FLG='N' AND EXEC_FLG='Y'";

    TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
    if (parm.getCount() > 0) {
      return true;
    }
    return false;
  }

  public String getAdmSql(String mrno)
  {
    String sql = "SELECT A.MR_NO,A.PAT_NAME,A.BIRTH_DATE,A.SEX_CODE PAT_SEX  FROM SYS_PATINFO A WHERE A.MR_NO='" + 
      mrno + "' ";
    return sql;
  }

  private void opbArrearage(TParm parm)
  {
    String startDate = getValue("START_DATE").toString().substring(0, 10)
      .replace("-", "");
    String endDate = getValue("END_DATE").toString().substring(0, 10)
      .replace("-", "");
    String newSql = " SELECT DISTINCT A.CASE_NO, SUM (AR_AMT) MONEY FROM OPD_ORDER A, REG_PATADM B WHERE     A.MR_NO = '" + 
      this.mrno + "'" + " AND A.BILL_FLG = 'N'" + 
      " AND A.EXEC_FLG = 'Y'" + " AND A.AR_AMT>0 AND A.MEM_PACKAGE_ID IS NULL" + 
      " AND A.CASE_NO = B.CASE_NO" + 
      " AND B.ADM_DATE BETWEEN TO_DATE ('" + startDate + 
      "', 'YYYYMMDDHH24MISS') " + " AND TO_DATE ('" + endDate + 
      "', 'YYYYMMDDHH24MISS') GROUP BY A.CASE_NO";
    System.out.println("1111-----------------:" + newSql);
    TParm p = new TParm(TJDODBTool.getInstance().select(newSql));
    parm.addData("SYSTEM", "COLUMNS", "OPB_ARREAGRAGE");
    for (int i = 0; i < parm.getCount(); i++) {
      parm.addData("OPB_ARREAGRAGE", "N");
      parm.setData("MONEY", i, Integer.valueOf(0));
      System.out.println(parm);
      for (int j = 0; j < p.getCount(); j++)
      {
        if (parm.getValue("CASE_NO", i)
          .equals(p.getValue("CASE_NO", j))) {
          parm.setData("OPB_ARREAGRAGE", i, "Y");
          parm.setData("MONEY", i, Double.valueOf(p.getDouble("MONEY", j)));
        }
      }
    }
  }

  public TRadioButton getTRadioButton(String tag)
  {
    return (TRadioButton)getComponent(tag);
  }
}