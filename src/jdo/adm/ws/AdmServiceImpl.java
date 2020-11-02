package jdo.adm.ws;

import java.util.ArrayList;
import java.util.List;
import javax.jws.WebService;
import com.dongyang.data.TParm;
/**
 * 
 * @author shibl
 *
 */
@WebService
public class AdmServiceImpl implements IAdmService{

	@Override
	public List<String> getAdmPatInfo(Object MrNo) {
		List<String>  list=new ArrayList<String>();
		if(MrNo==null){
			list.add("-1");
			list.add("��β��ܿ�");
			return list;
		}
		TParm parm=AdmServiceTool.getInstance().getAdmPatInfo(String.valueOf(MrNo));
		if(parm.getErrCode()<0){
			list.add("-1");
			list.add(parm.getErrText());
			return list;
		}
		if(parm.getCount()>0){
			list.add("0");
			list.add(parm.getValue("PAT_NAME", 0));// ĸ������
			list.add(parm.getValue("BED_NO_DESC", 0));//����
			list.add(parm.getValue("CONTACTS_TEL", 0));//��ϵ�绰
			list.add(parm.getValue("SEX", 0));//�Ա�
			list.add("");//��ע�� Ϊ��
		}
		return list;
	}

}
