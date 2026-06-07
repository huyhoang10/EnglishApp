package com.example.efishapp.feature.setting.domain.usecase

import com.example.efishapp.feature.setting.domain.repository.SettingRepository
import javax.inject.Inject

// kiểm tra nghiệp vụ trước khi mà đưa cho thằng repo lấy dữ liệu
class GetThemeUseCase @Inject constructor(
    private val repository: SettingRepository
) {
    operator fun invoke() = repository.getTheme()
}