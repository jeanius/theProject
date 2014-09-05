package com.jeanpower.reggieproject;

import java.util.ArrayList;
import android.content.Context;

/**
 * Set of checks run by Game over program before it is saved, or run.>p>
 * Errors are:<br>
 * 1) Increment Box with a branch arrow<br>
 * 2) Decrement Box without a branch arrow, or End instruction<br>
 * 3) Branch arrow branching to the same instruction<br>
 * 4) The last instruction is a Box without an Arrow or End<br>
 * 5) Branch with a successor of End - End will never be reached<br>
 * 6) Loop with a successor of End - End will never be reached<br>
 * 7) Multiple branches coming from same instruction<br>
 * 8) Multiple loops coming from same instruction<br>
 */
public class ErrorCheck {

	private Game game;
	private Context context;

	/**
	 * Constructor
	 * 
	 * @param g - game calling class
	 * @param con - context 
	 */
	public ErrorCheck(Game g, Context con){
		game = g;
		context = con;
	}
	/**
	 * Check for errors by iterating through the instruction list
	 * 
	 * @param void
	 * @return boolean - if instructions are ok
	 */
	public boolean checkErrors(){

		boolean instructionsOk = true;

		ArrayList<Instruction> instructionList = game.getInstructionList();

		for (Instruction i: instructionList){

			if (i instanceof Box){

				Box box = (Box) i;

				if (box.getType()){

					Instruction instruction = box.getSucc();

					while (null != instruction && instruction instanceof Arrow){
						Arrow arrow = (Arrow) instruction;

						if (!arrow.getType()){

							game.showActivityMessage(context.getString(R.string.error1)); //INC should not have branch arrows
							instructionsOk = false;
						}
						instruction = instruction.getSucc();
					}
				}

				else {

					if (box.getSucc() instanceof Box){
						game.showActivityMessage(context.getString(R.string.error2)); //DEB should have branch or end as successor
						instructionsOk = false;
					}

					else if (box.getSucc() instanceof Arrow){
						Arrow arrow = (Arrow) box.getSucc();
						Instruction succSucc = arrow.getSucc();
						boolean next = false;

						//This accounts for if DEB has loop as succ. This must be followed by a branch/end
						if (succSucc instanceof Arrow){

							Arrow succArrow = (Arrow) succSucc;
							if (!succArrow.getType()){
								next = true;
							}
						}

						if (succSucc instanceof End){
							next = true;
						}

						if (arrow.getType() && !next){
							game.showActivityMessage(context.getString(R.string.error2));
							instructionsOk = false;
						}
					}
				}
			}

			else if (i instanceof Arrow){
				Arrow arrow = (Arrow) i;

				if (!arrow.getType()){

					if (arrow.getTo().getId() == arrow.getPred().getId()){
						game.showActivityMessage(context.getString(R.string.error3)); //Branch cannot branch to itself
						instructionsOk = false;
					}

					if (arrow.getSucc() instanceof End){
						game.showActivityMessage(context.getString(R.string.error12)); //Branch on same box as end
						instructionsOk = false;
					}

					else if (arrow.getSucc() instanceof Arrow){

						Arrow arrowSucc = (Arrow) arrow.getSucc();

						if (!arrowSucc.getType())
						{
							game.showActivityMessage(context.getString(R.string.error15)); //Multiple arrows from same box
							instructionsOk = false;
						}
					}
				}

				if (arrow.getType()){
					
					if (arrow.getSucc() instanceof End){
						game.showActivityMessage(context.getString(R.string.error13)); //Loop on the same box as end
						instructionsOk = false;
					}

					else if (arrow.getSucc() instanceof Arrow){

						Arrow arrowSucc = (Arrow) arrow.getSucc();

						if (arrowSucc.getType())
						{
							game.showActivityMessage(context.getString(R.string.error14)); //Multiple arrows from same box
							instructionsOk = false;
						}
					}
				}
			}

			if (i.getId() == game.getLast()){

				if (i instanceof Box){
					game.showActivityMessage(context.getString(R.string.error4)); // Last instruction is not an End/Loop
					instructionsOk = false;
				}
			}
		}
		return instructionsOk;
	}
}
