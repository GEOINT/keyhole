keyhole reverse proxy
=====================

A servlet-based, JavaEE7 compliant (including web-profile), reverse proxy.

This projects goal is to produce a high-quality, feature rich, reverse proxy that can be easily deployed and managed on all TCK compliant JavaEE7 application containers.  

This project is at it's very infantile stage right now, but is opertional thanks to a "seed" fork from an abandoned project called J2EP (http://j2ep.sourceforge.net).  I'm using this code base as a "launching off" point, but have already made numerous changes to that code base to both get it to run with modern technologies as well as work past several bugs.  The first milestone of this project will be to stabilize the code base, focusing on the following goals:

* bring the code base up to modern technologies -- and maintain it
* Remove the no longer support unit test framework and replace the unit testing with a modern version of JUnit.  Developing a rich set of tests will be my first goal.
* Remove "common" implementation dependencies so as to avoid classpath hell.
* completly convert to a maven build process

Please submit issues for any problems or features you would like to see in this project.
