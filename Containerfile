ARG BASE_IMAGE=icr.io/appcafe/open-liberty:kernel-slim-java8-openj9-ubi
#ARG BASE_IMAGE=icr.io/appcafe/open-liberty:kernel-slim-java11-openj9-ubi
#ARG BASE_IMAGE=open-liberty:beta-java17
#ARG BASE_IMAGE=icr.io/appcafe/open-liberty:beta

FROM $BASE_IMAGE

COPY  --chown=1001:0  ./AcmeWebEjbEar/target/AcmeWebEjbEar.ear /config/apps/
COPY  --chown=1001:0  config/server.xml /config/server.xml
COPY  --chown=1001:0  config/jvm.options /config/jvm.options
COPY  --chown=1001:0  config/ACME_Liberty_combined_docker.xml /config/ACME_Liberty_combined_docker.xml


# It was decided not to use submodules for getting utility apps but using curl commands to get released versions
# COPY  --chown=1001:0  sharedApps/badapp.war /config/dropins
# COPY  --chown=1001:0  sharedApps/microwebapp.war /config/dropins
# COPY  --chown=1001:0  sharedApps/svtMessageApp.war /config/dropins
#COPY  --chown=1001:0 ltpa.keys /output/resources/security/ltpa.keys


# Getting war files for utility applications

User root

# see utility apps release notes to determine what app version to use
# https://github.ibm.com/was-svt/svtMessageApp/releases 
# https://github.ibm.com/was-svt/microwebapp/releases 
# https://github.ibm.com/was-lumberjack/badapp/releases

RUN --mount=type=secret,id=token --mount=type=secret,id=user\
      mkdir -p /mytemp && cd /mytemp && curl --retry 7 -sSf -u $(cat /run/secrets/user):$(cat /run/secrets/token) \
      -O 'https://na.artifactory.swg-devops.com/artifactory/hyc-wassvt-team-maven-virtual/svtMessageApp/svtMessageApp/1.0.3/svtMessageApp-1.0.3.war' \
      && curl --retry 7 -sSf -u $(cat /run/secrets/user):$(cat /run/secrets/token) \
      -O 'https://na.artifactory.swg-devops.com/artifactory/hyc-wassvt-team-maven-virtual/microwebapp/microwebapp/1.0.1/microwebapp-1.0.1.war' \
      && curl --retry 7 -sSf -u $(cat /run/secrets/user):$(cat /run/secrets/token) \
      -O 'https://na.artifactory.swg-devops.com/artifactory/hyc-wassvt-team-maven-virtual/com/ibm/ws/lumberjack/badapp/1.0.1/badapp-1.0.1.war' \
      && chown -R 1001:0 /mytemp/*.war  && mv /mytemp/*.war /config/dropins
user 1001

#DB2 files
COPY --chown=1001:0 ./db2jars /config/db2jars

#JaxRS 3rd party lib
#RUN mkdir /opt/ibm/wlp/usr/shared/resources/thirdPartyLib
COPY --chown=1001:0 ./thirdPartyLib/* /config/thirdPartyLib/

#truststore for LDAP
COPY  --chown=1001:0 config/trustStore.jks /config/trustStore.jks

#Add trust file for jaxrs-secure
#RUN mkdir /opt/ibm/wlp/usr/shared/resources/security
COPY --chown=1001:0 config/trust.jks /config/trust.jks

#create dir structure for ACME logs
RUN mkdir /logs/ACME
RUN mkdir /logs/ACME/client

# Setting for the verbose option
ARG VERBOSE=true
ARG FULL_IMAGE=false

# This script will add the requested XML snippets to enable Liberty features and grow image to be fit-for-purpose using featureUtility.
# Only available in 'kernel-slim'. The 'full' tag already includes all features for convenience.

#RUN if [[ -z "$FULL_IMAGE" ]] ; then echo Skip running features.sh for full image ; else  ; fi
#RUN if [ "$FULL_IMAGE" = "true" ] ; then echo "Skip running features.sh for full image" ; else features.sh ; fi
#RUN features.sh

# Add interim fixes for WL/OL (optional)
#COPY --chown=1001:0  interim-fixes /opt/ol/fixes/
#COPY --chown=1001:0  interim-fixes /opt/ibm/fixes/

# This script will add the requested XML snippets and grow image to be fit-for-purpose
RUN configure.sh