<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="html"/>
	<xsl:variable name="space">
		<xsl:text disable-output-escaping="yes">&#38;nbsp;</xsl:text>
	</xsl:variable>
	<xsl:variable name="RootTag" select="JAVAHIS-EMR"/>
	<!--主模版入口部分-->
	<xsl:template match="/">
		<xsl:variable name="pageCount">
			<xsl:value-of select="count($RootTag/PAGE)"/>
		</xsl:variable>
		<!--
			================================页元素处理开始=============================================
		 -->
		<xsl:for-each select="$RootTag/PAGE">
			<!--整个页面的外边框表格-->
			<table style="border: 2px solid black;width:{@WIDTH}px;height:{@HEIGHT}px;position: relative;margin-top:0px;margin-bottom:0px;margin-left:0px;margin-right:0px;">
				<tr>
					<td valign="top" style="border:0px solid black">
						<!--页实际内容部分  页数大小1显示分页-->
						<xsl:choose>
							<xsl:when test="$pageCount > 1">
								<table style="border: 0px solid black;width:{@BASEWIDTH}px;margin-left:{@Left}px;">
									<tbody>
										<tr>
											<td align="right">
												<xsl:choose>
													<xsl:when test="$pageCount > 1">
														<!--翻页部分-->
														<div>
															<font style="font-family:宋体;font-size :10">
																<a name="{@no}">第<xsl:value-of select="@no"/>页</a>
						共<xsl:value-of select="$pageCount"/>页
						<xsl:choose>
																	<xsl:when test="@no = 1">
								上一页
							</xsl:when>
																	<xsl:otherwise>
																		<a href="#{@no-1}">上一页</a>
																	</xsl:otherwise>
																</xsl:choose>
						&#160;
						<xsl:choose>
																	<xsl:when test="@no =$pageCount">
							下一页
						</xsl:when>
																	<xsl:otherwise>
																		<a href="#{@no+1}">下一页</a>
																	</xsl:otherwise>
																</xsl:choose>
															</font>
														</div>
													</xsl:when>
												</xsl:choose>
											</td>
										</tr>
									</tbody>
								</table>
							</xsl:when>
						</xsl:choose>
						<!--各页page元素部分；id="p{@no}"-->
						<table id="p{@no}" style="border: 0px solid black;width:{@BASEWIDTH}px;height:{@BASEHEIGHT}px;position: relative;margin-top:{@Top}px;margin-bottom:{@Bottom}px;margin-left:{@Left}px;margin-right:{@Right}px;">
							<tbody>
								<tr>
									<td valign="top" style="border:0px solid black">
										<!--
				================================遍利所有元素=============================================
		 -->
										<xsl:for-each select="*">
											<xsl:choose>
												<xsl:when test="name(.)='table'">
													<!--调用表格元素模版；-->
													<xsl:call-template name="tableXSL"/>
												</xsl:when>
												<!--扩展其它元素模版；（图片；多选框，单选框......）-->
												<xsl:when test="name(.)='picture'">
													<xsl:call-template name="pictureXSL"/>
												</xsl:when>
												<!--加入EIMAGE-->
												<xsl:when test="name(.)='EIMAGE'">
													<!--
										tableID******:<xsl:value-of select="../@no"/>
										-->
													<xsl:call-template name="eimageXSL">
														<!--页元素page的索引号;-->
														<xsl:with-param name="strNo" select="../@no"/>
													</xsl:call-template>
												</xsl:when>
												<xsl:otherwise>
													<!--调用其它通用元素模版；-->
													<xsl:call-template name="othersXSL"/>
												</xsl:otherwise>
											</xsl:choose>
										</xsl:for-each>
									</td>
								</tr>
							</tbody>
						</table>
					</td>
				</tr>
				<!--
		======================分页符==================================================
		-->
			</table>
			<div style="PAGE-BREAK-AFTER: always"/>
		</xsl:for-each>
		<!--
		================================页元素处理结束=================================
		 -->
	</xsl:template>
	<!--子模版功能部分-->
	<!--模版表格元素处理；-->
	<xsl:template name="tableXSL">
		<!--
		<br/>进入表格元素部分；
     -->
		<table style="border-collapse:collapse;top:{@Y}px;left:{@X}px;overflow: hidden;position:absolute;height:{@HEIGHT};width:{@WIDTH}">
			<tbody>
				<xsl:for-each select="tr">
					<tr>
						<xsl:for-each select="td">
							<!--  dom中提供一下居中方式:align="right" 0 left居左，1居中center，2居右，重新设置属性值；-->
							<xsl:choose>
								<xsl:when test="@ALIGNMENT='1'">
									<td style="border:1px #000000 solid;width:{@WIDTH}px;height:{@HEIGHT}px;" align="center">
										<xsl:call-template name="othersInTableXSL"/>
									</td>
								</xsl:when>
								<xsl:when test="@ALIGNMENT='2'">
									<td style="border:1px #000000 solid;width:{@WIDTH}px;height:{@HEIGHT}px;" align="right">
										<xsl:call-template name="othersInTableXSL"/>
									</td>
								</xsl:when>
								<xsl:otherwise>
									<td style="border:1px #000000 solid;width:{@WIDTH}px;height:{@HEIGHT}px;" align="left">
										<xsl:call-template name="othersInTableXSL"/>
									</td>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:for-each>
						<!--遍利所有表格内的元素结束-->
					</tr>
				</xsl:for-each>
			</tbody>
		</table>
	</xsl:template>
	<!--表格内的通用元素模版(位置由表格定位)-->
	<xsl:template name="othersInTableXSL">
		<!--遍利所有表格内的其它普通元素-->
		<xsl:choose>
			<xsl:when test=".!=''">
				<xsl:for-each select="*">
					<!--当为图片元素时-->
					<xsl:choose>
						<!--暂不支持表格嵌套-->
						<xsl:when test="name(.)='table'">
							<xsl:call-template name="tableXSL"/>
						</xsl:when>
						<!--扩展其它元素模版；（图片；多选框，单选框......）-->
						<xsl:when test="name(.)='picture'">
							<xsl:call-template name="pictureInTableXSL"/>
						</xsl:when>
						<xsl:otherwise>
							<!--当为其它元素时-->
							<xsl:choose>
								<!--bold -->
								<xsl:when test="@style='bold'">
									<font style="font-family:{@family};font-weight:bold;font-size :{@size}">
										<xsl:value-of select="."/>
									</font>
								</xsl:when>
								<!--italic -->
								<xsl:when test="@style='italic'">
									<font style="font-family:{@family};font-style:italic;font-size :{@size}">
										<xsl:value-of select="."/>
									</font>
								</xsl:when>
								<!--bold italic -->
								<xsl:when test="@style='bolditalic'">
									<font style="font-family:{@family};font-weight:bold;font-style:italic;font-size :{@size}">
										<xsl:value-of select="."/>
									</font>
								</xsl:when>
								<!--normal -->
								<xsl:otherwise>
									<font style="font-family:{@family};font-weight:normal;font-size :{@size}">
										<xsl:value-of select="."/>
									</font>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:for-each>
			</xsl:when>
			<!--td中没有值的情况加入空格填充-->
			<xsl:otherwise>								
																		&#160;				  											  
															  </xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<!--非表格内的通用元素模版(位置由 div中的X,Y定位)-->
	<xsl:template name="othersXSL">
		<!--普通元素结点处理
													（
													family：<xsl:value-of select="@family"/>
													size：<xsl:value-of select="@size"/>
													font-style：<xsl:value-of select="@style"/>）
-->
		<xsl:choose>
			<!-- bold -->
			<xsl:when test="@style='bold'">
				
				<div style="top:{@Y}px;left:{@X}px;overflow: hidden;position:absolute;">
					<font style="font-family:{@family};font-weight:bold;font-size :{@size}">
						<xsl:value-of select="."/>
					</font>
				</div>
			</xsl:when>
			<!-- italic -->
			<xsl:when test="@style='italic'">
				<div style="top:{@Y}px;left:{@X}px;overflow: hidden;position:absolute;">
					<font style="font-family:{@family};font-style:italic;font-size :{@size}">
						<xsl:value-of select="."/>
					</font>
				</div>
			</xsl:when>
			<!--bolditalic-->
			<xsl:when test="@style='bolditalic'">
				<div style="top:{@Y}px;left:{@X}px;overflow: hidden;position:absolute;">
					<font style="font-family:{@family};font-weight:bold;font-style:italic;font-size :{@size}">
						<xsl:value-of select="."/>
					</font>
				</div>
			</xsl:when>
			<!--normal -->
			<xsl:otherwise>
				<div style="top:{@Y}px;left:{@X}px;overflow: hidden;position:absolute;">
					<font style="font-family:{@family};font-weight:normal;font-size :{@size}">
						<xsl:value-of select="."/>
					</font>
				</div>
			</xsl:otherwise>
		</xsl:choose>
		<!--字体类型设置:  plain|bold|-->
		<!--判断模版属性，对应html元素属性，并取对应节点值 不同控件不同处理-->
	</xsl:template>
	<!--图片元素模版 picture-->
	<xsl:template name="pictureXSL">
		<div style="top:{@Y}px;left:{@X}px;overflow: hidden;position:absolute;">
			<img src="image/EMRImage/{.}" border="0" align="middle" width="{@WIDTH}" height="{@HEIGHT}"/>
		</div>
	</xsl:template>
	<!--在表格中图片元素模版 picture-->
	<xsl:template name="pictureInTableXSL">
		<img src="image/EMRImage/{.}" border="0" align="middle" width="{@WIDTH}" height="{@HEIGHT}"/>
	</xsl:template>
	<!--EIMAGE元素模版(用于绘图功能)  strNo参数：追加到元素的ID-->
	<xsl:template name="eimageXSL">
		<xsl:param name="strNo"/>
		<xsl:choose>
			<xsl:when test="@BORDERVISIBLE='true'">
				<script>
						drawRect('<xsl:value-of select="@X"/>','<xsl:value-of select="@Y"/>','<xsl:value-of select="@WIDTH"/>','<xsl:value-of select="@HEIGHT"/>','p<xsl:value-of select="$strNo"/>');				
										
					</script>
			</xsl:when>
		</xsl:choose>
		<xsl:for-each select="*">
			<xsl:choose>
				<!--处理GBLOCK元素(矩形框)-->
				<xsl:when test="name(.)='GBLOCK'">
					<script>
					drawChildRect('<xsl:value-of select="../@X"/>','<xsl:value-of select="../@Y"/>','<xsl:value-of select="@X"/>','<xsl:value-of select="@Y"/>','<xsl:value-of select="@WIDTH"/>','<xsl:value-of select="@HEIGHT"/>','p<xsl:value-of select="$strNo"/>');
					</script>
				</xsl:when>
				<!--处理GLINE元素(线图形)-->
				<xsl:when test="name(.)='GLINE'">
					<script>
						drawChildLine('<xsl:value-of select="../@X"/>','<xsl:value-of select="../@Y"/>','<xsl:value-of select="@X"/>', '<xsl:value-of select="@Y"/>', '<xsl:value-of select="@WIDTH"/>', '<xsl:value-of select="@HEIGHT"/>','p<xsl:value-of select="$strNo"/>');
						
					</script>
				</xsl:when>
			</xsl:choose>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>
