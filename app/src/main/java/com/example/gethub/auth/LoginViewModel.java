package com.example.gethub.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LoginViewModel extends ViewModel {

    // Use the singleton instance
    private final AuthRepository authRepository = AuthRepository.getInstance();
    private final MutableLiveData<AuthRepository.User> loginResult = new MutableLiveData<>();

    public LiveData<AuthRepository.User> getLoginResult() {
        return loginResult;
    }

    public void login(String studentId, String password) {
        AuthRepository.User user = authRepository.login(studentId, password);
        loginResult.setValue(user);
    }
}
