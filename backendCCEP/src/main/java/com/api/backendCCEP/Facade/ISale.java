package com.api.backendCCEP.Facade;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.api.backendCCEP.Model.Sale;
import com.api.backendCCEP.Model.Sale_Detail;

public interface ISale {

    public Page<Sale> listSales(Pageable pageable);
    public List<Sale_Detail> listSaleDetailsById(long saleId);
    public Sale findById(long id);
    public void save(Sale sale);
    public void saveDetails(Sale_Detail sale_Detail);
    public void deleteSales(Sale sale);
    public void deteteSalesDetails(long id);
    public void deteteSalesDetailsUpdate(Sale_Detail sale_Detail);

}
