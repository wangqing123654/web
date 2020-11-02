package jdo.pdf;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
import com.dongyang.config.TRegistry;
import java.io.PrintStream;
import java.util.List;
import org.pdfbox.pdmodel.*;
import org.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageFitWidthDestination;
import org.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import java.util.ArrayList;
import java.io.FileInputStream;
import org.pdfbox.pdfparser.PDFParser;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import org.pdfbox.util.PDFTextStripper;
import com.dongyang.data.TParm;

public class jacobTool {
    private static final Variant False = new Variant(false);
    private static final Variant True = new Variant(true);
    /**
     * ע�����Ŀ¼
     * @param path String
     */
    public static void Registry(String path)
    {
        TRegistry.set("HKEY_CURRENT_USER\\Software\\FinePrint Software\\pdfFactory3\\AutoSaveDir",path);
    }
    /**
     * ��ӡ��PDF�ļ�
     * @param filename String
     * @return boolean
     */
    public static boolean print(String filename)
    {
        try{
            ActiveXComponent wordCom = new ActiveXComponent("Word.Application");
            Dispatch wrdDocs = wordCom.getProperty("Documents").toDispatch();
            Object wordDoc = Dispatch.invoke(wrdDocs, "Open", Dispatch.Method,
                    new Object[]{ filename }, new int[1]).toDispatch();

            //�Ƿ��ں�̨����
            Variant Background = False;
            //�Ƿ�׷�Ӵ�ӡ
            Variant Append = False;
            //��ӡ�����ĵ�
            int wdPrintAllDocument = 0;
            Variant Range = new Variant(wdPrintAllDocument);

            wordCom.invoke("PrintOut", new Variant[]{Background, Append, Range});
            wordCom.invoke("Quit", new Variant[]{});
        }catch(Exception e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public static String getPath(String fileName)
    {
        int index = fileName.lastIndexOf("\\");
        return fileName.substring(0,index);
    }
    public static int getCount(String fileName)
    {
        int count = 0;
        PDDocument document;
        document = null;
        try {
            document = PDDocument.load(fileName);
            List pages = document.getDocumentCatalog().getAllPages();
            count = pages.size();
            document.close();
        }
        catch (Exception e) {
        }
        return count;
    }

    public static String setBookmarks(String fileName,List marks)
    {
        String path = getPath(fileName);
        int rows[] = new int[marks.size()];
        int row = 0;
        for(int i = 0;i < marks.size();i++)
        {
            rows[i] = row;
            row += getCount(path + "\\" + i + ".pdf");
        }
        PDDocument document;
        document = null;
        try {
            document = PDDocument.load(fileName);
            if (document.isEncrypted())
                return "Error: Cannot add bookmarks to encrypted document.";
            PDDocumentOutline outline = new PDDocumentOutline();
            document.getDocumentCatalog().setDocumentOutline(outline);
            PDOutlineItem pagesOutline = new PDOutlineItem();
            pagesOutline.setTitle("All Pages");
            outline.appendChild(pagesOutline);
            List pages = document.getDocumentCatalog().getAllPages();
            for (int i = 0; i < rows.length; i++) {
                PDPage page = (PDPage) pages.get(rows[i]);
                PDPageFitWidthDestination dest = new PDPageFitWidthDestination();
                dest.setPage(page);
                PDOutlineItem bookmark = new PDOutlineItem();
                bookmark.setDestination(dest);
                bookmark.setTitle((String)marks.get(i));
                pagesOutline.appendChild(bookmark);
            }

            pagesOutline.openNode();
            outline.openNode();
            document.save(fileName);
            document.close();
        }
        catch (Exception e) {
            return e.getMessage();
        }
        return "";
    }
    /**
     * ����PDF�ļ�
     * @param filename String
     * @return String
     */
    public static String getTextFromPdf(String filename)
    {
        try{
            FileInputStream is = new FileInputStream(filename);
            String s = getTextFromPdf(is);
            is.close();
            return s;
        }catch(Exception e)
        {
            e.printStackTrace();
            return "";
        }
    }
    /**
     * ����PDF�ļ�
     * @param stream InputStream
     * @return String
     */
    public static String getTextFromPdf(InputStream stream)
    {
        try{
            PDFParser parser = new PDFParser(stream);
            parser.parse();
            PDDocument nbsp = parser.getPDDocument();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            OutputStreamWriter writer = new OutputStreamWriter(out);
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.writeText(nbsp, writer);
            byte[] contents = out.toByteArray();
            //nbsp.clearWillEncryptWhenSaving();
            writer.close();
            return new String(contents);
        }catch(Exception e)
        {
            e.printStackTrace();
            return "";
        }
    }
    public static void main1(String args[])
    {
        //String s = getTextFromPdf("C:\\Documents and Settings\\li\\����\\pdf\\�½��ļ���\\1\\Ѫ����.pdf");//���µ�.pdf");//
        //TParm p = PDFT.check(s);
        //System.out.println(p);
        //TParm p = PDFT.look("D:\\PDF\\�½��ļ���","D:\\PDF\\�½��ļ���");
        //System.out.println(p);
        System.out.println(jacobTool.getTextFromPdf("D:\\PDF\\�½��ļ���\\000000273030_���鱨�浥_2010-11-23-10-50.pdf"));

    }
    public static void main(String[] argv){
        List list = new ArrayList();
        list.add("�״β��̼�¼");
        list.add("סԺҽ�������Է���Ŀȷ��ͬ����");
        list.add("��Ժ72Сʱ�ڲ����֪��");
        list.add("��Ժ��¼");
        list.add("LIS��鱨��2");
        setBookmarks("D:\\temp\\data\\100113000016.pdf",list);
        //print("D:\\lzk\\Project\\word\\11.doc");
    }
}
