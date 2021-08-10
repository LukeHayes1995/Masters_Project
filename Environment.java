package org.cloudbus.cloudsim.power;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.math3.util.Pair;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.CloudSim;

public class Environment {
	
	private Host host;
	private Hashtable<Pair,Double> qValues = new Hashtable<Pair,Double>();
	private List<Integer> uniqueIDS = new ArrayList<Integer>();
	private Hashtable<Integer,Integer> movedVmList = new Hashtable<Integer,Integer>();
	private Hashtable<Pair,Pair> stateValues = new Hashtable<Pair,Pair>();
	private Hashtable<Integer,Integer> oldHost = new Hashtable<Integer,Integer>();
	
	public Environment() {

	}
	
	public void setOldHost(int vmID, int hostID) {
		oldHost.put(vmID, hostID);
	}
	
	public int getOldHost(int vmID) {
		return oldHost.get(vmID);
	}
	
	//STATE = NUMBER OF ACTIVE HOSTS/TOTAL HOSTS
	public int getStateRachel(PowerHost host) {
				
		List<PowerHost> hostlist = new ArrayList<PowerHost>();
		hostlist = host.getDatacenter().getHostList();
		//Log.printLine("Host list size");
		//Log.printLine(hostlist.size());
		
		double activehosts = 0.0;
		double totalhosts = 0.0;
				
		for (PowerHost h : hostlist) {
			
			//Log.printLine(h.getVmList().size());
			if(h.getVmList().size() >= 1) {
				totalhosts = totalhosts + 1.0;
				activehosts = activehosts + 1.0;
			}
			else {
				totalhosts = totalhosts + 1.0;
			}
			
		}
		
		//Log.printLine("Up Next");
		//Log.printLine(totalhosts);
		//Log.printLine(activehosts);
		//Log.printLine(activehosts/totalhosts);
		//Log.printLine(activehosts/totalhosts * 100.00);

		
		double state_dbl = activehosts/totalhosts * 100.00;
		//Log.printLine(state_dbl);
		int state = (int) state_dbl;

		//Log.printLine("STATE INCOMING");
		//Log.printLine(state);
		return state;
	}
	
	
	public int getStateLuke(PowerHost host) {
		int totalMIPS = host.getTotalMips();
		
		List<PowerVm> hostVmList = host.getVmList();
		double inUseMIPS = 0.0;
		
		for(PowerVm vm: hostVmList) {
			inUseMIPS += vm.getTotalUtilizationOfCpuMips(CloudSim.clock()) / host.getTotalMips();
			Log.printLine(vm.getTotalUtilizationOfCpuMips(CloudSim.clock()) / host.getTotalMips());
		}
		
		double stateDbl = inUseMIPS * 100;
		int state = (int) stateDbl;
		
		Log.printLine("MIPS Incoming");
		Log.printLine(inUseMIPS);
		Log.printLine(totalMIPS);
		
		return state;
	}

	
	public void setMovedVm(int hostID, int VmID) {
		movedVmList.put(hostID, VmID);

	}
	
	public int getMovedVm(int hostID) {
		//Log.printLine(movedVmList);
		//Log.printLine(hostID);
		return movedVmList.get(hostID);

	}
	
	public int lookupCurrentStateValue(int hostID, int VmID) {
		
		Pair<Integer, Integer> lookup = new Pair<Integer, Integer>(hostID, VmID);
		Pair value = stateValues.get(lookup);
		
		//Log.printLine(value.getFirst());
		double currentState = (double) value.getFirst();
		int currentStateNext = (int) currentState;
		
		//int currentState = Integer.parseInt(value.getFirst().toString());

		
		return currentStateNext;	
	}
	
	public double lookupNextStateValue(int hostID, int VmID) {
		
		Pair<Integer, Integer> lookup = new Pair<Integer, Integer>(hostID, VmID);
		Pair value = stateValues.get(lookup);
		
		double nextState = (double) value.getSecond();
		
		return nextState;	
	}
	
	public void reset() {
		stateValues.clear();
		movedVmList.clear();
	}
	
	//WHEN WE CALL THIS ONE IT WILL NOT BE THERE ALREADY SO WE NEED TO CREATE INSTEAD OF REPLACE
	public void setCurrentStateValue(int hostID, int VmID, double newValue) {

		Pair<Integer, Integer> lookup = new Pair<Integer, Integer>(hostID, VmID);
		Pair<Double, Double> state = new Pair<Double, Double>(newValue, 0.00);
				
		stateValues.put(lookup, state);
			
	}
	
	//IT WILL ALREADY BE CREATED AS WE WILL ADD A CURRENT STATE TO IT 
	//SO WE NEED TO LOOK UP THE CURRENT STATE 
	public void setNextStateValue(int hostID, int VmID, double newValue) {
		
		Pair<Integer, Integer> lookup = new Pair<Integer, Integer>(hostID, VmID);
		Pair value = stateValues.get(lookup);
		Pair<Double, Double> states = new Pair<Double, Double>((Double)value.getFirst(), newValue);
		
		stateValues.replace(lookup, states);
			
	}
	
	public void wipetateValueTable() {

		stateValues.clear();
			
	}
	

	//STATE = sum of utilization of all migratable vms/ total host utilization 
	public int getStateKieran(PowerHost host, List<PowerVm> MigratableVmList) {
		
		List<PowerVm> hostVmList = host.getVmList();
		double migratableVmUtilization = 0.0;
		double totalVmUtilization = 0.0;
		
		for(PowerVm vm: hostVmList) {
			totalVmUtilization += vm.getTotalUtilizationOfCpuMips(CloudSim.clock()) / host.getTotalMips();
			
		}
		
		for(PowerVm vm: MigratableVmList) {
			migratableVmUtilization += vm.getTotalUtilizationOfCpuMips(CloudSim.clock()) / host.getTotalMips();
			
		}
		
		/*
		PowerHostUtilizationHistory _host = (PowerHostUtilizationHistory) host;

		Double VmUtilizationTotal = new Double(0.0);
		Double VmUtilizationTotal2 = new Double(0.0);
		
		System.out.println(MigratableVmList.size());
		System.out.println(host.getId());
		
		//We need to make sure each vm is on the host 
		Log.printLine(host.getId());
		List<PowerVm> hostVmList = host.getVmList();
		
		Log.printLine("Host VM list incoming :");
		Log.printLine(host.getId());
		for(PowerVm pvm: hostVmList) {
			Log.printLine(pvm.getId());
		}
		
		//NEED TO GET A LIST OF MIGRATABLE VMS 
		
		Log.printLine("The following VMs should match the previous list");
		for(PowerVm Vm : MigratableVmList) {
			Log.printLine(Vm.getId());
			Double VmUtilization = Vm.getUtilizationHistory().get(0);
			
			//Double VmUtilization2 = Vm.getUtilizationHistory().get(30);

			VmUtilizationTotal+=VmUtilization;
			//VmUtilizationTotal2+=VmUtilization2;

		}
		
		Double HostUtilization = host.getUtilizationOfCpu();

		Log.printLine("Utilization Statistics");
		Log.printLine(VmUtilizationTotal);
		Log.printLine(VmUtilizationTotal2);
		Log.printLine(HostUtilization);
		Log.printLine(host.getMaxUtilization());
		double[] d = _host.getUtilizationHistory();
		
		int state = (int) ((VmUtilizationTotal/HostUtilization) * 100);
		Log.printLine(state);
		
		Log.printLine("Host utilization loop incoming");
		for(double ds: d) {
			Log.printLine(ds);
		}
		*/

		int state = (int) ((migratableVmUtilization/totalVmUtilization) * 100);
		//Log.printLine("STATE INCOMING");
		//Log.printLine(state);
		
		return state;
	}
	
	public List<Integer> initQValues(Host host) {
		
		Log.printLine("Initiilizing Q values");
		//THE Q VALUES ACTUALLY SHOULD BE THE FOLLOWING:
		//STATE 1 ACTION 1 
		//WITH STATE 1 BEING 0% AND ACTION 1 BEING MOVE THE FIRST VM 
		
		
		//The states are the problem 
		//int numStates = 1000;
		int numStates = 150;

		//int numActions = host.getDatacenter().getVmList().size();
		int numActions = 101;
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
		
		/*
		for(int i=0; i<numStates; i++){
			
			//for(int j=0; j<numActions; j++){
				
			for(Integer vmID: uniqueIDS) {
			//WE SHOULD INSTEAD HERE LOOP THROUGH THE ARRAY OF ACTUAL IDS AND JUST ADD THOSE 
				
				
				Pair<Integer, Integer> entry = new Pair<Integer, Integer>(i, vmID);

				this.qValues.put(entry, 0.00);		
				
			}
			
		}
		*/
		
		
		for(int i=0; i<numStates; i++){
			
			for(int j=0; j<numActions; j++){
				
			//for(Integer vmID: uniqueIDS) {
			//WE SHOULD INSTEAD HERE LOOP THROUGH THE ARRAY OF ACTUAL IDS AND JUST ADD THOSE 
				
				
				Pair<Integer, Integer> entry = new Pair<Integer, Integer>(i, j);
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
	
	//REWARD FOR HOW MUCH POWER IS REDUCED BY THE MIGRATION
	//THIS WILL ENCOURAGE MOVES THAT RESULT IN LOWER POWER
	public double getRewardPower(double oldPower, double newPower) {
		
		double reward = (newPower - oldPower) * 20;
		
		//Log.printLine("Reward incoming");
		//Log.printLine(reward);
		return reward;
	}
	
	//COULD DO ONE HHERE WHERE WE ASSIGN THE REWARD BASED ON HOW BIG THE VM TRANSPORTED IS
	public double getRewardNew(double oldHostUtilization, double oldVmUtilisation) {
			
		double reward = oldHostUtilization/oldVmUtilisation;

		//Log.printLine("Reward incoming");
		//Log.printLine(reward);
		return reward;
			
	}
	
	//FOR THE REWARD I WANT TO REWARD MIGRATIONS TO BUSY HOSTS MORE AS THIS WILL REDUCE STARTING UP NEW HOSTS 
	//UTILIZATION OF THE VM WE JUST MOVED/ UTILIZATION OF THE NEW HOST
	public double getReward(PowerHost host, PowerVm powerVm) {
		//HOW ARE WE GOING TO CHOOSE THE REWARD?
		//WE COULD GET IT BY DOING THE MIGRATON AND THEN CHECKING THE ENERGY AND UTILIZING THE DIFFERENCE 
		//TO DO SO WE WOULD HAVE TO ALLOW THE MIGRATION
		//
		
		List<PowerVm> hostVmList = host.getVmList();
		double totalUtilization = 0.0;
		double movedVmUtilization = 0.0;
		
		for(PowerVm vm: hostVmList) {
			
			if(powerVm.getId()==vm.getId()) {
				movedVmUtilization = vm.getTotalUtilizationOfCpuMips(CloudSim.clock()) / host.getTotalMips();
				totalUtilization += vm.getTotalUtilizationOfCpuMips(CloudSim.clock()) / host.getTotalMips();

			}
			else {
				totalUtilization += vm.getTotalUtilizationOfCpuMips(CloudSim.clock()) / host.getTotalMips();
			}
			
		}
		
		/*
		//(p.getTotalUtilizationOfCpuMips(CloudSim.clock()) / host.getTotalMips();

		
		Log.printLine("Starting reward section");
		Log.printLine(host.getId());
		Log.printLine(powerVm.getId());
		
		Log.printLine("About to print Vms on host");
		List<PowerVm> vms = host.getVmList();
		for(PowerVm vm: vms) {
			Log.printLine(vm.getId());
			
			Log.printLine("Printing Vm utilization");
			List<Double> d = vm.getUtilizationHistory();
			for(Double v: d) {
				Log.printLine(d);
			}
			
			
			
		
		}
		Log.printLine("In reward section");
		double h = host.getUtilizationOfCpu();
		List<Double> d = powerVm.getUtilizationHistory();
		Log.printLine(host.getMaxUtilization());
		
		Log.printLine(h);
		
		for(double util: d) {
			Log.printLine(util);
		}
		
		int hostRam = host.getRam();
		int VmRam = powerVm.getRam();
		*/
		//Log.printLine("Reward Incoming");
		//Log.printLine(movedVmUtilization);
		//Log.printLine(totalUtilization);
	
		double reward = totalUtilization/movedVmUtilization;

		//double reward = movedVmUtilization/totalUtilization;
		//Log.printLine("Reward Incoming");
		//Log.printLine(reward);
	
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
