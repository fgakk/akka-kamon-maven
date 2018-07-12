package com.fgakk.samples.akka

import akka.actor.ActorRef
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.pattern.ask
import akka.http.scaladsl.server.{Directives, Route}
import org.slf4j.Logger

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration.FiniteDuration

trait Routes extends Directives {

  implicit val executionContext: ExecutionContextExecutor
  implicit val log: Logger
  implicit val notesService: ActorRef
  implicit val timeout: FiniteDuration

  def routes: Route =
    path("notes") {
      get {
        val notes = (notesService ? GetAllNotes())(timeout)
          .mapTo[Map[Int, String]]
          .map(
            m => {
              val detailsJson = m
                .to[List]
                .sortBy(_._1) // sort by resource name (key) for consistent ordering
                .map(kv => s""""${kv._1}": "${kv._2}"""")
                .mkString(", ")

              val code = StatusCodes.OK // TODO error handling ???
              HttpResponse(code).withEntity(detailsJson)
            }
          )
        complete("Get request for all notes")
      } ~
        post {
          complete("Post request for a note")
        }
    } ~
      path("notes" / IntNumber) { id =>
        get {
          complete(s"Get request for note with $id")
        } ~
          put {
            complete(s"Put request for note with $id")
          } ~
          delete {
            complete(s"Delete request received with $id")
          }
      }

}
