package jdo.sys.ws;

import java.sql.Timestamp;

import javax.jws.WebService;

import jdo.ind.INDSQL;
import jdo.ind.IndSysParmTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.config.TConfig;
import com.dongyang.data.TParm;
import com.dongyang.data.TSocket;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.util.StringTool;
import com.javahis.bsm.XmlUtils;

/**
 * <p>Title: ����װ��ҩ�����нӿ�ʵ��301</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: JAVAHIS</p>
 *
 * <p>Company:  </p>
 *
 * @author chenx  2013-05-22
 * @version 4.0
 */
@WebService
public class PretestMedicineServiceImpl implements IPretestMedicineService{


	/**
	 * 301������ʵ��
	 */
	public String onPrepareFinish(String xml) {
		TParm parm = XmlUtils.createSendxmltoParm(xml);
		String message = "" ;
		int    status  = 0 ;
		if(parm.getValue("PRESC_NO").length()<0 || parm == null){
			message = "���͵Ĵ�����û���յ�" ;
			return messageReturn(message, status) ;
		}
		String sql =  "      UPDATE OPD_ORDER SET PICK_TMIE = SYSDATE,LOCATION = '"+parm.getValue("LOCATION")+"' " +
				      "     WHERE RX_NO='"+parm.getValue("PRESC_NO")+"'" ;
		TParm result = new TParm(TJDODBTool.getInstance().update(sql));
		if(result.getErrCode()<0){
			message="��ҩʧ��" ;
			return messageReturn(message, status) ;
		}
		message ="��ҩ�����" ;
		status  = 1;
		return messageReturn(message, status);
	}

	/**
	 * 305����ʵ�֣���IND_REQUESTM��IND_REQUESTD�����ֶ�
	 */
	public String onRequestBox(String xml) {
		TParm result = new TParm() ;
		TParm parm = XmlUtils.createSendxml305toParm(xml) ;
		String returnxml = ""  ;
		System.out.println("parm========"+parm);
        TConfig config = TConfig.getConfig("WEB-INF\\config\\system\\TConfig.x");
        String request = config.getString("", "BOXDEPT_CODE");  //���벿��
		//��������
		TParm parmMain =  new TParm() ;
		String requestNo= SystemTool.getInstance().getNo("ALL", "IND",
                "IND_REQUEST", "No");
		parmMain.setData("REQUEST_NO", requestNo) ; //���뵥��
		Timestamp date = SystemTool.getInstance().getDate();
		parmMain.setData("REQTYPE_CODE", "DEP");//�������
		parmMain.setData("APP_ORG_CODE", request);//���벿��
		parmMain.setData("TO_ORG_CODE", this.onGetsupDept(request));//�������벿��
		parmMain.setData("REQUEST_DATE",date);//��������
		parmMain.setData("REQUEST_USER", request);//������Ա
		parmMain.setData("REASON_CHN_DESC", "");//����ԭ��
		parmMain.setData("DESCRIPTION", "");//��עDRUG_CATEGORY
		parmMain.setData("DRUG_CATEGORY", "1");//��ͨҩƷ
        String unit_type = "0";
            unit_type = IndSysParmTool.getInstance().onQuery().getValue(
                "UNIT_TYPE",
                0);
        parmMain.setData("UNIT_TYPE", unit_type);//���뵥ʹ�õ�λ1:��浥λ 2:��ҩ��λ 
        parmMain.setData("URGENT_FLG", "N");//����ע��
        parmMain.setData("REGION_CODE", "H01");//�������
        parmMain.setData("OPT_USER", request);
        parmMain.setData("OPT_DATE", date);
        parmMain.setData("OPT_TERM", "172.20.11.89");
        TSocket socket = new TSocket("127.0.0.1", 8080, "webgy");
        result = TIOM_AppServer.executeAction(socket,
                "action.ind.INDRequestAction", "onBoxInsertM", parmMain);     
		if(result.getErrCode()<0){
			returnxml = this.messageReturn(result.getErrName()+result.getErrText(), 0) ;
			 return  returnxml ;
				}
		//============================����ϸ��
		TParm parmDetail =  new TParm() ;
		for(int i=0;i<parm.getCount("ORDER_CODE");i++){
			parmDetail.addData("REQUEST_NO", requestNo) ;//���뵥��
			parmDetail.addData("SEQ_NO", i+1) ;//���
			parmDetail.addData("ORDER_CODE", parm.getValue("ORDER_CODE", i)) ;//ҩƷ����
			parmDetail.addData("QTY", parm.getValue("APPQUANTITY", i)) ;//����APPQUANTITY
			parmDetail.addData("UNIT_CODE", "150") ;//��λ���룬���Ǻ�װҩ
			 TParm resultorderCode = new TParm(TJDODBTool.getInstance().select(INDSQL.
			            getPHAInfoByOrder(parm.getValue("ORDER_CODE", i))));
			        if (resultorderCode.getErrCode() < 0) {
			            return  this.messageReturn("ҩƷ��Ϣ����", 0) ;
			        }
			        double stock_price = 0;
			        double retail_price = 0;
			  stock_price = StringTool.round(resultorderCode.getDouble("STOCK_PRICE", 0)
                            * resultorderCode.getDouble("DOSAGE_QTY", 0),
                            2);
               retail_price = StringTool.round(resultorderCode.getDouble("RETAIL_PRICE", 0)
                             * resultorderCode.getDouble("DOSAGE_QTY", 0),
                             2);
			parmDetail.addData("RETAIL_PRICE", stock_price) ;//���ۼ�
			parmDetail.addData("STOCK_PRICE", retail_price) ;//�ɱ���VALID_DATE
			parmDetail.addData("UPDATE_FLG", "0") ;//����״̬ 0:������
			parmDetail.addData("BATCH_NO", "") ;//
			parmDetail.addData("ACTUAL_QTY", 0) ;//ʵ�ʽ������� (�ۼ������)
			parmDetail.addData("OPT_USER", "040102") ;//
			parmDetail.addData("OPT_DATE", date) ;//
			parmDetail.addData("OPT_TERM", "172.20.11.89") ;//
		}
		result = TIOM_AppServer.executeAction(socket,
                "action.ind.INDRequestAction", "onInsertD", parmDetail);  
		if(result.getErrCode()<0){
			returnxml = this.messageReturn(result.getErrName()+result.getErrText(), 0) ;
			 return  returnxml ;
				}
		returnxml = this.messageReturn("�ɹ�", 1) ;
		return  returnxml ;
	}
	/**
	 * ������Ϣ
	 * @param status
	 * @return
	 */
	private String messageReturn(String message,int status) {
		String xml = XmlUtils.createParmtoXml(message, status).toString() ;
		return xml ;
	}
	/**
	 * ��ȡ��Ӧ����
	 */
	public String onGetsupDept(String request){
		String supdept= "" ;
		String sql = "SELECT SUP_ORG_CODE FROM IND_ORG WHERE ORG_CODE = '"+request+"'"   ;
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql)) ;
        if(parm.getErrCode()<0){
        	return supdept  ;
        }
        supdept = parm.getValue("SUP_ORG_CODE", 0) ;
		return supdept ;
	}
}
