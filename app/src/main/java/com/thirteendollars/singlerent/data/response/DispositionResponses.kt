package com.thirteendollars.singlerent.data.response

import com.thirteendollars.singlerent.data.model.Disposition

/**
 * Created by Damian Nowakowski on 20/03/2018.
 * mail: thirteendollars.com@gmail.com
 */
data class DispositionResponse(val disposition: Disposition)
data class FinishRentalResponse(val costSummary: Double, val timeSummary: Int, val kmSummary: Int)