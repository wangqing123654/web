package com.javahis.ui.main;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;

import jdo.med.MedNodifyTool;
import jdo.sys.Operator;
import jdo.sys.SYSRolePopedomTool;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTreeNode;
import com.dongyang.ui.event.TTreeEvent;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TComponent;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TMovePane;
import java.util.Date;
import java.util.Random;
import com.dongyang.util.StringTool;
import com.javahis.system.root.RootClientListener;
import com.javahis.ui.sys.LEDMEDImage;
import jdo.sys.SYSLoginStructureTool;
import com.dongyang.ui.TFrame;
import com.dongyang.ui.TToolBar;
import java.awt.Component;
import java.awt.Frame;
import java.awt.Point;
import com.dongyang.ui.TToolButton;
import com.dongyang.util.MemoryMonitor;
import com.dongyang.jdo.TJDODBTool;

/**
 *
 * <p>Title: 主窗口控制类</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author lzk 2008.10.8
 * @version 1.0
 */
public class SystemMainControl extends TControl{
    public static String TREE = "SYSTEM_TREE";
    public static String TAB = "SYSTEM_TAB";
    private TTreeNode treeRoot;
    private String defaultWindowID;
    private TTreeNode defaultTreeNode;
    private String msgId="PUB0113";//初始化打开公布栏的程序ID
    private String msgPath="%ROOT%\\config\\sys\\SYSPublishBoard.x";//初始化打开公布栏的程序地址
    /**
     * 当前的编辑标签
     */
    private String currEditTag;
    /**
     * 书钉
     */
    private boolean isSD;
    Thread threadTime;
	private LEDMEDImage ledMedImage; //监听   chenxi  
	private TParm  ledParm; //发送过来的消息    chenxi
    /**
     * 初始化方法
     */
    public void onInit()
    {
        super.onInit();
        initDefaultOpenWindowID();
        onInitTree();
        //单击选中树项目
        addEventListener(TREE + "->" + TTreeEvent.DOUBLE_CLICKED,"onTreeDoubleClicked");
        setValue("L_USERID",Operator.getID());
        setValue("L_USERNAME",Operator.getName());
        startTime();
        openMsgBoard();
        openMsgVersion();
        //初始化在线通讯设备
        RootClientListener.getInstance().setSysFrame(getComponent());
        RootClientListener.getInstance().init();
        //屏幕居中
        callFunction("UI|showMaxWindow");
        openLEDImageUI() ;  //chenxi  监听发送来的消息
        initSystemVersion();
    }
    
  
    	
    
    /**
     * 初始化系统版本号
     */
    public void initSystemVersion(){
    	TConfig config = TConfig.getConfig("WEB-INF/config/system/TConfig.x");
		String id = config.getString("SystemVersion");
		String zhTitle="BlueCore系统-"+id;
		String englishTitle="Blue Core System-"+id;
	    callFunction("UI|setZhTitle", zhTitle);
	    callFunction("UI|setEnTitle", englishTitle);
    }
    /**
     * 初始化默认打开程序ID
     */
    public void initDefaultOpenWindowID()
    {
        TParm p = new TParm(TJDODBTool.getInstance().select("SELECT PUB_FUNCTION FROM SYS_OPERATOR WHERE USER_ID='" + Operator.getID() + "'"));
        if(p.getCount() > 0)
            defaultWindowID = p.getValue("PUB_FUNCTION",0);
    }
    /**
     * 初始化时显示信息公布栏
     */
    public void openMsgBoard(){
        callFunction("UI|" + TAB + "|openPanel", msgId, msgPath, "", new TParm());
        if(defaultTreeNode != null)
            onTreeDoubleClicked(defaultTreeNode);
    }
    
    /**
     * 初始化显示版本说明
     */
    public void openMsgVersion() {
    	String versionId = "AUT07";
    	String versionPath = "%ROOT%\\config\\sys\\SYSVersion.x";
    	 callFunction("UI|" + TAB + "|openPanel", versionId, versionPath, "", new TParm());
         if(defaultTreeNode != null)
             onTreeDoubleClicked(defaultTreeNode);
    }
    
    
    
    
    /**
     * 启动时间
     */
    public void startTime()
    {
        if(threadTime != null)
            return;
        threadTime = new Thread(){
            public void run()
            {
                while(threadTime != null)
                {
                    try{
                        threadTime.sleep(100);
                    }catch(Exception e)
                    {
                    }
                    setTime();
                }
            }
        };
        threadTime.start();
    }
    /**
     * 停止时间
     */
    public void stopTime()
    {
        threadTime = null;
    }
    /**
     * 设置时间
     */
    public void setTime()
    {
        setValue("L_TIME",StringTool.getString(new Date(),"HH:mm:ss"));
    }
    /**
     * 初始化树
     */
    public void onInitTree()
    {
        treeRoot = (TTreeNode)callMessage("UI|" + TREE + "|getRoot");
        if(treeRoot == null)
            return;
        treeRoot.setText("BlueCore");
        treeRoot.setGroup("ROOT");
        treeRoot.setID("SYS_SUBSYSTEM");
        treeRoot.setType("PATH");
        treeRoot.setValue("ROOT:SYS_SUBSYSTEM");
        treeRoot.removeAllChildren();
        //TParm result = SYSRolePopedomTool.getInstance().getStructureTreeForRole(Operator.getRole());
        SYSLoginAuthTool tool = new SYSLoginAuthTool();
        TParm result = tool.getStructureTreeForRoleAndUserAuth(Operator.getID(),Operator.getRole());
        if(result.getErrCode() < 0)
            return;
        downloadRootTree(treeRoot,result);
        callMessage("UI|" + TREE + "|update");
    }
    /**
     * 下载树数据
     * @param parentNode TTreeNode
     * @param parm TParm
     */
    public void downloadRootTree(TTreeNode parentNode,TParm parm)
    {
        if(parentNode == null)
            return;
        int count = parm.getCount();
        for(int i = 0;i < count;i++)
        {
            String group = parm.getValue("GROUP_ID",i);
            String id = parm.getValue("ID",i);
            String name = parm.getValue("NAME",i);
            String enName = parm.getValue("ENNAME",i);
            String type = parm.getValue("TYPE",i);
            String data = parm.getValue("DATA",i);
            String value = parm.getValue("PARENT_ID",i);
            String state = parm.getValue("STATE",i);
            TParm child = parm.getParm("CHILD",i);
            TTreeNode node = new TTreeNode(name,type);
            node.setGroup(group);
            node.setID(id);
            node.setEnText(enName);
            node.setValue(group + ":" + id);
            node.setData(data);
            node.setValue(value);
            node.setState(state);
            parentNode.add(node);
            if(defaultWindowID != null && defaultWindowID.length() > 0 && id.equals(defaultWindowID))
            {
                defaultTreeNode = node;
            }
            if(!"PRG".equalsIgnoreCase(type))
                downloadRootTree(node,child);
        }
    }
    /**
     * 树双击事件打开窗口
     * @param parm Object
     */
    public void onTreeDoubleClicked(Object parm)
    {
        TTreeNode node = (TTreeNode)parm;
        if(node == null)
            return;
        if(!"PRG".equals(node.getType()))
            return;
        String id = node.getID();
        String data = (String)node.getData();
        String value = node.getValue();
        if(data == null || data.length() == 0)
            return;
        String state = node.getState();
        if(state == null)
            state = "";
        TParm result = SYSRolePopedomTool.getInstance().getStructureTreeForRole(id,Operator.getRole());
        if("WINDOW".equalsIgnoreCase(value))
        {
            openWindow(data, state);
            return;
        }
        callFunction("UI|" + TAB + "|openPanel", id, data, state, result);
        if(!isSD)
        {

            TMovePane mp = (TMovePane)callFunction("UI|SYSTEM_MP|getThis");
            mp.onDoubleClicked(true);
        }
    }
    public void onSystemMenuToolButton()
    {
        TMovePane mp = (TMovePane)callFunction("UI|SYSTEM_MP|getThis");
        mp.onDoubleClicked();
    }
    /**
     * 设置当前的编辑标签
     * @param currEditTag String
     */
    public void setCurrEditTag(String currEditTag)
    {
        this.currEditTag = currEditTag;
    }
    /**
     * 退出
     * @return Object
     */
    public Object onExit()
    {
        TTabbedPane tabbed = (TTabbedPane)callFunction("UI|" + TAB + "|getThis");
        while(tabbed.getTabCount() > 0)
        {
            TComponent component = tabbed.getSelectedTComponent();
            if(component == null)
            {
                tabbed.removeTabAt(tabbed.getSelectedIndex());
                continue;
            }
            //处理关闭验证
            TControl control = (TControl)component.callFunction("getControl");
            if(control != null && !control.onClosing())
                return "OK";
            //关闭动作
            tabbed.closePanel(component.getTag());
        }
        stopTime();
        RootClientListener.getInstance().close();
        return null;
    }
    /**
     * 设置状态
     * @param s String
     */
    public void setSysStatus(String s)
    {
        setValue("L_STATUS", s);
    }
    /**
     * 关闭
     * @return Object
     */
    public Object onClose()
    {
        TTabbedPane tabbed = (TTabbedPane)callFunction("UI|" + TAB + "|getThis");
        TComponent component = tabbed.getSelectedTComponent();
        if(component == null)
            return null;
        if(msgId.equals(component.getTag()))
        {
            if(tabbed.getTabCount() == 1)
                return null;
            return "OK";
        }
        //处理关闭验证
        TControl control = (TControl)component.callFunction("getControl");
        if(control != null && !control.onClosing())
            return "OK";
        //关闭动作
        tabbed.closePanel(component.getTag());
        //释放内存
        Runtime.getRuntime().gc();
        //如果关闭最后一个打开导航目录
        if(tabbed.getTabCount() == 1)
        {
            TMovePane mp = (TMovePane) callFunction("UI|SYSTEM_MP|getThis");
            mp.onDoubleClicked(false);
        }
        return "OK";
    }
    /**
     * 得到当前的编辑标签
     * @return String
     */
    public String getCurrEditTag()
    {
        return currEditTag;
    }
    /**
     * 书钉图标
     */
    public void onSystemTitlePic()
    {
        TLabel label = (TLabel)callFunction("UI|SYSTEM_TITLE_PIC|getThis");
        if("t1.gif".equals(label.getPictureName()))
        {
            label.setPictureName("t2.gif");
            isSD = true;
        }
        else
        {
            label.setPictureName("t1.gif");
            isSD = false;
        }
    }
    /**
     * 在线通讯
     */
    public void onRootClick()
    {
    	
        RootClientListener.getInstance().onClickedIcon();
    }
    /**
     * 更换ToolBar
     */
    public void onMainChildToolBarAction()
    {
        TFrame frame = (TFrame)getComponent();
        TToolBar toolbar = frame.getToolBar();
        Component component = toolbar.getComponentAtIndex(toolbar.getComponentCount() - 1);
        if(component instanceof TToolButton && "systemMenuToolButton".equals(((TToolButton)component).getTag()))
            return;
        TToolButton button = new TToolButton();
        button.setTag("systemMenuToolButton");
        button.setText("");
        button.setToolTipText("显示隐藏系统菜单");
        button.setMnemonic('X');
        button.setActionMessage("onSystemMenuToolButton");
        button.setPictureName("mro.gif");
        button.setToRight(true);
        toolbar.add(button);
        button.setParentComponent(toolbar);
        button.onInit();
    }
    /**
     * 内存调试
     */
    public void onMEMAction()
    {
        MemoryMonitor.run();
    }
    public void onModifyPassword()
    {
        this.openDialog("%ROOT%\\config\\sys\\SYSUpdatePassword.x");

    }
    
    
    /**
     * 双击图标，显示检验、检查数据 
     * chenx  20140324
     */
    public void onImageClick(){
//    	this.messageBox("你好") ;
//    	Component com = (Component) getComponent("L_IMAGE");	
//    	TLabel  label = (TLabel)com ;
//    	label.setIconName("%ROOT%\\image\\ImageIcon\\move.gif") ;
    		ledMedImage.onStopImage() ;
    		this.openDialog("%ROOT%\\config\\med\\MEDNodify.x",ledParm);
    }
    
    /**
	 * 监听接受消息
	 */
	public void openLEDImageUI() {
		Component com = (Component) getComponent("L_IMAGE");	
     if(onCheckData()){
    	 ledParm.addListener("onSelStation", this, "onSelStationListenerLed");
    		while ((com != null) && (!(com instanceof TLabel)))
    			com = com.getParent();
    		this.ledMedImage = new LEDMEDImage((TLabel) com, this, ledParm);    
     }
		     
	}
	/**
	 * 校验数据
	 * @return
	 */
	public boolean  onCheckData(){
		TParm parm = new TParm() ;
		parm.setData("IP", Operator.getIP()) ;
	  TParm result = MedNodifyTool.getInstance().query(parm) ;
	  boolean flg = false ;
	  if(result.getCount()>0){
		  ledParm = new TParm() ;
		  flg = true ;
		  ledParm.setData("IP", result.getValue("OPT_TERM", 0)) ; 
		  ledParm.setData("ADM_TYPE", result.getValue("ADM_TYPE", 0));
		  ledParm.setData("PASSWORD", result.getValue("PASSWORD", 0)) ;
		  ledParm.setData("ID", result.getValue("SKT_USER", 0)) ; 
		  ledParm.setData("STATION_CODE", result.getValue("STATION_CODE", 0)) ; 
		  if(result.getValue("SKT_TYPE", 0).equals("1")){
			  ledParm.setData("TYPE", "1") ;			  
		  }
		  else {
			  ledParm.setData("TYPE", "0") ;			
		  }
	  }
		return flg ;
	}
		 
}
