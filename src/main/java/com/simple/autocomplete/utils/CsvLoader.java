package com.simple.autocomplete.utils;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

/**
 *  @author LEE
 *  @contact ljh0326s@gmail.com
 *  CSV파일을 읽어서 객체화 시켜주는 메서드
 */
public class CsvLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(CsvLoader.class);
    private static final CsvLoader instance = new CsvLoader();

    /**
     * 여러 CSV 경로를 가르키는 enum
     */
    public enum CsvFilePath{
        /** 타이틀 csv 경로 */ TITLE_FILE_PATH("TITLE_FILE_PATH");

        private String path;

        CsvFilePath(String path){
            this.path = path;
        }

        public String getPath() {
            return path;
        }
    }

    private CsvLoader(){ }
    public static CsvLoader getInstance() {
        return instance;
    }

    /**
     * csv파일을 주어진 Class리스트로 변환한다.
     * @return 주어진 been Class리스트
     */
    public static <T> List<T> loadCsvInfo(CsvFilePath csvFilePath, Class clazz){
        List<T> objectList = null;
        ClassPathResource classPathResource = new ClassPathResource(PropertyLoader.getInstance().getPropertyValue(csvFilePath.getPath()));
        //파일에서 스트림 형태로 CSV정보를 한줄씩 읽는다.
        try(Reader reader = new BufferedReader(new InputStreamReader(classPathResource.getInputStream(), StandardCharsets.UTF_8))){
            CsvToBean<T> csvToBean = new CsvToBeanBuilder(reader)
                    .withType(clazz.getDeclaredConstructor().newInstance().getClass())
                    .withSkipLines(1)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            objectList = csvToBean.parse();

        }catch (Exception e){
            e.printStackTrace();
        }
        return objectList;
    }

}
