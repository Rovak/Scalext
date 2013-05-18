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
    println("added " + item)
    "1 toegevoegd"
  }

  def add(item: String, item2: String): String = {
    itemList ::= item
    println("added twee" + item)
    "twee toegevoegd"
  }

  def add(obj: Map[String, String]): String = {
    println("added object" + obj)
    "obj toegevoegd"
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
  def AddGas(item: String): String = {

    "Er is gas toegevoegd!"
  }

  @Remotable(name = "gas3")
  def gasErop(item2: String): String = {
    "Meeerrr gas erop"
  }
}