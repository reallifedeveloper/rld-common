RLD-COMMON
===========

Java code that can be reused in different projects.

Before building, you need to install the [parent POM](https://github.com/reallifedeveloper/rld-parent)
and [rld-build-tools](https://github.com/reallifedeveloper/rld-build-tools).

To build with all quality checks enabled:

    mvn -DcheckAll clean install

To create a Maven site with documentation, including Javadoc, in `target/site/index.html`:

    mvn -P coverage clean integration-test site

For more information, see <http://reallifedeveloper.com/>.
