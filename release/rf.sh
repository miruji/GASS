#!/bin/bash

clear

# remove back
if [ -e "gass" ]; then
  rm -rm ./gass
fi

# debug mode
argcBegin=2
debug=false
for arg in "$@"
do
  if [[ "$arg" == "d" ]]; then
      debug=true
      argcBegin=3
  fi
done

# rust build
./rustBuild.sh $1 $debug
if [ $? -ne 0 ]; then
  echo "rf: Skipped"
  exit 1
fi

# run gass
if [ "$debug" == "true" ]; then
  ./gass -rf "${@:$argcBegin}" -d
else
  ./gass -rf "${@:$argcBegin}"
fi

# asm build
./asmBuild.sh

# run builded
./builded