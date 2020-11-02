package jdo.ins;

import com.dongyang.data.TModifiedList;
import com.dongyang.data.TParm;

public class InsSxKa002List extends TModifiedList {
    /**
     * ������
     */
    public InsSxKa002List() {
        StringBuffer sb = new StringBuffer();
        //ҩƷ����
        sb.append("NhiOrderCode:NHI_ORDER_CODE;");
        // ��������
        sb.append("NhiOrderDesc:NHI_ORDER_DESC;");
        //Ӣ��
        sb.append("NhiOrderEngDesc:NHI_ORDER_ENG_DESC;");
        //�շ����
        sb.append("chargeCode:CHARGE_CODE;");
        //һ���������
        sb.append("orderCode1:ORDER_CAT1;");
        //�����������
        sb.append("orderCode2:ORDER_CAT2;");
        //�����������
        sb.append("orderCode3:ORDER_CAT3;");
        //����ҩ��־
        sb.append("preFlg:PRE_FLG;");
        //�շ���Ŀ�ȼ�
        sb.append("orderType:ORDER_TYPE;");
        //ע����
        sb.append("memoCode:MEMO_CODE;");
        //��λ
        sb.append("unit:UNIT;");
        //��׼�۸�
        sb.append("price:PRICE;");
        //�Ը�����
        sb.append("ownRate:OWN_RATE;");
        //����
        sb.append("doseCode:DOSE_CODE;");
        //ÿ������
        sb.append("qty:QTY;");
        //ʹ��Ƶ��
        sb.append("freqCode:FREQ_CODE;");
        //�÷�
        sb.append("routeCode:ROUTE_CODE;");
        //���
        sb.append("description:DESCRIPTION;");
        //��������ҩ��־
        sb.append("LimitFlg:LIMIT_FLG;");
        //��ע
        sb.append("remark:REMARK;");
        //������
        sb.append("aproveUserId:APROVE_USER_ID;");
        //��������
        sb.append("aproveDate:APPROVE_DATE;");
        //���ݷ������
        sb.append("DataAreaNo:DATAAREA_NO;");
        //��ǰ��Ч��־
        sb.append("activeFlg:ACTIVE_FLG;");
        //�����Ը�����
        sb.append("bka002:BKA002;");
        //�����շ����
        sb.append("bka001:BKA001;");
        //ҽԺҽ������
        sb.append("hospOrderCode:HOSP_ORDER_CODE;");
        //�շ����ȼ�
        sb.append("feeType:FEE_TYPE;");
        //ͳ�Ʒ���
        sb.append("staCode:STA_CODE");
        setMapString(sb.toString());
    }

    /**
     * ����
     * @return InsSxKa002
     */
    public InsSxKa002 newInsSxKa002() {
        InsSxKa002 inssxka002 = new InsSxKa002();
        this.newData(inssxka002);
        return inssxka002;
    }

    /**
     * ��ʼ��TPARM
     * @param parm
     * @return �棺�ɹ����٣�ʧ��
     */
    public boolean initParm(TParm parm) {
        //System.out.println("002 list");
       // System.out.println("parm = "+parm);
        if (parm == null)
            return false;
        int count = parm.getCount();
        //System.out.println("count = "+count);
        for (int i = 0; i < count; i++) {
            InsSxKa002 inssxka002 = new InsSxKa002();
            add(inssxka002);
            if (!inssxka002.initParm(parm, i))
                return false;
        }
        return true;
    }
}
