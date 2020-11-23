package com.example.buckit;

import android.app.Activity;
import android.app.Dialog;
//import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class PostActivity extends AppCompatActivity implements View.OnClickListener{

//    Context context = this;
    //EditText input;
    //Button post_advice_button;
    //private Dialog popup;

//    final AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
//    LayoutInflater inflater = this.getLayoutInflater();
//    View dialogView = inflater.inflate(R.layout.custom_dialog, null);

    //final EditText editText = (EditText) findViewById(R.id.edit_advice);
//    Button button1 = (Button) dialogView.findViewById(R.id.submit_advice_button);
//    private Button friendButton = (Button) dialogView.findViewById(R.id.friend_button);
    private Button friendButton;
    private Button sendFriendRequestButton;
    private Button cookingButton;
    private Button submitAdvice;
    //private ImageButton searchButton;
     TextView messageTextView;


    private Dialog popup;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addusers1);
        popup = new Dialog(this);


        friendButton = (Button) findViewById(R.id.friend_button);
        sendFriendRequestButton = (Button) findViewById(R.id.send_friend_request_button);
        cookingButton = (Button) findViewById(R.id.cooking_button);
        submitAdvice = (Button) findViewById(R.id.submit_advice_button);

//        searchButton = (ImageButton) findViewById(R.id.search_button);
//
//        friendButton.setOnClickListener(this);
//        sendFriendRequestButton.setOnClickListener(this);
//        cookingButton.setOnClickListener(this);
        //submitAdvice.setOnClickListener(this);
    }



    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.search_button) {
            this.setContentView(R.layout.activity_addusers2);
        }
         else if (v.getId() == R.id.friend_button) {
            setContentView(R.layout.activity_addusers3);
        }
        else if (v.getId() == R.id.send_friend_request_button) {
            Toast.makeText(this, "Friend request accepted.", Toast.LENGTH_SHORT).show();
            setContentView(R.layout.activity_addusers4);
        }
        else if (v.getId() == R.id.cooking_button) {
            setContentView(R.layout.activity_friend_list);
        }
        else if (v.getId() == R.id.post_advice_button) {
            adviceInput();
            setContentView(R.layout.activity_friend_list);
        }
//        else if (v.getId() == R.id.submit_advice_button) {
//            setContentView(R.layout.activity_friend_list2);
//        }
//        else if (v.getId() == R.id.register) {
//            Toast.makeText(this, "Redirecting to registration.", Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(this, RegisterActivity.class);
//            startActivity(intent);
//        }
//        friendButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                setContentView(R.layout.activity_addusers3);
//            }
//        });
    }

    public void adviceInput() {
        assert (popup != null);
        // Create the dialog that asks user to give advice
        popup.setContentView(R.layout.post_advice_popup);
        final EditText editText = (EditText) popup.findViewById(R.id.edit_advice);
        Button btnCreate = (Button) popup.findViewById(R.id.submit_advice_button);

        // By default, show the popup
        popup.show();

        //Once name list, create a new list
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String innerText = editText.getText().toString();

                // Make sure that the name for the list is valid
                if (innerText.length() > 0) {
                    //save this to string
                    //setContentView(messageTextView);
                    //messageTextView= (TextView) findViewById(R.id.userinputadvice);
                    //messageTextView.setText(innerText);
                    // Close the popup
                    //popup.setContentView(R.layout.activity_friend_list2);
                    popup.dismiss();
                    //setContentView(R.layout.activity_friend_list2);
                }
                if (v.getId() == R.id.submit_advice_button) {
                    adviceInput();
                    setContentView(R.layout.activity_friend_list2);
                }
                //setContentView(R.layout.activity_friend_list);
            }
        });

        //setContentView(R.layout.activity_friend_list); //set to one with the new advice

    }
//        button1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // DO SOMETHINGS
//                getAdvice();
//                //get text and call method that pass ess th etext and
//                dialogBuilder.dismiss();
//            }
//        });
//
//        dialogBuilder.setView(dialogView);
//        dialogBuilder.show();

//    public void adviceInput() {
//        assert (popup != null);
//        // Create the dialog that asks user to name their bucket
//        popup.setContentView(R.layout.post_advice_popup);
//        final EditText editText = (EditText) popup.findViewById(R.id.edit_advice);
//        Button btnCreate = (Button) popup.findViewById(R.id.submit_advice_button);
//
//        // By default, show the popup
//        popup.show();
//
//        // Once name list, create a new list
//        btnCreate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String innerText = editText.getText().toString();
//
//                // Make sure that the name for the list is valid
//                if (innerText.length() > 0) {
//                    // save this to string
//
//                    // Close the popup
//                    popup.dismiss();
//                }
//            }
//        });
//        setContentView(R.layout.activity_friend_list); //set to one with the new advice
//    }

        //input= (EditText) findViewById(R.id.advice_input);

        //post_advice_button.setOnClickListener(this);
//        post_advice_button.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View arg0) {
//
//                // get prompts.xml view
//                LayoutInflater li = LayoutInflater.from(context);
//                View promptsView = li.inflate(R.layout.post_advice_popup, null);
//
//                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
//                        context);
//
//                // set prompts.xml to alertdialog builder
//                alertDialogBuilder.setView(promptsView);
//
//                final EditText userInput = (EditText) promptsView
//                        .findViewById(R.id.editTextDialogUserInput);
//
//                // set dialog message
//                alertDialogBuilder
//                        .setCancelable(false)
//                        .setPositiveButton("OK",
//                                new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog,int id) {
//                                        // get user input and set it to result
//                                        // edit text
//                                        input.setText(userInput.getText());
//                                    }
//                                })
//                        .setNegativeButton("Cancel",
//                                new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog,int id) {
//                                        dialog.cancel();
//                                    }
//                                });
//
//                // create alert dialog
//                AlertDialog alertDialog = alertDialogBuilder.create();
//
//                // show it
//                alertDialog.show();
//
//            }
//        });




//    private void getAdvice() {
//        String advice = editText.getText().toString();
//    }
//calls a method that retuns container

//    AlertDialog.Builder alert = new AlertDialog.Builder(this);
//
//    alert.setTitle("Title");
//    alert.setMessage("Message");
//
//        // Set an EditText view to get user input
//        final EditText input = new EditText(this);
//    alert.setView(input);
//
//    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int whichButton) {
//
//                // Do something with value!
//            }
//        });
//
//    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int whichButton) {
//                // Canceled.
//            }
//        });
//
//    alert.show();

}

//    public void postAdvice(View view) {
////        String name =
//    }
//
//    @Override
//    public void onClick(View v) {
//        int myid = v.getId();
//
//        if (myid == R.id.post_advice_button) {
//            // Create the dialog that allows user to click on each
//            //popup.setContentView(R.layout.change_privacy_popup);
//            // By default, show the popup
//            //popup.show();
//        }
//
//    }
