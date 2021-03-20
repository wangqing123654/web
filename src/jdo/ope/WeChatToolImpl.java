package jdo.ope;

import javax.jws.WebService;

import com.dongyang.config.TConfig;
import com.dongyang.data.TSocket;
import com.dongyang.manager.TIOM_FileServer;
import sun.misc.BASE64Decoder;

@WebService
public class WeChatToolImpl implements WeChatTool {

	@Override
	public String upload(String idNo, String base64Pdf, String dataXml) {
		//
		TSocket s = new TSocket(TConfig.getSystemValue("FileServer.Main.IP"), TSocket.FILE_SERVER_PORT);
		//
		byte data[] = null;
		try {
			data = this.decoderBase64File(base64Pdf);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 流调PDF上传
		boolean is = TIOM_FileServer.writeFile(s,
				TConfig.getSystemValue("FileServer.Main.Root") + "\\流调结果\\" + idNo + "\\" + idNo + ".pdf", data);
		// 流程xml数据上传
		is = TIOM_FileServer.writeFile(s,
				TConfig.getSystemValue("FileServer.Main.Root") + "\\流调结果\\" + idNo + "\\" + idNo + ".xml",
				dataXml.getBytes());
		return is ? "success" : "failed";
	}

	/**
	 * base64解码
	 * 
	 * @param base64Code
	 * @return
	 * @throws Exception
	 */
	public byte[] decoderBase64File(String base64Code) throws Exception {
		byte[] buffer = new BASE64Decoder().decodeBuffer(base64Code);
		return buffer;
	}

}
