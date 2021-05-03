package com.example.marsrealestate.util


/**
 * A generic class that holds a value with its loading status.
 * @param <T>
 */
sealed class Result<out R>(val messageId : Int? = null) {


    class Success<out T>(val data: T? = null,val msgId  : Int? = null) : Result<T>(msgId)
    class Error(val exception: Exception? = null,val errorMsgId : Int? = null) : Result<Nothing>(errorMsgId)
    class Loading : Result<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$exception]"
            is Loading -> "Loading"
        }
    }

    fun tryGetData() : R? {
        return if (this is Success)
            this.data
        else
            null
    }

    //Convenient for use in xml
    @JvmName("isIsError")
    fun isError() = this is Error

    @JvmName("isIsLoading")
    fun isLoading() = this is Loading

    @JvmName("isIsSuccess")
    fun isSuccess() = this is Success


    companion object {
        @JvmStatic
                /**
                 * Useful in xml to tell if an operation of the form LiveData<Result<*>>
                 * has not been launched yet, it can be seen as a fourth state after
                 * [Success] ,[Error] and [Loading].
                 *
                 * The annotation @JvmStatic is mandatory for the use from xml
                 */
        fun notYetDone(res: Result<*>?) = res == null
    }
}

/**
 * `true` if [Result] is of type [Success] & holds non-null [Success.data].
 */
val Result<*>.succeeded
    get() = this is Result.Success && data != null

