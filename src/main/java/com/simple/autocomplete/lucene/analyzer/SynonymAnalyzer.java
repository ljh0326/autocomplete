package com.simple.autocomplete.lucene.analyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.synonym.SynonymGraphFilter;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author LEE
 * @contact ljh0326s@gmail.com
 *
 * 동의어를 색인하는 분석기
 * ex) good = nice를 설정해 줄 수 있음
 */
public class SynonymAnalyzer extends Analyzer {
    private static Logger LOGGER = LoggerFactory.getLogger(SynonymAnalyzer.class);
    private SynonymMap synonymMap;

    public SynonymAnalyzer(SynonymMap synonymMap){
        this.synonymMap = synonymMap;
    }

    /**
     * 기존 SynonymFilter가 deprecated 되어 SynonymGraphFilter를 대체로 사용해야 한다.
     */
    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        // Tokenizer로는 StandardTokenizer를 사용
        Tokenizer tokenizer = new StandardTokenizer();
        SynonymGraphFilter synonymGraphFilter = null;
        try {
            // 색인 분석 시 미리 설정한 SynonymMap으로 동의어를 포함한 문서를 찾는다
            synonymGraphFilter = new SynonymGraphFilter(tokenizer, synonymMap, true);
            return new TokenStreamComponents(tokenizer, synonymGraphFilter);
        }catch (Exception e){
            LOGGER.error("KORNgramAnalyzer 생성 오류: {}", e);
        }
        return null;
    }
}
