#!/bin/sh
echo "Start travisInstallJre..."

extractPath=$1
jreUrl=$2
echo "* ExtractPath: $extractPath"
echo "* JreUrl: $jreUrl"

cd $extractPath
wget -c -O jre.tar.gz --no-check-certificate --no-cookies --header "Cookie: oraclelicense=accept-securebackup-cookie" "$jreUrl"
ls -al
cat jre.tar.gz
file jre.tar.gz
tar zxvf jre.tar.gz
