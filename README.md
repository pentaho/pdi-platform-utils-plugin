# BA Server Utils
=================

Collection of kettle steps and plugins to interact with BA Server services.


#Steps

- __Get session variables__: reads session variables from the current user session in the BA Server
- __Set session variables__: writes session variables to the current user session in the BA Server
- __Call endpoint__: inspects the BA Server to list available endpoints / invokes a BA Server endpoint

## Additional info

The steps **Get session variables** and **Set session variables** read and write user session variables. In order to
simulate user session variables in Spoon (for testing purposes) the variables will be read from and written to internal
variables with a prefix `_FAKE_SESSION_`.


#Spoon Plugins

- __Pentaho Connection Plugin__: This plugin enables Spoon to connect to a Pentaho BA server repository in order to open and save kettle transformations and jobs. 
	- __Open File From Pentaho Repository__: To open a file that is in a Pentaho repository select "File=>Open URL" or press the corresponding button in the Spoon toolbar. This will open a dialog from where you should choose the option "Pentaho" from the "Look in" drop down. Fill in the necessary info, press connect choose the desired file and press the ok button.  
	- __Save File To Pentaho Repository__: To save a file to a Pentaho repository select "File=>Save as (VFS)" or press the corresponding button in the Spoon toolbar. This will open a dialog where you should choose the option "Pentaho" from the "Look in" dropdown. Fill in the necessary info, press connect choose the desired folder and file name and finally press the ok button.  