package com.javahis.ui.opb;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
/**
 * <p>Title: ΢��֧�������׺��޸�</p>
 *
 * <p>Description: ΢��֧�������׺��޸�</p>
 *
 * <p>Copyright: Copyright (c) 2016</p>
 *
 * <p>Company: bluecore</p>
 *
 * @author kangy 20160815
 * @version 1.0
 */

public class OPBModifyPrintNoControl extends TControl{
TParm parameter;
	/**
	 * ��ʼ������
	 */
	public void onInit() {
		Object obj = this.getParameter();
		parameter=(TParm) obj;
			if(parameter.getDouble("WX_AMT")>0&&parameter.getDouble("ZFB_AMT")<=0){
			callFunction("UI|ZFB|Visible",false);
			callFunction("UI|ZFB_AMT|Visible",false);
			callFunction("UI|ZFB_BUSINESS_NO|Visible",false);
			callFunction("UI|WX|Visible",true);
			callFunction("UI|WX_AMT|Visible",true);
			callFunction("UI|WX_BUSINESS_NO|Visible",true);
		}
		if(parameter.getDouble("WX_AMT")<=0&&parameter.getDouble("ZFB_AMT")>0){
			callFunction("UI|ZFB|setVisible",true);
			callFunction("UI|ZFB_AMT|setVisible",true);
			callFunction("UI|ZFB_BUSINESS_NO|setVisible",true);
			callFunction("UI|WX|setVisible",false);
			callFunction("UI|WX_AMT|setVisible",false);
			callFunction("UI|WX_BUSINESS_NO|setVisible",false);
		}
			if(parameter.getDouble("WX_AMT")>0&&parameter.getDouble("ZFB_AMT")>0){
			callFunction("UI|ZFB|setVisible",true);
			callFunction("UI|ZFB_AMT|setVisible",true);
			callFunction("UI|ZFB_BUSINESS_NO|setVisible",true);
			callFunction("UI|WX|setVisible",true);
			callFunction("UI|WX_AMT|setVisible",true);
			callFunction("UI|WX_BUSINESS_NO|setVisible",true);
		}
		if(parameter.getDouble("TOT_AMT")>0){
			this.setValue("WX", "΢�ų�ֵ");
			this.setValue("ZFB", "֧������ֵ");
			this.setValue("WX_AMT", parameter.getValue("WX_AMT")+"Ԫ");
			this.setValue("ZFB_AMT", parameter.getValue("ZFB_AMT")+"Ԫ");
			this.setValue("WX_BUSINESS_NO",parameter.getValue("WX_BUSINESS_NO"));
			this.setValue("ZFB_BUSINESS_NO", parameter.getValue("ZFB_BUSINESS_NO"));
			
		}
		if(parameter.getDouble("TOT_AMT")<0){
			this.setValue("WX", "΢���˷�");
			this.setValue("ZFB", "֧�����˷�");
			this.setValue("WX_AMT", parameter.getValue("WX_AMT")+"Ԫ");
			this.setValue("ZFB_AMT", parameter.getValue("ZFB_AMT")+"Ԫ");
			this.setValue("WX_BUSINESS_NO",parameter.getValue("WX_BUSINESS_NO"));
			this.setValue("ZFB_BUSINESS_NO", parameter.getValue("ZFB_BUSINESS_NO"));
		}
	}
	/**
	 * ����
	 */
	public void onSave(){
		TParm result=new TParm();
		if("JFDP".equals(parameter.getValue("TRADE_TYPE"))){
			if("O".equals(parameter.getValue("ADM_TYPE"))){
				String sql="UPDATE BIL_OPB_RECP SET RECEIPT_NO='"+parameter.getValue("TRADE_NO")+"'";
				sql+=" ,WX_BUSINESS_NO='"+this.getValue("WX_BUSINESS_NO")+"'";
				
				sql+=" ,ZFB_BUSINESS_NO='"+this.getValue("ZFB_BUSINESS_NO")+"'";
				
				sql+=" WHERE RECEIPT_NO='"+parameter.getValue("TRADE_NO")+"'";
				if(parameter.getValue("TRADE_NO").length()>0){
				result=new TParm(TJDODBTool.getInstance().update(sql));
			}else{
				this.messageBox("�������㣬�޷��޸�");
				return;
			}
				if(result.getErrCode()<0){
					this.messageBox("�޸�ʧ��");
				}else{
					this.messageBox("�޸ĳɹ�");
				}
				this.closeWindow();
			}
			if("I".equals(parameter.getValue("ADM_TYPE"))){
				String sql="UPDATE BIL_IBS_RECPM SET RECEIPT_NO='"+parameter.getValue("TRADE_NO")+"'";
sql+=" ,WX_BUSINESS_NO='"+this.getValue("WX_BUSINESS_NO")+"'";
				
				sql+=" ,ZFB_BUSINESS_NO='"+this.getValue("ZFB_BUSINESS_NO")+"'";
				
				sql+=" WHERE RECEIPT_NO='"+parameter.getValue("TRADE_NO")+"'";
				if(parameter.getValue("TRADE_NO").length()>0){
				result=new TParm(TJDODBTool.getInstance().update(sql));
			}else{
				this.messageBox("�������㣬�޷��޸�");
			}
				if(result.getErrCode()<0){
					this.messageBox("�޸�ʧ��");
				}else{
					this.messageBox("�޸ĳɹ�");
				}
				this.closeWindow();
			}
		}
	
		if("YJJ".equals(parameter.getValue("TRADE_TYPE"))){
			String sql="UPDATE BIL_PAY SET ";
			if("WX".equals(parameter.getValue("PAY_TYPE"))){
				sql+=" BUSINESS_NO='"+this.getValue("WX_BUSINESS_NO")+"'";
			}
			if("ZFB".equals(parameter.getValue("PAY_TYPE"))){
				sql+=" BUSINESS_NO='"+this.getValue("ZFB_BUSINESS_NO")+"'";
			}
				sql+=" WHERE RECEIPT_NO='"+parameter.getValue("RECEIPT_NO")+"'";
				if(parameter.getValue("RECEIPT_NO").length()>0){
					result=new TParm(TJDODBTool.getInstance().update(sql));
				}else{
					this.messageBox("�������㣬�޷��޸�");
				}
					if(result.getErrCode()<0){
						this.messageBox("�޸�ʧ��");
					}else{
						this.messageBox("�޸ĳɹ�");
					}
					this.closeWindow();
		}
		
		
		
		if("YLK".equals(parameter.getValue("TRADE_TYPE"))){
			String sql="UPDATE EKT_BIL_PAY SET ";
			if("WX".equals(parameter.getValue("GATHER_TYPE"))){
				sql+="PRINT_NO='"+this.getValue("WX_BUSINESS_NO")+"'";
			}
			if("ZFB".equals(parameter.getValue("GATHER_TYPE"))){
				sql+="PRINT_NO='"+this.getValue("ZFB_BUSINESS_NO")+"'";
			}
				sql+=" WHERE BIL_BUSINESS_NO='"+parameter.getValue("TRADE_NO")+"'";	
			if(parameter.getValue("TRADE_NO").length()>0){
			result=new TParm(TJDODBTool.getInstance().update(sql));
		}else{
			this.messageBox("�������㣬�޷��޸�");
		}
			if(result.getErrCode()<0){
				this.messageBox("�޸�ʧ��");
			}else{
				this.messageBox("�޸ĳɹ�");
			}
			this.closeWindow();
		
		}
		
		if("TC".equals(parameter.getValue("TRADE_TYPE"))){
		String sql="UPDATE MEM_PACKAGE_TRADE_M SET TRADE_NO='"+parameter.getValue("TRADE_NO")+"'";
			
			sql+=" ,WX_BUSINESS_NO='"+this.getValue("WX_BUSINESS_NO")+"'";
			
			sql+=" ,ZFB_BUSINESS_NO='"+this.getValue("ZFB_BUSINESS_NO")+"'";
			
			sql+=" WHERE TRADE_NO='"+parameter.getValue("TRADE_NO")+"'";
			if(parameter.getValue("TRADE_NO").length()>0){
			result=new TParm(TJDODBTool.getInstance().update(sql));
		}else{
			this.messageBox("�������㣬�޷��޸�");
		}
			if(result.getErrCode()<0){
				this.messageBox("�޸�ʧ��");
			}else{
				this.messageBox("�޸ĳɹ�");
			}
			this.closeWindow();
		}
		
		if("HYK".equals(parameter.getValue("TRADE_TYPE"))){
			String sql="UPDATE MEM_TRADE SET TRADE_NO='"+parameter.getValue("TRADE_NO")+"'";
			
			sql+=" ,WX_BUSINESS_NO='"+this.getValue("WX_BUSINESS_NO")+"'";
			
			sql+=" ,ZFB_BUSINESS_NO='"+this.getValue("ZFB_BUSINESS_NO")+"'";
			
			sql+=" WHERE TRADE_NO='"+parameter.getValue("TRADE_NO")+"'";
			if(parameter.getValue("TRADE_NO").length()>0){
			result=new TParm(TJDODBTool.getInstance().update(sql));
		}else{
			this.messageBox("�������㣬�޷��޸�");
		}
			if(result.getErrCode()<0){
				this.messageBox("�޸�ʧ��");
			}else{
				this.messageBox("�޸ĳɹ�");
			}
			this.closeWindow();
		}
		
		if("LPK".equals(parameter.getValue("TRADE_TYPE"))){
			String sql="UPDATE MEM_GIFTCARD_TRADE_M SET WX_BUSINESS_NO='"+this.getValue("WX_BUSINESS_NO")+"'," +
			"ZFB_BUSINESS_NO='"+this.getValue("ZFB_BUSINESS_NO")+"' " +
					"WHERE TRADE_NO='"+parameter.getValue("TRADE_NO")+"'";
			if(parameter.getValue("TRADE_NO").length()>0){
			result=new TParm(TJDODBTool.getInstance().update(sql));
		}else{
			this.messageBox("�������㣬�޷��޸�");
		}
			if(result.getErrCode()<0){
				this.messageBox("�޸�ʧ��");
			}else{
				this.messageBox("�޸ĳɹ�");
			}
			this.closeWindow();
			
		}
		
	}
}
