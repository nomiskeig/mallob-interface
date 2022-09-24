rm -rf fallob-data
mkdir fallob-data
cd fallob-data
mkdir results
mkdir descriptions
cd ..
cp ./backend/src/main/resources/database/fallobDatabase.mv.db ./fallob-data/
cp ./backend/fallob-configuration-defaultPaths.json ./fallob-data/fallob-configuration.json

# Find correct command for docker compose / docker-compose
dockercmd="docker compose"
if docker-compose -v >/dev/null ; then dockercmd="docker-compose"; fi

MALLOB_DIR_SOURCE="1" MALLOB_DIR_TARGET="2" CONFIG_FILE_SOURCE="3" RESULT_DIR_SOURCE="4" RESULT_DIR_TARGET="5" DESCRIPTION_DIR_SOURCE="6" DESCRIPTION_DIR_TARGET="7" DATABASE_SOURCE="8" DATABASE_TARGET="9" $dockercmd build

