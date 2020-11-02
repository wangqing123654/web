package com.javahis.util;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.image.BufferedImage;


/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class ClipboardTool {
    public ClipboardTool() {
    }

    public static void run(String gifName) throws Exception {
        ClipboardTool tool = new ClipboardTool();
        Image image = tool.getImageFromClipboard();
        tool.compressPic(image, gifName);
    }

    // ͼƬ����
    public void compressPic(Image img, String gifName) throws Exception {

        // �ж�ͼƬ��ʽ�Ƿ���ȷ
        if (img == null || img.getWidth(null) == -1) {
            throw new Exception("��ǰ��������û��ͼƬƬ�Ρ�����Ƭ�����ɷ�ʽ���ܸ���ĳ��ͼƬ�ļ����ǽ�ȡ��Ļ�ȡ�");

        }
        int newWidth;
        int newHeight;
        newWidth = img.getWidth(null); // �����ͼƬ���
        newHeight = img.getHeight(null); // �����ͼƬ�߶�
        BufferedImage tag = new BufferedImage( (int) newWidth, (int) newHeight,
                                              BufferedImage.TYPE_INT_RGB);

        /*
         * Image.SCALE_SMOOTH �������㷨 ��������ͼƬ��ƽ���ȵ� ���ȼ����ٶȸ� ���ɵ�ͼƬ�����ȽϺ� ���ٶ���
         */
        tag.getGraphics().drawImage(
            img.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH),
            0, 0, null);

        jpgToGif(tag, gifName);

    }

    /**
     * �������ȡͼƬ
     *
     * @return
     * @throws Exception
     */
    public Image getImageFromClipboard() throws Exception {
        Clipboard sysc = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable cc = sysc.getContents(null);

        if (cc == null) {
            return null;
        }
        else if (cc.isDataFlavorSupported(DataFlavor.imageFlavor)) {
            return (Image) cc.getTransferData(DataFlavor.imageFlavor);
        }
        return null;
    }

    /**
     * jpg��ʽתgif
     *
     * @param image
     *            BufferedImage ����
     * @param newPic
     *            ��ͼƬ�洢·��
     */
    public synchronized void jpgToGif(BufferedImage image, String newPic) {
        try {
            AnimatedGifEncoder e = new AnimatedGifEncoder();
            e.setRepeat(0);
            e.start(newPic);

            e.setDelay(200); // ���ò��ŵ��ӳ�ʱ��
            e.addFrame(image); // ��ӵ�֡��
            e.finish(); // ˢ���κ�δ�������ݣ����ر�����ļ�
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


}
