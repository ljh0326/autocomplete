package com.simple.autocomplete.utils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

/**
 *  @author LEE
 *  @contact ljh0326s@gmail.com
 *  application.properties에 설정된 속성정보를 읽어서 제공하는 클래스
 */
public class PropertyLoader {
    //설정 파일 경로
    private static final Logger LOGGER = LoggerFactory.getLogger(PropertyLoader.class);
    private static final String PROPERTY_FILE_LOCATION = "application.properties";
    private static final PropertyLoader instance = new PropertyLoader();

    //속성을 맵으로 저장
    private Map<String, String> propMap;

    private PropertyLoader() {
        loadProperties();
    }

    //속성을 불러오는 메서드
    private void loadProperties() {
        Properties prop = new Properties();
        ClassPathResource classPathResource = new ClassPathResource(PROPERTY_FILE_LOCATION);

        if(!classPathResource.exists()){
            LOGGER.error("Invalid filPath : {}", PROPERTY_FILE_LOCATION);
        }

        try {
            // FileInputStream으로 설정 파일에서 데이터를 읽어 온다
            prop.load(new InputStreamReader(classPathResource.getInputStream(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 프로퍼티 검색이 용이하도록 map에 추가한다
        propMap = new HashMap<>();
        prop.forEach((key, value) -> propMap.put((String)key, (String)value));
    }

    //유일한 객체 반환
    public static PropertyLoader getInstance() {
        return instance;
    }

    //해당 키에 해당하는 값 전달
    public String getPropertyValue(String key) {
        return propMap.get(key);
    }

}
