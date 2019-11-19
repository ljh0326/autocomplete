package com.simple.autocomplete.title.service;

import java.util.Set;

public interface AutoCompleteService {
    Set<String> getSearchResult(String searchWord);
}
