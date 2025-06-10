package com.api.backendCCEP.Facade;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.api.backendCCEP.Model.Category;


public interface ICategory {

    public Page<Category> categoryList(Pageable pageable);
    public List<Category> allCategories();
    public Category findById(Long id);
	public void save(Category category);
	public void delete(Category category);

}
