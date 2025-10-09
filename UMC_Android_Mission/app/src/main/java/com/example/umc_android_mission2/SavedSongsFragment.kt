package com.example.umc_android_mission2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.umc_android_mission2.databinding.FragmentSavedSongsBinding


class SavedSongsFragment: Fragment() {
    private var _binding: FragmentSavedSongsBinding? = null
    private val binding get() = _binding!!

    private var savedSongsDatas = ArrayList<SavedSongsData>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSavedSongsBinding.inflate(inflater, container, false)

        savedSongsDatas.apply {
            add(SavedSongsData("Lost corner", "Kenshi Yonezu", R.drawable.img_album_lost))
            add(SavedSongsData(title = "Lilac", artist = "아이유(IU)", coverImg = R.drawable.img_album_exp2))
            add(SavedSongsData(title = "Butter", artist = "방탄소년단(BTS)", coverImg = R.drawable.img_album_exp))
            add(SavedSongsData(title = "Lost corner", artist = "Kenshi Yonezu", coverImg = R.drawable.img_album_lost))
            add(SavedSongsData(title = "Lilac", artist = "아이유(IU)", coverImg = R.drawable.img_album_exp2))
            add(SavedSongsData(title = "Butter", artist = "방탄소년단(BTS)", coverImg = R.drawable.img_album_exp))
            add(SavedSongsData("Lost corner", "Kenshi Yonezu", R.drawable.img_album_lost))
            add(SavedSongsData(title = "Lilac", artist = "아이유(IU)", coverImg = R.drawable.img_album_exp2))
            add(SavedSongsData(title = "Butter", artist = "방탄소년단(BTS)", coverImg = R.drawable.img_album_exp))
            add(SavedSongsData(title = "Lost corner", artist = "Kenshi Yonezu", coverImg = R.drawable.img_album_lost))
            add(SavedSongsData(title = "Lilac", artist = "아이유(IU)", coverImg = R.drawable.img_album_exp2))
            add(SavedSongsData(title = "Butter", artist = "방탄소년단(BTS)", coverImg = R.drawable.img_album_exp))
        }

        val SavedSongsRVAdapter = SavedSongsRVAdapter(savedSongsDatas)
        binding.savedSongListRv.adapter = SavedSongsRVAdapter
        binding.savedSongListRv.layoutManager = LinearLayoutManager(context)


        return binding.root
    }
}