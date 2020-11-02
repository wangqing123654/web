package jdo.opb;

import java.sql.Timestamp;

import jdo.bil.BIL;
import jdo.ibs.IBSBilldTool;
import jdo.ibs.IBSBillmTool;
import jdo.sys.SystemTool;

import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

public class OPBCTZTool extends TJDOTool {
	/**
     * 实例
     */
    public static OPBCTZTool instanceObject;
    /**
     * 得到实例
     * @return OPBCTZTool
     */
    public static OPBCTZTool getInstance() {
        if (instanceObject == null)
            instanceObject = new OPBCTZTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public OPBCTZTool() {
        setModuleName("opb\\OPBCTZModule.x");
        onInit();
    }
    
    /**
	 * 修改身份
	 * 
	 * @author caowl
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm updBill(TParm parm, TConnection connection) {

		
		TParm result = new TParm();

		// 得到系统时间
		Timestamp sysDate = SystemTool.getInstance().getDate();

		//得到界面传过来的参数
		String caseNo = parm.getValue("CASE_NO");
		String optUser = parm.getValue("OPT_USER");
		//System.out.println("------>"+optUser);
		String optTerm = parm.getValue("OPT_TERM");
		//新的身份
		String CTZ1 = parm.getValue("CTZ1_CODE");
		String CTZ2 = parm.getValue("CTZ2_CODE");
		String CTZ3 = parm.getValue("CTZ3_CODE");

		//根据caseNo查询病人信息
		TParm selPatientParm = new TParm();
		selPatientParm = selBycaseNo(caseNo);
		
		//更新REG_PATADM修改身份
		String updSql = "UPDATE REG_PATADM SET CTZ1_CODE = '"+CTZ1+"' ,CTZ2_CODE =  '"+CTZ2+"',CTZ3_CODE = '"+CTZ3+"' ,OPT_USER = '"+optUser+"', OPT_DATE = SYSDATE ,OPT_TERM = '"+optTerm+"'  WHERE CASE_NO = '"+caseNo+"'";
		
		result = new TParm(TJDODBTool.getInstance().update(updSql));
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}
		//更新OPD_ORDER表的身份信息
		//step1  查询该case_no下的所有信息
		String sqlOpdOrder = "SELECT * FROM OPD_ORDER  WHERE CASE_NO = '"+caseNo+"' AND BILL_FLG = 'N' AND MEM_PACKAGE_ID IS NULL"; //add by huangtt 20160324 AND MEM_PACKAGE_ID IS NULL 将套餐医嘱除去
		
		//System.out.println("查询sql"+sqlOpdOrder);
		TParm parmOpdOrder  = new TParm(TJDODBTool.getInstance().select(sqlOpdOrder));
		
		//System.out.println("parmOpdOrder------>"+parmOpdOrder);
	
        for(int i = 0;i<parmOpdOrder.getCount();i++){
        	//System.out.println("第"+i+"次循环");
        	//step2 插入历史表
        	 String date = SystemTool.getInstance().getDate().toString().replace("-", "").replace(" ", "").replace(":", "").substring(0,14);
        	  
        	TParm insertParm = new TParm();
        	String case_no = parmOpdOrder.getValue("CASE_NO",i);
        	insertParm.setData("CASE_NO",case_no == null ? new TNull(String.class) : case_no);
        	String rx_no = parmOpdOrder.getValue("RX_NO",i);
        	insertParm.setData("RX_NO",rx_no == null ? new TNull(String.class) : rx_no);
        	int seq_no = parmOpdOrder.getInt("SEQ_NO",i);
        	insertParm.setData("SEQ_NO",seq_no);
        	//String dc_order_date = parmOpdOrder.getValue("DC_ORDER_DATE",i);
        	insertParm.setData("DC_ORDER_DATE",date);
        	String prest_no = parmOpdOrder.getValue("PRESRT_NO",i);
        	insertParm.setData("PRESRT_NO",prest_no == null ? new TNull(String.class) : prest_no);
        	String region_code = parmOpdOrder.getValue("REGION_CODE",i);
        	insertParm.setData("REGION_CODE",region_code == null ? new TNull(String.class) : region_code);
        	String mr_no = parmOpdOrder.getValue("MR_NO",i);
        	insertParm.setData("MR_NO",mr_no == null ? new TNull(String.class) : mr_no);
        	String adm_type = parmOpdOrder.getValue("ADM_TYPE",i);
        	insertParm.setData("ADM_TYPE",adm_type == null ? new TNull(String.class) : adm_type);
        	String rx_type = parmOpdOrder.getValue("RX_TYPE",i);
        	insertParm.setData("RX_TYPE",rx_type == null ? new TNull(String.class) : rx_type);
        	String release_flg = parmOpdOrder.getValue("RELEASE_FLG",i);
        	insertParm.setData("RELEASE_FLG",release_flg == null ? new TNull(String.class) : release_flg);
        	String linkmain_flg = parmOpdOrder.getValue("LINKMAIN_FLG",i);
        	insertParm.setData("LINKMAIN_FLG",linkmain_flg == null ? new TNull(String.class) : linkmain_flg);
        	String link_no = parmOpdOrder.getValue("LINK_NO",i);
        	insertParm.setData("LINK_NO",link_no == null ? new TNull(String.class) : link_no);
        	String order_code = parmOpdOrder.getValue("ORDER_CODE",i);
        	insertParm.setData("ORDER_CODE",order_code == null ? new TNull(String.class) : order_code);
        	String order_desc = parmOpdOrder.getValue("ORDER_DESC",i);
        	insertParm.setData("ORDER_DESC",order_desc == null ? new TNull(String.class) : order_desc);
        	String goods_desc = parmOpdOrder.getValue("GOODS_DESC",i);
        	insertParm.setData("GOODS_DESC",goods_desc == null ? new TNull(String.class) : goods_desc);
        	String specification = parmOpdOrder.getValue("SPECIFICATION",i);
        	insertParm.setData("SPECIFICATION",specification == null ? new TNull(String.class) : specification);
        	String order_cat1_code = parmOpdOrder.getValue("ORDER_CAT1_CODE",i);
        	insertParm.setData("ORDER_CAT1_CODE",order_cat1_code == null ? new TNull(String.class) : order_cat1_code);
        	double medi_qty = parmOpdOrder.getDouble("MEDI_QTY",i);
        	insertParm.setData("MEDI_QTY",medi_qty);
        	String medi_unit = parmOpdOrder.getValue("MEDI_UNIT",i);
        	insertParm.setData("MEDI_UNIT",medi_unit == null ? new TNull(String.class) : medi_unit);
        	String freq_code = parmOpdOrder.getValue("FREQ_CODE",i);
        	insertParm.setData("FREQ_CODE",freq_code == null ? new TNull(String.class) : freq_code);
        	String route_code = parmOpdOrder.getValue("ROUTE_CODE",i);
        	insertParm.setData("ROUTE_CODE",route_code == null ? new TNull(String.class) : route_code);
        	double take_days = parmOpdOrder.getDouble("TAKE_DAYS",i);
        	insertParm.setData("TAKE_DAYS",take_days);
        	double dosage_qty = parmOpdOrder.getDouble("DOSAGE_QTY",i);
        	insertParm.setData("DOSAGE_QTY",dosage_qty);
        	String dosage_unit = parmOpdOrder.getValue("DOSAGE_UNIT",i);
        	insertParm.setData("DOSAGE_UNIT",dosage_unit == null ? new TNull(String.class) : dosage_unit);
        	double dispense_qty = parmOpdOrder.getDouble("DISPENSE_QTY",i);
        	insertParm.setData("DISPENSE_QTY",dispense_qty);
        	String dispense_unit = parmOpdOrder.getValue("DISPENSE_UNIT",i);
        	insertParm.setData("DISPENSE_UNIT",dispense_unit == null ? new TNull(String.class) : dispense_unit);
        	String givebox_flg = parmOpdOrder.getValue("GIVEBOX_FLG",i);
        	insertParm.setData("GIVEBOX_FLG",givebox_flg == null ? new TNull(String.class) : givebox_flg);
        	double own_price = parmOpdOrder.getDouble("OWN_PRICE",i);
        	insertParm.setData("OWN_PRICE",own_price);
        	double nhi_price = parmOpdOrder.getDouble("NHI_PRICE",i);
        	insertParm.setData("NHI_PRICE",nhi_price );
        	double discount_rate = parmOpdOrder.getDouble("DISCOUNT_RATE",i);
        	insertParm.setData("DISCOUNT_RATE",discount_rate);
        	double own_amt_old = parmOpdOrder.getDouble("OWN_AMT",i);
        	insertParm.setData("OWN_AMT",own_amt_old);
        	double ar_amt_old = parmOpdOrder.getDouble("AR_AMT",i);
        	insertParm.setData("AR_AMT",ar_amt_old);
        	String dr_note = parmOpdOrder.getValue("DR_NOTE",i);
        	insertParm.setData("DR_NOTE",dr_note == null ? new TNull(String.class) : dr_note);
        	String ns_note = parmOpdOrder.getValue("NS_NOTE",i);
        	insertParm.setData("NS_NOTE",ns_note == null ? new TNull(String.class) : ns_note);
        	String dr_code = parmOpdOrder.getValue("DR_CODE",i);
        	insertParm.setData("DR_CODE",dr_code == null ? new TNull(String.class) : dr_code);
            Timestamp order_date = parmOpdOrder.getTimestamp("ORDER_DATE",i);       
        	insertParm.setData("ORDER_DATE",order_date == null ? new TNull(Timestamp.class)  : order_date);
        	String dept_code= parmOpdOrder.getValue("DEPT_CODE",i);
        	insertParm.setData("DEPT_CODE",dept_code == null ? new TNull(String.class) : dept_code);
        	String dc_dr_code = parmOpdOrder.getValue("DC_DR_CODE",i);
        	insertParm.setData("DC_DR_CODE",dc_dr_code == null ? new TNull(String.class) : dc_dr_code);
        	String dc_dept_code = parmOpdOrder.getValue("DC_DEPT_CODE",i);
        	insertParm.setData("DC_DEPT_CODE",dc_dept_code == null ? new TNull(String.class) : dc_dept_code);
        	String exec_dept_code = parmOpdOrder.getValue("EXEC_DEPT_CODE",i);
        	insertParm.setData("EXEC_DEPT_CODE",exec_dept_code == null ? new TNull(String.class) : exec_dept_code);
        	String exec_dr_code = parmOpdOrder.getValue("EXEC_DR_CODE",i);
        	insertParm.setData("EXEC_DR_CODE",exec_dr_code == null ? new TNull(String.class) : exec_dr_code);
        	String setmain_flg = parmOpdOrder.getValue("SETMAIN_FLG",i);
        	insertParm.setData("SETMAIN_FLG",setmain_flg == null ? new TNull(String.class) : setmain_flg);
        	int orderset_group_no = parmOpdOrder.getInt("ORDERSET_GROUP_NO",i);
        	insertParm.setData("ORDERSET_GROUP_NO",orderset_group_no);
        	String orderset_code = parmOpdOrder.getValue("ORDERSET_CODE",i);
        	insertParm.setData("ORDERSET_CODE",orderset_code == null ? new TNull(String.class) : orderset_code);
        	String hide_flg = parmOpdOrder.getValue("HIDE_FLG",i);
        	insertParm.setData("HIDE_FLG",hide_flg == null ? new TNull(String.class) : hide_flg);
        	String rpttype_code = parmOpdOrder.getValue("RPTTYPE_CODE",i);
        	insertParm.setData("RPTTYPE_CODE",rpttype_code == null ? new TNull(String.class) : rpttype_code);
        	String optitem_code = parmOpdOrder.getValue("OPTITEM_CODE",i);
        	insertParm.setData("OPTITEM_CODE",optitem_code == null ? new TNull(String.class) : optitem_code);
        	String dev_code = parmOpdOrder.getValue("DEV_CODE",i);
        	insertParm.setData("DEV_CODE",dev_code == null ? new TNull(String.class) : dev_code);
        	String mr_code = parmOpdOrder.getValue("MR_CODE",i);
        	insertParm.setData("MR_CODE",mr_code == null ? new TNull(String.class) : mr_code);
        	int file_no = parmOpdOrder.getInt("FILE_NO",i);
        	insertParm.setData("FILE_NO",file_no);
        	String degree_code = parmOpdOrder.getValue("DEGREE_CODE",i);
        	insertParm.setData("DEGREE_CODE",degree_code == null ? new TNull(String.class) : degree_code);
        	String urgent_flg = parmOpdOrder.getValue("URGENT_FLG",i);
        	insertParm.setData("URGENT_FLG",urgent_flg == null ? new TNull(String.class) : urgent_flg);
        	String inspay_type = parmOpdOrder.getValue("INSPAY_TYPE",i);
        	insertParm.setData("INSPAY_TYPE",inspay_type == null ? new TNull(String.class) : inspay_type);
        	String pha_type = parmOpdOrder.getValue("PHA_TYPE",i);
        	insertParm.setData("PHA_TYPE",pha_type == null ? new TNull(String.class) : pha_type);
        	String dose_type = parmOpdOrder.getValue("DOSE_TYPE",i);
        	insertParm.setData("DOSE_TYPE",dose_type == null ? new TNull(String.class) : dose_type);
        	
        	String expensive_flg = parmOpdOrder.getValue("EXPENSIVE_FLG",i);
        	
        	insertParm.setData("EXPENSIVE_FLG",expensive_flg == null ? new TNull(String.class) : expensive_flg);
        	
        	String printtypeflg_infant = parmOpdOrder.getValue("PRINTTYPEFLG_INFANT",i);
        	
        	insertParm.setData("PRINTTYPEFLG_INFANT",printtypeflg_infant == null ? new TNull(String.class) : printtypeflg_infant);
        	String ctrldrugclass_code = parmOpdOrder.getValue("CTRLDRUGCLASS_CODE",i);
        	insertParm.setData("CTRLDRUGCLASS_CODE",ctrldrugclass_code == null ? new TNull(String.class) : ctrldrugclass_code);
        	int prescript_no = parmOpdOrder.getInt("PRESCRIPT_NO",i);
        	insertParm.setData("PRESCRIPT_NO",prescript_no);
        	String hexp_code = parmOpdOrder.getValue("HEXP_CODE",i);
        	insertParm.setData("HEXP_CODE",hexp_code == null ? new TNull(String.class) : hexp_code);
        	String contract_code = parmOpdOrder.getValue("CONTRACT_CODE",i);
        	insertParm.setData("CONTRACT_CODE",contract_code == null ? new TNull(String.class) : contract_code);
        	String ctz1_code = parmOpdOrder.getValue("CTZ1_CODE",i);
        	insertParm.setData("CTZ1_CODE",ctz1_code == null ? new TNull(String.class) : ctz1_code);
        	String ctz2_code = parmOpdOrder.getValue("CTZ2_CODE",i);
        	insertParm.setData("CTZ2_CODE",ctz2_code == null ? new TNull(String.class) : ctz2_code);
        	String ctz3_code = parmOpdOrder.getValue("CTZ3_CODE",i);
        	insertParm.setData("CTZ3_CODE",ctz3_code == null ? new TNull(String.class) : ctz3_code);
        	String ns_exec_code = parmOpdOrder.getValue("NS_EXEC_CODE",i);
        	insertParm.setData("NS_EXEC_CODE",ns_exec_code == null ? new TNull(String.class) : ns_exec_code);
        	String ns_exec_date = parmOpdOrder.getValue("NS_EXEC_DATE",i);
        	insertParm.setData("NS_EXEC_DATE",ns_exec_date == null ? new TNull(Timestamp.class)  : ns_exec_date);
        	String ns_exec_dept = parmOpdOrder.getValue("NS_EXEC_DEPT",i);
        	insertParm.setData("NS_EXEC_DEPT",ns_exec_dept == null ? new TNull(String.class) : ns_exec_dept);
        	String dctagent_code = parmOpdOrder.getValue("DCTAGENT_CODE",i);
        	insertParm.setData("DCTAGENT_CODE",dctagent_code == null ? new TNull(String.class) : dctagent_code);
        	String dctexcep_code = parmOpdOrder.getValue("DCTEXCEP_CODE",i);
        	insertParm.setData("DCTEXCEP_CODE",dctexcep_code == null ? new TNull(String.class) : dctexcep_code);
        	int dct_take_qty = parmOpdOrder.getInt("DCT_TAKE_QTY",i);
        	insertParm.setData("DCT_TAKE_QTY",dct_take_qty );
        	int package_tot = parmOpdOrder.getInt("PACKAGE_TOT",i);
        	insertParm.setData("PACKAGE_TOT",package_tot);       	
        	insertParm.setData("OPT_USER",optUser == null ? new TNull(String.class) : optUser);      	
        	insertParm.setData("OPT_TERM",optTerm == null ? new TNull(String.class) : optTerm);
        	String exm_exec_end_date = parmOpdOrder.getValue("EXM_EXEC_END_DATE",i);
        	insertParm.setData("EXM_EXEC_END_DATE",exm_exec_end_date == null ? new TNull(String.class) : exm_exec_end_date);
        	String business_no = parmOpdOrder.getValue("BUSINESS_NO",i);
        	insertParm.setData("BUSINESS_NO",business_no == null ? new TNull(String.class) : business_no);
        
        	 result = update("insertdata", insertParm, connection);
              if (result.getErrCode() < 0) {
                  err(result.getErrCode() + " " + result.getErrText());
                  return result;
              }
    
    		//step3 循环更新价格,身份,折扣率
    		BIL bil = new BIL();
    		String chargeHospCode = hexp_code;
    		String orderCode = order_code;
    		double ownRate = bil.getOwnRate(CTZ1,CTZ2,CTZ3,chargeHospCode,orderCode) ;
    		//System.out.println("ownRate--->"+ownRate);
    		double own_amt = parmOpdOrder.getDouble("OWN_AMT",i);
    		//System.out.println("own_amt---->"+own_amt);
    		double ar_amt =  own_amt*ownRate;
    		//System.out.println("ar_amt--->"+ar_amt);
    		String updOpdOrder = "UPDATE OPD_ORDER SET CTZ1_CODE = '"+CTZ1+"' ,CTZ2_CODE =  '"+CTZ2+"',CTZ3_CODE = '"+CTZ3+"' ,OPT_USER = '"+optUser+"', OPT_DATE = SYSDATE ,OPT_TERM = '"+optTerm+"',DISCOUNT_RATE = '"+ownRate+"' , AR_AMT = '"+ar_amt+"' WHERE CASE_NO = '"+caseNo+"' AND RX_NO = '"+rx_no+"' AND SEQ_NO = '"+seq_no+"'";
    		//System.out.println("updOpdOrder-------->"+updOpdOrder);
    		result = new TParm(TJDODBTool.getInstance().update(updOpdOrder));
    		if (result.getErrCode() < 0) {
    			err(result.getErrName() + " " + result.getErrText());
    			return result;
    		}
		}		
		// 插入日志信息
		String mr_no = selPatientParm.getValue("MR_NO").toString();
		if (mr_no != null && mr_no.length() != 0) {
			mr_no = mr_no.substring(1, mr_no.length() - 1);
		} else {
			mr_no = "";
		}		
		String region_code = selPatientParm.getData("REGION_CODE").toString();
		if (region_code != null && region_code.length() != 0) {
			region_code = region_code.substring(1, region_code.length() - 1);
		} else {
			region_code = "";
		}

		String ctz1_code = selPatientParm.getData("CTZ1_CODE").toString();
		if (ctz1_code != null && ctz1_code.length() != 0) {
			ctz1_code = ctz1_code.substring(1, ctz1_code.length() - 1);
		} else {
			ctz1_code = "";
		}

		String ctz2_code = selPatientParm.getData("CTZ2_CODE").toString();
		if (ctz2_code != null && ctz2_code.length() != 0) {
			ctz2_code = ctz2_code.substring(1, ctz2_code.length() - 1);
		} else {
			ctz2_code = "";
		}

		String ctz3_code = selPatientParm.getData("CTZ3_CODE").toString();
		if (ctz3_code != null && ctz3_code.length() != 0) {
			ctz3_code = ctz3_code.substring(1, ctz3_code.length() - 1);
		} else {
			ctz3_code = "";
		}
		int seq_no = 1;
//		String sql = "SELECT MAX(SEQ_NO) AS SEQ_NO FROM OPB_CTZ_LOG WHERE CASE_NO = '"+caseNo+"'";
//		TParm parmSeq = new TParm(TJDODBTool.getInstance().select(sql));
//		if(parmSeq.getCount()>0){
//			seq_no = parmSeq.getInt("SEQ_NO",0)+1;
//		}		
		int count = selCountOfCaseno(caseNo);
		//System.out.println("条数："+count);
		seq_no += count;
		String logSql = "INSERT INTO "
				+ " OPB_CTZ_LOG(CASE_NO,SEQ_NO,MR_NO,REGION_CODE,CTZ_CODE1_O,CTZ_CODE2_O,CTZ_CODE3_O,CTZ_CODE1_N,CTZ_CODE2_N,CTZ_CODE3_N,OPT_USER,OPT_DATE,OPT_TERM) "
				+ " VALUES ('"
				+ caseNo
				+ "','"
				+ seq_no
				+ "','"
				+ mr_no
				+ "','"				
				+ region_code
				+ "','"
				+ ctz1_code
				+ "','"
				+ ctz2_code
				+ "','"
				+ ctz3_code
				+ "','"
				+ CTZ1
				+ "','"
				+ CTZ2
				+ "','"
				+ CTZ3
				+ "','"
				+ optUser
				+ "',SYSDATE,'"
				+ optTerm + "')";
		//System.out.println("日志："+logSql);
		result = new TParm(TJDODBTool.getInstance().update(logSql));
		
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * 修改身份 --查询病人信息
	 * 
	 * @author caowl
	 * @param parm
	 *            String
	 * @return TParm
	 */
	public TParm selBycaseNo(String caseNo) {
		TParm selParm = new TParm();
		String sql = "SELECT CASE_NO,MR_NO,REGION_CODE,CTZ1_CODE,CTZ2_CODE,CTZ3_CODE FROM REG_PATADM WHERE CASE_NO = '"
				+ caseNo + "'";
		selParm = new TParm(TJDODBTool.getInstance().select(sql));
		return selParm;
	}

	/**
	 * 修改身份 --查询日志
	 * 
	 * @author caowl
	 * @param parm
	 *            String
	 * @return int
	 */
	public int selCountOfCaseno(String caseNo) {
		String sql = "SELECT CASE_NO,SEQ_NO,MR_NO,REGION_CODE,CTZ_CODE1_O,CTZ_CODE2_O,CTZ_CODE3_O,CTZ_CODE1_N,CTZ_CODE2_N,CTZ_CODE3_N,OPT_USER,OPT_DATE,OPT_TERM " +
				" FROM OPB_CTZ_LOG " +
				" WHERE CASE_NO = '" + caseNo
				+ "'";
		//System.out.println(sql);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getCount() == -1){
			return 0;
		}
		return result.getCount();
	}
}
