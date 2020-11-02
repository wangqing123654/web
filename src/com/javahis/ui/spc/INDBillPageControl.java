package com.javahis.ui.spc;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import jdo.spc.INDBillPageTool;
import jdo.spc.INDSQL;
import jdo.sys.Operator;
import jdo.util.Manager;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;
/**
 *
 * <strong>Title : INDBillPageControl<br></strong>
 * <strong>Description : </strong>ҩ����ҳ<br>
 * <strong>Create on : 2012-1-25<br></strong>
 * <p>
 * <strong>Copyright (C) <br></strong>
 * <p>
 * @author luhai<br>
 * @version <strong>BlueCore</strong><br>
 * <br>
 * <strong>�޸���ʷ:</strong><br>
 * �޸���		�޸�����		�޸�����<br>
 * -------------------------------------------<br>
 * <br>
 * <br>
 */
public class INDBillPageControl extends TControl {
	//ҳ��ؼ�
	private TComboBox orgCombo;
	private TTextFormat startDate;
	private TTextFormat endDate;
	private TTable medTable;
	private TTable billTable;
	private TTextField orderCode;
	private TTextField orderDesc;
	private TTextField orderDescription;
	private TTextField orderUnitDesc;
	//���ڸ�ʽ��
	private SimpleDateFormat formateDate=new SimpleDateFormat("yyyy/MM/dd");
	//regionCode
	private String regionCode=Operator.getRegion();
	//��ҳ����
	private List<BillPageBean> billPageList;
	
	private final static String LAST_AMT_DESC = "������";
	private final static String CURRENT_AMT_DESC = "���½��";
    /**
     * ��ʼ������
     */
    public void onInit() {
//    	System.out.println("ϵͳ��ʼ��");
    	initComponent();
    	initPage();
//    	System.out.println("ϵͳ��ʼ�����");
    }
    /**
     *
     * ��ʼ���ؼ�Ĭ��ֵ
     */
    public void initPage(){
    	//���ÿ���������ֵbegin
//    	this.orderCode.setse
        TParm parmCbo = new TParm(TJDODBTool.getInstance().select(INDSQL.getIndOrgComobo("A","",Operator.getRegion())));
        this.orgCombo.setParmValue(parmCbo);
    	//���ÿ���������ֵend
		// ���´���ʼʱ��ͽ���ʱ�� begin luhai 2011-12-07
		Calendar cd = Calendar.getInstance();
		Calendar cdto = Calendar.getInstance();
		cd.add(Calendar.MONTH, -1);
		cd.set(Calendar.DAY_OF_MONTH, 26);
		cdto.set(Calendar.DAY_OF_MONTH, 25);
		String format = formateDate.format(cd.getTime());
		this.startDate.setValue(formateDate.format(cd.getTime()));
		this.endDate.setValue(formateDate.format(cdto.getTime()));
		// ���´���ʼʱ��ͽ���ʱ�� begin luhai 2011-12-07
		//��ʼ��Ĭ��ҩ��
		orgCombo.setSelectedIndex(1);
		//��ʼ��ҩƷ�б������ begin
		initMedTable(orgCombo.getValue(),this.regionCode);
		//��ʼ��ҩƷ�б������ end
		//��ʼ��ҽ����textField
        //ֻ��text���������������sys_fee������
		TParm parm = new TParm();
		parm.setData("RX_TYPE", 1);
        callFunction("UI|ORDER_CODE|setPopupMenuParameter", "ORDER",
                     "%ROOT%\\config\\sys\\SYSFeePopup.x",parm);

        //textfield���ܻش�ֵ
        callFunction("UI|ORDER_CODE|addEventListener",
                     TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
    }
    public void popReturn(String tag, Object obj) {
        TParm parmrtn = (TParm) obj;
        this.setValue("ORDER_CODE", parmrtn.getValue("ORDER_CODE"));
        this.setValue("ORDER_DESC", parmrtn.getValue("ORDER_DESC"));
        //����ҩƷ�������Ϣ
    	TParm parm = new TParm();
    	parm.setData("ORDER_CODE",parmrtn.getValue("ORDER_CODE"));
    	parm.setData("ORG_CODE",this.orgCombo.getValue());
    	parm.setData("REGION_CODE",regionCode);
    	TParm selectSysFeeMed = INDBillPageTool.getInstance().selectSysFeeMed(parm);
    	if(selectSysFeeMed.getCount("ORDER_CODE")<=0){
    		return;
    	}
    	String orderCode = selectSysFeeMed.getValue("ORDER_CODE",0);
    	String orderDesc=selectSysFeeMed.getValue("ORDER_DESC",0);
    	String orderDescription=selectSysFeeMed.getValue("SPECIFICATION",0);
    	String orderUnitDesc=selectSysFeeMed.getValue("UNIT_CHN_DESC",0);
    	this.orderCode.setValue(orderCode);
    	this.orderDesc.setValue(orderDesc);
    	this.orderDescription.setValue(orderDescription);
    	this.orderUnitDesc.setValue(orderUnitDesc);

    }
    public void queryMedList(){
    	String orgValue = orgCombo.getValue();
    	if("".equals(orgValue)){
    		this.messageBox("��ѡ���ѯҩ�⣡");
    		return ;
    	}
		//��ʼ��ҩƷ�б������ begin
		initMedTable(orgCombo.getValue(),this.regionCode);
		//��ʼ��ҩƷ�б������ end
    }
    /**
     *
     * ��ѯ��ҳ����ϸ��Ϣ
     * @throws ParseException
     */
    public void onQuery() throws ParseException{
//    	StringBuffer sqlbf = new StringBuffer("");
//    	sqlbf.append(" select ORDER_CODE from  sys_fee where  (order_code like 'Y01%'  OR order_code like 'Y02%' or order_code like 'Y03%')  ");
//    	Map select = TJDODBTool.getInstance().select(sqlbf.toString());
//    	TParm resultsorce=new TParm(select);
//    	for(int i=0;i<resultsorce.getCount("ORDER_CODE");i++){
//    		String orderCode=resultsorce.getValue("ORDER_CODE",i);
//    		StringBuffer sqlbforder=new StringBuffer();
//    		sqlbforder.append(" SELECT B.CHARGE_HOSP_CODE FROM SYS_ORDERSETDETAIL A,SYS_FEE B WHERE A.ORDER_CODE=B.ORDER_CODE AND  ORDERSET_CODE='"+orderCode+"' ");
//    		Map selectcharge = TJDODBTool.getInstance().select(sqlbforder.toString());
//    		TParm chargeCodeParm = new TParm(selectcharge);
//    		String chargeCode=chargeCodeParm.getValue("CHARGE_HOSP_CODE",0);
//    		System.out.println("chargeCode:"+chargeCode);
//    		StringBuffer updateSql=new StringBuffer();
//    		updateSql.append(" update sys_fee set CHARGE_HOSP_CODE='"+chargeCode+"' where order_code='"+orderCode+"'");
//    		System.out.println(updateSql);
//    		Map update = TJDODBTool.getInstance().update(updateSql.toString());
//    		TParm result = new TParm (update);
//    		if(result.getErrCode()<=0){
//    			System.out.println("error"+result.getErrText());
//    		}else {
//    			System.out.println("00000000ok");
//    		}
//    		updateSql.delete(0, updateSql.toString().length());
//    		updateSql.append(" update sys_fee_history set CHARGE_HOSP_CODE='"+chargeCode+"' where order_code='"+orderCode+"'");
//    		System.out.println(updateSql);
//    		update = TJDODBTool.getInstance().update(updateSql.toString());
//    		result = new TParm (update);
//    		if(result.getErrCode()<=0){
//    			System.out.println("error"+result.getErrText());
//    		}else {
//    			System.out.println("00000000ok");
//    		}
//    	}
    	String orgCode=this.orgCombo.getValue();
    	String orderCode=this.orderCode.getValue();
    	if("".equals(orgCode)){
    		this.messageBox("��ѡ���ѯ���ţ�");
    		return;
    	}
    	if("".equals(orderCode)){
    		this.messageBox("�������ѯҩƷ��");
    		return;
    	}
    	if("".equals(startDate.getValue())){
    		this.messageBox("�����뿪ʼʱ�䣡");
    		return;
    	}
    	if("".equals(endDate.getValue())){
    		this.messageBox("���������ʱ�䣡");
    		return;
    	}
    	initBillDetailTable(orgCode,orderCode,this.regionCode);
    }
    /**
     *
     * ҩƷ�б�click�¼�
     */
    public void onMedTableClick(){
    	int selectedIndx=this.medTable.getSelectedRow();
    	if(selectedIndx<0){
    		return;
    	}
    	TParm tableparm=this.medTable.getParmValue();
    	//��ҳ���ϴ���ҩƷ���������
    	String orderCode = tableparm.getValue("ORDER_CODE",selectedIndx);
    	String orderDesc=tableparm.getValue("ORDER_DESC",selectedIndx);
    	String orderDescription=tableparm.getValue("SPECIFICATION",selectedIndx);
    	String orderUnitDesc=tableparm.getValue("UNIT_CHN_DESC",selectedIndx);
    	this.orderCode.setValue(orderCode);
    	this.orderDesc.setValue(orderDesc);
    	this.orderDescription.setValue(orderDescription);
    	this.orderUnitDesc.setValue(orderUnitDesc);
    }
    /**
     * ��ӡ
     */
    public void onPrint() {
        if (this.billTable.getRowCount() <= 0) {
            this.messageBox("û��Ҫ��ӡ������");
            return;
        }
        TParm prtParm = new TParm();
        //��ͷ
        prtParm.setData("TITLE", "TEXT",Manager.getOrganization().
                getHospitalCHNFullName(Operator.getRegion()) +
                        "ҩ����ҳ");
        String startDate = this.startDate.getValue().toString();
        String endDate = this.endDate.getValue().toString();
            startDate = startDate.substring(0, 10).replace("-", "/");
            endDate = endDate.substring(0, 10).replace("-", "/");
        prtParm.setData("START_DATE", "TEXT",
                        startDate);
        prtParm.setData("END_DATE", "TEXT", endDate);
        prtParm.setData("UNIT", "TEXT", this.orderUnitDesc.getValue());
        prtParm.setData("MED_NAME", "TEXT", this.orderDesc.getValue());
        prtParm.setData("TYPE", "TEXT", this.orderDescription.getValue());
        prtParm.setData("UNIT", "TEXT", this.orderUnitDesc.getValue());
        TParm tableparm = this.billTable.getParmValue();
        tableparm.setCount(tableparm.getCount("DESC"));
        //����������
        tableparm.addData("SYSTEM", "COLUMNS", "BILL_DATE");
        tableparm.addData("SYSTEM", "COLUMNS", "BILL_NO");
        tableparm.addData("SYSTEM", "COLUMNS", "DESC");
        tableparm.addData("SYSTEM", "COLUMNS", "IN_NUM");
        tableparm.addData("SYSTEM", "COLUMNS", "IN_PRICE");
        tableparm.addData("SYSTEM", "COLUMNS", "IN_AMT");
        tableparm.addData("SYSTEM", "COLUMNS", "OUT_NUM");
        tableparm.addData("SYSTEM", "COLUMNS", "OUT_PRICE");
        tableparm.addData("SYSTEM", "COLUMNS", "OUT_AMT");
        tableparm.addData("SYSTEM", "COLUMNS", "LAST_NUM");
        tableparm.addData("SYSTEM", "COLUMNS", "LAST_PRICE");
        tableparm.addData("SYSTEM", "COLUMNS", "LAST_AMT");
        prtParm.setData("TABLE", tableparm.getData());
        //��β
        prtParm.setData("USER", "TEXT", "�Ʊ��ˣ�" + Operator.getName());
        this.openPrintWindow("%ROOT%\\config\\prt\\IND\\INDBillPage.jhw",
                             prtParm);
    }
    public void onExport(){
        if (billTable.getRowCount() <= 0) {
            this.messageBox("û�л������");
            return;
        }
        ExportExcelUtil.getInstance().exportExcel(billTable,
            "ҩ����ҳ");

    }
    /**
     * ��ʼ����ҳ��ϸ��
     *
     * @param orgCode
     * @param regionCode
     * @throws ParseException
     */
    private void initBillDetailTable(String orgCode,String orderCode,String regionCode) throws ParseException{
    	TParm parm = new TParm();
    	parm.setData("ORG_CODE",orgCode);
    	parm.setData("REGION_CODE",regionCode);
    	parm.setData("ORDER_CODE",orderCode);
    	parm.setData("START_DATE",(startDate.getValue()+"").substring(0,10).replace("-", "")+"000000");
    	parm.setData("END_DATE",(endDate.getValue()+"").substring(0,10).replace("-", "")+"235959");
    	this.billPageList=new ArrayList<INDBillPageControl.BillPageBean>();
    	//�õ�����parm
    	TParm dispenseINParm = INDBillPageTool.getInstance().selectDispenseIN(parm);
    	this.billPageList.addAll(getBillPageBeanList(dispenseINParm));
    	//�õ������parm
    	TParm dispenseOUTParm = INDBillPageTool.getInstance().selectDispenseOUT(parm);
    	this.billPageList.addAll(getBillPageBeanList(dispenseOUTParm));
    	//�õ����յ�parm
    	TParm verifyinParm=INDBillPageTool.getInstance().selectVerifyin(parm);
    	this.billPageList.addAll(getBillPageBeanList(verifyinParm));
    	//�õ��˻���Parm
    	TParm regressParm=INDBillPageTool.getInstance().selectRegress(parm);
    	this.billPageList.addAll(getBillPageBeanList(regressParm));
    	//����ʱ������biiPageList
    	Collections.sort(billPageList,new Comparator<BillPageBean>(){
    		   public int compare(BillPageBean o1, BillPageBean o2) {
    			   long time1 = o1.getBillDate().getTime();
    			   long time2 = o2.getBillDate().getTime();
    			   long diff= time1-time2;
    		       int diffint = (int) diff;
    		       return diffint;
    		    }
    		});
    	//�����ۼƺ��ܼ� begin
    	List<BillPageBean> newBillPageBeanList = new ArrayList<BillPageBean>();
    	BillPageBean eachMonth=new BillPageBean();
    	eachMonth.setDesc("�ϼ�");
    	BillPageBean totalBean= new BillPageBean();
    	totalBean.setDesc("�ۼ�");
    	Date lastDate=null;
    	for (BillPageBean billPage : billPageList) {
    		// identify by shendr 20131231 ͳһ����С�����7λ���ų���һ��Ǯ������
    		eachMonth.setInAmt(StringTool.round(eachMonth.getInAmt()+billPage.getInAmt(),7));
    		eachMonth.setOutAmt(StringTool.round(eachMonth.getOutAmt()+billPage.getOutAmt(),7));
    		totalBean.setInAmt(StringTool.round(totalBean.getInAmt()+billPage.getInAmt(),7));
    		totalBean.setOutAmt(StringTool.round(totalBean.getOutAmt()+billPage.getOutAmt(),7));
    		newBillPageBeanList.add(billPage);
    		if(lastDate!=null&&lastDate.getMonth()!=billPage.getBillDate().getMonth()){
    	    	BillPageBean tmpEachMonth=new BillPageBean();
    	    	tmpEachMonth.setDesc("�ϼ�");
    	    	tmpEachMonth.setInAmt(eachMonth.getInAmt());
    	    	tmpEachMonth.setOutAmt(eachMonth.getOutAmt());
    	    	BillPageBean tmpTotalBean= new BillPageBean();
    	    	tmpTotalBean.setDesc("�ۼ�");
    	    	tmpTotalBean.setInAmt(totalBean.getInAmt());
    	    	tmpTotalBean.setOutAmt(totalBean.getOutAmt());
    			newBillPageBeanList.add(tmpEachMonth);
    			newBillPageBeanList.add(tmpTotalBean);
    			//��պϼ�
    			eachMonth.setInAmt(0);
    			eachMonth.setOutAmt(0);
    		}
    		lastDate=billPage.getBillDate();
		}
    	//�б�������ϼ���begin
    	BillPageBean tmpEachMonth=new BillPageBean();
    	tmpEachMonth.setDesc("�ϼ�");
    	tmpEachMonth.setInAmt(eachMonth.getInAmt());
    	tmpEachMonth.setOutAmt(eachMonth.getOutAmt());
    	BillPageBean tmpTotalBean= new BillPageBean();
    	tmpTotalBean.setDesc("�ۼ�");
    	tmpTotalBean.setInAmt(totalBean.getInAmt());
    	tmpTotalBean.setOutAmt(totalBean.getOutAmt());
		newBillPageBeanList.add(tmpEachMonth);
		newBillPageBeanList.add(tmpTotalBean);
    	//�б�������ϼ���end
    	this.billPageList=newBillPageBeanList;
    	//�����ʱ����
    	newBillPageBeanList=null;
    	//�����ۼƺ��ܼ� end
    	//����������
    	BillPageBean lastYear = new BillPageBean();
    	Date startDate = this.formateDate.parse((this.startDate.getValue()+"").substring(0,10).replace("-", "/"));
    	Calendar lastYearCd=Calendar.getInstance();
    	lastYearCd.setTime(startDate);
    	lastYearCd.add(Calendar.DAY_OF_YEAR, -1);
    	lastYear.setBillDate(new Timestamp(lastYearCd.getTimeInMillis()));
    	lastYear.setDesc(this.LAST_AMT_DESC);
    	TParm selectParm = new TParm();
    	selectParm.setData("ORDER_CODE",this.orderCode.getValue());
    	selectParm.setData("ORG_CODE",this.orgCombo.getValue());
    	selectParm.setData("TRANDATE",this.formateDate.format(lastYearCd.getTime()));
    	TParm selectStockQty = INDBillPageTool.getInstance().selectStockQty(selectParm);
    	//����Ĭ��ֵ by liyh 20120823 ��Ϧ ���Ϊ��� Ĭ��ֵΪ0
    	int defaultNum = 0;
    	if(null != selectStockQty && selectStockQty.getCount("STOCK_QTY")>0){
    		lastYear.setLastNum(selectStockQty.getInt("STOCK_QTY",0));
//    		lastYear.setLastPrice(selectStockQty.getDouble("VERIFYIN_PRICE",0));
    		lastYear.setLastAmt(StringTool.round(selectStockQty.getDouble("STOCK_AMT",0),7));
    	}else{//by liyh 20120823 ��Ϧ ���Ϊ��� Ĭ��ֵΪ0
    		lastYear.setLastNum(defaultNum);
//    		lastYear.setLastPrice(selectStockQty.getDouble("VERIFYIN_PRICE",0));
    		lastYear.setLastAmt(defaultNum);
    	}
    	billPageList.add(0,lastYear);
    	//���뱾�½��
    	BillPageBean currentMonth = new BillPageBean();
    	Date endDate = this.formateDate.parse((this.endDate.getValue()+"").substring(0,10).replace("-", "/"));
    	Calendar endMonthCd=Calendar.getInstance();
    	endMonthCd.setTime(endDate);
    	currentMonth.setBillDate(new Timestamp(endMonthCd.getTimeInMillis()));
    	currentMonth.setDesc(this.CURRENT_AMT_DESC);
    	selectParm = new TParm();
    	selectParm.setData("ORDER_CODE",this.orderCode.getValue());
    	selectParm.setData("ORG_CODE",this.orgCombo.getValue());
    	selectParm.setData("TRANDATE",this.formateDate.format(endMonthCd.getTime()));
    	selectStockQty = INDBillPageTool.getInstance().selectStockQty(selectParm);
    	if(null != selectStockQty && selectStockQty.getCount("STOCK_QTY")>0){
    		currentMonth.setLastNum(selectStockQty.getInt("STOCK_QTY",0));
//    		currentMonth.setLastPrice(selectStockQty.getDouble("VERIFYIN_PRICE",0));
    		currentMonth.setLastAmt(StringTool.round(selectStockQty.getDouble("STOCK_AMT",0),7));
    	}else{//by liyh 20120823 ��Ϧ ���Ϊ��� Ĭ��ֵΪ0
    		currentMonth.setLastNum(defaultNum);
//    		currentMonth.setLastPrice(selectStockQty.getDouble("VERIFYIN_PRICE",0));
    		currentMonth.setLastAmt(defaultNum);
    	}
    	billPageList.add(currentMonth);
//    	this.medTable.setParmValue(selectSysFeeMed);
//    	System.out.println("��ѯ����list-size��"+billPageList.size());
    	//��listת����Tparm�������ۼ���Ϣ
    	TParm tableParm = getTParmFromBeanList(billPageList);
    	this.billTable.setParmValue(tableParm);
    }
    private List<BillPageBean> getBillPageBeanList(TParm parm){
    	List<BillPageBean> listBean = new ArrayList<INDBillPageControl.BillPageBean>();
    	for(int i=0;i<parm.getCount();i++){
    		BillPageBean billBean = new BillPageBean();
    		String type=parm.getValue("TYPE_CODE",i);
    		billBean.setBillDate(parm.getTimestamp("IN_DATE",i));
    		billBean.setBillNo(parm.getValue("IND_NO",i));
    		if("REGRESS".equals(type)||"DEP".equals(type)||"WAS".equals(type)||"THO".equals(type)||"COS".equals(type)){
    			billBean.setOutPrice(parm.getDouble("VERIFYIN_PRICE",i));
    			billBean.setOutNum(parm.getDouble("QTY",i));
    			billBean.setOutAmt(parm.getDouble("VERIFYIN_AMT",i));
    		}
    		if("VERIFY".equals(type)||"RET".equals(type)||"THI".equals(type)){
    			billBean.setInPrice(parm.getDouble("VERIFYIN_PRICE",i));
    			billBean.setInNum(parm.getDouble("QTY",i));
    			billBean.setInAmt(parm.getDouble("VERIFYIN_AMT",i));
    		}
    		billBean.setDesc(type);
    		//desc
    		if("REGRESS".equals(type)){
    			billBean.setDesc("�˻�");
    		}
    		if("DEP".equals(type)){
    			billBean.setDesc("����");
    		}
    		if("WAS".equals(type)){
    			billBean.setDesc("���");
    		}
    		if("THO".equals(type)){
    			billBean.setDesc("��������");
    		}
    		if("REGRESS".equals(type)){
    			billBean.setDesc("�˻�");
    		}
    		if("VERIFY".equals(type)){
    			billBean.setDesc("����");
    		}
    		if("COS".equals(type)){
    			billBean.setDesc("���Ĳ�����");
    		}
    		if("RET".equals(type)){
    			billBean.setDesc("�˿�");
    		}
    		if("THI".equals(type)){
    			billBean.setDesc("�������");
    		}
    		listBean.add(billBean);
    	}
    	return listBean;
    }
    /**
     *
     * ��beanת����TParm
     * @return
     */
    private TParm getTParmFromBeanList(List<BillPageBean> billPageBeanList){
    	TParm tableParm = new TParm();
    	for (BillPageBean billPageBean : billPageBeanList) {
    		String billDate="";
    		if(billPageBean.getBillDate()!=null){
    			billDate=this.formateDate.format(billPageBean.getBillDate());
    		}
    		tableParm.addData("BILL_DATE",billDate);
    		tableParm.addData("BILL_NO", billPageBean.getBillNo());
    		tableParm.addData("DESC", billPageBean.getDesc());
    		tableParm.addData("IN_NUM", nullToEmptyStr(billPageBean.getInNum()));
    		tableParm.addData("IN_PRICE", nullToEmptyStr(billPageBean.getInPrice()));
    		tableParm.addData("IN_AMT", nullToEmptyStr(billPageBean.getInAmt()));
    		tableParm.addData("OUT_NUM", nullToEmptyStr(billPageBean.getOutNum()));
    		tableParm.addData("OUT_PRICE", nullToEmptyStr(billPageBean.getOutPrice()));
    		tableParm.addData("OUT_AMT", nullToEmptyStr(billPageBean.getOutAmt()));
    		if(this.LAST_AMT_DESC.equals(billPageBean.getDesc()) || this.CURRENT_AMT_DESC.equals(billPageBean.getDesc())){//by liyh 20120823 ��Ϧ ���Ϊ��� Ĭ��ֵΪ0
    			tableParm.addData("LAST_NUM", nullToZero(nullToEmptyStr(billPageBean.getLastNum())));
    		}else{
    			tableParm.addData("LAST_NUM", nullToEmptyStr(billPageBean.getLastNum()));
    		}
    		tableParm.addData("LAST_PRICE", nullToEmptyStr(billPageBean.getLastPrice()));
    		if(this.LAST_AMT_DESC.equals(billPageBean.getDesc()) || this.CURRENT_AMT_DESC.equals(billPageBean.getDesc())){//by liyh 20120823 ��Ϧ ���Ϊ��� Ĭ��ֵΪ0
    			tableParm.addData("LAST_AMT", nullToZero(nullToEmptyStr(billPageBean.getLastAmt())));
    		}else{
    			tableParm.addData("LAST_AMT", nullToEmptyStr(billPageBean.getLastAmt()));
    		}
		}
    	return tableParm;
    }
    private String nullToEmptyStr(double num){
    	if((""+num).equals("0.0")){
    		return "";
    	}else{
    		return num+"";
    	}
    }
    
    private String nullToZero(String num){
    	if(null == num || "".equals(num)){
    		return "0";
    	}else{
    		return num;  
    	}
    }
                                   
    /**
     * ���                                                                                                                 
     */
    private void onClear(){ 
    	billTable.removeAll();
    }
    
    /**
     * ��ʼ��ҩƷ�б�
     * ��������
     * @param orgCode
     * @param regionCode
     */
    private void initMedTable(String orgCode,String regionCode){
    	TParm parm = new TParm();
    	parm.setData("ORG_CODE",orgCode);
    	parm.setData("REGION_CODE",regionCode);
    	TParm selectSysFeeMed = INDBillPageTool.getInstance().selectSysFeeMed(parm);
    	this.medTable.setParmValue(selectSysFeeMed);
    }
    /**
     *
     * ��ʼ��ҳ��ؼ����ڳ������
     */
    private void initComponent(){
    	orgCombo=(TComboBox)this.getComponent("ORG_COMB0");
    	startDate=(TTextFormat)this.getComponent("START_DATE");
    	endDate=(TTextFormat)this.getComponent("END_DATE");
    	this.medTable=(TTable)this.getComponent("MED_TABLE");
    	this.orderCode=(TTextField)this.getComponent("ORDER_CODE");
    	this.orderDesc=(TTextField)this.getComponent("ORDER_DESC");
    	this.orderDescription=(TTextField)this.getComponent("DESCRIPTION");
    	this.orderUnitDesc=(TTextField)this.getComponent("ORDER_UNIT_DESC");
    	this.billTable=(TTable)this.getComponent("BIL_TABLE");
    }
    class BillPageBean{
    	private Timestamp billDate;
    	private String billNo;
    	private String desc;
    	private double inNum;
    	private double inPrice;
    	private double inAmt;
    	private double outNum;
    	private double outPrice;
    	private double outAmt;
    	private double lastNum;
    	private double lastPrice;
    	private double lastAmt;
		public Timestamp getBillDate() {
			return billDate;
		}
		public void setBillDate(Timestamp billDate) {
			this.billDate = billDate;
		}
		public String getBillNo() {
			return billNo;
		}
		public void setBillNo(String billNo) {
			this.billNo = billNo;
		}
		public String getDesc() {
			return desc;
		}
		public void setDesc(String desc) {
			this.desc = desc;
		}
		public double getInNum() {
			return inNum;
		}
		public void setInNum(double inNum) {
			this.inNum = inNum;
		}
		public double getInPrice() {
			return inPrice;
		}
		public void setInPrice(double inPrice) {
			this.inPrice = inPrice;
		}
		public double getInAmt() {
			return inAmt;
		}
		public void setInAmt(double inAmt) {
			this.inAmt = inAmt;
		}
		public double getOutNum() {
			return outNum;
		}
		public void setOutNum(double outNum) {
			this.outNum = outNum;
		}
		public double getOutPrice() {
			return outPrice;
		}
		public void setOutPrice(double outPrice) {
			this.outPrice = outPrice;
		}
		public double getOutAmt() {
			return outAmt;
		}
		public void setOutAmt(double outAmt) {
			this.outAmt = outAmt;
		}
		public double getLastNum() {
			return lastNum;
		}
		public void setLastNum(double lastNum) {
			this.lastNum = lastNum;
		}
		public double getLastPrice() {
			return lastPrice;
		}
		public void setLastPrice(double lastPrice) {
			this.lastPrice = lastPrice;
		}
		public double getLastAmt() {
			return lastAmt;
		}
		public void setLastAmt(double lastAmt) {
			this.lastAmt = lastAmt;
		}

    }
}
