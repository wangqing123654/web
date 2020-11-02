package jdo.util;

import java.util.Map;

import jdo.ind.INDTool;
import jdo.pha.PhaTransUnitTool;
import jdo.sys.SYSAntibioticTool;
import jdo.sys.SYSCtrlDrugClassTool;
import jdo.sys.SYSDiagnosisTool;
import jdo.sys.SYSDiseaseTpyeTool;
import jdo.sys.SYSOperationicdTool;
import jdo.sys.SYSPhaFreqTool;
import jdo.sys.SYSPhaRouteTool;
import jdo.sys.SYSSQL;
import jdo.sys.SYS_ORDER_CAT1_Tool;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TStrike;

/**
 *
 * <p>
 * Title: ҩƷ����
 * </p>
 *
 * <p>
 * Description:
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 *
 * <p>
 * Company: JavaHis
 * </p>
 *
 * @author lzk 2009.6.2
 * @version JavaHis 1.0
 */
public class Medicine{

	/**
	 * ��Ⱦ���ϱ�ʱ��
	 *
	 * @param diseasetpye_code
	 *            ��Ⱦ��������
	 * @return
	 */
	public double getSYSDiseaseDeadLine(String diseasetpye_code) {

		return SYSDiseaseTpyeTool.getInstance()
				.getDiseaseTpye(diseasetpye_code).getDouble("DEADLINE", 0);
	}

	/**
	 * �õ��������Ϣ
	 *
	 * @param icd_type
	 *            ������
	 * @param icd_code
	 *            ��ϴ���
	 * @return Map TParm{ICD_CODE,ICD_TYPE,ICD_CHN_DESC,ICD_ENG_DESC,PY1,
	 *         PY2,SEQ,DESCRIPTION,SYNDROME_FLG,MDC_CODE,
	 *         CCMD_CODE,LIMIT_DEPT_CODE,MAIN_DIAG_FLG,LIMIT_SEX_CODE,STANDARD_STAY_DAYS,
	 *         CHRONIC_FLG,CHLR_FLG,DISEASETYPE_CODE,START_AGE,END_AGE,
	 *         AVERAGE_FEE,CAT_FLG,OPT_USER,OPT_DATE,OPT_TERM}
	 */
	public Map getSYSDiagnosis(String icd_type, String icd_code) {
		return SYSDiagnosisTool.getInstance().selectdata(icd_type, icd_code)
				.getData();
	}

	/**
	 * �õ�����ICD��Ϣ
	 *
	 * @param operation_icd
	 *            ��������ICD
	 * @return Map TParm{OPERATION_ICD,OPT_CHN_DESC,OPT_ENG_DESC,PY1,PY2,
	 *         SEQ,DESCRIPTION,AVG_IPD_DAY,AVG_OP_FEE,OPT_USER,
	 *         OPT_TERM,OPT_DATE}
	 */
	public Map getSYSOperationIcd(String operation_icd) {
		return SYSOperationicdTool.getInstance().selectdata(operation_icd)
				.getData();
	}

	/**
	 * �õ�ҽ��������Ϣ
	 *
	 * @param order_cat1_code
	 *            ҽ��ϸ����
	 * @return Map TParm{SEQ,ORDER_CAT1_CODE,ORDER_CAT1_DESC,PY1,PY2, CAT1_TYPE
	 *         ,DEAL_SYSTEM ,DESCRIPTION,OPT_USER ,OPT_DATE}
	 */
	public Map getSYSOderCat1(String order_cat1_code) {
		return SYS_ORDER_CAT1_Tool.getInstance().selectdata(order_cat1_code)
				.getData();
	}

	/**
	 * ȡ����ҩƵ����Ϣ
	 *
	 * @param freq_code
	 *            Ƶ�δ���
	 * @return Map TParm{SEQ,FREQ_CODE,FREQ_CHN_DESC,PY1,PY2,
	 *         FREQ_ENG_DESC,DESCRIPTION,TAKE_DAYS,DOUBEXD_FLG,NOCOMPUTE_FLG,
	 *         STAT_FLG,WESMED_FLG,CHIMED_FLG,CYCLE,FREQ_TIMES,
	 *         MON_FLG,TUE_FLG,WED_FLG,THUR_FLG,FRI_FLG,
	 *         STA_FLG,SUN_FLG,FREQ_UNIT_48,OPT_USER,OPT_DATE}
	 */
	public Map getSYSPhaFreq(String freq_code) {
		return SYSPhaFreqTool.getInstance().selectdata(freq_code).getData();
	}

	/**
	 * ȡ�ù���ҩƷ�ȼ���Ϣ
	 *
	 * @param ctrldrugclass_code
	 *            ����ҩƷ�ȼ�����
	 * @return Map
	 *         TParm{SEQ,CTRLDRUGCLASS_CODE,CTRLDRUGCLASS_CHN_DESC,CTRLDRUGCLASS_ENG_DESC,TAKE_DAYS,
	 *         DESCRIPTION,PRN_TYPE_CODE,PRN_TYPE_DESC,PRNSPCFORM_FLG,CTRL_FLG,
	 *         NARCOTIC_FLG,TOXICANT_FLG,PSYCHOPHA_FLG,RADIA_FLG,TEST_DRUG_FLG,
	 *         ANTISEPTIC_FLG,ANTIBIOTIC_FLG,TPN_FLG,OPT_USER,OPT_DATE,
	 *         OPT_TERM,PY1,PY2}
	 */
	public Map getSYSCtrldrugClass(String ctrldrugclass_code) {
		return SYSCtrlDrugClassTool.getInstance()
				.selectdata(ctrldrugclass_code).getData();
	}

	/**
	 * ȡ�ÿ����صȼ���Ϣ
	 *
	 * @param antibiotic_code
	 *            �����صȼ�����
	 * @param antibiotic_desc
	 *            ����������
	 * @return Map TParm{ANTIBIOTIC_CODE, ANTIBIOTIC_DESC, PY1, PY2,
	 *         DESCRIPTION, KE_DAYS, OPT_USER, MR_CODE, OPT_DATE, OPT_TERM}
	 */
	public Map getSYSAntibiotic(String antibiotic_code, String antibiotic_desc) {
		return SYSAntibioticTool.getInstance().selectdata(antibiotic_code,
				antibiotic_desc).getData();
	}

	/**
	 * ȡ���÷��ֵ���Ϣ
	 *
	 * @param route_code
	 *            �÷�����
	 * @return Map TParm{SEQ, ROUTE_CODE,ROUTE_CHN_DESC,PY1,PY2,
	 *         WESMED_FLG,CHIMED_FLG,ROUTE_ENG_DESC,DESCRIPTION,ORDER_CODE,
	 *         OPT_USER ,OPT_DATE}
	 */
	public Map getSYSPhaRoute(String route_code) {
		return SYSPhaRouteTool.getInstance().selectdata(route_code).getData();
	}

	/**
	 * �жϲ����Ƿ����ι�����
	 *
	 * @param parm :
	 *            org_code ҩ������
	 * @return boolean
	 */
	public boolean checkIndOrgBatch(TParm parm) {
		String org_code = parm.getValue("ORG_CODE");
		return INDTool.getInstance().checkIndOrgBatch(org_code);
	}

	/**
	 * �ж��Ƿ��ҩƷ���ڽ��е���
	 *
	 * @param parm
	 *            :org_code ҩ������ ; order_code ҩƷ����
	 *
	 * @return boolean
	 */
	public boolean checkIndStockReadjustp(TParm parm) {
		String org_code = parm.getValue("ORG_CODE");
		String order_code = parm.getValue("ORDER_CODE");
		return INDTool.getInstance().checkIndStockReadjustp(org_code,
				order_code);
	}

	/**
	 * ���������жϿ��
	 *
	 * @param parm
	 *            :org_code ҩ������ ; order_code ҩƷ���� ; dosage_qty ����
	 *
	 * @return boolean
	 */
	public boolean checkIndStockQty(TParm parm) {
		String org_code = parm.getValue("ORG_CODE");
		String order_code = parm.getValue("ORDER_CODE");
		double dosage_qty = parm.getDouble("DOSAGE_QTY");
		return INDTool.getInstance().inspectIndStock(org_code, order_code,
				dosage_qty);
	}

        /**
         * ҩ����ҩ�ۿ�
         *
         * @param parm
         *            :ORG_CODE=XXX(String),
         *             ORDER_CODE=XXX(String),
         *             DOSAGE_QTY=XXX(double),
         *             OPT_USER=XXX(String),
         *             OPT_DATE=XXX(Timestamp),
         *             OPT_TERM=XXX(String)
         * @param conn
         *            ����
         * @return
         */
        public Map reduceIndStock(TParm parm, String service_level,
                                  TConnection conn) {
            return INDTool.getInstance().reduceIndStock(parm, service_level, conn).
                getData();
        }

        /**
         * ҩ��ȡ����ҩ����
         *
         * @param parm
         *            :ORG_CODE=XXX(String),
         *             ORDER_CODE=XXX(String),
         *             DOSAGE_QTY=XXX(double),
         *             OPT_USER=XXX(String),
         *             OPT_DATE=XXX(Timestamp),
         *             OPT_TERM=XXX(String)
         * @param conn
         *            ����
         * @return
         */
        public Map unReduceIndStock(TParm parm, String service_level,
                                    TConnection conn) {
            return INDTool.getInstance().unReduceIndStock(parm, service_level, conn).
                getData();
        }

        /**
         * ҩ����ҩ����
         *
         * @param parm
         *            :ORG_CODE=XXX(String),
         *             ORDER_CODE=XXX(String),
         *             DOSAGE_QTY=XXX(double),
         *             OPT_USER=XXX(String),
         *             OPT_DATE=XXX(Timestamp),
         *             OPT_TERM=XXX(String)
         * @param conn
         *            ����
         * @return
         */
        public Map regressIndStock(TParm parm, String service_level,
                                   TConnection conn) {
            return INDTool.getInstance().regressIndStock(parm, service_level, conn).
                getData();
        }

        /**
         * ҩ��ȡ����ҩ�ۿ�
         *
         * @param parm
         *            :ORG_CODE=XXX(String),
         *             ORDER_CODE=XXX(String),
         *             DOSAGE_QTY=XXX(double),
         *             OPT_USER=XXX(String),
         *             OPT_DATE=XXX(Timestamp),
         *             OPT_TERM=XXX(String)
         * @param conn
         *            ����
         * @return
         */
        public Map unRegressIndStock(TParm parm, String service_level,
                                     TConnection conn) {
            return INDTool.getInstance().unRegressIndStock(parm, service_level,
                conn).getData();
        }


	/**
	 * ҩƷ��ҩ��λ����ҩ��λ����浥λת������
	 *
	 * @param order_code
	 *            ҩƷ����
	 * @return
	 */
	public Map getPhaTransUnit(String order_code) {
		TParm parm = new TParm();
		parm.setData("ORDER_CODE", order_code);
		return PhaTransUnitTool.getInstance().queryForAmount(parm).getData();
	}
	/**
	 * ���ݸ���orderCode��ѯsysFee����
	 * @param orderCode
	 * @return
	 */
	public TParm getSYSFee(String orderCode){
		TParm result=new TParm();
		if(orderCode==null||orderCode.trim().length()<1){
			return result;
		}
		TJDODBTool g;
		SYSSQL d;
		result=new TParm(TJDODBTool.getInstance().select(SYSSQL.getSYSFee(orderCode)));

		return result;
	}
}
