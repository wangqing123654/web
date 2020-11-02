package com.javahis.ui.inw;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TCheckBox;
import com.dongyang.util.StringTool;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:��ʿվѡ��Ƶ��ʱ�������
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2013 bluecore
 * </p>
 * 
 * <p>
 * Company: bluecore
 * </p>
 * 
 * @author shibl
 * @version 1.0
 */
public class InwPhaFreqControl extends TControl {
	// ʱ��TAG��
	private final static String TIME_TAG = "0;1;2;3;4;5;6;7;8;9;10;11;12;13;14;15;16;17;18;19;20;21;22;23;24;25;26;27;28;29;30;31;32;33;34;35;36;37;38;39;40;41;42;43;44;45;46;47";
	String[] tags;
	TParm parm = new TParm();
	Map map = new HashMap();
	Map TimeMap = new HashMap();
	String time = "";

	public InwPhaFreqControl() {
	}

	public void onInit() {
		super.onInit();
		time = (String) this.getParameter();
		tags = StringTool.parseLine(TIME_TAG, ";");
		SetTimeMap();
		if (time.equals("")) {
			this.onSet();
		} else {
			String[] timeLine = time.split(";");
			for (int i = 0; i < timeLine.length; i++) {
				String time = timeLine[i];
				if (TimeMap.get(time) != null) {
					((TCheckBox) this.getComponent((String) TimeMap.get(time)))
							.setSelected(true);
					map.put(TimeMap.get(time), time.replace(":", ""));
				}
			}
		}
	}

	private void SetTimeMap() {
		for (int i = 0; i < 48; i++) {
			String cnt = "";
			// �����ж�
			if ((i + 1) % 2 != 0) {
				cnt = String.valueOf((i + 1) % 2 == 0 ? (i + 1) / 2 - 1
						: (i + 1) / 2);
				if (cnt.length() == 0)
					cnt = "00";
				if (cnt.length() == 1)
					cnt = "0" + cnt;
				cnt += ":00";
			} else {
				cnt = String.valueOf((i + 1) % 2 == 0 ? (i + 1) / 2 - 1
						: (i + 1) / 2);
				if (cnt.length() == 0)
					cnt = "00";
				if (cnt.length() == 1)
					cnt = "0" + cnt;
				cnt += ":30";
			}
			TimeMap.put(cnt, i + "");
		}
	}

	// ȷ��
	public void onOK() {
		if (parm != null) {
			// ���parm
			while (parm.getCount("TIME") > 0) {
				parm.removeRow(0);
			}
		}
		int count = 0;
		for (int i = 0; i < tags.length; i++) {
			if (map.get(tags[i]) != null) {
				String time = (String) map.get(tags[i]);
				parm.addData("TIME", time);
				count++;
			}
		}
		parm.setCount(count);
		this.setReturnValue(parm);
		onCANCLE();
	}

	/**
	 * ʱ���CHECKBOX����¼����ı�ʱ���TABLE��ֵ��FREQ_TIMES��ֵ
	 * 
	 * @param text
	 *            ʱ���CHECKBOX��ʾ����
	 * @param tag
	 *            ʱ���CHECKBOX��TAG
	 */
	public void onCheckTime(String text, String tag) {
		if (tags == null || tags.length != 48) {
			this.setValue(tag, "N");
			this.messageBox_("����ȡ������");
			return;
		}
		// ����FREQ_TIMES
		String temp;
		temp = this.getValueString(tag);
		if (temp.equals("Y")) {
			map.put(tag, text);
		} else {
			map.remove(tag);
		}
	}

	// �ر�
	public boolean onCANCLE() {
		this.closeWindow();
		return false;
	}

	// ����
	public void onSet() {
		String[] tags = StringTool.parseLine(TIME_TAG, ";");
		for (int i = 0; i < tags.length; i++) {
			((TCheckBox) this.getComponent(tags[i])).setSelected(false);
		}
		map.clear();
		if (parm != null) {
			// ���parm
			while (parm.getCount("TIME") > 0) {
				parm.removeRow(0);
			}
		}
	}

	public static void main(String[] args) {
		String[] tags = StringTool.parseLine(TIME_TAG, ";");
		for (int i = 0; i < tags.length; i++) {
			System.out.println(i + " " + tags[i]);
		}
	}
}
