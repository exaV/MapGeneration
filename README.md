Ether-GL
========

A versatile, modular Java framework for 3D rendering, object editing, visualization and projection mapping. Primarily targeted at OpenGL 3.3+.

Manifesto
-------------

The goal of ether-gl is to create a versatile, modular graphics library, which builds on modern graphics hardware APIs (primarily OpenGL, but it is open enough to use different target engines, as our very simple ray tracing examples show). 

* We aim at keeping the library slim, and do not support legacy hardware / drivers (OpenGL 3.3 is required for GL targets). 

* We aim at high performance, but since we use the library for teaching, we keep the code design and style to a certain degree academic and didactic, which sometimes leads to small compromises. 

* We aim at perfecting the APIs, without compromise to legacy.

* Yes, we mainly use Java, excepted for some native code where appropriate (e.g. native movie/video support). We mainly focus on convenience and concepts that could be applied / ported to any language.

We will add further modules while working on the core library. The video library (part of which will go into the core library) is quite usable. Animation and physics will follow quickly.


Repository
----------

The repository contains an Eclipse project, including dependencies such as JOGL etc. Thus the code should run out of the box on Mac OS X, Windows and Linux.


Further Info & Contact
----------------------

For questions etc. feel free to be in touch with me (Stefan Müller Arisona) at robot@arisona.ch


Credits
-------

Stefan Müller Arisona

Simon Schubiger

Samuel von Stachelski

Contributions by: Eva Friedrich