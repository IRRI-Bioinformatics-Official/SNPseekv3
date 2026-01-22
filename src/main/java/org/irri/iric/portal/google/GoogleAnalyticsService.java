package org.irri.iric.portal.google;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.google.analytics.data.v1beta.BetaAnalyticsDataClient;
import com.google.analytics.data.v1beta.BetaAnalyticsDataSettings;
import com.google.analytics.data.v1beta.DateRange;
import com.google.analytics.data.v1beta.Dimension;
import com.google.analytics.data.v1beta.Metric;
import com.google.analytics.data.v1beta.Row;
import com.google.analytics.data.v1beta.RunReportRequest;
import com.google.analytics.data.v1beta.RunReportResponse;
import com.google.auth.oauth2.GoogleCredentials;

public class GoogleAnalyticsService {

	private final String propertyId;
	private final String credentialsPath;
	
	private final BetaAnalyticsDataSettings settings;
	
	private final GoogleCredentials credentials;

	public GoogleAnalyticsService(String propertyId, String credentialsPath) {
		this.propertyId = propertyId;
		this.credentialsPath = credentialsPath;

		// Initialize credentials
		System.out.println("Looking for credentials at: " + credentialsPath);
		System.out.println("ClassLoader: " + getClass().getClassLoader());

		InputStream credentialsStream = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(credentialsPath);

		try {
			initCredentials(credentialsStream);
			
			credentials = GoogleCredentials.fromStream(credentialsStream)
					.createScoped("https://www.googleapis.com/auth/analytics.readonly");

			// Initialize client settings
			settings = BetaAnalyticsDataSettings.newBuilder()
					.setCredentialsProvider(() -> credentials).build();

			
		} catch (IOException e) {
			throw new RuntimeException("Failed to initialize Google Analytics credentials", e);
		}
		

		
	}

	private void initCredentials(InputStream credentialsStream) throws IOException {
		if (credentialsStream == null) {
			throw new IOException("Credentials file not found in classpath: " + credentialsPath);
		}
	}

	/**
	 * Fetches page views and session data from Google Analytics
	 */
	public List<PageAnalytics> getPageViews(int daysAgo) throws IOException {
		List<PageAnalytics> results = new ArrayList<>();

		// Create the client
		try (BetaAnalyticsDataClient client = BetaAnalyticsDataClient.create(settings)) {

			// Build the request
			RunReportRequest request = RunReportRequest.newBuilder().setProperty("properties/" + propertyId)
					.addDimensions(Dimension.newBuilder().setName("pagePath"))
					.addMetrics(Metric.newBuilder().setName("sessions"))
					.addMetrics(Metric.newBuilder().setName("activeUsers"))
					.addMetrics(Metric.newBuilder().setName("newUsers"))
					.addMetrics(Metric.newBuilder().setName("averageSessionDuration"))
					.addDateRanges(DateRange.newBuilder().setStartDate(daysAgo + "daysAgo").setEndDate("today"))
					.build();

			// Execute the request
			RunReportResponse response = client.runReport(request);

			// Process the response
			for (Row row : response.getRowsList()) {
				PageAnalytics analytics = new PageAnalytics();
				analytics.setPagePath(row.getDimensionValues(0).getValue());
				analytics.setSessions(Long.parseLong(row.getMetricValues(0).getValue()));
				analytics.setActiveUsers(Long.parseLong(row.getMetricValues(1).getValue()));
				analytics.setNewUsers(Long.parseLong(row.getMetricValues(2).getValue()));
				analytics.setAvgSessionDuration(Double.parseDouble(row.getMetricValues(3).getValue()));

				results.add(analytics);
			}
		}

		return results;
	}

	/**
	 * Get total sessions for all pages
	 */
	public long getTotalSessions(int daysAgo) throws IOException {

		try (BetaAnalyticsDataClient client = BetaAnalyticsDataClient.create(settings)) {

			RunReportRequest request = RunReportRequest.newBuilder().setProperty("properties/" + propertyId)
					.addMetrics(Metric.newBuilder().setName("sessions"))
					.addDateRanges(DateRange.newBuilder().setStartDate(daysAgo + "daysAgo").setEndDate("today"))
					.build();

			RunReportResponse response = client.runReport(request);

			if (response.getRowsCount() > 0) {
				return Long.parseLong(response.getRows(0).getMetricValues(0).getValue());
			}
		}

		return 0;
	}
}
