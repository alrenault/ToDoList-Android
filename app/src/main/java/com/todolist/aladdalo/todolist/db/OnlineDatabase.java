package com.todolist.aladdalo.todolist.db;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orm.query.Select;
import com.todolist.aladdalo.todolist.ToDoListActivity;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

/**
 * Classe qui permet l'interaction avec la base de donnee distante firebase.
 * @author PITROU Adrien
 * @since 28/12/18
 * @version 1.0
 * */
public class OnlineDatabase{
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final ToDoListActivity act;

    /**
     * Constructeur qui prend en parametre le contexte de l'application
     * @param act le contexte de l'application
     * */
    public OnlineDatabase(ToDoListActivity act){
        this.act = act;
    }

    /**
     * Interface pour la recuperation de donnees depuis firebase
     * */
    public interface OnGetDataListener {
        public void onStart();
        public void onSuccess(DataSnapshot data);
        public void onFailed(DatabaseError databaseError);
    }

    /**
     * Interface pour la recuperation de reponse depuis firebase
     * */
    public interface OnResponseListener {
        public void onStart();
        public void onSuccess();
        public void onFailed(Exception error);
    }

    /**
     * Classe interne appelee lors de l'ajout d'un compte utilisateur sur firebase
     * */
    private class CreateUserListener implements OnCompleteListener<AuthResult> {

        OnResponseListener listener;

        CreateUserListener(OnResponseListener listener){
            this.listener = listener;
            listener.onStart();
        }

        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
                Log.v("aaa", "createUserWithEmail:success");
                listener.onSuccess();
            } else {
                Log.v("aaa", "createUserWithEmail:failure");
                listener.onFailed(task.getException());
            }
        }
    }

    /**
     * Classe interne appelee lors de l'authentification d'un utilisateur sur firebase
     * */
    private class SignInListener implements OnCompleteListener<AuthResult> {

        OnResponseListener listener;

        SignInListener(OnResponseListener listener){
            this.listener = listener;
            listener.onStart();
        }

        @Override
        public void onComplete(@NonNull com.google.android.gms.tasks.Task<AuthResult> task) {
            if (task.isSuccessful()) {
                listener.onSuccess();
            } else {
                listener.onFailed(task.getException());
            }
        }


    }

    /**
     * Classe interne appelee pour lire une valeur dans la bdd firebase
     * */
    private class ReadListener<T> implements ValueEventListener {

        OnGetDataListener listener;

        ReadListener(OnGetDataListener listener){
            this.listener = listener;
            listener.onStart();
        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            listener.onSuccess(dataSnapshot);
        }

        @Override
        public void onCancelled(DatabaseError error) {
            listener.onFailed(error);
        }


    }

    /**
     * Cree un nouvel utilisateur dans la bdd firebase
     * @param email l'email de l'utilisateur qui servira aussi d'identifiant
     * @param password le mot de passe a utiliser
     * @return FirebaseUser user l'utilisateur cree
     * */
    public void createUser(String email, String password, OnResponseListener listener){

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new CreateUserListener(listener));
    }

    /**
     * Authentifie l'utilisateur
     * @param email l'email de l'utilisateur qui servira aussi d'identifiant
     * @param password le mot de passe a utiliser
     * @return boolean signIn authentifie ou non
     * */
    public void signIn(String email, String password, OnResponseListener listener){
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new SignInListener(listener));
    }

    /**
     * Ecrit une donnee dans la bdd
     * @param value la valeur a sauvegarder
     * @param ref la reference en bdd
     * */
    public<T> void writeData(T value, String ref){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(ref);

        myRef.setValue(value);
    }

    /**
     * Lit une donnee en bdd
     * @param ref la reference en bdd
     * @param listener le callback pour executer du code a la reception des infos de firebase
     * @return T val la valeur stockee en bdd
     * */
    public void readData(String ref, OnGetDataListener listener){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference(ref);

        myRef.addValueEventListener(new ReadListener<List<com.todolist.aladdalo.todolist.db.Task>>(listener));
    }


    /**
     * Sauvegarde les taches en bdd
     * */
    public void writeTasks(){
        List<com.todolist.aladdalo.todolist.db.Task> tasks = Select.from(com.todolist.aladdalo.todolist.db.Task.class)
                .list();
        writeData(tasks, "Tasks");
    }

    /**
     * Recupere les taches en bdd
     * @return List<com.todolist.aladdalo.todolist.db.Task> tasks les taches enregistrees
     * */
    public void readTasks(OnGetDataListener listener){
        readData("Tasks", listener);
    }

    /**
     * Fait la MAJ des tâches en recuperant les taches de la bdd distante qui ne sont pas presentes
     * dans la bdd locale.
     * */
    public void fetchTasks(){
        readTasks(new OnGetDataListener() {
            @Override
            public void onStart() {
                Log.v("aaa", "start fetchTasks");
            }

            @Override
            public void onSuccess(DataSnapshot data) {
                Log.v("aaa", "recupere taches avec succes -> fetch");
                List<com.todolist.aladdalo.todolist.db.Task> tasksLocal = Select.from(com.todolist.aladdalo.todolist.db.Task.class)
                        .list();
                List<com.todolist.aladdalo.todolist.db.Task> tasksDistant = new ArrayList<com.todolist.aladdalo.todolist.db.Task>();
                Log.v("aaa","dataSnapshoot : "+data);
                try {

                    for (DataSnapshot userSnapshot : data.getChildren()) {
                        String texte = ""+userSnapshot.toString();
                        String[] infos = texte.split("\\{");
                        //Scanner sc = new Scanner(infos[2]);
                        String[] datas = infos[2].split("=");
                        int time = Integer.parseInt(datas[1].split(",")[0]);
                        int progress = Integer.parseInt(datas[2].split(",")[0]);
                        //int id = Integer.parseInt(datas[3].split(",")[0]);
                        boolean alarme = Boolean.parseBoolean(datas[4].split(",")[0]);
                        int date = Integer.parseInt(datas[5].split(",")[0]);
                        //String timeString = datas[6].split(",")[0];
                        int priority = Integer.parseInt(datas[7].split(",")[0]);
                        String taskName = datas[8].split(",")[0];
                        //String dateString = datas[9].split(",")[0];

                        //com.todolist.aladdalo.todolist.db.Task r = userSnapshot.getValue(com.todolist.aladdalo.todolist.db.Task.class);
                        tasksDistant.add(new com.todolist.aladdalo.todolist.db.Task(taskName, date, time, progress, alarme, priority));
                        //tasksDistant.add(userSnapshot.getValue(com.todolist.aladdalo.todolist.db.Task.class));
                    }
                }catch(Exception e){
                    Log.v("aaa","erreur  : "+e.getMessage());
                    e.printStackTrace();
                }
                //List<com.todolist.aladdalo.todolist.db.Task> tasksDistant = bullet.
                Log.v("aaa","for taskDistant : "+tasksDistant);
                for(com.todolist.aladdalo.todolist.db.Task i : tasksDistant){
                    if(!tasksLocal.contains(i)){
                        i.save();
                    }
                }
                act.refreshList();
            }

            @Override
            public void onFailed(DatabaseError databaseError) {
                Log.v("aaa", "fetch erreor : "+databaseError.getMessage());
            }
        });
    }

    /**
     * Teste si un utilisateur courant est enregistré en ligne ou non.
     * @return true si un utilisateur est loggé et false sinon
     * */
    public boolean isRegisteredOnline(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            return true;
        } else {
            // No user is signed in
            return false;
        }
    }

    @Override
    public String toString() { return "OnlineDatabase mAuth=" + mAuth; }
}
