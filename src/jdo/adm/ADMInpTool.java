package jdo.adm;

import java.sql.Timestamp;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.sys.SYSBedTool;
import com.dongyang.util.StringTool;

import jdo.bil.BILPayTool;
import jdo.ibs.IBSLumpWorkBatchNewTool;
import jdo.ibs.IBSLumpWorkBatchTool;
import jdo.ibs.IBSNewTool;
import jdo.ibs.IBSOrdmTool;
import jdo.ibs.IBSTool;
import jdo.sys.SYSDiagnosisTool;
import jdo.mem.MEMTool;
import jdo.mro.MRORecordTool;
import com.dongyang.jdo.TJDODBTool;

import jdo.sys.Operator;
import jdo.sys.SYSChargeHospCodeTool;
import jdo.sys.SYSEmrIndexTool;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

/**
 * <p>Title:住院登记 </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author JiaoY
 * @version 1.0
 */
public class ADMInpTool extends TJDOTool {
    /**
     * 实例
     */
    public static ADMInpTool instanceObject;
    /**
     * 得到实例
     * @return SchWeekTool
     */
    public static ADMInpTool getInstance() {
        if (instanceObject == null)
            instanceObject = new ADMInpTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public ADMInpTool() {
        setModuleName("adm\\ADMInpModule.x");
        onInit();
    }

    /**
     * insert ADM_INP
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm insertdata(TParm parm, TConnection conn) {
        TParm result = new TParm();
        result = update("insertForInp", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * insert ADM_INP
     * @param parm TParm
     * @return TParm
     */
    public TParm insertdata(TParm parm) {
        TParm result = new TParm();
        result = update("insertForInp", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 查询全字段
     * @param parm TParm
     * @return TParm
     */
    public TParm selectall(TParm parm) {
        TParm result = new TParm();
        result = query("selectall", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    //============================== add  by chenxi 20130228
    /**
     * 查询预约床号
     * @param parm TParm    
     * @return TParm
     */
    public TParm selectBedNo(TParm parm) {
        TParm result = new TParm();
        result = query("selectBedNo", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 检查病患是否住院
     * @param parm TParm
     * @return TParm
     */
    public TParm checkAdmInp(TParm parm) {
        TParm result = query("checkAdmInp", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 查询在院病患
     * @param parm TParm
     * @return TParm
     */
    public TParm queryInStation(TParm parm) {
        TParm result = query("queryInStation", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;

    }

    /**
     * 查询上一次出院时间
     * @param parm TParm
     * @return TParm
     */
    public TParm queryLastDsdate(TParm parm) {
        TParm result = query("SelectLastDsDate", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;

    }

    /**
     * 更新在院病患床位号
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm updateForWait(TParm parm, TConnection conn) {
        TParm result = new TParm();
        result = update("updateForWait", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 更新病患三级检诊信息
     * @param parm TParm
     * @return TParm
     */
    public TParm updateForWaitPat(TParm parm) {
        TParm result = new TParm();
        result = update("updateForWaitPat", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 更新病患三级检诊信息
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm updateForWaitPat(TParm parm, TConnection conn) {
        TParm result = new TParm();
        result = update("updateForWaitPat", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 更新在院病患床位号
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm updateForOutDept(TParm parm, TConnection conn) {
        TParm result = new TParm();
        result = update("updateForOutDept", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 出院
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm outAdmInp(TParm parm, TConnection conn) {
        TParm result = new TParm();
        result = update("outAdmInp", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 床位检索
     * @param parm TParm
     * @return TParm
     */
    public TParm QueryBed(TParm parm) {
        TParm result = new TParm();
        result = query("queryBed", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;

    }

    /**
     * 查询在院病患的case_no
     * 查询在院病患的基本信息
     * 就诊序号,住院号,病案号,科室,病区/护士站
     * 病床,身份一,来源别（O：门诊；E：急诊；I：住院）,入院日期,门急诊医生,
     * 经治医生,主治医生,目前住院天数,红色警戒,黄色警戒,预交金
     * 参数（全部为活参）：MR_NO；IPD_NO；CASE_NO
     * @param parm TParm
     * @return TParm
     */
    public TParm queryCaseNo(TParm parm) {
        TParm result = new TParm();
        result = query("queryCaseNo", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;

    }
    /**
     * 查询在院病患的case_no
     * 查询在院病患的基本信息
     * 就诊序号,住院号,病案号,科室,病区/护士站
     * 病床,身份一,来源别（O：门诊；E：急诊；I：住院）,入院日期,门急诊医生,
     * 经治医生,主治医生,目前住院天数,红色警戒,黄色警戒,预交金
     * 参数（全部为活参）：MR_NO；IPD_NO；CASE_NO
     * @param parm TParm
     * @return TParm
     * ==============pangben 2014-6-3 添加去掉出院日期条件查询
     */
    public TParm queryCaseNoBilPay(TParm parm) {
        TParm result = new TParm();
        result = query("queryCaseNoBilPay", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;

    }
    
    /**
     * 修改黄色警戒，红色警戒
     * @param parm TParm
     * @return TParm
     */
    public TParm updateYellowRed(TParm parm) {
        TParm result = new TParm();
        result = update("updateYellowRed", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;

    }

    /**
     * 修改黄色警戒，红色警戒,入院日期，住院次数（等表架构修改）
     * @param parm TParm
     * @return TParm
     */
    public TParm updateForAdmInp(TParm parm) {
        TParm result = new TParm();
        result = update("updateForAdmInp", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;

    }

    /**
     * 修改黄色警戒，红色警戒,入院日期，住院次数
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm updateForAdmInp(TParm parm, TConnection conn) {
        TParm result = new TParm();
        result = update("updateForAdmInp", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;

    }


    /**
     * 更新预交金
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updateForBillPay(TParm parm, TConnection connection) {
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "ADMInpTool.updateForBillPay()参数异常!");
            return result;
        }
        result = update("updateForBillPay", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;

        }
        return result;

    }

    /**
     * 取消住院
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm updateForCancel(TParm parm, TConnection conn) {
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "参数异常!");
            return result;
        }
        result = update("updateForCancel", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;

        }
        return result;

    }

    /**
     * 修改病患身高体重
     * 传参CASE_NO
     * @param parm TParm
     * @return TParm
     */
    public TParm upDateWeightHigh(TParm parm) {
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "参数异常!");
            return result;
        }
        result = update("upDateWeightHigh", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;

        }
        return result;
    }

    /**
     * 修改病患身高体重
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm upDateWeightHigh(TParm parm, TConnection conn) {
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "参数异常!");
            return result;
        }
        result = update("upDateWeightHigh", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;

        }
        return result;
    }

    /**
     * 检核病患是否在床
     * @param parm TParm
     * @return TParm
     */
    public TParm checkInBed(TParm parm) {
        TParm result = new TParm();
        result = query("queryCaseNo", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 入院登记
     * 1，插入住院主档（ADM_INP）
     * 2，插入代转病患档
     * 3，插入病患动态档
     * 4，更新床位档
     * 5，更新预约信息ADM_RESV
     * 6，插入病历主索引
     * 7，修改病患基本信息中的最近住院科室及时间
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm insertADMData(TParm parm, TConnection conn) {
//    	System.out.println("-----0-----");
        String MR_NO = parm.getValue("MR_NO");
        String CASE_NO = parm.getValue("CASE_NO");
        String OPT_USER = parm.getValue("OPT_USER");
        String OPT_TERM = parm.getValue("OPT_TERM");
        String IPD_NO = parm.getValue("IPD_NO");
		parm.setData("IN_DATE", SystemTool.getInstance().getDate());
        //插入病患动态档参数
        TParm admChg = loadAdmChg(parm);
        //删除待转档参数
        TParm waitDelParm = new TParm();
        waitDelParm.setData("CASE_NO", CASE_NO);
        //插入代转病患档 参数
        TParm waitParm = new TParm();
        waitParm.setData("CASE_NO", CASE_NO);
        waitParm.setData("MR_NO", MR_NO);
        waitParm.setData("IPD_NO", IPD_NO);
        waitParm.setData("DEPT_CODE", parm.getValue("DEPT_CODE"));
        waitParm.setData("STATION_CODE", parm.getValue("STATION_CODE"));
        waitParm.setData("OPT_USER", OPT_USER);
        waitParm.setData("OPT_TERM", OPT_TERM);
        //更新床位档 参数
        TParm bedParm = new TParm();
        bedParm.setData("MR_NO", MR_NO);
        bedParm.setData("CASE_NO", CASE_NO);
        bedParm.setData("IPD_NO", IPD_NO);
        bedParm.setData("OPT_USER", OPT_USER);
        bedParm.setData("OPT_TERM", OPT_TERM);
        bedParm.setData("BED_NO", parm.getValue("BED_NO"));
        bedParm.setData("APPT_FLG", "N"); //预约标记
        bedParm.setData("ALLO_FLG", "Y"); //入住标记
        bedParm.setData("BED_STATUS", "0");
        //预约查询 参数
        TParm selectResv = new TParm();
        selectResv.setData("MR_NO", MR_NO);
        selectResv.setData("RESV_NO", parm.getValue("RESV_NO"));//====pangben 2014-8-1
        //修改预约信息 参数
        TParm resvParm = new TParm();
        resvParm.setData("CASE_NO", CASE_NO);
        resvParm.setData("IN_DATE",parm.getTimestamp("IN_DATE").toString()
        		.replaceAll("-", "").replaceAll(" ", "").replaceAll(":", "").substring(0, 14));
        resvParm.setData("BED_NO", parm.getValue("BED_NO"));
        resvParm.setData("OPT_USER", OPT_USER);
        resvParm.setData("OPT_TERM", OPT_TERM);
        resvParm.setData("MR_NO", MR_NO);
        TParm result = new TParm();
//        System.out.println("333333==="+parm.getValue("LUMPWORK_FLG"));
        if (null!=parm.getValue("LUMPWORK_FLG")//包干套餐逻辑添加，预约住院已经操作添加ADM_INP操作，这里只能执行修改操作
        		&&parm.getValue("LUMPWORK_FLG").equals("Y")) {
        	result = ADMInpTool.getInstance().updateAmdInpByLumpworkCaseNo(parm, conn);
		}else{
			//1，插入住院主档
			result = ADMInpTool.getInstance().inHosptal(parm, conn);
		}
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        } 
        if (null!=parm.getValue("LUMPWORK_FLG")//正常办理住院将IPD_NO更新BIL_PAY表
        		&&parm.getValue("LUMPWORK_FLG").equals("Y")) {
        	result =BILPayTool.getInstance().updateBilPayIpdNo(parm, conn);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
        }
//        System.out.println("1");
        //2，插入待转病患档
        //先删除病患待转档
        result = ADMWaitTransTool.getInstance().deleteIn(waitDelParm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
//        System.out.println("2");
        if(!containsAny(MR_NO, "-")){//非新生儿 duzhw update 20140327
        	//插入代转病患档
            result = ADMWaitTransTool.getInstance().saveForInp(waitParm, conn);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText() +
                    result.getErrName());
                return result;
            }
        }
//        System.out.println("3");
        //插入病患动态档
        result = ADMChgTool.getInstance().insertAdmChg(admChg, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }      
      
        //更新套餐主档、时程和明细医嘱的套餐使用状态used_flg--xiongwg20150703
		if (parm.getParm("parmL") != null
				&&( parm.getParm("parmL").getValue("PACKAGE_CODE").length() > 0)) {
			TParm memParm = parm.getParm("parmL");
			memParm.setData("OPT_USER",parm.getValue("OPT_USER"));
			memParm.setData("OPT_TERM",parm.getValue("OPT_TERM"));
			result = MEMTool.getInstance().upMemUsedFlg(memParm, conn);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
		}     
//        System.out.println("4");
        // 更新床位档        
        //=========  chenxi  modify  住院登记时不更新床位档
//        if (!"".equals(bedParm.getValue("BED_NO"))) {
//            // 更新床位档
//            result = SYSBedTool.getInstance().upDate(bedParm, conn);
//            if (result.getErrCode() < 0) {
//                err("ERR:" + result.getErrCode() + result.getErrText() +
//                    result.getErrName());
//                return result;
//            }
//        }
        //=========   住院登记时，有预约床位时，更新appt_flg 为y   chenxi  modify 20130301
        if (!"".equals(bedParm.getValue("BED_NO"))) {
            TParm upBedAttp = new TParm();
            upBedAttp.setData("BED_NO", bedParm.getValue("BED_NO"));
            upBedAttp.setData("APPT_FLG", "Y");   
            upBedAttp.setData("OPT_USER", OPT_USER);
            upBedAttp.setData("OPT_TERM", OPT_TERM);
            result = SYSBedTool.getInstance().upDateForResv(upBedAttp, conn);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText() +
                    result.getErrName());
                return result;
            }
        }
//        System.out.println("5");
        //查询是否有预约信息
        TParm resvNo = ADMResvTool.getInstance().selectAll(selectResv);
        //=========================  chenxi modify 20130311 
        if(resvNo.getValue("BED_NO", 0).length()>0 && !resvNo.getValue("BED_NO", 0).equals(bedParm.getValue("BED_NO"))){
        	TParm  bedResult = new TParm() ;
        	bedResult.setData("BED_NO", resvNo.getData("BED_NO", 0)) ;
          result = ADMResvTool.getInstance().upDateSysBed(bedResult, conn);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText() +
                    result.getErrName());
                return result;
            }
        }
        //========================   chenxi modify  20130311
        //若次病患有预约信息 ，更新ADM_RESV 同时要将预约的床位的预约标记取消
        if (resvNo.getCount() > 0) {
            resvParm.setData("RESV_NO", resvNo.getData("RESV_NO", 0));
            result = ADMResvTool.getInstance().upDateForInp(resvParm, conn);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText() +
                    result.getErrName());
                return result;
            }
//            System.out.println("8");
//            System.out.println("9");
            //判断是否有门急诊诊断 如果有诊断需要插入到ADM_INPDIAG表
            if (resvNo.getValue("DIAG_CODE", 0).length() > 0) {
                //获取诊断信息
                TParm diag = SYSDiagnosisTool.getInstance().selectDataWithCode(
                        resvNo.getValue("DIAG_CODE", 0));
                TParm DiagParm = new TParm();
                DiagParm.setData("CASE_NO", CASE_NO);
                DiagParm.setData("IO_TYPE", "I"); //'I':门急诊诊断  ,'M'：入院诊断 'O':出院诊断
                DiagParm.setData("ICD_CODE", resvNo.getValue("DIAG_CODE", 0));
                DiagParm.setData("MAINDIAG_FLG", "Y");
                DiagParm.setData("ICD_TYPE", diag.getValue("ICD_TYPE", 0)); //需要根据诊断CODE去查询
                DiagParm.setData("SEQ_NO", "1");
                DiagParm.setData("MR_NO", MR_NO);
                DiagParm.setData("IPD_NO", IPD_NO);
                DiagParm.setData("DESCRIPTION",
                                 resvNo.getValue("DIAG_REMARK", 0));
                DiagParm.setData("OPT_USER", OPT_USER);
                DiagParm.setData("OPT_TERM", OPT_TERM);
                result = ADMDiagTool.getInstance().insertDiag(DiagParm, conn);
                if (result.getErrCode() < 0) {
                    err("ERR:" + result.getErrCode() + result.getErrText() +
                        result.getErrName());
                    return result;
                }
                // add by wanglong 20140410
                DiagParm.setData("ICD_KIND", diag.getValue("ICD_TYPE", 0));
                DiagParm.setData("ICD_DESC", diag.getValue("ICD_CHN_DESC", 0));
                DiagParm.setData("ICD_REMARK", resvNo.getValue("DIAG_REMARK", 0));
                DiagParm.setData("MAIN_FLG", "Y");
                DiagParm.setData("ICD_STATUS", "");
                DiagParm.setData("IN_PAT_CONDITION", "");
                DiagParm.setData("ADDITIONAL_CODE", "");
                DiagParm.setData("ADDITIONAL_DESC", "");
                result = MRORecordTool.getInstance().insertMRODiag(DiagParm, conn);
                if (result.getErrCode() < 0) {
                    err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
                    return result;
                }
                //add end
//                System.out.println("10");
                //修改ADM_INP的最新诊断字段MAINDIAG
                TParm daigNew = new TParm();
                daigNew.setData("MAINDIAG", resvNo.getValue("DIAG_CODE", 0));
                daigNew.setData("OPT_USER", OPT_USER);
                daigNew.setData("OPT_TERM", OPT_TERM);
                daigNew.setData("CASE_NO", CASE_NO);
                result = this.updateNewDaily(daigNew, conn);
                if (result.getErrCode() < 0) {
                    err("ERR:" + result.getErrCode() + result.getErrText() +
                        result.getErrName());
                    return result;
                }
//                System.out.println("11");
            }
        }
//        System.out.println("6");
        //插入病历主索引
        if (!SYSEmrIndexTool.getInstance().onInsertIpd(CASE_NO, "I",
                parm.getValue("REGION_CODE"), IPD_NO,
                MR_NO,
                parm.getTimestamp("DATE"),
                parm.getValue("IN_DEPT_CODE"),
                parm.getValue("VS_DR_CODE"),
                OPT_USER, OPT_TERM, conn)) {
            result.setErr( -1, "病历主索引插入错误");
            return result;
        }
        //修改病患基本信息 的 最近入院科室和入院时间
        TParm patParm = new TParm();
        patParm.setData("ADM_TYPE", "I"); //门急住别
        patParm.setData("VISIT_CODE", ""); //门诊使用属性 住院不填写
        patParm.setData("REALDEPT_CODE", parm.getValue("IN_DEPT_CODE")); //入住科室
        patParm.setData("MR_NO", MR_NO); //病案号
        result = PatTool.getInstance().upLatestDeptDate(patParm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
//        System.out.println("7");
        return result;
    }

    /**
     * 为adm_chg准备参数
     * @param parm TParm
     * @return TParm
     */
    public TParm loadAdmChg(TParm parm) {
        TParm result = new TParm();
        TParm seqParm = ADMChgTool.getInstance().ADMQuerySeq(parm);
        String seq = "";
        if (seqParm.getData("SEQ_NO", 0) == null ||
            "".equals(seqParm.getData("SEQ_NO", 0)))
            seq = "0";
        else
            seq = seqParm.getData("SEQ_NO", 0).toString();

        result.setData("CASE_NO", parm.getData("CASE_NO"));
        result.setData("SEQ_NO", seq);
        result.setData("IPD_NO", parm.getData("IPD_NO"));
        result.setData("MR_NO", parm.getData("MR_NO"));
        result.setData("CHG_DATE", parm.getData("DATE"));
        result.setData("PSF_KIND", "IN");
        result.setData("PSF_HOSP", "");
        result.setData("CANCEL_FLG", "N");
        result.setData("CANCEL_DATE", "");
        result.setData("CANCEL_USER", "");
        result.setData("DEPT_CODE", parm.getData("DEPT_CODE"));
        result.setData("STATION_CODE", parm.getData("STATION_CODE"));
        result.setData("BED_NO", parm.getData("BED_NO"));
        result.setData("VS_CODE_CODE", parm.getData("VS_DR_CODE"));
        result.setData("ATTEND_DR_CODE", "");
        result.setData("DIRECTOR_DR_CODE", "");
        result.setData("OPT_USER", parm.getData("OPT_USER"));
        result.setData("OPT_TERM", parm.getData("OPT_TERM"));
        //=======pangben modify 20110617 start
        result.setData("REGION_CODE", parm.getData("REGION_CODE"));
        result.setData("LUMPWORK_CODE", parm.getData("LUMPWORK_CODE"));//===liling 20140512
        return result;
    }

    /**
     * 修改adm_inp最新诊断 （出院主诊断>入院主诊断>门急诊主诊断）
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm updateNewDaily(TParm parm, TConnection conn) {
        TParm result = this.update("updateNewDaily", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 住院登记时插入信息
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm inHosptal(TParm parm, TConnection conn) {
        TParm result = this.update("inHosptal", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 包干套餐逻辑，办理住院登记时将预约住院操作时创建的就诊号码执行修改ADM_INP操作
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     * pangben 2014-7-30
     */
    public TParm updateAmdInpByLumpworkCaseNo(TParm parm, TConnection conn) {
        TParm result = this.update("updateAmdInpByLumpworkCaseNo", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 预约住院添加ADM_INP表，包干套餐逻辑使用
     * @param parm
     * @param conn
     * @return
     *  pangben 2014-7-30
     */
    public TParm insertAdmInpByCaseNo(TParm parm, TConnection conn) {
        TParm result = this.update("insertAdmInpByCaseNo", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    } 
    /**
     * 产生账单
     * @param parm TParm
     * @param FLG String   "Y":出院账单;"N":中结账单
     * @param conn TConnection
     * @return TParm
     */
    public TParm insertIBSBiilData(TParm parm, String FLG, TConnection conn) {
        //产生出院账单
        TParm result = new TParm();
        TParm ibsParm = new TParm();
        ibsParm.setData("IPD_NO", parm.getValue("IPD_NO"));
        TParm orderParm = new TParm();
        orderParm.setData("BILL_DATE", parm.getData("OUT_DATE"));
        orderParm.setData("OPT_USER", parm.getData("OPT_USER"));
        orderParm.setData("OPT_TERM", parm.getData("OPT_TERM"));
        orderParm.setData("FLG", FLG); //出院用Y  转科用N
        orderParm.addData("CASE_NO", parm.getValue("CASE_NO"));
        orderParm.setData("TYPE", "ADM");
        orderParm.setCount(1);
//        System.out.println("orderParm:"+orderParm);
        //判断如果存在费用 那么产生账单 否则不产生
        result = IBSTool.getInstance().insertIBSBillData(orderParm, conn);
//        System.out.println("费用回参："+result);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 住院召回 清空原有出院日期
     * @param CASE_NO String
     * @param conn TConnection
     * @return TParm
     */
    public TParm clearDsDate(String CASE_NO, TConnection conn) {
        TParm parm = new TParm();
        parm.setData("CASE_NO", CASE_NO);
        TParm result = this.update("clearDS_DATE", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 出院召回 修改召回后的床位
     * @param parm TParm   参数：BED_NO：床位号；   CASE_NO
     * @param conn TConnection
     * @return TParm
     */
    public TParm updateBedNoForReturn(TParm parm, TConnection conn) {
        TParm result = this.update("updateBedNoForReturn", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 住院召回 检查原有病床是否已经被占用
     * @param CASE_NO String
     * @return boolean 被占用返回 true； 空床返回 false
     */
    public boolean checkBedForReturn(String CASE_NO) {
        TParm parm = new TParm();
        parm.setData("CASE_NO", CASE_NO);
        TParm result = this.selectall(parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return false;
        }
        String bed_no = result.getValue("BED_NO", 0);
        TParm bed = new TParm();
        bed.setData("BED_NO", bed_no);
        TParm bedRe = SYSBedTool.getInstance().queryAll(bed);
        return bedRe.getBoolean("ALLO_FLG", 0);
    }

    /**
     * 出院患者召回 需要参数：CASE_NO;OPT_USER;OPT_TERM;
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm returnAdm(TParm parm, TConnection conn) {
        TParm result = new TParm();
        TParm admParm = new TParm();
        admParm.setData("CASE_NO", parm.getValue("CASE_NO"));
        TParm admInfo = this.selectall(admParm);
        if (admInfo.getCount() <= 0) {
            result.setErr( -1, "查无此病患的住院信息！");
            return result;
        }
        String caseNo = parm.getValue("CASE_NO");
        //记录上次出院时间
        result = this.updateLastDsDate(caseNo, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        //清空出院日期字段 DS_DATE
        result = this.clearDsDate(caseNo, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        //清空账务出院日期 CHARGE_DATE
        result = this.clearChargeDate(caseNo, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        //清空病案首页中病患的出院时间  OUT_DATE
        result = MRORecordTool.getInstance().clearOUT_DATE(caseNo, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        //写入待转入档
        TParm waitParm = new TParm();
        waitParm.setData("CASE_NO", caseNo);
        waitParm.setData("MR_NO", admInfo.getValue("MR_NO", 0));
        waitParm.setData("IPD_NO", admInfo.getValue("IPD_NO", 0));
        waitParm.setData("DEPT_CODE", admInfo.getValue("DS_DEPT_CODE", 0));
        waitParm.setData("STATION_CODE", admInfo.getValue("DS_STATION_CODE", 0));
        waitParm.setData("OPT_USER", parm.getValue("OPT_USER"));
        waitParm.setData("OPT_TERM", parm.getValue("OPT_TERM"));
        result = ADMWaitTransTool.getInstance().saveForInp(waitParm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }

        //修改床位信息(目前只写入代转表不召回到床位上)
        //更新床位档 参数
//        TParm bedParm = new TParm();
//        bedParm.setData("MR_NO",parm.getValue("MR_NO"));
//        bedParm.setData("CASE_NO",CASE_NO);
//        bedParm.setData("IPD_NO",admInfo.getValue("IPD_NO",0));
//        bedParm.setData("OPT_USER",parm.getValue("OPT_USER"));
//        bedParm.setData("OPT_TERM",parm.getValue("OPT_TERM"));
//        bedParm.setData("BED_NO",parm.getValue("BED_NO"));
//        bedParm.setData("APPT_FLG", "N");//预约标记
//        bedParm.setData("ALLO_FLG", "Y");//入住标记
//        bedParm.setData("BED_STATUS", "0");
//        if (!"".equals(bedParm.getValue("BED_NO"))) {
//            // 更新床位档
//            result = SYSBedTool.getInstance().upDate(bedParm, conn);
//            if (result.getErrCode() < 0) {
//                err("ERR:" + result.getErrCode() + result.getErrText() +
//                result.getErrName());
//                return result;
//            }
//        }
        //修改ADM_INP的床位字段
//        TParm inp_bed = new TParm();
//        inp_bed.setData("CASE_NO",CASE_NO);
//        inp_bed.setData("BED_NO",parm.getValue("BED_NO"));
//        result = this.updateBedNoForReturn(inp_bed,conn);
//        if (result.getErrCode() < 0) {
//            err("ERR:" + result.getErrCode() + result.getErrText() +
//                result.getErrName());
//            return result;
//        }
        //插入ADM_CHG病患动态档
        TParm adm_chg = new TParm();
        //查询最大SEQ
        TParm seqQuery = new TParm();
        seqQuery.setData("CASE_NO", caseNo);
        TParm seqParm = ADMChgTool.getInstance().ADMQuerySeq(seqQuery);
        String seq = seqParm.getData("SEQ_NO", 0).toString();
        adm_chg.setData("CASE_NO", caseNo);
        adm_chg.setData("SEQ_NO", seq);
        adm_chg.setData("IPD_NO", admInfo.getValue("IPD_NO", 0));
        adm_chg.setData("MR_NO", admInfo.getValue("MR_NO", 0));
        adm_chg.setData("PSF_KIND", "DSCC"); //住院处，出院召回
        adm_chg.setData("PSF_HOSP", "");
        adm_chg.setData("CANCEL_FLG", "N");
        adm_chg.setData("CANCEL_DATE", "");
        adm_chg.setData("CANCEL_USER", "");
        adm_chg.setData("DEPT_CODE", admInfo.getValue("DS_DEPT_CODE", 0));
        adm_chg.setData("STATION_CODE", admInfo.getValue("DS_STATION_CODE", 0));
        adm_chg.setData("BED_NO", ""); //召回到科 不入床
        adm_chg.setData("VS_CODE_CODE", "");
        adm_chg.setData("ATTEND_DR_CODE", "");
        adm_chg.setData("DIRECTOR_DR_CODE", "");
        adm_chg.setData("OPT_USER", parm.getValue("OPT_USER"));
        adm_chg.setData("OPT_TERM", parm.getValue("OPT_TERM"));
        //===========pangben modify 20110617
        adm_chg.setData("REGION_CODE", parm.getValue("REGION_CODE"));
        result = ADMChgTool.getInstance().insertAdmChg(adm_chg, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 护士执行更新三级检诊  更新“护理等级”字段(住院护士站使用)
     * @param parm TParm  参数：NURSING_CLASS:护理等级  CASE_NO:就诊序号
     * @param conn TConnection
     * @return TParm
     */
    public TParm updateNURSING_CLASS(TParm parm, TConnection conn) {
        TParm result = this.update("updateNURSING_CLASS", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 护士执行更新三级检诊  更新“病情状态”字段(住院护士站使用)
     * @param parm TParm  参数：PATIENT_STATUS:护理等级  CASE_NO:就诊序号
     * @param conn TConnection
     * @return TParm
     */
    public TParm updatePATIENT_STATUS(TParm parm, TConnection conn) {
        TParm result = this.update("updatePATIENT_STATUS", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 出院通知  更新“转归别代码”和“医疗出院日期”字段
     * @param parm TParm  参数：DISCH_CODE：转归别代码    MEDDISCH_DATE：医疗出院日期
     * @return TParm
     */
    public TParm updateMEDDISCH_DATE(TParm parm) {
        TParm result = this.update("updateMEDDISCH_DATE", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 修改绿色通道值
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm updateGREENPATH_VALUE(TParm parm, TConnection conn) {
        TParm result = this.update("updateGREENPATH_VALUE", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 修改ADM_INP表中的病案审核注记
     * @param parm TParm  必须参数：CASE_NO；MRO_CHAT_FLG：0，未审核   1，审核中   2，完成
     * @param conn TConnection
     * @return TParm
     */
    public TParm updateMRO_CHAT_FLG(TParm parm, TConnection conn) {
        TParm result = this.update("updateMRO_CHAT_FLG", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 清空账务出院日期(账务召回)
     * @param CASE_NO String
     * @param conn TConnection
     * @return TParm
     */
    public TParm clearChargeDate(String CASE_NO, TConnection conn) {
        TParm parm = new TParm();
        parm.setData("CASE_NO", CASE_NO);
        TParm result = this.update("clearCHARGE_DATE", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 查询生成信息看板XML的信息
     * @param parm TParm
     * @return TParm
     */
    public TParm selectForXML(TParm parm) {
        TParm result = this.query("selectForXML", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 查询在院病患信息
     * @param parm TParm
     * @return TParm
     */
    public TParm selectInHosp(TParm parm) {
        TParm result = this.query("selectInHosp", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 根据床位号查询病房信息
     * @param bed_no String
     * @return TParm
     */
    public TParm selectRoomInfo(String bed_no) {
        String sql = "SELECT A.ROOM_CODE, A.ROOM_DESC, A.PY1, " +
                     " A.PY2, A.SEQ, A.DESCRIPT, " +
                     " A.STATION_CODE, A.REGION_CODE, A.SEX_LIMIT_FLG, " +
                     " A.RED_SIGN, A.YELLOW_SIGN, A.ENG_DESC " +
                     " FROM SYS_ROOM A,SYS_BED B " +
                     " WHERE A.ROOM_CODE=B.ROOM_CODE " +
                     " AND B.BED_NO='" + bed_no + "' ";
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        return result;
    }

    /**
     * 出院患者账务召回 需要参数：CASE_NO;OPT_USER;OPT_TERM;
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm returnAdmBill(TParm parm, TConnection conn) {
        TParm result = new TParm();
        TParm admParm = new TParm();
        admParm.setData("CASE_NO", parm.getValue("CASE_NO"));
        TParm admInfo = this.selectall(admParm);
        if (admInfo.getCount() <= 0) {
            result.setErr( -1, "查无此病患的住院信息！");
            return result;
        }
        String CASE_NO = parm.getValue("CASE_NO");
        //记录上次出院时间LAST_DS_DATE,但是不清空出院日期字段 DS_DATE
        result = this.updateLastDsDate(CASE_NO, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        //清空账务出院日期 CHARGE_DATE
        result = this.clearChargeDate(CASE_NO, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        //清空病案首页中病患的出院时间  OUT_DATE
        result = MRORecordTool.getInstance().clearOUT_DATE(CASE_NO, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 记录上次出院时间
     * @param CASE_NO String
     * @param conn TConnection
     * @return TParm
     */
    public TParm updateLastDsDate(String CASE_NO, TConnection conn) {
        TParm parm = new TParm();
        parm.setData("CASE_NO", CASE_NO);
        TParm result = this.update("updateLAST_DS_DATE", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 包干套餐充值以后，取消预约住院删除ADM_INP表
     * @param CASE_NO
     * @param conn
     * @return
     * pangben 2014-8-1 
     */
    public TParm deleteAdmInpbyCaseNo(String CASE_NO, TConnection conn) {
        TParm parm = new TParm();
        parm.setData("CASE_NO", CASE_NO);
        TParm result = this.update("deleteAdmInpbyCaseNo", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 修改资格确认书编号
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm updateINPConfirmNo(TParm parm, TConnection conn) {
        TParm result = this.update("updateINPConfirmNo", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 查询未结案数据
     * @param parm TParm
     * @return TParm
     */
    public TParm queryAdmNClose(TParm parm) {
        TParm result = query("queryAdmNClose", parm);
        return result;
    }
    /**
     * 费用分割查询病患信息确定唯一数据
     * @param parm TParm
     * @return TParm
     * ========pangben 2012-6-18
     */
    public TParm queryAdmNCloseInsBalance(TParm parm) {
        TParm result = query("queryAdmNCloseInsBalance", parm);
        return result;
    }

	/**
     * 判断是否包含特殊符号-新生儿病案号判断 duzhw add 20140327
     * @param str
     * @param searchChars
     * @return
     */
	public boolean containsAny(String str, String searchChars){
		
	  if(str.length()!=str.replace(searchChars,"").length()){
	    return true;
	  }
	  return false;
	}
	/**
	 * 
	* @Title: onCheckNewBodyDsDate
	* @Description: TODO(母亲办理出院校验新生儿是否已经出院)
	* @author pangben
	* @param mrNo
	* @return
	* @throws
	 */
    public TParm onCheckNewBodyDsDate(String caseNo){
    	String sql="SELECT CASE_NO FROM ADM_INP WHERE M_CASE_NO='"+caseNo+
    	"' AND NEW_BORN_FLG='Y' AND DS_DATE IS NULL AND CANCEL_FLG <> 'Y' "; //add by wukai on 20160803 添加CANCLE_FLG判断
    	 TParm result = new TParm(TJDODBTool.getInstance().select(sql));
         return result;
    }
    /**
     * 校验套餐患者是否需要执行批次
     * 转科转病区使用
     * @return
     */
    public TParm onCheckLumpworkExeBatch(String caseNo){
    	String sql="SELECT CASE_NO FROM IBS_ORDD WHERE CASE_NO ='"+caseNo+"' AND INCLUDE_EXEC_FLG='N'";
    	TParm result = new TParm(TJDODBTool.getInstance().select(sql));
    	TParm parm= new TParm();
    	if (result.getCount()>0) {
    		parm.setErr(-1,"套餐患者存在未执行批次医嘱,不可以转科");
    		return parm;
		}
    	sql="SELECT A.CASE_NO FROM IBS_ORDD A,ADM_INP B WHERE A.CASE_NO=B.CASE_NO AND B.NEW_BORN_FLG='Y'  AND M_CASE_NO ='"+caseNo+"' AND INCLUDE_EXEC_FLG='N'";
    	result = new TParm(TJDODBTool.getInstance().select(sql));
    	if (result.getCount()>0) {
    		parm.setErr(-1,"此套餐患者婴儿存在未执行批次医嘱,不可以转科");
    		return parm;
		}
        return parm;
    }
    /**
     * 
    * @Title: onCheckLumpwork
    * @Description: TODO(校验此病患是否为套餐患者去掉新生儿)
    * @author pangben
    * @param caseNo
    * @return
    * @throws
     */
    public TParm onCheckLumpwork(String caseNo){
    	String sql="SELECT A.CASE_NO,A.NEW_BORN_FLG,B.AR_AMT AS FEE,A.DEPT_CODE,A.STATION_CODE," +
    			"A.BED_NO,A.VS_DR_CODE,A.CLNCPATH_CODE,A.DS_DATE FROM ADM_INP A,MEM_PACKAGE_TRADE_M B," +
    			"MEM_PAT_PACKAGE_SECTION C,MEM_PACKAGE D WHERE B.TRADE_NO=C.TRADE_NO AND C.PACKAGE_CODE=D.PACKAGE_CODE" +
    			" AND B.USED_FLG='1' AND D.ADM_TYPE='I' AND A.CASE_NO=B.CASE_NO AND A.LUMPWORK_CODE=C.PACKAGE_CODE AND A.CASE_NO='"+caseNo+
    			"' AND A.LUMPWORK_CODE IS NOT NULL GROUP BY A.CASE_NO,A.NEW_BORN_FLG,B.AR_AMT,A.DEPT_CODE,A.STATION_CODE," +
    			"A.BED_NO,A.VS_DR_CODE,A.CLNCPATH_CODE,A.DS_DATE";
    	TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        return result;
    }
    /**
     * 
    * @Title: onCheckLumWorkNew
    * @Description: TODO(校验此病患是否为套餐患者包括新生儿)
    * @author pangben
    * @param caseNo
    * @return
    * @throws
     */
    public TParm onCheckLumWorkNew(String caseNo){
    	String sql="SELECT M_CASE_NO,NEW_BORN_FLG FROM ADM_INP WHERE CASE_NO='"+caseNo+"'";
    	TParm newBobyParm = new TParm(TJDODBTool.getInstance().select(sql));
    	String caseNoNew=caseNo;
    	if (newBobyParm.getValue("NEW_BORN_FLG",0).equals("Y")) {
    		caseNoNew=newBobyParm.getValue("M_CASE_NO",0);
		}
    	sql="SELECT A.CASE_NO,A.NEW_BORN_FLG,B.AR_AMT AS FEE,A.DEPT_CODE,A.STATION_CODE," +
		"A.BED_NO,A.VS_DR_CODE,A.CLNCPATH_CODE,A.DS_DATE,D.PACKAGE_DESC,A.INCLUDE_FLG,A.CTZ1_CODE,A.CTZ2_CODE,A.CTZ3_CODE FROM ADM_INP A,MEM_PACKAGE_TRADE_M B," +
		"MEM_PAT_PACKAGE_SECTION C,MEM_PACKAGE D WHERE B.TRADE_NO=C.TRADE_NO AND C.PACKAGE_CODE=D.PACKAGE_CODE" +
		" AND B.USED_FLG='1' AND D.ADM_TYPE='I' AND A.CASE_NO=B.CASE_NO AND A.LUMPWORK_CODE=C.PACKAGE_CODE AND A.CASE_NO='"+caseNoNew+
		"' AND A.LUMPWORK_CODE IS NOT NULL GROUP BY A.CASE_NO,A.NEW_BORN_FLG,B.AR_AMT,A.DEPT_CODE,A.STATION_CODE," +
		"A.BED_NO,A.VS_DR_CODE,A.CLNCPATH_CODE,A.DS_DATE,D.PACKAGE_DESC,A.INCLUDE_FLG,A.CTZ1_CODE,A.CTZ2_CODE,A.CTZ3_CODE";
    	TParm result = new TParm(TJDODBTool.getInstance().select(sql));
    	if (result.getCount()>0) {
    		if (newBobyParm.getValue("NEW_BORN_FLG",0).equals("Y")) {
        		result.setData("newBobyParm",newBobyParm.getData());
    		}
		}
    	return result;
    }
    /**
     * 
    * @Title: onCheckLumWorkReturn
    * @Description: TODO(套餐病患，医疗召回，如果婴儿召回，母亲必须在院状态)
    * @author pangben 2016-1-21
    * @param caseNo
    * @return
    * @throws
     */
    public boolean onCheckLumWorkReturn(String caseNo){
    	TParm admLumWorkParm=onCheckLumWorkNew(caseNo);
    	//查询是否为套餐病患
    	if (admLumWorkParm.getCount()>0) {
    		//查询当前操作为婴儿
			if (null!=admLumWorkParm.getParm("newBobyParm")&&
					admLumWorkParm.getParm("newBobyParm").getValue("NEW_BORN_FLG",0).equals("Y")) {
				//查询母亲是否在院状态
				String sql="SELECT CASE_NO FROM ADM_INP WHERE CASE_NO='"+
				admLumWorkParm.getParm("newBobyParm").getValue("M_CASE_NO",0)+"' AND DS_DATE IS NULL";
				TParm result = new TParm(TJDODBTool.getInstance().select(sql));
				if (result.getCount()>0) {//母亲没有出院
					return true;
				}else{
					return false;
				}
			}
		}else{
			return true;
		}
    	return true;
    }
    /**
     * 
    * @Title: onSaveOutLumpworkDiffAmt
    * @Description: TODO(出院病患校验套餐病人补充差额数据)
    * @author pangben
    * @return
    * @throws
     */
    public TParm onSaveOutLumpworkDiffAmt(TParm admParm,TParm parm,TConnection conn){
    	String caseNo=parm.getValue("CASE_NO");
    	//String mrNo=parm.getValue("MR_NO");
    	TParm result=new TParm();
    	//校验此病患是否套餐患者并且不是新生儿
    	double fee=admParm.getDouble("FEE",0);
		//查询IBS_ORDD表套餐内费用，包括新生儿数据
		String sql="SELECT SUM(TOT_AMT) TOT_AMT FROM IBS_ORDD WHERE " +
				"CASE_NO IN(SELECT CASE_NO FROM ADM_INP WHERE (M_CASE_NO = '"+caseNo+
				"' AND NEW_BORN_FLG='Y' AND DS_DATE IS NOT NULL) OR CASE_NO='"+caseNo+"') AND (INCLUDE_FLG='N' OR INCLUDE_FLG IS NULL)";
		result = new TParm(TJDODBTool.getInstance().select(sql));
		double arAmt=result.getDouble("TOT_AMT",0);//获得实际套餐内金额
		double diffAmt=fee-arAmt;//套餐差额=套餐总金额-实际套餐内金额
		//====pangben 2015-11-10 不存在套餐差额不需要操作执行添加逻辑
		if (diffAmt==0.00) {
			result=new TParm();
			result.setData("DIFF_FLG","Y");
			return result;
		}
		TParm lumpWorkOrderParm=getLumpworkOrderCode();
		//String orderCode=lumpWorkOrderParm.get
		TParm maxCaseNoSeqParm = selMaxCaseNoSeq(caseNo);
		int maxCaseNoSeq = maxCaseNoSeqParm.getInt("CASE_NO_SEQ", 0) + 1;
		TParm insertIbsOrddNegativeParm = new TParm();	
		insertIbsOrddNegativeParm.setData("CASE_NO",caseNo);					
		if (maxCaseNoSeqParm.getCount("CASE_NO_SEQ") == 0) {
			insertIbsOrddNegativeParm.setData("CASE_NO_SEQ",1);
		} else {
			insertIbsOrddNegativeParm.setData("CASE_NO_SEQ",maxCaseNoSeq);
		}
		// 得到系统时间
		Timestamp sysDate = SystemTool.getInstance().getDate();
		insertIbsOrddNegativeParm.setData("SEQ_NO",1);
		getIbsOrddParm(insertIbsOrddNegativeParm, admParm.getRow(0), lumpWorkOrderParm.getRow(0),sysDate);
		insertIbsOrddNegativeParm.setData("ORDERSET_GROUP_NO","");
		insertIbsOrddNegativeParm.setData("OWN_RATE",1);
		insertIbsOrddNegativeParm.setData("TOT_AMT",diffAmt);
		insertIbsOrddNegativeParm.setData("INCLUDE_FLG","N");
		insertIbsOrddNegativeParm.setData("OWN_AMT",diffAmt);
		insertIbsOrddNegativeParm.setData("OPT_USER",parm.getValue("OPT_USER"));
		insertIbsOrddNegativeParm.setData("OPT_TERM",parm.getValue("OPT_TERM"));
		insertIbsOrddNegativeParm.setData("OWN_PRICE",diffAmt);
		insertIbsOrddNegativeParm.setData("COST_CENTER_CODE",parm.getValue("EXE_DEPT_CODE"));
		insertIbsOrddNegativeParm.setData("EXE_DEPT_CODE",parm.getValue("EXE_DEPT_CODE"));
		insertIbsOrddNegativeParm.setData("EXE_DR_CODE",parm.getValue("OPT_USER"));
		// 查询收据费用代码
		TParm hexpParm = SYSChargeHospCodeTool.getInstance().selectalldata(
				lumpWorkOrderParm.getRow(0));
		insertIbsOrddNegativeParm.setData("REXP_CODE", hexpParm.getValue("IPD_CHARGE_CODE",0));
		insertIbsOrddNegativeParm.setData("INCLUDE_EXEC_FLG", "Y");
		result =IBSOrdmTool.getInstance().insertdataLumpworkD(insertIbsOrddNegativeParm,conn);
		if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
		}
		TParm insertIbsOrdMNegativeParm = new TParm();					
		insertIbsOrdMNegativeParm.setData("CASE_NO",caseNo);
		if (maxCaseNoSeqParm.getCount("CASE_NO_SEQ") == 0) {
			insertIbsOrdMNegativeParm.setData("CASE_NO_SEQ",1);
		} else {
			insertIbsOrdMNegativeParm.setData("CASE_NO_SEQ",maxCaseNoSeq);
		}
		insertIbsOrdMNegativeParm.setData("BILL_DATE",sysDate);
		insertIbsOrdMNegativeParm.setData("IPD_NO",parm.getValue("IPD_NO"));
		insertIbsOrdMNegativeParm.setData("MR_NO",parm.getValue("MR_NO"));
		insertIbsOrdMNegativeParm.setData("DEPT_CODE", admParm.getRow(0).getValue("DEPT_CODE"));
		insertIbsOrdMNegativeParm.setData("STATION_CODE",admParm.getRow(0).getValue("STATION_CODE"));
		insertIbsOrdMNegativeParm.setData("BED_NO",admParm.getRow(0).getValue("BED_NO"));
		insertIbsOrdMNegativeParm.setData("DATA_TYPE","1");
		insertIbsOrdMNegativeParm.setData("BILL_NO","");
		insertIbsOrdMNegativeParm.setData("OPT_USER",parm.getValue("OPT_USER"));
		insertIbsOrdMNegativeParm.setData("OPT_TERM",parm.getValue("OPT_TERM"));
		insertIbsOrdMNegativeParm.setData("REGION_CODE",parm.getValue("REGION_CODE"));
		insertIbsOrdMNegativeParm.setData("COST_CENTER_CODE",parm.getValue("EXE_DEPT_CODE"));
		result =IBSOrdmTool.getInstance().insertdataM(insertIbsOrdMNegativeParm,conn);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
        return result;	
    }
    public TParm selMaxCaseNoSeq(String caseNo) {
		String sql = " SELECT MAX(CASE_NO_SEQ) AS CASE_NO_SEQ FROM IBS_ORDM WHERE CASE_NO = '"
				+ caseNo + "' ";

		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}
    /**
     * 
    * @Title: getLumpworkOrderCode
    * @Description: TODO(获得套餐差额医嘱代码)
    * @author Dangzhang
    * @return
    * @throws
     */
    public TParm getLumpworkOrderCode(){
    	String sql="SELECT A.LUMPWORK_ORDER_CODE,B.EXEC_DEPT_CODE,B.ORDER_CAT1_CODE,B.CAT1_TYPE," +
    			"B.CHARGE_HOSP_CODE,B.UNIT_CODE,B.NHI_PRICE,B.ORDER_DESC,B.ORDER_CODE" +
    			" FROM ODI_SYSPARM A,SYS_FEE B " +
    			"WHERE A.LUMPWORK_ORDER_CODE=B.ORDER_CODE AND B.ACTIVE_FLG='Y'";
    	TParm result = new TParm(TJDODBTool.getInstance().select(sql));
    	return result;
    }
    /**
     * 
    * @Title: getIbsOrddParm
    * @Description: TODO(获得IBS_ORDD表入参)
    * @author pangben
    * @param insertIbsOrddNegativeParm
    * @param admParm
    * @param parmIbsOrdd
    * @param sysDate
    * @throws
     */
    private void getIbsOrddParm(TParm insertIbsOrddNegativeParm,TParm admParm,
    		TParm parmIbsOrdd,Timestamp sysDate){
    	insertIbsOrddNegativeParm.setData("BILL_DATE",sysDate);
		insertIbsOrddNegativeParm.setData("EXEC_DATE",sysDate);
		insertIbsOrddNegativeParm.setData("ORDER_NO",SystemTool.getInstance().getNo("ALL", "ODI", "ORDER_NO","ORDER_NO"));
		insertIbsOrddNegativeParm.setData("ORDER_SEQ","1");
		insertIbsOrddNegativeParm.setData("ORDER_CODE",parmIbsOrdd.getValue("ORDER_CODE"));
		insertIbsOrddNegativeParm.setData("ORDER_CAT1_CODE",parmIbsOrdd.getValue("ORDER_CAT1_CODE"));
		insertIbsOrddNegativeParm.setData("CAT1_TYPE",parmIbsOrdd.getValue("CAT1_TYPE"));
		insertIbsOrddNegativeParm.setData("ORDERSET_CODE","");
		insertIbsOrddNegativeParm.setData("INDV_FLG","N");
		insertIbsOrddNegativeParm.setData("DEPT_CODE",admParm.getValue("DEPT_CODE"));
		insertIbsOrddNegativeParm.setData("STATION_CODE",admParm.getValue("STATION_CODE"));//ADM_INP表
		insertIbsOrddNegativeParm.setData("EXE_STATION_CODE",admParm.getValue("STATION_CODE"));//ADM_INP表
		insertIbsOrddNegativeParm.setData("DR_CODE",admParm.getValue("VS_DR_CODE"));//ADM_INP表
		insertIbsOrddNegativeParm.setData("MEDI_QTY",1);
		insertIbsOrddNegativeParm.setData("MEDI_UNIT",parmIbsOrdd.getValue("UNIT_CODE"));
		insertIbsOrddNegativeParm.setData("DOSE_CODE","");
		insertIbsOrddNegativeParm.setData("FREQ_CODE","STAT");
		insertIbsOrddNegativeParm.setData("TAKE_DAYS",1);
		insertIbsOrddNegativeParm.setData("DOSAGE_QTY",1);
		insertIbsOrddNegativeParm.setData("DOSAGE_UNIT",parmIbsOrdd.getValue("UNIT_CODE"));
		insertIbsOrddNegativeParm.setData("NHI_PRICE",0.00);
		insertIbsOrddNegativeParm.setData("OWN_FLG","Y");
		insertIbsOrddNegativeParm.setData("BILL_FLG","Y");
		insertIbsOrddNegativeParm.setData("BILL_NO","");
		insertIbsOrddNegativeParm.setData("HEXP_CODE",parmIbsOrdd.getData("CHARGE_HOSP_CODE"));
		insertIbsOrddNegativeParm.setData("BEGIN_DATE",sysDate);
		insertIbsOrddNegativeParm.setData("END_DATE",sysDate);
		insertIbsOrddNegativeParm.setData("REQUEST_FLG","N");
		insertIbsOrddNegativeParm.setData("REQUEST_NO","");
		insertIbsOrddNegativeParm.setData("INV_CODE","");
		insertIbsOrddNegativeParm.setData("COST_AMT",0.00);
		insertIbsOrddNegativeParm.setData("ORDER_CHN_DESC",parmIbsOrdd.getData("ORDER_DESC"));
		insertIbsOrddNegativeParm.setData("SCHD_CODE","");
		insertIbsOrddNegativeParm.setData("CLNCPATH_CODE",admParm.getValue("CLNCPATH_CODE"));
		insertIbsOrddNegativeParm.setData("DS_FLG","N");
		insertIbsOrddNegativeParm.setData("KN_FLG","N");
    }
    /**
     * 套餐批次查询未执行医嘱的病患信息
     * @param parm
     * @return
     */
    public TParm onQueryLumpWorkBatch(TParm parm){
		String sql="SELECT A.CASE_NO,A.LUMPWORK_CODE,A.MR_NO,A.CTZ1_CODE,A.CTZ2_CODE,A.CTZ3_CODE,A.M_CASE_NO,A.NEW_BORN_FLG,A.LUMPWORK_RATE,A.SERVICE_LEVEL " +
		"FROM ADM_INP A,IBS_ORDD B WHERE A.CASE_NO=B.CASE_NO " +
		" AND A.LUMPWORK_CODE IS NOT NULL AND A.DS_DATE IS NULL AND B.INCLUDE_EXEC_FLG='N'" +
		" AND A.CANCEL_FLG ='N' AND (A.CASE_NO='"+parm.getValue("CASE_NO")+"' OR A.M_CASE_NO='"+parm.getValue("CASE_NO")+"') " +
		"GROUP BY A.CASE_NO,A.LUMPWORK_CODE,A.MR_NO,A.CTZ1_CODE,A.CTZ2_CODE,A.CTZ3_CODE,A.M_CASE_NO,A.NEW_BORN_FLG,A.LUMPWORK_RATE,A.SERVICE_LEVEL ORDER BY A.MR_NO";
		TParm admParm=new TParm(TJDODBTool.getInstance().select(sql));
		return admParm;
    }
    /**
     * 
    * @Title: updlumpWorkBill
    * @Description: TODO(修改套餐病人身份，住院登记界面修改套餐类型操作，同时操作套餐批次)
    * @author pangben 2015-7-31
    * @param parm
    * @param connection
    * @return
    * @throws
     */
    public TParm updlumpWorkBill(TParm parm,TConnection connection){
    	TParm result = new TParm();
		//TParm temp=new TParm();
		TParm selParm =new TParm();
		IBSNewTool tool=new IBSNewTool();
		//如果存在婴儿将婴儿身份一起修改====pangben 2015-7-28
		String sql="SELECT CASE_NO,LUMPWORK_RATE,LUMPWORK_CODE,SERVICE_LEVEL FROM ADM_INP WHERE M_CASE_NO='"+parm.getValue("CASE_NO")+"' AND NEW_BORN_FLG='Y'";
		selParm = new TParm(TJDODBTool.getInstance().select(sql));
		boolean checkLumpWorkFlg=false;
		String level =""; 
	    String lumpworkCode="";//套餐代码
	    double lumpworkRate=0.00;//套餐折扣 住院登记时计算
		if(null!=parm.getValue("LUMPWORK_CODE")&&parm.getValue("LUMPWORK_CODE").length()>0){
			checkLumpWorkFlg=true;
			lumpworkCode=parm.getValue("LUMPWORK_CODE");
			lumpworkRate=parm.getDouble("LUMPWORK_RATE");
			level=parm.getValue("SERVICE_LEVEL");
		}
		TParm queryInfoParm=tool.onCheckLumWorkCaseNo(parm.getValue("MR_NO"),parm.getValue("CASE_NO"));
		String mrNoNew=queryInfoParm.getValue("MR_NO");
		String caseNoNew=queryInfoParm.getValue("CASE_NO");
		// 得到系统时间
		Timestamp sysDate = SystemTool.getInstance().getDate();
		for (int i = 0; i < selParm.getCount("CASE_NO"); i++) {
			result=tool.getUpdBillExe(selParm.getValue("CASE_NO",i), parm.getValue("OPT_USER"), 
					parm.getValue("OPT_TERM"), sysDate, 
					parm.getValue("CTZ1_CODE"), parm.getValue("CTZ2_CODE"), 
					parm.getValue("CTZ3_CODE"),true,checkLumpWorkFlg,null, connection,lumpworkRate,
					lumpworkCode,level,false,mrNoNew,caseNoNew);
			if (result.getErrCode()<0) {
				connection.rollback();
				connection.close();
				return result;
			}
		}
		TParm odiParm = new TParm(TJDODBTool.getInstance().select("SELECT LUMPWORK_ORDER_CODE FROM ODI_SYSPARM"));
		result = tool.getUpdBillExe(parm.getValue("CASE_NO"), parm.getValue("OPT_USER"), 
				parm.getValue("OPT_TERM"), sysDate, 
				parm.getValue("CTZ1_CODE"), parm.getValue("CTZ2_CODE"), 
				parm.getValue("CTZ3_CODE"),false,checkLumpWorkFlg,odiParm, connection,
				lumpworkRate,lumpworkCode,level,false,mrNoNew,caseNoNew);
		if (result.getErrCode() < 0) {
			connection.rollback();
			connection.close();
			return result;
		}
		connection.commit();
		if(null!=parm.getValue("LUMPWORK_CODE")&&parm.getValue("LUMPWORK_CODE").length()>0){
			TParm admParm=onQueryLumpWorkBatch(parm);
			if (admParm.getCount()<=0) {
				result=new TParm();
				result.setData("MESSAGE","没有需要操作的数据");
				return result;
			}
			//母亲婴儿跑套餐批次操作
			for (int i = 0; i < admParm.getCount(); i++) {
				result=IBSLumpWorkBatchNewTool.getInstance().onLumpWorkBatch(admParm.getRow(i), connection);
		    	if (result.getErrCode()<0) {
					connection.rollback();
					connection.close();
					return result;
				}
		    	if(admParm.getCount()>1&&i!=admParm.getCount()-1){
		    		connection.commit();
		    	}
		    	result = IBSTool.getInstance().updateAdmTotAmt(admParm.getRow(i));
				if (result.getErrCode() < 0) {
					return result;
				}
			}
		}
		return result;
    }
    /**
     * 校验套餐是否修改
     * @return
     */
    public TParm checkLumpWorkisUpdate(String caseNo,String oldLumpWorkCode){
    	TParm admParm = new TParm();
		admParm.setData("CASE_NO", caseNo);
		TParm selAdmParm = ADMInpTool.getInstance().selectall(admParm);
		String message="此病患已经修改套餐，不可以操作";
		if(null==selAdmParm.getValue("LUMPWORK_CODE", 0)||selAdmParm.getValue("LUMPWORK_CODE", 0).length()<=0){
			admParm.setErr(-1,message);
			return admParm;
		}
		if(null!=selAdmParm.getValue("LUMPWORK_CODE", 0)&&
				!oldLumpWorkCode.equals(selAdmParm.getValue("LUMPWORK_CODE", 0))){
			admParm.setErr(-1,message);
			return admParm;
		}
		return admParm;
    }
    /**
     * 住院套餐病患查询
     * @return
     */
    public TParm selectLumpWork(TParm parm){
    	TParm result = new TParm();
        result = query("selectLumpWork", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
}
