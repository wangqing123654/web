package com.javahis.device;

import java.math.BigDecimal;
import java.util.Hashtable;

import jdo.sys.PatTool;

import com.dongyang.data.TParm;

/**
 * <p>
 * Title: (̩����Ѫ��)ҽ�ƿ��ӿ�
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2011-09-15
 * </p>
 * 
 * <p>
 * Company: javahis
 * </p>
 * 
 * @li.xiang19790130@gmial.com
 * @version 1.0
 */
public class CardRW {
	public static boolean loadFlg = true; // ����Load Dll
	static Hashtable<Integer, String> h = new Hashtable<Integer, String>(); // ����ѶϢ

	public CardRW() {
	}

	static {
		try {
			System.loadLibrary("CardRWNew"); // ����dll
			loadFlg = true;
			// ���ش�����Ϣ;
			setErrorMessage();
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
	 *            String
	 * @param start
	 *            int
	 * @param total
	 *            int
	 * @param data
	 *            byte[]
	 * @return int
	 */
	private native static int readCard(String commPort, int start, int total,
			byte[] data);

	/**
	 * д��
	 * 
	 * @param commPort
	 *            String
	 * @param start
	 *            int
	 * @param total
	 *            int
	 * @param pin
	 *            byte[]
	 * @param data
	 *            byte[]
	 * @return int
	 */
	private native static int writeCard(String commPort, int start, int total,
			byte[] pin, byte[] data);

	/**
	 * ��ҽ�ƿ�
	 * 
	 * @param CommPort
	 *            String
	 * @return TParm
	 */
	public TParm readEKT(String commPort) {
		TParm result = new TParm();
		try {
			int start = 40; // �ӵڼ�λ��ʼд 40
			//int total = 25; // ȡ��λ(������(12��)+��ˮ��(3��)+10λ���);
			//$$=======Modified  2012/02/22��ʱȥ����� START============$$//
			int total=PatTool.getInstance().getMrNoLength() + 3;			
			//$$=======Modified  2012/02/22��ʱȥ����� END============$$//
			
			// ��ӽ�
			byte[] data = new byte[total];
			int error = readCard(commPort, start, total, data);
		//	System.out.println("===error==="+error);
			if (error != 0) {
				result.setErr(-1, this.getErrorMsg(error));
				return result;
			}
			//System.out.println("=====data========="+data);
			//System.out.println("data0"+data[2]);
			if(data[0]==-1){
				//System.out.println("=====���ǿտ�======");
				result.setErr(-1, "�˿�Ƭ�ǿտ�");
				return result;
			}
			//������Ƭ��������;	
			String strData = byte2Str(data);
			//System.out.println("===strData===="+strData);
		
			String strMrNo=strData.substring(0, PatTool.getInstance().getMrNoLength()); //======  chenxi
			//System.out.println("===strMrNo===="+strMrNo);	
			result.setData("MR_NO", strMrNo);
			String strSeq=strData.substring(PatTool.getInstance().getMrNoLength(), PatTool.getInstance().getMrNoLength()+3); //========== cehnxi
			result.setData("SEQ", strSeq);
			//System.out.println("===strSeq===="+strSeq);
			//$$====================Modified  2012/02/22��ʱȥ����� START==========================$$//
			//���֣�ԭ̩�Ŀ��޴����ϵͳ���벢�����ԭҽ�ƿ�;
			/**if(data[15]==-1){
				result.setData("CURRENT_BALANCE", "0.00"); 
			
			//�·��Ŀ��н�����
			}else{
				String strBalance=strData.substring(15, 25);
				//int lastPost=strBalance.indexOf("-");
				//strBalance=strBalance.substring(0,lastPost);
				strBalance=covertPrice(strBalance);
				result.setData("CURRENT_BALANCE", strBalance); 
			}**/
			//$$===================Modified  2012/02/22��ʱȥ����� END============================$$//
			//System.out.println("=====result========"+result);
			//result.addData("returnData", strData);

		} catch (Exception e) {
			e.printStackTrace();
			result.setErr(-1, "����ʧ��");

		}

		return result;
	}


	/**
	 * дҽ�ƿ�
	 * @param commPort  com�ں�
	 * @param cardNo    ҽ�ƿ���
	 * @param seq       ���
	 * @param price     ���(�ݲ�ʹ��)
	 * @return
	 */
	public TParm writeEKT(String commPort, String cardNo, String seq,String price) {
		TParm result = new TParm();
		//$$====================Modified  2012/02/22��ʱȥ����� START==========================$$//
		//String inputData=cardNo+seq+doFormatedPrice(price);
		//$$====================Modified  2012/02/22��ʱȥ����� START==========================$$//
		String inputData=cardNo+seq;
		try {
			int Start = 40; // �ӵڼ�λ��ʼд
			//$$====================Modified  2012/02/22��ʱȥ����� START==========================$$//
			//int Total = 25; // �ܹ�д��λ(������(12��)+��ˮ��(3��))
			//$$====================Modified  2012/02/22��ʱȥ����� START==========================$$//
			int Total = PatTool.getInstance().getMrNoLength() +3;
			byte[] Data = inputData.getBytes(); // д��Data
			//System.out.println("==Data=="+Data.length);
			
			String sPin = "FFFF";
			byte[] Pin = sPin.getBytes();
			//System.out.println("===========pin===="+Pin.length);
			//$$====================Modified  2012/02/22��ʱȥ����� START==========================$$//
			/**if (Data.length != 25) {
				result.setErr(-1, "�����������ӦΪ25");
				return result;
			}**/
			//$$====================Modified  2012/02/22��ʱȥ����� START==========================$$//
			if (Data.length != Total) {
				result.setErr(-1, "�����������ӦΪ15");
				return result;
			}
			
			// д����
			int error = writeCard(commPort, Start, Total, Pin, Data);
			if (error != 0) {
				result.setErr(-1, this.getErrorMsg(error));
				return result;
			}

		} catch (Exception e) {

			result.setErr(-1, "д��ʧ��");
		}
		return result;
	}
	/**
	 * @deprecated
	 * д���ʧ��.
	 * @param commPort
	 * @param balance   100---------(�ܹ�10λ)���㲹-
	 * @return
	 */
	public TParm writeBalance(String commPort, String balance){
		TParm result = new TParm();
		try {
			int Start = 55; // �ӵڼ�λ��ʼд
			int Total = 10; // �ܹ�д��λ(������(12��)+��ˮ��(3��))
			balance=doFormatedPrice(balance);
					
			byte[] Data = balance.getBytes(); // д��Data
			//System.out.println("==Data=="+Data.length);
			
			String sPin = "FFFF";
			byte[] Pin = sPin.getBytes();
			
			if (Data.length != 10) {
				result.setErr(-1, "�����������ӦΪ10λ.");
				return result;
			}
			// д����
			int error = writeCard(commPort, Start, Total, Pin, Data);
			if (error != 0) {
				result.setErr(-1, this.getErrorMsg(error));
				return result;
			}

		} catch (Exception e) {

			result.setErr(-1, "д���ʧ��");
		}
		return result;
		
	}
	/**
	 * @deprecated 
	 * ��ʽ����ҽ�ƿ��洢��ʽ10λ������10λǰ��0
	 * @param price  0.75,100.35  0000000.75  0000100.35
	 * @return
	 */
	public String doFormatedPrice(String price){
		if(price==null||price.equals("")){
			return "0.00";
		}
		String strCardPrice="";
		//price�������뵽2λ
		BigDecimal  dPrice=new BigDecimal(price).setScale(2, BigDecimal.ROUND_HALF_UP);
		String strPrice=dPrice.toString();
		//System.out.println("====strPrice====="+strPrice);	
		//����10λǰ��0
		if(strPrice!=null&&strPrice.length()<10){
			int total=10-strPrice.length();
			for(int i=0;i<total;i++){
				strCardPrice+="0";
			}
			strCardPrice+=strPrice;
		}
		//System.out.println("====strCardPrice====="+strCardPrice);	
		return strCardPrice;
	}
	/**
	 * @deprecated 
	 * ������ת�ɽ��;
	 * @param data  0000000.75|0000100.35
	 * @return
	 */
	public String covertPrice(String data){
		if(data==null||data.equals("")){
			return "0.00";
		}
		String strPrice="0.00";
		int pos=0;
		char c;
		for(int i=0;i<data.length();i++){
			//���ֲ�����0���
			c=data.charAt(i);
			if(c!='0'){
				//System.out.println("c==="+c);
				//System.out.println("i==="+i);
				pos=i;
				break;
				//strPrice=strPrice.substring(i);
				//System.out.println("======strPrice1======"+strPrice);		
				//return strPrice;
			//==0���
			}else{
				//��һ��Ϊ.
				c=data.charAt(i+1);
				if(c=='.'){					
					pos=i;
					break;

				}
				
			}
			
		}
		strPrice=data.substring(pos);
		//System.out.println("======strPrice3======"+strPrice);		
		return strPrice;
		
	}
	

	// ������ ���ձ�
	private String getErrorMsg(int error) {
		
		//System.out.println("=====error======="+error);
		String msg = "";
		if (h.containsKey(new Integer(error)))
			msg = (String) h.get(new Integer(error));
		else
			msg = "�������";
		return msg;
	}

	// �� byteת�ɷ���
	private  String byte2Str(byte[] Data) {
		String Total_Data = "";
		for (int i = 0; i < Data.length; i++) {
			Character CWord = new Character((char) Data[i]);
			Total_Data = Total_Data + CWord.toString();
		}
		// System.out.println("����Data===>"+Total_Data);
		return Total_Data;
	}

	/**
	 * ������Ϣ;
	 */
	static void setErrorMessage() {
		h.put(new Integer(1), "����COM��ʧ��"); // ����COM��ʧ��!!
		h.put(new Integer(2), "�ر�COM��ʧ��"); // �ر�COM��ʧ��!!
		h.put(new Integer(3), "�����PIN��"); // �����PIN��!!
		h.put(new Integer(65535), "ͨѶ����"); // 0xffff
		h.put(new Integer(25088), "�����뿨Ƭ"); // 0x6200
		h.put(new Integer(25089), "�����뿨Ƭ��Ƭδװ�ú�"); // 0x6201
		h.put(new Integer(37119), "�����뿨Ƭ��Ƭδװ�ú�"); // 0x90FF
		h.put(new Integer(37118), "�����뿨Ƭ��Ƭδװ�ú�"); // 0x90FE
		h.put(new Integer(25217), "��ȡ����"); // 0x6281
		h.put(new Integer(25218), "�ļ�����"); // 0x6282
		h.put(new Integer(25344), "�����PIN��"); // 0x6300
		h.put(new Integer(25537), "�����PIN��"); // 0x63c1
		h.put(new Integer(25538), "�����PIN��"); // 0x63c2
		h.put(new Integer(25544), "�����PIN��");
		h.put(new Integer(25600), "��λ���ɹ�"); // 0x6400
		h.put(new Integer(25985), "д��ʧ��"); // 0x6581
		h.put(new Integer(26368), "����������"); // 0x6700
		h.put(new Integer(26880), "�����״̬"); // 0x6900
		h.put(new Integer(27009), "������ļ����"); // 0x6981
		h.put(new Integer(27010), "�ļ�δѡ��"); // 0x6982
		h.put(new Integer(27011), "��������"); // 0x6983
		h.put(new Integer(27013), "�ļ��Ѵ���"); // 0x6985
		h.put(new Integer(27136), "�����P1/P2"); // 0x6A00
		h.put(new Integer(27264), "����Ĳ���"); // 0x6A80
		h.put(new Integer(27265), "�����P2"); // 0x6A81
		h.put(new Integer(27266), "�ļ�û�ҵ�"); // 0x6A82
		h.put(new Integer(27268), "�ļ����㹻������ռ�"); // 0x6A84
		h.put(new Integer(27270), "����Ĳ���"); // 0x6A86
		h.put(new Integer(48384), "�����ƫ����"); // 0xBD00
		h.put(new Integer(27904), "��Ч��ָ�����"); // 0x6D00
		h.put(new Integer(28160), "��Ч��CLA"); // 0x6E00
		h.put(new Integer(28656), "ϵͳ����"); // 0x6FF0ss
	}

	public static void main(String args[]) {
		
		//System.out.println("hex========"+Integer.toHexString(0xFFFF));
		CardRW  cardRW=new CardRW();
		int i=CardRW.init();
		//д��
		//TParm param=cardRW.writeEKT("COM1", "123456789987001100.12----");
		/*TParm param=cardRW.writeEKT("COM1", "123456789987","001","0.75");
		if(param.getErrCode()==-1){
			System.out.println("=========д����������.============");
			System.out.println(param.getErrParm().getErrText());
		}*/
		
		//����
		TParm param1=cardRW.readEKT("COM1");
		if(param1.getErrCode()==-1){
			System.out.println("=========������������.============");
			System.out.println(param1.getErrParm().getErrText());
		}
		
		//ֻд���
		//cardRW.writeBalance("COM1", "9999.35---");
		CardRW.free();
		//88  3A  B3  22  78  03  AE  26  78  03  AE  26  78  03  AE  
		//2CB4D88  3A  B3  22  78  03  AE  26  78  03  AE  26  78  03  AE 
		//2CB4D88  3A  B3  22  78  03  AE  26  78  03  AE  26  78  03  AE
		//byte[] Data={2CB4D88,3A,B3,22,78,03,AE,26,  78  03  AE  26  78  03  AE};
		
		//03201629   30  30  30  30  30  30  32  34  35  31  36  30  30  30  31
		//03237006   30  30  30  30  30  30  31  36  39  36  38  39  30  30  31
		//System.out.println(byte2Str(Data));
		
		//byte[] b={0x30,0x30,0x30,0x30,0x30,0x30,0x31,0x36,0x39,0x36,0x38,0x39,0x30,0x30,0x31};
		//System.out.println("byte2Str===="+byte2Str(b));//byte2Str====000000169689001 //169689
		//byte[] b={0x0,0x12,0x0,0x0,0x0};
		//System.out.println("byte2Str===="+byte2Str(b));
		//0.75,100.35  0000000.75  0000100.35
		//ת�ɴ��뿨�ڵ���������
		//cardRW.doFormatedPrice("0.75");
		//��תǮ
		//cardRW.covertPrice("0000000.75");
		
	}

}
