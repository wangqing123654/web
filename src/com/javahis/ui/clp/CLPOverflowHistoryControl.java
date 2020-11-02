package com.javahis.ui.clp;

import java.util.Date;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;
import com.dongyang.control.TControl;
import jdo.clp.CLPOverflowHistoryTool;
import com.dongyang.data.TParm;
import com.dongyang.ui.TComboNode;
import com.dongyang.ui.TTable;
import java.util.List;
import java.util.ArrayList;
import java.util.Vector;

import jdo.sys.Operator;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.base.TComboBoxModel;

import jdo.sys.SYSRegionTool;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>Title: </p>
 *
 * <p>Description: ���Ҳ����������</p>
 *
 * <p>Copyright: Copyright (c) 2011</p>
 *
 * <p>Company: </p>
 *
 * @author pangben 20110707
 * @version 1.0
 */
public class CLPOverflowHistoryControl extends TControl {
    /**
     * ��ʼ������
     */
    public void onInit() {
        initPage();
        //Ȩ�����
        TComboBox cboRegion = (TComboBox)this.getComponent("REGION_CODE");
        cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(this.
                getValueString("REGION_CODE")));
    }

    private TTable table;
    public CLPOverflowHistoryControl() {
    }

    /**
     * ��ѯ����
     */
    public void onQuery() {
        TParm parm = new TParm();
        TParm parmManagem = new TParm();
        TParm parmManagemHistory = new TParm();
        String date_s = getValueString("DATE_S");
        String date_e = getValueString("DATE_E");
        if (null == date_s || date_s.length() <= 0 || null == date_e ||
            date_e.length() <= 0) {
            this.messageBox("��������Ҫ��ѯ��ʱ�䷶Χ");
            return;
        }
        TParm result = new TParm();
        date_s = date_s.substring(0, date_s.lastIndexOf(".")).replace(":", "").
                 replace("-", "").replace(" ", "");
        date_e = date_e.substring(0, date_e.lastIndexOf(".")).replace(":", "").
                 replace("-", "").replace(" ", "");
        parm.setData("DATE_S", date_s);
        parm.setData("DATE_E", date_e);
        if (null != this.getValueString("REGION_CODE") &&
            this.getValueString("REGION_CODE").length() > 0)
            parm.setData("REGION_CODE", this.getValueString("REGION_CODE"));
        //·����
        parmManagem = CLPOverflowHistoryTool.getInstance().selectData(
                "selectManagem", parm);
        //�����
        parmManagemHistory = CLPOverflowHistoryTool.getInstance().selectData(
                "selectManagemHistory", parm);
        System.out.println("parmManagem::"+parmManagem);
        System.out.println("parmManagemHistory::::"+parmManagemHistory);
        //parmManagem.addParm(parmManagemHistory);
        List resultList = new ArrayList(); //����һ����Ų�ͬ��ֵ�ļ���
        for (int i = 0; i < parmManagem.getCount(); i++) {
        	String deptChnDesc=parmManagem.getValue("DEPT_CHN_DESC",i);
        	String stationDesc=parmManagem.getValue("STATION_DESC",i);
        	String clncpathCode=parmManagem.getValue("CLNCPATH_CODE",i);
        	for (int j = 0; j < parmManagemHistory.getCount(); j++) {
        		String deptChnDescH=parmManagemHistory.getValue("DEPT_CHN_DESC",j);
            	String stationDescH=parmManagemHistory.getValue("STATION_DESC",j);
            	String clncpathCodeH=parmManagemHistory.getValue("CLNCPATH_CODE",j);
				if (deptChnDesc.equals(deptChnDescH)&&stationDesc.equals(stationDescH)
						&&clncpathCode.equals(clncpathCodeH)) {
					parmManagem.setData("HISTORYSUM",i,parmManagemHistory.getInt("HISTORYSUM",j));
					parmManagem.setData("OVERBATILITY",i,StringTool.round(parmManagemHistory.getDouble("HISTORYSUM",j)/(parmManagem.getDouble("SUM",i)+parmManagemHistory.getDouble("HISTORYSUM",j))*100, 2)+"%");
					break;
				}
			}
		}
//        TComboBox com = (TComboBox) this.getComponent("REGION_CODE");
//        //�����ݷŵ�һ��������
//        for (int i = 0; i < parmManagemHistory.getCount(); i++) {
//        	String rn = parmManagemHistory.getValue("REGION_CODE", i);
//			TComboBoxModel tbm = com.getModel();
//			Vector v = tbm.getItems();
//			for (int j = 0; j < v.size(); j++) {
//				TComboNode tn = (TComboNode) v.get(j);
//				if (rn.equals(tn.getID())) {
//					rn = tn.getName();
//					break;
//				}
//			}
//        	result.addData("REGION_CODE",
//        			rn);// wangzhilei 20120724 ���
//            result.addData("STATION_DESC",
//                           parmManagemHistory.getValue("STATION_DESC", i));
//            result.addData("CLNCPATH_CODE",
//                           parmManagemHistory.getValue("CLNCPATH_CODE", i));
//            result.addData("DEPT_CHN_DESC",
//                           parmManagemHistory.getValue("DEPT_CHN_DESC", i));
//            result.addData("CLNCPATH_CHN_DESC",
//                           parmManagemHistory.getValue("CLNCPATH_CHN_DESC", i));
//            result.addData("HISTORYSUM",
//                           parmManagemHistory.getValue("HISTORYSUM", i));
//            result.addData("SUM", parmManagemHistory.getValue("HISTORYSUM", i));
//            //2011-07-21 luhai delete
////            resultList.add(parmManagemHistory.getValue("CLNCPATH_CODE", i));
//            if (!resultList.contains(parmManagemHistory.getValue("CLNCPATH_CODE", i)))
//                resultList.add(parmManagemHistory.getValue("CLNCPATH_CODE", i));
//        }
//        for (int i = 0; i < parmManagem.getCount(); i++) {
//            //20110721 luhai ɾ��
////            result.addData("STATION_DESC",
////                           parmManagemHistory.getValue("STATION_DESC", i));
//        	String rnM = parmManagem.getValue("REGION_CODE", i);
//			TComboBoxModel tbm = com.getModel();
//			Vector v = tbm.getItems();
//			for (int j = 0; j < v.size(); j++) {
//				TComboNode tn = (TComboNode) v.get(j);
//				if (rnM.equals(tn.getID())) {
//					rnM = tn.getName();
//					break;
//				}
//			}
//        	 result.addData("REGION_CODE",
//        			 rnM);// wangzhilei 20120724 ���
//            result.addData("STATION_DESC",
//                           parmManagem.getValue("STATION_DESC", i));
//            result.addData("CLNCPATH_CODE",
//                           parmManagem.getValue("CLNCPATH_CODE", i));
//            result.addData("DEPT_CHN_DESC",
//                           parmManagem.getValue("DEPT_CHN_DESC", i));
//            result.addData("CLNCPATH_CHN_DESC",
//                           parmManagem.getValue("CLNCPATH_CHN_DESC", i));
//            result.addData("SUM", parmManagem.getValue("SUM", i));
//            result.addData("HISTORYSUM", parmManagem.getValue("HISTORYSUM", i));
//            //�ڼ����в����ڴ�ֵ���
//            if (!resultList.contains(parmManagem.getValue("CLNCPATH_CODE", i)))
//                resultList.add(parmManagem.getValue("CLNCPATH_CODE", i));
//        }
//        result.setCount(parmManagem.getCount() + parmManagemHistory.getCount());
        //��parmManagem ��պ���ΪTable��TParmʹ��
       // parmManagem = new TParm();
        //ѭ�����������е�����ֵ����ͬ�������ۼ�
//        for (int i = 0; i < parmManagem.getCount(); i++) {
//            int sum = 0; //·���˴�
//            int sumHistory = 0; //����˴�
//            double sumDouble =0.00;
//            double sumHistoryDouble=0.00;
//            String CLNCPATH_CHN_DESC = ""; //·������
//            String DEPT_CHN_DESC = ""; //��������
//            String stationDesc="";//����
//            String regionCode="";
//            if (condition) {
//				
//			}
////            for (int j = 0; j < result.getCount(); j++) {
////                if (resultList.get(i).equals(result.getValue("CLNCPATH_CODE", j))) {
////                    sum += result.getInt("SUM", j);
////                    sumHistory += result.getInt("HISTORYSUM", j);
////                    sumHistoryDouble += result.getDouble("HISTORYSUM", j);
////                    sumDouble+= result.getDouble("SUM", j);
////                    CLNCPATH_CHN_DESC = result.getValue("CLNCPATH_CHN_DESC", j);
////                    DEPT_CHN_DESC = result.getValue("DEPT_CHN_DESC", j);
////                    stationDesc=result.getValue("STATION_DESC",j);
////                    regionCode=result.getValue("REGION_CODE",j);// wangzhilei 20120724 ���
////                }
////            }
//            parmManagem.addData("REGION_CODE",regionCode);// wangzhilei 20120724 ���
//            parmManagem.addData("STATION_DESC",stationDesc);
//            parmManagem.addData("DEPT_CHN_DESC", DEPT_CHN_DESC);
//            parmManagem.addData("CLNCPATH_CHN_DESC", CLNCPATH_CHN_DESC);
//            parmManagem.addData("SUM", sum);
//            parmManagem.addData("OVERBATILITY",
//                                StringTool.round(sumHistoryDouble /
//                                		sumDouble*100, 2) + "%");
//            parmManagem.addData("HISTORYSUM", sumHistory);
//        }
//        parmManagem.setCount(resultList.size());
        if (parmManagem.getCount() <= 0) {
            this.messageBox("��������");
            table.removeRowAll();
            return;
        }

        int sumAll = 0;
        int sumHistoryAll = 0;
        double sumDouble =0.00;
        double sumHistoryDouble=0.00;
        for (int i = 0; i < parmManagem.getCount(); i++) {
            sumAll += parmManagem.getInt("SUM", i);
            sumHistoryAll += parmManagem.getInt("HISTORYSUM", i);
            sumDouble+= parmManagem.getDouble("SUM", i);
            sumHistoryDouble+= parmManagem.getDouble("HISTORYSUM", i);
        }
        parmManagem.addData("REGION_CHN_ABN", "�ܼ�:");// wangzhilei 20120724 ���
        parmManagem.addData("STATION_DESC","");
        parmManagem.addData("CLNCPATH_CHN_DESC", "");
        parmManagem.addData("SUM", sumAll);
        parmManagem.addData("HISTORYSUM", sumHistoryAll);
        parmManagem.addData("OVERBATILITY",
                            (StringTool.round(sumHistoryDouble /sumDouble*100, 2)) +
                            "%");
        parmManagem.setCount(resultList.size() + 1);
        table.setParmValue(parmManagem);
    }

    /**
     * ��ʼ��������
     */
    private void initPage() {
        Timestamp date = StringTool.getTimestamp(new Date());
        table = (TTable) getComponent("TABLE");
        this.setValue("REGION_CODE", Operator.getRegion());
        // ��ʼ����ѯ����
        this.setValue("DATE_E",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("DATE_S",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
    }

    /**
     * ��ӡ����
     */
    public void onPrint() {

        TParm parm = table.getParmValue();
        if (null == parm || parm.getCount() <= 0) {
            this.messageBox("û����Ҫ��ӡ������");
            return;
        }
        TParm result = new TParm();
        parm.addData("SYSTEM", "COLUMNS", "REGION_CODE");
        parm.addData("SYSTEM", "COLUMNS", "DEPT_CHN_DESC");
        parm.addData("SYSTEM","COLUMNS","STATION_DESC");
        parm.addData("SYSTEM", "COLUMNS", "CLNCPATH_CHN_DESC");
        parm.addData("SYSTEM", "COLUMNS", "SUM");
        parm.addData("SYSTEM", "COLUMNS", "HISTORYSUM");
        parm.addData("SYSTEM", "COLUMNS", "OVERBATILITY");
        result.setData("S_DATE", "TEXT",
                       getValueString("DATE_S").substring(0,
                getValueString("DATE_S").lastIndexOf(".")));
        result.setData("E_DATE", "TEXT",
                       getValueString("DATE_E").substring(0,
                getValueString("DATE_S").lastIndexOf(".")));
        result.setData("OPT_USER", Operator.getName());
        result.setData("TABLE", parm.getData());
        result.setData("TITLE", "TEXT",
                       (null != Operator.getHospitalCHNShortName() ?
                        Operator.getHospitalCHNShortName() : "����Ժ��") +
                       "���Ҳ����������");
        //¬�������Ʊ���
        //��β
        result.setData("CREATEUSER", "TEXT", Operator.getName());
        this.openPrintWindow("%ROOT%\\config\\prt\\CLP\\CLPNewOverflowHistory.jhw",
                             result);
    }


    /**
     * ���
     */
    public void onClear() {
        initPage();
        table.removeRowAll();
    }

    /**
     * ���Excel
     */
    public void onExport() {
        //�õ�UI��Ӧ�ؼ�����ķ���
        TParm parm = table.getParmValue();
        if (null == parm || parm.getCount() <= 0) {
            this.messageBox("û����Ҫ����������");
            return;
        }
        ExportExcelUtil.getInstance().exportExcel(table, "���Ҳ����������");
    }

}
