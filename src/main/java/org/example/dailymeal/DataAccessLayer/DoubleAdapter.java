package org.example.dailymeal.DataAccessLayer;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

public class DoubleAdapter extends XmlAdapter<String, Double> {
    @Override
    public Double unmarshal(String v) throws Exception {
        if (v == null || v.trim().isEmpty()) return 0.0;
        String normalized = v.replace(',', '.');
        return Double.parseDouble(normalized);
    }

    @Override
    public String marshal(Double v) throws Exception {
        if (v == null) return "0";
        return v.toString().replace('.', ',');
    }
}