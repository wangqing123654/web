package com.javahis.ui.ekt;

import jdo.sys.Operator;
import jdo.opb.OPB;
import jdo.reg.PatAdmTool;
import com.dongyang.control.TControl;
import jdo.sys.SystemTool;
import com.dongyang.data.TParm;
import com.dongyang.ui.event.TTableEvent;
import java.sql.Timestamp;

/**
 * <p>Title: ҽ�ƿ������ѡ��</p>
 *
 * <p>Description: ҽ�ƿ������ѡ��</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author pangben 20111008
 * @version 1.0
 */
public class EKTChooseVisitControl  extends TControl {
   int selectrow = -1;
   OPB opb=new OPB();
   /**
    * ��ʼ������
    */
   public void onInit() {
       super.onInit();
       //�õ�ǰ̨���������ݲ���ʾ�ڽ�����
       TParm recptype = (TParm) getParameter();
       if (!recptype.getData("count").equals("0")) {
           setValueForParm("MR_NO;PAT_NAME;SEX_CODE;AGE", recptype.getParm("PARM"),
                           -1);
           callFunction("UI|TABLE|setParmValue", recptype.getParm("RESULT"));
       }
       if (recptype.getData("count").equals("0")) {
           setValueForParm("MR_NO;PAT_NAME;SEX_CODE;AGE", recptype, -1);
       }
       //Ԥ�����ʱ���
       this.callFunction("UI|STARTTIME|setValue",
                         SystemTool.getInstance().getDate());
       this.callFunction("UI|ENDTIME|setValue",
                         SystemTool.getInstance().getDate());
       //table1�ĵ��������¼�
       callFunction("UI|TABLE|addEventListener",
                    "TABLE->" + TTableEvent.CLICKED, this, "onTableClicked");
       //table1�ĵ��������¼�
       callFunction("UI|TABLE|addEventListener",
                    "TABLE->" + TTableEvent.DOUBLE_CLICKED, this,
                    "onTableDoubleClicked");
       //Ĭ��Table����ʾ����Һż�¼
//    onQuery();
   }

   /**
    *���Ӷ�Table�ļ���
    * @param row int
    */
   public void onTableClicked(int row) {
       //���������¼�
       this.callFunction("UI|TABLE|acceptText");
       selectrow = row;
   }

   public void onTableDoubleClicked(int row) {
       TParm data = (TParm) callFunction("UI|TABLE|getParmValue");
       this.setReturnValue( (String) data.getData("CASE_NO", row));
       this.callFunction("UI|onClose");
   }

   /**
    * ��ѯ
    */
   public void onQuery() {
       //==========pangben modify 20110421 start ��Ӳ�����ΪselDateByMrNo��������������������
       String regionCode = Operator.getRegion();
       TParm parm = PatAdmTool.getInstance().selDateByMrNo(getValueString("MR_NO"),
           (Timestamp) getValue("STARTTIME"), (Timestamp) getValue("ENDTIME"),regionCode);
       //==========pangben modify 20110421 start
       if (parm.getCount() < 0)
           return;
       if (parm.getCount() == 0)
           this.messageBox("�޹Һ���Ϣ!");
       this.callFunction("UI|TABLE|setParmValue", parm);
   }

   /**
    *
    */
   public void onOK() {
       TParm data = (TParm) callFunction("UI|TABLE|getParmValue");
       this.setReturnValue( (String) data.getData("CASE_NO", selectrow));
       this.callFunction("UI|onClose");
   }

}
