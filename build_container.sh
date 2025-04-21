#!/bin/bash
# copy this file and rename to build_container.sh in the target repo root dir
set -euo pipefail
echo "Starting to build MyApp svt applications $0 $@"
# common vars for all svt applications
source wassvt-common/env.sh

set -x
rootDir=`pwd`
echo "rootDir=$rootDir"

# remove any maven if installed by default
sudo dnf erase -yq maven-openjdk*
sudo dnf erase -yq java-*-openjdk*
sudo dnf -yq module enable maven:3.8
sudo dnf install -yq maven-openjdk8
echo "JAVA_HOME=$JAVA_HOME"
unset JAVA_HOME
echo 'mvn version:' `mvn -v`

# cd into the repos to build
cd $(dirname $(readlink -f $0))

# build the application
mvn -q clean package

# adjust the tags below to match your application
# example is based on ACME single-Arch
# Travis -> EBC migration, look at the .travis file to see how / what is being built / pushed
appImage='acme/acme-ee7'
baseImage="icr.io/appcafe/websphere-liberty:full-java8-openj9-ubi"
[ "${container_branch}" == 'main' ] && branch_tag='' || branch_tag="${container_branch}-"

podman build -t tmpimage -f Containerfile --secret id=token,src=/tmp/.token --secret id=user,src=/tmp/.user --build-arg FULL_IMAGE=true  --build-arg BASE_IMAGE="${baseImage}" .
podman tag tmpimage $HYCSVT/${appImage}:${branch_tag}${tag}
podman push $HYCSVT/${appImage}:${branch_tag}${tag}
# Push to SVT_REGISTRY
podman tag tmpimage $SVT_REGISTRY/${appImage}:${branch_tag}${tag}
podman push $SVT_REGISTRY/${appImage}:${branch_tag}${tag}

#cleanup
[ -f /tmp/.user ] && rm -f /tmp/.user 
[ -f /tmp/.token ] && rm -f /tmp/.token