FROM maven:3-adoptopenjdk-11 as builder
ARG POM_VERSION
WORKDIR /automation-model
COPY ./pom.xml /automation-model
COPY ./settings.xml /automation-model
RUN mvn --settings settings.xml dependency:go-offline
COPY . /automation-model
RUN mvn --settings settings.xml package -B
RUN mvn --settings settings.xml deploy:deploy-file -DgroupId=lk.dialog.crm -DartifactId=qa-automation-model -Dversion=${POM_VERSION} -Dpackaging=jar -Dfile=target/qa-automation-model-${POM_VERSION}.jar -DrepositoryId=nexus -Durl=https://devops.dialog.lk/repository/repository/maven-releases/
