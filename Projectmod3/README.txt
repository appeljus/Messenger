========== Integration project group 8 ==========
---------- Authors ----------
Kimberly Hengst
Tim Sonderen
Kevin Hetterscheid
Martijn de Bijl

---------- Before use ----------
Our application uses an external library. The library is included in our source, so the library only needs to be put into the projectpath.

---------- Setting up the adhoc network ----------
The application runs on an adhoc network. This only works on linux, and needs to be setup via the commandline. We have a small script which does this automatically. If you run adhoc_setup as root, the adhoc network is propperly setup. After using the application, the normal network can be setup again by running adhoc_desetup.

---------- Using the application ----------
The application is started by running the LogginWindow. In this window you are asked to fill in a user name. After filling in a username, the window will be changed to the chat lobby. At the bottom of this window is the text area in which messages can be typed to be send to the other users. On the left are a few buttons with other functionalities.

---------- Send files ----------
The send file button is used to send a file. After pressing the button, a pop-up opens in which you can choose a file to send.  The receiver is asked to save the file somewhere.

---------- Private chat ----------
On the left is a tick box. If the box is ticked, private chat messages will apear in a new window. Above the tick box is a list with names from users in the lobby. After clicking on a name a private chat window will apear. Private messages can also be send by typing /w "Receiver" before the message. "Receiver" should be a valid name in the list.