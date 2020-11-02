package com.javahis.ui.database;

import com.dongyang.control.TControl;
import com.dongyang.tui.text.MPage;
import com.dongyang.tui.text.div.MVList;
import com.dongyang.ui.TTable;
import com.dongyang.data.TParm;
import java.util.Vector;
import com.dongyang.tui.text.div.MV;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.tui.text.div.DIV;
import com.dongyang.util.TypeTool;
import com.dongyang.tui.text.div.VLine;
import com.dongyang.tui.text.div.VTable;
import com.dongyang.tui.text.div.VText;
import com.dongyang.tui.text.div.VPic;
import java.awt.Color;
import com.dongyang.tui.text.div.VBarCode;
import com.dongyang.tui.text.EComponent;
import com.dongyang.tui.text.EMacroroutineModel;
import com.dongyang.tui.text.EPic;
import com.dongyang.ui.TLabel;

/**
 *
 * <p>Title: ͼ����ƶԻ��������</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author lzk 2009.5.21
 * @version 1.0
 */
public class MvListDialogControl extends TControl{
    /**
     * ͼ�������
     */
    private MVList mvList;
    private TTable vlTable;
    private TTable divTable;
    /**
     * ��ʼ��
     */
    public void onInit()
    {
        mvList = (MVList)getParameter();
        vlTable = (TTable)getComponent("VL_TABLE");
        divTable = (TTable)getComponent("DIV_TABLE");
        //��ʼ��ͼ��Table
        initVLTable();
        vlTable.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
                                     "onCheckVL");
        divTable.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
                                     "onCheckDIV");

        EComponent com = mvList.getParent();
        if(!(com instanceof EPic))
            ((TLabel)getComponent("tLabel_4")).setVisible(false);

    }
    /**
     * ��ʼ��ͼ��Table
     */
    public void initVLTable()
    {
        TParm parm = new TParm();
        if(mvList.size() == 0)
        {
            parm.setData("ID",new Vector());
            parm.setData("SHOW",new Vector());
            parm.setData("NAME",new Vector());
            parm.setData("F",new Vector());
            parm.setCount(0);
        }else
        {
            for (int i = 0; i < mvList.size(); i++) {
                MV mv = mvList.get(i);
                parm.addData("ID",i);
                parm.addData("SHOW",mv.isVisible());
                parm.addData("NAME",mv.getName());
                parm.addData("F",mv.isForeground());
            }
            parm.setCount(mvList.size());
        }
        vlTable.setParmValue(parm);
    }
    /**
     * ���checkbox
     * @param obj Object
     */
    public void onCheckVL(Object obj)
    {
        TTable table=(TTable)obj;
        table.acceptText();
        int row = table.getSelectedRow();
        int column = table.getSelectedColumn();
        String columnName = table.getParmMap(column);
        boolean value = table.getParmValue().getBoolean(columnName,row);
        if("SHOW".equals(columnName))
            mvList.get(row).setVisible(value);
        else if("F".equals(columnName))
            mvList.get(row).setIsForeground(value);
        mvList.update();
    }
    /**
     * ���checkbox
     * @param obj Object
     */
    public void onCheckDIV(Object obj)
    {
        int mvrow = vlTable.getSelectedRow();
        TTable table=(TTable)obj;
        table.acceptText();
        int row = table.getSelectedRow();
        int column = table.getSelectedColumn();
        String columnName = table.getParmMap(column);
        boolean value = table.getParmValue().getBoolean(columnName,row);
        if("SHOW".equals(columnName))
            mvList.get(mvrow).get(row).setVisible(value);
        mvList.update();
        mvList.get(mvrow).get(row).propertyModify("VISIBLE");
    }
    /**
     * ���VLTable
     */
    public void onClickedVL()
    {
        int row = vlTable.getSelectedRow();
        MV mv = mvList.get(row);
        TParm parm = new TParm();
        if(mv.size() == 0)
        {
            parm.setData("ID",new Vector());
            parm.setData("SHOW",new Vector());
            parm.setData("NAME",new Vector());
            parm.setData("TYPE",new Vector());
            parm.setCount(0);
        }else
        {
            for (int i = 0; i < mv.size(); i++) {
                DIV div = mv.get(i);
                parm.addData("ID",i);
                parm.addData("SHOW",div.isVisible());
                parm.addData("NAME",div.getName());
                parm.addData("TYPE",div.getType());
            }
            parm.setCount(mv.size());
        }
        divTable.setParmValue(parm);
    }


    public void onVlDoubleClick(){
        int mvrow = vlTable.getSelectedRow();
        //this.messageBox("===mvrow===="+mvrow);
        MV mv = mvList.get(mvrow);
        //this.messageBox("name===="+mv.getName());
        TParm sendParm = new TParm();
        sendParm.setData("MV",mv);
        sendParm.setData("MV_LIST",mvList);
        //ȡ����;
        this.openDialog(
                      "%ROOT%\\config\\database\\MVTableDialog.x",sendParm);

        //mv.setName();
        //
        //mvList.update();

    }


    /**
     * ˫�����
     */
    public void onDivDoubleClick()
    {
       //this.messageBox("==com in==");
        int mvrow = vlTable.getSelectedRow();
        MV mv = mvList.get(mvrow);
        int row = divTable.getSelectedRow();
        DIV div = mv.get(row);
        div.openProperty();
    }
    /**
     * �޸Ĳ���
     * @param mv MV
     * @param div DIV
     * @param name String
     */
    public void modify(MV mv,DIV div,String name)
    {
        int row = vlTable.getSelectedRow();
        MV showmv = mvList.get(row);
        if(showmv != mv)
            return;
        int index = mv.indexOf(div);
        if(index == -1)
            return;
        if("NAME".equals(name))
        {
            divTable.setItem(index,"NAME",div.getName());
            return;
        }
        if("VISIBLE".equals(name))
        {
            divTable.setItem(index,"SHOW",div.isVisible());
            return;
        }
    }
    /**
     * �ر�
     * @return boolean
     */
    public boolean onClosing()
    {
        mvList.setPropertyWindow(null);
        return true;
    }
    /**
     * �½�ͼ��
     */
    public void onNewMV()
    {
        mvList.add();
        initVLTable();
    }
    /**
     * ɾ��ͼ��
     */
    public void onDeleteMV()
    {
        int row = vlTable.getSelectedRow();
        if(row < 0)
            return;
        MV showmv = mvList.get(row);
        if(messageBox("��ʾ��Ϣ","�Ƿ�ɾ��ͼ��" + showmv.getName() + "?",YES_NO_OPTION) == 1)
            return;
        mvList.remove(showmv);
        vlTable.removeRow(row);
        if(vlTable.getRowCount() > 0)
            vlTable.setSelectedRow(row >= vlTable.getRowCount()?vlTable.getRowCount() - 1:row);
        mvList.update();
        showmv.DC();
    }
    /**
     * �½�DIV
     */
    public void onNewDIV()
    {
        int row = vlTable.getSelectedRow();
        if(row < 0)
            return;
        MV mv = mvList.get(row);
        Object[] result = (Object[])openDialog("%ROOT%\\config\\database\\CreateDIVDialog.x",mv);
        if(result == null)
            return;
        switch(TypeTool.getInt(result[0]))
        {
            case 1://ֱ��
                VLine line = mv.addLine(100,100,200,100,new Color(0,0,0));
                line.setName(TypeTool.getString(result[1]));
                line.openProperty();
                break;
            case 2://���
                VTable table = mv.addTable(100,100,300,300);
                table.setName(TypeTool.getString(result[1]));
                table.openProperty();
                break;
            case 3://����
                VText text = mv.addText(100,100,new Color(0,0,0));
                text.setName(TypeTool.getString(result[1]));
                text.setText("����");
                text.openProperty();
                break;
            case 4://ͼƬ
                VPic pic = mv.addPic(100,100,300,300);
                pic.setName(TypeTool.getString(result[1]));
                pic.openProperty();
                break;
            case 5://����
                VBarCode barCode = mv.addBarCode(100,100);
                barCode.setName(TypeTool.getString(result[1]));
                barCode.openProperty();
                break;
        }
        onClickedVL();
    }
    /**
     * ɾ��DIV
     */
    public void onDeleteDIV()
    {
        int mvrow = vlTable.getSelectedRow();
        if(mvrow == -1)
            return;
        MV mv = mvList.get(mvrow);
        int row = divTable.getSelectedRow();
        if(row == -1)
            return;
        DIV div = mv.get(row);
        if(messageBox("��ʾ��Ϣ","�Ƿ�ɾ�����" + div.getName() + "?",YES_NO_OPTION) == 1)
            return;
        mv.remove(div);
        divTable.removeRow(row);
        if(divTable.getRowCount() > 0)
            divTable.setSelectedRow(row >= divTable.getRowCount()?divTable.getRowCount() - 1:row);
        mvList.update();
        div.DC();
    }
    /**
     * �ƶ�������ͼ��
     */
    public void onMoveBAK()
    {
        if(messageBox("��ʾ��Ϣ","�Ƿ�ȷ���ƶ�������ͼ��?",YES_NO_OPTION) != 0)
            return;
        EComponent com = mvList.getParent();
        if(!(com instanceof EPic))
            return;
        EPic pic = (EPic)com;
        MVList pkList = pic.getPM().getPageManager().getMVList();
        pkList.add();
        MV mv = pkList.get(pkList.size() - 1);
        mv.setName("Pic");
        VTable table = mv.addTable(pic.getX() + pic.getPanel().getX(),pic.getY() + pic.getPanel().getY(),pic.getWidth(),pic.getHeight());

        MV list = mvList.get(0);
        for(int i = 0;i < list.size();i++)
        {
            DIV div = list.get(i);
            div.setParent(table.getMV());
            table.getMV().add(div);
        }
        pkList.update();
        pic.createMVList();
        messageBox("�ƶ��ɹ�!");
        closeWindow();
    }
}
