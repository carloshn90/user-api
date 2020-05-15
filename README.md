# user-api

An example project showing how to use http4s and doobie with
authorization middleware using Jwt token.

## Using this project

You should be able to import this project into IntelliJ-IDEA or any other Scala
IDE or editor without issue.

## Introduction

The goal of this project is to create a small microservice using 
functional code. The main libraries in this project are 
Cats-effect, Http4s and Doobie. 

## Usage

To compile-and-run:

```
sbt run
```

To test:

```
sbt test
```

We are using stryker to test our test (Mutation test):

```
sbt stryker
```