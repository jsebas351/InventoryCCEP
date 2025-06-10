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
@Table(name = "purchases")
public class Purchase {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "total_purchase")
	private long total_purchase;

	@Column(name = "bill_number")
	private long bill_number;

	@Column(name = "purchase_date")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "America/Bogota")
	private Date purchase_date;

	@Column(name = "edit_date")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "America/Bogota")
	private Date edit_date;
	
	@ManyToOne
	@JoinColumn(name = "provider_id", referencedColumnName = "id")
	private Supplier provider_id;

	@Column(name = "state", nullable = false)
	private String state;

	@OneToMany(mappedBy = "purchase_id", fetch = FetchType.LAZY)
    @JsonBackReference
    private List<Purchase_Detail> purchases_Details = new ArrayList<>();
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getTotal_purchase() {
		return total_purchase;
	}

	public void setTotal_purchase(long total_purchase) {
		this.total_purchase = total_purchase;
	}

	public long getBill_number() {
		return bill_number;
	}

	public void setBill_number(long bill_number) {
		this.bill_number = bill_number;
	}

	public Date getPurchase_date() {
		return purchase_date;
	}

	public void setPurchase_date(Date purchase_date) {
		this.purchase_date = purchase_date;
	}
	
	public Date getEdit_date() {
		return edit_date;
	}

	public void setEdit_date(Date edit_date) {
		this.edit_date = edit_date;
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

	public List<Purchase_Detail> getPurchases_Details() {
		return purchases_Details;
	}

	public void setPurchases_Details(List<Purchase_Detail> purchases_Details) {
		this.purchases_Details = purchases_Details;
	}
	
	public Purchase() {
	}

	public Purchase(long id, long total_purchase, long bill_number, Date purchase_date, Date edit_date,
			Supplier provider_id, String state, List<Purchase_Detail> purchases_Details) {
		super();
		this.id = id;
		this.total_purchase = total_purchase;
		this.bill_number = bill_number;
		this.purchase_date = purchase_date;
		this.edit_date = edit_date;
		this.provider_id = provider_id;
		this.state = state;
		this.purchases_Details = purchases_Details;
	}

	public Purchase(long id) {
		this.id = id;
	}

}