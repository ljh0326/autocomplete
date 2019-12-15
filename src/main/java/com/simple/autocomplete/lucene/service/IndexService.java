package com.simple.autocomplete.lucene.service;

import com.simple.autocomplete.lucene.analyzer.CustomNgramAnalyzer;
import com.simple.autocomplete.title.domain.TitleInfo;
import com.simple.autocomplete.utils.CsvLoader;
import com.simple.autocomplete.utils.DateUtil;
import com.simple.autocomplete.utils.Eng2KorConverter;
import com.simple.autocomplete.utils.PropertyLoader;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author LEE
 * @contact ljh0326s@gmail.com
 */
public class IndexService {
    private static final Logger LOGGER = LoggerFactory.getLogger(IndexService.class);

    //분석기. IndexServiceFactory에서 주입받는다.

    /**
     * 데이터를 색인하는 메서드
     */
    public boolean index() {
        //색인할 데이터
        List<TitleInfo> objectList = CsvLoader.loadCsvInfo();
        //objectList에 담긴 데이터들을 색인한다.
        return index(objectList);
    }

    public boolean index(List<TitleInfo> objectList){
        try(IndexWriter writer = initWriter(IndexWriterConfig.OpenMode.CREATE)) {
            for (TitleInfo titleInfo : objectList) {
                addDocument(titleInfo, writer);
            }
            //색인이 끝난후 적용한다.
            writer.commit();
            return true;
        } catch (IOException e) {
            LOGGER.error("IndexWriter 생성 오류: IndexService directory OR config OR objectList 살펴볼것 {e}", e);
            return false;
        }
    }

    /**
     * 새로운 색인을 추가하는 메서드
     * @param titleInfo 추가할 title + contentNo
     * @return 추가 성공여부
     */
    public boolean add(TitleInfo titleInfo){

        try(IndexWriter writer = initWriter(IndexWriterConfig.OpenMode.CREATE_OR_APPEND)) {
            //writer에 새로운 document 추가 후 커밋
            addDocument(titleInfo, writer);
            writer.commit();
            return true;
        } catch (IOException e) {
            LOGGER.error("IndexWriter 추가 오류: IndexService directory OR config OR parameter 살펴볼것 {e}", e);
            return false;
        }
    }

    /**
     * 기존 색인을 수정하는 메서드
     * @param titleInfo 수정된 titleInfo
     * @return 수정 성공여부
     */
    public boolean update(TitleInfo titleInfo){
        try(IndexWriter writer = initWriter(IndexWriterConfig.OpenMode.CREATE_OR_APPEND)) {
            //writer에 기존 document 수정 후 커밋
            updateDocument(titleInfo, writer);
            writer.commit();
            return true;
        } catch (IOException e) {
            LOGGER.error("IndexWriter 추가 오류: IndexService directory OR config OR parameter 살펴볼것 {e}", e);
            return false;
        }
    }

    /**
     * 기존 색인을 삭제하는 메서드
     * @param titleInfo 삭제할 contentNo
     * @return 삭제성공여부
     */
    public boolean delete(TitleInfo titleInfo){
        try(IndexWriter writer = initWriter(IndexWriterConfig.OpenMode.CREATE_OR_APPEND)) {
            deleteDocument(titleInfo, writer);
            writer.commit();
            return true;
        } catch (IOException e) {
            LOGGER.error("IndexWriter 수정 오류: IndexService directory OR config OR parameter 살펴볼것 {e}", e);
            return false;
        }
    }

    /**
     * 새로운 문서를 추가하는 메서드
     * @param titleInfo 추가할 정보들
     * @param writer writer
     * @throws IOException
     */
    private void addDocument(TitleInfo titleInfo, IndexWriter writer) throws IOException {
        writer.addDocument(createDocument(titleInfo));
    }

    /**
     * 기존 문서를 수정 하는 메서드
     * @param titleInfo 업데이트할 정보
     * @param writer writer
     * @throws IOException
     */
    private void updateDocument(TitleInfo titleInfo, IndexWriter writer) throws IOException {
        //수정될 정보의 새로운 문서를 생성한다.
        Document tempDoc = createDocument(titleInfo);
        //contentNo를 이용해 기존 문서를 검색해온다.
        Term originDocument = new Term("contentsNo", titleInfo.getContentsNo());
        //문서 수정후 반영
        writer.updateDocument(originDocument, tempDoc);
        writer.flush();
    }

    /**
     * 기존 문서를 삭제하는 메서드
     * @param titleInfo 삭제된 title정보
     * @param writer writer
     * @throws IOException
     */
    private void deleteDocument(TitleInfo titleInfo, IndexWriter writer) throws IOException {
        //contentNo를 이용해 삭제할 문서를 불러온다.
        Query query =  new TermQuery(new Term("contentsNo", titleInfo.getContentsNo()));
        //문서 삭제후 반영
        writer.deleteDocuments(query);
        writer.flush();
    }

    /**
     * 새문서를 추가하는 메서드
     * @param titleInfo document로 생성될 정보들
     * @return 만들어진 document
     */
    private Document createDocument(TitleInfo titleInfo){
        //영 -> 한 컨버터
        Eng2KorConverter eng2Kor = new Eng2KorConverter();

        //TextField      토큰화가 필요한 필드
        //StringField    토큰화 안돼는 필드
        //StoredField    값만 저장하는 필드
        //DocValuesField 커스텀 스코어 관련 필드
        Document doc = new Document();
        doc.add(new StringField("contentsNo", titleInfo.getContentsNo(), Field.Store.YES));
        doc.add(new TextField("eng2korAutoKeyword", eng2Kor.engToKor(titleInfo.getAutoKeyword().toLowerCase()), Field.Store.NO));
        doc.add(new StringField("autoKeyword", titleInfo.getAutoKeyword(), Field.Store.YES));
        doc.add(new StoredField("hit", Optional.ofNullable(titleInfo.getHit()).orElse("0")));
        doc.add(new StringField("regDate", Optional.ofNullable(titleInfo.getRegDate()).orElse(DateTools.dateToString(new Date(), DateTools.Resolution.DAY)), Field.Store.YES));

        //Score 조회수 가산치 = (조회수 / 100) / (현재일과 등록일의 차이 수), 오래될수록 조회수가 쌓이는건 당연하니까
        //날짜가 최근이고 조회수 높은게 더 가산점 받을 수 있도록 설정
        double boostValue = (Double.parseDouble(Optional.ofNullable(titleInfo.getHit()).orElse("0"))/100) /
                getDiffDay(Optional.ofNullable(titleInfo.getRegDate()).orElse(DateTools.dateToString(new Date(), DateTools.Resolution.DAY)));

        doc.add(new DoubleDocValuesField("hit_boost", boostValue));
        return doc;
    }

    /**
     * indexWriter 생성하는 메서드
     * @param openMode 오픈모드 Append OR Create
     * @return IndexWriter
     * @throws IOException
     */
    private IndexWriter initWriter(IndexWriterConfig.OpenMode openMode) throws IOException {
        /* writer 생성시 필요한 것
         * IndexWriter = Config + Directory
         * 1. Config   (analyzer + 오픈 설정)
         * 2. Directory(색인이 저장된 폴더)
         */
        Analyzer analyzer = new CustomNgramAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer).setOpenMode(openMode);
        Directory indexDirectory = getDirectory();
        return new IndexWriter(indexDirectory, config);
    }

    /**
     * indexReader를 반환하는 메서드
     * @return indexReader
     * @throws IOException
     */
    private IndexReader initReader() throws IOException {
        Directory indexDirectory = getDirectory();
        return DirectoryReader.open(indexDirectory);
    }

    /**
     * IndexWriter 와 IndexReadr에 필요한 Directory를 반환하는 메서드
     * @return Directory
     * @throws IOException
     */
    private Directory getDirectory() throws IOException {
        //색인 경로
        String indexPath = PropertyLoader.getInstance().getPropertyValue("INDEX_DIR_PATH");
        return MMapDirectory.open(Paths.get(indexPath));
    }

    /**
     * 날짜차이 일수를 구하는 메서드
     * @param regDate 생성일
     * @return 두 날짜 차이 일수
     */
    private double getDiffDay(String regDate){
        //스트링형식을 Date로 변환

        Date startDate = DateUtil.getDate(regDate);
        Date endDate = new Date();
        double day = DateUtil.dayDiffDate(startDate, endDate);

        if(day == 0) return 1;
        return day;
    }
}