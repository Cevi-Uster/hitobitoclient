#!/bin/bash

PROJECTDIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

echo "Enter version:"
read VERSION

BUILDDATE=$(date "+%d.%m.%Y %T")

echo "Entered version $VERSION"
echo "Build date $BUILDDATE"

BUILDPATH="/tmp/build/cevi-db-client"
rm -rf $BUILDPATH
mkdir -p $BUILDPATH
cd $BUILDPATH
svn co svn+ssh://mbaumgar@asterix.fritz.box/Volumes/Extern1/share/svn/hitobitoclient/trunk/cevi-db-client
cd cevi-db-client
sed -i '' "s/@version@/${VERSION}/g" src/main/java/ch/cevi/db/client/configuration/Version.java
sed -i '' "s/@releasedate@/${BUILDDATE}/g" src/main/java/ch/cevi/db/client/configuration/Version.java
sed -i '' "s/@version@/${VERSION}/g" Setup.nsi
mvn clean package appbundle:bundle  -Dmaven.test.skip=true
makensis Setup.nsi
mv target/cevi-db-client-1.0-SNAPSHOT.dmg $PROJECTDIR/release/cevi-db-client-${VERSION}.dmg
mv target/*.exe $PROJECTDIR/release/

