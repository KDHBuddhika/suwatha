package com.spring.Suwatha.analytics_module.projection;

public interface IHourlyUsageProjection {
    Integer getHourOfDay();
    Long getSessionCount();
}
