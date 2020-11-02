package jdo.ins;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TStrike;
import com.dongyang.ui.TTreeNode;
/**
 * <p>
 * Title: ����ҽ����Ŀ�ֵ�
 * </p>
 * 
 * <p>
 * Description:����ҽ����Ŀ�ֵ��Ź�����𹤾���
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * 
 * <p>
 * Company: BlueCore
 * </p>
 * 
 * @author pangben 2011-12-09
 * @version 2.0
 */
public class INSSYSRuleTXTool extends TStrike{
	/**
	 * ʹ�ü���
	 */
	private int classifyCurrent = CLASSIFY_MAX;
	/**
	 * �����
	 */
	private static int CLASSIFY_MAX = 5;

	/**
	 * ����
	 */
	private int classify[] = new int[CLASSIFY_MAX];
	int serial;// ���ø�������

	int tot;// ����������

	/**
	 * �õ�������
	 * 
	 * @return int
	 */
	public int getTot() {
		return tot;
	}

	/**
	 * �õ���ˮ�������
	 * 
	 * @return int
	 */
	public int getSerial() {
		return serial;
	}
    /**
     * �Ƿ����
     */
    private boolean isLoad;
    /**
     * ��Ź������
     */
    private String ruleType;
    /**
     * ������
     * @param ruleType String
     */
    public INSSYSRuleTXTool(String ruleType)
    {
        //���ñ�Ź������
        setRuleType(ruleType);
        //��������
        load();
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
	 * �õ���Ч����
	 * 
	 * @return int
	 */
	public int getClassifyCurrent() {
		return classifyCurrent;
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
	 * ����Ŀǰ�������õ���Ŀ�б�
	 * 
	 * @return String
	 */
	public TTreeNode[] getTreeNode(TDataStore dataStore,TParm sysFeeParm, String id,
			String name, String type, String seq) {
		if (dataStore == null)
			return null;
		dataStore.setSort(id);
		dataStore.sort();
		int count = dataStore.rowCount();
		Map map = new HashMap();
		ArrayList root = new ArrayList();
		for (int i = 0; i < count; i++) {
			String code = dataStore.getItemString(i, id);// ��÷������
			String text = dataStore.getItemString(i, name);// ��÷�������
			int index = dataStore.getItemInt(i, seq);
			TTreeNode node = new TTreeNode(text, "Root");// ���һ���ڵ�
			node.setID(code);
			node.setSeq(index);
			int classifyIndex = getNumberClass(code);
			if (classifyIndex == 1) {
				root.add(node);
				map.put(code, node);
				continue;
			}
			String parentCode = getNumberParent(code);// �õ���ǰ�ڵ��ID����
			TTreeNode parentNode = (TTreeNode) map.get(parentCode);// �鿴�˽ڵ��Ƿ����
			if (parentNode == null) {
				root.add(node);
				map.put(code, node);
				continue;
			}
			parentNode.addSeq(node);// ���ڽڵ㽫�˽ڵ�ŵ����ڵ���
			map.put(code, node);// �ۼƴ˽ڵ�����
			if (classifyIndex == classifyCurrent) {//��С�ڵ�
				for (int j = 0; j < sysFeeParm.getCount(); j++) {// ����ҽ��
					int indexSql = 1;
					if (sysFeeParm.getValue("ORDER_CODE", j).contains(code)) {// ��ѯ��ǰҽ�������Ƿ�����˽ڵ�CODEֵ
						TTreeNode nodeTemp = new TTreeNode(sysFeeParm.getValue(
								"ORDER_DESC", j),type);// �����ӽڵ�
						nodeTemp.setID(sysFeeParm.getValue("ORDER_CODE", j));// �ӽڵ�ID
						nodeTemp.setSeq(indexSql);// ���ô˽ڵ��˳���
						// nodeTemp.setSeq(index);
						indexSql++;
						node.addSeq(nodeTemp);// ����ӽڵ�
					}
				}
			}
		}
		return (TTreeNode[]) root.toArray(new TTreeNode[] {});
	}

	/**
	 * �õ��ϲ����
	 * 
	 * @param s
	 *            String
	 * @return String
	 */
	public String getNumberParent(String s) {
		if (s == null || s.length() == 0)
			return "";
		int classifyIndex = getNumberClass(s) - 1;
		if (classifyIndex <= 0)
			return "";
		int length = getClassNumber(classifyIndex);
		return s.substring(0, length);
	}

	/**
	 * �õ��������
	 * 
	 * @param s
	 *            String
	 * @return int
	 */
	public int getNumberClass(String s) {
		int length = s.length();
		for (int i = 0; i < getClassifyCurrent(); i++) {
			length -= getClassify(i);
			if (length == 0)
				return i + 1;
		}
		if (length == serial)
			return getClassifyCurrent() + 1;
		return -1;
	}

	/**
	 * �õ�ĳ������
	 * 
	 * @param i
	 *            int
	 * @return int
	 */
	public int getClassify(int i) {
		if (i < 0 || i >= CLASSIFY_MAX)
			return -1;
		return classify[i];
	}

	/**
	 * �õ���������
	 * 
	 * @param classifyIndex
	 *            int
	 * @return int
	 */
	public int getClassNumber(int classifyIndex) {
		if (classifyIndex <= 0 || classifyIndex > getClassifyCurrent() + 1)
			return -1;
		if (classifyIndex == getClassifyCurrent() + 1)
			return getTot();
		int c = 0;
		for (int i = 0; i < classifyIndex; i++)
			c += getClassify(i);
		return c;
	}
}
