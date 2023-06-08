package vn.hitu.ntb.interfaces

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

interface OnYoutubePlayer {
    fun onYoutubePlayer(youTubePlayer: YouTubePlayer?, youTubePlayerView: YouTubePlayerView?)
}