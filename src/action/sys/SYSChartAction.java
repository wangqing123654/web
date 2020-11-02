package action.sys;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.apache.commons.lang.StringUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.servlet.ServletUtilities;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.ui.TextAnchor;

import com.dongyang.data.TParm;

/**
 * <p>
 * Title: ͼ�α������ɹ���
 * </p>
 * 
 * <p>
 * Description: ͼ�α������ɹ���
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2015
 * </p>
 * 
 * <p>
 * Company: Bluecore
 * </p>
 * 
 * @author wangb 2015.9.18
 * @version 1.0
 */
public class SYSChartAction {
	
	/**
	 * ����ͳ��ͼ
	 * 
	 * @param parm
	 */
	public TParm createChart(TParm parm) {
		TParm result = new TParm();
		
		// ����ͳ��ͼ��
		JFreeChart chart = getFreeChart(parm);
		
		if (chart == null) {
			result.setErr(-1, "��װͳ�����ݴ���");
			return result;
		}
		
		try {
			String name = ServletUtilities.saveChartAsJPEG(chart, 1200, 800, null);
			result.setData("FILE_NAME", name);
		} catch (Exception e) {
			e.printStackTrace();
			result.setErr(-1, e.getMessage());
		}
		
		return result;
	}
	
	/**
	 * ����ͳ��ͼ��
	 * 
	 * @param parm
	 * @return
	 */
	public JFreeChart getFreeChart(TParm parm) {
		JFreeChart chart = null;
		// ͳ������
		String type = parm.getValue("TYPE");
		// ����
		String title = parm.getValue("TITLE");
		TParm parmData = parm.getParm("CHART_DATA");
		// �����ǩ
		String XAxisLabel = parm.getValue("X_AXIS_LABLE");
		// �����ǩ
		String YAxisLabel = parm.getValue("Y_AXIS_LABLE");
		
		if (null == parmData) {
			return chart;
		}
		
		// ��״ͼ
		if (StringUtils.equals("1", type)) {
			chart = ChartFactory.createPieChart(title, getPieDataSet(parmData), true,
					false, false);

			// ��������
			PiePlot plot = (PiePlot) chart.getPlot();
			plot.setLabelFont(new Font("����", Font.PLAIN, 20));
			TextTitle textTitle = chart.getTitle();
			textTitle.setFont(new Font("����", Font.PLAIN, 20));
			chart.getLegend().setItemFont(new Font("����", Font.PLAIN, 20));
			
			plot.setNoDataMessage("�޶�Ӧ�����ݣ������²�ѯ��");
			plot.setNoDataMessageFont(new Font("����", Font.PLAIN, 20));// ����Ĵ�С
			plot.setNoDataMessagePaint(Color.RED);// ������ɫ
			
			// ͼƬ����ʾ�ٷֱ�:�Զ��巽ʽ��{0} ��ʾѡ� {1} ��ʾ��ֵ�� {2} ��ʾ��ռ���� ,С�������λ
	        plot.setLabelGenerator(new StandardPieSectionLabelGenerator(
	                "{0}={1}({2})", NumberFormat.getNumberInstance(),
	                new DecimalFormat("0.00%")));
	        // ͼ����ʾ�ٷֱ�:�Զ��巽ʽ�� {0} ��ʾѡ� {1} ��ʾ��ֵ�� {2} ��ʾ��ռ����
	        plot.setLegendLabelGenerator(new StandardPieSectionLabelGenerator(
	                "{0}={1}({2})"));
		} else if (StringUtils.equals("2", type)) {
			chart = ChartFactory.createBarChart(title, // ͼ�����
					XAxisLabel, // Ŀ¼�����ʾ��ǩ
					YAxisLabel, // ��ֵ�����ʾ��ǩ
					getBarDataSet(parmData), // ���ݼ�
					PlotOrientation.VERTICAL, // ͼ����ˮƽ����ֱ
					true, // �Ƿ���ʾͼ��(���ڼ򵥵���״ͼ������ false)
					false, // �Ƿ����ɹ���
					false // �Ƿ����� URL ����
					);
			// ��������
			CategoryPlot categoryplot = (CategoryPlot) chart.getPlot();
			NumberAxis numberaxis = (NumberAxis) categoryplot.getRangeAxis();
			CategoryAxis domainAxis = categoryplot.getDomainAxis();
			TextTitle textTitle = chart.getTitle();
			textTitle.setFont(new Font("����", Font.PLAIN, 20));
			domainAxis.setTickLabelFont(new Font("sans-serif", Font.PLAIN, 20));
			domainAxis.setLabelFont(new Font("����", Font.PLAIN, 20));
			numberaxis.setTickLabelFont(new Font("sans-serif", Font.PLAIN, 20));
			numberaxis.setLabelFont(new Font("����", Font.PLAIN, 20));
			chart.getLegend().setItemFont(new Font("����", Font.PLAIN, 20));
			
			// ��ʾÿ��������ֵ�����޸ĸ����ֵ���������
			BarRenderer renderer = new BarRenderer();
			renderer
					.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
			renderer.setBaseItemLabelsVisible(true);
			// Ĭ�ϵ�������ʾ�������У�ͨ����������������ֵ���ʾ
			// ע�⣬�˾�ܹؼ������޴˾䣬�����ֵ���ʾ�Ḳ�ǣ���������û����ʾ����������
			renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(
					ItemLabelAnchor.OUTSIDE12, TextAnchor.BASELINE_LEFT));
			renderer.setItemLabelAnchorOffset(10D);
			renderer.setBaseItemLabelFont(new Font("����", Font.PLAIN, 20));
			
			categoryplot.setRenderer(renderer);
			categoryplot.setNoDataMessage("�޶�Ӧ�����ݣ������²�ѯ��");
			categoryplot.setNoDataMessageFont(new Font("����", Font.PLAIN, 20));// ����Ĵ�С
			categoryplot.setNoDataMessagePaint(Color.RED);// ������ɫ
		} else if (StringUtils.equals("3", type)) {
			DefaultCategoryDataset dataset = getLineDataSet(parmData);
			// ��ͨ����ͼ
			chart = ChartFactory.createLineChart(title, XAxisLabel, YAxisLabel, dataset,
					PlotOrientation.VERTICAL, // ���Ʒ���
					true, // ��ʾͼ��
					true, // ���ñ�׼������
					false); // �Ƿ����ɳ�����

			Font font = new Font("����", Font.PLAIN, 20);
			chart.getTitle().setFont(font); // ���ñ�������
			chart.getLegend().setItemFont(font);// ����ͼ���������
			// chart.setBackgroundPaint();// ���ñ���ɫ

			// ��ȡ��ͼ������
			CategoryPlot plot = chart.getCategoryPlot();
			plot.setBackgroundPaint(Color.LIGHT_GRAY); // ���û�ͼ������ɫ
			plot.setRangeGridlinePaint(Color.WHITE); // ����ˮƽ���򱳾�����ɫ
			plot.setRangeGridlinesVisible(true);// �����Ƿ���ʾˮƽ���򱳾���,Ĭ��ֵΪtrue
			plot.setDomainGridlinePaint(Color.WHITE); // ���ô�ֱ���򱳾�����ɫ
			plot.setDomainGridlinesVisible(true); // �����Ƿ���ʾ��ֱ���򱳾���,Ĭ��ֵΪfalse

			CategoryAxis domainAxis = plot.getDomainAxis();
			domainAxis.setLabelFont(font); // ���ú�������
			domainAxis.setTickLabelFont(font);// ������������ֵ����
			domainAxis.setLowerMargin(0.01);// ��߾� �߿����
			domainAxis.setUpperMargin(0.06);// �ұ߾� �߿����,��ֹ���ߵ�һ�����ݿ����������ᡣ
			domainAxis.setMaximumCategoryLabelLines(2);

			ValueAxis rangeAxis = plot.getRangeAxis();
			rangeAxis.setLabelFont(font);
			rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());// Y����ʾ����
			rangeAxis.setAutoRangeMinimumSize(1); // ��С���
			rangeAxis.setUpperMargin(0.18);// �ϱ߾�,��ֹ����һ�����ݿ����������ᡣ
			rangeAxis.setLowerBound(0); // ��Сֵ��ʾ0
			rangeAxis.setAutoRange(false); // ���Զ�����Y������
			rangeAxis.setTickMarkStroke(new BasicStroke(1.6f)); // ���������Ǵ�С
			rangeAxis.setTickMarkPaint(Color.BLACK); // ������������ɫ

			// ��ȡ���߶���
			LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot
					.getRenderer();
			BasicStroke realLine = new BasicStroke(1.8f); // ����ʵ��
			// ��������
			float dashes[] = { 5.0f };
			BasicStroke brokenLine = new BasicStroke(2.2f, // ������ϸ
					BasicStroke.CAP_ROUND, // �˵���
					BasicStroke.JOIN_ROUND, // �۵���
					8f, dashes, 0.6f);
			for (int i = 0; i < dataset.getRowCount(); i++) {
				if (i % 2 == 0)
					renderer.setSeriesStroke(i, realLine); // ����ʵ�߻���
				else
					renderer.setSeriesStroke(i, brokenLine); // �������߻���
			}

			plot.setNoDataMessage("�޶�Ӧ�����ݣ������²�ѯ��");
			plot.setNoDataMessageFont(font);// ����Ĵ�С
			plot.setNoDataMessagePaint(Color.RED);// ������ɫ
		}

		return chart;
	}

	/**
	 * ��ȡ��״ͼ������ݼ�����
	 * 
	 * @return
	 */
	private static DefaultPieDataset getPieDataSet(TParm parm) {
		DefaultPieDataset dataset = new DefaultPieDataset();
		int count = parm.getCount("KEY");
		if (count > 0) {
			for (int i = 0; i < count; i++) {
				dataset.setValue(parm.getValue("KEY", i), parm.getDouble(
						"VALUE", i));
			}
		}
		return dataset;
	}

	/**
	 * ��ȡ��״ͼ������ݼ�����
	 * 
	 * @return
	 */
	private static CategoryDataset getBarDataSet(TParm parm) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		int count = parm.getCount("ROW_KEY");
		if (count > 0) {
			for (int i = 0; i < count; i++) {
				dataset.addValue(parm.getDouble("VALUE", i), parm.getValue(
						"ROW_KEY", i), parm.getValue("COLUMN_KEY", i));
			}
		}
		
		return dataset;
	}

	/**
	 * ��ȡ����ͼ������ݼ�����
	 */
	private static DefaultCategoryDataset getLineDataSet(TParm parm) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		int count = parm.getCount("ROW_KEY");
		if (count > 0) {
			for (int i = 0; i < count; i++) {
				dataset.addValue(parm.getDouble("VALUE", i), parm.getValue(
						"ROW_KEY", i), parm.getValue("COLUMN_KEY", i));
			}
		}

		return dataset;
	}
}
