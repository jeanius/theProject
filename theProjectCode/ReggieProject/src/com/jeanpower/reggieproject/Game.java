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
	private Instruction lastBox;
	private Instruction currPos;
	private int[] registers;
	private MainActivity activity;
	private final int MAXREGISTERS = 10;

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

	public void runGame() {

		currPos = first;
		int check = 1;

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
		}
	}

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
				Log.d("This is where i set my to to", arrow.getTo() + "");
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
	 * Updates Box instruction to new register. Updates Arrow instruction with
	 * new pred/succ when Arrow tail moved
	 * <p>
	 * 
	 * @param Instruction
	 *            , Box. The instruction to be updated, and the Box the tail
	 *            moved to
	 * @return void
	 */

	public void updateInstruction(Instruction i) {

		if (i instanceof Box) {
			Box box = (Box) i;
			box.setRegister();
		}
	}

	public boolean isSuccOfPred(Instruction from, int to) {

		boolean isSucc = false;
		Instruction currentPosition = from;

		while (null != currentPosition && !isSucc) {

			if (currentPosition.getId() == to) { // Allows for pred/goto to be
				// the same
				isSucc = true;
			}

			else {
				currentPosition = currentPosition.getSucc();
			}
		}

		return isSucc;
	}

	public void updateHead(Arrow arrow, Box move) {

		boolean succOfPred = this.isSuccOfPred(arrow, move.getId()); // Is theboxafterthetailofarrow?

		if ((arrow.getType() && !succOfPred) || (!arrow.getType() && succOfPred)) {

			arrow.setTo(move);
			arrow.calculateSpaces();
		}
	}

	public boolean isPredOfTo(Instruction from, int to) {

		boolean isPred = false;
		Arrow arrow = (Arrow) from;
		Instruction currentPosition = arrow.getTo();

		while (null != currentPosition && !isPred) {

			if (currentPosition.getId() == to) { // Allows for pred/goto to be
				// the same
				isPred = true;
			}

			else {
				currentPosition = currentPosition.getPred();
			}
		}

		return isPred;
	}

	public void updateTail(Arrow arrow, Box move) {

		boolean predOfTo = this.isPredOfTo(arrow, move.getId()); // Is the box
		// before
		// the head
		// of arrow?

		if ((arrow.getType() && !predOfTo) || (!arrow.getType() && predOfTo)) {

			Instruction arrowPred = arrow.getPred();
			Instruction arrowSucc = arrow.getSucc();
			Instruction boxSucc = move.getSucc();

			arrow.setPred(move);
			move.setSucc(arrow);
			arrowPred.setSucc(arrowSucc);
			arrow.setSucc(boxSucc);

			if (arrowSucc != null) {
				arrowSucc.setPred(arrowPred);
			}

			else {
				last = arrowPred;
			}

			if (boxSucc != null) {
				boxSucc.setPred(arrow);
			}
			arrow.calculateSpaces();
		}
	}

	/**
	 * Updates Box instruction to inc or dec. Updates Arrow instruction to loop
	 * or break
	 * <p>
	 * 
	 * @param Instruction
	 *            to be updated
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
			}
			
			/*
			if (pred.getId() != goTo.getId()) {

				if (pred instanceof Arrow) {
					Arrow predArrow = (Arrow) pred;
					arrow.setTo(predArrow.getTo());
					Log.d("I am in here", "here");
				}

				else {
					
					arrow.setTo(pred);
				}
				
				arrow.setPred(goTo);

				arrow.setSucc(goToSucc);

				goTo.setSucc(arrow);
				pred.setSucc(succ);

				if (goToSucc != null) {

					goToSucc.setPred(arrow);
				}
				if (succ != null) {
					succ.setPred(pred);
				}
*/
				arrow.calculateSpaces();
			
		}
	}

	public void delBox(Instruction delete) {
	}

	public void delArrow(Instruction delete) {
	}

	public void delEnd(Instruction delete) {
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
		activity.setRegisters();
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
	public void decrementReg(int registerNum) {

		registers[registerNum] = registers[registerNum]--;
		activity.setRegisters();
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

}
