package jdo.hl7.ws;


import java.util.List;
import javax.jws.WebService;
import jdo.hl7.pojo.Hl7Result;
/**
 * <p>
 * Title: ҽ��webService
 * </p>
 * 
 * <p>
 * Description: ҽ��webService
 * </p>
 * 
 * <p>
 * Copyright: BLUECORE
 * </p>
 * 
 * <p>
 * Company:BLUECORE
 * </p>
 * 
 * @author SHIBL
 * @version 1.0
 */
@WebService
public interface IHl7Service {
    /**
     * ���ݺ���õ�LISҽ����Ϣ
     * @param ApplyNo
     * @return
     */
	public List<Hl7Result> getLisOrderData(String ApplyNo);
	/**
	 * LISHL7��Ϣ����ʽ
	 * @param Hl7Data
	 * @return
	 */
	public List<Hl7Result> onLisOrderOperate(String Hl7Data);
	 /**
     * ���ݺ���õ�RISҽ����Ϣ
     * @param ApplyNo
     * @return
     */
	public List<Hl7Result> getRisOrderData(String ApplyNo, String ExecDept);
	/**
	 * RISHL7��Ϣ����ʽ
	 * @param Hl7Data
	 * @return
	 */
	public List<Hl7Result> onRisOrderOperate(String Hl7Data);
	/**
	 * ����������֤
	 * @param 
	 * @return
	 */
	public List<Hl7Result> onFeeQuery(String ApplyNo,String Cat1Type);
	/**
	 * ����������֤
	 * @param 
	 * @return
	 */
	public List<Hl7Result> onRisFeeQuery(String ApplyNo);
	/**
     * ���ݺ���õ�RISҽ����Ϣ
     * @param ApplyNo
     * @return
     */
	public String getRisOrderLine(String ApplyNo,String ExecDept, String MrNo,
			String StartDate, String EndDate);
	/**
	 * RISHL7��Ϣ����ʽ
	 * @param Hl7Data
	 * @return
	 */
	public String onRisOrderOperateLine(String Hl7Data);
	/**
	 * RIS����������֤
	 * @param 
	 * @return
	 */
	public String onRisFeeQueryLine(String ApplyNo);
	/**
	 * 
	 * @param ExecDept
	 * @param MrNo
	 * @param StartDate
	 * @param EndDate
	 * @return
	 */
	public List<Hl7Result> getRisOrderList(String ExecDept, String MrNo,
			String StartDate, String EndDate);
	
	/**
	 * �õ�HIS������Ϣ����
	 * @param mrNo
	 * @return
	 */
	public String getMchiszfPatData(String mrNo);

}
