package com.javahis.ui.udd;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Vector;
import jdo.bil.BILComparator;
import jdo.sys.Pat;
import jdo.udd.UDDAntibioticsMonitTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * 
 * <p> Title:����ҩ���ٴ�Ӧ�ü�� </p>
 * 
 * <p> Description:TODO </p>
 * 
 * <p> Copyright: Copyright (c) 2012 </p>
 * 
 * <p> Company: BlueCore </p>
 * 
 * @author Yuanxm
 * @version 1.0
 */
public class UDDAntibioticsMonitControl extends TControl {

    //TPanel PAT_PANEL;
    //TPanel MEDI_PANEL;
    TTable PAT_TABLE; // ���߱��
    TTable MEDI_TABLE; // ��ҩ���

    private TParm data;
    
    private BILComparator compare=new BILComparator();
    private boolean ascending = false;
    private int sortColumn = -1;
    private TTabbedPane TabbedPane;

    /**
     * ��ʼ���¼�
     */
    public void onInit() {
        super.onInit();
        addListener(getTTable("PAT_TABLE"));
        initComponent();
        onClear();// ��ս���
    }

    /**
     * ��ʼ�����
     */
    public void initComponent() {
        Timestamp date = StringTool.getTimestamp(new Date());
        // ��ʼ����ѯ����
        this.setValue("START_DATE", StringTool.rollDate(date, -30).toString().substring(0, 10).replace('-', '/') + " 00:00:00");
        this.setValue("END_DATE", date.toString().substring(0, 10).replace('-', '/') + " 23:59:59");
        //PAT_PANEL = (TPanel) this.getComponent("PAT_PANEL");
        //MEDI_PANEL = (TPanel) this.getComponent("MEDI_PANEL");
        TabbedPane = (TTabbedPane) this.getComponent("TabbedPane");   
        PAT_TABLE = (TTable) this.getComponent("PAT_TABLE");
        MEDI_TABLE = (TTable) this.getComponent("MEDI_TABLE");
        this.setValue("ANTIBIOTIC_WAY", "01");//Ԥ��ʹ��
        onTableShowHead();
    }

    /**
     * ��ѯ����
     */
    public void onQuery() {
        TParm selectCondition =
                getParmForTag("START_DATE;END_DATE;DEPT_CODE;MR_NO;PAT_NAME;ALL;SURGERY;UNSURGERY;DEAD;HEAL_LEVEL;USE_ANTIBIOTIC;ANTIBIOTIC_WAY",
                              true);// modify by wanglong 20130801
//        System.out.println("--------���------"+selectCondition);
        data = UDDAntibioticsMonitTool.getInstance().onQuery(selectCondition);
        if (data.getErrCode() < 0) {
            this.messageBox(data.getErrText());
        }
        System.out.println("data::::"+data);
        if (data.getCount()<=0) {
			this.messageBox("û�в�ѯ������");
			PAT_TABLE.setParmValue(new TParm());
			return;
		}
        // ����
        if (TabbedPane.getSelectedIndex()==0) {
            int count = data.getCount();
            if (count > 0) {
                double totCharge03 = 0;
                double totCharge03Fee = 0;
                double sumDDD = 0;// �������ۻ���
                double totdays = 0;// סԺ������
                int coCount = 0;// ����ʹ������ add by wanglong 20130802
                int preCount = 0; // Ԥ��ʹ������
                int timeCount = 0;// ʱ����������
                int cureCount = 0;// ���ƺ�������
                // �����ط��ò�Ϊ0�ĵ�����/��ѯ�������м�¼��=סԺ���߿���ҩ��ʹ����ͳ��ֵ
                TParm result=null;
                for (int i = 0; i < count; i++) {
                    TParm rowParm = data.getRow(i);
                    double charge03 = rowParm.getDouble("CHARGE_03");
                    result=new TParm(TJDODBTool.getInstance().select(" SELECT B.CASE_NO " +
                    " FROM ODI_ORDER B,PHA_BASE C WHERE B.CAT1_TYPE = 'PHA' AND B.CASE_NO='"+rowParm.getValue("CASE_NO")+
                    "' AND B.ORDER_CODE=C.ORDER_CODE AND B.ANTIBIOTIC_WAY IS NOT NULL GROUP BY B.CASE_NO"));//Ԥ��ʹ��"));
                    if (result.getCount()>0) {
                    	data.setData("PRE_USE", i, "Y");
                    	totCharge03 += 1;
                        data.setData("IS_USE", i, "ʹ��");
                    	preCount++;
					}else{
						data.setData("PRE_USE", i, "N");
						data.setData("IS_USE", i, "");
					}
                    totCharge03Fee += charge03;
                    result=new TParm(TJDODBTool.getInstance().select("SELECT SUM(B.DOSAGE_QTY * C.MEDI_QTY / D.DDD) DDD_SUM FROM IBS_ORDD B, PHA_TRANSUNIT C, PHA_BASE D " +
                    "  WHERE B.CAT1_TYPE = 'PHA' AND B.ORDER_CODE = C.ORDER_CODE AND B.ORDER_CODE = D.ORDER_CODE " +
                    "    AND D.ANTIBIOTIC_CODE IS NOT NULL AND B.CASE_NO='"+rowParm.getValue("CASE_NO")+"'"));
                    if (result.getErrCode()<0) {
						this.messageBox("��ѯ��������");
						return;
					}
                    data.setData("DDD_SUM",i,StringTool.round(result.getDouble("DDD_SUM",0), 2));
                    
                    sumDDD += result.getDouble("DDD_SUM",0);
                    int days = rowParm.getInt("REAL_STAY_DAYS");
                    if (days > 0) {
                        totdays += days;
                    }
                    if (rowParm.getBoolean("CO_USE")) {// ����ʹ��
                        coCount++;
                    }
                    if (rowParm.getBoolean("REASONABLE_TIME")) {// ʱ������
                        timeCount++;
                    }
                    if (rowParm.getBoolean("REASONABLE_CURE")) {// ���ƺ���
                        cureCount++;
                    }
                }
                //����ҩ��ʹ��ǿ��(����ҩ��������(��DDD)/��ѯ���ܲ���סԺ����/��ѯ���Ĳ�������)
                sumDDD = StringTool.round(sumDDD, 2);
                double ddd = sumDDD / totdays;// ������ǿ��add by wanglong 20130807
                ddd = StringTool.round(ddd, 2);
                //�����ط�����������0
                int newRow = count + 1;
                data.setData("CHOOSE", newRow, "");
                data.setData("IN_DATE", newRow, "");
                data.setData("OUT_DATE", newRow, "");
                data.setData("OUT_DEPT", newRow, "");
                data.setData("MR_NO", newRow, "");
                data.setData("PAT_NAME", newRow, "");
                data.setData("SEX", newRow, "");
                data.setData("AGE", newRow, "");
                data.setData("NB_WEIGHT", newRow, "");
                data.setData("INTE_DIAG_CODE", newRow, "");
                data.setData("OUT_DIAG_CODE1", newRow, "");
                data.setData("OUT_DIAG_CODE2", newRow, "");
                data.setData("OUT_DIAG_CODE4", newRow, "");
                data.setData("ALLEGIC", newRow, "");
                data.setData("SUM_TOT", newRow, "");
                data.setData("MEDICAL_TOT", newRow, "");
                data.setData("OUT_DIAG_CODE3", newRow, "");
                double average = totCharge03 / count;
                if (totCharge03 > 0) {
                    DecimalFormat df = new DecimalFormat("####0.####");
                    data.setData("CHARGE_03", newRow, df.format(totCharge03Fee));
                    data.setData("IS_USE", newRow, df.format(average*100) + "%");
                } else {
                    data.setData("CHARGE_03", newRow, "");
                    data.setData("IS_USE", newRow, "0.0000%");
                }
                data.setData("OP_CODE", newRow, "");
                data.setData("HEAL_LV", newRow, "");
                data.setData("CASE_NO", newRow, "");
                data.setData("REAL_STAY_DAYS", newRow, totdays);// add by wanglong 20130801
                data.setData("DDD_SUM", newRow, sumDDD);
                data.setData("CO_USE", newRow, coCount + "��");
                data.setData("PRE_USE", newRow, preCount + "��");
                data.setData("RT_FLG", newRow, timeCount);
                data.setData("RC_FLG", newRow, cureCount);
                this.setValue("DDD", ddd + "");// add by wanglong 20130807
            }
//            System.out.println("----------data-------------"+data);
            PAT_TABLE.setParmValue(data);
        }
    }
    public void onTableShowHead(){
    	  if (this.getValue("ANTIBIOTIC_WAY").equals("02")) {
              PAT_TABLE.setHeader("ѡ,30,boolean;��Ժ����,80,Timestamp;��Ժ����,80,Timestamp;��Ժ����,80,DEPT_CODE;������,100;����,80;�Ա�,50,SEX_CODE;����,100;����,50;���1,160;���2,160;���3,160;���4,160;���5,160;����ʷ,160;סԺ�ܷ���,100,double,#########0.00;ҩƷ�ܷ���,100,double,#########0.00;�����ط���,100,double,#########0.00;DDD�ۻ���,100;��Ժ����,80;ʹ�ÿ���ҩƷ,100;����ʹ��,80,YES_NO1;����ʹ��,80,YES_NO2;ʱ������,80,boolean;��������,80,boolean;��������,160;�п����,160;�����,100");
          }else {
              PAT_TABLE.setHeader("ѡ,30,boolean;��Ժ����,80,Timestamp;��Ժ����,80,Timestamp;��Ժ����,80,DEPT_CODE;������,100;����,80;�Ա�,50,SEX_CODE;����,100;����,50;���1,160;���2,160;���3,160;���4,160;���5,160;����ʷ,160;סԺ�ܷ���,100,double,#########0.00;ҩƷ�ܷ���,100,double,#########0.00;�����ط���,100,double,#########0.00;DDD�ۻ���,100;��Ժ����,80;ʹ�ÿ���ҩƷ,100;����ʹ��,80,YES_NO1;Ԥ��ʹ��,80,YES_NO2;ʱ������,80,boolean;��������,80,boolean;��������,160;�п����,160;�����,100"); 
          }
    }
    public void onQueryMedical() {
        PAT_TABLE.acceptText();
        //PAT_PANEL = (TPanel) this.getComponent("PAT_PANEL");
        String caseNo = "";
        TParm parm = new TParm();
        PAT_TABLE = (TTable) this.getComponent("PAT_TABLE");
        TParm patParm = PAT_TABLE.getParmValue();
        int count = patParm.getCount();
        int num = 0;
        // ��ѡ��
        for (int i = 0; i < count; i++) {
            boolean choose = patParm.getBoolean("CHOOSE", i);
            if (choose) {
                caseNo = patParm.getValue("CASE_NO", i);
                if (caseNo.length()<=0) {
					continue;
				}
                parm.addData("CASE_NO", caseNo);
                num++;
            }
        }
        parm.setCount(num);
        if (TabbedPane.getSelectedIndex()==1) {
        	if (parm.getCount("CASE_NO")<=0) {
				this.messageBox("��ѡ����Ҫ��ѯ������");
				TabbedPane.setSelectedIndex(0);
				return;
			}
		}
        if (parm.getCount() > 0) {
            //MEDI_TABLE = (TTable) this.getComponent("MEDI_TABLE");
            TParm result = UDDAntibioticsMonitTool.getInstance().onQueryMedical(parm);
            if (result.getErrCode()<0) {
				this.messageBox("��ѯʧ��");
				TabbedPane.setSelectedIndex(0);
				return;
			}
            if (result.getCount()<=0) {
            	this.messageBox("û����Ҫ��ѯ������");
				TabbedPane.setSelectedIndex(0);
				return;
			}
            MEDI_TABLE.setParmValue(result);
        }
    }
    
    /**
     * ����
     */
    public void onSave() {//add by wanglong 20130802
        if (TabbedPane.getSelectedIndex()==0) {
            PAT_TABLE.acceptText();
            TParm parm = PAT_TABLE.getShowParmValue();
            if (parm.getCount("MR_NO") < 1) {
                return;
            }
            TParm result = UDDAntibioticsMonitTool.getInstance().onSave(parm);
            if (result.getErrCode() < 0) {
                this.messageBox(result.getErrText());
            }
            this.messageBox("P0001");// ����ɹ�
        }
    }
    
    /**
     * ���
     */
    public void onExport() {
        if (TabbedPane.getSelectedIndex()==0) {
            PAT_TABLE = (TTable) this.getComponent("PAT_TABLE");
            if (PAT_TABLE.getRowCount() <= 0) {
                this.messageBox("û�л������");
                return;
            }
            ExportExcelUtil.getInstance().exportExcel(PAT_TABLE, "��������");
        }
        if (TabbedPane.getSelectedIndex()==1) {
            MEDI_TABLE = (TTable) this.getComponent("MEDI_TABLE");
            if (MEDI_TABLE.getRowCount() <= 0) {
                this.messageBox("û�л������");
                return;
            }
            ExportExcelUtil.getInstance().exportExcel(MEDI_TABLE, "��ҩ���");
        }
    }

    /**
     * �����Żس��¼�
     */
    public void onMrNo() {
        Pat pat = Pat.onQueryByMrNo(this.getValueString("MR_NO").trim());
        String mrNo = pat.getMrNo();
        this.setValue("MR_NO", mrNo);
        this.setValue("PAT_NAME", pat.getName());
        this.onQuery();
    }
    
    /**
     * ���
     */
    public void onClear() {
        initComponent();
        this.clearValue("DEPT_CODE;MR_NO;PAT_NAME;DEAD;HEAL_LEVEL;ANTIBIOTIC_WAY");
        PAT_TABLE.setParmValue(new TParm());
        MEDI_TABLE.removeRowAll();
        this.setValue("ALL", "Y");
        this.setValue("SURGERY", "N");
        this.setValue("UNSURGERY", "N");
        this.callFunction("UI|HEAL_LEVEL|setEnabled", false);//add by wanglong 20130802
        this.setValue("USE_ANTIBIOTIC", "Y");
        this.callFunction("UI|ANTIBIOTIC_WAY|setEnabled", true);
        this.setValue("ANTIBIOTIC_WAY", "01");//Ԥ��ʹ��
        this.setValue("DDD", "");// add by wanglong 20130807
        this.setValue("DEPT_CODE", "");
        this.setValue("DEAD", "");
        this.setValue("MR_NO", "");
        this.setValue("PAT_NAME", "");
        this.setValue("CHECK_ALL", "");
        TabbedPane.setSelectedIndex(0);
        onTableShowHead();
    }

    /**
     * �õ�TTable
     * @param tag
     * @return
     */
    public TTable getTTable(String tag) {
        return (TTable) this.getComponent(tag);
    }
    
    /**
     * ��񵥻��¼�
     * @param row
     */
    public void onTableClicked(int row) {
        PAT_TABLE.acceptText();
        if (row > 0) {
            setValueForParm("MR_NO;PAT_NAME",data, PAT_TABLE.getSelectedRow());
        }
    }
    
    /**
     * ȫѡ��ť
     */
    public void onSelectAll() {
        PAT_TABLE.acceptText();
        if (TabbedPane.getSelectedIndex()==0) {
            TParm parm = PAT_TABLE.getParmValue();
            if (parm.getCount() <= 0) {
                return;
            }
            // ȫѡ��ѡ
            if (this.getValueBoolean("CHECK_ALL")) {
                for (int i = 0; i < parm.getCount(); i++) {
                    parm.setData("CHOOSE", i, "Y");
                }
            } else {
                for (int i = 0; i < parm.getCount(); i++) {
                    parm.setData("CHOOSE", i, "N");
                }
            }
            PAT_TABLE.setParmValue(parm);
        }
    }

    /**
     * ������ѡ��ť�л��¼�
     */
    public void onChangeOpRadio() {
        if (this.getValueString("ALL").equals("Y") || this.getValueString("UNSURGERY").equals("Y")) {
            this.callFunction("UI|HEAL_LEVEL|setEnabled", false);
        } else {
            this.callFunction("UI|HEAL_LEVEL|setEnabled", true);
        }
    }
    
    /**
     * ʹ�ÿ���ҩ��CheckBox״̬�仯�¼�
     */
    public void onChangeAntibioticState() {
        if (this.getValueString("USE_ANTIBIOTIC").equals("Y")) {
            this.callFunction("UI|ANTIBIOTIC_WAY|setEnabled", true);
            onTableShowHead();
        } else {
            this.callFunction("UI|ANTIBIOTIC_WAY|setEnabled", false);
            PAT_TABLE.setHeader("ѡ,30,boolean;��Ժ����,80,Timestamp;��Ժ����,80,Timestamp;��Ժ����,80,DEPT_CODE;������,100;����,80;�Ա�,50,SEX_CODE;����,100;����,50;���1,160;���2,160;���3,160;���4,160;���5,160;����ʷ,160;סԺ�ܷ���,100,double,#########0.00;ҩƷ�ܷ���,100,double,#########0.00;�����ط���,100,double,#########0.00;DDD�ۻ���,100;��Ժ����,80;ʹ�ÿ���ҩƷ,100;����ʹ��,80,YES_NO1;ʹ��״̬,80,YES_NO2;ʱ������,80,boolean;��������,80,boolean;��������,160;�п����,160;�����,100");
        }
    }
    // =================================�����ܿ�ʼ==================================
    /**
     * �����������������
     * 
     * @param table
     */
    public void addListener(final TTable table) {
        table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent mouseevent) {
                int i = table.getTable().columnAtPoint(mouseevent.getPoint());
                int j = table.getTable().convertColumnIndexToModel(i);
                // �������򷽷�;
                // ת�����û���������к͵ײ����ݵ��У�Ȼ���ж�
                if (j == sortColumn) {
                    ascending = !ascending;
                } else {
                    ascending = true;
                    sortColumn = j;
                }
                // table.getModel().sort(ascending, sortColumn);
                // �����parmֵһ��,
                // 1.ȡparamwֵ;
                TParm tableData = table.getParmValue();
                TParm totAmtRow = tableData.getRow(tableData.getCount("CASE_NO") - 1);// add by wanglong 20130108
                tableData.removeRow(tableData.getCount("CASE_NO") - 1);// add by wanglong 20130108
                // System.out.println("tableData:"+tableData);
                tableData.removeGroupData("SYSTEM");
                // 2.ת�� vector����, ��vector ;
                String columnName[] = tableData.getNames("Data");
                String strNames = "";
                for (String tmp : columnName) {
                    strNames += tmp + ";";
                }
                strNames = strNames.substring(0, strNames.length() - 1);
                // System.out.println("==strNames=="+strNames);
                Vector vct = getVector(tableData, "Data", strNames, 0);
                // System.out.println("==vct=="+vct);
                // 3.���ݵ������,��vector����
                // System.out.println("sortColumn===="+sortColumn);
                // ������������;
                String tblColumnName = table.getParmMap(sortColumn);
                // ת��parm�е���
                int col = tranParmColIndex(columnName, tblColumnName);
                // System.out.println("==col=="+col);
                compare.setDes(ascending);
                compare.setCol(col);
                java.util.Collections.sort(vct, compare);
                // ��������vectorת��parm;
                TParm lastResultParm = new TParm();// ��¼���ս��
                lastResultParm = cloneVectoryParam(vct, new TParm(), strNames);// �����м�����
                for (int k = 0; k < columnName.length; k++) {// add by wanglong 20130108
                    lastResultParm.addData(columnName[k], totAmtRow.getData(columnName[k]));
                }
                lastResultParm.setCount(lastResultParm.getCount(columnName[0]));// add by wanglong 20130108
                table.setParmValue(lastResultParm);
            }
        });
    }

    /**
     * ����ת������ֵ
     * 
     * @param columnName
     * @param tblColumnName
     * @return
     */
    private int tranParmColIndex(String columnName[], String tblColumnName) {
        int index = 0;
        for (String tmp : columnName) {
            if (tmp.equalsIgnoreCase(tblColumnName)) {
                // System.out.println("tmp���");
                return index;
            }
            index++;
        }
        return index;
    }

    /**
     * �õ� Vector ֵ
     * 
     * @param group
     *            String ����
     * @param names
     *            String "ID;NAME"
     * @param size
     *            int �������
     */
    private Vector getVector(TParm parm, String group, String names, int size) {
        Vector data = new Vector();
        String nameArray[] = StringTool.parseLine(names, ";");
        if (nameArray.length == 0) {
            return data;
        }
        int count = parm.getCount(group, nameArray[0]);
        if (size > 0 && count > size) count = size;
        for (int i = 0; i < count; i++) {
            Vector row = new Vector();
            for (int j = 0; j < nameArray.length; j++) {
                row.add(parm.getData(group, nameArray[j], i));
            }
            data.add(row);
        }
        return data;
    }

    /**
     * vectoryת��param
     */
    private TParm cloneVectoryParam(Vector vectorTable, TParm parmTable, String columnNames) {
        String nameArray[] = StringTool.parseLine(columnNames, ";");
        // ������;
        for (Object row : vectorTable) {
            int rowsCount = ((Vector) row).size();
            for (int i = 0; i < rowsCount; i++) {
                Object data = ((Vector) row).get(i);
                parmTable.addData(nameArray[i], data);
            }
        }
        parmTable.setCount(vectorTable.size());
        return parmTable;
    }
    // ================================�����ܽ���==================================
}
