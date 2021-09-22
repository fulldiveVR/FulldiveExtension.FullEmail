package eu.faircode.email;

/*

*/

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemKeyProvider;
import androidx.recyclerview.widget.RecyclerView;

public class ItemKeyProviderMessage extends ItemKeyProvider<Long> {
    private RecyclerView recyclerView;

    ItemKeyProviderMessage(RecyclerView recyclerView) {
        super(ItemKeyProvider.SCOPE_CACHED);
        this.recyclerView = recyclerView;
    }

    @Nullable
    @Override
    public Long getKey(int pos) {
        AdapterMessage adapter = (AdapterMessage) recyclerView.getAdapter();
        return adapter.getKeyAtPosition(pos);
    }

    @Override
    public int getPosition(@NonNull Long key) {
        AdapterMessage adapter = (AdapterMessage) recyclerView.getAdapter();
        return adapter.getPositionForKey(key);
    }
}
