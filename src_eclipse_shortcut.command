#!/usr/bin/env bash

# get directory path ---------------------
PRG="$0"

while [ -h "$PRG" ]; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`/"$link"
  fi
done

# Get standard environment variables
PRGDIR=`dirname "$PRG"`

/Users/beef_in_jp/Documents/eclipse/Eclipse -data $PRGDIR/src -vmargs -Xms128m -Xmx512m &

