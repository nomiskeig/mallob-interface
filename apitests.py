import requests
import sys
from os.path import exists
from urllib.parse import urlencode
from urllib.request import Request, urlopen
import socket
import json

#Thi class is purely cosmetical
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

BASE_URL = "http://" + socket.gethostbyname(socket.gethostname() + ".local") + ":8080"
print(bcolors.OKGREEN + "Base-URL for API requests : "  + str(BASE_URL) + bcolors.ENDC)
SHOW_ALL_TESTS = "showAllTests"
SETTOKEN = "setToken"
AVAILABLE_TESTS = ["register (T1000)", "login (T1010)",  "submitDescription (T1019)",  "submitJobExternalDescription (T1020)",  "submitJobInternalDescription (T2021)",
    "downloadDescription (T1022)",
    "cancelJob (T1030)", "get (Single, All, Global, Multiple)-JobInformation (T1070)",  "getResult - one, multiple, all - (T1080)", "getMallobInfo (T1100)" , "getSystemConfig (T1120)"]

#these are variabls, which are being set at runtime
#holds the current authentication-token
HEADER = None
CURRENT_ACTIVE_TOKEN = None
LATEST_SAVED_JOB_ID = None
LATEST_SAVED_DESCRIPTION_ID = None


#---------------test-cases
REGISTER = "T1000"
LOGIN = "T1010"
JOBSUBMIT_INCLUDE = "T1021"
GET_JOB_INFO = "T1070"
SUBMIT_DESCRIPTION = "T1019"
DOWNLOAD_DESCRIPTION = "T2022"


#not yet implemented
SUBMIT_JOB_EXTERNAL = "T1020"
CANCEL_JOB = "T1030"
GET_RESULT = "T1080"
GET_SYSTEM_CONFIG = "T1120"
GET_MALLOB_INFO = "T1100"

URL_MAPPINGS = {REGISTER : "/api/v1/users/register",
                LOGIN : "/api/v1/users/login",
                JOBSUBMIT_INCLUDE : "/api/v1/jobs/submit/inclusive",
                GET_JOB_INFO : "/api/v1/jobs/info",
                SUBMIT_DESCRIPTION : "/api/v1/jobs/submit/exclusive/description",
                DOWNLOAD_DESCRIPTION : "/api/v1/jobs/description"}

AUTHENTICATION_MAPPINGS = {REGISTER : False,
                LOGIN : False,
                JOBSUBMIT_INCLUDE : True,
                GET_JOB_INFO : True,
                SUBMIT_DESCRIPTION : True,
                DOWNLOAD_DESCRIPTION : True
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
        DOWNLOAD_DESCRIPTION : printResponse
    }

    HELP_FUNCTION_MAPPINGS = {
        REGISTER : register_help,
        LOGIN : login_help,
        JOBSUBMIT_INCLUDE : submitJob_descriptionIncluded_help,
        GET_JOB_INFO : getJobInfo_help,
        SUBMIT_DESCRIPTION : descriptionUpload_help,
        DOWNLOAD_DESCRIPTION : downloadDescription_help
    }


#------------------------------------------------------after methods 
def noFunction(r):
    pass

def printResponse(r):
    responseJSON = r.json()
    print(responseJSON)


def afterLogin(request):
    global CURRENT_ACTIVE_TOKEN
    responseJSON = request.json()
    print(responseJSON)
    CURRENT_ACTIVE_TOKEN = responseJSON.get("token")

    #set the global header variable;
    setHeader()

def afterJobInclude(request):
    global LATEST_SAVED_JOB_ID
    responseJSON = request.json()
    LATEST_SAVED_JOB_ID = responseJSON.get("jobID")
    print(responseJSON)

def afterDescriptionUpload(request):
    global LATEST_SAVED_DESCRIPTION_ID
    responseJSON = request.json()
    LATEST_SAVED_DESCRIPTION_ID = responseJSON.get("descriptionID")
    print(responseJSON)
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
        ------------------------------Get information of a single job - API - Test----------------------------------------

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
        ------------------------------Get information of a single job - API - Test----------------------------------------

        1. Function  + Usage 
            Tries to download description of a job (or multiple)
            The path sepcified for this test is only /api/v1/jobs/description

            You actually have to specify in [arg2] what kind of request you want. Possible values :
            [arg2] == /all or [arg2] == /single/(id)

            Now, if you do not provide [arg2] with one of the operions above, you actually have to give a file-path
            to a json-body [arg2]. Because in this case you are requesting multiple job-informations. See API-Spec.

            If you choose to use the /single/ option, you can set [arg3] = (id). If you do that, it will be used.
            If you don't the LATEST_SAVED_JOB_ID is used.
        
        3. Response
            The test will print the json-body as a response. 
        """
    print(getJobInfo_help_text)

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
def generalGetRequest(testCase, parameter, parameterPossible):
    url = BASE_URL + URL_MAPPINGS.get(testCase)
    hasBody = False
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
            print(bcolors.FAIL + "Given filepath " + str(filePath) + " was not valid" + bcolors.ENDC)

    else:
        r = requests.get(url, headers=HEADER)
    print(bcolors.WARNING + "Response-Statuscode : " + str(r.status_code) + bcolors.ENDC)
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
        print(bcolors.FAIL + "You are not authenticated. Please authenticate yourself first - by using login" + bcolors.ENDC)
        return None
    if exists(pathToJsonFileContainingBody):
        if PRINT_REQ_JSON_BODY:
            print("Json-Body for this request ; \n" + str(readFile(pathToJsonFileContainingBody)))
        #r = requests.post(registerURL, json=fileContent)
        if authentication:
            r = requests.post(url, json=readFileAsPythonDict(pathToJsonFileContainingBody), headers=HEADER)
        else:
            r = requests.post(url, json=readFileAsPythonDict(pathToJsonFileContainingBody))
        print(bcolors.WARNING + "Response-Statuscode : " + str(r.status_code) + bcolors.ENDC)
        return r
    else:
        print(bcolors.FAIL + "Given filepath " + str(pathToJsonFileContainingBody) + " was not valid" + bcolors.ENDC)
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
    print(bcolors.WARNING + "Response-Statuscode : " + str(r.status_code) + bcolors.ENDC)
        
    if r != None:
        afterFunction = AFTER_REQUEST_FUNCTION_MAPPINGS.get(testCase)
        afterFunction(r)
        


def requestToJSON(request):
    return urlopen(request).read().decode()

#Sets the global HEADER variable to include authorisation. CURRENT_ACTIVE_TOKEN is used as token.
def setHeader():
    global HEADER
    HEADER = {'Authorization' : "Bearer " + str(CURRENT_ACTIVE_TOKEN)}

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

def printHelp():
    tutorialText = """
    --------------------------------This is a script for automating testruns with the API-----------------

        1. Usage

            Start the program by typing python3 apitests.py into your console.
            After that, type the commands you want to execute 

            > [arg1] [arg2] [arg3]

        
         Important function;
            
            set [arg1] to "runTestsFromFile" and [arg2] to a filePath in which you specify these files.
            Set the file up like the following;
            [tetscaseNumber1] [arg2] [arg3] ...
            [tetscaseNumber2] [arg2] [arg3] ...

            The program will then read your file and execute each instruction as if you typed it into the programs interface regularly.


        2. Args

            [arg1] is the type of test you want to execue. 
                - If you want to issue a specific tests, use the number given to each test-case in the Pflichtenheft. For example, registering a new 
                  user is T1000.
                
                -Specail commands for [arg1]. Set [arg1] to ...
                    ..."showAllTests" and a list of all possible tests is printed
                    ..."togglePrintBody" - toggles printing the request-body before each request - 
                    ..."toggleCatchReqError" - toggles catching the error.
                    ..."exit" to leave the program

           


            [arg2-n] is the resources necessary for executing the test, specified in [arg1]
                for example a filepath to a file contianing a JobConfiguration like a user would submit it
                or multiple-filepaths, for either multiple JobConfigurations
            
                For a detailed explanation of what parameters are requested for each test-run, specify your test in [arg1] and use "?", 
                or "help" as argument 2
            

            Notice; all paths given have to be absolute
    """
    print(tutorialText)


def executeTestCase(testCaseIdentifier):
    try:
        helperFunction = HELP_FUNCTION_MAPPINGS.get(testCaseIdentifier)
        if len (commandLineArguments) >  1 and requestsHelp(commandLineArguments[ARG_2]):
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
    #get-requests
    elif testCaseIdentifier == GET_JOB_INFO:
        generalGetRequest(GET_JOB_INFO, LATEST_SAVED_JOB_ID, True)
    elif testCaseIdentifier == DOWNLOAD_DESCRIPTION:
        generalGetRequest(DOWNLOAD_DESCRIPTION, LATEST_SAVED_JOB_ID, True)
    else:
        print(bcolors.FAIL + "Seems like the Test-case given has not yet been implemented, or is just wrong all together." + bcolors.ENDC)
        printHelp()


#Tries if the user wanted to use an extra function 
def tryExtraCommandLineFuncion():
    global PRINT_REQ_JSON_BODY, CATCH_REQUEST_ERROR
    if requestsHelp(commandLineArguments[ARG_1]):
        printHelp()
        return True
    
    if commandLineArguments[ARG_1].lower() == "togglePrintBody".lower():
        PRINT_REQ_JSON_BODY = not PRINT_REQ_JSON_BODY
        print(bcolors.WARNING + "Print JSON_BODY : " + str(PRINT_REQ_JSON_BODY) + bcolors.ENDC)
        return True
    
    if commandLineArguments[ARG_1].lower() == "toggleCatchReqError".lower():
        CATCH_REQUEST_ERROR = not CATCH_REQUEST_ERROR
        print(bcolors.WARNING + "Catch Request- Error : " + str(CATCH_REQUEST_ERROR) + bcolors.ENDC)
        return True
    
    if commandLineArguments[ARG_1] == "exit":
        running = False
        return True

    if commandLineArguments[ARG_1].lower() == SHOW_ALL_TESTS.lower():
        print(AVAILABLE_TESTS)
        return True

    return False

def executeRunLoop():
    if tryExtraCommandLineFuncion():
        return

    if CATCH_REQUEST_ERROR:
        try:    
            executeTestCase(commandLineArguments[ARG_1])
        except:
            print(bcolors.FAIL + "Something went wrong with the API-Request." + bcolors.ENDC)
    else:
        executeTestCase(commandLineArguments[ARG_1])

def runTestsFromFile():
    global commandLineArguments
    testSpecificationFile = open(commandLineArguments[ARG_2], "r")
    lines = testSpecificationFile.readlines()
    for line in lines:
        commandLineArguments = line.split()
        print(bcolors.OKGREEN + "Executing test-case : " + str(commandLineArguments[ARG_1]) + bcolors.ENDC)
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