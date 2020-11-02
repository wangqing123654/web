package jdo.inv;

import java.sql.Timestamp;

import javax.print.DocFlavor.STRING;

import jdo.ind.IndDDStockTool;
import jdo.ind.IndOrgTool;
import jdo.ind.IndStockDTool;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>Title:���ʲ���
 *
 * <p>Description: 
 *
 * <p>Copyright: 
 *
 * <p>Company: JavaHis</p>
 *
 * @author  chenx 
 * @version 4.0
 */
public class INVsettlementTool extends TJDOTool{
	
	/**
	 * ����ʵ��
	 */
  public static INVsettlementTool instanceObject ;
    /**
     * �õ�ʵ��
     * @return
     */
  public static INVsettlementTool getInstance(){
	  if(instanceObject==null)
		  instanceObject = new INVsettlementTool() ;
	  return instanceObject ;
  }
  
  /**
   * ������
   */
  public INVsettlementTool(){
	  setModuleName("inv\\INVsettlementModule.x") ;
	  onInit() ;
  }
  
  /**
   * ��ѯ��Ӧ�̶�Ӧ��ҩƷ�Ƿ�Ϊ��
   */
  public TParm getAgentInvCode(TParm parm){
	  String sql = " SELECT SUP_CODE, INV_CODE " +
	  		       "  FROM  INV_AGENT    " +
	  		       " WHERE SUP_CODE = '"+parm.getValue("SUP_CODE")+"'" +
	  		       " AND   INV_CODE = '"+parm.getValue("INV_CODE")+"'"  ;
	  TParm result = new TParm(TJDODBTool.getInstance().select(sql)) ;
      if (result.getErrCode() < 0) {
          err(result.getErrCode() + " " + result.getErrText());
          return result;
      }
      return result;
  }
  /**
   * �Զ��������쵥��ȡ�½�����
   * inv_account
   */
  public TParm getRequestAccount(TParm parm){
	  String sql = " SELECT INV_CODE,TOTAL_OUT_QTY QTY,VERIFYIN_PRICE COST_PRICE  FROM INV_ACCOUNT WHERE " +
	  		      "  CLOSE_DATE = '"+parm.getValue("CLOSE_DATE")+"'" +
	  		      "  AND ORG_CODE = '"+parm.getValue("ORG_CODE")+"'"  ;
	  System.out.println("sql=�Զ��������쵥��ȡ�½�����====="+sql);
	  TParm result = new TParm(TJDODBTool.getInstance().select(sql)) ;
      if (result.getErrCode() < 0) {
          err(result.getErrCode() + " " + result.getErrText());
          return result;
      }
      return result;
  }
  /**
   * �Զ��������쵥��ȡ�½����ݵĿ���
   * inv_account
   */
  public TParm getRequestInvOrg(TParm parm){
	  String sql = " SELECT DISTINCT ORG_CODE,CLOSE_DATE FROM INV_ACCOUNT WHERE " +
	  		      "  CLOSE_DATE = '"+parm.getValue("CLOSE_DATE")+"'"  ;
	  System.out.println("sql==�Զ��������쵥��ȡ�½����ݵĿ���===="+sql);
	  TParm result = new TParm(TJDODBTool.getInstance().select(sql)) ;
      if (result.getErrCode() < 0) {
          err(result.getErrCode() + " " + result.getErrText());
          return result;
      }
      return result;
  }
  /**
   * ��ȡ�սύ�׵�
   */
  public TParm getDayStock(TParm parm){
	  TParm result = query("getDayStock",parm);
      if (result.getErrCode() < 0) {
          err(result.getErrCode() + " " + result.getErrText());
          return result;
      }
      return result;
  }
  /**
   * ���ʲ��ŷ���ͳ�Ʊ���
   */
  public TParm queryDeptFee(TParm parm){
	  TParm result = query("queryDeptFee",parm);
      if (result.getErrCode() < 0) {
          err(result.getErrCode() + " " + result.getErrText());
          return result;
      }
      return result;
  }
  /**
   * ���ʳ��ⵥ����
   */
  public TParm queryOutDispense(TParm parm){
	  TParm result = query("queryOutDispense",parm);
      if (result.getErrCode() < 0) {
          err(result.getErrCode() + " " + result.getErrText());
          return result;
      }
      return result;
  }
  /**
   * ������ⵥ����
   */
  public TParm queryInDispense(TParm parm){
	  TParm result = query("queryInDispense",parm);
      if (result.getErrCode() < 0) {
          err(result.getErrCode() + " " + result.getErrText());
          return result;
      }
      return result;
  }
  /**
   * ��ȡ�Ĳ�sql
   */
  public String  getSpcInvRecordSql(String startDate,String endDate,String supCode){
	  String  sql = "SELECT C.INV_CODE ,A.INV_CHN_DESC ,A.DESCRIPTION,C.QTY," +
                   " C.OWN_PRICE ,B.UNIT_CHN_DESC,C.QTY*C.OWN_PRICE TOT_AMT,D.ORG_DESC, " +
                   " C.DEPT_CODE ORG_CODE,B.UNIT_CODE " +
                   " FROM  INV_BASE A ,SYS_UNIT B ,INV_ORG D ," +
                   " (SELECT INV_CODE ,SUM(QTY) QTY,OWN_PRICE,DEPT_CODE FROM  SPC_INV_RECORD  " +
                   " WHERE BILL_FLG='Y'  " +
                   " AND BILL_DATE BETWEEN TO_DATE ( '"+startDate+"' , 'YYYYMMDDHH24MISS')" +
                   " AND  TO_DATE ( '"+endDate+"' , 'YYYYMMDDHH24MISS')" +
                   " GROUP BY INV_CODE,OWN_PRICE,DEPT_CODE) C" +
                   " WHERE  C.INV_CODE = A.INV_CODE " +
                   " AND    C.DEPT_CODE = D.ORG_CODE" +
                   " AND    A.STOCK_UNIT = B.UNIT_CODE" +
                   " AND    A.SUP_CODE = '"+supCode+"'"  ;
	  return   sql  ;
  }
  /**
   * ��ȡ�������sql
   */
  public String  getInvDispenseSql(String startDate,String endDate,String supCode){
	  String  sql = "SELECT C.INV_CODE ,A.INV_CHN_DESC ,A.DESCRIPTION,C.QTY," +
        " C.COST_PRICE OWN_PRICE ,B.UNIT_CHN_DESC,C.QTY*C.COST_PRICE TOT_AMT,D.ORG_DESC, " +
        " C.FROM_ORG_CODE ORG_CODE ,B.UNIT_CODE " +
        " FROM  INV_BASE A ,SYS_UNIT B ,INV_ORG D, " +
       " (SELECT DE.INV_CODE ,SUM(DE.QTY) QTY,DE.COST_PRICE,MA.FROM_ORG_CODE " +
       " FROM  INV_DISPENSED DE , INV_DISPENSEM MA " +
       " WHERE DE.DISPENSE_NO=MA.DISPENSE_NO " +
       "  AND    MA.FINA_FLG  IN ('1','3') " +
       "  AND MA.DISPENSE_DATE BETWEEN TO_DATE ( '"+startDate+"' , 'YYYYMMDDHH24MISS') " +
       "  AND TO_DATE ( '"+endDate+"' , 'YYYYMMDDHH24MISS') " +
       " GROUP BY DE.INV_CODE,DE.COST_PRICE,MA.FROM_ORG_CODE) C" +
       " WHERE  C.INV_CODE = A.INV_CODE " +
       " AND    C.FROM_ORG_CODE = D.ORG_CODE" +
       " AND    A.STOCK_UNIT = B.UNIT_CODE" +
       " AND    A.SUP_CODE = '"+supCode+"'"  ;
  
	  return   sql  ;
  }
  /**
   * ��ȡ�˿����sql
   */
  public String  getInvRtnSql(){
	  String sql  =""  ;
	  return   sql  ;
  }
  /**
   * ��ȡ���ܹ�sql
   */
  public String  getInvCabinetSql(){
	  String sql  =""  ;
	  return   sql  ;
  }
  /**
   * ��ȡ��������
   */
  public TParm getAccountData(TParm parm){
	  //��ʼʱ��
	  String startDate = parm.getValue("START_DATE") ;
	 //����ʱ��
	String endDate = parm.getValue("END_DATE");
	//��Ӧ�̴���
	String supCode = parm.getValue("SUP_CODE");
	//��ȡ�Ĳ�sql
	String getSpcInvRecordSql = this.getSpcInvRecordSql(startDate,endDate, supCode) ;
	//��ȡ�������sql
	String getInvDispenseSql = this.getInvDispenseSql(startDate,endDate, supCode) ;
	String sql = getSpcInvRecordSql + " UNION ALL "  +getInvDispenseSql +" ORDER BY ORG_DESC" ;
	System.out.println("SQL ==========���ʽ���sql========"+sql);
	  TParm result = new TParm(TJDODBTool.getInstance().select(sql)) ;
      if (result.getErrCode() < 0) {
          err(result.getErrCode() + " " + result.getErrText());
          return result;
      }
      return result;
  }
  /**
   * ��ѯ�������в���
   */
  public TParm getInvOrg(){
	  String sql = "SELECT ORG_CODE FROM INV_ORG  "  ;
	  TParm result = new TParm(TJDODBTool.getInstance().select(sql)) ;
      if (result.getErrCode() < 0) {
          err(result.getErrCode() + " " + result.getErrText());
          return result;
      }
      return result;
  }
	/**
	 * �Ƿ�����
	 * @param parm
	 * @return
	 */
	public TParm checkInvAccount(TParm parm){
		String sql = " SELECT  ACCOUNT_NO FROM INV_DISPENSEM  WHERE  " +
				"   DISPENSE_DATE BETWEEN TO_DATE('"+parm.getValue("START_DATE")+"','YYYYMMDDHH24MISS') " +
				"   AND TO_DATE('"+parm.getValue("END_DATE")+"','YYYYMMDDHH24MISS') " +
		        "   AND FINA_FLG IN ('1','3') " +
		        "   AND ACCOUNT_NO IS NOT NULL " +
		        "  UNION ALL " +
		        "  SELECT ACCOUNT_NO  FROM SPC_INV_RECORD WHERE" +
		        "  BILL_DATE BETWEEN TO_DATE('"+parm.getValue("START_DATE")+"','YYYYMMDDHH24MISS') " +
				"  AND TO_DATE('"+parm.getValue("END_DATE")+"','YYYYMMDDHH24MISS') " +
		        "  AND BILL_FLG = 'Y' " +
		        "  AND ACCOUNT_NO IS NOT NULL " ;
		System.out.println("sql============="+sql);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		 if (result.getErrCode() < 0) {
	          err(result.getErrCode() + " " + result.getErrText());
	          return result;
	      }
		return result;
	}
	/**
	 * ��ѯinv_account ���ݣ���������ͬ����his
	 * @param parm
	 * @return
	 */
	public TParm onQueryInvAccount(TParm parm){
		String sql = "SELECT * FROM INV_ACCOUNT  WHERE  CLOSE_DATE='"+parm.getValue("CLOSE_DATE")+"' " ;
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		 if (result.getErrCode() < 0) {
	          err(result.getErrCode() + " " + result.getErrText());
	          return result;
	      }
		return result;
	}
	/**
	 * ��ѯȡ���ս������
	 * @param parm
	 * @return
	 */
	public TParm onCancleInvAccount(String date){   
		String sql = "SELECT  DISTINCT ACCOUNT_NO ,CLOSE_DATE,'N' AS FLG FROM INV_ACCOUNT  " +
				" WHERE  CLOSE_DATE='"+date+"'  " ;
		System.out.println("SQL=��ѯȡ���ս������=="+sql);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		 if (result.getErrCode() < 0) {
	          err(result.getErrCode() + " " + result.getErrText());
	          return result;
	      }
		return result;
	}
	/**
	 * ��inv_account��ʷ����
	 * @param parm
	 * @return
	 */
	public TParm getAccountData(String accountNo){
		String sql ="SELECT C.INV_CODE ,A.INV_CHN_DESC INV_DESC,A.DESCRIPTION,C.TOTAL_OUT_QTY QTY," +
        " C.VERIFYIN_PRICE OWN_PRICE ,B.UNIT_CHN_DESC,C.VERIFYIN_AMT TOT_AMT,D.ORG_DESC " +
        " FROM  INV_BASE A ,SYS_UNIT B , INV_ACCOUNT C ,INV_ORG D " +
        " WHERE  C.INV_CODE = A.INV_CODE " +
        " AND    C.ORG_CODE = D.ORG_CODE" +
        " AND    A.STOCK_UNIT = B.UNIT_CODE" +
        " AND    C.ACCOUNT_NO = '"+accountNo+"'"  ;
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		 if (result.getErrCode() < 0) {
	          err(result.getErrCode() + " " + result.getErrText());
	          return result;
	      }
		return result;
	}
  /**
   * ����invaccount ����
   * @param parm
   * @return
   */
  public TParm insertAccountData(TParm parm,TConnection conn){
	  TParm result = new TParm() ;
	  for(int i=0;i<parm.getCount();i++){
		  result = update("insertAccountData", parm.getRow(i),conn);
	      if (result.getErrCode() < 0) {
	          err("ERR:" + result.getErrCode() + result.getErrText() +
	              result.getErrName());
	          return result;
	      }  
	  }
	
	  return result ;
	  
  }
  /**
   * ������ˮ�ţ�ɾ��inv_account
   * @param parm
   * @return
   */
  public TParm deleteDataByAccountNo(TParm parm,TConnection conn){
	  TParm result = new TParm() ;
		  result = update("deleteDataByAccountNo", parm,conn);
	      if (result.getErrCode() < 0) {
	          err("ERR:" + result.getErrCode() + result.getErrText() +
	              result.getErrName());
	          return result;
	      }  
	  return result ;
	  
  }
  /**
   * ������ˮ�ţ���պ��ü�¼��ˮ��
   * @param parm
   * @return
   */
  public TParm updateSpcInvRecordByAccountNo(TParm parm,TConnection conn){
	  TParm result = new TParm() ;
		for(int i=0;i<parm.getCount();i++){
			  result = update("updateSpcInvRecordByAccountNo", parm.getRow(i),conn);
		      if (result.getErrCode() < 0) {
		          err("ERR:" + result.getErrCode() + result.getErrText() +
		              result.getErrName());
		          return result;
		      }  	
		}
		
	  return result ;
	  
  }
  /**
   * ������ˮ�ţ���ճ�����ˮ��
   * @param parm
   * @return
   */
  public TParm updateInvDisByAccountNo(TParm parm,TConnection conn){
	  TParm result = new TParm() ;
		for(int i=0;i<parm.getCount();i++){
			 result = update("updateInvDisByAccountNo", parm.getRow(i),conn);
		      if (result.getErrCode() < 0) {
		          err("ERR:" + result.getErrCode() + result.getErrText() +
		              result.getErrName());
		          return result;
		      }  	
		}
		 
	  return result ;
	  
  }
  /**
   * ���½��ױ��account_no ,˵�����ױ����������ѽ���    
   * ���ü�¼
   * @param parm
   * @return
   */
  public TParm updateSpcInvRecordAccountNo(TParm parm,TConnection conn){
	  TParm result = new TParm() ;
	
		  result = update("updateSpcInvRecordAccountNo", parm,conn);
	      if (result.getErrCode() < 0) {
	          err("ERR:" + result.getErrCode() + result.getErrText() +
	              result.getErrName());
	          return result;

	  }
	
	  return result ;
	  
  }
  /**
   * ���½��ױ��account_no ,˵�����ױ����������ѽ���  
   * �����¼  
   * @param parm
   * @return
   */
  public TParm updateInvDisAccountNo(TParm parm,TConnection conn){
	  TParm result = new TParm() ;
		  result = update("updateInvDisAccountNo", parm,conn);
	      if (result.getErrCode() < 0) {
	          err("ERR:" + result.getErrCode() + result.getErrText() +
	              result.getErrName());
	          return result;

	  }
	
	  return result ;
	  
  }
  /**
   * ����insertDDStock ����
   * @param parm
   * @return
   */
  public TParm insertDDStock(TParm parm,TConnection conn){
	  TParm result = new TParm() ;
	  result = update("insertDDStock", parm,conn);
      if (result.getErrCode() < 0) {
          err("ERR:" + result.getErrCode() + result.getErrText() +
              result.getErrName());
          return result;
      }
	  return result ;
	  
  }
  /**
   *  ���½����ʿ�����������ݹ�0
   * @param parm
   * @return
   */
  
  public TParm updateInvStockDToZero(TParm parm ,TConnection conn){
	  TParm result = new TParm() ;
	  result = update("updateInvStockDToZero", parm,conn);
      if (result.getErrCode() < 0) {
          err("ERR:" + result.getErrCode() + result.getErrText() +
              result.getErrName());
          return result;
      }
      return result;
  }

  /**
   * ɾ�� incaccount ����
   * @param parm
   * @return
   */
  public TParm deleteAccountData(TParm parm,TConnection conn){
	  TParm result = new TParm() ;
		for(int i=0;i<parm.getCount();i++){
			  result = update("deleteAccountData", parm.getRow(i),conn);
		      if (result.getErrCode() < 0) {
		          err("ERR:" + result.getErrCode() + result.getErrText() +
		              result.getErrName());
		          return result;
		      }
		}
	
	  return result ;
  }
  /**
   * ���� ����  ����
   * @param parm
   * @return
   */
  public TParm insertRequestM(TParm parm,TConnection conn){
	       TParm result = new TParm() ;
	    	   result = update("insertRequestM", parm,conn);
			   if (result.getErrCode() < 0) {
			    err("ERR:" + result.getErrCode() + result.getErrText() +
			           result.getErrName());
			      return result;
	       }
		
	
	  return result ;
  }
  /**
   * ���� ����  ϸ��
   * @param parm
   * @return
   */
  public TParm insertRequestD(TParm parm,TConnection conn){
	  TParm result = new TParm() ;
	  for(int i=0;i<parm.getCount();i++){
		  result = update("insertRequestD", parm.getRow(i),conn);
	      if (result.getErrCode() < 0) {
	          err("ERR:" + result.getErrCode() + result.getErrText() +
	              result.getErrName());
	          return result;

	}  
	  }
			
	
	  return result ;
  }
  /**
   * ���� �����  ϸ��
   * @param parm
   * @return
   */
  public TParm insertDispenseD(TParm parm,TConnection conn){
	  TParm result = new TParm() ;
	  for(int i=0;i<parm.getCount();i++){
		  result = update("insertDispenseD", parm.getRow(i),conn);
	      if (result.getErrCode() < 0) {
	          err("ERR:" + result.getErrCode() + result.getErrText() +
	              result.getErrName());
	          return result;

	}  
	  }
			
	
	  return result ;
  }
  /**
   *���� �����  ����
   * @param parm
   * @return
   */
  public TParm insertDispenseM(TParm parm,TConnection conn){
	  TParm result = new TParm() ;
	
		  result = update("insertDispenseM", parm,conn);
	      if (result.getErrCode() < 0) {
	          err("ERR:" + result.getErrCode() + result.getErrText() +
	              result.getErrName());
	          return result;

	  
	  }
			
	
	  return result ;
  }
  /**
   * ɾ��  his   incaccount ����
   * @param parm
   * @return
   */
  public TParm deleteAccountDataHis(TParm parm,TConnection conn){
	  TParm result = new TParm() ;
			  result = update("deleteAccountDataHis", parm,conn);
		      if (result.getErrCode() < 0) {
		          err("ERR:" + result.getErrCode() + result.getErrText() +
		              result.getErrName());
		          return result;

		}
	
	  return result ;
  }
  /**
   * ���� �ս�������ҵ
   * @param parm TParm
   * @param conn TConnection
   * @return TParm
   */
  public TParm onInvStockBatchByOrgCode(TParm parm, TConnection conn) {
      // �����
      TParm result1 = new TParm();
      result1.setErrCode(-1) ;
      // ���ݼ��
      if (parm == null)
          return result1;
      TParm resutlParm = new TParm();
      //��ѯ�տ�潻�׵�
      TParm result = this.getDayStock(parm) ;
      if (result.getErrCode() < 0) {
          return result1;
      }
      //�ս�������ҵ
      for (int i = 0; i < result.getCount("INV_CODE"); i++) {
          // ��ǰ���
          double stock_qty = result.getDouble("STOCK_QTY", i);
          // ���տ��
          double last_stock_qty = result.getDouble("LASTDAY_TOLSTOCK_QTY", i);
          // ������
          double in_qty = result.getDouble("DAYIN_QTY", i);
          // ���ճ�
          double out_qty = result.getDouble("DAYOUT_QTY", i);
          // �����̲�
          double modi_qty = result.getDouble("DAY_CHECKMODI_QTY", i);
          if (stock_qty == 0 && last_stock_qty == 0 && in_qty == 0 &&
              out_qty == 0 && modi_qty == 0) {
              continue;
          }
          // ������ʹ���� = ���ճ��� - �̲����
          double UseQty = out_qty - modi_qty;
          // �����տ���� = ���տ�� + ������� - ���ճ��� + �̲����
          double TotQty = stock_qty;//last_stock_qty + in_qty - out_qty + modi_qty;;;;update by liyh stock_qty��ʵʱֵ�����ü���
          if(TotQty != (last_stock_qty + in_qty - out_qty + modi_qty)){//������ д��־  add liyh by liudy
          	System.out.println("-------STOCK_QTY != (last_stock_qty + in_qty - out_qty + modi_qty):::"+"stock:"+TotQty+",last_stock_qty:"+last_stock_qty+
          			",in_qty:"+in_qty+",out_qty:"+out_qty+",modi_qty:"+modi_qty);
          }
          // ������֧�����(�ɱ���)
          double UseAmt = UseQty * result.getDouble("COST_PRICE", i); 
          // �����տ����
          double TotAmt = TotQty * result.getDouble("COST_PRICE", i);
          // �������տ����
          double TotLastStockAmt = last_stock_qty *
              result.getDouble("COST_PRICE", i);

          // д�����ʿ���ս���ҵ
          TParm inparm = new TParm();
          inparm.setData("TRANDATE",
                         parm.getValue("TRANDATE").substring(0, 10).
                         replaceAll("-", ""));

          inparm.setData("ORG_CODE", result.getData("ORG_CODE",i));     
          inparm.setData("INV_CODE", result.getData("INV_CODE", i));
          inparm.setData("BATCH_SEQ", result.getData("BATCH_SEQ", i));
          inparm.setData("REGION_CODE", result.getData("REGION_CODE", i));
          inparm.setData("BATCH_NO", result.getData("BATCH_NO", i));
          inparm.setData("VALID_DATE", result.getData("VALID_DATE", i));
          inparm.setData("DD_IN_QTY", result.getDouble("DAYIN_QTY", i));
          inparm.setData("DD_OUT_QTY", result.getDouble("DAYOUT_QTY", i));
          inparm.setData("DD_CHECKMODI_QTY", result.getDouble("DAY_CHECKMODI_QTY", i));
          inparm.setData("DD_STOCK_QTY", TotQty);
          inparm.setData("DD_STOCK_AMT", TotAmt);
          inparm.setData("LAST_DD_STOCK_QTY", last_stock_qty);
          inparm.setData("LAST_DD_STOCK_AMT", TotLastStockAmt);  
          inparm.setData("DD_VERIFYIN_QTY", result.getDouble("DAY_VERIFYIN_QTY", i));
          inparm.setData("DD_VERIFYIN_AMT", result.getDouble("DAY_VERIFYIN_AMT", i));
          inparm.setData("DD_GIFTIN_QTY", result.getDouble("GIFTIN_QTY", i));
          inparm.setData("DD_REGRESSGOODS_QTY",
                         result.getDouble("DAY_REGRESSGOODS_QTY", i));
          inparm.setData("DD_REGRESSGOODS_AMT",
                         result.getDouble("DAY_REGRESSGOODS_AMT", i));
          inparm.setData("DD_ASSIGNIN_QTY",
                         result.getDouble("DAY_REQUESTIN_QTY", i));
          inparm.setData("DD_ASSIGNOUT_QTY",
                         result.getDouble("DAY_REQUESTOUT_QTY", i));
          inparm.setData("DD_CHANGEIN_QTY",result.getDouble("DAY_CHANGEIN_QTY", i));
          inparm.setData("DD_CHANGEOUT_QTY",result.getDouble("DAY_CHANGEOUT_QTY", i));
          inparm.setData("DD_TRANSMITIN_QTY", result.getDouble("DAY_TRANSMITIN_QTY", i));
          inparm.setData("DD_TRANSMITOUT_QTY", result.getDouble("DAY_TRANSMITOUT_QTY", i));
          inparm.setData("DD_WASTE_QTY", result.getDouble("DAY_WASTE_QTY", i));
          inparm.setData("DD_DISPENSE_QTY", result.getDouble("DAY_DISPENSE_QTY", i));
          inparm.setData("DD_REGRESS_QTY", result.getDouble("DAY_REGRESS_QTY", i));
          inparm.setData("DD_STOCK_PRICE", result.getDouble("UNIT_PRICE", i));
          inparm.setData("DD_NHI_PRICE", result.getDouble("UNIT_PRICE", i));
          inparm.setData("DD_OWN_PRICE", result.getDouble("UNIT_PRICE", i));
          inparm.setData("DD_RPP_AMT", result.getDouble("UNIT_PRICE", i));
          inparm.setData("OPT_USER", parm.getData("OPT_USER"));
          inparm.setData("OPT_TERM", parm.getData("OPT_TERM"));
          resutlParm = this.insertDDStock(inparm, conn) ;
          if (resutlParm.getErrCode() < 0) {
              return result1;
          }
          // ��������¼����
          TParm srockParm = new TParm();
          srockParm.setData("ORG_CODE", result.getData("ORG_CODE",i));
          srockParm.setData("INV_CODE", result.getData("INV_CODE", i));
          srockParm.setData("BATCH_SEQ", result.getData("BATCH_SEQ", i));
          srockParm.setData("LASTDAY_TOLSTOCK_QTY", TotQty);
          srockParm.setData("OPT_USER", parm.getData("OPT_USER"));
          srockParm.setData("OPT_TERM", parm.getData("OPT_TERM"));

          resutlParm = this.updateInvStockDToZero(srockParm, conn) ;
          if (resutlParm.getErrCode() < 0) {
              return result1;
          }
      }
      return resutlParm;
  } 
  /**
   * ͨ�����ʱ��룬��ȡ���ʵ�һЩ������Ϣ
   */
  public TParm getInvBase(String invCode){
	  String sql = " SELECT INV_CHN_DESC ,DESCRIPTION,MAN_CODE,STOCK_UNIT " +
	  		       " FROM  INV_BASE WHERE" +
	  		       "  ACTIVE_FLG = 'Y'       " +
	  		       "  AND INV_CODE = '"+invCode+"' " ;
	  TParm result = new TParm(TJDODBTool.getInstance().select(sql)) ;
      if (result.getErrCode() < 0) {
          err(result.getErrCode() + " " + result.getErrText());
          return result;
      }
      return result;
  }
  /**
   * �鿴�Ƿ��ս�
   */
  public TParm selectTranDate(String tranDate){
	  String sql = "SELECT TRANDATE  FROM INV_DDSTOCK WHERE TRANDATE = '"+tranDate+"'" ;
	 
	  TParm result = new TParm(TJDODBTool.getInstance().select(sql)) ;
      if (result.getErrCode() < 0) {
          err(result.getErrCode() + " " + result.getErrText());
          return result;
      }
      return result;
  }
  /**
   * �鿴����������
   */
  public TParm maxTranDate(){
	  String sql = "SELECT MAX(TRANDATE) TRANDATE  FROM INV_DDSTOCK " ;
	  TParm result = new TParm(TJDODBTool.getInstance().select(sql)) ;
      if (result.getErrCode() < 0) {
          err(result.getErrCode() + " " + result.getErrText());
          return result;
      }
      return result;
  }
  /**
   * �鿴�սᵱ�յ������Ƿ��ս�
   */
  public TParm yesterdayTranDate(String yesterday){
	  String sql = "SELECT  TRANDATE  FROM INV_DDSTOCK WHERE TRANDATE ='"+yesterday+"'" ;
	  TParm result = new TParm(TJDODBTool.getInstance().select(sql)) ;
      if (result.getErrCode() < 0) {
          err(result.getErrCode() + " " + result.getErrText());
          return result;
      }
      return result;
  }
  /**
   * ���������뷢Ʊxml�Ա�
   * @param parm
   * @return
   */
  public TParm getCompareData(TParm parm){
	  String sql=" SELECT A.INV_CODE,A.INV_CHN_DESC,A.DESCRIPTION,A.STOCK_UNIT," +
	  		     "  B.CQTY,C.DQTY,B.CQTY-C.DQTY D_VALUE FROM" +
	  		     " INV_BASE A ,(SELECT INV_CODE,SUM(QTY) CQTY FROM INV_VERIFYIND WHERE " +
	  		     " VERIFYIN_NO = '"+parm.getValue("VERIFYIN_NO")+"' GROUP BY INV_CODE ) B ," +
	  		     " ( SELECT INV_CODE,SUM(TOTAL_OUT_QTY) DQTY FROM INV_ACCOUNT " +
	  		     " WHERE CLOSE_DATE = '"+parm.getValue("CLOSE_DATE")+"' GROUP BY INV_CODE) C " +
	  		     " WHERE A.INV_CODE = B.INV_CODE " +
	  		     " AND   A.INV_CODE = C.INV_CODE " +
	  		     " AND B.CQTY-C.DQTY <> '0' "  ;
	  System.out.println("sql=���������뷢Ʊxml�Ա�==="+sql);
	  TParm result = new TParm(TJDODBTool.getInstance().select(sql)) ;
	  if (result.getErrCode() < 0) {
          err(result.getErrCode() + " " + result.getErrText());
          return result;
      }
	  return result ;
  }
  /**
   * �����ս� ��һ������������
   */
  public TParm againInVerifyin(TParm parm){
	 
	  //һ�������� ���ݿ��ҷ��࣬һ��������
	  String sql = " SELECT SUM(B.QTY) QTY,B.UNIT_PRICE,B.INV_CODE,B.UNIT_PRICE*QTY AMT FROM" +
	  		       " INV_VERIFYINM A,INV_VERIFYIND B " +
	  		       " WHERE A.CHECK_FLG = 'Y' " +
	  		       " AND A.VERIFYIN_DATE BETWEEN TO_DATE ('"+parm.getValue("START_DATE")+"','YYYYMMDDHH24MISS')" +
	  		       " AND TO_DATE ('"+parm.getValue("END_DATE")+"','YYYYMMDDHH24MISS')" +
	  		       " AND A.VERIFYIN_DEPT = '"+parm.getValue("ORG_CODE")+"'" +
	  		       " AND B.INV_CODE = '"+parm.getValue("INV_CODE")+"'" +
	  		       " GROUP BY B.INV_CODE,B.UNIT_PRICE,B.UNIT_PRICE*QTY " ;
	  TParm result = new TParm(TJDODBTool.getInstance().select(sql));
	  if(result.getErrCode()<0){
		  err(result.getErrCode() + " " + result.getErrText());
          return result;
	  }
	  TParm resultParm = new TParm() ;
	 if(result.getCount()<0){
		 resultParm.setData("QTY", 0) ; 
		 resultParm.setData("UNIT_PRICE", 0) ; 
		 resultParm.setData("AMT", 0) ; 
		 return resultParm ;
	 }
	 return result.getRow(0) ;
  } 
  /**
   * �����ս� �����벡������ 
   */
  public TParm againPatRecord(TParm parm){
	 
	  //һ�������� ���ݿ��ҷ��࣬һ��������
	  String sql = " SELECT SUM(QTY) QTY  FROM" +
	  		       " SPC_INV_RECORD " +
	  		       " WHERE BILL_FLG = 'Y' " +
	  		       " AND BILL_DATE BETWEEN TO_DATE ('"+parm.getValue("START_DATE")+"','YYYYMMDDHH24MISS')" +
	  		       " AND TO_DATE ('"+parm.getValue("END_DATE")+"','YYYYMMDDHH24MISS')" +
	  		       " AND EXE_DEPT_CODE = '"+parm.getValue("ORG_CODE")+"'" +
	  		       " AND INV_CODE = '"+parm.getValue("INV_CODE")+"'" +
	  		     "   AND BATCH_SEQ = '"+parm.getValue("BATCH_SEQ")+"'"  ;
	  TParm result = new TParm(TJDODBTool.getInstance().select(sql));
	  if(result.getErrCode()<0){
		  err(result.getErrCode() + " " + result.getErrText());
          return result;
	  }
	  TParm resultParm = new TParm() ;
	 if(result.getCount()<0){
		 resultParm.setData("QTY", 0) ; 
		 resultParm.setData("UNIT_PRICE", 0) ; 
		 resultParm.setData("AMT", 0) ; 
		 return resultParm ;
	 }
	 return result.getRow(0) ;
  } 
  /**
   * �����ս� ������Ŀ����
   */
  public TParm againDDstockQty(TParm parm){
	 
	  //һ�������� ���ݿ��ҷ��࣬һ��������
	  String sql = " SELECT DD_STOCK_QTY,DD_STOCK_AMT,DD_STOCK_PRICE FROM" +
	  		       " INV_DDSTOCK " +
	  		       " WHERE TRANDATE = '"+parm.getValue("YESTERDAY")+"' " +
	  		       " AND ORG_CODE = '"+parm.getValue("ORG_CODE")+"'" +
	  		       " AND INV_CODE = '"+parm.getValue("INV_CODE")+"'" +
	  		       " AND BATCH_SEQ = '"+parm.getValue("BATCH_SEQ")+"'" ;
	  TParm result = new TParm(TJDODBTool.getInstance().select(sql));
	  if(result.getErrCode()<0){
		  err(result.getErrCode() + " " + result.getErrText());
          return result;
	  }
	  TParm resultParm = new TParm() ;
		 if(result.getCount()<0){
			 resultParm.setData("DD_STOCK_QTY", 0) ; 
			 resultParm.setData("DD_STOCK_AMT", 0) ; 
			 return resultParm ;
		 }
	 return result.getRow(0) ;
  } 
  /**
   * ���� �����ս���ҵ
   * @param parm TParm
   * @param conn TConnection
   * @return TParm
   */
  public TParm onAgainInvStockBatch(TParm parm, TConnection conn) {
      // �����
      TParm result1 = new TParm();
      result1.setErrCode(-1) ;
      // ���ݼ��
      if (parm == null)
          return result1;
      TParm resutlParm = new TParm();
      //��ѯ�տ�潻�׵�
      TParm result = this.getDayStock(parm) ;
      if (result.getErrCode() < 0) {
          return result1;
      }
      //�ս�������ҵ
      TParm queryParm =new TParm() ;
      for (int i = 0; i < result.getCount("INV_CODE"); i++) {
          // д�����ʿ���ս���ҵ
    	  queryParm.setData("START_DATE", parm.getValue("START_DATE"));
    	  queryParm.setData("END_DATE", parm.getValue("END_DATE"));
    	  queryParm.setData("ORG_CODE", result.getData("ORG_CODE",i));
    	  queryParm.setData("INV_CODE", result.getData("INV_CODE",i));
    	  queryParm.setData("BATCH_SEQ", result.getData("BATCH_SEQ",i));
    	  queryParm.setData("YESTERDAY",  parm.getValue("YESTERDAY"));
    	   //�����������
    	  TParm verifyinParm = this.againInVerifyin(queryParm) ; 
    	  //���벡������
    	  //�������� ���ǳ����ܹ�
    	  //���տ������
    	  TParm ddstockParm = this.againDDstockQty(queryParm);
    	  //�������յõ����յĵ�ǰ��棬last_stock_qty + in_qty - out_qty + modi_qty
    	  //���տ����
    	  int  last_stock_qty = ddstockParm.getInt("DD_STOCK_QTY") ;
    	  //������
    	  int in_qty = verifyinParm.getInt("QTY") ;
    	  //������
    	  int  out_qty = 0 ;
    	  //�̵���
    	  int modi_qty = 0 ;
    	  //��ǰ�����
    	  int stock_qty  = last_stock_qty + in_qty - out_qty + modi_qty ;
    	  //����
    	  double price = result.getDouble("UNIT_PRICE") ;
          TParm inparm = new TParm();
          inparm.setData("TRANDATE",
                         parm.getValue("START_DATE").substring(0, 8));
          inparm.setData("ORG_CODE", result.getData("ORG_CODE",i));     
          inparm.setData("INV_CODE", result.getData("INV_CODE", i));
          inparm.setData("BATCH_SEQ", result.getData("BATCH_SEQ", i));
          inparm.setData("REGION_CODE", result.getData("REGION_CODE", i));
          inparm.setData("BATCH_NO", result.getData("BATCH_NO", i));
          inparm.setData("VALID_DATE", result.getData("VALID_DATE", i));
          inparm.setData("DD_IN_QTY", verifyinParm.getInt("QTY"));
          inparm.setData("DD_OUT_QTY", out_qty);//������
          inparm.setData("DD_CHECKMODI_QTY", modi_qty);//�̵���
          inparm.setData("DD_STOCK_QTY", stock_qty);//��������
          inparm.setData("DD_STOCK_AMT", stock_qty*price);//��������
          inparm.setData("LAST_DD_STOCK_QTY", last_stock_qty);//���տ����
          inparm.setData("LAST_DD_STOCK_AMT", ddstockParm.getDouble("DD_STOCK_AMT"));  //���տ����
          inparm.setData("DD_VERIFYIN_QTY", verifyinParm.getInt("QTY"));
          inparm.setData("DD_VERIFYIN_AMT", verifyinParm.getDouble("AMT"));
          inparm.setData("DD_GIFTIN_QTY", 0);
          inparm.setData("DD_REGRESSGOODS_QTY",0);//���˻���
          inparm.setData("DD_REGRESSGOODS_AMT",0);//���˻����
          inparm.setData("DD_ASSIGNIN_QTY",0);//�����������
          inparm.setData("DD_ASSIGNOUT_QTY",0);//�����������
          inparm.setData("DD_CHANGEIN_QTY",0);//�յ��������
          inparm.setData("DD_CHANGEOUT_QTY",0);//�յ���������
          inparm.setData("DD_TRANSMITIN_QTY", 0);//���ˎ������
          inparm.setData("DD_TRANSMITOUT_QTY",0);//���ˎ������
          inparm.setData("DD_WASTE_QTY", 0);//�ՃȲ��ēp����
          inparm.setData("DD_DISPENSE_QTY", 0);//�ռ��벡������
          inparm.setData("DD_REGRESS_QTY", 0);//�ռ��벡���˻�����
          inparm.setData("DD_STOCK_PRICE", price);
          inparm.setData("DD_NHI_PRICE", price);
          inparm.setData("DD_OWN_PRICE", price);
          inparm.setData("DD_RPP_AMT", price);
          inparm.setData("OPT_USER", parm.getValue("OPT_USER"));
          inparm.setData("OPT_TERM", parm.getValue("OPT_TERM"));
          resutlParm = this.insertDDStock(inparm, conn) ;
          if (resutlParm.getErrCode() < 0) {
              return result1;
          }
      }
      return resutlParm;
  } 
}

