package com.javahis.ui.udd;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Date;

import jdo.udd.UDDToPreventTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;
/**
 * <p>Title: ����Ԥ��ʹ����ͳ��</p>
 *
 * <p>Description:����Ԥ��ʹ����ͳ��</p>
 *
 * <p>Copyright: Copyright (c)cao yong 2013</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author 2013.10.28
 * @version 1.0
 */
public class UDDToPreventControl  extends TControl{
	private TTable table;
	
	private TTable getTable(String tagName) {
		return (TTable) getComponent(tagName);
	}
  /**
   * ��ʼ��
   */
	public void onInit() {
		callFunction("UI|TABLE|addEventListener","TABLE->"+TTableEvent.CLICKED,this,"onTABLEClicked");//
		table=this.getTable("TABLE");
	    this.onClear();
		 initPage();
		
   }
	 /**
	 * ��ѯ
	 */
	public void onQuery() {
		table.removeRowAll();
		TParm parm=new TParm();
		boolean flg=false;
		DecimalFormat dec=new DecimalFormat("##.##%");
		
			String sDate = StringTool.getString(TypeTool.getTimestamp(getValue("S_DATE")), "yyyyMMddHHmmss");
			String eDate = StringTool.getString(TypeTool.getTimestamp(getValue("E_DATE")), "yyyyMMddHHmmss");
			parm.setData("S_DATE",sDate);
			parm.setData("E_DATE",eDate);
		//����
		if(this.getValueString("DEPT_CODE").length()>0){
			parm.setData("DEPT_CODE",this.getValueString("DEPT_CODE"));
		}
		//����
		if(this.getValueString("SESSION_CODE").length()>0){
			parm.setData("STATION_CODE",this.getValueString("SESSION_CODE"));
		}
		//ҽ��
		if(this.getValueString("VS_DR_CODE").length()>0){
			parm.setData("VS_DR_CODE",this.getValueString("VS_DR_CODE"));
		}
		//����ҩ��������
		if(this.getValueString("PHA_PREVENCODE").length()>0){
			parm.setData("PHA_PREVENCODE",this.getValueString("PHA_PREVENCODE"));
			flg=true;
		}
		TParm result=UDDToPreventTool.getInstance().selectdata(parm);//סԺ����
		
		TParm mresult=UDDToPreventTool.getInstance().selectdataM(parm);//ʹ�ÿ���ҩƷ����
		if(result.getErrCode()<0){
			this.messageBox("��ѯ���ֳ�����");
			return;
		}
		if(result.getCount()<=0){
			this.messageBox("û�в�ѯ����");
			return;
		}
		
		//סԺ��������Ӧ��ʹ�ÿ���ҩƷ������
		TParm sumParm=new TParm();
		double sum=0;//�ϼ�������
		double suma=0;//�ϼ�������
		double count=0;//����ҽ�ñ���
		double totnum=0;//ÿ�е�������
		double usernum=0;//ÿһ�е�ʹ������
		double totunum=0;//�ϼ�ʹ������
		double totunuma=0;//�ϼ�ʹ������
		double tsale=0;//�ϼ�����Ӧ�ñ���
		String phapreve="";
		if(result.getCount()>0){
			 boolean flag=false;
			 String region_code="";//����
			 String dept_code="";//����
			 String station_code="";//����
			 String vs_dr_code="";//ҽ��
			 
			 for(int i=0;i<result.getCount();i++){
				  region_code=result.getValue("REGION_CODE",i);
				  dept_code=result.getValue("DEPT_CODE",i);
				  station_code=result.getValue("STATION_CODE",i);
				  vs_dr_code=result.getValue("VS_DR_CODE",i);
				  flag=false;
				 for(int j=0;j<mresult.getCount();j++){
					 if(region_code.equals(mresult.getValue("REGION_CODE",j)) &&
						dept_code.equals(mresult.getValue("DEPT_CODE",j)) &&	
						station_code.equals(mresult.getValue("STATION_CODE",j)) &&
						vs_dr_code.equals(mresult.getValue("VS_DR_CODE",j)) 
						   ){
						//�������� 
						 sumParm.addData("REGION_CODE", region_code);
						 sumParm.addData("DEPT_CODE", dept_code);
						 sumParm.addData("STATION_CODE", station_code);
						 sumParm.addData("VS_DR_CODE", vs_dr_code);
						 sumParm.addData("PHA_PREVENCODE",mresult.getValue("PHA_PREVENCODE",j));//����
						 sumParm.addData("USER_NUM",mresult.getValue("USER_NUM",j));//ʹ������
						 sumParm.addData("TOTAL_NUM",result.getValue("TOTAL_NUM",i));//������
						// sumParm.addData("APPLICATION_SCALE",dec.format(count));
						    flag=true;
						}
				   }
				   sum+=result.getDouble("TOTAL_NUM",i);
				 //ʹ�ÿ���ҩƷ������סԺ���ߺϲ�
				 if(!flag){//����������
					 sumParm.addData("REGION_CODE", region_code);
					 sumParm.addData("PHA_PREVENCODE","");
					 sumParm.addData("DEPT_CODE", dept_code);
					 sumParm.addData("STATION_CODE", station_code);
					 sumParm.addData("VS_DR_CODE", vs_dr_code);
					 sumParm.addData("USER_NUM",0);
					 sumParm.addData("TOTAL_NUM",result.getValue("TOTAL_NUM",i));
					// sumParm.addData("APPLICATION_SCALE",dec.format(count));
				 }
				 
			}
		
		          sumParm.setCount(sumParm.getCount("REGION_CODE"));
		          
	          for(int i=0;i<sumParm.getCount();i++){
	        	  totnum=sumParm.getDouble("TOTAL_NUM",i);
	        	  usernum=sumParm.getDouble("USER_NUM",i);
				   if(totnum!=0){
				      count=usernum/totnum;
				   }
				   sumParm.addData("APPLICATION_SCALE", dec.format(count));//Ӧ�ñ���
				   
				   totunum+=sumParm.getDouble("USER_NUM",i);//�ܵ�ʹ����
	          }
	          
			
			if(sum!=0){
				tsale=totunum/sum;
			}
			//�ܼ�
			sumParm.addData("REGION_CODE", "�ϼƣ�");
			sumParm.addData("PHA_PREVENCODE", "");
			sumParm.addData("DEPT_CODE", "");
			sumParm.addData("STATION_CODE", "");
			sumParm.addData("VS_DR_CODE", "");
			sumParm.addData("USER_NUM",(int)totunum);
			sumParm.addData("TOTAL_NUM", (int)sum);
			sumParm.addData("APPLICATION_SCALE", dec.format(tsale));
			if(!flg){//���Ͳ�ѯ
				table.setParmValue(sumParm);
			}else{
				TParm phresult=new TParm();
				phapreve=getValueString("PHA_PREVENCODE");
				for(int g=0;g<sumParm.getCount();g++){
					if(phapreve.equals(sumParm.getValue("PHA_PREVENCODE",g))){
						phresult.addRowData(sumParm, g);
					}
				}
				if(phresult.getCount()<=0){
					this.messageBox("û�в�ѯ����");
					return;
				}
				 for(int k=0;k<phresult.getCount();k++){
					   totunuma+=phresult.getDouble("USER_NUM",k);//�ܵ�ʹ����
					   suma+=phresult.getDouble("TOTAL_NUM",k);
					   
		          }
		          
				
				if(suma!=0){
					tsale=totunuma/suma;
				}
				phresult.addData("REGION_CODE", "�ϼƣ�");
				phresult.addData("PHA_PREVENCODE", "");
				phresult.addData("DEPT_CODE", "");
				phresult.addData("STATION_CODE", "");
				phresult.addData("VS_DR_CODE", "");
				phresult.addData("USER_NUM",(int)totunuma);
				phresult.addData("TOTAL_NUM", (int)suma);
				phresult.addData("APPLICATION_SCALE", dec.format(tsale));
				table.setParmValue(phresult);
			}
			
		}
		    
		
    }
	
	public void initPage() {

	    Timestamp date = StringTool.getTimestamp(new Date());
		// ʱ����Ϊ1��
		// ��ʼ����ѯ����
		this.setValue("E_DATE", date.toString().substring(0, 10).replace('-','/')+ " 23:59:59");
		this.setValue("S_DATE", StringTool.rollDate(date, -1).toString().substring(0, 10).replace('-', '/')+ " 00:00:00");
	}
	
	/**
     * ���Excel
     */
    public void onExport() {
    	if(table.getRowCount()<=0){
    		this.messageBox("û�л������");
    		return;
    	}
        ExportExcelUtil.getInstance().exportExcel(table, "����Ԥ��ʹ����ͳ��");
    }
    
    /**
	 * �������
	 */
	public void onClear() {
		String clearString = "S_DATE;E_DATE;DEPT_CODE;SESSION_CODE;VS_DR_CODE;PHA_PREVENCODE";
		table.removeRowAll();
		clearValue(clearString);
		initPage();
	}
	
	/**
	 * �����¼�
	 */
	public void onTABLEClicked(int row){
		if(row<0){
			return;
		}
		TParm tparm= new TParm();
        tparm = table.getParmValue().getRow(row);
        this.setValue("DEPT_CODE", tparm.getValue("DEPT_CODE"));
        this.setValue("SESSION_CODE", tparm.getValue("STATION_CODE"));
        this.setValue("VS_DR_CODE", tparm.getValue("VS_DR_CODE"));
        this.setValue("PHA_PREVENCODE", tparm.getValue("PHA_PREVENCODE"));
			    
		  
	}
}