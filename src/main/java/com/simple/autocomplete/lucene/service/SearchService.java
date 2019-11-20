package com.simple.autocomplete.lucene.service;

import com.simple.autocomplete.lucene.analyzer.CustomNgramAnalyzer;
import com.simple.autocomplete.utils.Eng2KorConverter;
import com.simple.autocomplete.utils.PropertyLoader;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.MMapDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author LEE
 * @contact ljh0326s@gmail.com
 *
 * 검색할때 필요한 것
 *    1.IndexSearcher
 *      1.1 IndexReader
 *    2. Query
 *
 * IndexSearcher에서 Search한 결과가 TopDoc 순회돌리면서 출력
 */
@Component
public class SearchService {
    //색인 정보가 저장된 경로
    private static final String indexPath = PropertyLoader.getInstance().getPropertyValue("INDEX_DIR_PATH");
    private static final Logger LOGGER = LoggerFactory.getLogger(SearchService.class);
    private final Eng2KorConverter eng2Kor = new Eng2KorConverter();

    public Set<String> search(String searchValue) throws IOException {
        //색인을 읽어오는 리더
        Set<String> result = new LinkedHashSet<>();

        //빈 글자라면 검색하지말고 결과 반환
        if(searchValue.isEmpty()) return result;

        //들어온 검색어를 한글로 변환
        searchValue = eng2Kor.engToKor(searchValue.toLowerCase());
        LOGGER.info("한글 변환 검색어: {} \n", searchValue);

        //검색 시작
        try(IndexReader reader = DirectoryReader.open(MMapDirectory.open(Paths.get(indexPath)))){
            IndexSearcher searcher = new IndexSearcher(reader);

            BooleanQuery.Builder builder = new BooleanQuery.Builder();

            //검색어를 색인할 때 쓴 analyzer로 색인한다음 쿼리를 찾음
            Query analyzedQuery= new QueryParser("eng2korPathName", new CustomNgramAnalyzer()).parse(searchValue);
            FuzzyQuery fuzzy = new FuzzyQuery(new Term("eng2korPathName", searchValue), 1);

            //SHOULD : OR, MUST: AND, MUST_NOT: NOT AND
            BooleanQuery booleanQuery = new BooleanQuery.Builder()
                    .add(new BooleanClause(analyzedQuery, BooleanClause.Occur.SHOULD))
                    .add(new BooleanClause(fuzzy, BooleanClause.Occur.SHOULD))
                    .build();

            TopDocs docs = searcher.search(booleanQuery, 100);
            ScoreDoc[] hits = docs.scoreDocs;
            System.out.println(hits.length + " 개의 결과를 찾았습니다.");
            for(int i=0;i<hits.length;++i) {
                int docId = hits[i].doc;
                Document d = searcher.doc(docId);
                //스코어 출력
                result.add(d.get("pathName") + "\t" + hits[i].score);

                //콘솔창 출력
                LOGGER.info((i + 1) + ". [제목]" + "\t" + d.get("pathName")
                        + ". [한글변환 제목]" + "\t" + d.get("eng2korPathName") + "\n");
            }
        }catch(Exception e){
            LOGGER.error("색인 검색 오류: { }", e);
        }

        return result;
    }
}
