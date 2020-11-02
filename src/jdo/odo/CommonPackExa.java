package jdo.odo;

import java.sql.Timestamp;
import java.util.Vector;

import jdo.pha.PhaBaseTool;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.manager.TIOM_Database;
import com.dongyang.util.TypeTool;
import com.javahis.util.StringUtil;

public class CommonPackExa extends StoreBase {
	/**
	 * sys_fee������
	 */
	TDataStore sysFee = TIOM_Database.getLocalTable("SYS_FEE");

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
	 * Constructor
	 * @param deptOrDr
	 * @param deptOrDrCode
	 * @param packCode
	 */
	public CommonPackExa(String deptOrDr,String deptOrDrCode,String packCode){
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
        return OpdComPackQuoteTool.getInstance().initOrderSQL(OpdComPackQuoteTool.INIT_EXA_SQL,parm);
    }
    /**
     * ��������
     * @param row int
     * @return int
     */
    public int insertRow(int row){
        int newRow = super.insertRow(row);
        if(newRow==-1)
            return -1;
        //�����ż�ס��
        setItem(newRow,"DEPT_OR_DR",getDeptOrDr());
        setItem(newRow,"DEPTORDR_CODE",getDeptOrDrCode());
        setItem(newRow,"PACK_CODE",getPackCode());
        setItem(newRow,"SEQ_NO",this.getMaxSeq()+1);
        setActive(newRow, false);
//        setItem(newRow,"ORDER_DATE",super.getDBTime());
        return newRow;
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
    	 String dept=phaBaseParm.getValue("EXEC_DEPT_CODE");
    	 if(!"PHA".equalsIgnoreCase(sysFee.getValue("CAT1_TYPE"))){
    		 dept=sysFee.getValue("EXEC_DEPT_CODE");
    	 }
    	 //System.out.println("dept=--------------"+dept);
//    	 this.setItem(row, "PRESRT_NO", "");
    	 String orderCat1Code=sysFee.getValue("ORDER_CAT1_CODE");
    	 String cat1Type=sysFee.getValue("CAT1_TYPE");
    	 if("LIS".equalsIgnoreCase(cat1Type)||"RIS".equalsIgnoreCase(cat1Type)){
    		 this.setItem(row, "RX_TYPE", "5");
    	 }
    	 if(this.getItemInt(row, "LINK_NO")<=0){
    		 this.setItem(row, "LINK_NO", "");
    	 }
    	 this.setItem(row, "ORDER_CODE", sysFee.getValue("ORDER_CODE"));
    	 this.setItem(row, "TRADE_ENG_DESC", sysFee.getValue("TRADE_ENG_DESC"));	
    	 this.setItem(row, "MEDI_QTY", 1);
    	 this.setItem(row, "MEDI_UNIT", phaBaseParm.getValue("MEDI_UNIT"));
    	 this.setItem(row, "FREQ_CODE", sysFee.getValue("FREQ_CODE"));
    	 this.setItem(row, "ROUTE_CODE", phaBaseParm.getValue("ROUTE_CODE"));
    	 this.setItem(row, "TAKE_DAYS", phaBaseParm.getInt("TAKE_DAYS"));
    	 this.setItem(row, "GIVEBOX_FLG", phaBaseParm.getValue("GIVEBOX_FLG"));
    	 this.setItem(row, "DESCRIPTION", phaBaseParm.getValue("DESCRIPTION"));
    	 this.setItem(row, "EXEC_DEPT_CODE", dept);
    	 this.setItem(row, "SETMAIN_FLG", sysFee.getValue("SETMAIN_FLG"));
    	 this.setItem(row, "ORDER_DESC", sysFee.getValue("ORDER_DESC"));
    	 this.setItem(row, "SPECIFICATION", sysFee.getValue("SPECIFICATION"));
    	 this.setItem(row, "PRESRT_NO", "0");
    	return true;
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
		return code.indexOf(orderCode)>0;
	}

	/**
	 * �õ�����������
	 * @param parm TParm
	 * @param row int
	 * @param column String
	 * @return Object
	 */
	public Object getOtherColumnValue(TParm parm, int row, String column) {
		if("ORDER_DESC_SPECIFICATION".equalsIgnoreCase(column)){
			TParm sysFeeParm=sysFee.getBuffer(sysFee.isFilter()?sysFee.FILTER:sysFee.PRIMARY);
			Vector orderCode=(Vector)sysFeeParm.getData("ORDER_CODE");
			String code=this.getItemString(row, "ORDER_CODE");
			int rowNow=orderCode.indexOf(code);
			if(rowNow<0)
				return "";
			return sysFeeParm.getValue("ORDER_DESC",rowNow)+"  "+sysFeeParm.getValue("SPECIFICATION",rowNow);
		}
		return "";
	}
	/**
	 * ȡ��󴦷�ǩ�ڵ����Ӻ�
	 * @param order
	 * @return
	 */
	public int getMaxLinkNo(){
		int linkNo=0;
		for(int i=0;i<this.rowCount();i++){
			if(StringUtil.isNullString(this.getItemString(i, "ORDER_CODE")))
				continue;
			int temp=this.getItemInt(i, "LINK_NO");
			if(temp>linkNo)
				linkNo=temp;
		}
		return linkNo;
	}
	/**
	 * �е�ֵ�ı�
	 */
	public boolean setItem(int row, String column, Object value) {
		super.setItem(row, column, value);
		//LINKMAIN_FLG;LINK_NO;ORDER_DESC_SPECIFICATION;MEDI_QTY;MEDI_UNIT;FREQ_CODE;ROUTE_CODE;TAKE_DAYS;EXEC_DEPT_CODE;DESCRIPTION
		if (!(column.equalsIgnoreCase("MEDI_QTY")
				|| column.equalsIgnoreCase("TAKE_DAYS")
				|| column.equalsIgnoreCase("FREQ_CODE")
				|| "ROUTE_CODE".equalsIgnoreCase(column)
				|| "LINKMAIN_FLG".equalsIgnoreCase(column)
				|| "LINK_NO".equalsIgnoreCase(column)
				|| "EXEC_DEPT_CODE".equalsIgnoreCase(column) || "DESCRIPTION"
				.equalsIgnoreCase(column))) {
			return true;
		}
		
		if(TypeTool.getBoolean(this.getItemData(row, "LINKMAIN_FLG"))){
			int linkNo=this.getItemInt(row, "LINK_NO");
			boolean isTakeDay="TAKE_DAYS".equalsIgnoreCase(column);
			for(int i=0;i<this.rowCount();i++){
				int temp=TypeTool.getInt(this.getItemString(i, "LINK_NO"));
				boolean linkM=TypeTool.getBoolean(this.getItemData(i, "LINKMAIN_FLG"));
				if(linkNo!=temp||linkM){
					continue;
				}
				if(isTakeDay){
					this.setItem(i, "TAKE_DAYS", this.getItemData(row, "TAKE_DAYS"));
				}else{
					this.setItem(i, "FREQ_CODE", this.getItemData(row, "FREQ_CODE"));
				}
				if("EXEC_DEPT_CODE".equalsIgnoreCase(column)){
					this.setItem(i, "EXEC_DEPT_CODE", value);
				}
				this.addOtherShowRowList(i);
		}
			
		}
		return true;
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
    	order.setFilter("RX_TYPE !='3'");
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
