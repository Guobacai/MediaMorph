/*
 * Problem1: 
 * 
 * 1. Big O Analysis: 
 * Assume the master list has n records
 * Assume the average length of the movie title is m. In this case, I only assume that the title only contains the special character :. All the others are alphabets.
 * This assumption is decided based on the list of 2014 HollyWood movies.
 * 
 * Building the dictionary : 
 * 
 *   Building the dictionary requires to parse the master list for once. Thus it is O(n)
 *   For each title, it requires O(m) to do the parsing and put it to the dictionary.  
 *   Since the dictionary is using the HashMap and the index is just put at the end of a list.Thus, all of this operation requires O(1).
 *   
 *   Therefore, the time complexity of building the dictionary is O(m * n). Since m is relatively small, we can say it is close to O(n)
 *   
 * Executing the query
 * Assume it returns k words. For each word, it is in t title.
 * 
 *   Since the dictionary is HashMap, getting the result list only requires O(1). 
 * 	 However, comparing the result and getting the only relevant result requires O(t2 * k). Considering the return result is relatively small.
 * 	 This computation is not likely putting too much burden on performance. According to the different situation, this algorithm can be tuned.
 * 
 * ********************************************************************
 * 2. Other Performance Consideration and Trade-off
 * 
 *    1) Millions of transaction per day.
 *       
 *       I made the object MoiveDictionary as Singleton. We can make it start up with the application. The reason is as following:
 *       ~1. Since the MoiveDictionary provide the same service to the client, it is not necessary to generate the object for each transaction.
 *       	 This can help to release the burden of JVM(Garbage Collection).
 *       ~2. Since the MovieDictionary.dict is immutable(The client can't change the dict), it is saved to be used in the multithread environment. The program doesn't need to do more synchronization which is also improve the performance.
 *    2) Space Complexity
 *    
 *    	 I use the HashMap to store the Dictionary. Of course this will need more space, but I treat the performance as more importantly. Also, the memory is getting bigger and cheaper.
 *    	 If the memory is relative small, I would use the ArrayList or even the Array. It can save the space, but the query will enlarge to O(n) for each query.
 *    
 *    3) Future Improvement
 *    
 *    	 In the future, we can cache the hot query and result. By doing this, the performance can be improved.
 * *********************************************************************
 * 3. Ambiguities:
 *    
 *    1) All the words are stored as the lower case in the Dictionary.
 *    2) All the search key words are parsed as the lower case.
 *    3) By modify the collection of stem words, the application can reduce much more Ambiguities. 
 * *********************************************************************
 * 4. Arrays:
 *    
 *    I user Arrays.sort in the program. It uses the quick sort which gives O(nlogn) time complexity. 
 *    Also, the Arrays.binarySearch uses the binary search which gives O(logn).
 *    Consider the stem words has limited size, these two operations won't impose too much burden on performance.
 * */

package Problem1;

import Problem1.MovieDictionary;
import java.util.Iterator;
import java.util.List;

public class MovieMatcher {

	public static void main(String[] args) {
		// The masterList stores all the movie (title)
		final String[] masterList = {"The Rings Rings Rings", "The Lord of the Rings", "Rings", "The Matrix", "Transformers: Age of Extinction", "Transformers"};
		
		final boolean buildStatus;
		final String query = "lord";
		
		MovieDictionary dictInstance = MovieDictionary.getInstance();
		
		buildStatus = dictInstance.buildDict(masterList);
		
		if(buildStatus){

			 List<Integer> result = dictInstance.getMatch(query);
			 
			 System.out.println(result);
		}
		else{
			System.out.println("The dictionary has failed to be built");
		}
	}
	
}
