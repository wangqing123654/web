package action.bil;

import java.sql.Timestamp;

import org.hibernate.sql.Insert;

import com.dongyang.db.TConnection;
import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;

import jdo.bil.BILFinanceTool;
import jdo.bil.BILTool;
import jdo.ibs.IBSBillmTool;
import jdo.ibs.IBSNewTool;
import jdo.sys.SystemTool;

/**
 * <p>Title:账务action </p>
 *
 * <p>Description:账务action </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company:javahis </p>
 *
 * @author fudw 20090903
 * @version 1.0
 */
public class BILAction extends TAction {
    /**
     * 保存门诊日结
     * @param parm TParm
     * @return TParm
     */
    public TParm onSaveAcctionOpb(TParm parm) {
        TParm result=new TParm();
        if(parm==null)
            return result.newErrParm(-1,"参数为空");
        TConnection connection = getConnection();
        result=BILTool.getInstance().onSaveAccountOpb(parm,connection);
        if (result==null||result.getErrCode() < 0) {
            connection.close();
             return result;
        }
        connection.commit();
        connection.close();
        return result;

    }
    /**
     * 住院日结保存
     * @param parm TParm
     * @return TParm
     */
    public TParm onSaveAcctionBIL(TParm parm) {
        TParm result=new TParm();
        if(parm==null)
            return result.newErrParm(-1,"参数为空");
        TConnection connection = getConnection();
        result=BILTool.getInstance().onSaveAcctionBIL(parm,connection);
        if (result==null||result.getErrCode() < 0) {
            connection.close();
             return result;
        }
        connection.commit();
        connection.close();
        return result;

    }
    /**
     * 收据召回保存
     * @param parm TParm
     * @return TParm
     */
    public TParm onSaveReceiptReturn(TParm parm){
        TConnection connection = getConnection();
        TParm result = new TParm();
        result = BILTool.getInstance().insertRcpReturn(parm, connection);
        if (result.getErrCode() < 0) {
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }
    /**
     * 账单审核保存
     * @param parm TParm
     * @return TParm
     */
    public TParm onSaveAuditFee(TParm parm){
        TConnection connection = getConnection();
        TParm result = new TParm();
        result = BILTool.getInstance().onSaveAuditFee(parm, connection);
        if (result.getErrCode() < 0) {
            connection.rollback();
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }
    /**
     * 账单审核更新审核状态
     * @param parm TParm
     * @return TParm
     */
    public TParm onAuditFeeCheck(TParm parm){
//        System.out.println("进入账单审核更新审核状态Action"+parm);
        TConnection connection = getConnection();
        TParm result = new TParm();
        result = BILTool.getInstance().onAuditFeeCheck(parm, connection);
        if (result.getErrCode() < 0) {
            connection.rollback();
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }
    /**
     * 住院收据补印
     * @param parm TParm
     * @return TParm
     */
    public TParm onIBSReprint(TParm parm) {
        TConnection connection = getConnection();
        TParm result = new TParm();
        result = BILTool.getInstance().onIBSReprint(parm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;

    }

    /**
     * 保存发票
     * ======zhangp
     * @param parm
     * @return
     */
    public TParm onSaveTax(TParm parm){
    	TConnection connection = getConnection();
    	String sql = "";
    	TParm result = new TParm();
    	for (int i = 0; i < parm.getCount("SQL"); i++) {
    		sql = parm.getValue("SQL", i);
    		result = new TParm(TJDODBTool.getInstance().update(sql, connection)); 
    		if(result.getErrCode() < 0){
    			connection.rollback();
    			connection.close();
    			return result;
    		}
		}
    	connection.commit();
        connection.close();
    	return result;
    }

    /**
     * 调整账单
     * ======kangy 20170814
     * @param parm
     * @return
     */
    public TParm onNewBill(TParm parm){
    	TParm result = new TParm();
    	TConnection connection = getConnection();
    	//汇总IBS_ORDD表数据 
    	TParm ibsOrddParm=BILTool.getInstance().collectIbsOrdd(parm);
    	
    	//IBS_BILLM作废原有效账单
    	TParm selBillm = BILTool.getInstance().selBillm(parm,connection);
    	int billmCount=selBillm.getCount();
    	String newBillNo = "";
    	Timestamp sysDate=SystemTool.getInstance().getDate();
    	String optUser = parm.getValue("OPT_USER");
		String optTerm = parm.getValue("OPT_TERM");
		for(int i=0;i<billmCount;i++){
		//IBS_BILLM主档插入负数
			newBillNo = SystemTool.getInstance().getNo("ALL", "IBS","BILL_NO", "BILL_NO");
    	 result= BILTool.getInstance().insertNegativeIbsBillM(newBillNo, selBillm, i, sysDate, optTerm, optUser, connection);
    	 if (result.getErrCode() < 0) {
				err(result.getErrName() + " " + result.getErrText());
				connection.rollback();
				connection.close();
				return result;
			}

    	 //IBS_BILLM修改
    	 result= BILTool.getInstance().updateIbsBillM(selBillm,newBillNo,i,optUser, connection);
    	 if (result.getErrCode() < 0) {
				err(result.getErrName() + " " + result.getErrText());
				connection.rollback();
				connection.close();
				return result;
			}

    	 //IBS_BILLD表的数据作废--添加负数据
    	 result= BILTool.getInstance().insertNegativeIbsBillD(selBillm, i, optUser, connection, sysDate, optTerm, newBillNo);
    	 if (result.getErrCode() < 0) {
				err(result.getErrName() + " " + result.getErrText());
				connection.rollback();
				connection.close();
				return result;
			}
    	 //IBS_BILLD表的数据作废--更新原数据
     result= BILTool.getInstance().updateIbsBillD(selBillm, i, optUser, connection, sysDate, optTerm, newBillNo);
    	 if (result.getErrCode() < 0) {
				err(result.getErrName() + " " + result.getErrText());
				connection.rollback();
				connection.close();
				return result;
			}
    	
		}

    	//IBS_BILLM表插入正数据
		String newBillNo2 = SystemTool.getInstance().getNo("ALL", "IBS","BILL_NO", "BILL_NO");
		double tot_amt=0.00;
		double own_amt=0.00;
		for(int i=0;i<ibsOrddParm.getCount();i++){
			tot_amt+=ibsOrddParm.getDouble("TOT_AMT",i);
			own_amt+=ibsOrddParm.getDouble("OWN_AMT",i);
		}

		result =BILTool.getInstance().insertPositiveIbsBillM(ibsOrddParm,selBillm,newBillNo2,sysDate,tot_amt,own_amt,optUser,optTerm, connection);
		 if (result.getErrCode() < 0) {
				err(result.getErrName() + " " + result.getErrText());
				connection.rollback();
				connection.close();
				return result;
			}
    	//IBS_BILLD表插入数据--添加正数据

		 result =BILTool.getInstance().insertPositiveIbsBillD(ibsOrddParm, newBillNo2,optTerm,optUser,sysDate,connection);
		 if (result.getErrCode() < 0) {
				err(result.getErrName() + " " + result.getErrText());
				connection.rollback();
				connection.close();
				return result;
			}

    	//修改IBS_ORDD表所有存在的数据
		 parm.setData("BILL_NO",newBillNo2);
		 result =BILTool.getInstance().updateIbsOrdd(parm,connection);
		 if (result.getErrCode() < 0) {
				err(result.getErrName() + " " + result.getErrText());
				connection.rollback();
				connection.close();
				return result;
			}
			connection.commit();
			connection.close();
		return result;

    }
}
