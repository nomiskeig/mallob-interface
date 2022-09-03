import requests
import sys
from os.path import exists
from urllib.parse import urlencode
from urllib.request import Request, urlopen
import socket
import json




#This class is purely cosmetical
class bcolors:
    HEADER = '\033[95m'
    OKBLUE = '\033[94m'
    OKCYAN = '\033[96m'
    OKGREEN = '\033[92m'
    WARNING = '\033[93m'
    FAIL = '\033[91m'
    ENDC = '\033[0m'
    BOLD = '\033[1m'
    UNDERLINE = '\033[4m'

    
commandLineArguments = sys.argv
ARG_1 = 0
ARG_2 = 1
ARG_3 = 2


#print the request json body before each request
PRINT_REQ_JSON_BODY = False
CATCH_REQUEST_ERROR = False
PRINT_RESPONSE_JSON = True

BASE_URL = "http://" + socket.gethostbyname(socket.gethostname() + ".local") + ":8080"
print(bcolors.OKGREEN + "Base-URL for API requests : "  + str(BASE_URL) + bcolors.ENDC)
SHOW_ALL_TESTS = "showAllTests"
SETTOKEN = "setToken"


#these are variabls, which are being set at runtime
#holds the current authentication-token
HEADER = None
CURRENT_ACTIVE_TOKEN = None
ALL_ACTIVE_USERS = []
CURRENT_ACTIVE_USER_INDEX = 0

LATEST_SAVED_JOB_ID = []
LATEST_SAVED_DESCRIPTION_ID = []

LATEST_STATUS_CODE = None

#---------------test-cases
REGISTER = "register"
LOGIN = "login"
JOBSUBMIT_INCLUDE = "submitJob_include"
GET_JOB_INFO = "getJobInfo"
SUBMIT_DESCRIPTION = "submitDescription"
DOWNLOAD_DESCRIPTION = "downloadDescription"
SUBMIT_JOB_EXTERNAL = "submitJob_external"
CANCEL_JOB = "cancelJob"
GET_SYSTEM_CONFIG = "getSystemConfig"
GET_MALLOB_INFO = "getMallobInfo"
GET_RESULT = "getResult"

#not yet implemented


AVAILABLE_TESTS = [REGISTER,
LOGIN,
JOBSUBMIT_INCLUDE,
GET_JOB_INFO,
SUBMIT_DESCRIPTION ,
DOWNLOAD_DESCRIPTION,
SUBMIT_JOB_EXTERNAL,
CANCEL_JOB ,
GET_SYSTEM_CONFIG ,
GET_MALLOB_INFO, 
GET_RESULT]

URL_MAPPINGS = {REGISTER : "/api/v1/users/register",
                LOGIN : "/api/v1/users/login",
                JOBSUBMIT_INCLUDE : "/api/v1/jobs/submit/inclusive",
                GET_JOB_INFO : "/api/v1/jobs/info",
                SUBMIT_DESCRIPTION : "/api/v1/jobs/submit/exclusive/description",
                DOWNLOAD_DESCRIPTION : "/api/v1/jobs/description",
                SUBMIT_JOB_EXTERNAL : "/api/v1/jobs/submit/exclusive/config",
                CANCEL_JOB : "/api/v1/jobs/cancel",
                GET_SYSTEM_CONFIG : "/api/v1/system/config",
                GET_MALLOB_INFO : "/api/v1/system/mallobInfo",
                GET_RESULT : "/api/v1/jobs/solution"}

AUTHENTICATION_MAPPINGS = {REGISTER : False,
                LOGIN : False,
                JOBSUBMIT_INCLUDE : True,
                GET_JOB_INFO : True,
                SUBMIT_DESCRIPTION : True,
                DOWNLOAD_DESCRIPTION : True,
                SUBMIT_JOB_EXTERNAL : True,
                CANCEL_JOB : True,
                GET_SYSTEM_CONFIG : True,
                GET_MALLOB_INFO : True,
                GET_RESULT : True
}



AFTER_REQUEST_FUNCTION_MAPPINGS = None
HELP_FUNCTION_MAPPINGS = None

def setAfterRequestFuncitons():
    global AFTER_REQUEST_FUNCTION_MAPPINGS
    global HELP_FUNCTION_MAPPINGS

    AFTER_REQUEST_FUNCTION_MAPPINGS = {
        REGISTER : noFunction,
        LOGIN : afterLogin,
        JOBSUBMIT_INCLUDE : afterJobInclude,
        GET_JOB_INFO : printResponse,
        SUBMIT_DESCRIPTION : afterDescriptionUpload,
        DOWNLOAD_DESCRIPTION : printResponse,
        SUBMIT_JOB_EXTERNAL : afterJobInclude,
        CANCEL_JOB : noFunction,
        GET_SYSTEM_CONFIG : printResponse,
        GET_MALLOB_INFO : printResponse,
        GET_RESULT : printResponse
    }

    HELP_FUNCTION_MAPPINGS = {
        REGISTER : register_help,
        LOGIN : login_help,
        JOBSUBMIT_INCLUDE : submitJob_descriptionIncluded_help,
        GET_JOB_INFO : getJobInfo_help,
        SUBMIT_DESCRIPTION : descriptionUpload_help,
        DOWNLOAD_DESCRIPTION : downloadDescription_help,
        SUBMIT_JOB_EXTERNAL : submitJob_descriptionExcluded_help,
        CANCEL_JOB : cancel_job_help,
        GET_SYSTEM_CONFIG : getConfig_help,
        GET_MALLOB_INFO : getMallobInfo_help,
        GET_RESULT : getResult_help
    }


#------------------------------------------------------after methods 
def noFunction(r):
    pass

def printResponseJSON(responseJSON):
    if PRINT_RESPONSE_JSON:
        print(json.dumps(responseJSON, indent=4))

def printResponse(r):
    responseJSON = convertRequestToJSON(r)
    if responseJSON == None:
        return
    printResponseJSON(responseJSON)


def afterLogin(request):
    global CURRENT_ACTIVE_TOKEN
    responseJSON = convertRequestToJSON(request)
    if responseJSON == None:
        return
    printResponseJSON(responseJSON)
    
    if LATEST_STATUS_CODE == 200:
        #set the global header variable;
        setHeader(responseJSON.get("token"))

def afterJobInclude(request):
    global LATEST_SAVED_JOB_ID
    responseJSON = convertRequestToJSON(request)
    if responseJSON == None:
        return    
    LATEST_SAVED_JOB_ID[CURRENT_ACTIVE_USER_INDEX] = responseJSON.get("jobID")
    printResponseJSON(responseJSON)

def afterDescriptionUpload(request):
    global LATEST_SAVED_DESCRIPTION_ID
    responseJSON = convertRequestToJSON(request)
    if responseJSON == None:
        return
    LATEST_SAVED_DESCRIPTION_ID[CURRENT_ACTIVE_USER_INDEX] = responseJSON.get("descriptionID")
    printResponseJSON(responseJSON)

#Converts the given response into a json (python dict), if possible. and returns it. if not it Throws an error-message and returns None.
def convertRequestToJSON(request):
    try:
        responseJSON = request.json()
        return responseJSON
    except:
        printError("Could not convert Response into JSON. Maybe the returned type was a zip. Check status code for further information.")
        return None
#------------------------------------------------------helper funcitons





def register_help():
    registerHelpText = """
    ------------------------------Register - API - Test----------------------------------------

        1. Function
            Requests to regsiter a new user 
        
        2. Usage 
            This tests requires you to give 1 argument, which is the absolute filepath to a json-file,
            [arg2] == pathToJsonFile

        3. Response
            This Method will only print the body, which was sent in the request and the response status-code
        
        If you see this message after following the instructions correctly, maybe the file-path you gave was not correct
    """
    print(registerHelpText)

def login_help():
    loginHelpText = """
        ------------------------------Login/Authenticate - API - Test----------------------------------------

        1. Function
            Requests to authenticate a already existing user
        
        2. Usage 
            This tests requires you to give 1 argument, which is the absolute filepath to a json-file,
            [arg2] == pathToJsonFile

        3. Response
            The test will print the json-body as a response. The method, if you want expand the program, will return the token.
        
        4. Aftermath
            In case of success, a global TOKEN-Variable is set to the token, given by the response. This token is used for future request, which
            require an authorisation-token
        
        If you see this message after following the instructions correctly, maybe the file-path you gave was not correct
    """
    print(loginHelpText)


def submitJob_descriptionIncluded_help():
    submitJob_descriptionIncluded_help_text = """
        ------------------------------Submit Job with Included Description - API - Test----------------------------------------

        1. Function
            Tries to submit a job, which includes the jobdescription
        
        2. Usage 
            This tests requires you to give 1 argument, which is the absolute filepath to a json-file,
            [arg2] == pathToJsonFile

        3. Response
            The test will print the json-body as a response. 
        
        4. Aftermath
            In case of succcess, the LATEST_SAVED_JOB_ID variable is set to the returned JOB-id
        
        If you see this message after following the instructions correctly, maybe the file-path you gave was not correct
    """
    print(submitJob_descriptionIncluded_help_text)


def getJobInfo_help():
    getJobInfo_help_text = """
        ------------------------------Get information of a job - API - Test----------------------------------------

        1. Function  + Usage 
            Tries to get the information of a job (or multiple)
            The path sepcified for this test is only api/v1/jobs/info

            You actually have to specify in [arg2] what kind of request you want. Possible values :
            [arg2] == /all or [arg2] == /global or [arg2] == /single/(id)

            Now, if you do not provide [arg2] with one of the operions above, you actually have to give a file-path
            to a json-body [arg2]. Because in this case you are requesting multiple job-informations. See API-Spec.

            If you choose to use the /single/ option, you can set [arg3] = (id). If you do that, it will be used.
            If you don't the LATEST_SAVED_JOB_ID is used.
        
        

        3. Response
            The test will print the json-body as a response. 
        """
    print(getJobInfo_help_text)

def descriptionUpload_help():
    descriptionUpload_help_text = """
        ------------------------------Submit Description - API - Test----------------------------------------

        1. Function
            Tries to submit a desceription by itself
        
        2. Usage 
            This tests requires you to give (at least one) argument, which is the absolute filepath to a .cnf-file you want to upload
            however you can also give multiple arguments to upload multiple .cnf files.
            [arg2] == pathToJsonFile

        3. Response
            The test will print the json-body as a response. Furthermore,
        
        4. Aftermath
            In case of succcess, the LATEST_SAVED_DESCRIPTION_ID variable is set to the returned description-id
        
        If you see this message after following the instructions correctly, maybe the file-path you gave was not correct
    """
    print(descriptionUpload_help_text)

def downloadDescription_help():
    getJobInfo_help_text = """
        ------------------------------Get Description of a job - API - Test----------------------------------------

        1. Function  + Usage 
            Tries to download description of a job (or multiple)
            The path sepcified for this test is only /api/v1/jobs/description

            You actually have to specify in [arg2] what kind of request you want. Possible values :
            [arg2] == /all or [arg2] == /single/(id)

            Now, if you do not provide [arg2] with one of the operions above, you actually have to give a file-path
            to a json-body [arg2]. Because in this case you are requesting multiple job-descriptions. See API-Spec.

            If you choose to use the /single/ option, you can set [arg3] = (id). If you do that, it will be used.
            If you don't the LATEST_SAVED_JOB_ID is used.
        
        3. Response
            The test will print the json-body as a response. 
        """
    print(getJobInfo_help_text)


def submitJob_descriptionExcluded_help():
    submitJob_descriptionIncluded_help_text = """
        ------------------------------Submit Job with Included Description - API - Test----------------------------------------

        1. Function
            Tries to submit a job, with an exclusive description.
        
        2. Usage 
            [arg2] == filePath to the configuration-file of the job
            
            Now, if you want to use the latest description-ID that you got when using this porgram, you can 
            set [arg3] == "useLastDescriptionID" and the program will insert it into your configuration and then submit it.
            Notice, that [arg3] is optional 

        3. Response
            The test will print the json-body as a response. 
        
        4. Aftermath
            In case of succcess, the LATEST_SAVED_JOB_ID variable is set to the returned JOB-id
    """
    print(submitJob_descriptionIncluded_help_text)

def cancel_job_help():
    cancel_job_help_text = """
        ------------------------------Cancel Job - API - Test----------------------------------------

        1. Function
            Tries to cancel a job
        
        2. Usage 
            [arg2] == filePath to the .json file containing the body, if you want to cancel multiple jobs
            If you want to cancel only one, or all jobs, 
            set [arg2] == "/all", or "/single/(id)"

            Cancel a single job

            If you cancle a single job you can either set [arg3] == id, or you don't. In the case of no 
            [arg3] given, the job-ID stored in LAST_SAVED_JOB_ID is candelled.
        

        3. Response
            In case of a single job-cancellation, only a status-code is printed. 
            In case of all, or some job-cancellations the answer is printed.
    """
    print(cancel_job_help_text)


def getConfig_help():
    getConfig_help_text = """
        ------------------------------Getconfig Config API - Test----------------------------------------
        1. Function
            Get the configuration of fallob

        2. Params
            No parameters needed
    """
    print(getConfig_help_text)

def getMallobInfo_help():
    getMallobInfo_help_text = """
        ------------------------------GetMallob-Info API - Test----------------------------------------
        1. Function
            Get warnings from mallob

        2. Params
            No params required 
    """
    print(getMallobInfo_help_text)

def getResult_help():
    getResult_help_text = """
        ------------------------------Download result of a job - API - Test----------------------------------------

        1. Function  + Usage 
            Tries to download result of a job (or multiple)
            The path sepcified for this test is only /api/v1/jobs/solution

            You actually have to specify in [arg2] what kind of request you want. Possible values :
            [arg2] == /all or [arg2] == /single/(id)

            Now, if you do not provide [arg2] with one of the operions above, you actually have to give a file-path
            to a json-body [arg2]. Because in this case you are requesting multiple job-results. See API-Spec.

            If you choose to use the /single/ option, you can set [arg3] = (id). If you do that, it will be used.
            If you don't the LATEST_SAVED_JOB_ID is used.
        
        3. Response
            The test will print the json-body as a response. 
        """
    print(getResult_help_text)


#------------------------------------------------------submitting a job with external description needed and extra method...

def submit_job_external(testCase):
    filePath = commandLineArguments[ARG_2]
    if not exists(filePath):
        printError("Given filepath " + str(filePath) + " was not valid")
        return

    url = BASE_URL + URL_MAPPINGS.get(testCase)


    jsonContent = readFileAsPythonDict(filePath)

    #modify json-content and use the latest description ID that has been submitted
    if (len(commandLineArguments) > 2 and commandLineArguments[ARG_3].lower() == "useLastDescriptionID".lower()):
        jsonContent["descriptionID"] = LATEST_SAVED_DESCRIPTION_ID[CURRENT_ACTIVE_USER_INDEX] 

    r = requests.post(url, json=jsonContent, headers=HEADER)
    printStatusCode(r.status_code)

    if r != None:
        afterFunction = AFTER_REQUEST_FUNCTION_MAPPINGS.get(testCase)
        afterFunction(r)

def cancelJob(testCase):
    filePath = commandLineArguments[ARG_2]
    url = BASE_URL + URL_MAPPINGS.get(testCase)

    #build url
    if not exists(commandLineArguments[ARG_2]): # multiple jobs are about to be canceled
        url += commandLineArguments[ARG_2]
        if commandLineArguments[ARG_2] != "/all":
            #only a single job is cancelled
            if len(commandLineArguments) > 2:
                url += commandLineArguments[ARG_3]
            else:
                url += str(LATEST_SAVED_JOB_ID[CURRENT_ACTIVE_USER_INDEX]) 
        r = requests.post(url, headers=HEADER)
    else:
        r = requests.post(url, json=readFileAsPythonDict(filePath), headers=HEADER)
    printStatusCode(r.status_code)
    printResponse(r)
    
#------------------------------------------------------API, main, and other helping methods 
#runTestsFromFile /home/siwi/pse_dev/filesForTesting/multipleTests.txt

"""
This request is a general get-request for mostly all GET requests of our API..
Because most GET requests have the option to get /single/, /all, or just some
This method has some extra parameters.

For convenience, this method has the
    @param parameter, which is used, if 
            1. parameterPossible
            2. no Parameter was given in [arg2]
        This parameter is appended to the end of the URL
    

    @oaram parameterPossible is True, if a parameter is even possible - meaning if the GET-request HAS  parameter at the end
"""
def generalGetRequest(testCase, parameter, parameterPossible, urlModification):
    url = BASE_URL + URL_MAPPINGS.get(testCase)
    hasBody = False
    if urlModification:
        if exists(commandLineArguments[ARG_2]):
            #in this case [arg2] was a filepath to a json body and the url is done already
            #multiple-option was used
            hasBody = True
        else:
            url += commandLineArguments[ARG_2]
            #/single/ optio was used 
            if parameterPossible and commandLineArguments[ARG_2] != "/all": 
                if len(commandLineArguments) > 2:
                    url += str(commandLineArguments[ARG_3])
                else:
                    url += str(parameter)

    if hasBody:
        filePath = commandLineArguments[ARG_2]
        if (exists()):
            r = requests.get(url, json=readFileAsPythonDict(filePath), headers=HEADER)
        else:
            printError("Given filepath " + str(filePath) + " was not valid")

    else:
        r = requests.get(url, headers=HEADER)
    printStatusCode(r.status_code)
    printResponse(r)


"""
    This method performs a general post request to an API
    It uses the testCase, given as a parameter when the program starts up

    @param testCase, for example T1000

    This case is then used, to extract all necessary parameters from the MAPPINGS (see beginning of file)
    There are 4 Essential Mappings;

        1. URL_MAPPINGS
            Contains the url for the endpoint that the request is going to

        2. AUTHENTICATION_MAPPINGS
            True or False, depending on the request needing authentification

        3. AFTER_REQUEST_FUNCTION_MAPPINGS
            This is the function that is executed AFTER the request. The answer is passed as a parameter

        4. HELP_FUNCTION_MAPPINGS
            This is the function that is executed, if anything went wrong 

"""
def generalPostRequest(testCase):
    filePath = commandLineArguments[ARG_2]
    url = BASE_URL + URL_MAPPINGS.get(testCase)
    r = doPostRequest(filePath, url, AUTHENTICATION_MAPPINGS.get(testCase))
    if r != None:
        afterFunction = AFTER_REQUEST_FUNCTION_MAPPINGS.get(testCase)
        afterFunction(r)


"""

    This methof performs a post-Request to the given URL. The method then tries to print the status_code of the answer
    Before doing the request, the method prints out 


    @param pathToJsonFileContainingBody .json file which contians the body, sent in the request 7
    @param url url to the endpoint of the API
    @param helpFunciton - function that is executed, if the file does not exist
    @param authentication - boolean, if True, authentication - header is used, if False no authentication-header is used

    @return request-answer object if request was successful, None if something went wrong
"""
def doPostRequest(pathToJsonFileContainingBody, url, authentication):
    if authentication and HEADER == None:
        printError("You are not authenticated. Please authenticate yourself first - by using login")
        return None
    if exists(pathToJsonFileContainingBody):
        if PRINT_REQ_JSON_BODY:
            print("Json-Body for this request ; \n" + str(readFile(pathToJsonFileContainingBody)))
        #r = requests.post(registerURL, json=fileContent)
        if authentication:
            r = requests.post(url, json=readFileAsPythonDict(pathToJsonFileContainingBody), headers=HEADER)
        else:
            r = requests.post(url, json=readFileAsPythonDict(pathToJsonFileContainingBody))
        printStatusCode(r.status_code)

        return r
    else:
        printError("Given filepath " + str(pathToJsonFileContainingBody) + " was not valid")
        return None

"""

"""
def multiPartFileRequest(testCase):

    #filePath = commandLineArguments[ARG_2]
    url = BASE_URL + URL_MAPPINGS.get(testCase)
    r = None

    #build the file-parameter for the request
    fileDict = {}
    for i in range(1, len(commandLineArguments)):
        if exists(commandLineArguments[i]):
            fileDict["file" + str(i)] = (open(commandLineArguments[i], "rb"))

    #actually issue the request
    r = requests.post(url, headers=HEADER, files=fileDict)
    printStatusCode(r.status_code)
        
    if r != None:
        afterFunction = AFTER_REQUEST_FUNCTION_MAPPINGS.get(testCase)
        afterFunction(r)
        


def requestToJSON(request):
    return urlopen(request).read().decode()

def printStatusCode(statusCode):
    global LATEST_STATUS_CODE
    LATEST_STATUS_CODE = statusCode
    printWarning("Response-Statuscode : " + str(statusCode))

#Sets the global HEADER variable to include authorisation. CURRENT_ACTIVE_TOKEN is used as token.
def setHeader(token):
    global HEADER, ALL_ACTIVE_USERS, CURRENT_ACTIVE_USER_INDEX
    CURRENT_ACTIVE_TOKEN = token
    userIndex = len(ALL_ACTIVE_USERS)
    printSystemMessage("User is now authenticated. Refer to this user as 'user" + str(userIndex) + "'")
    CURRENT_ACTIVE_USER_INDEX = userIndex

    #expand the array for latest saved job-id and description-id
    LATEST_SAVED_DESCRIPTION_ID.append(-1)
    LATEST_SAVED_JOB_ID.append(-1)

    ALL_ACTIVE_USERS.append(CURRENT_ACTIVE_TOKEN)
    purelySetHeader(CURRENT_ACTIVE_TOKEN)

def purelySetHeader(token):
    global HEADER
    HEADER = {'Authorization' : "Bearer " + str(token)}

def switchUser():
    global CURRENT_ACTIVE_USER_INDEX
    try:
        index = int(commandLineArguments[ARG_2])
    except:
        printError("Cannot switch to 'user" + commandLineArguments[ARG_2] + "'. Please set [arg_2] to an int > 0.")
    if not ALL_ACTIVE_USERS or index >= len(ALL_ACTIVE_USERS) or index < 0:
        printError("User-index invalid.")
    CURRENT_ACTIVE_USER_INDEX = index
    purelySetHeader(ALL_ACTIVE_USERS[index])
    print(bcolors.OKCYAN + "Switched to 'user" + str(index) + "'" + bcolors.ENDC)

def readFile(path):
    f = open(path, "r")
    content = f.read()
    f.close()
    return content


def readFileAsPythonDict(path):
    with open(path) as handle:
        return json.loads(handle.read())

def requestsHelp(parameter):
    if parameter == "help" or parameter == "?":
        return True
    return False 

def printError(message):
    print(bcolors.FAIL + message + bcolors.ENDC)

def printWarning(message):
    print(bcolors.WARNING + message + bcolors.ENDC)

def printSystemMessage(message):
    print(bcolors.OKBLUE + message + bcolors.ENDC)

def printHelp():
    tutorialText = """
    --------------------------------This is a script for automating testruns with the API-----------------

        1. Usage

            Start the program by typing python3 apitests.py into your console.
            After that, type the commands you want to execute 

            > [arg1] [arg2] [arg3]

        
         Important functions;

            a) Run tests from File
            
                set [arg1] == "runTestsFromFile" and [arg2] to a filePath in which you specify these files.
                Set the file up like the following;
                    [tetscaseNumber1] [arg2] [arg3] ...
                    #this is a comment
                    [tetscaseNumber2] [arg2] [arg3] ...

                The program will then read your file and execute each instruction as if you typed it into the programs interface regularly.
                Lines beginning with # in your specification-file will be ignored as comments.

            b) Multi-User System

                If you want to simulate multiple users interacting with fallob, you can do that easily, by setting [arg1] == "switchUser" and
                [arg2] == (index). 
                Every user you authenticate is stored, the first one you authenticate is stored as user0, second one as user1, ...
                After you use switchUser, every request you do comes form the user-token you just switched to.


        2. Args

            [arg1] is the type of test you want to execue. 
                - If you want to issue a specific tests, use the number given to each test-case in the Pflichtenheft. For example, registering a new 
                  user is T1000.
            [arg2-n] resources and parameters for the test-case sepcified in [arg1]. Set [arg2] == help or [arg2] == ? to get a help page for each API-Request.
                
            -Specail commands for [arg1]. Set [arg1] to ...
                ..."showAllTests" and a list of all possible tests is printed
                ..."togglePrintBody" - toggles printing the request-body before each request - 
                ..."toggleCatchReqError" - toggles catching the error.
                ...."togglePrintResponse" - if true, prints the response json, if false, it doesn't
                ..."exit" to leave the program


            Notice; all paths given have to be absolute
    """
    print(tutorialText)


def executeTestCase(testCaseIdentifier):
    try:
        helperFunction = HELP_FUNCTION_MAPPINGS.get(testCaseIdentifier)
        if commandLineArguments and requestsHelp(commandLineArguments[ARG_1]) or len(commandLineArguments) >  1 and requestsHelp(commandLineArguments[ARG_2]):
            helperFunction()
            return
    except:
        noFunction()

    #this is only written so supid because i want to prevent 
    if testCaseIdentifier == REGISTER:
        generalPostRequest(REGISTER)
    elif testCaseIdentifier == LOGIN:
        generalPostRequest(LOGIN)
    elif testCaseIdentifier == JOBSUBMIT_INCLUDE:
        generalPostRequest(JOBSUBMIT_INCLUDE)
    elif testCaseIdentifier == SUBMIT_DESCRIPTION:
        multiPartFileRequest(SUBMIT_DESCRIPTION)
    elif testCaseIdentifier == SUBMIT_JOB_EXTERNAL:
        submit_job_external(SUBMIT_JOB_EXTERNAL)
    elif testCaseIdentifier == CANCEL_JOB:
        cancelJob(CANCEL_JOB)
    #get-requests
    elif testCaseIdentifier == GET_JOB_INFO:
        generalGetRequest(GET_JOB_INFO, LATEST_SAVED_JOB_ID[CURRENT_ACTIVE_USER_INDEX], True, True)
    elif testCaseIdentifier == DOWNLOAD_DESCRIPTION:
        generalGetRequest(DOWNLOAD_DESCRIPTION, LATEST_SAVED_JOB_ID[CURRENT_ACTIVE_USER_INDEX], True, True)
    elif testCaseIdentifier == GET_SYSTEM_CONFIG:
        generalGetRequest(GET_SYSTEM_CONFIG, None, False, False)
    elif testCaseIdentifier == GET_MALLOB_INFO:
        generalGetRequest(GET_MALLOB_INFO, None, False, False)
    elif testCaseIdentifier == GET_RESULT:
        generalGetRequest(GET_RESULT, LATEST_SAVED_JOB_ID[CURRENT_ACTIVE_USER_INDEX], True, True)
    else:
        print(bcolors.FAIL + "Seems like the Test-case given has not yet been implemented, or is just wrong all together." + bcolors.ENDC)


#Tries if the user wanted to use an extra function 
def tryExtraCommandLineFuncion():
    global PRINT_REQ_JSON_BODY, CATCH_REQUEST_ERROR, PRINT_RESPONSE_JSON
    if requestsHelp(commandLineArguments[ARG_1]):
        printHelp()
        return True
    
    if commandLineArguments[ARG_1].lower() == "togglePrintBody".lower():
        PRINT_REQ_JSON_BODY = not PRINT_REQ_JSON_BODY
        printSystemMessage("Print JSON_BODY : " + str(PRINT_REQ_JSON_BODY))
        return True
    
    if commandLineArguments[ARG_1].lower() == "toggleCatchReqError".lower():
        CATCH_REQUEST_ERROR = not CATCH_REQUEST_ERROR
        printSystemMessage("Catch Request- Error : " + str(CATCH_REQUEST_ERROR))
        return True

    if commandLineArguments[ARG_1].lower() == "togglePrintResponse".lower():
        PRINT_RESPONSE_JSON = not PRINT_RESPONSE_JSON
        printSystemMessage("Print response-json : " + str(PRINT_RESPONSE_JSON))
        return True

    if commandLineArguments[ARG_1].lower() == SHOW_ALL_TESTS.lower():
        print(AVAILABLE_TESTS)
        return True
    
    if commandLineArguments[ARG_1].lower() == "switchUser".lower():
        switchUser()
        return True

    return False

#
def runTestCase(testCase):
    if CURRENT_ACTIVE_TOKEN == None:
        print(bcolors.OKGREEN + "Executing test-case : " + str(testCase) + bcolors.ENDC)
    else:
        print(bcolors.OKGREEN + "Executing test-case : " + str(testCase) + "as 'user" + str(CURRENT_ACTIVE_USER_INDEX) + "'" + bcolors.ENDC)

    if CATCH_REQUEST_ERROR:
        try:    
            executeTestCase(testCase)
        except:
            printError("Something went wrong with the API-Request.")
    else:
        executeTestCase(testCase)

def executeRunLoop():
    if tryExtraCommandLineFuncion():
        return

    runTestCase(commandLineArguments[ARG_1])

def runTestsFromFile():
    global commandLineArguments
    if (not exists(commandLineArguments[ARG_2])):
        printError("Given file does not exist.")
        return
    testSpecificationFile = open(commandLineArguments[ARG_2], "r")
    lines = testSpecificationFile.readlines()
    for line in lines:
        commandLineArguments = line.split()

        if not commandLineArguments or commandLineArguments[ARG_1][0] =="#":
            continue
        executeRunLoop()

def main():
    global commandLineArguments, PRINT_REQ_JSON_BODY
    setAfterRequestFuncitons()
    
    running = True
    while(running):
        userInput = input(bcolors.OKCYAN + bcolors.BOLD + "Fallob-API-Tests> " + bcolors.ENDC)
        commandLineArguments = userInput.split()

        if not commandLineArguments:
            continue
    
        if commandLineArguments[ARG_1] == "exit":
            running = False
            continue
    
        

        if commandLineArguments[ARG_1] == "runTestsFromFile":
            runTestsFromFile()
            continue

        executeRunLoop()



def testting():
    print({"dicEntry" : 2, "dicentry" : "he"})
    with open(commandLineArguments[2]) as handle:
        dictdump = json.loads(handle.read())
    print(dictdump)
main()
 
#testting()