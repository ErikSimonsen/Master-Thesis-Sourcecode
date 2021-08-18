#!/bin/bash

CORES=$1
RUN=$2
SERVER=$3
CLIENT=$4

PROJ_DIR=$(pwd)

jbang ./scripts/resultsProcessing/ProcessResults.java -c $CORES -i $PROJ_DIR/results/data/$RUN/$CLIENT:$PROJ_DIR/results/data/$RUN/$SERVER -o $PROJ_DIR/results/data/$RUN/runResult.json

cd ./scripts/graphsGeneration/
mkdir -p $PROJ_DIR/results/graphs/$RUN
npm i
node ./genAllGraphsFromConfig.js $PROJ_DIR/scripts/graphsGeneration/graphConf.json $PROJ_DIR/results/data/$RUN/runResult.json $PROJ_DIR/results/graphs/$RUN $CORES

cd $PROJ_DIR

