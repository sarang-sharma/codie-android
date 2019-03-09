package org.codiecon.reportit.models;

public class IssuePoll {

    private int upVotes;

    private int downVotes;

    public IssuePoll(int upVotes, int downVotes) {
        this.upVotes = upVotes;
        this.downVotes = downVotes;
    }

    public int getUpVotes() {
        return upVotes;
    }

    public void setUpVotes(int upVotes) {
        this.upVotes = upVotes;
    }

    public int getDownVotes() {
        return downVotes;
    }

    public void setDownVotes(int downVotes) {
        this.downVotes = downVotes;
    }
}
