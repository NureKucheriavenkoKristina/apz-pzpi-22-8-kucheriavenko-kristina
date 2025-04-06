#include <iostream>
#include <steam/steam_api.h>

// Константи
const char* const kAchievementId_WinOneGame = "ACH_WIN_ONE_GAME";

// Ініціалізація Steam API
bool initializeSteam() {
    if (!SteamAPI_Init()) {
        std::cerr << "[Помилка] Steam API не ініціалізовано.\n";
        return false;
    }

    if (!SteamUser()->BLoggedOn()) {
        std::cerr << "[Помилка] Користувач не авторизований у Steam.\n";
        return false;
    }

    return true;
}

// Завантаження статистики користувача
bool loadUserStats() {
    if (!SteamUserStats()->RequestCurrentStats()) {
        std::cerr << "[Помилка] Неможливо завантажити статистику користувача.\n";
        return false;
    }
    return true;
}

// Розблокування досягнення
void unlockAchievement(const char* achievementId) {
    if (SteamUserStats()->SetAchievement(achievementId)) {
        SteamUserStats()->StoreStats();
        std::cout << "Досягнення '" << achievementId << "' розблоковано! 🎉\n";
    } else {
        std::cout << "Не вдалося розблокувати '" << achievementId << "'.\n";
    }
}

// Перевірка, чи отримано досягнення
bool isAchievementUnlocked(const char* achievementId) {
    bool achieved = false;
    SteamUserStats()->GetAchievement(achievementId, &achieved);
    return achieved;
}

// Вивід привітання
void greetUser() {
    const char* name = SteamFriends()->GetPersonaName();
    std::cout << "Привіт, " << name << "!\n";
}

// Завершення роботи Steam API
void shutdownSteam() {
    SteamAPI_Shutdown();
    std::cout << "Steam API завершено.\n";
}

// Основна логіка програми
int main() {
    if (!initializeSteam()) return 1;
    greetUser();

    if (!loadUserStats()) {
        shutdownSteam();
        return 1;
    }

    if (isAchievementUnlocked(kAchievementId_WinOneGame)) {
        std::cout << "Досягнення вже розблоковано. Молодець!\n";
    } else {
        std::cout << "Досягнення ще не відкрито. Відкриваємо...\n";
        unlockAchievement(kAchievementId_WinOneGame);
    }

    shutdownSteam();
    return 0;
}

