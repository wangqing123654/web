package jdo.hrm;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;

/**
 * 
 * <p> Title: ��������ͬ������ </p>
 * 
 * <p> Description: ��������ͬ������ </p>
 * 
 * <p> Copyright: Copyright (c) 2008 </p>
 * 
 * <p> Company: javahis </p>
 * 
 * @author ehui 20090922
 * @version 1.0
 */
public class HRMContractD extends TDataStore {
	// ��ʼ��SQL
	private static final String INIT = "SELECT * FROM HRM_CONTRACTD ORDER BY SEQ_NO,CONTRACT_CODE,MR_NO";
	// ��ʼ��SQL
	private static final String INITQ = "SELECT * FROM HRM_CONTRACTD WHERE COMPANY_CODE='#' AND CONTRACT_CODE='#' AND MR_NO='#' ORDER BY CONTRACT_CODE,MR_NO";
	// ���±���ע��
	private static final String UPDATE_COVER_FLG = "SELECT * FROM HRM_CONTRACTD WHERE COMPANY_CODE='#' AND CONTRACT_CODE='#' AND MR_NO='#'";
	// �����������ͺ�ͬ����õ�����COMBO����
	private static final String GET_MR_BY_CONTRACTD = // modify by wanglong 20121217
	"SELECT A.MR_NO ID,A.PAT_NAME NAME,A.PY1 PY1  " 
	        + " FROM HRM_CONTRACTD A"
			+ " WHERE CONTRACT_CODE='#' "
			+ "AND COMPANY_CODE='#' "
			+ " ORDER BY A.MR_NO ";
	// ����������롢��ͬ�����ѯ������Ϣ
    private static final String GET_REPORT_INFO = // modify by wanglong 20130206
    "SELECT 'N' AS CHOOSE,A.* ,A.BIRTHDAY AS BIRTH_DATE,FLOOR(MONTHS_BETWEEN(SYSDATE,A.BIRTHDAY)/12) AS AGE,C.PACKAGE_DESC,'#' DEPT_CODE "
            + "  FROM HRM_CONTRACTD A,HRM_PACKAGEM C"
            + " WHERE A.COMPANY_CODE='#' "
			+ "   AND A.CONTRACT_CODE='#' "
			+ "   AND A.COVER_FLG='#' "
			+ "   AND A.PACKAGE_CODE=C.PACKAGE_CODE "
			+ "	ORDER BY A.SEQ_NO,A.STAFF_NO";
	// ����������롢��ͬ�����ѯ�ѱ�����Ϣ
    private static final String GET_REPORTED_INFO =// modify by wanglong 20130408
    "SELECT 'N' AS CHOOSE,A.* ,A.BIRTHDAY AS BIRTH_DATE,FLOOR(MONTHS_BETWEEN(SYSDATE,A.BIRTHDAY)/12) AS AGE,C.PACKAGE_DESC,D.DEPT_CODE,D.CASE_NO,D.REPORTLIST,D.INTRO_USER  "
			+"	 FROM HRM_CONTRACTD A,HRM_PACKAGEM C,HRM_PATADM D "
			+ "	WHERE A.COMPANY_CODE='#' "
			+ "   AND A.CONTRACT_CODE='#' "
			+ "   AND A.COVER_FLG='#' "
			+ "	  AND A.PACKAGE_CODE=C.PACKAGE_CODE	"
			+ "   AND A.COMPANY_CODE=D.COMPANY_CODE"
			+ "   AND A.CONTRACT_CODE=D.CONTRACT_CODE"
			+ "   AND A.MR_NO=D.MR_NO " 
			+ "	ORDER BY A.SEQ_NO,A.STAFF_NO";
	// ���ݸ�����������ѯ������Ϣ
	// ============xueyf modify 20120228 start
	private static final String GET_REPORT_INFO_BY_PAT_NAME =// modify by wanglong 20121217
	"SELECT 'N' AS CHOOSE,A.*,A.BIRTHDAY AS BIRTH_DATE,FLOOR(MONTHS_BETWEEN(SYSDATE,A.BIRTHDAY)/12) AS AGE,C.PACKAGE_DESC,'#' DEPT_CODE "
			+ "  FROM HRM_CONTRACTD A,HRM_PACKAGEM C "
            + " WHERE A.PACKAGE_CODE=C.PACKAGE_CODE "
            + "   AND A.COVER_FLG='#' "
            + "   AND A.PAT_NAME='#' "
            + "ORDER BY A.SEQ_NO,A.STAFF_NO";
	// ============xueyf modify 20120228 stop
	// ============add by wanglong 20121217 begin===============
	// ����������롢��ͬ���롢�ײʹ��롢�Ա����õ�����COMBO����
	private static final String GET_PATLIST_BY_CONTRACTD_AND_PACKAGE_OR_SEX = 
		"SELECT A.MR_NO ID,A.PAT_NAME NAME,A.PY1 PY1  "
			+ "  FROM HRM_CONTRACTD A "
			+ " WHERE A.CONTRACT_CODE='#' "
			+ "  AND A.COMPANY_CODE='#' " 
			+ "  # # " 
			+ "ORDER BY A.MR_NO";
	// ============add by wanglong 20121217 end====================
	// ���ݸ���MR_NO�õ������������룬��ͬ����
	private static final String GET_WHERE_BY_MRNO = "SELECT A.COMPANY_CODE,A.CONTRACT_CODE,B.CASE_NO "
			+ "  FROM HRM_CONTRACTD A,HRM_PATADM B "
			+ " WHERE A.MR_NO='#' "
			+ "   AND A.MR_NO=B.MR_NO " + "ORDER BY B.CASE_NO DESC";
	// �Ƿ�����֧��
	private static final String IS_COMPANY_PAY = "SELECT COUNT(MR_NO) MR_NOS "
			+ "  FROM HRM_CONTRACTD " + " WHERE COMPANY_CODE='#' "
			+ "   AND CONTRACT_CODE='#' " + "   AND MR_NO='#' "
			+ "   AND COMPANY_PAY_FLG='Y'";
	private String id = Operator.getID();
	private String ip = Operator.getIP();
	private String contractDesc;
	public String packageCode;
	private TDataStore pat;

	/**
	 * ��ѯ�¼�
	 * @return
	 */
	public int onQuery() {
		this.setSQL(INIT);
		if (pat == null) {
			pat = new TDataStore();
			pat.setSQL("SELECT * FROM SYS_PATINFO WHERE MR_NO='#' ORDER BY MR_NO");
			pat.retrieve();
		}
		packageCode = this.getItemString(0, "PACKAGE_CODE");
		return this.retrieve();
	}

	/**
	 * ��ѯ�¼�
	 * @return
	 */
	public int onQuery(String company, String contract, String mrNo) {
		this.setSQL(INITQ.replaceFirst("#", company)
				.replaceFirst("#", contract).replaceFirst("#", mrNo));
		if (pat == null) {
			pat = new TDataStore();
			pat.setSQL("SELECT * FROM SYS_PATINFO WHERE MR_NO='#' ORDER BY MR_NO");
			pat.retrieve();
		}
		packageCode = this.getItemString(0, "PACKAGE_CODE");
		return this.retrieve();
	}

	/**
	 * ����MR_NO��ʼ��
	 * @param mrNo
	 * @return
	 */
	public boolean onQuery(String mrNo) {
		if (StringUtil.isNullString(mrNo)) {
			return false;
		}
		this.setSQL("SELECT * FROM HRM_CONTRACTD WHERE MR_NO='" + mrNo
				+ "' ORDER BY CONTRACT_CODE DESC");
		this.retrieve();
		if (pat == null) {
			pat = new TDataStore();
			pat.setSQL("SELECT * FROM SYS_PATINFO WHERE MR_NO='" + mrNo + "'");
			pat.retrieve();
		}
		packageCode = this.getItemString(0, "PACKAGE_CODE");
		return true;
	}

	/**
	 * ����
	 * @param companyCode String
	 * @return boolean
	 */
	public boolean filt(String contractCode) {
		if (StringUtil.isNullString(contractCode)) {
			return false;
		}
		this.setFilter("CONTRACT_CODE='" + contractCode + "'");
		return this.filter();
	}

	/**
	 * ���ݸ�����������ͺ�ͬ�������
	 * @param contractCode
	 * @param companyCode
	 * @return
	 */
	public boolean filt(String contractCode, String companyCode) {
		if (StringUtil.isNullString(contractCode)
				|| StringUtil.isNullString(companyCode)) {
			return false;
		}
		this.setFilter("CONTRACT_CODE='" + contractCode
				+ "' AND COMPANY_CODE='" + companyCode + "'");
		return this.filter();
	}

	/**
	 * ����һ������
	 * @param comCode
	 * @param contractCode
	 * @return
	 */
	public int insertRow(String comCode, String contractCode,
			String contractDesc, Double discnt, int rowCount) {
		int row = this.insertRow();
		this.setItem(row, "COMPANY_CODE", comCode);
		this.setItem(row, "CONTRACT_CODE", contractCode);
		this.setItem(row, "CONTRACT_DESC", contractDesc);
		this.setItem(row, "PRE_CHK_DATE", this.getDBTime());
		this.setItem(row, "PACKAGE_CODE", packageCode);
		// this.setItem(row, "MR_NO", "Y");
		this.setItem(row, "OPT_USER", id);
		this.setItem(row, "OPT_TERM", ip);
		this.setItem(row, "DISCNT", discnt);
		this.setItem(row, "OPT_DATE", TJDODBTool.getInstance().getDBTime());
		this.setItem(rowCount, "SEQ_NO", rowCount);
		this.setActive(row, false);
		return row;
	}

	/**
	 * @param parm
	 * @return
	 */
    public boolean insertRow(TParm parm, String comCode, String contractCode, String contractDesc) {
        if (StringUtil.isNullString(comCode) || StringUtil.isNullString(contractCode)
                || StringUtil.isNullString(contractDesc)) {
            return false;
        }
        if (parm == null || parm.getCount() < 1) {
            // System.out.println("count<1");
            return false;
        }
        int count = parm.getCount();
        for (int i = 0; i < count; i++) {
            int row = this.rowCount() - 1;
            // System.out.println("i="+parm.getRow(i));
            TParm mrParm =
                    new TParm(TJDODBTool.getInstance().select(
                                                              "SELECT MR_NO FROM SYS_PATINFO WHERE IDNO='"
                                                                      + parm.getValue("IDNO", i)
                                                                      + "' ORDER BY OPT_DATE"));
            if (mrParm.getCount("MR_NO") <= 0) {
                this.setItem(row, "MR_NO", "Y");
            }
            if (mrParm.getCount("MR_NO") == 1) {
                this.setItem(row, "MR_NO", mrParm.getData("MR_NO", 0));
            }
            if (mrParm.getCount("MR_NO") > 1) {
                this.setItem(row, "MR_NO", mrParm.getData("MR_NO", 0));
                // this.setItem(row, "MR_NO","N");
            }
            if (parm.getValue("IDNO", i).length() == 0 || parm.getValue("IDNO", i) == null) {
                this.setItem(row, "MR_NO", "Y");
            }
            this.setItem(row, "IDNO", parm.getValue("IDNO", i));
            this.setItem(row, "STAFF_NO", parm.getValue("STAFF_NO", i));
            Timestamp preChkDate =
                    StringTool.getTimestamp(parm.getValue("PRE_CHK_DATE", i), "yyyyMMdd");
            this.setItem(row, "PRE_CHK_DATE", preChkDate);
            this.setItem(row, "PAT_NAME", parm.getValue("PAT_NAME", i));
            String py = SystemTool.getInstance().charToCode(parm.getValue("PAT_NAME", i));
            this.setItem(row, "PY1", py);
            this.setActive(row, true);
            this.setItem(row, "COMPANY_CODE", comCode);
            this.setItem(row, "CONTRACT_CODE", contractCode);
            this.setItem(row, "CONTRACT_DESC", contractDesc);
            this.setItem(row, "PRE_CHK_DATE", this.getDBTime());
            this.setItem(row, "PACKAGE_CODE", parm.getValue("PACKAGE_CODE", i));
            this.setItem(row, "FOREIGNER_FLG", parm.getValue("FOREIGNER_FLG", i));
            this.setItem(row, "OPT_USER", id);
            this.setItem(row, "OPT_TERM", ip);
            this.setItem(row, "OPT_DATE", TJDODBTool.getInstance().getDBTime());
            this.setItem(row, "SEX_CODE", parm.getValue("SEX_CODE", i));
            this.setItem(row, "BIRTHDAY", parm.getData("BIRTHDAY", i));
            // ============xueyf modify 20120305 start
            this.setItem(row, "COMPANY_PAY_FLG", parm.getValue("COMPANY_PAY_FLG", i));
            this.setItem(row, "DISCNT", parm.getDouble("DISCNT", i));// modify by wanglong 20130116
            this.setItem(row, "TEL", parm.getValue("TEL", i));
            this.setItem(row, "SEQ_NO", parm.getInt("SEQ_NO", i));
            // ============xueyf modify 20120305 stop
            this.setItem(row, "MARRIAGE_CODE", parm.getValue("MARRIAGE_CODE", i));// ����״̬ add-by-wanglong-20130116
            this.setItem(row, "PAT_DEPT", parm.getValue("PAT_DEPT", i));// ���� add-by-wanglong-20130225
            row = this.insertRow();
            this.setActive(row, false);
        }
        int row = this.rowCount() - 1;
        this.setItem(row, "COMPANY_CODE", comCode);
        this.setItem(row, "CONTRACT_CODE", contractCode);
        this.setItem(row, "CONTRACT_DESC", contractDesc);
        this.setItem(row, "PRE_CHK_DATE", this.getDBTime());
        this.setItem(row, "PACKAGE_CODE", packageCode);
        this.setItem(row, "SEQ_NO", count + 1);
        this.setItem(row, "OPT_USER", id);
        this.setItem(row, "OPT_TERM", ip);
        this.setItem(row, "OPT_DATE", TJDODBTool.getInstance().getDBTime());
        this.setActive(row, false);
        return true;
    }

	/**
	 * ȡ�ò���pat�����SQL���
	 * 
	 * @return
	 */
	public String[] getSql() {
		String[] sql;
        List updateMarriageSql = new ArrayList();// add by wanglong 20130222
		String filterString = this.getFilter();
		this.setFilter("");
		this.filter();
		int[] insertRows = this.getNewRows();
		for (int insertRow : insertRows) {
			// System.out.println("insertRow="+insertRow);
			// System.out.println("rowData="+this.getRowParm(insertRow));
			String newMrNo = "";
			if ("Y".equals(this.getItemString(insertRow, "MR_NO"))) {
				int row = pat.insertRow();
				newMrNo = SystemTool.getInstance().getMrNo();
				pat.setItem(row, "MR_NO", newMrNo);
				pat.setItem(row, "IDNO", this.getItemString(insertRow, "IDNO"));
				pat.setItem(row, "PAT_NAME", this.getItemString(insertRow,
						"PAT_NAME"));
				pat.setItem(row, "TEL", this.getItemString(insertRow, "TEL"));
				pat.setItem(row, "POST_CODE", this.getItemString(insertRow,
						"POST_CODE"));
				pat.setItem(row, "ADDRESS", this.getItemString(insertRow,
						"ADDRESS"));
				// System.out.println("sexCode======="+this.getItemString(insertRow,
				// "SEX_CODE"));
				pat.setItem(row, "SEX_CODE", this.getItemString(insertRow,
						"SEX_CODE"));
				pat.setItem(row, "FOREIGNER_FLG", this.getItemString(insertRow,
						"FOREIGNER_FLG"));
				// System.out.println("birthdate======"+this.getItemData(insertRow,
				// "BIRTHDAY"));
				pat.setItem(row, "BIRTH_DATE", this.getItemData(insertRow,
						"BIRTHDAY"));
				pat.setItem(row, "OPT_USER", id);
				pat.setItem(row, "OPT_TERM", ip);
				pat.setItem(row, "OPT_DATE", this.getDBTime());
				pat.setItem(row, "MARRIAGE_CODE", this.getItemString(insertRow, "MARRIAGE_CODE"));//add by wanglong 20130116
				pat.setItem(row, "PAT_DEPT", this.getItemString(insertRow, "PAT_DEPT"));//add by wanglong 20130225
			} else {
				// System.out.println("������:"+this.getItemString(insertRow,
				// "MR_NO"));
				newMrNo = this.getItemString(insertRow, "MR_NO");
                String marriageCode=this.getItemString(insertRow, "MARRIAGE_CODE");//add by wanglong 20130222
                updateMarriageSql.add("UPDATE SYS_PATINFO SET MARRIAGE_CODE = '" + marriageCode
                        + "' WHERE MR_NO = '" + newMrNo + "'");//add by wanglong 20130222 ���»���״̬
			}
			this.setItem(insertRow, "MR_NO", newMrNo);
		}
		// System.out.println("getSql");
//		pat.showDebug();
		this.setFilter(filterString);
		this.filter();
		sql = pat.getUpdateSQL();
        sql = StringTool.copyArray(sql, (String[]) updateMarriageSql.toArray(new String[0]));//add by wanglong 20130222
		return sql;
	}

	/**
	 * ���ú�ͬ����
	 * @param desc
	 */
	public void setContractDesc(String desc) {
		contractDesc = desc;
	}

	/**
	 * �õ�����������
	 * @param parm TParm
	 * @param row int
	 * @param column String
	 * @return Object
	 */
	public Object getOtherColumnValue(TParm parm, int row, String column) {
		if ("CONTRACT_DESC".equalsIgnoreCase(column)) {
			return contractDesc;
		}
		return "";
	}

	/**
	 * ���ݸ�������ɾ��һ�ź�ͬ��Ա����Ϣ
	 * @param companyCode
	 * @param contractCode
	 * @return
	 */
	public boolean deleteContract(String contractCode) {
		if (StringUtil.isNullString(contractCode)) {
			return false;
		}
		if (!this.filt(contractCode)) {
			return false;
		}
		int count = this.rowCount();
		for (int i = count - 1; i > -1; i--) {
			this.deleteRow(i);
		}
		return true;
	}

	/**
	 * ���ݸ�����������ɾ����ͬϸ��
	 * @param companyCode
	 * @return
	 */
	public boolean deleteContractByCompanyCode(String companyCode) {
		this.setFilter("COMPANY_CODE='" + companyCode + "'");
		this.filter();
		int count = this.rowCount();
		for (int i = count - 1; i > -1; i--) {
			this.deleteRow(i);
		}
		return true;
	}

	/**
	 * ȡ��pat����
	 * @return
	 */
	public TDataStore getPat() {
		return pat;
	}

	private String filterCompanyCode;
	private String filterContractCode;
	private String filterMrno;

	/**
	 * ���ݸ��������������º�ͬϸ����ע�Ǻͱ���ʱ��
	 * 
	 * @param companyCode
	 * @param contractCode
	 * @param mrNo
	 */
	public void updateCoverFlg(String companyCode, String contractCode,
			String mrNo, String tel) {
		if (StringUtil.isNullString(companyCode)
				|| StringUtil.isNullString(contractCode)
				|| StringUtil.isNullString(mrNo)) {
			return;
		}
		this.setFilter("COMPANY_CODE='" + companyCode + "' AND CONTRACT_CODE='"
				+ contractCode + "' AND MR_NO='" + mrNo + "'");
		// this.filter();
		filterCompanyCode = companyCode;
		filterContractCode = contractCode;
		filterMrno = mrNo;
		filterObject(this, "filter1");
		// // System.out.println("updateCoverFlg");
		// this.showDebug();
		this.setItem(0, "COVER_FLG", "Y");
		this.setItem(0, "REAL_CHK_DATE", this.getDBTime());
		// System.out.println("after set");
//		this.showDebug();
	}

	/**
	 * ���˷���
	 * @param parm TParm
	 * @param row int
	 * @return boolean
	 */
	public boolean filter1(TParm parm, int row) {
		return filterCompanyCode.equals(parm.getValue("COMPANY_CODE", row))
				&& filterContractCode.equals(parm
						.getValue("CONTRACT_CODE", row))
				&& filterMrno.equals(parm.getValue("MR_NO", row));
	}

	/**
	 * �����������ͺ�ͬ����õ�����COMBO����
	 * @param companyCode
	 * @param contractCode
	 * @return
	 */
	public static TParm getPatCombo(String companyCode, String contractCode) {
		TParm result = new TParm();
		if (StringUtil.isNullString(companyCode)
				|| StringUtil.isNullString(contractCode)) {
			return result;
		}
		String sql = GET_MR_BY_CONTRACTD.replaceFirst("#", contractCode)
				.replaceFirst("#", companyCode);
		// System.out.println("getPatCombo.sql="+sql);
		result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}

	/**
	 * ����������롢��ͬ���롢�ײʹ���õ�����COMBO����
	 * @param companyCode
	 * @param contractCode
	 * @return
	 */
	public static TParm getPatComboByPackageAndSex(String companyCode,
			String contractCode, String packageCode, String sexCode) {// add by wanglong 20121217
		TParm result = new TParm();
		if (StringUtil.isNullString(companyCode)
				|| StringUtil.isNullString(contractCode)) {
			return result;
		}
		String sql = GET_PATLIST_BY_CONTRACTD_AND_PACKAGE_OR_SEX.replaceFirst(
				"#", contractCode).replaceFirst("#", companyCode);
		if (!StringUtil.isNullString(packageCode)) {
			sql = sql.replaceFirst("#", "  AND A.PACKAGE_CODE='" + packageCode
					+ "' ");
		}
		if (!StringUtil.isNullString(sexCode)) {
			sql = sql.replaceFirst("#", "  AND A.SEX_CODE='" + sexCode + "' ");
		}
		sql = sql.replaceFirst("#", "");
		sql = sql.replaceFirst("#", "");
		result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}

	/**
	 * ���ݸ�����������ѯ��ͬ��Ϣ
	 * @param companyCode
	 * @return
	 */
	public TParm onQueryByCompany(String companyCode) {
		TParm result = new TParm();
		if (StringUtil.isNullString(companyCode)) {
			return result;
		}
		String sql = "SELECT CONTRACT_CODE ID,CONTRACT_DESC NAME,PY1 PY1 FROM HRM_CONTRACTM WHERE COMPANY_CODE='"
				+ companyCode + "' ORDER BY OPT_DATE DESC";
		result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}
	
    /**
     * ���ݸ�����������ѯ��ͬ��Ϣ
     * @param companyCode
     * @return
     */
    public TParm onQueryByCompanyWithBlank(String companyCode) {//add by wanglong 20130510
        TParm result = new TParm();
        if (StringUtil.isNullString(companyCode)) {
            return result;
        }
        String sql =
                "SELECT CONTRACT_CODE ID,CONTRACT_DESC NAME,PY1 FROM HRM_CONTRACTM WHERE COMPANY_CODE = '"
                        + companyCode + "' UNION SELECT '' ID,'' NAME,'' PY1 FROM DUAL ORDER BY ID NULLS FIRST";
        result = new TParm(TJDODBTool.getInstance().select(sql));
        return result;
    }
    
	/**
	 * ���ݸ�����������ͺ�ͬ����ȡ��δ�����ĺ�ͬϸ����Ϣ
	 * @param companyCode
	 * @param contractCode
	 * @param isReport
	 * @return
	 */
	public TParm getUnReportParm(String companyCode, String contractCode,
			String isReport) {
		TParm result = new TParm();
		if (StringUtil.isNullString(companyCode)
				|| StringUtil.isNullString(contractCode)) {
			return result;
		}
		String sql = "";
		if ("Y".equalsIgnoreCase(isReport)) {
			sql = this.GET_REPORTED_INFO.replaceFirst("#", companyCode)
					.replaceFirst("#", contractCode)
					.replaceFirst("#", isReport);
		} else {
			sql = this.GET_REPORT_INFO.replaceFirst("#", Operator.getDept())
					.replaceFirst("#", companyCode).replaceFirst("#",
							contractCode).replaceFirst("#", isReport);
		}
		result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() != 0) {
			return result;
		}
		// int count=result.getCount();
		// for(int i=0;i<count;i++){
		// result.setData("CHOOSE",i,"N");
		// }
		return result;
	}

	/**
	 * ���ݸ�����������ѯ����
	 * @param companyCode
	 * @param contractCode
	 * @param isReport
	 * @param patName
	 * @return
	 */
    public TParm getUnReportParmByPatName(String isReport, String patName) {
        TParm result = new TParm();
        if (StringUtil.isNullString(isReport) || StringUtil.isNullString(patName)) {
            return result;
        }
        String sql =
                this.GET_REPORT_INFO_BY_PAT_NAME.replaceFirst("#", Operator.getDept())
                                                .replaceFirst("#", isReport)
                                                .replaceFirst("#", patName);
		result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() != 0) {
			return result;
		}
		int count = result.getCount();
		for (int i = 0; i < count; i++) {
			result.setData("CHOOSE", i, "N");
			result.setData("DEPT_CODE", i, Operator.getDept());
		}
		return result;
	}

	/**
	 * ���ݸ���MR_NO�鵽COMPANY_CODE,CONTRACT_CODE,CASE_NO
	 * @param mrNo
	 * @return
	 */
	public TParm getComInfoByMrNO(String mrNo) {
		TParm result = new TParm();
		if (StringUtil.isNullString(mrNo)) {
			return null;
		}
		String sql = this.GET_WHERE_BY_MRNO.replaceFirst("#", mrNo);
		result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}

	/**
	 * ȡ�ú�ͬ���
	 * @return
	 */
	public double getContractAmt() {
		double amt = 0.0;
		int count = this.rowCount();
		for (int i = 0; i < count; i++) {
			if (!this.isActive(i))
				continue;
			amt += HRMPackageD.getPackageAmt(this.getItemString(i,
					"PACKAGE_CODE"));
		}
		return amt;
	}

	/**
	 * ���ݴ����������룬��ͬ���룬MR_NO���ж�һ��Ա���Ƿ�Ϊ��������
	 * @param companyCode
	 * @param contractCode
	 * @param mrNo
	 * @return int -1:����;0:����;1:������
	 */
	public int isPatCompanyPay(String companyCode, String contractCode,
			String mrNo) {
		if (StringUtil.isNullString(companyCode)
				|| StringUtil.isNullString(contractCode)
				|| StringUtil.isNullString(mrNo)) {
			return -1;
		}
		String sql = IS_COMPANY_PAY.replaceFirst("#", companyCode)
				.replaceFirst("#", contractCode).replaceFirst("#", mrNo);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (parm.getErrCode() != 0 || parm.getCount() <= 0) {
			// System.out.println("isPatCompanyPay.err is:"+parm.getErrText());
			return -1;
		}
		int count = parm.getInt("MR_NOS", 0);
		if (count == 1) {
			return 0;
		} else {
			return 1;
		}
	}

	/**
	 * ����HRM_CONTRACTD��ͬ��ϸ�и���Ϊ�ѱ���
	 * @param companyCode
	 * @param contractCode
	 * @param mrNo
	 * @return
	 */
	public TParm updateCoverFlg(String companyCode, String contractCode,
			String mrNo, Timestamp now, String tel) {
		if (StringUtil.isNullString(companyCode)
				|| StringUtil.isNullString(contractCode)
				|| StringUtil.isNullString(mrNo)) {
			return null;
		}
		String sql = "UPDATE HRM_CONTRACTD";
		sql += " SET COVER_FLG='Y',";
		sql += " REAL_CHK_DATE=TO_DATE('"
				+ StringTool.getString(now, "yyyyMMddHHmmss")
				+ "','yyyyMMddHH24miss'),";
		sql += " TEL='" + tel + "' ";
		sql += " WHERE COMPANY_CODE='" + companyCode + "'";
		sql += " AND CONTRACT_CODE='" + contractCode + "'";
		sql += " AND MR_NO='" + mrNo + "'";
		TParm result = new TParm(TJDODBTool.getInstance().update(sql));

		return result;
	}
	
	/**
	 * ���µ绰��
	 * @param companyCode
	 * @param contractCode
	 * @param mrNo
	 * @param tel
	 * @return
	 */
	public TParm updateTel(String companyCode, String contractCode,
			String mrNo, String tel) {
		String sql = "UPDATE HRM_CONTRACTD";
		sql += " SET TEL='" + tel + "' ";
		sql += " WHERE COMPANY_CODE='" + companyCode + "'";
		sql += " AND CONTRACT_CODE='" + contractCode + "'";
		sql += " AND MR_NO='" + mrNo + "'";
		TParm result = new TParm(TJDODBTool.getInstance().update(sql));
		return result;
	}
	
}
