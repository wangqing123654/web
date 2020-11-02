package com.javahis.ui.ekt;

import java.sql.Timestamp;
import java.text.DecimalFormat;
//import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

//import javax.print.DocFlavor.STRING;

//import jdo.bil.BILAccountTool;
import jdo.bil.BILSysParmTool;
import jdo.ekt.EKTTool;
import jdo.sys.Operator;
import jdo.sys.SYSOperatorTool;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.StringUtil;

/**
*
* <p>Title: 医疗卡售卡结算日结控制类</p>
*
* <p>Description: 医疗卡售卡结算日结控制类</p>
*
* <p>Copyright: Copyright (c) Liu dongyang 2008</p>
*
* <p>Company: JavaHis</p>
*
* @author zhangp
* @version 1.0
*/

public class EKTAccountDailyControl extends TControl{
	String accountSeq = "";
	
    public void onInit() {
        super.onInit();
        //table监听单击事件
        TTable table = (TTable)this.getComponent("Table");
        //table监听checkBox事件
        table.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
                               "onTableComponent");
        initPage();
        TComboBox cboRegion = (TComboBox)this.getComponent("REGION_CODE");
        cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(this.
                getValueString("REGION_CODE")));
        String opid = Operator.getID();
        setValue("CASHIER_CODE", opid);

    }
    /**
     * 初始化界面
     */
    public void initPage() {
        //初始化院区
        setValue("REGION_CODE", Operator.getRegion());
        //初始化查询起时,迄时
        Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance().
                                                  getDate(), -1);
        Timestamp today = SystemTool.getInstance().getDate();
        setValue("S_DATE", yesterday);
        setValue("E_DATE", today);
        String todayTime = StringTool.getString(today, "HH:mm:ss");
        String accountTime = todayTime;
        if (getAccountDate().length() != 0) {
            accountTime = getAccountDate();
            accountTime =getStartTime(accountTime);//=========pangben modify 20110411
        }
        //查询条件起始日期设成零点
        String[] s_time=  accountTime.split(":");
        //将数据库查询的起始日期添加一秒方法
        s_time=  startTimeTemp(s_time,s_time.length-1);
        //转换页面显示的格式
        setValue("S_TIME", getStartTime(startTime(s_time)));
        setValue("E_TIME", accountTime);

        //置日结按钮为灰
        callFunction("UI|unreg|setEnabled", false);
        callFunction("UI|arrive|setEnabled", false);

    }
    /**
     * 得到结账时间点
     * @return String
     */
    public String getAccountDate() {
        String accountDate = "";
        TParm accountDateParm = new TParm();
        accountDateParm = BILSysParmTool.getInstance().getDayCycle("O");
        accountDate = accountDateParm.getValue("DAY_CYCLE", 0);
        return accountDate;
    }
    /**
     * 获得时间格式的开始日期
     * @param accountTime String
     * @return String
     */
    public String getStartTime(String accountTime){
        return accountTime.substring(0, 2) + ":" +
              accountTime.substring(2, 4) + ":" + accountTime.substring(4, 6);
    }
    /**
     * 开始日期添加一秒的递归方法
     * @param time String[]
     * @param i int
     * @return String[]
     */
    public String[] startTimeTemp(String[] time,int i){
        if(i<0)
             return time;//返回得到的数组
        else {
            //判断是否是可以进位的时间数字
            if (Integer.parseInt(time[i]) == 59 ||
                Integer.parseInt(time[i]) == 23) {
                time[i] = "00";
            } else {
                //不能进位将添加一个数
                if((Integer.parseInt(time[i]) + 1)<10)
                    time[i] =  "0"+(Integer.parseInt(time[i]) + 1) ;
                else
                    time[i] =  ""+(Integer.parseInt(time[i]) + 1) ;
                i=-1;//退出递归循环
            }
           return startTimeTemp(time,i-1);
        }
      }
    /**
     * 将数组转换获得开始时间字符串方法
     * @param startTime String[]
     * @return String
     */
    public String startTime(String[] startTime){
        String s_time="";
        for(int i=0;i<startTime.length;i++)
            s_time+=startTime[i];
        return s_time;
    }
    
    /**
     * 日结
     */
    public void onSave() {
        TParm parm = new TParm();
        String today = StringTool.getString(TypeTool.getTimestamp(SystemTool.
            getInstance().getDate()), "yyyyMMdd");
        String todayTime = StringTool.getString(SystemTool.getInstance().
                                                getDate(), "HHmmss");
        String accountTime = todayTime;
        if (getAccountDate().length() != 0) {
            accountTime = getAccountDate();//235959
        }
        parm.setData("BUSINESS_DATE", today + accountTime);
        String accountNo = SystemTool.getInstance().getNo("ALL","EKT","ACCOUNT_SEQ","ACCOUNT_SEQ");
        String casherUser= this.getValueString("CASHIER_CODE");
        TParm casherParm;
        if (casherUser == null || casherUser.length() == 0) {
        	this.messageBox("收费员不允许为空，请选择一个收费员!!!!");
        	return;
//            //得到全部门诊日结人员
//            casherParm = getAccountUser(Operator.getRegion());
//            //取出人员的个数
//            int row = casherParm.getCount();
//            //循环所有的收费员
//            //===zhangp 20120227 modify start
//            List<String> successCashier = new ArrayList<String>();
//            List<String>  faileCashier = new ArrayList<String>();
//            //====zhangp 20120227 modify end
//            for (int i = 0; i < row; i++) {
//                //取出一个收费人员
//                casherUser = casherParm.getValue("USER_ID", i);
//                //调用一个人的日结程序
//                if (!accountOneCasher(parm,accountNo,casherUser)) {
////                    messageBox("" + casherParm.getValue("USER_NAME", i) +
////                               "日结失败!");
//                	//====zhangp 20120227 modify start
////                    this.messageBox(casherParm.getValue("USER_NAME", i) + "无日结数据!");
//                	faileCashier.add(casherParm.getValue("USER_NAME", i));
//                	//=======zhangp 20120227 modify end
//                    continue;
//                }
//              //====zhangp 20120227 modify start
////                messageBox(casherParm.getValue("USER_NAME", i) + "日结成功!");
//                successCashier.add(casherParm.getValue("USER_NAME", i));
//              //=======zhangp 20120227 modify end
//            }
//          //====zhangp 20120227 modify start
//            String successCashiers = "";
//            String faileCashiers = "";
//            if(successCashier.size()>0){
//            	for (int i = 0; i < successCashier.size(); i++) {
//            		if(i%10==0){
//            			successCashiers = successCashiers+"," + successCashier.get(i) + "\n";
//            		}else{
//            			successCashiers = successCashiers+"," + successCashier.get(i);
//            		}
//				}
//            }
//            if(faileCashier.size()>0){
//            	for (int i = 0; i < faileCashier.size(); i++) {
//            		if(i%10==0){
//            			faileCashiers = faileCashiers+"," + faileCashier.get(i) + "\n";
//            		}else{
//            			faileCashiers = faileCashiers+"," + faileCashier.get(i);
//            		}
//				}
//            }
//            if(!faileCashiers.equals("")){
//            	messageBox(faileCashiers+"\n无日结数据!");
//            }
//            if(!successCashiers.equals("")){
//            	messageBox(successCashiers+"\n日结成功!");
//            }
//          //=======zhangp 20120227 modify end
//            return ;
        }else{
            if (!accountOneCasher(parm,accountNo,casherUser)) {
                    messageBox("无日结数据!");
                    return ;
                }
                messageBox("日结成功!");
            return ;
        }
        
        
    }
    
    /**
     * 得到日结人员组
     * @return String[]
     */
    public TParm getAccountUser(String regionCode) {
        String region = "";
        if (!"".equals(regionCode) && null != regionCode)
            region = " AND region_code = '" + regionCode + "' ";
        String sql =
                "SELECT user_id AS USER_ID, user_name AS USER_NAME, user_eng_name AS enname, py1, py2" +
                "  FROM sys_operator "
                + " WHERE pos_code IN (SELECT pos_code"
                + " FROM sys_position"
                + " WHERE pos_type = '5') " + region +//pos_type = '5'收费员权限
                "ORDER BY user_id";
        TParm accountUser = new TParm(TJDODBTool.getInstance().select(sql));
        if (accountUser.getErrCode() < 0)
            System.out.println(" 取得收费员 " + accountUser.getErrText());
        return accountUser;
    }
    
    /**
     * 日结实现所用用户日结方法
     * @param parm TParm
     * @param accountNo String
     * @return boolean
     */
    public boolean  accountOneCasher(TParm parm,String accountNo,String casherUser){
        parm.setData("ACCOUNT_USER", casherUser);
        parm.setData("ACCOUNT_DATE", SystemTool.getInstance().getDate());
        parm.setData("ACCOUNT_SEQ", accountNo);
        parm.setData("ACCOUNT_TYPE", "EKT");
        parm.setData("REGION_CODE", Operator.getRegion());
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        TParm result = TIOM_AppServer.executeAction("action.ekt.EKTAction",
                "onEKTAccount", parm);
//        System.out.println("1result result result is ::"+result);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return false;
        } else {
            //日结成功
            return true;
        }
    }
    
    /**
     * 查询
     */
    public void onQuery() {
        String startTime = StringTool.getString(TypeTool.getTimestamp(getValue(
            "S_DATE")), "yyyyMMdd");
        String endTime = StringTool.getString(TypeTool.getTimestamp(getValue(
            "E_DATE")), "yyyyMMdd");
        Timestamp today = SystemTool.getInstance().getDate();
        String todayTime = StringTool.getString(today, "HHmmss");
        String accountTime = todayTime;
        if (getAccountDate().length() != 0) {
            accountTime = getAccountDate();
            accountTime = accountTime.substring(0, 2) +
                accountTime.substring(2, 4) + accountTime.substring(4, 6);
        }
        TParm result = new TParm();
        TParm selAccountData = new TParm();
        selAccountData.setData("ACCOUNT_TYPE", "REG");
        if (this.getValueString("CASHIER_CODE") == null ||
            this.getValueString("CASHIER_CODE").length() == 0) {}
        else
            selAccountData.setData("ACCOUNT_USER", this.getValueString("CASHIER_CODE"));
        selAccountData.setData("S_TIME", startTime + "000000");
        selAccountData.setData("E_TIME", endTime + accountTime);
        if(this.getValue("REGION_CODE")!=null&&!this.getValue("REGION_CODE").equals(""))
            selAccountData.setData("REGION_CODE",this.getValue("REGION_CODE"));
//         if(this.getValue("ADM_TYPE")!=null&&!this.getValue("ADM_TYPE").equals(""))
//            selAccountData.setData("ADM_TYPE",this.getValue("ADM_TYPE"));
        result = EKTTool.getInstance().accountQuery(selAccountData);
        if(result.getCount()==0){
            this.messageBox("没有要查询的数据");
        }else{
        	double memAmt = 0.00;
        	double lpkAmt = 0.00;
        	double pakgeAmt = 0.00;
        	double arAmt = 0.00;
        	for(int i = 0;i<result.getCount();i++){
        		//取出日结时间
//            	String accountDate = result.getValue("ACCOUNT_DATE", i).toString().substring(0, 19)
//            	                                    .replace("-", "").replace("/", "").replace(" ", "").replace(":", "");
            	String accountSeq = result.getValue("ACCOUNT_SEQ", i).toString();
            	TParm memResultParm = this.onPrintMemDate(accountSeq);
            	TParm lpkResultParm =this.onPrintLpkDate(accountSeq);
            	TParm pakgeResultParm =this.onPrintPakgeDate(accountSeq);
            	lpkResultParm.getData("TOT_MEDICAL_AMT2", 0);
            	memAmt = memResultParm.getDouble("TOT_MEDICAL_AMT1");
            	lpkAmt = lpkResultParm.getDouble("TOT_MEDICAL_AMT3");
            	pakgeAmt = pakgeResultParm.getDouble("TOT_MEDICAL_AMT2");
            	arAmt = result.getDouble("AR_AMT", i)+memAmt+lpkAmt+pakgeAmt;
            	result.setData("AR_AMT", i, arAmt);
        	}
        }
        
        this.callFunction("UI|Table|setParmValue", result);

    }
    
    /**
     * 清空
     */
    public void onClear() {
        initPage();
        TTable table = (TTable)this.getComponent("Table");
        table.removeRowAll();
        this.setValue("SELECT","N");
        setValue("TOGEDER_FLG","N");
        accountSeq = new String();
    }
    
    /**
     * 医疗卡日结抓数据（现金交易总金额和笔数
     * @param flg
     * @param casherUser
     * @param accountTime
     * @return
     */
    public TParm selectEktaccntDetailSum(String flg,String casherUser,String accountTime){
    	TParm parm = new TParm();
    	String sql = "SELECT SUM(A.BUSINESS_AMT) AS SUM,COUNT(1) AS COUNT " +
	 		" FROM EKT_ACCNTDETAIL A,EKT_BIL_PAY B " +
	 		" WHERE A.ACCNT_STATUS = '1' AND A.ACCOUNT_SEQ IS NULL " +
	 		" AND A.CHARGE_FLG = '"+flg+"' " +
	 		" AND A.CASHIER_CODE = '"+casherUser+"' " +
	 		" AND A.BUSINESS_DATE <TO_DATE('"+accountTime+"','yyyyMMddHH24miss') " +
	 		" AND B.BIL_BUSINESS_NO = A.BUSINESS_NO";
    	parm = new TParm(TJDODBTool.getInstance().select(sql));
    	return parm;
    	
    }
    /**
	 * 打印预览(日结之前的预览功能)
	 * 
	 */
    public void onPrintReview(){
    	 String accountTime = StringTool.getString(SystemTool.getInstance().getDate(),
         "yyyyMMddHHmmss");
    	 String account_date1 = StringTool.getString(SystemTool.getInstance().getDate(),
         "yyyy/MM/dd HH:mm:ss");
    	 String casherUser= this.getValueString("CASHIER_CODE");
    	 if(casherUser.length() == 0){
    		 return;
    	 }
    	 TParm accountParm = new TParm();
    	 DecimalFormat df = new DecimalFormat("########0.00");
    	 
    	 TParm data = new TParm();
    	 data.setData("TITLE1", "TEXT",  Operator.getHospitalCHNShortName()+"门诊预收款日结(预览)");
    	 String casher = SYSOperatorTool.getInstance().selectdata(casherUser).getValue("USER_NAME", 0);
    	 accountParm.setData("CASHIER1","收费员："+casher);
 		accountParm.setData("PRINT_DATE", account_date1);//单个的打印时，时间取日结时间
 		accountParm.setData("ACCOUNT_SEQ", "");
 		accountParm.setData("PARAM0", "日结日期：");
 		accountParm.setData("PARAM1", "报表号：");
 		String billsql =
    		"SELECT BUSINESS_DATE,BUSINESS_NO,CASHIER_CODE FROM EKT_ACCNTDETAIL  WHERE ACCNT_STATUS = '1' AND ACCOUNT_SEQ IS NULL " +
    		" AND CASHIER_CODE = '"+casherUser+"' " +
	 		" AND BUSINESS_DATE <TO_DATE('"+accountTime+"','yyyyMMddHH24miss')  " +
    		" AND CHARGE_FLG IN ('3','4','5','7','8')   " +//(1,扣款;2,退款;3,医疗卡充值,4,制卡,5,补卡,7,退费,8,换卡)
    		" UNION (SELECT OPT_DATE AS BUSINESS_DATE,TRADE_NO AS BUSINESS_NO,OPT_USER AS CASHIER_CODE " +
    		" FROM MEM_TRADE WHERE STATUS IN ('1','2','3','4') AND MEM_FEE >=0 " +
			" AND (SALE_DATE<TO_DATE('"+accountTime+"','YYYYMMDDHH24MISS') OR SALE_DATE IS NULL) " +
			" AND OPT_USER = '"+casherUser+"' " +
			" AND (ACCOUNT_FLG='N' OR ACCOUNT_FLG IS NULL)  ) " +
    		" UNION (SELECT BILL_DATE AS BUSINESS_DATE,TRADE_NO AS BUSINESS_NO,CASHIER_CODE " +
    		" FROM MEM_PACKAGE_TRADE_M " +
    		" WHERE BILL_DATE<TO_DATE('"+accountTime+"','YYYYMMDDHH24MISS' )  " + 
			" AND CASHIER_CODE = '"+casherUser+"' " + 
			" AND (ACCOUNT_FLG='N' OR ACCOUNT_FLG IS NULL) )" +
    		" UNION (SELECT OPT_DATE AS BUSINESS_DATE,TRADE_NO AS BUSINESS_NO,OPT_USER AS CASHIER_CODE " +
    		" FROM MEM_GIFTCARD_TRADE_M " +
    		" WHERE OPT_DATE<TO_DATE('"+accountTime+"','YYYYMMDDHH24MISS') "+  
			" AND OPT_USER = '"+casherUser+"'  " + 
			" AND (ACCOUNT_FLG='N' OR ACCOUNT_FLG IS NULL) )" +
    		" ORDER BY BUSINESS_DATE,BUSINESS_NO  ";
    	System.out.println("1111billsql billsql billsql is ::"+billsql);
    	TParm billParm = new TParm(TJDODBTool.getInstance().select(billsql));
    	String date ="";
    	if(billParm.getCount() > 0){
    		date = getAccntDate(billParm.getData("BUSINESS_DATE", 0).toString(),billParm.getData("BUSINESS_DATE", billParm.getCount()-1).toString());
    	}
    	
    	accountParm.setData("BISSINESS_DATE", date);   	
    	accountParm.setData("CASHIER",  casher);
    	 
    	 //医疗卡
    	 int pay_medical_qty = 0;//充值数量
    	 int npay_medical_qty = 0;//退费数量
     	 double npay_medical_amt = 0.00;//退费金额
     	 double pay_medical_atm = 0.00;//充值金额
     	 TParm result = selectEktaccntDetailSum("3",casherUser,accountTime);
     	 if (result.getCount() > 0) {
     		 pay_medical_qty = new Integer(result.getData("COUNT", 0).toString());
         	 pay_medical_atm = StringTool.getDouble(result.getData("SUM", 0).toString());
		 }
     	 result = selectEktaccntDetailSum("7",casherUser,accountTime);
    	 if (result.getCount() > 0) {
    		 npay_medical_qty = new Integer(result.getData("COUNT", 0).toString());
    		 npay_medical_amt = StringTool.getDouble(result.getData("SUM", 0).toString());
		 }
     billsql = 
     		//===zhangp 201203 start
     		"SELECT SUM(AMT) AMT ,SUM(PROCEDURE_AMT) PROCEDURE_AMT,A.GATHER_TYPE,A.ACCNT_TYPE " +
     		" FROM EKT_BIL_PAY A,EKT_ACCNTDETAIL B "+
     		" WHERE A.BIL_BUSINESS_NO = B.BUSINESS_NO AND B.ACCNT_STATUS = '1' " +
     		" AND B.BUSINESS_DATE < TO_DATE('"+accountTime+"','yyyyMMddHH24miss') " +
     		" AND B.CASHIER_CODE = '"+casherUser+"' AND B.ACCOUNT_SEQ IS NULL" +
     		" GROUP BY A.GATHER_TYPE,A.ACCNT_TYPE ORDER BY A.ACCNT_TYPE";
     	TParm gatherParm = new TParm(TJDODBTool.getInstance().select(billsql));
     	double inCash = 0.00;//现金收入
    	double inB = 0.00;//支票收入
    	double inCard = 0.00;//刷卡收入
    	double inlpk = 0.00;//礼品卡收入
    	double inXjdyq = 0.00;//现金抵用券收入
    	double inWx = 0.00;//微信收入
    	double inZfb = 0.00;//支付宝收入
    	double inEkt = 0.00;//医疗卡收入
    	double inC4 = 0.00;//医院垫付收入
    	double outCash = 0.00;//现金退费
    	double outB = 0.00;//支票退费
    	double outCard = 0.00;//刷卡退费
    	double outLpk = 0.00;//礼品卡退费
    	double outXjdyq = 0.00;//现金抵用券退费
    	double outWx = 0.00;//微信退费
    	double outZfb = 0.00;//支付宝退费
    	double outEkt = 0.00;//医疗卡退费
    	double outC4 = 0.00;//医院垫付退费
    	for (int i = 0; i < gatherParm.getCount(); i++) {
			if(gatherParm.getData("ACCNT_TYPE",i).equals("2")){//(1:购卡,2:换卡,3:补卡,4:充值,5:扣款,6:退费)
				if(gatherParm.getData("GATHER_TYPE",i).equals("C0")){//C0现金，写死
					inCash+= gatherParm.getDouble("AMT", i);
				}else
					if(gatherParm.getData("GATHER_TYPE",i).equals("C1")){//C1银行卡，写死
						inCard+= gatherParm.getDouble("AMT", i);
					}else
						if(gatherParm.getData("GATHER_TYPE",i).equals("T0")){//T0支票，写死
							inB+= gatherParm.getDouble("AMT", i);
						}else
							if(gatherParm.getData("GATHER_TYPE",i).equals("LPK")){//LPK礼品卡，写死
								inlpk+=gatherParm.getDouble("AMT", i);
							}else 
								if(gatherParm.getData("GATHER_TYPE",i).equals("XJZKQ")){//XJZKQ现金抵用券，写死
									inXjdyq+=gatherParm.getDouble("AMT", i);
								}else 
									if(gatherParm.getData("GATHER_TYPE",i).equals("WX")){//WX微信 ，写死
										inWx+=gatherParm.getDouble("AMT", i);
									}else 
										if(gatherParm.getData("GATHER_TYPE",i).equals("ZFB")){//ZFB支付宝，写死
											inZfb+=gatherParm.getDouble("AMT", i);
										}else 
											if(gatherParm.getData("GATHER_TYPE",i).equals("EKT")){//EKT医疗卡，写死
												inEkt+=gatherParm.getDouble("AMT", i);
											}else 
												if(gatherParm.getData("GATHER_TYPE",i).equals("C4")){// c4医院垫付，写死
													inC4+=gatherParm.getDouble("AMT", i);
												}
			}
			if(gatherParm.getData("ACCNT_TYPE",i).equals("4")){//(1:购卡,2:换卡,3:补卡,4:充值,5:扣款,6:退费)
				if(gatherParm.getData("GATHER_TYPE",i).equals("C0")){//C0现金，写死
					inCash+= gatherParm.getDouble("AMT", i);
				}else
					if(gatherParm.getData("GATHER_TYPE",i).equals("C1")){//C1银行卡，写死
						inCard+= gatherParm.getDouble("AMT", i);
					}else
						if(gatherParm.getData("GATHER_TYPE",i).equals("T0")){//T0支票，写死
							inB+= gatherParm.getDouble("AMT", i);
						}else
							if(gatherParm.getData("GATHER_TYPE",i).equals("LPK")){//LPK礼品卡，写死
								inlpk+=gatherParm.getDouble("AMT", i);
							}else 
								if(gatherParm.getData("GATHER_TYPE",i).equals("XJZKQ")){//XJZKQ现金抵用券，写死
									inXjdyq+=gatherParm.getDouble("AMT", i);
								}else 
									if(gatherParm.getData("GATHER_TYPE",i).equals("WX")){//WX微信，写死
										inWx+=gatherParm.getDouble("AMT", i);
									}else 
										if(gatherParm.getData("GATHER_TYPE",i).equals("ZFB")){//ZFB支付宝，写死
											inZfb+=gatherParm.getDouble("AMT", i);
										}else 
											if(gatherParm.getData("GATHER_TYPE",i).equals("EKT")){//EKT医疗卡，写死
												inEkt+=gatherParm.getDouble("AMT", i);
											}else 
												if(gatherParm.getData("GATHER_TYPE",i).equals("C4")){// c4医院垫付，写死
													inC4+=gatherParm.getDouble("AMT", i);
												}
			}
			if(gatherParm.getData("ACCNT_TYPE",i).equals("6")){//(1:购卡,2:换卡,3:补卡,4:充值,5:扣款,6:退费)
				if(gatherParm.getData("GATHER_TYPE",i).equals("C0")){//C0现金，写死
					outCash+= gatherParm.getDouble("AMT", i);
				}else
					if(gatherParm.getData("GATHER_TYPE",i).equals("C1")){//C1银行卡，写死
						outCard+= gatherParm.getDouble("AMT", i);
					}else
						if(gatherParm.getData("GATHER_TYPE",i).equals("T0")){//T0支票，写死
							outB+= gatherParm.getDouble("AMT", i);
						}else
							if(gatherParm.getData("GATHER_TYPE",i).equals("LPK")){//LPK礼品卡，写死
								outLpk+=gatherParm.getDouble("AMT", i);
							}else 
								if(gatherParm.getData("GATHER_TYPE",i).equals("XJZKQ")){//XJZKQ现金抵用券，写死
									outXjdyq+=gatherParm.getDouble("AMT", i);
								}else 
									if(gatherParm.getData("GATHER_TYPE",i).equals("WX")){//WX微信，写死
										outWx=gatherParm.getDouble("AMT", i);
									}else 
										if(gatherParm.getData("GATHER_TYPE",i).equals("ZFB")){//ZFB支付宝，写死
											outZfb+=gatherParm.getDouble("AMT", i);
										}else 
											if(gatherParm.getData("GATHER_TYPE",i).equals("EKT")){//EKT医疗卡，写死
												outEkt+=gatherParm.getDouble("AMT", i);
											}else 
												if(gatherParm.getData("GATHER_TYPE",i).equals("C4")){// c4医院垫付，写死
													outC4+=gatherParm.getDouble("AMT", i);
												}
			}
		}
    	double totCash = inCash -outCash;//现金
    	double totB = inB - outB;//支票
    	double totCard = inCard - outCard;//刷卡
    	double totLpk = inlpk - outLpk;//礼品卡
    	double totXjdyq = inXjdyq - outXjdyq;//现金抵用券
    	double totWx = inWx - outWx;//微信
    	double totZfb = inZfb - outZfb;//支付宝
    	double totEkt = inEkt - outEkt; //医疗卡
    	double totC4 = inC4 - outC4; //医院垫付 
    	accountParm.setData("PAY_MEDICAL_QTY", pay_medical_qty);
    	accountParm.setData("NPAY_MEDICAL_QTY", npay_medical_qty);
    	accountParm.setData("TOT_MEDICAL_QTY", npay_medical_qty+pay_medical_qty);
    	accountParm.setData("PAY_MEDICAL_ATM_TOTAL", df.format(StringTool.round((pay_medical_atm),2)));
    	accountParm.setData("NPAY_MEDICAL_AMT", df.format(StringTool.round(npay_medical_amt,2)));
    	accountParm.setData("TOT_MEDICAL_AMT", df.format(StringTool.round((pay_medical_atm-npay_medical_amt),2)));
    	accountParm.setData("IN_CASH", df.format(StringTool.round(inCash,2)));
    	accountParm.setData("IN_B", df.format(StringTool.round(inB,2)));
    	accountParm.setData("IN_CARD", df.format(StringTool.round(inCard,2)));
    	accountParm.setData("IN_LPK", df.format(StringTool.round(inlpk,2)));//礼品卡
    	accountParm.setData("IN_XJDYQ", df.format(StringTool.round(inXjdyq,2)));//现金抵用券
    	accountParm.setData("IN_WX", df.format(StringTool.round(inWx,2)));//微信
    	accountParm.setData("IN_ZFB", df.format(StringTool.round(inZfb,2)));//支付宝
    	accountParm.setData("IN_EKT", df.format(StringTool.round(inEkt,2)));//医疗卡
    	accountParm.setData("IN_C4", df.format(StringTool.round(inC4,2)));
    	accountParm.setData("OUT_CASH", df.format(StringTool.round(outCash,2)));
    	accountParm.setData("OUT_B", df.format(StringTool.round(outB,2)));
    	accountParm.setData("OUT_CARD", df.format(StringTool.round(outCard,2)));
    	accountParm.setData("OUT_LPK", df.format(StringTool.round(outLpk,2)));//礼品卡
    	accountParm.setData("OUT_XJDYQ", df.format(StringTool.round(outXjdyq,2)));//现金抵用券
    	accountParm.setData("OUT_WX", df.format(StringTool.round(outWx,2)));//微信
    	accountParm.setData("OUT_ZFB", df.format(StringTool.round(outZfb,2)));//支付宝
    	accountParm.setData("OUT_EKT", df.format(StringTool.round(outEkt,2)));//医疗卡
    	accountParm.setData("OUT_C4", df.format(StringTool.round(outC4,2)));
    	accountParm.setData("TOT_CASH", df.format(StringTool.round(totCash,2)));
    	accountParm.setData("TOT_B", df.format(StringTool.round(totB,2)));
    	accountParm.setData("TOT_CARD", df.format(StringTool.round(totCard,2)));
    	accountParm.setData("TOT_LPK", df.format(StringTool.round(totLpk,2)));//礼品卡
    	accountParm.setData("TOT_XJDYQ", df.format(StringTool.round(totXjdyq,2)));//现金抵用券
    	accountParm.setData("TOT_WX", df.format(StringTool.round(totWx,2)));//微信
    	accountParm.setData("TOT_ZFB", df.format(StringTool.round(totZfb,2)));//支付宝
    	accountParm.setData("TOT_EKT", df.format(StringTool.round(totEkt,2)));//医疗卡
    	accountParm.setData("TOT_C4", df.format(StringTool.round(totC4,2))); 
    	
    	 TParm memResultParm =this.onPrintReviewMemDate(casherUser, accountTime);//会员卡
         TParm lpkResultParm =this.onPrintReviewLpkDate(casherUser, accountTime);//礼品卡
         TParm pakgeResultParm =this.onPrintReviewPakgeDate(casherUser, accountTime); //套餐
       //会员卡数据
     	accountParm.setData("IN_CASH1", df.format(StringTool.round(memResultParm.getDouble("IN_CASH1"),2)));
     	accountParm.setData("IN_B1", df.format(StringTool.round(memResultParm.getDouble("IN_B1"),2)));
     	accountParm.setData("IN_CARD1", df.format(StringTool.round(memResultParm.getDouble("IN_CARD1"),2)));
     	accountParm.setData("IN_LPK1", df.format(StringTool.round(memResultParm.getDouble("IN_LPK1"),2)));//礼品卡
     	accountParm.setData("IN_XJDYQ1", df.format(StringTool.round(memResultParm.getDouble("IN_XJDYQ1"),2)));//现金抵用券
     	accountParm.setData("IN_WX1", df.format(StringTool.round(memResultParm.getDouble("IN_WX1"),2)));//微信
     	accountParm.setData("IN_ZFB1", df.format(StringTool.round(memResultParm.getDouble("IN_ZFB1"),2)));//支付宝
     	accountParm.setData("IN_C41", df.format(StringTool.round(memResultParm.getDouble("IN_C41"),2)));//医院垫付
     	accountParm.setData("IN_EKT1", df.format(StringTool.round(memResultParm.getDouble("IN_EKT1"),2)));//医疗卡
     	if(memResultParm.getDouble("OUT_CASH1") == 0){
     		accountParm.setData("OUT_CASH1", df.format(StringTool.round(memResultParm.getDouble("OUT_CASH1"),2)));//退款总金额
         		
     	}else{
     		accountParm.setData("OUT_CASH1", df.format(StringTool.round(-memResultParm.getDouble("OUT_CASH1"),2)));//退款总金额
         	
     	}
     	if(memResultParm.getDouble("OUT_CARD1") == 0){
     		accountParm.setData("OUT_CARD1", df.format(StringTool.round(memResultParm.getDouble("OUT_CARD1"),2)));//退款总金额
         		
     	}else{
     		accountParm.setData("OUT_CARD1", df.format(StringTool.round(-memResultParm.getDouble("OUT_CARD1"),2)));//退款总金额
         	
     	}
     	if(memResultParm.getDouble("OUT_B1") == 0){
     		accountParm.setData("OUT_B1", df.format(StringTool.round(memResultParm.getDouble("OUT_B1"),2)));//退款总金额
         		
     	}else{
     		accountParm.setData("OUT_B1", df.format(StringTool.round(-memResultParm.getDouble("OUT_B1"),2)));//退款总金额
         	
     	}
     	if(memResultParm.getDouble("OUT_LPK1") == 0){
     		accountParm.setData("OUT_LPK1", df.format(StringTool.round(memResultParm.getDouble("OUT_LPK1"),2)));//退款总金额
         		
     	}else{
     		accountParm.setData("OUT_LPK1", df.format(StringTool.round(-memResultParm.getDouble("OUT_LPK1"),2)));//退款总金额
         	
     	}
     	if(memResultParm.getDouble("OUT_XJDYQ1") == 0){
     		accountParm.setData("OUT_XJDYQ1", df.format(StringTool.round(memResultParm.getDouble("OUT_XJDYQ1"),2)));//退款总金额
         		
     	}else{
     		accountParm.setData("OUT_XJDYQ1", df.format(StringTool.round(-memResultParm.getDouble("OUT_XJDYQ1"),2)));//退款总金额
         	
     	}
     	if(memResultParm.getDouble("OUT_WX1") == 0){
     		accountParm.setData("OUT_WX1", df.format(StringTool.round(memResultParm.getDouble("OUT_WX1"),2)));//退款总金额
         		
     	}else{
     		accountParm.setData("OUT_WX1", df.format(StringTool.round(-memResultParm.getDouble("OUT_WX1"),2)));//退款总金额
         	
     	}
     	if(memResultParm.getDouble("OUT_ZFB1") == 0){
     		accountParm.setData("OUT_ZFB1", df.format(StringTool.round(memResultParm.getDouble("OUT_ZFB1"),2)));//退款总金额
         		
     	}else{
     		accountParm.setData("OUT_ZFB1", df.format(StringTool.round(-memResultParm.getDouble("OUT_ZFB1"),2)));//退款总金额
         	
     	}
     	if(memResultParm.getDouble("OUT_EKT1") == 0){
     		accountParm.setData("OUT_EKT1", df.format(StringTool.round(memResultParm.getDouble("OUT_EKT1"),2)));//退款总金额
         		
     	}else{
     		accountParm.setData("OUT_EKT1", df.format(StringTool.round(-memResultParm.getDouble("OUT_EKT1"),2)));//退款总金额
         	
     	}
     	if(memResultParm.getDouble("OUT_C41") == 0){
     		accountParm.setData("OUT_C41", df.format(StringTool.round(memResultParm.getDouble("OUT_C41"),2)));//退款总金额
         		
     	}else{
     		accountParm.setData("OUT_C41", df.format(StringTool.round(-memResultParm.getDouble("OUT_C41"),2)));//退款总金额
         	
     	}
     	
     	accountParm.setData("PAY_MEDICAL_ATM_TOTAL1", df.format(StringTool.round(memResultParm.getDouble("PAY_MEDICAL_ATM_TOTAL1"),2)));//收入总金额
     	if(memResultParm.getDouble("NPAY_MEDICAL_AMT1") == 0){
     		accountParm.setData("NPAY_MEDICAL_AMT1", df.format(StringTool.round(memResultParm.getDouble("NPAY_MEDICAL_AMT1"),2)));//退款总金额
         		
     	}else{
     		accountParm.setData("NPAY_MEDICAL_AMT1", df.format(StringTool.round(-memResultParm.getDouble("NPAY_MEDICAL_AMT1"),2)));//退款总金额
         	
     	}
     	accountParm.setData("TOT_MEDICAL_AMT1", df.format(StringTool.round(memResultParm.getDouble("PAY_MEDICAL_ATM_TOTAL1")+memResultParm.getDouble("NPAY_MEDICAL_AMT1"),2)));//总金额
     	accountParm.setData("TOT_CASH1", df.format(StringTool.round(memResultParm.getDouble("TOT_CASH1"),2)));//现金总金额
     	accountParm.setData("TOT_B1", df.format(StringTool.round(memResultParm.getDouble("TOT_B1"),2)));//支票总金额
     	accountParm.setData("TOT_CARD1", df.format(StringTool.round(memResultParm.getDouble("TOT_CARD1"),2)));//刷卡总金额
     	accountParm.setData("TOT_LPK1", df.format(StringTool.round(memResultParm.getDouble("TOT_LPK1"),2)));//礼品卡总金额
     	accountParm.setData("TOT_XJDYQ1", df.format(StringTool.round(memResultParm.getDouble("TOT_XJDYQ1"),2)));//现金抵用券总金额
     	accountParm.setData("TOT_WX1", df.format(StringTool.round(memResultParm.getDouble("TOT_WX1"),2)));//微信总金额
     	accountParm.setData("TOT_ZFB1", df.format(StringTool.round(memResultParm.getDouble("TOT_ZFB1"),2)));//支付宝总金额
     	accountParm.setData("TOT_EKT1", df.format(StringTool.round(memResultParm.getDouble("TOT_EKT1"),2)));//医疗卡总金额
     	accountParm.setData("TOT_C41", df.format(StringTool.round(memResultParm.getDouble("TOT_C41"),2)));//医院垫付总金额
     	accountParm.setData("PAY_MEDICAL_QTY1", memResultParm.getInt("PAY_MEDICAL_QTY1"));//收入总数量
     	accountParm.setData("NPAY_MEDICAL_QTY1", memResultParm.getInt("NPAY_MEDICAL_QTY1"));//退款总数量
     	accountParm.setData("TOT_MEDICAL_QTY1", memResultParm.getInt("PAY_MEDICAL_QTY1")+memResultParm.getInt("NPAY_MEDICAL_QTY1"));//总数量
     	//套餐销售日结
     	accountParm.setData("IN_CASH2", df.format(StringTool.round(pakgeResultParm.getDouble("IN_CASH2"),2)));
     	accountParm.setData("IN_B2", df.format(StringTool.round(pakgeResultParm.getDouble("IN_B2"),2)));
     	accountParm.setData("IN_CARD2", df.format(StringTool.round(pakgeResultParm.getDouble("IN_CARD2"),2)));
     	accountParm.setData("IN_LPK2", df.format(StringTool.round(pakgeResultParm.getDouble("IN_LPK2"),2)));//礼品卡
     	accountParm.setData("IN_XJDYQ2", df.format(StringTool.round(pakgeResultParm.getDouble("IN_XJDYQ2"),2)));//现金抵用券
     	accountParm.setData("IN_WX2", df.format(StringTool.round(pakgeResultParm.getDouble("IN_WX2"),2)));//微信
     	accountParm.setData("IN_ZFB2", df.format(StringTool.round(pakgeResultParm.getDouble("IN_ZFB2"),2)));//支付宝
     	accountParm.setData("IN_EKT2", df.format(StringTool.round(pakgeResultParm.getDouble("IN_EKT2"),2)));//医疗卡
     	accountParm.setData("IN_C42", df.format(StringTool.round(pakgeResultParm.getDouble("IN_C42"),2)));
     	accountParm.setData("OUT_XJDYQ2", df.format(StringTool.round(pakgeResultParm.getDouble("OUT_XJDYQ2"),2)));//现金抵用券
     	accountParm.setData("PAY_MEDICAL_ATM_TOTAL2", df.format(StringTool.round(pakgeResultParm.getDouble("PAY_MEDICAL_ATM_TOTAL2"),2)));//收入总金额
     	if(pakgeResultParm.getDouble("NPAY_MEDICAL_AMT2") == 0){
     		accountParm.setData("NPAY_MEDICAL_AMT2", df.format(StringTool.round(pakgeResultParm.getDouble("NPAY_MEDICAL_AMT2"),2)));//退款总金额
     	}else{
     		accountParm.setData("NPAY_MEDICAL_AMT2", df.format(StringTool.round(-pakgeResultParm.getDouble("NPAY_MEDICAL_AMT2"),2)));//退款总金额
     	}
     	if(pakgeResultParm.getDouble("OUT_CASH2") == 0){
     		accountParm.setData("OUT_CASH2", df.format(StringTool.round(pakgeResultParm.getDouble("OUT_CASH2"),2)));//退款总金额
     	}else{
     		accountParm.setData("OUT_CASH2", df.format(StringTool.round(-pakgeResultParm.getDouble("OUT_CASH2"),2)));//退款总金额
     	}
     	if(pakgeResultParm.getDouble("OUT_B2") == 0){
     		accountParm.setData("OUT_B2", df.format(StringTool.round(pakgeResultParm.getDouble("OUT_B2"),2)));//退款总金额
     	}else{
     		accountParm.setData("OUT_B2", df.format(StringTool.round(-pakgeResultParm.getDouble("OUT_B2"),2)));//退款总金额
     	}
     	if(pakgeResultParm.getDouble("OUT_CARD2") == 0){
     		accountParm.setData("OUT_CARD2", df.format(StringTool.round(pakgeResultParm.getDouble("OUT_CARD2"),2)));//退款总金额
     	}else{
     		accountParm.setData("OUT_CARD2", df.format(StringTool.round(-pakgeResultParm.getDouble("OUT_CARD2"),2)));//退款总金额
     	}
     	if(pakgeResultParm.getDouble("OUT_LPK2") == 0){
     		accountParm.setData("OUT_LPK2", df.format(StringTool.round(pakgeResultParm.getDouble("OUT_LPK2"),2)));//退款总金额
     	}else{
     		accountParm.setData("OUT_LPK2", df.format(StringTool.round(-pakgeResultParm.getDouble("OUT_LPK2"),2)));//退款总金额
     	}
     	if(pakgeResultParm.getDouble("OUT_XJDYQ2") == 0){
     		accountParm.setData("OUT_XJDYQ2", df.format(StringTool.round(pakgeResultParm.getDouble("OUT_XJDYQ2"),2)));//退款总金额
     	}else{
     		accountParm.setData("OUT_XJDYQ2", df.format(StringTool.round(-pakgeResultParm.getDouble("OUT_XJDYQ2"),2)));//退款总金额
     	}
     	if(pakgeResultParm.getDouble("OUT_WX2") == 0){
     		accountParm.setData("OUT_WX2", df.format(StringTool.round(pakgeResultParm.getDouble("OUT_WX2"),2)));//退款总金额
     	}else{
     		accountParm.setData("OUT_WX2", df.format(StringTool.round(-pakgeResultParm.getDouble("OUT_WX2"),2)));//退款总金额
     	}
     	if(pakgeResultParm.getDouble("OUT_ZFB2") == 0){
     		accountParm.setData("OUT_ZFB2", df.format(StringTool.round(pakgeResultParm.getDouble("OUT_ZFB2"),2)));//退款总金额
     	}else{
     		accountParm.setData("OUT_ZFB2", df.format(StringTool.round(-pakgeResultParm.getDouble("OUT_ZFB2"),2)));//退款总金额
     	}
     	if(pakgeResultParm.getDouble("OUT_EKT2") == 0){
     		accountParm.setData("OUT_EKT2", df.format(StringTool.round(pakgeResultParm.getDouble("OUT_EKT2"),2)));//退款总金额
     	}else{
     		accountParm.setData("OUT_EKT2", df.format(StringTool.round(-pakgeResultParm.getDouble("OUT_EKT2"),2)));//退款总金额
     	}
     	if(pakgeResultParm.getDouble("OUT_C42") == 0){
     		accountParm.setData("OUT_C42", df.format(StringTool.round(pakgeResultParm.getDouble("OUT_C42"),2)));//退款总金额
     	}else{
     		accountParm.setData("OUT_C42", df.format(StringTool.round(-pakgeResultParm.getDouble("OUT_C42"),2)));//退款总金额
     	}
     	accountParm.setData("TOT_MEDICAL_AMT2", df.format(StringTool.round(pakgeResultParm.getDouble("PAY_MEDICAL_ATM_TOTAL2")+pakgeResultParm.getDouble("NPAY_MEDICAL_AMT2"),2)));//总金额
     	accountParm.setData("TOT_CASH2", df.format(StringTool.round(pakgeResultParm.getDouble("TOT_CASH2"),2)));//现金总金额
     	accountParm.setData("TOT_B2", df.format(StringTool.round(pakgeResultParm.getDouble("TOT_B2"),2)));//支票总金额
     	accountParm.setData("TOT_CARD2", df.format(StringTool.round(pakgeResultParm.getDouble("TOT_CARD2"),2)));//刷卡总金额
     	accountParm.setData("TOT_LPK2", df.format(StringTool.round(pakgeResultParm.getDouble("TOT_LPK2"),2)));//礼品卡总金额
     	accountParm.setData("TOT_XJDYQ2", df.format(StringTool.round(pakgeResultParm.getDouble("TOT_XJDYQ2"),2)));//现金抵用券总金额
     	accountParm.setData("TOT_WX2", df.format(StringTool.round(pakgeResultParm.getDouble("TOT_WX2"),2)));//微信总金额
     	accountParm.setData("TOT_ZFB2", df.format(StringTool.round(pakgeResultParm.getDouble("TOT_ZFB2"),2)));//支付宝总金额
     	accountParm.setData("TOT_EKT2", df.format(StringTool.round(pakgeResultParm.getDouble("TOT_EKT2"),2)));//医疗卡总金额
     	accountParm.setData("TOT_C42", df.format(StringTool.round(pakgeResultParm.getDouble("TOT_C42"),2)));
     	accountParm.setData("PAY_MEDICAL_QTY2", pakgeResultParm.getInt("PAY_MEDICAL_QTY2"));//收入总数量
     	accountParm.setData("NPAY_MEDICAL_QTY2", pakgeResultParm.getInt("NPAY_MEDICAL_QTY2"));//退款总数量
     	accountParm.setData("TOT_MEDICAL_QTY2", pakgeResultParm.getInt("PAY_MEDICAL_QTY2")+pakgeResultParm.getInt("NPAY_MEDICAL_QTY2"));//总数量
     	//礼品卡销售数据
     	accountParm.setData("IN_CASH3", df.format(StringTool.round(lpkResultParm.getDouble("IN_CASH3"),2)));
     	accountParm.setData("IN_B3", df.format(StringTool.round(lpkResultParm.getDouble("IN_B3"),2)));
     	accountParm.setData("IN_CARD3", df.format(StringTool.round(lpkResultParm.getDouble("IN_CARD3"),2)));
     	accountParm.setData("IN_LPK3", df.format(StringTool.round(lpkResultParm.getDouble("IN_LPK3"),2)));//礼品卡
     	accountParm.setData("IN_XJDYQ3", df.format(StringTool.round(lpkResultParm.getDouble("IN_XJDYQ3"),2)));//现金抵用券
     	accountParm.setData("IN_WX3", df.format(StringTool.round(lpkResultParm.getDouble("IN_WX3"),2)));//微信
     	accountParm.setData("IN_ZFB3", df.format(StringTool.round(lpkResultParm.getDouble("IN_ZFB3"),2)));//支付宝
     	accountParm.setData("IN_EKT3", df.format(StringTool.round(lpkResultParm.getDouble("IN_EKT3"),2)));//医疗卡
     	accountParm.setData("IN_C43", df.format(StringTool.round(lpkResultParm.getDouble("IN_C43"),2)));
     	accountParm.setData("OUT_XJDYQ3", df.format(StringTool.round(lpkResultParm.getDouble("OUT_XJDYQ3"),2)));//现金抵用券
     	accountParm.setData("PAY_MEDICAL_ATM_TOTAL3", df.format(StringTool.round(lpkResultParm.getDouble("PAY_MEDICAL_ATM_TOTAL3"),2)));//收入总金额
     	if(lpkResultParm.getDouble("NPAY_MEDICAL_AMT3") == 0){
     		accountParm.setData("NPAY_MEDICAL_AMT3", df.format(StringTool.round(lpkResultParm.getDouble("NPAY_MEDICAL_AMT3"),2)));//退款总金额	
     	}else{
     		accountParm.setData("NPAY_MEDICAL_AMT3", df.format(StringTool.round(-lpkResultParm.getDouble("NPAY_MEDICAL_AMT3"),2)));//退款总金额
     	}
     	if(lpkResultParm.getDouble("OUT_CASH3") == 0){
     		accountParm.setData("OUT_CASH3", df.format(StringTool.round(lpkResultParm.getDouble("OUT_CASH3"),2)));//退款总金额	
     	}else{
     		accountParm.setData("OUT_CASH3", df.format(StringTool.round(-lpkResultParm.getDouble("OUT_CASH3"),2)));//退款总金额
     	}
     	if(lpkResultParm.getDouble("OUT_B3") == 0){
     		accountParm.setData("OUT_B3", df.format(StringTool.round(lpkResultParm.getDouble("OUT_B3"),2)));//退款总金额	
     	}else{
     		accountParm.setData("OUT_B3", df.format(StringTool.round(-lpkResultParm.getDouble("OUT_B3"),2)));//退款总金额
     	}
     	if(lpkResultParm.getDouble("OUT_CARD3") == 0){
     		accountParm.setData("OUT_CARD3", df.format(StringTool.round(lpkResultParm.getDouble("OUT_CARD3"),2)));//退款总金额	
     	}else{
     		accountParm.setData("OUT_CARD3", df.format(StringTool.round(-lpkResultParm.getDouble("OUT_CARD3"),2)));//退款总金额
     	}
     	if(lpkResultParm.getDouble("OUT_LPK3") == 0){
     		accountParm.setData("OUT_LPK3", df.format(StringTool.round(lpkResultParm.getDouble("OUT_LPK3"),2)));//退款总金额	
     	}else{
     		accountParm.setData("OUT_LPK3", df.format(StringTool.round(-lpkResultParm.getDouble("OUT_LPK3"),2)));//退款总金额
     	}
     	if(lpkResultParm.getDouble("OUT_XJDYQ3") == 0){
     		accountParm.setData("OUT_XJDYQ3", df.format(StringTool.round(lpkResultParm.getDouble("OUT_XJDYQ3"),2)));//退款总金额	
     	}else{
     		accountParm.setData("OUT_XJDYQ3", df.format(StringTool.round(-lpkResultParm.getDouble("OUT_XJDYQ3"),2)));//退款总金额
     	}
     	if(lpkResultParm.getDouble("OUT_WX3") == 0){
     		accountParm.setData("OUT_WX3", df.format(StringTool.round(lpkResultParm.getDouble("OUT_WX3"),2)));//退款总金额	
     	}else{
     		accountParm.setData("OUT_WX3", df.format(StringTool.round(-lpkResultParm.getDouble("OUT_WX3"),2)));//退款总金额
     	}
     	if(lpkResultParm.getDouble("OUT_ZFB3") == 0){
     		accountParm.setData("OUT_ZFB3", df.format(StringTool.round(lpkResultParm.getDouble("OUT_ZFB3"),2)));//退款总金额	
     	}else{
     		accountParm.setData("OUT_ZFB3", df.format(StringTool.round(-lpkResultParm.getDouble("OUT_ZFB3"),2)));//退款总金额
     	}
     	if(lpkResultParm.getDouble("OUT_EKT3") == 0){
     		accountParm.setData("OUT_EKT3", df.format(StringTool.round(lpkResultParm.getDouble("OUT_EKT3"),2)));//退款总金额	
     	}else{
     		accountParm.setData("OUT_EKT3", df.format(StringTool.round(-lpkResultParm.getDouble("OUT_EKT3"),2)));//退款总金额
     	}
     	if(lpkResultParm.getDouble("OUT_C43") == 0){
     		accountParm.setData("OUT_C43", df.format(StringTool.round(lpkResultParm.getDouble("OUT_C43"),2)));//退款总金额	
     	}else{
     		accountParm.setData("OUT_C43", df.format(StringTool.round(-lpkResultParm.getDouble("OUT_C43"),2)));//退款总金额
     	}
     	
     	accountParm.setData("TOT_MEDICAL_AMT3", df.format(StringTool.round(lpkResultParm.getDouble("PAY_MEDICAL_ATM_TOTAL3")+lpkResultParm.getDouble("NPAY_MEDICAL_AMT3"),2)));//总金额
     	accountParm.setData("TOT_CASH3", df.format(StringTool.round(lpkResultParm.getDouble("TOT_CASH3"),2)));//现金总金额
     	accountParm.setData("TOT_B3", df.format(StringTool.round(lpkResultParm.getDouble("TOT_B3"),2)));//支票总金额
     	accountParm.setData("TOT_CARD3", df.format(StringTool.round(lpkResultParm.getDouble("TOT_CARD3"),2)));//刷卡总金额
     	accountParm.setData("TOT_LPK3", df.format(StringTool.round(lpkResultParm.getDouble("TOT_LPK3"),2)));//礼品卡总金额
     	accountParm.setData("TOT_XJDYQ3", df.format(StringTool.round(lpkResultParm.getDouble("TOT_XJDYQ3"),2)));//现金抵用券总金额
     	accountParm.setData("TOT_WX3", df.format(StringTool.round(lpkResultParm.getDouble("TOT_WX3"),2)));//微信总金额
     	accountParm.setData("TOT_ZFB3", df.format(StringTool.round(lpkResultParm.getDouble("TOT_ZFB3"),2)));//支付宝总金额
     	accountParm.setData("TOT_EKT3", df.format(StringTool.round(lpkResultParm.getDouble("TOT_EKT3"),2)));//医疗卡总金额
     	accountParm.setData("TOT_C43", df.format(StringTool.round(lpkResultParm.getDouble("TOT_C43"),2)));
     	accountParm.setData("PAY_MEDICAL_QTY3", lpkResultParm.getInt("PAY_MEDICAL_QTY3"));//收入总数量
     	accountParm.setData("NPAY_MEDICAL_QTY3", lpkResultParm.getInt("NPAY_MEDICAL_QTY3"));//退款总数量
     	accountParm.setData("TOT_MEDICAL_QTY3", lpkResultParm.getInt("PAY_MEDICAL_QTY3")+lpkResultParm.getInt("NPAY_MEDICAL_QTY3"));//总数量
     	
     	accountParm.setData("PAY_MEDICAL_QTY4",accountParm.getInt("PAY_MEDICAL_QTY")+accountParm.getInt("PAY_MEDICAL_QTY1")+accountParm.getInt("PAY_MEDICAL_QTY2")+accountParm.getInt("PAY_MEDICAL_QTY3"));
    	accountParm.setData("NPAY_MEDICAL_QTY4",accountParm.getInt("NPAY_MEDICAL_QTY")+accountParm.getInt("NPAY_MEDICAL_QTY1")+accountParm.getInt("NPAY_MEDICAL_QTY2")+accountParm.getInt("NPAY_MEDICAL_QTY3"));
    	accountParm.setData("TOT_MEDICAL_QTY4",accountParm.getInt("TOT_MEDICAL_QTY")+accountParm.getInt("TOT_MEDICAL_QTY1")+accountParm.getInt("TOT_MEDICAL_QTY2")+accountParm.getInt("TOT_MEDICAL_QTY3"));
    	accountParm.setData("PAY_MEDICAL_ATM_TOTAL4",df.format(StringTool.round(Double.valueOf(accountParm.getValue("PAY_MEDICAL_ATM_TOTAL"))+Double.valueOf(accountParm.getValue("PAY_MEDICAL_ATM_TOTAL1"))+Double.valueOf(accountParm.getValue("PAY_MEDICAL_ATM_TOTAL2"))+Double.valueOf(accountParm.getValue("PAY_MEDICAL_ATM_TOTAL3")),2)));
    	accountParm.setData("NPAY_MEDICAL_AMT4",df.format(StringTool.round(Double.valueOf(accountParm.getValue("NPAY_MEDICAL_AMT"))+Double.valueOf(accountParm.getValue("NPAY_MEDICAL_AMT1"))+Double.valueOf(accountParm.getValue("NPAY_MEDICAL_AMT2"))+Double.valueOf(accountParm.getValue("NPAY_MEDICAL_AMT3")),2)));
    	accountParm.setData("TOT_MEDICAL_AMT4",df.format(StringTool.round(Double.valueOf(accountParm.getValue("TOT_MEDICAL_AMT"))+Double.valueOf(accountParm.getValue("TOT_MEDICAL_AMT1"))+Double.valueOf(accountParm.getValue("TOT_MEDICAL_AMT2"))+Double.valueOf(accountParm.getValue("TOT_MEDICAL_AMT3")),2)));
    	accountParm.setData("IN_CASH4",df.format(StringTool.round(Double.valueOf(accountParm.getValue("IN_CASH"))+Double.valueOf(accountParm.getValue("IN_CASH1"))+Double.valueOf(accountParm.getValue("IN_CASH2"))+Double.valueOf(accountParm.getValue("IN_CASH3")),2)));
    	accountParm.setData("OUT_CASH4",df.format(StringTool.round(Double.valueOf(accountParm.getValue("OUT_CASH"))+Double.valueOf(accountParm.getValue("OUT_CASH1"))+Double.valueOf(accountParm.getValue("OUT_CASH2"))+Double.valueOf(accountParm.getValue("OUT_CASH3")),2)));
    	accountParm.setData("TOT_CASH4",df.format(StringTool.round(Double.valueOf(accountParm.getValue("TOT_CASH"))+Double.valueOf(accountParm.getValue("TOT_CASH1"))+Double.valueOf(accountParm.getValue("TOT_CASH2"))+Double.valueOf(accountParm.getValue("TOT_CASH3")),2)));
    	accountParm.setData("IN_B4",df.format(StringTool.round(Double.valueOf(accountParm.getValue("IN_B"))+Double.valueOf(accountParm.getValue("IN_B1"))+Double.valueOf(accountParm.getValue("IN_B2"))+Double.valueOf(accountParm.getValue("IN_B3")),2)));
    	accountParm.setData("OUT_B4",df.format(StringTool.round(Double.valueOf(accountParm.getValue("OUT_B"))+Double.valueOf(accountParm.getValue("OUT_B1"))+Double.valueOf(accountParm.getValue("OUT_B2"))+Double.valueOf(accountParm.getValue("OUT_B3")),2)));
    	accountParm.setData("TOT_B4",df.format(StringTool.round(Double.valueOf(accountParm.getValue("TOT_B"))+Double.valueOf(accountParm.getValue("TOT_B1"))+Double.valueOf(accountParm.getValue("TOT_B2"))+Double.valueOf(accountParm.getValue("TOT_B3")),2)));
    	accountParm.setData("IN_CARD4",df.format(StringTool.round(Double.valueOf(accountParm.getValue("IN_CARD"))+Double.valueOf(accountParm.getValue("IN_CARD1"))+Double.valueOf(accountParm.getValue("IN_CARD2"))+Double.valueOf(accountParm.getValue("IN_CARD3")),2)));
    	accountParm.setData("OUT_CARD4",df.format(StringTool.round(Double.valueOf(accountParm.getValue("OUT_CARD"))+Double.valueOf(accountParm.getValue("OUT_CARD1"))+Double.valueOf(accountParm.getValue("OUT_CARD2"))+Double.valueOf(accountParm.getValue("OUT_CARD3")),2)));
    	accountParm.setData("TOT_CARD4",df.format(StringTool.round(Double.valueOf(accountParm.getValue("TOT_CARD"))+Double.valueOf(accountParm.getValue("TOT_CARD1"))+Double.valueOf(accountParm.getValue("TOT_CARD2"))+Double.valueOf(accountParm.getValue("TOT_CARD3")),2)));
    	accountParm.setData("IN_LPK4",df.format(StringTool.round(Double.valueOf(accountParm.getValue("IN_LPK"))+Double.valueOf(accountParm.getValue("IN_LPK1"))+Double.valueOf(accountParm.getValue("IN_LPK2"))+Double.valueOf(accountParm.getValue("IN_LPK3")),2)));
    	accountParm.setData("OUT_LPK4",df.format(StringTool.round(Double.valueOf(accountParm.getValue("OUT_LPK"))+Double.valueOf(accountParm.getValue("OUT_LPK1"))+Double.valueOf(accountParm.getValue("OUT_LPK2"))+Double.valueOf(accountParm.getValue("OUT_LPK3")),2)));
    	accountParm.setData("TOT_LPK4",df.format(StringTool.round(Double.valueOf(accountParm.getValue("TOT_LPK"))+Double.valueOf(accountParm.getValue("TOT_LPK1"))+Double.valueOf(accountParm.getValue("TOT_LPK2"))+Double.valueOf(accountParm.getValue("TOT_LPK3")),2)));
    	accountParm.setData("IN_XJDYQ4",df.format(StringTool.round(Double.valueOf(accountParm.getValue("IN_XJDYQ"))+Double.valueOf(accountParm.getValue("IN_XJDYQ1"))+Double.valueOf(accountParm.getValue("IN_XJDYQ2"))+Double.valueOf(accountParm.getValue("IN_XJDYQ3")),2)));
    	accountParm.setData("OUT_XJDYQ4",df.format(StringTool.round(Double.valueOf(accountParm.getValue("OUT_XJDYQ"))+Double.valueOf(accountParm.getValue("OUT_XJDYQ1"))+Double.valueOf(accountParm.getValue("OUT_XJDYQ2"))+Double.valueOf(accountParm.getValue("OUT_XJDYQ3")),2)));
    	accountParm.setData("TOT_XJDYQ4",df.format(StringTool.round(Double.valueOf(accountParm.getValue("TOT_XJDYQ"))+Double.valueOf(accountParm.getValue("TOT_XJDYQ1"))+Double.valueOf(accountParm.getValue("TOT_XJDYQ2"))+Double.valueOf(accountParm.getValue("TOT_XJDYQ3")),2)));
    	accountParm.setData("IN_WX4",df.format(StringTool.round(Double.valueOf(accountParm.getDouble("IN_WX"))+Double.valueOf(accountParm.getDouble("IN_WX1"))+Double.valueOf(accountParm.getDouble("IN_WX2"))+Double.valueOf(accountParm.getDouble("IN_WX3")),2)));
    	accountParm.setData("OUT_WX4",df.format(StringTool.round(Double.valueOf(accountParm.getValue("OUT_WX"))+Double.valueOf(accountParm.getValue("OUT_WX1"))+Double.valueOf(accountParm.getValue("OUT_WX2"))+Double.valueOf(accountParm.getValue("OUT_WX3")),2)));
    	accountParm.setData("TOT_WX4",df.format(StringTool.round(Double.valueOf(accountParm.getValue("TOT_WX"))+Double.valueOf(accountParm.getValue("TOT_WX1"))+Double.valueOf(accountParm.getValue("TOT_WX2"))+Double.valueOf(accountParm.getValue("TOT_WX3")),2)));
    	accountParm.setData("IN_ZFB4",df.format(StringTool.round(Double.valueOf(accountParm.getValue("IN_ZFB"))+Double.valueOf(accountParm.getValue("IN_ZFB1"))+Double.valueOf(accountParm.getValue("IN_ZFB2"))+Double.valueOf(accountParm.getValue("IN_ZFB3")),2)));
    	accountParm.setData("OUT_ZFB4",df.format(StringTool.round(Double.valueOf(accountParm.getValue("OUT_ZFB"))+Double.valueOf(accountParm.getValue("OUT_ZFB1"))+Double.valueOf(accountParm.getValue("OUT_ZFB2"))+Double.valueOf(accountParm.getValue("OUT_ZFB3")),2)));
    	accountParm.setData("TOT_ZFB4",df.format(StringTool.round(Double.valueOf(accountParm.getValue("TOT_ZFB"))+Double.valueOf(accountParm.getValue("TOT_ZFB1"))+Double.valueOf(accountParm.getValue("TOT_ZFB2"))+Double.valueOf(accountParm.getValue("TOT_ZFB3")),2)));
    	accountParm.setData("IN_EKT4",df.format(StringTool.round(Double.valueOf(accountParm.getValue("IN_EKT"))+Double.valueOf(accountParm.getValue("IN_EKT1"))+Double.valueOf(accountParm.getValue("IN_EKT2"))+Double.valueOf(accountParm.getValue("IN_EKT3")),2)));
    	accountParm.setData("OUT_EKT4",df.format(StringTool.round(Double.valueOf(accountParm.getValue("OUT_EKT"))+Double.valueOf(accountParm.getValue("OUT_EKT1"))+Double.valueOf(accountParm.getValue("OUT_EKT2"))+Double.valueOf(accountParm.getValue("OUT_EKT3")),2)));
    	accountParm.setData("TOT_EKT4",df.format(StringTool.round(Double.valueOf(accountParm.getValue("TOT_EKT"))+Double.valueOf(accountParm.getValue("TOT_EKT1"))+Double.valueOf(accountParm.getValue("TOT_EKT2"))+Double.valueOf(accountParm.getValue("TOT_EKT3")),2)));
    	accountParm.setData("IN_C44",df.format(StringTool.round(Double.valueOf(accountParm.getValue("IN_C4"))+Double.valueOf(accountParm.getValue("IN_C41"))+Double.valueOf(accountParm.getValue("IN_C42"))+Double.valueOf(accountParm.getValue("IN_C43")),2)));
    	accountParm.setData("OUT_C44",df.format(StringTool.round(Double.valueOf(accountParm.getValue("OUT_C4"))+Double.valueOf(accountParm.getValue("OUT_C41"))+Double.valueOf(accountParm.getValue("OUT_C42"))+Double.valueOf(accountParm.getValue("OUT_C43")),2)));
    	accountParm.setData("TOT_C44",df.format(StringTool.round(Double.valueOf(accountParm.getValue("TOT_C4"))+Double.valueOf(accountParm.getValue("TOT_C41"))+Double.valueOf(accountParm.getValue("TOT_C42"))+Double.valueOf(accountParm.getValue("TOT_C43")),2)));
    	
    	accountParm.setData("TOT_TOT", df.format(StringTool.round(accountParm.getDouble("TOT_MEDICAL_AMT") + 
    			accountParm.getDouble("TOT_MEDICAL_AMT1")
    			+accountParm.getDouble("TOT_MEDICAL_AMT2")+accountParm.getDouble("TOT_MEDICAL_AMT3"),2)));
    	String ar_amt_word = StringUtil.getInstance().numberToWord(accountParm.getDouble("TOT_TOT"));
    	if(ar_amt_word.lastIndexOf("分")>0){
    		ar_amt_word=ar_amt_word.substring(0,ar_amt_word.lastIndexOf("分")+1);
    	}
    	accountParm.setData("AR_AMT_WORD", ar_amt_word);
    	data.setData("ACCOUNT", accountParm.getData());
//    	System.out.println("+++++++accountParm accountParm is ::"+accountParm);
    	this.openPrintWindow("%ROOT%\\config\\prt\\EKT\\EKTAccountDaily.jhw",data);

    }
    
    /**
     * 打印
     */
    public void onPrint() {
		String sysDate = StringTool.getString(SystemTool.getInstance()
				.getDate(), "yyyy/MM/dd HH:mm:ss");
        //打印每一个日结数据
        if ("N".equals(getValue("TOGEDER_FLG"))) {
           String [] accoutSeqs= accountSeq.split(",");
            for(int i=0;i<accoutSeqs.length;i++){
            	getPrintValue(accoutSeqs[i],sysDate);
            }
        } else {
            //将日结数据汇总
        	getPrintValue(accountSeq,sysDate);
        }
       



    }
    
    /**
     * 打印数据
     */
    public void getPrintValue(String accoutSeq,String sysDate){
    	String sql = "SELECT ACCOUNT_USER,ACCOUNT_DATE,BUY_QTY,CHANGE_QTY,ADD_QTY,SENT_COUNT,FACTORAGE_QTY,FACTORAGE_AMT," +
    			"PAY_MEDICAL_ATM,PAY_MEDICAL_QTY,NPAY_MEDICAL_AMT,NPAY_MEDICAL_QTY,REGION_CODE," +
    			"AR_AMT FROM EKT_ACCOUNT WHERE ACCOUNT_SEQ IN (" + accoutSeq + ")";
//    	System.out.println("打印数据 打印数据 打印数据 is ::"+sql);
    	TParm result = new TParm(TJDODBTool.getInstance().select(sql));
    	if(result.getErrCode()<0){
    		this.messageBox("请选择要打印的记录");
    		return;
    	}
    	//zhangp 20120131 合并
    	int count = 1;
    	if("Y".equals(getValue("TOGEDER_FLG"))){
    		String[] accoutSeqs = accoutSeq.split(",");
    		count = accoutSeqs.length;
    	}
    	int change_qty = 0 ;//换卡数量
    	int npay_medical_qty = 0;//退费数量
    	int sent_count = 0;//发卡总数
    	int factorage_qty = 0;//手续总数
    	int add_qty = 0;//充值数量
    	int buy_qty = 0;//购卡数量
    	int pay_medical_qty = 0;//充值数量
    	double npay_medical_amt = 0.00;//退费金额
    	double factorage_amt = 0.00;//手续费
    	double ar_amt = 0.00;//总金额
    	double pay_medical_atm = 0.00;//充值金额总计
    	String account_user = "";
    	String account_date = "";
    	String account_date1 = "";
    	for (int i = 0; i < count; i++) {
    		change_qty = change_qty + result.getInt("CHANGE_QTY", i);
        	npay_medical_qty = npay_medical_qty + result.getInt("NPAY_MEDICAL_QTY",i);
        	sent_count = sent_count + result.getInt("SENT_COUNT",i);
        	factorage_qty = factorage_qty + result.getInt("FACTORAGE_QTY",i);
        	add_qty = add_qty + result.getInt("ADD_QTY",i);
        	buy_qty = buy_qty + result.getInt("BUY_QTY",i);
        	pay_medical_qty = pay_medical_qty + result.getInt("PAY_MEDICAL_QTY",i);
        	npay_medical_amt = npay_medical_amt + result.getDouble("NPAY_MEDICAL_AMT",i);
        	factorage_amt = factorage_amt + result.getDouble("FACTORAGE_AMT",i);
        	ar_amt = ar_amt + result.getDouble("AR_AMT",i);
        	pay_medical_atm = pay_medical_atm + result.getDouble("PAY_MEDICAL_ATM",i);
        	if(i!=0){
        		account_user = account_user +","+ result.getData("ACCOUNT_USER", i).toString();
        		account_date = account_date + "," + result.getData("ACCOUNT_DATE", i).toString().substring(0, 4)+"/"+
            	result.getData("ACCOUNT_DATE", i).toString().substring(5, 7)+"/"+
            	result.getData("ACCOUNT_DATE", i).toString().substring(8, 10);
        	}else{
        		account_user = result.getData("ACCOUNT_USER", i).toString();
        		account_date = result.getData("ACCOUNT_DATE", i).toString().substring(0, 4)+"/"+
            	result.getData("ACCOUNT_DATE", i).toString().substring(5, 7)+"/"+
            	result.getData("ACCOUNT_DATE", i).toString().substring(8, 10);
        		account_date1 = result.getData("ACCOUNT_DATE", i).toString().substring(0,19).replace('-', '/');
        	}
		}
    	TTable table = ((TTable)this.getComponent("Table"));
    	String region = table.getParmValue().getRow(0).getValue("REGION_CHN_DESC");
    	String billsql =
    		"SELECT BUSINESS_DATE,BUSINESS_NO,CASHIER_CODE FROM EKT_ACCNTDETAIL  WHERE ACCOUNT_SEQ IN ("+accoutSeq+") " +
    				"AND CHARGE_FLG IN ('3','4','5','7','8')   " +//(1,扣款;2,退款;3,医疗卡充值,4,制卡,5,补卡,7,退费,8,换卡)
    				"UNION (SELECT ACCOUNT_DATE AS BUSINESS_DATE,TRADE_NO AS BUSINESS_NO,ACCOUNT_USER FROM MEM_TRADE WHERE ACCOUNT_SEQ IN ("+accoutSeq+") ) " +
    				"UNION (SELECT ACCOUNT_DATE AS BUSINESS_DATE,TRADE_NO AS BUSINESS_NO,ACCOUNT_USER FROM MEM_PACKAGE_TRADE_M WHERE ACCOUNT_SEQ IN ("+accoutSeq+") )" +
    				"UNION (SELECT ACCOUNT_DATE AS BUSINESS_DATE,TRADE_NO AS BUSINESS_NO,ACCOUNT_USER FROM MEM_GIFTCARD_TRADE_M WHERE ACCOUNT_SEQ IN ("+accoutSeq+") )ORDER BY BUSINESS_DATE,BUSINESS_NO  ";
//    	System.out.println("1111billsql billsql billsql is ::"+billsql);
    	TParm billParm = new TParm(TJDODBTool.getInstance().select(billsql));
    	billsql = 
    		"SELECT B.USER_NAME AS CASHIER_CODE FROM EKT_ACCNTDETAIL A,SYS_OPERATOR B  WHERE A.CASHIER_CODE = B.USER_ID AND A.ACCOUNT_SEQ IN ("+accoutSeq+") AND A.CHARGE_FLG IN ('3','4','5','7','8') GROUP BY B.USER_NAME " +//(1:购卡,2:换卡,3:补卡,4:充值,5:扣款,6:退费)
    				"UNION (SELECT B.USER_NAME AS CASHIER_CODE FROM MEM_TRADE A,SYS_OPERATOR B  WHERE A.ACCOUNT_USER = B.USER_ID AND A.ACCOUNT_SEQ IN ("+accoutSeq+") GROUP BY B.USER_NAME )" +
    				"UNION (SELECT B.USER_NAME AS CASHIER_CODE FROM MEM_PACKAGE_TRADE_M A,SYS_OPERATOR B  WHERE A.ACCOUNT_USER = B.USER_ID AND A.ACCOUNT_SEQ IN ("+accoutSeq+") GROUP BY B.USER_NAME ) " +
    				"UNION (SELECT B.USER_NAME AS CASHIER_CODE FROM MEM_GIFTCARD_TRADE_M A,SYS_OPERATOR B  WHERE A.ACCOUNT_USER = B.USER_ID AND A.ACCOUNT_SEQ IN ("+accoutSeq+") GROUP BY B.USER_NAME   ) ORDER BY CASHIER_CODE";
//    	System.out.println("2222billsql billsql billsql is ::"+billsql);
    	TParm billUserParm = new TParm(TJDODBTool.getInstance().select(billsql));
    	billsql = 
    		//===zhangp 201203 start
    		"SELECT SUM(AMT) AMT ,SUM(PROCEDURE_AMT) PROCEDURE_AMT,A.GATHER_TYPE,A.ACCNT_TYPE FROM EKT_BIL_PAY A,EKT_ACCNTDETAIL B "+
    		" WHERE A.BIL_BUSINESS_NO = B.BUSINESS_NO AND B.ACCOUNT_SEQ IN ("+accoutSeq+") GROUP BY A.GATHER_TYPE,A.ACCNT_TYPE ORDER BY A.ACCNT_TYPE";
    	//===zhangp 201203 end
//    	"SELECT SUM(AMT+PROCEDURE_AMT) AMT,A.GATHER_TYPE,A.ACCNT_TYPE FROM EKT_BIL_PAY A,EKT_ACCNTDETAIL B "+
//    	" WHERE A.BIL_BUSINESS_NO = B.BUSINESS_NO AND B.ACCOUNT_SEQ IN ("+accoutSeq+") GROUP BY A.GATHER_TYPE,A.ACCNT_TYPE ORDER BY A.ACCNT_TYPE";
//    	System.out.println("3333billsql billsql billsql is ::"+billsql);
    	TParm gatherParm = new TParm(TJDODBTool.getInstance().select(billsql));
    	
    	double inCash = 0.00;//现金收入
    	double inB = 0.00;//支票收入
    	double inCard = 0.00;//刷卡收入
    	double inlpk = 0.00;//礼品卡收入
    	double inXjdyq = 0.00;//现金抵用券收入
    	double inWx = 0.00;//微信收入
    	double inZfb = 0.00;//支付宝收入
    	double inEkt = 0.00;//医疗卡收入
    	double inC4 = 0.00;//医院垫付收入
    	double outCash = 0.00;//现金退费
    	double outB = 0.00;//支票退费
    	double outCard = 0.00;//刷卡退费
    	double outLpk = 0.00;//礼品卡退费
    	double outXjdyq = 0.00;//现金抵用券退费
    	double outWx = 0.00;//微信退费
    	double outZfb = 0.00;//支付宝退费
    	double outEkt = 0.00;//医疗卡退费
    	double outC4 = 0.00;//医院垫付退费
    	for (int i = 0; i < gatherParm.getCount(); i++) {
			if(gatherParm.getData("ACCNT_TYPE",i).equals("2")){//(1:购卡,2:换卡,3:补卡,4:充值,5:扣款,6:退费)
				if(gatherParm.getData("GATHER_TYPE",i).equals("C0")){//C0现金，写死
					inCash+= gatherParm.getDouble("AMT", i);
				}else
					if(gatherParm.getData("GATHER_TYPE",i).equals("C1")){//C1银行卡，写死
						inCard+= gatherParm.getDouble("AMT", i);
					}else
						if(gatherParm.getData("GATHER_TYPE",i).equals("T0")){//T0支票，写死
							inB+= gatherParm.getDouble("AMT", i);
						}else
							if(gatherParm.getData("GATHER_TYPE",i).equals("LPK")){//LPK礼品卡，写死
								inlpk+=gatherParm.getDouble("AMT", i);
							}else 
								if(gatherParm.getData("GATHER_TYPE",i).equals("XJZKQ")){//XJZKQ现金抵用券，写死
									inXjdyq+=gatherParm.getDouble("AMT", i);
								}else 
									if(gatherParm.getData("GATHER_TYPE",i).equals("WX")){//WX微信 ，写死
										inWx+=gatherParm.getDouble("AMT", i);
									}else 
										if(gatherParm.getData("GATHER_TYPE",i).equals("ZFB")){//ZFB支付宝，写死
											inZfb+=gatherParm.getDouble("AMT", i);
										}else 
											if(gatherParm.getData("GATHER_TYPE",i).equals("EKT")){//EKT医疗卡，写死
												inEkt+=gatherParm.getDouble("AMT", i);
											}else 
												if(gatherParm.getData("GATHER_TYPE",i).equals("C4")){// c4医院垫付，写死
													inC4+=gatherParm.getDouble("AMT", i);
												}
			}
			if(gatherParm.getData("ACCNT_TYPE",i).equals("4")){//(1:购卡,2:换卡,3:补卡,4:充值,5:扣款,6:退费)
				if(gatherParm.getData("GATHER_TYPE",i).equals("C0")){//C0现金，写死
					inCash+= gatherParm.getDouble("AMT", i);
				}else
					if(gatherParm.getData("GATHER_TYPE",i).equals("C1")){//C1银行卡，写死
						inCard+= gatherParm.getDouble("AMT", i);
					}else
						if(gatherParm.getData("GATHER_TYPE",i).equals("T0")){//T0支票，写死
							inB+= gatherParm.getDouble("AMT", i);
						}else
							if(gatherParm.getData("GATHER_TYPE",i).equals("LPK")){//LPK礼品卡，写死
								inlpk+=gatherParm.getDouble("AMT", i);
							}else 
								if(gatherParm.getData("GATHER_TYPE",i).equals("XJZKQ")){//XJZKQ现金抵用券，写死
									inXjdyq+=gatherParm.getDouble("AMT", i);
								}else 
									if(gatherParm.getData("GATHER_TYPE",i).equals("WX")){//WX微信，写死
										inWx+=gatherParm.getDouble("AMT", i);
									}else 
										if(gatherParm.getData("GATHER_TYPE",i).equals("ZFB")){//ZFB支付宝，写死
											inZfb+=gatherParm.getDouble("AMT", i);
										}else 
											if(gatherParm.getData("GATHER_TYPE",i).equals("EKT")){//EKT医疗卡，写死
												inEkt+=gatherParm.getDouble("AMT", i);
											}else 
												if(gatherParm.getData("GATHER_TYPE",i).equals("C4")){// c4医院垫付，写死
													inC4+=gatherParm.getDouble("AMT", i);
												}
			}
			if(gatherParm.getData("ACCNT_TYPE",i).equals("6")){//(1:购卡,2:换卡,3:补卡,4:充值,5:扣款,6:退费)
				if(gatherParm.getData("GATHER_TYPE",i).equals("C0")){//C0现金，写死
					outCash+= gatherParm.getDouble("AMT", i);
				}else
					if(gatherParm.getData("GATHER_TYPE",i).equals("C1")){//C1银行卡，写死
						outCard+= gatherParm.getDouble("AMT", i);
					}else
						if(gatherParm.getData("GATHER_TYPE",i).equals("T0")){//T0支票，写死
							outB+= gatherParm.getDouble("AMT", i);
						}else
							if(gatherParm.getData("GATHER_TYPE",i).equals("LPK")){//LPK礼品卡，写死
								outLpk+=gatherParm.getDouble("AMT", i);
							}else 
								if(gatherParm.getData("GATHER_TYPE",i).equals("XJZKQ")){//XJZKQ现金抵用券，写死
									outXjdyq+=gatherParm.getDouble("AMT", i);
								}else 
									if(gatherParm.getData("GATHER_TYPE",i).equals("WX")){//WX微信，写死
										outWx=gatherParm.getDouble("AMT", i);
									}else 
										if(gatherParm.getData("GATHER_TYPE",i).equals("ZFB")){//ZFB支付宝，写死
											outZfb+=gatherParm.getDouble("AMT", i);
										}else 
											if(gatherParm.getData("GATHER_TYPE",i).equals("EKT")){//EKT医疗卡，写死
												outEkt+=gatherParm.getDouble("AMT", i);
											}else 
												if(gatherParm.getData("GATHER_TYPE",i).equals("C4")){// c4医院垫付，写死
													outC4+=gatherParm.getDouble("AMT", i);
												}
			}
		}
    	double totCash = inCash -outCash;//现金
    	double totB = inB - outB;//支票
    	double totCard = inCard - outCard;//刷卡
    	double totLpk = inlpk - outLpk;//礼品卡
    	double totXjdyq = inXjdyq - outXjdyq;//现金抵用券
    	double totWx = inWx - outWx;//微信
    	double totZfb = inZfb - outZfb;//支付宝
    	double totEkt = inEkt - outEkt; //医疗卡
    	double totC4 = inC4 - outC4; //医院垫付
    	String billUser = "";
    	if(billUserParm.getCount()<0){
    		messageBox("未找到日结人员");
    		return;
    	}
    	for (int i = 0; i < billUserParm.getCount(); i++) {
    		billUser+= ","+ billUserParm.getData("CASHIER_CODE", i);
		}
    	billUser = billUser.substring(1, billUser.length());
    	String date = getAccntDate(billParm.getData("BUSINESS_DATE", 0).toString(),billParm.getData("BUSINESS_DATE", billParm.getCount()-1).toString());
    	String billNo = billParm.getData("BUSINESS_NO", 0) + " ~ " + billParm.getData("BUSINESS_NO", billParm.getCount()-1);
    	DecimalFormat df = new DecimalFormat("########0.00");
    	TParm data = new TParm();
    	if ("N".equals(getValue("TOGEDER_FLG"))) {
	    	data.setData("TITLE1", "TEXT", (region != null && !region.equals("") ? region :
	            Operator.getHospitalCHNShortName())+"门诊预收款日结");
    	}else{
    		data.setData("TITLE1", "TEXT", (region != null && !region.equals("") ? region :
	            Operator.getHospitalCHNShortName())+"门诊预收款日结(汇总)");
    	}
//    	String ar_amt_word = StringUtil.getInstance().numberToWord(pay_medical_atm-npay_medical_amt + factorage_amt);
//    	if(ar_amt_word.lastIndexOf("分")>0){
//    		ar_amt_word=ar_amt_word.substring(0,ar_amt_word.lastIndexOf("分")+1);
//    	}
    	data.setData("END_DATE", "TEXT", account_date);
//    	data.setData("FACTORAGE_QTY", "TEXT", factorage_qty);
//    	data.setData("PAY_MEDICAL_ATM", "TEXT", df.format(StringTool.round(pay_medical_atm,2)));
//    	data.setData("NPAY_MEDICAL_AMT_TOTAL", "TEXT", df.format(StringTool.round(npay_medical_amt,2)));
//    	data.setData("TOT_AMT", "TEXT", df.format(StringTool.round(totCard,2)));
    	TParm accountParm = new TParm();
    	
    	//根据accountSeq查询日结的日期
    	String accountDateSql = "SELECT ACCOUNT_DATE FROM EKT_ACCOUNT WHERE ACCOUNT_SEQ IN ("+accoutSeq+") ORDER BY ACCOUNT_DATE DESC";
//    	System.out.println("accountDateSql accountDateSql is ::"+accountDateSql);
    	TParm  accountDateParm = new TParm(TJDODBTool.getInstance().select(accountDateSql));
    	for(int j = 0;j< accountDateParm.getCount();j++){
    		String accountDate = accountDateParm.getValue("ACCOUNT_DATE", j).toString().substring(0, 19)
            .replace("-", "").replace("/", "").replace(" ", "").replace(":", "");
            TParm memResultParm = this.onPrintMemDate(accoutSeq);//会员卡
            TParm lpkResultParm =this.onPrintLpkDate(accoutSeq);//礼品卡
            TParm pakgeResultParm =this.onPrintPakgeDate(accoutSeq);//套餐
          //会员卡数据
        	accountParm.setData("IN_CASH1", df.format(StringTool.round(memResultParm.getDouble("IN_CASH1"),2)));
        	accountParm.setData("IN_B1", df.format(StringTool.round(memResultParm.getDouble("IN_B1"),2)));
        	accountParm.setData("IN_CARD1", df.format(StringTool.round(memResultParm.getDouble("IN_CARD1"),2)));
        	accountParm.setData("IN_LPK1", df.format(StringTool.round(memResultParm.getDouble("IN_LPK1"),2)));//礼品卡
        	accountParm.setData("IN_XJDYQ1", df.format(StringTool.round(memResultParm.getDouble("IN_XJDYQ1"),2)));//现金抵用券
        	accountParm.setData("IN_WX1", df.format(StringTool.round(memResultParm.getDouble("IN_WX1"),2)));//微信
        	accountParm.setData("IN_ZFB1", df.format(StringTool.round(memResultParm.getDouble("IN_ZFB1"),2)));//支付宝
        	accountParm.setData("IN_C41", df.format(StringTool.round(memResultParm.getDouble("IN_C41"),2)));//医院垫付
        	accountParm.setData("IN_EKT1", df.format(StringTool.round(memResultParm.getDouble("IN_EKT1"),2)));//医疗卡
        	if(memResultParm.getDouble("OUT_CASH1") == 0){
        		accountParm.setData("OUT_CASH1", df.format(StringTool.round(memResultParm.getDouble("OUT_CASH1"),2)));//退款总金额
            		
        	}else{
        		accountParm.setData("OUT_CASH1", df.format(StringTool.round(-memResultParm.getDouble("OUT_CASH1"),2)));//退款总金额
            	
        	}
        	if(memResultParm.getDouble("OUT_CARD1") == 0){
        		accountParm.setData("OUT_CARD1", df.format(StringTool.round(memResultParm.getDouble("OUT_CARD1"),2)));//退款总金额
            		
        	}else{
        		accountParm.setData("OUT_CARD1", df.format(StringTool.round(-memResultParm.getDouble("OUT_CARD1"),2)));//退款总金额
            	
        	}
        	if(memResultParm.getDouble("OUT_B1") == 0){
        		accountParm.setData("OUT_B1", df.format(StringTool.round(memResultParm.getDouble("OUT_B1"),2)));//退款总金额
            		
        	}else{
        		accountParm.setData("OUT_B1", df.format(StringTool.round(-memResultParm.getDouble("OUT_B1"),2)));//退款总金额
            	
        	}
        	if(memResultParm.getDouble("OUT_LPK1") == 0){
        		accountParm.setData("OUT_LPK1", df.format(StringTool.round(memResultParm.getDouble("OUT_LPK1"),2)));//退款总金额
            		
        	}else{
        		accountParm.setData("OUT_LPK1", df.format(StringTool.round(-memResultParm.getDouble("OUT_LPK1"),2)));//退款总金额
            	
        	}
        	if(memResultParm.getDouble("OUT_XJDYQ1") == 0){
        		accountParm.setData("OUT_XJDYQ1", df.format(StringTool.round(memResultParm.getDouble("OUT_XJDYQ1"),2)));//退款总金额
            		
        	}else{
        		accountParm.setData("OUT_XJDYQ1", df.format(StringTool.round(-memResultParm.getDouble("OUT_XJDYQ1"),2)));//退款总金额
            	
        	}
        	if(memResultParm.getDouble("OUT_WX1") == 0){
        		accountParm.setData("OUT_WX1", df.format(StringTool.round(memResultParm.getDouble("OUT_WX1"),2)));//退款总金额
            		
        	}else{
        		accountParm.setData("OUT_WX1", df.format(StringTool.round(-memResultParm.getDouble("OUT_WX1"),2)));//退款总金额
            	
        	}
        	if(memResultParm.getDouble("OUT_ZFB1") == 0){
        		accountParm.setData("OUT_ZFB1", df.format(StringTool.round(memResultParm.getDouble("OUT_ZFB1"),2)));//退款总金额
            		
        	}else{
        		accountParm.setData("OUT_ZFB1", df.format(StringTool.round(-memResultParm.getDouble("OUT_ZFB1"),2)));//退款总金额
            	
        	}
        	if(memResultParm.getDouble("OUT_EKT1") == 0){
        		accountParm.setData("OUT_EKT1", df.format(StringTool.round(memResultParm.getDouble("OUT_EKT1"),2)));//退款总金额
            		
        	}else{
        		accountParm.setData("OUT_EKT1", df.format(StringTool.round(-memResultParm.getDouble("OUT_EKT1"),2)));//退款总金额
            	
        	}
        	if(memResultParm.getDouble("OUT_C41") == 0){
        		accountParm.setData("OUT_C41", df.format(StringTool.round(memResultParm.getDouble("OUT_C41"),2)));//退款总金额
            		
        	}else{
        		accountParm.setData("OUT_C41", df.format(StringTool.round(-memResultParm.getDouble("OUT_C41"),2)));//退款总金额
            	
        	}
        	
//        	accountParm.setData("OUT_CASH1", df.format(StringTool.round(memResultParm.getDouble("OUT_CASH1"),2)));
//        	accountParm.setData("OUT_B1", df.format(StringTool.round(memResultParm.getDouble("OUT_B1"),2)));
//        	accountParm.setData("OUT_CARD1", df.format(StringTool.round(memResultParm.getDouble("OUT_CARD1"),2)));
//        	accountParm.setData("OUT_LPK1", df.format(StringTool.round(memResultParm.getDouble("OUT_LPK1"),2)));//礼品卡
//        	accountParm.setData("OUT_XJDYQ1", df.format(StringTool.round(memResultParm.getDouble("OUT_XJDYQ1"),2)));//现金抵用券
        	accountParm.setData("PAY_MEDICAL_ATM_TOTAL1", df.format(StringTool.round(memResultParm.getDouble("PAY_MEDICAL_ATM_TOTAL1"),2)));//收入总金额
        	if(memResultParm.getDouble("NPAY_MEDICAL_AMT1") == 0){
        		accountParm.setData("NPAY_MEDICAL_AMT1", df.format(StringTool.round(memResultParm.getDouble("NPAY_MEDICAL_AMT1"),2)));//退款总金额
            		
        	}else{
        		accountParm.setData("NPAY_MEDICAL_AMT1", df.format(StringTool.round(-memResultParm.getDouble("NPAY_MEDICAL_AMT1"),2)));//退款总金额
            	
        	}
//        	accountParm.setData("NPAY_MEDICAL_AMT1", df.format(StringTool.round(memResultParm.getDouble("NPAY_MEDICAL_AMT1"),2)));//退款总金额
        	accountParm.setData("TOT_MEDICAL_AMT1", df.format(StringTool.round(memResultParm.getDouble("PAY_MEDICAL_ATM_TOTAL1")+memResultParm.getDouble("NPAY_MEDICAL_AMT1"),2)));//总金额
        	accountParm.setData("TOT_CASH1", df.format(StringTool.round(memResultParm.getDouble("TOT_CASH1"),2)));//现金总金额
        	accountParm.setData("TOT_B1", df.format(StringTool.round(memResultParm.getDouble("TOT_B1"),2)));//支票总金额
        	accountParm.setData("TOT_CARD1", df.format(StringTool.round(memResultParm.getDouble("TOT_CARD1"),2)));//刷卡总金额
        	accountParm.setData("TOT_LPK1", df.format(StringTool.round(memResultParm.getDouble("TOT_LPK1"),2)));//礼品卡总金额
        	accountParm.setData("TOT_XJDYQ1", df.format(StringTool.round(memResultParm.getDouble("TOT_XJDYQ1"),2)));//现金抵用券总金额
        	accountParm.setData("TOT_WX1", df.format(StringTool.round(memResultParm.getDouble("TOT_WX1"),2)));//微信总金额
        	accountParm.setData("TOT_ZFB1", df.format(StringTool.round(memResultParm.getDouble("TOT_ZFB1"),2)));//支付宝总金额
        	accountParm.setData("TOT_EKT1", df.format(StringTool.round(memResultParm.getDouble("TOT_EKT1"),2)));//医疗卡总金额
        	accountParm.setData("TOT_C41", df.format(StringTool.round(memResultParm.getDouble("TOT_C41"),2)));//医院垫付总金额
        	accountParm.setData("PAY_MEDICAL_QTY1", memResultParm.getInt("PAY_MEDICAL_QTY1"));//收入总数量
        	accountParm.setData("NPAY_MEDICAL_QTY1", memResultParm.getInt("NPAY_MEDICAL_QTY1"));//退款总数量
        	accountParm.setData("TOT_MEDICAL_QTY1", memResultParm.getInt("PAY_MEDICAL_QTY1")+memResultParm.getInt("NPAY_MEDICAL_QTY1"));//总数量
        	//套餐销售日结
        	accountParm.setData("IN_CASH2", df.format(StringTool.round(pakgeResultParm.getDouble("IN_CASH2"),2)));
        	accountParm.setData("IN_B2", df.format(StringTool.round(pakgeResultParm.getDouble("IN_B2"),2)));
        	accountParm.setData("IN_CARD2", df.format(StringTool.round(pakgeResultParm.getDouble("IN_CARD2"),2)));
        	accountParm.setData("IN_LPK2", df.format(StringTool.round(pakgeResultParm.getDouble("IN_LPK2"),2)));//礼品卡
        	accountParm.setData("IN_XJDYQ2", df.format(StringTool.round(pakgeResultParm.getDouble("IN_XJDYQ2"),2)));//现金抵用券
        	accountParm.setData("IN_WX2", df.format(StringTool.round(pakgeResultParm.getDouble("IN_WX2"),2)));//微信
        	accountParm.setData("IN_ZFB2", df.format(StringTool.round(pakgeResultParm.getDouble("IN_ZFB2"),2)));//支付宝
        	accountParm.setData("IN_EKT2", df.format(StringTool.round(pakgeResultParm.getDouble("IN_EKT2"),2)));//医疗卡
        	accountParm.setData("IN_C42", df.format(StringTool.round(pakgeResultParm.getDouble("IN_C42"),2)));
//        	accountParm.setData("OUT_CASH2", df.format(StringTool.round(pakgeResultParm.getDouble("OUT_CASH2"),2)));
//        	accountParm.setData("OUT_B2", df.format(StringTool.round(pakgeResultParm.getDouble("OUT_B2"),2)));
//        	accountParm.setData("OUT_CARD2", df.format(StringTool.round(pakgeResultParm.getDouble("OUT_CARD2"),2)));
//        	accountParm.setData("OUT_LPK2", df.format(StringTool.round(pakgeResultParm.getDouble("OUT_LPK1"),2)));//礼品卡
        	accountParm.setData("OUT_XJDYQ2", df.format(StringTool.round(pakgeResultParm.getDouble("OUT_XJDYQ2"),2)));//现金抵用券
        	accountParm.setData("PAY_MEDICAL_ATM_TOTAL2", df.format(StringTool.round(pakgeResultParm.getDouble("PAY_MEDICAL_ATM_TOTAL2"),2)));//收入总金额
        	if(pakgeResultParm.getDouble("NPAY_MEDICAL_AMT2") == 0){
        		accountParm.setData("NPAY_MEDICAL_AMT2", df.format(StringTool.round(pakgeResultParm.getDouble("NPAY_MEDICAL_AMT2"),2)));//退款总金额
        	}else{
        		accountParm.setData("NPAY_MEDICAL_AMT2", df.format(StringTool.round(-pakgeResultParm.getDouble("NPAY_MEDICAL_AMT2"),2)));//退款总金额
        	}
        	if(pakgeResultParm.getDouble("OUT_CASH2") == 0){
        		accountParm.setData("OUT_CASH2", df.format(StringTool.round(pakgeResultParm.getDouble("OUT_CASH2"),2)));//退款总金额
        	}else{
        		accountParm.setData("OUT_CASH2", df.format(StringTool.round(-pakgeResultParm.getDouble("OUT_CASH2"),2)));//退款总金额
        	}
        	/*if(pakgeResultParm.getDouble("NPAY_MEDICAL_AMT2") == 0){
        		accountParm.setData("OUT_CASH2", df.format(StringTool.round(pakgeResultParm.getDouble("NPAY_MEDICAL_AMT2"),2)));//退款总金额
        	}else{
        		accountParm.setData("OUT_CASH2", df.format(StringTool.round(-pakgeResultParm.getDouble("NPAY_MEDICAL_AMT2"),2)));//退款总金额
        	}*/
        	if(pakgeResultParm.getDouble("OUT_B2") == 0){
        		accountParm.setData("OUT_B2", df.format(StringTool.round(pakgeResultParm.getDouble("OUT_B2"),2)));//退款总金额
        	}else{
        		accountParm.setData("OUT_B2", df.format(StringTool.round(-pakgeResultParm.getDouble("OUT_B2"),2)));//退款总金额
        	}
        	if(pakgeResultParm.getDouble("OUT_CARD2") == 0){
        		accountParm.setData("OUT_CARD2", df.format(StringTool.round(pakgeResultParm.getDouble("OUT_CARD2"),2)));//退款总金额
        	}else{
        		accountParm.setData("OUT_CARD2", df.format(StringTool.round(-pakgeResultParm.getDouble("OUT_CARD2"),2)));//退款总金额
        	}
        	if(pakgeResultParm.getDouble("OUT_LPK2") == 0){
        		accountParm.setData("OUT_LPK2", df.format(StringTool.round(pakgeResultParm.getDouble("OUT_LPK2"),2)));//退款总金额
        	}else{
        		accountParm.setData("OUT_LPK2", df.format(StringTool.round(-pakgeResultParm.getDouble("OUT_LPK2"),2)));//退款总金额
        	}
        	if(pakgeResultParm.getDouble("OUT_XJDYQ2") == 0){
        		accountParm.setData("OUT_XJDYQ2", df.format(StringTool.round(pakgeResultParm.getDouble("OUT_XJDYQ2"),2)));//退款总金额
        	}else{
        		accountParm.setData("OUT_XJDYQ2", df.format(StringTool.round(-pakgeResultParm.getDouble("OUT_XJDYQ2"),2)));//退款总金额
        	}
        	if(pakgeResultParm.getDouble("OUT_WX2") == 0){
        		accountParm.setData("OUT_WX2", df.format(StringTool.round(pakgeResultParm.getDouble("OUT_WX2"),2)));//退款总金额
        	}else{
        		accountParm.setData("OUT_WX2", df.format(StringTool.round(-pakgeResultParm.getDouble("OUT_WX2"),2)));//退款总金额
        	}
        	if(pakgeResultParm.getDouble("OUT_ZFB2") == 0){
        		accountParm.setData("OUT_ZFB2", df.format(StringTool.round(pakgeResultParm.getDouble("OUT_ZFB2"),2)));//退款总金额
        	}else{
        		accountParm.setData("OUT_ZFB2", df.format(StringTool.round(-pakgeResultParm.getDouble("OUT_ZFB2"),2)));//退款总金额
        	}
        	if(pakgeResultParm.getDouble("OUT_EKT2") == 0){
        		accountParm.setData("OUT_EKT2", df.format(StringTool.round(pakgeResultParm.getDouble("OUT_EKT2"),2)));//退款总金额
        	}else{
        		accountParm.setData("OUT_EKT2", df.format(StringTool.round(-pakgeResultParm.getDouble("OUT_EKT2"),2)));//退款总金额
        	}
        	if(pakgeResultParm.getDouble("OUT_C42") == 0){
        		accountParm.setData("OUT_C42", df.format(StringTool.round(pakgeResultParm.getDouble("OUT_C42"),2)));//退款总金额
        	}else{
        		accountParm.setData("OUT_C42", df.format(StringTool.round(-pakgeResultParm.getDouble("OUT_C42"),2)));//退款总金额
        	}
//        	accountParm.setData("NPAY_MEDICAL_AMT2", df.format(StringTool.round(pakgeResultParm.getDouble("NPAY_MEDICAL_AMT2"),2)));//退款总金额
        	accountParm.setData("TOT_MEDICAL_AMT2", df.format(StringTool.round(pakgeResultParm.getDouble("PAY_MEDICAL_ATM_TOTAL2")+pakgeResultParm.getDouble("NPAY_MEDICAL_AMT2"),2)));//总金额
        	accountParm.setData("TOT_CASH2", df.format(StringTool.round(pakgeResultParm.getDouble("TOT_CASH2"),2)));//现金总金额
        	//accountParm.setData("TOT_CASH2", df.format(StringTool.round(pakgeResultParm.getDouble("TOT_CASH2")+pakgeResultParm.getDouble("NPAY_MEDICAL_AMT2"),2)));//现金总金额
        	accountParm.setData("TOT_B2", df.format(StringTool.round(pakgeResultParm.getDouble("TOT_B2"),2)));//支票总金额
        	accountParm.setData("TOT_CARD2", df.format(StringTool.round(pakgeResultParm.getDouble("TOT_CARD2"),2)));//刷卡总金额
        	accountParm.setData("TOT_LPK2", df.format(StringTool.round(pakgeResultParm.getDouble("TOT_LPK2"),2)));//礼品卡总金额
        	accountParm.setData("TOT_XJDYQ2", df.format(StringTool.round(pakgeResultParm.getDouble("TOT_XJDYQ2"),2)));//现金抵用券总金额
        	accountParm.setData("TOT_WX2", df.format(StringTool.round(pakgeResultParm.getDouble("TOT_WX2"),2)));//微信总金额
        	accountParm.setData("TOT_ZFB2", df.format(StringTool.round(pakgeResultParm.getDouble("TOT_ZFB2"),2)));//支付宝总金额
        	accountParm.setData("TOT_EKT2", df.format(StringTool.round(pakgeResultParm.getDouble("TOT_EKT2"),2)));//医疗卡总金额
        	accountParm.setData("TOT_C42", df.format(StringTool.round(pakgeResultParm.getDouble("TOT_C42"),2)));
        	accountParm.setData("PAY_MEDICAL_QTY2", pakgeResultParm.getInt("PAY_MEDICAL_QTY2"));//收入总数量
        	accountParm.setData("NPAY_MEDICAL_QTY2", pakgeResultParm.getInt("NPAY_MEDICAL_QTY2"));//退款总数量
        	accountParm.setData("TOT_MEDICAL_QTY2", pakgeResultParm.getInt("PAY_MEDICAL_QTY2")+pakgeResultParm.getInt("NPAY_MEDICAL_QTY2"));//总数量
        	//礼品卡销售数据
        	accountParm.setData("IN_CASH3", df.format(StringTool.round(lpkResultParm.getDouble("IN_CASH3"),2)));
        	accountParm.setData("IN_B3", df.format(StringTool.round(lpkResultParm.getDouble("IN_B3"),2)));
        	accountParm.setData("IN_CARD3", df.format(StringTool.round(lpkResultParm.getDouble("IN_CARD3"),2)));
        	accountParm.setData("IN_LPK3", df.format(StringTool.round(lpkResultParm.getDouble("IN_LPK3"),2)));//礼品卡
        	accountParm.setData("IN_XJDYQ3", df.format(StringTool.round(lpkResultParm.getDouble("IN_XJDYQ3"),2)));//现金抵用券
        	accountParm.setData("IN_WX3", df.format(StringTool.round(lpkResultParm.getDouble("IN_WX3"),2)));//微信
        	accountParm.setData("IN_ZFB3", df.format(StringTool.round(lpkResultParm.getDouble("IN_ZFB3"),2)));//支付宝
        	accountParm.setData("IN_EKT3", df.format(StringTool.round(lpkResultParm.getDouble("IN_EKT3"),2)));//医疗卡
        	accountParm.setData("IN_C43", df.format(StringTool.round(lpkResultParm.getDouble("IN_C43"),2)));
//        	accountParm.setData("OUT_CASH3", df.format(StringTool.round(lpkResultParm.getDouble("OUT_CASH3"),2)));
//        	accountParm.setData("OUT_B3", df.format(StringTool.round(lpkResultParm.getDouble("OUT_B3"),2)));
//        	accountParm.setData("OUT_CARD3", df.format(StringTool.round(lpkResultParm.getDouble("OUT_CARD3"),2)));
//        	accountParm.setData("OUT_LPK3", df.format(StringTool.round(lpkResultParm.getDouble("OUT_LPK3"),2)));//礼品卡
        	accountParm.setData("OUT_XJDYQ3", df.format(StringTool.round(lpkResultParm.getDouble("OUT_XJDYQ3"),2)));//现金抵用券
        	accountParm.setData("PAY_MEDICAL_ATM_TOTAL3", df.format(StringTool.round(lpkResultParm.getDouble("PAY_MEDICAL_ATM_TOTAL3"),2)));//收入总金额
        	if(lpkResultParm.getDouble("NPAY_MEDICAL_AMT3") == 0){
        		accountParm.setData("NPAY_MEDICAL_AMT3", df.format(StringTool.round(lpkResultParm.getDouble("NPAY_MEDICAL_AMT3"),2)));//退款总金额	
        	}else{
        		accountParm.setData("NPAY_MEDICAL_AMT3", df.format(StringTool.round(-lpkResultParm.getDouble("NPAY_MEDICAL_AMT3"),2)));//退款总金额
        	}
        	if(lpkResultParm.getDouble("OUT_CASH3") == 0){
        		accountParm.setData("OUT_CASH3", df.format(StringTool.round(lpkResultParm.getDouble("OUT_CASH3"),2)));//退款总金额	
        	}else{
        		accountParm.setData("OUT_CASH3", df.format(StringTool.round(-lpkResultParm.getDouble("OUT_CASH3"),2)));//退款总金额
        	}
        	if(lpkResultParm.getDouble("OUT_B3") == 0){
        		accountParm.setData("OUT_B3", df.format(StringTool.round(lpkResultParm.getDouble("OUT_B3"),2)));//退款总金额	
        	}else{
        		accountParm.setData("OUT_B3", df.format(StringTool.round(-lpkResultParm.getDouble("OUT_B3"),2)));//退款总金额
        	}
        	if(lpkResultParm.getDouble("OUT_CARD3") == 0){
        		accountParm.setData("OUT_CARD3", df.format(StringTool.round(lpkResultParm.getDouble("OUT_CARD3"),2)));//退款总金额	
        	}else{
        		accountParm.setData("OUT_CARD3", df.format(StringTool.round(-lpkResultParm.getDouble("OUT_CARD3"),2)));//退款总金额
        	}
        	if(lpkResultParm.getDouble("OUT_LPK3") == 0){
        		accountParm.setData("OUT_LPK3", df.format(StringTool.round(lpkResultParm.getDouble("OUT_LPK3"),2)));//退款总金额	
        	}else{
        		accountParm.setData("OUT_LPK3", df.format(StringTool.round(-lpkResultParm.getDouble("OUT_LPK3"),2)));//退款总金额
        	}
        	if(lpkResultParm.getDouble("OUT_XJDYQ3") == 0){
        		accountParm.setData("OUT_XJDYQ3", df.format(StringTool.round(lpkResultParm.getDouble("OUT_XJDYQ3"),2)));//退款总金额	
        	}else{
        		accountParm.setData("OUT_XJDYQ3", df.format(StringTool.round(-lpkResultParm.getDouble("OUT_XJDYQ3"),2)));//退款总金额
        	}
        	if(lpkResultParm.getDouble("OUT_WX3") == 0){
        		accountParm.setData("OUT_WX3", df.format(StringTool.round(lpkResultParm.getDouble("OUT_WX3"),2)));//退款总金额	
        	}else{
        		accountParm.setData("OUT_WX3", df.format(StringTool.round(-lpkResultParm.getDouble("OUT_WX3"),2)));//退款总金额
        	}
        	if(lpkResultParm.getDouble("OUT_ZFB3") == 0){
        		accountParm.setData("OUT_ZFB3", df.format(StringTool.round(lpkResultParm.getDouble("OUT_ZFB3"),2)));//退款总金额	
        	}else{
        		accountParm.setData("OUT_ZFB3", df.format(StringTool.round(-lpkResultParm.getDouble("OUT_ZFB3"),2)));//退款总金额
        	}
        	if(lpkResultParm.getDouble("OUT_EKT3") == 0){
        		accountParm.setData("OUT_EKT3", df.format(StringTool.round(lpkResultParm.getDouble("OUT_EKT3"),2)));//退款总金额	
        	}else{
        		accountParm.setData("OUT_EKT3", df.format(StringTool.round(-lpkResultParm.getDouble("OUT_EKT3"),2)));//退款总金额
        	}
        	if(lpkResultParm.getDouble("OUT_C43") == 0){
        		accountParm.setData("OUT_C43", df.format(StringTool.round(lpkResultParm.getDouble("OUT_C43"),2)));//退款总金额	
        	}else{
        		accountParm.setData("OUT_C43", df.format(StringTool.round(-lpkResultParm.getDouble("OUT_C43"),2)));//退款总金额
        	}
        	
//        	accountParm.setData("NPAY_MEDICAL_AMT3", df.format(StringTool.round(lpkResultParm.getDouble("NPAY_MEDICAL_AMT3"),2)));//退款总金额
        	accountParm.setData("TOT_MEDICAL_AMT3", df.format(StringTool.round(lpkResultParm.getDouble("PAY_MEDICAL_ATM_TOTAL3")+lpkResultParm.getDouble("NPAY_MEDICAL_AMT3"),2)));//总金额
        	accountParm.setData("TOT_CASH3", df.format(StringTool.round(lpkResultParm.getDouble("TOT_CASH3"),2)));//现金总金额
        	accountParm.setData("TOT_B3", df.format(StringTool.round(lpkResultParm.getDouble("TOT_B3"),2)));//支票总金额
        	accountParm.setData("TOT_CARD3", df.format(StringTool.round(lpkResultParm.getDouble("TOT_CARD3"),2)));//刷卡总金额
        	accountParm.setData("TOT_LPK3", df.format(StringTool.round(lpkResultParm.getDouble("TOT_LPK3"),2)));//礼品卡总金额
        	accountParm.setData("TOT_XJDYQ3", df.format(StringTool.round(lpkResultParm.getDouble("TOT_XJDYQ3"),2)));//现金抵用券总金额
        	accountParm.setData("TOT_WX3", df.format(StringTool.round(lpkResultParm.getDouble("TOT_WX3"),2)));//微信总金额
        	accountParm.setData("TOT_ZFB3", df.format(StringTool.round(lpkResultParm.getDouble("TOT_ZFB3"),2)));//支付宝总金额
        	accountParm.setData("TOT_EKT3", df.format(StringTool.round(lpkResultParm.getDouble("TOT_EKT3"),2)));//医疗卡总金额
        	accountParm.setData("TOT_C43", df.format(StringTool.round(lpkResultParm.getDouble("TOT_C43"),2)));
        	accountParm.setData("PAY_MEDICAL_QTY3", lpkResultParm.getInt("PAY_MEDICAL_QTY3"));//收入总数量
        	accountParm.setData("NPAY_MEDICAL_QTY3", lpkResultParm.getInt("NPAY_MEDICAL_QTY3"));//退款总数量
        	accountParm.setData("TOT_MEDICAL_QTY3", lpkResultParm.getInt("PAY_MEDICAL_QTY3")+lpkResultParm.getInt("NPAY_MEDICAL_QTY3"));//总数量
        	
    	}
    	
    	String ar_amt_word = StringUtil.getInstance().numberToWord(pay_medical_atm-npay_medical_amt + factorage_amt
    			+accountParm.getDouble("TOT_MEDICAL_AMT1")+accountParm.getDouble("TOT_MEDICAL_AMT2")+accountParm.getDouble("TOT_MEDICAL_AMT3"));
    	if(ar_amt_word.lastIndexOf("分")>0){
    		ar_amt_word=ar_amt_word.substring(0,ar_amt_word.lastIndexOf("分")+1);
    	}
    	
    	if ("N".equals(getValue("TOGEDER_FLG"))) {
    		accountParm.setData("CASHIER1","收费员："+billUser);
    		accountParm.setData("PRINT_DATE", account_date1);//单个的打印时，时间取日结时间
    		accountParm.setData("ACCOUNT_SEQ", accoutSeq);
    		accountParm.setData("PARAM0", "日结日期：");
    		accountParm.setData("PARAM1", "报表号：");
    	}else{
    		accountParm.setData("EXAM1", "收银主管:");
    		accountParm.setData("CASHIER1", "审核2:");
    		accountParm.setData("EXAM2", "审核1:");
    		accountParm.setData("PRINT_DATE", accoutSeq);
    		accountParm.setData("ACCOUNT_SEQ", sysDate);
    		accountParm.setData("PARAM0", "报表号：");
    		accountParm.setData("PARAM1", "    打印日期：");
    	}
    	accountParm.setData("CASHIER", billUser);
    	accountParm.setData("BISSINESS_NO", billNo);
    	accountParm.setData("BISSINESS_DATE", date);
    	
    	accountParm.setData("CHANGE_QTY", change_qty);
    	accountParm.setData("ADD_QTY", add_qty);
    	accountParm.setData("BUY_QTY", buy_qty);
    	accountParm.setData("OPT_NAME1", "操作员: "+account_user);
    	accountParm.setData("SENT_COUNT", sent_count);
    	accountParm.setData("FACTORAGE_AMT", df.format(StringTool.round(factorage_amt,2)));
    	accountParm.setData("AR_AMT", df.format(StringTool.round(pay_medical_atm-npay_medical_amt,2)));
    	accountParm.setData("AR_AMT_WORD", ar_amt_word);
    	accountParm.setData("PAY_MEDICAL_QTY", pay_medical_qty);
    	accountParm.setData("NPAY_MEDICAL_QTY", npay_medical_qty);
    	accountParm.setData("TOT_MEDICAL_QTY", npay_medical_qty+pay_medical_qty);
    	accountParm.setData("PAY_MEDICAL_ATM_TOTAL", df.format(StringTool.round((pay_medical_atm),2)));
    	accountParm.setData("NPAY_MEDICAL_AMT", df.format(StringTool.round(npay_medical_amt,2)));
    	accountParm.setData("TOT_MEDICAL_AMT", df.format(StringTool.round((pay_medical_atm-npay_medical_amt),2)));
    	accountParm.setData("IN_CASH", df.format(StringTool.round(inCash,2)));
    	accountParm.setData("IN_B", df.format(StringTool.round(inB,2)));
    	accountParm.setData("IN_CARD", df.format(StringTool.round(inCard,2)));
    	accountParm.setData("IN_LPK", df.format(StringTool.round(inlpk,2)));//礼品卡
    	accountParm.setData("IN_XJDYQ", df.format(StringTool.round(inXjdyq,2)));//现金抵用券
    	accountParm.setData("IN_WX", df.format(StringTool.round(inWx,2)));//微信
    	accountParm.setData("IN_ZFB", df.format(StringTool.round(inZfb,2)));//支付宝
    	accountParm.setData("IN_EKT", df.format(StringTool.round(inEkt,2)));//医疗卡
    	accountParm.setData("IN_C4", df.format(StringTool.round(inC4,2)));
    	accountParm.setData("OUT_CASH", df.format(StringTool.round(outCash,2)));
    	accountParm.setData("OUT_B", df.format(StringTool.round(outB,2)));
    	accountParm.setData("OUT_CARD", df.format(StringTool.round(outCard,2)));
    	accountParm.setData("OUT_LPK", df.format(StringTool.round(outLpk,2)));//礼品卡
    	accountParm.setData("OUT_XJDYQ", df.format(StringTool.round(outXjdyq,2)));//现金抵用券
    	accountParm.setData("OUT_WX", df.format(StringTool.round(outWx,2)));//微信
    	accountParm.setData("OUT_ZFB", df.format(StringTool.round(outZfb,2)));//支付宝
    	accountParm.setData("OUT_EKT", df.format(StringTool.round(outEkt,2)));//医疗卡
    	accountParm.setData("OUT_C4", df.format(StringTool.round(outC4,2)));
    	accountParm.setData("TOT_CASH", df.format(StringTool.round(totCash,2)));
    	accountParm.setData("TOT_B", df.format(StringTool.round(totB,2)));
    	accountParm.setData("TOT_CARD", df.format(StringTool.round(totCard,2)));
    	accountParm.setData("TOT_LPK", df.format(StringTool.round(totLpk,2)));//礼品卡
    	accountParm.setData("TOT_XJDYQ", df.format(StringTool.round(totXjdyq,2)));//现金抵用券
    	accountParm.setData("TOT_WX", df.format(StringTool.round(totWx,2)));//微信
    	accountParm.setData("TOT_ZFB", df.format(StringTool.round(totZfb,2)));//支付宝
    	accountParm.setData("TOT_EKT", df.format(StringTool.round(totEkt,2)));//医疗卡
    	accountParm.setData("TOT_C4", df.format(StringTool.round(totC4,2)));
    	
    	accountParm.setData("PAY_MEDICAL_QTY4",accountParm.getInt("PAY_MEDICAL_QTY")+accountParm.getInt("PAY_MEDICAL_QTY1")+accountParm.getInt("PAY_MEDICAL_QTY2")+accountParm.getInt("PAY_MEDICAL_QTY3"));
    	accountParm.setData("NPAY_MEDICAL_QTY4",accountParm.getInt("NPAY_MEDICAL_QTY")+accountParm.getInt("NPAY_MEDICAL_QTY1")+accountParm.getInt("NPAY_MEDICAL_QTY2")+accountParm.getInt("NPAY_MEDICAL_QTY3"));
    	accountParm.setData("TOT_MEDICAL_QTY4",accountParm.getInt("TOT_MEDICAL_QTY")+accountParm.getInt("TOT_MEDICAL_QTY1")+accountParm.getInt("TOT_MEDICAL_QTY2")+accountParm.getInt("TOT_MEDICAL_QTY3"));
    	accountParm.setData("PAY_MEDICAL_ATM_TOTAL4",df.format(StringTool.round(Double.valueOf(accountParm.getValue("PAY_MEDICAL_ATM_TOTAL"))+Double.valueOf(accountParm.getValue("PAY_MEDICAL_ATM_TOTAL1"))+Double.valueOf(accountParm.getValue("PAY_MEDICAL_ATM_TOTAL2"))+Double.valueOf(accountParm.getValue("PAY_MEDICAL_ATM_TOTAL3")),2)));
    	accountParm.setData("NPAY_MEDICAL_AMT4",df.format(StringTool.round(Double.valueOf(accountParm.getValue("NPAY_MEDICAL_AMT"))+Double.valueOf(accountParm.getValue("NPAY_MEDICAL_AMT1"))+Double.valueOf(accountParm.getValue("NPAY_MEDICAL_AMT2"))+Double.valueOf(accountParm.getValue("NPAY_MEDICAL_AMT3")),2)));
    	accountParm.setData("TOT_MEDICAL_AMT4",df.format(StringTool.round(Double.valueOf(accountParm.getValue("TOT_MEDICAL_AMT"))+Double.valueOf(accountParm.getValue("TOT_MEDICAL_AMT1"))+Double.valueOf(accountParm.getValue("TOT_MEDICAL_AMT2"))+Double.valueOf(accountParm.getValue("TOT_MEDICAL_AMT3")),2)));
    	accountParm.setData("IN_CASH4",df.format(StringTool.round(Double.valueOf(accountParm.getValue("IN_CASH"))+Double.valueOf(accountParm.getValue("IN_CASH1"))+Double.valueOf(accountParm.getValue("IN_CASH2"))+Double.valueOf(accountParm.getValue("IN_CASH3")),2)));
    	accountParm.setData("OUT_CASH4",df.format(StringTool.round(Double.valueOf(accountParm.getValue("OUT_CASH"))+Double.valueOf(accountParm.getValue("OUT_CASH1"))+Double.valueOf(accountParm.getValue("OUT_CASH2"))+Double.valueOf(accountParm.getValue("OUT_CASH3")),2)));
    	accountParm.setData("TOT_CASH4",df.format(StringTool.round(Double.valueOf(accountParm.getValue("TOT_CASH"))+Double.valueOf(accountParm.getValue("TOT_CASH1"))+Double.valueOf(accountParm.getValue("TOT_CASH2"))+Double.valueOf(accountParm.getValue("TOT_CASH3")),2)));
    	accountParm.setData("IN_B4",df.format(StringTool.round(Double.valueOf(accountParm.getValue("IN_B"))+Double.valueOf(accountParm.getValue("IN_B1"))+Double.valueOf(accountParm.getValue("IN_B2"))+Double.valueOf(accountParm.getValue("IN_B3")),2)));
    	accountParm.setData("OUT_B4",df.format(StringTool.round(Double.valueOf(accountParm.getValue("OUT_B"))+Double.valueOf(accountParm.getValue("OUT_B1"))+Double.valueOf(accountParm.getValue("OUT_B2"))+Double.valueOf(accountParm.getValue("OUT_B3")),2)));
    	accountParm.setData("TOT_B4",df.format(StringTool.round(Double.valueOf(accountParm.getValue("TOT_B"))+Double.valueOf(accountParm.getValue("TOT_B1"))+Double.valueOf(accountParm.getValue("TOT_B2"))+Double.valueOf(accountParm.getValue("TOT_B3")),2)));
    	accountParm.setData("IN_CARD4",df.format(StringTool.round(Double.valueOf(accountParm.getValue("IN_CARD"))+Double.valueOf(accountParm.getValue("IN_CARD1"))+Double.valueOf(accountParm.getValue("IN_CARD2"))+Double.valueOf(accountParm.getValue("IN_CARD3")),2)));
    	accountParm.setData("OUT_CARD4",df.format(StringTool.round(Double.valueOf(accountParm.getValue("OUT_CARD"))+Double.valueOf(accountParm.getValue("OUT_CARD1"))+Double.valueOf(accountParm.getValue("OUT_CARD2"))+Double.valueOf(accountParm.getValue("OUT_CARD3")),2)));
    	accountParm.setData("TOT_CARD4",df.format(StringTool.round(Double.valueOf(accountParm.getValue("TOT_CARD"))+Double.valueOf(accountParm.getValue("TOT_CARD1"))+Double.valueOf(accountParm.getValue("TOT_CARD2"))+Double.valueOf(accountParm.getValue("TOT_CARD3")),2)));
    	accountParm.setData("IN_LPK4",df.format(StringTool.round(Double.valueOf(accountParm.getValue("IN_LPK"))+Double.valueOf(accountParm.getValue("IN_LPK1"))+Double.valueOf(accountParm.getValue("IN_LPK2"))+Double.valueOf(accountParm.getValue("IN_LPK3")),2)));
    	accountParm.setData("OUT_LPK4",df.format(StringTool.round(Double.valueOf(accountParm.getValue("OUT_LPK"))+Double.valueOf(accountParm.getValue("OUT_LPK1"))+Double.valueOf(accountParm.getValue("OUT_LPK2"))+Double.valueOf(accountParm.getValue("OUT_LPK3")),2)));
    	accountParm.setData("TOT_LPK4",df.format(StringTool.round(Double.valueOf(accountParm.getValue("TOT_LPK"))+Double.valueOf(accountParm.getValue("TOT_LPK1"))+Double.valueOf(accountParm.getValue("TOT_LPK2"))+Double.valueOf(accountParm.getValue("TOT_LPK3")),2)));
    	accountParm.setData("IN_XJDYQ4",df.format(StringTool.round(Double.valueOf(accountParm.getValue("IN_XJDYQ"))+Double.valueOf(accountParm.getValue("IN_XJDYQ1"))+Double.valueOf(accountParm.getValue("IN_XJDYQ2"))+Double.valueOf(accountParm.getValue("IN_XJDYQ3")),2)));
    	accountParm.setData("OUT_XJDYQ4",df.format(StringTool.round(Double.valueOf(accountParm.getValue("OUT_XJDYQ"))+Double.valueOf(accountParm.getValue("OUT_XJDYQ1"))+Double.valueOf(accountParm.getValue("OUT_XJDYQ2"))+Double.valueOf(accountParm.getValue("OUT_XJDYQ3")),2)));
    	accountParm.setData("TOT_XJDYQ4",df.format(StringTool.round(Double.valueOf(accountParm.getValue("TOT_XJDYQ"))+Double.valueOf(accountParm.getValue("TOT_XJDYQ1"))+Double.valueOf(accountParm.getValue("TOT_XJDYQ2"))+Double.valueOf(accountParm.getValue("TOT_XJDYQ3")),2)));
    	accountParm.setData("IN_WX4",df.format(StringTool.round(Double.valueOf(accountParm.getDouble("IN_WX"))+Double.valueOf(accountParm.getDouble("IN_WX1"))+Double.valueOf(accountParm.getDouble("IN_WX2"))+Double.valueOf(accountParm.getDouble("IN_WX3")),2)));
    	accountParm.setData("OUT_WX4",df.format(StringTool.round(Double.valueOf(accountParm.getValue("OUT_WX"))+Double.valueOf(accountParm.getValue("OUT_WX1"))+Double.valueOf(accountParm.getValue("OUT_WX2"))+Double.valueOf(accountParm.getValue("OUT_WX3")),2)));
    	accountParm.setData("TOT_WX4",df.format(StringTool.round(Double.valueOf(accountParm.getValue("TOT_WX"))+Double.valueOf(accountParm.getValue("TOT_WX1"))+Double.valueOf(accountParm.getValue("TOT_WX2"))+Double.valueOf(accountParm.getValue("TOT_WX3")),2)));
    	accountParm.setData("IN_ZFB4",df.format(StringTool.round(Double.valueOf(accountParm.getValue("IN_ZFB"))+Double.valueOf(accountParm.getValue("IN_ZFB1"))+Double.valueOf(accountParm.getValue("IN_ZFB2"))+Double.valueOf(accountParm.getValue("IN_ZFB3")),2)));
    	accountParm.setData("OUT_ZFB4",df.format(StringTool.round(Double.valueOf(accountParm.getValue("OUT_ZFB"))+Double.valueOf(accountParm.getValue("OUT_ZFB1"))+Double.valueOf(accountParm.getValue("OUT_ZFB2"))+Double.valueOf(accountParm.getValue("OUT_ZFB3")),2)));
    	accountParm.setData("TOT_ZFB4",df.format(StringTool.round(Double.valueOf(accountParm.getValue("TOT_ZFB"))+Double.valueOf(accountParm.getValue("TOT_ZFB1"))+Double.valueOf(accountParm.getValue("TOT_ZFB2"))+Double.valueOf(accountParm.getValue("TOT_ZFB3")),2)));
    	accountParm.setData("IN_EKT4",df.format(StringTool.round(Double.valueOf(accountParm.getValue("IN_EKT"))+Double.valueOf(accountParm.getValue("IN_EKT1"))+Double.valueOf(accountParm.getValue("IN_EKT2"))+Double.valueOf(accountParm.getValue("IN_EKT3")),2)));
    	accountParm.setData("OUT_EKT4",df.format(StringTool.round(Double.valueOf(accountParm.getValue("OUT_EKT"))+Double.valueOf(accountParm.getValue("OUT_EKT1"))+Double.valueOf(accountParm.getValue("OUT_EKT2"))+Double.valueOf(accountParm.getValue("OUT_EKT3")),2)));
    	accountParm.setData("TOT_EKT4",df.format(StringTool.round(Double.valueOf(accountParm.getValue("TOT_EKT"))+Double.valueOf(accountParm.getValue("TOT_EKT1"))+Double.valueOf(accountParm.getValue("TOT_EKT2"))+Double.valueOf(accountParm.getValue("TOT_EKT3")),2)));
    	accountParm.setData("IN_C44",df.format(StringTool.round(Double.valueOf(accountParm.getValue("IN_C4"))+Double.valueOf(accountParm.getValue("IN_C41"))+Double.valueOf(accountParm.getValue("IN_C42"))+Double.valueOf(accountParm.getValue("IN_C43")),2)));
    	accountParm.setData("OUT_C44",df.format(StringTool.round(Double.valueOf(accountParm.getValue("OUT_C4"))+Double.valueOf(accountParm.getValue("OUT_C41"))+Double.valueOf(accountParm.getValue("OUT_C42"))+Double.valueOf(accountParm.getValue("OUT_C43")),2)));
    	accountParm.setData("TOT_C44",df.format(StringTool.round(Double.valueOf(accountParm.getValue("TOT_C4"))+Double.valueOf(accountParm.getValue("TOT_C41"))+Double.valueOf(accountParm.getValue("TOT_C42"))+Double.valueOf(accountParm.getValue("TOT_C43")),2)));
    	
    	
    	//System.out.println("parmcount::"+df.format(StringTool.round(Double.valueOf(accountParm.getValue("PAY_MEDICAL_ATM_TOTAL"))+Double.valueOf(accountParm.getValue("PAY_MEDICAL_ATM_TOTAL1"))+Double.valueOf(accountParm.getValue("PAY_MEDICAL_ATM_TOTAL2"))+Double.valueOf(accountParm.getValue("PAY_MEDICAL_ATM_TOTAL3")),2)));
    	//accountParm.setData("",);
//    	accountParm.setData("TOT_TOT", df.format(StringTool.round(pay_medical_atm-npay_medical_amt + 
//    			factorage_amt+accountParm.getDouble("TOT_MEDICAL_AMT1")
//    			+accountParm.getDouble("TOT_MEDICAL_AMT2")+accountParm.getDouble("TOT_MEDICAL_AMT3"),2)));
    	accountParm.setData("TOT_TOT", df.format(StringTool.round(ar_amt + 
    			accountParm.getDouble("TOT_MEDICAL_AMT1")
    			+accountParm.getDouble("TOT_MEDICAL_AMT2")+accountParm.getDouble("TOT_MEDICAL_AMT3"),2)));
    	data.setData("ACCOUNT", accountParm.getData());
//    	System.out.println("+++++++accountParm accountParm is ::"+accountParm);
    	this.openPrintWindow("%ROOT%\\config\\prt\\EKT\\EKTAccountDaily.jhw",data);

    }
    /**
     * 会员卡销售打印数据
     */
//    public TParm onPrintMemDate(String casherUser,String today,String accountNo){
    public TParm onPrintMemDate(String accountSeq){
    	
		String memSql = "SELECT MEM_FEE AS AMT,PAY_TYPE01,PAY_TYPE02,PAY_TYPE03,PAY_TYPE04,PAY_TYPE05,PAY_TYPE06,PAY_TYPE07,PAY_TYPE08,PAY_TYPE09,"
				+ "PAY_TYPE10,PAY_TYPE11 FROM MEM_TRADE WHERE STATUS IN ('1','2','3','4') AND MEM_FEE >=0 AND ACCOUNT_SEQ IN ("
				+ accountSeq + ") ";
		// "GROUP BY GATHER_TYPE";
		// System.out.println("yuyuyuyumemSql memSql is :::"+memSql);
		TParm memResult = new TParm(TJDODBTool.getInstance().select(memSql));
		// 会员卡退费部分
		String memTfSql = "SELECT MEM_FEE AS AMT,PAY_TYPE01,PAY_TYPE02,PAY_TYPE03,PAY_TYPE04,PAY_TYPE05,PAY_TYPE06,PAY_TYPE07,PAY_TYPE08,PAY_TYPE09,PAY_TYPE10,PAY_TYPE11"
				+ " FROM MEM_TRADE WHERE STATUS IN ('1','2','3','4') AND MEM_FEE < 0 AND ACCOUNT_SEQ IN ("
				+ accountSeq + ") ";
		// "GROUP BY GATHER_TYPE";
		TParm memTfResult = new TParm(TJDODBTool.getInstance().select(memTfSql));
    	return onPrintMemDateDeal(memResult,memTfResult);
    }
    
    public TParm onPrintReviewMemDate(String casherUser,String accountTime){
    	
		String memSql = "SELECT MEM_FEE AS AMT,PAY_TYPE01,PAY_TYPE02,PAY_TYPE03,PAY_TYPE04,PAY_TYPE05,PAY_TYPE06,PAY_TYPE07,PAY_TYPE08,PAY_TYPE09,"
				+ " PAY_TYPE10,PAY_TYPE11 FROM MEM_TRADE " +
						" WHERE STATUS IN ('1','2','3','4') AND MEM_FEE >=0 " +
						" AND (SALE_DATE<TO_DATE('"+accountTime+"','YYYYMMDDHH24MISS') OR SALE_DATE IS NULL) " +
						" AND OPT_USER = '"+casherUser+"' " +
						" AND (ACCOUNT_FLG='N' OR ACCOUNT_FLG IS NULL) ";
		// "GROUP BY GATHER_TYPE";
		// System.out.println("yuyuyuyumemSql memSql is :::"+memSql);
		TParm memResult = new TParm(TJDODBTool.getInstance().select(memSql));
		// 会员卡退费部分
		String memTfSql = "SELECT MEM_FEE AS AMT,PAY_TYPE01,PAY_TYPE02,PAY_TYPE03,PAY_TYPE04,PAY_TYPE05,PAY_TYPE06,PAY_TYPE07,PAY_TYPE08,PAY_TYPE09,PAY_TYPE10,PAY_TYPE11"
				+ " FROM MEM_TRADE " 
				+ " WHERE STATUS IN ('1','2','3','4') AND MEM_FEE < 0 " 
				+ " AND (SALE_DATE<TO_DATE('"+accountTime+"','YYYYMMDDHH24MISS') OR SALE_DATE IS NULL) " +
				" AND OPT_USER = '"+casherUser+"' " +
				" AND (ACCOUNT_FLG='N' OR ACCOUNT_FLG IS NULL) ";
		// "GROUP BY GATHER_TYPE";
		TParm memTfResult = new TParm(TJDODBTool.getInstance().select(memTfSql));
    	return onPrintMemDateDeal(memResult,memTfResult);
    }
    
	public TParm onPrintMemDateDeal(TParm memResult, TParm memTfResult) {
		TParm result = new TParm();
		double inMem = 0.00;// 会员卡总金额（收入）
		double outMem = 0.00;// 会员卡总金额(退费)
		double inXj = 0.00;// 现金支付
		double inSk = 0.00;// 刷卡
		double inZp = 0.00;// 支票
		double inGs = 0.00;// 工商圈存机
		double inHp = 0.00;// 汇票
		double inYsk = 0.00;// 应收款
		double inLpk = 0.00;// 礼品卡
		double inXjzkq = 0.00;// 现金折扣券
		double inWx = 0.00;// 微信
		double inZfb = 0.00;// 支付宝
		double inEkt = 0.00;// 医疗卡
		// double inEktCard = 0.00;//医疗卡
		// double inInsCard = 0.00;//医保卡
		double outXj = 0.00;// 现金支付
		double outSk = 0.00;// 刷卡
		double outZp = 0.00;// 支票
		double outGs = 0.00;// 工商圈存机
		double outHp = 0.00;// 汇票
		double outYsk = 0.00;// 医院垫付
		double outLpk = 0.00;// 礼品卡
		double outXjzkq = 0.00;// 现金折扣券
		double outWx = 0.00;// 微信
		double outZfb = 0.00;// 微信
		double outEkt = 0.00;// 医疗卡
		// double outEktCard = 0.00;//医疗卡
		// double outInsCard = 0.00;//医保卡
		if (memResult.getCount() > 0) {
			for (int i = 0; i < memResult.getCount(); i++) {
				inXj += memResult.getDouble("PAY_TYPE01", i);// 现金
				inSk += memResult.getDouble("PAY_TYPE02", i);// 刷卡
				inZp += memResult.getDouble("PAY_TYPE06", i);// 汇票3
				inGs += memResult.getDouble("PAY_TYPE08", i);// 应收款
				inHp += memResult.getDouble("PAY_TYPE03", i);// 礼品卡5
				inYsk += memResult.getDouble("PAY_TYPE04", i);// 医院垫付
				inLpk += memResult.getDouble("PAY_TYPE05", i);// 现金折扣券7
				inXjzkq += memResult.getDouble("PAY_TYPE07", i);
				inWx += memResult.getDouble("PAY_TYPE09", i);
				inZfb += memResult.getDouble("PAY_TYPE10", i);
				inEkt += memResult.getDouble("PAY_TYPE11", i);
				inMem += memResult.getDouble("AMT", i);
			}
			// inMem = inXj+inSk +inZp +inGs +inHp +inYsk +inLpk +inXjzkq
			// +inEktCard +inInsCard;//会员卡收入总金额
			result.setData("IN_CASH1", inXj);// 现金
			result.setData("IN_B1", inZp);// 支票
			result.setData("IN_CARD1", inSk);// 刷卡
			result.setData("IN_LPK1", inLpk);// 礼品卡
			result.setData("IN_XJDYQ1", inXjzkq);// 现金抵用券
			result.setData("IN_WX1", inWx);// 微信
			result.setData("IN_ZFB1", inZfb);// 支付宝
			result.setData("IN_EKT1", inEkt);// 医疗卡
			result.setData("IN_C41", inYsk);// 医院垫付
			result.setData("PAY_MEDICAL_QTY1", memResult.getCount());// 收入笔数
		}

		if (memTfResult.getCount() > 0) {
			for (int i = 0; i < memTfResult.getCount(); i++) {
				outXj += memTfResult.getDouble("PAY_TYPE01", i);
				outSk += memTfResult.getDouble("PAY_TYPE02", i);
				outZp += memTfResult.getDouble("PAY_TYPE06", i);
				outGs += memTfResult.getDouble("PAY_TYPE08", i);
				outHp += memTfResult.getDouble("PAY_TYPE03", i);
				outYsk += memTfResult.getDouble("PAY_TYPE04", i);
				outLpk += memTfResult.getDouble("PAY_TYPE05", i);
				outXjzkq += memTfResult.getDouble("PAY_TYPE07", i);
				outWx += memTfResult.getDouble("PAY_TYPE09", i);
				outZfb += memTfResult.getDouble("PAY_TYPE10", i);
				outEkt += memTfResult.getDouble("PAY_TYPE11", i);
				outMem += memTfResult.getDouble("AMT", i);
			}
			result.setData("OUT_CASH1", outXj);// 现金
			result.setData("OUT_B1", outZp);// 支票
			result.setData("OUT_CARD1", outSk);// 刷卡
			result.setData("OUT_LPK1", outLpk);// 礼品卡
			result.setData("OUT_XJDYQ1", outXjzkq);// 现金抵用券
			result.setData("OUT_WX1", outWx);// 微信
			result.setData("OUT_ZFB1", outZfb);// 支付宝
			result.setData("OUT_EKT1", outEkt);// 医疗卡
			result.setData("OUT_C41", outYsk);// 医院垫付
			// result.setData("NPAY_MEDICAL_QTY1",
			// memTfResult.getCount());//退款笔数
			result.setData("NPAY_MEDICAL_QTY1", 5);// 退款笔数
		}
		result.setData("PAY_MEDICAL_ATM_TOTAL1", inMem); // 收入总金额
		result.setData("NPAY_MEDICAL_AMT1", outMem); // 退费总金额
		result.setData("TOT_MEDICAL_QTY1", memResult.getCount()
				+ memTfResult.getCount());// 总笔数
		result.setData("TOT_CASH1", inXj + outXj);// 现总金额
		result.setData("TOT_B1", inZp + outZp);// 支票现金总金额
		result.setData("TOT_CARD1", inSk + outSk);// 刷卡总金额
		result.setData("TOT_LPK1", inLpk + outLpk);// 礼品卡总金额
		result.setData("TOT_XJDYQ1", inXjzkq + outXjzkq);// 现金抵用券总金额
		result.setData("TOT_WX1", inWx + outWx);// 微信总金额
		result.setData("TOT_ZFB1", inZfb + outZfb);// 支付宝总金额
		result.setData("TOT_EKT1", inEkt + outEkt);// 医疗卡总金额
		result.setData("TOT_C41", inYsk + outYsk);// 医院垫付总金额
		result.setData("TOT_MEDICAL_AMT1", inMem + outMem);// 总金额
		return result;
	}
    
    /**
     * 套餐销售打印数据
     */
//    public TParm onPrintMemDate(String casherUser,String today,String accountNo){
	public TParm onPrintPakgeDate(String accountSeq) {

		String memSql = "SELECT AR_AMT AS AMT,PAY_TYPE01,PAY_TYPE02,PAY_TYPE03,PAY_TYPE04,PAY_TYPE05,PAY_TYPE06,PAY_TYPE07,PAY_TYPE08,PAY_TYPE09,PAY_TYPE10,PAY_TYPE11,PAY_MEDICAL_CARD"
				+ " FROM MEM_PACKAGE_TRADE_M WHERE AR_AMT >=0 AND ACCOUNT_SEQ IN ("
				+ accountSeq + ") ";
		// "GROUP BY GATHER_TYPE";
		TParm memResult = new TParm(TJDODBTool.getInstance().select(memSql));
		// 套餐退费部分
		String memTfSql = "SELECT AR_AMT AS AMT,PAY_TYPE01,PAY_TYPE02,PAY_TYPE03,PAY_TYPE04,PAY_TYPE05,PAY_TYPE06,PAY_TYPE07,PAY_TYPE08,PAY_TYPE09,PAY_TYPE10,PAY_TYPE11,PAY_MEDICAL_CARD"
				+ " FROM MEM_PACKAGE_TRADE_M WHERE AR_AMT < 0 AND ACCOUNT_SEQ IN ("
				+ accountSeq + ") ";
		// "GROUP BY GATHER_TYPE";
		TParm memTfResult = new TParm(TJDODBTool.getInstance().select(memTfSql));
		return onPrintPakgeDateDeal(memResult, memTfResult);

	}
	public TParm onPrintReviewPakgeDate(String casherUser,String accountTime) {

		String memSql = "SELECT AR_AMT AS AMT,PAY_TYPE01,PAY_TYPE02,PAY_TYPE03,PAY_TYPE04,PAY_TYPE05,PAY_TYPE06,PAY_TYPE07,PAY_TYPE08,PAY_TYPE09,PAY_TYPE10,PAY_TYPE11,PAY_MEDICAL_CARD"
				+ " FROM MEM_PACKAGE_TRADE_M WHERE AR_AMT >=0 " 
				+ " AND BILL_DATE<TO_DATE('"+accountTime+"','YYYYMMDDHH24MISS' )  " 
				+ " AND CASHIER_CODE = '"+casherUser+"' " 
				+ " AND (ACCOUNT_FLG='N' OR ACCOUNT_FLG IS NULL)";
		// "GROUP BY GATHER_TYPE";
		TParm memResult = new TParm(TJDODBTool.getInstance().select(memSql));
		// 套餐退费部分
		String memTfSql = "SELECT AR_AMT AS AMT,PAY_TYPE01,PAY_TYPE02,PAY_TYPE03,PAY_TYPE04,PAY_TYPE05,PAY_TYPE06,PAY_TYPE07,PAY_TYPE08,PAY_TYPE09,PAY_TYPE10,PAY_TYPE11,PAY_MEDICAL_CARD"
				+ " FROM MEM_PACKAGE_TRADE_M WHERE AR_AMT < 0 " 
				+ " AND BILL_DATE<TO_DATE('"+accountTime+"','YYYYMMDDHH24MISS' )  " 
				+ " AND CASHIER_CODE = '"+casherUser+"' " 
				+ " AND (ACCOUNT_FLG='N' OR ACCOUNT_FLG IS NULL)";
		// "GROUP BY GATHER_TYPE";
		TParm memTfResult = new TParm(TJDODBTool.getInstance().select(memTfSql));
		return onPrintPakgeDateDeal(memResult, memTfResult);

	}
    
	public TParm onPrintPakgeDateDeal(TParm memResult, TParm memTfResult) {
		TParm result = new TParm();
		double inMem = 0.00;// 会员卡总金额（收入）
		double inXj = 0.00;// 现金支付
		double inSk = 0.00;// 刷卡
		double inZp = 0.00;// 支票
		double inGs = 0.00;// 医院垫付
		double inHp = 0.00;// 汇票
		double inYsk = 0.00;// 应收款
		double inLpk = 0.00;// 礼品卡
		double inWx = 0.00;// 微信
		double inZfb = 0.00;// 支付宝
		double inEkt = 0.00;// 医疗卡
		double inXjzkq = 0.00;// 现金折扣券
		// double inEktCard = 0.00;//医疗卡
		// double inInsCard = 0.00;//医保卡
		double outMem = 0.00;// 会员卡总金额(退费)
		double outXj = 0.00;// 现金支付
		double outSk = 0.00;// 刷卡
		double outZp = 0.00;// 支票
		double outGs = 0.00;// 医院垫付
		double outHp = 0.00;// 汇票
		double outYsk = 0.00;// 应收款
		double outLpk = 0.00;// 礼品卡
		double outWx = 0.00;// 微信
		double outZfb = 0.00;// 支付宝
		double outEkt = 0.00;// 医疗卡
		double outXjzkq = 0.00;// 现金折扣券
		// double outEktCard = 0.00;//医疗卡
		// double outInsCard = 0.00;//医保卡
		if (memResult.getCount() > 0) {
			for (int i = 0; i < memResult.getCount(); i++) {
				inXj += memResult.getDouble("PAY_TYPE01", i);
				inSk += memResult.getDouble("PAY_TYPE02", i);
				inZp += memResult.getDouble("PAY_TYPE03", i);
				inGs += memResult.getDouble("PAY_TYPE04", i);
				inHp += memResult.getDouble("PAY_TYPE05", i);
				inYsk += memResult.getDouble("PAY_TYPE06", i);
				inLpk += memResult.getDouble("PAY_TYPE07", i);
				inXjzkq += memResult.getDouble("PAY_TYPE08", i);
				inWx += memResult.getDouble("PAY_TYPE09", i);
				inZfb += memResult.getDouble("PAY_TYPE10", i);
				inEkt += memResult.getDouble("PAY_TYPE11", i);
				inEkt += memResult.getDouble("PAY_MEDICAL_CARD", i);
				inMem += memResult.getDouble("AMT", i);
			}
			// inMem += inXj +inSk +inZp +inGs +inHp +inYsk +inLpk +inXjzkq
			// +inEktCard +inInsCard;//会员卡收入总金额
			result.setData("IN_CASH2", inXj);// 现金
			// result.setData("IN_B2", inZp);//支票
			result.setData("IN_B2", inYsk);// 支票
			result.setData("IN_CARD2", inSk);// 刷卡
			// result.setData("IN_LPK2", inLpk);//礼品卡
			result.setData("IN_LPK2", inHp);// 礼品卡
			// result.setData("IN_XJDYQ2", inXjzkq);//现金抵用券
			result.setData("IN_XJDYQ2", inLpk);// 现金抵用券
			result.setData("IN_WX2", inWx);// 微信
			result.setData("IN_ZFB2", inZfb);// 支付宝
			result.setData("IN_EKT2", inEkt);// 支付宝
			result.setData("IN_C42", inGs);
			result.setData("PAY_MEDICAL_QTY2", memResult.getCount());// 收入笔数
		}

		if (memTfResult.getCount() > 0) {
			for (int i = 0; i < memTfResult.getCount(); i++) {
				outXj += memTfResult.getDouble("PAY_TYPE01", i);
				outSk += memTfResult.getDouble("PAY_TYPE02", i);
				outZp += memTfResult.getDouble("PAY_TYPE03", i);
				outGs += memTfResult.getDouble("PAY_TYPE04", i);
				outHp += memTfResult.getDouble("PAY_TYPE05", i);
				outYsk += memTfResult.getDouble("PAY_TYPE06", i);
				outLpk += memTfResult.getDouble("PAY_TYPE07", i);
				outXjzkq += memTfResult.getDouble("PAY_TYPE08", i);
				// outEktCard += memTfResult.getDouble("PAY_TYPE09", i);
				// outInsCard += memTfResult.getDouble("PAY_TYPE10", i);
				outWx += memTfResult.getDouble("PAY_TYPE09", i);
				outZfb += memTfResult.getDouble("PAY_TYPE10", i);
				outEkt += memTfResult.getDouble("PAY_TYPE11", i);
				outEkt += memTfResult.getDouble("PAY_MEDICAL_CARD", i);
				outMem += memTfResult.getDouble("AMT", i);
			}
			// outMem =outXj +outSk +outZp +outGs +outHp +outYsk +outLpk
			// +outXjzkq +outEktCard +outInsCard;//会员卡收入总金额
			result.setData("OUT_CASH2", outXj);// 现金
			// result.setData("OUT_B2", outXj);//支票
			result.setData("OUT_B2", outYsk);// 支票
			result.setData("OUT_CARD2", outSk);// 刷卡
			// result.setData("OUT_LPK2", outLpk);//礼品卡
			result.setData("OUT_LPK2", outHp);// 礼品卡
			// result.setData("OUT_XJDYQ2", outXjzkq);//现金抵用券
			result.setData("OUT_XJDYQ2", outLpk);// 现金抵用券
			result.setData("OUT_WX2", outWx);// 微信
			result.setData("OUT_ZFB2", outZfb);// 支付宝
			result.setData("OUT_EKT2", outEkt);// 医疗卡
			result.setData("OUT_C42", outGs);
			result.setData("NPAY_MEDICAL_QTY2", memTfResult.getCount());// 退款笔数
		}
		result.setData("PAY_MEDICAL_ATM_TOTAL2", inMem); // 收入总金额
		result.setData("NPAY_MEDICAL_AMT2", outMem); // 退费总金额
		result.setData("TOT_MEDICAL_QTY2", memResult.getCount()
				+ memTfResult.getCount());// 总笔数
		// System.out.println("套餐   套餐  退款 is：："+outMem);
		result.setData("TOT_CASH2", inXj + outXj);// 现总金额
		// result.setData("TOT_B2", inZp+outZp);//支票现金总金额
		result.setData("TOT_B2", inYsk + outYsk);// 支票现金总金额
		result.setData("TOT_CARD2", inSk + outSk);// 刷卡总金额
		// result.setData("TOT_LPK2", inLpk+outLpk);//礼品卡总金额
		result.setData("TOT_LPK2", inHp + outHp);// 礼品卡总金额
		// result.setData("TOT_XJDYQ2", inXjzkq+outXjzkq);//现金抵用券总金额
		result.setData("TOT_XJDYQ2", inLpk + outLpk);// 现金抵用券总金额
		result.setData("TOT_WX2", inWx + outWx);// 微信总金额
		result.setData("TOT_ZFB2", inZfb + outZfb);// 支付宝总金额
		result.setData("TOT_EKT2", inEkt + outEkt);// 支付宝总金额
		result.setData("TOT_C42", inGs + outGs);// 支付宝总金额
		result.setData("TOT_MEDICAL_AMT2", inMem + outMem);// 总金额
		return result;
	}
    
    /**
     * 礼品卡销售打印数据
     */
//    public TParm onPrintMemDate(String casherUser,String today,String accountNo){
	public TParm onPrintLpkDate(String accountSeq) {
		
		String memSql = "SELECT AR_AMT AS AMT,PAY_TYPE01,PAY_TYPE02,PAY_TYPE03,PAY_TYPE04,PAY_TYPE05,PAY_TYPE06,PAY_TYPE07,PAY_TYPE08,PAY_TYPE09,PAY_TYPE10,0 AS PAY_TYPE11"
				+ " FROM MEM_GIFTCARD_TRADE_M WHERE AR_AMT >=0 AND ACCOUNT_SEQ IN ("
				+ accountSeq + ") ";
		// "GROUP BY GATHER_TYPE";
		TParm memResult = new TParm(TJDODBTool.getInstance().select(memSql));
		// 礼品卡退费部分
		String memTfSql = "SELECT AR_AMT AS AMT,PAY_TYPE01,PAY_TYPE02,PAY_TYPE03,PAY_TYPE04,PAY_TYPE05,PAY_TYPE06,PAY_TYPE07,PAY_TYPE08,PAY_TYPE09,PAY_TYPE10,0 AS PAY_TYPE11"
				+ " FROM MEM_GIFTCARD_TRADE_M WHERE AR_AMT < 0 AND ACCOUNT_SEQ IN ("
				+ accountSeq + ") ";
		// "GROUP BY GATHER_TYPE";
		TParm memTfResult = new TParm(TJDODBTool.getInstance().select(memTfSql));
		return onPrintLpkDateDeal(memResult, memTfResult);

	}
	public TParm onPrintReviewLpkDate(String casherUser,String accountTime) {
		
		String memSql = "SELECT AR_AMT AS AMT,PAY_TYPE01,PAY_TYPE02,PAY_TYPE03,PAY_TYPE04,PAY_TYPE05,PAY_TYPE06,PAY_TYPE07,PAY_TYPE08,PAY_TYPE09,PAY_TYPE10,0 AS PAY_TYPE11"
				+ " FROM MEM_GIFTCARD_TRADE_M WHERE AR_AMT >=0 " 
				+ " AND OPT_DATE<TO_DATE('"+accountTime+"','YYYYMMDDHH24MISS') " 
				+ " AND OPT_USER = '"+casherUser+"'  " 
				+ " AND (ACCOUNT_FLG='N' OR ACCOUNT_FLG IS NULL)";
		// "GROUP BY GATHER_TYPE";
		TParm memResult = new TParm(TJDODBTool.getInstance().select(memSql));
		// 礼品卡退费部分
		String memTfSql = "SELECT AR_AMT AS AMT,PAY_TYPE01,PAY_TYPE02,PAY_TYPE03,PAY_TYPE04,PAY_TYPE05,PAY_TYPE06,PAY_TYPE07,PAY_TYPE08,PAY_TYPE09,PAY_TYPE10,0 AS PAY_TYPE11"
				+ " FROM MEM_GIFTCARD_TRADE_M WHERE AR_AMT < 0 "
				+ " AND OPT_DATE<TO_DATE('"+accountTime+"','YYYYMMDDHH24MISS') " 
				+ " AND OPT_USER = '"+casherUser+"'  " 
				+ " AND (ACCOUNT_FLG='N' OR ACCOUNT_FLG IS NULL)";
		// "GROUP BY GATHER_TYPE";
		TParm memTfResult = new TParm(TJDODBTool.getInstance().select(memTfSql));
		return onPrintLpkDateDeal(memResult, memTfResult);

	}

	public TParm onPrintLpkDateDeal(TParm memResult, TParm memTfResult) {
		TParm result = new TParm();
		double inMem = 0.00;// 会员卡总金额（收入）
		double outMem = 0.00;// 会员卡总金额(退费)
		double inXj = 0.00;// 现金支付
		double inSk = 0.00;// 刷卡
		double inZp = 0.00;// 支票
		double inGs = 0.00;// 工商圈存机
		double inHp = 0.00;// 汇票
		double inYsk = 0.00;// 应收款
		double inLpk = 0.00;// 礼品卡
		double inXjzkq = 0.00;// 现金折扣券
		double inWx = 0.00;// 微信
		double inZfb = 0.00;// 支付宝
		double inEkt = 0.00;// 医疗卡
		// double inEktCard = 0.00;//医疗卡
		// double inInsCard = 0.00;//医保卡
		double outXj = 0.00;// 现金支付
		double outSk = 0.00;// 刷卡
		double outZp = 0.00;// 支票
		double outGs = 0.00;// 工商圈存机
		double outHp = 0.00;// 汇票
		double outYsk = 0.00;// 应收款
		double outLpk = 0.00;// 礼品卡
		double outXjzkq = 0.00;// 现金折扣券
		double outWx = 0.00;// 微信
		double outZfb = 0.00;// 支付宝
		double outEkt = 0.00;// 医疗卡
		// double outEktCard = 0.00;//医疗卡
		// double outInsCard = 0.00;//医保卡
		if (memResult.getCount() > 0) {
			for (int i = 0; i < memResult.getCount(); i++) {
				inXj += memResult.getDouble("PAY_TYPE01", i);
				inSk += memResult.getDouble("PAY_TYPE02", i);
				inZp += memResult.getDouble("PAY_TYPE06", i);
				inGs += memResult.getDouble("PAY_TYPE08", i);
				inHp += memResult.getDouble("PAY_TYPE03", i);
				inYsk += memResult.getDouble("PAY_TYPE04", i);
				inLpk += memResult.getDouble("PAY_TYPE05", i);
				inXjzkq += memResult.getDouble("PAY_TYPE07", i);
				inWx += memResult.getDouble("PAY_TYPE09", i);
				inZfb += memResult.getDouble("PAY_TYPE10", i);
				inEkt += memResult.getDouble("PAY_TYPE11", i);
				// inEktCard += memResult.getDouble("PAY_TYPE09", i);
				// inInsCard += memResult.getDouble("PAY_TYPE10", i);
				inMem += memResult.getDouble("AMT", i);
			}
			// inMem += inXj +inSk +inZp +inGs +inHp +inYsk +inLpk +inXjzkq
			// +inEktCard +inInsCard;//会员卡收入总金额
			result.setData("IN_CASH3", inXj);// 现金
			result.setData("IN_B3", inZp);// 支票
			result.setData("IN_CARD3", inSk);// 刷卡
			result.setData("IN_LPK3", inLpk);// 礼品卡
			result.setData("IN_XJDYQ3", inXjzkq);// 现金抵用券
			result.setData("IN_WX3", inWx);// 微信
			result.setData("IN_ZFB3", inZfb);// 支付宝
			result.setData("IN_EKT3", inEkt);// 医疗卡
			result.setData("IN_C43", inYsk);
			result.setData("PAY_MEDICAL_QTY3", memResult.getCount());// 收入笔数
		}

		if (memTfResult.getCount() > 0) {
			for (int i = 0; i < memTfResult.getCount(); i++) {
				outXj += memTfResult.getDouble("PAY_TYPE01", i);
				outSk += memTfResult.getDouble("PAY_TYPE02", i);
				outZp += memTfResult.getDouble("PAY_TYPE06", i);
				outGs += memTfResult.getDouble("PAY_TYPE08", i);
				outHp += memTfResult.getDouble("PAY_TYPE03", i);
				outYsk += memTfResult.getDouble("PAY_TYPE04", i);
				outLpk += memTfResult.getDouble("PAY_TYPE05", i);
				outXjzkq += memTfResult.getDouble("PAY_TYPE07", i);
				outWx += memTfResult.getDouble("PAY_TYPE09", i);
				outZfb += memTfResult.getDouble("PAY_TYPE10", i);
				outEkt += memTfResult.getDouble("PAY_TYPE11", i);
				// outEktCard += memTfResult.getDouble("PAY_TYPE09", i);
				// outInsCard += memTfResult.getDouble("PAY_TYPE10", i);
				outMem += memTfResult.getDouble("AMT", i);
			}
			// outMem += outXj +outSk +outZp +outGs +outHp +outYsk +outLpk
			// +outXjzkq +outEktCard +outInsCard;//会员卡收入总金额
			result.setData("OUT_CASH3", outXj);// 现金
			result.setData("OUT_B3", outZp);// 支票
			result.setData("OUT_CARD3", outSk);// 刷卡
			result.setData("OUT_LPK3", outLpk);// 礼品卡
			result.setData("OUT_XJDYQ3", outXjzkq);// 现金抵用券
			result.setData("OUT_WX3", outWx);// 微信
			result.setData("OUT_ZFB3", outZfb);// 支付宝
			result.setData("OUT_ZFB3", outEkt);// 医疗卡
			result.setData("OUT_C43", outYsk);// 医院垫付
			result.setData("NPAY_MEDICAL_QTY3", memTfResult.getCount());// 退款笔数
		}
		result.setData("PAY_MEDICAL_ATM_TOTAL3", inMem); // 收入总金额
		result.setData("NPAY_MEDICAL_AMT3", outMem); // 退费总金额
		result.setData("TOT_MEDICAL_QTY3", memResult.getCount()
				+ memTfResult.getCount());// 总笔数
		result.setData("TOT_CASH3", inXj + outXj);// 现总金额
		result.setData("TOT_B3", inZp + outZp);// 支票现金总金额
		result.setData("TOT_CARD3", inSk + outSk);// 刷卡总金额
		result.setData("TOT_LPK3", inLpk + outLpk);// 礼品卡总金额
		result.setData("TOT_XJDYQ3", inXjzkq + outXjzkq);// 现金抵用券总金额
		result.setData("TOT_WX3", inWx + outWx);// 微信总金额
		result.setData("TOT_ZFB3", inZfb + outZfb);// 支付宝总金额
		result.setData("TOT_EKT3", inEkt + outEkt);// 医疗卡总金额
		result.setData("TOT_C43", inYsk + outYsk);// 医院垫付总金额
		result.setData("TOT_MEDICAL_AMT3", inMem + outMem);// 总金额
		return result;
	}
    
    /**
     * table监听checkBox事件
     * @param obj Object
     * @return boolean
     */
    public boolean onTableComponent(Object obj) {
        accountSeq = new String();
        TTable table = (TTable) obj;
        table.acceptText();
        TParm tableParm = table.getParmValue();
        int allRow = table.getRowCount();
        StringBuffer allSeq = new StringBuffer();
        for (int i = 0; i < allRow; i++) {
            String seq = "";
            if ("Y".equals(tableParm.getValue("FLG", i))) {
                seq = tableParm.getValue("ACCOUNT_SEQ", i);
                if (allSeq.length() > 0)
                    allSeq.append(",");
                allSeq.append(seq);
            }
        }
        accountSeq = allSeq.toString();
        return true;
    }
    
    /**
     * 全选事件
     */
    public void onSelectAll() {
        accountSeq = new String();
        StringBuffer allSeq = new StringBuffer();
        String select = getValueString("SELECT");
        TTable table = (TTable)this.getComponent("Table");
        table.acceptText();
        TParm parm = table.getParmValue();
        int count = parm.getCount();
        for (int i = 0; i < count; i++) {
            String seq = "";
            parm.setData("FLG", i, select);
            seq = parm.getValue("ACCOUNT_SEQ", i);
               if (allSeq.length() > 0)
                   allSeq.append(",");
               allSeq.append(seq);
        }
        accountSeq = allSeq.toString();
        table.setParmValue(parm);
    }
    /**
     * 获取打印时间
     * @param startDate
     * @param enddate
     * @return
     */
    public String getAccntDate(String startDate,String enddate){
    	startDate = startDate.substring(0, 4)+"/"+startDate.substring(5, 7)+"/"+startDate.substring(8, 19);
    	enddate = enddate.substring(0, 4)+"/"+enddate.substring(5, 7)+"/"+enddate.substring(8, 19);
    	return startDate + " ~ " + enddate;
    }
}