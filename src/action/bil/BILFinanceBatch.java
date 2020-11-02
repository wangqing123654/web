package action.bil;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;
import jdo.bil.BILFinanceTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.db.TDBPoolManager;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.patch.Patch;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
/**
 * <p>Title: ����ӿ�����չ��������</p>
 *
 * <p>Description: ����ӿ�����չ��������</p>
 *
 * <p>Copyright: Copyright (c) 2014</p>
 *
 * <p>Company: BlueCore</p>
 *
 * @author pangben 2014.04.24
 * @version 1.0
 */
public class BILFinanceBatch extends Patch {
	public BILFinanceBatch() {
    }
    /**
     * �����߳�
     * @return boolean
     */
    public boolean run() {
    	boolean isDebug = true;
    	TConnection connection = TDBPoolManager.getInstance().getConnection("javahisHRP");
    	try {
	        TParm checkParm=new TParm();
	        System.out.println("ִ��HRP����...");
	        String sDate = StringTool.getString(StringTool.rollDate(SystemTool.getInstance().getDate(),-1), "yyyyMMdd");
	        String [] tableName={"di_incomeexp","di_incomereal","di_incomepre","his_feedetail","di_servicevolexp","di_drugs"};//����ı���һ��Ҫ���մ�˳������
	    	for(int j=0;j<tableName.length;j++){
				checkParm.setData("BATCHNAME",sDate);//���ڣ�ɾ�������ڵ�����
				checkParm.setData("EXPTABLENAME",tableName[j]);
		        TParm checkLog=BILFinanceTool.getInstance().checkLogDrlogAyh(checkParm);
				if (checkLog.getErrCode()<0) {
					System.out.println("��ѯ�м��LOG����ִ���:::::"+checkLog.getErrText());
					//this.setMessage("��ѯ�м��LOG����ִ���:"+checkLog.getErrText());
					insertPatchHrpLog(tableName[j]+"���ѯ�м��LOG����ִ���"+checkLog.getErrText());
					continue;
				}
				if (checkLog.getCount()>0) {
					System.out.println(tableName[j]+"��"+sDate+"�����Ѿ����,�����Ե���");
					//this.setMessage("����"+sDate+"����,�����Ե���");
					insertPatchHrpLog(tableName[j]+"��"+sDate+"�����Ѿ����,�����Ե���");
					continue;
				}
				TParm exeParm=onExeParm(j);
				if (connection.isClosed()) {
		        	System.out.println("���ݿ�û����");
		        	connection.close();
		        	return false;
				}
		        TParm result=onExe(exeParm,connection,j,tableName[j]);
		        if (result.getErrCode()<0) {
					connection.rollback();
					connection.close();
					continue;
				}
		        connection.commit();
		        System.out.println(tableName[j]+"��ִ�����....");
	    	}
    	} catch (Exception e) {
    		if(isDebug){
				System.out.println("come in class: BILFinanceBatch ��method ��run");
				e.printStackTrace();
			}
		}finally{
			connection.close();
		}
        return true;
     }
    /**
	 * ��ѯ
	 */
	public TParm onExe(TParm exeParm,TConnection connection,int j,String tableName){
		TParm result=new TParm();
		String sDate = StringTool.getString(StringTool.rollDate(SystemTool.getInstance().getDate(),-1), "yyyyMMdd");
		//sDate="20140322";
		TParm checkParm=new TParm();
		checkParm.setData("BATCHNAME",sDate);//���ڣ�ɾ�������ڵ�����
		//TParm tableResult=new TParm();
		if (null!=exeParm) {
			if(tableName.equals("di_drugs")){//ҩƷ������
				 //ҩƷ����ֵ��ѯ
		        String sql =" SELECT ID,CHN_DESC AS NAME,ENG_DESC AS ENNAME,PY1,PY2,DESCRIPTION FROM SYS_DICTIONARY " +
		            "WHERE GROUP_ID='BIL_FINANCE_PHA'";
				TParm phaParm=new TParm(TJDODBTool.getInstance().select(sql));
				if(phaParm.getErrCode()<0||phaParm.getCount()<=0){
					phaParm=new TParm();
					phaParm.setErr(-1,"ҩƷ����ֵ�û������");
					return phaParm;
				}
				for (int i = 0; i < phaParm.getCount(); i++) {
					//System.out.println(phaParm.getValue("NAME",i)+":PARM:"+exeParm.getParm(phaParm.getValue("NAME",i)));
					if(exeParm.getParm(phaParm.getValue("NAME",i)).getCount()<=0){
						insertPatchHrpLog(tableName+"��"+phaParm.getValue("NAME",i)+sDate+"����������");
					}else{
						result=onBilFinanceSave(checkParm, exeParm.getParm(phaParm.getValue("NAME",i)), connection, phaParm.getValue("NAME",i), j+1);
						if (result.getErrCode()<0) {
							return result;
						}
					}
				}
				
			}else{
				if (null!=exeParm.getParm("OPB_PARM")&&
						null!=exeParm.getParm("OPB_PARM").getValue("START_DATE")&&
						exeParm.getParm("OPB_PARM").getValue("START_DATE").length()>0) {
					result=onBilFinanceSave(checkParm, exeParm.getParm("OPB_PARM"), connection, "����", j+1);
					if (result.getErrCode()<0) {
						return result;
					}
				}else{
					insertPatchHrpLog(tableName+"������"+sDate+"����������");
				}
				if (null!=exeParm.getParm("ODI_PARM")&&
						null!=exeParm.getParm("ODI_PARM").getValue("START_DATE")&&
						exeParm.getParm("ODI_PARM").getValue("START_DATE").length()>0) {
					result=onBilFinanceSave(checkParm, exeParm.getParm("ODI_PARM"), connection, "סԺ", j+1);
					if (result.getErrCode()<0) {
						return result;
					}
				}else{
					insertPatchHrpLog(tableName+"��סԺ"+sDate+"����������");
				}
			}
		}
		if (null==result) {
			result=new TParm();
		}
		
		return result;
	}
     private void onCheckParmNull(TParm result,TParm parm,String type){
    	if (parm!=null) {
    		result.setData(type,parm.getData());
		}
    }
     /**
      * 
     * @Title: insertPatchHrpLog
     * @Description: TODO(��������log�����)
     * @author pangben
     * @param message
     * @return
     * @throws
      */
     private TParm insertPatchHrpLog(String message){
     	TParm parm=new TParm();
     	parm.setData("PATCH_CODE",getUUID());
        String postDate = StringTool.getString(SystemTool.getInstance().getDate(), "yyyyMMddHHmmss");
     	parm.setData("PATCH_START_DATE",postDate);
     	parm.setData("PATCH_DESC","HRP����log");
     	parm.setData("PATCH_TYPE","1");
     	parm.setData("PATCH_STATUS",message);
     	parm.setData("OPT_USER","HRP_SERVER");
     	parm.setData("OPT_TERM","127.0.0.1");
     	TParm result=BILFinanceTool.getInstance().insertPatchHrpLog(parm);
     	return result;
     }
    private TParm onExeParm(int j){
    	
		TParm parm=new TParm();
		TParm result=new TParm();
		String sDate = StringTool.getString(StringTool.rollDate(SystemTool.getInstance().getDate(),-1), "yyyyMMdd");
		//sDate="20140322";
		TParm checkParm=new TParm();
		checkParm.setData("BATCHNAME",sDate);//���ڣ�ɾ�������ڵ�����
		parm.setData("START_DATE",sDate+"000000");
		parm.setData("END_DATE",sDate+"235959");
		TParm tableResult=new TParm();
		TParm temp=new TParm();
		switch (j){
		case 0:
			//Ӧ����������:TYPE:1  ����:O
			parm.setData("TYPE","1");
			parm.setData("PRINT_TYPE","2");
			tableResult=BILFinanceTool.getInstance().onQueryByAccounts(parm);
			temp=diIncomeexpAndDiIncomerealParm(1,"O", tableResult);
			onCheckParmNull(result, temp, "OPB_PARM");
			//Ӧ������סԺ:TYPE:2  סԺ:O
			parm.setData("TYPE","2");
			tableResult=BILFinanceTool.getInstance().onQueryByAccounts(parm);
			temp=diIncomeexpAndDiIncomerealParm(1,"I", tableResult);
			onCheckParmNull(result, temp, "ODI_PARM");
			break;
		case 1:
			//ʵ����������:TYPE:1  ����:O
			parm.setData("TYPE","1");
			tableResult=BILFinanceTool.getInstance().onQueryByPaid(parm);
			temp=diIncomeexpAndDiIncomerealParm(2,"O",tableResult);
			onCheckParmNull(result, temp, "OPB_PARM");
			//ʵ������סԺ:TYPE:2  סԺ:O
			parm.setData("TYPE","2");
			tableResult=BILFinanceTool.getInstance().onQueryByPaid(parm);
			temp=diIncomeexpAndDiIncomerealParm(2,"I",tableResult);
			onCheckParmNull(result, temp, "ODI_PARM");
			break;
		case 2:
			//Ԥ��������TYPE:1 ����:O
			parm.setData("TYPE","1");
			tableResult=BILFinanceTool.getInstance().onQueryByTypePay(parm);
			temp=diIncomepreParm("O",tableResult);
			onCheckParmNull(result, temp, "OPB_PARM");
			//Ԥ��������TYPE:2  סԺ:I
			parm.setData("TYPE","2");
			tableResult=BILFinanceTool.getInstance().onQueryByTypePay(parm);
			temp=diIncomepreParm("I",tableResult);
			onCheckParmNull(result, temp, "ODI_PARM");
			break;
		case 3:
			//===================================================================
			//��ֵ�Ĳ�����TYPE:1���O
			parm.setData("TYPE",1);
			tableResult=BILFinanceTool.getInstance().onQureyByBillDate(parm);
			temp=diIncomehighParm("O",tableResult);
			onCheckParmNull(result, temp, "OPB_PARM");
			//��ֵ�Ĳ�����TYPE:2���I
			parm.setData("TYPE",2);
			tableResult=BILFinanceTool.getInstance().onQureyByBillDate(parm);
			temp=diIncomehighParm("I",tableResult);
			onCheckParmNull(result, temp, "ODI_PARM");
			break;
		case 4:
			//===================================================================
			//=======================================�ɱ�����
			//�ɱ��������� TYPE:1���O
			parm.setData("TYPE",1);
			tableResult=BILFinanceTool.getInstance().onQueryCostAcount(parm);
			temp=diIncomeCostAcountParm("O",tableResult);
			onCheckParmNull(result, temp, "OPB_PARM");
			//�ɱ��������� TYPE:2סԺ��I
			parm.setData("TYPE",2);
			tableResult=BILFinanceTool.getInstance().onQueryCostAcount(parm);
			temp=diIncomeCostAcountParm("I",tableResult);
			onCheckParmNull(result, temp, "ODI_PARM");
			break;
		case 5:
			 //ҩƷ����ֵ��ѯ
	        String sql =" SELECT ID,CHN_DESC AS NAME,ENG_DESC AS ENNAME,PY1,PY2,DESCRIPTION FROM SYS_DICTIONARY " +
	            "WHERE GROUP_ID='BIL_FINANCE_PHA'";
			TParm phaParm=new TParm(TJDODBTool.getInstance().select(sql));
			if(phaParm.getErrCode()<0||phaParm.getCount()<=0){
				System.out.println("ҩƷ����ֵ�����������");
				result=null;
				break;
			}
			parm.setData("BATCHNAME",sDate);
			parm.setData("EXPTABLENAME","DI_DRUGS".toLowerCase());
			for (int i = 0; i < phaParm.getCount(); i++) {
				parm.setData("BUSITYPE_NAME",phaParm.getValue("ID",i));
				tableResult=BILFinanceTool.getInstance().onQueryPhaData(parm);
				temp=diDrugsParm(tableResult, phaParm.getValue("NAME",i));
				onCheckParmNull(result, temp, phaParm.getValue("NAME",i));
			}
			break;
			//=======================================�ɱ�����
		}
		if (null==result) {
			result=new TParm();
		}
		return result;
	
    }
   
    /**
     * ����м�����ݹ��÷���
     * @param checkParm
     * @param result
     * @param connection
     * @param admType
     * @param dataType
     * @param dataTypeSum
     * @param index
     * @return
     */
    private TParm onBilFinanceSave(TParm checkParm,TParm result,TConnection connection,
    		String admType,int index){
    	checkParm.setData("BUSITYPENAME",admType);
		result.setData("BUSITYPENAME_SUM",admType);
		TParm checkCommParm=null;
		switch (index) {
		case 1:
			checkCommParm=BILFinanceTool.getInstance().checkDiIncomeexp(checkParm,connection);
			if (checkCommParm.getCount()>0) {
				//��ӽӿ�У��
				result.setData("CHECK_FLG","Y");
			}else{
			}
			TParm diIncomeexpParm=onSaveDiIncomeexp(result, connection);//Ӧ�����ݱ�������
			if (diIncomeexpParm.getErrCode()<0) {
				return diIncomeexpParm;
			}
			break;
		case 2:
			checkCommParm=BILFinanceTool.getInstance().checkDiIncomereal(checkParm,connection);
			if (checkCommParm.getCount()>0) {
				//��ӽӿ�У��
				result.setData("CHECK_FLG","Y");
			}else{
			}
			TParm diIncomerealParm=onSaveDiIncomereal(result, connection);
			if (diIncomerealParm.getErrCode()<0) {
				return diIncomerealParm;
			}
			break;
		case 3:
//			checkCommParm=BILFinanceTool.getInstance().checkDiIncomepre(checkParm,connection);
			if(admType.equals("����")){
				checkCommParm=BILFinanceTool.getInstance().checkDiIncomepreO(checkParm,connection);
				
			}else{
				checkCommParm=BILFinanceTool.getInstance().checkDiIncomepre(checkParm,connection);
			}
			if (checkCommParm.getCount()>0) {
				//��ӽӿ�У��
				result.setData("CHECK_FLG","Y");
			}else{
			}
			TParm diIncomepreParm=onSaveDiIncomepre(result, connection);
			if (diIncomepreParm.getErrCode()<0) {
				return diIncomepreParm;
			}
			break;
		case 4://��ֵ�Ĳ�
			checkCommParm=BILFinanceTool.getInstance().checkDiIncomeHigh(checkParm,connection);
			if (checkCommParm.getCount()>0) {
				//��ӽӿ�У��
				result.setData("CHECK_FLG","Y");
			}else{
			}
			TParm diIncomehighParm=onSaveDiIncomehigh(result, connection);
			if(diIncomehighParm.getErrCode()<0){
				return diIncomehighParm;
			}
			break;
		case 5://�ɱ�����
			checkCommParm=BILFinanceTool.getInstance().checkDiIncomeCostAcount(checkParm, connection);
			if(checkCommParm.getCount()>0){
				//��ӽӿ�У��
				result.setData("CHECK_FLG","Y");
			}else{
			}
			TParm diIncomeCostAcountParm=onSaveDiIncomeCostAcount(result,connection);
			if(diIncomeCostAcountParm.getErrCode()<0){
				return diIncomeCostAcountParm;
			}
			break;
		case 6://ҩƷ���
			checkCommParm=BILFinanceTool.getInstance().checkDidrugs(checkParm);
			if(checkCommParm.getCount()>0){
				//��ӽӿ�У��
				result.setData("CHECK_FLG","Y");
			}
			TParm diDrugsParm=onSaveDidrugs(result, connection);
			if(diDrugsParm.getErrCode()<0){
				return diDrugsParm;
			}
			break;
		default:
			break;
		}
		return checkParm;
    }
    /**
	 * ҩƷ�������
	 * pangben 2016-6-16
	 * @param parm
	 * @return
	 */
	private TParm onSaveDidrugs(TParm parm,TConnection connection){
		TParm parmValue=new TParm();
		TParm result=null;
		TParm logParm=parm.getParm("LOG_PARM");//��־������
		if (null!=parm.getValue("CHECK_FLG") &&parm.getValue("CHECK_FLG").equals("Y")) {//��ѯ�Ƿ�������ѡ��ȷ�϶���
			parmValue.setData("BATCHNAME",parm.getValue("CHEKC_DATE"));
			parmValue.setData("EXPTABLENAME",logParm.getValue("EXPTABLENAME"));
			parmValue.setData("BUSITYPENAME",logParm.getValue("SOURCESYSTEM"));//�ⲿϵͳ
			parmValue.setData("SOURCESYSTEM",logParm.getValue("SOURCESYSTEM"));
			result=BILFinanceTool.getInstance().deleteDidrugsByBatchname(parmValue, connection);
			if (result.getErrCode()<0) {
				connection.rollback();
				connection.close();
				return result;
			}
			result=BILFinanceTool.getInstance().deleteDiExpLog(parmValue, connection);//ɾ����־�����ҩƷ�������
			if (result.getErrCode()<0) {
				connection.rollback();
				connection.close();
				return result;
			}
		}
		for (int i = 0; i < parm.getCount(); i++) {
			parmValue=parm.getRow(i);
			//�����ɱ�����
			result=BILFinanceTool.getInstance().insertDidrugs(parmValue, connection);
			if (result.getErrCode()<0) {
				connection.rollback();
				connection.close();
				return result;
			}
		}
		//����log��
		
		//logParm.setData("BUSITYPENAME",parm.getValue("BUSITYPENAME_SUM"));
		result=BILFinanceTool.getInstance().insertDiExpLog(logParm, connection);
		if (result.getErrCode()<0) {
			connection.rollback();
			connection.close();
			return result;
		}
		connection.commit();
		connection.close();
		return result;
	}
    /**
	 * ҩƷ���
	 * @param type
	 * @return
	 */
	private TParm diDrugsParm(TParm parm,String billType){
		String date="";
		TParm parmValue=new TParm();
		int sum=0;
		String eDate =  StringTool.getString(StringTool.rollDate(SystemTool.getInstance().getDate(),-1), "yyyyMMdd");
		//String eDateT= StringTool.getString(StringTool.rollDate(SystemTool.getInstance().getDate(),-1), "yyyy-MM-dd");
		parmValue.setData("START_DATE",eDate+"000000");
		parmValue.setData("END_DATE",eDate+"235959");
		String uid=getUUID();//��־������
		//String checkDate="";
		for (int i = 0; i <parm.getCount("ACCOUNT_DATE"); i++) {
			date=SystemTool.getInstance().getDateReplace(parm.getValue("ACCOUNT_DATE",i), true).toString();
			parmValue.addData("PK_DRUGS",getUUID());//�ⲿϵͳ���������
			//parmValue.addData("DATA_TYPE","C");
			parmValue.addData("AMOUNT",  parm.getDouble("AR_AMT",i));//���
			parmValue.addData("PK_EXPLOG", uid);//������־������
			parmValue.addData("BATCHNAME",eDate);//����ǽ�����"YYYYMMDD"
			parmValue.addData("BILLCLERKNAME",parm.getValue("USER_NAME",i));//�Ƶ���
			parmValue.addData("BUSITYPENAME",parm.getValue("BILL_TYPE",i));//ҵ����������
			parmValue.addData("CDAY",parm.getValue("ACCOUNT_DATE",i));//����
			parmValue.addData("CMONTH", date.substring(4,6));//�·�
			parmValue.addData("CYEAR", date.substring(0,4));//���
			//parmValue.addData("EXPTIME", SystemTool.getInstance().getDate().toString().substring(11,19).replaceAll(":", ""));//����ʱ�佨���á�HHMMSS��
			parmValue.addData("CORPNAME", "��������������ҽԺ");//��˾����--��ѡ���룬���ƣ�pk,�Ƽ�����
			parmValue.addData("BILLDEPTCODE", parm.getValue("DEPT_CODE",i));//�������Ҵ���
			parmValue.addData("BILLDEPTNAME", parm.getValue("DEPT_DESC",i));//������������
			parmValue.addData("DDATE", parm.getValue("ACCOUNT_DATE",i));//����
			parmValue.addData("PK_CORP", "001");//��˾����--��ѡ���룬���ƣ�pk,�Ƽ�����
			parmValue.addData("EXECDEPTCODE", parm.getValue("EXE_DEPT_CODE",i));//ִ�п��Ҵ���
			parmValue.addData("EXECDEPTNAME", parm.getValue("EXE_DEPT_DESC",i));//ִ�е���������
			parmValue.addData("DURGCLCODE", parm.getValue("PHA_TYPE",i));//ҩƷ������
			parmValue.addData("DURGCLNAME", parm.getValue("PHA_TYPE_DESC",i));//ҩƷ�������
			parmValue.addData("CUSTCODE", parm.getValue("SUP_CODE",i));//�����̴���
			parmValue.addData("CUSTNAME", parm.getValue("SUP_CHN_DESC",i));//����������
			parmValue.addData("PHARMACYCODE", parm.getValue("PHA_CODE",i));//ҩ������
			parmValue.addData("PHARMACYNAME", parm.getValue("PHA_DESC",i));//ҩ������		
			parmValue.addData("INOUT", parm.getValue("INOUT",i));//��/��
			parmValue.addData("VDEF1", parm.getValue("VDEF1",i));//����סԺ���
			parmValue.addData("OPT_USER", Operator.getID());//
			parmValue.addData("OPT_TERM", Operator.getIP());//
			sum++;
		}
		//parmValue.setData("TYPE_SUM",billType);
		parmValue.setCount(parmValue.getCount("PK_EXPLOG"));
		parmValue.setData("CHEKC_DATE",eDate);
		TParm logParm=new TParm();
		logParm.setData("PK_EXPLOG",uid);//����
		logParm.setData("EXPTABLENAME","DI_DRUGS".toLowerCase());//��������
		logParm.setData("BATCHNAME",eDate);
		logParm.setData("EXPTIME",SystemTool.getInstance().getDate().toString().substring(11,19).replaceAll(":", ""));//����ʱ�佨���á�HHMMSS��
		logParm.setData("EXPOPERATOR",Operator.getName());//��������Ա����
		logParm.setData("EXPSTATE",0);//����״̬
		logParm.setData("TAOTALNUM",sum);//������¼����
		logParm.setData("SOURCESYSTEM",parm.getValue("BILL_TYPE",0));
		//logParm.setData("ADM_TYPE",type);
		//logParm.setData("DATA_TYPE","D");
		parmValue.setData("LOG_PARM",logParm.getData());
		//=====================================================
		return parmValue;
	}
	/**
	 * Ӧ��\ʵ��
	 * @return
	 */
	private TParm diIncomeexpAndDiIncomerealParm(int index,String type,TParm parm){
		String date="";
		TParm parmValue=new TParm();
		int row=parm.getCount();
		if (row<=0) {
			return null;
		}
		int sum=0;
		int dataType=0;
		//String dataMessage="";
		if (type.equals("O")) {
			dataType=13;
		}else if (type.equals("I")) {
			dataType=14;
		}
		String uid=getUUID();//��Ϊ��־������  add by huangjw 20150414
		String eDate =  StringTool.getString(StringTool.rollDate(SystemTool.getInstance().getDate(),-1), "yyyyMMdd");
		String eDateT= StringTool.getString(StringTool.rollDate(SystemTool.getInstance().getDate(),-1), "yyyy-MM-dd");
		parmValue.setData("START_DATE",eDate+"000000");
		parmValue.setData("END_DATE",eDate+"235959");
		//String checkDate="";
		String bilDate="";
		for (int i = 0; i <row; i++) {
			date=SystemTool.getInstance().getDateReplace(parm.getValue("ACCOUNT_DATE",i), true).toString();
			bilDate=SystemTool.getInstance().getDateReplace(parm.getValue("BILL_DATE",i),true).toString();
			//checkDate=date.substring(0,8);
			if (index==1) {
				parmValue.addData("PK_INCOMEEXP",getUUID());//�ⲿϵͳ���������
				parmValue.addData("DATA_TYPE","A");
			}else if(index==2){
				parmValue.addData("DATA_TYPE","B");
				parmValue.addData("PK_INCOMEREAL",getUUID());//�ⲿϵͳ���������
			}
			parmValue.addData("PK_EXPLOG", uid);//������־������
			parmValue.addData("BATCHNAME", eDate);//����ǽ�����"YYYYMMDD"
			parmValue.addData("EXPTIME",SystemTool.getInstance().getDate().toString().substring(11,19).replaceAll(":", ""));//����ʱ�佨���á�HHMMSS��
			parmValue.addData("CYEAR", date.substring(0,4));//���
			parmValue.addData("CMONTH", date.substring(4,6));//�·�
			parmValue.addData("CDAY",bilDate.substring(0,4)+"-"+bilDate.substring(4,6)+"-"+bilDate.substring(6,8));//����
			parmValue.addData("PK_CORP", "001");//��˾����--��ѡ���룬���ƣ�pk,�Ƽ�����
			parmValue.addData("CORPNAME", "��������������ҽԺ");//��˾����--��ѡ���룬���ƣ�pk,�Ƽ�����
			parmValue.addData("PK_ITEM", parm.getValue("BILL_ID",i));//��������
			parmValue.addData("ITEMNAME", parm.getValue("BILL_TYPE",i));//��������
			parmValue.addData("PK_INCOMETYPE", parm.getValue("BILL_ID",i));//�����������
			parmValue.addData("INCOMETYPENAME", parm.getValue("BILL_TYPE",i));//�����������
			parmValue.addData("PK_BUSITYPE",parm.getValue("TYPE_ID",i));//ҵ����������
			parmValue.addData("BUSITYPENAME", parm.getValue("TYPE",i));//ҵ����������
			parmValue.addData("PK_DIAGNOSISDEPT", parm.getValue("DEPT_CODE",i));//������������
			parmValue.addData("DIAGNOSISDEPTNAME", parm.getValue("DEPT_DESC",i));//������������
			parmValue.addData("PK_DIAGNOSTICIAN", parm.getValue("DR_CODE",i));//����ҽ������
			parmValue.addData("DIAGNOSTICIANNAME",parm.getValue("DR_DESC",i));//����ҽ������
			parmValue.addData("PK_EXEDEPT", parm.getValue("EXE_DEPT_CODE",i));//ִ�п�������
			parmValue.addData("EXEDEPTNAME", parm.getValue("EXE_DEPT_DESC",i));//ִ�п�������
			parmValue.addData("PK_EXECUTOR", Operator.getID());//ִ��������
			parmValue.addData("EXECUTORNAME",Operator.getName());//ִ��������
			parmValue.addData("PK_BILLCLERK",parm.getValue("PRINT_USER",i));//�շ�Ա����
			parmValue.addData("BILLCLERKNAME",parm.getValue("PRINT_NAME",i));//�շ�Ա����
			parmValue.addData("PK_PATIENT", parm.getValue("MR_NO",i));//��������
			parmValue.addData("PATIENTNAME", parm.getValue("PAT_NAME",i));//��������
			parmValue.addData("CUSTCODE", parm.getValue("MR_NO",i));//�ͻ�����--�м��
			parmValue.addData("CUSTNAME", parm.getValue("PAT_NAME",i));//�ͻ�����--�м��
			parmValue.addData("CPAYMENT", parm.getValue("BILL_TYPE",i));//֧����ʽ--�м��
			parmValue.addData("CPAYITEM", "");//֧����ϸ--�м��
			parmValue.addData("DDATE", parm.getValue("ACCOUNT_DATE",i).substring(0,10));//��������--�м��//��������--�м��
			parmValue.addData("REALDATE",  SystemTool.getInstance().getDateReplace(parm.getValue("BILL_DATE",i),true));//��������--�м��
			parmValue.addData("VNUMBER", parm.getValue("TID",i));//���--�м��
			parmValue.addData("CPAYMENTNUM", parm.getValue("BILL_ID",i));//֧����ʽ����--�м��
			parmValue.addData("CPAYITEMNUM", "");//֧����ϸ����--�м��
			parmValue.addData("PK_CINPATIENTAREA", parm.getValue("CLINICAREA_CODE",i));//��������
			parmValue.addData("CINPATIENTAREA", parm.getValue("CLINIC_DESC",i));//����
			parmValue.addData("ISINSURANCE", parm.getValue("INS_FLG",i));//�Ƿ�ҽ��Y:��ҽ��;N:��ҽ��
			parmValue.addData("DISEASE", "");//������
			parmValue.addData("DISEASEGROUP", "");//������
			parmValue.addData("AMOUNT",  parm.getDouble("TOT_AMT",i));//���
			parmValue.addData("SOURCESYSTEM", 1);//�ⲿ������Դϵͳ
			parmValue.addData("PK_OUTERSYSTEM", "HIS");//��ϵͳ����
			parmValue.addData("INPRICE", "");//���뵥��
			parmValue.addData("CASHBACK", "");//����
			parmValue.addData("ADM_TYPE", type);//����
			parmValue.addData("OPT_USER", "FC_BATCH");//
			parmValue.addData("OPT_TERM", "127.0.0.1");//
			sum++;
		}
		parmValue.setData("TYPE_SUM",type);
		parmValue.setCount(parmValue.getCount("PK_EXPLOG"));
		parmValue.setData("CHEKC_DATE",eDate);
		//====================================================��ӵ���־��add by huangjw 20150414
		TParm logParm=new TParm();
		logParm.setData("PK_EXPLOG",uid);//����
		if(index==1){//Ӧ�ձ�
			logParm.setData("EXPTABLENAME","DI_INCOMEEXP".toLowerCase());//��������
		}else{//ʵ�ձ�
			logParm.setData("EXPTABLENAME","DI_INCOMEREAL".toLowerCase());//��������
		}
		
		logParm.setData("BATCHNAME",eDate);
		logParm.setData("EXPTIME",SystemTool.getInstance().getDate().toString().substring(11,19).replaceAll(":", ""));//����ʱ�佨���á�HHMMSS��
		logParm.setData("EXPOPERATOR","FC_BATCH");//��������Ա����
		logParm.setData("EXPSTATE",0);//����״̬
		logParm.setData("TAOTALNUM",sum);//������¼����
		logParm.setData("SOURCESYSTEM",dataType);
		parmValue.setData("LOG_PARM",logParm.getData());
		//=====================================================
		return parmValue;
	}
	
	/**
	 * Ԥ����
	 * @return
	 */
	private TParm diIncomepreParm(String type,TParm parm){
		String date="";
		TParm parmValue=new TParm();
		int row=parm.getCount();
		if (row<=0) {
			return null;
		}
		int sum=0;
		int dataType=0;
		if (type.equals("O")) {
			dataType=13;
		}else if (type.equals("I")) {
			dataType=14;
		}
		String uid=getUUID();
		String eDate = StringTool.getString(StringTool.rollDate(SystemTool.getInstance().getDate(),-1), "yyyyMMdd");
		String eDateT= StringTool.getString(StringTool.rollDate(SystemTool.getInstance().getDate(),-1), "yyyy-MM-dd");
		parmValue.setData("START_DATE",eDate+"000000");
		parmValue.setData("END_DATE",eDate+"235959");//String checkDate="";
		String bilDate="";
		for (int i = 0; i <row; i++) {
			date=SystemTool.getInstance().getDateReplace(parm.getValue("ACCOUNT_DATE",i), true).toString();
			bilDate=SystemTool.getInstance().getDateReplace(parm.getValue("BILL_DATE",i),true).toString();
			//checkDate=date.substring(0,8);
			parmValue.addData("PK_INCOMEPRE",getUUID());//�ⲿϵͳ���������
			parmValue.addData("DATA_TYPE","C");
			parmValue.addData("PK_EXPLOG", uid);//������־������
			parmValue.addData("BATCHNAME",eDate);//����ǽ�����"YYYYMMDD"
			parmValue.addData("EXPTIME", SystemTool.getInstance().getDate().toString().substring(11,19).replaceAll(":", ""));//����ʱ�佨���á�HHMMSS��
			parmValue.addData("CYEAR", date.substring(0,4));//���
			parmValue.addData("CMONTH", date.substring(4,6));//�·�
			parmValue.addData("CDAY",bilDate.substring(0,4)+"-"+bilDate.substring(4,6)+"-"+bilDate.substring(6,8));//����
			parmValue.addData("DDATE", parm.getValue("ACCOUNT_DATE",i).substring(0,10));//����
			parmValue.addData("PK_CORP", "001");//��˾����--��ѡ���룬���ƣ�pk,�Ƽ�����
			parmValue.addData("CORPNAME", "��������������ҽԺ");//��˾����--��ѡ���룬���ƣ�pk,�Ƽ�����
			parmValue.addData("SOURCESYSTEM", 1);//�ⲿ������Դϵͳ
			parmValue.addData("PK_OUTERSYSTEM", "HIS");//��ϵͳ����
			parmValue.addData("REALDATE",  SystemTool.getInstance().getDateReplace(parm.getValue("BILL_DATE",i),true));//��������--�м��
			parmValue.addData("VNUMBER", parm.getValue("TID",i));//���--�м��
			parmValue.addData("BUSITYPENAME", parm.getValue("TYPE",i));//ҵ����������
			parmValue.addData("PK_INCOMETYPE", parm.getValue("BILL_ID",i));//�����������
			parmValue.addData("INCOMETYPENAME", parm.getValue("BILL_TYPE",i));//�����������
			parmValue.addData("AMOUNT",  parm.getDouble("TOT_AMT",i));//���
			parmValue.addData("CPAYMENT", parm.getValue("BILL_TYPE",i));//֧����ʽ--�м��
			parmValue.addData("CPAYITEM", "");//֧����ϸ--�м��
			parmValue.addData("CASHBACK", parm.getDouble("TOT_AMT",i)<0?"��":"��");//����
			parmValue.addData("BILLCLERKNAME",parm.getValue("PRINT_NAME",i));//�շ�Ա����
			parmValue.addData("PK_BILLCLERK",parm.getValue("PRINT_USER",i));//�շ�Ա����
			parmValue.addData("CPAYMENTNUM", parm.getValue("BILL_ID",i));//֧����ʽ����--�м��
			parmValue.addData("CPAYITEMNUM", "");//֧����ϸ����--�м��
			parmValue.addData("PK_PATIENT", parm.getValue("MR_NO",i));//��������
			parmValue.addData("PATIENTNAME", parm.getValue("PAT_NAME",i));//��������
			parmValue.addData("ADM_TYPE", type);//����
			parmValue.addData("OPT_USER", "FC_BATCH");//
			parmValue.addData("OPT_TERM", "127.0.0.1");//
			parmValue.addData("VDEF2", parm.getValue("PACKAGE_CODE",i));//�ײͱ���
			parmValue.addData("VDEF3", parm.getValue("PACKAGE_DESC",i));//�ײ�����
			sum++;
		}
		parmValue.setData("TYPE_SUM",type);
		parmValue.setCount(parmValue.getCount("PK_INCOMEPRE"));
		parmValue.setData("CHEKC_DATE",eDate);
		//====================================================��ӵ���־��add by huangjw 20150414
		TParm logParm=new TParm();
		logParm.setData("PK_EXPLOG",uid);//����
		logParm.setData("EXPTABLENAME","di_incomepre");//��������
		logParm.setData("BATCHNAME",eDate);
		logParm.setData("EXPTIME",SystemTool.getInstance().getDate().toString().substring(11,19).replaceAll(":", ""));//����ʱ�佨���á�HHMMSS��
		logParm.setData("EXPOPERATOR","FC_BATCH");//��������Ա����
		logParm.setData("EXPSTATE",0);//����״̬
		logParm.setData("TAOTALNUM",sum);//������¼����
		logParm.setData("SOURCESYSTEM",dataType);
		parmValue.setData("LOG_PARM",logParm.getData());
		//=====================================================
		return parmValue;
	}
	
	/**
	 * ��ֵ�Ĳ�
	 * @param type
	 * @param parm
	 * @return
	 */
	private TParm diIncomehighParm(String type,TParm parm){
		String date="";
		TParm parmValue=new TParm();
		int row=parm.getCount();
		if (row<=0) {
			return null;
		}
		String uid= getUUID();
		int sum=0;
		int dataType=0;
		String dataMessage="";
		if (type.equals("O")) {
			dataType=13;
			dataMessage="����";
		}else if (type.equals("I")) {
			dataType=14;
			dataMessage="סԺ";
		}
		String eDate = StringTool.getString(StringTool.rollDate(SystemTool.getInstance().getDate(),-1), "yyyyMMdd");
		String eDateT= StringTool.getString(StringTool.rollDate(SystemTool.getInstance().getDate(),-1), "yyyy-MM-dd");
		parmValue.setData("START_DATE",eDate+"000000");
		parmValue.setData("END_DATE",eDate+"235959");
		for(int i=0;i<row;i++){
			parmValue.addData("FLG", "Y");
			date=SystemTool.getInstance().getDateReplace(parm.getValue("ACCOUNT_DATE",i), true).toString();
			parmValue.addData("PK_EXPLOG", uid);//������־������
			parmValue.addData("BATCHNAME", eDate);//����ǽ�����"YYYYMMDD"
			parmValue.addData("EXPTIME",SystemTool.getInstance().getDate().toString().substring(11,19).replaceAll(":", ""));
			parmValue.addData("SOURCESYSTEM", dataType);//�ⲿ������Դϵͳ
			
			parmValue.addData("PK_FEEDETAIL",getUUID());//�ⲿϵͳ���������
			parmValue.addData("BISCHARGE",parm.getValue("BILL_FLG",i));
			//parmValue.addData("BISPROC", parm.getValue("HRP_FLG",i));
			parmValue.addData("VHISBUSID",parm.getValue("RECEIPT_NO",i));
			parmValue.addData("VBARCODE",parm.getValue("INV_CODE",i));
			parmValue.addData("VITEMCODE",parm.getValue("ORDER_CODE",i));
			parmValue.addData("VITEMNAME",parm.getValue("ORDER_DESC",i));
			parmValue.addData("VRECEIPTCODE",parm.getValue("REXP_CODE",i));
			parmValue.addData("VRECEIPTNAME",parm.getValue("REXP_DESC",i));
			parmValue.addData("PK_CORP","001");
			parmValue.addData("NCHARGEMNY",parm.getValue("AR_AMT",i));
			parmValue.addData("NCHARGENUMBER",parm.getValue("DOSAGE_QTY",i));
			parmValue.addData("DCHARGEDATE",date.substring(0,4)+"-"+date.substring(4,6)+"-"+date.substring(6,8));
			parmValue.addData("VBILLCLERKCODE",parm.getValue("BILL_USER",i));
			parmValue.addData("VBILLCKERKNAME",parm.getValue("BILL_NAME",i));
			parmValue.addData("VBILLDEPTCODE",parm.getValue("DEPT_CODE",i));
			parmValue.addData("VBILLDEPTNAME",parm.getValue("DEPT_NAME",i));
			parmValue.addData("VEXECDEPTCODE",parm.getValue("EXEC_DEPT_CODE",i));
			parmValue.addData("VEXECDEPTNAME",parm.getValue("EXEC_DEPT_NAME",i));
			parmValue.addData("VADMNUMBER",parm.getValue("MR_NO",i));
			parmValue.addData("VPATIENTNAME",parm.getValue("PAT_NAME",i));
			parmValue.addData("VSEX",parm.getValue("SEX_CODE",i));
			parmValue.addData("VOPNAME",parm.getValue("OP_DESC",i));
			parmValue.addData("DOPDATE",parm.getValue("OP_DATE",i));
			parmValue.addData("BUSITYPENAME",dataMessage);
			parmValue.addData("VNAMEPHYSICIAN",parm.getValue("DR_NAME",i));
			parmValue.addData("IPATIENTAGE",getYear(parm.getTimestamp("BIRTH_DATE",i)));
			parmValue.addData("VFAMILYMEMBERS",parm.getValue("PAT_FAMLIY",i));
			parmValue.addData("VTEL",parm.getValue("PAT_TEL",i));
			parmValue.addData("VUSERCODE",parm.getValue("BILL_USER",i));//FC_BATCH ��Ϊ parm.getValue("BILL_USER",i) 20160120 huangjw
			parmValue.addData("UNITCODE","001");
			parmValue.addData("BISPROC","N");
			//=================================סԺ��
			parmValue.addData("CASE_NO_SEQ", parm.getValue("CASE_NO_SEQ",i));//
			parmValue.addData("CASE_NO", parm.getValue("CASE_NO",i));//�����¼
			parmValue.addData("SEQ_NO", parm.getValue("SEQ_NO",i));//���
			//=================================סԺ��
			sum++;
		}
		parmValue.setData("TYPE_SUM",type);
		parmValue.setCount(parmValue.getCount("PK_FEEDETAIL"));
		parmValue.setData("CHEKC_DATE",eDate);
		//====================================================��ӵ���־��add by huangjw 20150414
		TParm logParm=new TParm();
		logParm.setData("PK_EXPLOG",uid);//����
		logParm.setData("EXPTABLENAME","his_feedetail");//��������
		logParm.setData("BATCHNAME",eDate);
		logParm.setData("EXPTIME",SystemTool.getInstance().getDate().toString().substring(11,19).replaceAll(":", ""));//����ʱ�佨���á�HHMMSS��
		logParm.setData("EXPOPERATOR",parm.getValue("BILL_USER",0));//��������Ա����  FC_BATCH ��Ϊ parm.getValue("BILL_USER",i) 20160120 huangjw
		logParm.setData("EXPSTATE",0);//����״̬
		logParm.setData("TAOTALNUM",sum);//������¼����
		logParm.setData("SOURCESYSTEM",dataType);
		//logParm.setData("ADM_TYPE",type);
		//logParm.setData("DATA_TYPE","D");
		parmValue.setData("LOG_PARM",logParm.getData());
		return parmValue;
	}
	/**
	 *�ɱ���������add by huangjw 20150410
	 * @param type
	 * @param parm
	 * @return
	 */
	public TParm diIncomeCostAcountParm(String type,TParm parm){
		TParm parmValue=new TParm();
		int row=parm.getCount();
		if (row<=0) {
			return null;
		}
		String exptime=SystemTool.getInstance().getDate().toString().substring(0,19);
		String exptime1=SystemTool.getInstance().getDate().toString().substring(11,19).replaceAll(":", "");
		
		String sDate = StringTool.getString(StringTool.rollDate(SystemTool.getInstance().getDate(),-1), "yyyyMMdd");
		String uid= getUUID();
		int sum=0;
		int dataType=0;
		String dataMessage="";
		if ("O".equals(type)) {
			dataType=13;
			dataMessage="����";
		}else if ("I".equals(type)) {
			dataType=14;
			dataMessage="סԺ";
		}
		String eDate = StringTool.getString(StringTool.rollDate(SystemTool.getInstance().getDate(),-1), "yyyyMMdd");
		String eDateT= StringTool.getString(StringTool.rollDate(SystemTool.getInstance().getDate(),-1), "yyyy-MM-dd");
		String cyear=eDateT.substring(0,4);
		String cmonth=eDateT.substring(5,7);
		String cday=eDateT;
		parmValue.setData("START_DATE",eDate+"000000");
		parmValue.setData("END_DATE",eDate+"235959");
		for (int i = 0; i <row; i++) {
			parmValue.addData("PK_SERVICEVOLEXP", getUUID());//�ⲿϵͳ���������������
			parmValue.addData("PK_EXPLOG", uid);//������־������
			parmValue.addData("BATCHNAME", sDate);//���������
			if("O".equals(type)){
				parmValue.addData("TYPE", "1");
			}else if("I".equals(type)){
				parmValue.addData("TYPE", "2");
			}
			parmValue.addData("BUSITYPENAME", parm.getValue("TYPE",i));
			parmValue.addData("EXPTIME", exptime1);//����ʱ��
			parmValue.addData("CYEAR", cyear);//���
			parmValue.addData("CMONTH", cmonth);//�·�
			parmValue.addData("CDAY", cday);//ҵ������
			parmValue.addData("PK_CORP", "001");//��˾����
			parmValue.addData("CORPNAME", "��������������ҽԺ");//��˾����
			parmValue.addData("PK_ITEM", parm.getValue("PK_ITEM",i));//������Ŀ����
			parmValue.addData("ITEMNAME", parm.getValue("ITEMNAME",i));//������Ŀ����
			parmValue.addData("VOLUME", parm.getDouble("VOLUME",i));//������
			parmValue.addData("PK_DIAGNOSISDEPT", parm.getValue("PK_DIAGNOSISDEPT",i));//��������
			parmValue.addData("DIAGNOSISDEPTNAME", parm.getValue("DIAGNOSISDEPTNAME",i));//��������
			parmValue.addData("PK_OUTERSYSTEM", dataMessage);//
			parmValue.addData("SOURCESYSTEM", dataType);//
			sum+=parm.getDouble("VOLUME",i);
		}
		parmValue.setData("CHEKC_DATE",sDate);
		parmValue.setCount(parmValue.getCount("PK_EXPLOG"));
		
		TParm logParm=new TParm();
		logParm.setData("PK_EXPLOG",uid);//����
		logParm.setData("EXPTABLENAME","DI_SERVICEVOLEXP".toLowerCase());//��������
		logParm.setData("BATCHNAME",sDate);
		logParm.setData("EXPTIME",exptime1);//����ʱ�佨���á�HHMMSS��
		logParm.setData("EXPOPERATOR","FC_BATCH");//��������Ա����
		logParm.setData("EXPSTATE",0);//����״̬
		logParm.setData("TAOTALNUM",sum);//������¼����
		logParm.setData("SOURCESYSTEM",dataType);
		//logParm.setData("ADM_TYPE",type);
		//logParm.setData("DATA_TYPE","D");
		parmValue.setData("LOG_PARM",logParm.getData());
		parmValue.setData("LOCAL_LOG_FLG",parm.getBoolean("LOCAL_LOG_FLG"));
		return parmValue;
	}
	private String getYear(Timestamp newDate){
		java.util.Date date = new Date();
		long day = (date.getTime() - newDate.getTime()) / (24 * 60 * 60 * 1000)
				+ 1;
		String year=new java.text.DecimalFormat("#.00").format(day/365f);
		return year;
	}
	
	public static String getUUID() {
		String s = UUID.randomUUID().toString();
		// ȥ����-������
		return s.substring(0, 8) + s.substring(9, 13) + s.substring(14, 18)
				+ s.substring(19, 23);
    } 
	/**
	 * Ӧ�ղ�����������
	 * @param parm
	 * @param connection
	 * @return
	 */
	private TParm onSaveDiIncomeexp(TParm parm,TConnection connection){
		TParm parmValue=new TParm();
		TParm result=null;
		TParm logParm=parm.getParm("LOG_PARM");//��־������
		if (null!=parm.getValue("CHECK_FLG") &&parm.getValue("CHECK_FLG").equals("Y")) {//��ѯ�Ƿ�������ѡ��ȷ�϶���
			parmValue.setData("BATCHNAME",parm.getValue("CHEKC_DATE"));
			parmValue.setData("EXPTABLENAME",logParm.getValue("EXPTABLENAME"));
			parmValue.setData("BUSITYPENAME",parm.getValue("BUSITYPENAME_SUM"));
			parmValue.setData("SOURCESYSTEM",logParm.getValue("SOURCESYSTEM"));//�ⲿϵͳ
			result=BILFinanceTool.getInstance().deleteDiIncomeexpByBatchname(parmValue, connection);
			if (result.getErrCode()<0) {
				return result;
			}
			result=BILFinanceTool.getInstance().deleteDiExpLog(parmValue, connection);//ɾ����־�����Ӧ������
			if (result.getErrCode()<0) {
				connection.rollback();
				connection.close();
				return result;
			}
		}
		for (int i = 0; i < parm.getCount(); i++) {
			parmValue=parm.getRow(i);
			if (i!=0&&i%1000==0) {
				try {
					Thread.sleep(5000);
				} catch (Exception e) {
					
					// TODO: handle exception
				}
			}
			//����Ӧ������
			result=BILFinanceTool.getInstance().insertDiIncomeexp(parmValue, connection);
			if (result.getErrCode()<0) {
				return result;
			}
		}
		//����Ӧ����־������
		result=BILFinanceTool.getInstance().insertDiExpLog(logParm, connection);
		if (result.getErrCode()<0) {
			connection.rollback();
			connection.close();
			return result;
		}
		String sql="";
		if (parm.getValue("BUSITYPENAME_SUM").equals("����")) {
			//�޸ı�������״̬
			sql="UPDATE OPD_ORDER SET BIL_FINANCE_FLG='Y' WHERE EXEC_FLG='Y' AND EXEC_DATE BETWEEN TO_DATE("+parm.getValue("START_DATE")
			+",'YYYYMMDDHH24MISS') AND TO_DATE("+parm.getValue("END_DATE")+",'YYYYMMDDHH24MISS') ";
			
		}else{
			sql="UPDATE IBS_ORDD SET BIL_FINANCE_FLG='Y' WHERE BILL_NO IN (SELECT A.BILL_NO FROM IBS_BILLM A,IBS_BILLD B WHERE A.BILL_NO=B.BILL_NO AND A.BILL_SEQ=B.BILL_SEQ  AND A.BILL_DATE BETWEEN TO_DATE('"+parm.getValue("START_DATE")+
		"','YYYYMMDDHH24MISS') AND TO_DATE('"+parm.getValue("END_DATE")+"','YYYYMMDDHH24MISS'))";
		}
		result = new TParm(TJDODBTool.getInstance().update(sql));
		if (result.getErrCode()<0) {
			return result;
		}
		return result;
	}
	/**
	 * ����ʵ������
	 * @param parm
	 * @return
	 */
	private TParm onSaveDiIncomereal(TParm parm,TConnection connection){
		TParm parmValue=new TParm();
		TParm result=null;
		TParm logParm=parm.getParm("LOG_PARM");//��־������
		if (null!=parm.getValue("CHECK_FLG")&&parm.getValue("CHECK_FLG").equals("Y")) {//��ѯ�Ƿ�������ѡ��ȷ�϶���
			parmValue.setData("BATCHNAME",parm.getValue("CHEKC_DATE"));
			parmValue.setData("EXPTABLENAME",logParm.getValue("EXPTABLENAME"));
			parmValue.setData("BUSITYPENAME",parm.getValue("BUSITYPENAME_SUM"));
			parmValue.setData("SOURCESYSTEM",logParm.getValue("SOURCESYSTEM"));//�ⲿϵͳ
			result=BILFinanceTool.getInstance().deleteDiIncomerealByBatchname(parmValue, connection);
			if (result.getErrCode()<0) {
				return result;
			}
			result=BILFinanceTool.getInstance().deleteDiExpLog(parmValue, connection);//ɾ����־�����ʵ������
			if (result.getErrCode()<0) {
				connection.rollback();
				connection.close();
				return result;
			}
		}
		for (int i = 0; i < parm.getCount(); i++) {
			parmValue=parm.getRow(i);
			//����ʵ������
			result=BILFinanceTool.getInstance().insertDiIncomereal(parmValue, connection);
			if (result.getErrCode()<0) {
				return result;
			}
		}
		result=BILFinanceTool.getInstance().insertDiExpLog(logParm, connection);
		if (result.getErrCode()<0) {
			connection.rollback();
			connection.close();
			return result;
		}
		String sql="";
		if (parm.getValue("BUSITYPENAME_SUM").equals("����")) {
			//�޸ı�������״̬
			sql="UPDATE BIL_OPB_RECP SET BIL_FINANCE_FLG='Y' WHERE ACCOUNT_SEQ IS NOT NULL  AND ACCOUNT_DATE BETWEEN TO_DATE('"+parm.getValue("START_DATE")+"','YYYYMMDDHH24MISS') AND TO_DATE('"
			+parm.getValue("END_DATE")+"','YYYYMMDDHH24MISS')";
			
		}else{
			sql="UPDATE BIL_IBS_RECPM SET BIL_FINANCE_FLG='Y' WHERE ACCOUNT_SEQ IS NOT NULL AND CHARGE_DATE BETWEEN TO_DATE('"
				+parm.getValue("START_DATE")+"','YYYYMMDDHH24MISS') AND TO_DATE('"+parm.getValue("END_DATE")+"','YYYYMMDDHH24MISS')";
		}
		result = new TParm(TJDODBTool.getInstance().update(sql));
		if (result.getErrCode()<0) {
			return result;
		}
		return result;
	}
	
	/**
	 * ����Ԥ��������
	 * @param parm
	 * @return
	 */
	public TParm onSaveDiIncomepre(TParm parm,TConnection connection){
		TParm parmValue=new TParm();
		TParm result=null;
		TParm logParm=parm.getParm("LOG_PARM");//��־������
		if (null!=parm.getValue("CHECK_FLG")&&parm.getValue("CHECK_FLG").equals("Y")) {//��ѯ�Ƿ�������ѡ��ȷ�϶���
			parmValue.setData("BATCHNAME",parm.getValue("CHEKC_DATE"));
			parmValue.setData("EXPTABLENAME",logParm.getValue("EXPTABLENAME"));
			parmValue.setData("BUSITYPENAME",parm.getValue("BUSITYPENAME_SUM"));
			parmValue.setData("SOURCESYSTEM",logParm.getValue("SOURCESYSTEM"));//�ⲿϵͳ
//			result=BILFinanceTool.getInstance().deleteDiIncomepreByBatchname(parmValue, connection);
			if(parm.getValue("BUSITYPENAME_SUM").equals("����")){
				result=BILFinanceTool.getInstance().deleteDiIncomepreByBatchnameO(parmValue, connection);
			}else{
				result=BILFinanceTool.getInstance().deleteDiIncomepreByBatchname(parmValue, connection);
			}									
			if (result.getErrCode()<0) {
				return result;
			}
			result=BILFinanceTool.getInstance().deleteDiExpLog(parmValue, connection);//ɾ����־����ĳɱ�����
			if (result.getErrCode()<0) {
				connection.rollback();
				connection.close();
				return result;
			}
		}
		for (int i = 0; i < parm.getCount(); i++) {
			parmValue=parm.getRow(i);
			//����Ԥ��������
			result=BILFinanceTool.getInstance().insertDiIncomepre(parmValue, connection);
			if (result.getErrCode()<0) {
				return result;
			}
		}
		result=BILFinanceTool.getInstance().insertDiExpLog(logParm, connection);
		if (result.getErrCode()<0) {
			connection.rollback();
			connection.close();
			return result;
		}
		String sql="";
		if (parm.getValue("BUSITYPENAME_SUM").equals("����")) {
			//�޸ı�������״̬
			sql="UPDATE EKT_BIL_PAY SET BIL_FINANCE_FLG='Y' WHERE ACCNT_TYPE IN('4','6') AND STORE_DATE BETWEEN "+                           
			" TO_DATE('"+parm.getValue("START_DATE")+"','YYYYMMDDHH24MISS') AND TO_DATE('"+parm.getValue("END_DATE")+"', 'YYYYMMDDHH24MISS') ";
			result = new TParm(TJDODBTool.getInstance().update(sql));
			if (result.getErrCode()<0) {
				connection.rollback();
				connection.close();
				return result;
			}
			sql="UPDATE MEM_PACKAGE_TRADE_M SET BIL_FINANCE_FLG='Y' WHERE  ACCOUNT_DATE BETWEEN "+                           
			" TO_DATE('"+parm.getValue("START_DATE")+"','YYYYMMDDHH24MISS') AND TO_DATE('"+parm.getValue("END_DATE")+"', 'YYYYMMDDHH24MISS') ";
			
		}else{
			sql="UPDATE BIL_PAY SET BIL_FINANCE_FLG='Y' WHERE TRANSACT_TYPE IN('01','04','02') AND CHARGE_DATE BETWEEN "+                           
			" TO_DATE('"+parm.getValue("START_DATE")+"','YYYYMMDDHH24MISS') AND TO_DATE('"+parm.getValue("END_DATE")+"', 'YYYYMMDDHH24MISS') " ;
		}
		result = new TParm(TJDODBTool.getInstance().update(sql));
		if (result.getErrCode()<0) {
			return result;
		}
		return result;
	}
	
	/**
	 * ��ֵ�Ĳ�
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm onSaveDiIncomehigh(TParm parm,TConnection connection){
		TParm parmValue=new TParm();
		TParm result=null;
		TParm logParm=parm.getParm("LOG_PARM");//��־������
		boolean flg=true;
		if (null!=parm.getValue("CHECK_FLG")&&parm.getValue("CHECK_FLG").equals("Y")) {//��ѯ�Ƿ�������ѡ��ȷ�϶���
			parmValue.setData("BATCHNAME",parm.getValue("CHEKC_DATE"));
			//parmValue.setData("EXPTABLENAME",logParm.getValue("EXPTABLENAME"));
			parmValue.setData("BUSITYPENAME",parm.getValue("BUSITYPENAME_SUM"));
			//parmValue.setData("SOURCESYSTEM",logParm.getValue("SOURCESYSTEM"));//�ⲿϵͳ
			result=BILFinanceTool.getInstance().deleteDiIncomeHighPriceByBatchname(parmValue, connection);
			if (result.getErrCode()<0) {
				return result;
			}
			flg=false;
		}
		for (int i = 0; i < parm.getCount(); i++) {
			parmValue=parm.getRow(i);
			//������ֵ�Ĳ�����
			result=BILFinanceTool.getInstance().insertDiIncomeHigh(parmValue, connection);
			if (result.getErrCode()<0) {
				return result;
			}
		}
		if(flg){
			result=BILFinanceTool.getInstance().insertDiExpLog(logParm, connection);
			if (result.getErrCode()<0) {
				connection.rollback();
				connection.close();
				return result;
			}
		}
		String sql="";
		if (parm.getValue("BUSITYPENAME_SUM").equals("����")) {
			//�޸ı�������״̬
			for (int i = 0; i < parm.getCount(); i++) {
				TParm newParm=parm.getRow(i);
				sql="UPDATE OPD_ORDER SET BIL_HIGH_FINANCE_FLG='Y' WHERE RECEIPT_NO='"+newParm.getValue("VHISBUSID")+"' AND BILL_DATE " +
						"BETWEEN TO_DATE("+parm.getValue("START_DATE")
				+",'YYYYMMDDHH24MISS') AND TO_DATE("+parm.getValue("END_DATE")+",'YYYYMMDDHH24MISS') AND ORDER_CODE = '"+parm.getValue("VITEMCODE",i)+"' ";
				result = new TParm(TJDODBTool.getInstance().update(sql));
				if (result.getErrCode()<0) {
					connection.rollback();
					connection.close();
					return result;
				}
				if(newParm.getDouble("NCHARGEMNY")<0){
					
					sql="UPDATE OPD_ORDER_HISTORY  SET BIL_HIGH_FINANCE_FLG_RETURN='Y' " +
							" WHERE RECEIPT_NO = (SELECT  RECEIPT_NO FROM BIL_OPB_RECP WHERE RESET_RECEIPT_NO='"+newParm.getValue("VHISBUSID")+"'" +
							" AND BILL_DATE " +
							"BETWEEN TO_DATE("+parm.getValue("START_DATE")
					+",'YYYYMMDDHH24MISS') AND TO_DATE("+parm.getValue("END_DATE")+",'YYYYMMDDHH24MISS') ) AND ORDER_CODE = '"+parm.getValue("VITEMCODE",i)+"'  ";
					result = new TParm(TJDODBTool.getInstance().update(sql));
				}
			}
			
		}else{
			for (int i = 0; i < parm.getCount(); i++) {
				TParm newParm=parm.getRow(i);
				sql="UPDATE IBS_ORDD SET BIL_HIGH_FINANCE_FLG='Y' WHERE CASE_NO_SEQ ="+Integer.parseInt(newParm.getValue("CASE_NO_SEQ"))+"" +
				" AND CASE_NO='"+newParm.getValue("CASE_NO")+"' AND SEQ_NO="+Integer.parseInt(newParm.getValue("SEQ_NO"))+""+
				" AND BILL_DATE BETWEEN TO_DATE('"+parm.getValue("START_DATE")+"','YYYYMMDDHH24MISS') " +
				" AND TO_DATE('"+parm.getValue("END_DATE")+"','YYYYMMDDHH24MISS')  AND ORDER_CODE = '"+parm.getValue("VITEMCODE",i)+"' ";
				result = new TParm(TJDODBTool.getInstance().update(sql));
			}
		}
		result = new TParm(TJDODBTool.getInstance().update(sql));
		if (result.getErrCode()<0) {
			return result;
		}
		return result;
	}
	/**
	 * �ɱ�����
	 * @return
	 */
	public TParm onSaveDiIncomeCostAcount(TParm parm,TConnection connection){
		TParm parmValue=new TParm();
		TParm result=null;
		TParm logParm=parm.getParm("LOG_PARM");//��־������
		if (null!=parm.getValue("CHECK_FLG") &&parm.getValue("CHECK_FLG").equals("Y")) {//��ѯ�Ƿ�������ѡ��ȷ�϶���
			parmValue.setData("BATCHNAME",parm.getValue("CHEKC_DATE"));
			parmValue.setData("EXPTABLENAME",logParm.getValue("EXPTABLENAME"));
			parmValue.setData("SOURCESYSTEM",logParm.getValue("SOURCESYSTEM"));//�ⲿϵͳ
			result=BILFinanceTool.getInstance().deleteDiIncomeCostAcountByBatchname(parmValue, connection);
			if (result.getErrCode()<0) {
				connection.rollback();
				connection.close();
				return result;
			}
			result=BILFinanceTool.getInstance().deleteDiExpLog(parmValue, connection);//ɾ����־����ĳɱ�����
			if (result.getErrCode()<0) {
				connection.rollback();
				connection.close();
				return result;
			}
		}
		for (int i = 0; i < parm.getCount(); i++) {
			parmValue=parm.getRow(i);
			//�����ɱ�����
			result=BILFinanceTool.getInstance().insertDiIncomeCostAcount(parmValue, connection);
			if (result.getErrCode()<0) {
				connection.rollback();
				connection.close();
				return result;
			}
		}
		//����log��
		
		//logParm.setData("BUSITYPENAME",parm.getValue("BUSITYPENAME_SUM"));
		result=BILFinanceTool.getInstance().insertDiExpLog(logParm, connection);
		if (result.getErrCode()<0) {
			connection.rollback();
			connection.close();
			return result;
		}
		if(!parm.getBoolean("LOCAL_LOG_FLG")){
			//���뱾�����ݿ�
			TConnection localConnection = TDBPoolManager.getInstance().getConnection();;
			for (int i = 0; i < parm.getCount(); i++) {
				parmValue=parm.getRow(i);
				if(!"02".equals(parmValue.getValue("PK_ITEM"))){//�������ݲ�����־��
					continue;
				}
				result = new TParm(TJDODBTool.getInstance().update(getInsertDiIncomeCostSql(parmValue), localConnection));
				if(result.getErrCode() < 0){
					localConnection.rollback();
					localConnection.close();
					connection.rollback();
					connection.close();
					return result;
				}
			}
			localConnection.commit();
			localConnection.close();
		}
		
		connection.commit();
		connection.close();
		return result;
	}
	
	public String getInsertDiIncomeCostSql(TParm parm){
		String sql = "INSERT INTO DI_SERVICEVOLEXP_LOG (PK_SERVICEVOLEXP,PK_EXPLOG,BATCHNAME,"+
			" BUSITYPENAME,EXPTIME,CYEAR,CMONTH,CDAY,PK_CORP,CORPNAME,"+
			" PK_ITEM,ITEMNAME,VOLUME,PK_DIAGNOSISDEPT,DIAGNOSISDEPTNAME,"+
			" PK_OUTERSYSTEM,SOURCESYSTEM,TYPE) "+
			" VALUES( '"+parm.getValue("PK_SERVICEVOLEXP")+"','"+parm.getValue("PK_EXPLOG")+"'," +
					" '"+parm.getValue("BATCHNAME")+"'," +
					" '"+parm.getValue("BUSITYPENAME")+"',"+
					" '"+parm.getValue("EXPTIME")+"',"+
					" '"+parm.getValue("CYEAR")+"',"+
					" '"+parm.getValue("CMONTH")+"',"+
					" '"+parm.getValue("CDAY")+"',"+
					" '"+parm.getValue("PK_CORP")+"',"+
					" '"+parm.getValue("CORPNAME")+"',"+
					" '"+parm.getValue("PK_ITEM")+"',"+
					" '"+parm.getValue("ITEMNAME")+"',"+
					" '"+parm.getValue("VOLUME")+"',"+
					" '"+parm.getValue("PK_DIAGNOSISDEPT")+"',"+
					" '"+parm.getValue("DIAGNOSISDEPTNAME")+"',"+
					" '"+parm.getValue("PK_OUTERSYSTEM")+"',"+
					" '"+parm.getValue("SOURCESYSTEM")+"',"+
					" '"+parm.getValue("TYPE")+"' )";
		System.out.println("sql������������������"+sql);
		
		return sql;
	}
}
