package com.scalext.direct.dispatcher

import scala.collection.parallel.ThreadPoolTaskSupport
import com.scalext.direct.remoting.{RpcResult, Rpc}

/**
 * Dispatches in a parallel thread pool
 */
class ParallelDispatcher(directClasses: Map[String, Class[_]]) extends StandardDispatcher(directClasses) {

  /**
   * Dispatch multiple requests
   */
  override def dispatch(rpcs: Seq[Rpc]): Seq[RpcResult] = {
    val rpcList = rpcs.toParArray
    rpcList.tasksupport = new ThreadPoolTaskSupport()
    rpcList.map(dispatch(_)).toList
  }
}