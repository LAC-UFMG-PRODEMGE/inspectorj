InspectorJ
========================
https://github.com/LAC-UFMG-PRODEMGE/inspectorj

* Java 7+
* Soot Framework
* Graphstream


Prerequisites
-------------------

Ensure you have JDK 7 (or newer) installed

    java -version

Ensure you have Eclipse kepler (or newer) installed

	https://www.eclipse.org/downloads/?


Building
-------------------

First of all you need to download the inspectorj application and import it to eclipse project

	git clone https://github.com/LAC-UFMG-PRODEMGE/inspectorj.git
	
InspectorJ application depends on **Soot** project, so you need to download it and import it to eclipse project

	git clone https://github.com/Sable/soot.git
	
Soot depends on two projects: **Heros** and **Jasmin**, so you also need to download it and import it to eclipse project

	git clone https://github.com/Sable/heros.git
	git clone https://github.com/Sable/jasmin.git
	
> P.S. InspectorJ uses **Lipstick Look and Feel** library to build its visual frame. You should put it in the project build path. The jar can be found at `inspectorj/lib` 

	

Running
-------------------

To start InspectorJ process, you need to run the method `main` of `br.ufmg.harmonia.inspectorj.InspectorSTPMain`.

	java br.ufmg.harmonia.inspectorj.InspectorSTPMain

InspectorJ will ask you the properties 



