#!/bin/bash
if [ $# -eq 1 ] && [ $1 == '--help' ] 
then
    docker run fallob-dbaccess --help
    exit 0
fi
if [ $# -ne 2 ] && [ $# -ne 3 ] && [ $# -ne 4 ]
then
    echo "Error, incorrect amount of arguments provided."
    exit 0
fi
if [ $# -eq 4 ] && [ $1 != 'setPriority' ]
then
    echo "Error, incorrect amount of arguments provided."
    exit 0
fi
if [ $# -eq 2 ] && [ $1 != 'setPriority' ]
then 
    file=./fallob-data/fallob-configuration.json
else
    file=$3
fi
if [ $# -eq 4 ] && [ $1 == 'setPriority' ]
then
    file=$4
fi
if [ $# -eq 3 ] && [ $1 == 'setPriority' ]
then file=./fallob-data/fallob-configuration.json
fi
databaseSource=$(grep -o '"databaseBasePath": "[^"]*' $file | grep -o '[^"]*$');
databaseTarget=$databaseSource

if [[ ${databaseSource::1} == "." ]]
then
    databaseTarget="/app${databaseSource:1}"
   databaseSource=$(pwd)${databaseSource:1}
fi

if [[ ${file::1} == "." ]]
then
    file=$(pwd)${file:1}
fi

if [ $1 != 'setPriority' ]
then 
docker run \
    --mount type=bind,source=$file,target=$file \
    --mount type=bind,source=$databaseSource,target=$databaseTarget \
    fallob-dbaccess $1 $2 $file
else
docker run \
    --mount type=bind,source=$file,target=$file \
    --mount type=bind,source=$databaseSource,target=$databaseTarget \
    fallob-dbaccess $1 $2 $3 $file
fi

