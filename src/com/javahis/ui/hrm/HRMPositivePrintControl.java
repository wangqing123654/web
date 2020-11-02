package com.javahis.ui.hrm;

import jdo.hrm.HRMContractD;
import jdo.hrm.HRMOrder;
import jdo.hrm.HRMPatAdm;
import jdo.hrm.HRMPatInfo;
import jdo.sys.Operator;
import jdo.sys.PatTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TWord;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import com.javahis.util.ExportExcelUtil;
/**
 * <p> Title: ����������Լ���������� </p>
 *
 * <p> Description: ����������Լ���������� </p>
 *
 * <p> Copyright: javahis 20090922 </p>
 *
 * <p> Company:JavaHis </p>
 *
 * @author ehui
 * @version 1.0
 */
public class HRMPositivePrintControl extends TControl {
	//��ӡ��ʾ��
	private TWord word;
	//������롢��ͬ����
	private String companyCode,contractCode;
	//��ͬ����
	private HRMContractD contractD;
	//��������
	private HRMPatAdm adm;
	//������Ϣ����
	private HRMPatInfo pat;
	//ҽ������
	private HRMOrder order;
	//��ͬ�ؼ�
	private TTextFormat contract,patName;
        /**
         * ��ӡEXECL����
         */
        private TParm[] execleTable;
	 /**
     * ��ʼ��
     */
    public void onInit() {
        super.onInit();
        initComponent();
        onClear();
    }
    /**
     * ����¼�
     */
    public void onClear() {
    	this.clearValue("START_DATE;END_DATE;MR_NO;COMPANY_CODE;CONTRACT_CODE;CASE_NO");
		word.onNewFile();
		word.update();
		contractD=new HRMContractD();
		adm=new HRMPatAdm();
		pat=new HRMPatInfo();
		order=new HRMOrder();
	}
    /**
     * �������ѡ���¼�
     */
    public void onCompanyChoose(){
    	//����ѡ���������룬���첢��ʼ��������ĺ�ͬ��ϢTTextFormat
		companyCode=this.getValueString("COMPANY_CODE");
                if(companyCode.length()==0){
                    return;
                }
		TParm contractParm=contractD.onQueryByCompany(companyCode);
		if(contractParm.getErrCode()!=0){
			this.messageBox_("û������");
		}
		// System.out.println("contractParm="+contractParm);
		contract.setPopupMenuData(contractParm);
		contract.setComboSelectRow();
		contract.popupMenuShowData();
		contractCode=contractParm.getValue("ID",0);
		if(StringUtil.isNullString(contractCode)){
			this.messageBox_("��ѯʧ��");
			return;
		}
		contract.setValue(contractCode);
		//����ѡ��ĺ�ͬ���룬���첢��ʼ���ú�ͬ�Ĳ�����ϢTTextFormat
		TParm patParm=adm.getTParmByCompanyAndContract(companyCode, contractCode);
		// System.out.println("patParm="+patParm);
		patName.setPopupMenuData(patParm);
		patName.setComboSelectRow();
		patName.popupMenuShowData();
    }
    /**
     * ��ͬ��Ϣѡ���¼�
     */
    public void onContractChoose(){
    	companyCode=this.getValueString("COMPANY_CODE");
    	contractCode=this.getValueString("CONTRACT_CODE");
    	//����ѡ��ĺ�ͬ���룬���첢��ʼ���ú�ͬ�Ĳ�����ϢTTextFormat
		TParm patParm=adm.getTParmByCompanyAndContract(companyCode, contractCode);
		// System.out.println("patParm="+patParm);
		patName.setPopupMenuData(patParm);
		patName.setComboSelectRow();
		patName.popupMenuShowData();
		TParm parm=getCompanyTParm();
		if(parm==null){
			return;
		}
		word.setWordParameter(parm);
		word.setPreview(true);
		word.setFileName("%ROOT%\\config\\prt\\HRM\\HRMPositiveReport.jhw");
    }
    /**
     * �����������ͺ�ͬ����鵽�ú�ͬ������CASE_NO����ѭ������CASE�����ݺϲ�
     * @return
     */
    public TParm getCompanyTParm(){
    	TParm result=new TParm();
    	if(StringUtil.isNullString(companyCode)||StringUtil.isNullString(contractCode)){
    		this.messageBox_("param is null");
    		return null;
    	}
    	if(!StringUtil.isNullString(this.getValueString("MR_NO"))||!StringUtil.isNullString(this.getValueString("CASE_NO"))){
    		this.messageBox_("���˲�ѯ");
    		return null;
    	}
    	TParm caseParm=adm.getTParmByCompanyAndContract(companyCode,contractCode);
    	if(caseParm==null){
    		this.messageBox_("ȡ�ò�����������ʧ��");
    		return null;
    	}
    	if(caseParm.getErrCode()!=0){
    		this.messageBox_("ȡ�ò�����������ʧ��");
    		return null;
    	}
    	if(caseParm.getCount()<=0){
    		this.messageBox_("û������");
    		return null;
    	}
    	int count=caseParm.getCount();
    	TParm lisParmAll=new TParm();
    	TParm risParmAll=new TParm();
    	for(int i=0;i<count;i++){
    		String tempCase=caseParm.getValue("ID",i);
    		if(StringUtil.isNullString(tempCase)){
    			this.messageBox_("caseNo is null");
    			return null;
    		}
    		TParm lisParm=order.getLisPositiveParmByCase(tempCase);
    		if(lisParm==null){
    			this.messageBox_("lisParm is null");
    			return null;
    		}
    		lisParmAll=addTParm(lisParmAll,lisParm,"LIS");
    		TParm risParm=order.getRisPositiveParmByCase(tempCase);
    		if(risParm==null){
    			this.messageBox_("risParm is null");
    			return null;
    		}
    		risParmAll=addTParm(risParmAll,risParm,"RIS");
    	}
        lisParmAll.addData("SYSTEM", "COLUMNS", "PAT_NAME");
        lisParmAll.addData("SYSTEM", "COLUMNS", "MR_NO");
        lisParmAll.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
        lisParmAll.addData("SYSTEM", "COLUMNS", "TESTITEM_CHN_DESC");
        lisParmAll.addData("SYSTEM", "COLUMNS", "TEST_VALUE");
        lisParmAll.addData("SYSTEM", "COLUMNS", "TEST_UNIT");
        lisParmAll.addData("SYSTEM", "COLUMNS", "LOWER_LIMIT");
        lisParmAll.addData("SYSTEM", "COLUMNS", "UPPE_LIMIT");
        lisParmAll.setCount(lisParmAll.getCount("MR_NO"));

        risParmAll.addData("SYSTEM", "COLUMNS", "PAT_NAME");
        risParmAll.addData("SYSTEM", "COLUMNS", "MR_NO");
        risParmAll.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
        risParmAll.addData("SYSTEM", "COLUMNS", "OUTCOME_DESCRIBE");
        risParmAll.addData("SYSTEM", "COLUMNS", "OUTCOME_CONCLUSION");
        risParmAll.setCount(risParmAll.getCount("MR_NO"));

    	TTextFormat company=(TTextFormat)this.getComponent("COMPANY_CODE");
    	TTextFormat contract=(TTextFormat)this.getComponent("CONTRACT_CODE");
    	result.setData("NAME","TEXT","�������ƣ�"+company.getTableShowValue("NAME"));
    	result.setData("CONTRACT_NAME","TEXT","��ͬ���ƣ�"+contract.getTableShowValue("NAME"));
    	result.setData("STATUS1","TEXT","���Ա��������"+getRepCount(lisParmAll,risParmAll));
    	result.setData("STATUS2","TEXT","��������"+caseParm.getCount());
    	result.setData("PRINT_TIME","TEXT","��ӡʱ�䣺"+StringTool.getString(order.getDBTime(),"yyyy/MM/dd HH:mm"));
    	result.setData("PRINT_USER","TEXT","�Ʊ���:"+Operator.getName());
    	result.setData("LIS_TABLE",lisParmAll.getData());
    	result.setData("RIS_TABLE",risParmAll.getData());
        creatExecleData(result);
    	return result;
    }
    /**
     * ��֯��ӡEXECLE����
     * @param parm TParm
     */
    public void creatExecleData(TParm parm){
        TParm table1 = parm.getParm("LIS_TABLE");
        table1.setData("TITLE","�������Ա���");
        table1.setData("HEAD","��������,120;������,120;ҽ������,200;������Ŀ,120;����ֵ,100;��λ,120;����ֵ,100;����ֵ,100");
        TParm table2 = parm.getParm("RIS_TABLE");
        table2.setData("TITLE","������Ա���");
        table2.setData("HEAD","��������,120;������,120;ҽ������,200;�������,200;������,200");
        this.execleTable = new TParm[]{table1,table2};
    }
    /**
     * �õ��쳣������
     * @param lisParm TParm
     * @param risParm TParm
     * @return int
     */
    public int getRepCount(TParm lisParm,TParm risParm){
        Set countRep = new HashSet();
        int rowLisCount = lisParm.getCount("MR_NO");
        int rowRisCount = risParm.getCount("MR_NO");
        for(int i=0;i<rowLisCount;i++){
            countRep.add(lisParm.getValue("PAT_NAME",i));
        }
        for(int i=0;i<rowRisCount;i++){
            countRep.add(risParm.getValue("PAT_NAME",i));
        }
        return countRep.size();
    }
    /**
     * ����LIS,����RIS ,�ۼ�TParm
     * @param main
     * @param temp
     * @param cat1Type
     * @return
     */
    public TParm addTParm(TParm main,TParm temp,String cat1Type){
    	String names="";
    	if("LIS".equalsIgnoreCase(cat1Type)){
//                names="PAT_NAME;MR_NO;ORDER_DESC;TESTITEM_CHN_DESC;TEST_VALUE;TEST_UNIT;LOWER_LIMIT;UPPE_LIMIT;CASE_NO;SEX_CODE;BIRTH_DATE;ORDER_CODE";
                names="PAT_NAME;MR_NO;ORDER_DESC;TESTITEM_CHN_DESC;TEST_VALUE;TEST_UNIT;LOWER_LIMIT;UPPE_LIMIT";
    	}else{
    		names="PAT_NAME;MR_NO;ORDER_DESC;OUTCOME_DESCRIBE;OUTCOME_CONCLUSION ";
    	}
    	int count=temp.getCount();
    	if(count<=0){
    		return main;
    	}
    	for(int i=0;i<count;i++){
    		main.addRowData(temp, i, names);
//                main.addData("SYSTEM", "COLUMNS", names);
    	}
//        if("LIS".equalsIgnoreCase(cat1Type)){
//            main.addData("SYSTEM", "COLUMNS", "PAT_NAME");
//            main.addData("SYSTEM", "COLUMNS", "MR_NO");
//            main.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
//            main.addData("SYSTEM", "COLUMNS", "TESTITEM_CHN_DESC");
//            main.addData("SYSTEM", "COLUMNS", "TEST_VALUE");
//            main.addData("SYSTEM", "COLUMNS", "TEST_UNIT");
//            main.addData("SYSTEM", "COLUMNS", "LOWER_LIMIT");
//            main.addData("SYSTEM", "COLUMNS", "UPPE_LIMIT");
//        }else{
//            main.addData("SYSTEM", "COLUMNS", "PAT_NAME");
//            main.addData("SYSTEM", "COLUMNS", "MR_NO");
//            main.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
//            main.addData("SYSTEM", "COLUMNS", "OUTCOME_DESCRIBE");
//            main.addData("SYSTEM", "COLUMNS", "OUTCOME_CONCLUSION");
//        }
//        main.setCount(main.getCount("MR_NO"));
    	return main;
    }
	/**
     * ��ʼ���ؼ�
     */
	private void initComponent() {
		word=(TWord)this.getComponent("WORD");
		contract=(TTextFormat)this.getComponent("CONTRACT_CODE");
		patName=(TTextFormat)this.getComponent("CASE_NO");
	}
	/**
	 * ���ݲ����Ų�ѯ
	 */
	public void onMrNo(String mrNo){
		 mrNo=StringUtil.isNullString(mrNo) ? this.getValueString("MR_NO"): mrNo;
		if(StringUtil.isNullString(mrNo)){
			return;
		}
		mrNo = PatTool.getInstance().checkMrno(mrNo);
		this.setValue("MR_NO", mrNo);
		TParm same = pat.getCaseByMr(mrNo);
		String tempCase="";
		if (same.getCount() > 0) {
			TParm patParm = new TParm();
			Object obj = (TParm) openDialog(
					"%ROOT%\\config\\sys\\SYSPatChoose.x", same);
			if (obj != null) {
				patParm = (TParm) obj;
				tempCase=patParm.getValue("CASE_NO");
			}
		}
		if(StringUtil.isNullString( tempCase)){
			this.messageBox_("ȡ�ò�������ʧ��");
			return;
		}
		TParm parm=new TParm();
		TParm lisParm=order.getLisPositiveParmByCase(tempCase);
		// System.out.println("lisParm==="+lisParm);
		if(lisParm==null){
			this.messageBox_("ȡ�ü�������ʧ��");
			return;
		}
		TParm risParm=order.getRisPositiveParmByCase(tempCase);
		if(risParm==null){
			this.messageBox_("ȡ�ü�������ʧ��");
			return;
		}
//                lisParm.addData("SYSTEM", "COLUMNS", "PAT_NAME");
//                lisParm.addData("SYSTEM", "COLUMNS", "MR_NO");
//                lisParm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
//                lisParm.addData("SYSTEM", "COLUMNS", "TESTITEM_CHN_DESC");
//                lisParm.addData("SYSTEM", "COLUMNS", "TEST_VALUE");
//                lisParm.addData("SYSTEM", "COLUMNS", "TEST_UNIT");
//                lisParm.addData("SYSTEM", "COLUMNS", "LOWER_LIMIT");
//                lisParm.addData("SYSTEM", "COLUMNS", "UPPE_LIMIT");
//                lisParm.setCount(lisParm.getCount("MR_NO"));
//
//                risParm.addData("SYSTEM", "COLUMNS", "PAT_NAME");
//                risParm.addData("SYSTEM", "COLUMNS", "MR_NO");
//                risParm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
//                risParm.addData("SYSTEM", "COLUMNS", "OUTCOME_DESCRIBE");
//                risParm.addData("SYSTEM", "COLUMNS", "OUTCOME_CONCLUSION");
//                risParm.setCount(risParm.getCount("MR_NO"));
		String patName=same.getValue("PAT_NAME",0);
		String sexCode=same.getValue("SEX_CODE",0);
		String birthday=StringTool.getString(same.getTimestamp("BIRTH_DATE",0),"yyyy/MM/dd");
		parm.setData("NAME","TEXT","������"+patName);
		parm.setData("CONTRACT_NAME","TEXT","�����ţ�"+mrNo);
		parm.setData("STATUS1","TEXT","�Ա�"+StringUtil.getDesc("SYS_DICTIONARY", "CHN_DESC", " GROUP_ID='SYS_SEX' AND ID='" +sexCode+"'"));
		parm.setData("STATUS2","TEXT","�������ڣ�"+birthday);
		parm.setData("PRINT_TIME","TEXT","��ӡʱ�䣺"+StringTool.getString(order.getDBTime(),"yyyy/MM/dd HH:mm"));
		parm.setData("PRINT_USER","TEXT","�Ʊ���:"+Operator.getName());
		parm.setData("LIS_TABLE",lisParm.getData());
		parm.setData("RIS_TABLE",risParm.getData());
                creatExecleData(parm);
		word.setWordParameter(parm);
		word.setPreview(true);
		word.setFileName("%ROOT%\\config\\prt\\HRM\\HRMPositiveReport.jhw");
	}
	/**
	 * ����Ա����Ϣ
	 */
	public void onCaseNo(){
		String tempCase=this.getValueString("CASE_NO");
		TTextFormat contract=(TTextFormat)this.getComponent("CASE_NO");
		onMrNo(tempCase);
		if(1==1){
			return ;
		}
		if(StringUtil.isNullString( tempCase)){
			this.messageBox_("ȡ�ò�������ʧ��");
			return;
		}
		TParm parm=new TParm();
		TParm lisParm=order.getLisPositiveParmByCase(tempCase);
		// System.out.println("lisParm="+lisParm);
		if(lisParm==null){
			this.messageBox_("ȡ�ü�������ʧ��");
			return;
		}
		TParm risParm=order.getRisPositiveParmByCase(tempCase);
                // System.out.println("risParm=="+risParm);
                // System.out.println("risParm="+risParm);
		if(risParm==null){
			this.messageBox_("ȡ�ü�������ʧ��");
			return;
		}
//                lisParm.addData("SYSTEM", "COLUMNS", "PAT_NAME");
//                lisParm.addData("SYSTEM", "COLUMNS", "MR_NO");
//                lisParm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
//                lisParm.addData("SYSTEM", "COLUMNS", "TESTITEM_CHN_DESC");
//                lisParm.addData("SYSTEM", "COLUMNS", "TEST_VALUE");
//                lisParm.addData("SYSTEM", "COLUMNS", "TEST_UNIT");
//                lisParm.addData("SYSTEM", "COLUMNS", "LOWER_LIMIT");
//                lisParm.addData("SYSTEM", "COLUMNS", "UPPE_LIMIT");
//                lisParm.setCount(lisParm.getCount("MR_NO"));
//
//                risParm.addData("SYSTEM", "COLUMNS", "PAT_NAME");
//                risParm.addData("SYSTEM", "COLUMNS", "MR_NO");
//                risParm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
//                risParm.addData("SYSTEM", "COLUMNS", "OUTCOME_DESCRIBE");
//                risParm.addData("SYSTEM", "COLUMNS", "OUTCOME_CONCLUSION");
//                risParm.setCount(risParm.getCount("MR_NO"));

		String patName=lisParm.getValue("PAT_NAME",0);
		String sexCode=lisParm.getValue("SEX_CODE",0);
		String birthday=StringTool.getString(lisParm.getTimestamp("BIRTH_DATE",0),"yyyy/MM/dd");
		parm.setData("NAME","TEXT","����1��"+patName);
		parm.setData("CONTRACT_NAME","TEXT","�����ţ�"+lisParm.getValue("MR_NO",0));
		parm.setData("STATUS1","TEXT","�Ա�"+StringUtil.getDesc("SYS_DICTIONARY", "CHN_DESC", " GROUP_ID='SYS_SEX' AND ID='" +sexCode+"'"));
		parm.setData("STATUS2","TEXT","�������ڣ�"+birthday);
		parm.setData("LIS_TABLE",lisParm.getData());
		parm.setData("RIS_TABLE",risParm.getData());
		parm.setData("PRINT_TIME","TEXT","��ӡʱ�䣺"+StringTool.getString(order.getDBTime(),"yyyy/MM/dd HH:mm"));
		parm.setData("PRINT_USER","TEXT","�Ʊ���:"+Operator.getName());
                creatExecleData(parm);
		word.setWordParameter(parm);
		word.setPreview(true);
		word.setFileName("%ROOT%\\config\\prt\\HRM\\HRMPositiveReport.jhw");
	}
	/**
	 * ��ӡ
	 */
	public void onPrint(){
		if(StringUtil.isNullString(word.getFileName())){
			return;
		}
		word.onPreviewWord();
		word.print();
	}
        /**
         * ����EXECLE
         */
        public void onExecl(){
            if(this.execleTable==null||this.execleTable.length==0){
                this.messageBox("û����Ҫ����������!");
                return;
            }
            ExportExcelUtil.getInstance().exeSaveExcel(this.execleTable,"������������Ա���");
        }
}
