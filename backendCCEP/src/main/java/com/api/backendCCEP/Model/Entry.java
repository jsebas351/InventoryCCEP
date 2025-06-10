package com.api.backendCCEP.Model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;

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
@Table(name = "entries")
public class Entry {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product_id;
	
	@Column(name = "quantity")
	private long quantity;
	
	@Column(name = "dateEntry")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "America/Bogota")
    private Date dateEntry;
	
	@Column(name = "edit_date")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "America/Bogota")
    private Date edit_date;

	@OneToMany(mappedBy = "entry_id", fetch = FetchType.LAZY)
	@JsonBackReference(value = "entryReference")
	private List<Inventory> listInventory = new ArrayList<>();
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Product getProduct_id() {
		return product_id;
	}

	public void setProduct_id(Product product_id) {
		this.product_id = product_id;
	}

	public long getQuantity() {
		return quantity;
	}

	public void setQuantity(long quantity) {
		this.quantity = quantity;
	}

	public List<Inventory> getListInventory() {
		return listInventory;
	}

	public void setListInventory(List<Inventory> listInventory) {
		this.listInventory = listInventory;
	}

	public Date getDateEntry() {
		return dateEntry;
	}

	public void setDateEntry(Date dateEntry) {
		this.dateEntry = dateEntry;
	}

	public Date getEdit_date() {
		return edit_date;
	}

	public void setEdit_date(Date edit_date) {
		this.edit_date = edit_date;
	}

	public Entry() {
	}

	public Entry(long id, Product product_id, long quantity, Date dateEntry, Date edit_date,
			List<Inventory> listInventory) {
		this.id = id;
		this.product_id = product_id;
		this.quantity = quantity;
		this.dateEntry = dateEntry;
		this.edit_date = edit_date;
		this.listInventory = listInventory;
	}
	
}
