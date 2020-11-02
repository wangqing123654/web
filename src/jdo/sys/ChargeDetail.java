package jdo.sys;

import com.dongyang.data.TModifiedData;
import com.dongyang.data.StringValue;
import com.dongyang.data.DoubleValue;
import com.dongyang.data.IntValue;

public class ChargeDetail extends TModifiedData{
    /**
     * ��ݴ���
     */
    private String ctzCode;
    /**
     * Ժ�ڷ��ô���
     */
    private StringValue chargeHospCode = new StringValue(this);
    /**
     * �Է���
     */
    private DoubleValue ownRate = new DoubleValue(this);
    /**
     * ��ע
     */
    private StringValue description = new StringValue(this);
    /**
     * ˳��
     */
    private IntValue seq = new IntValue(this);
    /**
     * ������ݴ���
     * @param ctzCode String
     */
    public void setCtzCode(String ctzCode)
    {
        this.ctzCode = ctzCode;
    }
    /**
     * �õ���ݴ���
     * @return String
     */
    public String getCtzCode()
    {
        return ctzCode;
    }
    /**
     * ����Ժ�ڷ��ô���
     * @param chargeHospCode String
     */
    public void setChargeHospCode(String chargeHospCode)
    {
        this.chargeHospCode.setValue(chargeHospCode);
    }
    /**
     * �õ�Ժ�ڷ��ô���
     * @return String
     */
    public String getChargeHospCode()
    {
        return chargeHospCode.getValue();
    }
    /**
     * �޸�Ժ�ڷ��ô���
     * @param chargeHospCode String
     */
    public void modifyChargeHospCode(String chargeHospCode)
    {
        this.chargeHospCode.modifyValue(chargeHospCode);
    }
    /**
     * �����Է���
     * @param ownRate double
     */
    public void setOwnRate(double ownRate)
    {
        this.ownRate.setValue(ownRate);
    }
    /**
     * �õ��Է���
     * @return double
     */
    public double getOwnRate()
    {
        return ownRate.getValue();
    }
    /**
     * �޸��Է���
     * @param ownRate double
     */
    public void modifyOwnRate(double ownRate)
    {
        this.ownRate.modifyValue(ownRate);
    }
    /**
     * ���ñ�ע
     * @param description String
     */
    public void setDescription(String description)
    {
        this.description.setValue(description);
    }
    /**
     * �õ���ע
     * @return String
     */
    public String getDescription()
    {
        return description.getValue();
    }
    /**
     * �޸ı�ע
     * @param description String
     */
    public void modifyDescription(String description)
    {
        this.description.modifyValue(description);
    }
    /**
     * ����˳��
     * @param seq int
     */
    public void setSeq(int seq)
    {
        this.seq.setValue(seq);
    }
    /**
     * �õ�˳��
     * @return int
     */
    public int getSeq()
    {
        return seq.getValue();
    }
    /**
     * �޸�˳��
     * @param seq int
     */
    public void modifySeq(int seq)
    {
        this.seq.modifyValue(seq);
    }
}
