package jdo.ope;

import javax.jws.WebService;

@WebService
public interface WeChatTool {

	/**
	 * ��������ϴ�
	 * 
	 * @param idNo
	 * @param base64Pdf
	 * @param dataXml
	 * @return
	 */
	public String upload(String idNo, String base64Pdf, String dataXml);

}
