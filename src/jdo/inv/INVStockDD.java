package jdo.inv;

import com.dongyang.data.TModifiedData;
import java.sql.Timestamp;
import com.dongyang.data.TParm;
import jdo.sys.SystemTool;
import jdo.sys.Operator;

public class INVStockDD
    extends TModifiedData {
    /**
     * ���ʴ��� INV_CODE
     */
    private String invCode;
    /**
     * ������� INVSEQ_NO
     */
    private int invseqNo;
    /**
     * ������� REGION_CODE
     */
    private String regionCode;
    /**
     * ������� BATCH_SEQ
     */
    private int batchSeq;
    /**
     * ���T���� ORG_CODE
     */
    private String orgCode;
    /**
     * ���� BATCH_NO
     */
    private String batChNo;
    /**
     * Ч�� VALID_DATE
     */
    private Timestamp validDate;
    /**
     * ������ STOCK_QTY
     */
    private double stockQty;
    /**
     * ���Ʋ���ֵ UNIT_PRICE
     */
    private double unitPrice;
    /**
     * �̵���ʧע�� CHECKTOLOSE_FLG
     */
    private String checkToLostFlg;
    /**
     * ���ע�� WAST_FLG
     */
    private String wastFlg;
    /**
     * ������� VERIFYIN_DATE
     */
    private Timestamp verifyInDate;
    /**
     * ���벡���������� OUT_DATE
     */
    private Timestamp outDate;
    /**
     * ���벡��������Ա OUT_USER
     */
    private String outUser;
    /**
     * ������ MR_NO
     */
    private String mrNo;
    /**
     * ����� CASE_NO
     */
    private String caseNo;
    /**
     * ������ RX_SEQ
     */
    private int rxSeq;
    /**
     * ҽ����� SEQ_NO
     */
    private int seqNo;
    /**
     * �ż�ס�� ADM_TYPE
     */
    private String admType;
    /**
     * ��;�������� WAIT_ORG_CODE
     */
    private String waitOrgCode;
    /**
    * ��ɾ���
    */
   private boolean sqlFlg;
   /**
    * ��λ
    */
   private String stockUnit;

    /**
     *  ������
     */
    public INVStockDD() {
        StringBuffer sb = new StringBuffer();
        //���ʴ��� INV_CODE
        sb.append("invCode:INV_CODE;");
        //������� INVSEQ_NO
        sb.append("invseqNo:INVSEQ_NO;");
        //������� REGION_CODE
        sb.append("regionCode:REGION_CODE;");
        //����������� BATCH_SEQ
        sb.append("batchSeq:BATCH_SEQ;");
        // ���T���� ORG_CODE
        sb.append("orgCode:ORG_CODE;");
        //���� BATCH_NO
        sb.append("batChNo:BATCH_NO;");
        // Ч�� VALID_DATE
        sb.append("validDate:VALID_DATE;");
        //������ STOCK_QTY
        sb.append("stockQty:STOCK_QTY;");
        // ���Ʋ���ֵ UNIT_PRICE
        sb.append("unitPrice:UNIT_PRICE;");
        // �̵���ʧע�� CHECKTOLOSE_FLG
        sb.append("checkToLostFlg:CHECKTOLOSE_FLG;");
        //���ע�� WAST_FLG
        sb.append("wastFlg:WAST_FLG;");
        // ������� VERIFYIN_DATE
        sb.append("verifyInDate:VERIFYIN_DATE;");
        //���벡���������� OUT_DATE
        sb.append("outDate:OUT_DATE;");
        // ���벡��������Ա OUT_USER
        sb.append("outUser:OUT_USER;");
        // ������ MR_NO
        sb.append("mrNo:MR_NO;");
        // ����� CASE_NO
        sb.append("caseNo:CASE_NO;");
        // ������ RX_SEQ
        sb.append("rxSeq:RX_SEQ;");
        // ҽ����� SEQ_NO
        sb.append("seqNo:SEQ_NO;");
        //�ż�ס�� ADM_TYPE
        sb.append("admType:ADM_TYPE;");
        // ��;�������� WAIT_ORG_CODE
        sb.append("waitOrgCode:WAIT_ORG_CODE;");
        // ��λ
        sb.append("stockUnit:STOCK_UNIT;");
        //��ɾ���
         sb.append("sqlFlg:SQLFLG;");
        setMapString(sb.toString());
    }
    /**
       * ������ɾ���
       * @param sqlFlg boolean
       */
      public void setSqlFlg(boolean sqlFlg) {
          this.sqlFlg = sqlFlg;
      }

      /**
       * �õ���ɾ���
       * @return boolean
       */
      public boolean getSqlFlg() {
          return this.sqlFlg;
    }
    /**
     * �������ʴ��� INV_CODE
     * @param invCode String
     */
    public void setInvCode(String invCode) {
        this.invCode = invCode;
    }

    /**
     * �õ����ʴ��� INV_CODE
     * @return String
     */
    public String getInvCode() {
        return this.invCode;
    }

    /**
     * ����������� INVSEQ_NO
     * @param invseqNo int
     */
    public void setInvseqNo(int invseqNo) {
        this.invseqNo = invseqNo;
    }

    /**
     * �õ�������� INVSEQ_NO
     * @return int
     */
    public int getInvseqNo() {
        return this.invseqNo;
    }

    /**
     * ����������� REGION_CODE
     * @param regionCode String
     */
    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    /**
     * �õ�������� REGION_CODE
     * @return String
     */
    public String getRegionCode() {
        return this.regionCode;
    }
    /**
     * ����������� BATCH_SEQ
     */
    public void setBatchSeq(int batchSeq){
    this.batchSeq=batchSeq;
    }
    /**
     * �õ�������� BATCH_SEQ
     * @return int
     */
    public int setBatchSeq() {
        return this.batchSeq;
    };
    /**
     * ���ò��T���� ORG_CODE
     * @param orgCode String
     */
    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    /**
     * �õ����T���� ORG_CODE
     * @return String
     */
    public String getOrgCode() {
        return this.orgCode;
    }

    /**
     * �������� BATCH_NO
     * @param batChNo String
     */
    public void setBatChNo(String batChNo) {
        this.batChNo = batChNo;
    }

    /**
     * �õ����� BATCH_NO
     * @return String
     */
    public String getBatChNo() {
        return this.batChNo;
    }

    /**
     * ����Ч�� VALID_DATE
     * @param validDate Timestamp
     */
    public void setCalidDate(Timestamp validDate) {
        this.validDate = validDate;
    }

    /**
     * �õ�Ч�� VALID_DATE
     * @return Timestamp
     */
    public Timestamp getValidDate() {
        return this.validDate;
    }

    /**
     * ���ô����� STOCK_QTY
     * @param stockQty double
     */
    public void setStockQty(double stockQty) {
        this.stockQty = stockQty;
    }

    /**
     * �õ������� STOCK_QTY
     * @return double
     */
    public double getStockQty() {
        return this.stockQty;
    }

    /**
     * ���õ��Ʋ���ֵ UNIT_PRICE
     * @param unitPrice double
     */
    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    /**
     * �õ����Ʋ���ֵ UNIT_PRICE
     * @return double
     */
    public double getUnitPrice() {
        return this.unitPrice;
    }

    /**
     * �����̵���ʧע�� CHECKTOLOSE_FLG
     * @param checkToLostFlg String
     */
    public void setCheckToLostFlg(String checkToLostFlg) {
        this.checkToLostFlg = checkToLostFlg;
    }

    /**
     * �õ��̵���ʧע�� CHECKTOLOSE_FLG
     * @return String
     */
    public String getCheckToLostFlg() {
        return this.checkToLostFlg;
    }

    /**
     * �������ע�� WAST_FLG
     * @param wastFlg String
     */
    public void setWastFlg(String wastFlg) {
        this.wastFlg = wastFlg;
    }

    /**
     * �õ����ע�� WAST_FLG
     * @return String
     */
    public String getWastFlg() {
        return this.wastFlg;
    }

    /**
     * ����������� VERIFYIN_DATE
     * @param verifyInDate Timestamp
     */
    public void setVerifyInDate(Timestamp verifyInDate) {
        this.verifyInDate = verifyInDate;
    }

    /**
     * �õ�������� VERIFYIN_DATE
     * @return Timestamp
     */
    public Timestamp getVerifyInDate() {
        return this.verifyInDate;
    }

    /**
     * ���ü��벡���������� OUT_DATE
     * @param outDate Timestamp
     */
    public void setOutDate(Timestamp outDate) {
        this.outDate = outDate;
    }

    /**
     * �õ����벡���������� OUT_DATE
     * @return Timestamp
     */
    public Timestamp getOutDate() {
        return this.outDate;
    }

    /**
     * ���ü��벡��������Ա OUT_USER
     * @param outUser String
     */
    public void setOutUser(String outUser) {
        this.outUser = outUser;
    }

    /**
     * �õ����벡��������Ա OUT_USER
     * @return String
     */
    public String getOutUser() {
        return this.outUser;
    }

    /**
     * ���ò����� MR_NO
     * @param mrNo String
     */
    public void setMrNo(String mrNo) {
        this.mrNo = mrNo;
    }

    /**
     * �õ������� MR_NO
     * @return String
     */
    public String getMrNo() {
        return this.mrNo;
    }

    /**
     * ���þ���� CASE_NO
     * @param caseNo String
     */
    public void setCaseNo(String caseNo) {
        this.caseNo = caseNo;
    }

    /**
     * �õ������ CASE_NO
     * @return String
     */
    public String getCaseNo() {
        return this.caseNo;
    }

    /**
     * ���ô����� RX_SEQ
     * @param rxSeq int
     */
    public void setRxSeq(int rxSeq) {
        this.rxSeq = rxSeq;
    }

    /**
     * �õ������� RX_SEQ
     * @return int
     */
    public int getRxSeq() {
        return this.rxSeq;
    }

    /**
     * ����ҽ����� SEQ_NO
     * @param seqNo int
     */
    public void setSeqNo(int seqNo) {
        this.seqNo = seqNo;
    }

    /**
     * �õ�ҽ����� SEQ_NO
     * @return int
     */
    public int getSeqNo() {
        return this.seqNo;
    }

    /**
     * �����ż�ס�� ADM_TYPE
     * @param admType String
     */
    public void setAdmType(String admType) {
        this.admType = admType;
    }

    /**
     * �õ��ż�ס�� ADM_TYPE
     * @return String
     */
    public String getAdmType() {
        return this.admType;
    }

    /**
     * ������;�������� WAIT_ORG_CODE
     * @param waitOrgCode String
     */
    public void setWaitOrgCode(String waitOrgCode) {
        this.waitOrgCode = waitOrgCode;
    }

    /**
     * �õ���;�������� WAIT_ORG_CODE
     * @return String
     */
    public String getWaitOrgCode() {
        return this.waitOrgCode;
    }
    /**
     * ������;�������� WAIT_ORG_CODE
     * @param waitOrgCode String
     */
    public void setStockUnit(String stockUnit) {
        this.stockUnit = stockUnit;
    }

    /**
     * �õ���;�������� WAIT_ORG_CODE
     * @return String
     */
    public String getStockUnit() {
        return this.stockUnit;
    }


    /**
     * �õ����ز���
     * @return TParm
     */
    public TParm getParm() {
        TParm parm = super.getParm();
        parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        return parm;
    }

    /**
     * ��ʼ����Ź���
     * @param invCode String(���ʴ���)
     * @param invseqNo int(�������)
     * @return INVStockDD
     */
    public INVStockDD initInvStock(String invCode, int invseqNo) {
        //�����β���Ϊ��
        if (invCode == null ||
            invCode.length() == 0 || invseqNo == 0)
            return null;
        TParm parm = new TParm();
        //���Ҵ���
        parm.setData("ORG_CODE", orgCode);
        //���ʴ���
        parm.setData("INV_CODE", invCode);
        INVStockDD invStock = new INVStockDD();

        return invStock;
    }
//    /**
//     * ����parm�õ�һ����Ź������
//     * @param parm TParm
//     * @return INVStockDD
//     */
//    public INVStockDD initInvStock(TParm parm,boolean sqlFlg) {
//        INVStockDD invStockDD = new INVStockDD();
//        //�������ʴ��� INV_CODE
//        invStockDD.setInvCode(parm.getValue("INV_CODE"));
//        // ����������� INVSEQ_NO/
//        invStockDD.setInvseqNo(parm.getInt("INVSEQ_NO"));
//        // ����������� REGION_CODE
//        invStockDD.setRegionCode(parm.getValue("REGION_CODE"));
//        //���ò��T���� ORG_CODE
//        invStockDD.setOrgCode(parm.getValue("ORG_CODE"));
//        // �������� BATCH_NO
//        invStockDD.setBatChNo(parm.getValue("BATCH_NO"));
//        //����Ч�� VALID_DATE
//        invStockDD.setCalidDate(parm.getTimestamp("VALID_DATE"));
//        //���ô����� STOCK_QTY
//        invStockDD.setStockQty(parm.getDouble("STOCK_QTY"));
//        // ���õ��Ʋ���ֵ UNIT_PRICE
//        invStockDD.setUnitPrice(parm.getDouble("UNIT_PRICE"));
//        //�����̵���ʧע�� CHECKTOLOSE_FLG
//        invStockDD.setCheckToLostFlg(parm.getValue("CHECKTOLOSE_FLG"));
//        //�������ע�� WAST_FLG
//        invStockDD.setWastFlg(parm.getValue("WAST_FLG"));
//        //����������� VERIFYIN_DATE
//        invStockDD.setVerifyInDate(parm.getTimestamp("VERIFYIN_DATE"));
//        // ���ü��벡���������� OUT_DATE
//        invStockDD.setOutDate(parm.getTimestamp("OUT_DATE"));
//        // ���ü��벡��������Ա OUT_USER
//        invStockDD.setOutUser(parm.getValue("OUT_USER"));
//        //���ò����� MR_NO
//        invStockDD.setMrNo(parm.getValue("MR_NO"));
//        // ���þ���� CASE_NO
//        invStockDD.setCaseNo(parm.getValue("CASE_NO"));
//        //���ô����� RX_SEQ
//        invStockDD.setRxSeq(parm.getInt("RX_SEQ"));
//        // ����ҽ����� SEQ_NO
//        invStockDD.setSeqNo(parm.getInt("SEQ_NO"));
//        //�����ż�ס�� ADM_TYPE
//        invStockDD.setAdmType(parm.getValue("ADM_TYPE"));
//        // ��;�������� WAIT_ORG_CODE
//        invStockDD.setWaitOrgCode(parm.getValue("WAIT_ORG_CODE"));
//        //��ɾ���
//        invStockDD.setSqlFlg(sqlFlg);
//        return invStockDD;
//    }
    /**
        * ����parm�õ�һ����Ź������
        * @param parm TParm
        * @return INVStockDD
        */
       public INVStockDD initInvStock(TParm parm,boolean sqlFlg) {
           INVStockDD invStockDD = new INVStockDD();
           //�������ʴ��� INV_CODE
           invStockDD.setInvCode(parm.getValue("INV_CODE"));
           // ����������� INVSEQ_NO/
           invStockDD.setInvseqNo(parm.getInt("DDSEQ_NO"));
           // ����������� REGION_CODE
           invStockDD.setRegionCode(parm.getValue("REGION_CODE"));
           //�������
           invStockDD.setBatchSeq(parm.getInt("BATCH_SEQ"));
           //���ò��T���� ORG_CODE
           invStockDD.setOrgCode(parm.getValue("ORG_CODE"));
           // �������� BATCH_NO
           invStockDD.setBatChNo(parm.getValue("BATCH_NO"));
           //����Ч�� VALID_DATE
           invStockDD.setCalidDate(parm.getTimestamp("VALID_DATE"));
           //����������� VERIFYIN_DATE
           invStockDD.setVerifyInDate(parm.getTimestamp("VERIFYIN_DATE"));
           //����������� VERIFYIN_DATE
           invStockDD.setStockUnit(parm.getValue("STOCK_UNIT"));
           invStockDD.setUnitPrice(parm.getDouble("UNIT_PRICE"));
           //��ɾ���
           invStockDD.setSqlFlg(sqlFlg);
           return invStockDD;
    }
}
