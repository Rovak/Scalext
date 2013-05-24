package com.scalext.direct.dispatcher

import com.scalext.direct.remoting.api.Rpc
import com.scalext.direct.remoting.api.RpcResult

trait Dispatcher {

  def dispatch(rpc: Rpc): RpcResult
  def dispatch(rpcs: Seq[Rpc]): Seq[RpcResult]

}