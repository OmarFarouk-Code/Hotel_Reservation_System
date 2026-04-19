package hotel.model.entities;
import java.io.Serializable;
public class Review implements Serializable
{
    private int score;
    private String comment;

    public Review ( int score, String comment)
    {
        this.score = score;   
        this.comment = comment;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getComment() {
        return comment;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }
    
}
