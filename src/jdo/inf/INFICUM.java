package jdo.inf;

import jdo.sys.Operator;
import jdo.sys.SystemTool;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.javahis.util.StringUtil;
/**
 * <p>Title: ICU�Ǽ�����  </p>
 *
 * <p>Description: ICU�Ǽ����� </p>
 *
 * <p>Copyright: 2014 </p>
 *
 * <p>Company:BlueCore </p>
 *
 * @author wanglong 2014.02.24
 * @version 1.0
 */
public class INFICUM extends TDataStore {

    /**
     * ��ѯ�¼�
     * 
     * @return
     */
    public int onQuery() {
        this.setSQL("SELECT * FROM INF_ICURGSM ORDER BY RE_NO"); // ��ʼ��SQL
        return 0;
    }

    /**
     * ����RE_NO��ʼ������
     * @param reNo
     * @return
     */
    public int onQueryByReNo(String reNo) {
        if (StringUtil.isNullString(reNo)) {
            return -1;
        }
        String sql = "SELECT * FROM INF_ICURGSM WHERE RE_NO='#'";
        sql = sql.replaceFirst("#", reNo);
        this.setSQL(sql);
        int result = this.retrieve();
        if (result < 0) {
            return result;
        }
        return 0;
    }
    
    /**
     * ��ʼ��
     * @param parm
     */
    public void initData(int row, TParm parm) {
        this.setActive(row, false);
        String reNo = SystemTool.getInstance().getNo("ALL", "INF", "INF_ICUNO", "INF_ICUNO"); // ����ȡ��ԭ��
        String[] names = parm.getNames();
        for (int i = 0; i < names.length; i++) {
            this.setItem(row, names[i], parm.getData(names[i]));
        }
        this.setItem(row, "RE_NO", reNo);
        this.setItem(row, "OPT_USER", Operator.getID());
        this.setItem(row, "OPT_DATE", this.getDBTime());
        this.setItem(row, "OPT_TERM", Operator.getIP());
        this.setActive(row, true);
    }

    /**
     * ��������
     * @param parm
     */
    public void updateData(int row, TParm parm) {
        String[] names = parm.getNames();
        for (int i = 0; i < names.length; i++) {
            this.setItem(row, names[i], parm.getData(names[i]));
        }
        this.setItem(row, "OPT_USER", Operator.getID());
        this.setItem(row, "OPT_DATE", this.getDBTime());
        this.setItem(row, "OPT_TERM", Operator.getIP());
    }
 
}
