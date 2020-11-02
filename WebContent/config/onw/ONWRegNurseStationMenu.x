#############################################
# <p>Title:门急诊护士工作站Menu </p>
#
# <p>Description:门急诊护士工作站Menu </p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company: Javahis</p>
#
# @author ZhangK
# @version 4.0
#############################################
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;card;|;onvisit;|;revisit;|;detach;|;patdata;|;nursingRec;|;insureinfo;|;barcode;|;body;|;bodycheck;|;planrep;|;docplan;|;opdrecord;|;checkrep;|;testrep;|;supcharge;|;Wrist;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=Refresh;query;|;clear;|;Wrist;|;close

query.Type=TMenuItem
query.Text=查询
query.Tip=查询
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.Tip=刷新
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif


clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空
clear.M=D
clear.key=Ctrl+D
clear.Action=onClear
clear.pic=clear.gif

detach.Type=TMenuItem
detach.Text=分诊
detach.Tip=分诊
detach.M=
detach.key=
detach.Action=onDetach
detach.pic=convert.gif

patdata.Type=TMenuItem
patdata.Text=病患资料
patdata.Tip=病患资料
patdata.M=
patdata.key=
patdata.Action=onPatdata
patdata.pic=038.gif

nursingRec.Type=TMenuItem
nursingRec.Text=病历
nursingRec.Tip=病历
nursingRec.M=
nursingRec.key=
nursingRec.Action=onNursingRec
nursingRec.pic=emr-2.gif

insureinfo.Type=TMenuItem
insureinfo.Text=保险
insureinfo.Tip=保险
insureinfo.M=
insureinfo.key=
insureinfo.Action=onInsureinfo
insureinfo.pic=038.gif

barcode.Type=TMenuItem
barcode.Text=条码
barcode.Tip=条码
barcode.M=X
barcode.key=
barcode.Action=onBarcode
barcode.pic=barcode.gif

body.Type=TMenuItem
body.Text=体征
body.Tip=体征
body.M=X
body.key=
body.Action=onBody
body.pic=new.gif

bodycheck.Type=TMenuItem
bodycheck.Text=采集查询
bodycheck.Tip=采集查询
bodycheck.M=X
bodycheck.key=
bodycheck.Action=onBodyCheck
bodycheck.pic=029.gif


planrep.Type=TMenuItem
planrep.Text=报告进度
planrep.Tip=报告进度
planrep.M=
planrep.key=
planrep.Action=onPlanrep
planrep.pic=detail-1.gif

docplan.Type=TMenuItem
docplan.Text=医技进度
docplan.Tip=医技进度
docplan.M=
docplan.key=
docplan.Action=onDocplan
docplan.pic=detail.gif

checkrep.Type=TMenuItem
checkrep.Text=检验报告
checkrep.Tip=检验报告
checkrep.M=
checkrep.key=
checkrep.Action=onCheckrep
checkrep.pic=Lis.gif

testrep.Type=TMenuItem
testrep.Text=检查报告
testrep.Tip=检查报告
testrep.M=
testrep.key=
testrep.Action=onTestrep
testrep.pic=emr-2.gif

supcharge.Type=TMenuItem
supcharge.Text=计价
supcharge.Tip=计价
supcharge.M=
supcharge.key=
supcharge.Action=onSupcharge
supcharge.pic=bill.gif

psmanage.Type=TMenuItem
psmanage.Text=皮试
psmanage.Tip=皮试
psmanage.M=
psmanage.key=
psmanage.Action=onPSManage
psmanage.pic=phl.gif

opdrecord.Type=TMenuItem
opdrecord.Text=就诊
opdrecord.Tip=就诊
opdrecord.M=
opdrecord.key=
opdrecord.Action=onOPDRecord
opdrecord.pic=010.gif

card.Type=TMenuItem
card.Text=医疗卡
card.Tip=医疗卡
card.M=D
card.key=
card.Action=onEKTcard
card.pic=042.gif

onvisit.Type=TMenuItem
onvisit.Text=到诊
onvisit.Tip=到诊
onvisit.M=D
onvisit.key=
onvisit.Action=onVisit
onvisit.pic=emr-1.gif


revisit.Type=TMenuItem
revisit.Text=回诊
revisit.Tip=回诊
revisit.M=D
revisit.key=
revisit.Action=onRevisit
revisit.pic=Retrieve.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

Wrist.Type=TMenuItem
Wrist.Text=腕带
Wrist.Tip=腕带
Wrist.M=
Wrist.key=
Wrist.Action=onWrist
Wrist.pic=print-1.gif
