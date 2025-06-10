package com.api.backendCCEP.FacadeImp;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import com.api.backendCCEP.Facade.IProduct;
import com.api.backendCCEP.Model.Product;
import com.api.backendCCEP.Repository.ProductRepository;

@Service
public class ProductDao implements IProduct {

    // Instacias
    private ProductRepository productRepository;

    public ProductDao(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    @Secured("ROLE_Administrador")
    public Page<Product> listProduct(Pageable pageable) {
        return productRepository.findAllProductsWithPagination(pageable);
    }

    @Override
    @Secured({"ROLE_Administrador", "ROLE_Vendedor"})
	public List<Product> allProducts() {
		return productRepository.findAll();
	}
    
    @Override
    @Secured({"ROLE_Administrador", "ROLE_Vendedor"})
    public Product findById(long id) {
        return productRepository.findById(id).orElse(null);
    }

    @Override
    @Secured("ROLE_Administrador")
    public void save(Product product) {
        productRepository.save(product);
    }

    @Override
    @Secured("ROLE_Administrador")
    public void delete(Product product) {
        productRepository.delete(product);
    }

	@Override
	@Secured("ROLE_Administrador")
	public List<Product> filterProductsByProviders(long id) {
		return productRepository.filterProductsByProviders(id);
	}

	@Override
	@Secured({"ROLE_Administrador", "ROLE_Vendedor"})
	public List<Product> searchProductByReferenceOrName(String value) {
		return productRepository.searchProductByKeywordOrReference(value);
	}

}
