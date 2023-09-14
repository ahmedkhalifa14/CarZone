package com.example.data.network

import android.net.Uri
import android.util.Log
import com.example.domain.entity.Ad
import com.example.domain.entity.AdData
import com.example.domain.entity.Car
import com.example.domain.entity.CarData
import com.example.domain.entity.ChatMessage
import com.example.domain.entity.MotorCycleData
import com.example.domain.entity.Motorcycle
import com.example.domain.entity.Truck
import com.example.domain.entity.TruckData
import com.example.domain.entity.User
import com.example.domain.entity.Van
import com.example.domain.entity.VanData
import com.example.domain.entity.Vehicle
import com.example.domain.entity.VehicleData
import com.example.domain.entity.VehiclesCategories
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class FireBaseService @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFireStore: FirebaseFirestore,
    private val firebaseStorage: FirebaseStorage,
    private val firebaseDataBase: FirebaseDatabase
) {
    suspend fun signInWithGoogle(idToken: String): AuthResult? {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        return firebaseAuth.signInWithCredential(credential).await()
    }

    suspend fun register(email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        firebaseAuth.currentUser?.sendEmailVerification()
    }

    suspend fun loginWithEmail(email: String, password: String) =
        firebaseAuth.signInWithEmailAndPassword(email, password).await()!!

    suspend fun saveUserData(user: User) {
        Log.d("authViewModel", "in FirebaseService user= " + user.toString())
        val documentReference = firebaseFireStore.collection("users").document(user.userId)
        try {
            documentReference.set(user).await()
        } catch (e: Exception) {
            Log.d("authViewModel", "in FirebaseService error is " + e.message)

            // Handle the exception appropriately
        }
    }

    suspend fun getAllVehiclesCategories(): MutableList<VehiclesCategories> {
        val vehiclesCategoriesRef = firebaseFireStore.collection("Vehicles")
        val snapshot = vehiclesCategoriesRef.get().await()
        val vehiclesCategoriesList = mutableListOf<VehiclesCategories>()
        for (document in snapshot.documents) {
            val categoryID = document.getString("categoryID") ?: ""
            val categoryName = document.getString("categoryName") ?: ""
            val categoryImage = document.getString("categoryImage") ?: ""
            val category = VehiclesCategories(categoryID, categoryName, categoryImage)
            vehiclesCategoriesList.add(category)
        }
        return vehiclesCategoriesList
    }

    suspend fun addVehicleAds(ads: Ad) {
        val collectionReference = firebaseFireStore.collection("Ads")
        val documentReference = collectionReference.document()
        documentReference.set(ads).await()
    }

    suspend fun sendVerificationCode(phoneNumber: String): String {
        val verificationIdDeferred =
            CompletableDeferred<String?>() // CompletableDeferred to hold the verificationId

        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumber) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout duration
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {}
                override fun onVerificationFailed(exception: FirebaseException) {}
                override fun onCodeSent(
                    verificationId: String, token: PhoneAuthProvider.ForceResendingToken
                ) {
                    verificationIdDeferred.complete(verificationId) // Complete the deferred with the verificationId
                }
            }).build()
        PhoneAuthProvider.verifyPhoneNumber(options)
        val verificationId = verificationIdDeferred.await()
        return verificationId ?: ""
    }

    suspend fun verifyCode(verificationId: String, code: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId, code)

        val task = CompletableDeferred<Unit>()
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener { signInTask ->
            if (signInTask.isSuccessful) {
                task.complete(Unit)
            } else {
                val exception =
                    signInTask.exception ?: RuntimeException("Unknown error occurred")
                task.completeExceptionally(exception)
            }
        }
        task.await()
    }


    suspend fun uploadImages(imageUris: List<Uri>): List<String> {
        val imageUrls = mutableListOf<String>()
        val tasks = imageUris.map { imageUri ->
            val imageName = UUID.randomUUID().toString()
            val imageRef = firebaseStorage.reference.child("images/$imageName")
            imageRef.putFile(imageUri).continueWithTask { task ->
                if (!task.isSuccessful) {
                    throw task.exception ?: Exception("Unknown upload error")
                }
                imageRef.downloadUrl
            }
        }

        try {
            val uploadResults = Tasks.whenAllComplete(tasks).await()
            uploadResults.forEachIndexed { index, taskResult ->
                if (taskResult.isSuccessful) {
                    val downloadUrl = (taskResult.result as Uri).toString()
                    imageUrls.add(downloadUrl)
                } else {
                    // Handle individual image upload failure at 'index'
                }
            }
        } catch (e: Exception) {
            throw e
        }

        return imageUrls
    }

    suspend fun saveImageUrl(imageUrl: String) {
        val imageInfo = hashMapOf("imageUrl" to imageUrl)

        try {
            firebaseFireStore.collection("images").add(imageInfo).await()
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun getImages(): List<String> {
        val images = mutableListOf<String>()

        try {
            val querySnapshot = firebaseFireStore.collection("images").get().await()
            for (document in querySnapshot.documents) {
                val imageUrl = document["imageUrl"] as String
                images.add(imageUrl)
            }
        } catch (e: Exception) {
            throw e
        }

        return images
    }


    suspend fun getAllAds(): MutableList<Ad> {
        val collectionReference = firebaseFireStore.collection("Ads")
        val querySnapshot = collectionReference.get().await()
        val adsList = mutableListOf<Ad>()
        for (documentSnapshot in querySnapshot.documents) {
            try {
                val adsData = documentSnapshot.get("adsData") as? Map<String, Any>
                val seller = documentSnapshot.getString("seller")
                val vehicleImages = documentSnapshot.get("vehicleImages") as? List<String>
                val vehicleTypeMap =
                    documentSnapshot.get("vehicleType") as? Map<String, Map<String, Any>>
                val vehicle = documentSnapshot.get("vehicle") as? Map<String, Any>

                val adsDateTimestamp = adsData?.get("date") as? com.google.firebase.Timestamp
                val adsDate = adsDateTimestamp?.toDate()

                val mappedVehicleType: VehicleData? = when {
                    vehicleTypeMap != null -> {
                        val typeKey = vehicleTypeMap.keys.firstOrNull()
                        val typeData = vehicleTypeMap[typeKey]

                        when (typeKey) {
                            "van" -> {
                                val cargoCapacity = typeData?.get("cargoCapacity") as? Double
                                VanData(Van(cargoCapacity ?: 0.0))
                            }

                            "car" -> {
                                val transmission = typeData?.get("transmission") as? String ?: ""
                                CarData(Car(transmission))
                            }

                            "motorcycle" -> {
                                val enginePower = typeData?.get("enginePower") as? String ?: ""
                                val engineTorque = typeData?.get("engineTorque") as? String ?: ""
                                val kerbWeight = typeData?.get("kerbWeight") as? Double ?: 0.0
                                MotorCycleData(Motorcycle(enginePower, engineTorque, kerbWeight))
                            }

                            "truck" -> {
                                val weight = typeData?.get("weight") as? Double ?: 0.0
                                val enginePower = typeData?.get("enginePower") as? Int ?: 0
                                TruckData(Truck(weight, enginePower))
                            }

                            else -> null
                        }
                    }

                    else -> null
                }

                if (adsData != null && vehicle != null && vehicleImages != null && seller != null && mappedVehicleType != null) {
                    val adsDataObj = AdData(
                        title = adsData["title"] as? String ?: "",
                        description = adsData["description"] as? String ?: "",
                        negotiable = adsData["negotiable"] as? Boolean ?: false,
                        price = adsData["price"] as? String ?: "",
                        location = adsData["location"] as? String ?: "",
                        date = adsDate ?: Date()
                    )

                    val vehicleObj = Vehicle(
                        vehicleId = vehicle["vehicleId"] as? String ?: "",
                        vehicleModel = vehicle["vehicleModel"] as? String ?: "",
                        manufacturer = vehicle["manufacturer"] as? String ?: "",
                        vehicleName = vehicle["vehicleName"] as? String ?: "",
                        vehicleEngine = vehicle["vehicleEngine"] as? String ?: "",
                        vehicleFuelType = vehicle["vehicleFuelType"] as? String ?: "",
                        vehicleMileage = vehicle["vehicleMileage"] as? String ?: "",
                        seatingCapacity = (vehicle["seatingCapacity"] as? Long)?.toInt() ?: 0,
                        vehicleType = vehicle["vehicleType"] as? String ?: ""
                    )

                    val ad = Ad(adsDataObj, vehicleObj, vehicleImages, seller, mappedVehicleType)
                    adsList.add(ad)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("adsData", e.toString())
            }
        }

        return adsList
    }

    suspend fun getUserByUserId(userId: String): User? {
        val usersCollection = firebaseFireStore.collection("users")
        try {
            val documentSnapshot = usersCollection.document(userId).get().await()

            if (documentSnapshot.exists()) {
                return documentSnapshot.toObject(User::class.java)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    suspend fun getAllAdsByVehicleType(vehicleType: String): List<Ad> {
        val collectionReference = firebaseFireStore.collection("Ads")
        val querySnapshot = collectionReference.get().await()
        val adsList = mutableListOf<Ad>()

        for (documentSnapshot in querySnapshot.documents) {
            try {
                val adsData = documentSnapshot.get("adsData") as? Map<String, Any>
                val seller = documentSnapshot.getString("seller")
                val vehicleImages = documentSnapshot.get("vehicleImages") as? List<String>
                val vehicleTypeMap =
                    documentSnapshot.get("vehicleType") as? Map<String, Map<String, Any>>
                val vehicle = documentSnapshot.get("vehicle") as? Map<String, Any>

                val adsDateTimestamp = adsData?.get("date") as? com.google.firebase.Timestamp
                val adsDate = adsDateTimestamp?.toDate()

                val mappedVehicleType: VehicleData? = when {
                    vehicleTypeMap != null -> {
                        val typeKey = vehicleTypeMap.keys.firstOrNull()
                        val typeData = vehicleTypeMap[typeKey]

                        when (typeKey) {
                            "van" -> {
                                val cargoCapacity = typeData?.get("cargoCapacity") as? Double
                                VanData(Van(cargoCapacity ?: 0.0))
                            }

                            "car" -> {
                                val transmission = typeData?.get("transmission") as? String ?: ""
                                CarData(Car(transmission))
                            }

                            "motorcycle" -> {
                                val enginePower = typeData?.get("enginePower") as? String ?: ""
                                val engineTorque = typeData?.get("engineTorque") as? String ?: ""
                                val kerbWeight = typeData?.get("kerbWeight") as? Double ?: 0.0
                                MotorCycleData(Motorcycle(enginePower, engineTorque, kerbWeight))
                            }

                            "truck" -> {
                                val weight = typeData?.get("weight") as? Double ?: 0.0
                                val enginePower = typeData?.get("enginePower") as? Int ?: 0
                                TruckData(Truck(weight, enginePower))
                            }

                            else -> null
                        }
                    }

                    else -> null
                }
                if (adsData != null && vehicle != null && vehicleImages != null && seller != null && mappedVehicleType != null) {
                    val adsDataObj = AdData(
                        title = adsData["title"] as? String ?: "",
                        description = adsData["description"] as? String ?: "",
                        negotiable = adsData["negotiable"] as? Boolean ?: false,
                        price = adsData["price"] as? String ?: "",
                        location = adsData["location"] as? String ?: "",
                        date = adsDate ?: Date()
                    )

                    val vehicleObj = Vehicle(
                        vehicleId = vehicle["vehicleId"] as? String ?: "",
                        vehicleModel = vehicle["vehicleModel"] as? String ?: "",
                        manufacturer = vehicle["manufacturer"] as? String ?: "",
                        vehicleName = vehicle["vehicleName"] as? String ?: "",
                        vehicleEngine = vehicle["vehicleEngine"] as? String ?: "",
                        vehicleFuelType = vehicle["vehicleFuelType"] as? String ?: "",
                        vehicleMileage = vehicle["vehicleMileage"] as? String ?: "",
                        seatingCapacity = (vehicle["seatingCapacity"] as? Long)?.toInt() ?: 0,
                        vehicleType = vehicle["vehicleType"] as? String ?: ""
                    )

                    val ad = Ad(adsDataObj, vehicleObj, vehicleImages, seller, mappedVehicleType)

                    // Check if the extracted vehicle type matches the desired type
                    if (vehicleType.equals(ad.vehicle.vehicleType, ignoreCase = true)) {
                        adsList.add(ad)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return adsList
    }

    suspend fun getUserAds(userId: String): List<Ad> {
        val collectionReference = firebaseFireStore.collection("Ads")
        val querySnapshot = collectionReference.get().await()
        val adsList = mutableListOf<Ad>()
        for (documentSnapshot in querySnapshot.documents) {
            try {
                val adsData = documentSnapshot.get("adsData") as? Map<String, Any>
                val seller = documentSnapshot.getString("seller")
                val vehicleImages = documentSnapshot.get("vehicleImages") as? List<String>
                val vehicleTypeMap =
                    documentSnapshot.get("vehicleType") as? Map<String, Map<String, Any>>
                val vehicle = documentSnapshot.get("vehicle") as? Map<String, Any>
                val adsDateTimestamp = adsData?.get("date") as? com.google.firebase.Timestamp
                val adsDate = adsDateTimestamp?.toDate()
                val mappedVehicleType: VehicleData? = when {
                    vehicleTypeMap != null -> {
                        val typeKey = vehicleTypeMap.keys.firstOrNull()
                        val typeData = vehicleTypeMap[typeKey]
                        when (typeKey) {
                            "van" -> {
                                val cargoCapacity = typeData?.get("cargoCapacity") as? Double
                                VanData(Van(cargoCapacity ?: 0.0))
                            }

                            "car" -> {
                                val transmission = typeData?.get("transmission") as? String ?: ""
                                CarData(Car(transmission))
                            }

                            "motorcycle" -> {
                                val enginePower = typeData?.get("enginePower") as? String ?: ""
                                val engineTorque = typeData?.get("engineTorque") as? String ?: ""
                                val kerbWeight = typeData?.get("kerbWeight") as? Double ?: 0.0
                                MotorCycleData(Motorcycle(enginePower, engineTorque, kerbWeight))
                            }

                            "truck" -> {
                                val weight = typeData?.get("weight") as? Double ?: 0.0
                                val enginePower = typeData?.get("enginePower") as? Int ?: 0
                                TruckData(Truck(weight, enginePower))
                            }

                            else -> null
                        }
                    }

                    else -> null
                }
                if (adsData != null && vehicle != null && vehicleImages != null && seller != null && mappedVehicleType != null) {
                    val adsDataObj = AdData(
                        title = adsData["title"] as? String ?: "",
                        description = adsData["description"] as? String ?: "",
                        negotiable = adsData["negotiable"] as? Boolean ?: false,
                        price = adsData["price"] as? String ?: "",
                        location = adsData["location"] as? String ?: "",
                        date = adsDate ?: Date()
                    )
                    val vehicleObj = Vehicle(
                        vehicleId = vehicle["vehicleId"] as? String ?: "",
                        vehicleModel = vehicle["vehicleModel"] as? String ?: "",
                        manufacturer = vehicle["manufacturer"] as? String ?: "",
                        vehicleName = vehicle["vehicleName"] as? String ?: "",
                        vehicleEngine = vehicle["vehicleEngine"] as? String ?: "",
                        vehicleFuelType = vehicle["vehicleFuelType"] as? String ?: "",
                        vehicleMileage = vehicle["vehicleMileage"] as? String ?: "",
                        seatingCapacity = (vehicle["seatingCapacity"] as? Long)?.toInt() ?: 0,
                        vehicleType = vehicle["vehicleType"] as? String ?: ""
                    )
                    val ad = Ad(adsDataObj, vehicleObj, vehicleImages, seller, mappedVehicleType)
                    if (userId.equals(ad.seller, ignoreCase = true)) {
                        adsList.add(ad)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return adsList
    }

    suspend fun sendMessage(message: ChatMessage) {
        //val messageText: String = binding.messageBox.getText().toString()
        val messageSenderRef = "Message/${message.messageSenderId}/${message.messageReceiverId}"
        val messageReceiverRef = "Message/${message.messageReceiverId}/${message.messageSenderId}"
        val messagesRef = firebaseDataBase.reference
        val user_message_key: DatabaseReference =
            messagesRef.child("Messages").child(message.messageSenderId)
                .child(message.messageReceiverId).push()
        val message_push_id = user_message_key.key
        val calFordDate = Calendar.getInstance()
        val currentDate = SimpleDateFormat("dd-MMMM-yyyy")
        val saveCurrentDate = currentDate.format(calFordDate.time)
        val calFordTime = Calendar.getInstance()
        val currentTime = SimpleDateFormat("HH:mm aa")
        val saveCurrentTime = currentTime.format(calFordDate.time)
        val messageTextBody: HashMap<String, String> = HashMap()
        messageTextBody["message"] = message.message
        messageTextBody["time"] = saveCurrentTime
        messageTextBody["date"] = saveCurrentDate
        messageTextBody["messageReceiverId"] = message.messageReceiverId
        messageTextBody["messageSenderId"] = message.messageSenderId
        val messageBodyDetails: HashMap<String, Any?> = HashMap()
        messageBodyDetails["$messageSenderRef/$message_push_id"] = messageTextBody
        messageBodyDetails["$messageReceiverRef/$message_push_id"] = messageTextBody
        messagesRef.updateChildren(messageBodyDetails).await()
    }

}

