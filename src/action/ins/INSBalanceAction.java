package action.ins;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import jdo.ins.INSADMConfirmTool;
import jdo.ins.INSIbsOrderTool;
import jdo.ins.INSIbsTool;
import jdo.ins.INSIbsUpLoadTool;
import jdo.ins.INSIpdHistoryTool;
import jdo.sys.SYSFeeTool;
import jdo.sys.SystemTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;

/**
 *
 * <p>
 * Title:סԺ���÷ָ�
 * </p>
 *
 * <p>
 * Description:סԺ���÷ָ�
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 *
 * <p>
 * Company:bluecore
 * </p>
 *
 * @author pangb 2012-2-07
 * @version 2.0
 */
public class INSBalanceAction extends TAction {
    // �������
    private String[] nameAmt = {"PHA_AMT", "PHA_OWN_AMT", "EXM_AMT",
                               "EXM_OWN_AMT", "TREAT_AMT", "TREAT_OWN_AMT",
                               "OP_AMT",
                               "OP_OWN_AMT", "BED_AMT", "BED_OWN_AMT",
                               "MATERIAL_AMT",
                               "MATERIAL_OWN_AMT", "OTHER_AMT", "OTHER_OWN_AMT",
                               "BLOOD_AMT",
                               "BLOOD_OWN_AMT", "TOTAL_AMT", "OWN_AMT",
                               "BLOODALL_AMT",
                               "BLOODALL_OWN_AMT", "PHA_NHI_AMT", "EXM_NHI_AMT",
                               "TREAT_NHI_AMT",
                               "OP_NHI_AMT", "BED_NHI_AMT", "MATERIAL_NHI_AMT",
                               "OTHER_NHI_AMT",
                               "BLOODALL_NHI_AMT", "BLOOD_NHI_AMT"};
    private TParm amtParm = new TParm(); // �����������
    // private TParm ibsOrddParm = new TParm();// סԺ������ϸ ����
    private int errorIndex; // �ۼƴ������
    private int succIndex; // �ۼƳɹ�����
    private int count = 1; // ��� INS_IBS_ORDER �ۼƸ���
    // У�����INS_IBS�����Ƿ�Ϊ��
    private String[] nameIbs = {"PAT_NAME", "IDNO", "BIRTH_DATE", "ADM_SEQ",
                               "CONFIRM_SRC", "HOSP_NHI_NO", "INSBRANCH_CODE",
                               "CTZ1_CODE",
                               "ADM_CATEGORY", "DIAG_CODE", "DIAG_DESC",
                               "OWN_RATE",
                               "DECREASE_RATE", "REALOWN_RATE", "INSOWN_RATE",
                               "STATION_DESC",
                               "BED_NO", "DEPT_DESC", "DEPT_CODE",
                               "BASEMED_BALANCE",
                               "INS_BALANCE", "OWN_AMT", "CONFIRM_NO",
                               "CHEMICAL_DESC", "ADM_PRJ",
                               "SPEDRS_CODE", "STATUS", "RECEIPT_USER",
                               "NHI_NUM", "INS_UNIT",
                               "HOSP_CLS_CODE", "INP_TIME", "HOMEBED_TIME",
                               "TRANHOSP_DESC",
                               "TRAN_CLASS", "HOMEDIAG_DESC", "SEX_CODE",
                               "SOURCE_CODE",
                               "UNIT_CODE", " UNIT_DESC", "PAT_AGE"};
    // У�����INS_IBS_ORDER�����Ƿ�Ϊ��
    private String[] nameIbsOrder = {"ADM_SEQ", "INSBRANCH_CODE",
                                    "HOSP_NHI_NO", "ORDER_CODE",
                                    "NHI_ORDER_CODE", "ORDER_DESC",
                                    "OWN_RATE", "DOSE_CODE", "STANDARD",
                                    "OP_FLG", "ADDPAY_FLG",
                                    "NHI_ORD_CLASS_CODE", "PHAADD_FLG",
                                    "CARRY_FLG","EXE_DEPT_CODE","DOSAGE_UNIT"};
    // У�����INS_IBS_UPLOAD�����Ƿ�Ϊ��
    private String[] nameIbsUpLoad = {"NHI_ORDER_CODE", "ORDER_CODE",
                                     "ORDER_DESC", "OWN_RATE", "DOSE_CODE",
                                     "STANDARD", "OP_FLG",
                                     "CARRY_FLG", "PHAADD_FLG", "ADDPAY_FLG",
                                     "HYGIENE_TRADE_CODE",
                                     "NHI_ORD_CLASS_CODE"};
    // INS_IBS_UPLOAD���޸Ľ�������ʱ����У��Ϊ��
    private String[] nameIbsUpLoadOne = {"ORDER_CODE", "ORDER_DESC",
                                        "DOSE_CODE", "STANDARD", "PHAADD_FLG",
                                        "CARRY_FLG","ADDPAY_FLG",
                                        "NHI_ORDER_CODE", "NHI_ORD_CLASS_CODE",
                                        "NHI_FEE_DESC", "HYGIENE_TRADE_CODE"};
    /**
     * ת������������ �� ת�걨����
     * @param tempParm TParm
     * @return TParm
     */
    public TParm onExe(TParm tempParm) {
        // ���÷ָ� ��ѯ����������Ϣ����
        TParm confirmParm = INSADMConfirmTool.getInstance().queryConfirmInfo(
                tempParm);
        if (confirmParm.getErrCode() < 0) {
            return confirmParm;
        }
        TParm result = new TParm();
        if (tempParm.getValue("TYPE").equals("M")) { // TYPE : M,ת������Ϣ
            result = onQueryInfo(confirmParm, tempParm);
        } else if (tempParm.getValue("TYPE").equals("H")) { // TYPE :  H,ת�걨
            result = onApply(confirmParm, tempParm);
        }
        if (result.getErrCode() < 0) {
            return result;
        }
        result.setData("ERROR_INDEX", errorIndex); // �ۼƴ������
        result.setData("SUCCESS_INDEX", succIndex); // �ۼƳɹ�����
        return result;
    }

    /**
     * ת������Ϣ �ۼƽ�����
     * @param confirmParm TParm
     * @param tempParm TParm
     * @return TParm
     */
    private TParm onQueryInfo(TParm confirmParm, TParm tempParm) {
        TParm tParm = null; // ��IBS_OrdD��ȡ���� ��ȡסԺ��������
        TParm insParm = null; //��ѯҽ��������
        TParm ibsOrddParm = null; // ��ѯҽ������
        TParm orderParm = new TParm(); // ��ѯҽ������ ִ�к� �������������
        TParm result = new TParm(); // ִ�н��
        for (int i = 0; i < confirmParm.getCount(); i++) {
        	// ��ѯ��������Ƿ����ʫ��
        	String sql = "SELECT REGION_CODE FROM INS_IBS WHERE YEAR_MON='" + tempParm.getValue("YEAR_MON")
                    + "' AND CASE_NO='" + tempParm.getValue("CASE_NO") + "'";
            TParm checkParm = new TParm(TJDODBTool.getInstance().select(sql));
            if (checkParm.getErrCode() < 0) {
                return checkParm;
            }         
            amtParm = new TParm();
            TParm confirmTempParm = confirmParm.getRow(i);
            System.out.println("confirmTempParm:"+confirmTempParm);
            TParm ibsOrdParm = new TParm();
            ibsOrdParm.setData("START_DATE", tempParm.getValue("START_DATE")
                               + "000000"); // ��ʼʱ��
            ibsOrdParm.setData("END_DATE", tempParm.getValue("END_DATE")
                               + "235959"); // ����ʱ��
            ibsOrdParm.setData("CASE_NO", tempParm.getValue("CASE_NO")); // �����
            // ��IBS_OrdD��ȡ���� ��ȡסԺ��������
            tParm = INSIbsTool.getInstance().queryIbsOrdd(ibsOrdParm);
            if (tParm.getErrCode() < 0) {
                return tParm;
            }

            for (int j = 0; j < tParm.getCount(); j++) {
                ibsOrddParm = tParm.getRow(i);
                if (null != tempParm.getValue("REGION_CODE")
                    && tempParm.getValue("REGION_CODE").length() > 0) {
                    ibsOrddParm.setData("REGION_CODE", tempParm
                                        .getValue("REGION_CODE"));
                }
                // ��ѯҽ��������
                insParm=INSIbsTool.getInstance().queryInsIbsOrderByInsRule(ibsOrddParm);
                if (insParm.getErrCode()<0 || insParm.getCount()!=1) {
                	if (insParm.getCount()!=1) {
                		TParm errResult=new TParm();
                    	errResult.setErr(-1,ibsOrddParm.getValue("ORDER_CODE")+"  "+
                    			            ibsOrddParm.getValue("ORDER_DESC")+"ҽ����������");
                    	return errResult;
					}
                	return insParm;
        		}
                // ������������
                getOrderParm(confirmTempParm, ibsOrddParm, tempParm, orderParm,insParm, 1);
            }
            orderParm.setCount(tParm.getCount());
            for (int z = 0; z < orderParm.getCount(); z++) {
                TParm exeParm = orderParm.getRow(z);

                // ����money
                double amt = exeParm.getDouble("TOT_AMT"); // ����
                double own_amt = exeParm.getDouble("OWN_AMT"); // �Է�
                double nhi_amt = exeParm.getDouble("TOTAL_NHI_AMT"); // ҽ�����
                int Nhi_ord_class_code = exeParm.getInt("NHI_ORD_CLASS_CODE");
                // ����� Order ������һ��Ŀ��Ǯ(ת�ļ�ʹ��)
                Accnt_OrderRange_Amt(Nhi_ord_class_code, amt, own_amt, nhi_amt);
            }
            // ���¸�ֵ
            setInsIbsParm(confirmTempParm, tempParm);
            // ��ֵ
            for (int j = 0; j < nameAmt.length; j++) {
                confirmTempParm.setData(nameAmt[j], amtParm
                                        .getDouble(nameAmt[j]));
            }
            //=====pangben 2012-5-30 ת���������
            TParm  statusParm = new TParm(TJDODBTool.getInstance().select("SELECT CODE1_STATUS FROM MRO_RECORD WHERE CASE_NO='" + tempParm.getValue("CASE_NO") + "'"));
            if (statusParm.getErrCode()<0) {
            	return statusParm;
			}
            confirmTempParm.setData("SOURCE_CODE",statusParm.getValue("CODE1_STATUS",0));
            // У�����INS_IBS�����Ƿ�Ϊ��
            for (int j = 0; j < nameIbs.length; j++) {
                if (confirmTempParm.getValue(nameIbs[j]).equals("null")
                    || confirmTempParm.getValue(nameIbs[j]).equals("")) {
                    confirmTempParm.setData(nameIbs[j], "");
                }
            }
                        
            if (checkParm.getCount() <= 0) { // ����������
                result = INSIbsTool.getInstance().insertInsIbs(confirmTempParm);
                if (result.getErrCode() < 0) {
                    errorIndex++; // �ۼƴ���
                } else {
                    succIndex++; // �ۼƳɹ�
                }
            } else {
                result = INSIbsTool.getInstance().updateINSIbs(confirmTempParm);
                if (result.getErrCode() < 0) {
                    errorIndex++; // �ۼƴ���
                } else {
                    succIndex++; // �ۼƳɹ�
                }
            }
            result = INSADMConfirmTool.getInstance().updatedDsDiag(
                    confirmTempParm);
            if (result.getErrCode() < 0) {
                //System.out.println("�޸�INSADMConfirmʧ��");
            }
        }
        TParm parm = new TParm();
        parm.setData("YEAR_MON", tempParm.getValue("YEAR_MON")); // �ں�
        parm.setData("CASE_NO", tempParm.getValue("CASE_NO"));
        result = INSIbsTool.getInstance().queryIbsSum(parm); // ��ѯ���ݸ����渳ֵ
        return result;
    }

    /**
     * ת�걨����
     * @param confirmParm TParm
     * @param tempParm TParm
     * @return TParm
     */
    private TParm onApply(TParm confirmParm, TParm tempParm) {
        // ɾ������
        TParm parm = new TParm();
        parm.setData("CASE_NO", tempParm.getValue("CASE_NO")); // �����
        parm.setData("YEAR_MON", tempParm.getValue("YEAR_MON")); // �ں�
        parm.setData("START_DATE", tempParm.getValue("START_DATE")); // ��ʼ
        parm.setData("END_DATE", tempParm.getValue("END_DATE")); // ����ʱ��
        TParm result = null;
        if (confirmParm.getCount("CASE_NO") > 0) {
            result = INSIbsOrderTool.getInstance().deleteINSIbsOrder(parm);
        }
        if (result.getErrCode() < 0) {
            return result;
        }
        TParm tParm = null; // ��IBS_OrdD��ȡ���� ��ȡסԺ��������
        TParm ibsOrddParm = null; // ��ѯҽ������
        TParm orderParm = new TParm(); // ��ѯҽ������ ִ�к� �������������
        boolean flg = false; // �ܿ� �ŵ�һ���������
        for (int i = 0; i < confirmParm.getCount(); i++) {
        	//int index=0;
            TParm confirmTempParm = confirmParm.getRow(i);
            // ��IBS_OrdD��ȡ���� ��ȡסԺ��������
            tParm = INSIbsTool.getInstance().queryIbsOrdd(parm);
            if (tParm.getErrCode() < 0) {
                return tParm;
            }
            count = 1; // �����ۼ����� ��� INS_IBS_ORDER
            for (int j = 0; j < tParm.getCount(); j++) {
                ibsOrddParm = tParm.getRow(j);
                if(!getInsertIbsOrder(tempParm, confirmTempParm, ibsOrddParm, orderParm)){
                	TParm errResult=new TParm();
                	errResult.setErr(-1,ibsOrddParm.getValue("ORDER_CODE")+"  "+
                			            ibsOrddParm.getValue("ORDER_DESC")+"ҽ����������");
                	return errResult;
                }
                //index++;
            }
            orderParm.setCount(tParm.getCount());
            //System.out.println("���INS_IBS_ORDER ����"+orderParm);
            flg = false;
            // ���INS_IBS_ORDER ����
            for (int j = 0; j < orderParm.getCount(); j++) {
                TParm ibsOrder = orderParm.getRow(j);
                //System.out.println("ibsOrder::::"+ibsOrder);
                // У��Ϊ��
                for (int k = 0; k < nameIbsOrder.length; k++) {
                    if (null == ibsOrder.getValue(nameIbsOrder[k]) ||
                        ibsOrder.getValue(nameIbsOrder[k]).equals("null")
                        || ibsOrder.getValue(nameIbsOrder[k]).equals("")) {
                        ibsOrder.setData(nameIbsOrder[k], "");
                    }
                }
                result = INSIbsOrderTool.getInstance().insertINSIbsOrder(
                        ibsOrder);
                if (result.getErrCode() < 0) {
                    flg = true;
                    errorIndex++;
                    break;
                }
            }
            if (flg) { // �ܿ� �ŵ�һ���������
                break;
            }
            // ��ϸ�ϴ�����
            TParm insIbsUnionParm = INSIbsOrderTool.getInstance()
                                    .queryInsIbsDUnion(parm);
            if (insIbsUnionParm.getErrCode() < 0) {
                errorIndex++;
                break;
            }
            //System.out.println("��ϸ�ϴ�����insIbsUnionParm:::"+insIbsUnionParm);
            // ��� INS_IBS_UPLOAD �����
            result = onApplyInsertIbsUpLoad(confirmTempParm, insIbsUnionParm);
            if (result.getErrCode() < 0) {
                errorIndex++;
                break;
            }
            succIndex++;
        }
        return new TParm();
    }
    /**
     * ��������
     * @param tempParm
     * @param confirmTempParm
     * @param ibsOrddParm
     * @param orderParm
     * @return
     */
    private boolean getInsertIbsOrder(TParm tempParm,TParm confirmTempParm,TParm ibsOrddParm,TParm orderParm){
		if (null != tempParm.getValue("REGION_CODE")
				&& tempParm.getValue("REGION_CODE").length() > 0) {
			ibsOrddParm
					.setData("REGION_CODE", tempParm.getValue("REGION_CODE"));
		}
		TParm insParm=INSIbsTool.getInstance().queryInsIbsOrderByInsRule(ibsOrddParm);
        if (insParm.getErrCode()<0 || insParm.getCount()!=1) {
        	return false;
		}
		// ������������
		getOrderParm(confirmTempParm, ibsOrddParm,
				tempParm, orderParm,insParm, 2);
		return true;
    }
    /**
     * ��� INS_IBS_UPLOAD ����� �����Էŵ�һ������������� ��Ҫ��ѯ֮ǰ��ӵ�����
     * @param confirmTempParm TParm ��ѯ ADM_CONFIRM ��
     * @param insIbsUnionParm TParm ��ѯINS_IBS_UPLOAD
     * @return TParm
     */
    private TParm onApplyInsertIbsUpLoad(TParm confirmTempParm,
                                         TParm insIbsUnionParm) {

        TParm result = new TParm();
        // ɾ��������
        if (insIbsUnionParm.getCount() > 0) {
            result = INSIbsUpLoadTool.getInstance().deleteINSIbsUpload(
                    confirmTempParm);
        }
        if (result.getErrCode() < 0) {
            return result;
        }
        // ִ����Ӳ���
        for (int j = 0; j < insIbsUnionParm.getCount(); j++) {
            TParm tempParm = insIbsUnionParm.getRow(j);
            tempParm.setData("SEQ_NO", j + 1);
            tempParm.setData("CHARGE_DATE", SystemTool.getInstance()
                             .getDateReplace(tempParm.getValue("CHARGE_DATE"), true)); // ��ϸ¼��ʱ��
            tempParm.setData("ADDPAY_FLG", "Y"); // ����
            for (int i = 0; i < nameIbsUpLoad.length; i++) { //У���Ƿ�Ϊ��
                if (tempParm.getValue(nameIbsUpLoad[i]).equals("null")
                    || tempParm.getValue(nameIbsUpLoad[i]).equals("")) {
                    tempParm.setData(nameIbsUpLoad[i], "");
                }
            }
            result = INSIbsUpLoadTool.getInstance()
                     .insertINSIbsUpload(tempParm);
            if (result.getErrCode() < 0) {
                return result;
            }
        }

        return result;
    }

    /**
     * ����� Order ������һ��Ŀ��Ǯ(ת�ļ�ʹ��)
     * @param Nhi_ord_class_code int INS_RULE ����ͳ�ƴ���
     * @param amt double
     * @param own_amt double
     * @param nhi_amt double
     */
    private void Accnt_OrderRange_Amt(int Nhi_ord_class_code, double amt,
                                      double own_amt, double nhi_amt) {
        switch (Nhi_ord_class_code) {
        case 1: // ҩƷ��
            amtParm.setData("PHA_AMT", amtParm.getDouble("PHA_AMT") + amt);
            amtParm.setData("PHA_OWN_AMT", amtParm.getDouble("PHA_OWN_AMT")
                            + own_amt);
            amtParm.setData("PHA_NHI_AMT", amtParm.getDouble("PHA_NHI_AMT")
                            + nhi_amt);
            break;
        case 2: // ����
            amtParm.setData("EXM_AMT", amtParm.getDouble("EXM_AMT") + amt);
            amtParm.setData("EXM_OWN_AMT", amtParm.getDouble("EXM_OWN_AMT")
                            + own_amt);
            amtParm.setData("EXM_NHI_AMT", amtParm.getDouble("EXM_NHI_AMT")
                            + nhi_amt);
            break;
        case 3: // ���Ʒ�
            amtParm.setData("TREAT_AMT", amtParm.getDouble("TREAT_AMT") + amt);
            amtParm.setData("TREAT_OWN_AMT", amtParm.getDouble("TREAT_OWN_AMT")
                            + own_amt);
            amtParm.setData("TREAT_NHI_AMT", amtParm.getDouble("TREAT_NHI_AMT")
                            + nhi_amt);
            break;
        case 4: // ������
            amtParm.setData("OP_AMT", amtParm.getDouble("OP_AMT") + amt);
            amtParm.setData("OP_OWN_AMT", amtParm.getDouble("OP_OWN_AMT")
                            + own_amt);
            amtParm.setData("OP_NHI_AMT", amtParm.getDouble("OP_NHI_AMT")
                            + nhi_amt);
            break;
        case 5: // ��λ��
            amtParm.setData("BED_AMT", amtParm.getDouble("BED_AMT") + amt);
            amtParm.setData("BED_OWN_AMT", amtParm.getDouble("BED_OWN_AMT")
                            + own_amt);
            amtParm.setData("BED_NHI_AMT", amtParm.getDouble("BED_NHI_AMT")
                            + nhi_amt);
            break;
        case 6: // ҽ�ò���
            amtParm.setData("MATERIAL_AMT", amtParm.getDouble("MATERIAL_AMT")
                            + amt);
            amtParm.setData("MATERIAL_OWN_AMT", amtParm
                            .getDouble("MATERIAL_OWN_AMT")
                            + own_amt);
            amtParm.setData("MATERIAL_NHI_AMT", amtParm
                            .getDouble("MATERIAL_NHI_AMT")
                            + nhi_amt);
            break;
        case 7: // ����
            amtParm.setData("OTHER_AMT", amtParm.getDouble("OTHER_AMT") + amt);
            amtParm.setData("OTHER_OWN_AMT", amtParm.getDouble("OTHER_OWN_AMT")
                            + own_amt);
            amtParm.setData("OTHER_NHI_AMT", amtParm.getDouble("OTHER_NHI_AMT")
                            + nhi_amt);
            break;
        case 8: // ��ȫѪ
            amtParm.setData("BLOODALL_AMT", amtParm.getDouble("BLOODALL_AMT")
                            + amt);
            amtParm.setData("BLOODALL_OWN_AMT", amtParm
                            .getDouble("BLOODALL_OWN_AMT")
                            + own_amt);
            amtParm.setData("BLOODALL_NHI_AMT", amtParm
                            .getDouble("BLOODALL_NHI_AMT")
                            + nhi_amt);
            break;
        case 9: // �ɷ���Ѫ
            amtParm.setData("BLOOD_AMT", amtParm.getDouble("BLOOD_AMT") + amt);
            amtParm.setData("BLOOD_OWN_AMT", amtParm.getDouble("BLOOD_OWN_AMT")
                            + own_amt);
            amtParm.setData("BLOOD_NHI_AMT", amtParm.getDouble("BLOOD_NHI_AMT")
                            + nhi_amt);
            break;
        }
        amtParm.setData("TOTAL_AMT", amtParm.getDouble("TOTAL_AMT") + amt); // �ܽ��
        amtParm.setData("OWN_AMT", amtParm.getDouble("OWN_AMT") + own_amt); // �Է��ܽ��
    }

    /**
     * ������������ ͳ�ƽ��ʹ��
     * @param confirmTempParm TParm
     * @param ibsParm TParm
     * @param sysFeeParm TParm
     * @param tempParm TParm
     * @param orderParm TParm
     * @param type int int type 1:ת������Ϣ��ѯ 2��ת�걨���� INS_IBS_ORDER ����
     * @return TParm
     */
    private TParm getOrderParm(TParm confirmTempParm, TParm ibsParm, TParm tempParm,
                               TParm orderParm,TParm insParm, int type) {
        if (type == 2) {
            orderParm.addData("YEAR_MON", tempParm.getValue("YEAR_MON")); // �ں�
            orderParm.addData("CASE_NO", tempParm.getValue("CASE_NO")); // �����
            orderParm.addData("INSBRANCH_CODE", confirmTempParm
                              .getValue("INSBRANCH_CODE")); // ������
            orderParm.addData("BILL_DATE", SystemTool.getInstance()
                              .getDateReplace(ibsParm.getValue("BILL_DATE1"), true)); // ��ϸ������ʱ��
            orderParm.addData("HOSP_NHI_NO", confirmTempParm
                              .getValue("NHIHOSP_NO")); // ҽ���������
            orderParm.addData("DOSE_DESC", ibsParm.getValue("DOSE_DESC")); // ��������
            orderParm.addData("OPT_USER", tempParm.getValue("OPT_USER")); // ID
            orderParm.addData("OPT_TERM", tempParm.getValue("OPT_TERM")); // IP

            orderParm.addData("SEQ_NO", count); // ˳���
            orderParm.addData("REGION_CODE", tempParm.getValue("REGION_CODE")); // �������
            orderParm.addData("ADM_SEQ", confirmTempParm.getValue("ADM_SEQ")); // ����˳���
            orderParm.addData("PRICE", ibsParm.getDouble("OWN_PRICE")); // ����
            orderParm.addData("QTY", ibsParm.getDouble("DOSAGE_QTY")); // ����
            orderParm.addData("ADDPAY_AMT", ibsParm.getDouble("ADDPAY_AMT")); // �ۼƽ��
            // ҽ����� =ҽ������*����
            orderParm.addData("TOTAL_NHI_AMT", 0.00);
            orderParm.addData("ADDPAY_FLG", "N"); // �ۼ�����ע��ACCRUAL_FLG?????? ԭ����
            // ��ѯ IBS_ORDD
            orderParm.addData("PHAADD_FLG", "N"); // ����ҩƷע��??????? ԭ���� ��ѯ
            //======pangben 2012-6-11 start �޸ĳ�Ժ��ҩ����
            orderParm.addData("CARRY_FLG", null==ibsParm.getValue("DS_FLG")||ibsParm.getValue("DS_FLG").trim().length()<=0||
            		ibsParm.getValue("DS_FLG").trim().equals("N")?"N":"Y"); // ��Ժ��ҩע�� 
            // IBS_ORDD
            // ԭ����
            // ��ѯ
            // IBS_ORDM
            orderParm.addData("ORDER_CODE", ibsParm.getValue("ORDER_CODE")); // ҽ������
            //�޸�:ҽ������ �Ժ���ҽ������֮ǰ�Ѿ��շѵ�ҽ��ҽ����ʹ�þɵ�,�¿������շ�ҽ��ʹ���µ�
            //======pangben 2012-9-7
            //�жϴ��շ�ʱ���Ƿ���ҽ���ֵ����ݿ�ʼʱ��֮ǰ
			orderParm.addData("NHI_ORDER_CODE", ibsParm
                    .getValue("INS_CODE")); // ҽ��ҽ������
            orderParm.addData("OP_FLG",
            		insParm.getValue("TJDM",0).equals("04") ? "Y" : "N"); // ��������ע��
            orderParm.addData("OWN_RATE", insParm.getDouble("ZFBL1",0)); // �Ը�����
            orderParm.addData("DOSAGE_UNIT", ibsParm.getValue("DOSAGE_UNIT"));//��ҩ��λ
            orderParm.addData("EXE_DEPT_CODE", ibsParm.getValue("EXE_DEPT_CODE"));//ִ�п���
            orderParm.addData("HYGIENE_TRADE_CODE", insParm.getValue("PZWH",0)); // ��׼�ĺ�
            orderParm.addData("ORDER_DESC", ibsParm.getValue("ORDER_DESC")); // ����
            orderParm.addData("STANDARD", null!=ibsParm.getValue("SPECIFICATION") && ibsParm.getValue("SPECIFICATION").length()>=20?//======pangben 20120801 �޸ı��泤��
            		ibsParm.getValue("SPECIFICATION").substring(0,20):ibsParm.getValue("SPECIFICATION")); // ��� 
            orderParm.addData("DOSE_CODE", ibsParm.getValue("DOSE_CODE")); // ����
            count++;
        }
        orderParm.addData("TOTAL_AMT", ibsParm.getDouble("TOT_AMT")); // �������
        orderParm.addData("OWN_AMT", ibsParm.getDouble("OWN_AMT")); // �Էѽ��
        orderParm.addData("NHI_ORD_CLASS_CODE", insParm.getValue("TJDM",0)); // ͳ�ƴ���

        // vRow.add(20, String.valueOf(vIBSOrdMD.get(18)).trim()); //��׼�ĺ�
        return orderParm;
    }

    /**
     * ��ӻ��޸�INS_IBS������
     * @param parm TParm
     * @param tempParm TParm
     */
    private void setInsIbsParm(TParm parm, TParm tempParm) {
        parm.setData("YEAR_MON", tempParm.getValue("YEAR_MON")); // �ں�
        parm.setData("BIRTH_DATE", SystemTool.getInstance().getDateReplace(
                parm.getValue("BIRTH_DATE"), true)); // ��������
        parm.setData("HOSP_NHI_NO", parm.getValue("NHIHOSP_NO")); // ҽ���������
        parm.setData("IN_DATE", SystemTool.getInstance().getDateReplace(
                parm.getValue("IN_DATE"), true)); // ��Ժ����
        parm.setData("DS_DATE", SystemTool.getInstance().getDateReplace(
                parm.getValue("DS_DATE"), true)); // ��Ժ����
        // ����ҽ��ʣ���----�����ҽ�Ʊ������֧���޶�ʣ���????
        parm
                .setData("BASEMED_BALANCE", parm
                         .getDouble("INSBASE_LIMIT_BALANCE"));
        // ҽ�ƾ���ʣ���--��ҽ�ƾ������֧���޶�ʣ���
        parm.setData("INS_BALANCE", parm.getDouble("INS_LIMIT_BALANCE"));
        parm.setData("HOSP_CLS_CODE", parm.getValue("HOSP_CLASS_CODE")); // ҽԺ�ȼ�����
        parm.setData("STATUS", "N"); // ״̬ N,δ�ϴ���S�ϴ��ɹ�
        parm.setData("OPT_USER", tempParm.getValue("OPT_USER")); // ID
        parm.setData("OPT_TERM", tempParm.getValue("OPT_TERM")); // IP
        parm.setData("REGION_CODE", tempParm.getValue("REGION_CODE"));
        String sql =
                " SELECT  A.ICD_CODE ,B.ICD_CHN_DESC,C.DS_DATE  " +
                " FROM ADM_INPDIAG A ,SYS_DIAGNOSIS B,ADM_INP C " +
                " WHERE A.CASE_NO=C.CASE_NO " +
                " AND A.ICD_CODE=B.ICD_CODE(+)" +
                " AND A.CASE_NO ='" + parm.getValue("CASE_NO") +"' " +
                " AND A.MAINDIAG_FLG ='Y' " +
                " AND A.IO_TYPE = 'O' " ;
//                " AND C.CONFIRM_NO='" + parm.getValue("CONFIRM_NO") + "'";
        TParm tempMroParm = new TParm(TJDODBTool.getInstance().select(sql));
        System.out.println("tempMroParmP:"+tempMroParm);
        if (tempMroParm.getErrCode() < 0) {
            return;
        }
        parm.setData("DIAG_CODE", tempMroParm.getValue("ICD_CODE", 0)); //��Ժ���
        parm.setData("DIAG_DESC", tempMroParm.getValue("ICD_CHN_DESC", 0));
        parm.setData("DSDIAG_DESC", tempMroParm.getValue("ICD_CHN_DESC", 0));
        parm.setData("DSDIAG_CODE", tempMroParm.getValue("ICD_CODE", 0));
        //��ô���� 
        parm.setData("DIAG_DESC2", getDiagDesc(parm.getValue("CASE_NO")));//for INS_IBS��
        parm.setData("DSDIAG_DESC2", getDiagDesc(parm.getValue("CASE_NO")));//for INS_ADM_CONFIRM�� 
        
    }
    /**
	 * ��ô����
	 * 
	 * @param caseNo
	 *            String
	 * @return String
	 */
	private String getDiagDesc(String caseNo) {
		String sql = "SELECT ICD_CODE,ICD_DESC AS ICD_CHN_DESC FROM MRO_RECORD_DIAG  WHERE CASE_NO='"
				+ caseNo + "' AND IO_TYPE='O' AND MAIN_FLG='N'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			return "";
		}
		String diagDesc = "";
		for (int i = 0; i < result.getCount(); i++) {
			diagDesc += result.getValue("ICD_CHN_DESC", i) + ",";
		}
		if (diagDesc.length() > 0) {
			diagDesc = diagDesc.substring(0, diagDesc.lastIndexOf(","));
		}
		return diagDesc;
	}

    /**
     * ���÷ָ� ���÷ָť
     * @param parm TParm
     * @return TParm
     */
    public TParm onSaveInsUpLoad(TParm parm) {
        TParm result = new TParm();
        TConnection connection = getConnection();
        // �������
        for (int i = 0; i < parm.getCount("SEQ_NO"); i++) {
            TParm tempParm = parm.getRow(i);
            tempParm.setData("OPT_USER", parm.getValue("OPT_USER"));
            tempParm.setData("OPT_TERM", parm.getValue("OPT_TERM"));
//                        System.out.println("���÷ָ���޸�����>>>"+tempParm);
            result = INSIbsUpLoadTool.getInstance().updateSplit(
                    tempParm, connection);
            if (result.getErrCode() < 0) {
                connection.close();
                return result;
            }
        }
        //�����ַָ����ִ���޸� INS_IBS ������д�λ�����������ҽ�ò��Ϸ�����������
        //TYPE=SINGLE �����ֲ���
        if (null != parm.getValue("TYPE") &&
            parm.getValue("TYPE").equals("SINGLE")) {
            TParm tempParm = new TParm();
            tempParm.setData("CASE_NO", parm.getValue("CASE_NO"));
            tempParm.setData("NHI_ORDER_CODE", "006409"); //��λ����������
            TParm bedParm = INSIbsUpLoadTool.getInstance().queryBedAndMaterial(
                    tempParm);
            if (bedParm.getErrCode() < 0) {
                connection.close();
                return bedParm;
            }
            tempParm.setData("NHI_ORDER_CODE", "006410"); //ҽ�ò��Ϸ�������
            TParm materialParm = INSIbsUpLoadTool.getInstance().
                                 queryBedAndMaterial(tempParm);
            if (materialParm.getErrCode() < 0) {
                connection.close();
                return materialParm;
            }
            tempParm.setData("YEAR_MON", parm.getValue("YEAR_MON")); //�ں�
            tempParm.setData("BED_SINGLE_AMT", bedParm.getDouble("OWN_AMT", 0)); //��λ����������
            tempParm.setData("MATERIAL_SINGLE_AMT",
                             materialParm.getDouble("OWN_AMT", 0)); //ҽ�ò��Ϸ�������
            //�޸Ĵ�λ���������ҽ�ò��Ϸ�������
            result = INSIbsTool.getInstance().updateIbsBedFee(tempParm);
            if (result.getErrCode() < 0) {
                connection.close();
                return result;
            }
        }
        connection.commit();
        connection.close();
        return result;
    }

    /**
     * �ۼ����� ����
     * @param parm TParm
     * @return TParm
     */
    public TParm onAdd(TParm parm) {
        TParm result = new TParm();
        TConnection connection = getConnection();
        //parm.setData("UPLOAD_FLG", "Y");// �ϴ�ע��
        // ���÷ָ�������� INS_IBS
        result = INSIbsTool.getInstance().updateSplitByIns(parm, connection);
        if (result.getErrCode() < 0) {
            connection.close();
            return result;
        }
        // ��ѯ�Ƿ�����ۼ���������
        result = INSIbsUpLoadTool.getInstance().queryIbsUploadAdd(parm);
        if (result.getErrCode() < 0) {
            connection.close();
            return result;
        }
        // ɾ������
        if (result.getCount() > 0) {
            result = INSIbsUpLoadTool.getInstance().deleteAddIbsUpload(parm,
                    connection);
        }
        if (result.getErrCode() < 0) {
            connection.close();
            return result;
        }
        // �ۼ������������
        result = INSIbsUpLoadTool.getInstance().insertINSIbsUploadOne(parm,
                connection);
        if (result.getErrCode() < 0) {
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }

    /**
     * �޸ı�����÷ָ����ϸ����
     * @param parm TParm
     * @return TParm
     */
    public TParm updateUpLoad(TParm parm) {
        TParm result = new TParm();
        TConnection connection = getConnection();
        for (int i = 0; i < parm.getCount(); i++) {
            TParm temp = parm.getRow(i);
            if (null != temp.getValue("FLG")
                && temp.getValue("FLG").length() > 0
                && temp.getValue("SEQ_NO").length() > 0) { // ��ȥ�ϼ�����
                temp.setData("OPT_USER", parm.getValue("OPT_USER"));
                temp.setData("OPT_TERM", parm.getValue("OPT_TERM"));
                temp.setData("REGION_CODE", parm.getValue("REGION_CODE"));
                temp.setData("CHARGE_DATE", SystemTool.getInstance()
                             .getDateReplace(temp.getValue("CHARGE_DATE"), true)); // ��ϸ������
                //У���Ƿ�Ϊ��ֵ
                for (int j = 0; j < nameIbsUpLoadOne.length; j++) {
                    if (null == temp.getValue(nameIbsUpLoadOne[j]) ||
                        temp.getValue(nameIbsUpLoadOne[j]).equals("null")
                        || temp.getValue(nameIbsUpLoadOne[j]).equals("")) {
                        temp.setData(nameIbsUpLoadOne[j], "");
                    }
                }
                if (temp.getBoolean("FLG")) { // ��Ӳ���
                    result = INSIbsUpLoadTool.getInstance()
                             .insertINSIbsUploadOne(temp, connection);
                } else {
                    // �޸Ĳ���
                    result = INSIbsUpLoadTool.getInstance()
                             .updateINSIbsUploadOne(temp, connection);
                }
            }
            if (result.getErrCode() < 0) {
                connection.close();
                return result;
            }
        }
        connection.commit();
        connection.close();
        return result;
    }
}
