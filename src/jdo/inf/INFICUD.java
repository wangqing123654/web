package jdo.inf;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.javahis.util.StringUtil;
/**
 * <p>Title: ICU�Ǽ�ϸ��  </p>
 *
 * <p>Description: ICU�Ǽ�ϸ�� </p>
 *
 * <p>Copyright: 2014 </p>
 *
 * <p>Company:BlueCore </p>
 *
 * @author wanglong 2014.02.24
 * @version 1.0
 */
public class INFICUD extends TDataStore {

    /**
     * ��ѯ�¼�
     *
     * @return
     */
    public int onQuery() {
        this.setSQL("SELECT * FROM INF_ICURGSD ORDER BY RE_NO");
        return 0;
    }

    /**
     * ����RE_NO��ʼ������
     * 
     * @param reNo
     * @return
     */
    public int onQueryByReNo(String reNo) {
        if (StringUtil.isNullString(reNo)) {
            return -1;
        }
        this.setSQL("SELECT * FROM INF_ICURGSD WHERE RE_NO='#' ORDER BY RE_NO"
                .replaceFirst("#", reNo));
        int result = this.retrieve();
        if (result < 0) {
            return result;
        }
        return 0;
    }

    /**
     * �����һ��SEQ_NO
     * @return
     */
    public int getNextSeqNo() {
        TParm buff = new TParm();
        if (isFilter()) {
            buff = this.getBuffer(FILTER);
        } else {
            buff = getBuffer(PRIMARY);
        }
        int count = buff.getCount();
        int seq = 0;
        for (int i = 0; i < count; i++) {
            int x = buff.getInt("SEQ", i);
            if (seq < x) seq = x;
        }
        return seq + 1;
    }
    
    /**
     * �Ƿ�������
     * 
     * @return boolean
     */
    public int isNewRow() {
        int rowCount = this.rowCount();
        for (int i = 0; i < rowCount; i++) {
            if (!this.isActive(i)) return i;
        }
        return -1;
    }

}
