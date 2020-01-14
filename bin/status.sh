#!/bin/bash

originalDir=`pwd`
bashFileDir="$( cd "$( dirname "${BASH_SOURCE[0]}")" && pwd )"
serviceDir="$( cd "${bashFileDir}/.." && pwd )"

found="NO"
for pid in `pgrep java`; do
    one_dir=`readlink -f /proc/$pid/cwd | gawk '{ print $1 }'`
    if [ "$one_dir" != "" ] && [ "$one_dir" == "$serviceDir" ]; then
        echo "INFO: ${serviceDir} is running, pid=${pid}"
        found="YES"
    fi
done

if [ "${found}" == "NO" ]; then
    echo "WARNING:   ${serviceDir} is not running ..."
fi

cd ${originalDir}
