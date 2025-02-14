package com.example.madapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private ConstraintLayout upperPart;
    private ConstraintLayout lowerPart;
    private NavController navController;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FirebaseAuth firebaseAuth;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        firebaseAuth = FirebaseAuth.getInstance();
        getChildFragmentManager().beginTransaction()
                .replace(R.id.lowerpart, new NewsFragment())
                .commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        TextView usernameText = view.findViewById(R.id.textView5);
        CircleImageView circleImageView = view.findViewById(R.id.imageView7);

        FirebaseUser user = firebaseAuth.getCurrentUser();

        // You can now store additional user information in the Firebase Realtime Database
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String username = String.valueOf(snapshot.child("username").getValue());
                    String profilePicUri = snapshot.child("profilePic").getValue(String.class);
                    usernameText.setText(username);
                    // Load and display the profile image using Glide
                    if (profilePicUri != null && !profilePicUri.isEmpty() && isAdded()) {
                        Glide.with(requireContext())
                                .load(Uri.parse(profilePicUri))
                                .into(circleImageView);
                    } else {
                        // If there's no profile picture, you might want to set a default image here
                        circleImageView.setImageResource(R.drawable.profile);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            }


        // Find button IDs
        ImageButton newsButton = view.findViewById(R.id.news);
        ImageButton programmesButton = view.findViewById(R.id.programmes);
        ImageButton organizationButton = view.findViewById(R.id.organization);

        // Initialize upper and lower parts
        upperPart = view.findViewById(R.id.upperpart);
        lowerPart = view.findViewById(R.id.lowerpart);

        // Set OnClickListener for each button
        newsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Replace the lower part with the NewsFragment
                getChildFragmentManager().beginTransaction()
                        .replace(R.id.lowerpart, new NewsFragment())
                        .commit();
            }
        });

        programmesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Replace the lower part with the ProgrammesFragment
                getChildFragmentManager().beginTransaction()
                        .replace(R.id.lowerpart, new ProgrammesFragment())
                        .commit();
            }
        });

        organizationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Replace the lower part with the OrganizationsFragment
                getChildFragmentManager().beginTransaction()
                        .replace(R.id.lowerpart, new OrganizationFragment())
                        .commit();
            }
        });

        return view;
    }

    private void showLogoutConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Confirm Logout");
        builder.setMessage("Are you sure you want to log out?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                logoutUser();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing, simply close the dialog
                dialog.dismiss();
            }
        });
        builder.show();
    }
    private void logoutUser() {

        Intent intent = new Intent(getActivity(), LoginFragment.class);
        startActivity(intent);
    }
}

