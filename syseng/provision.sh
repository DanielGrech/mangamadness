#!/bin/bash

echo "Installing git"
sudo apt-get install git

echo "Installing Golang"
sudo apt-get install golang

echo "Installing beanstalk"
sudo apt-get install beanstalkd

echo "Installing MongoDb"
sudo apt-get install mongodb

echo "Installing MangaMadness source"
git clone git@github.com:DanielGrech/mangamadness.git