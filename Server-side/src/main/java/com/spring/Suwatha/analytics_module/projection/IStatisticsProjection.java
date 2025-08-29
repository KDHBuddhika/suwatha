package com.spring.Suwatha.analytics_module.projection;

public interface IStatisticsProjection {
    Integer getActiveSessions();
    Long getTotalSessions();
    Long getFinishedSessions();
    Long getSessionsToday();
    Long getTherapistsAvailable();
    Long getTotalTherapists();
    Long getBusyTherapists();
    Long getOfflineTherapists();
    Double getAverageSessionRating();
}
