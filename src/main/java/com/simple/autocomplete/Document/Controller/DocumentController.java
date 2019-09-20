package com.simple.autocomplete.Document.Controller;

import com.simple.autocomplete.Document.Service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Set;

@Controller
@RequestMapping("/document")
public class DocumentController {

    private DocumentService documentService;

    @Autowired
    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping("/autocomplete")
    public ResponseEntity<Set<String>> autocomplete(@RequestParam("searchWord")String searchWord){
        return new ResponseEntity<>(documentService.autoComplete(searchWord), HttpStatus.OK);
    }
}
