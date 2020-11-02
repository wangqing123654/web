package action.clp;

import jdo.clp.CLPManagedTool;
import com.dongyang.db.TConnection;
import com.dongyang.data.TParm;
import java.util.Map;
import com.dongyang.action.TAction;
import jdo.clp.CLPVariationTool;
import jdo.sys.SystemTool;
import com.dongyang.util.StringTool;
import com.dongyang.data.TNull;
import java.sql.Timestamp;

/**
 * <p>Title: �ٴ�·���������action</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class CLPVariantionAction extends TAction {
    public CLPVariantionAction() {
    }

    public TParm saveVariance(TParm saveParm) {
        TParm basicParm = new TParm((Map) saveParm.getData("varianceParm"));
        Map operatorMap = (Map) saveParm.getData("operatorMap");
        TConnection conn = getConnection();
        TParm result = new TParm();
        for (int i = 0; i < basicParm.getCount() - 1; i++) {
            if (!"��".equals(basicParm.getRow(i).getValue("STATUS"))) {
                result = CLPVariationTool.getInstance().savaVariance(basicParm.
                        getRow(i), conn);
            } else {
                TParm addParm = basicParm.getRow(i);
                //System.out.println("data:" + addParm);
                if (!("".equals(addParm.getValue("CHKTYPE_CODE")) ||
                      "".equals(addParm.getValue("CHKUSER_CODE"))||"".equals(addParm.getValue("MAIN_ORDER_CODE_DESC")))){
                    addParm.setData("ORDER_NO",SystemTool.getInstance().getNo("ALL", "CLP", "CLP","ORDERNO"));
                    TParm parmSave= this.cloneTParm(addParm);
                    //�����ܷ�
                    addMainAMTIntoTParm(parmSave,conn);
                    result = CLPVariationTool.getInstance().addVariance(parmSave,
                            conn);
                    conn.commit();
                }
                //չ��clp_bill
                String regionCode = addParm.getValue("REGION_CODE");
                String clncpath_code = addParm.getValue("CLNCPATH_CODE");
                String case_no = addParm.getValue("CASE_NO");
                boolean flag = deployBill(regionCode, case_no, clncpath_code,
                                          conn, operatorMap);
                if (!flag) {
                    conn.rollback();
                    conn.close();
                    return result;
                }
            }
            if (result.getErrCode() < 0) {
                conn.close();
                return result;
            }

        }
        conn.commit();
        conn.close();
        return result;
    }
    /**
     * �����ܷ���
     * @param parmSave TParm
     * @param conn TConnection
     */
    private void addMainAMTIntoTParm(TParm parmSave,TConnection conn){
        //��������
        String orderCode = parmSave.getValue("MAINORD_CODE");
        TParm parm = new TParm();
        parm.setData("ORDER_CODE", orderCode);
        TParm ownPriceParm = CLPManagedTool.getInstance().
                             selectOwnPrice(parm, conn);
        double ownPrice = ownPriceParm.getCount() > 0 ?
                          ownPriceParm.getDouble("OWN_PRICE", 0) :
                          0;
        double mainTot = parmSave.getDouble("MAINTOT");
        double totAmt = ownPrice * mainTot;
        parmSave.setData("MAIN_AMT", totAmt);
    }
    private boolean deployBill(String regionCode, String case_no,
                               String clncpath_code, TConnection conn,
                               Map operatorMap) {
        boolean flag = true;
        //ɾ������
        TParm parmDelete = new TParm();
        parmDelete.setData("REGION_CODE", regionCode);
        parmDelete.setData("CASE_NO", case_no);
        parmDelete.setData("CLNCPATH_CODE", clncpath_code);
        TParm result = CLPManagedTool.getInstance().deleteCLPBill(parmDelete,
                conn);
        if (result.getErrCode() < 0) {
            flag = false;
            return flag;
        }else{
            conn.commit();
        }

        //չ����������
        flag = insertBill(regionCode, case_no, clncpath_code, conn, operatorMap);
        return flag;
    }

    /**
     * �����˵�
     * @param regionCode String
     * @param case_no String
     * @param clncpath_code String
     * @param conn TConnection
     * @param operatorMap Map
     * @return boolean
     */
    private boolean insertBill(String regionCode, String case_no,
                               String clncpath_code, TConnection conn,
                               Map operatorMap) {
        boolean flag = true;
        TParm selectParm = new TParm();
        selectParm.setData("REGION_CODE", regionCode);
        selectParm.setData("CASE_NO", case_no);
        selectParm.setData("CLNCPATH_CODE", clncpath_code);
        TParm result = CLPManagedTool.getInstance().
                       selectCLPManagedWithCondition(selectParm, conn);
      //=============pangben 2012-6-1 start
		String [] billName=new String[1];//ʱ�̱Ƚ���ͬ���ۼӽ��ʹ��
		double [] sum=new double[1];//ʱ���ۼӵĽ��
		String [] sqlName=new String[1];//sql �����Ҫ��õķ�����Ŀ
		String [] sqlTotName=new String[1];//sql �����Ҫ��õķ�����Ŀ���
		//=============pangben 2012-6-1 stop
        for (int i = 0; i < result.getCount(); i++) {
            TParm rowParm = result.getRow(i);
            flag = deployDuringBill(rowParm, conn, operatorMap,billName,sum,sqlName,sqlTotName);
            if (!flag) {
                return flag;
            }
        }
        return flag;
    }

    /**
	 * billName//ʱ�̱Ƚ���ͬ���ۼӽ��ʹ��
	 * sum//ʱ���ۼӵĽ��
	 * sqlName//sql �����Ҫ��õķ�����Ŀ
	 * sqlTotName//sql �����Ҫ��õķ�����Ŀ���
	 * ���õ���ʱ����Ϣ��ӻ���µ�CLP_BLL�� =========pangben 2012-5-24 ��Ӳ��� �Ż�����
	 */
    private boolean deployDuringBill(TParm managedParm, TConnection conn,
                                     Map operatorMap,String [] billName,
                         			double []sum,String [] sqlName,String []sqlTotName) {
        boolean flag = true;
        managedParm.setData("OPT_USER", operatorMap.get("OPT_USER"));
        managedParm.setData("OPT_DATE", operatorMap.get("OPT_DATE"));
        managedParm.setData("OPT_TERM", operatorMap.get("OPT_TERM"));
        managedParm.setData("REGION_CODE", operatorMap.get("REGION_CODE"));
        //��¼��׼��clp_bill
        flag = recordCLPBill(managedParm, conn, "1",billName,sum,sqlName,sqlTotName);
        if (!flag) {
            return false;
        }
        //��¼ʵ�ʵ�clp_bill
        flag = recordCLPBill(managedParm, conn, "2",billName,sum,sqlName,sqlTotName);
        if (!flag) {
            return false;
        }
        return flag;
    }

    private boolean recordCLPBill(TParm managedParm, TConnection conn,
                                  String schdType,String [] billName,
                      			double []sum,String [] sqlName,String []sqlTotName) {
        boolean flag = true;
        //�����ʼ����
        managedParm.setData("SCHD_TYPE", schdType);
        if ("1".equals(schdType)) {
            managedParm.setData("CHARGE", managedParm.getValue("TOT_AMT"));
        } else {
            managedParm.setData("CHARGE", managedParm.getValue("MAIN_AMT"));
        }
        //System.out.println("CHARGE:"+managedParm.getValue("CHARGE"));
        TParm stardardCLPBill = this.cloneTParm(managedParm);
        TParm existResult = CLPManagedTool.getInstance().checkCLPBillExist(this.
                cloneTParm(managedParm));
        TParm result = null; //ִ�н��parm
        //System.out.println("parm:"+stardardCLPBill);
        String columnName = getColumnNameWithCondition(stardardCLPBill.getValue(
                "MAINORD_CODE"));//����ʱ��Ҫʹ��Main_OrderCode ��Ϊ��ѯ����--------
        if("".equals(columnName)){
            return flag;
        }
        if (existResult.getDouble("TOTALCOUNT", 0) > 0) {
             //System.out.println("CLP_BILL���ݴ�����Ҫ����");
            result = CLPManagedTool.getInstance().updateCLPBill(managedParm,
                    conn,  sum,sqlName,schdType,managedParm.getValue("SCHD_CODE"));
        } else {
            //System.out.println("CLP_BILL���ݲ�������Ҫ���");
            result = CLPManagedTool.getInstance().saveCLPBill(managedParm, conn,
            		 sum,sqlName,sqlTotName,schdType,managedParm.getValue("SCHD_CODE"));
        }
        if (result.getErrCode() < 0) {
            flag = false;
        } else {

        }
        conn.commit();
        return flag;
    }

    /**
     * ��TParm�м���ϵͳĬ����Ϣ
     * @param parm TParm
     */
    private void putBasicSysInfoIntoParm(TParm parm, Map OperatorMap) {
        int total = parm.getCount();
        //System.out.println("total" + total);
        parm.setData("REGION_CODE", OperatorMap.get("REGION_CODE"));
        parm.setData("OPT_USER", OperatorMap.get("OPT_USER"));
        Timestamp today = SystemTool.getInstance().getDate();
        String datestr = StringTool.getString(today, "yyyyMMdd");
        parm.setData("OPT_DATE", datestr);
        parm.setData("OPT_TERM", OperatorMap.get("OPT_TERM"));
    }

    /**
     * ����TParm ���null�ķ���
     * @param parm TParm
     * @param keyStr String
     * @param type Class
     */
    private void putTNullVector(TParm parm, String keyStr, Class type) {
        for (int i = 0; i < parm.getCount(); i++) {
            if (parm.getData(keyStr, i) == null) {
                //System.out.println("����Ϊ�����");
                TNull tnull = new TNull(type);
                parm.setData(keyStr, i, tnull);
            }
        }
    }

    /**
     * ����TParm ��nullֵ����
     * @param parm TParm
     * @param keyStr String
     * @param type Class
     */
    private void putTNull(TParm parm, String keyStr, Class type) {
        if (parm.getData(keyStr) == null) {
            //System.out.println("����Ϊ�����");
            TNull tnull = new TNull(type);
            parm.setData(keyStr, tnull);
        }
    }

    /**
     * ����Ƿ�Ϊ�ջ�մ�
     * @return boolean
     */
    private boolean checkNullAndEmpty(String checkstr) {
        if (checkstr == null) {
            return false;
        }
        if ("".equals(checkstr)) {
            return false;
        }
        return true;
    }

    /**
     * ��¡����
     * @param parm TParm
     * @return TParm
     */
    private TParm cloneTParm(TParm from) {
        TParm returnTParm = new TParm();
        for (int i = 0; i < from.getNames().length; i++) {
            returnTParm.setData(from.getNames()[i],
                                from.getData(from.getNames()[i]));
        }
        return returnTParm;
    }

    private String getColumnNameWithCondition(String orderCode) {
        String columnName = "";
        TParm selectParm = new TParm();
        selectParm.setData("ORDER_CODE", orderCode);
        TParm result = CLPManagedTool.getInstance().getCLPBillColumName(
                selectParm);
        //System.out.println("��ѯ���������Ľ����" + result);
        if (result.getCount() > 0) {
            columnName = result.getValue("COLUMNNAME", 0);
        }
        return columnName;
    }


}
