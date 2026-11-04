package com.example.secureprogrammanager;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.secureprogrammanager.R;
import com.example.secureprogrammanager.VaultEntry;
import com.example.secureprogrammanager.CryptoManager;
import java.util.List;

/**
 RECYCLER VIEWS:
 a UI type that is helpful for conserving memory, especially when the total numbeer of rows is a large number
 but only a small amount of rows will actually be displayed (fit on screen) at once. The Recycler uses view(s) scrolled
 off of the page as the view to recycle (put the new data/row newly revealed on screen after the scroll into). The recycler view
 updates the index/position of the new row of data to bind the recycled view to by using knowledge of the amount of views
 on screen at once and the total number number of rows of data (from itemCount() which is called every update). It detarmines
 that the new position of the newly revealed row is equal to the previous last position + 1. That index along with the recycled
 view object are passed into the onBindViewHolder method everytime the RecyclerView needs to bind new data (extracted from the data object at that
 new index) to a recycled view.

 OnCreateViewHolder is called whenever the RecyclerView needs a new viewHolder object entirely (cannot just recycle a previous
 one). This usually happens in the beginning where there are few rows already made so that when you add a new entry it does not fill the screen
 and thus you do not scroll.

 SetEntries is called with every insertion or deletion of the dataset in the main activity. Doing so calls the
 VaultAdapter.setEntries() which calls notifyDataSetChange(). This is another built in method. What this method does
 is force the recycleView to "reload" --> it recounts the entirety of the number of total rows (calls itemCount())
 and re-evaluates (based on the LayoutManager) what views to bind to what indexes and if it needs to create new views
 */

// declares Adapter class as a subclass of Recycler View. All views will be objects of VaultAdapter.ViewHolder
public class VaultAdapter extends RecyclerView.Adapter<VaultAdapter.ViewHolder> {

    private List<VaultEntry> entries; // all of the vault entry passwords in the database
    private final CryptoManager crypto; // object that will be used for decrypting the passwords in the entries

    private final OnDeleteClickListener deleteListener; // when its onDeleteClick method is called, it runs the lamba expression it was set to

    public interface OnDeleteClickListener { // functional interface whose implementation becomes the lambda passed in
        void onDeleteClick(VaultEntry entry);
    }
    public VaultAdapter(List<VaultEntry> entries, CryptoManager crypto, OnDeleteClickListener deleteListener) {
        this.entries = entries;
        this.crypto = crypto;
        this.deleteListener = deleteListener;
    }

    public void setEntries(List<VaultEntry> newEntries) {
        this.entries = newEntries; // make the list of entries reflect the current state
        notifyDataSetChanged(); // refresh RecyclerView
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_vault_entry, parent, false);
        return new ViewHolder(view); // creates new ViewHolder object/view if necessary (called by RecyclerView)
    }

    @Override
    // binds an existing view (ViewHolder) with a new positon
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VaultEntry entry = entries.get(position); // retrieves the entry that corresponds to that position
        // and populates the holder view with that entry's data (as you can see, RecyclerView only really works if
        // you store data as parts of lists that where that data can be received just by knowing its index)
        holder.service.setText(entry.service);
        holder.username.setText(entry.username);

        holder.copy.setOnClickListener(v -> {
            try {
                byte[] decrypted = crypto.decrypt(entry.encryptedPassword, entry.iv); // calls the method to decrypt
                ClipboardManager clipboard = (ClipboardManager) // makes ClipboardManager object
                        v.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                // adds the decrypted password (converted from byte[] to string) to the clipboard as the primary
                clipboard.setPrimaryClip(ClipData.newPlainText("password", new String(decrypted)));
                Toast.makeText(v.getContext(), "Password copied!", Toast.LENGTH_SHORT).show(); //alerts user
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(v.getContext(), "Failed to copy password. You may have been timed out", Toast.LENGTH_SHORT).show();
                SessionManager.getInstance().lock();

            }
        });

        // Set Delete Click Listener
        holder.delete.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDeleteClick(entry);
            }
        });

    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView service, username;
        Button copy, delete;

        ViewHolder(View itemView) { // creates a view with username, service, and copy button (view that the rows are based on)
            super(itemView);
            service = itemView.findViewById(R.id.serviceText);
            username = itemView.findViewById(R.id.usernameText);
            copy = itemView.findViewById(R.id.copyPasswordButton);
            delete = itemView.findViewById(R.id.deleteEntryButton);
        }
    }
}

