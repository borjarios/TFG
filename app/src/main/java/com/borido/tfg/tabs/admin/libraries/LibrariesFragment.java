package com.borido.tfg.tabs.admin.libraries;

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

public class LibrariesFragment extends Fragment {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference librariesRef = database.getReference("libraries");

    private RecyclerView recyclerView;
    private FloatingActionButton faButton;
    private EditText searchBar;
    private ProgressBar progressBar;

    private AdapterLibraries adapterLibraries;

    /*------------------------------------------ UPDATE DATA --------------------------------------------------*/

    @Override
    public void onResume() {
        super.onResume();

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        adapterLibraries = new AdapterLibraries(new ArrayList<Library>(), getContext());
        recyclerView.setAdapter(adapterLibraries);

        controlData();

    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.admin_tabs_fragment_libraries, container, false);

        /*------------------------------------------ INIT --------------------------------------------------*/

        searchBar = (EditText) root.findViewById(R.id.SearchBar);
        faButton = (FloatingActionButton) root.findViewById(R.id.FabButton);
        recyclerView = (RecyclerView) root.findViewById(R.id.RecyclerView);
        progressBar = (ProgressBar) root.findViewById(R.id.ProgressBar);

        faButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddLibraryActivity.class);
                startActivity(intent);
            }
        });

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
                adapterLibraries = new AdapterLibraries(new ArrayList<Library>(), getContext());
                recyclerView.setAdapter(adapterLibraries);

                librariesRef.orderByChild("name").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Library library = snapshot.getValue(Library.class);
                            if(library.getName().toLowerCase().contains(searchBar.getText().toString().trim().toLowerCase()) || library.getDirection().toLowerCase().contains(searchBar.getText().toString().trim().toLowerCase()) || library.getLocation().toLowerCase().contains(searchBar.getText().toString().trim().toLowerCase()) || library.getTel().toLowerCase().contains(searchBar.getText().toString().trim().toLowerCase())){
                                adapterLibraries.addItem(library);
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

        librariesRef.orderByChild("name").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Library library = snapshot.getValue(Library.class);
                adapterLibraries.addItem(library);
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Library library = snapshot.getValue(Library.class);
                adapterLibraries.updateItem(library);
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Library library = snapshot.getValue(Library.class);
                adapterLibraries.delItem(library);
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

    class AdapterLibraries extends RecyclerView.Adapter<AdapterLibraries.MyViewHolder> {

        private List<Library> libraries;
        private Context context;

        public AdapterLibraries(List<Library> libraries, Context context) {
            this.libraries = libraries;
            this.context = context;
        }

        public void addItem(Library l) {
            libraries.add(l);
            notifyItemInserted(libraries.size() - 1);
            notifyDataSetChanged();
        }

        public void delItem(Library library) {
            Library libreria = null;
            for (Library l : libraries) {
                if (l.getId() == library.getId()) {
                    libreria = l;
                }
            }
            libraries.remove(libreria);
            notifyDataSetChanged();
        }

        public void updateItem(Library library) {
            for (Library l : libraries) {
                l.update(library);
            }
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(context).inflate(R.layout.item_view_control_libraries, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder.textViewName.setText(libraries.get(position).getName());
            holder.tableLayoutItemControlLibrary.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    searchBar.setText("");
                    Intent intent = new Intent(context, ControlLibraryActivity.class);
                    intent.putExtra("library", libraries.get(position));
                    context.startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return libraries.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            TextView textViewName;
            LinearLayout tableLayoutItemControlLibrary;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                textViewName = (TextView) itemView.findViewById(R.id.textViewName);
                tableLayoutItemControlLibrary = (TableLayout) itemView.findViewById(R.id.TableLayoutItemControlLibrary);
            }
        }
    }
}