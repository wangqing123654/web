<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;query;clear;|;delTableRow;|;mrshow;|;planReport;|;cxMrshow;|pdf;|;medApplyNo;merge;|;singledise;|;close


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
File.Item=save;query;clear;|;delTableRow;|;mrshow;|;planReport;|;cxMrshow;|pdf;|;medApplyNo;merge;|;singledise;|;close


query.Type=TMenuItem
query.Text=查询
query.zhText=查询
query.enText=Query
query.Tip=查询
query.zhTip=查询
query.enTip=Query
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

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

clear.Type=TMenuItem
clear.Text=清空
clear.zhText=清空
clear.enText=Clear
clear.Tip=清空
clear.zhTip=清空
clear.enTip=Clear
clear.M=C
clear.Action=onClear
clear.pic=clear.gif

delTableRow.Type=TMenuItem
delTableRow.Text=删除医嘱
delTableRow.zhText=删除医嘱
delTableRow.enText=Delete
delTableRow.Tip=删除医嘱
delTableRow.zhTip=删除医嘱
delTableRow.enTip=Delete
delTableRow.M=D
delTableRow.Action=onDelRow
delTableRow.pic=delete.gif

mrshow.Type=TMenuItem
mrshow.Text=病历浏览
mrshow.Tip=病历浏览(Ctrl+W)
mrshow.M=W
mrshow.key=Ctrl+W
mrshow.Action=onShow
mrshow.pic=012.gif

cxMrshow.Type=TMenuItem
cxMrshow.Text=病历浏览
cxMrshow.Tip=病历浏览(Ctrl+Q)
cxMrshow.M=Q
cxMrshow.key=Ctrl+Q
cxMrshow.Action=onCxShow
cxMrshow.pic=038.gif

planReport.Type=TMenuItem
planReport.Text=报告进度
planReport.Tip=报告进度
planReport.M=
planReport.key=
planReport.Action=onPlanrep
planReport.pic=detail-1.gif

close.Type=TMenuItem
close.Text=退出
close.zhText=退出
close.enText=Quit
close.Tip=退出
close.zhTip=退出
close.enTip=Quit
close.M=X
close.key=Alt+F4
close.Action=onClosePanel
close.pic=close.gif

Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.Tip=刷新
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

pdf.Type=TMenuItem
pdf.Text=病历整理
pdf.zhText=病历整理
pdf.enText=病历整理
pdf.Tip=病历整理
pdf.zhTip=病历整理
pdf.enTip=病历整理
pdf.M=X
pdf.Action=onSubmitPDF
pdf.pic=005.gif

merge.Type=TMenuItem
merge.Text=单病种合并
merge.Tip=单病种合并
merge.M=M
merge.key=
merge.Action=onMerge
merge.pic=sta-1.gif

singledise.Type=TMenuItem
singledise.Text=单病种准入
singledise.Tip=单病种准入
singledise.M=S
singledise.Action=onSingleDise
singledise.pic=emr-1.gif

medApplyNo.Type=TMenuItem
medApplyNo.Text=检验条码
medApplyNo.Tip=打印条码
medApplyNo.M=C
medApplyNo.Action=onMedApplyPrint
medApplyNo.pic=barCode.gif