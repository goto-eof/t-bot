# T-Bot




**DESCRIPTION**

Single-page application and daemon that allows to retrieve RSS data and send it to a Telegram group/channel through Telegram API.

**CONFIGURATION**
**1 - Upload jar on your raspberry PI**
In eclipses __Run configurations__ add new __Maven Build__ entry that should contain as __Goals__ value **install -P uploadToRaspberryPi**. In this way we run the maven plugin called __uploadToRaspberryPi__, that will upload, after making a project build, our jar file to the Raspberry PI device.


**2 - Start jar files**
java -jar "tbot-daemon.jar"
java -jar "tbot-web.jar"


**3 - Access to the web application and start to configure daemon**
http://RASPBERRY-PI-IP:8081/t-bot-manager/


**UPDATES**

2022-04-12 - added deploy with wagon for tbot-web and tbot-daemon through wagon-ssh referring to the maven user settings.xml for properties; Added maven user settings sample file.

2022-04-13 - When we build with uploadToRaspberryPi, we also will add an entry for the main class in the jars MANIFEST file

~~Currently all project information are available in italian [here](http://dodu.it/it/t-bot/).~~
