# Expense Tracker - Modern Android App

A modern, offline-first Android application designed to help you manage your personal finances with ease. Track your expenses and income, categorize your transactions, and visualize your spending habits through insightful charts.

## ✨ Features

* Track Transactions: Easily add, edit, and delete your daily expenses and income.
* Categorization: Assign categories to your transactions to understand where your money goes.
* Multiple Accounts: Manage finances across different accounts like a bank, wallet, or credit cards.
* Intuitive UI: A clean, card-based interface for a seamless user experience.
* Data Visualization:
    * Bar Graphs: Compare your spending over different periods.
    * Pie Charts: See a breakdown of your expenses by category.
* 100% Offline: All your financial data is stored securely on your device, ensuring privacy and accessibility without an internet connection.
* Efficient Data Handling: Smoothly scroll through a long history of transactions without performance issues.

## 🛠️ Tech Stack & Architecture

This project is built with a modern Android tech stack, focusing on performance, scalability, and maintainability.

* Language: Kotlin
* Architecture: MVVM (Model-View-ViewModel)
* UI: XML with Material Components / Jetpack Compose
* Database: Room Persistence Library for local data storage.
* Asynchronous Programming: Coroutines for managing background threads.
* Data Loading: Paging 3 for efficiently loading and displaying large datasets.
* Charting Library: Utilizes a powerful charting library to render bar and pie charts (e.g., MPAndroidChart or a similar library).
* Dependency Injection: Hilt or Koin

## 📸 Screenshots

### Light Mode
![Light Mode Screenshot](preview/ui.webp)

### Dark Mode
![Dark Mode Screenshot](dark_mode_screenshot.png)

## 🚀 Getting Started

To build and run this project, follow these steps:

* Clone the repository: `git clone https://github.com/dontknow492/ExpenseTracker.git`
* Open in Android Studio:
    * Open Android Studio.
    * Select File > Open and navigate to the cloned project directory.
* Build the project:
    * Let Android Studio sync the Gradle files.
    * Click the Run 'app' button or use the Shift + F10 shortcut.

## 🤝 Contributing

Contributions are welcome! If you have ideas for improvements or find any bugs, feel free to open an issue or submit a pull request.

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/NewFeature`)
3. Commit your Changes (`git commit -m 'Add some NewFeature'`)
4. Push to the Branch (`git push origin feature/NewFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the LICENSE.md file for details.
