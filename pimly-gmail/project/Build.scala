import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "pimly-gmail"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    jdbc,
    anorm,
    "com.sun.mail" % "javax.mail" % "1.4.7-rc1",
    "com.sun.mail" % "gimap" % "1.4.7-rc1",
    "securesocial" %% "securesocial" % "master-SNAPSHOT" exclude("javax.mail", "mail")
  )


  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here    
            
	resolvers += Resolver.url("SecureSocial Repository", url("http://securesocial.ws/repository/snapshots/"))(Resolver.ivyStylePatterns),
	resolvers += Resolver.url("sbt-plugin-snapshots", url("http://repo.scala-sbt.org/scalasbt/sbt-plugin-snapshots/"))(Resolver.ivyStylePatterns),
	resolvers += "java.net staging Repository" at "https://maven.java.net/content/groups/staging/"
		  
  )
  


}
