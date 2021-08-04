package org.cloudbus.cloudsim.power;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.math3.util.Pair;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;

public class Environment {
	
	private Host host;
	private Hashtable<Pair,Double> qValues = new Hashtable<Pair,Double>();
	private List<Integer> uniqueIDS = new ArrayList<Integer>();

	public Environment() {

	}
	
	//STATE = NUMBER OF ACTIVE HOSTS/TOTAL HOSTS
	public int getStateRachel(PowerHost host) {
				
		List<PowerHost> hostlist = new ArrayList<PowerHost>();
		hostlist = host.getDatacenter().getHostList();
		
		int activehosts = 0;
		int totalhosts = 0;
				
		for (PowerHost h : hostlist) {
			
			if(h.getVmList().size() > 1) {
				totalhosts += 1;
			}
			else {
				activehosts += 1;
				totalhosts += 1;
			}
			
		}
		
		int state = (int) ((activehosts/totalhosts) * 100);
		
		return state;
	}
	

	//STATE = sum of utilization of all migratable vms/ total host utilization 
	public int getStateKieran(PowerHost host, List<PowerVm> MigratableVmList) {
		
		Double VmUtilizationTotal = new Double(0.0);
				
		System.out.println(MigratableVmList.size());
		System.out.println(host.getId());
		
		//NEED TO GET A LIST OF MIGRATABLE VMS 
		for(PowerVm Vm : MigratableVmList) {
			Double VmUtilization = Vm.getUtilizationHistory().get(0);
			VmUtilizationTotal+=VmUtilization;
		}
		
		Double HostUtilization = host.getMaxUtilization();

		int state = (int) ((VmUtilizationTotal/HostUtilization) * 100);
		
		return state;
	}
	
	public List<Integer> initQValues(Host host) {
		
		//Log.printLine("Initiilizing Q values");
		//THE Q VALUES ACTUALLY SHOULD BE THE FOLLOWING:
		//STATE 1 ACTION 1 
		//WITH STATE 1 BEING 0% AND ACTION 1 BEING MOVE THE FIRST VM 
		
		
		//The states are the problem 
		int numStates = 1000;
		int numActions = host.getDatacenter().getVmList().size();
		int uniqueActions = 0;
		List<Integer> uniqueIDS = new ArrayList<Integer>();
		
		List<PowerVm> lst = host.getDatacenter().getVmList();
		int maxID = 0;
		
		for(PowerVm v: lst) {
			if(v.getId() > maxID) {
				maxID = v.getId();
			}
			if(!uniqueIDS.contains(v.getId())) {
				uniqueIDS.add(v.getId());
				//Log.printLine(v.getId());
			}
		}
		
		int actualSize = uniqueIDS.size();
		
		//Log.printLine("The number of actions is coming next: ");
		//Log.printLine(numActions);
		//Log.printLine(maxID);
		//Log.printLine(actualSize);
		
		for(int i=0; i<numStates; i++){
			
			//for(int j=0; j<numActions; j++){
				
			for(Integer vmID: uniqueIDS) {
			//WE SHOULD INSTEAD HERE LOOP THROUGH THE ARRAY OF ACTUAL IDS AND JUST ADD THOSE 
				
				
				Pair<Integer, Integer> entry = new Pair<Integer, Integer>(i, vmID);
				//Log.printLine(vmID);

				this.qValues.put(entry, 0.00);		
				
			}
			
		}
		
		return uniqueIDS;
		
		
		
		/*
		//WE NEED TO LOOP THROUGH ALL THE HOSTS IN A CONSISTENT MANNER SUCH THAT THEIR IDS DONT CHANGE 
		//THEN WE NEED TO LOOP THROUGH ALL THE VMS IN THE WHOLE SYSTEM 
		
		// Q VLAUE 1 = HOST 1 ACTION 1 
		// Q VALUE 2 = HOST 1 ACTION 2 
		//ALL THE ACTIONS STAY THE SAME AS THEY REPRESENT THE ACTUAL VM ID AND NOT THE ID OF THE VM ON THE HOST 
		//WE NEED A LIST OF ALL HOSTS 
		//AND A LIST OF ALL VMS 
		
		List<PowerHost> hostList = new ArrayList<PowerHost>();
		List<PowerVm> vmList = new ArrayList<PowerVm>();
	
		host.getDatacenter().getHostList();
		host.getDatacenter().getVmList();
		
		
		//EACH HOST HAS A UNIQUE ID
		for (PowerHost h : hostList) {
						
			//EACH VM HAS A UNIQUE ID
			for (PowerVm v : vmList) {
				
				//FOR EACH RECORD HERE WE MUST CREATE A VALUE IN THE Q TABLE 
				//WE WILL USE A HASH TABLE IN THE FORM OF <String,Int>
				//STRING WILL LOOK LIKE: HOST1VM1
				
				Pair<Integer, Integer> entry = new Pair<Integer, Integer>(h.getId(), v.getId());

				qValues.put(entry, 0.00);
				
			}	
		}
		*/
		
 		
	}
	
	public double lookupQValue(int state, int action) {
		
		//Log.printLine(state);
		//Log.printLine(action);
		Pair<Integer, Integer> lookup = new Pair<Integer, Integer>(state, action);
		double value = qValues.get(lookup);
		
		return value;
			
	}
	
	public void setHost(Host host) {
		//setHost(host);
		List<Integer> uniqueIDList = initQValues(host);
		this.setUniqueIDS(uniqueIDList);
		
	}
	
	public double getReward(PowerHost host, PowerVm Vm) {
		//HOW ARE WE GOING TO CHOOSE THE REWARD?
		//WE COULD GET IT BY DOING THE MIGRATON AND THEN CHECKING THE ENERGY AND UTILIZING THE DIFFERENCE 
		//TO DO SO WE WOULD HAVE TO ALLOW THE MIGRATION
		//
		
		//Log.printLine(Vm);
		
		int hostRam = host.getRam();
		int VmRam = Vm.getRam();
		
		double reward = hostRam/VmRam;
		
		return reward;
		
	}

	public Hashtable<Pair,Double> getqValues() {
		return qValues;
	}

	public void setqValues(Hashtable<Pair,Double> qValues) {
		this.qValues = qValues;
	}
	
	//*********************************************************************
	//NEEDS FIXING
	public void updateqValue(int state, int action, double newValue) {

		Pair<Integer, Integer> pair = new Pair<Integer, Integer>(state, action);
		
		qValues.replace(pair, newValue);
		
				
	}

	public List<Integer> getUniqueIDS() {
		return uniqueIDS;
	}

	public void setUniqueIDS(List<Integer> uniqueIDS) {
		this.uniqueIDS = uniqueIDS;
	}
}


//HERE WE NEED TO PUT THE FOLLOWING
//1) SOMETHING TO GET THE REWARD (TO DO WITH THE POWER - OLD POWER)
//2) GET THE STATE
//3) GET ALL THE POTENTIAL ACTIONS (ALL THE POSSIBLE VM's)
//Will also store the Q values

//THE AIM OF THIS CLASS IS TO GIVE THE AGENT INFORMATION ON THE ENVIRONMENT 


//THERE ARE N STATES DEPENDING ON HOW MANY HOSTS THERE ARE 
//IN EACH STATE WE HAVE M ACTIONS - DEPENDING ON THE NUMBER OF VM'S THAT THE HOST HAS
//EACH HOST WILL HAVE ITS OWN NUMBER OF ACTIONS 
//BUT WE CAN ONLY TAKE AN ACTION BY SELECTING ONE OF THE MIGRATABLE HOSTS - WE CANNOT SELECT A HOST THAT ISN'T MIGRATABLE 

//SO EACH STATE N HAS M ACTIONS 
