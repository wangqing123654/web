package com.javahis.ui.inw;


import java.awt.Dimension;

import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;

import javax.swing.JScrollPane;


import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TDialog;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.wcomponent.ui.WMatrix;
import com.dongyang.wcomponent.ui.WPanel;
import com.dongyang.wcomponent.ui.model.DefaultWMatrixModel;
import com.javahis.component.BaseCard;
import com.javahis.component.S_Card;
import com.javahis.component.T_Card;
import com.javahis.util.DateUtil;
import com.tiis.ui.TiPanel;



/**
 *
 * 床头卡功能
 *
 * @author lixiang
 *
 */
public class INWBedCardControl extends TControl  {

	private TDialog mainFrame;
	TPanel tiPanel1;
	TiPanel tiPanel2 = new TiPanel();
	JScrollPane jScrollPane1 = new JScrollPane();
	//TiPanel tiPanel3 = new TiPanel();

	TPanel tiPanel3 = new TPanel();

	// 病区
	private TTextFormat stationCode;
	//隐藏人员、
	private TTextFormat userId;
	/**
	 * 简卡单选
	 */
	private TRadioButton sCard;
	/**
	 * 细卡单选
	 */
	private TRadioButton tCard;
	/**
	 * 床位总数
	 */
	private TTextField bedsCount;

	/** 床头卡 */
	private WMatrix jList = null;

	/**
     *
     */
	public INWBedCardControl() {

	}

	/**
	 * 初始化
	 */
	public void onInit() {
		super.onInit();
		mainFrame = ((TDialog) getComponent("UI"));
		mainFrame.setResizable(true);
		//mainFrame.setState(0);
		// mainFrame.setUndecorated(true);
		tiPanel1 = ((TPanel) getComponent("tPanel_0"));
		//testBorder3 = new TitledBorder(BorderFactory.createEtchedBorder(new Color(228, 255, 255),new Color(112, 154, 161)),"测试重画");
		tiPanel2.setBounds(new Rectangle(0, 1, 1020, 630));
		tiPanel2.setSize(1010, 680);
		tiPanel2.setLayout(null);
		jScrollPane1.setBounds(new Rectangle(3, 12, 1000, 620));
		tiPanel1.add(tiPanel2, null);
		tiPanel2.add(jScrollPane1, null);
		//test
		//tiPanel3.setBorder(testBorder3);
		tiPanel3.setBorder("");
		jScrollPane1.getViewport().add(tiPanel3, null);
		sCard = (TRadioButton) this.getComponent("S_CARD");
		tCard = (TRadioButton) this.getComponent("T_CARD");
		bedsCount= (TTextField) this.getComponent("tTextField_0");

		this.stationCode = (TTextFormat) this.getComponent("STATION_CODE");
		this.userId = (TTextFormat) this.getComponent("USER_ID");
		String userid = Operator.getID();
		// this.messageBox("==currentStation=="+currentStation);
		this.userId.setValue(userid);
		stationCode.setValue(Operator.getStation());
		onQueryData();
	}

	/**
     *
     */
	public void onQueryData() {
		//this.messageBox("执行查询");
		this.buildBedData();
	}

	//

	/**
	 * 动态构造床位数据
	 */
	public void buildBedData() {

		//@@
		DefaultWMatrixModel model = new DefaultWMatrixModel();

		//##tiPanel3.removeAll();

		int j = 0;
		// this.messageBox("sCard"+sCard);

		//服务器时间
		Timestamp serverTime = TJDODBTool.getInstance().getDBTime();

		// 判断 该病区在系统中未设床位
		String bedsSql = " SELECT BED_NO_DESC,ALLO_FLG,BABY_BED_FLG  " + " FROM SYS_BED  "  //新增婴儿床
				+ " WHERE REGION_CODE='" + Operator.getRegion() + "' "
				+ " AND STATION_CODE='" + this.stationCode.getValue() + "' "
				+ " AND ACTIVE_FLG='Y' " + " ORDER BY REGION_CODE,BED_NO ";
		//System.out.println("==bedsSql=="+bedsSql);
		TParm bedParm = new TParm(TJDODBTool.getInstance().select(bedsSql));
		int count=0;
		int occuBedCount = 0;
		//this.messageBox("bedParm" + bedParm.getCount());
		if (bedParm.getCount() == 0 || bedParm.getCount() == -1) {
			this.messageBox("该病区在系统中未设床位");
		} else {
			// 简卡
			if (sCard.isSelected()) {//
				occuBedCount = 0;
				//this.messageBox("显示简卡");
				// 查询住院病人资料
				String sPatSql = "Select A.bed_no_desc,C.pat_name,B.mr_no,B.nursing_class,";
				sPatSql += "D.CHN_DESC,E.colour_red,E.colour_green,E.colour_blue,F.colour_red as colour_red1,";
				sPatSql += "F.colour_green as colour_green1,F.colour_blue as colour_blue1,B.PATIENT_STATUS,A.BED_OCCU_FLG,";
				sPatSql += "B.REGION_CODE,B.case_no,C.BIRTH_DATE,C.SEX_CODE,'' ins_status,B.CLNCPATH_CODE,B.DEPT_CODE,B.IPD_NO,A.BED_STATUS, ";
				sPatSql += "A.ROOM_CODE,G.BEDTYPE_DESC ";
				//--新生儿
				sPatSql += " ,B.NEW_BORN_FLG,A.BABY_BED_FLG ";
				//--新生日算法
				sPatSql += " ,B.in_date as IN_DATE_2 ";
				//##
				sPatSql += "from SYS_BED A,ADM_INP B,SYS_PATINFO C,(select * from sys_dictionary where group_id='SYS_SEX') D,";
				sPatSql += "ADM_NURSING_CLASS E,ADM_PATIENT_STATUS F,SYS_BED_TYPE G ";
				sPatSql += "Where A.REGION_CODE='"+Operator.getRegion()+"' ";
				sPatSql += "and A.station_code='"+this.stationCode.getValue()+"' ";
				sPatSql += "and A.REGION_CODE=B.REGION_CODE ";
				sPatSql += "and A.case_no=B.case_no ";
				sPatSql += "and B.mr_no=C.mr_no ";
				sPatSql += "and C.SEX_CODE=D.ID ";
				sPatSql += "and E.NURSING_CLASS_Code(+)=B.NURSING_CLASS ";
				sPatSql += "and F.PATIENT_STATUS_code(+)=B.PATIENT_STATUS ";
				sPatSql += "and A.BED_TYPE_CODE=G.BED_TYPE_CODE(+) ";
//                sPatSql +="AND A.ALLO_FLG='Y' AND B.CANCEL_FLG<>'Y' AND A.BED_STATUS='1' "; //与下面的一致
                sPatSql +="AND A.ALLO_FLG='Y' AND B.CANCEL_FLG<>'Y' "; //如不去掉床位状态条件 有的包床显示不出来
				sPatSql += "ORDER BY A.REGION_CODE,A.BED_NO";

			//System.out.println("=====sPatSql====="+sPatSql);
				TParm sPatParm = new TParm(TJDODBTool.getInstance().select(
						sPatSql));
				//this.messageBox("++sPatParm+"+sPatParm);
				int result_count = sPatParm.getCount();
				// 床位总数
				int row = Integer.parseInt(String.valueOf(bedParm.getCount()));
				//(int) TiMath.ceil(row / 5.0, 0)

				//System.out.println("=============简卡长度================"+row);

				for (int i = 0; i < row; i++) {
					S_Card s_card[] = new S_Card[row];
					//this.messageBox("==i=="+i);
					// 床号相同，说明有人使用床位
					// String.valueOf(((Vector)Sta_bed.get(1)).get(i)).equals(String.valueOf(((Vector)result.get(0)).get(j)))

					if (result_count != 0
							&& bedParm.getValue("BED_NO_DESC", i).equals(
									sPatParm.getValue("BED_NO_DESC", j))) {

						String s[] = new String[31];
						//this.messageBox("COLOUR_RED====="+sPatParm.getValue("COLOUR_RED", j));
						s[0] = sPatParm.getValue("COLOUR_RED", j).equals("")? "255"
								: sPatParm.getValue("COLOUR_RED", j);
						s[1] = sPatParm.getValue("COLOUR_GREEN", j).equals("")? "255"
								: sPatParm.getValue("COLOUR_GREEN", j);
						s[2] = sPatParm.getValue("COLOUR_BLUE", j).equals("")? "255"
								: sPatParm.getValue("COLOUR_BLUE", j);
						s[3] = sPatParm.getValue("COLOUR_RED1", j).equals("")? "255"
								: sPatParm.getValue("COLOUR_RED1", j);
						s[4] = sPatParm.getValue("COLOUR_GREEN1", j).equals("")? "255"
								: sPatParm.getValue("COLOUR_GREEN1", j);
						s[5] = sPatParm.getValue("COLOUR_BLUE1", j) .equals("")? "255"
								: sPatParm.getValue("COLOUR_BLUE1", j);
						// PATIENT_STATUS
						s[6] = sPatParm.getValue("PATIENT_STATUS", j);
						// BED_OCCU_FLG
						for (int k = 0; k < sPatParm.getCount("MR_NO"); k++) {
							if(sPatParm.getValue("MR_NO", j).equals(sPatParm.getValue("MR_NO", k))){
								if(sPatParm.getValue("BED_OCCU_FLG", j).equals("Y")){
									s[7] = "Y";
									occuBedCount++;
									break;
								}
							}
						}
						if(s[7] == null || s[7].length() <= 0)
							s[7] = sPatParm.getValue("BED_OCCU_FLG", j);
						// REGION_CODE
						s[8] = sPatParm.getValue("REGION_CODE", j);
						// case_no
						s[9] = sPatParm.getValue("CASE_NO", j);
						// BIRTH_DATE
						s[10] = sPatParm.getValue("BIRTH_DATE", j);
						// SEX_CODE
						s[11] = sPatParm.getValue("SEX_CODE", j);
						// ins_status
						s[12] = sPatParm.getValue("INS_STATUS", j);
						// CLNCPATH_CODE
						s[13] =sPatParm.getValue("CLNCPATH_CODE", j);
						s[14] =sPatParm.getValue("DEPT_CODE", j);
						s[15] =sPatParm.getValue("IPD_NO", j);
						s[16] =sPatParm.getValue("ROOM_CODE", j);
						s[17] =sPatParm.getValue("BEDTYPE_DESC", j);

                        //
                        //
						s[18] = "";
						s[19] = "";

						//##
						s[20] = "";
						s[21] = sPatParm.getValue("BED_STATUS", j);
						s[22] = "";
						s[23] = "";
						s[24] = "";
						s[25] = "";
						s[26] = "";
						s[27] = sPatParm.getValue("NEW_BORN_FLG", j);
						s[28] = sPatParm.getValue("PATIENT_STATUS", j);

						//新生日算法
						//Timestamp indate = (Timestamp)sPatParm.getData("IN_DATE_2", j);
						Timestamp birth = (Timestamp)sPatParm.getData("BIRTH_DATE", j);
						//s[29] = DateUtil.showAge( birth, indate);
						s[29] = DateUtil.showAge( birth,serverTime);

						//婴儿床
						s[30] = sPatParm.getValue("BABY_BED_FLG", j);

						//
						//this.messageBox("BED_NO"+sPatParm.getValue("BED_NO", j));
						s_card[i] = new S_Card( sPatParm.getValue("BED_NO_DESC", j),
								                sPatParm.getValue("PAT_NAME", j),
								                sPatParm.getValue("MR_NO", j),
								                sPatParm.getValue("NURSING_CLASS", j) == null ? "": sPatParm.getValue("NURSING_CLASS", j),
								                (String) stationCode.getValue(),
								                sPatParm.getValue("CHN_DESC", j) == null ? "": sPatParm.getValue("CHN_DESC", j),
										        s );

						if (j < result_count - 1) {
							j++;
						}
						count++;
						// 是空床的情况
					} else {
						// String.valueOf(((Vector)Sta_bed.get(1)).get(i))
						s_card[i] = new S_Card( bedParm.getValue("BED_NO_DESC", i),
								                "",
								                sPatParm.getValue("ROOM_CODE", j),
								                sPatParm.getValue("BEDTYPE_DESC", j),
								                bedParm.getValue("BABY_BED_FLG", i) );
					}
					//s_card[i].setPreferredSize(new Dimension(150, 60));
					s_card[i].setPreferredSize(new Dimension(183, 71));
					//this.messageBox("=====s_card["+i+"]"+s_card[i].sBed_no);
					// s_card[i].addMouseListener(this) ;
					//TButton btest=new TButton();
					//btest.setLabel("ok");
					//tiPanel3.add(btest,null);

					/**
					//
		        	s_card[i].addMouseListener(new MouseAdapter() {
						public void mouseClicked(MouseEvent mouseevent) {

							S_Card sc = (S_Card) mouseevent.getSource();

							if (sc.sMr_no.equals("")) {
								JOptionPane.showMessageDialog(sc, "此床没有病人");
								return;
							}
							// 构造参数;
							// 打开 .x 文件
							TParm parm = new TParm();
							parm.setData("CASE_NO", sc.sCase_no);
							parm.setData("STATION_CODE", sc.sStation_code);
							parm.setData("DEPT_CODE", sc.sDeptCode);
							parm.setData("IPD_NO", sc.sIpdNo);
							parm.setData("MR_NO", sc.sMr_no);
							openOdiStationWindow(parm);
						}
					});
					//
		        	**/

					//##tiPanel3.add(s_card[i], null);
					//@@
					WPanel jp = new WPanel();
					jp.setId("jp_" + i);
					jp.add(s_card[i], null);
					model.addElement(jp);
					jp.setRoundedBorder();
				}
				count -= occuBedCount;
				// 细卡
			} else if (tCard.isSelected()) {
				occuBedCount = 0;
				//this.messageBox("显示细卡");
				// 查询住院病人资料
				String tPatSql = "Select A.bed_no_desc,C.pat_name,B.mr_no,B.nursing_class,D.CHN_DESC,";
				tPatSql +="case NHI_CTZ_FLG when 'Y' then '医保' when 'N' then '自费' end CTZ,B.PATIENT_STATUS,G.USER_NAME,'' BLANK,";
				tPatSql +="to_number(to_char(sysdate,'yyyy'))-to_number(to_char(C.BIRTH_DATE,'yyyy')) age,TO_CHAR(B.in_date,'YYYY/MM/DD') IN_DATE,A.case_no,";
				tPatSql +="I.colour_red,I.colour_green,I.colour_blue,J.colour_red  as colour_red1,J.colour_green as colour_green1,J.colour_blue as colour_blue1,A.BED_OCCU_FLG,";
				tPatSql +="B.REGION_CODE,B.case_no patCaseNo,C.BIRTH_DATE,C.SEX_CODE,'' ins_status,B.CLNCPATH_CODE,B.DEPT_CODE,B.IPD_NO ";
				//--新生儿
				tPatSql += " ,B.NEW_BORN_FLG,A.BABY_BED_FLG ";
				//--新生日算法
				tPatSql += " ,B.in_date as IN_DATE_2 ";
				//##
				tPatSql +="from SYS_BED A,ADM_INP B,SYS_PATINFO C,(select * from sys_dictionary where group_id='SYS_SEX') D,";
				tPatSql +="SYS_CTZ E,SYS_OPERATOR G,ADM_NURSING_CLASS I,ADM_PATIENT_STATUS J ";
				tPatSql +="Where A.REGION_CODE='"+Operator.getRegion()+"' ";
				tPatSql +="and A.station_code='"+this.stationCode.getValue()+"' ";
				tPatSql +="and A.REGION_CODE=B.REGION_CODE ";
				tPatSql +="and A.case_no=B.case_no ";
				tPatSql +="and B.mr_no=C.mr_no ";
				tPatSql +="and C.SEX_CODE=D.ID ";
				tPatSql +="and B.ctz1_code=E.ctz_code(+) ";//add caoyong 20140304 添加(+)
				tPatSql +="and B.VS_DR_CODE=G.user_id ";
				tPatSql +="AND b.nursing_class=i.nursing_class_code(+) ";
				tPatSql +="AND b.patient_status=j.patient_status_code(+) ";
//				tPatSql +="AND A.ALLO_FLG='Y' AND B.CANCEL_FLG<>'Y' AND A.BED_STATUS='1' ";
				tPatSql +="AND A.ALLO_FLG='Y' AND B.CANCEL_FLG<>'Y'";

				//tPatSql +="AND A.ALLO_FLG='Y' AND B.CANCEL_FLG<>'Y' AND A.BED_STATUS='1' ";
				tPatSql +="ORDER BY A.REGION_CODE,A.BED_NO";

		//System.out.println("=====tPatSql====="+tPatSql);
				//
				TParm tPatParm = new TParm(TJDODBTool.getInstance().select(
						tPatSql));
				int result_count=tPatParm.getCount();
		        int row=bedParm.getCount();
		        //(int)TiMath.ceil(row/5.0,0)

		        T_Card t_card[] = new T_Card[row];
		        for (int i = 0; i < row; i++) {
		        	String s[]=new String[33];
		        	//已占用床位
		        	if(result_count!=0
							&& bedParm.getValue("BED_NO_DESC", i).equals(
									tPatParm.getValue("BED_NO_DESC", j))){

		        		//this.messageBox("case no"+tPatParm.getValue("CASE_NO",j));
		        		//构造细卡数据
		        		s[0]=tPatParm.getValue("CHN_DESC", j);
		                s[1]=tPatParm.getValue("CTZ", j);
		                s[2]=tPatParm.getValue("PATIENT_STATUS", j);
		                s[3]=tPatParm.getValue("USER_NAME", j);
		                s[4]=tPatParm.getValue("BLANK", j);
		                s[5]=tPatParm.getValue("AGE", j);
		                s[6]=tPatParm.getValue("IN_DATE", j);
		                //取诊断内容  ADM_INP ADM_INPDIAG
		                String diagSql="Select b.icd_chn_desc ";
		                diagSql+="from ADM_INP a ,SYS_DIAGNOSIS b ";
		                diagSql+="where a.CASE_NO='"+tPatParm.getValue("CASE_NO",j)+"' ";
		                //diagSql+="and a.maindiag_flg = 'Y' ";
		                diagSql+="and b.icd_code = a.MAINDIAG ";
		                //diagSql+="order by a.io_type desc";

//		                System.out.println("=====diagSql====="+diagSql);
		                TParm diagParm = new TParm(TJDODBTool.getInstance().select(
		                		diagSql));
		                if(diagParm.getCount()==0||diagParm.getCount()==-1){
		                  s[7]="";
		                }else{
		                  s[7]=diagParm.getValue("ICD_CHN_DESC", 0);
		                }
		                //colour_red
		                //this.messageBox("colour_red====="+tPatParm.getValue("colour_red", j));
		                //System.out.println("++++colour red++++"+tPatParm.getValue("COLOUR_RED", j));
		                s[8]=tPatParm.getValue("COLOUR_RED", j).equals("")?"255":tPatParm.getValue("COLOUR_RED", j);
		                s[9]=tPatParm.getValue("COLOUR_GREEN", j).equals("")?"255":tPatParm.getValue("COLOUR_GREEN", j);
		                s[10]=tPatParm.getValue("COLOUR_BLUE", j).equals("")?"255":tPatParm.getValue("COLOUR_BLUE", j);

		                s[11]=tPatParm.getValue("COLOUR_RED1", j).equals("")?"255":tPatParm.getValue("COLOUR_RED1", j);
		                s[12]=tPatParm.getValue("COLOUR_GREEN1", j).equals("")?"255":tPatParm.getValue("COLOUR_GREEN1", j);
		                s[13]=tPatParm.getValue("COLOUR_BLUE1", j).equals("")?"255":tPatParm.getValue("COLOUR_BLUE1", j);
		                //BED_OCCU_FLG
		                for (int k = 0; k < tPatParm.getCount("MR_NO"); k++) {
							if(tPatParm.getValue("MR_NO", j).equals(tPatParm.getValue("MR_NO", k))){
								if(tPatParm.getValue("BED_OCCU_FLG", j).equals("Y")){
										s[14] = "Y";
										occuBedCount++;
										break;
								}
							}
						}
						if(s[14] == null || s[14].length() <= 0)
							s[14]=tPatParm.getValue("BED_OCCU_FLG", j);
		                s[15]=tPatParm.getValue("REGION_CODE", j);
		                s[16]=tPatParm.getValue("CASE_NO", j);
		                s[17]=tPatParm.getValue("BIRTH_DATE", j);
		                s[18]=tPatParm.getValue("SEX_CODE", j);
		                s[19]=tPatParm.getValue("INS_STATUS", j);
		                s[20]=tPatParm.getValue("CLNCPATH_CODE", j);
		                s[21] =tPatParm.getValue("DEPT_CODE", j);
						s[22] =tPatParm.getValue("IPD_NO", j);

						//
						s[23] = "";
						s[24] = tPatParm.getValue("BED_STATUS", j);
						s[25] = "";
						s[26] = "";
						s[27] = "";
						s[28] = "";
						s[29] = "";
						s[30] = tPatParm.getValue("NEW_BORN_FLG", j);

						//新生日算法
						//Timestamp indate = (Timestamp)tPatParm.getData("IN_DATE_2", j);
						Timestamp birth = (Timestamp)tPatParm.getData("BIRTH_DATE", j);
						//s[31] = DateUtil.showAge( birth, indate);
						s[31] = DateUtil.showAge( birth,serverTime);

						//婴儿床
						s[32] = tPatParm.getValue("BABY_BED_FLG", j);

		                t_card[i] = new T_Card( tPatParm.getValue("BED_NO_DESC", j),
		                		                tPatParm.getValue("PAT_NAME", j),
		                		                tPatParm.getValue("MR_NO", j),
		                		                tPatParm.getValue("NURSING_CLASS", j),
		                		                (String)stationCode.getValue(),
		                		                s );

		                if(j<result_count-1){
		                  j++;
		                }
                      count++ ;
		              }else{
		            	//空床位
		                t_card[i] = new T_Card( bedParm.getValue("BED_NO_DESC", i),
		                		                "",
		                		                bedParm.getValue("BABY_BED_FLG", i) );
		              }
		        	  t_card[i].setPreferredSize(new Dimension(153, 180));
		              //t_card[i].addMouseListener(this) ;

		        	  /**
		        	  //
		        	  t_card[i].addMouseListener( new MouseAdapter(){
		        		  public void mouseClicked(MouseEvent mouseevent) {

		        			 T_Card tc = (T_Card) mouseevent.getSource();

	        				 if(tc.sMr_no.equals("")){
	        					 JOptionPane.showMessageDialog(tc, "此床没有病人");
	        					 return;
	        					 }
	        					 //构造参数；

	        					 //打开新
	        					 TParm parm=new TParm();
	        					 parm.setData("CASE_NO", tc.sCase_no);
	        					 parm.setData("STATION_CODE", tc.sStation_code);
	        					 parm.setData("DEPT_CODE", tc.sDeptCode);
	        					 parm.setData("IPD_NO", tc.sIpdNo);
	        					 openOdiStationWindow(parm);
		        		  }
		        	  });
		        	  //
		        	  **/

						//##tiPanel3.add(t_card[i], null);
						//@@
						T_Card tc = t_card[i];
						tc.tiL_ipdno.setVisible(false);
						WPanel jp = new WPanel();
						jp.add(tc, null);
						jp.setId("jp_" + i);
						model.addElement(jp);
						jp.setRoundedBorder();
		        }
		       // this.messageBox("细卡床位：：：："+String.valueOf(count));
				//this.messageBox("细卡占床：：：："+String.valueOf(occuBedCount));
		        count -= occuBedCount;
			}
		}
	    //this.messageBox("重画panel3");

	  //设置床位总数;

		bedsCount.setText(String.valueOf(count));
		//tiPanel3.repaint();
		//
		jList = new WMatrix(model);
		jList.doProcessRowCount(6);
		jScrollPane1.setViewportView(jList);

		//
		this.doMouseClick(jList);
	}

	/**
	 *
	 * @param jList
	 */
	private void doMouseClick(final WMatrix jList) {

		jList.addMouseListener(new MouseAdapter() {

			//点击
			public void mouseClicked(MouseEvent e) {

				if (jList.getSelectedIndex() != -1) {

					//单击
					if (e.getClickCount() == 1) {

						singleClick(jList);
					}
				}
			}

			//单击
			private void singleClick(WMatrix jList) {

				 // messageBox("================" );

			 //当前单击选中的Panel
			 WPanel svalue = (WPanel) jList.getSelectedValue();
			 //单击的床头卡
			 BaseCard sbc = (BaseCard)svalue.getComponent(0);

			 if(sbc.sMr_no.equals("")){
				 messageBox( "此床没有病人");
				 return;
				 }
				 //构造参数；

				 //打开新
				 TParm parm=new TParm();
				 parm.setData("CASE_NO", sbc.sCase_no);
				 parm.setData("STATION_CODE", sbc.sStation_code);
				 parm.setData("DEPT_CODE", sbc.sDeptCode);
				 parm.setData("IPD_NO", sbc.sIpdNo);

				 //
				 if ( sbc instanceof S_Card ) {
					 parm.setData("MR_NO", sbc.sMr_no);
				 }

				 openOdiStationWindow(parm);


			}

		});
	}

	/**
	 * 打开住院医生站窗口
	 * @param parm
	 */
	public void openOdiStationWindow(TParm parm) {
		this.setReturnValue(parm);
		this.closeWindow();
	}
}
