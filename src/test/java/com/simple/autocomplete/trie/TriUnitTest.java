package com.simple.autocomplete.trie;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * 트라이 자료구조 유닛 테스트
 * The type Tri unit test.
 */
public class TriUnitTest {


    /**
     * When empty trie then no elements.
     */
    @Test
    public void whenEmptyTrie_thenNoElements(){
        Trie trie = new Trie();
        assertFalse(trie.isEmpty());
    }

    /**
     * 문자 추가했을 때 잘 반환되는지 확인
     */
    @Test
    public void givenATrie_whenAddingElements_thenTrieNotEmpty(){
        Trie trie = createExampleTrie();
        assertFalse(trie.isEmpty());
    }


    /**
     * 문자를 추가했을 때 그 트라이가 그 문자들을 잘 갖고있는지 테스트
     */
    @Test
    public void givenTrie_whenAddingElements_thenTrieHasThoseElements(){
        Trie trie = createExampleTrie();

        assertFalse(trie.containsNode("3"));
        assertFalse(trie.containsNode("vida"));

        assertTrue(trie.containsNode("Programming"));
        assertTrue(trie.containsNode("a"));
        assertTrue(trie.containsNode("way"));
        assertTrue(trie.containsNode("of"));
        assertTrue(trie.containsNode("life"));
    }

    /*
        삭제가 정상적으로 동작하는지 테스트
     */
    @Test
    public void givenAtrie_whenDeletingElements_thenTreeDoesNotContainThoseElements(){
        Trie trie = createExampleTrie();
        trie.delete("Programming");
        assertFalse(trie.containsNode("Programming"));
    }

    /*
        삭제되는 단어와 겹치는 부분이 남아있는지 테스트
     */
    @Test
    public void givenATrie_whenDeletingOverlappingElements_thenDontDeleteSubElement(){
        Trie trie = createExampleTrie();

        trie.insert("pie");
        trie.insert("pies");

        trie.delete("pies");

        assertTrue(trie.containsNode("pie"));
        assertFalse(trie.containsNode("pies"));
    }

    private Trie createExampleTrie(){
        Trie trie = new Trie();
        trie.insert("Programming");
        trie.insert("is");
        trie.insert("a");
        trie.insert("way");
        trie.insert("of");
        trie.insert("life");

        return trie;
    }
}
