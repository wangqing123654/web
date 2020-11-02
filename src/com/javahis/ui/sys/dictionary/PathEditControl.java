package com.javahis.ui.sys.dictionary;

import java.util.Vector;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import jdo.sys.DictionaryTool;
import com.dongyang.util.StringTool;
import com.dongyang.util.TMessage;
import com.dongyang.util.TypeTool;

import jdo.sys.Operator;

import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TTableEvent;

public class PathEditControl extends TControl{
    private static final String TABLE = "Table";
    /**
     * �ϼ�����
     */
    private String groupID;
    /**
     * �ϼ����
     */
    private String parentID;
    /**
     * ��ǰ����
     */
    private String action;
    public void initRoot()
    {
        String s = (String)getParameter();
        if(s == null)
            s = "ROOT|GROUP";
        String s1[] = StringTool.parseLine(s,"|");
        groupID = s1[0];
        parentID = s1[1];
    }
    public void onInit()
    {
        super.onInit();
        initRoot();
        //��ʼ������
        initData();
        callFunction("UI|Table|addEventListener",TABLE + "->"+TTableEvent.CLICKED,this,"onTableClicked");
    }
    public void onInitReset()
    {
        initRoot();
        //��ʼ������
        initData();
    }
    /**
     * ��ʼ������
     */
    public void initData()
    {
        String name = getDicTool().getName(groupID,parentID);
        callFunction("UI|GROUP|setText",name);
        String title = (String)callFunction("UI|getTitle");
        callFunction("UI|setTitle",title + "-" + name);
        TParm result = getDicTool().getListAll(parentID);
        callFunction("UI|" + TABLE + "|setParmValue",result);
        onClear();
        int seq = 0;
        if (result.existData("SEQ")) {
			Vector vct = result.getVectorValue("SEQ");
			for (int i = 0; i < vct.size(); i++) {
				int a = TypeTool.getInt(vct.get(i));
				if (a > seq)
					seq = a;
			}
			this.setValue("SEQ", seq + 1);
		}
    }
    /**
     * TABLE�����¼�
     * @param row
     */
    public void onTableClicked(int row)
    {
        if(row < 0)
            return;
        callFunction("UI|ID|setEnabled",false);
        callFunction("UI|delete|setEnabled",true);
        action = "EDIT";
    }
    /**
     * �õ��ֵ乤����
     * @return DictionaryTool
     */
    public DictionaryTool getDicTool()
    {
        return DictionaryTool.getInstance();
    }
    /**
     * ���
     */
    public void onClear()
    {
        clearValue("ID;NAME;ENG_DESC;TYPE;STATE;DESCRIPTION;PY1;PY2;SEQ;PARENT_ID;DATA");
        callFunction("UI|" + TABLE + "|clearSelection");
        callFunction("UI|ID|setEnabled",true);
        callFunction("UI|save|setEnabled",true);
        callFunction("UI|delete|setEnabled",false);
        action = "INSERT";
        TParm result = getDicTool().getListAll(parentID);
        int seq = 0;
        if (result.existData("SEQ")) {
			Vector vct = result.getVectorValue("SEQ");
			for (int i = 0; i < vct.size(); i++) {
				int a = TypeTool.getInt(vct.get(i));
				if (a > seq)
					seq = a;
			}
			this.setValue("SEQ", seq + 1);
		}
    }

    /**
     * ����
     */
    public void onSave() {
        TParm parm = new TParm();
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        if ("EDIT".equals(action)) {
            if (!emptyTextCheck("NAME"))
                return;
            callFunction("UI|" + TABLE + "|setModuleParmUpdate", parm);
            if (! (Boolean) callFunction("UI|" + TABLE + "|onUpdate")) {
                messageBox("E0001");
                return;
            }
            String id = getText("ID");
            String name = getText("NAME");
            String type = getText("TYPE");
            callFunction("UI|callEvent","UPDATE_ACTION",new Object[]{groupID,parentID,id,name,type},
                         new String[]{"String","String","String","String","String"});
            messageBox("P0001");
        }
        else {
            if (parentID.length() == 0)
                return;
            if (!emptyTextCheck("ID,NAME"))
                return;
            parm.setData("GROUP_ID", parentID);
            callFunction("UI|" + TABLE + "|setModuleParmInsert", parm);
            if (! (Boolean) callFunction("UI|" + TABLE + "|onInsert")) {
                messageBox("E0002");
                return;
            }
            String id = getText("ID");
            String name = getText("NAME");
            String type = getText("TYPE");
            callFunction("UI|callEvent","INSERT_ACTION",new Object[]{groupID,parentID,id,name,type},
                         new String[]{"String","String","String","String","String"});
            messageBox("P0002");
            callFunction("UI|ID|setEnabled", false);
            callFunction("UI|delete|setEnabled",true);
            action = "EDIT";
        }
        onClear();
    }
    /**
     * ɾ��
     */
    public void onDelete()
    {
        int row = (Integer)callFunction("UI|Table|getSelectedRow");
        if(row < 0)
            return;
        String id = getText("ID");
        if(!(Boolean)callFunction("UI|" + TABLE + "|onDelete"))
        {
            messageBox("E0003");
            return;
        }
        callFunction("UI|callEvent","DELETE_ACTION",new Object[]{groupID,parentID,id},
                     new String[]{"String","String","String"});
        messageBox("P0003");
        onClear();
    }
    
    /**
     * ���ƻس��¼�
     */
    public void onUserNameAction(){
    	String userName = getValueString("NAME");
    	String py = TMessage.getPy(userName);
    	setValue("PY1", py);
    	((TTextField) getComponent("PY1")).grabFocus();
    }
    
    /**
     * ��ѯ
     */
    public void onQuery(){
    	String id = getValueString("ID");
    	TParm result = new TParm();
    	if (id.length() == 0)
    		result = getDicTool().getListAll(parentID);
    	else
    		result = getDicTool().query(parentID, id+"%");
    	callFunction("UI|" + TABLE + "|setParmValue",result);
    }
}
