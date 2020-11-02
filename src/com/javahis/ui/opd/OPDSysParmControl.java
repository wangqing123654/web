package com.javahis.ui.opd;

import jdo.opd.OPDSysParmTool;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;

/**
 *
 * <p>
 * Title: ҽ��������Panel
 * </p>
 *
 * <p>
 * Description:ҽ��������Panel
 * </p>
 *
 * <p>
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 * </p>
 *
 * <p>
 * Company:Javahis
 * </p>
 *
 * @author ehui 20080924
 * @version 1.0
 */
public class OPDSysParmControl
    extends TControl {
    public void onInit() {
        super.onInit();
        TParm parm = OPDSysParmTool.getInstance().selectAll();
        if (parm.getErrCode() < 0) {
            this.messageBox("��ʼ��ʧ��");
            return;
        }
        /*
         * ��ͯ����ǩ�����趨	I	AGE
         ����ǩÿҳ������	I	PAGE_NUM
         Ԥ����Ƭ����	I	DCT_TAKE_DAYS
         Ԥ����Ƭʹ�ü���(ml)	I	DCT_TAKE_QTY
         ��������	I	PREGNANT_WEEKS
         �������漴��������ҩ���ע��	I	SAVERDU_FLG
         ����ҩҽ�����
         ҽ�����ע��	I	W_NHICHECK_FLG
         ҩƷ������	I	W_TYPE_NUM
         �շ�	I	W_TAKE_DAYS
         �ܼ�	I	W_TOT_AMT
         ��Ƭҽ�����
         ҽ�����ע��	I	G_NHICHECK_FLG
         ҩƷ������	I	G_TYPE_NUM
         �շ�	I	G_TAKE_DAYS
         �ܼ�	I	G_TOT_AMT
         �����޶���������  E_DAYS
         */
        String value = parm.getValue("AGE", 0);
        int year = (value == null || value.trim().length() < 1) ? 0 :
            Integer.parseInt(value);
        this.setValue("AGE", year);

        value = parm.getValue("PAGE_NUM", 0);
        int PAGE_NUM = (value == null || value.trim().length() < 1) ? 0 :
            Integer.parseInt(value);
        this.setValue("PAGE_NUM", PAGE_NUM);

        value = parm.getValue("DCT_TAKE_DAYS", 0);
        int DCT_TAKE_DAYS = (value == null || value.trim().length() < 1) ? 0 :
            Integer.parseInt(value);
        this.setValue("DCT_TAKE_DAYS", DCT_TAKE_DAYS);

        value = parm.getValue("DCT_TAKE_QTY", 0);
        int DCT_TAKE_QTY = (value == null || value.trim().length() < 1) ? 0 :
            Integer.parseInt(value);
        this.setValue("DCT_TAKE_QTY", DCT_TAKE_QTY);

        value = parm.getValue("PREGNANT_WEEKS", 0);
        int PREGNANT_WEEKS = (value == null || value.trim().length() < 1) ? 0 :
            Integer.parseInt(value);
        this.setValue("PREGNANT_WEEKS", PREGNANT_WEEKS);

        value = parm.getValue("SAVERDU_FLG", 0);
        boolean SAVERDU_FLG = ("Y".equalsIgnoreCase(value)) ? true : false;
        this.setValue("SAVERDU_FLG", SAVERDU_FLG);

        value = parm.getValue("W_NHICHECK_FLG", 0);
        boolean W_NHICHECK_FLG = ("Y".equalsIgnoreCase(value)) ? true : false;
        this.setValue("W_NHICHECK_FLG", W_NHICHECK_FLG);

        value = parm.getValue("W_TYPE_NUM", 0);
        int W_TYPE_NUM = (value == null || value.trim().length() < 1) ? 0 :
            Integer.parseInt(value);
        this.setValue("W_TYPE_NUM", W_TYPE_NUM);

        value = parm.getValue("W_TAKE_DAYS", 0);
        int W_TAKE_DAYS = (value == null || value.trim().length() < 1) ? 0 :
            Integer.parseInt(value);
        this.setValue("W_TAKE_DAYS", W_TAKE_DAYS);

        value = parm.getValue("W_TOT_AMT", 0);
        double W_TOT_AMT = (value == null || value.trim().length() < 1) ? 0.0 :
            Double.parseDouble(value);
        this.setValue("W_TOT_AMT", W_TOT_AMT);

        value = parm.getValue("G_NHICHECK_FLG", 0);
        boolean G_NHICHECK_FLG = ("Y".equalsIgnoreCase(value)) ? true : false;
        this.setValue("G_NHICHECK_FLG", G_NHICHECK_FLG);

        value = parm.getValue("G_TYPE_NUM", 0);
        int G_TYPE_NUM = (value == null || value.trim().length() < 1) ? 0 :
            Integer.parseInt(value);
        this.setValue("G_TYPE_NUM", G_TYPE_NUM);

        value = parm.getValue("G_TAKE_DAYS", 0);
        int G_TAKE_DAYS = (value == null || value.trim().length() < 1) ? 0 :
            Integer.parseInt(value);
        this.setValue("G_TAKE_DAYS", G_TAKE_DAYS);

        value = parm.getValue("G_TOT_AMT", 0);
        double G_TOT_AMT = (value == null || value.trim().length() < 1) ? 0.0 :
            Double.parseDouble(value);
        this.setValue("G_TOT_AMT", G_TOT_AMT);

        value = parm.getValue("E_DAYS", 0);
        int days = (value == null || value.trim().length() < 1) ? 0 :
            Integer.parseInt(value);
        this.setValue("E_DAYS", days);
        //==zhangp 20120702 start
        value = parm.getValue("DS_MED_DAY", 0);
        int ds_med_day = (value == null || value.trim().length() < 1) ? 0 :
            Integer.parseInt(value);
        this.setValue("DS_MED_DAY", ds_med_day);
        //==zhangp 20120702 end
        
        //==caoyong 20131223 start
        value = parm.getValue("CH_AGE",0);
        int ch_age = (value == null || value.trim().length() < 1) ? 0 :
        	Integer.parseInt(value);
        this.setValue("CH_AGE",ch_age);
        
        value = parm.getValue("MED_FLG",0);
        String med_flg = (value == null || value.trim().length() < 1) ? "":value;
        this.setValue("MED_FLG",med_flg);
        
        value = parm.getValue("BOX_DISPENSE_FLG",0);
        String box_dispense_flg = (value == null || value.trim().length() < 1) ? "":value;
        this.setValue("BOX_DISPENSE_FLG",box_dispense_flg);
        
        
        //==caoyong 20131223 end
    }

    /**
     * ����
     */
    public void onSave() {
        /*
         * ��ͯ����ǩ�����趨	I	AGE
         ����ǩÿҳ������	I	PAGE_NUM
         Ԥ����Ƭ����	I	DCT_TAKE_DAYS
         Ԥ����Ƭʹ�ü���(ml)	I	DCT_TAKE_QTY
         ��������	I	PREGNANT_WEEKS
         �������漴��������ҩ���ע��	I	SAVERDU_FLG
         ����ҩҽ�����
         ҽ�����ע��	I	W_NHICHECK_FLG
         ҩƷ������	I	W_TYPE_NUM
         �շ�	I	W_TAKE_DAYS
         �ܼ�	I	W_TOT_AMT
         ��Ƭҽ�����
         ҽ�����ע��	I	G_NHICHECK_FLG
         ҩƷ������	I	G_TYPE_NUM
         �շ�	I	G_TAKE_DAYS
         �ܼ�	I	G_TOT_AMT
         �����޶���������  E_DAYS
         */
        TParm parm = getParmForTag("AGE;PAGE_NUM;DCT_TAKE_DAYS;DCT_TAKE_QTY;PREGNANT_WEEKS;SAVERDU_FLG;W_NHICHECK_FLG;" +
        		"W_TYPE_NUM;W_TAKE_DAYS;W_TOT_AMT;G_NHICHECK_FLG;G_TYPE_NUM;G_TAKE_DAYS;G_TOT_AMT;E_DAYS;CH_AGE;MED_FLG" +//add  CH_AGE caoyong 20131225 (��ͯ���䶨��)
        		//===ZHANGP 20120702 START
        		";DS_MED_DAY;BOX_DISPENSE_FLG");
        //===ZHANGP 20120702 END

        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        TParm result = OPDSysParmTool.getInstance().updataData(parm);
        if (result.getErrCode() < 0) {
            this.messageBox("E0001");
            return;
        }
        this.messageBox("P0001");
    }
}
