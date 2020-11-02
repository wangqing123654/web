package jdo.sys.ws;

import javax.jws.WebService;



/**
 * <p>Title:统一编码统用接口</p>
 *
 * <p>Description: 统一编码统用接口</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: BlueCore</p>
 *
 * @author li.xiang 2013-04-29
 * @version 4.0
 */
@WebService
public interface IDictionaryService {
	
    /**
     * 得到版本
     * @return String
     */
    public String getVersion();
    
    /**
     * 得到科室信息
     * @param code String
     * @param password String
     * @return String[]
     **/
    public String[] getDeptInf(String code,String password);

    /** * 得到病区
     * @param code String
     * @param password String
     * @return String[]
     */
    public String[] getStation(String code,String password);
    
    
    /**
     * 同步角色
     * @param code String
     * @param password String
     * @return String[]
     */
    public String[] getReulInf(String code,String password);
    
    /**
     * 得到科室信息
     * @param code String
     * @param password String
     * @return String[]
     */
    public String[] getDeptInfCode(String code,String password,String deptcode);
    
    /**
     * 用户信息
     * @param code String
     * @param password String
     * @return String[]
     */
    public String[] getOperatorInf(String code,String password);
    
    /**
     * LIS医嘱信息
     * @param code String
     * @param password String
     * @return String[]
     */
    public String[] getLisOrder(String code,String password);
    
    
    /**
     * 药品分类查询
     * @param code String
     * @param password String
     * @param name String
     * @return String[]
     */
    public String[] getPhaClassify(String code,String password,String name);
    
    /**
     * 药品查询
     * @param code String
     * @param password String
     * @param classify String
     * @return String[]
     */
    public String[] getPhaInf(String code,String password,String classify);
    
    /**
     * 拿到药品信息
     * @param code String
     * @param password String
     * @param orderCode String
     * @return String[]
     */
    public String[] getPhaOrder(String code,String password,String ordercode);
    
    /**
     * 住院包药机得到HIS系统药品字典(同步表)
     * @param code String
     * @param password String
     * @return String[]
     */
    public String[] getODIPhaOrderInfo(String code,String password);
    
    /**
     * 住院包药机得到HIS系统药品字典单医嘱查询
     * @param code String
     * @param password String
     * @return String[]
     */
    public String[] getODIPhaOrderInfoItem(String code,String password,String ordercode);
    
    /**
     * 药品料位
     * @param code String
     * @param password String
     * @return String[]
     */
    public String[] getIndMaterialloc(String code,String password);
    
    /**
     * 药品料位
     * @param code String
     * @param password String
     * @return String[]
     */
    public String[] getIndMateriallocOrder(String code,String password,String orgcode,String ordercode);
    
    /**
     * 拿到输液系统医嘱信息
     * @param code String
     * @param password String
     * @param ordercode String
     * @return String
     */
    public String[] getPhaEsyOrder(String code,String password,String ordercode);
    
    /**
     * 性别字典同步
     * @param code String
     * @param password String
     * @return String[]
     */
    public String[] getSexInf(String code,String password);
    
    
    /**
     * 血型
     * @param code String
     * @param password String
     * @return String[]
     *//*
    public String[] getBloodType(String code,String password);
    /**
     * 身份（费用类别）
     * @param code String
     * @param password String
     * @return String[]
     */
    public String[] getCtzInf(String code,String password);
    
    /**
     * 得到科室信息
     * @param code String
     * @param password String
     * @return String[]
     */
    public String[] getDeptInfSY(String code,String password);
    
    /**
     * 得到频次
     * @param code String
     * @param password String
     * @return String[]
     */
    public String[] getPhaFreqInfSY(String code, String password);
    
    
    /**
     * 得到用药方式
     * @param code String
     * @param password String
     * @return String[]
     */
    public String[] getRouteInf(String code, String password);
    
    /**
     * 同步班表
     * @param code String
     * @param password String
     * @param admtype String
     * @param startdate String
     * @param enddate String
     * @return String[]
     */
    public String[] getRegWorkList(String code,String password,String admtype,String startdate,String enddate);
    
    /**
     * 拿到药品信息(拼音码)
     * @param code String
     * @param password String
     * @param py1 String
     * @param startrow int
     * @param endrow int
     * @return String[]
     */
    public String[] getPhaOrderPY1(String code,String password,String py1,int startrow,int endrow);    
    
    /**
     * 得到频次
     * @param code String
     * @param password String
     * @return String[]
     */
    public String[] getPhaFreqInf(String code, String password);
    
    /**
     * 诊断信息
     * @param code String
     * @param password String
     * @param classify String
     * @param type String
     * @return String[]
     */
    public String[] getDiagnosisInf(String code,String password,String classify,String type);
    
    /**
     * 手术字典
     * @param code String
     * @param password String
     * @return String[]
     */
    public String[] getSysOperationICD(String code,String password);
    
    /**
     * 注册信息
     * @param code String 编号
     * @param chnDesc String 中文名称
     * @param engDesc String 英文名称
     * @param contactsName String 联系人
     * @param tel String 电话
     * @param email String mail
     * @param password String 密码
     * @return String Success
     */
    public String regist(String code,String chnDesc,String engDesc,String contactsName,String tel,String email,String password);
    
    /**
     * 读取注册状态
     * @param code String
     * @return String
     * No Find 没有找到
     * NEW 新增,待确认
     * ENABLED 有效的
     * DISABLED 无效的
     */
    public String getRegistStatus(String code);
    
    /**
     * 修改密码
     * @param code String
     * @param oldPassword String
     * @param newPassword String
     * @return String
     */
    public String modifyPassword(String code,String oldPassword,String newPassword);
    
    /**
     * 得到共享字典信息
     * @return String[]
     */
    public String[] getShareTable();
    
    /**
     * 注册表信息
     * @param code String 厂商编号
     * @param password String 密码
     * @param tableName String 表名
     * @param action String 操作
     * @return String
     */
    public String registTable(String code,String password,String tableName,String action);
    
    /**
     * 得到表注册信息
     * @param code String
     * @param password String
     * @param tableName String
     * @return String[]
     */
    public String[] getRegistTableInf(String code,String password,String tableName);
    
    /**
     * 得到有变化的表
     * @param code String
     * @param password String
     * @param status String
     * @return String[]
     */
    public String[] getModifyTable(String code,String password,String status);
    
    
    /**
     * 得到变化信息
     * @param code String
     * @param password String
     * @param status String
     * @param tableName String
     * @return String[]
     */
    public String[] getModifyInf(String code,String password,String status,String tableName);
    
    
    /**
     * 取走信息
     * @param code String
     * @param password String
     * @param tableName String
     * @param index String
     * @return String
     */
    public String fetchInf(String code,String password,String tableName,String index);
    
    /**
     * 确认
     * @param code String
     * @param password String
     * @param tableName String
     * @param index String
     * @return String
     */
    public String confirmedInf(String code,String password,String tableName,String index);
    
    
    /**
     * 删除同步信息
     * @param code String
     * @param password String
     * @param tableName String
     * @param index String
     * @return String
     */
    public String deleteInf(String code,String password,String tableName,String index);
    
    /**
     * 用户信息
     * @param code String
     * @param password String
     * @return String[]
     */
    public String[] getOperatorInfSY(String code,String password);
    
    /**
     * 皮试回写门急医嘱备注
     * @param code String
     * @param password String
     * @param caseNo String
     * @param rxNo String
     * @param seqNo String
     * @return String[]
     */
    public String[] readOpdOrderPS(String code,String password,String caseno,String rxno,String seqno,String value);
    
    /**
     * 得到门急住别
     * @param code String
     * @param password String
     * @return String[]
     */
    public String[] getAdmType(String code,String password);
    
    /**
     * 得到医师排班
     * @param code String
     * @param password String
     * @param admDate String 日期 20100921
     * @return String[]
     * 时段,门急住别,科室,诊室,医师
     */
    public String[] getRegSchDay(String code,String password,String admDate);
    
    /**
     * 得到静点区
     * @param code String
     * @param password String
     * @return String
     */
    public String[] getRegionJD(String code,String password);
    
    /**
     * 拿到门急医嘱是否退费
     * @param code String
     * @param password String
     * @param caseno String
     * @param rxno String
     * @param seqno String
     * @return String[]
     */
    public String[] getHisCancelOrder(String code,String password,String caseno,String rxno,String seqno);
    
    /**
     * 拿到包含病人信息门急医嘱信息
     * @param code String
     * @param password String
     * @param rxno String
     * @return String[]
     */
    public String[] getPatInfAndOrder(String code,String password,String rxno);
    
    /**
     * 拿到输液医嘱信息
     * @param code String
     * @param password String
     * @param rxno String
     * @return String[]
     */
    public String[] getOrderSY(String code,String password,String rxno);

    
    /**
     * 拿到字典信息
     * @param groupId String
     * @param id String
     * @return String
     */
    public String getDictionary(String groupId,String id);
    
    /**
     * 保存病患信息
     * @param code String
     * @param password String
     * @param name String 姓名
     * @param birthday String 出生日期
     * @param sex String 性别
     * @param SID String 身份证号
     * @param tel String 电话
     * @param address String 地址
     * @return String
     */
    public String savePat(String code,String password,String name,String birthday,String sex,String SID,String tel,String address);
    
    /**
     * 根据身份证号得到患者编号
     * @param code String
     * @param password String
     * @param SID String 身份证号
     * @param name String 姓名
     * @return String
     */
    public String[] getPatForSID(String code,String password,String SID,String name);
    
    
    /**
     * 预约挂号
     * @param code String
     * @param password String
     * @param mrNo String 病案号
     * @param date String 挂号日期
     * @param sessionCode String 时段
     * @param admType String 门急别
     * @param deptCode String 科室
     * @param clinicRoomNo String 诊间
     * @param drCode String 医生编码
     * @param regionCode String 区域
     * @param ctz1Code String 病患身份
     * @param serviceLevel String 服务等级
     * @return String
     */
    public String regAppt(String code,String password,String mrNo,String date,String sessionCode,
                          String admType,String deptCode,String clinicRoomNo,String drCode,String regionCode,
                          String ctz1Code,String serviceLevel);
    
    /**
     * 预约退号
     * @param code String
     * @param password String
     * @param caseNo String
     * @return String
     */
    public String regUnAppt(String code,String password,String caseNo);
    
    
    /**
     * 取得病患当前所有预约未报道信息
     * @param code String
     * @param password String
     * @param mrNo String
     * @return String[]
     */
    public String[] getPatAppRegInfo(String code,String password,String mrNo);
    
    /**
     * 取得时段信息
     * @param code String
     * @param password String
     * @param admType String
     * @return String[]
     */
    public String[] getSessionInf(String code,String password,String admType);
    
    /**
     * 取得诊间信息
     * @param code String
     * @param password String
     * @return String[]
     */
    public String[] getClinicRoom(String code,String password);
    
    /**
     * 取得诊区信息
     * @param code String
     * @param password String
     * @return String[]
     */
    public String[] getClinicArea(String code,String password);
    
    /**
     * 取得科室分类信息
     * @param code String
     * @param password String
     * @return String[]
     */
    public String[] getDeptClassRule(String code,String password);
    
    /**
     * 获取sys_fee数据（物联网）
     * @param code
     * @param password
     * @return
     */
    public String[] getSysFee(String code, String password);
    
    /**
     * 获取sys_fee_history数据（物联网）
     * @param code
     * @param password
     * @return
     */
    public String[] getSysFeeHistory(String code, String password);
    
    /**
     * 获取公用字典数据
     * @param code
     * @param password
     * @param tableName
     * @return
     */
    public String[] getData(String code, String password, String tableName);
    
    
    /**
     * 通过病案号获取病患信息（物联网）
     * @param mr_no
     * @return
     */
    public String[] getPatByMrNo(String mr_no);
    
  
    /**
     * 获得班表信息
     */
	public String getSchDay(String beginDate,String endDate,String deptCode,String drCode);
	
	
	/**
	 * 获得修改后的班表信息 
	 */
	public String getUpdatedScyDay(String beginDate,String endDate);
	
	/**
	 * 获得VIP班表信息
	 *
	 */
	public String getVipSchDay(String beginDate, String endDate);
	
	/**
	 * 更新班表中CRM同步标记
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	public boolean updateSchdayCrmSyncFlg(String beginDate, String endDate);
	
	/**
	 * 取得号别费用信息
	 * @return
	 */
	public String[] getClinictypeFee(String code, String password);
	
	/**
	 * 取得号别信息
	 * @return
	 */
	public String[] getClinictype(String code, String password);
	
	/**
	 * 取得挂号组别信息
	 * @return
	 */
	public String[] getQuegroup(String code, String password);
	
	/**
	 * 取得挂号方式信息
	 * @return
	 */
	public String[] getRegmethod(String code, String password);
	
	/**
     * 用户对应的科室信息
     * @param code String
     * @param password String
     * @return String[]
     */
    public String[] getOperatorDeptCode(String code,String password);
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}
