package com.jeanpower.reggieproject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * MainActivity View 
 * <p>
 * Deals with all layout of static and dynamic elements<br>
 * Routes user interaction to correct methods in Game<br>
 * Instantiates Tutorial, as required.<br>
 * Instantiates Game<br>
 * <p>
 * @author Jean Power 2014
 */
public class MainActivity extends Activity implements View.OnClickListener, OnMenuItemClickListener, View.OnLongClickListener, View.OnTouchListener, View.OnDragListener {

	private Game game;
	private int[] registerColours;
	int[] registerIds;
	private ImageButton arrowButton;
	private ImageButton endButton;
	private ImageButton runButton;
	private ImageButton binButton;
	private ImageButton boxButton;
	private double theLineY;
	private int maxRegisters; // Duplication, but constant get is inefficient
	private double origX;
	private double origY;
	private int buttonWidth; // For arrow sizes, set when window gets focus
	private int buttonHeight;
	private boolean arrowHead; // If user dragged head, or tail of arrow
	private Instruction currentlyDragging; // The arrow that is currently being dragged
	private boolean draggingArrow;
	private boolean draggingBox;
	private Box currentlyIn;
	private ImageButton sizer;
	private final int MAX_DURATION = 200; //Sensitivity of either click, or start drag operation
	private long startClickTime;
	private float screenDensity;
	private boolean changingButton = false;
	private final int THRESHOLD = (int) (buttonWidth * screenDensity); //Sensitivity of drag operation.
	private boolean running = false; //For correct action of "Run" button
	private RelativeLayout container;
	private boolean deleteInstruction = false; //For correct action after drag ends
	private ArrayList<Instruction> instructionList;
	private int instructionCounter; //Iteration through list of instructions
	private Tutorial tutorial;
	private int standardMargin = 1;

	/**
	 * Called when activity is created.
	 * <p>
	 * Sets up constants (maximum registers, find action frame, etc)<br>
	 * Places invisible sizer on screen for button width/height constant<br>
	 * Adds registers to screen, with colours and action listeners<br>
	 * Adds static icons to screen with action listeners.
	 * <p>
	 * @param savedInstanceState - Bundle contains previous state information
	 * @return void
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		game = new Game(this);
		maxRegisters = game.getMaxReg();
		screenDensity = this.getResources().getDisplayMetrics().density;
		container = (RelativeLayout) findViewById(R.id.actionFrame);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		LinearLayout registerFrame = (LinearLayout) findViewById(R.id.register_frame);
		LinearLayout.LayoutParams regParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

		sizer = new ImageButton(getApplicationContext());
		sizer.setBackgroundColor(Color.TRANSPARENT);
		sizer.setImageResource(R.drawable.teal);
		sizer.setPadding(0, 0, 0, 0);
		container.addView(sizer);
		sizer.setVisibility(View.INVISIBLE);

		// To give black border
		regParams.setMargins(standardMargin, standardMargin, standardMargin, standardMargin);
		registerFrame.setBackgroundColor(Color.BLACK);

		// Array of colours, from color.xml
		registerColours = getResources().getIntArray(R.array.rainbow);

		// Array of the register names
		String[] registerNames = getResources().getStringArray(R.array.register_names);

		// Setting up array to hold register IDs
		registerIds = new int[maxRegisters];

		for (int i = 0; i < maxRegisters; i++) {

			int resID = getResources().getIdentifier(registerNames[i],"string", getPackageName());
			registerIds[i] = resID; // At position 0 is first register, etc

			// Adding to screen, setting colour
			Button regButton = (Button) findViewById(resID);
			regButton.setLayoutParams(regParams); // For wrap content
			regButton.setBackgroundColor(registerColours[i]);
			regButton.setOnClickListener(this);
			regButton.setOnLongClickListener(this);
		}

		this.setRegisters();

		// Add listeners to activity buttons.
		arrowButton = (ImageButton) findViewById(R.id.new_arrow_button);
		arrowButton.setOnClickListener(this);

		runButton = (ImageButton) findViewById(R.id.run_button);
		runButton.setOnClickListener(this);

		boxButton = (ImageButton) findViewById(R.id.new_box_button);
		boxButton.setOnClickListener(this);

		endButton = (ImageButton) findViewById(R.id.new_end_button);
		endButton.setOnClickListener(this);

		binButton = (ImageButton) findViewById(R.id.bin_clear_button);
		binButton.setOnClickListener(this);
		binButton.setOnDragListener(this);

		arrowButton.setVisibility(View.INVISIBLE);
		endButton.setVisibility(View.INVISIBLE);
		runButton.setVisibility(View.INVISIBLE);
		binButton.setVisibility(View.INVISIBLE);

		//Find if user has seen tutorial before
		SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);

		if (pref.getBoolean("tutorial", true)){
			tutorial = new Tutorial(this, game);
		}
	}

	/**
	 * Inflates the menu and adds items (Save/Load and Tutorial)
	 * <p>
	 * @param menu - Menu which has items added
	 * @return boolean
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_activity_actions, menu);

		MenuItem loadButton = menu.findItem(R.id.load_menu_button); 
		MenuItem tutorialButton = menu.findItem(R.id.tutorial_menu_button); 

		loadButton.setOnMenuItemClickListener(this);
		tutorialButton.setOnMenuItemClickListener(this);
		return true;
	}

	/**
	 * Sets layout constants
	 * <p>
	 * Finds the width and height to be used for arrows, and the Y axis measurement of the line
	 * Also sets above/below in Game for arrow placement.
	 * <p>
	 * @param void
	 * @return void
	 */
	public void setLayoutConstants() {

		if (buttonWidth <= 0) {
			buttonWidth = sizer.getWidth();
		}

		if (buttonHeight <= 0) {
			buttonHeight = sizer.getHeight();
		}

		if (theLineY <= 0) {
			View parent = (View) findViewById(R.id.theLine).getParent();
			theLineY = parent.getHeight() / 2;
		}

		game.setAboveBelow();
	}

	/**
	 * Gets the instruction list and passes to Thread to create instruction
	 * <p>
	 * Thread then calls itself to add next instruction, and so on
	 * <p>
	 * @param void
	 * @return void
	 */
	public void updateDisplay() {

		//this.clearArrows();
		instructionList = game.getInstructionList();
		this.setLayoutConstants();
		instructionCounter = 0;
		Instruction [] instructions = new Instruction[1]; //ASyncTask only accepts array.

		if (instructionList.size() > 0){

			//User can add arrows, ends, bin instructions and run program, if there is an instruction on screen
			arrowButton.setVisibility(View.VISIBLE);
			endButton.setVisibility(View.VISIBLE);
			runButton.setVisibility(View.VISIBLE);
			binButton.setVisibility(View.VISIBLE);
			instructions[0] = instructionList.get(instructionCounter);
			CreateInstruction ci = new CreateInstruction();
			ci.execute(instructions);
		}
	}

	/**
	 * Helper method to add final instruction to screen, on UI thread
	 * <p>
	 * @param ibutton -  ImageButton of the Instruction, with all parameters/text/drawables attached.
	 * @return void
	 */
	public void addToScreen(ImageButton ibutton){

		final ImageButton button = ibutton;

		button.setOnTouchListener(this);
		button.setOnDragListener(this);

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				removeInstruction(button.getId());
				container.addView(button);		
			}
		});
	}

	/**
	 * Update the colour of the instruction, as per the register colours, when clicked
	 * <p>
	 * @param ID - identity of instruction, and therefore on screen ImageButton
	 * @param register - new associated register
	 * @return void
	 */
	public void updateColour(int ID, int register) {

		TypedArray instructionIcons = getResources().obtainTypedArray(R.array.instruction_icons);
		ImageButton button = (ImageButton) findViewById(ID);
		button.setImageResource(instructionIcons.getResourceId(register, -1));
		instructionIcons.recycle();
	}

	/**
	 * Iterates through register buttons, getting data from Game class and setting
	 * <p>
	 * @param void
	 * @return void
	 */
	public void setRegisters() {

		for (int i = 0; i < maxRegisters; i++) {

			int resID = registerIds[i];
			Button b = (Button) findViewById(resID);
			String data = game.getRegData(i) + "";
			b.setText(data);
		}
	}

	/**
	 * Deals with clicks on registers and clicks on icons
	 * <p>
	 * Click on register increments register by 1.<br>
	 * Click on icon creates a new instruction<br>
	 * Click on "Run" causes game to run<br>
	 * Click on Bin/Clear clears the screen and registers.<br>
	 * <p>
	 * @param v - View, the clicked button
	 * @return void
	 */
	@Override
	public void onClick(View v) {

		int resid = v.getId();
		boolean done = false;

		for (int i = 0; i < maxRegisters; i++) {
			if (resid == registerIds[i]) {
				game.incrementReg(i);
				done = true;
				setRegisters();
			}
		}

		if (!done) { //If it was not a click on a register.

			switch (resid) {
			case R.id.new_arrow_button:
			case R.id.new_box_button:
			case R.id.new_end_button:
				game.newInstruction(resid);
				this.updateDisplay();
				break;

			case R.id.run_button:
				if (!running) {
					running = true;
					runButton.setImageResource(R.drawable.ic_stop);
					arrowButton.setVisibility(View.INVISIBLE); //Prevent users adding new instructions while game is running
					endButton.setVisibility(View.INVISIBLE);
					boxButton.setVisibility(View.INVISIBLE);
					binButton.setVisibility(View.INVISIBLE);
					game.runGame();
				}

				else {
					running = false;
					runButton.setImageResource(R.drawable.ic_run);
					arrowButton.setVisibility(View.VISIBLE);
					endButton.setVisibility(View.VISIBLE);
					boxButton.setVisibility(View.VISIBLE);
					binButton.setVisibility(View.VISIBLE);
					game.setCurrPos(null);
				}

				break;

			case R.id.bin_clear_button:
				if (!running) {
					game.clearAll();
				}
				break;

			default:
				break;
			}
		}
	}

	/**
	 * Clears the screen, resets the registers and icons
	 * <p>
	 * Allows for clearing of screen without losing Game data, for file loading
	 * <p>
	 * @param void
	 * @return void
	 */
	public void clearScreen(){

		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				int childCount = container.getChildCount();

				for (int i = 1; i < childCount; i++) {

					View child = container.getChildAt(1);
					container.removeView(child);
				}

				arrowButton.setVisibility(View.INVISIBLE);
				endButton.setVisibility(View.INVISIBLE);
				runButton.setVisibility(View.INVISIBLE);
				binButton.setVisibility(View.INVISIBLE);
				setRegisters();
			}
		});
	}

	/**
	 * Clears the screen of arrows
	 * <p>
	 * Helper method for laying out arrow instructions, as they impact each other's layout - 
	 * cannot have previous versions of arrows on screen
	 * <p>
	 * @param void
	 * @return void
	 */
	public void clearArrows(){

		int childCount = container.getChildCount();

		for (int i = 1; i < childCount; i++) {

			View child = container.getChildAt(1);

			if (game.getInstruction(child.getId()) instanceof Arrow){
				container.removeView(child);
			}
		}		
	}

	/**
	 * Removes one instruction
	 * <p>
	 * @param ID - int identity of instruction to be removed, therefore ID of ImageButton
	 * @return void
	 */
	public void removeInstruction(int ID){

		if (null != findViewById(ID)){
			View child = findViewById(ID);
			container.removeView(child);
		}
	}

	/**
	 * Takes long click on register button and zeros register
	 * <p>
	 * @param v - View that was clicked
	 * @return boolean
	 */
	@Override
	public boolean onLongClick(View v) {

		int resid = v.getId();

		for (int i = 0; i < maxRegisters; i++) {
			if (resid == registerIds[i]) {
				game.zeroReg(i);
				this.setRegisters();
			}
		}
		return true;
	}

	/**
	 * Deals with touch listeners
	 * <p>
	 * If short click, boxes update colour, arrows become branch/arrow, end moves back along instruction list<br>
	 * If long and user starts dragging, starts drag event
	 * <p>
	 * @param v - View that has been clicked
	 * @param me - MotionEvent of action on View
	 * @return boolean
	 */
	@Override
	public boolean onTouch(View v, MotionEvent me) {

		int resid = v.getId();
		Instruction instruction = game.getInstruction(resid);
		v.setBackgroundColor(Color.YELLOW);

		switch (me.getAction() & MotionEvent.ACTION_MASK) { //Motioneventcontains pointer data too - bitwise AND to leave just action

		case MotionEvent.ACTION_DOWN:
			draggingArrow = false;
			draggingBox = false;
			origX = me.getRawX();
			origY = me.getRawY();

			if (instruction instanceof Arrow){

				//Find view in relation to parent for accurate arrowhead/arrowtail choice
				View parent = v.getRootView();

				int[] viewLocation = new int[2]; 
				v.getLocationInWindow(viewLocation);

				int[] rootLocation = new int[2];
				parent.getLocationInWindow(rootLocation);

				int relativeLeft = viewLocation[0] - rootLocation[0];

				Arrow dragArrow = (Arrow) instruction;

				if ((origX < (relativeLeft + v.getWidth()/2) && dragArrow.getType()) || (origX > (relativeLeft + v.getWidth()/2) && !dragArrow.getType())) {
					arrowHead = true;
				}

				else {
					arrowHead = false;
				}
			}

			startClickTime = Calendar.getInstance().getTimeInMillis();
			break;

		case MotionEvent.ACTION_CANCEL:
			break;

		case MotionEvent.ACTION_UP:
			long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;

			if (clickDuration < MAX_DURATION) { //Just a click

				if (instruction instanceof Box) {
					game.updateInstruction(instruction);
				}

				else if (instruction instanceof End) {
					game.updateInstruction(instruction);
				}

				else if (instruction instanceof Arrow) {
					game.changeInstruction(instruction);
				}

				this.updateDisplay();
			}

			if (null != v){ //Reset visibility of view when drag ends if not in instruction.
				v.setVisibility(View.VISIBLE);
			}

			binButton.setImageResource(R.drawable.ic_clear);
			break;

		case MotionEvent.ACTION_MOVE:
			float newY = me.getRawY();

			if (Math.abs(newY - origY) > THRESHOLD && !draggingArrow && !draggingBox) {

				binButton.setImageResource(R.drawable.ic_bin);

				//Keep account if arrow or box is being dragged.
				if (instruction instanceof Arrow) {
					draggingArrow = true;
				}

				else if (instruction instanceof Box) {
					draggingBox = true;
				}

				currentlyDragging = instruction; //Keep account of instruction being dragged
				v.setVisibility(View.INVISIBLE);
				DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
				v.startDrag(null, shadowBuilder, v, 0);
			}

			break;

		default:
			break;
		}
		return true;
	}

	/**
	 * Deals with drag operation
	 * <p>
	 * When drag enters a view, if bin, deletes instruction. <br>
	 * If enters Box, and arrow is being dragged, changes head/tail of arrow as appropriate.<br>
	 * If dragging Box, changes to deb or inc.<br>
	 * <p>
	 * @param v - View that is being dragged
	 * @param de - DragEvent with drag/location information
	 * @return boolean
	 */
	@Override
	public boolean onDrag(View v, DragEvent de) {

		int resid = v.getId();
		Instruction instruction = null;

		//If not dropped into bin, get instruction.
		if (resid != R.id.bin_clear_button) {
			instruction = game.getInstruction(resid);
		}

		switch (de.getAction()) {

		case DragEvent.ACTION_DRAG_STARTED:
			break;

		case DragEvent.ACTION_DRAG_ENTERED:

			if (resid == R.id.bin_clear_button) {
				game.deleteInstruction(currentlyDragging);
				deleteInstruction = true; 
				this.showMessage(getString(R.string.deleted));
			}

			else if (instruction instanceof Box && currentlyDragging != null && draggingArrow) { //If an arrow enters a box.
				currentlyIn = (Box) instruction;
			}
			break;

		case DragEvent.ACTION_DRAG_EXITED:
			if (instruction instanceof Box && currentlyDragging != null) {
				currentlyIn = null;
			}
			break;

		case DragEvent.ACTION_DRAG_ENDED:
			if (!deleteInstruction){

				if (draggingBox) { //If dragging a Box, update to inc/deb

					float newY = v.getY();

					if ((newY - origY > THRESHOLD) || (origY - newY > THRESHOLD)) {
						game.changeInstruction(currentlyDragging); 
						this.updateDisplay();
					}
				}

				if (draggingArrow && null != currentlyIn){

					Arrow currentArrow = (Arrow) currentlyDragging;

					if (arrowHead){

						game.updateHead(currentArrow, currentlyIn);
					}

					else if (!arrowHead){

						game.updateTail(currentArrow, currentlyIn);
					}
					this.updateDisplay();
				}

				if (draggingArrow){
					View arrowView = findViewById(currentlyDragging.getId());
					arrowView.setBackgroundColor(Color.TRANSPARENT);
					this.updateDisplay();
				}
			}

			if (deleteInstruction){
				this.updateDisplay();		
			}

			currentlyDragging = null;
			draggingArrow = false;
			draggingBox = false;
			deleteInstruction = false;
			binButton.setImageResource(R.drawable.ic_clear);

			break;
		}
		return true;
	}

	/**Respond to the action bar's Up button - finishes main activity and goes back to option screen
	 * <p>
	 * @param item - clicked MenuItem
	 * @return boolean
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home:

			if (null != tutorial && null != tutorial.getDialog()){ //Ensures all dialogs are closed
				tutorial.getDialog().dismiss();	
			}
			Intent intent = new Intent(this, OptionScreen.class);
			startActivity(intent);
			this.finish();
		}
		return true;
	}

	/**Prevent leak of Dialog from tutorial when MainActivity is destroyed.
	 * <p>
	 * @param void
	 * @return void
	 */
	@Override
	public void onDestroy() {	
		super.onDestroy();

		if (null != tutorial && null != tutorial.getDialog()){ //Ensures all dialogs are closed
			tutorial.getDialog().dismiss();	
		}
	}

	/**
	 * Helper method reset the Run button back to "Run" as must be called on main UI thread<br>
	 * Also resets visibility of icons
	 * <p>
	 * @param void
	 * @return void
	 */
	public void resetRunButton(){

		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				running = false;
				runButton.setImageResource(R.drawable.ic_run);
				arrowButton.setVisibility(View.VISIBLE);
				endButton.setVisibility(View.VISIBLE);
				boxButton.setVisibility(View.VISIBLE);
				binButton.setVisibility(View.VISIBLE);
			}
		});
	}

	/**
	 * As Game iterates through instructions, method highlights these on screen
	 * <p>
	 * @param int - ID of instruction, int - associated register
	 * @return void
	 */
	public synchronized void updateInstructionDisplay(int ID, int reg) {

		final int identity = ID;
		final int register = reg;

		runOnUiThread(new Runnable() {
			@Override
			public void run() {

				setRegisters();
				ImageButton currentlyChanging = (ImageButton) findViewById(identity);

				if (!changingButton) {

					currentlyChanging.getDrawable().setAlpha(100);

					if (register >= 0){
						int regID = registerIds[register]; //Get View ID from array of register IDs
						Button register1 = (Button) findViewById(regID);
						register1.getBackground().setAlpha(100);
					}
					changingButton = true;
				}

				else if (changingButton) {

					currentlyChanging.getDrawable().setAlpha(255);

					if (register >= 0){
						int regID = registerIds[register];
						Button register2 = (Button) findViewById(regID);
						register2.getBackground().setAlpha(255);
					}
					changingButton = false;
				}
			}
		});
	}

	/**
	 * CreateInstruction anonymous inner hybrid View/Presenter class
	 * <p>
	 * Is given the first instruction, creates a thread and sets all display parameters for this instruction.<br>
	 * Passes back ImageButton with correct parameters to MainActivity<br>
	 * Interacts directly with Instruction to get information to create correct display parameters<br>
	 * Uses DrawArrow to create arrow Bitmaps<br>
	 * ASyncTask allows for threading - this class is a helper class, performing all the calculations in the background<br>
	 * <p>
	 */
	private class CreateInstruction extends AsyncTask<Instruction, Void, ImageButton>{

		/**
		 * Actions completed on background thread, stopping UI from freezing
		 * 
		 *  @param params - Array of one Instruction, to have all display parameters created
		 *  @return ImageButton - Instruction's onscreen ImageButton with display parameters
		 */
		@Override
		protected ImageButton doInBackground(Instruction... params) {

			TypedArray instructionIcons = getResources().obtainTypedArray(R.array.instruction_icons);

			Instruction inst = params[0]; 

			ImageButton button = new ImageButton(getApplicationContext());
			button.setPadding(0,0,0,0);
			int instructionID = inst.getId(); 
			button.setId(instructionID); //Button has same ID as instruction, connecting the two
			RelativeLayout.LayoutParams instructionParameters = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

			if (inst instanceof Box) {
				Box box = (Box) inst;
				Instruction prevInstruction = null;

				if (box.getPred() != null) {
					prevInstruction = box.getPred();
				}

				button.setBackgroundColor(Color.TRANSPARENT);
				button.setImageResource(instructionIcons.getResourceId(box.getRegister(), -1)); //Set colour to associated register

				if (box.getType()) // If Increment
				{
					instructionParameters.addRule(RelativeLayout.ABOVE,	R.id.theLine);
				}

				else {
					instructionParameters.addRule(RelativeLayout.BELOW, R.id.theLine);
				}

				if (prevInstruction == null) {
					instructionParameters.addRule(RelativeLayout.ALIGN_PARENT_LEFT); //First box always aligns to left of parent
				}

				else {
					// To add next to previous box
					while (prevInstruction instanceof Arrow || prevInstruction instanceof End) {
						prevInstruction = prevInstruction.getPred();
					}
					instructionParameters.addRule(RelativeLayout.RIGHT_OF, prevInstruction.getId()); // Right of the current instruction
				}

				//Margins required due
				instructionParameters.leftMargin = standardMargin;
				instructionParameters.topMargin = standardMargin;
				button.setLayoutParams(instructionParameters);
			}

			else if (inst instanceof Arrow) {
				Arrow arrow = (Arrow) inst;
				arrow.calculateSpaces(); //Span required of arrow
				button.setBackgroundColor(Color.TRANSPARENT);
				DrawArrow drawArrow = new DrawArrow(arrow.getSpaces(), arrow.getType(), buttonWidth, buttonHeight);
				//If pred is arrow/end, it does have an associated register (same as predecessor)
				drawArrow.setColours(registerColours[arrow.getPred().getRegister()], registerColours[arrow.getTo().getRegister()]);

				int marginTop = 0;

				Bitmap arrowPicture = drawArrow.getImage();
				button.setImageBitmap(arrowPicture);

				// Arrow left or right matches its predecessor box
				Instruction prevBox = arrow.getPred();

				while (prevBox instanceof Arrow || prevBox instanceof End) {
					prevBox = prevBox.getPred();
				}

				if (arrow.getType()) {
					instructionParameters.addRule(RelativeLayout.ALIGN_RIGHT, prevBox.getId());
				}

				else {
					instructionParameters.addRule(RelativeLayout.ALIGN_LEFT, prevBox.getId());
				}

				// To work out offset above/below the line
				List<Instruction> instructionBetween = null;

				if (arrow.getType()) {

					if (game.getAbove() >= 0) {
						marginTop = buttonHeight * 2;
					}

					else {
						marginTop = buttonHeight;
					}

					instructionBetween = game.getToFrom(arrow.getTo(), arrow.getPred().getId());
					int pointer = 0;
					Instruction currentPosition = instructionList.get(pointer);

					//Compares the list of instructions spanned by arrow to the list of instructions spanned by all other arrows.
					while (currentPosition != null && currentPosition.getId() != arrow.getId()) {

						if (currentPosition instanceof Arrow) {

							Arrow check = (Arrow) currentPosition;

							if (check.getType()) {

								List<Instruction> instructList = game.getToFrom(check.getTo(), check.getPred().getId());

								int sizeList = instructList.size();

								instructList.removeAll(instructionBetween);

								int newSizeList = instructList.size();

								// If there is any overlap in the lists, increase the margin.
								if (sizeList != newSizeList) {

									if (null != findViewById(check.getId()) && null != findViewById(check.getId()).getLayoutParams()){
										RelativeLayout.LayoutParams par = (RelativeLayout.LayoutParams) findViewById(check.getId()).getLayoutParams();
										marginTop = -par.topMargin + buttonHeight;
									}
								}
							}
						}
						pointer++;
						currentPosition = instructionList.get(pointer);
					}

					instructionParameters.topMargin = -marginTop;
				}

				else {

					if (game.getBelow() >= 0) {
						marginTop = buttonHeight;
					}

					else {
						marginTop = 0;
					}

					if (arrow.getTo().getSucc() instanceof Arrow){
						Arrow toSucc = (Arrow) arrow.getTo().getSucc();

						if (!toSucc.getType()){
							instructionBetween = game.getToFrom(arrow.getPred(), toSucc.getId());
						}
						else {
							instructionBetween = game.getToFrom(arrow.getPred(), arrow.getTo().getId());
						}
					}

					else {
						instructionBetween = game.getToFrom(arrow.getPred(), arrow.getTo().getId());
					}

					int pointer = 0;
					Instruction currentPosition = instructionList.get(pointer);

					//Compares the list of instructions spanned by arrow to the list of instructions spanned by all other arrows.
					while (currentPosition != null && currentPosition.getId() != arrow.getId()) {

						if (currentPosition instanceof Arrow) {

							Arrow check = (Arrow) currentPosition;

							if (!check.getType()) {

								List<Instruction> instructList = null;

								if (arrow.getTo().getSucc() instanceof Arrow){
									Arrow toSucc = (Arrow) arrow.getTo().getSucc();

									if (!toSucc.getType()){
										instructList = game.getToFrom(arrow.getPred(), toSucc.getId());
									}

									else {
										instructList = game.getToFrom(arrow.getPred(), arrow.getTo().getId());
									}
								}

								else {
									instructList = game.getToFrom(arrow.getPred(), arrow.getTo().getId());
								}

								//List<Instruction> instructList = game.getToFrom(check.getPred(), check.getTo().getId());
								int sizeList = instructList.size();

								instructList.removeAll(instructionBetween);

								int newSizeList = instructList.size();

								// If there is any overlap in the lists, increase the margin.
								if (sizeList != newSizeList) {

									if (null != findViewById(check.getId()) && null != findViewById(check.getId()).getLayoutParams()){

										RelativeLayout.LayoutParams par = (RelativeLayout.LayoutParams) findViewById(check.getId()).getLayoutParams();
										marginTop = par.topMargin + buttonHeight;
									}
								}
							}
						}
						pointer++;
						currentPosition = instructionList.get(pointer);
					}

					instructionParameters.topMargin = marginTop;
				}

				button.setLayoutParams(instructionParameters);
			}

			else if (inst instanceof End) {

				End end = (End) inst;
				button.setImageResource(R.drawable.in_end);
				button.setBackgroundColor(Color.TRANSPARENT);
				int topMargin;
				Instruction prev = end.getPred();

				while (prev instanceof Arrow || prev instanceof End) {
					prev = prev.getPred();
				}

				Box prevBox = (Box) prev;

				if (prevBox.getType()) {
					instructionParameters.addRule(RelativeLayout.ABOVE,	R.id.theLine);
					topMargin = - buttonHeight;

				} 
				else {
					instructionParameters.addRule(RelativeLayout.BELOW,	R.id.theLine);
					topMargin = 0;
				}

				instructionParameters.width = (int) (buttonWidth/1.5);
				instructionParameters.height = (int) (buttonHeight/1.5);
				instructionParameters.addRule(RelativeLayout.ALIGN_TOP, prev.getId());
				instructionParameters.addRule(RelativeLayout.ALIGN_RIGHT, prev.getId());
				instructionParameters.topMargin = topMargin;
				button.setLayoutParams(instructionParameters);
			}

			instructionIcons.recycle();
			return button;
		}

		/**
		 * Called directly after doInBackground is completed.<p>
		 * Passes the ImageButton to MainActivity<br>
		 * Creates a new AsyncTask with the next instruction<br>
		 *  @param result - ImageButton result of doInBackground
		 *  @return void
		 */
		protected void onPostExecute(ImageButton result) {

			addToScreen(result);
			instructionCounter++;

			if (instructionCounter<instructionList.size()){
				Instruction [] instructions = new Instruction[1];
				instructions[0] = instructionList.get(instructionCounter);

				CreateInstruction ci = new CreateInstruction();
				ci.execute(instructions);
			}
		}
	}

	/**
	 * Deals with clicks on Save/Load and Tutorial
	 * @param item - MenuItem clicked
	 * @return boolean
	 */
	@Override
	public boolean onMenuItemClick(MenuItem item) {

		switch (item.getItemId()) {

		case R.id.load_menu_button:
			game.saveLoadClick();
			break;

		case R.id.tutorial_menu_button:
			tutorial = new Tutorial(this, game);
			break;
		}
		return true;
	}

	/**
	 * Shows messages on screen to user
	 * @param message - String message
	 * @return void
	 */
	public void showMessage(String message){

		Context context = getApplicationContext();
		CharSequence text = message;
		int duration = Toast.LENGTH_LONG;
		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
	}

	/**
	 * Get ButtonHeight
	 * @return int buttonHeight
	 */
	public int getHeight(){
		this.setLayoutConstants();
		return buttonHeight;
	}

	/**
	 * Get ButtonWidth
	 * @return int buttonWidth
	 */
	public int getWidth(){
		this.setLayoutConstants();
		return buttonWidth;
	}
}