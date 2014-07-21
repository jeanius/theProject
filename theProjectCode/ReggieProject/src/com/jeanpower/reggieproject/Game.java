//Presenter - Gateway to model, provides View with data it needs.

package com.jeanpower.reggieproject;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

public class Game {

	private Instruction first;
	private Instruction last;
	private Instruction lastBox;
	private Instruction currPos;
	private int[] registers;
	private final MainActivity activity;
	private final int MAXREGISTERS = 10;
	private Instruction prevPos = null;

	/**
	 * Constructor. Zeros all registers, instantiates instance variables.
	 * <p>
	 * 
	 * @return void
	 */
	public Game(MainActivity a) {
		activity = a;
		first = null;
		last = null;
		registers = new int[MAXREGISTERS];

		for (int i = 0; i < MAXREGISTERS; i++) {
			registers[i] = 0;
		}
	}

	public Instruction getPrevPos(){
		return prevPos;
	}

	public void runGame() {

		currPos = first;
		RunGame rungame = new RunGame();
		rungame.execute();
	}


	/*int check = 1;

		while (null != currPos) {

			// currPos.doWork();
			Log.d("This is the number instruction", check + "");
			Log.d("This is the pred of the current instruction", currPos.getPred() + "");
			Log.d("This is the current instruction", currPos + "");
			Log.d("This is the succ of current instruction", currPos.getSucc() + "");

			if (currPos instanceof Arrow) {
				Arrow a = (Arrow) currPos;
				Log.d("This is where the arrow is going", a.getTo() + "");
			}

			currPos = currPos.getSucc();
			check++;
		}*/


	/**
	 * Iterates through linked list of instructions, returns list of
	 * instructions in correct order, from a certain point in list
	 * <p>
	 * 
	 * @param int. Targeted point of instruction
	 * @return List<Instruction>
	 */
	public List<Instruction> getInstructionList() {

		List<Instruction> instructionList = new ArrayList<Instruction>();

		Instruction currentPosition = first;

		while (null != currentPosition) {

			instructionList.add(currentPosition);
			currentPosition = currentPosition.getSucc();
		}
		return instructionList;
	}

	public List<Instruction> getToFrom(Instruction from, int to) {

		List<Instruction> instructionList = new ArrayList<Instruction>();

		Instruction current = from;
		int target = to;
		boolean done = false;

		while (null != current && !done) {

			instructionList.add(current);

			if (current.getId() == target) {
				done = true;
			}

			else {

				current = current.getSucc();
			}
		}
		return instructionList;
	}

	public Instruction getInstruction(int ID) {

		Instruction currentPosition = first;
		int targetInstruction = ID;
		boolean found = false;

		while (null != currentPosition && !found) {

			if (targetInstruction == currentPosition.getId()) {
				found = true;
			}

			else {
				currentPosition = currentPosition.getSucc();
			}
		}

		return currentPosition;
	}

	@SuppressLint("NewApi")
	// Have dealt with different versions in code.
	public void newInstruction(int resourceID) {

		Instruction instruction = null;

		switch (resourceID) {
		case R.id.new_arrow_button:
			instruction = new Arrow(this);
			break;
		case R.id.new_box_button:
			instruction = new Box(this);
			lastBox = instruction;
			break;
		case R.id.new_end_button:
			instruction = new End(this);
			break;
		}

		if (null == first) // Can only be a box.
		{
			first = instruction;
			last = instruction;
			instruction.setSucc(null);
		}

		else {

			last.setSucc(instruction);
			instruction.setPred(last);
			last = instruction;

			if (instruction instanceof Arrow) {

				Arrow arrow = (Arrow) instruction;
				arrow.setTo(lastBox);
			}
		}

		if (Build.VERSION.SDK_INT >= 17) {
			instruction.setId(View.generateViewId());
		}

		else {

			instruction.setId(Util.generateViewId());
		}
	}

	/**
	 * Updates Box instruction to new register. Updates End instruction to a new Box.
	 * <p>
	 * @param Instruction to be updated
	 * @return void
	 */

	public void updateInstruction(Instruction i) {

		if (i instanceof Box) {
			Box box = (Box) i;
			box.setRegister();
		}

		else if (i instanceof End){
			Instruction prev = i.getPred().getPred();
			End end = (End) i;

			if (null != prev){
				while (null!= prev && (prev instanceof Arrow || prev instanceof End)){
					prev = prev.getPred();
				}

				Box prevBox = (Box) prev;
				Instruction boxSucc = prevBox.getSucc();
				Instruction endPred = end.getPred();
				Instruction endSucc = end.getSucc();

				if (boxSucc instanceof End){
					this.delEnd((End) i);
				}

				else {
					endPred.setSucc(endSucc);
					prevBox.setSucc(end);

					if (null != endSucc){
						endSucc.setPred(endPred);
					}
					else {
						last = endPred;
					}

					if (boxSucc instanceof Arrow){

						Instruction arrowSucc = boxSucc.getSucc();

						end.setSucc(arrowSucc);
						end.setPred(boxSucc);

						if (null != arrowSucc)
						{
							arrowSucc.setPred(end);
						}
					}

					else if (boxSucc instanceof Box || null == boxSucc){

						end.setSucc(boxSucc);
						end.setPred(prevBox);

						if (null != boxSucc){
							boxSucc.setPred(end);
						}
					}
				}
			}
		}
	}


	/**
	 * Assesses if the head of the arrow can move
	 * <p>
	 * To prevent issues with an Arrow having predecessors which are incorrect in relation to its goto, 
	 * this method reviews if the goto is the same as the predecessor, or before (if loop), or after (if branch).
	 * These are acceptable scenarios.
	 * <p>
	 * @param Arrow that is checked, ID of the Box that the head should move to.
	 * @return boolean
	 */
	public boolean headMove(Arrow from, int to) {

		boolean move = false;
		Instruction currentPosition = from;

		while (null != currentPosition && !move) {

			if (currentPosition.getId() == to) { // Allows for pred/goto to be the same
				move = true;
			}

			else {

				if (from.getType()){

					currentPosition = currentPosition.getPred();
				}

				else 
				{
					currentPosition = currentPosition.getSucc();
				}
			}
		}

		return move;
	}

	public void updateHead(Arrow arrow, Box move) {

		boolean canMove = this.headMove(arrow, move.getId());

		if (canMove) {

			arrow.setTo(move);
			arrow.calculateSpaces();
		}
	}


	/**
	 * Assesses if the tail of the arrow can move
	 * <p>
	 * To prevent issues with an Arrow having predecessors which are incorrect in relation to its goto, 
	 * this method reviews if the predecessor is the same as the goto, or after (if loop), or before (if branch).
	 * These are acceptable scenarios.
	 * <p>
	 * @param Arrow that is checked, ID of the Box that becomes the predecessor.
	 * @return boolean
	 */

	public boolean tailMove(Arrow from, int to) {

		boolean move = false;
		Instruction currentPosition = from.getTo();

		while (null != currentPosition && !move) {

			if (currentPosition.getId() == to) { // Allows for pred/goto to be the same
				move = true;
			}

			else {

				if (from.getType()){

					currentPosition = currentPosition.getSucc();
				}

				else 
				{
					currentPosition = currentPosition.getPred();
				}
			}
		}

		return move;
	}

	public void updateTail(Arrow arrow, Box move) {

		boolean canMove = this.tailMove(arrow, move.getId()); // Is the box before the head of arrow?

		if (canMove) {

			Instruction arrowPred = arrow.getPred();
			Instruction arrowSucc = arrow.getSucc();
			Instruction boxSucc = move.getSucc();

			arrow.setPred(move);
			move.setSucc(arrow);
			arrowPred.setSucc(arrowSucc);
			arrow.setSucc(boxSucc); //Instruction inserts before successor - correct position at all times - no movement of tail after end.

			if (arrowSucc != null) {
				arrowSucc.setPred(arrowPred);
			}

			else {
				last = arrowPred;
			}

			if (boxSucc != null) {
				boxSucc.setPred(arrow); //
			}

			arrow.calculateSpaces();
		}
	}

	/**
	 * Changes Box instruction to inc or dec. Changes Arrow instruction to loop or branch.
	 * <p>
	 * @param Instruction to be updated
	 * @return void
	 */
	public void changeInstruction(Instruction i) {

		if (i instanceof Box) {
			Box box = (Box) i;
			box.setType();
		}

		else if (i instanceof Arrow) {
			Arrow arrow = (Arrow) i;
			Instruction pred = arrow.getPred();
			Instruction succ = arrow.getSucc();
			Instruction goTo = arrow.getTo();
			Instruction goToSucc = goTo.getSucc();

			arrow.setType();

			if (pred.getId() != goTo.getId()){

				arrow.setPred(goTo);
				goTo.setSucc(arrow);
				arrow.setSucc(goToSucc);
				pred.setSucc(succ);

				if (goToSucc != null) {

					goToSucc.setPred(arrow);
				}

				if (succ != null) {

					succ.setPred(pred);
				}

				else {
					last = pred;
				}
			}
			arrow.calculateSpaces();
		}
	}

	public void delBox(Box delete) {
	}

	public void delArrow(Arrow delete) {
	}

	public void delEnd(End delete) {
	}

	/**
	 * Sets current position within running game
	 * <p>
	 * 
	 * @param Instruction
	 *            . New current instruction
	 * @return void
	 */
	public void setCurrPos(Instruction newPos) {

		currPos = newPos;
	}

	/**
	 * Returns data held in specific register
	 * <p>
	 * 
	 * @param int. Index/number of register.
	 * @return int
	 */
	public int getRegData(int registerNum) {

		return registers[registerNum];
	}

	/**
	 * Increments data held in specific register
	 * <p>
	 * Increments, then calls method in activity to pull data and update UI
	 * <p>
	 * 
	 * @param int. Index/number of register.
	 * @return void
	 */
	public void incrementReg(int registerNum) {

		int newNum = registers[registerNum] + 1;
		registers[registerNum] = newNum;

	}

	/**
	 * Decrements data held in specific register
	 * <p>
	 * Decrements, then calls method in activity to pull data and update UI
	 * <p>
	 * 
	 * @param int. Index/number of register.
	 * @return void
	 */
	public boolean decrementReg(int registerNum) {

		if (registers[registerNum] >=1){
			int newNum = registers[registerNum] - 1;
			registers[registerNum] = newNum;
			return true;
		}

		else {
			return false;
		}
	}

	/**
	 * Zeros a specific register
	 * <p>
	 * Zeros, then calls method in activity to pull data and update UI
	 * <p>
	 * 
	 * @param int. Index/number of register.
	 * @return void
	 */
	public void zeroReg(int registerNum) {

		registers[registerNum] = 0;
		;
		activity.setRegisters();
	}

	public int getMaxReg() {
		return MAXREGISTERS;
	}

	public Instruction getFirst() {
		return first;
	}


	private class RunGame extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... params) {

			while (null != currPos){

				prevPos = currPos;
				currPos.doWork();
				activity.updateInstructionDisplay(prevPos);

				try {
					Thread.sleep(1000);
					activity.updateInstructionDisplay(prevPos);
				}
				catch (Exception e){
					Log.d("This was interrupted", "interrupted");
				}	
			}
			return null;
		}

	}

}
