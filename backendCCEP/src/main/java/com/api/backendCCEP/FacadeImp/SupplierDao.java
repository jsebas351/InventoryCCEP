package com.api.backendCCEP.FacadeImp;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import com.api.backendCCEP.Facade.ISupplier;
import com.api.backendCCEP.Model.Supplier;
import com.api.backendCCEP.Repository.SupplierRepository;

@Service
public class SupplierDao implements ISupplier{

    //Instacias
    private SupplierRepository supplierRepository;

    public SupplierDao(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    //Llamado a la consulta personalizada ubicada en el repository
    @Override
    @Secured("ROLE_Administrador")
    public Page<Supplier> listSuppliers(Pageable pageable) {
        return supplierRepository.findAllSuppliersWithPagination(pageable);
    }
    
    @Override
    @Secured("ROLE_Administrador")
	public List<Supplier> allSuppliers() {
		return supplierRepository.findAll();
	}

    //Llamado al metodo findById del repository
    @Override
    @Secured("ROLE_Administrador")
    public Supplier findById(long id) {
        return supplierRepository.findById(id).orElse(null);
    }

    //Llamado al repository para el metodo save
    @Override
    @Secured("ROLE_Administrador")
    public void save(Supplier supplier) {
        supplierRepository.save(supplier);
    }

    //Llamado al repository para el metodo delete
    @Override
    @Secured("ROLE_Administrador")
    public void delete(Supplier supplier) {
        supplierRepository.delete(supplier);
    }

}
