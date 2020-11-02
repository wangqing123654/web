package com.javahis.ui.testOpb.bean;

import java.sql.Timestamp;

import com.javahis.ui.testOpb.annotation.Column;
import com.javahis.ui.testOpb.annotation.PKey;
import com.javahis.ui.testOpb.annotation.Table;
import com.javahis.ui.testOpb.tools.Type;

@Table(tableName = "OPD_ORDER_RETURN")
public class OpdOrderReturn extends BasePOJO{
	@PKey(name = "SEQ_NO", type = Type.NUM)
    public java.math.BigDecimal seqNo = null;

    @PKey(name = "RX_NO", type = Type.CHAR)
    public java.lang.String rxNo = null;

    @PKey(name = "CASE_NO", type = Type.CHAR)
    public java.lang.String caseNo = null;
    
    @PKey(name = "DC_ORDER_DATE", type = Type.CHAR)
    public java.lang.String dcOrderDate = null;

    @Column(name = "ORDER_DESC", type = Type.CHAR)
    public java.lang.String orderDesc = null;

    @Column(name = "ORDER_CODE", type = Type.CHAR)
    public java.lang.String orderCode = null;

    @Column(name = "OPT_USER", type = Type.CHAR)
    public java.lang.String optUser = null;

    @Column(name = "OPT_TERM", type = Type.CHAR)
    public java.lang.String optTerm = null;

    @Column(name = "OPT_DATE", type = Type.DATE)
    public java.lang.String optDate = null;

    @Column(name = "MR_NO", type = Type.CHAR)
    public java.lang.String mrNo = null;

    @Column(name = "DOSAGE_QTY", type = Type.NUM)
    public java.math.BigDecimal dosageQty = null;

    @Column(name = "SUB_QTY", type = Type.NUM)
    public java.math.BigDecimal subQty = null;
    
    @Column(name = "OLD_DOSAGE_QTY", type = Type.NUM)
    public java.math.BigDecimal oldDosageQty = null;
    
    @Column(name = "ORDERSET_CODE", type = Type.CHAR)
    public java.lang.String ordersetCode = null;
    
    @Column(name = "SETMAIN_FLG", type = Type.CHAR)
    public java.lang.String setmainFlg = null;
    
    @Column(name = "EXEC_DEPT_CODE", type = Type.CHAR)
    public java.lang.String execDeptCode = null;
    
}
