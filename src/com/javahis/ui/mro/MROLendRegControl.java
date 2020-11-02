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
 * <p>Title: 借阅注册</p>
 *
 * <p>Description: 借阅注册</p>
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
    private TParm Parameter;//存放参数
    //初始化
    public void onInit() {
        super.onInit();
        //接收参数
        Object obj = this.getParameter();
        if (obj != null) {
            if (obj instanceof TParm) { //判断是否是TParm
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

    //页面初始化
    private void pageInit() {
        this.setValue("IPD_NO", Parameter.getValue("IPD_NO"));
        this.setValue("MR_NO",Parameter.getValue("MR_NO"));
        this.setValue("PAT_NAME",Parameter.getValue("PAT_NAME"));
        this.setValue("VS_CODE",Parameter.getValue("VS_CODE"));
        this.setValue("QUE_DATE",SystemTool.getInstance().getDate());
        this.setValue("REQ_DEPT",Operator.getDept());//借阅人员部门
        this.setValue("MR_PERSON",Operator.getID());//借阅人员
        
        // add by wangbin 住院门诊病案借阅 20140917 START
        // 查询当前病患的案卷数据
        TParm parm = this.onQueryData();
        
        if (parm.getErrCode() < 0) {
        	this.messageBox("初始化案卷数据异常");
        	return;
        }
        
        if (parm.getCount() <= 0) {
        	if (StringUtils.equals(Parameter.getValue("ADM_TYPE"), "I")) {
        		this.messageBox("当前病患无住院病案");
        	} else {
        		this.messageBox("当前病患无门诊病案");
        	}
        	return;
        } else {
        	// 设定案卷
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
        	
        	// 设定案卷册号默认选中最大册
        	((TComboBox)this.getComponent("BOOK_NO")).setSelectedIndex(parm.getCount());
        }
        // add by wangbin 住院门诊病案借阅 20140917 END
    }
    
    /**
     * 保存
     */
    public void onSave(){
    	// 数据校验
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
        	this.messageBox_("查询归档信息失败！");
        	err(mrvResult.getErrName() + mrvResult.getErrCode() + mrvResult.getErrText());
            return;
        }
        
        if(mrvResult.getCount()<=0){
        	this.messageBox_("此病历无归档信息，不可借阅");
            return;
        }
        
        // 出库状况
        mrv.setData("ISSUE_CODE", "2");
        
        // 查询借阅表中已经存在的借阅数据
        mrvResult = MROQueueTool.getInstance().selectMroQueueInfo(mrv);
        
		if (mrvResult.getErrCode() < 0) {
			this.messageBox_("查询借阅数据失败！");
			err("ERR:" + mrvResult.getErrCode() + mrvResult.getErrText()
					+ mrvResult.getErrName());
			return;
		}
        
		// 当前借阅日期
		String currentLendDate = this.getValueString("QUE_DATE").substring(0, 10).replaceAll("-", "/");
		// 当前借阅预计归还时间
		String currentReturnDate = this.getValueString("RTN_DATE").substring(0, 10).replaceAll("-", "/");
		// 已存在的借阅时间
		String lendDate = "";
		// 已存在的借阅时间
		String rtnDate = "";
		
		// 如果存在借阅数据则先进行以下验证
        if(mrvResult.getCount() > 0){
        	for (int i = 0; i < mrvResult.getCount(); i++) {
        		lendDate = mrvResult.getValue("QUE_DATE", i).substring(0, 10).replaceAll("-", "/");
        		// 如果当前案卷没有出库
        		if (StringUtils.equals("0", mrvResult.getValue("ISSUE_CODE", i))) {
        			if (StringUtils.isEmpty(mrvResult.getValue("RTN_DATE", i))) {
        				// 借阅归还日期只能小于已有数据的借阅日期
    					if (this.compareDate(currentLendDate, lendDate) <= 0
    							&& this.compareDate(currentReturnDate, lendDate) >= 0) {
            				this.messageBox("当前填写的借阅归还期间内已有待出库的数据,请重新调整借阅时间");
            				return;
            			}
        			} else {
        				rtnDate = mrvResult.getValue("RTN_DATE", i).substring(0, 10).replaceAll("-", "/");
        				if (!(this.compareDate(currentReturnDate, lendDate) < 0 
								|| this.compareDate(currentLendDate, rtnDate) > 0)) {
							this.messageBox("当前填写的借阅归还期间内已有待出库的数据,借阅时间为【"
									+ lendDate + "】归还时间为【" + rtnDate
									+ "】请重新调整借阅时间");
            				return;
        				}
        			}
        		} else {
        			// 住院已出库的数据没有归还时间
        			if (StringUtils.isEmpty(mrvResult.getValue("RTN_DATE", i))) {
						if (this.compareDate(currentReturnDate, lendDate) >= 0) {
        					this.messageBox("当前案卷已经出库,因归还时间不详暂不可借阅");
        					return;
        				}
        			} else {
        				rtnDate = mrvResult.getValue("RTN_DATE", i).substring(0, 10).replaceAll("-", "/");
						if (!(this.compareDate(currentReturnDate, lendDate) < 0 
								|| this.compareDate(currentLendDate, rtnDate) > 0)) {
							this.messageBox("当前案卷已经出库,归还时间为【" + rtnDate
									+ "】请在此时间之后进行借阅");
							return;
            			}
        			}
        		}
        	}
        }
        
		TNull tn = new TNull(Timestamp.class);
		TParm parm = this.getParmForTag("IPD_NO;MR_NO;LEND_CODE;MR_PERSON;REQ_DEPT");
		String QUE_SEQ = SystemTool.getInstance().getNo("ALL", "MRO",
				"QUE_SEQ", "QUE_SEQ"); // 调用取号原则
		parm.setData("QUE_SEQ", QUE_SEQ);// 借阅号
		parm.setData("QUE_DATE", this.getValueString("QUE_DATE").substring(0, 10));// 借阅日期
		parm.setData("ADM_HOSP", "");// 借阅院区（门急诊用）
		parm.setData("ADM_AREA_CODE", Operator.getStation());// 病区/诊区
		parm.setData("QUE_NO", "");// 就诊序号（门急诊用）
		parm.setData("SESSION_CODE", "");// 时段（门急诊用）
		parm.setData("ISSUE_CODE", "0");// 出库状态：0 登记未出库
		parm.setData("RTN_DATE", this.getValueString("RTN_DATE").substring(0, 10));// 应归还日期
		parm.setData("DUE_DATE", tn);// 出院后，病历应该归档时间暂时不填
		parm.setData("CAN_FLG", "N");// 标记，取消否
		parm.setData("ADM_TYPE", Parameter.getValue("ADM_TYPE"));// 门急住别 病历调阅使用
		parm.setData("CASE_NO", Parameter.getValue("CASE_NO"));
		parm.setData("QUE_HOSP", Operator.getRegion());// 目前病案所在区域
		parm.setData("IN_DATE", tn);
		parm.setData("IN_PERSON", "");
		parm.setData("OUT_TYPE", "2");// 出库方式(0_门诊挂号出库,1_住院出库,2_借阅出库)
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setData("LEND_BOX_CODE", this.getValueString("BOX_CODE"));// 案卷号
		parm.setData("LEND_BOOK_NO", this.getValueString("BOOK_NO"));// 册号
		parm.setData("OUT_DATE", "");// 册号
		
        TParm result = MROLendRegTool.getInstance().insertQueue(parm);
        
        if(result.getErrCode()<0){
            this.messageBox("登记失败！"+ result.getErrName() + result.getErrText());
            return;
        }
        this.messageBox("登记完毕！");
        // 关闭当前模态框
        this.closeWindow();
    }
    
    /**
     * 清空
     */
    public void onClear(){
        this.clearValue("LEND_CODE;QUE_DATE;RTN_DATE;BOOK_NO");
    }
    
    /**
     * 选择“借阅原因”后自动填写 天数
     */
    public void getLendDays(){
        TParm result = MROLendRegTool.getInstance().queryLendDays(this.getValueString("LEND_CODE"));
        int days = result.getInt("LEND_DAY",0);
        Timestamp rueDate = StringTool.rollDate((Timestamp)((TTextFormat)this.getComponent("QUE_DATE")).getValue(),(long)days);
        this.setValue("RTN_DATE",rueDate);
    }
    
    /**
     * 查询当前病患的案卷数据
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
     * 查询当前病患的案卷数据
     */
    private boolean validateData(){
        if(StringUtils.isEmpty(this.getValueString("LEND_CODE"))){
        	this.messageBox("请填写借阅原因！");
        	this.grabFocus("LEND_CODE");
        	return false;
        }
    	
        if(StringUtils.isEmpty(this.getValueString("QUE_DATE"))){
        	this.messageBox("请填写借阅日期！");
        	this.grabFocus("QUE_DATE");
        	return false;
        }
        
        // 借阅日期不能早于当前时间
        String today = SystemTool.getInstance().getDate().toString().substring(0,
				10).replace('-', '/');
        int compareResult = this.compareDate(this.getValueString("QUE_DATE").substring(0, 10)
				.replace('-', '/'), today);
		if (compareResult < -1) {
			this.messageBox("日期比较错误");
			return false;
		} else if (compareResult < 0) {
			this.messageBox("借阅日期不能早于当前日期");
			return false;
		}
        
        if(StringUtils.isEmpty(this.getValueString("RTN_DATE"))){
        	this.messageBox("归还日期不能为空！");
        	this.grabFocus("QUE_DATE");
        	return false;
        }
        
        if(StringUtils.isEmpty(this.getValueString("BOOK_NO"))){
        	this.messageBox("请选择借阅册号！");
        	this.grabFocus("BOOK_NO");
        	return false;
        }
        
    	return true;
    }
    
    /**
     * 日期比较
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
