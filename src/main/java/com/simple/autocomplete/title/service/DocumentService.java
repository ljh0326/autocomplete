package com.simple.autocomplete.title.service;

import com.simple.autocomplete.title.domain.DocumentVo;
import com.simple.autocomplete.title.persistance.DocumentRepository;
import com.simple.autocomplete.utils.Eng2KorConverter;
import com.simple.autocomplete.utils.Trie;
import com.simple.autocomplete.utils.UnicodeKorean;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DocumentService {

    private final UnicodeKorean deComposer;
    private final Eng2KorConverter engToKor;
    private final Trie trieSearcher = new Trie(true);
    private final DocumentRepository documentRepository;

    @PostConstruct
    private void init(){
        trieSearcher.clear();
        List<DocumentVo> documentList = documentRepository.findAll();
        List<String> titleList = documentList.stream().map(DocumentVo::getTitle).collect(Collectors.toList());

        for (String title : titleList) {
            //타이틀을 음소분해한 결과와 타이틀을 삽입한다.
            //ex) add("ㅇㅏㄴㄴㅕㅇ", "안녕");
            trieSearcher.add(deComposer.getResult(title), title);

            //앞에 어떤안녕이 붙을줄 모르기 때문에 리버스해서 다시 삽입해준다.
            //ex) add("ㅇㅕㄴㄴㅏㅇ", "안녕")
            //이런식으로 reverse해서 넣어주면 고양이 안녕이라고 검색해도 안녕이라는 키워드가 검색된다.
            trieSearcher.add(deComposer.getReverseResult(title), title);
        }
    }

    public DocumentService(UnicodeKorean deComposer, Eng2KorConverter engToKor, DocumentRepository documentRepository) {
        this.deComposer = deComposer;
        this.engToKor = engToKor;
        this.documentRepository = documentRepository;
    }

    public Set<String> autoComplete(String searchWord) {
        Set<String> result = new HashSet<>();
        		/*
			안녕하세요같이 앞에서 일치하는 검색어를 찾기위해 음소분해된 키워드 생성
			ex) -> 안녕 : ㅇㅏㄴㄴㅕㅇ
		 */
        String kwd = deComposer.getResult(searchWord);
        getSearchResult(result, kwd);

		/*
			정말안녕등 앞에 뭘 붙은 단어를 찾기위해 역으로 음소분해된 키워드 생성
			ex) -> 안녕 : ㅇㅕㄴㄴㅏㅇ
		 */
        kwd = deComposer.getReverseResult(searchWord);
        getSearchResult(result, kwd);

		/*
			dkssudgktpdy 같이 한글이지만 영어로 입력된 단어들 검색 추천해주기위해
			 영 -> 한 변경해서 음소분해 키워드 생성
			ex) -> dkssud : ㅇㅏㄴㄴㅕㅇ
		 */
        kwd = deComposer.getResult(engToKor.engToKor(searchWord));
        getSearchResult(result, kwd);

		/*
			wjdakfdkssud 같이 한글이지만 영어로 입력된 단어들 검색 추천해주기위해
			 영 -> 한 변경한뒤 역음소분해 키워드 생성
			ex) -> dkssud : ㅇㅕㄴㄴㅏㅇ
		 */
        kwd = deComposer.getReverseResult(engToKor.engToKor(searchWord));
        getSearchResult(result, kwd);

        return result;
    }

    private void getSearchResult(Set<String> result, String kwd) {
        Iterator searchResult = trieSearcher.getPrefixedBy(kwd);
        while(searchResult.hasNext()) {
            result.add((String)searchResult.next());
        }
    }
}
