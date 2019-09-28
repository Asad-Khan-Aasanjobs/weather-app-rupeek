package com.asadkhan.global_module.network

public data class BaseResult<T>(
  val count: Int, val next: String?, val previous: String?, val data: List<T>?
)
