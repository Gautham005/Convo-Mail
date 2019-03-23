package com.example.convomail;

import java.io.Serializable;

public class Inbox implements Serializable {
    public Mail primary;
    public Mail draft;
    public Mail spam;
    public Mail trash;

    public Inbox(Mail p, Mail d, Mail s, Mail t){
        this.primary = p;
        this.draft = d;
        this.spam = s;
        this.trash = t;
    }
    public void setPrimary(Mail primary) {
        this.primary = primary;
    }
    public void setDraft(Mail draft){
        this.draft = draft;
    }

    public void setSpam(Mail spam) {
        this.spam = spam;
    }

    public void setTrash(Mail trash) {
        this.trash = trash;
    }

    public Mail getDraft() {
        return draft;
    }

    public Mail getPrimary() {
        return primary;
    }

    public Mail getSpam() {
        return spam;
    }

    public Mail getTrash() {
        return trash;
    }
    public void setInbox(Mail p, Mail d, Mail s, Mail t){
        this.primary = p;
        this.draft = d;
        this.spam = s;
        this.trash = t;
    }
    public void updateInbox(Inbox inbox){
        this.primary.getMessages().addAll(inbox.getPrimary().getMessages());
        this.draft.getMessages().addAll(inbox.getDraft().getMessages());
        this.spam.getMessages().addAll(inbox.getSpam().getMessages());
        this.trash.getMessages().addAll(inbox.getTrash().getMessages());
    }

}
