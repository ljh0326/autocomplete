package com.simple.autocomplete.title;

import com.simple.autocomplete.title.service.AutoCompleteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Set;

@Controller
@RequestMapping("/")
public class TitleController {

    private AutoCompleteService autoCompleteService;

    @Autowired
    public TitleController(AutoCompleteService autoCompleteService) {
        this.autoCompleteService = autoCompleteService;
    }

    @GetMapping("/autocomplete")
    public ResponseEntity<Set<String>> autocomplete(@RequestParam(value = "searchWord", required = false)String searchWord){
        return new ResponseEntity<>(autoCompleteService.getSearchResult(searchWord), HttpStatus.OK);
    }
}

