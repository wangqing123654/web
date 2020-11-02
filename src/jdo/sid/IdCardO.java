package jdo.sid;

import java.io.File;

import jdo.ekt.EKTIO;
import jdo.sys.PatTool;

import com.bluecore.cardreader.CardInfoBO;
import com.bluecore.cardreader.ICallBack;
import com.bluecore.cardreader.IdCardReaderUtil;
import com.dongyang.config.TConfig;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;

public class IdCardO {
	/**
	 * ʵ��
	 */
	private static IdCardO instanceObject;
	/**
	 * �õ�ʵ��
	 * 
	 * @return EKTIO
	 */
	public static IdCardO getInstance() {
		if (instanceObject == null)
			instanceObject = new IdCardO();
		return instanceObject;
	}
	private String path = getEktPort();// ̩��ҽ�ƿ�������Ĭ�ϲ����ż�ֵ����
	/**
	 * ��ȡ���֤����
	 * ����:1.���֤ͼƬ�ŵ�λ�� 2.������Ϣ
	 * @return
	 */
	public TParm readIdCard() {
		TParm parm = new TParm();
		CardInfoBO bo=null;
		try {
			bo=IdCardReaderUtil.getCardInfo(path);
		} catch (Exception e) {
			parm.setErr(-1,"���»�ȡ��Ϣ");
			System.out.println("���»�ȡ��Ϣ:"+e.getMessage());
			return parm;
			// TODO: handle exception
		}
        //CardInfoBO cardInfo = IdCardReaderUtil.getCardInfo(dir);
        if(bo ==null){
        	parm.setErr(-1,"δ������֤��Ϣ,�����²���");
        	return parm;
        }   
		parm.setData("PAT_NAME",bo.getName().trim());//����
		parm.setData("SEX_CODE",bo.getSex().trim().equals("��")?"1":"2");//�Ա�
		parm.setData("BRITHPLACE",bo.getFolk().trim());//����
		parm.setData("BIRTH_DATE",bo.getBirth().trim().substring(0,4)+"/"+bo.getBirth().trim().substring(4,6)+"/"+
				bo.getBirth().trim().substring(6,bo.getBirth().trim().length()));//����
		parm.setData("IDNO",bo.getCode().trim());//���֤��
		parm.setData("RESID_ADDRESS",bo.getAdd().trim());//סַ
		//ͨ�����֤�Ų�ѯ������Ϣ
		TParm infoParm=PatTool.getInstance().getInfoForIdNo(parm);
		if(infoParm.getCount()>0){
			parm=infoParm;
			//parm.setData("MESSAGE","�Ѵ��ڴ˾��ﲡ����Ϣ");
		}else{
			parm.setData("MESSAGE","�����ڴ˾��ﲡ����Ϣ");
		}
		delFolder(path);
		return parm;
	}
	/**
	 * �õ�debug���
	 * 
	 * @return
	 */
	public String getEktPort() {
		String com = "";
		com = getProp().getString("", "sid.path");
		if (com == null || com.trim().length() <= 0) {
			//System.out.println("�����ļ�ҽ�ƿ�com��Ǵ���");
		}
		return com;
	}

	/**
	 * ��ȡ TConfig.x
	 * 
	 * @return TConfig
	 */
	public static TConfig getProp() {
		TConfig config = TConfig
				.getConfig("WEB-INF\\config\\system\\TConfig.x");
		if (config == null) {
			//System.out.println("TConfig.x �ļ�û���ҵ���");
		}
		return config;
	}
	// ɾ���ļ���
	// param folderPath �ļ�����������·��

	public static void delFolder(String folderPath) {
		try {
			delAllFile(folderPath); // ɾ����������������
			String filePath = folderPath;
			filePath = filePath.toString();
			java.io.File myFilePath = new java.io.File(filePath);
			myFilePath.delete(); // ɾ�����ļ���
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ɾ��ָ���ļ����������ļ�
	// param path �ļ�����������·��
	public static boolean delAllFile(String path) {
		boolean flag = false;
		File file = new File(path);
		if (!file.exists()) {
			return flag;
		}
		if (!file.isDirectory()) {
			return flag;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + "/" + tempList[i]);// ��ɾ���ļ���������ļ�
				delFolder(path + "/" + tempList[i]);// ��ɾ�����ļ���
				flag = true;
			}
		}
		return flag;
	}
}
