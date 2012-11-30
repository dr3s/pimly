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
    "securesocial" % "securesocial_2.10" % "master"
  )


  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here    
            
	resolvers += Resolver.url("SecureSocial Repository", url("http://securesocial.ws/repository/snapshots/"))(Resolver.ivyStylePatterns)
		  
  )

}
