package jdo.sys;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TStrike;
import com.dongyang.ui.TTreeNode;
import com.dongyang.util.StringTool;

/**
 *
 * <p>Title: ��Ź�����𹤾���</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author lzk 2008.12.19
 * @version 1.0
 */
public class SYSRuleTool extends TStrike{
    /**
     * �����
     */
    public static int CLASSIFY_MAX = 5;
    /**
     * ��Ź������
     */
    private String ruleType;
    /**
     * �Ƿ����
     */
    private boolean isLoad;
    /**
     * ����
     */
    private int tot;
    /**
     * ����
     */
    private int classify[] = new int[CLASSIFY_MAX];
    /**
     * ʹ�ü���
     */
    private int classifyCurrent = CLASSIFY_MAX;
    /**
     * ��ˮ�������
     */
    private int serial;
    /**
     * ģʽ A �Զ����� B �ֶ�����
     */
    private String mode;
    /**
     * ���ñ����ý�������
     */
    private static final String EXA_ROOT_SQL=
    	"SELECT CATEGORY_CODE,CATEGORY_CHN_DESC FROM SYS_CATEGORY B,SYS_RULE A " +
    		" WHERE A.RULE_TYPE=B.RULE_TYPE" +
    		" AND A.RULE_TYPE='EXM_RULE'" +
    		" AND length(B.CATEGORY_CODE)=(select A.CLASSIFY1 FROM SYS_RULE A where A.RULE_TYPE='EXM_RULE') ";
    /**
     * ���ñ����ý����з���
     */
    private static final String EXA_MID_SQL=
    	"SELECT CATEGORY_CODE,CATEGORY_CHN_DESC FROM SYS_CATEGORY B,SYS_RULE A " +
	" WHERE A.RULE_TYPE=B.RULE_TYPE" +
	" AND A.RULE_TYPE='EXM_RULE'" +
	" AND (length(B.CATEGORY_CODE)=(select (A.CLASSIFY2+A.CLASSIFY1) FROM SYS_RULE A where A.RULE_TYPE='EXM_RULE'))" +
	" AND B.CATEGORY_CODE like ";
    /**
     * ���ñ����ý���С����
     */
    private static final String EXA_DETAIL_SQL=
    	"SELECT CATEGORY_CODE,CATEGORY_CHN_DESC FROM SYS_CATEGORY B,SYS_RULE A " +
	" WHERE A.RULE_TYPE=B.RULE_TYPE" +
	" AND A.RULE_TYPE='EXM_RULE'" +
	" AND length(B.CATEGORY_CODE)=(select (A.CLASSIFY2+A.CLASSIFY1+A.CLASSIFY3) FROM SYS_RULE A where A.RULE_TYPE='EXM_RULE')"+
	" AND B.CATEGORY_CODE like ";
    /**
     * ������
     * @param ruleType String
     */
    public SYSRuleTool(String ruleType)
    {
        //���ñ�Ź������
        setRuleType(ruleType);
        //��������
        load();
    }
    /**
     * ���ñ�Ź������
     * @param ruleType String
     */
    public void setRuleType(String ruleType)
    {
        this.ruleType = ruleType;
    }
    /**
     * �õ���Ź������
     * @return String
     */
    public String getRuleType()
    {
        return ruleType;
    }
    /**
     * ��������
     */
    public void load()
    {
        isLoad = false;
        TDataStore dataStore = new TDataStore();
        if(!dataStore.setSQL("SELECT * FROM SYS_RULE WHERE RULE_TYPE = '" + getRuleType() + "'"))
        {
            err(dataStore.getErrText());
            return;
        }
        if(dataStore.retrieve() == -1)
        {
            err(dataStore.getErrText());
            return;
        }
        if(dataStore.rowCount() != 1)
        {
            err("û���ҵ�" + getRuleType() + "��Ź������");
            return;
        }
        //����������
        tot = dataStore.getItemInt(0,"TOT_NUMBER");
        //������ˮ�������
        serial = dataStore.getItemInt(0,"SERIAL_NUMBER");
        //���ñ��뷽ʽ
        mode = dataStore.getItemString(0,"CODE_MODE");
        //���ø�������
        for(int i = 0;i < CLASSIFY_MAX;i++)
            classify[i] = dataStore.getItemInt(0,"CLASSIFY" + (i + 1));
        //������Ч����
        classifyCurrent = CLASSIFY_MAX;
        for(int i = 0;i < CLASSIFY_MAX;i++)
            if(classify[i] == 0)
            {
                classifyCurrent = i;
                break;
            }
        isLoad = true;
    }
    /**
     * �Ƿ��Ѿ�����
     * @return boolean true �Ѿ��������� false û�м�������
     */
    public boolean isLoad()
    {
        return isLoad;
    }
    /**
     * �õ�������
     * @return int
     */
    public int getTot()
    {
        return tot;
    }
    /**
     * �õ�ĳ������
     * @param i int
     * @return int
     */
    public int getClassify(int i)
    {
        if(i < 0 || i >= CLASSIFY_MAX)
            return -1;
        return classify[i];
    }
    /**
     * �õ���Ч����
     * @return int
     */
    public int getClassifyCurrent()
    {
        return classifyCurrent;
    }
    /**
     * �õ���ˮ�������
     * @return int
     */
    public int getSerial()
    {
        return serial;
    }
    /**
     * �õ����뷽ʽ
     * @return String A �Զ����� B �ֶ�����
     */
    public String getMode()
    {
        return mode;
    }
    /**
     * �õ��������
     * @param s String
     * @return int
     */
    public int getNumberClass(String s)
    {
        int length = s.length();
        for(int i = 0;i < getClassifyCurrent();i++)
        {
            length -= getClassify(i);
            if(length == 0)
                return i + 1;
        }
        if(length == getSerial())
            return getClassifyCurrent() + 1;
        return -1;
    }
    /**
     * �õ���������
     * @param classifyIndex int
     * @return int
     */
    public int getClassNumber(int classifyIndex)
    {
        if(classifyIndex <= 0 || classifyIndex > getClassifyCurrent() + 1)
            return -1;
        if(classifyIndex == getClassifyCurrent() + 1)
            return getTot();
        int c = 0;
        for(int i = 0;i < classifyIndex;i++)
            c += getClassify(i);
        return c;
    }
    /**
     * �õ��ϲ����
     * @param s String
     * @return String
     */
    public String getNumberParent(String s)
    {
        if(s == null || s.length() == 0)
            return "";
        int classifyIndex = getNumberClass(s) - 1;
        if(classifyIndex <= 0)
            return "";
        int length = getClassNumber(classifyIndex);
        return s.substring(0,length);
    }
    /**
     * �ֽ���Ϊ����
     * @param s String
     * @return String[]
     */
    public String[] getNumberArray(String s)
    {
        ArrayList list = new ArrayList();
        if(s == null || s.length() == 0)
            return new String[]{};
        int classifyIndex = getNumberClass(s);
        if(classifyIndex == -1)
            return new String[]{};
        boolean hasSerial = false;
        if(classifyIndex == getClassifyCurrent() + 1)
        {
            classifyIndex--;
            hasSerial = true;
        }
        for(int i = 0;i < classifyIndex;i++)
        {
            list.add(s.substring(0,getClassify(i)));
            s = s.substring(getClassify(i));
        }
        if(hasSerial)
            list.add(s);
        return (String[])list.toArray(new String[]{});
    }
    public TTreeNode[] getTreeNode(TDataStore dataStore,String id,String name,String type,String seq)
    {
        if(dataStore == null)
            return null;
        dataStore.setSort(id);
        dataStore.sort();
        int count = dataStore.rowCount();
        Map map = new HashMap();
        ArrayList root = new ArrayList();
        for(int i = 0;i < count;i ++)
        {
            String code = dataStore.getItemString(i,id);
            String text = dataStore.getItemString(i,name);
            int index = dataStore.getItemInt(i,seq);
            TTreeNode node = new TTreeNode(text,type);
            node.setID(code);
            node.setSeq(index);
            int classifyIndex = getNumberClass(code);
            if(classifyIndex == 1)
            {
                root.add(node);
                map.put(code,node);
                continue;
            }
            String parentCode = getNumberParent(code);
            TTreeNode parentNode = (TTreeNode)map.get(parentCode);
            if(parentNode == null)
            {
                root.add(node);
                map.put(code,node);
                continue;
            }
            parentNode.addSeq(node);
            map.put(code,node);
        }
        return (TTreeNode[])root.toArray(new TTreeNode[]{});
    }
    public TTreeNode[] getTreeNode(TDataStore dataStore,String id,String name,String type,String seq,String engName)
    {
        if(dataStore == null)
            return null;
        dataStore.setSort(id);
        dataStore.sort();
        int count = dataStore.rowCount();
        Map map = new HashMap();
        ArrayList root = new ArrayList();
        for(int i = 0;i < count;i ++)
        {
            String code = dataStore.getItemString(i,id);
            String text = dataStore.getItemString(i,name);
            String enName = dataStore.getItemString(i,engName);
            int index = dataStore.getItemInt(i,seq);
            TTreeNode node = new TTreeNode(text,type);
            node.setID(code);
            node.setEnText(enName);
            node.setSeq(index);
            int classifyIndex = getNumberClass(code);
            if(classifyIndex == 1)
            {
                root.add(node);
                map.put(code,node);
                continue;
            }
            String parentCode = getNumberParent(code);
            TTreeNode parentNode = (TTreeNode)map.get(parentCode);
            if(parentNode == null)
            {
                root.add(node);
                map.put(code,node);
                continue;
            }
            parentNode.addSeq(node);
            map.put(code,node);
        }
        return (TTreeNode[])root.toArray(new TTreeNode[]{});
    }

    /**
     * �õ��µĺ���
     * @param oldCode String �ɺ���
     * @param classifyIndex int ����
     * @return String
     */
    public String getNewCode(String oldCode, int classifyIndex) {
        int length = classifyIndex <= getClassifyCurrent() ?
            getClassify(classifyIndex - 1) : getSerial();
        if (oldCode == null || oldCode.length() == 0)
            oldCode = StringTool.fill("0", length);
        if (classifyIndex > 1)
            oldCode = oldCode.substring(oldCode.length() - length);
        Integer newCode = (Integer) (Integer.parseInt(oldCode) + 1);
        String code = newCode.toString();
        for (int i = 0; i < oldCode.length() - newCode.toString().length(); i++) {
            code = "0" + code;
        }
        return code;
    }

    /**
     * �õ��µĺ���(����Ϊ��ĸ)
     * @param oldCode String �ɺ���
     * @param classifyIndex int ����
     * @return String
     */
    public String getNewCodeByString( String parentID, String oldCode, int classifyIndex) {
        int length = classifyIndex <= getClassifyCurrent() ?
            getClassify(classifyIndex - 1) : getSerial();
        if (oldCode == null || oldCode.length() == 0)
            oldCode = StringTool.fill("0", length);
        if (classifyIndex > 1)
            oldCode = oldCode.substring(oldCode.length() - length);
        return StringTool.stringADD(oldCode);
    }
    
	/**
	 * �õ��µĺ���(����Ϊ��ĸ)
	 * 
	 * @param parentID
	 * @param dataStore
	 * @param classifyIndex
	 * @return
	 */
	public String getNewCode(String parentID, TDataStore dataStore, int classifyIndex) {
		int length = getClassify(classifyIndex - 1);
		String s = StringTool.fill("0", length);
		while (this.verify(dataStore, parentID + s)) {
			s = StringTool.stringADD(s);
		}
		return s;
	}

	/**
	 * У���Ƿ��Ѵ��ڴ���
	 * 
	 * @param dataStore
	 * @param categoryCode
	 * @return
	 */
	private boolean verify(TDataStore dataStore, String categoryCode) {
		int count = dataStore.rowCount();
		for (int i = 0; i < count; i++) {
			String value = dataStore.getItemString(i, "CATEGORY_CODE");
			if (StringTool.compareTo(categoryCode, value) == 0)
				return true;
		}
		return false;
	}

    /**
     * ���ñ������
     * @return TParm
     */
    public static TParm getExaRoot(){
    	TParm result=new TParm(TJDODBTool.getInstance().select(EXA_ROOT_SQL));
    	return result;
    }
    /**
     * ���ñ��з���
     * @param t0 String ������code
     * @return
     */
    public static TParm getExaMid(String t0){
    	String sql=EXA_MID_SQL+"'"+t0+"%'";
    	TParm result=new TParm(TJDODBTool.getInstance().select(sql));
    	return result;
    }
    /**
     * ���ñ��з���
     * @param t0 String ������code
     * @return
     */
    public static TParm getExaDetail(String t0){
    	String sql=EXA_DETAIL_SQL+"'"+t0+"%'";
    	TParm result=new TParm(TJDODBTool.getInstance().select(sql));
    	return result;
    }
    /**
     * ��ʾ�ڴ�(������)
     */
    public void showDebug()
    {
        //System.out.println("isLoad=" + isLoad());
        //System.out.println("tot=" + tot);
        //System.out.println("classifyCurrent=" + classifyCurrent);
        //System.out.println("classify=" + StringTool.getString(classify));
        //System.out.println("serial=" + serial);
        //System.out.println("mode=" + mode);
    }

    public static void main(String args[])
    {
        com.dongyang.util.TDebug.initClient();
        SYSRuleTool tool = new SYSRuleTool("SYS_DEPT");
        tool.showDebug();
        //System.out.println(tool.getNumberClass("A0102"));
        //System.out.println(tool.getClassNumber(2));
        //System.out.println(tool.getNumberParent("A0102004"));
        //System.out.println(StringTool.getString(tool.getNumberArray("A0102004")));

        //System.out.println(tool.getNewCode("A01",2));


    }
}
