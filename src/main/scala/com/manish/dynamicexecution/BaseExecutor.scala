package com.manish.dynamicexecution

trait BaseExecutor{

  def execute(): Unit = {
    try{
      if (check) run
    } finally {cleanUp}
  }
  def check(): Boolean
  def run(): Unit
  def cleanUp(): Unit

}
