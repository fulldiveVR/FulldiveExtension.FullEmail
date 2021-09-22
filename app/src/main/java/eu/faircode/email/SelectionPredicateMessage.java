package eu.faircode.email;

/*
   
*/

import androidx.annotation.NonNull;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.widget.RecyclerView;

public class SelectionPredicateMessage extends SelectionTracker.SelectionPredicate<Long> {
    private boolean enabled;
    private RecyclerView recyclerView;

    SelectionPredicateMessage(RecyclerView recyclerView) {
        this.enabled = true;
        this.recyclerView = recyclerView;
    }

    void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean canSetStateForKey(@NonNull Long key, boolean nextState) {
        if (!enabled)
            return false;

        AdapterMessage adapter = (AdapterMessage) recyclerView.getAdapter();
        TupleMessageEx message = adapter.getItemForKey(key);
        if (message == null) // happens when restoring state
            return true;

        if (message.accountProtocol != EntityAccount.TYPE_IMAP)
            return true;

        if (message.uid != null && !message.folderReadOnly)
            return true;

        return false;
    }

    @Override
    public boolean canSetStateAtPosition(int position, boolean nextState) {
        if (!enabled)
            return false;

        AdapterMessage adapter = (AdapterMessage) recyclerView.getAdapter();
        TupleMessageEx message = adapter.getItemAtPosition(position);
        if (message == null) // happens when restoring state
            return true;

        if (message.accountProtocol != EntityAccount.TYPE_IMAP)
            return true;

        if (message.uid != null && !message.folderReadOnly)
            return true;

        return false;
    }

    @Override
    public boolean canSelectMultiple() {
        return true;
    }
}