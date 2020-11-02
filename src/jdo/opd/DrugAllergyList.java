package jdo.opd;

import jdo.sys.Pat;

import com.dongyang.data.TModifiedList;
import com.dongyang.data.TParm;
/**
 * <p>Title: ����ʷ�б�</p>
 *
 * <p>Description: ����ʷ�б�</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: javahis</p>
 *
 * @author ehui 200800910
 * @version 1.0 
 */
public class DrugAllergyList extends TModifiedList{
	private Pat pat;

	public DrugAllergyList() {
		setMapString("mrNo:MR_NO;admDate:ADM_DATE;drugType:DRUG_TYPE;drugOringrdCode:DRUGORINGRD_CODE;admType:ADM_TYPE;caseNo:CASE_NO;deptCode:DEPT_CODE;drCode:DR_CODE;allergyNote:ALLERGY_NOTE;optUser:OPT_USER;optDate:OPT_DATE;optTerm:OPT_TERM");
	}

	public void setPat(Pat pat) {
		this.pat = pat;
	}

	public Pat getPat() {
		return pat;
	}

	/**
	 * ��������ʷ
	 * @return DrugAllergy
	 */
	public DrugAllergy newDrugAllergy() {
		DrugAllergy drugallergy = new DrugAllergy();
		drugallergy.setPat(getPat());
		this.newData(drugallergy);
		return drugallergy;
	}

	/**
	 * �õ�����ʷ
	 * @param index int
	 * @return DrugAllergy
	 */
	public DrugAllergy getDrugAllergy(int index) {
		return (DrugAllergy) get(index);
	}

	/**
	 * ��ʼ��TPARM
	 * @param parm
	 * @return �棺�ɹ����٣�ʧ��
	 */
	public boolean initParm(TParm parm) {
		if (parm == null)
			return false;
		int count = parm.getCount();
		for (int i = 0; i < count; i++) {
			DrugAllergy drugallergy =new DrugAllergy();
			drugallergy.setPat(getPat());
			drugallergy.setMapString(getMapString());
			if (!drugallergy.initParm(parm, i))
				return false;
			add(drugallergy);
		}
		return true;
	}
}
