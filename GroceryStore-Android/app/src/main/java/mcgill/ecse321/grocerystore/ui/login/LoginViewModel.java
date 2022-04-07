package mcgill.ecse321.grocerystore.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.util.Patterns;

import mcgill.ecse321.grocerystore.data.LoginRepository;
import mcgill.ecse321.grocerystore.data.Result;
import mcgill.ecse321.grocerystore.data.model.LoggedInUser;
import mcgill.ecse321.grocerystore.R;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private LoginRepository loginRepository;

    LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public void login(String username, String userType) {
        // can be launched in a separate asynchronous job
        Result<LoggedInUser> result = loginRepository.login(username, userType);

        if (result instanceof Result.Success) {
            LoggedInUser data = ((Result.Success<LoggedInUser>) result).getData();
            loginResult.setValue(new LoginResult(new LoggedInUserView(data.getUsername(), data.getUserType())));
        } else {
            loginResult.setValue(new LoginResult(R.string.login_failed));
        }
    }


    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {

        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        return !username.trim().isEmpty();
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null;
    }
}