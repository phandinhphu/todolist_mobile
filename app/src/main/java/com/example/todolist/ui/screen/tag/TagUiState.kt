package com.example.todolist.ui.screen.tag

import com.example.todolist.domain.model.Tag

data class TagUiState(
    // 1. Danh sách các thẻ lấy từ Database
    val tags: List<Tag> = emptyList(),

    // 2. Trạng thái đang tải dữ liệu (hiện ProgressIndicator)
    val isLoading: Boolean = false,

    // 3. Thông báo lỗi nếu có vấn đề xảy ra
    val errorMessage: String? = null,

    // 4. Trạng thái ẩn/hiện Dialog thêm thẻ mới
    val isAddDialogOpen: Boolean = false,

    // 5. Thẻ hiện tại đang được chọn để chỉnh sửa (null nếu đang thêm mới)
    val selectedTag: Tag? = null
)