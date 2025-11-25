package com.example.umc_android_mission2

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.umc_android_mission2.databinding.FragmentLockerBinding
import com.google.android.material.tabs.TabLayoutMediator

class LockerFragment : Fragment() {

    private var _binding: FragmentLockerBinding? = null
    private val binding get() = _binding!!

    private val repository by lazy { AuthRepository(ApiClient.authService) }
    private val viewModelFactory by lazy { AuthViewModelFactory(repository) }
    private val viewModel: AuthViewModel by viewModels { viewModelFactory }

    private val information = arrayListOf("저장한 곡", "음악파일", "저장앨범")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLockerBinding.inflate(inflater, container, false)

        val lockerVPAdapter = LockerVPAdapter(this)
        binding.lockerContentVp.adapter = lockerVPAdapter
        TabLayoutMediator(binding.lockerContentTb, binding.lockerContentVp) { tab, position ->
            tab.text = information[position]
        }.attach()

        observeTestTokenResult()

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        updateUiBasedOnLoginState()
    }

    private fun observeTestTokenResult() {
        viewModel.testTokenResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Toast.makeText(requireContext(), "토큰이 유효합니다. 로그아웃합니다.", Toast.LENGTH_SHORT).show()
                logout()
            }.onFailure {
                Toast.makeText(requireContext(), "토큰이 만료되었습니다. 강제 로그아웃합니다.", Toast.LENGTH_LONG).show()
                logout()
            }
        }
    }

    private fun isLoggedIn(): Boolean {
        val spf = activity?.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)
        return spf?.contains("accessToken") ?: false
    }

    private fun updateUiBasedOnLoginState() {
        if (isLoggedIn()) {
            setLoggedInUi()
        } else {
            setLoggedOutUi()
        }
    }

    private fun setLoggedInUi() {
        binding.lockerLoginTv.text = "로그아웃"
        binding.lockerLoginTv.setOnClickListener {
            val spf = requireActivity().getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)
            val token = spf.getString("accessToken", null)
            if (token != null) {
                viewModel.testToken(token)
            } else {
                logout() // 토큰이 없는 이례적인 경우에도 로그아웃 처리
            }
        }
    }

    private fun setLoggedOutUi() {
        binding.lockerLoginTv.text = "로그인"
        binding.lockerLoginTv.setOnClickListener {
            startActivity(Intent(activity, LoginActivity::class.java))
        }
    }

    private fun logout() {
        val spf = activity?.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)
        spf?.edit()?.clear()?.apply()

        updateUiBasedOnLoginState()
        Toast.makeText(requireContext(), "로그아웃되었습니다.", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}