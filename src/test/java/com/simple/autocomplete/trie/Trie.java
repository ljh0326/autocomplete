package com.simple.autocomplete.trie;

/**
 * The type Trie.
 * 트라이 자료구조
 */
class Trie {
    private TrieNode root;

    Trie(){
        root = new TrieNode();
    }

    /*
        문자열 삽입
     */
    void insert(String word){
        TrieNode current = root;

        //문자하나하나에 해당하는 노드를 생성해 준다.
        for(int i = 0; i < word.length(); i++){
            current = current.getChildren().computeIfAbsent(word.charAt(i), c -> new TrieNode());
        }

        //문자열 끝을 표시해 준다.
        current.setEndOfWord(true);
    }

    boolean delete(String word){
        return delete(root, word, 0);
    }

    /*
        해당 단어가 포함되 있는지 확인
     */
    boolean containsNode(String word){
        TrieNode current = root;

        //단어를 키로하는 맵이 있는지 살펴본다.
        for(int i = 0; i <word.length(); i++){
            char ch = word.charAt((i));
            TrieNode node = current.getChildren().get(ch);
            if(node == null){
                return false;
            }

            current = node;
        }

        //만약 단어의 끝이라면 true 아니라면 false
        return current.isEndOfWord();
    }

    /*
        해당 단어 삭제
     */
    private boolean delete(TrieNode current, String word, int index){

        //입력된 단어 끝까지 왔는데, 해당 단어가 없다면(끝이아니라면) 삭제 실패
        if(index == word.length()) {
            if (!current.isEndOfWord()) {
                return false;
            }
            //그 밑에 노드가 더있을 수 있으니까 false로 전환 pies, pie가 저장되 있을 때 pie가 삭제된 경우
            current.setEndOfWord(false);
            return current.getChildren().isEmpty();
        }

        //해당 인덱스에 해당하는 단어 노드가 있는지 확인
        char ch = word.charAt(index);
        TrieNode node = current.getChildren().get(ch);

        //만약 해당 단어가 없다면 false
        if(node == null){
            return false;
        }

        //자식노드가 저장된 단어의 끝도 아니고 리턴값이 true면 자식노드 삭제
        boolean shouldDeleteCurrentNode = delete(node, word, index + 1) && !node.isEndOfWord();

        if(shouldDeleteCurrentNode){
            current.getChildren().remove(ch);
            return current.getChildren().isEmpty();
        }

        return false;
    }

    boolean isEmpty(){
        return root == null;
    }
}
