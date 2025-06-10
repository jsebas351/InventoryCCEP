package com.api.backendCCEP.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.api.backendCCEP.Model.Entry;

@Repository
public interface EntryRepository extends JpaRepository<Entry, Long>{

	@Query(value = "SELECT * FROM entries c ORDER BY c.id ASC", nativeQuery = true)
	Page<Entry> entriesListPagination(Pageable pageable);
	
}
