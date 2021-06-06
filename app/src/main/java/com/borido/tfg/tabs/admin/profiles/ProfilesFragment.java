package com.borido.tfg.tabs.admin.profiles;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.borido.tfg.models.Book;
import com.borido.tfg.models.Library;
import com.borido.tfg.models.User;
import com.borido.tfg.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProfilesFragment extends Fragment {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference usersRef = database.getReference("users");

    private RecyclerView recyclerView;
    private EditText searchBar;
    private ProgressBar progressBar;

    private AdapterProfiles adapterProfiles;

    /*------------------------------------------ UPDATE DATA --------------------------------------------------*/

    @Override
    public void onStart() {
        super.onStart();

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        adapterProfiles = new AdapterProfiles(new ArrayList<User>(), getContext());
        recyclerView.setAdapter(adapterProfiles);

        controlData();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.admin_tabs_fragment_profiles, container, false);

        /*------------------------------------------ INIT --------------------------------------------------*/

        searchBar = (EditText) root.findViewById(R.id.SearchBar);
        recyclerView = (RecyclerView) root.findViewById(R.id.RecyclerView);
        progressBar = (ProgressBar) root.findViewById(R.id.ProgressBar);

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {Log.i("info", "beforeTextChanged");}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
                adapterProfiles = new AdapterProfiles(new ArrayList<User>(), getContext());
                recyclerView.setAdapter(adapterProfiles);

                usersRef.orderByChild("email").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot snapshots : snapshot.getChildren()) {
                            User user = snapshots.getValue(User.class);
                            if(user.getUsername().toLowerCase().contains(searchBar.getText().toString().trim().toLowerCase()) || user.getEmail().toLowerCase().contains(searchBar.getText().toString().trim().toLowerCase())){
                                adapterProfiles.addItem(user);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.i("info", "onCancelled");
                    }
                });

            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.i("info", "afterTextChanged");
            }

        });

        return root;
    }

    /*------------------------------------------ DATA --------------------------------------------------*/

    private void controlData() {

        usersRef.orderByChild("email").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                User user = snapshot.getValue(User.class);
                adapterProfiles.addItem(user);
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                User user = snapshot.getValue(User.class);
                adapterProfiles.updateItem(user);
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                adapterProfiles.delItem(user);
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.i("info", "onChildMoved:" + snapshot.getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i("info", "onCancelled", error.toException());
            }
        });

    }

    /*------------------------------------------ ADAPTER --------------------------------------------------*/

    class AdapterProfiles extends RecyclerView.Adapter<AdapterProfiles.MyViewHolder> {

        private List<User> users;
        private Context context;

        public AdapterProfiles(List<User> users, Context context) {
            this.users = users;
            this.context = context;
        }

        public void addItem(User u) {
            users.add(u);
            notifyItemInserted(users.size() - 1);
            notifyDataSetChanged();
        }

        public void delItem(User user) {
            User usuario = null;
            for (User u : users) {
                if (u.getId() == user.getId()) {
                    usuario = u;
                }
            }
            users.remove(usuario);
            notifyDataSetChanged();
        }

        public void updateItem(User user) {
            for (User u : users) {
                u.update(user);
            }
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(context).inflate(R.layout.item_view_control_profile, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder.textViewName.setText(users.get(position).getUsername());
            holder.textViewEmail.setText(users.get(position).getEmail());
            holder.tableLayoutItemControlProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    searchBar.setText("");
                    Intent intent = new Intent(context, ControlProfileActivity.class);
                    intent.putExtra("user", users.get(position));
                    context.startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return users.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            TextView textViewEmail;
            TextView textViewName;
            LinearLayout tableLayoutItemControlProfile;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                textViewName = (TextView) itemView.findViewById(R.id.textViewName);
                textViewEmail = (TextView) itemView.findViewById(R.id.textViewEmail);
                tableLayoutItemControlProfile = (TableLayout) itemView.findViewById(R.id.TableLayoutItemControlProfile);
            }
        }
    }
}