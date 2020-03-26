#!/bin/bash

echo "Enter tag name:"
read NAME

git tag {NAME}
git push origin {NAME}
