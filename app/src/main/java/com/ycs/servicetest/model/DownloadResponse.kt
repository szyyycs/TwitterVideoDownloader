package com.ycs.servicetest.model

/**
 * Created on 2024/04/09.
 * @author carsonyang
 */
data class DownloadResponse(
    val success: Boolean,
    val variants: List<VideoListResponse>?
)

data class VideoListResponse(
    val resolution: String,
    val size: Long,
    val url: String
)