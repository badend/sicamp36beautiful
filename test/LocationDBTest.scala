import java.util.Date

import controllers.LocationApi

import org.json4s.NoTypeHints
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization._
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
import play.api.test.Helpers._
import org.json4s._
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.{read => r, write => w}
import play.api.data.Forms._
import play.api.data._
import play.api.mvc._
import repository._
import org.specs2.mutable._
import collection.mutable.Stack
import org.scalatestplus.play._

import scala.text

/**
 * Created by badend on 9/24/14.
 */
class LocationDBTest extends PlaySpec with MockitoSugar  with Results{


  implicit val formats = Serialization.formats(NoTypeHints)

  "read by time" in new WithApplication {

    val read = LocationApi.readByTime(0).apply(FakeRequest())
    println(contentAsString(read))

  }

  "create location" in new WithApplication {


    val dt = LocationApi.fmt.print(System.currentTimeMillis())
    println( dt)
    val data = Map(
      "category_name"-> "category_name",
      "area_name"->"area_name",
      "name"->"testname",
      "updatedt"->  dt,
      "addr"->"addr",
      "homepage"->"hp",
      "phone"->"p",
      "description"->"desc",
      "image0"->"image0",
      "image1"->"image1",
      "image2"->"image2",
      "image3"->"image3",
      "image4"->"image4",
      "latitude"->"122.12",
      "longitude"->"123.456")



    val fq = FakeRequest(Helpers.POST,"/location").withFormUrlEncodedBody(data.toSeq:_*)

    val read = LocationApi.create()(fq)
    println(contentAsString(read))
    println("create")

  }

  "update jdbc" in new WithApplication {



    val randomn = math.round(math.random * 10)

    val test2str = contentAsString(LocationApi.readByName("testname")(FakeRequest()))
    val test2 = r[Seq[Location]](test2str)
    val item = test2(0)
    val desc = s"${item.description}$randomn"
    println(item)
    val dt = LocationApi.fmt.print(item.updatedt.get)
    println(dt)
    val data = Map[String, String](
    "id" -> item.id.get.toString,
      "category_name"-> item.category_name.get,
      "area_name"->item.area_name.get,
      "name"->item.name.get,
      "updatedt"-> dt,
      "addr"->item.addr.get,
      "homepage"->item.homepage.get,
      "phone"->item.phone.get,
      "description"->  desc,
      "image0"->item.image0.get,
      "image1"->item.image1.get,
      "image2"->item.image2.get,
      "image3"->item.image3.get,
      "image4"->item.image4.get,
      "latitude"->item.latitude.get.toString,
      "longitude"->item.longitude.get.toString)

    val fq = FakeRequest(Helpers.POST,"/location").withFormUrlEncodedBody(data.toSeq:_*)

    val update = LocationApi.update()(fq)
    println(contentAsString(update))
    val test2str_changed = contentAsString(LocationApi.readFromId(item.id.get)(FakeRequest()))
    val test2_changed = r[Seq[Location]](test2str_changed)
    println(test2_changed)
    assert(test2_changed(0).description.equals(desc))


  }
}