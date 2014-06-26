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
	private final int MAXREGISTERS = 10;


	/**
	 * Constructor. Zeros all registers, instantiates instance variables.                
	 * <p>
	 * @return void
	 */	
	public Game(MainActivity a){
		activity = a;
		first = null;
		last = null;
		registers = new int [MAXREGISTERS];

		for (int i = 0; i<MAXREGISTERS; i++)
		{
			registers[i] = 0;
		}
	}

	public void runGame(){
		
		currPos = first;
		
		while (null != currPos){
			currPos.doWork();
		}
	}

	/**
	 * Iterates through linked list of instructions, returns list of instructions in correct order, from a certain point in list      
	 * <p>
	 * @param int. Targeted point of instruction
	 * @return List<Instruction>
	 */	
	public List<Instruction> getInstructionList(int fromInst){

		List<Instruction> instructionList = new ArrayList<Instruction>();

		Instruction currentPosition = first;
		int targetInstruction = fromInst;

		while(null != currentPosition){

			if (targetInstruction == currentPosition.getId())
			{
				while(null != currentPosition)
				{
					currentPosition = currentPosition.getSucc();
					instructionList.add(currentPosition);
				}
			}	
			else if (targetInstruction == 0)
			{
				while(null != currentPosition)
				{
					instructionList.add(currentPosition);
					currentPosition = currentPosition.getSucc();	
				}
			}

			else
			{
				currentPosition = currentPosition.getSucc();
			}
		}

		return instructionList;
	}

	@SuppressLint("NewApi") //Have dealt with different versions in code.
	public int newInstruction(int resourceID){

		Instruction instruction = null;
		int previousInstruction = 0;
		
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

		
		if (null == first) //Can only be a box.
		{
			first = instruction;
			last = instruction;
			instruction.setSucc(null);
		}
	
		else {

			last.setSucc(instruction);
			instruction.setPred(last);
			last = instruction;
			
			if (instruction instanceof Arrow){
				
				Arrow arrow = (Arrow) instruction;
				arrow.setTo(last.getPred());
			}
			
			previousInstruction = instruction.getPred().getId();
		}

		if (Build.VERSION.SDK_INT >= 17 ) {
			instruction.setId(View.generateViewId());
		}

		else {

			instruction.setId(Util.generateViewId());
		}
		
		return previousInstruction;
	}

	public Instruction updateInstruction(int instructionID){

		Instruction currentPosition = first;
		int targetInstruction = instructionID;
		boolean found = false;

		while(null != currentPosition && !found){

			if (targetInstruction == currentPosition.getId())
			{	
				found = true;
				
				if (currentPosition instanceof Box)
				{
					currentPosition.setRegister();
				}
			}	
			else
			{
				currentPosition = currentPosition.getSucc();	
			}
		}

		return currentPosition;
	}

	public int changeInstruction(int instructionID){
		
		Instruction currentPosition = first;
		int previousInstruction = 0;
		int targetInstruction = instructionID;
		boolean found = false;

		while(null != currentPosition && !found){

			if (targetInstruction == currentPosition.getId())
			{	
				found = true;
				
				if (currentPosition instanceof Box)
				{
					Box b = (Box) currentPosition;
					b.setType();
				}
				
				else if (currentPosition instanceof Arrow)
				{
					Arrow a = (Arrow) currentPosition;
					a.setType();
					Instruction goTo = a.getTo();
					a.setTo(a.getPred());
					a.setPred(goTo);
				}
			}	
			else
			{
				currentPosition = currentPosition.getSucc();	
			}
		}

		if (targetInstruction != first.getId()){
			
			previousInstruction = currentPosition.getPred().getId();
		}
		
		return previousInstruction;
	}
	
	
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

	public int getMaxReg(){
		return MAXREGISTERS;
	}
	
}
