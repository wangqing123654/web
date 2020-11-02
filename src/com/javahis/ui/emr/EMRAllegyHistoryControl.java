package com.javahis.ui.emr;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TWord;

/**
 * 显示体征采集中的过敏史
 * @author huangjw
 *
 */
public class EMRAllegyHistoryControl extends TControl{
	TWord word;
	TWord allegyWord;
	TParm allegyParm= new TParm();
	String allegyFilePath="";
	String allegyFileName=""; 
	@Override
	public void onInit() {
		super.onInit();
//		String allegyHistory=(String) this.getParameter();
		allegyParm = (TParm) this.getParameter();
		System.out.println("allegyParm=="+allegyParm);
		String allegyHistory = allegyParm.getValue("allegyHistory");
		String sql="SELECT EMT_FILENAME,TEMPLET_PATH FROM EMR_TEMPLET WHERE SUBCLASS_CODE='EMR02000139'";
		TParm parm=new TParm(TJDODBTool.getInstance().select(sql));
		String path=parm.getValue("TEMPLET_PATH",0);
		String name=parm.getValue("EMT_FILENAME",0);		
		word=(TWord) this.getComponent("WORD");
		allegyWord=(TWord) this.getComponent("ALLEGYWORD");
		
		word.onOpen(path, name, 2, false);
		word.pasteString(allegyHistory);
		
		allegyFilePath = allegyParm.getValue("filePath");
		allegyFileName = allegyParm.getValue("fileName");
		if(allegyFilePath.length() > 0 && allegyFileName.length()>0){
			
			allegyWord.onOpen(allegyFilePath, allegyFileName, 3, false);
			allegyWord.setCanEdit(true);
		}
		
	}
	
	public void onSave(){

		if(allegyFilePath.length() > 0 && allegyFileName.length()>0){
			String text = word.getCaptureValue("AllegyHistory");
//			System.out.println(text);
			
			String mrNo = allegyParm.getValue("mrNo");
			String caseNo = allegyParm.getValue("caseNo");
			String sql = "SELECT * FROM OPD_DRUGALLERGY WHERE MR_NO='"+mrNo+"' AND DRUG_TYPE='D'";
			TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
			TParm result = new TParm();
			if(parm.getCount()>0){
				
				if(text.trim().length()== 0 || text.trim().equals("-")){
					String sqlD = "DELETE FROM OPD_DRUGALLERGY WHERE  MR_NO='"+mrNo+"' AND DRUG_TYPE='D'";
					result = new TParm(TJDODBTool.getInstance().update(sqlD));
				}else{
					//更新
					String sqlU = "UPDATE OPD_DRUGALLERGY SET DRUGORINGRD_CODE ='"+text+"',CASE_NO='"+caseNo+"' WHERE MR_NO='"+mrNo+"' AND DRUG_TYPE='D'";
					result = new TParm(TJDODBTool.getInstance().update(sqlU));
				}

			}else{
				
				if(text.trim().length() > 0 && !text.trim().equals("-")){
					//新增
					String admType = allegyParm.getValue("admType");
					String date = SystemTool.getInstance().getDate().toString();
					date = date.replaceAll("-", "").replaceAll("/", "").replaceAll(":", "").replaceAll(" ", "").substring(0, 14);
					
					String sqlI = "INSERT INTO OPD_DRUGALLERGY" +
							"(MR_NO, ADM_DATE, DRUG_TYPE, DRUGORINGRD_CODE, ADM_TYPE, " +
							" CASE_NO, DEPT_CODE, DR_CODE, OPT_USER, OPT_DATE, " +
							" OPT_TERM)" +
							" VALUES" +
							" ('"+mrNo+"', '"+date+"', 'D', '"+text+"', '"+admType+"', " +
							" '"+caseNo+"', '"+Operator.getDept()+"', '"+Operator.getID()+"', '"+Operator.getID()+"', SYSDATE, " +
							" '"+Operator.getIP()+"')";
					result = new TParm(TJDODBTool.getInstance().update(sqlI));
				}
				
				
			}
			
			if(result.getErrCode() < 0){
				this.messageBox("保存失败");
				return;
			}

			allegyWord.clearCapture("AllegyHistory");
			allegyWord.pasteString(text);
			allegyWord.setMessageBoxSwitch(false);
			allegyWord.onSaveAs(allegyFilePath, allegyFileName, 3);
			messageBox("P0001");
		}
		
	}
}
