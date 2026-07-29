package com.musicos.domain;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "instruments")
public class Instrument {
    @Id
    @Enumerated(EnumType.STRING)
    private InstrumentId id;
    private String name;
    private String shortName;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> focus = new ArrayList<>();

    protected Instrument() {
    }

    public Instrument(InstrumentId id, String name, String shortName, List<String> focus) {
        this.id = id;
        this.name = name;
        this.shortName = shortName;
        this.focus = new ArrayList<>(focus);
    }

    public InstrumentId getId() { return id; }
    public String getName() { return name; }
    public String getShortName() { return shortName; }
    public List<String> getFocus() { return List.copyOf(focus); }
}
