package jdo.ins;

import com.dongyang.data.*;
import java.sql.Timestamp;

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
public class InsSxKa003 extends TModifiedData {
    /**
     * ҩƷ���� NHI_ORDER_CODE
     */
    private StringValue nhiOrderCode = new StringValue(this);
    /**
     * ����޼�
     */
    private DoubleValue limitPrice = new DoubleValue(this);
    /**
     * ҽԺ�ȼ�
     */
    private StringValue hospClass = new StringValue(this);
    /**
     * ��Ŀ����
     */
    private StringValue nhiOrderDesc = new StringValue(this);
    /**
     * �շ����
     */
    private StringValue changeCode = new StringValue(this);
    /**
     * �շ���Ŀ�ȼ�
     */
    private StringValue aka065 = new StringValue(this);
    /**
     * һ���������
     */
    private StringValue orderCat1 = new StringValue(this);
    /**
     * �����������
     */
    private StringValue orderCat2 = new StringValue(this);
    /**
     * �����������
     */
    private StringValue orderCat3 = new StringValue(this);
    /**
     * ����Ŀ¼ʹ�÷�Χ
     */
    private StringValue description = new StringValue(this);
    /**
     * ע����
     */
    private StringValue memo = new StringValue(this);
    /**
     * ��׼�۸�
     */
    private DoubleValue price = new DoubleValue(this);
    /**
     * �Ը�����
     */
    private DoubleValue ownRate = new DoubleValue(this);
    /**
     * ����֧����׼
     */
    private DoubleValue nhiBasePrice = new DoubleValue(this);
    /**
     * ������
     */
    private StringValue approveUserId = new StringValue(this);
    /**
     * ��������
     */
    private TimestampValue approveDate = new TimestampValue(this);
    /**
     * ���ݷ������
     */
    private StringValue dataAreaNo = new StringValue(this);
    /**
     * ��ǰ��Ч��־
     */
    private BooleanValue activeFlg = new BooleanValue(this);
    /**
     * ҽԺҽ������
     */
    private StringValue hospOrderCode = new StringValue(this);
    /**
     * �շ����ȼ�
     */
    private StringValue feeType = new StringValue(this);

    /**
     * ����ҩƷ����
     * @param NhiOrderCode String ҩƷ����
     */
    public void setNhiOrderCode(String NhiOrderCode) {
        this.nhiOrderCode.setValue(NhiOrderCode);
    }

    /**
     * �õ�ҩƷ����
     * @return String ҩƷ����
     */
    public String getNhiOrderCode() {
        return nhiOrderCode.getValue();
    }

    /**
     * ��������޼�
     * @param chargeCode String
     */
    public void setlimitPrice(double limitPrice) {
        this.limitPrice.setValue(limitPrice);
    }
    /**
     * �õ�����޼�
     * @return double
     */
    public double getlimitPrice(){
        return limitPrice.getValue();
    }
    /**
    * �޸�����޼�
    * @param chargeCode String
    */

    public void modifylimitPrice(double limitPrice) {
       this.limitPrice.setValue(limitPrice);
   }
    /**
     * ����ҽԺ�ȼ�
     * @param orderCode1 String
     */
    public void sethospClass(String hospClass) {
        this.hospClass.setValue(hospClass);
    }

    /**
     * �ȵ�ҽԺ�ȼ�
     * @return String
     */
    public String gethospClass() {
        return hospClass.getValue();
    }

    /**
     * �޸�ҽԺ�ȼ�
     * @param hospClass String
     */
    public void modifyhospClass(String hospClass) {
        this.hospClass.modifyValue(hospClass);
    }

    /**
     * ������Ŀ����
     * @param orderCode1 String
     */
    public void setnhiOrderDesc(String nhiOrderDesc) {
        this.nhiOrderDesc.setValue(nhiOrderDesc);
    }

    /**
     * �ȵ���Ŀ����
     * @return String
     */
    public String getnhiOrderDesc() {
        return nhiOrderDesc.getValue();
    }

    /**
     * �޸���Ŀ����
     * @param hospClass String
     */
    public void modifynhiOrderDesc(String nhiOrderDesc) {
        this.nhiOrderDesc.modifyValue(nhiOrderDesc);
    }

    /**
     * �����շ����
     * @param orderCode1 String
     */
    public void setchangeCode(String changeCode) {
        this.changeCode.setValue(changeCode);
    }

    /**
     * �ȵ��շ����
     * @return String
     */
    public String getchangeCode() {
        return changeCode.getValue();
    }

    /**
     * �޸��շ����
     * @param hospClass String
     */
    public void modifychangeCode(String changeCode) {
        this.changeCode.modifyValue(changeCode);
    }

    /**
     * �����շ���Ŀ�ȼ�
     * @param orderCode1 String
     */
    public void setaka065(String aka065) {
        this.aka065.setValue(aka065);
    }

    /**
     * �ȵ��շ���Ŀ�ȼ�
     * @return String
     */
    public String getaka065() {
        return aka065.getValue();
    }

    /**
     * �޸��շ���Ŀ�ȼ�
     * @param hospClass String
     */
    public void modifyaka065(String aka065) {
        this.aka065.modifyValue(aka065);
    }

    /**
     * ����һ���������
     * @param orderCode1 String
     */
    public void setorderCat1(String orderCat1) {
        this.orderCat1.setValue(orderCat1);
    }

    /**
     * �ȵ�һ���������
     * @return String
     */
    public String getorderCat1() {
        return orderCat1.getValue();
    }

    /**
     * �޸�һ���������
     * @param hospClass String
     */
    public void modifyorderCat1(String orderCat1) {
        this.orderCat1.modifyValue(orderCat1);
    }

    /**
     * ���ö����������
     * @param orderCode1 String
     */
    public void setorderCat2(String orderCat2) {
        this.orderCat2.setValue(orderCat2);
    }

    /**
     * �ȵ������������
     * @return String
     */
    public String getorderCat2() {
        return orderCat2.getValue();
    }

    /**
     * �޸Ķ����������
     * @param hospClass String
     */
    public void modifyorderCat2(String orderCat2) {
        this.orderCat2.modifyValue(orderCat2);
    }

    /**
     * ���������������
     * @param orderCode3 String
     */
    public void setorderCat3(String orderCat3) {
        this.orderCat3.setValue(orderCat3);
    }

    /**
     * �ȵ������������
     * @return String
     */
    public String getorderCat3() {
        return orderCat3.getValue();
    }

    /**
     * �޸������������
     * @param hospClass String
     */
    public void modifyorderCat3(String orderCat3) {
        this.orderCat3.modifyValue(orderCat3);
    }


    /**
     * ��������Ŀ¼ʹ�÷�Χ
     * @param orderCode1 String
     */
    public void setdescription(String description) {
        this.description.setValue(description);
    }

    /**
     * �ȵ�����Ŀ¼ʹ�÷�Χ
     * @return String
     */
    public String getdescription() {
        return description.getValue();
    }

    /**
     * �޸�����Ŀ¼ʹ�÷�Χ
     * @param hospClass String
     */
    public void modifydescription(String description) {
        this.description.modifyValue(description);
    }

    /**
     * ����ע����
     * @param orderCode1 String
     */
    public void setmemo(String memo) {
        this.memo.setValue(memo);
    }

    /**
     * �õ�ע����
     * @return String
     */
    public String getmemo() {
        return memo.getValue();
    }

    /**
     * �޸�ע����
     * @param hospClass String
     */
    public void modifymemo(String memo) {
        this.memo.modifyValue(memo);
    }

    /**
     * ��׼�۸�
     * @param orderCode1 String
     */
    public void setprice(double price) {
        this.price.setValue(price);
    }

    /**
     * �õ���׼�۸�
     * @return String
     */
    public double getprice() {
        return price.getValue();
    }

    /**
     * �޸ı�׼�۸�
     * @param hospClass String
     */
    public void modifymemo(double price) {
        this.price.modifyValue(price);
    }

    /**
     * �����Ը�����
     * @param orderCode1 String
     */
    public void setownRate(double ownRate) {
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
     * @param hospClass String
     */
    public void modifyownRate(double ownRate) {
        this.ownRate.modifyValue(ownRate);
    }

    /**
     * ���û���֧����׼
     * @param orderCode1 String
     */
    public void setnhiBasePrice(double nhiBasePrice) {
        this.nhiBasePrice.setValue(nhiBasePrice);
    }

    /**
     * �õ�����֧����׼
     * @return String
     */
    public double getnhiBasePrice() {
        return nhiBasePrice.getValue();
    }

    /**
     * �޸Ļ���֧����׼
     * @param hospClass String
     */
    public void modifynhiBasePrice(double nhiBasePrice) {
        this.nhiBasePrice.modifyValue(nhiBasePrice);
    }

    /**
     * ���þ�����
     * @param orderCode1 String
     */
    public void setapproveUserId(String approveUserId) {
        this.approveUserId.setValue(approveUserId);
    }

    /**
     * �õ�������
     * @return String
     */
    public String getapproveUserId() {
        return approveUserId.getValue();
    }

    /**
     * �޸ľ�����
     * @param hospClass String
     */
    public void modifyapproveUserId(String approveUserId) {
        this.approveUserId.modifyValue(approveUserId);
    }

    /**
     * ���þ�������
     * @param orderCode1 String
     */
    public void setapproveDate(Timestamp approveDate) {
        this.approveDate.setValue(approveDate);
    }

    /**
     * �õ���������
     * @return String
     */
    public Timestamp getapproveDate() {
        return approveDate.getValue();
    }

    /**
     * �޸ľ�������
     * @param hospClass String
     */
    public void modifyapproveDate(Timestamp approveDate) {
        this.approveDate.modifyValue(approveDate);
    }

    /**
     * �������ݷ������
     * @param orderCode1 String
     */
    public void setdataAreaNo(String dataAreaNo) {
        this.dataAreaNo.setValue(dataAreaNo);
    }

    /**
     * �õ����ݷ������
     * @return String
     */
    public String getdataAreaNo() {
        return dataAreaNo.getValue();
    }

    /**
     * �޸����ݷ������
     * @param hospClass String
     */
    public void modifydataAreaNo(String dataAreaNo) {
        this.dataAreaNo.modifyValue(dataAreaNo);
    }

    /**
     * ���õ�ǰ��Ч��־
     * @param orderCode1 String
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
     * @param hospClass String
     */
    public void modifyactiveFlg(boolean activeFlg) {

            this.activeFlg.modifyValue(activeFlg);

    }

    /**
     * ����ҽԺҽ������
     * @param orderCode1 String
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
     * @param hospClass String
     */
    public void modifyhospOrderCode(String hospOrderCode) {
        this.hospOrderCode.modifyValue(hospOrderCode);
    }

    /**
     * �����շ����ȼ�
     * @param orderCode1 String
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
     * @param hospClass String
     */
    public void modifyfeeType(String feeType) {
        this.feeType.modifyValue(feeType);
    }
}
