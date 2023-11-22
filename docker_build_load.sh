#!/bin/bash
# Prereq.:
# - Setup docker buildkit -> buildx for platform linux/amd64,linux/arm64

set -e # exit if any failure occurs

if [[ "$1" == "" ]];  then
  echo "Evaluating version..."
  export VERSION=`mvn help:evaluate -Dexpression=revision -q -DforceStdout`
else
  export VERSION="$1"
fi

echo "VERSION=${VERSION}"

ROOT_DIR=$PWD

echo "############################################################################################################"
echo " basyx.aasregistry/basyx.aasregistry-service-release-log-mongodb"
echo "############################################################################################################"
cd $ROOT_DIR/basyx.aasregistry/basyx.aasregistry-service-release-log-mongodb  || exit

mvn clean install \
    -DskipTests \
    -Ddocker.provenance=false \
    -Ddocker.namespace=registry.devinf.objective-partner.net/i40-forks/basyx-java-server-sdk

echo "############################################################################################################"
echo " basyx.submodelregistry/basyx.submodelregistry-service-release-log-mongodb"
echo "############################################################################################################"
cd $ROOT_DIR/basyx.submodelregistry/basyx.submodelregistry-service-release-log-mongodb  || exit

mvn clean install \
    -DskipTests \
    -Ddocker.provenance=false \
    -Ddocker.namespace=registry.devinf.objective-partner.net/i40-forks/basyx-java-server-sdk

echo "############################################################################################################"
echo " basyx.aasrepository/basyx.aasrepository.component"
echo "############################################################################################################"
cd $ROOT_DIR/basyx.aasrepository/basyx.aasrepository.component  || exit

docker buildx build \
  --load \
  --no-cache \
  --provenance false \
  --platform linux/arm64 \
  -t registry.devinf.objective-partner.net/i40-forks/basyx-java-server-sdk/aas-repository:$VERSION .

echo "############################################################################################################"
echo " basyx.conceptdescriptionrepository/basyx.conceptdescriptionrepository.component"
echo "############################################################################################################"
cd $ROOT_DIR/basyx.conceptdescriptionrepository/basyx.conceptdescriptionrepository.component  || exit

docker buildx build \
  --load \
  --no-cache \
  --provenance false \
  --platform linux/arm64 \
  -t registry.devinf.objective-partner.net/i40-forks/basyx-java-server-sdk/cd-repository:$VERSION .

echo "############################################################################################################"
echo " basyx.submodelrepository/basyx.submodelrepository.component"
echo "############################################################################################################"
cd $ROOT_DIR/basyx.submodelrepository/basyx.submodelrepository.component  || exit

docker buildx build \
  --load \
  --no-cache \
  --provenance false \
  --platform linux/arm64 \
  -t registry.devinf.objective-partner.net/i40-forks/basyx-java-server-sdk/submodel-repository:$VERSION .
