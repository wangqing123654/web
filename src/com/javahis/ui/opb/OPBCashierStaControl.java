package com.javahis.ui.opb;

import com.dongyang.control.*;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import java.util.HashSet;
import java.util.Iterator;
import java.sql.Timestamp;
import jdo.sys.SystemTool;
import com.dongyang.util.StringTool;
import java.text.DecimalFormat;
import jdo.sys.Operator;
import jdo.opb.OPBCashierStaTool;
import com.dongyang.ui.TComboBox;
import jdo.sys.SYSRegionTool;

/**
 * <p>Title: �����շ�Ա������ͳ��</p>
 *
 * <p>Description: �����շ�Ա������ͳ��</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2010-3-24
 * @version 1.0
 */
public class OPBCashierStaControl
    extends TControl {
    TParm DATA; //��¼��ѯ������Ϣ
    TTable TABLE;
    String STA_DATE; //��¼ͳ������
    public void onInit() {
        super.onInit();
        TABLE = (TTable)this.getComponent("Table");
        onClear();
        //================pangben modify 20110405 start ��������
        setValue("REGION_CODE", Operator.getRegion());
        //================pangben modify 20110405 stop
        //========pangben modify 20110421 start Ȩ�����
        TComboBox cboRegion = (TComboBox)this.getComponent("REGION_CODE");
        cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(this.
                getValueString("REGION_CODE")));
        //===========pangben modify 20110421 stop

    }

    /**
     * ��ѯ
     */
    public void onQuery() {
        if (this.getValue("DATE_S") == null) {
            this.messageBox_("��ѡ���ѯ��ʼ����");
            this.grabFocus("DATE_S");
        }
        if (this.getValue("DATE_E") == null) {
            this.messageBox_("��ѡ���ѯ��ʼ����");
            this.grabFocus("DATE_E");
        }
        TParm parm = new TParm();
        parm.setData("DATE_S",
                     StringTool.getString( (Timestamp)this.getValue("DATE_S"),
                                          "yyyyMMdd") +
                     StringTool.getString( (Timestamp)this.getValue("TIME_S"),
                                          "HHmmss"));
        parm.setData("DATE_E",
                     StringTool.getString( (Timestamp)this.getValue("DATE_E"),
                                          "yyyyMMdd") +
                     StringTool.getString( (Timestamp)this.getValue("TIME_E"),
                                          "HHmmss"));
        if (this.getValueString("CASHIER_CODE").length() > 0) {
            parm.setData("CASHIER_CODE", this.getValue("CASHIER_CODE"));
        }
        if (this.getValueString("ADM_TYPE").length() > 0) {
            parm.setData("ADM_TYPE", this.getValue("ADM_TYPE"));
        }
        //=======================pangben modify 20110405 start  ��Ӳ�ѯ����
        //����
       if(getValueString("REGION_CODE").length()>0)
           parm.setData("REGION_CODE", getValueString("REGION_CODE"));
       //=======================pangben modify 20110405 stop
        //��¼ͳ������ ��ӡʹ��
        STA_DATE = StringTool.getString( (Timestamp)this.getValue("DATE_S"),
                                        "yyyy/MM/dd") + "��" +
            StringTool.
            getString( (Timestamp)this.getValue("DATE_E"), "yyyy/MM/dd");
        //��ѯ�շ�����
        TParm in = OPBCashierStaTool.getInstance().selectIn(parm);
        //��ѯ�˷�����
        TParm out = OPBCashierStaTool.getInstance().selectOut(parm);
        //========pangben modify 20110414 start ��Ժ������ʱ��������ʾ���������
        TParm regionParm = OPBCashierStaTool.getInstance().selectRegionCode(parm);
     // Map map=new HashMap();
     // map.put("",)
//      TParm regionParm=new TParm();
//      regionParm.addData("REGION_CODE","HIS");
//      regionParm.addData("REGION_CHN_DESC","�ӱ����ҽԺ");
//      regionParm.addData("COUNT",1);
     // regionParm.addData("REGION_CODE","HIS01");
         //========pangben modify 20110414 start
        //���շѺ��˷�������ɸѡ�����շ���ԱCODE
        HashSet usersIn = new HashSet();
        HashSet usersOut = new HashSet();
        for (int i = 0; i < in.getCount(); i++) {
            //=========pangben modify 20110414 start ����������ʾ����
            String [] usersTemp=new String[3];
            usersTemp[0]=in.getValue("CASHIER_CODE", i);
            usersTemp[1]=in.getValue("REGION_CHN_DESC", i);
            usersTemp[2]=in.getValue("REGION_CODE", i);
            usersIn.add(usersTemp);
              //=========pangben modify 20110414 stop
        }
        for (int i = 0; i < out.getCount(); i++) {
            //=========pangben modify 20110414 start ����������ʾ����
            String [] usersTemp=new String[2];
            usersTemp[0]=out.getValue("CASHIER_CODE", i);
            usersTemp[1]=out.getValue("REGION_CHN_DESC", i);
            usersOut.add(usersTemp);
            //=========pangben modify 20110414 stop
        }

        DecimalFormat df = new DecimalFormat("0.00");
        DATA = new TParm(); //���DATAԭ������
        //===========pangben modify 20110415 star �жϵõ��ı����Ǹ�������ֵ�࣬��Ϊ���ݲ�����Ա�����ƶ�����ͬ������ѭ���������ݽ��л���
        Iterator iter ;
        if(usersIn.size()>=usersOut.size())
            iter = usersIn.iterator();
        else
            iter=  usersOut.iterator();
        //===========pangben modify 20110415 star
        int row = 0; //��¼����
        //����ÿλ�շ�Ա��������
        while (iter.hasNext()) {

            //=========pangben modify 20110414 start
            String[] user_code =(String[]) iter.next(); //�շ�ԱCODE
            String user_name = ""; //��¼�շ�Ա����
            double sum = 0; //�����ܽ��
            boolean inFlg = false; //��¼���շ�Ա�Ƿ����շ�����
            boolean outFlg = false; //��¼���շ�Ա�Ƿ����˷�����
            //һ���շ����ݻ���
            for (int i = 0; i < in.getCount(); i++) {
                if (user_code[0].equals(in.getValue("CASHIER_CODE", i))&&user_code[2].equals(in.getValue("REGION_CODE", i))) {
                    DATA.setData("IN_NUM", row, in.getData("NUM", i));
                    DATA.setData("IN_MONEY", row,
                                 df.format(in.getData("MONEY", i)));
                    sum += in.getDouble("MONEY", i);
                    user_name = in.getValue("USER_NAME", i);
                    inFlg = true;
                }
            }
           //һ���˷����ݻ���
            for (int i = 0; i < out.getCount(); i++) {
                if (user_code[0].equals(out.getValue("CASHIER_CODE", i))&&user_code[2].equals(out.getValue("REGION_CODE", i))) {
                    DATA.setData("OUT_NUM", row, out.getData("NUM", i));
                    DATA.setData("OUT_MONEY", row,
                                 df.format(0 - out.getDouble("MONEY", i))); //�˷���ʾ����
                    sum += (0 - out.getDouble("MONEY", i));
                    //����û���Ϊ�� ֤���շ���Ϣ��û�и��շ�Ա������ ��ô���˷���Ϣ��ͳ��
                    if ("".equals(user_name)) {
                        user_name = out.getValue("USER_NAME", i);
                    }
                    outFlg = true;
                }
            }
            //����������շ����� ��ô����
            if (!inFlg) {
                DATA.setData("IN_NUM", row, 0);
                DATA.setData("IN_MONEY", row, 0);
            }
            //����������˷����� ��ô����
            if (!outFlg) {
                DATA.setData("OUT_NUM", row, 0);
                DATA.setData("OUT_MONEY", row, 0);
            }
            DATA.setData("USER_NAME", row, user_name);
            DATA.setData("SUM", row, df.format(sum));
            DATA.setData("REGION_CHN_DESC",row,user_code[1]);
            DATA.setData("REGION_CODE",row,user_code[2]);
            row++;
        }
        for (int j = 0; j < regionParm.getCount("REGION_CODE"); j++) {
            //����ϼ���
            int inCountSum = 0; //�շ�������
            int outCountSum = 0; //�˷�������
            double inMoneySum = 0; //�շ��ܽ��
            double outMoneySum = 0; //�˷��ܽ��
            double sumMoney = 0; //�ܽ��'
            for (int i = 0; i < DATA.getCount("USER_NAME"); i++) {

                //========pangben modify 20110414 start
                if(regionParm.getValue("REGION_CODE",j).equals(DATA.getValue("REGION_CODE",i))){
                    inCountSum += DATA.getInt("IN_NUM", i);
                    outCountSum += DATA.getInt("OUT_NUM", i);
                    inMoneySum += DATA.getDouble("IN_MONEY", i);
                    outMoneySum += DATA.getDouble("OUT_MONEY", i);
                    sumMoney +=StringTool.round(DATA.getDouble("SUM", i),2) ;
                }
                //========pangben modify 20110414 stop
            }
            DATA.setData("REGION_CHN_DESC", row,regionParm.getValue("REGION_CHN_DESC",j));//========pangben modify 20110414
            DATA.setData("USER_NAME", row, "�ܼƣ�");
            DATA.setData("IN_NUM", row, inCountSum);
            DATA.setData("OUT_NUM", row, outCountSum);
            DATA.setData("IN_MONEY", row, df.format(inMoneySum));
            DATA.setData("OUT_MONEY", row, df.format(outMoneySum));
            DATA.setData("SUM", row, df.format(StringTool.round(sumMoney,2)));
            row++;
        }
        for (int i = 0; i < DATA.getCount("USER_NAME"); i++) {
        	DATA.addData("SUM_NUM", DATA.getInt("IN_NUM", i)+DATA.getInt("OUT_NUM", i));
		}
        TABLE.setParmValue(DATA);
    }

    /**
     * ���
     */
    public void onClear() {
        //���ڳ�ʼ��
        Timestamp now = SystemTool.getInstance().getDate();
        this.setValue("DATE_S", now);
        this.setValue("DATE_E", now);
        this.setValue("TIME_S", StringTool.getTimestamp("00:00:00", "hh:mm:ss"));
        this.setValue("TIME_E", now);
        this.clearValue("CASHIER_CODE;ADM_TYPE");
//        TABLE.removeRowAll();
        TParm resultParm = new TParm();
        TABLE.setParmValue(resultParm);
        DATA = new TParm();
    }

    /**
     * ��ӡ
     */
    public void onPrint() {
        if (DATA.getCount("USER_NAME") <= 0) {
            return;
        }
        TParm printData = new TParm();
        TParm T1 = new TParm();
        for (int i = 0; i < DATA.getCount("USER_NAME"); i++) {
            T1.setRowData(i, DATA.getRow(i));
        }
        T1.setCount(DATA.getCount("USER_NAME"));
        //=========pangben modify 20110419 start
        T1.addData("SYSTEM", "COLUMNS", "REGION_CHN_DESC");
        //=========pangben modify 20110419 stop
        T1.addData("SYSTEM", "COLUMNS", "USER_NAME");
        T1.addData("SYSTEM", "COLUMNS", "IN_NUM");
        T1.addData("SYSTEM", "COLUMNS", "IN_MONEY");
        T1.addData("SYSTEM", "COLUMNS", "OUT_NUM");
        T1.addData("SYSTEM", "COLUMNS", "OUT_MONEY");
        T1.addData("SYSTEM", "COLUMNS", "SUM");
        printData.setData("T1", T1.getData());
        printData.setData("date", "TEXT", STA_DATE);
        //========pangben modify 20110419 start
        String region = ((TTable)this.getComponent("Table")).getParmValue().getRow(
                0).getValue("REGION_CHN_DESC");
        printData.setData("TITLE", "TEXT",
                          (this.getValue("REGION_CODE") != null &&
                           !this.getValue("REGION_CODE").equals("") ? region :
                           "����ҽԺ") + "�ż����շ�Ա��Чͳ��");
        //========pangben modify 20110419 stop

        printData.setData("printDate", "TEXT",
                          StringTool.getString(SystemTool.getInstance().getDate(),
                                               "yyyy/MM/dd"));
        printData.setData("printUser", "TEXT", Operator.getName());
        this.openPrintDialog("%ROOT%\\config\\prt\\bil\\BILOpbCashierSta.jhw",
                             printData);
    }
}
