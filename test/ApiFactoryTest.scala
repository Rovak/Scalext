import com.scalext.direct.remoting.api.ApiFactory
import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._

class ApiFactoryTest extends Specification {

  "The ApiFactory" should {

    "build classes from a comma separated string" in {
      val classes = ApiFactory.buildClasses("test.direct.Form")
      classes must have size(1)
    }

    "classes must retrieve name from remotable annotation" in {
      val classes = ApiFactory.buildClasses("test.direct.Form")
      classes must have key("test.ProfileForm")
    }
  }
}