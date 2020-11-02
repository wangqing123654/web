package jdo.reg;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import jdo.ekt.EKTIO;
import jdo.ins.INSMZConfirmTool;
import jdo.ins.INSOpdTJTool;
import jdo.ins.INSTJReg;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.data.TSocket;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.util.StringTool;
/**
 * <p>
 * Title: ���ｨ��ҽ���˷�
 * </p>
 * 		
 * <p>
 * Description: ���ｨ��ҽ���˷�
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: blueocre
 * </p>
 * 
 * @author pangben 2012-9-06
 * @version 4.0
 */
public class REGCcbReTool extends TJDOTool{
	  /**
     * ʵ��
     */
    private static REGCcbReTool instanceObject;
    /**
     * �õ�ʵ��
     * @return PatAdmTool
     */
    public static REGCcbReTool getInstance() {
        if (instanceObject == null)
            instanceObject = new REGCcbReTool();
        return instanceObject;
    }

	/**
	 * ҽ���˷Ѳ���
	 */
	public boolean reSetInsSave(TParm opbParm,TControl control) {

		// ��ѯҽ�����
		String sql = "SELECT PAY_INS_CARD,TOT_AMT,PAY_OTHER2 FROM BIL_OPB_RECP WHERE PRINT_NO='"
				+ opbParm.getValue("PRINT_NO") + "'";
		TParm bilParm = new TParm(TJDODBTool.getInstance().select(sql));

		TParm reSetInsParm = new TParm();
		TParm parm = new TParm();
		// System.out.println("---------------ҽ���˷�-------------:" + opbParm);
		parm.setData("CASE_NO", opbParm.getValue("CASE_NO"));
		parm.setData("INV_NO", opbParm.getValue("PRINT_NO"));
		parm.setData("RECP_TYPE", "OPB");// �շ�����

		// ��ѯ�Ƿ�ҽ�� �˷�
		TParm result = INSOpdTJTool.getInstance().selectInsInvNo(parm);
		if (result.getErrCode() < 0) {
			return false;
		}
		if (result.getCount("CASE_NO") <= 0) {// ����ҽ���˷�
			return true;
		}
		getInsValue(result,opbParm, bilParm,control);
		reSetInsParm.setData("CASE_NO",opbParm.getValue("CASE_NO"));// �����
		reSetInsParm.setData("CONFIRM_NO", result.getValue("CONFIRM_NO", 0));// ҽ�������
		reSetInsParm.setData("INS_TYPE", result.getValue("INS_TYPE"));// ҽ����ҽ����
		reSetInsParm.setData("RECP_TYPE", "OPB");// �շ�����
		reSetInsParm.setData("UNRECP_TYPE", "OPBT");// �˷�����
		reSetInsParm.setData("OPT_USER", opbParm.getValue("OPT_USER"));// id
		reSetInsParm.setData("OPT_TERM", opbParm.getValue("OPT_TERM"));// ip
		reSetInsParm.setData("INV_NO", opbParm.getValue("PRINT_NO"));// Ʊ�ݺ�
		reSetInsParm.setData("PAT_TYPE", opbParm.getValue("CTZ1_CODE"));// ���
		reSetInsParm.setData("REGION_CODE", opbParm.getValue("NHI_NO"));// ���

		result = INSTJReg.getInstance().insResetCommFunction(
				reSetInsParm.getData());

		if (result.getErrCode() < 0) {
			control.messageBox(result.getErrText());
			return false;
		}
		
		return true;
	}
	/**
	 * ҽ����ȡ����
	 * 
	 * @return
	 */
	private void getInsValue(TParm resultParm, TParm opbParm,TParm sumParm,TControl control) {
		TParm insFeeParm = new TParm();
		insFeeParm.setData("CASE_NO", opbParm.getValue("CASE_NO"));// �˹�ʹ��
		insFeeParm.setData("RECP_TYPE", "OPB");// �շ�ʹ��
		// insFeeParm.setData("CONFIRM_NO", resultParm.getValue("CONFIRM_NO",
		// 0));// ҽ�������
		// ---д������Ҫ�޸�
		insFeeParm.setData("NAME", opbParm.getValue("PAT_NAME"));
		insFeeParm.setData("MR_NO", opbParm.getValue("MR_NO"));// ������
		TParm result = INSMZConfirmTool.getInstance().queryMZConfirmOne(
				insFeeParm);// ��ѯҽ����Ϣ
		if (result.getErrCode() < 0 || result.getCount() <= 0) {
			return;
		}
		control.messageBox("Ʊ�ݺ���:"+opbParm.getValue("PRINT_NO")+" ҽ���˷ѽ��:"
				+ StringTool.round(sumParm.getDouble("PAY_INS_CARD", 0), 2)
				+ ","
				+ "�����˿�:"
				+ (StringTool.round(sumParm.getDouble("TOT_AMT", 0)
						- sumParm.getDouble("PAY_INS_CARD", 0), 2)));
		if (result.getInt("INS_CROWD_TYPE", 0) == 1
				&& result.getInt("INS_PAT_TYPE", 0) == 1) {
			resultParm.setData("INS_TYPE", "1");// ҽ����ҽ���
		} else if (result.getInt("INS_CROWD_TYPE", 0) == 1
				&& result.getInt("INS_PAT_TYPE", 0) == 2) {
			resultParm.setData("INS_TYPE", "2");// ҽ����ҽ���
		} else if (result.getInt("INS_CROWD_TYPE", 0) == 2
				&& result.getInt("INS_PAT_TYPE", 0) == 2) {
			resultParm.setData("INS_TYPE", "3");// ҽ����ҽ���
		}
	}
	/**
	 * ���нӿڵ���
	 * @param parm
	 * @param control
	 * @return
	 */
	public TParm getCcbRe(TParm parm){     		
		//socketͨѶ ipд��127.0.0.1
		TSocket socket = new TSocket("127.0.0.1",8080,"web");
		TParm ektCcbParm=REGCcbTool.getInstance().getTradeInfo(parm);
		if (ektCcbParm.getErrCode()<0 || ektCcbParm.getCount()<=0) {
			ektCcbParm=new TParm();
			ektCcbParm.setErr(-1,"���нӿڵ��ó��ִ���,����ϵ��Ϣ����");
			return ektCcbParm;
		}
		//����	
		TParm inParm = new TParm();
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		inParm.setData("HIS_CODE", parm.getValue("NHI_NO")); //ҽԺ���룺000551
		inParm.setData("CARD_NO", ektCcbParm.getValue("CARD_NO",0)); //���п��� 
		inParm.setData("PERSON_NO", ektCcbParm.getValue("PERSON_NO",0)); //���˱���  personal_no
		inParm.setData("O_TRAN_NO", parm.getValue("CASE_NO")); //ԭ��ͬ�� case_no	
		inParm.setData("O_STREAM_NO", ektCcbParm.getValue("BUSINESS_NO",0)); //ԭ������ˮ�� stream_no
		inParm.setData("O_PAY_SEQ", parm.getValue("RECEIPT_NO")); //ԭ֧��˳��� RECEIPT_NO
		inParm.setData("TRAN_NO",parm.getValue("CASE_NO")); //��ͬ�� ��ԭ��ͬ����ͬ
		inParm.setData("AMOUNT", String.valueOf(parm.getDouble("AMT"))); //�������ԷѲ���Ϊȫ�����ã�ҽ������Ϊ�Էѽ��
		inParm.setData("DATE",  sf.format((Date)ektCcbParm.getData("OPT_DATE",0))); //ԭ��������
		inParm.setData("RETURN_TYPE", "2"); //2-���սɷ��˷ѡ�3-���սɷ��˷�		
		inParm.setData("OPT_ID", parm.getValue("OPT_USER")); //����ԱID	
		inParm.setData("OPT_NAME", parm.getValue("OPT_NAME")); //����Ա����
		inParm.setData("OPT_IP", parm.getValue("OPT_TERM")); //����ԱIP
		inParm.setData("OPT_DATE", SystemTool.getInstance().getDate()); //����ʱ��
		inParm.setData("GUID", ektCcbParm.getValue("GUID",0)); //���β�����ΨһID����java����һ��40λα�����
		inParm.setData("TOKEN", ""); //��
		inParm.setData("EXECUTE_FLAG", "0");
		inParm.setData("EXECUTE_MESSAGE", ""); //��
		inParm.setData("ACCOUNT", ""); //��	
		//�����˷ѽӿ� DoReturn����
		TParm resultData = TIOM_AppServer.executeAction(socket,"action.ccb.CBCClientAction",
				"DoReturn", inParm);
		if (resultData.getErrCode()<0) {
			return resultData;
		}
		Map<String, Object> m = (HashMap)resultData.getData();		 
		Map<String, Object> r = (HashMap)m.get("Result");		
		Map<String, Object> info = (HashMap)m.get("OperateInfo");	
		Map<String, Object> commit = (HashMap)m.get("ReturnData");							
		String streamNo = (String) commit.get("STREAM_NO");
		String flg = (String) r.get("EXECUTE_FLAG"); 
		String optName = (String) info.get("OPT_ID");
		String optIp = (String) info.get("OPT_IP");
		String guid = (String) info.get("GUID");							
		 //ִ�н��:0�ɹ� 1����
		if (null!=resultData && "0".equals(flg)) {																					
			//�ز�							
			//resultData.getValue("STREAM_NO", 0); //�˱��˷ѵ�stream_no
			//resultData.getValue("ACCOUNT", 0); //����ʱ�䣺��дekt_ccb
			resultData.setData("CARD_NO",ektCcbParm.getValue("CARD_NO",0));
			resultData.setData("MR_NO",ektCcbParm.getValue("MR_NO",0));
			resultData.setData("CASE_NO",parm.getValue("CASE_NO"));
			resultData.setData("BUSINESS_NO",streamNo);					
			resultData.setData("PAT_NAME",ektCcbParm.getValue("PAT_NAME",0));
			resultData.setData("AMT",-parm.getDouble("AMT"));							
			resultData.setData("OPT_USER",optName);
			resultData.setData("OPT_TERM",optIp);
			resultData.setData("GUID",guid);
			resultData.setData("RECEIPT_NO",ektCcbParm.getValue("RECEIPT_NO",0));
		}else{
			//control.messageBox(resultData.getValue("EXECUTE_MESSAGE", 0));//������Ϣ
			resultData.setErr(-1,resultData.getValue("EXECUTE_MESSAGE", 0));
			return resultData;
		}
		return resultData;
	}
	/**
	 * ���һ��EKT_CCB_TREDE �˷�����
	 * @param parm
	 * @return
	 */
	public TParm saveEktCcbTrede(TParm parm){
		TParm ccbParm=new TParm();
		ccbParm.setData("TRADE_NO",SystemTool.getInstance().getNo("ALL",
				"EKT", "CCB_TRADE_NO", "CCB_TRADE_NO"));//��ˮ��
		ccbParm.setData("CARD_NO",parm.getValue("CARD_NO"));//���п���
		ccbParm.setData("MR_NO",parm.getValue("MR_NO"));//������
		ccbParm.setData("CASE_NO",parm.getValue("CASE_NO"));//�����
		ccbParm.setData("BUSINESS_NO",parm.getValue("BUSINESS_NO"));//���׺�
		ccbParm.setData("PAT_NAME",parm.getValue("PAT_NAME"));//��������
		ccbParm.setData("AMT",parm.getDouble("AMT"));//���
		ccbParm.setData("STATE","2");//�˷�ע��: 2
		ccbParm.setData("BUSINESS_TYPE",parm.getValue("BUSINESS_TYPE"));//�˷�����
		ccbParm.setData("OPT_USER",parm.getValue("OPT_USER"));
		ccbParm.setData("OPT_DATE",SystemTool.getInstance().getDate());
		ccbParm.setData("OPT_TERM",parm.getValue("OPT_TERM"));
		ccbParm.setData("TOKEN","");
		ccbParm.setData("BANK_FLG","Y");								
		ccbParm.setData("GUID",parm.getValue("GUID"));//���β�����ΨһID
		ccbParm.setData("RECEIPT_NO",parm.getValue("RECEIPT_NO"));//�վݺ���
		return REGCcbTool.getInstance().insertOpbCcbTrade(ccbParm);
	}
}

