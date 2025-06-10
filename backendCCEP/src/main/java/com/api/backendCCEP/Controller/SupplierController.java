package com.api.backendCCEP.Controller;

import org.springframework.web.bind.annotation.RestController;

import com.api.backendCCEP.Facade.ISupplier;
import com.api.backendCCEP.Model.Product;
import com.api.backendCCEP.Model.Supplier;
import com.api.backendCCEP.Util.ApiResponse;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping({ "/admin" })
public class SupplierController {

    // Instacias
    private ISupplier iSupplier;

    public SupplierController(ISupplier iSupplier) {
        this.iSupplier = iSupplier;
        
    }

    // Verificar si ya hay un proveedor registrado con informacion
    public boolean verifyExistingSupplier(Object[] array) {
        List<Supplier> existingSuppliers = iSupplier.allSuppliers();

        boolean supplierExists = existingSuppliers.stream().anyMatch(existingSupplier -> {
            for (Object field : array) {

                if (field.equals(existingSupplier.getName()) ||
                        field.equals(existingSupplier.getPhone()) ||
                        field.equals(existingSupplier.getMail()) ||
                        field.equals(existingSupplier.getNit())) {
                    return true;
                }

            }
            return false;
        });

        return supplierExists;
    }

    // Verificar si ya existe un proveedor registrado excluyendo el actual
    public boolean verifyExistingSuppliersById(long id, Object[] array) {
        List<Supplier> existingSuppliers = iSupplier.allSuppliers();

        boolean supplierExists = existingSuppliers.stream().anyMatch(existingSupplier -> {
            for (Object field : array) {

                if (field.equals(existingSupplier.getName()) ||
                        field.equals(existingSupplier.getPhone()) ||
                        field.equals(existingSupplier.getMail()) ||
                        field.equals(existingSupplier.getNit())) {
                    if (existingSupplier.getId() != id) {
                        return true;
                    }
                }
            }
            return false;
        });

        return supplierExists;
    }

    // Verficar la longitud del telefono y del nit
    public boolean checkPhoneAndNitLength(long phone, long nit) {

        if (Long.toString(phone).length() != 10 || Long.toString(nit).length() != 10) {
            return true;
        } else {
            return false;
        }
    }

    // Validar Campos, nombre y estado
    private boolean isNullOrEmpty(Object[] array) {
        if (array == null) {
            return true; // Si el array es nulo, consideramos que está vacío
        }

        for (Object obj : array) {
            if (obj == null) {
                return true; // Si encontramos un objeto nulo, devolvemos true
            } else if (obj instanceof String && ((String) obj).trim().isEmpty()) {
                return true; // Si encontramos un string vacío, devolvemos true
            }
        }

        return false; // Si ninguno de los objetos en el array es nulo o vacío, devolvemos false
    }

    // Listar Proveedores con paginacion
    @GetMapping({ "/suppliers" })
    @PreAuthorize("hasRole('Administrador')")
    public ApiResponse<Page<Supplier>> getSuppliersLisPaginated(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        ApiResponse<Page<Supplier>> response = new ApiResponse<>();

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Supplier> suppliers = iSupplier.listSuppliers(pageable);

            response.setSuccess(true);
            response.setMessage("Consulta exitosa");
            response.setData(suppliers);
            response.setCode(200);

        } catch (Exception e) {

            response.setSuccess(false);
            response.setMessage("Error en la consulta");
            response.setData(null);
            response.setCode(500);

        }

        return response;
    }

    // Listar Proveedores sin paginacion
    @GetMapping({ "/suppliersnotpaginated" })
    @PreAuthorize("hasRole('Administrador')")
    public ApiResponse<List<Supplier>> getSuppliersListNotPaginated() {

        ApiResponse<List<Supplier>> response = new ApiResponse<>();

        try {
            List<Supplier> suppliers = iSupplier.allSuppliers();

            response.setSuccess(true);
            response.setMessage("Consulta exitosa");
            response.setData(suppliers);
            response.setCode(200);

        } catch (Exception e) {

            response.setSuccess(false);
            response.setMessage("Error en la consulta");
            response.setData(null);
            response.setCode(500);

        }

        return response;
    }

    // Creacion de nuevos Proveedores
    @PostMapping({ "/suppliers/create" })
    @PreAuthorize("hasRole('Administrador')")
    public ApiResponse<Supplier> createSuppliers(@RequestBody Supplier supplier) {

        ApiResponse<Supplier> response = new ApiResponse<>();

        // Array de campos
        Object[] fieldsToValidate = { supplier.getNit(), supplier.getName(), supplier.getPhone(), supplier.getMail() };

        try {
            // Validar campos obligatorios
            if (isNullOrEmpty(fieldsToValidate)) {
                response.setSuccess(false);
                response.setMessage("Todos los campos son requeridos");
                response.setData(null);
                response.setCode(400); // Código de respuesta 400 para indicar una solicitud incorrecta
                // Verficar una Categoria Existente
            } else if (verifyExistingSupplier(fieldsToValidate)) {
                response.setSuccess(false);
                response.setMessage("Ya hay un proveedor registrado con la misma informacion");
                response.setData(null);
                response.setCode(400); // Código de respuesta 400 para indicar una solicitud incorrecta
            } else if (checkPhoneAndNitLength(supplier.getPhone(), supplier.getNit())) {
                response.setSuccess(false);
                response.setMessage("El telefono y el nit deben tener 10 digitos");
                response.setData(null);
                response.setCode(400); // Código de respuesta 400 para indicar una solicitud incorrecta
            } else {
                supplier.setState("Activo");
                iSupplier.save(supplier);

                response.setSuccess(true);
                response.setMessage("Proveedor creado exitosamente");
                response.setData(supplier);
                response.setCode(201); // Código de respuesta 201 para indicar creación exitosa
            }

        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Error al crear la proveedor");
            response.setData(null);
            response.setCode(500);
        }

        return response;
    }

    // Encontrar el proveedor por id
    @GetMapping({ "/suppliers/{id}" })
    @PreAuthorize("hasRole('Administrador')")
    public ApiResponse<Supplier> findSupplier(@PathVariable Long id) {

        ApiResponse<Supplier> response = new ApiResponse<>();

        try {
            // Aquí utilizamos el método findById de la interfaz ISupplier
            Supplier supplier = iSupplier.findById(id);

            if (supplier != null) {
                response.setSuccess(true);
                response.setMessage("Proveedor encontrado exitosamente");
                response.setData(supplier);
                response.setCode(200);
            } else {
                response.setSuccess(false);
                response.setMessage("No se encontró el proveedor con el ID proporcionado");
                response.setData(null);
                response.setCode(404);
            }

        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Error al buscar el proveedor");
            response.setData(null);
            response.setCode(500);
        }

        return response;
    }

    // Actualizos Proveedores
    @PutMapping("/suppliers/update/{id}")
    @PreAuthorize("hasRole('Administrador')")
    public ApiResponse<Supplier> updateSupplier(@PathVariable long id, @RequestBody Supplier updatedSupplier) {
        ApiResponse<Supplier> response = new ApiResponse<>();

        // Array de campos
        Object[] fieldsToValidate = { updatedSupplier.getNit(), updatedSupplier.getName(), updatedSupplier.getPhone(),
                updatedSupplier.getMail() };

        try {
            // Verificar si ya existe un proveedor con el mismo nombre, excluyendo la
            // proveedor actual
            if (isNullOrEmpty(fieldsToValidate)) {
                response.setSuccess(false);
                response.setMessage("Todos los campos son requeridos");
                response.setData(null);
                response.setCode(400); // Código de respuesta 400 para indicar una solicitud incorrecta
                // Verficar una Categoria Existente
            } else if (verifyExistingSuppliersById(id, fieldsToValidate)) {
                response.setSuccess(false);
                response.setMessage("Ya hay un proveedor registrado con la misma informacion");
                response.setData(null);
                response.setCode(400); // Código de respuesta 400 para indicar una solicitud incorrecta
            } else if (checkPhoneAndNitLength(updatedSupplier.getPhone(), updatedSupplier.getNit())) {
                response.setSuccess(false);
                response.setMessage("El telefono y el nit deben tener 10 digitos");
                response.setData(null);
                response.setCode(400); // Código de respuesta 400 para indicar una solicitud incorrecta
            } else {
                // Actualizar la proveedor solo si existe
                Optional<Supplier> optionalSupplier = Optional.of(iSupplier.findById(id));

                if (optionalSupplier.isPresent()) {
                    Supplier existingSupplier = optionalSupplier.get();

                    // Actualizar los campos del proveedor existente con la información
                    // proporcionada
                    existingSupplier.setNit(updatedSupplier.getNit());
                    existingSupplier.setName(updatedSupplier.getName());
                    existingSupplier.setPhone(updatedSupplier.getPhone());
                    existingSupplier.setMail(updatedSupplier.getMail());
                    existingSupplier.setState(updatedSupplier.getState());

                    // Guardar el proveedor actualizado
                    iSupplier.save(existingSupplier);

                    response.setSuccess(true);
                    response.setMessage("Proveedor actualizado exitosamente");
                    response.setData(existingSupplier);
                    response.setCode(200);
                } else {
                    response.setSuccess(false);
                    response.setMessage("No se encontró un proveedor con el ID proporcionado");
                    response.setData(null);
                    response.setCode(404);
                }
            }
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Error al actualizar el proveedor");
            response.setData(null);
            response.setCode(500);
        }

        return response;
    }

    // Eliminar Proveedores
    @DeleteMapping("/suppliers/delete/{id}")
    @PreAuthorize("hasRole('Administrador')")
    public ApiResponse<String> deleteSupplier(@PathVariable Long id) {

        ApiResponse<String> response = new ApiResponse<>();

        try {
            // Verificar si el proveedor existe antes de intentar eliminarla
            Supplier existingSupplier = iSupplier.findById(id);

            if (existingSupplier != null) {
                //Verificar si existen productos relacionadas
                List<Product> products = existingSupplier.getProducts();

                if (products.isEmpty()) {
                iSupplier.delete(existingSupplier);

                response.setSuccess(true);
                response.setMessage("Proveedor eliminado exitosamente");
                response.setData("Proveedor eliminado");
                response.setCode(200);
                } else {
                response.setSuccess(false);
                response.setMessage("No se puede eliminar un proveedor relacionado con uno o mas productos");
                response.setData(null);
                response.setCode(400);
                }
            } else {
                response.setSuccess(false);
                response.setMessage("No se encontró el proveedor con el ID proporcionado");
                response.setData(null);
                response.setCode(404);
            }

        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Error al eliminar el proveedor");
            response.setData(null);
            response.setCode(500);
        }

        return response;
    }

}
