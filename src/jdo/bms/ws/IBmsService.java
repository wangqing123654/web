package jdo.bms.ws;
import java.util.List;
import javax.jws.WebService;
/**
 * <p>
 * Title: Ѫ��webService
 * </p>
 * 
 * <p>
 * Description: Ѫ��webService
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
     * ���ݺ���õ����뵥��Ϣ
     * @param ApplyNo
     * @return
     */
	public List<String> getBmsApplyData(String ApplyNo);
	/**
	 * ����xml����Ѫ�����������Ϣ
	 * @param XmlData
	 * @return
	 */
	public List<String> onBmsApplyCheckData(String XmlData);
	/**
	 * ����xml����Ѫ�ͼ�����Ϣ
	 * @param XmlData
	 * @return
	 */
	public List<String> onBmsConfirmData(String XmlData);
	/**
	 * ����xml����Ѫ���շ�
	 * @param XmlData
	 * @return
	 */
	public List<String> onBmsFee(String XmlData);
	/**
	 * ����xml���ݳ���
	 * @param XmlData
	 * @return
	 */
	public List<String> onBmsOutBound(String XmlData);
	/**
	 * ����xml�����˿�
	 * @param XmlData
	 * @return
	 */
	public List<String> onBmsRefund(String XmlData);
	/**
     * ���ݺ���õ���Ѫ��Ӧ��Ϣ
     * @param ApplyNo
     * @return
     */
	public List<String> getBmsTranReacData(String ApplyNo);
	/**
	 * ����xml����Ѫ�������Ѫ��Ӧ��Ϣ
	 * @param XmlData
	 * @return
	 */
	public List<String> onBmsTranReacCheckData(String XmlData);
	
	
	
}
