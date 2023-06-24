package vn.hitu.ntb.chat.ui.handle

import android.animation.AnimatorSet
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.os.SystemClock
import android.util.Log
import androidx.appcompat.content.res.AppCompatResources
import com.google.firebase.database.*
import vn.hitu.ntb.chat.databinding.MessageAudioLeftBinding
import vn.hitu.ntb.model.entity.ChatMessage
import vn.hitu.ntb.chat.ui.adapter.MessageAdapter
import vn.hitu.ntb.model.entity.UserData
import vn.hitu.ntb.utils.AppUtils
import vn.hitu.ntb.utils.AudioUtils
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

/**
 * @Author: NGUYEN THANH BINH
 * @Date: 19/05/2023
 */
class AudioLeftHandle(
    private val binding: MessageAudioLeftBinding,
    private val data: ChatMessage,
    private val position: Int,
    private val chatHandle: MessageAdapter.ChatHandle,
    private val onAudioChatMessage: MessageAdapter.OnAudioChatMessage,
    private var adapter: MessageAdapter,

    ) {
    private var set1 = AnimatorSet()
    private var set2 = AnimatorSet()
    private var set3 = AnimatorSet()
    fun setData() {

        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(data.sendBy)
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user: UserData = snapshot.getValue(UserData::class.java)!!
                AppUtils.loadImageUser(binding.civShowAvatar, user.image)
                binding.tvShowUsername.text = user.name
            }

            override fun onCancelled(error: DatabaseError) {}
        })
        pauseTimeAudio()

        binding.root.setOnClickListener {
            runOrStopMediaPlayer()
            onAudioChatMessage.onRunning(position, data.message)
        }
        AudioUtils.setProcessBarForAudio(
            set1,
            set2,
            set3,
            binding.audio.pbProgressbar1,
            binding.audio.pbProgressbar2,
            binding.audio.pbProgressbar3
        )
        binding.tvShowTimeMessage.text = AppUtils.getLastTime(data.messageTime)


    }
    private fun runOrStopMediaPlayer() {
        if (!data.play) {
            startTimeAudio()
        } else {
            pauseTimeAudio()
        }
    }

    private fun startTimeAudio() {
        val mediaPlayer = MediaPlayer()
        val retriever = MediaMetadataRetriever()
        // Đặt nguồn dữ liệu cho trình phát đa phương tiện
        mediaPlayer.setDataSource(AppUtils.getLinkPhoto(data.message, "audios"))
        mediaPlayer.prepare()

        // Set the data source for the media metadata retriever
        retriever.setDataSource(AppUtils.getLinkPhoto(data.message, "audios"))

        // Get the duration of the audio file
        val duration = mediaPlayer.duration

        Log.d("Duration_Auido:", duration.toString())
        val time = SystemClock.elapsedRealtime() + duration - data.seekTo
        binding.audio.timeAudio.base = time
        binding.audio.timeAudio.start()
        binding.audio.ivPlayAudio.setImageDrawable(
            AppCompatResources.getDrawable(
                adapter.getContext(),
                vn.hitu.ntb.R.drawable.ic_pause_audio
            )
        )
        set1.start()
        set2.start()
        set3.start()
    }

    private fun pauseTimeAudio() {
        binding.audio.timeAudio.stop()
        if (!data.stop) {
            val mediaPlayer = MediaPlayer()
            val retriever = MediaMetadataRetriever()
            // Đặt nguồn dữ liệu cho trình phát đa phương tiện
            mediaPlayer.setDataSource(AppUtils.getLinkPhoto(data.message, "audios"))
            mediaPlayer.prepare()

            // Set the data source for the media metadata retriever
            retriever.setDataSource(AppUtils.getLinkPhoto(data.message, "audios"))

            // Get the duration of the audio file
            val duration = mediaPlayer.duration

            Log.d("Duration_Auido:", duration.toString())

            // Print the calculated values
            val time = SystemClock.elapsedRealtime() + duration - data.seekTo
            binding.audio.timeAudio.base = time
        }
        binding.audio.ivPlayAudio.setImageDrawable(
            AppCompatResources.getDrawable(
                adapter.getContext(),
                vn.hitu.ntb.R.drawable.ic_play_audio
            )
        )
        set1.end()
        set2.end()
        set3.end()
        data.stop = false
    }

}