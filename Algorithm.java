package org.cloudbus.cloudsim.power;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import org.apache.commons.math3.util.Pair;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;

//THE ALGORITHM CLASS WILL HAVE TWO METHODS AND BOTH OF THESE WILL BE Q-LEARNING AND SARSA

public class Algorithm {
	
	private String algorithm;
	private Environment env;
	private Double reward;
	private int currentState;
	private int nextState;
	private PowerVm lastVmMoved;
	
	public Algorithm(String algorithm, Environment env) {
		this.setAlgorithm(algorithm);
		this.setEnv(env); 
	}

	public String getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}
	
	public PowerVm SARSA(PowerHost host, List<PowerVm> MigratableVmList) {
		
		
		
		//JUST TO MAKE IT WORK
		PowerVm VmToMigrate = MigratableVmList.get(0);
		return VmToMigrate;
		
	}
	
	public PowerVm QLearning(PowerHost host, List<PowerVm> MigratableVmList) {
		//Log.printLine("Printing Migratable VMS");
		//Log.printLine(MigratableVmList);
		

		//THE STATE IS THE NUMBER OF ACTIVE HOSTS/TOTAL HOSTS - LESS HOSTS THE BETTER, THEREFORE WILL LEARN ACTIONS TO DO SO 
		
		//FIRST STATE
		
		
		//THEN WE NEED TO TAKE AN ACTION
		//HOW DO WE TAKE AN ACTION?
		//TAKE THE ACTION WITH THE HIGHEST Q VALUE 
		
		
		//NOW WE NEED TO GO GET THE REWARD
		//TO GET THE REWARD WE WILL ESSENTIALLY HAVE TO MOVE THE VM WHICH MEANS WE HAVE TO RETURN FROM THE METHOD
		//SO WE CAN EITHER:
		//1 DO IT ON THE NEXT CALL
		//2 TRY CALL AN UPDATE STEP AFTER THE VM HAS BEEN MIGRATED 
		//3 COME UP WITH A REWARD THAT ALLOWS US TO NOT NEED TO MOVE THE VM BEFORE WE CAN SEE IT 
		//4 
		
		//WILL ASK ENDA THE BEST WAY TO GO BUT FOR NOW I AM GOING TO JUST USE OE THAT MEANS WE DON'T HAVE TO MOVE THE VM (SOMEHTING TO DO WITH THE VM)
		
		//JUST TO MAKE IT WORK
		PowerVm VmToMigrate = null;
		
		//STEPS ARE AS FOLLOWS
		//1 WE NEED TO GET THE CURRENT STATE 
		//2 FROM THIS STATE WE NEED TO GET THE MIGRATABLE VMS 
		//3 LOOP THROUGH ALL THESE POSSIBLE VMS THAT WE CAN MIGRATE AND GET THE Q VALUES OF EACH - PUT THEM IN A LIST
		//4 SELECT THE ONE WITH THE HIGHEST Q VALUE 
		//5 OBSERVE THE REWARD
		//6 CALCULATE THE NEW Q VALUE
		//7 UPDATE THE Q VALUE TABLE 
		
		//1
		//THE CURRENT STATE IS THE NUMBER OF ACTIVE HOSTS/TOTAL HOSTS * 100
		//double curSt = this.getEnv().getStateRachel(host);
		int currentState = this.getEnv().getStateKieran(host, MigratableVmList);
		
		//Log.printLine(currentState);
		//Log.printLine(currentState);
		
		setCurrentState(currentState);
		
		//2
		//LETS CREATE A LIST OF ALL THE IDS OF THE MIGRATABLE VMS 
		List<Integer> MigratableVmIDList =  new ArrayList<Integer>();
		
		for (PowerVm v : MigratableVmList) {
			MigratableVmIDList.add(v.getId());
		}
		
		//NOW LETS GET THE CURRENT Q VALUE TABLE
		Hashtable<Pair,Double> currentQTable = this.getEnv().getqValues();
		//Log.printLine(currentQTable);
		int hostID = host.getId();
		
		Hashtable<Pair, Double> QValueList = new Hashtable<Pair, Double>(); 
		
		for(Integer i : MigratableVmIDList) {
			

			Pair<Integer, Integer> pair = new Pair<Integer, Integer>(hostID, i);
			
			//Log.printLine("Important info coming next:");
			//Log.printLine(hostID);
			//Log.printLine(i);
			//Log.printLine(currentQTable.contains(pair));
			QValueList.put(pair, currentQTable.get(pair));
			
		}
		
		//NOW WE HAVE A LIST OF ALL THE VM IDS AND TS CORRESPONDING Q VALUES 
		//NOW WE NEED TO CHOOSE THE MAXIMUM AND THE LOOP BACK OVER THEM ALL AND ADD THEM IF THEY ARE EQUAL TO THE MAXIMUM
		
		List<Pair> maxRewardKeys = new ArrayList<Pair>(); 
		
        Double maxQVal = (Collections.max(QValueList.values())); 
        
        Set<Entry<Pair, Double>> entrySet = QValueList.entrySet();

        int listSize = 0; 
        for (Entry<Pair, Double> entry : entrySet) { 
        	
            if (entry.getValue() == maxQVal) {
            	
                //ADD THE ENTRY TO THE LIST 
                maxRewardKeys.add(entry.getKey());
                listSize+=1;
            }
            
        }
        
        //NOW WE HAVE A LIST RIGHT AND FIRSTLY WE SHOULD CHECK IF THAT LIST IS BIGGER THAN 1 IN SIZE
        
        //IF THERE ARE MORE THAN ONE VMS WITH THE SAME Q VALUE WE WILL RANDOMLY SELECT ONE
        int VmIDToMigrate;
        
        if(listSize > 1) {

            Random rand = new Random();
            VmIDToMigrate = (int) maxRewardKeys.get(rand.nextInt(maxRewardKeys.size())).getValue();
            
        
        }
        
        //OTHERWISE WE SET THE VM WE ARE RETURNING TO MIGRATE AS THE ONLY ONE IN THE LIST 
        else {
            VmIDToMigrate = (int) maxRewardKeys.get(0).getValue();
	
        }
		
        //NOW THAT WE HAVE THE ID OF THE VM WE ARE GOING TO MIGRATE WE NEED TO LOOP THROUGH THE VM LIST AND TAKE THE VM OBJECT THAT MACTHES THE ID 
		List<PowerVm> vmList = new ArrayList<PowerVm>();
		vmList = host.getDatacenter().getVmList();
		
		//PowerVm VmToMigrate;
		
		for(PowerVm Vm : vmList) {
			if(Vm.getId()==VmIDToMigrate) {
				//Log.printLine("Should be in this very important loop");
				VmToMigrate = Vm;
			}
		}
        
        //Log.printLine(VmToMigrate);
		double rew = this.getEnv().getReward(host, VmToMigrate);
		setReward(rew);


		//NOW WE HAVE TO
		//1 UPDATE THE STATE 
		//2 CALCULATE THE NEW Q VALUE 
		//3 UPDATE THE Q TABLE WITH NEW VALUE 
		//4 
		
		//TO FO THIS WE NEED TO BE ABLE TO OBSERVE THE HOST UTILIZATION AFTER WE HAVE MOVED 
		//I THINK THIS MEANS WE SHOULD DO THIS AT THE START 
		
		//lastVmMoved = VmToMigrate;
		setLastVmMoved(VmToMigrate);
		
		return VmToMigrate;		
	}
	
	public void updateQValue(PowerHost host) {
		//I THINK TO DO SO HERE WE HAVE TO BE IN THE NEXT STATE AND SELECT THE MAX Q VALUE FROM THE NEXT STATE 
		//SO WE NEED TO GET THE NEXT STATE RIGHT AND THEN LOOK AT ALL THE Q VALUES FOR THE VM'S WE COULD MOVE FROM THAT STATE 
		//WE MUST MAKE SURE THE VM'S WE LOOK AT THOUGH ARE STILL ON THE HOST 
		
		//LIST TO STORE THE Q VALUES FOR THE NEXT STATE
		List<Double> newStateQVals = new ArrayList<Double>();
		
		//NOW WE NEED TO LOOP OVER THESE AND THEN WE SELECT THE HIGHEST 
		
		//GET THE LIST OF VM's THAT ARE ON THE HOST - THESE ARE THE ONLY ACTIONS WE CAN TAKE
		List<Vm> hostVmList = host.getVmList();
		
		
		
		
	}

	public void SARSAUpdateQValues(PowerHost host, List<PowerVm> migratableVmList) {
		//
		
	}
	
	public void QLearningUpdateQValues(PowerHost host, List<PowerVm> migratableVmList) {
		
		if(migratableVmList == null) {
			//Log.printLine("Should be in here to return - update method");
			return;
		}

		//I THINK TO DO SO HERE WE HAVE TO BE IN THE NEXT STATE AND SELECT THE MAX Q VALUE FROM THE NEXT STATE 
		//SO WE NEED TO GET THE NEXT STATE RIGHT AND THEN LOOK AT ALL THE Q VALUES FOR THE VM'S WE COULD MOVE FROM THAT STATE 
		//WE MUST MAKE SURE THE VM'S WE LOOK AT THOUGH ARE STILL ON THE HOST 
		
		//LIST TO STORE THE Q VALUES FOR THE NEXT STATE
		List<Double> newStateQVals = new ArrayList<Double>();
		
		//NOW WE NEED TO LOOP OVER THESE AND THEN WE SELECT THE HIGHEST 
		
		//GET THE LIST OF VM's THAT ARE ON THE HOST - THESE ARE THE ONLY ACTIONS WE CAN TAKE
		List<Vm> hostVmList = host.getVmList();
		
		int nextState = this.getEnv().getStateKieran(host, migratableVmList);

		setNextState(nextState);
		
		int currentState = getCurrentState();
		System.out.println();
		double rew = getReward();
		int numStates = 100;
		
		//Log.printLine("DO WE GET HERE ");
		
		//SO NOW WE HAVE THE CURRENT STATE, NEXT STATE AND THE REWARD OBSERVED
		//WE NEED TO LOOP THROUGH THE Q VALUE TABLE FOR THE NEXT STATE AND SELECT THE MAXIMUM Q VALUE 
		
		//NOW GET THE MAX Q VALUE FROM THE NEW STATE 
		
		List<Double> nextStateQvals = new ArrayList<Double>();

		
		//THIS SHOULD BE NEXT STATE AND ALL THE ACTONS NO?
		//SO WE SHOULD BE LOOPING THROUGH ALL THE POSSIBLE ACTIONS FOR THE NET STATE
		//WE NEED TO GET THE LIST OF VM IDS 
		
		List<Integer> uniqueIDS = env.getUniqueIDS();
		
		//Log.printLine(uniqueIDS);
		
		for(Integer i: uniqueIDS) {
			
			nextStateQvals.add(env.lookupQValue(nextState, i));
		}
		
		//NOW WE NEED TO GET THE MAX
		
		double maxQValNextState = Collections.max(nextStateQvals);
		
		//NOW WE HAVE ALL THE INFORMATION WE NEED EXCEPT ALPHA AND GAMMA AND THE OLD Q VALUE 
		//OLD Q VALUE IS THE OLD STATE AND THE ACTION WE TOOK 
		
		double alpha = 0.5;
		double gamma = 0.9;
		int lastAction = getLastVmMoved().getId();
		
		double oldQValue = env.lookupQValue(currentState, lastAction);
		

        double newQVal = oldQValue + alpha * (rew + gamma * (maxQValNextState) - oldQValue);
        
        env.updateqValue(currentState, lastAction, newQVal);
		
		
	}
	
	public Environment getEnv() {
		return env;
	}

	public void setEnv(Environment env) {
		this.env = env;
	}

	public Double getReward() {
		return reward;
	}

	public void setReward(Double reward) {
		this.reward = reward;
	}

	public int getCurrentState() {
		return currentState;
	}

	public void setCurrentState(int currentState) {
		this.currentState = currentState;
	}

	public int getNextState() {
		return nextState;
	}

	public void setNextState(int nextState) {
		this.nextState = nextState;
	}
	
	public PowerVm getLastVmMoved() {
		return lastVmMoved;
	}

	public void setLastVmMoved(PowerVm lastVmMoved) {
		this.lastVmMoved = lastVmMoved;
	}


}


