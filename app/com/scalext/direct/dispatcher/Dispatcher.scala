package com.scalext.direct.dispatcher

import com.scalext.direct.remoting.{RpcResult, Rpc}

/**
 * Dispatcher
 */
trait Dispatcher {

  /**
   * Dispatch a single result
   *
   * @param rpc RPC data
   * @return Executed RPC with result
   */
  def dispatch(rpc: Rpc): RpcResult

  /**
   * Dispatch multiple RPCS
   *
   * @param rpcs list of RPCs to dispatch
   * @return All resulting rpc calls
   */
  def dispatch(rpcs: Seq[Rpc]): Seq[RpcResult] = rpcs.map(dispatch)

}