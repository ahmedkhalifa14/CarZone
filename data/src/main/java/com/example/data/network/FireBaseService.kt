package com.example.data.network

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.domain.entity.Ad
import com.example.domain.entity.AdData
import com.example.domain.entity.Car
import com.example.domain.entity.CarData
import com.example.domain.entity.ChatMessage
import com.example.domain.entity.MotorCycleData
import com.example.domain.entity.Motorcycle
import com.example.domain.entity.SavedItem
import com.example.domain.entity.Truck
import com.example.domain.entity.TruckData
import com.example.domain.entity.User
import com.example.domain.entity.UserChat
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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
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
        Log.d("authViewModel", "in FirebaseService user= $user")
        val documentReference = firebaseFireStore.collection("Users").document(user.userId)
        try {
            documentReference.set(user).await()
        } catch (e: Exception) {
            Log.d("authViewModel", "in FirebaseService error is " + e.message)
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

    suspend fun addToSavedItems(savedItem: SavedItem) {
        val collectionReference = firebaseFireStore.collection("SavedItems")
        val documentReference = collectionReference.document()
        documentReference.set(savedItem).await()
    }
/*
    suspend fun getSavedItemsByUserId(id: String): List<SavedItem> {
        val collectionReference = firebaseFireStore.collection("SavedItems")
        val querySnapshot = collectionReference.get().await()
        val savedList = mutableListOf<SavedItem>()
        Log.d("querySnapshot",querySnapshot.toString())
        for (documentSnapshot in querySnapshot.documents) {
            try {
                val adsData = documentSnapshot.get("adsData") as? Map<String, Any>
                val seller = documentSnapshot.getString("seller")
                val adId = documentSnapshot.getString("adId")
                val userId = documentSnapshot.getString("userId")
                val itemId = documentSnapshot.getString("itemId")
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
                Log.d("adsData",adId.toString())
                Log.d("adsData",itemId.toString())

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
                        vehicleModel = vehicle["vehicleModel"] as? String ?: "",
                        manufacturer = vehicle["manufacturer"] as? String ?: "",
                        vehicleName = vehicle["vehicleName"] as? String ?: "",
                        vehicleEngine = vehicle["vehicleEngine"] as? String ?: "",
                        vehicleFuelType = vehicle["vehicleFuelType"] as? String ?: "",
                        vehicleMileage = vehicle["vehicleMileage"] as? String ?: "",
                        seatingCapacity = (vehicle["seatingCapacity"] as? Long)?.toInt() ?: 0,
                        vehicleType = vehicle["vehicleType"] as? String ?: ""
                    )
                    val ad = Ad(adId!!,adsDataObj, vehicleObj, vehicleImages, seller, mappedVehicleType)

                    Log.d("savedItemList",ad.toString())
                    Log.d("adId",ad.adId.toString())

                    val savedItem = SavedItem(userId!!, itemId!!, ad)
                    Log.d("adId",userId.toString())

                    if (id == savedItem.userId) {
                        savedList.add(savedItem)
                        Log.d("savedItemList",savedList.toString())
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        Log.d("savedItemList",savedList.toString())
        return savedList
    }

* */



    suspend fun removeFromSavedItemsByUserId(userId: String, itemId: String) {
        val collectionReference = firebaseFireStore.collection("SavedItems")
        val query = collectionReference
            .whereEqualTo("userId", userId)
            .whereEqualTo("itemId", itemId)

        try {
            val querySnapshot = query.get().await()

            if (!querySnapshot.isEmpty) {
                for (document in querySnapshot.documents) {
                    document.reference.delete().await()
                }
            } else {
                Log.d("removeFromSavedItemsByUserId", "no matching document was found")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
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
            uploadResults.forEachIndexed { _, taskResult ->
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
                val adId = documentSnapshot.getString("adId")
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
                        vehicleModel = vehicle["vehicleModel"] as? String ?: "",
                        manufacturer = vehicle["manufacturer"] as? String ?: "",
                        vehicleName = vehicle["vehicleName"] as? String ?: "",
                        vehicleEngine = vehicle["vehicleEngine"] as? String ?: "",
                        vehicleFuelType = vehicle["vehicleFuelType"] as? String ?: "",
                        vehicleMileage = vehicle["vehicleMileage"] as? String ?: "",
                        seatingCapacity = (vehicle["seatingCapacity"] as? Long)?.toInt() ?: 0,
                        vehicleType = vehicle["vehicleType"] as? String ?: ""
                    )

                    val ad = Ad(adId!!,adsDataObj, vehicleObj, vehicleImages, seller, mappedVehicleType)
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
        val usersCollection = firebaseFireStore.collection("Users")
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
                val adId = documentSnapshot.getString("adId")
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
                        vehicleModel = vehicle["vehicleModel"] as? String ?: "",
                        manufacturer = vehicle["manufacturer"] as? String ?: "",
                        vehicleName = vehicle["vehicleName"] as? String ?: "",
                        vehicleEngine = vehicle["vehicleEngine"] as? String ?: "",
                        vehicleFuelType = vehicle["vehicleFuelType"] as? String ?: "",
                        vehicleMileage = vehicle["vehicleMileage"] as? String ?: "",
                        seatingCapacity = (vehicle["seatingCapacity"] as? Long)?.toInt() ?: 0,
                        vehicleType = vehicle["vehicleType"] as? String ?: ""
                    )

                    val ad = Ad(adId!!,adsDataObj, vehicleObj, vehicleImages, seller, mappedVehicleType)

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
                val adId = documentSnapshot.getString("adId")

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
                        vehicleModel = vehicle["vehicleModel"] as? String ?: "",
                        manufacturer = vehicle["manufacturer"] as? String ?: "",
                        vehicleName = vehicle["vehicleName"] as? String ?: "",
                        vehicleEngine = vehicle["vehicleEngine"] as? String ?: "",
                        vehicleFuelType = vehicle["vehicleFuelType"] as? String ?: "",
                        vehicleMileage = vehicle["vehicleMileage"] as? String ?: "",
                        seatingCapacity = (vehicle["seatingCapacity"] as? Long)?.toInt() ?: 0,
                        vehicleType = vehicle["vehicleType"] as? String ?: ""
                    )
                    val ad = Ad(adId!!,adsDataObj, vehicleObj, vehicleImages, seller, mappedVehicleType)
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
        val messageSenderRef = "Message/${message.messageSenderId}/${message.messageReceiverId}"
        val messageReceiverRef = "Message/${message.messageReceiverId}/${message.messageSenderId}"
        val messagesRef = firebaseDataBase.reference
        val userMessageKey: DatabaseReference =
            messagesRef.child("Messages").child(message.messageSenderId)
                .child(message.messageReceiverId).push()
        val messagePushId = userMessageKey.key
        val calFordDate = Calendar.getInstance()
        val currentDate = SimpleDateFormat("dd-MMMM-yyyy", Locale.getDefault())
        val saveCurrentDate = currentDate.format(calFordDate.time)
        val currentTime = SimpleDateFormat("HH:mm aa", Locale.getDefault())
        val saveCurrentTime = currentTime.format(calFordDate.time)
        val currentTimestamp = System.currentTimeMillis()
        val messageTextBody: HashMap<String, Any> = HashMap()
        messageTextBody["message"] = message.message
        messageTextBody["time"] = saveCurrentTime
        messageTextBody["date"] = saveCurrentDate
        messageTextBody["messageReceiverId"] = message.messageReceiverId
        messageTextBody["messageSenderId"] = message.messageSenderId
        messageTextBody["timestamp"] = currentTimestamp
        val messageBodyDetails: HashMap<String, Any?> = HashMap()
        messageBodyDetails["$messageSenderRef/$messagePushId"] = messageTextBody
        messageBodyDetails["$messageReceiverRef/$messagePushId"] = messageTextBody
        messagesRef.updateChildren(messageBodyDetails).await()
    }

    fun getMessages(receiverID: String, senderID: String): LiveData<List<ChatMessage>> {
        val messagesRef = firebaseDataBase.reference
        val query = messagesRef.child("Message").child(receiverID).child(senderID)
        val liveData = MutableLiveData<List<ChatMessage>>()
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val messages = mutableListOf<ChatMessage>()
                for (childSnapshot in dataSnapshot.children) {
                    val message = childSnapshot.getValue(ChatMessage::class.java)
                    message?.let { messages.add(it) }
                }
                liveData.postValue(messages)
                // Add logging to check for data presence
                if (messages.isNotEmpty()) {
                    Log.d("getMessagesState", "Data is present")
                } else {
                    Log.d("getMessagesState", "No data found")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error if needed
            }
        })

        return liveData
    }
    suspend fun saveUserChats(userChat: UserChat) {
        try {
            val batch = firebaseFireStore.batch()
            val documentRef1 = firebaseFireStore.collection("Chats")
                .document(userChat.userId + userChat.otherUserId)
            batch.set(documentRef1, userChat)
            val documentRef2 = firebaseFireStore.collection("Chats")
                .document(userChat.otherUserId + userChat.userId)
            batch.set(documentRef2, userChat)
            batch.commit().await()
            Log.d("saveUserChats", "Saved")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    suspend fun getUserChats(userId: String): List<UserChat> {
        val userChats = mutableListOf<UserChat>()
        try {
            val querySnapshot = firebaseFireStore.collection("Chats").get().await()
            for (document in querySnapshot.documents) {
                val userID = document.getString("userId")!!
                val chatId = document.getString("chatId")!!
                val otherUserId = document.getString("otherUserId")!!
                val otherUserName = document.getString("otherUserName")!!
                val userName = document.getString("userName")!!
                val userImg = document.getString("userImg")!!
                val otherUserImg = document.getString("otherUserImg")!!
                val latestMessage = document.getString("latestMessage")!!
                val timestamp = document.getLong("timestamp")!!
                val chat =
                    UserChat(
                        userID,
                        chatId,
                        otherUserId,
                        otherUserName,
                        userName,
                        userImg,
                        otherUserImg,
                        latestMessage,
                        timestamp
                    )
                if (userId == userID || userId == otherUserId) userChats.add(chat)
            }
        } catch (e: Exception) {
            throw e
        }
        return userChats
    }
    suspend fun getSavedItemsByUserId(id:String): MutableList<SavedItem> {
        val collectionReference = firebaseFireStore.collection("SavedItems")
        val querySnapshot = collectionReference.get().await()
        val savedItemsList = mutableListOf<SavedItem>()

        for (documentSnapshot in querySnapshot.documents) {
            try {
                val adData = documentSnapshot.get("ad") as? Map<String, Any>
                val userId = documentSnapshot.getString("userId")
                val itemId = documentSnapshot.getString("itemId")

                val adId = adData?.get("adId") as? String
                val ad = createAdFromMap(adData)

                if (userId != null && itemId != null && adId != null && ad != null&&id==userId) {
                    val savedItem = SavedItem(userId, itemId, ad)
                    savedItemsList.add(savedItem)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("savedItems", e.toString())
            }
        }

        return savedItemsList
    }
    private fun createAdFromMap(adData: Map<String, Any>?): Ad? {
        if (adData == null) return null
        val adsDateTimestamp = adData["date"] as? com.google.firebase.Timestamp
        val adsDate = adsDateTimestamp?.toDate()
        val adsDataObj = AdData(
            title = adData["title"] as? String ?: "",
            description = adData["description"] as? String ?: "",
            negotiable = adData["negotiable"] as? Boolean ?: false,
            price = adData["price"] as? String ?: "",
            location = adData["location"] as? String ?: "",
            date = adsDate ?: Date()
        )
        val vehicle = adData["vehicle"] as? Map<String, Any>
        val vehicleImages = adData["vehicleImages"] as? List<String>
        val seller = adData["seller"] as? String
        val vehicleTypeMap = adData["vehicleType"] as? Map<String, Map<String, Any>>
        val mappedVehicleType = vehicleTypeMap?.let { createVehicleDataFromMap(it) }
        if (vehicle != null && vehicleImages != null && seller != null && mappedVehicleType != null) {
            val vehicleObj = Vehicle(
                vehicleModel = vehicle["vehicleModel"] as? String ?: "",
                manufacturer = vehicle["manufacturer"] as? String ?: "",
                vehicleName = vehicle["vehicleName"] as? String ?: "",
                vehicleEngine = vehicle["vehicleEngine"] as? String ?: "",
                vehicleFuelType = vehicle["vehicleFuelType"] as? String ?: "",
                vehicleMileage = vehicle["vehicleMileage"] as? String ?: "",
                seatingCapacity = (vehicle["seatingCapacity"] as? Long)?.toInt() ?: 0,
                vehicleType = vehicle["vehicleType"] as? String ?: ""
            )

            return Ad(adData["adId"] as String, adsDataObj, vehicleObj, vehicleImages, seller, mappedVehicleType)
        }

        return null
    }
    private fun createVehicleDataFromMap(vehicleData: Map<String, Map<String, Any>>): VehicleData? {

        val typeKey = vehicleData.keys.firstOrNull()
        val typeData = vehicleData[typeKey]

        return when (typeKey) {
            "van" -> {
                val cargoCapacity = typeData?.get("cargoCapacity") as? Double?
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



}







