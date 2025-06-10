package com.api.backendCCEP.Model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name")
    private String name;

    @Column(name = "reference")
    private long reference;

    @Column(name = "description")
    private String description;

    @Column(name = "purchase_price")
    private long purchase_price;

    @Column(name = "sale_price")
    private long sale_price;

    @ManyToOne
	@JoinColumn(name = "subcategory_id", referencedColumnName = "id")
	private SubCategory subcategory_id;

    @ManyToOne
	@JoinColumn(name = "provider_id", referencedColumnName = "id")
	private Supplier provider_id;

    @Column(name = "state", nullable = false)
	private String state;

    @OneToMany(mappedBy = "product_id", fetch = FetchType.LAZY)
    @JsonBackReference(value="sale-detailsReference")
    private List<Sale_Detail> sale_Details = new ArrayList<>();
    
    @OneToMany(mappedBy = "product_id", fetch = FetchType.LAZY)
    @JsonBackReference(value="purchase-detailsReference")
    private List<Purchase_Detail> purchases_Details = new ArrayList<>();
    
    @OneToMany(mappedBy = "product_id", fetch = FetchType.LAZY)
    @JsonBackReference(value="inventoryReference")
    private List<Inventory> inventoriesList = new ArrayList<>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getReference() {
        return reference;
    }

    public void setReference(long reference) {
        this.reference = reference;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getPurchase_price() {
        return purchase_price;
    }

    public void setPurchase_price(long purchase_price) {
        this.purchase_price = purchase_price;
    }

    public long getSale_price() {
        return sale_price;
    }

    public void setSale_price(long sale_price) {
        this.sale_price = sale_price;
    }

    public SubCategory getSubcategory_id() {
        return subcategory_id;
    }

    public void setSubcategory_id(SubCategory subcategory_id) {
        this.subcategory_id = subcategory_id;
    }

    public Supplier getProvider_id() {
        return provider_id;
    }

    public void setProvider_id(Supplier provider_id) {
        this.provider_id = provider_id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<Sale_Detail> getSale_Details() {
        return sale_Details;
    }

    public void setSale_Details(List<Sale_Detail> sale_Details) {
        this.sale_Details = sale_Details;
    }
    
    public List<Purchase_Detail> getPurchases_Details() {
		return purchases_Details;
	}

	public void setPurchases_Details(List<Purchase_Detail> purchases_Details) {
		this.purchases_Details = purchases_Details;
	}

	public List<Inventory> getInventoriesList() {
		return inventoriesList;
	}

	public void setInventoriesList(List<Inventory> inventoriesList) {
		this.inventoriesList = inventoriesList;
	}

	public Product() {
    }

	public Product(long id, String name, long reference, String description, long purchase_price, long sale_price,
			SubCategory subcategory_id, Supplier provider_id, String state, List<Sale_Detail> sale_Details,
			List<Purchase_Detail> purchases_Details, List<Inventory> inventoriesList) {
		super();
		this.id = id;
		this.name = name;
		this.reference = reference;
		this.description = description;
		this.purchase_price = purchase_price;
		this.sale_price = sale_price;
		this.subcategory_id = subcategory_id;
		this.provider_id = provider_id;
		this.state = state;
		this.sale_Details = sale_Details;
		this.purchases_Details = purchases_Details;
		this.inventoriesList = inventoriesList;
	}

	public Product(long id) {
        this.id = id;
    }

}
