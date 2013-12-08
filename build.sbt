name := "akka-chat"

version := "1.0"

description := "A simple chat server/client app demonstrating the Akka actors model"

resolvers ++= Seq("Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
                  "Sonatype snapshots"  at "http://oss.sonatype.org/content/repositories/snapshots/",
                  "Spray Repository"    at "http://repo.spray.io",
                  "Spray Nightlies"     at "http://nightlies.spray.io/")
 
scalaVersion := "2.10.2"

libraryDependencies ++= {
	val akkaVersion       = "2.2.3"
	val sprayVersion      = "1.1-20130123"
	Seq(
	  	"com.typesafe.akka" %% "akka-actor" % akkaVersion,
	  	"com.typesafe.akka" %% "akka-testkit" % akkaVersion,
	  	"com.typesafe.akka" %% "akka-remote" % akkaVersion,
	  	"io.spray"          %  "spray-can"       % sprayVersion,
	  	"io.spray"          %  "spray-routing"   % sprayVersion,
	  	"io.spray"          %% "spray-json"      % "1.2.3"
	)
}
