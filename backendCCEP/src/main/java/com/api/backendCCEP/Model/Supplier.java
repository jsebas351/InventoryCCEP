package com.api.backendCCEP.Model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "suppliers")
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "nit")
    private long nit;

    @Column(name = "name")
    private String name;

    @Column(name = "phone")
    private long phone;

    @Column(name = "mail")
    private String mail;

    @Column(name = "state")
    private String state;

    @OneToMany(mappedBy = "provider_id")
    @JsonIgnore
    private List<Product> products = new ArrayList<>();
    
    @OneToMany(mappedBy = "provider_id")
    @JsonIgnore
    private List<Purchase> purchases = new ArrayList<>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getNit() {
        return nit;
    }

    public void setNit(long nit) {
        this.nit = nit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPhone() {
        return phone;
    }

    public void setPhone(long phone) {
        this.phone = phone;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public Supplier() {
    }

    public Supplier(long id, long nit, String name, long phone, String mail, String state, List<Product> products) {
        this.id = id;
        this.nit = nit;
        this.name = name;
        this.phone = phone;
        this.mail = mail;
        this.state = state;
        this.products = products;
    }

    public Supplier(long id) {
        this.id = id;
    }

}
