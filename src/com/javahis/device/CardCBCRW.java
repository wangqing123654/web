package com.javahis.device;

import jdo.sys.PatTool;

import com.dongyang.data.TParm;

/**
 * �� ��ҽ�ƿ�����
 * 
 * @author lix@bluecore.com.cn
 * 
 * 
 */
public class CardCBCRW {

	public static boolean loadFlg = true; // ����Load Dll
	static {
		try {
			System.loadLibrary("CardCBCRW"); // ����dll
			loadFlg = true;
		} catch (Throwable ex) {
			loadFlg = false;
		}
	}

	/**
	 * ��ʼ������dll
	 * 
	 * @return int
	 */
	public native static int init();

	/**
	 * ע��dll
	 * 
	 * @return int
	 */
	public native static int free();

	/**
	 * ����
	 * 
	 * @param commPort
	 * @param baud
	 * @param ic_type
	 * @param cardno
	 * @param address
	 * @param len
	 * @param data
	 * @return
	 */
	private native static int readCard(int commPort, int baud, int ic_type,
			int cardno, int address, int len, byte[] data);

	/**
	 * д��
	 * 
	 * @param comPort
	 * @param baud
	 * @param ic_type
	 * @param cardno
	 * @param pin
	 * @param address
	 * @param len
	 * @param data
	 * @return
	 */
	private native static int writeCard(int comPort, int baud, int ic_type,
			int cardno, byte[] pin, int address, int len, byte[] data);

	/**
	 * ��������;
	 * 
	 * @param commPort
	 * @return
	 */
	public TParm readEKT(int commPort) {

		TParm result = new TParm();
		try {
			int start = 40; // �ӵڼ�λ��ʼд 40
			// int total = 25; // ȡ��λ(������(12��)+��ˮ��(3��)+10λ���);
			// $$=======Modified 2012/02/22��ʱȥ����� START============$$//
			int total = 15;
			// $$=======Modified 2012/02/22��ʱȥ����� END============$$//

			// ��ӽ�
			byte[] data = new byte[total];
			int flg = readCard(commPort, 9600, 1, 1, start, total, data);
			// System.out.println("===error==="+error);
			if (flg == 0) {
				result.setErr(-1, "����ʧ��");
				return result;
			}
			// System.out.println("=====data========="+data);
			//System.out.println("data0" + data[2]);
			if (data[0] == -1) {
				// System.out.println("=====���ǿտ�======");
				result.setErr(-1, "�˿�Ƭ�ǿտ�");
				return result;
			}
			// ������Ƭ��������;
			String strData = byte2Str(data);
			//System.out.println("===strData===="+strData);

			String strMrNo = strData.substring(0, PatTool.getInstance().getMrNoLength()); //=====  chenxi
			// System.out.println("===strMrNo===="+strMrNo);
			result.setData("MR_NO", strMrNo);
			String strSeq = strData.substring(12, 15);
			result.setData("SEQ", strSeq);
			//System.out.println("=====result========" + result);
			// result.addData("returnData", strData);

		} catch (Exception e) {
			e.printStackTrace();
			result.setErr(-1, "����ʧ��");

		}

		return result;
	}

	/**
	 * д������;
	 * 
	 * @param commPort
	 * @param cardNo
	 * @param seq
	 * @return
	 */
	public TParm writeEKT(int commPort, String cardNo, String seq) {
		TParm result = new TParm();
		String inputData = cardNo + seq;

		try {
			int Start = 40; // �ӵڼ�λ��ʼд
			int Total = 15;
			byte[] Data = inputData.getBytes(); // д��Data
			System.out.println("==Data==" + Data.length);

			String sPin = "FFFF";
			byte[] Pin = sPin.getBytes();

			if (Data.length != 15) {
				result.setErr(-1, "�����������ӦΪ15");
				return result;
			}

			// д����
			int flg = writeCard(commPort, 9600, 1, 1, Pin, Start, Total, Data);
			if (flg < 1) {
				result.setErr(-1, "д��ʧ��");
				return result;
			}

		} catch (Exception e) {

			result.setErr(-1, "д��ʧ��");
		}
		return result;

	}

	// �� byteת�ɷ���
	private String byte2Str(byte[] Data) {
		String Total_Data = "";
		for (int i = 0; i < Data.length; i++) {
			Character CWord = new Character((char) Data[i]);
			Total_Data = Total_Data + CWord.toString();
		}
		// System.out.println("����Data===>"+Total_Data);
		return Total_Data;
	}
	

	public static void main(String args[]) {

		// System.out.println("hex========"+Integer.toHexString(0xFFFF));
		CardCBCRW cardRW = new CardCBCRW();
		int i = CardCBCRW.init();
		// д��
		/**TParm param = cardRW.writeEKT(1, "111111111111", "001");
		if (param.getErrCode() == -1) {
			System.out.println("=========д����������.============");
			System.out.println(param.getErrParm().getErrText());
		}**/
		
		
		
		//����
		TParm param1=cardRW.readEKT(1);
		if(param1.getErrCode()==-1){
			System.out.println("=========������������.============");
			System.out.println(param1.getErrParm().getErrText());
		}
		
		CardCBCRW.free();

	}

}
