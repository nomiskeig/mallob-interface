#!/bin/bash
if [ $# -eq 1 ] && [ $1 == '--help' ] 
then
    docker run fallob-dbaccess --help
    exit 0
fi
if [ $# -ne 2 ] && [ $# -ne 3 ]
then
    echo "Error, incorrect amount of arguments provided."
    exit 0
fi
if [ $# -eq 2 ]
then 
    file=./fallob-data/fallob-configuration.json
else
    file=$3
fi
empty=" "
arguments="$1$empty$2$empty$file"
echo $arguments;
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

docker run \
    --mount type=bind,source=$file,target=$file \
    --mount type=bind,source=$databaseSource,target=$databaseTarget \
    fallob-dbaccess $1 $2 $file
