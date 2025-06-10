package com.api.backendCCEP.FacadeImp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.api.backendCCEP.Facade.IReport;
import com.api.backendCCEP.Repository.SaleRepository;

@Service
public class ReportDao implements IReport {

	//Instancias
    private SaleRepository saleRepository;
    
    public ReportDao(SaleRepository saleRepository) {
        this.saleRepository = saleRepository;
    }
	
	@Override
	public List<Map<String, Object>> getSalesSummary(String startDateStr, String endDateStr) {
	    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	    Date startDate = null;
	    Date endDate = null;

	    try {
	        startDate = dateFormat.parse(startDateStr);
	        endDate = dateFormat.parse(endDateStr);
	    } catch (ParseException e) {
	        e.printStackTrace();
	    }

	    List<Object[]> result = saleRepository.getSalesSummary(startDate, endDate);
	    List<Map<String, Object>> namedResult = new ArrayList<>();

	    for (Object[] row : result) {
	        Map<String, Object> map = new HashMap<>();
	        map.put("saleDate", row[0]);
	        map.put("totalSales", row[1]);
	        map.put("totalRevenue", row[2]);
	        namedResult.add(map);
	    }

	    return namedResult;
	}
	
}
