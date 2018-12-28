package com.todolist.aladdalo.todolist.db;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Classe qui encapsule les methodes de gestion et de recuperation de compte
 * */
public class AccountLauncher{

    private AccountLauncher(){} //ininstanciable

    public static boolean isAccount(){return false;}

    public static Task[] getTasksByAccount(){return null;}

    public static boolean authenticate(Context context){
        final String ACCOUNT_TYPE = "ccc";
        Bundle userdata = new Bundle();
        userdata.putString("SERVER", "extra");

        Log.v("aaa", "authenticate");
        /*AccountManager am = AccountManager.get(context);

        final Account account = new Account("test",  ACCOUNT_TYPE);
        Log.v("aaa", "authenticate");
        am.addAccountExplicitly(account, "testPwd", userdata);
        Log.v("aaa", "authenticate");*/

        //AccountAuthenticator aa = new AccountAuthenticator(context);
        //Log.v("aaa", "AccountAuthenticator = "+aa);

        AccountManager am = AccountManager.get(context);
        Log.v("aaa", "AccountManager = "+am);
        String authority = "testAuthority";
        String accountType = ACCOUNT_TYPE;
        String accountName = "testDanyCompte";

        Account account = new Account(accountName, accountType);

        Log.v("aaa", "NomCompte = "+account.name+" TypeCompte : "+account.type+" "+account.describeContents());

        new RegisterAsync(am, accountName, accountType, "psw", null, null).execute();

        return false;
    };

    private static class RegisterAsync extends  AsyncTask {


        private AccountManager am;
        private String accountName;
        private String accountType;
        private String password;
        private String[] requiredFeatures;
        private Bundle options;
        private Account account;

        public RegisterAsync(AccountManager am, String accountName, String accountType, String password, String[] requiredFeatures, Bundle options) {
            this.am = am;
            this.accountName = accountName;
            this.accountType = accountType;
            this.password = password;
            this.requiredFeatures = requiredFeatures;
            this.options = options;
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            account = new Account(accountName, accountType);
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            try{
                //am.addAccount("Base",null,null,null,null,null,null);
                if (am.addAccountExplicitly(account, password, null)) {
                    Log.v("aaa", "Success !!!");
                }
                Account[] existingAccs = am.getAccountsByType(accountType);
                if (existingAccs.length > 0) {
                    Log.v("aaa", "existing -> "+existingAccs[0]+" "+existingAccs.length);
                }
                final Bundle bundle = am.getAuthToken(account,accountType,null,null,null,null).getResult();
                Log.v("aaa", "getAuthToken -> "+bundle.getString(AccountManager.KEY_AUTHTOKEN));
            }catch(Exception e) {
                e.printStackTrace();
                Log.v("aaa", "Echec : "+e.getMessage());
            }
        }
    }
}
