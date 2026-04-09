package applicationId.ru.netology.nmedia.activity

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import applicationId.ru.netology.nmedia.R
import applicationId.ru.netology.nmedia.databinding.ActivityMainBinding
import applicationId.ru.netology.nmedia.viewModel.AuthViewModel
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var firebaseMessaging: FirebaseMessaging

    @Inject
    lateinit var googleApiAvailability: GoogleApiAvailability

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        val appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        lifecycleScope.launchWhenStarted {
            authViewModel.authState.collectLatest {
                invalidateOptionsMenu()
            }
        }

        firebaseMessaging.token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("MainActivity", "FCM token: ${task.result}")
            }
        }

        val result = googleApiAvailability.isGooglePlayServicesAvailable(this)
        if (result != com.google.android.gms.common.ConnectionResult.SUCCESS) {
            // при необходимости можно обработать ошибку
        }

        Log.d("MainActivity", "onCreate called")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_auth, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val authorized = authViewModel.authState.value.isAuthorized
        menu.findItem(R.id.login)?.isVisible = !authorized
        menu.findItem(R.id.logout)?.isVisible = authorized
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.login -> {
                showLoginDialog()
                true
            }

            R.id.logout -> {
                authViewModel.logout()
                invalidateOptionsMenu()
                true
            }

            R.id.openMap -> {
                navController.navigate(R.id.mapFragment)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showLoginDialog() {
        val loginEt = EditText(this).apply {
            hint = "login"
        }

        val passEt = EditText(this).apply {
            hint = "password"
        }

        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 24, 48, 0)
            addView(loginEt)
            addView(passEt)
        }

        AlertDialog.Builder(this)
            .setTitle("Login")
            .setView(container)
            .setPositiveButton("OK") { _, _ ->
                val login = loginEt.text.toString().trim()
                val pass = passEt.text.toString().trim()
                authViewModel.login(login, pass)
                invalidateOptionsMenu()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onStart() {
        super.onStart()
        Log.d("MainActivity", "onStart")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d("MainActivity", "onRestart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("MainActivity", "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d("MainActivity", "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("MainActivity", "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("MainActivity", "onDestroy")
    }
}