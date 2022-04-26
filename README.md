# T-Bot




**DESCRIPTION**

Single-page application and daemon that allows to retrieve RSS data and send it to a Telegram group/channel through Telegram API. The tbot-web project allows to configure the tbot-daemon through a web application in angular. The java tbot-daemon sends messages in background basing on the configuration set through user interface.


The tbot-web project runs as a simple spring boot application. Currently the software application has been translated into only two languages: English and Italian. Tbot-web was tested on a Linux machine running a local MySQL instance.

**CONFIGURATION**

** DB configuration **
Create a scheme called "tbot", to which the user called "tbot" is associated, with the password "password".

** Maven settings **
Create a user maven settingx.xml file and add and edit the content  [from here](https://github.com/AndreiDodu/t-bot/blob/main/tbot/config-samples/user-maven-settings.xml).


** Upload jar on a remote server **
In eclipses Run configurations add new Maven Build entry that should contain as Goals value install -P uploadToRaspberryPi. In this way we run the maven profile called uploadToRaspberryPi, that will upload, after making a project build, our jar file to the Raspberry PI device.






**UPDATES**

2022-04-12 - added deploy with wagon for tbot-web and tbot-daemon through wagon-ssh referring to the maven user settings.xml for properties; Added maven user settings sample file.

2022-04-13 - When we build with uploadToRaspberryPi, we also will add an entry for the main class in the jars MANIFEST file; Added run as service configuration for linux. 

2022-04-14 - upgraded to Angular 13 and added tranlsation module (uses ngx-translate library).

2022-04-15 - Fixed configuration for building t-bot web project with maven.

2022-04-26 - Upgraded to angular 13 and added the italian translation.

~~Currently all project information are available in italian [here](http://dodu.it/it/t-bot/).~~
