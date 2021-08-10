package org.cloudbus.cloudsim.power;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import org.apache.commons.math3.util.Pair;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;

//THE ALGORITHM CLASS WILL HAVE TWO METHODS AND BOTH OF THESE WILL BE Q-LEARNING AND SARSA

public class Algorithm {
	
	private String algorithm;
	private Environment env;
	private Double reward;
	private int currentState;
	private int nextState;
	private PowerVm lastVmMoved;
	private int lastAction;
	private boolean migrateMoreThanOne;
	List<Integer> stateList = new ArrayList<Integer>();
	List<Integer> actionList = new ArrayList<Integer>();
	List<PowerHost> oldHostList = new ArrayList<PowerHost>();
	List<Double> oldPowerList = new ArrayList<Double>();
	List<Double> newPowerList = new ArrayList<Double>();
	List<Double> oldHostUtilizationList = new ArrayList<Double>();
	List<Double> oldVmUtilizationList = new ArrayList<Double>();
	private int migrationCounter = 0;
	private int testCounter = 1;
	public boolean testVar = false;
	
	HashMap<PowerVm,Integer> VmActionPair = new HashMap<PowerVm,Integer>();
	HashMap<PowerVm,Integer> VmStatePair = new HashMap<PowerVm,Integer>();
	HashMap<PowerVm,Double> VmHostUtilizationPair = new HashMap<PowerVm,Double>();
	HashMap<PowerVm,Double> VmVmUtilizationPair = new HashMap<PowerVm,Double>();
	HashMap<Pair,Integer> stateActionCounter = new HashMap<Pair,Integer>();



	public Algorithm(String algorithm, Environment env) {
		this.setAlgorithm(algorithm);
		this.setEnv(env); 
		initStateActionCounter();
	}
	
	public void initStateActionCounter() {
		
		int numStates = 150;
		int numActions = 100;
		
		for(int i=0; i<numStates; i++){
			
			for(int j=0; j<numActions; j++){
			
				
				Pair<Integer, Integer> entry = new Pair<Integer, Integer>(i, j);
	
				this.stateActionCounter.put(entry, 0);		
				
			}
		}
	}
	
	public void incrementStateActionCounter(Integer state, Integer action) {
		
		Pair<Integer,Integer> key = new Pair<Integer,Integer>(state, action);
		int value = stateActionCounter.get(key);
		int newValue = value + 1;
		
		stateActionCounter.replace(key, value, newValue);
		
	}

	public String getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}
	
	public boolean getMultipleVmToMigrate() {
		return migrateMoreThanOne;
	}

	public void setMultipleVmToMigrate(boolean migrateMoreThanOne) {
		this.migrateMoreThanOne = migrateMoreThanOne;
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
		
		Log.printLine("State before migration");
		//int currentState = this.getEnv().getStateKieran(host, MigratableVmList);
		int currentState = this.getEnv().getStateRachel(host);

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
			Log.printLine(currentQTable.contains(pair));
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



		//NOW WE HAVE TO
		//1 UPDATE THE STATE 
		//2 CALCULATE THE NEW Q VALUE 
		//3 UPDATE THE Q TABLE WITH NEW VALUE 
		//4 
		
		//TO FO THIS WE NEED TO BE ABLE TO OBSERVE THE HOST UTILIZATION AFTER WE HAVE MOVED 
		//I THINK THIS MEANS WE SHOULD DO THIS AT THE START 
		
		//lastVmMoved = VmToMigrate;
		setLastVmMoved(VmToMigrate);
		this.getEnv().setMovedVm(host.getId(), VmToMigrate.getId());
		this.getEnv().setCurrentStateValue(host.getId(), VmToMigrate.getId(), currentState);
		this.getEnv().setOldHost(VmToMigrate.getId(), hostID);

		return VmToMigrate;		
		
		
		
	}
	
	//SO HERE WE ARE GOING TO IMPLEMENT THE NEW VERSION OF THE Q LEARNING 
	public PowerVm QLearningNew(PowerHost host, List<PowerVm> MigratableVmList, boolean migrateMoreThanOne) {
		
		Log.printLine("Entering first Q learning");
		migrationCounter = migrationCounter + 1;
		
		
		double currentTime = CloudSim.clock();

		if(currentTime > 86300) {
			Log.printLine("Here is the number of over-utilized mgrations");
			Log.printLine(migrationCounter);
			
			Log.printLine("Here are the Q values: ");
			Hashtable<Pair,Double> qvls = this.env.getqValues();
			
	        Enumeration<Pair> e = qvls.keys();
	        
	        while (e.hasMoreElements()) {
	        	Pair key = e.nextElement();
				Log.printLine(key);
				Log.printLine(qvls.get(key));

	        }

		}
		
		if(migrateMoreThanOne==false) {
			//WE NEED TO EMPTY BOTH LISTS
			
			Log.printLine("Are we emptying these lists?");
			stateList.clear();
			actionList.clear();
			oldHostList.clear();
			oldPowerList.clear();
			newPowerList.clear();
			oldHostUtilizationList.clear();
			oldVmUtilizationList.clear();
			//Log.printLine("Start of Test Number::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
			//Log.printLine(testCounter);
			testCounter = testCounter + 1;


		}
		
		//ONLT INCLUDE FOR TESTING 
		//if(testCounter > 100) {
		    //System.exit(0);	
		//}
		
		
		//NOW WE WANT TO ADD THESE TWO
		
		
		double totalEnergy = 0.0;
		List<PowerHost> hostList = host.getDatacenter().getHostList();
		
		for(PowerHost h: hostList) {
			totalEnergy = totalEnergy + h.getPower();
		}
		
		oldPowerList.add(totalEnergy);
		oldHostList.add(host);
		
		setMultipleVmToMigrate(migrateMoreThanOne);
		
		
		PowerVm VmToMigrate = null;
		
		//Log.printLine("Migratable VM list");
		
		//for(PowerVm vm: MigratableVmList) {
			//Log.printLine(vm.getId());
		//}

		
		//SO WE NEED TO LOOP THROUGH ALL THE MIGRATABLE VMS
		//FOR EACH ONE WE NEED TO GET THE ACTION VALUE (VM UTILIZATION/ HOST UTILIZATION)
		//THEN FOR EACH WE LOOKUP THE CORRESPONDING Q VALUE FOR THAT ACTION VALUE 
		//THEN WE CHOOSE THE HIGHEST Q VALUE AS THE VM TO MOVE 
		
		List<PowerVm> hostVmList = host.getVmList();

		double migratableVmUtilization = 0.0;
		double totalHostUtilization = 0.0;
		Hashtable<Integer,Integer> actionStates = new Hashtable<Integer,Integer>();
		List<Integer> actions = new ArrayList<Integer>();
		
		//Log.printLine("These are the VM's on the host");
		for(PowerVm vm: hostVmList) {
			totalHostUtilization += vm.getTotalUtilizationOfCpuMips(CloudSim.clock()) / host.getTotalMips();
			//Log.printLine(vm.getId());
		}
				
		
		Log.printLine("Actions available to us");
		for(PowerVm vm: MigratableVmList) {
			migratableVmUtilization = vm.getTotalUtilizationOfCpuMips(CloudSim.clock()) / host.getTotalMips();
			double actionValue = (migratableVmUtilization/totalHostUtilization) * 100.00;
	        int actionValueInt = (int) Math.round(actionValue);

			actionStates.put(vm.getId(), actionValueInt);
			actions.add(actionValueInt);
			//Log.printLine(actionValueInt);
			
		}
		
		//NOW WE NEED TO GET THE STATE AND THEN LOOKUP THE Q VALUE FOR EACH OF THESE NEW ACTIONS 
		Hashtable<Pair,Double> currentQTable = this.getEnv().getqValues();
		//int currentState = this.getEnv().getStateLuke(host);
		int currentState = this.getEnv().getStateRachel(host);
		//int currentState = this.getEnv().getStateKieran(host, MigratableVmList);
		


		setCurrentState(currentState);
		List<Double> qValues = new ArrayList<Double>();
		List<Integer> stateActionPairState = new ArrayList<Integer>();
		List<Integer> stateActionPairAction = new ArrayList<Integer>();
		List<Integer> vmIDs = new ArrayList<Integer>();

		Hashtable<Pair, Double> QValueList = new Hashtable<Pair, Double>(); 

		
		//LETS LOOP OVER ALL THE ACTIONS WE HAVE AVAILABLE TO US
		
		//Log.printLine("Q values for each action incoming");
		int index = 0;
		
		for (Integer d: actions) {
			
			
			Pair<Integer, Integer> pair = new Pair<Integer, Integer>(currentState, d);
			stateActionPairState.add(currentState);
			stateActionPairAction.add(d);
			vmIDs.add(MigratableVmList.get(index).getId());
			
			//NOW LOOKUP THE Q VALUE FOR EACH AND APPEND IT TO A LIST 
			double qValue = currentQTable.get(pair);
			//Log.printLine(qValue);
			qValues.add(qValue);
			QValueList.put(pair, qValue);
			index = index + 1;

		}

		//NOW WE JUST SELECT THE HIGHEST Q VALUE AND THEN TAKE THE VM AS THE HIGHEST Q VALUE AND MIGRATE THAT
		
		
		//NOW WE HAVE A LIST OF ALL THE VM IDS AND TS CORRESPONDING Q VALUES 
		//NOW WE NEED TO CHOOSE THE MAXIMUM AND THE LOOP BACK OVER THEM ALL AND ADD THEM IF THEY ARE EQUAL TO THE MAXIMUM
		
		List<Pair> maxRewardKeys = new ArrayList<Pair>();
		List<Integer> matchingKeyState = new ArrayList<Integer>();
		List<Integer> matchingKeyAction = new ArrayList<Integer>();
		List<Integer> VmIdThatMatch = new ArrayList<Integer>();
		int actionValueToReturn = 0;

		
        Double maxQVal = (Collections.max(QValueList.values())); 
        
        
		//Log.printLine("Current State Incoming:");
		//Log.printLine(currentState);
        //Log.printLine("Max Q value incoming");
        //Log.printLine(maxQVal);
        
        
        for(int i=0; i<stateActionPairState.size(); i++) {
        	//Log.printLine(qValues.get(i));
        	//Log.printLine(maxQVal);
        	if(qValues.get(i).equals(maxQVal)) {
        		//Log.printLine("Matches");
        		VmIdThatMatch.add(vmIDs.get(i));
        		matchingKeyState.add(stateActionPairState.get(i));
        		matchingKeyAction.add(stateActionPairAction.get(i));

        	}
        }
        
        //NOW WE CAN JUST RANDOMLY SELECT ONE 
        
        
        /*
        
        Set<Entry<Pair, Double>> entrySet = QValueList.entrySet();

        int listSize = 0; 
        for (Entry<Pair, Double> entry : entrySet) { 
        	
        	Log.printLine(entry.getValue());
        	Log.printLine(entry.getKey());

            if (entry.getValue() == maxQVal) {
            	
                //ADD THE ENTRY TO THE LIST 
            	//Log.printLine("Essential");
            	//Log.printLine(entry.getKey().getFirst());
            	//Log.printLine(entry.getKey().getSecond());
            	Log.printLine("Other values actually match the maimum Q value");
                maxRewardKeys.add(entry.getKey());
                listSize+=1;
            }
            
        }
        */

        //Log.printLine("IDS");
        //for(int i: VmIdThatMatch) {
        	//Log.printLine(i);
        //}

        
        //NOW WE HAVE A LIST RIGHT AND FIRSTLY WE SHOULD CHECK IF THAT LIST IS BIGGER THAN 1 IN SIZE
        
        //IF THERE ARE MORE THAN ONE VMS WITH THE SAME Q VALUE WE WILL RANDOMLY SELECT ONE
        int actionValueToMigrate;
        
        if(VmIdThatMatch.size() > 1) {

        	Log.printLine("We select a random Q value as many are the same");
            Random rand = new Random();
            actionValueToMigrate = (int) VmIdThatMatch.get(rand.nextInt(VmIdThatMatch.size()));
            
            for(int i=0; i<matchingKeyAction.size(); i++) {
            	if(VmIdThatMatch.get(i).equals(actionValueToMigrate)) {
            		actionValueToReturn = matchingKeyAction.get(i);
            	}
            }
            
        
        }
        
        //OTHERWISE WE SET THE VM WE ARE RETURNING TO MIGRATE AS THE ONLY ONE IN THE LIST 
        else {
        	
        	//Log.printLine("Select the only q value in the list");
        	actionValueToMigrate = (int) VmIdThatMatch.get(0);
        	actionValueToReturn = matchingKeyAction.get(0);
	
        }
        
        //Log.printLine(actionValueToReturn);

        
        /*
        //WE NEED TO LOOKUP THE VM WITH THAT ACTION VALUE 
        int VmIdToMigrate = 0;
        Set<Entry<Integer, Integer>> entrySt = actionStates.entrySet();

        for (Entry<Integer, Integer> entry : entrySt) { 
        	
        	//Log.printLine("This part is absolutely kep:::::::::");
        	//Log.printLine(actionValueToMigrate);
        	//Log.printLine(entry.getValue());
        	//Log.printLine(entry.getKey());

            if (entry.getValue() == actionValueToMigrate) {
            	
            	VmIdToMigrate = entry.getKey();
            	Log.printLine("The following is the VM ID of the VM we are migrating - Should match that of the maximum q value");
            	Log.printLine(VmIdToMigrate);
            }
            
        }
        */
		
        //NOW THAT WE HAVE THE ID OF THE VM WE ARE GOING TO MIGRATE WE NEED TO LOOP THROUGH THE VM LIST AND TAKE THE VM OBJECT THAT MACTHES THE ID 
		List<PowerVm> vmList = new ArrayList<PowerVm>();
		vmList = host.getDatacenter().getVmList();
		
		//PowerVm VmToMigrate;
		
		for(PowerVm Vm : vmList) {
			if(Vm.getId()==actionValueToMigrate) {
				//Log.printLine("Valid VM ID");
				VmToMigrate = Vm;
			}
		}
        
        //Log.printLine(VmToMigrate);

		//List<PowerVm> hostVmListNew = new ArrayList<PowerVm>();
		//hostVmListNew = host.getVmList();
		
		//for(PowerVm vm: hostVmListNew) {
			
			//if(VmToMigrate.getId() == vm.getId()) {
				//Log.printLine("We are returning a valid VM that is on the host");
			//}
		//}

		//NOW WE HAVE TO
		//1 UPDATE THE STATE 
		//2 CALCULATE THE NEW Q VALUE 
		//3 UPDATE THE Q TABLE WITH NEW VALUE 
		//4 
		
		//TO FO THIS WE NEED TO BE ABLE TO OBSERVE THE HOST UTILIZATION AFTER WE HAVE MOVED 
		//I THINK THIS MEANS WE SHOULD DO THIS AT THE START 
		
		//lastVmMoved = VmToMigrate;
		setLastVmMoved(VmToMigrate);
		this.getEnv().setMovedVm(host.getId(), VmToMigrate.getId());
		this.getEnv().setCurrentStateValue(host.getId(), VmToMigrate.getId(), currentState);
		this.getEnv().setOldHost(VmToMigrate.getId(), host.getId());
		
		//LETS SET THE ACTION AND THE STATE 
		
		actionList.add(actionValueToReturn);
		stateList.add(currentState);
		setLastAction(actionValueToMigrate);
		
		if(migrateMoreThanOne==true) {
			
			//ADD THE ACTION, CURRENT STATE AND 
		}
		
		//Log.printLine("We should be returning");
		//Log.printLine(VmToMigrate.getId());
		
		oldVmUtilizationList.add(VmToMigrate.getTotalUtilizationOfCpuMips(CloudSim.clock()) / host.getTotalMips());
		
		
		
		
		//Log.printLine("VM ID incoming");
		//Log.printLine(VmToMigrate.getId());
		//Log.printLine();
		//Log.printLine();
		
		
		oldHostUtilizationList.add(totalHostUtilization);
	
		VmActionPair.put(VmToMigrate, actionValueToReturn);
		VmStatePair.put(VmToMigrate, currentState);
		VmHostUtilizationPair.put(VmToMigrate, totalHostUtilization); 
		VmVmUtilizationPair.put(VmToMigrate, VmToMigrate.getTotalUtilizationOfCpuMips(CloudSim.clock()) / host.getTotalMips());
		
		incrementStateActionCounter(currentState, actionValueToReturn);

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

	public void SARSAUpdateQValues(PowerHost host, List<PowerVm> migratableVmList, PowerVm vm) {
		//
	
	}
	
	public void QLearningUpdateQValues(PowerHost host, List<PowerVm> migratableVmList, PowerVm vm) {
		
		Log.printLine("Entering second Q learning");
		
		int previousAction = VmActionPair.get(vm);
		int previousState = VmStatePair.get(vm);
		double previousHostUtilization = VmHostUtilizationPair.get(vm);
		double previousVmUtilization = VmVmUtilizationPair.get(vm);
		
		//Log.printLine(previousAction);
		//Log.printLine(previousState);
		//Log.printLine(previousHostUtilization);
		//Log.printLine(previousVmUtilization);

		
		VmActionPair.remove(vm);
		VmStatePair.remove(vm);
		VmHostUtilizationPair.remove(vm);
		VmVmUtilizationPair.remove(vm);
		
		//NOW WE NEED TO LOOKUP ALL THE THINGS WE JUST ADDED AND THEN REMOVE THEM FROM THE LIST

		
		if(migratableVmList == null) {
			//Log.printLine("Should be in here to return - update method");
			return;
		}
		

		double totalEnergy = 0.0;
		List<PowerHost> hostList = host.getDatacenter().getHostList();
		
		for(PowerHost h: hostList) {
			totalEnergy = totalEnergy + h.getPower();
		}
		
		newPowerList.add(totalEnergy);
		
		//I THINK TO DO SO HERE WE HAVE TO BE IN THE NEXT STATE AND SELECT THE MAX Q VALUE FROM THE NEXT STATE 
		//SO WE NEED TO GET THE NEXT STATE RIGHT AND THEN LOOK AT ALL THE Q VALUES FOR THE VM'S WE COULD MOVE FROM THAT STATE 
		//WE MUST MAKE SURE THE VM'S WE LOOK AT THOUGH ARE STILL ON THE HOST 
		
		//LIST TO STORE THE Q VALUES FOR THE NEXT STATE
		List<Double> newStateQVals = new ArrayList<Double>();
		
		//NOW WE NEED TO LOOP OVER THESE AND THEN WE SELECT THE HIGHEST 
		
		//GET THE LIST OF VM's THAT ARE ON THE HOST - THESE ARE THE ONLY ACTIONS WE CAN TAKE
		List<Vm> hostVmList = host.getVmList();
		
		int oldHostIndex = oldHostList.size() - 1;
		
		//double reward = this.getEnv().getReward(host, vm);
		int indexOldPowerList = oldPowerList.size() - 1;
		int indexNewPowerList = newPowerList.size() - 1;
		int indexOldHostUtilizationList = oldHostUtilizationList.size() - 1;
		int indexOldVmUtilizationList = oldVmUtilizationList.size() - 1;


		//double reward = this.getEnv().getReward(host, vm);
		//double reward = this.getEnv().getRewardPower(oldPowerList.get(indexOldPowerList), newPowerList.get(indexNewPowerList));
		double reward = this.getEnv().getRewardNew(previousHostUtilization, previousVmUtilization);

		oldHostUtilizationList.remove(indexOldHostUtilizationList);
		oldVmUtilizationList.remove(indexOldVmUtilizationList);
		oldPowerList.remove(indexOldPowerList);
		newPowerList.remove(indexNewPowerList);

		
		//Log.printLine("Host and VM after migration coming up");
		//Log.printLine(host.getId());
		//Log.printLine(vm.getId());
		
		//int nextState = this.getEnv().getStateLuke(host);
		int nextState = this.getEnv().getStateRachel(host);
		//int nextState = this.getEnv().getStateKieran(host, migratableVmList);
		
		//Log.printLine("Next state incoming");
		//Log.printLine(nextState);

		setNextState(nextState);
		
		int index = stateList.size() - 1;
		int currentState = stateList.get(index);
		int action = actionList.get(index);
		
		actionList.remove(index);
		stateList.remove(index);
		
		
		//SO NOW WE HAVE THE CURRENT STATE, NEXT STATE AND THE REWARD OBSERVED
		//WE NEED TO LOOP THROUGH THE Q VALUE TABLE FOR THE NEXT STATE AND SELECT THE MAXIMUM Q VALUE 
		
		//NOW GET THE MAX Q VALUE FROM THE NEW STATE 
		
		List<Double> nextStateQvals = new ArrayList<Double>();

		
		//THIS SHOULD BE NEXT STATE AND ALL THE ACTONS NO?
		//SO WE SHOULD BE LOOPING THROUGH ALL THE POSSIBLE ACTIONS FOR THE NET STATE
		//WE NEED TO GET THE LIST OF VM IDS 
		
		List<Integer> uniqueIDS = env.getUniqueIDS();
		

		for(int i=0; i<100; i++) {
			//Log.printLine(i);
			nextStateQvals.add(env.lookupQValue(nextState, i));

		}
		
		
		//NOW WE NEED TO GET THE MAX
		
		double maxQValNextState = Collections.max(nextStateQvals);
		
		//Log.printLine("Next state maximum Q value incoming");
		//Log.printLine(maxQValNextState);
		
		//NOW WE HAVE ALL THE INFORMATION WE NEED EXCEPT ALPHA AND GAMMA AND THE OLD Q VALUE 
		//OLD Q VALUE IS THE OLD STATE AND THE ACTION WE TOOK 
		
		double alpha = 0.5;
		double gamma = 0.9;
		
		//LAST ACTION NEEDS TO BE CHANGED TOO 
		int lastAction = getLastVmMoved().getId();
		
		int oldHostofVm = this.getEnv().getOldHost(vm.getId());
	
		//int action = this.getEnv().getMovedVm(oldHostofVm);
		//int action = getLastAction();
		
		//WE NEED TO CHANGE THIS LAST ACTION TO BE THE ACTION AND NOT THE VM
		
		
		
		//WE NEED TO PUT SOMETHING IN HERE SO WE CAN GET THE VM WE MOVED 
		//WE NEED ACCESS TO THE OLD HOST HERE
		
		
		//currentState = this.getEnv().lookupCurrentStateValue(oldHostofVm, action);
		//currentState = getCurrentState();
		
		//Log.printLine(previousState);
	
		double oldQValue = env.lookupQValue(previousState, previousAction);
		//Log.printLine("Old Q value");
		//Log.printLine(oldQValue);

        double newQVal = oldQValue + alpha * (reward + gamma * (maxQValNextState) - oldQValue);
        
        //Log.print("New Q value");
        //Log.printLine(newQVal);
        	
        env.updateqValue(previousState, previousAction, newQVal);
	
        //System.exit(0);

		
	}
	
	public void reset() {
		this.getEnv().reset();
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

	public int getLastAction() {
		return lastAction;
	}

	public void setLastAction(int lastAction) {
		this.lastAction = lastAction;
	}


}


