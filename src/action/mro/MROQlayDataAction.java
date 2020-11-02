package action.mro;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.mro.MROQlayControlMTool;
import jdo.mro.MROChrtvetrecTool;
import com.dongyang.data.TNull;
import com.sun.jmx.snmp.Timestamp;
import jdo.mro.MRORecordTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;
import jdo.sys.SystemTool;
import jdo.mro.MROChrtvetstdTool;

/**
 * <p>Title: </p>
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
public class MROQlayDataAction extends TAction {
    /**
     * ��ʱ�������ۼƲ���һ����Ŀ�ķ����Ƿ񳬳�Ĭ�Ϸ��������������ʹ��Ĭ�Ϸ�ֵ
     */
    private double [] priceTemp=null;
    
    public MROQlayDataAction() {
    }

    /**
     * �޸ı��淽��
     * @param parm TParm
     * @return TParm
     */
    public TParm saveQlayControlm(TParm parms) {
        TConnection conn = getConnection();
        double sumPrice = 0.0;//�ܷ�
        TParm result = null;
        TParm parmC = null; //�۷ֱ�����
        TParm parm=parms.getParm("SCODEPARM");//��¼�����������ʿ���Ϣ
        TParm selPrice=parms.getParm("selPrice");//��¼ÿ����֧���ܷ�
        priceTemp= new double[selPrice.getCount("DESCRIPTION")];
        String type=parms.getValue("TYPE");//�ύ����
        for (int i = 0; i < parm.getCount(); i++) {
            if (parm.getValue("ISTRUE",i).equals("1")) {//�޸�����(ISTRUE=1)
                //�۷��������Ѳ�״̬��δͨ������Ŀ�۷�
                if ("1".equals(parm.getValue("QUERYSTATUS",i)) &&
                    "0".equals(parm.getValue("STATUS",i))) {
                    priceStat(selPrice,parm.getValue("EXAMINE_CODE",i),parm,i);
                }
                //ִ���޸�
                result = MROQlayControlMTool.getInstance().onUpdate(parm.getRow(i), conn);//����MRO_QLAYCONTROLM��
                if (result.getErrCode() < 0) {
                    conn.close();
                    result.setErrText("�ʿؼ�¼������¼����");
                    return result;
                }
                parmC = saveMROChrtvetrec(parm, i);
                result = MROChrtvetrecTool.getInstance().updatedata(parmC, conn); //�޸Ŀ۷ֱ�����:��¼�˹��ʿ���Ŀ�ж�����ۼƷ���
                if (result.getErrCode() < 0) {
                    conn.close();
                    result.setErrText("��¼������˵���¼����");
                    return result;
                }
            } else { //�������(ISTRUE=0)
                //�۷��������Ѳ�״̬��δͨ������Ŀ�۷�
                if ("1".equals(parm.getValue("QUERYSTATUS",i))) {//modify  by wanglong 20121106
                    priceStat(selPrice,parm.getValue("EXAMINE_CODE",i),parm,i);
                    result = MROQlayControlMTool.getInstance().onInsert(parm.getRow(i), conn);//����MRO_QLAYCONTROLM��
                    if (result.getErrCode() < 0) {
                        conn.close();
                        result.setErrText("�ʿؼ�¼������¼����");
                        return result;
                    }
                    parmC = saveMROChrtvetrec(parm, i);
                    result = MROChrtvetrecTool.getInstance().insertdata(parmC, conn);//�����۷ֱ�����
                    if (result.getErrCode() < 0) {
                        conn.close();
                        result.setErrText("��¼������˵���¼����");
                        return result;
                    }
                }
            }
        }
        conn.commit();
        conn.close();
        conn=getConnection();
        //�޸Ĳ����ʿط������Լ����״̬
        for(int i=0;i<priceTemp.length;i++){
            int price=selPrice.getInt("DESCRIPTION",i);
            if (price <= priceTemp[i]) { //��������Ĭ��������
                sumPrice += price;
            } else {
                sumPrice += priceTemp[i];
            }
        }
        TParm parmR=new TParm();
        parmR.setData("MR_NO",parm.getValue("MR_NO",0));
        parmR.setData("CASE_NO",parm.getValue("CASE_NO",0));
        parmR.setData("IPD_NO",parm.getValue("IPD_NO",0));
        parmR.setData("TYPERESULT",type);
        parmR.setData("SUMSCODE", 100 - sumPrice);
   
        //�ύ��������޸�
        if ("0".equals(type)) {//δ���
            // ==============add by wanglong 20131025
            parmR.setData("MRO_CHAT_FLG", "1");
            result = MROChrtvetrecTool.getInstance().updateMRO_CHAT_FLG(parmR, conn);
            if (result.getErrCode() < 0) {
                conn.close();
                result.setErrText("�޸��ύ״̬����");
                return result;
            }
            // ==============add end
            result = MRORecordTool.getInstance().updateTYPERESULT(parmR, conn); //�޸ķ��������״̬
            if (result.getErrCode() < 0 || !"".equals(result.getErrText())) {
                conn.close();
                result.setErrText("�޸����ݴ���");
                return result;
            }
        } else {// �����
            // ==============add by wanglong 20131025
            parmR.setData("MRO_CHAT_FLG", "2");
            result = MROChrtvetrecTool.getInstance().updateMRO_CHAT_FLG(parmR, conn);
            if (result.getErrCode() < 0) {
                conn.close();
                result.setErrText("�޸��ύ״̬����");
                return result;
            }
            // ==============add end
            result = MRORecordTool.getInstance().updateTYPERESULT(parmR, conn); //�޸ķ��������״̬
            if (result.getErrCode() < 0 || !"".equals(result.getErrText())) {
                conn.close();
                result.setErrText("�޸����ݴ���");
                return result;
            }
        }
        conn.commit();
        conn.close();
        result.setData("RESULTPRICE", 100 - sumPrice);
        result.setData("TYPERESULT", type);
        return result;
    }
    
    /**
     * �ۼ�ÿһ��ķ�ֵ
     * @param parmNode TParm �ӽڵ�����
     * @param EXAMINE_CODE String ��ǰ������Ŀid��
     * @param parm TParm //��������
     */
    private void priceStat(TParm selPrice,String EXAMINE_CODE,TParm parm,int index){
       for(int i=0;i<selPrice.getCount("CATEGORY_CODE");i++){
           if(EXAMINE_CODE.substring(0,3).equals(selPrice.getValue("CATEGORY_CODE",i))){
               priceTemp[i]+=parm.getDouble("SCORE", index)*parm.getInt("DEDUCT_SCORECOUNT", index);
               break;
           }
       }
    }
    
    /**
     * ��¼������˵� �������
     * @param parm TParm
     * @param i int
     * @return TParm
     */
    private TParm saveMROChrtvetrec(TParm parm, int i) {
        TParm parmC = new TParm();
        parmC.setData("CASE_NO", parm.getValue("CASE_NO", i));
        parmC.setData("OLD_EXAMINE_CODE", parm.getValue("EXAMINE_CODE", i));
        parmC.setData("EXAMINE_CODE", parm.getValue("EXAMINE_CODE", i));
        parmC.setData("OLD_EXAMINE_DATE", parm.getValue("EXAMINE_DATE", i).substring(0, 8));
        String EXAMINE_DATE=StringTool.getString(SystemTool.getInstance().getDate(), "yyyyMMddHHmmss");
        parmC.setData("EXAMINE_DATE", EXAMINE_DATE.substring(0, 8));
        parmC.setData("IPD_NO", parm.getValue("IPD_NO", i));
        parmC.setData("MR_NO", parm.getValue("MR_NO", i));
        //parmC.setData("VS_CODE", parm.getValue("OPT_USER", i));
        parmC.setData("VS_CODE", parm.getValue("VS_CODE", i));//����ҽ�� modify by wanglong 20121105
        //parmC.setData("DEDUCT_SCORE", parm.getValue("SCORE", i));
        parmC.setData("DEDUCT_SCORE", Double.parseDouble(parm.getValue("SCORE", i)));//modify by wanglong 20121105
        parmC.setData("DEDUCT_NOTE", "");
        parmC.setData("URG_FLG", parm.getValue("URG_FLG", i));
        if (parm.getData("REPLY_DTTM", i) == null || parm.getValue("REPLY_DTTM", i).equals("")) {
            parmC.setData("REPLY_DTTM", "");// modify by wanglong 20121111
        } else {
            parmC.setData("REPLY_DTTM", parm.getData("REPLY_DTTM", i));
        }
        parmC.setData("REPLY_DR_CODE", parm.getData("REPLY_DR_CODE", i));
        parmC.setData("REPLY_REMK", parm.getData("REPLY_REMK", i)); 
        parmC.setData("OPT_USER", parm.getValue("OPT_USER", i));
        //parmC.setData("DEDUCT_SCORECOUNT", parm.getValue("DEDUCT_SCORECOUNT", i));
        parmC.setData("DEDUCT_SCORECOUNT", Integer.parseInt(parm.getValue("DEDUCT_SCORECOUNT", i)));//modify by wanglong 20121105
        parmC.setData("OPT_TERM", parm.getValue("OPT_TERM", i));
        return parmC;
    }
}
