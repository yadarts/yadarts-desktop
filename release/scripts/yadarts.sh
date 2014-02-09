#! /bin/sh

if [ -n "${JAVA_HOME+x}" ]; then
	echo using java at $JAVA_HOME
else
	echo \$JAVA_HOME environment variable is not set. please set in your system or in this script
fi

#use this if $JAVA_HOME is not set in your system:
#JAVA_HOME=/opt/java/sun-jdk6


#additional classpath entries. place your libs for plugins (also third party libs) here. multiple entries seperated by ':' (normal java syntax)
ADDITIONAL_CLASSPATH=


startApp() {

	$JAVA_HOME/bin/java -Dfile.encoding=UTF-8 -classpath lib/'*':bin/'*':$ADDITIONAL_CLASSPATH spare.n52.yadarts.YadartsDesktopMain
	OUT=$?
	if [ $OUT -eq 100 ];then
	   echo ""
	   echo "[yadarts] Restarting application..."
	   echo ""
	   startApp

	else
	   echo "[yadarts] Exiting"
	fi

}

startApp
