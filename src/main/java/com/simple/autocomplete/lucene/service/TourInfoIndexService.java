package com.simple.autocomplete.lucene.service;

import com.simple.autocomplete.title.domain.TourInfo;
import com.simple.autocomplete.utils.Eng2KorConverter;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author LEE
 * @contact ljh0326s@gmail.com
 * TourInfo를 색인하는 서비스
 */
public class TourInfoIndexService implements IndexService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TourInfoIndexService.class);
    private List<TourInfo> objectList;
    private Analyzer analyzer;
    private Eng2KorConverter eng2Kor = new Eng2KorConverter();

    TourInfoIndexService(List<TourInfo> objectList, Analyzer analyzer){
        this.objectList = objectList;
        this.analyzer = analyzer;
    }

    @Override
    public void index() {
        try {
            Directory indexDirectory = MMapDirectory.open(Paths.get(indexPath));
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            IndexWriter writer = new IndexWriter(indexDirectory, config);
            objectList.forEach(i -> addDocument(i, writer));
            writer.commit();
        } catch (IOException e) {
            LOGGER.error("Add document error { }", e);
        }
    }

    @Override
    public <T> void addDocument(T i, IndexWriter writer) {
        TourInfo tourInfo = (TourInfo)i;
        Document doc = new Document();
        //TextField: 토큰화가 필요한 필드
        doc.add(new TextField("eng2korPathName", eng2Kor.engToKor(tourInfo.getPathName().toLowerCase()), Field.Store.YES));
        //StoredField 저장만 필요한 필드, 색인은 모든 영어를 한글로 바꾼 eng2korTitle로 하고 결과는 title로 보여줄 예정
        doc.add(new StoredField("pathName", tourInfo.getPathName()));
        doc.add(new StoredField("level", tourInfo.getLevel()));
        doc.add(new StoredField("hour", tourInfo.getHour()));
        doc.add(new StoredField("distance", tourInfo.getDistance()));

        try{
            writer.addDocument(doc);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
