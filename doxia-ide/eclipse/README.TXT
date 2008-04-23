Eclipse plugin for Apache Maven Doxia

Required Prerequisites:
* Eclipse SDK Platform (http://download.eclipse.org/eclipse/downloads/)
* Web Tools Platform (http://download.eclipse.org/webtools/downloads/)
  - EMF
  - EMF XSD
  - GEF
  - DTP
  - WTP Tool Developers
* doxia-converter (https://svn.apache.org/repos/asf/maven/doxia/doxia-tools/trunk/doxia-converter)
  - Need to build it 
  - Put apache-doxia-1.0-SNAPSHOT-jar-with-dependencies.jar in ./plugins/org.apache.maven.doxia.ide.eclipse.common.ui/lib - unneeded since r650510
* needs Java 5 to build since Eclipse uses Java 5
* M2_HOME needs to be set - unneeded since r651102

Known Limitations:
* Needs to bump to WSTP M6 due to:
  - https://bugs.eclipse.org/bugs/show_bug.cgi?id=212330
  - https://bugs.eclipse.org/bugs/show_bug.cgi?id=224040

TODO:
* mavenize the plugins - done in r650510
* Add more PatternRule for text plugin (Apt, confluence, twiki) - done in r650506, r650503, r650502
* Review editor colors - done in r650501
* add more actions like add list, comment/uncomment in Apt...

References:
* DOXIA-233: Create an Eclipse plugin to edit Doxia files
* DOXIA-228: Create a new tool to handle module conversion