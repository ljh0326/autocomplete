package com.simple.autocomplete.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 *  @author LEE
 *  @contact ljh0326s@gmail.com
 *  application.properties에 설정된 속성정보를 읽어서 제공하는 클래스
 */
public class PropertyLoader {
    //설정 파일 경로
    private static final String PROPERTY_FILE_LOCATION = "src/main/resources/application.properties";
    private static final PropertyLoader instance = new PropertyLoader();

    //속성을 맵으로 저장
    private Map<String, String> propMap;

    private PropertyLoader() {
        loadProperties();
    }

    //속성을 불러오는 메서드
    private void loadProperties() {
        Properties prop = new Properties();
        Path path = Paths.get(PROPERTY_FILE_LOCATION);

        try {
            // FileInputStream으로 설정 파일에서 데이터를 읽어 온다
            prop.load(new InputStreamReader(new FileInputStream(path.toFile()), StandardCharsets.UTF_8));
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

    public static void main(String[] args) {
        PropertyLoader propertyLoader = PropertyLoader.getInstance();
        System.out.println(propertyLoader.getPropertyValue("CSV_FILE_PATH"));
    }
}
