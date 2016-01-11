# Spatial-Viewer-Panthera

A java swing application that connects to oracle database to query spatial data.
This project was developed as part of one of the assignment in CSCI 585 at University of Southern California.

## Library Used

- [oracle.spatial.geometry](https://docs.oracle.com/cd/B19306_01/appdev.102/b14373/oracle/spatial/geometry/package-summary.html)

## Problem Statement
Description: In this projecy you’ll write a program to create an application with a GUI, which allows users to interact with the spatial data provided in the first part of the homework. In case you choose to use Java, you will use JDBC in your java program to communicate with the oracle database.

Specification:
When the user runs your program, it must fetch all regions, all ponds and all lions from the oracle database and show them in the GUI. Here are the original colors that you should use to show the geometries:

- boundary/border of each region must be displayed in black color ● interior of each regions must be displayed in white color
- boundary/border of each pond must be displayed in black color ● interior of each pond must be displayed in blue color.
- each lion must be displayed in green color The GUI should interact with the user in the following way:
- Display a checkbox with title “show lions and ponds in the selected region”
- If the checkbox “show lions and ponds in selected region” is checked and the
user clicks on a region, the GUI must show all the lions and ponds inside the region in red color.￼
- After clicking on one region, if the user clicks on another region, lions and ponds in the previously clicked region must be reset to their original colors.
- If the user unchecks the checkbox “show lions and ponds in selected region”, all the lions and ponds must be reset to their original colors.

## Output

The code was developed in java using the java awt package and uses oracle spatial geometry for querying Oracle Spatial Database.
![Output Image](https://github.com/rahulravindran0108/Spatial-Viewer-Panthera/tree/master/images/output.png)

