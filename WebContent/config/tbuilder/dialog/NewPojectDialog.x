<extends=%ROOT%\config\tbuilder\dialog\TDialog.x>
UI.Title=�½����� Project
UI.ControlClassName=com.tbuilder.project.NewProjectDialogControl
UI.focusList=Name;Path;OK

WorkPanel.Item=LCaption;&
	       LName;Name;&
	       LPath;Path;&
	       SelectPath


//OK.enabled=false
//Cancel.enabled=false
//Path.enabled=false
//Name.enabled=false

LCaption.Type=TLabel
LCaption.Text=Ϊ���½���TBuilder����ѡ��һ�����ƺ�·��
LCaption.x=10
LCaption.y=10
LCaption.width=300
LCaption.height=20

LName.Type=TLabel
LName.Text=����:
LName.x=10
LName.y=70
LName.width=40
LName.height=20

Name.Type=TTextField
Name.Tip=��Ŀ����
Name.Text=tbuilder.tpx
Name.x=50
Name.y=70
Name.width=200
Name.height=20


LPath.Type=TLabel
LPath.Text=·��:
LPath.x=10
LPath.y=95
LPath.width=40
LPath.height=20

Path.Type=TTextField
Path.Text=%ROOT%\tbuilder
Path.Tip=��Ŀ·��
Path.x=50
Path.y=95
Path.width=200
Path.height=20

SelectPath.Type=TButton
SelectPath.Text=ѡ��Ŀ¼
SelectPath.Tip=����ѡ��Ŀ¼�ĶԻ���
SelectPath.x=150
SelectPath.y=120
SelectPath.width=80
SelectPath.height=20
SelectPath.Action=onSelectPath