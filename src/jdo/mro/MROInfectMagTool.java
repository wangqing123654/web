package jdo.mro;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>Title: ��Ⱦ�����濨����--���ݿ⹤����</p>
 *
 * <p>Description: ��Ⱦ�����濨����--���ݿ⹤����</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: javahis</p>
 *
 * @author zhangh 2013-11-05
 * @version 1.0
 */
public class MROInfectMagTool extends TJDOTool{

        /**
         * ������
         */
        public MROInfectMagTool() {
            setModuleName("mro\\MROInfectMagModule.x");
            onInit();
        }

        /**
         * ʵ��
         */
        private static MROInfectMagTool instanceObject;

        /**
         * �õ�ʵ��
         * @return INFCaseTool
         */
        public static MROInfectMagTool getInstance()
        {
            if(instanceObject == null)
                instanceObject = new MROInfectMagTool();
            return instanceObject;
        }

        /**
         * ��ѯ��Ⱦ�����濨��Ϣ
         * @param parm TParm
         * @return TParm
         */
        public TParm selectInfectReport(TParm parm){
        	TParm result = null;
        	if(parm.getValue("ADM_TYPE").equals("I"))
        		result = query("selectInfectReportInPatient",parm);
        	else
        		result = query("selectInfectReportOutPatient",parm);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText()
                    + result.getErrName());
                return result;
            }
            return result;
        }

        /**
         * ���´�Ⱦ�����濨��Ϣ
         * @param parm TParm
         * @return TParm
         */
        public TParm updateInfectReport(TParm parm){
        	TParm result = new TParm();
        	TParm updateTParm = new TParm();
        	for (int i = 0; i < parm.getCount("MR_NO"); i++) {
        		updateTParm.setData("MR_NO", parm.getValue("MR_NO", i));
        		updateTParm.setData("CARD_SEQ_NO", parm.getValue("CARD_SEQ_NO", i));
        		updateTParm.setData("UPLOAD_DATE", parm.getValue("UPLOAD_DATE", i));
        		updateTParm.setData("UPLOAD_CODE", parm.getValue("UPLOAD_CODE", i));
        		updateTParm.setData("PAD_DEPT", parm.getValue("PAD_DEPT", i));
        		result = update("updateInfectReport",updateTParm);
                if (result.getErrCode() < 0) {
                    err("ERR:" + result.getErrCode() + result.getErrText()
                        + result.getErrName());
                    return result;
                }
			}
            return result;
        }
        
        /**
         * ���´�Ⱦ�����濨��Ϣ
         * @param parm TParm
         * @return TParm
         */
        public TParm updateInfectReportCancel(TParm parm){
        	TParm result = new TParm();
        	TParm updateTParm = new TParm();
        	for (int i = 0; i < parm.getCount("MR_NO"); i++) {
        		updateTParm.setData("MR_NO", parm.getValue("MR_NO", i));
        		updateTParm.setData("CARD_SEQ_NO", parm.getValue("CARD_SEQ_NO", i));
        		updateTParm.setData("UPLOAD_DATE", parm.getValue("UPLOAD_DATE", i));
        		updateTParm.setData("UPLOAD_CODE", parm.getValue("UPLOAD_CODE", i));
        		updateTParm.setData("PAD_DEPT", parm.getValue("PAD_DEPT", i));
        		result = update("updateInfectReportCancel",updateTParm);
                if (result.getErrCode() < 0) {
                    err("ERR:" + result.getErrCode() + result.getErrText()
                        + result.getErrName());
                    return result;
                }
			}
            return result;
        }
    }
