package action.reg;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jdo.sys.Behavior;
import jdo.sys.CRMOrderInfo;
import jdo.sys.CRMReg;
import jdo.sys.MediumMember;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

import com.capinfo.dataswap.webservice.DataSwapInterface;
import com.dongyang.action.TAction;
import com.dongyang.config.TConfig;
import com.dongyang.data.TParm;

public class REGCRMAction extends TAction {

	public REGCRMAction() {

	}

	public DataSwapInterface getWS() {
		TConfig config = TConfig
				.getConfig("WEB-INF\\config\\system\\TConfig.x");
		String ip = config.getString("", "WEB_SERVICES_IP_CRM");
		String url = "http://" + ip + "/CXFService/dataswap";
		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.setAddress(url);
		factory.setServiceClass(DataSwapInterface.class);
		DataSwapInterface service = (DataSwapInterface) factory.create();
		return service;
	}
	
	public TParm isSchday(TParm parm){
		TParm result = new TParm();
		DataSwapInterface service = this.getWS();
		String admDate = parm.getValue("admDate").replace("/", "-").substring(
				0, 10);
		String session = parm.getValue("session");
		String deptCode = parm.getValue("deptCode");
		String drCode = parm.getValue("drCode");
		String quegroup = parm.getValue("quegroup");
		if(parm.getValue("quegroup") == null)
			quegroup="";
		
		// System.out.println("诊间预约==drCode=="+drCode);
		String jsons = service.getOrder(admDate, session, deptCode,quegroup, drCode);
//		String jsons ="[{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25261','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':1,'tablePrefix':'','timeMultiple':5,'timePeriod':'08:30-08:35','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六','orderMrNo':'00000081'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25262','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':1,'tablePrefix':'','timeMultiple':5,'timePeriod':'08:35-08:40','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六','orderMrNo':'00000089'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25263','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':1,'tablePrefix':'','timeMultiple':5,'timePeriod':'08:40-08:45','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六','orderMrNo':'00000092'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25264','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':1,'tablePrefix':'','timeMultiple':5,'timePeriod':'08:45-08:50','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六','orderMrNo':'00000093'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25265','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':1,'tablePrefix':'','timeMultiple':5,'timePeriod':'08:50-08:55','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六','orderMrNo':'00000136'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25266','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':1,'tablePrefix':'','timeMultiple':5,'timePeriod':'08:55-09:00','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六','orderMrNo':'00000141'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25267','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':1,'tablePrefix':'','timeMultiple':5,'timePeriod':'09:00-09:05','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25268','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'09:05-09:10','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25269','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'09:10-09:15','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25270','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'09:15-09:20','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25271','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'09:20-09:25','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25272','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'09:25-09:30','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25273','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'09:30-09:35','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25274','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'09:35-09:40','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25275','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'09:40-09:45','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25276','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'09:45-09:50','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25277','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'09:50-09:55','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25278','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'09:55-10:00','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25279','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'10:00-10:05','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25280','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'10:05-10:10','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25281','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'10:10-10:15','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25282','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'10:15-10:20','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25283','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'10:20-10:25','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25284','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'10:25-10:30','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25285','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'10:30-10:35','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25286','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'10:35-10:40','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25287','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'10:40-10:45','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25288','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'10:45-10:50','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25289','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'10:50-10:55','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25290','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'10:55-11:00','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25291','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'11:00-11:05','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25292','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'11:05-11:10','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25293','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'11:10-11:15','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25294','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'11:15-11:20','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25295','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'11:20-11:25','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25296','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'11:25-11:30','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25297','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'11:30-11:35','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25298','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'11:35-11:40','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25299','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'11:40-11:45','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25300','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'11:45-11:50','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25301','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'11:50-11:55','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25302','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'11:55-12:00','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25303','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'12:00-12:05','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25304','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'12:05-12:10','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25305','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'12:10-12:15','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25306','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'12:15-12:20','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25307','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'12:20-12:25','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25308','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'12:25-12:30','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25309','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'12:30-12:35','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25310','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'12:35-12:40','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25311','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'12:40-12:45','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25312','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'12:45-12:50','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25313','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'12:50-12:55','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25314','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'12:55-13:00','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25315','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'13:00-13:05','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25316','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'13:05-13:10','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25317','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'13:10-13:15','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25318','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'13:15-13:20','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25319','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'13:20-13:25','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25320','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'13:25-13:30','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'}]";	
//		 System.out.println(" 查询CRM中对应时段的预约情况getOrder::==" + jsons); 
		writerLog2(getNowDate() +" 查询CRM中对应时段的班表" + jsons,"isSchday");

		CRMOrderInfo clazz = new CRMOrderInfo();
		List<CRMOrderInfo> order = REGCRMAction.getJavaCollectionReg(clazz,
				jsons);
		for (int i = 0; i < order.size(); i++) {
			CRMOrderInfo info = (CRMOrderInfo) order.get(i);
				result.addData("DEPT_CODE", info.getDepartId());
				result.addData("DEPT_DESC", info.getDepartName());
				result.addData("DR_CODE", info.getDoctorId());
				result.addData("DR_DESC", info.getDoctorName());
				result.addData("ADM_DATE", info.getDate());
				result.addData("START_TIME", info.getTimePeriod());
				result.addData("CLINICTYPE_CODE", info.getQuegroupId());
				result.addData("CLINICTYPE_DESC", info.getQuegroupName());
				result.addData("STATUS", info.getState());
				result.addData("MR_NO", info.getOrderMrNo());
				result.addData("PAT_NAME", info.getOrderName());
				result.addData("SEX_CODE", info.getOrderSex());
				result.addData("BIRTH_DATE", info.getOrderBirthday());
				result.addData("CELL_PHONE", info.getOrderTelephone());
				result.addData("IDNO", info.getValidNumber());
				result.addData("SOURCE", info.getSource()); // 来源类型：0： CRM 1：HIS
				result.addData("CRM_ID", info.getId()); 

		}
		result.setCount(result.getCount("DEPT_CODE"));
		return result;
	}

	/**
	 * 用于HIS在诊间预约时，查询CRM中对应时段的预约情况
	 * 
	 * @param date
	 * @param timeInteval
	 * @param dept
	 * @param doc
	 * @return
	 */
	public TParm getOrder(TParm parm) {
		TParm result = new TParm();
		DataSwapInterface service = this.getWS();
		String admDate = parm.getValue("admDate").replace("/", "-").substring(
				0, 10);
		String session = parm.getValue("session");
		String deptCode = parm.getValue("deptCode");
		String drCode = parm.getValue("drCode");
		String quegroup = parm.getValue("quegroup");
		if(parm.getValue("quegroup") == null)
			quegroup="";
		
		// System.out.println("诊间预约==drCode=="+drCode);
		String jsons = service.getOrder(admDate, session, deptCode,quegroup, drCode);
//		String jsons ="[{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25261','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':1,'tablePrefix':'','timeMultiple':5,'timePeriod':'08:30-08:35','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六','orderMrNo':'00000081'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25262','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':1,'tablePrefix':'','timeMultiple':5,'timePeriod':'08:35-08:40','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六','orderMrNo':'00000089'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25263','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':1,'tablePrefix':'','timeMultiple':5,'timePeriod':'08:40-08:45','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六','orderMrNo':'00000092'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25264','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':1,'tablePrefix':'','timeMultiple':5,'timePeriod':'08:45-08:50','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六','orderMrNo':'00000093'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25265','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':1,'tablePrefix':'','timeMultiple':5,'timePeriod':'08:50-08:55','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六','orderMrNo':'00000136'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25266','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':1,'tablePrefix':'','timeMultiple':5,'timePeriod':'08:55-09:00','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六','orderMrNo':'00000141'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25267','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':1,'tablePrefix':'','timeMultiple':5,'timePeriod':'09:00-09:05','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25268','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'09:05-09:10','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25269','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'09:10-09:15','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25270','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'09:15-09:20','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25271','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'09:20-09:25','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25272','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'09:25-09:30','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25273','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'09:30-09:35','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25274','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'09:35-09:40','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25275','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'09:40-09:45','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25276','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'09:45-09:50','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25277','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'09:50-09:55','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25278','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'09:55-10:00','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25279','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'10:00-10:05','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25280','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'10:05-10:10','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25281','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'10:10-10:15','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25282','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'10:15-10:20','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25283','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'10:20-10:25','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25284','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'10:25-10:30','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25285','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'10:30-10:35','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25286','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'10:35-10:40','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25287','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'10:40-10:45','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25288','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'10:45-10:50','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25289','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'10:50-10:55','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25290','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'10:55-11:00','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25291','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'11:00-11:05','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25292','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'11:05-11:10','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25293','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'11:10-11:15','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25294','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'11:15-11:20','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25295','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'11:20-11:25','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25296','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'11:25-11:30','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25297','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'11:30-11:35','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25298','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'11:35-11:40','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25299','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'11:40-11:45','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25300','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'11:45-11:50','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25301','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'11:50-11:55','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25302','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'11:55-12:00','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25303','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'12:00-12:05','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25304','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'12:05-12:10','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25305','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'12:10-12:15','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25306','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'12:15-12:20','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25307','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'12:20-12:25','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25308','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'12:25-12:30','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25309','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'12:30-12:35','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25310','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'12:35-12:40','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25311','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'12:40-12:45','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25312','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'12:45-12:50','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25313','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'12:50-12:55','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25314','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'12:55-13:00','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25315','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'13:00-13:05','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25316','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'13:05-13:10','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25317','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'13:10-13:15','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25318','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'13:15-13:20','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25319','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'13:20-13:25','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'},{'beginTime':'','cost':0,'date':'2014-11-25','deleYn':0,'departId':'020702','departName':'儿科','doctorId':'D001','doctorName':'陈慧红','endTime':'','id':'vs25320','orderBirthday':'','orderMemberCard':'','orderMenId':'','orderMenType':0,'orderName':'','orderSex':'','orderTelephone':'','position':'','source':0,'sourceNum':0,'state':0,'tablePrefix':'','timeMultiple':5,'timePeriod':'13:25-13:30','validNumber':'','validType':'','version':0,'visitNum':'','week':'星期六'}]";	
//		 System.out.println(" 查询CRM中对应时段的预约情况getOrder::==" + jsons); 
		writerLog2(getNowDate() +" 查询CRM中对应时段的预约情况getOrder::==" + jsons,"getOrder");

		CRMOrderInfo clazz = new CRMOrderInfo();
		List<CRMOrderInfo> order = REGCRMAction.getJavaCollectionReg(clazz,
				jsons);
		for (int i = 0; i < order.size(); i++) {
			CRMOrderInfo info = (CRMOrderInfo) order.get(i);
			// System.out.println(i+"---"+info.getState()+"==="+info.getTimePeriod());
			if (info.getState() != 6) {
				result.addData("DEPT_CODE", info.getDepartId());
				result.addData("DEPT_DESC", info.getDepartName());
				result.addData("DR_CODE", info.getDoctorId());
				result.addData("DR_DESC", info.getDoctorName());
				result.addData("ADM_DATE", info.getDate());
				result.addData("START_TIME", info.getTimePeriod());
				result.addData("CLINICTYPE_CODE", info.getQuegroupId());
				result.addData("CLINICTYPE_DESC", info.getQuegroupName());
//				if (info.getState() == 0) {
//					result.addData("STATUS", "N");
//				} else {
//					result.addData("STATUS", "Y");
//				}
				result.addData("STATUS", info.getState());
				result.addData("MR_NO", info.getOrderMrNo());
				result.addData("PAT_NAME", info.getOrderName());
				result.addData("SEX_CODE", info.getOrderSex());
				result.addData("BIRTH_DATE", info.getOrderBirthday());
				result.addData("CELL_PHONE", info.getOrderTelephone());
				result.addData("IDNO", info.getValidNumber());
				result.addData("SOURCE", info.getSource()); // 来源类型：0： CRM 1：HIS
				result.addData("CRM_ID", info.getId()); 

			}

		}
		result.setCount(result.getCount("DEPT_CODE"));
		return result;
	}

	/**
	 * 用于HIS通过预约号修改客户信息
	 * 
	 * @param parm
	 * @return
	 */
	public TParm updateMember(TParm parm) {
		TParm result = new TParm();
		DataSwapInterface service = this.getWS();
		String orderNum = parm.getValue("orderNum");
		MediumMember info = new MediumMember();
		info.setMrNo(parm.getValue("MR_NO"));
		info.setName(parm.getValue("PAT_NAME"));
		info.setBirthday("".equals(parm.getValue("BIRTH_DATE")) ? "" : parm
				.getValue("BIRTH_DATE").toString().replace("/", "-").substring(
						0, 10));
		info.setTelephone(parm.getValue("CELL_PHONE"));
		info.setValidNumber(parm.getValue("IDNO"));
		info.setValidType(parm.getValue("ID_TYPE"));
		info.setEmail(parm.getValue("E_MAIL"));
		info.setSex(parm.getValue("SEX_CODE"));
		JSONObject object = JSONObject.fromObject(info);
		String jsonObject = object.toString();
		// System.out.println("通过预约号修改客户信息updateMember::---"+jsonObject);
		writerLog2(getNowDate() +"通过预约号修改客户信息updateMember::---"+jsonObject,"updateMember");
		boolean flg = service.updateMember(orderNum, jsonObject);
		result.addData("flg", flg);
		return result;
	}

	/**
	 * 在诊间预约取消时，HIS通过对应条件告知CRM进行对应预约号源的取消。
	 * 
	 * @param parm
	 * @return
	 */
	public TParm cancleOrderInfo(TParm parm) {
		TParm result = new TParm();
		DataSwapInterface service = this.getWS();
		String mrNo = parm.getValue("mrNo");
		String date = parm.getValue("date");
		String timeInteval = parm.getValue("timeInteval");
		String dept = parm.getValue("dept");
		String doc = parm.getValue("doc");
		String quegroup = parm.getValue("quegroup");
		boolean flg = service.cancleOrderInfo(mrNo, date, timeInteval, dept,quegroup,
				doc);
		result.addData("flg", flg);
		// System.out.println("cancleOrderInfo==="+flg);
		 writerLog2(getNowDate() +"cancleOrderInfo==="+flg,"cancleOrderInfo");
		return result;
	}

	/**
	 * 在诊间预约成功时，HIS向CRM中写入已预约的信息
	 * 
	 * @param parm
	 * @return
	 */
	public TParm addOrderInfo(TParm parm) {
		TParm result = new TParm();
		DataSwapInterface service = this.getWS();
		// String mrNo, String date,String timeInteval,String dept,String doc
		String mrNo = parm.getValue("mrNo");
		String date = parm.getValue("date").replace("/", "-").substring(0, 10);
		String timeInteval = parm.getValue("timeInteval");
		String dept = parm.getValue("dept");
		String doc = parm.getValue("doc");
		String quegroup = parm.getValue("quegroup");
		boolean flg = service.addOrderInfo(mrNo, date, timeInteval, dept ,quegroup, doc);
		 writerLog2(getNowDate() +"addOrderInfo==="+flg,"addOrderInfo");
		result.addData("flg", flg);
		return result;
	}

	/**
	 * 用于HIS通过病案号获取客户预约信息
	 * 
	 * @param mrNo
	 * @return
	 */
	public TParm orderInfo(TParm parm) {
		TParm result = new TParm();
		DataSwapInterface service = this.getWS();
		String mrNo = parm.getValue("MR_NO");
		String jsons = service.orderInfo(mrNo);
//		String jsons ="[{'beginTime':'','cost':300,'date':'2014-12-22','deleYn':0,'departId':'020325','departName':'儿外科','doctorId':'chennan','doctorName':'陈楠','endTime':'','id':'vs452668','orderBirthday':'2014-04-07','orderMemberCard':'','orderMenId':'pot3002','orderMenType':0,'orderName':'111','orderSex':'0','orderTelephone':'11111111111','position':'副主任医师','sourceNum':0,'state':4,'tablePrefix':'','timeMultiple':5,'timePeriod':'15:50-16:00','validNumber':'1111','validType':'','version':2,'visitNum':'','week':'星期四','quegroupId':'05','quegroupName':'诊疗费-主治医师'}]";		
//		 System.out.println("通过病案号获取客户预约信息orderInfo::==" + jsons);
		 if(jsons != null){
			 CRMOrderInfo clazz = new CRMOrderInfo();
				List order = this.getJavaCollectionReg(clazz, jsons);
				for (int i = 0; i < order.size(); i++) {
					CRMOrderInfo info = (CRMOrderInfo) order.get(i);
					if(info != null){
						result.addData("DEPT_CODE", info.getDepartId());
						result.addData("DEPT_DESC", info.getDepartName());
						result.addData("DR_CODE", info.getDoctorId());
						result.addData("DR_DESC", info.getDoctorName());
						result.addData("ADM_DATE", info.getDate());
						result.addData("START_TIME", info.getTimePeriod());
						result.addData("CRM_ID", info.getId());
						result.addData("CLINICTYPE_CODE", info.getQuegroupId());
						result.addData("CLINICTYPE_DESC", info.getQuegroupName());
					}
					
				}
				result.setCount(result.getCount("DEPT_CODE"));
		 }
		
		 writerLog2(getNowDate() +"orderInfo--result==="+result,"orderInfo");
		return result;

	}

	/**
	 * 用于HIS通过预约号获取客户信息
	 * 
	 * @param orderNum
	 * @return
	 */
	public TParm findMember(TParm parm) {
		TParm result = new TParm();
		DataSwapInterface service = this.getWS();
		String orderNum = parm.getValue("orderNum", 0);
		// System.out.println("orderNum==="+orderNum);
		String value = service.findMember(orderNum);
//		 String value ="{'birthHospital':'','birthday':'','cardId':'','contract1Unit':'','contract2Unit':'','ctz1Code':'','ctz2Code':'','ctz3Code':'','deleYn':0,'email':'','enName':'','formerName':'','id':'pot3009','insurance1Card':'','insurance2Card':'','jianpin':'','learnMode':'','marital':'','mrNo':'','name':'测试','nation':'','nationality':'','phone':'15111111111','presentAddress':'','presentAddressEx':'','religionCode':'','residAddress':'','residAddressEx':'','school':'','schoolPhone':'','sex':'1','tablePrefix':'','validNumber':'','validType':'02','version':0,'wechat':''}";
		 writerLog2(getNowDate() +"通过预约号获取客户信息findMember::==" + value,"findMember");
		MediumMember clazz = new MediumMember();
		JSONObject jsonObject = JSONObject.fromObject(value);
		MediumMember patInfo = (MediumMember) JSONObject.toBean(jsonObject,
				clazz.getClass());
		
		if(patInfo == null){
			return new TParm();
		}
		
		result.addData("MR_NO", patInfo.getMrNo());
		result.addData("PAT_NAME", patInfo.getName());
		result.addData("ID_TYPE", patInfo.getValidType());
		result.addData("IDNO", patInfo.getValidNumber());
		result.addData("SEX_CODE", patInfo.getSex());
		result.addData("BIRTH_DATE", patInfo.getBirthday());
		result.addData("NATION_CODE", patInfo.getNationality());
		result.addData("SPECIES_CODE", patInfo.getNation());
		result.addData("MARRIAGE_CODE", patInfo.getMarital());
		result.addData("RESID_POST_CODE", patInfo.getResidAddress());
		result.addData("RESID_ADDRESS", patInfo.getResidAddressEx());
		result.addData("POST_CODE", patInfo.getPresentAddress());
		result.addData("CURRENT_ADDRESS", patInfo.getPresentAddressEx());
		result.addData("CELL_PHONE", patInfo.getTelephone());
		result.addData("E_MAIL", patInfo.getEmail());
		result.addData("ID", patInfo.getId());
		result.setCount(result.getCount("MR_NO"));
		return result;
	}

	public TParm createMember1(TParm parm) {
		TParm result = new TParm();
		DataSwapInterface service = this.getWS();
		MediumMember info = new MediumMember();
		info.setMrNo(parm.getValue("MR_NO"));
		info.setName(parm.getValue("PAT_NAME"));
		info.setJianpin(parm.getValue("PY1"));
		info.setEnName(parm.getValue("FIRST_NAME") + " "
				+ parm.getValue("LAST_NAME"));
		info.setFormerName(parm.getValue("OLDNAME"));
		info.setValidType(parm.getValue("ID_TYPE"));
		info.setValidNumber(parm.getValue("IDNO"));
		info.setSex(parm.getValue("SEX_CODE"));
		info.setBirthday("".equals(parm.getValue("BIRTH_DATE")) ? "" : parm
				.getValue("BIRTH_DATE").replace("/", "-").substring(0, 10));
		info.setNationality(parm.getValue("NATION_CODE"));
		info.setNation(parm.getValue("NATION_CODE2"));
		info.setReligionCode(parm.getValue("RELIGION"));
		info.setMarital(parm.getValue("MARRIAGE"));
		info.setResidAddress(parm.getValue("RESID_POST_CODE"));
		info.setResidAddressEx(parm.getValue("RESID_ADDRESS"));
		info.setBirthAddress(parm.getValue("HOMEPLACE_CODE"));
		info.setPresentAddress(parm.getValue("POST_CODE"));
		info.setPresentAddressEx(parm.getValue("CURRENT_ADDRESS"));
		info.setBirthHospital(parm.getValue("BIRTH_HOSPITAL"));
		info.setSchool(parm.getValue("SCHOOL_NAME"));
		info.setSchoolPhone(parm.getValue("SCHOOL_TEL"));
		info.setPhone(parm.getValue("TEL_HOME"));
		info.setTelephone(parm.getValue("CELL_PHONE"));
		info.setLearnMode(parm.getValue("SOURCE"));
		info.setEmail(parm.getValue("E_MAIL"));
		info.setContract1Unit(parm.getValue("INSURANCE_COMPANY1_CODE"));
		info.setContract2Unit(parm.getValue("INSURANCE_COMPANY2_CODE"));
		info.setInsurance1Card(parm.getValue("INSURANCE_NUMBER1"));
		info.setInsurance2Card(parm.getValue("INSURANCE_NUMBER2"));

		info.setFirstFamilyName(parm.getValue("GUARDIAN1_NAME"));
		info.setFirstFamilyRelationship(parm.getValue("GUARDIAN1_RELATION"));
		info.setFirstFamilyContactWay(parm.getValue("GUARDIAN1_TEL"));
		info.setFirstFamilyTelephone(parm.getValue("GUARDIAN1_PHONE"));
		info.setFirstFamilyOrganization(parm.getValue("GUARDIAN1_COM"));
		info.setFirstFamilyValidType(parm.getValue("GUARDIAN1_ID_TYPE"));
		info.setFirstFamilyValidNumber(parm.getValue("GUARDIAN1_ID_CODE"));
		info.setFirstFamilyEmail(parm.getValue("GUARDIAN1_EMAIL"));
		info.setSecondFamilyName(parm.getValue("GUARDIAN2_NAME"));
		info.setSecondFamilyRelationship(parm.getValue("GUARDIAN2_RELATION"));
		info.setSecondFamilyContactWay(parm.getValue("GUARDIAN2_TEL"));
		info.setSecondFamilyTelephone(parm.getValue("GUARDIAN2_PHONE"));
		info.setSecondFamilyOrganization(parm.getValue("GUARDIAN2_COM"));
		info.setSecondFamilyValidType(parm.getValue("GUARDIAN2_ID_TYPE"));
		info.setSecondFamilyValidNumber(parm.getValue("GUARDIAN2_ID_CODE"));
		info.setSecondFamilyEmail(parm.getValue("GUARDIAN2_EMAIL"));

		info.setCtz1Code(parm.getValue("CTZ1_CODE"));
		info.setCtz2Code(parm.getValue("CTZ2_CODE"));
		info.setCtz3Code(parm.getValue("CTZ3_CODE"));
		info.setSpecialDiet(parm.getValue("SPECIAL_DIET"));
		info.setRegister1Code(parm.getValue("REG_CTZ1_CODE"));
		info.setRegister2Code(parm.getValue("REG_CTZ2_CODE"));

		info.setMemCode(parm.getValue("MEM_TYPE"));
		info.setFamilyDoctor(parm.getValue("FAMILY_DOCTOR"));
		info.setAccountManagerCode(parm.getValue("ACCOUNT_MANAGER_CODE"));
		info.setStartDate(parm.getValue("START_DATE").replace("/", "-"));
		info.setEndDate(parm.getValue("END_DATE").replace("/", "-"));
		info.setBuyMoon(parm.getValue("BUY_MONTH_AGE"));
		info.setHappenMoon(parm.getValue("HAPPEN_MONTH_AGE"));

		info.setHairpinType(parm.getValue("MEM_CODE"));
		info.setHairpinCause(parm.getValue("REASON"));
		info.setPredictStartDate(parm.getValue("START_DATE_TRADE").replace("/",
				"-"));
		info.setPredictEndDate(parm.getValue("END_DATE_TRADE")
				.replace("/", "-"));
		info.setCost(parm.getValue("MEM_FEE"));
		info.setSponsor1(parm.getValue("INTRODUCER1"));
		info.setSponsor2(parm.getValue("INTRODUCER2"));
		info.setSponsor3(parm.getValue("INTRODUCER3"));
		info.setRemark(parm.getValue("DESCRIPTION"));

		info.setCardId(parm.getValue("MEM_CODE"));
		JSONObject object = JSONObject.fromObject(info);
		String jsonObject = object.toString();
		writerLog2(getNowDate() +"添加新客户jsonObject==" + jsonObject,"createMember1");
		boolean flg = service.createMember(jsonObject);
		result.addData("flg", flg);
		return result;
	}

	/**
	 * 用于HIS通过病案号修改客户信息接口
	 * 
	 * @param parm
	 * @return
	 */
	public TParm updateMemberByMrNo1(TParm parm) {
		// System.out.println("11111用于HIS通过病案号修改客户信息接口===updateMemberByMrNo1");
		TParm result = new TParm();
		DataSwapInterface service = this.getWS();
		MediumMember info = new MediumMember();
		info.setMrNo(parm.getValue("MR_NO"));
		info.setName(parm.getValue("PAT_NAME"));
		info.setJianpin(parm.getValue("PY1"));
		info.setEnName(parm.getValue("FIRST_NAME") + " "
				+ parm.getValue("LAST_NAME"));
		info.setFormerName(parm.getValue("OLDNAME"));
		info.setValidType(parm.getValue("ID_TYPE"));
		info.setValidNumber(parm.getValue("IDNO"));
		info.setSex(parm.getValue("SEX_CODE"));
		info.setBirthday("".equals(parm.getValue("BIRTH_DATE")) ? "" : parm
				.getValue("BIRTH_DATE").replace("/", "-").substring(0, 10));
		info.setNationality(parm.getValue("NATION_CODE"));
		info.setNation(parm.getValue("NATION_CODE2"));
		info.setReligionCode(parm.getValue("RELIGION"));
		info.setMarital(parm.getValue("MARRIAGE"));
		info.setResidAddress(parm.getValue("RESID_POST_CODE"));
		info.setResidAddressEx(parm.getValue("RESID_ADDRESS"));
		info.setBirthAddress(parm.getValue("HOMEPLACE_CODE"));
		info.setPresentAddress(parm.getValue("POST_CODE"));
		info.setPresentAddressEx(parm.getValue("CURRENT_ADDRESS"));
		info.setBirthHospital(parm.getValue("BIRTH_HOSPITAL"));
		info.setSchool(parm.getValue("SCHOOL_NAME"));
		info.setSchoolPhone(parm.getValue("SCHOOL_TEL"));
		info.setPhone(parm.getValue("TEL_HOME"));
		info.setTelephone(parm.getValue("CELL_PHONE"));
		info.setLearnMode(parm.getValue("SOURCE"));
		info.setEmail(parm.getValue("E_MAIL"));
		info.setContract1Unit(parm.getValue("INSURANCE_COMPANY1_CODE"));
		info.setContract2Unit(parm.getValue("INSURANCE_COMPANY2_CODE"));
		info.setInsurance1Card(parm.getValue("INSURANCE_NUMBER1"));
		info.setInsurance2Card(parm.getValue("INSURANCE_NUMBER2"));
		info.setFirstFamilyName(parm.getValue("GUARDIAN1_NAME"));
		info.setFirstFamilyRelationship(parm.getValue("GUARDIAN1_RELATION"));
		info.setFirstFamilyContactWay(parm.getValue("GUARDIAN1_TEL"));
		info.setFirstFamilyTelephone(parm.getValue("GUARDIAN1_PHONE"));
		info.setFirstFamilyOrganization(parm.getValue("GUARDIAN1_COM"));
		info.setFirstFamilyValidType(parm.getValue("GUARDIAN1_ID_TYPE"));
		info.setFirstFamilyValidNumber(parm.getValue("GUARDIAN1_ID_CODE"));
		info.setFirstFamilyEmail(parm.getValue("GUARDIAN1_EMAIL"));
		info.setSecondFamilyName(parm.getValue("GUARDIAN2_NAME"));
		info.setSecondFamilyRelationship(parm.getValue("GUARDIAN2_RELATION"));
		info.setSecondFamilyContactWay(parm.getValue("GUARDIAN2_TEL"));
		info.setSecondFamilyTelephone(parm.getValue("GUARDIAN2_PHONE"));
		info.setSecondFamilyOrganization(parm.getValue("GUARDIAN2_COM"));
		info.setSecondFamilyValidType(parm.getValue("GUARDIAN2_ID_TYPE"));
		info.setSecondFamilyValidNumber(parm.getValue("GUARDIAN2_ID_CODE"));
		info.setSecondFamilyEmail(parm.getValue("GUARDIAN2_EMAIL"));
		info.setCtz1Code(parm.getValue("CTZ1_CODE"));
		info.setCtz2Code(parm.getValue("CTZ2_CODE"));
		info.setCtz3Code(parm.getValue("CTZ3_CODE"));
		info.setSpecialDiet(parm.getValue("SPECIAL_DIET"));
		info.setRegister1Code(parm.getValue("REG_CTZ1_CODE"));
		info.setRegister2Code(parm.getValue("REG_CTZ2_CODE"));
		info.setMemCode(parm.getValue("MEM_TYPE"));
		info.setFamilyDoctor(parm.getValue("FAMILY_DOCTOR"));
		info.setAccountManagerCode(parm.getValue("ACCOUNT_MANAGER_CODE"));
		info.setStartDate(parm.getValue("START_DATE").replace("/", "-"));
		info.setEndDate(parm.getValue("END_DATE").replace("/", "-"));
		info.setBuyMoon(parm.getValue("BUY_MONTH_AGE"));
		info.setHappenMoon(parm.getValue("HAPPEN_MONTH_AGE"));
		info.setHairpinType(parm.getValue("MEM_CODE"));
		info.setHairpinCause(parm.getValue("REASON"));
		info.setPredictStartDate(parm.getValue("START_DATE_TRADE").replace("/",
				"-"));
		info.setPredictEndDate(parm.getValue("END_DATE_TRADE")
				.replace("/", "-"));
		info.setCost(parm.getValue("MEM_FEE"));
		info.setSponsor1(parm.getValue("INTRODUCER1"));
		info.setSponsor2(parm.getValue("INTRODUCER2"));
		info.setSponsor3(parm.getValue("INTRODUCER3"));
		info.setRemark(parm.getValue("DESCRIPTION"));
		info.setCardId(parm.getValue("MEM_CODE"));
		JSONObject object = JSONObject.fromObject(info);
		String jsonObject = object.toString();
		String mrNo = parm.getValue("MR_NO");
		writerLog2(getNowDate() +"通过病案号修改客户信息====" + jsonObject,"updateMemberByMrNo1");
		boolean flg = service.updateMemberByMrNo(mrNo, jsonObject);
		result.addData("flg", flg);
		return result;
	}

	// /**
	// * 新增客户信息接口：用于HIS添加新客户信息
	// * @param parm
	// * @return
	// */
	// public TParm createMember(TParm parm){
	// TParm result = new TParm();
	// DataSwapInterface service = this.getWS();
	// CRMInfo info = new CRMInfo();
	// info.setMrNo(parm.getValue("MR_NO"));
	// info.setName(parm.getValue("PAT_NAME"));
	// info.setValidType(parm.getValue("ID_TYPE"));
	// info.setValidNumber(parm.getValue("IDNO"));
	// info.setSex(parm.getValue("SEX_CODE"));
	// info.setBirthday(parm.getValue("BIRTH_DATE").replace("/", "-"));
	// info.setNationality(parm.getValue("NATION_CODE"));
	// info.setNation(parm.getValue("NATION_CODE2"));
	// info.setMarital(parm.getValue("MARRIAGE"));
	// info.setResidAddress(parm.getValue("RESID_POST_CODE"));
	// info.setResidAddressEx(parm.getValue("RESID_ADDRESS"));
	// info.setPresentAddress(parm.getValue("POST_CODE"));
	// info.setPresentAddressEx(parm.getValue("CURRENT_ADDRESS"));
	// info.setPhone(parm.getValue("CELL_PHONE"));
	// JSONObject object = JSONObject.fromObject(info);
	// String jsonObject= object.toString();
	// System.out.println("添加新客户jsonObject=="+jsonObject);
	// boolean flg= service.createMember(jsonObject);
	// result.addData("flg", flg);
	// return result;
	// }
	// /**
	// * 用于HIS通过病案号修改客户信息接口
	// * @param parm
	// * @return
	// */
	// public TParm updateMemberByMrNo(TParm parm){
	// TParm result = new TParm();
	// DataSwapInterface service = this.getWS();
	// CRMInfo info = new CRMInfo();
	// info.setMrNo(parm.getValue("MR_NO"));
	// info.setName(parm.getValue("PAT_NAME"));
	// info.setValidType(parm.getValue("ID_TYPE"));
	// info.setValidNumber(parm.getValue("IDNO"));
	// info.setSex(parm.getValue("SEX_CODE"));
	// info.setBirthday(parm.getValue("BIRTH_DATE"));
	// info.setNationality(parm.getValue("NATION_CODE"));
	// info.setNation(parm.getValue("NATION_CODE2"));
	// info.setMarital(parm.getValue("MARRIAGE"));
	// info.setResidAddress(parm.getValue("RESID_POST_CODE"));
	// info.setResidAddressEx(parm.getValue("RESID_ADDRESS"));
	// info.setPresentAddress(parm.getValue("POST_CODE"));
	// info.setPresentAddressEx(parm.getValue("CURRENT_ADDRESS"));
	// info.setPhone(parm.getValue("CELL_PHONE"));
	// JSONObject object = JSONObject.fromObject(info);
	// String jsonObject= object.toString();
	// String mrNo = parm.getValue("MR_NO");
	// System.out.println("通过病案号修改客户信息==="+jsonObject);
	// boolean flg= service.updateMemberByMrNo(mrNo, jsonObject);
	// result.addData("flg", flg);
	// return result;
	// }

	// 把传入的json的字符串对象转换为java的对象 CRMInfo
	public static List<CRMOrderInfo> getJavaCollectionReg(CRMOrderInfo clazz,
			String jsons) {
		List<CRMOrderInfo> objs = new ArrayList<CRMOrderInfo>();
		// List list = JSON.parseArray(jsons, CRMOrderInfo.class);

		JSONArray jsonArray = JSONArray.fromObject(jsons);
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			CRMOrderInfo obj = (CRMOrderInfo) JSONObject.toBean(jsonObject,
					clazz.getClass());
			objs.add(obj);
		}

		return objs;
	}

	/**
	 * 用于HIS通知停诊信息，并对停诊涉及的客户进行相应的处理（短信通知，电话通知等），对换诊处理和停诊类似，停诊时，所有数据不变，但是有停诊标记位；
	 * 对换诊时，科室、医生可能会发生变化
	 * 
	 * @param parm
	 * @return
	 */
	public TParm updateOrder(TParm parm) {
		TParm result = new TParm();
		DataSwapInterface service = this.getWS();
		String beginDate = parm.getValue("sDate");
		String endDate = parm.getValue("eDate");
		String deptCode = parm.getValue("dept");
		String drCode = parm.getValue("dr");
		String timePeriod = parm.getValue("timePeriod");
		String quegroup = parm.getValue("quegroup");
		boolean flg = service.updateOrder(beginDate, endDate,timePeriod, deptCode ,quegroup, drCode);
		result.setData("flg", flg);
		return result;

	}

	/**
	 * 用于HIS向CRM中同步变化的科室、医生等基础数据
	 * 
	 * @return
	 */

	public TParm updateCode() {
		TParm result = new TParm();
		DataSwapInterface service = this.getWS();
		// System.out.println("用于HIS向CRM中同步变化的科室、医生等基础数据==start");
		boolean flg = service.updateCode();
		result.setData("flg", flg);
		// System.out.println("updateCode==向CRM中同步变化的科室、医生等基础数据=="+result);
		return result;
	}

	/**
	 * 用于HIS向CRM中同步客户行为，在HIS中的客户行为有：会员注册，会员卡升级，会员续费，会员退卡，会员急诊，住院，出院，手术，门诊。
	 * 诊断结论的记录，门诊总的消费记录，产科、新生儿建档的信息。记录相关内容及操作人员、科室等信息。
	 * 
	 * @param parm
	 * @return
	 */
	public TParm loadBehavior(TParm parm) {
		TParm result = new TParm();
		DataSwapInterface service = this.getWS();
		String mrNo = parm.getValue("MR_NO");
		Behavior behavior = new Behavior();
		behavior.setConductId(parm.getValue("conductId"));
		behavior.setConductName(parm.getValue("conductName"));
		behavior.setSource(parm.getValue("source"));
		behavior.setEvent(parm.getValue("event"));
		behavior.setContent(parm.getValue("content"));
		behavior
				.setBehaviorDate(parm.getValue("behaviorDate").substring(0, 19));
		behavior.setCreateUser(parm.getValue("createUser"));
		behavior.setDepartmentId(parm.getValue("departmentId"));
		behavior.setDepartmentName(parm.getValue("department"));
		behavior.setDoctorId(parm.getValue("doctorId"));
		behavior.setDoctorName(parm.getValue("doctor"));
		JSONObject object = JSONObject.fromObject(behavior);
		String jsonObject = object.toString();
//		System.out.println("用于HIS向CRM中同步客户行为===" + mrNo + "-------"+ jsonObject);
		boolean flg = service.loadBehavior(mrNo, jsonObject);
		result.setData("flg", flg);
		return result;
	}

	/**
	 * 用于HIS停诊开诊时，调用通知CRM停诊开诊信息
	 * 
	 * @param parm
	 * @return
	 */
	public TParm cancelUpdateOrder(TParm parm) {
		TParm result = new TParm();
		DataSwapInterface service = this.getWS();
		String beginDate = parm.getValue("sDate");
		String endDate = parm.getValue("eDate");
		String deptCode = parm.getValue("dept");
		String drCode = parm.getValue("dr");
		String timePeriod = parm.getValue("timePeriod");
		String quegroup = parm.getValue("quegroup");
		boolean flg = service.cancelUpdateOrder(beginDate, endDate, timePeriod,deptCode,quegroup,drCode);
		result.setData("flg", flg);
		return result;

	}

	/**
	 *用于HIS向CRM传输新增排班
	 * 
	 * @param parm
	 * @return
	 */
	public TParm addOrder(TParm parm) {
		TParm result = new TParm();
		DataSwapInterface service = this.getWS();

		JSONArray jsonArray = new JSONArray();
		if (parm.getCount() > 0) {
			List list = new ArrayList();
			CRMReg regInfo;
			for (int i = 0; i < parm.getCount(); i++) {
				regInfo = new CRMReg();
				// System.out.println(parm.getValue("REALDEPT_CODE", i));
				regInfo.setDepartmenttId(parm.getValue("REALDEPT_CODE", i));
				regInfo.setDepartmenttName(parm.getValue("REALDEPT_DESC", i));
				regInfo.setDoctorId(parm.getValue("REALDR_CODE", i));
				regInfo.setDoctorName(parm.getValue("REALDR_DESC", i));
				regInfo.setReserveDate(parm.getValue("ADM_DATE", i));
				regInfo.setReserveTimeSection(parm.getValue("START_TIME", i));
				regInfo.setSessionId(parm.getValue("SESSION_CODE", i));
				regInfo.setSessionName(parm.getValue("SESSION_DESC", i));
				regInfo.setRegFee(parm.getValue("OWN_PRICE", i));
				regInfo.setClinicroom(parm.getValue("CLINICROOM_DESC", i));
				regInfo.setClinicarea(parm.getValue("CLINIC_DESC", i));
				regInfo.setInterveenTime(parm.getValue("I_TIME", i));
				regInfo.setQuegroupId(parm.getValue("QUEGROUP_CODE", i));
				regInfo.setQuegroupName(parm.getValue("QUEGROUP_DESC", i));
				regInfo.setClinictypeId(parm.getValue("CLINICTYPE_CODE", i));
				regInfo.setClinictypeName(parm.getValue("CLINICTYPE_DESC", i));
				list.add(regInfo);
			}
			for (int i = 0; i < list.size(); i++) {
				jsonArray.add(JSONObject.fromObject(list.get(i)));
			}
		}
		String orderJson = jsonArray.toString();
		writerLog2(getNowDate() +"用于HIS向CRM传输新增排班==="+orderJson, "addOrder");
		boolean flg = service.addOrder(orderJson);
		result.setData("flg", flg);
		return result;

	}

	/**
	 * HIS向CRM传送挂号总数
	 * 
	 * @param parm
	 * @return
	 */
	public TParm orderCount(TParm parm) {
		TParm result = new TParm();
		DataSwapInterface service = this.getWS();
		String date = parm.getValue("date");
		String deptCode = parm.getValue("deptCode");
		String drCode = parm.getValue("drCode");
		String quegroup = parm.getValue("quegroup");
		Integer count = parm.getInt("count");
		boolean flg = service.orderCount(date, deptCode, quegroup,drCode, count);
		result.setData("flg", flg);
		return result;
	}
	/**
	 * 删除班表
	 * @param parm
	 * @return
	 */
	public TParm delOrder(TParm parm) {
		TParm result = new TParm();
		DataSwapInterface service = this.getWS();
		JSONArray jsonArray = new JSONArray();
		if (parm.getCount() > 0) {
			List list = new ArrayList();
			CRMReg regInfo;
			for (int i = 0; i < parm.getCount(); i++) {
				regInfo = new CRMReg();
				// System.out.println(parm.getValue("REALDEPT_CODE", i));
				regInfo.setDepartmenttId(parm.getValue("REALDEPT_CODE", i));
				regInfo.setDepartmenttName(parm.getValue("REALDEPT_DESC", i));
				regInfo.setDoctorId(parm.getValue("REALDR_CODE", i));
				regInfo.setDoctorName(parm.getValue("REALDR_DESC", i));
				regInfo.setReserveDate(parm.getValue("ADM_DATE", i));
				regInfo.setReserveTimeSection(parm.getValue("START_TIME", i));
				regInfo.setSessionId(parm.getValue("SESSION_CODE", i));
				regInfo.setSessionName(parm.getValue("SESSION_DESC", i));
				regInfo.setRegFee(parm.getValue("OWN_PRICE", i));
				regInfo.setClinicroom(parm.getValue("CLINICROOM_DESC", i));
				regInfo.setClinicarea(parm.getValue("CLINIC_DESC", i));
				regInfo.setInterveenTime(parm.getValue("I_TIME", i));
				regInfo.setQuegroupId(parm.getValue("QUEGROUP_CODE", i));
				regInfo.setQuegroupName(parm.getValue("QUEGROUP_DESC", i));
				regInfo.setClinictypeId(parm.getValue("CLINICTYPE_CODE", i));
				regInfo.setClinictypeName(parm.getValue("CLINICTYPE_DESC", i));
				list.add(regInfo);
			}
			for (int i = 0; i < list.size(); i++) {
				jsonArray.add(JSONObject.fromObject(list.get(i)));
			}
		}
		String orderJson = jsonArray.toString();
		writerLog2(getNowDate() +"用于HIS向CRM传输新增排班==="+orderJson,"delOrder");
		boolean flg = service.delOrder(orderJson);
		writerLog2(getNowDate() +"CRM删除班表返回值 ==="+flg,"delOrder");
		result.setData("flg", flg);
		return result;

	}
	
	/**
	 * 测试用的日志文件-2
	 * @param msg
	 * @author zhangp
	 */
	public static void writerLog2(String msg,String name) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//		String name = "getVipSchday";
		name +=format.format(new Date());
		File f = new File("C:\\JavaHisCRM\\ws_logs\\"+name+".log");
		BufferedWriter out = null;
		try {
			if (!f.exists()) {
				f.createNewFile();// 如果SPC.log不存在，则创建一个新文件
			}
			out = new BufferedWriter(new FileWriter(f, true));// 参数true表示将输出追加到文件内容的末尾而不覆盖原来的内容
			out.write(msg);
			out.newLine(); // 换行
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public String getNowDate(){
		SimpleDateFormat  df = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
		String dd = df.format(new Date());
		return dd;
	}

}
