package com.scalext.direct.remoting


/** Contains a RPC request and its result
  */
case class RpcResult(rpc: Rpc, result: Any) {
  def toJson = rpc.toJson
}
