[![CircleCI](https://circleci.com/gh/artshishkin/apache-camel-learn-by-coding-in-spring-boot.svg?style=svg)](https://circleci.com/gh/artshishkin/apache-camel-learn-by-coding-in-spring-boot)
[![codecov](https://codecov.io/gh/artshishkin/apache-camel-learn-by-coding-in-spring-boot/branch/master/graph/badge.svg)](https://codecov.io/gh/artshishkin/apache-camel-learn-by-coding-in-spring-boot)
![Java CI with Maven](https://github.com/artshishkin/apache-camel-learn-by-coding-in-spring-boot/workflows/Java%20CI%20with%20Maven/badge.svg)
[![GitHub issues](https://img.shields.io/github/issues/artshishkin/apache-camel-learn-by-coding-in-spring-boot)](https://github.com/artshishkin/apache-camel-learn-by-coding-in-spring-boot/issues)

![Spring Boot version][springver]
![Tested with Camel version][camelver]
![Project licence][licence]

# Tutorial on Building Apache Camel applications using Spring Boot Framework. - Dilip S (Udemy)

## App# 1 - Build a Simple Camel Route - File -> DB -> MAIL

## App# 2 - Build a Kafka Camel Route - Kafka -> DB -> MAIL

- [Kafka_Commands-course-related](https://github.com/artshishkin/apache-camel-learn-by-coding-in-spring-boot/blob/master/app02-kafka-db-mail/Kafka_Commands-course-related.md)

## App# 3 - Build an ActiveMQ Camel Route - ActiveMQ -> DB -> MAIL

- [ActiveMQ_Setting_Up](https://github.com/artshishkin/apache-camel-learn-by-coding-in-spring-boot/blob/master/app03-activemq-db-mail/ActiveMQ_Setting_Up.md)

## App# 4 - Build a REST Camel Route - REST -> RESTLET-> DB -> MAIL

- [REST Coutries](https://restcountries.eu/)


[camelver]: https://img.shields.io/badge/dynamic/xml?label=Camel&query=%2F%2A%5Blocal-name%28%29%3D%27project%27%5D%2F%2A%5Blocal-name%28%29%3D%27properties%27%5D%2F%2A%5Blocal-name%28%29%3D%27camel-spring-boot-starter.version%27%5D&url=https%3A%2F%2Fraw.githubusercontent.com%2Fartshishkin%2Fapache-camel-learn-by-coding-in-spring-boot%2Fmaster%2Fpom.xml&labelColor=white&color=grey&logo=data:image/png;base64,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
[springver]: https://img.shields.io/badge/dynamic/xml?label=Spring%20Boot&query=%2F%2A%5Blocal-name%28%29%3D%27project%27%5D%2F%2A%5Blocal-name%28%29%3D%27parent%27%5D%2F%2A%5Blocal-name%28%29%3D%27version%27%5D&url=https%3A%2F%2Fraw.githubusercontent.com%2Fartshishkin%2Fapache-camel-learn-by-coding-in-spring-boot%2Fmaster%2Fpom.xml&logo=Spring&labelColor=white&color=grey
[licence]: https://img.shields.io/github/license/artshishkin/apache-camel-learn-by-coding-in-spring-boot.svg