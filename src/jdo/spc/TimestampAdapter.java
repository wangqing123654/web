package jdo.spc;

import java.sql.Timestamp;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class TimestampAdapter extends XmlAdapter<String, Timestamp> {
	/**
	 * <������ϸ����>
	 * 
	 * @param time
	 * @return
	 * @throws Exception
	 * @see [�ࡢ��#��������#��Ա]
	 */
	public String marshal(Timestamp time) throws Exception {
		return DateUtil.timestamp2Str(time);
	}

	/**
	 * <������ϸ����>
	 * 
	 * @param v
	 * @throws Exception
	 * @see [�ࡢ��#��������#��Ա]
	 */
	public Timestamp unmarshal(String str) throws Exception {
		return DateUtil.str2Timestamp(str);
	}
}
