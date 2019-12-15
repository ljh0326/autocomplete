package com.simple.autocomplete.utils;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.simple.autocomplete.title.domain.TitleInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 *  @author LEE
 *  @contact ljh0326s@gmail.com
 *  CSV파일을 읽어서 객체화 시켜주는 메서드
 */
public class CsvLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(CsvLoader.class);
    private static String csvFilePath;
    private static Class clazz;
    private static final CsvLoader instance = new CsvLoader();

    /**
     *  생성자 파일경로를 초기화 한다.
     *  파일 경로는 application.properties에서 설정할 수 있다.
     */
    static{
        try {
            csvFilePath = PropertyLoader.getInstance().getPropertyValue("WALK_WAY_FILE_PATH");
            clazz = Class.forName(PropertyLoader.getInstance().getPropertyValue("WALK_WAY_OBJECT"));
        } catch (ClassNotFoundException e) {
            LOGGER.error("CsvLoader init error: {}", e);
        }
    }

    private CsvLoader(){ }

    public static CsvLoader getInstance() {
        return instance;
    }

    public static void setCsvFilePath(String csvFilePath) {
        CsvLoader.csvFilePath = csvFilePath;
    }

    public static void setClazz(Class clazz) {
        CsvLoader.clazz = clazz;
    }

    /**
     * csv파일을 주어진 Class리스트로 변환한다.
     * @return 주어진 been Class리스트
     */
    public static <T> List<T> loadCsvInfo(){
        List<T> objectList = null;
        //파일에서 스트림 형태로 CSV정보를 한줄씩 읽는다.
        try(Reader reader = Files.newBufferedReader(Paths.get(csvFilePath))){
            CsvToBean<T> csvToBean = new CsvToBeanBuilder(reader)
                    .withType(clazz.newInstance().getClass())
                    .withSkipLines(1)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            objectList = csvToBean.parse();

        }catch (Exception e){
            e.printStackTrace();
        }
        return objectList;
    }

    public static void main(String[] args) {
        List<TitleInfo> titleInfoList = CsvLoader.loadCsvInfo();
        titleInfoList.forEach(System.out::println);
    }
}
