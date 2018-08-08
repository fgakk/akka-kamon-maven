package com.fgakk.samples.akka

import java.util.concurrent.atomic.AtomicInteger

import akka.actor.{Actor, Props}

import scala.collection.mutable

object NotesService {
  // TODO maybe look at the scala stm
  def apply(map: mutable.Map[Int, String] = mutable.Map[Int, String]((1, "test value")),
            counter: AtomicInteger = new AtomicInteger(1)): Props =
    Props(new NotesService(map, counter))
}

class NotesService(map: mutable.Map[Int, String], counter: AtomicInteger) extends Actor {
  var idSeq: Int = 1

  override def receive: Receive = {
    case request: CreateNote => {
      val id = counter.incrementAndGet()
      map.put(id, request.note)
      idSeq += 1
      sender() ! NoteCreated(id)
    }
    case request: GetNote => sender() ! map.get(request.id)
    case _: GetAllNotes =>
      sender() ! map.toMap
    case request: DeleteNote =>
      map.remove(request.id)
      sender() ! NoteDeleted()
  }
}

case class CreateNote(note: String)

case class GetNote(id: Int)

case class GetAllNotes()

case class DeleteNote(id: Int)

case class NoteCreated(id: Int)

case class NoteDeleted()
