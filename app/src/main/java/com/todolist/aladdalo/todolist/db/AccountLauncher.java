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
import com.todolist.aladdalo.todolist.ToDoListActivity;

import java.util.List;

/**
 * Classe qui encapsule les methodes de gestion et de recuperation de compte
 * */
public class AccountLauncher{

    private static Account currentAccount;
    private static com.todolist.aladdalo.todolist.db.Account currentAccount2;

    private AccountLauncher(){} //ininstanciable

    public static boolean isAccount(Account[] accounts, Account account){
        for(Account i : accounts){
            if( i.name == account.name && i.type == account.type){
                return true;
            }
        }
        return false;
    }


    public static com.todolist.aladdalo.todolist.db.Account getCurrentAccount(){
        return currentAccount2;
    }

    public static void authenticate(Context context, String accountName, String password, OnGetDataListener listener){
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
    };

    public static void disableAllAccount(ToDoListActivity context){
        List<com.todolist.aladdalo.todolist.db.Account> accountRecupered = Select.from(com.todolist.aladdalo.todolist.db.Account.class).list();
        for(com.todolist.aladdalo.todolist.db.Account i : accountRecupered){
            if(i.isActive()){
                i.setActive(false);
            }
        }
        //context.refreshIcon(accountRecupered.get(0));
    }

    /**
     * Interface pour la recuperation de donnees depuis firebase
     * */
    public interface OnGetDataListener {
        public void onStart();
        public void onAddingDatabase(com.todolist.aladdalo.todolist.db.Account account);
        public void onAddingPhone(Account account);
        public void onFailure();
        public void onFailed(Exception error);
    }

    private static class RegisterAsync extends  AsyncTask {


        private AccountManager am;
        private String accountName;
        private String accountType;
        private String password;
        private String[] requiredFeatures;
        private Bundle options;
        private OnGetDataListener listener;
        private Context context;

        public RegisterAsync(AccountManager am, String accountName, String accountType, String password, OnGetDataListener listener, Context context) {
            this.am = am;
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
            currentAccount = new Account(accountName, accountType);
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            try{
                Account[] accounts = am.getAccounts();
                //am.addAccount("Base",null,null,null,null,null,null);
                if (am.addAccountExplicitly(currentAccount, password, null)) {
                    listener.onAddingPhone(currentAccount);

                    currentAccount2 = new com.todolist.aladdalo.todolist.db.Account(accountName, password);
                    currentAccount2.save();
                    listener.onAddingDatabase(currentAccount2);

                }else if(isAccount(accounts, currentAccount)){//compte déjà présent
                    List<com.todolist.aladdalo.todolist.db.Account> accountRecupered = Select.from(com.todolist.aladdalo.todolist.db.Account.class)
                            .where(Condition.prop("username").eq(accountName))
                            .where(Condition.prop("mdp").eq(password))
                            .list();
                    listener.onAddingDatabase(accountRecupered.get(0));
                }else {
                    listener.onFailure();
                }

            }catch(Exception e) {
                listener.onFailed(e);
            }
        }
    }
}
