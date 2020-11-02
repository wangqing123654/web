package jdo.ins;

import com.dongyang.data.TModifiedList;
import com.dongyang.data.TParm;

/**
 * <p>Title: ҽ������</p>
 *
 * <p>Description: ��Ƕʽҽ������</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author JiaoY
 * @version JavaHis 1.0
 */
public class InsSxKa004List extends TModifiedList {
    /**
    * ������
    */
   public InsSxKa004List() {
       StringBuffer sb = new StringBuffer();
       //ҽ�Ʒ�����ʩ����
       sb.append("aka100:AKA100;");
       // ����޼�
       sb.append("limitPrice:LIMIT_PRICE;");
       //ҽԺ�ȼ�
       sb.append("aka101:AKA101;");
       //������ʩ����
       sb.append("aka102:AKA102;");
       //�շ����
       sb.append("aka063:AKA063;");
       //�շ���Ŀ�ȼ�
       sb.append("aka065:AKA065;");
       //һ���������
       sb.append("bka246:BKA246;");
       //�����������
       sb.append("bka247:BKA247;");
       //�����������
       sb.append("bka260:BKA260;");
       //����Ŀ¼ʹ�÷�Χ
       sb.append("bka001:BKA001;");
       //�����ȼ�
       sb.append("bka103:AKA103;");
       //ע����
       sb.append("aka066:AKA066;");
       //��׼�۸�
       sb.append("aka068:AKA068;");
       //�Ը�����
       sb.append("aka069:AKA069;");
       //����֧����׼
       sb.append("aka104:AKA104;");
       //������
       sb.append("aae011:AAE011;");
       //��������
       sb.append("aae036:AAE036;");
       //���ݷ������
       sb.append("baa001:BAA001;");
       //��ǰ��Ч��־
       sb.append("aae100:AAE100;");
       //ҽԺҽ������
       sb.append("hospOrderCode:HOSP_ORDER_CODE;");
       //�շ����ȼ�
       sb.append("feeType:FEE_TYPE;");
       setMapString(sb.toString());
   }

   /**
    * ����
    * @return InsSxKa002
    */
   public InsSxKa004 newInsSxKa004() {
       InsSxKa004 inssxka004 = new InsSxKa004();
       this.newData(inssxka004);
       return inssxka004;
   }

   /**
    * ��ʼ��TPARM
    * @param parm
    * @return �棺�ɹ����٣�ʧ��
    */
   public boolean initParm(TParm parm) {
      // System.out.println(" 004 list");
      // System.out.println("list parm = "+parm);
       if (parm == null)
           return false;
       int count = parm.getCount();
       for (int i = 0; i < count; i++) {
           InsSxKa004 inssxka004 = new InsSxKa004();
           add(inssxka004);
           if (!inssxka004.initParm(parm, i))
               return false;
       }
       return true;
   }

}
