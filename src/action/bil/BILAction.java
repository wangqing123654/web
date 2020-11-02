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
 * <p>Title:����action </p>
 *
 * <p>Description:����action </p>
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
     * ���������ս�
     * @param parm TParm
     * @return TParm
     */
    public TParm onSaveAcctionOpb(TParm parm) {
        TParm result=new TParm();
        if(parm==null)
            return result.newErrParm(-1,"����Ϊ��");
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
     * סԺ�սᱣ��
     * @param parm TParm
     * @return TParm
     */
    public TParm onSaveAcctionBIL(TParm parm) {
        TParm result=new TParm();
        if(parm==null)
            return result.newErrParm(-1,"����Ϊ��");
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
     * �վ��ٻر���
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
     * �˵���˱���
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
     * �˵���˸������״̬
     * @param parm TParm
     * @return TParm
     */
    public TParm onAuditFeeCheck(TParm parm){
//        System.out.println("�����˵���˸������״̬Action"+parm);
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
     * סԺ�վݲ�ӡ
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
     * ���淢Ʊ
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
     * �����˵�
     * ======kangy 20170814
     * @param parm
     * @return
     */
    public TParm onNewBill(TParm parm){
    	TParm result = new TParm();
    	TConnection connection = getConnection();
    	//����IBS_ORDD������ 
    	TParm ibsOrddParm=BILTool.getInstance().collectIbsOrdd(parm);
    	
    	//IBS_BILLM����ԭ��Ч�˵�
    	TParm selBillm = BILTool.getInstance().selBillm(parm,connection);
    	int billmCount=selBillm.getCount();
    	String newBillNo = "";
    	Timestamp sysDate=SystemTool.getInstance().getDate();
    	String optUser = parm.getValue("OPT_USER");
		String optTerm = parm.getValue("OPT_TERM");
		for(int i=0;i<billmCount;i++){
		//IBS_BILLM�������븺��
			newBillNo = SystemTool.getInstance().getNo("ALL", "IBS","BILL_NO", "BILL_NO");
    	 result= BILTool.getInstance().insertNegativeIbsBillM(newBillNo, selBillm, i, sysDate, optTerm, optUser, connection);
    	 if (result.getErrCode() < 0) {
				err(result.getErrName() + " " + result.getErrText());
				connection.rollback();
				connection.close();
				return result;
			}

    	 //IBS_BILLM�޸�
    	 result= BILTool.getInstance().updateIbsBillM(selBillm,newBillNo,i,optUser, connection);
    	 if (result.getErrCode() < 0) {
				err(result.getErrName() + " " + result.getErrText());
				connection.rollback();
				connection.close();
				return result;
			}

    	 //IBS_BILLD�����������--��Ӹ�����
    	 result= BILTool.getInstance().insertNegativeIbsBillD(selBillm, i, optUser, connection, sysDate, optTerm, newBillNo);
    	 if (result.getErrCode() < 0) {
				err(result.getErrName() + " " + result.getErrText());
				connection.rollback();
				connection.close();
				return result;
			}
    	 //IBS_BILLD�����������--����ԭ����
     result= BILTool.getInstance().updateIbsBillD(selBillm, i, optUser, connection, sysDate, optTerm, newBillNo);
    	 if (result.getErrCode() < 0) {
				err(result.getErrName() + " " + result.getErrText());
				connection.rollback();
				connection.close();
				return result;
			}
    	
		}

    	//IBS_BILLM�����������
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
    	//IBS_BILLD���������--���������

		 result =BILTool.getInstance().insertPositiveIbsBillD(ibsOrddParm, newBillNo2,optTerm,optUser,sysDate,connection);
		 if (result.getErrCode() < 0) {
				err(result.getErrName() + " " + result.getErrText());
				connection.rollback();
				connection.close();
				return result;
			}

    	//�޸�IBS_ORDD�����д��ڵ�����
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
