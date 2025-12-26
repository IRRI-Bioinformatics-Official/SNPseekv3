package org.irri.test;

import org.zkoss.zk.ui.Component;
import org.zkoss.zkmax.zul.MatrixRenderer;

public class GenotypeRenderer implements MatrixRenderer<GenotypeData> {

	@Override
	public String renderCell(Component parent, GenotypeData data, int rowIndex, int colIndex) {
		if (data == null) {
			return "<div>No data</div>";
		}

		// Create HTML string
		StringBuilder html = new StringBuilder();
		html.append("<div style='padding:8px; border-bottom:1px solid #e0e0e0;'>");
		html.append("<table style='width:100%; border-collapse:collapse;'><tr>");
		html.append("<td style='width:150px; color:#2196F3; font-weight:bold;'>").append(escape(data.getSampleId()))
				.append("</td>");
		html.append("<td style='width:120px;'>").append(escape(data.getMarker())).append("</td>");
		html.append("<td style='width:100px; font-family:monospace;'>").append(escape(data.getAllele1()))
				.append("</td>");
		html.append("<td style='width:100px; font-family:monospace;'>").append(escape(data.getAllele2()))
				.append("</td>");
		html.append("<td style='width:120px; font-weight:bold; color:#4CAF50;'>").append(escape(data.getGenotype()))
				.append("</td>");
		html.append("<td style='width:120px;'>").append(data.getQualityScore()).append("%</td>");
		html.append("<td style='width:150px;'>").append(escape(String.valueOf(data.getCallDate()))).append("</td>");
		html.append("</tr></table>");
		html.append("</div>");

		return html.toString();
	}

	private String escape(String value) {
		if (value == null)
			return "";
		return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
	}

	@Override
	public String renderHeader(Component arg0, GenotypeData arg1, int arg2, int arg3) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}