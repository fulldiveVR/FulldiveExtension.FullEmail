package eu.faircode.email;

/*
   
*/

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TupleAccountNetworkState {
    public boolean enabled;
    @NonNull
    public Bundle command;
    @NonNull
    public ConnectionHelper.NetworkState networkState;
    @NonNull
    public TupleAccountState accountState;

    public TupleAccountNetworkState(
            boolean enabled,
            @NonNull Bundle command,
            @NonNull ConnectionHelper.NetworkState networkState,
            @NonNull TupleAccountState accountState) {
        this.enabled = enabled;
        this.command = command;
        this.networkState = networkState;
        this.accountState = accountState;
    }

    public boolean canRun() {
        return (this.networkState.isSuitable() && this.accountState.shouldRun(enabled));
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof TupleAccountNetworkState) {
            TupleAccountNetworkState other = (TupleAccountNetworkState) obj;
            return this.accountState.id.equals(other.accountState.id);
        } else
            return false;
    }

    @Override
    public int hashCode() {
        return accountState.id.hashCode();
    }

    @NonNull
    @Override
    public String toString() {
        return accountState.name;
    }
}

