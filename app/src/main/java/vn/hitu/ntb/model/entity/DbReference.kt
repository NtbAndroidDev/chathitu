package vn.hitu.ntb.model.entity

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import vn.hitu.ntb.constants.AppConstants
import vn.hitu.ntb.utils.AppUtils

object DbReference {


    var mDatabase: DatabaseReference? = null
    fun getInstance(): DatabaseReference {
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance().reference
            return mDatabase!!
        }
        return mDatabase!!
    }

    fun writeNewUser(
        uid: String,
        email: String?,
        name: String?,
        image: String?,
        isOnline: Boolean,
        did: String?
    ) {
        mDatabase = getInstance()
        val user = UserData(uid, email!!, name!!, image!!, isOnline, did!!)
        val userValues: Map<String, Any> = user.toMap()
        val userUpdates: MutableMap<String, Any> = HashMap()
        userUpdates["/Users/$uid"] = userValues
        mDatabase!!.updateChildren(userUpdates)
    }


    fun writeImageUser(uid: String?, imageId: String) {
        mDatabase = getInstance()
        mDatabase!!.child("Users").child(uid!!).child("image").setValue(imageId)
    }
    fun changeImageGroup(gId: String?, imageId: String) {
        mDatabase = getInstance()
        mDatabase!!.child("Groups").child(gId!!).child("imageId").setValue(imageId)
    }
    fun addUserGroup(uid: String?, gid: String?) {
        mDatabase = getInstance()
        mDatabase!!.child("Groups").child(gid!!).child("listUidMember").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val listGroup = ArrayList<String?>()
                if (snapshot.exists()) {
                    for (dataSnapshot in snapshot.children) {
                        if (dataSnapshot.exists()) {
                            val id = dataSnapshot.getValue(String::class.java)
                            listGroup.add(id)
                        }
                    }
                    listGroup.add(uid)
                    mDatabase!!.child("Groups").child(gid).child("listUidMember").setValue(listGroup)
                } else {
                    listGroup.add(uid)
                    mDatabase!!.child("Groups").child(gid).child("listUidMember").setValue(listGroup)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }
    fun removeUserGroup(uid: String?, gid: String?) {
        mDatabase = getInstance()
        mDatabase!!.child("Groups").child(gid!!).child("listUidMember").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val listGroup = ArrayList<String?>()
                if (snapshot.exists()) {
                    for (dataSnapshot in snapshot.children) {
                        if (dataSnapshot.exists()) {
                            val id = dataSnapshot.getValue(String::class.java)
                            listGroup.add(id)
                        }
                    }
                    if (listGroup.isNotEmpty()){
                        listGroup.remove(uid)
                        mDatabase!!.child("Groups").child(gid).child("listUidMember").setValue(listGroup)

                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }
    fun changeBackground(background: String?, gid: String?) {
        mDatabase = getInstance()
        mDatabase!!.child("Groups").child(gid!!).child("background").setValue(background).addOnCompleteListener {
            if (it.isSuccessful)
                AppUtils.sendMessage("đã thay đổi hình nền chat", gid)
        }
    }
    fun disbandTheGroup(gid: String?) {
        mDatabase = getInstance()
        mDatabase!!.child("Groups").child(gid!!).removeValue()
    }
    fun updateUserGroups(uid: String?, gid: String?) {
        val databaseReference = mDatabase!!.child("UserGroups").child(uid!!)
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val listGroup = ArrayList<String?>()
                if (snapshot.exists()) {
                    for (dataSnapshot in snapshot.children) {
                        if (dataSnapshot.exists()) {
                            val id = dataSnapshot.getValue(String::class.java)
                            listGroup.add(id)
                        }
                    }
                    listGroup.add(gid)
                    databaseReference.setValue(listGroup)
                } else {
                    listGroup.add(gid)
                    databaseReference.setValue(listGroup)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }


    fun writeIsOnlineUserAndGroup(uid: String, isOnline: Boolean) {
        mDatabase = getInstance()
        mDatabase!!.child("Users").child(uid).child("isOnline").setValue(isOnline)
        FirebaseDatabase.getInstance().getReference("Groups")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (dataSnapshot in snapshot.children) {
                        val group: GroupData? = dataSnapshot.getValue(GroupData::class.java)
                        //get uid of user other than the current user
                        if (group!!.listUidMember.contains(uid)) {
                            mDatabase!!.child("Groups").child(group.gid).child("isOnline")
                                .setValue(isOnline)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("isOnline", isOnline.toString())
                }
            })
    }

    fun writeNewGroup(
        name: String,
        listUidMember: ArrayList<String>,
        imageId: String,
        isOnline: Boolean,
        lastMessage: String,
        lastTime: String?
    ): String {
        mDatabase = getInstance()
        val mAuth = FirebaseAuth.getInstance()
        val gid = mDatabase!!.child("Groups").push().key //groupId
        val group = GroupData(gid!!, name, listUidMember, imageId, isOnline, lastMessage, lastTime!!, "background")
        if (listUidMember.size > 2)
            newLeader(gid, mAuth.currentUser!!.uid)
        val groupValues: Map<String, Any> = group.toMap()
        val groupUpdates: MutableMap<String, Any> = HashMap()
        groupUpdates["/Groups/$gid"] = groupValues
        mDatabase!!.updateChildren(groupUpdates)
        return gid
    }


    private fun newLeader(gId : String, id : String){  //tạo trưởng nhóm
        mDatabase = getInstance()
        val group = LeaderGroup(gId, id)
        val groupValues: Map<String, Any> = group.toMap()
        val groupUpdates: MutableMap<String, Any> = HashMap()
        groupUpdates["/Leader/$gId"] = groupValues
        mDatabase!!.updateChildren(groupUpdates)
    }

    fun requestFriend(userId : String, context : Context){
        val mAuth = FirebaseAuth.getInstance()
        val hashMap = HashMap<String, Any>()
        hashMap["contactType"] = AppConstants.WAITING_RESPONSE //gửi lời mời
        hashMap["id"] = userId //gửi lời mời
        val mRef = FirebaseDatabase.getInstance().reference.child("Requests").child(mAuth.currentUser!!.uid).updateChildren(hashMap).addOnCompleteListener {
            if (it.isSuccessful){
                Toast.makeText(context, "Friend request has been sent successfully", Toast.LENGTH_SHORT).show()
            }
        }
        
    }
    fun cancelRequest(userId : String, context : Context){
        val mAuth = FirebaseAuth.getInstance()
        val mRef = FirebaseDatabase.getInstance().reference.child("Requests").child(mAuth.currentUser!!.uid).removeValue().addOnCompleteListener {
            if (it.isSuccessful){
                Toast.makeText(context, "Friend request has been sent successfully", Toast.LENGTH_SHORT).show()
            }
        }

    }
    fun refuseRequest(userId : String, context : Context){
        val mRef = FirebaseDatabase.getInstance().reference.child("Requests").child(userId).removeValue().addOnCompleteListener {
            if (it.isSuccessful){
                Toast.makeText(context, "Friend request has been sent successfully", Toast.LENGTH_SHORT).show()
            }
        }

    }

    fun acceptFriend(user : UserData, context : Context){
        val mAuth = FirebaseAuth.getInstance()
        val mRef = FirebaseDatabase.getInstance().reference.child("Requests").child(user.uid).removeValue().addOnCompleteListener {
            if (it.isSuccessful){
                val mHashMap = HashMap<String, Any>()
                mHashMap["yId"] = user.uid
                mHashMap["mId"] = mAuth.currentUser!!.uid
                val key = mDatabase!!.child("Friends").push().key
                val mFriend = FirebaseDatabase.getInstance().reference.child("Friends").child(key!!).updateChildren(mHashMap).addOnCompleteListener {

                    if (it.isSuccessful){
                        val yHashMap = HashMap<String, Any>()
                        yHashMap["yId"] = mAuth.currentUser!!.uid
                        yHashMap["mId"] = user.uid
                        val yKey = mDatabase!!.child("Friends").push().key
                        val yFriend = FirebaseDatabase.getInstance().reference.child("Friends").child(yKey!!).updateChildren(yHashMap).addOnCompleteListener {
                            Toast.makeText(context, "You added friend ", Toast.LENGTH_SHORT).show()
                        }
                    }

                }
            }
        }
    }




    //a user have many groups.

}