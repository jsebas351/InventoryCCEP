package com.api.backendCCEP.Facade;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.api.backendCCEP.Model.Product;

public interface IProduct {

    public Page<Product> listProduct(Pageable pageable);
    public List<Product> allProducts();
    public Product findById(long id);
    public void save(Product product);
    public void delete(Product product);
    public List<Product> filterProductsByProviders(long id);
    public List<Product> searchProductByReferenceOrName(String value);

}
