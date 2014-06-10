package com.jeanpower.reggieproject;

public class Box implements Instruction{
	
	private int register;
	private Game caller;
	private Instruction succ;
	private Instruction pred;
	private boolean inc;
	
	public Box(Game g){
		 
		inc = true;
		register = 0;
		caller = g;

	}

	@Override
	public void dowork() {
		
		int currentData = caller.getRegData(register);
		
		if (inc)
		{
			caller.setRegData(register, currentData++);
		}
		
		else 
		{
			caller.setRegData(register, currentData--);
		}
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
}
