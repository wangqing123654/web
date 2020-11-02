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
public class InsSxKa003List extends TModifiedList {
    /**
     * ������
     */
    public InsSxKa003List() {
        StringBuffer sb = new StringBuffer();
        //ҩƷ����
        sb.append("nhiOrderCode:NHI_ORDER_CODE;");
        // ����޼�
        sb.append("limitPrice:LIMIT_PRICE;");
        //ҽԺ�ȼ�
        sb.append("hospClass:HOSP_CLASS;");
        //��Ŀ����
        sb.append("nhiOrderDesc:NHI_ORDER_DESC;");
        //�շ����
        sb.append("changeCode:CHARGE_CODE;");
        //�շ���Ŀ�ȼ�
        sb.append("aka065:AKA065;");
        //һ���������
        sb.append("orderCat1:ORDER_CAT1;");
        //�����������
        sb.append("orderCat2:ORDER_CAT2;");
        //�����������
        sb.append("orderCat3:ORDER_CAT3;");
        //����Ŀ¼ʹ�÷�Χ
        sb.append("description:DESCRIPTION;");
        //ע����
        sb.append("memo:MEMO;");
        //��׼�۸�
        sb.append("price:PRICE;");
        //�Ը�����
        sb.append("ownRate:OWN_RATE;");
        //����֧����׼
        sb.append("nhiBasePrice:NHI_BASE_PRICE;");
        //������
        sb.append("approveUserId:APPROVE_USER_ID;");
        //��������
        sb.append("approveDate:APPROVE_DATE;");
        //���ݷ������
        sb.append("dataAreaNo:DATAAREA_NO;");
        //��ǰ��Ч��־
        sb.append("activeFlg:ACTIVE_FLG;");
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
    public InsSxKa003 newInsSxKa003() {
        InsSxKa003 inssxka003 = new InsSxKa003();
        this.newData(inssxka003);
        return inssxka003;
    }

    /**
     * ��ʼ��TPARM
     * @param parm
     * @return �棺�ɹ����٣�ʧ��
     */
    public boolean initParm(TParm parm) {
        //System.out.println(" 003 list");
        //System.out.println("list parm = "+parm);
        if (parm == null)
            return false;
        int count = parm.getCount();
        for (int i = 0; i < count; i++) {
            InsSxKa003 inssxka003 = new InsSxKa003();
            add(inssxka003);
            if (!inssxka003.initParm(parm, i))
                return false;
        }
        return true;
    }

}
