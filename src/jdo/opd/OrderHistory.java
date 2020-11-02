package jdo.opd;

import java.sql.Timestamp;

import jdo.sys.Pat;

import com.dongyang.data.DoubleValue;
import com.dongyang.data.IntValue;
import com.dongyang.data.StringValue;
import com.dongyang.data.TModifiedData;
import com.dongyang.data.TParm;
import com.dongyang.data.TimestampValue;

/**
 *
 * <p>
 * Title: ҩ��������Ŀ��������jdo
 * </p>
 *
 * <p>
 * Description:ҩ��������Ŀ��������jdo
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 *
 * <p>
 * Company:Javahis
 * </p>
 *
 * @author ehui 200800908
 * @version 1.0
 */
public class OrderHistory
    extends TModifiedData {
    /**
     * ��������
     */
    private Pat pat;
    /**
     * �������
     */
    private StringValue caseNo = new StringValue(this);
    /**
     * ����ǩ��
     */
    private StringValue rxNo = new StringValue(this);
    /**
     * ѭ���
     */
    private IntValue seqNo = new IntValue(this);
    /**
     * �����ˆT
     */
    private StringValue optUser = new StringValue(this);
    /**
     * ��������
     */
    private TimestampValue optDate = new TimestampValue(this);
    /**
     * '������ĩ
     */
    private StringValue optTerm = new StringValue(this);
    /**
     * ����
     */
    private IntValue presrtNo = new IntValue(this);
    /**
     * ��������
     */
    private StringValue regionCode = new StringValue(this);
    /**
     * ������
     */
    private StringValue mrNo = new StringValue(this);
    /**
     * ����״̬
     */
    private StringValue admType = new StringValue(this);
    /**
     * ҽ������
     */
    private StringValue rxType = new StringValue(this);
    /**
     * '����ע�� (�Ա�)'
     */
    private StringValue releaseFlg = new StringValue(this);
    /**
     * ������ע��
     */
    private StringValue linkmainFlg = new StringValue(this);
    /**
     * ���Ӻ�
     */
    private DoubleValue linkNo = new DoubleValue(this);
    /**
     * ҽ�����
     */
    private StringValue orderCode = new StringValue(this);
    /**
     * ҽ������
     */
    private StringValue orderDesc = new StringValue(this);
    /**
     * ��Ʒ��
     */
    private StringValue goodsDesc = new StringValue(this);
    /**
     * ҽ��ϸ����
     */
    private StringValue orderCat1 = new StringValue(this);
    /**
     * ����
     */
    private DoubleValue takeQty = new DoubleValue(this);
    /**
     * ��ҩ��λ
     */
    private StringValue mediUnit = new StringValue(this);
    /**
     * Ƶ�δ���
     */
    private StringValue freqCode = new StringValue(this);
    /**
     * ��ҩ;������
     */
    private StringValue routeCode = new StringValue(this);
    /**
     * ����/�շ�
     */
    private IntValue takeDays = new IntValue(this);
    /**
     * ����
     */
    private DoubleValue totQty = new DoubleValue(this);
    /**
     * ʵ�ʷ�ҩ��
     */
    private DoubleValue dgtTot = new DoubleValue(this);
    /**
     * ��ҩ��λ
     */
    private StringValue dispenseUnit = new StringValue(this);
    /**
     * ��浥λ��ҩע��
     */
    private StringValue opdgiveboxFlg = new StringValue(this);
    /**
     * �ԷѼ�
     */
    private DoubleValue ownPrice = new DoubleValue(this);
    /**
     * ҽ����
     */
    private DoubleValue nhiPrice = new DoubleValue(this);
    /**
     * ����ۿ�%
     */
    private DoubleValue discnrate = new DoubleValue(this);
    /**
     * �Էѽ��
     */
    private DoubleValue ownAmt = new DoubleValue(this);
    /**
     * Ӧ�����
     */
    private DoubleValue totAmt = new DoubleValue(this);
    /**
     * ��ע��
     */
    private StringValue description = new StringValue(this);
    /**
     * ��ʿ��ע
     */
    private StringValue nsNote = new StringValue(this);
    /**
     * ����ҽʦ
     */
    private StringValue drCode = new StringValue(this);
    /**
     * \��������ʱ��
     */
    private TimestampValue orderDate = new TimestampValue(this);
    /**
     * ��������
     */
    private StringValue deptCode = new StringValue(this);
    /**
     * ͣ��ҽʦ
     */
    private StringValue dcDrCode = new StringValue(this);
    /**
     * ͣ��ʱ��
     */
    private TimestampValue dcOrderDate = new TimestampValue(this);
    /**
     * ͣ�ÿ���
     */
    private StringValue dcDeptCode = new StringValue(this);
    /**
     * ִ�п���
     */
    private StringValue rborderDeptCode = new StringValue(this);
    /**
     * ����ҽ��ע��
     */
    private StringValue setmainFlg = new StringValue(this);
    /**
     * ����ҽ����ţ���������ͬʱ����2����ͬ�ļ���ҽ����
     */
    private IntValue ordsetGroupNo = new IntValue(this);
    /**
     * ����ҽ�����
     */
    private StringValue ordersetCode = new StringValue(this);
    /**
     * ����ע��
     */
    private StringValue hideFlg = new StringValue(this);
    /**
     * �������
     */
    private StringValue rpttypeCode = new StringValue(this);
    /**
     * ����
     */
    private StringValue optitemCode = new StringValue(this);
    /**
     * ��������
     */
    private StringValue devCode = new StringValue(this);
    /**
     * ���뵥ģ��
     */
    private StringValue mrCode = new StringValue(this);
    /**
     * �ṹ�����뵥���
     */
    private IntValue fileNo = new IntValue(this);
    /**
     * ҽ�켨Ч����
     */
    private StringValue degreeCode = new StringValue(this);
    /**
     * ����ע��
     */
    private StringValue urgentFlg = new StringValue(this);
    /**
     * �������
     */
    private StringValue inspayType = new StringValue(this);
    /**
     * ҩƷ����
     */
    private StringValue phaType = new StringValue(this);
    /**
     * ���ʹ����
     */
    private StringValue doseType = new StringValue(this);
    /**
     * ��ͯ����ǩע��
     */
    private StringValue printtypeflgInfant = new StringValue(this);
    /**
     * ����ע��
     */
    private StringValue expensiveFlg = new StringValue(this);
    /**
     * ����ҩƷ����
     */
    private StringValue ctrldrugclassCode = new StringValue(this);
    /**
     * ��ҩ��ʽ
     */
    private StringValue dctagentCode = new StringValue(this);
    /**
     * ����巨
     */
    private StringValue dctexcepCode = new StringValue(this);
    /**
     * ��Ƭ������(ml) or ��Ƭʹ�ü���'
     */
    private IntValue dctTakeQty = new IntValue(this);
    /**
     * �����ܰ���
     */
    private IntValue packageTot = new IntValue(this);
    /**
     * �õ�����
     * @return Pat
     */
    public Pat getPat() {
        return pat;
    }

    /**
     * ���ò���
     * @param pat Pat
     */
    public void setPat(Pat pat) {
        this.pat = pat;
    }

    /**
     * �õ��������
     * @return String
     */
    public String getCaseNo() {
        return caseNo.getValue();
    }

    /**
     * ���ÿ������
     * @param value String
     */
    public void setCaseNo(String value) {
        this.caseNo.setValue(value);
    }

    /**
     * �õ�������
     * @return String
     */
    public String getRxNo() {
        return rxNo.getValue();
    }

    /**
     * ���ô�����
     * @param value String
     */
    public void setRxNo(String value) {
        this.rxNo.setValue(value);
    }

    /**
     * �õ�˳���
     * @return int
     */
    public int getSeqNo() {
        return seqNo.getValue();
    }

    /**
     * ����˳���
     * @param value int
     */
    public void setSeqNo(int value) {
        this.seqNo.setValue(value);
    }

    /**
     * �õ�������Ա
     * @return String
     */
    public String getOptUser() {
        return optUser.getValue();
    }

    /**
     * ���ò�����Ա
     * @param value String
     */
    public void setOptUser(String value) {
        this.optUser.setValue(value);
    }

    /**
     * �õ���������
     * @return Timestamp
     */
    public Timestamp getOptDate() {
        return optDate.getValue();
    }

    /**
     * ���ò�������
     * @param value Timestamp
     */
    public void setOptDate(Timestamp value) {
        this.optDate.setValue(value);
    }

    /**
     * �õ�������ĩ
     * @return String
     */
    public String getOptTerm() {
        return optTerm.getValue();
    }

    /**
     * ���ò�����ĩ
     * @param value String
     */
    public void setOptTerm(String value) {
        this.optTerm.setValue(value);
    }

    /**
     * �õ�����
     * @return int
     */
    public int getPresrtNo() {
        return presrtNo.getValue();
    }

    /**
     * ���÷���
     * @param value int
     */
    public void setPresrtNo(int value) {
        this.presrtNo.setValue(value);
    }

    /**
     * �õ�����
     * @return String
     */
    public String getRegionCode() {
        return regionCode.getValue();
    }

    /**
     * ��������
     * @param value String
     */
    public void setRegionCode(String value) {
        this.regionCode.setValue(value);
    }

    /**
     * �õ�������
     * @return String
     */
    public String getMrNo() {
        return mrNo.getValue();
    }

    /**
     * ���ò�����
     * @param value String
     */
    public void setMrNo(String value) {
        this.mrNo.setValue(value);
    }

    /**
     * �õ�����״̬
     * @return String
     */
    public String getAdmType() {
        return admType.getValue();
    }

    /**
     * ���þ���״̬
     * @param value String
     */
    public void setAdmType(String value) {
        this.admType.setValue(value);
    }

    /**
     * �õ�ҽ������
     * @return String
     */
    public String getRxType() {
        return rxType.getValue();
    }

    /**
     * ����ҽ������
     * @param value String
     */
    public void setRxType(String value) {
        this.rxType.setValue(value);
    }

    /**
     * �õ�����ע��
     * @return String
     */
    public String getReleaseFlg() {
        return releaseFlg.getValue();
    }

    /**
     * ���ý���ע��
     * @param value String
     */
    public void setReleaseFlg(String value) {
        this.releaseFlg.setValue(value);
    }

    /**
     * �õ�������ע��
     * @return String
     */

    public String getLinkmainFlg() {
        return linkmainFlg.getValue();
    }

    /**
     * ����������ע��
     * @param value String
     */
    public void setLinkmainFlg(String value) {
        this.linkmainFlg.setValue(value);
    }

    /**
     * �õ����Ӻ�
     * @return double
     */
    public double getLinkNo() {
        return linkNo.getValue();
    }

    /**
     * �������Ӻ�
     * @param value double
     */
    public void setLinkNo(double value) {
        this.linkNo.setValue(value);
    }

    /**
     * �õ�ҽ�����
     * @return String
     */
    public String getOrderCode() {
        return orderCode.getValue();
    }

    /**
     * ����ҽ�����
     * @param value String
     */
    public void setOrderCode(String value) {
        this.orderCode.setValue(value);
    }

    /**
     * �õ�ҽ������
     * @return String
     */
    public String getOrderDesc() {
        return orderDesc.getValue();
    }

    /**
     * ����ҽ������
     * @param value String
     */
    public void setOrderDesc(String value) {
        this.orderDesc.setValue(value);
    }

    /**
     * �õ���Ʒ��
     * @return String
     */
    public String getGoodsDesc() {
        return goodsDesc.getValue();
    }

    /**
     * ������Ʒ��
     * @param value String
     */
    public void setGoodsDesc(String value) {
        this.goodsDesc.setValue(value);
    }

    /**
     * �õ�ҽ��ϸ����
     * @return String
     */
    public String getOrderCat1() {
        return orderCat1.getValue();
    }

    /**
     * ����ҽ��ϸ����
     * @param value String
     */
    public void setOrderCat1(String value) {
        this.orderCat1.setValue(value);
    }

    /**
     * �õ�����
     * @return double
     */
    public double getTakeQty() {
        return takeQty.getValue();
    }

    /**
     * ��������
     * @param value double
     */
    public void setTakeQty(double value) {
        this.takeQty.setValue(value);
    }

    /**
     * �õ���ҩ��λ
     * @return String
     */
    public String getMediUnit() {
        return mediUnit.getValue();
    }

    /**
     * ���ÿ�ҩ��λ
     * @param value String
     */
    public void setMediUnit(String value) {
        this.mediUnit.setValue(value);
    }

    /**
     * �õ�Ƶ�δ���
     * @return String
     */
    public String getFreqCode() {
        return freqCode.getValue();
    }

    /**
     * ����Ƶ�δ���
     * @param value String
     */
    public void setFreqCode(String value) {
        this.freqCode.setValue(value);
    }

    /**
     * �õ���ҩ;������
     * @return String
     */
    public String getRouteCode() {
        return routeCode.getValue();
    }

    /**
     * ���ø�ҩ;������
     * @param value String
     */
    public void setRouteCode(String value) {
        this.routeCode.setValue(value);
    }

    /**
     * �õ�����/�շ�'
     * @return int
     */
    public int getTakeDays() {
        return takeDays.getValue();
    }

    /**
     * ���ø���/�շ�'
     * @param value int
     */
    public void setTakeDays(int value) {
        this.takeDays.setValue(value);
    }

    /**
     * �õ�����
     * @return double
     */
    public double getTotQty() {
        return totQty.getValue();
    }

    /**
     * ��������
     * @param value double
     */
    public void setTotQty(double value) {
        this.totQty.setValue(value);
    }

    /**
     * �õ�ʵ�ʷ�ҩ��
     * @return double
     */
    public double getDgtTot() {
        return dgtTot.getValue();
    }

    /**
     * ����ʵ�ʷ�ҩ��
     * @param value double
     */
    public void setDgtTot(double value) {
        this.dgtTot.setValue(value);
    }

    /**
     * �õ���ҩ��λ
     * @return String
     */
    public String getDispenseUnit() {
        return dispenseUnit.getValue();
    }

    /**
     * ���÷�ҩ��λ
     * @param value String
     */
    public void setDispenseUnit(String value) {
        this.dispenseUnit.setValue(value);
    }

    /**
     * �õ���浥λ��ҩע��
     * @return String
     */
    public String getOpdgiveboxFlg() {
        return opdgiveboxFlg.getValue();
    }

    /**
     * ���ÿ�浥λ��ҩע��
     * @param value String
     */
    public void setOpdgiveboxFlg(String value) {
        this.opdgiveboxFlg.setValue(value);
    }

    /**
     * �õ��ԷѼ�
     * @return double
     */
    public double getOwnPrice() {
        return ownPrice.getValue();
    }

    /**
     * �����ԷѼ�
     * @param value double
     */
    public void setOwnPrice(double value) {
        this.ownPrice.setValue(value);
    }

    /**
     * �õ�ҽ����
     * @return double
     */
    public double getNhiPrice() {
        return nhiPrice.getValue();
    }

    /**
     * ����ҽ����
     * @param value double
     */
    public void setNhiPrice(double value) {
        this.nhiPrice.setValue(value);
    }

    /**
     * �õ�����ۿ�%
     * @return double
     */
    public double getDiscnrate() {
        return discnrate.getValue();
    }

    /**
     * ��������ۿ�%
     * @param value double
     */
    public void setDiscnrate(double value) {
        this.discnrate.setValue(value);
    }

    /**
     * �õ��Էѽ��
     * @return double
     */
    public double getOwnAmt() {
        return ownAmt.getValue();
    }

    /**
     * �����Էѽ��
     * @param value double
     */
    public void setOwnAmt(double value) {
        this.ownAmt.setValue(value);
    }

    /**
     * �õ�Ӧ�����
     * @return double
     */
    public double getTotAmt() {
        return totAmt.getValue();
    }

    /**
     * ����Ӧ�����
     * @param value double
     */
    public void setTotAmt(double value) {
        this.totAmt.setValue(value);
    }

    /**
     * �õ���ע��
     * @return String
     */
    public String getDescription() {
        return description.getValue();
    }

    /**
     * ���ñ�ע��
     * @param value String
     */
    public void setDescription(String value) {
        this.description.setValue(value);
    }

    /**
     * �õ���ʿ��ע
     * @return String
     */
    public String getNsNote() {
        return nsNote.getValue();
    }

    /**
     * ���û�ʿ��ע
     * @param value String
     */
    public void setNsNote(String value) {
        this.nsNote.setValue(value);
    }

    /**
     * �õ�����ҽʦ
     * @return String
     */
    public String getDrCode() {
        return drCode.getValue();
    }

    /**
     * ���ÿ���ҽʦ
     * @param value String
     */
    public void setDrCode(String value) {
        this.drCode.setValue(value);
    }

    /**
     * �õ� ��������ʱ��
     * @return Timestamp
     */
    public Timestamp getOrderDate() {
        return orderDate.getValue();
    }

    /**
     * ���ÿ�������ʱ��
     * @param value Timestamp
     */
    public void setOrderDate(Timestamp value) {
        this.orderDate.setValue(value);
    }

    /**
     * �õ� ��������
     * @return String
     */
    public String getDeptCode() {
        return deptCode.getValue();
    }

    /**
     * ���ÿ�������
     * @param value String
     */
    public void setDeptCode(String value) {
        this.deptCode.setValue(value);
    }

    /**
     * �õ�ͣ��ҽʦ
     * @return String
     */
    public String getDcDrCode() {
        return dcDrCode.getValue();
    }

    /**
     * ����ͣ��ҽʦ
     * @param value String
     */
    public void setDcDrCode(String value) {
        this.dcDrCode.setValue(value);
    }

    /**
     * �õ�ͣ��ʱ��
     * @return Timestamp
     */
    public Timestamp getDcOrderDate() {
        return dcOrderDate.getValue();
    }

    /**
     * ����ͣ��ʱ��
     * @param value Timestamp
     */
    public void setDcOrderDate(Timestamp value) {
        this.dcOrderDate.setValue(value);
    }

    /**
     * �õ�ͣ�ÿ���
     * @return String
     */
    public String getDcDeptCode() {
        return dcDeptCode.getValue();
    }

    /**
     * ����ͣ�ÿ���
     * @param value String
     */
    public void setDcDeptCode(String value) {
        this.dcDeptCode.setValue(value);
    }

    /**
     * �õ�ִ�п���
     * @return String
     */
    public String getRborderDeptCode() {
        return rborderDeptCode.getValue();
    }

    /**
     * \����ִ�п���
     * @param value String
     */
    public void setRborderDeptCode(String value) {
        this.rborderDeptCode.setValue(value);
    }

    /**
     * �õ�����ҽ��ע��
     * @return String
     */
    public String getSetmainFlg() {
        return setmainFlg.getValue();
    }

    /**
     * ���ü���ҽ��ע��
     * @param value String
     */
    public void setSetmainFlg(String value) {
        this.setmainFlg.setValue(value);
    }

    /**
     * �õ�����ҽ����ţ���������ͬʱ����2����ͬ�ļ���ҽ����
     * @return int
     */
    public int getOrdsetGroupNo() {
        return ordsetGroupNo.getValue();
    }

    /**
     * ���ü���ҽ����ţ���������ͬʱ����2����ͬ�ļ���ҽ����
     * @param value int
     */
    public void setOrdsetGroupNo(int value) {
        this.ordsetGroupNo.setValue(value);
    }

    /**
     * �õ�����ҽ�����
     * @return String
     */
    public String getOrdersetCode() {
        return ordersetCode.getValue();
    }

    /**
     * ���ü���ҽ�����
     * @param value String
     */
    public void setOrdersetCode(String value) {
        this.ordersetCode.setValue(value);
    }

    /**
     * �õ�����ע��
     * @return String
     */
    public String getHideFlg() {
        return hideFlg.getValue();
    }

    /**
     * ��������ע��
     * @param value String
     */
    public void setHideFlg(String value) {
        this.hideFlg.setValue(value);
    }

    /**
     * �õ��������
     * @return String
     */
    public String getRpttypeCode() {
        return rpttypeCode.getValue();
    }

    /**
     * ���ñ������
     * @param value String
     */
    public void setRpttypeCode(String value) {
        this.rpttypeCode.setValue(value);
    }

    /**
     * �õ�����
     * @return String
     */
    public String getOptitemCode() {
        return optitemCode.getValue();
    }

    /**
     * ���ü���
     * @param value String
     */
    public void setOptitemCode(String value) {
        this.optitemCode.setValue(value);
    }

    /**
     * �õ���������
     * @return String
     */
    public String getDevCode() {
        return devCode.getValue();
    }

    /**
     * ������������
     * @param value String
     */
    public void setDevCode(String value) {
        this.devCode.setValue(value);
    }

    /**
     * �õ����뵥ģ��
     * @return String
     */
    public String getMrCode() {
        return mrCode.getValue();
    }

    /**
     * �������뵥ģ��
     * @param value String
     */
    public void setMrCode(String value) {
        this.mrCode.setValue(value);
    }

    /**
     * �õ��ṹ�����뵥���
     * @return int
     */
    public int getFileNo() {
        return fileNo.getValue();
    }

    /**
     * ���ýṹ�����뵥���
     * @param value int
     */
    public void setFileNo(int value) {
        this.fileNo.setValue(value);
    }

    /**
     * �õ�ҽ�켨Ч����
     * @return String
     */
    public String getDegreeCode() {
        return degreeCode.getValue();
    }

    /**
     * ����ҽ�켨Ч����
     * @param value String
     */
    public void setDegreeCode(String value) {
        this.degreeCode.setValue(value);
    }

    /**
     * �õ�����ע��
     * @return String
     */
    public String getUrgentFlg() {
        return urgentFlg.getValue();
    }

    /**
     * ���ü���ע��
     * @param value String
     */
    public void setUrgentFlg(String value) {
        this.urgentFlg.setValue(value);
    }

    /**
     * �õ��������
     * @return String
     */
    public String getInspayType() {
        return inspayType.getValue();
    }

    /**
     * ���ø������
     * @param value String
     */
    public void setInspayType(String value) {
        this.inspayType.setValue(value);
    }

    /**
     * �õ�ҩƷ����
     * @return String
     */
    public String getPhaType() {
        return phaType.getValue();
    }

    /**
     * ����ҩƷ����
     * @param value String
     */
    public void setPhaType(String value) {
        this.phaType.setValue(value);
    }

    /**
     * �õ����ʹ����
     * @return String
     */
    public String getDoseType() {
        return doseType.getValue();
    }

    /**
     * ���ü��ʹ����
     * @param value String
     */
    public void setDoseType(String value) {
        this.doseType.setValue(value);
    }

    /**
     * �õ���ͯ����ǩע��
     * @return String
     */
    public String getPrinttypeflgInfant() {
        return printtypeflgInfant.getValue();
    }

    /**
     * ���ö�ͯ����ǩע��
     * @param value String
     */
    public void setPrinttypeflgInfant(String value) {
        this.printtypeflgInfant.setValue(value);
    }

    /**
     * �õ�����ע��
     * @return String
     */
    public String getExpensiveFlg() {
        return expensiveFlg.getValue();
    }

    /**
     * ���ù���ע��
     * @param value String
     */
    public void setExpensiveFlg(String value) {
        this.expensiveFlg.setValue(value);
    }

    /**
     * �õ�����ҩƷ����
     * @return String
     */
    public String getCtrldrugclassCode() {
        return ctrldrugclassCode.getValue();
    }

    /**
     * ���ù���ҩƷ����
     * @param value String
     */
    public void setCtrldrugclassCode(String value) {
        this.ctrldrugclassCode.setValue(value);
    }


    /**
     * �õ���ҩ��ʽ
     * @return String
     */
    public String getDctagentCode() {
        return dctagentCode.getValue();
    }

    /**
     * ���ü�ҩ��ʽ
     * @param value String
     */
    public void setDctagentCode(String value) {
        this.dctagentCode.setValue(value);
    }

    /**
     * �õ�����巨
     * @return String
     */
    public String getDctexcepCode() {
        return dctexcepCode.getValue();
    }

    /**
     * ��������巨
     * @param value String
     */
    public void setDctexcepCode(String value) {
        this.dctexcepCode.setValue(value);
    }

    /**
     * �õ���Ƭ������
     * @return String
     */
    public int getDctTakeQty() {
        return dctTakeQty.getValue();
    }

    /**
     * ������Ƭ������
     * @param value int
     */
    public void setDctTakeQty(int value) {
        this.dctTakeQty.setValue(value);
    }

    /**
     * �õ������ܰ���
     * @return int
     */
    public int getPackageTot() {
        return packageTot.getValue();
    }

    /**
     * ���ô����ܰ���
     * @param value int
     */
    public void setPackageTot(int value) {
        this.packageTot.setValue(value);
    }


    /**
     * �޸Ŀ������
     * @param value String
     */
    public void modifyCaseNo(String value) {
        this.caseNo.modifyValue(value);
    }

    /**
     * �޸Ĵ���ǩ��
     * @param value String
     */
    public void modifyRxNo(String value) {
        this.rxNo.modifyValue(value);
    }

    /**
     * �޸�ѭ���
     * @param value int
     */
    public void modifySeqNo(int value) {
        this.seqNo.modifyValue(value);
    }

    /**
     * �޸Ĳ����ˆT
     * @param value String
     */
    public void modifyOptUser(String value) {
        this.optUser.modifyValue(value);
    }

    /**
     * �޸Ĳ�������
     * @param value Timestamp
     */
    public void modifyOptDate(Timestamp value) {
        this.optDate.modifyValue(value);
    }

    /**
     * �޸Ĳ�����ĩ
     * @param value String
     */
    public void modifyOptTerm(String value) {
        this.optTerm.modifyValue(value);
    }

    /**
     * �޸ķ���
     * @param value int
     */
    public void modifyPresrtNo(int value) {
        this.presrtNo.modifyValue(value);
    }

    /**
     * �޸ľ�������
     * @param value String
     */
    public void modifyRegionCode(String value) {
        this.regionCode.modifyValue(value);
    }

    /**
     * �޸Ĳ�����
     * @param value String
     */
    public void modifyMrNo(String value) {
        this.mrNo.modifyValue(value);
    }

    /**
     * �޸ľ���״̬
     * @param value String
     */
    public void modifyAdmType(String value) {
        this.admType.modifyValue(value);
    }

    /**
     * �޸�ҽ������
     * @param value String
     */
    public void modifyRxType(String value) {
        this.rxType.modifyValue(value);
    }

    /**
     * �޸Ľ���ע��
     * @param value String
     */
    public void modifyReleaseFlg(String value) {
        this.releaseFlg.modifyValue(value);
    }

    /**
     * �޸�������ע��
     * @param value String
     */
    public void modifyLinkmainFlg(String value) {
        this.linkmainFlg.modifyValue(value);
    }

    /**
     * �޸����Ӻ�
     * @param value double
     */
    public void modifyLinkNo(double value) {
        this.linkNo.modifyValue(value);
    }

    /**
     * �޸�ҽ�����
     * @param value String
     */
    public void modifyOrderCode(String value) {
        this.orderCode.modifyValue(value);
    }

    /**
     * �޸�ҽ������
     * @param value String
     */
    public void modifyOrderDesc(String value) {
        this.orderDesc.modifyValue(value);
    }

    /**
     * �޸���Ʒ��
     * @param value String
     */
    public void modifyGoodsDesc(String value) {
        this.goodsDesc.modifyValue(value);
    }

    /**
     * �޸�ҽ��ϸ����
     * @param value String
     */
    public void modifyOrderCat1(String value) {
        this.orderCat1.modifyValue(value);
    }

    /**
     * �޸�����
     * @param value double
     */
    public void modifyTakeQty(double value) {
        this.takeQty.modifyValue(value);
    }

    /**
     * �޸Ŀ�ҩ��λ
     * @param value String
     */
    public void modifyMediUnit(String value) {
        this.mediUnit.modifyValue(value);
    }

    /**
     * �޸�Ƶ�δ���
     * @param value String
     */
    public void modifyFreqCode(String value) {
        this.freqCode.modifyValue(value);
    }

    /**
     * �޸ĸ�ҩ;������
     * @param value String
     */
    public void modifyRouteCode(String value) {
        this.routeCode.modifyValue(value);
    }

    /**
     * �޸ĸ���/�շ�
     * @param value int
     */
    public void modifyTakeDays(int value) {
        this.takeDays.modifyValue(value);
    }

    /**
     * �޸�����
     * @param value double
     */
    public void modifyTotQty(double value) {
        this.totQty.modifyValue(value);
    }

    /**
     * �޸�ʵ�ʷ�ҩ��
     * @param value double
     */
    public void modifyDgtTot(double value) {
        this.dgtTot.modifyValue(value);
    }

    /**
     * �޸ķ�ҩ��λ
     * @param value String
     */
    public void modifyDispenseUnit(String value) {
        this.dispenseUnit.modifyValue(value);
    }

    /**
     * �޸Ŀ�浥λ��ҩע��
     * @param value String
     */
    public void modifyOpdgiveboxFlg(String value) {
        this.opdgiveboxFlg.modifyValue(value);
    }

    /**
     * �޸��ԷѼ�
     * @param value double
     */
    public void modifyOwnPrice(double value) {
        this.ownPrice.modifyValue(value);
    }

    /**
     * �޸�ҽ����
     * @param value double
     */
    public void modifyNhiPrice(double value) {
        this.nhiPrice.modifyValue(value);
    }

    /**
     * �޸�����ۿ�%
     * @param value double
     */
    public void modifyDiscnrate(double value) {
        this.discnrate.modifyValue(value);
    }

    /**
     * �޸��Էѽ��
     * @param value double
     */
    public void modifyOwnAmt(double value) {
        this.ownAmt.modifyValue(value);
    }

    /**
     * �޸�Ӧ�����
     * @param value double
     */
    public void modifyTotAmt(double value) {
        this.totAmt.modifyValue(value);
    }

    /**
     * �޸ı�ע��
     * @param value String
     */
    public void modifyDescription(String value) {
        this.description.modifyValue(value);
    }

    /**
     * �޸Ļ�ʿ��ע
     * @param value String
     */
    public void modifyNsNote(String value) {
        this.nsNote.modifyValue(value);
    }

    /**
     * �޸Ŀ���ҽʦ
     * @param value String
     */
    public void modifyDrCode(String value) {
        this.drCode.modifyValue(value);
    }

    /**
     * �޸Ŀ�������ʱ��
     * @param value Timestamp
     */
    public void modifyOrderDate(Timestamp value) {
        this.orderDate.modifyValue(value);
    }

    /**
     * �޸Ŀ�������
     * @param value String
     */
    public void modifyDeptCode(String value) {
        this.deptCode.modifyValue(value);
    }

    /**
     * �޸�ͣ��ҽʦ
     * @param value String
     */
    public void modifyDcDrCode(String value) {
        this.dcDrCode.modifyValue(value);
    }

    /**
     * �޸�ͣ��ʱ��
     * @param value Timestamp
     */
    public void modifyDcOrderDate(Timestamp value) {
        this.dcOrderDate.modifyValue(value);
    }

    /**
     * �޸�ͣ�ÿ���
     * @param value String
     */
    public void modifyDcDeptCode(String value) {
        this.dcDeptCode.modifyValue(value);
    }

    /**
     * �޸�ִ�п���
     * @param value String
     */
    public void modifyRborderDeptCode(String value) {
        this.rborderDeptCode.modifyValue(value);
    }

    /**
     * �޸ļ���ҽ��ע��
     * @param value String
     */
    public void modifySetmainFlg(String value) {
        this.setmainFlg.modifyValue(value);
    }

    /**
     * �޸ļ���ҽ����ţ���������ͬʱ����2����ͬ�ļ���ҽ����
     * @param value int
     */
    public void modifyOrdsetGroupNo(int value) {
        this.ordsetGroupNo.modifyValue(value);
    }

    /**
     * �޸ļ���ҽ�����
     * @param value String
     */
    public void modifyOrdersetCode(String value) {
        this.ordersetCode.modifyValue(value);
    }

    /**
     * �޸�����ע��
     * @param value String
     */
    public void modifyHideFlg(String value) {
        this.hideFlg.modifyValue(value);
    }

    /**
     * �޸ı������
     * @param value String
     */
    public void modifyRpttypeCode(String value) {
        this.rpttypeCode.modifyValue(value);
    }

    /**
     * �޸ļ���
     * @param value String
     */
    public void modifyOptitemCode(String value) {
        this.optitemCode.modifyValue(value);
    }

    /**
     * �޸���������
     * @param value String
     */
    public void modifyDevCode(String value) {
        this.devCode.modifyValue(value);
    }

    /**
     * �޸����뵥ģ��
     * @param value String
     */
    public void modifyMrCode(String value) {
        this.mrCode.modifyValue(value);
    }

    /**
     * �޸Ľṹ�����뵥���
     * @param value int
     */
    public void modifyFileNo(int value) {
        this.fileNo.modifyValue(value);
    }

    /**
     * �޸�ҽ�켨Ч����
     * @param value String
     */
    public void modifyDegreeCode(String value) {
        this.degreeCode.modifyValue(value);
    }

    /**
     * �޸ļ���ע��
     * @param value String
     */
    public void modifyUrgentFlg(String value) {
        this.urgentFlg.modifyValue(value);
    }

    /**
     * �޸ĸ������
     * @param value String
     */
    public void modifyInspayType(String value) {
        this.inspayType.modifyValue(value);
    }

    /**
     * �޸�ҩƷ����
     * @param value String
     */
    public void modifyPhaType(String value) {
        this.phaType.modifyValue(value);
    }

    /**
     * �޸ļ��ʹ����
     * @param value String
     */
    public void modifyDoseType(String value) {
        this.doseType.modifyValue(value);
    }

    /**
     * �޸Ķ�ͯ����ǩע��
     * @param value String
     */
    public void modifyPrinttypeflgInfant(String value) {
        this.printtypeflgInfant.modifyValue(value);
    }

    /**
     * �޸Ĺ���ע��
     * @param value String
     */
    public void modifyExpensiveFlg(String value) {
        this.expensiveFlg.modifyValue(value);
    }

    /**
     * �޸Ĺ���ҩƷ����
     * @param value String
     */
    public void modifyCtrldrugclassCode(String value) {
        this.ctrldrugclassCode.modifyValue(value);
    }


    /**
     * �޸ļ�ҩ��ʽ
     * @param value String
     */

    public void modifyDctagentCode(String value) {
        this.dctagentCode.modifyValue(value);
    }

    /**
     * �޸�����巨
     * @param value String
     */

    public void modifyDctexcepCode(String value) {
        this.dctexcepCode.modifyValue(value);
    }

    /**
     * �޸���Ƭ������
     * @param value int
     */

    public void modifyDctTakeQty(int value) {
        this.dctTakeQty.modifyValue(value);
    }

    /**
     * �޸Ĵ����ܰ���
     * @param value int
     */

    public void modifyPackageTot(int value) {
        this.packageTot.modifyValue(value);
    }
	/**
	 * ȡ��pram
	 */
	public TParm getParm()
    {
        TParm result = super.getParm();
        if(getPat() != null)
            result.setData("MR_NO",pat.getMrNo());
        return result;
    }

}
