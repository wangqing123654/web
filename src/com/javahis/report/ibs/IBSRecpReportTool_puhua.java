package com.javahis.report.ibs;

import java.text.DecimalFormat;
import jdo.bil.BILPrintTool;
import com.dongyang.data.TParm;
import com.javahis.util.IReport;
/**
 * <p> Title: סԺ�վ�����׼���� </p>
 * 
 * <p> Description: סԺ�վ�����׼����  </p>
 * 
 * <p> Copyright: Bluecore 20130730 </p>
 * 
 * <p> Company: Bluecore </p>
 * 
 * @author wanglong
 * @version 1.0
 */
public class IBSRecpReportTool_puhua implements IReport {

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
     * @param printParm
     */
    private TParm setUncertainItems(TParm printParm) {//add by wanglong 20121217
        int num = 1;
        DecimalFormat df = new DecimalFormat("###########0.00");
        TParm descParm = BILPrintTool.getInstance().getChargeDesc("I");
        if (printParm.existGroup("CHARGE01")&&(printParm.getDouble("CHARGE01", "TEXT")!=0)) {// ��λ��
            printParm.setData("ITEM"+num, "TEXT", descParm.getValue("CHARGE01"));
            printParm.setData("FEE"+(num++), "TEXT", df.format(printParm.getDouble("CHARGE01", "TEXT")));
        }
        if (printParm.existGroup("CHARGE02")&&(printParm.getDouble("CHARGE02", "TEXT")!=0)) {// ����
            printParm.setData("ITEM"+num, "TEXT", descParm.getValue("CHARGE02"));
            printParm.setData("FEE"+(num++), "TEXT", df.format(printParm.getDouble("CHARGE02", "TEXT")));
        }
        if (printParm.existGroup("CHARGE03")&&(printParm.getDouble("CHARGE03", "TEXT")!=0)) {// ��ҩ��
            printParm.setData("ITEM"+num, "TEXT", "��ҩ��");
            printParm.setData("FEE"+(num++), "TEXT", df.format(printParm.getDouble("CHARGE03", "TEXT")));
        }
        if (printParm.existGroup("CHARGE05")&&(printParm.getDouble("CHARGE05", "TEXT")!=0)) {// �г�ҩ��
            printParm.setData("ITEM"+num, "TEXT", descParm.getValue("CHARGE05"));
            printParm.setData("FEE"+(num++), "TEXT", df.format(printParm.getDouble("CHARGE05", "TEXT")));
        }
        if (printParm.existGroup("CHARGE06")&&(printParm.getDouble("CHARGE06", "TEXT")!=0)) {// �в�ҩ�ѡ�����/������ҩ��(ס)
            printParm.setData("ITEM"+num, "TEXT", descParm.getValue("CHARGE06"));
            printParm.setData("FEE"+(num++), "TEXT", df.format(printParm.getDouble("CHARGE06", "TEXT")));
        }
        if (printParm.existGroup("CHARGE07")&&(printParm.getDouble("CHARGE07", "TEXT")!=0)) {// ����
            printParm.setData("ITEM"+num, "TEXT", descParm.getValue("CHARGE07"));
            printParm.setData("FEE"+(num++), "TEXT", df.format(printParm.getDouble("CHARGE07", "TEXT")));
        }
        if (printParm.existGroup("CHARGE08")&&(printParm.getDouble("CHARGE08", "TEXT")!=0)) {// ���Ʒ�
            printParm.setData("ITEM"+num, "TEXT", descParm.getValue("CHARGE08"));
            printParm.setData("FEE"+(num++), "TEXT", df.format(printParm.getDouble("CHARGE08", "TEXT")));
        }
        if (printParm.existGroup("CHARGE09")&&(printParm.getDouble("CHARGE09", "TEXT")!=0)) {// �����
            printParm.setData("ITEM"+num, "TEXT", descParm.getValue("CHARGE09"));
            printParm.setData("FEE"+(num++), "TEXT", df.format(printParm.getDouble("CHARGE09", "TEXT")));
        }
        if (printParm.existGroup("CHARGE10")&&(printParm.getDouble("CHARGE10", "TEXT")!=0)) {// ������
            printParm.setData("ITEM"+num, "TEXT", descParm.getValue("CHARGE10"));
            printParm.setData("FEE"+(num++), "TEXT", df.format(printParm.getDouble("CHARGE10", "TEXT")));
        }
        if (printParm.existGroup("CHARGE11")&&(printParm.getDouble("CHARGE11", "TEXT")!=0)) {// �����
            printParm.setData("ITEM"+num, "TEXT", descParm.getValue("CHARGE11"));
            printParm.setData("FEE"+(num++), "TEXT", df.format(printParm.getDouble("CHARGE11", "TEXT")));
        }
        if (printParm.existGroup("CHARGE12")&&(printParm.getDouble("CHARGE12", "TEXT")!=0)) {// ��Ѫ��
            printParm.setData("ITEM"+num, "TEXT", descParm.getValue("CHARGE12"));
            printParm.setData("FEE"+(num++), "TEXT", df.format(printParm.getDouble("CHARGE12", "TEXT")));
        }
        if (printParm.existGroup("CHARGE13")&&(printParm.getDouble("CHARGE13", "TEXT")!=0)) {// �����ѡ�ICU/CCUҩ��(ס)
            printParm.setData("ITEM"+num, "TEXT", descParm.getValue("CHARGE13"));
            printParm.setData("FEE"+(num++), "TEXT", df.format(printParm.getDouble("CHARGE13", "TEXT")));
        }
        if (printParm.existGroup("CHARGE14")&&(printParm.getDouble("CHARGE14", "TEXT")!=0)) {// �����ѡ�����/�����Ҳ��Ϸ�(ס)
            printParm.setData("ITEM"+num, "TEXT", descParm.getValue("CHARGE14"));
            printParm.setData("FEE"+(num++), "TEXT", df.format(printParm.getDouble("CHARGE14", "TEXT")));
        }
        if (printParm.existGroup("CHARGE15")&&(printParm.getDouble("CHARGE15", "TEXT")!=0)) {// �����
            printParm.setData("ITEM"+num, "TEXT", descParm.getValue("CHARGE15"));
            printParm.setData("FEE"+(num++), "TEXT", df.format(printParm.getDouble("CHARGE15", "TEXT")));
        }
        if (printParm.existGroup("CHARGE16")&&(printParm.getDouble("CHARGE16", "TEXT")!=0)) {// �Ҵ��ѡ�ICU/CCU���Ϸ�(ס)
            printParm.setData("ITEM"+num, "TEXT", descParm.getValue("CHARGE16"));
            printParm.setData("FEE"+(num++), "TEXT", df.format(printParm.getDouble("CHARGE16", "TEXT")));
        }
        if (printParm.existGroup("CHARGE17")&&(printParm.getDouble("CHARGE17", "TEXT")!=0)) {// CT��
            printParm.setData("ITEM"+num, "TEXT", descParm.getValue("CHARGE17"));
            printParm.setData("FEE"+(num++), "TEXT", df.format(printParm.getDouble("CHARGE17", "TEXT")));
        }
        if (printParm.existGroup("CHARGE18")&&(printParm.getDouble("CHARGE18", "TEXT")!=0)) {// MR��
            printParm.setData("ITEM"+num, "TEXT", descParm.getValue("CHARGE18"));
            printParm.setData("FEE"+(num++), "TEXT", df.format(printParm.getDouble("CHARGE18", "TEXT")));
        }
        if (printParm.existGroup("CHARGE20")&&(printParm.getDouble("CHARGE20", "TEXT")!=0)) {// ���Ϸ�
            printParm.setData("ITEM"+num, "TEXT", descParm.getValue("CHARGE20"));
            printParm.setData("FEE"+(num++), "TEXT", df.format(printParm.getDouble("CHARGE20", "TEXT")));
        }
        if (printParm.existGroup("CHARGE19")&&(printParm.getDouble("CHARGE19", "TEXT")!=0)) {// �ԷѲ���
            printParm.setData("ITEM"+num, "TEXT", descParm.getValue("CHARGE19"));
            printParm.setData("FEE"+(num++), "TEXT", df.format(printParm.getDouble("CHARGE19", "TEXT")));
        }
        return printParm;
    }
    
}
