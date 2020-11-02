package com.javahis.device;

import com.silionmodule.Gen2;
import com.silionmodule.ParamNames;
import com.silionmodule.Reader;
import com.silionmodule.ReaderException;
import com.silionmodule.TagReadData;
import com.silionmodule.Gen2.MemBankE;
import com.silionmodule.ReaderType.AntTypeE;

/**
 * MT100 USB RFID ����
 * 
 * @author lix@bluecore.com.cn
 * 
 */
public class ModuleDriver {

	public static boolean isDebug = true;

	/**
	 * ���12λ
	 * 
	 * @param portNo
	 *            �˿ں�
	 * @param antTypeE
	 *            ��������
	 * @param no
	 *            EPC���洢����
	 * @throws Exception
	 */
	public static void write(String portNo, AntTypeE antTypeE, String no)
			throws Exception {
		if (no == null) {
			throw new Exception("EPC��Ų���Ϊ��!");
		} else if (no.length() > 12) {
			throw new Exception("EPC��ų��ȴ���12λ!");
		}

		Reader rd = null;
		// 1.
		try {
			rd = Reader.Create(portNo, antTypeE);
			// �󲿷�ֻ֧�ְ���д��
			rd.paramSet(ParamNames.Reader_Gen2_WriteMode,
					Gen2.WriteModeE.BLOCK_ONLY);

		} catch (ReaderException e) {
			if (isDebug) {
				e.printStackTrace();
			}
			throw new Exception(e.GetMessage());

		}
		// 2.
		// дEPC��ǩ
		// �����16��������
		String towrite = Uitltool.encode(no);// 1111FFBBFFCC003620404044
		if (isDebug) {
			System.out.println("====towrite===" + towrite);
			System.out.println("=======hexstr_short16======="
					+ Uitltool.hexstr_short16(towrite));
		}

		try {
			rd.WriteTagMemWords(null, MemBankE.EPC, 2, Uitltool.hexstr_short16(towrite));

		} catch (ReaderException e) {
			if (isDebug) {
				e.printStackTrace();
			}
			throw new Exception(e.GetMessage());
		}

		System.out.println("========д�ɹ�===========");

		rd.DisConnect();

	}

	/**
	 * ��EPC������
	 * 
	 * @param portNo   ˳���
	 * @param antTypeE ����������
	 * @param block
	 * @param isHex �Ƿ���16����ʾ
	 * @return
	 * @throws Exception
	 */
	public static String read(String portNo, AntTypeE antTypeE, int block,
			boolean isHex) throws Exception {
		Reader rd = null;
		// 1.
		try {
			rd = Reader.Create(portNo, antTypeE);
		} catch (ReaderException e) {
			if (isDebug) {
				e.printStackTrace();
			}
			throw new Exception(e.GetMessage());
		}

		short[] data;
		String rtnData = null;
		String dataHex = null;
		try {
			data = rd.ReadTagMemWords(null, MemBankE.EPC, 2, block);
			dataHex = Uitltool.shorts_HexString(data);
			if (isDebug) {
				System.out.println("-------dataHex-------" + dataHex);
			}

		} catch (ReaderException e) {
			if (isDebug) {
				e.printStackTrace();
			}
			throw new Exception(e.GetMessage());
		}

		if (isHex) {
			rtnData = dataHex;
		} else {
			rtnData = Uitltool.decode(dataHex);
		}
		rd.DisConnect();

		return rtnData;

	}

	/**
	 * �̵����(USB �ӿ��ݲ�ʵ��)
	 * 
	 * @param milliseconds
	 * @return
	 */
	public static TagReadData[] readByMil(int milliseconds) {

		return null;
	}

	/**
	 * ��Ԫ����
	 * 
	 * @param args
	 */
	public static void main(String args[]) {

		/*
		 * String towrite = encode("A20121105001");// 1111FFBBFFCC003620404044
		 * System.out.println("====towrite==="+towrite); try {
		 * System.out.println
		 * ("=======hexstr_short16======="+hexstr_short16(towrite)); } catch
		 * (Exception e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */

		// 1.������
		try {
			String data = ModuleDriver.read("COM5", AntTypeE.ONE_ANT, 6, false);
			System.out.println(data);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			e.getMessage();
		}

		// 2.д����
		/*
		 * try { write("COM5", AntTypeE.ONE_ANT, "B21211050003"); } catch
		 * (Exception e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); e.getMessage(); }
		 */

		// ����Ϊʵ����룬�ݲ�ʹ��
		// Reader rd = null;
		// 1.������д������
		/*
		 * try { rd=Reader.Create("COM5", AntTypeE.ONE_ANT);
		 * 
		 * } catch (ReaderException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */
		// .Uitltool.hexstr_short16(towrite)
		// �ַ���->ת��hexstr;
		// hexstr->�ַ���;

		// д����дEPC��ǩ
		/*
		 * String towrite="1111cCCCC";//1111FFBBFFCC003620404044 try {
		 * rd.WriteTagMemWords(null, MemBankE.EPC, 2, hexstr_short16(towrite));
		 * } catch (ReaderException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } catch (Exception e) { // TODO Auto-generated
		 * catch block e.printStackTrace(); }
		 */

		// String s=Integer.toHexString(20121101001);
		// System.out.println("===s===="+s);
		// ʮ����
		// int i=Integer.valueOf(s, 16);
		// System.out.println("==========="+i);

		// String s = Long.toHexString(201211010001l);
		/*
		 * Long i = Long.valueOf("E2001062580300361870FFEF", 16);
		 * System.out.println("===========" + i);
		 */

		/*
		 * String decodeStr = decode("E2001062580300361870FFEF"); //
		 * 323031323131303230303032 System.out.println("-------decodeStr-------"
		 * + decodeStr);
		 */

		// ���12λ
		/*
		 * String enstr = encode("201211020002");
		 * System.out.println("-------enstr-------" + enstr);
		 * 
		 * // ���12λ String decodeStr1 = decode("323031323131303230303032");
		 */

		/*
		 * try { short[] ss=hexstr_short16("323031323131303230303032"); for(int
		 * i=0;i<ss.length;i++){ System.out.println("======ss======="+ss[i]); }
		 * 
		 * String ss2=shorts_HexString(ss);
		 * System.out.println("-------ss2-------" + ss2); String decodeStr1 =
		 * decode(ss2); System.out.println("-------decodeStr1-------" +
		 * decodeStr1);
		 * 
		 * } catch (Exception e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */

		// System.out.println("-------decodeStr-------" + decodeStr1);

		/*
		 * String s1=toStringHex("BBBB"); System.out.println("===ssss========" +
		 * s1);
		 */

		// StringUtil.
		/*
		 * try { byte[] b=ModuleDriver.hexstr_Byte("1e240");
		 * 
		 * //ʮ��->ʮ���� //Integer.toString(i) //String s1=new String(b);
		 * //System.out.println("S1========"+s1);
		 * 
		 * } catch (Exception e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */

		// System.out.println("hex========"+Integer.toHexString(E2001062580300361870FFEF));
		// String.valueOf();

		// ������;
		/*
		 * short[] data; try { data = rd.ReadTagMemWords(null, MemBankE.EPC, 2,
		 * 3); System.out.println("read data:"+shorts_HexString(data)); } catch
		 * (ReaderException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */

		// rd.DisConnect();

	}

}
