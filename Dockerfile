FROM bde2020/spark-submit:3.1.1-hadoop3.2

COPY target/scala-**/*-assembly-*.jar app.jar

ENV SPARK_APPLICATION_MAIN_CLASS ro.neghina.crime.CrimeJob
ENV SPARK_APPLICATION_JAR_LOCATION app.jar

CMD ["/submit.sh"]