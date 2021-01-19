/*

    Hubitat Natrualight Home (Parent) v2.0
    Author: Brandon Spitza


    To Do:
        -Validate user inputs
        -Collect least "next run time" from children and don't run until it for static periods

*/

definition(
    name: "Naturalight Home",
    namespace: "naturalight",
    author: "Brandon Spitza",
    importURL: "https://raw.githubusercontent.com/brandonspitza/hubitat/main/Apps/Naturalight/Naturalight%20Home%20(Parent).groovy",
    description: "Configure natural daylight tones indoors with global home settings and optional refinements per room.",
    category: "My Apps",
    iconUrl: "",
    iconX2Url: "",
    singleInstance: true
)

preferences {
    page(name: "welcomePage", title: "Welcome Page", nextPage: "appPage", uninstall: true)
    page(name: "appPage", title: "App Page", nextPage: "timePage", uninstall: true) {
        section("Select a button that, upon pressing, will correct any bulbs that have gone out of sync with their Group or Zone (if, for example, its " +
                "lamp was momentarily unplugged). Consider a virtual button on a dashboard.") {
            input "correctBulbStatesBtn", "capability.pushableButton", title: "Select Button to Correct Bulb States:", multiple: false, required: false
            paragraph ""
            paragraph ""
        }
        section("Set bulb update frequency. This is how often the app will check for and update bulb lighting settings. 3-4 minutes is recommended to " +
                "reduce the perception of lighting changes.") {
            input "updateFreq", "int", title: "Update Frequncy (min):", defaultValue: 3, required: true
            paragraph ""
            paragraph ""
        }
        section("Debugging:") {
            input(name: "log", type: "bool", title: "Log settings and actions?",
                  description: "Log settings and actions.", defaultValue: false,
                  required: true, displayDuringSetup: true)
            paragraph ""
        }
    }
    page(name: "timePage", title: "Period Time Ranges", nextPage: "tempPage", uninstall: true) {
        section("Each time period fades into the next, so the Warm Morning bulb settings will be 100% active at the Warm Morning start time. As time goes on, " +
                "the lighting fades to the Cold Morning settings. Eventually the Cold Morning settings are 100% active at the Cold Morning start time. " +
                "Beginning at Cold Morning period's start time, its settings steadily fade into the Day period's settings, completing at the Day period's start time, " +
                "where that period's settings arrive at 100% active.") {
            paragraph "Static periods do not fade; rather, they maintain their settings at 100% throughout. After a static period ends, the next period " +
                "begins with the previous period's settings and fades into the next period's settings, completing the transition by the start time of the next period."
            paragraph ""
            input "strTimeMornWarm", "time", title: "Warm Morning Start:", defaultValue: "2021-01-01T06:00:00.000-0800", required: true
            input "strTimeMornCold", "time", title: "Cold Morning Start:", defaultValue: "2021-01-01T06:45:00.000-0800", required: true
            input "strTimeDay", "time", title: "Day (Static) Start:", defaultValue: "2021-01-01T07:30:00.000-0800", required: true
            input "strTimeAfternoon", "time", title: "Early Afternoon Start:", defaultValue: "2021-01-01T15:30:00.000-0800", required: true
            input "strTimeAfternoonStatic", "time", title: "Late Afternoon (Static) Start:", defaultValue: "2021-01-01T17:30:00.000-0800", required: true
            input "strTimeEveningCT", "time", title: "Early Evening Start:", defaultValue: "2021-01-01T18:30:00.000-0800", required: true
            input "strTimeEveningC", "time", title: "Late Evening Start:", defaultValue: "2021-01-01T19:30:00.000-0800", required: true
            input "strTimeNight", "time", title: "Night (Static) Start:", defaultValue: "2021-01-01T22:15:00.000-0800", required: true
            paragraph ""
        }
    }
    page(name: "tempPage", title: "Color Temperature Range", nextPage: "levelPage", uninstall: true) {
        section("The warmest white temperature entered below will begin the Warm Morning period, fade to the coldest white temperature to start the Cold Morning period where " +
                "it will stay set through the Day period. Into the Afternoon periods, white temperature will shift slowly back to the warmest white.") {
            paragraph ""
            input "tempWarmest", "int", title: "Warmest CT:", defaultValue: 2500, required: true
            paragraph "Hue bulb warmest CT: 2203"
            paragraph ""
            paragraph ""
            input "tempColdest", "int", title: "Coldest CT:", defaultValue: 6300, required: true
            paragraph "Hue bulb coldest CT: 6536"
            paragraph ""
        }
    }
    page(name: "levelPage", title: "Lighting Levels", nextPage: "huePage", uninstall: true) {
        section("Lighting levels (brightnesses) will begin the Warm Morning period at the Warm Morning Initial Level and progress to the Goal Level. The Warm " +
                "Morning Goal Level will then be the level to start the Cold Morning period (effectively the Warm Morning Goal is the Cold Morning Initial). Through " +
                "the Cold Morning period, lighting levels will brighten, arriving at the Cold Morning Goal Level by the end of the Cold Morning period. In this way, " +
                "the previous period's goal state is the next period's initial state. This concept applies for all the bulb lighting settings - temperature, level, hue, and saturation.") {
            paragraph "During the Early Evening period, Color Temperature Bulb levels have a separate setting than Color Bulb levels, allowing CT bulbs to fade independently " +
                "of Color bulbs as overall lighting level needs decrease."
            paragraph ""
            input "lvlInitWarmMorn", "int", title: "Warm Morning Period, Initial:", defaultValue: 40, required: true
            paragraph ""
            input "lvlGoalWarmMorn", "int", title: "Warm Morning Period, Goal:", defaultValue: 60, required: true
            paragraph ""
            paragraph ""
            paragraph ""
            
            input "lvlGoalColdMorn", "int", title: "Cold Morning Period, Goal:", defaultValue: 100, required: true
            paragraph ""
            paragraph ""
            paragraph ""
            
            input "lvlGoalDay", "int", title: "Day Period, Goal:", defaultValue: 100, required: true
            paragraph ""
            paragraph ""
            paragraph ""
            
            input "lvlGoalAfternoon", "int", title: "Afternoon Period, Goal:", defaultValue: 100, required: true
            paragraph ""
            paragraph ""
            paragraph ""

            input "lvlCTGoalEveningCT", "int", title: "Early Evening Period, CT Bulb Goal:", defaultValue: 1, required: true
            paragraph ""
            input "lvlCGoalEveningCT", "int", title: "Early Evening Period, Color Bulb Goal:", defaultValue: 100, required: true
            paragraph ""
            paragraph ""
            paragraph ""

            input "lvlCGoalEveningC", "int", title: "Late Evening Period, Color Bulb Goal:", defaultValue: 50, required: true
            paragraph ""
            paragraph ""
            paragraph ""

            input "lvlCGoalNight", "int", title: "Night Period, Color Bulb Goal:", defaultValue: 10, required: true
            paragraph ""
        }
    }
    page(name: "huePage", title: "Hue Settings", nextPage: "satPage", uninstall: true) {
        section("Set bulb hue range for each period.") {
            paragraph ""
            input "hueInitEveningCT", "int", title: "Early Evening Period, Color Bulb Initial:", defaultValue: 11, required: true
            paragraph ""
            input "hueGoalEveningCT", "int", title: "Early Evening Period, Color Bulb Goal:", defaultValue: 8, required: true
            paragraph ""
            paragraph ""
            paragraph ""

            input "hueGoalEveningC", "int", title: "Late Evening Period, Color Bulb Goal:", defaultValue: 5, required: true
            paragraph ""
            paragraph ""
            paragraph ""

            input "hueGoalNight", "int", title: "Night Period, Color Bulb Hue Goal:", defaultValue: 2, required: true
            paragraph ""
        }
    }
    page(name: "satPage", title: "Saturation Settings", nextPage: "altBrightPage", uninstall: true) {
        section("Set bulb saturation range by period.") {
            paragraph ""
            input "satInitEveningCT", "int", title: "Early Evening Period, Color Bulb Initial:", defaultValue: 70, required: true
            paragraph ""
            input "satGoalEveningCT", "int", title: "Early Evening Period, Color Bulb Goal:", defaultValue: 75, required: true
            paragraph ""
            paragraph ""
            paragraph ""

            input "satGoalEveningC", "int", title: "Late Evening Period, Color Bulb Saturion Goal:", defaultValue: 100, required: true
            paragraph ""
            paragraph ""
            paragraph ""

            input "satGoalNight", "int", title: "Night Period, Color Bulb Goal:", defaultValue: 100, required: true
            paragraph ""
        }
    }
    page(name: "altBrightPage", title: "Alternate Bright State", nextPage: "altDimPage", uninstall: true) {
        section("For moments when you need full bright light, an Alternate Bright Lighting button (set per room in the Room app), will override current lighting " +
                "to the following settings until it is deactivated with a second button press/hold. Activating this state will turn on bulbs that are off. " +
                "Like all lighting values, these can be refined at the room level if desired.") {
            paragraph ""
            input "lvlAltStateBright", "int", title: "Alternate Bright Lighting Level:", defaultValue: 100, required: false
            paragraph ""
            input "tempAltStateBright", "int", title: "Alternate Bright Lighting Temperature:", defaultValue: 2500, required: false
            paragraph ""
        }
    }
    page(name: "altDimPage", title: "Alternate Dim State", nextPage: "childPage", uninstall: true) {
        section("For occasions when you need dimmer light (i.e. 'nap mode'), an Alternate Dim Lighting button (set per room in the Room app), will override current lighting " +
                "to the following settings until it is deactivated with a second button press/hold. Activating this state will NOT turn on bulbs that are off. " +
                "Like all lighting values, these can be refined at the room level if desired.") {
            paragraph "Alternate Dim Lighting differs from Alternate Bright Lighting in that the Color Temperature setting below only applies if the current time is within a " +
                "Color Temperature time period, i.e. prior to the Early Evening period. During Early Evening period and later, only the Lighting Level setting below applies while " +
                "the bulb's other current states (hue, saturation) are maintained."
            paragraph ""
            input "lvlAltStateDim", "int", title: "Alternate Dim Lighting Level:", defaultValue: 50, required: false
            paragraph ""
            input "tempAltStateDim", "int", title: "Alternate Dim Lighting Temperature:", defaultValue: 2500, required: false
            paragraph ""
        }
    }
    page(name: "childPage", title: "Add Rooms", install: true, uninstall: true) {
        section {
            paragraph ""
            app name: "childApps", appName: "Naturalight Room", namespace: "naturalight", title: "New Room", multiple: true
            paragraph ""
        }
    }
}

def welcomePage() {
    dynamicPage(name: "welcomePage") {
        section() {
            input "appButtonPause", "button", title: "Un/Pause"
            if (state.paused) { paragraph "***APP CURRENTLY PAUSED***" }
            paragraph ""
        }
        section("Welcome!") {
            paragraph "Naturalight divides the day into eight periods where one period slowly fades into the next, allowing your eyes to adjust with no abrupt changes, " +
                "reducing the perception of lighting changes. The design concept was as follows."
            paragraph "Early morning starts with warm white lighting, fades into cold white lighting as morning progresss, and transitions back to warm white lighting in the late " +
                "afternoon. In the evening, bulbs that are only Color Temperature-adjustable are dimmed separately while Color bulb lighting levels stay high as they are switched " +
                "to color mode and tinted slightly orange. As the evening continues, CT bulbs are switched off and Color bulbs become deeper orange."
            paragraph "This scheme provides you with strong blue-shifted light during the day to promote wakefulness and improve mood. During the evening, " +
                "your home's blue lighting is reduced and nearly eliminated, prompting your body to relax and increase melatonin production before you go to bed to aid in sleep quality. "
            paragraph "Naturalight allows you to intermingle Color bulbs with CT bulbs, which typically are half the cost. Why run 3 Color bulbs in a fixture at 30% " +
                "when you can run 1 Color bulb at 100% and turn the other two cheaper bulbs off?"
            paragraph "The Naturalight Home (parent) app sets global values that all Room (child) apps will import, so you only have to set everything up once for " +
                "the home. However, the room apps allow global settings to be overridden on a per-setting basis, allowing refinement per room."
            paragraph "Please note that in this version, user-inputted settings are not validated, so take care when entering. For example, your period " +
                "start times must progress sequentially or lighting adjustments will be erratic."
            paragraph ""
        }
    }
}




def installed() {
    unsubscribe()
    unschedule()
    initialize()
    log.debug "Installed with settings: ${settings}"
}

def updated() {
    unsubscribe()
    unschedule()
    initialize()
    log.debug "Updated with settings: ${settings}"
}

def initialize() {
    state.paused = false
    
    if (correctBulbStatesBtn) {
        subscribe(correctBulbStatesBtn, "pushed.1", correctBulbStatesBtnHandler)
    }
        
    def now = new Date()
    log("Initialization run ${now}")
    
    driver(now)
}

def getTimeMornWarm(){ return strTimeMornWarm }
def getTimeMornCold(){ return strTimeMornCold }
def getTimeDay(){ return strTimeDay }
def getTimeAfternoon(){ return strTimeAfternoon }
def getTimeAfternoonStatic(){ return strTimeAfternoonStatic }
def getTimeEveningCT(){ return strTimeEveningCT }
def getTimeEveningC(){ return strTimeEveningC }
def getTimeNight(){ return strTimeNight }

def getLvlAltStateBright() { return lvlAltStateBright}
def getTempAltStateBright() { return tempAltStateBright}
def getLvlAltStateDim() { return lvlAltStateDim}
def getTempAltStateDim() { return tempAltStateDim}
def getTempColdest(){ return tempColdest }
def getTempWarmest(){ return tempWarmest }
def getLvlInitWarmMorn(){ return lvlInitWarmMorn }
def getLvlGoalWarmMorn(){ return lvlGoalWarmMorn }
def getLvlGoalColdMorn(){ return lvlGoalColdMorn }
def getLvlGoalDay(){ return lvlGoalDay }
def getLvlGoalAfternoon(){ return lvlGoalAfternoon }
def getLvlCTGoalEveningCT(){ return lvlCTGoalEveningCT }
def getLvlCGoalEveningCT(){ return lvlCGoalEveningCT }
def getLvlCGoalEveningC(){ return lvlCGoalEveningC }
def getLvlCGoalNight(){ return lvlCGoalNight }
def getHueInitEveningCT(){ return hueInitEveningCT }
def getHueGoalEveningCT(){ return hueGoalEveningCT }
def getHueGoalEveningC(){ return hueGoalEveningC }
def getHueGoalNight(){ return hueGoalNight }
def getSatInitEveningCT(){ return satInitEveningCT }
def getSatGoalEveningCT(){ return satGoalEveningCT }
def getSatGoalEveningC(){ return satGoalEveningC }
def getSatGoalNight(){ return satGoalNight }

def driver(now) {
    updateFreq = Integer.parseInt(updateFreq)
    
    if (state.paused) {
        log("${app.label} app paused")
        log("Next state update ${updateFreq} minutes from now")
        runIn(updateFreq * 60, driver)
        return
    }
    
    if (!now) { now = new Date() }
    log("Scheduled bulb state update ${now}")
    
    setPeriodTimes()
    
    def children = getChildApps()
    for (child in children) {
        log("Updating ${child.label}")
        child.driver(now)
        pauseExecution(500)
    }
    
   log("Next state update ${updateFreq} minutes from now.")
   runIn(updateFreq * 60, driver)
}

def setPeriodTimes() {
    log("${app.label}: Setting period times")
    
    timeMornWarm = toDateTime(strTimeMornWarm)
    timeMornCold = toDateTime(strTimeMornCold)
    timeDay = toDateTime(strTimeDay)
    timeAfternoon = toDateTime(strTimeAfternoon)
    timeAfternoonStatic = toDateTime(strTimeAfternoonStatic)
    timeEveningCT = toDateTime(strTimeEveningCT)
    timeEveningC = toDateTime(strTimeEveningC)
    timeNight = toDateTime(strTimeNight)
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

def correctBulbStatesBtnHandler(evt) {
    log("Correcting Bulb States")
    childApps.each { child ->
        log("Correcting ${child.label}")
        child.correctBulbs()
    }
}

def log(debugText) {
    if (settings.log) {
        log.info "${app.name}: ${debugText}"
    }
}
