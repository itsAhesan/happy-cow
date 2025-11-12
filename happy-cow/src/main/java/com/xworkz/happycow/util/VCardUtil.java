package com.xworkz.happycow.util;



import com.xworkz.happycow.entity.AgentEntity;
import java.util.StringJoiner;

public class VCardUtil {

    /**
     * Build a vCard (VERSION:3.0) for the agent.
     * This is a plain text builder that handles simple address and newline replacement.
     */
    public static String buildVCard(AgentEntity agent) {
        if (agent == null) return "";

        StringJoiner sj = new StringJoiner("\n");
        sj.add("BEGIN:VCARD");
        sj.add("VERSION:3.0");

        // N:Lastname;Firstname;AdditionalNames;HonorificPrefixes;HonorificSuffixes
        String last = safe(agent.getLastName());
        String first = safe(agent.getFirstName());
        sj.add("N:" + last + ";" + first + ";;;");
        sj.add("FN:" + (first + " " + last).trim());

        // Organization
        sj.add("ORG:HappyCow Dairy");

        // Telephone
        if (agent.getPhoneNumber() != null && !agent.getPhoneNumber().trim().isEmpty()) {
            sj.add("TEL;TYPE=CELL:" + safe(agent.getPhoneNumber()));
        }

        // Email
        if (agent.getEmail() != null && !agent.getEmail().trim().isEmpty()) {
            sj.add("EMAIL;TYPE=INTERNET:" + safe(agent.getEmail()));
        }

        // Address: vCard ADR format: ADR;TYPE=WORK:post-office-box;extended-address;street;locality;region;postal-code;country
        if (agent.getAddress() != null && !agent.getAddress().trim().isEmpty()) {
            // We will attempt to split address by newline or comma and map values to street/locality/region
            String addr = agent.getAddress().replace("\r", " ").replace("\n", " ");
            // Keep it simple: put whole address into street field (third item)
            String adr = ";;" + escapeForVCard(addr) + ";;;;";
            sj.add("ADR;TYPE=WORK:" + adr);
        }

        // Optional: job title, note, etc.
        // sj.add("TITLE:Agent");

        sj.add("END:VCARD");
        return sj.toString();
    }

    private static String safe(String s) {
        return s == null ? "" : s.trim();
    }

    // vCard needs special chars escaped: comma and semicolon and newline -> backslash-escaped
    private static String escapeForVCard(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\n", "\\n")
                .replace("\r", "\\n")
                .replace(";", "\\;")
                .replace(",", "\\,");
    }
}

