# Usage

The easiest way is to use docker.

#### Prerequisites

Docker and Docker compose have to be installed.

#### Installation

Execute the following steps:

```
git clone https://github.com/nomiskeig/mallob-interface.git
cd mallob-interface
WORKING_DIR="/path/to/working/dir/" CONFIG_FILE="/path/to/config/file.json" docker compose build
```

#### Start

Execute the following steps:
```
WORKING_DIR="/path/to/working/dir/" CONFIG_FILE="/path/to/config/file.json" docker compose run
```

This will start up the program. The API will be available on Port 8080, and the Frontend will be available at localhost:1337.

#### WORKING_DIR

The WORKING_DIR environment is the folder which gets mounted into to docker container.
Thus, the paths for the database specifed in the config file have to be files within this folder. The folder is mounted under /app/workingDir/ and the paths of the database have to be an absolute path starting with /app/workingDir/.

#### CONFIG_FILE 

The path to the config file. 

