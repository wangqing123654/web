package jdo.mro;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;
import java.sql.Connection;
import com.dongyang.db.TConnection;

/**
 * <p>Title:MRO�ӿڷ���(��̨Tool��ǰ��̨��Ҫ����) </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author zhangk 2009-4-28
 * @version 1.0
 */
public class MROInterfaceTool
    extends TJDODBTool {
    public MROInterfaceTool() {
    }
    /**
    * ʵ��
    */
   public static MROInterfaceTool instanceObject;

   /**
    * �õ�ʵ��
    * @return SYSRegionTool
    */
   public static MROInterfaceTool getInstance() {
       if (instanceObject == null)
           instanceObject = new MROInterfaceTool();
       return instanceObject;
   }
   /**
    * ת����ת����
    * @param parm TParm  ������Ϣ�� CASE_NO,OPT_USER,OPT_TERMΪ������������� ��ϲ�����ѡ ==>
    * TRANS_DEPT     ת�ƿƱ�
    * OUT_DATE       ��Ժ����
    * OUT_DEPT       ��Ժ�Ʊ�
    * OUT_STATION    ��Ժ����
    * OUT_ROOM_NO    ��Ժ����
    * REAL_STAY_DAYS ʵ��סԺ����
    * @param conn TConnection
    * @return TParm
    */
   public TParm updateTransDept(TParm parm, TConnection conn) {
       TParm result = new TParm();
       String sql = "UPDATE MRO_RECORD SET ";
       if (parm.getData("TRANS_DEPT") != null) //ת�ƿƱ�
           sql += " TRANS_DEPT='" + parm.getValue("TRANS_DEPT") + "',";
       if (parm.getData("OUT_DATE") != null) //��Ժ����
           sql += " OUT_DATE=TO_DATE('" + parm.getValue("OUT_DATE") +
               "','YYYYMMDD'),";
       if (parm.getData("OUT_DEPT") != null) //��Ժ�Ʊ�
           sql += " OUT_DEPT='" + parm.getValue("OUT_DEPT") + "',";
       if (parm.getData("OUT_STATION") != null) //��Ժ����
           sql += " OUT_STATION='" + parm.getValue("OUT_STATION") + "',";
       if (parm.getData("OUT_ROOM_NO") != null) //��Ժ����
           sql += " OUT_ROOM_NO='" + parm.getValue("OUT_ROOM_NO") + "',";
       if (parm.getData("REAL_STAY_DAYS") != null) //ʵ��סԺ����
           sql += " REAL_STAY_DAYS='" + parm.getValue("REAL_STAY_DAYS") +
               "',";
       sql += " OPT_USER='"+parm.getValue("OPT_USER")+"',OPT_TERM='"+parm.getValue("OPT_TERM")+"',OPT_DATE=SYSDATE ";
       if (parm.getData("CASE_NO") != null)
           sql += " WHERE CASE_NO='" +
               parm.getValue("CASE_NO") + "'";
       result.setData(this.update(sql, conn));
       if (result.getErrCode() < 0) {
           err("jdo.mro.MROInterfaceTool.updateTransDept==>ERR:" +
               result.getErrCode() + result.getErrText() +
               result.getErrName());
           return result;
       }
       return result;
   }
   /**
    * �޸������Ϣ
    * @param parm TParm ������Ϣ�� CASE_NO,OPT_USER,OPT_TERMΪ������������� ��ϲ�����ѡ ==>
    * OE_DIAG_CODE      �ż������
    * IN_DIAG_CODE      ��Ժ���
    * OUT_DIAG_CODE1    ��Ժ�����
    * OUT_DIAG_CODE2    ��Ժ�ڶ����
    * OUT_DIAG_CODE3    ��Ժ�������
    * OUT_DIAG_CODE4    ��Ժ�������
    * OUT_DIAG_CODE5    ��Ժ�������
    * OUT_DIAG_CODE6    ��Ժ�������
    * INTE_DIAG_CODE    Ժ�ڸ�Ⱦ���
    * @return TParm
    */
   public TParm updateDiag(TParm parm,TConnection conn){
       TParm result = new TParm();
       String sql = "UPDATE MRO_RECORD SET ";
       if(parm.getData("OE_DIAG_CODE")!=null)//�ż������
           sql += " OE_DIAG_CODE = '" + parm.getValue("OE_DIAG_CODE") + "',";
       if(parm.getData("IN_DIAG_CODE")!=null)//��Ժ���
           sql += " IN_DIAG_CODE = '" + parm.getValue("IN_DIAG_CODE") + "',";
       if(parm.getData("OUT_DIAG_CODE1")!=null)//��Ժ�����
           sql += " OUT_DIAG_CODE1 = '" + parm.getValue("OUT_DIAG_CODE1") + "',";
       if(parm.getData("OUT_DIAG_CODE2")!=null)//��Ժ�ڶ����
           sql += " OUT_DIAG_CODE2 = '" + parm.getValue("OUT_DIAG_CODE2") + "',";
       if(parm.getData("OUT_DIAG_CODE3")!=null)//��Ժ�������
           sql += " OUT_DIAG_CODE3 = '" + parm.getValue("OUT_DIAG_CODE3") + "',";
       if(parm.getData("OUT_DIAG_CODE4")!=null)//��Ժ�������
           sql += " OUT_DIAG_CODE4 = '" + parm.getValue("OUT_DIAG_CODE4") + "',";
       if(parm.getData("OUT_DIAG_CODE5")!=null)//��Ժ�������
           sql += " OUT_DIAG_CODE5 = '" + parm.getValue("OUT_DIAG_CODE5") + "',";
       if(parm.getData("OUT_DIAG_CODE6")!=null)//��Ժ�������
           sql += " OUT_DIAG_CODE6 = '" + parm.getValue("OUT_DIAG_CODE6") + "',";
       if(parm.getData("INTE_DIAG_CODE")!=null)//Ժ�ڸ�Ⱦ���
           sql += " INTE_DIAG_CODE = '" + parm.getValue("INTE_DIAG_CODE") + "',";

       sql += " OPT_USER='"+parm.getValue("OPT_USER")+"',OPT_TERM='"+parm.getValue("OPT_TERM")+"',OPT_DATE=SYSDATE ";
       sql += "  WHERE CASE_NO = '"+ parm.getValue("CASE_NO") +"'";
       result.setData(this.update(sql,conn));
       if (result.getErrCode() < 0) {
           err("jdo.mro.MROInterfaceTool.updateDiag==>ERR:" + result.getErrCode() + result.getErrText() +
               result.getErrName());
           return result;
       }
       return result;
   }
   /**
    * �޸� ҩƷ����
    * @param parm TParm ������Ϣ�� CASE_NO,ALLEGIC,OPT_USER,OPT_TERMΪ�������
    * @param conn TConnection
    * @return TParm
    */
   public TParm updateALLEGIC(TParm parm,TConnection conn){
       TParm result = new TParm();
       String sql = "UPDATE MRO_RECORD SET ";
       if(parm.getData("ALLEGIC")!=null)//������¼
           sql += " ALLEGIC='"+ parm.getValue("ALLEGIC") +"',";

       sql += " OPT_USER='"+parm.getValue("OPT_USER")+"',OPT_TERM='"+parm.getValue("OPT_TERM")+"',OPT_DATE=SYSDATE ";
       sql += " WHERE CASE_NO='"+ parm.getValue("CASE_NO") +"'";
       result.setData(this.update(sql,conn));
       if (result.getErrCode() < 0) {
          err("jdo.mro.MROInterfaceTool.updateALLEGIC==>ERR:" + result.getErrCode() + result.getErrText() +
              result.getErrName());
          return result;
      }
      return result;
   }
   /**
    * ���� ������Ϣ
    * @param <any> TParm
    * @return TParm
    */
   public TParm insertOP(TParm parm,TConnection conn){
       TParm result;
       result = MRORecordTool.getInstance().insertOP(parm,conn);
       return result;
   }
   /**
    * �޸� ������Ϣ
    * @param parm TParm ������Ϣ�� CASE_NO,SEQ_NO,OPT_USER,OPT_TERMΪ�������
    * @param conn TConnection
    * @return TParm
    */
   public TParm updateOP(TParm parm,TConnection conn){
       TParm result = new TParm();
       if(parm.getData("CASE_NO")==null||parm.getData("SEQ_NO")==null){
           result.setErr(-1,"��Ҫ�������������ڣ�");
           return result;
       }
       String sql = "UPDATE MRO_RECORD_OP SET ";
       if(parm.getData("IPD_NO")!=null)//סԺ��
           sql += " IPD_NO='"+ parm.getValue("IPD_NO") +"',";
       if(parm.getData("MR_NO")!=null)//������
           sql += " MR_NO='"+ parm.getValue("MR_NO") +"',";
       if(parm.getData("OP_CODE")!=null)//��������
           sql += " OP_CODE='"+ parm.getValue("OP_CODE") +"',";
       if(parm.getData("OP_DESC")!=null)//��������
           sql += " OP_DESC='"+ parm.getValue("OP_DESC") +"',";
       if(parm.getData("OP_REMARK")!=null)//������ע
           sql += " OP_REMARK='"+ parm.getValue("OP_REMARK") +"',";
       if(parm.getData("OP_DATE")!=null)//����ʱ��
           sql += " OP_DATE=TO_DATE('"+ parm.getValue("OP_DATE") +"','YYYYMMDDHH24MISS'),";
       if(parm.getData("ANA_WAY")!=null)//����ʽ
           sql += " ANA_WAY='"+ parm.getValue("ANA_WAY") +"',";
       if(parm.getData("ANA_DR")!=null)//����ҽʦ
           sql += " ANA_DR='"+ parm.getValue("ANA_DR") +"',";
       if(parm.getData("MAIN_SUGEON")!=null)//����
           sql += " MAIN_SUGEON='"+ parm.getValue("MAIN_SUGEON") +"',";
       if(parm.getData("AST_DR1")!=null)//����һ
           sql += " AST_DR1='"+ parm.getValue("AST_DR1") +"',";
       if(parm.getData("AST_DR2")!=null)//������
           sql += " AST_DR2='"+ parm.getValue("AST_DR2") +"',";
       if(parm.getData("HEALTH_LEVEL")!=null)//�п����ϵȼ�
           sql += " HEALTH_LEVEL='"+ parm.getValue("HEALTH_LEVEL") +"',";
       if(parm.getData("OP_LEVEL")!=null)//�����ȼ�
           sql += " OP_LEVEL='"+ parm.getValue("OP_LEVEL") +"',";
       if(parm.getData("OPT_USER")!=null)
           sql += " OPT_USER='"+ parm.getValue("OPT_USER") +"',";
       if(parm.getData("OPT_TERM")!=null)
           sql += " OPT_TERM='"+ parm.getValue("OPT_TERM") +"',";
       sql += " OPT_DATE=SYSDATE "; //�޸�ʱ��
       sql += " WHERE CASE_NO = '" + parm.getValue("CASE_NO") +
           "' AND SEQ_NO = '" + parm.getValue("SEQ_NO") + "'";
       result.setData(this.update(sql, conn));
       if (result.getErrCode() < 0) {
           err("ERR:" + result.getErrCode() + result.getErrText() +
               result.getErrName());
           return result;
       }
       return result;
   }
   /**
    * �޸� ��Ѫ��Ϣ
    * @param parm TParm CASE_NO,OPT_USER,OPT_TERMΪ�������
    * @param conn TConnection
    * @return TParm
    */
   public TParm updateBlood(TParm parm,TConnection conn){
       TParm result = new TParm();
       String sql = "UPDATE MRO_RECORD SET ";
       if(parm.getData("BLOOD_TYPE")!=null)//Ѫ��
           sql += " BLOOD_TYPE='"+ parm.getValue("BLOOD_TYPE") +"',";
       if(parm.getData("RH_TYPE")!=null)//RH
           sql += " RH_TYPE='"+ parm.getValue("RH_TYPE") +"',";
       if(parm.getData("TRANS_REACTION")!=null)//��Ѫ��Ӧ
           sql += " TRANS_REACTION='"+ parm.getValue("TRANS_REACTION") +"',";
       if(parm.getData("RBC")!=null)//��Ѫ��
           sql += " RBC='"+ parm.getValue("RBC") +"',";
       if(parm.getData("PLATE")!=null)//ѪС��
           sql += " PLATE='"+ parm.getValue("PLATE") +"',";
       if(parm.getData("PLASMA")!=null)//Ѫ��
           sql += " PLASMA='"+ parm.getValue("PLASMA") +"',";
       if(parm.getData("WHOLE_BLOOD")!=null)//ȫѪ
           sql += " WHOLE_BLOOD='"+ parm.getValue("WHOLE_BLOOD") +"',";
       if(parm.getData("OTH_BLOOD")!=null)//����ѪƷ����
           sql += " OTH_BLOOD='"+ parm.getValue("OTH_BLOOD") +"',";
       sql += " OPT_USER='"+parm.getValue("OPT_USER")+"',OPT_TERM='"+parm.getValue("OPT_TERM")+"',OPT_DATE=SYSDATE ";
       sql += " WHERE CASE_NO='"+ parm.getValue("CASE_NO") +"'";
       result.setData(this.update(sql, conn));
       if (result.getErrCode() < 0) {
           err("ERR:" + result.getErrCode() + result.getErrText() +
               result.getErrName());
           return result;
       }
       return result;
   }
   /**
    * �޸�������Ϣ
    * @param parm TParm  CASE_NO,OPT_USER,OPT_TERMΪ�������  ����������ѡ==>
    * CHARGE_01 ��λ��(ס);CHARGE_02 �����(ס);CHARGE_03 ��ҩ��(ס);CHARGE_04 �г�ҩ��(ס);CHARGE_05 �в�ҩ��(ס);
    * CHARGE_06 �����(ס);CHARGE_07 �����(ס);CHARGE_08 ������(ס);
    * CHARGE_09 ��Ѫ��(ס);CHARGE_10 ���Ʒ�(ס);CHARGE_11 ������(ס);
    * CHARGE_12 ������(ס);CHARGE_13 ����(ס);CHARGE_14 �Ҵ���(ס);
    * CHARGE_15 �����(ס);CHARGE_16 Ӥ����(ס);CHARGE_17 ����;
    * CHARGE_18 �����ޣ�;CHARGE_19 �����ޣ�;CHARGE_20 �����ޣ�; ���������Ǹ��� SYS_DICTIONARY ������ GROUP_ID='MRO_CHARGE'
    * @param conn TConnection
    * @return TParm
    */
   public TParm updateCharge(TParm parm,TConnection conn){
       TParm result = new TParm();
       String sql = "UPDATE MRO_RECORD SET ";
       if(parm.getData("CHARGE_01")!=null)//��λ��(ס)
           sql += " CHARGE_01='"+ parm.getValue("CHARGE_01") +"',";
       if(parm.getData("CHARGE_02")!=null)//�����(ס)
           sql += " CHARGE_02='"+ parm.getValue("CHARGE_02") +"',";
       if(parm.getData("CHARGE_03")!=null)//��ҩ��(ס)
           sql += " CHARGE_03='"+ parm.getValue("CHARGE_03") +"',";
       if(parm.getData("CHARGE_04")!=null)//�г�ҩ��(ס)
           sql += " CHARGE_04='"+ parm.getValue("CHARGE_04") +"',";
       if(parm.getData("CHARGE_05")!=null)//�в�ҩ��(ס)
           sql += " CHARGE_05='"+ parm.getValue("CHARGE_05") +"',";
       if(parm.getData("CHARGE_06")!=null)//�����(ס)
           sql += " CHARGE_06='"+ parm.getValue("CHARGE_06") +"',";
       if(parm.getData("CHARGE_07")!=null)//�����(ס)
           sql += " CHARGE_07='"+ parm.getValue("CHARGE_07") +"',";
       if(parm.getData("CHARGE_08")!=null)//������(ס)
           sql += " CHARGE_08='"+ parm.getValue("CHARGE_08") +"',";
       if(parm.getData("CHARGE_09")!=null)//��Ѫ��(ס)
           sql += " CHARGE_09='"+ parm.getValue("CHARGE_09") +"',";
       if(parm.getData("CHARGE_10")!=null)//���Ʒ�(ס)
           sql += " CHARGE_10='"+ parm.getValue("CHARGE_10") +"',";
       if(parm.getData("CHARGE_11")!=null)//������(ס)
           sql += " CHARGE_11='"+ parm.getValue("CHARGE_11") +"',";
       if(parm.getData("CHARGE_12")!=null)//������(ס)
           sql += " CHARGE_12='"+ parm.getValue("CHARGE_12") +"',";
       if(parm.getData("CHARGE_13")!=null)//����(ס)
           sql += " CHARGE_13='"+ parm.getValue("CHARGE_13") +"',";
       if(parm.getData("CHARGE_14")!=null)//�Ҵ���(ס)
           sql += " CHARGE_14='"+ parm.getValue("CHARGE_14") +"',";
       if(parm.getData("CHARGE_15")!=null)//�����(ס)
           sql += " CHARGE_15='"+ parm.getValue("CHARGE_15") +"',";
       if(parm.getData("CHARGE_16")!=null)//Ӥ����(ס)
           sql += " CHARGE_16='"+ parm.getValue("CHARGE_16") +"',";
       if(parm.getData("CHARGE_17")!=null)//����
           sql += " CHARGE_17='"+ parm.getValue("CHARGE_17") +"',";
       if(parm.getData("CHARGE_18")!=null)
           sql += " CHARGE_18='"+ parm.getValue("CHARGE_18") +"',";
       if(parm.getData("CHARGE_19")!=null)
           sql += " CHARGE_19='"+ parm.getValue("CHARGE_19") +"',";
       if(parm.getData("CHARGE_20")!=null)
           sql += " CHARGE_20='"+ parm.getValue("CHARGE_20") +"',";

       sql += " OPT_USER='"+parm.getValue("OPT_USER")+"',OPT_TERM='"+parm.getValue("OPT_TERM")+"',OPT_DATE=SYSDATE ";
       sql += " WHERE CASE_NO='"+ parm.getValue("CASE_NO") +"'";

       result.setData(this.update(sql, conn));
       if (result.getErrCode() < 0) {
           err("ERR:" + result.getErrCode() + result.getErrText() +
               result.getErrName());
           return result;
       }
       return result;
   }
}
