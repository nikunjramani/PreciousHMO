package com.lion_tech.hmo.client.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.media.ExifInterface
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import com.lion_tech.hmo.R
import com.lion_tech.hmo.app_data.AppLevelData
import com.lion_tech.hmo.client.activities.adpaters.autocompleteAdapters.ProvidersAutoCompleteAdapter
import com.lion_tech.hmo.client.activities.fragments.changeLoginDetail.ChangeLoginDetailViewModel
import com.lion_tech.hmo.client.activities.fragments.enrolleeProfile.EnrolleeProfile
import com.lion_tech.hmo.client.activities.models.ProviderModel
import com.lion_tech.hmo.server_urls.ServerUrls
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.content_dashboard_client.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class DashboardClient : AppCompatActivity() {

    private var selectedProviderId: String = ""
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var ivHamburger: ImageView
    private lateinit var tvTitle: TextView
    private lateinit var tvDate: TextView
    private lateinit var tvClientName: TextView
    private lateinit var tvWelcome: TextView
    private lateinit var tvAdd: TextView
    private lateinit var tvLogOut: TextView
    lateinit var ivEnrolleeProfile: ImageView
    lateinit var ivProfileElectronics: ImageView
    lateinit var ivPic: ImageView
    private lateinit var ivDialogProfileImage: ImageView

    private var isDialogImage = false


    private var bitmap: Bitmap? = null
    var filePath: String? = null
    private var imageName: String? = null
    internal lateinit var array: ByteArray
    private lateinit var encodeString: String

    private val calendar: Calendar = Calendar.getInstance()

    private lateinit var ivHeader: ImageView
    private lateinit var tvHeaderClientName: TextView
    private lateinit var tvHeaderEmail: TextView
    lateinit var searchView: SearchView
    private lateinit var cvSearch: CardView
    private lateinit var cvDateFilterComplain: CardView
    private lateinit var tvDateFilterForComplaint: TextView
    private lateinit var changeLoginDetail: ChangeLoginDetailViewModel
    private lateinit var ivSearchComplaint: ImageView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard_client)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.hide()

        changeLoginDetail =
            ViewModelProvider(this).get(ChangeLoginDetailViewModel::class.java)


        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        ivHamburger = findViewById(R.id.ivHamburger)


        tvClientName = findViewById(R.id.tvClientName)
        tvTitle = findViewById(R.id.tvTitle)
        tvDate = findViewById(R.id.tvDate)
        tvWelcome = findViewById(R.id.tvWelcome)
        tvAdd = findViewById(R.id.tvAddDependant)
        tvLogOut = findViewById(R.id.tvLogout)
        ivEnrolleeProfile = findViewById(R.id.ivProfile)
        ivProfileElectronics = findViewById(R.id.ivProfileElectronics)
        searchView = findViewById(R.id.searchView)
        cvSearch = findViewById(R.id.cvSearch)
        cvDateFilterComplain = findViewById(R.id.cvDate)
        tvDateFilterForComplaint = findViewById(R.id.tvDateFilter)
        ivSearchComplaint = findViewById(R.id.tvSearchComplaint)
        ivPic = findViewById(R.id.ivPic)
        progressBar = findViewById(R.id.progressBar)


        var dateFormat = SimpleDateFormat("EEEE", Locale.US)
        val date = Date()
        var dateString = "${dateFormat.format(date)}, ${calendar.get(Calendar.DAY_OF_MONTH)}"

        dateFormat = SimpleDateFormat("MMMM", Locale.US)

        dateString = "$dateString ${dateFormat.format(date)} ${calendar.get(Calendar.YEAR)}"
        tvDate.text = dateString

        dateFormat = SimpleDateFormat("MMM yyyy", Locale.US)
        tvDateFilterForComplaint.text = dateFormat.format(date)

        ivSearchComplaint.setOnClickListener {
            AppLevelData.complaintFragmentObject!!.filterCompaintOnDate(tvDateFilterForComplaint.text.toString())

        }

        tvLogOut.setOnClickListener {
            changeLoginDetail.logOut(this,progressBar)
        }


        tvDateFilterForComplaint.setOnClickListener {
            val newFragment =
                EnrolleeProfile.DatePickerFragment(tvDateFilterForComplaint, "formate")
            newFragment.show(supportFragmentManager, "datePicker")
        }

//
//        ivEnrolleeProfile.setOnClickListener {
//            isDialogImage = false
//            takePic()
//        }

        ivPic.setOnClickListener {
            isDialogImage = false
            takePic()
        }


        ivHamburger.setOnClickListener {
            drawerLayout.openDrawer(Gravity.LEFT)
        }

        tvAdd.setOnClickListener {
            if (tvAdd.text == getString(R.string.add_dependant)) {
                showAddDependentDialog()
            } else {
                AppLevelData.complaintFragmentObject!!.showComplaintDialog()
            }
        }


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_dashboard,
                R.id.nav_change_login_detail,
                R.id.nav_dependants,
                R.id.nav_enrollee_profile,
                R.id.nav_change_provider_request,
                R.id.nav_complains,
                R.id.subscriptionFragment,
                R.id.electronicsFrag,
                R.id.nav_faq,
                R.id.nav_user_policy,
                R.id.nav_how_the_scheme_work,
                R.id.nav_contact_us,
                R.id.nav_social_media_handles
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


        val view = navView.getHeaderView(0)
        ivHeader = view.findViewById(R.id.iv_Header)
        tvHeaderClientName = view.findViewById(R.id.tvHeaderClientName)
        tvHeaderEmail = view.findViewById(R.id.tvHeaderClientEmail)

        val clientJson = AppLevelData.clientDetailJson
        if (clientJson != null) {
            tvClientName.text = clientJson.getString("name")
            tvHeaderClientName.text = clientJson.getString("name")
            tvHeaderEmail.text = clientJson.getString("email")

            Picasso.get()
                .load("https://care.precioushmo.com/app/uploads/clients/${clientJson.getString("client_profile")}")
                .into(ivHeader)
        }

    }


    private fun setAdapterToSpinners(
        spSex: Spinner,
        spRelation: Spinner,
        acvProvider: AutoCompleteTextView
    ) {

        // -------- Set adapter to sex spinner
        ArrayAdapter.createFromResource(
            this,
            R.array.sex_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spSex.adapter = adapter
        }

        // -------- Set adapter to MaritalStatus spinner
        ArrayAdapter.createFromResource(
            this,
            R.array.relation_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spRelation.adapter = adapter
        }

        // -------- Set adapter to Provider spinner
        val acvProviderAdapter = ProvidersAutoCompleteAdapter(this)
        acvProvider.setAdapter(acvProviderAdapter)
        acvProviderAdapter.refreshAdapter(AppLevelData.providerList!!)
    }


    fun hideTextView(show: Boolean, title: String) {
        if (show) {
            tvClientName.visibility = View.VISIBLE
            tvTitle.visibility = View.VISIBLE
            tvDate.visibility = View.VISIBLE
            tvWelcome.visibility = View.VISIBLE
            tvTitle.text = title

            ivEnrolleeProfile.visibility = View.GONE
            ivProfileElectronics.visibility = View.GONE
            ivPic.visibility = View.GONE

            if (title == getString(R.string.my_complains)) {
                tvAdd.visibility = View.VISIBLE
                tvAdd.text = ""
                tvAdd.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    getDrawable(R.drawable.ic_add),
                    null,
                    null
                )

            } else if (title == getString(R.string.my_dependant)) {
                tvAdd.visibility = View.VISIBLE
                tvAdd.text = getString(R.string.add_dependant)
                tvAdd.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    getDrawable(R.drawable.ic_dependants),
                    null,
                    null
                )
            } else {
                tvAdd.visibility = View.GONE

            }

            if (title == getString(R.string.change_provider)) {
                cvSearch.visibility = View.VISIBLE
            } else {
                cvSearch.visibility = View.GONE
            }

            if (title == getString(R.string.my_complains)) {
                cvDateFilterComplain.visibility = View.VISIBLE
            } else {
                cvDateFilterComplain.visibility = View.GONE
            }


        } else {
            cvSearch.visibility = View.GONE
            cvDateFilterComplain.visibility = View.GONE
            tvClientName.visibility = View.GONE
            tvTitle.visibility = View.GONE
            tvDate.visibility = View.GONE
            tvWelcome.visibility = View.GONE

            ivEnrolleeProfile.visibility = View.VISIBLE
            ivProfileElectronics.visibility = View.GONE
            ivPic.visibility = View.VISIBLE
            tvAdd.visibility = View.GONE

            if (title == getString(R.string.electronics)) {
                ivPic.visibility = View.GONE
                ivProfileElectronics.visibility = View.VISIBLE
            }
        }
    }

    fun hideAddDependantButton() {
        tvAddDependant.visibility = View.GONE
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.dashboard_client, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


    private fun takePic() {
        CropImage.activity()
            .setGuidelines(CropImageView.Guidelines.ON)
            .start(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                try {
                    bitmap = compressImage(result.uri.path!!)
                    Log.d("imagePath",result.uri.path!!)
                    if (isDialogImage) {
                        ivDialogProfileImage.setImageBitmap(bitmap)
                    } else {
                        ivEnrolleeProfile.setImageBitmap(bitmap)
                    }

                } catch (e: IOException) {
                    e.printStackTrace()
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
            }
        }
    }


    private fun showAddDependentDialog() {
        selectedProviderId = ""
        val view =
            LayoutInflater.from(this).inflate(R.layout.dialog_dependant_profile, null, false)
        val etFirstName = view.findViewById<TextInputEditText>(R.id.etFirstName)
        val etLastName = view.findViewById<TextInputEditText>(R.id.etLastName)
        val etOtherName = view.findViewById<TextInputEditText>(R.id.etOtherName)
        val etContactNo = view.findViewById<TextInputEditText>(R.id.etContactNo)
        val etAge = view.findViewById<TextInputEditText>(R.id.etAge)
        val dialogProgressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        dialogProgressBar.visibility = View.GONE
        etAge.visibility = View.GONE


        ivDialogProfileImage = view.findViewById<ImageView>(R.id.ivDependantProfile)
        val ivAddImage = view.findViewById<ImageView>(R.id.ivAddImage)

        val spSex = view.findViewById<Spinner>(R.id.spSex)
        val spRelation = view.findViewById<Spinner>(R.id.spRelation)
        val acvProvider = view.findViewById<AutoCompleteTextView>(R.id.spProvider)

        val tvDate = view.findViewById<TextView>(R.id.tvDate)

        val btnAddDependant = view.findViewById<Button>(R.id.btnAddDependant)

        setAdapterToSpinners(spSex, spRelation, acvProvider)

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH) + 1
        val day = c.get(Calendar.DAY_OF_MONTH)
        var dayText = "$day"
        var monthText = "$month"
        if (day < 10) {
            dayText = "0$day"
        }

        if (month < 10) {
            monthText = "0$month"
        }

        tvDate.text = "$year/$monthText/$day"

        tvDate.setOnClickListener {
            val newFragment = EnrolleeProfile.DatePickerFragment(tvDate)
            newFragment.show(supportFragmentManager, "datePicker")
        }

        ivAddImage.setOnClickListener {
            isDialogImage = true
            takePic()
        }

        acvProvider.onItemClickListener = object : AdapterView.OnItemSelectedListener,
            AdapterView.OnItemClickListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                val providerModel: ProviderModel =
                    parent!!.getItemAtPosition(position) as ProviderModel
                selectedProviderId = providerModel.hospitalId.toString()

            }

            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val providerModel: ProviderModel =
                    parent!!.getItemAtPosition(position) as ProviderModel
                selectedProviderId = providerModel.hospitalId.toString()
            }
        }

        btnAddDependant.setOnClickListener {

            var splitDate = tvDate.text.split("/")
            etAge.setText(getAge(splitDate[0].toInt(), splitDate[1].toInt(), splitDate[2].toInt()))


            val selectedSex = spSex.selectedItem.toString()
            val selectedRelation = spRelation.selectedItem.toString()
//            val selectedProviderId = "${(acvProvider.getSel as ProviderModel).hospitalId}"


            when {
                etFirstName.text.toString().isEmpty() -> {
                    etFirstName.error = getString(R.string.first_name_required)
                    etFirstName.requestFocus()
                    return@setOnClickListener
                }
                etLastName.text.toString().isEmpty() -> {
                    etLastName.error = getString(R.string.last_name_required)
                    etLastName.requestFocus()
                    return@setOnClickListener
                }

                etContactNo.text.toString().isEmpty() -> {
                    etContactNo.error = getString(R.string.contact_name_required)
                    etContactNo.requestFocus()
                    return@setOnClickListener
                }


                etAge.text.toString().toInt() > 21 -> {
                    if (selectedRelation != "Spouse") {
                        Toast.makeText(
                            this,
                            getString(R.string.only_spouse_age_will_be_greater_than_21),
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setOnClickListener
                    }
                }
            }
            if (filePath == null) {
                Toast.makeText(this, "Select profile image", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!selectedProviderId.isEmpty()) {
                //------- Upload data to server ----------
                dialogProgressBar.visibility = View.VISIBLE
                AppLevelData.dependentProgressBar = dialogProgressBar

                AddDependantToServer().execute(
                    etFirstName.text.toString(),
                    etLastName.text.toString(),
                    etOtherName.text.toString(),
                    etContactNo.text.toString(),
                    selectedSex,
                    selectedRelation,
                    selectedProviderId,
                    tvDate.text.toString(),
                    "LOCATION"
                )

            } else {
                Toast.makeText(this, "Select provider", Toast.LENGTH_SHORT).show()
            }
        }


        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setView(view)

        val dialog = alertDialog.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        AppLevelData.dependentDialog = dialog

        dialog.show()
    }

    fun updateHeaderData(name: String, email: String, profileImage: String) {
        tvClientName.text = name
        tvHeaderClientName.text = name

        Picasso.get()
            .load("http://care.precioushmo.com/uploads/clients/$profileImage")
            .into(ivHeader)
    }

    private fun getAge(year: Int, month: Int, day: Int): String {
        val dob: Calendar = Calendar.getInstance();
        val today: Calendar = Calendar.getInstance();

        dob.set(year, month, day);

        var age: Int = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        var ageInt: Integer = Integer(age + 1)
        var ageS: String = ageInt.toString();
        Log.d("calculated age", ageS)
        return ageS;
    }


    @SuppressLint("StaticFieldLeak")
    private inner class AddDependantToServer() : AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg params: String): String {

            val sourceFile = File(filePath);

            Log.d("TAG", "File...::::" + sourceFile + " : " + sourceFile.exists());

            val MEDIA_TYPE_PNG = "image/*".toMediaTypeOrNull();

            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                    "client_profile",
                    "mmm.png",
                    RequestBody.create(MEDIA_TYPE_PNG, sourceFile)
                )
                .addFormDataPart("name", params[0])
                .addFormDataPart("last_name", params[1])
                .addFormDataPart("other_name", params[2])
                .addFormDataPart("contact_number", params[3])
                .addFormDataPart("sex", params[4])
                .addFormDataPart("relation", params[5])
                .addFormDataPart("hospital_id", params[6])
                .addFormDataPart("dob", params[7])
                .addFormDataPart("location", params[8])

                .build();

            val request = Request.Builder()
                .url("${ServerUrls.ADD_NEW_DEPENDANT}/${AppLevelData.clientId}")
                .post(requestBody)
                .addHeader(AppLevelData.clientServiceKey, AppLevelData.clientServiceValue)
                .addHeader(AppLevelData.authKey, AppLevelData.authKeyValue)
                .addHeader(AppLevelData.contentTypeKey, AppLevelData.contentTypeValue)
                .addHeader("User-ID", AppLevelData.clientId)
                .addHeader("token", AppLevelData.token)
                .build();

            val client = OkHttpClient();
            client.newCall(request).execute().use { response -> return response.body!!.string() }
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            Log.d("Dependant result", result)

            AppLevelData.dependentViewModel.getClientDependant(this@DashboardClient)
        }
    }


    private fun compressImage(filePath: String): Bitmap {


        var scaledBitmap: Bitmap? = null

        var options: BitmapFactory.Options = BitmapFactory.Options()

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = false;
        var bmp: Bitmap = BitmapFactory.decodeFile(filePath, options);

        var actualHeight: Int = options.outHeight;
        var actualWidth: Int = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        var maxHeight = 816.0f;
        var maxWidth = 612.0f;
        var imgRatio: Float = (actualWidth / actualHeight).toFloat();
        var maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (imgRatio * actualWidth).toInt()
                actualHeight = maxHeight.toInt()
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth
                actualHeight = (imgRatio * actualHeight).toInt()
                actualWidth = maxWidth.toInt()
            } else {
                actualHeight = maxHeight.toInt()
                actualWidth = maxWidth.toInt()

            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = ByteArray(16 * 1024)

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (exception: OutOfMemoryError) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (exception: OutOfMemoryError) {
            exception.printStackTrace();
        }

        val ratioX: Float = actualWidth / (options.outWidth).toFloat()
        val ratioY: Float = actualHeight / (options.outHeight).toFloat()
        val middleX: Float = actualWidth / 2.0f;
        val middleY: Float = actualHeight / 2.0f;

        val scaleMatrix: Matrix = Matrix()
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        val canvas: Canvas = Canvas(scaledBitmap!!);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(
            bmp,
            middleX - bmp.getWidth() / 2,
            middleY - bmp.getHeight() / 2,
            Paint(Paint.FILTER_BITMAP_FLAG)
        );

//      check the rotation of the image and display it properly
        val exif: ExifInterface;
        try {
            exif = ExifInterface(filePath);

            val orientation: Int = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION, 0
            );
            Log.d("EXIF", "Exif: $orientation");
            val matrix: Matrix = Matrix();
            if (orientation == 6) {
                matrix.postRotate(90F);
                Log.d("EXIF", "Exif: $orientation");
            } else if (orientation == 3) {
                matrix.postRotate(180F);
                Log.d("EXIF", "Exif: $orientation");
            } else if (orientation == 8) {
                matrix.postRotate(270F);
                Log.d("EXIF", "Exif: $orientation");
            }
            scaledBitmap = Bitmap.createBitmap(
                scaledBitmap, 0, 0,
                scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                true
            );
        } catch (e: IOException) {
            e.printStackTrace();
        }

        var out: FileOutputStream? = null;
        val filename: String = getFilename();
        try {
            out = FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap!!.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (e: FileNotFoundException) {
            e.printStackTrace();
        }

        return scaledBitmap!!;

    }

    private fun getFilename(): String {
        val file: File =
            File(getExternalFilesDir(null), "hmo/images");
//            File(Environment.getExternalStorageDirectory().path, "MyFolder/Images");
        if (!file.exists()) {
            Log.d("isFileExist", "Directory is creating")
            var isCreated:Boolean=false;
            try {
                isCreated= file.mkdirs();

            }catch (ex:Exception){
                Log.d("isFileExist", "Failed Ex ${ex.message}")

            }
            if(isCreated){
                Log.d("isFileExist", "Directory is created successfully")

            }else{
                Log.d("isFileExist", "Directory is created failed")

            }
        } else {
            Log.d("isFileExist", "Directory is is already exist")

        }

        val uriSting: String = (file.absolutePath + "/" + System.currentTimeMillis() + ".jpg");
        filePath = uriSting
        return uriSting

    }


    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        val height: Int = options.outHeight;
        val width: Int = options.outWidth;
        var inSampleSize: Int = 1;

        if (height > reqHeight || width > reqWidth) {
            val heightRatio: Int = Math.round(height.toFloat() / reqHeight.toFloat());
            val widthRatio: Int = Math.round(width.toFloat() / reqWidth.toFloat());
            inSampleSize = if (heightRatio < widthRatio) {
                heightRatio
            } else {
                widthRatio
            }
        }
        val totalPixels: Float = (width * height).toFloat()
        val totalReqPixelsCap: Float = (reqWidth * reqHeight * 2).toFloat()
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

}
