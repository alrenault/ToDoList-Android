package com.todolist.aladdalo.todolist.db;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.orm.query.Condition;
import com.orm.query.Select;
import com.todolist.aladdalo.todolist.AccountLayout;
import com.todolist.aladdalo.todolist.ToDoListActivity;

import java.util.List;

/**
 * Classe qui encapsule les methodes de gestion et de recuperation de compte
 * */
public class AccountLauncher{

    private static Account currentAccount;
    private static com.todolist.aladdalo.todolist.db.Account currentAccount2;

    private AccountLauncher(){} //ininstanciable

    /**
     * Interface pour un callback asynchrone de RegisterAsync
     * */
    public interface OnGetDataListener{
        void onStart();
        void onFailure(Exception e);
        void onSuccess();
        void onExisting();
    }

    /**
     * Teste
     * */
    /*public static boolean isAccount(Account[] accounts, Account account){
        for(Account i : accounts){
            if( i.name == account.name && i.type == account.type){
                return true;
            }
        }
        return false;
    }*/

    /**
     * Renvoie le compte en base de donnees courrant
     * @return currentAccount le compte courant en bdd
     * */
    public static com.todolist.aladdalo.todolist.db.Account getCurrentAccount(){
        return currentAccount2;
    }

    /**
     * Verifier que le compte existe dans le téléphone ou non
     * @param context le contexte de l'application
     * @return Account account le 1er compte qui matche correctement null sinon
     * */
    public static Account verifyIfAccountExist(Context context){

        Account[] telAccount = getPhoneAccounts(context);
        List<com.todolist.aladdalo.todolist.db.Account> dbAccount = Select.from(com.todolist.aladdalo.todolist.db.Account.class)
                .list();
        for(com.todolist.aladdalo.todolist.db.Account account : dbAccount){
            for(Account account2 : telAccount){
                if(account2.name == account.getUsername()){
                    return account2;
                }
            }
        }
        return null;
    }

    /**
     * Donne les comptes du téléphone
     * @param context le contexte de l'application
     * @return la liste des comptes du téléphone
     * */
    public static Account[] getPhoneAccounts(Context context){
        AccountManager am = AccountManager.get(context);
        return am.getAccounts();
    }

    /**
     * Donne le compte en bdd correspondant au compte stocke sur le telephone
     * @param telAccount le compte stocke sur le telephone
     * @return dbAccount le compte correspondant en bdd ou null
     * */
    private static com.todolist.aladdalo.todolist.db.Account getCorrespondingAccount(Account telAccount){
        Log.v("aaa", "getCorrespondingAccount");
        List<com.todolist.aladdalo.todolist.db.Account> accountRecupered = Select.from(com.todolist.aladdalo.todolist.db.Account.class)
                .where(Condition.prop("username").eq(telAccount.name))
                .list();
        if(accountRecupered.size() != 0){
            return accountRecupered.get(0);
        }
        return null;
    }

    /**
     * Interface pour la recuperation de donnees depuis firebase
     *
    public interface OnGetDataListener {
        public void onStart();
        public void onAddingDatabase(com.todolist.aladdalo.todolist.db.Account account);
        public void onAddingPhone(Account account);
        public void onFailure();
        public void onFailed(Exception error);
    }*/


    /**
     * Authentifie a partir d'un compte de telephone
     * @param context le contexte de l'application
     * @param telAccount le compte stocke sur le telephone
     * @param listener le callback pour executer des instructions a la fin de l'appel a firebase
     * */
    public static void auth(final ToDoListActivity context, final Account telAccount, final OnlineDatabase.OnResponseListener listener){
        Log.v("aaa", "auth");
        final com.todolist.aladdalo.todolist.db.Account dbAccount = getCorrespondingAccount(telAccount);

        if(dbAccount == null) {
            Log.v("aaa", "creer nouveau compte");
            currentAccount = telAccount;
            AccountLayout.addnewaccount(context, telAccount.name);
        }else{
            Log.v("aaa", "dbAccount != null");
            final OnlineDatabase o = new OnlineDatabase(context);
            o.signIn(dbAccount.getUsername(), dbAccount.getMdp(), new OnlineDatabase.OnResponseListener() {
                @Override
                public void onStart() {
                    listener.onStart();
                }

                @Override
                public void onSuccess() {
                    //l'identite est connue de firebase
                    context.refreshIcon(true);
                    currentAccount = telAccount;
                    currentAccount2 = dbAccount;
                    listener.onSuccess();
                }

                @Override
                public void onFailed(Exception error) {
                    //l'identite n'existe pas sur firebase
                    listener.onFailed(error);
                }
            });
        }
        Log.v("aaa", "fin auth");
    }


    /*public static void authenticate(Context context, String accountName, String password, OnGetDataListener listener){
        final String ACCOUNT_TYPE = "ccc";
        //Bundle userdata = new Bundle();
        //userdata.putString("SERVER", "extra");

        Log.v("aaa", "authenticateAccountLauncher");

        AccountManager am = AccountManager.get(context);

        //String authority = "testAuthority";
        String accountType = ACCOUNT_TYPE;

        Account account = new Account(accountName, accountType);

        Log.v("aaa", "NomCompte = "+account.name+" TypeCompte : "+account.type+" "+account.describeContents());

        new RegisterAsync(am, accountName, accountType, password, listener, context).execute();
    };*/

    public static boolean isAuthenticated() {
        return currentAccount != null;
    }

    /**
    * Supprime tout les comptes sauf celui passe en parametres
    * */
    private static void clearDbAccountExcept(com.todolist.aladdalo.todolist.db.Account accountExcept){
        List<com.todolist.aladdalo.todolist.db.Account> accountRecupered = Select.from(com.todolist.aladdalo.todolist.db.Account.class).list();
        for(com.todolist.aladdalo.todolist.db.Account account : accountRecupered){
            if(!account.same(accountExcept)){
                account.delete();
            }
        }
    }

    /**
     * Supprime tout
     * */
    public static void clearAccounts(ToDoListActivity context) {
        Log.v("aaa", "clearAllAccounts");
        //supprime les comptes en bdd
        List<com.todolist.aladdalo.todolist.db.Account> accountRecupered = Select.from(com.todolist.aladdalo.todolist.db.Account.class).list();
        for(com.todolist.aladdalo.todolist.db.Account account : accountRecupered){
            account.delete();
        }

        //supprime les comptes sur le telephone
        AccountManager am = AccountManager.get(context);
        Account[] accounts = am.getAccounts();
        for(Account a : accounts){
            am.removeAccount(a, context, null, null);
        }
    }

    /**
     * Desauthentifie
     * @param context le contexte de l'application
     * */
    public static void unauthenticate(ToDoListActivity context) {
        context.refreshIcon(false);
        //clearDbAccountExcept(currentAccount2);
        currentAccount = null;
        currentAccount2 = null;
    }

    /**
     * Classe interne utilisee pour ajouter un compte explicitement au telephone
     * */
    public static class RegisterAsync extends  AsyncTask {
        private String accountName;
        private String accountType;
        private String password;
        private String[] requiredFeatures;
        private Bundle options;
        private OnGetDataListener listener;
        private Context context;

        /**
         * Le constructeur
         * @param context le contexte de l'application
         * @param accountName le nom de compte
         * @param accountType le type de compte
         * @param password le mot de passe du compte
         * @param listener le callback pour exectuter du code asynchrone a la fin de l'ajout
         * */
        public RegisterAsync(Context context, String accountName, String accountType, String password, OnGetDataListener listener) {
            this.accountName = accountName;
            this.accountType = accountType;
            this.password = password;
            this.requiredFeatures = requiredFeatures;
            this.options = options;
            this.listener = listener;
            this.context = context;
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            listener.onStart();
            AccountManager am = AccountManager.get(context);
            Account account = new Account(accountName, accountType);
            try{
                Account[] accounts = am.getAccounts();
                //am.addAccount("Base",null,null,null,null,null,null);
                if (am.addAccountExplicitly(account, password, null)) {
                    currentAccount = account;
                    listener.onSuccess();
                }else{//compte déjà présent
                    listener.onExisting();
                }

            }catch(Exception e) {
                listener.onFailure(e);
            }
            return null;
        }
    }
}
