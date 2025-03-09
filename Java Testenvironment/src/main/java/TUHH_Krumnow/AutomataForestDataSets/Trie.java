package TUHH_Krumnow.AutomataForestDataSets;

import java.util.List;

public class Trie {

	private final int MAX  = 256; //max chars

    public TrieNode root;
    
    public Trie() {
    	root = new TrieNode();
    }
 
	public class TrieNode {
		
		public int freq;  // To store frequency
		private TrieNode[] child = new TrieNode[MAX]; //possible childs of trie
		
		public TrieNode() {
			freq = 1;
        	for (int i = 0; i < MAX; i++) {
        		child[i] = null;
        	}
		}
	}
	
	public void insert(String input) {
		TrieNode crawl = root;
		
		for(int i = 0; i < input.length(); i++) {
			int index = input.charAt(i);
			
			// Create a new child if not exist already
            if (crawl.child[index] == null) {
            	TrieNode node = new TrieNode();
                crawl.child[index] = node;
                crawl = node;
            }
            else {
               crawl.child[index].freq++; //incr. freq. if child exists
               crawl = crawl.child[index]; //move to child
            }
            
            
		}
	}
	
	public int getUniqueMarking(String input) {
		if (root == null) {
	           return -1;
		}
		
		TrieNode finder = root;
		
		for(int i = 0; i < input.length(); i++) {
			int index = input.charAt(i); //look up index
			
			if(finder.child[index] == null) { //if index does not exist throw -1, i.e., word was not added previously
				return -1;
			}
			
			finder = finder.child[index]; //crawl to index
			
			
			if(finder.freq == 1) {
				return i;
			}
			
		}
		
		return -1; //if word is element of another word (no unique suffix)
	}
}
