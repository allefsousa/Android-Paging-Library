package com.developer.allef.boilerplateapp.data.failure

/**
 * @author allef.santos on 12/05/20
 */
data class RequestFailure (val retryable: Retryable,val errorMessage:String)