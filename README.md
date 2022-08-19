# Usage

The easiest way is to use docker.

#### Prerequisites

Docker and Docker compose have to be installed.

#### Installation

Execute the following steps:

```
git clone https://github.com/nomiskeig/mallob-interface.git
cd mallob-interface
docker compose build
```

#### Start

Execute the following steps:
```
CONFIG_FILE="/path/to/config/file.json" docker compose run
```

This will start up the program. The API will be available on Port 8080, and the Frontend will be available at localhost:1337.
