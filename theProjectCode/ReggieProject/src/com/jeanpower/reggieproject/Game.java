//Presenter - Gateway to model, provides View with data it needs.

package com.jeanpower.reggieproject;

import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.os.Build;
import android.util.Log;
import android.view.View;

public class Game {

	private Instruction first;
	private Instruction last;
	private Instruction currPos;
	private int [] registers;
	private MainActivity activity;


	/**
	 * Constructor. Zeros all registers, instansiates instance variables.                
	 * <p>
	 * @return void
	 */	
	public Game(MainActivity a){
		activity = a;
		first = null;
		last = null;
		registers = new int [activity.getMaxReg()];

		for (int i = 0; i<activity.getMaxReg(); i++)
		{
			registers[i] = 0;
		}
	}

	public void runGame(){

	}

	/**
	 * Iterates through linked list of instructions, returns list of instructions in correct order          
	 * <p>
	 * @return List<Instruction>
	 */	
	public List<Instruction> getInstructionList(){

		List<Instruction> instructionList = new ArrayList<Instruction>();

		Instruction currentPosition = first;

		while(null != currentPosition)
		{
			instructionList.add(currentPosition);
			currentPosition = currentPosition.getSucc();
		}

		Log.d("This is the size of the instruction list", instructionList.size() +"");
		return instructionList;
	}

	@SuppressLint("NewApi") //Have dealt with different versions in code.
	public void newInstruction(int resourceID){

		Instruction instruction = null;

		switch (resourceID) {
		case R.id.new_arrow_button:
			instruction = new Arrow(this);
			break;
		case R.id.new_box_button:
			instruction = new Box(this);
			break;
		case R.id.new_end_button:
			instruction = new End(this);
			break;
		}

		
		if (null == first)
		{
			first = instruction;
			last = instruction;
			instruction.setSucc(null);
		}

		else {
			last.setSucc(instruction);
			instruction.setPred(last);
			last = instruction;
		}
		
		if (Build.VERSION.SDK_INT >= 17 ) {
			instruction.setId(View.generateViewId());
		}
		
		else {
			
			instruction.setId(Util.generateViewId());
		}
	}

	public void updateIns(Object o){}

	public void delBox(Instruction delete){}

	public void delArrow(Instruction delete){}

	public void delEnd(Instruction delete){}

	/**
	 * Sets current position within running game              
	 * <p>
	 * @param Instruction. New current instruction
	 * @return void
	 */	
	public void setCurrPos(Instruction newPos){

		currPos = newPos;
	}

	/**
	 * Returns data held in specific register            
	 * <p>
	 * @param int. Index/number of register.
	 * @return int
	 */	
	public int getRegData(int registerNum){

		return registers[registerNum];
	}

	/**
	 * Increments data held in specific register        
	 * <p>
	 * Increments, then calls method in activity to pull data and update UI
	 * <p>
	 * @param int. Index/number of register.
	 * @return void
	 */	
	public void incrementReg(int registerNum){

		int newNum = registers[registerNum]+1;
		registers[registerNum] = newNum;
		activity.setRegisters();
	}

	/**
	 * Decrements data held in specific register        
	 * <p>
	 * Decrements, then calls method in activity to pull data and update UI
	 * <p>
	 * @param int. Index/number of register.
	 * @return void
	 */	
	public void decrementReg(int registerNum){

		registers[registerNum] = registers[registerNum]--;
		activity.setRegisters();
	}

	/**
	 * Zeros a specific register        
	 * <p>
	 * Zeros, then calls method in activity to pull data and update UI
	 * <p>
	 * @param int. Index/number of register.
	 * @return void
	 */	
	public void zeroReg(int registerNum){

		registers[registerNum] = 0;;
		activity.setRegisters();
	}
}
