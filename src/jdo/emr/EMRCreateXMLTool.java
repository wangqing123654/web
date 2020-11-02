package jdo.emr;

import com.dongyang.tui.text.IBlock;
import com.dongyang.tui.text.ETable;
import com.dongyang.tui.text.EComponent;
import com.dongyang.ui.TWord;
import com.dongyang.tui.text.EFixed;
import com.dongyang.tui.text.ECapture;
import com.dongyang.tui.text.MPage;
import com.dongyang.tui.text.EPage;
import com.dongyang.tui.text.EPanel;
import com.dongyang.tui.text.ECheckBoxChoose;
import com.dongyang.data.TSocket;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.config.TConfig;
import com.dongyang.tui.text.ETD;
import com.dongyang.tui.text.EMacroroutine;
import com.dongyang.tui.text.div.VPic;
import com.dongyang.tui.text.EImage;
import com.dongyang.tui.text.image.GBlock;
import com.dongyang.tui.text.image.GLine;
import com.dongyang.data.TParm;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import java.io.StringWriter;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.stream.StreamResult;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.CDATASection;
import com.dongyang.tui.text.ETextFormat;
import com.dongyang.tui.text.div.VLine;
import com.dongyang.tui.text.div.DIV;
import com.dongyang.tui.text.div.VTable;
import com.dongyang.tui.text.div.VText;
import com.dongyang.tui.text.div.VBarCode;
import com.dongyang.tui.text.div.MV;
import com.dongyang.jdo.TJDODBTool;
import org.w3c.dom.NodeList;
import com.dongyang.tui.text.EMicroField;
import com.dongyang.tui.text.EText;
import org.w3c.dom.ProcessingInstruction;
import com.dongyang.tui.text.DStyle;
import com.dongyang.tui.text.ETR;
import com.dongyang.tui.text.ui.CTable;
import com.dongyang.tui.DText;
import com.dongyang.tui.text.EPic;
import com.dongyang.tui.text.div.MVList;

/**
 * <p>Title: EMRתXML������</p>
 *
 * <p>Description:EMRתXML������ </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: javahis</p>
 *
 * @author sundx
 * @version 1.0
 */
public class EMRCreateXMLTool {

    /**
     * WORD����
     */
    private DText word;

    /**
     * XML
     */
    private Document XMLDoc;


    /**
     * XML
     */
    private Document XMLDocCDA;

    /**
     * ����
     */
    private String ENCODING = "UTF-8";

    /**
     * CDA��ǩ���ڵ�
     */
    private String CDA_INDEX = "JAVAHIS-EMR-CDA";

    /**
     * ģ���������
     */
    private String CDA_INDEX_CODE = "HR00.00.001.05";

    /**
     * ʵ��
     */
    private static EMRCreateXMLTool instanceObject;


    /**
     * ������
     */
    public EMRCreateXMLTool() {
    }

    /**
     * �õ�ʵ��
     * @return EMRCreateXMLTool
     */
    public static EMRCreateXMLTool getInstance() {
        if (instanceObject == null)
            instanceObject = new EMRCreateXMLTool();
        return instanceObject;
    }

    /**
     * XML�ļ�
     * @param path String
     * @param fileName String
     * @param dir String
     * @param word TWord
     */
    public void createXML(String path,String fileName,String dir,TWord word){
        createXML(path,fileName,dir,word.getWordText());
    }
    /**
     * XML�ļ�
     * @param path String
     * @param fileName String
     * @param dir String
     * @param word TWord
     */
    public boolean createTXML(String path,String fileName,String dir,TWord word){
    	return createTXML(path,fileName,dir,word.getWordText());
    }

    /**
     * XML�ļ�
     * @param path String
     * @param fileName String
     * @param dir String
     * @param word DText
     */
    public void createXML(String path,String fileName,String dir,DText word){
        this.word = word;
        try {
            DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            XMLDoc=builder.newDocument();
            XMLDocCDA=builder.newDocument();
        } catch (ParserConfigurationException ex) {
            ex.printStackTrace();
        }
        Node nodeRoot = XMLRoot();
        setWordIndex();
        XMLRootCDA();
        for(int pageIndex = 0;pageIndex < getPageCount();pageIndex++)
            setPageData(pageIndex,nodeRoot);
        setTitleInfo(nodeRoot);
        writeFile(path,fileName,dir);
    }
    /**
     * XML�ļ�
     * @param path String
     * @param fileName String
     * @param dir String
     * @param word DText
     */
    public boolean createTXML(String path,String fileName,String dir,DText word){
        this.word = word;
        try {
            DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            XMLDoc=builder.newDocument();
            XMLDocCDA=builder.newDocument();
        } catch (ParserConfigurationException ex) {
            return false;
        }
        Node nodeRoot = XMLRoot();
        setWordIndex();
        XMLRootCDA();
        for(int pageIndex = 0;pageIndex < getPageCount();pageIndex++)
            setPageData(pageIndex,nodeRoot);
        setTitleInfo(nodeRoot);
        return writeFile(path,fileName,dir);
    }
    /**
     * ͨ����ϵͳ��CODE����CDA��Ӧ����;
     * @param cdaGroupName String
     * @param cdaCode String
     * @param sourceCode String
     * @return String
     */
    public String getCDACode(String cdaGroupName,String cdaCode,String sourceCode){
        String retCdaCode=sourceCode;

        return retCdaCode;
    }


    /**
     * ȡ���ĵ����
     */
    private void setWordIndex(){
       for(int i = 0;i < getPageManager().getMVList().size();i++){
           for (int j = 0; j < getPageManager().getMVList().get(i).size();j++) {
               DIV div = getPageManager().getMVList().get(i).get(j);
               if (!"UNVISITABLE_ATTR".equals(getPageManager().getMVList().get(i).getName()))
                   continue;
               if (!(div instanceof VText))
                   continue;
               if(!((VText)div).getName().equals(CDA_INDEX_CODE))
                   continue;
               CDA_INDEX = ((VText)div).getMicroText();
           }
       }
   }
   //$$=========Start add by lx �����ͼ���Ĵ���=============$$//
   /**
    *
    * @param nodeRoot Node
    * @param pic EPic
    */
   private void setPicInfo(EPic pic){
//       System.out.println("=====ȡPIC��ͼ�����=====");
       Node nodeRoot = XMLDocCDA.getElementsByTagName(CDA_INDEX).item(0);
       MVList pkList =pic.getMVList();
        //MVList pkList = pic.getPM().getPageManager().getMVList();
        for(int i = 0;i < pkList.size();i++){
//             System.out.println("PIC��ͼ������==========="+pkList.get(i).getName());
             for (int j = 0; j < pkList.get(i).size();j++) {
                    DIV div = pkList.get(i).get(j);
//                    System.out.println("=====div ����====="+div.getType());
//                    System.out.println("=====div ����====="+div.getName());
                    if(div instanceof VText){
                          //�Ƿ�ȡCDA����  20120813 shibl modify
                          if(((VText) div).getTakeCdaName()){
                        	  String cdaValue=((VText)div).getDrawText();
                              if(cdaValue!=null&&!cdaValue.equals("")){
                                  //������CDA��Ӧ���룬ȡ����;
                                  if ( ( (VText) div).getCdaValue() != null &&
                                      ! ( (VText) div).getCdaValue().equals("")) {
                                      cdaValue = ( (VText) div).getCdaValue();
                                  }

                              }
                              //��ȡCDA���ƣ�
                        	  appendNodeCDA( ((VText)div).getMicroName(),
                                      ( (VText) div).getGroupName(),
                                      cdaValue);
                          }else{
                        	  appendNodeCDA( ( (VText) div).getName(),
                                      ( (VText) div).getGroupName(),
                                      ( (VText) div).getMicroText());
                          }
                        continue;
                    }
                    setVInfo(div,nodeRoot);
             }

        }
        //System.out.println("=====setPicInfo���.=====");
   }



   //$$=========End add by lx �����ͼ���Ĵ���=============$$//
    /**
     * ����Title��Ϣ
     * @param nodeRoot Node
     */
    private void setTitleInfo(Node nodeRoot){
        for(int i = 0;i < getPageManager().getMVList().size();i++){
            //System.out.println("=============ͼ������==================="+getPageManager().getMVList().get(i).getName());
            for(int j = 0;j<getPageManager().getMVList().get(i).size();j++){
                DIV div = getPageManager().getMVList().get(i).get(j);
                if("UNVISITABLE_ATTR".equals(getPageManager().getMVList().get(i).getName())){
                    if(div instanceof VText)
                        appendNodeCDA(((VText)div).getName(),((VText)div).getGroupName(),((VText)div).getMicroText());
                    continue;
                }
                setVInfo(div,nodeRoot);
            }
        }

    }

    /**
     * ����Title��Ϣ
     * @param mv MV
     * @param nodeRoot Node
     */
    private void setTitleInfo(MV mv,Node nodeRoot){
        for(int i = 0;i < mv.size();i++)
            setVInfo(mv.get(i),nodeRoot);
    }

    /**
     * ����V�������
     * @param div DIV
     * @param nodeRoot Node
     */
    private void setVInfo(DIV div,Node nodeRoot){
//        System.out.println("setVInfo  div����============="+div.getType());
//        System.out.println("setVInfo  div����============="+div.getName());
        if(div instanceof VTable){
            setTitleInfo( ( (VTable) div).getMV(), nodeRoot);
        }
        if(div instanceof VLine){
            XMLElement("TITLE_VLINE", getStyle( (VLine) div), nodeRoot);
        }

        if(div instanceof VText){
            XMLElement("TITLE_VTEXT", getStyle( (VText) div), nodeRoot);
            //System.out.println("****div����******="+div.getType());
//            System.out.println("****div����******"+((VText)div).getName());
//            System.out.println("****divֵ******"+((VText)div).getMicroName());
            String cdaname=((VText)div).getName();
//            System.out.println("-------�Ƿ�ȡCDA����----------------------"+((VText) div).getTakeCdaName());
            //�Ƿ�ȡCDA����
            if(((VText) div).getTakeCdaName()){
                //��ȡCDA���ƣ�
                cdaname=((VText)div).getMicroName();
            }
            String cdaValue=((VText)div).getDrawText();
//            System.out.println("======cdaֵΪ��=========="+cdaValue);
            if(cdaValue!=null&&!cdaValue.equals("")){
                //������CDA��Ӧ���룬ȡ����;
                if ( ( (VText) div).getCdaValue() != null &&
                    ! ( (VText) div).getCdaValue().equals("")) {
                    cdaValue = ( (VText) div).getCdaValue();
                }

            }
            //CDAֵ���գ��򴴽�ֵ;
            if(cdaValue!=null&&!cdaValue.equals("")){
                appendNodeCDA(cdaname,((VText)div).getGroupName(),cdaValue);
            }

        }
        if(div instanceof VPic){
            XMLElement("TITLE_VPIC", getStyle( (VPic) div), nodeRoot);
        }
        if(div instanceof VBarCode){
            XMLElement("TITLE_VBARCODE", getStyle( (VBarCode) div), nodeRoot);
        }



    }



    /**
     * ����Title��Ϣ
     * @param div VText
     * @return TParm
     */
    private TParm getStyle(VText div){
       TParm parm = new TParm();
       parm.setData("ALIGNMENT",div.getAlignment());
       parm.setData("AUTOENTERHEIGHT",div.getAutoEnterHeight());
       parm.setData("COLOR",div.getColor());
       parm.setData("DRAWSTARTX",div.getDrawStartX());
       parm.setData("DRAWSTARTY",div.getDrawStartY());
       parm.setData("DRAWSTARTXP",div.getDrawStartXP());
       parm.setData("DRAWSTARTYP",div.getDrawStartYP());
       parm.setData("TEXT",div.getText());
       parm.setData("ISTEXTT",div.isTextT());
       parm.setData("DRAWTEXT",div.getDrawText());
       parm.setData("X",div.getX());
       parm.setData("Y",div.getY());
       parm.setData("X0",div.getX0());
       parm.setData("Y0",div.getY0());
       parm.setData("ENDX",div.getEndX());
       parm.setData("ENDXP",div.getEndXP());
       parm.setData("STARTX",div.getStartX());
       parm.setData("STARTY",div.getStartY());
       parm.setData("STARTXP",div.getStartXP());
       parm.setData("STARTYP",div.getStartYP());
       parm.setData("FONTSIZE",div.getFontSize());
       parm.setData("FONTSTYLE",div.getFontStyle());
       parm.setData("FONTNAME",div.getFontName());
       return parm;
   }


   private TParm getStyle(VBarCode div){
      TParm parm = new TParm();
      parm.setData("X",div.getX());
      parm.setData("Y",div.getY());
      parm.setData("XDIMENSIONCM",div.getXDimensionCM());
      parm.setData("BACKGROUND",div.getBackground());
      parm.setData("BACKGROUNDSTRING",div.getBackgroundString());
      parm.setData("BARCOLOR",div.getBarColor());
      parm.setData("BARCOLORSTRING",div.getBarColorString());
      parm.setData("BARHEIGHTCM",div.getBarHeightCM());
      parm.setData("BARTEXTCOLOR",div.getBarTextColor());
      parm.setData("BARTEXTCOLORSTRING",div.getBarTextColorString());
      parm.setData("CHECKCHARACTER",div.getCheckCharacter());
      parm.setData("DRAWSTARTX",div.getDrawStartX());
      parm.setData("DRAWSTARTXP",div.getDrawStartXP());
      parm.setData("DRAWSTARTY",div.getDrawStartY());
      parm.setData("DRAWSTARTYP",div.getDrawStartYP());
      parm.setData("DRAWTEXT",div.getDrawText());
      parm.setData("FONT",div.getFont());
      parm.setData("FONTNAME",div.getFontName());
      parm.setData("FONTSIZE",div.getFontSize());
      parm.setData("FONTSTYLE",div.getFontStyle());
      parm.setData("TEXT",div.getText());
      return parm;
   }

   /**
    * ����Title��Ϣ
    * @param div VLine
    * @return TParm
    */
   private TParm getStyle(VLine div){
       TParm parm = new TParm();
       parm.setData("COLOR",div.getColor());
       parm.setData("X0",div.getX0());
       parm.setData("Y0",div.getY0());
       parm.setData("X1",div.getX1());
       parm.setData("Y1",div.getY1());
       parm.setData("X2",div.getX2());
       parm.setData("Y2",div.getY2());
       parm.setData("LINETYPE",div.getLineType());
       parm.setData("LINEWIDTH",div.getLineWidth());
       parm.setData("DRAWSTARTX",div.getDrawStartX());
       parm.setData("DRAWSTARTY",div.getDrawStartY());
       parm.setData("DRAWSTARTXP",div.getDrawStartXP());
       parm.setData("DRAWSTARTYP",div.getDrawStartYP());
       parm.setData("DRAWSX1",div.getDrawX1());
       parm.setData("DRAWSY1",div.getDrawY1());
       parm.setData("DRAWSX1P",div.getDrawX1P());
       parm.setData("DRAWSY1P",div.getDrawY1P());
       return parm;
   }

   /**
    * ����Title��Ϣ
    * @param div VPic
    * @return TParm
    */
   private TParm getStyle(VPic div){
       TParm parm = new TParm();
       parm.setData("PICTURENAME",div.getPictureName());
       parm.setData("DRAWSTARTX",div.getDrawStartX());
       parm.setData("DRAWSTARTY",div.getDrawStartY());
       parm.setData("HEIGHT",div.getHeight());
       parm.setData("WIDTH",div.getWidth());
       parm.setData("X",div.getX());
       parm.setData("Y",div.getY());
       parm.setData("ENDX",div.getEndX());
       parm.setData("ENDY",div.getEndY());
       parm.setData("ENDXP",div.getEndXP());
       parm.setData("ENDYP",div.getEndYP());
       parm.setData("STARTX",div.getStartX());
       parm.setData("STARTY",div.getStartY());
       parm.setData("STARTXP",div.getStartXP());
       parm.setData("STARTYP",div.getStartYP());
       return parm;
   }

   /**
    * д�ļ�
    * @param path String
    * @param fileName String
    * @param dir String
    * @return boolean
    */
   private boolean writeFile(String path,String fileName,String dir){
        String fileServerIP = TConfig.getSystemValue("FileServer.Main.IP");
        int port = TSocket.FILE_SERVER_PORT;
        TSocket socket = new TSocket(fileServerIP,port);
        path = path.replaceFirst("JHW","XML");
        try{
            if (!TIOM_FileServer.writeFile(socket, getDir(dir) +
                                           getServerFileName(path, fileName),
                                           toXMLString().getBytes(ENCODING)))
                return false;
            if (!TIOM_FileServer.writeFile(socket, getDir(dir) +
                                           getServerFileName(path,fileName + "_CDA"),
                                           toXMLStringCDA().getBytes(ENCODING)))
                return false;
        }catch(Exception e){
            e.printStackTrace();
        }
        return true;

    }

    /**
     * �õ�Ŀ¼
     * @param dir String
     * @return String
     */
    private String getDir(String dir)
    {
        return TIOM_FileServer.getRoot() + TIOM_FileServer.getPath(dir);
    }

    /**
     * �õ��ļ���
     * @param path String
     * @param fileName String
     * @return String
     */
    private String getServerFileName(String path,String fileName)
    {
        if(path == null || path.length() == 0)
            return "";
        if(fileName == null || fileName.length() == 0)
            return "";
        if(!path.endsWith("\\"))
            path += "\\";
        fileName = path + fileName;
        if(!fileName.endsWith(".xml"))
            fileName += ".xml";
        return fileName;
    }

    /**
     * ��ȡҳ����Ϣ
     * @param pageIndex int
     * @param parent Node
     */
    private void setPageData(int pageIndex,Node parent){
        Node pageNode= XMLElement("PAGE",getStyle(getEPage(pageIndex),pageIndex + 1),parent);
        for(int panelIndex = 0;panelIndex < getPagePanelCount(pageIndex);
                             panelIndex++){
            setPanelData(pageIndex,panelIndex,pageNode);
        }
    }

    /**
     * ȡ��ҳ������
     * @param page EPage
     * @param pageNo int
     * @return TParm
     */
    private TParm getStyle(EPage page,int pageNo){
       TParm parm = new TParm();
       parm.setData("BASEHEIGHT",page.getBaseHeight());
       parm.setData("BASEWIDTH",page.getBaseWidth());
       parm.setData("HEIGHT",page.getHeight());
       parm.setData("WIDTH",page.getWidth());
       parm.setData("INSETSHEIGHT",page.getInsetsHeight());
       parm.setData("INSETSWIDTH",page.getInsetsWidth());
       parm.setData("X",page.getX());
       parm.setData("Y",page.getY());
       parm.setData("INSETSX",page.getInsetsX());
       parm.setData("INSETSY",page.getInsetsY());
       parm.setData("Top",getPageManager().getImageableY());
       parm.setData("Bottom",getPageManager().getPageHeight() -
                             getPageManager().getImageableHeight() -
                             getPageManager().getImageableY());
       parm.setData("Left",getPageManager().getImageableX());
       parm.setData("Right",getPageManager().getPageWidth() -
                            getPageManager().getImageableWidth() -
                            getPageManager().getImageableX());
       parm.setData("E_Top",getPageManager().getEditInsets().top);
       parm.setData("E_Bottom",getPageManager().getEditInsets().bottom);
       parm.setData("E_Left",getPageManager().getEditInsets().left);
       parm.setData("E_Right",getPageManager().getEditInsets().right);
       parm.setData("no",pageNo);
       return parm;
   }

   /**
    * ��ȡ�����Ϣ
    * @param pageIndex int
    * @param panelIndex int
    * @param parent Node
    */
   private void setPanelData(int pageIndex,int panelIndex,Node parent){
        for(int blockIndex = 0;blockIndex < getPagePanelBlockCount(pageIndex,
                panelIndex);blockIndex++){
            if(!isTable(pageIndex,panelIndex,blockIndex)){
                IBlock block = getIBlock(pageIndex,panelIndex,blockIndex);
//                System.out.println("--------block-----------------"+block.getBlockValue());
                if(block.getNextTrueBlock() != null &&
                   (block.getNextTrueBlock().findIndex()) - blockIndex > 1)
                    continue;
                setElement(block,parent);
                continue;
            }
            setTableData(pageIndex,panelIndex,blockIndex,parent);
        }
    }

    /**
     * ��ȡ�����Ϣ
     * @param pageIndex int
     * @param panelIndex int
     * @param blockIndex int
     * @param parent Node
     */
    private void setTableData(int pageIndex,int panelIndex,int blockIndex,Node parent){
        Node nodeTable = XMLElement("table",getStyle(getETable(pageIndex,panelIndex,blockIndex)),parent);
        for(int row = 0;row < getTableRowCount(pageIndex,panelIndex,blockIndex);row ++ ){
            Node nodeTr = XMLElement("tr",getStyle(getETR(pageIndex,panelIndex,blockIndex,row)),nodeTable);
            setTableRowData(pageIndex,panelIndex,blockIndex,row,nodeTr);
        }
    }

    /**
     * ȡ�ñ������
     * @param table ETable
     * @return TParm
     */
    private TParm getStyle(ETable table){
        TParm parm = new TParm();
        parm.setData("X",table.getX());
        parm.setData("Y",table.getPanel().getY() + table.getY());
        parm.setData("HEIGHT",table.getHeight());
        parm.setData("WIDTH",table.getWidth());
        parm.setData("INSETSHEIGHT",table.getInsetsHeight());
        parm.setData("INSETSWIDTH",table.getInsetsWidth());
        parm.setData("INSETSX",table.getInsetsX());
        parm.setData("INSETSY",table.getInsetsY());
        return parm;
   }

   /**
    * ��ȡ���������
    * @param pageIndex int
    * @param panelIndex int
    * @param blockIndex int
    * @param row int
    * @param parent Node
    */
   private void setTableRowData(int pageIndex,int panelIndex,
                                 int blockIndex,int row,Node parent){
        for(int column = 0;column < getTableRowColumnCount(pageIndex,
                panelIndex,blockIndex,row);column ++ ){
            Node nodeTd = XMLElement("td",
                                     getStyle(getETD(pageIndex,panelIndex,blockIndex,row,column)),
                                     parent);
            setTableRowColumn(pageIndex,panelIndex,blockIndex,row,column,nodeTd);
        }
    }

    /**
     * ȡ�ñ��������
     * @param eTD ETD
     * @return TParm
     */
    private TParm getStyle(ETD eTD){
        TParm parm = new TParm();
        parm.setData("HEIGHT",eTD.getHeight());
        parm.setData("WIDTH",eTD.getWidth());
        parm.setData("X",eTD.getX());
        parm.setData("Y",eTD.getY());
        parm.setData("ALIGNMENT",eTD.getAlignment());
        parm.setData("INSETSHEIGHT",eTD.getInsetsHeight());
        parm.setData("INSETSWIDTH",eTD.getInsetsWidth());
        parm.setData("INSETSX",eTD.getInsetsX());
        parm.setData("INSETSY",eTD.getInsetsY());
        parm.setData("ISVISIBLE", eTD.isVisible());

        return parm;
   }

   /**
    * �ĵ������
    * @param eTR ETR
    * @return TParm
    */
   private TParm getStyle(ETR eTR){
       TParm parm = new TParm();
       parm.setData("X",eTR.getX());
       parm.setData("Y",eTR.getPanel().getY() + eTR.getY());
       parm.setData("HEIGHT",eTR.getHeight());
       parm.setData("WIDTH",eTR.getWidth());
       parm.setData("INSETSHEIGHT",eTR.getInsetsHeight());
       parm.setData("INSETSWIDTH",eTR.getInsetsWidth());
       parm.setData("INSETSX",eTR.getInsetsX());
       parm.setData("INSETSY",eTR.getInsetsY());
       return parm;
   }
   /**
    * ��ȡ�����������
    * @param pageIndex int
    * @param panelIndex int
    * @param blockIndex int
    * @param row int
    * @param column int
    * @param parent Node
    */
   private void setTableRowColumn(int pageIndex,int panelIndex,
                                   int blockIndex,int row,int column,Node parent){
       String value = "";
        for(int tpc = 0;tpc < getTableRowColumnPanelCount(pageIndex,panelIndex,
                blockIndex,row,column);tpc++){
            value = setTableRowColumnPanelData(pageIndex,panelIndex,blockIndex,
                                               row,column,tpc,parent);
        }
        tableEMacroroutineToCDA(pageIndex,panelIndex,blockIndex,column,value);
    }

    /**
     * �ĵ������
     * @param pageIndex int
     * @param panelIndex int
     * @param blockIndex int
     * @param row int
     * @param column int
     * @param tpc int
     * @param parent Node
     * @return String
     */
    private String setTableRowColumnPanelData(int pageIndex,int panelIndex,
                                            int blockIndex,int row,
                                            int column,int tpc,Node parent){
        String value = "";
        if(getTableRowColumnPanelBlockCount(pageIndex,panelIndex,
                                            blockIndex,row,column,tpc) <= 0)
            return "";
        IBlock obj = (IBlock)getTableBlock(pageIndex,panelIndex,
                                           blockIndex,row,column,tpc,0);
        if(obj == null)
            return "";
        while(obj != null){
            setElement(obj,parent);
            value += getTableCellValue(obj);
            obj = obj.getNextTrueBlock();
        }
        return value;
    }

    /**
     * ȡ�õ�Ԫ���ı�
     * @param obj IBlock
     * @return String
     */
    private String getTableCellValue(IBlock obj){
        if(obj instanceof EText &&
           ((EText)obj).getBlockValue() != null &&
           ((EText)obj).getBlockValue().length() != 0){
            return ((EText)obj).getBlockValue();
        }
        return "";
    }


    /**
     * ���ø��ӱ������д��CDA
     * @param pageIndex int
     * @param panelIndex int
     * @param blockIndex int
     * @param column int
     * @param value String
     */
    private void tableEMacroroutineToCDA(int pageIndex,int panelIndex,
                                         int blockIndex,int column,String value){
        if(value == null ||
           value.length() == 0)
            return;
        ETable table =getETable(pageIndex,panelIndex,blockIndex);
        if(table == null)
            return;
        CTable cTable = table.getCTable();
        if(cTable == null)
            return;
        //String code = cTable.getColumnModel().get(column).getSyntax().getName();
        //appendNodeCDA(code,table.getName(),value);
        String groupName=cTable.getColumnModel().get(column).getGroupName();
        if(groupName==null||groupName.length()==0)
            return;
        String code=cTable.getColumnModel().get(column).getCdaName();
        if(code==null||code.length()==0)
            return;

        appendNodeCDA(code,groupName,value);

    }

    /**
     * ȡ���߶�����
     * @param line GLine
     * @return TParm
     */
    private TParm getStyle(GLine line){
        TParm parm = new TParm();
        parm.setData("HEIGHT",line.getHeight());
        parm.setData("WIDTH",line.getWidth());
        parm.setData("X",line.getX());
        parm.setData("Y",line.getY());
        return parm;
    }

    /**
     * ȡ��Ԫ�ػ�������
     * @param gBlock GBlock
     * @return TParm
     */
    private TParm getStyle(GBlock gBlock){
        TParm parm = new TParm();
        parm.setData("HEIGHT",gBlock.getHeight());
        parm.setData("WIDTH",gBlock.getWidth());
        parm.setData("X",gBlock.getX());
        parm.setData("Y",gBlock.getY());

        //System.out.println("=====gBlock��ɫ====="+gBlock.getBorderColor());
        //System.out.println("=====gBlockɫ====="+gBlock.getColor());
        //System.out.println("=====gBlock�ǿ��====="+gBlock.getBorderWidth());
        //System.out.println("=====gBlock�Ƿ���ʾ====="+gBlock.isVisible());
        parm.setData("ISVISIBLE",gBlock.isVisible());
        return parm;
    }

    /**
     * ȡ��ͼƬ����
     * @param eImage EImage
     * @return TParm
     */
    private TParm getStyle(EImage eImage){
        TParm parm = new TParm();
        parm.setData("HEIGHT",eImage.getHeight());
        parm.setData("WIDTH",eImage.getWidth());
        parm.setData("X",eImage.getX());
        parm.setData("Y",eImage.getY());
        parm.setData("BORDERVISIBLE",eImage.isBorderVisible());
        return parm;
    }

    /**
     * ��ȡͼƬ��Ϣ
     * @param eImage EImage
     * @param parent Node
     */
    private void setEImageData(EImage eImage,Node parent){
        Node nodeEImage = XMLElement("EIMAGE",getStyle(eImage),parent);
        for(int i = 0;i < eImage.size();i++){
            GBlock gBlock = eImage.get(i);
            if(gBlock instanceof GLine){
                XMLElement("GLINE",getStyle((GLine)gBlock),nodeEImage);
                continue;
            }
            XMLElement("GBLOCK",getStyle(gBlock),nodeEImage);
        }
    }

    private TParm getStyle(EText text){
        TParm parm = new TParm();
        parm.setData("TYPE",text.getObjectType());
        parm.setData("X",text.getX());
        parm.setData("Y",text.getPanel().getY() + text.getY());
        parm.setData("Width",text.getWidth());
        parm.setData("Position",text.getPosition());
		try {
			// FontName->family
			parm.setData("family", text.getFontName());
			// FontSize->size
			parm.setData("size", text.getFontSize());
			// style����Щ����
			parm.setData("style", ((DStyle) text.getStyle()).getFont()
					.getStyle());
			parm.setData("Length", text.getLength());
			parm.setData("Bold", text.isBold());
			parm.setData("Italic", text.isItalic());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
        return parm;
    }

    /**
     * ����Ԫ�������Ϣ
     * @param block IBlock
     * @param parent Node
     */
    private void setElement(IBlock block,Node parent) {
        XMLElementCDA(block);
        if(block instanceof EMacroroutine && ((EMacroroutine)block).isPic()){
            //((EMacroroutine) block).getName()
            XMLElement("VPIC",
                           ((VPic)((EMacroroutine) block).getModel().getMVList().get(0).get(0)).getPictureName(),
                           getStyle((EMacroroutine) block),parent);
            return;
        }
        if(block instanceof EImage){
            setEImageData((EImage)block,parent);
            return;
        }
        if(block.getObjectType() == EComponent.TEXT_TYPE){

            XMLElement("TEXT",
                       ((EText)block).getString(),
                       getStyle((EText)block),parent);
        }
        if (!(block instanceof EFixed))
            return;
        switch (block.getObjectType()) {
        case EComponent.CAPTURE_TYPE:
            if (((ECapture) block).getCaptureType() == 1)
                return;
            /**XMLElement(((EFixed) block).getName(),
                       ((ECapture) block).getValue(),
                       getStyle((EFixed) block),parent);**/
            return;
        case EComponent.CHECK_BOX_CHOOSE_TYPE:
            XMLElement(((EFixed) block).getName(),
                       ((ECheckBoxChoose) block).isChecked() ? "��" : "��",
                       getStyle((EFixed) block),parent);
            return;
        case EComponent.TEXTFORMAT_TYPE:
            setTextFormatElement(block,parent);
            return;

        default:
            XMLElement(((EFixed) block).getName(),
                       block.getBlockValue(),
                       getStyle((EFixed) block),parent);
            return;
        }
    }

    /**
     * ���������ñ��뼰����
     * @param block IBlock
     * @param parent Node
     */
    private void setTextFormatElement(IBlock block,Node parent){
        Node node = XMLElement(((ETextFormat) block).getName(),getStyle((ETextFormat) block),parent);
        XMLElement("DESC",block.getBlockValue(),new TParm(),node);
        XMLElement("CODE",((ETextFormat) block).getCode(),new TParm(),node);
    }

    /**
     * ȡ������������
     * @param eTextFormat ETextFormat
     * @return TParm
     */
    private TParm getStyle(ETextFormat eTextFormat){
        TParm parm = getStyle((EFixed)eTextFormat);
        parm.setData("CODE_SYSTEM",eTextFormat.getData());
        return parm;
    }

    /**
     * ȡ�ú�����
     * @param eMacroroutine EMacroroutine
     * @return TParm
     */
    private TParm getStyle(EMacroroutine eMacroroutine){
        TParm parm = new TParm();
        String[] fixGroup = eMacroroutine.toString().split(",");
        for(int i = 0;i<fixGroup.length;i++){
            if(!fixGroup[i].startsWith("start")&&
               !fixGroup[i].startsWith("end"))
                continue;
            String name = fixGroup[i].substring(0,fixGroup[i].indexOf("="));
            String value = fixGroup[i].substring(fixGroup[i].indexOf("=") + 1,fixGroup[i].length());
            parm.setData(name,value);
        }
        String style = eMacroroutine.getStyle().toString();
        style = style.substring(style.indexOf("[") + 1,style.length() - 1);
        String[] fixGrStyoup =style.split(",");
        for(int i = 0;i<fixGrStyoup.length;i++){
            String name = fixGrStyoup[i].substring(0,fixGrStyoup[i].indexOf("="));
            String value = fixGrStyoup[i].substring(fixGrStyoup[i].indexOf("=") + 1,fixGrStyoup[i].length());
            parm.setData(name,value);
        }
        parm.setData("TYPE",13);
        parm.setData("X",eMacroroutine.getX());
        parm.setData("Y",eMacroroutine.getPanel().getY() + eMacroroutine.getY());
        parm.setData("WIDTH",eMacroroutine.getWidth());
        parm.setData("HEIGHT",eMacroroutine.getHeight());
        return parm;
    }

    /**
     * ȡ�ù̶��ı�����
     * @param fix EFixed
     * @return TParm
     */
    private TParm getStyle(EFixed fix){
        TParm parm = new TParm();
        String[] fixGroup = fix.toString().split(",");
        for(int i = 0;i<fixGroup.length;i++){
            if(!fixGroup[i].startsWith("start")&&
               !fixGroup[i].startsWith("end"))
                continue;
            String name = fixGroup[i].substring(0,fixGroup[i].indexOf("="));
            String value = fixGroup[i].substring(fixGroup[i].indexOf("=") + 1,fixGroup[i].length());
            parm.setData(name,value);
        }
        String style = fix.getStyle().toString();
        style = style.substring(style.indexOf("[") + 1,style.length() - 1);
        String[] fixGrStyoup =style.split(",");
        for(int i = 0;i<fixGrStyoup.length;i++){
            String name = fixGrStyoup[i].substring(0,fixGrStyoup[i].indexOf("=") );
            String value = fixGrStyoup[i].substring(fixGrStyoup[i].indexOf("=") + 1,fixGrStyoup[i].length());
            parm.setData(name,value);

        }
        parm.setData("TYPE",fix.getObjectType());
        parm.setData("X",fix.getX());
        parm.setData("Y",fix.getPanel().getY() + fix.getY());
        return parm;
    }

    /**
     * ȡ��ҳ��
     * @return int
     */
    private int getPageCount(){
        return getPageManager().getComponentList().size();
    }

    /**
     * ȡ��ҳ������
     * @param pageIndex int
     * @return int
     */
    private int getPagePanelCount(int pageIndex){
        return getEPage(pageIndex).getComponentList().size();
    }

    /**
     * ȡ��ҳ���Ԫ�ظ���
     * @param pageIndex int
     * @param panelIndex int
     * @return int
     */
    private int getPagePanelBlockCount(int pageIndex,int panelIndex){
        return getEPanel(pageIndex,panelIndex).getComponentList().size();
    }

    /**
     * �ж��Ƿ��Ǳ��
     * @param pageIndex int
     * @param panelIndex int
     * @param blockIndex int
     * @return boolean
     */
    private boolean isTable(int pageIndex ,int panelIndex,int blockIndex){
        IBlock block = getIBlock(pageIndex, panelIndex, blockIndex);
        if(block == null)
            return false;
        return(block.getObjectType() == EComponent.TABLE_TYPE);
    }

    /**
     * �ĵ��������
     * @param pageIndex int
     * @param panelIndex int
     * @param blockIndex int
     * @return int
     */
    private int getTableRowCount(int pageIndex ,int panelIndex,int blockIndex){
        if(!isTable(pageIndex,panelIndex,blockIndex))
            return 0;
        return ((ETable)getIBlock(pageIndex,panelIndex,blockIndex)).
                getComponentList().size();
    }

    /**
     * �õ��������
     * @param pageIndex int
     * @param panelIndex int
     * @param blockIndex int
     * @param row int
     * @return int
     */
    private int getTableRowColumnCount(int pageIndex ,int panelIndex,
                                       int blockIndex,int row){
        if(!isTable(pageIndex,panelIndex,blockIndex))
            return 0;
        return ((ETable)getIBlock(pageIndex,panelIndex,blockIndex)).get(row).
                getComponentList().size();
    }

    /**
     * �õ�������������
     * @param pageIndex int
     * @param panelIndex int
     * @param blockIndex int
     * @param row int
     * @param column int
     * @return int
     */
    private int getTableRowColumnPanelCount(int pageIndex ,int panelIndex,
                                            int blockIndex,int row, int column){
        if(!isTable(pageIndex,panelIndex,blockIndex))
            return 0;
        return ((ETable)getIBlock(pageIndex,panelIndex,blockIndex)).get(row).
                get(column).getComponentList().size();
    }

    /**
     * �õ�����������Ԫ�ظ���
     * @param pageIndex int
     * @param panelIndex int
     * @param blockIndex int
     * @param row int
     * @param column int
     * @param tpanelIndex int
     * @return int
     */
    private int getTableRowColumnPanelBlockCount(int pageIndex ,int panelIndex,
                                                 int blockIndex, int row,
                                                 int column,int tpanelIndex){
        if(!isTable(pageIndex,panelIndex,blockIndex))
            return 0;
        return ((ETable)getIBlock(pageIndex,panelIndex,blockIndex)).get(row).
                get(column).get(tpanelIndex).getComponentList().size();
    }

    /**
     * �õ�Ҳ������
     * @return MPage
     */
    private MPage getPageManager(){
        return word.getPM().getPageManager();
    }
    /**
     * �ĵ�ҳ
     * @param pageIndex int
     * @return EPage
     */
    private EPage getEPage(int pageIndex){
        return getPageManager().get(pageIndex);
    }
    /**
     * �õ������
     * @param pageIndex int
     * @param panelIndex int
     * @param blockIndex int
     * @param row int
     * @param column int
     * @return ETD
     */
    private ETD getETD(int pageIndex,int panelIndex,int blockIndex ,int row, int column){
        return ((ETable)getIBlock(pageIndex,panelIndex,blockIndex)).get(row).
                get(column);
    }

    /**
     * �õ������
     * @param pageIndex int
     * @param panelIndex int
     * @param blockIndex int
     * @param row int
     * @return ETR
     */
    private ETR getETR(int pageIndex,int panelIndex,int blockIndex ,int row){
        return ((ETable)getIBlock(pageIndex,panelIndex,blockIndex)).get(row);
    }
    /**
     * �õ����
     * @param pageIndex int
     * @param panelIndex int
     * @return EPanel
     */
    private EPanel getEPanel(int pageIndex, int panelIndex){
        return getEPage(pageIndex).get(panelIndex);
    }

    /**
     * �õ����
     * @param pageIndex int
     * @param panelIndex int
     * @param blockIndex int
     * @return ETable
     */
    private ETable getETable(int pageIndex, int panelIndex,int blockIndex){
        return (ETable)getIBlock(pageIndex,panelIndex,blockIndex);
    }

    /**
     * �õ�����Ԫ��
     * @param pageIndex int
     * @param panelIndex int
     * @param blockIndex int
     * @return IBlock
     */
    private IBlock getIBlock(int pageIndex, int panelIndex,int blockIndex){
        return getEPanel(pageIndex,panelIndex).get(blockIndex);
    }

    /**
     * �õ����Ԫ��
     * @param pageIndex int
     * @param panelIndex int
     * @param blockIndex int
     * @param row int
     * @param column int
     * @param tpanelIndex int
     * @param tBlockIndex int
     * @return IBlock
     */
    private IBlock getTableBlock(int pageIndex ,int panelIndex,
                                 int blockIndex,int row, int column,
                                 int tpanelIndex,int tBlockIndex){
        if(!isTable(pageIndex,panelIndex,blockIndex))
            return null;
        return ((ETable)getIBlock(pageIndex,panelIndex,blockIndex)).get(row).
                get(column).get(tpanelIndex).get(tBlockIndex);
    }

    /**
     * XML�ļ����ڵ�
     * @return Node
     */
    private Node XMLRoot(){
        Element element = XMLDoc.createElement("JAVAHIS-EMR");
        XMLDoc.appendChild(element);
        return element;
    }

    /**
     * ����CDA���ڵ�
     */
    private void XMLRootCDA(){
        ProcessingInstruction processingInstruction = XMLDocCDA.createProcessingInstruction("xml-stylesheet", "type=\"text/xsl\" href=\"doc1_20110607.xsl\"");
        XMLDocCDA.appendChild(processingInstruction);
        Element element = XMLDocCDA.createElement(CDA_INDEX);
        XMLDocCDA.appendChild(element);
    }

    /**
     * ���CDA�ڵ�
     * @param name String
     * @param groupName String
     * @param value String
     */
    private void appendNodeCDA(String name,String groupName,String value){
        TParm parm = getDataInfo(name,groupName);
//        System.out.println("���CDA�ڵ�---------------"+parm);
        if(parm == null)
            return;
        /*Node node = getSectionNode(parm,groupName);*/
        Node node = XMLDocCDA.getElementsByTagName(CDA_INDEX).item(0);
        Element elementC = XMLDocCDA.createElement(name);
        CDATASection cDATASection = XMLDocCDA.createCDATASection(value);
        elementC.appendChild(cDATASection);
        node.appendChild(XMLDocCDA.createComment(parm.getValue("DATA_DESC",0)));
        node.appendChild(elementC);
    }

    /**
     * �ҳ������ڵ�
     * @param parm TParm
     * @param sectionName String
     * @return Node
     */
    private Node getSectionNode(TParm parm,String sectionName){
        if(parm == null)
            return null;
        Node root = XMLDocCDA.getElementsByTagName(CDA_INDEX).item(0);
        NodeList nodeList = root.getChildNodes();
        int size = nodeList.getLength();
        for(int i = 0;i < size;i++){
            if(!nodeList.item(i).getNodeName().equals(sectionName))
                continue;
            return nodeList.item(i);
        }
        Node node = XMLDocCDA.createElement(sectionName);
        root.appendChild(XMLDocCDA.createComment(parm.getValue("GROUP_DESC",0)));
        root.appendChild(node);
        return node;
    }

    /**
     * �ĵ���������
     * @param dataCode String
     * @param groupCode String
     * @return TParm
     */
    private TParm getDataInfo(String dataCode,String groupCode){
        String SQL = " SELECT GROUP_DESC,DATA_DESC "+
                     " FROM EMR_CLINICAL_DATAGROUP "+
                     " WHERE DATA_CODE = '"+dataCode+"' "+
                     " AND   GROUP_CODE = '"+groupCode+"' ";
//        System.out.println("---------��������--------"+SQL);
        TParm parm = new TParm(TJDODBTool.getInstance().select(SQL));
        if(parm.getErrCode() < 0)
            return null;
        if(parm.getCount() <= 0)
            return null;
        return parm;
    }

    /**
     * ����CDA���ֵ
     * @param block IBlock
     */
    private void XMLElementCDA(IBlock block){
//        System.out.println("======XMLElementCDA ������====="+block.getObjectType());
        //ͼ��Ԫ�ش���;
        if(block.getObjectType()==EComponent.PIC_TYPE){
            //���ڵ�
             //Node node = XMLDocCDA.getElementsByTagName(CDA_INDEX).item(0);
             this.setPicInfo((EPic) block);
             return;
        }

        if(!(block instanceof EFixed))
            return;
//        System.out.println("======XMLElementCDA1=====");
        if(!((EFixed)block).isIsDataElements())
            return;
//        System.out.println("======XMLElementCDA2=====");
        switch (block.getObjectType()) {
        case EComponent.CAPTURE_TYPE:
            if (((ECapture) block).getCaptureType() == 1)
                return;
            appendNodeCDA(((EFixed) block).getName(),
                          ((EFixed) block).getGroupName(),
                          ((ECapture) block).getValue());
            return;
        case EComponent.CHECK_BOX_CHOOSE_TYPE:
            appendNodeCDA(((EFixed) block).getName(),
                          ((EFixed) block).getGroupName(),
                          ((ECheckBoxChoose) block).isChecked() ? "Y" : "N");
            return;
        case EComponent.TEXTFORMAT_TYPE:
            appendNodeCDA(((ETextFormat) block).getName(),
                          ((EFixed) block).getGroupName(),
                          ((ETextFormat) block).getCode());
            return;
        case EComponent.MICRO_FIELD_TYPE:
            appendNodeCDA(((EMicroField) block).getName(),
                          ((EFixed) block).getGroupName(),
                          (((EMicroField) block).getCode() == null ||
                           ((EMicroField) block).getCode().length() == 0)?
                          ((EMicroField) block).getText():
                          ((EMicroField) block).getCode());
            return;

        default:
            appendNodeCDA(((EFixed) block).getName(),
                          ((EFixed) block).getGroupName(),
                          block.getBlockValue());
            return;
        }
    }

    /**
     * д��XML��ֵ�ڵ�
     * @param name String
     * @param attributes TParm
     * @param parent Node
     * @return Node
     */
    private Node XMLElement(String name,TParm attributes,Node parent){
        try{
            Element element = XMLDoc.createElement(name);
            parent.appendChild(element);
            String names[] = attributes.getNames();
            for (int i = 0; i < names.length; i++)
                element.setAttribute(names[i], attributes.getValue(names[i]));
            return element;
        }catch(Exception ex){
            ex.printStackTrace();
            out("name = "+name);
            return null;
        }
    }

    /**
     * д��XML��ֵ�ڵ�
     * @param name String
     * @param value String
     * @param attributes TParm
     * @param parent Node
     * @return Node
     */
    private Node XMLElement(String name,String value,TParm attributes,Node parent){
        try{
           //System.out.println("======name======"+name);
            Element element = XMLDoc.createElement(name);
            CDATASection cDATASection = XMLDoc.createCDATASection(value);
            element.appendChild(cDATASection);
            parent.appendChild(element);
            String names[] = attributes.getNames();
            for (int i = 0; i < names.length; i++)
                element.setAttribute(names[i], attributes.getValue(names[i]));
            return element;
        }catch(Exception ex){
            ex.printStackTrace();
            out("name = "+name + ";value = "+value);
            return null;
        }
    }

    /**
     * ����XML�ļ�
     * @return String
     */
    private String toXMLString() {
        StringWriter pw=new StringWriter();
        try{
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING,ENCODING);
            transformer.setOutputProperty(OutputKeys.INDENT,"yes");
            DOMSource source = new DOMSource(XMLDoc);
            StreamResult result = new StreamResult(pw);
            transformer.transform(source,result);
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        return pw.toString();
    }

    /**
     * ����XMLCDA�ļ�
     * @return String
     */
    private String toXMLStringCDA() {
        StringWriter pw = new StringWriter();
        try{
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING,ENCODING);
            transformer.setOutputProperty(OutputKeys.INDENT,"yes");
            DOMSource source = new DOMSource(XMLDocCDA);
            StreamResult result = new StreamResult(pw);
            transformer.transform(source,result);
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        return pw.toString();
    }

    /**
     * LOG
     * @param message String
     */
    private void out(String message){
        System.out.println(message);
    }

}
