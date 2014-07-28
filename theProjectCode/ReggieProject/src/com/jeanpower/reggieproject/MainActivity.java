//View - handles UI components and events

package com.jeanpower.reggieproject;

import java.util.Calendar;
import java.util.List;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainActivity extends Activity implements View.OnClickListener,
View.OnLongClickListener, View.OnTouchListener, View.OnDragListener {

	private Game game;
	private int[] registerColours;
	private int[] registerIds;
	private ImageButton arrowButton;
	private ImageButton endButton;
	private ImageButton runButton;
	private ImageButton binButton;
	private boolean oneBox;
	private double theLineX;
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
	private static int glow;
	private View boxAbove;
	private View boxBelow;
	private Button regButton;
	private final int MAX_DURATION = 200;
	private long startClickTime;
	private float screenDensity;
	private boolean changingButton = false;
	private final int THRESHOLD = (int) (buttonWidth * screenDensity);
	private boolean running = false;
	private RelativeLayout container;
	private boolean deleteInstruction = false;


	/**
	 * Called when application is opened.
	 * <p>
	 * Sets the view to first screen, adds register buttons Adds listeners to
	 * activity buttons
	 * <p>
	 * 
	 * @return void
	 */
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		game = new Game(this);
		oneBox = false; // To track if user has added a Box instruction,
		// preventing arrow/run/end being called
		maxRegisters = game.getMaxReg();
		glow = Color.argb(120, 255, 255, 0);
		screenDensity = this.getResources().getDisplayMetrics().density;
		container = (RelativeLayout) findViewById(R.id.actionFrame);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		LinearLayout registerFrame = (LinearLayout) findViewById(R.id.register_frame);
		LinearLayout.LayoutParams regParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

		// To give black border
		regParams.setMargins(1, 1, 1, 1);
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
			regButton = (Button) findViewById(resID);
			regButton.setLayoutParams(regParams); // For wrap content
			regButton.setBackgroundColor(registerColours[i]);
			regButton.setOnClickListener(this);
			regButton.setOnLongClickListener(this);
		}

		this.setRegisters();

		// Add listeners to activity buttons.
		arrowButton = (ImageButton) findViewById(R.id.new_arrow_button);
		arrowButton.setOnClickListener(this);
		arrowButton.setClickable(false);

		runButton = (ImageButton) findViewById(R.id.run_button);
		runButton.setOnClickListener(this);
		runButton.setClickable(false);

		ImageButton boxButton = (ImageButton) findViewById(R.id.new_box_button);
		boxButton.setOnClickListener(this);

		endButton = (ImageButton) findViewById(R.id.new_end_button);
		endButton.setOnClickListener(this);
		endButton.setClickable(false);

		binButton = (ImageButton) findViewById(R.id.bin_clear_button);
		binButton.setClickable(false);
		binButton.setOnClickListener(this);
		binButton.setOnDragListener(this);

	}

	/**
	 * Inflates the menu and adds items
	 * <p>
	 * 
	 * @param Menu. Menu which has items added
	 * @return boolean
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		/*
		 * // Inflate the menu; this adds items to the action bar if it is
		 * present. MenuInflater inflater = getMenuInflater();
		 * inflater.inflate(R.menu.main_activity_actions, menu);
		 * 
		 * arrowButton = menu.findItem(R.id.new_arrow_button); runButton =
		 * menu.findItem(R.id.run_button); endButton =
		 * menu.findItem(R.id.new_end_button); arrowButton.setEnabled(false);
		 * runButton.setEnabled(false); endButton.setEnabled(false);
		 */
		return true;
	}

	/**
	 * To ignore orientation change, so UI is horizontal
	 * <p>
	 * 
	 * @return void
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	public void setLayoutConstants(List<Instruction> list) {

		if (list.size() > 0){

			Instruction curr = list.get(0);

			boolean doneAbove = false;
			boolean doneBelow = false;
			boxAbove = null;
			boxBelow = null;

			while (null != curr && !doneAbove) {

				if (curr instanceof Box) {

					Box b = (Box) curr;

					if (b.getType()) {

						boxAbove = findViewById(b.getId());
						doneAbove = true;
					}
				}

				curr = curr.getSucc();
			}

			curr = list.get(0);

			while (null != curr && !doneBelow) {

				if (curr instanceof Box) {

					Box b = (Box) curr;

					if (!b.getType()) {

						boxBelow = findViewById(b.getId());
						doneBelow = true;
					}
				}
				curr = curr.getSucc();
			}
		}
	}

	/**
	 * Updates action frame with instructions - boxes and arrows
	 * <p>
	 * Iterates through list of instructions, and adds to action frame as
	 * appropriate. To be efficient, only redraws from specific position
	 * Above/below the line, backwards/forwards arrows.
	 * <p>
	 * 
	 * @param Instruction The instruction that was updated
	 * @return void
	 */

	public void updateDisplay() {

		List<Instruction> instructionList = game.getInstructionList();
		TypedArray instructionIcons = getResources().obtainTypedArray(R.array.instruction_icons);
		this.setLayoutConstants(instructionList);

		for (Instruction inst : instructionList) {

			ImageButton button = new ImageButton(this);
			button.setOnTouchListener(this);
			button.setOnDragListener(this);
			int instructionID = inst.getId();
			button.setId(instructionID);
			this.removeInstruction(instructionID); // If already there, remove to allow redraw
	
			RelativeLayout.LayoutParams instructionParameters = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

			if (inst instanceof Box) {
				Box box = (Box) inst;
				Instruction prevInstruction = null;
				Box prevBox = null;

				if (box.getPred() != null) {
					prevInstruction = box.getPred();
				}

				button.setBackgroundResource(R.drawable.curvededge);

				button.setImageResource(instructionIcons.getResourceId(box.getRegister(), -1));

				if (box.getType()) // If Increment
				{
					instructionParameters.addRule(RelativeLayout.ABOVE,	R.id.theLine);
				}

				else {
					instructionParameters.addRule(RelativeLayout.BELOW, R.id.theLine);
				}

				if (prevInstruction == null) {
					instructionParameters.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				}

				else {
					// To add next to previous box
					while (prevInstruction instanceof Arrow || prevInstruction instanceof End) {

						prevInstruction = prevInstruction.getPred();
					}

					// If previous instruction is also a box, remove the
					// connecting arrow, to allow refresh
					if (prevInstruction instanceof Box) {
						prevBox = (Box) prevInstruction;
						//container.removeView(prevBox.getConnect());
					}

					instructionParameters.addRule(RelativeLayout.RIGHT_OF, prevBox.getId()); // Right of the current instruction
				}

				instructionParameters.leftMargin = 1;
				instructionParameters.topMargin = 1;
				button.setLayoutParams(instructionParameters);
				container.addView(button);

				/*
				 * //Redraw the connecting lines if (null != prevBox){
				 * 
				 * ConnectLine connLine = new ConnectLine(this,
				 * findViewById(prevBox.getId()), findViewById(instructionID),
				 * container); connLine.setLayoutParams(new
				 * RelativeLayout.LayoutParams(buttonWidth, buttonHeight));
				 * container.addView(connLine); prevBox.setConnect(connLine); }
				 */
			}

			else if (inst instanceof Arrow) {
				Arrow arrow = (Arrow) inst;

				button.setBackgroundColor(Color.TRANSPARENT);
				DrawArrow drawArrow = new DrawArrow(arrow, buttonWidth,buttonHeight);
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

					instructionParameters.addRule(RelativeLayout.ALIGN_RIGHT,
							prevBox.getId());
				}

				else {
					instructionParameters.addRule(RelativeLayout.ALIGN_LEFT,
							prevBox.getId());
				}

				// To work out offset above/below the line
				List<Instruction> instructionBetween = null;

				if (arrow.getType()) {

					if (boxAbove != null) {
						marginTop = buttonHeight * 2;
					}

					else {
						marginTop = buttonHeight;
					}

					// Log.d("MarginTop", marginTop + "");

					instructionBetween = game.getToFrom(arrow.getTo(), arrow.getPred().getId());
					int pointer = 0;
					Instruction currentPosition = instructionList.get(pointer);

					while (currentPosition != null&& currentPosition.getId() != arrow.getId()) {

						if (currentPosition instanceof Arrow) {

							Arrow check = (Arrow) currentPosition;

							if (check.getType()) {

								List<Instruction> instructList = game.getToFrom(check.getTo(), check.getPred().getId());

								// Log.d("I am on arrow ", arrow + "");
								// Log.d("This is arrow's list",
								// instructionBetween + "");
								// Log.d("I am checking arrow against", check +
								// "");
								// Log.d("This is the instruction list",
								// instructList + "");
								// Log.d("I am check arrow and I am",
								// check.getType() + "");

								int sizeList = instructList.size();

								instructList.removeAll(instructionBetween);

								int newSizeList = instructList.size();

								// If there is any overlap in the lists
								if (sizeList != newSizeList) {
									marginTop += buttonHeight;
								}
							}
						}

						pointer++;
						currentPosition = instructionList.get(pointer);
					}
					instructionParameters.topMargin = -marginTop;
				}

				else {
					if (boxBelow != null) {
						marginTop = buttonHeight;
					}

					else {
						marginTop = 0;
					}

					instructionBetween = game.getToFrom(arrow.getPred(), arrow.getTo().getId());
					int pointer = 0;
					Instruction currentPosition = instructionList.get(pointer);

					while (currentPosition != null
							&& currentPosition.getId() != arrow.getId()) {

						if (currentPosition instanceof Arrow) {

							Arrow check = (Arrow) currentPosition;

							if (!check.getType()) {

								List<Instruction> instructList = game.getToFrom(check.getPred(), check.getTo().getId());
								int sizeList = instructList.size();

								instructList.removeAll(instructionBetween);

								int newSizeList = instructList.size();
								/*
								Log.d("I am on arrow ", arrow + "");
								Log.d("This is arrow's list",instructionBetween + "");
								Log.d("I am checking arrow against", check + "");
								Log.d("This is the instruction list",
										instructList + "");
								Log.d("I am check arrow and I am",
										check.getType() + "");
								 */
								// If there is any overlap in the lists
								if (sizeList != newSizeList) {

									marginTop += buttonHeight;
								}
							}
						}

						pointer++;
						currentPosition = instructionList.get(pointer);
					}

					instructionParameters.topMargin = marginTop;
				}

				button.setLayoutParams(instructionParameters);
				container.addView(button);
				button.bringToFront();

			}

			else if (inst instanceof End) {

				End end = (End) inst;
				button.setImageResource(R.drawable.end);
				int topMargin;
				Instruction prev = end.getPred();

				while (prev instanceof Arrow || prev instanceof End) {
					prev = prev.getPred();
				}

				Box prevBox = (Box) prev;

				if (prevBox.getType()) {
					topMargin = -(int) (buttonHeight / 1.5);

				} else {
					topMargin = 1;

				}
				instructionParameters.width = buttonWidth / 2;
				instructionParameters.height = buttonHeight / 2;
				instructionParameters.addRule(RelativeLayout.ALIGN_TOP, prev.getId());
				instructionParameters.addRule(RelativeLayout.ALIGN_RIGHT, prev.getId());
				instructionParameters.topMargin = topMargin;
				button.setLayoutParams(instructionParameters);
				container.addView(button);
				button.bringToFront();
			}
		}
		/*
		 * //Redraw the line RelativeLayout.LayoutParams params = new
		 * RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
		 * RelativeLayout.LayoutParams.WRAP_CONTENT);
		 * //params.addRule(RelativeLayout.CENTER_IN_PARENT,
		 * RelativeLayout.TRUE); Resources r = getResources(); float px =
		 * TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2,
		 * r.getDisplayMetrics()); params.width = container.getWidth();
		 * Log.d("This is the container width", container.getWidth() + "");
		 * params.height = (int) px; View line = findViewById(R.id.theLine);
		 * line.setLayoutParams(params);
		 */
		instructionIcons.recycle();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);

		if (buttonWidth <= 0) {
			buttonWidth = regButton.getWidth();
		}

		if (buttonHeight <= 0) {
			buttonHeight = regButton.getHeight();
		}

		if (theLineX <= 0) {
			View parent = (View) findViewById(R.id.theLine).getParent();
			theLineX = parent.getHeight() / 2;
		}
	}

	/**
	 * Update the colour of the instruction, as per the register colours, when
	 * clicked
	 * <p>
	 * 
	 * @param Instruction
	 *            , int ID of instruction
	 * @return void
	 */

	public void updateColour(Instruction instruction) {

		TypedArray instructionIcons = getResources().obtainTypedArray(R.array.instruction_icons);

		ImageButton button = (ImageButton) findViewById(instruction.getId());
		Box inst = (Box) instruction;
		button.setImageResource(instructionIcons.getResourceId(inst.getRegister(), -1));
		instructionIcons.recycle();
	}

	/**
	 * Iterates through register buttons, getting their data from Game class.
	 * <p>
	 * 
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
	 * Takes click on register button and increments register
	 * <p>
	 * 
	 * @param View
	 *            . The button which has been clicked
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

		if (!done) {

			switch (resid) {

			case R.id.new_arrow_button:
			case R.id.new_box_button:
			case R.id.new_end_button:
				game.newInstruction(resid);
				this.updateDisplay();

				// Can only run game, add arrows/ends, when there is already a
				// box.
				if (!oneBox) {
					arrowButton.setClickable(true);
					endButton.setClickable(true);
					runButton.setClickable(true);
					binButton.setClickable(true);
					oneBox = true;
				}
				break;

			case R.id.run_button:

				if (!running) {
					running = true;
					runButton.setImageResource(R.drawable.ic_stop);
					game.runGame();
				}

				else {
					running = false;
					runButton.setImageResource(R.drawable.ic_run);
					game.setCurrPos(null);
				}

				break;

			case R.id.bin_clear_button:

				if (!running) {

					game.clearAll();
					arrowButton.setClickable(false);
					endButton.setClickable(false);
					runButton.setClickable(false);
					oneBox = false;
					this.clearScreen ();
				}
				break;

			default:
				break;
			}
		}
	}

	public void clearScreen(){

		int childCount = container.getChildCount();

		for (int i = 1; i < childCount; i++) {

			View child = container.getChildAt(1);
			container.removeView(child);
		}
	}
	
	
	public void removeInstruction(int ID){

		View child = findViewById(ID);
		container.removeView(child);
	}

	/**
	 * Takes long click on register button and zeros register
	 * <p>
	 * @param View Button which has been clicked
	 * @return boolean
	 */
	@Override
	public boolean onLongClick(View v) {

		int resid = v.getId();

		for (int i = 0; i < maxRegisters; i++) {
			if (resid == registerIds[i]) {
				game.zeroReg(i);
			}
		}
		return true;
	}

	@Override
	public boolean onTouch(View v, MotionEvent me) {

		int resid = v.getId();
		Instruction instruction = game.getInstruction(resid);

		switch (me.getAction() & MotionEvent.ACTION_MASK) { // Motionevent	contains pointer data too - bitwise and to leave just action

		case MotionEvent.ACTION_DOWN:
			draggingArrow = false;
			draggingBox = false;
			origX = me.getRawX();
			origY = me.getRawY();

			if (instruction instanceof Arrow){

				if ((origX < (v.getX() + v.getWidth()) && v.getY() < theLineX) || (origX > (v.getX() + v.getWidth()) && v.getY() >= theLineX)) {
					arrowHead = true;
					Log.d("The arrow head is", "true");
				}

				else {

					Log.d("The arrow head is", "false");
					arrowHead = false;
				}
			}	

			startClickTime = Calendar.getInstance().getTimeInMillis();
			break;

		case MotionEvent.ACTION_CANCEL:
			break;

		case MotionEvent.ACTION_UP:
			long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;

			Log.d("clickDuration", clickDuration + "");
			if (clickDuration < MAX_DURATION) {

				if (instruction instanceof Box) {

					game.updateInstruction(instruction);
					this.updateColour(instruction);
				}

				else if (instruction instanceof End) {
					game.updateInstruction(instruction);
					this.updateDisplay();
				}

				else if (instruction instanceof Arrow) {
					game.changeInstruction(instruction);
					this.updateDisplay();
				}
			}
			
			binButton.setImageResource(R.drawable.ic_clear);
			
			break;

		case MotionEvent.ACTION_MOVE:

			float newY = me.getRawY();

			if (Math.abs(newY - origY) > THRESHOLD && !draggingArrow
					&& !draggingBox) {

				binButton.setImageResource(R.drawable.ic_bin);

				if (instruction instanceof Arrow) {
					draggingArrow = true;
					v.setBackgroundColor(glow);
				}

				else if (instruction instanceof Box) {
					draggingBox = true;
					v.setBackgroundColor(glow);
				}

				currentlyDragging = instruction;
				DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v); // TODO - 2 different shadow builders?
				v.startDrag(null, shadowBuilder, v, 0);
			}

			break;

		default:
			break;
		}

		return true;
	}

	@Override
	public boolean onDrag(View v, DragEvent de) {

		int resid = v.getId();
		Instruction instruction = null;

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

				Context context = getApplicationContext();
				CharSequence text = "Instruction deleted";
				int duration = Toast.LENGTH_SHORT;

				Toast toast = Toast.makeText(context, text, duration);
				toast.show();
			}

			else if (instruction instanceof Box && currentlyDragging != null && draggingArrow) {

				currentlyIn = (Box) instruction;
				Arrow currentArrow = (Arrow) currentlyDragging;

				if (arrowHead){

					if (currentlyIn.getId() == currentArrow.getTo().getId()){
						Context context = getApplicationContext();
						CharSequence text = "I'm already pointing here!";
						int duration = Toast.LENGTH_SHORT;

						Toast toast = Toast.makeText(context, text, duration);
						toast.show();
					}

					else if (!currentArrow.getType() && currentArrow.getPred().getId() == currentlyIn.getId()){

						Context context = getApplicationContext();
						CharSequence text = "I can't branch to the same instruction!";
						int duration = Toast.LENGTH_SHORT;

						Toast toast = Toast.makeText(context, text, duration);
						toast.show();
					}

					else {
						game.updateHead(currentArrow, currentlyIn);
					}
				}

				else if (!arrowHead){

					if (currentlyIn.getId() == currentlyDragging.getPred().getId()){

						Context context = getApplicationContext();
						CharSequence text = "I'm already coming from here!";
						int duration = Toast.LENGTH_SHORT;

						Toast toast = Toast.makeText(context, text, duration);
						toast.show();
					}

					else if (!currentArrow.getType() && currentArrow.getTo().getId() == currentlyIn.getId()){

						Context context = getApplicationContext();
						CharSequence text = "I can't branch to the same instruction!";
						int duration = Toast.LENGTH_SHORT;

						Toast toast = Toast.makeText(context, text, duration);
						toast.show();
					}

					else {
						game.updateTail(currentArrow, currentlyIn);
					}
				}
			}

			break;

		case DragEvent.ACTION_DRAG_EXITED:
			if (instruction instanceof Box && currentlyDragging != null) {
				currentlyIn = null;
			}
			break;

		case DragEvent.ACTION_DRAG_ENDED:
			if (!deleteInstruction){
				if (draggingBox) {

					float newY = v.getY();

					if ((newY - origY > THRESHOLD) || (origY - newY > THRESHOLD)) {
						game.changeInstruction(currentlyDragging);
						this.updateDisplay();
					}
				}

				if (draggingArrow){
					View arrowview = findViewById(currentlyDragging.getId());
					arrowview.setBackgroundColor(Color.TRANSPARENT);
				}
			}
			
			binButton.setImageResource(R.drawable.ic_clear);
			currentlyDragging = null;
			draggingArrow = false;
			draggingBox = false;
			deleteInstruction = false;
			this.updateDisplay();

			break;
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		
	    switch (item.getItemId()) {
	    // Respond to the action bar's Up/Home button
	    case android.R.id.home:
	        NavUtils.navigateUpFromSameTask(this);
	        return true;
	    }
	    return super.onOptionsItemSelected(item);

		/*
		 * int resaid = item.getItemId();
		 * 
		 * if (resid == R.id.new_arrow_button || resid == R.id.new_box_button ||
		 * resid == R.id.new_end_button) { game.newInstruction(resid);
		 * this.updateDisplay();
		 * 
		 * // Can only run game, add arrows/ends, when there is already a box.
		 * if (!oneBox) { arrowButton.setEnabled(true);
		 * runButton.setEnabled(true); endButton.setEnabled(true); oneBox =
		 * true; } }
		 * 
		 * else if (resid == R.id.run_button) { game.runGame(); }
		 */

		//return true;
	}

	public void resetRunButton(){
		runButton.setImageResource(R.drawable.ic_run);
	}
	
	public synchronized void updateInstructionDisplay(Instruction ins) {

		final Instruction i = ins;

		runOnUiThread(new Runnable() {
			@Override
			public void run() {

				setRegisters();
				ImageButton currentlyChanging = (ImageButton) findViewById(i.getId());

				if (!changingButton) {

					if (i instanceof Box) {
						Box box = (Box) i;
						currentlyChanging.setBackgroundResource(R.drawable.curvededgecolor);

						Button register = (Button) findViewById(registerIds[box.getRegister()]);
						register.getBackground().setAlpha(128);
					}

					else if (i instanceof Arrow) {
						Arrow arrow = (Arrow) i;
						currentlyChanging.setBackgroundColor(glow);
						ImageButton goTo = (ImageButton) findViewById(arrow.getTo().getId());
						goTo.getBackground().setAlpha(128);
					}

					changingButton = true;
				}

				else if (changingButton) {

					if (i instanceof Box) {
						Box box = (Box) i;
						currentlyChanging.setBackgroundResource(R.drawable.curvededge);
						Button register = (Button) findViewById(registerIds[box.getRegister()]);
						register.getBackground().setAlpha(255);
					}

					else if (i instanceof Arrow) {
						Arrow arrow = (Arrow) i;
						currentlyChanging.setBackgroundColor(Color.TRANSPARENT);
						ImageButton goTo = (ImageButton) findViewById(arrow.getTo().getId());
						goTo.getBackground().setAlpha(255);
					}

					changingButton = false;
				}
			}
		});

	}
}
