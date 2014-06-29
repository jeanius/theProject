package com.jeanpower.reggieproject;

public class Arrow implements Instruction{
	
	private Instruction toInstruction;
	private Instruction pred;
	private Instruction succ;
	private Game caller;
	private int register;
	private int identity;
	private boolean loop;
	private int spaces;

	
	public Arrow(Game g){
		 
		caller = g;
		succ = null;
		pred = null;
		loop = true;
	}
	
	
	@Override
	public void doWork() {
		
		if (loop)
		{
			caller.setCurrPos(toInstruction);
		}
		
		else
		{
			if (caller.getRegData(register) == 0)
			{
				caller.setCurrPos(toInstruction);
			}
			
			else
			{
				caller.setCurrPos(succ);
			}
		}
	
	}

	@Override
	public void setSucc(Instruction successor) {
		succ = successor;
	}

	@Override
	public Instruction getSucc() {
		
		if (null == succ){
			return null;
		}
	
		else {
			return succ;
		}
	}

	@Override
	public void setPred(Instruction predecessor) {
		pred = predecessor;
		register = pred.getRegister();
		this.calculateSpaces();
	}
	
	public void calculateSpaces(){
		
		int count = 1; 
		Instruction currPos = pred;
		
		while (currPos != null && toInstruction != null && currPos.getId() != toInstruction.getId()){
			count ++;
			
			if (loop)
			{
				currPos = currPos.getPred();
			}
			
			else
			{
				currPos = currPos.getSucc();	
			}
		}
		
		spaces = count;
	}
	
	public int getSpaces(){
		return spaces;
	}

	@Override
	public Instruction getPred() {
		if (null == pred){
			return null;
		}
	
		else {
			return pred;
		}
	}

	@Override
	public void setRegister() {
		//?!!!
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
	
	public boolean getType(){
		return loop;
	}
	
	public void setType(){
		
		if (loop)
		{
			loop = false;
		}
		
		else 
		{
			loop = true;
		}
	}
	
	public void setTo(Instruction to){
		toInstruction = to;
		this.calculateSpaces();
	}
	
	public Instruction getTo(){
		return toInstruction;
	}
}
