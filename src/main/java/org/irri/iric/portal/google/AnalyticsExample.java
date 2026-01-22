package org.irri.iric.portal.google;

import java.io.IOException;
import java.util.List;

public class AnalyticsExample {
	private static final String GA_ID = "GOOGLE_ANALYTICS_PROPERTY_ID";
	
    public static void main(String[] args) {
        String credentialsPath = "ga-credentials.json";
        
        String propertyId = System.getenv(GA_ID);
        GoogleAnalyticsService service = new GoogleAnalyticsService(propertyId, credentialsPath);
        
        try {
            // Get total sessions for last 28 days
            long totalSessions = service.getTotalSessions(30);
            System.out.println("Total Sessions (Last 28 days): " + totalSessions);
            
            System.out.println("\n--- Page Breakdown ---");
            
            // Get detailed page analytics
            List<PageAnalytics> pageAnalytics = service.getPageViews(28);
            
            for (PageAnalytics analytics : pageAnalytics) {
                System.out.println(analytics);
            }
            
        } catch (IOException e) {
            System.err.println("Error fetching analytics data: " + e.getMessage());
            e.printStackTrace();
        }
    }
}