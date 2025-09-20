RealLifeDeveloper: rld-common
=============================

This project is providing source code for the [RealLifeDeveloper blog](https://reallifedeveloper.com/). It provides code that can be
reused by different projects.

You are free to use this code in your own projects as you see fit, see the [license](LICENSE).

If you encounter any problems or have any suggestions for improvements, please use the Github issue tracking system:
https://github.com/reallifedeveloper/rld-common/issues

If you would like to contribute to this project, please take a look [here](CONTRIBUTING.md).

The code is available on Maven Central, so you can use it directly in a Maven project:
```
<dependency>
    <groupId>com.reallifedeveloper</groupId>
    <artifactId>rld-common</artifactId>
    <version>${rld-common.version}</version>
</dependency>
```

To build with all quality checks enabled:

    mvn -DcheckAll clean install

To create a Maven site with documentation, including Javadoc, in `target/site/index.html`:

    mvn -P coverage clean integration-test site

For more information, see <https://reallifedeveloper.com/maven-site/rld-common>.

[![CI](https://github.com/reallifedeveloper/rld-common/actions/workflows/main.yaml/badge.svg)](https://github.com/reallifedeveloper/rld-common/actions/workflows/main.yaml)
[![CD](https://github.com/reallifedeveloper/rld-common/actions/workflows/release.yaml/badge.svg)](https://github.com/reallifedeveloper/rld-common/actions/workflows/release.yaml)

![Maven Central Version](https://img.shields.io/maven-central/v/com.reallifedeveloper/rld-common)
[![License](https://img.shields.io/:license-mit-blue.svg)](https://badges.mit-license.org)


[![OpenSSF Scorecard](https://api.scorecard.dev/projects/github.com/reallifedeveloper/rld-common/badge)](https://scorecard.dev/viewer/?uri=github.com/reallifedeveloper/rld-common)
[![OpenSSF Best Practices](https://bestpractices.coreinfrastructure.org/projects/10898/badge)](https://bestpractices.coreinfrastructure.org/projects/10898)
