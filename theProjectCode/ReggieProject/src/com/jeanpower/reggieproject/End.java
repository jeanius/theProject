package com.jeanpower.reggieproject;

public class End implements Instruction{

	private Game caller;
	private int identity;
	private Instruction succ;
	private Instruction pred;
	private int register;

	public End(Game g){

		caller = g;
		succ = null;
		pred = null;
	}


	@Override
	public void doWork() {

		if (pred instanceof Box){

			Box box = (Box) pred;

			if (!box.decDone()){
				caller.setCurrPos(null);
			}

			else
			{
				caller.setCurrPos(succ);
			}
		}
		
		else {
			caller.setCurrPos(null);
		}
	}

	@Override
	public void setSucc(Instruction successor) {
		succ = successor;
	}

	@Override
	public Instruction getSucc() {
		return succ;
	}

	@Override
	public void setPred(Instruction predecessor) {
		pred = predecessor;
		if (pred != null){
			register = pred.getRegister();
		}
	}

	@Override
	public Instruction getPred() {
		return pred;
	}

	@Override
	public void setRegister() {		
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