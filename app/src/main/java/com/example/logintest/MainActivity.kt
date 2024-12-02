package com.example.logintest

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.logintest.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.OAuthLoginCallback

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    private lateinit var mGoogleSigninClient: GoogleSignInClient // 선언 추가

    private val googleLoginResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        val resultCode = result.resultCode
        val data = result.data

        try {
            val completedTask = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = completedTask.getResult(ApiException::class.java)
//            val email = account?.email.toString() //이름
//            val familyName = account?.familyName.toString()   //이름 (윤)
//            val givenName = account?.givenName.toString()     //이름 (민찬)
//            val displayName = account?.displayName.toString() //이름 (윤민찬)
            onLoginCompleted("${account?.id}", "${account?.idToken}")
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            onError(Error(e))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // naver login
        // NaverIdLoginSDK.initialize(this, getString(R.string.naver_client_id), getString(R.string.naver_client_secret), getString(R.string.naver_client_name))

        binding.buttonOAuthLoginImg.setOAuthLogin(object : OAuthLoginCallback {
            override fun onSuccess() {
                Log.d("test", "AccessToken : " + NaverIdLoginSDK.getAccessToken())
                // Log.d("test", "client id : " + getString(R.string.naver_client_id))
                Log.d("test", "ReFreshToken : " + NaverIdLoginSDK.getRefreshToken())
                Log.d("test", "Expires : " + NaverIdLoginSDK.getExpiresAt().toString())
                Log.d("test", "TokenType : " + NaverIdLoginSDK.getTokenType())
                Log.d("test", "State : " + NaverIdLoginSDK.getState().toString())
            }

            override fun onFailure(httpStatus: Int, message: String) {
                val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
                Log.e("test", "$errorCode $errorDescription")
            }

            override fun onError(errorCode: Int, message: String) {
                onFailure(errorCode, message)
            }
        })

        // google login
        binding.signInButton.setOnClickListener {
            signInWithGoogle()
        }
    }

    private fun signInWithGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            // .requestIdToken(getString(R.string.google_client_id)) // 요청할 때마다 받아오기
            .requestEmail()
            .build()

        mGoogleSigninClient = GoogleSignIn.getClient(this, gso)
        googleLoginResult.launch(mGoogleSigninClient.signInIntent)
    }

    private fun onLoginCompleted(userId: String?, accessToken: String?){
        Toast.makeText(this, "구글 로그인 성공", Toast.LENGTH_SHORT).show()
        Log.e("YMC", "userId: $userId / accessToken: $accessToken")
    }
    private fun onError(error : Error?){
        Toast.makeText(this, "구글 로그인 실패", Toast.LENGTH_SHORT).show()
        Log.e("YMC", "구글 로그인 실패 onError / error: ${error} / error.msg: ${error?.message}")
    }
}