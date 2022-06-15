package app.client.munchbear.munchbearclient.model.restaurant;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * @author Roman H.
 */

public class WorkingHours implements Parcelable {

    @SerializedName("to")
    private String to;

    @SerializedName("from")
    private String from;

    @SerializedName("is_dirty")
    private boolean isDirty;

    @SerializedName("is_all_day")
    private boolean isAllDay;

    @SerializedName("is_day_off")
    private boolean isDayOff;

    @SerializedName("is_open")
    private boolean isOpen;

    @SerializedName("date")
    private String date;

    public WorkingHours() {

    }

    public WorkingHours(String to, String from, boolean isDirty, boolean isAllDay, boolean isDayOff, boolean isOpen, String date) {
        this.to = to;
        this.from = from;
        this.isDirty = isDirty;
        this.isAllDay = isAllDay;
        this.isDayOff = isDayOff;
        this.isOpen = isOpen;
        this.date = date;
    }

    protected WorkingHours(Parcel in) {
        to = in.readString();
        from = in.readString();
        isDirty = in.readByte() != 0;
        isAllDay = in.readByte() != 0;
        isDayOff = in.readByte() != 0;
        isOpen = in.readByte() != 0;
        date = in.readString();
    }

    public static final Creator<WorkingHours> CREATOR = new Creator<WorkingHours>() {
        @Override
        public WorkingHours createFromParcel(Parcel in) {
            return new WorkingHours(in);
        }

        @Override
        public WorkingHours[] newArray(int size) {
            return new WorkingHours[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(to);
        dest.writeString(from);
        dest.writeByte((byte) (isDirty ? 1 : 0));
        dest.writeByte((byte) (isAllDay ? 1 : 0));
        dest.writeByte((byte) (isDayOff ? 1 : 0));
        dest.writeByte((byte) (isOpen ? 1 : 0));
        dest.writeString(date);
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public boolean isDirty() {
        return isDirty;
    }

    public void setDirty(boolean dirty) {
        isDirty = dirty;
    }

    public boolean isAllDay() {
        return isAllDay;
    }

    public void setAllDay(boolean allDay) {
        isAllDay = allDay;
    }

    public boolean isDayOff() {
        return isDayOff;
    }

    public void setDayOff(boolean dayOff) {
        isDayOff = dayOff;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
