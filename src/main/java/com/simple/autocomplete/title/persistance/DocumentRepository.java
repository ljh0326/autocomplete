package com.simple.autocomplete.title.persistance;

import com.simple.autocomplete.title.domain.DocumentVo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepository extends JpaRepository<DocumentVo, Integer> {

}
