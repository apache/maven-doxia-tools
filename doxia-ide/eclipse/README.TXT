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
  - Put apache-doxia-1.0-SNAPSHOT-jar-with-dependencies.jar in ./plugins/org.apache.maven.doxia.ide.eclipse.common.ui/lib

Known Limitations:
* Needs to bump to WSTP M6 due to:
  - https://bugs.eclipse.org/bugs/show_bug.cgi?id=212330
  - https://bugs.eclipse.org/bugs/show_bug.cgi?id=224040

TODO:
* mavenize the plugins
* Add more PatternRule for text plugin (Apt, confluence, twiki)
* Review editor colors

References:
* DOXIA-233: Create an Eclipse plugin to edit Doxia files
* DOXIA-228: Create a new tool to handle module conversion