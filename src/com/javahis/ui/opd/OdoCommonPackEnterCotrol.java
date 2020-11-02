package com.javahis.ui.opd;

import java.sql.Timestamp;

import jdo.odo.CommonPackDiag;
import jdo.odo.CommonPackMain;
import jdo.odo.CommonPackOrder;
import jdo.odo.Diagrec;
import jdo.odo.ODO;
import jdo.odo.OpdComPackQuoteTool;
import jdo.odo.OpdOrder;
import jdo.odo.Subjrec;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.StringUtil;
/**
 *
 * <p>Title: 门诊医生工作站保存组套输入组套名称</p>
 *
 * <p>Description:门诊医生工作站保存组套输入组套名称控制类</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company:JavaHis</p>
 *
 * @author ehui 20090506
 * @version 1.0
 */
public class OdoCommonPackEnterCotrol extends TControl {
	//保存的病历数据
	ODO odo;
	//新生成的PACK_CODE
	private String packCode;
	private String DR="2";
	private String[] sql;
	//模板代码combo
	private TComboBox combo;

	/**
	 * 初始化
	 */
	public void onInit() {
		super.onInit();
		Object obj=this.getParameter();
		TParm parm=new TParm();
		if(obj instanceof TParm){
			parm=(TParm)obj;
		}else{
			return;
		}
		odo=(ODO)parm.getData("ODO");
		
		//模板combo
		combo=(TComboBox)this.getComponent("PARENT_PACK_CODE");
		initCombo();
		
		
	}
	
	/**
	 * 初始化主Combo
	 */
	public void initCombo(){
		//取得combo用数据
		String sql = "SELECT PACK_CODE AS ID,PACK_DESC AS NAME FROM OPD_PACK_MAIN " +
		" WHERE DEPT_OR_DR='"+DR+"' AND DEPTORDR_CODE='"+Operator.getID()+"' " +
		" AND PARENT_PACK_CODE IS NULL ORDER BY PACK_CODE";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getErrCode()!=0){
//			this.messageBox_(result.getErrText());
			this.messageBox_("找不到数据");
			return;
		}
		combo.setParmValue(result);
		combo.onInit();
	}
	
	
	/**
	 * 确定事件
	 */
	public void onOk(){
		//组套名称不能为空
		if(StringUtil.isNullString(this.getValueString("PACK_DESC"))){
			this.messageBox_("请输入组套名称");
			this.callFunction("UI|PACK_DESC|grabFocus");
			return;
		}
		
		if(StringUtil.isNullString(this.getValueString("PARENT_PACK_CODE"))){
			this.messageBox_("请选择分类名称");
			this.callFunction("UI|PARENT_PACK_CODE|grabFocus");
			return;
		}
		
                this.setReturnValue(onSave());
		this.closeWindow();
	}
	/**
	 * 保存方法
	 * @return boolean true:保存成功,false:保存失败
	 */
	public boolean onSave(){
		if(odo==null){
			this.messageBox_("初始化错误");
			return false;
		}
		sql=new String[]{};
		//保存主表
		onSaveMain();
		//保存诊断
		onSaveDiag();
		//保存医嘱
		onSaveOrder();
		if(sql.length<1){
			this.messageBox_("无可保存的数据");
		}
		for(String sqlTemp:sql){
			TParm parm=new TParm(TJDODBTool.getInstance().update(sqlTemp));
			if(parm.getErrCode()!=0){
				this.messageBox("E0002");
			}
		}
		return true;
	}
	/**
	 * 保存模板主表
	 * @return boolean
	 */
	public boolean onSaveMain(){
		Subjrec subj=odo.getSubjrec();
		CommonPackMain main=new CommonPackMain(DR,Operator.getID());
		main.onQuery();
		int row=main.insertRow(-1);
		packCode=main.getItemString(row, "PACK_CODE");

		main.setItem(row, "PARENT_PACK_CODE", this.getValue("PARENT_PACK_CODE"));
		main.setItem(row, "PACK_DESC", this.getValue("PACK_DESC"));
		main.setItem(row, "SUBJ_TEXT", subj.getItemData(0, "SUBJ_TEXT"));
		main.setItem(row, "OBJ_TEXT", subj.getItemData(0, "OBJ_TEXT"));
		main.setItem(row, "PHYSEXAM_REC", subj.getItemData(0, "PHYSEXAM_REC"));
		main.setItem(row, "PROPOSAL", subj.getItemData(0, "PROPOSAL"));
		main.setItem(row, "OPT_USER", Operator.getID());
		main.setItem(row, "OPT_TERM", Operator.getIP());
		main.setItem(row, "OPT_DATE", TJDODBTool.getInstance().getDBTime());
//		insertOpeator(main);
		main.setActive(row,true);
		sql=main.getUpdateSQL();

		return true;
	}
	/**
	 * 保存模板诊断
	 * @return boolean
	 */
	public boolean onSaveDiag(){
		Diagrec diag=odo.getDiagrec();
		CommonPackDiag cpd=new CommonPackDiag(DR,Operator.getID(),packCode);
		cpd.onQuery();
		String[] columns=cpd.getColumns();
		int clmCount=columns.length;
		int count=diag.rowCount();
		Timestamp now=TJDODBTool.getInstance().getDBTime();
		for(int i=0;i<count;i++){
			if(StringUtil.isNullString(diag.getItemString(i, "ICD_CODE"))){
				continue;
			}
			int row=cpd.insertRow(-1);
			for(int j=0;j<clmCount;j++){
				if("DEPT_OR_DR".equalsIgnoreCase(columns[j])||"DEPTORDR_CODE".equalsIgnoreCase(columns[j])||"PACK_CODE".equalsIgnoreCase(columns[j]))
					continue;
				cpd.setItem(row, columns[j], diag.getItemData(i, columns[j]));
			}
			cpd.setItem(row, "OPT_USER", Operator.getID());
			cpd.setItem(row, "OPT_TERM", Operator.getIP());
			cpd.setItem(row, "OPT_DATE", now);
			cpd.setActive(row,true);

		}
		String[] temp=cpd.getUpdateSQL();
		sql = StringTool.copyArray(sql,cpd.getUpdateSQL());
		return true;
	}
	/**
	 * 保存医嘱
	 * @return boolean
	 */
	public boolean onSaveOrder(){
		OpdOrder order=odo.getOpdOrder();
		TParm parm=order.getBuffer(order.FILTER);
		CommonPackOrder cpo=new CommonPackOrder(DR,Operator.getID(),packCode);
		cpo.onQuery();
		String[] columns=cpo.getColumns();
		int clmCount=columns.length;
		int count=parm.getCount();
		for(int i=0;i<count;i++){
			int row=cpo.insertRow();
			if(StringUtil.isNullString(parm.getValue("ORDER_DESC",i))){
				continue;
			}

			if(!StringUtil.isNullString(parm.getValue("ORDERSET_CODE",i))&&!parm.getBoolean("SETMAIN_FLG",i)){
				continue;
			}
			for(int j=0;j<clmCount;j++){
				if("DEPT_OR_DR".equalsIgnoreCase(columns[j])||"DEPTORDR_CODE".equalsIgnoreCase(columns[j])||"PACK_CODE".equalsIgnoreCase(columns[j]))
					continue;
				cpo.setItem(row, columns[j],parm.getData(columns[j],i));
			}
			cpo.setItem(i, "SEQ_NO", i+1);
			cpo.setItem(i, "PRESRT_NO", parm.getData("RX_NO",i));
			cpo.setActive(i,true);
		}
		insertOpeator(cpo);
		sql = StringTool.copyArray(sql,cpo.getUpdateSQL());
		order.filter();
//                // System.out.println("医嘱组套：");
//                cpo.showDebug();
		return true;
	}
	/**
	 * 插入操作人员信息
	 * @param tds
	 */
	public void insertOpeator(TDataStore tds){
		if(tds==null)
			return;
		Timestamp now=TJDODBTool.getInstance().getDBTime();
		for(int i=0;i<tds.rowCount();i++){
			if(!tds.isActive(i)){
				continue;
			}
			tds.setItem(i, "OPT_USER", Operator.getID());
			tds.setItem(i, "OPT_DATE", now);
			tds.setItem(i, "OPT_TERM", Operator.getIP());
		}
	}
}
