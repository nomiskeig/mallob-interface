# Usage

The easiest way is to use docker.

#### Prerequisites

Docker and Docker compose have to be installed.

#### Installation

Execute the following steps:

```
git clone https://github.com/nomiskeig/mallob-interface.git
cd mallob-interface
WORKING_DIR="/path/to/working/dir/" CONFIG_FILE="/path/to/config/file.json" MALLOB_DIR="/path/to/mallob/" docker compose build
```

#### Start

First, start mallob with the -iff flag, the log flag set to .api and the log level set to four.
Here is an example to start mallob with said options:
```
RDMAV_FORK_SAFE=1 mpirun -np 6 --oversubscribe build/mallob -t=1 -log=.api -v=4 -iff
```
Then, execute the following command:
```
WORKING_DIR="/path/to/working/dir/" CONFIG_FILE="/path/to/config/file.json" MALLOB_DIR="/path/to/mallob/" docker compose run
```

This will start up the program. The API will be available on Port 8080, and the frontend will be available at localhost:1337.

#### WORKING_DIR

The WORKING_DIR environment is a folder  contains a folder for the descriptions and a folder for the solutions as well as the database file.
This folder gets mounted into to docker container and thus, the paths for the database, results and descriptions specified in the configuration file have to exist within this folder.

#### CONFIG_FILE 

The path to the configuration file. An example configuration file can be found [here](backend/fallob-configuration.json). 
Note that the example config expects mallob to be run with six processes.


#### MALLOB_DIR

The path to be directory where mallob is installed. Note that this has to be the folder where mallob creates the .api folder in, which by default is the mallob directory.

# Database

A database file can be found [here](backend/src/main/resources/database/). Note that this database is completely empty.


# Configuration file

Some notes on the paths which have to be set in the configuration file:

#### databaseBasePath

An absolute path to the database-file. The .mv.db file ending must not be included in the path.

#### mallobBasePath

An absolute path to the folder where mallob creates the .api folder in. Must end with a /.

#### descriptionBasePath

An absolute path to the folder where fallob saves the descriptions of the jobs. Must end with the name of the folder without an / at the end.

#### resultBasePath

An absolute path to the folder where fallob saves the results of the jobs. Must end with the name of the folder without an / at the end.


# Reseting 

As there are still bugs in the application, you may have to reset all the files to get up and running again. The following steps have to be done in order to do so:

- Clear the description folder
- Clear the result folder
- Delete the database file and replace with a working version (most likely an empty one)
- Delete the .api folder mallob creates.
