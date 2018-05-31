#!/usr/bin/env bash

conf_file="conf/flyway.conf"

if [ ! -f $conf_file ]
then
   echo "Configure file $conf_file does not exist!"
fi

classpath=$(find "$(pwd)" -name \*.jar | awk 1 ORS=':')
echo "$classpath"

java -cp "$classpath" com.here.owc.database.DatabaseMigrationManager "$@" || exit 1

