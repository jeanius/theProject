package com.jeanpower.reggieproject;

/**
 * File Save/Load model class
 * <p>
 * Handles all actions related to saving and loading files. <p>
 * This includes getting user input for name, exporting program to a file, importing files and error checking <p>
 * <p>
 * Interacts with Game object to get the instruction list, and create an instruction list, and MainActivity to send error messages
 * <p>
 * aFileDialog Copyright 2013 Jose F. Maldonado
 * <p>
 * @author Jean Power 2014
 * 
 */

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Scanner;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import ar.com.daidalos.afiledialog.FileChooserDialog;
import ar.com.daidalos.afiledialog.FileChooserLabels;

public class SaveLoad{

	private Game game;
	private Context context;
	private MainActivity main;
	private int counter = 0;
	private String [] instructionInput;
	private Instruction [] instructionArray;
	private String [] strings;
	private int [] boxEndIDs;
	private int numIns;

	/**
	 * Constructor.
	 * <p>
	 * <p>
	 * @param Context - application context, MainActivity - calling class, Game - current game
	 */
	public SaveLoad(Context c, MainActivity ma, Game g){
		context = c;
		main = ma;
		game = g;
	}

	/**
	 * Shows the FileDialog
	 * <p>
	 * If file loaded, calls readFile helper method.
	 * If file saved, calls createGameText helper method.
	 * <p>
	 * @param void
	 * @return void
	 */
	public void saveLoad(){

		FileChooserDialog dialog = new FileChooserDialog(context);
		dialog.setCanCreateFiles(true);
		dialog.setFilter(".*txt"); //Can only import .txt files
		dialog.setShowFullPath(true);
		dialog.loadFolder(Environment.getExternalStorageDirectory() + "/Documents/"); //File Dialog always shows document folder
		FileChooserLabels labels = new FileChooserLabels();
		labels.createFileDialogTitle = "Save Game";
		labels.createFileDialogMessage = "Enter game name";
		labels.labelAddButton = "Navigate to file or click here to save current game";
		dialog.setLabels(labels);
		dialog.addListener(new FileChooserDialog.OnFileSelectedListener() {

			public void onFileSelected(Dialog source, File file) { //Load File
				source.hide();
				readFile(file); //Read in file and create instruction list
				main.showMessage("File selected: " + file.getName());
			}

			public void onFileSelected(Dialog source, File folder, String name) { //Save File
				source.hide();
				String filename = name + ".txt";
				File output = new File(folder, filename);
				FileOutputStream fw = null;

				try {			
					fw = new FileOutputStream(output);
					byte[] filedata = createGameText().getBytes(); //Get text output from instruction list
					fw.write(filedata);
					fw.close();
				}
				catch (Exception e){
					Log.d("exception", e +"");
					Log.e("exception", Log.getStackTraceString(e));
				}

				if (null != fw){
					main.showMessage("File created: " + folder.getName() + "/" + name);
				}
				else { //If user is denied access to folder
					main.showMessage("You need to save to a storage folder, like 'Documents'");
				}
			}
		});

		dialog.show();
	}

	/**
	 * Creates text from instruction list for output
	 * <p>
	 * Includes error checking
	 * <p>
	 * @param void
	 * @return void
	 */
	public String createGameText(){

		int counter = 0; //Position in list, also step number.
		ArrayList<Instruction> instructionList = game.getInstructionList();
		numIns = instructionList.size();

		strings = new String[numIns];
		boxEndIDs = new int[numIns];

		/*
		 * Iterates through instruction list
		 * First, boxes/ends are added in order. These are separate steps.
		 * Arrows are not separate instructions in Dennett's language.
		 * 
		 * Final output of this step:
		 * 0,DEB,0,1 - DEB with 1 successor arrow (always a branch) will not be complete. Needs branch.
		 * 1,INC,2,2 - INC with no arrow will be complete
		 * 2,DEB,1,3,3 - DEB with END successor will be complete
		 * 3,END - END will be complete
		 * 4,INC,3 - INC with arrow will not be complete. Needs go-to.
		 * 5,DEB,4 - DEB with 2 successor arrows will not be complete. Needs go-to and branch.
		 */
		for (Instruction i: instructionList){

			if (i instanceof Box){

				StringBuilder sb = new StringBuilder();

				Box box = (Box) i;

				sb.append(counter + ","); //Step number

				if (box.getType()){ //Type of instruction
					sb.append(main.getString(R.string.inc));
				}

				else {
					sb.append(main.getString(R.string.deb));
				}

				sb.append(box.getRegister() + ",");

				Instruction successor = box.getSucc();
				Instruction succOfSucc = null;

				if (null != successor){
					succOfSucc = box.getSucc().getSucc();
				}

				if (box.getType() && (successor instanceof Box || successor instanceof End)){ //No arrows needed, "Go To" is just the next instruction/step
					sb.append(counter + 1 + ",");
				}

				else if (!box.getType() && successor instanceof Arrow){ //If decrement, successor will always be Arrow
					Arrow successorArrow = (Arrow) successor;

					if (!successorArrow.getType()){ //If the arrow is a branch - go to is successor of arrow

						if (succOfSucc instanceof Box || succOfSucc instanceof End){ //Go To is just next instruction/step, if Box or End
							sb.append(counter + 1 + ",");
						}
					}
				}

				//Successor of End will always be a Box or null, as End automatically positions after arrows
				else if (!box.getType() && successor instanceof End){

					if (null != succOfSucc){
						sb.append(counter + 2 + ","); //If Box, then GoTo is the successor of End
					}
					else {
						sb.append(counter + 1 + ","); //GoTo is end instruction, if no successor
					}

					sb.append(counter + 1 + ",");//Branch is the End instruction
				}

				strings[counter] = sb.toString(); //Stores the half string at the step position in array
				boxEndIDs[counter] = box.getId(); //Stores the Box/End IDs in same position in another array

				counter++;
			}

			else if (i instanceof End){

				StringBuilder sb = new StringBuilder();

				sb.append(counter + ",");

				sb.append(main.getString(R.string.endText));
				strings[counter] = sb.toString();
				boxEndIDs[counter] = i.getId();

				counter++;
			}
		}

		/*
		 * For the instructions with arrows:
		 * This step adds on the loop arrows, as these are in GoTo position - 
		 * Which is the next character in Dennett's language.
		 * 
		 * Final output of this step:
		 * 0,DEB,0,1 - DEB with 1 successor arrow will not change. Need branch
		 * 1,INC,2,2, - N/A
		 * 2,DEB,1,3,3, - N/A
		 * 3,END, - N/A
		 * 4,INC,3,0, - INC with arrow will be completed with "Go to" step
		 * 5,DEB,4,1 - DEB with 2 successor arrows will have added "Go To" step, but not complete. Needs branch.
		 */
		for (Instruction i: instructionList){

			if (i instanceof Arrow){

				Arrow curr = (Arrow) i;

				if (curr.getType()){
					this.arrowHelper(curr);
				}
			}
		}

		/*
		 * For the instructions with arrows:
		 * This step adds on the branch arrows, as these are in Branch position - 
		 * Which is the next character in Dennett's language.
		 * 
		 * Final output of this step: (Not a proper program - just to visualise the added steps)
		 * 0,DEB,0,1,3, - Complete
		 * 1,INC,2,2, - N/A
		 * 2,DEB,1,3,3, - N/A
		 * 3,END, - N/A
		 * 4,INC,3,0, - N/A
		 * 5,DEB,4,1,2 - Complete
		 */
		for (Instruction i: instructionList){

			if (i instanceof Arrow){

				Arrow curr = (Arrow) i;

				if (!curr.getType()){
					this.arrowHelper(curr);
				}
			}
		}

		StringBuilder resultSB = new StringBuilder();

		for (int j = 0; j<numIns; j++){

			if (null != strings[j]){
				resultSB.append(strings[j] + System.getProperty("line.separator"));
			}
		}
		return resultSB.toString();
	}



	/**
	 * Appends goto/branch steps to strings, as per the Arrows
	 * <p>
	 * @param Arrow - current arrow to be added
	 * @return void
	 */
	public void arrowHelper(Arrow curr){

		Arrow pred = null;
		Instruction predBox = curr.getPred();
		int predID = curr.getPred().getId();
		int goToId = curr.getTo().getId();
		int predIndex = 0; //To be the index in array of the arrow's predecessor
		int goToIndex = 0; //To be the index in array of arrow's goto

		if (curr.getPred() instanceof Arrow){
			pred = (Arrow) curr.getPred();	
		}

		while (predBox instanceof Arrow || predBox instanceof End){
			predBox = predBox.getPred();
			predID = predBox.getId();
		}

		for (int j = 0; j<numIns; j++){

			int checkID = boxEndIDs[j]; //Iterate through Box/End IDs and compare to Arrow's goto/Branch IDs

			if (checkID == predID){
				predIndex = j;
			}
			if (checkID == goToId){
				goToIndex = j;
			}
		}

		String predString = strings[predIndex];
		StringBuilder sb = new StringBuilder();
		
		//If more than one arrow out of same box, ignored as one will never be reached.
		if ((curr.getPred() instanceof Box || curr.getPred() instanceof End) ||  (null != pred && (curr.getType() != pred.getType()))){ 

			sb.append(predString);
			sb.append(goToIndex + ",");	
		}

		//And if loop followed by end, add End step number
		if (curr.getType() && curr.getSucc() instanceof End){
			
			int endID = curr.getSucc().getId();
			int endIndex = -1;

			for (int j = 0; j<numIns; j++){

				int checkID = boxEndIDs[j]; //Iterate through Box/End IDs and compare to Arrow's goto/Branch IDs

				if (endID == checkID){
					endIndex = j;
				}
			}
			sb.append(endIndex + ",");
		}
		
		if (sb.toString().length() >0){
			strings[predIndex] = sb.toString();	
		}
	}

	/**
	 * Reads in file, creates String array, checking strings for length and checking step numbers
	 * <p>
	 * @param void
	 * @return void
	 */
	public void readFile(File file){

		Scanner scan = null;
		Scanner scanner = null;

		try {
			try{
				scan = new Scanner(file);
				scanner = new Scanner(file);

				while (scan.hasNextLine()){
					counter ++;
					scan.nextLine();
				}

				instructionInput = new String[counter];
				counter = 0;

				while (scanner.hasNextLine()){
					instructionInput[counter] = scanner.nextLine(); //Array of input strings
					counter ++;
				}
			}

			finally {
				if (null != scan){
					scan.close();
				}
				if (null != scanner){
					scanner.close();
				}
			}
		}

		catch (Exception e) {
			Log.d("Exception", e +"");
			Log.e("exception", Log.getStackTraceString(e));
		}

		boolean done = true;

		for (int i = 0; i<counter; i++){

			String instructionText = instructionInput[i];
			String [] tokens = instructionText.split(",");

			if (tokens.length > 5 || tokens.length < 1){
				
				String error = main.getString(R.string.error5, i); 

				main.showMessage(error);
				done = false;
			}	

			try {
				int step = Integer.parseInt(tokens[0]);

				if (step != i){ //If step does not equal the row number/index number
					done = false;
					
					String error = main.getString(R.string.error6, i); 
					main.showMessage(error);
				}
			}
			catch (Exception e) {
				done = false;
				String error = main.getString(R.string.error7, i); 
				main.showMessage(error);
			}
		}

		//If all the strings have at least one, and no more than 5 tokens, the instruction can be created on screen
		if (done){
			this.createInstruction();
		}	
	}

	/**
	 * From String array of instruction texts, creates all Boxes/Ends
	 * <p>
	 * Includes error checking of instruction and register number
	 * Adds instructions to an array of Instructions
	 * <p>
	 * @param void
	 * @return void
	 */
	public void createInstruction(){	

		instructionArray = new Instruction[counter];
		boolean done = true; //Ensure overall correctness of all rows

		for (int i = 0; i<counter; i++){

			boolean ok = true; //Ensure correctness within row 

			String instructionText = instructionInput[i];
			String [] tokens = instructionText.split(",");

			String instruction = tokens[1];
			int register = -1;

			if (!instruction.equals("INC") && !instruction.equals("DEB") && !instruction.equals("END")){
				done = false;	
				ok = false;
				String error = main.getString(R.string.error8, i); 
				main.showMessage(error);
			}

			if (instruction.equals("INC") || instruction.equals("DEB")){

				try {
					register = Integer.parseInt(tokens[2]);

					if (register >= game.getMaxReg() || register<0){
						done = false;
						ok = false;
						String error = main.getString(R.string.error9, i);
						main.showMessage(error);
					}
				}
				catch(Exception e){
					done = false;
					ok = false;
					String error = main.getString(R.string.error9, i);
					main.showMessage(error);
				}			
			}

			if (ok){

				if (instruction.equals("END")){
					End end = new End(game);
					instructionArray[i] = end;
				}

				else if (instruction.equals("INC") || instruction.equals("DEB")){

					Box box = new Box(game);
					box.changeRegister(register);

					if (instruction.equals("DEB")){
						box.setType();
					}
					instructionArray[i] = box;
				}	


				//Start off as predecessors and successors of each other. Arrows are then inserted
				if (i-1 >= 0){
					Instruction predIns = instructionArray[i-1];
					instructionArray[i].setPred(predIns);
					predIns.setSucc(instructionArray[i]);
				}
			}
		}

		//If overall correct, add arrows.
		if (done){
			this.addArrows();
		}
	}

	/**
	 * From String array of instruction texts, creates all Arrows
	 * <p>
	 * Includes error checking of step numbers, 
	 * <p>
	 * @param void
	 * @return void
	 */
	@SuppressLint("NewApi") //Dealt with in code
	public void addArrows(){

		boolean ok = true; //Overall correctness of all instructions

		for (int i = 0; i<counter; i++){

			String instructionText = instructionInput[i];
			String [] tokens = instructionText.split(",");
			Instruction inst = instructionArray[i]; //Box or End

			int goTo = -1;
			int branch = -1;

			boolean done = true; //correctness within instruction

			//Checking step numbers are correct, before adding arrow.
			if (instructionArray[i] instanceof Box){

				Box box = (Box) inst;

				try {
					goTo = Integer.parseInt(tokens[3]); //Both types of box will have a "GoTo"

					if (goTo >= counter || goTo < 0){
						String error = main.getString(R.string.error10, i);
						main.showMessage(error);
						done = false;
						ok = false;
					}
				}
				catch(Exception e){
					String error = main.getString(R.string.error10, i);
					main.showMessage(error);
					done = false;
					ok = false;
				}			

				if (!box.getType()){

					try {
						branch = Integer.parseInt(tokens[4]); //Only DEB will have a branch

						if (branch >= counter || branch < 0){
							String error = main.getString(R.string.error11, i);
							main.showMessage(error);
							done = false;
							ok = false;
						}
					}

					catch(Exception e){
						String error = main.getString(R.string.error11, i);
						main.showMessage(error);
						done = false;
						ok = false;
					}	
				}
			}

			if (done){

				Instruction succ = null;

				if (null != inst){

					succ = inst.getSucc();

					//Set ID of instruction
					if (Build.VERSION.SDK_INT >= 17) {
						inst.setId(View.generateViewId());
					}

					else {
						inst.setId(Util.generateViewId());
					}

					//Arrows are needed if goTo is not the immediate successor, except in case of END 
					if (goTo >= 0 && goTo != (i+1) && (inst.getSucc() instanceof Box || inst.getSucc() == null)){ //TODO - aaaa file
						Arrow arrow = new Arrow (game);

						if (Build.VERSION.SDK_INT >= 17) {
							arrow.setId(View.generateViewId());
						}

						else {
							arrow.setId(Util.generateViewId());
						}

						inst.setSucc(arrow);
						arrow.setSucc(succ);
						arrow.setPred(inst);

						if (null !=succ){
							succ.setPred(arrow);
						}

						Instruction goToIns = instructionArray[goTo];

						while (goToIns instanceof End){ //Arrows cannot branch to an end
							goToIns = goToIns.getPred();
						}

						arrow.setTo(goToIns);
						succ = arrow;
					}

					//Any branch requires an arrow, except those that branch to END
					if (branch >= 0 && instructionArray[branch] instanceof Box){

						Arrow arrow = new Arrow (game);
						arrow.setType();

						if (Build.VERSION.SDK_INT >= 17) {
							arrow.setId(View.generateViewId());
						}

						else {
							arrow.setId(Util.generateViewId());
						}

						Instruction branchIns = instructionArray[branch]; //Know this is box

						arrow.setTo(branchIns);
						inst.setSucc(arrow);
						arrow.setPred(inst);
						succ.setPred(inst);
						arrow.setSucc(succ);
					}
				}
			}
		}

		/*
		 * If overall correct:
		 * Reset game's first and last, clear the screen and update display
		 * 
		 */
		if (ok){

			game.setFirst(instructionArray[0]);
			game.setLastBox(instructionArray[counter-1]);
			Instruction last = instructionArray[counter-1];

			while (null != last){
				game.setLast(last);
				last = last.getSucc();
			}

			main.clearScreen();
			main.updateDisplay();
		}
	}
}
