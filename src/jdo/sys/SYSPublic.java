package jdo.sys;

import com.dongyang.data.TParm;
import com.dongyang.ui.TTreeNode;
import java.util.Map;
/**
 *
 * <p>Title:���÷��� </p>
 *
 * <p>Description:���÷��� </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company:javahis </p>
 *
 * @author fudw
 * @version 1.0
 */
public class SYSPublic {
    /**
     * �õ��ڵ��¶��ٲ�
     * @param node TTreeNode(���Ľ��,һ���Ǹ��ڵ�)
     * @return TParm(���ز�ķ���)
     */
    public static TParm getTreeLoyar(TTreeNode node) {
        //�����β���Ϊ��
        if(node==null)
            return null;
        TParm parm = new TParm();
        //���
        int loyar = 0;
        //��������ӽڵ�
        while (getTreeNodeChild(node)) {
            loyar++;
            //���ҵ�һ���ӽڵ�
            node = (TTreeNode) node.getChildAt(0);
            //������������һ��
            int row = parm.insertRow();
            //���ID
            parm.setData("ID", row, loyar);
            //���name
            parm.setData("NAME", row, "��" + loyar + "��");
            //�����к�
            parm.setCount(loyar);
        }
        return parm;
    }
    /**
     * ��˽ڵ��Ƿ񻹴����ӽڵ�
     * @param node TTreeNode
     * @return boolean
     */
    public static boolean getTreeNodeChild(TTreeNode node) {
        if (node.getChildCount() > 0)
            return true;
        return false;
    }
    /**
     * �õ���λ���е����е�λ
     * @return Map
     */
    public static Map getUnitMap(){
        return SYSUnitTool.getInstance().getUnitMap();
    }

}
