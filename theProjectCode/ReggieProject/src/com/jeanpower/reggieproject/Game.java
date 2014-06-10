package com.jeanpower.reggieproject;

import android.app.Activity;
import android.util.Log;

public class Game {
	
	private Instruction first;
	private Instruction currPos;
	private int [] registers;
	private MainActivity activity;

	
	public Game(MainActivity a){
		activity = a;
		registers = new int [10];
		int x = 1;
		
		for (int i = 0; i<10; i++)
		{
			x = x + 7;
			registers[i] = x;
		}
		
		
		activity.setRegisters();
	}

	public void runGame(){}
	
	public void getInstructionSet(){}

	public void updateIns(Object o){}
	
	public void delBox(Instruction delete){
		
	}
	public void delArrow(Instruction delete){
		
	}
	public void delEnd(Instruction delete){
		
	}
	public void setCurrPos(Instruction newPos){
		
		currPos = newPos;
		
	}
	
	public int getRegData(int registerNum){
		
		int returnedVal = registers[registerNum];
		Log.d("here", returnedVal + "");
		
		return returnedVal;
	}
	
	public void setRegData(int registerNum, int data){
		
		registers[registerNum] = data;
		activity.setRegisters();
	}

}
