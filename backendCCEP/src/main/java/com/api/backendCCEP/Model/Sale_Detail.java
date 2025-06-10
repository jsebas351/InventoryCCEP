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
@Table(name = "sales_details")
public class Sale_Detail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "quantity")
    private long quantity;

    @Column(name = "subtotal")
    private long subtotal;

    @Column(name = "discount_product")
    private long discount_product;

    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product_id;

    @ManyToOne
    @JoinColumn(name = "sale_id", referencedColumnName = "id")
    private Sale sale_id;

    @OneToMany(mappedBy = "saledetail_id", fetch = FetchType.LAZY)
	@JsonBackReference(value = "saleDetailReference")
	private List<Inventory> listInventory = new ArrayList<>();
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public long getSubtotal() {
        return subtotal;
    }

    public long getDiscount_product() {
        return discount_product;
    }

    public void setDiscount_product(long discount_product) {
        this.discount_product = discount_product;
    }

    public void setSubtotal(long subtotal) {
        this.subtotal = subtotal;
    }

    public Product getProduct_id() {
        return product_id;
    }

    public void setProduct_id(Product product_id) {
        this.product_id = product_id;
    }

    public Sale getSale_id() {
        return sale_id;
    }

    public void setSale_id(Sale sale_id) {
        this.sale_id = sale_id;
    }

    public Sale_Detail() {
    }

    public Sale_Detail(long id, long quantity, long subtotal, long discount_product, Product product_id, Sale sale_id) {
        this.id = id;
        this.quantity = quantity;
        this.subtotal = subtotal;
        this.discount_product = discount_product;
        this.product_id = product_id;
        this.sale_id = sale_id;
    }

}
