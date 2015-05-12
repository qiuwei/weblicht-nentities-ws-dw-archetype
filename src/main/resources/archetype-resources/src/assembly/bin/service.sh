#!/bin/bash
#
# This is a simple startup script that should work for the
# dropwizard based web services. This script needs to be renamed as
# the name of the service to function properly.
#

APPNAME=$(basename $0)
BASEDIR=${BASEDIR:-$(dirname $0)}
CONFFILE=${CONFFILE:-${BASEDIR}/${APPNAME}.yaml}
HEAPSIZE=${HEAPSIZE:-2048m}
JARFILE=${SERVICEJARFILE:-${BASEDIR}/${APPNAME}.jar}

while getopts ":c:h:" opt; do
    case $opt in
        c) CONFFILE=${OPTARG}
        ;;
        h) HEAPSIZE=${OPTARG}
        ;;
    esac
done


cd ${BASEDIR}
LOGDIR=${LOGDIR:-.}
ERRLOG=${LOGDIR}/${APPNAME}-error.log

exec java -Xmx${HEAPSIZE} -jar ${JARFILE} server ${CONFFILE} $@ &>> $ERRLOG
