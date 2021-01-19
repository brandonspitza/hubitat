/*

    Hubitat Natrualight Room (Child) v2.0
    Author: Brandon Spitza


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
            input "btnOnOff", "capability.pushableButton", title: "Select Button to Turn Lights On/Off:", multiple: true, required: true
            input "btnNumOnOffBtnOn", "int", title: "Select Button Press Number to Turn Lights On:", required: true
            input "btnNumOnOffBtnOff", "int", title: "Select Button Press Number to Turn Lights Off", required: true
            paragraph ""
            paragraph ""
            paragraph "Select bulbs to be controlled. The ideal setup leverages the Hue Bridge Integration app where Hue Zones and Groups are synced from the Hue Bridge " + 
                "(individual bulbs do not need to be synced from the bridge in this scenario). A room would consist of a single Hue Zone containing both a Hue " +
                "Group of color bulbs and a Hue Group of color-temperature bulbs. The Room will still work with any combination of Zones, Groups, Color or CT bulbs."
            paragraph ""
            input "hueZones", "capability.colorTemperature", title: "Select Hue Zones:", multiple: true, required: false
            input "bulbsCT", "capability.colorTemperature", title: "Select CT Bulbs:", multiple: true, required: false
            input "bulbsC", "capability.colorControl", title: "Select Color Bulbs:", multiple: true, required: false
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
            input "appButtonPause", "button", title: "Un/Pause"
            if (state.paused) { paragraph "***APP CURRENTLY PAUSED***" }
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
            input "strTimeMornWarm", "time", title: "Warm Morning Start:", required: false
            paragraph "Home setting: ${parent.getTimeMornWarm().substring(11,16)}"
            paragraph ""
            paragraph ""
            input "strTimeMornCold", "time", title: "Cold Morning Start:", required: false
            paragraph "Home setting: ${parent.getTimeMornCold().substring(11,16)}"
            paragraph ""
            paragraph ""
            input "strTimeDay", "time", title: "Day (Static) Start:", required: false
            paragraph "Home setting: ${parent.getTimeDay().substring(11,16)}"
            paragraph ""
            paragraph ""
            input "strTimeAfternoon", "time", title: "Afternoon Start:", required: false
            paragraph "Home setting: ${parent.getTimeAfternoon().substring(11,16)}"
            paragraph ""
            paragraph ""
            input "strTimeAfternoonStatic", "time", title: "Afternoon (Static) Start:", required: false
            paragraph "Home setting: ${parent.getTimeAfternoonStatic().substring(11,16)}"
            paragraph ""
            paragraph ""
            input "strTimeEveningCT", "time", title: "Early Evening Start:", required: false
            paragraph "Home setting: ${parent.getTimeEveningCT().substring(11,16)}"
            paragraph ""
            paragraph ""
            input "strTimeEveningC", "time", title: "Late Evening Start:", required: false
            paragraph "Home setting: ${parent.getTimeEveningC().substring(11,16)}"
            paragraph ""
            paragraph ""
            input "strTimeNight", "time", title: "Night (Static) Start:", required: false
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
            input "tempWarmest", "int", title: "Warmest CT:", required: false
            paragraph "Home setting: ${parent.getTempWarmest()}"
            paragraph ""
            paragraph ""
            input "tempColdest", "int", title: "Coldest CT:", required: false
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
            input "lvlInitWarmMorn", "int", title: "Warm Morning Period, Initial:", required: false
            paragraph "Home setting: ${parent.getLvlInitWarmMorn()}"
            paragraph ""
            paragraph ""
            input "lvlGoalWarmMorn", "int", title: "Warm Morning Period, Goal:", required: false
            paragraph "Home setting: ${parent.getLvlGoalWarmMorn()}"
            paragraph ""
            paragraph ""
            paragraph ""
            paragraph ""
            input "lvlGoalColdMorn", "int", title: "Cold Morning Period, Goal:", required: false
            paragraph "Home setting: ${parent.getLvlGoalColdMorn()}"
            paragraph ""
            paragraph ""
            paragraph ""
            paragraph ""
            paragraph ""
            input "lvlGoalDay", "int", title: "Day Period, Goal:", required: false
            paragraph "Home setting: ${parent.getLvlGoalDay()}"
            paragraph ""
            paragraph ""
            paragraph ""
            paragraph ""
            input "lvlGoalAfternoon", "int", title: "Afternoon Period, Goal:", required: false
            paragraph "Home setting: ${parent.getLvlGoalAfternoon()}"
            paragraph ""
            paragraph ""
            paragraph ""
            paragraph ""
            input "lvlCTGoalEveningCT", "int", title: "Early Evening Period, CT Bulb Goal:", required: false
            paragraph "Home setting: ${parent.getLvlCTGoalEveningCT()}"
            paragraph ""
            paragraph ""
            input "lvlCGoalEveningCT", "int", title: "Early Evening Period, Color Bulb Goal:", required: false
            paragraph "Home setting: ${parent.getLvlCGoalEveningCT()}"
            paragraph ""
            paragraph ""
            paragraph ""
            paragraph ""
            input "lvlCGoalEveningC", "int", title: "Late Evening Period, Color Bulb Goal:", required: false
            paragraph "Home setting: ${parent.getLvlCGoalEveningC()}"
            paragraph ""
            paragraph ""
            paragraph ""
            paragraph ""
            input "lvlCGoalNight", "int", title: "Night Period, Color Bulb Goal:", required: false
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
            input "hueInitEveningCT", "int", title: "Early Evening Period, Color Bulb Initial:", required: false
            paragraph "Home setting: ${parent.getHueInitEveningCT()}"
            paragraph ""
            paragraph ""
            input "hueGoalEveningCT", "int", title: "Early Evening Period, Color Bulb Goal:", required: false
            paragraph "Home setting: ${parent.getHueGoalEveningCT()}"
            paragraph ""
            paragraph ""
            input "hueGoalEveningC", "int", title: "Late Evening Period, Color Bulb Goal:", required: false
            paragraph "Home setting: ${parent.getHueGoalEveningC()}"
            paragraph ""
            paragraph ""
            input "hueGoalNight", "int", title: "Night Period, Color Bulb Hue Goal:", required: false
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
            input "satInitEveningCT", "int", title: "Early Evening Period, Color Bulb Initial:", required: false
            paragraph "Home setting: ${parent.getSatInitEveningCT()}"
            paragraph ""
            paragraph ""
            input "satGoalEveningCT", "int", title: "Early Evening Period, Color Bulb Goal:", required: false
            paragraph "Home setting: ${parent.getSatGoalEveningCT()}"
            paragraph ""
            paragraph ""
            paragraph ""
            paragraph ""
            input "satGoalEveningC", "int", title: "Late Evening Period, Color Bulb Saturation Goal:", required: false
            paragraph "Home setting: ${parent.getSatGoalEveningC()}"
            paragraph ""
            paragraph ""
            paragraph ""
            paragraph ""
            input "satGoalNight", "int", title: "Night Period, Color Bulb Goal:", required: false
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
            input "btnAltStateBright", "capability.holdableButton", title: "Select Button to Activate Alternate Bright Lighting:", multiple: true, required: false
            paragraph ""
            input "btnPressNumAltStateBright", "int", title: "Select Button Number to PRESS to Activate Alternate Bright Lighting:", required: false
            input "btnHoldNumAltStateBright", "int", title: "Select Button Number to HOLD to Activate Alternate Bright Lighting:", required: false
            paragraph ""
            paragraph ""
            paragraph "Leave one or both of the fields below blank to default to global settings from Home (parent) app. An input overrides that specific Home parameter while " +
                "others will still default. Home setting are displayed below the input line for reference."
            paragraph ""
            input "lvlAltStateBright", "int", title: "Alternate Bright Lighting Level:", required: false
            paragraph "Home setting: ${parent.getLvlAltStateBright()}"
            paragraph ""
            paragraph ""
            input "tempAltStateBright", "int", title: "Alternate Bright Lighting Temperature:", required: false
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
            input "btnAltStateDim", "capability.holdableButton", title: "Select Button to Activate Alternate Dim Lighting:", multiple: true, required: false
            paragraph ""
            input "btnPressNumAltStateDim", "int", title: "Select Button Number to PRESS to Activate Alternate Dim Lighting:", required: false
            input "btnHoldNumAltStateDim", "int", title: "Select Button Number to HOLD to Activate Alternate Dim Lighting:", required: false
            paragraph ""
            paragraph ""
            paragraph "Leave one or both of the fields below blank to default to global settings from Home (parent) app. An input overrides that specific Home parameter while " +
                "others will still default. Home setting are displayed below the input line for reference."
            paragraph ""
            input "lvlAltStateDim", "int", title: "Alternate Dim Lighting Level:", required: false
            paragraph "Home setting: ${parent.getLvlAltStateDim()}"
            paragraph ""
            paragraph ""
            input "tempAltStateDim", "int", title: "Alternate Dim Lighting Temperature:", required: false
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
    
    state.paused = false
    state.on = isOn()
    state.altStateBright = false
    state.altStateDim = false
    state.period
    state.periodIndex
    state.periodIndexStartColorCTDivergence = 6
    state.lvlCT
    state.temp
    state.lvlC
    state.hue
    state.sat
    state.staticPeriod
    
    log("${app.label}: state.on: ${state.on}, " +
        "state.toggleButton: ${state.toggleButton}, " +
        "state.periodIndexStartColorCTDivergence: ${state.periodIndexStartColorCTDivergence}, ")
    
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
    if (!state.paused) {
        log("${app.label} app paused")
        state.paused = true
    } else {
        log("${app.label} app unpaused")
        state.paused = false
    }
}

def onBtnHandler(evt) {
    log("${app.label}: On Button Press - Turning On Bulbs")
    
    state.on = true
    
    if (state.periodIndex <= state.periodIndexStartColorCTDivergence || (state.altStateBright)) {
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
    
    state.on = false
    state.altStateDim = false
    state.altStateBright = false
    
    if (state.periodIndex <= state.periodIndexStartColorCTDivergence) {
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
    if (!state.on) {
        log("${app.label}: Toggle Button Press. Bulbs currently off. Turning On.")
        onBtnHandler(evt)
    }
    else if (state.on) {
        log("${app.label}: Toggle Button Press. Bulbs currently on. Turning Off.")
        offBtnHandler(evt)
    }
}

def btnAltStateBrightHandler(evt) {
    log("${app.label}: Alternate Bright Lighting button pressed")
    if (!state.altStateBright) {
        log("${app.label}: Alternate Bright Lighting activated")
        state.altStateBright = true
        state.altStateDim = false
        onBtnHandler()
    }
    else if (state.altStateBright) {
        log("${app.label}: Alternate Bright Lighting deactivated")
        state.altStateBright = false
        driver(new Date())
    }
}

def btnAltStateDimHandler(evt) {
    log("${app.label}: Alternate Dim Lighting button pressed")
    if (!state.altStateDim) {
        log("${app.label}: Alternate Dim Lighting activated")
        state.altStateDim = true
        state.altStateBright = false
    }
    else if (state.altStateDim) {
        log("${app.label}: Alternate Dim Lighting deactivated")
        state.altStateDim = false
    }
    driver(new Date())
}




def driver(now) {
    if (!state.on) {
        log("${app.label}: lights are off. No update needed.")
        return
    }
    if (state.paused) {
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
    

    if (state.altStateBright) {
        log("${app.label}: Alternate Bright Lighting active. Setting CT Bulb level to ${lvlAltStateBright}%")
        state.lvlCT = Integer.parseInt(lvlAltStateBright)
        
        log("${app.label}: Alternate Bright Lighting active. Setting Bulb temperature to ${tempAltStateBright}K")
        state.temp = Integer.parseInt(tempAltStateBright)
        
        return
    }
    
    //state.altStateDim processed last
    
    if (state.period == "WarmMorning") {
        
        lvlCTInit = Float.parseFloat(lvlInitWarmMorn)
        lvlCTGoal = Float.parseFloat(lvlGoalWarmMorn)
        state.lvlCT = getWeightedStateVal(lvlCTInit, lvlCTGoal, percentThroughPeriod)
        
        tempInit = Float.parseFloat(tempWarmest)
        tempGoal = Float.parseFloat(tempColdest)
        state.temp = getWeightedStateVal(tempInit, tempGoal, percentThroughPeriod)
        
    } else if (state.period == "ColdMorning") {
        
        lvlCTInit = Float.parseFloat(lvlGoalWarmMorn)
        lvlCTGoal = Float.parseFloat(lvlGoalColdMorn)
        state.lvlCT = getWeightedStateVal(lvlCTInit, lvlCTGoal, percentThroughPeriod)
        
        tempInit = Float.parseFloat(tempColdest)
        tempGoal = Float.parseFloat(tempColdest)
        state.temp = getWeightedStateVal(tempInit, tempGoal, percentThroughPeriod)
        
    } else if (state.period == "Day") {

        lvlCTInit = Float.parseFloat(lvlGoalColdMorn)
        lvlCTGoal = Float.parseFloat(lvlGoalDay)
        state.lvlCT = getWeightedStateVal(lvlCTInit, lvlCTGoal, percentThroughPeriod)
        
        state.temp = Float.parseFloat(tempColdest)
        
    } else if (state.period == "Afternoon") {
        
        lvlCTInit = Float.parseFloat(lvlGoalDay)
        lvlCTGoal = Float.parseFloat(lvlGoalAfternoon)
        state.lvlCT = getWeightedStateVal(lvlCTInit, lvlCTGoal, percentThroughPeriod)
        
        tempInit = Float.parseFloat(tempColdest)
        tempGoal = Float.parseFloat(tempWarmest)
        state.temp = getWeightedStateVal(tempInit, tempGoal, percentThroughPeriod)
        
    } else if (state.period == "AfternoonStatic") {
        
        state.lvlCT = Float.parseFloat(lvlGoalAfternoon)
        
        state.temp = Float.parseFloat(tempWarmest)
        
    } else if (state.period == "EveningCT") {
        
        lvlCTInit = Float.parseFloat(lvlGoalAfternoon)
        lvlCTGoal = Float.parseFloat(lvlCTGoalEveningCT)
        state.lvlCT = getWeightedStateVal(lvlCTInit, lvlCTGoal, percentThroughPeriod)
        
        state.temp = Float.parseFloat(tempWarmest)
        
        lvlCInit = Float.parseFloat(lvlGoalAfternoon)
        lvlCGoal = Float.parseFloat(lvlCGoalEveningCT)
        state.lvlC = getWeightedStateVal(lvlCInit, lvlCGoal, percentThroughPeriod)
        
        hueInit = Float.parseFloat(hueInitEveningCT)
        hueGoal = Float.parseFloat(hueGoalEveningCT)
        state.hue = getWeightedStateVal(hueInit, hueGoal, percentThroughPeriod)
        
        satInit = Float.parseFloat(satInitEveningCT)
        satGoal = Float.parseFloat(satGoalEveningCT)
        state.sat = getWeightedStateVal(satInit, satGoal, percentThroughPeriod)
        
    } else if (state.period == "EveningC") {
        
        lvlCInit = Float.parseFloat(lvlCGoalEveningCT)
        lvlCGoal = Float.parseFloat(lvlCGoalEveningC)
        state.lvlC = getWeightedStateVal(lvlCInit, lvlCGoal, percentThroughPeriod)
        
        hueInit = Float.parseFloat(hueInitEveningCT)
        hueGoal = Float.parseFloat(hueGoalEveningC)
        state.hue = getWeightedStateVal(hueInit, hueGoal, percentThroughPeriod)
        
        satInit = Float.parseFloat(satInitEveningCT)
        satGoal = Float.parseFloat(satGoalEveningC)
        state.sat = getWeightedStateVal(satInit, satGoal, percentThroughPeriod)
        
    } else if (state.period == "Night") {
        
        state.lvlC = Float.parseFloat(lvlCGoalNight)
        
        state.hue = Float.parseFloat(hueGoalNight)
        
        state.sat = Float.parseFloat(satGoalNight)
        
    }
    
    if (state.altStateDim == true) {
        if (state.periodIndex <= state.periodIndexStartColorCTDivergence) {
            if (state.lvlCT > Integer.parseInt(lvlAltStateDim)) {
                log("${app.label}: Alternate Dim Lighting active. Setting CT Bulb level to ${lvlAltStateDim}%.")
                state.lvlCT = Integer.parseInt(lvlAltStateDim)
            }
            log("${app.label}: Alternate Dim Lighting active. Setting Bulb temperature to ${tempAltStateDim}K")
            state.temp = Integer.parseInt(tempAltStateDim)
        }
        if (state.periodIndex >= state.periodIndexStartColorCTDivergence) {
            log("${app.label}: Alternate Dim Lighting active. Setting Color Bulb level to ${lvlAltStateDim}%.")
            state.lvlC = Integer.parseInt(lvlAltStateDim)
        }
    }
    
    log("${app.label}: state.lvlCT: ${state.lvlCT}, state.temp: ${state.temp}, state.lvlC: ${state.lvlC}, state.hue: ${state.hue}, state.sat: ${state.sat}, ")
}

def getWeightedStateVal(init, goal, percentThroughPeriod) {
    return ( (float)(init + (goal - init) * percentThroughPeriod) ).round()
}

def setPeriod(now) {
    log("${app.label}: Setting period")
    def periodTimeStart, periodTimeEnd
    
    if (timeOfDayIsBetween(timeMornWarm, timeMornCold, now)) {
        state.period = "WarmMorning"
        state.periodIndex = 1
        state.staticPeriod = false
        periodTimeStart = timeMornWarm
        periodTimeEnd = timeMornCold
    } else if (timeOfDayIsBetween(timeMornCold, timeDay, now)) {
        state.period = "ColdMorning"
        state.periodIndex = 2
        state.staticPeriod = false
        periodTimeStart = timeMornCold
        periodTimeEnd = timeDay
    } else if (timeOfDayIsBetween(timeDay, timeAfternoon, now)) {
        state.period = "Day"
        state.periodIndex = 3
        state.staticPeriod = true
        periodTimeStart = timeDay
        periodTimeEnd = timeAfternoon
    } else if (timeOfDayIsBetween(timeAfternoon, timeAfternoonStatic, now)) {
        state.period = "Afternoon"
        state.periodIndex = 4
        state.staticPeriod = false
        periodTimeStart = timeAfternoon
        periodTimeEnd = timeAfternoonStatic
    } else if (timeOfDayIsBetween(timeAfternoonStatic, timeEveningCT, now)) {
        state.period = "AfternoonStatic"
        state.periodIndex = 5
        state.staticPeriod = true
        periodTimeStart = timeAfternoonStatic
        periodTimeEnd = timeEveningCT
    } else if (timeOfDayIsBetween(timeEveningCT, timeEveningC, now)) {
        state.period = "EveningCT"
        state.periodIndex = 6
        state.staticPeriod = false
        periodTimeStart = timeEveningCT
        periodTimeEnd = timeEveningC
    } else if (timeOfDayIsBetween(timeEveningC, timeNight, now)) {
        state.period = "EveningC"
        state.periodIndex = 7
        state.staticPeriod = false
        periodTimeStart = timeEveningC
        periodTimeEnd = timeNight
    } else if (timeOfDayIsBetween(timeNight, timeMornWarm.plus(1), now)) {
        state.period = "Night"
        state.periodIndex = 8
        state.staticPeriod = true
        periodTimeStart = timeNight
        periodTimeEnd = timeMornWarm.plus(1)
    } else if (timeOfDayIsBetween(timeNight.minus(1), timeMornWarm, now)) {
        state.period = "Night"
        state.periodIndex = 9
        state.staticPeriod = true
        periodTimeStart = timeNight.minus(1)
        periodTimeEnd = timeMornWarm
    }

    log("${app.label}: Period ${state.period}, beginning ${periodTimeStart}, ending ${periodTimeEnd} based on time of ${now}. Static period: ${state.staticPeriod}")
    
    return getPercentThroughPeriod(periodTimeStart, periodTimeEnd, now)
}

def getPercentThroughPeriod(periodTimeStart, periodTimeEnd, now) {
    def periodDuration = ((float)(periodTimeEnd.time - periodTimeStart.time) / 1000 / 60).round()
    def durationPassed = ((float)(now.time - periodTimeStart.time) / 1000 / 60).round()
    def percentThroughPeriod = ((float)(durationPassed / periodDuration)).round(2)

    log("${app.label}: Period duration (min) ${periodDuration}, Duration passed (min) ${durationPassed}. Currently ${(percentThroughPeriod * 100).round()}% through period")

    return percentThroughPeriod
}

def activateBulbStates() {
    log("${app.label}: Activating bulb states")
    
    if ((hueZones) && ((state.periodIndex < state.periodIndexStartColorCTDivergence) || (state.altStateBright))) {
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

    if (state.periodIndex < state.periodIndexStartColorCTDivergence || state.altStateBright) {
        currLvl = bulbC.currentValue("level")
        log("${app.label}: ${bulbC} Current bulb level: ${currLvl}%")
        if (currLvl != state.lvlCT) {
            bulbC.setLevel(state.lvlCT)
            log("${app.label}: ${bulbC} Level now changed to: ${state.lvlCT}%")
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
        if (currCT != state.temp || colorModeWasReset) {
            bulbC.setColorTemperature(state.temp)
            log("${app.label}: ${bulbC} CT now changed to: ${state.temp.round()}K")
        } else {
            log("${app.label}: ${bulbC} Bulb already at proper color temperature")
        }
    } else {

        currLvl = bulbC.currentValue("level")
        log("${app.label}: ${bulbC} Current bulb level: ${currLvl}%")
        if (currLvl != state.lvlC) {
            bulbC.setLevel(state.lvlC)
            log("${app.label}: ${bulbC} Level now changed to: ${state.lvlC}%")
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
        if (currHue != state.hue || colorModeWasReset) {
            bulbC.setHue(state.hue)
            log("${app.label}: ${bulbC} hue now changed to: ${state.hue}")
        } else {
            log("${app.label}: ${bulbC} Bulb already at proper hue")
        }

        currSat = bulbC.currentValue("saturation")
        log("${app.label}: ${bulbC} Current bulb saturation: ${currSat}")
        if (currSat != state.sat || colorModeWasReset) {
            bulbC.setSaturation(state.sat)
            log("${app.label}: ${bulbC} saturation now changed to: ${state.sat}")
        } else {
            log("${app.label}: ${bulbC} Bulb already at proper saturation")
        }
    }
}

def setCTBulb(bulbCT) {
    log("${app.label}: ${bulbCT} Checking states for a CT Bulb update")
    
    def currLvl, currCT

    if (state.periodIndex <= state.periodIndexStartColorCTDivergence) {

        currLvl = bulbCT.currentValue("level")
        log("${app.label}: ${bulbCT} Current bulb level: ${currLvl}%")
        if (currLvl != state.lvlCT) {
            bulbCT.setLevel(state.lvlCT)
            log("${app.label}: ${bulbCT} Level now changed to: ${state.lvlCT}%")
        } else {
            log("${app.label}: ${bulbCT} Bulb already at proper level")
        }

        currCT = bulbCT.currentValue("colorTemperature")
        log("${app.label}: ${bulbCT} Current bulb CT: ${currCT}K")
        if (currCT != state.temp) {
            bulbCT.setColorTemperature(state.temp)
            log("${app.label}: ${bulbCT} CT now changed to: ${state.temp.round()}K")
        } else {
            log("${app.label}: ${bulbCT} Bulb already at proper color temperature")
        }
    } else {
        bulbCT.off()
        log("${app.label}: ${bulbCT} CT bulb was switched off")
    }
}

def correctBulbs() {
    log("${app.label}: Correcting bulbs to the following states - state.lvlCT: ${state.lvlCT}, state.temp: ${state.temp}, state.lvlC: ${state.lvlC}, state.hue: ${state.hue}, state.sat: ${state.sat}, ")
    
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
        if (state.periodIndex <= state.periodIndexStartColorCTDivergence) {

            bulbCT.setLevel(state.lvlCT)
            log("${app.label}: ${bulbCT} Level set to: ${state.lvlCT}")

            bulbCT.setColorTemperature(state.temp)
            log("${app.label}: ${bulbCT} CT set to: ${state.temp}")
            
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
        if (state.periodIndex < state.periodIndexStartColorCTDivergence) {

            bulbC.setLevel(state.lvlCT)
            log("${app.label}: ${bulbC} Level set to: ${state.lvlCT}")

            bulbC.updateSetting("colorMode", "CT")
            log("${app.label}: ${bulbC} Color mode set to: CT")

            bulbC.setColorTemperature(state.temp)
            log("${app.label}: ${bulbC} CT set to: ${state.temp}")
            
        } else {

            bulbC.setLevel(state.lvlC)
            log("${app.label}: ${bulbC} Level set to: ${state.lvlC}")

            bulbC.updateSetting("colorMode", "RGB")
            log("${app.label}: ${bulbC} Color Mode set to: RGB")

            bulbC.setHue(state.hue)
            log("${app.label}: ${bulbC} hue set to: ${state.hue}")

            bulbC.setSaturation(state.sat)
            log("${app.label}: ${bulbC} saturation set to: ${state.sat}")
        }
    } else {
        log("${app.label}: ${bulbC} Bulb not on")
    }
}

def log(debugText) {
    if (settings.log) {
        log.info "${app.name}: ${debugText}"
    }
}
