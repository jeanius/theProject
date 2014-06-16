package com.jeanpower.reggieproject;

public interface Instruction {

	public void dowork();
	public void setSucc(Instruction successor);
	public Instruction getSucc();
	public void setPred(Instruction predecessor);
	public Instruction getPred();
	public void setRegister(int register);
	public int getRegister();
	public void setId(int ID);
	public int getId();

}
