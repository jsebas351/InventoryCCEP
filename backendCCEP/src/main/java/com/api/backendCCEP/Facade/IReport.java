package com.api.backendCCEP.Facade;

import java.util.List;
import java.util.Map;

public interface IReport {

	public List<Map<String, Object>> getSalesSummary(String starDate, String endDate);

}
