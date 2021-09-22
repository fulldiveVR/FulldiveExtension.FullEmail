package eu.faircode.email;

/*

*/

import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemDetailsLookup;

public class ItemDetailsMessage extends ItemDetailsLookup.ItemDetails<Long> {
    private AdapterMessage.ViewHolder viewHolder;

    ItemDetailsMessage(AdapterMessage.ViewHolder viewHolder) {
        this.viewHolder = viewHolder;
    }

    @Override
    public int getPosition() {
        int pos = viewHolder.getAdapterPosition();
        Log.d("ItemDetails pos=" + pos);
        return pos;
    }

    @Nullable
    @Override
    public Long getSelectionKey() {
        int pos = viewHolder.getAdapterPosition();
        Long key = viewHolder.getKey();
        Log.d("ItemDetails pos=" + pos + " key=" + key);
        return key;
    }
}
