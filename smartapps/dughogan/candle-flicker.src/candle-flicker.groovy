/**
* Candle Flicker
*
* Copyright 2015
*
* Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at:
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
* on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
* for the specific language governing permissions and limitations under the License.
*
*/
definition(
	name: "Candle Flicker",
	namespace: "dughogan",
	author: "Doug Hogan",
	description: "Flicker your lights like Candles using a constrained random dimming value.",
	category: "My Apps",
	iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
	iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")

	preferences {
		section("Select Dimmable Lights...") {
		input "dimmers", "capability.switchLevel", title: "Lights", required: true, multiple: true
	}

	section("Activate the flicker when this switch is on...") {
		input "switches", "capability.switch", title: "Switch", required: true, multiple: false
	}
}


def installed() {
	initialize()
}

def updated() {	
	initialize()
}

def initialize() {
	unsubscribe()
	unschedule() 
	subscribe(switches, "switch.on", eventHandler)
}


def eventHandler(evt) {
	if(switches.currentValue("switch") == "on") {
		for (dimmer in dimmers) {      
                	def lowLevel= Math.abs(new Random().nextInt() % 3) + 59
                	def upLevel= Math.abs(new Random().nextInt() % 10) + 90
                	def upDelay = Math.abs(new Random().nextInt() % 500)
                	def lowDelay = upDelay + Math.abs(new Random().nextInt() % 200)
            		//log.debug "low: $lowLevel $lowDelay high: $upLevel $upDelay"
           
			dimmer.setLevel(upLevel,[delay: upDelay])
                	dimmer.setLevel(lowLevel,[delay: lowDelay])
        	}
        	def sleepTime = Math.abs(new Random().nextInt() % 200)
        	pause(sleepTime)
        	runIn(1,"eventHandler")
	}
}