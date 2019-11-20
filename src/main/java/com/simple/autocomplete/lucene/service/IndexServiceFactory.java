package com.simple.autocomplete.lucene.service;


import com.simple.autocomplete.lucene.analyzer.CustomNgramAnalyzer;
import com.simple.autocomplete.lucene.analyzer.KORNgramAnalyzer;
import com.simple.autocomplete.lucene.analyzer.SynonymAnalyzer;
import com.simple.autocomplete.title.domain.TourInfo;
import com.simple.autocomplete.utils.CsvLoader;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.ko.KoreanAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.apache.lucene.util.CharsRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * @author LEE
 * @contact ljh0326s@gmail.com
 * 주어진 타입에 해당하는 인덱스 서비스를 반환하는 팩토리
 * 타입종류는 enum Type을 참고할 것
 */
public class IndexServiceFactory {
    private static Logger LOGGER = LoggerFactory.getLogger(IndexServiceFactory.class);

    //서비스와 분석기 타입 종류를 선택할 때 사용한다.
    public static enum Type{
        /** 기본 분석기 */ STANDARD,
        /** 한글 분석기 */ KOR,
        /** 동의어 분석기 */ SYNONYM,
        /** Custom 분석기 */ CUSTOM,
        /** KOR_NGRAM 분석기 */ KOR_NGRAM,
    }

    //색인에 사용될 데이터
    private static List<TourInfo> objectList;

    //색인에 사용할 데이터 초기화
    static{
        objectList = CsvLoader.loadCsvInfo();
    }

    //객체생성 방지
    private IndexServiceFactory(){};

    //사용되는 데이터를 변경하고 싶을 때 사용
    public static void setObjectList(List<TourInfo> objectList) {
        IndexServiceFactory.objectList = objectList;
    }

    //주어진 타입을 이용해 분석기를 생성하고 서비스를 반환한다.
    public static IndexService getIndexService(Type type){
        Analyzer analyzer = getAnalyzer(type);
        return new TourInfoIndexService(objectList, analyzer);
    }

    //IndexService에서 사용할 분석기 반환
     private static Analyzer getAnalyzer(Type type){
         switch (type){
             case SYNONYM: {
                 SynonymMap.Builder builder = new SynonymMap.Builder(true);
                 // add(출력,입력)
                 // 동의어 처리를 둘다 하려면 둘다 넣어줘야한다.
                 builder.add(new CharsRef("good"), new CharsRef("nice"), true);
                 builder.add(new CharsRef("좋다"), new CharsRef("너무"), true);
                 builder.add(new CharsRef("너무"), new CharsRef("좋다"), true);
                 try {
                     return new SynonymAnalyzer(builder.build());
                 } catch (IOException e) {
                     LOGGER.error("동의어 처리오류 { }", e);
                 }
             }
             case STANDARD: return new StandardAnalyzer();
             case KOR: return new KoreanAnalyzer();
             case CUSTOM: return new CustomNgramAnalyzer();
             case KOR_NGRAM: return KORNgramAnalyzer.getAnalyzer();
             default: return new SimpleAnalyzer();
         }
     }
}


