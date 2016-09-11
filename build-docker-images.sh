#!/bin/sh

for service in counter-service greeting-service master-service
do
    ( cd $service && ./gradlew buildDocker )
done

