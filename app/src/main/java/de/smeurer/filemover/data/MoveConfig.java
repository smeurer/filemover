package de.smeurer.filemover.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import org.joda.time.DateTime;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Created by Simon Meurer on 16.04.18.
 */
public class MoveConfig implements Parcelable {

    public static final Parcelable.Creator<MoveConfig> CREATOR
            = new Parcelable.Creator<MoveConfig>() {
        @Override
        public MoveConfig createFromParcel(Parcel source) {
            return new MoveConfig(source);
        }

        @Override
        public MoveConfig[] newArray(int size) {
            return new MoveConfig[size];
        }
    };
    private UUID id;
    private String fromPath;
    private String toPath;
    private List<String> extensions;
    @NonNull
    private Mode mode;
    private DateTime createdAt;
    private DateTime updatedAt;
    private DateTime lastRunAt;

    public MoveConfig() {
        this.id = UUID.randomUUID();
        this.createdAt = DateTime.now();
        updateUpdatedAt();
    }

    protected MoveConfig(Parcel in) {
        this.id = (UUID) in.readSerializable();
        this.fromPath = in.readString();
        this.toPath = in.readString();
        this.extensions = in.createStringArrayList();
        int tmpMode = in.readInt();
        this.mode = tmpMode == -1 ? null : Mode.values()[tmpMode];
        this.createdAt = (DateTime) in.readSerializable();
        this.updatedAt = (DateTime) in.readSerializable();
    }

    private void updateUpdatedAt() {
        this.updatedAt = DateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public DateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MoveConfig that = (MoveConfig) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public String getFromPath() {
        return fromPath;
    }

    public void setFromPath(String fromPath) {
        this.fromPath = fromPath;
        updateUpdatedAt();
    }

    public String getToPath() {
        return toPath;
    }

    public void setToPath(String toPath) {
        this.toPath = toPath;
        updateUpdatedAt();
    }

    public List<String> getExtensions() {
        return extensions;
    }

    public void setExtensions(List<String> extensions) {
        this.extensions = extensions;
        updateUpdatedAt();
    }

    public String getExtensionsString() {
        return TextUtils.join(", ", getExtensions());
    }

    @NonNull
    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
        updateUpdatedAt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this.id);
        dest.writeString(this.fromPath);
        dest.writeString(this.toPath);
        dest.writeStringList(this.extensions);
        dest.writeInt(this.mode == null ? -1 : this.mode.ordinal());
        dest.writeSerializable(this.createdAt);
        dest.writeSerializable(this.updatedAt);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("MoveConfig{");
        sb.append("id=").append(id);
        sb.append(", fromPath='").append(fromPath).append('\'');
        sb.append(", toPath='").append(toPath).append('\'');
        sb.append(", extensions=").append(extensions);
        sb.append(", mode=").append(mode);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append('}');
        return sb.toString();
    }

    public DateTime getLastRunAt() {
        return lastRunAt;
    }

    public void setLastRunAt(DateTime lastRunAt) {
        this.lastRunAt = lastRunAt;
    }
}
