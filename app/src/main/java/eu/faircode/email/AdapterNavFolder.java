/*
 * This file is part of FairEmail.
 *     FairEmail is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *     FairEmail is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *     You should have received a copy of the GNU General Public License
 *     along with FairEmail.  If not, see <http://www.gnu.org/licenses/>.
 *     Copyright 2018-2021 by Marcel Bokhorst (M66B)
 */

package eu.faircode.email;

/*

*/

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.graphics.ColorUtils;
import androidx.lifecycle.LifecycleOwner;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListUpdateCallback;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AdapterNavFolder extends RecyclerView.Adapter<AdapterNavFolder.ViewHolder> {
    private Context context;
    private LifecycleOwner owner;
    private LayoutInflater inflater;

    private boolean nav_count;
    private int colorUnread;
    private int textColorSecondary;
    private int colorWarning;

    private boolean expanded = true;
    private List<TupleFolderNav> items = new ArrayList<>();

    private NumberFormat NF = NumberFormat.getNumberInstance();
    private DateFormat TF;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private View view;
        private ImageView ivItem;
        private ImageView ivBadge;
        private TextView tvItem;
        private TextView tvItemExtra;
        private ImageView ivExtra;
        private ImageView ivWarning;

        ViewHolder(View itemView) {
            super(itemView);

            view = itemView.findViewById(R.id.clItem);
            ivItem = itemView.findViewById(R.id.ivItem);
            ivBadge = itemView.findViewById(R.id.ivBadge);
            tvItem = itemView.findViewById(R.id.tvItem);
            tvItemExtra = itemView.findViewById(R.id.tvItemExtra);
            ivExtra = itemView.findViewById(R.id.ivExtra);
            ivWarning = itemView.findViewById(R.id.ivWarning);
        }

        private void wire() {
            view.setOnClickListener(this);
        }

        private void unwire() {
            view.setOnClickListener(null);
        }

        private void bindTo(TupleFolderNav folder) {
            if (EntityFolder.OUTBOX.equals(folder.type)) {
                if ("syncing".equals(folder.sync_state))
                    ivItem.setImageResource(R.drawable.twotone_compare_arrows_24);
                else
                    ivItem.setImageResource(EntityFolder.getIcon(folder.type));

                ivItem.clearColorFilter();
            } else {
                if ("syncing".equals(folder.sync_state))
                    ivItem.setImageResource(R.drawable.twotone_compare_arrows_24);
                else if ("downloading".equals(folder.sync_state))
                    ivItem.setImageResource(R.drawable.twotone_cloud_download_24);
                else if (folder.executing > 0)
                    ivItem.setImageResource(R.drawable.twotone_dns_24);
                else
                    ivItem.setImageResource("connected".equals(folder.state)
                            ? R.drawable.twotone_folder_done_24
                            : R.drawable.twotone_folder_24);

                if (folder.accountColor == null || !ActivityBilling.isPro(context))
                    ivItem.clearColorFilter();
                else
                    ivItem.setColorFilter(folder.accountColor);
            }

            int count;
            if (EntityFolder.DRAFTS.equals(folder.type) ||
                    EntityFolder.OUTBOX.equals(folder.type))
                count = folder.messages;
            else
                count = folder.unseen;

            ivBadge.setVisibility(count == 0 || expanded ? View.GONE : View.VISIBLE);

            if (count == 0)
                tvItem.setText(folder.getDisplayName(context));
            else
                tvItem.setText(context.getString(R.string.title_name_count,
                        folder.getDisplayName(context), NF.format(count)));

            tvItem.setTextColor(count == 0 ? textColorSecondary : colorUnread);
            tvItem.setTypeface(count == 0 ? Typeface.DEFAULT : Typeface.DEFAULT_BOLD);
            tvItem.setVisibility(expanded ? View.VISIBLE : View.GONE);

            tvItemExtra.setText(NF.format(folder.messages));
            tvItemExtra.setVisibility(nav_count && expanded ? View.VISIBLE : View.GONE);

            ivExtra.setVisibility(View.GONE);
            ivWarning.setVisibility(folder.error != null && expanded ? View.VISIBLE : View.GONE);
            view.setBackgroundColor(folder.error != null && !expanded ? colorWarning : Color.TRANSPARENT);
        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            if (pos == RecyclerView.NO_POSITION)
                return;

            TupleFolderNav folder = items.get(pos);
            if (folder == null)
                return;

            LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(context);
            lbm.sendBroadcast(
                    new Intent(ActivityView.ACTION_VIEW_MESSAGES)
                            .putExtra("account", folder.account)
                            .putExtra("folder", folder.id)
                            .putExtra("type", folder.type));
        }
    }

    AdapterNavFolder(Context context, LifecycleOwner owner) {
        this.context = context;
        this.owner = owner;
        this.inflater = LayoutInflater.from(context);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        this.nav_count = prefs.getBoolean("nav_count", false);
        boolean highlight_unread = prefs.getBoolean("highlight_unread", true);
        int colorHighlight = prefs.getInt("highlight_color", Helper.resolveColor(context, R.attr.colorUnreadHighlight));
        this.colorUnread = (highlight_unread ? colorHighlight : Helper.resolveColor(context, R.attr.colorUnread));
        this.textColorSecondary = Helper.resolveColor(context, android.R.attr.textColorSecondary);
        this.colorWarning = ColorUtils.setAlphaComponent(Helper.resolveColor(context, R.attr.colorWarning), 128);

        this.TF = Helper.getTimeInstance(context, SimpleDateFormat.SHORT);

        setHasStableIds(true);
    }

    public void set(@NonNull List<TupleFolderNav> folders, boolean expanded) {
        Log.i("Set nav folders=" + folders.size() + " expanded=" + expanded);

        if (folders.size() > 0)
            Collections.sort(folders, folders.get(0).getComparator(context));

        DiffUtil.DiffResult diff = DiffUtil.calculateDiff(new DiffCallback(items, folders), false);

        this.expanded = expanded;
        this.items = folders;

        diff.dispatchUpdatesTo(new ListUpdateCallback() {
            @Override
            public void onInserted(int position, int count) {
                Log.d("Inserted @" + position + " #" + count);
            }

            @Override
            public void onRemoved(int position, int count) {
                Log.d("Removed @" + position + " #" + count);
            }

            @Override
            public void onMoved(int fromPosition, int toPosition) {
                Log.d("Moved " + fromPosition + ">" + toPosition);
            }

            @Override
            public void onChanged(int position, int count, Object payload) {
                Log.d("Changed @" + position + " #" + count);
            }
        });
        diff.dispatchUpdatesTo(this);
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
        notifyDataSetChanged();
    }

    private static class DiffCallback extends DiffUtil.Callback {
        private List<TupleFolderNav> prev = new ArrayList<>();
        private List<TupleFolderNav> next = new ArrayList<>();

        DiffCallback(List<TupleFolderNav> prev, List<TupleFolderNav> next) {
            this.prev.addAll(prev);
            this.next.addAll(next);
        }

        @Override
        public int getOldListSize() {
            return prev.size();
        }

        @Override
        public int getNewListSize() {
            return next.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            TupleFolderNav f1 = prev.get(oldItemPosition);
            TupleFolderNav f2 = next.get(newItemPosition);
            return f1.id.equals(f2.id);
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            TupleFolderNav f1 = prev.get(oldItemPosition);
            TupleFolderNav f2 = next.get(newItemPosition);
            return f1.equals(f2);
        }
    }

    @Override
    public long getItemId(int position) {
        return items.get(position).id;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.item_nav, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.unwire();
        TupleFolderNav folder = items.get(position);
        holder.bindTo(folder);
        holder.wire();
    }
}
