**Kojo Links**

* The [Kojo home-page][1] provides user-level information about Kojo.
* The [Kojo dev-board][2] gives you a high level view of what's going on with the development of Kojo. 
* The [Kojo issue-tracker][3] let's you file bug reports.

**To start hacking:**

* Fork the repo (i.e. create a server-clone), and then clone your fork (using the `hg clone` command) to create a local Kojo workspace.
* Make sure you have Java 6 on your path. You need Java 6 to build Kojo.  You can run Kojo with Java 6, Java 7, or Java 8.
    * [Download JDK 1.6][4] (if you don't already have it).
* Copy `javaws.jar` and `deploy.jar` from your `jre/lib` directory into the `lib` directory in your Kojo workspace. These jar files are required to compile the Kojo Webstart launcher.
* Run `./sbt.sh clean package` to build Kojo.
* Run `./sbt.sh test` to run the Kojo unit tests.
* Run `./sbt.sh run` to run Kojo (use `net.kogics.kojo.lite.DesktopMain` as the main class)
* Run `./sbt.sh eclipse` or `./sbt.sh gen-idea` to generate project files for Eclipse or IDEA (you should be able to do something similar for Netbeans after installing the sbt-netbeans plugin). Import the newly generated project into your IDE, and start hacking! For running Kojo from within the IDE, the main class is `net.kogics.kojo.lite.DesktopMain`. For debugging, the main class is `net.kogics.kojo.lite.Main`. 

**Eclipse Notes**:
You need to tweak the Eclipse project generated by sbt. Right-click on the project in Eclipse, bring up *Properties*, go to *Java Build Path*, and then go to *Libraries*. Remove the *Scala Library* and *Scala Compiler* containers, and add the Scala library and compiler jars (from your local Scala install, or cached sbt jars). Your project *Libraries* should now contain the following Scala jars:

* scala-library.jar
* scala-compiler.jar
* scala-reflect.jar
* scala-actors-xx.jar
* scala-parser-combinators-xx.jar
* scala-xml-xx.jar
* scala-swing-xx.jar
 
Also make sure that the *JRE System Library* used by the project is at the JDK 1.6 level.

**Localization:**

There are two levels of localization. 1st level means translation of all user interface texts. 2nd level means translation of the most important turtle commands.

1. **UI localization**: Translate file [Bundle.properties][5] to your local language giving it the name Bundle_xx.properties with xx being your language code. In file [LangMenuFactory.scala][8] add your language code to `val supportedLanguages` in line 31 and your language name to `private val langNames` in line 75.
2. **Turtle command localization**: Create translations of the swedish files [svInit.scala][6] as well as [sv.tw.kojo][7] using your language code instead of "sv". Add a call to your xxInit.init in file [LangInit.scala][9]

If you want to know the key of a UI text item (english or translation), create in your home directory a  file called `.kojo/lite/kojo.properties` and put the following entry in there:
 
i18n.string.showkey=true
 
Then (re)start Kojo. All UI texts will be shown in the format *localText[key]*.


  [1]: http://www.kogics.net/kojo
  [2]: https://trello.com/b/hxgeMSOj/kojo-development
  [3]: http://code.google.com/p/kojo/issues/list
  [4]: http://www.oracle.com/technetwork/java/javase/downloads/java-archive-downloads-javase6-419409.html
  [5]: http://code.google.com/p/kojolite/source/browse/src/main/resources/net/kogics/kojo/lite/Bundle.properties
  [6]: http://code.google.com/p/kojolite/source/browse/src/main/scala/net/kogics/kojo/lite/i18n/svInit.scala
  [7]: http://code.google.com/p/kojolite/source/browse/src/main/resources/i18n/initk/sv.tw.kojo
  [8]: http://code.google.com/p/kojolite/source/browse/src/main/scala/net/kogics/kojo/lite/LangMenuFactory.scala
  [9]: http://code.google.com/p/kojolite/source/browse/src/main/scala/net/kogics/kojo/lite/i18n/LangInit.scala
  
  
