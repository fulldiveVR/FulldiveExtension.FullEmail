package eu.faircode.email;

/*

*/

import java.util.Objects;

import static eu.faircode.email.ServiceAuthenticator.AUTH_TYPE_PASSWORD;

public class TupleAccountState extends EntityAccount {
    // TODO: folder property changes (name, poll)
    public int folders;
    public int operations;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TupleAccountState) {
            TupleAccountState other = (TupleAccountState) obj;
            return (this.host.equals(other.host) &&
                    this.encryption.equals(other.encryption) &&
                    this.insecure.equals(other.insecure) &&
                    this.port.equals(other.port) &&
                    this.user.equals(other.user) &&
                    (auth_type != AUTH_TYPE_PASSWORD || this.password.equals(other.password)) &&
                    Objects.equals(this.certificate_alias, other.certificate_alias) &&
                    Objects.equals(this.realm, other.realm) &&
                    Objects.equals(this.fingerprint, other.fingerprint) &&
                    this.notify.equals(other.notify) &&
                    this.leave_on_server == other.leave_on_server &&
                    this.leave_on_device == other.leave_on_device &&
                    Objects.equals(this.max_messages, other.max_messages) &&
                    this.poll_interval.equals(other.poll_interval) &&
                    this.partial_fetch.equals(other.partial_fetch) &&
                    this.ignore_size.equals(other.ignore_size) &&
                    this.use_date.equals(other.use_date) &&
                    this.use_received.equals(other.use_received) &&
                    this.folders == other.folders &&
                    Objects.equals(this.tbd, other.tbd));
        } else
            return false;
    }

    boolean isEnabled(boolean enabled) {
        return (enabled && synchronize && !ondemand && folders > 0 && tbd == null);
    }

    boolean shouldRun(boolean enabled) {
        return (isEnabled(enabled) || (operations > 0 && synchronize && tbd == null));
    }
}
