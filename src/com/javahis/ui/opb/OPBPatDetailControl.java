/**
 * ���ﲡ����ϸ����
 * 
 * @param mrno
 * String ������
 * @return TParm <PAT_NAME>,<IDNO>
 */
package com.javahis.ui.opb;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import jdo.bil.BILSysParmTool;
import jdo.sys.Operator;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;

public class OPBPatDetailControl extends TControl{
	private TTable table;
	/**
     * ��ʼ������
     */
    public void onInit() {
        initPage();
    }

    
	/**
     * ��ʼ��������
     */ 
    private void initPage() {
        //Timestamp date = StringTool.getTimestamp(new Date());
        // ��ʼ����ѯ����
        this.setValue("S_DATE",
                      getDateForInit(queryFirstDayOfLastMonth(StringTool.
                      getString(SystemTool.getInstance().getDate(), "yyyyMMdd"))));
                      Timestamp rollDay = StringTool.rollDate(getDateForInit(SystemTool.getInstance().getDate()), -1);
        this.setValue("E_DATE", rollDay);
        // ��ʼ��TABLE
        table = getTable("TABLE");
       
    }
    public void onQuery(){
    	String mrno = this.getValueString("MR_NO");
    	String sDate = this.getValueString("S_DATE");
    	String eDate = this.getValueString("E_DATE");
    	String CHARGE_DATE = "";
    	sDate = sDate.substring(0, 19).replace(" ", "").replace("-", "").
          replace(":", "");
    	CHARGE_DATE +=  " AND CHARGE_DATE > TO_DATE(" + sDate +
        ",'YYYYMMDDHH24MISS') ";
    	eDate = eDate.substring(0, 19).replace(" ", "").replace("-", "").
          replace(":", "");
    	CHARGE_DATE +=  " AND CHARGE_DATE < TO_DATE(" + eDate +
        ",'YYYYMMDDHH24MISS') ";
    	String selSql = " SELECT B.PAT_NAME,A.MR_NO,CHARGE_DATE,TOT_AMT  " +
    			        " FROM BIL_OPB_RECP A,SYS_PATINFO B " +
    			        " WHERE A.MR_NO = B.MR_NO           " +
    			          CHARGE_DATE+
    			        " AND RESET_RECEIPT_NO IS NULL                   " +
    			        " AND TOT_AMT >0 " ;
    	System.out.println("selSql==="+selSql); 
    	TParm result = new TParm(TJDODBTool.getInstance().select(selSql));
    	//System.out.println("result==="+result);
    	if(result.getErrCode() < 0 ){
    		this.messageBox(result.getErrText());
    		return;
    	}
        if(result.getCount() <= 0)
        {
        	this.messageBox("��������");
        }
        this.callFunction("UI|TABLE|setParmValue",result);
    }
    
    
    
    
    
    /**
     * ��ѯ
     */ 
      public void onQuery2(){                   
    	  String mrno = this.getValueString("MR_NO");            
    	  String sDate = this.getValueString("S_DATE");
    	  String eDate = this.getValueString("E_DATE");
    	  String sql = "SELECT B.PAT_NAME,A.MR_NO,CHARGE_DATE,TOT_AMT          "+
    	               "FROM BIL_OPB_RECP A,SYS_PATINFO B                      "+
    	               "WHERE  A.MR_NO =B.MR_NO                                "+
//    	               "AND CHARGE_DATE   BETWEEN TO_DATE ('20120326 00:00:00', "+
//    	               "             'yyyymmdd hh24:mi:ss ') "+
//    	               "                      AND TO_DATE ('20120425 23:59:59', "+
//    	               "             'yyyymmdd hh24:mi:ss ') "+
    	               "AND RESET_RECEIPT_NO IS NULL  "+
    	               "AND TOT_AMT >0                ";
    	  StringBuilder sbuilder = new StringBuilder(sql);
    	  System.out.println("sql=="+sql);
    	  if(mrno!=null && !"".equals(mrno)){//������
      		sbuilder.append(" AND A.MR_NO= '"+mrno+"' ") ;
          	}  
    	  if((sDate!=null && !"".equals(sDate)))
    	  { sDate = sDate.substring(0, 19) ;
    	    sbuilder.append(" AND TO_CHAR(CHARGE_DATE,'YYYY-MM-DD HH:mm:ss') >= '"+sDate+"'");
    	  }
    	  if((eDate!=null && !"".equals(eDate)))
    	  { eDate = eDate.substring(0, 19) ;
    	    sbuilder.append(" AND TO_CHAR(CHARGE_DATE,'YYYY-MM-DD HH:mm:ss') <= '"+eDate+"'");
    	  }
    	    sbuilder.append(" ORDER BY CHARGE_DATE ") ;
    	  TParm result = new TParm(TJDODBTool.getInstance().select(sbuilder.toString()));
    	//   ��ô�����Ϣ��Ϣ
      	  if (result.getErrCode() < 0) {
      	      messageBox(result.getErrText());
      	      return;      
      	   }
      	   if (result.getCount() <= 0) {
               messageBox("��������");
               this.callFunction("UI|TABLE|setParmValue", new TParm());
               return;
           }       	
    	  this.callFunction("UI|TABLE|setParmValue", result); 
    	  //TTable table =this.getTTable("TABLE");
    	  //table = (TTable) getComponent("TABLE");
    	  //table.setParmValue(result);
  }

     /**
      * ��ӡ
      */
 	public void onPrint () {
 		//ϵͳ����
    	Timestamp P_DATE = SystemTool.getInstance().getDate();
    	//ϵͳ���ڼ������ʱ��
 		String P_DATE_FORMAT = StringTool.getString(P_DATE,"yyyy/MM/dd HH:mm:ss") ;
        if (this.table.getRowCount() <= 0) {
            this.messageBox("û��Ҫ��ӡ������");
            return;
        }
        double allMoney = 0.00;
        //parm ȡtableֵ
        TParm parm = table.getParmValue();
        TParm printData = new TParm();
        for(int i=0 ; i<parm.getCount("MR_NO");i++)
        {  
        //PAT_NAME;MR_NO;CHARGE_DATE;TOT_AMT
        //printData.addData("APPLY_AMT",parm.getValue("APPLY_AMT",i));
        	printData.addData("PAT_NAME", parm.getValue("PAT_NAME", i));
            printData.addData("MR_NO", parm.getValue("MR_NO", i));
        	printData.addData("CHARGE_DATE",StringTool.getString(parm.getTimestamp("CHARGE_DATE", i), "yyyy/MM/dd"));
        	printData.addData("TOT_AMT", parm.getValue("TOT_AMT", i));
        	allMoney += parm.getDouble("TOT_AMT",i);
        	//������λС��
        	allMoney = StringTool.round(allMoney, 2);
        } 
        //����һ��
        printData.addData("PAT_NAME", "�ܼ�");
    	printData.addData("MR_NO", "");
    	printData.addData("CHARGE_DATE","");
    	printData.addData("TOT_AMT",allMoney);
    	//������������i�м�1��
    	printData.setCount(parm.getCount()+1);
    	//�и�ֵ
    	printData.addData("SYSTEM", "COLUMNS","PAT_NAME");
    	printData.addData("SYSTEM", "COLUMNS","MR_NO");
    	printData.addData("SYSTEM", "COLUMNS","CHARGE_DATE");
    	printData.addData("SYSTEM", "COLUMNS","TOT_AMT");
    	TParm result = new TParm();
    	result.setData("T1", printData.getData());
    	result.setData("TITLE","TEXT","���ﲡ����ϸ����");
        result.setData("P_DATE","TEXT",P_DATE_FORMAT);
    	result.setData("USER", "TEXT", "�Ʊ��ˣ�" + Operator.getName());
    	//System.out.println("result==="+result);
        this.openPrintWindow("%ROOT%\\config\\prt\\opb\\OPBPatDetail.jhw",result);
  	}  
      /**
       * ��շ���   
       */
      public void onClear() {
          String clearString ="S_DATE;E_DATE;MR_NO" ;
          this.clearValue(clearString);
          Timestamp date = StringTool.getTimestamp(new Date());
          // ��ʼ����ѯ���� 
          this.setValue("S_DATE",
                  getDateForInit(queryFirstDayOfLastMonth(StringTool.
                  getString(SystemTool.getInstance().getDate(), "yyyyMMdd"))));
                  Timestamp rollDay = StringTool.rollDate(getDateForInit(SystemTool.getInstance().getDate()), -1);
          this.setValue("E_DATE", rollDay);
          table.removeRowAll(); 
          }
      /**
       * ��񵥻��¼�
       */ 
       public void onTableClicked() {
    	 int row = table.getSelectedRow();
         if (row != -1) {
             this.setValue("MR_NO", table.getItemData(row, "MR_NO"));
             }
         }  
    /**
     * ���Excel
     */
      public void onExport() {

          //�õ�UI��Ӧ�ؼ�����ķ�����UI|XXTag|getThis��
          TTable table = (TTable) callFunction("UI|Table|getThis");
          ExportExcelUtil.getInstance().exportExcel(table, "���ﲡ����ϸ����");
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
  	 * �õ�TABLE
  	 * 
  	 * @param tag
  	 *            String
  	 * @return TTable
  	 */
  	public TTable getTTable(String tag) {
      return (TTable) this.getComponent(tag);
  	
  	
  	}
	/**
  	 * �����Ų���
  	 */
  	public void onMrNoAction(){
        String mr_no = PatTool.getInstance().checkMrno(this.getValueString("MR_NO"));
        this.setValue("MR_NO", mr_no) ;
	}
//	/**
//	 * ��ⲡ���ų���
//	 * 
//	 * @param mrno
//	 *            String
//	 * @return String
//	 */
//	public String checkMrno(String mrno) {
//		int mrnoLength = getMrNoLength();
//		if (mrnoLength <= 0)
//			mrnoLength = mrno.length();
//		return StringTool.fill("0", mrnoLength - mrno.length()) + mrno;
//	}
//
//	/**
//	 * �õ������ŵĳ���
//	 * 
//	 * @return int
//	 */
//	public int getMrNoLength() {
//		return getResultInt(query("getMrNoLength"), "MRNO_LENGTH");
//	}
    /**
     * �õ��ϸ���
     * @param dateStr String
     * @return Timestamp
     */
    public Timestamp queryFirstDayOfLastMonth(String dateStr) {
        DateFormat defaultFormatter = new SimpleDateFormat("yyyyMMdd");
        Date d = null;
        try {
            d = defaultFormatter.parse(dateStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(d);
        cal.add(Calendar.MONTH, -1);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return StringTool.getTimestamp(cal.getTime());
    }
	
	
    /**
     * ��ʼ��ʱ������
     * @param date Timestamp
     * @return Timestamp
     */
    public Timestamp getDateForInit(Timestamp date) {
        String dateStr = StringTool.getString(date, "yyyyMMdd");
        TParm sysParm = BILSysParmTool.getInstance().getDayCycle("I");
        int monthM = sysParm.getInt("MONTH_CYCLE", 0) + 1;
        String monThCycle = "" + monthM;
        dateStr = dateStr.substring(0, 6) + monThCycle;
        Timestamp result = StringTool.getTimestamp(dateStr, "yyyyMMdd");
        return result;
    }
  }

      
      

