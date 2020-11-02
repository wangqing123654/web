package jdo.ins;

import com.dongyang.data.TModifiedData;
import com.dongyang.data.StringValue;
import com.dongyang.data.DoubleValue;
import com.dongyang.data.BooleanValue;
import com.dongyang.data.TimestampValue;
import java.sql.Timestamp;


public class InsSxKa002 extends TModifiedData {
    /**
     * ҩƷ���� NHI_ORDER_CODE
     */
    private StringValue NhiOrderCode = new StringValue(this);
    /**
     * ��������
     */
    private StringValue NhiOrderDesc = new StringValue(this);
    /**
     * Ӣ��
     */
    private StringValue NhiOrderEngDesc = new StringValue(this);
    /**
     * �շ����
     */
    private StringValue chargeCode = new StringValue(this);
    /**
     * һ���������
     */
    private StringValue orderCode1 = new StringValue(this);
    /**
     * �����������
     */
    private StringValue orderCode2 = new StringValue(this);
    /**
     * �����������
     */
    private StringValue orderCode3 = new StringValue(this);
    /**
     * ����ҩ��־
     */
    private StringValue preFlg = new StringValue(this);
    /**
     * �շ���Ŀ�ȼ�
     */
    private StringValue orderType = new StringValue(this);
    /**
     * ע����
     */
    private StringValue memoCode = new StringValue(this);
    /**
     * ��λ
     */
    private StringValue unit = new StringValue(this);
    /**
     * ��׼�۸�
     */
    private DoubleValue price = new DoubleValue(this);
    /**
     * �Ը�����
     */
    private DoubleValue ownRate = new DoubleValue(this);
    /**
     * ����
     */
    private StringValue doseCode = new StringValue(this);
    /**
     * ÿ������
     */
    private DoubleValue qty = new DoubleValue(this);
    /**
     * ʹ��Ƶ��
     */
    private StringValue freqCode = new StringValue(this);
    /**
     * �÷�
     */
    private StringValue routeCode = new StringValue(this);
    /**
     * ���
     */
    private StringValue description = new StringValue(this);
    /**
     * ��������ҩ��־
     */
    private BooleanValue LimitFlg = new BooleanValue(this);
    /**
     * ��ע
     */
    private StringValue remark = new StringValue(this);
    /**
     * ������
     */
    private StringValue aproveUserId = new StringValue(this);
    /**
     * ��������
     */
    private TimestampValue aproveDate = new TimestampValue(this);
    /**
     * ���ݷ������
     */
    private StringValue DataAreaNo = new StringValue(this);
    /**
     * ��ǰ��Ч��־
     */
    private BooleanValue activeFlg = new BooleanValue(this);
    /**
     * �����Ը�����
     */
    private DoubleValue bka002 = new DoubleValue(this);
    /**
     * �����շ����
     */
    private StringValue bka001 = new StringValue(this);
    /**
     * ҽԺҽ������
     */
    private StringValue hospOrderCode = new StringValue(this);
    /**
     * �շ����ȼ�
     */
    private StringValue feeType = new StringValue(this);
    /**
     * ͳ�Ʒ���
     * �����ؼ졢����ͳ�Ʊ�ķ���ʹ��
     */
    private StringValue staCode = new StringValue(this);


    /**
     * ����ҩƷ����
     * @param NhiOrderCode String ҩƷ����
     */
    public void setNhiOrderCode(String NhiOrderCode) {
        this.NhiOrderCode.setValue(NhiOrderCode);
    }

    /**
     * �õ�ҩƷ����
     * @return String ҩƷ����
     */
    public String getNhiOrderCode() {
        return NhiOrderCode.getValue();
    }

    /**
     * �޸�ҩƷ����
     * @param NhiOrderDesc String
     */
    public void modifyNhiOrderCode(String NhiOrderCode) {
        this.NhiOrderCode.modifyValue(NhiOrderCode);
    }



    /**
     * ����ҩƷ��������
     * @param NhiOrderDesc String
     */
    public void setNhiOrderDesc(String NhiOrderDesc) {
        this.NhiOrderDesc.setValue(NhiOrderDesc);
    }

    /**
     * �õ���������
     * @return String
     */
    public String getNhiOrderDesc() {
        return NhiOrderDesc.getValue();
    }

    /**
     * �޸���������
     * @param NhiOrderDesc String
     */
    public void modifyNhiOrderDesc(String NhiOrderDesc) {
        this.NhiOrderDesc.modifyValue(NhiOrderDesc);
    }

    /**
     * ����ҩƷӢ������
     * @param NhiOrderEngDesc String
     */
    public void setNhiOrderEngDesc(String NhiOrderEngDesc) {
        this.NhiOrderEngDesc.setValue(NhiOrderEngDesc);
    }

    /**
     * �õ�Ӣ������
     * @return String
     */
    public String getNhiOrderEngDesc() {
        return NhiOrderEngDesc.getValue();
    }

    /**
     * �޸�Ӣ������
     * @param NhiOrderEngDesc String
     */
    public void modifyNhiOrderEngDesc(String NhiOrderEngDesc) {
        this.NhiOrderEngDesc.modifyValue(NhiOrderEngDesc);
    }

    /**
     * �����շ����
     * @param chargeCode String
     */
    public void setchargeCode(String chargeCode) {
        this.chargeCode.setValue(chargeCode);
    }

    /**
     * �õ��շ����
     * @return String
     */
    public String getchargeCode() {
        return chargeCode.getValue();
    }

    /**
     * �޸��շ����
     * @param chargeCode String
     */
    public void modifychargeCode(String chargeCode) {
        this.chargeCode.modifyValue(chargeCode);
    }

    /**
     * ����һ���������
     * @param orderCode1 String
     */
    public void setorderCode1(String orderCode1) {
        this.orderCode1.setValue(orderCode1);
    }

    /**
     * �õ�һ���������
     * @return String
     */
    public String getorderCode1() {
        return orderCode1.getValue();
    }

    /**
     * �޸�һ���������
     * @param orderCode1 String
     */
    public void modifyorderCode1(String orderCode1) {
        this.orderCode1.modifyValue(orderCode1);
    }

    /**
     * ���ö����������
     * @param orderCode2 String
     */
    public void setorderCode2(String orderCode2) {
        this.orderCode2.setValue(orderCode2);
    }

    /**
     * �õ������������
     * @return String
     */
    public String getorderCode2() {
        return orderCode2.getValue();
    }

    /**
     * �޸Ķ����������
     * @param orderCode2 String
     */
    public void modifyorderCode2(String orderCode2) {
        this.orderCode2.modifyValue(orderCode2);
    }

    /**
     * ���������������
     * @param orderCode3 String
     */
    public void setorderCode3(String orderCode3) {
        this.orderCode3.setValue(orderCode3);
    }

    /**
     * �õ������������
     * @return String
     */
    public String getorderCode3() {
        return orderCode3.getValue();
    }

    /**
     * �޸������������
     * @param orderCode3 String
     */
    public void modifyorderCode3(String orderCode3) {
        this.orderCode3.modifyValue(orderCode3);
    }

    /**
     * ���ô���ҩ��־
     * @param preFlg String
     */
    public void setpreFlg(String preFlg) {
        this.preFlg.setValue(preFlg);
    }

    /**
     * �õ�����ҩ��־
     * @return String
     */
    public String getpreFlg() {
        return preFlg.getValue();
    }

    /**
     * �޸Ĵ���ҩ��־
     * @param preFlg String
     */
    public void modifypreFlg(String preFlg) {
        this.preFlg.modifyValue(preFlg);
    }

    /**
     * �����շ���Ŀ�ȼ�
     * @param orderType String
     */
    public void setorderType(String orderType) {
        this.orderType.setValue(orderType);
    }

    /**
     * �õ��շ���Ŀ�ȼ�
     * @return String
     */
    public String getorderType() {
        return orderType.getValue();
    }

    /**
     * �޸��շ���Ŀ�ȼ�
     * @param orderType String
     */
    public void modifyorderType(String orderType) {
        this.orderType.modifyValue(orderType);
    }

    /**
     * ����ע����
     * @param memoCode String
     */
    public void setmemoCode(String memoCode) {
        this.memoCode.setValue(memoCode);
    }

    /**
     * �õ�ע����
     * @return String
     */
    public String getmemoCode() {
        return memoCode.getValue();
    }

    /**
     * �޸�ע����
     * @param memoCode String
     */
    public void modifymemoCode(String memoCode) {
        this.memoCode.modifyValue(memoCode);
    }

    /**
     * ���õ�λ
     * @param unit String
     */
    public void setunit(String unit) {
        this.unit.setValue(unit);
    }

    /**
     * �õ���λ
     * @return String
     */
    public String getunit() {
        return unit.getValue();
    }

    /**
     * �޸ĵ�λ
     * @param unit String
     */
    public void modifyunit(String unit) {
        this.unit.modifyValue(unit);
    }

    /**
     * ���ñ�׼�۸�
     * @param price double
     */
    public void setprice(double price) {
        this.price.setValue(price);
    }

    /**
     * �õ���׼�۸�
     * @return double
     */
    public double getprice() {
        return price.getValue();
    }

    /**
     * �޸ı�׼�۸�
     * @param price double
     */
    public void modifyprice(double price) {
        this.price.modifyValue(price);
    }

    /**
     * �����Ը�����
     * @param unit double
     */
    public void setownRatet(double ownRate) {
        this.ownRate.setValue(ownRate);
    }

    /**
     * �õ��Ը�����
     * @return String
     */
    public double getownRate() {
        return ownRate.getValue();
    }

    /**
     * �޸��Ը�����
     * @param unit double
     */
    public void modifyownRate(double ownRate) {
        this.ownRate.modifyValue(ownRate);
    }

    /**
     * ���ü���
     * @param doseCode String
     */
    public void setdoseCode(String doseCode) {
        this.doseCode.setValue(doseCode);
    }

    /**
     * �õ�����
     * @return String
     */
    public String getdoseCode() {
        return doseCode.getValue();
    }

    /**
     * �޸ļ���
     * @param doseCode String
     */
    public void modifydoseCode(String doseCode) {
        this.doseCode.modifyValue(doseCode);
    }

    /**
     * ����ÿ������
     * @param qty double
     */
    public void setqty(double qty) {
        this.qty.setValue(qty);
    }

    /**
     * �õ�ÿ������
     * @return double
     */
    public double getqty() {
        return qty.getValue();
    }

    /**
     * �޸�ÿ������
     * @param qty double
     */
    public void modifyqty(double qty) {
        this.qty.modifyValue(qty);
    }

    /**
     * ����ʹ��Ƶ��
     * @param freqCode String
     */
    public void setfreqCode(String freqCode) {
        this.freqCode.setValue(freqCode);
    }

    /**
     * �õ�ʹ��Ƶ��
     * @return String
     */
    public String getfreqCode() {
        return freqCode.getValue();
    }

    /**
     * �޸�ʹ��Ƶ��
     * @param freqCode String
     */
    public void modifyfreqCode(String freqCode) {
        this.freqCode.modifyValue(freqCode);
    }

    /**
     * �����÷�
     * @param routeCode String
     */
    public void setrouteCode(String routeCode) {
        this.routeCode.setValue(routeCode);
    }

    /**
     * �õ��÷�
     * @return String
     */
    public String getrouteCode() {
        return routeCode.getValue();
    }

    /**
     * �޸��÷�
     * @param routeCode String
     */
    public void modifyrouteCode(String routeCode) {
        this.routeCode.modifyValue(routeCode);
    }

    /**
     * ���ù��
     * @param description String
     */
    public void setdescription(String description) {
        this.description.setValue(description);
    }

    /**
     * �õ����
     * @return String
     */
    public String getdescription() {
        return description.getValue();
    }

    /**
     * �޸Ĺ��
     * @param description String
     */
    public void modifydescription(String description) {
        this.description.modifyValue(description);
    }

    /**
     * ������������ҩ��־
     * @param LimitFlg boolean
     */
    public void setLimitFlg(boolean LimitFlg) {
        this.LimitFlg.setValue(LimitFlg);
    }

    /**
     * �õ���������ҩ��־
     * @return boolean
     */
    public boolean getLimitFlgn() {
        return LimitFlg.getValue();
    }

    /**
     * �޸���������ҩ��־
     * @param LimitFlg boolean
     */
    public void modifyLimitFlg(boolean LimitFlg) {
        this.LimitFlg.modifyValue(LimitFlg);
    }

    /**
     * ���ñ�ע
     * @param remark String
     */
    public void setremark(String remark) {
        this.remark.setValue(remark);
    }

    /**
     * �õ���ע
     * @return String
     */
    public String getremark() {
        return remark.getValue();
    }

    /**
     * �޸ı�ע
     * @param description String
     */
    public void modifyremark(String remark) {
        this.remark.modifyValue(remark);
    }

    /**
     * ���þ�����
     * @param aproveUserId String
     */
    public void setaproveUserId(String aproveUserId) {
        this.aproveUserId.setValue(aproveUserId);
    }

    /**
     * �õ�������
     * @return String
     */
    public String getaproveUserId() {
        return aproveUserId.getValue();
    }

    /**
     * �޸ľ�����
     * @param aproveUserId String
     */
    public void modifyaproveUserId(String aproveUserId) {
        this.aproveUserId.modifyValue(aproveUserId);
    }

    /**
     * ���þ�������
     * @param aproveDate Timestamp
     */
    public void setaproveDate(Timestamp aproveDate) {
        this.aproveDate.setValue(aproveDate);
    }

    /**
     * �õ���������
     * @return Timestamp
     */
    public Timestamp getaproveDate() {
        return aproveDate.getValue();
    }

    /**
     * �޸ľ�������
     * @param aproveDate Timestamp
     */
    public void modifyaproveDate(Timestamp aproveDate) {
        this.aproveDate.modifyValue(aproveDate);
    }

    /**
     * �������ݷ������
     * @param DataAreaNo String
     */
    public void setDataAreaNo(String DataAreaNo) {
        this.DataAreaNo.setValue(DataAreaNo);
    }

    /**
     * �õ����ݷ������
     * @return String
     */
    public String getDataAreaNo() {
        return DataAreaNo.getValue();
    }

    /**
     * �޸����ݷ������
     * @param DataAreaNo String
     */
    public void modifyDataAreaNo(String DataAreaNo) {
        this.DataAreaNo.modifyValue(DataAreaNo);
    }
    /**
     * ���õ�ǰ��Ч��־
     * @param activeFlg boolean
     */
    public void setactiveFlg(boolean activeFlg) {
        this.activeFlg.setValue(activeFlg);
    }

    /**
     * �õ���ǰ��Ч��־
     * @return String
     */
    public boolean getactiveFlg() {
        return activeFlg.getValue();
    }

    /**
     * �޸ĵ�ǰ��Ч��־
     * @param activeFlg boolean
     */
    public void modifyactiveFlg(boolean activeFlg) {
        this.activeFlg.modifyValue(activeFlg);
    }

    /**
     * ���ù����Ը�����
     * @param DataAreaNo double
     */
    public void setbka002(double DataAreaNo) {
        this.bka002.setValue(DataAreaNo);
    }

    /**
     * �õ������Ը�����
     * @return double
     */
    public double getbka002() {
        return bka002.getValue();
    }

    /**
     * �޸Ĺ����Ը�����
     * @param DataAreaNo double
     */
    public void modifybka002(double bka002) {
        this.bka002.modifyValue(bka002);
    }

    /**
     * ���ù����շ����
     * @param bka001 String
     */
    public void setbka001(String bka001) {
        this.bka001.setValue(bka001);
    }

    /**
     * �õ������շ����
     * @return String
     */
    public String getbka001() {
        return bka001.getValue();
    }

    /**
     * �޸Ĺ����շ����
     * @param bka001 String
     */
    public void modifybka001(String bka001) {
        this.bka001.modifyValue(bka001);
    }

    /**
     * ����ҽԺҽ������
     * @param hospOrderCode String
     */
    public void sethospOrderCode(String hospOrderCode) {
        this.hospOrderCode.setValue(hospOrderCode);
    }

    /**
     * �õ�ҽԺҽ������
     * @return String
     */
    public String gethospOrderCode() {
        return hospOrderCode.getValue();
    }

    /**
     * �޸�ҽԺҽ������
     * @param hospOrderCode String
     */
    public void modifyhospOrderCode(String hospOrderCode) {
        this.hospOrderCode.modifyValue(hospOrderCode);
    }

    /**
     * �����շ����ȼ�
     * @param feeType String
     */
    public void setfeeType(String feeType) {
        this.feeType.setValue(feeType);
    }

    /**
     * �õ��շ����ȼ�
     * @return String
     */
    public String getfeeType() {
        return feeType.getValue();
    }

    /**
     * �޸��շ����ȼ�
     * @param feeType String
     */
    public void modifyfeeType(String feeType) {
        this.feeType.modifyValue(feeType);
    }

    /**
     * ����ͳ�Ʒ���
     * @param staCode String
     */
    public void setstaCode(String staCode) {
        this.staCode.setValue(staCode);
    }

    /**
     * �õ�ͳ�Ʒ���
     * @return String
     */
    public String getstaCode() {
        return staCode.getValue();
    }

    /**
     * �޸�ͳ�Ʒ���
     * @param staCode String
     */
    public void modifystaCode(String staCode) {
        this.staCode.modifyValue(staCode);
    }









}
