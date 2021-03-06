/*

    Hubitat Natrualight Room (Child) v2.1

    Author:
            Brandon Spitza

    Discussion:
            https://community.hubitat.com/t/release-app-naturalight-home-room-v2-0/62343


    Changelog:
        2.1 (2021-02-03)
            -Fixed: BUG: Turning off Alternate Dim State while lights are off turns lights on
            -Fixed: BUG: Turning light off with a toggle button and Alternate Dim State active, lights don't turn back on (use Alternate Bright State on as workaround)
            -Switched from state to atomicState due to ack'd Hubitat bug tinyurl.com/u0z8tjt7
            -Explicitly defined conditionals to ==true or ==false rather than exists or !exists
            -Cleaned up some sloppy type-ing

    To Do:
            -Validate user inputs
            -Motion support

*/

definition(
    name: "Naturalight Room",
    namespace: "naturalight",
    author: "Brandon Spitza",
    importURL: "",
    description: "Configure natural daylight tones indoors with global home settings and optional refinements per room.",
    category: "My Apps",
    iconUrl: "",
    iconX2Url: "",
    parent: "naturalight:Naturalight Home"
)

preferences {
    page(name: "roomPage", title: "Room Page", nextPage: "devicePage", uninstall: true)
    page(name: "devicePage", title: "Device Page", nextPage: "timePage", uninstall: true) {
        section("Select on/off button. Motion is not currently supported, but can be accommodated by adding a virtual button here and using Rule Machine to press it. " +
                "For Rule Machine logic, see: https://tinyurl.com/y425chws") {
            paragraph ""
            input name: "btnOnOff", type: "capability.pushableButton", title: "Select Button to Turn Lights On/Off:", multiple: true, required: true
            input name: "btnNumOnOffBtnOn", type: "number", title: "Select Button Press Number to Turn Lights On:", required: true
            input name: "btnNumOnOffBtnOff", type: "number", title: "Select Button Press Number to Turn Lights Off:", required: true
            paragraph ""
            paragraph ""
            paragraph "Select bulbs to be controlled. The ideal setup leverages the Hue Bridge Integration app where Hue Zones and Groups are synced from the Hue Bridge " + 
                "(individual bulbs do not need to be synced from the bridge in this scenario). A room would consist of a single Hue Zone containing both a Hue " +
                "Group of color bulbs and a Hue Group of color-temperature bulbs. The Room will still work with any combination of Zones, Groups, Color or CT bulbs."
            paragraph ""
            input name: "hueZones", type: "capability.colorTemperature", title: "Select Hue Zones:", multiple: true, required: false
            input name: "bulbsCT", type: "capability.colorTemperature", title: "Select CT Bulbs:", multiple: true, required: false
            input name: "bulbsC", type: "capability.colorControl", title: "Select Color Bulbs:", multiple: true, required: false
            paragraph ""
            paragraph ""
        }
    }
    page(name: "timePage", title: "Time Settings", nextPage: "tempPage", uninstall: true)
    page(name: "tempPage", title: "Color Temperature Settings", nextPage: "levelPage", uninstall: true)
    page(name: "levelPage", title: "Lighting Level Settings", nextPage: "huePage", uninstall: true)
    page(name: "huePage", title: "Hue Settings", nextPage: "satPage", uninstall: true)
    page(name: "satPage", title: "Saturation Settings", nextPage: "altBrightPage", uninstall: true)
    page(name: "altBrightPage", title: "Alternate Bright Lighting Settings", nextPage: "altDimPage", uninstall: true)
    page(name: "altDimPage", title: "Alternate Dim Lighting Settings", install: true, uninstall: true)
}

def roomPage() {
    dynamicPage(name: "roomPage") {
        section() {
            input name: "appButtonPause", type: "button", title: "Un/Pause"
            if (atomicState.paused == true) { paragraph "***APP CURRENTLY PAUSED***" }
            paragraph ""
        }
        section("Room Name:") {
            paragraph ""
            label title: "Enter room name", required: true
            paragraph ""
            paragraph ""
        }
        section("Debugging:") {
            paragraph ""
            input(name: "log", type: "bool", title: "Log settings and actions?",
                description: "Log settings and actions.", defaultValue: false,
                required: true, displayDuringSetup: true)
            paragraph ""
            paragraph ""
        }
    }
}

def timePage() {
    dynamicPage(name: "timePage") {
        section("Leave any or all of these fields blank to default to global settings from Home (parent) app. An input overrides that specific Home parameter while " +
                "others will still default. Home setting are displayed below the input line for reference.") {
            paragraph ""
            input name: "strTimeMornWarm", type: "time", title: "Warm Morning Start:", required: false
            paragraph "Home setting: ${parent.getTimeMornWarm().substring(11,16)}"
            paragraph ""
            paragraph ""
            input name: "strTimeMornCold", type: "time", title: "Cold Morning Start:", required: false
            paragraph "Home setting: ${parent.getTimeMornCold().substring(11,16)}"
            paragraph ""
            paragraph ""
            input name: "strTimeDay", type: "time", title: "Day (Static) Start:", required: false
            paragraph "Home setting: ${parent.getTimeDay().substring(11,16)}"
            paragraph ""
            paragraph ""
            input name: "strTimeAfternoon", type: "time", title: "Afternoon Start:", required: false
            paragraph "Home setting: ${parent.getTimeAfternoon().substring(11,16)}"
            paragraph ""
            paragraph ""
            input name: "strTimeAfternoonStatic", type: "time", title: "Afternoon (Static) Start:", required: false
            paragraph "Home setting: ${parent.getTimeAfternoonStatic().substring(11,16)}"
            paragraph ""
            paragraph ""
            input name: "strTimeEveningCT", type: "time", title: "Early Evening Start:", required: false
            paragraph "Home setting: ${parent.getTimeEveningCT().substring(11,16)}"
            paragraph ""
            paragraph ""
            input name: "strTimeEveningC", type: "time", title: "Late Evening Start:", required: false
            paragraph "Home setting: ${parent.getTimeEveningC().substring(11,16)}"
            paragraph ""
            paragraph ""
            input name: "strTimeNight", type: "time", title: "Night (Static) Start:", required: false
            paragraph "Home setting: ${parent.getTimeNight().substring(11,16)}"
            paragraph ""
            paragraph ""
        }
    }
}

def tempPage() {
    dynamicPage(name: "tempPage") {
        section("Leave any or all of these fields blank to default to global settings from Home (parent) app. An input overrides that specific Home parameter while " +
                "others will still default. Home setting are displayed below the input line for reference.") {
            paragraph ""
            input name: "tempWarmest", type: "number", title: "Warmest CT:", required: false
            paragraph "Home setting: ${parent.getTempWarmest()}"
            paragraph ""
            paragraph ""
            input name: "tempColdest", type: "number", title: "Coldest CT:", required: false
            paragraph "Home setting: ${parent.getTempColdest()}"
            paragraph ""
            paragraph ""
        }
    }
}

def levelPage() {
    dynamicPage(name: "levelPage") {
        section("Leave any or all of these fields blank to default to global settings from Home (parent) app. An input overrides that specific Home parameter while " +
                "others will still default. Home setting are displayed below the input line for reference.") {
            paragraph ""
            input name: "lvlInitWarmMorn", type: "number", title: "Warm Morning Period, Initial:", required: false
            paragraph "Home setting: ${parent.getLvlInitWarmMorn()}"
            paragraph ""
            paragraph ""
            input name: "lvlGoalWarmMorn", type: "number", title: "Warm Morning Period, Goal:", required: false
            paragraph "Home setting: ${parent.getLvlGoalWarmMorn()}"
            paragraph ""
            paragraph ""
            paragraph ""
            paragraph ""
            input name: "lvlGoalColdMorn", type: "number", title: "Cold Morning Period, Goal:", required: false
            paragraph "Home setting: ${parent.getLvlGoalColdMorn()}"
            paragraph ""
            paragraph ""
            paragraph ""
            paragraph ""
            paragraph ""
            input name: "lvlGoalDay", type: "number", title: "Day Period, Goal:", required: false
            paragraph "Home setting: ${parent.getLvlGoalDay()}"
            paragraph ""
            paragraph ""
            paragraph ""
            paragraph ""
            input name: "lvlGoalAfternoon", type: "number", title: "Afternoon Period, Goal:", required: false
            paragraph "Home setting: ${parent.getLvlGoalAfternoon()}"
            paragraph ""
            paragraph ""
            paragraph ""
            paragraph ""
            input name: "lvlCTGoalEveningCT", type: "number", title: "Early Evening Period, CT Bulb Goal:", required: false
            paragraph "Home setting: ${parent.getLvlCTGoalEveningCT()}"
            paragraph ""
            paragraph ""
            input name: "lvlCGoalEveningCT", type: "number", title: "Early Evening Period, Color Bulb Goal:", required: false
            paragraph "Home setting: ${parent.getLvlCGoalEveningCT()}"
            paragraph ""
            paragraph ""
            paragraph ""
            paragraph ""
            input name: "lvlCGoalEveningC", type: "number", title: "Late Evening Period, Color Bulb Goal:", required: false
            paragraph "Home setting: ${parent.getLvlCGoalEveningC()}"
            paragraph ""
            paragraph ""
            paragraph ""
            paragraph ""
            input name: "lvlCGoalNight", type: "number", title: "Night Period, Color Bulb Goal:", required: false
            paragraph "Home setting: ${parent.getLvlCGoalNight()}"
            paragraph ""
            paragraph ""
        }
    }
}

def huePage() {
    dynamicPage(name: "huePage") {
        section("Leave any or all of these fields blank to default to global settings from Home (parent) app. An input overrides that specific Home parameter while " +
                "others will still default. Home setting are displayed below the input line for reference.") {
            paragraph ""
            input name: "hueInitEveningCT", type: "number", title: "Early Evening Period, Color Bulb Initial:", required: false
            paragraph "Home setting: ${parent.getHueInitEveningCT()}"
            paragraph ""
            paragraph ""
            input name: "hueGoalEveningCT", type: "number", title: "Early Evening Period, Color Bulb Goal:", required: false
            paragraph "Home setting: ${parent.getHueGoalEveningCT()}"
            paragraph ""
            paragraph ""
            input name: "hueGoalEveningC", type: "number", title: "Late Evening Period, Color Bulb Goal:", required: false
            paragraph "Home setting: ${parent.getHueGoalEveningC()}"
            paragraph ""
            paragraph ""
            input name: "hueGoalNight", type: "number", title: "Night Period, Color Bulb Hue Goal:", required: false
            paragraph "Home setting: ${parent.getHueGoalNight()}"
            paragraph ""
            paragraph ""
        }
    }
}

def satPage() {
    dynamicPage(name: "satPage") {
        section("Leave any or all of these fields blank to default to global settings from Home (parent) app. An input overrides that specific Home parameter while " +
                "others will still default. Home setting are displayed below the input line for reference.") {
            paragraph ""
            input name: "satInitEveningCT", type: "number", title: "Early Evening Period, Color Bulb Initial:", required: false
            paragraph "Home setting: ${parent.getSatInitEveningCT()}"
            paragraph ""
            paragraph ""
            input name: "satGoalEveningCT", type: "number", title: "Early Evening Period, Color Bulb Goal:", required: false
            paragraph "Home setting: ${parent.getSatGoalEveningCT()}"
            paragraph ""
            paragraph ""
            paragraph ""
            paragraph ""
            input name: "satGoalEveningC", type: "number", title: "Late Evening Period, Color Bulb Saturation Goal:", required: false
            paragraph "Home setting: ${parent.getSatGoalEveningC()}"
            paragraph ""
            paragraph ""
            paragraph ""
            paragraph ""
            input name: "satGoalNight", type: "number", title: "Night Period, Color Bulb Goal:", required: false
            paragraph "Home setting: ${parent.getSatGoalNight()}"
            paragraph ""
            paragraph ""
        }
    }
}

def altBrightPage() {
    dynamicPage(name: "altBrightPage") {
        section("") {
            paragraph ""
            paragraph "If using the same button as this room's on/off button, you can save time by leaving the button field below blank and just fill out one of the two Button Number fields."
            paragraph "Consider a virtual button on a dashboard for this if you are out of physical buttons."
            paragraph ""
            input name: "btnAltStateBright", type: "capability.holdableButton", title: "Select Button to Activate Alternate Bright Lighting:", multiple: true, required: false
            paragraph ""
            input name: "btnPressNumAltStateBright", type: "number", title: "Select Button Number to PRESS to Activate Alternate Bright Lighting:", required: false
            input name: "btnHoldNumAltStateBright", type: "number", title: "Select Button Number to HOLD to Activate Alternate Bright Lighting:", required: false
            paragraph ""
            paragraph ""
            paragraph "Leave one or both of the fields below blank to default to global settings from Home (parent) app. An input overrides that specific Home parameter while " +
                "others will still default. Home setting are displayed below the input line for reference."
            paragraph ""
            input name: "lvlAltStateBright", type: "number", title: "Alternate Bright Lighting Level:", required: false
            paragraph "Home setting: ${parent.getLvlAltStateBright()}"
            paragraph ""
            paragraph ""
            input name: "tempAltStateBright", type: "number", title: "Alternate Bright Lighting Temperature:", required: false
            paragraph "Home setting: ${parent.getTempAltStateBright()}"
            paragraph ""
            paragraph ""
        }
    }
}

def altDimPage() {
    dynamicPage(name: "altDimPage") {
        section("") {
            paragraph ""
            paragraph "If using the same button as this room's on/off button, you can save time by leaving the button field below blank and just fill out one of the two Button Number fields."
            paragraph "Consider a virtual button on a dashboard for this if you are out of physical buttons."
            paragraph ""
            input name: "btnAltStateDim", type: "capability.holdableButton", title: "Select Button to Activate Alternate Dim Lighting:", multiple: true, required: false
            paragraph ""
            input name: "btnPressNumAltStateDim", type: "number", title: "Select Button Number to PRESS to Activate Alternate Dim Lighting:", required: false
            input name: "btnHoldNumAltStateDim", type: "number", title: "Select Button Number to HOLD to Activate Alternate Dim Lighting:", required: false
            paragraph ""
            paragraph ""
            paragraph "Leave one or both of the fields below blank to default to global settings from Home (parent) app. An input overrides that specific Home parameter while " +
                "others will still default. Home setting are displayed below the input line for reference."
            paragraph ""
            input name: "lvlAltStateDim", type: "number", title: "Alternate Dim Lighting Level:", required: false
            paragraph "Home setting: ${parent.getLvlAltStateDim()}"
            paragraph ""
            paragraph ""
            input name: "tempAltStateDim", type: "number", title: "Alternate Dim Lighting Temperature:", required: false
            paragraph "Home setting: ${parent.getTempAltStateDim()}"
            paragraph ""
            paragraph ""
        }
    }
}




def installed() {
    unsubscribe()
    unschedule()
    initialize()
    log("${app.label}: Installed with settings: ${settings}")
}

def updated() {
    unsubscribe()
    unschedule()
    initialize()
    log("${app.label}: Updated with settings: ${settings}")
}

def initialize() {
    log("${app.label}: Initializing app")
    

    atomicState.paused = false
    atomicState.on = isOn()
    atomicState.altStateBright = false
    atomicState.altStateDim = false
    atomicState.period
    atomicState.periodIndex
    atomicState.periodIndexStartColorCTDivergence = 6
    atomicState.lvlCT
    atomicState.temp
    atomicState.lvlC
    atomicState.hue
    atomicState.sat
    atomicState.staticPeriod
    
    log("${app.label}: atomicState.on: ${atomicState.on}, " +
        "atomicState.periodIndexStartColorCTDivergence: ${atomicState.periodIndexStartColorCTDivergence}, ")
    
    if (btnOnOff) {
        if (btnNumOnOffBtnOn == btnNumOnOffBtnOff) {
            subscribe(btnOnOff, "pushed.${btnNumOnOffBtnOn}", togglebtnOnOffHandler)
        } else {
            subscribe(btnOnOff, "pushed.${btnNumOnOffBtnOn}", onBtnHandler)
            subscribe(btnOnOff, "pushed.${btnNumOnOffBtnOff}", offBtnHandler)
        }
    }
    
    if (btnAltStateBright && btnHoldNumAltStateBright) {
        subscribe(btnAltStateBright, "held.${btnHoldNumAltStateBright}", btnAltStateBrightHandler)
    } else if (btnAltStateBright && btnPressNumAltStateBright) {
        subscribe(btnAltStateBright, "pushed.${btnPressNumAltStateBright}", btnAltStateBrightHandler)
    } else if (!btnAltStateBright && btnHoldNumAltStateBright) {
        subscribe(btnOnOff, "held.${btnHoldNumAltStateBright}", btnAltStateBrightHandler)
    } else if (!btnAltStateBright && btnPressNumAltStateBright) {
        subscribe(btnOnOff, "pushed.${btnPressNumAltStateBright}", btnAltStateBrightHandler)
    }
    
    if (btnAltStateDim && btnHoldNumAltStateDim) {
        subscribe(btnAltStateDim, "held.${btnHoldNumAltStateDim}", btnAltStateDimHandler)
    } else if (btnAltStateDim && btnPressNumAltStateDim) {
        subscribe(btnAltStateDim, "pushed.${btnPressNumAltStateDim}", btnAltStateDimHandler)
    } else if (!btnAltStateDim && btnHoldNumAltStateDim) {
        subscribe(btnOnOff, "held.${btnHoldNumAltStateDim}", btnAltStateDimHandler)
    } else if (!btnAltStateDim && btnPressNumAltStateDim) {
        subscribe(btnOnOff, "pushed.${btnPressNumAltStateDim}", btnAltStateDimHandler)
    }
    
    def now = new Date()
    log("${app.label}: Activating initial bulb states")
    
    driver(now)
}

def isOn() {
    log("${app.label}: Checking if the lights are on")
    
    for (hueZone in hueZones) {
        if (hueZone.currentValue("switch") == "on") {
            log("${app.label}: ${hueZone} is on")
            return true
        }
    }
    for (bulbCT in bulbsCT) {
        if (bulbCT.currentValue("switch") == "on") {
            log("${app.label}: ${bulbCT} is on")
            return true
        }
    }
    for (bulbC in bulbsC) {
        if (bulbC.currentValue("switch") == "on") {
            log("${app.label}: ${bulbC} is on")
            return true
        }
    }
    
    log("${app.label}: Lights are not on")
    return false
}

def appButtonHandler(btn) {
    switch(btn) {
        case "appButtonPause":  toggleAppPause()
            break
    }
}

def toggleAppPause() {
    if (atomicState.paused == false) {
        log("${app.label} app paused")
        atomicState.paused = true
    } else {
        log("${app.label} app unpaused")
        atomicState.paused = false
    }
}

def onBtnHandler(evt) {
    log("${app.label}: On Button Press - Turning On Bulbs")
        
    atomicState.on = true
    
    if (atomicState.periodIndex <= atomicState.periodIndexStartColorCTDivergence || (atomicState.altStateBright)) {
        if (hueZones) {
            for (hueZone in hueZones) {
                hueZone.on()
            }
            driver(new Date())
            return
        }
        for (bulbCT in bulbsCT) {
            bulbCT.on()
        }
    }
    for (bulbC in bulbsC) {
        bulbC.on()
    }
    driver(new Date())
}

def offBtnHandler(evt) {
    log("${app.label}: Off Button Press - Turning Off Bulbs")
    
    atomicState.on = false
    atomicState.altStateDim = false
    atomicState.altStateBright = false
    
    
    if (atomicState.periodIndex <= atomicState.periodIndexStartColorCTDivergence) {
        if (hueZones) {
            for (hueZone in hueZones) {
                hueZone.off()
            }            
            return
        }
    }
    for (bulbCT in bulbsCT) {
        bulbCT.off()
    }
    for (bulbC in bulbsC) {
        bulbC.off()
    }    
}

def togglebtnOnOffHandler(evt) {    
    if (atomicState.on == false) {
        log("${app.label}: Toggle Button Press. Bulbs currently off. Turning On.")
        onBtnHandler(evt)
    } else if (atomicState.on == true) {
        log("${app.label}: Toggle Button Press. Bulbs currently on. Turning Off.")
        offBtnHandler(evt)
    }
}

def btnAltStateBrightHandler(evt) {    
    log("${app.label}: Alternate Bright Lighting button pressed")
    if (atomicState.altStateBright == false) {
        log("${app.label}: Alternate Bright Lighting activated")
        atomicState.altStateBright = true
        atomicState.altStateDim = false
        onBtnHandler()
    } else if (atomicState.altStateBright == true) {
        log("${app.label}: Alternate Bright Lighting deactivated")
        atomicState.altStateBright = false
        driver(new Date())
    }
}

def btnAltStateDimHandler(evt) {
    
    log("${app.label}: Alternate Dim Lighting button pressed")
    if (atomicState.altStateDim == false) {
        log("${app.label}: Alternate Dim Lighting activated")
        atomicState.altStateDim = true
        atomicState.altStateBright = false
    } else if (atomicState.altStateDim == true) {
        log("${app.label}: Alternate Dim Lighting deactivated")
        atomicState.altStateDim = false
    }
    driver(new Date())
}




def driver(now) {
    if (atomicState.on == false) {
        log("${app.label}: lights are off. No update needed.")
        return
    }
    if (atomicState.paused == true) {
        log("${app.label} app paused")
        return
    }
    setPeriodTimes()
    setPeriodBulbValues()
    setBulbStates(now)
    activateBulbStates()
}

def setPeriodTimes() {
    log("${app.label}: Setting period times")
    
    if (strTimeMornWarm == null) {
        timeMornWarm = toDateTime(parent.getTimeMornWarm())
    } else {
        timeMornWarm = toDateTime(strTimeMornWarm)
    }
    
    if (strTimeMornCold == null) {
        timeMornCold = toDateTime(parent.getTimeMornCold())
    } else {
        timeMornCold = toDateTime(strTimeMornCold)
    }
    
    if (strTimeDay == null) {
        timeDay = toDateTime(parent.getTimeDay())
    } else {
        timeDay = toDateTime(strTimeDay)
    }
    
    if (strTimeAfternoon == null) {
        timeAfternoon = toDateTime(parent.getTimeAfternoon())
    } else {
        timeAfternoon = toDateTime(strTimeAfternoon)
    }
    
    if (strTimeAfternoonStatic == null) {
        timeAfternoonStatic = toDateTime(parent.getTimeAfternoonStatic())
    } else {
        timeAfternoonStatic = toDateTime(strTimeAfternoonStatic)
    }
    
    if (strTimeEveningCT == null) {
        timeEveningCT = toDateTime(parent.getTimeEveningCT())
    } else {
        timeEveningCT = toDateTime(strTimeEveningCT)
    }
    
    if (strTimeEveningC == null) {
        timeEveningC = toDateTime(parent.getTimeEveningC())
    } else {
        timeEveningC = toDateTime(strTimeEveningC)
    }
    
    if (strTimeNight == null) {
        timeNight = toDateTime(parent.getTimeNight())
    } else {
        timeNight = toDateTime(strTimeNight)
    }
    
}

def setPeriodBulbValues() {
    log("${app.label}: Setting period bulb values")
    
    if (lvlAltStateBright == null) { lvlAltStateBright = parent.getLvlAltStateBright() }
    if (tempAltStateBright == null) { tempAltStateBright = parent.getTempAltStateBright() }
    if (lvlAltStateDim == null) { lvlAltStateDim = parent.getLvlAltStateDim() }
    if (tempAltStateDim == null) { tempAltStateDim = parent.getTempAltStateDim() }
    if (tempColdest == null) { tempColdest = parent.getTempColdest() }
    if (tempWarmest == null) { tempWarmest = parent.getTempWarmest() }
    if (lvlInitWarmMorn == null) { lvlInitWarmMorn = parent.getLvlInitWarmMorn() }
    if (lvlGoalWarmMorn == null) { lvlGoalWarmMorn = parent.getLvlGoalWarmMorn() }
    if (lvlGoalColdMorn == null) { lvlGoalColdMorn = parent.getLvlGoalColdMorn() }
    if (lvlGoalDay == null) { lvlGoalDay = parent.getLvlGoalDay() }
    if (lvlGoalAfternoon == null) { lvlGoalAfternoon = parent.getLvlGoalAfternoon() }
    if (lvlCTGoalEveningCT == null) { lvlCTGoalEveningCT = parent.getLvlCTGoalEveningCT() }
    if (lvlCGoalEveningCT == null) { lvlCGoalEveningCT = parent.getLvlCGoalEveningCT() }
    if (lvlCGoalEveningC == null) { lvlCGoalEveningC = parent.getLvlCGoalEveningC() }
    if (lvlCGoalNight == null) { lvlCGoalNight = parent.getLvlCGoalNight() }
    if (hueInitEveningCT == null) { hueInitEveningCT = parent.getHueInitEveningCT() }
    if (hueGoalEveningCT == null) { hueGoalEveningCT = parent.getHueGoalEveningCT() }
    if (hueGoalEveningC == null) { hueGoalEveningC = parent.getHueGoalEveningC() }
    if (hueGoalNight == null) { hueGoalNight = parent.getHueGoalNight() }
    if (satInitEveningCT == null) { satInitEveningCT = parent.getSatInitEveningCT() }
    if (satGoalEveningCT == null) { satGoalEveningCT = parent.getSatGoalEveningCT() }
    if (satGoalEveningC == null) { satGoalEveningC = parent.getSatGoalEveningC() }
    if (satGoalNight == null) { satGoalNight = parent.getSatGoalNight() }
}

def setBulbStates(now) {    
    def percentThroughPeriod = setPeriod(now)
    log("${app.label}: Setting bulb state values")
    
    def lvlCTInit, lvlCTGoal, tempInit, tempGoal
    def lvlCInit, lvlCGoal, hueInit, hueGoal, satInit, satGoal
    

    if (atomicState.altStateBright == true) {
        log("${app.label}: Alternate Bright Lighting active. Setting CT Bulb level to ${lvlAltStateBright}%")
        atomicState.lvlCT = lvlAltStateBright
        
        log("${app.label}: Alternate Bright Lighting active. Setting Bulb temperature to ${tempAltStateBright}K")
        atomicState.temp = tempAltStateBright
        
        return
    }
    
    //atomicState.altStateDim processed last
    
    if (atomicState.period == "WarmMorning") {
        
        lvlCTInit = lvlInitWarmMorn
        lvlCTGoal = lvlGoalWarmMorn
        atomicState.lvlCT = getWeightedStateVal(lvlCTInit, lvlCTGoal, percentThroughPeriod)
        
        tempInit = tempWarmest
        tempGoal = tempColdest
        atomicState.temp = getWeightedStateVal(tempInit, tempGoal, percentThroughPeriod)
        
    } else if (atomicState.period == "ColdMorning") {
        
        lvlCTInit = lvlGoalWarmMorn
        lvlCTGoal = lvlGoalColdMorn
        atomicState.lvlCT = getWeightedStateVal(lvlCTInit, lvlCTGoal, percentThroughPeriod)
        
        tempInit = tempColdest
        tempGoal = tempColdest
        atomicState.temp = getWeightedStateVal(tempInit, tempGoal, percentThroughPeriod)
        
    } else if (atomicState.period == "Day") {

        lvlCTInit = lvlGoalColdMorn
        lvlCTGoal = lvlGoalDay
        atomicState.lvlCT = getWeightedStateVal(lvlCTInit, lvlCTGoal, percentThroughPeriod)
        
        atomicState.temp = tempColdest
        
    } else if (atomicState.period == "Afternoon") {
        
        lvlCTInit = lvlGoalDay
        lvlCTGoal = lvlGoalAfternoon
        atomicState.lvlCT = getWeightedStateVal(lvlCTInit, lvlCTGoal, percentThroughPeriod)
        
        tempInit = tempColdest
        tempGoal = tempWarmest
        atomicState.temp = getWeightedStateVal(tempInit, tempGoal, percentThroughPeriod)
        
    } else if (atomicState.period == "AfternoonStatic") {
        
        atomicState.lvlCT = lvlGoalAfternoon
        
        atomicState.temp = tempWarmest
        
    } else if (atomicState.period == "EveningCT") {
        
        lvlCTInit = lvlGoalAfternoon
        lvlCTGoal = lvlCTGoalEveningCT
        atomicState.lvlCT = getWeightedStateVal(lvlCTInit, lvlCTGoal, percentThroughPeriod)
        
        atomicState.temp = tempWarmest
        
        lvlCInit = lvlGoalAfternoon
        lvlCGoal = lvlCGoalEveningCT
        atomicState.lvlC = getWeightedStateVal(lvlCInit, lvlCGoal, percentThroughPeriod)
        
        hueInit = hueInitEveningCT
        hueGoal = hueGoalEveningCT
        atomicState.hue = getWeightedStateVal(hueInit, hueGoal, percentThroughPeriod)
        
        satInit = satInitEveningCT
        satGoal = satGoalEveningCT
        atomicState.sat = getWeightedStateVal(satInit, satGoal, percentThroughPeriod)
        
    } else if (atomicState.period == "EveningC") {
        
        lvlCInit = lvlCGoalEveningCT
        lvlCGoal = lvlCGoalEveningC
        atomicState.lvlC = getWeightedStateVal(lvlCInit, lvlCGoal, percentThroughPeriod)
        
        hueInit = hueGoalEveningCT
        hueGoal = hueGoalEveningC
        atomicState.hue = getWeightedStateVal(hueInit, hueGoal, percentThroughPeriod)
        
        satInit = satGoalEveningCT
        satGoal = satGoalEveningC
        atomicState.sat = getWeightedStateVal(satInit, satGoal, percentThroughPeriod)
        
    } else if (atomicState.period == "Night") {
        
        atomicState.lvlC = lvlCGoalNight
        
        atomicState.hue = hueGoalNight
        
        atomicState.sat = satGoalNight
        
    }
    
    if (atomicState.altStateDim == true) {
        if (atomicState.periodIndex <= atomicState.periodIndexStartColorCTDivergence) {
            if (atomicState.lvlCT > lvlAltStateDim) {
                log("${app.label}: Alternate Dim Lighting active. Setting CT Bulb level to ${lvlAltStateDim}%.")
                atomicState.lvlCT = lvlAltStateDim
            }
            log("${app.label}: Alternate Dim Lighting active. Setting Bulb temperature to ${tempAltStateDim}K")
            atomicState.temp = tempAltStateDim
        }
        if (atomicState.periodIndex >= atomicState.periodIndexStartColorCTDivergence) {
            log("${app.label}: Alternate Dim Lighting active. Setting Color Bulb level to ${lvlAltStateDim}%.")
            atomicState.lvlC = lvlAltStateDim
        }
    }
    
    log("${app.label}: atomicState.lvlCT: ${atomicState.lvlCT}, atomicState.temp: ${atomicState.temp}, atomicState.lvlC: ${atomicState.lvlC}, atomicState.hue: ${atomicState.hue}, atomicState.sat: ${atomicState.sat}, ")
}

def getWeightedStateVal(init, goal, percentThroughPeriod) {
    return ( init + ((float)goal - init) * percentThroughPeriod ).round()
}

def setPeriod(now) {
    log("${app.label}: Setting period")
    def periodTimeStart, periodTimeEnd
    
    if (timeOfDayIsBetween(timeMornWarm, timeMornCold, now)) {
        atomicState.period = "WarmMorning"
        atomicState.periodIndex = 1
        atomicState.staticPeriod = false
        periodTimeStart = timeMornWarm
        periodTimeEnd = timeMornCold
    } else if (timeOfDayIsBetween(timeMornCold, timeDay, now)) {
        atomicState.period = "ColdMorning"
        atomicState.periodIndex = 2
        atomicState.staticPeriod = false
        periodTimeStart = timeMornCold
        periodTimeEnd = timeDay
    } else if (timeOfDayIsBetween(timeDay, timeAfternoon, now)) {
        atomicState.period = "Day"
        atomicState.periodIndex = 3
        atomicState.staticPeriod = true
        periodTimeStart = timeDay
        periodTimeEnd = timeAfternoon
    } else if (timeOfDayIsBetween(timeAfternoon, timeAfternoonStatic, now)) {
        atomicState.period = "Afternoon"
        atomicState.periodIndex = 4
        atomicState.staticPeriod = false
        periodTimeStart = timeAfternoon
        periodTimeEnd = timeAfternoonStatic
    } else if (timeOfDayIsBetween(timeAfternoonStatic, timeEveningCT, now)) {
        atomicState.period = "AfternoonStatic"
        atomicState.periodIndex = 5
        atomicState.staticPeriod = true
        periodTimeStart = timeAfternoonStatic
        periodTimeEnd = timeEveningCT
    } else if (timeOfDayIsBetween(timeEveningCT, timeEveningC, now)) {
        atomicState.period = "EveningCT"
        atomicState.periodIndex = 6
        atomicState.staticPeriod = false
        periodTimeStart = timeEveningCT
        periodTimeEnd = timeEveningC
    } else if (timeOfDayIsBetween(timeEveningC, timeNight, now)) {
        atomicState.period = "EveningC"
        atomicState.periodIndex = 7
        atomicState.staticPeriod = false
        periodTimeStart = timeEveningC
        periodTimeEnd = timeNight
    } else if (timeOfDayIsBetween(timeNight, timeMornWarm.plus(1), now)) {
        atomicState.period = "Night"
        atomicState.periodIndex = 8
        atomicState.staticPeriod = true
        periodTimeStart = timeNight
        periodTimeEnd = timeMornWarm.plus(1)
    } else if (timeOfDayIsBetween(timeNight.minus(1), timeMornWarm, now)) {
        atomicState.period = "Night"
        atomicState.periodIndex = 9
        atomicState.staticPeriod = true
        periodTimeStart = timeNight.minus(1)
        periodTimeEnd = timeMornWarm
    }

    log("${app.label}: Period ${atomicState.period}, beginning ${periodTimeStart}, ending ${periodTimeEnd} based on time of ${now}. Static period: ${atomicState.staticPeriod}")
    
    return getPercentThroughPeriod(periodTimeStart, periodTimeEnd, now)
}

def getPercentThroughPeriod(periodTimeStart, periodTimeEnd, now) {
    float periodDuration = ( (float)(periodTimeEnd.time - periodTimeStart.time) / 1000 / 60 ).round()
    float durationPassed = ( (float)(now.time - periodTimeStart.time) / 1000 / 60 ).round()
    float percentThroughPeriod = (durationPassed / periodDuration).round(2)

    log("${app.label}: Period duration (min) ${periodDuration}, Duration passed (min) ${durationPassed}. Currently ${(percentThroughPeriod * 100).round()}% through period")

    return percentThroughPeriod
}

def activateBulbStates() {
    log("${app.label}: Activating bulb states")
    
    if ((hueZones) && ((atomicState.periodIndex < atomicState.periodIndexStartColorCTDivergence) || (atomicState.altStateBright))) {
        for (hueZone in hueZones) {
            setCBulb(hueZone)
        }
    }
    else {
        for (bulbC in bulbsC) {
            setCBulb(bulbC)
        }
        for (bulbCT in bulbsCT) {
            setCTBulb(bulbCT)
        }
    }
}

def setCBulb(bulbC) {
    log("${app.label}: ${bulbC} Checking states for a Color Bulb update")
    
    def currLvl, currColorMode, currCT, currHue, currSat
    def colorModeWasReset = false

    if (atomicState.periodIndex < atomicState.periodIndexStartColorCTDivergence || atomicState.altStateBright) {
        currLvl = bulbC.currentValue("level")
        log("${app.label}: ${bulbC} Current bulb level: ${currLvl}%")
        if (currLvl != atomicState.lvlCT) {
            bulbC.setLevel(atomicState.lvlCT)
            log("${app.label}: ${bulbC} Level now changed to: ${atomicState.lvlCT}%")
        } else {
            log("${app.label}: ${bulbC} Bulb already at proper level")
        }

        currColorMode = bulbC.currentValue("colorMode")
        log("${app.label}: ${bulbC} Current bulb color mode: ${currColorMode}")
        if (currColorMode != "CT") {
            bulbC.updateSetting("colorMode", "CT")
            colorModeWasReset = true
            log("${app.label}: ${bulbC} Color mode now changed to: CT")
        } else {
            log("${app.label}: ${bulbC} Bulb already at proper color mode")
        }

        currCT = bulbC.currentValue("colorTemperature")
        log("${app.label}: ${bulbC} Current bulb CT: ${currCT}K")
        if (currCT != atomicState.temp || colorModeWasReset) {
            bulbC.setColorTemperature(atomicState.temp)
            log("${app.label}: ${bulbC} CT now changed to: ${atomicState.temp}K")
        } else {
            log("${app.label}: ${bulbC} Bulb already at proper color temperature")
        }
    } else {

        currLvl = bulbC.currentValue("level")
        log("${app.label}: ${bulbC} Current bulb level: ${currLvl}%")
        if (currLvl != atomicState.lvlC) {
            bulbC.setLevel(atomicState.lvlC)
            log("${app.label}: ${bulbC} Level now changed to: ${atomicState.lvlC}%")
        } else {
            log("${app.label}: ${bulbC} Bulb already at proper level")
        }

        currColorMode = bulbC.currentValue("colorMode")
        log("${app.label}: ${bulbC} Current bulb Color Mode: ${currColorMode}")
        if (bulbC.currentValue("colorMode") != "RGB") {
            bulbC.updateSetting("colorMode", "RGB")
            colorModeWasReset = true
            log("${app.label}: ${bulbC} Color Mode now changed to: RGB")
        } else {
            log("${app.label}: ${bulbC} Bulb already at proper color mode")
        }

        currHue = bulbC.currentValue("hue")
        log("${app.label}: ${bulbC} Current bulb hue: ${currHue}")
        if (currHue != atomicState.hue || colorModeWasReset) {
            bulbC.setHue(atomicState.hue)
            log("${app.label}: ${bulbC} hue now changed to: ${atomicState.hue}")
        } else {
            log("${app.label}: ${bulbC} Bulb already at proper hue")
        }

        currSat = bulbC.currentValue("saturation")
        log("${app.label}: ${bulbC} Current bulb saturation: ${currSat}")
        if (currSat != atomicState.sat || colorModeWasReset) {
            bulbC.setSaturation(atomicState.sat)
            log("${app.label}: ${bulbC} saturation now changed to: ${atomicState.sat}")
        } else {
            log("${app.label}: ${bulbC} Bulb already at proper saturation")
        }
    }
}

def setCTBulb(bulbCT) {
    log("${app.label}: ${bulbCT} Checking states for a CT Bulb update")
    
    def currLvl, currCT

    if (atomicState.periodIndex <= atomicState.periodIndexStartColorCTDivergence) {

        currLvl = bulbCT.currentValue("level")
        log("${app.label}: ${bulbCT} Current bulb level: ${currLvl}%")
        if (currLvl != atomicState.lvlCT) {
            bulbCT.setLevel(atomicState.lvlCT)
            log("${app.label}: ${bulbCT} Level now changed to: ${atomicState.lvlCT}%")
        } else {
            log("${app.label}: ${bulbCT} Bulb already at proper level")
        }

        currCT = bulbCT.currentValue("colorTemperature")
        log("${app.label}: ${bulbCT} Current bulb CT: ${currCT}K")
        if (currCT != atomicState.temp) {
            bulbCT.setColorTemperature(atomicState.temp)
            log("${app.label}: ${bulbCT} CT now changed to: ${atomicState.temp}K")
        } else {
            log("${app.label}: ${bulbCT} Bulb already at proper color temperature")
        }
    } else {
        bulbCT.off()
        log("${app.label}: ${bulbCT} CT bulb was switched off")
    }
}

def correctBulbs() {
    log("${app.label}: Correcting bulbs to the following states - atomicState.lvlCT: ${atomicState.lvlCT}, atomicState.temp: ${atomicState.temp}, atomicState.lvlC: ${atomicState.lvlC}, atomicState.hue: ${atomicState.hue}, atomicState.sat: ${atomicState.sat}, ")
    
    for (bulbCT in bulbsCT) {
        correctCTBulb(bulbCT)
    }
    for (bulbC in bulbsC) {
        correctCBulb(bulbC)
    }
}

def correctCTBulb(bulbCT) {
    log("${app.label}: ${bulbCT} Correcting CT Bulb state")

    if (bulbCT.currentValue("switch") == "on") {
        if (atomicState.periodIndex <= atomicState.periodIndexStartColorCTDivergence) {

            bulbCT.setLevel(atomicState.lvlCT)
            log("${app.label}: ${bulbCT} Level set to: ${atomicState.lvlCT}")

            bulbCT.setColorTemperature(atomicState.temp)
            log("${app.label}: ${bulbCT} CT set to: ${atomicState.temp}")
            
        } else {
            bulbCT.off()
            log("${app.label}: ${bulbCT} CT bulb was switched off")
        }
    } else {
        log("${app.label}: ${bulbCT} Bulb not on")
    }
}

def correctCBulb(bulbC) {
    log("${app.label}: ${bulbC} Correcting Color Bulb state")

    if (bulbC.currentValue("switch") == "on") {
        if (atomicState.periodIndex < atomicState.periodIndexStartColorCTDivergence) {

            bulbC.setLevel(atomicState.lvlCT)
            log("${app.label}: ${bulbC} Level set to: ${atomicState.lvlCT}")

            bulbC.updateSetting("colorMode", "CT")
            log("${app.label}: ${bulbC} Color mode set to: CT")

            bulbC.setColorTemperature(atomicState.temp)
            log("${app.label}: ${bulbC} CT set to: ${atomicState.temp}")
            
        } else {

            bulbC.setLevel(atomicState.lvlC)
            log("${app.label}: ${bulbC} Level set to: ${atomicState.lvlC}")

            bulbC.updateSetting("colorMode", "RGB")
            log("${app.label}: ${bulbC} Color Mode set to: RGB")

            bulbC.setHue(atomicState.hue)
            log("${app.label}: ${bulbC} hue set to: ${atomicState.hue}")

            bulbC.setSaturation(atomicState.sat)
            log("${app.label}: ${bulbC} saturation set to: ${atomicState.sat}")
        }
    } else {
        log("${app.label}: ${bulbC} Bulb not on")
    }
}

def log(debugText) {
    if (settings.log == true) {
        log.info "${app.name}: ${debugText}"
    }
}
