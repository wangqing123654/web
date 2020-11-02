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
 * Title: 图形报表生成工具
 * </p>
 * 
 * <p>
 * Description: 图形报表生成工具
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
	 * 生成统计图
	 * 
	 * @param parm
	 */
	public TParm createChart(TParm parm) {
		TParm result = new TParm();
		
		// 构建统计图形
		JFreeChart chart = getFreeChart(parm);
		
		if (chart == null) {
			result.setErr(-1, "组装统计数据错误");
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
	 * 构建统计图形
	 * 
	 * @param parm
	 * @return
	 */
	public JFreeChart getFreeChart(TParm parm) {
		JFreeChart chart = null;
		// 统计类型
		String type = parm.getValue("TYPE");
		// 标题
		String title = parm.getValue("TITLE");
		TParm parmData = parm.getParm("CHART_DATA");
		// 横轴标签
		String XAxisLabel = parm.getValue("X_AXIS_LABLE");
		// 纵轴标签
		String YAxisLabel = parm.getValue("Y_AXIS_LABLE");
		
		if (null == parmData) {
			return chart;
		}
		
		// 饼状图
		if (StringUtils.equals("1", type)) {
			chart = ChartFactory.createPieChart(title, getPieDataSet(parmData), true,
					false, false);

			// 中文乱码
			PiePlot plot = (PiePlot) chart.getPlot();
			plot.setLabelFont(new Font("黑体", Font.PLAIN, 20));
			TextTitle textTitle = chart.getTitle();
			textTitle.setFont(new Font("黑体", Font.PLAIN, 20));
			chart.getLegend().setItemFont(new Font("宋体", Font.PLAIN, 20));
			
			plot.setNoDataMessage("无对应的数据，请重新查询。");
			plot.setNoDataMessageFont(new Font("黑体", Font.PLAIN, 20));// 字体的大小
			plot.setNoDataMessagePaint(Color.RED);// 字体颜色
			
			// 图片中显示百分比:自定义方式，{0} 表示选项， {1} 表示数值， {2} 表示所占比例 ,小数点后两位
	        plot.setLabelGenerator(new StandardPieSectionLabelGenerator(
	                "{0}={1}({2})", NumberFormat.getNumberInstance(),
	                new DecimalFormat("0.00%")));
	        // 图例显示百分比:自定义方式， {0} 表示选项， {1} 表示数值， {2} 表示所占比例
	        plot.setLegendLabelGenerator(new StandardPieSectionLabelGenerator(
	                "{0}={1}({2})"));
		} else if (StringUtils.equals("2", type)) {
			chart = ChartFactory.createBarChart(title, // 图表标题
					XAxisLabel, // 目录轴的显示标签
					YAxisLabel, // 数值轴的显示标签
					getBarDataSet(parmData), // 数据集
					PlotOrientation.VERTICAL, // 图表方向：水平、垂直
					true, // 是否显示图例(对于简单的柱状图必须是 false)
					false, // 是否生成工具
					false // 是否生成 URL 链接
					);
			// 中文乱码
			CategoryPlot categoryplot = (CategoryPlot) chart.getPlot();
			NumberAxis numberaxis = (NumberAxis) categoryplot.getRangeAxis();
			CategoryAxis domainAxis = categoryplot.getDomainAxis();
			TextTitle textTitle = chart.getTitle();
			textTitle.setFont(new Font("黑体", Font.PLAIN, 20));
			domainAxis.setTickLabelFont(new Font("sans-serif", Font.PLAIN, 20));
			domainAxis.setLabelFont(new Font("宋体", Font.PLAIN, 20));
			numberaxis.setTickLabelFont(new Font("sans-serif", Font.PLAIN, 20));
			numberaxis.setLabelFont(new Font("黑体", Font.PLAIN, 20));
			chart.getLegend().setItemFont(new Font("宋体", Font.PLAIN, 20));
			
			// 显示每个柱的数值，并修改该数字的字体属性
			BarRenderer renderer = new BarRenderer();
			renderer
					.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
			renderer.setBaseItemLabelsVisible(true);
			// 默认的数字显示在柱子中，通过以下两句调整数字的显示
			// 注意，此句很关键，若无此句，那数字的显示会覆盖，给人数字没有显示出来的问题
			renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(
					ItemLabelAnchor.OUTSIDE12, TextAnchor.BASELINE_LEFT));
			renderer.setItemLabelAnchorOffset(10D);
			renderer.setBaseItemLabelFont(new Font("宋体", Font.PLAIN, 20));
			
			categoryplot.setRenderer(renderer);
			categoryplot.setNoDataMessage("无对应的数据，请重新查询。");
			categoryplot.setNoDataMessageFont(new Font("黑体", Font.PLAIN, 20));// 字体的大小
			categoryplot.setNoDataMessagePaint(Color.RED);// 字体颜色
		} else if (StringUtils.equals("3", type)) {
			DefaultCategoryDataset dataset = getLineDataSet(parmData);
			// 普通折线图
			chart = ChartFactory.createLineChart(title, XAxisLabel, YAxisLabel, dataset,
					PlotOrientation.VERTICAL, // 绘制方向
					true, // 显示图例
					true, // 采用标准生成器
					false); // 是否生成超链接

			Font font = new Font("黑体", Font.PLAIN, 20);
			chart.getTitle().setFont(font); // 设置标题字体
			chart.getLegend().setItemFont(font);// 设置图例类别字体
			// chart.setBackgroundPaint();// 设置背景色

			// 获取绘图区对象
			CategoryPlot plot = chart.getCategoryPlot();
			plot.setBackgroundPaint(Color.LIGHT_GRAY); // 设置绘图区背景色
			plot.setRangeGridlinePaint(Color.WHITE); // 设置水平方向背景线颜色
			plot.setRangeGridlinesVisible(true);// 设置是否显示水平方向背景线,默认值为true
			plot.setDomainGridlinePaint(Color.WHITE); // 设置垂直方向背景线颜色
			plot.setDomainGridlinesVisible(true); // 设置是否显示垂直方向背景线,默认值为false

			CategoryAxis domainAxis = plot.getDomainAxis();
			domainAxis.setLabelFont(font); // 设置横轴字体
			domainAxis.setTickLabelFont(font);// 设置坐标轴标尺值字体
			domainAxis.setLowerMargin(0.01);// 左边距 边框距离
			domainAxis.setUpperMargin(0.06);// 右边距 边框距离,防止最后边的一个数据靠近了坐标轴。
			domainAxis.setMaximumCategoryLabelLines(2);

			ValueAxis rangeAxis = plot.getRangeAxis();
			rangeAxis.setLabelFont(font);
			rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());// Y轴显示整数
			rangeAxis.setAutoRangeMinimumSize(1); // 最小跨度
			rangeAxis.setUpperMargin(0.18);// 上边距,防止最大的一个数据靠近了坐标轴。
			rangeAxis.setLowerBound(0); // 最小值显示0
			rangeAxis.setAutoRange(false); // 不自动分配Y轴数据
			rangeAxis.setTickMarkStroke(new BasicStroke(1.6f)); // 设置坐标标记大小
			rangeAxis.setTickMarkPaint(Color.BLACK); // 设置坐标标记颜色

			// 获取折线对象
			LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot
					.getRenderer();
			BasicStroke realLine = new BasicStroke(1.8f); // 设置实线
			// 设置虚线
			float dashes[] = { 5.0f };
			BasicStroke brokenLine = new BasicStroke(2.2f, // 线条粗细
					BasicStroke.CAP_ROUND, // 端点风格
					BasicStroke.JOIN_ROUND, // 折点风格
					8f, dashes, 0.6f);
			for (int i = 0; i < dataset.getRowCount(); i++) {
				if (i % 2 == 0)
					renderer.setSeriesStroke(i, realLine); // 利用实线绘制
				else
					renderer.setSeriesStroke(i, brokenLine); // 利用虚线绘制
			}

			plot.setNoDataMessage("无对应的数据，请重新查询。");
			plot.setNoDataMessageFont(font);// 字体的大小
			plot.setNoDataMessagePaint(Color.RED);// 字体颜色
		}

		return chart;
	}

	/**
	 * 获取饼状图组合数据集对象
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
	 * 获取柱状图组合数据集对象
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
	 * 获取折线图组合数据集对象
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
