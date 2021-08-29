package com.example.mymargdarshaka;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class AuthSignupStudents2 extends AppCompatActivity {

    AutoCompleteTextView a;

    CheckBox english, math, hindi, telugu, physics, chemistry, biology, history, geography, science, social;
    Button submit_button;
    boolean x;
    private DatabaseReference rootRef;

    ArrayList<String> subjects = new ArrayList<>();
    HashMap<String, String> regSub = new HashMap<>();

    Integer it;

    Bundle extras;
    String name;
    String email;
    String class_selected;
    String language_selected;
    String phone;
    String time_selected;
    ArrayList<Map<String,String>> regs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_signup_students2);

        x = true;

        a = AuthSignupStudents1.text_view_class;
        String s = a.getEditableText().toString();

        english = (CheckBox) findViewById(R.id.check_english);
        math = (CheckBox) findViewById(R.id.check_math);
        hindi = (CheckBox) findViewById(R.id.check_hindi);
        telugu = (CheckBox) findViewById(R.id.check_telugu);
        physics = (CheckBox) findViewById(R.id.check_physics);
        chemistry = (CheckBox) findViewById(R.id.check_chemistry);
        biology = (CheckBox) findViewById(R.id.check_biology);
        science = (CheckBox) findViewById(R.id.check_science);
        social = (CheckBox) findViewById(R.id.check_social);
        history = (CheckBox) findViewById(R.id.check_history);
        geography = (CheckBox) findViewById(R.id.check_geography);

        english.setVisibility(View.VISIBLE);
        hindi.setVisibility(View.VISIBLE);
        math.setVisibility(View.VISIBLE);
        telugu.setVisibility(View.VISIBLE);

        if (s.equals("Class 11") || s.equals("Class 12")) {
            physics.setVisibility(View.VISIBLE);
            chemistry.setVisibility(View.VISIBLE);
            biology.setVisibility(View.VISIBLE);
            history.setVisibility(View.VISIBLE);
            geography.setVisibility(View.VISIBLE);
        } else {
            science.setVisibility(View.VISIBLE);
            social.setVisibility(View.VISIBLE);
        }


        submit_button = (Button) findViewById(R.id.studentSignupButton2);
        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                extras = getIntent().getExtras();
                name = extras.getString("name");
                email = extras.getString("email");
                class_selected = extras.getString("class_selected");
                language_selected = extras.getString("language_selected");
                phone = extras.getString("phone");
                time_selected = extras.getString("time_selected");


                if (english.isChecked()) {
                    subjects.add("english"+class_selected.toString());
//                    regSub.add(new Pair<>("english", "mentorid1"));
                }
                if (hindi.isChecked()) {
                    subjects.add("hindi"+class_selected.toString());
//                    regSub.add(new Pair<>("english", "mentorid2"));
                }
                if (telugu.isChecked()) {
                    subjects.add("telugu"+class_selected.toString());
//                    regSub.add(new Pair<>("english", "mentorid3"));
                }
                if (math.isChecked()) {
                    subjects.add("math"+class_selected.toString());
                }
                if (physics.isChecked()) {
                    subjects.add("physics"+class_selected.toString());
                }
                if (chemistry.isChecked()) {
                    subjects.add("chemistry"+class_selected.toString());
                }
                if (biology.isChecked()) {
                    subjects.add("biology"+class_selected.toString());
                }
                if (history.isChecked()) {
                    subjects.add("history"+class_selected.toString());
                }
                if (geography.isChecked()) {
                    subjects.add("geography"+class_selected.toString());
                }
                if (social.isChecked()) {
                    subjects.add("social"+class_selected.toString());
                }
                if (science.isChecked()) {
                    subjects.add("science"+class_selected.toString());
                }

                // uncomment this !!
                DatabaseReference newStudentRef = FirebaseDatabase.getInstance().getReference("users").push();


                String key = newStudentRef.getKey();
                ArrayList<Pair<Integer, Pair<String, ArrayList<String>>>> arr = new ArrayList<>();
                rootRef = FirebaseDatabase.getInstance().getReference();
                DatabaseReference mentorsRef = rootRef.child("mentors");

                ArrayList<Pair<Integer, Pair<String, String>>> mentors = new ArrayList<>();     // Integer, <mentor id, subject>

                mentorsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        long l=snapshot.getChildrenCount();
                        long k=0;
                        for (DataSnapshot child : snapshot.getChildren()) {

                            k++;
                            Log.e("mentor: ", child.getValue().toString());

                            MentorDetails mentor = child.getValue(MentorDetails.class);
                            Log.e("1",String.valueOf(mentor.getClasses().contains(class_selected)));
                            Log.e("2",String.valueOf(mentor.getPrefLangs().contains(language_selected)));
                            Log.e("3",String.valueOf(mentor.getTimeSlots().contains(time_selected)));
                            for(int i=0;i<subjects.size();i++){
                                Log.e("lkdsfl",subjects.get(i));
                            }
                            if (mentor.getClasses().contains(class_selected) && mentor.getPrefLangs().contains(language_selected) && mentor.getTimeSlots().contains(time_selected)) {
                                for (String i : mentor.getTeachSubjects()) {
                                    Log.e("sub",i);
                                    if (subjects.contains(i)) {
                                        mentors.add(Pair.create(mentor.getRegStudents()==null?0:mentor.getRegStudents().size(), Pair.create(child.getKey(), i)));
                                    }
                                }
                            }
                            if(k==l){
                                assignMentor(key,newStudentRef,mentorsRef,mentors);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


                // --------------------------------------------------------------------------------------------------------------------

//                newStudentRef.setValue(new UserSchema(newStudentRef.getKey(), name, email, phone, class_selected, language_selected, subjects, time_selected, regSub));

                //newStudentRef.child("name").setValue(name);
                //newStudentRef.child("email").setValue(email);
                //newStudentRef.child("standard").setValue(class_selected);
                //newStudentRef.child("prefLang").setValue(language_selected);
                //newStudentRef.child("timeSlots").setValue(time_selected);
                //newStudentRef.child("intrSubjects").setValue(subjects);

//                String key=newStudentRef.getKey();
//
//                ArrayList<Pair<Integer,Pair<String,ArrayList<String>>>> arr = new ArrayList<>();
//
//                rootRef = FirebaseDatabase.getInstance().getReference();
//
//                DatabaseReference mentorsRef = rootRef.child("mentors");
//                try {
//                    mentorsRef.addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            long l = snapshot.getChildrenCount();
//                            long k = 0;
//                            for (DataSnapshot child : snapshot.getChildren()) {
//                                Log.e("hello: ", child.getValue().toString());
//                                try {
//                                    MentorDetails mentor = child.getValue(MentorDetails.class);
//
//                                    ArrayList<String> classes = mentor.getClasses();
//                                    ArrayList<String> prefLangs = mentor.getPrefLangs();
//                                    ArrayList<String> teachSubjects = mentor.getTeachSubjects();
//                                    ArrayList<String> timeSlots = mentor.getTimeSlots();
//                                    ArrayList<Map<String, String>> regStudents = mentor.getRegStudents();
//                                    if (regStudents == null) {
//                                        regStudents = new ArrayList<>();
//                                    }
//                                    boolean fine = false;
//                                    if (classes.contains(s)) {
//                                        fine = true;
//=======

//                mentorsRef.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        long l=snapshot.getChildrenCount();
//                        long k=0;
//                        for(DataSnapshot child: snapshot.getChildren()){
//                            Log.e("Mentor key", child.getKey());
//                            Log.e("Mentor val", child.getValue().toString());
//
//                            MentorDetails mentor=child.getValue(MentorDetails.class);
//
//                            ArrayList<String> classes=mentor.getClasses();
//                            ArrayList<String> prefLangs=mentor.getPrefLangs();
//                            ArrayList<String> teachSubjects=mentor.getTeachSubjects();
//                            ArrayList<String> timeSlots=mentor.getTimeSlots();
//                            ArrayList<String> regStudents=mentor.getRegStudents();
//                            boolean fine=false;
//                            if(classes.contains(s)){
//                                fine=true;
//                            }
//                            if(fine){
//                                if(prefLangs.contains(language_selected)){
//                                    fine=true;
//                                }
//                            }
//                            if(fine){
//                                if(timeSlots.contains(time_selected)){
//                                    fine=true;
//                                }
//                            }
//                            // t is an array of matched subjects
//                            ArrayList<String> t=new ArrayList<>();
//                            if(fine){
//                                fine=false;
//                                for(int i=0;i<subjects.size();i++){
//                                    if(teachSubjects.contains(subjects.get(i))){
//                                        fine=true;
//                                        t.add(subjects.get(i));
//                                    }
//                                    else{
//                                        fine=false;
//                                    }
//                                    Log.e("1: ", String.valueOf(fine));
//                                    if (fine) {
//                                        if (prefLangs.contains(language_selected)) {
//                                            fine = true;
//                                        }
//                                        else{
//                                            fine=false;
//                                        }
//                                    }
//                                    Log.e("2: ", String.valueOf(fine));
//                                    if (fine) {
//                                        for(String i:timeSlots){
//                                            Log.e("fds",i);
//                                        }
//                                        Log.e("fds2",time_selected);
//                                        if (timeSlots.contains(time_selected)) {
//                                            fine = true;
//                                        }
//                                        else{
//                                            fine=false;
//                                        }
//                                    }
//                                    Log.e("3: ", String.valueOf(fine));
//                                    ArrayList<String> t = new ArrayList<>();
//
//                                    for (String i : teachSubjects) {
//                                        System.out.println(i);
//                                    }
//                                    System.out.println();
//                                    for (String i : subjects) {
//                                        System.out.println(i);
//                                    }
//
//                                    if (fine) {
//                                        fine = false;
//                                        for (int i = 0; i < subjects.size(); i++) {
//                                            if (teachSubjects.contains(subjects.get(i))) {
//                                                fine = true;
//                                                t.add(subjects.get(i));
//                                            }
//                                        }
//                                    }
//                                    Log.e("4: ", String.valueOf(fine));
//                                    if (fine) {
//                                        arr.add(Pair.create(regStudents.size(), Pair.create(child.getKey(), t)));
//                                    }
//
//                                    Log.e("k: ", String.valueOf(k));
//
//                                    if (k >= l - 1) {
//                                        assignMentors(arr, key, newStudentRef);
//                                        Intent i = new Intent(AuthSignupStudents2.this, MyMentors.class);
//                                        i.putExtra("noMentorsAssignedHere",x);
//                                        i.putExtra("userid",key);
//                                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                                        startActivity(i);
//                                        break;
//                                    }
//
//                                    k++;
//                                }
//                                catch (Exception e){
//
//                                }
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//
//                        }
//                    });
//                }
//                catch (Exception e){
//                    Log.e("Error ---- ",e.getMessage());
//                }
//
//            }
//
//            public void assignMentors(ArrayList<Pair<Integer,Pair<String,ArrayList<String>>>> arr, String key, DatabaseReference newStudentRef){
//                ArrayList<Pair<String,Pair<String, String>>> forMentors = new ArrayList<>();
//                if(arr.size()>0) {
//                    Collections.sort(arr, new Comparator<Pair<Integer, Pair<String, ArrayList<String>>>>() {
//                        @Override
//                        public int compare(Pair<Integer, Pair<String, ArrayList<String>>> p1, Pair<Integer, Pair<String, ArrayList<String>>> p2) {
//                            return p1.first - p2.first;
//                        }
//                    });
//                    ArrayList<String> tempSub = new ArrayList<>();
//                    for (int i = 0; i < subjects.size(); i++) {
//                        tempSub.add(subjects.get(i));
//                    }
//
//                    for(int i=0;i<arr.size();i++){
//                        for(int j=0;j<arr.get(i).second.second.size();j++){
//                            if(tempSub.contains(arr.get(i).second.second.get(j))){
//                                regSub.add(Pair.create(arr.get(i).second.first,arr.get(i).second.second.get(j)));
//                                tempSub.remove(arr.get(i).second.second.get(j));
//                                forMentors.add(Pair.create(arr.get(i).second.second.get(j),Pair.create(key, arr.get(i).second.first)));
////                                System.out.println(arr.get(i).second.second.get(j));
//                            }
//                        }
//                        if (tempSub.size() == 0) {
//                            break;
//                        }
//                    }
//                }
//                else{
//                    x=true;
//                }
//                    UserDetails userSchema = new UserDetails(name,email,phone,class_selected,language_selected,subjects,time_selected,regSub);
//                    newStudentRef.setValue(userSchema);
//
//                    if(forMentors.size()>0){
//                        rootRef = FirebaseDatabase.getInstance().getReference();
//
//                        DatabaseReference mentorsRef = rootRef.child("mentors");
//
//                        for(int i=0;i<forMentors.size();i++){
//                            System.out.println("ajflakjflkjafkljk;lsklsdafl;aklsfkladsklsfjklaj;l" + forMentors.get(i).first);
//                            System.out.println("safsadfsdafasfsadfsf" + forMentors.get(i).second.first);
//                            System.out.println(" asfsafasfasdfdsafsafsfdsa" + forMentors.get(i).second.second);
//                            Map<String,String> m=new HashMap<>();
//                            m.put(forMentors.get(i).first, forMentors.get(i).second.first);
//                            Map<String, Object> childUpdates = new HashMap<>();
//
//
//                            mentorsRef.orderByChild(forMentors.get(i).second.second).addListenerForSingleValueEvent(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                    regs=snapshot.getValue(MentorDetails.class).getRegStudents();
////                                    if(regs==null){
//
////                                    }
//                                }
//
//                                @Override
//                                public void onCancelled(@NonNull DatabaseError error) {
//
//                                }
//                            });
////                            regs.add(m);
//                            childUpdates.put("regStudents", m);
//                            mentorsRef.child(forMentors.get(i).second.second).setValue(childUpdates);
//                        }
//
////                        mentorsRef.addListenerForSingleValueEvent(new ValueEventListener() {
////                            @Override
////                            public void onDataChange(@NonNull DataSnapshot snapshot) {
////                                for(DataSnapshot child: snapshot.getChildren()){
////                                    for(int i=0;i<forMentors.size();i++){
////                                        if(forMentors.get(i).second.second.equals(child.getKey())){
////                                                MentorDetails mentor = child.getValue(MentorDetails.class);
////
////                                                ArrayList<String> classes = mentor.getClasses();
////                                                ArrayList<String> prefLangs = mentor.getPrefLangs();
////                                                ArrayList<String> teachSubjects = mentor.getTeachSubjects();
////                                                ArrayList<String> timeSlots = mentor.getTimeSlots();
////                                                ArrayList<Map<String, String>> regStudents = mentor.getRegStudents();
////                                                if (regStudents == null) {
////                                                    regStudents = new ArrayList<>();
////                                                }
////
////                                                String name = mentor.getName();
////                                                String email = mentor.getEmail();
////                                                String phone = mentor.getPhone();
////
////                                                Map<String,String> m=new HashMap<>();
////                                                m.put(forMentors.get(i).first, forMentors.get(i).second.first);
////
////                                                regStudents.add(m);
////
////                                                mentorsRef.child(child.getKey()).child("name").setValue(name);
////                                                mentorsRef.child(child.getKey()).child("phone").setValue(phone);
////                                                mentorsRef.child(child.getKey()).child("email").setValue(email);
////                                                mentorsRef.child(child.getKey()).child("name").setValue(name);
////                                                mentorsRef.child(child.getKey()).child("classes").setValue(classes);
////                                                mentorsRef.child(child.getKey()).child("prefLangs").setValue(prefLangs);
////                                                mentorsRef.child(child.getKey()).child("teachSubjects").setValue(teachSubjects);
////                                                mentorsRef.child(child.getKey()).child("regStudents").setValue(regStudents);
////                                                mentorsRef.child(child.getKey()).child("timeSlots").setValue(timeSlots);
////
////                                        }
////                                    }
////                                }
////                            }
////
////                            @Override
////                            public void onCancelled(@NonNull DatabaseError error) {
////
////                            }
////                        });
//
//
//                }
//                else{
//                    x=true;
//                }
//
//
//                Intent i = new Intent(AuthSignupStudents2.this, MyMentors.class);
//                i.putExtra("noMentorsAssignedHere",x);
//                i.putExtra("userid",key);
//                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(i);
//
//            }
//
//
//        });
            }

            public void assignMentor(String key, DatabaseReference newStudentRef, DatabaseReference mentorsRef, ArrayList<Pair<Integer, Pair<String, String>>> mentors) {
                Collections.sort(mentors, new Comparator<Pair<Integer, Pair<String, String>>>() {
                    @Override
                    public int compare(Pair<Integer, Pair<String, String>> p1, Pair<Integer, Pair<String, String>> p2) {
                        return p1.first - p2.first;
                    }
                });
                ArrayList<String> tempSub1 = new ArrayList<>();
                tempSub1.addAll(subjects);
                for (int i = 0; i < mentors.size(); i++) {
                    if (tempSub1.size() == 0) {
                        break;
                    }
                    tempSub1.remove(mentors.get(i).second.second);
                }
                ArrayList<String> tempSub = new ArrayList<>();
                tempSub.addAll(subjects);
                UserDetails userSchema = new UserDetails(name, email, phone, class_selected, language_selected, tempSub1, time_selected, regSub);
                newStudentRef.setValue(userSchema);
                DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
                ArrayList<Pair<String, String>> forMentors = new ArrayList<>();                 // <Mentorid, subject>
                for (int i = 0; i < mentors.size(); i++) {
                    if (tempSub.size() == 0) {
                        break;
                    }
                    usersRef.child(key).child("regSubjects").child(mentors.get(i).second.second).setValue(mentors.get(i).second.first);
                    tempSub.remove(mentors.get(i).second.second);
                    forMentors.add(Pair.create(mentors.get(i).second.first, mentors.get(i).second.second));
                }

                for(int i=0;i<forMentors.size();i++){
                    Log.e("forMentors: "+i,forMentors.get(i).first+" : "+forMentors.get(i).second);
                }

//                for(int i=0;i<forMentors.size();i++){
//                    x=false;
//                    HashMap<String,String> m=new HashMap<>();
//                    m.put(forMentors.get(i).second,"true");
//                    mentorsRef.child(forMentors.get(i).first.first).child("regStudents").child(forMentors.get(i).first.second).setValue(m);
//                }

//                for(it=0;it<forMentors.size();it++){
//                    Log.e("mentor",forMentors.get(it).first.first);
                mentorsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot child : snapshot.getChildren()) {
                            for (int j = 0; j < forMentors.size(); j++) {
                                if (forMentors.get(j).first.equals(child.getKey())) {
                                    Log.e("there", "there");
                                    MentorDetails details = child.getValue(MentorDetails.class);
//                                    Log.e("mentor details",details.getPhone());
//                                    Log.e("value",child.getValue().toString());
                                    if (details.getRegStudents() == null) {
                                        Log.e("here", "here");
                                        HashMap<String, ArrayList<String>> regStudents = new HashMap<>();
                                        ArrayList<String> temp=new ArrayList<>();
                                        temp.add(key);
                                        regStudents.put(forMentors.get(j).second,temp);
                                        mentorsRef.child(forMentors.get(j).first).child("regStudents").child(forMentors.get(j).second).setValue(temp);
                                    }
                                    else if (details.getRegStudents().get(forMentors.get(j).second)==null) {
//                                        Log.e("it is null here", "arr is null here");
//                                        HashMap<String, ArrayList<String>> t = details.getRegStudents();
//                                        ArrayList<String> x = new ArrayList<>();
//                                        x.add(forMentors.get(j).second);
//                                        t.put(forMentors.get(j).first.second, x);
//                                        mentorsRef.child(forMentors.get(j).first.first).child("regStudents").setValue(t);
                                        ArrayList<String> temp=new ArrayList<>();
                                        temp.add(key);
                                        mentorsRef.child(forMentors.get(j).first).child("regStudents").child(forMentors.get(j).second).setValue(temp);

                                    } else {
                                        ArrayList<String> temp=new ArrayList<>();
                                        for(String k:details.getRegStudents().get(forMentors.get(j).second)){
                                            Log.e("kdsfjaklsflksda",k);
                                        }
                                        temp.addAll(details.getRegStudents().get(forMentors.get(j).second));
                                        temp.add(key);
                                        mentorsRef.child(forMentors.get(j).first).child("regStudents").child(forMentors.get(j).second).setValue(temp);
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                Intent i=new Intent(AuthSignupStudents2.this,MyMentors.class);
                i.putExtra("noMentorsAssignedHere",x);
                i.putExtra("userid",key);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }

        });
    }
        }