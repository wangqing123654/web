 #
  # Title: 拨补作业
  #
  # Description:拨补作业
  #
  # Copyright: JavaHis (c) 2008
  #
  # @author zhangy 2009.05.06
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;clear;|;exportxls;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=query;|;clear;|;exportxls;|;close

save.Type=TMenuItem
save.Text=结算
save.Tip=结算(Ctrl+S)
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif


query.Type=TMenuItem
query.Text=查询
query.Tip=查询(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif


clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空
clear.M=C
clear.Action=onClear
clear.pic=clear.gif


exportxml.Type=TMenuItem
exportxml.Text=导出结算单(XML)
exportxml.Tip=导出结算单(XML)
exportxml.M=P
exportxml.Action=onExportXml
exportxml.pic=export.gif

exportxls.Type=TMenuItem
exportxls.Text=导出Excel
exportxls.Tip=导出Excel
exportxls.M=P
exportxls.Action=onExportXls
exportxls.pic=export.gif

print.Type=TMenuItem
print.Text=打印
print.Tip=打印
print.M=P
print.Action=onPrint
print.pic=print.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空
clear.M=C
clear.Action=onClear
clear.pic=clear.gif



