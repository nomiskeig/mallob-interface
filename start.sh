#!/bin/bash
if [ $# -eq 0 ]
then 
    file=./fallob-data/fallob-configuration.json
else 
    file=$1
fi
databaseSource=$(grep -o '"databaseBasePath": "[^"]*' $file | grep -o '[^"]*$');
descriptionsSource=$(grep -o '"descriptionsBasePath": "[^"]*' $file | grep -o '[^"]*$');
resultsSource=$(grep -o '"resultBasePath": "[^"]*' $file | grep -o '[^"]*$');
mallobSource=$(grep -o '"mallobBasePath": "[^"]*' $file | grep -o '[^"]*$');
databaseTarget=$databaseSource
descriptionsTarget=$descriptionsSource
resultsTarget=$resultsSource
mallobTarget=$mallobSource

#make relative paths absolute since docker can not work with relative paths
if [[ ${file::1} == "." ]]
then
    file=$(pwd)${file:1}
fi
if [[ ${descriptionsSource::1} == "." ]]
then
    descriptionsTarget="/app${descriptionsSource:1}"
    descriptionsSource=$(pwd)${descriptionsSource:1}
fi
if [[ ${resultsSource::1} == "." ]]
then
    resultsTarget="/app${resultsSource:1}"
    resultsSource=$(pwd)${resultsSource:1}
fi
if [[ ${databaseSource::1} == "." ]]
then
    databaseTarget="/app${databaseSource:1}"
   databaseSource=$(pwd)${databaseSource:1}
fi
if [[ ${mallobSource::1} == "." ]]
then
    mallobTarget="/app${mallobSource:1}"
    mallobSource=$(pwd)${mallobSource:1}
fi

# Find correct command for docker compose / docker-compose
dockercmd="docker compose"
if docker-compose -v >/dev/null ; then dockercmd="docker-compose"; fi

MALLOB_DIR_SOURCE=$mallobSource MALLOB_DIR_TARGET=$mallobTarget CONFIG_FILE_SOURCE=$file RESULT_DIR_SOURCE=$resultsSource RESULT_DIR_TARGET=$resultsTarget DESCRIPTION_DIR_SOURCE=$descriptionsSource DESCRIPTION_DIR_TARGET=$descriptionsTarget DATABASE_SOURCE=$databaseSource DATABASE_TARGET=$databaseTarget $dockercmd up

