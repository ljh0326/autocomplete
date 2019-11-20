package com.simple.autocomplete.lucene.service;

import com.simple.autocomplete.lucene.analyzer.KORNgramAnalyzer;
import com.simple.autocomplete.title.domain.TourInfo;
import com.simple.autocomplete.utils.CsvLoader;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * @author LEE
 * @contact ljh0326s@gmail.com
 *
 * 주어진 정보를 분류기로 분류하는 서비스
 * 분류기는 글을 토큰화 시키는 역활을 한다.
 * ex) I likes blue sky -> i/like/blue/sky
 */
public class AnalyzerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AnalyzerService.class);

    public void analyzeText(List<TourInfo> objectList, Analyzer analyzer) {

        StringBuffer stringBuffer = new StringBuffer();
        objectList.stream()
                .filter(tourInfo -> StringUtils.isNotEmpty(tourInfo.getPathName()))
                .forEach(tourInfo -> {
                    //분석기 기준으로 토큰 스트림이 생김
                    try (TokenStream tokenStream = analyzer.tokenStream("description", tourInfo.getDescription())) {

                        // Token String을 가져오기 위한 CharTermAttribute 설정
                        CharTermAttribute cta = tokenStream.addAttribute(CharTermAttribute.class);
                        // 스트림의 시작을 리셋한다.(필수)
                        tokenStream.reset();
                        stringBuffer.append("\n");
                        // 토큰을 순차적으로 읽는다
                        while (tokenStream.incrementToken()) {
                            // 토큰을 String Value로 표시한다.
                            stringBuffer.append(cta).append(" | ");
                        }
                        tokenStream.end(); // 사용을 마친 TokenStream을 종료 처리한다.
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
        LOGGER.info("토큰화 결과 {}", stringBuffer.toString());
    }

    public static void main(String[] args) {

        // CSV 파일에서 데이터를 읽는다.
        List<TourInfo> titleInfoList = CsvLoader.loadCsvInfo();

        // 분석기 서비스를 생성한다.
        AnalyzerService analyzerService = new AnalyzerService();

        // StandardAnalyzer를 테스트한다.
        analyzerService.analyzeText(titleInfoList, KORNgramAnalyzer.getAnalyzer());

    }
}
