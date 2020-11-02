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
 * <p>Title: �����ڿ�����</p>
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
    private String msgId="PUB0113";//��ʼ���򿪹������ĳ���ID
    private String msgPath="%ROOT%\\config\\sys\\SYSPublishBoard.x";//��ʼ���򿪹������ĳ����ַ
    /**
     * ��ǰ�ı༭��ǩ
     */
    private String currEditTag;
    /**
     * �鶤
     */
    private boolean isSD;
    Thread threadTime;
	private LEDMEDImage ledMedImage; //����   chenxi  
	private TParm  ledParm; //���͹�������Ϣ    chenxi
    /**
     * ��ʼ������
     */
    public void onInit()
    {
        super.onInit();
        initDefaultOpenWindowID();
        onInitTree();
        //����ѡ������Ŀ
        addEventListener(TREE + "->" + TTreeEvent.DOUBLE_CLICKED,"onTreeDoubleClicked");
        setValue("L_USERID",Operator.getID());
        setValue("L_USERNAME",Operator.getName());
        startTime();
        openMsgBoard();
        openMsgVersion();
        //��ʼ������ͨѶ�豸
        RootClientListener.getInstance().setSysFrame(getComponent());
        RootClientListener.getInstance().init();
        //��Ļ����
        callFunction("UI|showMaxWindow");
        openLEDImageUI() ;  //chenxi  ��������������Ϣ
        initSystemVersion();
    }
    
  
    	
    
    /**
     * ��ʼ��ϵͳ�汾��
     */
    public void initSystemVersion(){
    	TConfig config = TConfig.getConfig("WEB-INF/config/system/TConfig.x");
		String id = config.getString("SystemVersion");
		String zhTitle="BlueCoreϵͳ-"+id;
		String englishTitle="Blue Core System-"+id;
	    callFunction("UI|setZhTitle", zhTitle);
	    callFunction("UI|setEnTitle", englishTitle);
    }
    /**
     * ��ʼ��Ĭ�ϴ򿪳���ID
     */
    public void initDefaultOpenWindowID()
    {
        TParm p = new TParm(TJDODBTool.getInstance().select("SELECT PUB_FUNCTION FROM SYS_OPERATOR WHERE USER_ID='" + Operator.getID() + "'"));
        if(p.getCount() > 0)
            defaultWindowID = p.getValue("PUB_FUNCTION",0);
    }
    /**
     * ��ʼ��ʱ��ʾ��Ϣ������
     */
    public void openMsgBoard(){
        callFunction("UI|" + TAB + "|openPanel", msgId, msgPath, "", new TParm());
        if(defaultTreeNode != null)
            onTreeDoubleClicked(defaultTreeNode);
    }
    
    /**
     * ��ʼ����ʾ�汾˵��
     */
    public void openMsgVersion() {
    	String versionId = "AUT07";
    	String versionPath = "%ROOT%\\config\\sys\\SYSVersion.x";
    	 callFunction("UI|" + TAB + "|openPanel", versionId, versionPath, "", new TParm());
         if(defaultTreeNode != null)
             onTreeDoubleClicked(defaultTreeNode);
    }
    
    
    
    
    /**
     * ����ʱ��
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
     * ֹͣʱ��
     */
    public void stopTime()
    {
        threadTime = null;
    }
    /**
     * ����ʱ��
     */
    public void setTime()
    {
        setValue("L_TIME",StringTool.getString(new Date(),"HH:mm:ss"));
    }
    /**
     * ��ʼ����
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
     * ����������
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
     * ��˫���¼��򿪴���
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
     * ���õ�ǰ�ı༭��ǩ
     * @param currEditTag String
     */
    public void setCurrEditTag(String currEditTag)
    {
        this.currEditTag = currEditTag;
    }
    /**
     * �˳�
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
            //����ر���֤
            TControl control = (TControl)component.callFunction("getControl");
            if(control != null && !control.onClosing())
                return "OK";
            //�رն���
            tabbed.closePanel(component.getTag());
        }
        stopTime();
        RootClientListener.getInstance().close();
        return null;
    }
    /**
     * ����״̬
     * @param s String
     */
    public void setSysStatus(String s)
    {
        setValue("L_STATUS", s);
    }
    /**
     * �ر�
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
        //����ر���֤
        TControl control = (TControl)component.callFunction("getControl");
        if(control != null && !control.onClosing())
            return "OK";
        //�رն���
        tabbed.closePanel(component.getTag());
        //�ͷ��ڴ�
        Runtime.getRuntime().gc();
        //����ر����һ���򿪵���Ŀ¼
        if(tabbed.getTabCount() == 1)
        {
            TMovePane mp = (TMovePane) callFunction("UI|SYSTEM_MP|getThis");
            mp.onDoubleClicked(false);
        }
        return "OK";
    }
    /**
     * �õ���ǰ�ı༭��ǩ
     * @return String
     */
    public String getCurrEditTag()
    {
        return currEditTag;
    }
    /**
     * �鶤ͼ��
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
     * ����ͨѶ
     */
    public void onRootClick()
    {
    	
        RootClientListener.getInstance().onClickedIcon();
    }
    /**
     * ����ToolBar
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
        button.setToolTipText("��ʾ����ϵͳ�˵�");
        button.setMnemonic('X');
        button.setActionMessage("onSystemMenuToolButton");
        button.setPictureName("mro.gif");
        button.setToRight(true);
        toolbar.add(button);
        button.setParentComponent(toolbar);
        button.onInit();
    }
    /**
     * �ڴ����
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
     * ˫��ͼ�꣬��ʾ���顢������� 
     * chenx  20140324
     */
    public void onImageClick(){
//    	this.messageBox("���") ;
//    	Component com = (Component) getComponent("L_IMAGE");	
//    	TLabel  label = (TLabel)com ;
//    	label.setIconName("%ROOT%\\image\\ImageIcon\\move.gif") ;
    		ledMedImage.onStopImage() ;
    		this.openDialog("%ROOT%\\config\\med\\MEDNodify.x",ledParm);
    }
    
    /**
	 * ����������Ϣ
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
	 * У������
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
