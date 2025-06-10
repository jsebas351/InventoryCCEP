package com.api.backendCCEP.Facade;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.api.backendCCEP.Model.SubCategory;

public interface ISubCategory {

    public Page<SubCategory> listSubCategories(Pageable pageable);
    public List<SubCategory> allSubCategories();
    public SubCategory findById(long id);
    public void save(SubCategory subCategory);
    public void delete(SubCategory subCategory);

}
