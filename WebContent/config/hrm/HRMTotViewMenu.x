 #
  # Title: HRM总检
  #
  # Description:HRM总检
  #
  # Copyright: JavaHis (c) 2008
  #
  # @author ehui
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Edit;Window
UI.button=save;query;unfold;|;crawl;crawls;|;InsertLCSJ;InsertPY;print;|;ClearMenu;|;DelTable;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

Edit.Type=TMenu
Edit.Text=编辑
Edit.M=E
Edit.Item=DelTable

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;query;unfold;|;crawl;crawls;|;InsertLCSJ;InsertPY;print;|;ClearMenu;|;DelTable;|;clear;|;close

save.Type=TMenuItem
save.Text=保存
save.Tip=保存
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

query.Type=TMenuItem
query.Text=查询
query.Tip=查询
query.M=N
query.key=F5
query.Action=onQuery
query.pic=query.gif

unfold.Type=TMenuItem
unfold.Text=总检展开
unfold.Tip=总检展开
unfold.M=S
unfold.key=Ctrl+U
unfold.Action=onUnfold
unfold.pic=048.gif

crawl.Type=TMenuItem
crawl.Text=总检抓取
crawl.Tip=总检抓取
crawl.M=S
crawl.key=Ctrl+W
crawl.Action=onCrawl
crawl.pic=008.gif

crawls.Type=TMenuItem
crawls.Text=总检部分抓取
crawls.Tip=总检部分抓取
crawls.M=S
crawls.key=Ctrl+W
crawls.Action=onCrawls
crawls.pic=convert.gif

InsertLCSJ.Type=TMenuItem
InsertLCSJ.Text=临床数据
InsertLCSJ.Tip=临床数据
InsertLCSJ.M=S
InsertLCSJ.key=Ctrl+J
InsertLCSJ.Action=onInsertLCSJ
InsertLCSJ.pic=053.gif

InsertPY.Type=TMenuItem
InsertPY.Text=片语
InsertPY.Tip=片语
InsertPY.M=S
InsertPY.key=Ctrl+Y
InsertPY.Action=onInsertPY
InsertPY.pic=Line.gif

print.Type=TMenuItem
print.Text=打印
print.Tip=打印
print.M=C
print.Action=onPrint
print.pic=print.gif

ClearMenu.Type=TMenuItem
ClearMenu.Text=清空剪贴板
ClearMenu.Tip=清空剪贴板
ClearMenu.M=v
ClearMenu.Action=onClearMenu
ClearMenu.Key=
ClearMenu.pic=001.gif

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
close.Action=onClose
close.pic=close.gif

DelTable.type=TMenuItem
DelTable.Text=删除表格
DelTable.Tip=删除表格
DelTable.M=I
DelTable.key=
DelTable.Action=onDelTable
DelTable.pic=delete.gif
