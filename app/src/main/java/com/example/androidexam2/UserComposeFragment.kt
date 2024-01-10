package com.example.androidexam2

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.content.pm.PackageManager
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID


class UserComposeFragment : Fragment() {

    val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val userID = auth.currentUser?.uid
    private var currentUser: User? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return ComposeView(requireContext()).apply {

            setContent {
                ComposeContent()
            }
        }
    }

    @Composable
    fun ComposeContent() {

        val currentUserState: MutableState<User?> = remember { mutableStateOf(null) }
        var userName = remember { mutableStateOf("") }
        var userMail = remember { mutableStateOf("") }

        LaunchedEffect(true) {
            getUserFromDB { user ->
                currentUserState.value = user
                currentUser = user
                userName.value = user?.userName.toString()
                userMail.value = user?.userId.toString()
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(10.dp)

        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = CenterVertically,


            ) {
                Box(
                    Modifier
                        .size(100.dp)
                        .align(CenterVertically)
                        .clip(CircleShape)



                ){
                    profileImage(currentUserState.value?.imageUrl)
                }

                Text(
                    text = userName.value.uppercase(),
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(R.font.montserrat_bold))
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                        .padding(5.dp)
                )
            }

            Text(
                text = stringResource(R.string.your_posts),
                fontFamily = FontFamily(Font(R.font.montserrat_bold)),
                fontSize = 16.sp,
                modifier = Modifier
                    .fillMaxWidth()

            )
            Barrier()
            Box(
                 modifier = Modifier
                     .weight(1f)
                     .padding(0.dp, 0.dp, 0.dp, 5.dp)
            ){
                addListOfCreatedMeals()
            }

            logOutButton()
        }


    }

    private fun getMeals(callback: (List<Meal>) -> Unit) {
        if (userID != null){
            db.collection("meals").whereEqualTo("creator", userID).get().addOnSuccessListener { data ->
                val listOfMeals = mutableListOf<Meal>()
                if (data != null){
                    for (document in data.documents){
                        val meal = document.toObject<Meal>()
                        if (meal != null){
                            listOfMeals.add(meal)
                        }
                    }
                }
                callback.invoke(listOfMeals)
            }
        }
    }

    fun goToListFragment(){
        val listFragment = ListFragment()
        parentFragmentManager.beginTransaction().replace(R.id.fragmentContainer, listFragment).commit()
    }

    private fun getUserFromDB(callback: (User?) -> Unit) {
        if (userID != null) {
            db.collection("users").document(userID).get().addOnSuccessListener { document ->
                val user = document.toObject<User>()

                callback.invoke(user)
            }
        }
    }

    @Composable
    fun logOutButton() {
        MaterialTheme(
        ){
            Button(
                modifier = Modifier
                    .fillMaxWidth(),
                onClick = {
                    val activity = requireActivity()
                    if (activity is MainActivity){
                        activity.onLogOut()
                    }
                    auth.signOut()
                    goToListFragment()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(red = 0, green = 32, blue = 63)
                )

            ) {
                Text(text = "Log out")

            }
        }
    }

    @Composable
    fun profileImage(firebaseRef: String?) {
        var isImagePermissionGranted by remember { mutableStateOf(ContextCompat.checkSelfPermission(requireContext(),
            Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) }

        val downloadedProfileImage = remember {
            mutableStateOf(firebaseRef?.let { Uri.parse(it) })
        }


        val imagePicker =
            rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    val selectedImageUri: Uri? = data?.data
                    Log.d("!!!", "Made it 247")
                    if (selectedImageUri != null){
                        val storageRef =
                            FirebaseStorage.getInstance().getReference("images/${UUID.randomUUID()}")
                        selectedImageUri?.let { storageRef.putFile(it) }
                            ?.addOnSuccessListener {
                                if (currentUser?.imageUrl != null){
                                    deleteOldUserImage(currentUser!!.imageUrl!!)
                                }
                                currentUser?.imageUrl = storageRef.toString()
                                saveUserImage(storageRef.toString())
                                downloadedProfileImage.value = selectedImageUri
                            }
                    }
                }
            }

        val permissionLauncher =
            rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    isImagePermissionGranted = isGranted

                    imagePicker.launch(Intent(Intent.ACTION_PICK).setType("image/*"))
                } else {

                }
            }


        val imageModifier = Modifier
            .fillMaxSize()
            .clip(CircleShape)
            .clickable {
                if (isImagePermissionGranted) {
                    imagePicker.launch(Intent(Intent.ACTION_PICK).setType("image/*"))
                } else {
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                }

            }

        if (firebaseRef != null){
            LaunchedEffect(key1 = true){
                downloadedImageFromFireBase(firebaseRef) { uri ->
                    downloadedProfileImage.value = uri
                }
            }
            loadFirebaseImage(downloadedProfileImage.value, imageModifier)
        }else if (downloadedProfileImage.value != null){
            loadFirebaseImage(uri = downloadedProfileImage.value, imageModifier = imageModifier)
        } else{
            loadPlaceholderImage(imageModifier)
        }

    }

    private fun deleteOldUserImage(urlToDelete: String) {
            if (urlToDelete != null) {
                val filename = urlToDelete.substringAfterLast("/")
                val storage = FirebaseStorage.getInstance()
                val storageRef = storage.reference.child("images/$filename")
                storageRef.delete().addOnSuccessListener {
                    Log.d("!!!", "deleted")
                }
            }
        }

    private fun saveUserImage(imageRef: String) {
        val db = FirebaseFirestore.getInstance()
        if (userID != null) {
            db.collection("users").document(userID).update("imageUrl", imageRef).addOnSuccessListener {
            }
        }
    }

    @Composable
    private fun loadPlaceholderImage(imageModifier: Modifier) {
        Image(painter = painterResource(id = R.drawable.baseline_question_mark_24),
            contentDescription = stringResource(id = R.string.imageNotSet),
            modifier = imageModifier)
    }

    @OptIn(ExperimentalGlideComposeApi::class)
    @Composable
    private fun loadFirebaseImage(uri: Uri?, imageModifier: Modifier) {
            GlideImage(
                modifier = imageModifier,
                model = uri,
                contentScale = ContentScale.FillBounds,
                contentDescription = "Profile image",
            )
    }


    @Composable
    fun addListOfCreatedMeals() {
        var mealsState = remember {
            mutableStateListOf<Meal>()
        }
        LaunchedEffect(key1 = true) {
            getMeals { meals ->
                mealsState.addAll(meals)
            }
        }
        LazyColumn(
        ) {
            items(mealsState.toList()) { meal ->
                mealItem(meal)
            }
        }
    }
    @OptIn(ExperimentalGlideComposeApi::class)
    @Composable
    private fun mealItem(meal: Meal) {
        val downloadedImage = remember {
            mutableStateOf<Uri?>(Uri.parse(meal.imageURI))
        }
        LaunchedEffect(key1 = true){
            downloadedImageFromFireBase(meal.imageURI) { uri ->
                downloadedImage.value = uri
            }
        }

        Row (modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(0.dp, 5.dp)
            .clickable {
                goToDetailsFragment(meal)
            }
            ){
            GlideImage(model = downloadedImage.value,
                contentDescription = "Test",
                modifier = Modifier
                    .size(100.dp),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .padding(5.dp)

            ) {
                meal.name?.let {
                    Text(
                        text = it,
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(R.font.montserrat_bold)),
                        modifier = Modifier

                    )
                }
                Row {
                    val rating = meal.getAverageRating()
                    var ratingString = stringResource(R.string.no_ratings)
                    if (rating != 0.0){
                        ratingString = String.format("%.1f", rating)
                    }else{
                        ""
                    }
                    Text(
                        text = ratingString,
                        )
                    if (rating != 0.0){
                        Image(
                            painter = painterResource(id = R.drawable.baseline_star_rate_24),
                            contentDescription = "Stars",
                            modifier = Modifier
                                .size(20.dp)
                        )
                    }
                }
                meal.description?.let {
                    Text(
                        text = it,
                        fontFamily = FontFamily(Font(R.font.montserrat_light)),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis

                    ) }

            }
        }
        Barrier()
    }

    private fun goToDetailsFragment(meal: Meal) {
        val detailsFragment = DetailsFragment()
        val bundle = Bundle()
        bundle.putSerializable("meal", meal)
        detailsFragment.arguments = bundle
        val transaction = parentFragmentManager.beginTransaction()
        transaction.addToBackStack("user")
        transaction.replace(R.id.fragmentContainer, detailsFragment).commit()
    }

    private fun downloadedImageFromFireBase(image: String?, callback: (Uri?) -> Unit) {
        if (image != null){
            val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(image.toString())
            storageRef?.downloadUrl?.addOnSuccessListener {uri ->
                callback.invoke(uri)
            }
        }
    }


    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        ComposeContent()
    }
}
@Composable
private fun Barrier(){
    Divider(
        color = Color(R.color.dark_grey),
        thickness = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
    )
}


