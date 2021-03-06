# GoogleDriveAPISpringboot

## Introduction

This Springboot Project completed some Google Drive API features including Google account sign in, file uploading, folder creating, and file uploading to folders.

## Installation

Download all the code and put it into a folder, then import the project into Eclipse or IntelliJ IDEA.


## Project Description
The main source code is in src/main/java/com/gdrive/sbt/drivelistfiles/controller/HomepageController.java

src/main/resources/static/index.html is the html for Log in page

src/main/resources/static/dashboard.html is the html for the page after log in

src/main/resources/static/scripts/index.js contents the javascrpts code

## How to use
To use your own Google Cloud project to run this project, you need to create Oauth client credentials and download the json file. Then replace the jason file in src/main/resources/keys and src/main/resources/credentials. 

Change the path name shown in the image if you want to upload your own file:
![3BJ3)MKYU _QLW`7~O@%UXD](https://user-images.githubusercontent.com/70415185/163737698-b3de6e06-703d-4ce4-b7a0-a04b0e1770d8.png)

Change the folder ID and pathname of file if you want to upload a file to a folder:
![$YC B28D48)SQ(LX@M}CWLP](https://user-images.githubusercontent.com/70415185/163737775-ef6a3442-675c-4df5-8849-a267fd8a78a4.png)
You can find the folder ID in the url after you open the folder

## How to run the project
src/main/java/com/gdrive/sbt/drivelistfiles/DriveListFilesApplication.java

Build the project first and then run this java file. After everything is good, open http://localhost:8080/ on your browser.

## Trouble Shooting
If you receive the error that your access token is expired or revoked after open the website, change String USER_IDENTIFIER_KEY's value and try to run the project again.

![S6)_O16EP_IJ`{`R)}QFZDS](https://user-images.githubusercontent.com/70415185/163738114-fb5ff353-5c4a-41ed-ae4e-e407ae6fc836.png)


## Feature not completed
I do write the code for download files, but it is not working right now

## Videos
https://youtu.be/b5GdX0yf1UE

https://youtu.be/2_rAitwHrpE









