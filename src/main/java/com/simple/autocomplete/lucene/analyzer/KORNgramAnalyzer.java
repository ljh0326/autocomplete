package com.simple.autocomplete.lucene.analyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.core.StopFilterFactory;
import org.apache.lucene.analysis.core.WhitespaceTokenizerFactory;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.ko.KoreanPartOfSpeechStopFilterFactory;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilterFactory;
import org.apache.lucene.analysis.miscellaneous.WordDelimiterGraphFilterFactory;
import org.apache.lucene.analysis.ngram.NGramFilterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author LEE
 * @contact ljh0326s@gmail.com
 *
 * 커스텀 분석기
 */
public class KORNgramAnalyzer {
    private static Logger LOGGER = LoggerFactory.getLogger(KORNgramAnalyzer.class);

    private static final KORNgramAnalyzer instance = new KORNgramAnalyzer();
    private static Analyzer analyzer;

    static{
        try {
            analyzer = initAnalyzer();
        } catch (IOException e) {
            LOGGER.error("KORNgramAnalyzer 생성자 오류: {}", e);
        }
    }
    private KORNgramAnalyzer(){ }

    private static KORNgramAnalyzer getInstance() {
        return instance;
    }

    public static Analyzer getAnalyzer() {
        return analyzer;
    }

    private static Analyzer initAnalyzer() throws IOException {
        return CustomAnalyzer.builder()
                //공백으로 구분하는 필터
                .withTokenizer(WhitespaceTokenizerFactory.class)
                //. ,등 필요없는 기호 삭제하는 필터
                .addTokenFilter(WordDelimiterGraphFilterFactory.class)
                // 영어를 위한 불용어 제거하는 필터
                .addTokenFilter(StopFilterFactory.class)
                // 소문자로 통일하는 필터
                .addTokenFilter(LowerCaseFilterFactory.class)
                // 아스키코드 제거 필터
                .addTokenFilter(ASCIIFoldingFilterFactory.class)
                //한국어 형태소 분석
                .addTokenFilter(KoreanPartOfSpeechStopFilterFactory.class)
                .addTokenFilter(NGramFilterFactory.class, "minGramSize", "1", "maxGramSize", "3")
                .build();
    }
}
