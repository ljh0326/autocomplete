package com.simple.autocomplete.title.service;

import com.simple.autocomplete.title.domain.TitleInfo;

import java.util.List;
import java.util.Set;

public interface AutoCompleteService {
    boolean initIndex(List<TitleInfo> list);
    Set<TitleInfo> getSearchResult(String searchWord);
    boolean addAutocomplete(TitleInfo titleInfo);
    boolean updateAutocomplete(TitleInfo titleInfo);
    boolean deleteAutocomplete(TitleInfo titleInfo);
}

