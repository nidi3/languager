Languager [![Build Status](https://travis-ci.org/nidi3/languager.png?branch=master)](https://travis-ci.org/nidi3/languager)
=========

A simple tool to handle internationalization of source code.

- It extracts keys and default translations out of source files using regexes.
- For each language, it generates a set of translated files where the regexes are replaced with their actual value in a given language.
- It can generate properties files to be used by java.
- It can perform validity checks on the translated values.
- It provides a form of online translation directly inside the application.

See languager-demo module for how it works.

