package com.jeanpower.reggieproject;

import java.util.ArrayList;

import android.content.Context;

public class ErrorCheck {

	private Game game;
	private Context context;
	
	public ErrorCheck(Game g, Context con){
		
		game = g;
		context = con;
	}
	
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

							game.showActivityMessage(context.getString(R.string.error1));
							instructionsOk = false;
						}
						instruction = instruction.getSucc();
					}
				}

				else {

					if (box.getSucc() instanceof Box){
						game.showActivityMessage(context.getString(R.string.error2));
						instructionsOk = false;
					}

					else if (box.getSucc() instanceof Arrow){
						Arrow arrow = (Arrow) box.getSucc();
						Instruction succSucc = arrow.getSucc();
						boolean next = false;

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
						game.showActivityMessage(context.getString(R.string.error3));
						instructionsOk = false;
					}
				}
			}

			if (i.getId() == game.getLast()){

				if (i instanceof Box){
					game.showActivityMessage(context.getString(R.string.error4));
					instructionsOk = false;
				}
			}
		}
		return instructionsOk;
	}

}
