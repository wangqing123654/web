package jdo.inv.ws;

import java.util.List;

import javax.jws.WebService;

import jdo.sys.Operator;

import com.dongyang.data.TParm;
import com.dongyang.data.TSocket;
import com.dongyang.manager.TIOM_AppServer;

/**
 * <p>Title:</p>ͬ��his inv_account ����
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: BlueCore</p>
 *
 * @author chenxi 20130805
 * @version 4.0
 */
@WebService
public class AccountServiceImplTool implements AccountServiceTool{

	@Override
	public String onSaveAccount(INVAccounts invAccounts) {
		System.out.println("invaccoun======��ʼ");
	List<INVAccount>  list =	invAccounts.getInvAccount() ;
	TParm inParm = new TParm() ;
	 for(int i=0;i<list.size();i++){
		    inParm.setData("REGION_CODE",i,list.get(i).getRegionCode());
		    inParm.setData("ACCOUNT_NO",i,list.get(i).getAccountNo());
			inParm.setData("CLOSE_DATE",i,list.get(i).getCloseDate());
			inParm.setData("ORG_CODE",i,list.get(i).getOrgCode());
			inParm.setData("INV_CODE",i,list.get(i).getInvCode());
			//��������
			inParm.setData("TOTAL_OUT_QTY",i,list.get(i).getTotalOutQty());
			inParm.setData("TOTAL_UNIT_CODE",i,list.get(i).getTotalUnitCode());
			//����
			inParm.setData("VERIFYIN_PRICE",i,list.get(i).getVerifyinPrice());
			//�ܽ��
			inParm.setData("VERIFYIN_AMT",i,list.get(i).getVerifyinAmt());
			//��Ӧ����
			inParm.setData("SUP_CODE",i,list.get(i).getSupCode());
			inParm.setData("OPT_USER",i,list.get(i).getOptUser());
			inParm.setData("OPT_TERM",i,list.get(i).getOptTerm());
	 }
	 inParm.setCount(inParm.getCount("INV_CODE")) ;
	 System.out.println("inparm=========="+inParm);
	 TSocket socket = new TSocket("127.0.0.1", 8080, "web");
	 TParm result = TIOM_AppServer.executeAction(socket,"action.inv.INVsettlementAction",
             "onSaveAccountHis", inParm);
	 if (result == null || result.getErrCode() < 0) {
			return "-1";  //-1����ʧ��
		}
		return "1";//-1����ɹ�
	}

}
