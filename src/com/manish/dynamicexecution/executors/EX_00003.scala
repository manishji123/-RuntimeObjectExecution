package com.manish.dynamicexecution.executors

import com.manish.dynamicexecution._


object EX_00003 extends BaseExecutor{

  /**
    * run method will be executed only if this returns true
    * Migration DB can be useful to maintain a history of last run if that decides to execute the run method.
    * @return
    */
  override def check() = true


  override def run(): Unit = {
    println("----------------- In EX_00003.run() Method.. ")
  }

  override def cleanUp(): Unit = {

  }
}
