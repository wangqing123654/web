package jdo.pdf;

import com.dongyang.data.TParm;
import java.io.File;
import com.dongyang.util.FileTool;
import java.util.List;
import java.util.ArrayList;

/**
 *
 * <p>Title: PDF����</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author lzk 2010.12.6
 * @version 1.0
 */
public class PDFT {
    public static TParm check(String s)
    {
        //�������µ�
        TParm p = isTWD(s);
        if(p.getErrCode() != -1)
            return p;

        //�����¼��
        p = isMZJLD(s);
        if(p.getErrCode() != -1)
            return p;
        //����ѭ����¼��
        p = isTWXHJLD(s);
        if(p.getErrCode() != -1)
            return p;
        //���鱨�浥
        p = isJCBGD(s);
        if(p.getErrCode() != -1)
            return p;
        //�������浥
        p = isCSBGD(s);
        if(p.getErrCode() != -1)
            return p;
        return p;
    }
    /**
     * �������µ�
     * @param s String
     * @return TParm
     */
    public static TParm isTWD(String s)
    {
        TParm p = new TParm();
        if(s == null || s.length() == 0)
        {
            p.setErr(-1,"�ļ�����!");
            return p;
        }
        if(!"���±�".equals(getRow(s,1)))
        {
            p.setErr(-1,"���ʹ���!");
            return p;
        }
        String s1 = getRow(s,0);
        p.setData("TYPE","���±�");
        String mrno = s1.substring("�����ũs".length());
        p.setData("MR_NO",mrno);
        s1 = getRow(s,3);
        int i = s1.indexOf("�Ա�");
        if(i != -1)
            p.setData("NAME",s1.substring("����:".length(),i));
        s1 = getRow(s,4);
        p.setData("DATE",rq(s1.substring(0,10)));
        return p;
    }
    public static String rq(String s)
    {
        StringBuffer sb = new StringBuffer();
        for(int i = 0;i < s.length();i++)
        {
            char c = s.charAt(i);
            if(c == '/' || c == '��' || c == '��' || c == ':')
                c = '-';
            if(c == ' ' || c == '��')
                continue;
            if(sb.length() == 10)
                sb.append("-");
            sb.append(c);
        }
        return sb.toString();
    }
    /**
     * �����¼��
     * @param s String
     * @return TParm
     */
    public static TParm isMZJLD(String s)
    {
        TParm p = new TParm();
        if(s == null || s.length() == 0)
        {
            p.setErr(-1,"�ļ�����!");
            return p;
        }
        String t = "\r\n";
        int index = s.indexOf("�����ţ�" + t + "�����¼��\r\n");
        if(index == -1)
        {
            p.setErr(-1,"���ʹ���!");
            return p;
        }
        s = s.substring(index + ("�����ţ�" + t + "�����¼��\r\n").length());
        index = s.indexOf("\r\n");
        s = s.substring(index + 2);
        index = s.indexOf("\r\n");
        String mrno = s.substring(0,index);
        p.setData("TYPE","�����¼��");
        p.setData("MR_NO",mrno);
        t = "���䣺\r\n";
        index = s.indexOf(t);
        s = s.substring(index + t.length());
        index = s.indexOf("\r\n");
        p.setData("DATE",rq(s.substring(0,index)));
        return p;
    }
    /**
     * ����ѭ����¼��
     * @param s String
     * @return TParm
     */
    public static TParm isTWXHJLD(String s)
    {
        TParm p = new TParm();
        if(s == null || s.length() == 0)
        {
            p.setErr(-1,"�ļ�����!");
            return p;
        }
        int index = s.indexOf("\r\n����ѭ����¼��  ��");
        if(index == -1)
        {
            p.setErr(-1,"���ʹ���!");
            return p;
        }
        String t = "\r\n140\r\n0\r\n";
        index = s.indexOf(t);
        s = s.substring(index + t.length());
        index = s.indexOf("\r\n");
        String mrno = s.substring(0,index);
        p.setData("TYPE","����ѭ����¼��");
        p.setData("MR_NO",mrno);
        index = s.indexOf("��עҽʦ��   ");
        s = s.substring(index + "��עҽʦ��   ".length());
        index = s.indexOf("\r\n");
        p.setData("DATE",rq(s.substring(0,index)));
        return p;
    }
    /**
     * ���鱨�浥
     * @param s String
     * @return TParm
     */
    public static TParm isJCBGD(String s)
    {
        TParm p = new TParm();
        if(s == null || s.length() == 0)
        {
            p.setErr(-1,"�ļ�����!");
            return p;
        }
        String t = "\r\n���鱨�浥 ������: ";
        int index = s.indexOf(t);
        if(index == -1)
        {
            p.setErr(-1,"���ʹ���!");
            return p;
        }
        int i = s.indexOf("\r\n����ʱ��");
        String rq = s.substring(i + "\r\n����ʱ��".length());
        rq = rq.substring(0,16);
        s = s.substring(index + t.length());
        index = s.indexOf("Pat.No.");
        s = s.substring(0,index);
        p.setData("TYPE","���鱨�浥");
        p.setData("MR_NO",s);
        p.setData("DATE",rq(rq));
        return p;
    }
    /**
     * �������浥
     * @param s String
     * @return TParm
     */
    public static TParm isCSBGD(String s)
    {
        TParm p = new TParm();
        if(s == null || s.length() == 0)
        {
            p.setErr(-1,"�ļ�����!");
            return p;
        }
        String t = "\r\n�������浥 \r\n";
        int index = s.indexOf(t);
        if(index == -1)
        {
            p.setErr(-1,"���ʹ���!");
            return p;
        }
        p.setData("TYPE","�������浥");
        p.setData("MR_NO","");
        index = s.indexOf("������� ");
        s = s.substring(index + "������� ".length());
        index = s.indexOf("\r\n");
        s = s.substring(0,index);
        p.setData("DATE",rq(s));
        p.setErr(-1,"������Ϊ��!");
        return p;
    }
    /**
     * ��ȡ��
     * @param s String
     * @param row int
     * @return String
     */
    public static String getRow(String s,int row)
    {
        String t = "\r\n";
        if(s == null || s.length() == 0)
            return "";
        int index1 = 0;
        int index2 = s.indexOf(t);
        if(index2 == -1)
            if(row == 0)
                return s;
            else
                return "";
        for(int i = 0;i < row;i++)
        {
            index1 = index2 + 2;
            index2 = s.indexOf(t,index1);
            if(index2 == -1)
                if(i == row - 1)
                    index2 = s.length();
                else
                    return "";
        }
        return s.substring(index1,index2);
    }
    /**
     * ɨ�贰��
     * @param dir1 String
     * @return TParm
     */
    public static TParm listener(String dir1)
    {
        TParm parm = new TParm();
        File f = new File(dir1);
        File list[] = f.listFiles();
        List l = new ArrayList();
        for(int i = 0;i < list.length;i++)
        {
            if(!list[i].isFile())
                continue;
            String name = list[i].getName();
            String s = dir1 + "\\" + name;
            parm.addData("FILE_NAME",name);

            TParm p = check(jacobTool.getTextFromPdf(s));
            parm.addData("MR_NO",p.getValue("MR_NO"));
            parm.addData("TYPE",p.getValue("TYPE"));
            parm.addData("DATE",p.getValue("DATE"));
            if(p.getErrCode() == -1)
                parm.addData("STATE",p.getErrText());
            else
                parm.addData("STATE","OK");
        }
        return parm;
    }
    public static TParm look(String dir1,String dir2)
    {
        TParm parm = new TParm();
        File f = new File(dir1);
        File list[] = f.listFiles();
        List l = new ArrayList();
        for(int i = 0;i < list.length;i++)
        {
            if(!list[i].isFile())
                continue;
            String name = list[i].getName();
            String s = dir1 + "\\" + name;

            TParm p = check(jacobTool.getTextFromPdf(s));
            if(p.getErrCode() == -1)
            {
                parm.addData("FileName",name);
                parm.addData("Data",p.getErrText());
                continue;
            }
            System.out.println(p);
            try{
                byte data[] = FileTool.getByte(s);
                String n = p.getValue("MR_NO") + "_" + p.getValue("TYPE") + "_" + p.getValue("DATE");
                FileTool.setByte(dir2 + "\\" + n,data);
                l.add(s);
            }catch(Exception e)
            {
                parm.addData("FileName",name);
                parm.addData("Data",e.getMessage());
            }
        }
        /*for(int i = 0;i < l.size();i++)
        {
            File f1 = new File((String)l.get(i));
            f1.delete();
        }*/
        return parm;
    }
    public static void main(String args[])
    {
        System.out.println(getRow("2\n\rd",1));
    }
}

