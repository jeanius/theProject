package com.jeanpower.reggieproject;

public class Arrow implements Instruction{
	
	private Instruction toIns;
	private Instruction pred;
	private Instruction succ;
	private Game caller;
	private int register;
	private int identity;

	
	public Arrow(Game g){
		 
		caller = g;
		succ = null;
		pred = null;
	}
	
	
	@Override
	public void dowork() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setSucc(Instruction successor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Instruction getSucc() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setPred(Instruction predecessor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Instruction getPred() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setRegister(int register) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getRegister() {
		// TODO Auto-generated method stub
		return 0;
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
