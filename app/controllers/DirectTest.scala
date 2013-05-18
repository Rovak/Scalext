package controllers

import com.scalext.annotations.Remotable
import com.scalext.annotations.FormHandler

@Remotable(name = "play.remotablectrl")
class DirectTest {

  private var itemList = List[String]()

  @Remotable
  @FormHandler
  def add(item: String): String = {
    itemList ::= item
    "added"
  }

  def add(item: String, item2: String): String = {
    itemList ::= item
    println(s"added $item")
    "two added"
  }

  def add(obj: Map[String, String]): String = {
    println("added object" + obj)
    "obj added"
  }

  @Remotable
  def all(): List[String] = itemList

  @Remotable(name = "list")
  protected def list: List[String] = {
    itemList
  }
}

@Remotable
class Direct2Test {

  @Remotable(name = "gas2")
  def AddItem(item: String) = {
    println(s"Added Item $item")
  }
}