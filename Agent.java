package org.cloudbus.cloudsim.power;

import java.util.List;

import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;

//THE AIM HERE IS TO UTILIZE THE ALGORITHM TO COME SELECT A VM TO MIGRATE 
//WE CAN USE SARSA OR Q-LEARNING
//THIS CLASS WILL THEREFORE ALSO STORE THE MATRIX THAT WILL STORE THE Q VALUES 

public class Agent {

	private Algorithm algorithm; 
	private List<PowerVm> MigratableVmList;
	private Environment env;
	
	
	public Agent(Algorithm algorithm, Environment env) {
		this.algorithm = algorithm;
		this.setEnv(env);
		//this.getEnv().initQValues();
	}
	
	public Algorithm getAlgorithm() {
		return this.algorithm;
	}

	public Algorithm setAlgorithm(Algorithm alg) {
		return this.algorithm = alg;
	}
	
	public Environment getEnv() {
		return env;
	}

	public void setEnv(Environment env) {
		this.env = env;
	}
	
	public PowerVm getAction(PowerHost host, List<PowerVm> MigratableVmList) {
		System.out.println("here");
		//THIS IS GOING TO BE CALLED EACH TIME THAT A HOST IS OVERRUN 
		
		//THIS IS JUST A PLACEHOLDER TO ALLOW THE FUNCTION TO WORK 
		PowerVm VmToMigrate = null;
		
		
		//SO HERE WE HAVE A LIST OF ALL THE POSSIBLE VM'S WHICH ARE ESSENTIALLY THE POSSIBLE ACTIONS AVAILABLE 
		//SO I THINK NOW WE NEED TO 
		
		if (algorithm.getAlgorithm().contentEquals("SARSA")) {
			
			//WE WILL SOMEHOW CALL THE SARSA ALGORITHM AND USE THE SARSA FUNCTIONALITY IN THE ALGORITHM CLASS
			VmToMigrate = algorithm.SARSA(host, MigratableVmList);
		}
		else if(algorithm.getAlgorithm().contentEquals("Q-Learning")) {
			
			//WE WILL SOMEHOW CALL THE Q-Learning ALGORITHM AND USE THE Q-Learning FUNCTIONALITY IN THE ALGORITHM CLASS
			//Log.printLine(MigratableVmList);
			VmToMigrate = algorithm.QLearning(host, MigratableVmList);
		}
		
		return VmToMigrate;
	}

	public void updateQValues(PowerHost host, List<PowerVm> MigratableVmList) {
		//WHAT DO WE NEED TO DO HERE?
		//WE NEED TO 
		
		//Log.printLine("DO WE GET HERE");
		
		if (algorithm.getAlgorithm().contentEquals("SARSA")) {
			algorithm.SARSAUpdateQValues(host, MigratableVmList);
		}
		
		else if(algorithm.getAlgorithm().contentEquals("Q-Learning")) {
			algorithm.QLearningUpdateQValues(host, MigratableVmList);

		}
		
		
	}
	

	

	
}


//public String getAlgorithm()