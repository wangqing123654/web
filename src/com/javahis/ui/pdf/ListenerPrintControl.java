package com.javahis.ui.pdf;

import com.dongyang.control.TControl;
import com.dongyang.config.TRegistry;
import jdo.pdf.PDFT;
import com.dongyang.data.TParm;
import java.io.File;
import com.dongyang.ui.TTable;
import com.dongyang.manager.TIOM_FileServer;
import jdo.pdf.PdfTool;
import java.util.Map;
import java.util.HashMap;
import com.dongyang.util.FileTool;
import com.dongyang.jdo.TJDODBTool;

/**
 *
 * <p>Title: ��ӡɨ�贰�ڿ�����</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author lzk 2010.12.10
 * @version 1.0
 */
public class ListenerPrintControl extends TControl implements Runnable{
    String fileServerRoot;
    Thread thread = new Thread();
    /**
     * ��ʼ��
     */
    public void onInit()
    {
        String path = TRegistry.get("HKEY_CURRENT_USER\\Software\\JavaHis\\PDF\\LocalPath");
        if(path != null)
            setText("tTextField_1",path);
        path = TRegistry.get("HKEY_CURRENT_USER\\Software\\JavaHis\\PDF\\PrintPath");
        if(path != null)
            setText("tTextField_0",path);
        fileServerRoot = PdfTool.getInstance().getRoot();
    }
    /**
     * �洢Ŀ¼
     */
    public void savePath()
    {
        TRegistry.set("HKEY_CURRENT_USER\\Software\\JavaHis\\PDF\\LocalPath",getText("tTextField_1"));
        TRegistry.set("HKEY_CURRENT_USER\\Software\\JavaHis\\PDF\\PrintPath",getText("tTextField_0"));
    }
    /**
     * ɨ��
     */
    public void onL()
    {
        String localPath = getText("tTextField_0");
        if(localPath.length() == 0)
        {
            messageBox("�������ӡ���Ŀ¼!");
            return;
        }
        File f = new File(localPath);
        if(!f.exists())
        {
            messageBox("�������ӡ���Ŀ¼������!");
            return;
        }
        savePath();
        TParm parm = PDFT.listener(localPath);
        TTable table = (TTable)getComponent("tTable_0");
        int count = parm.getCount("STATE");
        Map map = new HashMap();
        for(int i = 0;i < count;i++)
        {
            String mrno = parm.getValue("MR_NO",i);
            String caseno = (String)map.get(mrno);
            if(mrno == null)
            {
                parm.addData("CASE_NO","");
                continue;
            }
            if(caseno == null)
            {
                caseno = getCaseNo(mrno);
                map.put(mrno,caseno);
            }
            parm.addData("CASE_NO",caseno);
        }
        table.setParmValue(parm);
    }
    /**
     * �õ�����Ŀ¼
     * @param mrno String
     * @return String
     */
    public String getCaseNo(String mrno)
    {
        TParm parm = new TParm(TJDODBTool.getInstance().select("SELECT max(CASE_NO)AS CASE_NO FROM MRO_MRV WHERE MR_NO='" + mrno + "'"));
        System.out.println("case_no:"+parm);
        return parm.getValue("CASE_NO",0);
    }
    /**
     * �ϴ�
     */
    public void onUpdate()
    {
        TTable table = (TTable)getComponent("tTable_0");
        TParm p = table.getParmValue();
        for(int i = 0;i < table.getRowCount();i++)
            onU(p,i);
    }
    public void onU(TParm parm,int row)
    {
        String fileName = parm.getValue("FILE_NAME",row);
        String mrno = parm.getValue("MR_NO",row);
        String type = parm.getValue("TYPE",row);
        String date = parm.getValue("DATE",row);
        String caseno = parm.getValue("CASE_NO",row);
        String localPath = getText("tTextField_0");
        if(mrno == null || mrno.length() == 0)
            return;
        if(type == null || type.length() == 0)
            return;
        if(date == null || date.length() == 0)
            return;
        if(caseno == null || caseno.length() == 0)
            return;
        String f = mrno + "_" + type + "_" + date + ".pdf";

        String path = fileServerRoot + "\\����ԭʼ��\\" + mrno.substring(0,6) + "\\" + mrno + "\\" + caseno + "\\";
        try{
            byte data[] = FileTool.getByte(localPath + "\\" + fileName);
            if(TIOM_FileServer.writeFile(TIOM_FileServer.getSocket(),path + "\\" + f,data))
            {
                File f1 = new File(localPath + "\\" + fileName);
                f1.delete();
            }
        }catch(Exception e)
        {
        }
    }
    public void onS()
    {
        String s = getText("tButton_5");
        if(s.equals("��ʼ"))
        {
            thread = new Thread(this);
            thread.start();
            setText("tButton_5", "ֹͣ");
        }
        else
        {
            setText("tButton_5", "��ʼ");
            thread = null;
        }
    }
    public void run()
    {
        while(thread != null)
        {
            try{
                thread.sleep(200);
            }catch(Exception e)
            {

            }
            onL();
            onUpdate();
        }
    }
}
