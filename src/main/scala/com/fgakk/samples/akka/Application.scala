package com.fgakk.samples.akka

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.server.HttpApp
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import kamon.Kamon
import kamon.prometheus.PrometheusReporter
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration._

object Application extends HttpApp with Routes {

  val log: Logger                                         = LoggerFactory.getLogger(Application.getClass)
  implicit val system: ActorSystem                        = ActorSystem("akka-kamon-maven")
  implicit val materializer: ActorMaterializer            = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher
  implicit val timeout: FiniteDuration                    = 2.seconds
  val notesService: ActorRef                              = system.actorOf(NotesService())

  def main(args: Array[String]): Unit = {

    try {
      val config = ConfigFactory.load()
      // Load reporter for Kamon
      Kamon.addReporter(new PrometheusReporter())
      // Start http server
      Application.startServer(
        config.getString("app.http.host"),
        config.getInt("app.http.port")
      )
    } catch {
      case e: Exception =>
        log.error("error during startup... shut the server down", e)
        System.exit(-1)
    }

  }
}
