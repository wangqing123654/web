package com.javahis.ui.mro;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import jdo.mro.MROLendRegTool;
import jdo.mro.MROQueueTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import org.apache.commons.lang.StringUtils;

import com.dongyang.control.TControl;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;


/**
 * <p>Title: ����ע��</p>
 *
 * <p>Description: ����ע��</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-5-11
 * @version 1.0
 */
public class MROLendRegControl
    extends TControl {
    private TParm Parameter;//��Ų���
    //��ʼ��
    public void onInit() {
        super.onInit();
        //���ղ���
        Object obj = this.getParameter();
        if (obj != null) {
            if (obj instanceof TParm) { //�ж��Ƿ���TParm
                Parameter = (TParm) obj;
            }
            else {
                this.closeWindow();
            }
        }
        else {
            this.closeWindow();
        }
        pageInit();
    }

    //ҳ���ʼ��
    private void pageInit() {
        this.setValue("IPD_NO", Parameter.getValue("IPD_NO"));
        this.setValue("MR_NO",Parameter.getValue("MR_NO"));
        this.setValue("PAT_NAME",Parameter.getValue("PAT_NAME"));
        this.setValue("VS_CODE",Parameter.getValue("VS_CODE"));
        this.setValue("QUE_DATE",SystemTool.getInstance().getDate());
        this.setValue("REQ_DEPT",Operator.getDept());//������Ա����
        this.setValue("MR_PERSON",Operator.getID());//������Ա
        
        // add by wangbin סԺ���ﲡ������ 20140917 START
        // ��ѯ��ǰ�����İ�������
        TParm parm = this.onQueryData();
        
        if (parm.getErrCode() < 0) {
        	this.messageBox("��ʼ�����������쳣");
        	return;
        }
        
        if (parm.getCount() <= 0) {
        	if (StringUtils.equals(Parameter.getValue("ADM_TYPE"), "I")) {
        		this.messageBox("��ǰ������סԺ����");
        	} else {
        		this.messageBox("��ǰ���������ﲡ��");
        	}
        	return;
        } else {
        	// �趨����
        	this.setValue("BOX_CODE", parm.getValue("BOX_CODE", 0));
        	
        	String stringData = "[[ID,TEXT],[],";
        	String bookNo = "";
        	for (int i = 0; i < parm.getCount(); i++) {
        		bookNo = parm.getValue("BOOK_NO", i);
        		stringData = stringData + "[" + bookNo + "," + bookNo + "]";
        		if (i < parm.getCount() - 1) {
        			stringData = stringData + ",";
        		}
        	}
        	
        	stringData = stringData + "]";
        	((TComboBox)this.getComponent("BOOK_NO")).setStringData(stringData);
        	
        	// �趨������Ĭ��ѡ������
        	((TComboBox)this.getComponent("BOOK_NO")).setSelectedIndex(parm.getCount());
        }
        // add by wangbin סԺ���ﲡ������ 20140917 END
    }
    
    /**
     * ����
     */
    public void onSave(){
    	// ����У��
    	if (!this.validateData()) {
    		return;
    	}
    	
        TParm mrv = new TParm();
        mrv.setData("MR_NO", this.getValueString("MR_NO"));
        mrv.setData("ADM_TYPE", Parameter.getValue("ADM_TYPE"));
        mrv.setData("BOX_CODE", this.getValueString("BOX_CODE"));
        mrv.setData("BOOK_NO", this.getValueString("BOOK_NO"));
        TParm mrvResult = MROQueueTool.getInstance().selectMRO_MRV(mrv);
        
        if(mrvResult.getErrCode() < 0){
        	this.messageBox_("��ѯ�鵵��Ϣʧ�ܣ�");
        	err(mrvResult.getErrName() + mrvResult.getErrCode() + mrvResult.getErrText());
            return;
        }
        
        if(mrvResult.getCount()<=0){
        	this.messageBox_("�˲����޹鵵��Ϣ�����ɽ���");
            return;
        }
        
        // ����״��
        mrv.setData("ISSUE_CODE", "2");
        
        // ��ѯ���ı����Ѿ����ڵĽ�������
        mrvResult = MROQueueTool.getInstance().selectMroQueueInfo(mrv);
        
		if (mrvResult.getErrCode() < 0) {
			this.messageBox_("��ѯ��������ʧ�ܣ�");
			err("ERR:" + mrvResult.getErrCode() + mrvResult.getErrText()
					+ mrvResult.getErrName());
			return;
		}
        
		// ��ǰ��������
		String currentLendDate = this.getValueString("QUE_DATE").substring(0, 10).replaceAll("-", "/");
		// ��ǰ����Ԥ�ƹ黹ʱ��
		String currentReturnDate = this.getValueString("RTN_DATE").substring(0, 10).replaceAll("-", "/");
		// �Ѵ��ڵĽ���ʱ��
		String lendDate = "";
		// �Ѵ��ڵĽ���ʱ��
		String rtnDate = "";
		
		// ������ڽ����������Ƚ���������֤
        if(mrvResult.getCount() > 0){
        	for (int i = 0; i < mrvResult.getCount(); i++) {
        		lendDate = mrvResult.getValue("QUE_DATE", i).substring(0, 10).replaceAll("-", "/");
        		// �����ǰ����û�г���
        		if (StringUtils.equals("0", mrvResult.getValue("ISSUE_CODE", i))) {
        			if (StringUtils.isEmpty(mrvResult.getValue("RTN_DATE", i))) {
        				// ���Ĺ黹����ֻ��С���������ݵĽ�������
    					if (this.compareDate(currentLendDate, lendDate) <= 0
    							&& this.compareDate(currentReturnDate, lendDate) >= 0) {
            				this.messageBox("��ǰ��д�Ľ��Ĺ黹�ڼ������д����������,�����µ�������ʱ��");
            				return;
            			}
        			} else {
        				rtnDate = mrvResult.getValue("RTN_DATE", i).substring(0, 10).replaceAll("-", "/");
        				if (!(this.compareDate(currentReturnDate, lendDate) < 0 
								|| this.compareDate(currentLendDate, rtnDate) > 0)) {
							this.messageBox("��ǰ��д�Ľ��Ĺ黹�ڼ������д����������,����ʱ��Ϊ��"
									+ lendDate + "���黹ʱ��Ϊ��" + rtnDate
									+ "�������µ�������ʱ��");
            				return;
        				}
        			}
        		} else {
        			// סԺ�ѳ��������û�й黹ʱ��
        			if (StringUtils.isEmpty(mrvResult.getValue("RTN_DATE", i))) {
						if (this.compareDate(currentReturnDate, lendDate) >= 0) {
        					this.messageBox("��ǰ�����Ѿ�����,��黹ʱ�䲻���ݲ��ɽ���");
        					return;
        				}
        			} else {
        				rtnDate = mrvResult.getValue("RTN_DATE", i).substring(0, 10).replaceAll("-", "/");
						if (!(this.compareDate(currentReturnDate, lendDate) < 0 
								|| this.compareDate(currentLendDate, rtnDate) > 0)) {
							this.messageBox("��ǰ�����Ѿ�����,�黹ʱ��Ϊ��" + rtnDate
									+ "�����ڴ�ʱ��֮����н���");
							return;
            			}
        			}
        		}
        	}
        }
        
		TNull tn = new TNull(Timestamp.class);
		TParm parm = this.getParmForTag("IPD_NO;MR_NO;LEND_CODE;MR_PERSON;REQ_DEPT");
		String QUE_SEQ = SystemTool.getInstance().getNo("ALL", "MRO",
				"QUE_SEQ", "QUE_SEQ"); // ����ȡ��ԭ��
		parm.setData("QUE_SEQ", QUE_SEQ);// ���ĺ�
		parm.setData("QUE_DATE", this.getValueString("QUE_DATE").substring(0, 10));// ��������
		parm.setData("ADM_HOSP", "");// ����Ժ�����ż����ã�
		parm.setData("ADM_AREA_CODE", Operator.getStation());// ����/����
		parm.setData("QUE_NO", "");// ������ţ��ż����ã�
		parm.setData("SESSION_CODE", "");// ʱ�Σ��ż����ã�
		parm.setData("ISSUE_CODE", "0");// ����״̬��0 �Ǽ�δ����
		parm.setData("RTN_DATE", this.getValueString("RTN_DATE").substring(0, 10));// Ӧ�黹����
		parm.setData("DUE_DATE", tn);// ��Ժ�󣬲���Ӧ�ù鵵ʱ����ʱ����
		parm.setData("CAN_FLG", "N");// ��ǣ�ȡ����
		parm.setData("ADM_TYPE", Parameter.getValue("ADM_TYPE"));// �ż�ס�� ��������ʹ��
		parm.setData("CASE_NO", Parameter.getValue("CASE_NO"));
		parm.setData("QUE_HOSP", Operator.getRegion());// Ŀǰ������������
		parm.setData("IN_DATE", tn);
		parm.setData("IN_PERSON", "");
		parm.setData("OUT_TYPE", "2");// ���ⷽʽ(0_����Һų���,1_סԺ����,2_���ĳ���)
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setData("LEND_BOX_CODE", this.getValueString("BOX_CODE"));// �����
		parm.setData("LEND_BOOK_NO", this.getValueString("BOOK_NO"));// ���
		parm.setData("OUT_DATE", "");// ���
		
        TParm result = MROLendRegTool.getInstance().insertQueue(parm);
        
        if(result.getErrCode()<0){
            this.messageBox("�Ǽ�ʧ�ܣ�"+ result.getErrName() + result.getErrText());
            return;
        }
        this.messageBox("�Ǽ���ϣ�");
        // �رյ�ǰģ̬��
        this.closeWindow();
    }
    
    /**
     * ���
     */
    public void onClear(){
        this.clearValue("LEND_CODE;QUE_DATE;RTN_DATE;BOOK_NO");
    }
    
    /**
     * ѡ�񡰽���ԭ�򡱺��Զ���д ����
     */
    public void getLendDays(){
        TParm result = MROLendRegTool.getInstance().queryLendDays(this.getValueString("LEND_CODE"));
        int days = result.getInt("LEND_DAY",0);
        Timestamp rueDate = StringTool.rollDate((Timestamp)((TTextFormat)this.getComponent("QUE_DATE")).getValue(),(long)days);
        this.setValue("RTN_DATE",rueDate);
    }
    
    /**
     * ��ѯ��ǰ�����İ�������
     */
    private TParm onQueryData(){
    	TParm parm = new TParm();
    	parm.setData("MR_NO", Parameter.getValue("MR_NO"));
    	parm.setData("ADM_TYPE", Parameter.getValue("ADM_TYPE"));
    	
    	parm = MROQueueTool.getInstance().selectMRO_MRV(parm);
    	
    	if (parm.getErrCode() < 0) {
    		err(parm.getErrCode() + " " + parm.getErrText());
    	}
    	
    	return parm;
    }
    
    /**
     * ��ѯ��ǰ�����İ�������
     */
    private boolean validateData(){
        if(StringUtils.isEmpty(this.getValueString("LEND_CODE"))){
        	this.messageBox("����д����ԭ��");
        	this.grabFocus("LEND_CODE");
        	return false;
        }
    	
        if(StringUtils.isEmpty(this.getValueString("QUE_DATE"))){
        	this.messageBox("����д�������ڣ�");
        	this.grabFocus("QUE_DATE");
        	return false;
        }
        
        // �������ڲ������ڵ�ǰʱ��
        String today = SystemTool.getInstance().getDate().toString().substring(0,
				10).replace('-', '/');
        int compareResult = this.compareDate(this.getValueString("QUE_DATE").substring(0, 10)
				.replace('-', '/'), today);
		if (compareResult < -1) {
			this.messageBox("���ڱȽϴ���");
			return false;
		} else if (compareResult < 0) {
			this.messageBox("�������ڲ������ڵ�ǰ����");
			return false;
		}
        
        if(StringUtils.isEmpty(this.getValueString("RTN_DATE"))){
        	this.messageBox("�黹���ڲ���Ϊ�գ�");
        	this.grabFocus("QUE_DATE");
        	return false;
        }
        
        if(StringUtils.isEmpty(this.getValueString("BOOK_NO"))){
        	this.messageBox("��ѡ����Ĳ�ţ�");
        	this.grabFocus("BOOK_NO");
        	return false;
        }
        
    	return true;
    }
    
    /**
     * ���ڱȽ�
     */
	private int compareDate(String date1, String date2) {
		DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
		try {
			Date dt1 = df.parse(date1);
			Date dt2 = df.parse(date2);
			if (dt1.getTime() > dt2.getTime()) {
				return 1;
			} else if (dt1.getTime() < dt2.getTime()) {
				return -1;
			} else {
				return 0;
			}
		} catch (Exception e) {
			err(e.getMessage());
		}
		return -2;
	}
}
