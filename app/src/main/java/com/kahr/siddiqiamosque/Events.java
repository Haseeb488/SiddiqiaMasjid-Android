package com.kahr.siddiqiamosque;

class Events {
    private String eventName, description, eventDate, eventTime;


    public Events(String eventName, String description, String eventDate) {
        this.eventName = eventName;
        this.description = description;
        this.eventDate = eventDate;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {

        this.eventName = eventName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {

        this.description = description;
    }

    public String getEventDate() {

        return eventDate;
    }

    public void setEventDate(String eventDate) {

        this.eventDate = eventDate;
    }



    public void setEventTime() {

        this.eventTime = eventTime;
    }

    public String getEventTime()
    {

        return eventTime;
    }

}
