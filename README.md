# Simple_GUI_Shopping_Cart
Develop a Java application that creates a standalone GUI application that simulates an e-store. 
We’ll call our store Nile Dot Com… we’re not quite as big as Amazon.com!1 
The application will allow the user to place in stock items into a shopping cart 
and once all items are included, total all costs (including tax), produce an invoice, and append
a transaction log file.

The program development includes the following steps:
1. Create a main GUI containing the following components:
a. An area that allows the user to input data into the application along with the
descriptive text that describes each input area.
b. An area that shows the current contents of the shopping cart.
c. A total of six buttons as shown below with functionality as described below. The
various input fields and buttons on the interface are only accessible at certain
points during a user’s interaction with the e-store.
2. An input file named “inventory.csv”. This is a comma separated file which contains
the data that will be read by the application when the user makes a selection. Each line
in this file contains five entries: an item id (a string), a quoted string containing the
description of the item, an in stock status (a string), the quantity on hand (an integer),
and the unit price for one of the item (a double).
3. An output file (append only) named “transactions.csv” must be created that
uniquely identifies and logs each user transaction with the e-store. The unique
transaction id will be generated as a permutation of the current date/time when the
transaction occurred (see below). Note that this file must use a .csv extension and not
a .txt extension. We’ll point out why this should be the case in class/discussions.
