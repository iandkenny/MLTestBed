<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template match="/">
		<html>
			<body>
				<!-- <a><xsl:attribute name="href"> <xsl:value-of select="link"/></xsl:attribute> 
					<xsl:value-of select="@name"/> </a> -->
				<br />
				<h2>Thoracic Experiment Result</h2>
				<hr />
				<p>
					<b>
						<xsl:if test="IterResult/Key/*">
							Key =
							<xsl:value-of select="IterResult/Key" />
						</xsl:if>
						<xsl:choose>
							<xsl:when test="IterResult/ParticleNeighbourhood">
								Neighbourhood =
								<xsl:value-of
									select="IterResult/ParticleNeighbourhood" />

							</xsl:when>
							<xsl:otherwise>
								Neighbourhood = FIPS
							</xsl:otherwise>
						</xsl:choose>

					</b>
				</p>
				<table border="0" cellpadding="0" cellspacing="1">
					<tr>
						<td>
							<h4>Matrix</h4>
						</td>
						<xsl:if test="IterResult/Vector/*">
							<td width="3" />
							<td>
								<h4>Sub Vector</h4>
							</td>
						</xsl:if>
						<xsl:if test="IterResult/PowerVector/*">
							<td width="3" />
							<td>
								<h4>PowerVector</h4>
							</td>
						</xsl:if>
						<xsl:if test="IterResult/LogVector/*">
							<td width="3" />
							<td>
								<h4>LogVector</h4>
							</td>
						</xsl:if>
					</tr>
					<tr>
						<td>
							<table border="0" cellpadding="0" cellspacing="0">
								<xsl:for-each select="IterResult/Matrix">
									<xsl:for-each select="Row">
										<tr>
											<xsl:for-each select="Cell">
												<td style="border: thin solid #000000;">
													<xsl:value-of select="." />
												</td>
											</xsl:for-each>
										</tr>
									</xsl:for-each>
								</xsl:for-each>
							</table>
						</td>
						<xsl:if test="IterResult/Vector/*">
							<td width="3" />
							<td>
								<table border="0" cellpadding="0" cellspacing="0">
									<xsl:for-each select="IterResult/Vector">
										<xsl:for-each select="Cell">
											<tr>
												<td style="border: thin solid #000000;">
													<xsl:value-of select="." />
												</td>
											</tr>
										</xsl:for-each>

									</xsl:for-each>
								</table>
							</td>
						</xsl:if>
												<xsl:if test="IterResult/PowerVector/*">
							<td width="3"></td>
							<td>
								<table border="0" cellpadding="0" cellspacing="0">
									<xsl:for-each select="IterResult/PowerVector">
										<xsl:for-each select="Cell">
											<tr>
												<td style="border: thin solid #000000;">
													<xsl:value-of select="." />
												</td>
											</tr>
										</xsl:for-each>

									</xsl:for-each>
								</table>
							</td>
						</xsl:if>
						<xsl:if test="IterResult/LogVector/*">
							<td width="3"></td>
							<td>
								<table border="0" cellpadding="0" cellspacing="0">
									<xsl:for-each select="IterResult/LogVector">
										<xsl:for-each select="Cell">
											<tr>
												<td style="border: thin solid #000000;">
													<xsl:value-of select="." />
												</td>
											</tr>
										</xsl:for-each>

									</xsl:for-each>
								</table>
							</td>
						</xsl:if>
						
					</tr>
				</table>
				<h4>Results Table</h4>
				<table border="1">
					<tr
						style="width: 100%; background-color: #3333CC; color: #FFFFFF;"
						border="1" cellpadding="0" cellspacing="0">
						<th align="left">Input</th>
						<th align="left">RequiredValue</th>
						<th align="left">SumResult</th>
						<th align="left">Result (Difference)</th>
					</tr>
					<xsl:for-each select="IterResult/Output">
						<tr>
							<td>
								<xsl:value-of select="Input" />
							</td>
							<td>
								<xsl:value-of select="RequiredValue" />
							</td>
							<td>
								<xsl:value-of select="SumResult" />
							</td>
							<td>
								<xsl:value-of select="Result" />
							</td>
						</tr>
					</xsl:for-each>
				</table>
				<hr />
				<!-- <h4>Table of Statistics</h4> <table border="0"> <tr> <td>Sum </td> 
					<td> <xsl:value-of select="IterResult/Sum" /> </td> </tr> <tr> <td>RequiredSum 
					</td> <td> <xsl:value-of select="IterResult/ReqSum" /> </td> </tr> <tr> <td>AvgSum 
					</td> <td> <xsl:value-of select="IterResult/AvgSum" /> </td> </tr> <tr> <td>AvgRequiredSum 
					</td> <td> <xsl:value-of select="IterResult/AvgRequiredSum" /> </td> </tr> 
					<tr> <td>Absolute Maximum Error </td> <td> <xsl:value-of select="IterResult/AME" 
					/> </td> </tr> <tr> <td>Mean Absolute Error </td> <td> <xsl:value-of select="IterResult/MAE" 
					/> </td> </tr> <tr> <td>Mean Error </td> <td> <xsl:value-of select="IterResult/ME" 
					/> </td> </tr> <tr> <td>Relative Absolute Error </td> <td> <xsl:value-of 
					select="IterResult/RAE" /> </td> </tr> <tr> <td>Root Mean Squared Error </td> 
					<td> <xsl:value-of select="IterResult/RMSE" /> </td> </tr> <tr> <td> R <sup>2</sup> 
					</td> <td> <xsl:value-of select="IterResult/R2" /> </td> </tr> <tr> <td>Score 
					Result </td> <td> <xsl:value-of select="IterResult/Result" /> </td> </tr> 
					</table> -->
			</body>
		</html>
	</xsl:template>

</xsl:stylesheet>