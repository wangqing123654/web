package com.javahis.ui.pdf;

import com.dongyang.control.TControl;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTreeNode;
import com.dongyang.util.FileTool;
import com.dongyang.config.TRegistry;
import jdo.pdf.PdfTool;
import java.util.ArrayList;
import java.io.File;
import java.util.List;
import jdo.pdf.jacobTool;

/**
 *
 * <p>Title: ��������</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class PDFLinkControl extends TControl{
    String fileServerRoot;
    /**
     * ��ʼ��
     */
    public void onInit()
    {
        String path = TRegistry.get("HKEY_CURRENT_USER\\Software\\JavaHis\\PDF\\LocalPath");
        String mrno = TRegistry.get("HKEY_CURRENT_USER\\Software\\JavaHis\\PDF\\mrno");
        String caseno = TRegistry.get("HKEY_CURRENT_USER\\Software\\JavaHis\\PDF\\caseno");

        if(path != null)
            setText("tTextField_1",path);
        if(mrno != null)
            setText("tTextField_3",mrno);
        if(caseno != null)
            setText("tTextField_4",caseno);

        fileServerRoot = PdfTool.getInstance().getRoot();
    }
    /**
     * ����ȫ������
     */
    public void onDownLoad()
    {
        String localPath = getText("tTextField_1");
        if(localPath == null || localPath.length() == 0)
        {
            messageBox("��������ʱĿ¼!");
            return;
        }
        String mrno = getText("tTextField_3");
        if(mrno == null || mrno.length() == 0)
        {
            messageBox("�����벡����!");
            return;
        }
        String caseno = getText("tTextField_4");
        if(caseno == null || caseno.length() == 0)
        {
            messageBox("�����������!");
            return;
        }
        TRegistry.set("HKEY_CURRENT_USER\\Software\\JavaHis\\PDF\\LocalPath",localPath);
        TRegistry.set("HKEY_CURRENT_USER\\Software\\JavaHis\\PDF\\mrno",mrno);
        TRegistry.set("HKEY_CURRENT_USER\\Software\\JavaHis\\PDF\\caseno",caseno);
        String dir = fileServerRoot + "\\����ԭʼ��\\" + mrno.substring(0,6) + "\\" + mrno + "\\" + caseno;
        String s[] = TIOM_FileServer.listFile(TIOM_FileServer.getSocket(),dir);
        if(s == null)
        {
            messageBox("û���ҵ��ļ�!");
            return;
        }
        TParm parm = new TParm();
        for(int i = 0;i < s.length;i++)
        {
            String path = dir + "\\" + s[i];
            parm.addData("FileName",s[i]);
            String d1[] = StringTool.parseLine(s[i],"_");
            if(d1 == null || d1.length < 2)
                parm.addData("Type","");
            else
                parm.addData("Type",d1[1]);
            byte data[] = TIOM_FileServer.readFile(TIOM_FileServer.getSocket(),path);
            try{
                FileTool.setByte(localPath + "\\" + s[i], data);
            }catch(Exception e)
            {
            }
        }
        TTable table = (TTable)getComponent("tTable_0");
        table.setParmValue(parm);
    }
    /**
     * ���ƶ�
     */
    public void onUp()
    {
        TTable table = (TTable)getComponent("tTable_0");
        int row = table.getSelectedRow();
        if(row < 1)
            return;
        TParm p = table.getParmValue();
        String t = p.getValue("FileName",row);
        p.setData("FileName",row,p.getValue("FileName",row - 1));
        p.setData("FileName",row - 1,t);
        t = p.getValue("Type",row);
        p.setData("Type",row,p.getValue("Type",row - 1));
        p.setData("Type",row - 1,t);
        table.setParmValue(p);
        table.setSelectedRow(row - 1);
    }
    /**
     * ���ƶ�
     */
    public void onDown()
    {
        TTable table = (TTable)getComponent("tTable_0");
        int row = table.getSelectedRow();
        if(row < 0 || row > table.getRowCount() - 2)
            return;
        TParm p = table.getParmValue();
        String t = p.getValue("FileName",row);
        p.setData("FileName",row,p.getValue("FileName",row + 1));
        p.setData("FileName",row + 1,t);
        t = p.getValue("Type",row);
        p.setData("Type",row,p.getValue("Type",row + 1));
        p.setData("Type",row + 1,t);
        table.setParmValue(p);
        table.setSelectedRow(row + 1);
    }
    /**
     * �ϲ�����
     */
    public void onAddPdf()
    {
        String localPath = getText("tTextField_1");
        if(localPath == null || localPath.length() == 0)
        {
            messageBox_("��ѡ����ʱĿ¼!");
            return;
        }
        String caseno = getText("tTextField_4");
        if(caseno == null || caseno.length() == 0)
        {
            messageBox_("�����������!");
            return;
        }
        String mrno = getText("tTextField_3");
        if(mrno == null || mrno.length() == 0)
        {
            messageBox_("�����벡����!");
            return;
        }
        File f = new File(localPath + "\\data");
        if(!f.exists())
            f.mkdirs();
        TTable table = (TTable)getComponent("tTable_0");
        TParm parm = table.getParmValue();
        int count = parm.getCount("FileName");
        List list = new ArrayList();
        int c = 0;
        for(int i = 0;i < count;i++)
        {
            String fileName = parm.getValue("FileName",i);
            String type = parm.getValue("Type",i);
            f = new File(localPath + "\\" + fileName);
            if(!f.exists())
                continue;
            try{
                byte[] data = FileTool.getByte(localPath + "\\" + fileName);
                FileTool.setByte(localPath + "\\data\\" + c + ".pdf", data);
                list.add(type);
                c++;
            }catch(Exception e)
            {
            }
        }

        //����ִ���ļ�
        byte data[] = TIOM_FileServer.readFile(TIOM_FileServer.getSocket(),PdfTool.getInstance().getRoot() + "\\pdftk");
        if(data == null)
        {
            messageBox_("��������û���ҵ��ļ� " + PdfTool.getInstance().getRoot() + "\\pdftk");
            return;
        }
        try{
            FileTool.setByte(localPath + "\\data\\pdftk.exe", data);
        }catch(Exception e)
        {
            e.printStackTrace();
            return;
        }
        //�����������ļ�
        StringBuffer sb = new StringBuffer();
        for(int i = 0;i < c;i++)
            sb.append(i + ".pdf ");
        String s = localPath.substring(0,2) + "\r\n" +
            "cd " + localPath + "\\data\r\n" +
            "pdftk.exe " + sb.toString() + " cat output " + caseno + ".pdf";
        try{
            FileTool.setByte(localPath + "\\data\\pdf.bat", s.getBytes());
        }catch(Exception e)
        {
            e.printStackTrace();
            return;
        }
        //ִ���������ļ�
        TParm p = new TParm(PdfTool.getInstance().exec(localPath + "\\data\\pdf.bat"));
        if(p.getErrCode() != 0)
        {
            messageBox_(p.getErrText());
            System.out.println(p.getErrText());
            return;
        }
        jacobTool.setBookmarks(localPath + "\\data\\" + caseno + ".pdf",list);
        for(int i = 0;i < c;i++)
        {
            File f1 = new File(localPath + "\\data\\" + i + ".pdf");
            f1.delete();
        }
        File f1 = new File(localPath + "\\data\\pdf.bat");
        f1.delete();
        f1 = new File(localPath + "\\data\\pdftk.exe");
        f1.delete();
        try{
            data = FileTool.getByte(localPath + "\\data\\" + caseno + ".pdf");
            FileTool.setByte(localPath + "\\" + caseno + ".pdf", data);
        }catch(Exception e)
        {
        }
        f1 = new File(localPath + "\\data\\" + caseno + ".pdf");
        f1.delete();
        f1 = new File(localPath + "\\data");
        f1.delete();
        messageBox_("�ϲ��ɹ�!");
    }
    /**
     * �ϴ�����
     */
    public void onUpdate()
    {
        String localPath = getText("tTextField_1");
        if(localPath == null || localPath.length() == 0)
        {
            messageBox_("��ѡ����ʱĿ¼!");
            return;
        }
        String caseno = getText("tTextField_4");
        if(caseno == null || caseno.length() == 0)
        {
            messageBox_("�����������!");
            return;
        }
        String mrno = getText("tTextField_3");
        if(mrno == null || mrno.length() == 0)
        {
            messageBox_("�����벡����!");
            return;
        }
        File f = new File(localPath + "\\" + caseno + ".pdf");
        if(!f.exists())
        {
            messageBox_("�����ļ�������!");
            return;
        }
        String fileName = fileServerRoot + "\\��ʽ����\\" + mrno.substring(0,6) + "\\" + mrno + "\\" + caseno + ".pdf";
        try{
            byte data[] = FileTool.getByte(f);
            if(!TIOM_FileServer.writeFile(TIOM_FileServer.getSocket(),fileName,data))
            {
                messageBox_("�ϴ�ʧ��!");
                return;
            }
        }catch(Exception e)
        {
        }
        messageBox_("�ϴ��ɹ�!");
    }
}
