/**
 *  Turn off light after trigger has stopped firing
 *
 *  Copyright 2015 Bruce Ravenel
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
 */
definition(
    name: "Turn off light after trigger has stopped firing",
    namespace: "smartthings",
    author: "Doug Hogan",
    description: "Turn some switches off some minutes after camera motion stops",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	section("Select Switches, Trigger and Minutes") {
		input "switches", "capability.switch", title: "Switches", required: true, multiple: true
		input "trigger", "capability.momentary", title: "Trigger", required: true, multiple: false
		input "minutes", "number", title: "Minutes", required: false
	}
}

def installed() {
	initialize()
}

def updated() {
	unsubscribe()
	initialize()
}

def initialize() {
	subscribe(trigger, "switch.on", activeHandler)
	subscribe(trigger, "switch.off", inactiveHandler)
}

def activeHandler(evt) {
	state.pending = false
}

def inactiveHandler(evt) {
	state.pending = !("on" in trigger.currentState)
	if(state.pending) {if(minutes) runIn(minutes*60, switchesOff) else switchesOff()}
}

def switchesOff() {
	if(state.pending) switches.off()
}