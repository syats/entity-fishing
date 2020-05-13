FROM maven:3-openjdk-8

RUN mkdir /grobid
RUN git clone --single-branch --branch 0.6.0-fixes https://github.com/kermitt2/grobid.git

RUN mkdir /grobid-ner
RUN git clone https://github.com/kermitt2/grobid-ner.git

# as per grobid-ner documentation  https://grobid-ner.readthedocs.io/en/latest/build-and-install/
RUN mv /grobid-ner/grobid-ner /grobid/
# # RUN cd grobid/ && mvn -Dmaven.test.skip=true clean install
RUN mv grobid-ner/grobid-home/models/* /grobid/grobid-home/models

RUN echo "version=0.6.0-SNAPSHOT" > /grobid/gradle.properties && echo "org.gradle.workers.max=1" >> /grobid/gradle.properties
RUN cd /grobid/ && ./gradlew clean install
RUN cp /grobid/grobid-trainer/build/libs/grobid-trainer-0.6.0-SNAPSHOT.jar /grobid/grobid-ner/lib
RUN cp /grobid/grobid-core/build/libs/grobid-core-0.6.0-SNAPSHOT.jar /grobid/grobid-ner/lib
RUN cd /grobid/grobid-ner && mvn -Dmaven.test.skip=true clean install

RUN mkdir /fishing
RUN cd /fishing && git clone https://github.com/kermitt2/nerd
ADD mention.yaml /fishing/nerd/data/config
RUN cd /fishing/nerd/ && mvn -DskipTests clean install

WORKDIR /fishing/nerd/
CMD ["mvn", "jetty:run"]

# docker run --rm --name fishing1 --p 9897:8090 -it fishing
