package com.javahis.device;

import org.jawin.COMException;
import org.jawin.DispatchPtr;
import org.jawin.win32.Ole32;
/**
 * RFID�����豸�ӿ���
 * @author lix@bluecore.com.cn
 *
 */
public class RFIDMidUtils {
	public static DispatchPtr app = null;

	static {

		try {
			app = new DispatchPtr("Com.Sunray.SunrayRfidMidClass"); // ����dll
		} catch (Throwable ex) {

		}
	}

	/**
	 * ����
	 */
	public static void connect() {
		// ����
		try {
			app.invoke("Connect");
		} catch (COMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * �ͷ�����
	 */
	public static void disconnect() {

		try {
			app.invoke("Disconnect");
		} catch (COMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * д��ǩ
	 */
	public static void writeTag(String oldValue, String newValue) {

		try {
			// Ole32.CoInitialize();

			// д��
			app.invoke("WriteEPC", oldValue, newValue);
			// �ͷ�����
			// app.invoke("Disconnect");
			// Ole32.CoUninitialize();
		} catch (COMException e) {
			e.printStackTrace();
		} finally {

		}
	}

	/**
	 * ����ǩ
	 * 
	 * @return
	 */
	public static String[] readTags() {
		Object o;
		try {
			// app.invoke("Connect");
			o = app.invoke("GetTagEPCList");
			String[] array = (String[]) o;
			// app.invoke("Disconnect");
			return array;
		} catch (COMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private static void test() {
		System.out.println("--------------come in------------------");
		try {
			// Ole32.CoInitialize();
			app.invoke("Connect");
			app.invoke("WriteEPC", "213126820080430320001001",
					"213126820080430320001002");
			System.out.println("-----lxtest end ----");

			Object o = app.invoke("GetTagEPCList");
			String[] array = (String[]) o;
			for (String s : array) {
				System.out.println("-----lxtest----" + s);
			}

			app.invoke("Disconnect");
			// Ole32.CoUninitialize();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("--------------come end------------------");

	}

}
