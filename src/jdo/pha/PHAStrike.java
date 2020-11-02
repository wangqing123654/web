package jdo.pha;

import com.dongyang.jdo.TStrike;
import com.dongyang.data.TParm;
import java.util.Map;
import jdo.opd.TotQtyTool;

public class PHAStrike extends TStrike{
    /**
     * Ψһʵ��
     */
    private static PHAStrike instance;
    /**
     * �õ�ʵ��
     * @return PHAStrike
     */
    public static PHAStrike getInstance()
    {
        if(instance == null)
            instance = new PHAStrike();
        return instance;
    }
    /**
     * ��������
     * @param data Map
     * @return Map
     */
    public Map getTotQty(Map data) {
        if(isClientlink())
            return (Map)callServerMethod(data);
        TotQtyTool qty = new TotQtyTool();
        return qty.getTotQty(new TParm(data)).getData();
    }
}
