package com.jeanpower.reggieproject;

import android.util.Log;

public class Box implements Instruction{
	
	private int register;
	private Game caller;
	private Instruction succ;
	private Instruction pred;
	private boolean inc;
	private int identity;
	private ConnectLine line;
	private boolean decrementDone;
	
	
	public Box(Game g){
		 
		inc = true;
		register = 0;
		caller = g;
		succ = null;
		pred = null;
	}

	@Override
	public void doWork() {
		
		if (inc)
		{
			caller.incrementReg(register);
			caller.setCurrPos(succ);
		}
		
		else 
		{
			decrementDone = caller.decrementReg(register);
			caller.setCurrPos(succ);
		}
	}

	public boolean decDone(){
		return decrementDone;
	}
	
	@Override
	public void setSucc(Instruction successor) {	
		succ = successor;
	}
	
	public boolean getType(){
		return inc;
	}
	
	public void setConnect(ConnectLine cl){
		line = cl;
	}
	
	public ConnectLine getConnect(){
			return line;
	}

	public void setType(){
		
		if (inc)
		{
			inc = false;
		}
		
		else 
		{
			inc = true;
		}
	}

	@Override
	public Instruction getSucc() {
			return succ;
	}

	@Override
	public void setPred(Instruction predecessor) {
		pred = predecessor;
	}

	@Override
	public Instruction getPred() {
			return pred;
	}

	@Override
	public void setRegister() {
		
		register++;

		if (register>=caller.getMaxReg()){
			
			register = 0;
		}
	}
	

	public void changeRegister(int reg) {
		
		register = reg;
	}

	@Override
	public int getRegister() {
		return register;
	}

	@Override
	public void setId(int ID) {
		identity = ID;
	}
	
	@Override
	public int getId() {
		return identity;
	}
}