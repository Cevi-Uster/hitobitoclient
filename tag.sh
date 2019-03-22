#!/bin/bash

echo "Enter tag name:"
read NAME

svn copy svn+ssh://mbaumgar@asterix.fritz.box/Volumes/Extern1/share/svn/hitobitoclient/trunk/ svn+ssh://mbaumgar@asterix.fritz.box/Volumes/Extern1/share/svn/hitobitoclient/tags/${NAME} -m "Tagging ${NAME}"