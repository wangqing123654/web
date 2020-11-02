package com.javahis.ui.database;

import java.awt.print.PrinterJob;

import javax.print.PrintService;

import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.tui.DText;
import com.dongyang.tui.text.EComponent;
import com.dongyang.tui.text.EFixed;
import com.dongyang.tui.text.MStyle;
import com.dongyang.ui.TDPanel;
import com.dongyang.ui.TDialog;
import com.dongyang.ui.TFrame;
import com.dongyang.ui.TWord;
import com.dongyang.util.TypeTool;
import com.dongyang.util.TSystem;
import com.dongyang.ui.TPrintListCombo;
import com.dongyang.config.TRegistry;
import com.javahis.util.StringUtil;

/**
 *
 * <p>Title: ��ӡ�������ڿ�����</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author lzk 2009.5.14
 * @version 1.0
 */
public class PreviewWordControl extends TControl{
    //��������
    private TDPanel panel;
    private DText text;
    private boolean isPrint;
    private TPrintListCombo printlist;
    private String defaultPrint;
    private String filename;
    private int fs = 1;
    private TParm data;
    /**
     * ��ʼ��
     */
    public void onInit()
    {
        panel = (TDPanel) getComponent("DPanel");
        printlist = (TPrintListCombo)getComponent("PRINT_LIST");
        initPanel();
        Object[] parm = (Object[])getParameter();
        if(parm == null)
            return;
        filename = TypeTool.getString(parm[0]);
        String s1 = TRegistry.get("HKEY_CURRENT_USER\\Software\\JavaHis\\print\\" + filename);
        //System.out.println("filename===="+filename);
        //$$===================add by lx 2012/02/05ͨ��IP������ID��ȡĬ�ϴ�ӡ��=======================$$//
	        String reportID=filename.substring(filename.lastIndexOf("\\")+1);
	        //System.out.println("===reportID==="+reportID);
	        //IP;
	        String ip=Operator.getIP();
	        //System.out.println("===ip==="+ip);
	        String sql="SELECT PRINTER_CHN_DESC FROM SYS_PRINTER_LIST";
	        	sql+=" WHERE PRINTER_IP='"+ip+"'";
	        	sql+=" AND REPORT_ID='"+reportID+"'";
	        //System.out.println("==sql=="+sql);
	        TParm printNameParm = new TParm(TJDODBTool.getInstance().select(sql));
	     //System.out.println("PRINTER_CHN_DESC==="+printNameParm.getValue("PRINTER_CHN_DESC",0));
        //$$===================add by lx 2012/02/05ͨ��IP������ID��ȡĬ�ϴ�ӡ��=======================$$//
        PrintService ps = PrinterJob.getPrinterJob().getPrintService();
        defaultPrint = ps.getName();
        if(s1 == null)
            s1 = defaultPrint;

        //�����ô�ӡ��ȡ  �����ô�ӡ��
        if(!printNameParm.getValue("PRINTER_CHN_DESC",0).equals("")){
        	s1=printNameParm.getValue("PRINTER_CHN_DESC",0);
        //����û�����ã���ȡ����Ĭ��ֵ
        }else{
        	s1=defaultPrint;
        }


        printlist.setSelectedID(s1);
        if(parm[1] != null && parm[1] instanceof TParm)
        {
            data = (TParm)parm[1];
            try{
                fs = data.getInt("Print.fs");
            }catch(Exception e)
            {
                fs = 1;
            }
            if(fs < 1)
                fs = 1;
        }

        //���ݲ���
        text.getPM().setStyleManager(new MStyle(true));
        //
        text.getFileManager().setParameter(data);
        text.getFileManager().onOpen(filename);
        //
        EFixed subject=(EFixed)text.getPM().getPageManager().findObject("OPDSubject", EComponent.FIXED_TYPE);
        EFixed opdPass=(EFixed)text.getPM().getPageManager().findObject("OPDPass", EComponent.FIXED_TYPE);
        EFixed opdPhys=(EFixed)text.getPM().getPageManager().findObject("OPDPhys", EComponent.FIXED_TYPE);
        EFixed opdResult=(EFixed)text.getPM().getPageManager().findObject("OPDResult", EComponent.FIXED_TYPE);
        EFixed opdProposal=(EFixed)text.getPM().getPageManager().findObject("OPDProposal", EComponent.FIXED_TYPE);
        // add by wangb 2017/10/13 ������ҩ���
        EFixed opdMedication=(EFixed)text.getPM().getPageManager().findObject("OPDMedication", EComponent.FIXED_TYPE);
       //add by huangtt 20171229 ��������
        EFixed opdAssessment=(EFixed)text.getPM().getPageManager().findObject("OPDAssessment", EComponent.FIXED_TYPE);
        //
        //
        //this.messageBox("subject="+subject);
        if(data.getData("CASE_NO")!=null){
        	text.getPM().setCaseNo((String)data.getData("CASE_NO"));
        }
        
        if(data.getData("MR_NO")!=null){
        	text.getPM().setMrNo((String)data.getData("MR_NO"));
        }
        //1
        if(subject!=null){
        	subject.tryReset();
        }
        //2
        if(opdPass!=null){
        	opdPass.tryReset();
        }
        //3
        if(opdPhys!=null){
        	opdPhys.tryReset();
        }
        //4
        if(opdResult!=null){
        	opdResult.tryReset();
        }
        //5
        if(opdProposal!=null){
        	opdProposal.tryReset();
        }
        if(opdMedication!=null){
        	opdMedication.tryReset();
        }
        if(opdAssessment!=null){
        	opdAssessment.tryReset();
        }
        //
        text.setPreview(true);
        isPrint = TypeTool.getBoolean(parm[2]);
        //��ʼ������
        initFrame();
        //��ʼ����Ϣ����
        initDialog();
        //���÷�������
        this.setReturnValue(text);
    }
    /**
     * ����
     */
    public void onSave()
    {
        if(data == null)
            data = new TParm();
        data.setData("FILE_NAME",filename);
        String s = "c:\\ParmData.dat";
        data.save(s);
        //System.out.println(filename + " " + data);
    }
    /**
     * ��ȡ
     */
    public void onLoad()
    {
        data = new TParm();
        String s = "c:\\ParmData.dat";
        data.read(s);
        filename = data.getValue("FILE_NAME");
        text.getFileManager().setParameter(data);
        text.getFileManager().onOpen(filename);
        text.setPreview(true);
        //System.out.println(filename + " " + data);
    }
    public void onPrintList()
    {
        String s1 = TRegistry.get("HKEY_CURRENT_USER\\Software\\JavaHis\\print\\" + filename);
        if(!printlist.getSelectedID().equals(s1))
        {
            TRegistry.set("HKEY_CURRENT_USER\\Software\\JavaHis\\print\\" +
                          filename, printlist.getSelectedID());
        }
    }
    /**
     * ��ʼ����Ϣ����
     */
    public void initDialog()
    {
        if(!(getComponent() instanceof TDialog))
            return;
        TDialog dialog = (TDialog)getComponent();
        if(text == null)
            return;
        String title = text.getFileManager().getEnTitle();
        String language = (String)TSystem.getObject("Language");
        if("en".equals(language) && title != null && title.length() > 0)
            dialog.setTitle(title);
        else
        {
            title = text.getFileManager().getTitle();
            if(title != null && title.length() > 0)
                dialog.setTitle(title);
        }
        dialog.setLocation(text.getFileManager().getPreviewWindowX(),text.getFileManager().getPreviewWindowY());
        dialog.setSize(text.getFileManager().getPreviewWindowWidth(),text.getFileManager().getPreviewWindowHeight());
        dialog.setCenterWindow(text.getFileManager().isPreviewWindowCenter());
        if(isPrint)
        {
            dialog.setOpenShow(false);
            onPrint();
            dialog.onClosed();
        }
    }
    /**
     * ��ʼ������
     */
    public void initFrame()
    {
        if(!(getComponent() instanceof TFrame))
            return;
        TFrame frame = (TFrame)getComponent();
        if(text == null)
            return;
        String language = (String)TSystem.getObject("Language");
        String title = text.getFileManager().getEnTitle();
        if("en".equals(language) && title != null && title.length() > 0)
            frame.setTitle(title);
        else
        {
            title = text.getFileManager().getTitle();
            if(title != null && title.length() > 0)
                frame.setTitle(title);
        }
        frame.setLocation(text.getFileManager().getPreviewWindowX(),text.getFileManager().getPreviewWindowY());
        frame.setSize(text.getFileManager().getPreviewWindowWidth(),text.getFileManager().getPreviewWindowHeight());
        frame.setCenterWindow(text.getFileManager().isPreviewWindowCenter());
        if(isPrint)
        {
            frame.setOpenShow(false);
            onPrint();
            frame.onClosed();
        }
    }
    /**
     * ��ʼ�����
     */
    public void initPanel()
    {
    	//System.out.println("========initPanel1111==========");
    	text=new DText();
        text.setTag("text");
        text.setBorder("��");
        text.setAutoBaseSize(true);
        panel.addDComponent(text);
    }
    /**
     * ��ӡ
     */
    public void onPrint()
    {
    	this.updateLastPrintUser();

        PrintService print = printlist.getSelectPrint();
        if(print == null)
        {
            messageBox_("��ѡ���ӡ��!");
            return;
        }
        for(int i = 0;i < fs;i++)
            text.getPM().getPageManager().print(print);
        //text.getPM().getPageManager().printDialog();
    }

    /**
     * �������һ�δ�ӡ��
     * (ֻ������ʱ��)
     */
    private void updateLastPrintUser(){

    	if( null!=data ){

        	String caseNO = data.getValue("CASE_NO");
        	String subClassCode = data.getValue("SUBCLASSCODE_ZHUSU");

        	if( !StringUtil.isNullString(caseNO) && !StringUtil.isNullString(subClassCode) ){

            	int fileSeq = this.getFileSEQ(caseNO,subClassCode);
            	fileSeq = fileSeq > 0 ? fileSeq-1: fileSeq;

            	TJDODBTool.getInstance().update(" update EMR_FILE_INDEX " +
            			"set EMR_PRINT_USER ='"+getOperatorName(Operator.getID())+"',"+
            			" EMR_PRINT_DATE = SYSDATE " +
            			"where CASE_NO ='"+caseNO+"' and FILE_SEQ ="+fileSeq+"");

            	//
            	Object wordObj = data.getData("WORD_ZHUSU");
            	if( null!=wordObj ){
            		( (TWord)wordObj ).getPM().getModifyNodeManager().setIndex(1);
            	}
        	}
    	}

    }

	/**
	 * �õ��û���
	 *
	 * @param userID
	 *            String
	 * @return String
	 */
    private String getOperatorName(String userID) {
		TParm parm = new TParm(TJDODBTool.getInstance().select(
				"SELECT USER_NAME FROM SYS_OPERATOR WHERE USER_ID='" + userID
						+ "'"));
		return parm.getValue("USER_NAME", 0);
	}

	/**
	 * ȡ����ļ���
	 *
	 * @param caseNo
	 * @param subClassCode
	 * @return
	 */
	private int getFileSEQ(String caseNo, String subClassCode) {
		String sql = "SELECT NVL(MAX(FILE_SEQ)+1,1) MAXFILENO  FROM EMR_FILE_INDEX";
		sql += " WHERE CASE_NO='" + caseNo + "' AND SUBCLASS_CODE='"+subClassCode+"'";
		// System.out.println("==sql=="+sql);
		TParm action = new TParm(TJDODBTool.getInstance().select(sql));
		int index = action.getInt("MAXFILENO", 0);
		return index;
	}

    public void onPrintSetup()
    {
        text.getPM().getPageManager().printDialog(fs);
    }
    public void onPSetup()
    {
        text.getPM().getPageManager().printSetup();
    }
    /**
     * ѡ���ӡ���Ĵ�ӡ
     * @param service PrintService
     */
    public void onPrint(PrintService service){
        for(int i = 0;i < fs;i++)
            text.getPM().getPageManager().print(service);
    }
    /**
     * ��ʾ����
     */
    public void onShowZoom()
    {
        String s = TypeTool.getString(getValue("ShowZoom"));
        if(s.endsWith("%"))
        {
            try{
                double d = Double.parseDouble(s.substring(0, s.length() - 1));
                text.getPM().getViewManager().setZoom(d);
                text.getPM().getFocusManager().update();
                text.getPM().getViewManager().resetSize();
            }catch(Exception e)
            {
            }
        }
    }
    /**
     * ��ӡ
     */
    public void onPrintXDDialog()
    {
        text.getPM().getPageManager().printXDDialog();
    }
    /**
     * ��ʾ�кſ���
     */
    public void onShowRowIDSwitch()
    {
        text.getPM().getViewManager().setShowRowID(!text.getPM().getViewManager().isShowRowID());
        text.getPM().getFocusManager().update();
    }
    public static void main(String args[])
    {
        TParm parm = new TParm();
        TParm data = new TParm();
        /*data.addData("A1","a1");
        data.addData("A2","a2");
        data.addData("A3","a3");
        data.addData("A4","a4");
        data.addData("A5","a5");
        data.setCount(1);
        data.addData("SYSTEM","COLUMNS","A1");
        data.addData("SYSTEM","COLUMNS","A2");
        data.addData("SYSTEM","COLUMNS","A3");
        data.addData("SYSTEM","COLUMNS","A4");
        data.addData("SYSTEM","COLUMNS","A5");
        parm.setData("aaa",data.getData());*/
        /*double[][] aaa = new double[][]{
            {20,20,20,20,30},
            {30,60,30,30,20},
            {40,40,40,40,60}};
        parm.setData("AAA","DATA",aaa);
        TFrame frame = com.javahis.util.JavaHisDebug.runFrame(
            "database\\PreviewWord.x",new Object[]{"%ROOT%\\config\\prt\\X1.jhw",parm,false});*/

        data.addData("PACK_CODE_SEQ", "123456789012");
        data.addData("PACK_DESC","123");
        data.addData("PACK_DEPT","ABC");
        data.addData("PACK_CODE_SEQ", "123456789012");
        data.addData("PACK_DESC","123");
        data.addData("PACK_DEPT","ABC");
        data.setCount(2);
        data.addData("SYSTEM", "COLUMNS", "PACK_CODE_SEQ");
        data.addData("SYSTEM", "COLUMNS", "PACK_DESC");
        data.addData("SYSTEM", "COLUMNS", "PACK_DEPT");

        parm=new TParm();
        parm.setData("T1",data.getData());

        TFrame frame = com.javahis.util.JavaHisDebug.runFrame(
            "database\\PreviewWord.x",new Object[]{"%ROOT%\\config\\prt\\ceshi.jhw",parm,false});

    }
}
