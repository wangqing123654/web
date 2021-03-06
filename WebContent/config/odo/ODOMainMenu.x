<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;tempsave;|;ekt;|;delete;|;clear;|;ClearMenu;|;caseHistory;|;showpat;|;crm;|;showPatDetail;|;planrep;|;toTemplate;|;AbnormalReg;CallNumber;|;mrshow;|;singledise;|;searchFee;|;MarkText;|;SpecialChars;|;onCancel;close

Window.Type=TMenu
Window.Text=窗口
Window.zhText=窗口
Window.enText=Window
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.zhText=文件
File.enText=File
File.M=F
File.Item=save;|;tempsave;|;delete;|;clear;|;ClearMenu;|;mrshow;|;close

save.Type=TMenuItem
save.Text=保存
save.zhText=保存
save.enText=Save
save.Tip=保存
save.zhTip=保存
save.enTip=Save
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

fee.Type=TMenuItem
fee.Text=门诊收费
fee.zhText=门诊收费
fee.enText=Fee
fee.Tip=门诊收费
fee.zhTip=门诊收费
fee.enTip=Fee
fee.M=F
fee.key=Ctrl+F
fee.Action=onFee
fee.pic=openbill-2.gif

searchFee.Type=TMenuItem
searchFee.Text=试算
searchFee.zhText=试算
searchFee.enText=searchFee
searchFee.Tip=试算
searchFee.zhTip=试算
searchFee.enTip=searchFee
searchFee.Action=onMrSearchFee
searchFee.pic=bill.gif

showpat.Type=TMenuItem
showpat.Text=病患
showpat.zhText=展开患者列表
showpat.enText=Pat Info
showpat.Tip=展开患者列表
showpat.zhTip=展开患者列表
showpat.enTip=Pat Info
showpat.M=P
showpat.key=Ctrl+P
showpat.Action=onPat
showpat.pic=patlist.gif

showPatDetail.Type=TMenuItem
showPatDetail.Text=病患详情
showPatDetail.zhText=病患详细信息
showPatDetail.enText=Pat Profile
showPatDetail.Tip=病患详细信息
showPatDetail.zhTip=病患详细信息
showPatDetail.enTip=Pat Profile
showPatDetail.M=
showPatDetail.key=
showPatDetail.Action=onPatDetail
showPatDetail.pic=pat.gif

tempsave.Type=TMenuItem
tempsave.Text=暂存
tempsave.zhText=暂存
tempsave.enText=Pending
tempsave.Tip=暂存
tempsave.zhTip=暂存
tempsave.enTip=Pending
tempsave.M=T
tempsave.key=Ctrl+T
tempsave.Action=onTempSave
tempsave.pic=tempsave.gif

delete.Type=TMenuItem
delete.Text=删除
delete.zhText=删除
delete.enText=Delete
delete.Tip=删除
delete.zhTip=删除
delete.enTip=Delete
delete.M=D
delete.key=Ctrl+D
delete.Action=deleteRow
delete.pic=delete.gif

reportstatus.Type=TMenuItem
reportstatus.Text=报告状态
reportstatus.zhText=报告状态
reportstatus.enText=Report Status
reportstatus.Tip=报告状态
reportstatus.zhTip=报告状态
reportstatus.enTip=Report Status
reportstatus.M=B
reportstatus.key=Ctrl+B
reportstatus.Action=onReportStatus
reportstatus.pic=detail-1.gif

casehistory.Type=TMenuItem
casehistory.Text=病历
casehistory.zhText=病历
casehistory.enText=Medical Records
casehistory.Tip=病历
casehistory.zhTip=病历
casehistory.enTip=Medical Records
casehistory.M=E
casehistory.key=Ctrl+E
casehistory.Action=onCaseHistory
casehistory.pic=emr.gif

toTemplate.Type=TMenuItem
toTemplate.Text=模板
toTemplate.zhText=存模板
toTemplate.enText=Save Tmpl
toTemplate.Tip=存模板
toTemplate.zhTip=存模板
toTemplate.enTip=Save Tmpl
toTemplate.M=
toTemplate.key=
toTemplate.Action=onSaveTemplate
toTemplate.pic=sta-1.gif

appointment.Type=TMenuItem
appointment.Text=预约
appointment.zhText=预约
appointment.enText=Appoint
appointment.Tip=预约
appointment.zhTip=预约
appointment.enTip=Appoint
appointment.M=A
appointment.key=Ctrl+A
appointment.Action=onAppointMent
appointment.pic=time.gif

planrep.Type=TMenuItem
planrep.Text=报告进度
planrep.Tip=报告进度
planrep.M=
planrep.key=
planrep.Action=onPlanrep
planrep.pic=detail-1.gif

mainline.Type=TMenuItem
mainline.Text=静点床位
mainline.Tip=静点床位
mainline.M=M
mainline.key=Ctrl+M
mainline.Action=onMainLine
mainline.pic=phl.gif

resonablemed.Type=TMenuItem
resonablemed.Text=合理用药
resonablemed.Tip=合理用药
resonablemed.M=Y
resonablemed.key=Ctrl+Y
resonablemed.Action=onResonablemed
resonablemed.pic=sta-4.gif

Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.Tip=刷新
Refresh.enText=Refresh
Refresh.enTip=Refresh
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

clear.Type=TMenuItem
clear.Text=清空
clear.zhText=清空
clear.enText=Empty
clear.Tip=清空
clear.zhTip=清空
clear.enTip=Empty
clear.M=E
clear.key=Ctrl+E
clear.Action=onClear
clear.pic=clear.gif


caseHistory.Type=TMenuItem
caseHistory.Text=就诊记录
caseHistory.Tip=就诊记录
caseHistory.M=C
caseHistory.Action=onCaseHistory
caseHistory.pic=032.gif

close.Type=TMenuItem
close.Text=退出
close.zhText=退出
close.enText=Quit
close.Tip=退出
close.zhTip=退出
close.enTip=Quit
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

reg.Type=TMenuItem
reg.Text=预约
reg.zhText=预约挂号
reg.enText=Appointment of registered
reg.Tip=预约挂号
reg.zhTip=预约挂号
reg.enTip=Appointment of registered
reg.Action=onReg
reg.pic=date.gif

crm.Type=TMenuItem
crm.Text=预约
crm.zhText=预约
crm.enText=Appointment of registered
crm.Tip=预约
crm.zhTip=预约
crm.enTip=Appointment of registered
crm.Action=onCrmDr
crm.pic=date.gif

ekt.Type=TMenuItem
ekt.Text=医疗卡
ekt.zhText=医疗卡
ekt.enText=
ekt.Tip=医疗卡
ekt.zhTip=医疗卡
ekt.enTip=
ekt.Action=onEKT
ekt.pic=042.gif

mrshow.Type=TMenuItem
mrshow.Text=病历
mrshow.Tip=病历浏览(Ctrl+W)
mrshow.M=W
mrshow.key=Ctrl+W
mrshow.Action=onShow
mrshow.pic=012.gif

AbnormalReg.Type=TMenuItem
AbnormalReg.Text=非常态门诊
AbnormalReg.zhText=非常态门诊
AbnormalReg.enText=
AbnormalReg.Tip=
AbnormalReg.zhTip=非常态门诊
AbnormalReg.enTip=
AbnormalReg.Action=onAbnormalReg
AbnormalReg.pic=nurse.gif

CallNumber.Type=TMenuItem
CallNumber.Text=叫号
CallNumber.zhText=下一个
CallNumber.enText=NextCall
CallNumber.Tip=
CallNumber.zhTip=下一个
CallNumber.enTip=
CallNumber.Action=onNextCallNo
CallNumber.pic=044.gif

SpecialCase.Type=TMenuItem
SpecialCase.Text=医保特殊
SpecialCase.zhText=医保特殊情况
SpecialCase.enText=
SpecialCase.Tip=
SpecialCase.zhTip=医保特殊情况
SpecialCase.enTip=
SpecialCase.Action=onSpecialCase
SpecialCase.pic=053.gif

INSDrQuery.Type=TMenuItem
INSDrQuery.Text=门特处方
INSDrQuery.zhText=门特处方查询
INSDrQuery.enText=INS Dr Query
INSDrQuery.Tip=门特处方查询
INSDrQuery.zhTip=门特处方查询
INSDrQuery.enTip=INS Dr Query
INSDrQuery.M=I
INSDrQuery.Action=onINSDrQuery
INSDrQuery.Key=Ctrl+I
INSDrQuery.pic=search-1.gif

ClearMenu.Type=TMenuItem
ClearMenu.Text=清剪贴
ClearMenu.Tip=清剪贴
ClearMenu.Action=onClearMenu
ClearMenu.Key=
ClearMenu.pic=001.gif

insMTRegister.Type=TMenuItem
insMTRegister.Text=门特
insMTRegister.zhText=门特登记
insMTRegister.enText=INS Dr Query
insMTRegister.Tip=门特登记
insMTRegister.zhTip=门特登记
insMTRegister.enTip=INS Dr Query
insMTRegister.M=Y
insMTRegister.Action=onMTRegister
insMTRegister.pic=exportword.gif

singledise.Type=TMenuItem
singledise.Text=单病种
singledise.Tip=单病种准入
singledise.Action=onSingleDise
singledise.pic=emr-1.gif

MarkText.type=TMenuItem
MarkText.Text=上下标
MarkText.Tip=上下标
MarkText.M=
MarkText.key=
MarkText.Action=onInsertMarkText
MarkText.pic=Edit.gif

SpecialChars.type=TMenuItem
SpecialChars.Text=特殊符
SpecialChars.Tip=特殊符
SpecialChars.M=
SpecialChars.key=
SpecialChars.Action=onInsertSpecialChars
SpecialChars.pic=Text.gif


onCancel.Type=TMenuItem
onCancel.Text=取消看诊
onCancel.zhText=取消看诊
onCancel.enText=onCancelConsult
onCancel.Tip=取消看诊
onCancel.zhTip=取消看诊
onCancel.enTip=取消看诊
onCancel.M=D
onCancel.key=Ctrl+D
onCancel.Action=onCancelConsult
onCancel.pic=delete.gif


