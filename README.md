# Usage

The only supported way to use fall is to use docker.

#### Prerequisites

Docker and Docker compose have to be installed. The build and start Scripts are bash files, so you need to be able to run them. 

#### Building

Execute the following steps:

```
git clone https://github.com/nomiskeig/mallob-interface.git
cd mallob-interface
./build.sh
```
You may have to give the build.sh file permission to execute. This can be done with
```
chmod +x ./build.sh
```

After execution of the command, some files are created under the fallob-data folder:

- 'fallob-configuration.json': The default configuration. Used by the start-Script by default. Renaming or moving/deleting this file without providing an alternative file will result in errors.
- 'fallobDatabase.mv.db': The database used by default.
- 'results'-Folder: Results are stored in this folder by default.
- 'descriptions'-Folder: Job-Descriptions are stored in this folder by default.

NOTE:
The mallobBasePath of the configuration-File has to be changed to the correct path before starting up fallob as well as the number of processes your instance of mallob uses. All other parameters should be fine if left at the default values.

NOTE 2:
Executing the build script will delete the fallob-data folder and its contents and reset the folder to the default layout/values.


#### Start

First, start mallob with the -iff flag, the log flag set to .api and the log level set to four.

Here is an example to start mallob with said options:
```
RDMAV_FORK_SAFE=1 mpirun -np 6 --oversubscribe build/mallob -t=1 -log=.api -v=4 -iff
```
Then, execute the following command:
```
./start.sh
```
This will start up the program. The API will be available on Port 8080, and the Web-Interface will be available on Port 1337 of the localhost, i.e. the Web-Interface is accessable at localhost:1337/login.


You may have to give the start.sh file permission to execute. This can be done with the following command:
```
chmod +x ./start.sh
```
NOTE:

The start-Script does not check the provided parameters of the config. Thus, a wrongly formatted configuration-File might lead to undefined behavior.

# Configuration file

All the paths in the configuration file can be either absolute or relative. Relative paths begin in the mallob-interface-folder.
Provided relative Paths will internally be converted into absolute paths, which might not work correctly on all machines. It's only tested on Arch Linux, but should work on most Unix-Based operating systems. Wrong behavior because of wrong translation of paths will most likely not be caught and might not be immediately noticeable.

Some specific notes on the paths which have to be set in the configuration file:

#### databaseBasePath

The path to the database-file.

#### mallobBasePath

The path to the folder where mallob creates the .api folder in. Must end with a /.

#### descriptionBasePath

The path to the folder where fallob saves the descriptions of the jobs. Must end with the name of the folder without an / at the end.

#### resultBasePath

The path to the folder where fallob saves the results of the jobs. Must end with the name of the folder without an / at the end.

For the other parameters, see the Design-Document.

# Changing the default configuration

The provided default-Configuration file can be changed as you may wish.
Alternatively, an alternative configuration-File can be provided by passing it's path to the start-Script as a parameter.

For example:
```
.\start.sh /path/to/alternative/config/file.json
```

# Deleting stored data

Deleting files within the result or description folder or deleting/resetting the database-file without clearing the result and description folders will lead to undefined behavior.
<!---
# Reseting 

As there are still bugs in the application, you may have to reset all the files to get up and running again. The following steps have to be done in order to do so:

- Clear the description folder
- Clear the result folder
- Delete the database file and replace with a working version (most likely an empty one)
- Delete the .api folder mallob creates.
--->
