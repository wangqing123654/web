package com.javahis.report.opb;

import java.text.DecimalFormat;
import jdo.bil.BILPrintTool;
import com.dongyang.data.TParm;
import com.javahis.util.IReport;
/**
 * <p> Title: �����վ�����׼���� </p>
 * 
 * <p> Description: �����վ�����׼����  </p>
 * 
 * <p> Copyright: Bluecore 20130730 </p>
 * 
 * <p> Company: Bluecore </p>
 * 
 * @author wanglong
 * @version 1.0
 */
public class OPBRECTPrintReportTool_puhua implements IReport {

    /**
     * ׼��������
     * @param parm
     * @return
     */
    public TParm getReportParm(TParm parm) {
        parm = setUncertainItems(parm);
        return parm;
    }
    
    /**
     * ��������������ӵ�����ӡƱ�ݵ�TParm��
     * @param oneReceiptParm
     */
    private TParm setUncertainItems(TParm oneReceiptParm) {//add by wanglong 20121217
        int num=1;
        DecimalFormat df = new DecimalFormat("###########0.00");
        TParm descParm = BILPrintTool.getInstance().getChargeDesc("O");
        if (oneReceiptParm.existGroup("CHARGE19")&&(oneReceiptParm.getDouble("CHARGE19", "TEXT")!=0)) {// ���� add by wanglong 20130228
            oneReceiptParm.setData("ITEM"+num, "TEXT", descParm.getValue("CHARGE19"));
            oneReceiptParm.setData("FEE"+(num++), "TEXT", df.format(oneReceiptParm.getDouble("CHARGE19", "TEXT")));
        }
        if (oneReceiptParm.existGroup("CHARGE01")&&(oneReceiptParm.getDouble("CHARGE01", "TEXT")!=0)) {// ��ҩ��
            oneReceiptParm.setData("ITEM"+num, "TEXT", "��ҩ��");
            oneReceiptParm.setData("FEE"+(num++), "TEXT", df.format(oneReceiptParm.getDouble("CHARGE01", "TEXT")));
        }
        if (oneReceiptParm.existGroup("CHARGE03")&&(oneReceiptParm.getDouble("CHARGE03", "TEXT")!=0)) {// �г�ҩ��
            oneReceiptParm.setData("ITEM"+num, "TEXT", descParm.getValue("CHARGE03"));
            oneReceiptParm.setData("FEE"+(num++), "TEXT", df.format(oneReceiptParm.getDouble("CHARGE03", "TEXT")));
        }
        if (oneReceiptParm.existGroup("CHARGE04")&&(oneReceiptParm.getDouble("CHARGE04", "TEXT")!=0)) {// �в�ҩ��
            oneReceiptParm.setData("ITEM"+num, "TEXT", descParm.getValue("CHARGE04"));
            oneReceiptParm.setData("FEE"+(num++), "TEXT", df.format(oneReceiptParm.getDouble("CHARGE04", "TEXT")));
        }
        if (oneReceiptParm.existGroup("CHARGE05")&&(oneReceiptParm.getDouble("CHARGE05", "TEXT")!=0)) {// ����
            oneReceiptParm.setData("ITEM"+num, "TEXT", descParm.getValue("CHARGE05"));
            oneReceiptParm.setData("FEE"+(num++), "TEXT", df.format(oneReceiptParm.getDouble("CHARGE05", "TEXT")));
        }
        if (oneReceiptParm.existGroup("CHARGE10")&&(oneReceiptParm.getDouble("CHARGE10", "TEXT")!=0)) {// �����
            oneReceiptParm.setData("ITEM"+num, "TEXT", descParm.getValue("CHARGE10"));
            oneReceiptParm.setData("FEE"+(num++), "TEXT", df.format(oneReceiptParm.getDouble("CHARGE10", "TEXT")));
        }
        if (oneReceiptParm.existGroup("CHARGE06")&&(oneReceiptParm.getDouble("CHARGE06", "TEXT")!=0)) {// ���Ʒ�
            oneReceiptParm.setData("ITEM"+num, "TEXT", descParm.getValue("CHARGE06"));
            oneReceiptParm.setData("FEE"+(num++), "TEXT", df.format(oneReceiptParm.getDouble("CHARGE06", "TEXT")));
        }
        if (oneReceiptParm.existGroup("CHARGE07")&&(oneReceiptParm.getDouble("CHARGE07", "TEXT")!=0)) {// �����
            oneReceiptParm.setData("ITEM"+num, "TEXT", descParm.getValue("CHARGE07"));
            oneReceiptParm.setData("FEE"+(num++), "TEXT", df.format(oneReceiptParm.getDouble("CHARGE07", "TEXT")));
        }
        if (oneReceiptParm.existGroup("CHARGE08")&&(oneReceiptParm.getDouble("CHARGE08", "TEXT")!=0)) {// ������
            oneReceiptParm.setData("ITEM"+num, "TEXT", descParm.getValue("CHARGE08"));
            oneReceiptParm.setData("FEE"+(num++), "TEXT", df.format(oneReceiptParm.getDouble("CHARGE08", "TEXT")));
        }
        if (oneReceiptParm.existGroup("CHARGE09")&&(oneReceiptParm.getDouble("CHARGE09", "TEXT")!=0)) {// ��Ѫ��
            oneReceiptParm.setData("ITEM"+num, "TEXT", descParm.getValue("CHARGE09"));
            oneReceiptParm.setData("FEE"+(num++), "TEXT", df.format(oneReceiptParm.getDouble("CHARGE09", "TEXT")));
        }
        if (oneReceiptParm.existGroup("CHARGE18")&&(oneReceiptParm.getDouble("CHARGE18", "TEXT")!=0)) {// ������
            oneReceiptParm.setData("ITEM"+num, "TEXT", descParm.getValue("CHARGE18"));
            oneReceiptParm.setData("FEE"+(num++), "TEXT", df.format(oneReceiptParm.getDouble("CHARGE18", "TEXT")));
        }
        if (oneReceiptParm.existGroup("CHARGE13")&&(oneReceiptParm.getDouble("CHARGE13", "TEXT")!=0)) {// �۲촲��
            oneReceiptParm.setData("ITEM"+num, "TEXT", descParm.getValue("CHARGE13"));
            oneReceiptParm.setData("FEE"+(num++), "TEXT", df.format(oneReceiptParm.getDouble("CHARGE13", "TEXT")));
        }
        if (oneReceiptParm.existGroup("CHARGE14")&&(oneReceiptParm.getDouble("CHARGE14", "TEXT")!=0)) {// CT��
            oneReceiptParm.setData("ITEM"+num, "TEXT", descParm.getValue("CHARGE14"));
            oneReceiptParm.setData("FEE"+(num++), "TEXT", df.format(oneReceiptParm.getDouble("CHARGE14", "TEXT")));
        }
        if (oneReceiptParm.existGroup("CHARGE15")&&(oneReceiptParm.getDouble("CHARGE15", "TEXT")!=0)) {// MR��
            oneReceiptParm.setData("ITEM"+num, "TEXT", descParm.getValue("CHARGE15"));
            oneReceiptParm.setData("FEE"+(num++), "TEXT", df.format(oneReceiptParm.getDouble("CHARGE15", "TEXT")));
        }
        if (oneReceiptParm.existGroup("CHARGE17")&&(oneReceiptParm.getDouble("CHARGE17", "TEXT")!=0)) {// ���Ϸ�
            oneReceiptParm.setData("ITEM"+num, "TEXT", descParm.getValue("CHARGE17"));
            oneReceiptParm.setData("FEE"+(num++), "TEXT", df.format(oneReceiptParm.getDouble("CHARGE17", "TEXT")));
        }
        if (oneReceiptParm.existGroup("CHARGE16")&&(oneReceiptParm.getDouble("CHARGE16", "TEXT")!=0)) {// �ԷѲ���
            oneReceiptParm.setData("ITEM"+num, "TEXT", descParm.getValue("CHARGE16"));
            oneReceiptParm.setData("FEE"+(num++), "TEXT", df.format(oneReceiptParm.getDouble("CHARGE16", "TEXT")));
        }
        double partAMt = 0;
        partAMt += oneReceiptParm.getDouble("FEE1", "TEXT");
        partAMt += oneReceiptParm.getDouble("FEE2", "TEXT");
        partAMt += oneReceiptParm.getDouble("FEE3", "TEXT");
        partAMt += oneReceiptParm.getDouble("FEE4", "TEXT");
        partAMt += oneReceiptParm.getDouble("FEE5", "TEXT");
        partAMt += oneReceiptParm.getDouble("FEE6", "TEXT");
        partAMt += oneReceiptParm.getDouble("FEE7", "TEXT");
        partAMt += oneReceiptParm.getDouble("FEE8", "TEXT");
        oneReceiptParm.setData("PART_AMT1", "TEXT", partAMt);//С��1
        if (num > 9) {
            partAMt = 0;
            partAMt += oneReceiptParm.getDouble("FEE9", "TEXT");
            partAMt += oneReceiptParm.getDouble("FEE10", "TEXT");
            partAMt += oneReceiptParm.getDouble("FEE11", "TEXT");
            partAMt += oneReceiptParm.getDouble("FEE12", "TEXT");
            partAMt += oneReceiptParm.getDouble("FEE13", "TEXT");
            partAMt += oneReceiptParm.getDouble("FEE14", "TEXT");
            partAMt += oneReceiptParm.getDouble("FEE15", "TEXT");
            partAMt += oneReceiptParm.getDouble("FEE16", "TEXT");
            oneReceiptParm.setData("PART_AMT2", "TEXT", partAMt);//С��2
        }
        return oneReceiptParm;
    }

}
