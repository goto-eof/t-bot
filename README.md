# T-Bot




**DESCRIPTION**

Single-page application and daemon that allows to retrieve RSS data and send it to a Telegram group/channel through Telegram API. The tbot-web project allows to configure the tbot-daemon through a web application in angular. The java tbot-daemon sends messages in background basing on the configuration set through user interface.

**CONFIGURATION**

**1 - Upload jar on your raspberry PI**

In eclipses __Run configurations__ add new __Maven Build__ entry that should contain as __Goals__ value **install -P uploadToRaspberryPi**. In this way we run the maven profile called __uploadToRaspberryPi__, that will upload, after making a project build, our jar file to the Raspberry PI device.


**2.1 - Start jar files for manual execution**

java -jar "tbot-daemon.jar"

java -jar "tbot-web.jar"


**2.2 - Start jar files as services at linux startup**

Follow [this configuration](https://github.com/AndreiDodu/t-bot/tree/main/tbot/config-samples/linux)

**3 - tbot-web and tbot-daemon configuration**

Dupplicate application.prod.yml, rename it to application.yml and change db configuration.

Do the same for the tbot-daemon project.

**4 - Database**

Create a new schema called tbot. 

**5 - Access to the web application and start to configure daemon**

http://RASPBERRY-PI-IP:8081/t-bot-manager/








**UPDATES**

2022-04-12 - added deploy with wagon for tbot-web and tbot-daemon through wagon-ssh referring to the maven user settings.xml for properties; Added maven user settings sample file.

2022-04-13 - When we build with uploadToRaspberryPi, we also will add an entry for the main class in the jars MANIFEST file; Added run as service configuration for linux. 

2022-04-14 - upgraded to Angular 13 and added tranlsation module (uses ngx-translate library).

2022-04-15 - Fixed configuration for building t-bot web project with maven.

~~Currently all project information are available in italian [here](http://dodu.it/it/t-bot/).~~
