package jdo.hl7.ws;


import java.util.List;
import javax.jws.WebService;
import jdo.hl7.pojo.Hl7Result;
/**
 * <p>
 * Title: 医技webService
 * </p>
 * 
 * <p>
 * Description: 医技webService
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
     * 根据号码得到LIS医嘱信息
     * @param ApplyNo
     * @return
     */
	public List<Hl7Result> getLisOrderData(String ApplyNo);
	/**
	 * LISHL7消息处理方式
	 * @param Hl7Data
	 * @return
	 */
	public List<Hl7Result> onLisOrderOperate(String Hl7Data);
	 /**
     * 根据号码得到RIS医嘱信息
     * @param ApplyNo
     * @return
     */
	public List<Hl7Result> getRisOrderData(String ApplyNo, String ExecDept);
	/**
	 * RISHL7消息处理方式
	 * @param Hl7Data
	 * @return
	 */
	public List<Hl7Result> onRisOrderOperate(String Hl7Data);
	/**
	 * 申请号余额验证
	 * @param 
	 * @return
	 */
	public List<Hl7Result> onFeeQuery(String ApplyNo,String Cat1Type);
	/**
	 * 申请号余额验证
	 * @param 
	 * @return
	 */
	public List<Hl7Result> onRisFeeQuery(String ApplyNo);
	/**
     * 根据号码得到RIS医嘱信息
     * @param ApplyNo
     * @return
     */
	public String getRisOrderLine(String ApplyNo,String ExecDept, String MrNo,
			String StartDate, String EndDate);
	/**
	 * RISHL7消息处理方式
	 * @param Hl7Data
	 * @return
	 */
	public String onRisOrderOperateLine(String Hl7Data);
	/**
	 * RIS申请号余额验证
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
	 * 得到HIS病患信息方法
	 * @param mrNo
	 * @return
	 */
	public String getMchiszfPatData(String mrNo);

}
