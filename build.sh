#!/bin/bash

PROJECTDIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

echo "Enter version:"
read VERSION

BUILDDATE=$(date "+%d.%m.%Y %T")

echo "Entered version $VERSION"
echo "Build date $BUILDDATE"

BUILDPATH="/tmp/build/"
rm -rf $BUILDPATH
mkdir -p $BUILDPATH
cd $BUILDPATH

echo "Download JRE from AdoptOpenJDK for Windows"
wget https://github.com/AdoptOpenJDK/openjdk11-binaries/releases/download/jdk-11.0.6%2B10/OpenJDK11U-jdk_x64_windows_hotspot_11.0.6_10.zip
unzip OpenJDK11U-jdk_x64_windows_hotspot_11.0.6_10.zip
mv jdk-11.0.6+10 jre

echo "Download JRE from AdoptOpenJDK for MacOS"
wget https://github.com/AdoptOpenJDK/openjdk11-binaries/releases/download/jdk-11.0.6%2B10/OpenJDK11U-jdk_x64_mac_hotspot_11.0.6_10.tar.gz
tar -xvf OpenJDK11U-jdk_x64_mac_hotspot_11.0.6_10.tar.gz
mv jdk-11.0.6+10 jre_macos

git clone https://github.com/Cevi-Uster/hitobitoclient.git
cd hitobitoclient/cevi-db-client


sed -i '' "s/@version@/${VERSION}/g" src/main/java/ch/cevi/db/client/configuration/Version.java
sed -i '' "s/@releasedate@/${BUILDDATE}/g" src/main/java/ch/cevi/db/client/configuration/Version.java
sed -i '' "s/@version@/${VERSION}/g" Setup.nsi

mvn clean package appbundle:bundle  -Dmaven.test.skip=true
#mvn release:prepare --batch-mode -DreleaseVersion=${VERSION} -Dmaven.test.skip=true
#mvn release:clean -Dmaven.test.skip=true


makensis Setup.nsi
mkdir -p $PROJECTDIR/release
mv target/cevi-db-client-1.2-SNAPSHOT.dmg $PROJECTDIR/release/cevi-db-client-${VERSION}.dmg
mv target/*.exe $PROJECTDIR/release/

