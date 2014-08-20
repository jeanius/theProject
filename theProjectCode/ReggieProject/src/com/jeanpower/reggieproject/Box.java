package com.jeanpower.reggieproject;

/**
 * Box model object
 * <p>
 * 
 * A box corresponds to either an increment or decrement/branch instruction.<p>
 * Holds attributes to relating to identity and position of Box within the doubly linked list <p>
 * of instructions, type of Box, action of Box when it is doing work, and associated register.<p>
 * <p>
 * Implements instruction interface.
 * <p>
 * Note: Does not hold drawing information, as this is controlled by MainActivity.
 * <p>
 * @author Jean Power 2014
 * 
 */

public class Box implements Instruction{

	private int register;
	private Game caller;
	private Instruction succ;
	private Instruction pred;
	private boolean inc; //true if increment, false if decrement/branch
	private int identity;
	private boolean decrementDone;

	/**
	 * Constructor.
	 * 
	 * @param Game - calling class.
	 */
	public Box(Game g){

		inc = true;
		register = 0;
		caller = g;
		succ = null;
		pred = null;
	}

	/**
	 * Completes work of Box instruction
	 * <p>
	 * If increment, box increments its associated register, sets currPos to its successor.<p>
	 * If decrement/branch, box decrements its associated register, sets currPos to its successor.<p>
	 * Also captures if decrement was possible (Cannot reduce past zero). This is used to control action next instruction (branch, end)<p>
	 * <p>
	 * @param void
	 * @return void
	 */
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


	//Getters/Setters

	/** Return if decrement was done
	 * @param void
	 * @return boolean 
	 */
	public boolean decDone(){
		return decrementDone;
	}

	/** Set successor of this instruction
	 * @param Instruction - new successor
	 * @return void 
	 */
	@Override
	public void setSucc(Instruction successor) {	
		succ = successor;
	}

	/** Return successor of this instruction
	 * @param void
	 * @return Instruction - successor 
	 */
	@Override
	public Instruction getSucc() {
		return succ;
	}

	/** Set predecessor of this instruction
	 * @param Instruction - new predecessor
	 * @return void 
	 */
	@Override
	public void setPred(Instruction predecessor) {
		pred = predecessor;
	}

	/** Return predecessor of this instruction
	 * @param void
	 * @return Instruction - predecessor
	 */
	@Override
	public Instruction getPred() {
		return pred;
	}

	/** Return type of box - true increment, false decrement/branch
	 * @param void
	 * @return boolean 
	 */
	public boolean getType(){
		return inc;
	}

	/** Switch type of box  - true increment, false decrement/branch
	 * @param void
	 * @return void
	 */
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

	/** Set associated register for this instruction
	 * <p>
	 * As on screen view of Box is tapped, the associated register is updated<p>
	 * to the next register.<p>
	 * <p>
	 * @param void
	 * @return void 
	 */
	public void setRegister() {

		register++;

		if (register>=caller.getMaxReg()){

			register = 0;
		}
	}

	/** Return register for this instruction
	 * @param void
	 * @return int - register number
	 */
	@Override
	public int getRegister() {
		return register;
	}

	/** Set associated register for this instruction
	 * <p>
	 * Direct setting of register, not iteration<p>
	 * @param int - register number
	 * @return void
	 */
	public void changeRegister(int reg) {
		register = reg;
	}


	/** Set ID of this instruction
	 * <p>
	 * ID is unique to this Box, and connects onscreen view with Box.
	 * <p>
	 * @param int - ID
	 * @return void 
	 */
	@Override
	public void setId(int ID) {
		identity = ID;
	}

	/** Return ID for this instruction
	 * @param void
	 * @return int - register number
	 */
	@Override
	public int getId() {
		return identity;
	}
}