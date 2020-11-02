package com.javahis.ui.spc;

import java.sql.Timestamp;
import java.util.Date;

import jdo.ind.INDSQL;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TMessage;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: ҩƷ��λ
 * </p>
 *
 * <p>
 * Description: ҩƷ��λ
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company:
 * </p>
 *
 * @author zhangy 2009.04.22
 * @version 1.0
 */

public class INDMateriallocControl
    extends TControl {

    private String action = "save";

    public INDMateriallocControl() {
        super();
    }

    /**
     * ��ʼ������
     */
    public void onInit() {
        // ��ʼ��������
        initPage();
    }

    /**
     * ���淽��
     */
    public void onSave() {
        TTable table = getTable("TABLE");
        table.acceptText();
        TParm a = table.getParmValue();
        int row = 0;
        if ("save".equals(action)) {
            TTextFormat combo = this.getTextFormat("ORG_CODE");
            boolean flg = combo.isEnabled();
            if (flg) {
                if (!CheckData())
                    return;
                row = table.addRow();
            }
            else {
                row = table.getSelectedRow();
            }
            table.setItem(row, "ORG_CODE", getValueString("ORG_CODE"));
            table.setItem(row, "MATERIAL_LOC_CODE",
                          getValueString("MATERIAL_LOC_CODE"));
            table.setItem(row, "MATERIAL_CHN_DESC",
                          getValueString("MATERIAL_CHN_DESC"));
            table.setItem(row, "MATERIAL_ENG_DESC",
                          getValueString("MATERIAL_ENG_DESC"));
            table.setItem(row, "ELETAG_CODE", getValueString("ELETAG_CODE"));
            table.setItem(row, "ORDER_CODE",  getValueString("ORDER_CODE"));
            table.setItem(row, "ORDER_DESC",  getValueString("ORDER_DESC"));
            table.setItem(row, "PY1", getValueString("PY1"));
            table.setItem(row, "PY2", getValueString("PY2"));
            table.setItem(row, "SEQ", getValueString("SEQ"));
            table.setItem(row, "DESCRIPTION", getValueString("DESCRIPTION"));
            table.setItem(row, "OPT_USER", Operator.getID());
            Timestamp date = StringTool.getTimestamp(new Date());
            table.setItem(row, "OPT_DATE", date);
            table.setItem(row, "OPT_TERM", Operator.getIP());
        }
        TParm aa = table.getParmValue();
        TDataStore dataStore = table.getDataStore();
        if (dataStore.isModified()) {
            table.acceptText();
            if (!table.update()) {
                messageBox("E0001");
                return;
            }
            table.setDSValue();
        }
        messageBox("P0001");
        table.setDSValue();
        onClear();
    }

    /**
     * ɾ������
     */
    public void onDelete() {
        TTable table = getTable("TABLE");
        int row = table.getTable().getSelectedRow();
        if (row < 0)
            return;
        table.removeRow(row);
        table.setSelectionMode(0);
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
        action = "delete";
    }

    /**
     * ��ѯ����
     */
    public void onQuery() {
        String org = getValueString("ORG_CODE");
        String material = getValueString("MATERIAL_LOC_CODE");
        String filterString = "";
        if (org.length() > 0 && material.length() > 0) {
            filterString = "ORG_CODE = '" + org
                + "' AND MATERIAL_LOC_CODE like '" + material + "%'";
        }
        else if (org.length() > 0) {
            filterString = "ORG_CODE = '" + org + "'";
        }
        else if (material.length() > 0) {
            filterString = "MATERIAL_LOC_CODE like '" + material + "%'";
        }
        else {
            filterString = "";
        }
        TTable table = getTable("TABLE");
        table.setFilter(filterString);
        table.filter();
    }

    /**
     * ��շ���
     */
    public void onClear() {
        getTextFormat("ORG_CODE").setEnabled(true);
        getTextField("MATERIAL_LOC_CODE").setEnabled(true);
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
        String tags =
            "ORG_CODE;MATERIAL_LOC_CODE;MATERIAL_CHN_DESC;MATERIAL_ENG_DESC;"
            + "PY1;PY2;DESCRIPTION;ORDER_CODE;ORDER_DESC;ELETAG_CODE";
        clearValue(tags);
        // ����+1(SEQ)
        TDataStore dataStroe = getTable("TABLE").getDataStore();
        int seq = getMaxSeq(dataStroe, "SEQ",
                            dataStroe.isFilter() ? dataStroe.FILTER :
                            dataStroe.PRIMARY);
        setValue("SEQ", seq);
        TTable table = getTable("TABLE");
        table.removeRowAll();
        table.setSelectionMode(0);
        action = "save";
    }

    /**
     * TABLE�����¼�
     */
    public void onTableClicked() {
        TTable table = getTable("TABLE");
        int row = table.getSelectedRow();
        if (row != -1) {
            ( (TMenuItem) getComponent("delete")).setEnabled(true);
            TParm parm = table.getDataStore().getRowParm(row);
            String likeNames =
                "ORG_CODE;MATERIAL_LOC_CODE;MATERIAL_CHN_DESC;MATERIAL_ENG_DESC;"
                + "PY1;PY2;SEQ;DESCRIPTION;ORDER_CODE;ORDER_DESC;ELETAG_CODE";
            this.setValueForParm(likeNames, parm);
            getTextFormat("ORG_CODE").setEnabled(false);
            getTextField("MATERIAL_LOC_CODE").setEnabled(false);
            action = "save";
        }
    }

    /**
     * ��λ���ƻس��¼�
     */
    public void onMaterialAction() {
        String name = getValueString("MATERIAL_CHN_DESC");
        if (name.length() > 0)
            setValue("PY1", TMessage.getPy(name));
        ( (TTextField) getComponent("MATERIAL_ENG_DESC")).grabFocus();
    }

    /**
     * ��ʼ��������
     */
    private void initPage() {
        TParm parm = new TParm();
        parm.setData("CAT1_TYPE", "PHA");
        // ���õ����˵�
        getTextField("ORDER_CODE").setPopupMenuParameter("UD",
            getConfigParm().newConfig(
                "%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
        // ������ܷ���ֵ����
        getTextField("ORDER_CODE").addEventListener(
            TPopupMenuEvent.RETURN_VALUE, this, "popReturn");

        
        // ��ʼ��Table
        TTable table = getTable("TABLE");
        table.removeRowAll();
        TDataStore dataStroe = new TDataStore();
        dataStroe.setSQL(INDSQL.getMaterialloc());
        dataStroe.retrieve();
        table.setDataStore(dataStroe);
        table.setDSValue();

        // ����+1(SEQ)
        int seq = getMaxSeq(dataStroe, "SEQ",
                            dataStroe.isFilter() ? dataStroe.FILTER :
                            dataStroe.PRIMARY);
        setValue("SEQ", seq);
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
    }

    /**
     * �������
     */
    private boolean CheckData() {
        String org_code = getValueString("ORG_CODE");
        if ("".equals(org_code)) {
            this.messageBox("���Ŵ��벻��Ϊ��");
            return false;
        }
        String material_loc_code = getValueString("MATERIAL_LOC_CODE");
        if ("".equals(material_loc_code)) {
            this.messageBox("��λ���벻��Ϊ��");
            return false;
        }
        String sql = INDSQL.getMaterialloc(org_code, material_loc_code);
        TDataStore dataStore = new TDataStore();
        dataStore.setSQL(sql);
        dataStore.retrieve();
        if (dataStore.rowCount() > 0) {
            this.messageBox("���ź���λ�ظ����޷�����");
            return false;
        }
        return true;
    }

    /**
     * �õ�ComboBox����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TComboBox getComboBox(String tagName) {
        return (TComboBox) getComponent(tagName);
    }

    /**
     * �õ�TextFormat����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TTextFormat getTextFormat(String tagName) {
        return (TTextFormat) getComponent(tagName);
    }


    /**
     * �õ�Table����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }

    /**
     * �õ�TextField����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TTextField getTextField(String tagName) {
        return (TTextField) getComponent(tagName);
    }

    /**
     * �õ����ı�� +1
     *
     * @param dataStore
     *            TDataStore
     * @param columnName
     *            String
     * @return String
     */
    private int getMaxSeq(TDataStore dataStore, String columnName,
                          String dbBuffer) {
        if (dataStore == null)
            return 0;
        // ����������
        int count = dataStore.getBuffer(dbBuffer).getCount();
        // ��������
        int max = 0;
        for (int i = 0; i < count; i++) {
            int value = TCM_Transform.getInt(dataStore.getItemData(i,
                columnName, dbBuffer));
            // �������ֵ
            if (max < value) {
                max = value;
                continue;
            }
        }
        // ���ż�1
        max++;
        return max;
    }
    /**
     * ���ܷ���ֵ����
     *
     * @param tag
     * @param obj
     */
    public void popReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        String order_code = parm.getValue("ORDER_CODE");
        if (!StringUtil.isNullString(order_code)) {
            getTextField("ORDER_CODE").setValue(order_code);
        }
        String order_desc = parm.getValue("ORDER_DESC");
        if (!StringUtil.isNullString(order_desc)) {
            getTextField("ORDER_DESC").setValue(order_desc);
        }
    }
    
 /*   *//**
     * �����ҩ������
     * @date 20120828
     * @author liyh
     *//*
    public void onImpXML(){
        TParm parm = new TParm();
        Object result = openDialog("%ROOT%\\config\\ind\\INDMateriallocEleTag.x", parm);
		if(null != result){
            TParm fileParm = (TParm) result;
            if (fileParm == null) {
                return;
            }
            String filePath = (String) fileParm.getData("PATH", 0);
            TParm xmlParm = new TParm();
			try {
				try {
					filePath = filePath.replaceAll("\\\\", "/");
					xmlParm = (TParm) FileUtils.readXMLFileOfMateriaLocAndOrderCode(filePath);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("-------xmlparm : "+xmlParm);
				int count = xmlParm.getCount("ORDER_CODE");
				if(null != xmlParm && count > 0){
					TParm resultTParm = new TParm();
					//�õ�����seq
				    int maxSeq = getMaxSeqByImport();
					for(int i = 0 ; i < count ; i++){
						String materialChnDesc = (String) xmlParm.getData("MATERIAL_CHN_DESC",i);
						String materialEngDesc = (String) xmlParm.getData("MATERIAL_ENG_DESC",i);
						String py1 = "";
						if(null != materialChnDesc && materialChnDesc.length() > 0){
							py1 = TMessage.getPy(materialChnDesc);
						}
						if(null == materialEngDesc || "".equals(materialEngDesc) ){
							materialEngDesc = py1;
						}
						TParm mlTParm = new TParm();
						String orgCode = (String) xmlParm.getData("ORG_CODE",i);
						String orderCode = (String) xmlParm.getData("ORDER_CODE",i);
						String materialLocCode = (String) xmlParm.getData("MATERIAL_LOC_CODE",i);
						mlTParm.setData("ORG_CODE", orgCode);
						mlTParm.setData("MATERIAL_LOC_CODE", materialLocCode);
						mlTParm.setData("MATERIAL_CHN_DESC", materialChnDesc);
						mlTParm.setData("MATERIAL_ENG_DESC", materialEngDesc);
						mlTParm.setData("PY1", py1);
						mlTParm.setData("PY2", py1);
						mlTParm.setData("SEQ", maxSeq+i);
						mlTParm.setData("ELETAG_CODE", xmlParm.getData("ELETAG_CODE",i));
						mlTParm.setData("ORDER_CODE",orderCode);
						mlTParm.setData("ORDER_DESC", xmlParm.getData("ORDER_DESC",i));
						mlTParm.setData("DESCRIPTION", xmlParm.getData("MATERIAL_LOC_CODE",i));
						mlTParm.setData("OPT_USER", Operator.getID());
						mlTParm.setData("OPT_TERM", Operator.getIP());
						System.out.println("----00--------update sql: "+INDSQL.saveMaterialLoc(mlTParm));
						resultTParm = new TParm(TJDODBTool.getInstance().update(INDSQL.saveMaterialLoc(mlTParm)));
				        if (resultTParm.getErrCode() < 0) {
				            err("ERR:" + resultTParm.getErrCode() + resultTParm.getErrText()
				                + resultTParm.getErrName());
				            break;
				        }
				        //�޸�ҩ����λ
				        String updateSql = INDSQL.onUpdateIndStcokMaterialLocCode(orgCode, orderCode, materialLocCode);
				        System.out.println("------111----updateSql: "+updateSql );
				        resultTParm = new TParm(TJDODBTool.getInstance().update(updateSql));
				        if (resultTParm.getErrCode() < 0) {
				            err("ERR:" + resultTParm.getErrCode() + resultTParm.getErrText()
				                + resultTParm.getErrName());
				            break;
				        }
				        
				        //�޸�ҩ��������λ
				        updateSql = INDSQL.onUpdateStcokMaterialLocCode(orgCode, orderCode, materialLocCode);
				        System.out.println("------111----updateSql: "+updateSql );
				        resultTParm = new TParm(TJDODBTool.getInstance().update(updateSql));
				        if (resultTParm.getErrCode() < 0) {
				            err("ERR:" + resultTParm.getErrCode() + resultTParm.getErrText()
				                + resultTParm.getErrName());
				            break;
				        }
					}
			        if (resultTParm.getErrCode() < 0) {
			            err("ERR:" + resultTParm.getErrCode() + resultTParm.getErrText()
			                + resultTParm.getErrName());
			            messageBox("����ʧ��");
			            return ;
			        }					
					messageBox("����ɹ�");
					initPage();
					return;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	
    }
    */
    
 /*   
    *//**
     * �õ����ı�� +1
     * @return String
     *//*
	private int getMaxSeqByImport() {
		int maxSeq = 0;
		// �õ��������к�
		TParm seqTParm = new TParm(TJDODBTool.getInstance().select(INDSQL.getMaxSeqByMaterialLoc()));
		if (null != seqTParm) {
			maxSeq = Integer.parseInt(seqTParm.getValue("SEQ", 0));
			maxSeq++;
		} else {
			TDataStore dataStroe = getTable("TABLE").getDataStore();
			maxSeq = getMaxSeq(dataStroe, "SEQ", dataStroe.isFilter() ? dataStroe.FILTER : dataStroe.PRIMARY);
		}
		return maxSeq;
	}
*/

	public static void main(String args[]){
		String path = "c:/temp/123.xml";
		TParm a;
		try {
			a = FileUtils.readXMLFileOfMateriaLocAndOrderCode("c:/temp/123.xml");
			System.out.println("a: "+a);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
