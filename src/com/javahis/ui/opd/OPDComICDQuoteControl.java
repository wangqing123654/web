package com.javahis.ui.opd;

import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;
/**
 *
 * <p>Title: 门诊医生工作站常用诊断调用</p>
 *
 * <p>Description:门诊医生工作站常用诊断调用</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company:Javahis</p>
 *
 * @author ehui 20090406
 * @version 1.0
 */
public class OPDComICDQuoteControl extends TControl {
	private String deptCode,drCode,icdType;
	private final static String INIT_SQL="SELECT 'N' AS USE, A.ICD_CODE AS ICD_CODE, B.ICD_CHN_DESC,B.ICD_ENG_DESC ,A.SEQ AS SEQ,A.ICD_TYPE ICD_TYPE,B.PY1 FROM OPD_COMDIAG A ,SYS_DIAGNOSIS B WHERE B.ICD_CODE=A.ICD_CODE";
	TTable table;
	TComboBox dept,operator;
	TTextField query;  //add by huangtt 20150310
	private String icdCode=""; //add by huangtt 20150310 记录回传的诊断码
	public void onInit() {
		super.onInit();
		getInitParam();
		onClear();
	}
	/**
	 * 取得科室代码，用户代码，诊断类别（中西医）
	 */
	public void getInitParam(){
		String temp=TCM_Transform.getString(this.getParameter());
		if(StringUtil.isNullString(temp)){
			this.messageBox_("取得初始化数据失败");
			return;
		}
		String[] param=StringTool.parseLine(temp, ",");
		if(param==null||param.length!=3){
			this.messageBox_("取得初始化数据失败");
			return;
		}
		table=(TTable)this.getComponent("TABLE");
		query= (TTextField)this.getComponent("QUERY");  //add by huangtt 20150310

		table.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
		"onCheckBox");


		deptCode=param[0];
		drCode=param[1];
		icdType=param[2];
		dept=(TComboBox)this.getComponent("DEPT_CODE");
		operator=(TComboBox)this.getComponent("OPERATOR");
		TLabel label=(TLabel)this.getComponent("LABEL");		
		if("1".equalsIgnoreCase(deptCode)){
			operator.setVisible(false);
			dept.setSelectedID(Operator.getDept());

			if("en".equalsIgnoreCase(Operator.getLanguage())){
				label.setEnText("Dept.");
			}else{
				label.setZhText("科室");
			}
		}else{
			dept.setVisible(false);
			operator.setSelectedID(Operator.getID());
			if("en".equalsIgnoreCase(Operator.getLanguage())){
				label.setEnText("Dr.");
			}else{
				label.setZhText("医师");
			}
		}
		//add by huangtt 20150310  start
		TParm parm =new TParm();
		parm.setData("ICD_TYPE", icdType);	
		parm.setData("DEPTORDR_CODE", drCode);	
		
        // 设置弹出菜单  
		query.setPopupMenuParameter("UD", getConfigParm().newConfig("%ROOT%\\config\\opd\\OPDComDiagPopup.x"), parm); 
		query.getPopupMenu().setFocusable(true);
//        // 定义接受返回值方法
		query.addEventListener(TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
		
		//add by huangtt 20150310  end

	}
	
	/**
     * 接受返回值方法  //add by huangtt 20150310
     *
     * @param tag  
     * @param obj
     */
    public void popReturn(String tag, Object obj) {
    	TParm parm = (TParm) obj;
    	icdCode = parm.getValue("ICD_CODE");//清空查询控件
        this.setValue("QUERY", "");
        onClear();
        table.acceptText();
        table.getTable().grabFocus();
        if(table.getRowCount() == 1){
        	table.setItem(0, "USE", "Y");
        }

    }
	
	/**
	 * 清空
	 */
	public void onClear(){
		StringBuffer sb=new StringBuffer(INIT_SQL);
		sb.append(" AND  A.DEPT_OR_DR='").append(deptCode).append("' AND A.DEPTORDR_CODE='").append(drCode).append("' AND A.ICD_TYPE='").append(icdType).append("'");
		
		//add by huangtt 20150310 start
		if(icdCode.length()>0){
			sb.append("  AND  A.ICD_CODE='").append(icdCode).append("'");
		}
		//add by huangtt 20150310 end
		
		sb.append(" ORDER BY A.SEQ,A.ICD_CODE");
//		 System.out.println("icd Quote.sql="+sb.toString());

		TParm parm=new TParm(TJDODBTool.getInstance().select(sb.toString()));
		table.setParmValue(parm);
		if("1".equalsIgnoreCase(deptCode)){
			dept.setValue(Operator.getDept());
		}else{
			dept.setValue(Operator.getID());
		}
		icdCode="";  //add by huangtt 20150310


	}
	/**
	 * CHECK_BOX 点击事件
	 * @param obj
	 */
	public void onCheckBox(Object obj){
		TTable table1=(TTable)obj;
		table1.acceptText();
	}
	/**
	 * 回传事件
	 */
	public void onOk(){
		TParm parm=table.getParmValue();
		int count=table.getRowCount();
		String[] names=parm.getNames();
		int clmCount=names.length;
		TParm result=new TParm();
		boolean yN;
		for(int i=0;i<count;i++){
			yN=TCM_Transform.getBoolean(table.getValueAt(i, 0));
			if(yN){
				for(int j=0;j<clmCount;j++){
					result.addData(names[j], parm.getValue(names[j],i));
				}
			}
		}
		this.setReturnValue(result);
		this.closeWindow();
	}
	/**
	 * 改变科室查询诊断
	 */
	public void onChangeDept(){
		drCode=this.getValueString("DEPT_CODE");
		StringBuffer sb=new StringBuffer(INIT_SQL);
		sb.append(" AND  A.DEPT_OR_DR='").append(deptCode).append("' AND A.DEPTORDR_CODE='").append(drCode).append("' AND A.ICD_TYPE='").append(icdType).append("' ORDER BY A.SEQ,A.ICD_CODE");
		TParm parm=new TParm(TJDODBTool.getInstance().select(sb.toString()));
		table.setParmValue(parm);
	}
    /**
     * 设置语种
     * @param language String
     */
    public void onChangeLanguage(String language)
    {
    	TParm parm=table.getParmValue();
    	if(parm==null){
    		return;
    	}
    	table.setParmValue(parm);
    }
}
