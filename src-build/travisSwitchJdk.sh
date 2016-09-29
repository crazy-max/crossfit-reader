#!/bin/sh
echo "Start travisSwitchJdk..."

jdkUrl=`sed '/^\#/d' build.properties | grep '^travis.jdk.url' | tail -n 1 | cut -d "=" -f2- | sed 's/^[[:space:]]*//;s/[[:space:]]*$//'`
echo "jdkUrl: $jdkUrl"

wget --no-cookies --header "Cookie: oraclelicense=accept-securebackup-cookie" $jdkUrl -O /tmp/jdk.tar.gz
tar -xzvf /tmp/jdk.tar.gz -C $HOME
echo "export JAVA_HOME=$HOME/jdk1.8.0_66" >> ~/.bash_profile
echo "export PATH=$PATH:$JAVA_HOME/bin" >> ~/.bash_profile

source ~/.bash_profile
echo "JAVA_HOME: $JAVA_HOME"
echo "PATH: $PATH"
