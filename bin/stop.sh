#!/bin/bash

originalDir=`pwd`
bashFileDir="$( cd "$( dirname "${BASH_SOURCE[0]}")" && pwd )"
serviceDir="$( cd "${bashFileDir}/.." && pwd )"

seconds=0
for pid in `pgrep java`; do
    one_dir=`readlink -f /proc/$pid/cwd | gawk '{ print $1 }'`
    if [ "$one_dir" != "" ] && [ "$one_dir" == "$serviceDir" ]; then
        echo -n 'killing' $pid '.'
        kill $pid
        while ps --pid $pid &> /dev/null; do
            sleep 1
            echo -n '.'
            seconds=$(($seconds + 1))
            if [ $seconds -eq 5 ] ; then
                echo -n 'killing -9 ' $pid '.'
                kill -9 $pid
            fi
        done
        echo 'done.'
    fi
done

cd ${originalDir}
