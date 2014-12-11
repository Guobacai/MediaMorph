package Problem1;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class MovieDictionary {
	private static MovieDictionary instance = null;
	private HashMap<String, List<Integer>> dict;
	private final String[] stem = {"The", "the", "A", "a", "of", "Of", ",", ":"};
	private MovieDictionary(){
	// The initial size of the hashmap is 16. If the masterlist has over 1 million records, it is better to give a proper initial size of the hashmap in order to avoid rehashing
		this.dict = new HashMap<String, List<Integer>>();
	}
	
	//Create singleton instance
	public static MovieDictionary getInstance(){
		//Do the double check in order to avoid wasting time on waiting for the lock
		if(instance == null){
			synchronized(MovieDictionary.class){
				if(instance == null){
					instance = new MovieDictionary();
				}
			}
		}
		
		return instance;
	}
	
	/*
	 * Function: Build the dictionary
	 * 
	 * @Params: master -> The master list of the movie
	 * @Params: stem -> The dictionary of the stem words
	 * */
	public boolean buildDict(final String[] master) {
		//Sorting the stem array for binary search
		Arrays.sort(stem);
		
		String[] mainTitle = null;  // The name of the series
		String[] sequelTitle = null;  // The name of the sequel
		String[] wholeTitle = null; // The entire name of the movie
		int itemIndex = 0;
		
		if(stem == null || master == null){
			return false;
		}
		
		for(String item : master){
			if(item.contains(":")){
				// The movie is sequel
				wholeTitle = item.split(":");
				if(wholeTitle != null){
					mainTitle = wholeTitle[0].split("\\s+");
					sequelTitle = wholeTitle[1].split("\\s+");

					for(String word : mainTitle){
						putToDict(dict, word.toLowerCase(), itemIndex);
					}
					
					for(String word : sequelTitle){
						putToDict(dict, word.toLowerCase(), itemIndex);
					}
				}
				/*
				 * Here should throw out an exception
				 * */
			}
			else{
				// The movie without sequel
				wholeTitle = item.split("\\s+");
				for(String word : wholeTitle){
					putToDict(dict, word.toLowerCase(), itemIndex);
				}				
			}
			
			itemIndex++;
		}
		
		return true;
	}
	
	/*
	 * Function: Put a non-stem word into the dictionary
	 * 
	 * Condition1 : If a non-stem word already exists, add it to the end of the list
	 * Condition2 : If a non-stem word doesn't exist, generate a new List and add the index to it
	 * 
	 * @Params: dictionary -> The building dictionary
	 * @Params: data -> The word that is under evaluated
	 * @Params: stem -> The dictionary of the stem words
	 * */
	private void putToDict(HashMap<String, List<Integer>> dictionary, String data, int itemSeq){
		List<Integer> values = null;
		int index = Arrays.binarySearch(stem, data);
		
		// If the word is stem word, just skip it.
		if(index >= 0){
			/*System.out.println(data + " is stem word");*/
			return;
		}
		
		// If the word is not the stem word, it should be counted and put it into the dictionary
		if(dictionary != null){
			/*System.out.println(data + " IS NOT stem word");*/
			if(dictionary.containsKey(data)){
				values = dictionary.get(data);
				//Extinct the duplicated index. Since the program only traverse the master list for once, therefore, it only needs to check the last element
				if(values.get(values.size() - 1) != itemSeq){
					values.add(itemSeq);
				}
			}
			else{
				//Use the Array List
				values = new ArrayList<Integer>();
				values.add(itemSeq);
				dictionary.put(data, values);
			}
		}
	}
	
	/*
	 * Function: Execute a query on dictionary. Return the index if the proper movie is found. Otherwise, return null
	 * 
	 * @Params query -> the query
	 * */
	
	public List<Integer> getMatch(String query){
		ArrayList<List<Integer>> resultSet = new ArrayList<List<Integer>>();

		List<Integer> results = new ArrayList<Integer>();
		List<String> keywords = parseUserInput(query);
		List<Integer> itemList;
		
		Iterator itr = keywords.iterator();
		
		//Get the query result and put it into the resultSet
		while(itr.hasNext()){
			String item = (String)itr.next();
			itemList = dict.get(item);
			//only add the existed result into the result set
			if(itemList != null){
				resultSet.add(itemList);
			}
			
		}
		
		//Compare the resultSet and get the final result.
		//The threshold of suitability: The result must contain all the key word in the query
		//The comparision: O(n3). Since the resultSet is likely to be small, therefore, O(n3) won't put too much burden on the performance
		
		// If the result set is null
		if(resultSet.size() == 0){
			return null;
		}
		//If the query has only one keywords, then return all the results
		if(resultSet.size() == 1){
			return resultSet.get(0);
		}
		
		for(Integer index : resultSet.get(0)){
			boolean match = false;
			for(int i = 1; i < resultSet.size(); i++){
				match = false;
				List<Integer> list = resultSet.get(i);
				Iterator itrIndex = list.iterator();
				while(itrIndex.hasNext()){
					Integer j = (Integer)itrIndex.next();
					if(j == index){
						match = true;
						break;
					}
				}
				if(!match){
					break;
				}
			}
			
			if(match){
				results.add(index);
			}
		}
		
		if(results.isEmpty()){
			return null;
		}
		
		return results;
	}
	
	/*
	 * Function: Parsing the user input and return the search key words
	 * 
	 * @Params query -> the query
	 * */
	public List<String> parseUserInput(String query){
		ArrayList<String> parseResult = new ArrayList<String>();
		int index;
		//Adding a space at the end of the string to deal the last word
		char[] queryWord = (new String(query + ' ')).toCharArray();
		
		StringBuffer word = new StringBuffer();
		
		for(char singleWord : queryWord){
			int ascii = (int) singleWord;
			if(ascii == 32){
				String st = word.toString();
				index = Arrays.binarySearch(stem, st);
				//if the word is stem word, just ignore it.
				if(index < 0){
					parseResult.add(st.toLowerCase());
				}
				//Clean up the StringBuffer
				word.delete(0, word.length());
			}
			//Only counting the alphabets(Lowercase & Uppercase)
			if((ascii >= 65 && ascii <= 90) || (ascii >= 97 && ascii <= 122)){
				word.append(singleWord);
			}
		}
		
		return parseResult;
	}
}
