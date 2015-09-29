name := "http4s-eval"

version := "1.0"

scalaVersion := "2.10.4"

libraryDependencies ++= Seq(
  "log4j"                     %  "log4j"                  % "1.2.17",
  "org.slf4j"                 %  "slf4j-log4j12"          % "1.7.9",
  "org.http4s" %% "http4s-dsl"          % "0.10.0",  // to use the core dsl
  "org.http4s" %% "http4s-blaze-server" % "0.10.0",  // to use the blaze backend
  "org.http4s" %% "http4s-servlet"      % "0.10.0",  // to use the raw servlet backend
  "org.http4s" %% "http4s-jetty"        % "0.10.0",  // to use the jetty servlet backend
  "org.http4s" %% "http4s-blaze-client" % "0.10.0"  // to use the blaze client
)

resolvers ++= Seq(
  "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases"
)