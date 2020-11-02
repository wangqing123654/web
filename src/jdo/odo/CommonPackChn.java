package jdo.odo;

import java.sql.Timestamp;
import java.util.Vector;

import jdo.pha.PhaBaseTool;
import jdo.sys.SystemTool;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.manager.TIOM_Database;
import com.dongyang.ui.TTableNode;
import com.dongyang.util.TypeTool;
import com.javahis.util.StringUtil;

/**
 * 
 * <p>Title: ����ҽ������վ����������ҩ</p>
 * 
 * <p>Description:����ҽ������վ����������ҩ</p>
 * 
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 * 
 * <p>Company:Javahis</p>
 * 
 * @author ehui 20090429
 * @version 1.0
 */
public class CommonPackChn extends StoreBase {
	/**
	 * ���ҡ�ҽʦ����
	 */
	private String deptOrDr;
	/**
	 * ���һ�ҽʦ����
	 */
	private String deptOrDrCode;
	/**
	 * ģ�����
	 */
	private String packCode;
	/**
	 * ������
	 */
	private String presrtNo;
	/**
	 * ��������
	 */
	private double mediQty;
	/**
	 * sys_fee������
	 */
	TDataStore sysFee = TIOM_Database.getLocalTable("SYS_FEE");
	/**
	 * �õ����ҡ�ҽʦ����
	 * @return deptOrDr String
	 */
	public String getDeptOrDr() {
		return deptOrDr;
	}
	/**
	 * ���ÿ��ҡ�ҽʦ����
	 * @parm deptOrDr String
	 */
	public void setDeptOrDr(String deptOrDr) {
		this.deptOrDr=deptOrDr;
	}
	/**
	 * �õ����һ�ҽʦ����
	 * @return deptOrDrCode String
	 */
	public String getDeptOrDrCode() {
		return deptOrDrCode;
	}
	/**
	 * ���ÿ��һ�ҽʦ����
	 * @parm deptOrDrCode String
	 */
	public void setDeptOrDrCode(String deptOrDrCode) {
		this.deptOrDrCode=deptOrDrCode;
	}
	/**
	 * �õ�ģ�����
	 * @return packCode String
	 */
	public String getPackCode() {
		return packCode;
	}
	/**
	 * ����ģ�����
	 * @parm packCode String
	 */
	public void setPackCode(String packCode) {
		this.packCode=packCode;
	}
	/**
	 * �õ���ҽ������
	 * @return presrtNo String
	 */
	public String getPresrtNo() {
		return presrtNo;
	}
	/**
	 * ������ҽ������
	 * @parm presrtNo String
	 */
	public void setPresrtNo(String presrtNo) {
		this.presrtNo=presrtNo;
	}
	/**
	 * �õ���������
	 * @return  mediQty double
	 */
	public double getMediQty() {
		return mediQty;
	}
	/**
	 * ����ģ�����
	 * @parm mediQty double
	 */
	public void setMediQty(double mediQty) {
		this.mediQty=mediQty;
	}
	/**
	 * Constructor
	 * @param deptOrDr
	 * @param deptOrDrCode
	 * @param packCode
	 */
	public CommonPackChn(String deptOrDr,String deptOrDrCode,String packCode){
		this.setDeptOrDr(deptOrDr);
		this.setDeptOrDrCode(deptOrDrCode);
		this.setPackCode(packCode);
	}
    /**
     * �õ�SQL
     * @return String
     */
    protected String getQuerySQL(){
    	TParm parm=new TParm();
    	parm.setData("DEPT_OR_DR",this.getDeptOrDr());
    	parm.setData("DEPTORDR_CODE",this.getDeptOrDrCode());
    	parm.setData("PACK_CODE",this.getPackCode());
        return OpdComPackQuoteTool.getInstance().initChnSQL(OpdComPackQuoteTool.INIT_CHN_SQL,parm);
    }
    /**
     * ��������
     * @param row int
     * @return int
     */
    public int insertRow(int row,String rxNo){
        int newRow = super.insertRow(row);
        if(newRow==-1)
            return -1;
        //�����ż�ס��
        setItem(newRow,"DEPT_OR_DR",getDeptOrDr());
        setItem(newRow,"DEPTORDR_CODE",getDeptOrDrCode());
        setItem(newRow,"PACK_CODE",getPackCode());
        setItem(newRow,"RX_TYPE","3");
        setItem(newRow,"PRESRT_NO",rxNo);
        setItem(newRow,"SEQ_NO",this.getMaxSeq()+1);
        setActive(newRow, false);
//        setItem(newRow,"ORDER_DATE",super.getDBTime());
        return newRow;
    }
    /**
     * ���ò����û�
     * @param optUser String �����û�
     * @param optDate Timestamp ����ʱ��
     * @param optTerm String ����IP
     * @return boolean true �ɹ� false ʧ��
     */
    public boolean setOperator(String optUser,Timestamp optDate,String optTerm)
    {
        String storeName = isFilter()?FILTER:PRIMARY;
        int rows[] = getNewRows(storeName);
        return super.setOperator(optUser,optDate,optTerm);
    }
	/**
	 * �жϸ����ORDER_CODE�Ƿ��Ѿ�����
	 * @param orderCode String �����ORDER_CODE
	 * @return boolean true:����,false:û����
	 */
	public boolean isSameOrder(String orderCode){
		TParm parm=this.getBuffer(this.isFilter()?this.FILTER:this.PRIMARY);
		Vector code=(Vector)parm.getData("ORDER_CODE");
		//System.out.println("isSameOrder->code="+code);
		return code.indexOf(orderCode)>-1;
	}
	/**
	 * 
	 * @param rxType ��������
	 * @return String �´���ǩ�� 
	 */
	public boolean newPrsrp() {
		String rxNo = getNewRx();
//		this.setPresrtNo(rxNo);
		if (rxNo == null)
			return false;
		for(int i=0;i<4;i++){
			insertRow(-1,rxNo);
		}
		return true;
	}
	/**
	 * ɾ��һ�Ŵ���
	 * @param rxNo
	 * @return
	 */
	public boolean deletePrsrp(String rxNo){
		if(StringUtil.isNullString(rxNo)){
			return false;
		}
		//System.out.println("rxNo============"+rxNo);
		this.setFilter("PRESRT_NO='" +rxNo+"'");
		this.filter();
		//System.out.println("in deleterxp");
//		this.showDebug();
		int count=this.rowCount();
		//System.out.println("count="+count);
		for(int i=count-1;i>-1;i--){
			this.deleteRow(i);
		}
		//System.out.println("after deleterow deleterxp");
//		this.showDebug();
		for(int i=0;i<4;i++){
			this.insertRow(-1,rxNo);
		}
		//System.out.println("after insertrow deleterxp");
//		this.showDebug();
		return true;
	}
	/**
	 * �õ�ȡ��ԭ��������µĴ�����
	 * @return String
	 */
	public String getNewRx() {
		String rxNo = SystemTool.getInstance().getNo("ALL", "ODO", "RX_NO",
				"RX_NO");
		if (rxNo.length() == 0) {
			//err("ȡ������ʧ��");
			//System.out.println("ȡ������ʧ��");
			return null;
		}
		return rxNo;
	}
    /**
     * ��������SEQ_NO
     * @return result int 
     */
    public int getMaxSeq(){
    	int result=0;
    	int temp=0;
    	int count=this.rowCount();
    	for(int i=0;i<count;i++){
    		temp=this.getItemInt(i, "SEQ_NO");
    		if(temp>=result){
    			result=temp;
    			continue;
    		}
    		
    	}
    	return result;
    }
    /**
	 * Ϊ����ǩcombo�ṩ����
	 * @return String[]
	 */
	public TParm getRxComboData(){
		TParm parm=this.getBuffer(this.isFilter()?this.FILTER:this.PRIMARY);
		//System.out.println("parm==============="+parm);
		int count=parm.getCount("PRESRT_NO");
		//System.out.println("count==============="+count);
		Vector list=new Vector();
		TParm result=new TParm();
		for(int i=0;i<count;i++){
			String tempPrint= parm.getValue("PRESRT_NO",i);
			if(list.indexOf(tempPrint)<0){
				list.add(tempPrint);
			}
		}
		
		if(list.size()<1){

			return result;
		}
		for(int i=0;i<list.size();i++){
			for(int j=i+1;j<list.size();j++){
				Long rxI=Long.parseLong(list.get(i)+"");
				Long rxJ=Long.parseLong(list.get(j)+"");
				if(rxI>rxJ){
					Long temp=rxI;
					list.set(i, "0"+rxJ.toString());
					list.set(j, "0"+rxI.toString());
				}
				
			}
		}
		//System.out.println("list==="+list);
		for(int i=0;i<list.size();i++){
			String s = (String) list.get(i);
			result.addData("ID", s);
			result.addData("NAME", "�� ��" + (i + 1) + "�� ����ǩ");
		}
		result.setCount(list.size());
//		//System.out.println("result======="+result);
		return result;
	}
    /**
     * ��ʼ������ҩ�����á�������
     * @param row
     * @param sysFee
     * @return
     */
    public boolean initOrder(int row,TParm sysFee){
    	 //ȡ��pha_base����
    	 String orderCode=sysFee.getValue("ORDER_CODE");
    	 TParm phaBaseParm=PhaBaseTool.getInstance().selectByOrder(orderCode).getRow(0);
    	 this.setItem(row, "SEQ_NO", this.getMaxSeq()+1);
    	 this.setItem(row, "ORDER_CODE", sysFee.getValue("ORDER_CODE"));
    	 this.setItem(row, "MEDI_QTY", phaBaseParm.getDouble("MEDI_QTY"));
    	 this.setItem(row, "MEDI_UNIT", phaBaseParm.getValue("MEDI_UNIT"));
    	 this.setItem(row, "FREQ_CODE", phaBaseParm.getValue("FREQ_CODE"));
    	 this.setItem(row, "ROUTE_CODE", phaBaseParm.getValue("ROUTE_CODE"));
    	 this.setItem(row, "TAKE_DAYS", phaBaseParm.getInt("TAKE_DAYS"));
    	 this.setItem(row, "GIVEBOX_FLG", phaBaseParm.getValue("GIVEBOX_FLG"));
    	 this.setItem(row, "DESCRIPTION", phaBaseParm.getValue("DESCRIPTION"));
    	 this.setItem(row, "EXEC_DEPT_CODE", phaBaseParm.getValue("EXEC_DEPT_CODE"));
    	 this.setItem(row, "SETMAIN_FLG", sysFee.getValue("SETMAIN_FLG"));
    	 this.setItem(row, "ORDER_DESC", sysFee.getValue("ORDER_DESC"));
    	 this.setItem(row, "SPECIFICATION", sysFee.getValue("SPECIFICATION"));
    	 setPackageTot(this.getItemString(row, "PRESRT_NO"));
    	return true;
    }
    /**
     * ÿ���������������˴����ŵ�������ѭ����ֵ��ÿ��ҩƷ��
     * @param presrtNo ������
     */
    public void setPackageTot(String presrtNo){
    	double packTot=0.0;
    	int count=this.rowCount();
    	for(int i=0;i<count;i++){
    		packTot+=this.getItemDouble(i, "MEDI_QTY");
    	}
    	for(int i=0;i<count;i++){
    		this.setItem(i, "PACKAGE_TOT", packTot);
    	}
    }
    /**
	 * ȡ��TABLE�����ݣ����ݶ����ʱ��Ҫ�Ѿ���4����������
	 * @param orderNo
	 * @return
	 */
	public TParm getTableParm(String orderNo){
		String filter="PRESRT_NO='" +orderNo+"'";
		this.setFilter(filter);
		this.filter();
		//System.out.println("this.rowCount="+this.rowCount());
		TParm parm=this.getBuffer(this.PRIMARY);
		TParm tableParm=new TParm();
		int count=parm.getCount();
		
		for(int i=0;i<count;i++){
			int idx=i%4+1;
			tableParm.addData("ORDER_DESC"+idx, getOrderDesc(parm.getValue("ORDER_CODE",i)));
			tableParm.addData("MEDI_QTY"+idx, parm.getDouble("MEDI_QTY",i));
			tableParm.addData("DCTEXCEP_CODE"+idx, parm.getValue("DCTEXCEP_CODE",i));
		}
		return tableParm;
	}
	/**
	 * ȡ��ҩƷ��
	 * @param orderCode String
	 * @return orderDesc String
	 */
	public String getOrderDesc(String orderCode){
		TParm sysFeeParm=sysFee.getBuffer(sysFee.isFilter()?sysFee.FILTER:sysFee.PRIMARY);
		Vector sysFeeVec=(Vector)sysFeeParm.getData("ORDER_CODE");
		int row=sysFeeVec.indexOf(orderCode);
		Vector desc=(Vector)sysFeeParm.getData("ORDER_DESC");
		//System.out.println("desc="+(desc==null||desc.size()<0));
		if(desc==null||desc.size()<0||row<0){
			return "";
		}
		if(row>-1){
			return sysFeeParm.getValue("ORDER_DESC",row);
		}else{
			return "";
		}
	}
    /**
     * �õ�����������
     * @param parm TParm
     * @param row int
     * @param column String
     * @return Object
     */
    public Object getOtherColumnValue(TParm parm,int row,String column)
    {
    	//System.out.println("herer");
    	if("ORDER_DESC1".equalsIgnoreCase(column)){
    		//System.out.println("column======"+column);
        	//System.out.println("row========="+row);
			int index=row*4+0;
			//System.out.println("orderDesc="+getItemString(index, "ORDER_DESC"));
			return getItemString(index, "ORDER_DESC");
		}
		if("ORDER_DESC2".equalsIgnoreCase(column)){
			int index=row*4+1;
			return getItemString(index, "ORDER_DESC");
		}
		if("ORDER_DESC3".equalsIgnoreCase(column)){
			int index=row*4+2;
			return getItemString(index, "ORDER_DESC");
		}
		if("ORDER_DESC4".equalsIgnoreCase(column)){
			int index=row*4+3;
			return getItemString(index, "ORDER_DESC");
		}
		if("MEDI_QTY1".equalsIgnoreCase(column)){
			
			int index=row*4+0;
			//System.out.println("mediQty="+getItemDouble(index, "MEDI_QTY"));
			return getItemDouble(index, "MEDI_QTY");
		}
		if("MEDI_QTY2".equalsIgnoreCase(column)){
			int index=row*4+1;
			return getItemDouble(index, "MEDI_QTY");
		}
		if("MEDI_QTY3".equalsIgnoreCase(column)){
			int index=row*4+2;
			return getItemDouble(index, "MEDI_QTY");
		}
		if("MEDI_QTY4".equalsIgnoreCase(column)){
			int index=row*4+3;
			return getItemDouble(index, "MEDI_QTY");
		}
		if("DCTEXCEP_CODE1".equalsIgnoreCase(column)){
			int index=row*4+0;
			return getItemString(index, "DCTEXCEP_CODE");
		}
		if("DCTEXCEP_CODE2".equalsIgnoreCase(column)){
			int index=row*4+1;
			return getItemString(index, "DCTEXCEP_CODE");
		}
		if("DCTEXCEP_CODE3".equalsIgnoreCase(column)){
			int index=row*4+2;
			return getItemString(index, "DCTEXCEP_CODE");
		}
		if("DCTEXCEP_CODE4".equalsIgnoreCase(column)){
			int index=row*4+3;
			return getItemString(index, "DCTEXCEP_CODE");
		}
		return "";
    }

    /**
     * �����ﲡ����ʼ��
     * @param odo ODO
     * @return boolean true:��ʼ���ɹ�,false:��ʼ��ʧ��
     */
    public boolean initOdo(ODO odo){
    	if(!this.onQuery())
    		return false;
    	if(odo==null||odo.getDiagrec()==null||odo.getDiagrec().rowCount()<1)
    		return false;
    	OpdOrder order=odo.getOpdOrder();
    	order.setFilter("RX_TYPE =='3'");
    	order.filter();
    	TParm orderParm=order.getBuffer(order.PRIMARY);
    	int count=orderParm.getCount();
    	for(int i=0;i<count;i++){
    		int row=this.insertRow();
    		if(row<0)
    			return false;
    		boolean isMainSet=TypeTool.getBoolean(orderParm.getBoolean("SETMAIN_FLG",i));
    		String groupNo=TypeTool.getString(orderParm.getValue("ORDERSET_CODE",i));
    		if (!isMainSet && !StringUtil.isNullString(groupNo))
				continue;
    		this.setItem(row, "SEQ_NO", this.getMaxSeq()+1);
    		this.setItem(row, "RX_TYPE", orderParm.getValue("RX_TYPE",i));
    		this.setItem(row, "LINKMAIN_FLG", orderParm.getValue("LINKMAIN_FLG",i));
    		this.setItem(row, "LINK_NO", orderParm.getValue("LINK_NO",i));
    		this.setItem(row, "ORDER_CODE", orderParm.getValue("ORDER_CODE",i));
    		this.setItem(row, "TRADE_ENG_DESC", orderParm.getValue("TRADE_ENG_DESC",i));
    		this.setItem(row, "MEDI_QTY", orderParm.getValue("MEDI_QTY",i));
    		this.setItem(row, "MEDI_UNIT", orderParm.getValue("MEDI_UNIT",i));
    		this.setItem(row, "FREQ_CODE", orderParm.getValue("FREQ_CODE",i));
    		this.setItem(row, "ROUTE_CODE", orderParm.getValue("ROUTE_CODE",i));
    		this.setItem(row, "TAKE_DAYS", orderParm.getValue("TAKE_DAYS",i));
    		this.setItem(row, "GIVEBOX_FLG", orderParm.getValue("GIVEBOX_FLG",i));
    		this.setItem(row, "DESCRIPTION", orderParm.getValue("DR_NOTE",i));
    		this.setItem(row, "EXEC_DEPT_CODE", orderParm.getValue("EXEC_DEPT_CODE",i));
    		this.setItem(row, "SETMAIN_FLG", orderParm.getValue("SETMAIN_FLG",i));
    		this.setItem(row, "DCTAGENT_CODE", orderParm.getValue("DCTAGENT_CODE",i));
    		this.setItem(row, "DCTEXCEP_CODE", orderParm.getValue("DCTEXCEP_CODE",i));
    		this.setItem(row, "DCT_TAKE_QTY", orderParm.getValue("DCT_TAKE_QTY",i));
    		this.setItem(row, "PACKAGE_TOT", orderParm.getValue("PACKAGE_TOT",i));
    		this.setItem(row, "ORDER_DESC", orderParm.getValue("ORDER_DESC",i));
    		this.setItem(row, "SPECIFICATION", orderParm.getValue("SPECIFICATION",i));
    		this.setItem(row, "PRESRT_NO", orderParm.getValue("RX_NO",i));
    	}
    	order.insertRow();
    	return true;
    }
    /**
     * ����ģ���ɾ��ģ��
     * @param packCode
     * @return
     */
    public boolean deletePack(String packCode){
    	if(StringUtil.isNullString(packCode)){
    		return false;
    	}
    	this.setFilter("PACK_CODE='" +packCode+
    			"'");
    	this.filter();
    	for(int i=this.rowCount()-1;i>-1;i--){
    		this.deleteRow(i);
    	}
    	return true;
    }
}
