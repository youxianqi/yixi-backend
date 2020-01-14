#!/bin/bash

serviceName=yixi-backend
appClassName=youxianqi.yixi.App
JVM_OPTS_MS_SIZE=256M
JVM_OPTS_MX_SIZE=512M
extraArgs=$1

# common below

ENV=dev
if [ -f /opt/youxianqi/.ssinfo ]; then
    source /opt/youxianqi/.ssinfo
    ENV=$env_info
fi

originalDir=`pwd`
thisFileDir="$( cd "$( dirname "${BASH_SOURCE[0]}")" && pwd )"
serviceDir="$( cd "${thisFileDir}/.." && pwd )"
projectDir="$( cd "${serviceDir}/../.." && pwd )"
logDir=${projectDir}/log/${serviceName}
dataDir=${projectDir}/data/${serviceName}
mkdir -p ${logDir}
mkdir -p ${dataDir}

echo "serviceDir: $serviceDir"
echo "projectDir: $projectDir"
sh  ${serviceDir}/bin/stop.sh
cd  ${serviceDir}

JAVA_OPT="-server -Xms${JVM_OPTS_MS_SIZE} -Xmx${JVM_OPTS_MX_SIZE} -XX:+PrintGCDetails -Xloggc:${logDir}/gc.log"
JAVA_ARGS=" -Dspring.profiles.active=$ENV  -Dservice_log_dir=${logDir}  -Dfile.encoding=utf-8  ${extraArgs}  "

nohup  java  $JAVA_OPT  $JAVA_ARGS  -cp  ${serviceDir}/control:${serviceDir}/javalib/*  ${appClassName}  &> ${logDir}/nohup.out &

curr_dir=`pwd`
fails=0
while [ $fails -le 3 ]; do
    for pid in `pgrep java`; do
        one_dir=`readlink -e /proc/$pid/cwd`
        if [ "x$one_dir" = "x$curr_dir" ]; then
            echo $pid':' $one_dir
            cd  ${originalDir}
            exit 0
        fi
    done
    sleep 1
    fails=$(($fails + 1))
done
echo 'start error...'

tail  -n  15  ${logDir}/nohup.out

cd  ${originalDir}
