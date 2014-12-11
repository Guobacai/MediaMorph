/*
 * Problem 2
 * 
 * ConcurrentHashMap:
 * 
 * ConcurrentHashMap uses the lock stripping to improve the availability. Therefore, one thread only needs to lock part of the HashMap
 * Since the JobQueue uses ConcurrentHashMap, there is no need to do more synchronization.
 * 
 * The putIfAbsent is autonomic operation which can prevent other thread to update the HashMap while the current thread is judging the existence of the key.
 * 
 * Note: I didn't write the main function. The thread only needs to initilize the JobQueue object and call function executeJobForUser
 *
 * */

package Problem2;

import java.util.concurrent.ConcurrentHashMap;

public class JobQueue {
	private static JobQueue queue = null;
	private ConcurrentHashMap<Integer, String> jobQueue = null;
	
	private JobQueue(){
		jobQueue = new ConcurrentHashMap<Integer, String>();
	}
	
	//Singleton
	public static final JobQueue getInstance(){
		if(queue == null){
			synchronized(JobQueue.class){
				if(queue == null){
					queue = new JobQueue();
				}
			}
		}
		
		return queue;
	}
	
	public String executeJobForUser(String userName, Integer jobId){
		//if the task has been run by some one, return the user name immediately
		if(jobQueue.containsKey(jobId)){
			return jobQueue.get(jobId);
		}
		
		//No user is running this task
		//
		//In order to avoid concurrent modification, set this operation as autonomic
		//After this operation finishes, other threads can see this key in the HashMap. If other threads wants to execute the same job, it will return the user name
		jobQueue.putIfAbsent(jobId, userName);
		
		//The method will be blocked until it finishes. During this period, since the jobId still exists in the jobQueue, other threads should return the user name
		Integer result = executeJob(jobId);
		
		//When the job finishes, remove the jobId from jobQueue immediately. Thus, other threads which wants to run the same task can run it.
		jobQueue.remove(jobId);
		
		//return the result to corresponding thread.
		return result.toString();
	}
	
	// Assume the executeJob do some computation
	private int executeJob(Integer id){
		int i = 10000;
		System.out.print("Runing.");
		while(i >= 0){
			System.out.print(".");
			i--;
		}
		return id;
	}
}
