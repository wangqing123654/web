package jdo.bms.ws;
import java.util.List;
import javax.jws.WebService;
/**
 * <p>
 * Title: 血库webService
 * </p>
 * 
 * <p>
 * Description: 血库webService
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
public interface IBmsService {
	/**
     * 根据号码得到申请单信息
     * @param ApplyNo
     * @return
     */
	public List<String> getBmsApplyData(String ApplyNo);
	/**
	 * 根据xml数据血库审核申请信息
	 * @param XmlData
	 * @return
	 */
	public List<String> onBmsApplyCheckData(String XmlData);
	/**
	 * 根据xml数据血型鉴定信息
	 * @param XmlData
	 * @return
	 */
	public List<String> onBmsConfirmData(String XmlData);
	/**
	 * 根据xml数据血库收费
	 * @param XmlData
	 * @return
	 */
	public List<String> onBmsFee(String XmlData);
	/**
	 * 根据xml数据出库
	 * @param XmlData
	 * @return
	 */
	public List<String> onBmsOutBound(String XmlData);
	/**
	 * 根据xml数据退库
	 * @param XmlData
	 * @return
	 */
	public List<String> onBmsRefund(String XmlData);
	/**
     * 根据号码得到输血反应信息
     * @param ApplyNo
     * @return
     */
	public List<String> getBmsTranReacData(String ApplyNo);
	/**
	 * 根据xml数据血库审核输血反应信息
	 * @param XmlData
	 * @return
	 */
	public List<String> onBmsTranReacCheckData(String XmlData);
	
	
	
}
