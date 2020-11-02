package jdo.ins;

import com.dongyang.data.TModifiedData;
import com.dongyang.data.TimestampValue;
import com.dongyang.data.BooleanValue;
import com.dongyang.data.DoubleValue;
import com.dongyang.data.StringValue;
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
public class InsSxKa004 extends TModifiedData {
    /**
    * ҽ�Ʒ�����ʩ����
    */
   private StringValue aka100 = new StringValue(this);
   /**
    * ����޼�
    */
   private DoubleValue limitPrice = new DoubleValue(this);
   /**
    * ҽԺ�ȼ�
    */
   private StringValue aka101 = new StringValue(this);
   /**
    * ������ʩ����
    */
   private StringValue aka102 = new StringValue(this);
   /**
    * �շ����
    */
   private StringValue aka063 = new StringValue(this);
   /**
    * �շ���Ŀ�ȼ�
    */
   private StringValue aka065 = new StringValue(this);
   /**
    * һ���������
    */
   private StringValue bka246 = new StringValue(this);
   /**
    * �����������
    */
   private StringValue bka247 = new StringValue(this);
   /**
    * �����������
    */
   private StringValue bka260 = new StringValue(this);
   /**
    * ����Ŀ¼ʹ�÷�Χ
    */
   private StringValue bka001 = new StringValue(this);
   /**
    * �����ȼ�
    */
   private StringValue bka103 = new StringValue(this);
   /**
    * ע����
    */
   private StringValue aka066 = new StringValue(this);
   /**
    * ��׼�۸�
    */
   private DoubleValue aka068 = new DoubleValue(this);
   /**
    * �Ը�����
    */
   private DoubleValue aka069 = new DoubleValue(this);
   /**
    * ����֧����׼
    */
   private DoubleValue aka104 = new DoubleValue(this);
   /**
    * ������
    */
   private StringValue aae011 = new StringValue(this);
   /**
    * ��������
    */
   private TimestampValue aae036 = new TimestampValue(this);
   /**
    * ���ݷ������
    */
   private StringValue baa001 = new StringValue(this);
   /**
    * ��ǰ��Ч��־
    */
   private BooleanValue aae100 = new BooleanValue(this);
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
   public void setaka100(String aka100) {
       this.aka100.setValue(aka100);
   }

   /**
    * ҽ�Ʒ�����ʩ����
    * @return String ҩƷ����
    */
   public String getaka100() {
       return aka100.getValue();
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
      this.limitPrice.modifyValue(limitPrice);
  }
  /**
    * ����ҽԺ�ȼ�
    * @param orderCode1 String
    */
   public void setaka101(String aka101) {
       this.aka101.setValue(aka101);
   }

   /**
    * �ȵ�ҽԺ�ȼ�
    * @return String
    */
   public String getaka101() {
       return aka101.getValue();
   }

   /**
    * �޸�ҽԺ�ȼ�
    * @param hospClass String
    */
   public void modifyaka101(String aka101) {
       this.aka101.modifyValue(aka101);
   }

   /**
    * ���÷�����ʩ����
    * @param orderCode1 String
    */
   public void setaka102(String aka102) {
       this.aka102.setValue(aka102);
   }

   /**
    * �ȵ�������ʩ����
    * @return String
    */
   public String getaka102() {
       return aka102.getValue();
   }

   /**
    * �޸ķ�����ʩ����
    * @param hospClass String
    */
   public void modifyaka102(String aka102) {
       this.aka102.modifyValue(aka102);
   }

   /**
    * �����շ����
    * @param orderCode1 String
    */
   public void setaka063(String aka063) {
       this.aka063.setValue(aka063);
   }

   /**
    * �ȵ��շ����
    * @return String
    */
   public String getaka063() {
       return aka063.getValue();
   }

   /**
    * �޸��շ����
    * @param hospClass String
    */
   public void modifyaka063(String aka063) {
       this.aka063.modifyValue(aka063);
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
   public void setbka246(String bka246) {
       this.bka246.setValue(bka246);
   }

   /**
    * �ȵ�һ���������
    * @return String
    */
   public String getorderCat1() {
       return bka246.getValue();
   }

   /**
    * �޸�һ���������
    * @param hospClass String
    */
   public void modifybka246(String bka246) {
       this.bka246.modifyValue(bka246);
   }

   /**
    * ���ö����������
    * @param orderCode1 String
    */
   public void setbka247(String bka247) {
       this.bka247.setValue(bka247);
   }

   /**
    * �ȵ������������
    * @return String
    */
   public String getbka247() {
       return bka247.getValue();
   }

   /**
    * �޸Ķ����������
    * @param hospClass String
    */
   public void modifybka247(String bka247) {
       this.bka247.modifyValue(bka247);
   }

   /**
    * ���������������
    * @param orderCode3 String
    */
   public void setbka260(String bka260) {
       this.bka260.setValue(bka260);
   }

   /**
    * �ȵ������������
    * @return String
    */
   public String getbka260() {
       return bka260.getValue();
   }

   /**
    * �޸������������
    * @param hospClass String
    */
   public void modifybka260(String bka260) {
       this.bka260.modifyValue(bka260);
   }


   /**
    * ��������Ŀ¼ʹ�÷�Χ
    * @param orderCode1 String
    */
   public void setbka001(String bka001) {
       this.bka001.setValue(bka001);
   }

   /**
    * �ȵ�����Ŀ¼ʹ�÷�Χ
    * @return String
    */
   public String getbka001() {
       return bka001.getValue();
   }

   /**
    * �޸�����Ŀ¼ʹ�÷�Χ
    * @param hospClass String
    */
   public void modifybka001(String bka001) {
       this.bka001.modifyValue(bka001);
   }
   /**
    * ���ò����ȼ�
    * @param bka103 String
    */
   public void setbka103(String bka103){
       this.bka103.setValue(bka103);
   }
   /**
    * �õ������ȼ�
    * @return String
    */
   public String getbka103(){
       return bka103.getValue();
   }
   /**
    * �޸Ĳ����ȼ�
    * @param bka103 String
    */
   public void modifybka103(String bka103){
       this.bka103.modifyValue(bka103);
   }

   /**
    * ����ע����
    * @param orderCode1 String
    */
   public void setaka066(String aka066) {
       this.aka066.setValue(aka066);
   }

   /**
    * �õ�ע����
    * @return String
    */
   public String getaka066() {
       return aka066.getValue();
   }

   /**
    * �޸�ע����
    * @param hospClass String
    */
   public void modifyaka066(String aka066) {
       this.aka066.modifyValue(aka066);
   }

   /**
    * ��׼�۸�
    * @param orderCode1 String
    */
   public void setaka068(double aka068) {
       this.aka068.setValue(aka068);
   }

   /**
    * �õ���׼�۸�
    * @return String
    */
   public double getaka068() {
       return aka068.getValue();
   }

   /**
    * �޸ı�׼�۸�
    * @param hospClass String
    */
   public void modifyaka068(double aka068) {
       this.aka068.modifyValue(aka068);
   }

   /**
    * �����Ը�����
    * @param orderCode1 String
    */
   public void setaka069(double aka069) {
       this.aka069.setValue(aka069);
   }

   /**
    * �õ��Ը�����
    * @return String
    */
   public double getaka069() {
       return aka069.getValue();
   }

   /**
    * �޸��Ը�����
    * @param hospClass String
    */
   public void modifyaka069(double aka069) {
       this.aka069.modifyValue(aka069);
   }

   /**
    * ���û���֧����׼
    * @param orderCode1 String
    */
   public void setaka104(double aka104) {
       this.aka104.setValue(aka104);
   }

   /**
    * �õ�����֧����׼
    * @return String
    */
   public double getaka104() {
       return aka104.getValue();
   }

   /**
    * �޸Ļ���֧����׼
    * @param hospClass String
    */
   public void modifyaka104(double aka104) {
       this.aka104.modifyValue(aka104);
   }

   /**
    * ���þ�����
    * @param orderCode1 String
    */
   public void setaae011(String aae011) {
       this.aae011.setValue(aae011);
   }

   /**
    * �õ�������
    * @return String
    */
   public String getaae011() {
       return aae011.getValue();
   }

   /**
    * �޸ľ�����
    * @param hospClass String
    */
   public void modifyaae011(String aae011) {
       this.aae011.modifyValue(aae011);
   }

   /**
    * ���þ�������
    * @param orderCode1 String
    */
   public void setaae036(Timestamp aae036) {
       this.aae036.setValue(aae036);
   }

   /**
    * �õ���������
    * @return String
    */
   public Timestamp getaae036() {
       return aae036.getValue();
   }

   /**
    * �޸ľ�������
    * @param hospClass String
    */
   public void modifyaae036(Timestamp aae036) {
       this.aae036.modifyValue(aae036);
   }

   /**
    * �������ݷ������
    * @param orderCode1 String
    */
   public void setbaa001(String baa001) {
       this.baa001.setValue(baa001);
   }

   /**
    * �õ����ݷ������
    * @return String
    */
   public String getbaa001() {
       return baa001.getValue();
   }

   /**
    * �޸����ݷ������
    * @param hospClass String
    */
   public void modifybaa001(String baa001) {
       this.baa001.modifyValue(baa001);
   }

   /**
    * ���õ�ǰ��Ч��־
    * @param orderCode1 String
    */
   public void setaae100(boolean aae100) {

       this.aae100.setValue(aae100);
   }

   /**
    * �õ���ǰ��Ч��־
    * @return String
    */
   public boolean getaae100() {
       return aae100.getValue();
   }

   /**
    * �޸ĵ�ǰ��Ч��־
    * @param hospClass String
    */
   public void modifyaae100(boolean aae100) {
           this.aae100.modifyValue(aae100);
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
