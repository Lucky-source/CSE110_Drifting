package backend.util.database;

import android.net.Uri;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import backend.util.time.DriftTime;

/*
**A Database class aiming at providing the fronted with needed database operations
* Our user cannot access our database without using SetDatabase class
*** Applied the model layer of MVC
 */
public class SetDatabase {
    private DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReference();
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    public static Pair<String, String> parseName(String filename) {
        int i = filename.lastIndexOf('.');
        if (i > 0 && i < filename.length() - 1) {
            return new Pair<String, String>(filename.substring(0, i), filename.substring(i + 1));
        } else return null;
    }

    //add a new user to the database
    public void addNewUser(UserProfile userProfile) {
        //System.out.println(database == null);
        DatabaseReference usersRef = database.child("user");
        usersRef.child(userProfile.user_id).setValue(userProfile);
    }

    //add a new chat to the database
    public void addNewChat(Chat chat) {
        //System.out.println(database == null);
        DatabaseReference chatsRef = database.child("Chats");
        chatsRef.push().setValue(chat);
        Log.d("successful add ", "yay" + chat.receiver + chat.sender);
    }

    //add a new bottle to the database
    public void addNewBottle(Bottle_back this_bottle, Uri file) {
        DatabaseReference bottlesRef = database.child("bottle");

        if (this_bottle.ext != null) {
            StorageReference targetRef;

            if (!this_bottle.isVideo)
                targetRef = storageRef.child("picture/" + this_bottle.bottleID + ".jpg");
            else
                targetRef = storageRef.child("video/" + this_bottle.bottleID + ".mp4");

            UploadTask uploadTask = targetRef.putFile(file);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {

                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    targetRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Uri downloadUrl = uri;
                            //Log.d("eafawnvaw", uri.toString());


                            if (this_bottle.isVideo) this_bottle.video = uri.toString();
                            else this_bottle.picture = uri.toString();
                            //Do what you want with the url
                            bottlesRef.child(String.valueOf(this_bottle.bottleID)).setValue(this_bottle);
                        }

                    });
                }
            });
        } else {
            bottlesRef.child(String.valueOf(this_bottle.bottleID)).setValue(this_bottle);
        }

    }

    //upload the avatar to the database
    public void uploadAvatars(String user_id, Uri file) {
        StorageReference targetRef = storageRef.child("avatars/" + user_id + ".jpg");
        DatabaseReference targetdataRef = database.child("avatars/");

        UploadTask uploadTask = targetRef.putFile(file);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                String user_id = auth.getUid();
                targetRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Uri downloadUrl = uri;
                        targetdataRef.child(user_id + "/picture").setValue(uri.toString());
                        //Do what you want with the url
                    }

                });
            }
        });
    }

    // get sent bottles for the bag
    public void get_sent_bottles(ArrayList<String> sentBottle, ArrayList<String> sentTime, ArrayList<String> sentLocation) {
        ArrayList<Bottle_back> sent_bottles = new ArrayList<Bottle_back>();
        //get current userID
        FirebaseAuth fAuth;
        DriftTime d_time = new DriftTime();
        fAuth = FirebaseAuth.getInstance();
        String userID = fAuth.getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        DatabaseReference user_ref = ref.child("user").child(userID).child("send_list");

        Object hm_obj = new Object();
        user_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.getValue(hm_obj.getClass()) != null) {
                    HashMap<String, Boolean> hp = (HashMap) snapshot.getValue(hm_obj.getClass());
                    for (Map.Entry<String, Boolean> set : hp.entrySet()) {
                        //set.getKey() is the bottle id
                        //Log.d("HashMap: ","Key: "+ set.getKey() + " Val: " + set.getValue());
                        DatabaseReference bottle_ref = ref.child("bottle").child(set.getKey());
                        bottle_ref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot_2) {
                                String msg = snapshot_2.child("message").getValue(String.class);
                                sentBottle.add(msg);
                                long time = snapshot_2.child("timestamp").getValue(Long.class);
                                sentTime.add(d_time.getDate(time));
                                String city = snapshot_2.child("city").getValue(String.class);
                                sentLocation.add(city);
                                bottle_ref.removeEventListener(this);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                }

                user_ref.removeEventListener(this);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //get picked bottles for the bag
    public void get_picked_bottles(ArrayList<String> pickedBottle, ArrayList<String> pickedTime, ArrayList<String> pickedLocation) {
        //get current userID
        DriftTime d_time = new DriftTime();
        FirebaseAuth fAuth;
        fAuth = FirebaseAuth.getInstance();
        String userID = fAuth.getUid();
        //Log.d("IDIDIDIDID", userID);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        DatabaseReference user_ref = ref.child("user").child(userID).child("receive_list");

        Object hm_obj = new Object();

        user_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue(hm_obj.getClass()) != null) {
                    HashMap<String, Boolean> hp = (HashMap) snapshot.getValue(hm_obj.getClass());
                    for (Map.Entry<String, Boolean> set : hp.entrySet()) {
                        if (set.getValue() == true) {
                            //set.getKey() is the bottle id
                            //Log.d("HashMap: ","Key: "+ set.getKey() + " Val: " + set.getValue());
                            DatabaseReference bottle_ref = ref.child("bottle").child(set.getKey());
                            bottle_ref.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot_2) {
                                    String msg = snapshot_2.child("message").getValue(String.class);
                                    pickedBottle.add(msg);
                                    long time = snapshot_2.child("timestamp").getValue(Long.class);
                                    pickedTime.add(d_time.getDate(time));
                                    String city = snapshot_2.child("city").getValue(String.class);
                                    pickedLocation.add(city);
                                    bottle_ref.removeEventListener(this);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }
                }
                user_ref.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //throw a bottle back function
    public void throw_bottle_back(String finalBottleID) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("bottle");
        DatabaseReference this_bottle_data = reference.child(finalBottleID);
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        String current_user = fAuth.getUid();

        // set isViewed to false
        Map<String, Object> bottle_update = new HashMap<>();
        bottle_update.put("isViewed", false);
        this_bottle_data.updateChildren(bottle_update);

        //add the user info to bottle history
        final DatabaseReference added_user= this_bottle_data.child("pickHistory").child(current_user);
        added_user.setValue(true);

        //remove the bottle from user's receive list
        DatabaseReference UserRef = FirebaseDatabase.getInstance().getReference().child("user").child(current_user);
        final DatabaseReference added_bottle= UserRef.child("receive_list");
        Map<String, Object> user_update = new HashMap<>();
        user_update.put(finalBottleID, false);
        added_bottle.updateChildren(user_update);
    }

    //set isviewed to be true
    public void view_bottle(String bottleID){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("bottle");
        DatabaseReference this_bottle_data = reference.child(bottleID);
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        String current_user = fAuth.getUid();

        if(!bottleID.equals("")) {
            Map<String, Object> bottle_update = new HashMap<>();
            bottle_update.put("isViewed", true);
            this_bottle_data.updateChildren(bottle_update);

            //save the bottle id in user's receive list
            DatabaseReference UserRef = FirebaseDatabase.getInstance().getReference().child("user").child(current_user);
            final DatabaseReference added_bottle = UserRef.child("receive_list");
            Map<String, Object> user_update = new HashMap<>();
            user_update.put(bottleID, true);
            added_bottle.updateChildren(user_update);
        }
    }

    //get a new bottle
    public void get_bottle(Bottle_back[] this_bottle_list) {
        //get database reference
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("bottle");
        //get current userID
        FirebaseAuth fAuth;
        fAuth = FirebaseAuth.getInstance();
        reference.orderByChild("isViewed").equalTo(false).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    Bottle_back this_bottle = snapshot1.getValue(Bottle_back.class);
                    Log.d("urlefawefea",this_bottle.picture);
                    //String bottleID = this_bottle.getBottleID();
                    String userID = fAuth.getUid();
                    HashMap<String, Boolean> this_history= this_bottle.getPickHistory();

                    //debug: print picked history
                    for(String users : this_history.keySet()) {
                        Log.d("", "picked content:");
                        Log.d("user:", users);
                    }

                    //check if the bottle is viewed
                    if(this_bottle.getIsViewed()) {
                        Log.d("isViewed","A viewed bottle was returned");
                        continue;
                    }

                    //TODO: comment for test purpose, REUSE for formal product
                    //check if the bottle is from the same user
//                                    if(this_bottle.getUserID().equals(userID)){
//                                        continue;
//                                    }

                    //check if the bottle has been picked up by the same user before
                    if(this_bottle.pickHistory.containsKey(userID)){
                        Log.d("isPicked","A bottle picked before was returned");
                        continue;
                    }

                    else {
                        reference.removeEventListener(this);
                        this_bottle_list[0] = this_bottle;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    //get bottle likes
    public int get_likes(String bottleID){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("bottle");
        DatabaseReference this_bottle_data = reference.child(bottleID);
        final int[] like = new int[1];

        this_bottle_data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                like[0] = Integer.parseInt(snapshot.child("likes").getValue().toString());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return like[0];
    }

    //update bottle likes
    public void update_likes(String bottleID){

        int current_like = get_likes(bottleID);

        //reach the bottle
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("bottle");
        DatabaseReference this_bottle_data = reference.child(bottleID);

        if(!bottleID.equals("")) {
            Map<String, Object> bottle_update = new HashMap<>();
            bottle_update.put("likes", (current_like+1));
            this_bottle_data.updateChildren(bottle_update);

        }

    }



}


