package jdo.ibs;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
/**
 * <p>Title: 住院计价退费</p>
 *
 * <p>Description:住院计价退费</p>
 *
 * <p>Copyright: Copyright (c)cao yong 2014</p>
 *
 * <p>Company: BlueCore</p>
 *
 * @author 2014.05.12
 * @version 1.0
 */
public class IBSReturnTool extends TJDOTool{
	/**
	 * 实例
	 */
	public static IBSReturnTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return IBSOrderdTool
	 */
	public static IBSReturnTool getInstance() {
		if (instanceObject == null)
			instanceObject = new IBSReturnTool();
		return instanceObject;
	}

	/**
	 * 构造器
	 */
	public IBSReturnTool() {
		setModuleName("ibs\\IBSReturnModule.x");
		onInit();
	}
	/**
	 * 明细
	 * @param parm
	 * @return
	 */
	public TParm selectdataAll(TParm parm) {
		TParm result = new TParm();
		if (parm == null) {
			result.setErr(-1, "IBSOrderdTool.selectdataAll()参数异常!");
			return result;
		}
		result = query("selFeeDetail", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;

		}
		return result;
	}
	
	/**
	 * 汇总
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selMergeFee(TParm parm) {
		TParm result = new TParm();
		if (parm == null) {
			result.setErr(-1, "IBSOrderdTool.selMergeFee()参数异常!");
			return result;
		}
		String where =" AND D.CASE_NO='"+parm.getValue("CASE_NO")+"' ";
		String whereOne=" AND D.CASE_NO='"+parm.getValue("CASE_NO")+"' ";
		if(!"".equals(parm.getValue("ORDER_CODE"))&&parm.getValue("ORDER_CODE")!=null){//医嘱查询
			where+=" AND D.ORDER_CODE='"+parm.getValue("ORDER_CODE")+"' ";
			whereOne+=" AND D.ORDERSET_CODE='"+parm.getValue("ORDER_CODE")+"' ";
		}
		if(!"".equals(parm.getValue("COST_CENTER_CODE"))&&parm.getValue("COST_CENTER_CODE")!=null){//成本中心查询
			where+=" AND D.COST_CENTER_CODE='"+parm.getValue("COST_CENTER_CODE")+"' ";
			whereOne+=" AND D.COST_CENTER_CODE='"+parm.getValue("COST_CENTER_CODE")+"' ";
		}
		if(!"".equals(parm.getValue("REXP_CODE"))&&parm.getValue("REXP_CODE")!=null){//医嘱查询
			where+=" AND D.REXP_CODE='"+parm.getValue("REXP_CODE")+"' ";
			whereOne+=" AND D.REXP_CODE='"+parm.getValue("REXP_CODE")+"' ";
		}
		//System.out.println("REXP_TYPE:::"+parm);
		String sql1="SELECT   A.FLG, A.REXP_CODE, A.ORDER_CODE, A.ORDER_DESC, A.DESCRIPTION,"+
        " A.DOSAGE_QTY, A.UNIT_CODE, A.OWN_PRICE, A.TOT_AMT, A.RETURN_SUM,"+
        " A.CHARGE_HOSP_CODE, A.ORDERSET_FLG, A.CAT_FLG, A.OWN_RATE,"+
        " A.INV_CODE, A.INCLUDE_FLG ,'' ORDERSET_GROUP_NO "+
    "FROM (SELECT   'N' AS FLG, D.REXP_CODE, D.ORDER_CODE, F.ORDER_DESC,"+
                  " F.SPECIFICATION DESCRIPTION,"+
                  " SUM (D.DOSAGE_QTY) AS DOSAGE_QTY,"+
                  " D.DOSAGE_UNIT AS UNIT_CODE, D.OWN_PRICE,"+
                  " SUM (D.TOT_AMT) AS TOT_AMT, '0' AS RETURN_SUM,"+
                  " F.CHARGE_HOSP_CODE, ORDERSET_FLG, 'N' CAT_FLG, D.OWN_RATE,"+
                  " D.INV_CODE, D.INCLUDE_FLG "+
             " FROM IBS_ORDD D, SYS_FEE F "+
             " WHERE D.ORDER_CODE = F.ORDER_CODE "+
              " AND (D.INDV_FLG IS NULL OR D.INDV_FLG = 'N') "+
              " AND D.CAT1_TYPE NOT IN ('LIS', 'RIS') AND "+
             " D.BILL_DATE  BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"', 'YYYYMMDDHH24MISS') "+
           "AND TO_DATE ('"+parm.getValue("E_DATE")+"', 'YYYYMMDDHH24MISS') "+where+
         " GROUP BY D.REXP_CODE,"+
                 "  D.ORDER_CODE,"+
                 "  D.DOSAGE_UNIT,"+
                 " D.OWN_PRICE,"+
                 "  D.INV_CODE,"+
                 "  F.ORDER_DESC,"+
                 "  F.SPECIFICATION,"+
                 "  F.CHARGE_HOSP_CODE,"+
                 "  ORDERSET_FLG,"+
                 "  D.OWN_RATE,"+
                 "  D.INCLUDE_FLG ) A "+
                 "WHERE  A.DOSAGE_QTY<>0 "+
"ORDER BY A.REXP_CODE, A.ORDER_CODE,A.INCLUDE_FLG,A.DOSAGE_QTY ";
		String sql="SELECT   'N' AS FLG, D.REXP_CODE, D.ORDER_CODE, F.ORDER_DESC,"+
                 "  F.SPECIFICATION DESCRIPTION, A.DOSAGE_QTY,"+
                 "  D.DOSAGE_UNIT AS UNIT_CODE,"+
                 "  A.OWN_AMT / A.DOSAGE_QTY AS OWN_PRICE, A.TOT_AMT,"+
                 "  '0' AS RETURN_SUM, F.CHARGE_HOSP_CODE, ORDERSET_FLG,"+
                 "  'Y' CAT_FLG, D.INV_CODE, A.INCLUDE_FLG,A.DOSAGE_QTYDETAIL,A.ORDERSET_GROUP_NO,NUM,D.OWN_RATE "+
            "  FROM IBS_ORDD D,"+
                "   SYS_FEE F, " +
                
                
                "(SELECT   D.CASE_NO_SEQ, D.ORDERSET_CODE, "+
                "CASE WHEN ABS(DOSAGE_QTYDETAIL)/ABS(B.DOSAGE_QTY) >1 THEN SUM (TOT_AMT) ELSE SUM (TOT_AMT)/NUM END TOT_AMT,"+
                " CASE WHEN ABS(DOSAGE_QTYDETAIL)/ABS(B.DOSAGE_QTY) >1 THEN SUM (OWN_AMT) ELSE SUM (OWN_AMT)/NUM END OWN_AMT,"+
                " D.ORDERSET_GROUP_NO, B.DOSAGE_QTY,"+
                " D.INCLUDE_FLG,C.NUM,E.DOSAGE_QTYDETAIL FROM IBS_ORDD D,"+
                " (SELECT DOSAGE_QTY, CASE_NO_SEQ,  ORDERSET_GROUP_NO,  ORDERSET_CODE, CASE_NO "+
                 "   FROM IBS_ORDD D   WHERE INDV_FLG = 'N' AND ORDERSET_CODE IS NOT NULL  AND D.BILL_DATE "+
                   " BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"', 'YYYYMMDDHH24MISS') "+
                 "AND TO_DATE ('"+parm.getValue("E_DATE")+"', 'YYYYMMDDHH24MISS') "+whereOne+ ") B,"+
                 "(SELECT   COUNT (ORDER_CODE) NUM, CASE_NO_SEQ, ORDERSET_GROUP_NO, ORDERSET_CODE, CASE_NO "+
                     " FROM IBS_ORDD D WHERE INDV_FLG = 'Y' AND ORDERSET_CODE IS NOT NULL "+
                      " AND D.BILL_DATE BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"', 'YYYYMMDDHH24MISS') "+
                       "AND TO_DATE ('"+parm.getValue("E_DATE")+"', 'YYYYMMDDHH24MISS') "+whereOne+
                 " GROUP BY CASE_NO_SEQ,  ORDERSET_GROUP_NO,  ORDERSET_CODE,  CASE_NO) C,"+
                " (SELECT DOSAGE_QTY DOSAGE_QTYDETAIL, CASE_NO_SEQ, ORDERSET_GROUP_NO,"+
                        " ORDERSET_CODE, CASE_NO,ORDER_CODE FROM IBS_ORDD D "+
                  " WHERE INDV_FLG = 'Y' AND ORDERSET_CODE IS NOT NULL "+
                    " AND D.BILL_DATE BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"', 'YYYYMMDDHH24MISS') "+
                 "AND TO_DATE ('"+parm.getValue("E_DATE")+"', 'YYYYMMDDHH24MISS') "+whereOne+") E "+
           "WHERE D.CASE_NO = B.CASE_NO AND D.CASE_NO_SEQ = B.CASE_NO_SEQ AND D.ORDERSET_GROUP_NO =  B.ORDERSET_GROUP_NO "+
             "AND D.ORDERSET_CODE = B.ORDERSET_CODE  AND D.CASE_NO = C.CASE_NO AND D.CASE_NO_SEQ = C.CASE_NO_SEQ "+
             "AND D.ORDERSET_GROUP_NO = C.ORDERSET_GROUP_NO AND D.ORDERSET_CODE = C.ORDERSET_CODE "+
             "AND D.CASE_NO = E.CASE_NO  AND D.CASE_NO_SEQ = E.CASE_NO_SEQ AND D.ORDERSET_GROUP_NO = E.ORDERSET_GROUP_NO "+
             "AND D.ORDERSET_CODE = E.ORDERSET_CODE AND D.BILL_DATE BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"', 'YYYYMMDDHH24MISS') "+
             "AND TO_DATE ('"+parm.getValue("E_DATE")+"', 'YYYYMMDDHH24MISS') "+whereOne+
             "AND INDV_FLG = 'Y'  AND D.ORDERSET_CODE IS NOT NULL GROUP BY D.CASE_NO_SEQ, D.ORDERSET_CODE, "+
                 "D.ORDERSET_GROUP_NO,  B.DOSAGE_QTY, D.INCLUDE_FLG, C.NUM, E.DOSAGE_QTYDETAIL ORDER BY ORDERSET_GROUP_NO,ORDERSET_CODE ) "+
                  " A WHERE D.ORDER_CODE = F.ORDER_CODE "+
             "  AND (D.INDV_FLG IS NULL OR D.INDV_FLG = 'N') "+
             " AND D.BILL_DATE  BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"', 'YYYYMMDDHH24MISS') "+
           "AND TO_DATE ('"+parm.getValue("E_DATE")+"', 'YYYYMMDDHH24MISS') "+whereOne+" AND A.TOT_AMT<>0 "+
              " AND D.ORDER_CODE = A.ORDERSET_CODE AND A.ORDERSET_GROUP_NO=D.ORDERSET_GROUP_NO "+
              " AND D.INCLUDE_FLG=A.INCLUDE_FLG "+
         " GROUP BY D.REXP_CODE, "+
                  " D.ORDER_CODE, "+
                  " D.DOSAGE_UNIT,"+
                  " D.INV_CODE,"+
                 "  F.ORDER_DESC,"+
                 "  F.SPECIFICATION,"+
                 "  F.CHARGE_HOSP_CODE,"+
                 "  ORDERSET_FLG,"+
                 "  A.TOT_AMT,"+
                 "  A.DOSAGE_QTY,D.OWN_RATE ,"+
                 "  A.OWN_AMT,"+
                 "  A.INCLUDE_FLG ,A.NUM, A.DOSAGE_QTYDETAIL,A.ORDERSET_GROUP_NO, A.ORDERSET_CODE" +
                 " ORDER BY A.ORDERSET_GROUP_NO, A.ORDERSET_CODE,ABS(A.DOSAGE_QTYDETAIL) ";
		//System.out.println("sql11::::pangben:"+sql1);
		result=new TParm(TJDODBTool.getInstance().select(sql1));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;

		}
		
		TParm orderSetParm=new TParm(TJDODBTool.getInstance().select(sql));
		if (orderSetParm.getErrCode() < 0) {
			err("ERR:" + orderSetParm.getErrCode() + orderSetParm.getErrText()
					+ orderSetParm.getErrName());
			return orderSetParm;

		}
		TParm useParm=new TParm();
		if (orderSetParm.getCount()>0) {//集合医嘱重新统计数据，显示界面使用
			String array[]={"FLG","REXP_CODE","ORDER_CODE","ORDER_DESC","DESCRIPTION","DOSAGE_QTY",
					"UNIT_CODE","OWN_PRICE","TOT_AMT","RETURN_SUM","CHARGE_HOSP_CODE","ORDERSET_FLG","CAT_FLG","OWN_RATE","INV_CODE",
					"INCLUDE_FLG","DOSAGE_QTYDETAIL","ORDERSET_GROUP_NO","NUM"};
			
			TParm exeParm=new TParm();
			String groupNo=orderSetParm.getValue("ORDERSET_GROUP_NO",0);
			//double ownRate=orderSetParm.getDouble("OWN_RATE",0);
			//double dosageQty=orderSetParm.getDouble("DOSAGE_QTY",0);
			int dosageQtyDetail=orderSetParm.getInt("DOSAGE_QTYDETAIL",0);
			TParm temp=new TParm();
			for (int i = 0; i < orderSetParm.getCount(); i++) {
				//去重操作
				if (groupNo.
						equals(orderSetParm.getValue("ORDERSET_GROUP_NO",i))) {//正负抵消的不可以去重，校验数字符号
					temp=orderSetParm.getRow(i);
					if (Math.abs(dosageQtyDetail)<Math.abs(orderSetParm.getInt("DOSAGE_QTYDETAIL",i))) {//去除相同GROUP_NO的集合医嘱
						groupNo=orderSetParm.getValue("ORDERSET_GROUP_NO",i);
						//ownRate=orderSetParm.getDouble("OWN_RATE",i);
						dosageQtyDetail=orderSetParm.getInt("DOSAGE_QTYDETAIL",i);
						if (i==orderSetParm.getCount()-1) {
							setParmValue(exeParm, temp, array);
							break;
						}
						continue;
					}
				}else{
					setParmValue(exeParm, temp, array);
					groupNo=orderSetParm.getValue("ORDERSET_GROUP_NO",i);
					//ownRate=orderSetParm.getDouble("OWN_RATE",i);
					dosageQtyDetail=orderSetParm.getInt("DOSAGE_QTYDETAIL",i);
					temp=orderSetParm.getRow(i);
				}
				if (i==orderSetParm.getCount()-1) {
					setParmValue(exeParm, temp, array);
				}
			}
			exeParm.setCount(exeParm.getCount("ORDERSET_GROUP_NO"));
			
			if (exeParm.getCount("ORDER_CODE")>0) {//汇总相同集合医嘱
				for (int i = 0; i < exeParm.getCount(); i++) {
					if (useParm.getCount("ORDER_CODE")<=0) {
						setParmValue(useParm, exeParm.getRow(i), array);
					}else{
						boolean flg=true;
						for (int j = 0; j < useParm.getCount("ORDER_CODE"); j++) {
							//汇总重复的，将正负和相同的医嘱汇总
							if (useParm.getValue("REXP_CODE",j).equals(exeParm.getValue("REXP_CODE",i))&&
									useParm.getValue("ORDER_CODE",j).equals(exeParm.getValue("ORDER_CODE",i))&&
											useParm.getValue("UNIT_CODE",j).equals(exeParm.getValue("UNIT_CODE",i))&&
													useParm.getDouble("OWN_PRICE",j)==exeParm.getDouble("OWN_PRICE",i)&&
													useParm.getValue("CHARGE_HOSP_CODE",j).equals(exeParm.getValue("CHARGE_HOSP_CODE",i))&&
													useParm.getValue("INCLUDE_FLG",j).equals(exeParm.getValue("INCLUDE_FLG",i))&&
													useParm.getDouble("OWN_RATE",j)==exeParm.getDouble("OWN_RATE",i)&&
													Math.abs(useParm.getInt("DOSAGE_QTYDETAIL",j))==Math.abs(exeParm.getInt("DOSAGE_QTYDETAIL",i))&&
													useParm.getInt("NUM",j)==exeParm.getInt("NUM",i)) {
								useParm.setData("DOSAGE_QTY",j,useParm.getDouble("DOSAGE_QTY",j)+exeParm.getDouble("DOSAGE_QTY",i));
								useParm.setData("TOT_AMT",j,useParm.getDouble("TOT_AMT",j)+exeParm.getDouble("TOT_AMT",i));
								flg=true;
								break;
							}else{
								flg=false;
							}
						}
						if (!flg) {
							setParmValue(useParm, exeParm.getRow(i), array);
						}
					}
				}
			}
			useParm.setCount(useParm.getCount("ORDER_CODE"));
		}
		String array[]={"FLG","REXP_CODE","ORDER_CODE","ORDER_DESC","DESCRIPTION","DOSAGE_QTY",
				"UNIT_CODE","OWN_PRICE","TOT_AMT","RETURN_SUM","CHARGE_HOSP_CODE","ORDERSET_FLG","CAT_FLG","OWN_RATE","INV_CODE",
				"INCLUDE_FLG","ORDERSET_GROUP_NO"};
		for (int i = 0; i < useParm.getCount(); i++) {
			if (useParm.getRow(i).getDouble("DOSAGE_QTY")==0) {
				continue;
			}
			setParmValue(result, useParm.getRow(i), array);
		}
		result.setCount(result.getCount("ORDER_CODE"));
		//result = query("selMergeFee", parm);
		return result;
	}
	private void setParmValue(TParm exeParm,TParm temp,String array[]){
		for (int i = 0; i < array.length; i++) {
			exeParm.addData(array[i], temp.getValue(array[i]));
		}
	}
	/**
	 * 
	* @Title: checkSetOrderDetailParm
	* @Description: TODO(集合医嘱操作:明细页签操作)
	* @author pangben
	* @param caseNo
	* @param resultParm
	* @return
	* @throws
	 */
	private boolean checkSetOrderDetailParm(String caseNo,TParm resultParm,double dosageQty){
		//获得条件查询的集合医嘱数据
		TParm checkParm=getOrderSetDetailParm(caseNo, resultParm);
		if (checkParm.getErrCode()<0) {
			return false;
		}
		String sql="SELECT A.ORDERSET_GROUP_NO,A.TOT_AMT,A.TOT_AMT/B.DOSAGE_QTY OWN_PRICE, A.ORDERSET_CODE, " +
				"A.CASE_NO,A.NUM,B.DOSAGE_QTY,C.DOSAGE_QTYDETAIL,C.ORDER_CODE,B.OWN_RATE FROM "+ 
		 "(SELECT   A.ORDERSET_GROUP_NO, SUM (A.OWN_AMT) TOT_AMT,"+ 
		                          " A.ORDERSET_CODE, A.CASE_NO,"+ 
		                          " COUNT (A.ORDER_CODE) NUM "+ 
		                     " FROM IBS_ORDD A "+ 
		                    " WHERE CASE_NO = '"+caseNo+"' "+ 
		                      " AND A.ORDERSET_CODE = '"+resultParm.getValue("ORDER_CODE")+"' "+ 
		                      " AND A.INDV_FLG = 'Y' "+ 
		                      //" AND A.OWN_RATE ="+ resultParm.getDouble("OWN_RATE") + 
		                      " AND A.INCLUDE_FLG = '"+resultParm.getValue("INCLUDE_FLG")+"' "+ 
		                 " GROUP BY A.ORDERSET_GROUP_NO, A.ORDERSET_CODE, A.CASE_NO) A,"+ 
		 " (SELECT   A.ORDERSET_GROUP_NO,"+ 
		                          " A.ORDERSET_CODE, A.CASE_NO,"+ 
		                         " SUM(DOSAGE_QTY) DOSAGE_QTY,A.OWN_RATE "+ 
		                     " FROM IBS_ORDD A"+ 
		                    " WHERE CASE_NO = '"+caseNo+"' "+ 
		                      " AND A.ORDERSET_CODE ='"+resultParm.getValue("ORDER_CODE")+"' "+ 
		                      " AND A.INDV_FLG = 'N' "+ 
		                      " AND A.OWN_RATE ="+ resultParm.getDouble("OWN_RATE") + 
		                      " AND A.INCLUDE_FLG = '"+resultParm.getValue("INCLUDE_FLG")+"' "+ 
		                 " GROUP BY A.ORDERSET_GROUP_NO, A.ORDERSET_CODE, A.CASE_NO,A.OWN_RATE) B   ,"+ 
		                 " (         SELECT   A.ORDERSET_GROUP_NO, "+ 
		                          " A.ORDERSET_CODE, A.CASE_NO,"+ 
		                         " A.ORDER_CODE,A.DOSAGE_QTY AS DOSAGE_QTYDETAIL "+ 
		                     " FROM IBS_ORDD A "+ 
		                    " WHERE CASE_NO = '"+caseNo+"' "+ 
		                      " AND A.ORDERSET_CODE = '"+resultParm.getValue("ORDER_CODE")+"' "+ 
		                      " AND A.INDV_FLG = 'Y' "+ 
		                     // " AND A.OWN_RATE ="+ resultParm.getDouble("OWN_RATE") +
		                      " AND A.INCLUDE_FLG = '"+resultParm.getValue("INCLUDE_FLG")+"') C "+ 
		                 " WHERE  A.CASE_NO = B.CASE_NO "+ 
		            " AND A.ORDERSET_GROUP_NO = B.ORDERSET_GROUP_NO "+ 
		            " AND A.ORDERSET_CODE =B.ORDERSET_CODE     "+   
		            " AND A.CASE_NO = C.CASE_NO "+ 
		            " AND A.ORDERSET_GROUP_NO = C.ORDERSET_GROUP_NO "+ 
		            " AND A.ORDERSET_CODE =C.ORDERSET_CODE     ORDER BY ORDERSET_GROUP_NO,ORDER_CODE ";
		TParm result=new TParm(TJDODBTool.getInstance().select(sql));
		double exeDosageQty=0.00;
		if (checkParm.getCount()==1) {	
			for (int i = 0; i < result.getCount(); i++) {//查询所有符合条件的医嘱，执行比较工作
				if (checkParm.getInt("NUM",0)==Math.abs(result.getInt("NUM",i))&&//比较细项个数
						checkParm.getDouble("OWN_RATE",0)==Math.abs(result.getDouble("OWN_RATE",i))&&
						checkParm.getInt("DOSAGE_QTYDETAIL",0)/checkParm.getInt("DOSAGE_QTY",0)==Math.abs(result.getInt("DOSAGE_QTYDETAIL",i)/result.getInt("DOSAGE_QTY",i))//比较细项数量
						&&checkParm.getDouble("OWN_PRICE",0)==Math.abs(result.getDouble("OWN_PRICE",i))) {//比较单价
					exeDosageQty+=result.getDouble("DOSAGE_QTY",i);
				}
			}
		}else{
			String groupNo=result.getValue("ORDERSET_GROUP_NO",0);
			TParm exeParm=new TParm();
			//double sumDosageQty=0.00;
			double dosageQtyDemo=0.00;
			for (int i = 0; i < result.getCount(); i++) {//查询所有符合条件的医嘱，执行比较工作
				if (groupNo.equals(result.getValue("ORDERSET_GROUP_NO",i))) {
					for (int j = 0; j < checkParm.getCount(); j++) {
						if (checkParm.getInt("NUM",j)==Math.abs(result.getInt("NUM",i))&&//比较细项个数
								checkParm.getDouble("OWN_RATE",j)==Math.abs(result.getDouble("OWN_RATE",i))&&//比较折扣
								checkParm.getInt("DOSAGE_QTYDETAIL",j)/checkParm.getInt("DOSAGE_QTY",j)==Math.abs(result.getInt("DOSAGE_QTYDETAIL",i)/result.getInt("DOSAGE_QTY",i))&&//比较细项数量
								checkParm.getDouble("OWN_PRICE",j)==Math.abs(result.getDouble("OWN_PRICE",i))&&//比较单价
								checkParm.getValue("ORDER_CODE",j).equals(result.getValue("ORDER_CODE",i))){//比较医嘱代码
							exeParm.addData("FLG", "Y");//比较细项个数
							dosageQtyDemo=result.getDouble("DOSAGE_QTY",i);//获得此操作医嘱数量
							break;
						}
					}	
					if (i==result.getCount()-1) {//最后一行统计可以操作的数量
						if (exeParm.getCount("FLG")==checkParm.getCount()) {
							exeDosageQty+=dosageQtyDemo;
						}
					}
				}else{
					if (exeParm.getCount("FLG")==checkParm.getCount()) {
						exeDosageQty+=dosageQtyDemo;
					}
					dosageQtyDemo=0.00;
					exeParm=new TParm();
					groupNo=result.getValue("ORDERSET_GROUP_NO",i);//重新赋值分组数据
					
					if (i==result.getCount()-1) {
						for (int j = 0; j < checkParm.getCount(); j++) {
							if (checkParm.getInt("NUM",j)==Math.abs(result.getInt("NUM",i))&&//比较细项个数
									checkParm.getDouble("OWN_RATE",j)==Math.abs(result.getDouble("OWN_RATE",i))&&//比较折扣
									checkParm.getInt("DOSAGE_QTYDETAIL",j)/checkParm.getInt("DOSAGE_QTY",j)==Math.abs(result.getInt("DOSAGE_QTYDETAIL",i)/result.getInt("DOSAGE_QTY",i))&&//比较细项数量
									checkParm.getDouble("OWN_PRICE",j)==Math.abs(result.getDouble("OWN_PRICE",i))&&//比较单价
									checkParm.getValue("ORDER_CODE",i).equals(result.getValue("ORDER_CODE",j))){//比较医嘱代码
								exeParm.addData("FLG", "Y");//比较细项个数
								dosageQtyDemo=result.getDouble("DOSAGE_QTY",i);//获得此操作医嘱数量
								break;
							}
						}
						if (exeParm.getCount("FLG")==checkParm.getCount()) {
							exeDosageQty+=dosageQtyDemo;//统计有多少条可以操作的医嘱
						}
					}else{
						for (int j = 0; j < checkParm.getCount(); j++) {
							if (checkParm.getInt("NUM",j)==Math.abs(result.getInt("NUM",i))&&//比较细项个数
									checkParm.getInt("DOSAGE_QTYDETAIL",j)/checkParm.getInt("DOSAGE_QTY",j)==Math.abs(result.getInt("DOSAGE_QTYDETAIL",i)/result.getInt("DOSAGE_QTY",i))&&//比较细项数量
									checkParm.getDouble("OWN_PRICE",j)==Math.abs(result.getDouble("OWN_PRICE",i))&&//比较单价
									checkParm.getValue("ORDER_CODE",j).equals(result.getValue("ORDER_CODE",i))){//比较医嘱代码
								exeParm.addData("FLG", "Y");
								dosageQtyDemo=result.getDouble("DOSAGE_QTY",i);
								break;
							}
						}	
					}
				}
			}
		}
		if (Math.abs(dosageQty) > exeDosageQty) {
			return false;
		}
		return true;
	}
	/**
	 * 
	* @Title: getOrderSetDetailParm
	* @Description: TODO(获得条件查询的集合医嘱数据：公用方法)
	* @author pangben
	* @return
	* @throws
	 */
	private TParm getOrderSetDetailParm(String caseNo,TParm resultParm){
		String checkSql="SELECT A.ORDERSET_GROUP_NO,A.TOT_AMT,A.OWN_AMT,A.OWN_AMT/B.DOSAGE_QTY OWN_PRICE,A.TOT_AMT/B.DOSAGE_QTY TOT_PRICE," +
				" A.ORDERSET_CODE, A.CASE_NO,A.NUM,B.DOSAGE_QTY,C.DOSAGE_QTYDETAIL,C.ORDER_CODE,B.OWN_RATE" +
		" FROM (SELECT   A.ORDERSET_GROUP_NO, SUM (A.TOT_AMT) TOT_AMT,SUM (A.OWN_AMT) OWN_AMT,"+ 
                          " A.ORDERSET_CODE, A.CASE_NO,"+ 
                          " COUNT (A.ORDER_CODE) NUM "+ 
                     " FROM IBS_ORDD A "+ 
                    " WHERE CASE_NO = '"+caseNo+"' "+ 
                      " AND A.ORDERSET_GROUP_NO = '"+resultParm.getValue("ORDERSET_GROUP_NO")+"' "+ 
                      " AND A.INDV_FLG = 'Y'  AND A.ORDERSET_CODE = '"+resultParm.getValue("ORDER_CODE")+"' "+ 
                 " GROUP BY A.ORDERSET_GROUP_NO, A.ORDERSET_CODE, A.CASE_NO) A,"+ 
 " (SELECT   A.ORDERSET_GROUP_NO,"+ 
                          " A.ORDERSET_CODE, A.CASE_NO,"+ 
                         " SUM(DOSAGE_QTY) DOSAGE_QTY,A.OWN_RATE "+ 
                     " FROM IBS_ORDD A "+ 
                    " WHERE CASE_NO = '"+caseNo+"' "+ 
                      " AND A.ORDERSET_GROUP_NO = '"+resultParm.getValue("ORDERSET_GROUP_NO")+"' "+ 
                      " AND A.INDV_FLG = 'N' AND A.ORDERSET_CODE = '"+resultParm.getValue("ORDER_CODE")+"' "+ 
                 " GROUP BY A.ORDERSET_GROUP_NO, A.ORDERSET_CODE, A.CASE_NO,A.OWN_RATE) B   ,"+ 
                 " (         SELECT   A.ORDERSET_GROUP_NO, "+ 
                          " A.ORDERSET_CODE, A.CASE_NO,"+ 
                         " A.ORDER_CODE,A.DOSAGE_QTY AS DOSAGE_QTYDETAIL "+ 
                     " FROM IBS_ORDD A "+ 
                    " WHERE CASE_NO = '"+caseNo+"' "+ 
                      " AND A.ORDERSET_GROUP_NO = '"+resultParm.getValue("ORDERSET_GROUP_NO")+"' "+ 
                      " AND A.INDV_FLG = 'Y' AND A.ORDERSET_CODE = '"+resultParm.getValue("ORDER_CODE")+"' ) C "+ 
                 " WHERE        A.CASE_NO = B.CASE_NO "+ 
            " AND A.ORDERSET_GROUP_NO = B.ORDERSET_GROUP_NO "+ 
            " AND A.ORDERSET_CODE =B.ORDERSET_CODE     "+   
            " AND A.CASE_NO = C.CASE_NO "+ 
            " AND A.ORDERSET_GROUP_NO = C.ORDERSET_GROUP_NO "+ 
            " AND A.ORDERSET_CODE =C.ORDERSET_CODE     ORDER BY ORDERSET_GROUP_NO,ABS(DOSAGE_QTYDETAIL) DESC";

		TParm checkParm=new TParm(TJDODBTool.getInstance().select(checkSql));
		return checkParm;
	}
	/**
	 * 
	* @Title: checkSetOrderSumParm
	* @Description: TODO(集合医嘱退费数据汇总校验:汇总页签操作)
	* @author pangben
	* @param caseNo
	* @param resultParm
	* @param index
	* @param dosageQty
	* @param row
	* @return
	* @throws
	 */
	private boolean checkSetOrderSumParm(String caseNo,TParm resultParm, double dosageQty){
		//根据GROUP_NO 查询符合条件的集合医嘱数据
		
		TParm checkParm=getOrderSetDetailParm(caseNo, resultParm);
		if (checkParm.getErrCode()<0) {
			return false;
		}
		//checkParm 集合中查询单条数据直接获得即可，查询多条数据获得最大DOSAGE_QTYDETAIL 值的数据
		//查询所有符合条件的数据
		String sql="SELECT SUM (A.TOT_AMT) TOT_AMT,SUM (A.OWN_AMT) OWN_AMT, A.OWN_PRICE, A.ORDERSET_CODE, A.CASE_NO,"+
				"A.NUM, SUM (A.DOSAGE_QTY) DOSAGE_QTY FROM (SELECT A.ORDERSET_GROUP_NO,A.TOT_AMT,A.OWN_AMT,A.TOT_AMT/B.DOSAGE_QTY TOT_PRICE,A.OWN_AMT/B.DOSAGE_QTY OWN_PRICE," +
				" A.ORDERSET_CODE, A.CASE_NO,A.NUM,B.DOSAGE_QTY,C.DOSAGE_QTYDETAIL,C.ORDER_CODE FROM "+ 
		 "(SELECT   A.ORDERSET_GROUP_NO, SUM (A.OWN_AMT) OWN_AMT,SUM(A.TOT_AMT) TOT_AMT, "+ 
		                          " A.ORDERSET_CODE, A.CASE_NO,"+ 
		                          " COUNT (A.ORDER_CODE) NUM "+ 
		                     " FROM IBS_ORDD A "+ 
		                    " WHERE CASE_NO = '"+caseNo+"' "+ 
		                      " AND A.ORDERSET_CODE = '"+resultParm.getValue("ORDER_CODE")+"' "+ 
		                      " AND A.INDV_FLG = 'Y' "+ 
		                      //" AND A.OWN_RATE ="+ resultParm.getDouble("OWN_RATE") + 
		                      " AND A.INCLUDE_FLG = '"+resultParm.getValue("INCLUDE_FLG")+"' "+ 
		                 " GROUP BY A.ORDERSET_GROUP_NO, A.ORDERSET_CODE, A.CASE_NO) A,"+ 
		 " (SELECT   A.ORDERSET_GROUP_NO,"+ 
		                          " A.ORDERSET_CODE, A.CASE_NO,"+ 
		                         " SUM(DOSAGE_QTY) DOSAGE_QTY "+ 
		                     " FROM IBS_ORDD A"+ 
		                    " WHERE CASE_NO = '"+caseNo+"' "+ 
		                      " AND A.ORDERSET_CODE ='"+resultParm.getValue("ORDER_CODE")+"' "+ 
		                      " AND A.INDV_FLG = 'N' "+ 
		                      //" AND A.OWN_RATE ="+ resultParm.getDouble("OWN_RATE") + 
		                      " AND A.INCLUDE_FLG = '"+resultParm.getValue("INCLUDE_FLG")+"' "+ 
		                 " GROUP BY A.ORDERSET_GROUP_NO, A.ORDERSET_CODE, A.CASE_NO) B   ,"+ 
		                 " (         SELECT   A.ORDERSET_GROUP_NO, "+ 
		                          " A.ORDERSET_CODE, A.CASE_NO,"+ 
		                         " A.ORDER_CODE,A.DOSAGE_QTY AS DOSAGE_QTYDETAIL "+ 
		                     " FROM IBS_ORDD A "+ 
		                    " WHERE CASE_NO = '"+caseNo+"' "+ 
		                      " AND A.ORDERSET_CODE = '"+resultParm.getValue("ORDER_CODE")+"' "+ 
		                      " AND A.INDV_FLG = 'Y' "+ 
		                      //" AND A.OWN_RATE ="+ resultParm.getDouble("OWN_RATE") +
		                      " AND A.INCLUDE_FLG = '"+resultParm.getValue("INCLUDE_FLG")+"') C "+ 
		                 " WHERE  A.CASE_NO = B.CASE_NO "+ 
		            " AND A.ORDERSET_GROUP_NO = B.ORDERSET_GROUP_NO "+ 
		            " AND A.ORDERSET_CODE =B.ORDERSET_CODE     "+   
		            " AND A.CASE_NO = C.CASE_NO "+ 
		            " AND A.ORDERSET_GROUP_NO = C.ORDERSET_GROUP_NO "+ 
		            " AND A.ORDERSET_CODE =C.ORDERSET_CODE  ) A WHERE  A.OWN_PRICE="
		            +checkParm.getDouble("OWN_PRICE",0)+" AND A.TOT_PRICE="+checkParm.getDouble("TOT_PRICE",0)+" AND A.ORDERSET_CODE='"+
		            checkParm.getValue("ORDERSET_CODE",0)+"' AND ABS(A.DOSAGE_QTYDETAIL)="
		            +Math.abs(checkParm.getInt("DOSAGE_QTYDETAIL",0))+" AND A.NUM="+checkParm.getInt("NUM",0)+
		            " GROUP BY A.OWN_PRICE, A.ORDERSET_CODE, A.CASE_NO, A.NUM ";
		TParm result=new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode()<0) {
			return false;
		}
		if (Math.abs(dosageQty) > result.getDouble("DOSAGE_QTY",0)) {
			return false;
		}
		return true;
	}
	/**
	 * 
	* @Title: returnCheckSum
	* @Description: TODO(细项退费校验:汇总页签操作)
	* @author pangben
	* @param caseNo
	* @param resultParm
	* @param index
	* @param dosageQty
	* @param row
	* @return
	* @throws
	 */
	public boolean returnCheckSum(String caseNo,TParm resultParm,double dosageQty){
		if (null!=resultParm.getValue("CAT_FLG")&&resultParm.getValue("CAT_FLG").equals("Y")) {
			if (!checkSetOrderSumParm(caseNo, resultParm, dosageQty)) {
				return false;
			}else{
				return true;
			}
		}else{
			String selQtySql = " SELECT SUM(DOSAGE_QTY) AS DOSAGE_QTY,ORDER_CODE "
				+ "  FROM IBS_ORDD  WHERE ORDER_CODE = '"
				+ resultParm.getValue("ORDER_CODE") + "' "
				+ "    AND CASE_NO = '" + caseNo + "' AND INCLUDE_FLG='"+resultParm.getValue("INCLUDE_FLG")+"' AND OWN_RATE="
				+ resultParm.getDouble("OWN_RATE") + " AND OWN_PRICE="+resultParm.getDouble("OWN_PRICE")+"   GROUP BY ORDER_CODE ";
			TParm selQtyParm = new TParm(TJDODBTool.getInstance().select(selQtySql));
			double dosageQtyTot = selQtyParm.getDouble("DOSAGE_QTY", 0);
			if (Math.abs(dosageQty) > dosageQtyTot) {
				return false;
			}
		}
		return true;
	}
	/**
	 * 
	* @Title: returnCheckDetail
	* @Description: TODO(细项退费校验:明细页签操作)
	* @author pangben
	* @param caseNo
	* @param resultParm
	* @param index
	* @param dosageQty
	* @param row
	* @return
	* @throws
	 */
	public boolean returnCheckDetail(String caseNo,TParm resultParm,double dosageQty){
		String selQtySql="";
		if (resultParm.getValue("ORDER_CODE").equals(resultParm.getValue("ORDERSET_CODE"))&&
				resultParm.getValue("INDV_FLG").equals("N")) {
			if (!checkSetOrderDetailParm(caseNo, resultParm, dosageQty)) {
				return false;
			}else{
				return true;
			}
		}else{
			selQtySql = " SELECT SUM(DOSAGE_QTY) AS DOSAGE_QTY,ORDER_CODE "
				+ "  FROM IBS_ORDD  WHERE ORDER_CODE = '"
				+ resultParm.getValue("ORDER_CODE") + "' "
				+ "    AND CASE_NO = '" + caseNo + "' AND INCLUDE_FLG='"+resultParm.getValue("INCLUDE_FLG")+"' AND OWN_RATE="
				+ resultParm.getDouble("OWN_RATE") + " AND OWN_PRICE="+resultParm.getDouble("OWN_PRICE")+"   GROUP BY ORDER_CODE ";
			TParm selQtyParm = new TParm(TJDODBTool.getInstance().select(selQtySql));
			double dosageQtyTot = selQtyParm.getDouble("DOSAGE_QTY", 0);
			if (Math.abs(dosageQty) > dosageQtyTot) {
				return false;
			}
		}
		return true;
	}
}
