package com.mattmayers.todo.framework.crud

class UpdateFailedError(message: String? = null) : RuntimeException(message)
class CreateFailedError(message: String? = null) : RuntimeException(message)
class DeleteFailedError(message: String? = null) : RuntimeException(message)