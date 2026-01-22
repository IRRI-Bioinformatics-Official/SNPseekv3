package org.irri.iric.portal.google;

public class PageAnalytics {
    private String pagePath;
    private long sessions;
    private long activeUsers;
    private long newUsers;
    private double avgSessionDuration;
    
    // Getters and Setters
    public String getPagePath() {
        return pagePath;
    }
    
    public void setPagePath(String pagePath) {
        this.pagePath = pagePath;
    }
    
    public long getSessions() {
        return sessions;
    }
    
    public void setSessions(long sessions) {
        this.sessions = sessions;
    }
    
    public long getActiveUsers() {
        return activeUsers;
    }
    
    public void setActiveUsers(long activeUsers) {
        this.activeUsers = activeUsers;
    }
    
    public long getNewUsers() {
        return newUsers;
    }
    
    public void setNewUsers(long newUsers) {
        this.newUsers = newUsers;
    }
    
    public double getAvgSessionDuration() {
        return avgSessionDuration;
    }
    
    public void setAvgSessionDuration(double avgSessionDuration) {
        this.avgSessionDuration = avgSessionDuration;
    }
    
    @Override
    public String toString() {
        return String.format("Page: %s, Sessions: %d, Active Users: %d, New Users: %d, Avg Duration: %.2fs",
            pagePath, sessions, activeUsers, newUsers, avgSessionDuration);
    }
}