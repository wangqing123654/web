package jdo.device;

import com.dongyang.util.StringTool;
import com.dongyang.config.TRegistry;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import jdo.sys.SystemTool;

/**
 *
 * <p>Title: Windowϵͳ�ײ�ӿ�</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class WindowsDriver {
    static {
        System.loadLibrary("WindowsDriver"); //����dll
    }
    /**
     * �õ�������
     * @return String
     */
    public native static String getComputer();
    /**
     * �õ������˿����
     * @return String 2|7|5 ����|�˿�1|�˿�2
     */
    private native static String getMacIndex();
    /**
     * �õ�������ַ
     * @return String 2|00-21-00-20-E1-42|00-1D-72-58-7C-7A ����|��ַ1|��ַ2
     */
    private native static String getMac();
    /**
     * �õ���ǰʱ��
     * @return String 2010|9|28|11|42|12 ��|��|��|ʱ|��|��
     */
    private native static String getDate();
    /**
     * �õ��������
     * @return String
     * 00210020E142001D72587C7A
     * 00000000000000210020E142
     */
    private static String getMacNo()
    {
        String s = getMac();
        if(s.startsWith("ERR"))
            return StringTool.fill0("",48);
        StringBuffer sb = new StringBuffer();
        String ss[] = StringTool.parseLine(s,"|");
        int count = StringTool.getInt(ss[0]);
        for(int i = 0;i < count;i++)
        {
            String s1 = ss[i + 1];
            String s2[] = StringTool.parseLine(s1,"-");
            for(int j = 0;j < s2.length;j++)
                sb.append(s2[j]);
        }
        return StringTool.fill0(sb.toString(),48);
    }
    private static String[] getMacList()
    {
        String s = getMac();
        if(s.startsWith("ERR"))
            return null;
        String ss[] = StringTool.parseLine(s,"|");
        int count = StringTool.getInt(ss[0]);
        String values[] = new String[count];
        for(int i = 0;i < count;i++)
        {
            String s1 = ss[i + 1];
            StringBuffer sb = new StringBuffer();
            String s2[] = StringTool.parseLine(s1,"-");
            for(int j = 0;j < s2.length;j++)
                sb.append(s2[j]);
            values[i] = sb.toString();
        }
        return values;
    }
    private static String P1(String s,int l)
    {
        StringBuffer sb = new StringBuffer();
        for(int i = 0;i < s.length();i++)
        {
            int x = s.charAt(i);
            sb.append(Integer.toHexString(x));
        }
        return StringTool.fill0(sb.toString().toUpperCase(),l);
    }
    private static String P3(String s)
    {
        int x = 0;
        byte b[] = s.getBytes();
        for(int i = 0;i < b.length;i++)
        {
            if(b[i] >= 0)
                x += b[i];
            else
                x += b[i] & 255;
        }
        return "" + x;
    }
    private static String P2(String s)
    {
        StringBuffer sb = new StringBuffer();
        for(int i = 0;i < s.length() - 1;i+=2)
        {
            String s1 = s.substring(i,i + 2);
            char x = (char)(int)Integer.valueOf(s1,16);
            if(x == 0)
                continue;
            sb.append(x);
        }
        return sb.toString();
    }
    private static String fill(String s,int i)
    {
        String computer = s;
        if(computer.length() > i)
            return computer.substring(0,i);
        return StringTool.fill0(computer,i);
    }
    public static String getOutNo(String coname)
    {
        String computer = P1(getComputer(),20);
        String mac = getMacNo();
        String s = computer + mac + fill(P3(coname),8);
        return II(I(s));
    }
    private static boolean checkConame(String coname,String code)
    {
        return fill(P3(coname),8).equals(code);
    }
    private static String[] getInNo(String no)
    {
        if(no == null || no.length() != 92)
            return null;
        String s = I(OO(no));
        String s1 = P2(s.substring(0,20));
        String s2 = s.substring(20,32);
        String s3 = s.substring(32,44);
        String s4 = s.substring(44,56);
        String s5 = s.substring(56,68);
        String s6 = s.substring(68,76);
        String s7 = s.substring(76,84);
        String s8 = s.substring(84);
        return new String[]{s1,s2,s3,s4,s5,s6,s7,s8};
    }
    private static String I(String s)
    {
        StringBuffer sb = new StringBuffer();
        for(int i = 0;i < s.length();i++)
        {
            char c = s.charAt(i);
            if((i % 2) == 1)
            {
                sb.append(c);
                continue;
            }
            if(c >= '0' && c <= '9')
            {
                sb.append((char) (9 - c + '0' + '0'));
                continue;
            }
            if(c >= 'A' && c <= 'Z')
            {
                sb.append((char) (25 - c + 'A' + 'A'));
                continue;
            }
            sb.append(c);
        }
        return sb.toString();
    }
    private static String II(String s)
    {
        int l = (int)(s.length() / 2.0 + 0.99);
        String s1 = s.substring(0,l);
        String s2 = s.substring(l);
        StringBuffer sb = new StringBuffer();
        for(int i = 0;i < s1.length();i++)
        {
            sb.append(s1.charAt(i));
            if(s2.length() > i)
                sb.append(s2.charAt(s2.length() - i - 1));
        }
        return sb.toString();
    }
    private static String OO(String s)
    {
        StringBuffer s1 = new StringBuffer();
        StringBuffer s2 = new StringBuffer();
        for(int i = 0;i < s.length();i++)
        {
            char c = s.charAt(i);
            if ( (i % 2) == 1)
                s2.insert(0,c);
            else
                s1.append(c);
        }
        return s1.toString() + s2.toString();
    }
    /**
     * �õ�ע�����
     * @return String
     */
    public static String getKey()
    {
        return TRegistry.get("HKEY_CURRENT_USER\\Software\\JavaHis\\1.0\\Key");
    }
    /**
     * д��ע�����
     * @param key String
     */
    public static void setKey(String key)
    {
        TRegistry.set("HKEY_CURRENT_USER\\Software\\JavaHis\\1.0\\Key",key);
    }
    /**
     * �õ���˾����
     * @return String
     */
    public static String getConame()
    {
        return TRegistry.get("HKEY_CURRENT_USER\\Software\\JavaHis\\1.0\\Coname");
    }
    /**
     * ���ù�˾����
     * @param coname String
     */
    public static void setConame(String coname)
    {
        TRegistry.set("HKEY_CURRENT_USER\\Software\\JavaHis\\1.0\\Coname",coname);
    }
    public static String checkKey()
    {
        return check();
    }
    /**
     * ����ע��
     * @return String
     */
    private static String check()
    {
        String coname = getConame();
        if(coname == null || coname.length() == 0)
            return "û��ע��";
        String key = getKey();
        if(key == null || key.length() == 0)
            return "û��ע��";
        String s1[] = getInNo(key);
        if(s1 == null)
            return "û��ע��";
        String computer = getComputer();
        if(computer == null || s1[0] == null || !computer.endsWith(s1[0]))
            return "�����������ע�᲻����";
        String mac[] = getMacList();
        if(mac != null)
            for(int i = 0;i < mac.length;i++)
                if(!s1[1].equals(mac[i]) && !s1[2].equals(mac[i]) && !s1[3].equals(mac[i]) && !s1[4].equals(mac[i]))
                    return "������ַ��ע�᲻����";
        String cono = fill(P3(coname),8);
        if(!cono.equals(s1[5]))
            return "��˾������ע�᲻����";
        //System.out.println("=========s1[6]======="+s1[6]);
        //
        //System.out.println("=========s1[7]======="+s1[7]);
        //
		// ȡ����������� �ж���
		// s1[7]-��������ǰʱ��<=7�죬����ʾ������ϵͳ���ڡ�+s1[7]+��ע�ᵽ�ڣ��뼱ʱ��������˾��ϵ��\n�ۺ�������䣺service@bluecore.com.cn��
		// ȡϵͳ�¼�
		Timestamp date = SystemTool.getInstance().getDate();
		String today = StringTool.getString(date, "yyyyMMdd");
		//
		//System.out.println("-----ע�ᵽ��ʱ��------"+s1[7]);
		//System.out.println("-----����ʱ�䣺------"+today);
		long daynum = getDays(s1[7], today);
		//
		//
		//System.out.println("-----daynum������------"+daynum);
		//
		if (daynum>0&&daynum <= 7) {
			return "�����Ŀͻ�������ϵͳ����"+s1[7]+"ע�Ὣ���ڣ��뼱ʱ����Ϣ������ϵ��\n��ע����뷢��������˾�ۺ�������䣺service@bluecore.com.cn";
		}
        //
        if(!checkDate(s1[6],s1[7]))
            return "����ע��ʹ������";
        return "OK";
    }
    public static boolean checkDate(String s1,String s2)
    {
        Date d = new Date();
        String sd = StringTool.getString(d,"yyyyMMdd");
        if(sd.compareTo(s1) < 0)
            return false;
        if(sd.compareTo(s2) > 0)
            return false;
        return true;
    }
    
    
	/**
	 * ����ʱ��֮�������
	 *
	 * @param date1
	 * @param date2
	 * @return
	 */
	private static long getDays(String date1, String date2) {
		if (date1 == null || date1.equals(""))
			return 0;
		if (date2 == null || date2.equals(""))
			return 0;
		// ת��Ϊ��׼ʱ��
		SimpleDateFormat myFormatter = new SimpleDateFormat("yyyyMMdd");
		java.util.Date date = null;
		java.util.Date mydate = null;
		try {
			date = myFormatter.parse(date1);
			mydate = myFormatter.parse(date2);
		} catch (Exception e) {
		}
		long day = (date.getTime() - mydate.getTime()) / (24 * 60 * 60 * 1000);
		return day;
	}
    
    public static void main(String args[])
    {
        //String s = II("123456789");
        //System.out.println(s);
        //System.out.println(OO(s));
        //System.out.println(getMacNo());
        /*String s = getOutNo("�����к�������޹�˾");
        System.out.println(s);
        s = "99059105900990099205910V9007900921A730A9900990099009900990099009900990099009";
        System.out.println(s);*/
        //String s1[] = getInNo("9109910990029908910991099002990829A531A5900990099205910V900790099107900990099009900990099009");
        //for(int i = 0;i < s1.length;i++)
        //    System.out.println(s1[i]);
        WindowsDriver.setConame("�����к�������޹�˾");
        WindowsDriver.setKey("9409910891089007940991089008900729A531A5900990099205910V900790099107900990099009900990099009");
        System.out.println(check());
        //System.out.println(checkConame("�����к�������޹�˾",s1[3]));
        //System.out.println(checkDate("20101028","20101029"));
    }
    //9006900890089007980790089008900729A531A5900990099205910V900790099107
}


//99059105900990099A029C02980492022DA830A99205711V9007
//9006900890089007980790089008900729A531A5900970199A027C02V81452229D08
