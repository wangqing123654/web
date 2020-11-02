package com.javahis.ui.hrm;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Pattern;
import jdo.bil.BILComparator;
import jdo.hrm.HRMOrder;
import jdo.hrm.HRMPatAdm;
import jdo.hrm.HRMPatInfo;
import jdo.hrm.HRMSchdayDr;
import jdo.odo.MedApply;
import jdo.sys.Operator;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;
import jdo.util.Manager;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.tui.text.CopyOperator;
import com.dongyang.tui.text.ECapture;
import com.dongyang.tui.text.EComponent;
import com.dongyang.tui.text.ETD;
import com.dongyang.tui.text.ETR;
import com.dongyang.tui.text.ETable;
import com.dongyang.tui.text.EText;
import com.dongyang.ui.TMovePane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TWindow;
import com.dongyang.ui.TWord;
import com.dongyang.util.ImageTool;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.EmrUtil;
import com.javahis.util.OdiUtil;
import com.javahis.util.StringUtil;

/**
 * <p>Title: 健检总检 </p>
 *
 * <p>Description: 健检总检 </p>
 *
 * <p>Copyright: javahis 20090922 </p>
 *
 * <p>Company:JavaHis </p>
 *
 * @author ehui
 * @version 1.0
 */
public class HRMTotViewControl extends TControl {

    // 患者信息TABLE,医嘱TABLE
    private TTable patTable, orderTable;
    // 病历书写器
    private TWord word;
    // 医嘱对象
    private HRMOrder order;
    // 结构化病例是否为文件
    private boolean isOld;
    // EMR共用数据
    private TParm pathParm;
    // 保存时使用的fileNo
    private String fileNo;
    private BILComparator compare = new BILComparator();// add by wanglong 20150515
    private boolean ascending = false;
    private int sortColumn = -1;
    
    /**
     * 初始化事件
     */
    public void onInit() {
        super.onInit();
        // 初始化控件
        initComponent();
        // initData();
        // 清空
        onClear();
    }

    /**
     * 初始化控件
     */
    private void initComponent() {
        patTable = (TTable) this.getComponent("PAT_TABLE");
        addSortListener(patTable);// add by wanglong 20130515
        orderTable = (TTable) this.getComponent("ORDER_TABLE");
        word = (TWord) this.getComponent("WORD");
    }

	/**
	 * 清空事件
	 */
    public void onClear() {
        initData();
        patTable.removeRowAll();
        orderTable.removeRowAll();
        word.onNewFile();
        word.update();
        fileNo = "";
        this.clearText("MR_NO;PAT_NAME;SEX_CODE;AGE");
        clearValue("UNDONE_NUM;DONE_NUM;ALL_NUM");// add by wanglong 20130328
    }

    /**
     * 初始化数据
     */
    private void initData() {
        Timestamp now = TJDODBTool.getInstance().getDBTime();
        String date = StringTool.getString(now, "yyyyMMdd");
        this.setValue("START_DATE", StringTool.getTimestamp(date + "000000", "yyyyMMddHHmmss"));
        this.setValue("END_DATE", StringTool.getTimestamp(date + "235959", "yyyyMMddHHmmss"));
        // Timestamp tomorrow=StringTool.rollDate(now, 1L);
        // this.setValue("START_DATE", now);
        // this.setValue("END_DATE", tomorrow);
        order = new HRMOrder();
        this.setValue("UNDONE", "Y");
        String deptAtt = HRMSchdayDr.getDeptAttribute();
        if (StringUtil.isNullString(deptAtt)) {
            this.messageBox_("取得科别属性错误");
            return;
        }
        this.setValue("DEPT_ATT", deptAtt);
    }

    /**
     * 点击日期和科别属性时，执行查询
     */
    public void onDoQuery() {
        onQuery();
    }
    
	/**
	 * 查询
	 */
    public void onQuery() {
        Timestamp now = (Timestamp) this.getValue("START_DATE");
        Timestamp tomorrow = (Timestamp) this.getValue("END_DATE");
        String startDate = StringTool.getString(now, "yyyyMMdd") + "000000";
        String endDate = StringTool.getString(tomorrow, "yyyyMMdd") + "235959";
        String isUndone = "";
        if (this.getValueBoolean("UNDONE")) {
            isUndone = "1";
        } else if (this.getValueBoolean("DONE")) {
            isUndone = "2";
        } else {
            isUndone = "3";
        }
        order = new HRMOrder();
        TParm patParm = order.getFinalCheckPat(startDate, endDate, isUndone);
        TParm patParm1 = order.getFinalCheckPat(startDate, endDate, "1");
        int count1 = patParm1.getCount() <= 0 ? 0 : patParm1.getCount();
        TParm patParm2 = order.getFinalCheckPat(startDate, endDate, "2");
        int count2 = patParm2.getCount() <= 0 ? 0 : patParm2.getCount();
        TParm patParm3 = order.getFinalCheckPat(startDate, endDate, "");
        int count3 = patParm3.getCount() <= 0 ? 0 : patParm3.getCount();
        this.setValue("UNDONE_NUM", count1 + "人");
        this.setValue("DONE_NUM", count2 + "人");
        this.setValue("ALL_NUM", count3 + "人");
        if (patParm.getErrCode() != 0) {
            this.messageBox_("查询失败 " + patParm.getErrText());
            patTable.removeRowAll();
            return;
        }
        if (patParm.getCount() <= 0) {
            patTable.removeRowAll();
            return;
        }
        fileNo = "";
        patTable.setParmValue(calcuAge(patParm));
    }

    /**
     * 计算给入TParm 中的年龄
     * 
     * @param parm
     * @return
     */
    private TParm calcuAge(TParm parm) {
        if (parm == null) {
            return parm;
        }
        int count = parm.getCount();
        if (count <= 0) {
            return parm;
        }
        boolean isDone = TypeTool.getBoolean(this.getValue("DONE"));
        for (int i = 0; i < count; i++) {
            // Timestamp birth=parm.getTimestamp("BIRTHDAY",i);
            // System.out.println("birth="+birth);
            // if(birth==null){
            // continue;
            // }
            // String age=StringUtil.showAge(birth, TJDODBTool.getInstance().getDBTime());
            // parm.setData("AGE",i,age);
            if (isDone) {
                parm.setData("DONE", i, "Y");
            } else {
                parm.setData("DONE", i, "N");
            }
        }
        return parm;
    }

    /**
     * 总检抓取
     */
    public void onCrawl() {
        if (this.word.getFileOpenName().length() == 0) {
            this.messageBox("无总检文件");
            return;
        }
        int row = patTable.getSelectedRow();
        TParm parm = patTable.getParmValue();
        getEmrAddress(parm.getValue("CASE_NO", row), "04");
        // TWord word = new TWord();
        // word.onOpen("//10//12//000000002078", "101221000014_健检总检_05", 3, false);
        // word.setFileRemark("05|01|06");
        // word.onSave();
        // // System.out.println("" + word.getFileRemark());
    }

    /**
     * 总检部分抓取
     */
    public void onCrawls() {
        // this.messageBox("-------come in===========");
        if (this.word.getFileOpenName().length() == 0) {
            this.messageBox("无总检文件");
            return;
        }
        // 取到人
        int row = patTable.getSelectedRow();
        TParm parm = patTable.getParmValue();
        // System.out.println("-----orderTable----"+ orderTable.getParmValue());
        List<TParm> orders = new ArrayList<TParm>();
        this.orderTable.acceptText();
        // 取要生成的文件
        for (int i = 0; i < orderTable.getParmValue().getCount("ORDER_DESC"); i++) {
            if ((orderTable.getParmValue().getValue("SFLG", i)).equals("Y")) {
                // System.out.println("-----SFLG is Y-------"+orderTable.getParmValue().getRow(i));
                orders.add(orderTable.getParmValue().getRow(i));
            }
        }
        // 如果没有选择的任何文件， 报提示
        if (orders.size() <= 0) {
            this.messageBox("请选择要抓取的问诊医嘱！");
            return;
        }
        // 将文件内容追加到总检最后
        this.onMerge(parm.getValue("CASE_NO", row), "04", orders);
    }

	/**
	 * 病患选择
	 */
	public void onPatChoose(){
		int row=patTable.getSelectedRow();
		TParm parm=patTable.getParmValue();
		if(parm==null){
			return;
		}
		int count=parm.getCount();
		if(count<=0){
			return;
		}
		
		String caseNo=parm.getValue("CASE_NO",row);
		if(StringUtil.isNullString(caseNo)){
			this.messageBox_("取得医嘱数据失败");
			return;
		}
		this.setValue("MR_NO", parm.getValue("MR_NO",row));
		this.setValue("PAT_NAME", parm.getValue("PAT_NAME",row));
		this.setValue("SEX_CODE", parm.getValue("SEX_CODE",row));
		this.setValue("BIRTHDAY", parm.getTimestamp("BIRTHDAY",row));
		fileNo="";
		getPatData(caseNo);
    }

    /**
     * 病患选择
     */
    public void onPatChooseFullWindow() {
        onPatChoose();
        onUnfold();
    }

    /**
     * 根据给入CASE_NO查询数据
     * 
     * @param caseNo
     */
    private void getPatData(String caseNo) {
        if (StringUtil.isNullString(caseNo)) {
            return;
        }
        // System.out.println("----come in getPatData-----");
        TParm parm = order.getParmForReview(caseNo);
        if (parm.getErrCode() != 0) {
            this.messageBox_("取得医嘱数据失败");
            this.messageBox_(parm.getErrText());
            return;
        }
        int count = parm.getCount();
        if (count <= 0) {
            return;
        }
        TParm review = new TParm();
        for (int i = 0; i < count; i++) {
            if ("04".equalsIgnoreCase(parm.getValue("DEPT_ATTRIBUTE", i))) {
                review = parm.getRow(i);
                break;
            }
        }
        orderTable.setParmValue(parm);
        openFile(review);
    }

    /**
     * 根据给入信息打开结构化病历
     * 
     * @param parm
     */
    private void openFile(TParm parm) {
        if ("LIS".equals(parm.getValue("CAT1_TYPE")) || "RIS".equals(parm.getValue("CAT1_TYPE"))) {
            return;
        }
        TParm emrParm = new TParm();
        emrParm.setData("MR_CODE", parm.getValue("MR_CODE"));
        emrParm.setData("CASE_NO", parm.getValue("CASE_NO"));
        emrParm.setData("ADM_TYPE", "H");
        emrParm = EmrUtil.getInstance().getEmrFilePath(emrParm);
        if (StringUtil.isNullString(emrParm.getValue("SUBCLASS_CODE"))) {
            this.messageBox_("打开病历错误");
            return;
        }
        pathParm = emrParm;
        isOld = TypeTool.getBoolean(emrParm.getData("FLG"));
        //modify by wanglong 20130522
        String caseNo = parm.getValue("CASE_NO");
        String mrNo = parm.getValue("MR_NO");
        HRMPatInfo pat = new HRMPatInfo();
        TParm patParm = pat.getHRMPatInfo(mrNo, caseNo);
        emrParm.setData("PAT_NAME", "TEXT", patParm.getValue("PAT_NAME", 0));
        emrParm.setData("TEL_HOME","TEXT",  patParm.getValue("TEL", 0));
        emrParm.setData("SEX", "TEXT", getDictionary("SYS_SEX", patParm.getValue("SEX_CODE", 0)));
        emrParm.setData("AGE", "TEXT", StringUtil.showAge(patParm.getTimestamp("BIRTHDAY", 0), TJDODBTool.getInstance().getDBTime()));
        emrParm.setData("MARRY","TEXT",  getDictionary("SYS_MARRIAGE", patParm.getValue("MARRIAGE_CODE", 0)));
        emrParm.setData("CHECK_DATE", "TEXT", StringTool.getString(patParm.getTimestamp("START_DATE", 0), "yyyy/MM/dd"));
        //add by wanglong 20130813
        emrParm.setData("COMPANY_DESC", "TEXT", patParm.getValue("COMPANY_DESC", 0));// 团体
        emrParm.setData("CONTRACT_DESC", "TEXT", patParm.getValue("CONTRACT_DESC", 0));// 合同
        emrParm.setData("SEQ_NO", "TEXT", patParm.getValue("SEQ_NO", 0));// 序号
        emrParm.setData("PAT_DEPT", "TEXT", patParm.getValue("PAT_DEPT", 0));// 部门
        emrParm.setData("STAFF_NO", "TEXT", patParm.getValue("STAFF_NO", 0));// 工号
        //modify end
        emrParm.setData("MR_NO", parm.getValue("MR_NO"));
        emrParm.setData("CASE_NO", parm.getValue("CASE_NO"));
        emrParm.setData("TXT_MR_NO", parm.getValue("MR_NO"));
        emrParm.setData("REAL_MR", parm.getValue("MR_NO"));
        emrParm.setData("PAT_NAME", parm.getValue("PAT_NAME"));
        Timestamp repDate = EmrUtil.getInstance().getReportDate(parm.getValue("CASE_NO")).getTimestamp("REPORT_DATE");
        emrParm.setData("ADM_DATE", repDate);
        emrParm.setData("FILE_TITLE_TEXT", "TEXT", Manager.getOrganization().getHospitalCHNFullName(Operator.getRegion()));
        emrParm.setData("FILE_TITLEENG_TEXT", "TEXT", Manager.getOrganization().getHospitalENGFullName(Operator.getRegion()));
        emrParm.setData("FILE_HEAD_TITLE_MR_NO", "TEXT", parm.getValue("MR_NO"));
        emrParm.setData("FILE_HEAD_TITLE_IPD_NO", "TEXT", "");
        emrParm.setData("FILE_128CODE", "TEXT", parm.getValue("MR_NO"));
        // ============xueyf modify 20120223 start
        // emrParm.setData("TEMPLET_PATH","JHW\\"+emrParm.getValue("TEMPLET_PATH"));
        emrParm.setData("TEMPLET_PATH", emrParm.getValue("TEMPLET_PATH"));
        // ============xueyf modify 20120223 stop
        // System.out.println("xueyf emrParm="+emrParm);
        if (!isOld) {
            word.onOpen(emrParm.getValue("TEMPLET_PATH"), emrParm.getValue("EMT_FILENAME"), 2, false);
            emrParm.addListener("EMR_SAVE_LISTENER", this, "emrSaveListener");
            word.setWordParameter(emrParm);
            word.setCanEdit(true);
            word.setVisible(false);
            getEmrAddress(parm.getValue("CASE_NO"));
            word.setVisible(true);
            setMicroField(emrParm);
        } else {
            // ============xueyf modify 20120223 start
            String filePath =
                    emrParm.getValue("FILE_PATH").indexOf("JHW") < 0 ? "JHW\\" + emrParm.getValue("FILE_PATH") : emrParm.getValue("FILE_PATH");
            word.onOpen(filePath, emrParm.getValue("FILE_NAME"), 3, false);
            // ============xueyf modify 20120223 stop
            emrParm.addListener("EMR_SAVE_LISTENER", this, "emrSaveListener");
            word.setWordParameter(emrParm);
            word.setCanEdit(true);
        }
        if (!insertTableData(parm.getValue("CASE_NO"), parm.getValue("MR_NO"))) {
            return;
        }

        // 新增一个病案号
        word.setMicroField("MR_NO", mrNo);
        word.setMicroField("PAT_NAME", patParm.getValue("PAT_NAME", 0));
        word.setMicroField("TEL_HOME", patParm.getValue("TEL", 0));
        word.setMicroField("SEX", getDictionary("SYS_SEX", patParm.getValue("SEX_CODE", 0)));
        word.setMicroField("AGE", StringUtil.showAge(patParm.getTimestamp("BIRTHDAY", 0), TJDODBTool.getInstance().getDBTime()));
        word.setMicroField("MARRY", getDictionary("SYS_MARRIAGE", patParm.getValue("MARRIAGE_CODE", 0)));
        word.setMicroField("CHECK_DATE", StringTool.getString(patParm.getTimestamp("START_DATE", 0), "yyyy/MM/dd"));
        // add by wanglong 20130813
        word.setMicroField("COMPANY_DESC", patParm.getValue("COMPANY_DESC", 0));//团体
        word.setMicroField("CONTRACT_DESC", patParm.getValue("CONTRACT_DESC", 0));//合同
        word.setMicroField("SEQ_NO", patParm.getValue("SEQ_NO", 0));//序号
        word.setMicroField("PAT_DEPT", patParm.getValue("PAT_DEPT", 0));//部门
        word.setMicroField("STAFF_NO", patParm.getValue("STAFF_NO", 0));//工号
        // add end
        word.setMicroField("CASE_NO", caseNo);
        word.setMicroField("OPERATOR", Operator.getName());
        word.setMicroField("CURRENTDATE", StringTool.getString(TJDODBTool.getInstance().getDBTime(), "yyyy/MM/dd"));
    }

    /**
     * 设置宏
     */
    public void setMicroField(TParm microParm) {
        Map map = this.getDBTool().select("SELECT A.PAT_NAME AS 姓名,A.IDNO AS 身份证号,TO_CHAR(A.BIRTH_DATE,'YYYY-MM-DD') AS 出生日期,"+
                                          " D.CTZ_DESC AS 付款方式,A.TEL_HOME AS 家庭电话,A.SEX_CODE AS 性别,A.CONTACTS_NAME AS 联系人,"+
                                          " TO_CHAR(A.FIRST_ADM_DATE,'YYYY-MM-DD') AS 初诊日期,A.CELL_PHONE AS 手机,"+
                                          " A.ADDRESS AS 家庭住址,A.OCC_CODE AS 职业,A.COMPANY_DESC AS 工作单位,A.TEL_COMPANY AS 单位电话,"+
                                          " A.RELATION_CODE AS 与患者关系,A.CONTACTS_TEL AS 联系人电话,A.CONTACTS_ADDRESS AS 联系人地址,"+
                                          " A.SPECIES_CODE AS 民族,A.HEIGHT AS 身高,A.WEIGHT AS 体重,A.ADDRESS AS 户籍地址,"+
                                          " A.RESID_POST_CODE AS 户籍邮编,"+
                                          " A.POST_CODE AS 邮编,A.MARRIAGE_CODE AS 婚姻状况,"+
                                          " A.RESID_ADDRESS AS 出生地,A.NATION_CODE AS 国籍 "+
                                          " FROM SYS_PATINFO A,SYS_CTZ D"+
                                          " WHERE A.CTZ1_CODE=D.CTZ_CODE(+)"+
                                          " AND A.MR_NO='"+microParm.getValue("MR_NO")+"'");
        TParm parm = new TParm(map);
        if(parm.getErrCode()<0){
            //取得病人基本资料失败
            this.messageBox("E0110");
            return;
        }
        // System.out.println("parm"+parm.getData("出生日期",0).getClass());
        // System.out.println("parm.getTimestamp(,0)"+parm.getValue("出生日期",0));
        Timestamp tempBirth =
                parm.getValue("出生日期", 0).length() == 0 ? SystemTool.getInstance().getDate()
                        : StringTool.getTimestamp(parm.getValue("出生日期", 0), "yyyy-MM-dd");
        // System.out.println("tempBirth"+tempBirth);
        // System.out.println("this.getAdmDate()"+this.getAdmDate());
        // 计算年龄
        String age = "0";
        if (microParm.getTimestamp("ADM_DATE") != null) age =
                OdiUtil.getInstance().showAge(tempBirth, microParm.getTimestamp("ADM_DATE"));
        else age = "";
        String dateStr = StringTool.getString(SystemTool.getInstance().getDate(),"yyyy/MM/dd HH:mm:ss");
        parm.addData("年龄",age);
        parm.addData("就诊号",microParm.getValue("CASE_NO"));
        parm.addData("病案号",microParm.getValue("MR_NO"));
        parm.addData("住院号",microParm.getValue("IPD_NO"));
        parm.addData("科室",this.getDeptDesc(Operator.getDept()));
        parm.addData("操作者",Operator.getName());
        parm.addData("申请日期",dateStr);
        parm.addData("日期",StringTool.getString(SystemTool.getInstance().getDate(),"yyyy/MM/dd"));
        parm.addData("时间",StringTool.getString(SystemTool.getInstance().getDate(),"HH:mm:ss"));
        parm.addData("病历时间",dateStr);
        parm.addData("入院时间",StringTool.getString(microParm.getTimestamp("ADM_DATE"),"yyyy/MM/dd HH:mm:ss"));
        parm.addData("调用科室",this.getDeptDesc(Operator.getDept()));
        parm.addData("SYSTEM","COLUMNS","年龄");
        parm.addData("SYSTEM","COLUMNS","就诊号");
        parm.addData("SYSTEM","COLUMNS","病案号");
        parm.addData("SYSTEM","COLUMNS","住院号");
        parm.addData("SYSTEM","COLUMNS","科室");
        parm.addData("SYSTEM","COLUMNS","操作者");
        parm.addData("SYSTEM","COLUMNS","申请日期");
        parm.addData("SYSTEM","COLUMNS","病历时间");
        parm.addData("SYSTEM","COLUMNS","入院时间");
        parm.addData("SYSTEM","COLUMNS","调用科室");
        
        
        // 查询住院基本信息(床号，住院诊断)
        TParm odiParm = new TParm(this.getDBTool().select("SELECT B.BED_NO_DESC,C.ICD_CHN_DESC FROM ADM_INP A,SYS_BED B,SYS_DIAGNOSIS C "
                                                        + " WHERE A.CASE_NO=B.CASE_NO "
                                                        + " AND A.MR_NO=B.MR_NO "
                                                        + " AND A.IPD_NO=B.IPD_NO "
                                                        + " AND A.BED_NO=B.BED_NO "
                                                        + " AND A.MAINDIAG=C.ICD_CODE(+) "
                                                        + " AND A.CASE_NO='" + microParm.getValue("CASE_NO") + "'"));
        if (odiParm.getCount() > 0) {
            parm.addData("床号", odiParm.getValue("BED_NO_DESC", 0));
            parm.addData("住院最新诊断", odiParm.getValue("ICD_CHN_DESC", 0));
        } else {
            parm.addData("床号", "");
            parm.addData("住院最新诊断", "");
        }
        if ("HRM".equals("HRM")) {
            parm.addData("门急住别", "健康检查");
            parm.addData("SYSTEM", "COLUMNS", "门急住别");
        }

        //公用信息
        //过敏史(MR_NO)
        StringBuffer drugStr = new StringBuffer();
        TParm drugParm = new TParm(this.getDBTool().select("SELECT A.DRUG_TYPE,A.DRUGORINGRD_CODE,A.ADM_TYPE,"
                                                         + "CASE WHEN A.DRUG_TYPE='A' THEN C.CHN_DESC "
                                                         + "WHEN A.DRUG_TYPE='B' THEN B.ORDER_DESC "
                                                         + "ELSE D.CHN_DESC END ORDER_DESC "
                                                         + " FROM OPD_DRUGALLERGY A,SYS_FEE B, "
                                                         + " (SELECT ID,CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='PHA_INGREDIENT') C, "
                                                         + " (SELECT ID,CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_ALLERGYTYPE') D "
                                                         + " WHERE A.DRUGORINGRD_CODE=B.ORDER_CODE(+) "
                                                         + " AND A.DRUGORINGRD_CODE=C.ID(+) "
                                                         + " AND A.DRUGORINGRD_CODE=D.ID(+) "
                                                         + " AND A.MR_NO='" + microParm.getValue("MR_NO") + "'"));
        if (drugParm.getCount() > 0) {
            int rowCount = drugParm.getCount();
            for (int i = 0; i < rowCount; i++) {
                TParm temp = drugParm.getRow(i);
                drugStr.append(temp.getValue("ORDER_DESC") + ",");
            }
            parm.addData("过敏史", drugStr.toString());
        }else{
            parm.addData("过敏史", "");
        }
        parm.addData("SYSTEM", "COLUMNS", "过敏史");
        //本次看诊门诊主诉现病史(CASE_NO)
        TParm subjParm = new TParm(this.getDBTool().select("SELECT SUBJ_TEXT FROM OPD_SUBJREC WHERE CASE_NO='"+microParm.getValue("CASE_NO")+"'"));
        if(subjParm.getCount()>0){
            parm.addData("主诉现病史", subjParm.getValue("SUBJ_TEXT", 0));
        }else{
            parm.addData("主诉现病史", "");
        }
        parm.addData("SYSTEM", "COLUMNS", "主诉现病史");
        //既往史(MR_NO)
        StringBuffer medhisStr = new StringBuffer();
        TParm medhisParm = new TParm(this.getDBTool().select("SELECT B.ICD_CHN_DESC,A.DESCRIPTION FROM OPD_MEDHISTORY A,SYS_DIAGNOSIS B WHERE A.MR_NO='"+microParm.getValue("MR_NO")+"' AND A.ICD_CODE=B.ICD_CODE"));
        if(medhisParm.getCount()>0){
            int rowCount = medhisParm.getCount();
            for(int i=0;i<rowCount;i++){
                TParm temp = medhisParm.getRow(i);
                medhisStr.append(temp.getValue("ICD_CHN_DESC").length()!=0?temp.getValue("ICD_CHN_DESC"):""+temp.getValue("DESCRIPTION"));
            }
            parm.addData("既往史", medhisStr.toString());
        }else{
            parm.addData("既往史", "");
        }
        parm.addData("SYSTEM", "COLUMNS", "既往史");
        //体征
        TParm sumParm = new TParm(this.getDBTool().select("SELECT TEMPERATURE,PLUSE,RESPIRE,SYSTOLICPRESSURE,DIASTOLICPRESSURE FROM SUM_VTSNTPRDTL WHERE CASE_NO='"+microParm.getValue("CASE_NO")+"' ORDER BY EXAMINE_DATE||EXAMINESESSION DESC"));
        if(sumParm.getCount()>0){
            parm.addData("体温", sumParm.getValue("TEMPERATURE", 0));
            parm.addData("脉搏", sumParm.getValue("PLUSE", 0));
            parm.addData("呼吸", sumParm.getValue("RESPIRE", 0));
            parm.addData("收缩压", sumParm.getValue("SYSTOLICPRESSURE", 0));
            parm.addData("舒张压", sumParm.getValue("DIASTOLICPRESSURE", 0));
        }else{
            parm.addData("体温", "");
            parm.addData("脉搏", "");
            parm.addData("呼吸", "");
            parm.addData("收缩压", "");
            parm.addData("舒张压", "");
        }
        parm.addData("SYSTEM","COLUMNS","体温");
        parm.addData("SYSTEM","COLUMNS","脉搏");
        parm.addData("SYSTEM","COLUMNS","呼吸");
        parm.addData("SYSTEM","COLUMNS","收缩压");
        parm.addData("SYSTEM","COLUMNS","舒张压");
        String names[] = parm.getNames();
        for (String temp : names) {
            // System.out.println(temp+":"+parm.getValue(temp,0));
            if ("性别".equals(temp)) {
                if (parm.getInt(temp, 0) == 9) {
                    this.word.setSexControl(0);
                } else {
                    this.word.setSexControl(parm.getInt(temp, 0));
                }
                this.word.setMicroField(temp, getDictionary("SYS_SEX", parm.getValue(temp,0)));
                this.setCaptureValueArray(temp, getDictionary("SYS_SEX", parm.getValue(temp,0)));
                continue;
            }
            if ("婚姻状况".equals(temp)) {
                this.word.setMicroField(temp, getDictionary("SYS_MARRIAGE", parm.getValue(temp,0)));
                this.setCaptureValueArray(temp, getDictionary("SYS_MARRIAGE", parm.getValue(temp,0)));
                continue;
            }
            if ("国籍".equals(temp)) {
                this.word.setMicroField(temp, getDictionary("SYS_NATION", parm.getValue(temp,0)));
                this.setCaptureValueArray(temp, getDictionary("SYS_NATION", parm.getValue(temp,0)));
                continue;
            }
            if ("民族".equals(temp)) {
                this.word.setMicroField(temp, getDictionary("SYS_SPECIES", parm.getValue(temp,0)));
                this.setCaptureValueArray(temp, getDictionary("SYS_SPECIES", parm.getValue(temp,0)));
                continue;
            }
            if ("与患者关系".equals(temp)) {
                this.word.setMicroField(temp, getDictionary("SYS_RELATIONSHIP", parm.getValue(temp,0)));
                this.setCaptureValueArray(temp, getDictionary("SYS_RELATIONSHIP", parm.getValue(temp,0)));
                continue;
            }
            if ("职业".equals(temp)) {
                this.word.setMicroField(temp, getDictionary("SYS_OCCUPATION", parm.getValue(temp, 0)));
                this.setCaptureValueArray(temp, getDictionary("SYS_OCCUPATION", parm.getValue(temp, 0)));
                continue;
            }
            String tempValue = parm.getValue(temp, 0);
            this.word.setMicroField(temp, tempValue);
            this.setCaptureValueArray(temp, tempValue);
        }
    }

    /**
     * 设置抓取框
     * 
     * @param name String
     * @param value String
     */
    public void setCaptureValueArray(String name, String value) {
        ECapture ecap = this.word.findCapture(name);
        if (ecap == null) return;
        ecap.setFocusLast();
        ecap.clear();
        this.word.pasteString(value);
    }

    /**
     * 拿到科室
     * @param deptCode String
     * @return String
     */
    public String getDeptDesc(String deptCode){
        TParm parm = new TParm(this.getDBTool().select("SELECT DEPT_CHN_DESC FROM SYS_DEPT WHERE DEPT_CODE='"+deptCode+"'"));
        return parm.getValue("DEPT_CHN_DESC",0);
    }

    /**
     * 拿到字典信息
     * @param groupId String
     * @param id String
     * @return String
     */
    public String getDictionary(String groupId,String id){
        String result="";
        TParm parm = new TParm(this.getDBTool().select("SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='"+groupId+"' AND ID='"+id+"'"));
        result = parm.getValue("CHN_DESC",0);
        return result;
    }

    /**
     * 返回数据库操作工具
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }

    /**
     * 删除Table原有数据
     * @param table ETable
     */
    public void removeTableData(ETable table) {
        ETable t = table.getNextTable();
        while (t != null) {
            t.removeThis();
            t = table.getNextTable();
        }
        while (table.size() > 1) table.remove(table.get(table.size() - 1));
        table.setModify(true);
        table.update();
    }
    
	/**
	 * 插入LIS_TABLE\RIS_TABLE的数据
	 * @return
	 */
	private boolean insertTableData(String caseNo,String mrNo){
        MedApply medApply = new MedApply();
        TParm lisParm = medApply.getLisParm(caseNo);
        if (lisParm == null || lisParm.getErrCode() != 0) {
            this.messageBox_("取得检验结果失败");
            return false;
        }
        int count = lisParm.getCount();
        TParm risParm = medApply.getRisParm(caseNo);
        if (risParm == null || risParm.getErrCode() != 0) {
            this.messageBox_("取得检查结果失败");
            return false;
        }
        ETable table = (ETable) word.findObject("LIS_TABLE", EComponent.TABLE_TYPE);
        if (table != null) {
            // 删除Table原有数据
            removeTableData(table);
            // 相同的ORDERDESC 只显示一行
            String orderDescOld = "";
            orderDescOld = lisParm.getValue("ORDER_DESC", 0);
            String orderDesc = "";
            for (int i = 0; i < count; i++) {
                ETR tr = table.appendTR();
                // System.out.println("tr"+tr);
                // System.out.println("tr"+tr.size());
                // 医嘱名称
                ETD td = tr.get(0);
                orderDesc = lisParm.getValue("ORDER_DESC", i);
                if (i == 0) {
                    td.setString(orderDesc);
                } else {
                    if (!orderDesc.equals(orderDescOld)) {
                        orderDescOld = orderDesc;
                        td.setString(orderDesc);
                    }
                }
                // 检验项目
                td = tr.get(1);
                td.setString(lisParm.getValue("TESTITEM_CHN_DESC", i));
                // 检验值
                td = tr.get(2);
                String testValue = lisParm.getValue("TEST_VALUE", i);
                String uppeLimit = lisParm.getValue("UPPE_LIMIT", i);
                String lowerLimit = lisParm.getValue("LOWER_LIMIT", i);
                if (isNumber(testValue) && isNumber(uppeLimit) && isNumber(lowerLimit)) {
                    Double test = Double.parseDouble(testValue);
                    Double uppe = Double.parseDouble(uppeLimit);
                    Double lower = Double.parseDouble(lowerLimit);
                    if (test > uppe) {
                        td.setString(lisParm.getValue("TEST_VALUE", i) + "↑");
                        ((EText) td.get(0).get(0)).modifyColor(new Color(255, 0, 0));
                    }
                    if (test < lower) {
                        td.setString(lisParm.getValue("TEST_VALUE", i) + "↓");
                        ((EText) td.get(0).get(0)).modifyColor(new Color(255, 0, 0));
                    }
                    if (test < uppe && test > lower) {
                        td.setString(lisParm.getValue("TEST_VALUE", i));
                    }
                } else {
                    td.setString(lisParm.getValue("TEST_VALUE", i));
                }
                // 检验单位
                td = tr.get(3);
                td.setString(lisParm.getValue("TEST_UNIT", i));
                // 上限值
                td = tr.get(4);
                // System.out.println("td"+td);
                // System.out.println("LIS"+lisParm.getValue("UPPE_LIMIT",i));
                td.setString(lisParm.getValue("UPPE_LIMIT", i));
                // 下限值
                td = tr.get(5);
                td.setString(lisParm.getValue("LOWER_LIMIT", i));
            }
            table.setLockEdit(true);
            table.update();
        }
        table = (ETable) word.findObject("RIS_TABLE", EComponent.TABLE_TYPE);
        if (table != null) {
            count = risParm.getCount();
            // 删除Table原有数据
            removeTableData(table);
            for (int i = 0; i < count; i++) {
                ETR tr = table.appendTR();
                // 医嘱名称
                ETD td = tr.get(0);
                td.setString(risParm.getValue("ORDER_DESC", i));
                // 检验项目
                td = tr.get(1);
                String outcomeType = risParm.getValue("OUTCOME_TYPE", i);
                if ("H".equalsIgnoreCase(outcomeType)) {
                    td.setString("阴性");
                } else {
                    td.setString("阳性");
                }
                // 检验值
                td = tr.get(2);
                td.setString(risParm.getValue("OUTCOME_DESCRIBE", i));
                // 检验单位
                td = tr.get(3);
                td.setString(risParm.getValue("OUTCOME_CONCLUSION", i));
            }
            table.setLockEdit(true);
            table.update();
        }
        word.update();
        return true;
    }

    /**
     * 是否是数字
     * 
     * @return boolean
     */
    public boolean isNumber(String str) {
        if (str == null || str.length() == 0) {
            return false;
        }
        Pattern pattern = Pattern.compile("[0-9]*(\\.?)[0-9]*");
        return pattern.matcher(str).matches();
    }

    /**
     * 右键事件
     */
    public void showPopMenu() {
        TParm action = orderTable.getParmValue().getRow(orderTable.getSelectedRow());
        if (("LIS".equals(action.getValue("CAT1_TYPE")) && "Y".equals(action.getValue("DONE")))
         || ("RIS".equals(action.getValue("CAT1_TYPE")) && "Y".equals(action.getValue("DONE")))) {
            orderTable.setPopupMenuSyntax("查看报告,showRept");
            return;
        } else {
            orderTable.setPopupMenuSyntax("");
            return;
        }
    }

    /**
     * 查看报告
     */
    public void showRept() {
        TParm action = orderTable.getParmValue().getRow(orderTable.getSelectedRow());
        // LIS报告
        if ("LIS".equals(action.getValue("CAT1_TYPE"))) {
            String labNo = action.getValue("MED_APPLY_NO");
            if (labNo.length() == 0) {
                this.messageBox("E0188");
                return;
            }
            SystemTool.getInstance().OpenLisWeb(action.getValue("MR_NO"), null, "", "", "", "");
        }
        // RIS报告
        if ("RIS".equals(action.getValue("CAT1_TYPE"))) {
            SystemTool.getInstance().OpenRisWeb(action.getValue("MR_NO"));
        }
    }

    /**
     * 根据给入的地址，循环插入总检报告
     * @param add
     */
    private void insertWord(TParm add) {
        if (add == null) {
            this.messageBox_("取得文件失败");
            return;
        }
        int count = add.getCount("FILE_PATH");
        if (count <= 0) {
            this.messageBox_("没有新文件需要带入！");
            return;
        }
        // this.messageBox("-count--"+count);
        for (int i = 0; i < count; i++) {
            word.onInsertFileFrontFixed("INSERT0", add.getValue("FILE_PATH", i), add.getValue("FILE_NAME", i), 3, false);
            word.update();
        }
        if (add.getValue("REMARK", "FILE").length() != 0) {
            word.setFileRemark(add.getValue("REMARK", "FILE"));
        }
    }
    
	/**
	 * 根据CASE_NO取得其他结构化病例，并取得其地址
	 * @param caseNo
	 * @return
	 */
    private TParm getEmrAddress(String caseNo) {
        TParm result = new TParm();
        // ============xueyf modify 20120224 start
        // String root = TConfig.getSystemValue("FileServer.Main.Root") + "\\" + TConfig.getSystemValue("EmrData") + "\\JHW\\";
        String root = "\\JHW\\";
        // ============xueyf modify 20120224 stop
        String sql =
                "SELECT A.FILE_PATH, A.FILE_NAME, B.DEPT_ATTRIBUTE "
                        + " FROM EMR_FILE_INDEX A, HRM_ORDER B, HRM_DEPT_ATTRIBUTE F "
                        + "	WHERE A.CASE_NO = '" + caseNo + "' AND A.CASE_NO = B.CASE_NO "
                        + "   AND A.FILE_SEQ = B.FILE_NO " + " AND B.SETMAIN_FLG = 'Y' "
                        + "  AND B.DEPT_ATTRIBUTE = F.DEPT_ATTRIBUTE(+) "
                        + "   AND B.DEPT_ATTRIBUTE <> '04' " + " ORDER BY F.SEQ";
        result = new TParm(TJDODBTool.getInstance().select(sql));
        // System.out.println("getEmrAddress.sql=="+sql);
        if (result.getErrCode() != 0) {
            // this.messageBox_(result.getErrText());
            this.messageBox("没有问诊资料");
            return null;
        }
        int count = result.getCount();
        if (count <= 0) {
            this.messageBox("没有问诊资料");
            return null;
        }
        StringBuffer deptAttribute = new StringBuffer();
        for (int i = 0; i < count; i++) {
            String wholePath = root + result.getValue("FILE_PATH", i);
            result.setData("FILE_PATH", i, wholePath);
            result.setData("FILE_NAME", i, result.getValue("FILE_NAME", i) + ".jhw");
            deptAttribute.append(result.getValue("DEPT_ATTRIBUTE", i));
            deptAttribute.append("|");
        }
        if (deptAttribute.toString().length() != 0) result.setData("REMARK", "FILE", deptAttribute.toString());
        else result.setData("REMARK", "FILE", "");
        insertWord(result);
        return result;
    }
	
	/**
	 * 部分抓取合并功能
	 * @param caseNo
	 * @param deptAttribute
	 * @param orders
	 * @return
	 */
    private TParm onMerge(String caseNo, String deptAttribute, List<TParm> orders) {
        TParm result = new TParm();
        // 构造医嘱的名子
        String strInSQL = "";
        // caseNo_code + file_no
        for (TParm parm : orders) {
            strInSQL += "'" + parm.getValue("FILE_NO") + "',";
        }
        if (strInSQL != null && strInSQL.length() > 0) {
            strInSQL = strInSQL.substring(0, strInSQL.length() - 1);
        }
        // 合并选中的医嘱文件
        // String root = TConfig.getSystemValue("FileServer.Main.Root") + "\\" + TConfig.getSystemValue("EmrData") + "\\JHW\\";
        String root = "\\JHW\\";
        String sql =
                "SELECT A.FILE_PATH, A.FILE_NAME, B.DEPT_ATTRIBUTE "
                        + " FROM EMR_FILE_INDEX A, HRM_ORDER B, HRM_DEPT_ATTRIBUTE F "
                        + "	WHERE A.CASE_NO = '" + caseNo + "' AND A.CASE_NO = B.CASE_NO "
                        + " AND A.FILE_SEQ = B.FILE_NO " + " AND B.SETMAIN_FLG = 'Y' "
                        + " AND B.DEPT_ATTRIBUTE = F.DEPT_ATTRIBUTE(+) "
                        + " AND B.DEPT_ATTRIBUTE <> '" + deptAttribute + "' AND FILE_NO IN ("
                        + strInSQL + ") ORDER BY F.SEQ";
        // System.out.println("===========onMerge SQL============"+sql);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() != 0) {
            // this.messageBox_(result.getErrText());
            this.messageBox("没有问诊资料！");
            return null;
        }
        int count = result.getCount();
        if (count <= 0) {
            this.messageBox("没有问诊资料！");
            return null;
        }
        String[] remark = this.word.getFileRemark().split("\\|");
        TParm fileList = new TParm();
        StringBuffer deptAttributes = new StringBuffer();
        for (int i = 0; i < count; i++) {
            String deptAttributeTemp = result.getValue("DEPT_ATTRIBUTE", i);
            // 已经存在的是否还加入
            // if (isDeptAttribute(remark, deptAttributeTemp)) {
            //      continue;
            // }
            String wholePath = root + result.getValue("FILE_PATH", i);
            fileList.addData("FILE_PATH", wholePath);
            fileList.addData("FILE_NAME", result.getValue("FILE_NAME", i) + ".jhw");
            deptAttributes.append(this.word.getFileRemark() + "|" + deptAttributeTemp);
            deptAttributes.append("|");
        }
        if (deptAttributes.toString().length() != 0) fileList.setData("REMARK", "FILE", deptAttributes.toString());
        else fileList.setData("REMARK", "FILE", "");
        insertWord(fileList);
        return result;
    }

    /**
     * 根据CASE_NO取得其他结构化病例，并取得其地址
     * 
     * @param caseNo
     * @return
     */
    private TParm getEmrAddress(String caseNo, String deptAttribute) {
        TParm result = new TParm();
        // ============xueyf modify 20120224 start
        // String root = TConfig.getSystemValue("FileServer.Main.Root") + "\\" + TConfig.getSystemValue("EmrData") + "\\JHW\\";
        String root = "\\JHW\\";
        // ============xueyf modify 20120224 stop
        String sql =
                "SELECT A.FILE_PATH, A.FILE_NAME, B.DEPT_ATTRIBUTE "
                        + " FROM EMR_FILE_INDEX A, HRM_ORDER B, HRM_DEPT_ATTRIBUTE F "
                        + "	WHERE A.CASE_NO = '" + caseNo + "' AND A.CASE_NO = B.CASE_NO "
                        + " AND A.FILE_SEQ = B.FILE_NO " + " AND B.SETMAIN_FLG = 'Y' "
                        + " AND B.DEPT_ATTRIBUTE = F.DEPT_ATTRIBUTE(+) "
                        + " AND B.DEPT_ATTRIBUTE <> '" + deptAttribute + "' ORDER BY F.SEQ";
        result = new TParm(TJDODBTool.getInstance().select(sql));
        // System.out.println("getEmrAddress.sql=="+sql);
        if (result.getErrCode() != 0) {
            // this.messageBox_(result.getErrText());
            this.messageBox("没有问诊资料！");
            return null;
        }
        int count = result.getCount();
        if (count <= 0) {
            this.messageBox("没有问诊资料！");
            return null;
        }
        String[] remark = this.word.getFileRemark().split("\\|");
        TParm fileList = new TParm();
        StringBuffer deptAttributes = new StringBuffer();
        for (int i = 0; i < count; i++) {
            String deptAttributeTemp = result.getValue("DEPT_ATTRIBUTE", i);
            if (isDeptAttribute(remark, deptAttributeTemp)) {
                continue;
            }
            String wholePath = root + result.getValue("FILE_PATH", i);
            fileList.addData("FILE_PATH", wholePath);
            fileList.addData("FILE_NAME", result.getValue("FILE_NAME", i) + ".jhw");
            deptAttributes.append(this.word.getFileRemark() + "|" + deptAttributeTemp);
            deptAttributes.append("|");
        }
        if (deptAttributes.toString().length() != 0) fileList.setData("REMARK", "FILE", deptAttributes.toString());
        else fileList.setData("REMARK", "FILE", "");
        insertWord(fileList);
        return result;
    }

    /**
     * 检核是否存在
     * 
     * @param deptAtts String[]
     * @param deptAtt String
     * @return boolean
     */
    public boolean isDeptAttribute(String[] deptAtts, String deptAtt) {
        boolean falg = false;
        // System.out.println("deptAtt=="+deptAtt);
        for (String temp : deptAtts) {
            // System.out.println("temp=="+temp);
            if (temp.equals(deptAtt)) {
                falg = true;
                break;
            }
        }
        return falg;
    }

    /**
     * ORDER_TABLE点击事件
     */
    public void onOrder() {
        int row = orderTable.getSelectedRow();
        if (row < 0) {
            return;
        }
        TParm parm = orderTable.getParmValue();
        if (parm.getCount() <= 0) {
            return;
        }
        int columnIndex = orderTable.getSelectedColumn();
        // this.messageBox("---columnIndex---"+columnIndex);
        if (columnIndex != 1) {
            openFile(parm.getRow(row));
        } else {
            String cat = parm.getValue("ORDER_CAT1_CODE", row);
            if (cat != null && !cat.equals("ORD")) {
                this.messageBox("非问诊医嘱不能选择！");
                return;
            }
        }
    }

    /**
     * 根据MR_NO查询数据
     */
    public void onMrNo() {
        String mrNo = this.getValueString("MR_NO");
        if (StringUtil.isNullString(mrNo)) {
            return;
        }
        mrNo = StringTool.fill0(mrNo, PatTool.getInstance().getMrNoLength()); // ==== chenxi
        this.setValue("MR_NO", mrNo);
        // ========================= caowl 20130326 start
        String startDate = "";
        String endDate = "";
        String isUndone = "";
        order = new HRMOrder();
        fileNo = "";
        //TParm patParm = order.getFinalCheckPat(startDate, endDate, isUndone);
        TParm patParm1 = order.getFinalCheckPat(mrNo,startDate, endDate, "1");
        int count1 = patParm1.getCount() <= 0 ? 0 : patParm1.getCount();
        TParm patParm2 = order.getFinalCheckPat(mrNo,startDate, endDate, "2");
        int count2 = patParm2.getCount() <= 0 ? 0 : patParm2.getCount();
        TParm patParm3 = order.getFinalCheckPat(mrNo,startDate, endDate, "");
        int count3 = patParm3.getCount() <= 0 ? 0 : patParm3.getCount();
        if (patParm1.getErrCode() != 0 || patParm2.getErrCode() != 0 || patParm3.getErrCode() != 0) {
            this.messageBox_("查询失败");
            this.setValue("UNDONE_NUM", count1 + "人");
            this.setValue("DONE_NUM", count2 + "人");
            this.setValue("ALL_NUM", count3 + "人");
            patTable.removeRowAll();
            return;
        }
        if ((patParm1 == null || patParm1.getCount() <= 0)
                && (patParm2 == null || patParm2.getCount() <= 0)
                && (patParm3 == null || patParm3.getCount() <= 0)) {
            this.messageBox("无数据！");
            this.setValue("UNDONE_NUM", count1 + "人");
            this.setValue("DONE_NUM", count2 + "人");
            this.setValue("ALL_NUM", count3 + "人");
            patTable.removeRowAll();
            return;
        }
        this.setValue("UNDONE_NUM", count1 + "人");
        this.setValue("DONE_NUM", count2 + "人");
        this.setValue("ALL_NUM", count3 + "人");
        if (this.getValueBoolean("UNDONE")) {
            if (patParm1.getCount() <= 0 && patParm2.getCount() > 0) {
                patTable.setParmValue(patParm2);
                this.setValue("DONE", "Y");
            } else {
                patTable.setParmValue(patParm1);
            }
        } else if (this.getValueBoolean("DONE")) {
            if (patParm2.getCount() <= 0 && patParm1.getCount() > 0) {
                patTable.setParmValue(patParm1);
                this.setValue("UNDONE", "Y");
            } else {
                patTable.setParmValue(patParm2);
            }
        } else {
            patTable.setParmValue(patParm3);
        }
        patTable.setSelectedRow(0);
        onPatChoose();
    }

    /**
     * 保存
     */
    public void onSave() {
        TParm parm = orderTable.getParmValue();
        int row = orderTable.getSelectedRow();
        if (orderTable.getRowCount() <= 0) {
            return;
        }
        if (row < 0) {
            row = orderTable.getRowCount() - 1;
        }
        if (parm == null) {
            return;
        }
        int count = parm.getCount();
        if (count <= 0) {
            return;
        }
        if (!isSaveble(parm)) {
            if (this.messageBox("提示信息", "该患者没有完成所有检查，是否写入总检报告", this.YES_NO_OPTION) != 0) {
                return;
            }
        }
        // 保存结构化病历
        if (!this.saveEmr(parm.getValue("CASE_NO", row), parm.getValue("MR_NO", row))) {
            this.messageBox_("保存病历失败");
            return;
        }
        if (!this.saveData(parm.getValue("CASE_NO", row), fileNo, !StringUtil.isNullString(fileNo))) {
            this.messageBox_("保存数据失败");
            return;
        }
    }

    /**
     * ORDER_TABLE中如有未完成的检查，则返回false,否则返回true
     * @param parm
     * @return
     */
    private boolean isSaveble(TParm parm) {
        int count = parm.getCount();
        if (count <= 0) {
            return false;
        }
        for (int i = 0; i < count; i++) {
            boolean isDone = TypeTool.getBoolean(parm.getData("DONE", i));
            if (!isDone) {
                if (!"04".equalsIgnoreCase(parm.getValue("DEPT_ATTRIBUTE", i))) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 结构化病例保存方法
     * 
     * @param caseNo
     * @param mrNo
     * @return
     */
    private boolean saveEmr(String caseNo, String mrNo) {
        if (StringUtil.isNullString(caseNo) || StringUtil.isNullString(mrNo)) {
            return false;
        }
        if (!isOld) {
            pathParm.setData("CASE_NO", caseNo);
            pathParm.setData("MR_NO", mrNo);
            TParm parm = EmrUtil.getInstance().getFileServerEmrName(pathParm);
            parm.setData("OPT_USER", Operator.getID());
            parm.setData("OPT_DATE", TJDODBTool.getInstance().getDBTime());
            // ============xueyf modify 20120222 start
            parm.setData("CREATOR_DATE", TJDODBTool.getInstance().getDBTime());
            parm.setData("REPORT_FLG", "N");
            // ============xueyf modify 20120222 stop
            parm.setData("OPT_TERM", Operator.getIP());
            parm.setData("CREATOR_USER", Operator.getID());
            parm.setData("CURRENT_USER", Operator.getID());
            fileNo = parm.getValue("FILE_SEQ");
            TParm result =
                    TIOM_AppServer.executeAction("action.odi.ODIAction", "saveNewEmrFile", parm);
            if (result.getErrCode() != 0) {
                // System.out.println("errText="+result.getErrText());
                this.messageBox("E0001");
                return false;
            }
            // ============xueyf modify 20120223 start
            String filePath =
                    parm.getValue("FILE_PATH").indexOf("JHW") < 0 ? "JHW\\" + parm.getValue("FILE_PATH") : parm.getValue("FILE_PATH");
            word.onSaveAs(filePath, parm.getValue("FILE_NAME"), 3);
            // ============xueyf modify 20120223 stop
        } else {
            pathParm.setData("CASE_NO", caseNo);
            pathParm.setData("MR_NO", mrNo);
            TParm parm = EmrUtil.getInstance().getFileServerEmrName(pathParm);
            parm.setData("OPT_USER", Operator.getID());
            parm.setData("OPT_DATE", TJDODBTool.getInstance().getDBTime());
            parm.setData("OPT_TERM", Operator.getIP());
            parm.setData("CREATOR_USER", Operator.getID());
            parm.setData("CURRENT_USER", Operator.getID());
            // System.out.println("parm====="+parm);
            TParm result = TIOM_AppServer.executeAction("action.odi.ODIAction", "updateEmrFile", parm);
            if (result.getErrCode() != 0) {
                // System.out.println("errText="+result.getErrText());
                this.messageBox("E0002");// 新增失败
                return false;
            }
            word.onSave();
        }
        return true;
    }
	
    /**
     * 保存HRM_ORDER，HRM_PATADM的数据
     * @return
     */
    private boolean saveData(String caseNo, String fileNo, boolean isFirst) {
        TParm orderParm = orderTable.getParmValue();
        int row = orderTable.getSelectedRow();
        if (orderParm == null) {
            return false;
        }
        if (orderTable.getRowCount() <= 0) {
            return false;
        }
        if (row < 0) {
            row = orderTable.getRowCount() - 1;
        }
        if (orderParm.getCount() <= 0) {
            return false;
        }
        order = new HRMOrder();
        order.onQuery();
        order.setFilter("CASE_NO='" + orderParm.getValue("CASE_NO", row) 
                + "' AND ORDERSET_CODE='" + orderParm.getValue("ORDERSET_CODE", row) 
                + "' AND ORDERSET_GROUP_NO=" + orderParm.getInt("ORDERSET_GROUP_NO", row));
        order.filter();
        // System.out.println("after order filter");
        // order.showDebug();
        TParm result =
                order.saveByCheck(Operator.getRegion(), Operator.getDept(), caseNo, "04", fileNo,
                                  isFirst);
        if (result.getErrCode() != 0) {
            // this.messageBox_(result.getErrText());
            return false;
        }
        order.resetModify();
        Timestamp now = TJDODBTool.getInstance().getDBTime();
        HRMPatAdm patAdm = new HRMPatAdm();
        patAdm.onQueryByCaseNo(caseNo);
        patAdm.setItem(0, "FINAL_JUDGE_DR", Operator.getID());
        patAdm.setItem(0, "FINAL_JUDGE_DATE", now);
        // patAdm.setItem(0, "REPORT_DATE", now);
        patAdm.setItem(0, "END_DATE", now);
        patAdm.setItem(0, "OPT_USER", Operator.getID());
        patAdm.setItem(0, "OPT_DATE", now);
        patAdm.setItem(0, "OPT_TERM", Operator.getIP());
        if (!patAdm.update()) {
            this.messageBox("E0001");// 保存失败
            return false;
        }
        patAdm.resetModify();
        return true;
    }

    /**
     * 打印结构化病历
     */
    public void onPrint() {
        TParm parm = orderTable.getParmValue();
        if (parm == null) {
            return;
        }
        if (parm.getCount() <= 0) {
            return;
        }
        word.onPreviewWord();
        // word.printXDDialog();
        word.printDialog();
        // word.print();
    }

    /**
     * 总检展开
     */
    public void onUnfold() {
        TMovePane mp = (TMovePane) callFunction("UI|MOVE|getThis");
        mp.onDoubleClicked();
        TMovePane mp1 = (TMovePane) callFunction("UI|MP2|getThis");
        mp1.onDoubleClicked();
    }

    /**
     * 临床数据
     */
    public void onInsertLCSJ() {
        int row = patTable.getSelectedRow();
        TParm parm = patTable.getParmValue();
        if (parm == null) {
            return;
        }
        int count = parm.getCount();
        if (count <= 0) {
            return;
        }
        String caseNo = parm.getValue("CASE_NO", row);
        if (StringUtil.isNullString(caseNo)) {
            this.messageBox_("取得医嘱数据失败");
            return;
        }
        TParm inParm = new TParm();
        inParm.setData("CASE_NO", caseNo);
        inParm.addListener("onReturnContent", this, "onReturnContent");
        // this.openWindow("%ROOT%\\config\\emr\\EMRMEDDataUI.x",inParm);
        TWindow window =
                (TWindow) this.openWindow("%ROOT%\\config\\emr\\EMRMEDDataUI.x", inParm, true);
        window.setX(ImageTool.getScreenWidth() - window.getWidth());
        window.setY(0);
        window.setVisible(true);
    }

    /**
     * 片语记录
     * 
     * @param value String
     */
    public void onReturnContent(String value) {
        if (!this.word.pasteString(value)) {
            this.messageBox("E0005");// 执行失败
        }
    }

    /**
     * 片语
     */
    public void onInsertPY() {
        TParm inParm = new TParm();
        inParm.setData("TYPE", "2");
        inParm.setData("ROLE", "1");
        inParm.setData("DR_CODE", Operator.getID());
        inParm.setData("DEPT_CODE", Operator.getDept());
        inParm.addListener("onReturnContent", this, "onReturnContent");
        // this.openWindow("%ROOT%\\config\\emr\\EMRComPhraseQuote.x",inParm);
        TWindow window =
                (TWindow) this.openWindow("%ROOT%\\config\\emr\\EMRComPhraseQuote.x", inParm, true);
        window.setX(ImageTool.getScreenWidth() - window.getWidth());
        window.setY(0);
        window.setVisible(true);
    }

    /**
     * 清空系统剪贴板
     */
    public void onClearMenu() {
        CopyOperator.clearComList();
    }

    /**
     * 删除表格
     */
    public void onDelTable() {
        if (this.word.getFileOpenName().length() != 0) {
            int yesNo = this.messageBox("提示信息", "确认删除本焦点下的表格？", this.YES_NO_OPTION);
            if (yesNo == 0) {
                this.word.deleteTable();
            }
        } else {
            this.messageBox("请选择总检报告！");
        }
    }

    public static void main(String[] args) {
        String FINAL_CHEfCK_PAT =
                "SELECT DISTINCT A.*,B.PAT_NAME,B.SEX_CODE,B.REPORT_DATE,A.DEPT_CODE,B.BIRTHDAY,B.REPORT_STATUS"
                        + "     FROM HRM_ORDER A,HRM_PATADM B" + "  WHERE A.EXEC_DEPT_CODE='#'"
                        + "       AND A.DEPT_ATTRIBUTE='04'" + "          AND A.CASE_NO=B.CASE_NO"
                        + "       AND B.REPORT_DATE >=TO_DATE('#','YYYYMMDDHH24MISS')"
                        + "       AND B.REPORT_DATE<TO_DATE('#','YYYYMMDDHH24MISS')"
                        + "          #" + "       AND A.SETMAIN_FLG='Y'" + "    ORDER BY A.SEQ_NO";
        String str = "1111.2235";
        // System.out.println(FINAL_CHEfCK_PAT);
        Pattern pattern = Pattern.compile("[0-9]*(\\.?)[0-9]*");
        // System.out.println(""+pattern.matcher(str).matches());
        String str1 = "08|05";
        String[] s = str1.split("\\|");
        for (String temp : s) {
            // System.out.println("temp=="+temp);
        }
        // System.out.println("=="+s.length);
        TWord word = new TWord();
        word.onOpen("10\01\000000000311", "100123000005_门诊家族史_02.jhw", 3, false);
        word.setFileRemark("05|01|06");
        word.onSave();
        // System.out.println(""+word.getFileRemark());
    }

    // ====================排序功能begin======================add by wanglong 20130515
    /**
     * 加入表格排序监听方法
     * @param table
     */
    public void addSortListener(final TTable table) {
        table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent mouseevent) {
                int i = table.getTable().columnAtPoint(mouseevent.getPoint());
                int j = table.getTable().convertColumnIndexToModel(i);
                if (j == sortColumn) {
                    ascending = !ascending;// 点击相同列，翻转排序
                } else {
                    ascending = true;
                    sortColumn = j;
                }
                TParm tableData = table.getParmValue();// 取得表单中的数据
                String columnName[] = tableData.getNames("Data");// 获得列名
                String strNames = "";
                for (String tmp : columnName) {
                    strNames += tmp + ";";
                }
                strNames = strNames.substring(0, strNames.length() - 1);
                Vector vct = getVector(tableData, "Data", strNames, 0);
                String tblColumnName = table.getParmMap(sortColumn); // 表格排序的列名;
                int col = tranParmColIndex(columnName, tblColumnName); // 列名转成parm中的列索引
                compare.setDes(ascending);
                compare.setCol(col);
                java.util.Collections.sort(vct, compare);
                // 将排序后的vector转成parm;
                cloneVectoryParam(vct, new TParm(), strNames, table);
            }
        });
    }

    /**
     * 根据列名数据，将TParm转为Vector
     * @param parm
     * @param group
     * @param names
     * @param size
     * @return
     */
    private Vector getVector(TParm parm, String group, String names, int size) {
        Vector data = new Vector();
        String nameArray[] = StringTool.parseLine(names, ";");
        if (nameArray.length == 0) {
            return data;
        }
        int count = parm.getCount(group, nameArray[0]);
        if (size > 0 && count > size)
            count = size;
        for (int i = 0; i < count; i++) {
            Vector row = new Vector();
            for (int j = 0; j < nameArray.length; j++) {
                row.add(parm.getData(group, nameArray[j], i));
            }
            data.add(row);
        }
        return data;
    }

    /**
     * 返回指定列在列名数组中的index
     * @param columnName
     * @param tblColumnName
     * @return int
     */
    private int tranParmColIndex(String columnName[], String tblColumnName) {
        int index = 0;
        for (String tmp : columnName) {
            if (tmp.equalsIgnoreCase(tblColumnName)) {
                return index;
            }
            index++;
        }
        return index;
    }

    /**
     * 根据列名数据，将Vector转成Parm
     * @param vectorTable
     * @param parmTable
     * @param columnNames
     * @param table
     */
    private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
            String columnNames, final TTable table) {
        String nameArray[] = StringTool.parseLine(columnNames, ";");
        for (Object row : vectorTable) {
            int rowsCount = ((Vector) row).size();
            for (int i = 0; i < rowsCount; i++) {
                Object data = ((Vector) row).get(i);
                parmTable.addData(nameArray[i], data);
            }
        }
        parmTable.setCount(vectorTable.size());
        table.setParmValue(parmTable);
    }
    // ====================排序功能end======================
}
