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
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Objects;

public class AdapterNavAccount extends RecyclerView.Adapter<AdapterNavAccount.ViewHolder> {
    private Context context;
    private LifecycleOwner owner;
    private LayoutInflater inflater;

    private int colorUnread;
    private int textColorSecondary;
    private int colorWarning;

    private boolean expanded = true;
    private List<TupleAccountEx> items = new ArrayList<>();

    private NumberFormat NF = NumberFormat.getNumberInstance();
    private DateFormat TF;

    private static final int QUOTA_WARNING = 95; // percent

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
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
            view.setOnLongClickListener(this);
            ivWarning.setOnClickListener(this);
        }

        private void unwire() {
            view.setOnClickListener(null);
            view.setOnLongClickListener(null);
            ivWarning.setOnClickListener(null);
        }

        private void bindTo(TupleAccountEx account) {
            if ("connected".equals(account.state))
                ivItem.setImageResource(account.primary
                        ? R.drawable.twotone_folder_special_24
                        : R.drawable.twotone_folder_done_24);
            else
                ivItem.setImageResource(account.backoff_until == null
                        ? R.drawable.twotone_folder_24
                        : R.drawable.twotone_update_24);

            if (account.color == null)
                ivItem.clearColorFilter();
            else
                ivItem.setColorFilter(account.color);
            ivBadge.setVisibility(account.unseen == 0 || expanded ? View.GONE : View.VISIBLE);

            if (account.unseen == 0)
                tvItem.setText(account.name);
            else
                tvItem.setText(context.getString(R.string.title_name_count,
                        account.name, NF.format(account.unseen)));

            tvItem.setTextColor(account.unseen == 0 ? textColorSecondary : colorUnread);
            tvItem.setTypeface(account.unseen == 0 ? Typeface.DEFAULT : Typeface.DEFAULT_BOLD);
            tvItem.setVisibility(expanded ? View.VISIBLE : View.GONE);

            tvItemExtra.setText(account.last_connected == null ? null : TF.format(account.last_connected));
            tvItemExtra.setVisibility(account.last_connected != null && expanded ? View.VISIBLE : View.GONE);

            ivExtra.setVisibility(View.GONE);

            Integer percent = account.getQuotaPercentage();

            if (account.error != null) {
                ivWarning.setImageResource(R.drawable.twotone_warning_24);
                ivWarning.setVisibility(expanded ? View.VISIBLE : View.GONE);
                view.setBackgroundColor(expanded ? Color.TRANSPARENT : colorWarning);
            } else if (percent != null && percent > QUOTA_WARNING) {
                ivWarning.setImageResource(R.drawable.twotone_disc_full_24);
                ivWarning.setVisibility(expanded ? View.VISIBLE : View.GONE);
                view.setBackgroundColor(expanded ? Color.TRANSPARENT : colorWarning);
            } else {
                ivWarning.setVisibility(View.GONE);
                view.setBackgroundColor(Color.TRANSPARENT);
            }
        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            if (pos == RecyclerView.NO_POSITION)
                return;

            TupleAccountEx account = items.get(pos);
            if (account == null)
                return;

            if (v.getId() == R.id.ivWarning && account.error == null) {
                ToastEx.makeText(context, R.string.title_legend_quota, Toast.LENGTH_LONG).show();
                return;
            }

            LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(context);
            lbm.sendBroadcast(
                    new Intent(ActivityView.ACTION_VIEW_FOLDERS)
                            .putExtra("id", account.id));
        }

        @Override
        public boolean onLongClick(View v) {
            int pos = getAdapterPosition();
            if (pos == RecyclerView.NO_POSITION)
                return false;

            TupleAccountEx account = items.get(pos);
            if (account == null)
                return false;

            Bundle args = new Bundle();
            args.putLong("id", account.id);

            new SimpleTask<EntityFolder>() {
                @Override
                protected EntityFolder onExecute(Context context, Bundle args) {
                    long id = args.getLong("id");

                    DB db = DB.getInstance(context);
                    return db.folder().getFolderByType(id, EntityFolder.INBOX);
                }

                @Override
                protected void onExecuted(Bundle args, EntityFolder inbox) {
                    if (inbox == null)
                        return;

                    LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(context);
                    lbm.sendBroadcast(
                            new Intent(ActivityView.ACTION_VIEW_MESSAGES)
                                    .putExtra("account", inbox.account)
                                    .putExtra("folder", inbox.id)
                                    .putExtra("type", inbox.type));
                }

                @Override
                protected void onException(Bundle args, Throwable ex) {
                    // Ignored
                }
            }.execute(context, owner, args, "account:inbox");

            return true;
        }
    }

    AdapterNavAccount(Context context, LifecycleOwner owner) {
        this.context = context;
        this.owner = owner;
        this.inflater = LayoutInflater.from(context);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean highlight_unread = prefs.getBoolean("highlight_unread", true);
        int colorHighlight = prefs.getInt("highlight_color", Helper.resolveColor(context, R.attr.colorUnreadHighlight));
        this.colorUnread = (highlight_unread ? colorHighlight : Helper.resolveColor(context, R.attr.colorUnread));
        this.textColorSecondary = Helper.resolveColor(context, android.R.attr.textColorSecondary);
        this.colorWarning = ColorUtils.setAlphaComponent(Helper.resolveColor(context, R.attr.colorWarning), 128);

        this.TF = Helper.getTimeInstance(context, SimpleDateFormat.SHORT);

        setHasStableIds(true);
    }

    public void set(@NonNull List<TupleAccountEx> accounts, boolean expanded) {
        Log.i("Set nav accounts=" + accounts.size());

        if (accounts.size() > 0)
            Collections.sort(accounts, accounts.get(0).getComparator(context));

        DiffUtil.DiffResult diff = DiffUtil.calculateDiff(new DiffCallback(items, accounts), false);

        this.expanded = expanded;
        this.items = accounts;

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
        private List<TupleAccountEx> prev = new ArrayList<>();
        private List<TupleAccountEx> next = new ArrayList<>();

        DiffCallback(List<TupleAccountEx> prev, List<TupleAccountEx> next) {
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
            TupleAccountEx a1 = prev.get(oldItemPosition);
            TupleAccountEx a2 = next.get(newItemPosition);
            return a1.id.equals(a2.id);
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            TupleAccountEx a1 = prev.get(oldItemPosition);
            TupleAccountEx a2 = next.get(newItemPosition);
            return Objects.equals(a1.name, a2.name) &&
                    Objects.equals(a1.color, a2.color) &&
                    a1.primary == a2.primary &&
                    a1.unseen == a2.unseen &&
                    Objects.equals(a1.state, a2.state) &&
                    Objects.equals(a1.last_connected, a2.last_connected) &&
                    Objects.equals(a1.error, a2.error);
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
        TupleAccountEx account = items.get(position);
        holder.bindTo(account);
        holder.wire();
    }
}
