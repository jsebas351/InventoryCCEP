package com.api.backendCCEP.FacadeImp;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import com.api.backendCCEP.Facade.IEntry;
import com.api.backendCCEP.Model.Entry;
import com.api.backendCCEP.Repository.EntryRepository;

@Service
public class EntryService implements IEntry{

	private EntryRepository entryRepository;
	
	public EntryService(EntryRepository entryRepository) {
		this.entryRepository = entryRepository;
	}

	@Override
	@Secured("ROLE_Administrador")
	public Page<Entry> entriesListPaginted(Pageable pageable) {
		return entryRepository.entriesListPagination(pageable);
	}

	@Override
	@Secured("ROLE_Administrador")
	public List<Entry> allEntries() {
		return entryRepository.findAll();
	}
	
	@Override
	@Secured("ROLE_Administrador")
	public Entry findById(long id) {
		return entryRepository.findById(id).orElse(null);
	}

	@Override
	@Secured("ROLE_Administrador")
	public void save(Entry entry) {
		entryRepository.save(entry);		
	}

	@Override
	@Secured("ROLE_Administrador")
	public void delete(Entry entry) {
		Entry en = entryRepository.findById(entry.getId()).orElse(null);
		entryRepository.delete(en);
	}

}
