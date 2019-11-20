package com.simple.autocomplete.lucene.analyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.ngram.NGramTokenFilter;
import org.apache.lucene.analysis.ngram.NGramTokenizer;
import org.apache.lucene.analysis.pattern.PatternReplaceCharFilter;
import org.apache.lucene.analysis.synonym.SynonymGraphFilter;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.apache.lucene.util.CharsRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.util.regex.Pattern;

/**
 * @author LEE
 * @contact ljh0326s@gmail.com
 *
 * 주어진 문자를 N개로 나누고 동의어 처리하는 분석기
 * N이 2일때
 * ex) 오늘은 날씨가 매우 좋군요 -> 오늘, 늘은, 은 ,  날, 날씨, 씨가, 가 ,  매, 매우, 우,  좋, 좋군, 군요
 * ex) 문자 == 메일
 */
public class CustomNgramAnalyzer extends Analyzer {
    private static Logger LOGGER = LoggerFactory.getLogger(CustomNgramAnalyzer.class);

    //SynonymGraphFilter를 이용하기 위한 동의어 맵
    private SynonymMap synonymMap;

    public CustomNgramAnalyzer(){
        SynonymMap.Builder builder = new SynonymMap.Builder(true);
        // add(출력,입력)
        // 동의어 처리를 둘다 하려면 둘다 넣어줘야한다.
        builder.add(new CharsRef("숲길"), new CharsRef("숲속길"), true);
        builder.add(new CharsRef("웨이"), new CharsRef("길"), true);
        builder.add(new CharsRef("길"), new CharsRef("웨이"), true);

        try {
            this.synonymMap = builder.build();
        } catch (IOException e) {
            LOGGER.error("CustomNgramAnalyzer 초기화 오류: {}", e);
        }
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        //NGramTokenizer param (min N, max N) NGramTokenizer(2,3)은 N=2, N=3일때 term을 모두 만든다.
        Tokenizer tokenizer = new NGramTokenizer(2,2);
        TokenStream filter = new NGramTokenFilter(tokenizer, 2);
        filter = new SynonymGraphFilter(tokenizer, synonymMap, true);
        return new TokenStreamComponents(tokenizer, filter);
    }

    //CharFilter를 사용해 데이터를 정제한다.
    //모든 공백제거  ex) 안 녕 하 세 요 -> 안녕하세요
    @Override
    protected Reader initReader(String fieldName, Reader reader){
        return new PatternReplaceCharFilter(Pattern.compile(" "), "", reader);
    }
}
