package com.javahis.ui.mem;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.net.ntp.TimeStamp;

import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.TypeTool;
/**
 * 修改新生儿出生日期
 * @author Administrator
 *
 */
public class MEMUpdateBirthDateControl extends TControl{
	@Override
	public void onInit() {
		super.onInit();
		TParm parm=new TParm();
		Object obj=this.getParameter();
		if(obj instanceof TParm)
			parm=(TParm) obj;
		String mrNo=parm.getValue("MR_NO");
		TParm result=getPatInfo(mrNo);
		this.setValue("GESTATIONAL_WEEKS", result.getValue("GESTATIONAL_WEEKS",0));
		this.setValue("NEW_BODY_WEIGHT", result.getValue("NEW_BODY_WEIGHT",0));
		this.setValue("NEW_BODY_HEIGHT", result.getValue("NEW_BODY_HEIGHT",0));
		if(result.getValue("BIRTH_DATE",0).equals("")||result.getValue("BIRTH_DATE",0).toString().length()<0){
			this.setValue("BIRTH_DATE",SystemTool.getInstance().getDate());
		}else{
			this.setValue("BIRTH_DATE",
					result.getValue("BIRTH_DATE",0).toString().substring(0, 19).replaceAll("-", "/"));
		}
	}
	/**
	 * 获取病患信息
	 * @param mrNo
	 * @return
	 */
	public TParm getPatInfo(String mrNo){
		String sql="SELECT BIRTH_DATE,GESTATIONAL_WEEKS,NEW_BODY_WEIGHT,NEW_BODY_HEIGHT FROM SYS_PATINFO WHERE MR_NO='"+mrNo+"'";
		TParm result=new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}
	/**
	 * 传回
	 * @throws ParseException 
	 */
	public void onReturn() throws ParseException{
			TParm tagParm=this.getParmForTag("GESTATIONAL_WEEKS;NEW_BODY_WEIGHT;NEW_BODY_HEIGHT");
			TParm parm=new TParm();
			//获取当前时间转换为date类型  ============================================ wuxy 2016/11/10 start 对新生儿出生日期按钮，里边的日期进行修改
			Timestamp today = new Timestamp(System.currentTimeMillis());
			String strToday = today.toString().substring(0,19).replace("-", "/");
			Date date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(strToday);
			System.out.println(strToday+"---"+date);
			//获取修改的日期转换为date类型
			String birth = this.getValueString("BIRTH_DATE").substring(0,19).replace("-", "/");
			Date birthDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(birth);
			System.out.println(birth+"---"+birthDate);
			//比较
			int i = birthDate.compareTo(date);
		if(i>0){
			this.messageBox("出生日期不能大于当前时间");
			// ======================================================wuxy 2016/11/10 end
		}else{
			parm.setData("BIRTH_DATE",TypeTool.getTimestamp(this.getValue("BIRTH_DATE")));
			parm.setData("GESTATIONAL_WEEKS",tagParm.getValue("GESTATIONAL_WEEKS"));
			parm.setData("NEW_BODY_WEIGHT",tagParm.getValue("NEW_BODY_WEIGHT"));
			parm.setData("NEW_BODY_HEIGHT",tagParm.getValue("NEW_BODY_HEIGHT"));
			parm.setCount(parm.getCount("BIRTH_DATE"));
			this.setReturnValue(parm);
			this.closeWindow();
		}
	}
}
