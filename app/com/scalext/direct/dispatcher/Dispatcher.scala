package com.scalext.direct.dispatcher

import com.scalext.direct.remoting.{RpcResult, Rpc}

trait Dispatcher {

  def dispatch(rpc: Rpc): RpcResult
  def dispatch(rpcs: Seq[Rpc]): Seq[RpcResult]

}