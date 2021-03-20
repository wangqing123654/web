package jdo.ope;

public interface WeChatTool {

	/**
	 * 流调结果上传
	 * 
	 * @param idNo
	 * @param base64Pdf
	 * @param dataXml
	 * @return
	 */
	public String upload(String idNo, String base64Pdf, String dataXml);

}
