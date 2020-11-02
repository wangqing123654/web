<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;query;|;clear;|;Newprint;|;categoryPrint;|;temporaryPrint;|;medPrint;|;medApplyNo;|;send;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;delete;Refresh;query;Newprint;categoryPrint;temporaryPrint;medPrint;medApplyNo;|;send;|;clear;|;close

save.Type=TMenuItem
save.Text=保存
save.Tip=保存
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

send.Type=TMenuItem
send.Text=重送
send.Tip=重送
send.M=O
send.Action=onReSendGYPha
send.pic=Commit.gif


query.Type=TMenuItem
query.Text=查询
query.Tip=查询
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

Newprint.Type=TMenuItem
Newprint.Text=多人打印
Newprint.Tip=多人打印
Newprint.M=PN
Newprint.Action=onPrintExe
Newprint.pic=print-1.gif

medPrint.Type=TMenuItem
medPrint.Text=取药单打印
medPrint.Tip=取药单打印
medPrint.Action=onDispenseSheet
medPrint.pic=print-2.gif

categoryPrint.Type=TMenuItem
categoryPrint.Text=分类执行单打印
categoryPrint.Tip=分类执行单打印
categoryPrint.M=PN
categoryPrint.Action=onCategoryPrint
categoryPrint.pic=print-1.gif

temporaryPrint.Type=TMenuItem
temporaryPrint.Text=汇总执行单打印
temporaryPrint.Tip=汇总执行单打印
temporaryPrint.M=PN
temporaryPrint.Action=onTemporaryPrint
temporaryPrint.pic=print-1.gif

clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空
clear.M=C
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出
close.M=X
close.key=Alt+F4
close.Action=onClosePanel
close.pic=close.gif

medApplyNo.Type=TMenuItem
medApplyNo.Text=检验条码
medApplyNo.Tip=打印条码
medApplyNo.M=C
medApplyNo.Action=onMedApplyPrint
medApplyNo.pic=barCode.gif