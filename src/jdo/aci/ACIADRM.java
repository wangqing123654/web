package jdo.aci;

import jdo.sys.Operator;
import jdo.sys.SystemTool;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.javahis.util.StringUtil;
/**
 * <p>Title: 药品不良事件信息 </p>
 *
 * <p>Description: 药品不良事件信息 </p>
 *
 * <p>Copyright: 2013 </p>
 *
 * <p>Company:BlueCore </p>
 *
 * @author wanglong 2013.09.30
 * @version 1.0
 */
public class ACIADRM extends TDataStore {

 
    /**
     * 查询事件
     * 
     * @return
     */
    public int onQuery() {
        this.setSQL("SELECT * FROM ACI_ADRM ORDER BY ACI_NO"); // 初始化SQL
        return 0;
    }

    /**
     * 根据ACI_NO初始化对象
     * @param aciNo
     * @return
     */
    public int onQueryByACINo(String aciNo) {
        if (StringUtil.isNullString(aciNo)) {
            return -1;
        }
        this.setSQL("SELECT * FROM ACI_ADRM  WHERE ACI_NO='#'".replaceFirst("#", aciNo));
        int result = this.retrieve();
        if (result < 0) {
            return result;
        }
        return 0;
    }

    /**
     * 获得ACI_NO对应数据的TParm
     * @param aciNo
     * @return
     */
    public TParm getDataParm() {
        if (this.rowCount() < 1) {
            return null;
        }
        TParm result = new TParm();
        TParm parm = this.getBuffer(this.PRIMARY);
        for (int i = 0; i < parm.getCount(); i++) {
            if (parm.getBoolean("#ACTIVE#", i)) {
                result.addRowData(parm, i);
            }
        }
        return result;
    }
    
    /**
     * 初始化
     * @param parm
     */
    public void initData(int row, TParm parm) {
        this.setActive(row, false);
        String aciNo = SystemTool.getInstance().getNo("ALL", "ACI", "RECORD_NO", "ACI_NO"); // 调用取号原则
        this.setItem(row, "ACI_NO", aciNo);
        this.setItem(row, "FIRST_FLG", parm.getData("FIRST_FLG"));
        this.setItem(row, "REPORT_TYPE", parm.getData("REPORT_TYPE"));
        this.setItem(row, "ADM_TYPE", parm.getData("ADM_TYPE"));
        this.setItem(row, "CASE_NO", parm.getData("CASE_NO"));
        this.setItem(row, "MR_NO", parm.getData("MR_NO"));
        this.setItem(row, "PAT_NAME", parm.getData("PAT_NAME"));
        this.setItem(row, "SEX_CODE", parm.getData("SEX_CODE"));
        this.setItem(row, "SPECIES_CODE", parm.getData("SPECIES_CODE"));
        this.setItem(row, "BIRTH_DATE", parm.getTimestamp("BIRTH_DATE"));
        this.setItem(row, "WEIGHT", parm.getData("WEIGHT"));
        this.setItem(row, "TEL", parm.getData("TEL"));
        this.setItem(row, "DIAG_CODE1", parm.getData("DIAG_CODE1"));
        this.setItem(row, "DIAG_CODE2", parm.getData("DIAG_CODE2"));
        this.setItem(row, "DIAG_CODE3", parm.getData("DIAG_CODE3"));
        this.setItem(row, "DIAG_CODE4", parm.getData("DIAG_CODE4"));
        this.setItem(row, "DIAG_CODE5", parm.getData("DIAG_CODE5"));
        this.setItem(row, "PERSON_HISTORY", parm.getData("PERSON_HISTORY"));
        this.setItem(row, "PERSON_REMARK", parm.getData("PERSON_REMARK"));
        this.setItem(row, "FAMILY_HISTORY", parm.getData("FAMILY_HISTORY"));
        this.setItem(row, "FAMILY_REMARK", parm.getData("FAMILY_REMARK"));
        this.setItem(row, "SMOKE_FLG", parm.getData("SMOKE_FLG"));
        this.setItem(row, "DRINK_FLG", parm.getData("DRINK_FLG"));
        this.setItem(row, "PREGNANT_FLG", parm.getData("PREGNANT_FLG"));
        this.setItem(row, "HEPATOPATHY_FLG", parm.getData("HEPATOPATHY_FLG"));
        this.setItem(row, "NEPHROPATHY_FLG", parm.getData("NEPHROPATHY_FLG"));
        this.setItem(row, "ALLERGY_FLG", parm.getData("ALLERGY_FLG"));
        this.setItem(row, "ALLERGY_REMARK", parm.getData("ALLERGY_REMARK"));
        this.setItem(row, "OTHER_FLG", parm.getData("OTHER_FLG"));
        this.setItem(row, "OTHER_REMARK", parm.getData("OTHER_REMARK"));
        this.setItem(row, "ADR_ID1", parm.getData("ADR_ID1"));
        this.setItem(row, "ADR_DESC1", parm.getData("ADR_DESC1"));
        this.setItem(row, "ADR_ID2", parm.getData("ADR_ID2"));
        this.setItem(row, "ADR_ID3", parm.getData("ADR_ID3"));
        this.setItem(row, "ADR_ID4", parm.getData("ADR_ID4"));
        this.setItem(row, "EVENT_DATE", parm.getTimestamp("EVENT_DATE"));
        this.setItem(row, "EVENT_DESC", parm.getData("EVENT_DESC"));
        this.setItem(row, "EVENT_RESULT", parm.getData("EVENT_RESULT"));
        this.setItem(row, "RESULT_REMARK", parm.getData("RESULT_REMARK"));
        this.setItem(row, "DIED_DATE", parm.getTimestamp("DIED_DATE"));
        this.setItem(row, "STOP_REDUCE", parm.getData("STOP_REDUCE"));
        this.setItem(row, "AGAIN_APPEAR", parm.getData("AGAIN_APPEAR"));
        this.setItem(row, "DISE_DISTURB", parm.getData("DISE_DISTURB"));
        this.setItem(row, "USER_REVIEW", parm.getData("USER_REVIEW"));
        this.setItem(row, "UNIT_REVIEW", parm.getData("UNIT_REVIEW"));
        this.setItem(row, "REPORT_USER", parm.getData("REPORT_USER"));
        this.setItem(row, "REPORT_OCC", parm.getData("REPORT_OCC"));
        this.setItem(row, "OCC_REMARK", parm.getData("OCC_REMARK"));
        this.setItem(row, "REPORT_TEL", parm.getData("REPORT_TEL"));
        this.setItem(row, "REPORT_EMAIL", parm.getData("REPORT_EMAIL"));
        this.setItem(row, "REPORT_DEPT", parm.getData("REPORT_DEPT"));
        this.setItem(row, "REPORT_STATION", parm.getData("REPORT_STATION"));
        this.setItem(row, "REPORT_UNIT", parm.getData("REPORT_UNIT"));
        this.setItem(row, "UNIT_CONTACTS", parm.getData("UNIT_CONTACTS"));
        this.setItem(row, "UNIT_TEL", parm.getData("UNIT_TEL"));
        this.setItem(row, "REPORT_DATE", parm.getTimestamp("REPORT_DATE"));
        this.setItem(row, "REMARK", parm.getData("REMARK"));
        this.setItem(row, "UPLOAD_FLG", parm.getData("UPLOAD_FLG"));
        this.setItem(row, "UPLOAD_DATE", parm.getTimestamp("UPLOAD_DATE"));
        this.setItem(row, "REGION_CODE", parm.getData("REGION_CODE"));
        this.setItem(row, "OPT_USER", Operator.getID());
        this.setItem(row, "OPT_DATE", this.getDBTime());
        this.setItem(row, "OPT_TERM", Operator.getIP());
        this.setActive(row, true);
    }
    
    /**
     * 更新数据
     * @param parm
     */
    public void updateData(int row, TParm parm) {
        this.setItem(row, "ACI_NO", parm.getData("ACI_NO"));
        this.setItem(row, "FIRST_FLG", parm.getData("FIRST_FLG"));
        this.setItem(row, "REPORT_TYPE", parm.getData("REPORT_TYPE"));
        this.setItem(row, "ADM_TYPE", parm.getData("ADM_TYPE"));
        this.setItem(row, "CASE_NO", parm.getData("CASE_NO"));
        this.setItem(row, "MR_NO", parm.getData("MR_NO"));
        this.setItem(row, "PAT_NAME", parm.getData("PAT_NAME"));
        this.setItem(row, "SEX_CODE", parm.getData("SEX_CODE"));
        this.setItem(row, "SPECIES_CODE", parm.getData("SPECIES_CODE"));
        this.setItem(row, "BIRTH_DATE", parm.getTimestamp("BIRTH_DATE"));
        this.setItem(row, "WEIGHT", parm.getData("WEIGHT"));
        this.setItem(row, "TEL", parm.getData("TEL"));
        this.setItem(row, "DIAG_CODE1", parm.getData("DIAG_CODE1"));
        this.setItem(row, "DIAG_CODE2", parm.getData("DIAG_CODE2"));
        this.setItem(row, "DIAG_CODE3", parm.getData("DIAG_CODE3"));
        this.setItem(row, "DIAG_CODE4", parm.getData("DIAG_CODE4"));
        this.setItem(row, "DIAG_CODE5", parm.getData("DIAG_CODE5"));
        this.setItem(row, "PERSON_HISTORY", parm.getData("PERSON_HISTORY"));
        this.setItem(row, "PERSON_REMARK", parm.getData("PERSON_REMARK"));
        this.setItem(row, "FAMILY_HISTORY", parm.getData("FAMILY_HISTORY"));
        this.setItem(row, "FAMILY_REMARK", parm.getData("FAMILY_REMARK"));
        this.setItem(row, "SMOKE_FLG", parm.getData("SMOKE_FLG"));
        this.setItem(row, "DRINK_FLG", parm.getData("DRINK_FLG"));
        this.setItem(row, "PREGNANT_FLG", parm.getData("PREGNANT_FLG"));
        this.setItem(row, "HEPATOPATHY_FLG", parm.getData("HEPATOPATHY_FLG"));
        this.setItem(row, "NEPHROPATHY_FLG", parm.getData("NEPHROPATHY_FLG"));
        this.setItem(row, "ALLERGY_FLG", parm.getData("ALLERGY_FLG"));
        this.setItem(row, "ALLERGY_REMARK", parm.getData("ALLERGY_REMARK"));
        this.setItem(row, "OTHER_FLG", parm.getData("OTHER_FLG"));
        this.setItem(row, "OTHER_REMARK", parm.getData("OTHER_REMARK"));
        this.setItem(row, "ADR_ID1", parm.getData("ADR_ID1"));
        this.setItem(row, "ADR_DESC1", parm.getData("ADR_DESC1"));
        this.setItem(row, "ADR_ID2", parm.getData("ADR_ID2"));
        this.setItem(row, "ADR_ID3", parm.getData("ADR_ID3"));
        this.setItem(row, "ADR_ID4", parm.getData("ADR_ID4"));
        this.setItem(row, "EVENT_DATE", parm.getTimestamp("EVENT_DATE"));
        this.setItem(row, "EVENT_DESC", parm.getData("EVENT_DESC"));
        this.setItem(row, "EVENT_RESULT", parm.getData("EVENT_RESULT"));
        this.setItem(row, "RESULT_REMARK", parm.getData("RESULT_REMARK"));
        this.setItem(row, "DIED_DATE", parm.getTimestamp("DIED_DATE"));
        this.setItem(row, "STOP_REDUCE", parm.getData("STOP_REDUCE"));
        this.setItem(row, "AGAIN_APPEAR", parm.getData("AGAIN_APPEAR"));
        this.setItem(row, "DISE_DISTURB", parm.getData("DISE_DISTURB"));
        this.setItem(row, "USER_REVIEW", parm.getData("USER_REVIEW"));
        this.setItem(row, "UNIT_REVIEW", parm.getData("UNIT_REVIEW"));
        this.setItem(row, "REPORT_USER", parm.getData("REPORT_USER"));
        this.setItem(row, "REPORT_OCC", parm.getData("REPORT_OCC"));
        this.setItem(row, "OCC_REMARK", parm.getData("OCC_REMARK"));
        this.setItem(row, "REPORT_TEL", parm.getData("REPORT_TEL"));
        this.setItem(row, "REPORT_EMAIL", parm.getData("REPORT_EMAIL"));
        this.setItem(row, "REPORT_DEPT", parm.getData("REPORT_DEPT"));
        this.setItem(row, "REPORT_STATION", parm.getData("REPORT_STATION"));
        this.setItem(row, "REPORT_UNIT", parm.getData("REPORT_UNIT"));
        this.setItem(row, "UNIT_CONTACTS", parm.getData("UNIT_CONTACTS"));
        this.setItem(row, "UNIT_TEL", parm.getData("UNIT_TEL"));
        this.setItem(row, "REPORT_DATE", parm.getTimestamp("REPORT_DATE"));
        this.setItem(row, "REMARK", parm.getData("REMARK"));
        this.setItem(row, "UPLOAD_FLG", parm.getData("UPLOAD_FLG"));
        this.setItem(row, "UPLOAD_DATE", parm.getTimestamp("UPLOAD_DATE"));
        this.setItem(row, "REGION_CODE", parm.getData("REGION_CODE"));
        this.setItem(row, "OPT_USER", Operator.getID());
        this.setItem(row, "OPT_DATE", this.getDBTime());
        this.setItem(row, "OPT_TERM", Operator.getIP());
    }
}
