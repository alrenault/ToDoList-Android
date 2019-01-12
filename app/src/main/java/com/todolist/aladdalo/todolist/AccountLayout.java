package com.todolist.aladdalo.todolist;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.todolist.aladdalo.todolist.db.Account;
import com.todolist.aladdalo.todolist.db.AccountLauncher;
import com.todolist.aladdalo.todolist.db.OnlineDatabase;
import com.todolist.aladdalo.todolist.db.Task;

public class AccountLayout {

    private AccountLayout(Context context){} //ininstanciable


    public static LinearLayout createAccountLayout(Context context, String username, String mdp){
        /*---------------------------------------
        Crée le layout pour l' authentification
        ----------------------------------------*/

        /*username*/
        final EditText usernameEditText = new EditText(context);
        usernameEditText.setHint(R.string.desc_tache);
        usernameEditText.setText(username);

        /*mdp*/
        final EditText mdpEditText = new EditText(context);
        mdpEditText.setHint(R.string.desc_tache);
        mdpEditText.setText(mdp);

        //Layout pour organiser l'AlerteDialog
        final LinearLayout linearLayout = new LinearLayout(context);

        linearLayout.addView(usernameEditText);
        linearLayout.addView(mdpEditText);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        /*Définir la bonne taille pour le champ usernameEditText*/
        ViewGroup.LayoutParams params = usernameEditText.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        usernameEditText.setLayoutParams(params);

        /*Définir la bonne taille pour le champ mdpEditText*/
        ViewGroup.LayoutParams params2 = mdpEditText.getLayoutParams();
        params2.width = ViewGroup.LayoutParams.MATCH_PARENT;
        mdpEditText.setLayoutParams(params2);

        /*---------------------------------------
             Fin de la création du layout
        ----------------------------------------*/

        return linearLayout;
    }


    /**Créé et gère l'AlertDialog lors de la création de tâche**/
    public static void addnewaccount(final ToDoListActivity context, String username, String mdp){

        final LinearLayout linearLayout = createAccountLayout(context, username, mdp);
        /*---------------------------------------
        Crée l'AlertDialog pour la création de tâche
        ----------------------------------------*/

        //Creation du AlertDialog
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(R.string.ajout_compte)
                .setMessage(R.string.faire_ensuite)
                .setView(linearLayout)
                .setPositiveButton(R.string.ajouter, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int progress = 0;
                        EditText usernameLayout = (EditText) linearLayout.getChildAt(0);
                        EditText mdpLayout = (EditText) linearLayout.getChildAt(0);

                        if(!usernameLayout.getText().toString().equals("") && !mdpLayout.getText().toString().equals("")) {//si intitule des 2 edit text est non-vide

                            Account currentAccount = AccountLauncher.getCurrentAccount();
                            if (currentAccount != null) {//desactive le compte courant si deja authentifie
                                context.refreshIcon(!AccountLauncher.getCurrentAccount().isActive());

                            } else {//authentification
                                String username = String.valueOf(usernameLayout.getText());
                                String mdp = String.valueOf(usernameLayout.getText());

                                /*AccountLauncher.authenticate(context, username, mdp, new AccountLauncher.OnGetDataListener() {
                                    @Override
                                    public void onStart() {
                                        Log.v("aaa", "Demarrage authentication");
                                    }

                                    @Override
                                    public void onFailed(Exception error) {
                                        Log.v("aaa", "Erreur authentication : "+error.getMessage());
                                    }

                                    @Override
                                    public void onAddingDatabase(Account account) {
                                        Log.v("aaa","compte cree avec succes en bdd -> "+account);
                                        context.refreshIcon(AccountLauncher.getCurrentAccount().isActive());
                                    }

                                    @Override
                                    public void onAddingPhone(android.accounts.Account account) {
                                        Log.v("aaa","compte cree avec succes sur tel -> "+account);
                                    }

                                    @Override
                                    public void onFailure() {
                                        Log.v("aaa", "echec ajout");
                                    }
                                });*/
                                OnlineDatabase o = new OnlineDatabase(context);
                                o.test(username, mdp);
                            }
                        }
                    }
                })
                .setNegativeButton(R.string.annuler, null)
                .create();

        dialog.show();
    }
}
