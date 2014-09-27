import java.util.Date

import controllers.{LocationApi, CommentApi}

import org.json4s.NoTypeHints
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.{read => r, write => w, _}
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.specs2.execute.Results
import play.api.data.Forms._
import play.api.test.Helpers._
import play.api.test.{Helpers, FakeRequest, WithApplication}

import org.scalatest.FunSuite
import org.scalatest.mock.MockitoSugar
import org.specs2.execute.Results
import play.api.data.Form
import play.api.data.Forms._

import play.api.test._
import org.json4s._
import org.json4s.jackson.Serialization
import play.api.data.Forms._
import play.api.data._
import play.api.mvc._
import repository._
import org.specs2.mutable._
import collection.mutable.Stack
import org.scalatestplus.play._

import scala.concurrent.Future
import scala.text


/**
 * Created by badend on 9/24/14.
 */
class CommentDBTest extends PlaySpec with MockitoSugar  with Results{

  implicit val formats = org.json4s.DefaultFormats ++ org.json4s.ext.JodaTimeSerializers.all

  "read by time" in new WithApplication {

    val read = CommentApi.readByTime(0).apply(FakeRequest())
    println(contentAsString(read))

  }

  "create comment" in new WithApplication {


    val dt = CommentApi.fmt.print(System.currentTimeMillis())
    println( dt)
    val data = Map(
       "user_id" -> "user_id",
        "editor" -> "editor",
        "content" -> "content",
        "updatedt" -> dt,
        "location_id" -> "0",
        "score" -> "1")



    val fq = FakeRequest(Helpers.POST,"/comment").withFormUrlEncodedBody(data.toSeq:_*)

    val read = CommentApi.create()(fq)
    println(contentAsString(read))
    println("create")

  }

  "update jdbc" in new WithApplication {



    val randomn = math.round(math.random * 10)

    val test2str = contentAsString(CommentApi.readByLocationId(0)(FakeRequest()))
    val test2 = r[Seq[Comment]](test2str)

    val item = test2(0)
    val content = s"${item.content.get}$randomn"
    println(item)
    val dt = CommentApi.fmt.print(item.updatedt.get)
    val data = Map(
      "user_id" -> "user_id",
      "editor" -> "editor",
      "content" -> content,
      "updatedt" -> dt,
      "location_id" -> "0",
      "score" -> "2")

    val fq = FakeRequest(Helpers.PUT,"/comment").withFormUrlEncodedBody(data.toSeq:_*)

    val update = CommentApi.update()(fq)
    println(contentAsString(update))
    println(s"map=$data, lastitem=$item")
    val test2str_changed = contentAsString(CommentApi.read(item.id.get)(FakeRequest()))
    val test2_changed = r[Seq[Comment]](test2str_changed)
    println(s"changed:${test2_changed.head.content.get} ,  $content")



  }

  "delete" in new WithApplication {

    val read = CommentApi.readByLocationId(0)(FakeRequest())
    val item = r[Seq[Comment]](contentAsString(read))
    val del = CommentApi.delete(item(0).id.get)(FakeRequest())


    println(contentAsString(read))

    println(contentAsString(del))
  }

}