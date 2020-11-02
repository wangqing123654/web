package jdo.ins;

import java.util.Properties;
import javax.swing.JOptionPane;
import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) BlueCore 2011</p>
 *
 * <p>Company: BlueCore</p>
 *
 * @author wangl 2011.12.07
 * @version 1.0
 */
public class InsManager {
    /**
     * Debug���<BR>
     * true Debug״̬<BR>
     * false ����״̬
     * @alias Debug���
     */
    public static boolean DEBUG;

    /**
     * ʵ��������
     * @alias ʵ��
     */
    private static InsManager instanceObject;


    /**
     * ��������
     * @alias ��������
     */
    public final static String ACTION_CLASS_NAME = "action.ins.INSActionTJ";

    /**
     * ��������(��̬Action)
     * @alias ��������
     */
    public final static String ACTION_ENGINE_CLASS_NAME =
        "action.ins.InsUpdateEngine";

    /**
     * ��������
     * @alias ��������
     */
    private TAction actionObject;

    /**
     * ��������(��̬Action)
     * @alias ��������
     */
    private TAction actionEngineObject;
    /**
     * ���Ψһʵ��
     * @alias ���Ψһʵ��
     * @return InsManager
     */
    public static synchronized InsManager getInstance() {
        if (instanceObject == null) {
            instanceObject = new InsManager();
        }
        return instanceObject;
    }

    /**
     * ���캯��
     * @alias ���캯��
     */
    public InsManager() {
        actionObject = new TAction();
        actionEngineObject = new TAction();
    }

    //========================================//
    // ҽ�����λ����
    //========================================//
    /**
     * ����ҽ������״̬
     * @return TParm NHI_DEBUG,REG_NHI_ONLINE,OPB_NHI_ONLINE<BR>
     * NHI_DEBUG<BR>
     * 0 ��׼����������<BR>
     * 1 ���ԣ��������ҽ����̬�������õĲ����ͽ������־�ļ�<BR>
     * REG_NHI_ONLINE<BR>
     * 0 ��ʱ���ӣ��ڹҺű���ͬʱ��ֻ�ǽ��Һ�����д������ҽ����ϸ����Ժ�վ�ҵ���д���ҽ��ҵ��<BR>
     * 1 ʵʱ���ӣ��ڹҺű���ͬʱ���������Һ�����д������ҽ����ϸ����Ҫ��������ʵʱ����<BR>
     * OPB_NHI_ONLINE<BR>
     * 0 ��ʱ���ӣ����շѱ���ͬʱ��ֻ�ǽ���������д������ҽ����ϸ��<BR>
     * 1 ʵʱ���ӣ����շѱ���ͬʱ����������������д������ҽ����ϸ����Ҫ��������ʵʱ����<BR>
     */
    private TParm getNhiFlg() {
        TParm parm = new TParm();
        parm.setData("HOSP_AREA", "HIS");
        TParm result = (TParm) actionObject.executeAction(parm, "INSActionTJ", "getNhiFlg");
        if (result.getErrCode() < 0) {
            messageBox(result.getErrName());
            return null;
        }
        return result;
    }

    /**
     * �Ƿ��ǵ���״̬
     * @return boolean<BR>
     *  true ���ԣ��������ҽ����̬�������õĲ����ͽ������־�ļ�<BR>
     *  false ��׼����������<BR>
     */
    public boolean isDebug() {
        TParm result = getNhiFlg();
        if (result == null || result.getErrCode() != 0)
            return false;
        return result.getInt("NHI_DEBUG", 0) == 1;
    }

    /**
     * REG �Ƿ�ʵʱ����
     * @return boolean<BR>
     *   true ʵʱ���ӣ��ڹҺű���ͬʱ���������Һ�����д������ҽ����ϸ����Ҫ��������ʵʱ����<BR>
     *   false ��ʱ���ӣ��ڹҺű���ͬʱ��ֻ�ǽ��Һ�����д������ҽ����ϸ����Ժ�վ�ҵ���д���ҽ��ҵ��<BR>
     */
    public boolean isRegOnline() {
        TParm result = getNhiFlg();
        if (result == null || result.getErrCode() != 0)
            return false;
        return result.getInt("REG_NHI_ONLINE", 0) == 1;
    }

    /**
     * REG �Ƿ�ʵʱ����
     * @return boolean<BR>
     *  true ʵʱ���ӣ����շѱ���ͬʱ����������������д������ҽ����ϸ����Ҫ��������ʵʱ����<BR>
     *  false ��ʱ���ӣ����շѱ���ͬʱ��ֻ�ǽ���������д������ҽ����ϸ��<BR>
     */
    public boolean isOpbOnline() {
        TParm result = getNhiFlg();

        if (result == null || result.getErrCode() != 0)
            return false;
        return result.getInt("OPB_NHI_ONLINE", 0) == 1;
    }


    /**
     * ҽ�ƿ�����
     * @param type int
     * "1" ��ְ ����
     * "2" ��ְ ����
     * "3" �Ǿ� ���� (��)
     * "4" �Ǿ� ����
     * @return TParm
     */
//  public TParm readCard(String type)
//  {
//    //System.out.println("----------a");
//    ReadCard readCard = new ReadCard(type);
//    //System.out.println("----------b");
//    readCard.setVisible(true);
//    return (TParm)readCard.getReturnValue();
//  }
    /**
     * ��̬Action
     * @param parm TParm
     * @return TParm
     */
    public TParm doAction(TParm parm) {
        return (TParm) actionEngineObject.executeAction(parm, "InsUpdateEngine", "doAction");
    }

    /**
     * �Ƿ���ҽ������
     * @return boolean<BR>
     * true ��ҽ������<BR>
     * false ����ҽ������<BR>
     */
    public boolean isInsWindow() {
        TParm parm = new TParm();
        parm.setData("HOSP_AREA", "HIS");
        parm.setData("TERM_IP", publics.Comm_Operator.user_IP);
        parm.setData("ACTION_ID", "INS_GET_YB_ONLINE");
        TParm result = doAction(parm);
        if (result.getErrCode() != 0)
            return false;
        return result.getBoolean("YB_ONLINE_FLG", 0);
    }

    /**
     * �Ƿ���ҽ�����
     * @param ctzCode String ��ݴ���
     * @return boolean<BR>
     * true ��ҽ�����<BR>
     * false ����ҽ�����
     */
    public boolean isInsCtz(String ctzCode) {
        TParm parm = new TParm();
        parm.setData("HOSP_AREA", "HIS");
        parm.setData("CTZ_CODE", ctzCode);
        parm.setData("ACTION_ID", "INS_GET_CTZ_IS_INS");
        TParm result = doAction(parm);
        if (result.getErrCode() != 0)
            return false;
        return result.getBoolean("NHI_CTZ_FLG", 0);
    }

    public TParm getNhiRegCode(String admType, String clinicTypeCode) {
        TParm parm = new TParm();
        parm.setData("HOSP_AREA", "HIS");
        parm.setData("ADM_TYPE", admType);
        parm.setData("CLINICTYPE_CODE", clinicTypeCode);
        parm.setData("ACTION_ID", "INS_GET_NHI_CODE");
        parm.setData("ACTION_ID", "INS_GET_NHI_CODE");
        return doAction(parm);
    }

    /**
     * ��ߴ�λ��
     * @return double
     */
    public double getTipBedprice() {
        TParm parm = new TParm();
        parm.setData("HOSP_AREA", "HIS");
        parm.setData("REGION_CODE","HIS");
        parm.setData("ACTION_ID", "INS_GETTIP_BEDPRICE");
        TParm result = doAction(parm);
        if (result.getErrCode() != 0) {
            messageBox("��ߴ�λ�Ѷ�ȡʧ��!" + result.getErrName());
            return -1;
        }
        return result.getDouble("TIP_BEDPRICE", 0);
    }

    /**
     * ���÷ָ�
     * @param admType String
     * @param clinicTypeCode String
     * @param ctz1Code String
     * @return TParm
     */
    public TParm regFeePartition(String admType, String clinicTypeCode,
                                 String ctz1Code) {
        TParm parm = new TParm();
        //����
        parm.setData("REGION_CODE", "HIS");
        //��Ա���
        parm.setData("CTZ1_CODE", ctz1Code);
        //�ż�ס��
        parm.setData("ADM_TYPE", admType);
        //�ű�
        parm.setData("CLINICTYPE_CODE", clinicTypeCode);
        //ҽԺ����
        parm.setData("HOSP_NHI_NO", "000551");

        return (TParm) actionObject.executeAction(parm, "INSActionTJ", "regFeePartition");
    }

    /**
     * ��ʾ��Ϣ����
     * @param text ��ʾ��Ϣ
     * @alias ��Ϣ�Ի���(����)
     */
    public void messageBox(String text) {
        if (DEBUG) {
            return;
        }
        JOptionPane.showMessageDialog(null, text);
    }

    //========================================//
    // ������ ���ڵ��Գ���,����������
    //========================================//
    public Properties test_Data() {
        Properties prop = new Properties();
        prop.setProperty("HOSP_AREA", "HIS");
        return prop;
    }

    public boolean test_getNhiFlg() {
        //System.out.println("========>test_getNhiFlg()");
        TParm result = getNhiFlg();
        //System.out.println("outPut:" + result);
        boolean state = result.getErrCode() == 0;
       // System.out.println("state:" + state);
        return state;
    }

    public boolean test_NhiFlg1() {
//        System.out.println("isDebug()=" + isDebug());
//        System.out.println("isRegOnline()=" + isRegOnline());
//        System.out.println("isOpbOnline()=" + isOpbOnline());
        return true;
    }

    /**
     * ҽ���ӿڷ���
     * @param parm TParm
     * @return TParm
     */
    public TParm safe(TParm parm) {
        TParm result = new TParm();
        //�������ݼ��
        if (parm.checkEmpty("PIPELINE,PLOT_TYPE", result)) {
            result.setErr(-1,result.getErrName()+result.getErrText());
            return null;
        }
        parm.setData("HOSP_AREA", "HIS");
        Object obj = INSDebugTool.getInstance().INSInterface(parm);
       // System.out.println("action obj" + obj);
        if (obj instanceof String) {
            result.setErr(-1,result.getErrName()+result.getErrText());
           // System.out.println(obj);
            return null;
        }
        result = (TParm) obj;
        if (result.getErrCode() < 0) {
            result.setErr(-1,result.getErrName()+result.getErrText());
            return result;
        }
        return result.getRow(0);
    }

    /**
     * ҽ���ӿڷ���(����)
     * @param parm TParm
     * @param str String
     * @return TParm
     */
    public TParm safe(TParm parm, String str) {
        TParm result = new TParm();
        //�������ݼ��
        if (parm.checkEmpty("PIPELINE,PLOT_TYPE", result)) {
            result.setErr(-1,result.getErrName()+result.getErrText());
            return result;
        }
        parm.setData("HOSP_AREA", "HIS");
        Object obj = INSDebugTool.getInstance().INSInterface(parm);
      //  System.out.println("action obj" + obj);
        if (obj instanceof String) {
            result.setErr(-1,result.getErrName()+result.getErrText());
           // System.out.println(obj);
            return result;
        }
        result = (TParm) obj;
        if (result.getErrCode() < 0) {
            result.setErr(-1,result.getErrName()+result.getErrText());
            return result;
        }
        return result;
    }
}
