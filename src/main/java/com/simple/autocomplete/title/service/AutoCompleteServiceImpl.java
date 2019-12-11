package com.simple.autocomplete.title.service;

import com.simple.autocomplete.lucene.service.IndexService;
import com.simple.autocomplete.lucene.service.SearchService;
import com.simple.autocomplete.title.domain.TitleInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class AutoCompleteServiceImpl implements AutoCompleteService{
    private final Logger LOGGER = LoggerFactory.getLogger(AutoCompleteServiceImpl.class);

    @PostConstruct
    private void initIndex(){
        IndexService indexService = new IndexService();
        if(indexService.index())
            LOGGER.info("색인 초기화 성공");
    }

    @Override
    public boolean initIndex(List<TitleInfo> list){
        IndexService indexService = new IndexService();
        return indexService.index(list);
    }

    @Override
    public Set<TitleInfo> getSearchResult(String searchWord) {
        long start = System.currentTimeMillis();

        SearchService searchService = new SearchService();
        try {
            return searchService.search(searchWord);
//            검색시간 체크하려면 추석 풀기
//            long end = System.currentTimeMillis();
//            LOGGER.info("검색 실행시간 : " + (end - start ) + "ms");
//            return result;
        } catch (IOException e) {
            LOGGER.error("searchService 에러, 검색어: {}, 에러: {}", searchWord, e);
        }

        return new HashSet<>();
    }

    @Override
    public boolean addAutocomplete(TitleInfo titleInfo) {
        IndexService indexService = new IndexService();
        return indexService.add(titleInfo);
    }

    @Override
    public boolean updateAutocomplete(TitleInfo titleInfo) {
        IndexService indexService = new IndexService();
        return indexService.update(titleInfo);
    }

    @Override
    public boolean deleteAutocomplete(TitleInfo titleInfo) {
        IndexService indexService = new IndexService();
        return indexService.delete(titleInfo);
    }
}
