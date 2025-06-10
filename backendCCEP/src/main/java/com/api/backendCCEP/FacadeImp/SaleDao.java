package com.api.backendCCEP.FacadeImp;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import com.api.backendCCEP.Facade.ISale;
import com.api.backendCCEP.Model.Sale;
import com.api.backendCCEP.Model.Sale_Detail;
import com.api.backendCCEP.Repository.SaleRepository;
import com.api.backendCCEP.Repository.Sale_DetailRepository;

import jakarta.transaction.Transactional;

@Service
public class SaleDao implements ISale {

	// Instancias
	private SaleRepository saleRepository;
	private Sale_DetailRepository detailRepository;

	public SaleDao(SaleRepository saleRepository, Sale_DetailRepository detailRepository) {
		this.saleRepository = saleRepository;
		this.detailRepository = detailRepository;
	}

	@Override
	@Secured("ROLE_Administrador")
	public Page<Sale> listSales(Pageable pageable) {
		return saleRepository.listSalesWithPagination(pageable);
	}

	// Guardar el objeto sale
	@Override
	@Secured({ "ROLE_Administrador", "ROLE_Vendedor" })
	public void save(Sale sale) {
		saleRepository.save(sale);
	}

	// Guardar el objeto sale_Detail
	@Override
	@Secured({ "ROLE_Administrador", "ROLE_Vendedor" })
	public void saveDetails(Sale_Detail sale_Detail) {
		detailRepository.save(sale_Detail);
	}

	// Listar los detalles basado en el id de la venta
	@Override
	@Secured("ROLE_Administrador")
	public List<Sale_Detail> listSaleDetailsById(long saleId) {
		return detailRepository.listSalesDetailsById(saleId);
	}

	// Encontrar la venta por el id
	@Override
	@Secured("ROLE_Administrador")
	public Sale findById(long id) {
		return saleRepository.findById(id).orElse(null);
	}

	@Override
	@Secured("ROLE_Administrador")
	public void deleteSales(Sale sale) {
		saleRepository.delete(sale);
	}

	@Override
	@Transactional
	@Secured("ROLE_Administrador")
	public void deteteSalesDetails(long saleId) {
		detailRepository.deleteDetails(saleId);
	}

	// Se crea un nuevo metodo para borrar un solo detalle de la venta
	// el anterior metodo borra todos los detalles asociados a la venta
	@Override
	@Secured("ROLE_Administrador")
	public void deteteSalesDetailsUpdate(Sale_Detail sale_Detail) {
		Sale_Detail details = detailRepository.findById(sale_Detail.getId()).orElse(null);
		detailRepository.delete(details);
	}

}
