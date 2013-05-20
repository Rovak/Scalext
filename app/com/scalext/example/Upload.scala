package com.scalext.example

import com.scalext.annotations._
import play.api.libs.Files.TemporaryFile
import com.scalext.direct.remoting.api.FormResult

@Remotable(name = "Scalext.example.Upload")
class Upload {

  @Remotable
  @FormHandler
  def uploadFile(post: Any, files: Seq[TemporaryFile]): FormResult = {

    println("Post " + post)
    println("Files " + files)

    FormResult(Map("msg" -> s"${files.size} file(s) succesfully uploaded"))
  }

}