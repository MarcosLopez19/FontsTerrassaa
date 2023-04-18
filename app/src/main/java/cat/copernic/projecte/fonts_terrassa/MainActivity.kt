package cat.copernic.projecte.fonts_terrassa

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.DialogInterface
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import cat.copernic.projecte.fonts_terrassa.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import java.util.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var connected = false
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connected =
            connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)!!.state == NetworkInfo.State.CONNECTED ||
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)!!.state == NetworkInfo.State.CONNECTED

        if (connected) {
            val binding =
                DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

            loadLocale()

            //motrar mensaje de entrada
            showExplanationDialog()


            val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.myNavHostFragment) as NavHostFragment
            val navController = navHostFragment.navController
            NavigationUI.setupWithNavController(binding.navView, navController)

            requestLocationPermissions()

            //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            alertInternetCon()
        }
    }

    private fun alertInternetCon(){
        val objectAlerDialog = AlertDialog.Builder(this)
        objectAlerDialog.setTitle(R.string.atencio)
        objectAlerDialog.setMessage(R.string.no_internet_con)
        objectAlerDialog.setPositiveButton(R.string.sortir) { dialog, which ->
            finishAndRemoveTask()
        }
        var alertDialog: AlertDialog = objectAlerDialog.create()
        alertDialog.show()

    }

    private fun requestLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1
            )
            return
        }
    }
    private fun showExplanationDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("L'Observatori d'aigua de Terrassa (OAT) et dona la benvinguda a l'aplicació Fonts Terrassa")
        builder.setMessage("Amb aquesta aplicació podràs trobar i conèixer totes les fonts que es troben a la ciutat.\n" +
                "\n" +
                "En obrir l'aplicació, veureu un mapa interactiu de la ciutat amb diferents gotes d'aigua marcades. Cada gota d'aigua representa una font a la ciutat, i el color indica el tipus de font que és. A la part dreta de la pantalla, trobareu l'apartat d'informació sobre les fonts, on podreu obtenir detalls addicionals sobre cadascuna.\n" +
                "\n" +
                "Per obtenir més informació sobre una font en particular, simplement fes clic a la gota corresponent al mapa. Això us mostrarà l'adreça de la font i el seu nom. Si fas clic al nom de la font, seràs portat a una altra pàgina on podràs veure una imatge de la font, així com les seves característiques i tindrás l'opció d'evaluar la font visitada.\n" +
                "\n" +
                "També una de les funcionalitats que té l'aplicació, al menú el botó de l'esquerra trobaràs un llistat en ordre alfabètic amb totes les fonts, on també les podràs buscar pel nom i llistar-les.")
        builder.setPositiveButton("Sí") { dialogInterface: DialogInterface, i: Int ->
            // Aquí puedes poner código para empezar la aplicación
        }
        //builder.setNegativeButton("No") { dialogInterface: DialogInterface, i: Int ->
            // Aquí puedes poner código para cerrar la aplicación
          //  finish()
       // }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    if ((ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) ==
                                PackageManager.PERMISSION_GRANTED)
                    ) {
                        finish()
                        startActivity(intent)
                    }
                }
                return
            }
        }
    }

    fun setLocale(lang: String, value: Int) {
        if (value == 0) {
            val locale: Locale = Locale(lang)
            Locale.setDefault(locale)
            val config: Configuration = Configuration()
            config.locale = locale
            baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)

            val editor: SharedPreferences.Editor =
                getSharedPreferences("Settings", MODE_PRIVATE).edit()
            editor.putString("My_Lang", lang)
            editor.apply()
        } else {
            if (lang != getLocale()) {
                val locale: Locale = Locale(lang)
                Locale.setDefault(locale)
                val config: Configuration = Configuration()
                config.locale = locale
                baseContext.resources.updateConfiguration(config,
                    baseContext.resources.displayMetrics)

                val editor: SharedPreferences.Editor =
                    getSharedPreferences("Settings", MODE_PRIVATE).edit()
                editor.putString("My_Lang", lang)
                editor.apply()
                reloadActivity()
            }
        }
    }

    fun loadLocale() {
        val prefs: SharedPreferences = getSharedPreferences("Settings", Activity.MODE_PRIVATE)
        val language: String = prefs.getString("My_Lang", "").toString()
        setLocale(language, 0)
    }

    fun getLocale(): String {
        val prefs: SharedPreferences = getSharedPreferences("Settings", Activity.MODE_PRIVATE)
        return prefs.getString("My_Lang", "").toString()
    }

    fun reloadActivity() {
        finish()
        startActivity(getIntent())
    }
}