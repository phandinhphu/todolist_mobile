# 📱 ToDoList Mobile App

Ứng dụng quản lý công việc (ToDo List) hiện đại được xây dựng bằng **Jetpack Compose** và **Clean Architecture** cho nền tảng Android.

## ✨ Tính năng chính

- 🔐 **Xác thực người dùng**: Đăng ký, đăng nhập với Firebase Authentication
- ✅ **Quản lý công việc**: Tạo, chỉnh sửa, xóa và đánh dấu hoàn thành task
- 🏷️ **Phân loại task**: Personal (Cá nhân), Work (Công việc), Study (Học tập)
- ⚡ **Độ ưu tiên**: Low, Medium, High
- 📅 **Deadline & Nhắc nhở**: Đặt thời hạn và thời gian nhắc nhở cho task
- 💾 **Lưu trữ offline**: Sử dụng Room Database để lưu trữ dữ liệu local
- 🎨 **Material Design 3**: Giao diện đẹp, hiện đại với Material You

## 🛠️ Công nghệ sử dụng

### UI & Framework
- **Jetpack Compose** - Modern UI toolkit
- **Material Design 3** - Design system
- **Navigation Compose** - Navigation component
- **Coil** - Image loading library
- **Lottie** - Animation library

### Architecture & Dependency Injection
- **Clean Architecture** - Kiến trúc sạch với 3 lớp (UI, Domain, Data)
- **MVVM Pattern** - Model-View-ViewModel
- **Dagger Hilt** - Dependency Injection
- **Use Cases** - Business logic separation

### Local Storage
- **Room Database** - SQLite database với type-safe queries
- **DataStore Preferences** - Key-value storage
- **Kotlin Coroutines & Flow** - Asynchronous programming

### Backend & Authentication
- **Firebase Authentication** - User authentication
- **Firebase Cloud (Ready for)** - Backend services

### Tools & Libraries
- **Timber** - Logging
- **KSP (Kotlin Symbol Processing)** - Code generation

## 📐 Kiến trúc dự án

Dự án sử dụng **Clean Architecture** với 3 lớp chính:

```
┌─────────────────────────────────────┐
│         UI Layer (Presentation)     │
│  ┌─────────┐  ┌──────────┐         │
│  │ Screens │  │ ViewModels│         │
│  │(Compose)│  │  (MVVM)  │         │
│  └─────────┘  └──────────┘         │
└─────────────────────────────────────┘
                  ↕
┌─────────────────────────────────────┐
│      Domain Layer (Business)        │
│  ┌──────────┐  ┌─────────────┐     │
│  │  Models  │  │  Use Cases  │     │
│  │          │  │ Repositories│     │
│  │          │  │ (Interfaces)│     │
│  └──────────┘  └─────────────┘     │
└─────────────────────────────────────┘
                  ↕
┌─────────────────────────────────────┐
│        Data Layer (Storage)         │
│  ┌──────────┐  ┌──────────────┐    │
│  │Repository│  │ Data Sources │    │
│  │Impl      │  │ Room/Firebase│    │
│  └──────────┘  └──────────────┘    │
└─────────────────────────────────────┘
```

### Data Flow

```
User Action → ViewModel → Use Case → Repository → Data Source
                                    ↓
UI Update ← StateFlow ← Flow ← Repository ← Data Source
```

## 📋 Yêu cầu hệ thống

- **Minimum SDK**: 24 (Android 7.0 Nougat)
- **Target SDK**: 36
- **Kotlin**: 2.1.0+
- **Java**: 11+
- **Android Studio**: Hedgehog | 2023.1.1 hoặc cao hơn
- **Gradle**: 8.13.0+

## 🚀 Cài đặt

### 1. Clone repository

```bash
git clone <repository-url>
cd todolist_mobile
```

### 2. Cấu hình Firebase

1. Tạo một dự án Firebase mới tại [Firebase Console](https://console.firebase.google.com/)
2. Thêm Android app vào dự án Firebase
3. Tải file `google-services.json`
4. Đặt file vào thư mục `app/` (đã có sẵn trong dự án)

### 3. Build và chạy

1. Mở project trong Android Studio
2. Đợi Gradle sync hoàn tất
3. Chạy ứng dụng trên emulator hoặc thiết bị thật

```bash
# Hoặc sử dụng Gradle command
./gradlew assembleDebug
./gradlew installDebug
```

## 📁 Cấu trúc dự án

```
app/src/main/java/com/example/todolist/
│
├── 📂 ui/                          # UI Layer
│   ├── screen/
│   │   ├── auth/                  # Authentication screens
│   │   │   ├── LoginScreen.kt
│   │   │   ├── RegisterScreen.kt
│   │   │   ├── AuthViewModel.kt
│   │   │   └── AuthUiState.kt
│   │   └── task/                  # Task management screens
│   │       ├── TaskListScreen.kt
│   │       ├── AddEditTaskScreen.kt
│   │       ├── TaskViewModel.kt
│   │       └── TaskUiState.kt
│   └── theme/                     # Theme configuration
│       ├── Color.kt
│       ├── Theme.kt
│       └── Type.kt
│
├── 📂 domain/                      # Domain Layer (Business Logic)
│   ├── model/                     # Domain models
│   │   ├── Task.kt
│   │   ├── User.kt
│   │   └── Enums.kt
│   ├── repository/                # Repository interfaces
│   │   ├── TaskRepository.kt
│   │   └── AuthRepository.kt
│   └── usecase/                   # Use cases
│       ├── auth/
│       │   ├── LoginUseCase.kt
│       │   ├── RegisterUseCase.kt
│       │   ├── LogoutUseCase.kt
│       │   └── GetCurrentUserUseCase.kt
│       └── task/
│           ├── GetTasksUseCase.kt
│           ├── AddTaskUseCase.kt
│           ├── UpdateTaskUseCase.kt
│           ├── DeleteTaskUseCase.kt
│           └── ToggleTaskCompleteUseCase.kt
│
├── 📂 data/                        # Data Layer
│   ├── local/                     # Local data sources
│   │   └── database/
│   │       ├── AppDatabase.kt
│   │       ├── entity/
│   │       │   ├── TaskEntity.kt
│   │       │   └── TagEntity.kt
│   │       ├── dao/
│   │       │   └── TaskDao.kt
│   │       └── converter/
│   │           └── Converters.kt
│   ├── remote/                    # Remote data sources
│   │   └── firebase/
│   │       └── FirebaseAuthDataSource.kt
│   ├── repository/                # Repository implementations
│   │   ├── TaskRepositoryImpl.kt
│   │   └── AuthRepositoryImpl.kt
│   └── mapper/                    # Data mappers
│       ├── TaskMapper.kt
│       └── FirebaseUserMapper.kt
│
├── 📂 di/                          # Dependency Injection
│   ├── AuthModule.kt
│   ├── DatabaseModule.kt
│   └── TaskModule.kt
│
├── 📂 route/                       # Navigation
│   ├── NavGraph.kt
│   └── Routes.kt
│
├── 📂 util/                        # Utilities
│   └── DateFormatter.kt
│
├── App.kt                          # Application class
├── MainActivity.kt                 # Main activity
└── MainViewModel.kt                # Main view model
```

## 💾 Database Schema

### Tasks Table

| Column | Type | Description |
|--------|------|-------------|
| `id` | Long (PK) | Primary key (auto-generated) |
| `title` | String | Tiêu đề task |
| `description` | String? | Mô tả task (nullable) |
| `category` | TaskCategory | Phân loại (PERSONAL, WORK, STUDY) |
| `priority` | PriorityLevel | Độ ưu tiên (LOW, MEDIUM, HIGH) |
| `isCompleted` | Boolean | Trạng thái hoàn thành |
| `reminderTime` | Long? | Thời gian nhắc nhở (timestamp, nullable) |
| `dueDate` | Long? | Deadline (timestamp, nullable) |
| `userId` | String | Firebase User ID |
| `createdAt` | Long | Thời gian tạo (timestamp) |

## 🎯 Sử dụng ứng dụng

### Đăng ký/Đăng nhập
1. Mở ứng dụng
2. Đăng ký tài khoản mới hoặc đăng nhập
3. Sử dụng email và password để xác thực

### Quản lý Task
1. Sau khi đăng nhập, bạn sẽ thấy danh sách task (nếu có)
2. Nhấn nút **+** để thêm task mới
3. Nhấn vào task để chỉnh sửa
4. Tích checkbox để đánh dấu hoàn thành
5. Nhấn icon xóa để xóa task

### Tạo Task
- **Title**: Tiêu đề task (bắt buộc)
- **Description**: Mô tả chi tiết (tùy chọn)
- **Category**: Chọn Personal, Work, hoặc Study
- **Priority**: Chọn Low, Medium, hoặc High
- **Due Date**: Chọn deadline (tùy chọn)
- **Reminder**: Chọn thời gian nhắc nhở (tùy chọn)

## 🔧 Development

### Thêm dependency mới

Chỉnh sửa file `gradle/libs.versions.toml` để thêm version và dependency mới.

### Chạy tests

```bash
./gradlew test
```

### Build APK

```bash
./gradlew assembleRelease
```

APK sẽ được tạo tại: `app/build/outputs/apk/release/`

## 📝 Coding Conventions

- Tuân thủ [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Sử dụng Clean Architecture principles
- ViewModels quản lý UI state thông qua StateFlow
- Repository pattern cho data access
- Use Cases cho business logic
- Dependency Injection với Hilt

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the project
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 👨‍💻 Author

Created with ❤️ for learning Android development with Clean Architecture and Jetpack Compose.

## 🙏 Acknowledgments

- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Room Database](https://developer.android.com/training/data-storage/room)
- [Firebase](https://firebase.google.com/)
- [Dagger Hilt](https://dagger.dev/hilt/)

---

⭐ Nếu dự án này hữu ích, hãy cho một star!

