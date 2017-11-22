/* 
 * Notification - EchoSistant Add-on 
 *
 *		3/01/2017		Version:4.0 R.0.2.1		weather 2.0, default tts messages
 *		2/27/2017		Version:4.0 R.0.0.6		time scheduling bug fix 
 *		2/17/2017		Version:4.0 R.0.0.1		Public Release
 *
 *  Copyright 2016 Jason Headley & Bobby Dobrescu
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
/**********************************************************************************************************************************************/
definition(
	name			: "NotificationProfile",
    namespace		: "Echo",
    author			: "JH/BD",
	description		: "EchoSistant Add-on",
	category		: "My Apps",
    parent			: "Echo:EchoSistant", 
	iconUrl			: "https://raw.githubusercontent.com/BamaRayne/Echosistant/master/smartapps/bamarayne/echosistant.src/app-Echosistant.png",
	iconX2Url		: "https://raw.githubusercontent.com/BamaRayne/Echosistant/master/smartapps/bamarayne/echosistant.src/app-Echosistant@2x.png",
	iconX3Url		: "https://raw.githubusercontent.com/BamaRayne/Echosistant/master/smartapps/bamarayne/echosistant.src/app-Echosistant@2x.png")
/**********************************************************************************************************************************************/


preferences {

    page name: "mainProfilePage"
    		page name: "pNotifyScene"          
        	page name: "pNotifications"
        	page name: "pRestrict"
            page name: "pNotifyConfig"
            page name: "SMS"
            page name: "customSounds"
            page( name: "timeIntervalInput", title: "Only during a certain time")

}

//dynamic page methods
page name: "mainProfilePage"
    def mainProfilePage() {
        dynamicPage (name: "mainProfilePage", install: true, uninstall: true) {
	        section ("Create a Notification") {
                input "actionType", "enum", title: "Choose the message output...", required: false, defaultValue: "", submitOnChange: true, options: [
				"Custom",
				"Bell 1",
				"Bell 2",
				"Dogs Barking",
				"Fire Alarm",
				"The mail has arrived",
				"A door opened",
				"There is motion",
				"Smartthings detected a flood",
				"Smartthings detected smoke",
				"Someone is arriving",
				"Piano",
				"Lightsaber"]
			}

        if (actionType == "Custom") {
            section ("Send this message (optional - leave empty for defalut message") {
                input "message", "text", title: "Play this message...", required:false, multiple: false, defaultValue: ""
                paragraph "You can use the following variables in your custom message: &device, &action , &event and &time \n" +
                    "\nFor Example: \n&event sensor &device is &action and the event happened at &time \n" +
                    "Translates to: 'Contact' sensor 'Bedroom' is 'Open' and the event happened at '1:00 PM'"
            }
        }
        section ("Using These Triggers", hideWhenEmpty: true) {
        	def actions = location.helloHome?.getPhrases()*.label.sort()
            input "timeOfDay", "time", title: "At this time every day", required: false
            input "mySwitch", "capability.switch", title: "Choose Switch(es)...", required: false, multiple: true, submitOnChange: true
            	if (mySwitch) input "switchBoth", "bool", title: "Enable to play message for on and off", required: false, defaultValue: false // Jason 2/21/2017
            input "myContact", "capability.contactSensor", title: "Choose Doors and Windows..", required: false, multiple: true, submitOnChange: true
            	if (myContact) input "contactBoth", "bool", title: "Enable to play message for open and close", required: false, defaultValue: false  // Jason 2/21/2017
            input "myLocks", "capability.lock", title: "Choose Locks..", required: false, multiple: true, submitOnChange: true
            	if (myLocks) input "locksBoth", "bool", title: "Enable to play message for lock and unlock", required: false, defaultValue: false  // Jason 2/21/2017
            input "myMotion", "capability.motionSensor", title: "Choose Motion Sensors..", required: false, multiple: true, submitOnChange: true
            input "myPresence", "capability.presenceSensor", title: "Choose Presence Sensors...", required: false, multiple: true, submitOnChange: true
            input "myTstat", "capability.thermostat", title: "Choose Thermostats...", required: false, multiple: true, submitOnChange: true
            input "myWeatherAlert", "enum", title: "Choose Weather Alerts...", required: false, multiple: true, submitOnChange: true,
                    options: [
                    "TOR":	"Tornado Warning",
                    "TOW":	"Tornado Watch",
                    "WRN":	"Severe Thunderstorm Warning",
                    "SEW":	"Severe Thunderstorm Watch",
                    "WIN":	"Winter Weather Advisory",
                    "FLO":	"Flood Warning",
                    "WND":	"High Wind Advisoryt",
                    "HEA":	"Heat Advisory",
                    "FOG":	"Dense Fog Advisory",
                    "FIR":	"Fire Weather Advisory",
                    "VOL":	"Volcanic Activity Statement",
                    "HWW":	"Hurricane Wind Warning"
					]          
			input "myWeather", "enum", title: "Choose Hourly Weather Forecast Updates...", required: false, multiple: true, submitOnChange: true,
					options: ["Weather Condition Changes", "Chance of Precipitation Changes", "Wind Speed Changes", "Humidity Changes", "Any Weather Updates"]   
            input "myMode", "enum", title: "Choose Modes...", options: location.modes.name.sort(), multiple: true, required: false 
            input "myRoutine", "enum", title: "Choose Routines...", options: actions, multiple: true, required: false
        }    
        section ("and these output methods..." , hideWhenEmpty: true) {    
			input "sonos", "capability.musicPlayer", title: "On this Music Player", required: false, multiple: true, submitOnChange: true
            	if (sonos) {
					input "sonosVolume", "number", title: "Temporarily change volume", description: "0-100%", required: false
				}
			if (actionType == "Custom" && message) {
                input "speechSynth", "capability.speechSynthesis", title: "On this Speech Synthesis Device", required: false, multiple: true, submitOnChange: true
                    if (speechSynth) {
                        input "speechVolume", "number", title: "Temporarily change volume", description: "0-100%", required: false
                    }
            }
            
            href "SMS", title: "Send SMS & Push Messages...", description: pSendComplete(), state: pSendSettings()
        }
        section ("Using these Restrictions") {
            href "pRestrict", title: "Use these restrictions...", description: pRestComplete(), state: pRestSettings()
        }
		section ("Name and/or Remove this Profile") {
 		   	label title:"              Rename Profile ", required:false, defaultValue: "Notification Profile"  
		} 
	}
}
page name: "SMS"
    def SMS(){
        dynamicPage(name: "SMS", title: "Send SMS and/or Push Messages...", uninstall: false) {
        section ("Push Messages") {
            input "push", "bool", title: "Send Push Notification...", required: false, defaultValue: false
            input "timeStamp", "bool", title: "Add time stamp to Push Messages...", required: false, defaultValue: false  
            }
        section ("Text Messages" , hideWhenEmpty: true) {
            input "sendContactText", "bool", title: "Enable Text Notifications to Contact Book (if available)", required: false, submitOnChange: true
                if (sendContactText){
                    input "recipients", "contact", title: "Send text notifications to...", multiple: true, required: false
                }
            input "sendText", "bool", title: "Enable Text Notifications to non-contact book phone(s)", required: false, submitOnChange: true      
                if (sendText){      
                    paragraph "You may enter multiple phone numbers separated by comma to deliver the Alexa message as a text and a push notification. E.g. 8045551122,8046663344"
                    input name: "sms", title: "Send text notification to...", type: "phone", required: false
                }
            }    
        }        
    }
page name: "pRestrict"
    def pRestrict(){
        dynamicPage(name: "pRestrict", title: "", uninstall: false) {
			section ("Mode Restrictions") {
                input "modes", "mode", title: "Only when mode is", multiple: true, required: false, submitOnChange: true
            }        
            section ("Days - Audio only on these days"){	
                input "days", title: "Only on certain days of the week", multiple: true, required: false, submitOnChange: true,
                    "enum", options: ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"]
            }
            section ("Time - Audio only during these times"){
                href "certainTime", title: "Only during a certain time", description: pTimeComplete(), state: pTimeSettings()
            }   
	    }
	}
page name: "certainTime"
    def certainTime() {
        dynamicPage(name:"certainTime",title: "Only during a certain time", uninstall: false) {
            section("Beginning at....") {
                input "startingX", "enum", title: "Starting at...", options: ["A specific time", "Sunrise", "Sunset"], required: false , submitOnChange: true
                if(startingX in [null, "A specific time"]) input "starting", "time", title: "Start time", required: false, submitOnChange: true
                else {
                    if(startingX == "Sunrise") input "startSunriseOffset", "number", range: "*..*", title: "Offset in minutes (+/-)", required: false, submitOnChange: true
                    else if(startingX == "Sunset") input "startSunsetOffset", "number", range: "*..*", title: "Offset in minutes (+/-)", required: false, submitOnChange: true
                }
            }
            section("Ending at....") {
                input "endingX", "enum", title: "Ending at...", options: ["A specific time", "Sunrise", "Sunset"], required: false, submitOnChange: true
                if(endingX in [null, "A specific time"]) input "ending", "time", title: "End time", required: false, submitOnChange: true
                else {
                    if(endingX == "Sunrise") input "endSunriseOffset", "number", range: "*..*", title: "Offset in minutes (+/-)", required: false, submitOnChange: true
                    else if(endingX == "Sunset") input "endSunsetOffset", "number", range: "*..*", title: "Offset in minutes (+/-)", required: false, submitOnChange: true
                }
            }
        }
    }
/************************************************************************************************************
		
************************************************************************************************************/
def installed() {
	log.debug "Installed with settings: ${settings}"
	if (timeOfDay) {
		schedule(timeOfDay, "scheduledTimeHandler")
	}
	if (myWeatherAlert) {
		runEvery5Minutes(mGetWeatherAlerts)
	}
	if (myWeather) {
		runEvery1Hour(mGetCurrentWeather)
	}    
}
def updated() { 
	log.debug "Updated with settings: ${settings}"
    initialize()
}
def initialize() {
	state.lastTime
    state.lastWeatherCheck
    state.lastAlert
	if (timeOfDay) {
		schedule(timeOfDay, "scheduledTimeHandler")
	}
	if (myWeatherAlert) {
		runEvery5Minutes(mGetWeatherAlerts)
		state.weatherAlert
    }
	if (myWeather) {
    	log.debug "refreshing hourly weather"
    	mGetCurrentWeather()
		runEvery1Hour(mGetCurrentWeather)
        state.lastWeather
	}    
    state.lastWeather
    if (actionType) {
    if (myRoutine) {subscribe(location, "routineExecuted",alertsHandler)}
    if (myMode) {subscribe(location, "mode", alertsHandler)}
   	if (mySwitch) {
    	if (switchBoth) {  // Jason 2/21/2017
        	subscribe(mySwitch, "switch.on", alertsHandler)
            subscribe(mySwitch, "switch.off", alertsHandler)
            }
        if (!switchBoth) {
        	subscribe(mySwitch, "switch.on", alertsHandler)
            }
        }    
	if (myContact) {
    	if (contactBoth) {  // Jason 2/21/2017
    		subscribe(myContact, "contact.open", alertsHandler)
            subscribe(myContact, "contact.closed", alertsHandler)
            }
        if (!contactBoth) {
        	subscribe(myContact, "contact.open", alertsHandler)
            }
        }    
    if (myMotion) {subscribe(myMotion, "motion.active", alertsHandler)}
    if (myLocks) {
    	if (locksBoth) {  // Jason 2/21/2017 
    		subscribe(myLocks, "lock.locked", alertsHandler)
    		subscribe(myLocks, "lock.unlocked", alertsHandler)
    		}
    	if (!locksBoth) {
        	subscribe(myLocks, "lock.locked", alertsHandler)
            }
        }    
    if (myPresence) {
    	subscribe(myPresence, "presence", alertsHandler)
        }
    if (myTstat) {    
		subscribe(myTstat, "heatingSetpoint", alertsHandler)
        subscribe(myTstat, "coolingSetpoint", alertsHandler)
    	}
    }
}    
/************************************************************************************************************
   TIME OF DAY HANDLER
************************************************************************************************************/
def scheduledTimeHandler() {
	def data = [:]
		if (getDayOk()==true && getModeOk()==true && getTimeOk()==true) {	
			data = [value:"time", name:"time of day", device:"schedule"] 
    		alertsHandler(data)
    	}
}
/************************************************************************************************************
   EVENTS HANDLER
************************************************************************************************************/
def alertsHandler(evt) {
	def eVal = evt.value
    def eName = evt.name
    def eDev = evt.device
    def eDisplayN = evt.displayName
    def nRoutine = false
    def eTxt	
	def stamp = state.lastTime = new Date(now()).format("h:mm aa", location.timeZone)     
    
    if(parent.debug) log.info "Event Data: eName ${eName}, eVal:  ${eVal}, eDev: ${eDev}, eDisplayN: ${eDisplayN}, stamp: ${stamp}"	
    if (getDayOk()==true && getModeOk()==true && getTimeOk()==true) {
		if(eName == "coolingSetpoint" || eName == "heatingSetpoint") {
            eVal = evt.value.toFloat() // 2/22 Bobby rounding temps
            eVal = Math.round(eVal) // 2/22 Bobby rounding temps
        }
        if(eName == "coolingSetpoint" || eName == "heatingSetpoint") {
            eVal = evt.value.toFloat() // 2/22 Bobby rounding temps
            eVal = Math.round(eVal) // 2/22 Bobby rounding temps
        }
        if(eName == "routineExecuted" && myRoutine) {
        	def deviceMatch = myRoutine?.find {r -> r == eDisplayN}  
            if (deviceMatch){
            	eTxt = message ? "$message".replace("&device", "${eDisplayN}").replace("&event", "routine").replace("&action", "executed").replace("&time", "${stamp}") : null
            	if(parent.debug) log.debug "eTxt = ${eTxt}"
                if (message){
					if(recipients?.size()>0 || sms?.size()>0) {
                    	sendtxt(eTxt)
                	}
                    takeAction(eTxt)
                }
                else {
                	eTxt = "routine was executed"
                    takeAction(eTxt) 
        		}
         	}
        }
        else {
            if(eName == "mode" && myMode) {
                def deviceMatch = myMode?.find {m -> m == eVal}  
                if (deviceMatch){
                    eTxt = message ? "$message".replace("&device", "${eVal}").replace("&event", "${eName}").replace("&action", "changed").replace("&time", "${stamp}") : null
                    if(parent.debug) log.debug "eTxt = ${eTxt}"
                    if (message){
                        if(recipients?.size()>0 || sms?.size()>0) {
                            sendtxt(eTxt)
                        }
                        takeAction(eTxt)
                    }
                    else {
                        eTxt = "location mode has changed"
                        takeAction(eTxt) 
                    }
                }
            }        
            else {
                if (message){      
                    eTxt = message ? "$message".replace("&device", "${eDev}").replace("&event", "${eName}").replace("&action", "${eVal}").replace("&time", "${stamp}") : null
                    if(parent.debug) log.debug "eTxt = ${eTxt}"
                    if(eTxt){
                        if(recipients?.size()>0 || sms?.size()>0) {
                            sendtxt(eTxt)
                        }
                        takeAction(eTxt)
                    }
                }
                else {
                	if (eDev == "weather"){eTxt = eName}
                    else {eTxt = "Heads up, ${eDev} is now ${eVal}"}         
                    log.info "last else eTxt = ${eTxt}"
                    takeAction(eTxt)
                }
            }
        }
	}
}
/***********************************************************************************************************************
    CUSTOM SOUNDS HANDLER
***********************************************************************************************************************/
private takeAction(eTxt) {
	def sVolume
	log.debug "received message (eTxt) = ${eTxt}"
	
    if (actionType == "Custom") {
		state.sound = textToSpeech(eTxt instanceof List ? eTxt[0] : eTxt)
    }
    else loadSound()
    //Playing Audio Message
        if (speechSynth) {
            def currVolLevel = speechSynth.latestValue("level")
            def currMute = speechSynth.latestValue("mute")
                log.debug "vol switch = ${currVolSwitch}, vol level = ${currVolLevel}, currMute = ${currMute} "
                sVolume = settings.speechVolume ?: 30 
                speechSynth?.playTextAndResume(eTxt, sVolume)
                log.info "Playing message on the speech synthesizer'${speechSynth}' at volume '${sVolume}'"
        }
        if (sonos) { 
            def currVolLevel = sonos.latestValue("level")
            def currMuteOn = sonos.latestValue("mute").contains("muted")
                log.debug "currVolSwitchOff = ${currVolSwitchOff}, vol level = ${currVolLevel}, currMuteOn = ${currMuteOn} "
                if (currMuteOn) { 
                    log.warn "speaker is on mute, sending unmute command"
                    sonos.unmute()
                }
                sVolume = settings.sonosVolume ?: 30
                sonos?.playTrackAndResume(state.sound.uri, state.sound.duration, sVolume)
                log.info "Playing message on the music player '${sonos}' at volume '${volume}'"
        }
}
/***********************************************************************************************************************
    CUSTOM SOUNDS HANDLER
***********************************************************************************************************************/
private loadSound() {
	switch (actionType) {
		case "Bell 1":
			state.sound = [uri: "http://s3.amazonaws.com/smartapp-media/sonos/bell1.mp3", duration: "10"]
			break;
		case "Bell 2":
			state.sound = [uri: "http://s3.amazonaws.com/smartapp-media/sonos/bell2.mp3", duration: "10"]
			break;
		case "Dogs Barking":
			state.sound = [uri: "http://s3.amazonaws.com/smartapp-media/sonos/dogs.mp3", duration: "10"]
			break;
		case "Fire Alarm":
			state.sound = [uri: "http://s3.amazonaws.com/smartapp-media/sonos/alarm.mp3", duration: "17"]
			break;
		case "The mail has arrived":
			state.sound = [uri: "http://s3.amazonaws.com/smartapp-media/sonos/the+mail+has+arrived.mp3", duration: "1"]
			break;
		case "A door opened":
			state.sound = [uri: "http://s3.amazonaws.com/smartapp-media/sonos/a+door+opened.mp3", duration: "1"]
			break;
		case "There is motion":
			state.sound = [uri: "http://s3.amazonaws.com/smartapp-media/sonos/there+is+motion.mp3", duration: "1"]
			break;
		case "Smartthings detected a flood":
			state.sound = [uri: "http://s3.amazonaws.com/smartapp-media/sonos/smartthings+detected+a+flood.mp3", duration: "2"]
			break;
		case "Smartthings detected smoke":
			state.sound = [uri: "http://s3.amazonaws.com/smartapp-media/sonos/smartthings+detected+smoke.mp3", duration: "1"]
			break;
		case "Someone is arriving":
			state.sound = [uri: "http://s3.amazonaws.com/smartapp-media/sonos/someone+is+arriving.mp3", duration: "1"]
			break;
		case "Piano":
			state.sound = [uri: "http://s3.amazonaws.com/smartapp-media/sonos/piano2.mp3", duration: "10"]
			break;
		case "Lightsaber":
			state.sound = [uri: "http://s3.amazonaws.com/smartapp-media/sonos/lightsaber.mp3", duration: "10"]
			break;
		default:
			state?.sound = [uri: "http://s3.amazonaws.com/smartapp-media/sonos/bell1.mp3", duration: "10"]
			break;
	}
}
/***********************************************************************************************************************
    WEATHER ALERTS
***********************************************************************************************************************/
def mGetWeatherAlerts(){
	def result = "There are no weather alerts for your area"
	def data = [:]
//    try {
		if (getDayOk()==true && getModeOk()==true && getTimeOk()==true) {
        	def weather = getWeatherFeature("alerts", settings.wZipCode)
        	def type = weather.alerts.type[0]
            def alert = weather.alerts.description[0]
            def expire = weather.alerts.expires[0]
            def typeOk = myWeatherAlert?.find {a -> a == type}
			if(typeOk){
                if(expire != null) expire = expire?.replaceAll(~/ EST /, " ").replaceAll(~/ CST /, " ").replaceAll(~/ MST /, " ").replaceAll(~/ PST /, " ")
                if(alert != null) {
                    result = alert  + " is in effect for your area, that expires at " + expire
                    if(state.weatherAlert == null){
                        log.warn "saving the weather alert for the first time alert = ${alert} , expire = ${expire}"
                        state.weatherAlert = result
                        state.lastAlert = new Date(now()).format("h:mm aa", location.timeZone)
                        data = [value:"alert", name: result, device:"weather"] 
                        alertsHandler(data)
                    }
                    else {
                        log.warn "new weather alert = ${alert} , expire = ${expire}"
                        def newAlert = result != state.weatherAlert ? true : false
                        if(newAlert == true){
                            state.weatherAlert = result
                            state.lastAlert = new Date(now()).format("h:mm aa", location.timeZone)
                            data = [value:"alert", name: result, device:"weather"] 
                            alertsHandler(data)
                        }
                    }
                }
         	}
            log.warn "weather alert not selected"
    	}
/*    
    }
	catch (Throwable t) {
	log.error t
	return result
	}
*/    
}
/***********************************************************************************************************************
    HOURLY FORECAST
***********************************************************************************************************************/
def mGetCurrentWeather(){
    def weatherData = [:]
    def data = [:]
   	def result
//    try {
		if (getDayOk()==true && getModeOk()==true && getTimeOk()==true) {
        //hourly updates
        def cWeather = getWeatherFeature("hourly", settings.wZipCode)
        def cWeatherCondition = cWeather.hourly_forecast[0].condition
        def cWeatherPrecipitation = cWeather.hourly_forecast[0].pop + " percent"
        def cWeatherWind = cWeather.hourly_forecast[0].wspd.english + " miles per hour"
        def cWeatherHum = cWeather.hourly_forecast[0].humidity + " percent"
        def cWeatherUpdate = cWeather.hourly_forecast[0].FCTTIME.civil
        def pastWeather = state.lastWeather
        if(myWeather) {
            if(pastWeather == null) {
                log.warn "pastWeather = ${pastWeather}"
                weatherData.wCond = cWeatherCondition
                weatherData.wWind = cWeatherWind
                weatherData.wHum = cWeatherHum
                weatherData.wPrecip = cWeatherPrecipitation        
                state.lastWeather = weatherData
				state.lastTime = new Date(now()).format("h:mm aa", location.timeZone)
            }
            else {
            	def wUpdate = pastWeather.wCond != cWeatherCondition ? "current weather condition" : pastWeather.wWind != cWeatherWind ? "wind intensity" : pastWeather.wHum != cWeatherHum ? "humidity" : pastWeather.wPrecip != cWeatherPrecipitation ? "chance of precipitation" : null
            	def wChange = wUpdate == "current weather condition" ? cWeatherCondition : wUpdate == "wind intensity" ? cWeatherWind  : wUpdate == "humidity" ? cWeatherHum : wUpdate == "chance of precipitation" ? cWeatherPrecipitation : null                    
                //something has changed
                if(wUpdate != null){
                    // saving update
                    log.warn "saving hourly weather as it has changed" 
                    weatherData.wCond = cWeatherCondition
                    weatherData.wWind = cWeatherWind
                    weatherData.wHum = cWeatherHum
                    weatherData.wPrecip = cWeatherPrecipitation        
                    state.lastWeather = weatherData
                    state.lastWeatherCheck = new Date(now()).format("h:mm aa", location.timeZone)
                    log.warn "hourly weather changed: wChange=${wChange}" 
                    if (myWeather == "Any Weather Updates"){                   
                        result = "The hourly weather forecast has been updated. The " + wUpdate + " has been changed to "  + wChange
                        data = [value:"forecast", name: result, device:"weather"] 
                        alertsHandler(data)
                    }
                    else {
                        if (myWeather == "Weather Condition Changes" && wUpdate ==  "current weather condition"){
                            result = "The " + wUpdate + " has been updated to " + wChange
                            data = [value:"condition", name: result, device:"weather"] 
                            alertsHandler(data)
                        }
                        else if (myWeather == "Chance of Precipitation Changes" && wUpdate ==  "chance of precipitation"){
                            result = "The " + wUpdate + " has been updated to " + wChange
                            data = [value:"precipitation", name: result, device:"weather"] 
                            alertsHandler(data)
                        }        
                        else if (myWeather == "Wind Speed Changes" && wUpdate == "wind intensity"){
                            result = "The " + wUpdate + " has been updated to " + wChange
                            data = [value:"wind", name: result, device:"weather"] 
                            alertsHandler(data)
                        }         
                        else if (myWeather == "Humidity Changes" && wUpdate == "humidity"){
                            result = "The " + wUpdate + " has been updated to " + wChange
                            data = [value:"humidity", name: result, device:"weather"] 
                            alertsHandler(data)
                        }
                    }
                }       
            }
            log.info "updating hourly weather"  
        }
    }
/*    
    }
	catch (Throwable t) {
	log.error t
	return result
	}
*/ 
}

/***********************************************************************************************************************
    RESTRICTIONS HANDLER
***********************************************************************************************************************/
private getAllOk() {
	modeOk && daysOk && timeOk
}
private getModeOk() {
    def result = !modes || modes?.contains(location.mode)
	log.debug "modeOk = $result"
    result
} 
private getDayOk() {
    def result = true
if (days) {
		def df = new java.text.SimpleDateFormat("EEEE")
		if (location.timeZone) {
			df.setTimeZone(location.timeZone)
		}
		else {
			df.setTimeZone(TimeZone.getTimeZone("America/New_York"))
		}
		def day = df.format(new Date())
		result = days.contains(day)
	}
	log.debug "daysOk = $result"
	result
}
private getTimeOk() {
	def result = true
	if ((starting && ending) ||
	(starting && endingX in ["Sunrise", "Sunset"]) ||
	(startingX in ["Sunrise", "Sunset"] && ending) ||
	(startingX in ["Sunrise", "Sunset"] && endingX in ["Sunrise", "Sunset"])) {
		def currTime = now()
		def start = null
		def stop = null
		def s = getSunriseAndSunset(zipCode: zipCode, sunriseOffset: startSunriseOffset, sunsetOffset: startSunsetOffset)
		if(startingX == "Sunrise") start = s.sunrise.time
		else if(startingX == "Sunset") start = s.sunset.time
		else if(starting) start = timeToday(starting,location.timeZone).time
		s = getSunriseAndSunset(zipCode: zipCode, sunriseOffset: endSunriseOffset, sunsetOffset: endSunsetOffset)
		if(endingX == "Sunrise") stop = s.sunrise.time
		else if(endingX == "Sunset") stop = s.sunset.time
		else if(ending) stop = timeToday(ending,location.timeZone).time
		result = start < stop ? currTime >= start && currTime <= stop : currTime <= stop || currTime >= start
	if (parent.debug) log.trace "getTimeOk = $result."
    }
    log.debug "timeOk = $result"
    return result
}
private hhmm(time, fmt = "h:mm a") {
	def t = timeToday(time, location.timeZone)
	def f = new java.text.SimpleDateFormat(fmt)
	f.setTimeZone(location.timeZone ?: timeZone(time))
	f.format(t)
}
private offset(value) {
	def result = value ? ((value > 0 ? "+" : "") + value + " min") : ""
}
private timeIntervalLabel() {
	def result = ""
	if      (startingX == "Sunrise" && endingX == "Sunrise") result = "Sunrise" + offset(startSunriseOffset) + " to Sunrise" + offset(endSunriseOffset)
	else if (startingX == "Sunrise" && endingX == "Sunset") result = "Sunrise" + offset(startSunriseOffset) + " to Sunset" + offset(endSunsetOffset)
	else if (startingX == "Sunset" && endingX == "Sunrise") result = "Sunset" + offset(startSunsetOffset) + " to Sunrise" + offset(endSunriseOffset)
	else if (startingX == "Sunset" && endingX == "Sunset") result = "Sunset" + offset(startSunsetOffset) + " to Sunset" + offset(endSunsetOffset)
	else if (startingX == "Sunrise" && ending) result = "Sunrise" + offset(startSunriseOffset) + " to " + hhmm(ending, "h:mm a z")
	else if (startingX == "Sunset" && ending) result = "Sunset" + offset(startSunsetOffset) + " to " + hhmm(ending, "h:mm a z")
	else if (starting && endingX == "Sunrise") result = hhmm(starting) + " to Sunrise" + offset(endSunriseOffset)
	else if (starting && endingX == "Sunset") result = hhmm(starting) + " to Sunset" + offset(endSunsetOffset)
	else if (starting && ending) result = hhmm(starting) + " to " + hhmm(ending, "h:mm a z")
}
/***********************************************************************************************************************
    SMS HANDLER
***********************************************************************************************************************/
private void sendtxt(message) {
	def stamp = state.lastTime = new Date(now()).format("h:mm aa", location.timeZone)
    if (parent.debug) log.debug "Request to send sms received with message: '${message}'"
    if (sendContactText) { 
        sendNotificationToContacts(message, recipients)
            if (parent.debug) log.debug "Sending sms to selected reipients"
    } 
    else {
    	if (push) {
        	message = timeStamp==true ? message + " at " + stamp : message
    		sendPush message
            	if (parent.debug) log.debug "Sending push message to selected reipients"
        }
    } 
    if (notify) {
        sendNotificationEvent(message)
             	if (parent.debug) log.debug "Sending notification to mobile app"
    }
    if (sms) {
        sendText(sms, message)
        if (parent.debug) log.debug "Processing message for selected phones"
	}
}
private void sendText(number, message) {
    if (sms) {
        def phones = sms.split("\\,")
        for (phone in phones) {
            sendSms(phone, message)
            if (parent.debug) log.debug "Sending sms to selected phones"
        }
    }
}
/************************************************************************************************************
   Page status and descriptions 
************************************************************************************************************/       
def pSendSettings() {def result = ""
    if (sendContactText || sendText || push) {
    	result = "complete"}
   		result}
def pSendComplete() {def text = "Tap here to configure settings" 
    if (sendContactText || sendText || push) {
    	text = "Configured"}
    	else text = "Tap to Configure"
		text}
def pRestSettings() {def result = ""
    if (modes || days) {
    	result = "complete"}
   		result}
def pRestComplete() {def text = "Tap here to configure settings" 
    if (modes || days) {
    	text = "Configured"}
    	else text = "Tap to Configure"
		text}     
def pTimeSettings() {def result = ""
    if (startingX || endingX) {
    	result = "complete"}
   		result}
def pTimeComplete() {def text = "Tap here to configure settings" 
    if (startingX || endingX) {
    	text = "Configured"}
    	else text = "Tap to Configure"
		text}