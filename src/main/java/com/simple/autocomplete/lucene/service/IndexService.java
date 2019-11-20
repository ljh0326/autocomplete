package com.simple.autocomplete.lucene.service;

import com.simple.autocomplete.utils.PropertyLoader;
import org.apache.lucene.index.IndexWriter;

/**
 * @author LEE
 * @contact ljh0326s@gmail.com
 *
 *
 * 색인할때 필요한 것
 * 1. IndexWriter
 *    1.1 IndexConfig
 *        1.1.1 Analyzer
 *    1.2 Direct 인댁스 저장소`
 * 2. 색인할 데이터
 */
public interface IndexService {

    //색인할 경로
    static final String indexPath = PropertyLoader.getInstance().getPropertyValue("INDEX_DIR_PATH");

    //데이터와 분석기를 받아서 색인하는 메서드 램 기반 색인할지 파일기반 색인을 할지 성택할 수 있다.
    public <T> void index();


    //색인 중 Document를 만드는 메서드
    public <T> void addDocument(T i, IndexWriter writer);

}
