Finloader
=========

ETL tool for personal finance. Basically it can load your CSV files of special format containing records of
expenses, incomes and balance status into relational database so that further analysis via SQL queries or
more advanced business intelligence tools is possible.

## Quickstart guide

### Prerequisites

1. JDK (at least 1.6)
2. [Sbt 0.13](http://www.scala-sbt.org/release/docs/Getting-Started/Setup.html)
3. Relational database. Preferably PostgreSQL - its driver is included by default in ./build.sbt

### Build

From project root folder:

    sbt assembly

Then you'll get `target/scala-2.10/finloader-assembly-1.0.jar` file that you may run in java environment

If you want to run integration tests - copy `it.conf.sample` to `it.conf`, put there correct values and run

    sbt it:test

At the moment project doesn't have unit tests - only integrational.

### Run

Copy `sample_data/fintracker.conf.sample` to convinient place, rename it to fintracker.conf (or something like this)
and put there correct settings: separator used in your CSV files and JDBC URL for your database.
Create directory with your data files with expenses, balances and income (let's call it `dataset`). Sample files containing such data can be found in `sample_data/dataset1`
Supposing you have config file, data directory and .jar file in one directory you may run following command to load your data into the database:

    java -jar finloader-assembly-1.0.jar --data dataset --config fintracker.conf

If you use other database then PostgreSQL you need to provide JDBC driver via classpath modification or via changing
project dependencies in `build.sbt` and rebuilding the project.

## License

Licensed under Apache 2.0 license:
http://www.apache.org/licenses/LICENSE-2.0.txt
