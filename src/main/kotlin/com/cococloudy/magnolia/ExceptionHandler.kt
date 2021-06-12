package com.cococloudy.magnolia

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class WrongParameterException(message: String? = null) : ResponseStatusException(HttpStatus.BAD_REQUEST, message)

class WrongRequestException(message: String? = null) : ResponseStatusException(HttpStatus.BAD_REQUEST, message)

class NotFoundException(entityName: String, entityId: Any) :
    ResponseStatusException(HttpStatus.NOT_FOUND, "${entityName}: $entityId")

class NotAuthorizedException(entityName: String, entityId: Any, accountId: Long) :
    ResponseStatusException(HttpStatus.UNAUTHORIZED, "Account $accountId is not authorized to do $entityName $entityId")

class ForbiddenException(action: String, accountId: Long) :
    ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden to do $action for account $accountId")
