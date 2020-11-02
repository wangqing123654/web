package jdo.clp;
/**
 *
 * <p>Title: Ami���ݼ�</p>
 *
 * <p>Description:Ami���ݼ�</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company:JavaHis</p>
 *
 * @author ehui 20100305
 * @version 1.0
 */
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.TypeTool;

public class ClpAmiTool {
	//�ṹ�������м��1��Ӧ���ݿ��е�ֵ
	private static final String exm1="E";
	//�ṹ�������м��2��Ӧ���ݿ��е�ֵ
	private static final String exm2="I30";
	//�ṹ�������м��3��Ӧ���ݿ��е�ֵ
	private static final String exm3="I60";
	//�ṹ�������м��4��Ӧ���ݿ��е�ֵ
	private static final String exm4="I90";
	//�ṹ�������м��5��Ӧ���ݿ��е�ֵ
	private static final String exm5="I360";
	//�ṹ�������м��6��Ӧ���ݿ��е�ֵ
	private static final String exm6="I24";
	//�ṹ�������м��7��Ӧ���ݿ��е�ֵ
	private static final String exm7="IN";
	//�ṹ�������м��8��Ӧ���ݿ��е�ֵ
	private static final String exm8="OUT";
	private static final String GET_AMI1_NO="SELECT COUNT(CASE_NO) AMI_1 FROM CLP_AMI WHERE AMI_1='#'";
	private static final String GET_AMI2_NO="SELECT COUNT(CASE_NO) AMI_2 FROM CLP_AMI WHERE AMI_2='#'";
	private static final String GET_AMI3_1_NO="SELECT COUNT(CASE_NO) AMI_3_1 FROM CLP_AMI WHERE AMI_3_1='#'";
	private static final String GET_AMI3_2_NO="SELECT COUNT(CASE_NO) AMI_3_2 FROM CLP_AMI WHERE AMI_3_2='#'";
	private static final String GET_AMI3_3_NO="SELECT COUNT(CASE_NO) AMI_3_3 FROM CLP_AMI WHERE AMI_3_3='#'";
	private static final String GET_AMI4_NO="SELECT COUNT(CASE_NO) AMI_4 FROM CLP_AMI WHERE AMI_4='#'";
	private static final String GET_AMI5_NO="SELECT COUNT(CASE_NO) AMI_5 FROM CLP_AMI WHERE AMI_5='#'";
	private static final String GET_AMI6_NO="SELECT COUNT(CASE_NO) AMI_6 FROM CLP_AMI WHERE AMI_6='#'";
	private static final String GET_AMI7_NO="SELECT COUNT(CASE_NO) AMI_7 FROM CLP_AMI WHERE AMI_7='Y'";
	private static final String GET_AMI7_1_NO="SELECT COUNT(CASE_NO) AMI_7_1 FROM CLP_AMI WHERE AMI_7_1='#'";
	private static final String GET_AMI7_2_NO="SELECT COUNT(CASE_NO) AMI_7_2 FROM CLP_AMI WHERE AMI_7_2='#'";
	private static final String GET_AMI7_3_NO="SELECT COUNT(CASE_NO) AMI_7_3 FROM CLP_AMI WHERE AMI_7_3='#'";
	private static final String GET_AMI3_NO_NULL1="SELECT COUNT(CASE_NO) AMI3_NULL1 FROM CLP_AMI WHERE AMI_3_1 IS NOT NULL";
	private static final String GET_AMI3_NO_NULL2="SELECT COUNT(CASE_NO) AMI3_NULL2 FROM CLP_AMI WHERE AMI_3_2 IS NOT NULL";
	private static final String GET_AMI3_NO_NULL3="SELECT COUNT(CASE_NO) AMI3_NULL3 FROM CLP_AMI WHERE AMI_3_3 IS NOT NULL";
	private static final String GET_ALL_NO="SELECT COUNT(CASE_NO) AMI_NO FROM CLP_AMI";

//        /**
//         * ʵ��
//         */
//        public static ClpAmiTool instanceObject;
//        /**
//         * �õ�ʵ��
//         * @return BILAccountTool
//         */
//        public static ClpAmiTool getInstance() {
//
//            if (instanceObject == null)
//                instanceObject = new ClpAmiTool();
//            return instanceObject;
//        }


	/**
	 * ȡ��AMI_1������
	 * @return
	 */
	public double getAmi1No(String ami1){
		if(ami1==null||ami1.trim().length()<=0){
			return -1;
		}
		String sql=GET_AMI1_NO.replaceFirst("#", ami1);
		TParm result=new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getErrCode()!=0){
			//SYSTEM.OUT.PRINTln("getAmi1No.getErrText:"+result.getErrText());
			return -1;
		}
		double rate=TypeTool.getDouble(result.getData("AMI_1",0));
		if(rate==0){
			return -1;
		}
		return  rate;
	}
	public double getAmi2No(String ami2){
		if(ami2==null||ami2.trim().length()<=0){
			return -1;
		}
		String sql=GET_AMI2_NO.replaceFirst("#", ami2);
		TParm result=new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getErrCode()!=0){
			//SYSTEM.OUT.PRINTln("getAmi2No.getErrText:"+result.getErrText());
			return -1;
		}
		double rate=TypeTool.getDouble(result.getData("AMI_2",0));
		if(rate==0){
			return -1;
		}
		return  rate;
	}
	public double getAmi3_1No(){
		String sql=GET_AMI3_NO_NULL1;
		TParm result=new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getErrCode()!=0){
			//SYSTEM.OUT.PRINTln("getAmi3_1No.getErrText:"+result.getErrText());
			return -1;
		}
		double rate=TypeTool.getDouble(result.getData("AMI3_NULL1",0));
		if(rate==0){
			return -1;
		}
		return  rate;
	}
	public double getAmi3_2No(){
		String sql=GET_AMI3_NO_NULL2;
		TParm result=new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getErrCode()!=0){
			//SYSTEM.OUT.PRINTln("getAmi3_2No.getErrText:"+result.getErrText());
			return -1;
		}
		double rate=TypeTool.getDouble(result.getData("AMI3_NULL2",0));
		if(rate==0){
			return -1;
		}
		return  rate;
	}
	public double getAmi3_3No(){
		String sql=GET_AMI3_NO_NULL3;
		TParm result=new TParm(TJDODBTool.getInstance().select(sql));
		double rate=TypeTool.getDouble(result.getData("AMI3_NULL3",0));
		if(rate==0){
			return -1;
		}
		return  rate;
	}
	public double getAmi3_1No(String am3_1){
		if(am3_1==null||am3_1.trim().length()<=0){
			return -1;
		}
		String sql=GET_AMI3_1_NO.replaceFirst("#", am3_1);
		TParm result=new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getErrCode()!=0){
			//SYSTEM.OUT.PRINTln("getAmi3_1No.getErrText:"+result.getErrText());
			return -1;
		}
		double rate=TypeTool.getDouble(result.getData("AMI_3_1",0));
		if(rate==0){
			return -1;
		}
		return  rate;
	}
	public double getAmi3_2No(String am3_2){
		if(am3_2==null||am3_2.trim().length()<=0){
			return -1;
		}
		String sql=GET_AMI3_2_NO.replaceFirst("#", am3_2);
		TParm result=new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getErrCode()!=0){
			//SYSTEM.OUT.PRINTln("getAmi3_2No.getErrText:"+result.getErrText());
			return -1;
		}
		double rate=TypeTool.getDouble(result.getData("AMI_3_2",0));
		if(rate==0){
			return -1;
		}
		return  rate;
	}
	public double getAmi3_3No(String am3_3){
		if(am3_3==null||am3_3.trim().length()<=0){
			return -1;
		}
		String sql=GET_AMI3_3_NO.replaceFirst("#", am3_3);
		TParm result=new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getErrCode()!=0){
			//SYSTEM.OUT.PRINTln("getAmi3_3No.getErrText:"+result.getErrText());
			return -1;
		}
		double rate=TypeTool.getDouble(result.getData("AMI_3_3",0));
		if(rate==0){
			return -1;
		}
		return  rate;
	}

	public double getAmi4No(String ami4){
		if(ami4==null||ami4.trim().length()<=0){
			return -1;
		}
		String sql=GET_AMI4_NO.replaceFirst("#", ami4);
		TParm result=new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getErrCode()!=0){
			//SYSTEM.OUT.PRINTln("getAmi4No.getErrText:"+result.getErrText());
			return -1;
		}
		double rate=TypeTool.getDouble(result.getData("AMI_4",0));
		if(rate==0){
			return -1;
		}
		return rate;
	}
	public double getAmi5No(String ami5){
		if(ami5==null||ami5.trim().length()<=0){
			return -1;
		}
		String sql=GET_AMI5_NO.replaceFirst("#", ami5);
		TParm result=new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getErrCode()!=0){
			//SYSTEM.OUT.PRINTln("getAmi5No.getErrText:"+result.getErrText());
			return -1;
		}
		double rate=TypeTool.getDouble(result.getData("AMI_5",0));
		if(rate==0){
			return -1;
		}
		return  rate;
	}
	public double getAmi6No(String ami6){
		if(ami6==null||ami6.trim().length()<=0){
			return -1;
		}
		String sql=GET_AMI6_NO.replaceFirst("#", ami6);
		TParm result=new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getErrCode()!=0){
			//SYSTEM.OUT.PRINTln("getAmi6No.getErrText:"+result.getErrText());
			return -1;
		}
		double rate=TypeTool.getDouble(result.getData("AMI_6",0));
		if(rate==0){
			return -1;
		}
		return  rate;
	}
	public double getAmi7_1No(String ami7_1){
		if(ami7_1==null||ami7_1.trim().length()<=0){
			return -1;
		}
		String sql=GET_AMI7_1_NO.replaceFirst("#", ami7_1);
		TParm result=new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getErrCode()!=0){
			//SYSTEM.OUT.PRINTln("getAmi7_1No.getErrText:"+result.getErrText());
			return -1;
		}
		double rate=TypeTool.getDouble(result.getData("AMI_7_1",0));
		if(rate==0){
			return -1;
		}
		return  rate;
	}
	public double getAmi7_2No(String ami7_2){
		if(ami7_2==null||ami7_2.trim().length()<=0){
			return -1;
		}
		String sql=GET_AMI7_2_NO.replaceFirst("#", ami7_2);
		TParm result=new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getErrCode()!=0){
			//SYSTEM.OUT.PRINTln("getAmi7_2No.getErrText:"+result.getErrText());
			return -1;
		}
		double rate=TypeTool.getDouble(result.getData("AMI_7_2",0));
		if(rate==0){
			return -1;
		}
		return  rate;
	}
	public double getAmi7_3No(String ami7_3){
		if(ami7_3==null||ami7_3.trim().length()<=0){
			return -1;
		}
		String sql=GET_AMI7_3_NO.replaceFirst("#", ami7_3);
		TParm result=new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getErrCode()!=0){
			//SYSTEM.OUT.PRINTln("getAmi7_3No.getErrText:"+result.getErrText());
			return -1;
		}
		double rate=TypeTool.getDouble(result.getData("AMI_7_3",0));
		if(rate==0){
			return -1;
		}
		return  rate;
	}
	public double getAmi8No(String fee){
		return 0;
	}
	/**
	 * ȡ��AMI_1EXM1
	 * @return
	 */
	public String getAmi1ERate(){
		double rate=this.getAmi1No(exm1)/this.getAm1TotPerson();
		return this.getRateString(rate);
	}
	public String getRateString(double amt){
		return Math.round(amt)*100+"%";
	}
	/**
	 * ȡ��AMI_1EXM2
	 * @return
	 */
	public String getAmi1Emx2Rate(){
		double rate=this.getAmi1No(exm2)/this.getAm1TotPerson();
		return this.getRateString(rate);
	}
	/**
	 * ȡ��AMI_1EXM3
	 * @return
	 */
	public String getAmi1Emx3Rate(){
		double rate=this.getAmi1No(exm3)/this.getAm1TotPerson();
		return this.getRateString(rate);
	}
	/**
	 * ȡ��AMI_1EXM4
	 * @return
	 */
	public String getAmi1Emx4Rate(){
		double rate=this.getAmi1No(exm4)/this.getAm1TotPerson();
		return this.getRateString(rate);
	}
	/**
	 * AMI-1����ҽԺ�󼴿�ʹ�ð�˾ƥ�֣��н�����Ӧ�����������ף�
	 * @return
	 */
	public String getAmi1Rate(){
		double rate1=this.getAmi1No(exm1);
		double rate2=this.getAmi1No(exm2);
		double rate3=this.getAmi1No(exm3);
		double rate4=this.getAmi1No(exm4);
		if(rate1<0||rate2<0||rate3<0||rate4<0){
			return "--";
		}
		double rate=0;
		try{
			rate=(rate1+rate2+rate3+rate4)/this.getAm1TotPerson();
		}catch(Exception e){
			return "--";
		}
		return getRateString(rate);
	}
	/**
	 * AMI-2ʵʩ�����ҹ�������
	 * @return
	 */
	public String getAmi2Rate(){
		double rate1=this.getAmi2No(exm5);
		double rate2=this.getAmi2No(exm6);
		double rate3=this.getAmi2No(exm7);
		double rate4=this.getAmi2No(exm8);
		double rate=0;
		if(rate1<0||rate2<0||rate3<0||rate4<0){
			return "--";
		}
		try{
			rate=(rate1+rate2+rate3+rate4)/this.getAm1TotPerson();
		}catch(Exception e){
			return "--";
		}

		return getRateString(rate);
	}
	/**
	 * AMI-3�ٹ�ע���ƣ���������STEMI��
	 * @return
	 */
	public String getAmi3Rate(){
		double rate3_1=this.getAmi3_1No();
		double rate3_2=this.getAmi3_2No();
		double rate3_3=this.getAmi3_3No();
		double rate=0;
		if(rate3_1<0||rate3_2<0||rate3_3<0){
			return "--";
		}
		try{
			rate=(rate3_1+rate3_2)/(rate3_1+rate3_2+rate3_3);
		}catch(Exception e){
			return "--";
		}

		return getRateString(rate);
	}
	/**
	 * ʵʩ��˨����
	 * @return
	 */
	public String getAmi3_1Rate(){
		double rate3_1_IM=this.getAmi3_1No(exm1)+this.getAmi3_1No(exm2);
		double rate3_1=this.getAmi3_1No();
		double rate3_2=this.getAmi3_2No();
		double rate3_3=this.getAmi3_3No();
		double rate1=0;
		if(rate3_1_IM<0||rate3_1<0||rate3_2<0||rate3_3<0){
			return "--";
		}
		try{
			rate1=rate3_1_IM/(rate3_1+rate3_2+rate3_3);
		}catch(Exception e){
			return "--";
		}

		return getRateString(rate1);
	}
	/**
	 * ʵʩPCI����
	 * @return
	 */
	public String getAmi3_2Rate(){
		double rate3_2_IM=this.getAmi3_2No(exm1)+this.getAmi3_2No(exm2)+this.getAmi3_2No(exm3)+this.getAmi3_2No(exm4);
		double rate3_1=this.getAmi3_1No();
		double rate3_2=this.getAmi3_2No();
		double rate3_3=this.getAmi3_3No();
		double rate1=rate3_2_IM/(rate3_1+rate3_2+rate3_3);
		return getRateString(rate1);
	}
	/**
	 * ��Ҫ����PCIתԺ
	 * @return
	 */
	public String getAmi3_3Rate(){
		double rate3_1=this.getAmi3_1No();
		double rate3_2=this.getAmi3_2No();
		double rate3_3=this.getAmi3_3No();
		double rate1=rate3_3/(rate3_1+rate3_2+rate3_3);
		return getRateString(rate1);
	}
	/**
	 * ����ҽԺ�󼴿�ʹ�æ����ͼ����޽���֢��
	 * @return
	 */
	public String getAmi4Rate(){
		double rate2=this.getAmi4No(exm2);
		double rate3=this.getAmi4No(exm3);
		double rate4=this.getAmi4No(exm4);
		double rate5=this.getAmi4No(exm5);
		double rate6=this.getAmi4No(exm6);
		double rate=(rate2+rate3+rate4+rate5+rate6)/this.getAm1TotPerson();
		return getRateString(rate);
	}
	/**
	 * AMI-5סԺ�ڼ�ʹ�ð�˾ƥ�֡������ͼ���ACEI/ARB����͡��ҩ������ʾ���޽���֢�ߣ�
	 * @return
	 */
	public String getAmi5Rate(){
		double rate5=this.getAmi5No(exm5);
		double rate6=this.getAmi5No(exm6);
		double rate7=this.getAmi5No(exm7);
		double rate=(rate5+rate6+rate7)/this.getAm1TotPerson();
		return getRateString(rate);
	}
	/**
	 * ��Ժʱ����ʹ�ð�˾ƥ�֡������ͼ���ACEI/ARB����͡��ҩ������ʾ���޽���֢�ߣ�
	 * @return
	 */
	public String getAmi6Rate(){
		double rate=this.getAmi6No(exm8)/this.getAm1TotPerson();
		return getRateString(rate);
	}
	/**
	 * AMI-7Ϊ�����ṩ��������
	 * @return
	 */
	public String getAmi7Rate(){
		TParm result=new TParm(TJDODBTool.getInstance().select(GET_AMI7_NO));
		double rate=TypeTool.getDouble(result.getData("AMI_7",0));
		return getRateString(rate);
	}
	public String getAmi8Rate(){
		return "";
	}
	/**
	 * ȡ��AMI������
	 * @return
	 */
	public double getAm1TotPerson(){
		String sql=GET_ALL_NO;
		TParm result=new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getErrCode()!=0){
			return -1;
		}
		return TypeTool.getDouble(result.getData("AMI_NO",0));
	}
}
