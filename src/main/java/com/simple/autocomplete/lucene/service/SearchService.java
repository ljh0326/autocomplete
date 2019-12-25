package com.simple.autocomplete.lucene.service;

import com.simple.autocomplete.lucene.analyzer.CustomNgramAnalyzer;
import com.simple.autocomplete.title.domain.TitleInfo;
import com.simple.autocomplete.utils.Eng2KorConverter;
import com.simple.autocomplete.utils.PropertyLoader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.LinkedHashSet;
import java.util.Set;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queries.function.FunctionScoreQuery;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.DoubleValuesSource;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.MMapDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author LEE
 * @contact ljh0326s@gmail.com
 */
public class SearchService {
    //색인 정보가 저장된 경로
    private final Logger LOGGER = LoggerFactory.getLogger(SearchService.class);
    private static final int DEFAULT_SEARCH_COUNT = 10;
    /**
     * 검색 결과를 반환하는 메서드
     * @param searchValue 검색어
     * @return 검색결과
     * @throws IOException
     */
    public Set<TitleInfo> search(String searchValue) throws IOException {
        return search(searchValue, DEFAULT_SEARCH_COUNT, false);
    }

    public Set<TitleInfo> search(String searchValue, int searchCount) throws IOException {
        return search(searchValue, searchCount, false);
    }

    public Set<TitleInfo> search(String searchValue, int searchCount, boolean showScoreLog) throws IOException {

        Set<TitleInfo> result = new LinkedHashSet<>();
        //영 -> 한 변환기 ex) 안녕 -> dkssud
        Eng2KorConverter eng2Kor = new Eng2KorConverter();
        //인댁스 경로. property.properties 참조
        String indexPath = PropertyLoader.getInstance().getPropertyValue("INDEX_DIR_PATH");

        //빈 글자라면 검색하지말고 결과 반환
        if(searchValue.isEmpty()) {
            return result;
        }

        //들어온 검색어를 한글로 변환
        searchValue = eng2Kor.engToKor(searchValue.toLowerCase());
        LOGGER.info("한글 변환 검색어: {}", searchValue);

        //검색 시작
        //IndexSearcher = IndexReader + query
        try(IndexReader reader = DirectoryReader.open(MMapDirectory.open(Paths.get(indexPath)))){

            //searcher 생성
            IndexSearcher searcher = new IndexSearcher(reader);
            //검색할 query생성
            Query query = setQuery(searchValue);

            //검색
            TopDocs docs = searcher.search(query, searchCount);
            ScoreDoc[] hits = docs.scoreDocs;

            //검색 결과를 돌면서 결과반환
            for (ScoreDoc hit : hits) {
                int docId = hit.doc;
                Document document = searcher.doc(docId);
                result.add(new TitleInfo(document.get("contentsNo"), document.get("autoKeyword"), document.get("regDate"), document.get("hit"), hit.score));

                //스코어 출력
                if(showScoreLog) showScoreLog(searcher, query,docId);
            }

            return result;
        }catch(Exception e){
            LOGGER.error("색인 조회 실패 | 색인 경로 {} ", indexPath);
            return new LinkedHashSet<>();
        }
    }

    private void showScoreLog(IndexSearcher searcher, Query query,int docId) throws IOException {
        Document d = searcher.doc(docId);

        LOGGER.info("#######################");
        LOGGER.info(searcher.explain(query, docId).toString());
        LOGGER.info("#######################");
        //콘솔창 출력
        LOGGER.info(". [제목]" + "\t" + d.get("autoKeyword")
                + ". [한글변환 제목]" + "\t" + d.get("eng2AutoKeyword")
                + ". [조회수]" + "\t" + d.get("hit")+ "\n");
    }

    /**
     * 검색어를 이용해 query를 만들어주는 메서드
     * @param searchValue 검색어
     * @return Query
     * @throws ParseException
     */
    private Query setQuery(String searchValue) throws ParseException {

        //검색어와 일치하는 텀이 포함된 색인을 가져옴
        //ex) (Term: 하늘 searchValue: 하늘) 이렇게 일치하면 결과반환
        Query analyzedQuery= new QueryParser("eng2korAutoKeyword", new CustomNgramAnalyzer()).parse(searchValue);
        //오타 보정한 결과를 가져옴
        //ex) 호늘 -> 하늘로 검색된 결과 반환
        FuzzyQuery fuzzy = new FuzzyQuery(new Term("eng2korAutoKeyword", searchValue), 2);
        //조회수가 높을수록 상위로 정렬되도록 Score변환
        Query customQuery = new FunctionScoreQuery(analyzedQuery, DoubleValuesSource.fromDoubleField("hit_boost"));

        //SHOULD : optional, MUST: required, MUST_NOT: NOT AND
        //생성한 Query 들을 조합해서 반환
        return new BooleanQuery.Builder()
                .add(new BooleanClause(customQuery, BooleanClause.Occur.MUST))
                .add(new BooleanClause(fuzzy, BooleanClause.Occur.SHOULD))
                .build();
    }
}

