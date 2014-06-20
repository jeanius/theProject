package com.jeanpower.reggieproject;

public class End implements Instruction{

	private Game caller;
	private int identity;
	
	public End(Game g){
		 
		caller = g;
	}
	
	
	@Override
	public void doWork() {
	
		caller.setCurrPos(null);
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
	public void setRegister() {
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
