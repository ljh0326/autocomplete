package com.simple.autocomplete.title;

import com.simple.autocomplete.title.domain.TitleInfo;
import com.simple.autocomplete.title.service.AutoCompleteService;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/autocomplete")
public class AutocompleteController {

    private final AutoCompleteService autoCompleteService;

    @Autowired
    public AutocompleteController(AutoCompleteService autoCompleteService) {
        this.autoCompleteService = autoCompleteService;
    }

    /**
     * 색인된 데이터를 조회한다.
     * @param searchWord 검색어
     * @return 검색된 titleInfo
     */
    @GetMapping("/getResult")
    @ResponseBody
    @Cacheable(value = "autocompleteResult", key= "#searchWord", condition = "#searchWord.length() > 1")
    public Set<TitleInfo> autocomplete(@RequestParam(value = "searchWord", required = false)String searchWord){
        return autoCompleteService.getSearchResult(searchWord);
    }

    /**
     * 새로운 색인 추가
     * @param titleInfo 추가할 색인 정보
     * @return 성공여부
     */
    @PostMapping("/add")
    @CacheEvict(value = "autocompleteResult", allEntries = true)
    public ResponseEntity<String> addAutocomplete(@RequestBody TitleInfo titleInfo){

        if(StringUtils.isEmpty(titleInfo.getAutoKeyword())){
            return new ResponseEntity<>("title is empty", HttpStatus.FORBIDDEN);
        }

        if(autoCompleteService.addAutocomplete(titleInfo)){
            return new ResponseEntity<>("autocomplete add success", HttpStatus.OK);
        }

        return new ResponseEntity<>("autocomplete add fail", HttpStatus.FORBIDDEN);
    }

    /**
     * 기존 색인 업데이트
     * @param titleInfo 수정할 색인 정보
     * @return 수정 성공여부
     */
    @PostMapping("/update")
    @CacheEvict(value = "autocompleteResult", allEntries = true)
    public ResponseEntity<String> updateAutocomplete(@RequestBody TitleInfo titleInfo){
        if(StringUtils.isEmpty(titleInfo.getAutoKeyword()) ){
            return new ResponseEntity<>("title is empty", HttpStatus.FORBIDDEN);
        }

        if(autoCompleteService.updateAutocomplete(titleInfo)){
            return new ResponseEntity<>("autocomplete update success", HttpStatus.OK);
        }

        return new ResponseEntity<>("autocomplete update fail", HttpStatus.FORBIDDEN);
    }

    /**
     * 기존 색인 삭제
     * @param titleInfo 삭제할 색인 정보
     * @return 삭제 성공여부
     */
    @PostMapping("/delete")
    @CacheEvict(value = "autocompleteResult", allEntries = true)
    public ResponseEntity<String> deleteAutocomplete(@RequestBody TitleInfo titleInfo){

        if(StringUtils.isEmpty(titleInfo.getContentsNo())){
            return new ResponseEntity<>("titleObject not exit", HttpStatus.FORBIDDEN);
        }

        if(autoCompleteService.deleteAutocomplete(titleInfo))
            return new ResponseEntity<>( "delete success", HttpStatus.OK);

        return new ResponseEntity<>("delete fail", HttpStatus.FORBIDDEN);
    }

    /**
     * 색인 요청
     * @param list 색인할 데이터
     * @return 색인성공여부
     */
    @PostMapping("/index")
    @CacheEvict(value = "autocompleteResult", allEntries = true)
    public ResponseEntity<String> requestIndex(@RequestBody List<TitleInfo> list){
        if(autoCompleteService.initIndex(list))
            return new ResponseEntity<>( "index success", HttpStatus.OK);

        return new ResponseEntity<>("index fail", HttpStatus.FORBIDDEN);
    }
}

