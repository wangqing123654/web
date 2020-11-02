package com.javahis.ui.clp;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.*;
import jdo.clp.*;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class CLPSingleDiseInfoControl extends TControl {

    /**
     * �����ϵ�cUI����
     */
    private TTextFormat START_DATE;
    private TTextFormat END_DATE;

    //ҽ�����
    private TRadioButton All;
    private TRadioButton DEPT;

    //����
    private TTextFormat DEPT_CODE;
    private TComboBox CLASSIFY;
    /**
     * ��ʼ������
     */
    public void onInit() {
        super.onInit();
        //������ĳ�ʼ��
        myInitControler();
    }


    /**
     * ���ȵõ�����UI�Ŀؼ�����/ע����Ӧ���¼�
     */

    public void myInitControler() {
        //�õ�ʱ��ؼ�
        END_DATE = (TTextFormat)this.getComponent("END_DATE");
        START_DATE = (TTextFormat)this.getComponent("START_DATE");
        DEPT_CODE = (TTextFormat)this.getComponent("DEPT_CODE");
        CLASSIFY = (TComboBox)this.getComponent("CLASSIFY");
        //�õ���ѯ����UI�Ķ���
        All = (TRadioButton)this.getComponent("All");
        DEPT = (TRadioButton)this.getComponent("DEPT");

        //��ʼ��ʱ��
        initDateTime();

    }

    /**
     * ��ʼ��ʱ��ؼ�
     */
    public void initDateTime() {
        TParm parm = new TParm(TJDODBTool.getInstance().select(
                "SELECT current date as DATE FROM sysibm.sysdummy1"));
//        this.messageBox_(parm);
        START_DATE.setValue(parm.getData("DATE", 0));
        END_DATE.setValue(parm.getData("DATE", 0));

    }

    public TParm getParm() {

        TParm result = new TParm();
        result.setData("START_DATE", START_DATE.getValue());
        result.setData("END_DATE", END_DATE.getValue());

        if (All.isSelected()) {
            //
        } else {
            result.setData("DEPT_CODE", DEPT_CODE.getValue());
        }

        result.setData("CLASSIFY", CLASSIFY.getValue());
        return result;
    }


    public CLPSingleDiseInfoControl() {

    }

    public void onSave() {
        String lab = CLASSIFY.getValue();
        TParm parm = new TParm();
        //����EXCLE
        if ("ORAL.CLP_AMI".equals(lab)) {
//            this.messageBox("�����ļ�����");
            ClpAmiTool ClpAmiTool = new ClpAmiTool();
            parm.addData("A", "AMI-1����ҽԺ�󼴿�ʹ�ð�˾ƥ�֣��н�����Ӧ�����������ף�");
            parm.addData("A", "AMI-2ʵʩ�����ҹ�������");
            parm.addData("A", "AMI-3�ٹ�ע���ƣ���������STEMI��");
            parm.addData("A", "ʵʩ��˨����");
            parm.addData("A", "ʵʩPCI����");
            parm.addData("A", "��Ҫ����PCIתԺ");
            parm.addData("A", "����ҽԺ�󼴿�ʹ�æ����ͼ����޽���֢��");
            parm.addData("A", "AMI-5סԺ�ڼ�ʹ�ð�˾ƥ�֡������ͼ���ACEI/ARB����͡��ҩ������ʾ���޽���֢�ߣ�");
            parm.addData("A", "��Ժʱ����ʹ�ð�˾ƥ�֡������ͼ���ACEI/ARB����͡��ҩ������ʾ���޽���֢�ߣ�");
            parm.addData("A", "AMI-7Ϊ�����ṩ��������");
            parm.addData("A", "AMI-8���ܶ�֬���׵��̴�������");

            parm.addData("B", ClpAmiTool.getAmi1ERate());
            parm.addData("B", ClpAmiTool.getAmi2Rate());
            parm.addData("B", ClpAmiTool.getAmi3Rate());
            parm.addData("B", ClpAmiTool.getAmi3_1Rate());
            parm.addData("B", ClpAmiTool.getAmi3_2Rate());
            parm.addData("B", ClpAmiTool.getAmi3_3Rate());
            parm.addData("B", ClpAmiTool.getAmi4Rate());
            parm.addData("B", ClpAmiTool.getAmi5Rate());
            parm.addData("B", ClpAmiTool.getAmi6Rate());
            parm.addData("B", ClpAmiTool.getAmi7Rate());
            parm.addData("B", ClpAmiTool.getAmi8Rate());

            parm.setCount(11);

            ClpExportExcleTool.getInstance().exeSaveExcel("AMI,500;����,100",
                    parm, "�ļ�");
        } else if ("ORAL.CLP_CABG".equals(lab)) {
//            this.messageBox("��״������·��ֲ��");
            ClpCABGTool ClpCABGTool = new ClpCABGTool();
            parm.addData("A", "1.����ҽԺ�󼴿��ڿ�����");
            parm.addData("A", "2.������Ӧ��������");
            parm.addData("A", "3.����ѡ��LIMA����");
            parm.addData("A", "4.1 ����ǰһСʱ��ʼʹ��");
            parm.addData("A", "4.2 ����ʱ�䳬��3Сʱ��׷��һ��");
            parm.addData("A", "4.3 ����120h֮�ڽ���ʹ��");
            parm.addData("A", "5.�����޻�Գ�Ѫ��Ѫ��������");
            parm.addData("A", "6.�������޲���֢");
            parm.addData("A", "7.�п����ϣ����");
            parm.addData("A", "8.סԺ21���ڳ�Ժ");

            parm.addData("B", ClpCABGTool.getCABG1Rate());
            parm.addData("B", ClpCABGTool.getCABG2_2Rate());
            parm.addData("B", ClpCABGTool.getCABG3_1Rate());
            parm.addData("B", ClpCABGTool.getCABG4_1Rate());
            parm.addData("B", ClpCABGTool.getCABG4_2Rate());
            parm.addData("B", ClpCABGTool.getCABG4_3Rate());
            parm.addData("B", ClpCABGTool.getCABG5Rate());
            parm.addData("B", ClpCABGTool.getCABG6Rate());
            parm.addData("B", ClpCABGTool.getCABG7Rate());
            parm.addData("B", ClpCABGTool.getCABG9Rate());

            parm.setCount(10);

            ClpExportExcleTool.getInstance().exeSaveExcel("CABG,500;����,100",
                    parm, "�ļ�");

        } else if ("ORAL.CLP_HF".equals(lab)) {
//            this.messageBox("����˥��");
            ClpHFTool ClpHFTool = new ClpHFTool();
            parm.addData("A", "1.ʵʩ�����ҹ������ۣ���Ƭ");
            parm.addData("A", "1.ʵʩ�����ҹ������ۣ�CDFI");
            parm.addData("A", "2.�����+�ؼ�");
            parm.addData("A", "3.Ѫ�ܽ�����ת��ø��ACE�����Ƽ���Ѫ�ܽ����آ������׿�����ARB��");
            parm.addData("A", "4.��-�������ͼ�");
            parm.addData("A", "5.ȩ��ͪ�׿������ض���˥��");
            parm.addData("A",
                         "6.סԺ�ڼ�ά��ʹ����������ؼ���ACEI/ARBs����-B��ȩ��ͪ�׿���������Ӧ֢�����޸����ã�");
            parm.addData("A",
                         "7.��Ժʱ����ʹ���������ACEI/ARBs����-B��ȩ��ͪ�׿���������Ӧ֢�����޸����ã�����ʾ");
            parm.addData("A", "8.��ҩ�����ƣ�����ͬ�������ƣ�����Ӧ֢��");
            parm.addData("A", "9.Ϊ�����ṩ��������");

            parm.addData("B", ClpHFTool.getHf1Rate());
            parm.addData("B", ClpHFTool.getHf1_1Rate());
            parm.addData("B", ClpHFTool.getHf2Rate());
            parm.addData("B", ClpHFTool.getHf3Rate());
            parm.addData("B", ClpHFTool.getHf4Rate());
            parm.addData("B", ClpHFTool.getHf5Rate());
            parm.addData("B", ClpHFTool.getHf6Rate());
            parm.addData("B", ClpHFTool.getHf7Rate());
            parm.addData("B", ClpHFTool.getHf8Rate());
            parm.addData("B", ClpHFTool.getHf9Rate());

            parm.setCount(10);

            ClpExportExcleTool.getInstance().exeSaveExcel("HF,500;����,100", parm,
                    "�ļ�");

        } else {
            this.messageBox("��ѡ��һ�ֲ��ֵ���");
            return;
        }


    }


    public void onQuery() {
//        this.messageBox_(getParm());
        String sql = "";
        sql = getSql(getParm());
//        System.out.println("===>"+sql);
        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
//        this.messageBox_(parm);


    }

    public String getSql(TParm parm) {
        String str = "";
        str = " SELECT * FROM " + parm.getData("CLASSIFY") + " WHERE " +
              " IN_DATE BETWEEN DATE((char('" + parm.getData("START_DATE") +
              "'))) AND DATE((char('" + parm.getData("END_DATE") + "'))) " +
              (parm.getData("DEPT_CODE") == null ? "" :
               " AND DEPT_CODE='" + parm.getData("DEPT_CODE") + "'");

        return str;
    }

    public void onClear() {
        initDateTime();
        All.setSelected(true);
        DEPT_CODE.setText("");
        CLASSIFY.setText("");

    }

    public void onDept() {
        DEPT_CODE.setEnabled(true);
    }


}
