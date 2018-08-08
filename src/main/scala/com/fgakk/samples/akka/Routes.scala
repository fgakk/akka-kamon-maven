package com.fgakk.samples.akka

import akka.actor.ActorRef
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.pattern.ask
import akka.http.scaladsl.server.{Directives, Route}
import org.slf4j.Logger

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.concurrent.duration.FiniteDuration

trait Routes extends Directives {

  implicit val executionContext: ExecutionContextExecutor
  implicit val log: Logger
  implicit val notesService: ActorRef
  implicit val timeout: FiniteDuration

  def routes: Route =
    path("notes") {
      get {
        val response: Future[HttpResponse] = (notesService ? GetAllNotes())(timeout)
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
        complete(response)
      } ~
        post {
          formField("value") { value =>
            val response: Future[HttpResponse] = (notesService ? CreateNote(value))(timeout)
              .mapTo[NoteCreated]
              .map(
                m => {
                  val code = StatusCodes.OK
                  HttpResponse(code).withEntity(s"note crate with id ${m.id}")
                }
              )
            complete(response)
          }

        }
    } ~
      path("notes" / IntNumber) { id =>
        get {
          val response: Future[HttpResponse] = (notesService ? GetNote(id))(timeout)
            .mapTo[Option[String]]
            .map {
              case Some(value) =>
                val code = StatusCodes.OK // TODO error handling ???
                HttpResponse(code).withEntity(value)
              case None =>
                val code = StatusCodes.NotFound // TODO error handling ???
                HttpResponse(code)
            }
          complete(response)
        } ~
          delete {
            val response: Future[HttpResponse] = (notesService ? DeleteNote(id))(timeout)
              .mapTo[NoteDeleted]
              .map(m => {
                val code = StatusCodes.OK // TODO error handling ???
                HttpResponse(code).withEntity("Note deleted")
              })
            complete(response)
          }
      }

}
