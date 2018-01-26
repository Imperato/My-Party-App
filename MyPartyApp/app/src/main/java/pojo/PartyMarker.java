package pojo;

/**
 * Created by michele on 20/09/17.
 */

public class PartyMarker {

    private String id;
    private String partyName;
    private String organizerName;
    private String date;
    private double latitude;
    private double longitude;

    public PartyMarker(String id, String partyName, String organizerName, String date, double latitude, double longitude) {
        super();
        this.id = id;
        this.partyName = partyName;
        this.organizerName = organizerName;
        this.date = date;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id=id;
    }

    public String getPartyName() {
        return partyName;
    }

    public void setPartyName(String partyName) {
        this.partyName=partyName;
    }

    public String getOrganizerName() {
        return organizerName;
    }

    public void setOrganizerName(String organizerName) {
        this.organizerName=organizerName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date=date;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) { this.latitude=latitude;}

    public double getLongitude() { return longitude;}

    public void setLongitude(double longitude) {
        this.longitude=longitude;
    }
}
