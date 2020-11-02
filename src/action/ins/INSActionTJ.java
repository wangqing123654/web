package action.ins;

import jdo.sys.SystemTool;

import com.dongyang.data.TParm;

/**
 * <p>Title: ҽ��������(���)</p>
 *
 * <p>Description: ҽ��������(���)</p>
 *
 * <p>Copyright: Copyright (c) BlueCore 2011</p>
 *
 * <p>Company: BlueCore</p>
 *
 * @author wangl 2011.12.07
 * @version 1.0
 */
public class INSActionTJ
    extends INSInterface {
    public INSActionTJ() {
    }

    public TParm regFeePartition(Object parm) {
        log("regFeePartition", "start input object");
        TParm result = new TParm();
        //��˲����Ƿ�Ϊ��
        if (parm == null) {
            result.setErr( -1,
                          "action.ins.InsAction.regFeePartition->Err:����Ϊ��");
            return result;
        }
        //��˲��������Ƿ���ȷ
        if (! (parm instanceof TParm)) {
            result.setErr( -1,
                          "action.ins.InsAction.regFeePartition->Err:�������ʹ���");
            return result;
        }
        TParm acParm = (TParm) parm;
        return regFeePartition(acParm);
    }

    public TParm uploadDetail(Object parm) {
        log("uploadDetail", "start input object");
        TParm result = new TParm();
        //��˲����Ƿ�Ϊ��
        if (parm == null) {
            result.setErr( -1,
                          "action.ins.InsAction.uploadDetail->Err:����Ϊ��");
            return result;
        }
        //��˲��������Ƿ���ȷ
        if (! (parm instanceof TParm)) {
            result.setErr( -1,
                          "action.ins.InsAction.uploadDetail->Err:�������ʹ���");
            return result;
        }
        TParm acParm = (TParm) parm;
        return uploadDetail(acParm);
    }

    public TParm createREGDetil(TParm parm) {
        TParm result = new TParm();
        //ȡ�Һ�ҽ����ͷ���
        parm.setData("ACTION_ID", "INS_GET_NHI_CODE");
//    TParm nhiCodeParm = InsUpdateEngine.getInstance().doAction(parm);
        TParm nhiCodeParm = new TParm();
        if (nhiCodeParm.getErrCode() != 0) {
            result.setErr( -1, "ȡ�Һ�ҽ����ͷ���->" + nhiCodeParm.getErrName());
            return result;
        }

        int count = nhiCodeParm.getCount("NHI_ORDER_CODE");
        for (int row = 0; row < count; row++) {
            TParm inData = new TParm();
            //�ӹҺ�ҽ����ͷ���
            inData.setRowData(row, nhiCodeParm);
            //�Ӵ������
            inData.setRowData(parm);
            //�����
            inData.setData("SEQ_NO", row);
            //������ϸ
            inData.setData("ACTION_ID", "INS_INS_DETAIL");
//      result = InsUpdateEngine.getInstance().doAction(inData);
            result = new TParm();
            if (result.getErrCode() != 0) {
                return result;
            }
        }
        return result;
    }

    /**
     * ���÷ָ�
     * @param parm TParm
     * @return TParm
     */
    public TParm regFeePartition(TParm parm) {
        log("regFeePartition", "start input TParm");
        TParm result = new TParm();

        //ȡ�Һ�ҽ����ͷ���
        parm.setData("ACTION_ID", "INS_GET_NHI_CODE");
//    TParm nhiCodeParm = InsUpdateEngine.getInstance().doAction(parm);
        TParm nhiCodeParm = new TParm();
        if (nhiCodeParm.getErrCode() != 0) {
            result.setErr( -1, "ȡ�Һ�ҽ����ͷ���->" + nhiCodeParm.getErrName());
            return result;
        }
        log("regFeePartition", "INS_GET_NHI_CODE ok");

        //��ߴ�λ��
        parm.setData("ACTION_ID", "INS_GETTIP_BEDPRICE");
//    TParm tipBedpriceParm = InsUpdateEngine.getInstance().doAction(parm);
        TParm tipBedpriceParm = new TParm();
        if (tipBedpriceParm.getErrCode() != 0) {
            result.setErr( -1, "ȡ��ߴ�λ��ʧ��->" + tipBedpriceParm.getErrName());
            return result;
        }
        double tipBedprice = tipBedpriceParm.getDouble("TIP_BEDPRICE", 0);
        log("regFeePartition", "INS_GETTIP_BEDPRICE ok");

        //ѭ���ָ�
        for (int i = 0; i < nhiCodeParm.getCount("ORDER_CODE"); i++) {
            log("regFeePartition", "for �ָ� " + i);
            TParm inparm = new TParm();
            inparm.setData("PIPELINE", "DataDown_sp1");
            inparm.setData("PLOT_TYPE", "B");
            inparm.setData("HOSP_AREA", parm.getValue("HOSP_AREA"));
            //�շ���Ŀ����
            inparm.setData("NHI_ORDER_CODE", 0,
                           nhiCodeParm.getData("ORDER_CODE", i));
            //��Ա���
            inparm.setData("CTZ1_CODE", 0, parm.getValue("CTZ1_CODE"));
            //����
            inparm.setData("QTY", 0, 1);
            //�������
            inparm.setData("TOTAL_AMT", 0, nhiCodeParm.getData("TOTAL_AMT", i));
            //��ߴ�λ��
            inparm.setData("TIPTOP_BED_AMT", 0, tipBedprice);
            //����ҩƷ��־
            inparm.setData("PHAADD_FLG", 0, "0");
            //ȫ�Էѱ�־
            inparm.setData("FULL_OWN_FLG", 0, "0");
            //ҽԺ����
            inparm.setData("HOSP_NHI_NO", 0, parm.getValue("HOSP_NHI_NO"));
            //סԺ��ʼʱ��
            inparm.setData("HOSP_START_DATE", 0,
                           SystemTool.getInstance().getDate());
            TParm returnResult = safe(inparm);
            if (returnResult.getErrCode() != 0)
                return returnResult;
            //�շ���Ŀ����
            result.addData("NHI_ORDER_CODE",
                           nhiCodeParm.getData("ORDER_CODE", i));
            //��Ա���
            result.addData("CTZ1_CODE", parm.getValue("CTZ1_CODE"));
            //����
            result.addData("QTY", 1);
            //�������
            result.addData("TOTAL_AMT", nhiCodeParm.getData("TOTAL_AMT", i));
            //��ߴ�λ��
            result.addData("TIPTOP_BED_AMT", tipBedprice);
            //ҽԺ����
            result.addData("HOSP_NHI_NO", parm.getValue("HOSP_NHI_NO"));
            //����ִ��״̬
            result.addData("PROGRAM_STATE",
                           returnResult.getData("PROGRAM_STATE", 0));
            //����ִ����Ϣ
            result.addData("PROGRAM_MESSAGE",
                           returnResult.getData("PROGRAM_MESSAGE", 0));
            //ȫ�Էѽ��
            result.addData("OWN_AMT", returnResult.getData("OWN_AMT", 0));
            //�������
            result.addData("ADDPAY_AMT", returnResult.getData("ADDPAY_AMT", 0));
            //�걨���
            result.addData("NHI_AMT", returnResult.getData("NHI_AMT", 0));
            //�Ը�����
            result.addData("OWN_RATE", returnResult.getData("OWN_RATE", 0));
            //�ۼ�������־
            result.addData("ADDPAY_FLG", returnResult.getData("ADDPAY_FLG", 0));
            //ͳ�ƴ���
            result.addData("NHI_ORD_CLASS_CODE",
                           returnResult.getData("NHI_ORD_CLASS_CODE", 0));
            //�ż�ס��
            result.addData("ADM_TYPE", parm.getValue("ADM_TYPE"));
            //�ű�
            result.addData("CLINIC_TYPE_CODE", parm.getValue("CLINIC_TYPE_CODE"));
        }
        return result;
    }

    /**
     * ������ϸ�ϴ�
     * @param parm TParm HOSP_AREA,CASE_NO,SEQ_NO,HOSP_NHI_NO
     * @return TParm
     */
    public TParm uploadDetail(TParm parm) {
        log("uploadDetail", "start input TParm");
        TParm result = new TParm();
        //ȡ������ϸ
        parm.setData("ACTION_ID", "INS_GET_UPLOAD_DETAIL");
//    TParm nhiCodeParm = InsUpdateEngine.getInstance().doAction(parm);
        TParm nhiCodeParm = new TParm();
        if (nhiCodeParm.getErrCode() != 0) {
            result.setErr( -1, "ȡ������ϸ->" + nhiCodeParm.getErrName());
            return result;
        }
        log("uploadDetail", "INS_GET_UPLOAD_DETAIL ok");
        //ȡҽԺ����
        result.setData("HOSP_NHI_NO", parm.getValue("HOSP_NHI_NO"));
        //����ҽ���ӿ�
        result.setData("PIPELINE", "DataUpload");
        result.setData("PLOT_TYPE", "B");
        return safe(result);
    }

    public static void main(String args[]) {
        INSActionTJ driver = new INSActionTJ();
        TParm parm = new TParm();
        //-----���ԹҺŷ��÷ָ�----//
        parm.setData("HOSP_AREA", "HIS");
        parm.setData("REGION_CODE", "101");
        //��Ա���
        parm.setData("CTZ1_CODE", "51");
        //�ż�ס��
        parm.setData("ADM_TYPE", "O");
        //�ű�
        parm.setData("CLINICTYPE_CODE", "03");

        parm.setData("CASE_NO", "001");
        parm.setData("OPT_USER", "tiis");
        parm.setData("OPT_TERM", "1.1.1.2");
        //ҽԺ����
        //parm.setCommitData("HOSP_NHI_NO","000551");
        //���÷ָ�
        //System.out.println(driver.regFeePartition(parm));

        //System.out.println(driver.createREGDetil(parm));
    }
}
