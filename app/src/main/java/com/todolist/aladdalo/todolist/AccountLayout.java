package com.todolist.aladdalo.todolist;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.orm.query.Condition;
import com.orm.query.Select;
import com.todolist.aladdalo.todolist.db.Account;
import com.todolist.aladdalo.todolist.db.AccountLauncher;
import com.todolist.aladdalo.todolist.db.OnlineDatabase;
import com.todolist.aladdalo.todolist.db.Task;

import java.util.List;

/**
 * Classe statique qui encapsule des fonctions utiles a la creation des vues pour l'authentification et
 * les fonctions qui ouvrent les AlertDialog
 * */
public class AccountLayout {
    private static String ACCOUNT_TYPE = "ccc";

    /**
     * Constructeur ininstanciable
     * @param context le contexte de l'application
     * */
    private AccountLayout(Context context){} //ininstanciable

    /**
     * Cree le layout de creation de compte pour l'authentification
     * @param context le contexte de l'application
     * @return linearlayout le layout contenant toute la vue
     * */
    public static LinearLayout createAccountLayout(Context context){
        return createAccountLayout(context, "", "");
    }

    /**
     * Cree le layout de creation de compte pour l'authentification
     * @param context le contexte de l'application
     * @param username le nom d'utilisateur de base
     * @param mdp le mdp de base
     * @return linearlayout le layout contenant toute la vue
     * */
    public static LinearLayout createAccountLayout(Context context, String username, String mdp){

        /*username*/
        final EditText usernameEditText = new EditText(context);
        usernameEditText.setHint(R.string.username);
        usernameEditText.setText(username);

        /*mdp*/
        final EditText mdpEditText = new EditText(context);
        mdpEditText.setHint(R.string.password);
        mdpEditText.setText(mdp);
        mdpEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

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

        return linearLayout;
    }

    /**
     * Cree le layout de selection de compte pour l'authentification
     * @param context le contexte de l'application
     * @return linearlayout le layout contenant toute la vue
     * */
    private static LinearLayout createSelectLayout(ToDoListActivity context) {
        int counter = 0;

        /*box*/
        final RadioGroup box = new RadioGroup(context);
        LinearLayout textes = new LinearLayout(context);
        box.setOrientation(LinearLayout.VERTICAL);
        textes.setOrientation(LinearLayout.VERTICAL);

        android.accounts.Account[] telAccounts = AccountLauncher.getPhoneAccounts(context);


        //Pour aligner RadioBox avec le texte
        LinearLayout.LayoutParams layoutParamsTxt = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParamsTxt.setMargins(0,10,0,0);

        for(android.accounts.Account account : telAccounts){
            LinearLayout layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.HORIZONTAL);

            TextView textName = new TextView(context);
            textName.setText(account.name.substring(10));
            layout.addView(textName);

            TextView textType = new TextView(context);
            textType.setText(account.type);
            layout.addView(textType);

            RadioButton bouton = new RadioButton(context);
            bouton.setId(counter);
            if(counter == 0){
                bouton.setChecked(true);
            }
            counter++;


            box.addView(bouton);
            textes.addView(layout,layoutParamsTxt);
        }

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.addView(box);
        linearLayout.addView(textes);

        return linearLayout;
    }


    /**
     * Vérifie si avec les comptes du telephone on peux s'authentifier. Sinon, propose de s'authentifier avec
     * un compte déjà présent ou un nouveau compte.
     * @param context le contexte de l'application
     * */
    public static void checkPhoneAccounts(final ToDoListActivity context){
        //Joue des commandes différentes si l'utilisateur est authentifié ou non
        if(AccountLauncher.isAuthenticated()){
            AccountLauncher.unauthenticate(context);
        }else{
            final LinearLayout linearLayout = createSelectLayout(context);
            Log.v("aaa","linearLayout : "+linearLayout);

            AlertDialog dialog = new AlertDialog.Builder(context)
                    .setTitle(R.string.authentification)
                    .setMessage(R.string.message_auth)
                    .setView(linearLayout)
                    .setPositiveButton(R.string.ajouter, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(AccountLauncher.getPhoneAccounts(context).length == 0){
                                Log.v("aaa", "addnewaccount");
                                addnewaccount(context, "", "");
                            }else {
                                LinearLayout textes = (LinearLayout) linearLayout.getChildAt(1);
                                RadioGroup group = (RadioGroup) linearLayout.getChildAt(0);

                                //Log.v("aaa","group : "+group);
                                int radioButtonID = group.getCheckedRadioButtonId();
                                //Log.v("aaa","id : "+radioButtonID);
                                LinearLayout layout = (LinearLayout) textes.getChildAt(radioButtonID);
                                //Log.v("aaa","layout : "+layout);

                                final String textNom = ((TextView) layout.getChildAt(0)).getText().toString();
                                final String textType = ((TextView) layout.getChildAt(1)).getText().toString();

                                Log.v("aaa", "fin : nom= " + textNom + " type=" + textType);

                                AccountLauncher.auth(context, new android.accounts.Account(textNom, textType), new OnlineDatabase.OnResponseListener() {
                                    @Override
                                    public void onStart() {

                                    }

                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onFailed(Exception error) {
                                        addnewaccount(context, textNom, textType);
                                    }
                                });
                            }
                        }
                    })
                    .setNegativeButton(R.string.annuler, null)
                    .create();

            dialog.show();
        }
    }

    /**
     * Ajoute les infos de compte sur le telephone
     * @param context le contexte de l'application
     * @param baseUsername baseUsername le nom d'utilisateur de base
     **/
    public static void addnewaccount(final ToDoListActivity context, String baseUsername){
        addnewaccount(context, baseUsername, "");
    }

    /**
     * Ajoute les infos de compte sur le telephone
     * @param context le contexte de l'application
     * @param baseUsername baseUsername le nom d'utilisateur de base
     * @param baseMdp baseMdp le mdp de base
     **/
    public static void addnewaccount(final ToDoListActivity context, final String baseUsername, final String baseMdp){

        final LinearLayout linearLayout = createAccountLayout(context, baseUsername, baseMdp);
        /*---------------------------------------
        Crée l'AlertDialog pour la création de tâche
        ----------------------------------------*/

        //Creation du AlertDialog
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(R.string.ajout_compte)
                .setMessage(R.string.message_new_account)
                .setView(linearLayout)
                .setPositiveButton(R.string.ajouter, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.v("aaa","recuperation des infos pour addnewaccount");
                        final String username = ((EditText) linearLayout.getChildAt(0)).getText().toString();
                        final String mdp = ((EditText) linearLayout.getChildAt(1)).getText().toString();
                        Log.v("aaa","fin recuperation des infos pour addnewaccount");
                        if(!username.equals("") && !mdp.equals("")) { //si intitule des 2 edit text est non-vide

                            if (Select.from(com.todolist.aladdalo.todolist.db.Account.class)
                                    .where(Condition.prop("username").eq(username))
                                    .where(Condition.prop("mdp").eq(mdp))
                                    .list().size() == 0) {
                                //cree un compte en bdd si le compte est deja cree sur tel
                                Log.v("aaa", "isnotAccount");
                                Account dbAccount = new Account(username, mdp);
                                dbAccount.save();
                            }
                            if (!AccountLauncher.isAuthenticated()) {
                                //creer un compte sur le telephone
                                Log.v("aaa", "isnotauthenticated");
                                new AccountLauncher.RegisterAsync(context, username, ACCOUNT_TYPE, mdp, new AccountLauncher.OnGetDataListener() {
                                    @Override
                                    public void onStart() {
                                        Log.v("aaa", "onStart RegisterAsync");
                                    }

                                    @Override
                                    public void onFailure(Exception e) {
                                        Log.v("aaa", "Failure : " + e.getMessage());
                                    }

                                    @Override
                                    public void onSuccess() {
                                        Log.v("aaa", "Success ");
                                    }

                                    @Override
                                    public void onExisting() {
                                        Log.v("aaa", "Existing already ");
                                    }
                                }).execute();
                            }
                            final OnlineDatabase o = new OnlineDatabase(context);
                            o.createUser(username, mdp, new OnlineDatabase.OnResponseListener() {
                                @Override
                                public void onStart() {
                                    Log.v("aaa", "startingCreateUser");
                                }

                                @Override
                                public void onSuccess() {
                                    Log.v("aaa", "success");
                                    o.signIn(username, mdp, new OnlineDatabase.OnResponseListener() {
                                        @Override
                                        public void onStart() {
                                            Log.v("aaa", "startingSignIn");
                                        }

                                        @Override
                                        public void onSuccess() {
                                            Log.v("aaa", "successLogin");
                                            context.refreshIcon(true);
                                        }

                                        @Override
                                        public void onFailed(Exception error) {
                                            Log.v("aaa", "signIn failed : " + error.getMessage());
                                        }
                                    });
                                }

                                @Override
                                public void onFailed(Exception error) {
                                    Log.v("aaa", "failed : " + error.getMessage());
                                }
                            });
                        }
                    }
                })
                .setNegativeButton(R.string.annuler, null)
                .create();

        dialog.show();
    }
}

