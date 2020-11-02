package action.inw;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import action.inw.client.SpcInwCheckDto;
import action.inw.client.SpcInwCheckDtos;
import action.inw.client.SpcOdiDspnm;
import action.inw.client.SpcOdiDspnms;
import action.inw.client.SpcOdiService_SpcOdiServiceImplPort_Client;
import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import jdo.inw.InwOrderCheckTool;
import jdo.inw.InwOrderExecTool;
import jdo.sys.SystemTool;

/**
 * <p>
 * Title: 住院护士站审核Action
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: JAVAHIS
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author ZangJH 2009-10-30
 * @version 1.0
 */
public class InwOrderCheckAction extends TAction {
	public InwOrderCheckAction() {
	}

	/**
	 * 展开入口
	 * 
	 * @param parm
	 * @return TParm
	 */
	public TParm onSave(TParm parm) {

		TParm result = new TParm();
		// 创建一个连接，在多事物的时候连接各个操作使用
		TConnection connection = getConnection();
		// 调用长期处置的展开方法
		try {
			result = InwOrderCheckTool.getInstance().onCheck(parm, connection);
//			System.out.println("============="+result);
			if(result.getErrCode()<0){
				connection.rollback();
			}
		} catch (Exception e) {
			connection.rollback();
			e.printStackTrace();
		} finally {
			connection.close();
			return result;
		}
	}

	/**
	 * 取消审核展开入口
	 * 
	 * @param parm
	 * @return TParm
	 */
	public TParm onUndoSave(TParm parm) {
		TParm result = new TParm();
		// 创建一个连接，在多事物的时候连接各个操作使用
		TConnection connection = getConnection();
		// 调用长期处置的展开方法
		result = InwOrderCheckTool.getInstance().onUndoCheck(parm, connection);
		if (result.getErrCode() < 0) {
			connection.rollback();
			connection.close();
			return result;
		}
		connection.commit();
		connection.close();
		return result;
	}
	/**
	 * 得库存数量
	 * @param parm
	 * @return
	 */
    public TParm getIndQty(TParm  parm){
    	TParm  result=new TParm();
    	double qty=0;
    	try {
    		 SpcInwCheckDto dto=new SpcInwCheckDto();
             dto.setOrderCode(parm.getValue("ORDER_CODE"));
             List<SpcInwCheckDto> dtoList =new ArrayList<SpcInwCheckDto>();
             dtoList.add(dto);
             SpcInwCheckDtos dtos = new SpcInwCheckDtos();
             dtos.setRegionCode(parm.getValue("REGION_CODE"));
             dtos.setStationCode(parm.getValue("STATION_CODE"));
             dtos.setSpcInwCheckDtos(dtoList);
             SpcInwCheckDtos returnDto = SpcOdiService_SpcOdiServiceImplPort_Client.inwCheck(dtos);
             List<SpcInwCheckDto> dtoReturnList = returnDto.getSpcInwCheckDtos();
             SpcInwCheckDto rDto = (SpcInwCheckDto)dtoReturnList.get(0);  
             qty=dtoReturnList.get(0).getQty();
//             System.out.println(parm.getValue("ORDER_CODE")+" "+parm.getValue("STATION_CODE")+qty);
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("护士审核校验库存WS失败");
			System.out.println(e);
			result.setErrCode(-1);
			result.setErrText("调用物联网WS失败");
		}
        result.setData("QTY", qty);
    	return result;
    }
    /**
     * 智能柜保存调用国药(暂时只针对临时)
     * @param parm
     * @return
     */
    public TParm onSendGYPha(TParm  parm){
    	TParm result=new TParm();
    	String type=parm.getValue("TYPE");
//    	System.out.println("入参===========parm======"+parm);
    	try {
    		Map pat = InwOrderExecTool.getInstance().groupByPatParm(parm);
    		Iterator it = pat.values().iterator();
    		while (it.hasNext()) {
        		SpcOdiDspnms dspnms=new SpcOdiDspnms();
        		List<SpcOdiDspnm> spcOdiDspnms =new ArrayList<SpcOdiDspnm>();
    			TParm patParm = (TParm) it.next();
    			for (int i = 0; i < patParm.getCount(); i++) {
    				String caseNo=patParm.getValue("CASE_NO",i);
    				String orderNo=patParm.getValue("ORDER_NO",i);
    				String orderSeq=patParm.getValue("ORDER_SEQ",i);
    				String sql="SELECT CASE_NO,ORDER_NO,ORDER_SEQ,START_DTTM,END_DTTM,REGION_CODE,"+
    				"STATION_CODE,DEPT_CODE,VS_DR_CODE,BED_NO,MR_NO,DSPN_KIND,"+
    				"DSPN_DATE,DSPN_USER,ORDER_CAT1_CODE,CAT1_TYPE,ORDER_CODE,"+
    				"ORDER_DESC,GOODS_DESC,SPECIFICATION,MEDI_QTY,MEDI_UNIT,"+
    				"FREQ_CODE,ROUTE_CODE,DOSAGE_QTY,DOSAGE_UNIT,DISPENSE_QTY,"+
    				"DISPENSE_UNIT,PHA_DISPENSE_NO,TAKEMED_ORG,TAKEMED_NO,OPT_USER,"+
    				"OPT_DATE,OPT_TERM,CTRLDRUGCLASS_CODE FROM ODI_DSPNM WHERE CASE_NO='"+caseNo+"'" 
    				+" AND ORDER_NO='"+orderNo+"' AND ORDER_SEQ='"+orderSeq+"'"; 				
//    				System.out.println("SQL=========="+sql);
    				TParm selParm=new TParm(TJDODBTool.getInstance().select(sql));
    				if(selParm.getCount()<0)
    					continue;
    				TParm dspnmParm=selParm.getRow(0);
    				SpcOdiDspnm dspnm=new SpcOdiDspnm();
    				dspnm.setCaseNo(dspnmParm.getValue("CASE_NO"));
    				dspnm.setOrderNo(dspnmParm.getValue("ORDER_NO"));
    				dspnm.setOrderSeq(dspnmParm.getInt("ORDER_SEQ"));
    				dspnm.setStartDttm(dspnmParm.getValue("START_DTTM"));
    				dspnm.setEndDttm(dspnmParm.getValue("END_DTTM"));
    				dspnm.setRegionCode(dspnmParm.getValue("REGION_CODE"));
    				dspnm.setStationCode(dspnmParm.getValue("STATION_CODE"));
    				dspnm.setDeptCode(dspnmParm.getValue("DEPT_CODE"));
    				dspnm.setVsDrCode(dspnmParm.getValue("VS_DR_CODE"));
    				dspnm.setBedNo(dspnmParm.getValue("BED_NO"));
    				dspnm.setMrNo(dspnmParm.getValue("MR_NO"));
    				dspnm.setDspnKind(dspnmParm.getValue("DSPN_KIND"));
    				dspnm.setDspnDate(dspnmParm.getValue("DSPN_DATE"));
    				dspnm.setDspnUser(dspnmParm.getValue("DSPN_USER"));
    				dspnm.setOrderCat1Code(dspnmParm.getValue("ORDER_CAT1_CODE"));
    				dspnm.setCat1Type(dspnmParm.getValue("CAT1_TYPE"));
    				dspnm.setOrderCode(dspnmParm.getValue("ORDER_CODE"));
    				dspnm.setOrderDesc(dspnmParm.getValue("ORDER_DESC"));
    				dspnm.setGoodsDesc(dspnmParm.getValue("GOODS_DESC"));
    				dspnm.setSpecification(dspnmParm.getValue("SPECIFICATION"));
    				dspnm.setMediQty(dspnmParm.getDouble("MEDI_QTY"));
    				dspnm.setMediUnit(dspnmParm.getValue("MEDI_UNIT"));
    				dspnm.setFreqCode(dspnmParm.getValue("FREQ_CODE"));
    				dspnm.setRouteCode(dspnmParm.getValue("ROUTE_CODE"));
    				dspnm.setDosageQty(dspnmParm.getDouble("DOSAGE_QTY"));
    				dspnm.setDosageUnit(dspnmParm.getValue("DOSAGE_UNIT"));
    				dspnm.setDispenseQty(dspnmParm.getDouble("DISPENSE_QTY"));
    				dspnm.setDispenseUnit(dspnmParm.getValue("DISPENSE_UNIT"));
    				dspnm.setPhaDispenseNo(dspnmParm.getValue("PHA_DISPENSE_NO"));
    				dspnm.setTakemedOrg(dspnmParm.getValue("TAKEMED_ORG"));
    				dspnm.setTakemedNo(dspnmParm.getValue("TAKEMED_NO"));
    				dspnm.setOptUser(dspnmParm.getValue("OPT_USER"));
    				dspnm.setOptDate(dspnmParm.getValue("OPT_DATE"));
    				dspnm.setOptTerm(dspnmParm.getValue("OPT_TERM"));
    				dspnm.setCtrldrugclassCode(dspnmParm.getValue("CTRLDRUGCLASS_CODE"));
//    				System.out.println("CtrldrugclassCode======"+dspnm.getCtrldrugclassCode());
    				spcOdiDspnms.add(dspnm);	
    			}
    			dspnms.setSpcOdiDspnms(spcOdiDspnms);
    			String str=SpcOdiService_SpcOdiServiceImplPort_Client.OnSaveIndCabdspn(dspnms,type);
    			
//    			System.out.println("结果======"+str);
    			if(!str.toLowerCase().toString().equals("success")){
    				result.setErrCode(-1);
    				result.setErrText("调用物联网WS失败"+str);
    			}
    		}
		} catch (Exception e) {
			// TODO: handle exception
			result.setErrCode(-1);
			result.setErrText("调用物联网WS异常");
		}
    	return result;	
    }
    
    /**
     * 术中医嘱审核
     * @param parm
     * @return
     */
    public TParm onCheckOP(TParm parm) { // wanglong add 20140707
        TParm result = new TParm();
        TConnection connection = getConnection();
        //add by yangjj 20150625
        TParm dataParm = parm.getParm("dataParm");
        
        TParm dataParm1 = new TParm();
        TParm d1 = new TParm();
        dataParm1.setData("OPT_USER", parm.getData("OPT_USER"));
        dataParm1.setData("OPT_DATE", parm.getData("OPT_DATE"));
        dataParm1.setData("OPT_TERM", parm.getData("OPT_TERM"));
        TParm dataParm2 = new TParm();
        for(int i = 0 ; i < dataParm.getCount() ; i++){
        		d1.addRowData(dataParm, i);
        }
        
        dataParm1.setData("dataParm", d1.getData());
        
        if(dataParm1 != null&&d1.getCount()>0){
        	result = InwOrderCheckTool.getInstance().onCheckOPOrder(parm, connection);//审核医嘱
            if (result == null || result.getErrCode() < 0) {
                connection.rollback();
                connection.close();
                return result;
            }
            
            String sql = "SELECT * FROM ODI_ORDER WHERE 1 <> 1";
            TParm feeParm = new TParm(TJDODBTool.getInstance().select(sql));
            if (feeParm.getErrCode() < 0) {
                connection.rollback();
                connection.close();
                return feeParm;
            }
            for (int i = 0; i < d1.getCount(); i++) {
                if (!d1.getValue("ORDER_CODE", i).equals(d1.getValue("ORDERSET_CODE", i))) {
                    String orderSql =
                            "SELECT * FROM ODI_ORDER WHERE CASE_NO='#' AND ORDER_NO='#' AND ORDER_SEQ=#";
                    orderSql = orderSql.replaceFirst("#", d1.getValue("CASE_NO", i));
                    orderSql = orderSql.replaceFirst("#", d1.getValue("ORDER_NO", i));
                    orderSql = orderSql.replaceFirst("#", d1.getValue("ORDER_SEQ", i));
                    TParm rowParm = new TParm(TJDODBTool.getInstance().select(orderSql));
                    if (rowParm.getErrCode() < 0) {
                        connection.rollback();
                        connection.close();
                        return rowParm;
                    }
                    feeParm.addRowData(rowParm, 0);// rowParm只有一行
                } else {
                    String ordersSql =
                            "SELECT * FROM ODI_ORDER WHERE CASE_NO='#' AND ORDER_NO='#' AND ORDERSET_GROUP_NO=# ORDER BY ORDER_SEQ";
                    ordersSql = ordersSql.replaceFirst("#", d1.getValue("CASE_NO", i));
                    ordersSql = ordersSql.replaceFirst("#", d1.getValue("ORDER_NO", i));
                    ordersSql = ordersSql.replaceFirst("#", d1.getValue("ORDERSETGROUP_NO", i));
                    TParm rowsParm = new TParm(TJDODBTool.getInstance().select(ordersSql));
                    if (rowsParm.getErrCode() < 0) {
                        connection.rollback();
                        connection.close();
                        return rowsParm;
                    }
                    for (int j = 0; j < rowsParm.getCount(); j++) {
                        feeParm.addRowData(rowsParm, j);// rowsParm包含集合医嘱的主细项
                    }
                }
            }
            //=========pangben 2015-9-14 修改开单科室
            for (int i = 0; i < feeParm.getCount("ORDER_CODE"); i++) {
            	 feeParm.setData("ORDER_DEPT_CODE", i, feeParm.getValue("DEPT_CODE",i));
			}
            feeParm.setData("DATA_TYPE", 6);// 术中医嘱为6
            feeParm.setData("FLG", "ADD");
            
            result = InwOrderCheckTool.getInstance().onFee(feeParm, connection);//计费，写IBS_ORDM，IBS_ORDD
            if (result == null || result.getErrCode() < 0) {
                connection.rollback();
                connection.close();
                return result;
            }
        }
        connection.commit();
        connection.close();
        return result;
    }
    
    /**
     * 术中医嘱取消审核
     * @param parm
     * @return
     */
    public TParm onUndoCheckOP(TParm parm) { // wanglong add 20140707
        TParm result = new TParm();
        TConnection connection = getConnection();
        result = InwOrderCheckTool.getInstance().onUndoCheckOPOrder(parm, connection);//无效审核医嘱
        if (result.getErrCode() < 0) {
            connection.rollback();
            connection.close();
            return result;
        }
        TParm dataParm = parm.getParm("dataParm");
        TParm dataParm1 = new TParm();
        TParm d1 = new TParm();
        dataParm1.setData("OPT_USER", parm.getData("OPT_USER"));
        dataParm1.setData("OPT_DATE", parm.getData("OPT_DATE"));
        dataParm1.setData("OPT_TERM", parm.getData("OPT_TERM"));
        
        
        
        for(int i = 0 ; i < dataParm.getCount() ; i++){
        	String orderNo = dataParm.getValue("ORDER_NO", i);
        	String orderSeq = dataParm.getValue("ORDER_SEQ", i);
        	String caseNo = dataParm.getValue("CASE_NO", i);
        	String s1 = "SELECT A.CLASSIFY FROM SYS_DEPT A , ODI_ORDER B " +
        			" WHERE A.DEPT_CODE = B.EXEC_DEPT_CODE AND B.ORDER_NO = '"+orderNo+"' AND B.ORDER_SEQ = '"+orderSeq+"' AND CASE_NO = '"+caseNo+"'";
        	TParm r = new TParm(TJDODBTool.getInstance().select(s1));
        	//药品
        	d1.addRowData(dataParm, i);
        }
        
        dataParm1.setData("dataParm", d1.getData());
        
        if(dataParm1 != null&&d1.getCount()>0){
        String sql = "SELECT * FROM ODI_ORDER WHERE 1 <> 1";
        TParm feeParm = new TParm(TJDODBTool.getInstance().select(sql));
        if (feeParm.getErrCode() < 0) {
            connection.rollback();
            connection.close();
            return feeParm;
        }
        for (int i = 0; i < d1.getCount(); i++) {
            if (!d1.getValue("ORDER_CODE", i).equals(d1.getValue("ORDERSET_CODE", i))) {
                String orderSql =
                        "SELECT * FROM ODI_ORDER WHERE CASE_NO='#' AND ORDER_NO='#' AND ORDER_SEQ=#";
                orderSql = orderSql.replaceFirst("#", d1.getValue("CASE_NO", i));
                orderSql = orderSql.replaceFirst("#", d1.getValue("ORDER_NO", i));
                orderSql = orderSql.replaceFirst("#", d1.getValue("ORDER_SEQ", i));
                TParm rowParm = new TParm(TJDODBTool.getInstance().select(orderSql));
                if (rowParm.getErrCode() < 0) {
                    connection.rollback();
                    connection.close();
                    return rowParm;
                }
                feeParm.addRowData(rowParm, 0);// rowParm只有一行
            } else {
                String ordersSql =
                        "SELECT * FROM ODI_ORDER WHERE CASE_NO='#' AND ORDER_NO='#' AND ORDERSET_GROUP_NO=# ORDER BY ORDER_SEQ";
                ordersSql = ordersSql.replaceFirst("#", d1.getValue("CASE_NO", i));
                ordersSql = ordersSql.replaceFirst("#", d1.getValue("ORDER_NO", i));
                ordersSql = ordersSql.replaceFirst("#", d1.getValue("ORDERSET_GROUP_NO", i));
                TParm rowsParm = new TParm(TJDODBTool.getInstance().select(ordersSql));
                
                System.out.println("rowsParm:"+rowsParm);
                if (rowsParm.getErrCode() < 0) {
                    connection.rollback();
                    connection.close();
                    return rowsParm;
                }
                for (int j = 0; j < rowsParm.getCount(); j++) {
                    feeParm.addRowData(rowsParm, j);// rowsParm包含集合医嘱的主细项
                }
            }
        }
        feeParm.setData("DATA_TYPE", 6);// 术中医嘱为6
        feeParm.setData("FLG", "SUB");
        
        result = InwOrderCheckTool.getInstance().onFee(feeParm, connection);//退费，写IBS_ORDM，IBS_ORDD
        if (result == null || result.getErrCode() < 0) {
            connection.rollback();
            connection.close();
            return result;
        }
        }
        connection.commit();
        connection.close();
        return result;
    }
}
