#############################################
# <p>Title:�ż��ﻤʿ����վMenu </p>
#
# <p>Description:�ż��ﻤʿ����վMenu </p>
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
UI.button=query;|;clear;|;detach;|;patdata;|;barcode;|;body;|;planrep;|;docplan;|;opdrecord;|;checkrep;|;testrep;|;supcharge;|;close;

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=Refresh;query;|;clear;|;close;

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

Refresh.Type=TMenuItem
Refresh.Text=ˢ��
Refresh.Tip=ˢ��
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif



clear.Type=TMenuItem
clear.Text=���
clear.Tip=���
clear.M=C
clear.Action=onClear
clear.pic=clear.gif

detach.Type=TMenuItem
detach.Text=����
detach.Tip=����
detach.M=
detach.key=
detach.Action=onDetach
detach.pic=convert.gif

patdata.Type=TMenuItem
patdata.Text=��������
patdata.Tip=��������
patdata.M=
patdata.key=
patdata.Action=onPatdata
patdata.pic=038.gif

barcode.Type=TMenuItem
barcode.Text=���������ӡ
barcode.Tip=���������ӡ
barcode.M=X
barcode.key=
barcode.Action=onBarcode
barcode.pic=barcode.gif

body.Type=TMenuItem
body.Text=�����ɼ�
body.Tip=�����ɼ�
body.M=X
body.key=
body.Action=onBody
body.pic=new.gif

planrep.Type=TMenuItem
planrep.Text=�������
planrep.Tip=�������
planrep.M=
planrep.key=
planrep.Action=onPlanrep
planrep.pic=detail-1.gif

docplan.Type=TMenuItem
docplan.Text=ҽ������
docplan.Tip=ҽ������
docplan.M=
docplan.key=
docplan.Action=onDocplan
docplan.pic=detail.gif

checkrep.Type=TMenuItem
checkrep.Text=���鱨��
checkrep.Tip=���鱨��
checkrep.M=
checkrep.key=
checkrep.Action=onCheckrep
checkrep.pic=Lis.gif

testrep.Type=TMenuItem
testrep.Text=��鱨��
testrep.Tip=��鱨��
testrep.M=
testrep.key=
testrep.Action=onTestrep
testrep.pic=emr-2.gif

supcharge.Type=TMenuItem
supcharge.Text=����Ƽ�
supcharge.Tip=����Ƽ�
supcharge.M=
supcharge.key=
supcharge.Action=onSupcharge
supcharge.pic=bill.gif

psmanage.Type=TMenuItem
psmanage.Text=Ƥ��
psmanage.Tip=Ƥ��
psmanage.M=
psmanage.key=
psmanage.Action=onPSManage
psmanage.pic=phl.gif

opdrecord.Type=TMenuItem
opdrecord.Text=�����¼
opdrecord.Tip=�����¼
opdrecord.M=
opdrecord.key=
opdrecord.Action=onOPDRecord
opdrecord.pic=010.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

