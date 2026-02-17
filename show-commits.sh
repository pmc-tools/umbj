#!/bin/bash

REMOTE=prism
BRANCH=master

git fetch ${REMOTE}

echo
echo "Local commits"
git log -n 5 --oneline

echo
echo "Latest changes in ${REMOTE}/${BRANCH}:"
git log prism/master --oneline -- prism/src/io/github

