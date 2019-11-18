package com.simple.autocomplete.Document.Repository;

import com.simple.autocomplete.Document.Entity.DocumentVo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<DocumentVo, Integer> {

}
