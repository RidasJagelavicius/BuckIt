package com.example.buckit.ui.search;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.buckit.R;

import java.util.ArrayList;

public class SearchFragment extends Fragment implements View.OnClickListener {

    private SearchViewModel mViewModel;
    protected Context thisContext;
    private View root;
    private Button friendButton;
    private Button sendFriendRequestButton;
    private Button cookingButton;
    private Button openAdvicePopup;
    private Button submitAdvice;
    private ImageButton searchButton;
    TextView messageTextView;
    private EditText search_bar;
    private String username;
    private boolean isAdded = false;
    public ArrayList<String> added_users = new ArrayList();

    private Dialog popup;
    public void FirstFragment() {
        // Required empty public constructor
    }

    @Override
    // This and the method above enable creating new elements by replacing "this" with "thisContext"
    public void onAttach(Context context) {
        super.onAttach(context);
        thisContext=context;
    }

    public void onClick(View v) {
        if (v.getId() == R.id.search_button) {
            String input = search_bar.getText().toString();
            if (input.length() == 0) {
                Toast.makeText(thisContext, "Please enter a username", Toast.LENGTH_SHORT).show();
            } else {
                username = "@" + input;
                Intent intent = new Intent(thisContext, PostActivity2.class);
                Bundle b = new Bundle();
                b.putStringArrayList("added_users", added_users);
                intent.putExtras(b);
                intent.putExtra("username", username); // pass the dictionary to the list
                startActivity(intent);
            }
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mViewModel =
                ViewModelProviders.of(this).get(SearchViewModel.class);
        root = inflater.inflate(R.layout.activity_addusers1, container, false);
        super.onCreate(savedInstanceState);
        searchButton = root.findViewById(R.id.search_button);
        search_bar = root.findViewById(R.id.search_bar);
        searchButton.setOnClickListener(this);

        return root;
    }
}