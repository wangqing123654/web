package jdo.odo;

import java.util.Vector;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

/**
*
* <p>Title: ����ҽ������վ���׵���</p>
*
* <p>Description:����ҽ������վ���׵���</p>
*
* <p>Copyright: Copyright (c) 2008</p>
*
* <p>Company:Javahis </p>
*
* @author ehui 20090503
* @version 1.0
*/
public class OpdComPackQuoteTool extends TJDOTool{
	//��ʼ����DATASTORE��
	public  static final String INIT_SQL="SELECT * FROM OPD_PACK_MAIN";
	public  static final String INIT_ORDER_SQL="SELECT * FROM OPD_PACK_ORDER WHERE RX_TYPE<>'5' AND RX_TYPE<>'3'";
	public  static final String INIT_EXA_SQL="SELECT * FROM OPD_PACK_ORDER WHERE RX_TYPE='5'";
	public  static final String INIT_CHN_SQL="SELECT * FROM OPD_PACK_ORDER WHERE RX_TYPE='3'";

	public  static final String INIT_DIAG_SQL="SELECT * FROM OPD_PACK_DIAG";
	//��ʼ�������
	private static final String INIT_DIAG_SQL_QUOTE=
		"SELECT 'Y' AS CHOOSE,B.ICD_CHN_DESC,B.ICD_ENG_DESC,A.DEPT_OR_DR,A.DEPTORDR_CODE," +
			"	A.PACK_CODE,A.ICD_TYPE,A.ICD_CODE,A.MAIN_DIAG_FLG,A.DIAG_NOTE,B.ICD_ENG_DESC" +
		" FROM OPD_PACK_DIAG A,SYS_DIAGNOSIS B";
	//��ʼ��ҽ����
	private static final String INIT_ORDER_SQL_QUOTE=
		"SELECT 'Y' AS CHOOSE,DEPT_OR_DR,DEPTORDR_CODE,PACK_CODE,PRESRT_NO," +
		"		SEQ_NO,RX_TYPE,LINKMAIN_FLG,LINK_NO,ORDER_CODE," +
		"		MEDI_QTY,MEDI_UNIT,FREQ_CODE,ROUTE_CODE,TAKE_DAYS," +
		"		GIVEBOX_FLG,DESCRIPTION,EXEC_DEPT_CODE,SETMAIN_FLG,URGENT_FLG," +
		"		DCTAGENT_CODE,DCTEXCEP_CODE,DCT_TAKE_QTY,PACKAGE_TOT,OPT_USER," +
		"		OPT_DATE,OPT_TERM,ORDER_DESC,SPECIFICATION,TRADE_ENG_DESC " +
		"	FROM OPD_PACK_ORDER ";
	//��ʼ��combo��
	private static final String INIT_COMBO_SQL="SELECT PACK_CODE AS ID,PACK_DESC AS NAME FROM OPD_PACK_MAIN";
	/**
     * ʵ��
     */
    public static OpdComPackQuoteTool instanceObject;
    /**
     * �õ�ʵ��
     * @return OPDMainTool
     */
    public static OpdComPackQuoteTool getInstance()
    {
        if(instanceObject == null)
            instanceObject = new OpdComPackQuoteTool();
        return instanceObject;
    }
    /**
     * ������
     */
    public OpdComPackQuoteTool()
    {
//        setModuleName("opd\\OPDComPackQuoteModule.x");
        onInit();
    }
    /**
     * ��ʼ�����淽��
     * @param parm DEPT_OR_DR,DEPTORDR_CODE
     * @return String
     */
    public String initQuote(TParm parm){
    	StringBuffer result=new StringBuffer(INIT_SQL);
    	result.append(" WHERE DEPT_OR_DR='" +parm.getValue("DEPT_OR_DR")+"'").append(" AND DEPTORDR_CODE='" +parm.getValue("DEPTORDR_CODE")+"' ORDER BY SEQ,PACK_CODE");  //add by huangtt 20150225 seq����
    	return result.toString();
    }
    /**
     * ��ʼ��ģ�����
     * @param parm DEPT_OR_DR,DEPTORDR_CODE
     * @return String
     */
    public TParm initQuoteDiag(TParm parm){
    	String sql=initQuoteDiagSQl(INIT_DIAG_SQL_QUOTE,parm);
    	//System.out.println("diag sql----============="+sql);
    	TParm result=new TParm(TJDODBTool.getInstance().select(sql.toString()));
    	return result;
    }
    public String initQuoteDiagSQl(String sql,TParm parm){
    	StringBuffer result=new StringBuffer(sql);
    	result.append(" WHERE DEPT_OR_DR='" +parm.getValue("DEPT_OR_DR")+"'").append(" AND DEPTORDR_CODE='"
    			+parm.getValue("DEPTORDR_CODE")+"'AND PACK_CODE='" +parm.getValue("PACK_CODE")+
    			"' AND A.ICD_CODE=B.ICD_CODE ");
    	return result.toString();
    }
    /**
     * ��ʼ��ģ�����
     * @param parm DEPT_OR_DR,DEPTORDR_CODE
     * @return String
     */
    public String initQuoteOrder(TParm parm){
    	StringBuffer result=new StringBuffer(INIT_ORDER_SQL);
    	result.append(" WHERE DEPT_OR_DR='" +parm.getValue("DEPT_OR_DR")+"'").append(" AND DEPTORDR_CODE='" +parm.getValue("DEPTORDR_CODE")+"'");
    	return result.toString();
    }
    /**
     * ��ʼ��������COMBO
     * @param parm
     * @return
     */
    public TParm initCombo(TParm parm){
    	TParm result=new TParm();
    	if(parm==null){
    		return result;
    	}
    	String sql=INIT_COMBO_SQL+
    			" WHERE DEPT_OR_DR='" +parm.getValue("DEPT_OR_DR")+
    				 "' AND DEPTORDR_CODE='"+parm.getValue("DEPTORDR_CODE")+
    				 "'  ORDER BY PACK_CODE";
//    	//System.out.println("INIT_COMOBO_SQL======="+sql);
//    	//System.out.println("INIT_COMOBO_SQL=======");
    	result=new TParm(TJDODBTool.getInstance().select(sql));
    	return result;
    }
    /**
     * ������ȡ����ҽ����������
     * @param parm
     * @return
     */
    public TParm initOrderForPackQuote(TParm parm) {
		TParm result = new TParm();
		if (parm == null) {
			return result;
		}
		String sql = initOrderSqlForPackQuote(parm);
		result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}
    /**
     * ������ȡ����ҽ����
     * @param parm
     * @return
     */
    public TParm initChnForPackQuote(TParm parm) {
		TParm result = new TParm();
		if (parm == null) {
			return result;
		}
		String sql = initChnSqlForPackQuote(parm);
		result = new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getErrCode()!=0){
			return result;
		}
		return result;
	}
    /**
     * ���ҩƷ�ĳ�ʼ��SQL
     * @param parm
     * @return
     */
    public String initOrderSqlForPackQuote(TParm parm){
		if (parm == null) {
			return "";
		}
		//yanjing 20130625 �޸�SQL
		String init_order_sql = "SELECT B.OPD_FIT_FLG,B.EMG_FIT_FLG,B.ACTIVE_FLG," +
				"'Y' AS CHOOSE,DEPT_OR_DR,DEPTORDR_CODE,PACK_CODE,PRESRT_NO," +
				"SEQ_NO,RX_TYPE,LINKMAIN_FLG,LINK_NO,B.ORDER_CODE,MEDI_QTY," +
				"MEDI_UNIT,FREQ_CODE,A.ROUTE_CODE,A.TAKE_DAYS," +
				"A.GIVEBOX_FLG,A.DESCRIPTION,A.EXEC_DEPT_CODE,SETMAIN_FLG,URGENT_FLG," +
				"DCTAGENT_CODE,DCTEXCEP_CODE,DCT_TAKE_QTY,PACKAGE_TOT,A.OPT_USER," +
				"A.OPT_DATE,A.OPT_TERM,A.ORDER_DESC,A.SPECIFICATION,A.TRADE_ENG_DESC," +
				"B.USE_CAT"+
				" FROM OPD_PACK_ORDER A ,SYS_FEE B ";
//		String sql = INIT_ORDER_SQL_QUOTE + " WHERE DEPT_OR_DR='"
		String sql = init_order_sql + " WHERE A.ORDER_CODE = B.ORDER_CODE(+) AND DEPT_OR_DR='"
				+ parm.getValue("DEPT_OR_DR") + "' AND DEPTORDR_CODE='"
				+ parm.getValue("DEPTORDR_CODE") + "' AND PACK_CODE='"
				+ parm.getValue("PACK_CODE") + "' AND RX_TYPE IN('1','2','4')  ORDER BY RX_TYPE,SEQ_NO ";
		//System.out.println("initOrderSqlForPackQuote=========="+sql);
		return sql;
    }
    /**
     * ���ҩƷ�ĳ�ʼ��SQL
     * @param parm
     * @return
     */
    public String initChnSqlForPackQuote(TParm parm){
		if (parm == null) {
			return "";
		}
		String sql = INIT_ORDER_SQL_QUOTE + " WHERE DEPT_OR_DR='"
				+ parm.getValue("DEPT_OR_DR") + "' AND DEPTORDR_CODE='"
				+ parm.getValue("DEPTORDR_CODE") + "' AND PACK_CODE='"
				+ parm.getValue("PACK_CODE") + "' AND RX_TYPE ='3'  ORDER BY RX_TYPE,SEQ_NO ";
		//System.out.println("initChnSqlForPackQuote=========="+sql);
		return sql;
    }
    /**
     * ��ü�����ĳ�ʼ��SQL
     * @param parm
     * @return
     */
    public TParm initExaForPackQuote(TParm parm){
		TParm result = new TParm();
		if (parm == null) {
			return result;
		}
		String sql = initExaSqlForPackQuote(parm);
		result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
    }
    /**
     * ���ҩƷ�ĳ�ʼ��SQL
     * @param parm
     * @return
     */
    public String initExaSqlForPackQuote(TParm parm){
    	TParm result = new TParm();
		if (parm == null) {
			return "";
		}
		//yanjing 20130717 �޸�SQL �����sys_fee�Ĺ���
		String init_order_sql = "SELECT B.OPD_FIT_FLG,B.EMG_FIT_FLG,B.ACTIVE_FLG," +
				"'Y' AS CHOOSE,DEPT_OR_DR,DEPTORDR_CODE,PACK_CODE,PRESRT_NO," +
				"SEQ_NO,RX_TYPE,LINKMAIN_FLG,LINK_NO,B.ORDER_CODE,MEDI_QTY," +
				"MEDI_UNIT,FREQ_CODE,A.ROUTE_CODE,A.TAKE_DAYS," +
				"A.GIVEBOX_FLG,A.DESCRIPTION,A.EXEC_DEPT_CODE,SETMAIN_FLG,URGENT_FLG," +
				"DCTAGENT_CODE,DCTEXCEP_CODE,DCT_TAKE_QTY,PACKAGE_TOT,A.OPT_USER," +
				"A.OPT_DATE,A.OPT_TERM,A.ORDER_DESC,A.SPECIFICATION,A.TRADE_ENG_DESC  " +
				"FROM OPD_PACK_ORDER A ,SYS_FEE B ";
//		String sql = INIT_ORDER_SQL_QUOTE + " WHERE DEPT_OR_DR='"
		String sql = init_order_sql +  " WHERE A.ORDER_CODE = B.ORDER_CODE(+) AND DEPT_OR_DR='"
				+ parm.getValue("DEPT_OR_DR") + "' AND DEPTORDR_CODE='"
				+ parm.getValue("DEPTORDR_CODE") + "' AND PACK_CODE='"
				+ parm.getValue("PACK_CODE") + "' AND RX_TYPE='5'  ORDER BY RX_TYPE,SEQ_NO ";
		//System.out.println("initExaSqlForPackQuote=========="+sql);
		return sql;
    }
    /**
     * ȡ������ҩ��SQL���
     * @param parm DEPT_OR_DR,DEPTORDR_CODE,PACK_CODE
     * @return
     */
    public String initOrderSQL(String sql,TParm parm){
    	if(parm==null){
    		return "";
    	}
    	String result = sql + " AND DEPT_OR_DR='"
		+ parm.getValue("DEPT_OR_DR") + "' AND DEPTORDR_CODE='"
		+ parm.getValue("DEPTORDR_CODE") + "' AND PACK_CODE='"
		+ parm.getValue("PACK_CODE") + "' ORDER BY SEQ_NO";
    	return result;
    }
    /**
     * ������ȡ�ô�������
     * @param parm
     * @return
     */
    public TParm initOrder(TParm parm){

		TParm result = new TParm();
		if (parm == null) {
			return result;
		}
		String sql = initOrderSQL(INIT_ORDER_SQL_QUOTE, parm);
		result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
    }

    /**
     * ȡ����ҩ��SQL���
     * @param sql ����SQL
     * @param parm
     * @return
     */
    public String initChnSQL(String sql,TParm parm){
      	if(parm==null){
    		return "";
    	}
    	String result = sql + " AND DEPT_OR_DR='"
		+ parm.getValue("DEPT_OR_DR") + "' AND DEPTORDR_CODE='"
		+ parm.getValue("DEPTORDR_CODE") + "' AND PACK_CODE='"
		+ parm.getValue("PACK_CODE") + "' ORDER BY PRESRT_NO , SEQ_NO";

    	return result;
    }
    /**
     * ������ȡ�ô�������
     * @param parm
     * @return
     */
    public TParm initChn(TParm parm){
		TParm result = new TParm();
		if (parm == null) {
			return result;
		}
		String sql = INIT_ORDER_SQL + " WHERE DEPT_OR_DR='"
				+ parm.getValue("DEPT_OR_DR") + "' AND DEPTORDR_CODE='"
				+ parm.getValue("DEPTORDR_CODE") + "' AND PACK_CODE='"
				+ parm.getValue("PACK_CODE") + "' AND RX_TYPE ='3' ORDER BY SEQ_NO";
		result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
    }
    /**
     * ������ҽ���ݼ�ȡ����ҽ����ǩcombo����
     * @param parm
     * @return
     */
    public TParm initChnCombo(TParm parm){
		int count=parm.getCount("PRESRT_NO");
		//System.out.println("count==============="+count);
		TParm result=new TParm();
		if(count<1){
			return result;
		}
		Vector list=new Vector();

		for(int i=0;i<count;i++){
			String tempPrint= parm.getValue("PRESRT_NO",i);
			if(list.indexOf(tempPrint)<0){
				list.add(tempPrint);
			}
		}

		if(list.size()<1){
			return result;
		}
		for(int i=0;i<list.size();i++){
			for(int j=i+1;j<list.size();j++){
				Long rxI=Long.parseLong(list.get(i)+"");
				Long rxJ=Long.parseLong(list.get(j)+"");
				if(rxI>rxJ){
					Long temp=rxI;
					list.set(i, "0"+rxJ.toString());
					list.set(j, "0"+rxI.toString());
				}

			}
		}
		//System.out.println("list==="+list);
		for(int i=0;i<list.size();i++){
			String s = (String) list.get(i);
			result.addData("ID", s);
			result.addData("NAME", "�� ��" + (i + 1) + "�� ����ǩ");
		}
		result.setCount(list.size());
//		//System.out.println("result======="+result);
		return result;
    }
    /**
     * ȡ����ҽTABLE��ʾ����
     * @param chn
     * @param rxNo
     * @return
     */
    public TParm getChnTableParm(TParm chn,String rxNo){
    	TParm result=new TParm();
    	if(chn==null||chn.getCount()<1||rxNo==null||rxNo.trim().length()<1){
    		return result;
    	}
    	int count=chn.getCount("PRESRT_NO");
    	TParm chnParm=new TParm();
    	String[] columnNames=chn.getNames();
    	//System.out.println("rxNo================="+rxNo);
    	for(int i=0;i<count;i++){
    		if(!rxNo.equalsIgnoreCase(chn.getValue("PRESRT_NO",i))){
    			continue;
    		}
    		for(int j=0;j<columnNames.length;j++){
    			chnParm.addData(columnNames[j], chn.getValue(columnNames[j],i));
    		}

    	}
    	count=chnParm.getCount("PRESRT_NO");
    	for(int i=0;i<count;i++){
    		int idx=i%4+1;
    		result.addData("ORDER_DESC"+idx, chnParm.getValue("ORDER_DESC",i));
			result.addData("MEDI_QTY"+idx, chnParm.getDouble("MEDI_QTY",i));
			result.addData("DCTEXCEP_CODE"+idx, chnParm.getValue("DCTEXCEP_CODE",i));

			result.addData("TAKE_DAYS", chnParm.getValue("TAKE_DAYS",i));
			result.addData("DCT_TAKE_QTY", chnParm.getValue("DCT_TAKE_QTY",i));
			result.addData("FREQ_CODE", chnParm.getValue("FREQ_CODE",i));
			result.addData("ROUTE_CODE", chnParm.getValue("ROUTE_CODE",i));
			result.addData("DCTAGENT_CODE", chnParm.getValue("DCTAGENT_CODE",i));
			result.addData("DESCRIPTION", chnParm.getValue("DESCRIPTION",i));
    	}
    	return result;
    }
    /**
     * ��ORDER_CODE�õ�ҩƷ��
     * @param orderCode
     * @return
     */
    public String getOrderDesc(String orderCode){
    	if(orderCode==null||orderCode.trim().length()<1){
    		return "";
    	}
    	TParm parm=new TParm(TJDODBTool.getInstance().select("SELECT ORDER_DESC FROM SYS_FEE WHERE ORDER_CODE='" +orderCode+"'"));
    	return parm.getValue("ORDER_DESC",0);
    }
    /**
     * ���ݸ����TParm��rxNoȡ�ø�TParm�д��Ŵ���������
     * @param parm
     * @param rxNo
     * @return
     */
    public TParm getRxParm(TParm parm,String rxNo){
    	TParm result=new TParm();
    	if(parm==null){
    		return result;
    	}
    	int count=parm.getCount();
    	if(count<1){
    		return result;
    	}
    	String name="";
    	String[] names=parm.getNames();
    	for(String temp:names){
    		name=name+temp+";";
    	}
    	name=name.substring(0,name.lastIndexOf(";"));
    	for(int i=0;i<count;i++){
    		if(rxNo.equalsIgnoreCase(parm.getValue("PRESRT_NO",i))){
    			result.addRowData(parm, i, name);
    		}
    	}
    	result.setCount(result.getCount("PRESRT_NO"));
    	return result;
    }
}
