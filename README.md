## Mobile app loggers
- This project contains loggers that can be used for logging events in mobile applications (native mobile Android application or web application) that are automatically send to remote API [Keen.io](https://www.keen.io).

### Mobile web applications logger
- Logger in `/Web-logger` can be used for logging events from web mobile applications.

###### Usage
1. Insert `logger.js` file into root folder of your project.
2. Import logger into your project. Eg. in `index.js` file in head section add line `<script src=’logger.js’></script>`.
3. Initialize logger as following: `Logger.init()`.
4. Subscribe for detected events:

```java
Logger.subscribe(’click’, function(e){ console.log(e);
```


### Native Android applications logger
- Logger in `/Android-logger` can be used for logging events from native Android mobile applications, and logger automatically send captured events to remote API.

###### Usage
1. Add file `logger.aar` found in `/Android-logger/logger/build/outputs/aar` as new module to your project Example using Android Studio: (1) File -> New -> New Module, (2) Import .JAR/.AAR package, (3) choose file `logger.aar` and type name `logger`.
2. In your project `settings.gradle` file add line: `include ’:logger’`.
3. In your project `build.gradle` file in section `dependencies` add line `compile
project(’:logger’)`.
4. In all your app Activities thet can be entry point to your application initialize logger as follows:

```java
//import Logger
import com.example.logger.Logger;
/*
* initialize logger
* @params:
* - uuid - unique device identifier
* - true/false - send events to Keen API
* - keenProjectID - ID of keen.io project
* - keenWriteKey - keen project write key
*/
Logger.start(uuid, true, keenProjectID, keenWriteKey);
```
