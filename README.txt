
wit-container
=============

Sehr schnelle und minimalistische IoC -Container-Implementierung
basierend auf Injection per Annotations.

Derzeit gelten folgende Praemisse:
  - Nur Setter-Injection mit nur einem Parameter
  - erzeugte Instancen sind Singletons 
  - Unterstuetzt Provider
  
Thread-sicher im Bereich der Erzeugung (sprich getInstance).
Alle bind-Aufrufe sind nicht thread-sicher, da davon ausgegangen wird,
dass das Binden von einen einzelnen Prozess gemacht wird.

Author: Bernd Ledig <bernd@ledig.info>
Author: Torsten Fehre <post@feson.de>


Anwendung:
 siehe JavaDocs der Klassen sowie 
 die Testbeispiele in srcTest/working_it/witcontainer/sample
 
Lizense: LPGL V3 (siehe license.txt)

 