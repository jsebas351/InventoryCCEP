package com.api.backendCCEP.Facade;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.api.backendCCEP.Model.Entry;

public interface IEntry {

	public Page<Entry> entriesListPaginted(Pageable pageable);
	public List<Entry> allEntries();
	public Entry findById(long id);
	public void save(Entry entry);
	public void delete(Entry entry);
	
}
