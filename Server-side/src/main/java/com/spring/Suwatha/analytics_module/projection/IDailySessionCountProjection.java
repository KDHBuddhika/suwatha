package com.spring.Suwatha.analytics_module.projection;

public interface IDailySessionCountProjection {
    
    java.sql.Date getSessionDate(); // The native query returns a SQL Date
    Long getSessionCount();
    
}
