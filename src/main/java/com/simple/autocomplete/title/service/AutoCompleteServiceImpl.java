package com.simple.autocomplete.title.service;

import com.simple.autocomplete.lucene.service.IndexService;
import com.simple.autocomplete.lucene.service.IndexServiceFactory;
import com.simple.autocomplete.lucene.service.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Set;

@Service
public class AutoCompleteServiceImpl implements AutoCompleteService{
    private final Logger LOGGER = LoggerFactory.getLogger(AutoCompleteServiceImpl.class);
    private final SearchService searchService;

    //색인 초기화
    @PostConstruct
    public void init(){
        IndexService indexService = IndexServiceFactory.getIndexService(IndexServiceFactory.Type.CUSTOM);
        indexService.index();
    }

    @Autowired
    AutoCompleteServiceImpl(SearchService searchService){
        this.searchService = searchService;
    }

    @Override
    public Set<String> getSearchResult(String searchWord) {
        long start = System.currentTimeMillis();

        SearchService searchService = new SearchService();
        try {
            Set<String> result = searchService.search(searchWord);
            long end = System.currentTimeMillis();
            LOGGER.info("검색 실행시간 : " + (end - start ) + "ms");
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
