package pojo;

import java.sql.Date;

/**
 * Created by michele on 15/09/17.
 */

public class PartyRow {


    private String id;
    private String name;
    private String orgName;
    private Date date;
    private int tickets;

    public PartyRow(String id, String name, String orgName, Date date, int tickets) {
        super();
        this.id=id;
        this.name=name;
        this.orgName=orgName;
        this.date=date;
        this.tickets=tickets;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id=id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name=name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date=date;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName=orgName;
    }

    public int getTickets() {
        return this.tickets;
    }

    public void setTickets(int tickets) {
        this.tickets=tickets;
    }

}
