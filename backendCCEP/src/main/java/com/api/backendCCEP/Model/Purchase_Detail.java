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
@Table(name = "purchases_details")
public class Purchase_Detail {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(name = "quantity")
	private long quantity;
	
	@Column(name = "subtotal")
	private long subtotal;
	
	@ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product_id;

	@ManyToOne
    @JoinColumn(name = "purchase_id", referencedColumnName = "id")
    private Purchase purchase_id;

	@OneToMany(mappedBy = "purchasedetail_id", fetch = FetchType.LAZY)
	@JsonBackReference(value = "purchaseDetailReference")
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

	public void setSubtotal(long subtotal) {
		this.subtotal = subtotal;
	}

	public Product getProduct_id() {
		return product_id;
	}

	public void setProduct_id(Product product_id) {
		this.product_id = product_id;
	}

	public Purchase getPurchase_id() {
		return purchase_id;
	}

	public void setPurchase_id(Purchase purchase_id) {
		this.purchase_id = purchase_id;
	}
	
	public Purchase_Detail() {
	}

	public Purchase_Detail(long id, long quantity, long subtotal, Product product_id, Purchase purchase_id) {
		this.id = id;
		this.quantity = quantity;
		this.subtotal = subtotal;
		this.product_id = product_id;
		this.purchase_id = purchase_id;
	}

	public Purchase_Detail(long id) {
		this.id = id;
	}
	
}
