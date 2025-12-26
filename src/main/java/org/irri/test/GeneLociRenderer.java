package org.irri.test;

import org.zkoss.zk.ui.Component;
import org.zkoss.zkmax.zul.MatrixRenderer;

// The signature should match: <RowData, RowHead, CellData, ColumnHead>
public class GeneLociRenderer implements MatrixRenderer<GenelociData> {
    
    @Override
    public String renderCell(Component parent, GenelociData data, int rowIndex, int colIndex) {
        System.out.println("Renderer called - Row: " + rowIndex + ", Data: " + (data != null ? data.getLociId() : "NULL"));
        
        if (data == null) {
            return "<div style='padding:8px;'>No data</div>";
        }
        
        // Build HTML
        StringBuilder html = new StringBuilder();
        html.append("<div class='z-biglistbox-data-row' style='padding:8px; border-bottom:1px solid #e0e0e0; cursor:pointer; display:block;'>");
        html.append("  <div style='display:flex; gap:10px;'>");
        html.append("    <div style='width:150px; color:#1976D2; font-weight:bold;'>").append(escape(data.getLociId())).append("</div>");
        html.append("    <div style='width:200px; font-weight:bold;'>").append(escape(data.getLociName())).append("</div>");
        html.append("    <div style='width:120px;'>").append(escape(data.getChromosome())).append("</div>");
        html.append("    <div style='width:150px;'>").append(escape(data.getPosition())).append("</div>");
        html.append("    <div style='width:120px;'>").append(escape(data.getType())).append("</div>");
        html.append("    <div style='width:300px;'>").append(escape(data.getDescription())).append("</div>");
        html.append("  </div>");
        html.append("</div>");
        
        return html.toString();
    }
    
    private String escape(String value) {
        if (value == null || value.isEmpty()) return "&nbsp;";
        return value.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;");
    }
    
   

	@Override
	public String renderHeader(Component arg0, GenelociData arg1, int arg2, int arg3) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}