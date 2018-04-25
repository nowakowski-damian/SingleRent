package com.thirteendollars.singlerent.data.response

import com.thirteendollars.singlerent.data.model.Disposition
import com.thirteendollars.singlerent.data.model.User

/**
 * Created by Damian Nowakowski on 14/03/2018.
 * mail: thirteendollars.com@gmail.com
 */
data class UserResponse(var user: User, var refreshToken: String?, var accessToken: String?, var currentDisposition: Disposition?)