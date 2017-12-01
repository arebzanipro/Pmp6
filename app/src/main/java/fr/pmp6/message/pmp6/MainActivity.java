package fr.pmp6.message.pmp6;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.security.GeneralSecurityException;

import fr.pmp6.message.pmp6.fr.pmp6.message.utils.AESCrypt;

public class MainActivity extends Activity {
    private int SIGN_IN_REQUEST_CODE = 1;
    private FirebaseListAdapter<ChatMessage> adapter;
    private static final String pass = "HELLO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            startActivityForResult(
                    AuthUI.getInstance().createSignInIntentBuilder().build(),
                    SIGN_IN_REQUEST_CODE
            );
        } else {
            Toast.makeText(this, "Welcome "+FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), Toast.LENGTH_LONG).show();
            displayChatMessages();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText input = (EditText) findViewById(R.id.input);
                String encryptedMessage = null;
                try {

                    encryptedMessage = AESCrypt.encrypt(String.valueOf(R.string.aes_pass),input.getText().toString());
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }
                Log.e("TEST", encryptedMessage);
                FirebaseDatabase.getInstance()
                        .getReference()
                        .push().setValue(new ChatMessage(
                        encryptedMessage,
                        //input.getText().toString(),
                        FirebaseAuth.getInstance()
                        .getCurrentUser()
                        .getDisplayName())
                );
                input.setText("");
            }
        });

    }

    private void displayChatMessages(){
        ListView listOfMessages = (ListView) findViewById(R.id.list_of_messages);
        adapter = new FirebaseListAdapter<ChatMessage>(this, ChatMessage.class,
                R.layout.message, FirebaseDatabase.getInstance().getReference()) {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                TextView messageText = (TextView) v.findViewById(R.id.message_text);
                TextView messageUser = (TextView) v.findViewById(R.id.message_user);
                TextView messageTime = (TextView) v.findViewById(R.id.message_time);
                String decryptedMessage = null;
                try {
                    decryptedMessage = AESCrypt.decrypt(String.valueOf(R.string.aes_pass),model.getMessageText());
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }

                messageText.setText(decryptedMessage);
                //messageText.setText(model.getMessageText());
                messageUser.setText(model.getMessageUSer());
                messageTime.setText(android.text.format.DateFormat.format("dd-MM-yyy (HH:mm:ss)",
                        model.getMessageTime())
                );
            }
        };
        listOfMessages.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(resultCode, resultCode, data);
        if(requestCode == SIGN_IN_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                Toast.makeText(this, "Bonjour", Toast.LENGTH_LONG).show();
                displayChatMessages();
            } else {
                Toast.makeText(this, "Impossible de se connecter ", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    @Override
    public  boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == R.id.menu_sign_out){
            AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(MainActivity.this,  "Deconnecté ", Toast.LENGTH_LONG).show();
                    finish();
                }
            });
        }
        return true;
    }
}
