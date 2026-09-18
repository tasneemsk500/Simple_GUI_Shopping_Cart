/* Name: Tasneem Shahnewaz Khan
Course: CNT 4714 – Fall 2026
Assignment title: Project 1 – An Event-driven Enterprise Simulation
Date: Sunday September 13, 2026
*/

import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.io.BufferedReader;


//*************************************************************************************************************
public class completeGUI extends JFrame 
{
	//static variables to hold frame dimensions in pixels
	private static final int WIDTH = 800;
	//private static final int HEIGHT = 230; //use for FlowLayout for buttons
	private static final int HEIGHT = 650; //use for GridLayouut for buttons  was 350
	
	private JLabel blankLabel, idLabel, qtyLabel, itemLabel, totalLabel, cartLabel, controlsLabel;
	private JButton blankButton, processB, confirmB, deleteB, finishB, newB, exitB;
	private JTextField blankTextField, blankTextFields, idTextField, qtyTextField, itemTextField, totalTextField;
	
	//declare reference variables for event handlers - one for each handler required - six in this case
	private ProcessButtonHandler processbHandler;
	private ConfirmButtonHandler confirmbHandler;
	private DeleteButtonHandler deletebHandler;
	private FinishButtonHandler finishbHandler;
	private NewButtonHandler newbHandler;
	private ExitButtonHandler exitbHandler;	
	
	
	//more static variables for formatting currency, percentages, and decimal values
	static NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();
	static NumberFormat percentFormatter = NumberFormat.getPercentInstance();
	static DecimalFormat decimalFormatter = (DecimalFormat) percentFormatter;
	
	static String [] itemIDArray;
	static String [] itemTittleArray;
	static String [] itemInStockArray;
	static double [] itemPriceArray;
	static int    [] itemQuantityArray;
	static double [] itemDiscountArray;
	static double [] itemSubtotalArray;
	static int    [] itemFoundInFileAtRowArray;
	
	static String itemID = "", itemTitle = "", outputStr = "", maxArraySizeStr = "",
			itemPricesStr = "", itemInStock = "", itemQuantityStr = "", itemSubtotalStr = "", itemDiscountStr = "",
			taxRateStr, discountRateStr, orderSubtotalStr;
	
	static double itemPrice = 0.0, itemSubtotal = 0.0, orderSubtotal = 0.0, orderTotal = 0.0,
			itemDiscount = 0, orderTaxAmount;
	
	static int itemQuantity = 0, itemCount = 0, itemQtyOnHand = 0, maxArraySize = 0, fileRowCounter = 0; 
	
	final static double TAX_RATE = 0.060,
						DISCOUNT_FOR_05 = 0.10,
						DISCOUNT_FOR_10 = 0.15,
						DISCOUNT_FOR_15 = 0.20;
	
	String inputFileName = "inventory.csv";
	String outputFileName = "transactions.csv";
	
	
	/////////////old variables
	static int MAXITEMS = 5;
	
//	static String itemIDFromFile;
//	static int requestedQuantity;
//	static String description;
//	static int qtyOnHand;
//	static double unitPrice;
//	
//	static double totalPrice = 0.0;
//	static double totalUnitPrice;
//	static double[] totalPrices = new double[MAXITEMS];
//	
//	static String cartLine;
	
	//define arrays for holding items in the cart
	private JTextField[] cartLineArray;
	
	//define additional variables as needed
//	static int itemCount = 0;
	
	
	//*********************************************************************************************************
	public completeGUI() //constructor for GUI
	{ 
		setTitle("Nile.Com - Fall 2026"); //set the title of the frame
		setSize(WIDTH, HEIGHT); //set the frame size
		
		//Define some colours to use
		Color light_blue = new Color(51,204,255);   //was 51,153,255 - slightly darker light blue
		Color light_green = new Color(0,255,51);   //for future use
		
		//Define the size of the arrays
		maxArraySize = MAXITEMS;
		itemIDArray = new String[maxArraySize];
		itemTittleArray = new String[maxArraySize];
		itemInStockArray = new String[maxArraySize];
		itemPriceArray = new double[maxArraySize];
		itemQuantityArray = new int[maxArraySize];
		itemDiscountArray = new double[maxArraySize];
		itemSubtotalArray = new double[maxArraySize];
		itemFoundInFileAtRowArray = new int[maxArraySize];
		
		//instantiate JLabel objects
		blankButton = new JButton(" ");
		blankLabel = new JLabel(" ", SwingConstants.RIGHT);
		
		idLabel = new JLabel("Enter item ID for Item #" + (itemCount + 1) + ":", SwingConstants.RIGHT);
		qtyLabel = new JLabel("Enter quantity for Item #" + (itemCount + 1) + ":", SwingConstants.RIGHT);
		itemLabel = new JLabel("Details for Item #" + (itemCount + 1) + ":", SwingConstants.RIGHT);
		totalLabel = new JLabel("Current Subtotal for " + itemCount + " item(s):", SwingConstants.RIGHT);
		cartLabel = new JLabel("Your Shopping Cart Is Currently Empty", SwingConstants.CENTER);
		controlsLabel = new JLabel(" USER CONTROLS ", SwingConstants.RIGHT);
		

		//instantiate JTextField objects 
		blankTextField = new JTextField();
		blankTextFields = new JTextField();
		idTextField = new JTextField();
		qtyTextField = new JTextField();
		itemTextField = new JTextField();
		totalTextField = new JTextField();
		
		cartLineArray = new JTextField[MAXITEMS];
		for (int counter = 0; counter < MAXITEMS; counter++) 
		{
		  cartLineArray[counter] = new JTextField();
		}
		
		//instantiate buttons and register handlers
		processB = new JButton("Search For Item #" + (itemCount + 1));
		processbHandler = new ProcessButtonHandler();
		processB.addActionListener(processbHandler);
		
		confirmB = new JButton("Add Item #" + (itemCount + 1) + " To Cart");
		confirmbHandler = new ConfirmButtonHandler();
		confirmB.addActionListener(confirmbHandler);
		
		deleteB = new JButton("Delete Last Item From Cart");
		deletebHandler = new DeleteButtonHandler();
		deleteB.addActionListener(deletebHandler);
		
		finishB = new JButton("Check Out");
		finishbHandler = new FinishButtonHandler();
		finishB.addActionListener(finishbHandler);
		
		newB = new JButton("Empty Cart - Start A New Order");
		newbHandler = new NewButtonHandler();
		newB.addActionListener(newbHandler);
		
		exitB = new JButton("Exit (Close App)");
		exitbHandler = new ExitButtonHandler();
		exitB.addActionListener(exitbHandler);
		
		
		//initial settings for buttons, fields
		confirmB.setEnabled(false); //disable confirm until calculation complete
		deleteB.setEnabled(false);
		finishB.setEnabled(false); // disable finish until confirm complete
		itemTextField.setEnabled(false);
		totalTextField.setEnabled(false);
		blankTextField.setEnabled(false);
		blankTextField.setBackground(Color.DARK_GRAY);
		blankTextField.setVisible(false);
		blankButton.setBackground(Color.DARK_GRAY);
		blankButton.setVisible(false);
		blankTextFields.setEditable(false);
		blankTextFields.setBackground(Color.DARK_GRAY);
		blankTextFields.setVisible(false);
		
		
		//Define a content pane to hold everything
		Container pane = getContentPane();  // get a content pane for the frame
		
		//Create the grid layouts for the layout -- grid layout specs (#rows, #columns, horizontal space, vertical space)
		GridLayout grid6by2 = new GridLayout(6, 2, 8, 4);
		GridLayout grid7by1 = new GridLayout(7, 1, 8, 4);
		//try other arrangements
		//GridLayout grid5by2 = new GridLayout(5,2,8,6);  //grid layout for buttons
		//GridLayout grid1by1 = new GridLayout(1,1,2,2);
		//GridLayout grid6by2x = new GridLayout(6,2,2,2);
		//GridLayout grid5by1 = new GridLayout(5,1,2,2);  // for cart
		
		//create panels - using a border layout with north, center, and south panels
		//these panels will go inside the main container (pane)
		JPanel northPanel = new JPanel();
		JPanel centerPanel = new JPanel();
		JPanel southPanel = new JPanel();
		
		//set layouts for panels
		//to configure the panels into various grid size
		northPanel.setLayout(grid6by2);
		centerPanel.setLayout(grid7by1);  //was grid1by1
		southPanel.setLayout(grid6by2);
		
		//add panels to content pane using BorderLayout
		pane.add(northPanel, BorderLayout.NORTH);
		pane.add(centerPanel, BorderLayout.CENTER);
		pane.add(southPanel, BorderLayout.SOUTH);
		
		
		//style panes
		pane.setBackground(Color.DARK_GRAY);
		northPanel.setBackground(Color.DARK_GRAY);
		northPanel.setForeground(Color.YELLOW); //set text color
		//southPanel.setBackground(Color.GREEN);
		centerPanel.setBackground(Color.BLACK);
		southPanel.setBackground(Color.DARK_GRAY);
		//southPanel.setBackground(light_blue);
		Font font = new Font("Tahoma", Font.PLAIN, 30);
		northPanel.setFont(new java.awt.Font("Tahoma", 2, 60));
		
		centerFrame(WIDTH, HEIGHT); //call method to center frame on screen
		
		
		//NOTE: Items are added to a grid layout in a row major fashion, i.e left to right, top to bottom
		
		//add labels to panel
		//NORTH PANEL
		northPanel.add(blankLabel);
		northPanel.add(blankTextField);
		idLabel.setForeground(Color.YELLOW);
		northPanel.add(idLabel);
		northPanel.add(idTextField);
		qtyLabel.setForeground(Color.YELLOW);
		northPanel.add(qtyLabel);
		northPanel.add(qtyTextField);
		
		//itemLabel.setForeground(Color.RED);
		itemLabel.setFont(new Font("Calibri", Font.BOLD, 14));
		itemLabel.setForeground(light_blue);
		itemTextField.setBackground(Color.LIGHT_GRAY);
		itemTextField.setDisabledTextColor(Color.BLACK);
		totalTextField.setBackground(Color.LIGHT_GRAY);
		totalTextField.setDisabledTextColor(Color.BLACK);
		
		northPanel.add(itemLabel);
		northPanel.add(itemTextField);
		northPanel.add(totalLabel);
		northPanel.add(totalTextField);
		 
		totalLabel.setFont(new Font("Calibri", Font.BOLD, 14));
		totalLabel.setForeground(light_blue);
		
		
		//CENTER PANEL
		cartLabel.setForeground(Color.RED);
		cartLabel.setFont(new Font("Calibri", Font.BOLD, 18));
		centerPanel.add(cartLabel);
		cartLabel.setHorizontalAlignment(JLabel.CENTER);
		
		
		for (int counter = 0; counter < MAXITEMS; counter++) 
		{
			cartLineArray[counter].setEnabled(false);
			cartLineArray[counter].setBackground(Color.WHITE); 
			cartLineArray[counter].setDisabledTextColor(Color.BLACK);
			centerPanel.add(cartLineArray[counter]);
		}
		
		//SOUTH PANEL
		
		//add buttons to panel
		//southPanel.add(blankLabel); southPanel.add(blankLabel);
		//southPanel.add(blankLabel); southPanel.add(blankLabel);
		//southPanel.add(blankTextFields); southPanel.add(blankTextFields);
		//southPanel.add(blankButton);
		//southPanel.add(blankButton);
		//southPanel.add(blankButton);
		//southPanel.add(blankTextFields);
		
		
		controlsLabel.setForeground(light_green);
		controlsLabel.setFont(new Font("Calibri", Font.BOLD, 25));
		southPanel.add(controlsLabel);
		controlsLabel.setHorizontalAlignment(JLabel.CENTER);
		controlsLabel.setVerticalAlignment(JLabel.CENTER);
		southPanel.add(blankButton);
		
		
		//Style the buttons ininially
		processB.setBackground(Color.WHITE);
		confirmB.setBackground(Color.LIGHT_GRAY);
		deleteB.setBackground(Color.LIGHT_GRAY);
		finishB.setBackground(Color.LIGHT_GRAY);
		newB.setBackground(Color.WHITE);
		exitB.setBackground(Color.WHITE);	
		
		
		southPanel.add(processB);
		southPanel.add(confirmB);
		southPanel.add(deleteB);
		southPanel.add(finishB);
		southPanel.add(newB);
		southPanel.add(exitB);
		//southPanel.add(blankButton); southPanel.add(blankButton);
		
		
	} //end constructor
		
	//*********************************************************************************************************
	public void centerFrame(int frameWidth, int frameHeight)
	{
		// create a Toolkit object
		Toolkit aToolkit = Toolkit.getDefaultToolkit();
		
		//create a Dimension object with user screen information
		Dimension screen = aToolkit.getScreenSize();
		
		//assign x, y position of upper-left corner of frame
		int xPositionOfFrame = (screen.width - frameWidth) / 2;
		int yPositionOfFrame = (screen.height - frameHeight) / 2;
		
		//method to center frame on user's screen
		setBounds(xPositionOfFrame, yPositionOfFrame, frameWidth, frameHeight);
		
	} // end method
	
	//*********************************************************************************************************
	public void setButtonStatus(JButton btn, boolean enabled)
	{
		//change the status to either disable or active
		btn.setEnabled(enabled);
		
		//change the colour accordingly
		if(enabled)
		{
			btn.setBackground(Color.WHITE);
		}
		else 
		{
			btn.setBackground(Color.LIGHT_GRAY);
		}
		
	} // end method
	
	//*********************************************************************************************************
	private class ProcessButtonHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			//set class variables
			File inputFile = new File(inputFileName); // the input file
			FileReader inputFileReader = null;
			BufferedReader inputBufReader = null;
			Scanner aScanner = null; //Scanner object
			String inventoryLine; //inbound line
			boolean found = false;
			
			//Read the two input values into the GUI
			String response1 = idTextField.getText();
			String response2 = qtyTextField.getText();
//			requestedQuantity = Integer.parseInt(response2);
			
			boolean inStock;	
//			int discountPercent;

			
			System.out.println("The Find Item Button Was Clicked...");
			
			try {
				//read the two input values from the GUI
//				response1 = idTextField.getText();
//				response2 = qtyTextField.getText();
				itemQuantity = Integer.parseInt(response2);
				
				inputFileReader = new FileReader(inputFile);
				inputBufReader = new BufferedReader(inputFileReader);
				
				//System.out.println("Search Item is: " + searchItem);
				
				inventoryLine = inputBufReader.readLine(); //read from file
				fileRowCounter = 0; // track the row we're at
				
				//search for the item in the inventory.csv file
				whileloop: while(inventoryLine != null) {
					//Increment
					fileRowCounter++;
					
					aScanner = new Scanner(inventoryLine).useDelimiter("\\s*,\\s*");
					
					//Read from files
					itemID = aScanner.next();
					itemTitle = aScanner.next();
					inStock = Boolean.parseBoolean(aScanner.next());
					itemQtyOnHand = aScanner.nextInt();
					itemPrice = aScanner.nextDouble();
					
					//if found
					if(itemID.equals(response1))
					{
//						System.out.println("FOUND IT!!");
						
						
						if(!inStock) //check if in stock
						{
							JOptionPane.showMessageDialog(null, "Sorry... that item is out of stock, please try another item", "Nile Dot Com - Error", JOptionPane.ERROR_MESSAGE);
							
							//Clear the id field and quantity field
							idTextField.setText("");
							qtyTextField.setText("");
							
						}
						else if (itemQuantity > itemQtyOnHand) //check if it's  in the range
						{
							JOptionPane.showMessageDialog(null, "Insufficient stock. Only " + itemQtyOnHand + " on hand.  Please reduce the quantity.", "Nile Dot Com - Error", JOptionPane.ERROR_MESSAGE);
							
							//Clear only the quantity
							qtyTextField.setText("");
						}
						else
						{
							////North Panel
							//Check for discount
							itemDiscount = getDiscountPercent(itemQuantity);
							
							//calculate the final cost
							itemSubtotal = itemQuantity * itemPrice * (1.0 - itemDiscount);
							
							//Set up the details
							//String details = itemID + " " + itemTitle + " $" + String.format("%.2f", unitPrice) + " " + requestedQuantity + " " + discountPercent + "% $" + String.format("%.2f", totalUnitPrice);
							String details = itemID + " " + itemTitle + " "
									+ currencyFormatter.format(itemPrice) + " "
									+ itemQuantity + " "
									+ percentFormatter.format(itemDiscount) + " "
									+ currencyFormatter.format(itemSubtotal);
							
							//update the text label and field
							itemLabel.setText("Details for Item #" + (itemCount + 1) + ":");
							itemTextField.setText(details);
							
							////South Panel
							//update the button
							setButtonStatus(processB, false);
							setButtonStatus(confirmB, true);
						}
						
						
						found = true;
						break whileloop;
					}
					
					//if not found
					else {
						inventoryLine = inputBufReader.readLine(); // read the next line from file
					}
				} // end while
				if (found == false)
				{
					System.out.println("Search Item Not In File!");
					
					//Open dialog box
					JOptionPane.showMessageDialog(null, "item ID " + response1 + " not in file", "Nile Dot Com - Error", JOptionPane.ERROR_MESSAGE);
					
					//Clear the id field and quantity field
					idTextField.setText("");
					qtyTextField.setText("");
				}
				
			} // end try
			
			catch(FileNotFoundException fileNotFoundExeption) { //file not found exception
				JOptionPane.showMessageDialog(null, "Error: File not found", "Error", JOptionPane.ERROR_MESSAGE);
			} // end catch
			
			catch(IOException ioException) { //IO exception
				JOptionPane.showMessageDialog(null, "Error: Problem reading from file", "Error", JOptionPane.ERROR_MESSAGE);
			} // end catch
			
			catch (NumberFormatException numberFormatExeption) {
				
				// //number format exception
				System.out.println("Error: '" + response2 + "' is not a valid integer.");
            } // end catch
			
		}//end method
		
		//the discount function
		private double getDiscountPercent(int qty)
		{
				if(qty >= 15)
					return DISCOUNT_FOR_15;
				
				if(qty >=10)
					return  DISCOUNT_FOR_10;
				
				if(qty >= 5)
					return DISCOUNT_FOR_05;
				
				return 0.0;
		}
		
	} // end class
	
	//*********************************************************************************************************
	private class ConfirmButtonHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			System.out.println("The Add Item To Cart Button Was Clicked...");
//			
//			//update the unaffected text field, buttons etc
//			totalPrice += totalUnitPrice;
//			totalTextField.setText(totalPrice + "");
//			
//			
//			//check if itemCount reached max reaches the max
//			if(itemCount >= MAXITEMS)
//			{
//				////North Panel
//				idLabel.setText("Enter item ID for Item #" + (itemCount + 1) + ":");
//				qtyLabel.setText("Enter quantity for Item #" + (itemCount + 1) + ":");
//				
//				idTextField.setEditable(false);
//				idTextField.setVisible(false);
//				
//				qtyTextField.setEditable(false);
//				qtyTextField.setVisible(false);
//				
//				//buttons
//				setButtonStatus(processB, false);
//				setButtonStatus(confirmB, false);
//			}
//			else
//			{
//				idTextField.setText("");
//				qtyTextField.setText("");
//			}
//			
//			//
//			
//			////Update the center panel
//			//update the cartlineString
//			cartLine = "Item " + (itemCount + 1) + " - SKU: " + itemIDFromFile + ", Desc: " + description + ", Price Ea. $" + unitPrice + ", Qty: " + requestedQuantity + ", Total: $" + totalUnitPrice;
//			cartLineArray[itemCount].setText(cartLine);
			
			//copy the single item onto the cart's array
			itemIDArray[itemCount] = itemID;
			itemTittleArray[itemCount] = itemTitle;
			itemInStockArray[itemCount] = "true";
			itemPriceArray[itemCount] = itemPrice;
			itemQuantityArray[itemCount] = itemQuantity;
			itemDiscountArray[itemCount] = itemDiscount;
			itemSubtotalArray[itemCount] = itemSubtotal;
			itemFoundInFileAtRowArray[itemCount] = fileRowCounter;
			
            ////North Panel
			
			totalLabel.setText("Current Subtotal for " + (itemCount + 1) + " item(s):");
			orderSubtotal += itemSubtotalArray[itemCount];
			totalTextField.setText(currencyFormatter.format(orderSubtotal));
			
			//totalPrices[itemCount] = totalPrice;
			
			
			////Center Panel
			cartLabel.setText("Your Shopping Cart Currently Contains " + (itemCount + 1) + " Item(s)");
//			cartLine = "Item " + (itemCount + 1) + " - SKU: " + itemIDFromFile + ", Desc: " + description + ", Price Ea. $" + String.format("%.2f", unitPrice) + ", Qty: " + requestedQuantity + ", Total: $" + String.format("%.2f", totalUnitPrice);
//			cartLineArray[itemCount].setText(cartLine);
			String cartLine = "Item " + (itemCount + 1) + " - SKU: " + itemIDArray[itemCount]
					+ ", Desc: " + itemTittleArray[itemCount]
					+ ", Price Ea. " + currencyFormatter.format(itemPriceArray[itemCount])
					+ ", Qty: " + itemQuantityArray[itemCount]
					+ ", Total: " + currencyFormatter.format(itemSubtotalArray[itemCount]);
			cartLineArray[itemCount].setText(cartLine);
			
			
			////South Panel
			setButtonStatus(deleteB, true);
			setButtonStatus(finishB, true);
			
			
			//Increment the count
			itemCount++;
			
			//update the northpanel's labels
			idLabel.setText("Enter item ID for Item #" + (itemCount + 1) + ":");
			qtyLabel.setText("Enter quantity for Item #" + (itemCount + 1) + ":");
			
			//check if itemCount reached max reaches the max
			if(itemCount >= MAXITEMS)
			{
				//update the north panel
				idTextField.setEditable(false);
				idTextField.setVisible(false);
				
				qtyTextField.setEditable(false);
				qtyTextField.setVisible(false);
				
				//update the south panel
				//update buttons. no longer accepting new order
				setButtonStatus(processB, false);
				setButtonStatus(confirmB, false);
			}
			else
			{
				///north panel
				idTextField.setText("");
				qtyTextField.setText("");
				
				///south panel
				setButtonStatus(processB, true);
				processB.setText("Search For Item #" + (itemCount + 1));
				
				setButtonStatus(confirmB, false);
				confirmB.setText("Add Item #" + (itemCount + 1) + " To Cart");
			}
			

		} // end method
			
	} // end class
	
	//*********************************************************************************************************
	private class DeleteButtonHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			System.out.println("The Delete Last Item Button Was Clicked...");
			
			//Check if there's smth to delete
			if(itemCount == 0) {
				return;
			}
			
			//decrement the count
			itemCount--;
			orderSubtotal -= itemSubtotalArray[itemCount];
			
			//Clear the last itms's info from the cart
			itemIDArray[itemCount] = null;
			itemTittleArray[itemCount] = null;
			itemInStockArray[itemCount] = null;
			itemPriceArray[itemCount] = 0.0;
			itemQuantityArray[itemCount] = 0;
			itemDiscountArray[itemCount] = 0.0;
			itemSubtotalArray[itemCount] = 0.0;
			itemFoundInFileAtRowArray[itemCount] = 0;
			
			
			////North Panel
			totalLabel.setText("Current Subtotal for " + itemCount + " item(s):");
			//check if that was the only item in the cart
			if(itemCount == 0)
			{
				totalTextField.setText("");
			}
			else
			{
				totalTextField.setText(currencyFormatter.format(orderSubtotal));
			}
					
			idLabel.setText("Enter item ID for Item #" + (itemCount + 1) + ":");
			idTextField.setEditable(true);
			idTextField.setVisible(true); // in case if the cart is full
			idTextField.setText("");
			
			qtyLabel.setText("Enter quantity for Item #" + (itemCount + 1) + ":");
			qtyTextField.setEditable(true);
			qtyTextField.setVisible(true);
			qtyTextField.setText("");
			
			itemLabel.setText("Details for Item #" + (itemCount + 1) + ":");
			itemTextField.setText("");
			
			////CEnter Panel
			cartLineArray[itemCount].setText("");
			
			if (itemCount == 0)
			{
				cartLabel.setText("Your Shopping Cart Is Currently Empty");
			}
			else
			{
				cartLabel.setText("Your Shopping Cart Currently Contains " + itemCount + " Item(s)");
			}
			
			////South Panel
			processB.setText("Search For Item #" + (itemCount + 1));
			setButtonStatus(processB, true);
					
			confirmB.setText("Add Item #" + (itemCount + 1) + " To Cart");
			setButtonStatus(confirmB, false);
			
			//if that was the only item in the cart
			if (itemCount == 0)
			{
				setButtonStatus(deleteB, false);
				setButtonStatus(finishB, false);
			}

		} // end method
		
	} // end class
	
	//*********************************************************************************************************
	private class FinishButtonHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			System.out.println("The Check Out Button Was Clicked...");
			//set the class variables
			//need file writer for writing to transactions.csv
			//will need a Data object here for output in invoice and permuting for timestamp
			//use DateFormat.getDateTimeInstace(x,x,Local.y) for permutations
			//example: DateFormat frenchDateTime = 
			//         DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.LONG, Locale.France); //was SHORT LONG
			
			
			try 
			{
				//set up the file writer object
				FileWriter transactionFile = new FileWriter(outputFileName, true); //02092026185953 - DDMMYYHHMMSS
				
				//set up a String for the date/time
				Date currentDate = new Date();
				DateFormat usDateTime = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, Locale.US);
				String dateTimeStr = usDateTime.format(currentDate);
				
				SimpleDateFormat logDateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a zzz");
				String logDateTimeStr = logDateFormat.format(currentDate);
				
				//set up a StringBuilder object to hold the big string of data for the invoice
				StringBuilder invoiceBuilder = new StringBuilder();
				
				//pick apart the date/time object for formatting into the timestamp
				SimpleDateFormat permutationFormat = new SimpleDateFormat("ddMMyyyyHHmmss");
				String transactionID = permutationFormat.format(currentDate);
				
				//xObject.deleteCharAt(xObject.indexOf("/"); //delete first '/'
				
				//build up the output message by just appending strings together
				//Tax calculation
				orderTaxAmount = orderSubtotal * TAX_RATE;
				orderTotal = orderSubtotal + orderTaxAmount;
				
				orderSubtotalStr = currencyFormatter.format(orderSubtotal);
				taxRateStr = decimalFormatter.format(TAX_RATE);
				
				String taxAmtStr = currencyFormatter.format(orderTaxAmount);
				String totalStr = currencyFormatter.format(orderTotal);
				
				invoiceBuilder.append("Date: ").append(dateTimeStr).append("\n\n");
	            invoiceBuilder.append("Number of line items: ").append(itemCount).append("\n\n");
	            invoiceBuilder.append("Item# / ID / Title / Price / Qty / Disc % / Subtotal:\n\n");
	            
	            //put up the items from the cart
	            for (int i = 0; i < itemCount; i++)
	            {
	            	//copy the single item onto the cart's array
	            	itemID = itemIDArray[i];
	            	itemTitle = itemTittleArray[i];
	                itemPricesStr = currencyFormatter.format(itemPriceArray[i]);
	                itemQuantityStr = String.valueOf(itemQuantityArray[i]);
	                itemDiscountStr = decimalFormatter.format(itemDiscountArray[i]);
	                itemSubtotalStr = currencyFormatter.format(itemSubtotalArray[i]);

	                //Show up the items in the cart on the dialogue box
	                invoiceBuilder.append(i + 1).append(". ")
	                              .append(itemID).append(" ")
	                              .append(itemTitle).append(" ")
	                              .append(itemPricesStr).append(" ")
	                              .append(itemQuantityStr).append(" ")
	                              .append(itemDiscountStr).append(" ")
	                              .append(itemSubtotalStr)
	                              .append("\n");

	            }
	            
	            invoiceBuilder.append("\n\nOrder subtotal: ").append(orderSubtotalStr).append("\n\n");
                invoiceBuilder.append("Tax rate:      ").append(taxRateStr).append("\n\n");
                invoiceBuilder.append("Tax amount:    ").append(taxAmtStr).append("\n\n");
                invoiceBuilder.append("ORDER TOTAL:   ").append(totalStr).append("\n\n");
                invoiceBuilder.append("Thanks for shopping at Nile Dot Com!");
               
                //save the final line
                outputStr = invoiceBuilder.toString();
				
				//write lines to the transactions.csv file
				for(int count = 0; count < itemCount; count++)
				{
					// refresh the per-item strings for the log line
	                itemID          = itemIDArray[count];
	                itemTitle       = itemTittleArray[count];
	                itemPricesStr   = String.valueOf(itemPriceArray[count]);
	                itemQuantityStr = String.valueOf(itemQuantityArray[count]);
	                itemDiscountStr = String.valueOf(itemDiscountArray[count]);
	                itemSubtotalStr = currencyFormatter.format(itemSubtotalArray[count]);

	                transactionFile.write(transactionID + ", " + itemID + ", " + itemTitle + ", " + itemPricesStr + ", " + itemQuantityStr + ", " + itemDiscountStr + ", " + itemSubtotalStr + ", " + dateTimeStr + "\n");
				}
				 transactionFile.write("\n");
				//file close
				transactionFile.close();
				
				//dump dialog box with final invoice
				JOptionPane.showMessageDialog(null, outputStr, "Nile Dot Com - Final Invoice", JOptionPane.INFORMATION_MESSAGE);
				
				//reset buttons and fields in GUI
				idTextField.setEditable(false);
				idTextField.setVisible(false);
				
				qtyTextField.setEditable(false);
				qtyTextField.setVisible(false);
	            
				setButtonStatus(processB, false);
				setButtonStatus(confirmB, false);
				setButtonStatus(deleteB, false);
				setButtonStatus(finishB, false);
				
			} // end try
			
			catch(IOException ioException) { //IO exception
				JOptionPane.showMessageDialog(null, "Error: Problem reading from file", "Error", JOptionPane.ERROR_MESSAGE);
			} // end catch

		} // end method
		
	} // end class
	
	//*********************************************************************************************************
	private class NewButtonHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			System.out.println("The Empty Cart Button Was Clicked...");
			
			itemCount = 0;
			orderSubtotal = 0.0;
			orderTotal = 0.0;
			orderTaxAmount = 0.0;
			
//			for(int count = 0; count < MAXITEMS; count++)
//			{
//				totalPrices[count] = 0.0;
//			}
			
			for(int count = 0; count < maxArraySize; count++)
			{
				itemIDArray[count] = null;
				itemTittleArray[count] = null;
				itemInStockArray[count] = null;
				itemPriceArray[count] = 0.0;
				itemQuantityArray[count] = 0;
				itemDiscountArray[count] = 0.0;
				itemSubtotalArray[count] = 0.0;
				itemFoundInFileAtRowArray[count] = 0;
			}
			
			////North Panel
			idLabel.setText("Enter item ID for Item #" + (itemCount + 1) + ":");
			idTextField.setText("");
			idTextField.setEditable(true);
			idTextField.setVisible(true);
			
			qtyLabel.setText("Enter quantity for Item #" + (itemCount + 1) + ":");
			qtyTextField.setText("");
			qtyTextField.setEditable(true);
			qtyTextField.setVisible(true);
			
			itemLabel.setText("Details for Item #" + (itemCount + 1) + ":");
			itemTextField.setText("");
			
			totalLabel.setText("Current Subtotal for " + itemCount + " item(s):");
			totalTextField.setText("");
			
			////Center Panel
			cartLabel.setText("Your Shopping Cart Is Currently Empty");
			for(int count = 0; count < MAXITEMS; count++)
			{
				cartLineArray[count].setText("");
			}
			
			////South panel
			setButtonStatus(processB, true);
			setButtonStatus(confirmB, false);
			setButtonStatus(deleteB, false);
			setButtonStatus(finishB, false);
			
			processB.setText("Search For Item #" + (itemCount + 1));
			confirmB.setText("Add Item #" + (itemCount + 1) + " To Cart");

		} // end method
		
	} // end class
	
	//*********************************************************************************************************
	private class ExitButtonHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			System.out.println("The Exit Button Was Clicked...");
			System.exit(0);
		} // end method
		
	} // end class
	
	//*********************************************************************************************************
	public static void main(String[] args) 
	{
		// TODO Auto-generated method stub
		JFrame aNewStore = new completeGUI(); //create the frame object
		aNewStore.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		aNewStore.setVisible(true); // display the frame
	} // end main

} // end class completeGUI
