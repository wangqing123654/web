package com.javahis.ui.clp;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;

import jdo.clp.BscInfoTool;
import jdo.odi.OdiMainTool;
import jdo.sys.Operator;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TCheckBox;
import com.javahis.system.textFormat.TextFormatCLPDuration;
import com.javahis.system.textFormat.TextFormatSYSCtz;
import com.javahis.util.OrderUtil;

/**
 * <p>Title: �ٴ�·��ģ��</p>
 *
 * <p>Description: �ٴ�·��ģ�� </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author luhai
 * @version 1.0
 */
public class CLPTemplateOrderQuoteControl extends TControl {
    //�ٴ�·����
    private String clncPathCode;
    //ҽ��ѡ��chk�������
    private int selectedColumnIndex=0;
    private String caseNo;//�������
    private String ind_flg;//סԺ�Ƽ� ����ҽ��ҽ��
    private boolean ind_clp_flg = false;//סԺ�Ƽ�-ȡ��ҳǩ���յ�����Ϣ
    private String tag;//ҳǩ��ǣ�����ҩ�ﴫ��
    private String statCode;
    public CLPTemplateOrderQuoteControl() {

    }

    public void onInit() {
        super.onInit();
        TParm inParm = (TParm)this.getParameter();
        this.clncPathCode = inParm.getValue("CLNCPATH_CODE");
        this.setValue("CLNCPATH_CODE",clncPathCode);
        caseNo=inParm.getValue("CASE_NO");//��ǰ�������
        ind_flg=inParm.getValue("IND_FLG");
        ind_clp_flg=inParm.getBoolean("IND_CLP_FLG");
        tag=inParm.getValue("PAGE_FLG");//ҳǩ���  yuml 20141110
        TextFormatCLPDuration combo_schd = (TextFormatCLPDuration) this
		.getComponent("SCHD_CODE");
        statCode=getOdiSysParmData("ODI_STAT_CODE").toString();
        combo_schd.setCaseNo(caseNo);
        combo_schd.setClncpathCode(clncPathCode);
        combo_schd.onQuery();
        this.setValue("SCHD_CODE",inParm.getValue("SCHD_CODE")); //��ǰʱ��
        if (null!=ind_flg &&ind_flg.length()>0) {
        	this.setValue("FEE_TYPE", "4");
		}else{
			this.setValue("FEE_TYPE", "3");
		}
        //Ĭ�ϲ�ѯ
        this.onQuery();
        addLisener();
    }
    /**
     * ��ʼ����������
     */
    private void addLisener(){
        addEventListener("TABLECLPTemplate->" + TTableEvent.CLICKED,
                         "onTableClick");

    }
    /**
     * �����ʱ��
     * @param row int
     */
    public void onTableClick(int row){
        TTable tmpTable=(TTable)this.getComponent("TABLECLPTemplate");
        TParm tableParm = tmpTable.getParmValue();
        int selectedColumn=tmpTable.getSelectedColumn();
        //���ѡ���ʱִ��check����
        if(selectedColumn==selectedColumnIndex){
            TParm rowParm = tableParm.getRow(row);
            String status = rowParm.getValue("ISCHECK");
            if("Y".equals(status)){
                status="N";
            }else{
                status="Y";
            }
        }
        tmpTable.setParmValue(tableParm);
    }

    public void onWindowClose() {
        this.closeWindow();
    }
    /**
     * combo ��actionֱ�����÷���onQuery��ͻ����ʹ��onSchdChange�滻
     */
    public void onSchdChange(){
        onQuery();
    }
    /**
	 * �õ�סԺ����
	 * 
	 * @param key
	 *            String
	 * @return Object
	 */
	public Object getOdiSysParmData(String key) {
		return OdiMainTool.getInstance().getOdiSysParmData(key);
	}
    /**
     * ���ݲ�ѯ
     */
    public void onQuery() {
        String regionCode=Operator.getRegion();
        StringBuffer sqlbf = new StringBuffer();
        sqlbf.append(" SELECT '' AS ISCHECK,A.CLNCPATH_CODE,A.SCHD_CODE,A.ORDER_TYPE,A.ORDER_CODE, A.NOTE, ");
        sqlbf.append(" A.CHKTYPE_CODE,A.ORDER_SEQ_NO, ");
        sqlbf.append(" B.ORDER_DESC || CASE WHEN B.SPECIFICATION IS NOT NULL THEN '(' || B.SPECIFICATION || ')' END AS ORDER_DESC, ");
        sqlbf.append(" A.START_DAY,A.DOSE AS MEDI_QTY, ");
        sqlbf.append(" A.DOSE_UNIT AS MEDI_UNIT,A.FREQ_CODE,A.RBORDER_DEPT_CODE AS DEPTORDR_CODE ");
        sqlbf.append(" FROM  CLP_PACK A ,SYS_FEE B,CLP_CHKUSER C ");
        sqlbf.append(" WHERE A.ORDER_CODE=B.ORDER_CODE AND A.CHKUSER_CODE=C.CHKUSER_CODE AND A.ORDER_FLG='Y' ");
        //===============pangben 2012-5-18
        sqlbf.append(" AND (A.REGION_CODE IS NULL OR A.REGION_CODE='"+regionCode+"' OR A.REGION_CODE='' ) ");
        //===============pangben 2012-5-18 stop
        sqlbf.append(" AND (B.REGION_CODE IS NULL OR B.REGION_CODE='"+regionCode+"') ");
        if (null!=ind_flg&&ind_flg.equals("Y")) {
        	sqlbf.append(" AND A.CLNCPATH_CODE='"+this.clncPathCode+"' AND C.CHKUSER_FLG='N' ");
        //===============yuml s   2014-10-30
		}else if(null!=ind_flg&&ind_flg.equals("N")){
			sqlbf.append(" AND A.CLNCPATH_CODE='"+this.clncPathCode+"' AND C.CHKUSER_FLG='Y' ");
		}else{
			sqlbf.append(" AND A.CLNCPATH_CODE='"+this.clncPathCode+"' ");
		}
      //===============yuml   e    2014-10-30  
       
        String schdCode = this.getValueString("SCHD_CODE"); 
        int startDay=this.getValueInt("START_DAY");//�ڼ���
        if(checkNullAndEmpty(schdCode)){
            sqlbf.append(" AND A.SCHD_CODE='"+schdCode+"' ");
        }
        //==========pangben 2012-6-19 start
        if (startDay!=0) {
        	  sqlbf.append(" AND A.START_DAY='"+startDay+"' ");
		}
      //==========pangben 2012-6-19 stop
        String orderType=getOrderType();
        if(checkNullAndEmpty(orderType)){
            sqlbf.append(" AND A.ORDER_TYPE='"+orderType+"' ");
        }
        if(this.getValueString("FEE_TYPE").equals("1")){
        	 sqlbf.append(" AND B.ORDER_CAT1_CODE = 'PHA_W' ");
    	}else if(this.getValueString("FEE_TYPE").equals("2")){
    		sqlbf.append(" AND B.ORDER_CAT1_CODE != 'PHA_W' ");
    	}
//        System.out.println("sqlbf::::"+sqlbf);
        TParm result=new TParm(TJDODBTool.getInstance().select(sqlbf.append(" ORDER BY A.SEQ ").toString()));
        this.callFunction("UI|TABLECLPTemplate|setParmValue",result);
    }
    /**
     * ����
     */
    public void onOk(){
            TParm returnParm = new TParm();
            TTable table = (TTable)this.getComponent("TABLECLPTemplate");
            table.acceptText();
            TParm tableParm=table.getParmValue();
            for(int i=0;i<tableParm.getCount();i++){
                TParm tableRow=tableParm.getRow(i);
                if(tableRow.getValue("ISCHECK").equals("Y")){
                	//Ҫ�ش���ҽ���Ƿ�ͣ��
            		String sql = "SELECT ACTIVE_FLG FROM SYS_FEE WHERE ORDER_CODE = '"+tableRow.getValue("ORDER_CODE")+"'";
            		TParm activeParm = new TParm(TJDODBTool.getInstance().select(sql));
            		if(activeParm.getValue("ACTIVE_FLG",0).equals("N")){
            			this.messageBox(tableRow.getValue("ORDER_DESC")+",��ͣ�á�");
            			return ;
            		}
            		
            		if(!ind_clp_flg){//סԺ�Ƽ�ȡ������
            			TParm parm = new TParm();
        				parm.setData("FREQ_CODE",tableRow.getValue("FREQ_CODE"));
        				parm.setData("STAT_FLG", tag.equalsIgnoreCase("ST") ? "Y"
        						: "N");
        				TParm result = BscInfoTool.getInstance().checkFreq(parm);
        				if (result.getCount() <= 0) {
        					this.messageBox(tableRow.getValue("ORDER_DESC")+",Ƶ�δ��󲻿��Դ���");
                			return ;// �ع� ������
        				}
                		if(!tag.equals(tableRow.getValue("ORDER_TYPE"))){
                			this.messageBox(tableRow.getValue("ORDER_DESC")+",��"+this.getTagName(tag)+"��");
                			return ;
                		}
            		}           		
            		//yuml   e   20141110   �ж������ҳ���Ƿ���ȷ
//                	System.out.println("123455========"+tableRow);
//                 tableRow.setData("PAGE_FLG",tag);
//                 TParm result=OrderUtil.getInstance().checkPhaAntiOnFech(tableRow);
//                 if (result.getErrCode()<0) {
//						this.messageBox(result.getErrText());
//						return;
//                 }
	                 returnParm.addData("ORDER_CODE",tableRow.getValue("ORDER_CODE"));
	                 returnParm.addData("CLNCPATH_CODE",tableRow.getValue("CLNCPATH_CODE"));
	                 returnParm.addData("SCHD_CODE",tableRow.getValue("SCHD_CODE"));
	                 returnParm.addData("CHKTYPE_CODE",tableRow.getValue("CHKTYPE_CODE"));
	                 returnParm.addData("ORDER_SEQ_NO",tableRow.getValue("ORDER_SEQ_NO"));
	                 returnParm.addData("ORDER_TYPE",tableRow.getValue("ORDER_TYPE"));
                }
            }
            this.setReturnValue(returnParm);
            this.closeWindow();
    }
    /**
     * ȫѡ��ť
     */
    public void chkAll(){
        TTable table = (TTable)this.getComponent("TABLECLPTemplate");
        TCheckBox tbtn = (TCheckBox)this.getComponent("chkAll");
        String isCheck=tbtn.isSelected()?"Y":"";
        TParm tableParm = table.getParmValue();
        for(int i=0;i<tableParm.getCount();i++){
            tableParm.setData("ISCHECK",i,isCheck);
        }
        table.setParmValue(tableParm);
    }
    /**
     * �õ�ҽ������
     * @return String
     */
    private String getOrderType(){
        String orderType="";
        TRadioButton tbAll=(TRadioButton)this.getComponent("rbAll");
        if(tbAll.isSelected()){
            orderType="";
        }
        TRadioButton rbUD=(TRadioButton)this.getComponent("rbUD");//����
        if (rbUD.isSelected()) {
            orderType = "UD";
        }
        TRadioButton rbST=(TRadioButton)this.getComponent("rbST");//��ʱ
        if (rbST.isSelected()) {
            orderType = "ST";
        }
        TRadioButton rbDS=(TRadioButton)this.getComponent("rbDS");//��ʱ
        if (rbDS.isSelected()) {
            orderType = "DS";
        }
        return orderType;
    }
    /**
     * ���ؼ��Ƿ�Ϊ��
     * @param componentName String
     * @return boolean
     */
    private boolean checkComponentNullOrEmpty(String componentName) {
        if (componentName == null || "".equals(componentName)) {
            return false;
        }
        String valueStr = this.getValueString(componentName);
        if (valueStr == null || "".equals(valueStr)) {
            return false;
        }
        return true;
    }

    /**
     * ����Ƿ�Ϊ�ջ�մ�
     * @return boolean
     */
    private boolean checkNullAndEmpty(String checkstr) {
        if (checkstr == null) {
            return false;
        }
        if ("".equals(checkstr)) {
            return false;
        }
        return true;
    }
    /**
     * ��ѯ��������
     */
    public void onStartDay(){
    	if (this.getValue("FEE_TYPE").equals("3")) {
    		ind_flg="N";
		}else if (this.getValue("FEE_TYPE").equals("4")) {
			ind_flg="Y";
		}else{
			ind_flg=null;
		}
    	
    	 onQuery();
    }
    /**
     * ��ȡҳ���ǩ����
       yuml   20141110
     */
    public String getTagName(String tag){
    	String tagName = "";
    	if("ST".equals(tag)){
    		tagName="��ʱҽ��";
    	}else if("UD".equals(tag)){
    		tagName="����ҽ��";
    	}else if("DS".equals(tag)){
    		tagName="��Ժ��ҩҽ��";
    	}else if("IG".equals(tag)){
    		tagName="��ҩ��Ƭҽ��";
    	}
    	return tagName;
    }
}
