package com.javahis.ui.emr;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import javax.swing.BorderFactory;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TRootPanel;
import com.dongyang.ui.TWindow;

/**
 * <p>Title:�����ַ����� </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright:Copyright (c) 2014 </p>
 *
 * <p>Company:Bluecore </p>
 *
 * @author wanglong 2014.9.8
 * @version 1.0
 */
public class EMRSpecialCharsControl extends TControl {

    private TParm parameter;
//    private int x_start = 0;//����X��ʼ����
//    private int y = 0;// ����Y����
//    private int x_offset = 160;// X����ƫ����
//    private int x_end = 0;
//    private Timer timer;
    private HashSet<TLabel> labelSet;

    /**
     * ��ʼ��
     */
    public void onInit() {
        super.onInit();
        parameter = this.getInputParm();
        this.initUI();
    }

    /**
	 * ��ʼ������
	 */
    private void initUI() {
        TWindow window = (TWindow) this.getComponent("UI");
        window.setAlwaysOnTop(true);
//        x_start = window.getX();
//        y = window.getY();
//        x_end = x_start + x_offset;
//        timer = new Timer(15, new TimerActionListener(window));
//        timer.start();
        labelSet = new HashSet<TLabel>();
        TRootPanel rootPanel = (TRootPanel) this.getComponent("ROOT_PANEL");
        rootPanel.setLayout(new GridLayout(0, 5, 10, 10));
        String sql = "SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID = 'EMR_SPECIAL_CHAR'";
        TParm chars = new TParm(TJDODBTool.getInstance().select(sql));
        Border lb = BorderFactory.createLineBorder(Color.GREEN);
        for (int i = 0; i < chars.getCount(); i++) {
            TLabel label = new TLabel();
            label.setBorder(lb);
            label.setOpaque(true);
            label.setBackground(Color.WHITE);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setFont(new Font("΢���ź�", Font.BOLD, 16));
            label.setText(chars.getValue("CHN_DESC", i));
            label.addMouseListener(new MouseAdapter() {

                public void mouseClicked(MouseEvent e) {
                    if (e.getButton() == 1) {
                        for (TLabel tlb : labelSet) {
                            tlb.setForeground(Color.BLACK);
                            tlb.setBackground(Color.WHITE);
                        }
                        TLabel label = ((TLabel) e.getSource());
                        if (e.getClickCount() == 1) {//����
                            label.setForeground(Color.YELLOW);
                            label.setBackground(Color.GREEN);
                            parameter.runListener("onReturnContent", label.getText());
                        } 
//                        else if (e.getClickCount() == 2) {//˫��
//                            label.setForeground(Color.YELLOW);
//                            label.setBackground(Color.GREEN);
//                        }
                    }
                }
            });
            rootPanel.add(label);
            labelSet.add(label);
        }

    }

    /**
     * ��ʱ��������
     * @author Administrator
     *
     */
//    class TimerActionListener implements ActionListener {
//
//        private TWindow window;
//
//        public TimerActionListener(TWindow frame) {
//            this.window = frame;
//        }
//
//        public void actionPerformed(ActionEvent e) {
//            if (x_start < x_end) {
//                x_start += 4;
//                window.setLocation(x_start, y);
//            } else {
//                window.setLocation(x_end, y);
//                timer.stop();
//            }
//        }
//    }
}
