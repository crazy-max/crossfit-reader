#!/bin/sh
echo "Start travisSwitchJdk..."

jdkUrl=`sed '/^\#/d' build.properties | grep '^travis.jdk.url' | tail -n 1 | cut -d "=" -f2- | sed 's/^[[:space:]]*//;s/[[:space:]]*$//'`
echo "jdkUrl: $jdkUrl"

wget --no-cookies --header "Cookie: oraclelicense=accept-securebackup-cookie" $jdkUrl -O /tmp/jdk.tar.gz
tar -xzvf /tmp/jdk.tar.gz -C $HOME
mv $HOME/jdk* $HOME/jdk
echo "export JAVA_HOME=$HOME/jdk" >> ~/.bash_profile
echo "export PATH=$PATH:$JAVA_HOME/bin" >> ~/.bash_profile

export JAVA_HOME=$HOME/jdk
export PATH=$PATH:$JAVA_HOME/bin
echo "JAVA_HOME: $JAVA_HOME"
echo "PATH: $PATH"
