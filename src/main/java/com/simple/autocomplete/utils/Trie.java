package com.simple.autocomplete.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.NoSuchElementException;

/**
 * An information reTRIEval tree, a.k.a., a prefix tree. A Trie is similar to a
 * dictionary, except that keys must be strings. Furthermore, Trie provides an
 * efficient means (getPrefixedBy()) to find all values given just a PREFIX of a
 * key.
 * <p>
 *
 * All retrieval operations run in O(nm) time, where n is the size of the
 * key/prefix and m is the size of the alphabet. Some implementations may reduce
 * this to O(n log m) or even O(n) time. Insertion operations are assumed to be
 * infrequent and may be slower. The space required is roughly linear with
 * respect to the sum of the sizes of all keys in the tree, though this may be
 * reduced if many keys have common prefixes.
 * <p>
 *
 * The Trie can be set to ignore case. Doing so is the same as making all keys
 * and prefixes lower case. That means the original keys cannot be extracted
 * from the Trie.
 * <p>
 *
 * Restrictions (not necessarily limitations!)
 * <ul>
 * <li><b>This class is not synchronized.</b> Do that externally if you
 * desire.
 * <li>Keys and values may not be null.
 * <li>The interface to this is not complete.
 * </ul>
 *
 * See http://www.csse.monash.edu.au/~lloyd/tildeAlgDS/Tree/Trie.html for a
 * discussion of Tries.
 */
public class Trie {
    /**
     * Our representation consists of a tree of nodes whose edges are labelled
     * by strings. The first characters of all labels of all edges of a node
     * must be distinct. Typically the edges are sorted, but this is determined
     * by TrieNode.
     * <p>
     *
     * An abstract TrieNode is a mapping from String keys to values, { <K1, V1>,
     * ..., <KN, VN> }, where all Ki and Kj are distinct for all i != j. For any
     * node N, define KEY(N) to be the concatenation of all labels on the edges
     * from the root to that node. Then the abstraction function is:
     * <p>
     *
     * <blockquote> { <KEY(N), N.getValue() | N is a child of root and
     * N.getValue() != null} </blockquote>
     *
     * An earlier version used character labels on edges. This made
     * implementation simpler but used more memory because one node would be
     * allocated to each character in long strings if that string had no common
     * prefixes with other elements of the Trie.
     * <p>
     *
     * <dl>
     * <dt>INVARIANT:</td>
     * <dd>For any node N, for any edges Ei and Ej from N,<br>
     * i != j &lt;==&gt; Ei.getLabel().getCharAt(0) !=
     * Ej.getLabel().getCharAt(0)</dd>
     * <dd>Also, all invariants for TrieNode and TrieEdge must hold.</dd>
     * </dl>
     */
    private TrieNode root;

    /**
     * Indicates whever search keys are case-sensitive or not. If true, keys
     * will be canonicalized to lowercase.
     */
    private boolean ignoreCase;

    /**
     * The constant EmptyIterator to return when nothing matches.
     */
    private final static Iterator EMPTY_ITERATOR = new EmptyIterator();

    /**
     * Constructs a new, empty tree.
     */
    public Trie(boolean ignoreCase) {
        this.ignoreCase = ignoreCase;
        clear();
    }

    /**
     * Makes this empty.
     *
     * @modifies this
     */
    public void clear() {
        this.root = new TrieNode();
    }

    /**
     * Returns the canonical version of the given string.
     * <p>
     *
     * In the basic version, strings are added and searched without
     * modification. So this simply returns its parameter s.
     * <p>
     *
     * Other overrides may also perform a conversion to the NFC form
     * (interoperable across platforms) or to the NFKC form after removal of
     * accents and diacritics from the NFKD form (ideal for searches using
     * strings in natural language).
     * <p>
     *
     * Made public instead of protected, because the public Prefix operations
     * below may need to use a coherent conversion of search prefixes.
     */
    public String canonicalCase(final String s) {
        if (!ignoreCase)
            return s;
        return s.toUpperCase(Locale.US).toLowerCase(Locale.US);
    }

    /**
     * Matches the pattern <tt>b</tt> against the text
     * <tt>a[startOffset...stopOffset - 1]</tt>.
     *
     * @return the first <tt>j</tt> so that:<br>
     *         <tt>0 &lt;= i &lt; b.length()</tt> AND<br>
     *         <tt>a[startOffset + j] != b[j]</tt> [a and b differ]<br>
     *         OR <tt>stopOffset == startOffset + j</tt> [a is undefined];<br>
     *         Returns -1 if no such <tt>j</tt> exists, i.e., there is a
     *         match.<br>
     *         Examples:
     *         <ol>
     *         <li>a = "abcde", startOffset = 0, stopOffset = 5, b = "abc"<br>
     *         abcde ==&gt; returns -1<br>
     *         abc
     *         <li>a = "abcde", startOffset = 1, stopOffset = 5, b = "bXd"<br>
     *         abcde ==&gt; returns 1 bXd
     *         <li>a = "abcde", startOffset = 1, stopOffset = 3, b = "bcd"<br>
     *         abc ==&gt; returns 2<br>
     *         bcd
     *         </ol>
     *
     * @requires 0 &lt;= startOffset &lt;= stopOffset &lt;= a.length()
     */
    private final int match(String a, int startOffset, int stopOffset, String b) {
        // j is an index into b
        // i is a parallel index into a
        int i = startOffset;
        for (int j = 0; j < b.length(); j++) {
            if (i >= stopOffset)
                return j;
            if (a.charAt(i) != b.charAt(j))
                return j;
            i++;
        }
        return -1;
    }


    /**
     * Maps the given key (which may be empty) to the given value.
     *
     * @return the old value associated with key, or <tt>null</tt> if none
     * @requires value != null
     * @modifies this
     */
    public Object add(String key, Object value) {
        // early conversion of key, for best performance
        key = canonicalCase(key);
        // Find the largest prefix of key, key[0..i - 1], already in this.
        TrieNode node = root;
        int i = 0;
        while (i < key.length()) {
            // Find the edge whose label starts with key[i].
            TrieEdge edge = node.get(key.charAt(i));
            if (edge == null) {
                // 1) Additive insert.
                TrieNode newNode = new TrieNode(value);
                node.put(key.substring(i), newNode);
                return null;
            }
            // Now check that rest of label matches
            String label = edge.getLabel();
            int j = match(key, i, key.length(), label);
            if (j >= 0) {
                // 2) Prefix overlaps perfectly with just part of edge label
                // Do split insert as follows...
                //
                // node node ab = label
                // ab | ==> a | a = label[0...j - 1] (inclusive)
                // child intermediate b = label[j...] (inclusive)
                // b / \ c c = key[i + j...] (inclusive)
                // child newNode
                //
                // ...unless c = "", in which case you just do a "splice
                // insert" by ommiting newNew and setting intermediate's value.
                TrieNode child = edge.getChild();
                TrieNode intermediate = new TrieNode();
                String a = label.substring(0, j);
                // Assert.that(canonicalCase(a).equals(a), "Bad edge a");
                String b = label.substring(j);
                // Assert.that(canonicalCase(b).equals(b), "Bad edge a");
                String c = key.substring(i + j);
                if (c.length() > 0) {
                    // Split.
                    TrieNode newNode = new TrieNode(value);
                    node.remove(label.charAt(0));
                    node.put(a, intermediate);
                    intermediate.put(b, child);
                    intermediate.put(c, newNode);
                } else {
                    // Splice.
                    node.remove(label.charAt(0));
                    node.put(a, intermediate);
                    intermediate.put(b, child);
                    intermediate.setValue(value);
                }
                return null;
            }
            // Prefix overlaps perfectly with all of edge label.
            // Keep searching.
            node = edge.getChild();
            i += label.length();
        }
        // 3) Relabel insert. Prefix already in this, though not necessarily
        // associated with a value.
        Object ret = node.getValue();
        node.setValue(value);
        return ret;
    }

    /**
     * Returns the node associated with prefix, or null if none. (internal)
     */
    private TrieNode fetch(String prefix) {
        // This private method uses prefixes already in canonical form.
        TrieNode node = root;
        for (int i = 0; i < prefix.length();) {
            // Find the edge whose label starts with prefix[i].
            TrieEdge edge = node.get(prefix.charAt(i));
            if (edge == null)
                return null;
            // Now check that rest of label matches.
            String label = edge.getLabel();
            int j = match(prefix, i, prefix.length(), label);
            if (j != -1)
                return null;
            i += label.length();
            node = edge.getChild();
        }
        return node;
    }

    /**
     * Returns the value associated with the given key, or null if none.
     *
     * @return the <tt>Object</tt> value or <tt>null</tt>
     */
    public Object get(String key) {
        // early conversion of search key
        key = canonicalCase(key);
        // search the node associated with key, if it exists
        TrieNode node = fetch(key);
        if (node == null)
            return null;
        // key exists, return the value
        return node.getValue();
    }

    /**
     * Ensures no values are associated with the given key.
     *
     * @return <tt>true</tt> if any values were actually removed
     * @modifies this
     */
    public boolean remove(String key) {
        // early conversion of search key
        key = canonicalCase(key);
        // search the node associated with key, if it exists
        TrieNode node = fetch(key);
        if (node == null)
            return false;
        // key exists and can be removed.
        // TODO: prune unneeded nodes to save space
        boolean ret = node.getValue() != null;
        node.setValue(null);
        return ret;
    }

    /**
     * Returns an iterator (of Object) of the values mapped by keys in this that
     * start with the given prefix, in any order. That is, the returned iterator
     * contains exactly the values v for which there exists a key k so that
     * k.startsWith(prefix) and get(k) == v. The remove() operation on the
     * iterator is unimplemented.
     *
     * @requires this not modified while iterator in use
     */
    public Iterator getPrefixedBy(String prefix) {
        // Early conversion of search key
        prefix = canonicalCase(prefix);
        // Note that canonicalization MAY have changed the prefix length!
        return getPrefixedBy(prefix, 0, prefix.length());
    }

    /**
     * Same as getPrefixedBy(prefix.substring(startOffset, stopOffset). This is
     * useful as an optimization in certain applications to avoid allocations.
     * <p>
     *
     * Important: canonicalization of prefix substring is NOT performed here!
     * But it can be performed early on the whole buffer using the public method
     * <tt>canonicalCase(String)</tt> of this.
     *
     * @requires 0 &lt;= startOffset &lt;= stopOffset &lt;= prefix.length
     * @see #canonicalCase(String)
     */
    public Iterator getPrefixedBy(String prefix, int startOffset, int stopOffset) {
        // Find the first node for which "prefix" prefixes KEY(node). (See the
        // implementation overview for a definition of KEY(node).) This code is
        // similar to fetch(prefix), except that if prefix extends into the
        // middle of an edge label, that edge's child is considered a match.
        TrieNode node = root;
        for (int i = startOffset; i < stopOffset;) {
            // Find the edge whose label starts with prefix[i].
            TrieEdge edge = node.get(prefix.charAt(i));
            if (edge == null) {
                return EMPTY_ITERATOR;
            }
            // Now check that rest of label matches
            node = edge.getChild();
            String label = edge.getLabel();
            int j = match(prefix, i, stopOffset, label);
            if (i + j == stopOffset) {
                // a) prefix overlaps perfectly with just part of edge label
                break;
            } else if (j >= 0) {
                // b) prefix and label differ at some point
                node = null;
                break;
            } else {
                // c) prefix overlaps perfectly with all of edge label.
            }
            i += label.length();
        }
        // Yield all children of node, including node itself.
        if (node == null)
            return EMPTY_ITERATOR;
        else
            return new ValueIterator(node);
    }

    //KAY_ADD_2011_1021 //아직은 사용할 때가 아닌 함수 같다.=>Private로 해두자
    /**
     * 사전에 있는 단어들과 prefix의 startOffset부터 일치하는 모든 단어들을 찾아 resultList에 저장한다.
     * ex) getMatches("서울 특별시 강남구 대치동에 산다", 0, 18, resultList);
     *     => {"서울특별시", "서울 특별시 강남구", "서울 특별시 강남구 대치동"} 이 출력된다. 
     * @param prefix
     * @param startOffset
     * @param stopOffset
     * @param resultList
     * @return
     */
    private ArrayList getMatches(String prefix, int startOffset, int stopOffset, ArrayList resultList) 
    {
    	TrieNode node = root;
        for (int i = startOffset; i < stopOffset;) 
        {
            // Find the edge whose label starts with prefix[i].
            TrieEdge edge = node.get(prefix.charAt(i));
            if (edge == null) return resultList;

            // Now check that rest of label matches
            node = edge.getChild();
            String label = edge.getLabel();
            int j = match(prefix, i, stopOffset, label);
            if (i + j == stopOffset) {
                // a) prefix overlaps perfectly with just part of edge label =>prefix가 label보다 짧다.
                break;
            } else if (j >= 0) {
                // b) prefix and label differ at some point =>prefix와 label이 일치하지 않는다.
                node = null;
                break;
            } else {
                // c) prefix overlaps perfectly with all of edge label. =>prefix가 label보다 같거나 길다. 
            	Object o=node.getValue();
            	if ( o!=null ) resultList.add(o);
            }
            i += label.length();
        }
        
        return resultList;
    }
    
    private ArrayList getMatches(String prefix, int startOffset, int stopOffset) 
    {
    	ArrayList resultList=new ArrayList();
    	return getMatches(prefix, startOffset, stopOffset, resultList);
    }
    
    private ArrayList getMatches(String prefix) 
    {
    	ArrayList resultList=new ArrayList();
    	return getMatches(prefix, 0, prefix.length(), resultList);
    }    
    //KAY_ADD_END
    
    //KAY_ADD_2011_1028 원문에서 사전을 기반으로 태깅을 하기 위해 추가한 함수들
    /**
     * 두 문자열이 같은지를  공백을 무시하고 비교한다.
     * @param a	공백이 포함된 문자열
     * @param startOffset
     * @param stopOffset
     * @param b	공백이 포함안된 문자열(사전에 들어있는 단어이므로 이미 공백이 제거된 단어로 처리한다.
     * @return  -1은   b문자열(사전에 있는 단어)가 더 길거나 일부문이 일치하지 하지 않는다.
     *          0이상 값은 a문자열에서 매칭된 위치를 나타내며, a문자열이 b문자열과 같거나 더 길다
     */
    private final int matchEx(String a, int startOffset, int stopOffset, boolean bIgnoreSpaceOfPrefix, String b) 
    {
        char chA;
        int i, j, bLength=b.length();
        if ( bIgnoreSpaceOfPrefix==true )
        {
	        for (i = startOffset, j = 0; j < bLength; i++, j++) 
	        {
	            if (i >= stopOffset) return -1;		//b문자열이 더 길다.
	           	while( (chA=a.charAt(i))==' ') { i++; if ( i>=stopOffset ) return -1;};
	           	if (chA != b.charAt(j)) return -1;
	        }
        }
        else
        {
	        for (i = startOffset, j = 0; j < bLength; i++, j++) 
	        {
	            if (i >= stopOffset) return -1;		//b문자열이 더 길다.
	           	if (a.charAt(i) != b.charAt(j)) return -1;
	        }
        }
        return i;
    }
    
    /**
     * 입력문자열(prefix)이 사전에 들어 있는 단어로 시작되면, 매칭되는 단어를 찾아 준다
     * @param prefix 단어를 찾을 입력 문자열
     * @param startOffset 입력 문자열에서 시작 위치
     * @param stopOffset 입력 문자열에서 중지 위치
     * @param bIgnoreSpaceOfPrefix 입력 문자열에 대해 공백을 무시하고 단어 찾기
     * @param bLongestMatch 가장 길게 있치하는 단어 찾기
     * @param minMatchLength 일치하는 단어(사전에서 찾은)의 가장 최소 길이
     * @return
     */
    public MatchTerm getPrefixMatches(String prefix, 
    								   int startOffset, int stopOffset, 
    								   boolean bIgnoreSpaceOfPrefix, boolean bLongestMatch, 
    								   int minMatchLength) 
    {
    	Object oValue;
    	
    	TrieNode node = root;
    	MatchTerm matchValue=null;	//매칭된 결과
    	int matchLength=0;
    	
    	//if ( startOffset >=3169 )
    	//	System.out.println(prefix.substring(startOffset, startOffset+100));
    	
        for (int current = startOffset; current < stopOffset;) 
        {
        	// Find the edge whose label starts with prefix[i].
        	TrieEdge edge = node.get(prefix.charAt(current));
            if (edge == null) return matchValue;

            // Now check that rest of label matches
            node = edge.getChild();
            String label = edge.getLabel();

            //검색된 단어와 입력 문자열이 일치하는지 비교한다.
            if ( (current=matchEx(prefix, current, stopOffset, bIgnoreSpaceOfPrefix, label))<=0 ) break;
            matchLength+=label.length();

            if ( ((oValue=node.getValue())!=null) && (matchLength >= minMatchLength) )
         	{	//주의 탐색중에는  oValue가 null인 경우 존재하므로 반듯이 확인할것
            	if ( bLongestMatch==false ) return new MatchTerm(startOffset, current, oValue);
	            //가장 긴것을 찾기 위해 저장하여 두고 탐색을 계속한다. 
	         	if ( matchValue==null ) matchValue=new MatchTerm(startOffset, current, oValue);
	     		else matchValue.set(startOffset, current, oValue);
         	}
        	
            if ( bIgnoreSpaceOfPrefix==true ) 
        		while ((current<stopOffset) && (prefix.charAt(current)==' ') ) current++;
        }
        
        return matchValue;
    }

    public class MatchTerm
    {
    	public int startOffset, stopOffset;
    	public Object value;
    	MatchTerm(int startOffset, int stopOffset, Object value)
    	{
    		set(startOffset,stopOffset, value);
    	}
    	
    	void set(int startOffset, int stopOffset, Object value)
    	{
    		this.startOffset=startOffset;
    		this.stopOffset=stopOffset;
    		this.value=value;
    	}
    }
    //KAY_ADD_END
    
    /**
     * Returns all the (non-null) values associated with a given node and its
     * children. (internal)
     */
    private class ValueIterator extends NodeIterator {
        ValueIterator(TrieNode start) {
            super(start, false);
        }

        // inherits javadoc comment
        public Object next() {
            return ((TrieNode) super.next()).getValue();
        }
    }

    /**
     * Yields nothing. (internal)
     */
    private static class EmptyIterator implements Iterator {
        // inherits javadoc comment
        public boolean hasNext() {
            return false;
        }

        // inherits javadoc comment
        public Object next() {
            throw new NoSuchElementException();
        }

        public void remove() {

        }
    }

    private abstract class UnmodifiableIterator implements Iterator {
    	public final void remove() {

    	}
    }



    public class NodeIterator extends UnmodifiableIterator {
        /**
         * Stack for DFS. Push and pop from back. The last element of stack is
         * the next node who's value will be returned.
         * <p>
         *
         * INVARIANT: Top of stack contains the next node with not null value to
         * pop. All other elements in stack are iterators.
         */
        private ArrayList /* of Iterator of TrieNode */stack = new ArrayList();
        private boolean withNulls;

        /**
         * Creates a new iterator that yields all the nodes of start and its
         * children that have values (ignoring internal nodes).
         */
        public NodeIterator(TrieNode start, boolean withNulls) {
            this.withNulls = withNulls;
            if (withNulls || start.getValue() != null)
                // node has a value, push it for next
                stack.add(start);
            else
                // scan node children to find the next node
                advance(start);
        }

        // inherits javadoc comment
        public boolean hasNext() {
            return !stack.isEmpty();
        }

        // inherits javadoc comment
        public Object next() {
            int size;
            if ((size = stack.size()) == 0)
                throw new NoSuchElementException();
            TrieNode node = (TrieNode) stack.remove(size - 1);
            advance(node);
            return node;
        }

        /**
         * Scan the tree (top-down) starting at the already visited node until
         * finding an appropriate node with not null value for next(). Keep
         * unvisited nodes in a stack of siblings iterators. Return either an
         * empty stack, or a stack whose top will be the next node returned by
         * next().
         */
        private void advance(TrieNode node) {
            Iterator children = node.childrenForward();
            while (true) { // scan siblings and their children
                int size;
                if (children.hasNext()) {
                    node = (TrieNode) children.next();
                    if (children.hasNext()) // save siblings
                        stack.add(children);
                    // check current node and scan its sibling if necessary
                    if (withNulls || node.getValue() == null)
                        children = node.childrenForward(); // loop from there
                    else { // node qualifies for next()
                        stack.add(node);
                        return; // next node exists
                    }
                } else if ((size = stack.size()) == 0)
                    return; // no next node
                else
                    // no more siblings, return to parent
                    children = (Iterator) stack.remove(size - 1);
            }
        }
    }

    /**
     * Returns a string representation of the tree state of this, i.e., the
     * concrete state. (The version of toString commented out below returns a
     * representation of the abstract state of this.
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("<root>"); //$NON-NLS-1$
        toStringHelper(root, buf, 1);
        return buf.toString();
    }

    /**
     * Prints a description of the substree starting with start to buf. The
     * printing starts with the given indent level. (internal)
     */
    private void toStringHelper(TrieNode start, StringBuffer buf, int indent) {
        // Print value of node.
        if (start.getValue() != null) {
            buf.append(" -> "); //$NON-NLS-1$
            buf.append(start.getValue().toString());
        }
        buf.append("\n"); //$NON-NLS-1$
        // For each child...
        for (Iterator iter = start.labelsForward(); iter.hasNext();) {
            // Indent child appropriately.
            for (int i = 0; i < indent; i++)
                buf.append(" "); //$NON-NLS-1$
            // Print edge.
            String label = (String) iter.next();
            buf.append(label);
            // Recurse to print value.
            TrieNode child = start.get(label.charAt(0)).getChild();
            toStringHelper(child, buf, indent + 1);
        }
    }



    /**
     * A node of the Trie. Each Trie has a list of children, labelled by strings.
     * Each of these [String label, TrieNode child] pairs is considered an "edge".
     * The first character of each label must be distinct. When managing children,
     * different implementations may trade space for time. Each node also stores an
     * arbitrary Object value.
     * <p>
     *
     * Design note: this is a "dumb" class. It is <i>only</i> responsible for
     * managing its value and its children. None of its operations are recursive;
     * that is Trie's job. Nor does it deal with case.
     */
    final class TrieNode {
        /**
         * The value of this node.
         */
        private Object value = null;

        /**
         * The list of children. Children are stored as a sorted Vector because it
         * is a more compact than a tree or linked lists. Insertions and deletions
         * are more expensive, but they are rare compared to searching.
         * <p>
         *
         * INVARIANT: children are sorted by distinct first characters of edges,
         * i.e., for all i &lt; j,<br>
         * children[i].edge.charAt(0) &lt; children[j].edge.charAt(0)
         */
        private ArrayList /* of TrieEdge */children = new ArrayList(0);

        /**
         * Creates a trie with no children and no value.
         */
        public TrieNode() {
        }

        /**
         * Creates a trie with no children and the given value.
         */
        public TrieNode(Object value) {
            this.value = value;
        }

        /**
         * Gets the value associated with this node, or null if none.
         */
        public Object getValue() {
            return value;
        }

        /**
         * Sets the value associated with this node.
         */
        public void setValue(Object value) {
            this.value = value;
        }

        /**
         * Get the nth child edge of this node.
         *
         * @requires 0 &lt;= i &lt; children.size()
         */
        private final TrieEdge get(int i) {
            return (TrieEdge) children.get(i);
        }

        /**
         * (internal) If exact, returns the unique i so that:
         * children[i].getLabelStart() == c<br>
         * If !exact, returns the largest i so that: children[i].getLabelStart()
         * &lt;= c<br>
         * In either case, returns -1 if no such i exists.
         * <p>
         *
         * This method uses binary search and runs in O(log N) time, where N =
         * children.size().<br>
         * The standard Java binary search methods could not be used because they
         * only return exact matches. Also, they require allocating a dummy Trie.
         *
         * Example1: Search non exact c == '_' in {[0] => 'a...', [1] => 'c...'};
         * start loop with low = 0, high = 1; middle = 0, cmiddle == 'a', c <
         * cmiddle, high = 0 (low == 0); middle = 0, cmiddle == 'a', c < cmiddle,
         * high = -1 (low == 0); end loop; return high == -1 (no match, insert at
         * 0). Example2: Search non exact c == 'a' in {[0] => 'a', [1] => 'c'} start
         * loop with low = 0, high = 1; middle = 0, cmiddle == 'a', c == cmiddle,
         * abort loop by returning middle == 0 (exact match). Example3: Search non
         * exact c == 'b' in {[0] => 'a...', [1] => 'c...'}; start loop with low =
         * 0, high = 1; middle = 0, cmiddle == 'a', cmiddle < c, low = 1 (high ==
         * 1); middle = 1, cmiddle == 'c', c < cmiddle, high = 0 (low == 1); end
         * loop; return high == 0 (no match, insert at 1). Example4: Search non
         * exact c == 'c' in {[0] => 'a...', [1] => 'c...'}; start loop with low =
         * 0, high = 1; middle = 0, cmiddle == 'a', cmiddle < c, low = 1 (high ==
         * 1); middle = 1, cmiddle == 'c', c == cmiddle, abort loop by returning
         * middle == 1 (exact match). Example5: Search non exact c == 'd' in {[0] =>
         * 'a...', [1] => 'c...'}; start loop with low = 0, high = 1; middle = 0,
         * cmiddle == 'a', cmiddle < c, low = 1 (high == 1); middle = 1, cmiddle ==
         * 'c', cmiddle < c, low = 2 (high == 1); end loop; return high == 1 (no
         * match, insert at 2).
         */
        private final int search(char c, boolean exact) {
            // This code is stolen from IntSet.search.
            int low = 0;
            int high = children.size() - 1;
            while (low <= high) {
                int middle = (low + high) / 2;
                char cmiddle = get(middle).getLabelStart();
                if (cmiddle < c)
                    low = middle + 1;
                else if (c < cmiddle)
                    high = middle - 1;
                else
                    // c == cmiddle
                    return middle; // Return exact match.
            }
            if (exact)
                return -1; // Return no match.
            return high; // Return closest *lower or equal* match. (This works!)
        }

        /**
         * Returns the edge (at most one) whose label starts with the given
         * character, or null if no such edge.
         */
        public TrieEdge get(char labelStart) {
            int i = search(labelStart, true);
            if (i < 0)
                return null;
            TrieEdge ret = get(i);
            return ret;
        }

        /**
         * Inserts an edge with the given label to the given child to this. Keeps
         * all edges binary sorted by their label start.
         *
         * @requires label not empty.
         * @requires for all edges E in this, label.getLabel[0] != E not already
         *           mapped to a node.
         * @modifies this
         */
        public void put(String label, TrieNode child) {
            int i;
            // If there's a match it is the closest lower or equal one, and
            // precondition requires it to be lower, so we add the edge *after*
            // it. If there's no match, there are two cases: the Trie is empty,
            // or the closest match returned is the last edge in the list.
            if ((i = search(label.charAt(0), // find closest match
                    false)) >= 0) {
            }
            children.add(i + 1, new TrieEdge(label, child));
        }

        /**
         * Removes the edge (at most one) whose label starts with the given
         * character. Returns true if any edges where actually removed.
         */
        public boolean remove(char labelStart) {
            int i;
            if ((i = search(labelStart, true)) < 0)
                return false;
            children.remove(i);
            return true;
        }

        /**
         * Ensures that this's children take a minimal amount of storage. This
         * should be called after numerous calls to add().
         *
         * @modifies this
         */
        public void trim() {
            children.trimToSize();
        }

        /**
         * Returns the children of this in forward order, as an iterator of
         * TrieNode.
         */
        public Iterator childrenForward() {
            return new ChildrenForwardIterator();
        }



        /**
         * Maps (lambda(edge) edge.getChild) on children.iterator().
         */
       private class ChildrenForwardIterator extends UnmodifiableIterator {
            int i = 0;

            public boolean hasNext() {
                return i < children.size();
            }

            public Object next() {
                if (i < children.size())
                    return get(i++).getChild();
                throw new NoSuchElementException();
            }
        }

        /**
         * Returns the children of this in forward order, as an iterator of
         * TrieNode.
         */
        /*
         * public Iterator childrenBackward() { return new
         * ChildrenBackwardIterator(); }
         */

        /**
         * Maps (lambda(edge) edge.getChild) on children.iteratorBackward().
         */
        /*
         * private class ChildrenBackwardIterator extends UnmodifiableIterator { int
         * i = children.size() - 1;
         *
         * public boolean hasNext() { return i >= 0; }
         *
         * public Object next() { if (i >= 0) return get(i--).getChild(); throw new
         * NoSuchElementException(); } }
         */

        /**
         * Returns the labels of the children of this in forward order, as an
         * iterator of Strings.
         */
        public Iterator labelsForward() {
            return new LabelForwardIterator();
        }

        /**
         * Maps (lambda(edge) edge.getLabel) on children.iterator()
         */
        private class LabelForwardIterator extends UnmodifiableIterator {
            int i = 0;

            public boolean hasNext() {
                return i < children.size();
            }

            public Object next() {
                if (i < children.size())
                    return get(i++).getLabel();
                throw new NoSuchElementException();
            }
        }

        /**
         * Returns the labels of the children of this in backward order, as an
         * iterator of Strings.
         */
        /*
         * public Iterator labelsBackward() { return new LabelBackwardIterator(); }
         */

        /**
         * Maps (lambda(edge) edge.getLabel) on children.iteratorBackward()
         */
        /*
         * private class LabelBackwardIterator extends UnmodifiableIterator { int i =
         * children.size() - 1;
         *
         * public boolean hasNext() { return i >= 0; }
         *
         * public Object next() { if (i >= 0) return get(i--).getLabel(); throw new
         * NoSuchElementException(); } }
         */

        // inherits javadoc comment.
        public String toString() {
            Object val = getValue();
            if (val != null)
                return val.toString();
            return "NULL"; //$NON-NLS-1$
        }

        /**
         * Unit test.
         *
         * @see TrieNodeTest
         */
    }

    /**
     * A labelled edge, i.e., a String label and a TrieNode endpoint.
     */
    final class TrieEdge {
        private String label;
        private TrieNode child;

        /**
         * @requires label.size() > 0
         * @requires child != null
         */
        TrieEdge(String label, TrieNode child) {
            this.label = label;
            this.child = child;
        }

        public String getLabel() {
            return label;
        }

        /**
         * Returns the first character of the label, i.e., getLabel().charAt(0).
         */
        public char getLabelStart() {
            // You could store this char as an optimization if needed.
            return label.charAt(0);
        }

        public TrieNode getChild() {
            return child;
        }

    }
}
