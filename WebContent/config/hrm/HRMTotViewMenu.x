 #
  # Title: HRM�ܼ�
  #
  # Description:HRM�ܼ�
  #
  # Copyright: JavaHis (c) 2008
  #
  # @author ehui
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Edit;Window
UI.button=save;query;unfold;|;crawl;crawls;|;InsertLCSJ;InsertPY;print;|;ClearMenu;|;DelTable;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

Edit.Type=TMenu
Edit.Text=�༭
Edit.M=E
Edit.Item=DelTable

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;query;unfold;|;crawl;crawls;|;InsertLCSJ;InsertPY;print;|;ClearMenu;|;DelTable;|;clear;|;close

save.Type=TMenuItem
save.Text=����
save.Tip=����
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ
query.M=N
query.key=F5
query.Action=onQuery
query.pic=query.gif

unfold.Type=TMenuItem
unfold.Text=�ܼ�չ��
unfold.Tip=�ܼ�չ��
unfold.M=S
unfold.key=Ctrl+U
unfold.Action=onUnfold
unfold.pic=048.gif

crawl.Type=TMenuItem
crawl.Text=�ܼ�ץȡ
crawl.Tip=�ܼ�ץȡ
crawl.M=S
crawl.key=Ctrl+W
crawl.Action=onCrawl
crawl.pic=008.gif

crawls.Type=TMenuItem
crawls.Text=�ܼ첿��ץȡ
crawls.Tip=�ܼ첿��ץȡ
crawls.M=S
crawls.key=Ctrl+W
crawls.Action=onCrawls
crawls.pic=convert.gif

InsertLCSJ.Type=TMenuItem
InsertLCSJ.Text=�ٴ�����
InsertLCSJ.Tip=�ٴ�����
InsertLCSJ.M=S
InsertLCSJ.key=Ctrl+J
InsertLCSJ.Action=onInsertLCSJ
InsertLCSJ.pic=053.gif

InsertPY.Type=TMenuItem
InsertPY.Text=Ƭ��
InsertPY.Tip=Ƭ��
InsertPY.M=S
InsertPY.key=Ctrl+Y
InsertPY.Action=onInsertPY
InsertPY.pic=Line.gif

print.Type=TMenuItem
print.Text=��ӡ
print.Tip=��ӡ
print.M=C
print.Action=onPrint
print.pic=print.gif

ClearMenu.Type=TMenuItem
ClearMenu.Text=��ռ�����
ClearMenu.Tip=��ռ�����
ClearMenu.M=v
ClearMenu.Action=onClearMenu
ClearMenu.Key=
ClearMenu.pic=001.gif

clear.Type=TMenuItem
clear.Text=���
clear.Tip=���
clear.M=C
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

DelTable.type=TMenuItem
DelTable.Text=ɾ�����
DelTable.Tip=ɾ�����
DelTable.M=I
DelTable.key=
DelTable.Action=onDelTable
DelTable.pic=delete.gif
