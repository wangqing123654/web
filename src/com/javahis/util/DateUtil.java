package com.javahis.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.lang.time.DurationFormatUtils;

import com.dongyang.util.StringTool;

public class DateUtil {
	//
	private static final boolean isDebug=false;
	// ����ȫ�ֿ��� ��һ�ܣ����ܣ���һ�ܵ������仯
	private int weeks = 0;
	private int MaxDate;// һ���������
	private int MaxYear;// һ���������

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DateUtil tt = new DateUtil();
//		System.out.println("��ȡ��������:" + tt.getNowTime("yyyy/MM/dd"));
//		System.out.println("��ȡ����һ����:" + tt.getMondayOFWeek());
//		System.out.println("��ȡ�����յ�����~:" + tt.getCurrentWeekday());
//		System.out.println("��ȡ����һ����:" + tt.getPreviousWeekday());
//		System.out.println("��ȡ����������:" + tt.getPreviousWeekSunday());
//		System.out.println("��ȡ����һ����:" + tt.getNextMonday());
//		System.out.println("��ȡ����������:" + tt.getNextSunday());
//		System.out.println("�����Ӧ�ܵ�����������:" + tt.getNowTime("yyyy/MM/dd"));
//		System.out.println("��ȡ���µ�һ������:" + tt.getFirstDayOfMonth());
//		System.out.println("��ȡ�������һ������:" + tt.getDefaultDay());
//		System.out.println("��ȡ���µ�һ������:" + tt.getPreviousMonthFirst());
//		System.out.println("��ȡ�������һ�������:" + tt.getPreviousMonthEnd());
//		System.out.println("��ȡ���µ�һ������:" + tt.getNextMonthFirst());
//		System.out.println("��ȡ�������һ������:" + tt.getNextMonthEnd());
//		System.out.println("��ȡ����ĵ�һ������:" + tt.getCurrentYearFirst());
//		System.out.println("��ȡ�������һ������:" + tt.getCurrentYearEnd());
//		System.out.println("��ȡȥ��ĵ�һ������:" + tt.getPreviousYearFirst());
//		System.out.println("��ȡȥ������һ������:" + tt.getPreviousYearEnd());
//		System.out.println("��ȡ�����һ������:" + tt.getNextYearFirst());
//		System.out.println("��ȡ�������һ������:" + tt.getNextYearEnd());
//		System.out.println("��ȡ�����ȵ�һ�쵽���һ��:" + tt.getThisSeasonTime(11));
//		System.out.println("��ȡ��������֮��������2008-12-1~2008-9.29:"
//				+ DateUtil.getTwoDay("2008-12-1", "2008-9-29"));
		
		Timestamp in = StringTool.getTimestamp("2014/05/08 22:00:00 12:00:00", "yyyy/MM/dd HH:mm:ss");
		System.out.println("======in=====" + in);
		//------------------------------------------------------------------------
		Timestamp birthdate = StringTool.getTimestamp("2014/05/01 00:00:00 12:00:00", "yyyy/MM/dd HH:mm:ss");

		System.out.println("=======birthdate====" + birthdate);
		
		showAge(birthdate, in);
	}

	/**
	 * �õ��������ڼ�ļ������
	 */
	public static String getTwoDay(String sj1, String sj2) {
		SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy/MM/dd");
		long day = 0;
		try {
			java.util.Date date = myFormatter.parse(sj1);
			java.util.Date mydate = myFormatter.parse(sj2);
			day = (date.getTime() - mydate.getTime()) / (24 * 60 * 60 * 1000);
		} catch (Exception e) {
			return "";
		}
		return day + "";
	}

	/**
	 * ����һ�����ڣ����������ڼ����ַ���
	 *
	 * @param sdate
	 * @return
	 */
	public static String getWeek(String sdate) {
		// ��ת��Ϊʱ��
		Date date = DateUtil.strToDate(sdate);
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		// int hour=c.get(Calendar.DAY_OF_WEEK);
		// hour�д�ľ������ڼ��ˣ��䷶Χ 1~7
		// 1=������ 7=����������������
		return new SimpleDateFormat("EEEE").format(c.getTime());
	}

	/**
	 * ����ʱ���ʽ�ַ���ת��Ϊʱ�� yyyy/MM/dd
	 *
	 * @param strDate
	 * @return
	 */
	public static Date strToDate(String strDate) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
		ParsePosition pos = new ParsePosition(0);
		Date strtodate = formatter.parse(strDate, pos);
		return strtodate;
	}

	/**
	 * ����ʱ��֮�������
	 *
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static long getDays(String date1, String date2) {
		if (date1 == null || date1.equals(""))
			return 0;
		if (date2 == null || date2.equals(""))
			return 0;
		// ת��Ϊ��׼ʱ��
		SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy/MM/dd");
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

	// ���㵱�����һ��,�����ַ���
	public String getDefaultDay() {
		String str = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

		Calendar lastDate = Calendar.getInstance();
		lastDate.set(Calendar.DATE, 1);// ��Ϊ��ǰ�µ�1��
		lastDate.add(Calendar.MONTH, 1);// ��һ���£���Ϊ���µ�1��
		lastDate.add(Calendar.DATE, -1);// ��ȥһ�죬��Ϊ�������һ��

		str = sdf.format(lastDate.getTime());
		return str;
	}

	// ���µ�һ��
	public String getPreviousMonthFirst() {
		String str = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

		Calendar lastDate = Calendar.getInstance();
		lastDate.set(Calendar.DATE, 1);// ��Ϊ��ǰ�µ�1��
		lastDate.add(Calendar.MONTH, -1);// ��һ���£���Ϊ���µ�1��
		// lastDate.add(Calendar.DATE,-1);//��ȥһ�죬��Ϊ�������һ��

		str = sdf.format(lastDate.getTime());
		return str;
	}

	// ��ȡ���µ�һ��
	public static String getFirstDayOfMonth() {
		String str = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

		Calendar lastDate = Calendar.getInstance();
		lastDate.set(Calendar.DATE, 1);// ��Ϊ��ǰ�µ�1��
		str = sdf.format(lastDate.getTime());
		return str;
	}

	// ��ñ��������յ�����
	public String getCurrentWeekday() {
		weeks = 0;
		int mondayPlus = this.getMondayPlus();
		GregorianCalendar currentDate = new GregorianCalendar();
		currentDate.add(GregorianCalendar.DATE, mondayPlus + 6);
		Date monday = currentDate.getTime();

		DateFormat df = DateFormat.getDateInstance();
		String preMonday = df.format(monday);
		return preMonday;
	}

	// ��ȡ����ʱ��
	public static String getNowTime(String dateformat) {
		Date now = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat(dateformat);// ���Է�����޸����ڸ�ʽ
		String hehe = dateFormat.format(now);
		return hehe;
	}

	// ��õ�ǰ�����뱾������������
	private int getMondayPlus() {
		Calendar cd = Calendar.getInstance();
		// ��ý�����һ�ܵĵڼ��죬�������ǵ�һ�죬���ڶ��ǵڶ���......
		int dayOfWeek = cd.get(Calendar.DAY_OF_WEEK) - 1; // ��Ϊ���й����һ��Ϊ��һ�����������1
		if (dayOfWeek == 1) {
			return 0;
		} else {
			return 1 - dayOfWeek;
		}
	}

	// ��ñ���һ������
	public String getMondayOFWeek() {
		weeks = 0;
		int mondayPlus = this.getMondayPlus();
		GregorianCalendar currentDate = new GregorianCalendar();
		currentDate.add(GregorianCalendar.DATE, mondayPlus);
		Date monday = currentDate.getTime();

		DateFormat df = DateFormat.getDateInstance();
		String preMonday = df.format(monday);
		return preMonday;
	}

	// �����Ӧ�ܵ�����������
	public String getSaturday() {
		int mondayPlus = this.getMondayPlus();
		GregorianCalendar currentDate = new GregorianCalendar();
		currentDate.add(GregorianCalendar.DATE, mondayPlus + 7 * weeks + 6);
		Date monday = currentDate.getTime();
		DateFormat df = DateFormat.getDateInstance();
		String preMonday = df.format(monday);
		return preMonday;
	}

	// ������������յ�����
	public String getPreviousWeekSunday() {
		weeks = 0;
		weeks--;
		int mondayPlus = this.getMondayPlus();
		GregorianCalendar currentDate = new GregorianCalendar();
		currentDate.add(GregorianCalendar.DATE, mondayPlus + weeks);
		Date monday = currentDate.getTime();
		DateFormat df = DateFormat.getDateInstance();
		String preMonday = df.format(monday);
		return preMonday;
	}

	// �����������һ������
	public String getPreviousWeekday() {
		weeks--;
		int mondayPlus = this.getMondayPlus();
		GregorianCalendar currentDate = new GregorianCalendar();
		currentDate.add(GregorianCalendar.DATE, mondayPlus + 7 * weeks);
		Date monday = currentDate.getTime();
		DateFormat df = DateFormat.getDateInstance();
		String preMonday = df.format(monday);
		return preMonday;
	}

	// �����������һ������
	public String getNextMonday() {
		weeks++;
		int mondayPlus = this.getMondayPlus();
		GregorianCalendar currentDate = new GregorianCalendar();
		currentDate.add(GregorianCalendar.DATE, mondayPlus + 7);
		Date monday = currentDate.getTime();
		DateFormat df = DateFormat.getDateInstance();
		String preMonday = df.format(monday);
		return preMonday;
	}

	// ������������յ�����
	public String getNextSunday() {

		int mondayPlus = this.getMondayPlus();
		GregorianCalendar currentDate = new GregorianCalendar();
		currentDate.add(GregorianCalendar.DATE, mondayPlus + 7 + 6);
		Date monday = currentDate.getTime();
		DateFormat df = DateFormat.getDateInstance();
		String preMonday = df.format(monday);
		return preMonday;
	}

	private int getMonthPlus() {
		Calendar cd = Calendar.getInstance();
		int monthOfNumber = cd.get(Calendar.DAY_OF_MONTH);
		cd.set(Calendar.DATE, 1);// ����������Ϊ���µ�һ��
		cd.roll(Calendar.DATE, -1);// ���ڻع�һ�죬Ҳ�������һ��
		MaxDate = cd.get(Calendar.DATE);
		if (monthOfNumber == 1) {
			return -MaxDate;
		} else {
			return 1 - monthOfNumber;
		}
	}

	// ����������һ�������
	public String getPreviousMonthEnd() {
		String str = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

		Calendar lastDate = Calendar.getInstance();
		lastDate.add(Calendar.MONTH, -1);// ��һ����
		lastDate.set(Calendar.DATE, 1);// ����������Ϊ���µ�һ��
		lastDate.roll(Calendar.DATE, -1);// ���ڻع�һ�죬Ҳ���Ǳ������һ��
		str = sdf.format(lastDate.getTime());
		return str;
	}

	// ����¸��µ�һ�������
	public String getNextMonthFirst() {
		String str = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

		Calendar lastDate = Calendar.getInstance();
		lastDate.add(Calendar.MONTH, 1);// ��һ����
		lastDate.set(Calendar.DATE, 1);// ����������Ϊ���µ�һ��
		str = sdf.format(lastDate.getTime());
		return str;
	}

	// ����¸������һ�������
	public String getNextMonthEnd() {
		String str = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

		Calendar lastDate = Calendar.getInstance();
		lastDate.add(Calendar.MONTH, 1);// ��һ����
		lastDate.set(Calendar.DATE, 1);// ����������Ϊ���µ�һ��
		lastDate.roll(Calendar.DATE, -1);// ���ڻع�һ�죬Ҳ���Ǳ������һ��
		str = sdf.format(lastDate.getTime());
		return str;
	}

	// ����������һ�������
	public String getNextYearEnd() {
		String str = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

		Calendar lastDate = Calendar.getInstance();
		lastDate.add(Calendar.YEAR, 1);// ��һ����
		lastDate.set(Calendar.DAY_OF_YEAR, 1);
		lastDate.roll(Calendar.DAY_OF_YEAR, -1);
		str = sdf.format(lastDate.getTime());
		return str;
	}

	// ��������һ�������
	public String getNextYearFirst() {
		String str = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

		Calendar lastDate = Calendar.getInstance();
		lastDate.add(Calendar.YEAR, 1);// ��һ����
		lastDate.set(Calendar.DAY_OF_YEAR, 1);
		str = sdf.format(lastDate.getTime());
		return str;

	}

	// ��ñ����ж�����
	private int getMaxYear() {
		Calendar cd = Calendar.getInstance();
		cd.set(Calendar.DAY_OF_YEAR, 1);// ��������Ϊ�����һ��
		cd.roll(Calendar.DAY_OF_YEAR, -1);// �����ڻع�һ�졣
		int MaxYear = cd.get(Calendar.DAY_OF_YEAR);
		return MaxYear;
	}

	private int getYearPlus() {
		Calendar cd = Calendar.getInstance();
		int yearOfNumber = cd.get(Calendar.DAY_OF_YEAR);// ��õ�����һ���еĵڼ���
		cd.set(Calendar.DAY_OF_YEAR, 1);// ��������Ϊ�����һ��
		cd.roll(Calendar.DAY_OF_YEAR, -1);// �����ڻع�һ�졣
		int MaxYear = cd.get(Calendar.DAY_OF_YEAR);
		if (yearOfNumber == 1) {
			return -MaxYear;
		} else {
			return 1 - yearOfNumber;
		}
	}

	// ��ñ����һ�������
	public String getCurrentYearFirst() {
		int yearPlus = this.getYearPlus();
		GregorianCalendar currentDate = new GregorianCalendar();
		currentDate.add(GregorianCalendar.DATE, yearPlus);
		Date yearDay = currentDate.getTime();
		DateFormat df = DateFormat.getDateInstance();
		String preYearDay = df.format(yearDay);
		return preYearDay;
	}

	// ��ñ������һ������� *
	public String getCurrentYearEnd() {
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");// ���Է�����޸����ڸ�ʽ
		String years = dateFormat.format(date);
		return years + "-12-31";
	}

	// ��������һ������� *
	public String getPreviousYearFirst() {
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");// ���Է�����޸����ڸ�ʽ
		String years = dateFormat.format(date);
		int years_value = Integer.parseInt(years);
		years_value--;
		return years_value + "-1-1";
	}

	// ����������һ�������
	public String getPreviousYearEnd() {
		weeks--;
		int yearPlus = this.getYearPlus();
		GregorianCalendar currentDate = new GregorianCalendar();
		currentDate.add(GregorianCalendar.DATE, yearPlus + MaxYear * weeks
				+ (MaxYear - 1));
		Date yearDay = currentDate.getTime();
		DateFormat df = DateFormat.getDateInstance();
		String preYearDay = df.format(yearDay);
		getThisSeasonTime(11);
		return preYearDay;
	}

	// ��ñ�����
	public String getThisSeasonTime(int month) {
		int array[][] = { { 1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 9 }, { 10, 11, 12 } };
		int season = 1;
		if (month >= 1 && month <= 3) {
			season = 1;
		}
		if (month >= 4 && month <= 6) {
			season = 2;
		}
		if (month >= 7 && month <= 9) {
			season = 3;
		}
		if (month >= 10 && month <= 12) {
			season = 4;
		}
		int start_month = array[season - 1][0];
		int end_month = array[season - 1][2];

		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");// ���Է�����޸����ڸ�ʽ
		String years = dateFormat.format(date);
		int years_value = Integer.parseInt(years);

		int start_days = 1;// years+"-"+String.valueOf(start_month)+"-1";//getLastDayOfMonth(years_value,start_month);
		int end_days = getLastDayOfMonth(years_value, end_month);
		String seasonDate = years_value + "-" + start_month + "-" + start_days
				+ ";" + years_value + "-" + end_month + "-" + end_days;
		return seasonDate;

	}

	/**
	 * ��ȡĳ��ĳ�µ����һ��
	 *
	 * @param year
	 *            ��
	 * @param month
	 *            ��
	 * @return ���һ��
	 */
	private int getLastDayOfMonth(int year, int month) {
		if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8
				|| month == 10 || month == 12) {
			return 31;
		}
		if (month == 4 || month == 6 || month == 9 || month == 11) {
			return 30;
		}
		if (month == 2) {
			if (isLeapYear(year)) {
				return 29;
			} else {
				return 28;
			}
		}
		return 0;
	}

	/**
	 * �Ƿ�����
	 *
	 * @param year
	 *            ��
	 * @return
	 */
	public boolean isLeapYear(int year) {
		return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
	}
	
	
	    /**
	    * ���ݲ������պʹ���Ľ������ڣ����㲡�����䲻ͬ����ʽ��ʾ����
	    * @param odo
	    * @return String ������ʾ������
	    */
	   public static String showAge(Timestamp birth, Timestamp sysdate) {
		   //����ʱ�����ϵͳʱ��
		   if(birth.getTime()>sysdate.getTime()){
			  return ""; 
		   }
		   //
	       String strAge = "";
	       String[] res;
	       //res = CountAgeByTimestamp(birth, sysdate);
	       //modified by lx  ���㾫ȷ����
	       String times=DurationFormatUtils.formatPeriod(birth.getTime(), sysdate.getTime(),
			"y-M-d-H-m-s");
	       res=times.split("-");	      
	       //
	       if(isDebug){
	    	   System.out.println("-----age:----"+times);
	           for (String temp : res) {
		           System.out.println("----temp----\n" + temp);
		       }     
	       }
	      /* if (OPDSysParmTool.getInstance().isChild(birth)) {
	           age = res[0] + "��" + res[1] + "��" + res[2] + "��";
	       }
	       else {
	           age = (Integer.parseInt(res[0])==0?1:res[0]) + "��";
	       }*/
	       strAge=showAgeString(res);
	       
	       return strAge;
	   }
	   
	/**
	 * ������������㴦��
	 * 
	 * @param res
	 * @return
	 */
	private static String showAgeString(String[] t) {
		String strAge = "";
		int nYear = Integer.valueOf(t[0]);
		int nMonth = Integer.valueOf(t[1]);
		int nDay = Integer.valueOf(t[2]);  //ȥ��+1
		int nHour = Integer.valueOf(t[3]);
		// int nMinuter=Integer.valueOf(t[4]);
		int nHour1 = (Integer.valueOf(t[2]) * 24 + nHour);
		//System.out.println("---nYear--"+nYear);
		//System.out.println("---nMonth--"+nMonth);
		//System.out.println("---nHour1--"+nHour1);
		
		// ����<=72Сʱ
		if ((nYear == 0 && nMonth == 0) && (nHour1 <= 72)) {
			if(nHour1==0){
				if (isDebug) {
					System.out.println("--strAge1--" + "1Сʱ");
				}
				return strAge = "1Сʱ";
			}
			strAge = nHour1 + "Сʱ";
			if (isDebug) {
				System.out.println("--strAge1--" + strAge);
			}
			return strAge;
		}
		// 72Сʱ<����<=28��
		if ((nYear == 0 && nMonth == 0) && (nHour1 > 72 && nDay <= 28)) {
			// ��ʾ0��0��n��
			strAge = nYear + "��" + nMonth + "��" + nDay + "��";
			;
			if (isDebug) {
				System.out.println("--strAge1--" + strAge);
			}
			return strAge;
		}

		// 28��<����<=2�� nMonth����0�������Σ���������
		if ((nYear == 0 && nMonth == 0 && nDay > 28)) {
			// ��ʾn��m��s��
			strAge = nYear + "��" + nMonth + "��" + nDay + "��";
			if (isDebug) {
				System.out.println("--strAge2_1--" + strAge);
			}
			return strAge;
		}

		if (nYear == 0 && nMonth > 0) {
			strAge = nYear + "��" + nMonth + "��" + nDay + "��";
			if (isDebug) {
				System.out.println("--strAge2_2--" + strAge);
			}
			return strAge;
		}
		if (nYear == 1 && nMonth > 0) {
			strAge = nYear + "��" + nMonth + "��" + nDay + "��";
			if (isDebug) {
				System.out.println("--strAge2_2--" + strAge);
			}
			return strAge;
		}
		 
		if ((nYear > 0 && nYear <= 2)&&nMonth == 0) {
			// ��ʾn��m��s��
			strAge = nYear + "��" + nMonth + "��" + nDay + "��";
			if (isDebug) {
				System.out.println("--strAge2_3--" + strAge);
			}
			return strAge;
		}

		// 2��<����<18��
		if ((nYear >= 2) && nYear < 18) {
			// ��ʾn��m��s��
			strAge = nYear + "��" + nMonth + "��";
			if (isDebug) {
				System.out.println("--strAge3--" + strAge);
			}
			return strAge;
		}

		// ����>=18�� ��ʾn��
		if (isDebug) {
			System.out.println("--strAge4--" + nYear + "��");
		}
		return nYear + "��";

	}
	   
	   
	   /**
	     * �õ���������ֱ𴫳�������(eh)
	     * @param t1 Timestamp ��������
	     * @param t2 Timestamp ��������
	     * @return String[](0:��,1:��,2:��)
	     */
	    public static String[] CountAgeByTimestamp(Timestamp t1, Timestamp t2)
	    {
	    	//
	        String ayear = "";
	        String amonth = "";
	        String aday = "";
	        
	        //Сʱ
	        String  ahour="";
	        String  aminute="";

	        String[] rString =
	                           {ayear, amonth, aday,ahour,aminute};
	        if (t1 == null || t2 == null)
	        {
	            return rString;
	        }

	        // ȡ�ý����������
	        GregorianCalendar today = new GregorianCalendar(getYear(t2),
	                getMonth(t2)-1, getDay(t2),getHour(t2),getMinute(t2));
	        // today.set(1973,6-1,30);
	        int iyear2 = getYear(t2);
	        int imonth2 = getMonth(t2);
	        int iday2 = getDay(t2);
	        
	        int iHour2=getHour(t2);
	        if(isDebug){
	        	System.out.println("---iHour2---"+iHour2);
	        }
	        int iMinute2=getMinute(t2);
	        
	        
	        int maxDays = today.getActualMaximum(today.DAY_OF_MONTH);
	        // ȡ�������������
	        GregorianCalendar birthday = new GregorianCalendar();
	        int iyear1 = getYear(t1);
	        int imonth1 = getMonth(t1);
	        int iday1 = getDay(t1);
	        
	        int iHour1=getHour(t1);
	        if(isDebug){
	        	System.out.println("---iHour1---"+iHour1);
	        }
	        
	        int iMinute1=getMinute(t1);
	        
	        birthday.set(iyear1, imonth1-1, iday1,iHour1,iMinute1);
	        int minDays = birthday.getActualMaximum(birthday.DAY_OF_MONTH);
	        // �趨������ʼֵ
	        int year = iyear2 - iyear1;
	        int month = imonth2 - imonth1;
	        int day = iday2 - iday1; //+ 1;
	        
	        int hour=iHour2-iHour1;
	        if(isDebug){
	        	System.out.println("-----hour-----"+hour);
	        }
	        int minute=iMinute2-iMinute1;
	        if(isDebug){
	        	System.out.println("-----minute-----"+minute);
	        }
	        
	        if(minute<0){
	        	minute = 60 + minute;
	        	hour--;
	        }
	        
	        if(hour<0){
	        	 hour = 24 + hour;
		         day--;
	        }       	        
	        
	        if (day < 0)
	        {
	            day = maxDays + day;
	            month--;
	        } else
	        {
	            day = minDays + day - maxDays;
	            if (day < 0)
	            {
	                day = maxDays + day;
	                month--;
	            }
	        }
	        if (month < 0)
	        {
	            month = 12 + month;
	            year--;
	        }
	        rString[0] = String.valueOf(year);
	        rString[1] = String.valueOf(month);
	        rString[2] = String.valueOf(day);
	        
	        rString[3] = String.valueOf(hour);
	        rString[4] = String.valueOf(minute);

	        return rString;
	    }
	    
	    
	    /**
	     * �õ���
	     * @param d Date
	     * @return int
	     */
	    public static int getYear(Date d)
	    {
	        Calendar c = Calendar.getInstance();
	        c.setTime(d);
	        return c.get(Calendar.YEAR);
	    }
	    /**
	     * �õ���
	     * @param d Date
	     * @return int
	     */
	    public static int getMonth(Date d)
	    {
	        Calendar c = Calendar.getInstance();
	        c.setTime(d);
	        return c.get(Calendar.MONTH) + 1;
	    }
	    /**
	     * �õ���
	     * @param d Date
	     * @return int
	     */
	    public static int getDay(Date d)
	    {
	        Calendar c = Calendar.getInstance();
	        c.setTime(d);
	        return c.get(Calendar.DATE);
	    }
	    /**
	     * �õ�Сʱ
	     * @param d Date
	     * @return int
	     */
	    public static int getHour(Date d)
	    {
	        Calendar c = Calendar.getInstance();
	        c.setTime(d);
	        return c.get(Calendar.HOUR_OF_DAY);
	    }
	    /**
	     * �õ�����
	     * @param d Date
	     * @return int
	     */
	    public static int getMinute(Date d)
	    {
	        Calendar c = Calendar.getInstance();
	        c.setTime(d);
	        return c.get(Calendar.MINUTE);
	    }
	    /**
	     * �õ���
	     * @param d Date
	     * @return int
	     */
	    public static int getSecond(Date d)
	    {
	        Calendar c = Calendar.getInstance();
	        c.setTime(d);
	        return c.get(Calendar.SECOND);
	    }
}
