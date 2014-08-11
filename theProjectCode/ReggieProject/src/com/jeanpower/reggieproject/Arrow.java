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
		spaces = 1;
	}

	@Override
	public void doWork() {

		if (loop)
		{
			if (pred instanceof Box){
				Box box = (Box) pred;

				if (!box.getType()){

					if (!box.decDone()){
						caller.setCurrPos(succ);
					}

					else
					{
						caller.setCurrPos(toInstruction);
					}
				}
				else {
					caller.setCurrPos(toInstruction);
				}
			}
		}

		else
		{
			if (pred instanceof Box){

				Box box = (Box) pred;

				if (!box.decDone()){
					caller.setCurrPos(toInstruction);
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

	public void calculateSpaces(){

		int count = 1; 
		Instruction currPos;

		if (loop){

			currPos = pred;


			while (currPos != null && currPos.getId() != toInstruction.getId()){


				if (currPos instanceof Box) {
					count ++;
				}

				currPos = currPos.getPred();
			}
		}

		else {

			if (pred.getId() != toInstruction.getId()){

				currPos = pred;

				while (currPos != null && currPos.getId() != toInstruction.getId()){
					currPos = currPos.getSucc();

					if (currPos instanceof Box) {
						count ++;
					}
				}
			}
		}

		spaces = count;
	}

	public int getSpaces(){
		return spaces;
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
	}

	public Instruction getTo(){
		return toInstruction;
	}
}
