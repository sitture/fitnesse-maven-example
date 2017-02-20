# fitnesse-mvn-example
An example maven based Fitnesse project.

## Starting FitNesse

```bash
mvn clean test
```

By default, fitnesse will start running on port 8082 at `http://127.0.0.1:8082`. To run it on a different port:

```bash
mvn clean test -Dport=9090
```

## Running a FitNesse Suite (JUnit)

You can run the following to run a suite headlessly.

```bash
mvn clean test-compile failsafe:integration-test
```

By default, fitnesse will run `FitNesse.SuiteAcceptanceTests` suite.

To run a different suite:

```bash
mvn clean test-compile failsafe:integration-test -DsuitePath=FitNesse.SuiteAcceptanceTests
```
## Base Urls

All of root urls can be stored in `FitNesse/plugins.properties` file.
**Note:** Any changes to the file requires a FitNesse restart.

Same file can be used to add any FitNesse related plugins.
