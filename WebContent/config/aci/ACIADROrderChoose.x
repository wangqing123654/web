## TBuilder Config File ## Title: 住院医嘱选择## Company:BlueCore## Author:wanglong 2014.01.07## version 1.0#<Type=TFrame>UI.Title=住院医嘱选择UI.MenuConfig=UI.Width=790UI.Height=270UI.toolbar=YUI.controlclassname=com.javahis.ui.aci.ACIADROrderChooseControlUI.item=tPanel_0;tPanel_1UI.layout=nullUI.AutoSize=0UI.Border=CANCEL.Type=TButtonCANCEL.X=412CANCEL.Y=15CANCEL.Width=81CANCEL.Height=23CANCEL.Text=取消CANCEL.Action=onCancelRETURN.Type=TButtonRETURN.X=266RETURN.Y=15RETURN.Width=81RETURN.Height=23RETURN.Text=回传RETURN.Action=onReturntPanel_1.Type=TPaneltPanel_1.X=0tPanel_1.Y=216tPanel_1.Width=789tPanel_1.Height=53tPanel_1.Border=tPanel_1.Item=RETURN;CANCELtPanel_1.AutoWidth=YtPanel_1.AutoX=YtPanel_1.AutoY=YtPanel_1.AutoW=YtPanel_1.AutoH=YtPanel_1.AutoHeight=NtPanel_1.AutoSize=1tPanel_1.AutoHSize=1DR_CODE.Type=人员下拉列表DR_CODE.X=290DR_CODE.Y=94DR_CODE.Width=81DR_CODE.Height=23DR_CODE.Text=TButtonDR_CODE.showID=YDR_CODE.showName=YDR_CODE.showText=NDR_CODE.showValue=NDR_CODE.showPy1=YDR_CODE.showPy2=YDR_CODE.Editable=YDR_CODE.Tip=人员DR_CODE.TableShowList=nameROUTE_CODE.Type=途径下拉列表ROUTE_CODE.X=240ROUTE_CODE.Y=92ROUTE_CODE.Width=81ROUTE_CODE.Height=23ROUTE_CODE.Text=TButtonROUTE_CODE.showID=YROUTE_CODE.showName=YROUTE_CODE.showText=NROUTE_CODE.showValue=NROUTE_CODE.showPy1=YROUTE_CODE.showPy2=YROUTE_CODE.Editable=YROUTE_CODE.Tip=频次ROUTE_CODE.TableShowList=nameFREQ_CODE.Type=频次下拉列表FREQ_CODE.X=190FREQ_CODE.Y=96FREQ_CODE.Width=81FREQ_CODE.Height=23FREQ_CODE.Text=TButtonFREQ_CODE.showID=YFREQ_CODE.showName=YFREQ_CODE.showText=NFREQ_CODE.showValue=NFREQ_CODE.showPy1=YFREQ_CODE.showPy2=YFREQ_CODE.Editable=YFREQ_CODE.Tip=频次FREQ_CODE.TableShowList=nameUNIT_CODE.Type=计量单位下拉列表UNIT_CODE.X=150UNIT_CODE.Y=94UNIT_CODE.Width=81UNIT_CODE.Height=23UNIT_CODE.Text=TButtonUNIT_CODE.showID=YUNIT_CODE.showName=YUNIT_CODE.showText=NUNIT_CODE.showValue=NUNIT_CODE.showPy1=YUNIT_CODE.showPy2=YUNIT_CODE.Editable=YUNIT_CODE.Tip=计量单位UNIT_CODE.TableShowList=nameTABLE.Type=TTableTABLE.X=2TABLE.Y=2TABLE.Width=784TABLE.Height=214TABLE.SpacingRow=1TABLE.RowHeight=20TABLE.AutoX=NTABLE.AutoY=NTABLE.AutoWidth=YTABLE.AutoHeight=YTABLE.Header=主,25,boolean;急,25,boolean;组,30;医嘱,200;规格,60;开药量,55;单位,40,UNIT_CODE;配药量,55;单位,40,UNIT_CODE;频次,60,FREQ_CODE;用法,60,ROUTE_CODE;下达时间,150,Timestamp,yyyy/MM/dd HH:mm:ss;停用日期,160,Timestamp,yyyy/MM/dd HH:mm:ss;医生备注,150;开单医生,100,DR_CODETABLE.ParmMap=LINKMAIN_FLG;URGENT_FLG;LINK_NO;ORDER_DESC;SPECIFICATION;MEDI_QTY;MEDI_UNIT;DOSAGE_QTY;DOSAGE_UNIT;FREQ_CODE;ROUTE_CODE;EFF_DATE;DC_DATE;DR_NOTE;ORDER_DR_CODETABLE.LockColumns=allTABLE.ColumnHorizontalAlignmentData=2,left;3,left;4,right;5,left;6,right;7,left;8,left;9,left;10,left;11,left;12,left;15,left;16,left;17,leftTABLE.Item=UNIT_CODE;FREQ_CODE;ROUTE_CODE;DR_CODETABLE.AutoSize=3TABLE.DoubleClickedAction=onTableDoubleClieckedtPanel_0.Type=TPaneltPanel_0.X=0tPanel_0.Y=0tPanel_0.Width=790tPanel_0.Height=219tPanel_0.Border=tPanel_0.item=TABLEtPanel_0.AutoWidth=YtPanel_0.AutoHSize=50tPanel_0.AutoHeight=YtPanel_0.AutoSize=1